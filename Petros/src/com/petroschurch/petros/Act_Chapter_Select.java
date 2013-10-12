package com.petroschurch.petros;

import java.util.ArrayList;
import java.util.HashMap;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.petroschurch.petros.lib.CommonPara;
import com.petroschurch.petros.lib.Database;

public class Act_Chapter_Select extends SherlockActivity implements OnItemClickListener
{        
    GridView grid_chapter;
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        setTheme(CommonPara.THEME);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_chapter_select);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        grid_chapter = (GridView)findViewById(R.id.grid_act_chapter_select_chapter);
        
        SimpleAdapter adapter = new SimpleAdapter(this,
                GetData(),
                R.layout.grid_menu_text,     
                new String[] {"chapter"},   
                new int[] {R.id.grid_menu_text_text});
        grid_chapter.setAdapter(adapter);
        grid_chapter.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
    {
        CommonPara.currentChapter = arg2+1;
        CommonPara.currentSection = 1;
        CommonPara.bibleDevitionPos = 0;
        CommonPara.bibleMp3Pos = 0;
        
        this.finish();   
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) 
        {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    private ArrayList<HashMap<String,Object>> GetData()
    {
        ArrayList<HashMap<String, Object>> data = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        
        try
        {
            data = new ArrayList<HashMap<String,Object>>();
            db = new Database(this).DbConnection(CommonPara.DB_CONTENT_PATH+CommonPara.DB_CONTENT_NAME);            
            String sql = "select * from book where _id = " + CommonPara.currentBook;        
            cursor = db.rawQuery(sql, null);
            cursor.moveToFirst();
            int max = cursor.getInt(cursor.getColumnIndex("count"));            
            for(int i = 1; i <= max; i++)
            {     
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("chapter", i);    
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
        }        
        return data;
    }
}

