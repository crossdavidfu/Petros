package com.petroschurch.petros;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.petroschurch.petros.lib.*;

public class Act_Book_Select extends SherlockActivity implements OnItemClickListener
{        
    ListView list_book;
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        setTheme(CommonPara.THEME);
        super.onCreate(savedInstanceState);        
        setContentView(R.layout.act_book_select);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        list_book = (ListView)findViewById(R.id.list_act_book_select_book);
        list_book.setDivider(null);
        
        SimpleAdapter adapter = new SimpleAdapter(this,
                GetData(),
                R.layout.list_menu_text,     
                new String[] {"book"},   
                new int[] {R.id.list_menu_text_text});
        list_book.setAdapter(adapter);
        list_book.setOnItemClickListener(this);
    }
 
    @Override
    protected void onPause()
    {        
        super.onPause();
        this.finish();
    }
    
    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
    {
        CommonPara.currentBook = arg2 + 1;
        CommonPara.currentChapter = 1;
        CommonPara.currentSection = 1;
        CommonPara.bibleDevitionPos = 0;
        CommonPara.bibleMp3Pos = 0;
        
        Intent intent = new Intent();
        
        intent.setClass(this, Act_Chapter_Select.class);
        startActivity(intent);     
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
    
    /*
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
        if(resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case 2:
                    Bundle bundle = data.getExtras(); 
                    book = bundle.getInt("book");
                    chapter = bundle.getInt("chapter");
                    
                    Intent intent = new Intent();
                    intent.putExtra("book", book);
                    intent.putExtra("chapter", chapter);
                    
                    Act_Book_Select.this.setResult(RESULT_OK, intent);
                    Act_Book_Select.this.finish();       
                    break;
                    
                default:
                    break;
            }
        }
    }
    */
    
    private ArrayList<HashMap<String,Object>> GetData()
    {
        ArrayList<HashMap<String, Object>> data = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;
        
        try
        {
            data = new ArrayList<HashMap<String,Object>>();
            db = new Database(this).DbConnection(CommonPara.DB_CONTENT_PATH+CommonPara.DB_CONTENT_NAME);            
            String sql = "select * from book";            
            cursor = db.rawQuery(sql, null);
            while(cursor.moveToNext())
            {     
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("book", cursor.getString(cursor.getColumnIndex("chn")) + "  "
                        + cursor.getString(cursor.getColumnIndex("eng")));    
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
