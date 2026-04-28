package it.tecnosystemi.TS.Model;

import android.app.Activity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Model.ModBusRecipe;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
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

public class VMC implements Serializable {
    String IP;
    long Id;
    String LastAddTimezone;
    String Name;
    String Pin;
    String Serial;
    ModBusRecipe.Param antigelo;
    ModBusRecipe.Param byPass;
    ModBusRecipe.Param cO2;
    ModBusRecipe.Param errorParam;
    int[][] errors;
    String fw_ver;
    ModBusRecipe.Param giorniPulizia;
    Boolean isOffline;
    String key_recipe;
    int m_crono;
    ModBusRecipe.Param modelloTaglia;
    ModBusRecipe.Param stagione;
    ModBusRecipe.Param statoFiltri;
    ModBusRecipe.Param tempAmb;
    ModBusRecipe.Param tempAspEst;
    ModBusRecipe.Param tempEspAria;
    ModBusRecipe.Param tempMand;
    ModBusRecipe.Param tempRipInt;
    ModBusRecipe.Param tempoTimer;
    String timezone;
    int tw_active;
    ModBusRecipe.Param velVentola;

    public String getKey_recipe() {
        return this.key_recipe;
    }

    public void setKey_recipe(String str) {
        this.key_recipe = str;
    }

    public String getFw_ver() {
        return this.fw_ver;
    }

    public void setFw_ver(String str) {
        this.fw_ver = str;
    }

    public String getTimezone() {
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

    public void setErrors(int[][] iArr) {
        this.errors = iArr;
    }

    public int[][] getErrors() {
        return this.errors;
    }

    public ModBusRecipe.Param getByPass() {
        return this.byPass;
    }

    public void setByPass(ModBusRecipe.Param param) {
        this.byPass = param;
    }

    public ModBusRecipe.Param getTempRipInt() {
        return this.tempRipInt;
    }

    public void setTempRipInt(ModBusRecipe.Param param) {
        this.tempRipInt = param;
    }

    public ModBusRecipe.Param getTempAmb() {
        return this.tempAmb;
    }

    public void setTempAmb(ModBusRecipe.Param param) {
        this.tempAmb = param;
    }

    public ModBusRecipe.Param getTempAspEst() {
        return this.tempAspEst;
    }

    public void setTempAspEst(ModBusRecipe.Param param) {
        this.tempAspEst = param;
    }

    public ModBusRecipe.Param getTempEspAria() {
        return this.tempEspAria;
    }

    public void setTempEspAria(ModBusRecipe.Param param) {
        this.tempEspAria = param;
    }

    public ModBusRecipe.Param getTempMand() {
        return this.tempMand;
    }

    public void setTempMand(ModBusRecipe.Param param) {
        this.tempMand = param;
    }

    public ModBusRecipe.Param getAntigelo() {
        return this.antigelo;
    }

    public void setAntigelo(ModBusRecipe.Param param) {
        this.antigelo = param;
    }

    public ModBusRecipe.Param getStatoFiltri() {
        return this.statoFiltri;
    }

    public void setStatoFiltri(ModBusRecipe.Param param) {
        this.statoFiltri = param;
    }

    public ModBusRecipe.Param getGiorniPulizia() {
        return this.giorniPulizia;
    }

    public void setGiorniPulizia(ModBusRecipe.Param param) {
        this.giorniPulizia = param;
    }

    public ModBusRecipe.Param getStagione() {
        return this.stagione;
    }

    public void setStagione(ModBusRecipe.Param param) {
        this.stagione = param;
    }

    public ModBusRecipe.Param getTempoTimer() {
        return this.tempoTimer;
    }

    public void setTempoTimer(ModBusRecipe.Param param) {
        this.tempoTimer = param;
    }

    public ModBusRecipe.Param getErrorParam() {
        return this.errorParam;
    }

    public void setErrorParam(ModBusRecipe.Param param) {
        this.errorParam = param;
    }

    public ModBusRecipe.Param getModelloTaglia() {
        return this.modelloTaglia;
    }

    public void setModelloTaglia(ModBusRecipe.Param param) {
        this.modelloTaglia = param;
    }

    public ModBusRecipe.Param getcO2() {
        return this.cO2;
    }

    public void setcO2(ModBusRecipe.Param param) {
        this.cO2 = param;
    }

    public ModBusRecipe.Param getVelVentola() {
        return this.velVentola;
    }

    public void setVelVentola(ModBusRecipe.Param param) {
        this.velVentola = param;
    }

    public String getLastAddTimezone() {
        return this.LastAddTimezone;
    }

    public void setLastAddTimezone(String str) {
        this.LastAddTimezone = str;
    }

    public String getIP() {
        return this.IP;
    }

    public void setIP(String str) {
        this.IP = str;
    }

    public Boolean getOffline() {
        Boolean bool = this.isOffline;
        if (bool == null) {
            return false;
        }
        return bool;
    }

    public void setOffline(Boolean bool) {
        this.isOffline = bool;
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

    public static VMC getVMCFromPref(String str, Activity activity) {
        String string = new SavePreferences(activity, activity.getString(R.string.PrefsName)).getString(Constants.PREF_VMC_DEVS);
        if (string == null) {
            return null;
        }
        try {
            List list = (List) new Gson().fromJson(string, new TypeToken<List<VMC>>() {
            }.getType());
            if (list == null) {
                return null;
            }
            for (int i = 0; i < list.size(); i++) {
                if (((VMC) list.get(i)).getSerial().equals(str)) {
                    return (VMC) list.get(i);
                }
            }
            return null;
        } catch (Exception unused) {
            return null;
        }
    }

    public static void deleteVMCfromPref(String str, BaseActivity baseActivity) {
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        String string = savePreferences.getString(Constants.PREF_VMC_DEVS);
        if (string != null) {
            try {
                List list = (List) new Gson().fromJson(string, new TypeToken<List<VMC>>() {
                }.getType());
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < list.size(); i++) {
                    if (!((VMC) list.get(i)).getSerial().equals(str)) {
                        arrayList.add((VMC) list.get(i));
                    }
                }
                savePreferences.save(Constants.PREF_VMC_DEVS, new Gson().toJson((Object) arrayList));
            } catch (Exception unused) {
            }
        }
    }

    public static void deleteListVMCfromPref(List<String> list, BaseActivity baseActivity) {
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        String string = savePreferences.getString(Constants.PREF_VMC_DEVS);
        if (string != null) {
            try {
                List list2 = (List) new Gson().fromJson(string, new TypeToken<List<VMC>>() {
                }.getType());
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < list2.size(); i++) {
                    if (!list.contains(((VMC) list2.get(i)).getSerial())) {
                        arrayList.add((VMC) list2.get(i));
                    }
                }
                savePreferences.save(Constants.PREF_VMC_DEVS, new Gson().toJson((Object) arrayList));
            } catch (Exception unused) {
            }
        }
    }

    public static void saveVMCInPref(VMC vmc, Activity activity) {
        saveVMCInPref(vmc.getName(), vmc.getSerial(), vmc.getPin(), vmc.getIP(), activity, vmc.getOffline().booleanValue(), false);
    }

    public static void saveVMCInPref(String str, String str2, String str3, String str4, Activity activity, boolean z, boolean z2) {
        VMC vmc = new VMC();
        vmc.setName(str);
        vmc.setSerial(str2);
        vmc.setOffline(Boolean.valueOf(z));
        vmc.setPin(str3);
        if (z2) {
            vmc.setLastAddTimezone(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date()));
        }
        SavePreferences savePreferences = new SavePreferences(activity, activity.getString(R.string.PrefsName));
        String string = savePreferences.getString(Constants.PREF_VMC_DEVS);
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
            arrayList.add(new JSONObject(new Gson().toJson((Object) vmc)));
            Collections.sort(arrayList, new Comparator<JSONObject>() {
                public int compare(JSONObject jSONObject, JSONObject jSONObject2) {
                    try {
                        return ((String) jSONObject.get(Constants.JSON_CU_NAME)).compareTo((String) jSONObject2.get(Constants.JSON_CU_NAME));
                    } catch (Exception unused) {
                        return 0;
                    }
                }
            });
            savePreferences.save(Constants.PREF_VMC_DEVS, new JSONArray(arrayList).toString());
        } catch (Exception unused) {
        }
    }
}
