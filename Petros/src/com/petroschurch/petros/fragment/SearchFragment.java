package com.petroschurch.petros.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.petroschurch.petros.MainActivity;
import com.petroschurch.petros.R;
import com.petroschurch.petros.lib.CommonPara;
import com.petroschurch.petros.lib.Database;
import com.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.HashMap;

public class SearchFragment extends SherlockFragment implements OnClickListener, OnItemClickListener {
    private Button button_search;
    private ListView list_search;
    private EditText text_search;
    private TextView text_search_count;

    private Handler handler = null;
    private MainActivity mActivity = null;
    private ActionBar mActionBar = null;
    private FragmentManager mManager = null;
    private Message msg = null;

    public static final int LIST_SEARCH_SET = 0;

    //int position = 0;
    int count = 0;

    @SuppressLint("HandlerLeak")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = (MainActivity) getSherlockActivity();
        mActionBar = mActivity.getSupportActionBar();
        mManager = mActivity.getSupportFragmentManager();
    }

    @SuppressLint("HandlerLeak")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_search, container, false);

        button_search = (Button) view.findViewById(R.id.button_frag_search_click);
        list_search = (ListView) view.findViewById(R.id.list_frag_search_content);
        text_search = (EditText) view.findViewById(R.id.text_frag_search_word);
        text_search_count = (TextView) view.findViewById(R.id.text_frag_search_count);

        button_search.setOnClickListener(this);
        list_search.setOnItemClickListener(this);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case LIST_SEARCH_SET:
                        SimpleAdapter adapter = (SimpleAdapter) (msg.obj);
                        list_search.setAdapter(adapter);
                        list_search.setSelection(CommonPara.searchPos);
                        text_search_count.setText("共搜索到" + count + "条经文");

                        InputMethodManager inputMethodManager = (InputMethodManager) mActivity.getSystemService
                                (Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(mActivity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                        super.handleMessage(msg);
                        break;
                }
            }
        };
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //position = CommonPara.searchPos;
        SharedPreferences settings = mActivity.getSharedPreferences(CommonPara.STORE_NAME, Context.MODE_PRIVATE);
        text_search.setText(settings.getString("search_key", ""));
        text_search.setSelectAllOnFocus(true);

        SearchVerse();
    }

    @Override
    public void onPause() {
        super.onPause();
        CommonPara.searchPos = list_search.getFirstVisiblePosition();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_frag_search_click:
                CommonPara.searchPos = 0;
                SearchVerse();
                break;

        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        ListView listView = (ListView) arg0;
        HashMap<String, Object> map = (HashMap<String, Object>) listView.getItemAtPosition(arg2);

        FragmentTransaction fragTrans = null;
        CommonPara.currentBook = Integer.parseInt(map.get("book").toString());
        CommonPara.currentChapter = Integer.parseInt(map.get("chapter").toString());
        CommonPara.currentSection = Integer.parseInt(map.get("section").toString());
        CommonPara.bibleDevitionPos = 0;

        CommonPara.menuIndex = CommonPara.MENU_BIBLE;
        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        fragTrans = mManager.beginTransaction();
        fragTrans.replace(R.id.frag_content_frame, new BibleFragment());
        fragTrans.commit();
    }

    private void SearchVerse() {
        list_search.setAdapter(null);
        text_search_count.setText("正在搜索，请稍候");

        new Thread() {
            public void run() {
                try {
                    SimpleAdapter adapter = new SimpleAdapter(mActivity,
                            GetData(),
                            R.layout.list_parallel_text,
                            new String[]{"title", "verse"},
                            new int[]{R.id.para_title, R.id.para_content});

                    msg = new Message();
                    msg.what = LIST_SEARCH_SET;
                    msg.obj = adapter;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                }

            }
        }.start();
    }

    private ArrayList<HashMap<String, Object>> GetData() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        ArrayList<HashMap<String, Object>> data = null;

        try {
            data = new ArrayList<HashMap<String, Object>>();
            db = new Database(mActivity)
                    .DbConnection(CommonPara.DB_CONTENT_PATH
                            + CommonPara.DB_CONTENT_NAME);
            String version = "";
            for (int i = 0; i < CommonPara.VERSION_NUMBER; i++) {
                if (CommonPara.bibleVersion[i] == true) {
                    version += CommonPara.VERSION_TABLE[i] + ",";
                }
            }
            if (version.equals("")) {
                version = CommonPara.VERSION_TABLE[0] + ",book,chapter,section";
            } else {
                version += "book,chapter,section";
            }
            String text = text_search.getText().toString().trim();
            if (!text.equals("")) {
                SharedPreferences settings = mActivity.getSharedPreferences(
                        CommonPara.STORE_NAME, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("search_key", text);
                editor.commit();

                String condition = "%"
                        + text_search.getText().toString().trim()
                        .replace(' ', '%') + "%";
                String sql = "select " + version + " from bible where ";
                String whereString = "";
                for (int i = 0; i < CommonPara.VERSION_NUMBER; i++) {
                    if (CommonPara.bibleVersion[i] == true) {
                        whereString += "or " + CommonPara.VERSION_TABLE[i]
                                + " like '" + condition + "' ";
                    }
                }
                if (!whereString.equals("")) {
                    sql += whereString.substring(3);
                    cursor = db.rawQuery(sql, null);

                    count = cursor.getCount();
                    while (cursor.moveToNext()) {
                        int book = cursor.getInt(cursor.getColumnIndex("book"));
                        String bookSql = "select schn from book where _id = "
                                + book;
                        Cursor bookCursor = db.rawQuery(bookSql, null);
                        bookCursor.moveToNext();
                        String bookName = bookCursor.getString(bookCursor
                                .getColumnIndex("schn"));
                        bookName += cursor.getInt(cursor
                                .getColumnIndex("chapter"))
                                + ":\r\n"
                                + cursor.getInt(cursor
                                .getColumnIndex("section"));

                        bookCursor.close();
                        String content = "";

                        for (int i = 0; i < CommonPara.VERSION_NUMBER; i++) {
                            if (CommonPara.bibleVersion[i] == true) {
                                content += cursor
                                        .getString(
                                                cursor.getColumnIndex(CommonPara.VERSION_TABLE[i]))
                                        .trim()
                                        + "\r\n\r\n";
                            }
                        }

                        if (!content.trim().equals("")) {
                            HashMap<String, Object> map = new HashMap<String, Object>();
                            map.put("book", cursor.getInt(cursor
                                    .getColumnIndex("book")));
                            map.put("chapter", cursor.getInt(cursor
                                    .getColumnIndex("chapter")));
                            map.put("section", cursor.getInt(cursor
                                    .getColumnIndex("section")));
                            map.put("title", bookName.trim());
                            map.put("verse", content.trim());
                            data.add(map);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return data;
    }
}
