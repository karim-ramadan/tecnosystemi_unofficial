package it.tecnosystemi.TS.Model;

import java.io.Serializable;
import org.json.JSONObject;

public class WiFi implements Serializable {
    int crip;
    String mac;
    String pwd;
    int rssi;
    String sid;

    public WiFi() {
    }

    public WiFi(JSONObject jSONObject) {
        try {
            setSid(jSONObject.getString("id"));
            setCrip(jSONObject.getInt("sec"));
            try {
                setRssi(jSONObject.getInt("rssi"));
            } catch (Exception unused) {
                setRssi(0);
            }
            try {
                setMac(jSONObject.getString("mac"));
            } catch (Exception unused2) {
                setMac("");
            }
        } catch (Exception unused3) {
        }
    }

    public int getRssi() {
        return this.rssi;
    }

    public void setRssi(int i) {
        this.rssi = i;
    }

    public String getSid() {
        return this.sid;
    }

    public void setSid(String str) {
        this.sid = str;
    }

    public String getPwd() {
        return this.pwd;
    }

    public void setPwd(String str) {
        this.pwd = str;
    }

    public int isCrip() {
        return this.crip;
    }

    public void setCrip(int i) {
        this.crip = i;
    }

    public String getMac() {
        return this.mac;
    }

    public void setMac(String str) {
        this.mac = str;
    }
}
