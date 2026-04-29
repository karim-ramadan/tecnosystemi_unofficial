package it.tecnosystemi.TS.Threads;

import android.content.Intent;
import android.content.res.Resources;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Activity.Config.ConfigActivity;
import it.tecnosystemi.TS.Activity.Config.SetNameAndPinActivity;
import it.tecnosystemi.TS.Activity.ControlUnitActivity;
import it.tecnosystemi.TS.Activity.GDPRActivity;
import it.tecnosystemi.TS.Activity.HomeActivity;
import it.tecnosystemi.TS.Activity.LoginActivity;
import it.tecnosystemi.TS.Activity.PICO.Config.ConfigPICOActivity;
import it.tecnosystemi.TS.Activity.PICO.Config.SetNameAndPinPICOActivity;
import it.tecnosystemi.TS.Activity.PICO.PICOCronoSummaryActivity;
import it.tecnosystemi.TS.Activity.PICO.PicoActivity;
import it.tecnosystemi.TS.Activity.PICO.PicoCascataActivity;
import it.tecnosystemi.TS.Activity.PICO.PicoCronoSetActivity;
import it.tecnosystemi.TS.Activity.SEIX.Config.ConfigSeiXActivity;
import it.tecnosystemi.TS.Activity.SEIX.Config.SetNameAndPinSeiXActivity;
import it.tecnosystemi.TS.Activity.SignUpActivity;
import it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity;
import it.tecnosystemi.TS.Activity.TS.TSHomeActivity;
import it.tecnosystemi.TS.Activity.VMC.Config.ConfigVMCActivity;
import it.tecnosystemi.TS.Activity.VMC.Config.SetNameAndPinVMCActivity;
import it.tecnosystemi.TS.Activity.ZoneActivity;
import it.tecnosystemi.TS.Model.ControlUnit;
import it.tecnosystemi.TS.Model.Response;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import it.tecnosystemi.TS.Utils.SavePreferences;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;

public class ThreadWebService extends Thread {
    BaseActivity activity;
    private int idUtente = -1;
    int method;
    String[] params;
    String payload;
    /* access modifiers changed from: private */
    public Resources res;
    Response response;
    boolean showprogress;
    String token;
    int tried = 1;
    int type;
    String url;

    public void infoFws() {
    }

    public ThreadWebService(BaseActivity baseActivity, int i, int i2, String str, String str2, String[] strArr) {
        this.activity = baseActivity;
        this.method = i;
        this.type = i2;
        this.url = str;
        this.payload = str2;
        this.params = strArr;
        this.showprogress = true;
        this.res = baseActivity.getResources();
    }

    public ThreadWebService(BaseActivity baseActivity, int i, int i2, String str, String str2, String[] strArr, boolean z) {
        this.activity = baseActivity;
        this.method = i;
        this.type = i2;
        this.url = str;
        this.payload = str2;
        this.params = strArr;
        this.showprogress = z;
        this.res = baseActivity.getResources();
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(8:0|(1:7)|8|9|(2:11|(2:13|(2:15|(1:17)(1:44))(1:45))(1:46))(1:47)|48|49|(1:62)(2:55|57)) */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x002b, code lost:
        getPicoFasce();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x002e, code lost:
        getPicoSetFasce();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:?, code lost:
        return;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:48:0x009e */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
            r4 = this;
            int r0 = r4.type
            r1 = 11
            r2 = 9
            if (r0 == r2) goto L_0x0016
            if (r0 == r1) goto L_0x0016
            r3 = 5
            if (r0 == r3) goto L_0x0016
            boolean r0 = r4.showprogress
            if (r0 == 0) goto L_0x0016
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r4.activity
            r0.showProgress()
        L_0x0016:
            int r0 = r4.type     // Catch:{ Exception -> 0x009e }
            if (r0 == 0) goto L_0x009b
            r3 = 1
            if (r0 == r3) goto L_0x0097
            r3 = 2
            if (r0 == r3) goto L_0x0093
            r3 = 3
            if (r0 == r3) goto L_0x008f
            switch(r0) {
                case 8: goto L_0x008b;
                case 9: goto L_0x0087;
                case 10: goto L_0x0083;
                case 11: goto L_0x007f;
                case 12: goto L_0x007b;
                case 13: goto L_0x0077;
                case 14: goto L_0x0073;
                case 15: goto L_0x006f;
                case 16: goto L_0x006b;
                case 17: goto L_0x0067;
                case 18: goto L_0x0063;
                case 19: goto L_0x005f;
                case 20: goto L_0x005b;
                case 21: goto L_0x0057;
                case 22: goto L_0x0053;
                case 23: goto L_0x004f;
                case 24: goto L_0x004b;
                case 25: goto L_0x0083;
                case 26: goto L_0x0047;
                case 27: goto L_0x0042;
                case 28: goto L_0x003d;
                case 29: goto L_0x0038;
                case 30: goto L_0x0033;
                case 31: goto L_0x0033;
                case 32: goto L_0x0028;
                case 33: goto L_0x002b;
                case 34: goto L_0x002e;
                default: goto L_0x0026;
            }     // Catch:{ Exception -> 0x009e }
        L_0x0026:
            goto L_0x009e
        L_0x0028:
            r4.updGDPR()     // Catch:{ Exception -> 0x009e }
        L_0x002b:
            r4.getPicoFasce()     // Catch:{ Exception -> 0x009e }
        L_0x002e:
            r4.getPicoSetFasce()     // Catch:{ Exception -> 0x009e }
            goto L_0x009e
        L_0x0033:
            r4.getPicoSlave()     // Catch:{ Exception -> 0x009e }
            goto L_0x009e
        L_0x0038:
            r4.checkPicoConfig()     // Catch:{ Exception -> 0x009e }
            goto L_0x009e
        L_0x003d:
            r4.picoCMD()     // Catch:{ Exception -> 0x009e }
            goto L_0x009e
        L_0x0042:
            r4.setStatoPico()     // Catch:{ Exception -> 0x009e }
            goto L_0x009e
        L_0x0047:
            r4.getStatoPico()     // Catch:{ Exception -> 0x009e }
            goto L_0x009e
        L_0x004b:
            r4.connUsrPico()     // Catch:{ Exception -> 0x009e }
            goto L_0x009e
        L_0x004f:
            r4.getHomeTS()     // Catch:{ Exception -> 0x009e }
            goto L_0x009e
        L_0x0053:
            r4.plantInsUPD()     // Catch:{ Exception -> 0x009e }
            goto L_0x009e
        L_0x0057:
            r4.restConfig()     // Catch:{ Exception -> 0x009e }
            goto L_0x009e
        L_0x005b:
            r4.delUser()     // Catch:{ Exception -> 0x009e }
            goto L_0x009e
        L_0x005f:
            r4.resetConn()     // Catch:{ Exception -> 0x009e }
            goto L_0x009e
        L_0x0063:
            r4.checkConfigCU()     // Catch:{ Exception -> 0x009e }
            goto L_0x009e
        L_0x0067:
            r4.addNewCu()     // Catch:{ Exception -> 0x009e }
            goto L_0x009e
        L_0x006b:
            r4.updCUDatetime()     // Catch:{ Exception -> 0x009e }
            goto L_0x009e
        L_0x006f:
            r4.updTW()     // Catch:{ Exception -> 0x009e }
            goto L_0x009e
        L_0x0073:
            r4.getTW()     // Catch:{ Exception -> 0x009e }
            goto L_0x009e
        L_0x0077:
            r4.updState()     // Catch:{ Exception -> 0x009e }
            goto L_0x009e
        L_0x007b:
            r4.getCuState()     // Catch:{ Exception -> 0x009e }
            goto L_0x009e
        L_0x007f:
            r4.getCuState()     // Catch:{ Exception -> 0x009e }
            goto L_0x009e
        L_0x0083:
            r4.deleteUsrfromUC()     // Catch:{ Exception -> 0x009e }
            goto L_0x009e
        L_0x0087:
            r4.getHome()     // Catch:{ Exception -> 0x009e }
            goto L_0x009e
        L_0x008b:
            r4.connUsrUC()     // Catch:{ Exception -> 0x009e }
            goto L_0x009e
        L_0x008f:
            r4.recPwd()     // Catch:{ Exception -> 0x009e }
            goto L_0x009e
        L_0x0093:
            r4.logIn()     // Catch:{ Exception -> 0x009e }
            goto L_0x009e
        L_0x0097:
            r4.checkEmail()     // Catch:{ Exception -> 0x009e }
            goto L_0x009e
        L_0x009b:
            r4.signUp()     // Catch:{ Exception -> 0x009e }
        L_0x009e:
            int r0 = r4.type     // Catch:{ Exception -> 0x00b1 }
            r3 = 17
            if (r0 == r3) goto L_0x00b1
            r3 = 18
            if (r0 == r3) goto L_0x00b1
            if (r0 == r1) goto L_0x00b1
            if (r0 == r2) goto L_0x00b1
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r4.activity     // Catch:{ Exception -> 0x00b1 }
            r0.hideProgress()     // Catch:{ Exception -> 0x00b1 }
        L_0x00b1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Threads.ThreadWebService.run():void");
    }

    private void plantInsUPD() {
        BaseActivity baseActivity = this.activity;
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        if (Constants.token == null || Constants.token == "") {
            this.token = Constants.FIX_TOKEN;
        } else {
            this.token = Functions.calcNewToken(savePreferences, this.res);
        }
        this.response = WebClientDevWrapper.getNewHttpClient(this.url, this.payload, this.token, Constants.user, this.method, this.type);
        this.activity.hideProgress();
        Response response2 = this.response;
        if (response2 == null) {
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    Functions.makeErrorToast(ThreadWebService.this.activity, ThreadWebService.this.activity.getResources().getString(R.string.resCodeError));
                }
            });
        } else if (response2.getHttpResponceCode() == 200) {
            try {
                JSONObject jSONObject = new JSONObject(this.response.getHttpResponcePayload());
                if (jSONObject.has("ResCode")) {
                    int i = jSONObject.getInt("ResCode");
                    if (i == 0) {
                        this.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                ThreadWebService.this.activity.setResult(-1);
                                ThreadWebService.this.activity.finish();
                            }
                        });
                    } else if (i == 2) {
                        pinerror();
                    } else {
                        this.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Functions.makeErrorToast(ThreadWebService.this.activity, ThreadWebService.this.res.getString(R.string.msg_commandKo));
                            }
                        });
                    }
                } else {
                    Functions.makeErrorToast(this.activity, this.res.getString(R.string.msg_commandKo));
                }
            } catch (JSONException e) {
                e.printStackTrace();
                this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Functions.makeErrorToast(ThreadWebService.this.activity, ThreadWebService.this.res.getString(R.string.msg_commandKo));
                    }
                });
            }
        } else {
            try {
                JSONObject jSONObject2 = new JSONObject(this.response.getHttpResponcePayload());
                if (this.response.getHttpResponceCode() == 400) {
                    if ((jSONObject2.has("ResCode") ? jSONObject2.getInt("ResCode") : -1) == 2) {
                        pinerror();
                    } else {
                        this.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Functions.makeErrorToast(ThreadWebService.this.activity, ThreadWebService.this.res.getString(R.string.msg_commandKo));
                            }
                        });
                    }
                } else {
                    this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Functions.makeErrorToast(ThreadWebService.this.activity, ThreadWebService.this.activity.getResources().getString(R.string.resCodeError));
                        }
                    });
                }
            } catch (Exception unused) {
            }
        }
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(5:20|(1:22)(2:23|24)|25|26|(2:28|49)(2:29|50)) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:25:0x00a8 */
    /* JADX WARNING: Removed duplicated region for block: B:28:0x00af A[Catch:{ JSONException -> 0x00c9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00b5 A[Catch:{ JSONException -> 0x00c9 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void getHomeTS() {
        /*
            r7 = this;
            it.tecnosystemi.TS.Utils.SavePreferences r0 = new it.tecnosystemi.TS.Utils.SavePreferences
            it.tecnosystemi.TS.Activity.BaseActivity r1 = r7.activity
            int r2 = it.tecnosystemi.TS.R.string.PrefsName
            java.lang.String r2 = r1.getString(r2)
            r0.<init>(r1, r2)
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.token
            if (r1 == 0) goto L_0x0020
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.token
            java.lang.String r2 = ""
            if (r1 == r2) goto L_0x0020
            android.content.res.Resources r1 = r7.res
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Functions.calcNewToken(r0, r1)
            r7.token = r0
            goto L_0x0024
        L_0x0020:
            java.lang.String r0 = "Ga5mM61KCm5Bk18lhD5J999jC2Mu0Vaf"
            r7.token = r0
        L_0x0024:
            java.lang.String r1 = r7.url
            java.lang.String r2 = r7.payload
            java.lang.String r3 = r7.token
            java.lang.String r4 = it.tecnosystemi.TS.Utils.Constants.user
            int r5 = r7.method
            int r6 = r7.type
            it.tecnosystemi.TS.Model.Response r0 = it.tecnosystemi.TS.Threads.WebClientDevWrapper.getNewHttpClient(r1, r2, r3, r4, r5, r6)
            r7.response = r0
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity
            r0.hideProgress()
            it.tecnosystemi.TS.Model.Response r0 = r7.response
            if (r0 != 0) goto L_0x004a
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity
            it.tecnosystemi.TS.Threads.ThreadWebService$7 r1 = new it.tecnosystemi.TS.Threads.ThreadWebService$7
            r1.<init>()
            r0.runOnUiThread(r1)
            return
        L_0x004a:
            int r0 = r0.getHttpResponceCode()
            r1 = 200(0xc8, float:2.8E-43)
            r2 = 2
            java.lang.String r3 = "ResCode"
            if (r0 != r1) goto L_0x00d8
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x00c9 }
            it.tecnosystemi.TS.Model.Response r1 = r7.response     // Catch:{ JSONException -> 0x00c9 }
            java.lang.String r1 = r1.getHttpResponcePayload()     // Catch:{ JSONException -> 0x00c9 }
            r0.<init>(r1)     // Catch:{ JSONException -> 0x00c9 }
            boolean r1 = r0.has(r3)     // Catch:{ JSONException -> 0x00c9 }
            if (r1 == 0) goto L_0x00bb
            int r1 = r0.getInt(r3)     // Catch:{ JSONException -> 0x00c9 }
            if (r1 == 0) goto L_0x007f
            if (r1 != r2) goto L_0x0073
            r7.pinerror()     // Catch:{ JSONException -> 0x00c9 }
            goto L_0x0114
        L_0x0073:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity     // Catch:{ JSONException -> 0x00c9 }
            it.tecnosystemi.TS.Threads.ThreadWebService$8 r1 = new it.tecnosystemi.TS.Threads.ThreadWebService$8     // Catch:{ JSONException -> 0x00c9 }
            r1.<init>()     // Catch:{ JSONException -> 0x00c9 }
            r0.runOnUiThread(r1)     // Catch:{ JSONException -> 0x00c9 }
            goto L_0x0114
        L_0x007f:
            it.tecnosystemi.TS.Threads.ThreadWebService$9 r1 = new it.tecnosystemi.TS.Threads.ThreadWebService$9     // Catch:{ JSONException -> 0x00c9 }
            r1.<init>()     // Catch:{ JSONException -> 0x00c9 }
            java.lang.reflect.Type r1 = r1.getType()     // Catch:{ JSONException -> 0x00c9 }
            com.google.gson.Gson r2 = new com.google.gson.Gson     // Catch:{ JSONException -> 0x00c9 }
            r2.<init>()     // Catch:{ JSONException -> 0x00c9 }
            java.lang.String r3 = "ResDescr"
            java.lang.String r0 = r0.getString(r3)     // Catch:{ JSONException -> 0x00c9 }
            java.lang.Object r0 = r2.fromJson((java.lang.String) r0, (java.lang.reflect.Type) r1)     // Catch:{ JSONException -> 0x00c9 }
            java.util.List r0 = (java.util.List) r0     // Catch:{ JSONException -> 0x00c9 }
            if (r0 != 0) goto L_0x00a3
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ JSONException -> 0x00c9 }
            r0.<init>()     // Catch:{ JSONException -> 0x00c9 }
            it.tecnosystemi.TS.Utils.Constants.listaImpianti = r0     // Catch:{ JSONException -> 0x00c9 }
            goto L_0x00a8
        L_0x00a3:
            it.tecnosystemi.TS.Activity.BaseActivity r1 = r7.activity     // Catch:{ Exception -> 0x00a8 }
            it.tecnosystemi.TS.Utils.Functions.SyncInpianti(r0, r1)     // Catch:{ Exception -> 0x00a8 }
        L_0x00a8:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity     // Catch:{ JSONException -> 0x00c9 }
            boolean r1 = r0 instanceof it.tecnosystemi.TS.Activity.TS.TSHomeActivity     // Catch:{ JSONException -> 0x00c9 }
            r2 = 0
            if (r1 == 0) goto L_0x00b5
            it.tecnosystemi.TS.Activity.TS.TSHomeActivity r0 = (it.tecnosystemi.TS.Activity.TS.TSHomeActivity) r0     // Catch:{ JSONException -> 0x00c9 }
            r0.refreshlist(r2)     // Catch:{ JSONException -> 0x00c9 }
            goto L_0x0114
        L_0x00b5:
            it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r0 = (it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity) r0     // Catch:{ JSONException -> 0x00c9 }
            r0.refreshlist(r2)     // Catch:{ JSONException -> 0x00c9 }
            goto L_0x0114
        L_0x00bb:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity     // Catch:{ JSONException -> 0x00c9 }
            android.content.res.Resources r1 = r7.res     // Catch:{ JSONException -> 0x00c9 }
            int r2 = it.tecnosystemi.TS.R.string.msg_commandKo     // Catch:{ JSONException -> 0x00c9 }
            java.lang.String r1 = r1.getString(r2)     // Catch:{ JSONException -> 0x00c9 }
            it.tecnosystemi.TS.Utils.Functions.makeErrorToast(r0, r1)     // Catch:{ JSONException -> 0x00c9 }
            goto L_0x0114
        L_0x00c9:
            r0 = move-exception
            r0.printStackTrace()
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity
            it.tecnosystemi.TS.Threads.ThreadWebService$10 r1 = new it.tecnosystemi.TS.Threads.ThreadWebService$10
            r1.<init>()
            r0.runOnUiThread(r1)
            goto L_0x0114
        L_0x00d8:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0114 }
            it.tecnosystemi.TS.Model.Response r1 = r7.response     // Catch:{ Exception -> 0x0114 }
            java.lang.String r1 = r1.getHttpResponcePayload()     // Catch:{ Exception -> 0x0114 }
            r0.<init>(r1)     // Catch:{ Exception -> 0x0114 }
            it.tecnosystemi.TS.Model.Response r1 = r7.response     // Catch:{ Exception -> 0x0114 }
            int r1 = r1.getHttpResponceCode()     // Catch:{ Exception -> 0x0114 }
            r4 = 400(0x190, float:5.6E-43)
            if (r1 != r4) goto L_0x010a
            boolean r1 = r0.has(r3)     // Catch:{ Exception -> 0x0114 }
            if (r1 == 0) goto L_0x00f8
            int r0 = r0.getInt(r3)     // Catch:{ Exception -> 0x0114 }
            goto L_0x00f9
        L_0x00f8:
            r0 = -1
        L_0x00f9:
            if (r0 != r2) goto L_0x00ff
            r7.pinerror()     // Catch:{ Exception -> 0x0114 }
            goto L_0x0114
        L_0x00ff:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity     // Catch:{ Exception -> 0x0114 }
            it.tecnosystemi.TS.Threads.ThreadWebService$11 r1 = new it.tecnosystemi.TS.Threads.ThreadWebService$11     // Catch:{ Exception -> 0x0114 }
            r1.<init>()     // Catch:{ Exception -> 0x0114 }
            r0.runOnUiThread(r1)     // Catch:{ Exception -> 0x0114 }
            goto L_0x0114
        L_0x010a:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity     // Catch:{ Exception -> 0x0114 }
            it.tecnosystemi.TS.Threads.ThreadWebService$12 r1 = new it.tecnosystemi.TS.Threads.ThreadWebService$12     // Catch:{ Exception -> 0x0114 }
            r1.<init>()     // Catch:{ Exception -> 0x0114 }
            r0.runOnUiThread(r1)     // Catch:{ Exception -> 0x0114 }
        L_0x0114:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Threads.ThreadWebService.getHomeTS():void");
    }

    public void getStatoPico() {
        BaseActivity baseActivity = this.activity;
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        if (Constants.token == null || Constants.token == "") {
            this.token = Constants.FIX_TOKEN;
        } else {
            this.token = Functions.calcNewToken(savePreferences, this.res);
        }
        Response newHttpClient = WebClientDevWrapper.getNewHttpClient(this.url, this.payload, this.token, Constants.user, this.method, this.type);
        this.response = newHttpClient;
        ((PicoActivity) this.activity).parserServerStato(newHttpClient);
    }

    public synchronized void setStatoPico() {
        BaseActivity baseActivity = this.activity;
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        if (Constants.token == null || Constants.token == "") {
            this.token = Constants.FIX_TOKEN;
        } else {
            this.token = Functions.calcNewToken(savePreferences, this.res);
        }
        Response newHttpClient = WebClientDevWrapper.getNewHttpClient(this.url, this.payload, this.token, Constants.user, this.method, this.type);
        this.response = newHttpClient;
        ((PicoActivity) this.activity).parseRespSetStato(newHttpClient);
    }

    public void picoCMD() {
        BaseActivity baseActivity = this.activity;
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        if (Constants.token == null || Constants.token == "") {
            this.token = Constants.FIX_TOKEN;
        } else {
            this.token = Functions.calcNewToken(savePreferences, this.res);
        }
        Response newHttpClient = WebClientDevWrapper.getNewHttpClient(this.url, this.payload, this.token, Constants.user, this.method, this.type);
        this.response = newHttpClient;
        ((PicoActivity) this.activity).parseRespPicoCmd(newHttpClient, this.type);
    }

    public void checkPicoConfig() {
        BaseActivity baseActivity = this.activity;
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        if (Constants.token == null || Constants.token == "") {
            this.token = Constants.FIX_TOKEN;
        } else {
            this.token = Functions.calcNewToken(savePreferences, this.res);
        }
        Response newHttpClient = WebClientDevWrapper.getNewHttpClient(this.url, this.payload, this.token, Constants.user, this.method, this.type);
        this.response = newHttpClient;
        BaseActivity baseActivity2 = this.activity;
        if (baseActivity2 instanceof ConfigPICOActivity) {
            ((ConfigPICOActivity) baseActivity2).parseCheckConfig(newHttpClient);
        } else if (baseActivity2 instanceof ConfigVMCActivity) {
            ((ConfigVMCActivity) baseActivity2).parseCheckConfig(newHttpClient);
        } else if (baseActivity2 instanceof ConfigSeiXActivity) {
            ((ConfigSeiXActivity) baseActivity2).parseCheckConfig(newHttpClient);
        }
    }

    public void getPicoSlave() {
        BaseActivity baseActivity = this.activity;
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        if (Constants.token == null || Constants.token == "") {
            this.token = Constants.FIX_TOKEN;
        } else {
            this.token = Functions.calcNewToken(savePreferences, this.res);
        }
        Response newHttpClient = WebClientDevWrapper.getNewHttpClient(this.url, this.payload, this.token, Constants.user, this.method, this.type);
        this.response = newHttpClient;
        ((PicoCascataActivity) this.activity).parseGetSetSlaveServer(newHttpClient, this.type);
    }

    public void getPicoFasce() {
        BaseActivity baseActivity = this.activity;
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        if (Constants.token == null || Constants.token == "") {
            this.token = Constants.FIX_TOKEN;
        } else {
            this.token = Functions.calcNewToken(savePreferences, this.res);
        }
        Response newHttpClient = WebClientDevWrapper.getNewHttpClient(this.url, this.payload, this.token, Constants.user, this.method, this.type);
        this.response = newHttpClient;
        ((PICOCronoSummaryActivity) this.activity).parseRespGetFasceServer(newHttpClient, this.type);
    }

    public void getPicoSetFasce() {
        BaseActivity baseActivity = this.activity;
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        if (Constants.token == null || Constants.token == "") {
            this.token = Constants.FIX_TOKEN;
        } else {
            this.token = Functions.calcNewToken(savePreferences, this.res);
        }
        Response newHttpClient = WebClientDevWrapper.getNewHttpClient(this.url, this.payload, this.token, Constants.user, this.method, this.type);
        this.response = newHttpClient;
        ((PicoCronoSetActivity) this.activity).parseRespSetFasceServer(newHttpClient, this.type);
    }

    private void resetConn() {
        int i;
        BaseActivity baseActivity = this.activity;
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        if (Constants.token == null || Constants.token == "") {
            this.token = Constants.FIX_TOKEN;
        } else {
            this.token = Functions.calcNewToken(savePreferences, this.res);
        }
        Response newHttpClient = WebClientDevWrapper.getNewHttpClient(this.url, this.payload, this.token, Constants.user, this.method, 19);
        this.response = newHttpClient;
        JSONObject jSONObject = null;
        if (newHttpClient == null) {
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    Functions.makeErrorToast(ThreadWebService.this.activity, ThreadWebService.this.activity.getResources().getString(R.string.resCodeError));
                }
            });
        } else if (newHttpClient.getHttpResponceCode() == 200) {
            try {
                JSONObject jSONObject2 = new JSONObject(this.response.getHttpResponcePayload());
                if (!jSONObject2.has("ResCode") || (i = jSONObject2.getInt("ResCode")) == 0) {
                    jSONObject = jSONObject2;
                } else if (i == 2) {
                    this.activity.firtCalltoGetState = false;
                    pinerror();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONObject jSONObject3 = new JSONObject(this.response.getHttpResponcePayload());
                if (this.response.getHttpResponceCode() == 400) {
                    if ((jSONObject3.has("ResCode") ? jSONObject3.getInt("ResCode") : -1) == 2) {
                        this.activity.firtCalltoGetState = false;
                        pinerror();
                    }
                }
            } catch (Exception unused) {
            }
        }
        ((ControlUnitActivity) this.activity).respResConn(jSONObject);
    }

    private void restConfig() {
        BaseActivity baseActivity = this.activity;
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        if (Constants.token == null || Constants.token == "") {
            this.token = Constants.FIX_TOKEN;
        } else {
            this.token = Functions.calcNewToken(savePreferences, this.res);
        }
        Response newHttpClient = WebClientDevWrapper.getNewHttpClient(this.url, this.payload, this.token, Constants.user, this.method, 20);
        this.response = newHttpClient;
        if (newHttpClient != null) {
            try {
                if (newHttpClient.getHttpResponceCode() == 200) {
                    JSONObject jSONObject = new JSONObject(this.response.getHttpResponcePayload());
                    int i = jSONObject.has("ResCode") ? jSONObject.getInt("ResCode") : -1;
                    if (i == 0) {
                        this.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Functions.makeNormalToast(ThreadWebService.this.activity, ThreadWebService.this.res.getString(R.string.cu_okRestoreCU));
                                ThreadWebService.this.activity.hideProgress();
                            }
                        });
                        return;
                    } else if (i == 2) {
                        this.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Functions.makeErrorToast(ThreadWebService.this.activity, ThreadWebService.this.res.getString(R.string.pin_error));
                                ThreadWebService.this.activity.hideProgress();
                            }
                        });
                        return;
                    } else if (i == 9) {
                        this.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Functions.makeErrorToast(ThreadWebService.this.activity, ThreadWebService.this.res.getString(R.string.cu_errorRestoreCU));
                                ThreadWebService.this.activity.hideProgress();
                            }
                        });
                        return;
                    }
                }
            } catch (Exception unused) {
            }
            this.activity.hideProgress();
            Functions.makeErrorToast(this.activity, this.res.getString(R.string.errorSendCMDToServer));
            return;
        }
        Functions.makeErrorToast(this.activity, this.res.getString(R.string.resCodeError));
    }

    private void delUser() {
        BaseActivity baseActivity = this.activity;
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        if (Constants.token == null || Constants.token == "") {
            this.token = Constants.FIX_TOKEN;
        } else {
            this.token = Functions.calcNewToken(savePreferences, this.res);
        }
        Response newHttpClient = WebClientDevWrapper.getNewHttpClient(this.url, this.payload, this.token, Constants.user, this.method, 20);
        this.response = newHttpClient;
        if (newHttpClient != null) {
            try {
                if (newHttpClient.getHttpResponceCode() == 200) {
                    JSONObject jSONObject = new JSONObject(this.response.getHttpResponcePayload());
                    if ((jSONObject.has("ResCode") ? jSONObject.getInt("ResCode") : -1) == 0) {
                        this.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Functions.makeNormalToast(ThreadWebService.this.activity, ThreadWebService.this.res.getString(R.string.cu_okDeleteAccount));
                                new SavePreferences(ThreadWebService.this.activity, ThreadWebService.this.activity.getString(R.string.PrefsName)).deletePref();
                                Intent intent = new Intent(ThreadWebService.this.activity, LoginActivity.class);
                                intent.addFlags(67108864);
                                ThreadWebService.this.activity.startActivity(intent);
                            }
                        });
                        return;
                    }
                }
            } catch (Exception unused) {
            }
        }
        this.activity.hideProgress();
        Functions.makeErrorToast(this.activity, this.res.getString(R.string.cu_errorDeleteAccount));
    }

    private void signUp() {
        Response newHttpClient = WebClientDevWrapper.getNewHttpClient(this.url, this.payload, Constants.FIX_TOKEN, (String) null, this.method, 0);
        this.response = newHttpClient;
        if (newHttpClient != null && newHttpClient.getHttpResponceCode() == 200) {
            try {
                JSONObject jSONObject = new JSONObject(this.response.getHttpResponcePayload());
                if ((jSONObject.has("ResCode") ? jSONObject.getInt("ResCode") : -1) == 0) {
                    this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Functions.makeNormalToast(ThreadWebService.this.activity, ThreadWebService.this.res.getString(R.string.sa_checkEmail));
                            ThreadWebService.this.activity.finish();
                        }
                    });
                } else {
                    this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Functions.makeErrorToast(ThreadWebService.this.activity, ThreadWebService.this.res.getString(R.string.resCodeError));
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void checkEmail() {
        Response newHttpClient = WebClientDevWrapper.getNewHttpClient(this.url, this.payload, Constants.FIX_TOKEN, (String) null, 0, 1);
        if (newHttpClient != null) {
            if (newHttpClient.getHttpResponceCode() == 200) {
                try {
                    JSONObject jSONObject = new JSONObject(newHttpClient.getHttpResponcePayload());
                    if ((jSONObject.has("ResCode") ? jSONObject.getInt("ResCode") : -1) == 9) {
                        BaseActivity baseActivity = this.activity;
                        if (baseActivity instanceof SignUpActivity) {
                            ((SignUpActivity) baseActivity).emailExits = true;
                        }
                        this.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                ((SignUpActivity) ThreadWebService.this.activity).showError();
                            }
                        });
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            BaseActivity baseActivity2 = this.activity;
            if (baseActivity2 instanceof SignUpActivity) {
                ((SignUpActivity) baseActivity2).emailExits = false;
            }
        }
    }

    private void updGDPR() {
        Response response2;
        try {
            response2 = WebClientDevWrapper.getNewHttpClient(this.url, this.payload, Constants.FIX_TOKEN, (String) null, this.method, 2);
        } catch (Exception unused) {
            response2 = null;
        }
        ((GDPRActivity) this.activity).ResSave(response2);
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void logIn() {
        /*
            r17 = this;
            r1 = r17
            java.lang.String r0 = "ListRecipe"
            java.lang.String r2 = "ListTimezone"
            java.lang.String r3 = "MarketingAccepted"
            java.lang.String r4 = "LastVrAndroidText"
            java.lang.String r5 = "LastVrAndroid"
            java.lang.String r6 = "LastFWVr"
            java.lang.String r7 = "ResetPin"
            java.lang.String r8 = "ResCode"
            java.lang.String r9 = "Ga5mM61KCm5Bk18lhD5J999jC2Mu0Vaf"
            r1.token = r9
            java.lang.String[] r9 = r1.params
            r10 = 0
            r10 = r9[r10]
            r11 = 1
            r9 = r9[r11]
            java.lang.String r11 = r1.url
            java.lang.String r12 = r1.payload
            int r15 = r1.method
            r16 = 2
            java.lang.String r13 = "Ga5mM61KCm5Bk18lhD5J999jC2Mu0Vaf"
            r14 = 0
            it.tecnosystemi.TS.Model.Response r11 = it.tecnosystemi.TS.Threads.WebClientDevWrapper.getNewHttpClient(r11, r12, r13, r14, r15, r16)
            if (r11 != 0) goto L_0x0033
            r17.logIn_error()
            return
        L_0x0033:
            int r12 = r11.getHttpResponceCode()     // Catch:{ Exception -> 0x017c }
            r13 = 200(0xc8, float:2.8E-43)
            if (r12 != r13) goto L_0x0186
            org.json.JSONObject r12 = new org.json.JSONObject     // Catch:{ Exception -> 0x017c }
            java.lang.String r11 = r11.getHttpResponcePayload()     // Catch:{ Exception -> 0x017c }
            r12.<init>(r11)     // Catch:{ Exception -> 0x017c }
            boolean r11 = r12.has(r8)     // Catch:{ Exception -> 0x017c }
            if (r11 == 0) goto L_0x004f
            int r8 = r12.getInt(r8)     // Catch:{ Exception -> 0x017c }
            goto L_0x0050
        L_0x004f:
            r8 = -1
        L_0x0050:
            r11 = 10
            if (r8 == r11) goto L_0x0171
            switch(r8) {
                case 0: goto L_0x00a8;
                case 1: goto L_0x009b;
                case 2: goto L_0x008e;
                case 3: goto L_0x0081;
                case 4: goto L_0x0074;
                case 5: goto L_0x0186;
                case 6: goto L_0x0068;
                case 7: goto L_0x005b;
                default: goto L_0x0057;
            }     // Catch:{ Exception -> 0x017c }
        L_0x0057:
            r17.logIn_error()     // Catch:{ Exception -> 0x017c }
            return
        L_0x005b:
            android.content.res.Resources r0 = r1.res     // Catch:{ Exception -> 0x017c }
            int r2 = it.tecnosystemi.TS.R.string.la_requestNewTemporaryPwd     // Catch:{ Exception -> 0x017c }
            java.lang.String r0 = r0.getString(r2)     // Catch:{ Exception -> 0x017c }
            r1.toastOnMainThread(r0)     // Catch:{ Exception -> 0x017c }
            goto L_0x0186
        L_0x0068:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r1.activity     // Catch:{ Exception -> 0x017c }
            it.tecnosystemi.TS.Threads.ThreadWebService$25 r2 = new it.tecnosystemi.TS.Threads.ThreadWebService$25     // Catch:{ Exception -> 0x017c }
            r2.<init>(r9, r10)     // Catch:{ Exception -> 0x017c }
            r0.runOnUiThread(r2)     // Catch:{ Exception -> 0x017c }
            goto L_0x0186
        L_0x0074:
            android.content.res.Resources r0 = r1.res     // Catch:{ Exception -> 0x017c }
            int r2 = it.tecnosystemi.TS.R.string.la_userNotFound     // Catch:{ Exception -> 0x017c }
            java.lang.String r0 = r0.getString(r2)     // Catch:{ Exception -> 0x017c }
            r1.toastOnMainThread(r0)     // Catch:{ Exception -> 0x017c }
            goto L_0x0186
        L_0x0081:
            android.content.res.Resources r0 = r1.res     // Catch:{ Exception -> 0x017c }
            int r2 = it.tecnosystemi.TS.R.string.la_userBlocked     // Catch:{ Exception -> 0x017c }
            java.lang.String r0 = r0.getString(r2)     // Catch:{ Exception -> 0x017c }
            r1.toastOnMainThread(r0)     // Catch:{ Exception -> 0x017c }
            goto L_0x0186
        L_0x008e:
            android.content.res.Resources r0 = r1.res     // Catch:{ Exception -> 0x017c }
            int r2 = it.tecnosystemi.TS.R.string.la_userToConfirm     // Catch:{ Exception -> 0x017c }
            java.lang.String r0 = r0.getString(r2)     // Catch:{ Exception -> 0x017c }
            r1.toastOnMainThread(r0)     // Catch:{ Exception -> 0x017c }
            goto L_0x0186
        L_0x009b:
            android.content.res.Resources r0 = r1.res     // Catch:{ Exception -> 0x017c }
            int r2 = it.tecnosystemi.TS.R.string.la_wrongUserOrPwd     // Catch:{ Exception -> 0x017c }
            java.lang.String r0 = r0.getString(r2)     // Catch:{ Exception -> 0x017c }
            r1.toastOnMainThread(r0)     // Catch:{ Exception -> 0x017c }
            goto L_0x0186
        L_0x00a8:
            r1.savepref(r12, r10)     // Catch:{ Exception -> 0x017c }
            it.tecnosystemi.TS.Utils.Constants.user = r10     // Catch:{ Exception -> 0x017c }
            boolean r8 = r12.has(r7)     // Catch:{ Exception -> 0x017c }
            if (r8 == 0) goto L_0x00b9
            java.lang.String r7 = r12.getString(r7)     // Catch:{ Exception -> 0x017c }
            it.tecnosystemi.TS.Utils.Constants.RESET_PIN = r7     // Catch:{ Exception -> 0x017c }
        L_0x00b9:
            boolean r7 = r12.has(r6)     // Catch:{ Exception -> 0x017c }
            if (r7 == 0) goto L_0x00c5
            java.lang.String r6 = r12.getString(r6)     // Catch:{ Exception -> 0x017c }
            it.tecnosystemi.TS.Utils.Constants.LastFWVr = r6     // Catch:{ Exception -> 0x017c }
        L_0x00c5:
            boolean r6 = r12.has(r5)     // Catch:{ Exception -> 0x017c }
            if (r6 == 0) goto L_0x00d1
            java.lang.String r5 = r12.getString(r5)     // Catch:{ Exception -> 0x017c }
            it.tecnosystemi.TS.Utils.Constants.LastAppVr = r5     // Catch:{ Exception -> 0x017c }
        L_0x00d1:
            boolean r5 = r12.has(r4)     // Catch:{ Exception -> 0x017c }
            if (r5 == 0) goto L_0x00dd
            java.lang.String r4 = r12.getString(r4)     // Catch:{ Exception -> 0x017c }
            it.tecnosystemi.TS.Utils.Constants.LastAppVrTxt = r4     // Catch:{ Exception -> 0x017c }
        L_0x00dd:
            boolean r4 = r12.has(r3)     // Catch:{ Exception -> 0x017c }
            if (r4 == 0) goto L_0x00e9
            boolean r3 = r12.getBoolean(r3)     // Catch:{ Exception -> 0x017c }
            it.tecnosystemi.TS.Activity.SignUpActivity.MARKETING = r3     // Catch:{ Exception -> 0x017c }
        L_0x00e9:
            boolean r3 = r12.has(r2)     // Catch:{ Exception -> 0x017c }
            if (r3 == 0) goto L_0x0109
            it.tecnosystemi.TS.Threads.ThreadWebService$21 r3 = new it.tecnosystemi.TS.Threads.ThreadWebService$21     // Catch:{ Exception -> 0x017c }
            r3.<init>()     // Catch:{ Exception -> 0x017c }
            java.lang.reflect.Type r3 = r3.getType()     // Catch:{ Exception -> 0x017c }
            com.google.gson.Gson r4 = new com.google.gson.Gson     // Catch:{ Exception -> 0x017c }
            r4.<init>()     // Catch:{ Exception -> 0x017c }
            java.lang.String r2 = r12.getString(r2)     // Catch:{ Exception -> 0x017c }
            java.lang.Object r2 = r4.fromJson((java.lang.String) r2, (java.lang.reflect.Type) r3)     // Catch:{ Exception -> 0x017c }
            java.util.List r2 = (java.util.List) r2     // Catch:{ Exception -> 0x017c }
            it.tecnosystemi.TS.Utils.Constants.TIMEZONES = r2     // Catch:{ Exception -> 0x017c }
        L_0x0109:
            boolean r2 = r12.has(r0)     // Catch:{ Exception -> 0x017c }
            java.lang.String r3 = "TAG"
            if (r2 == 0) goto L_0x0134
            it.tecnosystemi.TS.Threads.ThreadWebService$22 r2 = new it.tecnosystemi.TS.Threads.ThreadWebService$22     // Catch:{ Exception -> 0x017c }
            r2.<init>()     // Catch:{ Exception -> 0x017c }
            java.lang.reflect.Type r2 = r2.getType()     // Catch:{ Exception -> 0x017c }
            com.google.gson.Gson r4 = new com.google.gson.Gson     // Catch:{ Exception -> 0x017c }
            r4.<init>()     // Catch:{ Exception -> 0x017c }
            java.lang.String r0 = r12.getString(r0)     // Catch:{ Exception -> 0x017c }
            java.lang.Object r0 = r4.fromJson((java.lang.String) r0, (java.lang.reflect.Type) r2)     // Catch:{ Exception -> 0x017c }
            java.util.List r0 = (java.util.List) r0     // Catch:{ Exception -> 0x017c }
            it.tecnosystemi.TS.Utils.Constants.MODBUSRECEPIES = r0     // Catch:{ Exception -> 0x017c }
            java.util.List<it.tecnosystemi.TS.Model.ModBusRecipe> r0 = it.tecnosystemi.TS.Utils.Constants.MODBUSRECEPIES     // Catch:{ Exception -> 0x017c }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x017c }
            android.util.Log.d(r3, r0)     // Catch:{ Exception -> 0x017c }
        L_0x0134:
            it.tecnosystemi.TS.Threads.ThreadWebService$23 r0 = new it.tecnosystemi.TS.Threads.ThreadWebService$23     // Catch:{ Exception -> 0x017c }
            r0.<init>()     // Catch:{ Exception -> 0x017c }
            java.lang.reflect.Type r0 = r0.getType()     // Catch:{ Exception -> 0x017c }
            com.google.gson.Gson r2 = new com.google.gson.Gson     // Catch:{ Exception -> 0x017c }
            r2.<init>()     // Catch:{ Exception -> 0x017c }
            java.lang.String r4 = "ListPlants"
            java.lang.String r4 = r12.getString(r4)     // Catch:{ Exception -> 0x017c }
            java.lang.Object r0 = r2.fromJson((java.lang.String) r4, (java.lang.reflect.Type) r0)     // Catch:{ Exception -> 0x017c }
            java.util.List r0 = (java.util.List) r0     // Catch:{ Exception -> 0x017c }
            if (r0 != 0) goto L_0x0158
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ Exception -> 0x017c }
            r0.<init>()     // Catch:{ Exception -> 0x017c }
            it.tecnosystemi.TS.Utils.Constants.listaImpianti = r0     // Catch:{ Exception -> 0x017c }
            goto L_0x0166
        L_0x0158:
            it.tecnosystemi.TS.Activity.BaseActivity r2 = r1.activity     // Catch:{ Exception -> 0x015e }
            it.tecnosystemi.TS.Utils.Functions.SyncInpianti(r0, r2)     // Catch:{ Exception -> 0x015e }
            goto L_0x0166
        L_0x015e:
            r0 = move-exception
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x017c }
            android.util.Log.d(r3, r0)     // Catch:{ Exception -> 0x017c }
        L_0x0166:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r1.activity     // Catch:{ Exception -> 0x017c }
            it.tecnosystemi.TS.Threads.ThreadWebService$24 r2 = new it.tecnosystemi.TS.Threads.ThreadWebService$24     // Catch:{ Exception -> 0x017c }
            r2.<init>()     // Catch:{ Exception -> 0x017c }
            r0.runOnUiThread(r2)     // Catch:{ Exception -> 0x017c }
            goto L_0x0186
        L_0x0171:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r1.activity     // Catch:{ Exception -> 0x017c }
            it.tecnosystemi.TS.Threads.ThreadWebService$26 r2 = new it.tecnosystemi.TS.Threads.ThreadWebService$26     // Catch:{ Exception -> 0x017c }
            r2.<init>()     // Catch:{ Exception -> 0x017c }
            r0.runOnUiThread(r2)     // Catch:{ Exception -> 0x017c }
            goto L_0x0186
        L_0x017c:
            r0 = move-exception
            java.lang.String r2 = "LogIn"
            java.lang.String r0 = r0.toString()
            android.util.Log.d(r2, r0)
        L_0x0186:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r1.activity
            it.tecnosystemi.TS.Threads.ThreadWebService$27 r2 = new it.tecnosystemi.TS.Threads.ThreadWebService$27
            r2.<init>()
            r0.runOnUiThread(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Threads.ThreadWebService.logIn():void");
    }

    private void logIn_error() {
        this.activity.runOnUiThread(new Runnable() {
            public void run() {
                Functions.makeErrorToast(ThreadWebService.this.activity, ThreadWebService.this.res.getString(R.string.resCodeError));
            }
        });
    }

    private void savepref(JSONObject jSONObject, String str) throws JSONException {
        BaseActivity baseActivity = this.activity;
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        this.idUtente = jSONObject.getInt("ID");
        if (jSONObject.has("Token")) {
            Constants.token = jSONObject.getString("Token");
            savePreferences.save(Constants.PREF_TOKEN, this.token);
            savePreferences.save(Constants.PREF_TOKEN_DATE, String.valueOf(new Date(System.currentTimeMillis()).getTime()));
        }
        savePreferences.save(Constants.PREF_USER_ID, this.idUtente);
        String string = savePreferences.getString(Constants.PREF_LAST_USER_NAME);
        if (string == null || string.isEmpty() || !string.equals(str)) {
            savePreferences.initialize();
            savePreferences.save(Constants.PREF_LAST_USER_NAME, str);
        }
    }

    private void recPwd() {
        int i;
        String[] strArr = this.params;
        String str = strArr[0];
        String str2 = strArr[1];
        this.response = WebClientDevWrapper.getNewHttpClient(this.url, this.payload, Constants.FIX_TOKEN, (String) null, this.method, 3);
        try {
            JSONObject jSONObject = new JSONObject(this.response.getHttpResponcePayload());
            if (jSONObject.has("ResCode")) {
                i = jSONObject.getInt("ResCode");
            } else {
                i = -1;
            }
            switch (i) {
                case 0:
                    toastOnMainThread(this.res.getString(R.string.sa_checkEmail));
                    return;
                case 1:
                    toastOnMainThread(this.res.getString(R.string.la_wrongUserOrPwd));
                    return;
                case 2:
                    toastOnMainThread(this.res.getString(R.string.la_userToConfirm));
                    return;
                case 3:
                    toastOnMainThread(this.res.getString(R.string.la_userBlocked));
                    return;
                case 4:
                    toastOnMainThread(this.res.getString(R.string.la_userNotFound));
                    return;
                case 5:
                    return;
                case 6:
                    toastOnMainThread(this.res.getString(R.string.la_temporaryPwdSent));
                    return;
                case 7:
                    toastOnMainThread(this.res.getString(R.string.la_requestNewTemporaryPwd));
                    return;
                default:
                    logIn_error();
                    return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void connUsrUC() {
        BaseActivity baseActivity = this.activity;
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        if (Constants.token == null || Constants.token == "") {
            this.token = Constants.FIX_TOKEN;
        } else {
            this.token = Functions.calcNewToken(savePreferences, this.res);
        }
        Response newHttpClient = WebClientDevWrapper.getNewHttpClient(this.url, this.payload, this.token, Constants.user, this.method, 8);
        this.response = newHttpClient;
        if (newHttpClient == null) {
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    Functions.makeErrorToast(ThreadWebService.this.activity, ThreadWebService.this.activity.getResources().getString(R.string.resCodeError));
                }
            });
        } else if (newHttpClient.getHttpResponceCode() == 200) {
            try {
                JSONObject jSONObject = new JSONObject(this.response.getHttpResponcePayload());
                if ((jSONObject.has("ResCode") ? jSONObject.getInt("ResCode") : -1) != 0) {
                    this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Functions.makeErrorToast(ThreadWebService.this.activity, ThreadWebService.this.res.getString(R.string.au_errorAddUser));
                        }
                    });
                } else {
                    this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            ((SetNameAndPinActivity) ThreadWebService.this.activity).savsercerUc();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONObject jSONObject2 = new JSONObject(this.response.getHttpResponcePayload());
                if (this.response.getHttpResponceCode() == 400) {
                    if (jSONObject2.has("ResCode")) {
                        jSONObject2.getInt("ResCode");
                    }
                    this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Functions.makeErrorToast(ThreadWebService.this.activity, ThreadWebService.this.res.getString(R.string.au_errorAddUser));
                        }
                    });
                    return;
                }
                this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Functions.makeErrorToast(ThreadWebService.this.activity, ThreadWebService.this.activity.getResources().getString(R.string.resCodeError));
                    }
                });
            } catch (Exception unused) {
            }
        }
    }

    private void deleteUsrfromUC() {
        String[] strArr = this.params;
        final String str = strArr != null ? strArr[0] : null;
        BaseActivity baseActivity = this.activity;
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        if (Constants.token == null || Constants.token == "") {
            this.token = Constants.FIX_TOKEN;
        } else {
            this.token = Functions.calcNewToken(savePreferences, this.res);
        }
        Response newHttpClient = WebClientDevWrapper.getNewHttpClient(this.url, this.payload, this.token, Constants.user, this.method, 10);
        this.response = newHttpClient;
        if (newHttpClient == null) {
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    Functions.makeErrorToast(ThreadWebService.this.activity, ThreadWebService.this.activity.getResources().getString(R.string.resCodeError));
                }
            });
        } else if (newHttpClient.getHttpResponceCode() == 200) {
            try {
                JSONObject jSONObject = new JSONObject(this.response.getHttpResponcePayload());
                if ((jSONObject.has("ResCode") ? jSONObject.getInt("ResCode") : -1) != 0) {
                    this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            if (ThreadWebService.this.url.contains(ThreadWebService.this.res.getString(R.string.uri_DeletePlant))) {
                                Functions.makeErrorToast(ThreadWebService.this.activity, ThreadWebService.this.res.getString(R.string.resDeletePlantError));
                            } else {
                                Functions.makeErrorToast(ThreadWebService.this.activity, ThreadWebService.this.res.getString(R.string.cu_errorDeleteUser));
                            }
                        }
                    });
                } else {
                    this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            if (ThreadWebService.this.activity instanceof PicoActivity) {
                                ThreadWebService.this.activity.finish();
                            } else if (ThreadWebService.this.activity instanceof TSHomeActivity) {
                                ((TSHomeActivity) ThreadWebService.this.activity).loadHome();
                            } else if (!(ThreadWebService.this.activity instanceof TSDeviceListActivity)) {
                                ControlUnit.deleteCufromPref(str, ThreadWebService.this.activity);
                                if (ThreadWebService.this.activity instanceof ControlUnitActivity) {
                                    ThreadWebService.this.activity.finish();
                                } else {
                                    ((HomeActivity) ThreadWebService.this.activity).loadHome();
                                }
                            } else if (ThreadWebService.this.type == 25) {
                                ThreadWebService.this.activity.finish();
                            } else {
                                ((TSDeviceListActivity) ThreadWebService.this.activity).loadHome();
                            }
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONObject jSONObject2 = new JSONObject(this.response.getHttpResponcePayload());
                if (this.response.getHttpResponceCode() == 400) {
                    if (jSONObject2.has("ResCode")) {
                        jSONObject2.getInt("ResCode");
                    }
                    this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            if (ThreadWebService.this.url.contains(ThreadWebService.this.res.getString(R.string.uri_DeletePlant))) {
                                Functions.makeErrorToast(ThreadWebService.this.activity, ThreadWebService.this.res.getString(R.string.resDeletePlantError));
                            } else {
                                Functions.makeErrorToast(ThreadWebService.this.activity, ThreadWebService.this.res.getString(R.string.cu_errorDeleteUser));
                            }
                        }
                    });
                    return;
                }
                this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Functions.makeErrorToast(ThreadWebService.this.activity, ThreadWebService.this.activity.getResources().getString(R.string.resCodeError));
                    }
                });
            } catch (Exception unused) {
            }
        }
    }

    private void getHome() {
        BaseActivity baseActivity = this.activity;
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        if (Constants.token == null || Constants.token == "") {
            this.token = Constants.FIX_TOKEN;
        } else {
            this.token = Functions.calcNewToken(savePreferences, this.res);
        }
        Response newHttpClient = WebClientDevWrapper.getNewHttpClient(this.url, this.payload, this.token, Constants.user, this.method, 9);
        this.response = newHttpClient;
        if (newHttpClient == null) {
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    ((HomeActivity) ThreadWebService.this.activity).getHomeFromServer("[]");
                }
            });
        } else if (newHttpClient.getHttpResponceCode() == 200) {
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    ((HomeActivity) ThreadWebService.this.activity).getHomeFromServer(ThreadWebService.this.response.getHttpResponcePayload());
                }
            });
        } else {
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    ((HomeActivity) ThreadWebService.this.activity).getHomeFromServer("[]");
                }
            });
        }
    }

    private void getCuState() {
        int i;
        BaseActivity baseActivity = this.activity;
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        if (Constants.token == null || Constants.token == "") {
            this.token = Constants.FIX_TOKEN;
        } else {
            this.token = Functions.calcNewToken(savePreferences, this.res);
        }
        Response newHttpClient = WebClientDevWrapper.getNewHttpClient(this.url, this.payload, this.token, Constants.user, this.method, 11);
        this.response = newHttpClient;
        JSONObject jSONObject = null;
        if (newHttpClient == null) {
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                }
            });
        } else if (newHttpClient.getHttpResponceCode() == 200) {
            try {
                JSONObject jSONObject2 = new JSONObject(this.response.getHttpResponcePayload());
                if (!jSONObject2.has("ResCode") || (i = jSONObject2.getInt("ResCode")) == 0) {
                    jSONObject = jSONObject2;
                } else if (i == 2 && this.type == 12) {
                    this.activity.firtCalltoGetState = false;
                    pinerror();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONObject jSONObject3 = new JSONObject(this.response.getHttpResponcePayload());
                if (this.response.getHttpResponceCode() == 400) {
                    if ((jSONObject3.has("ResCode") ? jSONObject3.getInt("ResCode") : -1) == 2 && this.type == 12) {
                        this.activity.firtCalltoGetState = false;
                        pinerror();
                    }
                }
            } catch (Exception unused) {
            }
        }
        BaseActivity baseActivity2 = this.activity;
        if (baseActivity2 instanceof ControlUnitActivity) {
            ((ControlUnitActivity) baseActivity2).getCuState(jSONObject, false);
        } else if (baseActivity2 instanceof ZoneActivity) {
            ((ZoneActivity) baseActivity2).getCuState(jSONObject);
        }
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(6:33|34|35|(1:37)|38|39) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:38:0x00e6 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updState() {
        /*
            r7 = this;
            it.tecnosystemi.TS.Utils.SavePreferences r0 = new it.tecnosystemi.TS.Utils.SavePreferences
            it.tecnosystemi.TS.Activity.BaseActivity r1 = r7.activity
            int r2 = it.tecnosystemi.TS.R.string.PrefsName
            java.lang.String r2 = r1.getString(r2)
            r0.<init>(r1, r2)
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.token
            if (r1 == 0) goto L_0x0020
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.token
            java.lang.String r2 = ""
            if (r1 == r2) goto L_0x0020
            android.content.res.Resources r1 = r7.res
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Functions.calcNewToken(r0, r1)
            r7.token = r0
            goto L_0x0024
        L_0x0020:
            java.lang.String r0 = "Ga5mM61KCm5Bk18lhD5J999jC2Mu0Vaf"
            r7.token = r0
        L_0x0024:
            java.lang.String r1 = r7.url
            java.lang.String r2 = r7.payload
            java.lang.String r3 = r7.token
            java.lang.String r4 = it.tecnosystemi.TS.Utils.Constants.user
            int r5 = r7.method
            r6 = 13
            it.tecnosystemi.TS.Model.Response r0 = it.tecnosystemi.TS.Threads.WebClientDevWrapper.getNewHttpClient(r1, r2, r3, r4, r5, r6)
            r7.response = r0
            r1 = 0
            if (r0 != 0) goto L_0x0053
            android.content.res.Resources r0 = r7.res
            int r2 = it.tecnosystemi.TS.R.string.resCodeError
            java.lang.String r0 = r0.getString(r2)
            r7.showErr(r0)
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity
            it.tecnosystemi.TS.Threads.ThreadWebService$43 r2 = new it.tecnosystemi.TS.Threads.ThreadWebService$43
            r2.<init>()
            r0.runOnUiThread(r2)
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity
            r0.sendingstate = r1
            return
        L_0x0053:
            int r0 = r0.getHttpResponceCode()
            r2 = 200(0xc8, float:2.8E-43)
            r3 = 2
            java.lang.String r4 = "ResCode"
            if (r0 != r2) goto L_0x0124
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0106 }
            it.tecnosystemi.TS.Model.Response r2 = r7.response     // Catch:{ JSONException -> 0x0106 }
            java.lang.String r2 = r2.getHttpResponcePayload()     // Catch:{ JSONException -> 0x0106 }
            r0.<init>(r2)     // Catch:{ JSONException -> 0x0106 }
            boolean r2 = r0.has(r4)     // Catch:{ JSONException -> 0x0106 }
            if (r2 == 0) goto L_0x00eb
            int r0 = r0.getInt(r4)     // Catch:{ JSONException -> 0x0106 }
            if (r0 == 0) goto L_0x0096
            if (r0 != r3) goto L_0x007b
            r7.pinerror()     // Catch:{ JSONException -> 0x0106 }
            goto L_0x0086
        L_0x007b:
            android.content.res.Resources r0 = r7.res     // Catch:{ JSONException -> 0x0106 }
            int r2 = it.tecnosystemi.TS.R.string.msg_commandKo     // Catch:{ JSONException -> 0x0106 }
            java.lang.String r0 = r0.getString(r2)     // Catch:{ JSONException -> 0x0106 }
            r7.showErr(r0)     // Catch:{ JSONException -> 0x0106 }
        L_0x0086:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity     // Catch:{ JSONException -> 0x0106 }
            boolean r2 = r0 instanceof it.tecnosystemi.TS.Activity.ZoneActivity     // Catch:{ JSONException -> 0x0106 }
            if (r2 == 0) goto L_0x0191
            it.tecnosystemi.TS.Threads.ThreadWebService$44 r2 = new it.tecnosystemi.TS.Threads.ThreadWebService$44     // Catch:{ JSONException -> 0x0106 }
            r2.<init>()     // Catch:{ JSONException -> 0x0106 }
            r0.runOnUiThread(r2)     // Catch:{ JSONException -> 0x0106 }
            goto L_0x0191
        L_0x0096:
            java.lang.String[] r0 = r7.params     // Catch:{ JSONException -> 0x0106 }
            if (r0 == 0) goto L_0x00c3
            int r2 = r0.length     // Catch:{ JSONException -> 0x0106 }
            if (r2 <= 0) goto L_0x00c3
            r0 = r0[r1]     // Catch:{ JSONException -> 0x0106 }
            java.lang.String r2 = "0"
            int r0 = r0.compareTo(r2)     // Catch:{ JSONException -> 0x0106 }
            if (r0 == 0) goto L_0x00b3
            java.lang.String[] r0 = r7.params     // Catch:{ JSONException -> 0x0106 }
            r0 = r0[r1]     // Catch:{ JSONException -> 0x0106 }
            java.lang.String r2 = "2"
            int r0 = r0.compareTo(r2)     // Catch:{ JSONException -> 0x0106 }
            if (r0 != 0) goto L_0x00c3
        L_0x00b3:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity     // Catch:{ JSONException -> 0x0106 }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ JSONException -> 0x0106 }
            int r2 = it.tecnosystemi.TS.R.string.msg_commandOk     // Catch:{ JSONException -> 0x0106 }
            java.lang.String r0 = r0.getString(r2)     // Catch:{ JSONException -> 0x0106 }
            r7.toastOnMainThread(r0)     // Catch:{ JSONException -> 0x0106 }
            return
        L_0x00c3:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity     // Catch:{ JSONException -> 0x0106 }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ JSONException -> 0x0106 }
            int r2 = it.tecnosystemi.TS.R.string.okSendCMDToServer     // Catch:{ JSONException -> 0x0106 }
            java.lang.String r0 = r0.getString(r2)     // Catch:{ JSONException -> 0x0106 }
            java.lang.String[] r2 = r7.params     // Catch:{ Exception -> 0x00e6 }
            r3 = 1
            r2 = r2[r3]     // Catch:{ Exception -> 0x00e6 }
            boolean r2 = it.tecnosystemi.TS.Utils.Functions.IS4x(r2)     // Catch:{ Exception -> 0x00e6 }
            if (r2 == 0) goto L_0x00e6
            it.tecnosystemi.TS.Activity.BaseActivity r2 = r7.activity     // Catch:{ Exception -> 0x00e6 }
            android.content.res.Resources r2 = r2.getResources()     // Catch:{ Exception -> 0x00e6 }
            int r3 = it.tecnosystemi.TS.R.string.msg_commandOk     // Catch:{ Exception -> 0x00e6 }
            java.lang.String r0 = r2.getString(r3)     // Catch:{ Exception -> 0x00e6 }
        L_0x00e6:
            r7.toastOnMainThread(r0)     // Catch:{ JSONException -> 0x0106 }
            goto L_0x0191
        L_0x00eb:
            android.content.res.Resources r0 = r7.res     // Catch:{ JSONException -> 0x0106 }
            int r2 = it.tecnosystemi.TS.R.string.msg_commandKo     // Catch:{ JSONException -> 0x0106 }
            java.lang.String r0 = r0.getString(r2)     // Catch:{ JSONException -> 0x0106 }
            r7.showErr(r0)     // Catch:{ JSONException -> 0x0106 }
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity     // Catch:{ JSONException -> 0x0106 }
            boolean r2 = r0 instanceof it.tecnosystemi.TS.Activity.ZoneActivity     // Catch:{ JSONException -> 0x0106 }
            if (r2 == 0) goto L_0x0191
            it.tecnosystemi.TS.Threads.ThreadWebService$45 r2 = new it.tecnosystemi.TS.Threads.ThreadWebService$45     // Catch:{ JSONException -> 0x0106 }
            r2.<init>()     // Catch:{ JSONException -> 0x0106 }
            r0.runOnUiThread(r2)     // Catch:{ JSONException -> 0x0106 }
            goto L_0x0191
        L_0x0106:
            r0 = move-exception
            r0.printStackTrace()
            android.content.res.Resources r0 = r7.res
            int r2 = it.tecnosystemi.TS.R.string.msg_commandKo
            java.lang.String r0 = r0.getString(r2)
            r7.showErr(r0)
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity
            boolean r2 = r0 instanceof it.tecnosystemi.TS.Activity.ZoneActivity
            if (r2 == 0) goto L_0x0191
            it.tecnosystemi.TS.Threads.ThreadWebService$46 r2 = new it.tecnosystemi.TS.Threads.ThreadWebService$46
            r2.<init>()
            r0.runOnUiThread(r2)
            goto L_0x0191
        L_0x0124:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x0175 }
            it.tecnosystemi.TS.Model.Response r2 = r7.response     // Catch:{ Exception -> 0x0175 }
            java.lang.String r2 = r2.getHttpResponcePayload()     // Catch:{ Exception -> 0x0175 }
            r0.<init>(r2)     // Catch:{ Exception -> 0x0175 }
            it.tecnosystemi.TS.Model.Response r2 = r7.response     // Catch:{ Exception -> 0x0175 }
            int r2 = r2.getHttpResponceCode()     // Catch:{ Exception -> 0x0175 }
            r5 = 400(0x190, float:5.6E-43)
            if (r2 != r5) goto L_0x0157
            boolean r2 = r0.has(r4)     // Catch:{ Exception -> 0x0175 }
            if (r2 == 0) goto L_0x0144
            int r0 = r0.getInt(r4)     // Catch:{ Exception -> 0x0175 }
            goto L_0x0145
        L_0x0144:
            r0 = -1
        L_0x0145:
            if (r0 != r3) goto L_0x014b
            r7.pinerror()     // Catch:{ Exception -> 0x0175 }
            goto L_0x0166
        L_0x014b:
            android.content.res.Resources r0 = r7.res     // Catch:{ Exception -> 0x0175 }
            int r2 = it.tecnosystemi.TS.R.string.msg_commandKo     // Catch:{ Exception -> 0x0175 }
            java.lang.String r0 = r0.getString(r2)     // Catch:{ Exception -> 0x0175 }
            r7.showErr(r0)     // Catch:{ Exception -> 0x0175 }
            goto L_0x0166
        L_0x0157:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity     // Catch:{ Exception -> 0x0175 }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ Exception -> 0x0175 }
            int r2 = it.tecnosystemi.TS.R.string.resCodeError     // Catch:{ Exception -> 0x0175 }
            java.lang.String r0 = r0.getString(r2)     // Catch:{ Exception -> 0x0175 }
            r7.showErr(r0)     // Catch:{ Exception -> 0x0175 }
        L_0x0166:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity     // Catch:{ Exception -> 0x0175 }
            boolean r2 = r0 instanceof it.tecnosystemi.TS.Activity.ZoneActivity     // Catch:{ Exception -> 0x0175 }
            if (r2 == 0) goto L_0x0191
            it.tecnosystemi.TS.Threads.ThreadWebService$47 r2 = new it.tecnosystemi.TS.Threads.ThreadWebService$47     // Catch:{ Exception -> 0x0175 }
            r2.<init>()     // Catch:{ Exception -> 0x0175 }
            r0.runOnUiThread(r2)     // Catch:{ Exception -> 0x0175 }
            goto L_0x0191
        L_0x0175:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity
            boolean r0 = r0 instanceof it.tecnosystemi.TS.Activity.ZoneActivity
            if (r0 == 0) goto L_0x0191
            android.content.res.Resources r0 = r7.res
            int r2 = it.tecnosystemi.TS.R.string.msg_commandKo
            java.lang.String r0 = r0.getString(r2)
            r7.showErr(r0)
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity
            it.tecnosystemi.TS.Threads.ThreadWebService$48 r2 = new it.tecnosystemi.TS.Threads.ThreadWebService$48
            r2.<init>()
            r0.runOnUiThread(r2)
        L_0x0191:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity
            r0.sendingstate = r1
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Threads.ThreadWebService.updState():void");
    }

    private void showErr(final String str) {
        String[] strArr = this.params;
        if (strArr == null || strArr.length <= 0 || strArr[0].compareTo(Constants.COMMANDSENT) != 0) {
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    Functions.makeErrorToast(ThreadWebService.this.activity, str);
                }
            });
        } else {
            toastOnMainThread(this.activity.getResources().getString(R.string.msg_commandOk));
        }
    }

    /* JADX WARNING: type inference failed for: r1v3, types: [java.lang.Throwable, org.json.JSONObject] */
    /* JADX WARNING: type inference failed for: r1v4, types: [org.json.JSONObject] */
    /* JADX WARNING: type inference failed for: r1v5 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void getTW() {
        /*
            r7 = this;
            it.tecnosystemi.TS.Utils.SavePreferences r0 = new it.tecnosystemi.TS.Utils.SavePreferences
            it.tecnosystemi.TS.Activity.BaseActivity r1 = r7.activity
            int r2 = it.tecnosystemi.TS.R.string.PrefsName
            java.lang.String r2 = r1.getString(r2)
            r0.<init>(r1, r2)
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.token
            if (r1 == 0) goto L_0x0020
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.token
            java.lang.String r2 = ""
            if (r1 == r2) goto L_0x0020
            android.content.res.Resources r1 = r7.res
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Functions.calcNewToken(r0, r1)
            r7.token = r0
            goto L_0x0024
        L_0x0020:
            java.lang.String r0 = "Ga5mM61KCm5Bk18lhD5J999jC2Mu0Vaf"
            r7.token = r0
        L_0x0024:
            java.lang.String r1 = r7.url
            java.lang.String r2 = r7.payload
            java.lang.String r3 = r7.token
            java.lang.String r4 = it.tecnosystemi.TS.Utils.Constants.user
            int r5 = r7.method
            r6 = 11
            it.tecnosystemi.TS.Model.Response r0 = it.tecnosystemi.TS.Threads.WebClientDevWrapper.getNewHttpClient(r1, r2, r3, r4, r5, r6)
            r7.response = r0
            r1 = 0
            if (r0 != 0) goto L_0x0044
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity
            it.tecnosystemi.TS.Threads.ThreadWebService$50 r2 = new it.tecnosystemi.TS.Threads.ThreadWebService$50
            r2.<init>()
            r0.runOnUiThread(r2)
            goto L_0x00a1
        L_0x0044:
            int r0 = r0.getHttpResponceCode()
            r2 = 200(0xc8, float:2.8E-43)
            java.lang.String r3 = "ResCode"
            if (r0 != r2) goto L_0x0088
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x0079 }
            it.tecnosystemi.TS.Model.Response r2 = r7.response     // Catch:{ JSONException -> 0x0079 }
            java.lang.String r2 = r2.getHttpResponcePayload()     // Catch:{ JSONException -> 0x0079 }
            r0.<init>(r2)     // Catch:{ JSONException -> 0x0079 }
            boolean r2 = r0.has(r3)     // Catch:{ JSONException -> 0x0079 }
            if (r2 == 0) goto L_0x0077
            int r2 = r0.getInt(r3)     // Catch:{ JSONException -> 0x0079 }
            if (r2 == 0) goto L_0x0077
            r0 = 2
            if (r2 != r0) goto L_0x006c
            r7.pinerror()     // Catch:{ JSONException -> 0x0079 }
            goto L_0x00a1
        L_0x006c:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity     // Catch:{ JSONException -> 0x0079 }
            it.tecnosystemi.TS.Threads.ThreadWebService$51 r2 = new it.tecnosystemi.TS.Threads.ThreadWebService$51     // Catch:{ JSONException -> 0x0079 }
            r2.<init>()     // Catch:{ JSONException -> 0x0079 }
            r0.runOnUiThread(r2)     // Catch:{ JSONException -> 0x0079 }
            goto L_0x00a1
        L_0x0077:
            r1 = r0
            goto L_0x00a1
        L_0x0079:
            r0 = move-exception
            r0.printStackTrace()
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity
            it.tecnosystemi.TS.Threads.ThreadWebService$52 r2 = new it.tecnosystemi.TS.Threads.ThreadWebService$52
            r2.<init>()
            r0.runOnUiThread(r2)
            goto L_0x00a1
        L_0x0088:
            it.tecnosystemi.TS.Model.Response r0 = r7.response     // Catch:{ Exception -> 0x00a1 }
            int r0 = r0.getHttpResponceCode()     // Catch:{ Exception -> 0x00a1 }
            r2 = 400(0x190, float:5.6E-43)
            if (r0 == r2) goto L_0x009d
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity     // Catch:{ Exception -> 0x00a1 }
            it.tecnosystemi.TS.Threads.ThreadWebService$54 r2 = new it.tecnosystemi.TS.Threads.ThreadWebService$54     // Catch:{ Exception -> 0x00a1 }
            r2.<init>()     // Catch:{ Exception -> 0x00a1 }
            r0.runOnUiThread(r2)     // Catch:{ Exception -> 0x00a1 }
            goto L_0x00a1
        L_0x009d:
            r1.has(r3)     // Catch:{ Exception -> 0x00a1 }
            throw r1     // Catch:{ Exception -> 0x00a1 }
        L_0x00a1:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity
            it.tecnosystemi.TS.Activity.CronoSummaryActivity r0 = (it.tecnosystemi.TS.Activity.CronoSummaryActivity) r0
            r0.getTW(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Threads.ThreadWebService.getTW():void");
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(7:28|29|30|(1:32)|33|34|54) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:33:0x00bf */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updTW() {
        /*
            r7 = this;
            it.tecnosystemi.TS.Utils.SavePreferences r0 = new it.tecnosystemi.TS.Utils.SavePreferences
            it.tecnosystemi.TS.Activity.BaseActivity r1 = r7.activity
            int r2 = it.tecnosystemi.TS.R.string.PrefsName
            java.lang.String r2 = r1.getString(r2)
            r0.<init>(r1, r2)
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.token
            if (r1 == 0) goto L_0x0020
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.token
            java.lang.String r2 = ""
            if (r1 == r2) goto L_0x0020
            android.content.res.Resources r1 = r7.res
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Functions.calcNewToken(r0, r1)
            r7.token = r0
            goto L_0x0024
        L_0x0020:
            java.lang.String r0 = "Ga5mM61KCm5Bk18lhD5J999jC2Mu0Vaf"
            r7.token = r0
        L_0x0024:
            java.lang.String r1 = r7.url
            java.lang.String r2 = r7.payload
            java.lang.String r3 = r7.token
            java.lang.String r4 = it.tecnosystemi.TS.Utils.Constants.user
            int r5 = r7.method
            r6 = 15
            it.tecnosystemi.TS.Model.Response r0 = it.tecnosystemi.TS.Threads.WebClientDevWrapper.getNewHttpClient(r1, r2, r3, r4, r5, r6)
            r7.response = r0
            if (r0 != 0) goto L_0x0044
            android.content.res.Resources r0 = r7.res
            int r1 = it.tecnosystemi.TS.R.string.resCodeError
            java.lang.String r0 = r0.getString(r1)
            r7.showErr(r0)
            return
        L_0x0044:
            int r0 = r0.getHttpResponceCode()
            r1 = 200(0xc8, float:2.8E-43)
            r2 = 2
            java.lang.String r3 = "ResCode"
            if (r0 != r1) goto L_0x00df
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x00cf }
            it.tecnosystemi.TS.Model.Response r1 = r7.response     // Catch:{ JSONException -> 0x00cf }
            java.lang.String r1 = r1.getHttpResponcePayload()     // Catch:{ JSONException -> 0x00cf }
            r0.<init>(r1)     // Catch:{ JSONException -> 0x00cf }
            boolean r1 = r0.has(r3)     // Catch:{ JSONException -> 0x00cf }
            if (r1 == 0) goto L_0x00c3
            int r0 = r0.getInt(r3)     // Catch:{ JSONException -> 0x00cf }
            if (r0 == 0) goto L_0x007a
            if (r0 != r2) goto L_0x006d
            r7.pinerror()     // Catch:{ JSONException -> 0x00cf }
            goto L_0x011d
        L_0x006d:
            android.content.res.Resources r0 = r7.res     // Catch:{ JSONException -> 0x00cf }
            int r1 = it.tecnosystemi.TS.R.string.msg_commandKo     // Catch:{ JSONException -> 0x00cf }
            java.lang.String r0 = r0.getString(r1)     // Catch:{ JSONException -> 0x00cf }
            r7.showErr(r0)     // Catch:{ JSONException -> 0x00cf }
            goto L_0x011d
        L_0x007a:
            java.lang.String[] r0 = r7.params     // Catch:{ JSONException -> 0x00cf }
            if (r0 == 0) goto L_0x009c
            int r1 = r0.length     // Catch:{ JSONException -> 0x00cf }
            if (r1 <= 0) goto L_0x009c
            r1 = 0
            r0 = r0[r1]     // Catch:{ JSONException -> 0x00cf }
            java.lang.String r1 = "0"
            int r0 = r0.compareTo(r1)     // Catch:{ JSONException -> 0x00cf }
            if (r0 != 0) goto L_0x009c
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity     // Catch:{ JSONException -> 0x00cf }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ JSONException -> 0x00cf }
            int r1 = it.tecnosystemi.TS.R.string.msg_commandOk     // Catch:{ JSONException -> 0x00cf }
            java.lang.String r0 = r0.getString(r1)     // Catch:{ JSONException -> 0x00cf }
            r7.toastOnMainThread(r0)     // Catch:{ JSONException -> 0x00cf }
            return
        L_0x009c:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity     // Catch:{ JSONException -> 0x00cf }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ JSONException -> 0x00cf }
            int r1 = it.tecnosystemi.TS.R.string.okSendCMDToServer     // Catch:{ JSONException -> 0x00cf }
            java.lang.String r0 = r0.getString(r1)     // Catch:{ JSONException -> 0x00cf }
            java.lang.String[] r1 = r7.params     // Catch:{ Exception -> 0x00bf }
            r2 = 1
            r1 = r1[r2]     // Catch:{ Exception -> 0x00bf }
            boolean r1 = it.tecnosystemi.TS.Utils.Functions.IS4x(r1)     // Catch:{ Exception -> 0x00bf }
            if (r1 == 0) goto L_0x00bf
            it.tecnosystemi.TS.Activity.BaseActivity r1 = r7.activity     // Catch:{ Exception -> 0x00bf }
            android.content.res.Resources r1 = r1.getResources()     // Catch:{ Exception -> 0x00bf }
            int r2 = it.tecnosystemi.TS.R.string.msg_commandOk     // Catch:{ Exception -> 0x00bf }
            java.lang.String r0 = r1.getString(r2)     // Catch:{ Exception -> 0x00bf }
        L_0x00bf:
            r7.toastOnMainThread(r0)     // Catch:{ JSONException -> 0x00cf }
            goto L_0x011d
        L_0x00c3:
            android.content.res.Resources r0 = r7.res     // Catch:{ JSONException -> 0x00cf }
            int r1 = it.tecnosystemi.TS.R.string.msg_commandKo     // Catch:{ JSONException -> 0x00cf }
            java.lang.String r0 = r0.getString(r1)     // Catch:{ JSONException -> 0x00cf }
            r7.showErr(r0)     // Catch:{ JSONException -> 0x00cf }
            goto L_0x011d
        L_0x00cf:
            r0 = move-exception
            r0.printStackTrace()
            android.content.res.Resources r0 = r7.res
            int r1 = it.tecnosystemi.TS.R.string.msg_commandKo
            java.lang.String r0 = r0.getString(r1)
            r7.showErr(r0)
            goto L_0x011d
        L_0x00df:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x011d }
            it.tecnosystemi.TS.Model.Response r1 = r7.response     // Catch:{ Exception -> 0x011d }
            java.lang.String r1 = r1.getHttpResponcePayload()     // Catch:{ Exception -> 0x011d }
            r0.<init>(r1)     // Catch:{ Exception -> 0x011d }
            it.tecnosystemi.TS.Model.Response r1 = r7.response     // Catch:{ Exception -> 0x011d }
            int r1 = r1.getHttpResponceCode()     // Catch:{ Exception -> 0x011d }
            r4 = 400(0x190, float:5.6E-43)
            if (r1 != r4) goto L_0x0112
            boolean r1 = r0.has(r3)     // Catch:{ Exception -> 0x011d }
            if (r1 == 0) goto L_0x00ff
            int r0 = r0.getInt(r3)     // Catch:{ Exception -> 0x011d }
            goto L_0x0100
        L_0x00ff:
            r0 = -1
        L_0x0100:
            if (r0 != r2) goto L_0x0106
            r7.pinerror()     // Catch:{ Exception -> 0x011d }
            goto L_0x011d
        L_0x0106:
            android.content.res.Resources r0 = r7.res     // Catch:{ Exception -> 0x011d }
            int r1 = it.tecnosystemi.TS.R.string.msg_commandKo     // Catch:{ Exception -> 0x011d }
            java.lang.String r0 = r0.getString(r1)     // Catch:{ Exception -> 0x011d }
            r7.showErr(r0)     // Catch:{ Exception -> 0x011d }
            goto L_0x011d
        L_0x0112:
            android.content.res.Resources r0 = r7.res     // Catch:{ Exception -> 0x011d }
            int r1 = it.tecnosystemi.TS.R.string.resCodeError     // Catch:{ Exception -> 0x011d }
            java.lang.String r0 = r0.getString(r1)     // Catch:{ Exception -> 0x011d }
            r7.showErr(r0)     // Catch:{ Exception -> 0x011d }
        L_0x011d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Threads.ThreadWebService.updTW():void");
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(7:20|21|22|(1:24)|25|26|46) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:25:0x009b */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updCUDatetime() {
        /*
            r7 = this;
            it.tecnosystemi.TS.Utils.SavePreferences r0 = new it.tecnosystemi.TS.Utils.SavePreferences
            it.tecnosystemi.TS.Activity.BaseActivity r1 = r7.activity
            int r2 = it.tecnosystemi.TS.R.string.PrefsName
            java.lang.String r2 = r1.getString(r2)
            r0.<init>(r1, r2)
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.token
            if (r1 == 0) goto L_0x0020
            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.token
            java.lang.String r2 = ""
            if (r1 == r2) goto L_0x0020
            android.content.res.Resources r1 = r7.res
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Functions.calcNewToken(r0, r1)
            r7.token = r0
            goto L_0x0024
        L_0x0020:
            java.lang.String r0 = "Ga5mM61KCm5Bk18lhD5J999jC2Mu0Vaf"
            r7.token = r0
        L_0x0024:
            java.lang.String r1 = r7.url
            java.lang.String r2 = r7.payload
            java.lang.String r3 = r7.token
            java.lang.String r4 = it.tecnosystemi.TS.Utils.Constants.user
            int r5 = r7.method
            r6 = 16
            it.tecnosystemi.TS.Model.Response r0 = it.tecnosystemi.TS.Threads.WebClientDevWrapper.getNewHttpClient(r1, r2, r3, r4, r5, r6)
            r7.response = r0
            if (r0 != 0) goto L_0x0043
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity
            it.tecnosystemi.TS.Threads.ThreadWebService$55 r1 = new it.tecnosystemi.TS.Threads.ThreadWebService$55
            r1.<init>()
            r0.runOnUiThread(r1)
            return
        L_0x0043:
            int r0 = r0.getHttpResponceCode()
            r1 = 200(0xc8, float:2.8E-43)
            r2 = 2
            java.lang.String r3 = "ResCode"
            if (r0 != r1) goto L_0x00bc
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ JSONException -> 0x00ad }
            it.tecnosystemi.TS.Model.Response r1 = r7.response     // Catch:{ JSONException -> 0x00ad }
            java.lang.String r1 = r1.getHttpResponcePayload()     // Catch:{ JSONException -> 0x00ad }
            r0.<init>(r1)     // Catch:{ JSONException -> 0x00ad }
            boolean r1 = r0.has(r3)     // Catch:{ JSONException -> 0x00ad }
            if (r1 == 0) goto L_0x009f
            int r0 = r0.getInt(r3)     // Catch:{ JSONException -> 0x00ad }
            if (r0 == 0) goto L_0x0078
            if (r0 != r2) goto L_0x006c
            r7.pinerror()     // Catch:{ JSONException -> 0x00ad }
            goto L_0x00f8
        L_0x006c:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity     // Catch:{ JSONException -> 0x00ad }
            it.tecnosystemi.TS.Threads.ThreadWebService$56 r1 = new it.tecnosystemi.TS.Threads.ThreadWebService$56     // Catch:{ JSONException -> 0x00ad }
            r1.<init>()     // Catch:{ JSONException -> 0x00ad }
            r0.runOnUiThread(r1)     // Catch:{ JSONException -> 0x00ad }
            goto L_0x00f8
        L_0x0078:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity     // Catch:{ JSONException -> 0x00ad }
            android.content.res.Resources r0 = r0.getResources()     // Catch:{ JSONException -> 0x00ad }
            int r1 = it.tecnosystemi.TS.R.string.okSendCMDToServer     // Catch:{ JSONException -> 0x00ad }
            java.lang.String r0 = r0.getString(r1)     // Catch:{ JSONException -> 0x00ad }
            java.lang.String[] r1 = r7.params     // Catch:{ Exception -> 0x009b }
            r2 = 1
            r1 = r1[r2]     // Catch:{ Exception -> 0x009b }
            boolean r1 = it.tecnosystemi.TS.Utils.Functions.IS4x(r1)     // Catch:{ Exception -> 0x009b }
            if (r1 == 0) goto L_0x009b
            it.tecnosystemi.TS.Activity.BaseActivity r1 = r7.activity     // Catch:{ Exception -> 0x009b }
            android.content.res.Resources r1 = r1.getResources()     // Catch:{ Exception -> 0x009b }
            int r2 = it.tecnosystemi.TS.R.string.msg_commandOk     // Catch:{ Exception -> 0x009b }
            java.lang.String r0 = r1.getString(r2)     // Catch:{ Exception -> 0x009b }
        L_0x009b:
            r7.toastOnMainThread(r0)     // Catch:{ JSONException -> 0x00ad }
            goto L_0x00f8
        L_0x009f:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity     // Catch:{ JSONException -> 0x00ad }
            android.content.res.Resources r1 = r7.res     // Catch:{ JSONException -> 0x00ad }
            int r2 = it.tecnosystemi.TS.R.string.msg_commandKo     // Catch:{ JSONException -> 0x00ad }
            java.lang.String r1 = r1.getString(r2)     // Catch:{ JSONException -> 0x00ad }
            it.tecnosystemi.TS.Utils.Functions.makeErrorToast(r0, r1)     // Catch:{ JSONException -> 0x00ad }
            goto L_0x00f8
        L_0x00ad:
            r0 = move-exception
            r0.printStackTrace()
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity
            it.tecnosystemi.TS.Threads.ThreadWebService$57 r1 = new it.tecnosystemi.TS.Threads.ThreadWebService$57
            r1.<init>()
            r0.runOnUiThread(r1)
            goto L_0x00f8
        L_0x00bc:
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x00f8 }
            it.tecnosystemi.TS.Model.Response r1 = r7.response     // Catch:{ Exception -> 0x00f8 }
            java.lang.String r1 = r1.getHttpResponcePayload()     // Catch:{ Exception -> 0x00f8 }
            r0.<init>(r1)     // Catch:{ Exception -> 0x00f8 }
            it.tecnosystemi.TS.Model.Response r1 = r7.response     // Catch:{ Exception -> 0x00f8 }
            int r1 = r1.getHttpResponceCode()     // Catch:{ Exception -> 0x00f8 }
            r4 = 400(0x190, float:5.6E-43)
            if (r1 != r4) goto L_0x00ee
            boolean r1 = r0.has(r3)     // Catch:{ Exception -> 0x00f8 }
            if (r1 == 0) goto L_0x00dc
            int r0 = r0.getInt(r3)     // Catch:{ Exception -> 0x00f8 }
            goto L_0x00dd
        L_0x00dc:
            r0 = -1
        L_0x00dd:
            if (r0 != r2) goto L_0x00e3
            r7.pinerror()     // Catch:{ Exception -> 0x00f8 }
            goto L_0x00f8
        L_0x00e3:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity     // Catch:{ Exception -> 0x00f8 }
            it.tecnosystemi.TS.Threads.ThreadWebService$58 r1 = new it.tecnosystemi.TS.Threads.ThreadWebService$58     // Catch:{ Exception -> 0x00f8 }
            r1.<init>()     // Catch:{ Exception -> 0x00f8 }
            r0.runOnUiThread(r1)     // Catch:{ Exception -> 0x00f8 }
            goto L_0x00f8
        L_0x00ee:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity     // Catch:{ Exception -> 0x00f8 }
            it.tecnosystemi.TS.Threads.ThreadWebService$59 r1 = new it.tecnosystemi.TS.Threads.ThreadWebService$59     // Catch:{ Exception -> 0x00f8 }
            r1.<init>()     // Catch:{ Exception -> 0x00f8 }
            r0.runOnUiThread(r1)     // Catch:{ Exception -> 0x00f8 }
        L_0x00f8:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Threads.ThreadWebService.updCUDatetime():void");
    }

    private void addNewCu() {
        if (Constants.modesviluppatore) {
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    BaseActivity baseActivity = ThreadWebService.this.activity;
                    Functions.makeNormalToast(baseActivity, "addNewCu" + ((ConfigActivity) ThreadWebService.this.activity).times);
                }
            });
        }
        BaseActivity baseActivity = this.activity;
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        if (Constants.token == null || Constants.token == "") {
            this.token = Constants.FIX_TOKEN;
        } else {
            this.token = Functions.calcNewToken(savePreferences, this.res);
        }
        this.response = WebClientDevWrapper.getNewHttpClient(this.url, this.payload, this.token, Constants.user, this.method, 17);
        if (Constants.modesviluppatore) {
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    if (ThreadWebService.this.response == null) {
                        BaseActivity baseActivity = ThreadWebService.this.activity;
                        Functions.makeNormalToast(baseActivity, "NULL" + ((ConfigActivity) ThreadWebService.this.activity).times);
                        return;
                    }
                    BaseActivity baseActivity2 = ThreadWebService.this.activity;
                    Functions.makeNormalToast(baseActivity2, ThreadWebService.this.response.getHttpResponcePayload() + ((ConfigActivity) ThreadWebService.this.activity).times);
                }
            });
        }
        Response response2 = this.response;
        if (response2 == null) {
            ((ConfigActivity) this.activity).tryNewCU();
            return;
        }
        if (response2.getHttpResponceCode() == 200) {
            try {
                JSONObject jSONObject = new JSONObject(this.response.getHttpResponcePayload());
                if (jSONObject.has("ResCode") && jSONObject.getInt("ResCode") == 0) {
                    BaseActivity baseActivity2 = this.activity;
                    ((ConfigActivity) baseActivity2).times = ((ConfigActivity) baseActivity2).CONFIG_TIMES;
                    ((ConfigActivity) this.activity).checkCentralina();
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (this.response.getHttpResponceCode() == 401) {
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    Functions.makeNormalToast(ThreadWebService.this.activity, ThreadWebService.this.activity.getResources().getString(R.string.sessioneScaduta));
                    Intent intent = new Intent(ThreadWebService.this.activity, LoginActivity.class);
                    intent.addFlags(67108864);
                    ThreadWebService.this.activity.startActivity(intent);
                }
            });
            return;
        }
        ((ConfigActivity) this.activity).tryNewCU();
    }

    private void checkConfigCU() {
        if (Constants.modesviluppatore) {
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    BaseActivity baseActivity = ThreadWebService.this.activity;
                    Functions.makeNormalToast(baseActivity, "CHECK CENTRALINA" + ((ConfigActivity) ThreadWebService.this.activity).times);
                }
            });
        }
        BaseActivity baseActivity = this.activity;
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        if (Constants.token == null || Constants.token == "") {
            this.token = Constants.FIX_TOKEN;
        } else {
            this.token = Functions.calcNewToken(savePreferences, this.res);
        }
        this.response = WebClientDevWrapper.getNewHttpClient(this.url, this.payload, this.token, Constants.user, this.method, 18);
        if (Constants.modesviluppatore) {
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    if (ThreadWebService.this.response == null) {
                        BaseActivity baseActivity = ThreadWebService.this.activity;
                        Functions.makeNormalToast(baseActivity, "NULL" + ((ConfigActivity) ThreadWebService.this.activity).times);
                        return;
                    }
                    BaseActivity baseActivity2 = ThreadWebService.this.activity;
                    Functions.makeNormalToast(baseActivity2, ThreadWebService.this.response.getHttpResponcePayload() + ((ConfigActivity) ThreadWebService.this.activity).times);
                }
            });
        }
        Response response2 = this.response;
        if (response2 == null) {
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    ((ConfigActivity) ThreadWebService.this.activity).checkCentralina();
                }
            });
            return;
        }
        if (response2.getHttpResponceCode() == 200) {
            try {
                JSONObject jSONObject = new JSONObject(this.response.getHttpResponcePayload());
                if (jSONObject.has("ResCode") && jSONObject.getInt("ResCode") == 0) {
                    BaseActivity baseActivity2 = this.activity;
                    ((ConfigActivity) baseActivity2).times = ((ConfigActivity) baseActivity2).CONFIG_TIMES;
                    ((ConfigActivity) this.activity).addCentralinaOnline();
                    return;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (this.response.getHttpResponceCode() == 401) {
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    Functions.makeNormalToast(ThreadWebService.this.activity, ThreadWebService.this.activity.getResources().getString(R.string.sessioneScaduta));
                    Intent intent = new Intent(ThreadWebService.this.activity, LoginActivity.class);
                    intent.addFlags(67108864);
                    ThreadWebService.this.activity.startActivity(intent);
                }
            });
            return;
        }
        ((ConfigActivity) this.activity).checkCentralina();
    }

    private void connUsrPico() {
        BaseActivity baseActivity = this.activity;
        SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
        if (Constants.token == null || Constants.token == "") {
            this.token = Constants.FIX_TOKEN;
        } else {
            this.token = Functions.calcNewToken(savePreferences, this.res);
        }
        Response newHttpClient = WebClientDevWrapper.getNewHttpClient(this.url, this.payload, this.token, Constants.user, this.method, 8);
        this.response = newHttpClient;
        if (newHttpClient == null) {
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    Functions.makeErrorToast(ThreadWebService.this.activity, ThreadWebService.this.activity.getResources().getString(R.string.resCodeError));
                }
            });
        } else if (newHttpClient.getHttpResponceCode() == 200) {
            try {
                JSONObject jSONObject = new JSONObject(this.response.getHttpResponcePayload());
                if ((jSONObject.has("ResCode") ? jSONObject.getInt("ResCode") : -1) != 0) {
                    this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Functions.makeErrorToast(ThreadWebService.this.activity, ThreadWebService.this.res.getString(R.string.au_errorAddUser));
                        }
                    });
                    return;
                }
                BaseActivity baseActivity2 = this.activity;
                if (baseActivity2 instanceof SetNameAndPinPICOActivity) {
                    baseActivity2.runOnUiThread(new Runnable() {
                        public void run() {
                            ((SetNameAndPinPICOActivity) ThreadWebService.this.activity).savserverPico();
                        }
                    });
                }
                BaseActivity baseActivity3 = this.activity;
                if (baseActivity3 instanceof SetNameAndPinVMCActivity) {
                    baseActivity3.runOnUiThread(new Runnable() {
                        public void run() {
                            ((SetNameAndPinVMCActivity) ThreadWebService.this.activity).savserverVMC();
                        }
                    });
                }
                BaseActivity baseActivity4 = this.activity;
                if (baseActivity4 instanceof SetNameAndPinSeiXActivity) {
                    baseActivity4.runOnUiThread(new Runnable() {
                        public void run() {
                            ((SetNameAndPinSeiXActivity) ThreadWebService.this.activity).savserver6X();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                JSONObject jSONObject2 = new JSONObject(this.response.getHttpResponcePayload());
                if (this.response.getHttpResponceCode() == 400) {
                    if (jSONObject2.has("ResCode")) {
                        jSONObject2.getInt("ResCode");
                    }
                    this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Functions.makeErrorToast(ThreadWebService.this.activity, ThreadWebService.this.res.getString(R.string.au_errorAddUser));
                        }
                    });
                    return;
                }
                this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Functions.makeErrorToast(ThreadWebService.this.activity, ThreadWebService.this.activity.getResources().getString(R.string.resCodeError));
                    }
                });
            } catch (Exception unused) {
            }
        }
    }

    public void pinerror() {
        this.activity.runOnUiThread(new Runnable() {
            public void run() {
                Functions.makeErrorToast(ThreadWebService.this.activity, ThreadWebService.this.activity.getResources().getString(R.string.pin_error));
                ThreadWebService.this.activity.pinerror();
            }
        });
    }

    public void toastOnMainThread(final String str) {
        this.activity.runOnUiThread(new Runnable() {
            public void run() {
                Functions.makeNormalToast(ThreadWebService.this.activity, str);
            }
        });
    }
}
