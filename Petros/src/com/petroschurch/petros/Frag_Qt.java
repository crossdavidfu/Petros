package com.petroschurch.petros;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragment;
import com.petroschurch.petros.lib.CommonPara;
import com.slidingmenu.lib.SlidingMenu;

public class Frag_Qt extends SherlockFragment
{    
    private Act_Main mActivity = null;
    private ViewPager mViewPager = null;  
    private ActionBar mActionBar = null;
    private FragmentManager mManager = null;
    private Adap_Qt mAdapter = null;
    
    private int lastYear = CommonPara.currentYear;
    private int lastMonth = CommonPara.currentMonth;
    private int lastDay = CommonPara.currentDay;
    
    @Override  
    public void onCreate(Bundle savedInstanceState) 
    {  
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
        mActivity = (Act_Main)getSherlockActivity();
        mActionBar = mActivity.getSupportActionBar();        
        
        // 创建Tab   
        mActionBar.removeAllTabs();
        AddQtRead();  
        AddQtDevote();   
    }  
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {     
        View view = inflater.inflate(R.layout.frag_qt, container, false); 
        mManager = getChildFragmentManager();
        mAdapter = new Adap_Qt(mManager);
        mViewPager = (ViewPager)view.findViewById(R.id.frag_qt_pager);                
        mViewPager.setAdapter(mAdapter);  
        mViewPager.setCurrentItem(0); 
        mViewPager.setOnPageChangeListener(pageChangeListener);
        
        return view; 
    }         
  
    private void AddQtRead()
    { 
        Tab tab = mActionBar.newTab();  
        //tab.setContentDescription("Tab 0");  
        tab.setText("圣经");  
        tab.setTabListener(mTabListener);  
        mActionBar.addTab(tab);  
    }  
    
    private void AddQtDevote()
    {  
        Tab tab = mActionBar.newTab();  
        //tab.setContentDescription("Tab 1");  
        tab.setText("灵修");  
        tab.setTabListener(mTabListener);  
        mActionBar.addTab(tab);  
    }  
  
    private final TabListener mTabListener = new TabListener() 
    {  
        @Override  
        public void onTabReselected(Tab tab, FragmentTransaction ft)
        {  
            
        }  
  
        @Override  
        public void onTabSelected(Tab tab, FragmentTransaction ft) 
        {  
            if (mViewPager != null)  
                mViewPager.setCurrentItem(tab.getPosition());  
        }  
  
        @Override  
        public void onTabUnselected(Tab tab, FragmentTransaction ft)
        {  

        }  
    };  
    
    private OnPageChangeListener pageChangeListener = new OnPageChangeListener() 
    {
        @Override
        public void onPageSelected(int arg0) 
        {
            mActionBar.setSelectedNavigationItem(arg0);
            switch (arg0) 
            {
                case Adap_Qt.TAB_QT_READ:
                    mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    break;
                case Adap_Qt.TAB_QT_DEVOTE:
                    mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
                    if((lastYear != CommonPara.currentYear) 
                    || (lastMonth != CommonPara.currentMonth)
                    || (lastDay != CommonPara.currentDay))
                    { 
                        mAdapter.notifyDataSetChanged();
                        
                        lastYear = CommonPara.currentYear;
                        lastMonth = CommonPara.currentMonth; 
                        lastDay = CommonPara.currentDay; 
                    }                    
                    break;
                default:
                    mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
                    break;
            }
        }
        
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2)
        {
            
        }
        
        @Override
        public void onPageScrollStateChanged(int arg0) 
        {
            
        }
    };
}
