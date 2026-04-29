package it.tecnosystemi.TS.Activity.TS;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Activity.Config.ChooseTypeVCActivity;
import it.tecnosystemi.TS.Activity.IstrBootloaderActivity;
import it.tecnosystemi.TS.Activity.PICO.Config.ChooseTypePICOActivity;
import it.tecnosystemi.TS.Activity.PICO.IstrBootloaderActivityPICO;
import it.tecnosystemi.TS.Activity.SEIX.Config.ChooseTypeSeiXActivity;
import it.tecnosystemi.TS.Activity.SEIX.IstrBootloaderActivitySeiX;
import it.tecnosystemi.TS.Activity.VMC.Config.ChooseTypeVMCActivity;
import it.tecnosystemi.TS.Activity.VMC.IstrBootloaderActivityVMC;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.util.List;

public class SelectTypeDevActivity extends BaseActivity {
    int TS_TIPO_OP;
    BaseActivity activity;

    public BaseActivity getActivity() {
        return this;
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        return list;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_select_type_dev);
        this.activity = this;
        this.typeActStyle = 1;
        super.onCreate(bundle);
        this.TS_TIPO_OP = getIntent().getIntExtra(Constants.TS_TIPO_OP, -1);
        hideMenuButton();
        setUpGui();
    }

    public void btnProAir(View view) {
        if (this.TS_TIPO_OP == 0) {
            startActivity(new Intent(this, ChooseTypeVCActivity.class));
            return;
        }
        this.activity.startActivityForResult(new Intent(this.activity, IstrBootloaderActivity.class), Constants.RESULT_WIFI);
    }

    /* access modifiers changed from: protected */
    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1) {
            setResult(-1);
            finish();
        }
    }

    public void btnPico(View view) {
        if (this.TS_TIPO_OP == 0) {
            startActivity(new Intent(this, ChooseTypePICOActivity.class));
            return;
        }
        this.activity.startActivityForResult(new Intent(this.activity, IstrBootloaderActivityPICO.class), Constants.RESULT_WIFI);
    }

    public void btnVMC(View view) {
        if (this.TS_TIPO_OP == 0) {
            startActivity(new Intent(this, ChooseTypeVMCActivity.class));
            return;
        }
        this.activity.startActivityForResult(new Intent(this.activity, IstrBootloaderActivityVMC.class), Constants.RESULT_WIFI);
    }

    public void btn6X(View view) {
        if (this.TS_TIPO_OP == 0) {
            startActivity(new Intent(this, ChooseTypeSeiXActivity.class));
            return;
        }
        this.activity.startActivityForResult(new Intent(this.activity, IstrBootloaderActivitySeiX.class), Constants.RESULT_WIFI);
    }

    private void setUpGui() {
        Functions.setFonts(findViewById(R.id.ly_container), this);
        if (this.TS_TIPO_OP == 0) {
            ((TextView) findViewById(R.id.ca_lblDesrc)).setText(R.string.sd_lblSelectDevice);
        }
    }

    public View getToolBar() {
        return findViewById(R.id.ha_toolbar);
    }

    public String setToolbarTitle() {
        return getResources().getString(R.string.sd_lblTitle);
    }
}
