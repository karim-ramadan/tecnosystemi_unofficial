package it.tecnosystemi.TS.Activity.PICO;

import android.graphics.Typeface;
import android.os.Bundle;
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
import it.tecnosystemi.TS.Threads.ThreadWebService;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class PicoCronoSetActivity extends BaseActivity {
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
    TextView csa_lblFrecciaF1Val;
    TextView csa_lblFrecciaF2Mode;
    TextView csa_lblFrecciaF2Val;
    TextView csa_lblFrecciaF3Mode;
    TextView csa_lblFrecciaF3Val;
    TextView csa_lblFrecciaF4Mode;
    TextView csa_lblFrecciaF4Val;
    TextView csa_lblf1val;
    TextView csa_lblf2val;
    TextView csa_lblf3val;
    TextView csa_lblf4val;
    Spinner csa_spF1Mode;
    Spinner csa_spF1Val;
    Spinner csa_spF2Mode;
    Spinner csa_spF2Val;
    Spinner csa_spF3Mode;
    Spinner csa_spF3Val;
    Spinner csa_spF4Mode;
    Spinner csa_spF4Val;
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
        setContentView(R.layout.activity_pico_crono_set);
        this.typeActStyle = 2;
        this.activity = this;
        super.onCreate(bundle);
        setUpGui();
        setValues();
    }

    private boolean controllofasce() {
        for (int i = 0; i < 7; i++) {
            PICOCronoObj pICOCronoObj = PICOCronoSummaryActivity.cronos.get(i);
            ArrayList arrayList = new ArrayList();
            for (int i2 = 0; i2 < 4; i2++) {
                if (pICOCronoObj.getStartTime(i2) == null) {
                    arrayList.add(true);
                } else if (i2 == 0 || !((Boolean) arrayList.get(i2 - 1)).booleanValue()) {
                    arrayList.add(false);
                    if (pICOCronoObj.getMode(i2).intValue() == 0 || pICOCronoObj.getEndTime(i2) == null) {
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

    public void parseRespSetFasceServer(Response response, int i) {
        if (response != null) {
            try {
                if (response.getHttpResponceCode() == 200) {
                    JSONObject jSONObject = new JSONObject(response.getHttpResponcePayload());
                    if (jSONObject.has("ResCode") && jSONObject.getInt("ResCode") == 0) {
                        parseRespSetFasce(jSONObject.getString("ResDescr"));
                        hideProgress();
                        return;
                    }
                }
            } catch (Exception unused) {
            }
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
                        PicoCronoSetActivity.this.hideProgress();
                        PicoCronoSetActivity.this.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Functions.makeNormalToast(PicoCronoSetActivity.this.activity, PicoCronoSetActivity.this.getResources().getString(R.string.cr_salvaOk));
                            }
                        });
                    }
                }).start();
                return;
            }
            final CmdPICO.PICO_Fasce pICO_Fasce = new CmdPICO.PICO_Fasce();
            pICO_Fasce.setCmd("set_fasce");
            pICO_Fasce.setPin(PicoActivity.pico.getPin());
            pICO_Fasce.setIdp((long) PicoActivity.getIDP());
            pICO_Fasce.setM_crono(1);
            pICO_Fasce.setD0(PICOCronoSummaryActivity.cronos.get(0).getPicoFasce());
            pICO_Fasce.setD1(PICOCronoSummaryActivity.cronos.get(1).getPicoFasce());
            pICO_Fasce.setD2(PICOCronoSummaryActivity.cronos.get(2).getPicoFasce());
            pICO_Fasce.setD3(PICOCronoSummaryActivity.cronos.get(3).getPicoFasce());
            pICO_Fasce.setD4(PICOCronoSummaryActivity.cronos.get(4).getPicoFasce());
            pICO_Fasce.setD5(PICOCronoSummaryActivity.cronos.get(5).getPicoFasce());
            pICO_Fasce.setD6(PICOCronoSummaryActivity.cronos.get(6).getPicoFasce());
            showProgress();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        if (PicoActivity.pico.getOffline().booleanValue()) {
                            PicoCronoSetActivity.this.parseRespSetFasce(UDPSocket.sendCMD(pICO_Fasce));
                            return;
                        }
                        PICOServerTimezone pICOServerTimezone = new PICOServerTimezone();
                        pICOServerTimezone.setSerial(PicoActivity.pico.getSerial());
                        pICOServerTimezone.setPin(PicoActivity.pico.getPin());
                        CmdPICO.PICO_Fasce pICO_Fasce = pICO_Fasce;
                        if (pICO_Fasce != null) {
                            pICO_Fasce.setFrm("mqtt");
                            pICOServerTimezone.setCmd(new Gson().toJson((Object) pICO_Fasce));
                        }
                        new ThreadWebService(PicoCronoSetActivity.this.activity, 1, 34, PicoCronoSetActivity.this.getResources().getString(R.string.uriWebService_PICO) + PicoCronoSetActivity.this.getResources().getString(R.string.uri_SendPicoCmd), new Gson().toJson((Object) pICOServerTimezone), (String[]) null).start();
                    } catch (Exception unused) {
                        PicoCronoSetActivity.this.hideProgress();
                    }
                }
            }).start();
        }
    }

    public void btnSave(View view) {
        saveValue();
    }

    public void deleteF1(View view) {
        this.txtf1start.setText("");
        this.txtf1end.setText("");
        this.csa_spF1Mode.setSelection(0);
        PICOCronoSummaryActivity.cronos.get(index).setStartTime((String) null, 0);
        PICOCronoSummaryActivity.cronos.get(index).setEndTime((String) null, 0);
    }

    public void deleteF2(View view) {
        this.txtf2start.setText("");
        this.txtf2end.setText("");
        this.csa_spF2Mode.setSelection(0);
        PICOCronoSummaryActivity.cronos.get(index).setStartTime((String) null, 1);
        PICOCronoSummaryActivity.cronos.get(index).setEndTime((String) null, 1);
    }

    public void deleteF3(View view) {
        this.txtf3start.setText("");
        this.txtf3end.setText("");
        this.csa_spF3Mode.setSelection(0);
        PICOCronoSummaryActivity.cronos.get(index).setStartTime((String) null, 2);
        PICOCronoSummaryActivity.cronos.get(index).setEndTime((String) null, 2);
    }

    public void deleteF4(View view) {
        this.txtf4start.setText("");
        this.txtf4end.setText("");
        this.csa_spF4Mode.setSelection(0);
        PICOCronoSummaryActivity.cronos.get(index).setStartTime((String) null, 3);
        PICOCronoSummaryActivity.cronos.get(index).setEndTime((String) null, 3);
    }

    public void setUpTxtFascia(final EditText editText, final EditText editText2, Spinner spinner, Spinner spinner2, final int i) {
        editText.setTypeface(avenir);
        editText2.setTypeface(avenir);
        editText.setFocusable(false);
        editText.setClickable(true);
        editText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AnonymousClass1 r6 = new Runnable() {
                    public void run() {
                        PicoCronoSetActivity.this.s = editText.getText().toString();
                        PICOCronoSummaryActivity.cronos.get(PicoCronoSetActivity.index).setStartTime(PicoCronoSetActivity.this.s, i);
                        PicoCronoSetActivity.this.dismissdialog();
                        PicoCronoSetActivity.this.timeStart = PicoCronoSetActivity.this.s;
                    }
                };
                if (!editText.getText().toString().isEmpty()) {
                    PicoCronoSetActivity picoCronoSetActivity = PicoCronoSetActivity.this;
                    picoCronoSetActivity.bdl = picoCronoSetActivity.createChangeNumberPopUp("", 1, editText.getText().toString(), 10, 35, r6, editText);
                } else {
                    String str = PicoCronoSetActivity.this.timeStart;
                    if (i != 0) {
                        try {
                            if (PICOCronoSummaryActivity.cronos.get(PicoCronoSetActivity.index).getStartTime(i - 1) != null) {
                                str = Crono.convertTimeToNormal(PICOCronoSummaryActivity.cronos.get(PicoCronoSetActivity.index).getEndtimeAsInt15(i - 1));
                            }
                        } catch (Exception unused) {
                        }
                    }
                    String str2 = str;
                    PicoCronoSetActivity picoCronoSetActivity2 = PicoCronoSetActivity.this;
                    picoCronoSetActivity2.bdl = picoCronoSetActivity2.createChangeNumberPopUp("", 1, str2, 10, 35, r6, editText);
                }
                PicoCronoSetActivity picoCronoSetActivity3 = PicoCronoSetActivity.this;
                picoCronoSetActivity3.openDialogFragment(picoCronoSetActivity3.bdl);
            }
        });
        editText2.setFocusable(false);
        editText2.setClickable(true);
        editText2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AnonymousClass1 r6 = new Runnable() {
                    public void run() {
                        PicoCronoSetActivity.this.s = editText2.getText().toString();
                        PICOCronoSummaryActivity.cronos.get(PicoCronoSetActivity.index).setEndTime(PicoCronoSetActivity.this.s, i);
                        PicoCronoSetActivity.this.dismissdialog();
                    }
                };
                if (!editText2.getText().toString().isEmpty()) {
                    PicoCronoSetActivity picoCronoSetActivity = PicoCronoSetActivity.this;
                    picoCronoSetActivity.bdl = picoCronoSetActivity.createChangeNumberPopUp("", 1, editText2.getText().toString(), 10, 35, r6, editText2);
                } else {
                    String str = PicoCronoSetActivity.this.timeStart;
                    try {
                        if (PICOCronoSummaryActivity.cronos.get(PicoCronoSetActivity.index).getStartTime(i) != null) {
                            str = Crono.convertTimeToNormal(PICOCronoSummaryActivity.cronos.get(PicoCronoSetActivity.index).getStarttimeAsInt15(i) + 1);
                        }
                    } catch (Exception unused) {
                    }
                    String str2 = str;
                    PicoCronoSetActivity picoCronoSetActivity2 = PicoCronoSetActivity.this;
                    picoCronoSetActivity2.bdl = picoCronoSetActivity2.createChangeNumberPopUp("", 1, str2, 10, 35, r6, editText2);
                }
                PicoCronoSetActivity picoCronoSetActivity3 = PicoCronoSetActivity.this;
                picoCronoSetActivity3.openDialogFragment(picoCronoSetActivity3.bdl);
            }
        });
    }

    private void setValueVisible(int i, int i2, boolean z) {
        if (i == 0) {
            this.csa_lblf1val.setVisibility(i2);
            this.csa_spF1Val.setVisibility(i2);
            this.csa_lblFrecciaF1Val.setVisibility(i2);
            this.csa_lblf1val.setText(z ? R.string.cr_pico_txtVelocita : R.string.cr_pico_txtUmidita);
        } else if (i == 1) {
            this.csa_lblf2val.setVisibility(i2);
            this.csa_spF2Val.setVisibility(i2);
            this.csa_lblFrecciaF2Val.setVisibility(i2);
            this.csa_lblf2val.setText(z ? R.string.cr_pico_txtVelocita : R.string.cr_pico_txtUmidita);
        } else if (i == 2) {
            this.csa_lblf3val.setVisibility(i2);
            this.csa_spF3Val.setVisibility(i2);
            this.csa_lblFrecciaF3Val.setVisibility(i2);
            this.csa_lblf3val.setText(z ? R.string.cr_pico_txtVelocita : R.string.cr_pico_txtUmidita);
        } else if (i == 3) {
            this.csa_lblf4val.setVisibility(i2);
            this.csa_spF4Val.setVisibility(i2);
            this.csa_lblFrecciaF4Val.setVisibility(i2);
            this.csa_lblf4val.setText(z ? R.string.cr_pico_txtVelocita : R.string.cr_pico_txtUmidita);
        }
    }

    /* access modifiers changed from: private */
    public void selectedMode(Spinner spinner, int i, int i2) {
        PICOCronoSummaryActivity.cronos.get(index).setMode(Integer.valueOf(i), i2);
        ArrayList arrayList = new ArrayList();
        if (i == 1 || i == 2 || i == 3 || i == 6 || i == 7) {
            setValueVisible(i2, 0, true);
            arrayList.add(getResources().getString(R.string.cr_Speed1));
            arrayList.add(getResources().getString(R.string.cr_Speed2));
            arrayList.add(getResources().getString(R.string.cr_Speed3));
            arrayList.add(getResources().getString(R.string.cr_Speed4));
        } else if (i == 4 || i == 5 || i == 10 || i == 11) {
            setValueVisible(i2, 0, false);
            arrayList.add(getResources().getString(R.string.cr_Umd1));
            arrayList.add(getResources().getString(R.string.cr_Umd2));
            arrayList.add(getResources().getString(R.string.cr_Umd3));
        } else {
            setValueVisible(i2, 4, true);
            PICOCronoSummaryActivity.cronos.get(index).setValueToWrite(0, i2);
            return;
        }
        spinner.setAdapter(new SpinnerAdapter(this, R.layout.spinner_item, arrayList, false));
        PICOCronoSummaryActivity.cronos.get(index).setValueToWrite(1, i2);
    }

    public void setZoneTxtValue(EditText editText, EditText editText2, Spinner spinner, Spinner spinner2, int i) {
        try {
            if (PICOCronoSummaryActivity.cronos == null) {
                Functions.makeErrorToast(this, getResources().getString(R.string.resCodeError));
            } else if (PICOCronoSummaryActivity.cronos.get(index).getStartTime(i) != null) {
                editText.setText(PICOCronoSummaryActivity.cronos.get(index).getStartTime(i));
                editText2.setText(PICOCronoSummaryActivity.cronos.get(index).getEndTime(i));
                if (PICOCronoSummaryActivity.cronos.get(index).getMode(i) == null) {
                    spinner.setSelection(0);
                    setValueVisible(4, i, true);
                    return;
                }
                spinner.setSelection(PICOCronoSummaryActivity.cronos.get(index).getMode(i).intValue());
                selectedMode(spinner2, PICOCronoSummaryActivity.cronos.get(index).getMode(i).intValue(), i);
                if (PICOCronoSummaryActivity.cronos.get(index).getValueToWrite(i) != null) {
                    spinner2.setSelection(PICOCronoSummaryActivity.cronos.get(index).getValueToWrite(i).intValue() - 1);
                }
            } else {
                editText.setText("");
                editText2.setText("");
                spinner.setSelection(0);
                setValueVisible(4, i, true);
            }
        } catch (Exception unused) {
            editText.setText("");
            editText2.setText("");
            spinner.setSelection(0);
            setValueVisible(4, i, true);
        }
    }

    public void setValues() {
        this.startupvalue = true;
        setZoneTxtValue(this.txtf1start, this.txtf1end, this.csa_spF1Mode, this.csa_spF1Val, 0);
        setZoneTxtValue(this.txtf2start, this.txtf2end, this.csa_spF2Mode, this.csa_spF2Val, 1);
        setZoneTxtValue(this.txtf3start, this.txtf3end, this.csa_spF3Mode, this.csa_spF3Val, 2);
        setZoneTxtValue(this.txtf4start, this.txtf4end, this.csa_spF4Mode, this.csa_spF4Val, 3);
        this.startupvalue = false;
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
        this.csa_lblFrecciaF1Val = (TextView) findViewById(R.id.csa_lblFrecciaF1Val);
        this.csa_lblFrecciaF2Mode = (TextView) findViewById(R.id.csa_lblFrecciaF2Mode);
        this.csa_lblFrecciaF2Val = (TextView) findViewById(R.id.csa_lblFrecciaF2Val);
        this.csa_lblFrecciaF3Mode = (TextView) findViewById(R.id.csa_lblFrecciaF3Mode);
        this.csa_lblFrecciaF3Val = (TextView) findViewById(R.id.csa_lblFrecciaF3Val);
        this.csa_lblFrecciaF4Mode = (TextView) findViewById(R.id.csa_lblFrecciaF4Mode);
        this.csa_lblFrecciaF4Val = (TextView) findViewById(R.id.csa_lblFrecciaF4Val);
        this.csa_lblf1val = (TextView) findViewById(R.id.csa_lblf1val);
        this.csa_lblf2val = (TextView) findViewById(R.id.csa_lblf2val);
        this.csa_lblf3val = (TextView) findViewById(R.id.csa_lblf3val);
        this.csa_lblf4val = (TextView) findViewById(R.id.csa_lblf4val);
        this.csa_spF1Mode = (Spinner) findViewById(R.id.csa_spF1Mode);
        this.csa_spF1Val = (Spinner) findViewById(R.id.csa_spF1Val);
        this.csa_spF2Mode = (Spinner) findViewById(R.id.csa_spF2Mode);
        this.csa_spF2Val = (Spinner) findViewById(R.id.csa_spF2Val);
        this.csa_spF3Mode = (Spinner) findViewById(R.id.csa_spF3Mode);
        this.csa_spF3Val = (Spinner) findViewById(R.id.csa_spF3Val);
        this.csa_spF4Mode = (Spinner) findViewById(R.id.csa_spF4Mode);
        this.csa_spF4Val = (Spinner) findViewById(R.id.csa_spF4Val);
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
        this.csa_lblf1val.setTypeface(avenir);
        this.csa_lblf2val.setTypeface(avenir);
        this.csa_lblf3val.setTypeface(avenir);
        this.csa_lblf4val.setTypeface(avenir);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "fonts/icomoon.ttf");
        this.lblFreccia.setTypeface(createFromAsset);
        this.csa_lblFrecciaF1Mode.setTypeface(createFromAsset);
        this.csa_lblFrecciaF1Val.setTypeface(createFromAsset);
        this.csa_lblFrecciaF2Mode.setTypeface(createFromAsset);
        this.csa_lblFrecciaF2Val.setTypeface(createFromAsset);
        this.csa_lblFrecciaF3Mode.setTypeface(createFromAsset);
        this.csa_lblFrecciaF3Val.setTypeface(createFromAsset);
        this.csa_lblFrecciaF4Mode.setTypeface(createFromAsset);
        this.csa_lblFrecciaF4Val.setTypeface(createFromAsset);
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
                PicoCronoSetActivity.index = i;
                PicoCronoSetActivity.this.setValues();
            }
        });
        ArrayList arrayList2 = new ArrayList();
        arrayList2.add("---");
        arrayList2.add(getResources().getString(R.string.cr_Recuperatore));
        arrayList2.add(getResources().getString(R.string.cr_Estrazione));
        arrayList2.add(getResources().getString(R.string.cr_Immissione));
        arrayList2.add(getResources().getString(R.string.cr_Auto1));
        arrayList2.add(getResources().getString(R.string.cr_Auto2));
        arrayList2.add(getResources().getString(R.string.cr_Auto3));
        arrayList2.add(getResources().getString(R.string.cr_Auto4));
        arrayList2.add(getResources().getString(R.string.cr_Auto5));
        arrayList2.add(getResources().getString(R.string.cr_Auto6));
        arrayList2.add(getResources().getString(R.string.cr_Auto7));
        arrayList2.add(getResources().getString(R.string.cr_Auto8));
        arrayList2.add(getResources().getString(R.string.cr_RicambioNaturale));
        this.csa_spF1Mode.setAdapter(new SpinnerAdapter(this, R.layout.spinner_item, arrayList2, false));
        this.csa_spF2Mode.setAdapter(new SpinnerAdapter(this, R.layout.spinner_item, arrayList2, false));
        this.csa_spF3Mode.setAdapter(new SpinnerAdapter(this, R.layout.spinner_item, arrayList2, false));
        this.csa_spF4Mode.setAdapter(new SpinnerAdapter(this, R.layout.spinner_item, arrayList2, false));
        this.csa_spF1Mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                if (!PicoCronoSetActivity.this.startupvalue) {
                    PicoCronoSetActivity picoCronoSetActivity = PicoCronoSetActivity.this;
                    picoCronoSetActivity.selectedMode(picoCronoSetActivity.csa_spF1Val, i, 0);
                }
            }
        });
        this.csa_spF2Mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                if (!PicoCronoSetActivity.this.startupvalue) {
                    PicoCronoSetActivity picoCronoSetActivity = PicoCronoSetActivity.this;
                    picoCronoSetActivity.selectedMode(picoCronoSetActivity.csa_spF2Val, i, 1);
                }
            }
        });
        this.csa_spF3Mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                if (!PicoCronoSetActivity.this.startupvalue) {
                    PicoCronoSetActivity picoCronoSetActivity = PicoCronoSetActivity.this;
                    picoCronoSetActivity.selectedMode(picoCronoSetActivity.csa_spF3Val, i, 2);
                }
            }
        });
        this.csa_spF4Mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                if (!PicoCronoSetActivity.this.startupvalue) {
                    PicoCronoSetActivity picoCronoSetActivity = PicoCronoSetActivity.this;
                    picoCronoSetActivity.selectedMode(picoCronoSetActivity.csa_spF4Val, i, 3);
                }
            }
        });
        this.csa_spF1Val.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                PICOCronoSummaryActivity.cronos.get(PicoCronoSetActivity.index).setValueToWrite(Integer.valueOf(i + 1), 0);
            }
        });
        this.csa_spF2Val.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                PICOCronoSummaryActivity.cronos.get(PicoCronoSetActivity.index).setValueToWrite(Integer.valueOf(i + 1), 1);
            }
        });
        this.csa_spF3Val.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                PICOCronoSummaryActivity.cronos.get(PicoCronoSetActivity.index).setValueToWrite(Integer.valueOf(i + 1), 2);
            }
        });
        this.csa_spF4Val.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                PICOCronoSummaryActivity.cronos.get(PicoCronoSetActivity.index).setValueToWrite(Integer.valueOf(i + 1), 3);
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
        setUpTxtFascia(this.txtf1start, this.txtf1end, this.csa_spF1Mode, this.csa_spF1Val, 0);
        setUpTxtFascia(this.txtf2start, this.txtf2end, this.csa_spF2Mode, this.csa_spF2Val, 1);
        setUpTxtFascia(this.txtf3start, this.txtf3end, this.csa_spF3Mode, this.csa_spF3Val, 2);
        setUpTxtFascia(this.txtf4start, this.txtf4end, this.csa_spF4Mode, this.csa_spF4Val, 3);
    }

    public View getToolBar() {
        return findViewById(R.id.ewa_toolbar);
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        AnonymousClass14 r5 = new Runnable() {
            public void run() {
                PicoCronoSetActivity.this.dismissdialog();
                PicoCronoSetActivity.indexZonetoCopy = PicoCronoSetActivity.index;
            }
        };
        list.add(createMenuItem(true, getResources().getString(R.string.cr_menuCopia), "", "", r5, false, false));
        AnonymousClass15 r6 = new Runnable() {
            public void run() {
                PicoCronoSetActivity.this.dismissdialog();
                if (PicoCronoSetActivity.indexZonetoCopy == -1) {
                    Functions.makeNormalToast(PicoCronoSetActivity.this.activity, PicoCronoSetActivity.this.getResources().getString(R.string.cr_erroreIncolla));
                    return;
                }
                PICOCronoSummaryActivity.cronos.get(PicoCronoSetActivity.index).setValues(PICOCronoSummaryActivity.cronos.get(PicoCronoSetActivity.indexZonetoCopy).getValues());
                PICOCronoSummaryActivity.cronos.get(PicoCronoSetActivity.index).setTimeLists();
                PicoCronoSetActivity.this.setValues();
            }
        };
        list.add(createMenuItem(false, getResources().getString(R.string.cr_menuIncolla), "", "", r6, false, false));
        return list;
    }

    public String setToolbarTitle() {
        return PicoActivity.pico.getName();
    }
}
