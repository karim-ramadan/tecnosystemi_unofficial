package it.tecnosystemi.TS.Activity.PICO;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Adapters.SpinnerAdapter;
import it.tecnosystemi.TS.Commands.CmdPICO;
import it.tecnosystemi.TS.Commands.UDPSocket;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PicoBootloaderActivity extends BaseActivity {
    public static Class<?> CLASSTOCALL = null;
    static boolean versioneDUBUG = false;
    PicoBootloaderActivity activity;
    Button btnAggiorna;
    boolean contine_getInfo = true;
    File file;
    Uri fileUri;
    boolean fromAct;
    JSONArray infofwToShow;
    JSONArray infofwsAll;
    String ipSlave;
    boolean isSlave;
    TextView lblChangeLog;
    List<String> listFwVer;
    String old_fwver;
    String old_ser;
    ProgressBar pb_file;
    SharedPreferences preferences;
    String respInfo;
    String selectedFW;
    Spinner spn_fw;
    Thread threadinfo;

    public BaseActivity getActivity() {
        return this;
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        return list;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_pico_bootloader);
        this.activity = this;
        super.onCreate(bundle);
        if (versioneDUBUG && ActivityCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") != 0) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        }
        this.fromAct = getIntent().getBooleanExtra("FROMPICOACT", false);
        this.isSlave = getIntent().getBooleanExtra("ISSLAVE", false);
        this.ipSlave = getIntent().getStringExtra("IPSLAVE");
        this.pb_file = (ProgressBar) findViewById(R.id.pb_file);
        this.spn_fw = (Spinner) findViewById(R.id.ba_spn_fw);
        this.lblChangeLog = (TextView) findViewById(R.id.ba_lblChangeLog);
        this.btnAggiorna = (Button) findViewById(R.id.ba_btnAggiorna);
        Functions.setFontsWithIcon(findViewById(R.id.ly_picoboot), this);
        this.fileUri = null;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
        hideMenuButton();
        getFWs();
        setUpSPinner();
        ((TextView) findViewById(R.id.ba_lblSelectFW3)).setText(getResources().getString(R.string.c2_1_pico_InfoFWVer) + ": ---");
        getInfo();
    }

    public void getInfo() {
        if (!Constants.ISDEMO) {
            this.contine_getInfo = true;
            showProgress();
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        CmdPICO cmdPICO = new CmdPICO();
                        cmdPICO.setCmd("pico_info");
                        if (PicoBootloaderActivity.this.isSlave) {
                            UDPSocket.startListening(true, true, PicoBootloaderActivity.this.ipSlave);
                        } else {
                            UDPSocket.startListening();
                        }
                        PicoBootloaderActivity.this.respInfo = UDPSocket.sendCMD(cmdPICO, 15000, 15000, false, 1);
                        int i = 0;
                        while (PicoBootloaderActivity.this.respInfo == null && i < 5 && PicoBootloaderActivity.this.contine_getInfo) {
                            int i2 = i + 1;
                            if (PicoBootloaderActivity.this.isSlave) {
                                UDPSocket.startListening(true, true, PicoBootloaderActivity.this.ipSlave);
                            } else {
                                UDPSocket.startListening();
                            }
                            PicoBootloaderActivity.this.respInfo = UDPSocket.sendCMD(cmdPICO, 15000, 15000, false, 1);
                            if (PicoBootloaderActivity.this.respInfo == null) {
                                Thread.sleep(2000);
                            }
                            i = i2;
                        }
                        if (PicoBootloaderActivity.this.contine_getInfo) {
                            PicoBootloaderActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    try {
                                        JSONObject jSONObject = new JSONObject(PicoBootloaderActivity.this.respInfo);
                                        StringBuilder sb = new StringBuilder();
                                        sb.append(PicoBootloaderActivity.this.getResources().getString(R.string.c2_1_pico_InfoFWVer));
                                        sb.append(": ");
                                        PicoBootloaderActivity.this.old_fwver = jSONObject.getString("fw_ver");
                                        PicoBootloaderActivity.this.old_ser = jSONObject.getString("ser");
                                        sb.append(PicoBootloaderActivity.this.old_fwver);
                                        ((TextView) PicoBootloaderActivity.this.findViewById(R.id.ba_lblSelectFW3)).setText(sb.toString());
                                    } catch (Exception unused) {
                                    }
                                }
                            });
                        }
                    } catch (Exception unused) {
                    }
                    PicoBootloaderActivity.this.hideProgress();
                }
            });
            this.threadinfo = thread;
            thread.start();
        }
    }

    public void setUpSPinner() {
        this.listFwVer = new ArrayList();
        for (int i = 0; i < this.infofwsAll.length(); i++) {
            try {
                if (this.infofwsAll.getJSONObject(i).has(Constants.JSON_LVDV_Type) && this.infofwsAll.getJSONObject(i).getInt(Constants.JSON_LVDV_Type) == 1) {
                    this.listFwVer.add(this.infofwsAll.getJSONObject(i).getString(Constants.JSON_VERSION));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        this.spn_fw.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                try {
                    String string = PicoBootloaderActivity.this.infofwToShow.getJSONObject(i).getString("ChangeLogIt");
                    String string2 = PicoBootloaderActivity.this.infofwToShow.getJSONObject(i).getString("ChangeLogEn");
                    if (string2.toUpperCase().equals("NULL") && string.toUpperCase().equals("NULL")) {
                        PicoBootloaderActivity.this.lblChangeLog.setText("");
                    } else if (string2.toUpperCase().equals("NULL")) {
                        PicoBootloaderActivity.this.lblChangeLog.setText(string);
                    } else if (string.toUpperCase().equals("NULL")) {
                        PicoBootloaderActivity.this.lblChangeLog.setText(string2);
                    } else if (Locale.getDefault().getLanguage().toLowerCase().equals("it")) {
                        PicoBootloaderActivity.this.lblChangeLog.setText(string);
                    } else {
                        PicoBootloaderActivity.this.lblChangeLog.setText(string2);
                    }
                } catch (Exception unused) {
                }
            }
        });
        this.spn_fw.setAdapter(new SpinnerAdapter(this, R.layout.spinner_item, this.listFwVer, false));
    }

    /* access modifiers changed from: private */
    public void setBarProgress(final int i, final int i2) {
        runOnUiThread(new Runnable() {
            public void run() {
                PicoBootloaderActivity.this.pb_file.setMax(i2);
                PicoBootloaderActivity.this.pb_file.setProgress(i);
            }
        });
    }

    public void btnChooseFIle(View view) {
        this.fileUri = null;
        shooseFile();
    }

    private void shooseFile() {
        if (ActivityCompat.checkSelfPermission(this.activity, "android.permission.READ_EXTERNAL_STORAGE") == 0) {
            startActivityForResult(Intent.createChooser(new Intent().setType("*/*").setAction("android.intent.action.GET_CONTENT"), "Select a file"), 123);
        } else {
            ActivityCompat.requestPermissions(this.activity, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        }
    }

    public void disableBtnAggiorna() {
        runOnUiThread(new Runnable() {
            public void run() {
                PicoBootloaderActivity.this.btnAggiorna.setEnabled(false);
            }
        });
    }

    public void enableBtnAggiorna() {
        runOnUiThread(new Runnable() {
            public void run() {
                PicoBootloaderActivity.this.btnAggiorna.setEnabled(true);
            }
        });
    }

    public void btnAggiorna(View view) {
        try {
            if (!versioneDUBUG) {
                String string = this.infofwToShow.getJSONObject(this.spn_fw.getSelectedItemPosition()).getString("Path");
                this.selectedFW = this.listFwVer.get(this.spn_fw.getSelectedItemPosition());
                this.file = new File(this.activity.getBaseContext().getFileStreamPath(Constants.FW_DIRECTORY_NAME), string);
            } else if (this.fileUri == null) {
                return;
            }
        } catch (Exception unused) {
        }
        showProgress();
        new Thread(new Runnable() {
            /* JADX WARNING: Can't wrap try/catch for region: R(9:45|46|47|48|49|50|(2:58|(2:63|75)(1:62))|64|74) */
            /* JADX WARNING: Missing exception handler attribute for start block: B:49:0x019c */
            /* JADX WARNING: Removed duplicated region for block: B:62:0x01d0 A[Catch:{ Exception -> 0x0208 }] */
            /* JADX WARNING: Removed duplicated region for block: B:63:0x01d1 A[Catch:{ Exception -> 0x0208 }] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r14 = this;
                    java.lang.String r0 = "1.0.3"
                    java.lang.String r1 = "res"
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r2 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    r2.disableBtnAggiorna()     // Catch:{ Exception -> 0x0208 }
                    boolean r2 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.versioneDUBUG     // Catch:{ Exception -> 0x0208 }
                    if (r2 == 0) goto L_0x001c
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r2 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    android.content.ContentResolver r2 = r2.getContentResolver()     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r3 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    android.net.Uri r3 = r3.fileUri     // Catch:{ Exception -> 0x0208 }
                    java.io.InputStream r2 = r2.openInputStream(r3)     // Catch:{ Exception -> 0x0208 }
                    goto L_0x0025
                L_0x001c:
                    java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r3 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    java.io.File r3 = r3.file     // Catch:{ Exception -> 0x0208 }
                    r2.<init>(r3)     // Catch:{ Exception -> 0x0208 }
                L_0x0025:
                    int r3 = r2.available()     // Catch:{ Exception -> 0x0208 }
                    byte[] r4 = new byte[r3]     // Catch:{ Exception -> 0x0208 }
                    r2.read(r4)     // Catch:{ Exception -> 0x0208 }
                    r2.close()     // Catch:{ Exception -> 0x0208 }
                    java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0208 }
                    r2.<init>()     // Catch:{ Exception -> 0x0208 }
                    java.lang.String r5 = "SHA-256"
                    java.security.MessageDigest r5 = java.security.MessageDigest.getInstance(r5)     // Catch:{ Exception -> 0x0208 }
                    r6 = 23
                    byte r6 = r4[r6]     // Catch:{ Exception -> 0x0208 }
                    r7 = 0
                    r8 = 1
                    if (r6 != r8) goto L_0x004e
                    int r6 = r3 + -32
                    byte[] r6 = java.util.Arrays.copyOfRange(r4, r7, r6)     // Catch:{ Exception -> 0x0208 }
                    r5.update(r6)     // Catch:{ Exception -> 0x0208 }
                    goto L_0x0051
                L_0x004e:
                    r5.update(r4)     // Catch:{ Exception -> 0x0208 }
                L_0x0051:
                    byte[] r5 = r5.digest()     // Catch:{ Exception -> 0x0208 }
                    int r6 = r5.length     // Catch:{ Exception -> 0x0208 }
                    r9 = 0
                L_0x0057:
                    if (r9 >= r6) goto L_0x006f
                    byte r10 = r5[r9]     // Catch:{ Exception -> 0x0208 }
                    java.lang.String r11 = "%02x"
                    java.lang.Byte r10 = java.lang.Byte.valueOf(r10)     // Catch:{ Exception -> 0x0208 }
                    java.lang.Object[] r12 = new java.lang.Object[r8]     // Catch:{ Exception -> 0x0208 }
                    r12[r7] = r10     // Catch:{ Exception -> 0x0208 }
                    java.lang.String r10 = java.lang.String.format(r11, r12)     // Catch:{ Exception -> 0x0208 }
                    r2.append(r10)     // Catch:{ Exception -> 0x0208 }
                    int r9 = r9 + 1
                    goto L_0x0057
                L_0x006f:
                    it.tecnosystemi.TS.Commands.CmdPICO$OTA_Start r5 = new it.tecnosystemi.TS.Commands.CmdPICO$OTA_Start     // Catch:{ Exception -> 0x0208 }
                    r5.<init>()     // Catch:{ Exception -> 0x0208 }
                    java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0208 }
                    r5.setSha(r2)     // Catch:{ Exception -> 0x0208 }
                    r5.setSize(r3)     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r2 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    boolean r2 = r2.isSlave     // Catch:{ Exception -> 0x0208 }
                    if (r2 == 0) goto L_0x008c
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r2 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    java.lang.String r2 = r2.ipSlave     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Commands.UDPSocket.startListening(r8, r8, r2)     // Catch:{ Exception -> 0x0208 }
                    goto L_0x008f
                L_0x008c:
                    it.tecnosystemi.TS.Commands.UDPSocket.startListening()     // Catch:{ Exception -> 0x0208 }
                L_0x008f:
                    r9 = 15000(0x3a98, double:7.411E-320)
                    java.lang.String r2 = it.tecnosystemi.TS.Commands.UDPSocket.sendCMD(r5, r9, r9)     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r5 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    r5.hideProgress()     // Catch:{ Exception -> 0x0208 }
                    if (r2 == 0) goto L_0x0202
                    org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ Exception -> 0x0208 }
                    r5.<init>(r2)     // Catch:{ Exception -> 0x0208 }
                    boolean r2 = r5.has(r1)     // Catch:{ Exception -> 0x0208 }
                    if (r2 == 0) goto L_0x0202
                    int r2 = r5.getInt(r1)     // Catch:{ Exception -> 0x0208 }
                    if (r2 != r8) goto L_0x0202
                    r5 = 500(0x1f4, double:2.47E-321)
                    java.lang.Thread.sleep(r5)     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r2 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    java.lang.String r2 = r2.ipSlave     // Catch:{ Exception -> 0x0208 }
                    boolean r2 = it.tecnosystemi.TS.Commands.PicoSocketBootloader.connectToSocket(r2)     // Catch:{ Exception -> 0x0208 }
                    if (r2 != 0) goto L_0x00c2
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r0 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    r0.enableBtnAggiorna()     // Catch:{ Exception -> 0x0208 }
                    return
                L_0x00c2:
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r2 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    r2.setBarProgress(r7, r3)     // Catch:{ Exception -> 0x0208 }
                    java.util.Date r2 = new java.util.Date     // Catch:{ Exception -> 0x0208 }
                    r2.<init>()     // Catch:{ Exception -> 0x0208 }
                L_0x00cc:
                    java.lang.String r11 = "PICOBOOT"
                    if (r7 >= r3) goto L_0x0104
                    java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0208 }
                    r12.<init>()     // Catch:{ Exception -> 0x0208 }
                    java.lang.String r13 = "curr: "
                    r12.append(r13)     // Catch:{ Exception -> 0x0208 }
                    r12.append(r7)     // Catch:{ Exception -> 0x0208 }
                    java.lang.String r13 = " total: "
                    r12.append(r13)     // Catch:{ Exception -> 0x0208 }
                    r12.append(r3)     // Catch:{ Exception -> 0x0208 }
                    java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x0208 }
                    android.util.Log.d(r11, r12)     // Catch:{ Exception -> 0x0208 }
                    int r11 = r7 + 1300
                    if (r11 <= r3) goto L_0x00f1
                    r11 = r3
                L_0x00f1:
                    r12 = 20
                    java.lang.Thread.sleep(r12)     // Catch:{ Exception -> 0x0208 }
                    byte[] r7 = java.util.Arrays.copyOfRange(r4, r7, r11)     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Commands.PicoSocketBootloader.sendfile(r7)     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r7 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    r7.setBarProgress(r11, r3)     // Catch:{ Exception -> 0x0208 }
                    r7 = r11
                    goto L_0x00cc
                L_0x0104:
                    it.tecnosystemi.TS.Commands.PicoSocketBootloader.closeSocket()     // Catch:{ Exception -> 0x0208 }
                    java.util.Date r3 = new java.util.Date     // Catch:{ Exception -> 0x0208 }
                    r3.<init>()     // Catch:{ Exception -> 0x0208 }
                    long r3 = r3.getTime()     // Catch:{ Exception -> 0x0208 }
                    long r12 = r2.getTime()     // Catch:{ Exception -> 0x0208 }
                    long r3 = r3 - r12
                    java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0208 }
                    r2.<init>()     // Catch:{ Exception -> 0x0208 }
                    java.lang.String r7 = "durata: "
                    r2.append(r7)     // Catch:{ Exception -> 0x0208 }
                    r12 = 1000(0x3e8, double:4.94E-321)
                    long r3 = r3 / r12
                    r2.append(r3)     // Catch:{ Exception -> 0x0208 }
                    java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0208 }
                    android.util.Log.d(r11, r2)     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r2 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    r2.showProgress()     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Commands.UDPSocket.stopListening()     // Catch:{ Exception -> 0x0208 }
                    java.lang.Thread.sleep(r5)     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r2 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    boolean r2 = r2.isSlave     // Catch:{ Exception -> 0x0208 }
                    if (r2 == 0) goto L_0x0145
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r2 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    java.lang.String r2 = r2.ipSlave     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Commands.UDPSocket.startListening(r8, r8, r2)     // Catch:{ Exception -> 0x0208 }
                    goto L_0x0148
                L_0x0145:
                    it.tecnosystemi.TS.Commands.UDPSocket.startListening()     // Catch:{ Exception -> 0x0208 }
                L_0x0148:
                    it.tecnosystemi.TS.Commands.CmdPICO r2 = new it.tecnosystemi.TS.Commands.CmdPICO     // Catch:{ Exception -> 0x0208 }
                    r2.<init>()     // Catch:{ Exception -> 0x0208 }
                    java.lang.String r3 = "ota_end"
                    r2.setCmd(r3)     // Catch:{ Exception -> 0x0208 }
                    java.lang.String r2 = it.tecnosystemi.TS.Commands.UDPSocket.sendCMD(r2, r9, r9)     // Catch:{ Exception -> 0x0208 }
                    if (r2 == 0) goto L_0x0208
                    org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ Exception -> 0x0208 }
                    r3.<init>(r2)     // Catch:{ Exception -> 0x0208 }
                    boolean r2 = r3.has(r1)     // Catch:{ Exception -> 0x0208 }
                    if (r2 == 0) goto L_0x0208
                    int r1 = r3.getInt(r1)     // Catch:{ Exception -> 0x0208 }
                    if (r1 != r8) goto L_0x0208
                    it.tecnosystemi.TS.Commands.CmdPICO$PicoAP r2 = new it.tecnosystemi.TS.Commands.CmdPICO$PicoAP     // Catch:{ Exception -> 0x0208 }
                    r2.<init>()     // Catch:{ Exception -> 0x0208 }
                    r1 = 5
                    r2.setAp_m(r1)     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Activity.BaseActivity.bootloadResetWiFi = r8     // Catch:{ Exception -> 0x0208 }
                    r5 = 10000(0x2710, double:4.9407E-320)
                    r7 = 1
                    r3 = 10000(0x2710, double:4.9407E-320)
                    it.tecnosystemi.TS.Commands.UDPSocket.sendCMD(r2, r3, r5, r7)     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r1 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    r1.hideProgress()     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Commands.UDPSocket.stopListening()     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r1 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r1 = r1.activity     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r2 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    android.content.res.Resources r2 = r2.getResources()     // Catch:{ Exception -> 0x0208 }
                    int r3 = it.tecnosystemi.TS.R.string.ba_aggiornamento_ok     // Catch:{ Exception -> 0x0208 }
                    java.lang.String r2 = r2.getString(r3)     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Utils.Functions.makeNormalToast(r1, r2)     // Catch:{ Exception -> 0x0208 }
                    r1 = 2000(0x7d0, double:9.88E-321)
                    java.lang.Thread.sleep(r1)     // Catch:{ Exception -> 0x019c }
                L_0x019c:
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r1 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    boolean r1 = r1.isSlave     // Catch:{ Exception -> 0x0208 }
                    if (r1 != 0) goto L_0x01f2
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r1 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    java.lang.String r1 = r1.old_fwver     // Catch:{ Exception -> 0x0208 }
                    if (r1 == 0) goto L_0x01f2
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r1 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    java.lang.String r1 = r1.old_fwver     // Catch:{ Exception -> 0x0208 }
                    int r1 = it.tecnosystemi.TS.Utils.Functions.compareVersions(r1, r0)     // Catch:{ Exception -> 0x0208 }
                    if (r1 <= 0) goto L_0x01bc
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r1 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    java.lang.String r1 = r1.selectedFW     // Catch:{ Exception -> 0x0208 }
                    int r1 = it.tecnosystemi.TS.Utils.Functions.compareVersions(r1, r0)     // Catch:{ Exception -> 0x0208 }
                    if (r1 > 0) goto L_0x01f2
                L_0x01bc:
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r1 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    java.lang.String r1 = r1.old_fwver     // Catch:{ Exception -> 0x0208 }
                    int r1 = it.tecnosystemi.TS.Utils.Functions.compareVersions(r1, r0)     // Catch:{ Exception -> 0x0208 }
                    if (r1 > 0) goto L_0x01d1
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r1 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    java.lang.String r1 = r1.selectedFW     // Catch:{ Exception -> 0x0208 }
                    int r0 = it.tecnosystemi.TS.Utils.Functions.compareVersions(r1, r0)     // Catch:{ Exception -> 0x0208 }
                    if (r0 > 0) goto L_0x01d1
                    goto L_0x01f2
                L_0x01d1:
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r0 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    java.lang.String r0 = r0.old_ser     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r1 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r1 = r1.activity     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Model.Pico.deletePICOfromPref(r0, r1)     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r0 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    java.lang.String r0 = r0.old_ser     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r1 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r1 = r1.activity     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Model.Device.deleteDevPICOFromPef(r0, r1)     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r0 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity$6$2 r1 = new it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity$6$2     // Catch:{ Exception -> 0x0208 }
                    r1.<init>()     // Catch:{ Exception -> 0x0208 }
                    r0.runOnUiThread(r1)     // Catch:{ Exception -> 0x0208 }
                    goto L_0x0201
                L_0x01f2:
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r0 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity$6$1 r1 = new it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity$6$1     // Catch:{ Exception -> 0x0208 }
                    r1.<init>()     // Catch:{ Exception -> 0x0208 }
                    r0.runOnUiThread(r1)     // Catch:{ Exception -> 0x0208 }
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r0 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    r0.enableBtnAggiorna()     // Catch:{ Exception -> 0x0208 }
                L_0x0201:
                    return
                L_0x0202:
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r0 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this     // Catch:{ Exception -> 0x0208 }
                    r0.enableBtnAggiorna()     // Catch:{ Exception -> 0x0208 }
                    return
                L_0x0208:
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r0 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this
                    r0.enableBtnAggiorna()
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r0 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r0 = r0.activity
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r1 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this
                    android.content.res.Resources r1 = r1.getResources()
                    int r2 = it.tecnosystemi.TS.R.string.ba_aggiornamento_ko
                    java.lang.String r1 = r1.getString(r2)
                    it.tecnosystemi.TS.Utils.Functions.makeErrorToast(r0, r1)
                    it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity r0 = it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.this
                    r0.hideProgress()
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity.AnonymousClass6.run():void");
            }
        }).start();
    }

    public void onDestroy() {
        super.onDestroy();
        this.contine_getInfo = false;
        try {
            this.threadinfo.interrupt();
        } catch (Exception unused) {
        }
        if (!this.fromAct) {
            disconnectFromWIfi();
        } else if (!PicoActivity.pico.getOffline().booleanValue()) {
            disconnectFromWIfi();
        } else {
            UDPSocket.startListening(true);
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 123 && i2 == -1) {
            this.fileUri = intent.getData();
        }
    }

    private void getFWs() {
        try {
            this.infofwsAll = new JSONArray(this.preferences.getString(Constants.PREF_INFOFWS, "[]"));
            this.infofwToShow = new JSONArray();
            for (int i = 0; i < this.infofwsAll.length(); i++) {
                if (this.infofwsAll.getJSONObject(i).getInt(Constants.JSON_LVDV_Type) == 1) {
                    this.infofwToShow.put(this.infofwsAll.getJSONObject(i));
                }
            }
        } catch (Exception unused) {
        }
    }

    public View getToolBar() {
        return findViewById(R.id.bl_toolbar);
    }

    public String setToolbarTitle() {
        return "Bootloader";
    }
}
