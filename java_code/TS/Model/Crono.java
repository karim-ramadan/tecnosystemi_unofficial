package it.tecnosystemi.TS.Model;

import com.google.gson.annotations.SerializedName;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.io.Serializable;
import org.json.JSONException;
import org.json.JSONObject;

public class Crono implements Serializable {
    @SerializedName("End")
    String endTime;
    @SerializedName("Start")
    String startTime;
    @SerializedName("Temp")
    String temperatura;

    public void normalizzaOrario() {
        String str = this.startTime;
        if (str != null && this.endTime != null && this.temperatura != null) {
            if (str.compareTo(Constants.COMMANDSENT) == 0 && this.endTime.compareTo(Constants.COMMANDSENT) == 0) {
                this.startTime = "";
                this.endTime = "";
                this.temperatura = "";
                return;
            }
            if (!this.startTime.contains(":")) {
                int parseInt = Integer.parseInt(this.startTime) * 15;
                setStartTime(String.format("%02d", new Object[]{Integer.valueOf(parseInt / 60)}) + ":" + String.format("%02d", new Object[]{Integer.valueOf(parseInt % 60)}));
            }
            if (!this.endTime.contains(":")) {
                int parseInt2 = Integer.parseInt(this.endTime) * 15;
                setEndTime(String.format("%02d", new Object[]{Integer.valueOf(parseInt2 / 60)}) + ":" + String.format("%02d", new Object[]{Integer.valueOf(parseInt2 % 60)}));
            }
            try {
                double parseDouble = Double.parseDouble(this.temperatura);
                this.temperatura = "" + (parseDouble / 10.0d);
            } catch (Exception unused) {
            }
        }
    }

    public int getEndtimeAsInt15() {
        try {
            String str = this.endTime;
            if (str == null) {
                return -1;
            }
            String[] split = str.split(":");
            return ((Integer.parseInt(split[0]) * 60) + Integer.parseInt(split[1])) / 15;
        } catch (Exception unused) {
            return -1;
        }
    }

    public int getStarttimeAsInt15() {
        try {
            String str = this.startTime;
            if (str == null) {
                return -1;
            }
            String[] split = str.split(":");
            return ((Integer.parseInt(split[0]) * 60) + Integer.parseInt(split[1])) / 15;
        } catch (Exception unused) {
            return -1;
        }
    }

    public JSONObject commandUpd() {
        try {
            JSONObject jSONObject = new JSONObject("{}");
            jSONObject.put(Constants.JSON_COMMAND_START, getStarttimeAsInt15());
            jSONObject.put(Constants.JSON_COMMAND_END, getEndtimeAsInt15());
            if (this.temperatura.contains("°")) {
                this.temperatura.replace("°", "");
            }
            jSONObject.put(Constants.JSON_COMMAND_TEMP, (int) (Double.parseDouble(this.temperatura) * 10.0d));
            return jSONObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JSONObject commandUpdVoid() {
        try {
            JSONObject jSONObject = new JSONObject("{}");
            jSONObject.put(Constants.JSON_COMMAND_START, 0);
            jSONObject.put(Constants.JSON_COMMAND_END, 0);
            jSONObject.put(Constants.JSON_COMMAND_TEMP, 240);
            return jSONObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean isok() {
        try {
            String str = this.startTime;
            if (str == null || this.endTime == null || this.temperatura == null) {
                if (str != null || this.endTime != null) {
                    return false;
                }
                if (this.temperatura != null) {
                    return false;
                }
            }
            if (str.isEmpty() || this.endTime.isEmpty() || this.temperatura.isEmpty()) {
                if (!this.startTime.isEmpty() || !this.endTime.isEmpty()) {
                    return false;
                }
                if (!this.temperatura.isEmpty()) {
                    return false;
                }
            }
            if (Functions.differencetime(this.startTime, this.endTime) <= 0) {
                return false;
            }
            Double.parseDouble(this.temperatura);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean isTempOK() {
        String str = this.temperatura;
        if (str != null && !str.isEmpty()) {
            return true;
        }
        return false;
    }

    public String getStartTime() {
        return this.startTime;
    }

    public void setStartTime(String str) {
        this.startTime = str;
    }

    public String getEndTime() {
        return this.endTime;
    }

    public void setEndTime(String str) {
        this.endTime = str;
    }

    public String getTemperatura() {
        return this.temperatura;
    }

    public void setTemperatura(String str) {
        this.temperatura = str;
    }

    public Crono copyforDemo() {
        Crono crono = new Crono();
        crono.setStartTime(getStartTime());
        crono.setEndTime(getEndTime());
        crono.setTemperatura(getTemperatura());
        return crono;
    }

    public boolean isEmpty() {
        try {
            if (getStartTime() == null) {
                setStartTime("");
            }
            if (getEndTime() == null) {
                setEndTime("");
            }
            if (getTemperatura() == null) {
                setTemperatura("");
            }
            if (!getTemperatura().isEmpty() || !getEndTime().isEmpty() || !getStartTime().isEmpty()) {
                return false;
            }
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public static Crono JsonOffline(JSONObject jSONObject) {
        Crono crono = new Crono();
        try {
            if (jSONObject.has(Constants.JSON_COMMAND_START)) {
                crono.setStartTime(jSONObject.getString(Constants.JSON_COMMAND_START));
            }
            if (jSONObject.has(Constants.JSON_COMMAND_END)) {
                crono.setEndTime(jSONObject.getString(Constants.JSON_COMMAND_END));
            }
            if (jSONObject.has(Constants.JSON_COMMAND_TEMP)) {
                crono.setTemperatura(jSONObject.getString(Constants.JSON_COMMAND_TEMP));
            }
        } catch (Exception unused) {
        }
        return crono;
    }

    public static String convertTimeToNormal(int i) {
        int i2 = i * 15;
        int i3 = i2 / 60;
        if (i3 == 24) {
            i3 = 0;
        }
        return String.format("%02d", new Object[]{Integer.valueOf(i3)}) + ":" + String.format("%02d", new Object[]{Integer.valueOf(i2 % 60)});
    }
}
