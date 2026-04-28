package it.tecnosystemi.TS.Activity.VMC.Config;

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

public class ChooseTypeVMCActivity extends BaseActivity {
    ChooseTypeVMCActivity activity;
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
        setContentView(R.layout.activity_choose_type_vmcactivity);
        this.activity = this;
        this.typeActStyle = 3;
        super.onCreate(bundle);
        hideMenuButton();
        setUpGui();
        Functions.setFonts(findViewById(R.id.main), this);
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
            intent = new Intent(this, SetNameAndPinVMCActivity.class);
        } else {
            intent = new Intent(this, EnableWifiVCActivityVMC.class);
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
    }

    public View getToolBar() {
        return findViewById(R.id.vmc_toolbar);
    }

    public String setToolbarTitle() {
        return getResources().getString(R.string.c1_vmc_title);
    }
}
