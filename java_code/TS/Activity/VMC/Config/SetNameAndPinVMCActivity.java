package it.tecnosystemi.TS.Activity.VMC.Config;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.gson.Gson;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity;
import it.tecnosystemi.TS.Activity.TS.TSHomeActivity;
import it.tecnosystemi.TS.Adapters.SpinnerCUIconsAdapter;
import it.tecnosystemi.TS.Commands.CmdPICO;
import it.tecnosystemi.TS.Commands.Protocols;
import it.tecnosystemi.TS.Commands.UDPSocket;
import it.tecnosystemi.TS.Model.Device_OP;
import it.tecnosystemi.TS.Model.VMC;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Threads.ThreadWebService;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONObject;

public class SetNameAndPinVMCActivity extends BaseActivity {
    BaseActivity activity;
    boolean background;
    boolean errorcollegamento;
    int mode;
    String pin;
    String serial;
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
        setContentView(R.layout.activity_set_name_and_pin_vmcactivity);
        this.activity = this;
        this.typeActStyle = 3;
        super.onCreate(bundle);
        int intExtra = getIntent().getIntExtra(Constants.INTENT_SETUPMODE, -1);
        this.mode = intExtra;
        if (intExtra == -1) {
            finish();
        }
        hideMenuButton();
        setUpGui();
        Functions.setFonts(findViewById(R.id.main), this);
    }

    private void setUpGui() {
        Functions.setFontsWithIcon(findViewById(R.id.ly_container), this);
        Button button = (Button) findViewById(R.id.snpa_btnProc);
        TextView textView = (TextView) findViewById(R.id.snpa_lblDescr);
        this.txtNome = (EditText) findViewById(R.id.snpa_txtNome);
        this.txtPin = (EditText) findViewById(R.id.snpa_txtPin);
        this.txtNome2 = (EditText) findViewById(R.id.snpa_txtNome2);
        new SpinnerCUIconsAdapter(this, new ArrayList(Arrays.asList(Constants.ICON_TYPE)));
        int i = this.mode;
        if (i == 2 || i == 4) {
            this.txtNome.setHint(getResources().getString(R.string.au_txtSerial));
            this.txtPin.setHint(getResources().getString(R.string.au_txtPin));
            button.setText(R.string.au_btnProcedi);
            textView.setText(getResources().getString(R.string.au_pico_lblInstruction));
            if (this.mode == 4) {
                this.txtNome2.setVisibility(0);
            }
        }
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

    public void btnProc(View view) {
        if (checinput()) {
            int i = this.mode;
            if (i == 2 || i == 4) {
                if (Constants.ISDEMO) {
                    Functions.makeNormalToast(this, getResources().getString(R.string.cu_DemoVersion));
                    Intent intent = new Intent(this, TSHomeActivity.class);
                    intent.addFlags(67108864);
                    startActivity(intent);
                } else if (this.mode == 2) {
                    this.serial = this.txtNome.getText().toString().toUpperCase();
                    this.pin = this.txtPin.getText().toString();
                    this.activity.getSharedPreferences(Constants.PREF_REGID_NAME, 0);
                    Device_OP device_OP = new Device_OP();
                    device_OP.setType_dev(Constants.DEVICE_TYPE_VMC);
                    device_OP.setPIN(this.txtPin.getText().toString());
                    device_OP.setSerial(this.txtNome.getText().toString().toUpperCase());
                    device_OP.setToken(this.activity.FirebaseToken);
                    device_OP.setPlatform(Constants.NOTIFIC_PLAT);
                    device_OP.setPlantId(TSDeviceListActivity.idSelected);
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    new ThreadWebService(this, 1, 24, getResources().getString(R.string.uriWebService) + getResources().getString(R.string.uri_AddUserToDeviceV2), new Gson().toJson((Object) device_OP), (String[]) null).start();
                } else {
                    showProgress();
                    this.serial = this.txtNome.getText().toString().toUpperCase();
                    this.pin = this.txtPin.getText().toString();
                    AnonymousClass1 r2 = new Runnable() {
                        public void run() {
                            SetNameAndPinVMCActivity.this.activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    SetNameAndPinVMCActivity.this.checkpin();
                                }
                            });
                        }
                    };
                    toConnPwd = "VMC_" + this.serial;
                    toConnSid = "VMC_" + this.serial;
                    connectToWifi(r2, new Runnable() {
                        public void run() {
                            SetNameAndPinVMCActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    String str = ((SetNameAndPinVMCActivity.this.getResources().getString(R.string.ba_apAssente) + "\n" + SetNameAndPinVMCActivity.this.getResources().getString(R.string.connectToPolaris)) + "\nSSID: " + BaseActivity.toConnSid) + "\n" + SetNameAndPinVMCActivity.this.getResources().getString(R.string.c4_PwdHint) + ": " + BaseActivity.toConnPwd;
                                    AlertDialog.Builder builder = new AlertDialog.Builder(SetNameAndPinVMCActivity.this.activity);
                                    builder.setMessage(str).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            try {
                                                Intent intent = new Intent("android.settings.WIFI_SETTINGS");
                                                intent.addFlags(268435456);
                                                SetNameAndPinVMCActivity.this.activity.startActivity(intent);
                                            } catch (Exception unused) {
                                            }
                                        }
                                    });
                                    AlertDialog create = builder.create();
                                    create.show();
                                    create.getButton(-1).setTextColor(SetNameAndPinVMCActivity.this.getResources().getColor(R.color.vmc_main));
                                }
                            });
                        }
                    }, false, false);
                }
            } else if (i == 3) {
                Intent intent2 = new Intent(this, ConfigVMCActivity.class);
                intent2.putExtra(Constants.INTENT_SETUPMODE, this.mode);
                intent2.putExtra(Constants.INTENT_PIN, this.txtPin.getText().toString());
                intent2.putExtra("name", this.txtNome.getText().toString());
                startActivity(intent2);
            } else {
                Intent intent3 = new Intent(this, ChooseWifiVMCActivity.class);
                intent3.putExtra(Constants.INTENT_SETUPMODE, this.mode);
                intent3.putExtra(Constants.INTENT_PIN, this.txtPin.getText().toString());
                intent3.putExtra("name", this.txtNome.getText().toString());
                startActivity(intent3);
            }
        }
    }

    public void checkpin() {
        new Thread(new Runnable() {
            public void run() {
                CmdPICO cmdPICO = new CmdPICO();
                cmdPICO.setCmd(Protocols.CMD_CHECK_PIN);
                cmdPICO.setPin(SetNameAndPinVMCActivity.this.pin);
                UDPSocket.startListening();
                String sendCMD = UDPSocket.sendCMD(cmdPICO);
                UDPSocket.stopListening();
                if (sendCMD != null) {
                    try {
                        JSONObject jSONObject = new JSONObject(sendCMD);
                        if (jSONObject.has(Constants.JSON_RES)) {
                            if (jSONObject.getInt(Constants.JSON_RES) == 1) {
                                VMC.saveVMCInPref(SetNameAndPinVMCActivity.this.txtNome2.getText().toString(), SetNameAndPinVMCActivity.this.serial, SetNameAndPinVMCActivity.this.pin, "", SetNameAndPinVMCActivity.this.activity, true, true);
                                SetNameAndPinVMCActivity setNameAndPinVMCActivity = SetNameAndPinVMCActivity.this;
                                setNameAndPinVMCActivity.addDevToPlant(setNameAndPinVMCActivity.serial, SetNameAndPinVMCActivity.this.txtNome2.getText().toString());
                                SetNameAndPinVMCActivity.this.activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Intent intent = new Intent(SetNameAndPinVMCActivity.this.activity, TSDeviceListActivity.class);
                                        intent.addFlags(67108864);
                                        SetNameAndPinVMCActivity.this.activity.startActivity(intent);
                                    }
                                });
                            } else {
                                SetNameAndPinVMCActivity.this.activity.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Functions.makeErrorToast(SetNameAndPinVMCActivity.this.activity, SetNameAndPinVMCActivity.this.getResources().getString(R.string.au_errorAddUser));
                                    }
                                });
                            }
                        }
                    } catch (Exception unused) {
                        SetNameAndPinVMCActivity.this.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Functions.makeErrorToast(SetNameAndPinVMCActivity.this.activity, SetNameAndPinVMCActivity.this.getResources().getString(R.string.msg_commandKo));
                            }
                        });
                    }
                } else {
                    SetNameAndPinVMCActivity.this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Functions.makeErrorToast(SetNameAndPinVMCActivity.this.activity, SetNameAndPinVMCActivity.this.getResources().getString(R.string.msg_commandKo));
                        }
                    });
                }
                SetNameAndPinVMCActivity.this.hideProgress();
            }
        }).start();
    }

    public void savserverVMC() {
        VMC.saveVMCInPref(this.txtNome2.getText().toString(), this.serial, this.pin, "", this.activity, false, true);
        addDevToPlant(this.serial, this.txtNome2.getText().toString());
        runOnUiThread(new Runnable() {
            public void run() {
                Intent intent = new Intent(SetNameAndPinVMCActivity.this.activity, TSDeviceListActivity.class);
                intent.setFlags(67108864);
                SetNameAndPinVMCActivity.this.activity.startActivity(intent);
            }
        });
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0075  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x008b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void addDevToPlant(java.lang.String r7, java.lang.String r8) {
        /*
            r6 = this;
            it.tecnosystemi.TS.Model.Device r0 = new it.tecnosystemi.TS.Model.Device
            r0.<init>()
            int r1 = it.tecnosystemi.TS.Utils.Constants.DEVICE_TYPE_VMC
            r0.setLVDV_Type(r1)
            r0.setSerial(r7)
            r0.setName(r8)
            it.tecnosystemi.TS.Activity.BaseActivity r7 = r6.activity
            it.tecnosystemi.TS.Model.Device.deleteDevFromPref(r0, r7)
            long r1 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.idSelected
            it.tecnosystemi.TS.Model.Plant.addDeviceToPlantPref(r0, r1, r6)
            r7 = 0
        L_0x001b:
            java.util.List<it.tecnosystemi.TS.Model.Plant> r1 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            int r1 = r1.size()
            if (r7 >= r1) goto L_0x009f
            java.util.List<it.tecnosystemi.TS.Model.Plant> r1 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r1 = r1.get(r7)
            it.tecnosystemi.TS.Model.Plant r1 = (it.tecnosystemi.TS.Model.Plant) r1
            long r1 = r1.getLVPL_Id()
            long r3 = it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity.idSelected
            int r5 = (r1 > r3 ? 1 : (r1 == r3 ? 0 : -1))
            if (r5 != 0) goto L_0x009b
            java.util.List<it.tecnosystemi.TS.Model.Plant> r1 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r1 = r1.get(r7)
            it.tecnosystemi.TS.Model.Plant r1 = (it.tecnosystemi.TS.Model.Plant) r1
            java.util.List r1 = r1.getListDevices()
            if (r1 == 0) goto L_0x0062
            java.util.List<it.tecnosystemi.TS.Model.Plant> r1 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r1 = r1.get(r7)
            it.tecnosystemi.TS.Model.Plant r1 = (it.tecnosystemi.TS.Model.Plant) r1
            java.util.List r1 = r1.getListDevices()
            if (r1 == 0) goto L_0x0072
            java.util.List<it.tecnosystemi.TS.Model.Plant> r1 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r1 = r1.get(r7)
            it.tecnosystemi.TS.Model.Plant r1 = (it.tecnosystemi.TS.Model.Plant) r1
            java.util.List r1 = r1.getListDevices()
            int r1 = r1.indexOf(r0)
            goto L_0x0073
        L_0x0062:
            java.util.List<it.tecnosystemi.TS.Model.Plant> r1 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r1 = r1.get(r7)
            it.tecnosystemi.TS.Model.Plant r1 = (it.tecnosystemi.TS.Model.Plant) r1
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r1.setListDevices(r2)
        L_0x0072:
            r1 = -1
        L_0x0073:
            if (r1 < 0) goto L_0x008b
            java.util.List<it.tecnosystemi.TS.Model.Plant> r0 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r7 = r0.get(r7)
            it.tecnosystemi.TS.Model.Plant r7 = (it.tecnosystemi.TS.Model.Plant) r7
            java.util.List r7 = r7.getListDevices()
            java.lang.Object r7 = r7.get(r1)
            it.tecnosystemi.TS.Model.Device r7 = (it.tecnosystemi.TS.Model.Device) r7
            r7.setName(r8)
            goto L_0x009f
        L_0x008b:
            java.util.List<it.tecnosystemi.TS.Model.Plant> r8 = it.tecnosystemi.TS.Utils.Constants.listaImpianti
            java.lang.Object r7 = r8.get(r7)
            it.tecnosystemi.TS.Model.Plant r7 = (it.tecnosystemi.TS.Model.Plant) r7
            java.util.List r7 = r7.getListDevices()
            r7.add(r0)
            goto L_0x009f
        L_0x009b:
            int r7 = r7 + 1
            goto L_0x001b
        L_0x009f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.VMC.Config.SetNameAndPinVMCActivity.addDevToPlant(java.lang.String, java.lang.String):void");
    }

    public View getToolBar() {
        return findViewById(R.id.vmc_toolbar);
    }

    public String setToolbarTitle() {
        if (this.mode == 2) {
            return getResources().getString(R.string.au_vmc_title);
        }
        return getResources().getString(R.string.c3_vmc_title);
    }
}
