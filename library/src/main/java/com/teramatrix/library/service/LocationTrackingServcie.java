package com.teramatrix.library.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.teramatrix.library.IoTSDK;
import com.teramatrix.library.R;
import com.teramatrix.library.util.SdkSPUtils;
import com.google.android.gms.location.LocationListener;


/**
 * Created by arun.singh on 1/12/2017.
 */

public class LocationTrackingServcie extends Service implements LocationListener {


    /**
     * A flag to notify that service is a foreground service
     */
    public static final String START_FOREGROUND_SERVICE = "start";

    /**
     * Flag to remove notification and location listener
     */
    public static final String STOP_FOREGROUND_SERVICE = "stop";
    /**
     * Notification id for foreground service while tracking the current location of device
     */
    public static final int REQUEST_FOREGROUND = 105;

    /**
     * To get continues location updates
     */

    private FusedLocationProvider provider;
    /**
     * Because it is a foreground service so it is needed to have a notification on notification drawer always.
     * Following instance will build a notification on every location update with default style and settings.
     */
    private NotificationCompat.Builder builder;

    private int interval = 30;
    /**
     * To keep track on the battery status. Every time we receive battery level,
     * this static variable's value will be changed
     */
    private static int BATTERY_LEVEL;
    /**
     * It's nothing more than a simple broad cast to catch battery level whenever it changes
     */

    private BroadcastReceiver batteryReceiver = new BatteryBroadcast();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        if (provider != null) {
            provider = null;
            Log.e("LocationService", "Setting null to fused location provider and current interval is " + interval);
        }

        //Settting Location Update Interval
        interval = new SdkSPUtils(getApplicationContext()).getInt(SdkSPUtils.LOCATION_UPDATE_INTERVAL);

        // now initiating fused location provider and notification builder
        provider = new FusedLocationProvider(getApplicationContext())
                .setInterval(1000 * interval)
                .setFastestInterval(1000 * interval)
                .setPriority(interval < 10 ? FusedLocationProvider.PRIORITY_HIGH_ACCURACY : FusedLocationProvider.PRIORITY_BALANCED_POWER_ACCURACY)
                .setLocationListener(this);

        builder = new NotificationCompat.Builder(this)
                .setContentTitle("Location Service")
                .setContentText("Fetching current location...")
                .setSmallIcon(R.drawable.ic_current_location)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_current_location))
                .setShowWhen(true)
                .setOngoing(true);

        // registering battery receiver
        registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));


        //Setting Exit Service to false
        new SdkSPUtils(getApplicationContext()).setValue(SdkSPUtils.IS_LCOATION_SERVICE_RUNNING, true);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        switch (intent.getAction()) {
            // if we have to start service
            case START_FOREGROUND_SERVICE:
                startForeground(REQUEST_FOREGROUND, builder.build());
                provider.start();
                break;
            // if we are going to stop everything
            case STOP_FOREGROUND_SERVICE:
                stopForeground(true);
                provider.stop();
                stopSelf();
                break;
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            // stopping location provider
            if (provider != null)
                provider.stop();

            // un-registering network receiver
            /*if (networkReceiver != null)
                unregisterReceiver(networkReceiver);*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // un-registering battery receiver
            if (batteryReceiver != null)
                unregisterReceiver(batteryReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Service re start cases
        if (new SdkSPUtils(getApplicationContext()).getString(SdkSPUtils.SERVICE_EXECUTE_MODE).equalsIgnoreCase(IoTSDK.SERVICE_EXECUTE_MODE_ONLY_FORGROUND)) {
            //do nothing - Let service be stopped
        } else if (!new SdkSPUtils(getApplicationContext()).getBoolean(SdkSPUtils.IS_LCOATION_SERVICE_RUNNING)) {
            //do nothing - Let service be stopped
        } else {
            //Re-start Service
            startService(new Intent(this, LocationTrackingServcie.class).setAction(START_FOREGROUND_SERVICE));
        }
    }

    /*Callback method  to receive newly found location*/
    @Override
    public void onLocationChanged(Location location) {

        /*Get previous location from SP*/
        SdkSPUtils sdkSPUtils = new SdkSPUtils(getApplicationContext());
        float oldLocationLatitude = sdkSPUtils.getFloat(SdkSPUtils.LOCATION_LATITUDE);
        float oldLocationLongitude = sdkSPUtils.getFloat(SdkSPUtils.LOCATION_LONGITUDE);

        Location oldLocation = new Location("");
        oldLocation.setLatitude(oldLocationLatitude);
        oldLocation.setLongitude(oldLocationLongitude);

        /*If new location is within 100 meter of previous location , we can consider both location same and can ignore the new location */
        if (oldLocation.distanceTo(location) <100) {
            //Same location, do nothing
        } else {
            //new lcoation, replace previos saved location with new location . this locatino will be sent to server
            //save new location in SP
            sdkSPUtils.setValue(SdkSPUtils.LOCATION_LATITUDE,(float) location.getLatitude());
            sdkSPUtils.setValue(SdkSPUtils.LOCATION_LONGITUDE,(float) location.getLongitude());
        }
    }

    /**
     * It's nothing more than a simple broad cast to catch battery level whenever it changes
     *
     */
    class BatteryBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            BATTERY_LEVEL = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        }
    }
}
