package com.petroschurch.petros;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.petroschurch.petros.lib.*;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengUpdateAgent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

public class Act_Main extends SlidingFragmentActivity 
{
    private long exitTime = 0;
    private ActionBar mActionBar = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        CommonFunc.InitCommonPara(this);
        CommonFunc.InitOncePara(this);
        CommonFunc.LoadTheme();        
        setTheme(CommonPara.THEME);
        CommonFunc.InitTheme(this);
        LoadLast();
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frag_content);
            
        if(CommonPara.auto_update)
        {
            UmengUpdateAgent.setUpdateOnlyWifi(CommonPara.allow_gprs?false:true);
            UmengUpdateAgent.update(this);
        }
        
        FeedbackAgent agent = new FeedbackAgent(this);
        agent.sync();
        
        mActionBar = getSupportActionBar();        
        setBehindContentView(R.layout.frag_menu);        
        FragmentTransaction fragTrans = getSupportFragmentManager().beginTransaction();
        fragTrans.replace(R.id.frag_menu_frame, new Frag_Menu());
        fragTrans.replace(R.id.frag_content_frame,new Frag_Home());
        fragTrans.commit();

        InitSlidingMenu();        
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        MobclickAgent.onResume(this);

        if(CommonPara.always_bright)
        {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
        else
        {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
    
    @Override
    protected void onPause()
    {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    
    @Override
    protected void onDestroy()
    {
        super.onPause();
        SharedPreferences settings = getSharedPreferences(CommonPara.STORE_NAME, MODE_PRIVATE);  
        SharedPreferences.Editor editor = settings.edit();  
        editor.putInt("currentBook", CommonPara.currentBook);  
        editor.putInt("currentChapter", CommonPara.currentChapter); 
        editor.putInt("currentSection", CommonPara.currentSection); 
        editor.commit();  
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        switch (item.getItemId()) 
        {
	        case android.R.id.home:
	            toggle();
	            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) 
    {
        if(keyCode == KeyEvent.KEYCODE_BACK &&
                event.getAction() == KeyEvent.ACTION_DOWN)
        {   
            if((System.currentTimeMillis()-exitTime) > 2000)
            {  
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();                                
                exitTime = System.currentTimeMillis();   
            } 
            else 
            {
                finish();
            }
            return true;   
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressWarnings("deprecation")
    private void InitSlidingMenu() 
    {
        SlidingMenu slidingMenu = getSlidingMenu();
        //设置是左滑还是右滑，还是左右都可以滑
        slidingMenu.setMode(SlidingMenu.LEFT);
        //设置阴影宽度
        slidingMenu.setShadowWidth(20);//getWindowManager().getDefaultDisplay().getWidth() / 40);        
        //设置左菜单阴影图片
        slidingMenu.setShadowDrawable(R.drawable.shadow);
        //设置右菜单阴影图片
        //slidingMenu.setSecondaryShadowDrawable(R.drawable.right_shadow);
        //设置菜单占屏幕的比例
        slidingMenu.setBehindOffset(getWindowManager().getDefaultDisplay().getWidth() / 3);
        //设置滑动时菜单的是否淡入淡出
        slidingMenu.setFadeEnabled(false);
        //设置淡入淡出的比例
        //slidingMenu.setFadeDegree(0.6f);
        //设置滑动时拖拽效果
        slidingMenu.setBehindScrollScale(0.0f);
        //设置要使菜单滑动，触碰屏幕的范围
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        slidingMenu.invalidate();  
        
        mActionBar.setDisplayHomeAsUpEnabled(true);
    }
    
    private void LoadLast()
    {
        CommonPara.lastBook = CommonPara.currentBook;
        CommonPara.lastChapter = CommonPara.currentChapter;
        CommonPara.lastSection = CommonPara.currentSection;
    }
   
}
