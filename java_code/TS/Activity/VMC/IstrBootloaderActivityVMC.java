package it.tecnosystemi.TS.Activity.VMC;

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
import it.tecnosystemi.TS.Activity.BootloaderActivity;
import it.tecnosystemi.TS.Activity.VMC.Config.CheckLedVMCActivity;
import it.tecnosystemi.TS.Commands.MySocketBootLoader;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.util.List;

public class IstrBootloaderActivityVMC extends BaseActivity {
    IstrBootloaderActivityVMC activity;
    int mode;

    public BaseActivity getActivity() {
        return this;
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        return list;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_istr_bootloader_vmc);
        this.activity = this;
        this.typeActStyle = 3;
        super.onCreate(bundle);
        setupgui();
        hideMenuButton();
        MySocketBootLoader.lastFWPK = 0;
        try {
            if (BootloaderActivity.FROMCONFIG) {
                this.mode = getIntent().getIntExtra(Constants.INTENT_SETUPMODE, -1);
            }
        } catch (Exception unused) {
        }
        this.runnable = null;
        Functions.setFonts(findViewById(R.id.main), this);
    }

    public void setupgui() {
        ((TextView) findViewById(R.id.bi_istruzioni)).setMovementMethod(new ScrollingMovementMethod());
        ((TextView) findViewById(R.id.bi_linkFAQ)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                IstrBootloaderActivityVMC.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(IstrBootloaderActivityVMC.this.getResources().getString(R.string.bi_url))));
            }
        });
        Functions.setFonts(findViewById(R.id.ly_istrbl), this);
    }

    public void btnProcedi(View view) {
        if (Constants.ISDEMO) {
            gotoCheckLed();
            return;
        }
        showProgress();
        toConnPwd = "12345678";
        toConnSid = Constants.WIFI_NAME_VMC_CONFIG;
        connectToWifi(new Runnable() {
            public void run() {
                IstrBootloaderActivityVMC.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        IstrBootloaderActivityVMC.this.gotoCheckLed();
                    }
                });
            }
        }, new Runnable() {
            public void run() {
                IstrBootloaderActivityVMC.this.runOnUiThread(new Runnable() {
                    public void run() {
                        String str = ((IstrBootloaderActivityVMC.this.getResources().getString(R.string.ba_apAssente) + "\n" + IstrBootloaderActivityVMC.this.getResources().getString(R.string.connectToPolaris)) + "\nSSID: " + BaseActivity.toConnSid) + "\n" + IstrBootloaderActivityVMC.this.getResources().getString(R.string.c4_PwdHint) + ": " + BaseActivity.toConnPwd;
                        AlertDialog.Builder builder = new AlertDialog.Builder(IstrBootloaderActivityVMC.this.activity);
                        builder.setMessage(str).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                try {
                                    Intent intent = new Intent("android.settings.WIFI_SETTINGS");
                                    intent.addFlags(268435456);
                                    IstrBootloaderActivityVMC.this.activity.startActivity(intent);
                                } catch (Exception unused) {
                                }
                            }
                        });
                        AlertDialog create = builder.create();
                        create.show();
                        create.getButton(-1).setTextColor(IstrBootloaderActivityVMC.this.getResources().getColor(R.color.vmc_main));
                    }
                });
            }
        }, false, false);
    }

    public void gotoCheckLed() {
        runOnUiThread(new Runnable() {
            public void run() {
                Intent intent = new Intent(IstrBootloaderActivityVMC.this.activity, CheckLedVMCActivity.class);
                intent.putExtra(Constants.INTENT_BOOTLOADER, true);
                IstrBootloaderActivityVMC.this.startActivity(intent);
            }
        });
    }

    public void onResume() {
        super.onResume();
        MySocketBootLoader.lastFWPK = 0;
    }

    public View getToolBar() {
        return findViewById(R.id.vmc_toolbar);
    }

    public String setToolbarTitle() {
        return getResources().getString(R.string.bi_lblTitleVMC);
    }
}
