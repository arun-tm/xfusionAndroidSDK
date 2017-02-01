package com.teramatrix.library;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;


import com.teramatrix.library.model.CellInfoProperties;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by arun.singh on 1/20/2017.
 *
 * This class is used for collecting device related data known as service data.
 * IT collects Network ,Phone and Traffic data.
 *
 */

public class DeviceServcieDataProvider {


    /*Phone data*/
    public static ArrayList<CellInfoProperties> getPhoneData(Context context) {

        ArrayList<CellInfoProperties> cellInfoProperties = new ArrayList<CellInfoProperties>();
        TelephonyManager tm = (TelephonyManager) (context.getSystemService(Context.TELEPHONY_SERVICE));
        String phone_type = "";
        switch (tm.getPhoneType()) {
            case (TelephonyManager.PHONE_TYPE_CDMA):
                phone_type = "CDMA";
                break;
            case (TelephonyManager.PHONE_TYPE_GSM):
                phone_type = "GSM";
                ;
                break;
            case (TelephonyManager.PHONE_TYPE_NONE):
                phone_type = "NONE";
                break;
            default:
                phone_type = "UNKNOWN";
        }
        String sim_state = "";
        switch (tm.getSimState()) {
            case (TelephonyManager.SIM_STATE_ABSENT):
                sim_state = "ABSENT";
                break;
            case (TelephonyManager.SIM_STATE_NETWORK_LOCKED):
                sim_state = "NETWORK_LOCKED";
                ;
                break;
            case (TelephonyManager.SIM_STATE_PIN_REQUIRED):
                sim_state = "PIN_REQUESTED";
                break;
            case (TelephonyManager.SIM_STATE_PUK_REQUIRED):
                sim_state = "PUK_REQUESTED";
                break;
            case (TelephonyManager.SIM_STATE_READY):
                sim_state = "READY";
                break;
            case (TelephonyManager.SIM_STATE_UNKNOWN):
                sim_state = "UNKNOWN";
                break;
            default:
                sim_state = "-";
        }
        cellInfoProperties.add(new CellInfoProperties("Phone Type", phone_type,"Phone"));
        cellInfoProperties.add(new CellInfoProperties("IMEI Number", tm.getDeviceId(),"Phone"));
        cellInfoProperties.add(new CellInfoProperties("Manufacturer", android.os.Build.MANUFACTURER,"Phone"));
        cellInfoProperties.add(new CellInfoProperties("Model", android.os.Build.MODEL,"Phone"));
        cellInfoProperties.add(new CellInfoProperties("Sim Serial Number", tm.getSimSerialNumber(),"Phone"));
        cellInfoProperties.add(new CellInfoProperties("SIM Country ISO", tm.getSimCountryIso(),"Phone"));
        cellInfoProperties.add(new CellInfoProperties("SIM State", sim_state,"Phone"));
        cellInfoProperties.add(new CellInfoProperties("Software Version", tm.getDeviceSoftwareVersion(),"Phone"));
        cellInfoProperties.add(new CellInfoProperties("SubscriberID", tm.getSubscriberId(),"Phone"));

        return cellInfoProperties;

    }
    /*Network specific data*/
    public static ArrayList<CellInfoProperties> getNetworkData(Context context) {

        ArrayList<CellInfoProperties> cellInfoProperties = new ArrayList<CellInfoProperties>();
        //Get the instance of TelephonyManager
        TelephonyManager tm = (TelephonyManager) (context.getSystemService(Context.TELEPHONY_SERVICE));
//        Get the phone type
        String strphoneType = "";
        switch (tm.getPhoneType()) {
            case (TelephonyManager.PHONE_TYPE_CDMA):
                strphoneType = "CDMA";
                break;
            case (TelephonyManager.PHONE_TYPE_GSM):
                strphoneType = "GSM";
                break;
            case (TelephonyManager.PHONE_TYPE_NONE):
                strphoneType = "NONE";
                break;
        }
        //Call State
        String call_state = "";
        switch (tm.getCallState()) {
            case (TelephonyManager.CALL_STATE_IDLE):
                call_state = "IDLE";
                break;
            case (TelephonyManager.CALL_STATE_OFFHOOK):
                call_state = "OFF HOOK";
                ;
                break;
            case (TelephonyManager.CALL_STATE_RINGING):
                call_state = "RINGING";
                break;
        }
        String network_technology = "";
        switch (tm.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
                network_technology = "2G GPRS";
                break;
            case TelephonyManager.NETWORK_TYPE_EDGE:
                network_technology = "2G EDGE";
                break;
            case TelephonyManager.NETWORK_TYPE_CDMA:
                network_technology = "2G CDMA";
                break;
            case TelephonyManager.NETWORK_TYPE_1xRTT:
                network_technology = "2G 1xRTT";
                break;
            case TelephonyManager.NETWORK_TYPE_IDEN:
                network_technology = "2G IDEN";
                break;
            case TelephonyManager.NETWORK_TYPE_UMTS:
                network_technology = "3G UMTS";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
                network_technology = "3G EVDO_0";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                network_technology = "3G EVDO_A";
                break;
            case TelephonyManager.NETWORK_TYPE_HSDPA:
                network_technology = "3G HSDPA";
                break;
            case TelephonyManager.NETWORK_TYPE_HSUPA:
                network_technology = "3G HSUPA";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPA:
                network_technology = "3G HSPA";
                break;
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
                network_technology = "3G EVDO_B";
                break;
            case TelephonyManager.NETWORK_TYPE_EHRPD:
                network_technology = "3G EHRPD";
                break;
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                network_technology = "3G HSPAP";
                break;

            case TelephonyManager.NETWORK_TYPE_LTE:
                network_technology = "4G LTE";
                break;
            default:
                network_technology = "UNKNOWN";
        }
        //Network Parameters
        cellInfoProperties.add(new CellInfoProperties("Network Operator", tm.getNetworkOperatorName(),"Network"));
        cellInfoProperties.add(new CellInfoProperties("Network State", getConnectivityStatus(context),"Network"));
        cellInfoProperties.add(new CellInfoProperties("Technology", network_technology,"Network"));
        cellInfoProperties.add(new CellInfoProperties("Network Country ISO", tm.getNetworkCountryIso(),"Network"));
        cellInfoProperties.add(new CellInfoProperties("Phone Network Type", strphoneType,"Network"));
        cellInfoProperties.add(new CellInfoProperties("In Roaming", tm.isNetworkRoaming() + "","Network"));
        cellInfoProperties.add(new CellInfoProperties("Call State", call_state,"Network"));

        ArrayList<String> property_strings = new ArrayList<String>();
        List<CellInfo> cellInfos = tm.getAllCellInfo();
        if(cellInfos!=null){
            for (int i = 0 ; i<cellInfos.size(); i++){
                if (cellInfos.get(i).isRegistered()){
                    if(cellInfos.get(i) instanceof CellInfoWcdma){
                        CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) tm.getAllCellInfo().get(0);
                        CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();

                        cellSignalStrengthWcdma.getAsuLevel();


                        property_strings.add("Signal Strength(dbm),"+String.valueOf(cellSignalStrengthWcdma.getDbm()));
                        property_strings.add("Signal Level,"+cellSignalStrengthWcdma.getLevel());
                        property_strings.add("CID,"+cellInfoWcdma.getCellIdentity().getCid()+"");
                        property_strings.add("LAC,"+cellInfoWcdma.getCellIdentity().getLac());
                        property_strings.add("MCC,"+cellInfoWcdma.getCellIdentity().getMcc());
                        property_strings.add("MNC,"+cellInfoWcdma.getCellIdentity().getMnc());
                        property_strings.add("PSC,"+cellInfoWcdma.getCellIdentity().getPsc());
//                        property_strings.add("WCDMA UARFCN,"+cellInfoWcdma.getCellIdentity().getUarfcn());

                    }else if(cellInfos.get(i) instanceof CellInfoGsm){
                        CellInfoGsm cellInfogsm = (CellInfoGsm) tm.getAllCellInfo().get(0);
                        CellSignalStrengthGsm cellSignalStrengthGsm = cellInfogsm.getCellSignalStrength();


                        property_strings.add("Signal Strength(dbm),"+String.valueOf(cellSignalStrengthGsm.getDbm()));
                        property_strings.add("Signal Level,"+cellSignalStrengthGsm.getLevel());
                        property_strings.add("CID,"+cellInfogsm.getCellIdentity().getCid());
                        property_strings.add("LAC,"+cellInfogsm.getCellIdentity().getLac());
                        property_strings.add("MCC,"+cellInfogsm.getCellIdentity().getMcc());
                        property_strings.add("MNC,"+cellInfogsm.getCellIdentity().getMnc());
                        property_strings.add("PSC,"+cellInfogsm.getCellIdentity().getPsc());
//                        property_strings.add("GSM ARFCN,"+cellInfogsm.getCellIdentity().getArfcn());


                    }else if(cellInfos.get(i) instanceof CellInfoLte){
                        CellInfoLte cellInfoLte = (CellInfoLte) tm.getAllCellInfo().get(0);
                        CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();

                        property_strings.add("Signal Strength(dbm),"+ String.valueOf(cellSignalStrengthLte.getDbm()));
                        property_strings.add("Signal Level,"+cellSignalStrengthLte.getLevel());
                        property_strings.add("CI,"+cellInfoLte.getCellIdentity().getCi());
                        property_strings.add("TAC,"+cellInfoLte.getCellIdentity().getTac());
                        property_strings.add("MCC,"+cellInfoLte.getCellIdentity().getMcc());
                        property_strings.add("MNC,"+cellInfoLte.getCellIdentity().getMnc());
                        property_strings.add("PCI,"+cellInfoLte.getCellIdentity().getPci());

                    }

                }
            }
            for(int i =0;i<property_strings.size();i++)
            {
                cellInfoProperties.add(new CellInfoProperties(property_strings.get(i).split(",")[0],property_strings.get(i).split(",")[1],"Network"));
            }

        }

        return cellInfoProperties;
    }

    /*Traffic specific data*/
    public static ArrayList<CellInfoProperties> getTraficData(Context context) {

        ArrayList<CellInfoProperties> cellInfoProperties = new ArrayList<CellInfoProperties>();
        //Get the instance of TelephonyManager
        TelephonyManager tm=(TelephonyManager)(context.getSystemService(Context.TELEPHONY_SERVICE));

        String data_state ="";
        switch (tm.getDataState())
        {
            case (TelephonyManager.DATA_CONNECTED):
                data_state="CONNECTED";
                break;
            case (TelephonyManager.DATA_CONNECTING):
                data_state="CONNECTING";;
                break;
            case (TelephonyManager.DATA_DISCONNECTED):
                data_state="DISCONNECTED";
                break;
            case (TelephonyManager.DATA_SUSPENDED):
                data_state="SUSPENDED";
                break;
            default:
                data_state="-";
        }

        String data_activity ="";
        switch (tm.getDataActivity())
        {
            case (TelephonyManager.DATA_ACTIVITY_INOUT):
                data_activity="INOUT";
                break;
            case (TelephonyManager.DATA_ACTIVITY_IN):
                data_activity="IN";;
                break;
            case (TelephonyManager.DATA_ACTIVITY_DORMANT):
                data_activity="DORMANT";
                break;
            case (TelephonyManager.DATA_ACTIVITY_NONE):
                data_activity="NONE";
                break;
            case (TelephonyManager.DATA_ACTIVITY_OUT):
                data_activity="OUT";
                break;
            default:
                data_activity="-";
        }


        cellInfoProperties.add(new CellInfoProperties("Data Activity", data_activity,"Trafic"));
        cellInfoProperties.add(new CellInfoProperties("Data State", data_state,"Trafic"));
        cellInfoProperties.add(new CellInfoProperties("Mobile Rx bytes", TrafficStats.getMobileRxBytes()+"","Trafic"));
        cellInfoProperties.add(new CellInfoProperties("Mobile Tx bytes",TrafficStats.getMobileTxBytes()+"","Trafic"));
        cellInfoProperties.add(new CellInfoProperties("Total Rx bytes",TrafficStats.getTotalRxBytes()+"","Trafic"));
        cellInfoProperties.add(new CellInfoProperties("Total Tx bytes",TrafficStats.getTotalTxBytes()+"","Trafic"));

        cellInfoProperties.add(new CellInfoProperties("Mobile Rx packets",TrafficStats.getMobileRxPackets()+"","Trafic"));
        cellInfoProperties.add(new CellInfoProperties("Mobile Tx packets",TrafficStats.getMobileTxPackets()+"","Trafic"));
        cellInfoProperties.add(new CellInfoProperties("Total Rx packets",TrafficStats.getTotalRxPackets()+"","Trafic"));
        cellInfoProperties.add(new CellInfoProperties("Total Tx packets",TrafficStats.getTotalTxPackets()+"","Trafic"));

        return cellInfoProperties;

    }

    /*Get network connectivity status*/
    private static String getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return "Wifi Connected";

            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return "Mobile Data Connected";
        }
        return "Not Connected";
    }
}
