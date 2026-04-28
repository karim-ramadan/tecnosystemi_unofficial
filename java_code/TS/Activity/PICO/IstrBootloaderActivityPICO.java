package it.tecnosystemi.TS.Activity.PICO;

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
import it.tecnosystemi.TS.Activity.PICO.Config.CheckLedPICOActivity;
import it.tecnosystemi.TS.Commands.MySocketBootLoader;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.util.List;

public class IstrBootloaderActivityPICO extends BaseActivity {
    IstrBootloaderActivityPICO activity;
    int mode;

    public BaseActivity getActivity() {
        return this;
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        return list;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_istr_bootloader_pico);
        this.activity = this;
        this.typeActStyle = 2;
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
    }

    public void onResume() {
        super.onResume();
        MySocketBootLoader.lastFWPK = 0;
    }

    public void setupgui() {
        ((TextView) findViewById(R.id.bi_istruzioni)).setMovementMethod(new ScrollingMovementMethod());
        ((TextView) findViewById(R.id.bi_linkFAQ)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                IstrBootloaderActivityPICO.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(IstrBootloaderActivityPICO.this.getResources().getString(R.string.bi_url))));
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
        toConnSid = Constants.WIFI_NAME_PICO_CONFIG;
        connectToWifi(new Runnable() {
            public void run() {
                IstrBootloaderActivityPICO.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        IstrBootloaderActivityPICO.this.gotoCheckLed();
                    }
                });
            }
        }, new Runnable() {
            public void run() {
                IstrBootloaderActivityPICO.this.runOnUiThread(new Runnable() {
                    public void run() {
                        String str = ((IstrBootloaderActivityPICO.this.getResources().getString(R.string.ba_apAssente) + "\n" + IstrBootloaderActivityPICO.this.getResources().getString(R.string.connectToPolaris)) + "\nSSID: " + BaseActivity.toConnSid) + "\n" + IstrBootloaderActivityPICO.this.getResources().getString(R.string.c4_PwdHint) + ": " + BaseActivity.toConnPwd;
                        AlertDialog.Builder builder = new AlertDialog.Builder(IstrBootloaderActivityPICO.this.activity);
                        builder.setMessage(str).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                try {
                                    Intent intent = new Intent("android.settings.WIFI_SETTINGS");
                                    intent.addFlags(268435456);
                                    IstrBootloaderActivityPICO.this.activity.startActivity(intent);
                                } catch (Exception unused) {
                                }
                            }
                        });
                        AlertDialog create = builder.create();
                        create.show();
                        create.getButton(-1).setTextColor(IstrBootloaderActivityPICO.this.getResources().getColor(R.color.picoBlueColor));
                    }
                });
            }
        }, false, false);
    }

    public void gotoCheckLed() {
        runOnUiThread(new Runnable() {
            public void run() {
                Intent intent = new Intent(IstrBootloaderActivityPICO.this.activity, CheckLedPICOActivity.class);
                intent.putExtra(Constants.INTENT_BOOTLOADER, true);
                IstrBootloaderActivityPICO.this.startActivity(intent);
            }
        });
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1) {
            setResult(-1);
            finish();
        }
    }

    public View getToolBar() {
        return findViewById(R.id.ba_toolbar);
    }

    public String setToolbarTitle() {
        return getResources().getString(R.string.c2_pico_title);
    }
}
