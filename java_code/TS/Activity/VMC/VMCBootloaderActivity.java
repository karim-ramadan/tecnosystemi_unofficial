package it.tecnosystemi.TS.Activity.VMC;

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

public class VMCBootloaderActivity extends BaseActivity {
    public static Class<?> CLASSTOCALL = null;
    static boolean versioneDUBUG = false;
    VMCBootloaderActivity activity;
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
        setContentView(R.layout.activity_vmcbootloader);
        this.typeActStyle = 3;
        this.activity = this;
        super.onCreate(bundle);
        if (versioneDUBUG && ActivityCompat.checkSelfPermission(this, "android.permission.READ_EXTERNAL_STORAGE") != 0) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
        }
        this.fromAct = getIntent().getBooleanExtra("FROMPICOACT", false);
        this.isSlave = false;
        this.pb_file = (ProgressBar) findViewById(R.id.pb_file);
        this.spn_fw = (Spinner) findViewById(R.id.ba_spn_fw);
        this.lblChangeLog = (TextView) findViewById(R.id.ba_lblChangeLog);
        this.btnAggiorna = (Button) findViewById(R.id.ba_btnAggiorna);
        Functions.setFontsWithIcon(findViewById(R.id.main), this);
        this.fileUri = null;
        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
        hideMenuButton();
        getFWs();
        setUpSPinner();
        ((TextView) findViewById(R.id.ba_lblSelectFW3)).setText(getResources().getString(R.string.c2_1_pico_InfoFWVer) + ": ---");
        getInfo();
        Functions.setFontsWithIcon(findViewById(R.id.main), this);
    }

    public void getInfo() {
        if (!Constants.ISDEMO) {
            this.contine_getInfo = true;
            showProgress();
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        CmdPICO cmdPICO = new CmdPICO();
                        cmdPICO.setCmd("vmc_info");
                        if (VMCBootloaderActivity.this.isSlave) {
                            UDPSocket.startListening(true, true, VMCBootloaderActivity.this.ipSlave);
                        } else {
                            UDPSocket.startListening();
                        }
                        VMCBootloaderActivity.this.respInfo = UDPSocket.sendCMD(cmdPICO, 15000, 15000, false, 1);
                        int i = 0;
                        while (VMCBootloaderActivity.this.respInfo == null && i < 5 && VMCBootloaderActivity.this.contine_getInfo) {
                            int i2 = i + 1;
                            if (VMCBootloaderActivity.this.isSlave) {
                                UDPSocket.startListening(true, true, VMCBootloaderActivity.this.ipSlave);
                            } else {
                                UDPSocket.startListening();
                            }
                            VMCBootloaderActivity.this.respInfo = UDPSocket.sendCMD(cmdPICO, 15000, 15000, false, 1);
                            if (VMCBootloaderActivity.this.respInfo == null) {
                                Thread.sleep(2000);
                            }
                            i = i2;
                        }
                        if (VMCBootloaderActivity.this.contine_getInfo) {
                            VMCBootloaderActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    try {
                                        JSONObject jSONObject = new JSONObject(VMCBootloaderActivity.this.respInfo);
                                        StringBuilder sb = new StringBuilder();
                                        sb.append(VMCBootloaderActivity.this.getResources().getString(R.string.c2_1_pico_InfoFWVer));
                                        sb.append(": ");
                                        VMCBootloaderActivity.this.old_fwver = jSONObject.getString("fw_ver");
                                        VMCBootloaderActivity.this.old_ser = jSONObject.getString("ser");
                                        sb.append(VMCBootloaderActivity.this.old_fwver);
                                        ((TextView) VMCBootloaderActivity.this.findViewById(R.id.ba_lblSelectFW3)).setText(sb.toString());
                                    } catch (Exception unused) {
                                    }
                                }
                            });
                        }
                    } catch (Exception unused) {
                    }
                    VMCBootloaderActivity.this.hideProgress();
                }
            });
            this.threadinfo = thread;
            thread.start();
        }
    }

    private void getFWs() {
        try {
            this.infofwsAll = new JSONArray(this.preferences.getString(Constants.PREF_INFOFWS, "[]"));
            this.infofwToShow = new JSONArray();
            for (int i = 0; i < this.infofwsAll.length(); i++) {
                if (this.infofwsAll.getJSONObject(i).getInt(Constants.JSON_LVDV_Type) == 2) {
                    this.infofwToShow.put(this.infofwsAll.getJSONObject(i));
                }
            }
        } catch (Exception unused) {
        }
    }

    public void setUpSPinner() {
        this.listFwVer = new ArrayList();
        for (int i = 0; i < this.infofwsAll.length(); i++) {
            try {
                if (this.infofwsAll.getJSONObject(i).has(Constants.JSON_LVDV_Type) && this.infofwsAll.getJSONObject(i).getInt(Constants.JSON_LVDV_Type) == 2) {
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
                    String string = VMCBootloaderActivity.this.infofwToShow.getJSONObject(i).getString("ChangeLogIt");
                    String string2 = VMCBootloaderActivity.this.infofwToShow.getJSONObject(i).getString("ChangeLogEn");
                    if (string2.toUpperCase().equals("NULL") && string.toUpperCase().equals("NULL")) {
                        VMCBootloaderActivity.this.lblChangeLog.setText("");
                    } else if (string2.toUpperCase().equals("NULL")) {
                        VMCBootloaderActivity.this.lblChangeLog.setText(string);
                    } else if (string.toUpperCase().equals("NULL")) {
                        VMCBootloaderActivity.this.lblChangeLog.setText(string2);
                    } else if (Locale.getDefault().getLanguage().toLowerCase().equals("it")) {
                        VMCBootloaderActivity.this.lblChangeLog.setText(string);
                    } else {
                        VMCBootloaderActivity.this.lblChangeLog.setText(string2);
                    }
                } catch (Exception unused) {
                }
            }
        });
        this.spn_fw.setAdapter(new SpinnerAdapter(this, R.layout.spinner_item, this.listFwVer, false));
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
            /* JADX WARNING: Can't wrap try/catch for region: R(7:45|46|47|48|49|50|51) */
            /* JADX WARNING: Missing exception handler attribute for start block: B:49:0x019a */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r13 = this;
                    java.lang.String r0 = "res"
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity r1 = it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity.this     // Catch:{ Exception -> 0x01b0 }
                    r1.disableBtnAggiorna()     // Catch:{ Exception -> 0x01b0 }
                    boolean r1 = it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity.versioneDUBUG     // Catch:{ Exception -> 0x01b0 }
                    if (r1 == 0) goto L_0x001a
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity r1 = it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity.this     // Catch:{ Exception -> 0x01b0 }
                    android.content.ContentResolver r1 = r1.getContentResolver()     // Catch:{ Exception -> 0x01b0 }
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity r2 = it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity.this     // Catch:{ Exception -> 0x01b0 }
                    android.net.Uri r2 = r2.fileUri     // Catch:{ Exception -> 0x01b0 }
                    java.io.InputStream r1 = r1.openInputStream(r2)     // Catch:{ Exception -> 0x01b0 }
                    goto L_0x0023
                L_0x001a:
                    java.io.FileInputStream r1 = new java.io.FileInputStream     // Catch:{ Exception -> 0x01b0 }
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity r2 = it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity.this     // Catch:{ Exception -> 0x01b0 }
                    java.io.File r2 = r2.file     // Catch:{ Exception -> 0x01b0 }
                    r1.<init>(r2)     // Catch:{ Exception -> 0x01b0 }
                L_0x0023:
                    int r2 = r1.available()     // Catch:{ Exception -> 0x01b0 }
                    byte[] r3 = new byte[r2]     // Catch:{ Exception -> 0x01b0 }
                    r1.read(r3)     // Catch:{ Exception -> 0x01b0 }
                    r1.close()     // Catch:{ Exception -> 0x01b0 }
                    java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01b0 }
                    r1.<init>()     // Catch:{ Exception -> 0x01b0 }
                    java.lang.String r4 = "SHA-256"
                    java.security.MessageDigest r4 = java.security.MessageDigest.getInstance(r4)     // Catch:{ Exception -> 0x01b0 }
                    r5 = 23
                    byte r5 = r3[r5]     // Catch:{ Exception -> 0x01b0 }
                    r6 = 0
                    r7 = 1
                    if (r5 != r7) goto L_0x004c
                    int r5 = r2 + -32
                    byte[] r5 = java.util.Arrays.copyOfRange(r3, r6, r5)     // Catch:{ Exception -> 0x01b0 }
                    r4.update(r5)     // Catch:{ Exception -> 0x01b0 }
                    goto L_0x004f
                L_0x004c:
                    r4.update(r3)     // Catch:{ Exception -> 0x01b0 }
                L_0x004f:
                    byte[] r4 = r4.digest()     // Catch:{ Exception -> 0x01b0 }
                    int r5 = r4.length     // Catch:{ Exception -> 0x01b0 }
                    r8 = 0
                L_0x0055:
                    if (r8 >= r5) goto L_0x006d
                    byte r9 = r4[r8]     // Catch:{ Exception -> 0x01b0 }
                    java.lang.String r10 = "%02x"
                    java.lang.Byte r9 = java.lang.Byte.valueOf(r9)     // Catch:{ Exception -> 0x01b0 }
                    java.lang.Object[] r11 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x01b0 }
                    r11[r6] = r9     // Catch:{ Exception -> 0x01b0 }
                    java.lang.String r9 = java.lang.String.format(r10, r11)     // Catch:{ Exception -> 0x01b0 }
                    r1.append(r9)     // Catch:{ Exception -> 0x01b0 }
                    int r8 = r8 + 1
                    goto L_0x0055
                L_0x006d:
                    it.tecnosystemi.TS.Commands.CmdPICO$OTA_Start r4 = new it.tecnosystemi.TS.Commands.CmdPICO$OTA_Start     // Catch:{ Exception -> 0x01b0 }
                    r4.<init>()     // Catch:{ Exception -> 0x01b0 }
                    java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x01b0 }
                    r4.setSha(r1)     // Catch:{ Exception -> 0x01b0 }
                    r4.setSize(r2)     // Catch:{ Exception -> 0x01b0 }
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity r1 = it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity.this     // Catch:{ Exception -> 0x01b0 }
                    boolean r1 = r1.isSlave     // Catch:{ Exception -> 0x01b0 }
                    if (r1 == 0) goto L_0x008a
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity r1 = it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity.this     // Catch:{ Exception -> 0x01b0 }
                    java.lang.String r1 = r1.ipSlave     // Catch:{ Exception -> 0x01b0 }
                    it.tecnosystemi.TS.Commands.UDPSocket.startListening(r7, r7, r1)     // Catch:{ Exception -> 0x01b0 }
                    goto L_0x008d
                L_0x008a:
                    it.tecnosystemi.TS.Commands.UDPSocket.startListening()     // Catch:{ Exception -> 0x01b0 }
                L_0x008d:
                    r8 = 15000(0x3a98, double:7.411E-320)
                    java.lang.String r1 = it.tecnosystemi.TS.Commands.UDPSocket.sendCMD(r4, r8, r8)     // Catch:{ Exception -> 0x01b0 }
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity r4 = it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity.this     // Catch:{ Exception -> 0x01b0 }
                    r4.hideProgress()     // Catch:{ Exception -> 0x01b0 }
                    if (r1 == 0) goto L_0x01aa
                    org.json.JSONObject r4 = new org.json.JSONObject     // Catch:{ Exception -> 0x01b0 }
                    r4.<init>(r1)     // Catch:{ Exception -> 0x01b0 }
                    boolean r1 = r4.has(r0)     // Catch:{ Exception -> 0x01b0 }
                    if (r1 == 0) goto L_0x01aa
                    int r1 = r4.getInt(r0)     // Catch:{ Exception -> 0x01b0 }
                    if (r1 != r7) goto L_0x01aa
                    r4 = 500(0x1f4, double:2.47E-321)
                    java.lang.Thread.sleep(r4)     // Catch:{ Exception -> 0x01b0 }
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity r1 = it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity.this     // Catch:{ Exception -> 0x01b0 }
                    java.lang.String r1 = r1.ipSlave     // Catch:{ Exception -> 0x01b0 }
                    boolean r1 = it.tecnosystemi.TS.Commands.PicoSocketBootloader.connectToSocket(r1)     // Catch:{ Exception -> 0x01b0 }
                    if (r1 != 0) goto L_0x00c0
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity.this     // Catch:{ Exception -> 0x01b0 }
                    r0.enableBtnAggiorna()     // Catch:{ Exception -> 0x01b0 }
                    return
                L_0x00c0:
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity r1 = it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity.this     // Catch:{ Exception -> 0x01b0 }
                    r1.setBarProgress(r6, r2)     // Catch:{ Exception -> 0x01b0 }
                    java.util.Date r1 = new java.util.Date     // Catch:{ Exception -> 0x01b0 }
                    r1.<init>()     // Catch:{ Exception -> 0x01b0 }
                L_0x00ca:
                    java.lang.String r10 = "VMCBOOT"
                    if (r6 >= r2) goto L_0x0102
                    java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01b0 }
                    r11.<init>()     // Catch:{ Exception -> 0x01b0 }
                    java.lang.String r12 = "curr: "
                    r11.append(r12)     // Catch:{ Exception -> 0x01b0 }
                    r11.append(r6)     // Catch:{ Exception -> 0x01b0 }
                    java.lang.String r12 = " total: "
                    r11.append(r12)     // Catch:{ Exception -> 0x01b0 }
                    r11.append(r2)     // Catch:{ Exception -> 0x01b0 }
                    java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x01b0 }
                    android.util.Log.d(r10, r11)     // Catch:{ Exception -> 0x01b0 }
                    int r10 = r6 + 1300
                    if (r10 <= r2) goto L_0x00ef
                    r10 = r2
                L_0x00ef:
                    r11 = 20
                    java.lang.Thread.sleep(r11)     // Catch:{ Exception -> 0x01b0 }
                    byte[] r6 = java.util.Arrays.copyOfRange(r3, r6, r10)     // Catch:{ Exception -> 0x01b0 }
                    it.tecnosystemi.TS.Commands.PicoSocketBootloader.sendfile(r6)     // Catch:{ Exception -> 0x01b0 }
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity r6 = it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity.this     // Catch:{ Exception -> 0x01b0 }
                    r6.setBarProgress(r10, r2)     // Catch:{ Exception -> 0x01b0 }
                    r6 = r10
                    goto L_0x00ca
                L_0x0102:
                    it.tecnosystemi.TS.Commands.PicoSocketBootloader.closeSocket()     // Catch:{ Exception -> 0x01b0 }
                    java.util.Date r2 = new java.util.Date     // Catch:{ Exception -> 0x01b0 }
                    r2.<init>()     // Catch:{ Exception -> 0x01b0 }
                    long r2 = r2.getTime()     // Catch:{ Exception -> 0x01b0 }
                    long r11 = r1.getTime()     // Catch:{ Exception -> 0x01b0 }
                    long r2 = r2 - r11
                    java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01b0 }
                    r1.<init>()     // Catch:{ Exception -> 0x01b0 }
                    java.lang.String r6 = "durata: "
                    r1.append(r6)     // Catch:{ Exception -> 0x01b0 }
                    r11 = 1000(0x3e8, double:4.94E-321)
                    long r2 = r2 / r11
                    r1.append(r2)     // Catch:{ Exception -> 0x01b0 }
                    java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x01b0 }
                    android.util.Log.d(r10, r1)     // Catch:{ Exception -> 0x01b0 }
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity r1 = it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity.this     // Catch:{ Exception -> 0x01b0 }
                    r1.showProgress()     // Catch:{ Exception -> 0x01b0 }
                    it.tecnosystemi.TS.Commands.UDPSocket.stopListening()     // Catch:{ Exception -> 0x01b0 }
                    java.lang.Thread.sleep(r4)     // Catch:{ Exception -> 0x01b0 }
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity r1 = it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity.this     // Catch:{ Exception -> 0x01b0 }
                    boolean r1 = r1.isSlave     // Catch:{ Exception -> 0x01b0 }
                    if (r1 == 0) goto L_0x0143
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity r1 = it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity.this     // Catch:{ Exception -> 0x01b0 }
                    java.lang.String r1 = r1.ipSlave     // Catch:{ Exception -> 0x01b0 }
                    it.tecnosystemi.TS.Commands.UDPSocket.startListening(r7, r7, r1)     // Catch:{ Exception -> 0x01b0 }
                    goto L_0x0146
                L_0x0143:
                    it.tecnosystemi.TS.Commands.UDPSocket.startListening()     // Catch:{ Exception -> 0x01b0 }
                L_0x0146:
                    it.tecnosystemi.TS.Commands.CmdPICO r1 = new it.tecnosystemi.TS.Commands.CmdPICO     // Catch:{ Exception -> 0x01b0 }
                    r1.<init>()     // Catch:{ Exception -> 0x01b0 }
                    java.lang.String r2 = "ota_end"
                    r1.setCmd(r2)     // Catch:{ Exception -> 0x01b0 }
                    java.lang.String r1 = it.tecnosystemi.TS.Commands.UDPSocket.sendCMD(r1, r8, r8)     // Catch:{ Exception -> 0x01b0 }
                    if (r1 == 0) goto L_0x01b0
                    org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ Exception -> 0x01b0 }
                    r2.<init>(r1)     // Catch:{ Exception -> 0x01b0 }
                    boolean r1 = r2.has(r0)     // Catch:{ Exception -> 0x01b0 }
                    if (r1 == 0) goto L_0x01b0
                    int r0 = r2.getInt(r0)     // Catch:{ Exception -> 0x01b0 }
                    if (r0 != r7) goto L_0x01b0
                    it.tecnosystemi.TS.Commands.CmdPICO$VmcAP r1 = new it.tecnosystemi.TS.Commands.CmdPICO$VmcAP     // Catch:{ Exception -> 0x01b0 }
                    r1.<init>()     // Catch:{ Exception -> 0x01b0 }
                    r0 = 4
                    r1.setAp_m(r0)     // Catch:{ Exception -> 0x01b0 }
                    it.tecnosystemi.TS.Activity.BaseActivity.bootloadResetWiFi = r7     // Catch:{ Exception -> 0x01b0 }
                    r4 = 10000(0x2710, double:4.9407E-320)
                    r6 = 1
                    r2 = 10000(0x2710, double:4.9407E-320)
                    it.tecnosystemi.TS.Commands.UDPSocket.sendCMD(r1, r2, r4, r6)     // Catch:{ Exception -> 0x01b0 }
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity.this     // Catch:{ Exception -> 0x01b0 }
                    r0.hideProgress()     // Catch:{ Exception -> 0x01b0 }
                    it.tecnosystemi.TS.Commands.UDPSocket.stopListening()     // Catch:{ Exception -> 0x01b0 }
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity.this     // Catch:{ Exception -> 0x01b0 }
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity r0 = r0.activity     // Catch:{ Exception -> 0x01b0 }
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity r1 = it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity.this     // Catch:{ Exception -> 0x01b0 }
                    android.content.res.Resources r1 = r1.getResources()     // Catch:{ Exception -> 0x01b0 }
                    int r2 = it.tecnosystemi.TS.R.string.ba_aggiornamento_ok     // Catch:{ Exception -> 0x01b0 }
                    java.lang.String r1 = r1.getString(r2)     // Catch:{ Exception -> 0x01b0 }
                    it.tecnosystemi.TS.Utils.Functions.makeNormalToast(r0, r1)     // Catch:{ Exception -> 0x01b0 }
                    r0 = 2000(0x7d0, double:9.88E-321)
                    java.lang.Thread.sleep(r0)     // Catch:{ Exception -> 0x019a }
                L_0x019a:
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity.this     // Catch:{ Exception -> 0x01b0 }
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity$3$1 r1 = new it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity$3$1     // Catch:{ Exception -> 0x01b0 }
                    r1.<init>()     // Catch:{ Exception -> 0x01b0 }
                    r0.runOnUiThread(r1)     // Catch:{ Exception -> 0x01b0 }
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity.this     // Catch:{ Exception -> 0x01b0 }
                    r0.enableBtnAggiorna()     // Catch:{ Exception -> 0x01b0 }
                    return
                L_0x01aa:
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity.this     // Catch:{ Exception -> 0x01b0 }
                    r0.enableBtnAggiorna()     // Catch:{ Exception -> 0x01b0 }
                    return
                L_0x01b0:
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity.this
                    r0.enableBtnAggiorna()
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity.this
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity r0 = r0.activity
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity r1 = it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity.this
                    android.content.res.Resources r1 = r1.getResources()
                    int r2 = it.tecnosystemi.TS.R.string.ba_aggiornamento_ko
                    java.lang.String r1 = r1.getString(r2)
                    it.tecnosystemi.TS.Utils.Functions.makeErrorToast(r0, r1)
                    it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity.this
                    r0.hideProgress()
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity.AnonymousClass3.run():void");
            }
        }).start();
    }

    public void disableBtnAggiorna() {
        runOnUiThread(new Runnable() {
            public void run() {
                VMCBootloaderActivity.this.btnAggiorna.setEnabled(false);
            }
        });
    }

    /* access modifiers changed from: private */
    public void setBarProgress(final int i, final int i2) {
        runOnUiThread(new Runnable() {
            public void run() {
                VMCBootloaderActivity.this.pb_file.setMax(i2);
                VMCBootloaderActivity.this.pb_file.setProgress(i);
            }
        });
    }

    public void enableBtnAggiorna() {
        runOnUiThread(new Runnable() {
            public void run() {
                VMCBootloaderActivity.this.btnAggiorna.setEnabled(true);
            }
        });
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
        } else if (!VMCActivity.vmc.getOffline().booleanValue()) {
            disconnectFromWIfi();
        } else {
            UDPSocket.startListening(true);
        }
    }

    public View getToolBar() {
        return findViewById(R.id.vmc_toolbar);
    }

    public String setToolbarTitle() {
        return getResources().getString(R.string.ba_vmc_lblTitle);
    }
}
