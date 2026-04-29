package it.tecnosystemi.TS.Activity.Config;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity;
import it.tecnosystemi.TS.Adapters.SpinnerCUIconsAdapter;
import it.tecnosystemi.TS.Commands.MySocket;
import it.tecnosystemi.TS.Model.ControlUnit;
import it.tecnosystemi.TS.Model.Device_OP;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Threads.ThreadWebService;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;

public class SetNameAndPinActivity extends BaseActivity {
    BaseActivity activity;
    boolean background;
    boolean errorcollegamento;
    TextView lblfreccia;
    int mode;
    String pin;
    String serial;
    Spinner spinnerhome;
    EditText txtNome;
    EditText txtNome2;
    EditText txtPin;

    public BaseActivity getActivity() {
        return this;
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        return list;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_set_name_and_pin);
        this.errorcollegamento = false;
        this.activity = this;
        int intExtra = getIntent().getIntExtra(Constants.INTENT_SETUPMODE, -1);
        this.mode = intExtra;
        if (intExtra == -1) {
            finish();
        }
        super.onCreate(bundle);
        hideMenuButton();
        setUpGui();
    }

    private void setUpGui() {
        Button button = (Button) findViewById(R.id.snpa_btnProc);
        TextView textView = (TextView) findViewById(R.id.snpa_lblDescr);
        this.txtNome = (EditText) findViewById(R.id.snpa_txtNome);
        this.txtPin = (EditText) findViewById(R.id.snpa_txtPin);
        this.txtNome2 = (EditText) findViewById(R.id.snpa_txtNome2);
        this.lblfreccia = (TextView) findViewById(R.id.snpa_lblFreccia);
        this.spinnerhome = (Spinner) findViewById(R.id.snpa_spinner);
        this.spinnerhome.setAdapter(new SpinnerCUIconsAdapter(this, new ArrayList(Arrays.asList(Constants.ICON_TYPE))));
        this.spinnerhome.setSelection(0);
        if (this.mode == 2) {
            this.spinnerhome.setVisibility(8);
        } else {
            this.spinnerhome.setVisibility(8);
        }
        this.lblfreccia.setTypeface(fontawesome);
        button.setTypeface(avenir);
        textView.setTypeface(avenir);
        this.txtNome.setTypeface(avenir);
        this.txtPin.setTypeface(avenir);
        this.txtNome2.setTypeface(avenir);
        int i = this.mode;
        if (i == 2 || i == 4) {
            this.txtNome.setHint(getResources().getString(R.string.au_txtSerial));
            this.txtPin.setHint(getResources().getString(R.string.au_txtPin));
            button.setText(R.string.au_btnProcedi);
            textView.setText(getResources().getString(R.string.au_lblInstruction));
            this.txtNome.setInputType(2);
            if (this.mode == 4) {
                this.txtNome2.setVisibility(0);
            }
        }
    }

    public void btnProc(View view) {
        if (checinput()) {
            int i = this.mode;
            if (i == 2 || i == 4) {
                if (Constants.ISDEMO) {
                    Functions.makeNormalToast(this, getResources().getString(R.string.cu_DemoVersion));
                    Intent intent = new Intent(this, TSDeviceListActivity.class);
                    intent.addFlags(67108864);
                    startActivity(intent);
                    return;
                }
                this.pin = this.txtPin.getText().toString();
                this.serial = this.txtNome.getText().toString();
                if (this.mode == 2) {
                    this.activity.getSharedPreferences(Constants.PREF_REGID_NAME, 0);
                    if (Functions.getNotificationPermision(this.activity)) {
                        FirebaseInstanceId.getInstance().getToken();
                    }
                    Device_OP device_OP = new Device_OP();
                    device_OP.setPico(false);
                    device_OP.setPIN(this.txtPin.getText().toString());
                    device_OP.setSerial(this.txtNome.getText().toString());
                    device_OP.setToken(this.activity.FirebaseToken);
                    device_OP.setPlatform(Constants.NOTIFIC_PLAT);
                    device_OP.setPlantId(TSDeviceListActivity.idSelected);
                    new ThreadWebService(this, 1, 8, getResources().getString(R.string.uriWebService) + getResources().getString(R.string.uri_AddUserToDevice), new Gson().toJson((Object) device_OP), (String[]) null).start();
                    return;
                }
                showProgress();
                AnonymousClass1 r2 = new Runnable() {
                    public void run() {
                        SetNameAndPinActivity.this.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                SetNameAndPinActivity.this.checkpin();
                            }
                        });
                    }
                };
                toConnPwd = "TS_" + this.serial;
                toConnSid = Constants.WIFI_NAME_OFFLINE + this.serial;
                connectToWifi(r2, false, false);
            } else if (i == 3) {
                Intent intent2 = new Intent(this, ConfigActivity.class);
                intent2.putExtra(Constants.INTENT_ICON, this.spinnerhome.getSelectedItemPosition());
                intent2.putExtra(Constants.INTENT_SETUPMODE, this.mode);
                intent2.putExtra(Constants.INTENT_PIN, this.txtPin.getText().toString());
                intent2.putExtra("name", this.txtNome.getText().toString());
                startActivity(intent2);
            } else {
                Intent intent3 = new Intent(this, ChooseWifiActivity.class);
                intent3.putExtra(Constants.INTENT_SETUPMODE, this.mode);
                intent3.putExtra(Constants.INTENT_ICON, this.spinnerhome.getSelectedItemPosition());
                intent3.putExtra(Constants.INTENT_PIN, this.txtPin.getText().toString());
                intent3.putExtra("name", this.txtNome.getText().toString());
                startActivity(intent3);
            }
        }
    }

    public void savsercerUc() {
        addcentralina("", this.txtNome.getText().toString(), -1, false);
        Intent intent = new Intent(this.activity, TSDeviceListActivity.class);
        intent.addFlags(67108864);
        this.activity.startActivity(intent);
    }

    public void addcentralina(String str, String str2, int i, boolean z) {
        ControlUnit.deleteCufromPref(str2, this);
        ControlUnit.saveCuInPref(str, str2, this.pin, "", i, this.activity, z);
        addDevToPlant(str2, str);
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x0088  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x009e  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void addDevToPlant(java.lang.String r7, java.lang.String r8) {
        /*
            r6 = this;
            it.tecnosystemi.TS.Model.Device r0 = new it.tecnosystemi.TS.Model.Device
            r0.<init>()
            int r1 = it.tecnosystemi.TS.Utils.Constants.DEVICE_TYPE_PROAIR
            r0.setLVDV_Type(r1)
            r0.setSerial(r7)
            r0.setName(r8)
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
        L_0x002e:
            java.util.List<it.tecnosystemi.TS.Model.Plant> r1 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            int r1 = r1.size()
            if (r7 >= r1) goto L_0x00b2
            java.util.List<it.tecnosystemi.TS.Model.Plant> r1 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r1 = r1.get(r7)
            it.tecnosystemi.TS.Model.Plant r1 = (it.tecnosystemi.TS.Model.Plant) r1
            long r1 = r1.getLVPL_Id()
            long r3 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.idSelected
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x00ae
            java.util.List<it.tecnosystemi.TS.Model.Plant> r1 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r1 = r1.get(r7)
            it.tecnosystemi.TS.Model.Plant r1 = (it.tecnosystemi.TS.Model.Plant) r1
            java.util.List r1 = r1.getListDevices()
            if (r1 == 0) goto L_0x0075
            java.util.List<it.tecnosystemi.TS.Model.Plant> r1 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r1 = r1.get(r7)
            it.tecnosystemi.TS.Model.Plant r1 = (it.tecnosystemi.TS.Model.Plant) r1
            java.util.List r1 = r1.getListDevices()
            if (r1 == 0) goto L_0x0085
            java.util.List<it.tecnosystemi.TS.Model.Plant> r1 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r1 = r1.get(r7)
            it.tecnosystemi.TS.Model.Plant r1 = (it.tecnosystemi.TS.Model.Plant) r1
            java.util.List r1 = r1.getListDevices()
            int r1 = r1.indexOf(r0)
            goto L_0x0086
        L_0x0075:
            java.util.List<it.tecnosystemi.TS.Model.Plant> r1 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r1 = r1.get(r7)
            it.tecnosystemi.TS.Model.Plant r1 = (it.tecnosystemi.TS.Model.Plant) r1
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r1.setListDevices(r2)
        L_0x0085:
            r1 = -1
        L_0x0086:
            if (r1 < 0) goto L_0x009e
            java.util.List<it.tecnosystemi.TS.Model.Plant> r0 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r7 = r0.get(r7)
            it.tecnosystemi.TS.Model.Plant r7 = (it.tecnosystemi.TS.Model.Plant) r7
            java.util.List r7 = r7.getListDevices()
            java.lang.Object r7 = r7.get(r1)
            it.tecnosystemi.TS.Model.Device r7 = (it.tecnosystemi.TS.Model.Device) r7
            r7.setName(r8)
            goto L_0x00b2
        L_0x009e:
            java.util.List<it.tecnosystemi.TS.Model.Plant> r8 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r7 = r8.get(r7)
            it.tecnosystemi.TS.Model.Plant r7 = (it.tecnosystemi.TS.Model.Plant) r7
            java.util.List r7 = r7.getListDevices()
            r7.add(r0)
            goto L_0x00b2
        L_0x00ae:
            int r7 = r7 + 1
            goto L_0x002e
        L_0x00b2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.Config.SetNameAndPinActivity.addDevToPlant(java.lang.String, java.lang.String):void");
    }

    public void checkpin() {
        new Thread(new Runnable() {
            public void run() {
                String checkPinCmd = MySocket.checkPinCmd(SetNameAndPinActivity.this.pin, Constants.ip, Constants.port);
                if (checkPinCmd != null) {
                    try {
                        JSONObject jSONObject = new JSONObject(checkPinCmd);
                        if (jSONObject.has(Constants.JSON_RES)) {
                            if (jSONObject.getInt(Constants.JSON_RES) == 1) {
                                SetNameAndPinActivity setNameAndPinActivity = SetNameAndPinActivity.this;
                                setNameAndPinActivity.addcentralina(setNameAndPinActivity.txtNome2.getText().toString(), SetNameAndPinActivity.this.serial, SetNameAndPinActivity.this.spinnerhome.getSelectedItemPosition(), true);
                                SetNameAndPinActivity.this.activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Intent intent = new Intent(SetNameAndPinActivity.this.activity, TSDeviceListActivity.class);
                                        intent.addFlags(67108864);
                                        SetNameAndPinActivity.this.startActivity(intent);
                                    }
                                });
                            } else {
                                SetNameAndPinActivity.this.activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Functions.makeErrorToast(SetNameAndPinActivity.this.activity, SetNameAndPinActivity.this.getResources().getString(R.string.au_errorAddUser));
                                    }
                                });
                            }
                        }
                    } catch (Exception unused) {
                        SetNameAndPinActivity.this.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Functions.makeErrorToast(SetNameAndPinActivity.this.activity, SetNameAndPinActivity.this.getResources().getString(R.string.msg_commandKo));
                            }
                        });
                    }
                } else {
                    SetNameAndPinActivity.this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Functions.makeErrorToast(SetNameAndPinActivity.this.activity, SetNameAndPinActivity.this.getResources().getString(R.string.msg_commandKo));
                        }
                    });
                }
                SetNameAndPinActivity.this.hideProgress();
            }
        }).start();
    }

    public boolean checinput() {
        boolean z;
        if (this.txtNome.getText().toString().isEmpty()) {
            Functions.ShowerrorOnView(this, this.txtNome, getResources().getString(R.string.sa_errorEmpty));
            z = false;
        } else {
            z = true;
        }
        if (this.mode == 4 && this.txtNome2.getText().toString().isEmpty()) {
            Functions.ShowerrorOnView(this, this.txtNome2, getResources().getString(R.string.sa_errorEmpty));
            z = false;
        }
        if (this.txtPin.getText().toString().length() != 4) {
            Functions.ShowerrorOnView(this, this.txtPin, getResources().getString(R.string.msg_errorPinLenght));
            z = false;
        }
        try {
            Integer.parseInt(this.txtPin.getText().toString());
            return z;
        } catch (Exception unused) {
            Functions.ShowerrorOnView(this, this.txtPin, getResources().getString(R.string.msg_errorPinLenght));
            return false;
        }
    }

    public void onResume() {
        super.onResume();
        this.background = false;
        if (this.errorcollegamento) {
            this.errorcollegamento = false;
            Functions.makeErrorToast(this, getResources().getString(R.string.ba_apAssente));
        }
    }

    public void onPause() {
        super.onPause();
        this.background = true;
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        if (this.cercacentralina != null) {
            this.cercacentralina.interrupt();
        }
        super.onDestroy();
    }

    public View getToolBar() {
        return findViewById(R.id.snpa_toolbar);
    }

    public String setToolbarTitle() {
        if (this.mode == 2) {
            return getResources().getString(R.string.au_title);
        }
        return getResources().getString(R.string.c3_title);
    }
}
