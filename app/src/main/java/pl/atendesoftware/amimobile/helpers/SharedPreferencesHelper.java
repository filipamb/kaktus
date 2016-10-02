package pl.atendesoftware.amimobile.helpers;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Set;

public class SharedPreferencesHelper {
    private static final String TAG = "amimobile";
    private static final int MODE = Context.MODE_PRIVATE;

    public static final String app_user_login_key = "app-user-login";
    public static final String app_user_username_key = "app-user-username";
    public static final String app_user_name_key = "app-user-name";
    public static final String app_user_email_key = "app-user-email";

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(TAG, MODE);
    }

    private static Editor getEditor(Context context) {
        return getSharedPreferences(context).edit();
    }

    public static void putValue(Context context, String key, String value) {
        getEditor(context).putString(key, value).apply();
    }

    public static void putValue(Context context, String key, Set<String> values) {
        getEditor(context).putStringSet(key, values).apply();
    }

    public static void putValue(Context context, String key, int value) {
        getEditor(context).putInt(key, value).apply();
    }

    public static void putValue(Context context, String key, long value) {
        getEditor(context).putLong(key, value).apply();
    }

    public static void putValue(Context context, String key, float value) {
        getEditor(context).putFloat(key, value).apply();
    }

    public static void putValue(Context context, String key, boolean value) {
        getEditor(context).putBoolean(key, value).apply();
    }

    public static void remove(Context context, String key) {
        getEditor(context).remove(key).apply();
    }

    public static String getString(Context context, String key, String defValue) {
        return getSharedPreferences(context).getString(key, defValue);
    }

    public static Set<String> getStringSet(Context context, String key, Set<String> defValue) {
        return getSharedPreferences(context).getStringSet(key, defValue);
    }

    public static int getInt(Context context, String key, int defValue) {
        return getSharedPreferences(context).getInt(key, defValue);
    }

    public static long getLong(Context context, String key, long defValue) {
        return getSharedPreferences(context).getLong(key, defValue);
    }

    public static float getFloat(Context context, String key, float defValue) {
        return getSharedPreferences(context).getFloat(key, defValue);
    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        return getSharedPreferences(context).getBoolean(key, defValue);
    }

    public static boolean contains(Context context, String key) {
        return getSharedPreferences(context).contains(key);
    }
}
