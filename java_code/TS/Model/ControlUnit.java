package it.tecnosystemi.TS.Model;

import android.app.Activity;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.SavePreferences;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ControlUnit implements Serializable {
    private String FWVer;
    private boolean IsCooling;
    private int NumError;
    private int OperatingModeCooling;
    private int day;
    private boolean[] errors;
    private int f_est;
    private int f_inv;
    private int h24;
    private int icontype;
    private String ip;
    private int ir_present;
    private boolean isOff;
    private boolean isOffline;
    private String lastConfigUpdate;
    private String lastSyncUpdate;
    private int master;
    private int minuti;
    private String name;
    private int ore;
    private String pin;
    private String pinOffline;
    private String serial;
    private int t_can;
    private int unitOfMesure;
    private List<Zona> zone;

    public ControlUnit(String str) {
        this.icontype = -1;
        this.t_can = 0;
        this.NumError = 0;
        this.f_inv = -1;
        this.f_est = -1;
        this.name = str.toUpperCase();
    }

    public ControlUnit() {
        this.icontype = -1;
        this.t_can = 0;
        this.NumError = 0;
        this.f_inv = -1;
        this.f_est = -1;
        this.zone = new ArrayList();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str.toUpperCase();
    }

    public List<Zona> getZone() {
        return this.zone;
    }

    public void setZone(List<Zona> list) {
        this.zone = list;
    }

    public String getSerial() {
        return this.serial;
    }

    public void setSerial(String str) {
        this.serial = str;
    }

    public String getFWVer() {
        return this.FWVer;
    }

    public void setFWVer(String str) {
        this.FWVer = str;
    }

    public boolean getIsOff() {
        return this.isOff;
    }

    public void setIsOff(boolean z) {
        this.isOff = z;
    }

    public int getOperatingMode() {
        return this.OperatingModeCooling;
    }

    public void setOperatingMode(int i) {
        this.OperatingModeCooling = i;
    }

    public boolean[] getErrors() {
        return this.errors;
    }

    public void setErrors(boolean[] zArr) {
        this.errors = zArr;
    }

    public String getPin() {
        return this.pin;
    }

    public void setPin(String str) {
        this.pin = str;
    }

    public int getUnitOfMesure() {
        return this.unitOfMesure;
    }

    public void setUnitOfMesure(int i) {
        this.unitOfMesure = i;
    }

    public boolean getIsCooling() {
        return this.IsCooling;
    }

    public void setIsCooling(boolean z) {
        this.IsCooling = z;
    }

    public int getNumError() {
        return this.NumError;
    }

    public void setNumError(int i) {
        this.NumError = i;
    }

    public String getPinOffline() {
        return this.pinOffline;
    }

    public void setPinOffline(String str) {
        this.pinOffline = str;
    }

    public boolean isOffline() {
        return this.isOffline;
    }

    public void setOffline(boolean z) {
        this.isOffline = z;
    }

    public String getIp() {
        return this.ip;
    }

    public void setIp(String str) {
        this.ip = str;
    }

    public int getMaster() {
        return this.master;
    }

    public void setMaster(int i) {
        this.master = i;
    }

    public String getLastConfigUpdate() {
        return this.lastConfigUpdate;
    }

    public void setLastConfigUpdate(String str) {
        this.lastConfigUpdate = str;
    }

    public String getLastSyncUpdate() {
        return this.lastSyncUpdate;
    }

    public void setLastSyncUpdate(String str) {
        this.lastSyncUpdate = str;
    }

    public int getOre() {
        return this.ore;
    }

    public void setOre(int i) {
        this.ore = i;
    }

    public int getMinuti() {
        return this.minuti;
    }

    public void setMinuti(int i) {
        this.minuti = i;
    }

    public int getH24() {
        return this.h24;
    }

    public void setH24(int i) {
        this.h24 = i;
    }

    public int getDay() {
        return this.day;
    }

    public void setDay(int i) {
        this.day = i;
    }

    public int getF_inv() {
        return this.f_inv;
    }

    public void setF_inv(int i) {
        this.f_inv = i;
    }

    public int getF_est() {
        return this.f_est;
    }

    public void setF_est(int i) {
        this.f_est = i;
    }

    public String getStringerrors(String[] strArr) {
        String str = "";
        if (this.errors == null) {
            return str;
        }
        int i = 0;
        while (true) {
            boolean[] zArr = this.errors;
            if (i >= zArr.length) {
                return str;
            }
            if (zArr[i]) {
                if (str.isEmpty()) {
                    str = strArr[i];
                } else {
                    str = str + ", " + strArr[i];
                }
            }
            i++;
        }
    }

    public int getIcontype() {
        return this.icontype;
    }

    public void setIcontype(int i) {
        this.icontype = i;
    }

    public int getIr_present() {
        return this.ir_present;
    }

    public void setIr_present(int i) {
        this.ir_present = i;
    }

    public int getT_can() {
        return this.t_can;
    }

    public void setT_can(int i) {
        this.t_can = i;
    }

    public JSONObject cuLocalJson() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put(Constants.JSON_CU_NAME, getName());
            jSONObject.put(Constants.JSON_CU_SERIAL, getSerial());
            jSONObject.put(Constants.JSON_CU_PIN, getPin());
            jSONObject.put(Constants.JSON_CU_PINOFF, getPinOffline());
            jSONObject.put(Constants.JSON_CU_ISOFFLINE, isOffline());
            jSONObject.put(Constants.JSON_CU_IP, getIp());
            jSONObject.put(Constants.JSON_CU_UNIMESURE, getUnitOfMesure());
            jSONObject.put(Constants.JSON_CU_H24, getH24());
            jSONObject.put(Constants.JSON_CU_ICON, getIcontype());
            jSONObject.put(Constants.JSON_CU_FINV, getF_inv());
            jSONObject.put(Constants.JSON_CU_FEST, getF_est());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jSONObject;
    }

    public String update_CU_command() {
        try {
            JSONObject jSONObject = new JSONObject("{\"c\":\"upd_cu\"}");
            if (this.isOffline) {
                jSONObject.put(Constants.JSON_OFFLINE_COMMAND_PIN, getPinOffline());
            } else {
                jSONObject.put(Constants.JSON_OFFLINE_COMMAND_PIN, getPin());
            }
            jSONObject.put(Constants.JSON_OFFLINE_COMMAND_ISOFF, getIsOff() ? 1 : 0);
            jSONObject.put(Constants.JSON_OFFLINE_COMMAND_ISCOOL, getIsCooling() ? 1 : 0);
            jSONObject.put(Constants.JSON_OFFLINE_COMMAND_COOLMOD, getOperatingMode());
            jSONObject.put(Constants.JSON_OFFLINE_COMMAND_TCAN, getT_can() * 10);
            jSONObject.put(Constants.JSON_OFFLINE_COMMAND_FINV, getF_inv());
            jSONObject.put(Constants.JSON_OFFLINE_COMMAND_FEST, getF_est());
            return jSONObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean equals(Object obj) {
        if (obj instanceof ControlUnit) {
            return ((ControlUnit) obj).getSerial().equals(getSerial());
        }
        return false;
    }

    public ControlUnit makeTempCopy() {
        ControlUnit controlUnit = new ControlUnit();
        controlUnit.setOffline(controlUnit.isOffline());
        controlUnit.setFWVer(controlUnit.getFWVer());
        controlUnit.setZone(getZone());
        controlUnit.setName(getName());
        controlUnit.setIsCooling(getIsCooling());
        controlUnit.setIsOff(getIsOff());
        controlUnit.setOperatingMode(getOperatingMode());
        controlUnit.setZone(getZone());
        controlUnit.setPin(getPin());
        controlUnit.setPinOffline(getPinOffline());
        controlUnit.setIr_present(getIr_present());
        controlUnit.setT_can(getT_can());
        controlUnit.setZone(getZone());
        controlUnit.setF_est(getF_est());
        controlUnit.setF_inv(getF_inv());
        return controlUnit;
    }

    public ControlUnit makeIntentCopy() {
        ControlUnit controlUnit = new ControlUnit();
        controlUnit.setName(getName());
        controlUnit.setSerial(getSerial());
        controlUnit.setPin(getPin());
        return controlUnit;
    }

    public static ControlUnit setCULocal(JSONObject jSONObject) {
        ControlUnit controlUnit = new ControlUnit();
        try {
            controlUnit.setName(jSONObject.getString(Constants.JSON_CU_NAME).toUpperCase());
            controlUnit.setSerial(jSONObject.getString(Constants.JSON_CU_SERIAL));
            controlUnit.setOffline(jSONObject.getBoolean(Constants.JSON_CU_ISOFFLINE));
            if (jSONObject.has(Constants.JSON_CU_PIN) || jSONObject.has(Constants.JSON_CU_PINOFF)) {
                if (!controlUnit.isOffline) {
                    controlUnit.setPin(jSONObject.getString(Constants.JSON_CU_PIN));
                } else {
                    controlUnit.setPinOffline(jSONObject.getString(Constants.JSON_CU_PINOFF));
                }
            }
            if (jSONObject.has(Constants.JSON_CU_IP)) {
                controlUnit.setIp(jSONObject.getString(Constants.JSON_CU_IP));
            }
            controlUnit.setUnitOfMesure(jSONObject.getInt(Constants.JSON_CU_UNIMESURE));
            if (jSONObject.has(Constants.JSON_CU_H24)) {
                controlUnit.setH24(jSONObject.getInt(Constants.JSON_CU_H24));
            } else {
                controlUnit.setH24(0);
            }
            if (jSONObject.has(Constants.JSON_CU_ICON)) {
                controlUnit.setIcontype(jSONObject.getInt(Constants.JSON_CU_ICON));
            } else {
                controlUnit.setIcontype(0);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return controlUnit;
    }

    public static ControlUnit setCUServer(JSONObject jSONObject) {
        ControlUnit controlUnit = new ControlUnit();
        try {
            controlUnit.setName(jSONObject.getString(Constants.JSON_CU_NAME).toUpperCase());
            controlUnit.setSerial(jSONObject.getString(Constants.JSON_CU_SERIAL));
            controlUnit.setFWVer(jSONObject.getString(Constants.JSON_CU_FWVER));
            if (jSONObject.has(Constants.JSON_CU_ISOFF)) {
                controlUnit.setIsOff(jSONObject.getBoolean(Constants.JSON_CU_ISOFF));
            }
            if (jSONObject.has(Constants.JSON_CU_ISCOOLING)) {
                controlUnit.setIsCooling(jSONObject.getBoolean(Constants.JSON_CU_ISCOOLING));
            }
            if (jSONObject.has(Constants.JSON_CU_OPERMODE)) {
                controlUnit.setOperatingMode(jSONObject.getInt(Constants.JSON_CU_OPERMODE));
            }
            if (jSONObject.has(Constants.JSON_CU_LASCONFIGTUPDATE)) {
                controlUnit.setLastConfigUpdate(jSONObject.getString(Constants.JSON_CU_LASCONFIGTUPDATE));
            }
            if (jSONObject.has(Constants.JSON_CU_LASTSYNCUPDATE)) {
                controlUnit.setLastSyncUpdate(jSONObject.getString(Constants.JSON_CU_LASTSYNCUPDATE));
            }
            if (jSONObject.has(Constants.JSON_CU_NUMERR)) {
                controlUnit.setNumError(jSONObject.getInt(Constants.JSON_CU_NUMERR));
            }
            if (jSONObject.has(Constants.JSON_CU_ICON)) {
                controlUnit.setIcontype(jSONObject.getInt(Constants.JSON_CU_ICON));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return controlUnit;
    }

    public static ControlUnit mergeServerPref(ControlUnit controlUnit, ControlUnit controlUnit2) {
        if (controlUnit2.getIcontype() == -1) {
            controlUnit2.setIcontype(controlUnit.getIcontype());
        }
        controlUnit2.setIcontype(controlUnit.getIcontype());
        if (controlUnit2.getName().isEmpty()) {
            controlUnit2.setName(controlUnit.getName());
        }
        if (!controlUnit2.isOffline()) {
            controlUnit2.setNumError(controlUnit.getNumError());
        }
        controlUnit2.setName(controlUnit.getName());
        controlUnit2.setFWVer(controlUnit.getFWVer());
        return controlUnit2;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(4:29|30|31|32) */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x0109, code lost:
        r7 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x010a, code lost:
        r7.printStackTrace();
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:31:0x008c */
    /* JADX WARNING: Missing exception handler attribute for start block: B:38:0x00a2 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:45:0x00bb */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static it.tecnosystemi.TS.Model.ControlUnit getCuFromJSONFOfflineRidotto(org.json.JSONObject r7) {
        /*
            it.tecnosystemi.TS.Model.ControlUnit r0 = new it.tecnosystemi.TS.Model.ControlUnit
            r0.<init>()
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ISOFF_R     // Catch:{ JSONException -> 0x0109 }
            boolean r1 = r7.has(r1)     // Catch:{ JSONException -> 0x0109 }
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L_0x001e
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ISOFF_R     // Catch:{ JSONException -> 0x0109 }
            int r1 = r7.getInt(r1)     // Catch:{ JSONException -> 0x0109 }
            if (r1 != 0) goto L_0x001b
            r0.setIsOff(r3)     // Catch:{ JSONException -> 0x0109 }
            goto L_0x001e
        L_0x001b:
            r0.setIsOff(r2)     // Catch:{ JSONException -> 0x0109 }
        L_0x001e:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ISCOOL_R     // Catch:{ JSONException -> 0x0109 }
            boolean r1 = r7.has(r1)     // Catch:{ JSONException -> 0x0109 }
            if (r1 == 0) goto L_0x0035
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ISCOOL_R     // Catch:{ JSONException -> 0x0109 }
            int r1 = r7.getInt(r1)     // Catch:{ JSONException -> 0x0109 }
            if (r1 != 0) goto L_0x0032
            r0.setIsCooling(r3)     // Catch:{ JSONException -> 0x0109 }
            goto L_0x0035
        L_0x0032:
            r0.setIsCooling(r2)     // Catch:{ JSONException -> 0x0109 }
        L_0x0035:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_COOLMOD_R     // Catch:{ JSONException -> 0x0109 }
            boolean r1 = r7.has(r1)     // Catch:{ JSONException -> 0x0109 }
            if (r1 == 0) goto L_0x0046
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_COOLMOD_R     // Catch:{ JSONException -> 0x0109 }
            int r1 = r7.getInt(r1)     // Catch:{ JSONException -> 0x0109 }
            r0.setOperatingMode(r1)     // Catch:{ JSONException -> 0x0109 }
        L_0x0046:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ERRCU     // Catch:{ JSONException -> 0x0109 }
            boolean r1 = r7.has(r1)     // Catch:{ JSONException -> 0x0109 }
            if (r1 == 0) goto L_0x0055
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ERRCU     // Catch:{ JSONException -> 0x0109 }
            int r1 = r7.getInt(r1)     // Catch:{ JSONException -> 0x0109 }
            goto L_0x0056
        L_0x0055:
            r1 = 0
        L_0x0056:
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_MASTERNR_R     // Catch:{ JSONException -> 0x0109 }
            boolean r2 = r7.has(r2)     // Catch:{ JSONException -> 0x0109 }
            if (r2 == 0) goto L_0x0067
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_MASTERNR_R     // Catch:{ JSONException -> 0x0109 }
            int r2 = r7.getInt(r2)     // Catch:{ JSONException -> 0x0109 }
            r0.setMaster(r2)     // Catch:{ JSONException -> 0x0109 }
        L_0x0067:
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_IRPRESENT_R     // Catch:{ JSONException -> 0x0109 }
            boolean r2 = r7.has(r2)     // Catch:{ JSONException -> 0x0109 }
            if (r2 == 0) goto L_0x0078
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_IRPRESENT_R     // Catch:{ JSONException -> 0x0109 }
            int r2 = r7.getInt(r2)     // Catch:{ JSONException -> 0x0109 }
            r0.setIr_present(r2)     // Catch:{ JSONException -> 0x0109 }
        L_0x0078:
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_TCAN_R     // Catch:{ JSONException -> 0x0109 }
            boolean r2 = r7.has(r2)     // Catch:{ JSONException -> 0x0109 }
            if (r2 == 0) goto L_0x008f
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_TCAN_R     // Catch:{ Exception -> 0x008c }
            int r2 = r7.getInt(r2)     // Catch:{ Exception -> 0x008c }
            int r2 = r2 / 10
            r0.setT_can(r2)     // Catch:{ Exception -> 0x008c }
            goto L_0x008f
        L_0x008c:
            r0.setT_can(r3)     // Catch:{ JSONException -> 0x0109 }
        L_0x008f:
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_FEST_R     // Catch:{ JSONException -> 0x0109 }
            boolean r2 = r7.has(r2)     // Catch:{ JSONException -> 0x0109 }
            r4 = -1
            if (r2 == 0) goto L_0x00a6
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_FEST_R     // Catch:{ Exception -> 0x00a2 }
            int r2 = r7.getInt(r2)     // Catch:{ Exception -> 0x00a2 }
            r0.setF_est(r2)     // Catch:{ Exception -> 0x00a2 }
            goto L_0x00a9
        L_0x00a2:
            r0.setF_est(r4)     // Catch:{ JSONException -> 0x0109 }
            goto L_0x00a9
        L_0x00a6:
            r0.setF_est(r4)     // Catch:{ JSONException -> 0x0109 }
        L_0x00a9:
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_FINV_R     // Catch:{ JSONException -> 0x0109 }
            boolean r2 = r7.has(r2)     // Catch:{ JSONException -> 0x0109 }
            if (r2 == 0) goto L_0x00bf
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_FINV_R     // Catch:{ Exception -> 0x00bb }
            int r2 = r7.getInt(r2)     // Catch:{ Exception -> 0x00bb }
            r0.setF_inv(r2)     // Catch:{ Exception -> 0x00bb }
            goto L_0x00c2
        L_0x00bb:
            r0.setF_inv(r4)     // Catch:{ JSONException -> 0x0109 }
            goto L_0x00c2
        L_0x00bf:
            r0.setF_inv(r4)     // Catch:{ JSONException -> 0x0109 }
        L_0x00c2:
            boolean[] r1 = it.tecnosystemi.TS.Utils.Functions.geterror(r1)     // Catch:{ JSONException -> 0x0109 }
            r0.setErrors(r1)     // Catch:{ JSONException -> 0x0109 }
            boolean[] r1 = r0.getErrors()     // Catch:{ JSONException -> 0x0109 }
            int r2 = r1.length     // Catch:{ JSONException -> 0x0109 }
            r4 = 0
            r5 = 0
        L_0x00d0:
            if (r4 >= r2) goto L_0x00db
            boolean r6 = r1[r4]     // Catch:{ JSONException -> 0x0109 }
            if (r6 == 0) goto L_0x00d8
            int r5 = r5 + 1
        L_0x00d8:
            int r4 = r4 + 1
            goto L_0x00d0
        L_0x00db:
            r0.setNumError(r5)     // Catch:{ JSONException -> 0x0109 }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ JSONException -> 0x0109 }
            r1.<init>()     // Catch:{ JSONException -> 0x0109 }
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ZONE     // Catch:{ JSONException -> 0x0109 }
            boolean r2 = r7.has(r2)     // Catch:{ JSONException -> 0x0109 }
            if (r2 == 0) goto L_0x0105
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ZONE     // Catch:{ JSONException -> 0x0109 }
            org.json.JSONArray r7 = r7.getJSONArray(r2)     // Catch:{ JSONException -> 0x0109 }
        L_0x00f1:
            int r2 = r7.length()     // Catch:{ JSONException -> 0x0109 }
            if (r3 >= r2) goto L_0x0105
            org.json.JSONObject r2 = r7.getJSONObject(r3)     // Catch:{ JSONException -> 0x0109 }
            it.tecnosystemi.TS.Model.Zona r2 = it.tecnosystemi.TS.Model.Zona.getZonaFromJsonOfflineRidotto(r2)     // Catch:{ JSONException -> 0x0109 }
            r1.add(r2)     // Catch:{ JSONException -> 0x0109 }
            int r3 = r3 + 1
            goto L_0x00f1
        L_0x0105:
            r0.setZone(r1)     // Catch:{ JSONException -> 0x0109 }
            goto L_0x010d
        L_0x0109:
            r7 = move-exception
            r7.printStackTrace()
        L_0x010d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Model.ControlUnit.getCuFromJSONFOfflineRidotto(org.json.JSONObject):it.tecnosystemi.TS.Model.ControlUnit");
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(4:35|36|37|38) */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x0124, code lost:
        r7 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x0125, code lost:
        r7.printStackTrace();
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:37:0x00a7 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:44:0x00bd */
    /* JADX WARNING: Missing exception handler attribute for start block: B:51:0x00d6 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static it.tecnosystemi.TS.Model.ControlUnit getCuFromJSONFOffline(org.json.JSONObject r7) {
        /*
            it.tecnosystemi.TS.Model.ControlUnit r0 = new it.tecnosystemi.TS.Model.ControlUnit
            r0.<init>()
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_C     // Catch:{ JSONException -> 0x0124 }
            boolean r1 = r7.has(r1)     // Catch:{ JSONException -> 0x0124 }
            if (r1 == 0) goto L_0x0020
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_C     // Catch:{ JSONException -> 0x0124 }
            java.lang.String r1 = r7.getString(r1)     // Catch:{ JSONException -> 0x0124 }
            java.lang.String r2 = "stato_r"
            boolean r1 = r1.equals(r2)     // Catch:{ JSONException -> 0x0124 }
            if (r1 == 0) goto L_0x0020
            it.tecnosystemi.TS.Model.ControlUnit r7 = getCuFromJSONFOfflineRidotto(r7)     // Catch:{ JSONException -> 0x0124 }
            return r7
        L_0x0020:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ISOFF     // Catch:{ JSONException -> 0x0124 }
            boolean r1 = r7.has(r1)     // Catch:{ JSONException -> 0x0124 }
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L_0x0039
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ISOFF     // Catch:{ JSONException -> 0x0124 }
            int r1 = r7.getInt(r1)     // Catch:{ JSONException -> 0x0124 }
            if (r1 != 0) goto L_0x0036
            r0.setIsOff(r3)     // Catch:{ JSONException -> 0x0124 }
            goto L_0x0039
        L_0x0036:
            r0.setIsOff(r2)     // Catch:{ JSONException -> 0x0124 }
        L_0x0039:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ISCOOL     // Catch:{ JSONException -> 0x0124 }
            boolean r1 = r7.has(r1)     // Catch:{ JSONException -> 0x0124 }
            if (r1 == 0) goto L_0x0050
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ISCOOL     // Catch:{ JSONException -> 0x0124 }
            int r1 = r7.getInt(r1)     // Catch:{ JSONException -> 0x0124 }
            if (r1 != 0) goto L_0x004d
            r0.setIsCooling(r3)     // Catch:{ JSONException -> 0x0124 }
            goto L_0x0050
        L_0x004d:
            r0.setIsCooling(r2)     // Catch:{ JSONException -> 0x0124 }
        L_0x0050:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_COOLMOD     // Catch:{ JSONException -> 0x0124 }
            boolean r1 = r7.has(r1)     // Catch:{ JSONException -> 0x0124 }
            if (r1 == 0) goto L_0x0061
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_COOLMOD     // Catch:{ JSONException -> 0x0124 }
            int r1 = r7.getInt(r1)     // Catch:{ JSONException -> 0x0124 }
            r0.setOperatingMode(r1)     // Catch:{ JSONException -> 0x0124 }
        L_0x0061:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ERRCU     // Catch:{ JSONException -> 0x0124 }
            boolean r1 = r7.has(r1)     // Catch:{ JSONException -> 0x0124 }
            if (r1 == 0) goto L_0x0070
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ERRCU     // Catch:{ JSONException -> 0x0124 }
            int r1 = r7.getInt(r1)     // Catch:{ JSONException -> 0x0124 }
            goto L_0x0071
        L_0x0070:
            r1 = 0
        L_0x0071:
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_MASTERNR     // Catch:{ JSONException -> 0x0124 }
            boolean r2 = r7.has(r2)     // Catch:{ JSONException -> 0x0124 }
            if (r2 == 0) goto L_0x0082
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_MASTERNR     // Catch:{ JSONException -> 0x0124 }
            int r2 = r7.getInt(r2)     // Catch:{ JSONException -> 0x0124 }
            r0.setMaster(r2)     // Catch:{ JSONException -> 0x0124 }
        L_0x0082:
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_IRPRESENT     // Catch:{ JSONException -> 0x0124 }
            boolean r2 = r7.has(r2)     // Catch:{ JSONException -> 0x0124 }
            if (r2 == 0) goto L_0x0093
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_IRPRESENT     // Catch:{ JSONException -> 0x0124 }
            int r2 = r7.getInt(r2)     // Catch:{ JSONException -> 0x0124 }
            r0.setIr_present(r2)     // Catch:{ JSONException -> 0x0124 }
        L_0x0093:
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_TCAN     // Catch:{ JSONException -> 0x0124 }
            boolean r2 = r7.has(r2)     // Catch:{ JSONException -> 0x0124 }
            if (r2 == 0) goto L_0x00aa
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_TCAN     // Catch:{ Exception -> 0x00a7 }
            int r2 = r7.getInt(r2)     // Catch:{ Exception -> 0x00a7 }
            int r2 = r2 / 10
            r0.setT_can(r2)     // Catch:{ Exception -> 0x00a7 }
            goto L_0x00aa
        L_0x00a7:
            r0.setT_can(r3)     // Catch:{ JSONException -> 0x0124 }
        L_0x00aa:
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_FEST     // Catch:{ JSONException -> 0x0124 }
            boolean r2 = r7.has(r2)     // Catch:{ JSONException -> 0x0124 }
            r4 = -1
            if (r2 == 0) goto L_0x00c1
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_FEST     // Catch:{ Exception -> 0x00bd }
            int r2 = r7.getInt(r2)     // Catch:{ Exception -> 0x00bd }
            r0.setF_est(r2)     // Catch:{ Exception -> 0x00bd }
            goto L_0x00c4
        L_0x00bd:
            r0.setF_est(r4)     // Catch:{ JSONException -> 0x0124 }
            goto L_0x00c4
        L_0x00c1:
            r0.setF_est(r4)     // Catch:{ JSONException -> 0x0124 }
        L_0x00c4:
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_FINV     // Catch:{ JSONException -> 0x0124 }
            boolean r2 = r7.has(r2)     // Catch:{ JSONException -> 0x0124 }
            if (r2 == 0) goto L_0x00da
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_FINV     // Catch:{ Exception -> 0x00d6 }
            int r2 = r7.getInt(r2)     // Catch:{ Exception -> 0x00d6 }
            r0.setF_inv(r2)     // Catch:{ Exception -> 0x00d6 }
            goto L_0x00dd
        L_0x00d6:
            r0.setF_inv(r4)     // Catch:{ JSONException -> 0x0124 }
            goto L_0x00dd
        L_0x00da:
            r0.setF_inv(r4)     // Catch:{ JSONException -> 0x0124 }
        L_0x00dd:
            boolean[] r1 = it.tecnosystemi.TS.Utils.Functions.geterror(r1)     // Catch:{ JSONException -> 0x0124 }
            r0.setErrors(r1)     // Catch:{ JSONException -> 0x0124 }
            boolean[] r1 = r0.getErrors()     // Catch:{ JSONException -> 0x0124 }
            int r2 = r1.length     // Catch:{ JSONException -> 0x0124 }
            r4 = 0
            r5 = 0
        L_0x00eb:
            if (r4 >= r2) goto L_0x00f6
            boolean r6 = r1[r4]     // Catch:{ JSONException -> 0x0124 }
            if (r6 == 0) goto L_0x00f3
            int r5 = r5 + 1
        L_0x00f3:
            int r4 = r4 + 1
            goto L_0x00eb
        L_0x00f6:
            r0.setNumError(r5)     // Catch:{ JSONException -> 0x0124 }
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ JSONException -> 0x0124 }
            r1.<init>()     // Catch:{ JSONException -> 0x0124 }
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ZONE     // Catch:{ JSONException -> 0x0124 }
            boolean r2 = r7.has(r2)     // Catch:{ JSONException -> 0x0124 }
            if (r2 == 0) goto L_0x0120
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ZONE     // Catch:{ JSONException -> 0x0124 }
            org.json.JSONArray r7 = r7.getJSONArray(r2)     // Catch:{ JSONException -> 0x0124 }
        L_0x010c:
            int r2 = r7.length()     // Catch:{ JSONException -> 0x0124 }
            if (r3 >= r2) goto L_0x0120
            org.json.JSONObject r2 = r7.getJSONObject(r3)     // Catch:{ JSONException -> 0x0124 }
            it.tecnosystemi.TS.Model.Zona r2 = it.tecnosystemi.TS.Model.Zona.getZonaFromJsonOffline(r2)     // Catch:{ JSONException -> 0x0124 }
            r1.add(r2)     // Catch:{ JSONException -> 0x0124 }
            int r3 = r3 + 1
            goto L_0x010c
        L_0x0120:
            r0.setZone(r1)     // Catch:{ JSONException -> 0x0124 }
            goto L_0x0128
        L_0x0124:
            r7 = move-exception
            r7.printStackTrace()
        L_0x0128:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Model.ControlUnit.getCuFromJSONFOffline(org.json.JSONObject):it.tecnosystemi.TS.Model.ControlUnit");
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(4:41|42|43|44) */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x0166, code lost:
        r6 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x0167, code lost:
        r6.printStackTrace();
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:43:0x00e9 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:50:0x00ff */
    /* JADX WARNING: Missing exception handler attribute for start block: B:57:0x0118 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static it.tecnosystemi.TS.Model.ControlUnit getCuFromJSONFromServer(org.json.JSONObject r6, android.app.Activity r7) {
        /*
            it.tecnosystemi.TS.Model.ControlUnit r7 = new it.tecnosystemi.TS.Model.ControlUnit
            r7.<init>()
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_SERIAL     // Catch:{ JSONException -> 0x0166 }
            boolean r0 = r6.has(r0)     // Catch:{ JSONException -> 0x0166 }
            if (r0 == 0) goto L_0x0016
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_SERIAL     // Catch:{ JSONException -> 0x0166 }
            java.lang.String r0 = r6.getString(r0)     // Catch:{ JSONException -> 0x0166 }
            r7.setSerial(r0)     // Catch:{ JSONException -> 0x0166 }
        L_0x0016:
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_NAME     // Catch:{ JSONException -> 0x0166 }
            boolean r0 = r6.has(r0)     // Catch:{ JSONException -> 0x0166 }
            if (r0 == 0) goto L_0x002b
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_NAME     // Catch:{ JSONException -> 0x0166 }
            java.lang.String r0 = r6.getString(r0)     // Catch:{ JSONException -> 0x0166 }
            java.lang.String r0 = r0.toUpperCase()     // Catch:{ JSONException -> 0x0166 }
            r7.setName(r0)     // Catch:{ JSONException -> 0x0166 }
        L_0x002b:
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_FWVER     // Catch:{ JSONException -> 0x0166 }
            boolean r0 = r6.has(r0)     // Catch:{ JSONException -> 0x0166 }
            if (r0 == 0) goto L_0x003c
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_FWVER     // Catch:{ JSONException -> 0x0166 }
            java.lang.String r0 = r6.getString(r0)     // Catch:{ JSONException -> 0x0166 }
            r7.setFWVer(r0)     // Catch:{ JSONException -> 0x0166 }
        L_0x003c:
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_ISOFF     // Catch:{ JSONException -> 0x0166 }
            boolean r0 = r6.has(r0)     // Catch:{ JSONException -> 0x0166 }
            if (r0 == 0) goto L_0x004d
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_ISOFF     // Catch:{ JSONException -> 0x0166 }
            boolean r0 = r6.getBoolean(r0)     // Catch:{ JSONException -> 0x0166 }
            r7.setIsOff(r0)     // Catch:{ JSONException -> 0x0166 }
        L_0x004d:
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_ISCOOLING     // Catch:{ JSONException -> 0x0166 }
            boolean r0 = r6.has(r0)     // Catch:{ JSONException -> 0x0166 }
            if (r0 == 0) goto L_0x005e
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_ISCOOLING     // Catch:{ JSONException -> 0x0166 }
            boolean r0 = r6.getBoolean(r0)     // Catch:{ JSONException -> 0x0166 }
            r7.setIsCooling(r0)     // Catch:{ JSONException -> 0x0166 }
        L_0x005e:
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_OPERMODE     // Catch:{ JSONException -> 0x0166 }
            boolean r0 = r6.has(r0)     // Catch:{ JSONException -> 0x0166 }
            if (r0 == 0) goto L_0x006f
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_OPERMODE     // Catch:{ JSONException -> 0x0166 }
            int r0 = r6.getInt(r0)     // Catch:{ JSONException -> 0x0166 }
            r7.setOperatingMode(r0)     // Catch:{ JSONException -> 0x0166 }
        L_0x006f:
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_LASCONFIGTUPDATE     // Catch:{ JSONException -> 0x0166 }
            boolean r0 = r6.has(r0)     // Catch:{ JSONException -> 0x0166 }
            if (r0 == 0) goto L_0x0080
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_LASCONFIGTUPDATE     // Catch:{ JSONException -> 0x0166 }
            java.lang.String r0 = r6.getString(r0)     // Catch:{ JSONException -> 0x0166 }
            r7.setLastConfigUpdate(r0)     // Catch:{ JSONException -> 0x0166 }
        L_0x0080:
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_LASTSYNCUPDATE     // Catch:{ JSONException -> 0x0166 }
            boolean r0 = r6.has(r0)     // Catch:{ JSONException -> 0x0166 }
            if (r0 == 0) goto L_0x0091
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_LASTSYNCUPDATE     // Catch:{ JSONException -> 0x0166 }
            java.lang.String r0 = r6.getString(r0)     // Catch:{ JSONException -> 0x0166 }
            r7.setLastSyncUpdate(r0)     // Catch:{ JSONException -> 0x0166 }
        L_0x0091:
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_ICON     // Catch:{ JSONException -> 0x0166 }
            boolean r0 = r6.has(r0)     // Catch:{ JSONException -> 0x0166 }
            if (r0 == 0) goto L_0x00a2
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_ICON     // Catch:{ JSONException -> 0x0166 }
            int r0 = r6.getInt(r0)     // Catch:{ JSONException -> 0x0166 }
            r7.setIcontype(r0)     // Catch:{ JSONException -> 0x0166 }
        L_0x00a2:
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_ERR     // Catch:{ JSONException -> 0x0166 }
            boolean r0 = r6.has(r0)     // Catch:{ JSONException -> 0x0166 }
            r1 = 0
            if (r0 == 0) goto L_0x00b2
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_ERR     // Catch:{ JSONException -> 0x0166 }
            int r0 = r6.getInt(r0)     // Catch:{ JSONException -> 0x0166 }
            goto L_0x00b3
        L_0x00b2:
            r0 = 0
        L_0x00b3:
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_IP     // Catch:{ JSONException -> 0x0166 }
            boolean r2 = r6.has(r2)     // Catch:{ JSONException -> 0x0166 }
            if (r2 == 0) goto L_0x00c4
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_IP     // Catch:{ JSONException -> 0x0166 }
            java.lang.String r2 = r6.getString(r2)     // Catch:{ JSONException -> 0x0166 }
            r7.setIp(r2)     // Catch:{ JSONException -> 0x0166 }
        L_0x00c4:
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_IRPRESENT     // Catch:{ JSONException -> 0x0166 }
            boolean r2 = r6.has(r2)     // Catch:{ JSONException -> 0x0166 }
            if (r2 == 0) goto L_0x00d5
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_IRPRESENT     // Catch:{ JSONException -> 0x0166 }
            int r2 = r6.getInt(r2)     // Catch:{ JSONException -> 0x0166 }
            r7.setIr_present(r2)     // Catch:{ JSONException -> 0x0166 }
        L_0x00d5:
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_TCAN     // Catch:{ JSONException -> 0x0166 }
            boolean r2 = r6.has(r2)     // Catch:{ JSONException -> 0x0166 }
            if (r2 == 0) goto L_0x00ec
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_TCAN     // Catch:{ Exception -> 0x00e9 }
            int r2 = r6.getInt(r2)     // Catch:{ Exception -> 0x00e9 }
            int r2 = r2 / 10
            r7.setT_can(r2)     // Catch:{ Exception -> 0x00e9 }
            goto L_0x00ec
        L_0x00e9:
            r7.setT_can(r1)     // Catch:{ JSONException -> 0x0166 }
        L_0x00ec:
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_FEST     // Catch:{ JSONException -> 0x0166 }
            boolean r2 = r6.has(r2)     // Catch:{ JSONException -> 0x0166 }
            r3 = -1
            if (r2 == 0) goto L_0x0103
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_FEST     // Catch:{ Exception -> 0x00ff }
            int r2 = r6.getInt(r2)     // Catch:{ Exception -> 0x00ff }
            r7.setF_est(r2)     // Catch:{ Exception -> 0x00ff }
            goto L_0x0106
        L_0x00ff:
            r7.setF_est(r3)     // Catch:{ JSONException -> 0x0166 }
            goto L_0x0106
        L_0x0103:
            r7.setF_est(r3)     // Catch:{ JSONException -> 0x0166 }
        L_0x0106:
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_FINV     // Catch:{ JSONException -> 0x0166 }
            boolean r2 = r6.has(r2)     // Catch:{ JSONException -> 0x0166 }
            if (r2 == 0) goto L_0x011c
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_FINV     // Catch:{ Exception -> 0x0118 }
            int r2 = r6.getInt(r2)     // Catch:{ Exception -> 0x0118 }
            r7.setF_inv(r2)     // Catch:{ Exception -> 0x0118 }
            goto L_0x011f
        L_0x0118:
            r7.setF_inv(r3)     // Catch:{ JSONException -> 0x0166 }
            goto L_0x011f
        L_0x011c:
            r7.setF_inv(r3)     // Catch:{ JSONException -> 0x0166 }
        L_0x011f:
            boolean[] r0 = it.tecnosystemi.TS.Utils.Functions.geterror(r0)     // Catch:{ JSONException -> 0x0166 }
            r7.setErrors(r0)     // Catch:{ JSONException -> 0x0166 }
            boolean[] r0 = r7.getErrors()     // Catch:{ JSONException -> 0x0166 }
            int r2 = r0.length     // Catch:{ JSONException -> 0x0166 }
            r3 = 0
            r4 = 0
        L_0x012d:
            if (r3 >= r2) goto L_0x0138
            boolean r5 = r0[r3]     // Catch:{ JSONException -> 0x0166 }
            if (r5 == 0) goto L_0x0135
            int r4 = r4 + 1
        L_0x0135:
            int r3 = r3 + 1
            goto L_0x012d
        L_0x0138:
            r7.setNumError(r4)     // Catch:{ JSONException -> 0x0166 }
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ JSONException -> 0x0166 }
            r0.<init>()     // Catch:{ JSONException -> 0x0166 }
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_ZONE     // Catch:{ JSONException -> 0x0166 }
            boolean r2 = r6.has(r2)     // Catch:{ JSONException -> 0x0166 }
            if (r2 == 0) goto L_0x0162
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_ZONE     // Catch:{ JSONException -> 0x0166 }
            org.json.JSONArray r6 = r6.getJSONArray(r2)     // Catch:{ JSONException -> 0x0166 }
        L_0x014e:
            int r2 = r6.length()     // Catch:{ JSONException -> 0x0166 }
            if (r1 >= r2) goto L_0x0162
            org.json.JSONObject r2 = r6.getJSONObject(r1)     // Catch:{ JSONException -> 0x0166 }
            it.tecnosystemi.TS.Model.Zona r2 = it.tecnosystemi.TS.Model.Zona.getZonaFromJson(r2)     // Catch:{ JSONException -> 0x0166 }
            r0.add(r2)     // Catch:{ JSONException -> 0x0166 }
            int r1 = r1 + 1
            goto L_0x014e
        L_0x0162:
            r7.setZone(r0)     // Catch:{ JSONException -> 0x0166 }
            goto L_0x016a
        L_0x0166:
            r6 = move-exception
            r6.printStackTrace()
        L_0x016a:
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Model.ControlUnit.getCuFromJSONFromServer(org.json.JSONObject, android.app.Activity):it.tecnosystemi.TS.Model.ControlUnit");
    }

    public static ControlUnit mergeFromGetState(ControlUnit controlUnit, ControlUnit controlUnit2) {
        controlUnit.setIsOff(controlUnit2.getIsOff());
        controlUnit.setIsCooling(controlUnit2.getIsCooling());
        controlUnit.setMaster(controlUnit2.getMaster());
        controlUnit.setZone(controlUnit2.getZone());
        controlUnit.setOperatingMode(controlUnit2.getOperatingMode());
        controlUnit.setErrors(controlUnit2.getErrors());
        controlUnit.setNumError(controlUnit2.getNumError());
        controlUnit.setT_can(controlUnit2.getT_can());
        controlUnit.setIr_present(controlUnit2.getIr_present());
        controlUnit.setF_inv(controlUnit2.getF_inv());
        controlUnit.setF_est(controlUnit2.getF_est());
        controlUnit.setFWVer(controlUnit2.getFWVer());
        if (controlUnit2.getName() != null) {
            controlUnit.setName(controlUnit2.getName());
        }
        controlUnit.setLastSyncUpdate(controlUnit2.getLastSyncUpdate());
        if (controlUnit2.getIp() != null) {
            controlUnit.setIp(controlUnit2.getIp());
        }
        return controlUnit;
    }

    public static void deleteCufromPref(String str, BaseActivity baseActivity) {
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        String string = savePreferences.getString(Constants.PREF_UC_HOME);
        if (string != null) {
            try {
                JSONArray jSONArray = new JSONArray(string);
                JSONArray jSONArray2 = new JSONArray();
                for (int i = 0; i < jSONArray.length(); i++) {
                    if (jSONArray.getJSONObject(i).getString(Constants.JSON_CU_SERIAL).compareTo(str) != 0) {
                        jSONArray2.put(jSONArray.getJSONObject(i));
                    }
                }
                savePreferences.save(Constants.PREF_UC_HOME, jSONArray2.toString());
            } catch (Exception unused) {
            }
        }
    }

    public static void deleteListCufromPref(List<String> list, BaseActivity baseActivity) {
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        String string = savePreferences.getString(Constants.PREF_UC_HOME);
        if (string != null) {
            try {
                JSONArray jSONArray = new JSONArray(string);
                JSONArray jSONArray2 = new JSONArray();
                for (int i = 0; i < jSONArray.length(); i++) {
                    if (!list.contains(jSONArray.getJSONObject(i).getString(Constants.JSON_CU_SERIAL))) {
                        jSONArray2.put(jSONArray.getJSONObject(i));
                    }
                }
                savePreferences.save(Constants.PREF_UC_HOME, jSONArray2.toString());
            } catch (Exception unused) {
            }
        }
    }

    public static void saveCuInPref(String str, String str2, String str3, String str4, int i, Activity activity, boolean z) {
        ControlUnit controlUnit = new ControlUnit();
        controlUnit.setName(str);
        controlUnit.setSerial(str2);
        controlUnit.setOffline(z);
        if (z) {
            controlUnit.setPin(str3);
            controlUnit.setPinOffline(str3);
        } else {
            controlUnit.setPin(str3);
            controlUnit.setPinOffline(str3);
        }
        controlUnit.setIp(str4);
        controlUnit.setUnitOfMesure(0);
        if (i >= 0) {
            controlUnit.setIcontype(i);
        }
        SavePreferences savePreferences = new SavePreferences(activity, activity.getString(R.string.PrefsName));
        try {
            JSONArray jSONArray = new JSONArray(savePreferences.getString(Constants.PREF_UC_HOME));
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                if (setCULocal(jSONArray.getJSONObject(i2)).getSerial().compareTo(str2) != 0) {
                    arrayList.add(jSONArray.getJSONObject(i2));
                }
            }
            arrayList.add(controlUnit.cuLocalJson());
            Collections.sort(arrayList, new Comparator<JSONObject>() {
                public int compare(JSONObject jSONObject, JSONObject jSONObject2) {
                    try {
                        return ((String) jSONObject.get(Constants.JSON_CU_NAME)).compareTo((String) jSONObject2.get(Constants.JSON_CU_NAME));
                    } catch (Exception unused) {
                        return 0;
                    }
                }
            });
            savePreferences.save(Constants.PREF_UC_HOME, new JSONArray(arrayList).toString());
        } catch (Exception unused) {
        }
    }

    public static void saveCuInPref(ControlUnit controlUnit, Activity activity) {
        SavePreferences savePreferences = new SavePreferences(activity, activity.getString(R.string.PrefsName));
        String string = savePreferences.getString(Constants.PREF_UC_HOME);
        try {
            if (string.isEmpty()) {
                string = "[]";
            }
            JSONArray jSONArray = new JSONArray(string);
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < jSONArray.length(); i++) {
                if (setCULocal(jSONArray.getJSONObject(i)).getSerial().compareTo(controlUnit.getSerial()) != 0) {
                    arrayList.add(jSONArray.getJSONObject(i));
                }
            }
            arrayList.add(controlUnit.cuLocalJson());
            Collections.sort(arrayList, new Comparator<JSONObject>() {
                public int compare(JSONObject jSONObject, JSONObject jSONObject2) {
                    try {
                        return ((String) jSONObject.get(Constants.JSON_CU_NAME)).compareTo((String) jSONObject2.get(Constants.JSON_CU_NAME));
                    } catch (Exception unused) {
                        return 0;
                    }
                }
            });
            savePreferences.save(Constants.PREF_UC_HOME, new JSONArray(arrayList).toString());
        } catch (Exception unused) {
        }
    }

    public static ControlUnit getCuFromPref(String str, Activity activity) {
        String string = new SavePreferences(activity, activity.getString(R.string.PrefsName)).getString(Constants.PREF_UC_HOME);
        if (string == null) {
            return null;
        }
        try {
            JSONArray jSONArray = new JSONArray(string);
            for (int i = 0; i < jSONArray.length(); i++) {
                ControlUnit cULocal = setCULocal(jSONArray.getJSONObject(i));
                if (cULocal.serial.equals(str)) {
                    return cULocal;
                }
            }
            return null;
        } catch (Exception unused) {
            return null;
        }
    }
}
