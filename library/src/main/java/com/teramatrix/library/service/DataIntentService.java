package com.teramatrix.library.service;

import android.app.IntentService;
import android.content.Intent;


import com.teramatrix.library.DeviceServcieDataProvider;
import com.teramatrix.library.IoTSDK;
import com.teramatrix.library.controller.RESTClient;
import com.teramatrix.library.model.CellInfoProperties;
import com.teramatrix.library.receivers.SdkWakefulBroadcastReceiver;
import com.teramatrix.library.util.SdkSPUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Response;

/**
 * Created by arun.singh on 1/20/2017.
 * This Service will be invoked from SdkWakefulBroadcastReceiver . It collects all device service data + location data and send them to server.
 * It calls APIs synchronously and once its task is completed ,it gets finshed
 *
 *
 */

public class DataIntentService extends IntentService {

    public DataIntentService() {
        super("DataIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Do the work that requires your app to keep the CPU running.
        //Send Devcie-App data to Server

        ArrayList<CellInfoProperties> cellInfoProperties = new ArrayList<>();

        SdkSPUtils sdkSPUtils = new SdkSPUtils(getApplicationContext());
        float LocationLatitude = sdkSPUtils.getFloat(SdkSPUtils.LOCATION_LATITUDE);
        float LocationLongitude = sdkSPUtils.getFloat(SdkSPUtils.LOCATION_LONGITUDE);

        CellInfoProperties Properties_latitude = new CellInfoProperties("Latitude", LocationLatitude + "","Location");
        CellInfoProperties Properties_longitude = new CellInfoProperties("Longitude", LocationLongitude + "","Location");
        cellInfoProperties.add(Properties_latitude);
        cellInfoProperties.add(Properties_longitude);

        System.out.println("DataIntentService " + LocationLatitude + " " + LocationLongitude);
        //Get All Phone Related Parametrs
        if (sdkSPUtils.getBoolean(IoTSDK.DEVICE_PARAMETERS_PHONE)) {
            cellInfoProperties.addAll(DeviceServcieDataProvider.getPhoneData(this));
        }
        //Get All Network Related Parametrs
        if (sdkSPUtils.getBoolean(IoTSDK.DEVICE_PARAMETERS_NETWORKS)) {
            cellInfoProperties.addAll(DeviceServcieDataProvider.getNetworkData(this));
        }
        //Get All Trafic Related Parametrs
        if (sdkSPUtils.getBoolean(IoTSDK.DEVICE_PARAMETERS_TRAFIC)) {
            cellInfoProperties.addAll(DeviceServcieDataProvider.getTraficData(this));
        }

        //Form Json String from all servcie data
        JSONArray jsonArray = new JSONArray();
        try {
            String devcie_Id = sdkSPUtils.getString(SdkSPUtils.DEVCIE_ID);
            long currentUtcTime_seconds = System.currentTimeMillis();
            currentUtcTime_seconds =  currentUtcTime_seconds/1000;

            for (CellInfoProperties cellInfoProperties1 : cellInfoProperties) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("data_source", cellInfoProperties1.paramName);
                if(cellInfoProperties1.type.equalsIgnoreCase("Phone"))
                {
                    jsonObject.put("service_name", "Phone Parameters");
                }else if(cellInfoProperties1.type.equalsIgnoreCase("Network"))
                {
                    jsonObject.put("service_name", "Network Parameters");
                }else if(cellInfoProperties1.type.equalsIgnoreCase("Trafic"))
                {
                    jsonObject.put("service_name", "Trafic Parameters");
                }else if(cellInfoProperties1.type.equalsIgnoreCase("Location"))
                {
                    jsonObject.put("service_name", "Location Parameters");
                }
                jsonObject.put("check_timestamp", currentUtcTime_seconds);
                jsonObject.put("sys_timestamp", currentUtcTime_seconds);
                jsonObject.put("current_value", cellInfoProperties1.paramValue);
                jsonObject.put("device_id", devcie_Id);
                jsonArray.put(jsonObject);
            }

            //Add Host Status
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data_source", "host_status");
            jsonObject.put("service_name", "host_status");
            jsonObject.put("check_timestamp", currentUtcTime_seconds);
            jsonObject.put("sys_timestamp", currentUtcTime_seconds);
            jsonObject.put("current_value","ON");
            jsonObject.put("device_id", devcie_Id);
            jsonArray.put(jsonObject);


            System.out.println("jsonArray:" + jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Calling Publish API to Upload data onto Server
        String body = "data=" + jsonArray.toString();
        Response response = RESTClient.updateDataSynchronusly(getApplicationContext(), sdkSPUtils.getString(SdkSPUtils.API_SERVICE), body);

        //Getting Response OF Publish API
        if (response != null) {
            if (response.isSuccessful()) {
                String res = null;
                try {
                    res = response.body().string();
//                    System.out.println("Data Publish result " + res);
                } catch (IOException e) {
//                    System.out.println("Data Publish result " + "Failur");
                    e.printStackTrace();
                }

            } else {
//                System.out.println("Data Publish result " + "Failur");
            }
        } else {
//            System.out.println("Data Publish result " + "Failur");
        }
        // Release the wake lock provided by the SdkWakefulBroadcastReceiver.
        SdkWakefulBroadcastReceiver.completeWakefulIntent(intent);
    }
}
