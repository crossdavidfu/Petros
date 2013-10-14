package com.petroschurch.petros.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.ViewGroup;

import com.petroschurch.petros.fragment.QtDevoteFragment;
import com.petroschurch.petros.fragment.QtReadFragment;

import java.util.ArrayList;

public class QtAdapter extends FragmentPagerAdapter {
    public final static int TAB_QT_READ = 0;
    public final static int TAB_QT_DEVOTE = 1;
    public final static int TAB_COUNT = 2;

    private FragmentTransaction mTransaction = null;
    private FragmentManager mManager = null;

    private ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>(2);
    private QtReadFragment qt_read = null;
    private QtDevoteFragment qt_devote = null;

    public QtAdapter(FragmentManager fm) {
        super(fm);
        mManager = fm;
        qt_read = new QtReadFragment();
        qt_devote = new QtDevoteFragment();
        mFragmentList.add(TAB_QT_READ, qt_read);
        mFragmentList.add(TAB_QT_DEVOTE, qt_devote);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }

    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mTransaction == null) {
            mTransaction = mManager.beginTransaction();
        }
        Fragment fragment = mManager.findFragmentById(getId(position));
        if (fragment != null) {
            mTransaction.attach(fragment);
        } else {
            fragment = getItem(position);
            mTransaction.add(container.getId(), fragment, String.valueOf(getId(position)));
        }
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (mTransaction == null) {
            mTransaction = mManager.beginTransaction();
        }
        mTransaction.detach((Fragment) object);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mTransaction != null) {
            mTransaction.commitAllowingStateLoss();
            mTransaction = null;
            mManager.executePendingTransactions();
        }
    }

    protected int getId(int position) {
        return mFragmentList.get(position).getId();
    }

    @Override
    public int getItemPosition(Object object) {
        if (((Fragment) object) == mFragmentList.get(TAB_QT_DEVOTE)) {
            mFragmentList.remove(TAB_QT_DEVOTE);
            qt_devote = new QtDevoteFragment();
            mFragmentList.add(TAB_QT_DEVOTE, qt_devote);
            return POSITION_NONE;
        } else {
            return super.getItemPosition(object);
        }
    }


}
