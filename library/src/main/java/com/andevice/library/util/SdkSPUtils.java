package com.andevice.library.util;

import android.content.Context;
import android.content.SharedPreferences;


public class SdkSPUtils {
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedpreferences;

    public static final String PREFERENCES = "track_me";
    public static final String REGISTERED_OTP = "registered_otp";
    public static final String USER_NAME = "user_name";
    public static final String TOKEN = "token";
    public static final String USER_KEY = "userKey";
    public static final String USER_ID = "user_id";
    public static final String DEVCIE_ID = "device_id";
    public static final String DEVCIE_NAME = "device_name";
    public static final String ACCESS_KEY = "access_key";
    public static final String ROLES_NAME = "roles_name";
    public static final String ROLES_ID = "roles_id";

    //Device registration configuration parameters
    public static final String GATEWAY_ID = "gateway_id";
    public static final String PROTOCOL = "protocol";

    public static final String API_END_POINT = "api_end_point";
    public static final String API_SERVICE = "api_service";

    public static final String LOCATION_UPDATE_INTERVAL = "location_update_interval";
    public static final String LOCATION_LATITUDE = "location_latitude";
    public static final String LOCATION_LONGITUDE = "location_longitude";
    public static final String IS_LCOATION_SERVICE_RUNNING = "is_location_service_running";

    public static final String SERVICE_EXECUTE_MODE = "servcie_execute_mode";

    public static String IS_REPEATING_ALARM_SET = "is_repeating_alarm_set";



    public SdkSPUtils(Context context) {
        if (context != null)
            sharedpreferences = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
    }

    public String getString(String tag) {
        return sharedpreferences.getString(tag, "");
    }

    public Boolean getBoolean(String tag) {
        return sharedpreferences.getBoolean(tag, false);
    }

    public String getDate(String tag) {
        return sharedpreferences.getString(tag, "1 Jan 2015 00:00:00");
    }


    public int getInt(String tag) {
        return sharedpreferences.getInt(tag, 0);
    }

    public float getFloat(String tag) {
        return sharedpreferences.getFloat(tag, 0);
    }

    public void setValue(String tag, String value) {
        editor = sharedpreferences.edit();
        editor.putString(tag, value);
        editor.apply();
    }

    public void setValue(String tag, boolean value) {
        editor = sharedpreferences.edit();
        editor.putBoolean(tag, value);
        editor.apply();
    }

    public void setValue(String tag, int value) {
        editor = sharedpreferences.edit();
        editor.putInt(tag, value);
        editor.apply();
    }

    public void setValue(String tag, float value) {
        editor = sharedpreferences.edit();
        editor.putFloat(tag, value);
        editor.apply();
    }

    public void clearPreferences() {
        editor = sharedpreferences.edit();
        editor.clear();
        editor.apply();
    }

    public void set_API_End_Point(String value) {
        editor = sharedpreferences.edit();
        editor.putString(API_END_POINT, value);
        editor.apply();
    }
    public String get_API_End_Point() {

        return sharedpreferences.getString(API_END_POINT, "");
    }

}