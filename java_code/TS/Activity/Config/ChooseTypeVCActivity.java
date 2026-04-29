package it.tecnosystemi.TS.Activity.Config;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import java.util.List;

public class ChooseTypeVCActivity extends BaseActivity {
    Button btnProc;
    TextView lbldesrc;
    TextView lblmode1;
    TextView lblmode2;
    TextView lblmode3;
    TextView lblmode4;
    RadioButton rbmode1;
    RadioButton rbmode2;
    RadioButton rbmode3;
    RadioButton rbmode4;

    public BaseActivity getActivity() {
        return this;
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        return list;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_choose_type_vc);
        super.onCreate(bundle);
        hideMenuButton();
        setUpGui();
    }

    private void setUpGui() {
        this.rbmode1 = (RadioButton) findViewById(R.id.cta_rbMode1);
        this.rbmode2 = (RadioButton) findViewById(R.id.cta_rbMode2);
        this.rbmode3 = (RadioButton) findViewById(R.id.cta_rbMode3);
        this.rbmode4 = (RadioButton) findViewById(R.id.cta_rbMode4);
        this.lbldesrc = (TextView) findViewById(R.id.cta_lblInstruction);
        this.lblmode1 = (TextView) findViewById(R.id.cta_lblMode1);
        this.lblmode2 = (TextView) findViewById(R.id.cta_lblMode2);
        this.lblmode3 = (TextView) findViewById(R.id.cta_lblMode3);
        this.lblmode4 = (TextView) findViewById(R.id.cta_lblMode4);
        this.btnProc = (Button) findViewById(R.id.cta_btnProcedi);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
        Typeface createFromAsset2 = Typeface.createFromAsset(getAssets(), "fonts/AvenirNextCondensed_Bold.ttf");
        this.rbmode1.setTypeface(createFromAsset2);
        this.rbmode2.setTypeface(createFromAsset2);
        this.rbmode3.setTypeface(createFromAsset2);
        this.rbmode4.setTypeface(createFromAsset2);
        this.lbldesrc.setTypeface(createFromAsset);
        this.lblmode1.setTypeface(createFromAsset);
        this.lblmode2.setTypeface(createFromAsset);
        this.lblmode3.setTypeface(createFromAsset);
        this.lblmode4.setTypeface(createFromAsset);
        this.btnProc.setTypeface(createFromAsset);
    }

    public void btnProc(View view) {
        int i;
        Intent intent;
        if (this.rbmode1.isChecked()) {
            i = 1;
        } else if (this.rbmode2.isChecked()) {
            i = 2;
        } else if (this.rbmode3.isChecked()) {
            i = 3;
        } else if (this.rbmode4.isChecked()) {
            i = 4;
        } else {
            return;
        }
        if (i == 2 || i == 4) {
            intent = new Intent(this, SetNameAndPinActivity.class);
        } else {
            intent = new Intent(this, EnableWifiVCActivity.class);
        }
        intent.putExtra(Constants.INTENT_SETUPMODE, i);
        startActivity(intent);
    }

    public View getToolBar() {
        return findViewById(R.id.cta_toolbar);
    }

    public String setToolbarTitle() {
        return getResources().getString(R.string.c1_title);
    }
}
