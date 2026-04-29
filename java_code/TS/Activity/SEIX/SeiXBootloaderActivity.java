package it.tecnosystemi.TS.Activity.SEIX;

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

public class SeiXBootloaderActivity extends BaseActivity {
    public static Class<?> CLASSTOCALL = null;
    static boolean versioneDUBUG = false;
    SeiXBootloaderActivity activity;
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
        setContentView(R.layout.activity_seix_bootloader);
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
                        cmdPICO.setCmd("get_info");
                        if (SeiXBootloaderActivity.this.isSlave) {
                            UDPSocket.startListening(true, true, SeiXBootloaderActivity.this.ipSlave);
                        } else {
                            UDPSocket.startListening();
                        }
                        SeiXBootloaderActivity.this.respInfo = UDPSocket.sendCMD(cmdPICO, 15000, 15000, false, 1);
                        int i = 0;
                        while (SeiXBootloaderActivity.this.respInfo == null && i < 5 && SeiXBootloaderActivity.this.contine_getInfo) {
                            int i2 = i + 1;
                            if (SeiXBootloaderActivity.this.isSlave) {
                                UDPSocket.startListening(true, true, SeiXBootloaderActivity.this.ipSlave);
                            } else {
                                UDPSocket.startListening();
                            }
                            SeiXBootloaderActivity.this.respInfo = UDPSocket.sendCMD(cmdPICO, 15000, 15000, false, 1);
                            if (SeiXBootloaderActivity.this.respInfo == null) {
                                Thread.sleep(2000);
                            }
                            i = i2;
                        }
                        if (SeiXBootloaderActivity.this.contine_getInfo) {
                            SeiXBootloaderActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    try {
                                        JSONObject jSONObject = new JSONObject(SeiXBootloaderActivity.this.respInfo);
                                        StringBuilder sb = new StringBuilder();
                                        sb.append(SeiXBootloaderActivity.this.getResources().getString(R.string.c2_1_pico_InfoFWVer));
                                        sb.append(": ");
                                        SeiXBootloaderActivity.this.old_fwver = jSONObject.getString("fw_ver");
                                        SeiXBootloaderActivity.this.old_ser = jSONObject.getString("ser");
                                        sb.append(SeiXBootloaderActivity.this.old_fwver);
                                        ((TextView) SeiXBootloaderActivity.this.findViewById(R.id.ba_lblSelectFW3)).setText(sb.toString());
                                    } catch (Exception unused) {
                                    }
                                }
                            });
                        }
                    } catch (Exception unused) {
                    }
                    SeiXBootloaderActivity.this.hideProgress();
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
                if (this.infofwsAll.getJSONObject(i).getInt(Constants.JSON_LVDV_Type) == Constants.DEVICE_TYPE_6X) {
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
                if (this.infofwsAll.getJSONObject(i).has(Constants.JSON_LVDV_Type) && this.infofwsAll.getJSONObject(i).getInt(Constants.JSON_LVDV_Type) == Constants.DEVICE_TYPE_6X) {
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
                    String string = SeiXBootloaderActivity.this.infofwToShow.getJSONObject(i).getString("ChangeLogIt");
                    String string2 = SeiXBootloaderActivity.this.infofwToShow.getJSONObject(i).getString("ChangeLogEn");
                    if (string2.toUpperCase().equals("NULL") && string.toUpperCase().equals("NULL")) {
                        SeiXBootloaderActivity.this.lblChangeLog.setText("");
                    } else if (string2.toUpperCase().equals("NULL")) {
                        SeiXBootloaderActivity.this.lblChangeLog.setText(string);
                    } else if (string.toUpperCase().equals("NULL")) {
                        SeiXBootloaderActivity.this.lblChangeLog.setText(string2);
                    } else if (Locale.getDefault().getLanguage().toLowerCase().equals("it")) {
                        SeiXBootloaderActivity.this.lblChangeLog.setText(string);
                    } else {
                        SeiXBootloaderActivity.this.lblChangeLog.setText(string2);
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
            /* JADX WARNING: Can't wrap try/catch for region: R(7:43|44|45|46|47|48|49) */
            /* JADX WARNING: Missing exception handler attribute for start block: B:47:0x0167 */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r13 = this;
                    java.lang.String r0 = "res"
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity r1 = it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity.this     // Catch:{ Exception -> 0x017d }
                    r1.disableBtnAggiorna()     // Catch:{ Exception -> 0x017d }
                    boolean r1 = it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity.versioneDUBUG     // Catch:{ Exception -> 0x017d }
                    if (r1 == 0) goto L_0x001a
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity r1 = it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity.this     // Catch:{ Exception -> 0x017d }
                    android.content.ContentResolver r1 = r1.getContentResolver()     // Catch:{ Exception -> 0x017d }
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity r2 = it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity.this     // Catch:{ Exception -> 0x017d }
                    android.net.Uri r2 = r2.fileUri     // Catch:{ Exception -> 0x017d }
                    java.io.InputStream r1 = r1.openInputStream(r2)     // Catch:{ Exception -> 0x017d }
                    goto L_0x0023
                L_0x001a:
                    java.io.FileInputStream r1 = new java.io.FileInputStream     // Catch:{ Exception -> 0x017d }
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity r2 = it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity.this     // Catch:{ Exception -> 0x017d }
                    java.io.File r2 = r2.file     // Catch:{ Exception -> 0x017d }
                    r1.<init>(r2)     // Catch:{ Exception -> 0x017d }
                L_0x0023:
                    int r2 = r1.available()     // Catch:{ Exception -> 0x017d }
                    byte[] r3 = new byte[r2]     // Catch:{ Exception -> 0x017d }
                    r1.read(r3)     // Catch:{ Exception -> 0x017d }
                    r1.close()     // Catch:{ Exception -> 0x017d }
                    java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x017d }
                    r1.<init>()     // Catch:{ Exception -> 0x017d }
                    java.lang.String r4 = "SHA-256"
                    java.security.MessageDigest r4 = java.security.MessageDigest.getInstance(r4)     // Catch:{ Exception -> 0x017d }
                    r5 = 23
                    byte r5 = r3[r5]     // Catch:{ Exception -> 0x017d }
                    r6 = 0
                    r7 = 1
                    if (r5 != r7) goto L_0x004c
                    int r5 = r2 + -32
                    byte[] r5 = java.util.Arrays.copyOfRange(r3, r6, r5)     // Catch:{ Exception -> 0x017d }
                    r4.update(r5)     // Catch:{ Exception -> 0x017d }
                    goto L_0x004f
                L_0x004c:
                    r4.update(r3)     // Catch:{ Exception -> 0x017d }
                L_0x004f:
                    byte[] r4 = r4.digest()     // Catch:{ Exception -> 0x017d }
                    int r5 = r4.length     // Catch:{ Exception -> 0x017d }
                    r8 = 0
                L_0x0055:
                    if (r8 >= r5) goto L_0x006d
                    byte r9 = r4[r8]     // Catch:{ Exception -> 0x017d }
                    java.lang.String r10 = "%02x"
                    java.lang.Byte r9 = java.lang.Byte.valueOf(r9)     // Catch:{ Exception -> 0x017d }
                    java.lang.Object[] r11 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x017d }
                    r11[r6] = r9     // Catch:{ Exception -> 0x017d }
                    java.lang.String r9 = java.lang.String.format(r10, r11)     // Catch:{ Exception -> 0x017d }
                    r1.append(r9)     // Catch:{ Exception -> 0x017d }
                    int r8 = r8 + 1
                    goto L_0x0055
                L_0x006d:
                    it.tecnosystemi.TS.Commands.CmdPICO$OTA_Start r4 = new it.tecnosystemi.TS.Commands.CmdPICO$OTA_Start     // Catch:{ Exception -> 0x017d }
                    r4.<init>()     // Catch:{ Exception -> 0x017d }
                    java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x017d }
                    r4.setSha(r1)     // Catch:{ Exception -> 0x017d }
                    r4.setSize(r2)     // Catch:{ Exception -> 0x017d }
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity r1 = it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity.this     // Catch:{ Exception -> 0x017d }
                    boolean r1 = r1.isSlave     // Catch:{ Exception -> 0x017d }
                    if (r1 == 0) goto L_0x008a
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity r1 = it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity.this     // Catch:{ Exception -> 0x017d }
                    java.lang.String r1 = r1.ipSlave     // Catch:{ Exception -> 0x017d }
                    it.tecnosystemi.TS.Commands.UDPSocket.startListening(r7, r7, r1)     // Catch:{ Exception -> 0x017d }
                    goto L_0x008d
                L_0x008a:
                    it.tecnosystemi.TS.Commands.UDPSocket.startListening()     // Catch:{ Exception -> 0x017d }
                L_0x008d:
                    r8 = 15000(0x3a98, double:7.411E-320)
                    java.lang.String r1 = it.tecnosystemi.TS.Commands.UDPSocket.sendCMD(r4, r8, r8)     // Catch:{ Exception -> 0x017d }
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity r4 = it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity.this     // Catch:{ Exception -> 0x017d }
                    r4.hideProgress()     // Catch:{ Exception -> 0x017d }
                    if (r1 == 0) goto L_0x0177
                    org.json.JSONObject r4 = new org.json.JSONObject     // Catch:{ Exception -> 0x017d }
                    r4.<init>(r1)     // Catch:{ Exception -> 0x017d }
                    boolean r1 = r4.has(r0)     // Catch:{ Exception -> 0x017d }
                    if (r1 == 0) goto L_0x0177
                    int r1 = r4.getInt(r0)     // Catch:{ Exception -> 0x017d }
                    if (r1 != r7) goto L_0x0177
                    r4 = 500(0x1f4, double:2.47E-321)
                    java.lang.Thread.sleep(r4)     // Catch:{ Exception -> 0x017d }
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity r1 = it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity.this     // Catch:{ Exception -> 0x017d }
                    java.lang.String r1 = r1.ipSlave     // Catch:{ Exception -> 0x017d }
                    boolean r1 = it.tecnosystemi.TS.Commands.PicoSocketBootloader.connectToSocket(r1)     // Catch:{ Exception -> 0x017d }
                    if (r1 != 0) goto L_0x00c0
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity r0 = it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity.this     // Catch:{ Exception -> 0x017d }
                    r0.enableBtnAggiorna()     // Catch:{ Exception -> 0x017d }
                    return
                L_0x00c0:
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity r1 = it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity.this     // Catch:{ Exception -> 0x017d }
                    r1.setBarProgress(r6, r2)     // Catch:{ Exception -> 0x017d }
                    java.util.Date r1 = new java.util.Date     // Catch:{ Exception -> 0x017d }
                    r1.<init>()     // Catch:{ Exception -> 0x017d }
                L_0x00ca:
                    if (r6 >= r2) goto L_0x00e4
                    int r10 = r6 + 1300
                    if (r10 <= r2) goto L_0x00d1
                    r10 = r2
                L_0x00d1:
                    r11 = 20
                    java.lang.Thread.sleep(r11)     // Catch:{ Exception -> 0x017d }
                    byte[] r6 = java.util.Arrays.copyOfRange(r3, r6, r10)     // Catch:{ Exception -> 0x017d }
                    it.tecnosystemi.TS.Commands.PicoSocketBootloader.sendfile(r6)     // Catch:{ Exception -> 0x017d }
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity r6 = it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity.this     // Catch:{ Exception -> 0x017d }
                    r6.setBarProgress(r10, r2)     // Catch:{ Exception -> 0x017d }
                    r6 = r10
                    goto L_0x00ca
                L_0x00e4:
                    it.tecnosystemi.TS.Commands.PicoSocketBootloader.closeSocket()     // Catch:{ Exception -> 0x017d }
                    java.util.Date r2 = new java.util.Date     // Catch:{ Exception -> 0x017d }
                    r2.<init>()     // Catch:{ Exception -> 0x017d }
                    r2.getTime()     // Catch:{ Exception -> 0x017d }
                    r1.getTime()     // Catch:{ Exception -> 0x017d }
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity r1 = it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity.this     // Catch:{ Exception -> 0x017d }
                    r1.showProgress()     // Catch:{ Exception -> 0x017d }
                    it.tecnosystemi.TS.Commands.UDPSocket.stopListening()     // Catch:{ Exception -> 0x017d }
                    java.lang.Thread.sleep(r4)     // Catch:{ Exception -> 0x017d }
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity r1 = it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity.this     // Catch:{ Exception -> 0x017d }
                    boolean r1 = r1.isSlave     // Catch:{ Exception -> 0x017d }
                    if (r1 == 0) goto L_0x010b
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity r1 = it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity.this     // Catch:{ Exception -> 0x017d }
                    java.lang.String r1 = r1.ipSlave     // Catch:{ Exception -> 0x017d }
                    it.tecnosystemi.TS.Commands.UDPSocket.startListening(r7, r7, r1)     // Catch:{ Exception -> 0x017d }
                    goto L_0x010e
                L_0x010b:
                    it.tecnosystemi.TS.Commands.UDPSocket.startListening()     // Catch:{ Exception -> 0x017d }
                L_0x010e:
                    it.tecnosystemi.TS.Commands.CmdPICO r1 = new it.tecnosystemi.TS.Commands.CmdPICO     // Catch:{ Exception -> 0x017d }
                    r1.<init>()     // Catch:{ Exception -> 0x017d }
                    java.lang.String r2 = "ota_end"
                    r1.setCmd(r2)     // Catch:{ Exception -> 0x017d }
                    java.lang.String r1 = it.tecnosystemi.TS.Commands.UDPSocket.sendCMD(r1, r8, r8)     // Catch:{ Exception -> 0x017d }
                    if (r1 == 0) goto L_0x017d
                    org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ Exception -> 0x017d }
                    r2.<init>(r1)     // Catch:{ Exception -> 0x017d }
                    boolean r1 = r2.has(r0)     // Catch:{ Exception -> 0x017d }
                    if (r1 == 0) goto L_0x017d
                    int r0 = r2.getInt(r0)     // Catch:{ Exception -> 0x017d }
                    if (r0 != r7) goto L_0x017d
                    it.tecnosystemi.TS.Commands.CmdPICO$PicoAP r1 = new it.tecnosystemi.TS.Commands.CmdPICO$PicoAP     // Catch:{ Exception -> 0x017d }
                    r1.<init>()     // Catch:{ Exception -> 0x017d }
                    java.lang.String r0 = "set_ap"
                    r1.setCmd(r0)     // Catch:{ Exception -> 0x017d }
                    r0 = 4
                    r1.setAp_m(r0)     // Catch:{ Exception -> 0x017d }
                    it.tecnosystemi.TS.Activity.BaseActivity.bootloadResetWiFi = r7     // Catch:{ Exception -> 0x017d }
                    r4 = 10000(0x2710, double:4.9407E-320)
                    r6 = 1
                    r2 = 10000(0x2710, double:4.9407E-320)
                    it.tecnosystemi.TS.Commands.UDPSocket.sendCMD(r1, r2, r4, r6)     // Catch:{ Exception -> 0x017d }
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity r0 = it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity.this     // Catch:{ Exception -> 0x017d }
                    r0.hideProgress()     // Catch:{ Exception -> 0x017d }
                    it.tecnosystemi.TS.Commands.UDPSocket.stopListening()     // Catch:{ Exception -> 0x017d }
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity r0 = it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity.this     // Catch:{ Exception -> 0x017d }
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity r0 = r0.activity     // Catch:{ Exception -> 0x017d }
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity r1 = it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity.this     // Catch:{ Exception -> 0x017d }
                    android.content.res.Resources r1 = r1.getResources()     // Catch:{ Exception -> 0x017d }
                    int r2 = it.tecnosystemi.TS.R.string.ba_aggiornamento_ok     // Catch:{ Exception -> 0x017d }
                    java.lang.String r1 = r1.getString(r2)     // Catch:{ Exception -> 0x017d }
                    it.tecnosystemi.TS.Utils.Functions.makeNormalToast(r0, r1)     // Catch:{ Exception -> 0x017d }
                    r0 = 2000(0x7d0, double:9.88E-321)
                    java.lang.Thread.sleep(r0)     // Catch:{ Exception -> 0x0167 }
                L_0x0167:
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity r0 = it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity.this     // Catch:{ Exception -> 0x017d }
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity$3$1 r1 = new it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity$3$1     // Catch:{ Exception -> 0x017d }
                    r1.<init>()     // Catch:{ Exception -> 0x017d }
                    r0.runOnUiThread(r1)     // Catch:{ Exception -> 0x017d }
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity r0 = it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity.this     // Catch:{ Exception -> 0x017d }
                    r0.enableBtnAggiorna()     // Catch:{ Exception -> 0x017d }
                    return
                L_0x0177:
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity r0 = it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity.this     // Catch:{ Exception -> 0x017d }
                    r0.enableBtnAggiorna()     // Catch:{ Exception -> 0x017d }
                    return
                L_0x017d:
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity r0 = it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity.this
                    r0.enableBtnAggiorna()
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity r0 = it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity.this
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity r0 = r0.activity
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity r1 = it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity.this
                    android.content.res.Resources r1 = r1.getResources()
                    int r2 = it.tecnosystemi.TS.R.string.ba_aggiornamento_ko
                    java.lang.String r1 = r1.getString(r2)
                    it.tecnosystemi.TS.Utils.Functions.makeErrorToast(r0, r1)
                    it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity r0 = it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity.this
                    r0.hideProgress()
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity.AnonymousClass3.run():void");
            }
        }).start();
    }

    public void disableBtnAggiorna() {
        runOnUiThread(new Runnable() {
            public void run() {
                SeiXBootloaderActivity.this.btnAggiorna.setEnabled(false);
            }
        });
    }

    /* access modifiers changed from: private */
    public void setBarProgress(final int i, final int i2) {
        runOnUiThread(new Runnable() {
            public void run() {
                SeiXBootloaderActivity.this.pb_file.setMax(i2);
                SeiXBootloaderActivity.this.pb_file.setProgress(i);
            }
        });
    }

    public void enableBtnAggiorna() {
        runOnUiThread(new Runnable() {
            public void run() {
                SeiXBootloaderActivity.this.btnAggiorna.setEnabled(true);
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
        }
    }

    public View getToolBar() {
        return findViewById(R.id.ba_toolbar);
    }

    public String setToolbarTitle() {
        return getResources().getString(R.string.bi_lblTitle);
    }
}
