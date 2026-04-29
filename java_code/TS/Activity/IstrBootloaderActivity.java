package it.tecnosystemi.TS.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import it.tecnosystemi.TS.Commands.MySocketBootLoader;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.util.List;

public class IstrBootloaderActivity extends BaseActivity {
    int mode;

    public BaseActivity getActivity() {
        return this;
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        return list;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_istr_bootloader);
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
                IstrBootloaderActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(IstrBootloaderActivity.this.getResources().getString(R.string.bi_url))));
            }
        });
        Functions.setFonts(findViewById(R.id.ly_istrbl), this);
    }

    public void btnProcedi(View view) {
        AnonymousClass2 r2 = new Runnable() {
            public void run() {
                IstrBootloaderActivity.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        MySocketBootLoader.lastFWPK = 0;
                        Intent intent = new Intent(IstrBootloaderActivity.this.activity, BootloaderActivity.class);
                        intent.putExtra(Constants.INTENT_SETUPMODE, IstrBootloaderActivity.this.mode);
                        IstrBootloaderActivity.this.startActivityForResult(intent, 1233);
                    }
                });
            }
        };
        toConnSid = Constants.WIFI_NAME_BOOT;
        toConnPwd = Constants.WIFI_PWD_BOOT;
        connectToWifi(r2, false, false);
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
        return getResources().getString(R.string.ba_lblTitle);
    }
}
