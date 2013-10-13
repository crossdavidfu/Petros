package com.petroschurch.petros.activity;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.petroschurch.petros.MainActivity;
import com.petroschurch.petros.R;
import com.petroschurch.petros.lib.*;
import com.umeng.fb.FeedbackAgent;
import com.umeng.socialize.controller.RequestType;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceScreen;

public class SettingActivity extends SherlockPreferenceActivity implements OnPreferenceChangeListener
{
    private PreferenceScreen click_about;
    private PreferenceScreen click_share;
    private PreferenceScreen click_feedback;
    private CheckBoxPreference[] check_bible_version = new CheckBoxPreference[CommonPara.VERSION_NUMBER];
    private ListPreference list_font_size;
    private CheckBoxPreference check_always_bright;
    private CheckBoxPreference check_theme_black;
    private CheckBoxPreference check_auto_update;
    private CheckBoxPreference check_show_color;
    private CheckBoxPreference check_allow_gprs;
    private ListPreference list_mp3_ver;
    private Intent intent = null;
    
    private boolean theme;
    
    @SuppressWarnings("deprecation")
    @Override  
    public void onCreate(Bundle savedInstanceState) 
    {  
        setTheme(CommonPara.THEME);
        super.onCreate(savedInstanceState);  
        addPreferencesFromResource(R.layout.act_setting);
        
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        intent = new Intent(this, MainActivity.class);
        setResult(CommonPara.NOT_NEED_RESTART, intent);

        for(int i=0;i<CommonPara.VERSION_NUMBER;i++)
        {
            check_bible_version[i] = (CheckBoxPreference)findPreference("bible_version_"+i);
            check_bible_version[i].setOnPreferenceChangeListener(this);     
        }
        
        click_about = (PreferenceScreen)findPreference("click_about");
        click_share = (PreferenceScreen)findPreference("click_share");
        click_share.setOnPreferenceChangeListener(this);   
        click_feedback = (PreferenceScreen)findPreference("click_feedback");
        click_feedback.setOnPreferenceChangeListener(this);    
        list_font_size = (ListPreference)findPreference("font_size");
        list_font_size.setOnPreferenceChangeListener(this);
        check_always_bright = (CheckBoxPreference)findPreference("always_bright");
        check_always_bright.setOnPreferenceChangeListener(this);
        check_theme_black = (CheckBoxPreference)findPreference("theme_black");
        check_theme_black.setOnPreferenceChangeListener(this);
        check_auto_update = (CheckBoxPreference)findPreference("auto_update");
        check_auto_update.setOnPreferenceChangeListener(this);
        check_show_color = (CheckBoxPreference)findPreference("show_color");
        check_show_color.setOnPreferenceChangeListener(this);
        check_allow_gprs = (CheckBoxPreference)findPreference("allow_gprs");
        check_allow_gprs.setOnPreferenceChangeListener(this);
        list_mp3_ver = (ListPreference)findPreference("mp3_ver");
        list_mp3_ver.setOnPreferenceChangeListener(this);
        
        theme = CommonPara.theme_black;
        
        click_about.setSummary("软件版本" + "  " + CommonFunc.GetVerName(this));
    }  
       
    //@SuppressLint("SimpleDateFormat")
    @SuppressWarnings("deprecation")
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) 
    {
        if(preference == click_feedback)
        {      
            FeedbackAgent agent = new FeedbackAgent(this);
            agent.startFeedbackActivity();
        }
        else if(preference == click_share)
        {
            UMSocialService umService = UMServiceFactory.getUMSocialService("PetrosChurch", RequestType.SOCIAL);
            umService.openUserCenter(this);
        }
        
        return super.onPreferenceTreeClick(preferenceScreen, preference);  
    }  
    
    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) 
    {          
        SharedPreferences settings = getSharedPreferences(CommonPara.STORE_NAME, MODE_PRIVATE);  
        SharedPreferences.Editor editor = settings.edit();  
        for(int i=0;i<CommonPara.VERSION_NUMBER;i++)
        {
            if (preference == check_bible_version[i])
            {  
                editor.putBoolean("bible_version_"+i, (Boolean)objValue); 
                break;
            }
        }
        
        if (preference == list_font_size)
        {
            editor.putFloat("font_size", Float.valueOf(objValue.toString())); 
        }
        else if(preference == check_always_bright)
        {
            editor.putBoolean("always_bright", (Boolean)objValue); 
        }
        else if(preference == check_theme_black)
        {
            editor.putBoolean("theme_black", (Boolean)objValue); 
        }
        else if(preference == check_auto_update)
        {
            editor.putBoolean("auto_update", (Boolean)objValue); 
        }
        else if(preference == check_show_color)
        {
            editor.putBoolean("show_color", (Boolean)objValue);
        }
        else if(preference == check_allow_gprs)
        {
            editor.putBoolean("allow_gprs", (Boolean)objValue);
        }
        if (preference == list_mp3_ver)
        {
            editor.putInt("mp3_ver", Integer.valueOf(objValue.toString())); 
        }
        
        editor.commit();  
        
        CommonFunc.InitCommonPara(this);
        
        if(theme != CommonPara.theme_black)
        {
            setResult(CommonPara.NEED_RESTART, intent);
        }
        else
        {
            setResult(CommonPara.NOT_NEED_RESTART, intent);
        }
        return true;
    } 
}
