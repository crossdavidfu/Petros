package com.petroschurch.petros.fragment;

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
import com.petroschurch.petros.MainActivity;
import com.petroschurch.petros.R;
import com.petroschurch.petros.adapter.BibleAdapter;
import com.petroschurch.petros.lib.CommonPara;
import com.slidingmenu.lib.SlidingMenu;

public class BibleFragment extends SherlockFragment
{    
    private MainActivity mActivity = null;
    private ViewPager mViewPager = null;  
    private ActionBar mActionBar = null;
    private FragmentManager mManager = null;
    private BibleAdapter mAdapter = null;
    
    private int lastBook = CommonPara.currentBook;
    private int lastChapter = CommonPara.currentChapter;
    
    @Override  
    public void onCreate(Bundle savedInstanceState) 
    {  
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
        mActivity = (MainActivity)getSherlockActivity();
        mActionBar = mActivity.getSupportActionBar();        
        
        // 创建Tab   
        mActionBar.removeAllTabs();
        AddBibleRead();  
        AddBibleDevote();   
    }  
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {     
        View view = inflater.inflate(R.layout.frag_bible, container, false);
        mManager = getChildFragmentManager();//mActivity.getSupportFragmentManager();
        mAdapter = new BibleAdapter(mManager);
        mViewPager = (ViewPager)view.findViewById(R.id.frag_bible_pager);                
        mViewPager.setAdapter(mAdapter);  
        mViewPager.setCurrentItem(0); 
        mViewPager.setOnPageChangeListener(pageChangeListener);
        
        return view; 
    }     

    private void AddBibleRead()
    { 
        Tab tab = mActionBar.newTab();  
        //tab.setContentDescription("Tab 0");          
        tab.setText("圣经");  
        tab.setTabListener(mTabListener);  
        mActionBar.addTab(tab);  
    }  
    
    private void AddBibleDevote()
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
                case BibleAdapter.TAB_BIBLE_READ:
                    mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    break;
                case BibleAdapter.TAB_BIBLE_DEVOTE:
                    mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
                    if((lastBook != CommonPara.currentBook) || (lastChapter != CommonPara.currentChapter))
                    { 
                        mAdapter.notifyDataSetChanged();
                        
                        lastBook = CommonPara.currentBook;
                        lastChapter = CommonPara.currentChapter; 
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
