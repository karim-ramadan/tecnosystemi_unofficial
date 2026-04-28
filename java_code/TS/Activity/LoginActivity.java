package it.tecnosystemi.TS.Activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat$$ExternalSyntheticApiModelOutline0;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import it.tecnosystemi.TS.Activity.TS.TSHomeActivity;
import it.tecnosystemi.TS.Model.LoginModel;
import it.tecnosystemi.TS.Model.MenuList;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Threads.ThreadWebService;
import it.tecnosystemi.TS.Utils.AESAlgorithm;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.DataClass;
import it.tecnosystemi.TS.Utils.Functions;
import it.tecnosystemi.TS.Utils.Functions$$ExternalSyntheticApiModelOutline0;
import it.tecnosystemi.TS.Utils.SavePreferences;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

public class LoginActivity extends BaseActivity {
    private static SavePreferences pref;
    final int PERMISSION_REQUEST_CODE = 112;
    Button btnLogIn;
    Button btnReg;
    CheckBox chB_remember;
    String description = "tecnosystemi_channel_notification";
    String id = "tecnosystemi_channel_ID";
    String name = "tecnosystemi_channel";
    LoginModel objLogin;
    boolean onCreate;
    String pwdText;
    Resources res;
    EditText txtPass;
    EditText txtUser;
    TextInputLayout txtlypwd;
    String usrText;

    public BaseActivity getActivity() {
        return this;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        this.activity = this;
        setContentView(R.layout.activity_login);
        this.typeActStyle = 1;
        super.onCreate(bundle);
        setupGui();
        this.res = getResources();
        pref = new SavePreferences(this, getString(R.string.PrefsName));
        Constants.android_id = Settings.Secure.getString(getContentResolver(), "android_id");
        hideBackButton();
        this.txtPass.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable editable) {
            }

            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                LoginActivity.this.txtPass.setError((CharSequence) null);
            }
        });
        createNotificationChannel();
        if (Build.VERSION.SDK_INT > 32) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.POST_NOTIFICATIONS", "android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.CHANGE_WIFI_STATE"}, 101);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION", "android.permission.CHANGE_WIFI_STATE"}, 101);
        }
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private void createNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager) getSystemService("notification");
        if (Build.VERSION.SDK_INT >= 26 && NotificationCompat$$ExternalSyntheticApiModelOutline0.m(notificationManager, this.id) == null) {
            NotificationChannel notificationChannel = new NotificationChannel(this.id, this.name, 4);
            notificationChannel.setDescription(this.description);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    private void setupGui() {
        this.txtUser = (EditText) findViewById(R.id.la_txtUsername);
        this.txtPass = (EditText) findViewById(R.id.la_txtPwd);
        this.chB_remember = (CheckBox) findViewById(R.id.la_cbricordami);
        this.txtlypwd = (TextInputLayout) findViewById(R.id.la_txtlyPwd);
        this.btnLogIn = (Button) findViewById(R.id.la_btnLogIn);
        this.btnReg = (Button) findViewById(R.id.la_btnRegistrati);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
        this.txtUser.setTypeface(createFromAsset);
        this.txtPass.setTypeface(createFromAsset);
        this.chB_remember.setTypeface(createFromAsset);
        this.btnLogIn.setTypeface(createFromAsset);
        this.btnReg.setTypeface(createFromAsset);
        try {
            String str = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            TextView textView = (TextView) findViewById(R.id.lblversionname);
            textView.setText(getResources().getString(R.string.version) + "  " + str);
            textView.setTypeface(createFromAsset);
        } catch (Exception unused) {
        }
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 112 && iArr.length > 0) {
            int i2 = iArr[0];
        }
    }

    private void checkRegNotification() {
        if (!this.activity.getSharedPreferences(Constants.PREF_REGID_NAME, 0).contains(Constants.PREF_REGID_SETTING_NAME)) {
            AnonymousClass2 r6 = new Runnable() {
                public void run() {
                    LoginActivity.this.dismissdialog();
                    LoginActivity.this.activity.getSharedPreferences(Constants.PREF_REGID_NAME, 0).edit().putInt(Constants.PREF_REGID_SETTING_NAME, 0).apply();
                    if (Functions.getNotificationPermision(LoginActivity.this.activity)) {
                        Intent intent = new Intent();
                        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                        intent.putExtra("app_package", LoginActivity.this.getPackageName());
                        intent.putExtra("app_uid", LoginActivity.this.getApplicationInfo().uid);
                        intent.setFlags(268435456);
                        intent.putExtra("android.provider.extra.APP_PACKAGE", LoginActivity.this.getPackageName());
                        LoginActivity.this.startActivity(intent);
                    }
                }
            };
            AnonymousClass3 r7 = new Runnable() {
                public void run() {
                    LoginActivity.this.dismissdialog();
                    LoginActivity.this.activity.getSharedPreferences(Constants.PREF_REGID_NAME, 0).edit().putInt(Constants.PREF_REGID_SETTING_NAME, 1).apply();
                    if (!Functions.getNotificationPermision(LoginActivity.this.activity)) {
                        Intent intent = new Intent();
                        intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                        intent.putExtra("app_package", LoginActivity.this.getPackageName());
                        intent.putExtra("app_uid", LoginActivity.this.getApplicationInfo().uid);
                        intent.setFlags(268435456);
                        intent.putExtra("android.provider.extra.APP_PACKAGE", LoginActivity.this.getPackageName());
                        LoginActivity.this.startActivity(intent);
                    }
                }
            };
            openDialogFragment(createYesNoPopUp(getResources().getString(R.string.np_AlertTitle), getResources().getString(R.string.np_AlertMessage), getResources().getString(R.string.no), getResources().getString(R.string.yes), r6, r7));
        }
    }

    public void onResume() {
        super.onResume();
        try {
            String string = pref.getString(Constants.PREF_USERNAME);
            Constants.android_id = Settings.Secure.getString(getContentResolver(), "android_id");
            String Decrypt = AESAlgorithm.Decrypt(Constants.android_id, pref.getString(Constants.PREF_PWD));
            if (pref.getBoolean(Constants.PREF_REMEBERME)) {
                Constants.userId = pref.getInt(Constants.PREF_USER_ID);
                Constants.user = string;
                this.txtUser.setText(string);
                this.txtPass.setText(Decrypt);
                this.chB_remember.setChecked(true);
            } else {
                this.txtUser.setText("");
                this.txtPass.setText("");
            }
        } catch (Exception unused) {
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                }
            });
        }
        new Thread(new Runnable() {
            public void run() {
                List<WifiConfiguration> configuredNetworks;
                try {
                    if (Build.VERSION.SDK_INT < 29) {
                        WifiManager wifiManager = (WifiManager) LoginActivity.this.getApplicationContext().getSystemService(Constants.INTENT_WIFI);
                        if (ActivityCompat.checkSelfPermission(LoginActivity.this.activity, "android.permission.ACCESS_FINE_LOCATION") == 0 && (configuredNetworks = wifiManager.getConfiguredNetworks()) != null) {
                            for (WifiConfiguration next : configuredNetworks) {
                                try {
                                    String replace = next.SSID.replace("\"", "");
                                    if (replace.equals(Constants.WIFI_NAME_PICO_CONFIG)) {
                                        wifiManager.removeNetwork(next.networkId);
                                    } else if (Functions.isPicoWiFi(replace)) {
                                        wifiManager.removeNetwork(next.networkId);
                                    } else if (replace.equals(Constants.WIFI_NAME_CONFIG)) {
                                        wifiManager.removeNetwork(next.networkId);
                                    } else if (replace.equals(Constants.WIFI_NAME_BOOT)) {
                                        wifiManager.removeNetwork(next.networkId);
                                    }
                                } catch (Exception unused) {
                                }
                            }
                        }
                    }
                } catch (Exception unused2) {
                }
            }
        }).start();
    }

    public View getToolBar() {
        return findViewById(R.id.lia_toolbar);
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        AnonymousClass6 r5 = new Runnable() {
            public void run() {
                LoginActivity.this.dismissdialog();
                DataClass.getInstance(LoginActivity.this.activity).populateDemo(LoginActivity.this.activity);
                Intent intent = new Intent(LoginActivity.this.activity, TSHomeActivity.class);
                Constants.ISDEMO = true;
                LoginActivity.this.activity.startActivity(intent);
            }
        };
        list.add(createMenuItem(true, getResources().getString(R.string.la_btnDemo), "", "", r5, false, false));
        AnonymousClass7 r6 = new Runnable() {
            public void run() {
                LoginActivity.this.dismissdialog();
                LoginActivity.this.confirmEmail();
            }
        };
        list.add(createMenuItem(false, getResources().getString(R.string.la_btnRecPwd), "", "", r6, false, false));
        return list;
    }

    public String setToolbarTitle() {
        return "";
    }

    public void btnLogin(View view) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                Functions$$ExternalSyntheticApiModelOutline0.m((ConnectivityManager) getSystemService("connectivity"), (Network) null);
            }
        } catch (Exception unused) {
        }
        this.chB_remember.isChecked();
        try {
            BaseActivity baseActivity = this.activity;
            ((InputMethodManager) getSystemService("input_method")).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 2);
        } catch (Exception unused2) {
        }
        login(this.res.getString(R.string.uri_Login));
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(7:9|10|11|12|13|14|(5:16|17|(1:19)(1:20)|21|28)(2:22|24)) */
    /* JADX WARNING: Missing exception handler attribute for start block: B:12:0x007b */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x00f7 A[SYNTHETIC, Splitter:B:16:0x00f7] */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x0115 A[Catch:{ Exception -> 0x0124 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void login(java.lang.String r12) {
        /*
            r11 = this;
            r0 = 0
            it.tecnosystemi.TS.Utils.Constants.ISDEMO = r0     // Catch:{ Exception -> 0x0124 }
            android.widget.EditText r1 = r11.txtUser     // Catch:{ Exception -> 0x0124 }
            android.text.Editable r1 = r1.getText()     // Catch:{ Exception -> 0x0124 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0124 }
            r11.usrText = r1     // Catch:{ Exception -> 0x0124 }
            android.widget.EditText r1 = r11.txtPass     // Catch:{ Exception -> 0x0124 }
            android.text.Editable r1 = r1.getText()     // Catch:{ Exception -> 0x0124 }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x0124 }
            r11.pwdText = r1     // Catch:{ Exception -> 0x0124 }
            java.lang.String r1 = r11.usrText     // Catch:{ Exception -> 0x0124 }
            boolean r1 = it.tecnosystemi.TS.Utils.Validation.isValidEmail(r1)     // Catch:{ Exception -> 0x0124 }
            java.lang.String r2 = r11.pwdText     // Catch:{ Exception -> 0x0124 }
            boolean r2 = r2.isEmpty()     // Catch:{ Exception -> 0x0124 }
            r3 = 1
            r2 = r2 ^ r3
            if (r1 != 0) goto L_0x0038
            android.content.res.Resources r4 = r11.res     // Catch:{ Exception -> 0x0124 }
            int r5 = it.tecnosystemi.TS.R.string.la_wrongUserOrPwd     // Catch:{ Exception -> 0x0124 }
            java.lang.String r4 = r4.getString(r5)     // Catch:{ Exception -> 0x0124 }
            android.widget.EditText r5 = r11.txtUser     // Catch:{ Exception -> 0x0124 }
            r5.setError(r4)     // Catch:{ Exception -> 0x0124 }
        L_0x0038:
            if (r2 != 0) goto L_0x0047
            android.content.res.Resources r4 = r11.res     // Catch:{ Exception -> 0x0124 }
            int r5 = it.tecnosystemi.TS.R.string.la_wrongUserOrPwd     // Catch:{ Exception -> 0x0124 }
            java.lang.String r4 = r4.getString(r5)     // Catch:{ Exception -> 0x0124 }
            android.widget.EditText r5 = r11.txtPass     // Catch:{ Exception -> 0x0124 }
            r5.setError(r4)     // Catch:{ Exception -> 0x0124 }
        L_0x0047:
            if (r1 == 0) goto L_0x0124
            if (r2 == 0) goto L_0x0124
            it.tecnosystemi.TS.Model.LoginModel r1 = new it.tecnosystemi.TS.Model.LoginModel     // Catch:{ Exception -> 0x0124 }
            r1.<init>()     // Catch:{ Exception -> 0x0124 }
            r11.objLogin = r1     // Catch:{ Exception -> 0x0124 }
            it.tecnosystemi.TS.Utils.AESCrypt r1 = new it.tecnosystemi.TS.Utils.AESCrypt     // Catch:{ Exception -> 0x007b }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x007b }
            r2.<init>()     // Catch:{ Exception -> 0x007b }
            java.lang.String r4 = it.tecnosystemi.TS.Utils.Constants.android_id     // Catch:{ Exception -> 0x007b }
            r5 = 8
            java.lang.String r4 = r4.substring(r0, r5)     // Catch:{ Exception -> 0x007b }
            r2.append(r4)     // Catch:{ Exception -> 0x007b }
            java.lang.String r4 = it.tecnosystemi.TS.Utils.Constants.SALT     // Catch:{ Exception -> 0x007b }
            r2.append(r4)     // Catch:{ Exception -> 0x007b }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x007b }
            r1.<init>(r2)     // Catch:{ Exception -> 0x007b }
            it.tecnosystemi.TS.Model.LoginModel r2 = r11.objLogin     // Catch:{ Exception -> 0x007b }
            java.lang.String r4 = r11.pwdText     // Catch:{ Exception -> 0x007b }
            java.lang.String r1 = r1.encrypt(r4)     // Catch:{ Exception -> 0x007b }
            r2.setPwd(r1)     // Catch:{ Exception -> 0x007b }
        L_0x007b:
            java.util.TimeZone r1 = java.util.TimeZone.getDefault()     // Catch:{ Exception -> 0x0124 }
            it.tecnosystemi.TS.Model.LoginModel r2 = r11.objLogin     // Catch:{ Exception -> 0x0124 }
            java.lang.String r1 = r1.getID()     // Catch:{ Exception -> 0x0124 }
            java.lang.String r4 = "/"
            java.lang.String r5 = "@"
            java.lang.String r1 = r1.replace(r4, r5)     // Catch:{ Exception -> 0x0124 }
            r2.setTimezone(r1)     // Catch:{ Exception -> 0x0124 }
            it.tecnosystemi.TS.Model.LoginModel r1 = r11.objLogin     // Catch:{ Exception -> 0x0124 }
            java.lang.String r2 = r11.usrText     // Catch:{ Exception -> 0x0124 }
            r1.setUser(r2)     // Catch:{ Exception -> 0x0124 }
            it.tecnosystemi.TS.Model.LoginModel r1 = r11.objLogin     // Catch:{ Exception -> 0x0124 }
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.android_id     // Catch:{ Exception -> 0x0124 }
            r1.setDeviceId(r2)     // Catch:{ Exception -> 0x0124 }
            it.tecnosystemi.TS.Model.LoginModel r1 = r11.objLogin     // Catch:{ Exception -> 0x0124 }
            it.tecnosystemi.TS.Activity.BaseActivity r2 = r11.activity     // Catch:{ Exception -> 0x0124 }
            java.lang.String r2 = r2.FirebaseToken     // Catch:{ Exception -> 0x0124 }
            r1.setTokenPush(r2)     // Catch:{ Exception -> 0x0124 }
            it.tecnosystemi.TS.Model.LoginModel r1 = r11.objLogin     // Catch:{ Exception -> 0x0124 }
            java.lang.String r2 = "fcm2"
            r1.setPlatform(r2)     // Catch:{ Exception -> 0x0124 }
            it.tecnosystemi.TS.Utils.Constants.CHECKED_TS_VER = r0     // Catch:{ Exception -> 0x0124 }
            com.google.gson.Gson r1 = new com.google.gson.Gson     // Catch:{ Exception -> 0x0124 }
            r1.<init>()     // Catch:{ Exception -> 0x0124 }
            it.tecnosystemi.TS.Model.LoginModel r2 = r11.objLogin     // Catch:{ Exception -> 0x0124 }
            java.lang.String r9 = r1.toJson((java.lang.Object) r2)     // Catch:{ Exception -> 0x0124 }
            java.lang.String r1 = r11.usrText     // Catch:{ Exception -> 0x0124 }
            java.lang.String r2 = r11.pwdText     // Catch:{ Exception -> 0x0124 }
            java.lang.String[] r10 = new java.lang.String[]{r1, r2}     // Catch:{ Exception -> 0x0124 }
            it.tecnosystemi.TS.Threads.ThreadWebService r1 = new it.tecnosystemi.TS.Threads.ThreadWebService     // Catch:{ Exception -> 0x0124 }
            it.tecnosystemi.TS.Activity.BaseActivity r5 = r11.activity     // Catch:{ Exception -> 0x0124 }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0124 }
            r2.<init>()     // Catch:{ Exception -> 0x0124 }
            android.content.res.Resources r4 = r11.res     // Catch:{ Exception -> 0x0124 }
            int r6 = it.tecnosystemi.TS.R.string.uriWebService     // Catch:{ Exception -> 0x0124 }
            java.lang.String r4 = r4.getString(r6)     // Catch:{ Exception -> 0x0124 }
            r2.append(r4)     // Catch:{ Exception -> 0x0124 }
            r2.append(r12)     // Catch:{ Exception -> 0x0124 }
            java.lang.String r8 = r2.toString()     // Catch:{ Exception -> 0x0124 }
            r6 = 1
            r7 = 2
            r4 = r1
            r4.<init>(r5, r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x0124 }
            r1.start()     // Catch:{ Exception -> 0x0124 }
            android.widget.CheckBox r12 = r11.chB_remember     // Catch:{ Exception -> 0x0124 }
            boolean r12 = r12.isChecked()     // Catch:{ Exception -> 0x0124 }
            java.lang.String r1 = "pwd"
            java.lang.String r2 = "username"
            java.lang.String r4 = "rememberme"
            java.lang.String r5 = ""
            if (r12 == 0) goto L_0x0115
            java.lang.String r12 = it.tecnosystemi.TS.Utils.Constants.android_id     // Catch:{ Exception -> 0x0124 }
            java.lang.String r0 = r11.pwdText     // Catch:{ Exception -> 0x0124 }
            java.lang.String r12 = it.tecnosystemi.TS.Utils.AESAlgorithm.Encrypt(r12, r0)     // Catch:{ Exception -> 0x0124 }
            it.tecnosystemi.TS.Utils.SavePreferences r0 = pref     // Catch:{ Exception -> 0x0124 }
            r0.save((java.lang.String) r4, (boolean) r3)     // Catch:{ Exception -> 0x0124 }
            it.tecnosystemi.TS.Utils.SavePreferences r0 = pref     // Catch:{ Exception -> 0x0124 }
            java.lang.String r3 = r11.usrText     // Catch:{ Exception -> 0x0124 }
            r0.save((java.lang.String) r2, (java.lang.String) r3)     // Catch:{ Exception -> 0x0124 }
            it.tecnosystemi.TS.Utils.SavePreferences r0 = pref     // Catch:{ Exception -> 0x0124 }
            if (r12 != 0) goto L_0x0110
            goto L_0x0111
        L_0x0110:
            r5 = r12
        L_0x0111:
            r0.save((java.lang.String) r1, (java.lang.String) r5)     // Catch:{ Exception -> 0x0124 }
            goto L_0x0124
        L_0x0115:
            it.tecnosystemi.TS.Utils.SavePreferences r12 = pref     // Catch:{ Exception -> 0x0124 }
            r12.save((java.lang.String) r4, (boolean) r0)     // Catch:{ Exception -> 0x0124 }
            it.tecnosystemi.TS.Utils.SavePreferences r12 = pref     // Catch:{ Exception -> 0x0124 }
            r12.save((java.lang.String) r2, (java.lang.String) r5)     // Catch:{ Exception -> 0x0124 }
            it.tecnosystemi.TS.Utils.SavePreferences r12 = pref     // Catch:{ Exception -> 0x0124 }
            r12.save((java.lang.String) r1, (java.lang.String) r5)     // Catch:{ Exception -> 0x0124 }
        L_0x0124:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.LoginActivity.login(java.lang.String):void");
    }

    public void needprivacy() {
        SignUpActivity.PRIVACY = false;
        SignUpActivity.TOU = false;
        SignUpActivity.MARKETING = false;
        Intent intent = new Intent(this.activity, GDPRActivity.class);
        intent.putExtra(Constants.GDPRFROMLOGIN, true);
        intent.putExtra(Constants.GDprUSERNAME, this.usrText);
        startActivity(intent);
    }

    public void btnSignUp(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    public void btnRecPwd(View view) {
        confirmEmail();
    }

    /* access modifiers changed from: private */
    public void confirmEmail() {
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
        Typeface.createFromAsset(getAssets(), "fonts/AvenirNextCondensed_Bold.ttf");
        View inflate = LayoutInflater.from(this.activity).inflate(R.layout.popup_changepwd, (ViewGroup) null);
        ((TextView) inflate.findViewById(R.id.lblcmTitle)).setTypeface(createFromAsset);
        final EditText editText = (EditText) inflate.findViewById(R.id.txtcm);
        editText.setTypeface(createFromAsset);
        Button button = (Button) inflate.findViewById(R.id.btncmyes);
        Button button2 = (Button) inflate.findViewById(R.id.btncmno);
        button.setTypeface(createFromAsset);
        button2.setTypeface(createFromAsset);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                LoginActivity.this.dismissdialog();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                LoginModel loginModel = new LoginModel();
                loginModel.setUser(editText.getText().toString());
                loginModel.setPwd("");
                loginModel.setDeviceId(Constants.android_id);
                loginModel.setTimezone(TimeZone.getDefault().getID().replace("/", "@"));
                BaseActivity baseActivity = LoginActivity.this.activity;
                new ThreadWebService(baseActivity, 1, 3, LoginActivity.this.res.getString(R.string.uriWebService) + LoginActivity.this.res.getString(R.string.uri_ForgotPwd), new Gson().toJson((Object) loginModel), new String[]{editText.getText().toString(), ""}).start();
                LoginActivity.this.dismissdialog();
            }
        });
        MenuList menuList = new MenuList();
        ArrayList arrayList = new ArrayList();
        arrayList.add((ConstraintLayout) inflate.findViewById(R.id.ly_change_pwd));
        menuList.setLayouts(arrayList);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BUNDLE_MENU, Constants.AS_POPUP);
        bundle.putString(Constants.BUNDLE_TITLE, getResources().getString(R.string.la_dialogForgot));
        openDialogFragment(bundle, menuList);
    }
}
