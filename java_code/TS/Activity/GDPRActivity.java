package it.tecnosystemi.TS.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import it.tecnosystemi.TS.Fragment.InstructionDialogFragment;
import it.tecnosystemi.TS.Model.Response;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Threads.ThreadWebService;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.util.List;
import org.json.JSONObject;

public class GDPRActivity extends BaseActivity {
    GDPRActivity activity;
    CheckBox cbMarket;
    CheckBox cbPrivacy;
    CheckBox cbTof;
    boolean fromlogin;
    TextView lblAccpolicy;
    TextView lblAcctof;
    TextView lblMarket;
    TextView lblprivacy;
    TextView lbltof;
    String usrText;

    public BaseActivity getActivity() {
        return this;
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        return list;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_gdpractivity);
        this.activity = this;
        super.onCreate(bundle);
        Functions.setFonts(findViewById(R.id.ly_Gdpr), this);
        Intent intent = getIntent();
        boolean booleanExtra = intent.getBooleanExtra(Constants.GDPRFROMLOGIN, false);
        this.fromlogin = booleanExtra;
        if (booleanExtra) {
            try {
                this.usrText = intent.getStringExtra(Constants.GDprUSERNAME);
            } catch (Exception unused) {
            }
        }
        init();
        if (intent.getBooleanExtra(Constants.GDPRFROMHOME, false)) {
            this.cbTof.setVisibility(8);
            this.cbPrivacy.setVisibility(8);
            this.lblAcctof.setVisibility(8);
            this.lblAccpolicy.setVisibility(8);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) this.lblprivacy.getLayoutParams();
            layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin * 3, layoutParams.rightMargin, layoutParams.bottomMargin);
            this.lblprivacy.setLayoutParams(layoutParams);
            ConstraintLayout.LayoutParams layoutParams2 = (ConstraintLayout.LayoutParams) this.lblMarket.getLayoutParams();
            layoutParams2.setMargins(layoutParams2.leftMargin, layoutParams2.topMargin * 3, layoutParams2.rightMargin, layoutParams2.bottomMargin);
            this.lblMarket.setLayoutParams(layoutParams2);
        }
        hideMenuButton();
    }

    private void init() {
        this.lbltof = (TextView) findViewById(R.id.lbl_BoldTOF);
        this.lblAcctof = (TextView) findViewById(R.id.lblTof);
        this.cbTof = (CheckBox) findViewById(R.id.chB_TermService);
        this.lblprivacy = (TextView) findViewById(R.id.lblBoldPrivacy);
        this.lblAccpolicy = (TextView) findViewById(R.id.lblPrivacy);
        this.cbPrivacy = (CheckBox) findViewById(R.id.chB_Privacy);
        this.lblMarket = (TextView) findViewById(R.id.lblMarket);
        this.cbMarket = (CheckBox) findViewById(R.id.chB_Maket);
        this.lbltof.setTextColor(getResources().getColorStateList(R.color.hover_from_white));
        this.lblprivacy.setTextColor(getResources().getColorStateList(R.color.hover_from_white));
        this.cbTof.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                SignUpActivity.TOU = GDPRActivity.this.cbTof.isChecked();
                if (SignUpActivity.TOU) {
                    GDPRActivity.this.lblAcctof.setError((CharSequence) null);
                }
            }
        });
        this.cbPrivacy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                SignUpActivity.PRIVACY = GDPRActivity.this.cbPrivacy.isChecked();
                if (SignUpActivity.PRIVACY) {
                    GDPRActivity.this.lblAccpolicy.setError((CharSequence) null);
                }
            }
        });
        this.cbMarket.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                SignUpActivity.MARKETING = GDPRActivity.this.cbMarket.isChecked();
            }
        });
        this.lblAcctof.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                GDPRActivity gDPRActivity = GDPRActivity.this;
                gDPRActivity.changeCB(gDPRActivity.cbTof);
            }
        });
        this.lblAccpolicy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                GDPRActivity gDPRActivity = GDPRActivity.this;
                gDPRActivity.changeCB(gDPRActivity.cbPrivacy);
            }
        });
        this.lblMarket.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                GDPRActivity gDPRActivity = GDPRActivity.this;
                gDPRActivity.changeCB(gDPRActivity.cbMarket);
            }
        });
        this.lbltof.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                InstructionDialogFragment.newInstance(GDPRActivity.this.getResources().getString(R.string.sa_popUp_termOfUse), GDPRActivity.this.activity, GDPRActivity.this.getResources().getString(R.string.sa_HTML_TermOfUseTS), 0).show(GDPRActivity.this.getSupportFragmentManager(), "fragment_instructions");
            }
        });
        this.lblprivacy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                InstructionDialogFragment.newInstance(GDPRActivity.this.getResources().getString(R.string.title_popUp_privacy), GDPRActivity.this.activity, GDPRActivity.this.getResources().getString(R.string.sa_HTML_PrivacyTS), 0).show(GDPRActivity.this.getSupportFragmentManager(), "fragment_instructions");
            }
        });
        this.cbPrivacy.setChecked(SignUpActivity.PRIVACY);
        this.cbTof.setChecked(SignUpActivity.TOU);
        this.cbMarket.setChecked(SignUpActivity.MARKETING);
    }

    public void btnOk(View view) {
        boolean z;
        boolean z2 = false;
        if (!SignUpActivity.PRIVACY) {
            this.lblAccpolicy.setError(getResources().getString(R.string.sa_errorPrivacy));
            z = false;
        } else {
            z = true;
        }
        if (!SignUpActivity.TOU) {
            this.lblAcctof.setError(getResources().getString(R.string.sa_errorTermOfUse));
        } else {
            z2 = z;
        }
        if (!z2) {
            return;
        }
        if (this.fromlogin) {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put("Username", this.usrText);
            } catch (Exception unused) {
            }
            try {
                jSONObject.put("Ok_TOS", this.cbTof.isChecked());
            } catch (Exception unused2) {
            }
            try {
                jSONObject.put("Ok_Privacy", this.cbPrivacy.isChecked());
            } catch (Exception unused3) {
            }
            try {
                jSONObject.put("Ok_Marketing", this.cbMarket.isChecked());
            } catch (Exception unused4) {
            }
            GDPRActivity gDPRActivity = this.activity;
            new ThreadWebService(gDPRActivity, 1, 32, getResources().getString(R.string.uriWebService) + getResources().getString(R.string.uri_TermOfUse), jSONObject.toString(), (String[]) null).start();
            return;
        }
        finish();
    }

    public void ResSave(Response response) {
        if (response != null && response.getHttpResponceCode() == 200) {
            try {
                JSONObject jSONObject = new JSONObject(response.getHttpResponcePayload());
                if (jSONObject.has("ResCode") && jSONObject.getInt("ResCode") == 0) {
                    finish();
                    return;
                }
            } catch (Exception unused) {
            }
        }
        Functions.makeErrorToast(this.activity, getResources().getString(R.string.msg_commandKo));
    }

    /* access modifiers changed from: private */
    public void changeCB(final CheckBox checkBox) {
        runOnUiThread(new Runnable() {
            public void run() {
                CheckBox checkBox = checkBox;
                checkBox.setChecked(!checkBox.isChecked());
            }
        });
    }

    public View getToolBar() {
        return findViewById(R.id.sua_toolbar);
    }

    public String setToolbarTitle() {
        return getResources().getString(R.string.GDPR_Title);
    }
}
