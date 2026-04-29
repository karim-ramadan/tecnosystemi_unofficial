package it.tecnosystemi.TS.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.common.ConnectionResult;
import it.tecnosystemi.TS.R;

public class SplashActivity extends AppCompatActivity {
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);
        scheduleSplashScreen();
    }

    private void scheduleSplashScreen() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                SplashActivity.this.gotoActivity();
            }
        }, (long) ConnectionResult.DRIVE_EXTERNAL_STORAGE_REQUIRED);
    }

    /* access modifiers changed from: private */
    public void gotoActivity() {
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(17432576, 17432577);
        finish();
    }
}
