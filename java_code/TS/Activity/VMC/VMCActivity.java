package it.tecnosystemi.TS.Activity.VMC;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import com.google.gson.Gson;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity;
import it.tecnosystemi.TS.Activity.VMC.Installer.VMCInstallerActivity;
import it.tecnosystemi.TS.Commands.CmdPICO;
import it.tecnosystemi.TS.Commands.PICOServerTimezone;
import it.tecnosystemi.TS.Commands.Protocols;
import it.tecnosystemi.TS.Commands.UDPSocket;
import it.tecnosystemi.TS.Model.Device;
import it.tecnosystemi.TS.Model.Device_OP;
import it.tecnosystemi.TS.Model.ModBusRecipe;
import it.tecnosystemi.TS.Model.Pico;
import it.tecnosystemi.TS.Model.Response;
import it.tecnosystemi.TS.Model.VMC;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Threads.ThreadDowloadFirmWare;
import it.tecnosystemi.TS.Threads.ThreadWebService;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class VMCActivity extends BaseActivity {
    static String TAG = "VMCActivity";
    private static int idp;
    public static List<ModBusRecipe.Param> params;
    public static VMC vmc;
    VMCActivity activity;
    boolean asckedPin = false;
    BaseActivity.BundleMenuList bundlePopUp;
    List<Integer> bypassIcons;
    List<Integer> cronoIcons;
    boolean cronoOn = false;
    Device devtodel;
    boolean errcon = false;
    boolean firstCheck = false;
    boolean firstStato = true;
    boolean gotofasce = false;
    final int idAntigelo = 8;
    final int idCO2 = 16;
    final int idError = 13;
    final int idEspAria = 6;
    final int idGiorniPulizia = 10;
    final int idModelloTaglia = 15;
    final int idParamByPass = 2;
    final int idParamVelVentola = 1;
    final int idStagione = 11;
    final int idStatoFiltri = 9;
    final int idTempAmb = 4;
    final int idTempAspEst = 5;
    final int idTempMand = 7;
    final int idTempRipInt = 3;
    final int idTempoTimer = 12;
    ImageView imgBypass;
    ImageView imgCrono;
    ImageView imgMode;
    boolean isFasce = false;
    Date lastRetry;
    /* access modifiers changed from: private */
    public TextView lblMode;
    /* access modifiers changed from: private */
    public TextView lblTempEspBold;
    /* access modifiers changed from: private */
    public TextView lblTempExtBold;
    /* access modifiers changed from: private */
    public TextView lblTempManBold;
    /* access modifiers changed from: private */
    public TextView lblTempRipBold;
    /* access modifiers changed from: private */
    public TextView lblTempRipH;
    ConstraintLayout lyAntigelo;
    ConstraintLayout lyCrono;
    ConstraintLayout lyFiltri;
    ConstraintLayout lyMode;
    List<Integer> modesIcons;
    List<String> modesLabels;
    int selectedMode;
    boolean sendingCmds = false;
    boolean showingRipresa = false;
    StatoTimer timerStato;
    String urlDateTime;
    String urlStato;
    String urlUpdate;

    public BaseActivity getActivity() {
        return this;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_vmcactivity);
        this.typeActStyle = 3;
        this.activity = this;
        super.onCreate(bundle);
        Functions.setFontsWithIcon(findViewById(R.id.main), this);
        params = null;
        setUpGui();
        this.timerStato = new StatoTimer();
        this.urlStato = getResources().getString(R.string.uriWebService) + getResources().getString(R.string.uri_GetVMCState) + "?vmcSerial=" + vmc.getSerial() + "&PIN=" + vmc.getPin();
        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.uriWebService));
        sb.append(getResources().getString(R.string.uri_SendVMCCmd));
        this.urlUpdate = sb.toString();
        this.urlDateTime = getResources().getString(R.string.uriWebService) + getResources().getString(R.string.uri_UpdVMCTimeAndTimezone);
        if (vmc.getOffline().booleanValue()) {
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
                            r8 = this;
                            it.tecnosystemi.TS.Model.VMC r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc     // Catch:{ Exception -> 0x00e4 }
                            java.lang.Boolean r0 = r0.getOffline()     // Catch:{ Exception -> 0x00e4 }
                            boolean r0 = r0.booleanValue()     // Catch:{ Exception -> 0x00e4 }
                            if (r0 == 0) goto L_0x00a1
                            boolean r0 = it.tecnosystemi.TS.Commands.UDPSocket.isConnected()     // Catch:{ Exception -> 0x00e4 }
                            if (r0 != 0) goto L_0x0061
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity$1 r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.AnonymousClass1.this     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this     // Catch:{ Exception -> 0x00e4 }
                            r0.showProgress()     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity$1 r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.AnonymousClass1.this     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity$StatoTimer r0 = r0.timerStato     // Catch:{ Exception -> 0x00e4 }
                            r0.stop()     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity$1 r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.AnonymousClass1.this     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this     // Catch:{ Exception -> 0x00e4 }
                            java.util.Date r0 = r0.lastRetry     // Catch:{ Exception -> 0x00e4 }
                            if (r0 == 0) goto L_0x0044
                            java.util.Date r0 = new java.util.Date     // Catch:{ Exception -> 0x00e4 }
                            r0.<init>()     // Catch:{ Exception -> 0x00e4 }
                            long r0 = r0.getTime()     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity$1 r2 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.AnonymousClass1.this     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity r2 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this     // Catch:{ Exception -> 0x00e4 }
                            java.util.Date r2 = r2.lastRetry     // Catch:{ Exception -> 0x00e4 }
                            long r2 = r2.getTime()     // Catch:{ Exception -> 0x00e4 }
                            long r0 = r0 - r2
                            r2 = 10000(0x2710, double:4.9407E-320)
                            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
                            if (r4 <= 0) goto L_0x0060
                        L_0x0044:
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity$1 r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.AnonymousClass1.this     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this     // Catch:{ Exception -> 0x00e4 }
                            java.util.Date r1 = new java.util.Date     // Catch:{ Exception -> 0x00e4 }
                            r1.<init>()     // Catch:{ Exception -> 0x00e4 }
                            r0.lastRetry = r1     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity$1 r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.AnonymousClass1.this     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this     // Catch:{ Exception -> 0x00e4 }
                            r0.showProgress()     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Commands.UDPSocket.stopListening()     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity$1 r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.AnonymousClass1.this     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this     // Catch:{ Exception -> 0x00e4 }
                            r0.reconnect()     // Catch:{ Exception -> 0x00e4 }
                        L_0x0060:
                            return
                        L_0x0061:
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity$1 r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.AnonymousClass1.this     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this     // Catch:{ Exception -> 0x00e4 }
                            boolean r0 = r0.firstStato     // Catch:{ Exception -> 0x00e4 }
                            if (r0 == 0) goto L_0x0070
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity$1 r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.AnonymousClass1.this     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this     // Catch:{ Exception -> 0x00e4 }
                            r0.showProgress()     // Catch:{ Exception -> 0x00e4 }
                        L_0x0070:
                            it.tecnosystemi.TS.Commands.UDPSocket.startListening()     // Catch:{ Exception -> 0x00e4 }
                            r0 = 500(0x1f4, double:2.47E-321)
                            java.lang.Thread.sleep(r0)     // Catch:{ Exception -> 0x0078 }
                        L_0x0078:
                            it.tecnosystemi.TS.Commands.CmdPICO r0 = new it.tecnosystemi.TS.Commands.CmdPICO     // Catch:{ Exception -> 0x00e4 }
                            r0.<init>()     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Model.VMC r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc     // Catch:{ Exception -> 0x00e4 }
                            java.lang.String r1 = r1.getPin()     // Catch:{ Exception -> 0x00e4 }
                            r0.setPin(r1)     // Catch:{ Exception -> 0x00e4 }
                            int r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.getIDP()     // Catch:{ Exception -> 0x00e4 }
                            long r1 = (long) r1     // Catch:{ Exception -> 0x00e4 }
                            r0.setIdp(r1)     // Catch:{ Exception -> 0x00e4 }
                            java.lang.String r1 = "stato_sync"
                            r0.setCmd(r1)     // Catch:{ Exception -> 0x00e4 }
                            r1 = 6000(0x1770, double:2.9644E-320)
                            java.lang.String r0 = it.tecnosystemi.TS.Commands.UDPSocket.sendCMD(r0, r1)     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity$1 r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.AnonymousClass1.this     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this     // Catch:{ Exception -> 0x00e4 }
                            r1.parseStato(r0)     // Catch:{ Exception -> 0x00e4 }
                            goto L_0x00e4
                        L_0x00a1:
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity$1 r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.AnonymousClass1.this     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this     // Catch:{ Exception -> 0x00e4 }
                            boolean r0 = r0.firstStato     // Catch:{ Exception -> 0x00e4 }
                            if (r0 == 0) goto L_0x00b0
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity$1 r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.AnonymousClass1.this     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this     // Catch:{ Exception -> 0x00e4 }
                            r0.showProgress()     // Catch:{ Exception -> 0x00e4 }
                        L_0x00b0:
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity$1 r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.AnonymousClass1.this     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity$1 r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.AnonymousClass1.this     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this     // Catch:{ Exception -> 0x00e4 }
                            java.lang.String r2 = r0.urlStato     // Catch:{ Exception -> 0x00e4 }
                            java.lang.String r6 = it.tecnosystemi.TS.Utils.Constants.user     // Catch:{ Exception -> 0x00e4 }
                            r7 = 0
                            r3 = 0
                            r4 = 0
                            r5 = 0
                            it.tecnosystemi.TS.Model.Response r0 = r1.makeApiCall(r2, r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity$1 r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.AnonymousClass1.this     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this     // Catch:{ Exception -> 0x00e4 }
                            boolean r1 = r1.checkonlineError(r0)     // Catch:{ Exception -> 0x00e4 }
                            if (r1 == 0) goto L_0x00e4
                            org.json.JSONObject r1 = new org.json.JSONObject     // Catch:{ Exception -> 0x00e4 }
                            java.lang.String r0 = r0.getHttpResponcePayload()     // Catch:{ Exception -> 0x00e4 }
                            r1.<init>(r0)     // Catch:{ Exception -> 0x00e4 }
                            java.lang.String r0 = "ResDescr"
                            java.lang.String r0 = r1.getString(r0)     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity$1 r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.AnonymousClass1.this     // Catch:{ Exception -> 0x00e4 }
                            it.tecnosystemi.TS.Activity.VMC.VMCActivity r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this     // Catch:{ Exception -> 0x00e4 }
                            r1.parseStato(r0)     // Catch:{ Exception -> 0x00e4 }
                        L_0x00e4:
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.VMC.VMCActivity.AnonymousClass1.AnonymousClass1.run():void");
                    }
                }).start();
            }
        });
        this.timerStato.start();
    }

    public void reconnect() {
        AnonymousClass2 r2 = new Runnable() {
            public void run() {
                VMCActivity.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        VMCActivity.this.hideProgress();
                        UDPSocket.resetIDP();
                        UDPSocket.startListening(true);
                        VMCActivity.this.check\Pin();
                    }
                });
            }
        };
        AnonymousClass3 r3 = new Runnable() {
            public void run() {
                VMCActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            String str = ((VMCActivity.this.getResources().getString(R.string.ba_apAssente) + "\n" + VMCActivity.this.getResources().getString(R.string.connectToPolaris)) + "\nSSID: " + BaseActivity.toConnSid) + "\n" + VMCActivity.this.getResources().getString(R.string.c4_PwdHint) + ": " + BaseActivity.toConnPwd;
                            AlertDialog.Builder builder = new AlertDialog.Builder(VMCActivity.this.activity);
                            builder.setMessage(str).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    try {
                                        Intent intent = new Intent("android.settings.WIFI_SETTINGS");
                                        intent.addFlags(268435456);
                                        VMCActivity.this.activity.startActivity(intent);
                                    } catch (Exception unused) {
                                    }
                                }
                            });
                            AlertDialog create = builder.create();
                            create.show();
                            create.getButton(-1).setTextColor(VMCActivity.this.getResources().getColor(R.color.picoBlueColor));
                            VMCActivity.this.timerStato.start();
                        } catch (Exception unused) {
                        }
                    }
                });
            }
        };
        toConnPwd = "VMC_" + vmc.getSerial();
        toConnSid = "VMC_" + vmc.getSerial();
        connectToWifi(r2, r3, false, false);
    }

    /* access modifiers changed from: private */
    public void checkPin() {
        hideProgress();
        new Thread(new Runnable() {
            public void run() {
                VMCActivity.this.showProgress();
                CmdPICO cmdPICO = new CmdPICO();
                cmdPICO.setCmd(Protocols.CMD_CHECK_PIN);
                cmdPICO.setPin(VMCActivity.vmc.getPin());
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
                            VMCActivity.this.timerStato.start();
                            VMCActivity.this.hideProgress();
                            return;
                        }
                    } catch (Exception unused4) {
                    }
                } else if (!UDPSocket.isConnected()) {
                    Functions.makeErrorToast(VMCActivity.this.activity, VMCActivity.this.getResources().getString(R.string.connectToPolaris));
                    VMCActivity.this.reconnect();
                    return;
                }
                VMCActivity.this.hideProgress();
                VMCActivity.this.showGetPin();
            }
        }).start();
    }

    private void setUpGui() {
        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.lyMode);
        this.lyMode = constraintLayout;
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                VMCActivity.this.createPoUpOperatingMOde();
                VMCActivity vMCActivity = VMCActivity.this;
                vMCActivity.openDialogFragment(vMCActivity.bundlePopUp);
            }
        });
        this.lblMode = (TextView) findViewById(R.id.lblMode);
        this.lblTempExtBold = (TextView) findViewById(R.id.lblTempExtBold);
        this.lblTempEspBold = (TextView) findViewById(R.id.lblTempEspBold);
        this.lblTempRipH = (TextView) findViewById(R.id.lblTempRipH);
        this.lblTempRipBold = (TextView) findViewById(R.id.lblTempRipBold);
        this.lblTempManBold = (TextView) findViewById(R.id.lblTempManBold);
        this.imgMode = (ImageView) findViewById(R.id.imgMode);
        ArrayList arrayList = new ArrayList();
        this.modesLabels = arrayList;
        arrayList.add(getResources().getString(R.string.cr_STANDBY));
        this.modesLabels.add(getResources().getString(R.string.cr_ABSMIN));
        this.modesLabels.add(getResources().getString(R.string.cr_MINSPEED));
        this.modesLabels.add(getResources().getString(R.string.cr_MEDSPEED));
        this.modesLabels.add(getResources().getString(R.string.cr_MAXSPEED));
        this.modesLabels.add(getResources().getString(R.string.cr_BOOSTMODE));
        this.modesLabels.add(getResources().getString(R.string.cr_AUTOMODE));
        ArrayList arrayList2 = new ArrayList();
        this.modesIcons = arrayList2;
        arrayList2.add(Integer.valueOf(R.drawable.vmc_standby1));
        this.modesIcons.add(Integer.valueOf(R.drawable.vmc_absmode));
        this.modesIcons.add(Integer.valueOf(R.drawable.vmc_minimum_speed));
        this.modesIcons.add(Integer.valueOf(R.drawable.vmc_medium_speed));
        this.modesIcons.add(Integer.valueOf(R.drawable.vmc_maximum_speed));
        this.modesIcons.add(Integer.valueOf(R.drawable.vmc_boostmode));
        this.modesIcons.add(Integer.valueOf(R.drawable.vmc_auto));
        ConstraintLayout constraintLayout2 = (ConstraintLayout) findViewById(R.id.lyFiltri);
        this.lyFiltri = constraintLayout2;
        constraintLayout2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                AnonymousClass1 r6 = new Runnable() {
                    public void run() {
                        VMCActivity.this.writeResetFiltri();
                        VMCActivity.this.dismissdialog();
                    }
                };
                VMCActivity vMCActivity = VMCActivity.this;
                vMCActivity.bundlePopUp = vMCActivity.createActionPopUp(vMCActivity.getResources().getString(R.string.vmc_btnGiorniFiltri), String.valueOf((int) VMCActivity.vmc.getGiorniPulizia().getParsedValue()), VMCActivity.this.getResources().getString(R.string.vmc_btnResetFiltri), r6);
                VMCActivity vMCActivity2 = VMCActivity.this;
                vMCActivity2.openDialogFragment(vMCActivity2.bundlePopUp);
            }
        });
        this.lblhome.setTypeface(fontawesome);
        this.lblhome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                StringBuilder sb = new StringBuilder();
                if (VMCActivity.vmc.getErrorParam().getParsedValue() == 4.0d) {
                    sb.append(VMCActivity.this.getResources().getString(R.string.ERRVMC_SensoreRipresa));
                }
                if (VMCActivity.vmc.getErrorParam().getParsedValue() == 5.0d) {
                    sb.append(VMCActivity.this.getResources().getString(R.string.ERRVMC_SensoreAspirazione));
                }
                if (VMCActivity.vmc.getErrorParam().getParsedValue() == 6.0d) {
                    sb.append(VMCActivity.this.getResources().getString(R.string.ERRVMC_SensoreMandata));
                }
                if (VMCActivity.vmc.getErrorParam().getParsedValue() == 7.0d) {
                    sb.append(VMCActivity.this.getResources().getString(R.string.ERRVMC_SensoreEspulsione));
                }
                if (VMCActivity.vmc.getErrorParam().getParsedValue() == 2.0d) {
                    sb.append(VMCActivity.this.getResources().getString(R.string.ERRVMC_SensoreMandata));
                }
                if (VMCActivity.vmc.getErrorParam().getParsedValue() == 9.0d) {
                    sb.append(VMCActivity.this.getResources().getString(R.string.ERRVMC_FiltroSporco));
                }
                if (VMCActivity.vmc.getErrors() != null) {
                    for (int i = 0; i < VMCActivity.vmc.getErrors().length; i++) {
                        if (VMCActivity.vmc.getErrors()[i] != null && VMCActivity.vmc.getErrors()[i].length > 0) {
                            sb.append("\n");
                            sb.append(Functions.getStringResourceByName("VMC" + i, VMCActivity.this.activity));
                            sb.append(":");
                            boolean z = true;
                            for (int i2 = 0; i2 < VMCActivity.vmc.getErrors()[i].length; i2++) {
                                int i3 = VMCActivity.vmc.getErrors()[i][i2] / 1000;
                                int i4 = VMCActivity.vmc.getErrors()[i][i2] % 1000;
                                for (int i5 = 0; i5 < 8; i5++) {
                                    int pow = (int) Math.pow(2.0d, (double) i5);
                                    if ((i4 & pow) == pow) {
                                        if (z) {
                                            z = false;
                                        } else {
                                            sb.append(" - ");
                                        }
                                        sb.append(Functions.getStringResourceByName("ERRVMC" + VMCActivity.vmc.getErrors()[i][i2], VMCActivity.this.activity));
                                    }
                                }
                            }
                        }
                    }
                }
                AnonymousClass1 r10 = new Runnable() {
                    public void run() {
                        VMCActivity.this.dismissdialog();
                    }
                };
                VMCActivity vMCActivity = VMCActivity.this;
                VMCActivity.this.openDialogFragment(vMCActivity.createYesNoPopUp(vMCActivity.getString(R.string.pm_dialogPICOinError), sb.toString(), "", VMCActivity.this.getString(R.string.general_OK), (Runnable) null, r10));
            }
        });
        this.lyCrono = (ConstraintLayout) findViewById(R.id.lyCrono);
        this.imgCrono = (ImageView) findViewById(R.id.imgCrono);
        ArrayList arrayList3 = new ArrayList();
        this.cronoIcons = arrayList3;
        arrayList3.add(Integer.valueOf(R.drawable.vmc_cronooff));
        this.cronoIcons.add(Integer.valueOf(R.drawable.vmc_cronoonfasceoff));
        this.cronoIcons.add(Integer.valueOf(R.drawable.vmc_cronoonfasceon));
        this.imgBypass = (ImageView) findViewById(R.id.imgBypass);
        ArrayList arrayList4 = new ArrayList();
        this.bypassIcons = arrayList4;
        arrayList4.add(Integer.valueOf(R.drawable.vmc_bypassoff));
        this.bypassIcons.add(Integer.valueOf(R.drawable.vmc_bypasson));
        this.lyAntigelo = (ConstraintLayout) findViewById(R.id.lyAntigelo);
        ConstraintLayout constraintLayout3 = (ConstraintLayout) findViewById(R.id.lyCrono);
        this.lyCrono = constraintLayout3;
        constraintLayout3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                VMCActivity.this.checkAndGoToFasce();
            }
        });
    }

    public void createPoUpOperatingMOde() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new Runnable() {
            public void run() {
                VMCActivity.this.SendCmdMode(0);
                VMCActivity.this.dismissdialog();
            }
        });
        arrayList.add(new Runnable() {
            public void run() {
                VMCActivity.this.SendCmdMode(1);
                VMCActivity.this.dismissdialog();
            }
        });
        arrayList.add(new Runnable() {
            public void run() {
                VMCActivity.this.SendCmdMode(2);
                VMCActivity.this.dismissdialog();
            }
        });
        arrayList.add(new Runnable() {
            public void run() {
                VMCActivity.this.SendCmdMode(3);
                VMCActivity.this.dismissdialog();
            }
        });
        arrayList.add(new Runnable() {
            public void run() {
                VMCActivity.this.SendCmdMode(4);
                VMCActivity.this.dismissdialog();
            }
        });
        arrayList.add(new Runnable() {
            public void run() {
                VMCActivity.this.SendCmdMode(5);
                VMCActivity.this.dismissdialog();
            }
        });
        arrayList.add(new Runnable() {
            public void run() {
                VMCActivity.this.SendCmdMode(6);
                VMCActivity.this.dismissdialog();
            }
        });
        this.bundlePopUp = createPopUpImg(true, getResources().getString(R.string.vmc_menuModalita), this.modesLabels, this.modesIcons, arrayList, this.selectedMode, true);
    }

    public synchronized void parseStato(String str) {
        if (str != null) {
            if (!this.sendingCmds) {
                try {
                    if (this.firstStato) {
                        hideProgress();
                    }
                    this.firstStato = false;
                    JSONObject jSONObject = new JSONObject(str);
                    if (params == null) {
                        params = Functions.getParamsFromKey(jSONObject.getString("key_recipe"), 1);
                    }
                    JSONArray jSONArray = jSONObject.getJSONArray("ids");
                    JSONArray jSONArray2 = jSONObject.getJSONArray("val");
                    for (int i = 0; i < jSONArray.length(); i++) {
                        ModBusRecipe.Param param = params.get(jSONArray.getInt(i));
                        param.setValue(jSONArray2.getString(i));
                        long pRPA_IdParam = param.getPRPA_IdParam();
                        if (pRPA_IdParam == 1) {
                            vmc.setVelVentola(param);
                        } else if (pRPA_IdParam == 2) {
                            vmc.setByPass(param);
                        } else if (pRPA_IdParam == 3) {
                            vmc.setTempRipInt(param);
                        } else if (pRPA_IdParam == 4) {
                            vmc.setTempAmb(param);
                        } else if (pRPA_IdParam == 5) {
                            vmc.setTempAspEst(param);
                        } else if (pRPA_IdParam == 6) {
                            vmc.setTempEspAria(param);
                        } else if (pRPA_IdParam == 7) {
                            vmc.setTempMand(param);
                        } else if (pRPA_IdParam == 8) {
                            vmc.setAntigelo(param);
                        } else if (pRPA_IdParam == 9) {
                            vmc.setStatoFiltri(param);
                        } else if (pRPA_IdParam == 10) {
                            vmc.setGiorniPulizia(param);
                        } else if (pRPA_IdParam == 11) {
                            vmc.setStagione(param);
                        } else if (pRPA_IdParam == 12) {
                            vmc.setTempoTimer(param);
                        } else if (pRPA_IdParam == 13) {
                            vmc.setErrorParam(param);
                        } else if (pRPA_IdParam == 15) {
                            vmc.setModelloTaglia(param);
                        } else if (pRPA_IdParam == 16) {
                            vmc.setcO2(param);
                        }
                    }
                    JSONArray jSONArray3 = jSONObject.getJSONArray(NotificationCompat.CATEGORY_ERROR);
                    int length = jSONArray3.length();
                    int[][] iArr = new int[length][];
                    for (int i2 = 0; i2 < length; i2++) {
                        iArr[i2] = Functions.getIntArray(jSONArray3.getJSONArray(i2));
                    }
                    vmc.setErrors(iArr);
                    vmc.setM_crono(jSONObject.getInt("m_crono"));
                    vmc.setTw_active(jSONObject.getInt("tw_active"));
                    if (jSONObject.has("timezone")) {
                        vmc.setTimezone(jSONObject.getString("timezone"));
                    }
                    vmc.setFw_ver(jSONObject.getString("fw_ver"));
                    vmc.setKey_recipe(jSONObject.getString("key_recipe"));
                    updateView(true);
                } catch (Exception unused) {
                }
            }
        }
    }

    public void updateView(final boolean z) {
        runOnUiThread(new Runnable() {
            /* JADX WARNING: Removed duplicated region for block: B:38:0x01c9  */
            /* JADX WARNING: Removed duplicated region for block: B:45:0x021c  */
            /* JADX WARNING: Removed duplicated region for block: B:51:0x0254  */
            /* JADX WARNING: Removed duplicated region for block: B:52:0x0274  */
            /* JADX WARNING: Removed duplicated region for block: B:55:0x02a1  */
            /* JADX WARNING: Removed duplicated region for block: B:56:0x02a9  */
            /* JADX WARNING: Removed duplicated region for block: B:59:0x02be  */
            /* JADX WARNING: Removed duplicated region for block: B:60:0x02c0  */
            /* JADX WARNING: Removed duplicated region for block: B:72:0x02ea  */
            /* JADX WARNING: Removed duplicated region for block: B:83:0x030f  */
            /* JADX WARNING: Removed duplicated region for block: B:84:0x0319  */
            /* JADX WARNING: Removed duplicated region for block: B:87:0x030a A[SYNTHETIC] */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r15 = this;
                    it.tecnosystemi.TS.Model.VMC r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc
                    it.tecnosystemi.TS.Model.ModBusRecipe$Param r0 = r0.getVelVentola()
                    if (r0 != 0) goto L_0x0009
                    return
                L_0x0009:
                    it.tecnosystemi.TS.Model.VMC r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc
                    it.tecnosystemi.TS.Model.ModBusRecipe$Param r0 = r0.getVelVentola()
                    double r0 = r0.getParsedValue()
                    r2 = 4
                    r3 = 2
                    r4 = 0
                    r6 = 1
                    r7 = 0
                    int r8 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
                    if (r8 == 0) goto L_0x00b7
                    it.tecnosystemi.TS.Model.VMC r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc
                    it.tecnosystemi.TS.Model.ModBusRecipe$Param r0 = r0.getVelVentola()
                    double r0 = r0.getParsedValue()
                    r8 = 4626604192193052672(0x4035000000000000, double:21.0)
                    int r10 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
                    if (r10 != 0) goto L_0x002f
                    goto L_0x00b7
                L_0x002f:
                    it.tecnosystemi.TS.Model.VMC r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc
                    it.tecnosystemi.TS.Model.ModBusRecipe$Param r0 = r0.getVelVentola()
                    double r0 = r0.getParsedValue()
                    r8 = 4607182418800017408(0x3ff0000000000000, double:1.0)
                    int r10 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
                    if (r10 != 0) goto L_0x0046
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    r0.setMode(r6)
                    goto L_0x00bc
                L_0x0046:
                    it.tecnosystemi.TS.Model.VMC r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc
                    it.tecnosystemi.TS.Model.ModBusRecipe$Param r0 = r0.getVelVentola()
                    double r0 = r0.getParsedValue()
                    r8 = 4611686018427387904(0x4000000000000000, double:2.0)
                    int r10 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
                    if (r10 != 0) goto L_0x005c
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    r0.setMode(r3)
                    goto L_0x00bc
                L_0x005c:
                    it.tecnosystemi.TS.Model.VMC r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc
                    it.tecnosystemi.TS.Model.ModBusRecipe$Param r0 = r0.getVelVentola()
                    double r0 = r0.getParsedValue()
                    r8 = 4627167142146473984(0x4037000000000000, double:23.0)
                    int r10 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
                    if (r10 != 0) goto L_0x0073
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    r1 = 3
                    r0.setMode(r1)
                    goto L_0x00bc
                L_0x0073:
                    it.tecnosystemi.TS.Model.VMC r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc
                    it.tecnosystemi.TS.Model.ModBusRecipe$Param r0 = r0.getVelVentola()
                    double r0 = r0.getParsedValue()
                    r8 = 4613937818241073152(0x4008000000000000, double:3.0)
                    int r10 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
                    if (r10 != 0) goto L_0x0089
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    r0.setMode(r2)
                    goto L_0x00bc
                L_0x0089:
                    it.tecnosystemi.TS.Model.VMC r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc
                    it.tecnosystemi.TS.Model.ModBusRecipe$Param r0 = r0.getVelVentola()
                    double r0 = r0.getParsedValue()
                    r8 = 4623507967449235456(0x402a000000000000, double:13.0)
                    int r10 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
                    if (r10 != 0) goto L_0x00a0
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    r1 = 5
                    r0.setMode(r1)
                    goto L_0x00bc
                L_0x00a0:
                    it.tecnosystemi.TS.Model.VMC r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc
                    it.tecnosystemi.TS.Model.ModBusRecipe$Param r0 = r0.getVelVentola()
                    double r0 = r0.getParsedValue()
                    r8 = 4627448617123184640(0x4038000000000000, double:24.0)
                    int r10 = (r0 > r8 ? 1 : (r0 == r8 ? 0 : -1))
                    if (r10 != 0) goto L_0x00bc
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    r1 = 6
                    r0.setMode(r1)
                    goto L_0x00bc
                L_0x00b7:
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    r0.setMode(r7)
                L_0x00bc:
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    android.widget.TextView r0 = r0.lblTempExtBold
                    it.tecnosystemi.TS.Model.VMC r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc
                    it.tecnosystemi.TS.Model.ModBusRecipe$Param r1 = r1.getTempAspEst()
                    r8 = -4591138345127510016(0xc049000000000000, double:-50.0)
                    java.lang.Double r10 = java.lang.Double.valueOf(r8)
                    r11 = 4639481672377565184(0x4062c00000000000, double:150.0)
                    java.lang.Double r13 = java.lang.Double.valueOf(r11)
                    java.lang.String r1 = r1.getValToShowUser(r10, r13)
                    java.lang.String r1 = java.lang.String.valueOf(r1)
                    r0.setText(r1)
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    android.widget.TextView r0 = r0.lblTempEspBold
                    it.tecnosystemi.TS.Model.VMC r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc
                    it.tecnosystemi.TS.Model.ModBusRecipe$Param r1 = r1.getTempEspAria()
                    java.lang.Double r10 = java.lang.Double.valueOf(r8)
                    java.lang.Double r13 = java.lang.Double.valueOf(r11)
                    java.lang.String r1 = r1.getValToShowUser(r10, r13)
                    java.lang.String r1 = java.lang.String.valueOf(r1)
                    r0.setText(r1)
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    android.widget.TextView r0 = r0.lblTempManBold
                    it.tecnosystemi.TS.Model.VMC r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc
                    it.tecnosystemi.TS.Model.ModBusRecipe$Param r1 = r1.getTempMand()
                    java.lang.Double r10 = java.lang.Double.valueOf(r8)
                    java.lang.Double r13 = java.lang.Double.valueOf(r11)
                    java.lang.String r1 = r1.getValToShowUser(r10, r13)
                    java.lang.String r1 = java.lang.String.valueOf(r1)
                    r0.setText(r1)
                    boolean r0 = r2
                    if (r0 == 0) goto L_0x0189
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    boolean r0 = r0.showingRipresa
                    if (r0 == 0) goto L_0x0189
                    it.tecnosystemi.TS.Model.VMC r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc
                    it.tecnosystemi.TS.Model.ModBusRecipe$Param r0 = r0.getcO2()
                    double r0 = r0.getParsedValue()
                    r13 = 4643985272004935680(0x4072c00000000000, double:300.0)
                    int r10 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
                    if (r10 < 0) goto L_0x0189
                    r13 = 4656510908468559872(0x409f400000000000, double:2000.0)
                    int r10 = (r0 > r13 ? 1 : (r0 == r13 ? 0 : -1))
                    if (r10 > 0) goto L_0x0189
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    r0.showingRipresa = r7
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    android.widget.TextView r0 = r0.lblTempRipH
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    android.content.res.Resources r1 = r1.getResources()
                    int r8 = it.tecnosystemi.TS.R.string.vmc_lblCO2
                    java.lang.String r1 = r1.getString(r8)
                    r0.setText(r1)
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    android.widget.TextView r0 = r0.lblTempRipBold
                    it.tecnosystemi.TS.Model.VMC r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc
                    it.tecnosystemi.TS.Model.ModBusRecipe$Param r1 = r1.getcO2()
                    r8 = 4643983512786331238(0x4072be6666666666, double:299.9)
                    java.lang.Double r8 = java.lang.Double.valueOf(r8)
                    r9 = 4656511348273210982(0x409f406666666666, double:2000.1)
                    java.lang.Double r9 = java.lang.Double.valueOf(r9)
                    java.lang.String r1 = r1.getValToShowUser(r8, r9)
                    java.lang.String r1 = java.lang.String.valueOf(r1)
                    r0.setText(r1)
                    goto L_0x01c1
                L_0x0189:
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    r0.showingRipresa = r6
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    android.widget.TextView r0 = r0.lblTempRipH
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    android.content.res.Resources r1 = r1.getResources()
                    int r10 = it.tecnosystemi.TS.R.string.vmc_lblTempRipresa
                    java.lang.String r1 = r1.getString(r10)
                    r0.setText(r1)
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    android.widget.TextView r0 = r0.lblTempRipBold
                    it.tecnosystemi.TS.Model.VMC r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc
                    it.tecnosystemi.TS.Model.ModBusRecipe$Param r1 = r1.getTempRipInt()
                    java.lang.Double r8 = java.lang.Double.valueOf(r8)
                    java.lang.Double r9 = java.lang.Double.valueOf(r11)
                    java.lang.String r1 = r1.getValToShowUser(r8, r9)
                    java.lang.String r1 = java.lang.String.valueOf(r1)
                    r0.setText(r1)
                L_0x01c1:
                    it.tecnosystemi.TS.Model.VMC r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc
                    int r0 = r0.getM_crono()
                    if (r0 != r6) goto L_0x021c
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    boolean r0 = r0.cronoOn
                    if (r0 != 0) goto L_0x01d4
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    r0.updatemenu()
                L_0x01d4:
                    it.tecnosystemi.TS.Model.VMC r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc
                    int r0 = r0.getTw_active()
                    if (r0 <= 0) goto L_0x01fc
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    android.widget.ImageView r0 = r0.imgCrono
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    android.content.res.Resources r1 = r1.getResources()
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r8 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    java.util.List<java.lang.Integer> r8 = r8.cronoIcons
                    java.lang.Object r3 = r8.get(r3)
                    java.lang.Integer r3 = (java.lang.Integer) r3
                    int r3 = r3.intValue()
                    android.graphics.drawable.Drawable r1 = r1.getDrawable(r3)
                    r0.setImageDrawable(r1)
                    goto L_0x0246
                L_0x01fc:
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    android.widget.ImageView r0 = r0.imgCrono
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    android.content.res.Resources r1 = r1.getResources()
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r3 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    java.util.List<java.lang.Integer> r3 = r3.cronoIcons
                    java.lang.Object r3 = r3.get(r6)
                    java.lang.Integer r3 = (java.lang.Integer) r3
                    int r3 = r3.intValue()
                    android.graphics.drawable.Drawable r1 = r1.getDrawable(r3)
                    r0.setImageDrawable(r1)
                    goto L_0x0246
                L_0x021c:
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    boolean r0 = r0.cronoOn
                    if (r0 == 0) goto L_0x0227
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    r0.updatemenu()
                L_0x0227:
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    android.widget.ImageView r0 = r0.imgCrono
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    android.content.res.Resources r1 = r1.getResources()
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r3 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    java.util.List<java.lang.Integer> r3 = r3.cronoIcons
                    java.lang.Object r3 = r3.get(r7)
                    java.lang.Integer r3 = (java.lang.Integer) r3
                    int r3 = r3.intValue()
                    android.graphics.drawable.Drawable r1 = r1.getDrawable(r3)
                    r0.setImageDrawable(r1)
                L_0x0246:
                    it.tecnosystemi.TS.Model.VMC r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc
                    it.tecnosystemi.TS.Model.ModBusRecipe$Param r0 = r0.getByPass()
                    double r0 = r0.getParsedValue()
                    int r3 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
                    if (r3 != 0) goto L_0x0274
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    android.widget.ImageView r0 = r0.imgBypass
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    android.content.res.Resources r1 = r1.getResources()
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r3 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    java.util.List<java.lang.Integer> r3 = r3.bypassIcons
                    java.lang.Object r3 = r3.get(r7)
                    java.lang.Integer r3 = (java.lang.Integer) r3
                    int r3 = r3.intValue()
                    android.graphics.drawable.Drawable r1 = r1.getDrawable(r3)
                    r0.setImageDrawable(r1)
                    goto L_0x0293
                L_0x0274:
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    android.widget.ImageView r0 = r0.imgBypass
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    android.content.res.Resources r1 = r1.getResources()
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r3 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    java.util.List<java.lang.Integer> r3 = r3.bypassIcons
                    java.lang.Object r3 = r3.get(r6)
                    java.lang.Integer r3 = (java.lang.Integer) r3
                    int r3 = r3.intValue()
                    android.graphics.drawable.Drawable r1 = r1.getDrawable(r3)
                    r0.setImageDrawable(r1)
                L_0x0293:
                    it.tecnosystemi.TS.Model.VMC r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc
                    it.tecnosystemi.TS.Model.ModBusRecipe$Param r0 = r0.getAntigelo()
                    double r0 = r0.getParsedValue()
                    int r3 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
                    if (r3 != 0) goto L_0x02a9
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    androidx.constraintlayout.widget.ConstraintLayout r0 = r0.lyAntigelo
                    r0.setVisibility(r2)
                    goto L_0x02b0
                L_0x02a9:
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    androidx.constraintlayout.widget.ConstraintLayout r0 = r0.lyAntigelo
                    r0.setVisibility(r7)
                L_0x02b0:
                    it.tecnosystemi.TS.Model.VMC r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc
                    it.tecnosystemi.TS.Model.ModBusRecipe$Param r0 = r0.getErrorParam()
                    double r0 = r0.getParsedValue()
                    int r2 = (r0 > r4 ? 1 : (r0 == r4 ? 0 : -1))
                    if (r2 != 0) goto L_0x02c0
                    r0 = 1
                    goto L_0x02c1
                L_0x02c0:
                    r0 = 0
                L_0x02c1:
                    if (r0 == 0) goto L_0x030d
                    it.tecnosystemi.TS.Model.VMC r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc
                    int[][] r1 = r1.getErrors()
                    if (r1 == 0) goto L_0x030d
                    it.tecnosystemi.TS.Model.VMC r1 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc
                    int[][] r1 = r1.getErrors()
                    int r1 = r1.length
                    if (r1 <= 0) goto L_0x030d
                    r1 = 0
                L_0x02d5:
                    it.tecnosystemi.TS.Model.VMC r2 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc
                    int[][] r2 = r2.getErrors()
                    int r2 = r2.length
                    if (r1 >= r2) goto L_0x030d
                    if (r0 == 0) goto L_0x030d
                    it.tecnosystemi.TS.Model.VMC r2 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc
                    int[][] r2 = r2.getErrors()
                    r2 = r2[r1]
                    if (r2 == 0) goto L_0x030a
                    r2 = 0
                L_0x02eb:
                    it.tecnosystemi.TS.Model.VMC r3 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc
                    int[][] r3 = r3.getErrors()
                    r3 = r3[r1]
                    int r3 = r3.length
                    if (r2 >= r3) goto L_0x030a
                    if (r0 == 0) goto L_0x030a
                    it.tecnosystemi.TS.Model.VMC r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.vmc
                    int[][] r0 = r0.getErrors()
                    r0 = r0[r1]
                    r0 = r0[r2]
                    if (r0 != 0) goto L_0x0306
                    r0 = 1
                    goto L_0x0307
                L_0x0306:
                    r0 = 0
                L_0x0307:
                    int r2 = r2 + 1
                    goto L_0x02eb
                L_0x030a:
                    int r1 = r1 + 1
                    goto L_0x02d5
                L_0x030d:
                    if (r0 == 0) goto L_0x0319
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    android.widget.TextView r0 = r0.lblhome
                    r1 = 8
                    r0.setVisibility(r1)
                    goto L_0x0320
                L_0x0319:
                    it.tecnosystemi.TS.Activity.VMC.VMCActivity r0 = it.tecnosystemi.TS.Activity.VMC.VMCActivity.this
                    android.widget.TextView r0 = r0.lblhome
                    r0.setVisibility(r7)
                L_0x0320:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.VMC.VMCActivity.AnonymousClass16.run():void");
            }
        });
    }

    public void SendCmdMode(final int i) {
        if (i != this.selectedMode) {
            new Thread(new Runnable() {
                public void run() {
                    VMCActivity.this.sendingCmds = true;
                    VMCActivity.this.showProgress();
                    try {
                        float f = 0.0f;
                        switch (i) {
                            case 0:
                                break;
                            case 1:
                                f = 1.0f;
                                break;
                            case 2:
                                f = 2.0f;
                                break;
                            case 3:
                                f = 23.0f;
                                break;
                            case 4:
                                f = 3.0f;
                                break;
                            case 5:
                                f = 13.0f;
                                break;
                            case 6:
                                f = 24.0f;
                                break;
                        }
                        CmdPICO.Wr_param fromParam = CmdPICO.Wr_param.fromParam(VMCActivity.vmc.getVelVentola(), VMCActivity.vmc.getKey_recipe(), f);
                        fromParam.setPin(VMCActivity.vmc.getPin());
                        Pair access$1000 = VMCActivity.this.sendCmdUpdVMC(fromParam);
                        if (((Boolean) access$1000.first).booleanValue() && VMCActivity.this.checkRespWriteParam((String) access$1000.second)) {
                            VMCActivity.this.setMode(i);
                        }
                    } catch (Exception unused) {
                    }
                    VMCActivity.this.hideProgress();
                    VMCActivity.this.sendingCmds = false;
                }
            }).start();
        }
    }

    public boolean checkRespWriteParam(String str) {
        try {
            Log.d(TAG, str);
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.has("res") && jSONObject.getInt("res") == 1) {
                return true;
            }
        } catch (Exception unused) {
        }
        VMCActivity vMCActivity = this.activity;
        Functions.makeErrorToast(vMCActivity, vMCActivity.getResources().getString(R.string.msg_commandKo));
        return false;
    }

    public void setMode(int i) {
        this.selectedMode = i;
        runOnUiThread(new Runnable() {
            public void run() {
                VMCActivity.this.lblMode.setText(VMCActivity.this.modesLabels.get(VMCActivity.this.selectedMode));
                VMCActivity.this.imgMode.setImageDrawable(VMCActivity.this.getResources().getDrawable(VMCActivity.this.modesIcons.get(VMCActivity.this.selectedMode).intValue()));
            }
        });
    }

    public void goToInstall() {
        if (params != null) {
            openDialogFragment(createGenarlPin(new Runnable() {
                public void run() {
                    VMCActivity.this.dismissdialog();
                    if (VMCActivity.this.txtPin2.getText().toString().equals("6673")) {
                        VMCActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                VMCActivity.this.startActivity(new Intent(VMCActivity.this.activity, VMCInstallerActivity.class));
                            }
                        });
                    } else {
                        Functions.makeErrorToast(VMCActivity.this.activity, VMCActivity.this.getResources().getString(R.string.vmc_PinError));
                    }
                }
            }, new Runnable() {
                public void run() {
                    VMCActivity.this.dismissdialog();
                }
            }, getResources().getString(R.string.vmc_pinRequest), getResources().getString(R.string.vmc_pinHint), ""));
        }
    }

    public void createPopUpRinominaCU() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        arrayList.add(vmc.getName().toUpperCase());
        arrayList2.add(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 6) {
                    return false;
                }
                VMCActivity.this.dismissdialog();
                if (Constants.ISDEMO) {
                    VMCActivity.vmc.setName(textView.getText().toString().toUpperCase());
                    VMCActivity.this.changeTitle(VMCActivity.vmc.getName());
                    return false;
                }
                VMCActivity.vmc.setName(textView.getText().toString().toUpperCase());
                if (VMCActivity.vmc.getOffline().booleanValue()) {
                    TSDeviceListActivity.SELECTED_DEV.setName(VMCActivity.vmc.getName());
                    Device.updateDevice(TSDeviceListActivity.SELECTED_DEV, VMCActivity.this.activity);
                    VMC.saveVMCInPref(VMCActivity.vmc, VMCActivity.this.activity);
                    return false;
                }
                new Thread(new Runnable() {
                    public void run() {
                        try {
                            Pair unused = VMCActivity.this.sendCmdUpdVMC((CmdPICO) null);
                            VMCActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    VMCActivity.this.changeTitle(VMCActivity.vmc.getName());
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

    /* access modifiers changed from: private */
    public void setDateTime() {
        this.bundlePopUp = createDataOraTimezonePopUp(getResources().getString(R.string.dialog_data_ora_title), "", vmc.getTimezone());
    }

    public void cancellaDevice() {
        Device fromPref = Device.getFromPref(vmc.getSerial(), Constants.DEVICE_TYPE_VMC, this);
        this.devtodel = fromPref;
        if (fromPref == null) {
            Device device = new Device();
            this.devtodel = device;
            device.setSerial(vmc.getSerial());
            this.devtodel.setLVDV_Type(Constants.DEVICE_TYPE_VMC);
        }
        AnonymousClass22 r8 = new Runnable() {
            public void run() {
                VMCActivity.this.dismissdialog();
                Device.deleteDevFromPref(VMCActivity.this.devtodel, VMCActivity.this.activity);
                if (VMCActivity.vmc == null || !VMCActivity.vmc.getOffline().booleanValue()) {
                    Pico.deletePICOfromPref(VMCActivity.this.devtodel.getSerial(), VMCActivity.this.activity);
                    Device.deleteDevFromPref(VMCActivity.this.devtodel, VMCActivity.this.activity);
                    Device_OP.DeviceOp deviceOp = new Device_OP.DeviceOp();
                    deviceOp.setDeviceID(VMCActivity.this.devtodel.getLVDV_Id());
                    deviceOp.setToken(VMCActivity.this.activity.FirebaseToken);
                    deviceOp.setPlatform(Constants.NOTIFIC_PLAT);
                    new ThreadWebService(VMCActivity.this.activity, 2, 10, VMCActivity.this.getResources().getString(R.string.uriWebService) + VMCActivity.this.getResources().getString(R.string.uri_DeleteDevice), new Gson().toJson((Object) deviceOp), new String[]{VMCActivity.this.devtodel.getSerial()}).start();
                    return;
                }
                VMC.deleteVMCfromPref(VMCActivity.this.devtodel.getSerial(), VMCActivity.this.activity);
                VMCActivity.this.finish();
            }
        };
        openDialogFragment(createYesNoPopUp(getResources().getString(R.string.hd_deleteDEVAlert_title), getResources().getString(R.string.hd_deleteDEVAlert_text), getResources().getString(R.string.no), getResources().getString(R.string.yes), new Runnable() {
            public void run() {
                VMCActivity.this.dismissdialog();
            }
        }, r8));
    }

    public static int getIDP() {
        if (idp > 500) {
            idp = 1;
        }
        int i = idp + 1;
        idp = i;
        return i;
    }

    /* access modifiers changed from: private */
    public boolean checkonlineError(Response response) {
        if (response == null) {
            try {
                VMCActivity vMCActivity = this.activity;
                Functions.makeErrorToast(vMCActivity, vMCActivity.getResources().getString(R.string.resCodeError));
                return false;
            } catch (Exception unused) {
            }
        } else {
            JSONObject jSONObject = new JSONObject(response.getHttpResponcePayload());
            if (jSONObject.has("ResCode")) {
                if (jSONObject.getInt("ResCode") == 0) {
                    return true;
                }
                if (jSONObject.getInt("ResCode") == 2) {
                    this.timerStato.stop();
                    showGetPin();
                } else {
                    VMCActivity vMCActivity2 = this.activity;
                    Functions.makeErrorToast(vMCActivity2, vMCActivity2.getResources().getString(R.string.msg_commandKo));
                }
                return false;
            }
            VMCActivity vMCActivity3 = this.activity;
            Functions.makeErrorToast(vMCActivity3, vMCActivity3.getResources().getString(R.string.msg_commandKo));
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void showGetPin() {
        runOnUiThread(new Runnable() {
            public void run() {
                AnonymousClass1 r0 = new Runnable() {
                    public void run() {
                        VMCActivity.vmc.setPin(VMCActivity.this.txtPin.getText().toString());
                        VMCActivity vMCActivity = VMCActivity.this;
                        vMCActivity.urlStato = VMCActivity.this.getResources().getString(R.string.uriWebService) + VMCActivity.this.getResources().getString(R.string.uri_GetVMCState) + "?vmcSerial=" + VMCActivity.vmc.getSerial() + "&PIN=" + VMCActivity.vmc.getPin();
                        VMC.saveVMCInPref(VMCActivity.vmc, VMCActivity.this.activity);
                        VMCActivity.this.dismissdialog();
                        if (VMCActivity.vmc.getOffline().booleanValue()) {
                            VMCActivity.this.checkPin();
                        } else {
                            VMCActivity.this.timerStato.start();
                        }
                    }
                };
                VMCActivity vMCActivity = VMCActivity.this;
                vMCActivity.bundlePopUp = vMCActivity.createSetPin(r0);
                VMCActivity vMCActivity2 = VMCActivity.this;
                vMCActivity2.openDialogFragment(vMCActivity2.bundlePopUp);
            }
        });
    }

    /* access modifiers changed from: private */
    public void setApConfig() {
        this.timerStato.stop();
        if (vmc.getOffline().booleanValue()) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Intent intent = new Intent(VMCActivity.this.activity, VMCBootloaderActivity.class);
                    intent.setFlags(67108864);
                    intent.putExtra("FROMPICOACT", true);
                    VMCActivity.this.startActivity(intent);
                }
            });
            return;
        }
        AnonymousClass26 r2 = new Runnable() {
            public void run() {
                VMCActivity.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(VMCActivity.this.activity, VMCBootloaderActivity.class);
                        intent.setFlags(67108864);
                        intent.putExtra("FROMPICOACT", true);
                        VMCActivity.this.startActivity(intent);
                    }
                });
            }
        };
        AnonymousClass27 r3 = new Runnable() {
            public void run() {
                VMCActivity.this.hideProgress();
                VMCActivity.this.timerStato.start();
            }
        };
        toConnPwd = "VMC_" + vmc.getSerial();
        toConnSid = "VMC_" + vmc.getSerial();
        connectToWifi(r2, r3, false, false);
    }

    public void impostaDataOraTimeZone(int i, int i2, int i3, int i4) {
        dismissdialog();
        final int i5 = i2;
        final int i6 = i3;
        final int i7 = i;
        final int i8 = i4;
        new Thread(new Runnable() {
            public void run() {
                CmdPICO.UPD_DateTime uPD_DateTime = new CmdPICO.UPD_DateTime();
                uPD_DateTime.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                uPD_DateTime.setTime(String.format("%02d", new Object[]{Integer.valueOf(i5)}) + ":" + String.format("%02d", new Object[]{Integer.valueOf(i6)}) + ":00");
                uPD_DateTime.setWeek(i7);
                if (VMCActivity.vmc.getOffline().booleanValue()) {
                    String sendCMD = UDPSocket.sendCMD(uPD_DateTime);
                    VMCActivity.this.hideProgress();
                    if (VMCActivity.this.checkRespSetMode(sendCMD) && VMCActivity.this.gotofasce) {
                        VMCActivity.this.goToFasce();
                        return;
                    }
                    return;
                }
                VMCActivity.this.sendingCmds = true;
                VMCActivity.this.showProgress();
                try {
                    PICOServerTimezone pICOServerTimezone = new PICOServerTimezone();
                    pICOServerTimezone.setSerial(VMCActivity.vmc.getSerial());
                    pICOServerTimezone.setPin(VMCActivity.vmc.getPin());
                    pICOServerTimezone.setTimezone(Constants.TIMEZONES.get(i8).getIdTimeZone());
                    uPD_DateTime.setFrm("mqtt");
                    uPD_DateTime.setIdp((long) VMCActivity.getIDP());
                    pICOServerTimezone.setCmd(new Gson().toJson((Object) uPD_DateTime));
                    VMCActivity vMCActivity = VMCActivity.this;
                    if (VMCActivity.this.checkonlineError(vMCActivity.makeApiCall(vMCActivity.urlDateTime, new Gson().toJson((Object) pICOServerTimezone), 1, 0, Constants.user, false))) {
                        VMCActivity.vmc.setTimezone(Constants.TIMEZONES.get(i8).getIdTimeZone());
                        if (VMCActivity.this.gotofasce) {
                            VMCActivity.this.goToFasce();
                        }
                    }
                } catch (Exception unused) {
                }
                VMCActivity.this.sendingCmds = false;
                VMCActivity.this.hideProgress();
            }
        }).start();
    }

    /* access modifiers changed from: private */
    public boolean checkRespSetMode(String str) {
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
        VMCActivity vMCActivity = this.activity;
        Functions.makeErrorToast(vMCActivity, vMCActivity.getResources().getString(R.string.msg_commandKo));
        return false;
    }

    /* access modifiers changed from: private */
    public void checkAndGoToFasce() {
        if (vmc.getOffline().booleanValue() || (vmc.getTimezone() != null && !vmc.getTimezone().isEmpty())) {
            goToFasce();
            return;
        }
        this.isFasce = true;
        setDateTime();
        openDialogFragment(this.bundlePopUp);
    }

    /* access modifiers changed from: private */
    public void goToFasce() {
        this.timerStato.stop();
        runOnUiThread(new Runnable() {
            public void run() {
                VMCActivity.this.activity.startActivity(new Intent(VMCActivity.this.activity, VMCCronoSummaryActivity.class));
            }
        });
    }

    /* access modifiers changed from: private */
    public void setCrono(int i) {
        CmdPICO.UpdPicoMCrono updPicoMCrono = new CmdPICO.UpdPicoMCrono();
        updPicoMCrono.setPin(vmc.getPin());
        updPicoMCrono.setM_crono(i);
        updPicoMCrono.setIdp((long) getIDP());
        updPicoMCrono.setCmd("upd_vmc");
        if (((Boolean) sendCmdUpdVMC(updPicoMCrono).first).booleanValue()) {
            vmc.setM_crono(i);
            updateView(false);
        }
    }

    /* access modifiers changed from: private */
    public void writeResetFiltri() {
        new Thread(new Runnable() {
            public void run() {
                VMCActivity.this.showProgress();
                ModBusRecipe.Param param = null;
                int i = 0;
                while (i < VMCActivity.params.size() && param == null) {
                    try {
                        if (VMCActivity.params.get(i).getPRPA_IdParam() == 14) {
                            param = VMCActivity.params.get(i);
                        }
                        i++;
                    } catch (Exception unused) {
                    }
                }
                if (param != null) {
                    CmdPICO.Wr_param fromParam = CmdPICO.Wr_param.fromParam(param, VMCActivity.vmc.getKey_recipe(), 1.0f);
                    fromParam.setPin(VMCActivity.vmc.getPin());
                    Pair access$1000 = VMCActivity.this.sendCmdUpdVMC(fromParam);
                    if (((Boolean) access$1000.first).booleanValue()) {
                        VMCActivity.this.checkRespWriteParam((String) access$1000.second);
                    }
                } else {
                    Functions.makeErrorToast(VMCActivity.this.activity, VMCActivity.this.activity.getResources().getString(R.string.msg_commandKo));
                }
                VMCActivity.this.hideProgress();
            }
        }).start();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't wrap try/catch for region: R(6:11|12|13|14|15|16) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:14:0x0083 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.util.Pair<java.lang.Boolean, java.lang.String> sendCmdUpdVMC(it.tecnosystemi.TS.Commands.CmdPICO r10) {
        /*
            r9 = this;
            r0 = 0
            r9.gotofasce = r0     // Catch:{ Exception -> 0x008e }
            it.tecnosystemi.TS.Model.VMC r1 = vmc     // Catch:{ Exception -> 0x008e }
            java.lang.Boolean r1 = r1.getOffline()     // Catch:{ Exception -> 0x008e }
            boolean r1 = r1.booleanValue()     // Catch:{ Exception -> 0x008e }
            if (r1 == 0) goto L_0x0024
            java.lang.String r10 = it.tecnosystemi.TS.Commands.UDPSocket.sendCMD(r10)     // Catch:{ Exception -> 0x008e }
            r9.hideProgress()     // Catch:{ Exception -> 0x008e }
            android.util.Pair r1 = new android.util.Pair     // Catch:{ Exception -> 0x008e }
            boolean r2 = r9.checkRespSetMode(r10)     // Catch:{ Exception -> 0x008e }
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r2)     // Catch:{ Exception -> 0x008e }
            r1.<init>(r2, r10)     // Catch:{ Exception -> 0x008e }
            return r1
        L_0x0024:
            it.tecnosystemi.TS.Commands.PICOServer r1 = new it.tecnosystemi.TS.Commands.PICOServer     // Catch:{ Exception -> 0x008e }
            r1.<init>()     // Catch:{ Exception -> 0x008e }
            it.tecnosystemi.TS.Model.VMC r2 = vmc     // Catch:{ Exception -> 0x008e }
            java.lang.String r2 = r2.getSerial()     // Catch:{ Exception -> 0x008e }
            r1.setSerial(r2)     // Catch:{ Exception -> 0x008e }
            it.tecnosystemi.TS.Model.VMC r2 = vmc     // Catch:{ Exception -> 0x008e }
            java.lang.String r2 = r2.getPin()     // Catch:{ Exception -> 0x008e }
            r1.setPin(r2)     // Catch:{ Exception -> 0x008e }
            it.tecnosystemi.TS.Model.VMC r2 = vmc     // Catch:{ Exception -> 0x008e }
            java.lang.String r2 = r2.getName()     // Catch:{ Exception -> 0x008e }
            r1.setName(r2)     // Catch:{ Exception -> 0x008e }
            if (r10 == 0) goto L_0x0057
            java.lang.String r2 = "mqtt"
            r10.setFrm(r2)     // Catch:{ Exception -> 0x008e }
            com.google.gson.Gson r2 = new com.google.gson.Gson     // Catch:{ Exception -> 0x008e }
            r2.<init>()     // Catch:{ Exception -> 0x008e }
            java.lang.String r10 = r2.toJson((java.lang.Object) r10)     // Catch:{ Exception -> 0x008e }
            r1.setCmd(r10)     // Catch:{ Exception -> 0x008e }
        L_0x0057:
            java.lang.String r3 = r9.urlUpdate     // Catch:{ Exception -> 0x008e }
            com.google.gson.Gson r10 = new com.google.gson.Gson     // Catch:{ Exception -> 0x008e }
            r10.<init>()     // Catch:{ Exception -> 0x008e }
            java.lang.String r4 = r10.toJson((java.lang.Object) r1)     // Catch:{ Exception -> 0x008e }
            java.lang.String r7 = it.tecnosystemi.TS.Utils.Constants.user     // Catch:{ Exception -> 0x008e }
            r8 = 0
            r5 = 1
            r6 = 0
            r2 = r9
            it.tecnosystemi.TS.Model.Response r10 = r2.makeApiCall(r3, r4, r5, r6, r7, r8)     // Catch:{ Exception -> 0x008e }
            boolean r1 = r9.checkonlineError(r10)     // Catch:{ Exception -> 0x008e }
            if (r1 == 0) goto L_0x008e
            java.lang.String r1 = ""
            org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ Exception -> 0x0083 }
            java.lang.String r10 = r10.getHttpResponcePayload()     // Catch:{ Exception -> 0x0083 }
            r2.<init>(r10)     // Catch:{ Exception -> 0x0083 }
            java.lang.String r10 = "ResDescr"
            java.lang.String r1 = r2.getString(r10)     // Catch:{ Exception -> 0x0083 }
        L_0x0083:
            android.util.Pair r10 = new android.util.Pair     // Catch:{ Exception -> 0x008e }
            r2 = 1
            java.lang.Boolean r2 = java.lang.Boolean.valueOf(r2)     // Catch:{ Exception -> 0x008e }
            r10.<init>(r2, r1)     // Catch:{ Exception -> 0x008e }
            return r10
        L_0x008e:
            android.util.Pair r10 = new android.util.Pair
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r0)
            r1 = 0
            r10.<init>(r0, r1)
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.VMC.VMCActivity.sendCmdUpdVMC(it.tecnosystemi.TS.Commands.CmdPICO):android.util.Pair");
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
        if (vmc.getOffline().booleanValue()) {
            try {
                disconnectFromWIfi();
            } catch (Exception unused) {
            }
        }
    }

    public View getToolBar() {
        return findViewById(R.id.vmc_toolbar);
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        JSONArray jSONArray;
        AnonymousClass31 r5 = new Runnable() {
            public void run() {
                VMCActivity.this.dismissdialog();
                VMCActivity.this.createPopUpRinominaCU();
                VMCActivity vMCActivity = VMCActivity.this;
                vMCActivity.openDialogFragment(vMCActivity.bundlePopUp);
            }
        };
        list.add(createMenuItem(true, getResources().getString(R.string.pm_menuRename), "", (String) null, r5, false, false));
        AnonymousClass32 r6 = new Runnable() {
            public void run() {
                VMCActivity.this.dismissdialog();
                Functions.makeNormalToast(VMCActivity.this.activity, VMCActivity.this.getResources().getString(R.string.cu_setPinInfo));
            }
        };
        list.add(createMenuItem(false, getResources().getString(R.string.pm_menuEditPin), "", (String) null, r6, false, false));
        if (!Constants.ISDEMO) {
            if (!vmc.getOffline().booleanValue()) {
                AnonymousClass33 r62 = new Runnable() {
                    public void run() {
                        try {
                            VMCActivity.this.dismissdialog();
                        } catch (Exception unused) {
                        }
                        VMCActivity.this.gotobooloader = false;
                        VMCActivity vMCActivity = VMCActivity.this.activity;
                        new ThreadDowloadFirmWare(vMCActivity, VMCActivity.this.getResources().getString(R.string.uriWebService) + VMCActivity.this.getResources().getString(R.string.uri_infoFrameWork)).start();
                    }
                };
                list.add(createMenuItem(false, getResources().getString(R.string.ha_menuVerificaFW), "", "", r62, false, false));
            }
            AnonymousClass34 r63 = new Runnable() {
                public void run() {
                    VMCActivity.this.dismissdialog();
                    VMCActivity.this.setApConfig();
                }
            };
            try {
                jSONArray = new JSONArray(PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.PREF_INFOFWS, "[]"));
            } catch (JSONException e) {
                e.printStackTrace();
                jSONArray = null;
            }
            if (jSONArray != null && jSONArray.length() > 0) {
                list.add(createMenuItem(false, getResources().getString(R.string.pm_menuUpdateFW), "", (String) null, r63, false, false));
            }
        }
        AnonymousClass35 r64 = new Runnable() {
            public void run() {
                VMCActivity.this.isFasce = false;
                VMCActivity.this.setDateTime();
                VMCActivity vMCActivity = VMCActivity.this;
                vMCActivity.openDialogFragment(vMCActivity.bundlePopUp);
            }
        };
        list.add(createMenuItem(false, getResources().getString(R.string.pm_menuUpdateDateTime), "", (String) null, r64, false, false));
        AnonymousClass36 r65 = new Runnable() {
            public void run() {
                VMCActivity.this.checkAndGoToFasce();
            }
        };
        list.add(createMenuItem(false, getResources().getString(R.string.pm_menuFasce), "", (String) null, r65, false, false));
        if (vmc.getM_crono() == 1) {
            this.cronoOn = true;
            list.add(createMenuItem(false, getResources().getString(R.string.pm_menuDisabilitaCrono), "", (String) null, new Runnable() {
                public void run() {
                    VMCActivity.this.dismissdialog();
                    VMCActivity.this.showProgress();
                    VMCActivity.this.sendingCmds = true;
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                VMCActivity.this.setCrono(2);
                            } catch (Exception unused) {
                            }
                            VMCActivity.this.hideProgress();
                            VMCActivity.this.sendingCmds = false;
                        }
                    }).start();
                }
            }, false, false));
        } else {
            this.cronoOn = false;
            AnonymousClass38 r66 = new Runnable() {
                public void run() {
                    VMCActivity.this.dismissdialog();
                    VMCActivity.this.sendingCmds = true;
                    VMCActivity.this.showProgress();
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                VMCActivity.this.setCrono(1);
                            } catch (Exception unused) {
                            }
                            VMCActivity.this.hideProgress();
                            VMCActivity.this.sendingCmds = false;
                        }
                    }).start();
                }
            };
            list.add(createMenuItem(false, getResources().getString(R.string.pm_menuAbilitaCrono), "", (String) null, r66, false, false));
        }
        AnonymousClass39 r67 = new Runnable() {
            public void run() {
                VMCActivity.this.dismissdialog();
                if (Constants.ISDEMO) {
                    Functions.makeNormalToast(VMCActivity.this.activity, VMCActivity.this.getResources().getString(R.string.cu_DemoVersion));
                } else {
                    VMCActivity.this.cancellaDevice();
                }
            }
        };
        list.add(createMenuItem(false, getResources().getString(R.string.pm_deleteDevice), "", (String) null, r67, false, false));
        AnonymousClass40 r68 = new Runnable() {
            public void run() {
                VMCActivity.this.dismissdialog();
                VMCActivity.this.goToInstall();
            }
        };
        list.add(createMenuItem(false, getResources().getString(R.string.pm_menuInstaller), "", (String) null, r68, false, false));
        AnonymousClass41 r69 = new Runnable() {
            public void run() {
                VMCActivity.this.dismissdialog();
            }
        };
        VMC vmc2 = vmc;
        if (!(vmc2 == null || vmc2.getSerial() == null)) {
            list.add(createMenuItem(false, getResources().getString(R.string.pm_IDDevice) + vmc.getSerial(), "", (String) null, r69, false, false));
        }
        AnonymousClass42 r610 = new Runnable() {
            public void run() {
                VMCActivity.this.dismissdialog();
            }
        };
        VMC vmc3 = vmc;
        if (!(vmc3 == null || vmc3.getFw_ver() == null)) {
            list.add(createMenuItem(false, getResources().getString(R.string.c2_1_vmc_InfoFWVer) + ": " + vmc.getFw_ver(), "", (String) null, r610, false, false));
        }
        return list;
    }

    public String setToolbarTitle() {
        return vmc.getName();
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
