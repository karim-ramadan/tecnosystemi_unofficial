package it.tecnosystemi.TS.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginModel {
    @SerializedName("DeviceId")
    private String deviceId;
    @SerializedName("Platform")
    private String platform;
    @SerializedName("Password")
    private String pwd;
    @SerializedName("Timezone")
    private String timezone;
    @SerializedName("TokenPush")
    private String tokenPush;
    @SerializedName("Username")
    @Expose
    private String user;

    public String getUser() {
        return this.user;
    }

    public void setUser(String str) {
        this.user = str;
    }

    public String getPwd() {
        return this.pwd;
    }

    public void setPwd(String str) {
        this.pwd = str;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(String str) {
        this.deviceId = str;
    }

    public String getTokenPush() {
        return this.tokenPush;
    }

    public void setTokenPush(String str) {
        this.tokenPush = str;
    }

    public String getPlatform() {
        return this.platform;
    }

    public void setPlatform(String str) {
        this.platform = str;
    }

    public String getTimezone() {
        return this.timezone;
    }

    public void setTimezone(String str) {
        this.timezone = str;
    }
}
