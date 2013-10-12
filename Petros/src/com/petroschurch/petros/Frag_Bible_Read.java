package com.petroschurch.petros;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.ClipboardManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.petroschurch.petros.lib.*;
import com.petroschurch.petros.lib.ProgressShow.ProgressCallBack;
import com.umeng.socialize.controller.UMServiceFactory;
import com.actionbarsherlock.app.SherlockFragment;

@SuppressWarnings("deprecation")
public class Frag_Bible_Read extends SherlockFragment implements OnClickListener, OnItemClickListener, OnItemLongClickListener ,OnSeekBarChangeListener
{
    private Act_Main mActivity = null;
    
    private ListView list_content;
    private Button button_book;
    private Button button_chapter;
    private Button button_left;
    private Button button_right;
    private Button button_start;
    private SeekBar bar_progress;
    private LinearLayout layout_mp3;
    
    private int versePos = 0;
    
    private MediaPlayer mediaPlayer;  
    
    private Message msg = null;
    private Handler handler = null;
    private Handler mp3Handler = null;
    private Thread mp3Thread = null;

    private int mp3Pos = 0;
    
    public static final int LIST_READ_SET = 0;
    public static final int BTN_BOOK_SET = 1;
    public static final int BTN_CHAPTER_SET = 2;
    
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
	    super.onCreate(savedInstanceState);
        //  setRetainInstance(true);
	    mActivity = (Act_Main)getSherlockActivity();
    }

	@SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
	    View view = inflater.inflate(R.layout.frag_bible_read,container,false);
	    list_content = (ListView)view.findViewById(R.id.list_bible_read_content);
        button_book = (Button)view.findViewById(R.id.button_bible_read_book);
        button_chapter = (Button)view.findViewById(R.id.button_bible_read_chapter);
        button_left = (Button)view.findViewById(R.id.button_bible_read_left);
        button_right = (Button)view.findViewById(R.id.button_bible_read_right);        
        button_start = (Button)view.findViewById(R.id.button_bible_read_start);
        bar_progress = (SeekBar)view.findViewById(R.id.bar_bible_read_progress);        
        layout_mp3 = (LinearLayout)view.findViewById(R.id.layout_bible_read_mp3);
        
        //list_content.setDivider(null);
        
        if(CommonPara.mp3_ver == 0)
        {
            layout_mp3.setVisibility(View.GONE);   
            CommonPara.bibleMp3Pos = 0;
            mp3Pos = CommonPara.bibleMp3Pos;
            bar_progress.setProgress(mp3Pos);
        }
        
        button_book.setOnClickListener(this);
        button_chapter.setOnClickListener(this);
        button_left.setOnClickListener(this);
        button_right.setOnClickListener(this);
        list_content.setOnItemClickListener(this);
        list_content.setOnItemLongClickListener(this);
        
        button_start.setOnClickListener(this);
        bar_progress.setOnSeekBarChangeListener(this);
        
        handler = new Handler()
        {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) 
            {
                switch (msg.what)
                {
                    case LIST_READ_SET:
                        SimpleAdapter adapter = (SimpleAdapter)(msg.obj);
                        list_content.setAdapter(adapter); 
                        list_content.setSelection(CommonPara.currentSection-1);   
                        break;                        
                    case BTN_BOOK_SET:
                        button_book.setText(String.valueOf(msg.obj));
                        break;                        
                    case BTN_CHAPTER_SET:
                        button_chapter.setText(String.valueOf(msg.obj));
                        break;
                }
                super.handleMessage(msg);
            }
        };
        
        mp3Handler = new Handler()
        {
            @Override
            public void handleMessage(Message msg) 
            {
                CheckMp3();             
                
                super.handleMessage(msg);
            }
        };
                
        return view;
    }

	@Override
    public void onResume()
    {
        super.onResume(); 
        mp3Pos = CommonPara.bibleMp3Pos;
        if(mp3Pos == 0)
        {
            bar_progress.setProgress(mp3Pos);  
        }        
        SetButtonName();
        GetVerse();
    }

    @Override
    public void onPause() 
    {        
        super.onPause();
        CommonPara.currentSection = list_content.getFirstVisiblePosition()+1;
        CommonPara.bibleMp3Pos = mp3Pos;
        StopMp3();
    }
    
    @Override
    public void onClick(View v) 
    {
        Intent intent;
        intent = new Intent();
        
        switch (v.getId())
        {
            case R.id.button_bible_read_book:
                intent.setClass(mActivity, Act_Book_Select.class);
                startActivity(intent);
                break;
            case R.id.button_bible_read_chapter:
                intent.setClass(mActivity, Act_Chapter_Select.class);
                startActivity(intent);
                break;
            case R.id.button_bible_read_left:
                ChangeVerse(false);
                break;
            case R.id.button_bible_read_right:
                ChangeVerse(true);
                break;
            case R.id.button_bible_read_start:
                CheckMp3();
                break;
            default:
                break;
        }
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

            String bookName = button_book.getText().toString().trim();
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
                    intent.setClass(mActivity, Act_Mark_Content.class);
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
        intent.setClass(mActivity, Act_Mark_Content.class);
        startActivity(intent);     
    }
          
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) 
    {
        
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) 
    {
        
    }
  
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromUser)
    {
        if (fromUser==true) 
        {  
            if(mediaPlayer != null && mediaPlayer.isPlaying())
            {
                mediaPlayer.seekTo(progress*mediaPlayer.getDuration()/100);   
            }             
        }  
    }

    private void CheckMp3()
    {
        if(mediaPlayer != null && mediaPlayer.isPlaying())
        {
            StopMp3();
        }
        else
        {
            final String mp3 = CommonPara.BIBLE_MP3_PATH 
                    + CommonPara.MP3_VER_TABLE[CommonPara.mp3_ver] + "/"
                    + String.format("%02d", CommonPara.currentBook) + "_"
                    + String.format("%03d", CommonPara.currentChapter) + ".mp3";
            File file = new File(mp3);
            if(!file.exists())
            {
                new File(file.getParent()).mkdirs();                
                final String url = CommonPara.BIBLE_MP3_URL 
                        + CommonPara.MP3_VER_TABLE[CommonPara.mp3_ver] + "/"
                        + String.format("%02d", CommonPara.currentBook) + "_"
                        + String.format("%03d", CommonPara.currentChapter) + ".mp3";
                
                if(CommonFunc.isWifi(mActivity) || CommonPara.allow_gprs)
                {
                    final ProgressShow dialog = new ProgressShow(
                            mActivity, "请稍候", "正在载入", ProgressShow.DIALOG_TYPE_SPINNER, ProgressShow.DIALOG_DEFAULT_MAX);
                    dialog.ShowDialog(new ProgressCallBack() 
                    {  
                        public void action() 
                        {   
                            if(Download.DownFile(url, mp3))
                            {
                                mp3Handler.sendEmptyMessage(0);
                            }
                            dialog.CloseDialog();
                        }
                    });
                }
                else
                {
                    Toast.makeText(mActivity, "下载失败\r\n请在WIFI环境下再下载\r\n或在设置中打开使用数据流量选项", Toast.LENGTH_LONG).show();
                }
                
            }
            else 
            {
                PlayMp3();
            }
        }  
    }
    
    private void PlayMp3()
    {
        String file = CommonPara.BIBLE_MP3_PATH 
                + CommonPara.MP3_VER_TABLE[CommonPara.mp3_ver] + "/"
                + String.format("%02d", CommonPara.currentBook) + "_"
                + String.format("%03d", CommonPara.currentChapter) + ".mp3";
        mediaPlayer = new MediaPlayer();
        try
        {
            mediaPlayer.setDataSource(file);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new OnCompletionListener()
            {
                public void onCompletion(MediaPlayer player) 
                {
                    try 
                    {
                        mediaPlayer.stop();
                        ChangeVerse(true);
                        CheckMp3();
                    }
                    catch (Exception e) 
                    {
                        e.printStackTrace();
                    }
                }
            });
            mediaPlayer.start();
            mediaPlayer.seekTo(mp3Pos);
            mp3Thread = new Thread( new Runnable() 
            {     
                public void run() 
                { 
                    try
                    {
                        while(mediaPlayer.isPlaying())
                        {                            
                            bar_progress.setProgress(mediaPlayer.getCurrentPosition()*100/mediaPlayer.getDuration());
                            mp3Pos = mediaPlayer.getCurrentPosition(); 
                            Thread.sleep(200);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }            
                }            
            });
            mp3Thread.start();            
            button_start.setText(R.string.pause_sign);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            StopMp3();
        }  
    }
    
    private void StopMp3()
    {
        if(mediaPlayer != null)
        {
            mediaPlayer.stop();            
        }
        button_start.setText(R.string.start_sign);
        CommonPara.bibleMp3Pos = mp3Pos;
    }
    
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
                
        SharedPreferences settings = mActivity.getSharedPreferences(CommonPara.STORE_NAME, Context.MODE_PRIVATE);  
        SharedPreferences.Editor editor = settings.edit();  
        editor.putInt("currentBook", CommonPara.currentBook);  
        editor.putInt("currentChapter", CommonPara.currentChapter); 
        editor.putInt("currentSection", CommonPara.currentSection); 
        editor.commit();  
    }    
    
    public void SetButtonName()
    {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        
        try
        {
            db = new Database(mActivity).DbConnection(CommonPara.DB_CONTENT_PATH+CommonPara.DB_CONTENT_NAME);
            
            String sql = "select * from book where _id = " + CommonPara.currentBook;        
            cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            CommonPara.currentCount = cursor.getInt(cursor.getColumnIndex("count"));
            
            msg = new Message();  
            msg.what = BTN_BOOK_SET;
            msg.obj = cursor.getString(cursor.getColumnIndex("chn"));
            handler.sendMessage(msg);
            msg = new Message();  
            msg.what = BTN_CHAPTER_SET;
            msg.obj = "第"+CommonPara.currentChapter+"章";
            handler.sendMessage(msg);
            
            if(CommonPara.currentBook != 1)
            {
                sql = "select * from book where _id = " + (CommonPara.currentBook-1);        
                cursor = db.rawQuery(sql, null);
                cursor.moveToFirst();
                CommonPara.previousCount = cursor.getInt(cursor.getColumnIndex("count"));
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
    
    public void ChangeVerse(Boolean isNext)
    {        
        if(!isNext)
        {
            if(CommonPara.currentChapter > 1)
            {
                CommonPara.currentChapter--;
            }
            else 
            {
                if(CommonPara.currentBook > 1)
                {
                    CommonPara.currentBook--;
                    CommonPara.currentChapter = CommonPara.previousCount;                    
                }
            }
        }
        else
        {
            if(CommonPara.currentChapter < CommonPara.currentCount)
            {
                CommonPara.currentChapter++;
            }
            else 
            {
                if(CommonPara.currentBook < 66)
                {
                    CommonPara.currentBook++;
                    CommonPara.currentChapter = 1;
                }
            }
        }
        StopMp3();
        CommonPara.currentSection = 1;
        CommonPara.bibleDevitionPos = 0;
        CommonPara.bibleMp3Pos = 0;
        //position = CommonPara.currentSection;
        mp3Pos = CommonPara.bibleMp3Pos;
        bar_progress.setProgress(mp3Pos);
        
        SetButtonName();
        GetVerse();     
    }
    
    private ArrayList<HashMap<String,Object>> GetData()
    {
        SQLiteDatabase db = null;  
        Cursor cursor = null;        
        ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String,Object>>();
        
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
            
            String sql = "select "+ version +" from bible where book = " + CommonPara.currentBook 
                    + " and chapter = " + CommonPara.currentChapter;
            
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
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("book", cursor.getInt(cursor.getColumnIndex("book")));
                    map.put("chapter", cursor.getInt(cursor.getColumnIndex("chapter")));
                    map.put("section", cursor.getInt(cursor.getColumnIndex("section")));
                    map.put("verse", content.trim());                
                    data.add(map); 
                }                    
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
	
}
