package com.petroschurch.petros.lib;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.petroschurch.petros.R;

import java.util.Calendar;

public class CommonFunc {
    public static boolean isWifi(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

    public static void InitCommonPara(Context context) {
        SharedPreferences settings = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        Calendar calendar = Calendar.getInstance();
        CommonPara.todayYear = calendar.get(Calendar.YEAR);
        CommonPara.todayMonth = calendar.get(Calendar.MONTH) + 1;
        CommonPara.todayDay = calendar.get(Calendar.DAY_OF_MONTH);

        for (int i = 0; i < CommonPara.VERSION_NUMBER; i++) {
            CommonPara.bibleVersion[i] = settings.getBoolean("bible_version_" + i, (i == 0) || (i == 3) ? true : false);
            editor.putBoolean("bible_version_" + i, CommonPara.bibleVersion[i]);
        }
        CommonPara.font_size = settings.getFloat("font_size", 18);
        editor.putFloat("font_size", CommonPara.font_size);
        CommonPara.always_bright = settings.getBoolean("always_bright", false);
        editor.putBoolean("always_bright", CommonPara.always_bright);
        CommonPara.theme_black = settings.getBoolean("theme_black", false);
        editor.putBoolean("theme_black", CommonPara.theme_black);
        CommonPara.auto_update = settings.getBoolean("auto_update", true);
        editor.putBoolean("auto_update", CommonPara.auto_update);
        CommonPara.show_color = settings.getBoolean("show_color", true);
        editor.putBoolean("show_color", CommonPara.show_color);
        CommonPara.allow_gprs = settings.getBoolean("allow_gprs", false);
        editor.putBoolean("allow_gprs", CommonPara.allow_gprs);
        CommonPara.mp3_ver = settings.getInt("mp3_ver", 1);
        editor.putInt("mp3_ver", CommonPara.mp3_ver);
        editor.commit();
    }

    public static void LoadTheme() {
        CommonPara.THEME = CommonPara.theme_black ? R.style.Theme_Sherlock : R.style.Theme_Sherlock_Light;
    }

    public static void InitTheme(Context context) {
        CommonPara.DEFAULT_TEXT_COLOR
                = context.getTheme().obtainStyledAttributes(CommonPara.THEME, new int[]{android.R.attr.textColorSecondary})
                .getColor(0, CommonPara.theme_black ? Color.WHITE : Color.BLACK);
        CommonPara.HIGHLIGHT_TEXT_COLOR = CommonPara.theme_black ? Color.YELLOW : Color.BLUE;
    }

    public static void InitOncePara(Context context) {
        SharedPreferences settings = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);

        CommonPara.currentBook = settings.getInt("currentBook", 1);
        CommonPara.currentChapter = settings.getInt("currentChapter", 1);
        CommonPara.currentSection = settings.getInt("currentSection", 1);

        CommonPara.currentYear = CommonPara.todayYear;
        CommonPara.currentMonth = CommonPara.todayMonth;
        CommonPara.currentDay = CommonPara.todayDay;

        CommonPara.bookYear = CommonPara.todayYear;
        CommonPara.bookMonth = CommonPara.todayMonth;
        CommonPara.bookDay = CommonPara.todayDay;
    }

    public static String GetVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {

        }
        return verName;
    }
}
