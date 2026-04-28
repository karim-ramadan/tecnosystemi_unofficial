package it.tecnosystemi.TS.Model;

import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Utils.Constants;

public class Device {
    long DevId;
    String FWVer;
    boolean IsOff;
    long LVDV_Id;
    int LVDV_Type;
    String LastAddTimezone;
    String LastConfigUpd;
    String LastSyncUpd;
    int NUM_ERROR;
    String Name;
    long OperatingMode;
    String Serial;

    public String getLastAddTimezone() {
        return this.LastAddTimezone;
    }

    public void setLastAddTimezone(String str) {
        this.LastAddTimezone = str;
    }

    public int getLVDV_Type() {
        return this.LVDV_Type;
    }

    public void setLVDV_Type(int i) {
        this.LVDV_Type = i;
    }

    public long getLVDV_Id() {
        return this.LVDV_Id;
    }

    public void setLVDV_Id(long j) {
        this.LVDV_Id = j;
    }

    public long getDevId() {
        return this.DevId;
    }

    public void setDevId(long j) {
        this.DevId = j;
    }

    public String getSerial() {
        return this.Serial;
    }

    public void setSerial(String str) {
        this.Serial = str;
    }

    public String getName() {
        String str = this.Name;
        if (str != null) {
            return str.toUpperCase();
        }
        return "";
    }

    public void setName(String str) {
        if (str != null) {
            str = str.toUpperCase();
        }
        this.Name = str;
    }

    public String getFWVer() {
        return this.FWVer;
    }

    public void setFWVer(String str) {
        this.FWVer = str;
    }

    public long getOperatingMode() {
        return this.OperatingMode;
    }

    public void setOperatingMode(long j) {
        this.OperatingMode = j;
    }

    public boolean isOff() {
        return this.IsOff;
    }

    public void setOff(boolean z) {
        this.IsOff = z;
    }

    public String getLastConfigUpd() {
        return this.LastConfigUpd;
    }

    public void setLastConfigUpd(String str) {
        this.LastConfigUpd = str;
    }

    public String getLastSyncUpd() {
        return this.LastSyncUpd;
    }

    public void setLastSyncUpd(String str) {
        this.LastSyncUpd = str;
    }

    public int getNUM_ERROR() {
        return this.NUM_ERROR;
    }

    public void setNUM_ERROR(int i) {
        this.NUM_ERROR = i;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Device)) {
            return false;
        }
        Device device = (Device) obj;
        if (device.getLVDV_Type() != getLVDV_Type() || !device.getSerial().equals(getSerial())) {
            return false;
        }
        return true;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(9:2|3|(3:7|8|9)|10|11|(5:14|(4:17|(2:21|28)|22|15)|27|23|12)|26|24|31) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:10:0x0035 */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0043 A[Catch:{ Exception -> 0x00b9 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void updateDevice(it.tecnosystemi.TS.Model.Device r8, it.tecnosystemi.TS.Activity.BaseActivity r9) {
        /*
            it.tecnosystemi.TS.Utils.SavePreferences r0 = new it.tecnosystemi.TS.Utils.SavePreferences
            int r1 = it.tecnosystemi.TS.R.string.PrefsName
            java.lang.String r1 = r9.getString(r1)
            r0.<init>(r9, r1)
            java.lang.String r9 = "DevsTS"
            java.lang.String r1 = r0.getString(r9)
            if (r1 == 0) goto L_0x00b9
            java.util.HashMap r2 = new java.util.HashMap     // Catch:{ Exception -> 0x00b9 }
            r2.<init>()     // Catch:{ Exception -> 0x00b9 }
            if (r1 == 0) goto L_0x0035
            boolean r3 = r1.isEmpty()     // Catch:{ Exception -> 0x00b9 }
            if (r3 != 0) goto L_0x0035
            it.tecnosystemi.TS.Model.Device$1 r3 = new it.tecnosystemi.TS.Model.Device$1     // Catch:{ Exception -> 0x0035 }
            r3.<init>()     // Catch:{ Exception -> 0x0035 }
            java.lang.reflect.Type r3 = r3.getType()     // Catch:{ Exception -> 0x0035 }
            com.google.gson.Gson r4 = new com.google.gson.Gson     // Catch:{ Exception -> 0x0035 }
            r4.<init>()     // Catch:{ Exception -> 0x0035 }
            java.lang.Object r1 = r4.fromJson((java.lang.String) r1, (java.lang.reflect.Type) r3)     // Catch:{ Exception -> 0x0035 }
            java.util.HashMap r1 = (java.util.HashMap) r1     // Catch:{ Exception -> 0x0035 }
            r2 = r1
        L_0x0035:
            java.util.Set r1 = r2.keySet()     // Catch:{ Exception -> 0x00b9 }
            java.util.Iterator r1 = r1.iterator()     // Catch:{ Exception -> 0x00b9 }
        L_0x003d:
            boolean r3 = r1.hasNext()     // Catch:{ Exception -> 0x00b9 }
            if (r3 == 0) goto L_0x00ad
            java.lang.Object r3 = r1.next()     // Catch:{ Exception -> 0x00b9 }
            java.lang.Long r3 = (java.lang.Long) r3     // Catch:{ Exception -> 0x00b9 }
            r3.longValue()     // Catch:{ Exception -> 0x00b9 }
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ Exception -> 0x00b9 }
            r4.<init>()     // Catch:{ Exception -> 0x00b9 }
            r5 = 0
        L_0x0052:
            java.lang.Object r6 = r2.get(r3)     // Catch:{ Exception -> 0x00b9 }
            java.util.List r6 = (java.util.List) r6     // Catch:{ Exception -> 0x00b9 }
            int r6 = r6.size()     // Catch:{ Exception -> 0x00b9 }
            if (r5 >= r6) goto L_0x00a9
            java.lang.Object r6 = r2.get(r3)     // Catch:{ Exception -> 0x00b9 }
            java.util.List r6 = (java.util.List) r6     // Catch:{ Exception -> 0x00b9 }
            java.lang.Object r6 = r6.get(r5)     // Catch:{ Exception -> 0x00b9 }
            it.tecnosystemi.TS.Model.Device r6 = (it.tecnosystemi.TS.Model.Device) r6     // Catch:{ Exception -> 0x00b9 }
            int r6 = r6.getLVDV_Type()     // Catch:{ Exception -> 0x00b9 }
            int r7 = r8.getLVDV_Type()     // Catch:{ Exception -> 0x00b9 }
            if (r6 != r7) goto L_0x0097
            java.lang.Object r6 = r2.get(r3)     // Catch:{ Exception -> 0x00b9 }
            java.util.List r6 = (java.util.List) r6     // Catch:{ Exception -> 0x00b9 }
            java.lang.Object r6 = r6.get(r5)     // Catch:{ Exception -> 0x00b9 }
            it.tecnosystemi.TS.Model.Device r6 = (it.tecnosystemi.TS.Model.Device) r6     // Catch:{ Exception -> 0x00b9 }
            java.lang.String r6 = r6.getSerial()     // Catch:{ Exception -> 0x00b9 }
            java.lang.String r7 = r8.getSerial()     // Catch:{ Exception -> 0x00b9 }
            boolean r6 = r6.equals(r7)     // Catch:{ Exception -> 0x00b9 }
            if (r6 == 0) goto L_0x0097
            java.lang.Object r6 = r2.get(r3)     // Catch:{ Exception -> 0x00b9 }
            java.util.List r6 = (java.util.List) r6     // Catch:{ Exception -> 0x00b9 }
            r6.set(r5, r8)     // Catch:{ Exception -> 0x00b9 }
        L_0x0097:
            java.lang.Object r6 = r2.get(r3)     // Catch:{ Exception -> 0x00b9 }
            java.util.List r6 = (java.util.List) r6     // Catch:{ Exception -> 0x00b9 }
            java.lang.Object r6 = r6.get(r5)     // Catch:{ Exception -> 0x00b9 }
            it.tecnosystemi.TS.Model.Device r6 = (it.tecnosystemi.TS.Model.Device) r6     // Catch:{ Exception -> 0x00b9 }
            r4.add(r6)     // Catch:{ Exception -> 0x00b9 }
            int r5 = r5 + 1
            goto L_0x0052
        L_0x00a9:
            r2.put(r3, r4)     // Catch:{ Exception -> 0x00b9 }
            goto L_0x003d
        L_0x00ad:
            com.google.gson.Gson r8 = new com.google.gson.Gson     // Catch:{ Exception -> 0x00b9 }
            r8.<init>()     // Catch:{ Exception -> 0x00b9 }
            java.lang.String r8 = r8.toJson((java.lang.Object) r2)     // Catch:{ Exception -> 0x00b9 }
            r0.save((java.lang.String) r9, (java.lang.String) r8)     // Catch:{ Exception -> 0x00b9 }
        L_0x00b9:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Model.Device.updateDevice(it.tecnosystemi.TS.Model.Device, it.tecnosystemi.TS.Activity.BaseActivity):void");
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(8:2|3|(3:7|8|9)|10|11|(3:14|(2:15|(2:17|(1:23)(3:26|21|22))(1:27))|12)|25|32) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:10:0x0035 */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0043 A[Catch:{ Exception -> 0x0091 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static it.tecnosystemi.TS.Model.Device getFromPref(java.lang.String r4, int r5, it.tecnosystemi.TS.Activity.BaseActivity r6) {
        /*
            it.tecnosystemi.TS.Utils.SavePreferences r0 = new it.tecnosystemi.TS.Utils.SavePreferences
            int r1 = it.tecnosystemi.TS.R.string.PrefsName
            java.lang.String r1 = r6.getString(r1)
            r0.<init>(r6, r1)
            java.lang.String r6 = "DevsTS"
            java.lang.String r6 = r0.getString(r6)
            if (r6 == 0) goto L_0x0091
            java.util.HashMap r0 = new java.util.HashMap     // Catch:{ Exception -> 0x0091 }
            r0.<init>()     // Catch:{ Exception -> 0x0091 }
            if (r6 == 0) goto L_0x0035
            boolean r1 = r6.isEmpty()     // Catch:{ Exception -> 0x0091 }
            if (r1 != 0) goto L_0x0035
            it.tecnosystemi.TS.Model.Device$2 r1 = new it.tecnosystemi.TS.Model.Device$2     // Catch:{ Exception -> 0x0035 }
            r1.<init>()     // Catch:{ Exception -> 0x0035 }
            java.lang.reflect.Type r1 = r1.getType()     // Catch:{ Exception -> 0x0035 }
            com.google.gson.Gson r2 = new com.google.gson.Gson     // Catch:{ Exception -> 0x0035 }
            r2.<init>()     // Catch:{ Exception -> 0x0035 }
            java.lang.Object r6 = r2.fromJson((java.lang.String) r6, (java.lang.reflect.Type) r1)     // Catch:{ Exception -> 0x0035 }
            java.util.HashMap r6 = (java.util.HashMap) r6     // Catch:{ Exception -> 0x0035 }
            r0 = r6
        L_0x0035:
            java.util.Set r6 = r0.keySet()     // Catch:{ Exception -> 0x0091 }
            java.util.Iterator r6 = r6.iterator()     // Catch:{ Exception -> 0x0091 }
        L_0x003d:
            boolean r1 = r6.hasNext()     // Catch:{ Exception -> 0x0091 }
            if (r1 == 0) goto L_0x0091
            java.lang.Object r1 = r6.next()     // Catch:{ Exception -> 0x0091 }
            java.lang.Long r1 = (java.lang.Long) r1     // Catch:{ Exception -> 0x0091 }
            r1.longValue()     // Catch:{ Exception -> 0x0091 }
            r2 = 0
        L_0x004d:
            java.lang.Object r3 = r0.get(r1)     // Catch:{ Exception -> 0x0091 }
            java.util.List r3 = (java.util.List) r3     // Catch:{ Exception -> 0x0091 }
            int r3 = r3.size()     // Catch:{ Exception -> 0x0091 }
            if (r2 >= r3) goto L_0x003d
            java.lang.Object r3 = r0.get(r1)     // Catch:{ Exception -> 0x0091 }
            java.util.List r3 = (java.util.List) r3     // Catch:{ Exception -> 0x0091 }
            java.lang.Object r3 = r3.get(r2)     // Catch:{ Exception -> 0x0091 }
            it.tecnosystemi.TS.Model.Device r3 = (it.tecnosystemi.TS.Model.Device) r3     // Catch:{ Exception -> 0x0091 }
            int r3 = r3.getLVDV_Type()     // Catch:{ Exception -> 0x0091 }
            if (r3 != r5) goto L_0x008e
            java.lang.Object r3 = r0.get(r1)     // Catch:{ Exception -> 0x0091 }
            java.util.List r3 = (java.util.List) r3     // Catch:{ Exception -> 0x0091 }
            java.lang.Object r3 = r3.get(r2)     // Catch:{ Exception -> 0x0091 }
            it.tecnosystemi.TS.Model.Device r3 = (it.tecnosystemi.TS.Model.Device) r3     // Catch:{ Exception -> 0x0091 }
            java.lang.String r3 = r3.getSerial()     // Catch:{ Exception -> 0x0091 }
            boolean r3 = r3.equals(r4)     // Catch:{ Exception -> 0x0091 }
            if (r3 == 0) goto L_0x008e
            java.lang.Object r4 = r0.get(r1)     // Catch:{ Exception -> 0x0091 }
            java.util.List r4 = (java.util.List) r4     // Catch:{ Exception -> 0x0091 }
            java.lang.Object r4 = r4.get(r2)     // Catch:{ Exception -> 0x0091 }
            it.tecnosystemi.TS.Model.Device r4 = (it.tecnosystemi.TS.Model.Device) r4     // Catch:{ Exception -> 0x0091 }
            return r4
        L_0x008e:
            int r2 = r2 + 1
            goto L_0x004d
        L_0x0091:
            r4 = 0
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Model.Device.getFromPref(java.lang.String, int, it.tecnosystemi.TS.Activity.BaseActivity):it.tecnosystemi.TS.Model.Device");
    }

    public static void deleteDevPICOFromPef(String str, BaseActivity baseActivity) {
        deleteDevFromPref(str, Constants.DEVICE_TYPE_PICO, baseActivity);
    }

    public static void deleteDevFromPref(String str, int i, BaseActivity baseActivity) {
        Device device = new Device();
        device.setSerial(str);
        device.setLVDV_Type(i);
        deleteDevFromPref(device, baseActivity);
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(9:2|3|(3:7|8|9)|10|11|(5:14|(4:17|(2:22|29)(2:21|30)|23|15)|28|24|12)|27|25|31) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:10:0x0035 */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x0043 A[Catch:{ Exception -> 0x00b1 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void deleteDevFromPref(it.tecnosystemi.TS.Model.Device r8, it.tecnosystemi.TS.Activity.BaseActivity r9) {
        /*
            it.tecnosystemi.TS.Utils.SavePreferences r0 = new it.tecnosystemi.TS.Utils.SavePreferences
            int r1 = it.tecnosystemi.TS.R.string.PrefsName
            java.lang.String r1 = r9.getString(r1)
            r0.<init>(r9, r1)
            java.lang.String r9 = "DevsTS"
            java.lang.String r1 = r0.getString(r9)
            if (r1 == 0) goto L_0x00b1
            java.util.HashMap r2 = new java.util.HashMap     // Catch:{ Exception -> 0x00b1 }
            r2.<init>()     // Catch:{ Exception -> 0x00b1 }
            if (r1 == 0) goto L_0x0035
            boolean r3 = r1.isEmpty()     // Catch:{ Exception -> 0x00b1 }
            if (r3 != 0) goto L_0x0035
            it.tecnosystemi.TS.Model.Device$3 r3 = new it.tecnosystemi.TS.Model.Device$3     // Catch:{ Exception -> 0x0035 }
            r3.<init>()     // Catch:{ Exception -> 0x0035 }
            java.lang.reflect.Type r3 = r3.getType()     // Catch:{ Exception -> 0x0035 }
            com.google.gson.Gson r4 = new com.google.gson.Gson     // Catch:{ Exception -> 0x0035 }
            r4.<init>()     // Catch:{ Exception -> 0x0035 }
            java.lang.Object r1 = r4.fromJson((java.lang.String) r1, (java.lang.reflect.Type) r3)     // Catch:{ Exception -> 0x0035 }
            java.util.HashMap r1 = (java.util.HashMap) r1     // Catch:{ Exception -> 0x0035 }
            r2 = r1
        L_0x0035:
            java.util.Set r1 = r2.keySet()     // Catch:{ Exception -> 0x00b1 }
            java.util.Iterator r1 = r1.iterator()     // Catch:{ Exception -> 0x00b1 }
        L_0x003d:
            boolean r3 = r1.hasNext()     // Catch:{ Exception -> 0x00b1 }
            if (r3 == 0) goto L_0x00a5
            java.lang.Object r3 = r1.next()     // Catch:{ Exception -> 0x00b1 }
            java.lang.Long r3 = (java.lang.Long) r3     // Catch:{ Exception -> 0x00b1 }
            r3.longValue()     // Catch:{ Exception -> 0x00b1 }
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ Exception -> 0x00b1 }
            r4.<init>()     // Catch:{ Exception -> 0x00b1 }
            r5 = 0
        L_0x0052:
            java.lang.Object r6 = r2.get(r3)     // Catch:{ Exception -> 0x00b1 }
            java.util.List r6 = (java.util.List) r6     // Catch:{ Exception -> 0x00b1 }
            int r6 = r6.size()     // Catch:{ Exception -> 0x00b1 }
            if (r5 >= r6) goto L_0x00a1
            java.lang.Object r6 = r2.get(r3)     // Catch:{ Exception -> 0x00b1 }
            java.util.List r6 = (java.util.List) r6     // Catch:{ Exception -> 0x00b1 }
            java.lang.Object r6 = r6.get(r5)     // Catch:{ Exception -> 0x00b1 }
            it.tecnosystemi.TS.Model.Device r6 = (it.tecnosystemi.TS.Model.Device) r6     // Catch:{ Exception -> 0x00b1 }
            int r6 = r6.getLVDV_Type()     // Catch:{ Exception -> 0x00b1 }
            int r7 = r8.getLVDV_Type()     // Catch:{ Exception -> 0x00b1 }
            if (r6 != r7) goto L_0x008f
            java.lang.Object r6 = r2.get(r3)     // Catch:{ Exception -> 0x00b1 }
            java.util.List r6 = (java.util.List) r6     // Catch:{ Exception -> 0x00b1 }
            java.lang.Object r6 = r6.get(r5)     // Catch:{ Exception -> 0x00b1 }
            it.tecnosystemi.TS.Model.Device r6 = (it.tecnosystemi.TS.Model.Device) r6     // Catch:{ Exception -> 0x00b1 }
            java.lang.String r6 = r6.getSerial()     // Catch:{ Exception -> 0x00b1 }
            java.lang.String r7 = r8.getSerial()     // Catch:{ Exception -> 0x00b1 }
            boolean r6 = r6.equals(r7)     // Catch:{ Exception -> 0x00b1 }
            if (r6 == 0) goto L_0x008f
            goto L_0x009e
        L_0x008f:
            java.lang.Object r6 = r2.get(r3)     // Catch:{ Exception -> 0x00b1 }
            java.util.List r6 = (java.util.List) r6     // Catch:{ Exception -> 0x00b1 }
            java.lang.Object r6 = r6.get(r5)     // Catch:{ Exception -> 0x00b1 }
            it.tecnosystemi.TS.Model.Device r6 = (it.tecnosystemi.TS.Model.Device) r6     // Catch:{ Exception -> 0x00b1 }
            r4.add(r6)     // Catch:{ Exception -> 0x00b1 }
        L_0x009e:
            int r5 = r5 + 1
            goto L_0x0052
        L_0x00a1:
            r2.put(r3, r4)     // Catch:{ Exception -> 0x00b1 }
            goto L_0x003d
        L_0x00a5:
            com.google.gson.Gson r8 = new com.google.gson.Gson     // Catch:{ Exception -> 0x00b1 }
            r8.<init>()     // Catch:{ Exception -> 0x00b1 }
            java.lang.String r8 = r8.toJson((java.lang.Object) r2)     // Catch:{ Exception -> 0x00b1 }
            r0.save((java.lang.String) r9, (java.lang.String) r8)     // Catch:{ Exception -> 0x00b1 }
        L_0x00b1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Model.Device.deleteDevFromPref(it.tecnosystemi.TS.Model.Device, it.tecnosystemi.TS.Activity.BaseActivity):void");
    }
}
