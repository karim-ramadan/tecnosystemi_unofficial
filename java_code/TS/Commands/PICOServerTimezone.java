package it.tecnosystemi.TS.Commands;

public class PICOServerTimezone {
    private String Cmd;
    private String Pin;
    private String Serial;
    private String Timezone;

    public String getSerial() {
        return this.Serial;
    }

    public void setSerial(String str) {
        this.Serial = str;
    }

    public String getTimezone() {
        return this.Timezone;
    }

    public void setTimezone(String str) {
        this.Timezone = str;
    }

    public String getPin() {
        return this.Pin;
    }

    public void setPin(String str) {
        this.Pin = str;
    }

    public String getCmd() {
        return this.Cmd;
    }

    public void setCmd(String str) {
        this.Cmd = str;
    }
}
