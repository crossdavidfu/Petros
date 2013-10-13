package com.petroschurch.petros.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import com.petroschurch.petros.fragment.BibleDevoteFragment;
import com.petroschurch.petros.fragment.BibleReadFragment;

public class BibleAdapter extends FragmentPagerAdapter
{
    public final static int TAB_BIBLE_READ = 0; 
    public final static int TAB_BIBLE_DEVOTE = 1; 
    public final static int TAB_COUNT = 2; 
    
    private FragmentTransaction mTransaction = null;
    private FragmentManager mManager = null;
    
    private ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>(2);
    private BibleReadFragment bible_read = null;
    private BibleDevoteFragment bible_devote = null;
    
    public BibleAdapter(FragmentManager fm)
    { 
        super(fm);
        mManager = fm;
        bible_read = new BibleReadFragment();
        bible_devote = new BibleDevoteFragment();
        mFragmentList.add(TAB_BIBLE_READ, bible_read);
        mFragmentList.add(TAB_BIBLE_DEVOTE, bible_devote);        
    }
    
    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return ((Fragment) object).getView() == view;
    }
    
    public Fragment getItem(int position) 
    {  
        return mFragmentList.get(position);
    }  
  
    @Override  
    public int getCount() 
    {  
        return TAB_COUNT;  
    }
    
    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        if (mTransaction == null) 
        {
            mTransaction = mManager.beginTransaction();
        }
        Fragment fragment = mManager.findFragmentById(getId(position));
        if (fragment != null) 
        {
            mTransaction.attach(fragment);
        } 
        else
        {
            fragment = getItem(position);
            mTransaction.add(container.getId(), fragment, String.valueOf(getId(position)));
        }
        return fragment;
    }
    
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) 
    {
        if (mTransaction == null) 
        {
            mTransaction = mManager.beginTransaction();
        }
        mTransaction.detach((Fragment) object);
    }
    
    @Override
    public void finishUpdate(ViewGroup container)
    {
        if (mTransaction != null)
        {
            mTransaction.commitAllowingStateLoss();
            mTransaction = null;
            mManager.executePendingTransactions();
        }
    }
    
    protected int getId(int position)
    {
        return mFragmentList.get(position).getId();
    }
    
    @Override
    public int getItemPosition(Object object) 
    {
        if(((Fragment)object) == mFragmentList.get(TAB_BIBLE_DEVOTE))
        {
            mFragmentList.remove(TAB_BIBLE_DEVOTE);
            bible_devote = new BibleDevoteFragment();
            mFragmentList.add(TAB_BIBLE_DEVOTE, bible_devote);
            return POSITION_NONE;
        }
        else
        {
            return super.getItemPosition(object);
        }
    }    

}
