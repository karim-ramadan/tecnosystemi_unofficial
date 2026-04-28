package it.tecnosystemi.TS.Activity.SEIX.Config;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Activity.SEIX.IstrBootloaderActivitySeiX;
import it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity;
import it.tecnosystemi.TS.Commands.CmdPICO;
import it.tecnosystemi.TS.Commands.UDPSocket;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.util.List;
import org.json.JSONObject;

public class CheckLedSeiXActivity extends BaseActivity {
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
        setContentView(R.layout.activity_check_led_sei_xactivity);
        this.activity = this;
        super.onCreate(bundle);
        hideMenuButton();
        this.mode = getIntent().getIntExtra(Constants.INTENT_SETUPMODE, -1);
        this.bootloader = getIntent().getBooleanExtra(Constants.INTENT_BOOTLOADER, false);
        this.ewa_lblDescr2 = (TextView) findViewById(R.id.ewa_lblDescr2);
        this.ewa_lblDescr2.setMovementMethod(new ScrollingMovementMethod());
        ((TextView) findViewById(R.id.ewa_lblDescr)).setMovementMethod(new ScrollingMovementMethod());
        Functions.setFonts(findViewById(R.id.main), this);
        getInfo();
    }

    public void btnProc() {
        if (this.bootloader) {
            hideProgress();
            startActivity(new Intent(this, SeiXBootloaderActivity.class));
            return;
        }
        hideProgress();
        Intent intent = new Intent(this, SetNameAndPinSeiXActivity.class);
        intent.putExtra(Constants.INTENT_SETUPMODE, this.mode);
        startActivity(intent);
    }

    public void btnCheckLed(View view) {
        askLed();
    }

    public void getInfo() {
        if (!Constants.ISDEMO) {
            showProgress();
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        CmdPICO cmdPICO = new CmdPICO();
                        cmdPICO.setCmd("get_info");
                        UDPSocket.startListening();
                        CheckLedSeiXActivity.this.respInfo = UDPSocket.sendCMD(cmdPICO, 10000, 10000);
                        UDPSocket.stopListening();
                        int i = 0;
                        while (CheckLedSeiXActivity.this.respInfo == null && i < 3) {
                            i++;
                            Thread.sleep(1000);
                            UDPSocket.startListening();
                            CheckLedSeiXActivity.this.respInfo = UDPSocket.sendCMD(cmdPICO, 10000, 10000);
                        }
                        UDPSocket.stopListening();
                        CheckLedSeiXActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject jSONObject = new JSONObject(CheckLedSeiXActivity.this.respInfo);
                                    CheckLedSeiXActivity.this.ewa_lblDescr2.setText(CheckLedSeiXActivity.this.getResources().getString(R.string.c2_1_vmc_InfoVMC) + "\n" + CheckLedSeiXActivity.this.getResources().getString(R.string.c2_1_vmc_InfoSerial) + ": " + jSONObject.get("ser") + "\n" + CheckLedSeiXActivity.this.getResources().getString(R.string.c2_1_vmc_InfoFWVer) + ": " + jSONObject.get("fw_ver"));
                                } catch (Exception unused) {
                                }
                                ((Button) CheckLedSeiXActivity.this.findViewById(R.id.ewa_btnCHeckLed)).setBackground(ContextCompat.getDrawable(CheckLedSeiXActivity.this.activity, R.drawable.btn_selector));
                                CheckLedSeiXActivity.this.findViewById(R.id.ewa_btnCHeckLed).setEnabled(true);
                            }
                        });
                    } catch (Exception unused) {
                    }
                    CheckLedSeiXActivity.this.hideProgress();
                }
            });
            this.threadinfo = thread;
            thread.start();
        }
    }

    /* access modifiers changed from: private */
    public void askLed() {
        AnonymousClass3 r5 = new Runnable() {
            public void run() {
                CheckLedSeiXActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        CheckLedSeiXActivity.this.dismissdialog();
                        new Thread(new Runnable() {
                            public void run() {
                                CheckLedSeiXActivity.this.spegniCOnf();
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
                                CheckLedSeiXActivity.this.disconnectFromWIfi();
                                CheckLedSeiXActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        CheckLedSeiXActivity.this.recoonect();
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
                CheckLedSeiXActivity.this.dismissdialog();
                CheckLedSeiXActivity.this.btnProc();
            }
        };
        openDialogFragment(createYesNoPopUp(getResources().getString(R.string.c2_1_vmc_DialogTitle), getResources().getString(R.string.c2_1_vmc_DialogText), getResources().getString(R.string.c2_1_vmc_DialogNo), getResources().getString(R.string.c2_1_vmc_DialogYes), r5, r6));
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
                    CheckLedSeiXActivity.this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            CheckLedSeiXActivity.this.getInfo();
                        }
                    });
                }
            };
            AnonymousClass6 r1 = new Runnable() {
                public void run() {
                    CheckLedSeiXActivity.this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            CheckLedSeiXActivity.this.tornaIndietro();
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
                if (CheckLedSeiXActivity.this.bootloader) {
                    Functions.makeErrorToast(CheckLedSeiXActivity.this.activity, CheckLedSeiXActivity.this.getResources().getString(R.string.c2_1_vmc_RetryMessage));
                    Intent intent = new Intent(CheckLedSeiXActivity.this.activity, IstrBootloaderActivitySeiX.class);
                    intent.setFlags(67108864);
                    CheckLedSeiXActivity.this.startActivity(intent);
                    return;
                }
                Functions.makeErrorToast(CheckLedSeiXActivity.this.activity, CheckLedSeiXActivity.this.getResources().getString(R.string.c2_1_vmc_RetryMessage));
                Intent intent2 = new Intent(CheckLedSeiXActivity.this.activity, EnableWifiSeiXActivity.class);
                intent2.setFlags(67108864);
                intent2.putExtra(Constants.INTENT_SETUPMODE, CheckLedSeiXActivity.this.mode);
                CheckLedSeiXActivity.this.startActivity(intent2);
            }
        });
    }

    public View getToolBar() {
        return findViewById(R.id.proair_toolbar);
    }

    public String setToolbarTitle() {
        return getResources().getString(R.string.c2_1_vmc_btnCheckLed);
    }
}
