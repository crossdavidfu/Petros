package com.petroschurch.petros.fragment;

import java.util.ArrayList;
import java.util.HashMap;

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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;
import com.petroschurch.petros.MainActivity;
import com.petroschurch.petros.R;
import com.petroschurch.petros.lib.CommonPara;
import com.petroschurch.petros.lib.Database;
import com.slidingmenu.lib.SlidingMenu;

public class MarkFragment extends SherlockFragment implements OnItemClickListener
{
    private MainActivity mActivity = null;
    private ActionBar mActionBar = null;
    private FragmentManager mManager = null;
    private ListView list_content;
    
    private Handler handler = null;    
    private Message msg = null;
   
    public static final int LIST_MARK_SET = 0;
            
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        mActivity = (MainActivity)getSherlockActivity();
        mActionBar = mActivity.getSupportActionBar();
        mManager = mActivity.getSupportFragmentManager();
    }
    
    @SuppressLint("HandlerLeak")
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.frag_mark,container,false);
        list_content = (ListView)view.findViewById(R.id.list_frag_mark_content);
        list_content.setOnItemClickListener(this);
        
        handler = new Handler()
        {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) 
            {
                switch (msg.what)
                {
                    case LIST_MARK_SET:
                        SimpleAdapter adapter = (SimpleAdapter)(msg.obj);
                        list_content.setAdapter(adapter); 
                        list_content.setSelection(CommonPara.bookmarkPos);   
                        break;  
                }
                super.handleMessage(msg);
            }
        };
        
        return view;
    }
    
    @Override
    public void onResume()
    {
        super.onResume(); 
        GetBookmark();
    }    
    
    @Override
    public void onPause() 
    {        
        super.onPause();
        CommonPara.bookmarkPos = list_content.getFirstVisiblePosition();
    }
    
    @SuppressWarnings("unchecked")
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
    {
        ListView listView = (ListView)arg0;
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
    
    private void GetBookmark()
    {
        new Thread() 
        {  
            public void run() 
            { 
                try
                {
                    SimpleAdapter adapter = new SimpleAdapter(mActivity,
                            GetData(),
                            R.layout.list_double_text,     
                            new String[] {"title", "content"},   
                            new int[] {R.id.double_title, R.id.double_content});
                    
                    msg = new Message();  
                    msg.what = LIST_MARK_SET;
                    msg.obj = adapter;
                    handler.sendMessage(msg); 
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }   
            }
        }.start();

    }
    
    private ArrayList<HashMap<String,Object>> GetData()
    {
        SQLiteDatabase db = null;
        SQLiteDatabase dbBook = null;
        Cursor cursor = null;
        ArrayList<HashMap<String, Object>> data = null;
        
        try
        {
            data = new ArrayList<HashMap<String,Object>>();
            
            db = new Database(mActivity).DbConnection(CommonPara.DB_DATA_PATH+CommonPara.DB_DATA_NAME);   
            dbBook = new Database(mActivity).DbConnection(CommonPara.DB_CONTENT_PATH+CommonPara.DB_CONTENT_NAME);
            
            String sql = "select * from bookmark order by updatetime DESC";       
            cursor = db.rawQuery(sql, null);
            
            while(cursor.moveToNext())
            {  
                int book = cursor.getInt(cursor.getColumnIndex("book"));
                String bookSql = "select chn from book where _id = " + book;
                Cursor bookCursor = dbBook.rawQuery(bookSql, null);
                bookCursor.moveToNext();
                String bookName = bookCursor.getString(bookCursor.getColumnIndex("chn"));
                bookCursor.close();
                
                HashMap<String, Object> map = new HashMap<String, Object>();  
                map.put("book", cursor.getInt(cursor.getColumnIndex("book")));
                map.put("chapter", cursor.getInt(cursor.getColumnIndex("chapter")));
                map.put("section", cursor.getInt(cursor.getColumnIndex("section")));
                map.put("title", cursor.getString(cursor.getColumnIndex("title")).trim());
                map.put("content",                     
                        bookName + " " +
                        cursor.getInt(cursor.getColumnIndex("chapter")) + ":" + 
                        cursor.getInt(cursor.getColumnIndex("section")));
                data.add(map);            
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
            if(dbBook != null)
            {
                db.close();
            }        
        }

        return data;
    }
}
