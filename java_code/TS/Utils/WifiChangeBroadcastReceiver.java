package it.tecnosystemi.TS.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WifiChangeBroadcastReceiver extends BroadcastReceiver {
    private WifiChangeBroadcastListener listener;

    public interface WifiChangeBroadcastListener {
        void onWifiChangeBroadcastReceived(Context context, Intent intent);
    }

    public WifiChangeBroadcastReceiver(WifiChangeBroadcastListener wifiChangeBroadcastListener) {
        this.listener = wifiChangeBroadcastListener;
    }

    public void onReceive(Context context, Intent intent) {
        this.listener.onWifiChangeBroadcastReceived(context, intent);
    }
}
