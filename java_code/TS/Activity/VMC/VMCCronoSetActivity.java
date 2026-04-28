package it.tecnosystemi.TS.Activity.VMC;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.gson.Gson;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Adapters.SpinnerAdapter;
import it.tecnosystemi.TS.Commands.CmdPICO;
import it.tecnosystemi.TS.Commands.PICOServerTimezone;
import it.tecnosystemi.TS.Commands.UDPSocket;
import it.tecnosystemi.TS.Model.Crono;
import it.tecnosystemi.TS.Model.PICOCronoObj;
import it.tecnosystemi.TS.Model.Response;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class VMCCronoSetActivity extends BaseActivity {
    public static int index = 0;
    static int indexZonetoCopy = -1;
    BaseActivity activity;
    BaseActivity.BundleMenuList bdl;
    Button btnSalva;
    Button btndeletef1;
    Button btndeletef2;
    Button btndeletef3;
    Button btndeletef4;
    TextView csa_lblFrecciaF1Mode;
    TextView csa_lblFrecciaF2Mode;
    TextView csa_lblFrecciaF3Mode;
    TextView csa_lblFrecciaF4Mode;
    Spinner csa_spF1Mode;
    Spinner csa_spF2Mode;
    Spinner csa_spF3Mode;
    Spinner csa_spF4Mode;
    Spinner days;
    TextView lblF1;
    TextView lblF2;
    TextView lblF3;
    TextView lblF4;
    TextView lblFreccia;
    TextView lblf1Mode;
    TextView lblf1end;
    TextView lblf1start;
    TextView lblf2Mode;
    TextView lblf2end;
    TextView lblf2start;
    TextView lblf3Mode;
    TextView lblf3end;
    TextView lblf3start;
    TextView lblf4Mode;
    TextView lblf4end;
    TextView lblf4start;
    String s;
    boolean startupvalue = true;
    String timeStart = "00:00";
    EditText txtf1end;
    EditText txtf1start;
    EditText txtf2end;
    EditText txtf2start;
    EditText txtf3end;
    EditText txtf3start;
    EditText txtf4end;
    EditText txtf4start;

    public BaseActivity getActivity() {
        return this;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_vmccrono_set);
        this.typeActStyle = 3;
        this.activity = this;
        super.onCreate(bundle);
        setUpGui();
        setValues();
    }

    public void setUpGui() {
        avenir = Typeface.createFromAsset(getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
        this.txtf1start = (EditText) findViewById(R.id.csa_txtf1start);
        this.txtf1end = (EditText) findViewById(R.id.csa_txtf1end);
        this.txtf2start = (EditText) findViewById(R.id.csa_txtf2start);
        this.txtf2end = (EditText) findViewById(R.id.csa_txtf2end);
        this.txtf3start = (EditText) findViewById(R.id.csa_txtf3start);
        this.txtf3end = (EditText) findViewById(R.id.csa_txtf3end);
        this.txtf4start = (EditText) findViewById(R.id.csa_txtf4start);
        this.txtf4end = (EditText) findViewById(R.id.csa_txtf4end);
        this.lblf1start = (TextView) findViewById(R.id.csa_lblf1start);
        this.lblf1end = (TextView) findViewById(R.id.csa_lblF1end);
        this.lblf1Mode = (TextView) findViewById(R.id.csa_lblf1mode);
        this.lblf2start = (TextView) findViewById(R.id.csa_lblf2start);
        this.lblf2end = (TextView) findViewById(R.id.csa_lblF2end);
        this.lblf2Mode = (TextView) findViewById(R.id.csa_lblf2mode);
        this.lblf3start = (TextView) findViewById(R.id.csa_lblf3start);
        this.lblf3end = (TextView) findViewById(R.id.csa_lblF3end);
        this.lblf3Mode = (TextView) findViewById(R.id.csa_lblf3mode);
        this.lblf4start = (TextView) findViewById(R.id.csa_lblf4start);
        this.lblf4end = (TextView) findViewById(R.id.csa_lblF4end);
        this.lblf4Mode = (TextView) findViewById(R.id.csa_lblf4mode);
        this.lblF1 = (TextView) findViewById(R.id.csa_lblF1);
        this.lblF2 = (TextView) findViewById(R.id.csa_lblF2);
        this.lblF3 = (TextView) findViewById(R.id.csa_lblF3);
        this.lblF4 = (TextView) findViewById(R.id.csa_lblF4);
        this.lblFreccia = (TextView) findViewById(R.id.csa_lblFreccia);
        this.csa_lblFrecciaF1Mode = (TextView) findViewById(R.id.csa_lblFrecciaF1Mode);
        this.csa_lblFrecciaF2Mode = (TextView) findViewById(R.id.csa_lblFrecciaF2Mode);
        this.csa_lblFrecciaF3Mode = (TextView) findViewById(R.id.csa_lblFrecciaF3Mode);
        this.csa_lblFrecciaF4Mode = (TextView) findViewById(R.id.csa_lblFrecciaF4Mode);
        this.csa_spF1Mode = (Spinner) findViewById(R.id.csa_spF1Mode);
        this.csa_spF2Mode = (Spinner) findViewById(R.id.csa_spF2Mode);
        this.csa_spF3Mode = (Spinner) findViewById(R.id.csa_spF3Mode);
        this.csa_spF4Mode = (Spinner) findViewById(R.id.csa_spF4Mode);
        this.lblf1start.setTypeface(avenir);
        this.lblf1end.setTypeface(avenir);
        this.lblf1Mode.setTypeface(avenir);
        this.lblf2start.setTypeface(avenir);
        this.lblf2end.setTypeface(avenir);
        this.lblf2Mode.setTypeface(avenir);
        this.lblf3start.setTypeface(avenir);
        this.lblf3end.setTypeface(avenir);
        this.lblf3Mode.setTypeface(avenir);
        this.lblf4start.setTypeface(avenir);
        this.lblf4end.setTypeface(avenir);
        this.lblf4Mode.setTypeface(avenir);
        this.lblF1.setTypeface(avenir);
        this.lblF2.setTypeface(avenir);
        this.lblF3.setTypeface(avenir);
        this.lblF4.setTypeface(avenir);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "fonts/icomoon.ttf");
        this.lblFreccia.setTypeface(createFromAsset);
        this.csa_lblFrecciaF1Mode.setTypeface(createFromAsset);
        this.csa_lblFrecciaF2Mode.setTypeface(createFromAsset);
        this.csa_lblFrecciaF3Mode.setTypeface(createFromAsset);
        this.csa_lblFrecciaF4Mode.setTypeface(createFromAsset);
        this.days = (Spinner) findViewById(R.id.csa_Spinner);
        ArrayList arrayList = new ArrayList();
        arrayList.add(getResources().getString(R.string.f2cr_Luned));
        arrayList.add(getResources().getString(R.string.f3cr_Marted));
        arrayList.add(getResources().getString(R.string.f4cr_Mercoled));
        arrayList.add(getResources().getString(R.string.f1cr_Gioved));
        arrayList.add(getResources().getString(R.string.f5cr_Venerd));
        arrayList.add(getResources().getString(R.string.cr_Sabato));
        arrayList.add(getResources().getString(R.string.cr_Domenica));
        this.days.setAdapter(new SpinnerAdapter(this, R.layout.spinner_item, arrayList, false));
        this.days.setSelection(index);
        this.days.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                VMCCronoSetActivity.index = i;
                VMCCronoSetActivity.this.setValues();
            }
        });
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add("---");
        arrayList2.add(getResources().getString(R.string.cr_STANDBY));
        arrayList2.add(getResources().getString(R.string.cr_ABSMIN));
        arrayList2.add(getResources().getString(R.string.cr_MINSPEED));
        arrayList2.add(getResources().getString(R.string.cr_MEDSPEED));
        arrayList2.add(getResources().getString(R.string.cr_MAXSPEED));
        arrayList2.add(getResources().getString(R.string.cr_BOOSTMODE));
        arrayList2.add(getResources().getString(R.string.cr_AUTOMODE));
        this.csa_spF1Mode.setAdapter(new SpinnerAdapter(this, R.layout.spinner_item, arrayList2, false));
        this.csa_spF2Mode.setAdapter(new SpinnerAdapter(this, R.layout.spinner_item, arrayList2, false));
        this.csa_spF3Mode.setAdapter(new SpinnerAdapter(this, R.layout.spinner_item, arrayList2, false));
        this.csa_spF4Mode.setAdapter(new SpinnerAdapter(this, R.layout.spinner_item, arrayList2, false));
        this.csa_spF1Mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                if (!VMCCronoSetActivity.this.startupvalue) {
                    VMCCronoSetActivity.this.selectedMode(i, 0);
                }
            }
        });
        this.csa_spF2Mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                if (!VMCCronoSetActivity.this.startupvalue) {
                    VMCCronoSetActivity.this.selectedMode(i, 1);
                }
            }
        });
        this.csa_spF3Mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                if (!VMCCronoSetActivity.this.startupvalue) {
                    VMCCronoSetActivity.this.selectedMode(i, 2);
                }
            }
        });
        this.csa_spF4Mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                if (!VMCCronoSetActivity.this.startupvalue) {
                    VMCCronoSetActivity.this.selectedMode(i, 3);
                }
            }
        });
        this.btndeletef1 = (Button) findViewById(R.id.csa_brnf1Delete);
        this.btndeletef2 = (Button) findViewById(R.id.csa_brnF2Delete);
        this.btndeletef3 = (Button) findViewById(R.id.csa_brnF3Delete);
        this.btndeletef4 = (Button) findViewById(R.id.csa_brnF4Delete);
        this.btnSalva = (Button) findViewById(R.id.csa_btnSalva);
        this.btndeletef1.setTypeface(createFromAsset);
        this.btndeletef2.setTypeface(createFromAsset);
        this.btndeletef3.setTypeface(createFromAsset);
        this.btndeletef4.setTypeface(createFromAsset);
        this.btnSalva.setTypeface(avenir);
        setUpTxtFascia(this.txtf1start, this.txtf1end, this.csa_spF1Mode, 0);
        setUpTxtFascia(this.txtf2start, this.txtf2end, this.csa_spF2Mode, 1);
        setUpTxtFascia(this.txtf3start, this.txtf3end, this.csa_spF3Mode, 2);
        setUpTxtFascia(this.txtf4start, this.txtf4end, this.csa_spF4Mode, 3);
    }

    /* access modifiers changed from: private */
    public void selectedMode(int i, int i2) {
        if (i == 0) {
            VMCCronoSummaryActivity.cronos.get(index).setModeVMC(-1, i2);
            if (i2 == 0) {
                deleteF1((View) null);
            } else if (i2 == 1) {
                deleteF2((View) null);
            } else if (i2 == 2) {
                deleteF3((View) null);
            } else if (i2 == 3) {
                deleteF4((View) null);
            }
        } else {
            VMCCronoSummaryActivity.cronos.get(index).setModeVMC(Integer.valueOf(i), i2);
        }
    }

    public void setUpTxtFascia(final EditText editText, final EditText editText2, Spinner spinner, final int i) {
        editText.setTypeface(avenir);
        editText2.setTypeface(avenir);
        editText.setFocusable(false);
        editText.setClickable(true);
        editText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AnonymousClass1 r6 = new Runnable() {
                    public void run() {
                        VMCCronoSetActivity.this.s = editText.getText().toString();
                        VMCCronoSummaryActivity.cronos.get(VMCCronoSetActivity.index).setStartTime(VMCCronoSetActivity.this.s, i);
                        VMCCronoSetActivity.this.dismissdialog();
                        VMCCronoSetActivity.this.timeStart = VMCCronoSetActivity.this.s;
                    }
                };
                if (!editText.getText().toString().isEmpty()) {
                    VMCCronoSetActivity vMCCronoSetActivity = VMCCronoSetActivity.this;
                    vMCCronoSetActivity.bdl = vMCCronoSetActivity.createChangeNumberPopUp("", 1, editText.getText().toString(), 10, 35, r6, editText);
                } else {
                    String str = VMCCronoSetActivity.this.timeStart;
                    if (i != 0) {
                        try {
                            if (VMCCronoSummaryActivity.cronos.get(VMCCronoSetActivity.index).getStartTime(i - 1) != null) {
                                str = Crono.convertTimeToNormal(VMCCronoSummaryActivity.cronos.get(VMCCronoSetActivity.index).getEndtimeAsInt15(i - 1));
                            }
                        } catch (Exception unused) {
                        }
                    }
                    String str2 = str;
                    VMCCronoSetActivity vMCCronoSetActivity2 = VMCCronoSetActivity.this;
                    vMCCronoSetActivity2.bdl = vMCCronoSetActivity2.createChangeNumberPopUp("", 1, str2, 10, 35, r6, editText);
                }
                VMCCronoSetActivity vMCCronoSetActivity3 = VMCCronoSetActivity.this;
                vMCCronoSetActivity3.openDialogFragment(vMCCronoSetActivity3.bdl);
            }
        });
        editText2.setFocusable(false);
        editText2.setClickable(true);
        editText2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AnonymousClass1 r6 = new Runnable() {
                    public void run() {
                        VMCCronoSetActivity.this.s = editText2.getText().toString();
                        VMCCronoSummaryActivity.cronos.get(VMCCronoSetActivity.index).setEndTime(VMCCronoSetActivity.this.s, i);
                        VMCCronoSetActivity.this.dismissdialog();
                    }
                };
                if (!editText2.getText().toString().isEmpty()) {
                    VMCCronoSetActivity vMCCronoSetActivity = VMCCronoSetActivity.this;
                    vMCCronoSetActivity.bdl = vMCCronoSetActivity.createChangeNumberPopUp("", 1, editText2.getText().toString(), 10, 35, r6, editText2);
                } else {
                    String str = VMCCronoSetActivity.this.timeStart;
                    try {
                        if (VMCCronoSummaryActivity.cronos.get(VMCCronoSetActivity.index).getStartTime(i) != null) {
                            str = Crono.convertTimeToNormal(VMCCronoSummaryActivity.cronos.get(VMCCronoSetActivity.index).getStarttimeAsInt15(i) + 1);
                        }
                    } catch (Exception unused) {
                    }
                    String str2 = str;
                    VMCCronoSetActivity vMCCronoSetActivity2 = VMCCronoSetActivity.this;
                    vMCCronoSetActivity2.bdl = vMCCronoSetActivity2.createChangeNumberPopUp("", 1, str2, 10, 35, r6, editText2);
                }
                VMCCronoSetActivity vMCCronoSetActivity3 = VMCCronoSetActivity.this;
                vMCCronoSetActivity3.openDialogFragment(vMCCronoSetActivity3.bdl);
            }
        });
    }

    public void setValues() {
        this.startupvalue = true;
        setZoneTxtValue(this.txtf1start, this.txtf1end, this.csa_spF1Mode, 0);
        setZoneTxtValue(this.txtf2start, this.txtf2end, this.csa_spF2Mode, 1);
        setZoneTxtValue(this.txtf3start, this.txtf3end, this.csa_spF3Mode, 2);
        setZoneTxtValue(this.txtf4start, this.txtf4end, this.csa_spF4Mode, 3);
        this.startupvalue = false;
    }

    public void setZoneTxtValue(EditText editText, EditText editText2, Spinner spinner, int i) {
        try {
            if (VMCCronoSummaryActivity.cronos == null) {
                Functions.makeErrorToast(this, getResources().getString(R.string.resCodeError));
            } else if (VMCCronoSummaryActivity.cronos.get(index).getStartTime(i) != null) {
                editText.setText(VMCCronoSummaryActivity.cronos.get(index).getStartTime(i));
                editText2.setText(VMCCronoSummaryActivity.cronos.get(index).getEndTime(i));
                if (VMCCronoSummaryActivity.cronos.get(index).getVMCModeIndex(i) == null) {
                    spinner.setSelection(0);
                } else {
                    spinner.setSelection(VMCCronoSummaryActivity.cronos.get(index).getVMCModeIndex(i).intValue());
                }
            } else {
                editText.setText("");
                editText2.setText("");
                spinner.setSelection(0);
            }
        } catch (Exception unused) {
            editText.setText("");
            editText2.setText("");
            spinner.setSelection(0);
        }
    }

    public void deleteF1(View view) {
        this.txtf1start.setText("");
        this.txtf1end.setText("");
        this.csa_spF1Mode.setSelection(0);
        VMCCronoSummaryActivity.cronos.get(index).setStartTime((String) null, 0);
        VMCCronoSummaryActivity.cronos.get(index).setEndTime((String) null, 0);
    }

    public void deleteF2(View view) {
        this.txtf2start.setText("");
        this.txtf2end.setText("");
        this.csa_spF2Mode.setSelection(0);
        VMCCronoSummaryActivity.cronos.get(index).setStartTime((String) null, 1);
        VMCCronoSummaryActivity.cronos.get(index).setEndTime((String) null, 1);
    }

    public void deleteF3(View view) {
        this.txtf3start.setText("");
        this.txtf3end.setText("");
        this.csa_spF3Mode.setSelection(0);
        VMCCronoSummaryActivity.cronos.get(index).setStartTime((String) null, 2);
        VMCCronoSummaryActivity.cronos.get(index).setEndTime((String) null, 2);
    }

    public void deleteF4(View view) {
        this.txtf4start.setText("");
        this.txtf4end.setText("");
        this.csa_spF4Mode.setSelection(0);
        VMCCronoSummaryActivity.cronos.get(index).setStartTime((String) null, 3);
        VMCCronoSummaryActivity.cronos.get(index).setEndTime((String) null, 3);
    }

    public void btnSave(View view) {
        saveValue();
    }

    private boolean controllofasce() {
        for (int i = 0; i < 7; i++) {
            PICOCronoObj pICOCronoObj = VMCCronoSummaryActivity.cronos.get(i);
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < 4; i2++) {
                if (pICOCronoObj.getStartTime(i2) == null) {
                    arrayList.add(true);
                } else if (i2 == 0 || !((Boolean) arrayList.get(i2 - 1)).booleanValue()) {
                    arrayList.add(false);
                    if (pICOCronoObj.getMode(i2).intValue() == -1 || pICOCronoObj.getEndTime(i2) == null) {
                        Functions.makeErrorToast(this, getResources().getStringArray(R.array.cu_errorFasce)[i]);
                        return false;
                    }
                } else {
                    Functions.makeErrorToast(this, getResources().getStringArray(R.array.cu_errorFasceTemp)[i]);
                    return false;
                }
            }
            if (!pICOCronoObj.isok()) {
                Functions.makeErrorToast(this, getResources().getStringArray(R.array.cu_errorFasceTemp)[i]);
                return false;
            }
        }
        return true;
    }

    private boolean checkRespSetMode(String str) {
        if (str == null) {
            return false;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (!jSONObject.has("res") || jSONObject.getInt("res") != 1) {
                return false;
            }
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void parseRespSetFasce(String str) {
        try {
            if (checkRespSetMode(str)) {
                Functions.makeNormalToast(this.activity, getResources().getString(R.string.msg_commandOk));
                hideProgress();
                return;
            }
        } catch (Exception unused) {
        }
        Functions.makeErrorToast(this.activity, getResources().getString(R.string.msg_commandKo));
        hideProgress();
    }

    public void saveValue() {
        if (controllofasce()) {
            if (Constants.ISDEMO) {
                showProgress();
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (Exception unused) {
                        }
                        VMCCronoSetActivity.this.hideProgress();
                        VMCCronoSetActivity.this.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Functions.makeNormalToast(VMCCronoSetActivity.this.activity, VMCCronoSetActivity.this.getResources().getString(R.string.cr_salvaOk));
                            }
                        });
                    }
                }).start();
                return;
            }
            final CmdPICO.PICO_Fasce pICO_Fasce = new CmdPICO.PICO_Fasce();
            pICO_Fasce.setCmd("set_fasce");
            pICO_Fasce.setPin(VMCActivity.vmc.getPin());
            pICO_Fasce.setIdp((long) VMCActivity.getIDP());
            pICO_Fasce.setM_crono(1);
            pICO_Fasce.setD0(VMCCronoSummaryActivity.cronos.get(0).getVMCFasce());
            pICO_Fasce.setD1(VMCCronoSummaryActivity.cronos.get(1).getVMCFasce());
            pICO_Fasce.setD2(VMCCronoSummaryActivity.cronos.get(2).getVMCFasce());
            pICO_Fasce.setD3(VMCCronoSummaryActivity.cronos.get(3).getVMCFasce());
            pICO_Fasce.setD4(VMCCronoSummaryActivity.cronos.get(4).getVMCFasce());
            pICO_Fasce.setD5(VMCCronoSummaryActivity.cronos.get(5).getVMCFasce());
            pICO_Fasce.setD6(VMCCronoSummaryActivity.cronos.get(6).getVMCFasce());
            showProgress();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        if (VMCActivity.vmc.getOffline().booleanValue()) {
                            VMCCronoSetActivity.this.parseRespSetFasce(UDPSocket.sendCMD(pICO_Fasce));
                            VMCCronoSetActivity.this.hideProgress();
                            return;
                        }
                        PICOServerTimezone pICOServerTimezone = new PICOServerTimezone();
                        pICOServerTimezone.setSerial(VMCActivity.vmc.getSerial());
                        pICOServerTimezone.setPin(VMCActivity.vmc.getPin());
                        CmdPICO.PICO_Fasce pICO_Fasce = pICO_Fasce;
                        if (pICO_Fasce != null) {
                            pICO_Fasce.setFrm("mqtt");
                            pICOServerTimezone.setCmd(new Gson().toJson((Object) pICO_Fasce));
                        }
                        Log.d("TAG", pICOServerTimezone.getCmd());
                        Response makeApiCall = VMCCronoSetActivity.this.makeApiCall(VMCCronoSetActivity.this.getResources().getString(R.string.uriWebService) + VMCCronoSetActivity.this.getResources().getString(R.string.uri_SendVMCCmd), new Gson().toJson((Object) pICOServerTimezone), 1, 0, Constants.user, false);
                        if (makeApiCall != null) {
                            JSONObject jSONObject = new JSONObject(makeApiCall.getHttpResponcePayload());
                            if (jSONObject.getInt("ResCode") == 0) {
                                VMCCronoSetActivity.this.parseRespSetFasce(jSONObject.getString("ResDescr"));
                                VMCCronoSetActivity.this.hideProgress();
                                return;
                            }
                        }
                        Functions.makeErrorToast(VMCCronoSetActivity.this.activity, VMCCronoSetActivity.this.getResources().getString(R.string.msg_commandKo));
                        VMCCronoSetActivity.this.hideProgress();
                    } catch (Exception unused) {
                    }
                }
            }).start();
        }
    }

    public View getToolBar() {
        return findViewById(R.id.vmc_toolbar);
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        AnonymousClass10 r5 = new Runnable() {
            public void run() {
                VMCCronoSetActivity.this.dismissdialog();
                VMCCronoSetActivity.indexZonetoCopy = VMCCronoSetActivity.index;
            }
        };
        list.add(createMenuItem(true, getResources().getString(R.string.cr_menuCopia), "", "", r5, false, false));
        AnonymousClass11 r6 = new Runnable() {
            public void run() {
                VMCCronoSetActivity.this.dismissdialog();
                if (VMCCronoSetActivity.indexZonetoCopy == -1) {
                    Functions.makeNormalToast(VMCCronoSetActivity.this.activity, VMCCronoSetActivity.this.getResources().getString(R.string.cr_erroreIncolla));
                    return;
                }
                VMCCronoSummaryActivity.cronos.get(VMCCronoSetActivity.index).setValues(VMCCronoSummaryActivity.cronos.get(VMCCronoSetActivity.indexZonetoCopy).getValues());
                VMCCronoSummaryActivity.cronos.get(VMCCronoSetActivity.index).setTimeListsVMC();
                VMCCronoSetActivity.this.setValues();
            }
        };
        list.add(createMenuItem(false, getResources().getString(R.string.cr_menuIncolla), "", "", r6, false, false));
        return list;
    }

    public String setToolbarTitle() {
        return VMCActivity.vmc.getName();
    }
}
