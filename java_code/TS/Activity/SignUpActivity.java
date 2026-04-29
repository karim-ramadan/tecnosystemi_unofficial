package it.tecnosystemi.TS.Activity;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import it.tecnosystemi.TS.Model.User;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Threads.ThreadWebService;
import it.tecnosystemi.TS.Utils.AESCrypt;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import it.tecnosystemi.TS.Utils.Validation;
import java.util.List;
import java.util.Locale;

public class SignUpActivity extends BaseActivity {
    public static boolean MARKETING;
    public static boolean PRIVACY;
    public static boolean TOU;
    Button btnRegistrati;
    CheckBox checkTerms;
    public boolean emailExits = false;
    TextView lblTermOfuse;
    Resources res;
    EditText txtemail;
    TextInputLayout txtlypwd;
    TextInputLayout txtlypwd2;
    EditText txtname;
    EditText txtpwd;
    EditText txtpwd2;

    public BaseActivity getActivity() {
        return this;
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        return list;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        Log.d(this.TAG, "oncreate");
        setContentView(R.layout.activity_sign_up);
        this.typeActStyle = 1;
        super.onCreate(bundle);
        setUpGui();
        hideMenuButton();
        this.res = getResources();
        Log.d(this.TAG, "fine oncreate");
        MARKETING = false;
        TOU = false;
        PRIVACY = false;
    }

    private void setUpGui() {
        Log.d(this.TAG, "setupgui");
        this.txtname = (EditText) findViewById(R.id.sa_txtNome);
        this.txtemail = (EditText) findViewById(R.id.sa_txtUsername);
        this.txtpwd = (EditText) findViewById(R.id.sa_txtPwd);
        this.txtpwd2 = (EditText) findViewById(R.id.sa_txtPwd2);
        this.checkTerms = (CheckBox) findViewById(R.id.sa_cbtermOfUse);
        this.lblTermOfuse = (TextView) findViewById(R.id.sa_lbltermOfUse);
        this.txtlypwd = (TextInputLayout) findViewById(R.id.la_txtlyPwd);
        this.txtlypwd2 = (TextInputLayout) findViewById(R.id.sa_txtlyPwd2);
        this.btnRegistrati = (Button) findViewById(R.id.sa_btnRegistrati);
        TextView textView = this.lblTermOfuse;
        textView.setPaintFlags(textView.getPaintFlags() | 8);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
        this.txtname.setTypeface(createFromAsset);
        this.txtemail.setTypeface(createFromAsset);
        this.txtpwd.setTypeface(createFromAsset);
        this.txtpwd2.setTypeface(createFromAsset);
        this.checkTerms.setTypeface(createFromAsset);
        this.lblTermOfuse.setTypeface(createFromAsset);
        this.txtlypwd.setTypeface(createFromAsset);
        this.txtlypwd2.setTypeface(createFromAsset);
        this.btnRegistrati.setTypeface(createFromAsset);
        this.lblTermOfuse.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                SignUpActivity.this.startActivity(new Intent(SignUpActivity.this.activity, GDPRActivity.class));
            }
        });
        this.txtemail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View view, boolean z) {
                if (!z) {
                    String obj = SignUpActivity.this.txtemail.getText().toString();
                    if (!Validation.isValidEmail(obj)) {
                        SignUpActivity.this.txtemail.setError(SignUpActivity.this.res.getString(R.string.sa_errorEmail));
                        return;
                    }
                    BaseActivity baseActivity = SignUpActivity.this.activity;
                    new ThreadWebService(baseActivity, 0, 1, SignUpActivity.this.res.getString(R.string.uriWebService) + SignUpActivity.this.res.getString(R.string.uri_CheckEmail) + "?email=" + obj, (String) null, (String[]) null).start();
                }
            }
        });
        this.txtpwd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View view, boolean z) {
                if (z) {
                    return;
                }
                if (!Validation.isValidPassword(SignUpActivity.this.txtpwd.getText().toString())) {
                    String string = SignUpActivity.this.res.getString(R.string.sa_errorPwd);
                    SignUpActivity.this.txtlypwd.setPasswordVisibilityToggleEnabled(false);
                    SignUpActivity.this.txtpwd.setError(string);
                    return;
                }
                SignUpActivity.this.txtpwd.setError((CharSequence) null);
            }
        });
        this.txtpwd.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                SignUpActivity.this.txtpwd.setError((CharSequence) null);
                SignUpActivity.this.txtlypwd.setPasswordVisibilityToggleEnabled(true);
            }
        });
        Log.d(this.TAG, "fine setupgui");
    }

    public void showError() {
        this.txtemail.setError(getResources().getString(R.string.sa_emailFounded));
    }

    public void btnRegistrati(View view) {
        registrati();
    }

    public void registrati() {
        String obj = this.txtname.getText().toString();
        String obj2 = this.txtpwd.getText().toString();
        String obj3 = this.txtpwd2.getText().toString();
        String obj4 = this.txtemail.getText().toString();
        boolean isValidPassword = Validation.isValidPassword(obj2);
        boolean isValidEmail = Validation.isValidEmail(obj4);
        boolean equals = obj2.equals(obj3);
        boolean z = TOU && PRIVACY;
        if (!isValidPassword || !isValidEmail || !equals || !z || this.emailExits) {
            if (!isValidPassword) {
                this.txtpwd.setError(this.res.getString(R.string.sa_errorPwd));
                this.txtlypwd.setPasswordVisibilityToggleEnabled(false);
            } else if (!equals) {
                this.txtpwd2.setError(this.res.getString(R.string.sa_errorPwd));
                this.txtlypwd2.setPasswordVisibilityToggleEnabled(false);
            }
            if (!isValidEmail) {
                Functions.ShowerrorOnView(this, this.txtemail, this.res.getString(R.string.sa_errorEmail));
            }
            if (!z) {
                this.lblTermOfuse.setError(this.res.getString(R.string.sa_errorTermOfUse));
            } else {
                this.lblTermOfuse.setError((CharSequence) null);
            }
        } else {
            User user = new User();
            try {
                user.setPassword(new AESCrypt(Constants.android_id.substring(0, 8) + Constants.SALT).encrypt(obj2));
            } catch (Exception e) {
                Log.i("beninca", e.toString());
            }
            user.setId(0);
            user.setName(obj);
            user.setUsername(obj4);
            user.setDeviceId(Constants.android_id);
            String lowerCase = Locale.getDefault().getLanguage().toLowerCase();
            if (lowerCase.compareTo("nl") == 0 && Locale.getDefault().getCountry().toLowerCase().compareTo("be") == 0) {
                lowerCase = "nl-BE";
            }
            user.setLang(lowerCase);
            user.setOk_Marketing(MARKETING);
            user.setOk_TOS(TOU);
            user.setOk_Privacy(PRIVACY);
            String json = new Gson().toJson((Object) user);
            new ThreadWebService(this, 1, 0, this.res.getString(R.string.uriWebService) + this.res.getString(R.string.uri_RegisterUser), json, (String[]) null).start();
        }
    }

    public void onResume() {
        super.onResume();
        if (TOU && PRIVACY) {
            this.lblTermOfuse.setError((CharSequence) null);
        }
    }

    public View getToolBar() {
        return findViewById(R.id.sua_toolbar);
    }

    public String setToolbarTitle() {
        return getResources().getString(R.string.sa_lblTitle);
    }
}
