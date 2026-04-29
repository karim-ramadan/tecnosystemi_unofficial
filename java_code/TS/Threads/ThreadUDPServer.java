package it.tecnosystemi.TS.Threads;

import android.util.Log;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Model.ControlUnit;
import it.tecnosystemi.TS.Model.UdpMessage;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.SavePreferences;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Date;

public class ThreadUDPServer extends Thread {
    public static SavePreferences pref;
    private boolean Server_aktiv = true;
    BaseActivity activity;
    UdpMessage udpMessage;

    public ThreadUDPServer(BaseActivity baseActivity) {
        this.activity = baseActivity;
    }

    public void run() {
        byte[] bArr = new byte[4096];
        new DatagramPacket(bArr, 4096);
        new Date();
        this.Server_aktiv = true;
        while (this.Server_aktiv) {
            try {
                DatagramSocket datagramSocket = new DatagramSocket((SocketAddress) null);
                datagramSocket.setReuseAddress(true);
                datagramSocket.bind(new InetSocketAddress(5678));
                datagramSocket.setSoTimeout(5000);
                if (Constants._LOG) {
                    Log.e("SOCKET - UDP", "WAITING..");
                }
                datagramSocket.receive(new DatagramPacket(bArr, 4096));
                String str = new String(bArr, 0, 4096);
                if (Constants._LOG) {
                    Log.e("SOCKET - UDP", str.substring(0, 20));
                }
                this.udpMessage = parseUdpMessage(str);
                if (this.activity.cu != null && this.activity.cu.getSerial().equals(this.udpMessage.getSeriale()) && !this.activity.cu.getIp().equals(this.udpMessage.getIp())) {
                    this.activity.cu.setIp(this.udpMessage.getIp());
                    ControlUnit.saveCuInPref(this.activity.cu, this.activity);
                }
            } catch (Exception unused) {
            }
        }
    }

    private UdpMessage parseUdpMessage(String str) {
        return new UdpMessage();
    }

    public void stop_UDP_Server() {
        this.Server_aktiv = false;
    }
}
