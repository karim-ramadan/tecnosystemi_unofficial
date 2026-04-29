package it.tecnosystemi.TS.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    private boolean Ok_Marketing;
    private boolean Ok_Privacy;
    private boolean Ok_TOS;
    @SerializedName("DeviceId")
    @Expose
    private String deviceId;
    @SerializedName("Id")
    @Expose
    private long id;
    @SerializedName("Lang")
    @Expose
    private String lang;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("Password")
    @Expose
    private String password;
    @SerializedName("Username")
    @Expose
    private String username;

    public boolean isOk_TOS() {
        return this.Ok_TOS;
    }

    public void setOk_TOS(boolean z) {
        this.Ok_TOS = z;
    }

    public boolean isOk_Privacy() {
        return this.Ok_Privacy;
    }

    public void setOk_Privacy(boolean z) {
        this.Ok_Privacy = z;
    }

    public boolean isOk_Marketing() {
        return this.Ok_Marketing;
    }

    public void setOk_Marketing(boolean z) {
        this.Ok_Marketing = z;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long j) {
        this.id = j;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String str) {
        this.username = str;
    }

    public String getDeviceId() {
        return this.deviceId;
    }

    public void setDeviceId(String str) {
        this.deviceId = str;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String str) {
        this.password = str;
    }

    public String getLang() {
        return this.lang;
    }

    public void setLang(String str) {
        this.lang = str;
    }
}
