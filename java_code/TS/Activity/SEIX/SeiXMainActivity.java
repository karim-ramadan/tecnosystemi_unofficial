package it.tecnosystemi.TS.Activity.SEIX;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import com.google.gson.Gson;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity;
import it.tecnosystemi.TS.Adapters.ZoneAdapterSeix;
import it.tecnosystemi.TS.Commands.CmdPICO;
import it.tecnosystemi.TS.Commands.Protocols;
import it.tecnosystemi.TS.Commands.UDPSocket;
import it.tecnosystemi.TS.Model.Device;
import it.tecnosystemi.TS.Model.Device_OP;
import it.tecnosystemi.TS.Model.Pico;
import it.tecnosystemi.TS.Model.SeiX;
import it.tecnosystemi.TS.Model.VMC;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Threads.ThreadDowloadFirmWare;
import it.tecnosystemi.TS.Threads.ThreadWebService;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import it.tecnosystemi.TS.Utils.SavePreferences;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SeiXMainActivity extends BaseActivity {
    public static SeiX cu6x;
    private static int idp;
    SeiXMainActivity activity;
    Button btnOnOff;
    Button btnRaff;
    Button btnRisc;
    BaseActivity.BundleMenuList bundlePopUp;
    Device devtodel;
    boolean firstStato = true;
    Date lastRetry;
    TextView lblLastSync;
    TextView lblStatus;
    TextView lblStatusIcon;
    ListView lv;
    SavePreferences pref;
    StatoTimer timerStato;
    String urlStato;
    public ZoneAdapterSeix zoneAdapter;

    public BaseActivity getActivity() {
        return this;
    }

    public static int getIDP() {
        if (idp > 500) {
            idp = 1;
        }
        int i = idp + 1;
        idp = i;
        return i;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_sei_xmain);
        this.activity = this;
        super.onCreate(bundle);
        Functions.setFontsWithIcon(findViewById(R.id.main), this);
        this.pref = new SavePreferences(this, getString(R.string.PrefsName));
        setUpGui();
        this.timerStato = new StatoTimer();
        if (cu6x.getOffline().booleanValue()) {
            this.hideloading = false;
            findViewById(R.id.ly_progress).setVisibility(0);
            this.timerStato.setIntervalMillis(10000);
        }
        this.timerStato.setOnTick(new Runnable() {
            public void run() {
                new Thread(new Runnable() {
                    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
                    /* JADX WARNING: Missing exception handler attribute for start block: B:17:0x0078 */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public void run() {
                        /*
                            r5 = this;
                            it.tecnosystemi.TS.Model.SeiX r0 = it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity.cu6x     // Catch:{ Exception -> 0x00a0 }
                            java.lang.Boolean r0 = r0.getOffline()     // Catch:{ Exception -> 0x00a0 }
                            boolean r0 = r0.booleanValue()     // Catch:{ Exception -> 0x00a0 }
                            if (r0 == 0) goto L_0x00a0
                            boolean r0 = it.tecnosystemi.TS.Commands.UDPSocket.isConnected()     // Catch:{ Exception -> 0x00a0 }
                            if (r0 != 0) goto L_0x0061
                            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity$1 r0 = it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity.AnonymousClass1.this     // Catch:{ Exception -> 0x00a0 }
                            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r0 = it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity.this     // Catch:{ Exception -> 0x00a0 }
                            r0.showProgress()     // Catch:{ Exception -> 0x00a0 }
                            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity$1 r0 = it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity.AnonymousClass1.this     // Catch:{ Exception -> 0x00a0 }
                            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r0 = it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity.this     // Catch:{ Exception -> 0x00a0 }
                            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity$StatoTimer r0 = r0.timerStato     // Catch:{ Exception -> 0x00a0 }
                            r0.stop()     // Catch:{ Exception -> 0x00a0 }
                            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity$1 r0 = it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity.AnonymousClass1.this     // Catch:{ Exception -> 0x00a0 }
                            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r0 = it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity.this     // Catch:{ Exception -> 0x00a0 }
                            java.util.Date r0 = r0.lastRetry     // Catch:{ Exception -> 0x00a0 }
                            if (r0 == 0) goto L_0x0044
                            java.util.Date r0 = new java.util.Date     // Catch:{ Exception -> 0x00a0 }
                            r0.<init>()     // Catch:{ Exception -> 0x00a0 }
                            long r0 = r0.getTime()     // Catch:{ Exception -> 0x00a0 }
                            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity$1 r2 = it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity.AnonymousClass1.this     // Catch:{ Exception -> 0x00a0 }
                            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r2 = it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity.this     // Catch:{ Exception -> 0x00a0 }
                            java.util.Date r2 = r2.lastRetry     // Catch:{ Exception -> 0x00a0 }
                            long r2 = r2.getTime()     // Catch:{ Exception -> 0x00a0 }
                            long r0 = r0 - r2
                            r2 = 10000(0x2710, double:4.9407E-320)
                            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                            if (r4 <= 0) goto L_0x0060
                        L_0x0044:
                            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity$1 r0 = it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity.AnonymousClass1.this     // Catch:{ Exception -> 0x00a0 }
                            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r0 = it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity.this     // Catch:{ Exception -> 0x00a0 }
                            java.util.Date r1 = new java.util.Date     // Catch:{ Exception -> 0x00a0 }
                            r1.<init>()     // Catch:{ Exception -> 0x00a0 }
                            r0.lastRetry = r1     // Catch:{ Exception -> 0x00a0 }
                            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity$1 r0 = it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity.AnonymousClass1.this     // Catch:{ Exception -> 0x00a0 }
                            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r0 = it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity.this     // Catch:{ Exception -> 0x00a0 }
                            r0.showProgress()     // Catch:{ Exception -> 0x00a0 }
                            it.tecnosystemi.TS.Commands.UDPSocket.stopListening()     // Catch:{ Exception -> 0x00a0 }
                            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity$1 r0 = it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity.AnonymousClass1.this     // Catch:{ Exception -> 0x00a0 }
                            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r0 = it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity.this     // Catch:{ Exception -> 0x00a0 }
                            r0.reconnect()     // Catch:{ Exception -> 0x00a0 }
                        L_0x0060:
                            return
                        L_0x0061:
                            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity$1 r0 = it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity.AnonymousClass1.this     // Catch:{ Exception -> 0x00a0 }
                            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r0 = it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity.this     // Catch:{ Exception -> 0x00a0 }
                            boolean r0 = r0.firstStato     // Catch:{ Exception -> 0x00a0 }
                            if (r0 == 0) goto L_0x0070
                            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity$1 r0 = it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity.AnonymousClass1.this     // Catch:{ Exception -> 0x00a0 }
                            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r0 = it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity.this     // Catch:{ Exception -> 0x00a0 }
                            r0.showProgress()     // Catch:{ Exception -> 0x00a0 }
                        L_0x0070:
                            it.tecnosystemi.TS.Commands.UDPSocket.startListening()     // Catch:{ Exception -> 0x00a0 }
                            r0 = 500(0x1f4, double:2.47E-321)
                            java.lang.Thread.sleep(r0)     // Catch:{ Exception -> 0x0078 }
                        L_0x0078:
                            it.tecnosystemi.TS.Commands.CmdPICO r0 = new it.tecnosystemi.TS.Commands.CmdPICO     // Catch:{ Exception -> 0x00a0 }
                            r0.<init>()     // Catch:{ Exception -> 0x00a0 }
                            it.tecnosystemi.TS.Model.SeiX r1 = it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity.cu6x     // Catch:{ Exception -> 0x00a0 }
                            java.lang.String r1 = r1.getPin()     // Catch:{ Exception -> 0x00a0 }
                            r0.setPin(r1)     // Catch:{ Exception -> 0x00a0 }
                            int r1 = it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity.getIDP()     // Catch:{ Exception -> 0x00a0 }
                            long r1 = (long) r1     // Catch:{ Exception -> 0x00a0 }
                            r0.setIdp(r1)     // Catch:{ Exception -> 0x00a0 }
                            java.lang.String r1 = "stato_sync"
                            r0.setCmd(r1)     // Catch:{ Exception -> 0x00a0 }
                            r1 = 6000(0x1770, double:2.9644E-320)
                            java.lang.String r0 = it.tecnosystemi.TS.Commands.UDPSocket.sendCMD(r0, r1)     // Catch:{ Exception -> 0x00a0 }
                            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity$1 r1 = it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity.AnonymousClass1.this     // Catch:{ Exception -> 0x00a0 }
                            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r1 = it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity.this     // Catch:{ Exception -> 0x00a0 }
                            r1.parseStato(r0)     // Catch:{ Exception -> 0x00a0 }
                        L_0x00a0:
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity.AnonymousClass1.AnonymousClass1.run():void");
                    }
                }).start();
            }
        });
        this.timerStato.start();
    }

    public void reconnect() {
        AnonymousClass2 r2 = new Runnable() {
            public void run() {
                SeiXMainActivity.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        SeiXMainActivity.this.hideProgress();
                        UDPSocket.resetIDP();
                        UDPSocket.startListening(true);
                        SeiXMainActivity.this.checkPin();
                    }
                });
            }
        };
        AnonymousClass3 r3 = new Runnable() {
            public void run() {
                SeiXMainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            String str = ((SeiXMainActivity.this.getResources().getString(R.string.ba_apAssente) + "\n" + SeiXMainActivity.this.getResources().getString(R.string.connectToPolaris)) + "\nSSID: " + BaseActivity.toConnSid) + "\n" + SeiXMainActivity.this.getResources().getString(R.string.c4_PwdHint) + ": " + BaseActivity.toConnPwd;
                            AlertDialog.Builder builder = new AlertDialog.Builder(SeiXMainActivity.this.activity);
                            builder.setMessage(str).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    try {
                                        Intent intent = new Intent("android.settings.WIFI_SETTINGS");
                                        intent.addFlags(268435456);
                                        SeiXMainActivity.this.activity.startActivity(intent);
                                    } catch (Exception unused) {
                                    }
                                }
                            });
                            AlertDialog create = builder.create();
                            create.show();
                            create.getButton(-1).setTextColor(SeiXMainActivity.this.getResources().getColor(R.color.picoBlueColor));
                            SeiXMainActivity.this.timerStato.start();
                        } catch (Exception unused) {
                        }
                    }
                });
            }
        };
        toConnPwd = "TS_" + cu6x.getSerial();
        toConnSid = Constants.WIFI_NAME_OFFLINE_6X + cu6x.getSerial();
        connectToWifi(r2, r3, false, false);
    }

    /* access modifiers changed from: private */
    public void checkPin() {
        hideProgress();
        new Thread(new Runnable() {
            public void run() {
                SeiXMainActivity.this.showProgress();
                CmdPICO cmdPICO = new CmdPICO();
                cmdPICO.setCmd(Protocols.CMD_CHECK_PIN);
                cmdPICO.setPin(SeiXMainActivity.cu6x.getPin());
                UDPSocket.startListening();
                try {
                    Thread.sleep(100);
                } catch (Exception unused) {
                }
                String sendCMD = UDPSocket.sendCMD(cmdPICO);
                if (sendCMD == null) {
                    UDPSocket.stopListening();
                    try {
                        Thread.sleep(2000);
                    } catch (Exception unused2) {
                    }
                    UDPSocket.startListening();
                    try {
                        Thread.sleep(100);
                    } catch (Exception unused3) {
                    }
                    sendCMD = UDPSocket.sendCMD(cmdPICO);
                }
                if (sendCMD != null) {
                    try {
                        JSONObject jSONObject = new JSONObject(sendCMD);
                        if (jSONObject.has(Constants.JSON_RES) && jSONObject.getInt(Constants.JSON_RES) == 1) {
                            SeiXMainActivity.this.timerStato.start();
                            SeiXMainActivity.this.hideProgress();
                            return;
                        }
                    } catch (Exception unused4) {
                    }
                } else if (!UDPSocket.isConnected()) {
                    Functions.makeErrorToast(SeiXMainActivity.this.activity, SeiXMainActivity.this.getResources().getString(R.string.connectToPolaris));
                    SeiXMainActivity.this.reconnect();
                    return;
                }
                SeiXMainActivity.this.hideProgress();
                SeiXMainActivity.this.showGetPin();
            }
        }).start();
    }

    /* access modifiers changed from: private */
    public void showGetPin() {
        runOnUiThread(new Runnable() {
            public void run() {
                AnonymousClass1 r0 = new Runnable() {
                    public void run() {
                        SeiXMainActivity.cu6x.setPin(SeiXMainActivity.this.txtPin.getText().toString());
                        SeiXMainActivity seiXMainActivity = SeiXMainActivity.this;
                        seiXMainActivity.urlStato = SeiXMainActivity.this.getResources().getString(R.string.uriWebService) + SeiXMainActivity.this.getResources().getString(R.string.uri_GetVMCState) + "?vmcSerial=" + SeiXMainActivity.cu6x.getSerial() + "&PIN=" + SeiXMainActivity.cu6x.getPin();
                        SeiX.save6XInPref(SeiXMainActivity.cu6x, SeiXMainActivity.this.activity);
                        SeiXMainActivity.this.dismissdialog();
                        if (SeiXMainActivity.cu6x.getOffline().booleanValue()) {
                            SeiXMainActivity.this.checkPin();
                        } else {
                            SeiXMainActivity.this.timerStato.start();
                        }
                    }
                };
                SeiXMainActivity seiXMainActivity = SeiXMainActivity.this;
                seiXMainActivity.bundlePopUp = seiXMainActivity.createSetPin(r0);
                SeiXMainActivity seiXMainActivity2 = SeiXMainActivity.this;
                seiXMainActivity2.openDialogFragment(seiXMainActivity2.bundlePopUp);
            }
        });
    }

    public synchronized void parseStato(String str) {
        Log.d("STATO", str);
        if (this.firstStato) {
            hideProgress();
            this.firstStato = false;
        }
        try {
            new JSONObject(str);
            SeiX.Stato stato = (SeiX.Stato) new Gson().fromJson(str, SeiX.Stato.class);
            SeiX seiX = cu6x;
            if (!(seiX == null || seiX.getStato() == null || cu6x.getStato().getZ() == null)) {
                List<SeiX.Zona> z = cu6x.getStato().getZ();
                for (int i = 0; i < z.size(); i++) {
                    stato.getZ().size();
                    boolean z2 = false;
                    for (int i2 = 0; i2 < stato.getZp().length && !z2; i2++) {
                        z2 = stato.getZp()[i2] == z.get(i).getN();
                    }
                    if (z2) {
                        stato.getZ().add(z.get(i).getN(), z.get(i));
                    }
                }
            }
            cu6x.setStato(stato);
            SeiX seiX2 = cu6x;
            seiX2.setIP(seiX2.getStato().getIp());
            runOnUiThread(new Runnable() {
                public void run() {
                    SeiXMainActivity.this.loadData(false);
                }
            });
        } catch (Exception e) {
            Log.d("STATO", e.toString());
        }
        return;
    }

    public void loadData(boolean z) {
        this.lblStatus.setVisibility(8);
        this.lblStatusIcon.setVisibility(8);
        if (!z) {
            this.btnOnOff.setVisibility(0);
            this.btnRisc.setVisibility(0);
            this.btnRaff.setVisibility(0);
            this.lv.setVisibility(0);
            this.btnOnOff.setText(Constants.CU_ONOFF_ICON[0]);
            changeTitle(cu6x.getName().toUpperCase());
            if (cu6x.getStato().getIs_off() == 1) {
                this.btnOnOff.setTextColor(getResources().getColorStateList(R.color.textdisableselector));
                this.btnRisc.setEnabled(false);
                this.btnRaff.setEnabled(false);
            } else {
                this.btnOnOff.setTextColor(getResources().getColorStateList(R.color.textprimaryselector));
                this.btnRisc.setEnabled(true);
                this.btnRaff.setEnabled(true);
            }
            if (cu6x.getStato().getIs_cool() != 1) {
                this.btnRaff.setBackground(getResources().getDrawable(R.drawable.btndisable));
                this.btnRisc.setBackground(getResources().getDrawable(R.drawable.btn_selector));
                this.btnRaff.setText(Constants.CU_OPERATINGMODE_ICON[1]);
            } else {
                this.btnRisc.setBackground(getResources().getDrawable(R.drawable.btndisable));
                this.btnRaff.setBackground(getResources().getDrawable(R.drawable.btn_selector));
                this.btnRaff.setText(Constants.CU_OPERATINGMODE_ICON[cu6x.getStato().getCool_mod()]);
            }
            if (this.zoneAdapter == null) {
                try {
                    if (cu6x.getStato().getZ().size() > 0) {
                        ZoneAdapterSeix zoneAdapterSeix = new ZoneAdapterSeix(this, cu6x);
                        this.zoneAdapter = zoneAdapterSeix;
                        this.lv.setAdapter(zoneAdapterSeix);
                    }
                } catch (Exception unused) {
                }
            }
            refreshList();
        }
    }

    public void refreshList() {
        SeiX seiX;
        ZoneAdapterSeix zoneAdapterSeix;
        if (!Constants.ISDEMO && (seiX = cu6x) != null && (zoneAdapterSeix = this.zoneAdapter) != null) {
            zoneAdapterSeix.updateData(seiX);
            this.zoneAdapter.notifyDataSetChanged();
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
    }

    public void btnOnOff(View view) {
        showProgress();
        new Thread(new Runnable() {
            public void run() {
                CmdPICO.Upd6X_OnOff upd6X_OnOff = new CmdPICO.Upd6X_OnOff();
                if (SeiXMainActivity.cu6x.getStato().getIs_off() == 1) {
                    upd6X_OnOff.setOn_off(2);
                } else {
                    upd6X_OnOff.setOn_off(1);
                }
                String unused = SeiXMainActivity.this.sendCmdTo6X(upd6X_OnOff, true, true);
            }
        }).start();
    }

    /* access modifiers changed from: private */
    public String sendCmdTo6X(CmdPICO cmdPICO, boolean z, boolean z2) {
        if (z2) {
            showProgress();
        }
        if (z) {
            this.timerStato.stop();
        }
        String str = null;
        try {
            if (cu6x.getOffline().booleanValue()) {
                str = UDPSocket.sendCMD(cmdPICO, 2000);
            }
        } catch (Exception unused) {
        }
        if (z2) {
            hideProgress();
        }
        if (z) {
            this.timerStato.start();
        }
        return str;
    }

    public void createPopUpRinominaCU() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        arrayList.add(cu6x.getName().toUpperCase());
        arrayList2.add(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 6) {
                    return false;
                }
                SeiXMainActivity.this.dismissdialog();
                if (Constants.ISDEMO) {
                    SeiXMainActivity.cu6x.setName(textView.getText().toString().toUpperCase());
                    SeiXMainActivity.this.changeTitle(SeiXMainActivity.cu6x.getName());
                    return false;
                }
                SeiXMainActivity.cu6x.setName(textView.getText().toString().toUpperCase());
                if (SeiXMainActivity.cu6x.getOffline().booleanValue()) {
                    TSDeviceListActivity.SELECTED_DEV.setName(SeiXMainActivity.cu6x.getName());
                    Device.updateDevice(TSDeviceListActivity.SELECTED_DEV, SeiXMainActivity.this.activity);
                    SeiX.save6XInPref(SeiXMainActivity.cu6x, SeiXMainActivity.this.activity);
                    SeiXMainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            SeiXMainActivity.this.changeTitle(SeiXMainActivity.cu6x.getName());
                        }
                    });
                    return false;
                }
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            String unused = SeiXMainActivity.this.sendCmdTo6X((CmdPICO) null, true, true);
                            SeiXMainActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    SeiXMainActivity.this.changeTitle(SeiXMainActivity.cu6x.getName());
                                }
                            });
                        } catch (Exception unused2) {
                        }
                    }
                }).start();
                return false;
            }
        });
        this.bundlePopUp = createTxtPopUp(getResources().getString(R.string.cu_menuRinomina), arrayList, "", arrayList2);
    }

    public void createPopUpUnitMisura() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        arrayList.add(getResources().getString(R.string.cu_unitOfMeasureC));
        arrayList.add(getResources().getString(R.string.cu_unitOfMeasureF));
        arrayList2.add("");
        arrayList2.add("");
        AnonymousClass9 r0 = new Runnable() {
            public void run() {
                SeiXMainActivity.this.dismissdialog();
                Constants.SEI_X_TEMP_UM = 0;
                SeiXMainActivity.this.pref.save(Constants.SEI_X_TEMP_UM_PREF, 0);
                SeiXMainActivity.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        SeiXMainActivity.this.refreshList();
                    }
                });
            }
        };
        AnonymousClass10 r1 = new Runnable() {
            public void run() {
                SeiXMainActivity.this.dismissdialog();
                Constants.SEI_X_TEMP_UM = 1;
                SeiXMainActivity.this.pref.save(Constants.SEI_X_TEMP_UM_PREF, 1);
                SeiXMainActivity.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        SeiXMainActivity.this.refreshList();
                    }
                });
            }
        };
        arrayList3.add(r0);
        arrayList3.add(r1);
        this.bundlePopUp = createPopUp(true, getResources().getString(R.string.cu_menuSetUM), arrayList, arrayList2, (List<String>) null, arrayList3, Constants.SEI_X_TEMP_UM, true);
    }

    public void createPopUpInfoCu() {
        String str = getResources().getString(R.string.cu_idCU) + " " + cu6x.getSerial();
        this.bundlePopUp = createYesNoPopUp(getResources().getString(R.string.cu_infoCentralinaTitle), str, "", getResources().getString(R.string.general_OK), new Runnable() {
            public void run() {
            }
        }, new Runnable() {
            public void run() {
                SeiXMainActivity.this.dismissdialog();
            }
        });
    }

    public void cancellaDevice() {
        Device fromPref = Device.getFromPref(cu6x.getSerial(), Constants.DEVICE_TYPE_6X, this);
        this.devtodel = fromPref;
        if (fromPref == null) {
            Device device = new Device();
            this.devtodel = device;
            device.setSerial(cu6x.getSerial());
            this.devtodel.setLVDV_Type(Constants.DEVICE_TYPE_6X);
        }
        AnonymousClass13 r8 = new Runnable() {
            public void run() {
                SeiXMainActivity.this.dismissdialog();
                Device.deleteDevFromPref(SeiXMainActivity.this.devtodel, SeiXMainActivity.this.activity);
                if (SeiXMainActivity.cu6x == null || !SeiXMainActivity.cu6x.getOffline().booleanValue()) {
                    Pico.deletePICOfromPref(SeiXMainActivity.this.devtodel.getSerial(), SeiXMainActivity.this.activity);
                    Device.deleteDevFromPref(SeiXMainActivity.this.devtodel, SeiXMainActivity.this.activity);
                    Device_OP.DeviceOp deviceOp = new Device_OP.DeviceOp();
                    deviceOp.setDeviceID(SeiXMainActivity.this.devtodel.getLVDV_Id());
                    deviceOp.setToken(SeiXMainActivity.this.activity.FirebaseToken);
                    deviceOp.setPlatform(Constants.NOTIFIC_PLAT);
                    new ThreadWebService(SeiXMainActivity.this.activity, 2, 10, SeiXMainActivity.this.getResources().getString(R.string.uriWebService) + SeiXMainActivity.this.getResources().getString(R.string.uri_DeleteDevice), new Gson().toJson((Object) deviceOp), new String[]{SeiXMainActivity.this.devtodel.getSerial()}).start();
                    return;
                }
                VMC.deleteVMCfromPref(SeiXMainActivity.this.devtodel.getSerial(), SeiXMainActivity.this.activity);
                SeiXMainActivity.this.finish();
            }
        };
        openDialogFragment(createYesNoPopUp(getResources().getString(R.string.hd_deleteDEVAlert_title), getResources().getString(R.string.hd_deleteDEVAlert_text), getResources().getString(R.string.no), getResources().getString(R.string.yes), new Runnable() {
            public void run() {
                SeiXMainActivity.this.dismissdialog();
            }
        }, r8));
    }

    /* access modifiers changed from: private */
    public void setApConfig() {
        this.timerStato.stop();
        if (cu6x.getOffline().booleanValue()) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Intent intent = new Intent(SeiXMainActivity.this.activity, SeiXBootloaderActivity.class);
                    intent.setFlags(67108864);
                    intent.putExtra("FROMPICOACT", true);
                    SeiXMainActivity.this.startActivity(intent);
                }
            });
            return;
        }
        AnonymousClass16 r2 = new Runnable() {
            public void run() {
                SeiXMainActivity.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(SeiXMainActivity.this.activity, SeiXBootloaderActivity.class);
                        intent.setFlags(67108864);
                        intent.putExtra("FROMPICOACT", true);
                        SeiXMainActivity.this.startActivity(intent);
                    }
                });
            }
        };
        AnonymousClass17 r3 = new Runnable() {
            public void run() {
                SeiXMainActivity.this.hideProgress();
                SeiXMainActivity.this.timerStato.start();
            }
        };
        toConnPwd = "TS_" + cu6x.getSerial();
        toConnSid = Constants.WIFI_NAME_OFFLINE_6X + cu6x.getSerial();
        connectToWifi(r2, r3, false, false);
    }

    public void onResume() {
        super.onResume();
        this.timerStato.start();
    }

    public void onPause() {
        super.onPause();
        this.timerStato.stop();
    }

    public void onDestroy() {
        super.onDestroy();
        this.timerStato.stop();
        if (cu6x.getOffline().booleanValue()) {
            try {
                disconnectFromWIfi();
            } catch (Exception unused) {
            }
        }
    }

    public View getToolBar() {
        return findViewById(R.id.proair_toolbar);
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        JSONArray jSONArray;
        AnonymousClass18 r5 = new Runnable() {
            public void run() {
                SeiXMainActivity.this.dismissdialog();
                SeiXMainActivity.this.createPopUpRinominaCU();
                SeiXMainActivity seiXMainActivity = SeiXMainActivity.this;
                seiXMainActivity.openDialogFragment(seiXMainActivity.bundlePopUp);
            }
        };
        list.add(createMenuItem(true, getResources().getString(R.string.cu_menuRinomina), "", (String) null, r5, false, false));
        AnonymousClass19 r6 = new Runnable() {
            public void run() {
                SeiXMainActivity.this.dismissdialog();
                SeiXMainActivity.this.createPopUpUnitMisura();
                SeiXMainActivity seiXMainActivity = SeiXMainActivity.this;
                seiXMainActivity.openDialogFragment(seiXMainActivity.bundlePopUp);
            }
        };
        list.add(createMenuItem(false, getResources().getString(R.string.cu_menuSetUM), "", (String) null, r6, false, false));
        AnonymousClass20 r62 = new Runnable() {
            public void run() {
                SeiXMainActivity.this.dismissdialog();
            }
        };
        list.add(createMenuItem(false, getResources().getString(R.string.cu_menuSetOra), "", (String) null, r62, false, false));
        AnonymousClass21 r63 = new Runnable() {
            public void run() {
                SeiXMainActivity.this.dismissdialog();
                Functions.makeNormalToast(SeiXMainActivity.this.activity, SeiXMainActivity.this.getResources().getString(R.string.cu_setPinInfo));
            }
        };
        list.add(createMenuItem(false, getResources().getString(R.string.cu_menuSetPin), "", (String) null, r63, false, false));
        if (!Constants.ISDEMO) {
            if (!cu6x.getOffline().booleanValue()) {
                AnonymousClass22 r64 = new Runnable() {
                    public void run() {
                        try {
                            SeiXMainActivity.this.dismissdialog();
                        } catch (Exception unused) {
                        }
                        SeiXMainActivity.this.gotobooloader = false;
                        SeiXMainActivity seiXMainActivity = SeiXMainActivity.this.activity;
                        new ThreadDowloadFirmWare(seiXMainActivity, SeiXMainActivity.this.getResources().getString(R.string.uriWebService) + SeiXMainActivity.this.getResources().getString(R.string.uri_infoFrameWork)).start();
                    }
                };
                list.add(createMenuItem(false, getResources().getString(R.string.ha_menuVerificaFW), "", "", r64, false, false));
            }
            AnonymousClass23 r65 = new Runnable() {
                public void run() {
                    SeiXMainActivity.this.dismissdialog();
                    SeiXMainActivity.this.setApConfig();
                }
            };
            try {
                jSONArray = new JSONArray(PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.PREF_INFOFWS, "[]"));
            } catch (JSONException e) {
                e.printStackTrace();
                jSONArray = null;
            }
            if (jSONArray != null && jSONArray.length() > 0) {
                list.add(createMenuItem(false, getResources().getString(R.string.pm_menuUpdateFW), "", (String) null, r65, false, false));
            }
        }
        AnonymousClass24 r66 = new Runnable() {
            public void run() {
                SeiXMainActivity.this.dismissdialog();
                if (Constants.ISDEMO) {
                    Functions.makeNormalToast(SeiXMainActivity.this.activity, SeiXMainActivity.this.getResources().getString(R.string.cu_DemoVersion));
                } else {
                    SeiXMainActivity.this.cancellaDevice();
                }
            }
        };
        list.add(createMenuItem(false, getResources().getString(R.string.pm_deleteDevice), "", (String) null, r66, false, false));
        AnonymousClass25 r67 = new Runnable() {
            public void run() {
                SeiXMainActivity.this.dismissdialog();
                SeiXMainActivity.this.createPopUpInfoCu();
                SeiXMainActivity seiXMainActivity = SeiXMainActivity.this;
                seiXMainActivity.openDialogFragment(seiXMainActivity.bundlePopUp);
            }
        };
        list.add(createMenuItem(false, getResources().getString(R.string.cu_infoCentralina), "", (String) null, r67, false, false));
        return list;
    }

    public String setToolbarTitle() {
        return cu6x.getName().toUpperCase();
    }

    public class StatoTimer {
        /* access modifiers changed from: private */
        public Runnable action;
        /* access modifiers changed from: private */
        public final Handler handler = new Handler(Looper.getMainLooper());
        /* access modifiers changed from: private */
        public int intervalMillis = 5000;
        /* access modifiers changed from: private */
        public boolean isRunning = false;
        private final Runnable runnable = new Runnable() {
            public void run() {
                if (StatoTimer.this.isRunning && StatoTimer.this.action != null) {
                    StatoTimer.this.action.run();
                    StatoTimer.this.handler.postDelayed(this, (long) StatoTimer.this.intervalMillis);
                }
            }
        };

        public StatoTimer() {
        }

        public void setIntervalMillis(int i) {
            this.intervalMillis = i;
        }

        public void start() {
            if (!this.isRunning) {
                this.isRunning = true;
                this.handler.postDelayed(this.runnable, 0);
            }
        }

        public void stop() {
            this.isRunning = false;
            this.handler.removeCallbacks(this.runnable);
        }

        public boolean isRunning() {
            return this.isRunning;
        }

        public void setOnTick(Runnable runnable2) {
            this.action = runnable2;
        }
    }
}
