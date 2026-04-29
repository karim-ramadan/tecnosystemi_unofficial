package it.tecnosystemi.TS.Model;

import android.app.Activity;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import it.tecnosystemi.TS.Utils.SavePreferences;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class Pico implements Serializable {
    String Fw_ver;
    String IP;
    Integer Icon;
    long Id;
    Boolean IsDev;
    Boolean IsOff;
    String LastAddPC;
    String LastAddTimezone;
    String LastConfigUpd;
    String LastStateUpdSent;
    String LastSyncUpd;
    String Name;
    String Note;
    Long PCOM_Id;
    String Pin;
    Boolean SaveMQTTLog;
    String Serial;
    Integer WRSSI;
    int[][] err;
    int has_slave;
    int humvel;
    Boolean isOffline;
    int led;
    int m_crono;
    int[] man;
    boolean night;
    List<Integer> par_amb;
    List<Integer> par_ext;
    List<Slave> picoSlave;
    int spd_rich;
    int speed_raw;
    String timezone;
    int tw_active;
    int ventvel;
    int vr = 0;

    public static class Result {
        public static final int OK = 1;
    }

    public String getTimezone() {
        if (this.timezone.equalsIgnoreCase("null")) {
            return null;
        }
        return this.timezone;
    }

    public void setTimezone(String str) {
        this.timezone = str;
    }

    public int getM_crono() {
        return this.m_crono;
    }

    public void setM_crono(int i) {
        this.m_crono = i;
    }

    public int getTw_active() {
        return this.tw_active;
    }

    public void setTw_active(int i) {
        this.tw_active = i;
    }

    public List<Integer> getPar_amb() {
        return this.par_amb;
    }

    public void setPar_amb(List<Integer> list) {
        this.par_amb = list;
    }

    public List<Integer> getPar_ext() {
        return this.par_ext;
    }

    public void setPar_ext(List<Integer> list) {
        this.par_ext = list;
    }

    public String getLastAddTimezone() {
        return this.LastAddTimezone;
    }

    public void setLastAddTimezone(String str) {
        this.LastAddTimezone = str;
    }

    public int getSpd_rich() {
        return this.spd_rich;
    }

    public void setSpd_rich(int i) {
        this.spd_rich = i;
    }

    public boolean isNight() {
        return this.night;
    }

    public void setNight(boolean z) {
        this.night = z;
    }

    public long getId() {
        return this.Id;
    }

    public void setId(long j) {
        this.Id = j;
    }

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

    public Boolean getOff() {
        return this.IsOff;
    }

    public void setOff(Boolean bool) {
        this.IsOff = bool;
    }

    public Long getPCOM_Id() {
        return this.PCOM_Id;
    }

    public void setPCOM_Id(Long l) {
        this.PCOM_Id = l;
    }

    public String getLastSyncUpd() {
        return this.LastSyncUpd;
    }

    public void setLastSyncUpd(String str) {
        this.LastSyncUpd = str;
    }

    public String getLastConfigUpd() {
        return this.LastConfigUpd;
    }

    public void setLastConfigUpd(String str) {
        this.LastConfigUpd = str;
    }

    public Integer getIcon() {
        return this.Icon;
    }

    public void setIcon(Integer num) {
        this.Icon = num;
    }

    public String getIP() {
        return this.IP;
    }

    public void setIP(String str) {
        this.IP = str;
    }

    public String getLastAddPC() {
        return this.LastAddPC;
    }

    public void setLastAddPC(String str) {
        this.LastAddPC = str;
    }

    public Integer getWRSSI() {
        return this.WRSSI;
    }

    public void setWRSSI(Integer num) {
        this.WRSSI = num;
    }

    public String getNote() {
        return this.Note;
    }

    public void setNote(String str) {
        this.Note = str;
    }

    public String getLastStateUpdSent() {
        return this.LastStateUpdSent;
    }

    public void setLastStateUpdSent(String str) {
        this.LastStateUpdSent = str;
    }

    public Boolean getSaveMQTTLog() {
        return this.SaveMQTTLog;
    }

    public void setSaveMQTTLog(Boolean bool) {
        this.SaveMQTTLog = bool;
    }

    public Boolean getDev() {
        return this.IsDev;
    }

    public void setDev(Boolean bool) {
        this.IsDev = bool;
    }

    public int getLed() {
        return this.led;
    }

    public void setLed(int i) {
        this.led = i;
    }

    public int getHumvel() {
        return this.humvel;
    }

    public void setHumvel(int i) {
        this.humvel = i;
    }

    public int getVentvel() {
        return this.ventvel;
    }

    public void setVentvel(int i) {
        this.ventvel = i;
        this.speed_raw = 0;
    }

    public int getSpeed_raw() {
        return this.speed_raw;
    }

    public void setSpeed_raw(int i) {
        this.speed_raw = i;
        this.ventvel = 0;
    }

    public Boolean getOffline() {
        return this.isOffline;
    }

    public void setOffline(Boolean bool) {
        this.isOffline = bool;
    }

    public int getVr() {
        return this.vr;
    }

    public void setVr(int i) {
        this.vr = i;
    }

    public int[] getMan() {
        return this.man;
    }

    public void setMan(int[] iArr) {
        this.man = iArr;
    }

    public int[][] getErr() {
        return this.err;
    }

    public void setErr(int[][] iArr) {
        this.err = iArr;
    }

    public List<Slave> getPicoSlave() {
        return this.picoSlave;
    }

    public void setPicoSlave(List<Slave> list) {
        this.picoSlave = list;
    }

    public int getHas_slave() {
        return this.has_slave;
    }

    public void setHas_slave(int i) {
        this.has_slave = i;
    }

    public String getFw_ver() {
        return this.Fw_ver;
    }

    public void setFw_ver(String str) {
        this.Fw_ver = str;
    }

    public void resetMan() {
        int i = 0;
        while (true) {
            int[] iArr = this.man;
            if (i < iArr.length) {
                if (iArr[i] == 1) {
                    iArr[i] = 0;
                }
                i++;
            } else {
                return;
            }
        }
    }

    public Pico getCopy() {
        Pico pico = new Pico();
        pico.setId(this.Id);
        pico.setSerial(this.Serial);
        pico.setName(this.Name);
        pico.setPin(this.Pin);
        pico.setFw_ver(getFw_ver());
        pico.setOff(this.IsOff);
        pico.setPCOM_Id(this.PCOM_Id);
        pico.setLastSyncUpd(this.LastSyncUpd);
        pico.setLastConfigUpd(this.LastConfigUpd);
        pico.setIcon(this.Icon);
        pico.setIP(this.IP);
        pico.setLastAddPC(this.LastAddPC);
        pico.setWRSSI(this.WRSSI);
        pico.setNote(this.Note);
        pico.setLastStateUpdSent(this.LastStateUpdSent);
        pico.setSaveMQTTLog(this.SaveMQTTLog);
        pico.setDev(this.IsDev);
        pico.setOffline(this.isOffline);
        pico.setHumvel(this.humvel);
        pico.setVentvel(this.ventvel);
        pico.setLed(this.led);
        pico.setVr(this.vr);
        pico.setMan(this.man);
        pico.setErr(this.err);
        pico.setSpeed_raw(this.speed_raw);
        pico.setNight(this.night);
        pico.setSpd_rich(this.spd_rich);
        pico.setHas_slave(this.has_slave);
        pico.setTimezone(this.timezone);
        pico.setM_crono(this.m_crono);
        pico.setTw_active(this.tw_active);
        pico.setPar_amb(this.par_amb);
        pico.setPar_ext(this.par_ext);
        return pico;
    }

    public boolean receivedSync(String str) {
        String str2 = "par_amb";
        String str3 = "tw_active";
        String str4 = "m_crono";
        String str5 = "timezone";
        String str6 = NotificationCompat.CATEGORY_ERROR;
        String str7 = "night_mod";
        try {
            String str8 = "man";
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.has("fw_ver")) {
                setFw_ver(jSONObject.getString("fw_ver"));
            }
            if (jSONObject.has("on_off")) {
                setOff(Boolean.valueOf(jSONObject.getInt("on_off") == 2));
            }
            if (jSONObject.has("mod")) {
                setPCOM_Id(Long.valueOf(jSONObject.getLong("mod")));
            }
            if (jSONObject.has("speed")) {
                this.ventvel = jSONObject.getInt("speed");
            }
            if (jSONObject.has("spd_row")) {
                this.speed_raw = jSONObject.getInt("spd_row");
            }
            if (jSONObject.has("spd_rich")) {
                this.spd_rich = jSONObject.getInt("spd_rich");
            }
            if (jSONObject.has("umd")) {
                setHumvel(jSONObject.getInt("umd"));
            }
            if (jSONObject.has("s_umd")) {
                setHumvel(jSONObject.getInt("s_umd"));
            }
            if (jSONObject.has("led_on_off")) {
                setLed(jSONObject.getInt("led_on_off"));
            }
            if (jSONObject.has("led")) {
                setLed(jSONObject.getInt("led"));
            }
            if (jSONObject.has("led_on_off_breve")) {
                setLed(jSONObject.getInt("led_on_off_breve"));
            }
            if (jSONObject.has("vr")) {
                setVr(jSONObject.getInt("vr"));
            }
            if (jSONObject.has("has_slave")) {
                setHas_slave(jSONObject.getInt("has_slave"));
            }
            String str9 = str8;
            if (jSONObject.has(str9)) {
                try {
                    setMan(Functions.getIntArray(jSONObject.getJSONArray(str9)));
                } catch (Exception unused) {
                }
            }
            String str10 = str7;
            if (jSONObject.has(str10)) {
                try {
                    setNight(jSONObject.getInt(str10) == 1);
                } catch (Exception unused2) {
                }
            }
            String str11 = str6;
            if (jSONObject.has(str11)) {
                try {
                    JSONArray jSONArray = jSONObject.getJSONArray(str11);
                    int length = jSONArray.length();
                    int[][] iArr = new int[length][];
                    for (int i = 0; i < length; i++) {
                        iArr[i] = Functions.getIntArray(jSONArray.getJSONArray(i));
                    }
                    setErr(iArr);
                } catch (Exception unused3) {
                }
            }
            String str12 = str5;
            if (jSONObject.has(str12)) {
                setTimezone(jSONObject.getString(str12));
            } else {
                setTimezone("");
            }
            String str13 = str4;
            if (jSONObject.has(str13)) {
                setM_crono(jSONObject.getInt(str13));
            }
            String str14 = str3;
            if (jSONObject.has(str14)) {
                setTw_active(jSONObject.getInt(str14));
            }
            String str15 = str2;
            if (jSONObject.has(str15)) {
                try {
                    JSONArray jSONArray2 = jSONObject.getJSONArray(str15);
                    ArrayList arrayList = new ArrayList();
                    for (int i2 = 0; i2 < jSONArray2.length(); i2++) {
                        arrayList.add(Integer.valueOf(jSONArray2.getInt(i2)));
                    }
                    setPar_amb(arrayList);
                } catch (Exception unused4) {
                }
            }
            if (jSONObject.has("par_ext")) {
                try {
                    JSONArray jSONArray3 = jSONObject.getJSONArray("par_ext");
                    ArrayList arrayList2 = new ArrayList();
                    for (int i3 = 0; i3 < jSONArray3.length(); i3++) {
                        arrayList2.add(Integer.valueOf(jSONArray3.getInt(i3)));
                    }
                    setPar_ext(arrayList2);
                } catch (Exception unused5) {
                }
            }
            return true;
        } catch (Exception e) {
            Log.d("TAG", e.toString());
            return false;
        }
    }

    public static Pico getPICOFromPref(String str, Activity activity) {
        String string = new SavePreferences(activity, activity.getString(R.string.PrefsName)).getString(Constants.PREF_PICO_DEVS);
        if (string == null) {
            return null;
        }
        try {
            List list = (List) new Gson().fromJson(string, new TypeToken<List<Pico>>() {
            }.getType());
            if (list == null) {
                return null;
            }
            for (int i = 0; i < list.size(); i++) {
                if (((Pico) list.get(i)).getSerial().equals(str)) {
                    return (Pico) list.get(i);
                }
            }
            return null;
        } catch (Exception unused) {
            return null;
        }
    }

    public static void deletePICOfromPref(String str, BaseActivity baseActivity) {
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        String string = savePreferences.getString(Constants.PREF_PICO_DEVS);
        if (string != null) {
            try {
                List list = (List) new Gson().fromJson(string, new TypeToken<List<Pico>>() {
                }.getType());
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < list.size(); i++) {
                    if (!((Pico) list.get(i)).getSerial().equals(str)) {
                        arrayList.add((Pico) list.get(i));
                    }
                }
                savePreferences.save(Constants.PREF_PICO_DEVS, new Gson().toJson((Object) arrayList));
            } catch (Exception unused) {
            }
        }
    }

    public static void deleteListPICOfromPref(List<String> list, BaseActivity baseActivity) {
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        String string = savePreferences.getString(Constants.PREF_PICO_DEVS);
        if (string != null) {
            try {
                List list2 = (List) new Gson().fromJson(string, new TypeToken<List<Pico>>() {
                }.getType());
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < list2.size(); i++) {
                    if (!list.contains(((Pico) list2.get(i)).getSerial())) {
                        arrayList.add((Pico) list2.get(i));
                    }
                }
                savePreferences.save(Constants.PREF_PICO_DEVS, new Gson().toJson((Object) arrayList));
            } catch (Exception unused) {
            }
        }
    }

    public static void savePicoInPref(Pico pico, Activity activity) {
        savePicoInPref(pico.getName(), pico.getSerial(), pico.getPin(), pico.getIP(), activity, pico.getOffline().booleanValue(), false);
    }

    public static void savePicoInPref(String str, String str2, String str3, String str4, Activity activity, boolean z, boolean z2) {
        Pico pico = new Pico();
        pico.setName(str);
        pico.setSerial(str2);
        pico.setOffline(Boolean.valueOf(z));
        pico.setPin(str3);
        if (z2) {
            pico.setLastAddTimezone(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date()));
        }
        SavePreferences savePreferences = new SavePreferences(activity, activity.getString(R.string.PrefsName));
        String string = savePreferences.getString(Constants.PREF_PICO_DEVS);
        try {
            if (string.isEmpty()) {
                string = "[]";
            }
            JSONArray jSONArray = new JSONArray(string);
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < jSONArray.length(); i++) {
                if (!str2.equals(jSONArray.getJSONObject(i).getString("Serial"))) {
                    arrayList.add(jSONArray.getJSONObject(i));
                }
            }
            arrayList.add(new JSONObject(new Gson().toJson((Object) pico)));
            Collections.sort(arrayList, new Comparator<JSONObject>() {
                public int compare(JSONObject jSONObject, JSONObject jSONObject2) {
                    try {
                        return ((String) jSONObject.get(Constants.JSON_CU_NAME)).compareTo((String) jSONObject2.get(Constants.JSON_CU_NAME));
                    } catch (Exception unused) {
                        return 0;
                    }
                }
            });
            savePreferences.save(Constants.PREF_PICO_DEVS, new JSONArray(arrayList).toString());
        } catch (Exception unused) {
        }
    }

    public static class Slave {
        public static int VERSOASYNC = 2;
        public static int VERSOSYNC = 1;
        int flag;
        int id_slave;
        String ip;
        int mode;
        String name;
        int set_stato;
        int verso;

        public String getIp() {
            return this.ip;
        }

        public void setIp(String str) {
            this.ip = str;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String str) {
            this.name = str;
        }

        public int getId_slave() {
            return this.id_slave;
        }

        public void setId_slave(int i) {
            this.id_slave = i;
        }

        public int getMode() {
            return this.mode;
        }

        public void setMode(int i) {
            this.mode = i;
        }

        public int getVerso() {
            return this.verso;
        }

        public void setVerso(int i) {
            this.verso = i;
        }

        public int getFlag() {
            return this.flag;
        }

        public void setFlag(int i) {
            this.flag = i;
        }

        public int getSet_stato() {
            return this.set_stato;
        }

        public void setSet_stato(int i) {
            this.set_stato = i;
        }

        public Slave Clone() {
            Slave slave = new Slave();
            slave.setId_slave(this.id_slave);
            slave.setName(this.name);
            slave.setVerso(this.verso);
            slave.setMode(this.mode);
            slave.setIp(this.ip);
            slave.setFlag(this.flag);
            slave.setSet_stato(this.set_stato);
            return slave;
        }

        public ForSet getSlaveForSet() {
            ForSet forSet = new ForSet();
            forSet.setId_slave(this.id_slave);
            forSet.setVerso(this.verso);
            forSet.setName(this.name);
            forSet.setSet_stato(this.set_stato);
            return forSet;
        }

        public static class ForSet {
            int id_slave;
            String name;
            int set_stato;
            int verso;

            public String getName() {
                return this.name;
            }

            public void setName(String str) {
                this.name = str;
            }

            public int getId_slave() {
                return this.id_slave;
            }

            public void setId_slave(int i) {
                this.id_slave = i;
            }

            public int getVerso() {
                return this.verso;
            }

            public void setVerso(int i) {
                this.verso = i;
            }

            public int getSet_stato() {
                return this.set_stato;
            }

            public void setSet_stato(int i) {
                this.set_stato = i;
            }
        }
    }
}
