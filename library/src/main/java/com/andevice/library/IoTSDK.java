package com.andevice.library;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.telephony.TelephonyManager;


import com.andevice.library.controller.RESTClient;
import com.andevice.library.exception.InvalidRequestParametersException;
import com.andevice.library.exception.UnAuthorizedAccess;
import com.andevice.library.receivers.LocationUpdateReceiver;
import com.andevice.library.receivers.SdkWakefulBroadcastReceiver;
import com.andevice.library.service.LocationTrackingServcie;
import com.andevice.library.util.PermissionsUtils;
import com.andevice.library.util.SdkSPUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import dmax.dialog.SpotsDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by arun.singh on 1/19/2017.
 * This is main Interface for user to access sdk services.
 */

public class IoTSDK {

    public static String SERVICE_EXECUTE_MODE_ALWAYS = "always_run";
    public static String SERVICE_EXECUTE_MODE_ONLY_FORGROUND = "run_only_in_forground";
    public static String DEVICE_PARAMETERS_PHONE = "device_parameters_phone";
    public static String DEVICE_PARAMETERS_NETWORKS = "device_parameters_network";
    public static String DEVICE_PARAMETERS_TRAFIC = "device_parameters_trafic";
    private static IoTSDK instance;
    private String api_end_point = "http://180.149.46.100:7878";
    private Context context;

    /*Location tracking Service variables*/
    private boolean is_location_tracking_enabled = false;
    private int locationUpdateInterval = 20;
    private String service_execute_mode;

    /* Device/App data API service variables*/
    private int device_data_api_update_interval = 30;

    private ArrayList<String> devcieParamsListToTrack = new ArrayList<>();
    //Progress dialog
    private AlertDialog dialog;
    private Call call;

    private IoTSDK(Context context) {
        this.context = context;
        this.service_execute_mode = SERVICE_EXECUTE_MODE_ALWAYS;

        devcieParamsListToTrack.add(DEVICE_PARAMETERS_PHONE);
        devcieParamsListToTrack.add(DEVICE_PARAMETERS_NETWORKS);
        devcieParamsListToTrack.add(DEVICE_PARAMETERS_TRAFIC);

        //API configuration
        SdkSPUtils sdkSPUtils = new SdkSPUtils(context);
        sdkSPUtils.setValue(SdkSPUtils.API_END_POINT, api_end_point);
        sdkSPUtils.setValue(SdkSPUtils.GATEWAY_ID, "3");
        sdkSPUtils.setValue(SdkSPUtils.PROTOCOL, "https");


    }

    public static IoTSDK getInstance(Context context) {
        instance = new IoTSDK(context);
        return instance;
    }

    public int getLocationUpdateInterval() {
        return locationUpdateInterval;
    }

    public void setLocationUpdateInterval(int locationUpdateInterval) {
        this.locationUpdateInterval = locationUpdateInterval;
    }


    public void setAPI_END_POINT(String api_end_point) {
        this.api_end_point = api_end_point;
    }


    /*Service Execute mode setter getter*/
    public void setLocationTrackingServiceExecuteMode(String execute_mode) {
        service_execute_mode = execute_mode;
    }

    public String getLocationTrackingServcieExecuteMode() {
        return service_execute_mode;
    }

    public boolean isLocationTrackingEnabled() {
        return is_location_tracking_enabled;
    }

    public void setLocationTrackingEnabled(boolean is_location_tracking_enabled) {
        this.is_location_tracking_enabled = is_location_tracking_enabled;
    }

    public int getDevceiDataAPIUpdateInterval() {
        return device_data_api_update_interval;
    }

    public void setDevceiDataAPIUpdateInterval(int device_data_api_update_interval) {
        this.device_data_api_update_interval = device_data_api_update_interval;
    }

    public void setDeviceParametersToTrack(String... params) {

        devcieParamsListToTrack = new ArrayList<>();
        for (String param : params) {
            devcieParamsListToTrack.add(param);
        }
    }

    public void setGatewayID(String gateway_id) {
        new SdkSPUtils(context).setValue(SdkSPUtils.GATEWAY_ID, gateway_id);
    }

    public String getGatewayId() {
        return new SdkSPUtils(context).getString(SdkSPUtils.GATEWAY_ID);
    }

    public void setProtocol(String protocol) {
        new SdkSPUtils(context).setValue(SdkSPUtils.PROTOCOL, protocol);
    }

    public String protocol() {
        return new SdkSPUtils(context).getString(SdkSPUtils.PROTOCOL);
    }

    public void requestPermission(Activity activity) {
        new PermissionsUtils().requestForPermission(activity);
    }

    /*Method to initialize sdk services*/
    public void initService() throws UnAuthorizedAccess {

        SdkSPUtils sdkSPUtils = new SdkSPUtils(context);

        if (sdkSPUtils.getString(SdkSPUtils.TOKEN).isEmpty() || sdkSPUtils.getString(SdkSPUtils.ACCESS_KEY).isEmpty())
            throw new UnAuthorizedAccess("Access Token or Access Key is not found. Login to get these credential");


        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            /*IF Device gps is off ,alert user to turn it on*/
            buildAlertMessageNoGps((Activity) context);
        } else {

            //save module configuration set by user
            sdkSPUtils.setValue(SdkSPUtils.LOCATION_UPDATE_INTERVAL, locationUpdateInterval);
            sdkSPUtils.setValue(SdkSPUtils.API_END_POINT, api_end_point);
            sdkSPUtils.setValue(SdkSPUtils.SERVICE_EXECUTE_MODE, service_execute_mode);

            sdkSPUtils.setValue(IoTSDK.DEVICE_PARAMETERS_PHONE, false);
            sdkSPUtils.setValue(IoTSDK.DEVICE_PARAMETERS_NETWORKS, false);
            sdkSPUtils.setValue(IoTSDK.DEVICE_PARAMETERS_TRAFIC, false);
            for (String param : devcieParamsListToTrack) {
                sdkSPUtils.setValue(param, true);
            }


            //Do not start Location Tracking service if user has set it diabled. by default it is enabled
            if (is_location_tracking_enabled) {

                //Start Location Tracking if only it is not running already
                if (!new SdkSPUtils(context).getBoolean(SdkSPUtils.IS_LCOATION_SERVICE_RUNNING)) {
                    Intent locationServiceIntent = new Intent(context, LocationTrackingServcie.class);
                    locationServiceIntent.setAction(LocationTrackingServcie.START_FOREGROUND_SERVICE);
                    context.startService(locationServiceIntent);
                }
            }

            //Configure Alarm(It invokes device/app data api continuosly at each predefined interval)
            //If already configured,skip it.
            if (!sdkSPUtils.getBoolean(SdkSPUtils.IS_REPEATING_ALARM_SET)) {
                AlarmManager alarmMgr = (AlarmManager) context.getApplicationContext().getSystemService(ALARM_SERVICE);
                Intent receiverIntent = new Intent(context.getApplicationContext(), SdkWakefulBroadcastReceiver.class);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, receiverIntent, 0);
                alarmMgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        1000 * device_data_api_update_interval,
                        1000 * device_data_api_update_interval, alarmIntent);

                sdkSPUtils.setValue(SdkSPUtils.IS_REPEATING_ALARM_SET, true);
            }

        }
    }

    /*Alert message if device GPS is OFF*/
    private void buildAlertMessageNoGps(final Activity activity) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setCancelable(false);
        builder.setTitle("Location Service Disabled");
        builder.setMessage("Please enable location services.")
                .setCancelable(false)
                .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    /*User can stop sdk servcie by calling this method*/
    public void stopIoTServcies() {
        //Stop Location Traking Service
        new SdkSPUtils(context).setValue(SdkSPUtils.IS_LCOATION_SERVICE_RUNNING, false);
        Intent locationServiceIntent = new Intent(context, LocationTrackingServcie.class);
        context.stopService(locationServiceIntent);
        //Cancel Registerd Alarm so no more Data API invokation occur.
        cancelRepeatingAlarm();
    }

    /*For canceling alarm mangaer*/
    private void cancelRepeatingAlarm() {
        Intent receiverIntent = new Intent(context, SdkWakefulBroadcastReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, 0, receiverIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(alarmIntent);
        SdkSPUtils sdkSPUtils = new SdkSPUtils(context);
        sdkSPUtils.setValue(SdkSPUtils.IS_REPEATING_ALARM_SET, false);
    }

    /*Logout - Client App has to call this method while log-out from app*/
    public void logout() {
        stopIoTServcies();
        new SdkSPUtils(context).clearPreferences();
        instance = null;
    }


    /*Login method */
    public void login(final Activity activity, String username, String password, final IUserAuthorizationCallback iUserAuthorizationCallback) throws InvalidRequestParametersException {


        if (activity == null)
            throw new InvalidRequestParametersException("Activity instance can not be null");
        else if (username == null || username.isEmpty())
            throw new InvalidRequestParametersException("Username can not be empty");
        else if (password == null || password.isEmpty())
            throw new InvalidRequestParametersException("Password can not be empty");
        else if (iUserAuthorizationCallback == null)
            throw new InvalidRequestParametersException("IUserAuthorizationCallback can not be null");

        //call auth Api async
        dialog = new SpotsDialog(activity);
        dialog.setCancelable(false);
        dialog.show();

        String body = "username=" + username +
                "&password=" + password +
                "&applicationid=" + RESTClient.APP_ID;
        call = RESTClient.Login(activity, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                System.out.println("Auth onFailure");
                dialog.dismiss();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iUserAuthorizationCallback.onFailure("Auth onFailure");
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {

                        String res = response.body().string();
                        System.out.println("Auth Success " + res);

                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.has("valid")) {
                            String valid = jsonObject.getString("valid");
                            if (valid.equalsIgnoreCase("true")) {

                                JSONObject jsonObject1 = jsonObject.getJSONObject("object");
                                String access_token = jsonObject1.getString("access_token");
                                String message = jsonObject1.getString("message");
                                String userKey = jsonObject1.getString("userKey");
                                String user_id = jsonObject1.getString("user_id");
                                String access_key = jsonObject1.getString("access_key");
                                String roles_name = jsonObject1.getString("roles_name");
                                String roles_id = jsonObject1.getString("roles_id");

                                SdkSPUtils sdkSPUtils = new SdkSPUtils(context);
                                sdkSPUtils.setValue(SdkSPUtils.TOKEN, access_token);
                                sdkSPUtils.setValue(SdkSPUtils.USER_KEY, userKey);
                                sdkSPUtils.setValue(SdkSPUtils.USER_ID, user_id);
                                sdkSPUtils.setValue(SdkSPUtils.ACCESS_KEY, access_key);
                                sdkSPUtils.setValue(SdkSPUtils.ROLES_NAME, roles_name);
                                sdkSPUtils.setValue(SdkSPUtils.ROLES_ID, roles_id);
                                sdkSPUtils.setValue(SdkSPUtils.DEVCIE_NAME, ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId() + "_PLATFORM_SDK");

                                registerDeviceOnPlatform(activity, iUserAuthorizationCallback);
                            }else
                            {
                                iUserAuthorizationCallback.onFailure("Invalid request");
                            }
                        }else
                        {
                            iUserAuthorizationCallback.onFailure("Response format is not correct");
                        }
                    } catch (Exception e) {
                        dialog.dismiss();
                        e.printStackTrace();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iUserAuthorizationCallback.onFailure("Error in parsing response");
                            }
                        });
                    }
                } else {
                    dialog.dismiss();
                    System.out.println("Auth Fail ");
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iUserAuthorizationCallback.onFailure("Error in request");
                        }
                    });

                }
            }
        });
    }

    public interface IUserAuthorizationCallback {
        void onSuccess(String response_messg);
        void onFailure(String response_messg);
    }

    private void registerDeviceOnPlatform(final Activity activity, final IUserAuthorizationCallback iUserAuthorizationCallback) {

        SdkSPUtils sdkSPUtils = new SdkSPUtils(activity);
        String body = "token=" + sdkSPUtils.getString(SdkSPUtils.TOKEN) +
                "&userKey=" + sdkSPUtils.getString(SdkSPUtils.USER_KEY) +
                "&user_id=" + sdkSPUtils.getString(SdkSPUtils.USER_ID) +
                "&device_id=" + sdkSPUtils.getString(SdkSPUtils.DEVCIE_NAME) +
                "&gatewayid=" + sdkSPUtils.getString(SdkSPUtils.GATEWAY_ID) +
                "&protocol=" + sdkSPUtils.getString(SdkSPUtils.PROTOCOL) +
                "&access_key=" + sdkSPUtils.getString(SdkSPUtils.ACCESS_KEY);

        call = RESTClient.registerDevice(activity, body, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                dialog.dismiss();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        iUserAuthorizationCallback.onFailure("Registration onFailure");
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String res = response.body().string();
                        System.out.println("Devcie Register Success " + res);

                        JSONObject jsonObject = new JSONObject(res);
                        if (jsonObject.has("valid")) {
                            String valid = jsonObject.getString("valid");
                            if (valid.equalsIgnoreCase("true")) {

                                JSONArray jsonArray = jsonObject.getJSONArray("object");
                                if (jsonArray.length() > 0) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);

                                    String port = jsonObject1.getString("port");
                                    String api_url = jsonObject1.getString("api_url");
                                    String id = jsonObject1.getString("id");

                                    SdkSPUtils sdkSPUtils = new SdkSPUtils(context);
                                    sdkSPUtils.setValue(SdkSPUtils.API_SERVICE, api_url);
                                    sdkSPUtils.setValue(sdkSPUtils.DEVCIE_ID, id);
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            iUserAuthorizationCallback.onSuccess("Device Register Successfully");
                                        }
                                    });
                                }
                            } else {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        iUserAuthorizationCallback.onFailure("Request not valid");
                                    }
                                });
                            }
                        } else {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iUserAuthorizationCallback.onFailure("Response format is incorrect");
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iUserAuthorizationCallback.onFailure("Error in parsing response");
                            }
                        });

                    } finally {
                        if (dialog.isShowing())
                            dialog.dismiss();
                    }
                } else {
                    if (dialog.isShowing())
                        dialog.dismiss();
                    System.out.println("Device Regiser Fail ");
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iUserAuthorizationCallback.onFailure("Error in request");
                        }
                    });

                }
            }
        });
    }

    public static LocationUpdateReceiver registerLocationUpdateReeiver(LocationUpdateReceiver.INotifyLocationUpdates activity)
    {
        LocationUpdateReceiver locationUpdateReceiver= new LocationUpdateReceiver(activity);
        ((Activity)activity).registerReceiver(locationUpdateReceiver,new IntentFilter("iot_sdk_mobile_android"));
        return locationUpdateReceiver;
    }
    public static void unregisterLocationUpdateReeiver(Activity activity,LocationUpdateReceiver locationUpdateReceiver)
    {
        activity.unregisterReceiver(locationUpdateReceiver);
    }
}
