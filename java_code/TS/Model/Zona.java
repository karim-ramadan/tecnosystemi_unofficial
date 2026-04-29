package it.tecnosystemi.TS.Model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import it.tecnosystemi.TS.Commands.Protocols;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Zona implements Serializable {
    @SerializedName("CBadge")
    @Expose
    Object CBadge;
    @SerializedName("CWin")
    @Expose
    Object CWin;
    @SerializedName("EV")
    @Expose
    int EV = -1;
    @SerializedName("Fancoil")
    @Expose
    int Fancoil = -1;
    @SerializedName("FancoilSet")
    @Expose
    int FancoilSet;
    int ManCrono;
    @SerializedName("Name")
    @Expose
    String Name;
    @SerializedName("Serranda")
    @Expose
    int Serranda = -1;
    @SerializedName("SerrandaSet")
    @Expose
    int SerrandaSet;
    @SerializedName("SetTemp")
    @Expose
    String SetTemp;
    @SerializedName("SetUmd")
    @Expose
    String SetUmd;
    @SerializedName("Temp")
    @Expose
    String Temp;
    @SerializedName("Umd")
    @Expose
    String Umd;
    @SerializedName("ZoneId")
    @Expose
    int ZoneId;
    @SerializedName("COff")
    @Expose
    boolean coff = false;
    List<List<Crono>> cronos;
    boolean[] errors;
    @SerializedName("IsCronoActive")
    @Expose
    boolean fasciaAttiva;
    @SerializedName("IsCronoMode")
    @Expose
    boolean isCronoMode;
    @SerializedName("IsMaster")
    @Expose
    boolean isMaster;
    @SerializedName("IsOFF")
    @Expose
    boolean isOff;
    boolean lastFancoil = true;
    int numError = 0;
    @SerializedName("Errors")
    @Expose
    int toconvertError;

    public Zona() {
        azzeracrono();
        setName("");
    }

    public String getName() {
        return this.Name.toUpperCase();
    }

    public void setName(String str) {
        this.Name = str.toUpperCase();
    }

    public int getManCrono() {
        return this.ManCrono;
    }

    public void setManCrono(int i) {
        this.ManCrono = i;
        boolean z = true;
        if (i != 1) {
            z = false;
        }
        this.isCronoMode = z;
    }

    public int getZoneId() {
        return this.ZoneId;
    }

    public void setZoneId(int i) {
        this.ZoneId = i;
    }

    public String getTemp() {
        return this.Temp;
    }

    public void setTemp(String str) {
        this.Temp = str;
    }

    public String getSetTemp() {
        return this.SetTemp;
    }

    public void setSetTemp(String str) {
        this.SetTemp = str;
    }

    public int getSerranda() {
        return this.Serranda;
    }

    public void setSerranda(int i) {
        this.Serranda = i;
    }

    public int getSerrandaSet() {
        return this.SerrandaSet;
    }

    public void setSerrandaSet(int i) {
        this.SerrandaSet = i;
    }

    public int getFancoil() {
        return this.Fancoil;
    }

    public void setFancoil(int i) {
        this.Fancoil = i;
    }

    public int getFancoilSet() {
        return this.FancoilSet;
    }

    public void setFancoilSet(int i) {
        this.FancoilSet = i;
    }

    public int getEV() {
        return this.EV;
    }

    public void setEV(int i) {
        this.EV = i;
    }

    public boolean[] getErrors() {
        return this.errors;
    }

    public void setErrors(boolean[] zArr) {
        this.errors = zArr;
    }

    public List<List<Crono>> getCrono() {
        return this.cronos;
    }

    public void setCrono(List<List<Crono>> list) {
        this.cronos = list;
    }

    public boolean isOff() {
        return this.isOff;
    }

    public void setOff(boolean z) {
        this.isOff = z;
    }

    public boolean isFasciaAttiva() {
        return this.fasciaAttiva;
    }

    public void setFasciaAttiva(boolean z) {
        this.fasciaAttiva = z;
        setCoff();
    }

    public int getNumError() {
        return this.numError;
    }

    public void setNumError(int i) {
        this.numError = i;
    }

    public String getStringerrors(String[] strArr) {
        String str = "";
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

    public boolean isMaster() {
        return this.isMaster;
    }

    public void setMaster(boolean z) {
        this.isMaster = z;
    }

    public boolean isCronoMode() {
        return this.isCronoMode;
    }

    public void setCronoMode(boolean z) {
        this.isCronoMode = z;
        setCoff();
    }

    public int getToconvertError() {
        return this.toconvertError;
    }

    public void setToconvertError(int i) {
        this.toconvertError = i;
    }

    public boolean isLastFancoil() {
        return this.lastFancoil;
    }

    public void setLastFancoil(boolean z) {
        this.lastFancoil = z;
    }

    public String getUmd() {
        return this.Umd;
    }

    public void setUmd(String str) {
        this.Umd = str;
    }

    public String getSetUmd() {
        return this.SetUmd;
    }

    public void setSetUmd(String str) {
        this.SetUmd = str;
    }

    public Object getCWin() {
        return this.CWin;
    }

    public void setCWin(Object obj) {
        this.CWin = obj;
        setCoff();
    }

    public Object getCBadge() {
        return this.CBadge;
    }

    public void setCBadge(Object obj) {
        this.CBadge = obj;
        setCoff();
    }

    public boolean isCoff() {
        return this.coff;
    }

    public void setCoff(boolean z) {
        this.coff = z;
    }

    /* JADX WARNING: Removed duplicated region for block: B:19:0x003e  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0040  */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x001b A[SYNTHETIC, Splitter:B:8:0x001b] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setCoff() {
        /*
            r7 = this;
            java.lang.Object r0 = r7.getCWin()
            r1 = -4616189618054758400(0xbff0000000000000, double:-1.0)
            if (r0 == 0) goto L_0x0014
            java.lang.Object r0 = r7.getCWin()     // Catch:{ Exception -> 0x0013 }
            java.lang.Double r0 = (java.lang.Double) r0     // Catch:{ Exception -> 0x0013 }
            double r3 = r0.doubleValue()     // Catch:{ Exception -> 0x0013 }
            goto L_0x0015
        L_0x0013:
        L_0x0014:
            r3 = r1
        L_0x0015:
            java.lang.Object r0 = r7.getCBadge()
            if (r0 == 0) goto L_0x0027
            java.lang.Object r0 = r7.getCBadge()     // Catch:{ Exception -> 0x0026 }
            java.lang.Double r0 = (java.lang.Double) r0     // Catch:{ Exception -> 0x0026 }
            double r1 = r0.doubleValue()     // Catch:{ Exception -> 0x0026 }
            goto L_0x0027
        L_0x0026:
        L_0x0027:
            r5 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            int r0 = (r1 > r5 ? 1 : (r1 == r5 ? 0 : -1))
            if (r0 == 0) goto L_0x0040
            int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r0 == 0) goto L_0x0040
            boolean r0 = r7.isCronoMode()
            if (r0 == 0) goto L_0x003e
            boolean r0 = r7.isFasciaAttiva()
            if (r0 != 0) goto L_0x003e
            goto L_0x0040
        L_0x003e:
            r0 = 0
            goto L_0x0041
        L_0x0040:
            r0 = 1
        L_0x0041:
            r7.coff = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Model.Zona.setCoff():void");
    }

    public void azzeracrono() {
        this.cronos = new ArrayList();
        for (int i = 0; i < 7; i++) {
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < 4; i2++) {
                arrayList.add((Object) null);
            }
            this.cronos.add(arrayList);
        }
    }

    public JSONObject update_ZONA_Command() {
        try {
            JSONObject jSONObject = new JSONObject("{\"c\":\"upd_zona\"}");
            jSONObject.put("id_zona", getZoneId());
            jSONObject.put("name", getName());
            jSONObject.put("is_off", isOff() ? 1 : 0);
            jSONObject.put("t_set", String.valueOf((int) (Double.parseDouble(getSetTemp()) * 10.0d)));
            if (getFancoil() != -1) {
                if (getSerranda() != -1) {
                    if (this.lastFancoil) {
                        if (getFancoilSet() == 7) {
                            jSONObject.put("fan_set", 16);
                            jSONObject.put("shu_set", 16);
                        } else {
                            jSONObject.put("fan_set", getFancoilSet());
                            jSONObject.put("shu_set", getFancoilSet());
                        }
                    } else if (getSerrandaSet() == 7) {
                        jSONObject.put("shu_set", 16);
                        jSONObject.put("fan_set", 16);
                    } else {
                        jSONObject.put("shu_set", getSerrandaSet());
                        jSONObject.put("fan_set", getSerrandaSet());
                    }
                    jSONObject.put("is_crono", isCronoMode() ? 1 : 0);
                    return jSONObject;
                }
            }
            if (getFancoil() == -1) {
                if (getSerrandaSet() == 7) {
                    jSONObject.put("shu_set", 16);
                    jSONObject.put("fan_set", 16);
                } else {
                    jSONObject.put("shu_set", getSerrandaSet());
                    jSONObject.put("fan_set", getSerrandaSet());
                }
            } else if (getFancoilSet() == 7) {
                jSONObject.put("fan_set", 16);
                jSONObject.put("shu_set", 16);
            } else {
                jSONObject.put("fan_set", getFancoilSet());
                jSONObject.put("shu_set", getFancoilSet());
            }
            jSONObject.put("is_crono", isCronoMode() ? 1 : 0);
            return jSONObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Zona makeTempCopy() {
        String json = new Gson().toJson((Object) this);
        try {
            new Zona();
            Zona zonaFromJson = getZonaFromJson(new JSONObject(json));
            zonaFromJson.setSetTemp(getSetTemp());
            zonaFromJson.setLastFancoil(isLastFancoil());
            zonaFromJson.setManCrono(getManCrono());
            return zonaFromJson;
        } catch (Exception unused) {
            return null;
        }
    }

    public JSONObject updCronoCommand(String str) {
        try {
            JSONObject jSONObject = new JSONObject("{}");
            jSONObject.put("c", Protocols.CMD_UPDFASCE);
            jSONObject.put("id_zona", getZoneId());
            jSONObject.put(Constants.INTENT_PIN, str);
            for (int i = 0; i < 7; i++) {
                JSONArray jSONArray = new JSONArray("[]");
                for (int i2 = 0; i2 < 4; i2++) {
                    if (this.cronos.get(i).get(i2) == null) {
                        jSONArray.put(Crono.commandUpdVoid());
                    } else if (((Crono) this.cronos.get(i).get(i2)).isEmpty()) {
                        jSONArray.put(Crono.commandUpdVoid());
                    } else {
                        jSONArray.put(((Crono) this.cronos.get(i).get(i2)).commandUpd());
                    }
                }
                jSONObject.put(String.valueOf(i), jSONArray);
            }
            return jSONObject;
        } catch (Exception unused) {
            return null;
        }
    }

    public static Zona getZonaFromJson(JSONObject jSONObject) {
        Zona zona = new Zona();
        try {
            Zona zona2 = (Zona) new Gson().fromJson(jSONObject.toString(), Zona.class);
            try {
                if (zona2.isCronoMode) {
                    zona2.setManCrono(1);
                } else {
                    zona2.setManCrono(0);
                }
                zona2.setErrors(Functions.geterror(zona2.getToconvertError()));
                int i = 0;
                for (boolean z : zona2.getErrors()) {
                    if (z) {
                        i++;
                    }
                }
                zona2.setNumError(i);
                double parseDouble = Double.parseDouble(zona2.getSetTemp());
                double parseDouble2 = Double.parseDouble(zona2.getTemp());
                zona2.setSetTemp(String.valueOf(parseDouble / 10.0d));
                zona2.setTemp(String.valueOf(parseDouble2 / 10.0d));
                return zona2;
            } catch (Exception unused) {
                zona = zona2;
                return zona;
            }
        } catch (Exception unused2) {
            return zona;
        }
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(45:0|1|2|(1:4)|5|(1:7)|8|(3:10|(1:12)(1:13)|14)|15|(1:17)|18|(4:20|21|22|23)|24|(4:26|27|28|29)|30|(1:32)|33|(1:35)|36|(1:38)|39|(1:41)|42|(1:44)|45|(1:47)|48|(3:50|(1:52)(1:53)|54)|55|(3:57|(1:59)(1:60)|61)|62|(1:64)|65|(1:67)|68|(2:70|71)|72|73|(2:75|76)|77|78|(3:80|(2:82|88)(1:89)|83)|87|84|85) */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:72:0x0153 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:77:0x0168 */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x015b A[SYNTHETIC, Splitter:B:75:0x015b] */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x017b A[Catch:{ Exception -> 0x0187 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static it.tecnosystemi.TS.Model.Zona getZonaFromJsonOffline(org.json.JSONObject r8) {
        /*
            it.tecnosystemi.TS.Model.Zona r0 = new it.tecnosystemi.TS.Model.Zona
            r0.<init>()
            com.google.gson.Gson r1 = new com.google.gson.Gson
            r1.<init>()
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_IDZONAGETSTATE     // Catch:{ Exception -> 0x0187 }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x0187 }
            if (r1 == 0) goto L_0x001b
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_IDZONAGETSTATE     // Catch:{ Exception -> 0x0187 }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x0187 }
            r0.setZoneId(r1)     // Catch:{ Exception -> 0x0187 }
        L_0x001b:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_NRZONAGETSTATE     // Catch:{ Exception -> 0x0187 }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x0187 }
            if (r1 == 0) goto L_0x002c
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_NRZONAGETSTATE     // Catch:{ Exception -> 0x0187 }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x0187 }
            r0.setZoneId(r1)     // Catch:{ Exception -> 0x0187 }
        L_0x002c:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ISOFF     // Catch:{ Exception -> 0x0187 }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x0187 }
            r2 = 0
            r3 = 1
            if (r1 == 0) goto L_0x0044
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ISOFF     // Catch:{ Exception -> 0x0187 }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x0187 }
            if (r1 != r3) goto L_0x0040
            r1 = 1
            goto L_0x0041
        L_0x0040:
            r1 = 0
        L_0x0041:
            r0.setOff(r1)     // Catch:{ Exception -> 0x0187 }
        L_0x0044:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_NAME     // Catch:{ Exception -> 0x0187 }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x0187 }
            if (r1 == 0) goto L_0x0055
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_NAME     // Catch:{ Exception -> 0x0187 }
            java.lang.String r1 = r8.getString(r1)     // Catch:{ Exception -> 0x0187 }
            r0.setName(r1)     // Catch:{ Exception -> 0x0187 }
        L_0x0055:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_TEMP     // Catch:{ Exception -> 0x0187 }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x0187 }
            r4 = 4621819117588971520(0x4024000000000000, double:10.0)
            if (r1 == 0) goto L_0x0071
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_TEMP     // Catch:{ Exception -> 0x0187 }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x0187 }
            double r6 = (double) r1
            java.lang.Double.isNaN(r6)
            double r6 = r6 / r4
            java.lang.String r1 = java.lang.String.valueOf(r6)     // Catch:{ Exception -> 0x0187 }
            r0.setTemp(r1)     // Catch:{ Exception -> 0x0187 }
        L_0x0071:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_TEMPSET     // Catch:{ Exception -> 0x0187 }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x0187 }
            if (r1 == 0) goto L_0x008b
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_TEMPSET     // Catch:{ Exception -> 0x0187 }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x0187 }
            double r6 = (double) r1
            java.lang.Double.isNaN(r6)
            double r6 = r6 / r4
            java.lang.String r1 = java.lang.String.valueOf(r6)     // Catch:{ Exception -> 0x0187 }
            r0.setSetTemp(r1)     // Catch:{ Exception -> 0x0187 }
        L_0x008b:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_FAN     // Catch:{ Exception -> 0x0187 }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x0187 }
            if (r1 == 0) goto L_0x009c
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_FAN     // Catch:{ Exception -> 0x0187 }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x0187 }
            r0.setFancoil(r1)     // Catch:{ Exception -> 0x0187 }
        L_0x009c:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_FANSET     // Catch:{ Exception -> 0x0187 }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x0187 }
            if (r1 == 0) goto L_0x00ad
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_FANSET     // Catch:{ Exception -> 0x0187 }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x0187 }
            r0.setFancoilSet(r1)     // Catch:{ Exception -> 0x0187 }
        L_0x00ad:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_EV     // Catch:{ Exception -> 0x0187 }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x0187 }
            if (r1 == 0) goto L_0x00be
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_EV     // Catch:{ Exception -> 0x0187 }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x0187 }
            r0.setEV(r1)     // Catch:{ Exception -> 0x0187 }
        L_0x00be:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_SHU     // Catch:{ Exception -> 0x0187 }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x0187 }
            if (r1 == 0) goto L_0x00cf
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_SHU     // Catch:{ Exception -> 0x0187 }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x0187 }
            r0.setSerranda(r1)     // Catch:{ Exception -> 0x0187 }
        L_0x00cf:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_SHUSET     // Catch:{ Exception -> 0x0187 }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x0187 }
            if (r1 == 0) goto L_0x00e0
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_SHUSET     // Catch:{ Exception -> 0x0187 }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x0187 }
            r0.setSerrandaSet(r1)     // Catch:{ Exception -> 0x0187 }
        L_0x00e0:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ERR     // Catch:{ Exception -> 0x0187 }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x0187 }
            if (r1 == 0) goto L_0x00f1
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ERR     // Catch:{ Exception -> 0x0187 }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x0187 }
            r0.setToconvertError(r1)     // Catch:{ Exception -> 0x0187 }
        L_0x00f1:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ISCRONO     // Catch:{ Exception -> 0x0187 }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x0187 }
            if (r1 == 0) goto L_0x0107
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ISCRONO     // Catch:{ Exception -> 0x0187 }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x0187 }
            if (r1 != r3) goto L_0x0103
            r1 = 1
            goto L_0x0104
        L_0x0103:
            r1 = 0
        L_0x0104:
            r0.setCronoMode(r1)     // Catch:{ Exception -> 0x0187 }
        L_0x0107:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_CRONOON     // Catch:{ Exception -> 0x0187 }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x0187 }
            if (r1 == 0) goto L_0x011c
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_CRONOON     // Catch:{ Exception -> 0x0187 }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x0187 }
            if (r1 != r3) goto L_0x0118
            goto L_0x0119
        L_0x0118:
            r3 = 0
        L_0x0119:
            r0.setFasciaAttiva(r3)     // Catch:{ Exception -> 0x0187 }
        L_0x011c:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_UMD     // Catch:{ Exception -> 0x0187 }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x0187 }
            if (r1 == 0) goto L_0x012d
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_UMD     // Catch:{ Exception -> 0x0187 }
            java.lang.String r1 = r8.getString(r1)     // Catch:{ Exception -> 0x0187 }
            r0.setUmd(r1)     // Catch:{ Exception -> 0x0187 }
        L_0x012d:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_UMDSET     // Catch:{ Exception -> 0x0187 }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x0187 }
            if (r1 == 0) goto L_0x013e
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_UMDSET     // Catch:{ Exception -> 0x0187 }
            java.lang.String r1 = r8.getString(r1)     // Catch:{ Exception -> 0x0187 }
            r0.setSetUmd(r1)     // Catch:{ Exception -> 0x0187 }
        L_0x013e:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_CWIN     // Catch:{ Exception -> 0x0187 }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x0187 }
            if (r1 == 0) goto L_0x0153
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_CWIN     // Catch:{ Exception -> 0x0153 }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x0153 }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ Exception -> 0x0153 }
            r0.setCWin(r1)     // Catch:{ Exception -> 0x0153 }
        L_0x0153:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_CBADGE     // Catch:{ Exception -> 0x0187 }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x0187 }
            if (r1 == 0) goto L_0x0168
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_CBADGE     // Catch:{ Exception -> 0x0168 }
            int r8 = r8.getInt(r1)     // Catch:{ Exception -> 0x0168 }
            java.lang.Integer r8 = java.lang.Integer.valueOf(r8)     // Catch:{ Exception -> 0x0168 }
            r0.setCBadge(r8)     // Catch:{ Exception -> 0x0168 }
        L_0x0168:
            int r8 = r0.getToconvertError()     // Catch:{ Exception -> 0x0187 }
            boolean[] r8 = it.tecnosystemi.TS.Utils.Functions.geterror(r8)     // Catch:{ Exception -> 0x0187 }
            r0.setErrors(r8)     // Catch:{ Exception -> 0x0187 }
            boolean[] r8 = r0.getErrors()     // Catch:{ Exception -> 0x0187 }
            int r1 = r8.length     // Catch:{ Exception -> 0x0187 }
            r3 = 0
        L_0x0179:
            if (r2 >= r1) goto L_0x0184
            boolean r4 = r8[r2]     // Catch:{ Exception -> 0x0187 }
            if (r4 == 0) goto L_0x0181
            int r3 = r3 + 1
        L_0x0181:
            int r2 = r2 + 1
            goto L_0x0179
        L_0x0184:
            r0.setNumError(r3)     // Catch:{ Exception -> 0x0187 }
        L_0x0187:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Model.Zona.getZonaFromJsonOffline(org.json.JSONObject):it.tecnosystemi.TS.Model.Zona");
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(48:0|1|2|(1:4)|5|(1:7)|8|(3:10|(1:12)(1:13)|14)|15|(1:17)|18|(4:20|21|22|23)|24|(4:26|27|28|29)|30|(1:32)|33|(1:35)|36|(1:38)|39|(1:41)|42|(1:44)|45|(1:47)|48|(3:50|(1:52)(1:53)|54)|55|(3:57|(1:59)(1:60)|61)|62|(1:64)|65|(1:67)|68|(2:70|71)|72|73|(2:75|76)|77|78|(4:80|81|(1:83)(1:84)|85)|86|87|(3:89|(2:91|97)(1:98)|92)|96|93|94) */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:72:0x0154 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:77:0x0169 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:86:0x017e */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x015c A[SYNTHETIC, Splitter:B:75:0x015c] */
    /* JADX WARNING: Removed duplicated region for block: B:80:0x0171 A[SYNTHETIC, Splitter:B:80:0x0171] */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0191 A[Catch:{ Exception -> 0x019d }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static it.tecnosystemi.TS.Model.Zona getZonaFromJsonOfflineRidotto(org.json.JSONObject r8) {
        /*
            it.tecnosystemi.TS.Model.Zona r0 = new it.tecnosystemi.TS.Model.Zona
            r0.<init>()
            com.google.gson.Gson r1 = new com.google.gson.Gson
            r1.<init>()
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_IDZONAGETSTATE     // Catch:{ Exception -> 0x019d }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x019d }
            if (r1 == 0) goto L_0x001b
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_IDZONAGETSTATE     // Catch:{ Exception -> 0x019d }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x019d }
            r0.setZoneId(r1)     // Catch:{ Exception -> 0x019d }
        L_0x001b:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_NRZONAGETSTATE     // Catch:{ Exception -> 0x019d }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x019d }
            if (r1 == 0) goto L_0x002c
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_NRZONAGETSTATE     // Catch:{ Exception -> 0x019d }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x019d }
            r0.setZoneId(r1)     // Catch:{ Exception -> 0x019d }
        L_0x002c:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ISOFF_R     // Catch:{ Exception -> 0x019d }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x019d }
            r2 = 0
            r3 = 1
            if (r1 == 0) goto L_0x0044
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ISOFF_R     // Catch:{ Exception -> 0x019d }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x019d }
            if (r1 != r3) goto L_0x0040
            r1 = 1
            goto L_0x0041
        L_0x0040:
            r1 = 0
        L_0x0041:
            r0.setOff(r1)     // Catch:{ Exception -> 0x019d }
        L_0x0044:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_NAME_R     // Catch:{ Exception -> 0x019d }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x019d }
            if (r1 == 0) goto L_0x0055
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_NAME_R     // Catch:{ Exception -> 0x019d }
            java.lang.String r1 = r8.getString(r1)     // Catch:{ Exception -> 0x019d }
            r0.setName(r1)     // Catch:{ Exception -> 0x019d }
        L_0x0055:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_TEMP     // Catch:{ Exception -> 0x019d }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x019d }
            r4 = 4621819117588971520(0x4024000000000000, double:10.0)
            if (r1 == 0) goto L_0x0071
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_TEMP     // Catch:{ Exception -> 0x019d }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x019d }
            double r6 = (double) r1
            java.lang.Double.isNaN(r6)
            double r6 = r6 / r4
            java.lang.String r1 = java.lang.String.valueOf(r6)     // Catch:{ Exception -> 0x019d }
            r0.setTemp(r1)     // Catch:{ Exception -> 0x019d }
        L_0x0071:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_TEMPSET_R     // Catch:{ Exception -> 0x019d }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x019d }
            if (r1 == 0) goto L_0x008b
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_TEMPSET_R     // Catch:{ Exception -> 0x019d }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x019d }
            double r6 = (double) r1
            java.lang.Double.isNaN(r6)
            double r6 = r6 / r4
            java.lang.String r1 = java.lang.String.valueOf(r6)     // Catch:{ Exception -> 0x019d }
            r0.setSetTemp(r1)     // Catch:{ Exception -> 0x019d }
        L_0x008b:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_FAN     // Catch:{ Exception -> 0x019d }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x019d }
            if (r1 == 0) goto L_0x009c
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_FAN     // Catch:{ Exception -> 0x019d }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x019d }
            r0.setFancoil(r1)     // Catch:{ Exception -> 0x019d }
        L_0x009c:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_FANSET     // Catch:{ Exception -> 0x019d }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x019d }
            if (r1 == 0) goto L_0x00ad
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_FANSET     // Catch:{ Exception -> 0x019d }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x019d }
            r0.setFancoilSet(r1)     // Catch:{ Exception -> 0x019d }
        L_0x00ad:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_EV     // Catch:{ Exception -> 0x019d }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x019d }
            if (r1 == 0) goto L_0x00be
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_EV     // Catch:{ Exception -> 0x019d }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x019d }
            r0.setEV(r1)     // Catch:{ Exception -> 0x019d }
        L_0x00be:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_SHU     // Catch:{ Exception -> 0x019d }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x019d }
            if (r1 == 0) goto L_0x00cf
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_SHU     // Catch:{ Exception -> 0x019d }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x019d }
            r0.setSerranda(r1)     // Catch:{ Exception -> 0x019d }
        L_0x00cf:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_SHUSET     // Catch:{ Exception -> 0x019d }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x019d }
            if (r1 == 0) goto L_0x00e0
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_SHUSET     // Catch:{ Exception -> 0x019d }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x019d }
            r0.setSerrandaSet(r1)     // Catch:{ Exception -> 0x019d }
        L_0x00e0:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ERR     // Catch:{ Exception -> 0x019d }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x019d }
            if (r1 == 0) goto L_0x00f1
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ERR     // Catch:{ Exception -> 0x019d }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x019d }
            r0.setToconvertError(r1)     // Catch:{ Exception -> 0x019d }
        L_0x00f1:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ISCRONO     // Catch:{ Exception -> 0x019d }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x019d }
            if (r1 == 0) goto L_0x0107
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_ISCRONO     // Catch:{ Exception -> 0x019d }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x019d }
            if (r1 != r3) goto L_0x0103
            r1 = 1
            goto L_0x0104
        L_0x0103:
            r1 = 0
        L_0x0104:
            r0.setCronoMode(r1)     // Catch:{ Exception -> 0x019d }
        L_0x0107:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_CRONOON     // Catch:{ Exception -> 0x019d }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x019d }
            if (r1 == 0) goto L_0x011d
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_CRONOON     // Catch:{ Exception -> 0x019d }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x019d }
            if (r1 != r3) goto L_0x0119
            r1 = 1
            goto L_0x011a
        L_0x0119:
            r1 = 0
        L_0x011a:
            r0.setFasciaAttiva(r1)     // Catch:{ Exception -> 0x019d }
        L_0x011d:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_UMD     // Catch:{ Exception -> 0x019d }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x019d }
            if (r1 == 0) goto L_0x012e
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_UMD     // Catch:{ Exception -> 0x019d }
            java.lang.String r1 = r8.getString(r1)     // Catch:{ Exception -> 0x019d }
            r0.setUmd(r1)     // Catch:{ Exception -> 0x019d }
        L_0x012e:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_UMDSET_R     // Catch:{ Exception -> 0x019d }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x019d }
            if (r1 == 0) goto L_0x013f
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_UMDSET_R     // Catch:{ Exception -> 0x019d }
            java.lang.String r1 = r8.getString(r1)     // Catch:{ Exception -> 0x019d }
            r0.setSetUmd(r1)     // Catch:{ Exception -> 0x019d }
        L_0x013f:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_CWIN_R     // Catch:{ Exception -> 0x019d }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x019d }
            if (r1 == 0) goto L_0x0154
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_CWIN_R     // Catch:{ Exception -> 0x0154 }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x0154 }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ Exception -> 0x0154 }
            r0.setCWin(r1)     // Catch:{ Exception -> 0x0154 }
        L_0x0154:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_CBADGE_R     // Catch:{ Exception -> 0x019d }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x019d }
            if (r1 == 0) goto L_0x0169
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_CBADGE_R     // Catch:{ Exception -> 0x0169 }
            int r1 = r8.getInt(r1)     // Catch:{ Exception -> 0x0169 }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ Exception -> 0x0169 }
            r0.setCBadge(r1)     // Catch:{ Exception -> 0x0169 }
        L_0x0169:
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_CO_R     // Catch:{ Exception -> 0x019d }
            boolean r1 = r8.has(r1)     // Catch:{ Exception -> 0x019d }
            if (r1 == 0) goto L_0x017e
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_CO_R     // Catch:{ Exception -> 0x017e }
            int r8 = r8.getInt(r1)     // Catch:{ Exception -> 0x017e }
            if (r8 != r3) goto L_0x017a
            goto L_0x017b
        L_0x017a:
            r3 = 0
        L_0x017b:
            r0.setCoff(r3)     // Catch:{ Exception -> 0x017e }
        L_0x017e:
            int r8 = r0.getToconvertError()     // Catch:{ Exception -> 0x019d }
            boolean[] r8 = it.tecnosystemi.TS.Utils.Functions.geterror(r8)     // Catch:{ Exception -> 0x019d }
            r0.setErrors(r8)     // Catch:{ Exception -> 0x019d }
            boolean[] r8 = r0.getErrors()     // Catch:{ Exception -> 0x019d }
            int r1 = r8.length     // Catch:{ Exception -> 0x019d }
            r3 = 0
        L_0x018f:
            if (r2 >= r1) goto L_0x019a
            boolean r4 = r8[r2]     // Catch:{ Exception -> 0x019d }
            if (r4 == 0) goto L_0x0197
            int r3 = r3 + 1
        L_0x0197:
            int r2 = r2 + 1
            goto L_0x018f
        L_0x019a:
            r0.setNumError(r3)     // Catch:{ Exception -> 0x019d }
        L_0x019d:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Model.Zona.getZonaFromJsonOfflineRidotto(org.json.JSONObject):it.tecnosystemi.TS.Model.Zona");
    }
}
