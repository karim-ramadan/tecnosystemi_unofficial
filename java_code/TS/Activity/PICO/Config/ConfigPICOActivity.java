package it.tecnosystemi.TS.Activity.PICO.Config;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
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
import it.tecnosystemi.TS.Model.Pico;
import it.tecnosystemi.TS.Model.Response;
import it.tecnosystemi.TS.Model.WiFi;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Threads.ThreadWebService;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import it.tecnosystemi.TS.Utils.SavePreferences;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.json.JSONObject;

public class ConfigPICOActivity extends BaseActivity {
    public static Activity ConFAct = null;
    public static int ID_SLAVE = 0;
    public static int VERSO = 0;
    public static final boolean WriteLogs = false;
    public static TextView lblLogs;
    public int CONFIG_TIMES = 40;
    BaseActivity activity;
    int akwait = 15000;
    boolean blockCheckPico;
    Button btnFine;
    Thread checkcentralina;
    boolean continueThread = true;
    long datediff = 0;
    int lastmqtt_sta = -1;
    int lastpico_st = -1;
    int lastwifi_sta = -1;
    TextView lblDesrc;
    int mode;
    String name;
    boolean okconf;
    String payload;
    String pin;
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
        setContentView(R.layout.activity_config_pico_activity);
        this.typeActStyle = 2;
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
            final SavePreferences savePreferences = new SavePreferences(baseActivity, baseActivity.getString(R.string.PrefsName));
            int i = this.mode;
            if (i == 1 || i == 5) {
                final WiFi wiFi = (WiFi) getIntent().getSerializableExtra(Constants.INTENT_WIFI);
                connectToWifi(new Runnable() {
                    public void run() {
                        new Thread(new Runnable() {
                            public void run() {
                                long j;
                                int i;
                                CmdPICO.ConfigMasterOnline configMasterOnline = new CmdPICO.ConfigMasterOnline();
                                configMasterOnline.setName(ConfigPICOActivity.this.name);
                                configMasterOnline.setPin(ConfigPICOActivity.this.pin);
                                if (ConfigPICOActivity.this.mode == 1) {
                                    configMasterOnline.setConfig_mod(2);
                                    configMasterOnline.setWifi_sec(wiFi.isCrip());
                                    configMasterOnline.setWifi_ssid(wiFi.getSid());
                                    configMasterOnline.setWifi_pwd(wiFi.getPwd());
                                    configMasterOnline.setWifi_mac(wiFi.getMac());
                                    if (Constants.token == null || Constants.token == "") {
                                        ConfigPICOActivity.this.token = Constants.FIX_TOKEN;
                                    } else {
                                        ConfigPICOActivity.this.token = Functions.calcNewToken(savePreferences, ConfigPICOActivity.this.getResources());
                                    }
                                    configMasterOnline.setApi_token(ConfigPICOActivity.this.token);
                                    configMasterOnline.setApi_user(Constants.user);
                                    i = 100;
                                    j = 300000;
                                } else {
                                    configMasterOnline.setConfig_mod(3);
                                    configMasterOnline.setName(ConfigPICOActivity.this.name);
                                    configMasterOnline.setPin(ConfigPICOActivity.this.pin);
                                    configMasterOnline.setIdSlave((long) ConfigPICOActivity.ID_SLAVE);
                                    configMasterOnline.setVerso(ConfigPICOActivity.VERSO);
                                    String substring = wiFi.getSid().substring(5);
                                    configMasterOnline.setWifi_ssid(wiFi.getSid());
                                    configMasterOnline.setWifi_mac(wiFi.getMac());
                                    configMasterOnline.setWifi_sec(1);
                                    configMasterOnline.setWifi_pwd("TS_" + substring);
                                    i = 101;
                                    j = 60000;
                                }
                                String str = null;
                                for (int i2 = 0; i2 < 5; i2++) {
                                    UDPSocket.startListening(true);
                                    str = UDPSocket.sendCMD(configMasterOnline, 15000, (long) ConfigPICOActivity.this.akwait);
                                    UDPSocket.stopListening();
                                    if (str != null) {
                                        break;
                                    }
                                    try {
                                        Thread.sleep(500);
                                    } catch (Exception unused) {
                                    }
                                }
                                if (str == null) {
                                    ConfigPICOActivity.this.error();
                                    return;
                                }
                                try {
                                    JSONObject jSONObject = new JSONObject(str);
                                    if (jSONObject.getInt(Constants.JSON_RES) != 1) {
                                        ConfigPICOActivity.this.error();
                                    } else if (!jSONObject.has("ser")) {
                                        ConfigPICOActivity.this.error();
                                    } else {
                                        ConfigPICOActivity.this.serialToCheck = jSONObject.getString("ser");
                                        ConfigPICOActivity.this.CheckInfoPICO(i, j);
                                    }
                                } catch (Exception unused2) {
                                    ConfigPICOActivity.this.error();
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
                                configMasterOffline.setName(ConfigPICOActivity.this.name);
                                configMasterOffline.setPin(ConfigPICOActivity.this.pin);
                                UDPSocket.startListening();
                                String sendCMD = UDPSocket.sendCMD(configMasterOffline, (long) ConfigPICOActivity.this.akwait, (long) ConfigPICOActivity.this.akwait);
                                if (sendCMD == null) {
                                    ConfigPICOActivity.this.error();
                                }
                                try {
                                    JSONObject jSONObject = new JSONObject(sendCMD);
                                    if (jSONObject.getInt(Constants.JSON_RES) == 1) {
                                        String string = jSONObject.getString(Constants.JSON_SER);
                                        if (string == null || string.isEmpty()) {
                                            ConfigPICOActivity.this.error();
                                        } else {
                                            boolean unused = ConfigPICOActivity.this.setPicoWiFiMode(1);
                                            ConfigPICOActivity.this.disconnectFromWIfi();
                                            ConfigPICOActivity.this.addpico(string, true);
                                        }
                                    } else {
                                        ConfigPICOActivity.this.error();
                                    }
                                } catch (Exception unused2) {
                                    ConfigPICOActivity.this.error();
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

    /* JADX WARNING: Can't wrap try/catch for region: R(7:17|18|19|20|21|22|23) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:22:0x0063 */
    /* JADX WARNING: Removed duplicated region for block: B:15:0x0055  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x00a4  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void CheckInfoPICO(final int r9, long r10) {
        /*
            r8 = this;
            r0 = 0
            r8.okconf = r0
            java.util.Date r1 = new java.util.Date
            r1.<init>()
            it.tecnosystemi.TS.Commands.CmdPICO r2 = new it.tecnosystemi.TS.Commands.CmdPICO
            r2.<init>()
            java.lang.String r3 = "pico_info"
            r2.setCmd(r3)
            java.lang.String r3 = r8.pin
            r2.setPin(r3)
            r3 = -1
            r8.lastwifi_sta = r3
            r8.lastmqtt_sta = r3
            r8.lastpico_st = r3
            r3 = 0
            r8.datediff = r3
            r8.blockCheckPico = r0
        L_0x0024:
            boolean r3 = r8.okconf
            r4 = 1
            if (r3 != 0) goto L_0x0053
            long r5 = r8.datediff
            int r7 = (r5 > r10 ? 1 : (r5 == r10 ? 0 : -1))
            if (r7 >= 0) goto L_0x0053
            boolean r5 = r8.blockCheckPico
            if (r5 != 0) goto L_0x0053
            it.tecnosystemi.TS.Activity.PICO.Config.ConfigPICOActivity$3 r3 = new it.tecnosystemi.TS.Activity.PICO.Config.ConfigPICOActivity$3     // Catch:{ Exception -> 0x003b }
            r3.<init>(r2, r9)     // Catch:{ Exception -> 0x003b }
            r8.connectToWifi(r3, r0, r4)     // Catch:{ Exception -> 0x003b }
        L_0x003b:
            java.util.Date r3 = new java.util.Date
            r3.<init>()
            long r3 = r3.getTime()
            long r5 = r1.getTime()
            long r3 = r3 - r5
            r8.datediff = r3
            r3 = 5000(0x1388, double:2.4703E-320)
            java.lang.Thread.sleep(r3)     // Catch:{ Exception -> 0x0051 }
            goto L_0x0024
        L_0x0051:
            goto L_0x0024
        L_0x0053:
            if (r3 == 0) goto L_0x00a4
            it.tecnosystemi.TS.Commands.UDPSocket.startListening()
            int r9 = r8.mode
            if (r9 != r4) goto L_0x0074
            r9 = 2
            r8.setPicoWiFiMode(r9)     // Catch:{ Exception -> 0x006f }
            it.tecnosystemi.TS.Commands.UDPSocket.stopListening()     // Catch:{ Exception -> 0x0063 }
        L_0x0063:
            r8.unbidNetwork()     // Catch:{ Exception -> 0x006f }
            int r9 = r8.CONFIG_TIMES     // Catch:{ Exception -> 0x006f }
            r8.times = r9     // Catch:{ Exception -> 0x006f }
            r8.checkPicoOnline()     // Catch:{ Exception -> 0x006f }
            goto L_0x0111
        L_0x006f:
            r8.error()
            goto L_0x0111
        L_0x0074:
            r10 = 5
            if (r9 != r10) goto L_0x0111
            r9 = 4
            r8.setPicoWiFiMode(r9)
            it.tecnosystemi.TS.Commands.UDPSocket.stopListening()     // Catch:{ Exception -> 0x007e }
        L_0x007e:
            it.tecnosystemi.TS.Model.Device r9 = new it.tecnosystemi.TS.Model.Device
            r9.<init>()
            int r10 = it.tecnosystemi.TS.Utils.Constants.DEVICE_TYPE_PICO
            r9.setLVDV_Type(r10)
            java.lang.String r10 = r8.serial
            r9.setSerial(r10)
            java.lang.String r10 = r8.serial
            it.tecnosystemi.TS.Activity.BaseActivity r11 = r8.activity
            it.tecnosystemi.TS.Model.Pico.deletePICOfromPref(r10, r11)
            it.tecnosystemi.TS.Activity.BaseActivity r10 = r8.activity
            it.tecnosystemi.TS.Model.Device.deleteDevFromPref(r9, r10)
            it.tecnosystemi.TS.Activity.BaseActivity r9 = r8.activity
            it.tecnosystemi.TS.Activity.PICO.Config.ConfigPICOActivity$4 r10 = new it.tecnosystemi.TS.Activity.PICO.Config.ConfigPICOActivity$4
            r10.<init>()
            r9.runOnUiThread(r10)
            goto L_0x0111
        L_0x00a4:
            int r9 = r8.lastwifi_sta
            if (r9 <= 0) goto L_0x00bb
            r10 = 7
            if (r9 >= r10) goto L_0x00bb
            it.tecnosystemi.TS.Activity.BaseActivity r9 = r8.activity
            android.content.res.Resources r10 = r8.getResources()
            int r11 = it.tecnosystemi.TS.R.string.c5_lblConfigKo_wifiProblem
            java.lang.String r10 = r10.getString(r11)
            it.tecnosystemi.TS.Utils.Functions.makeErrorToast(r9, r10)
            goto L_0x0109
        L_0x00bb:
            if (r9 > 0) goto L_0x00d4
            int r9 = r8.lastpico_st
            if (r9 > 0) goto L_0x00d4
            int r9 = r8.lastmqtt_sta
            if (r9 <= 0) goto L_0x00c6
            goto L_0x00d4
        L_0x00c6:
            android.content.res.Resources r9 = r8.getResources()
            int r10 = it.tecnosystemi.TS.R.string.not_errcom
            java.lang.String r9 = r9.getString(r10)
            r8.showErrConf(r9)
            goto L_0x0109
        L_0x00d4:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            android.content.res.Resources r10 = r8.getResources()
            int r11 = it.tecnosystemi.TS.R.string.c5_lblConfigKo_errorCode
            java.lang.String r10 = r10.getString(r11)
            r9.append(r10)
            java.lang.String r10 = "\n"
            r9.append(r10)
            int r10 = r8.lastwifi_sta
            r9.append(r10)
            java.lang.String r10 = "-"
            r9.append(r10)
            int r11 = r8.lastmqtt_sta
            r9.append(r11)
            r9.append(r10)
            int r10 = r8.lastpico_st
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            r8.showErrConf(r9)
        L_0x0109:
            it.tecnosystemi.TS.Activity.PICO.Config.ConfigPICOActivity$5 r9 = new it.tecnosystemi.TS.Activity.PICO.Config.ConfigPICOActivity$5
            r9.<init>()
            r8.runOnUiThread(r9)
        L_0x0111:
            it.tecnosystemi.TS.Commands.UDPSocket.stopListening()     // Catch:{ Exception -> 0x0114 }
        L_0x0114:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.PICO.Config.ConfigPICOActivity.CheckInfoPICO(int, long):void");
    }

    /* access modifiers changed from: private */
    public boolean setPicoWiFiMode(int i) {
        try {
            CmdPICO.PicoAP picoAP = new CmdPICO.PicoAP();
            picoAP.setAp_m(i);
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
                        ConfigPICOActivity.this.dismissdialog();
                    }
                };
                ConfigPICOActivity configPICOActivity = ConfigPICOActivity.this;
                ConfigPICOActivity.this.openDialogFragment(configPICOActivity.createYesNoPopUp("", str, "", configPICOActivity.getResources().getString(R.string.general_OK), r5, r6));
            }
        });
    }

    private void checkPicoOnline() {
        showProgress();
        int i = this.times;
        if (i > 0) {
            this.times = i - 1;
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            Device_OP device_OP = new Device_OP();
                            device_OP.setPIN(ConfigPICOActivity.this.pin);
                            device_OP.setSerial(ConfigPICOActivity.this.serialToCheck);
                            device_OP.setPlantId(TSDeviceListActivity.idSelected);
                            device_OP.setToken(ConfigPICOActivity.this.activity.FirebaseToken);
                            device_OP.setPlatform(Constants.NOTIFIC_PLAT);
                            device_OP.setPico(true);
                            device_OP.setPhoneDate(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date()));
                            Gson gson = new Gson();
                            new ThreadWebService(ConfigPICOActivity.this.activity, 1, 29, ConfigPICOActivity.this.getResources().getString(R.string.uriWebService_PICO) + ConfigPICOActivity.this.getResources().getString(R.string.uri_AddUserToDevice), gson.toJson((Object) device_OP), (String[]) null).start();
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
                    addpico(this.serialToCheck, false);
                    return;
                }
            } catch (Exception unused) {
            }
        }
        checkPicoOnline();
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0077  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x008f  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void addDevToPlant(java.lang.String r7) {
        /*
            r6 = this;
            it.tecnosystemi.TS.Model.Device r0 = new it.tecnosystemi.TS.Model.Device
            r0.<init>()
            int r1 = it.tecnosystemi.TS.Utils.Constants.DEVICE_TYPE_PICO
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
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.PICO.Config.ConfigPICOActivity.addDevToPlant(java.lang.String):void");
    }

    public void addpico(String str, boolean z) {
        Device device = new Device();
        device.setSerial(str);
        device.setLVDV_Type(Constants.DEVICE_TYPE_PICO);
        Pico.deletePICOfromPref(str, this.activity);
        Pico.savePicoInPref(this.name, str, this.pin, "", this.activity, z, true);
        addDevToPlant(str);
        hideProgress();
        this.activity.runOnUiThread(new Runnable() {
            public void run() {
                ConfigPICOActivity.this.enableView();
                ConfigPICOActivity.this.lblDesrc.setText(ConfigPICOActivity.this.getResources().getString(R.string.c5_deviceAdd));
                ConfigPICOActivity.this.lblDesrc.setTextColor(-1);
            }
        });
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

    private void setUpGui() {
        this.btnFine = (Button) findViewById(R.id.ca_btnFine);
        this.lblDesrc = (TextView) findViewById(R.id.ca_lblDesrc);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
        this.btnFine.setTypeface(createFromAsset);
        this.lblDesrc.setTypeface(createFromAsset);
    }

    public void error() {
        hideProgress();
        runOnUiThread(new Runnable() {
            public void run() {
                Functions.makeErrorToast(ConfigPICOActivity.this.activity, ConfigPICOActivity.this.getResources().getString(R.string.c5_lblConfigKo));
                ConfigPICOActivity.this.lblDesrc.setText(ConfigPICOActivity.this.getResources().getString(R.string.c5_lblConfigKo));
                ConfigPICOActivity.this.lblDesrc.setTextColor(ConfigPICOActivity.this.getResources().getColor(R.color.colorerror));
                ConfigPICOActivity.this.enableView();
            }
        });
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

    public void disableView() {
        this.btnFine.setEnabled(false);
    }

    public void enableView() {
        this.btnFine.setEnabled(true);
    }

    public View getToolBar() {
        return findViewById(R.id.ca_toolbar);
    }

    public String setToolbarTitle() {
        return getResources().getString(R.string.c5_title);
    }
}
