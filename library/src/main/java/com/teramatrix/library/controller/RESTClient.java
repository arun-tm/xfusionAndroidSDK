package com.teramatrix.library.controller;

import android.content.Context;
import android.util.Log;


import com.teramatrix.library.util.SdkSPUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * A rest api client to handle all network calls in the application. Each method will take a {@link Callback} implementation
 * so there is no need to handle response here.
 *
 */
public class RESTClient {

    public static final String APP_ID = "9a959887-5946-11e6-9bb0-fe984cc15272";
    public static final String LOGIN = "/XFusionPlatform/oauth/token";
    public static final String REGISTER_DEVICE = "/XFusionPlatform/deviceregister";

    /**
     * All apis contains same media type that is {@code application/x-www-form-urlencoded}
     */
    private static MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");

    /**
     * Factory for calls, which can be used to send HTTP requests and read their responses. Most applications can use a single
     * OkHttpClient for all of their HTTP requests, benefiting from a shared response cache, thread pool, connection re-use, etc.
     */
    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build();


    /*Login call*/
    public static Call Login(Context context, String body, Callback callback) {
        Request request = new Request.Builder().url(new SdkSPUtils(context).get_API_End_Point() + LOGIN)
                .post(RequestBody.create(mediaType, body)).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        printRequest(request, true);
        return call;
    }


    /*Register devcie on platform*/
    public static Call registerDevice(Context context, String body, Callback callback) {
        Request request = new Request.Builder().url(new SdkSPUtils(context).get_API_End_Point() + REGISTER_DEVICE)
                .post(RequestBody.create(mediaType, body)).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        printRequest(request, true);
        return call;
    }

    /*Upload data to server Synchronusly*/
    public static Response updateDataSynchronusly(Context context, String url, String body)
    {
        Request request = new Request.Builder().url(url)
                .post(RequestBody.create(mediaType, body)).build();
        Call call = client.newCall(request);
        try {
            Response response =call.execute();
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }
        printRequest(request, true);
        return null;
    }

    /**
     * Method will request data to the server and publish results in the callback provided in arguments.
     *
     * @param context  application/activity context to get shared preferences
     * @param url      api/resource url
     * @param body     request body separated by '&'
     * @param callback implementation of callback class to get results
     * @return currently initiated call
     */
    public static Call requestData(Context context, String url, String body, Callback callback) {
        SdkSPUtils spUtils = new SdkSPUtils(context);
        Request request = new Request.Builder()
                .url(spUtils.get_API_End_Point() + url)
                .post(RequestBody.create(mediaType, body))
                .addHeader("token", spUtils.getString(spUtils.TOKEN))
                .addHeader("userKey", spUtils.getString(spUtils.USER_KEY))
                .addHeader("user_id", spUtils.getString(spUtils.USER_ID))
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
        printRequest(request, true);
        return call;
    }

    /**
     * Method will print Http Request body to the LogCat Error
     *
     * @param request pass the request to print its body
     */
    private static void printRequest(Request request, boolean error) {
        try {
            if (error) {
                Log.e("-----------", "-------------------------------------------------------------------------");
                Buffer buffer = new Buffer();
                request.body().writeTo(buffer);
                Log.e("REST_CLIENT", "_URL\t-> " + request.url().toString());
                Log.e("REST_CLIENT", "HEAD\t-> " + request.headers().toString().replace("\n", "&").replace(": ", ":"));
                Log.e("REST_CLIENT", "BODY\t-> " + buffer.readUtf8().replace("=", ":"));
                Log.e("-----------", "-------------------------------------------------------------------------");
            } else {
                Log.d("-----------", "-------------------------------------------------------------------------");
                Buffer buffer = new Buffer();
                request.body().writeTo(buffer);
                Log.d("REST_CLIENT", "_URL\t-> " + request.url().toString());
                Log.d("REST_CLIENT", "BODY\t-> " + buffer.readUtf8().replace("=", ":"));
                Log.d("-----------", "-------------------------------------------------------------------------");
            }
        } catch (Exception e) {
            Log.e("REST_CLIENT", e.getMessage());
        }
    }

    /**
     * To print that request which is structured for the form data.
     *
     * @param r ok http request
     */
    private static void printFormData(Request r) {
        Log.e("-----------", "-------------------------------------------------------------------------");
        try {
            Log.e("REST_CLIENT", "_URL\t-> " + r.url().toString());
            Log.e("REST_CLIENT", "HEAD\t-> " + r.headers().toString().replace("\n", "&"));
            Buffer b = new Buffer();
            r.body().writeTo(b);
            String arg = "";
            for (String s : b.readUtf8().replaceAll("(\r)", "").split("\n")) {
                if (s.startsWith("Content-Disposition:")) {
                    arg = s.substring(s.indexOf("; n") + 8, s.length() - 1);
                } else if (s.startsWith("Content-Length:") || s.startsWith("--") || s.equals("")) {
                    // continue;
                    // doing nothing at the moment
                } else {
                    arg += (":" + s);
                    Log.e("BODY", arg);
                    arg = "";
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Log.e("-----------", "-------------------------------------------------------------------------");
        }
    }
}
