package it.tecnosystemi.TS.Activity.VMC.Config;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.util.List;

public class EnableWifiVCActivityVMC extends BaseActivity {
    BaseActivity activity;
    boolean background;
    boolean errorcollegamento;
    int mode;

    public BaseActivity getActivity() {
        return this;
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        return list;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_enable_wifi_vcvmc);
        this.activity = this;
        this.typeActStyle = 3;
        super.onCreate(bundle);
        this.mode = getIntent().getIntExtra(Constants.INTENT_SETUPMODE, -1);
        ((TextView) findViewById(R.id.ewa_lblDescr)).setMovementMethod(new ScrollingMovementMethod());
        if (this.mode == -1) {
            finish();
        }
        hideMenuButton();
        setUpGui();
        Functions.setFonts(findViewById(R.id.main), this);
    }

    private void setUpGui() {
        ((TextView) findViewById(R.id.bi_linkFAQ2)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                EnableWifiVCActivityVMC.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(EnableWifiVCActivityVMC.this.getResources().getString(R.string.bi_url))));
            }
        });
    }

    public void btnProc(View view) {
        if (Constants.ISDEMO) {
            gotoCheckLed();
            return;
        }
        showProgress();
        AnonymousClass2 r3 = new Runnable() {
            public void run() {
                EnableWifiVCActivityVMC.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        EnableWifiVCActivityVMC.this.gotoCheckLed();
                    }
                });
            }
        };
        toConnPwd = "12345678";
        toConnSid = Constants.WIFI_NAME_VMC_CONFIG;
        connectToWifi(r3, new Runnable() {
            public void run() {
                EnableWifiVCActivityVMC.this.runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            String str = ((EnableWifiVCActivityVMC.this.getResources().getString(R.string.ba_apAssente) + "\n" + EnableWifiVCActivityVMC.this.getResources().getString(R.string.connectToPolaris)) + "\nSSID: " + BaseActivity.toConnSid) + "\n" + EnableWifiVCActivityVMC.this.getResources().getString(R.string.c4_PwdHint) + ": " + BaseActivity.toConnPwd;
                            AlertDialog.Builder builder = new AlertDialog.Builder(EnableWifiVCActivityVMC.this.activity);
                            builder.setMessage(str).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    try {
                                        Intent intent = new Intent("android.settings.WIFI_SETTINGS");
                                        intent.addFlags(268435456);
                                        EnableWifiVCActivityVMC.this.activity.startActivity(intent);
                                    } catch (Exception unused) {
                                    }
                                }
                            });
                            AlertDialog create = builder.create();
                            create.show();
                            create.getButton(-1).setTextColor(EnableWifiVCActivityVMC.this.getResources().getColor(R.color.vmc_main));
                        } catch (Exception unused) {
                        }
                    }
                });
            }
        }, false, false);
    }

    public void gotoCheckLed() {
        hideProgress();
        Intent intent = new Intent(this, CheckLedVMCActivity.class);
        intent.addFlags(67108864);
        intent.putExtra(Constants.INTENT_SETUPMODE, this.mode);
        startActivity(intent);
    }

    public void onResume() {
        super.onResume();
        this.background = false;
        if (this.errorcollegamento) {
            this.errorcollegamento = false;
            Functions.makeErrorToast(this, getResources().getString(R.string.ba_apAssente));
        }
    }

    public void onPause() {
        super.onPause();
        this.background = true;
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        if (this.cercacentralina != null) {
            this.cercacentralina.interrupt();
        }
        super.onDestroy();
    }

    public View getToolBar() {
        return findViewById(R.id.vmc_toolbar);
    }

    public String setToolbarTitle() {
        return getResources().getString(R.string.c2_vmc_title);
    }
}
