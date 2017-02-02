# xfusion-android-sdk

This is android mobile library for automating connection(registration) and device's service data updation process for xFusion platform. Any third party app can integrate it and run a service in background which will send device's servcie data to xFusion Platform.

xFusion-android-sdk has following functionality.
  * Register user device to xFusion Platform.
  * Retrieve device's service data like Network,Phone,Traffic and send them to xFusion Platform.
  * Run a background service for location tracking(Optional) of device.

For integrating xFusion-android-sdk to any android project follow below steps.


### Initial configuration

##### Importing library

**Step 1:**
add following code in project's build.gradle file

```sh
allprojects {
    repositories {
        jcenter()
        maven { url = 'https://jitpack.io' }
    }
}
```


**Step 2:**
add following dependency in app's build.gradle file
```sh
compile 'com.github.arun-tm:xfusionAndroidSDK:0.1.0'
compile 'com.github.d-max:spots-dialog:0.4@aar'
```
 
##### Ask For Permission:

xfusion-android-sdk uses some device specific control and information like Imei number , GPS , internet etc. For accessing these information permissions has to be taken from user during installation time or first opening of user app. Put below code in your app’s splash screen or login screen.

```sh
LoginActivity -------------

@Override
protected void onCreate(Bundle savedInstanceState) {
    //Request For Permission
    IoTSDK ioTSDK = IoTSDK.getInstance(getApplicationContext());
    ioTSDK.requestPermission(this);
}
```


Two permissions will be requested by xFusion-android-sdk from user .One is for allowing location services of device and second is for accessing phone state to read device imei number. User will have to grant both these permission.

 

##### Login using xFusion-android-sdk authorization
                   
     
Authorization process internally has two steps .One is to validate user (User name and password) on backend Auth server and in return get auth_token from server. Second is to register user device on platform using auth_token. Both steps has been abstracted from user and he has to call only a single method IoTSDK.login().


###### 1 . Implement IoTSDK.IUserAuthorizationCallback interface in your Login activity.


```sh
public class LoginActivity extends AppCompatActivity implements IoTSDK.IUserAuthorizationCallback
{
    @Override
    public void onSuccess(String response_messg) {
    // This method will be called when authorization process is successful.
    //Code here to move inside your app.
    }
    @Override
    public void onFailure(String response_messg) {
        // This method will be called when authorization process is Fail
        //Code here to prompt user to try again with either correct username or password.
   }
}
```



 

###### 2. Call IoTSDK.login()

```sh


findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
         String username = ((TextView))findViewById(R.id.txt_username)).getText().toString();
         String password = ((TextView) findViewById(R.id.txt_password)).getText().toString()
        try {
                  IoTSDK.getInstance(LoginActivity.this).login(LoginActivity.this, username,                        password, LoginActivity.this);
            } catch (InvalidRequestParametersException e)
            {
                e.printStackTrace();
            }
        }
    });
```


###### 3. Redirect user into app’s main screen


If authorization process is successful take user inside your app (Main screen of app)

```sh
@Override
public void onSuccess(String response_messg) {

        startActivity(new Intent(LoginActivity.this,MainActivity.class));
        
       //Set flag to maintain logged in status-------------
       sharedPreferences.edit().putBoolean("is_login",true).commit();
       finish();
}
```


##### Initialize and Configure xfusion-android-sdk for using its services


Put below code in app’s main activity(Landing screen) onStart() method


```sh
@Override
protected void onStart() {
            super.onStart();
         //Initialize IoT SDK Servcies
            IoTSDK ioTSDK = IoTSDK.getInstance(MainActivity.this);
        //Configure Location Tracking
            ioTSDK.setLocationTrackingEnabled(true);
            ioTSDK.setLocationUpdateInterval(20);
            ioTSDK.setLocationTrackingServiceExecuteMode(IoTSDK.SERVICE_EXECUTE_MODE_ALWAYS);
        //Configure device|app servcie data API
            ioTSDK.setDeviceParametersToTrack(IoTSDK.DEVICE_PARAMETERS_PHONE,                            IoTSDK.DEVICE_PARAMETERS_NETWORKS, IoTSDK.DEVICE_PARAMETERS_TRAFIC);                                    
            ioTSDK.setDevceiDataAPIUpdateInterval(30);
            try {
                    ioTSDK.initService();
                } catch (UnAuthorizedAccess unAuthorizedAccess) {
                    unAuthorizedAccess.printStackTrace();
                }
    }
```

**Configuration Parameters:**

- [IoTSDK.getInstance(Context context)]() - will always have current activity context.

- [ioTSDK.setLocationTrackingEnabled(boolean flag)]() - put true/false as flag.
‘true’ (if you want to enable location tracking of user’s device) otherwise ‘false’ for disabling tracking. By default it is disabled.

- [ioTSDK.setLocationUpdateInterval(int interval)]() - IF you have enabled location tracking then this value ‘interval’ will define time for new location update frequency. ‘Interval‘ value is for seconds of time. By default its value is 30.

- [ioTSDK.setLocationTrackingServiceExecuteMode(String execute_mode)]()
It denotes xfusion-android-sdk services running pattern. If [‘IoTSDK.SERVICE_EXECUTE_MODE_ALWAYS’]() is used all services will be active/running even app is closed i.e in background.If [‘IoTSDK.SERVICE_EXECUTE_MODE_ONLY_FORGROUND’]() is used services will only be active/running when app is in foreground . Services will stop when user close the app.

- [ioTSDK.setDeviceParametersToTrack(String ...service_parameters_to_track)]() - Put service_parameters_to_track which you want to send to platform

    service_parameters_to_track can be one of following three
```sh
* IoTSDK.DEVICE_PARAMETERS_PHONE
* IoTSDK.DEVICE_PARAMETERS_NETWORKS
* IoTSDK.DEVICE_PARAMETERS_TRAFIC
```


 

1.DEVICE_PARAMETERS_PHONE includes following device information
```sh
Phone Type
IMEI Number
Manufacturer
Model
Sim Serial Number
SIM Country ISO
SIM State
Software Version
SubscriberID
Battery Status
```


2.DEVICE_PARAMETERS_NETWORKS includes following device information

```sh
Network Operator
Network State
Technology
Network Country ISO
Phone Network Type
In Roaming
Call State
Signal Strength(dbm)
Signal Level
CID
CI
TAC
LAC
MCC
MNC
PSC
```



3.DEVICE_PARAMETERS_TRAFIC includes following device information

```sh
Data Activity
Data State
Mobile Rx bytes
Mobile Tx bytes
Total Rx bytes
Total Tx byte
Mobile Rx packets
Mobile Tx packets
Total Rx packets
Total Tx packets
```


- [ioTSDK.setDevceiDataAPIUpdateInterval(int interval)]() - set interval time in which xfusion-android-sdk will send all servcie data to platform. Default value is 20 i.e 20 seconds.
 
#### Get Location Update in your app

xfusion-android-sdk continuously run a service in background for tracking location of device. New found locations are directly sent to server(xFusion Platform) in background. But if you want to get the location updates in your app for some purpose like drawing current position on map screen, you can register a receiver in map activity or any activity where you want to be notified about location updates.

**Implement Interface**

Implement LocationUpdateReceiver.INotifyLocationUpdates interface in you activity/fragment . It will define [onLocationChanged(Location location)]() method in the activity/fragment. Location updates will be available through this method.

**Register Receiver in Activity:**

declare a global variable
```sh
com.teramatrix.library.receivers.LocationUpdateReceiver locationUpdateReceiver;
```

register receiver in onResume method
```sh
@Override
protected void onResume() {
       super.onResume();
       locationUpdateReceiver = IoTSDK.registerLocationUpdateReeiver(this);
}
```

**Unregister Receiver in Activity:**

Do not forget to unregister receiver .
```sh
@Override
protected void onPause() {
super.onPause();
    IoTSDK.unregisterLocationUpdateReeiver(this,locationUpdateReceiver);
}
```

**Receive Location Updates:**

```sh
@Override
public void onLocationChanged(Location location) {
    //use location here
);
```


