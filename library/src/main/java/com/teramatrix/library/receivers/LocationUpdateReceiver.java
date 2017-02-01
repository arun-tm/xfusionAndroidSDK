package com.teramatrix.library.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

/**
 * Created by arun.singh on 1/27/2017.
 * This BroadcastReceiver will be used to transfer location data from LcoationTracking service to any activity or fragment.
 *
 */

public class LocationUpdateReceiver extends BroadcastReceiver {


    public interface INotifyLocationUpdates
    {
        public void onLocationChanged(Location location);
    }
    INotifyLocationUpdates iNotifyLocationUpdates;
    public LocationUpdateReceiver(INotifyLocationUpdates iNotifyLocationUpdates)
    {
        this.iNotifyLocationUpdates = iNotifyLocationUpdates;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        float latitude = intent.getFloatExtra("latitude",0.0f);
        float longitude = intent.getFloatExtra("longitude",0.0f);
        long log_time = intent.getLongExtra("log_time",0);
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setTime(log_time);
        iNotifyLocationUpdates.onLocationChanged(location);
    }
}
