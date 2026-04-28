package it.tecnosystemi.TS.Activity.PICO;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Group;
import androidx.preference.PreferenceManager;
import com.google.gson.Gson;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Commands.CmdPICO;
import it.tecnosystemi.TS.Commands.PICOServer;
import it.tecnosystemi.TS.Commands.UDPSocket;
import it.tecnosystemi.TS.Model.Pico;
import it.tecnosystemi.TS.Model.Response;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Threads.ThreadWebService;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PicoCascataActivity extends BaseActivity {
    PicoCascataActivity activity;
    List<SlaveGUI> listGUI;
    String prexSlave;
    boolean showFW = false;
    List<Pico.Slave> slaves;

    public BaseActivity getActivity() {
        return this;
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        return list;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        JSONArray jSONArray;
        setContentView(R.layout.activity_pico_cascata);
        this.typeActStyle = 2;
        this.activity = this;
        this.slaves = new ArrayList();
        this.prexSlave = this.activity.getResources().getString(R.string.mc_slavePrefix);
        super.onCreate(bundle);
        hideMenuButton();
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
        if (jSONArray != null && jSONArray.length() > 0) {
            this.showFW = true;
        }
        setUpGui();
        getSlaves();
    }

    private void getSlaves() {
        if (Constants.ISDEMO) {
            this.slaves = new ArrayList();
            for (Pico.Slave next : PicoActivity.pico.getPicoSlave()) {
                if (next != null) {
                    this.slaves.add(next.Clone());
                } else {
                    this.slaves.add((Object) null);
                }
            }
            setUpSlave();
            return;
        }
        showProgress();
        final CmdPICO cmdPICO = new CmdPICO();
        cmdPICO.setCmd("get_slave");
        cmdPICO.setPin(PicoActivity.pico.getPin());
        if (PicoActivity.pico.getOffline().booleanValue()) {
            new Thread(new Runnable() {
                public void run() {
                    UDPSocket.startListening();
                    String sendCMD = UDPSocket.sendCMD(cmdPICO);
                    if (sendCMD != null) {
                        PicoCascataActivity.this.hideProgress();
                        try {
                            JSONObject jSONObject = new JSONObject(sendCMD);
                            if (jSONObject.has(Constants.JSON_RES) && jSONObject.getInt(Constants.JSON_RES) == 1) {
                                PicoCascataActivity.this.parsePicoSlaveCMD(jSONObject);
                            }
                        } catch (Exception unused) {
                        }
                    }
                }
            }).start();
            return;
        }
        PICOServer pICOServer = new PICOServer();
        pICOServer.setSerial(PicoActivity.pico.getSerial());
        pICOServer.setPin(PicoActivity.pico.getPin());
        pICOServer.setName(PicoActivity.pico.getName());
        cmdPICO.setIdp((long) PicoActivity.getIDP());
        pICOServer.setCmd(new Gson().toJson((Object) cmdPICO));
        new ThreadWebService(this.activity, 1, 30, getResources().getString(R.string.uriWebService_PICO) + getResources().getString(R.string.uri_SendPicoCmd), new Gson().toJson((Object) pICOServer), (String[]) null).start();
    }

    /* access modifiers changed from: private */
    public void parsePicoSlaveCMD(JSONObject jSONObject) {
        try {
            new ArrayList();
            if (jSONObject.has("list_slave")) {
                JSONArray jSONArray = jSONObject.getJSONArray("list_slave");
                ArrayList arrayList = new ArrayList();
                this.slaves = arrayList;
                arrayList.add((Object) null);
                this.slaves.add((Object) null);
                this.slaves.add((Object) null);
                this.slaves.add((Object) null);
                for (int i = 0; i < jSONArray.length(); i++) {
                    try {
                        JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                        Pico.Slave slave = new Pico.Slave();
                        slave.setId_slave(jSONObject2.getInt("id_slave"));
                        slave.setIp(jSONObject2.getString("ip"));
                        slave.setName(jSONObject2.getString("name"));
                        slave.setMode(jSONObject2.getInt("mode"));
                        slave.setVerso(jSONObject2.getInt("verso"));
                        slave.setFlag(jSONObject2.getInt("flag"));
                        slave.setFlag(jSONObject2.getInt("flag"));
                        if ((slave.getFlag() & 2) == 2) {
                            slave.setSet_stato(1);
                        } else {
                            slave.setSet_stato(2);
                        }
                        this.slaves.set(slave.getId_slave() - 1, slave);
                    } catch (Exception e) {
                        Log.d("ERR", e.toString());
                    }
                }
                setUpSlave();
            }
        } catch (Exception unused) {
        }
    }

    public void parseGetSetSlaveServer(Response response, int i) {
        if (response == null) {
            PicoCascataActivity picoCascataActivity = this.activity;
            Functions.makeErrorToast(picoCascataActivity, picoCascataActivity.getResources().getString(R.string.resCodeError));
        } else if (response.getHttpResponceCode() != 200) {
            Functions.makeErrorToast(this, getResources().getString(R.string.msg_commandKo));
        } else {
            try {
                JSONObject jSONObject = new JSONObject(new JSONObject(response.getHttpResponcePayload()).getString("ResDescr"));
                if (i == 30) {
                    parsePicoSlaveCMD(jSONObject);
                } else if (jSONObject.getInt("res") == 1) {
                    Functions.makeNormalToast(this, getResources().getString(R.string.msg_commandOk));
                    setUpSlave();
                } else {
                    Functions.makeErrorToast(this, getResources().getString(R.string.msg_commandKo));
                }
            } catch (Exception unused) {
            }
        }
    }

    /* access modifiers changed from: private */
    public int getColorInt(int i) {
        if (Build.VERSION.SDK_INT >= 23) {
            return getActivity().getResources().getColor(i, getActivity().getTheme());
        }
        return getActivity().getResources().getColor(i);
    }

    /* access modifiers changed from: private */
    public void setUpSlave() {
        runOnUiThread(new Runnable() {
            public void run() {
                PicoCascataActivity.this.findViewById(R.id.scrollVw).setVisibility(0);
                PicoCascataActivity.this.listGUI = new ArrayList();
                SlaveGUI slaveGUI = new SlaveGUI();
                slaveGUI.setBtnIconDelete((Button) PicoCascataActivity.this.findViewById(R.id.btnIconDelete1));
                slaveGUI.setBtnIconUpdFw((Button) PicoCascataActivity.this.findViewById(R.id.btnIconUpdFw1));
                slaveGUI.setBtnMoonDisable((Button) PicoCascataActivity.this.findViewById(R.id.btnMoonDisable1));
                slaveGUI.setLy_slave_conf((ConstraintLayout) PicoCascataActivity.this.findViewById(R.id.ly_slave_conf1));
                slaveGUI.setLblTitle((TextView) PicoCascataActivity.this.findViewById(R.id.lblTitle1));
                slaveGUI.setLblNoConf((TextView) PicoCascataActivity.this.findViewById(R.id.lblNoConf1));
                slaveGUI.setTxtName((EditText) PicoCascataActivity.this.findViewById(R.id.txtName1));
                slaveGUI.setCk_sync((RadioButton) PicoCascataActivity.this.findViewById(R.id.ck_sync1));
                slaveGUI.setCk_async((RadioButton) PicoCascataActivity.this.findViewById(R.id.ck_async1));
                slaveGUI.setGroup_enable((Group) PicoCascataActivity.this.findViewById(R.id.group_enable_1));
                PicoCascataActivity.this.listGUI.add(slaveGUI);
                SlaveGUI slaveGUI2 = new SlaveGUI();
                slaveGUI2.setBtnIconDelete((Button) PicoCascataActivity.this.findViewById(R.id.btnIconDelete2));
                slaveGUI2.setBtnIconUpdFw((Button) PicoCascataActivity.this.findViewById(R.id.btnIconUpdFw2));
                slaveGUI2.setBtnMoonDisable((Button) PicoCascataActivity.this.findViewById(R.id.btnMoonDisable2));
                slaveGUI2.setLy_slave_conf((ConstraintLayout) PicoCascataActivity.this.findViewById(R.id.ly_slave_conf2));
                slaveGUI2.setLblTitle((TextView) PicoCascataActivity.this.findViewById(R.id.lblTitle2));
                slaveGUI2.setLblNoConf((TextView) PicoCascataActivity.this.findViewById(R.id.lblNoConf2));
                slaveGUI2.setTxtName((EditText) PicoCascataActivity.this.findViewById(R.id.txtName2));
                slaveGUI2.setCk_sync((RadioButton) PicoCascataActivity.this.findViewById(R.id.ck_sync2));
                slaveGUI2.setCk_async((RadioButton) PicoCascataActivity.this.findViewById(R.id.ck_async2));
                slaveGUI2.setGroup_enable((Group) PicoCascataActivity.this.findViewById(R.id.group_enable_2));
                PicoCascataActivity.this.listGUI.add(slaveGUI2);
                SlaveGUI slaveGUI3 = new SlaveGUI();
                slaveGUI3.setBtnIconDelete((Button) PicoCascataActivity.this.findViewById(R.id.btnIconDelete3));
                slaveGUI3.setBtnIconUpdFw((Button) PicoCascataActivity.this.findViewById(R.id.btnIconUpdFw3));
                slaveGUI3.setBtnMoonDisable((Button) PicoCascataActivity.this.findViewById(R.id.btnMoonDisable3));
                slaveGUI3.setLy_slave_conf((ConstraintLayout) PicoCascataActivity.this.findViewById(R.id.ly_slave_conf3));
                slaveGUI3.setLblTitle((TextView) PicoCascataActivity.this.findViewById(R.id.lblTitle3));
                slaveGUI3.setLblNoConf((TextView) PicoCascataActivity.this.findViewById(R.id.lblNoConf3));
                slaveGUI3.setTxtName((EditText) PicoCascataActivity.this.findViewById(R.id.txtName3));
                slaveGUI3.setCk_sync((RadioButton) PicoCascataActivity.this.findViewById(R.id.ck_sync3));
                slaveGUI3.setCk_async((RadioButton) PicoCascataActivity.this.findViewById(R.id.ck_async3));
                slaveGUI3.setGroup_enable((Group) PicoCascataActivity.this.findViewById(R.id.group_enable_3));
                PicoCascataActivity.this.listGUI.add(slaveGUI3);
                SlaveGUI slaveGUI4 = new SlaveGUI();
                slaveGUI4.setBtnIconDelete((Button) PicoCascataActivity.this.findViewById(R.id.btnIconDelete4));
                slaveGUI4.setBtnIconUpdFw((Button) PicoCascataActivity.this.findViewById(R.id.btnIconUpdFw4));
                slaveGUI4.setBtnMoonDisable((Button) PicoCascataActivity.this.findViewById(R.id.btnMoonDisable4));
                slaveGUI4.setLy_slave_conf((ConstraintLayout) PicoCascataActivity.this.findViewById(R.id.ly_slave_conf4));
                slaveGUI4.setLblTitle((TextView) PicoCascataActivity.this.findViewById(R.id.lblTitle4));
                slaveGUI4.setLblNoConf((TextView) PicoCascataActivity.this.findViewById(R.id.lblNoConf4));
                slaveGUI4.setTxtName((EditText) PicoCascataActivity.this.findViewById(R.id.txtName4));
                slaveGUI4.setCk_sync((RadioButton) PicoCascataActivity.this.findViewById(R.id.ck_sync4));
                slaveGUI4.setCk_async((RadioButton) PicoCascataActivity.this.findViewById(R.id.ck_async4));
                slaveGUI4.setGroup_enable((Group) PicoCascataActivity.this.findViewById(R.id.group_enable_4));
                PicoCascataActivity.this.listGUI.add(slaveGUI4);
                int i = 0;
                while (i < PicoCascataActivity.this.listGUI.size()) {
                    TextView lblTitle = PicoCascataActivity.this.listGUI.get(i).getLblTitle();
                    StringBuilder sb = new StringBuilder();
                    sb.append(PicoCascataActivity.this.prexSlave);
                    sb.append(" ");
                    int i2 = i + 1;
                    sb.append(i2);
                    lblTitle.setText(sb.toString());
                    PicoCascataActivity.this.listGUI.get(i).getCk_sync().setTypeface(BaseActivity.avenirbold);
                    PicoCascataActivity.this.listGUI.get(i).getCk_async().setTypeface(BaseActivity.avenirbold);
                    if (PicoCascataActivity.this.slaves.get(i) == null || PicoCascataActivity.this.slaves.get(i).getSet_stato() == 3) {
                        PicoCascataActivity.this.listGUI.get(i).getLy_slave_conf().setVisibility(4);
                        PicoCascataActivity.this.listGUI.get(i).getLblNoConf().setVisibility(0);
                        PicoCascataActivity.this.listGUI.get(i).getLblNoConf().setText(PicoCascataActivity.this.getResources().getString(R.string.mc_lblNomeNotConfigText));
                    } else {
                        PicoCascataActivity.this.listGUI.get(i).getLy_slave_conf().setVisibility(0);
                        if (PicoCascataActivity.this.slaves.get(i).getSet_stato() == 1) {
                            PicoCascataActivity.this.listGUI.get(i).getGroup_enable().setVisibility(8);
                            PicoCascataActivity.this.listGUI.get(i).getGroup_enable().setVisibility(0);
                            PicoCascataActivity.this.listGUI.get(i).getBtnMoonDisable().setTextColor(PicoCascataActivity.this.getColorInt(R.color.colorPrimary));
                            PicoCascataActivity.this.listGUI.get(i).getLblNoConf().setVisibility(8);
                            PicoCascataActivity.this.listGUI.get(i).getTxtName().setText(PicoCascataActivity.this.slaves.get(i).getName());
                            if (PicoCascataActivity.this.showFW) {
                                PicoCascataActivity.this.listGUI.get(i).getBtnIconUpdFw().setVisibility(0);
                            } else {
                                PicoCascataActivity.this.listGUI.get(i).getBtnIconUpdFw().setVisibility(8);
                            }
                            if (PicoCascataActivity.this.slaves.get(i).getVerso() == Pico.Slave.VERSOASYNC) {
                                PicoCascataActivity.this.listGUI.get(i).getCk_async().setChecked(true);
                            } else if (PicoCascataActivity.this.slaves.get(i).getVerso() == Pico.Slave.VERSOSYNC) {
                                PicoCascataActivity.this.listGUI.get(i).getCk_sync().setChecked(true);
                            } else {
                                PicoCascataActivity.this.listGUI.get(i).getCk_sync().setChecked(false);
                                PicoCascataActivity.this.listGUI.get(i).getCk_async().setChecked(false);
                            }
                        } else {
                            PicoCascataActivity.this.listGUI.get(i).getBtnMoonDisable().setTextColor(PicoCascataActivity.this.getColorInt(R.color.colordisable));
                            PicoCascataActivity.this.listGUI.get(i).getGroup_enable().setVisibility(8);
                            PicoCascataActivity.this.listGUI.get(i).getGroup_enable().setVisibility(4);
                            PicoCascataActivity.this.listGUI.get(i).getBtnIconUpdFw().setVisibility(8);
                            PicoCascataActivity.this.listGUI.get(i).getLblNoConf().setVisibility(0);
                            PicoCascataActivity.this.listGUI.get(i).getLblNoConf().setText(PicoCascataActivity.this.getResources().getString(R.string.mc_lblNomeConfigButDisconnectedText));
                        }
                    }
                    PicoCascataActivity.this.setUpEventSalve(i2);
                    i = i2;
                }
            }
        });
    }

    public void cancellaSlave(final int i) {
        AnonymousClass3 r6 = new Runnable() {
            public void run() {
                PicoCascataActivity.this.dismissdialog();
                PicoCascataActivity.this.slaves.get(i).setSet_stato(3);
                PicoCascataActivity picoCascataActivity = PicoCascataActivity.this;
                picoCascataActivity.setSlave(picoCascataActivity.slaves.get(i));
            }
        };
        AnonymousClass4 r5 = new Runnable() {
            public void run() {
                PicoCascataActivity.this.dismissdialog();
            }
        };
        openDialogFragment(createYesNoPopUp(getResources().getString(R.string.mc_CancelDialogTitle), getResources().getString(R.string.mc_CancelDialogText), getResources().getString(R.string.no), getResources().getString(R.string.yes), r5, r6));
    }

    public void disableSlave(final int i) {
        AnonymousClass5 r6 = new Runnable() {
            public void run() {
                PicoCascataActivity.this.dismissdialog();
                PicoCascataActivity.this.slaves.get(i).setSet_stato(2);
                PicoCascataActivity picoCascataActivity = PicoCascataActivity.this;
                picoCascataActivity.setSlave(picoCascataActivity.slaves.get(i));
            }
        };
        AnonymousClass6 r5 = new Runnable() {
            public void run() {
                PicoCascataActivity.this.dismissdialog();
            }
        };
        openDialogFragment(createYesNoPopUp(getResources().getString(R.string.mc_DisableDialogTitle), getResources().getString(R.string.mc_DisableDialogText), getResources().getString(R.string.no), getResources().getString(R.string.yes), r5, r6));
    }

    public void enableSlave(final int i) {
        AnonymousClass7 r6 = new Runnable() {
            public void run() {
                PicoCascataActivity.this.dismissdialog();
                PicoCascataActivity.this.slaves.get(i).setSet_stato(1);
                PicoCascataActivity picoCascataActivity = PicoCascataActivity.this;
                picoCascataActivity.setSlave(picoCascataActivity.slaves.get(i));
            }
        };
        AnonymousClass8 r5 = new Runnable() {
            public void run() {
                PicoCascataActivity.this.dismissdialog();
            }
        };
        openDialogFragment(createYesNoPopUp(getResources().getString(R.string.mc_EnableDialogTitle), getResources().getString(R.string.mc_EnableDialogText), getResources().getString(R.string.no), getResources().getString(R.string.yes), r5, r6));
    }

    /* access modifiers changed from: private */
    public void goToBootloader(final int i) {
        if (PicoActivity.pico.getOffline().booleanValue()) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Intent intent = new Intent(PicoCascataActivity.this.activity, PicoBootloaderActivity.class);
                    intent.setFlags(67108864);
                    intent.putExtra("FROMPICOACT", true);
                    intent.putExtra("IPSLAVE", PicoCascataActivity.this.slaves.get(i).getIp());
                    intent.putExtra("ISSLAVE", true);
                    PicoCascataActivity.this.startActivity(intent);
                }
            });
            return;
        }
        AnonymousClass10 r2 = new Runnable() {
            public void run() {
                PicoCascataActivity.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(PicoCascataActivity.this.activity, PicoBootloaderActivity.class);
                        intent.setFlags(67108864);
                        intent.putExtra("FROMPICOACT", true);
                        intent.putExtra("IPSLAVE", PicoCascataActivity.this.slaves.get(i).getIp());
                        intent.putExtra("ISSLAVE", true);
                        PicoCascataActivity.this.startActivity(intent);
                    }
                });
            }
        };
        toConnPwd = "TS_" + PicoActivity.pico.getSerial();
        toConnSid = Constants.WIFI_NAME_OFFLINE_PICO + PicoActivity.pico.getSerial();
        connectToWifi(r2, false, false);
    }

    /* access modifiers changed from: private */
    public void setUpEventSalve(int i) {
        final int i2 = i - 1;
        this.listGUI.get(i2).getTxtName().addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void afterTextChanged(Editable editable) {
                PicoCascataActivity.this.slaves.get(i2).setName(editable.toString());
            }
        });
        this.listGUI.get(i2).getBtnIconDelete().setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PicoCascataActivity.this.cancellaSlave(i2);
            }
        });
        this.listGUI.get(i2).getBtnMoonDisable().setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (PicoCascataActivity.this.slaves.get(i2).getSet_stato() == 1) {
                    PicoCascataActivity.this.disableSlave(i2);
                } else {
                    PicoCascataActivity.this.enableSlave(i2);
                }
            }
        });
        this.listGUI.get(i2).getBtnIconUpdFw().setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PicoCascataActivity.this.goToBootloader(i2);
            }
        });
        this.listGUI.get(i2).getCk_async().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                if (z) {
                    PicoCascataActivity.this.slaves.get(i2).setVerso(Pico.Slave.VERSOASYNC);
                }
            }
        });
        this.listGUI.get(i2).getCk_sync().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                if (z) {
                    PicoCascataActivity.this.slaves.get(i2).setVerso(Pico.Slave.VERSOSYNC);
                }
            }
        });
    }

    private void setUpGui() {
        Functions.setFontsWithIcon(findViewById(R.id.ly_container), this);
    }

    public void changeSlave(Pico.Slave slave) {
        for (int i = 0; i < this.slaves.size(); i++) {
            if (this.slaves.get(i).getId_slave() == slave.getId_slave()) {
                this.slaves.get(i).setName(slave.getName());
                this.slaves.get(i).setVerso(slave.getVerso());
                this.slaves.get(i).setMode(slave.getMode());
                return;
            }
        }
    }

    public void setSlave(Pico.Slave slave) {
        final CmdPICO.SetSlave setSlave;
        if (Constants.ISDEMO) {
            setUpSlave();
            return;
        }
        if (slave == null) {
            setSlave = new CmdPICO.SetSlave(this.slaves, false);
        } else {
            ArrayList arrayList = new ArrayList();
            arrayList.add(slave);
            setSlave = new CmdPICO.SetSlave(arrayList, true);
        }
        if (PicoActivity.pico.getOffline().booleanValue()) {
            new Thread(new Runnable() {
                public void run() {
                    String sendCMD = UDPSocket.sendCMD(setSlave);
                    if (sendCMD != null) {
                        PicoCascataActivity.this.hideProgress();
                        try {
                            JSONObject jSONObject = new JSONObject(sendCMD);
                            if (jSONObject.has(Constants.JSON_RES) && jSONObject.getInt(Constants.JSON_RES) == 1) {
                                Functions.makeNormalToast(PicoCascataActivity.this.activity, PicoCascataActivity.this.getResources().getString(R.string.msg_commandOk));
                                PicoCascataActivity.this.setUpSlave();
                            }
                        } catch (Exception unused) {
                        }
                    }
                }
            }).start();
            return;
        }
        PICOServer pICOServer = new PICOServer();
        pICOServer.setSerial(PicoActivity.pico.getSerial());
        pICOServer.setPin(PicoActivity.pico.getPin());
        pICOServer.setName(PicoActivity.pico.getName());
        setSlave.setIdp((long) PicoActivity.getIDP());
        pICOServer.setCmd(new Gson().toJson((Object) setSlave));
        new ThreadWebService(this.activity, 1, 31, getResources().getString(R.string.uriWebService_PICO) + getResources().getString(R.string.uri_SendPicoCmd), new Gson().toJson((Object) pICOServer), (String[]) null).start();
    }

    public void btnSalva(View view) {
        if (!Constants.ISDEMO) {
            setSlave((Pico.Slave) null);
        }
    }

    public View getToolBar() {
        return findViewById(R.id.pico_toolbar);
    }

    public String setToolbarTitle() {
        return PicoActivity.pico.getName().toUpperCase();
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        PicoActivity.pico.getOffline().booleanValue();
        super.onDestroy();
    }

    private class SlaveGUI {
        Button btnIconDelete;
        Button btnIconUpdFw;
        Button btnMoonDisable;
        RadioButton ck_async;
        RadioButton ck_sync;
        Group group_enable;
        TextView lblNoConf;
        TextView lblTitle;
        ConstraintLayout ly_slave_conf;
        EditText txtName;

        private SlaveGUI() {
        }

        public Group getGroup_enable() {
            return this.group_enable;
        }

        public void setGroup_enable(Group group) {
            this.group_enable = group;
        }

        public Button getBtnMoonDisable() {
            return this.btnMoonDisable;
        }

        public void setBtnMoonDisable(Button button) {
            this.btnMoonDisable = button;
        }

        public Button getBtnIconUpdFw() {
            return this.btnIconUpdFw;
        }

        public void setBtnIconUpdFw(Button button) {
            this.btnIconUpdFw = button;
        }

        public TextView getLblTitle() {
            return this.lblTitle;
        }

        public void setLblTitle(TextView textView) {
            this.lblTitle = textView;
        }

        public TextView getLblNoConf() {
            return this.lblNoConf;
        }

        public void setLblNoConf(TextView textView) {
            this.lblNoConf = textView;
        }

        public ConstraintLayout getLy_slave_conf() {
            return this.ly_slave_conf;
        }

        public void setLy_slave_conf(ConstraintLayout constraintLayout) {
            this.ly_slave_conf = constraintLayout;
        }

        public Button getBtnIconDelete() {
            return this.btnIconDelete;
        }

        public void setBtnIconDelete(Button button) {
            this.btnIconDelete = button;
        }

        public EditText getTxtName() {
            return this.txtName;
        }

        public void setTxtName(EditText editText) {
            this.txtName = editText;
        }

        public RadioButton getCk_sync() {
            return this.ck_sync;
        }

        public void setCk_sync(RadioButton radioButton) {
            this.ck_sync = radioButton;
        }

        public RadioButton getCk_async() {
            return this.ck_async;
        }

        public void setCk_async(RadioButton radioButton) {
            this.ck_async = radioButton;
        }
    }
}
