package com.petroschurch.petros.lib;

import android.graphics.Color;

import com.petroschurch.petros.R;

public class CommonPara {
    public static final String SD_PATH = "";
    public static final String DB_CONTENT_ASSET = "petros";
    public static final String DB_CONTENT_PATH = "sdcard/petroschurch/petros/content/";
    public static final String DB_CONTENT_NAME = "petros.db";
    public static final int DB_CONTENT_VER = 7;

    public static final String DB_DATA_PATH = "sdcard/petroschurch/petros/data/";
    public static final String DB_DATA_NAME = "data.db";
    public static final int DB_DATA_VER = 1;

    public static final int VERSE_NUMBER = 473;

    public static final String DB_BAK_PATH = "sdcard/petroschurch/petros/bak/";

    public static final String BIBLE_MP3_PATH = "sdcard/petroschurch/petros/bible/mp3/";

    public static final String STORE_NAME = "Settings";

    public static final int VERSION_NUMBER = 6;
    public static final String[] VERSION_TABLE =
            {"nuv", "ncb", "tcb", "niv", "nrsv", "kjv"};

    public static final String[] MP3_VER_TABLE =
            {"", "nuv", "niv"};

    public static final int DB_CONTENT_COUNT = 100;

    public static int DEFAULT_TEXT_COLOR = Color.BLACK;
    public static int HIGHLIGHT_TEXT_COLOR = Color.BLUE;
    public static int THEME = R.style.Theme_Sherlock;

    protected static final int PROGRESS_MAX = 100;

    public static final String UPDATE_URL = "http://www.petroschurch.com/soft/petros/update.json";
    public static final String APP_NAME = "petros";
    public static final String DOWN_NAME = "petros_update.apk";

    public static final String FORUM_URL = "http://www.petroschurch.com/";

    public static final String BIBLE_MP3_URL = "http://www.petroschurch.com/down/bible/mp3/";

    public static boolean[] bibleVersion = new boolean[VERSION_NUMBER];
    public static Float font_size;
    public static boolean always_bright;
    public static boolean theme_black;
    public static boolean full_screen;
    public static boolean auto_update;
    public static boolean show_color;
    public static boolean allow_gprs;
    public static int mp3_ver;

    public static int currentBook;
    public static int currentChapter;
    public static int currentSection;
    public static int currentCount;
    public static int previousCount;

    public static int lastBook;
    public static int lastChapter;
    public static int lastSection;

    public static int currentYear;
    public static int currentMonth;
    public static int currentDay;

    public static int todayYear;
    public static int todayMonth;
    public static int todayDay;

    public static int bookYear;
    public static int bookMonth;
    public static int bookDay;
    public static int bookType = 0;
    public static final String[] BOOK_TABLE =
            {"sod", "fsp", "utmost"};
    public static final String[] BOOK_NAME =
            {"荒漠甘泉", "花香满径", "竭诚为主"};

    public static int qtBook;
    public static int qtSChapter;
    public static int qtSSection;
    public static int qtEChapter;
    public static int qtESection;

    public static int bibleDevitionPos = 0;
    public static int qtReaderPos = 0;
    public static int qtDevitionPos = 0;
    public static int bookmarkPos = 0;
    public static int searchPos = 0;
    public static int bibleMp3Pos = 0;

    public static int menuIndex = 0;
    public static final String[] menuName =
            {"主页", "圣经", "QT", "书签", "阅读", "金句", "搜索", "论坛", "设置"};
    public static final int MENU_HOME = 0;
    public static final int MENU_BIBLE = 1;
    public static final int MENU_QT = 2;
    public static final int MENU_MARK = 3;
    public static final int MENU_BOOK = 4;
    public static final int MENU_VERSE = 5;
    public static final int MENU_SEARCH = 6;
    public static final int MENU_SITE = 7;
    public static final int MENU_SET = 8;

    public static final int NEED_RESTART = 1;
    public static final int NOT_NEED_RESTART = 0;

    public static final String[] VERSE_CHOISE = {"添加查看书签", "复制到剪贴板", "分享到网络"};
    public static final int VERSE_CHOISE_MARK = 0;
    public static final int VERSE_CHOISE_COPY = 1;
    public static final int VERSE_CHOISE_SHARE = 2;
}
