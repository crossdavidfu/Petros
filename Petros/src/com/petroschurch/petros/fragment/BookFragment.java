package com.petroschurch.petros.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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

public class BookFragment extends SherlockFragment implements OnClickListener {
    private MainActivity mActivity = null;
    private ListView list_content;
    public Button button_date;
    public Button button_type;
    private Message msg = null;
    private Handler handler = null;

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
        View view = inflater.inflate(R.layout.frag_book, container, false);
        list_content = (ListView) view.findViewById(R.id.list_book_content);
        button_date = (Button) view.findViewById(R.id.button_book_date);
        button_type = (Button) view.findViewById(R.id.button_book_type);

        button_type.setOnClickListener(this);
        button_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCreateDialog(button_date).show();
            }
        });

        handler = new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case LIST_READ_SET:
                        SimpleAdapter adapter = (SimpleAdapter) (msg.obj);
                        list_content.setAdapter(adapter);
                        list_content.setSelection(CommonPara.bibleDevitionPos);
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
        SetButtonName();
        GetVerse();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public void SetButtonName() {
        button_date.setText(String.format("%04d", CommonPara.bookYear) + "-"
                + String.format("%02d", CommonPara.bookMonth) + "-"
                + String.format("%02d", CommonPara.bookDay));
        button_type.setText(CommonPara.BOOK_NAME[CommonPara.bookType]);
    }

    public void GetVerse() {
        new Thread() {
            public void run() {
                try {
                    SimpleAdapter adapter = new SimpleAdapter(mActivity,
                            GetData(),
                            R.layout.list_single_text,
                            new String[]{"content"},
                            new int[]{R.id.single_content}) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);

                            TextView text_content = (TextView) view.findViewById(R.id.single_content);
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
        ArrayList<HashMap<String, Object>> data = null;

        try {
            data = new ArrayList<HashMap<String, Object>>();
            HashMap<String, Object> map = null;

            db = new Database(mActivity).DbConnection(CommonPara.DB_CONTENT_PATH + CommonPara.DB_CONTENT_NAME);
            String sql = "select * from " + CommonPara.BOOK_TABLE[CommonPara.bookType] + " where month = "
                    + CommonPara.bookMonth + " and day = " + CommonPara.bookDay;
            cursor = db.rawQuery(sql, null);

            if (cursor.moveToFirst()) {
                map = new HashMap<String, Object>();
                map.put("content", cursor.getString(cursor.getColumnIndex("content")).trim());
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

    protected Dialog onCreateDialog(final Button btn) {
        Dialog dialog = null;
        //Calendar time = Calendar.getInstance();
        dialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker dp, int year, int month, int day) {
                        CommonPara.bookYear = year;
                        CommonPara.bookMonth = month + 1;
                        CommonPara.bookDay = day;

                        SetButtonName();
                        GetVerse();
                    }
                },
                CommonPara.bookYear,
                CommonPara.bookMonth - 1,
                CommonPara.bookDay);

        return dialog;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_book_type:
                AlertDialog dialog = new AlertDialog.Builder(mActivity)
                        .setIcon(android.R.drawable.btn_star)
                        .setTitle("书册")
                        .setItems(CommonPara.BOOK_NAME, onSelect).create();
                dialog.show();

                break;

            default:
                break;
        }
    }

    DialogInterface.OnClickListener onSelect = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            CommonPara.bookType = which;
            SetButtonName();
            GetVerse();
        }

    };
}
