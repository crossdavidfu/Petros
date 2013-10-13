package com.petroschurch.petros.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.petroschurch.petros.MainActivity;
import com.petroschurch.petros.R;
import com.petroschurch.petros.activity.MarkContentActivity;
import com.petroschurch.petros.lib.CommonPara;
import com.petroschurch.petros.lib.Database;
import com.umeng.socialize.controller.UMServiceFactory;

@SuppressWarnings("deprecation")
public class QtReadFragment extends SherlockFragment implements OnItemClickListener, OnItemLongClickListener
{
    private MainActivity mActivity = null;
    
    public ListView list_content;
    public Button button_date;
    public TextView text_section;  
    
    private Message msg = null;    
    private Handler handler = null; 
    
    private int versePos = 0;
    
    //public int position = 0;
    public static final int LIST_READ_SET = 0;
    String titleString = "";
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //  setRetainInstance(true);
        mActivity = (MainActivity)getSherlockActivity();
    }
    
    @SuppressLint("HandlerLeak")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frag_qt_read,container,false);
        
        list_content = (ListView)view.findViewById(R.id.list_qt_read_content);
        button_date =(Button)view.findViewById(R.id.button_qt_read_day);
        text_section = (TextView)view.findViewById(R.id.text_qt_read_title);

        button_date.setOnClickListener(new View.OnClickListener() 
        {
            @Override
            public void onClick(View v) 
            {
                onCreateDialog(button_date).show();
            }
        });
        
        list_content.setOnItemClickListener(this);
        list_content.setOnItemLongClickListener(this);
        
        handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) 
            {
                SimpleAdapter adapter = (SimpleAdapter)(msg.obj);
                list_content.setAdapter(adapter); 
                list_content.setSelection(CommonPara.qtReaderPos);
                text_section.setText(titleString);
                
                super.handleMessage(msg);
            }
        };    
        return view;
    }
    
    @Override
    public void onResume()
    {
        super.onResume(); 
        //position = CommonPara.qtReaderPos;
        SetButtonName();
        GetVerse();
    }
    
    @Override
    public void onPause() 
    {        
        super.onPause();
        CommonPara.qtReaderPos = list_content.getFirstVisiblePosition();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
    {
        ListView listView = (ListView)arg0;
        HashMap<String, Object> map = (HashMap<String, Object>) listView.getItemAtPosition(arg2);
        
        Intent intent = new Intent();
        intent.putExtra("book", Integer.parseInt(map.get("book").toString()));
        intent.putExtra("chapter", Integer.parseInt(map.get("chapter").toString()));
        intent.putExtra("section", Integer.parseInt(map.get("section").toString()));
        intent.setClass(mActivity, MarkContentActivity.class);
        startActivity(intent);    
    }
      
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id)
    {        
        versePos = position;
        AlertDialog dialog = new AlertDialog.Builder(mActivity)  
            .setIcon(android.R.drawable.btn_star)  
            .setTitle("功能")  
            .setItems(CommonPara.VERSE_CHOISE, onSelect).create();  
        dialog.show();  
        return true;
    }
    
    DialogInterface.OnClickListener onSelect = new DialogInterface.OnClickListener() 
    {
        @SuppressWarnings("unchecked")
        @Override
        public void onClick(DialogInterface dialog, int which)
        {
            HashMap<String, Object> map = (HashMap<String, Object>) list_content.getItemAtPosition(versePos);
            SQLiteDatabase db = null;
            Cursor cursor = null;
            String bookName = "";
            try
            {
                db = new Database(mActivity).DbConnection(CommonPara.DB_CONTENT_PATH+CommonPara.DB_CONTENT_NAME);                
                String sql = "select * from book where _id = " + map.get("book");        
                cursor = db.rawQuery(sql, null);
                cursor.moveToFirst();
                bookName = cursor.getString(cursor.getColumnIndex("chn")).trim();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if(cursor != null)
                {
                    cursor.close();
                }
                if(db != null)
                {
                    db.close();
                }
            }
            
            String content = map.get("verse").toString() + "\r\n\r\n"
                      + "("+ bookName + " " 
                      + map.get("chapter").toString() + ":" 
                      + map.get("section").toString() + ")";
            
            switch (which) 
            {
                case CommonPara.VERSE_CHOISE_MARK:
                    Intent intent = new Intent();
                    intent.putExtra("book", Integer.parseInt(map.get("book").toString()));
                    intent.putExtra("chapter", Integer.parseInt(map.get("chapter").toString()));
                    intent.putExtra("section", Integer.parseInt(map.get("section").toString()));
                    intent.setClass(mActivity, MarkContentActivity.class);
                    startActivity(intent);     
                    break;
                case CommonPara.VERSE_CHOISE_COPY:
                    ClipboardManager clip = (ClipboardManager)mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                    clip.setText(content); // 复制
                    Toast.makeText(mActivity, content + "    已复制到剪贴板", Toast.LENGTH_SHORT).show();
                    break;
                case CommonPara.VERSE_CHOISE_SHARE:
                    UMServiceFactory.shareTo(mActivity, content, null);
                    break;
            }
        }
        
    };
    
    public void GetVerse() 
    {
        new Thread() 
        {  
            public void run() 
            {  
                try
                {
                    SimpleAdapter adapter = new SimpleAdapter(mActivity,
                            GetData(),
                            R.layout.list_parallel_text,     
                            new String[] {"section","verse"},   
                            new int[] {R.id.para_title,R.id.para_content})
                    {
                        @SuppressWarnings("unchecked")
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) 
                        {                            
                            View view =super.getView(position, convertView, parent);

                            TextView text_title=(TextView) view.findViewById(R.id.para_title);
                            //text_title.setTextSize(TypedValue.COMPLEX_UNIT_SP, CommonPara.font_size);
                            //text_title.setMinimumWidth(50);
                            //text_title.setTextColor(Color.MAGENTA);
                            TextView text_content=(TextView) view.findViewById(R.id.para_content);
                            text_content.setTextSize(TypedValue.COMPLEX_UNIT_SP, CommonPara.font_size);
                            
                            if(CommonPara.show_color)
                            {
                                ListView listView = (ListView)parent;
                                HashMap<String, Object> map = (HashMap<String, Object>) listView.getItemAtPosition(position);
                                
                                int book = Integer.parseInt(map.get("book").toString());
                                int chapter = Integer.parseInt(map.get("chapter").toString());
                                int section = Integer.parseInt(map.get("section").toString());
                                
                                SQLiteDatabase db = null;
                                Cursor cursor = null;
                                
                                try
                                {
                                    db = new Database(mActivity).DbConnection(CommonPara.DB_DATA_PATH+CommonPara.DB_DATA_NAME);                
                                    String sql = "select * from bookmark where book = " + book 
                                            + " and chapter = " + chapter 
                                            + " and section = " + section;        
                                    cursor = db.rawQuery(sql, null);
                                    if(cursor.moveToFirst())
                                    {
                                        text_title.setTextColor(CommonPara.HIGHLIGHT_TEXT_COLOR);  
                                    }
                                    else
                                    {
                                        text_title.setTextColor(CommonPara.DEFAULT_TEXT_COLOR);
                                    }
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                                finally
                                {
                                    if(cursor != null)
                                    {
                                        cursor.close();
                                    }
                                    if(db != null)
                                    {
                                        db.close();
                                    }
                                } 
                            }
                            return view;
                        }                    
                    };          
                    
                    msg = new Message();  
                    msg.what = LIST_READ_SET;
                    msg.obj = adapter;
                    handler.sendMessage(msg);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    
                }
                
            }  
        }.start(); 

    }    
    
    public void SetButtonName()
    {
        button_date.setText(String.format("%04d", CommonPara.currentYear) + "-" 
                          + String.format("%02d", CommonPara.currentMonth) + "-" 
                          + String.format("%02d", CommonPara.currentDay));
    }
    
    private ArrayList<HashMap<String,Object>> GetData()
    {
        SQLiteDatabase db = null;        
        ArrayList<HashMap<String,Object>> data = new ArrayList<HashMap<String,Object>>();
        HashMap<String, Object> map = null;
        Cursor cursor = null;
        
        try
        {
            db = new Database(mActivity).DbConnection(CommonPara.DB_CONTENT_PATH+CommonPara.DB_CONTENT_NAME);
            
            String version = "";
            for(int i=0;i<CommonPara.VERSION_NUMBER;i++)
            {
                if(CommonPara.bibleVersion[i] == true)
                {
                    version += CommonPara.VERSION_TABLE[i] + ",";
                }
            }
            
            if(version.equals(""))
            {
                version = CommonPara.VERSION_TABLE[0] + ",book,chapter,section";
            }
            else
            {
                version += "book,chapter,section";
            }
            
            String sql = "select * from qt where date = '" 
                        + String.format("%04d", CommonPara.currentYear) + "-" 
                          + String.format("%02d", CommonPara.currentMonth) + "-" 
                          + String.format("%02d", CommonPara.currentDay) + "' order by count";       
            cursor = db.rawQuery(sql, null);
                
            if(cursor.moveToFirst())
            {
                CommonPara.qtSChapter = cursor.getInt(cursor.getColumnIndex("sChapter")); 
                CommonPara.qtSSection = cursor.getInt(cursor.getColumnIndex("sSection"));
                CommonPara.qtEChapter = cursor.getInt(cursor.getColumnIndex("eChapter"));
                CommonPara.qtESection = cursor.getInt(cursor.getColumnIndex("eSection"));
                CommonPara.qtBook = cursor.getInt(cursor.getColumnIndex("book"));

                if(CommonPara.qtSChapter == CommonPara.qtEChapter)
                {
                    sql = "select "+ version +" from bible where book = " + CommonPara.qtBook + " and chapter = " + CommonPara.qtSChapter 
                            + " and section >= " + CommonPara.qtSSection + " and section <= " + CommonPara.qtESection ;
                }
                else
                {
                    sql = "select "+ version +" from bible where book = " + CommonPara.qtBook + " and ( " 
                            + "(chapter = " + CommonPara.qtSChapter + " and section >= " + CommonPara.qtSSection + ")"
                            + " or (chapter = " + CommonPara.qtEChapter + " and section <= " + CommonPara.qtESection + ")"
                            + " or (chapter > " + CommonPara.qtSChapter + " and chapter < " + CommonPara.qtEChapter + " ))";
                }            
                cursor = db.rawQuery(sql, null);
                
                String tempContent = "";                        
                while(cursor.moveToNext())
                {   
                    String content = "";
                    for(int i =0;i<CommonPara.VERSION_NUMBER;i++)
                    {
                        if(CommonPara.bibleVersion[i] == true)
                        {
                            tempContent = cursor.getString(cursor.getColumnIndex(CommonPara.VERSION_TABLE[i]));
                            if(!tempContent.equals("NULL"))
                            {
                                content += tempContent.trim() + "\r\n\r\n";
                            }   
                            else 
                            {
                                content += "————\r\n\r\n";
                            }
                        }
                    }
                    if(content.trim() != "")
                    {
                        map = new HashMap<String, Object>();
                        map.put("book", cursor.getInt(cursor.getColumnIndex("book")));
                        map.put("chapter", cursor.getInt(cursor.getColumnIndex("chapter")));
                        map.put("section", cursor.getInt(cursor.getColumnIndex("section")));
                        map.put("verse", content.trim());
                        map.put("title", cursor.getInt(cursor.getColumnIndex("chapter")) 
                                + ":\r\n" +cursor.getInt(cursor.getColumnIndex("section")));
                        data.add(map); 
                    }    
                }
            }

            if(map != null)
            {
                sql = "select * from book where _id = " + CommonPara.qtBook;
                cursor = db.rawQuery(sql, null);

                cursor.moveToFirst();
                titleString = cursor.getString(cursor.getColumnIndex("chn")) + " "
                                    + CommonPara.qtSChapter + ":" + CommonPara.qtSSection + "-" ;
                if(CommonPara.qtSChapter == CommonPara.qtEChapter)
                {
                    titleString += CommonPara.qtESection;
                }
                else
                {
                    titleString += CommonPara.qtEChapter + ":" + CommonPara.qtESection;
                }
            }
            else
            {
                titleString = "暂无内容";
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(cursor != null)
            {
                cursor.close();
            }
            if(db != null)
            {
                db.close();
            }
        }
        
        return data;
    }
    
    protected Dialog onCreateDialog(final Button btn) 
    {
        Dialog dialog = null;
        //Calendar time = Calendar.getInstance();
        dialog = new DatePickerDialog(getActivity(), 
                new DatePickerDialog.OnDateSetListener()
                {
                    public void onDateSet(DatePicker dp, int year, int month, int day) 
                    {
                        CommonPara.currentYear = year;     
                        CommonPara.currentMonth = month+1;     
                        CommonPara.currentDay = day;  
                        
                        CommonPara.qtReaderPos = 0;
                        CommonPara.qtDevitionPos = 0;
                        //position = CommonPara.qtReaderPos;                        
                        SetButtonName();
                        GetVerse();
                    }
                }, 
                CommonPara.currentYear,
                CommonPara.currentMonth-1,
                CommonPara.currentDay);
       
        return dialog;
    }


}

