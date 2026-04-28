package it.tecnosystemi.TS.Commands;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Cmd {

    public class BaseRequestCommand {
        @SerializedName("c")
        @Expose
        public String command;
        @SerializedName("pin")
        public String pin;

        public BaseRequestCommand() {
        }
    }

    public class ConfigCuOffline extends BaseRequestCommand {
        @SerializedName("offline")
        @Expose
        public int offline;

        public ConfigCuOffline() {
            super();
        }
    }

    public class ConfigCuOnLine extends ConfigCuOffline {
        @SerializedName("wifi_mac")
        public String mac;
        @SerializedName("wifi_pwd")
        public String pwd;
        @SerializedName("wifi_sec")
        @Expose
        public int sec;
        @SerializedName("wifi_ssid")
        public String sid;

        public ConfigCuOnLine() {
            super();
        }
    }

    public class GetWifi extends BaseRequestCommand {
        public GetWifi() {
            super();
        }
    }

    public class CheckPin extends BaseRequestCommand {
        public CheckPin() {
            super();
        }
    }

    public class RGetWifi {
        @SerializedName("aps")
        public ApsElem[] aps;
        @SerializedName("c")
        public String command;

        public RGetWifi() {
        }
    }

    public class ApsElem {
        @SerializedName("id")
        public String id;
        @SerializedName("sec")
        public int sec;

        public ApsElem() {
        }
    }
}
