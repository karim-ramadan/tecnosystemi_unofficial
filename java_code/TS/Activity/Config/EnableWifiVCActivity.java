package it.tecnosystemi.TS.Activity.Config;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import it.tecnosystemi.TS.Utils.Functions$$ExternalSyntheticApiModelOutline0;
import java.util.List;

public class EnableWifiVCActivity extends BaseActivity {
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
        setContentView(R.layout.activity_enable_wifi_vc);
        this.activity = this;
        this.errorcollegamento = false;
        this.mode = getIntent().getIntExtra(Constants.INTENT_SETUPMODE, -1);
        ((TextView) findViewById(R.id.ewa_lblDescr)).setMovementMethod(new ScrollingMovementMethod());
        if (this.mode == -1) {
            finish();
        }
        super.onCreate(bundle);
        hideMenuButton();
        setUpGui();
    }

    private void setUpGui() {
        ((TextView) findViewById(R.id.bi_linkFAQ2)).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                EnableWifiVCActivity.this.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(EnableWifiVCActivity.this.getResources().getString(R.string.bi_url))));
            }
        });
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
        ((TextView) findViewById(R.id.bi_linkFAQ2)).setTypeface(createFromAsset);
        ((TextView) findViewById(R.id.ewa_lblDescr)).setTypeface(createFromAsset);
        ((Button) findViewById(R.id.ewa_btnProc)).setTypeface(createFromAsset);
    }

    public void btnProc(View view) {
        if (Constants.ISDEMO) {
            Intent intent = new Intent(this, SetNameAndPinActivity.class);
            intent.putExtra(Constants.INTENT_SETUPMODE, this.mode);
            startActivity(intent);
            return;
        }
        AnonymousClass2 r5 = new Runnable() {
            public void run() {
                EnableWifiVCActivity.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        EnableWifiVCActivity.this.gosetnamepin();
                    }
                });
            }
        };
        toConnSid = Constants.WIFI_NAME_CONFIG;
        toConnPwd = Constants.WIFI_PWD_CONFIG;
        try {
            if (Build.VERSION.SDK_INT >= 21) {
                ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService("connectivity");
                connectivityManager.unregisterNetworkCallback(networkCallback);
                if (Build.VERSION.SDK_INT >= 23) {
                    Functions$$ExternalSyntheticApiModelOutline0.m(connectivityManager, (Network) null);
                } else {
                    boolean unused = ConnectivityManager.setProcessDefaultNetwork((Network) null);
                }
            }
        } catch (Exception e) {
            Log.d("TAG", e.toString());
        }
        connectToWifi(r5, false, false);
    }

    public void gosetnamepin() {
        hideProgress();
        Intent intent = new Intent(this, SetNameAndPinActivity.class);
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
        return findViewById(R.id.ewa_toolbar);
    }

    public String setToolbarTitle() {
        return getResources().getString(R.string.c2_title);
    }
}
