package com.petroschurch.petros.fragment;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.petroschurch.petros.MainActivity;
import com.petroschurch.petros.R;
import com.petroschurch.petros.lib.CommonPara;
import com.petroschurch.petros.lib.Database;

import java.util.ArrayList;
import java.util.HashMap;

public class QtDevoteFragment extends SherlockFragment {
    private MainActivity mActivity = null;
    private ListView list_content;
    private Handler handler = null;
    private Message msg = null;
    //private int position = 0;    
    public static final int LIST_READ_SET = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity) getSherlockActivity();
    }

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_qt_devote, container, false);
        list_content = (ListView) view.findViewById(R.id.list_qt_devote_content);

        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case LIST_READ_SET:
                        SimpleAdapter adapter = (SimpleAdapter) (msg.obj);
                        list_content.setAdapter(adapter);
                        list_content.setSelection(CommonPara.qtDevitionPos);
                        break;
                }
                super.handleMessage(msg);
            }
        };

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //position = CommonPara.qtDevitionPos;
        //SetButtonName();
        GetVerse();
    }

    @Override
    public void onPause() {
        super.onPause();
        CommonPara.qtDevitionPos = list_content.getFirstVisiblePosition();
    }

    public void GetVerse() {
        new Thread() {
            public void run() {
                try {
                    SimpleAdapter adapter = new SimpleAdapter(mActivity,
                            GetData(),
                            R.layout.list_double_text,
                            new String[]{"title", "content"},
                            new int[]{R.id.double_title, R.id.double_content}) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);

                            TextView text_title = (TextView) view.findViewById(R.id.double_title);
                            text_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, CommonPara.font_size);
                            //text_title.setMinimumWidth(50);
                            //text_title.setTextColor(Color.MAGENTA);
                            TextView text_content = (TextView) view.findViewById(R.id.double_content);
                            text_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, CommonPara.font_size);

                            return view;
                        }
                    };

                    msg = new Message();
                    msg.what = LIST_READ_SET;
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
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

        try {
            db = new Database(mActivity)
                    .DbConnection(CommonPara.DB_CONTENT_PATH
                            + CommonPara.DB_CONTENT_NAME);
            String sql = "select * from qt where date = '"
                    + String.format("%04d", CommonPara.currentYear) + "-"
                    + String.format("%02d", CommonPara.currentMonth) + "-"
                    + String.format("%02d", CommonPara.currentDay)
                    + "' order by count";
            cursor = db.rawQuery(sql, null);
            while (cursor.moveToNext()) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                CommonPara.qtSChapter = cursor.getInt(cursor
                        .getColumnIndex("sChapter"));
                CommonPara.qtSSection = cursor.getInt(cursor
                        .getColumnIndex("sSection"));
                CommonPara.qtEChapter = cursor.getInt(cursor
                        .getColumnIndex("eChapter"));
                CommonPara.qtESection = cursor.getInt(cursor
                        .getColumnIndex("eSection"));
                CommonPara.qtBook = cursor
                        .getInt(cursor.getColumnIndex("book"));

                map.put("title",
                        cursor.getString(cursor.getColumnIndex("title")).trim());
                map.put("content",
                        cursor.getString(cursor.getColumnIndex("content"))
                                .trim());
                data.add(map);
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

