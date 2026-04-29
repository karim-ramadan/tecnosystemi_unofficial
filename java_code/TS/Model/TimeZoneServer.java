package it.tecnosystemi.TS.Model;

public class TimeZoneServer {
    String DisplayName;
    String IdTimeZone;
    int isYours;

    public String getIdTimeZone() {
        return this.IdTimeZone;
    }

    public void setIdTimeZone(String str) {
        this.IdTimeZone = str;
    }

    public String getDisplayName() {
        return this.DisplayName;
    }

    public void setDisplayName(String str) {
        this.DisplayName = str;
    }

    public int getIsYours() {
        return this.isYours;
    }

    public void setIsYours(int i) {
        this.isYours = i;
    }
}
