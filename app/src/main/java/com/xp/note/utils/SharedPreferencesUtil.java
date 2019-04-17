package com.xp.note.utils;

import android.content.Context;
import android.content.SharedPreferences;



public class SharedPreferencesUtil {

    private final static String USER_INFO = "user_info";
    private final static String SETTING_INFO = "setting_info";

    private final static String  IS_LOGIN = "is_login";
    private final static String  USER_NAME = "user_name";
    private final static String  PASSWORD = "password";


    private static Context context;

    public static void init(Context context) {
        SharedPreferencesUtil.context = context;
    }


    //是否登录
    public static void setIsLogin(boolean setIsLogin){
        SharedPreferences.Editor editor = context.getSharedPreferences(SETTING_INFO, Context.MODE_PRIVATE).edit();
        editor.putBoolean(SharedPreferencesUtil.IS_LOGIN, setIsLogin);
        editor.apply();
    }

    public static boolean getIsLogin(){
        SharedPreferences preferences = context.getSharedPreferences(SETTING_INFO, Context.MODE_PRIVATE);
        return preferences.getBoolean(SharedPreferencesUtil.IS_LOGIN,false);
    }

    //账号
    public static void setUsername(String username) {
        SharedPreferences.Editor editor = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).edit();
        editor.putString(SharedPreferencesUtil.USER_NAME, username);
        editor.apply();
    }

    public static String getUsername() {
        SharedPreferences preferences = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
        return preferences.getString(SharedPreferencesUtil.USER_NAME, "");
    }


    //密码
    public static void setPassword(String psw) {
        SharedPreferences.Editor editor = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE).edit();
        editor.putString(SharedPreferencesUtil.PASSWORD, psw);
        editor.apply();
    }

    public static String getPassword() {
        SharedPreferences preferences = context.getSharedPreferences(USER_INFO, Context.MODE_PRIVATE);
        return preferences.getString(SharedPreferencesUtil.PASSWORD, "");
    }





}
