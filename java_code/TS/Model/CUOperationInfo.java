package it.tecnosystemi.TS.Model;

import com.google.gson.annotations.SerializedName;

public class CUOperationInfo {
    @SerializedName("PIN")
    String PIN;
    String PlantId;
    @SerializedName("cuSerial")
    String cuSerial;
    @SerializedName("platform")
    String platform;
    @SerializedName("token")
    String token;
    @SerializedName("usrName")
    String usrName;

    public String getUsrName() {
        return this.usrName;
    }

    public void setUsrName(String str) {
        this.usrName = str;
    }

    public String getCuSerial() {
        return this.cuSerial;
    }

    public void setCuSerial(String str) {
        this.cuSerial = str;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String str) {
        this.token = str;
    }

    public String getPIN() {
        return this.PIN;
    }

    public void setPIN(String str) {
        this.PIN = str;
    }

    public String getPlatform() {
        return this.platform;
    }

    public void setPlatform(String str) {
        this.platform = str;
    }

    public String getPlantId() {
        return this.PlantId;
    }

    public void setPlantId(String str) {
        this.PlantId = str;
    }
}
