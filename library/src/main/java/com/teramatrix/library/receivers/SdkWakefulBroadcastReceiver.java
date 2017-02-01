package com.teramatrix.library.receivers;

import android.content.Context;
import android.content.Intent;

import com.teramatrix.library.service.DataIntentService;


/**
 * Created by arun.singh on 1/20/2017.
 * This BroadcastReceiver will be invoked periodicall from Alarmmanager. It wll launch DataIntentService service to uploading data to server.
 *
 */

public class SdkWakefulBroadcastReceiver extends android.support.v4.content.WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Start the service, keeping the device awake while the service is
        // launching. This is the Intent to deliver to the service.
       Intent service = new Intent(context, DataIntentService.class);
        startWakefulService(context, service);
    }
}
