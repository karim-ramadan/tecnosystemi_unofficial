package it.tecnosystemi.TS.Activity.Config;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.R;
import java.util.List;

public class ConnectToApActivity extends BaseActivity {
    public BaseActivity getActivity() {
        return this;
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        return list;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_connect_to_ap);
        super.onCreate(bundle);
        setUpGui();
        hideMenuButton();
    }

    private void setUpGui() {
        TextView textView = (TextView) findViewById(R.id.caa_lblDescr);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
        ((Button) findViewById(R.id.caa_btnProc)).setTypeface(createFromAsset);
        ((Button) findViewById(R.id.caa_btnImpost)).setTypeface(createFromAsset);
        textView.setTypeface(createFromAsset);
        textView.setMovementMethod(new ScrollingMovementMethod());
    }

    public void btnProc(View view) {
        startActivity(new Intent(this, SetNameAndPinActivity.class));
    }

    public View getToolBar() {
        return findViewById(R.id.caa_toolbar);
    }

    public String setToolbarTitle() {
        return "";
    }
}
