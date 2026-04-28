package it.tecnosystemi.TS.Commands;

public class PICOServer {
    private String Cmd;
    private String Name;
    private String Pin;
    private String Serial;

    public String getSerial() {
        return this.Serial;
    }

    public void setSerial(String str) {
        this.Serial = str;
    }

    public String getName() {
        return this.Name;
    }

    public void setName(String str) {
        this.Name = str;
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
