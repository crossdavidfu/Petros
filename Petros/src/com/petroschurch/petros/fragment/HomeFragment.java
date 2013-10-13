package com.petroschurch.petros.fragment;

import java.util.Random;

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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.petroschurch.petros.MainActivity;
import com.petroschurch.petros.R;
import com.petroschurch.petros.lib.*;
import com.slidingmenu.lib.SlidingMenu;
import com.umeng.socialize.controller.UMServiceFactory;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragment;

public class HomeFragment extends SherlockFragment implements OnClickListener
{
    private MainActivity mActivity = null;
    private ActionBar mActionBar = null;
    private FragmentManager mManager = null;
    
    private TextView text_verse = null;
    private TextView text_mark = null;
    private TextView text_last = null;
    private TextView text_qt = null;
    private Button button_share = null;
    private LinearLayout layout_status = null;
    
    private Handler handler = null;
	
    
    private int verseBook = 0;
    private int verseChapter = 0;
    private int verseSection = 0;
    private int markBook = 0;
    private int markChapter = 0;
    private int markSection = 0;
    private int qtYear = 0;
    private int qtMonth = 0;
    private int qtDay = 0;
    
    public static final int TEXT_VERSE_SET = 0;
    public static final int TEXT_MARK_SET = 1;
    public static final int TEXT_LAST_SET = 2;
    public static final int TEXT_QT_SET = 3;
    	
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        
        mActivity = (MainActivity)getSherlockActivity();
        mActionBar = mActivity.getSupportActionBar();
        mManager = mActivity.getSupportFragmentManager();
    }

    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {     
    	View view = inflater.inflate(R.layout.frag_home,container,false);
    	text_verse = (TextView)view.findViewById(R.id.text_home_verse);
    	text_mark = (TextView)view.findViewById(R.id.text_home_mark);
    	text_last = (TextView)view.findViewById(R.id.text_home_last);
    	text_qt = (TextView)view.findViewById(R.id.text_home_qt);
    	button_share = (Button)view.findViewById(R.id.button_home_share);
    	layout_status = (LinearLayout)view.findViewById(R.id.layout_home_status);
    	
    	text_verse.setOnClickListener(this);
    	text_mark.setOnClickListener(this);
    	text_last.setOnClickListener(this);
    	text_qt.setOnClickListener(this);
    	button_share.setOnClickListener(this);
    	
    	handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) 
            {
                switch (msg.what)
                {
                    case TEXT_VERSE_SET:
                        text_verse.setText(String.valueOf(msg.obj));
                        break;
                    case TEXT_MARK_SET:
                        text_mark.setText(String.valueOf(msg.obj) + "\r\n");
                        break;
                    case TEXT_LAST_SET:
                        text_last.setText(String.valueOf(msg.obj) + "\r\n");
                        break;
                    case TEXT_QT_SET:
                        text_qt.setText(String.valueOf(msg.obj));
                        break;
                }                    
                
                super.handleMessage(msg);
            }
        };
        
        if(new Database(mActivity).OpenDbCheck())
        {
            layout_status.setVisibility(View.VISIBLE);
            new Thread() 
            {  
                public void run() 
                { 
                    ShowVerse();  
                    ShowMark();  
                    ShowLast();
                    ShowQt();
                }
            }.start();            
        }    
        else
        {
            Message msg = new Message();  
            msg.what = TEXT_VERSE_SET;
            msg.obj = "神爱世人,甚至将他的独生子赐给他们,叫一切信他的,不至灭亡,反得永生。(约翰福音 3:16)";
            handler.sendMessage(msg);
            
            layout_status.setVisibility(View.GONE);
            /*
            msg = new Message();  
            msg.what = TEXT_MARK_SET;
            msg.obj = "无最新书签";
            handler.sendMessage(msg);
            
            msg = new Message();  
            msg.what = TEXT_LAST_SET;
            msg.obj = "无上次阅读经文";
            handler.sendMessage(msg);*/
        }
        
    	return view;
    }	
    
    @Override
    public void onClick(View v) 
    {
        FragmentTransaction fragTrans = null;
        
        switch (v.getId())
        {
            case R.id.text_home_verse:
                if(verseBook*verseChapter*verseSection != 0)
                {
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
            case R.id.text_home_mark:
                if(markBook*markChapter*markSection != 0)
                {
                    CommonPara.currentBook = markBook;
                    CommonPara.currentChapter = markChapter;
                    CommonPara.currentSection = markSection;
                    CommonPara.bibleDevitionPos = 0;
                    
                    CommonPara.menuIndex = CommonPara.MENU_BIBLE;  
                    mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                    mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    fragTrans = mManager.beginTransaction();                    
                    fragTrans.replace(R.id.frag_content_frame, new BibleFragment());
                    fragTrans.commit();     
                }
                break;
            case R.id.text_home_last:
                if(CommonPara.lastBook*CommonPara.lastChapter*CommonPara.lastSection != 0)
                {
                    CommonPara.currentBook = CommonPara.lastBook;
                    CommonPara.currentChapter = CommonPara.lastChapter;
                    CommonPara.currentSection = CommonPara.lastSection;
                    CommonPara.bibleDevitionPos = 0;
                    
                    CommonPara.menuIndex = CommonPara.MENU_BIBLE;  
                    mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                    mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    fragTrans = mManager.beginTransaction();                    
                    fragTrans.replace(R.id.frag_content_frame, new BibleFragment());
                    fragTrans.commit();     
                }
                break;
            case R.id.text_home_qt:
                if(qtYear*qtMonth*qtDay != 0)
                {
                    CommonPara.currentYear = qtYear;
                    CommonPara.currentMonth = qtMonth;
                    CommonPara.currentDay = qtDay;
                    CommonPara.qtDevitionPos = 0;
                    
                    CommonPara.menuIndex = CommonPara.MENU_QT;  
                    mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                    mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    fragTrans = mManager.beginTransaction();                    
                    fragTrans.replace(R.id.frag_content_frame, new QtFragment());
                    fragTrans.commit();     
                }
                break;
            case R.id.button_home_share:
                if(verseBook*verseChapter*verseSection != 0)
                {
                    String content = text_verse.getText().toString().trim();
                    UMServiceFactory.shareTo(mActivity, content, null);
                }                
                break;
            default:
                break;
        }
    }    
    
    @Override
    public void onPause()
    {        
        super.onPause();
    }

    private void ShowVerse()
    {
        Random random = new Random(System.currentTimeMillis());
        int id = random.nextInt(CommonPara.VERSE_NUMBER)%(CommonPara.VERSE_NUMBER-1+1) + 1;
                
        SQLiteDatabase db = null;
        Cursor cursor = null;    
        
        try
        {
            db = new Database(mActivity).DbConnection(CommonPara.DB_CONTENT_PATH+CommonPara.DB_CONTENT_NAME);
            
            if(db != null)
            {
                String sql = "select * from verse where _id = " + id;    
                cursor = db.rawQuery(sql, null);
                cursor.moveToFirst();            
                int book = cursor.getInt(cursor.getColumnIndex("book"));
                int chapter = cursor.getInt(cursor.getColumnIndex("chapter"));
                int bSection = cursor.getInt(cursor.getColumnIndex("bSection"));
                int eSection = cursor.getInt(cursor.getColumnIndex("eSection"));
                
                sql = "select nuv from bible where book = " + book + " and chapter = " + chapter 
                        + " and section >= " + bSection + " and section <= " + eSection ;
                cursor = db.rawQuery(sql, null);
                
                String content = "";
                String tempContent = "";
                while(cursor.moveToNext())
                {
                    tempContent = cursor.getString(cursor.getColumnIndex("nuv"));
                    if(!tempContent.equals("NULL"))
                    {
                        content += tempContent.trim();
                    }                    
                }

                sql = "select * from book where _id = " + book;    
                cursor = db.rawQuery(sql, null);
                cursor.moveToFirst();  
                String title = "(" + cursor.getString(cursor.getColumnIndex("chn")) + " " + chapter + ":" + bSection;
                
                if(bSection == eSection)
                {
                    title += ")";
                }
                else
                {
                    title += "-" + eSection + ")";
                }
                
                verseBook = book;
                verseChapter = chapter;
                verseSection = bSection;
                
                Message msg = new Message();  
                msg.what = TEXT_VERSE_SET;
                msg.obj = content+title;
                handler.sendMessage(msg);
            }            
        }
        catch (Exception e) 
        {
            e.printStackTrace();
            verseBook = 0;
            verseChapter = 0;
            verseSection = 0;
            
            Message msg = new Message();  
            msg.what = TEXT_VERSE_SET;
            msg.obj = "神爱世人,甚至将他的独生子赐给他们,叫一切信他的,不至灭亡,反得永生。(约翰福音 3:16)";
            handler.sendMessage(msg);
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
    
    private void ShowMark()
    {
    	SQLiteDatabase db = null;
    	SQLiteDatabase dbBook = null;
        Cursor cursor = null;    
        
        try
        {
            db = new Database(getSherlockActivity()).DbConnection(CommonPara.DB_CONTENT_PATH+CommonPara.DB_CONTENT_NAME);
            dbBook = new Database(getSherlockActivity()).DbConnection(CommonPara.DB_DATA_PATH+CommonPara.DB_DATA_NAME);
            
            if(dbBook != null)
            {
            	String sql = "select * from bookmark order by updatetime DESC limit 1";
                cursor = dbBook.rawQuery(sql, null);
                cursor.moveToFirst();            
                int book = cursor.getInt(cursor.getColumnIndex("book"));
                int chapter = cursor.getInt(cursor.getColumnIndex("chapter"));
                int section = cursor.getInt(cursor.getColumnIndex("section"));
                String title = cursor.getString(cursor.getColumnIndex("title")).trim();
                
                if(db != null)
                {
                	sql = "select * from book where _id = " + book;    
                    cursor = db.rawQuery(sql, null);
                    cursor.moveToFirst();                      
                    String bookName = "(" + cursor.getString(cursor.getColumnIndex("chn"))
                    				+ " " + chapter + ":" + section + ")";
                    
                    //sql = "select nuv from bible where book = " + book + " and chapter = " + chapter + " and section = " + section;    
                    //cursor = db.rawQuery(sql, null);
                    //cursor.moveToFirst();                      
                    //String verse = cursor.getString(cursor.getColumnIndex("nuv"));
                    
                    markBook = book;
                    markChapter = chapter;
                    markSection = section;
                    
                    Message msg = new Message();  
                    msg.what = TEXT_MARK_SET;
                    //msg.obj = title + "\r\n" + verse + bookName;
                    msg.obj = title + bookName;
                    handler.sendMessage(msg);
                }
            }            
        }
        catch (Exception e) 
        {
            e.printStackTrace();
            markBook = 0;
            markChapter = 0;
            markSection = 0;
            
            Message msg = new Message();  
            msg.what = TEXT_MARK_SET;
            msg.obj = "无最新书签";
            handler.sendMessage(msg);
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
    }
    
    private void ShowLast()
    {
        SQLiteDatabase db = null;
        Cursor cursor = null;    
        
        try
        {
            db = new Database(mActivity).DbConnection(CommonPara.DB_CONTENT_PATH+CommonPara.DB_CONTENT_NAME);
            
            if(db != null)
            {                               
                String sql = "select nuv from bible where book = " + CommonPara.lastBook 
                        + " and chapter = " + CommonPara.lastChapter 
                        + " and section = " + CommonPara.lastSection;
                cursor = db.rawQuery(sql, null);
                
                String content = "";
                String tempContent = "";
                while(cursor.moveToNext())
                {
                    tempContent = cursor.getString(cursor.getColumnIndex("nuv"));
                    if(!tempContent.equals("NULL"))
                    {
                        content += tempContent.trim();
                    }                    
                }

                sql = "select * from book where _id = " + CommonPara.lastBook;    
                cursor = db.rawQuery(sql, null);
                cursor.moveToFirst();  
                String title = cursor.getString(cursor.getColumnIndex("chn"));
                title = "("+ title + " " + CommonPara.lastChapter + ":" + CommonPara.lastSection + ")";
                                
                Message msg = new Message();  
                msg.what = TEXT_LAST_SET;
                msg.obj = content+title;
                handler.sendMessage(msg);
            }            
        }
        catch (Exception e) 
        {
            e.printStackTrace();
            CommonPara.lastBook = 0;
            CommonPara.lastChapter = 0;
            CommonPara.lastSection = 0;
            
            Message msg = new Message();  
            msg.what = TEXT_LAST_SET;
            msg.obj = "无上次阅读经文";
            handler.sendMessage(msg);
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
    
    private void ShowQt()
    {
        SQLiteDatabase db = null;        
        Cursor cursor = null;
        String title = "";
        
        try
        {
            db = new Database(mActivity).DbConnection(CommonPara.DB_CONTENT_PATH+CommonPara.DB_CONTENT_NAME);
            
            int sChapter = 0;
            int sSection = 0;
            int eChapter = 0;
            int eSection = 0;
            int book = 0;
            
            if(db != null)
            {
                String sql = "select * from qt where date = '" 
                        + String.format("%04d", CommonPara.todayYear) + "-" 
                          + String.format("%02d", CommonPara.todayMonth) + "-" 
                          + String.format("%02d", CommonPara.todayDay) + "' Limit 1";  
                cursor = db.rawQuery(sql, null);
                
                
                if(cursor.moveToFirst())
                {
                    sChapter = cursor.getInt(cursor.getColumnIndex("sChapter")); 
                    sSection = cursor.getInt(cursor.getColumnIndex("sSection"));
                    eChapter = cursor.getInt(cursor.getColumnIndex("eChapter"));
                    eSection = cursor.getInt(cursor.getColumnIndex("eSection"));
                    book = cursor.getInt(cursor.getColumnIndex("book"));
                }
                
                if(book*sChapter*sSection*eChapter*eSection != 0)
                {
                    qtYear = CommonPara.todayYear;
                    qtMonth = CommonPara.todayMonth;
                    qtDay = CommonPara.todayDay;
                    sql = "select * from book where _id = " + book;
                    cursor = db.rawQuery(sql, null);

                    cursor.moveToFirst();
                    title = cursor.getString(cursor.getColumnIndex("chn")) + " "
                                        + sChapter + ":" + sSection + "-";
                    if(sChapter == eChapter)
                    {
                        title += eSection;
                    }
                    else
                    {
                        title += eChapter + ":" + eSection;
                    }
                }
                else
                {
                    qtYear = 0;
                    qtMonth = 0;
                    qtDay = 0;
                    title = "暂无今日QT";
                }
                
                Message msg = new Message();  
                msg.what = TEXT_QT_SET;
                msg.obj = title;
                handler.sendMessage(msg);                
            }
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            qtYear = 0;
            qtMonth = 0;
            qtDay = 0;
            
            Message msg = new Message();  
            msg.what = TEXT_QT_SET;
            msg.obj = "暂无今日QT";
            handler.sendMessage(msg);     
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
}
