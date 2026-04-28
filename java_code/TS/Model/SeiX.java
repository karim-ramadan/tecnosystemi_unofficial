package it.tecnosystemi.TS.Model;

import android.app.Activity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.SavePreferences;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class SeiX {
    String IP;
    long Id;
    String LastAddTimezone;
    String Name;
    String Pin;
    String Serial;
    Boolean isOffline;
    Stato stato;

    public Stato getStato() {
        return this.stato;
    }

    public void setStato(Stato stato2) {
        this.stato = stato2;
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

    public String getIP() {
        return this.IP;
    }

    public void setIP(String str) {
        this.IP = str;
    }

    public Boolean getOffline() {
        return this.isOffline;
    }

    public void setOffline(Boolean bool) {
        this.isOffline = bool;
    }

    public String getLastAddTimezone() {
        return this.LastAddTimezone;
    }

    public void setLastAddTimezone(String str) {
        this.LastAddTimezone = str;
    }

    public static class Stato {
        private int BaseTop;
        private String Grd_DM;
        private String cmd;
        private int cntr;
        private int config_mod;
        private int cool_mod;
        private String date_e;
        int[][] err;
        private int f_est;
        private int f_inv;
        private String frm;
        private String fw_note;
        private String fw_ver;
        private int idp;
        private String ip;
        private int ir_present;
        private int is_cool;
        private int is_off;
        private int master_nr;
        private int maxcom;
        private long memfree;
        private int modello;
        private String name;
        private int res;
        private String reset;
        private String time;
        private String time_e;
        private long up_time;
        private String vfw;
        private int vr;
        private int w_rssi;
        private int week;
        List<Zona> z;
        private int[] zp;

        public List<Zona> getZ() {
            return this.z;
        }

        public void setZ(List<Zona> list) {
            this.z = list;
        }

        public int getBaseTop() {
            return this.BaseTop;
        }

        public void setBaseTop(int i) {
            this.BaseTop = i;
        }

        public String getCmd() {
            return this.cmd;
        }

        public void setCmd(String str) {
            this.cmd = str;
        }

        public int getCntr() {
            return this.cntr;
        }

        public void setCntr(int i) {
            this.cntr = i;
        }

        public int getConfig_mod() {
            return this.config_mod;
        }

        public void setConfig_mod(int i) {
            this.config_mod = i;
        }

        public int getCool_mod() {
            return this.cool_mod;
        }

        public void setCool_mod(int i) {
            this.cool_mod = i;
        }

        public String getDate_e() {
            return this.date_e;
        }

        public void setDate_e(String str) {
            this.date_e = str;
        }

        public int[][] getErr() {
            return this.err;
        }

        public void setErr(int[][] iArr) {
            this.err = iArr;
        }

        public int getF_est() {
            return this.f_est;
        }

        public void setF_est(int i) {
            this.f_est = i;
        }

        public int getF_inv() {
            return this.f_inv;
        }

        public void setF_inv(int i) {
            this.f_inv = i;
        }

        public String getFrm() {
            return this.frm;
        }

        public void setFrm(String str) {
            this.frm = str;
        }

        public String getFw_note() {
            return this.fw_note;
        }

        public void setFw_note(String str) {
            this.fw_note = str;
        }

        public String getFw_ver() {
            return this.fw_ver;
        }

        public void setFw_ver(String str) {
            this.fw_ver = str;
        }

        public String getGrd_DM() {
            return this.Grd_DM;
        }

        public void setGrd_DM(String str) {
            this.Grd_DM = str;
        }

        public int getIdp() {
            return this.idp;
        }

        public void setIdp(int i) {
            this.idp = i;
        }

        public String getIp() {
            return this.ip;
        }

        public void setIp(String str) {
            this.ip = str;
        }

        public int getIr_present() {
            return this.ir_present;
        }

        public void setIr_present(int i) {
            this.ir_present = i;
        }

        public int getIs_cool() {
            return this.is_cool;
        }

        public void setIs_cool(int i) {
            this.is_cool = i;
        }

        public int getIs_off() {
            return this.is_off;
        }

        public void setIs_off(int i) {
            this.is_off = i;
        }

        public int getMaster_nr() {
            return this.master_nr;
        }

        public void setMaster_nr(int i) {
            this.master_nr = i;
        }

        public int getMaxcom() {
            return this.maxcom;
        }

        public void setMaxcom(int i) {
            this.maxcom = i;
        }

        public long getMemfree() {
            return this.memfree;
        }

        public void setMemfree(long j) {
            this.memfree = j;
        }

        public int getModello() {
            return this.modello;
        }

        public void setModello(int i) {
            this.modello = i;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String str) {
            this.name = str;
        }

        public int getRes() {
            return this.res;
        }

        public void setRes(int i) {
            this.res = i;
        }

        public String getReset() {
            return this.reset;
        }

        public void setReset(String str) {
            this.reset = str;
        }

        public String getTime() {
            return this.time;
        }

        public void setTime(String str) {
            this.time = str;
        }

        public String getTime_e() {
            return this.time_e;
        }

        public void setTime_e(String str) {
            this.time_e = str;
        }

        public long getUp_time() {
            return this.up_time;
        }

        public void setUp_time(long j) {
            this.up_time = j;
        }

        public String getVfw() {
            return this.vfw;
        }

        public void setVfw(String str) {
            this.vfw = str;
        }

        public int getVr() {
            return this.vr;
        }

        public void setVr(int i) {
            this.vr = i;
        }

        public int getW_rssi() {
            return this.w_rssi;
        }

        public void setW_rssi(int i) {
            this.w_rssi = i;
        }

        public int getWeek() {
            return this.week;
        }

        public void setWeek(int i) {
            this.week = i;
        }

        public int[] getZp() {
            return this.zp;
        }

        public void setZp(int[] iArr) {
            this.zp = iArr;
        }
    }

    public static class Zona {
        private int c_b;
        private int c_w;
        private int err;
        private int is_off;
        private int m_crono;
        private int n;
        private String name;
        private int tw_active;

        public int getC_b() {
            return this.c_b;
        }

        public void setC_b(int i) {
            this.c_b = i;
        }

        public int getC_w() {
            return this.c_w;
        }

        public void setC_w(int i) {
            this.c_w = i;
        }

        public int getErr() {
            return this.err;
        }

        public void setErr(int i) {
            this.err = i;
        }

        public int getIs_off() {
            return this.is_off;
        }

        public void setIs_off(int i) {
            this.is_off = i;
        }

        public int getM_crono() {
            return this.m_crono;
        }

        public void setM_crono(int i) {
            this.m_crono = i;
        }

        public int getN() {
            return this.n;
        }

        public void setN(int i) {
            this.n = i;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String str) {
            this.name = str;
        }

        public int getTw_active() {
            return this.tw_active;
        }

        public void setTw_active(int i) {
            this.tw_active = i;
        }
    }

    public static SeiX get6XFromPref(String str, Activity activity) {
        String string = new SavePreferences(activity, activity.getString(R.string.PrefsName)).getString(Constants.PREF_6X_DEVS);
        if (string == null) {
            return null;
        }
        try {
            List list = (List) new Gson().fromJson(string, new TypeToken<List<SeiX>>() {
            }.getType());
            if (list == null) {
                return null;
            }
            for (int i = 0; i < list.size(); i++) {
                if (((SeiX) list.get(i)).getSerial().equals(str)) {
                    return (SeiX) list.get(i);
                }
            }
            return null;
        } catch (Exception unused) {
            return null;
        }
    }

    public static void delete6XfromPref(String str, BaseActivity baseActivity) {
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        String string = savePreferences.getString(Constants.PREF_6X_DEVS);
        if (string != null) {
            try {
                List list = (List) new Gson().fromJson(string, new TypeToken<List<SeiX>>() {
                }.getType());
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < list.size(); i++) {
                    if (!((SeiX) list.get(i)).getSerial().equals(str)) {
                        arrayList.add((SeiX) list.get(i));
                    }
                }
                savePreferences.save(Constants.PREF_6X_DEVS, new Gson().toJson((Object) arrayList));
            } catch (Exception unused) {
            }
        }
    }

    public static void deleteList6XfromPref(List<String> list, BaseActivity baseActivity) {
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        String string = savePreferences.getString(Constants.PREF_6X_DEVS);
        if (string != null) {
            try {
                List list2 = (List) new Gson().fromJson(string, new TypeToken<List<SeiX>>() {
                }.getType());
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < list2.size(); i++) {
                    if (!list.contains(((SeiX) list2.get(i)).getSerial())) {
                        arrayList.add((SeiX) list2.get(i));
                    }
                }
                savePreferences.save(Constants.PREF_6X_DEVS, new Gson().toJson((Object) arrayList));
            } catch (Exception unused) {
            }
        }
    }

    public static void save6XInPref(SeiX seiX, Activity activity) {
        save6XInPref(seiX.getName(), seiX.getSerial(), seiX.getPin(), seiX.getIP(), activity, seiX.getOffline().booleanValue(), false);
    }

    public static void save6XInPref(String str, String str2, String str3, String str4, Activity activity, boolean z, boolean z2) {
        SeiX seiX = new SeiX();
        seiX.setName(str);
        seiX.setSerial(str2);
        seiX.setOffline(Boolean.valueOf(z));
        seiX.setPin(str3);
        if (z2) {
            seiX.setLastAddTimezone(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date()));
        }
        SavePreferences savePreferences = new SavePreferences(activity, activity.getString(R.string.PrefsName));
        String string = savePreferences.getString(Constants.PREF_6X_DEVS);
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
            arrayList.add(new JSONObject(new Gson().toJson((Object) seiX)));
            Collections.sort(arrayList, new Comparator<JSONObject>() {
                public int compare(JSONObject jSONObject, JSONObject jSONObject2) {
                    try {
                        return ((String) jSONObject.get(Constants.JSON_CU_NAME)).compareTo((String) jSONObject2.get(Constants.JSON_CU_NAME));
                    } catch (Exception unused) {
                        return 0;
                    }
                }
            });
            savePreferences.save(Constants.PREF_6X_DEVS, new JSONArray(arrayList).toString());
        } catch (Exception unused) {
        }
    }
}
