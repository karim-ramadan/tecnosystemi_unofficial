package it.tecnosystemi.TS.Activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import it.tecnosystemi.TS.Model.ChangePwdModel;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Threads.ThreadWebService;
import it.tecnosystemi.TS.Utils.AESAlgorithm;
import it.tecnosystemi.TS.Utils.AESCrypt;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.SavePreferences;
import it.tecnosystemi.TS.Utils.Validation;
import java.util.List;
import java.util.TimeZone;

public class ChangePwdActivity extends BaseActivity {
    EditText confirmPswd;
    String oldPasswd;
    EditText oldPwswd;
    String oldUser;
    EditText pswd;
    TextInputLayout txtlynewpwd1;
    TextInputLayout txtlynewpwd2;
    TextInputLayout txtlyoldpwd;

    public BaseActivity getActivity() {
        return this;
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        return list;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_change_pwd);
        this.typeActStyle = 1;
        super.onCreate(bundle);
        this.oldPasswd = getIntent().getStringExtra("OLD_PWD");
        this.oldUser = getIntent().getStringExtra("OLD_USR");
        this.txtlyoldpwd = (TextInputLayout) findViewById(R.id.cpa_txtlyoldPwd);
        this.txtlynewpwd1 = (TextInputLayout) findViewById(R.id.cpa_txtlyPwd);
        this.txtlynewpwd2 = (TextInputLayout) findViewById(R.id.cpa_txtlyPwd2);
        this.pswd = (EditText) findViewById(R.id.cpa_txtPwd);
        this.confirmPswd = (EditText) findViewById(R.id.cpa_txtPwd2);
        this.pswd.setTypeface(avenir);
        this.confirmPswd.setTypeface(avenir);
        ((Button) findViewById(R.id.cpa_btnSend)).setTypeface(avenir);
        disableInstallerMode();
        this.pswd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View view, boolean z) {
                if (!z) {
                    if (!ChangePwdActivity.this.pswd.getText().toString().equals(ChangePwdActivity.this.pswd.getText().toString())) {
                        ChangePwdActivity.this.pswd.setError(ChangePwdActivity.this.getResources().getString(R.string.sa_errorPwd));
                        ChangePwdActivity.this.txtlynewpwd1.setPasswordVisibilityToggleEnabled(false);
                    }
                }
            }
        });
        this.confirmPswd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View view, boolean z) {
                if (!z) {
                    if (!ChangePwdActivity.this.confirmPswd.getText().toString().equals(ChangePwdActivity.this.confirmPswd.getText().toString())) {
                        ChangePwdActivity.this.confirmPswd.setError(ChangePwdActivity.this.getResources().getString(R.string.sa_errorPwd2));
                        ChangePwdActivity.this.txtlynewpwd2.setPasswordVisibilityToggleEnabled(false);
                    }
                }
            }
        });
        this.pswd.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                ChangePwdActivity.this.pswd.setError((CharSequence) null);
                ChangePwdActivity.this.txtlynewpwd1.setPasswordVisibilityToggleEnabled(true);
            }
        });
        this.confirmPswd.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                ChangePwdActivity.this.confirmPswd.setError((CharSequence) null);
                ChangePwdActivity.this.txtlynewpwd2.setPasswordVisibilityToggleEnabled(true);
            }
        });
        hideMenuButton();
    }

    private void disableInstallerMode() {
        this.txtlyoldpwd.setVisibility(8);
    }

    public void btnChange(View view) {
        sendRequestChangePwd();
    }

    public void sendRequestChangePwd() {
        boolean z;
        String str = this.oldPasswd;
        if (str != null) {
            z = true;
        } else {
            str = this.oldPwswd.getText().toString();
            z = Validation.isValidPassword(str);
        }
        String obj = this.pswd.getText().toString();
        String obj2 = this.confirmPswd.getText().toString();
        boolean isValidPassword = Validation.isValidPassword(obj);
        boolean equals = obj.equals(obj2);
        if (!isValidPassword) {
            this.pswd.setError(getResources().getString(R.string.sa_errorPwd));
        }
        if (!equals) {
            this.pswd.setError(getResources().getString(R.string.sa_errorPwd2));
        }
        if (isValidPassword && z && equals) {
            ChangePwdModel changePwdModel = new ChangePwdModel();
            try {
                AESCrypt aESCrypt = new AESCrypt(Constants.android_id.substring(0, 8) + Constants.SALT);
                changePwdModel.setPwd(aESCrypt.encrypt(obj));
                changePwdModel.setOldPwd(aESCrypt.encrypt(str));
            } catch (Exception e) {
                Log.i("beninca", e.toString());
            }
            changePwdModel.setUser(this.oldUser);
            changePwdModel.setDeviceId(Constants.android_id);
            changePwdModel.setTimezone(TimeZone.getDefault().getID().replace("/", "@"));
            changePwdModel.setTokenPush(this.activity.FirebaseToken);
            changePwdModel.setPlatform(Constants.NOTIFIC_PLAT);
            String json = new Gson().toJson((Object) changePwdModel);
            String[] strArr = {this.oldUser, this.oldPasswd};
            BaseActivity baseActivity = this.activity;
            new ThreadWebService(baseActivity, 1, 2, getResources().getString(R.string.uriWebService) + getResources().getString(R.string.uri_ChangePwd), json, strArr).start();
        }
    }

    public void changePref() {
        SavePreferences savePreferences = new SavePreferences(this.activity, this.activity.getString(R.string.PrefsName));
        if (savePreferences.getBoolean(Constants.PREF_REMEBERME)) {
            savePreferences.save(Constants.PREF_PWD, AESAlgorithm.Encrypt(Constants.android_id, this.pswd.getText().toString()));
        }
    }

    public View getToolBar() {
        return findViewById(R.id.cpa_toolbar);
    }

    public String setToolbarTitle() {
        return getResources().getString(R.string.cpa_lblTitle);
    }
}
