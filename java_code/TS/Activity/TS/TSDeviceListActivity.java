package it.tecnosystemi.TS.Activity.TS;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import com.google.gson.Gson;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Activity.ControlUnitActivity;
import it.tecnosystemi.TS.Activity.GDPRActivity;
import it.tecnosystemi.TS.Activity.PICO.PicoActivity;
import it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity;
import it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity;
import it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity;
import it.tecnosystemi.TS.Activity.SignUpActivity;
import it.tecnosystemi.TS.Activity.VMC.VMCActivity;
import it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity;
import it.tecnosystemi.TS.Adapters.DeviceLVAdapterTS;
import it.tecnosystemi.TS.Commands.MySocketBootLoader;
import it.tecnosystemi.TS.Model.ControlUnit;
import it.tecnosystemi.TS.Model.Device;
import it.tecnosystemi.TS.Model.Device_OP;
import it.tecnosystemi.TS.Model.Pico;
import it.tecnosystemi.TS.Model.Plant;
import it.tecnosystemi.TS.Model.SeiX;
import it.tecnosystemi.TS.Model.VMC;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Threads.ThreadDowloadFirmWare;
import it.tecnosystemi.TS.Threads.ThreadWebService;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.DataClass;
import it.tecnosystemi.TS.Utils.Functions;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TSDeviceListActivity extends BaseActivity {
    public static Device SELECTED_DEV;
    public static long idSelected;
    TSDeviceListActivity activity;
    Button btnAddCu;
    DeviceLVAdapterTS cuAdapter;
    boolean errorcollegamento;
    boolean first_getHome;
    ListView homeListView;
    int indexListPL;
    JSONArray infofws;
    TextView lblSelect;
    SharedPreferences preferences;

    public BaseActivity getActivity() {
        return this;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        try {
            setContentView(R.layout.activity_tsdevice_list);
            this.indexListPL = getIntent().getIntExtra(Constants.INP_INDEX, -1);
            if (Constants.ISDEMO) {
                Constants.DEMO_IMP_INDEX = this.indexListPL;
            }
            this.activity = this;
            this.typeActStyle = 1;
            this.first_getHome = true;
            if (this.indexListPL < 0) {
                this.first_getHome = false;
                int i = 0;
                while (true) {
                    if (i >= Constants.listaImpianti.size()) {
                        break;
                    } else if (Constants.listaImpianti.get(i).getLVPL_Id() == idSelected) {
                        this.indexListPL = i;
                        break;
                    } else {
                        i++;
                    }
                }
            }
            super.onCreate(bundle);
            MySocketBootLoader.lastFWPK = 0;
            this.homeListView = (ListView) findViewById(R.id.ha_listView);
            if (Constants.ISDEMO) {
                hideMenuButton();
            }
            setUpGui();
        } catch (Exception unused) {
            finish();
        }
    }

    public void btnConf(View view) {
        runOnUiThread(new Runnable() {
            public void run() {
                Intent intent = new Intent(TSDeviceListActivity.this.activity, SelectTypeDevActivity.class);
                intent.putExtra(Constants.TS_TIPO_OP, 0);
                TSDeviceListActivity.this.startActivity(intent);
            }
        });
    }

    public View getToolBar() {
        return findViewById(R.id.ha_toolbar);
    }

    public void getInfoFw() {
        try {
            if (this.preferences == null) {
                this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
            }
            this.infofws = new JSONArray(this.preferences.getString(Constants.PREF_INFOFWS, "[]"));
            int i = 0;
            while (i < this.infofws.length()) {
                try {
                    if (!this.infofws.getJSONObject(i).getString(Constants.JSON_VERSION).equals(Constants.LastFWVr)) {
                        i++;
                    } else {
                        return;
                    }
                } catch (Exception unused) {
                }
            }
        } catch (Exception unused2) {
        }
    }

    public void createDeleteUserPopUp() {
        AnonymousClass2 r5 = new Runnable() {
            public void run() {
                TSDeviceListActivity.this.dismissdialog();
            }
        };
        AnonymousClass3 r6 = new Runnable() {
            public void run() {
                TSDeviceListActivity.this.dismissdialog();
                TSDeviceListActivity.this.showProgress();
                TSDeviceListActivity tSDeviceListActivity = TSDeviceListActivity.this.activity;
                new ThreadWebService(tSDeviceListActivity, 2, 20, TSDeviceListActivity.this.getResources().getString(R.string.uriWebService) + TSDeviceListActivity.this.getResources().getString(R.string.uri_DeleteUser), (String) null, (String[]) null).start();
            }
        };
        String string = getResources().getString(R.string.cu_deleteAccountAlert_text);
        openDialogFragment(createYesNoPopUp(getResources().getString(R.string.cu_deleteAccountAlert_title), string, getResources().getString(R.string.ba_cancel), getResources().getString(R.string.general_OK), r5, r6));
    }

    public void cancellaDevice(final Device device) {
        refreshlist(false);
        AnonymousClass4 r7 = new Runnable() {
            /* JADX WARNING: Code restructure failed: missing block: B:11:0x0073, code lost:
                if (r0.getOffline().booleanValue() != false) goto L_0x0155;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:17:0x00af, code lost:
                if (r0.getOffline().booleanValue() != false) goto L_0x0155;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:23:0x00eb, code lost:
                if (r0.getOffline().booleanValue() != false) goto L_0x0155;
             */
            /* JADX WARNING: Code restructure failed: missing block: B:5:0x0037, code lost:
                if (r0.isOffline() != false) goto L_0x0155;
             */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r10 = this;
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r0 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.this
                    r0.dismissdialog()
                    it.tecnosystemi.TS.Model.Device r0 = r9
                    int r0 = r0.getLVDV_Type()
                    int r1 = it.tecnosystemi.TS.Utils.Constants.DEVICE_TYPE_PROAIR
                    if (r0 != r1) goto L_0x003b
                    it.tecnosystemi.TS.Model.Device r0 = r9
                    java.lang.String r0 = r0.getSerial()
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r1 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.this
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r1 = r1.activity
                    it.tecnosystemi.TS.Model.ControlUnit r0 = it.tecnosystemi.TS.Model.ControlUnit.getCuFromPref(r0, r1)
                    it.tecnosystemi.TS.Model.Device r1 = r9
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r2 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.this
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r2 = r2.activity
                    it.tecnosystemi.TS.Model.Device.deleteDevFromPref(r1, r2)
                    java.lang.String r1 = r0.getSerial()
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r2 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.this
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r2 = r2.activity
                    it.tecnosystemi.TS.Model.ControlUnit.deleteCufromPref(r1, r2)
                    if (r0 == 0) goto L_0x00ee
                    boolean r0 = r0.isOffline()
                    if (r0 == 0) goto L_0x00ee
                    goto L_0x0155
                L_0x003b:
                    it.tecnosystemi.TS.Model.Device r0 = r9
                    int r0 = r0.getLVDV_Type()
                    int r1 = it.tecnosystemi.TS.Utils.Constants.DEVICE_TYPE_PICO
                    if (r0 != r1) goto L_0x0077
                    it.tecnosystemi.TS.Model.Device r0 = r9
                    java.lang.String r0 = r0.getSerial()
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r1 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.this
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r1 = r1.activity
                    it.tecnosystemi.TS.Model.Pico r0 = it.tecnosystemi.TS.Model.Pico.getPICOFromPref(r0, r1)
                    it.tecnosystemi.TS.Model.Device r1 = r9
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r2 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.this
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r2 = r2.activity
                    it.tecnosystemi.TS.Model.Device.deleteDevFromPref(r1, r2)
                    it.tecnosystemi.TS.Model.Device r1 = r9
                    java.lang.String r1 = r1.getSerial()
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r2 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.this
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r2 = r2.activity
                    it.tecnosystemi.TS.Model.Pico.deletePICOfromPref(r1, r2)
                    if (r0 == 0) goto L_0x00ee
                    java.lang.Boolean r0 = r0.getOffline()
                    boolean r0 = r0.booleanValue()
                    if (r0 == 0) goto L_0x00ee
                    goto L_0x0155
                L_0x0077:
                    it.tecnosystemi.TS.Model.Device r0 = r9
                    int r0 = r0.getLVDV_Type()
                    int r1 = it.tecnosystemi.TS.Utils.Constants.DEVICE_TYPE_VMC
                    if (r0 != r1) goto L_0x00b3
                    it.tecnosystemi.TS.Model.Device r0 = r9
                    java.lang.String r0 = r0.getSerial()
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r1 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.this
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r1 = r1.activity
                    it.tecnosystemi.TS.Model.VMC r0 = it.tecnosystemi.TS.Model.VMC.getVMCFromPref(r0, r1)
                    it.tecnosystemi.TS.Model.Device r1 = r9
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r2 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.this
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r2 = r2.activity
                    it.tecnosystemi.TS.Model.Device.deleteDevFromPref(r1, r2)
                    it.tecnosystemi.TS.Model.Device r1 = r9
                    java.lang.String r1 = r1.getSerial()
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r2 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.this
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r2 = r2.activity
                    it.tecnosystemi.TS.Model.VMC.deleteVMCfromPref(r1, r2)
                    if (r0 == 0) goto L_0x00ee
                    java.lang.Boolean r0 = r0.getOffline()
                    boolean r0 = r0.booleanValue()
                    if (r0 == 0) goto L_0x00ee
                    goto L_0x0155
                L_0x00b3:
                    it.tecnosystemi.TS.Model.Device r0 = r9
                    int r0 = r0.getLVDV_Type()
                    int r1 = it.tecnosystemi.TS.Utils.Constants.DEVICE_TYPE_6X
                    if (r0 != r1) goto L_0x0155
                    it.tecnosystemi.TS.Model.Device r0 = r9
                    java.lang.String r0 = r0.getSerial()
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r1 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.this
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r1 = r1.activity
                    it.tecnosystemi.TS.Model.SeiX r0 = it.tecnosystemi.TS.Model.SeiX.get6XFromPref(r0, r1)
                    it.tecnosystemi.TS.Model.Device r1 = r9
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r2 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.this
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r2 = r2.activity
                    it.tecnosystemi.TS.Model.Device.deleteDevFromPref(r1, r2)
                    it.tecnosystemi.TS.Model.Device r1 = r9
                    java.lang.String r1 = r1.getSerial()
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r2 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.this
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r2 = r2.activity
                    it.tecnosystemi.TS.Model.VMC.deleteVMCfromPref(r1, r2)
                    if (r0 == 0) goto L_0x00ee
                    java.lang.Boolean r0 = r0.getOffline()
                    boolean r0 = r0.booleanValue()
                    if (r0 == 0) goto L_0x00ee
                    goto L_0x0155
                L_0x00ee:
                    java.lang.StringBuilder r0 = new java.lang.StringBuilder
                    r0.<init>()
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r1 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.this
                    android.content.res.Resources r1 = r1.getResources()
                    int r2 = it.tecnosystemi.TS.R.string.uriWebService
                    java.lang.String r1 = r1.getString(r2)
                    r0.append(r1)
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r1 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.this
                    android.content.res.Resources r1 = r1.getResources()
                    int r2 = it.tecnosystemi.TS.R.string.uri_DeleteDevice
                    java.lang.String r1 = r1.getString(r2)
                    r0.append(r1)
                    java.lang.String r6 = r0.toString()
                    it.tecnosystemi.TS.Model.Device_OP$DeviceOp r0 = new it.tecnosystemi.TS.Model.Device_OP$DeviceOp
                    r0.<init>()
                    it.tecnosystemi.TS.Model.Device r1 = r9
                    long r1 = r1.getLVDV_Id()
                    r0.setDeviceID(r1)
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r1 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.this
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r1 = r1.activity
                    java.lang.String r1 = r1.FirebaseToken
                    r0.setToken(r1)
                    java.lang.String r1 = "fcm2"
                    r0.setPlatform(r1)
                    com.google.gson.Gson r1 = new com.google.gson.Gson
                    r1.<init>()
                    it.tecnosystemi.TS.Model.Device r2 = r9
                    java.lang.String r2 = r2.getSerial()
                    java.lang.String[] r8 = new java.lang.String[]{r2}
                    it.tecnosystemi.TS.Threads.ThreadWebService r9 = new it.tecnosystemi.TS.Threads.ThreadWebService
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r2 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.this
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r3 = r2.activity
                    r5 = 10
                    java.lang.String r7 = r1.toJson((java.lang.Object) r0)
                    r4 = 2
                    r2 = r9
                    r2.<init>(r3, r4, r5, r6, r7, r8)
                    r9.start()
                    goto L_0x01b0
                L_0x0155:
                    r0 = 0
                    r1 = 0
                L_0x0157:
                    java.util.List<it.tecnosystemi.TS.Model.Plant> r2 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r3 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.this
                    int r3 = r3.indexListPL
                    java.lang.Object r2 = r2.get(r3)
                    it.tecnosystemi.TS.Model.Plant r2 = (it.tecnosystemi.TS.Model.Plant) r2
                    java.util.List r2 = r2.getListDevices()
                    int r2 = r2.size()
                    if (r1 >= r2) goto L_0x0197
                    java.util.List<it.tecnosystemi.TS.Model.Plant> r2 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r3 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.this
                    int r3 = r3.indexListPL
                    java.lang.Object r2 = r2.get(r3)
                    it.tecnosystemi.TS.Model.Plant r2 = (it.tecnosystemi.TS.Model.Plant) r2
                    java.util.List r2 = r2.getListDevices()
                    java.lang.Object r2 = r2.get(r1)
                    it.tecnosystemi.TS.Model.Device r2 = (it.tecnosystemi.TS.Model.Device) r2
                    java.lang.String r2 = r2.getSerial()
                    it.tecnosystemi.TS.Model.Device r3 = r9
                    java.lang.String r3 = r3.getSerial()
                    boolean r2 = r2.equals(r3)
                    if (r2 == 0) goto L_0x0194
                    goto L_0x0198
                L_0x0194:
                    int r1 = r1 + 1
                    goto L_0x0157
                L_0x0197:
                    r1 = -1
                L_0x0198:
                    java.util.List<it.tecnosystemi.TS.Model.Plant> r2 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r3 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.this
                    int r3 = r3.indexListPL
                    java.lang.Object r2 = r2.get(r3)
                    it.tecnosystemi.TS.Model.Plant r2 = (it.tecnosystemi.TS.Model.Plant) r2
                    java.util.List r2 = r2.getListDevices()
                    r2.remove(r1)
                    it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity r1 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.this
                    r1.refreshlist(r0)
                L_0x01b0:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.AnonymousClass4.run():void");
            }
        };
        openDialogFragment(createYesNoPopUp(getResources().getString(R.string.hd_deleteDEVAlert_title), getResources().getString(R.string.hd_deleteDEVAlert_text), getResources().getString(R.string.no), getResources().getString(R.string.yes), new Runnable() {
            public void run() {
                TSDeviceListActivity.this.dismissdialog();
            }
        }, r7));
    }

    public void cancellapico(Device device) {
        Pico pICOFromPref = Pico.getPICOFromPref(device.getSerial(), this.activity);
        Device.deleteDevFromPref(device, this.activity);
        if (pICOFromPref == null || !pICOFromPref.getOffline().booleanValue()) {
            Device_OP device_OP = new Device_OP();
            device_OP.setPico(true);
            device_OP.setSerial(device.getSerial());
            device_OP.setToken(this.activity.FirebaseToken);
            device_OP.setPlatform(Constants.NOTIFIC_PLAT);
            new ThreadWebService(this.activity, 2, 10, getResources().getString(R.string.uriWebService) + getResources().getString(R.string.uri_DeleteDevice), new Gson().toJson((Object) device_OP), new String[]{device.getSerial()}).start();
            return;
        }
        Pico.deletePICOfromPref(device.getSerial(), this.activity);
        this.activity.runOnUiThread(new Runnable() {
            public void run() {
                TSDeviceListActivity.this.loadHome();
            }
        });
    }

    public void cancellacentralina(final Device device) {
        AnonymousClass7 r6 = new Runnable() {
            public void run() {
                TSDeviceListActivity.this.dismissdialog();
                ControlUnit cuFromPref = ControlUnit.getCuFromPref(device.getSerial(), TSDeviceListActivity.this.activity);
                Device.deleteDevFromPref(device, TSDeviceListActivity.this.activity);
                if (cuFromPref == null || !cuFromPref.isOffline()) {
                    Device_OP.DeviceOp deviceOp = new Device_OP.DeviceOp();
                    deviceOp.setDeviceID(device.getDevId());
                    deviceOp.setToken(TSDeviceListActivity.this.activity.FirebaseToken);
                    deviceOp.setPlatform(Constants.NOTIFIC_PLAT);
                    new ThreadWebService(TSDeviceListActivity.this.activity, 2, 10, TSDeviceListActivity.this.getResources().getString(R.string.uriWebService) + TSDeviceListActivity.this.getResources().getString(R.string.uri_DeleteDevice), new Gson().toJson((Object) deviceOp), new String[]{device.getSerial()}).start();
                    return;
                }
                ControlUnit.deleteCufromPref(cuFromPref.getSerial(), TSDeviceListActivity.this.activity);
                TSDeviceListActivity.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        TSDeviceListActivity.this.loadHome();
                    }
                });
            }
        };
        AnonymousClass8 r5 = new Runnable() {
            public void run() {
                TSDeviceListActivity.this.dismissdialog();
            }
        };
        openDialogFragment(createYesNoPopUp(getResources().getString(R.string.cu_deleteCUAlert_title), getResources().getString(R.string.cu_deleteCUAlert_text), getResources().getString(R.string.no), getResources().getString(R.string.yes), r5, r6));
    }

    public void connectDevice(Device device) {
        if (Constants.ISDEMO) {
            int i = 0;
            while (true) {
                if (i >= Constants.listaImpianti.get(this.indexListPL).getListDevices().size()) {
                    break;
                } else if (Constants.listaImpianti.get(this.indexListPL).getListDevices().get(i).getSerial().equals(device.getSerial())) {
                    Constants.DEMO_DEV_INDEX = i;
                    break;
                } else {
                    i++;
                }
            }
        }
        SELECTED_DEV = device;
        if (device.getLVDV_Type() == Constants.DEVICE_TYPE_PROAIR) {
            startCUActivity(device);
        } else if (device.getLVDV_Type() == Constants.DEVICE_TYPE_PICO) {
            startPICOActivity(device);
        } else if (device.getLVDV_Type() == Constants.DEVICE_TYPE_VMC) {
            startVMCActivity(device);
        } else if (device.getLVDV_Type() == Constants.DEVICE_TYPE_6X) {
            start6XActivity(device);
        }
    }

    public void startCUActivity(Device device) {
        ControlUnit cuFromPref = ControlUnit.getCuFromPref(device.getSerial(), this.activity);
        if (cuFromPref == null) {
            cuFromPref = new ControlUnit();
            cuFromPref.setOffline(false);
        }
        cuFromPref.setSerial(device.getSerial());
        cuFromPref.setName(device.getName());
        Intent intent = new Intent(this, ControlUnitActivity.class);
        intent.putExtra(Constants.INTENT_CU, cuFromPref);
        startActivity(intent);
    }

    public void startPICOActivity(Device device) {
        Pico pICOFromPref = Pico.getPICOFromPref(device.getSerial(), this.activity);
        int i = 0;
        if (pICOFromPref == null) {
            pICOFromPref = new Pico();
            pICOFromPref.setOffline(false);
        }
        pICOFromPref.setName(device.getName());
        pICOFromPref.setSerial(device.getSerial());
        if (Constants.ISDEMO) {
            while (true) {
                if (i >= DataClass.getInstance(this).pico_list.size()) {
                    break;
                } else if (DataClass.getInstance(this).pico_list.get(i).getSerial().equals(device.getSerial())) {
                    Constants.DEMO_PICO_INDEX = i;
                    break;
                } else {
                    i++;
                }
            }
        }
        Intent intent = new Intent(this, PicoActivity.class);
        intent.addFlags(67108864);
        intent.putExtra(Constants.INTENT_PICO, pICOFromPref);
        startActivity(intent);
    }

    public void startVMCActivity(Device device) {
        VMC vMCFromPref = VMC.getVMCFromPref(device.getSerial(), this.activity);
        if (vMCFromPref == null) {
            vMCFromPref = new VMC();
            vMCFromPref.setOffline(false);
        }
        vMCFromPref.setName(device.getName());
        vMCFromPref.setSerial(device.getSerial());
        Intent intent = new Intent(this, VMCActivity.class);
        VMCActivity.vmc = vMCFromPref;
        intent.addFlags(67108864);
        startActivity(intent);
    }

    public void start6XActivity(Device device) {
        SeiX seiX = SeiX.get6XFromPref(device.getSerial(), this.activity);
        if (seiX == null) {
            seiX = new SeiX();
            seiX.setOffline(false);
        }
        seiX.setName(device.getName());
        seiX.setSerial(device.getSerial());
        Intent intent = new Intent(this, SeiXMainActivity.class);
        SeiXMainActivity.cu6x = seiX;
        intent.addFlags(67108864);
        startActivity(intent);
    }

    private void setUpGui() {
        this.btnAddCu = (Button) findViewById(R.id.ha_btnAggCentralina);
        this.lblSelect = (TextView) findViewById(R.id.ha_txtSeleziona);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
        this.btnAddCu.setTypeface(createFromAsset);
        this.lblSelect.setTypeface(createFromAsset);
        if (Constants.listaImpianti.get(this.indexListPL).getListDevices() != null && Constants.listaImpianti.get(this.indexListPL).getListDevices().size() > 0) {
            DeviceLVAdapterTS deviceLVAdapterTS = new DeviceLVAdapterTS(this.activity, Constants.listaImpianti.get(this.indexListPL).getListDevices(), false);
            this.cuAdapter = deviceLVAdapterTS;
            this.homeListView.setAdapter(deviceLVAdapterTS);
        }
    }

    public void refreshlist(boolean z) {
        refreshlist(z, true);
    }

    public void refreshlist(final boolean z, boolean z2) {
        this.indexListPL = -1;
        if (Constants.listaImpianti != null) {
            int i = 0;
            while (true) {
                if (i >= Constants.listaImpianti.size()) {
                    break;
                } else if (Constants.listaImpianti.get(i).getLVPL_Id() == idSelected) {
                    this.indexListPL = i;
                    break;
                } else {
                    i++;
                }
            }
            if (this.indexListPL >= 0) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        TSDeviceListActivity.this.changeTitle(Constants.listaImpianti.get(TSDeviceListActivity.this.indexListPL).getLVPL_Name());
                        TSDeviceListActivity.this.changeIconType(Constants.ICON_TYPE[Constants.listaImpianti.get(TSDeviceListActivity.this.indexListPL).getLVPL_Icon()]);
                        if (Constants.listaImpianti.get(TSDeviceListActivity.this.indexListPL).getListDevices() == null) {
                            Constants.listaImpianti.get(TSDeviceListActivity.this.indexListPL).setListDevices(new ArrayList());
                        }
                        TSDeviceListActivity.this.lblSelect.setText(TSDeviceListActivity.this.getResources().getString(R.string.da_lblSelectDevice));
                        if (TSDeviceListActivity.this.cuAdapter != null) {
                            TSDeviceListActivity.this.cuAdapter.changeCancella(z);
                            TSDeviceListActivity.this.cuAdapter.changeDataSet(Constants.listaImpianti.get(TSDeviceListActivity.this.indexListPL).getListDevices());
                        }
                        if (Constants.listaImpianti.get(TSDeviceListActivity.this.indexListPL).getListDevices().size() == 0) {
                            TSDeviceListActivity.this.lblSelect.setText(TSDeviceListActivity.this.getResources().getString(R.string.da_lblSelectNoDevice));
                        }
                        TSDeviceListActivity.this.updatemenu();
                    }
                });
                return;
            }
        }
        if (z2) {
            runOnUiThread(new Runnable() {
                public void run() {
                    TSDeviceListActivity.this.finish();
                }
            });
        }
    }

    public void loadHome() {
        showProgress();
        new ThreadWebService(this.activity, 0, 23, getResources().getString(R.string.uriWebService) + getResources().getString(R.string.uri_GetPlants), "", (String[]) null).start();
    }

    public void onResume() {
        super.onResume();
        if (Constants.ISDEMO) {
            refreshlist(false);
            return;
        }
        try {
            unbidNetwork();
        } catch (Exception unused) {
        }
        this.background = false;
        if (this.errorcollegamento) {
            this.errorcollegamento = false;
            Functions.makeErrorToast(this, getResources().getString(R.string.ba_apAssente));
        }
        if (this.first_getHome) {
            this.first_getHome = false;
            refreshlist(false);
        } else {
            try {
                refreshlist(false, false);
            } catch (Exception unused2) {
            }
            loadHome();
        }
        this.background = false;
        getInfoFw();
        if (!Constants.CHECKED_TS_VER) {
            Constants.CHECKED_TS_VER = true;
            try {
                String str = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                if (Constants.LastAppVrTxt != null && !Constants.LastAppVrTxt.isEmpty() && Functions.compareVerString(str, Constants.LastAppVr) < 0) {
                    shownewappinfo();
                }
            } catch (Exception unused3) {
            }
        }
    }

    public void shownewappinfo() {
        AnonymousClass11 r6 = new Runnable() {
            public void run() {
                TSDeviceListActivity.this.dismissdialog();
            }
        };
        openDialogFragment(createYesNoPopUp(getResources().getString(R.string.la_new_app), Constants.LastAppVrTxt, "", getResources().getString(R.string.ba_OK), r6, r6));
    }

    public void cancellaplant(final Plant plant) {
        refreshlist(false);
        AnonymousClass12 r7 = new Runnable() {
            public void run() {
                TSDeviceListActivity.this.dismissdialog();
                String str = TSDeviceListActivity.this.getResources().getString(R.string.uriWebService) + TSDeviceListActivity.this.getResources().getString(R.string.uri_DeletePlant);
                try {
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("plantID", plant.getLVPL_Id());
                    jSONObject.put(Constants.PREF_TOKEN, TSDeviceListActivity.this.activity.FirebaseToken);
                    jSONObject.put("platform", Constants.NOTIFIC_PLAT);
                    new ThreadWebService(TSDeviceListActivity.this.activity, 2, 25, str, jSONObject.toString(), (String[]) null).start();
                } catch (Exception unused) {
                }
            }
        };
        openDialogFragment(createYesNoPopUp(getResources().getString(R.string.ha_deleteImpiantoAlert_title), getResources().getString(R.string.ha_deleteImpiantoAlert_text), getResources().getString(R.string.no), getResources().getString(R.string.yes), new Runnable() {
            public void run() {
                TSDeviceListActivity.this.dismissdialog();
            }
        }, r7));
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        JSONArray jSONArray;
        getInfoFw();
        if (!Constants.ISDEMO) {
            list.add(createMenuItem(true, getResources().getString(R.string.ha_menuVerificaFW), "", "", new Runnable() {
                public void run() {
                    try {
                        TSDeviceListActivity.this.dismissdialog();
                    } catch (Exception unused) {
                    }
                    TSDeviceListActivity.this.gotobooloader = false;
                    TSDeviceListActivity tSDeviceListActivity = TSDeviceListActivity.this.activity;
                    new ThreadDowloadFirmWare(tSDeviceListActivity, TSDeviceListActivity.this.getResources().getString(R.string.uriWebService) + TSDeviceListActivity.this.getResources().getString(R.string.uri_infoFrameWork)).start();
                }
            }, false, false));
            AnonymousClass15 r7 = new Runnable() {
                public void run() {
                    try {
                        TSDeviceListActivity.this.dismissdialog();
                    } catch (Exception unused) {
                    }
                    TSDeviceListActivity.this.gotobooloader = true;
                    PicoBootloaderActivity.CLASSTOCALL = TSDeviceListActivity.class;
                    VMCBootloaderActivity.CLASSTOCALL = TSDeviceListActivity.class;
                    SeiXBootloaderActivity.CLASSTOCALL = TSDeviceListActivity.class;
                    TSDeviceListActivity tSDeviceListActivity = TSDeviceListActivity.this.activity;
                    new ThreadDowloadFirmWare(tSDeviceListActivity, TSDeviceListActivity.this.getResources().getString(R.string.uriWebService) + TSDeviceListActivity.this.getResources().getString(R.string.uri_infoFrameWork)).start();
                }
            };
            try {
                jSONArray = new JSONArray(PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.PREF_INFOFWS, "[]"));
            } catch (JSONException e) {
                try {
                    jSONArray = new JSONArray("[]");
                } catch (JSONException e2) {
                    e2.printStackTrace();
                    jSONArray = null;
                }
                e.printStackTrace();
            }
            if (jSONArray.length() > 0) {
                list.add(createMenuItem(false, getResources().getString(R.string.ha_menuUpdateFW), "", "", r7, false, false));
            }
            list.add(createMenuItem(false, getResources().getString(R.string.hd_nuovoImpianto), "", "", new Runnable() {
                public void run() {
                    if (TSDeviceListActivity.this.myDialogFragment != null) {
                        TSDeviceListActivity.this.dismissdialog();
                    }
                    TSDeviceListActivity.this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            TSDeviceListActivity.this.startActivityForResult(new Intent(TSDeviceListActivity.this.activity, AddUpdPlantActivity.class), 1001);
                        }
                    });
                }
            }, false, false));
            list.add(createMenuItem(false, getResources().getString(R.string.hd_rinominaImpianto), "", "", new Runnable() {
                public void run() {
                    if (TSDeviceListActivity.this.myDialogFragment != null) {
                        TSDeviceListActivity.this.dismissdialog();
                    }
                    TSDeviceListActivity.this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Intent intent = new Intent(TSDeviceListActivity.this.activity, AddUpdPlantActivity.class);
                            intent.putExtra(Constants.INP_INDEX, TSDeviceListActivity.this.indexListPL);
                            TSDeviceListActivity.this.startActivity(intent);
                        }
                    });
                }
            }, false, false));
            AnonymousClass18 r6 = new Runnable() {
                public void run() {
                    TSDeviceListActivity.this.cancellaplant(Constants.listaImpianti.get(TSDeviceListActivity.this.indexListPL));
                }
            };
            if (Constants.listaImpianti != null && Constants.listaImpianti.size() > 0) {
                list.add(createMenuItem(false, getResources().getString(R.string.ha_cancellaCentralina), "", "", r6, false, false));
            }
            AnonymousClass19 r62 = new Runnable() {
                public void run() {
                    if (TSDeviceListActivity.this.myDialogFragment != null) {
                        TSDeviceListActivity.this.dismissdialog();
                    }
                    TSDeviceListActivity.this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Functions.makeNormalToast(TSDeviceListActivity.this.activity, TSDeviceListActivity.this.getResources().getString(R.string.ha_cancellaCentralinaToast));
                            TSDeviceListActivity.this.refreshlist(true);
                        }
                    });
                    TSDeviceListActivity.this.updatemenu();
                }
            };
            try {
                if (Constants.listaImpianti.get(this.indexListPL).getListDevices() != null && Constants.listaImpianti.get(this.indexListPL).getListDevices().size() > 0) {
                    list.add(createMenuItem(false, getResources().getString(R.string.ha_cancellaDispositivo), "", "", r62, false, false));
                }
            } catch (Exception unused) {
            }
            list.add(createMenuItem(false, getResources().getString(R.string.ha_cancellaAccount), "", "", new Runnable() {
                public void run() {
                    if (TSDeviceListActivity.this.myDialogFragment != null) {
                        TSDeviceListActivity.this.dismissdialog();
                    }
                    TSDeviceListActivity.this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            TSDeviceListActivity.this.createDeleteUserPopUp();
                        }
                    });
                }
            }, false, false));
            list.add(createMenuItem(false, getResources().getString(R.string.GDPR_Menu), "", "", new Runnable() {
                public void run() {
                    TSDeviceListActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            SignUpActivity.PRIVACY = true;
                            SignUpActivity.TOU = true;
                            Intent intent = new Intent(TSDeviceListActivity.this.activity, GDPRActivity.class);
                            intent.putExtra(Constants.GDPRFROMLOGIN, true);
                            intent.putExtra(Constants.GDPRFROMHOME, true);
                            intent.putExtra(Constants.GDprUSERNAME, Constants.user);
                            TSDeviceListActivity.this.startActivity(intent);
                        }
                    });
                }
            }, false, false));
        }
        return list;
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 1001 && i2 == -1) {
            Intent intent2 = new Intent(this.activity, TSHomeActivity.class);
            intent2.putExtra("ForceUPD", true);
            this.activity.startActivity(intent2);
            finish();
        }
    }

    public String setToolbarTitle() {
        return Constants.listaImpianti.get(this.indexListPL).getLVPL_Name();
    }
}
