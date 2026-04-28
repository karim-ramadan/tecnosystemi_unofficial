package it.tecnosystemi.TS.Threads;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import androidx.preference.PreferenceManager;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Activity.BootloaderActivity;
import it.tecnosystemi.TS.Activity.TS.SelectTypeDevActivity;
import it.tecnosystemi.TS.Model.Response;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import it.tecnosystemi.TS.Utils.SavePreferences;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

public class ThreadDowloadFirmWare extends Thread {
    private static SavePreferences pref;
    BaseActivity activity;
    String method = "GET";
    boolean ok;
    SharedPreferences preferences;
    private Resources res;
    String serial;
    String url;

    public ThreadDowloadFirmWare(BaseActivity baseActivity, String str) {
        this.activity = baseActivity;
        this.url = str;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(baseActivity);
        this.ok = true;
    }

    /* JADX WARNING: Removed duplicated region for block: B:61:0x019a A[SYNTHETIC, Splitter:B:61:0x019a] */
    /* JADX WARNING: Removed duplicated region for block: B:73:0x01e5 A[Catch:{ Exception -> 0x020b }] */
    /* JADX WARNING: Removed duplicated region for block: B:76:0x01f5 A[Catch:{ Exception -> 0x020b }] */
    /* JADX WARNING: Removed duplicated region for block: B:77:0x0200 A[Catch:{ Exception -> 0x020b }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
            r17 = this;
            r1 = r17
            java.lang.String r0 = "[]"
            it.tecnosystemi.TS.Activity.BaseActivity r2 = r1.activity
            r2.showProgress()
            it.tecnosystemi.TS.Activity.BaseActivity r2 = r1.activity
            android.content.res.Resources r2 = r2.getResources()
            r1.res = r2
            it.tecnosystemi.TS.Utils.SavePreferences r2 = new it.tecnosystemi.TS.Utils.SavePreferences
            it.tecnosystemi.TS.Activity.BaseActivity r3 = r1.activity
            int r4 = it.tecnosystemi.TS.R.string.PrefsName
            java.lang.String r4 = r3.getString(r4)
            r2.<init>(r3, r4)
            pref = r2
            com.google.gson.Gson r2 = new com.google.gson.Gson
            r2.<init>()
            r2 = 5
            r3 = 1
            r4 = 0
        L_0x0028:
            if (r2 <= 0) goto L_0x005b
            if (r3 == 0) goto L_0x005b
            java.lang.String r4 = it.tecnosystemi.TS.Utils.Constants.token
            if (r4 == 0) goto L_0x003f
            java.lang.String r4 = it.tecnosystemi.TS.Utils.Constants.token
            java.lang.String r6 = ""
            if (r4 == r6) goto L_0x003f
            it.tecnosystemi.TS.Utils.SavePreferences r4 = pref
            android.content.res.Resources r6 = r1.res
            java.lang.String r4 = it.tecnosystemi.TS.Utils.Functions.calcNewToken(r4, r6)
            goto L_0x0041
        L_0x003f:
            java.lang.String r4 = "Ga5mM61KCm5Bk18lhD5J999jC2Mu0Vaf"
        L_0x0041:
            r8 = r4
            java.lang.String r6 = r1.url
            java.lang.String r9 = it.tecnosystemi.TS.Utils.Constants.user
            r10 = 0
            r11 = 4
            r7 = 0
            it.tecnosystemi.TS.Model.Response r4 = it.tecnosystemi.TS.Threads.WebClientDevWrapper.getNewHttpClient(r6, r7, r8, r9, r10, r11)
            if (r4 == 0) goto L_0x0058
            int r6 = r4.getHttpResponceCode()
            r7 = 401(0x191, float:5.62E-43)
            if (r6 == r7) goto L_0x0058
            r3 = 0
        L_0x0058:
            int r2 = r2 + -1
            goto L_0x0028
        L_0x005b:
            if (r4 != 0) goto L_0x006d
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r1.activity     // Catch:{ Exception -> 0x020b }
            it.tecnosystemi.TS.Threads.ThreadDowloadFirmWare$1 r2 = new it.tecnosystemi.TS.Threads.ThreadDowloadFirmWare$1     // Catch:{ Exception -> 0x020b }
            r2.<init>()     // Catch:{ Exception -> 0x020b }
            r0.runOnUiThread(r2)     // Catch:{ Exception -> 0x020b }
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r1.activity     // Catch:{ Exception -> 0x020b }
            r0.hideProgress()     // Catch:{ Exception -> 0x020b }
            return
        L_0x006d:
            java.lang.String r2 = r4.getHttpResponcePayload()     // Catch:{ Exception -> 0x020b }
            if (r2 != 0) goto L_0x0086
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r1.activity     // Catch:{ Exception -> 0x020b }
            it.tecnosystemi.TS.Threads.ThreadDowloadFirmWare$2 r2 = new it.tecnosystemi.TS.Threads.ThreadDowloadFirmWare$2     // Catch:{ Exception -> 0x020b }
            r2.<init>()     // Catch:{ Exception -> 0x020b }
            r0.runOnUiThread(r2)     // Catch:{ Exception -> 0x020b }
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r1.activity     // Catch:{ Exception -> 0x020b }
            r0.hideProgress()     // Catch:{ Exception -> 0x020b }
            r17.gotobootloader()     // Catch:{ Exception -> 0x020b }
            return
        L_0x0086:
            java.lang.String r2 = "FirmWare"
            int r3 = r4.getHttpResponceCode()     // Catch:{ Exception -> 0x020b }
            java.lang.String r3 = java.lang.String.valueOf(r3)     // Catch:{ Exception -> 0x020b }
            android.util.Log.d(r2, r3)     // Catch:{ Exception -> 0x020b }
            org.json.JSONArray r2 = new org.json.JSONArray     // Catch:{ Exception -> 0x020b }
            java.lang.String r3 = r4.getHttpResponcePayload()     // Catch:{ Exception -> 0x020b }
            r2.<init>(r3)     // Catch:{ Exception -> 0x020b }
            it.tecnosystemi.TS.Activity.BaseActivity r3 = r1.activity     // Catch:{ Exception -> 0x020b }
            android.content.Context r3 = r3.getBaseContext()     // Catch:{ Exception -> 0x020b }
            java.lang.String r4 = "FWDir"
            java.io.File r3 = r3.getFileStreamPath(r4)     // Catch:{ Exception -> 0x020b }
            r3.mkdir()     // Catch:{ Exception -> 0x020b }
            org.json.JSONArray r4 = new org.json.JSONArray     // Catch:{ Exception -> 0x020b }
            r4.<init>()     // Catch:{ Exception -> 0x020b }
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ Exception -> 0x020b }
            r6.<init>()     // Catch:{ Exception -> 0x020b }
            java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ Exception -> 0x020b }
            r7.<init>()     // Catch:{ Exception -> 0x020b }
            java.util.ArrayList r8 = new java.util.ArrayList     // Catch:{ Exception -> 0x020b }
            r8.<init>()     // Catch:{ Exception -> 0x020b }
            java.util.ArrayList r9 = new java.util.ArrayList     // Catch:{ Exception -> 0x020b }
            r9.<init>()     // Catch:{ Exception -> 0x020b }
            java.util.ArrayList r10 = new java.util.ArrayList     // Catch:{ Exception -> 0x020b }
            r10.<init>()     // Catch:{ Exception -> 0x020b }
            java.io.File[] r11 = r3.listFiles()     // Catch:{ Exception -> 0x020b }
            java.lang.String r12 = "Path"
            java.lang.String r13 = "NumByte"
            if (r11 == 0) goto L_0x0168
            int r14 = r11.length     // Catch:{ Exception -> 0x020b }
            r15 = 0
        L_0x00d5:
            if (r15 >= r14) goto L_0x00e7
            r16 = r11[r15]     // Catch:{ Exception -> 0x020b }
            java.lang.String r16 = r16.getName()     // Catch:{ Exception -> 0x020b }
            java.lang.String r5 = r16.toString()     // Catch:{ Exception -> 0x020b }
            r8.add(r5)     // Catch:{ Exception -> 0x020b }
            int r15 = r15 + 1
            goto L_0x00d5
        L_0x00e7:
            r5 = 0
        L_0x00e8:
            int r11 = r2.length()     // Catch:{ Exception -> 0x020b }
            if (r5 >= r11) goto L_0x0103
            org.json.JSONObject r11 = r2.getJSONObject(r5)     // Catch:{ Exception -> 0x020b }
            java.lang.String r11 = r11.getString(r12)     // Catch:{ Exception -> 0x020b }
            r9.add(r11)     // Catch:{ Exception -> 0x020b }
            java.lang.Object r11 = r2.get(r5)     // Catch:{ Exception -> 0x020b }
            r4.put(r11)     // Catch:{ Exception -> 0x020b }
            int r5 = r5 + 1
            goto L_0x00e8
        L_0x0103:
            int r4 = r8.size()     // Catch:{ Exception -> 0x020b }
            if (r4 == 0) goto L_0x014f
            java.util.Iterator r4 = r8.iterator()     // Catch:{ Exception -> 0x020b }
        L_0x010d:
            boolean r5 = r4.hasNext()     // Catch:{ Exception -> 0x020b }
            if (r5 == 0) goto L_0x0123
            java.lang.Object r5 = r4.next()     // Catch:{ Exception -> 0x020b }
            java.lang.String r5 = (java.lang.String) r5     // Catch:{ Exception -> 0x020b }
            boolean r11 = r9.contains(r5)     // Catch:{ Exception -> 0x020b }
            if (r11 != 0) goto L_0x010d
            r7.add(r5)     // Catch:{ Exception -> 0x020b }
            goto L_0x010d
        L_0x0123:
            java.util.Iterator r4 = r9.iterator()     // Catch:{ Exception -> 0x020b }
            r5 = 0
        L_0x0128:
            boolean r9 = r4.hasNext()     // Catch:{ Exception -> 0x020b }
            if (r9 == 0) goto L_0x0182
            java.lang.Object r9 = r4.next()     // Catch:{ Exception -> 0x020b }
            java.lang.String r9 = (java.lang.String) r9     // Catch:{ Exception -> 0x020b }
            boolean r11 = r8.contains(r9)     // Catch:{ Exception -> 0x020b }
            if (r11 != 0) goto L_0x014c
            r6.add(r9)     // Catch:{ Exception -> 0x020b }
            org.json.JSONObject r9 = r2.getJSONObject(r5)     // Catch:{ Exception -> 0x020b }
            int r9 = r9.getInt(r13)     // Catch:{ Exception -> 0x020b }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ Exception -> 0x020b }
            r10.add(r9)     // Catch:{ Exception -> 0x020b }
        L_0x014c:
            int r5 = r5 + 1
            goto L_0x0128
        L_0x014f:
            r4 = 0
        L_0x0150:
            int r5 = r9.size()     // Catch:{ Exception -> 0x020b }
            if (r4 >= r5) goto L_0x0181
            org.json.JSONObject r5 = r2.getJSONObject(r4)     // Catch:{ Exception -> 0x020b }
            int r5 = r5.getInt(r13)     // Catch:{ Exception -> 0x020b }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x020b }
            r10.add(r5)     // Catch:{ Exception -> 0x020b }
            int r4 = r4 + 1
            goto L_0x0150
        L_0x0168:
            r4 = 0
        L_0x0169:
            int r5 = r9.size()     // Catch:{ Exception -> 0x020b }
            if (r4 >= r5) goto L_0x0181
            org.json.JSONObject r5 = r2.getJSONObject(r4)     // Catch:{ Exception -> 0x020b }
            int r5 = r5.getInt(r13)     // Catch:{ Exception -> 0x020b }
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)     // Catch:{ Exception -> 0x020b }
            r10.add(r5)     // Catch:{ Exception -> 0x020b }
            int r4 = r4 + 1
            goto L_0x0169
        L_0x0181:
            r6 = r9
        L_0x0182:
            r1.deleteFw(r7)     // Catch:{ Exception -> 0x020b }
            r1.downloadFw(r6, r10)     // Catch:{ Exception -> 0x020b }
            org.json.JSONArray r4 = new org.json.JSONArray     // Catch:{ Exception -> 0x020b }
            r4.<init>(r0)     // Catch:{ Exception -> 0x020b }
            java.io.File[] r3 = r3.listFiles()     // Catch:{ Exception -> 0x020b }
            java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ Exception -> 0x020b }
            r4.<init>()     // Catch:{ Exception -> 0x020b }
            java.lang.String r5 = "infofw"
            if (r3 == 0) goto L_0x01e5
            int r0 = r3.length     // Catch:{ Exception -> 0x020b }
            r6 = 0
        L_0x019c:
            if (r6 >= r0) goto L_0x01c7
            r7 = r3[r6]     // Catch:{ Exception -> 0x020b }
            r8 = 0
        L_0x01a1:
            int r9 = r2.length()     // Catch:{ Exception -> 0x020b }
            if (r8 >= r9) goto L_0x01c4
            org.json.JSONObject r9 = r2.getJSONObject(r8)     // Catch:{ Exception -> 0x020b }
            java.lang.String r9 = r9.getString(r12)     // Catch:{ Exception -> 0x020b }
            java.lang.String r10 = r7.getName()     // Catch:{ Exception -> 0x020b }
            boolean r9 = r9.equals(r10)     // Catch:{ Exception -> 0x020b }
            if (r9 == 0) goto L_0x01c1
            org.json.JSONObject r7 = r2.getJSONObject(r8)     // Catch:{ Exception -> 0x020b }
            r4.add(r7)     // Catch:{ Exception -> 0x020b }
            goto L_0x01c4
        L_0x01c1:
            int r8 = r8 + 1
            goto L_0x01a1
        L_0x01c4:
            int r6 = r6 + 1
            goto L_0x019c
        L_0x01c7:
            it.tecnosystemi.TS.Threads.ThreadDowloadFirmWare$3 r0 = new it.tecnosystemi.TS.Threads.ThreadDowloadFirmWare$3     // Catch:{ Exception -> 0x020b }
            r0.<init>()     // Catch:{ Exception -> 0x020b }
            java.util.Collections.sort(r4, r0)     // Catch:{ Exception -> 0x020b }
            org.json.JSONArray r0 = new org.json.JSONArray     // Catch:{ Exception -> 0x020b }
            r0.<init>(r4)     // Catch:{ Exception -> 0x020b }
            android.content.SharedPreferences r2 = r1.preferences     // Catch:{ Exception -> 0x020b }
            android.content.SharedPreferences$Editor r2 = r2.edit()     // Catch:{ Exception -> 0x020b }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x020b }
            r2.putString(r5, r0)     // Catch:{ Exception -> 0x020b }
            r2.apply()     // Catch:{ Exception -> 0x020b }
            goto L_0x01f1
        L_0x01e5:
            android.content.SharedPreferences r2 = r1.preferences     // Catch:{ Exception -> 0x020b }
            android.content.SharedPreferences$Editor r2 = r2.edit()     // Catch:{ Exception -> 0x020b }
            r2.putString(r5, r0)     // Catch:{ Exception -> 0x020b }
            r2.apply()     // Catch:{ Exception -> 0x020b }
        L_0x01f1:
            boolean r0 = r1.ok     // Catch:{ Exception -> 0x020b }
            if (r0 == 0) goto L_0x0200
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r1.activity     // Catch:{ Exception -> 0x020b }
            it.tecnosystemi.TS.Threads.ThreadDowloadFirmWare$4 r2 = new it.tecnosystemi.TS.Threads.ThreadDowloadFirmWare$4     // Catch:{ Exception -> 0x020b }
            r2.<init>()     // Catch:{ Exception -> 0x020b }
            r0.runOnUiThread(r2)     // Catch:{ Exception -> 0x020b }
            goto L_0x021c
        L_0x0200:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r1.activity     // Catch:{ Exception -> 0x020b }
            it.tecnosystemi.TS.Threads.ThreadDowloadFirmWare$5 r2 = new it.tecnosystemi.TS.Threads.ThreadDowloadFirmWare$5     // Catch:{ Exception -> 0x020b }
            r2.<init>()     // Catch:{ Exception -> 0x020b }
            r0.runOnUiThread(r2)     // Catch:{ Exception -> 0x020b }
            goto L_0x021c
        L_0x020b:
            r0 = move-exception
            r0.getCause()
            r0.getMessage()
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r1.activity
            it.tecnosystemi.TS.Threads.ThreadDowloadFirmWare$6 r2 = new it.tecnosystemi.TS.Threads.ThreadDowloadFirmWare$6
            r2.<init>()
            r0.runOnUiThread(r2)
        L_0x021c:
            r17.gotobootloader()
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r1.activity
            r0.hideProgress()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Threads.ThreadDowloadFirmWare.run():void");
    }

    /* access modifiers changed from: private */
    public void gotobootloader() {
        if (this.activity.gotobooloader) {
            this.activity.gotobooloader = false;
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    BootloaderActivity.FROMCONFIG = false;
                    Intent intent = new Intent(ThreadDowloadFirmWare.this.activity, SelectTypeDevActivity.class);
                    intent.putExtra(Constants.TS_TIPO_OP, 1);
                    ThreadDowloadFirmWare.this.activity.startActivity(intent);
                }
            });
        }
    }

    private void deleteFw(List<String> list) {
        File file = new File(this.activity.getFilesDir(), Constants.FW_DIRECTORY_NAME);
        for (String file2 : list) {
            new File(file, file2).delete();
        }
    }

    private void downloadFw(List<String> list, List<Integer> list2) {
        String str;
        int i = 0;
        while (i < list.size()) {
            String str2 = this.activity.getResources().getString(R.string.uriWebService) + this.activity.getResources().getString(R.string.uri_FrameWork) + "?filename=" + list.get(i);
            this.res = this.activity.getResources();
            BaseActivity baseActivity = this.activity;
            pref = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
            boolean z = true;
            Response response = null;
            for (int i2 = 5; i2 > 0 && z; i2--) {
                if (Constants.token == null || Constants.token == "") {
                    str = Constants.FIX_TOKEN;
                } else {
                    str = Functions.calcNewToken(pref, this.res);
                }
                response = WebClientDevWrapper.getNewHttpClient(str2, (String) null, str, Constants.user, 0, 5);
                if (!(response == null || response.getHttpResponceCode() == 401)) {
                    z = false;
                }
            }
            if (response == null) {
                this.ok = false;
                return;
            } else if (response.getByteResponsePayload() == null) {
                this.ok = false;
                return;
            } else {
                Log.e("FirmWareDowload", String.valueOf(response.getHttpResponceCode()));
                File file = new File(this.activity.getFilesDir(), Constants.FW_DIRECTORY_NAME);
                try {
                    if (response.getByteResponsePayload().length == list2.get(i).intValue()) {
                        FileOutputStream fileOutputStream = new FileOutputStream(file.getPath() + "/" + list.get(i));
                        fileOutputStream.write(response.getByteResponsePayload());
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    } else {
                        this.ok = false;
                    }
                } catch (Exception unused) {
                    this.ok = false;
                }
                i++;
            }
        }
    }
}
