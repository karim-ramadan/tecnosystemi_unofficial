package it.tecnosystemi.TS.Activity.PICO.Config;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Activity.PICO.IstrBootloaderActivityPICO;
import it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity;
import it.tecnosystemi.TS.Commands.CmdPICO;
import it.tecnosystemi.TS.Commands.UDPSocket;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.util.List;
import org.json.JSONObject;

public class CheckLedPICOActivity extends BaseActivity {
    Activity activity;
    boolean bootloader;
    TextView ewa_lblDescr2;
    int mode;
    String respInfo;
    Thread threadinfo;

    public BaseActivity getActivity() {
        return this;
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        return list;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_check_led_picoa_ctivity);
        this.typeActStyle = 2;
        this.activity = this;
        super.onCreate(bundle);
        hideMenuButton();
        this.mode = getIntent().getIntExtra(Constants.INTENT_SETUPMODE, -1);
        this.bootloader = getIntent().getBooleanExtra(Constants.INTENT_BOOTLOADER, false);
        this.ewa_lblDescr2 = (TextView) findViewById(R.id.ewa_lblDescr2);
        this.ewa_lblDescr2.setMovementMethod(new ScrollingMovementMethod());
        ((TextView) findViewById(R.id.ewa_lblDescr)).setMovementMethod(new ScrollingMovementMethod());
        Functions.setFonts(findViewById(R.id.lyCheckLed), this);
        getInfo();
    }

    public void getInfo() {
        if (!Constants.ISDEMO) {
            showProgress();
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        CmdPICO cmdPICO = new CmdPICO();
                        cmdPICO.setCmd("pico_info");
                        UDPSocket.startListening();
                        CheckLedPICOActivity.this.respInfo = UDPSocket.sendCMD(cmdPICO, 10000, 10000);
                        UDPSocket.stopListening();
                        int i = 0;
                        while (CheckLedPICOActivity.this.respInfo == null && i < 3) {
                            i++;
                            Thread.sleep(1000);
                            UDPSocket.startListening();
                            CheckLedPICOActivity.this.respInfo = UDPSocket.sendCMD(cmdPICO, 10000, 10000);
                        }
                        UDPSocket.stopListening();
                        CheckLedPICOActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject jSONObject = new JSONObject(CheckLedPICOActivity.this.respInfo);
                                    StringBuilder sb = new StringBuilder();
                                    sb.append(CheckLedPICOActivity.this.getResources().getString(R.string.c2_1_pico_InfoPICO));
                                    sb.append("\n");
                                    sb.append(CheckLedPICOActivity.this.getResources().getString(R.string.c2_1_pico_InfoSerial));
                                    sb.append(": ");
                                    sb.append(jSONObject.get("ser"));
                                    sb.append("\n");
                                    sb.append(CheckLedPICOActivity.this.getResources().getString(R.string.c2_1_pico_InfoStato));
                                    sb.append(": ");
                                    int i = jSONObject.getInt("config_mod");
                                    if (i == 1) {
                                        sb.append(CheckLedPICOActivity.this.getResources().getString(R.string.c2_1_pico_StatoMasterOffline));
                                    } else if (i == 2) {
                                        sb.append(CheckLedPICOActivity.this.getResources().getString(R.string.c2_1_pico_StatoMasterOnline));
                                    } else if (i != 3) {
                                        sb.append(CheckLedPICOActivity.this.getResources().getString(R.string.c2_1_pico_StatoNoConfig));
                                    } else {
                                        sb.append(CheckLedPICOActivity.this.getResources().getString(R.string.c2_1_pico_StatoSlave));
                                        sb.append(":  (");
                                        sb.append(CheckLedPICOActivity.this.getResources().getString(R.string.c2_1_pico_InfoIdSlave));
                                        sb.append(jSONObject.get("id_slave"));
                                        sb.append(")");
                                    }
                                    sb.append("\n");
                                    sb.append(CheckLedPICOActivity.this.getResources().getString(R.string.c2_1_pico_InfoFWVer));
                                    sb.append(": ");
                                    sb.append(jSONObject.get("fw_ver"));
                                    if (jSONObject.getString("fw_ver").length() > 0 && CheckLedPICOActivity.this.mode == 1 && Functions.compareVersions(jSONObject.getString("fw_ver"), "1.0.3") <= 0) {
                                        CheckLedPICOActivity.this.bootloader = true;
                                        sb.append("\n");
                                        sb.append(CheckLedPICOActivity.this.getResources().getString(R.string.fw_too_old));
                                    }
                                    CheckLedPICOActivity.this.ewa_lblDescr2.setText(sb.toString());
                                } catch (Exception unused) {
                                }
                            }
                        });
                    } catch (Exception unused) {
                    }
                    CheckLedPICOActivity.this.hideProgress();
                }
            });
            this.threadinfo = thread;
            thread.start();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        try {
            this.threadinfo.interrupt();
        } catch (Exception unused) {
        }
    }

    public void btnProc() {
        if (this.bootloader) {
            hideProgress();
            startActivity(new Intent(this, PicoBootloaderActivity.class));
            return;
        }
        hideProgress();
        Intent intent = new Intent(this, SetNameAndPinPICOActivity.class);
        intent.putExtra(Constants.INTENT_SETUPMODE, this.mode);
        startActivity(intent);
    }

    public void btnCheckLed(View view) {
        if (Constants.ISDEMO) {
            askLed();
            return;
        }
        showProgress();
        new Thread(new Runnable() {
            public void run() {
                try {
                    CmdPICO.CheckLed checkLed = new CmdPICO.CheckLed();
                    UDPSocket.startListening();
                    String sendCMD = UDPSocket.sendCMD(checkLed, 5000, 1500);
                    UDPSocket.stopListening();
                    if (sendCMD != null) {
                        JSONObject jSONObject = new JSONObject(sendCMD);
                        if (jSONObject.has("res") && jSONObject.getInt("res") == 1) {
                            CheckLedPICOActivity.this.hideProgress();
                            CheckLedPICOActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    CheckLedPICOActivity.this.askLed();
                                }
                            });
                            return;
                        }
                    } else if (!UDPSocket.isConnected()) {
                        Functions.makeErrorToast(CheckLedPICOActivity.this.activity, CheckLedPICOActivity.this.getResources().getString(R.string.c2_1_pico_RetryMessage));
                        CheckLedPICOActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                CheckLedPICOActivity.this.finish();
                            }
                        });
                    }
                } catch (Exception unused) {
                }
                Functions.makeErrorToast(CheckLedPICOActivity.this.activity, CheckLedPICOActivity.this.getResources().getString(R.string.msg_commandKo));
                CheckLedPICOActivity.this.hideProgress();
            }
        }).start();
    }

    /* access modifiers changed from: private */
    public void askLed() {
        AnonymousClass3 r5 = new Runnable() {
            public void run() {
                CheckLedPICOActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        CheckLedPICOActivity.this.dismissdialog();
                        new Thread(new Runnable() {
                            public void run() {
                                CheckLedPICOActivity.this.spegniCOnf();
                                int i = 0;
                                while (i < 5) {
                                    try {
                                        Thread.sleep(1000);
                                        if (!UDPSocket.isConnected()) {
                                            break;
                                        }
                                        i++;
                                    } catch (Exception unused) {
                                    }
                                }
                                CheckLedPICOActivity.this.disconnectFromWIfi();
                                CheckLedPICOActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        CheckLedPICOActivity.this.recoonect();
                                    }
                                });
                            }
                        }).start();
                    }
                });
            }
        };
        AnonymousClass4 r6 = new Runnable() {
            public void run() {
                CheckLedPICOActivity.this.dismissdialog();
                CheckLedPICOActivity.this.btnProc();
            }
        };
        openDialogFragment(createYesNoPopUp(getResources().getString(R.string.c2_1_pico_DialogTitle), getResources().getString(R.string.c2_1_pico_DialogText), getResources().getString(R.string.c2_1_pico_DialogNo), getResources().getString(R.string.c2_1_pico_DialogYes), r5, r6));
    }

    public void spegniCOnf() {
        if (Constants.ISDEMO) {
            tornaIndietro();
            return;
        }
        showProgress();
        try {
            CmdPICO.PicoAP picoAP = new CmdPICO.PicoAP();
            picoAP.setAp_m(5);
            UDPSocket.startListening();
            UDPSocket.sendCMD(picoAP, 5000, 5000);
            UDPSocket.stopListening();
        } catch (Exception unused) {
        }
    }

    public void recoonect() {
        if (!Constants.ISDEMO) {
            showProgress();
            AnonymousClass5 r0 = new Runnable() {
                public void run() {
                    CheckLedPICOActivity.this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            CheckLedPICOActivity.this.getInfo();
                        }
                    });
                }
            };
            AnonymousClass6 r1 = new Runnable() {
                public void run() {
                    CheckLedPICOActivity.this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            CheckLedPICOActivity.this.tornaIndietro();
                        }
                    });
                }
            };
            toConnPwd = "12345678";
            toConnSid = Constants.WIFI_NAME_PICO_CONFIG;
            connectToWifi(r0, r1, false, false);
        }
    }

    /* access modifiers changed from: private */
    public void tornaIndietro() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (CheckLedPICOActivity.this.bootloader) {
                    Functions.makeErrorToast(CheckLedPICOActivity.this.activity, CheckLedPICOActivity.this.getResources().getString(R.string.c2_1_pico_RetryMessage));
                    Intent intent = new Intent(CheckLedPICOActivity.this.activity, IstrBootloaderActivityPICO.class);
                    intent.setFlags(67108864);
                    CheckLedPICOActivity.this.startActivity(intent);
                    return;
                }
                Functions.makeErrorToast(CheckLedPICOActivity.this.activity, CheckLedPICOActivity.this.getResources().getString(R.string.c2_1_pico_RetryMessage));
                Intent intent2 = new Intent(CheckLedPICOActivity.this.activity, EnableWifiVCActivityPICO.class);
                intent2.setFlags(67108864);
                intent2.putExtra(Constants.INTENT_SETUPMODE, CheckLedPICOActivity.this.mode);
                CheckLedPICOActivity.this.startActivity(intent2);
            }
        });
    }

    public View getToolBar() {
        return findViewById(R.id.ewa_toolbar);
    }

    public String setToolbarTitle() {
        return getResources().getString(R.string.c2_1_pico_title);
    }
}
