package it.tecnosystemi.TS.Activity.Config;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Activity.BootloaderActivity;
import it.tecnosystemi.TS.Activity.IstrBootloaderActivity;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Threads.ThreadDowloadFirmWare;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.util.List;

public class VerFWActivity extends BaseActivity {
    boolean donebutton = false;
    int mode;

    public BaseActivity getActivity() {
        return this;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_ver_fw);
        super.onCreate(bundle);
        Functions.setFonts(findViewById(R.id.lyverfw), this);
        ((TextView) findViewById(R.id.lblistr)).setMovementMethod(new ScrollingMovementMethod());
        this.mode = getIntent().getIntExtra(Constants.INTENT_SETUPMODE, -1);
        hideMenuButton();
    }

    public void btnVerFw(View view) {
        this.donebutton = true;
        new ThreadDowloadFirmWare(this, getResources().getString(R.string.uriWebService) + getResources().getString(R.string.uri_infoFrameWork)).start();
    }

    public void btnContinueConfig(View view) {
        Intent intent = new Intent(this, EnableWifiVCActivity.class);
        intent.putExtra(Constants.INTENT_SETUPMODE, this.mode);
        startActivity(intent);
    }

    public View getToolBar() {
        return findViewById(R.id.ba_toolbar);
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        if (this.donebutton) {
            this.donebutton = false;
            BootloaderActivity.FROMCONFIG = true;
            Intent intent = new Intent(this, IstrBootloaderActivity.class);
            intent.putExtra(Constants.INTENT_SETUPMODE, this.mode);
            startActivityForResult(intent, Constants.RESULT_WIFI);
        }
        return list;
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1) {
            setResult(-1);
            finish();
        }
    }

    public String setToolbarTitle() {
        return getResources().getString(R.string.c11_title);
    }
}
