package it.tecnosystemi.TS.Activity.VMC.Config;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Activity.VMC.IstrBootloaderActivityVMC;
import it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity;
import it.tecnosystemi.TS.Commands.CmdPICO;
import it.tecnosystemi.TS.Commands.UDPSocket;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.util.List;
import org.json.JSONObject;

public class CheckLedVMCActivity extends BaseActivity {
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
        setContentView(R.layout.activity_check_led_vmcactivity);
        this.typeActStyle = 3;
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
            startActivity(new Intent(this, VMCBootloaderActivity.class));
            return;
        }
        hideProgress();
        Intent intent = new Intent(this, SetNameAndPinVMCActivity.class);
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
                            CheckLedVMCActivity.this.hideProgress();
                            CheckLedVMCActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    CheckLedVMCActivity.this.askLed();
                                }
                            });
                            return;
                        }
                    } else if (!UDPSocket.isConnected()) {
                        Functions.makeErrorToast(CheckLedVMCActivity.this.activity, CheckLedVMCActivity.this.getResources().getString(R.string.c2_1_vmc_RetryMessage));
                        CheckLedVMCActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                CheckLedVMCActivity.this.finish();
                            }
                        });
                    }
                } catch (Exception unused) {
                }
                Functions.makeErrorToast(CheckLedVMCActivity.this.activity, CheckLedVMCActivity.this.getResources().getString(R.string.msg_commandKo));
                CheckLedVMCActivity.this.hideProgress();
            }
        }).start();
    }

    public void getInfo() {
        if (!Constants.ISDEMO) {
            showProgress();
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        CmdPICO cmdPICO = new CmdPICO();
                        cmdPICO.setCmd("vmc_info");
                        UDPSocket.startListening();
                        CheckLedVMCActivity.this.respInfo = UDPSocket.sendCMD(cmdPICO, 10000, 10000);
                        UDPSocket.stopListening();
                        int i = 0;
                        while (CheckLedVMCActivity.this.respInfo == null && i < 3) {
                            i++;
                            Thread.sleep(1000);
                            UDPSocket.startListening();
                            CheckLedVMCActivity.this.respInfo = UDPSocket.sendCMD(cmdPICO, 10000, 10000);
                        }
                        UDPSocket.stopListening();
                        CheckLedVMCActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    JSONObject jSONObject = new JSONObject(CheckLedVMCActivity.this.respInfo);
                                    CheckLedVMCActivity.this.ewa_lblDescr2.setText(CheckLedVMCActivity.this.getResources().getString(R.string.c2_1_vmc_InfoVMC) + "\n" + CheckLedVMCActivity.this.getResources().getString(R.string.c2_1_vmc_InfoSerial) + ": " + jSONObject.get("ser") + "\n" + CheckLedVMCActivity.this.getResources().getString(R.string.c2_1_vmc_InfoFWVer) + ": " + jSONObject.get("fw_ver"));
                                } catch (Exception unused) {
                                }
                            }
                        });
                    } catch (Exception unused) {
                    }
                    CheckLedVMCActivity.this.hideProgress();
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
                CheckLedVMCActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        CheckLedVMCActivity.this.dismissdialog();
                        new Thread(new Runnable() {
                            public void run() {
                                CheckLedVMCActivity.this.spegniCOnf();
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
                                CheckLedVMCActivity.this.disconnectFromWIfi();
                                CheckLedVMCActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        CheckLedVMCActivity.this.recoonect();
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
                CheckLedVMCActivity.this.dismissdialog();
                CheckLedVMCActivity.this.btnProc();
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
                    CheckLedVMCActivity.this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            CheckLedVMCActivity.this.getInfo();
                        }
                    });
                }
            };
            AnonymousClass6 r1 = new Runnable() {
                public void run() {
                    CheckLedVMCActivity.this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            CheckLedVMCActivity.this.tornaIndietro();
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
                if (CheckLedVMCActivity.this.bootloader) {
                    Functions.makeErrorToast(CheckLedVMCActivity.this.activity, CheckLedVMCActivity.this.getResources().getString(R.string.c2_1_vmc_RetryMessage));
                    Intent intent = new Intent(CheckLedVMCActivity.this.activity, IstrBootloaderActivityVMC.class);
                    intent.setFlags(67108864);
                    CheckLedVMCActivity.this.startActivity(intent);
                    return;
                }
                Functions.makeErrorToast(CheckLedVMCActivity.this.activity, CheckLedVMCActivity.this.getResources().getString(R.string.c2_1_vmc_RetryMessage));
                Intent intent2 = new Intent(CheckLedVMCActivity.this.activity, EnableWifiVCActivityVMC.class);
                intent2.setFlags(67108864);
                intent2.putExtra(Constants.INTENT_SETUPMODE, CheckLedVMCActivity.this.mode);
                CheckLedVMCActivity.this.startActivity(intent2);
            }
        });
    }

    public View getToolBar() {
        return findViewById(R.id.vmc_toolbar);
    }

    public String setToolbarTitle() {
        return getResources().getString(R.string.c2_1_vmc_btnCheckLed);
    }
}
