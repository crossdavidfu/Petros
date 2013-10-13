package com.petroschurch.petros.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.petroschurch.petros.activity.SettingActivity;
import com.petroschurch.petros.lib.CommonPara;
import com.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MenuFragment extends SherlockFragment implements OnItemClickListener {
    private MainActivity mActivity = null;
    private ActionBar mActionBar = null;
    private FragmentManager mManager = null;
    private Fragment fragment = null;
    private ListView list_menu = null;

    private static final int INTENT_SET = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setRetainInstance(true);
        mActivity = (MainActivity) getSherlockActivity();
        mActionBar = mActivity.getSupportActionBar();
        mManager = mActivity.getSupportFragmentManager();
        CommonPara.menuIndex = CommonPara.MENU_HOME;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_menu, container, false);
        list_menu = (ListView) view.findViewById(R.id.frag_menu_list);
        list_menu.setOnItemClickListener(this);
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        list_menu.setDivider(null);
        for (int i = 0; i < CommonPara.menuName.length; i++) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("img", Integer.toString(R.drawable.menu_img));
            map.put("text", CommonPara.menuName[i]);
            list.add(map);
        }

        String[] from = {"img", "text"};
        int[] to = {R.id.list_menu_img_img, R.id.list_menu_img_text};

        SimpleAdapter adapter = new SimpleAdapter(mActivity, list, R.layout.list_menu_img, from, to);
        list_menu.setAdapter(adapter);
        return view;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case CommonPara.MENU_HOME:
                if (CommonPara.menuIndex != CommonPara.MENU_HOME) {
                    CommonPara.menuIndex = CommonPara.MENU_HOME;
                    mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                    mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    FragmentTransaction fragTrans = mManager.beginTransaction();
                    fragment = new HomeFragment();
                    fragTrans.replace(R.id.frag_content_frame, fragment);
                    fragTrans.commit();
                }
                break;
            case CommonPara.MENU_BIBLE:
                if (CommonPara.menuIndex != CommonPara.MENU_BIBLE) {
                    CommonPara.menuIndex = CommonPara.MENU_BIBLE;
                    mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                    mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    FragmentTransaction fragTrans = mManager.beginTransaction();
                    fragment = new BibleFragment();
                    fragTrans.replace(R.id.frag_content_frame, fragment);
                    fragTrans.commit();
                }
                break;
            case CommonPara.MENU_QT:
                if (CommonPara.menuIndex != CommonPara.MENU_QT) {
                    CommonPara.menuIndex = CommonPara.MENU_QT;
                    mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
                    mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    FragmentTransaction fragTrans = mManager.beginTransaction();
                    fragment = new QtFragment();
                    fragTrans.replace(R.id.frag_content_frame, fragment);
                    fragTrans.commit();
                }
                break;
            case CommonPara.MENU_MARK:
                if (CommonPara.menuIndex != CommonPara.MENU_MARK) {
                    CommonPara.menuIndex = CommonPara.MENU_MARK;
                    mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                    mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    FragmentTransaction fragTrans = mManager.beginTransaction();
                    fragment = new MarkFragment();
                    fragTrans.replace(R.id.frag_content_frame, fragment);
                    fragTrans.commit();
                }
                break;
            case CommonPara.MENU_BOOK:
                if (CommonPara.menuIndex != CommonPara.MENU_BOOK) {
                    CommonPara.menuIndex = CommonPara.MENU_BOOK;
                    mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                    mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    FragmentTransaction fragTrans = mManager.beginTransaction();
                    fragment = new BookFragment();
                    fragTrans.replace(R.id.frag_content_frame, fragment);
                    fragTrans.commit();
                }
                break;
            case CommonPara.MENU_VERSE:
                if (CommonPara.menuIndex != CommonPara.MENU_VERSE) {
                    CommonPara.menuIndex = CommonPara.MENU_VERSE;
                    mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                    mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    FragmentTransaction fragTrans = mManager.beginTransaction();
                    fragment = new VerseFragment();
                    fragTrans.replace(R.id.frag_content_frame, fragment);
                    fragTrans.commit();
                }
                break;
            case CommonPara.MENU_SEARCH:
                if (CommonPara.menuIndex != CommonPara.MENU_SEARCH) {
                    CommonPara.menuIndex = CommonPara.MENU_SEARCH;
                    mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                    mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    FragmentTransaction fragTrans = mManager.beginTransaction();
                    fragment = new SearchFragment();
                    fragTrans.replace(R.id.frag_content_frame, fragment);
                    fragTrans.commit();
                }
                break;
            case CommonPara.MENU_SITE:
                if (CommonPara.menuIndex != CommonPara.MENU_SITE) {
                    CommonPara.menuIndex = CommonPara.MENU_SITE;
                    mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                    mActivity.getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    FragmentTransaction fragTrans = mManager.beginTransaction();
                    fragment = new SiteFragment();
                    fragTrans.replace(R.id.frag_content_frame, fragment);
                    fragTrans.commit();
                }
                break;
            case CommonPara.MENU_SET:
                Intent intent;
                intent = new Intent();
                intent.setClass(mActivity, SettingActivity.class);
                startActivityForResult(intent, INTENT_SET);
                break;
        }

        mActivity.getSlidingMenu().toggle();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case INTENT_SET:
                if (resultCode == CommonPara.NEED_RESTART) {
                    Reload();
                }
                break;
        }
    }

    protected void Reload() {
        Intent intent = mActivity.getIntent();
        mActivity.overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        mActivity.finish();
        mActivity.overridePendingTransition(0, 0);
        startActivity(intent);
    }

}
