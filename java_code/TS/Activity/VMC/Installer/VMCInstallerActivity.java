package it.tecnosystemi.TS.Activity.VMC.Installer;

import android.os.Bundle;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.gson.Gson;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Activity.VMC.VMCActivity;
import it.tecnosystemi.TS.Adapters.ParamAdapter;
import it.tecnosystemi.TS.Adapters.SpinnerAdapter;
import it.tecnosystemi.TS.Commands.CmdPICO;
import it.tecnosystemi.TS.Model.ModBusRecipe;
import it.tecnosystemi.TS.Model.Response;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Functions;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class VMCInstallerActivity extends BaseActivity {
    final int MAXPARAMAREQ = 10;
    String Val;
    VMCInstallerActivity activity;
    Button btnSalva;
    TextView lblFrecciaMoon;
    TextView lblParamName;
    TextView lbldesc;
    ListView lsParams;
    ConstraintLayout lyModifica;
    ParamAdapter paramAdapter;
    ModBusRecipe.Param paramClicked;
    List<ModBusRecipe.Param> params;
    Spinner spMulti;
    EditText txtVal;

    public BaseActivity getActivity() {
        return this;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_vmcinstaller);
        this.typeActStyle = 3;
        this.activity = this;
        super.onCreate(bundle);
        setUpGui();
        this.params = new ArrayList();
        for (int i = 0; i < VMCActivity.params.size(); i++) {
            if (VMCActivity.params.get(i).getPRPA_PRMT_Id() == 2) {
                VMCActivity.params.get(i).setValue((String) null);
                VMCActivity.params.get(i).setIndicepercolore(this.params.size());
                this.params.add(VMCActivity.params.get(i));
            }
        }
        getParametri();
        initLsParamas();
        this.hideloading = false;
        findViewById(R.id.ly_progress).setVisibility(0);
        Functions.setFontsWithIcon(findViewById(R.id.main), this);
    }

    private void setUpGui() {
        this.lsParams = (ListView) findViewById(R.id.lsParams);
        this.lyModifica = (ConstraintLayout) findViewById(R.id.lyModifica);
        this.lblParamName = (TextView) findViewById(R.id.lblParamName);
        this.lbldesc = (TextView) findViewById(R.id.lbldesc);
        this.txtVal = (EditText) findViewById(R.id.txtVal);
        this.btnSalva = (Button) findViewById(R.id.btnSalva);
        this.spMulti = (Spinner) findViewById(R.id.spMulti);
        this.lblFrecciaMoon = (TextView) findViewById(R.id.lblFrecciaMoon);
    }

    private void getParametri() {
        new Thread(new Runnable() {
            /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
            /* JADX WARNING: Missing exception handler attribute for start block: B:4:0x0011 */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r3 = this;
                    it.tecnosystemi.TS.Activity.VMC.Installer.VMCInstallerActivity r0 = it.tecnosystemi.TS.Activity.VMC.Installer.VMCInstallerActivity.this     // Catch:{ Exception -> 0x001b }
                    r0.showProgress()     // Catch:{ Exception -> 0x001b }
                    it.tecnosystemi.TS.Activity.VMC.Installer.VMCInstallerActivity r0 = it.tecnosystemi.TS.Activity.VMC.Installer.VMCInstallerActivity.this     // Catch:{ Exception -> 0x0011 }
                    it.tecnosystemi.TS.Model.VMC r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc     // Catch:{ Exception -> 0x0011 }
                    java.lang.String r1 = r1.getKey_recipe()     // Catch:{ Exception -> 0x0011 }
                    r2 = 1
                    r0.letturaParametri(r2, r1)     // Catch:{ Exception -> 0x0011 }
                L_0x0011:
                    it.tecnosystemi.TS.Activity.VMC.Installer.VMCInstallerActivity r0 = it.tecnosystemi.TS.Activity.VMC.Installer.VMCInstallerActivity.this     // Catch:{ Exception -> 0x001b }
                    r0.hideProgress()     // Catch:{ Exception -> 0x001b }
                    it.tecnosystemi.TS.Activity.VMC.Installer.VMCInstallerActivity r0 = it.tecnosystemi.TS.Activity.VMC.Installer.VMCInstallerActivity.this     // Catch:{ Exception -> 0x001b }
                    r0.aggLista()     // Catch:{ Exception -> 0x001b }
                L_0x001b:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.VMC.Installer.VMCInstallerActivity.AnonymousClass1.run():void");
            }
        }).start();
    }

    public void initLsParamas() {
        ParamAdapter paramAdapter2 = new ParamAdapter(this, this.params);
        this.paramAdapter = paramAdapter2;
        this.lsParams.setAdapter(paramAdapter2);
        this.lsParams.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                VMCInstallerActivity vMCInstallerActivity = VMCInstallerActivity.this;
                vMCInstallerActivity.paramClicked = vMCInstallerActivity.params.get(i);
                if (VMCInstallerActivity.this.paramClicked.isPRPP_Editable()) {
                    VMCInstallerActivity.this.showParam();
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void showParam() {
        hideMenuButton();
        this.lblParamName.setText(this.paramClicked.getName());
        this.lbldesc.setText(this.paramClicked.getLdesc());
        if (this.paramClicked.getPRPA_PRPT_Id() == 3) {
            this.txtVal.setVisibility(4);
            this.spMulti.setVisibility(0);
            this.lblFrecciaMoon.setVisibility(0);
            ArrayList arrayList = new ArrayList();
            int i = 0;
            for (int i2 = 0; i2 < this.paramClicked.getParamLabels().size(); i2++) {
                arrayList.add(this.paramClicked.getParamLabels().get(i2).getName());
                if (((double) this.paramClicked.getParamLabels().get(i2).getPRPL_Value()) == this.paramClicked.getParsedValue()) {
                    i = i2;
                }
            }
            this.spMulti.setAdapter(new SpinnerAdapter(this, R.layout.spinner_item_alto_alto, arrayList, false));
            this.spMulti.setSelection(i);
            this.btnSalva.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    VMCInstallerActivity.this.showProgress();
                    new Thread(new Runnable() {
                        public void run() {
                            Pair access$100 = VMCInstallerActivity.this.sendCmd(CmdPICO.Wr_param.fromParam(VMCInstallerActivity.this.paramClicked, VMCActivity.vmc.getKey_recipe(), (float) VMCInstallerActivity.this.paramClicked.getParamLabels().get(VMCInstallerActivity.this.spMulti.getSelectedItemPosition()).getPRPL_Value()));
                            VMCInstallerActivity.this.hideProgress();
                            if (((Boolean) access$100.first).booleanValue()) {
                                try {
                                    VMCInstallerActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            VMCInstallerActivity.this.onBackPressed();
                                        }
                                    });
                                } catch (Exception unused) {
                                }
                            }
                        }
                    }).start();
                }
            });
        } else {
            this.txtVal.setVisibility(0);
            this.spMulti.setVisibility(4);
            this.lblFrecciaMoon.setVisibility(4);
            this.lyModifica.setOnTouchListener(new VMCInstallerActivity$$ExternalSyntheticLambda0(this));
            this.txtVal.setText(this.paramClicked.getValToShow(true));
            this.txtVal.setOnFocusChangeListener(new VMCInstallerActivity$$ExternalSyntheticLambda1(this));
            this.txtVal.setOnEditorActionListener(new VMCInstallerActivity$$ExternalSyntheticLambda2(this));
            this.btnSalva.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    VMCInstallerActivity.this.showProgress();
                    new Thread(new Runnable() {
                        public void run() {
                            Pair access$100 = VMCInstallerActivity.this.sendCmd(CmdPICO.Wr_param.fromParam(VMCInstallerActivity.this.paramClicked, VMCActivity.vmc.getKey_recipe(), (float) VMCInstallerActivity.this.paramClicked.getParsedValue()));
                            VMCInstallerActivity.this.hideProgress();
                            if (((Boolean) access$100.first).booleanValue()) {
                                try {
                                    VMCInstallerActivity.this.runOnUiThread(new Runnable() {
                                        public void run() {
                                            VMCInstallerActivity.this.onBackPressed();
                                        }
                                    });
                                } catch (Exception unused) {
                                }
                            } else {
                                Functions.makeErrorToast(VMCInstallerActivity.this.activity, VMCInstallerActivity.this.getResources().getString(R.string.msg_commandKo));
                            }
                        }
                    }).start();
                }
            });
        }
        this.lyModifica.setVisibility(0);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$showParam$0$it-tecnosystemi-TS-Activity-VMC-Installer-VMCInstallerActivity  reason: not valid java name */
    public /* synthetic */ boolean m44lambda$showParam$0$ittecnosystemiTSActivityVMCInstallerVMCInstallerActivity(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() != 0 || !this.txtVal.isFocused()) {
            return false;
        }
        this.txtVal.clearFocus();
        hideKeyboard(this.txtVal);
        return false;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$showParam$1$it-tecnosystemi-TS-Activity-VMC-Installer-VMCInstallerActivity  reason: not valid java name */
    public /* synthetic */ void m45lambda$showParam$1$ittecnosystemiTSActivityVMCInstallerVMCInstallerActivity(View view, boolean z) {
        if (z) {
            this.txtVal.setText("");
        } else {
            applyEditTextValue(this.txtVal);
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$showParam$2$it-tecnosystemi-TS-Activity-VMC-Installer-VMCInstallerActivity  reason: not valid java name */
    public /* synthetic */ boolean m46lambda$showParam$2$ittecnosystemiTSActivityVMCInstallerVMCInstallerActivity(TextView textView, int i, KeyEvent keyEvent) {
        applyEditTextValue(this.txtVal);
        this.txtVal.clearFocus();
        hideKeyboard(this.txtVal);
        return true;
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService("input_method");
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void applyEditTextValue(EditText editText) {
        String trim = editText.getText().toString().trim();
        if (!trim.isEmpty()) {
            try {
                this.Val = trim;
                float parseDouble = (float) Double.parseDouble(trim);
                if (parseDouble < ((float) this.paramClicked.getPRPP_Min())) {
                    parseDouble = (float) this.paramClicked.getPRPP_Min();
                }
                if (parseDouble > ((float) this.paramClicked.getPRPP_Max())) {
                    parseDouble = (float) this.paramClicked.getPRPP_Max();
                }
                this.paramClicked.setValue(CmdPICO.Wr_param.fromParam(this.paramClicked, "", parseDouble).getWrv());
            } catch (Exception unused) {
            }
            editText.setText(this.paramClicked.getValToShow(true));
        }
    }

    public void aggLista() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (VMCInstallerActivity.this.paramAdapter != null) {
                    VMCInstallerActivity.this.paramAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    public boolean letturaParametri(int i, String str) {
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < this.params.size(); i2++) {
            arrayList.add(Integer.valueOf(this.params.get(i2).getIdScheda()));
        }
        ArrayList arrayList2 = new ArrayList();
        CmdPICO.Rd_param rd_param = new CmdPICO.Rd_param();
        rd_param.setKey_recipe(str);
        rd_param.setDad(i);
        boolean z = true;
        for (int i3 = 0; i3 < arrayList.size() && z; i3++) {
            arrayList2.add((Integer) arrayList.get(i3));
            if (arrayList2.size() >= 10) {
                rd_param.setIds(arrayList2);
                Pair<Boolean, String> sendCmd = sendCmd(rd_param);
                ArrayList arrayList3 = new ArrayList();
                if (((Boolean) sendCmd.first).booleanValue()) {
                    try {
                        parseRespReadParam((String) sendCmd.second);
                    } catch (Exception unused) {
                    }
                    arrayList2 = arrayList3;
                } else {
                    arrayList2 = arrayList3;
                    z = false;
                }
            }
        }
        if (z && arrayList2.size() > 0) {
            rd_param.setIds(arrayList2);
            Pair<Boolean, String> sendCmd2 = sendCmd(rd_param);
            if (!((Boolean) sendCmd2.first).booleanValue()) {
                return false;
            }
            try {
                parseRespReadParam((String) sendCmd2.second);
            } catch (Exception unused2) {
            }
        }
        return z;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:13:0x00a9 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.util.Pair<java.lang.Boolean, java.lang.String> sendCmd(it.tecnosystemi.TS.Commands.CmdPICO r10) {
        /*
            r9 = this;
            int r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.getIDP()     // Catch:{ Exception -> 0x00b7 }
            long r0 = (long) r0     // Catch:{ Exception -> 0x00b7 }
            r10.setIdp(r0)     // Catch:{ Exception -> 0x00b7 }
            it.tecnosystemi.TS.Model.VMC r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc     // Catch:{ Exception -> 0x00b7 }
            java.lang.Boolean r0 = r0.getOffline()     // Catch:{ Exception -> 0x00b7 }
            boolean r0 = r0.booleanValue()     // Catch:{ Exception -> 0x00b7 }
            if (r0 == 0) goto L_0x0029
            java.lang.String r10 = it.tecnosystemi.TS.Commands.UDPSocket.sendCMD(r10)     // Catch:{ Exception -> 0x00b7 }
            r9.hideProgress()     // Catch:{ Exception -> 0x00b7 }
            android.util.Pair r0 = new android.util.Pair     // Catch:{ Exception -> 0x00b7 }
            boolean r1 = r9.checkRespSetMode(r10)     // Catch:{ Exception -> 0x00b7 }
            java.lang.Boolean r1 = java.lang.Boolean.valueOf(r1)     // Catch:{ Exception -> 0x00b7 }
            r0.<init>(r1, r10)     // Catch:{ Exception -> 0x00b7 }
            return r0
        L_0x0029:
            it.tecnosystemi.TS.Commands.PICOServer r0 = new it.tecnosystemi.TS.Commands.PICOServer     // Catch:{ Exception -> 0x00b7 }
            r0.<init>()     // Catch:{ Exception -> 0x00b7 }
            it.tecnosystemi.TS.Model.VMC r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc     // Catch:{ Exception -> 0x00b7 }
            java.lang.String r1 = r1.getSerial()     // Catch:{ Exception -> 0x00b7 }
            r0.setSerial(r1)     // Catch:{ Exception -> 0x00b7 }
            it.tecnosystemi.TS.Model.VMC r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc     // Catch:{ Exception -> 0x00b7 }
            java.lang.String r1 = r1.getPin()     // Catch:{ Exception -> 0x00b7 }
            r0.setPin(r1)     // Catch:{ Exception -> 0x00b7 }
            it.tecnosystemi.TS.Model.VMC r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc     // Catch:{ Exception -> 0x00b7 }
            java.lang.String r1 = r1.getName()     // Catch:{ Exception -> 0x00b7 }
            r0.setName(r1)     // Catch:{ Exception -> 0x00b7 }
            if (r10 == 0) goto L_0x005c
            java.lang.String r1 = "mqtt"
            r10.setFrm(r1)     // Catch:{ Exception -> 0x00b7 }
            com.google.gson.Gson r1 = new com.google.gson.Gson     // Catch:{ Exception -> 0x00b7 }
            r1.<init>()     // Catch:{ Exception -> 0x00b7 }
            java.lang.String r10 = r1.toJson((java.lang.Object) r10)     // Catch:{ Exception -> 0x00b7 }
            r0.setCmd(r10)     // Catch:{ Exception -> 0x00b7 }
        L_0x005c:
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00b7 }
            r10.<init>()     // Catch:{ Exception -> 0x00b7 }
            android.content.res.Resources r1 = r9.getResources()     // Catch:{ Exception -> 0x00b7 }
            int r2 = it.tecnosystemi.TS.R.string.uriWebService     // Catch:{ Exception -> 0x00b7 }
            java.lang.String r1 = r1.getString(r2)     // Catch:{ Exception -> 0x00b7 }
            r10.append(r1)     // Catch:{ Exception -> 0x00b7 }
            android.content.res.Resources r1 = r9.getResources()     // Catch:{ Exception -> 0x00b7 }
            int r2 = it.tecnosystemi.TS.R.string.uri_SendVMCCmd     // Catch:{ Exception -> 0x00b7 }
            java.lang.String r1 = r1.getString(r2)     // Catch:{ Exception -> 0x00b7 }
            r10.append(r1)     // Catch:{ Exception -> 0x00b7 }
            java.lang.String r3 = r10.toString()     // Catch:{ Exception -> 0x00b7 }
            com.google.gson.Gson r10 = new com.google.gson.Gson     // Catch:{ Exception -> 0x00b7 }
            r10.<init>()     // Catch:{ Exception -> 0x00b7 }
            java.lang.String r4 = r10.toJson((java.lang.Object) r0)     // Catch:{ Exception -> 0x00b7 }
            java.lang.String r7 = it.tecnosystemi.TS.Utils.Constants.user     // Catch:{ Exception -> 0x00b7 }
            r8 = 0
            r5 = 1
            r6 = 0
            r2 = r9
            it.tecnosystemi.TS.Model.Response r10 = r2.makeApiCall(r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x00b7 }
            boolean r0 = r9.checkonlineError(r10)     // Catch:{ Exception -> 0x00b7 }
            if (r0 == 0) goto L_0x00b7
            java.lang.String r0 = ""
            org.json.JSONObject r1 = new org.json.JSONObject     // Catch:{ Exception -> 0x00a9 }
            java.lang.String r10 = r10.getHttpResponcePayload()     // Catch:{ Exception -> 0x00a9 }
            r1.<init>(r10)     // Catch:{ Exception -> 0x00a9 }
            java.lang.String r10 = "ResDescr"
            java.lang.String r0 = r1.getString(r10)     // Catch:{ Exception -> 0x00a9 }
        L_0x00a9:
            android.util.Pair r10 = new android.util.Pair     // Catch:{ Exception -> 0x00b7 }
            boolean r1 = r9.checkRespSetMode(r0)     // Catch:{ Exception -> 0x00b7 }
            java.lang.Boolean r1 = java.lang.Boolean.valueOf(r1)     // Catch:{ Exception -> 0x00b7 }
            r10.<init>(r1, r0)     // Catch:{ Exception -> 0x00b7 }
            return r10
        L_0x00b7:
            android.util.Pair r10 = new android.util.Pair
            r0 = 0
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r0)
            r1 = 0
            r10.<init>(r0, r1)
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.VMC.Installer.VMCInstallerActivity.sendCmd(it.tecnosystemi.TS.Commands.CmdPICO):android.util.Pair");
    }

    private boolean checkRespSetMode(String str) {
        if (str != null) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.has("res")) {
                    if (jSONObject.getInt("res") == 1) {
                        return true;
                    }
                    return false;
                }
            } catch (Exception unused) {
            }
        }
        VMCInstallerActivity vMCInstallerActivity = this.activity;
        Functions.makeErrorToast(vMCInstallerActivity, vMCInstallerActivity.getResources().getString(R.string.msg_commandKo));
        return false;
    }

    private boolean checkonlineError(Response response) {
        if (response == null) {
            try {
                VMCInstallerActivity vMCInstallerActivity = this.activity;
                Functions.makeErrorToast(vMCInstallerActivity, vMCInstallerActivity.getResources().getString(R.string.resCodeError));
                return false;
            } catch (Exception unused) {
            }
        } else {
            JSONObject jSONObject = new JSONObject(response.getHttpResponcePayload());
            if (jSONObject.has("ResCode") && jSONObject.getInt("ResCode") == 0) {
                return true;
            }
            VMCInstallerActivity vMCInstallerActivity2 = this.activity;
            Functions.makeErrorToast(vMCInstallerActivity2, vMCInstallerActivity2.getResources().getString(R.string.msg_commandKo));
            return false;
        }
    }

    public void parseRespReadParam(String str) {
        CmdPICO.Rd_param.Resp resp = (CmdPICO.Rd_param.Resp) new Gson().fromJson(str, CmdPICO.Rd_param.Resp.class);
        for (int i = 0; i < resp.getIds().size(); i++) {
            int i2 = 0;
            while (true) {
                if (i2 >= this.params.size()) {
                    break;
                } else if (this.params.get(i2).getIdScheda() == resp.getIds().get(i).intValue()) {
                    this.params.get(i2).setValue(resp.getVal().get(i));
                    break;
                } else {
                    i2++;
                }
            }
        }
    }

    public void showRendimento() {
        int i;
        double parsedValue = VMCActivity.vmc.getTempAmb() != null ? VMCActivity.vmc.getTempAmb().getParsedValue() : -999.0d;
        double parsedValue2 = VMCActivity.vmc.getTempMand() != null ? VMCActivity.vmc.getTempMand().getParsedValue() : -999.0d;
        double parsedValue3 = VMCActivity.vmc.getTempRipInt() != null ? VMCActivity.vmc.getTempRipInt().getParsedValue() : -999.0d;
        String str = "---";
        try {
            if (VMCActivity.vmc.getByPass().getParsedValue() == 0.0d && parsedValue != -999.0d && parsedValue2 != -999.0d && parsedValue3 != -999.0d && (i = (int) (((parsedValue2 - parsedValue) / (parsedValue3 - parsedValue)) * 100.0d)) >= 30 && i <= 100) {
                str = "" + i;
            }
        } catch (Exception unused) {
        }
        openDialogFragment(createInfoPopUp(getResources().getString(R.string.pm_menuRendimento), str + " %", getResources().getString(R.string.popup_rendimento)));
    }

    public void onBackPressed() {
        if (this.lyModifica.getVisibility() == 0) {
            this.lyModifica.setVisibility(8);
            showMenuButton();
            getParametri();
            return;
        }
        super.onBackPressed();
    }

    public View getToolBar() {
        return findViewById(R.id.vmc_toolbar);
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        AnonymousClass6 r5 = new Runnable() {
            public void run() {
                VMCInstallerActivity.this.dismissdialog();
                VMCInstallerActivity.this.showRendimento();
            }
        };
        if (!(VMCActivity.vmc == null || VMCActivity.vmc.getSerial() == null)) {
            list.add(createMenuItem(false, getResources().getString(R.string.pm_menuRendimento), "", (String) null, r5, false, false));
        }
        AnonymousClass7 r6 = new Runnable() {
            public void run() {
                VMCInstallerActivity.this.dismissdialog();
            }
        };
        if (!(VMCActivity.vmc == null || VMCActivity.vmc.getSerial() == null)) {
            list.add(createMenuItem(false, getResources().getString(R.string.pm_IDDevice) + VMCActivity.vmc.getSerial(), "", (String) null, r6, false, false));
        }
        AnonymousClass8 r62 = new Runnable() {
            public void run() {
                VMCInstallerActivity.this.dismissdialog();
            }
        };
        if (!(VMCActivity.vmc == null || VMCActivity.vmc.getFw_ver() == null)) {
            list.add(createMenuItem(false, "Vr. " + VMCActivity.vmc.getFw_ver(), "", (String) null, r62, false, false));
        }
        return list;
    }

    public String setToolbarTitle() {
        return VMCActivity.vmc.getName();
    }
}
