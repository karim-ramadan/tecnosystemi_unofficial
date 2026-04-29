package it.tecnosystemi.TS.Activity.PICO.Config;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.util.List;

public class ChooseTypePICOActivity extends BaseActivity {
    RadioButton rbmode1;
    RadioButton rbmode2;
    RadioButton rbmode3;
    RadioButton rbmode4;
    RadioButton rbmode5;

    public BaseActivity getActivity() {
        return this;
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        return list;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_choose_type_pico_activity);
        this.typeActStyle = 2;
        super.onCreate(bundle);
        hideMenuButton();
        setUpGui();
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
        } else if (this.rbmode5.isChecked()) {
            i = 5;
        } else {
            return;
        }
        if (i == 2 || i == 4) {
            intent = new Intent(this, SetNameAndPinPICOActivity.class);
        } else {
            intent = new Intent(this, EnableWifiVCActivityPICO.class);
        }
        intent.putExtra(Constants.INTENT_SETUPMODE, i);
        startActivity(intent);
    }

    private void setUpGui() {
        Functions.setFontsWithIcon(findViewById(R.id.ly_container), this);
        this.rbmode1 = (RadioButton) findViewById(R.id.cta_rbMode1Bold);
        this.rbmode2 = (RadioButton) findViewById(R.id.cta_rbMode2Bold);
        this.rbmode3 = (RadioButton) findViewById(R.id.cta_rbMode3Bold);
        this.rbmode4 = (RadioButton) findViewById(R.id.cta_rbMode4Bold);
        this.rbmode5 = (RadioButton) findViewById(R.id.cta_rbMode5Bold);
    }

    public View getToolBar() {
        return findViewById(R.id.pico_toolbar);
    }

    public String setToolbarTitle() {
        return getResources().getString(R.string.c1_pico_title);
    }
}
