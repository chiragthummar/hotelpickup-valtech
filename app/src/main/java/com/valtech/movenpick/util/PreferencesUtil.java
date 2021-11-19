package com.valtech.movenpick.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PreferencesUtil {


    private static SharedPreferences getDefaultPrefs(Context c) {

        return PreferenceManager.getDefaultSharedPreferences(c);
    }


    public static boolean contains(Context context, String key) {
        SharedPreferences prefs = getDefaultPrefs(context);
        return prefs.contains(key);
    }


    public static void remove(Context ctx, String key) {
        SharedPreferences prefs = getDefaultPrefs(ctx);
        prefs.edit().remove(key).commit();
    }

    public static String getString(Context ctx, String key, String defaultValue) {
        SharedPreferences prefs = getDefaultPrefs(ctx);
        return prefs.getString(key, defaultValue);
    }

    public static int getInt(Context ctx, String key, int defaultValue) {
        SharedPreferences prefs = getDefaultPrefs(ctx);
        return prefs.getInt(key, defaultValue);
    }

    public static boolean putInt(Context ctx, String key, int value) {
        SharedPreferences prefs = getDefaultPrefs(ctx);
        return prefs.edit().putInt(key, value).commit();
    }


    public static long getLong(Context ctx, String key, int defaultValue) {
        SharedPreferences prefs = getDefaultPrefs(ctx);
        return prefs.getLong(key, defaultValue);
    }

    public static boolean putLong(Context ctx, String key, long value) {
        SharedPreferences prefs = getDefaultPrefs(ctx);
        return prefs.edit().putLong(key, value).commit();
    }


    public static boolean putString(Context ctx, String key, String value) {
        SharedPreferences prefs = getDefaultPrefs(ctx);
        return prefs.edit().putString(key, value).commit();
    }

    public static boolean getBool(Context ctx, String key, boolean defaultValue) {
        SharedPreferences prefs = getDefaultPrefs(ctx);
        return prefs.getBoolean(key, defaultValue);
    }

    public static boolean putBool(Context ctx, String key, boolean value) {
        SharedPreferences prefs = getDefaultPrefs(ctx);
        return prefs.edit().putBoolean(key, value).commit();
    }

}
