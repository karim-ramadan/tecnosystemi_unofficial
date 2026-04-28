package it.tecnosystemi.TS.Activity.Config;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.gson.Gson;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity;
import it.tecnosystemi.TS.Commands.MySocket;
import it.tecnosystemi.TS.Model.ControlUnit;
import it.tecnosystemi.TS.Model.Device_OP;
import it.tecnosystemi.TS.Model.WiFi;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Threads.ThreadWebService;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.json.JSONObject;

public class ConfigActivity extends BaseActivity {
    public int CONFIG_TIMES = 40;
    BaseActivity activity;
    String addnewurl;
    Button btnFine;
    Thread checkcentralina;
    String checkurl;
    JSONObject cmd;
    boolean connecttedConf;
    boolean continueThread = true;
    int icon;
    TextView lblDesrc;
    int maxtryrecc = 10;
    int mode;
    String name;
    String payload;
    String pin;
    String serial;
    String ssid;
    boolean stop;
    int timeout = 30000;
    public int times = 40;
    String token;
    int tryrecc;
    WiFi wifi;

    public BaseActivity getActivity() {
        return this;
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        return list;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_config);
        super.onCreate(bundle);
        hideMenuButton();
        setUpGui();
        this.activity = this;
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
            this.icon = getIntent().getIntExtra(Constants.INTENT_ICON, 0);
            this.checkurl = getResources().getString(R.string.uriWebService) + getResources().getString(R.string.uri_CheckConfigCu);
            this.addnewurl = getResources().getString(R.string.uriWebService) + getResources().getString(R.string.uri_AddNewCu);
            this.tryrecc = 0;
            sendconfig();
            return;
        }
        this.btnFine.setEnabled(true);
    }

    public void sendconfig() {
        connectToWifi(new Runnable() {
            public void run() {
                ConfigActivity.this.sendconfig_();
            }
        }, false, true);
    }

    public void sendconfig_() {
        if (this.mode == 1) {
            this.wifi = (WiFi) getIntent().getSerializableExtra(Constants.INTENT_WIFI);
            try {
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            String configCu = MySocket.configCu(ConfigActivity.this.pin, false, ConfigActivity.this.wifi, Constants.ip, Constants.port);
                            if (ConfigActivity.this.tryrecc < ConfigActivity.this.maxtryrecc) {
                                if (configCu == null) {
                                    ConfigActivity.this.reconnect();
                                    return;
                                } else if (configCu.isEmpty()) {
                                    ConfigActivity.this.reconnect();
                                    return;
                                }
                            }
                            Log.d("CONFIGCU", configCu);
                            JSONObject jSONObject = new JSONObject(configCu);
                            if (jSONObject.getInt(Constants.JSON_RES) == 1) {
                                ConfigActivity.this.serial = jSONObject.getString(Constants.JSON_SER);
                                if (ConfigActivity.this.serial == null || ConfigActivity.this.serial.isEmpty()) {
                                    ConfigActivity.this.error();
                                    return;
                                }
                                Device_OP device_OP = new Device_OP();
                                device_OP.setSerial(ConfigActivity.this.serial);
                                device_OP.setPico(false);
                                device_OP.setPlantId(TSDeviceListActivity.idSelected);
                                device_OP.setPIN(ConfigActivity.this.pin);
                                device_OP.setToken(ConfigActivity.this.activity.FirebaseToken);
                                device_OP.setPlatform(Constants.NOTIFIC_PLAT);
                                Gson gson = new Gson();
                                ConfigActivity.this.payload = gson.toJson((Object) device_OP);
                                ConfigActivity.this.cmd = new JSONObject("{}");
                                ConfigActivity.this.cmd.put(Constants.JSON_CU_PIN, ConfigActivity.this.pin);
                                ConfigActivity.this.cmd.put(Constants.JSON_CU_NAME, ConfigActivity.this.name);
                                ConfigActivity.this.cmd.put(Constants.JSON_CU_ICON, ConfigActivity.this.icon);
                                ConfigActivity.this.cmd.put(Constants.JSON_CU_SERIAL, ConfigActivity.this.serial);
                                ConfigActivity.this.cmd.put("PhoneDate", new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(new Date()));
                                ConfigActivity.this.cmd.put("cmd", "");
                                ConfigActivity.this.unbidNetwork();
                                ConfigActivity.this.tryNewCU();
                                return;
                            }
                            ConfigActivity.this.error();
                        } catch (Exception unused) {
                            ConfigActivity.this.error();
                        }
                    }
                }).start();
            } catch (Exception unused) {
            }
        } else {
            new Thread(new Runnable() {
                public void run() {
                    String configCu = MySocket.configCu(ConfigActivity.this.pin, true, (WiFi) null, Constants.ip, Constants.port);
                    if (ConfigActivity.this.tryrecc < ConfigActivity.this.maxtryrecc) {
                        if (configCu == null) {
                            ConfigActivity.this.reconnect();
                            return;
                        } else if (configCu.isEmpty()) {
                            ConfigActivity.this.reconnect();
                            return;
                        }
                    }
                    if (configCu == null) {
                        ConfigActivity.this.error();
                    }
                    try {
                        JSONObject jSONObject = new JSONObject(configCu);
                        if (jSONObject.getInt(Constants.JSON_RES) == 1) {
                            String string = jSONObject.getString(Constants.JSON_SER);
                            if (string == null || string.isEmpty()) {
                                ConfigActivity.this.error();
                            } else {
                                ConfigActivity.this.addcentralina(string, true);
                            }
                        } else {
                            ConfigActivity.this.error();
                        }
                    } catch (Exception unused) {
                        ConfigActivity.this.error();
                    }
                }
            }).start();
        }
    }

    /* access modifiers changed from: private */
    public void reconnect() {
        this.tryrecc++;
        disconnectFromWIfi();
        this.connecttedConf = false;
        AnonymousClass4 r2 = new Runnable() {
            public void run() {
                ConfigActivity.this.connecttedConf = true;
                ConfigActivity.this.sendconfig();
            }
        };
        try {
            Thread.sleep(1000);
        } catch (Exception unused) {
        }
        connectToWifi(r2, false, true);
    }

    public void checkCentralina() {
        if (!this.continueThread) {
            this.continueThread = true;
            return;
        }
        int i = this.times;
        if (i > 0) {
            this.times = i - 1;
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            new ThreadWebService(ConfigActivity.this.activity, 1, 18, ConfigActivity.this.checkurl, ConfigActivity.this.payload, (String[]) null).start();
                        }
                    }, 5000);
                }
            });
            return;
        }
        error();
    }

    public void tryNewCU() {
        showProgress();
        int i = this.times;
        if (i > 0) {
            this.times = i - 1;
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            new ThreadWebService(ConfigActivity.this.activity, 1, 17, ConfigActivity.this.addnewurl, ConfigActivity.this.cmd.toString(), (String[]) null).start();
                        }
                    }, 5000);
                }
            });
            return;
        }
        error();
    }

    public void addCentralinaOnline() {
        ControlUnit.deleteCufromPref(this.serial, this.activity);
        addDevToPlant(this.serial);
        this.cu = new ControlUnit();
        this.cu.setName(this.name);
        this.cu.setPin(this.pin);
        this.cu.setIcontype(this.icon);
        this.cu.setOffline(false);
        this.cu.setSerial(this.serial);
        ControlUnit.saveCuInPref(this.cu, this);
        hideProgress();
        this.activity.runOnUiThread(new Runnable() {
            public void run() {
                ConfigActivity.this.enableView();
                ConfigActivity.this.lblDesrc.setText(ConfigActivity.this.getResources().getString(R.string.c5_lblConfigOk));
                ConfigActivity.this.lblDesrc.setTextColor(-1);
            }
        });
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x008a  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x00a2  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void addDevToPlant(java.lang.String r7) {
        /*
            r6 = this;
            it.tecnosystemi.TS.Model.Device r0 = new it.tecnosystemi.TS.Model.Device
            r0.<init>()
            int r1 = it.tecnosystemi.TS.Utils.Constants.DEVICE_TYPE_PROAIR
            r0.setLVDV_Type(r1)
            r0.setSerial(r7)
            java.lang.String r7 = r6.name
            r0.setName(r7)
            java.text.SimpleDateFormat r7 = new java.text.SimpleDateFormat
            java.lang.String r1 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
            r7.<init>(r1)
            java.util.Date r1 = new java.util.Date
            r1.<init>()
            java.lang.String r7 = r7.format(r1)
            r0.setLastAddTimezone(r7)
            it.tecnosystemi.TS.Activity.BaseActivity r7 = r6.activity
            it.tecnosystemi.TS.Model.Device.deleteDevFromPref(r0, r7)
            long r1 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.idSelected
            it.tecnosystemi.TS.Model.Plant.addDeviceToPlantPref(r0, r1, r6)
            r7 = 0
        L_0x0030:
            java.util.List<it.tecnosystemi.TS.Model.Plant> r1 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            int r1 = r1.size()
            if (r7 >= r1) goto L_0x00b6
            java.util.List<it.tecnosystemi.TS.Model.Plant> r1 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r1 = r1.get(r7)
            it.tecnosystemi.TS.Model.Plant r1 = (it.tecnosystemi.TS.Model.Plant) r1
            long r1 = r1.getLVPL_Id()
            long r3 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.idSelected
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x00b2
            java.util.List<it.tecnosystemi.TS.Model.Plant> r1 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r1 = r1.get(r7)
            it.tecnosystemi.TS.Model.Plant r1 = (it.tecnosystemi.TS.Model.Plant) r1
            java.util.List r1 = r1.getListDevices()
            if (r1 == 0) goto L_0x0077
            java.util.List<it.tecnosystemi.TS.Model.Plant> r1 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r1 = r1.get(r7)
            it.tecnosystemi.TS.Model.Plant r1 = (it.tecnosystemi.TS.Model.Plant) r1
            java.util.List r1 = r1.getListDevices()
            if (r1 == 0) goto L_0x0087
            java.util.List<it.tecnosystemi.TS.Model.Plant> r1 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r1 = r1.get(r7)
            it.tecnosystemi.TS.Model.Plant r1 = (it.tecnosystemi.TS.Model.Plant) r1
            java.util.List r1 = r1.getListDevices()
            int r1 = r1.indexOf(r0)
            goto L_0x0088
        L_0x0077:
            java.util.List<it.tecnosystemi.TS.Model.Plant> r1 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r1 = r1.get(r7)
            it.tecnosystemi.TS.Model.Plant r1 = (it.tecnosystemi.TS.Model.Plant) r1
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r1.setListDevices(r2)
        L_0x0087:
            r1 = -1
        L_0x0088:
            if (r1 < 0) goto L_0x00a2
            java.util.List<it.tecnosystemi.TS.Model.Plant> r0 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r7 = r0.get(r7)
            it.tecnosystemi.TS.Model.Plant r7 = (it.tecnosystemi.TS.Model.Plant) r7
            java.util.List r7 = r7.getListDevices()
            java.lang.Object r7 = r7.get(r1)
            it.tecnosystemi.TS.Model.Device r7 = (it.tecnosystemi.TS.Model.Device) r7
            java.lang.String r0 = r6.name
            r7.setName(r0)
            goto L_0x00b6
        L_0x00a2:
            java.util.List<it.tecnosystemi.TS.Model.Plant> r1 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r7 = r1.get(r7)
            it.tecnosystemi.TS.Model.Plant r7 = (it.tecnosystemi.TS.Model.Plant) r7
            java.util.List r7 = r7.getListDevices()
            r7.add(r0)
            goto L_0x00b6
        L_0x00b2:
            int r7 = r7 + 1
            goto L_0x0030
        L_0x00b6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.Config.ConfigActivity.addDevToPlant(java.lang.String):void");
    }

    public void addcentralina(String str, boolean z) {
        ControlUnit.deleteCufromPref(str, this);
        ControlUnit.saveCuInPref(this.name, str, this.pin, "", this.icon, this.activity, z);
        addDevToPlant(str);
        hideProgress();
        this.activity.runOnUiThread(new Runnable() {
            public void run() {
                ConfigActivity.this.enableView();
                ConfigActivity.this.lblDesrc.setText(ConfigActivity.this.getResources().getString(R.string.c5_lblConfigOk));
                ConfigActivity.this.lblDesrc.setTextColor(-1);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
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
                Functions.makeErrorToast(ConfigActivity.this.activity, ConfigActivity.this.getResources().getString(R.string.c5_lblConfigKo));
                ConfigActivity.this.lblDesrc.setText(ConfigActivity.this.getResources().getString(R.string.c5_lblConfigKo));
                ConfigActivity.this.lblDesrc.setTextColor(ConfigActivity.this.getResources().getColor(R.color.colorerror));
                ConfigActivity.this.enableView();
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
