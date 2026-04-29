package it.tecnosystemi.TS.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Adapters.SpinnerAdapter;
import it.tecnosystemi.TS.Commands.MySocket;
import it.tecnosystemi.TS.Model.ControlUnit;
import it.tecnosystemi.TS.Model.Crono;
import it.tecnosystemi.TS.Model.Zona;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class CronoSetActivity extends BaseActivity {
    Typeface avenir;
    BaseActivity.BundleMenuList bdl;
    Button btnSalva;
    Button btndeletef1;
    Button btndeletef2;
    Button btndeletef3;
    Button btndeletef4;
    Spinner days;
    int index;
    int indexZonetoCopy;
    TextView lblF1;
    TextView lblF2;
    TextView lblF3;
    TextView lblF4;
    TextView lblFreccia;
    TextView lblf1Temp;
    TextView lblf1end;
    TextView lblf1start;
    TextView lblf2Temp;
    TextView lblf2end;
    TextView lblf2start;
    TextView lblf3Temp;
    TextView lblf3end;
    TextView lblf3start;
    TextView lblf4Temp;
    TextView lblf4end;
    TextView lblf4start;
    int max = 35;
    int min = 10;
    String s;
    String tempStartC = "20";
    String tempStartF = "70";
    String timeStart = "00:00";
    EditText txtf1Temp;
    EditText txtf1end;
    EditText txtf1start;
    EditText txtf2Temp;
    EditText txtf2end;
    EditText txtf2start;
    EditText txtf3Temp;
    EditText txtf3end;
    EditText txtf3start;
    EditText txtf4Temp;
    EditText txtf4end;
    EditText txtf4start;
    int unitm;
    Zona zona;

    public BaseActivity getActivity() {
        return this;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_crono_set);
        Intent intent = getIntent();
        this.zona = (Zona) intent.getSerializableExtra(Constants.INTENT_ZONA);
        this.index = intent.getIntExtra("index", 0);
        this.cu = (ControlUnit) intent.getSerializableExtra(Constants.INTENT_CU);
        this.unitm = this.cu.getUnitOfMesure();
        super.onCreate(bundle);
        this.activity = this;
        setUpGui();
        setValues();
        this.indexZonetoCopy = -1;
    }

    public void setUpGui() {
        this.avenir = Typeface.createFromAsset(getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
        this.txtf1start = (EditText) findViewById(R.id.csa_txtf1start);
        this.txtf1end = (EditText) findViewById(R.id.csa_txtf1end);
        EditText editText = (EditText) findViewById(R.id.csa_txtf1temp);
        this.txtf1Temp = editText;
        setUpTxtFascia(this.txtf1start, this.txtf1end, editText, 0);
        this.txtf2start = (EditText) findViewById(R.id.csa_txtf2start);
        this.txtf2end = (EditText) findViewById(R.id.csa_txtf2end);
        EditText editText2 = (EditText) findViewById(R.id.csa_txtf2temp);
        this.txtf2Temp = editText2;
        setUpTxtFascia(this.txtf2start, this.txtf2end, editText2, 1);
        this.txtf3start = (EditText) findViewById(R.id.csa_txtf3start);
        this.txtf3end = (EditText) findViewById(R.id.csa_txtf3end);
        EditText editText3 = (EditText) findViewById(R.id.csa_txtf3temp);
        this.txtf3Temp = editText3;
        setUpTxtFascia(this.txtf3start, this.txtf3end, editText3, 2);
        this.txtf4start = (EditText) findViewById(R.id.csa_txtf4start);
        this.txtf4end = (EditText) findViewById(R.id.csa_txtf4end);
        EditText editText4 = (EditText) findViewById(R.id.csa_txtf4temp);
        this.txtf4Temp = editText4;
        setUpTxtFascia(this.txtf4start, this.txtf4end, editText4, 3);
        this.lblf1start = (TextView) findViewById(R.id.csa_lblf1start);
        this.lblf1end = (TextView) findViewById(R.id.csa_lblF1end);
        this.lblf1Temp = (TextView) findViewById(R.id.csa_lblf1temp);
        this.lblf2start = (TextView) findViewById(R.id.csa_lblf2start);
        this.lblf2end = (TextView) findViewById(R.id.csa_lblF2end);
        this.lblf2Temp = (TextView) findViewById(R.id.csa_lblf2temp);
        this.lblf3start = (TextView) findViewById(R.id.csa_lblf3start);
        this.lblf3end = (TextView) findViewById(R.id.csa_lblF3end);
        this.lblf3Temp = (TextView) findViewById(R.id.csa_lblf3temp);
        this.lblf4start = (TextView) findViewById(R.id.csa_lblf4start);
        this.lblf4end = (TextView) findViewById(R.id.csa_lblF4end);
        this.lblf4Temp = (TextView) findViewById(R.id.csa_lblf4temp);
        this.lblF1 = (TextView) findViewById(R.id.csa_lblF1);
        this.lblF2 = (TextView) findViewById(R.id.csa_lblF2);
        this.lblF3 = (TextView) findViewById(R.id.csa_lblF3);
        this.lblF4 = (TextView) findViewById(R.id.csa_lblF4);
        this.lblFreccia = (TextView) findViewById(R.id.csa_lblFreccia);
        this.lblf1start.setTypeface(this.avenir);
        this.lblf1end.setTypeface(this.avenir);
        this.lblf1Temp.setTypeface(this.avenir);
        this.lblf2start.setTypeface(this.avenir);
        this.lblf2end.setTypeface(this.avenir);
        this.lblf2Temp.setTypeface(this.avenir);
        this.lblf3start.setTypeface(this.avenir);
        this.lblf3end.setTypeface(this.avenir);
        this.lblf3Temp.setTypeface(this.avenir);
        this.lblf4start.setTypeface(this.avenir);
        this.lblf4end.setTypeface(this.avenir);
        this.lblf4Temp.setTypeface(this.avenir);
        this.lblF1.setTypeface(this.avenir);
        this.lblF2.setTypeface(this.avenir);
        this.lblF3.setTypeface(this.avenir);
        this.lblF4.setTypeface(this.avenir);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "fonts/icomoon.ttf");
        this.lblFreccia.setTypeface(createFromAsset);
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
        this.days.setSelection(this.index);
        this.days.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                CronoSetActivity.this.index = i;
                CronoSetActivity.this.setValues();
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
        this.btnSalva.setTypeface(this.avenir);
    }

    public void setUpTxtFascia(final EditText editText, final EditText editText2, final EditText editText3, final int i) {
        editText.setTypeface(this.avenir);
        editText2.setTypeface(this.avenir);
        editText3.setTypeface(this.avenir);
        editText.setFocusable(false);
        editText.setClickable(true);
        editText.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AnonymousClass1 r6 = new Runnable() {
                    public void run() {
                        CronoSetActivity.this.s = editText.getText().toString();
                        if (CronoSetActivity.this.zona.getCrono().get(CronoSetActivity.this.index).get(i) == null) {
                            CronoSetActivity.this.zona.getCrono().get(CronoSetActivity.this.index).set(i, new Crono());
                        }
                        ((Crono) CronoSetActivity.this.zona.getCrono().get(CronoSetActivity.this.index).get(i)).setStartTime(CronoSetActivity.this.s);
                        CronoSetActivity.this.dismissdialog();
                        CronoSetActivity.this.timeStart = CronoSetActivity.this.s;
                    }
                };
                if (!editText.getText().toString().isEmpty()) {
                    CronoSetActivity cronoSetActivity = CronoSetActivity.this;
                    cronoSetActivity.bdl = cronoSetActivity.createChangeNumberPopUp("", 1, editText.getText().toString(), 10, 35, r6, editText);
                } else {
                    String str = CronoSetActivity.this.timeStart;
                    if (i != 0) {
                        try {
                            if (CronoSetActivity.this.zona.getCrono().get(CronoSetActivity.this.index).get(i - 1) != null) {
                                str = Crono.convertTimeToNormal(((Crono) CronoSetActivity.this.zona.getCrono().get(CronoSetActivity.this.index).get(i - 1)).getEndtimeAsInt15());
                            }
                        } catch (Exception unused) {
                        }
                    }
                    String str2 = str;
                    CronoSetActivity cronoSetActivity2 = CronoSetActivity.this;
                    cronoSetActivity2.bdl = cronoSetActivity2.createChangeNumberPopUp("", 1, str2, 10, 35, r6, editText);
                }
                CronoSetActivity cronoSetActivity3 = CronoSetActivity.this;
                cronoSetActivity3.openDialogFragment(cronoSetActivity3.bdl);
            }
        });
        editText2.setFocusable(false);
        editText2.setClickable(true);
        editText2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AnonymousClass1 r6 = new Runnable() {
                    public void run() {
                        CronoSetActivity.this.s = editText2.getText().toString();
                        if (CronoSetActivity.this.zona.getCrono().get(CronoSetActivity.this.index).get(i) == null) {
                            CronoSetActivity.this.zona.getCrono().get(CronoSetActivity.this.index).set(i, new Crono());
                        }
                        ((Crono) CronoSetActivity.this.zona.getCrono().get(CronoSetActivity.this.index).get(i)).setEndTime(CronoSetActivity.this.s);
                        CronoSetActivity.this.dismissdialog();
                    }
                };
                if (!editText2.getText().toString().isEmpty()) {
                    CronoSetActivity cronoSetActivity = CronoSetActivity.this;
                    cronoSetActivity.bdl = cronoSetActivity.createChangeNumberPopUp("", 1, editText2.getText().toString(), 10, 35, r6, editText2);
                } else {
                    String str = CronoSetActivity.this.timeStart;
                    try {
                        if (CronoSetActivity.this.zona.getCrono().get(CronoSetActivity.this.index).get(i) != null) {
                            str = Crono.convertTimeToNormal(((Crono) CronoSetActivity.this.zona.getCrono().get(CronoSetActivity.this.index).get(i)).getStarttimeAsInt15() + 1);
                        }
                    } catch (Exception unused) {
                    }
                    String str2 = str;
                    CronoSetActivity cronoSetActivity2 = CronoSetActivity.this;
                    cronoSetActivity2.bdl = cronoSetActivity2.createChangeNumberPopUp("", 1, str2, 10, 35, r6, editText2);
                }
                CronoSetActivity cronoSetActivity3 = CronoSetActivity.this;
                cronoSetActivity3.openDialogFragment(cronoSetActivity3.bdl);
            }
        });
        editText3.setFocusable(false);
        editText3.setClickable(true);
        editText3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AnonymousClass1 r6 = new Runnable() {
                    public void run() {
                        CronoSetActivity.this.s = editText3.getText().toString();
                        while (CronoSetActivity.this.s.contains("°")) {
                            CronoSetActivity.this.s = CronoSetActivity.this.s.replace("°", "");
                        }
                        if (CronoSetActivity.this.zona.getCrono().get(CronoSetActivity.this.index).get(i) == null) {
                            CronoSetActivity.this.zona.getCrono().get(CronoSetActivity.this.index).set(i, new Crono());
                        }
                        if (CronoSetActivity.this.unitm == 1) {
                            ((Crono) CronoSetActivity.this.zona.getCrono().get(CronoSetActivity.this.index).get(i)).setTemperatura(Functions.fromFtoC(CronoSetActivity.this.s));
                        } else {
                            ((Crono) CronoSetActivity.this.zona.getCrono().get(CronoSetActivity.this.index).get(i)).setTemperatura(CronoSetActivity.this.s);
                        }
                        CronoSetActivity.this.dismissdialog();
                    }
                };
                String str = CronoSetActivity.this.tempStartC;
                if (CronoSetActivity.this.unitm == 1) {
                    str = String.valueOf((int) Double.parseDouble(Functions.fromCtoF(str)));
                    CronoSetActivity.this.min = Constants.tempminF;
                    CronoSetActivity.this.max = Constants.tempmaxF;
                }
                if (!editText3.getText().toString().isEmpty()) {
                    str = editText3.getText().toString();
                }
                String str2 = str;
                CronoSetActivity cronoSetActivity = CronoSetActivity.this;
                cronoSetActivity.bdl = cronoSetActivity.createChangeNumberPopUp("", 0, str2, cronoSetActivity.min, CronoSetActivity.this.max, r6, editText3);
                CronoSetActivity cronoSetActivity2 = CronoSetActivity.this;
                cronoSetActivity2.openDialogFragment(cronoSetActivity2.bdl);
            }
        });
    }

    public void setValues() {
        setZoneTxtValue(this.txtf1start, this.txtf1end, this.txtf1Temp, 0);
        setZoneTxtValue(this.txtf2start, this.txtf2end, this.txtf2Temp, 1);
        setZoneTxtValue(this.txtf3start, this.txtf3end, this.txtf3Temp, 2);
        setZoneTxtValue(this.txtf4start, this.txtf4end, this.txtf4Temp, 3);
    }

    public void setZoneTxtValue(EditText editText, EditText editText2, EditText editText3, int i) {
        try {
            Zona zona2 = this.zona;
            if (zona2 == null) {
                Functions.makeErrorToast(this, getResources().getString(R.string.resCodeError));
            } else if (zona2.getCrono() == null && this.zona == null) {
                Functions.makeErrorToast(this, getResources().getString(R.string.resCodeError));
            } else if (this.zona.getCrono().get(this.index) == null && this.zona == null) {
                Functions.makeErrorToast(this, getResources().getString(R.string.resCodeError));
            } else if (this.zona.getCrono().get(this.index).get(i) != null) {
                editText.setText(((Crono) this.zona.getCrono().get(this.index).get(i)).getStartTime());
                editText2.setText(((Crono) this.zona.getCrono().get(this.index).get(i)).getEndTime());
                String temperatura = ((Crono) this.zona.getCrono().get(this.index).get(i)).getTemperatura();
                this.s = temperatura;
                if (temperatura != null) {
                    if (!temperatura.isEmpty()) {
                        while (this.s.contains("°")) {
                            this.s = this.s.replace("°", "");
                        }
                        if (this.unitm == 1) {
                            editText3.setText(String.valueOf(((int) Functions.fromCtoF(Double.parseDouble(this.s))) + "°"));
                            return;
                        }
                        editText3.setText(String.valueOf((int) Double.parseDouble(this.s)) + "°");
                        return;
                    }
                }
                editText3.setText("");
            } else {
                editText.setText("");
                editText2.setText("");
                editText3.setText("");
            }
        } catch (Exception unused) {
            editText.setText("");
            editText2.setText("");
            editText3.setText("");
        }
    }

    public void deleteF1(View view) {
        this.txtf1start.setText("");
        this.txtf1end.setText("");
        this.txtf1Temp.setText("");
        this.zona.getCrono().get(this.index).set(0, (Object) null);
    }

    public void deleteF2(View view) {
        this.txtf2start.setText("");
        this.txtf2end.setText("");
        this.txtf2Temp.setText("");
        this.zona.getCrono().get(this.index).set(1, (Object) null);
    }

    public void deleteF3(View view) {
        this.txtf3start.setText("");
        this.txtf3end.setText("");
        this.txtf3Temp.setText("");
        this.zona.getCrono().get(this.index).set(2, (Object) null);
    }

    public void deleteF4(View view) {
        this.txtf4start.setText("");
        this.txtf4end.setText("");
        this.txtf4Temp.setText("");
        this.zona.getCrono().get(this.index).set(3, (Object) null);
    }

    public void saveValue() {
        if (controllofasce()) {
            if (Constants.ISDEMO) {
                Constants.DEMO_CU.getZone().set(Constants.DEMO_INDEX_ZONA, this.zona);
                showProgress();
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Thread.sleep(500);
                        } catch (Exception unused) {
                        }
                        CronoSetActivity.this.hideProgress();
                        CronoSetActivity.this.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                Functions.makeNormalToast(CronoSetActivity.this.activity, CronoSetActivity.this.getResources().getString(R.string.cr_salvaOk));
                            }
                        });
                    }
                }).start();
                return;
            }
            try {
                final JSONObject jSONObject = new JSONObject("{}");
                if (this.cu.isOffline()) {
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                String commandToCU = MySocket.commandToCU(CronoSetActivity.this.zona.updCronoCommand(CronoSetActivity.this.cu.getPinOffline()).toString(), Constants.ip, Constants.port, true, true, true);
                                if (commandToCU != null) {
                                    JSONObject jSONObject = new JSONObject(commandToCU);
                                    if (jSONObject.has(Constants.JSON_RES) && jSONObject.getInt(Constants.JSON_RES) == 1) {
                                        CronoSetActivity.this.activity.runOnUiThread(new Runnable() {
                                            public void run() {
                                                Functions.makeNormalToast(CronoSetActivity.this.activity, CronoSetActivity.this.getResources().getString(R.string.cr_salvaOk));
                                            }
                                        });
                                    }
                                }
                            } catch (Exception unused) {
                            }
                        }
                    }).start();
                } else {
                    new Thread(new Runnable() {
                        /* JADX WARNING: Can't wrap try/catch for region: R(7:0|(3:4|5|(2:9|(2:11|(1:13)(2:14|15))))|16|17|(1:19)(1:20)|21|25) */
                        /* JADX WARNING: Code restructure failed: missing block: B:22:0x012c, code lost:
                            r11.this$0.activity.runOnUiThread(new it.tecnosystemi.TS.Activity.CronoSetActivity.AnonymousClass7.AnonymousClass2(r11));
                         */
                        /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
                            return;
                         */
                        /* JADX WARNING: Failed to process nested try/catch */
                        /* JADX WARNING: Missing exception handler attribute for start block: B:16:0x007b */
                        /* JADX WARNING: Removed duplicated region for block: B:19:0x00f0 A[Catch:{ Exception -> 0x012c }] */
                        /* JADX WARNING: Removed duplicated region for block: B:20:0x0106 A[Catch:{ Exception -> 0x012c }] */
                        /* Code decompiled incorrectly, please refer to instructions dump. */
                        public void run() {
                            /*
                                r11 = this;
                                it.tecnosystemi.TS.Activity.CronoSetActivity r0 = it.tecnosystemi.TS.Activity.CronoSetActivity.this
                                it.tecnosystemi.TS.Model.ControlUnit r0 = r0.cu
                                java.lang.String r0 = r0.getIp()
                                r1 = 1
                                r2 = -1
                                if (r0 == 0) goto L_0x007b
                                it.tecnosystemi.TS.Activity.CronoSetActivity r0 = it.tecnosystemi.TS.Activity.CronoSetActivity.this
                                it.tecnosystemi.TS.Model.ControlUnit r0 = r0.cu
                                java.lang.String r0 = r0.getIp()
                                boolean r0 = r0.isEmpty()
                                if (r0 != 0) goto L_0x007b
                                it.tecnosystemi.TS.Activity.CronoSetActivity r0 = it.tecnosystemi.TS.Activity.CronoSetActivity.this     // Catch:{ Exception -> 0x007b }
                                it.tecnosystemi.TS.Activity.BaseActivity r0 = r0.activity     // Catch:{ Exception -> 0x007b }
                                it.tecnosystemi.TS.Activity.CronoSetActivity r3 = it.tecnosystemi.TS.Activity.CronoSetActivity.this     // Catch:{ Exception -> 0x007b }
                                it.tecnosystemi.TS.Activity.BaseActivity r3 = r3.activity     // Catch:{ Exception -> 0x007b }
                                it.tecnosystemi.TS.Commands.MySocket.initInstance(r0, r3, r1)     // Catch:{ Exception -> 0x007b }
                                it.tecnosystemi.TS.Activity.CronoSetActivity r0 = it.tecnosystemi.TS.Activity.CronoSetActivity.this     // Catch:{ Exception -> 0x007b }
                                it.tecnosystemi.TS.Model.Zona r0 = r0.zona     // Catch:{ Exception -> 0x007b }
                                it.tecnosystemi.TS.Activity.CronoSetActivity r3 = it.tecnosystemi.TS.Activity.CronoSetActivity.this     // Catch:{ Exception -> 0x007b }
                                it.tecnosystemi.TS.Model.ControlUnit r3 = r3.cu     // Catch:{ Exception -> 0x007b }
                                java.lang.String r3 = r3.getPin()     // Catch:{ Exception -> 0x007b }
                                org.json.JSONObject r0 = r0.updCronoCommand(r3)     // Catch:{ Exception -> 0x007b }
                                java.lang.String r3 = r0.toString()     // Catch:{ Exception -> 0x007b }
                                it.tecnosystemi.TS.Activity.CronoSetActivity r0 = it.tecnosystemi.TS.Activity.CronoSetActivity.this     // Catch:{ Exception -> 0x007b }
                                it.tecnosystemi.TS.Model.ControlUnit r0 = r0.cu     // Catch:{ Exception -> 0x007b }
                                java.lang.String r4 = r0.getIp()     // Catch:{ Exception -> 0x007b }
                                int r5 = it.tecnosystemi.TS.Utils.Constants.port     // Catch:{ Exception -> 0x007b }
                                r7 = 1
                                r8 = 0
                                r6 = 0
                                java.lang.String r0 = it.tecnosystemi.TS.Commands.MySocket.commandToCU(r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x007b }
                                if (r0 == 0) goto L_0x007b
                                boolean r3 = r0.isEmpty()     // Catch:{ Exception -> 0x007b }
                                if (r3 != 0) goto L_0x007b
                                org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ Exception -> 0x007b }
                                r3.<init>(r0)     // Catch:{ Exception -> 0x007b }
                                java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.JSON_RES     // Catch:{ Exception -> 0x007b }
                                boolean r0 = r3.has(r0)     // Catch:{ Exception -> 0x007b }
                                if (r0 == 0) goto L_0x007b
                                java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.JSON_RES     // Catch:{ Exception -> 0x007b }
                                int r2 = r3.getInt(r0)     // Catch:{ Exception -> 0x007b }
                                java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.JSON_RES     // Catch:{ Exception -> 0x007b }
                                int r0 = r3.getInt(r0)     // Catch:{ Exception -> 0x007b }
                                if (r0 != r1) goto L_0x006e
                                goto L_0x007b
                            L_0x006e:
                                it.tecnosystemi.TS.Activity.CronoSetActivity r0 = it.tecnosystemi.TS.Activity.CronoSetActivity.this     // Catch:{ Exception -> 0x007b }
                                it.tecnosystemi.TS.Activity.BaseActivity r0 = r0.activity     // Catch:{ Exception -> 0x007b }
                                it.tecnosystemi.TS.Activity.CronoSetActivity$7$1 r3 = new it.tecnosystemi.TS.Activity.CronoSetActivity$7$1     // Catch:{ Exception -> 0x007b }
                                r3.<init>()     // Catch:{ Exception -> 0x007b }
                                r0.runOnUiThread(r3)     // Catch:{ Exception -> 0x007b }
                                return
                            L_0x007b:
                                org.json.JSONObject r0 = r0     // Catch:{ Exception -> 0x012c }
                                java.lang.String r3 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_SERIAL     // Catch:{ Exception -> 0x012c }
                                it.tecnosystemi.TS.Activity.CronoSetActivity r4 = it.tecnosystemi.TS.Activity.CronoSetActivity.this     // Catch:{ Exception -> 0x012c }
                                it.tecnosystemi.TS.Model.ControlUnit r4 = r4.cu     // Catch:{ Exception -> 0x012c }
                                java.lang.String r4 = r4.getSerial()     // Catch:{ Exception -> 0x012c }
                                r0.put(r3, r4)     // Catch:{ Exception -> 0x012c }
                                org.json.JSONObject r0 = r0     // Catch:{ Exception -> 0x012c }
                                java.lang.String r3 = it.tecnosystemi.TS.Utils.Constants.JSON_ZONE_ID     // Catch:{ Exception -> 0x012c }
                                it.tecnosystemi.TS.Activity.CronoSetActivity r4 = it.tecnosystemi.TS.Activity.CronoSetActivity.this     // Catch:{ Exception -> 0x012c }
                                it.tecnosystemi.TS.Model.Zona r4 = r4.zona     // Catch:{ Exception -> 0x012c }
                                int r4 = r4.getZoneId()     // Catch:{ Exception -> 0x012c }
                                r0.put(r3, r4)     // Catch:{ Exception -> 0x012c }
                                org.json.JSONObject r0 = r0     // Catch:{ Exception -> 0x012c }
                                java.lang.String r3 = it.tecnosystemi.TS.Utils.Constants.JSON_CU_PIN     // Catch:{ Exception -> 0x012c }
                                it.tecnosystemi.TS.Activity.CronoSetActivity r4 = it.tecnosystemi.TS.Activity.CronoSetActivity.this     // Catch:{ Exception -> 0x012c }
                                it.tecnosystemi.TS.Model.ControlUnit r4 = r4.cu     // Catch:{ Exception -> 0x012c }
                                java.lang.String r4 = r4.getPin()     // Catch:{ Exception -> 0x012c }
                                r0.put(r3, r4)     // Catch:{ Exception -> 0x012c }
                                org.json.JSONObject r0 = r0     // Catch:{ Exception -> 0x012c }
                                java.lang.String r3 = "cmd"
                                it.tecnosystemi.TS.Activity.CronoSetActivity r4 = it.tecnosystemi.TS.Activity.CronoSetActivity.this     // Catch:{ Exception -> 0x012c }
                                it.tecnosystemi.TS.Model.Zona r4 = r4.zona     // Catch:{ Exception -> 0x012c }
                                it.tecnosystemi.TS.Activity.CronoSetActivity r5 = it.tecnosystemi.TS.Activity.CronoSetActivity.this     // Catch:{ Exception -> 0x012c }
                                it.tecnosystemi.TS.Model.ControlUnit r5 = r5.cu     // Catch:{ Exception -> 0x012c }
                                java.lang.String r5 = r5.getPin()     // Catch:{ Exception -> 0x012c }
                                org.json.JSONObject r4 = r4.updCronoCommand(r5)     // Catch:{ Exception -> 0x012c }
                                java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x012c }
                                r0.put(r3, r4)     // Catch:{ Exception -> 0x012c }
                                java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x012c }
                                r0.<init>()     // Catch:{ Exception -> 0x012c }
                                it.tecnosystemi.TS.Activity.CronoSetActivity r3 = it.tecnosystemi.TS.Activity.CronoSetActivity.this     // Catch:{ Exception -> 0x012c }
                                android.content.res.Resources r3 = r3.getResources()     // Catch:{ Exception -> 0x012c }
                                int r4 = it.tecnosystemi.TS.R.string.uriWebService_POLARIS     // Catch:{ Exception -> 0x012c }
                                java.lang.String r3 = r3.getString(r4)     // Catch:{ Exception -> 0x012c }
                                r0.append(r3)     // Catch:{ Exception -> 0x012c }
                                it.tecnosystemi.TS.Activity.CronoSetActivity r3 = it.tecnosystemi.TS.Activity.CronoSetActivity.this     // Catch:{ Exception -> 0x012c }
                                android.content.res.Resources r3 = r3.getResources()     // Catch:{ Exception -> 0x012c }
                                int r4 = it.tecnosystemi.TS.R.string.uri_UpdTW     // Catch:{ Exception -> 0x012c }
                                java.lang.String r3 = r3.getString(r4)     // Catch:{ Exception -> 0x012c }
                                r0.append(r3)     // Catch:{ Exception -> 0x012c }
                                java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x012c }
                                r3 = 2
                                java.lang.String[] r10 = new java.lang.String[r3]     // Catch:{ Exception -> 0x012c }
                                r3 = 0
                                if (r2 != r1) goto L_0x0106
                                java.lang.String r2 = "0"
                                r10[r3] = r2     // Catch:{ Exception -> 0x012c }
                                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x012c }
                                r2.<init>()     // Catch:{ Exception -> 0x012c }
                                r2.append(r0)     // Catch:{ Exception -> 0x012c }
                                java.lang.String r0 = "?create_command=false"
                                r2.append(r0)     // Catch:{ Exception -> 0x012c }
                                java.lang.String r0 = r2.toString()     // Catch:{ Exception -> 0x012c }
                                goto L_0x010a
                            L_0x0106:
                                java.lang.String r2 = "1"
                                r10[r3] = r2     // Catch:{ Exception -> 0x012c }
                            L_0x010a:
                                r8 = r0
                                it.tecnosystemi.TS.Activity.CronoSetActivity r0 = it.tecnosystemi.TS.Activity.CronoSetActivity.this     // Catch:{ Exception -> 0x012c }
                                it.tecnosystemi.TS.Model.ControlUnit r0 = r0.cu     // Catch:{ Exception -> 0x012c }
                                java.lang.String r0 = r0.getFWVer()     // Catch:{ Exception -> 0x012c }
                                r10[r1] = r0     // Catch:{ Exception -> 0x012c }
                                it.tecnosystemi.TS.Threads.ThreadWebService r0 = new it.tecnosystemi.TS.Threads.ThreadWebService     // Catch:{ Exception -> 0x012c }
                                it.tecnosystemi.TS.Activity.CronoSetActivity r1 = it.tecnosystemi.TS.Activity.CronoSetActivity.this     // Catch:{ Exception -> 0x012c }
                                it.tecnosystemi.TS.Activity.BaseActivity r5 = r1.activity     // Catch:{ Exception -> 0x012c }
                                org.json.JSONObject r1 = r0     // Catch:{ Exception -> 0x012c }
                                java.lang.String r9 = r1.toString()     // Catch:{ Exception -> 0x012c }
                                r6 = 1
                                r7 = 15
                                r4 = r0
                                r4.<init>(r5, r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x012c }
                                r0.start()     // Catch:{ Exception -> 0x012c }
                                goto L_0x0138
                            L_0x012c:
                                it.tecnosystemi.TS.Activity.CronoSetActivity r0 = it.tecnosystemi.TS.Activity.CronoSetActivity.this
                                it.tecnosystemi.TS.Activity.BaseActivity r0 = r0.activity
                                it.tecnosystemi.TS.Activity.CronoSetActivity$7$2 r1 = new it.tecnosystemi.TS.Activity.CronoSetActivity$7$2
                                r1.<init>()
                                r0.runOnUiThread(r1)
                            L_0x0138:
                                return
                            */
                            throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.CronoSetActivity.AnonymousClass7.run():void");
                        }
                    }).start();
                }
            } catch (Exception unused) {
            }
        }
    }

    private boolean controllofasce() {
        for (int i = 0; i < 7; i++) {
            String str = "";
            boolean z = false;
            for (int i2 = 0; i2 < 4; i2++) {
                Crono crono = (Crono) this.zona.getCrono().get(i).get(i2);
                if (crono == null || crono.isEmpty()) {
                    z = true;
                } else if (z) {
                    Functions.makeErrorToast(this, getResources().getStringArray(R.array.cu_errorFasce)[i]);
                    return false;
                } else if (!crono.isok()) {
                    if (!crono.isTempOK()) {
                        Functions.makeErrorToast(this, getResources().getStringArray(R.array.cu_errorFasceTemp)[i]);
                        return false;
                    }
                    Functions.makeErrorToast(this, getResources().getStringArray(R.array.cu_errorFasce)[i]);
                    return false;
                } else if (str.isEmpty() || Functions.differencetime(str, crono.getStartTime()) >= 0) {
                    str = crono.getEndTime();
                } else {
                    Functions.makeErrorToast(this, getResources().getStringArray(R.array.cu_errorFasce)[i]);
                    return false;
                }
            }
        }
        return true;
    }

    public void btnSave(View view) {
        saveValue();
    }

    public View getToolBar() {
        return findViewById(R.id.csa_toolbar);
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        AnonymousClass8 r5 = new Runnable() {
            public void run() {
                CronoSetActivity.this.dismissdialog();
                CronoSetActivity cronoSetActivity = CronoSetActivity.this;
                cronoSetActivity.indexZonetoCopy = cronoSetActivity.index;
            }
        };
        list.add(createMenuItem(true, getResources().getString(R.string.cr_menuCopia), "", "", r5, false, false));
        AnonymousClass9 r6 = new Runnable() {
            public void run() {
                CronoSetActivity.this.dismissdialog();
                if (CronoSetActivity.this.indexZonetoCopy == -1) {
                    Functions.makeNormalToast(CronoSetActivity.this.activity, CronoSetActivity.this.getResources().getString(R.string.cr_erroreIncolla));
                    return;
                }
                CronoSetActivity.this.zona.getCrono().set(CronoSetActivity.this.index, CronoSetActivity.this.zona.getCrono().get(CronoSetActivity.this.indexZonetoCopy));
                CronoSetActivity.this.setValues();
            }
        };
        list.add(createMenuItem(false, getResources().getString(R.string.cr_menuIncolla), "", "", r6, false, false));
        return list;
    }

    public String setToolbarTitle() {
        return this.zona.getName();
    }
}
