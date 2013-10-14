package com.petroschurch.petros.fragment;

import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.petroschurch.petros.MainActivity;
import com.petroschurch.petros.R;
import com.petroschurch.petros.lib.CommonPara;
import com.petroschurch.petros.lib.Database;
import com.slidingmenu.lib.SlidingMenu;

import java.util.Random;

public class VerseFragment extends SherlockFragment implements OnClickListener {
    private Button button_next;
    private TextView text_verse;

    private int verseBook = 0;
    private int verseChapter = 0;
    private int verseSection = 0;

    private MainActivity mActivity = null;
    private ActionBar mActionBar = null;
    private FragmentManager mManager = null;
    private Handler handler = null;

    private static final int TEXT_VERSE_SET = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActivity = (MainActivity) getSherlockActivity();
        mActionBar = mActivity.getSupportActionBar();
        mManager = mActivity.getSupportFragmentManager();
    }

    @SuppressLint("HandlerLeak")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_verse, container, false);
        button_next = (Button) view.findViewById(R.id.button_verse_next);
        text_verse = (TextView) view.findViewById(R.id.text_verse_content);
        button_next.setOnClickListener(this);
        text_verse.setOnClickListener(this);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case TEXT_VERSE_SET:
                        text_verse.setText(String.valueOf(msg.obj));
                        break;
                }
                super.handleMessage(msg);
            }
        };
        return view;
    }


    @Override
    public void onResume() {
        GetVerse();
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction fragTrans = null;

        switch (v.getId()) {
            case R.id.button_verse_next:
                GetVerse();
                break;
            case R.id.text_verse_content:
                if (verseBook * verseChapter * verseSection != 0) {
                    CommonPara.currentBook = verseBook;
                    CommonPara.currentChapter = verseChapter;
                    CommonPara.currentSection = verseSection;
                    CommonPara.bibleDevitionPos = 0;

                    CommonPara.menuIndex = CommonPara.MENU_BIBLE;
                    mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                    mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    fragTrans = mManager.beginTransaction();
                    fragTrans.replace(R.id.frag_content_frame, new BibleFragment());
                    fragTrans.commit();
                }
                break;
        }
    }

    private void GetVerse() {
        Random random = new Random(System.currentTimeMillis());
        int id = random.nextInt(CommonPara.VERSE_NUMBER) % (CommonPara.VERSE_NUMBER - 1 + 1) + 1;

        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = new Database(mActivity).DbConnection(CommonPara.DB_CONTENT_PATH + CommonPara.DB_CONTENT_NAME);

            if (db != null) {
                String sql = "select * from verse where _id = " + id;
                cursor = db.rawQuery(sql, null);
                cursor.moveToFirst();
                int book = cursor.getInt(cursor.getColumnIndex("book"));
                int chapter = cursor.getInt(cursor.getColumnIndex("chapter"));
                int bSection = cursor.getInt(cursor.getColumnIndex("bSection"));
                int eSection = cursor.getInt(cursor.getColumnIndex("eSection"));

                sql = "select nuv from bible where book = " + book + " and chapter = " + chapter
                        + " and section >= " + bSection + " and section <= " + eSection;
                cursor = db.rawQuery(sql, null);

                String content = "";
                String tempContent = "";
                while (cursor.moveToNext()) {
                    tempContent = cursor.getString(cursor.getColumnIndex("nuv"));
                    if (!tempContent.equals("NULL")) {
                        content += tempContent.trim();
                    }
                }

                sql = "select * from book where _id = " + book;
                cursor = db.rawQuery(sql, null);
                cursor.moveToFirst();
                String title = "(" + cursor.getString(cursor.getColumnIndex("chn")) + " " + chapter + ":" + bSection;

                if (bSection == eSection) {
                    title += ")";
                } else {
                    title += "-" + eSection + ")";
                }

                verseBook = book;
                verseChapter = chapter;
                verseSection = bSection;

                Message msg = new Message();
                msg.what = TEXT_VERSE_SET;
                msg.obj = content + title;
                handler.sendMessage(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            verseBook = 0;
            verseChapter = 0;
            verseSection = 0;

            Message msg = new Message();
            msg.what = TEXT_VERSE_SET;
            msg.obj = "神爱世人,甚至将他的独生子赐给他们,叫一切信他的,不至灭亡,反得永生。(约翰福音 3:16)";
            handler.sendMessage(msg);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
    }
}
