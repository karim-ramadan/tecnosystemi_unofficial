package it.tecnosystemi.TS.Activity.SEIX.Config;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.gson.Gson;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity;
import it.tecnosystemi.TS.Commands.CmdPICO;
import it.tecnosystemi.TS.Commands.UDPSocket;
import it.tecnosystemi.TS.Model.Device;
import it.tecnosystemi.TS.Model.Device_OP;
import it.tecnosystemi.TS.Model.ModBusRecipe;
import it.tecnosystemi.TS.Model.Response;
import it.tecnosystemi.TS.Model.SeiX;
import it.tecnosystemi.TS.Model.WiFi;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Threads.ThreadWebService;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import it.tecnosystemi.TS.Utils.SavePreferences;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONObject;

public class ConfigSeiXActivity extends BaseActivity {
    public static Activity ConFAct = null;
    public static int ID_SLAVE = 0;
    public static int VERSO = 0;
    public static final boolean WriteLogs = false;
    public static TextView lblLogs;
    public int CONFIG_TIMES = 40;
    ModBusRecipe RICETTA;
    BaseActivity activity;
    int akwait = 15000;
    boolean blockCheckPico;
    Button btnFine;
    Thread checkcentralina;
    boolean continueThread = true;
    TextView lblDesrc;
    int mode;
    String name;
    String payload;
    String pin;
    SavePreferences pref;
    String serial;
    String serialToCheck;
    String ssid;
    boolean stop;
    int timeout = 30000;
    public int times = 40;
    String token;
    WiFi wifi;

    public BaseActivity getActivity() {
        return this;
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        return list;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_config_sei_xactivity);
        this.activity = this;
        super.onCreate(bundle);
        TextView textView = (TextView) findViewById(R.id.lblLogs);
        lblLogs = textView;
        textView.setMovementMethod(new ScrollingMovementMethod());
        ConFAct = this;
        hideMenuButton();
        setUpGui();
        disableView();
        if (!Constants.ISDEMO) {
            showProgress();
            int intExtra = getIntent().getIntExtra(Constants.INTENT_SETUPMODE, -1);
            this.mode = intExtra;
            if (intExtra == -1) {
                finish();
                return;
            }
            this.name = getIntent().getStringExtra("name");
            this.pin = getIntent().getStringExtra(Constants.INTENT_PIN);
            BaseActivity baseActivity = this.activity;
            this.pref = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
            int i = this.mode;
            if (i == 1) {
                final WiFi wiFi = (WiFi) getIntent().getSerializableExtra(Constants.INTENT_WIFI);
                connectToWifi(new Runnable() {
                    public void run() {
                        new Thread(new Runnable() {
                            public void run() {
                                AnonymousClass1 r1 = new Runnable() {
                                    public void run() {
                                    }
                                };
                                CmdPICO.ConfigMasterOnline configMasterOnline = new CmdPICO.ConfigMasterOnline();
                                configMasterOnline.setName(ConfigSeiXActivity.this.name);
                                configMasterOnline.setPin(ConfigSeiXActivity.this.pin);
                                configMasterOnline.setConfig_mod(2);
                                configMasterOnline.setWifi_sec(wiFi.isCrip());
                                configMasterOnline.setWifi_ssid(wiFi.getSid());
                                configMasterOnline.setWifi_pwd(wiFi.getPwd());
                                configMasterOnline.setWifi_mac(wiFi.getMac());
                                if (Constants.token == null || Constants.token == "") {
                                    ConfigSeiXActivity.this.token = Constants.FIX_TOKEN;
                                } else {
                                    ConfigSeiXActivity.this.token = Functions.calcNewToken(ConfigSeiXActivity.this.pref, ConfigSeiXActivity.this.getResources());
                                }
                                configMasterOnline.setApi_token(ConfigSeiXActivity.this.token);
                                configMasterOnline.setApi_user(Constants.user);
                                String str = null;
                                for (int i = 0; i < 5; i++) {
                                    if (ConfigSeiXActivity.this.getCurretWifiName().replace("\"", "").equals(BaseActivity.toConnSid)) {
                                        UDPSocket.startListening(true);
                                        str = UDPSocket.sendCMD(configMasterOnline, 15000, (long) ConfigSeiXActivity.this.akwait);
                                        UDPSocket.stopListening();
                                        if (str != null) {
                                            break;
                                        }
                                        try {
                                            Thread.sleep(500);
                                        } catch (Exception unused) {
                                        }
                                    } else {
                                        ConfigSeiXActivity.this.connectToWifi(r1, false, true);
                                        try {
                                            Thread.sleep(10000);
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }
                                ConfigSeiXActivity.this.hideProgress();
                                if (str == null) {
                                    ConfigSeiXActivity.this.error();
                                    return;
                                }
                                try {
                                    JSONObject jSONObject = new JSONObject(str);
                                    if (jSONObject.getInt(Constants.JSON_RES) != 1) {
                                        ConfigSeiXActivity.this.error();
                                    } else if (!jSONObject.has("ser")) {
                                        ConfigSeiXActivity.this.error();
                                    } else {
                                        ConfigSeiXActivity.this.serialToCheck = jSONObject.getString("ser");
                                        ConfigSeiXActivity.this.CheckInfo6X(100, 0, 0, 300000);
                                    }
                                } catch (Exception unused2) {
                                    ConfigSeiXActivity.this.error();
                                }
                            }
                        }).start();
                    }
                }, false, true);
            } else if (i == 3) {
                connectToWifi(new Runnable() {
                    public void run() {
                        new Thread(new Runnable() {
                            public void run() {
                                CmdPICO.ConfigMasterOffline configMasterOffline = new CmdPICO.ConfigMasterOffline();
                                configMasterOffline.setConfig_mod(1);
                                configMasterOffline.setName(ConfigSeiXActivity.this.name);
                                configMasterOffline.setPin(ConfigSeiXActivity.this.pin);
                                UDPSocket.startListening();
                                String sendCMD = UDPSocket.sendCMD(configMasterOffline, (long) ConfigSeiXActivity.this.akwait, (long) ConfigSeiXActivity.this.akwait);
                                if (sendCMD == null) {
                                    ConfigSeiXActivity.this.error();
                                }
                                try {
                                    JSONObject jSONObject = new JSONObject(sendCMD);
                                    if (jSONObject.getInt(Constants.JSON_RES) == 1) {
                                        String string = jSONObject.getString(Constants.JSON_SER);
                                        if (string == null || string.isEmpty()) {
                                            ConfigSeiXActivity.this.error();
                                        } else {
                                            boolean unused = ConfigSeiXActivity.this.set6XWiFiMode(1);
                                            ConfigSeiXActivity.this.disconnectFromWIfi();
                                            ConfigSeiXActivity.this.add6X(string, true);
                                        }
                                    } else {
                                        ConfigSeiXActivity.this.error();
                                    }
                                } catch (Exception unused2) {
                                    ConfigSeiXActivity.this.error();
                                }
                                try {
                                    UDPSocket.stopListening();
                                } catch (Exception unused3) {
                                }
                            }
                        }).start();
                    }
                }, false, true);
            }
        } else {
            this.btnFine.setEnabled(true);
        }
    }

    private boolean check_invioRicetta() {
        try {
            JSONObject fw = getFw();
            String string = fw.getString("fw_ver");
            String string2 = fw.getString("key_recipe");
            this.RICETTA = null;
            for (int i = 0; i < Constants.MODBUSRECEPIES.size() && this.RICETTA == null; i++) {
                if (Constants.MODBUSRECEPIES.get(i).getPRAN_Id() == 2) {
                    this.RICETTA = Constants.MODBUSRECEPIES.get(i);
                }
            }
            int i2 = -1;
            int i3 = -1;
            for (int i4 = 0; i4 < this.RICETTA.getFws().size() && i2 < 0; i4++) {
                int compareVersions = Functions.compareVersions(this.RICETTA.getFws().get(i4).getPRFW_Version(), string);
                if (compareVersions == 0) {
                    i2 = i4;
                } else if (compareVersions < 0) {
                    if (i3 != -1) {
                        if (Functions.compareVersions(this.RICETTA.getFws().get(i4).getPRFW_Version(), this.RICETTA.getFws().get(i3).getPRFW_Version()) <= 0) {
                        }
                    }
                    i3 = i4;
                }
            }
            if (i2 < 0) {
                i2 = i3 < 0 ? 0 : i3;
            }
            this.RICETTA.getFws().get(i2).getPRFW_RecipeKey().equals(string2);
            return sendRecipe(i2);
        } catch (Exception unused) {
            return false;
        }
    }

    private boolean sendRecipe(int i) {
        try {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            List<ModBusRecipe.Param> params = Functions.getParams(this.RICETTA.getFws().get(i).getPRFW_Version(), 1);
            int i2 = 0;
            for (int i3 = 0; i3 < params.size(); i3++) {
                i2++;
                if (arrayList2.size() >= Constants.RECIPE_MAX_SIZE) {
                    arrayList.add(arrayList2);
                    arrayList2 = new ArrayList();
                }
                arrayList2.add(params.get(i3));
            }
            if (arrayList2.size() > 0) {
                arrayList.add(arrayList2);
            }
            String pRFW_RecipeKey = this.RICETTA.getFws().get(i).getPRFW_RecipeKey();
            CmdPICO.Init_End_Recipe init_End_Recipe = new CmdPICO.Init_End_Recipe();
            init_End_Recipe.setCmd("ini_recipe");
            init_End_Recipe.setKey_recipe(pRFW_RecipeKey);
            init_End_Recipe.setPrmtot(i2);
            init_End_Recipe.setNumblock(arrayList.size());
            UDPSocket.startListening();
            String sendCMD = UDPSocket.sendCMD(init_End_Recipe);
            if (sendCMD == null || new JSONObject(sendCMD).getInt("res") != 1) {
                return false;
            }
            int i4 = 0;
            while (i4 < arrayList.size()) {
                CmdPICO.Send_Recipe send_Recipe = new CmdPICO.Send_Recipe();
                send_Recipe.setKey_recipe(pRFW_RecipeKey);
                send_Recipe.setPrmtot(i2);
                int i5 = i4 + 1;
                send_Recipe.setBlock(i5);
                send_Recipe.setPrmprz(((List) arrayList.get(i4)).size());
                send_Recipe.setLstprm((List) arrayList.get(i4));
                UDPSocket.startListening();
                String sendCMD2 = UDPSocket.sendCMD(send_Recipe);
                if (sendCMD2 == null || new JSONObject(sendCMD2).getInt("res") != 1) {
                    return false;
                }
                i4 = i5;
            }
            CmdPICO.Init_End_Recipe init_End_Recipe2 = new CmdPICO.Init_End_Recipe();
            init_End_Recipe2.setCmd("end_recipe");
            init_End_Recipe2.setKey_recipe(pRFW_RecipeKey);
            init_End_Recipe2.setPrmtot(i2);
            init_End_Recipe2.setNumblock(arrayList.size());
            UDPSocket.startListening();
            String sendCMD3 = UDPSocket.sendCMD(init_End_Recipe2);
            if (sendCMD3 == null || new JSONObject(sendCMD3).getInt("res") != 1) {
                return false;
            }
            try {
                Thread.sleep(2000);
            } catch (Exception unused) {
            }
            return true;
        } catch (Exception e) {
            Log.d(this.TAG, e.toString());
            return false;
        }
    }

    private JSONObject getFw() {
        UDPSocket.startListening();
        Date date = new Date();
        CmdPICO cmdPICO = new CmdPICO();
        cmdPICO.setCmd("get_info");
        cmdPICO.setPin(this.pin);
        this.blockCheckPico = false;
        long j = 0;
        while (j < 30000 && !this.blockCheckPico) {
            try {
                UDPSocket.startListening(true);
                String sendCMD = UDPSocket.sendCMD(cmdPICO, 10000, 10000);
                UDPSocket.stopListening();
                if (sendCMD != null) {
                    return new JSONObject(sendCMD);
                }
            } catch (Exception unused) {
            }
            j = new Date().getTime() - date.getTime();
            try {
                Thread.sleep(5000);
            } catch (Exception unused2) {
            }
        }
        return null;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(7:42|43|44|45|46|47|48) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:47:0x00c3 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void CheckInfo6X(int r21, int r22, int r23, long r24) {
        /*
            r20 = this;
            r0 = r20
            java.lang.String r1 = "mqtt_sta"
            java.lang.String r2 = "wifi_sta"
            java.lang.String r3 = "net_st"
            java.util.Date r4 = new java.util.Date
            r4.<init>()
            it.tecnosystemi.TS.Commands.CmdPICO r5 = new it.tecnosystemi.TS.Commands.CmdPICO
            r5.<init>()
            java.lang.String r6 = "get_info"
            r5.setCmd(r6)
            java.lang.String r6 = r0.pin
            r5.setPin(r6)
            r6 = 0
            r0.blockCheckPico = r6
            r7 = -1
            r8 = 0
            r10 = r8
            r8 = -1
            r9 = -1
            r12 = 0
        L_0x0026:
            r13 = 1
            if (r12 != 0) goto L_0x00b0
            int r14 = (r10 > r24 ? 1 : (r10 == r24 ? 0 : -1))
            if (r14 >= 0) goto L_0x00b0
            boolean r10 = r0.blockCheckPico
            if (r10 != 0) goto L_0x00b0
            java.lang.String r10 = r20.getCurretWifiName()
            java.lang.String r11 = "\""
            java.lang.String r14 = ""
            java.lang.String r10 = r10.replace(r11, r14)
            java.lang.String r11 = toConnSid
            boolean r10 = r10.equals(r11)
            r14 = 5000(0x1388, double:2.4703E-320)
            if (r10 == 0) goto L_0x008b
            it.tecnosystemi.TS.Commands.UDPSocket.startListening(r13)     // Catch:{ Exception -> 0x0088 }
            r10 = 10000(0x2710, double:4.9407E-320)
            java.lang.String r10 = it.tecnosystemi.TS.Commands.UDPSocket.sendCMD(r5, r10, r10)     // Catch:{ Exception -> 0x0088 }
            it.tecnosystemi.TS.Commands.UDPSocket.stopListening()     // Catch:{ Exception -> 0x0088 }
            if (r10 == 0) goto L_0x0088
            org.json.JSONObject r11 = new org.json.JSONObject     // Catch:{ Exception -> 0x0088 }
            r11.<init>(r10)     // Catch:{ Exception -> 0x0088 }
            java.lang.String r10 = it.tecnosystemi.TS.Utils.Constants.JSON_RES     // Catch:{ Exception -> 0x0088 }
            int r10 = r11.getInt(r10)     // Catch:{ Exception -> 0x0088 }
            if (r10 != r13) goto L_0x0088
            boolean r10 = r11.has(r3)     // Catch:{ Exception -> 0x0088 }
            if (r10 == 0) goto L_0x006c
            int r8 = r11.getInt(r3)     // Catch:{ Exception -> 0x0088 }
        L_0x006c:
            boolean r10 = r11.has(r2)     // Catch:{ Exception -> 0x0088 }
            if (r10 == 0) goto L_0x0076
            int r7 = r11.getInt(r2)     // Catch:{ Exception -> 0x0088 }
        L_0x0076:
            boolean r10 = r11.has(r1)     // Catch:{ Exception -> 0x0088 }
            if (r10 == 0) goto L_0x0080
            int r9 = r11.getInt(r1)     // Catch:{ Exception -> 0x0088 }
        L_0x0080:
            r10 = r21
            if (r8 != r10) goto L_0x0086
            r12 = 1
            goto L_0x0098
        L_0x0086:
            r12 = 0
            goto L_0x0098
        L_0x0088:
            r10 = r21
            goto L_0x0098
        L_0x008b:
            r10 = r21
            it.tecnosystemi.TS.Activity.SEIX.Config.ConfigSeiXActivity$3 r11 = new it.tecnosystemi.TS.Activity.SEIX.Config.ConfigSeiXActivity$3
            r11.<init>()
            r0.connectToWifi(r11, r6, r13)
            java.lang.Thread.sleep(r14)     // Catch:{ Exception -> 0x0098 }
        L_0x0098:
            java.util.Date r11 = new java.util.Date
            r11.<init>()
            long r16 = r11.getTime()
            long r18 = r4.getTime()
            long r16 = r16 - r18
            java.lang.Thread.sleep(r14)     // Catch:{ Exception -> 0x00ab }
            goto L_0x00ac
        L_0x00ab:
        L_0x00ac:
            r10 = r16
            goto L_0x0026
        L_0x00b0:
            r20.hideProgress()
            if (r12 == 0) goto L_0x00d2
            it.tecnosystemi.TS.Commands.UDPSocket.startListening()
            int r1 = r0.mode
            if (r1 != r13) goto L_0x0133
            r1 = 2
            r0.set6XWiFiMode(r1)     // Catch:{ Exception -> 0x00ce }
            it.tecnosystemi.TS.Commands.UDPSocket.stopListening()     // Catch:{ Exception -> 0x00c3 }
        L_0x00c3:
            r20.unbidNetwork()     // Catch:{ Exception -> 0x00ce }
            int r1 = r0.CONFIG_TIMES     // Catch:{ Exception -> 0x00ce }
            r0.times = r1     // Catch:{ Exception -> 0x00ce }
            r20.check6XOnline()     // Catch:{ Exception -> 0x00ce }
            goto L_0x0133
        L_0x00ce:
            r20.error()
            goto L_0x0133
        L_0x00d2:
            if (r7 <= 0) goto L_0x00e7
            r1 = 7
            if (r7 >= r1) goto L_0x00e7
            it.tecnosystemi.TS.Activity.BaseActivity r1 = r0.activity
            android.content.res.Resources r2 = r20.getResources()
            int r3 = it.tecnosystemi.TS.R.string.c5_lblConfigKo_wifiProblem
            java.lang.String r2 = r2.getString(r3)
            it.tecnosystemi.TS.Utils.Functions.makeErrorToast(r1, r2)
            goto L_0x012b
        L_0x00e7:
            if (r7 > 0) goto L_0x00fc
            if (r8 > 0) goto L_0x00fc
            if (r9 <= 0) goto L_0x00ee
            goto L_0x00fc
        L_0x00ee:
            android.content.res.Resources r1 = r20.getResources()
            int r2 = it.tecnosystemi.TS.R.string.not_errcom
            java.lang.String r1 = r1.getString(r2)
            r0.showErrConf(r1)
            goto L_0x012b
        L_0x00fc:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            android.content.res.Resources r2 = r20.getResources()
            int r3 = it.tecnosystemi.TS.R.string.c5_lblConfigKo_errorCode
            java.lang.String r2 = r2.getString(r3)
            r1.append(r2)
            java.lang.String r2 = "\n"
            r1.append(r2)
            r1.append(r7)
            java.lang.String r2 = "-"
            r1.append(r2)
            r1.append(r9)
            r1.append(r2)
            r1.append(r8)
            java.lang.String r1 = r1.toString()
            r0.showErrConf(r1)
        L_0x012b:
            it.tecnosystemi.TS.Activity.SEIX.Config.ConfigSeiXActivity$4 r1 = new it.tecnosystemi.TS.Activity.SEIX.Config.ConfigSeiXActivity$4
            r1.<init>()
            r0.runOnUiThread(r1)
        L_0x0133:
            it.tecnosystemi.TS.Commands.UDPSocket.stopListening()     // Catch:{ Exception -> 0x0136 }
        L_0x0136:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.SEIX.Config.ConfigSeiXActivity.CheckInfo6X(int, int, int, long):void");
    }

    private void check6XOnline() {
        showProgress();
        int i = this.times;
        if (i > 0) {
            this.times = i - 1;
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            Device_OP device_OP = new Device_OP();
                            device_OP.setPIN(ConfigSeiXActivity.this.pin);
                            device_OP.setSerial(ConfigSeiXActivity.this.serialToCheck);
                            device_OP.setPlantId(TSDeviceListActivity.idSelected);
                            device_OP.setToken(ConfigSeiXActivity.this.activity.FirebaseToken);
                            device_OP.setPlatform(Constants.NOTIFIC_PLAT);
                            device_OP.setType_dev(Constants.DEVICE_TYPE_6X);
                            device_OP.setPhoneDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date()));
                            Gson gson = new Gson();
                            new ThreadWebService(ConfigSeiXActivity.this.activity, 1, 29, ConfigSeiXActivity.this.getResources().getString(R.string.uriWebService) + ConfigSeiXActivity.this.getResources().getString(R.string.uri_AddUserToDeviceV2), gson.toJson((Object) device_OP), (String[]) null).start();
                        }
                    }, 5000);
                }
            });
            return;
        }
        error();
    }

    public void parseCheckConfig(Response response) {
        if (response != null) {
            try {
                if (response.getHttpResponceCode() == 200 && new JSONObject(response.getHttpResponcePayload()).getInt("ResCode") == 0) {
                    add6X(this.serialToCheck, false);
                    return;
                }
            } catch (Exception unused) {
            }
        }
        check6XOnline();
    }

    public void add6X(String str, boolean z) {
        Device device = new Device();
        device.setSerial(str);
        device.setLVDV_Type(Constants.DEVICE_TYPE_6X);
        SeiX.delete6XfromPref(str, this.activity);
        SeiX.save6XInPref(this.name, str, this.pin, "", this.activity, z, true);
        addDevToPlant(str);
        hideProgress();
        this.activity.runOnUiThread(new Runnable() {
            public void run() {
                ConfigSeiXActivity.this.enableView();
                ConfigSeiXActivity.this.lblDesrc.setText(ConfigSeiXActivity.this.getResources().getString(R.string.c5_deviceAdd));
                ConfigSeiXActivity.this.lblDesrc.setTextColor(-1);
            }
        });
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0077  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x008f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void addDevToPlant(java.lang.String r7) {
        /*
            r6 = this;
            it.tecnosystemi.TS.Model.Device r0 = new it.tecnosystemi.TS.Model.Device
            r0.<init>()
            int r1 = it.tecnosystemi.TS.Utils.Constants.DEVICE_TYPE_6X
            r0.setLVDV_Type(r1)
            r0.setSerial(r7)
            java.lang.String r7 = r6.name
            r0.setName(r7)
            it.tecnosystemi.TS.Activity.BaseActivity r7 = r6.activity
            it.tecnosystemi.TS.Model.Device.deleteDevFromPref(r0, r7)
            long r1 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.idSelected
            it.tecnosystemi.TS.Model.Plant.addDeviceToPlantPref(r0, r1, r6)
            r7 = 0
        L_0x001d:
            java.util.List<it.tecnosystemi.TS.Model.Plant> r1 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            int r1 = r1.size()
            if (r7 >= r1) goto L_0x00a3
            java.util.List<it.tecnosystemi.TS.Model.Plant> r1 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r1 = r1.get(r7)
            it.tecnosystemi.TS.Model.Plant r1 = (it.tecnosystemi.TS.Model.Plant) r1
            long r1 = r1.getLVPL_Id()
            long r3 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.idSelected
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x009f
            java.util.List<it.tecnosystemi.TS.Model.Plant> r1 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r1 = r1.get(r7)
            it.tecnosystemi.TS.Model.Plant r1 = (it.tecnosystemi.TS.Model.Plant) r1
            java.util.List r1 = r1.getListDevices()
            if (r1 == 0) goto L_0x0064
            java.util.List<it.tecnosystemi.TS.Model.Plant> r1 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r1 = r1.get(r7)
            it.tecnosystemi.TS.Model.Plant r1 = (it.tecnosystemi.TS.Model.Plant) r1
            java.util.List r1 = r1.getListDevices()
            if (r1 == 0) goto L_0x0074
            java.util.List<it.tecnosystemi.TS.Model.Plant> r1 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r1 = r1.get(r7)
            it.tecnosystemi.TS.Model.Plant r1 = (it.tecnosystemi.TS.Model.Plant) r1
            java.util.List r1 = r1.getListDevices()
            int r1 = r1.indexOf(r0)
            goto L_0x0075
        L_0x0064:
            java.util.List<it.tecnosystemi.TS.Model.Plant> r1 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r1 = r1.get(r7)
            it.tecnosystemi.TS.Model.Plant r1 = (it.tecnosystemi.TS.Model.Plant) r1
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r1.setListDevices(r2)
        L_0x0074:
            r1 = -1
        L_0x0075:
            if (r1 < 0) goto L_0x008f
            java.util.List<it.tecnosystemi.TS.Model.Plant> r0 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r7 = r0.get(r7)
            it.tecnosystemi.TS.Model.Plant r7 = (it.tecnosystemi.TS.Model.Plant) r7
            java.util.List r7 = r7.getListDevices()
            java.lang.Object r7 = r7.get(r1)
            it.tecnosystemi.TS.Model.Device r7 = (it.tecnosystemi.TS.Model.Device) r7
            java.lang.String r0 = r6.name
            r7.setName(r0)
            goto L_0x00a3
        L_0x008f:
            java.util.List<it.tecnosystemi.TS.Model.Plant> r1 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r7 = r1.get(r7)
            it.tecnosystemi.TS.Model.Plant r7 = (it.tecnosystemi.TS.Model.Plant) r7
            java.util.List r7 = r7.getListDevices()
            r7.add(r0)
            goto L_0x00a3
        L_0x009f:
            int r7 = r7 + 1
            goto L_0x001d
        L_0x00a3:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.SEIX.Config.ConfigSeiXActivity.addDevToPlant(java.lang.String):void");
    }

    /* access modifiers changed from: private */
    public boolean set6XWiFiMode(int i) {
        try {
            CmdPICO.PicoAP picoAP = new CmdPICO.PicoAP();
            picoAP.setCmd("set_ap");
            picoAP.setAp_m(i);
            picoAP.setPin(this.pin);
            String sendCMD = UDPSocket.sendCMD(picoAP);
            UDPSocket.stopListening();
            if (new JSONObject(sendCMD).getInt(Constants.JSON_RES) == 1) {
                return true;
            }
            return false;
        } catch (Exception unused) {
            return false;
        }
    }

    private void showErrConf(final String str) {
        runOnUiThread(new Runnable() {
            public void run() {
                AnonymousClass1 r5 = new Runnable() {
                    public void run() {
                    }
                };
                AnonymousClass2 r6 = new Runnable() {
                    public void run() {
                        ConfigSeiXActivity.this.dismissdialog();
                    }
                };
                ConfigSeiXActivity configSeiXActivity = ConfigSeiXActivity.this;
                ConfigSeiXActivity.this.openDialogFragment(configSeiXActivity.createYesNoPopUp("", str, "", configSeiXActivity.getResources().getString(R.string.general_OK), r5, r6));
            }
        });
    }

    public void error() {
        hideProgress();
        runOnUiThread(new Runnable() {
            public void run() {
                Functions.makeErrorToast(ConfigSeiXActivity.this.activity, ConfigSeiXActivity.this.getResources().getString(R.string.c5_lblConfigKo));
                ConfigSeiXActivity.this.lblDesrc.setText(ConfigSeiXActivity.this.getResources().getString(R.string.c5_lblConfigKo));
                ConfigSeiXActivity.this.lblDesrc.setTextColor(ConfigSeiXActivity.this.getResources().getColor(R.color.colorerror));
                ConfigSeiXActivity.this.enableView();
            }
        });
    }

    private void setUpGui() {
        this.btnFine = (Button) findViewById(R.id.ca_btnFine);
        this.lblDesrc = (TextView) findViewById(R.id.ca_lblDesrc);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
        this.btnFine.setTypeface(createFromAsset);
        this.lblDesrc.setTypeface(createFromAsset);
    }

    public void disableView() {
        this.btnFine.setEnabled(false);
    }

    public void btnFine(View view) {
        if (Constants.ISDEMO) {
            Functions.makeNormalToast(this, getResources().getString(R.string.cu_DemoVersion));
            Intent intent = new Intent(this, TSDeviceListActivity.class);
            intent.addFlags(67108864);
            startActivity(intent);
            return;
        }
        Intent intent2 = new Intent(this, TSDeviceListActivity.class);
        intent2.addFlags(67108864);
        startActivity(intent2);
    }

    public void enableView() {
        this.btnFine.setEnabled(true);
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        try {
            UDPSocket.stopListening();
            this.blockCheckPico = true;
        } catch (Exception unused) {
        }
        this.continueThread = false;
    }

    public View getToolBar() {
        return findViewById(R.id.ca_toolbar);
    }

    public String setToolbarTitle() {
        return getResources().getString(R.string.c5_title);
    }
}
