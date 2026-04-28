package it.tecnosystemi.TS.Model;

import java.util.List;

public class Plant {
    int LVPL_Icon;
    long LVPL_Id;
    String LVPL_Name;
    long LVPL_USAN_Id;
    List<Device> ListDevices;

    public long getLVPL_Id() {
        return this.LVPL_Id;
    }

    public void setLVPL_Id(long j) {
        this.LVPL_Id = j;
    }

    public long getLVPL_USAN_Id() {
        return this.LVPL_USAN_Id;
    }

    public void setLVPL_USAN_Id(long j) {
        this.LVPL_USAN_Id = j;
    }

    public String getLVPL_Name() {
        String str = this.LVPL_Name;
        if (str != null) {
            return str.toUpperCase();
        }
        return null;
    }

    public void setLVPL_Name(String str) {
        if (str != null) {
            str = str.toUpperCase();
        }
        this.LVPL_Name = str;
    }

    public int getLVPL_Icon() {
        return this.LVPL_Icon;
    }

    public void setLVPL_Icon(int i) {
        this.LVPL_Icon = i;
    }

    public List<Device> getListDevices() {
        return this.ListDevices;
    }

    public void setListDevices(List<Device> list) {
        this.ListDevices = list;
    }

    public int getNumError() {
        if (this.ListDevices == null) {
            return 0;
        }
        int i = 0;
        for (int i2 = 0; i2 < this.ListDevices.size(); i2++) {
            if (this.ListDevices.get(i2) != null) {
                i += this.ListDevices.get(i2).getNUM_ERROR();
            }
        }
        return i;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(6:2|3|(3:7|8|9)|10|11|13) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:10:0x0035 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void addDeviceToPlantPref(it.tecnosystemi.TS.Model.Device r5, long r6, it.tecnosystemi.TS.Activity.BaseActivity r8) {
        /*
            it.tecnosystemi.TS.Utils.SavePreferences r0 = new it.tecnosystemi.TS.Utils.SavePreferences
            int r1 = it.tecnosystemi.TS.R.string.PrefsName
            java.lang.String r1 = r8.getString(r1)
            r0.<init>(r8, r1)
            java.lang.String r8 = "DevsTS"
            java.lang.String r1 = r0.getString(r8)
            if (r1 == 0) goto L_0x004e
            java.util.HashMap r2 = new java.util.HashMap     // Catch:{ Exception -> 0x004e }
            r2.<init>()     // Catch:{ Exception -> 0x004e }
            if (r1 == 0) goto L_0x0035
            boolean r3 = r1.isEmpty()     // Catch:{ Exception -> 0x004e }
            if (r3 != 0) goto L_0x0035
            it.tecnosystemi.TS.Model.Plant$1 r3 = new it.tecnosystemi.TS.Model.Plant$1     // Catch:{ Exception -> 0x0035 }
            r3.<init>()     // Catch:{ Exception -> 0x0035 }
            java.lang.reflect.Type r3 = r3.getType()     // Catch:{ Exception -> 0x0035 }
            com.google.gson.Gson r4 = new com.google.gson.Gson     // Catch:{ Exception -> 0x0035 }
            r4.<init>()     // Catch:{ Exception -> 0x0035 }
            java.lang.Object r1 = r4.fromJson((java.lang.String) r1, (java.lang.reflect.Type) r3)     // Catch:{ Exception -> 0x0035 }
            java.util.HashMap r1 = (java.util.HashMap) r1     // Catch:{ Exception -> 0x0035 }
            r2 = r1
        L_0x0035:
            java.lang.Long r6 = java.lang.Long.valueOf(r6)     // Catch:{ Exception -> 0x004e }
            java.lang.Object r6 = r2.get(r6)     // Catch:{ Exception -> 0x004e }
            java.util.List r6 = (java.util.List) r6     // Catch:{ Exception -> 0x004e }
            r6.add(r5)     // Catch:{ Exception -> 0x004e }
            com.google.gson.Gson r5 = new com.google.gson.Gson     // Catch:{ Exception -> 0x004e }
            r5.<init>()     // Catch:{ Exception -> 0x004e }
            java.lang.String r5 = r5.toJson((java.lang.Object) r2)     // Catch:{ Exception -> 0x004e }
            r0.save((java.lang.String) r8, (java.lang.String) r5)     // Catch:{ Exception -> 0x004e }
        L_0x004e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Model.Plant.addDeviceToPlantPref(it.tecnosystemi.TS.Model.Device, long, it.tecnosystemi.TS.Activity.BaseActivity):void");
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(9:2|3|(3:7|8|9)|10|11|(4:14|(2:19|24)(2:18|25)|20|12)|23|21|26) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:10:0x0035 */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x004b A[Catch:{ Exception -> 0x00ad }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void deleteDevFromPlantPref(it.tecnosystemi.TS.Model.Device r6, long r7, it.tecnosystemi.TS.Activity.BaseActivity r9) {
        /*
            it.tecnosystemi.TS.Utils.SavePreferences r0 = new it.tecnosystemi.TS.Utils.SavePreferences
            int r1 = it.tecnosystemi.TS.R.string.PrefsName
            java.lang.String r1 = r9.getString(r1)
            r0.<init>(r9, r1)
            java.lang.String r9 = "DevsTS"
            java.lang.String r1 = r0.getString(r9)
            if (r1 == 0) goto L_0x00ad
            java.util.HashMap r2 = new java.util.HashMap     // Catch:{ Exception -> 0x00ad }
            r2.<init>()     // Catch:{ Exception -> 0x00ad }
            if (r1 == 0) goto L_0x0035
            boolean r3 = r1.isEmpty()     // Catch:{ Exception -> 0x00ad }
            if (r3 != 0) goto L_0x0035
            it.tecnosystemi.TS.Model.Plant$2 r3 = new it.tecnosystemi.TS.Model.Plant$2     // Catch:{ Exception -> 0x0035 }
            r3.<init>()     // Catch:{ Exception -> 0x0035 }
            java.lang.reflect.Type r3 = r3.getType()     // Catch:{ Exception -> 0x0035 }
            com.google.gson.Gson r4 = new com.google.gson.Gson     // Catch:{ Exception -> 0x0035 }
            r4.<init>()     // Catch:{ Exception -> 0x0035 }
            java.lang.Object r1 = r4.fromJson((java.lang.String) r1, (java.lang.reflect.Type) r3)     // Catch:{ Exception -> 0x0035 }
            java.util.HashMap r1 = (java.util.HashMap) r1     // Catch:{ Exception -> 0x0035 }
            r2 = r1
        L_0x0035:
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x00ad }
            r1.<init>()     // Catch:{ Exception -> 0x00ad }
            r3 = 0
        L_0x003b:
            java.lang.Long r4 = java.lang.Long.valueOf(r7)     // Catch:{ Exception -> 0x00ad }
            java.lang.Object r4 = r2.get(r4)     // Catch:{ Exception -> 0x00ad }
            java.util.List r4 = (java.util.List) r4     // Catch:{ Exception -> 0x00ad }
            int r4 = r4.size()     // Catch:{ Exception -> 0x00ad }
            if (r3 >= r4) goto L_0x009a
            java.lang.Long r4 = java.lang.Long.valueOf(r7)     // Catch:{ Exception -> 0x00ad }
            java.lang.Object r4 = r2.get(r4)     // Catch:{ Exception -> 0x00ad }
            java.util.List r4 = (java.util.List) r4     // Catch:{ Exception -> 0x00ad }
            java.lang.Object r4 = r4.get(r3)     // Catch:{ Exception -> 0x00ad }
            it.tecnosystemi.TS.Model.Device r4 = (it.tecnosystemi.TS.Model.Device) r4     // Catch:{ Exception -> 0x00ad }
            int r4 = r4.getLVDV_Type()     // Catch:{ Exception -> 0x00ad }
            int r5 = r6.getLVDV_Type()     // Catch:{ Exception -> 0x00ad }
            if (r4 != r5) goto L_0x0084
            java.lang.Long r4 = java.lang.Long.valueOf(r7)     // Catch:{ Exception -> 0x00ad }
            java.lang.Object r4 = r2.get(r4)     // Catch:{ Exception -> 0x00ad }
            java.util.List r4 = (java.util.List) r4     // Catch:{ Exception -> 0x00ad }
            java.lang.Object r4 = r4.get(r3)     // Catch:{ Exception -> 0x00ad }
            it.tecnosystemi.TS.Model.Device r4 = (it.tecnosystemi.TS.Model.Device) r4     // Catch:{ Exception -> 0x00ad }
            java.lang.String r4 = r4.getSerial()     // Catch:{ Exception -> 0x00ad }
            java.lang.String r5 = r6.getSerial()     // Catch:{ Exception -> 0x00ad }
            boolean r4 = r4.equals(r5)     // Catch:{ Exception -> 0x00ad }
            if (r4 == 0) goto L_0x0084
            goto L_0x0097
        L_0x0084:
            java.lang.Long r4 = java.lang.Long.valueOf(r7)     // Catch:{ Exception -> 0x00ad }
            java.lang.Object r4 = r2.get(r4)     // Catch:{ Exception -> 0x00ad }
            java.util.List r4 = (java.util.List) r4     // Catch:{ Exception -> 0x00ad }
            java.lang.Object r4 = r4.get(r3)     // Catch:{ Exception -> 0x00ad }
            it.tecnosystemi.TS.Model.Device r4 = (it.tecnosystemi.TS.Model.Device) r4     // Catch:{ Exception -> 0x00ad }
            r1.add(r4)     // Catch:{ Exception -> 0x00ad }
        L_0x0097:
            int r3 = r3 + 1
            goto L_0x003b
        L_0x009a:
            java.lang.Long r6 = java.lang.Long.valueOf(r7)     // Catch:{ Exception -> 0x00ad }
            r2.put(r6, r1)     // Catch:{ Exception -> 0x00ad }
            com.google.gson.Gson r6 = new com.google.gson.Gson     // Catch:{ Exception -> 0x00ad }
            r6.<init>()     // Catch:{ Exception -> 0x00ad }
            java.lang.String r6 = r6.toJson((java.lang.Object) r2)     // Catch:{ Exception -> 0x00ad }
            r0.save((java.lang.String) r9, (java.lang.String) r6)     // Catch:{ Exception -> 0x00ad }
        L_0x00ad:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Model.Plant.deleteDevFromPlantPref(it.tecnosystemi.TS.Model.Device, long, it.tecnosystemi.TS.Activity.BaseActivity):void");
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Plant) || ((Plant) obj).getLVPL_Id() != getLVPL_Id()) {
            return false;
        }
        return true;
    }
}
