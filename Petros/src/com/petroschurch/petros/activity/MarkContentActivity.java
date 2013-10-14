package com.petroschurch.petros.activity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;
import com.petroschurch.petros.R;
import com.petroschurch.petros.lib.CommonPara;
import com.petroschurch.petros.lib.Database;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MarkContentActivity extends SherlockActivity implements OnClickListener {
    EditText text_title;
    EditText text_content;
    Button button_confirm;
    Button button_delete;
    Button button_cancel;

    int book;
    int chapter;
    int section;
    boolean isExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(CommonPara.THEME);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_mark_content);

        Bundle extras = getIntent().getExtras();
        book = extras.getInt("book");
        chapter = extras.getInt("chapter");
        section = extras.getInt("section");

        text_title = (EditText) findViewById(R.id.text_act_mark_content_title);
        text_content = (EditText) findViewById(R.id.text_act_mark_content_content);
        button_confirm = (Button) findViewById(R.id.button_act_mark_content_confirm);
        button_delete = (Button) findViewById(R.id.button_act_mark_content_delete);
        button_cancel = (Button) findViewById(R.id.button_act_mark_content_cancel);

        button_confirm.setOnClickListener(this);
        button_delete.setOnClickListener(this);
        button_cancel.setOnClickListener(this);

        GetContent();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_act_mark_content_confirm:
                SaveBookmark();
                break;
            case R.id.button_act_mark_content_delete:
                DeleteBookmark();
                break;
            case R.id.button_act_mark_content_cancel:
                CancelBookmark();
                break;
        }

    }

    @SuppressLint("SimpleDateFormat")
    private void SaveBookmark() {
        if (text_title.getText().toString().trim().length() != 0) {
            SQLiteDatabase db = null;
            try {
                db = new Database(this).DbConnection(CommonPara.DB_DATA_PATH + CommonPara.DB_DATA_NAME);
                String sql;

                String updateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                if (isExist) {
                    sql = "update bookmark set updateTime = '" + updateTime + "', title = '" + text_title.getText() + "', content = '" + text_content.getText() + "'"
                            + " where book = " + book + " and chapter = " + chapter + " and section = " + section;
                    db.execSQL(sql);
                } else {
                    sql = "insert into bookmark (updateTime,book,chapter,section,title,content) values ("
                            + "'" + updateTime + "'," + book + "," + chapter + "," + section + ","
                            + "'" + text_title.getText() + "'," + "'" + text_content.getText() + "')";
                    db.execSQL(sql);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.close();
                }
            }
        }
        this.finish();
    }

    private void DeleteBookmark() {
        if (isExist) {
            SQLiteDatabase db = null;
            try {
                db = new Database(this).DbConnection(CommonPara.DB_DATA_PATH + CommonPara.DB_DATA_NAME);
                String sql;

                sql = "delete from bookmark where book = " + book + " and chapter = " + chapter + " and section = " + section;
                db.execSQL(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (db != null) {
                    db.close();
                }
            }
        }
        this.finish();
    }

    private void CancelBookmark() {
        this.finish();
    }

    private void GetContent() {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = new Database(this).DbConnection(CommonPara.DB_DATA_PATH + CommonPara.DB_DATA_NAME);

            String sql = "select * from bookmark where book = " + book + " and chapter = " + chapter + " and section = " + section;
            cursor = db.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                isExist = true;
                text_title.setText(cursor.getString(cursor.getColumnIndex("title")));
                text_content.setText(cursor.getString(cursor.getColumnIndex("content")));
            } else {
                isExist = false;
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

    }

}
