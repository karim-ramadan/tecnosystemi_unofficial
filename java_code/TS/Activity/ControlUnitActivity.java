package it.tecnosystemi.TS.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity;
import it.tecnosystemi.TS.Adapters.ZoneAdapter;
import it.tecnosystemi.TS.Commands.MySocket;
import it.tecnosystemi.TS.Commands.Protocols;
import it.tecnosystemi.TS.Model.ControlUnit;
import it.tecnosystemi.TS.Model.Device;
import it.tecnosystemi.TS.Model.Device_OP;
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

public class ControlUnitActivity extends BaseActivity {
    public static int indexcudemo;
    Button btnOnOff;
    Button btnRaff;
    Button btnRisc;
    BaseActivity.BundleMenuList bundlePopUp;
    boolean firstcu;
    boolean hasfinest = false;
    TextView lblLastSync;
    TextView lblStatus;
    TextView lblStatusIcon;
    ListView lv;
    public ZoneAdapter zoneAdapter;

    public BaseActivity getActivity() {
        return this;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        this.activity = this;
        setContentView(R.layout.activity_control_unit);
        Intent intent = getIntent();
        int i = 0;
        this.interrupt = false;
        this.firstcu = true;
        this.cu = (ControlUnit) intent.getSerializableExtra(Constants.INTENT_CU);
        if (Constants.ISDEMO) {
            while (true) {
                if (i >= DataClass.getInstance(this).controlunit_list.size()) {
                    break;
                } else if (DataClass.getInstance(this).controlunit_list.get(i).getSerial().equals(this.cu.getSerial())) {
                    indexcudemo = i;
                    break;
                } else {
                    i++;
                }
            }
            Constants.DEMO_CU = DataClass.getInstance(this).controlunit_list.get(indexcudemo);
            this.cu = Constants.DEMO_CU;
        } else if (!this.cu.isOffline()) {
            this.urlgetupd = getResources().getString(R.string.uriWebService_POLARIS) + getResources().getString(R.string.uri_GetState) + "?cuSerial=" + this.cu.getSerial() + "&PIN=" + this.cu.getPin();
        }
        this.tempcu = new ControlUnit(this.cu.getName());
        super.onCreate(bundle);
        setUpGui();
        try {
            if (this.cu.getZone().size() > 0) {
                ZoneAdapter zoneAdapter2 = new ZoneAdapter(this, this.cu);
                this.zoneAdapter = zoneAdapter2;
                this.lv.setAdapter(zoneAdapter2);
            }
        } catch (Exception unused) {
        }
    }

    public void setUpGui() {
        this.lblStatus = (TextView) findViewById(R.id.cua_lblStatus);
        this.lblStatusIcon = (TextView) findViewById(R.id.cua_lblStatuIcon);
        this.lblLastSync = (TextView) findViewById(R.id.cua_lblLassync);
        this.btnOnOff = (Button) findViewById(R.id.cua_btnOnOff);
        this.btnRisc = (Button) findViewById(R.id.cua_btnRiscaldamento);
        this.btnRaff = (Button) findViewById(R.id.cua_btnRaffrescamento);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "fonts/fontawesome.ttf");
        Typeface createFromAsset2 = Typeface.createFromAsset(getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
        Typeface createFromAsset3 = Typeface.createFromAsset(getAssets(), "fonts/icomoon.ttf");
        this.lblStatusIcon.setTypeface(createFromAsset);
        this.lblStatus.setTypeface(createFromAsset2);
        this.btnOnOff.setTypeface(createFromAsset3);
        this.btnRisc.setTypeface(createFromAsset3);
        this.btnRaff.setTypeface(createFromAsset3);
        this.lv = (ListView) findViewById(R.id.cua_listView);
        this.btnOnOff.setVisibility(8);
        this.btnRisc.setVisibility(8);
        this.btnRaff.setVisibility(8);
        this.lblStatus.setVisibility(8);
        this.lblStatusIcon.setVisibility(8);
        if (Constants.ISDEMO) {
            loadData(false);
        }
    }

    public void openZone(int i) {
        Intent intent = new Intent(this, ZoneActivity.class);
        intent.putExtra(Constants.INTENT_CU, this.cu);
        if (Constants.ISDEMO) {
            Constants.DEMO_INDEX_ZONA = i;
            intent.putExtra(Constants.INTENT_ZONA, Constants.DEMO_CU.getZone().get(i));
        } else {
            intent.putExtra(Constants.INTENT_ZONA, this.tempcu.getZone().get(i));
        }
        intent.putExtra(Constants.INTENT_INDEXZONA, i);
        startActivity(intent);
    }

    public void checkPin() {
        if (!this.cu.isOffline() || (this.cu.getPinOffline() != null && !this.cu.getPinOffline().isEmpty())) {
            if (this.cu.isOffline()) {
                return;
            }
            if (this.cu.getPin() != null && !this.cu.getPin().isEmpty()) {
                return;
            }
        }
        BaseActivity.BundleMenuList createSetPin = createSetPin(new Runnable() {
            public void run() {
                ControlUnitActivity.this.dismissdialog();
                ControlUnitActivity.this.changepinempty = false;
                if (ControlUnitActivity.this.cu.isOffline()) {
                    ControlUnitActivity.this.cu.setPinOffline(ControlUnitActivity.this.txtPin.getText().toString());
                } else {
                    ControlUnitActivity.this.cu.setPin(ControlUnitActivity.this.txtPin.getText().toString());
                    ControlUnitActivity controlUnitActivity = ControlUnitActivity.this;
                    controlUnitActivity.urlgetupd = ControlUnitActivity.this.getResources().getString(R.string.uriWebService_POLARIS) + ControlUnitActivity.this.getResources().getString(R.string.uri_GetState) + "?cuSerial=" + ControlUnitActivity.this.cu.getSerial() + "&PIN=" + ControlUnitActivity.this.cu.getPin();
                }
                ControlUnit.saveCuInPref(ControlUnitActivity.this.cu, ControlUnitActivity.this.activity);
                ControlUnitActivity.this.firtCalltoGetState = true;
            }
        });
        this.bundlePopUp = createSetPin;
        openDialogFragment(createSetPin);
    }

    public void loadData(boolean z) {
        if (this.firstcu || Constants.ISDEMO) {
            this.tempcu = this.cu.makeTempCopy();
        }
        this.tempcu.setZone(this.cu.getZone());
        this.tempcu.setF_inv(this.cu.getF_inv());
        this.tempcu.setF_est(this.cu.getF_est());
        if (this.cu.getNumError() > 0) {
            this.cu.getStringerrors(getResources().getStringArray(R.array.cu_errors));
            this.lblStatus.setText(this.cu.getStringerrors(getResources().getStringArray(R.array.cu_errors)));
            this.lblStatus.setTypeface(avenir);
            this.lblStatus.setVisibility(0);
            this.lblStatusIcon.setVisibility(0);
        } else {
            this.lblStatus.setVisibility(8);
            this.lblStatusIcon.setVisibility(8);
        }
        if (!z) {
            this.btnOnOff.setVisibility(0);
            this.btnRisc.setVisibility(0);
            this.btnRaff.setVisibility(0);
            this.lv.setVisibility(0);
            this.btnOnOff.setText(Constants.CU_ONOFF_ICON[0]);
            changeTitle(this.cu.getName().toUpperCase());
            if (this.cu.getIsOff()) {
                this.btnOnOff.setTextColor(getResources().getColorStateList(R.color.textdisableselector));
                this.btnRisc.setEnabled(false);
                this.btnRaff.setEnabled(false);
            } else {
                this.btnOnOff.setTextColor(getResources().getColorStateList(R.color.textprimaryselector));
                this.btnRisc.setEnabled(true);
                this.btnRaff.setEnabled(true);
            }
            if (!this.cu.getIsCooling()) {
                this.btnRaff.setBackground(getResources().getDrawable(R.drawable.btndisable));
                this.btnRisc.setBackground(getResources().getDrawable(R.drawable.btn_selector));
                this.btnRaff.setText(Constants.CU_OPERATINGMODE_ICON[1]);
            } else {
                this.btnRisc.setBackground(getResources().getDrawable(R.drawable.btndisable));
                this.btnRaff.setBackground(getResources().getDrawable(R.drawable.btn_selector));
                this.btnRaff.setText(Constants.CU_OPERATINGMODE_ICON[this.cu.getOperatingMode()]);
            }
            if (this.zoneAdapter == null) {
                try {
                    if (this.cu.getZone().size() > 0) {
                        ZoneAdapter zoneAdapter2 = new ZoneAdapter(this, this.cu);
                        this.zoneAdapter = zoneAdapter2;
                        this.lv.setAdapter(zoneAdapter2);
                    }
                } catch (Exception unused) {
                }
            }
            refreshList();
        }
    }

    public void refreshList() {
        ZoneAdapter zoneAdapter2;
        if (Constants.ISDEMO) {
            this.cu = DataClass.getInstance(this).controlunit_list.get(indexcudemo);
            ZoneAdapter zoneAdapter3 = new ZoneAdapter(this, this.cu);
            this.zoneAdapter = zoneAdapter3;
            this.lv.setAdapter(zoneAdapter3);
        } else if (this.cu != null && (zoneAdapter2 = this.zoneAdapter) != null) {
            zoneAdapter2.updateData(this.cu);
            this.zoneAdapter.notifyDataSetChanged();
        }
    }

    public void createPoUpOperatingMOde() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        arrayList.add(getResources().getString(R.string.cu_menuTipoRaffrescamento));
        arrayList.add(getResources().getString(R.string.cu_menuTipoDeumidificazione));
        arrayList.add(getResources().getString(R.string.cu_menuTipoVentilazione));
        arrayList2.add(Constants.CU_OPERATINGMODE_ICON[1]);
        arrayList2.add(Constants.CU_OPERATINGMODE_ICON[2]);
        arrayList2.add(Constants.CU_OPERATINGMODE_ICON[3]);
        arrayList3.add(new Runnable() {
            public void run() {
                ControlUnitActivity.this.tempcu.setOperatingMode(1);
                ControlUnitActivity.this.tempcu.setIsCooling(true);
                ControlUnitActivity.this.dismissdialog();
                if (ControlUnitActivity.this.cu.getIr_present() == 1) {
                    ControlUnitActivity.this.createPopUpChangeTCan(false);
                    ControlUnitActivity controlUnitActivity = ControlUnitActivity.this;
                    controlUnitActivity.openDialogFragment(controlUnitActivity.bundlePopUp);
                    return;
                }
                ControlUnitActivity.this.saveData();
            }
        });
        arrayList3.add(new Runnable() {
            public void run() {
                ControlUnitActivity.this.tempcu.setOperatingMode(2);
                ControlUnitActivity.this.tempcu.setIsCooling(true);
                ControlUnitActivity.this.dismissdialog();
                ControlUnitActivity.this.saveData();
            }
        });
        arrayList3.add(new Runnable() {
            public void run() {
                ControlUnitActivity.this.tempcu.setOperatingMode(3);
                ControlUnitActivity.this.tempcu.setIsCooling(true);
                ControlUnitActivity.this.dismissdialog();
                ControlUnitActivity.this.saveData();
            }
        });
        if (!this.cu.getIsCooling()) {
            this.bundlePopUp = createPopUp(true, getResources().getString(R.string.cu_menuTipoUCTitle), arrayList, arrayList2, (List<String>) null, arrayList3, -1, true);
            return;
        }
        this.bundlePopUp = createPopUp(true, getResources().getString(R.string.cu_menuTipoUCTitle), arrayList, arrayList2, (List<String>) null, arrayList3, this.cu.getOperatingMode() - 1, true);
    }

    public void createPopUpRinominaCU() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        arrayList.add(this.cu.getName().toUpperCase());
        arrayList2.add(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 6) {
                    return false;
                }
                ControlUnitActivity.this.tempcu.setName(textView.getText().toString());
                ControlUnitActivity.this.dismissdialog();
                ControlUnitActivity.this.saveData();
                return false;
            }
        });
        this.bundlePopUp = createTxtPopUp(getResources().getString(R.string.cu_menuRinomina), arrayList, "", arrayList2);
    }

    public void createPopUpDataOra() {
        this.bundlePopUp = createDataOraPopUp(getResources().getString(R.string.cu_menuSetOra), "", new Runnable() {
            public void run() {
                ControlUnitActivity.this.dismissdialog();
                if (!Constants.ISDEMO) {
                    try {
                        JSONObject jSONObject = new JSONObject("{}");
                        jSONObject.put("c", Protocols.CMD_UPDDATA);
                        if (ControlUnitActivity.this.cu.isOffline()) {
                            jSONObject.put(Constants.INTENT_PIN, ControlUnitActivity.this.cu.getPinOffline());
                        } else {
                            jSONObject.put(Constants.INTENT_PIN, ControlUnitActivity.this.cu.getPin());
                        }
                        jSONObject.put("h24", ControlUnitActivity.this.cu.getH24());
                        jSONObject.put("d", ControlUnitActivity.this.cu.getDay());
                        jSONObject.put("h", ControlUnitActivity.this.cu.getOre());
                        jSONObject.put("m", ControlUnitActivity.this.cu.getMinuti());
                        if (ControlUnitActivity.this.cu.isOffline()) {
                            MySocket.commandToCU(jSONObject.toString(), Constants.ip, Constants.port, true, true, true);
                        } else {
                            if (ControlUnitActivity.this.cu.getIp() != null && !ControlUnitActivity.this.cu.getIp().isEmpty()) {
                                String commandToCU = MySocket.commandToCU(jSONObject.toString(), ControlUnitActivity.this.cu.getIp(), Constants.port, true, false, true);
                                if (commandToCU != null && !commandToCU.isEmpty()) {
                                    JSONObject jSONObject2 = new JSONObject(commandToCU);
                                    if (jSONObject2.has(Constants.JSON_RES) && jSONObject2.getInt(Constants.JSON_RES) == 1) {
                                        ControlUnitActivity.this.activity.runOnUiThread(new Runnable() {
                                            public void run() {
                                                ControlUnitActivity.this.hideProgress();
                                                Functions.makeNormalToast(ControlUnitActivity.this.activity, ControlUnitActivity.this.getResources().getString(R.string.msg_commandOk));
                                            }
                                        });
                                        ControlUnitActivity.this.hideProgress();
                                        return;
                                    }
                                }
                                ControlUnitActivity.this.cu.setIp("");
                            }
                            JSONObject jSONObject3 = new JSONObject("{}");
                            jSONObject3.put(Constants.JSON_CU_SERIAL, ControlUnitActivity.this.cu.getSerial());
                            jSONObject3.put(Constants.JSON_CU_NAME, ControlUnitActivity.this.cu.getName());
                            jSONObject3.put(Constants.JSON_CU_PIN, ControlUnitActivity.this.cu.getPin());
                            jSONObject3.put("cmd", jSONObject.toString());
                            String[] strArr = new String[2];
                            strArr[1] = ControlUnitActivity.this.cu.getFWVer();
                            new ThreadWebService(ControlUnitActivity.this.activity, 1, 16, ControlUnitActivity.this.getResources().getString(R.string.uriWebService_POLARIS) + ControlUnitActivity.this.getResources().getString(R.string.uri_UpdDataOra), jSONObject3.toString(), strArr).start();
                        }
                        ControlUnit.saveCuInPref(ControlUnitActivity.this.cu, ControlUnitActivity.this.activity);
                    } catch (Exception unused) {
                    }
                }
            }
        });
    }

    public void createPopUpUnitMisura() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        arrayList.add(getResources().getString(R.string.cu_unitOfMeasureC));
        arrayList.add(getResources().getString(R.string.cu_unitOfMeasureF));
        arrayList2.add("");
        arrayList2.add("");
        AnonymousClass7 r0 = new Runnable() {
            public void run() {
                ControlUnitActivity.this.dismissdialog();
                ControlUnitActivity.this.tempcu.setUnitOfMesure(0);
                ControlUnitActivity.this.cu.setUnitOfMesure(0);
                if (Constants.ISDEMO) {
                    ControlUnitActivity.this.saveData();
                    return;
                }
                ControlUnit.saveCuInPref(ControlUnitActivity.this.cu, ControlUnitActivity.this.activity);
                ControlUnitActivity.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        ControlUnitActivity.this.refreshList();
                    }
                });
            }
        };
        AnonymousClass8 r1 = new Runnable() {
            public void run() {
                ControlUnitActivity.this.dismissdialog();
                ControlUnitActivity.this.tempcu.setUnitOfMesure(1);
                ControlUnitActivity.this.cu.setUnitOfMesure(1);
                if (Constants.ISDEMO) {
                    ControlUnitActivity.this.saveData();
                    return;
                }
                ControlUnit.saveCuInPref(ControlUnitActivity.this.cu, ControlUnitActivity.this.activity);
                ControlUnitActivity.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        ControlUnitActivity.this.refreshList();
                    }
                });
            }
        };
        arrayList3.add(r0);
        arrayList3.add(r1);
        this.bundlePopUp = createPopUp(true, getResources().getString(R.string.cu_menuSetUM), arrayList, arrayList2, (List<String>) null, arrayList3, this.cu.getUnitOfMesure(), true);
    }

    public void createPopUpFunzInvEst(final boolean z) {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        AnonymousClass9 r0 = new Runnable() {
            public void run() {
                ControlUnitActivity.this.dismissdialog();
                if (z) {
                    ControlUnitActivity.this.tempcu.setF_inv(0);
                } else {
                    ControlUnitActivity.this.tempcu.setF_est(0);
                }
                ControlUnitActivity.this.saveData();
            }
        };
        AnonymousClass10 r1 = new Runnable() {
            public void run() {
                ControlUnitActivity.this.dismissdialog();
                if (z) {
                    ControlUnitActivity.this.tempcu.setF_inv(1);
                } else {
                    ControlUnitActivity.this.tempcu.setF_est(1);
                }
                ControlUnitActivity.this.saveData();
            }
        };
        AnonymousClass11 r2 = new Runnable() {
            public void run() {
                ControlUnitActivity.this.dismissdialog();
                if (z) {
                    ControlUnitActivity.this.tempcu.setF_inv(2);
                } else {
                    ControlUnitActivity.this.tempcu.setF_est(2);
                }
                ControlUnitActivity.this.saveData();
            }
        };
        AnonymousClass12 r5 = new Runnable() {
            public void run() {
                ControlUnitActivity.this.dismissdialog();
                if (z) {
                    ControlUnitActivity.this.tempcu.setF_inv(3);
                } else {
                    ControlUnitActivity.this.tempcu.setF_est(3);
                }
                ControlUnitActivity.this.saveData();
            }
        };
        AnonymousClass13 r7 = new Runnable() {
            public void run() {
                ControlUnitActivity.this.dismissdialog();
                if (z) {
                    ControlUnitActivity.this.tempcu.setF_inv(4);
                } else {
                    ControlUnitActivity.this.tempcu.setF_est(4);
                }
                ControlUnitActivity.this.saveData();
            }
        };
        AnonymousClass14 r8 = new Runnable() {
            public void run() {
                ControlUnitActivity.this.dismissdialog();
                if (z) {
                    ControlUnitActivity.this.tempcu.setF_inv(5);
                } else {
                    ControlUnitActivity.this.tempcu.setF_est(5);
                }
                ControlUnitActivity.this.saveData();
            }
        };
        AnonymousClass15 r9 = new Runnable() {
            public void run() {
                ControlUnitActivity.this.dismissdialog();
                if (z) {
                    ControlUnitActivity.this.tempcu.setF_inv(6);
                } else {
                    ControlUnitActivity.this.tempcu.setF_est(6);
                }
                ControlUnitActivity.this.saveData();
            }
        };
        AnonymousClass16 r10 = new Runnable() {
            public void run() {
                ControlUnitActivity.this.dismissdialog();
                if (z) {
                    ControlUnitActivity.this.tempcu.setF_inv(7);
                } else {
                    ControlUnitActivity.this.tempcu.setF_est(7);
                }
                ControlUnitActivity.this.saveData();
            }
        };
        arrayList.add(getResources().getString(R.string.cu_menuFunzOff));
        arrayList3.add(r0);
        arrayList.add(getResources().getString(R.string.cu_menuFunzCanalizzata));
        arrayList3.add(r1);
        arrayList.add(getResources().getString(R.string.cu_menuFunzEv));
        arrayList3.add(r2);
        arrayList.add(getResources().getString(R.string.cu_menuFunzEvCanalizzata));
        arrayList3.add(r5);
        arrayList.add(getResources().getString(R.string.cu_menuFunzFancoil));
        arrayList3.add(r7);
        arrayList.add(getResources().getString(R.string.cu_menuFunzFancoilCanalizzata));
        arrayList3.add(r8);
        arrayList.add(getResources().getString(R.string.cu_menuFunzFancoilEv));
        arrayList3.add(r9);
        arrayList.add(getResources().getString(R.string.cu_menuFunzFancoilEvCanalizzata));
        arrayList3.add(r10);
        arrayList2.add("");
        arrayList2.add("");
        arrayList2.add("");
        arrayList2.add("");
        arrayList2.add("");
        arrayList2.add("");
        arrayList2.add("");
        arrayList2.add("");
        String string = getResources().getString(R.string.cu_menuFunzEst);
        if (z) {
            string = getResources().getString(R.string.cu_menuFunzInv);
        }
        this.bundlePopUp = createPopUp(true, string, arrayList, arrayList2, (List<String>) null, arrayList3, z ? this.cu.getF_inv() : this.cu.getF_est(), true);
    }

    public void createPopUpChangeTCan(boolean z) {
        int i;
        int i2;
        AnonymousClass17 r6 = new Runnable() {
            public void run() {
                ControlUnitActivity.this.dismissdialog();
                ControlUnitActivity.this.saveData();
            }
        };
        int t_can = this.cu.getT_can();
        if (t_can == 0) {
            t_can = 23;
        }
        if (this.cu.getUnitOfMesure() == 1) {
            int i3 = Constants.tempminFCan;
            int i4 = Constants.tempmaxFCan;
            t_can = (int) Functions.fromCtoF((double) t_can);
            i2 = i3;
            i = i4;
        } else {
            i2 = 18;
            i = 30;
        }
        this.bundlePopUp = createChangeNumberPopUp(getResources().getString(R.string.cu_titleTemp), 0, String.valueOf(t_can), i2, i, r6, (TextView) null);
    }

    public void createPopUpInfoCu() {
        String str = getResources().getString(R.string.cu_idCU) + " " + this.cu.getSerial();
        this.bundlePopUp = createYesNoPopUp(getResources().getString(R.string.cu_infoCentralinaTitle), str, "", getResources().getString(R.string.general_OK), new Runnable() {
            public void run() {
            }
        }, new Runnable() {
            public void run() {
                ControlUnitActivity.this.dismissdialog();
            }
        });
    }

    public void btnOnOff(View view) {
        if (this.cu.getIsOff()) {
            this.tempcu.setIsOff(false);
        } else {
            this.tempcu.setIsOff(true);
        }
        saveData();
    }

    public void bntEstate(View view) {
        boolean z = false;
        if (this.tempcu.getIsCooling()) {
            this.tempcu.setIsCooling(false);
            z = true;
        }
        if (this.cu.getIr_present() == 1) {
            createPopUpChangeTCan(true);
            openDialogFragment(this.bundlePopUp);
        } else if (z) {
            saveData();
        }
    }

    public void btnRaff(View view) {
        createPoUpOperatingMOde();
        openDialogFragment(this.bundlePopUp);
    }

    public void getCuState(JSONObject jSONObject, boolean z) {
        boolean z2 = false;
        this.gettingstate = false;
        if (jSONObject != null) {
            if (z) {
                try {
                    this.cu = ControlUnit.mergeFromGetState(this.cu, ControlUnit.getCuFromJSONFOffline(jSONObject));
                    if (this.cu.getF_est() >= 0 || this.cu.getF_inv() >= 0) {
                        z2 = true;
                    }
                    if (z2 != this.hasfinest) {
                        this.hasfinest = z2;
                        updatemenu();
                    }
                } catch (Exception e) {
                    Log.d("prova", e.toString());
                }
            } else {
                this.cu = ControlUnit.mergeFromGetState(this.cu, ControlUnit.getCuFromJSONFromServer(jSONObject, this));
                if (this.cu.getF_est() >= 0 || this.cu.getF_inv() >= 0) {
                    z2 = true;
                }
                if (z2 != this.hasfinest) {
                    this.hasfinest = z2;
                    updatemenu();
                }
                if (this.cu.getIp() != null) {
                    MySocket.initInstance(this.activity, this.activity, true);
                }
                if (Constants.modesviluppatore) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            try {
                                ControlUnitActivity.this.lblLastSync.setText(ControlUnitActivity.this.cu.getLastSyncUpdate());
                            } catch (Exception unused) {
                            }
                        }
                    });
                }
            }
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    ControlUnitActivity.this.loadData(false);
                    ControlUnitActivity.this.firstcu = false;
                    ControlUnitActivity.this.firtCalltoGetState = false;
                }
            });
        } else if (!this.cu.isOffline()) {
            this.cu.setErrors(Functions.geterror(32));
            this.cu.setNumError(1);
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    ControlUnitActivity.this.loadData(true);
                }
            });
        }
    }

    public void saveData() {
        if (Constants.ISDEMO) {
            this.cu.setName(this.tempcu.getName());
            this.cu.setIsOff(this.tempcu.getIsOff());
            this.cu.setOperatingMode(this.tempcu.getOperatingMode());
            this.cu.setZone(this.tempcu.getZone());
            this.cu.setIsCooling(this.tempcu.getIsCooling());
            this.cu.setT_can(this.tempcu.getT_can());
            this.cu.setF_inv(this.tempcu.getF_inv());
            this.cu.setF_est(this.tempcu.getF_est());
            DataClass.getInstance(this).controlunit_list.set(indexcudemo, this.cu);
            this.zoneAdapter.notifyDataSetChanged();
            showProgress();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (Exception unused) {
                    }
                    ControlUnitActivity.this.hideProgress();
                    ControlUnitActivity.this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            ControlUnitActivity.this.loadData(false);
                        }
                    });
                }
            }).start();
            return;
        }
        final String str = "";
        try {
            if (!onlyname()) {
                this.tempcu.setOffline(this.cu.isOffline());
                str = this.tempcu.update_CU_command();
            }
            if (this.cu.isOffline()) {
                new Thread(new Runnable() {
                    public void run() {
                        String commandToCU;
                        ControlUnitActivity.this.sendingstate = true;
                        try {
                            ControlUnitActivity.this.cu.setName(ControlUnitActivity.this.tempcu.getName());
                            ControlUnitActivity controlUnitActivity = ControlUnitActivity.this;
                            controlUnitActivity.changeTitle(controlUnitActivity.cu.getName());
                            ControlUnit.saveCuInPref(ControlUnitActivity.this.cu, ControlUnitActivity.this.activity);
                            if (!ControlUnitActivity.this.onlyname() && (commandToCU = MySocket.commandToCU(str, Constants.ip, Constants.port, true, true, true)) != null && !commandToCU.isEmpty()) {
                                JSONObject jSONObject = new JSONObject(commandToCU);
                                if (jSONObject.has(Constants.JSON_RES) && jSONObject.getInt(Constants.JSON_RES) == 1) {
                                    ControlUnitActivity.this.activity.runOnUiThread(new Runnable() {
                                        public void run() {
                                            Functions.makeNormalToast(ControlUnitActivity.this.activity, ControlUnitActivity.this.getResources().getString(R.string.msg_commandOk));
                                        }
                                    });
                                }
                            }
                        } catch (Exception unused) {
                        }
                        ControlUnitActivity.this.sendingstate = false;
                    }
                }).start();
            } else {
                new Thread(new Runnable() {
                    /* JADX WARNING: Removed duplicated region for block: B:24:0x00dd A[Catch:{ JSONException -> 0x0124 }] */
                    /* JADX WARNING: Removed duplicated region for block: B:25:0x00f4 A[Catch:{ JSONException -> 0x0124 }] */
                    /* JADX WARNING: Removed duplicated region for block: B:28:0x010b A[Catch:{ JSONException -> 0x0124 }] */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public void run() {
                        /*
                            r12 = this;
                            r0 = 0
                            it.tecnosystemi.TS.Activity.ControlUnitActivity r1 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ Exception -> 0x012d }
                            r2 = 1
                            r1.sendingstate = r2     // Catch:{ Exception -> 0x012d }
                            it.tecnosystemi.TS.Activity.ControlUnitActivity r1 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ Exception -> 0x012d }
                            it.tecnosystemi.TS.Model.ControlUnit r1 = r1.cu     // Catch:{ Exception -> 0x012d }
                            java.lang.String r1 = r1.getIp()     // Catch:{ Exception -> 0x012d }
                            if (r1 == 0) goto L_0x007d
                            java.lang.String r1 = r0     // Catch:{ Exception -> 0x012d }
                            boolean r1 = r1.isEmpty()     // Catch:{ Exception -> 0x012d }
                            if (r1 != 0) goto L_0x007d
                            it.tecnosystemi.TS.Activity.ControlUnitActivity r1 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ Exception -> 0x012d }
                            it.tecnosystemi.TS.Model.ControlUnit r1 = r1.cu     // Catch:{ Exception -> 0x012d }
                            java.lang.String r1 = r1.getIp()     // Catch:{ Exception -> 0x012d }
                            boolean r1 = r1.isEmpty()     // Catch:{ Exception -> 0x012d }
                            if (r1 != 0) goto L_0x007d
                            it.tecnosystemi.TS.Activity.ControlUnitActivity r1 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ Exception -> 0x012d }
                            it.tecnosystemi.TS.Activity.BaseActivity r1 = r1.activity     // Catch:{ Exception -> 0x012d }
                            it.tecnosystemi.TS.Activity.ControlUnitActivity r3 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ Exception -> 0x012d }
                            it.tecnosystemi.TS.Activity.BaseActivity r3 = r3.activity     // Catch:{ Exception -> 0x012d }
                            it.tecnosystemi.TS.Commands.MySocket.initInstance(r1, r3, r0)     // Catch:{ Exception -> 0x012d }
                            java.lang.String r4 = r0     // Catch:{ Exception -> 0x012d }
                            it.tecnosystemi.TS.Activity.ControlUnitActivity r1 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ Exception -> 0x012d }
                            it.tecnosystemi.TS.Model.ControlUnit r1 = r1.cu     // Catch:{ Exception -> 0x012d }
                            java.lang.String r5 = r1.getIp()     // Catch:{ Exception -> 0x012d }
                            int r6 = it.tecnosystemi.TS.Utils.Constants.port     // Catch:{ Exception -> 0x012d }
                            r8 = 1
                            r9 = 0
                            r7 = 0
                            java.lang.String r1 = it.tecnosystemi.TS.Commands.MySocket.commandToCU(r4, r5, r6, r7, r8, r9)     // Catch:{ Exception -> 0x012d }
                            if (r1 == 0) goto L_0x007d
                            boolean r3 = r1.isEmpty()     // Catch:{ Exception -> 0x012d }
                            if (r3 != 0) goto L_0x007d
                            org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ Exception -> 0x012d }
                            r3.<init>(r1)     // Catch:{ Exception -> 0x012d }
                            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_RES     // Catch:{ Exception -> 0x012d }
                            boolean r1 = r3.has(r1)     // Catch:{ Exception -> 0x012d }
                            if (r1 == 0) goto L_0x007d
                            java.lang.String r1 = it.tecnosystemi.TS.Utils.Constants.JSON_RES     // Catch:{ Exception -> 0x012d }
                            int r1 = r3.getInt(r1)     // Catch:{ Exception -> 0x012d }
                            java.lang.String r4 = it.tecnosystemi.TS.Utils.Constants.JSON_RES     // Catch:{ Exception -> 0x012d }
                            int r3 = r3.getInt(r4)     // Catch:{ Exception -> 0x012d }
                            if (r3 != r2) goto L_0x006c
                            it.tecnosystemi.TS.Activity.ControlUnitActivity r3 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ Exception -> 0x012d }
                            r3.sendingstate = r0     // Catch:{ Exception -> 0x012d }
                            goto L_0x007e
                        L_0x006c:
                            it.tecnosystemi.TS.Activity.ControlUnitActivity r1 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ Exception -> 0x012d }
                            it.tecnosystemi.TS.Activity.BaseActivity r1 = r1.activity     // Catch:{ Exception -> 0x012d }
                            it.tecnosystemi.TS.Activity.ControlUnitActivity$25$1 r2 = new it.tecnosystemi.TS.Activity.ControlUnitActivity$25$1     // Catch:{ Exception -> 0x012d }
                            r2.<init>()     // Catch:{ Exception -> 0x012d }
                            r1.runOnUiThread(r2)     // Catch:{ Exception -> 0x012d }
                            it.tecnosystemi.TS.Activity.ControlUnitActivity r1 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ Exception -> 0x012d }
                            r1.sendingstate = r0     // Catch:{ Exception -> 0x012d }
                            return
                        L_0x007d:
                            r1 = -1
                        L_0x007e:
                            org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ Exception -> 0x012d }
                            r3.<init>()     // Catch:{ Exception -> 0x012d }
                            java.lang.String r4 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_SERIAL     // Catch:{ JSONException -> 0x0124 }
                            it.tecnosystemi.TS.Activity.ControlUnitActivity r5 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ JSONException -> 0x0124 }
                            it.tecnosystemi.TS.Model.ControlUnit r5 = r5.cu     // Catch:{ JSONException -> 0x0124 }
                            java.lang.String r5 = r5.getSerial()     // Catch:{ JSONException -> 0x0124 }
                            r3.put(r4, r5)     // Catch:{ JSONException -> 0x0124 }
                            java.lang.String r4 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_NAME     // Catch:{ JSONException -> 0x0124 }
                            it.tecnosystemi.TS.Activity.ControlUnitActivity r5 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ JSONException -> 0x0124 }
                            it.tecnosystemi.TS.Model.ControlUnit r5 = r5.tempcu     // Catch:{ JSONException -> 0x0124 }
                            java.lang.String r5 = r5.getName()     // Catch:{ JSONException -> 0x0124 }
                            r3.put(r4, r5)     // Catch:{ JSONException -> 0x0124 }
                            java.lang.String r4 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_PIN     // Catch:{ JSONException -> 0x0124 }
                            it.tecnosystemi.TS.Activity.ControlUnitActivity r5 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ JSONException -> 0x0124 }
                            it.tecnosystemi.TS.Model.ControlUnit r5 = r5.cu     // Catch:{ JSONException -> 0x0124 }
                            java.lang.String r5 = r5.getPin()     // Catch:{ JSONException -> 0x0124 }
                            r3.put(r4, r5)     // Catch:{ JSONException -> 0x0124 }
                            java.lang.String r4 = "Cmd"
                            java.lang.String r5 = r0     // Catch:{ JSONException -> 0x0124 }
                            r3.put(r4, r5)     // Catch:{ JSONException -> 0x0124 }
                            r4 = 2
                            java.lang.String[] r11 = new java.lang.String[r4]     // Catch:{ JSONException -> 0x0124 }
                            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0124 }
                            r4.<init>()     // Catch:{ JSONException -> 0x0124 }
                            it.tecnosystemi.TS.Activity.ControlUnitActivity r5 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ JSONException -> 0x0124 }
                            android.content.res.Resources r5 = r5.getResources()     // Catch:{ JSONException -> 0x0124 }
                            int r6 = it.tecnosystemi.TS.R.string.uriWebService_POLARIS     // Catch:{ JSONException -> 0x0124 }
                            java.lang.String r5 = r5.getString(r6)     // Catch:{ JSONException -> 0x0124 }
                            r4.append(r5)     // Catch:{ JSONException -> 0x0124 }
                            it.tecnosystemi.TS.Activity.ControlUnitActivity r5 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ JSONException -> 0x0124 }
                            android.content.res.Resources r5 = r5.getResources()     // Catch:{ JSONException -> 0x0124 }
                            int r6 = it.tecnosystemi.TS.R.string.uri_UpdCuState     // Catch:{ JSONException -> 0x0124 }
                            java.lang.String r5 = r5.getString(r6)     // Catch:{ JSONException -> 0x0124 }
                            r4.append(r5)     // Catch:{ JSONException -> 0x0124 }
                            java.lang.String r4 = r4.toString()     // Catch:{ JSONException -> 0x0124 }
                            if (r1 != r2) goto L_0x00f4
                            java.lang.String r1 = "0"
                            r11[r0] = r1     // Catch:{ JSONException -> 0x0124 }
                            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0124 }
                            r1.<init>()     // Catch:{ JSONException -> 0x0124 }
                            r1.append(r4)     // Catch:{ JSONException -> 0x0124 }
                            java.lang.String r4 = "?create_command=false"
                            r1.append(r4)     // Catch:{ JSONException -> 0x0124 }
                            java.lang.String r1 = r1.toString()     // Catch:{ JSONException -> 0x0124 }
                            r9 = r1
                            goto L_0x00f9
                        L_0x00f4:
                            java.lang.String r1 = "1"
                            r11[r0] = r1     // Catch:{ JSONException -> 0x0124 }
                            r9 = r4
                        L_0x00f9:
                            it.tecnosystemi.TS.Activity.ControlUnitActivity r1 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ JSONException -> 0x0124 }
                            it.tecnosystemi.TS.Model.ControlUnit r1 = r1.cu     // Catch:{ JSONException -> 0x0124 }
                            java.lang.String r1 = r1.getFWVer()     // Catch:{ JSONException -> 0x0124 }
                            r11[r2] = r1     // Catch:{ JSONException -> 0x0124 }
                            it.tecnosystemi.TS.Activity.ControlUnitActivity r1 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ JSONException -> 0x0124 }
                            boolean r1 = r1.onlyname()     // Catch:{ JSONException -> 0x0124 }
                            if (r1 == 0) goto L_0x010f
                            java.lang.String r1 = "2"
                            r11[r0] = r1     // Catch:{ JSONException -> 0x0124 }
                        L_0x010f:
                            it.tecnosystemi.TS.Threads.ThreadWebService r1 = new it.tecnosystemi.TS.Threads.ThreadWebService     // Catch:{ JSONException -> 0x0124 }
                            it.tecnosystemi.TS.Activity.ControlUnitActivity r2 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ JSONException -> 0x0124 }
                            it.tecnosystemi.TS.Activity.BaseActivity r6 = r2.activity     // Catch:{ JSONException -> 0x0124 }
                            java.lang.String r10 = r3.toString()     // Catch:{ JSONException -> 0x0124 }
                            r7 = 1
                            r8 = 13
                            r5 = r1
                            r5.<init>(r6, r7, r8, r9, r10, r11)     // Catch:{ JSONException -> 0x0124 }
                            r1.start()     // Catch:{ JSONException -> 0x0124 }
                            goto L_0x0131
                        L_0x0124:
                            r1 = move-exception
                            r1.printStackTrace()     // Catch:{ Exception -> 0x012d }
                            it.tecnosystemi.TS.Activity.ControlUnitActivity r1 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ Exception -> 0x012d }
                            r1.sendingstate = r0     // Catch:{ Exception -> 0x012d }
                            goto L_0x0131
                        L_0x012d:
                            it.tecnosystemi.TS.Activity.ControlUnitActivity r1 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this
                            r1.sendingstate = r0
                        L_0x0131:
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.ControlUnitActivity.AnonymousClass25.run():void");
                    }
                }).start();
            }
        } catch (Exception e) {
            Log.d("ErrCu", e.toString());
        }
    }

    /* access modifiers changed from: private */
    public boolean parseOfflineRes(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.has("c")) {
                jSONObject.getString(Constants.JSON_OFFLINE_COMMAND_C);
                return true;
            }
        } catch (Exception unused) {
        }
        return false;
    }

    /* access modifiers changed from: private */
    public boolean onlyname() {
        return this.cu.getIsOff() == this.tempcu.getIsOff() && this.cu.getIsCooling() == this.tempcu.getIsCooling() && this.cu.getOperatingMode() == this.tempcu.getOperatingMode() && this.cu.getT_can() == this.tempcu.getT_can() && this.cu.getF_est() == this.tempcu.getF_est() && this.cu.getF_inv() == this.tempcu.getF_inv();
    }

    public void saveData(final int i) {
        try {
            if (Constants.ISDEMO) {
                this.cu.setName(this.tempcu.getName());
                this.cu.setIsOff(this.tempcu.getIsOff());
                this.cu.setOperatingMode(this.tempcu.getOperatingMode());
                this.cu.setZone(this.tempcu.getZone());
                this.cu.setIsCooling(this.tempcu.getIsCooling());
                DataClass.getInstance(this).controlunit_list.set(indexcudemo, this.cu);
                this.zoneAdapter.notifyDataSetChanged();
                showProgress();
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (Exception unused) {
                        }
                        ControlUnitActivity.this.hideProgress();
                        ControlUnitActivity.this.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                ControlUnitActivity.this.loadData(false);
                            }
                        });
                    }
                }).start();
                return;
            }
            final JSONObject update_ZONA_Command = this.tempcu.getZone().get(i).update_ZONA_Command();
            if (this.cu.isOffline()) {
                update_ZONA_Command.put(Constants.INTENT_PIN, this.cu.getPinOffline());
                final String jSONObject = update_ZONA_Command.toString();
                ControlUnit.saveCuInPref(this.tempcu, this);
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            String commandToCU = MySocket.commandToCU(jSONObject, Constants.ip, Constants.port, false, true, true);
                            if (commandToCU != null && !commandToCU.isEmpty()) {
                                JSONObject jSONObject = new JSONObject(commandToCU);
                                if (jSONObject.has(Constants.JSON_RES) && jSONObject.getInt(Constants.JSON_RES) == 1) {
                                    ControlUnitActivity.this.activity.runOnUiThread(new Runnable() {
                                        public void run() {
                                            Functions.makeNormalToast(ControlUnitActivity.this.activity, ControlUnitActivity.this.getResources().getString(R.string.msg_commandOk));
                                        }
                                    });
                                    boolean unused = ControlUnitActivity.this.parseOfflineRes(commandToCU);
                                }
                            }
                        } catch (Exception unused2) {
                        }
                    }
                }).start();
                return;
            }
            new Thread(new Runnable() {
                /* JADX WARNING: Removed duplicated region for block: B:21:0x00f9 A[Catch:{ JSONException -> 0x0133 }] */
                /* JADX WARNING: Removed duplicated region for block: B:22:0x010f A[Catch:{ JSONException -> 0x0133 }] */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void run() {
                    /*
                        r12 = this;
                        org.json.JSONObject r0 = r0     // Catch:{ Exception -> 0x0137 }
                        java.lang.String r1 = "pin"
                        it.tecnosystemi.TS.Activity.ControlUnitActivity r2 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ Exception -> 0x0137 }
                        it.tecnosystemi.TS.Model.ControlUnit r2 = r2.cu     // Catch:{ Exception -> 0x0137 }
                        java.lang.String r2 = r2.getPin()     // Catch:{ Exception -> 0x0137 }
                        r0.put(r1, r2)     // Catch:{ Exception -> 0x0137 }
                        it.tecnosystemi.TS.Activity.ControlUnitActivity r0 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ Exception -> 0x0137 }
                        it.tecnosystemi.TS.Model.ControlUnit r0 = r0.cu     // Catch:{ Exception -> 0x0137 }
                        java.lang.String r0 = r0.getIp()     // Catch:{ Exception -> 0x0137 }
                        r1 = 1
                        if (r0 == 0) goto L_0x007b
                        it.tecnosystemi.TS.Activity.ControlUnitActivity r0 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ Exception -> 0x0137 }
                        it.tecnosystemi.TS.Model.ControlUnit r0 = r0.cu     // Catch:{ Exception -> 0x0137 }
                        java.lang.String r0 = r0.getIp()     // Catch:{ Exception -> 0x0137 }
                        boolean r0 = r0.isEmpty()     // Catch:{ Exception -> 0x0137 }
                        if (r0 != 0) goto L_0x007b
                        it.tecnosystemi.TS.Activity.ControlUnitActivity r0 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ Exception -> 0x0137 }
                        it.tecnosystemi.TS.Activity.BaseActivity r0 = r0.activity     // Catch:{ Exception -> 0x0137 }
                        it.tecnosystemi.TS.Activity.ControlUnitActivity r2 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ Exception -> 0x0137 }
                        it.tecnosystemi.TS.Activity.BaseActivity r2 = r2.activity     // Catch:{ Exception -> 0x0137 }
                        it.tecnosystemi.TS.Commands.MySocket.initInstance(r0, r2, r1)     // Catch:{ Exception -> 0x0137 }
                        org.json.JSONObject r0 = r0     // Catch:{ Exception -> 0x0137 }
                        java.lang.String r2 = r0.toString()     // Catch:{ Exception -> 0x0137 }
                        it.tecnosystemi.TS.Activity.ControlUnitActivity r0 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ Exception -> 0x0137 }
                        it.tecnosystemi.TS.Model.ControlUnit r0 = r0.cu     // Catch:{ Exception -> 0x0137 }
                        java.lang.String r3 = r0.getIp()     // Catch:{ Exception -> 0x0137 }
                        int r4 = it.tecnosystemi.TS.Utils.Constants.port     // Catch:{ Exception -> 0x0137 }
                        r6 = 1
                        r7 = 0
                        r5 = 0
                        java.lang.String r0 = it.tecnosystemi.TS.Commands.MySocket.commandToCU(r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0137 }
                        if (r0 == 0) goto L_0x007b
                        boolean r2 = r0.isEmpty()     // Catch:{ Exception -> 0x0137 }
                        if (r2 != 0) goto L_0x007b
                        org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ Exception -> 0x0137 }
                        r2.<init>(r0)     // Catch:{ Exception -> 0x0137 }
                        java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.JSON_RES     // Catch:{ Exception -> 0x0137 }
                        boolean r0 = r2.has(r0)     // Catch:{ Exception -> 0x0137 }
                        if (r0 == 0) goto L_0x007b
                        java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.JSON_RES     // Catch:{ Exception -> 0x0137 }
                        int r0 = r2.getInt(r0)     // Catch:{ Exception -> 0x0137 }
                        java.lang.String r3 = it.tecnosystemi.TS.Utils.Constants.JSON_RES     // Catch:{ Exception -> 0x0137 }
                        int r2 = r2.getInt(r3)     // Catch:{ Exception -> 0x0137 }
                        if (r2 != r1) goto L_0x006e
                        goto L_0x007c
                    L_0x006e:
                        it.tecnosystemi.TS.Activity.ControlUnitActivity r0 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ Exception -> 0x0137 }
                        it.tecnosystemi.TS.Activity.BaseActivity r0 = r0.activity     // Catch:{ Exception -> 0x0137 }
                        it.tecnosystemi.TS.Activity.ControlUnitActivity$28$1 r1 = new it.tecnosystemi.TS.Activity.ControlUnitActivity$28$1     // Catch:{ Exception -> 0x0137 }
                        r1.<init>()     // Catch:{ Exception -> 0x0137 }
                        r0.runOnUiThread(r1)     // Catch:{ Exception -> 0x0137 }
                        return
                    L_0x007b:
                        r0 = -1
                    L_0x007c:
                        org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ Exception -> 0x0137 }
                        r2.<init>()     // Catch:{ Exception -> 0x0137 }
                        java.lang.String r3 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_SERIAL     // Catch:{ JSONException -> 0x0133 }
                        it.tecnosystemi.TS.Activity.ControlUnitActivity r4 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ JSONException -> 0x0133 }
                        it.tecnosystemi.TS.Model.ControlUnit r4 = r4.cu     // Catch:{ JSONException -> 0x0133 }
                        java.lang.String r4 = r4.getSerial()     // Catch:{ JSONException -> 0x0133 }
                        r2.put(r3, r4)     // Catch:{ JSONException -> 0x0133 }
                        java.lang.String r3 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_NAME     // Catch:{ JSONException -> 0x0133 }
                        it.tecnosystemi.TS.Activity.ControlUnitActivity r4 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ JSONException -> 0x0133 }
                        it.tecnosystemi.TS.Model.ControlUnit r4 = r4.tempcu     // Catch:{ JSONException -> 0x0133 }
                        java.lang.String r4 = r4.getName()     // Catch:{ JSONException -> 0x0133 }
                        r2.put(r3, r4)     // Catch:{ JSONException -> 0x0133 }
                        java.lang.String r3 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_PIN     // Catch:{ JSONException -> 0x0133 }
                        it.tecnosystemi.TS.Activity.ControlUnitActivity r4 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ JSONException -> 0x0133 }
                        it.tecnosystemi.TS.Model.ControlUnit r4 = r4.cu     // Catch:{ JSONException -> 0x0133 }
                        java.lang.String r4 = r4.getPin()     // Catch:{ JSONException -> 0x0133 }
                        r2.put(r3, r4)     // Catch:{ JSONException -> 0x0133 }
                        java.lang.String r3 = it.tecnosystemi.TS.Utils.Constants.JSON_ZONE_ID     // Catch:{ JSONException -> 0x0133 }
                        it.tecnosystemi.TS.Activity.ControlUnitActivity r4 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ JSONException -> 0x0133 }
                        it.tecnosystemi.TS.Model.ControlUnit r4 = r4.tempcu     // Catch:{ JSONException -> 0x0133 }
                        java.util.List r4 = r4.getZone()     // Catch:{ JSONException -> 0x0133 }
                        int r5 = r4     // Catch:{ JSONException -> 0x0133 }
                        java.lang.Object r4 = r4.get(r5)     // Catch:{ JSONException -> 0x0133 }
                        it.tecnosystemi.TS.Model.Zona r4 = (it.tecnosystemi.TS.Model.Zona) r4     // Catch:{ JSONException -> 0x0133 }
                        int r4 = r4.getZoneId()     // Catch:{ JSONException -> 0x0133 }
                        r2.put(r3, r4)     // Catch:{ JSONException -> 0x0133 }
                        java.lang.String r3 = "Cmd"
                        org.json.JSONObject r4 = r0     // Catch:{ JSONException -> 0x0133 }
                        java.lang.String r4 = r4.toString()     // Catch:{ JSONException -> 0x0133 }
                        r2.put(r3, r4)     // Catch:{ JSONException -> 0x0133 }
                        java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0133 }
                        r3.<init>()     // Catch:{ JSONException -> 0x0133 }
                        it.tecnosystemi.TS.Activity.ControlUnitActivity r4 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ JSONException -> 0x0133 }
                        android.content.res.Resources r4 = r4.getResources()     // Catch:{ JSONException -> 0x0133 }
                        int r5 = it.tecnosystemi.TS.R.string.uriWebService_POLARIS     // Catch:{ JSONException -> 0x0133 }
                        java.lang.String r4 = r4.getString(r5)     // Catch:{ JSONException -> 0x0133 }
                        r3.append(r4)     // Catch:{ JSONException -> 0x0133 }
                        it.tecnosystemi.TS.Activity.ControlUnitActivity r4 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ JSONException -> 0x0133 }
                        android.content.res.Resources r4 = r4.getResources()     // Catch:{ JSONException -> 0x0133 }
                        int r5 = it.tecnosystemi.TS.R.string.uri_UpdZonaState     // Catch:{ JSONException -> 0x0133 }
                        java.lang.String r4 = r4.getString(r5)     // Catch:{ JSONException -> 0x0133 }
                        r3.append(r4)     // Catch:{ JSONException -> 0x0133 }
                        java.lang.String r3 = r3.toString()     // Catch:{ JSONException -> 0x0133 }
                        r4 = 2
                        java.lang.String[] r11 = new java.lang.String[r4]     // Catch:{ JSONException -> 0x0133 }
                        r4 = 0
                        if (r0 != r1) goto L_0x010f
                        java.lang.String r0 = "0"
                        r11[r4] = r0     // Catch:{ JSONException -> 0x0133 }
                        java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ JSONException -> 0x0133 }
                        r0.<init>()     // Catch:{ JSONException -> 0x0133 }
                        r0.append(r3)     // Catch:{ JSONException -> 0x0133 }
                        java.lang.String r3 = "?create_command=false"
                        r0.append(r3)     // Catch:{ JSONException -> 0x0133 }
                        java.lang.String r3 = r0.toString()     // Catch:{ JSONException -> 0x0133 }
                        goto L_0x0113
                    L_0x010f:
                        java.lang.String r0 = "1"
                        r11[r4] = r0     // Catch:{ JSONException -> 0x0133 }
                    L_0x0113:
                        r9 = r3
                        it.tecnosystemi.TS.Activity.ControlUnitActivity r0 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ JSONException -> 0x0133 }
                        it.tecnosystemi.TS.Model.ControlUnit r0 = r0.cu     // Catch:{ JSONException -> 0x0133 }
                        java.lang.String r0 = r0.getFWVer()     // Catch:{ JSONException -> 0x0133 }
                        r11[r1] = r0     // Catch:{ JSONException -> 0x0133 }
                        it.tecnosystemi.TS.Threads.ThreadWebService r0 = new it.tecnosystemi.TS.Threads.ThreadWebService     // Catch:{ JSONException -> 0x0133 }
                        it.tecnosystemi.TS.Activity.ControlUnitActivity r1 = it.tecnosystemi.TS.Activity.ControlUnitActivity.this     // Catch:{ JSONException -> 0x0133 }
                        it.tecnosystemi.TS.Activity.BaseActivity r6 = r1.activity     // Catch:{ JSONException -> 0x0133 }
                        java.lang.String r10 = r2.toString()     // Catch:{ JSONException -> 0x0133 }
                        r7 = 1
                        r8 = 13
                        r5 = r0
                        r5.<init>(r6, r7, r8, r9, r10, r11)     // Catch:{ JSONException -> 0x0133 }
                        r0.start()     // Catch:{ JSONException -> 0x0133 }
                        goto L_0x0137
                    L_0x0133:
                        r0 = move-exception
                        r0.printStackTrace()     // Catch:{ Exception -> 0x0137 }
                    L_0x0137:
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.ControlUnitActivity.AnonymousClass28.run():void");
                }
            }).start();
        } catch (Exception unused) {
        }
    }

    public void cancellacentralina() {
        AnonymousClass29 r6 = new Runnable() {
            public void run() {
                int i;
                if (ControlUnitActivity.this.cu.isOffline()) {
                    ControlUnit.deleteCufromPref(ControlUnitActivity.this.cu.getSerial(), ControlUnitActivity.this.activity);
                    ControlUnitActivity.this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            ControlUnitActivity.this.activity.finish();
                        }
                    });
                } else {
                    int i2 = 0;
                    ControlUnitActivity.this.activity.getSharedPreferences(Constants.PREF_REGID_NAME, 0);
                    if (Functions.getNotificationPermision(ControlUnitActivity.this.activity)) {
                        FirebaseInstanceId.getInstance().getToken();
                    }
                    Device.deleteDevFromPref(TSDeviceListActivity.SELECTED_DEV, ControlUnitActivity.this.activity);
                    if (ControlUnitActivity.this.cu == null || !ControlUnitActivity.this.cu.isOffline()) {
                        Device_OP.DeviceOp deviceOp = new Device_OP.DeviceOp();
                        deviceOp.setDeviceID(TSDeviceListActivity.SELECTED_DEV.getLVDV_Id());
                        deviceOp.setToken(ControlUnitActivity.this.activity.FirebaseToken);
                        deviceOp.setPlatform(Constants.NOTIFIC_PLAT);
                        new ThreadWebService(ControlUnitActivity.this.activity, 2, 10, ControlUnitActivity.this.getResources().getString(R.string.uriWebService_POLARIS) + ControlUnitActivity.this.getResources().getString(R.string.uri_DeleteDevice), new Gson().toJson((Object) deviceOp), new String[]{TSDeviceListActivity.SELECTED_DEV.getSerial()}).start();
                    } else {
                        ControlUnit.deleteCufromPref(ControlUnitActivity.this.cu.getSerial(), ControlUnitActivity.this.activity);
                        int i3 = 0;
                        while (true) {
                            i = -1;
                            if (i3 >= Constants.listaImpianti.size()) {
                                i3 = -1;
                                break;
                            } else if (Constants.listaImpianti.get(i3).getLVPL_Id() == TSDeviceListActivity.idSelected) {
                                break;
                            } else {
                                i3++;
                            }
                        }
                        while (true) {
                            if (i2 >= Constants.listaImpianti.get(i3).getListDevices().size()) {
                                break;
                            } else if (Constants.listaImpianti.get(i3).getListDevices().get(i2).getSerial().equals(TSDeviceListActivity.SELECTED_DEV.getSerial())) {
                                i = i2;
                                break;
                            } else {
                                i2++;
                            }
                        }
                        Constants.listaImpianti.get(i3).getListDevices().remove(i);
                        ControlUnitActivity.this.finish();
                    }
                }
                ControlUnitActivity.this.dismissdialog();
            }
        };
        AnonymousClass30 r5 = new Runnable() {
            public void run() {
                ControlUnitActivity.this.dismissdialog();
            }
        };
        openDialogFragment(createYesNoPopUp(getResources().getString(R.string.cu_deleteCUAlert_title), getResources().getString(R.string.cu_deleteCUAlert_text), getResources().getString(R.string.no), getResources().getString(R.string.yes), r5, r6));
    }

    public void changeZona(int i) {
        this.tempcu.getZone().get(i).setOff(!this.cu.getZone().get(i).isOff());
        saveData(i);
    }

    public void resetConn() {
        new ThreadWebService(this.activity, 0, 19, getResources().getString(R.string.uriWebService_POLARIS) + getResources().getString(R.string.uri_ResetConn) + "?cuSerial=" + this.cu.getSerial() + "&PIN=" + this.cu.getPin(), (String) null, (String[]) null).start();
        showProgress();
    }

    public void respResConn(JSONObject jSONObject) {
        hideProgress();
        if (jSONObject == null) {
            Functions.makeErrorToast(this, getString(R.string.errorSendCMDToServer));
        } else {
            Functions.makeNormalToast(this, getString(R.string.msg_commandOk));
        }
    }

    public void onResume() {
        super.onResume();
        refreshList();
        if (!Constants.ISDEMO) {
            checkPin();
            this.interrupt = false;
            this.gettingstate = false;
            inizializeGetState(this.cu.isOffline());
            this.getState.start();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        continueTrie = true;
        stopgetState();
    }

    public void onPause() {
        stopgetState();
        super.onPause();
    }

    public View getToolBar() {
        return findViewById(R.id.csa_toolbar);
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        JSONArray jSONArray;
        list.add(createMenuItem(true, getResources().getString(R.string.cu_menuRinomina), "", (String) null, new Runnable() {
            public void run() {
                ControlUnitActivity.this.dismissdialog();
                ControlUnitActivity.this.createPopUpRinominaCU();
                ControlUnitActivity controlUnitActivity = ControlUnitActivity.this;
                controlUnitActivity.openDialogFragment(controlUnitActivity.bundlePopUp);
            }
        }, false, false));
        list.add(createMenuItem(false, getResources().getString(R.string.cu_menuSetUM), "", (String) null, new Runnable() {
            public void run() {
                ControlUnitActivity.this.dismissdialog();
                ControlUnitActivity.this.createPopUpUnitMisura();
                ControlUnitActivity controlUnitActivity = ControlUnitActivity.this;
                controlUnitActivity.openDialogFragment(controlUnitActivity.bundlePopUp);
            }
        }, false, false));
        list.add(createMenuItem(false, getResources().getString(R.string.cu_menuSetOra), "", (String) null, new Runnable() {
            public void run() {
                ControlUnitActivity.this.dismissdialog();
                ControlUnitActivity.this.createPopUpDataOra();
                ControlUnitActivity controlUnitActivity = ControlUnitActivity.this;
                controlUnitActivity.openDialogFragment(controlUnitActivity.bundlePopUp);
            }
        }, false, false));
        list.add(createMenuItem(false, getResources().getString(R.string.cu_menuSetPin), "", (String) null, new Runnable() {
            public void run() {
                ControlUnitActivity.this.dismissdialog();
                Functions.makeNormalToast(ControlUnitActivity.this.activity, ControlUnitActivity.this.getResources().getString(R.string.cu_setPinInfo));
            }
        }, false, false));
        AnonymousClass35 r7 = new Runnable() {
            public void run() {
                try {
                    ControlUnitActivity.this.dismissdialog();
                } catch (Exception unused) {
                }
                BootloaderActivity.FROMCONFIG = false;
                ControlUnitActivity.this.gotobooloader = true;
                BaseActivity baseActivity = ControlUnitActivity.this.activity;
                new ThreadDowloadFirmWare(baseActivity, ControlUnitActivity.this.getResources().getString(R.string.uriWebService_POLARIS) + ControlUnitActivity.this.getResources().getString(R.string.uri_infoFrameWork)).start();
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
        if (this.hasfinest) {
            list.add(createMenuItem(false, getResources().getString(R.string.cu_menuFunzInv), "", (String) null, new Runnable() {
                public void run() {
                    ControlUnitActivity.this.dismissdialog();
                    ControlUnitActivity.this.createPopUpFunzInvEst(true);
                    ControlUnitActivity controlUnitActivity = ControlUnitActivity.this;
                    controlUnitActivity.openDialogFragment(controlUnitActivity.bundlePopUp);
                }
            }, false, false));
            list.add(createMenuItem(false, getResources().getString(R.string.cu_menuFunzEst), "", (String) null, new Runnable() {
                public void run() {
                    ControlUnitActivity.this.dismissdialog();
                    ControlUnitActivity.this.createPopUpFunzInvEst(false);
                    ControlUnitActivity controlUnitActivity = ControlUnitActivity.this;
                    controlUnitActivity.openDialogFragment(controlUnitActivity.bundlePopUp);
                }
            }, false, false));
        }
        if (is5x()) {
            list.add(createMenuItem(false, getResources().getString(R.string.cu_menuRestoreControlUnit), "", "", new Runnable() {
                public void run() {
                    if (ControlUnitActivity.this.myDialogFragment != null) {
                        ControlUnitActivity.this.dismissdialog();
                    }
                    ControlUnitActivity.this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            ControlUnitActivity.this.createRestoreConfig();
                        }
                    });
                }
            }, false, false));
        }
        list.add(createMenuItem(false, getResources().getString(R.string.cu_menuDeleteControlUnit), "", (String) null, new Runnable() {
            public void run() {
                ControlUnitActivity.this.dismissdialog();
                if (Constants.ISDEMO) {
                    Functions.makeNormalToast(ControlUnitActivity.this.activity, ControlUnitActivity.this.getResources().getString(R.string.cu_DemoVersion));
                } else {
                    ControlUnitActivity.this.cancellacentralina();
                }
            }
        }, false, false));
        list.add(createMenuItem(false, getResources().getString(R.string.cu_infoCentralina), "", (String) null, new Runnable() {
            public void run() {
                ControlUnitActivity.this.dismissdialog();
                ControlUnitActivity.this.createPopUpInfoCu();
                ControlUnitActivity controlUnitActivity = ControlUnitActivity.this;
                controlUnitActivity.openDialogFragment(controlUnitActivity.bundlePopUp);
            }
        }, false, false));
        new Runnable() {
            public void run() {
                ControlUnitActivity.this.dismissdialog();
                ControlUnitActivity.this.resetConn();
            }
        };
        Functions.IS4x(this.cu.getFWVer());
        return list;
    }

    public void createRestoreConfig() {
        AnonymousClass42 r5 = new Runnable() {
            public void run() {
                ControlUnitActivity.this.dismissdialog();
            }
        };
        AnonymousClass43 r6 = new Runnable() {
            public void run() {
                ControlUnitActivity.this.dismissdialog();
                ControlUnitActivity.this.showProgress();
                BaseActivity baseActivity = ControlUnitActivity.this.activity;
                new ThreadWebService(baseActivity, 0, 21, ControlUnitActivity.this.getResources().getString(R.string.uriWebService_POLARIS) + ControlUnitActivity.this.getResources().getString(R.string.uri_SendRestoreCmd) + "?cuSerial=" + ControlUnitActivity.this.cu.getSerial() + "&PIN=" + ControlUnitActivity.this.cu.getPin(), (String) null, (String[]) null).start();
            }
        };
        String string = getResources().getString(R.string.cu_restoreCUAlert_text);
        openDialogFragment(createYesNoPopUp(getResources().getString(R.string.cu_restoreCUAlert_title), string, getResources().getString(R.string.ba_cancel), getResources().getString(R.string.general_OK), r5, r6));
    }

    private boolean is5x() {
        try {
            if (this.cu.getFWVer() == null || Integer.parseInt(this.cu.getFWVer().split("\\.")[0]) < 5) {
                return false;
            }
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    public String setToolbarTitle() {
        return this.cu.getName().toUpperCase();
    }
}
