package it.tecnosystemi.TS.Model;

public class Device_OP {
    String PIN;
    String PhoneDate;
    long PlantId;
    String Serial;
    boolean isPico;
    String platform;
    String token;
    int type_dev;

    public int getType_dev() {
        return this.type_dev;
    }

    public void setType_dev(int i) {
        this.type_dev = i;
    }

    public boolean isPico() {
        return this.isPico;
    }

    public void setPico(boolean z) {
        this.isPico = z;
    }

    public String getSerial() {
        return this.Serial;
    }

    public void setSerial(String str) {
        this.Serial = str;
    }

    public String getPIN() {
        return this.PIN;
    }

    public void setPIN(String str) {
        this.PIN = str;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String str) {
        this.token = str;
    }

    public String getPlatform() {
        return this.platform;
    }

    public void setPlatform(String str) {
        this.platform = str;
    }

    public long getPlantId() {
        return this.PlantId;
    }

    public void setPlantId(long j) {
        this.PlantId = j;
    }

    public String getPhoneDate() {
        return this.PhoneDate;
    }

    public void setPhoneDate(String str) {
        this.PhoneDate = str;
    }

    public static class DeviceOp {
        long deviceID;
        String platform;
        String token;

        public long getDeviceID() {
            return this.deviceID;
        }

        public void setDeviceID(long j) {
            this.deviceID = j;
        }

        public String getToken() {
            return this.token;
        }

        public void setToken(String str) {
            this.token = str;
        }

        public String getPlatform() {
            return this.platform;
        }

        public void setPlatform(String str) {
            this.platform = str;
        }
    }
}
