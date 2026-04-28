package it.tecnosystemi.TS.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.view.ViewCompat;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import it.tecnosystemi.TS.Activity.PICO.PicoActivity;
import it.tecnosystemi.TS.Activity.VMC.VMCActivity;
import it.tecnosystemi.TS.Adapters.SpinnerAdapter;
import it.tecnosystemi.TS.Commands.MySocket;
import it.tecnosystemi.TS.Commands.MySocketBootLoader;
import it.tecnosystemi.TS.Commands.PicoSocketBootloader;
import it.tecnosystemi.TS.Fragment.MenuFragment;
import it.tecnosystemi.TS.Model.ControlUnit;
import it.tecnosystemi.TS.Model.MenuList;
import it.tecnosystemi.TS.Model.Response;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import it.tecnosystemi.TS.Utils.Functions$$ExternalSyntheticApiModelOutline0;
import it.tecnosystemi.TS.Utils.SavePreferences;
import it.tecnosystemi.TS.Utils.WifiChangeBroadcastReceiver;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.json.JSONObject;

public abstract class BaseActivity extends AppCompatActivity implements WifiChangeBroadcastReceiver.WifiChangeBroadcastListener {
    public static boolean PICO_12H = false;
    static int STATIC_networkId = 0;
    static BaseActivity ShowingActivity = null;
    public static Typeface avenir = null;
    public static Typeface avenirbold = null;
    public static boolean bootloadResetWiFi = false;
    public static BootloaderActivity bootloaderActivity = null;
    public static boolean continueTrie = true;
    public static Typeface fontawesome = null;
    public static Typeface icomoon = null;
    /* access modifiers changed from: private */
    public static Runnable mDelayedRunnable = null;
    /* access modifiers changed from: private */
    public static Handler mHandler = null;
    public static ConnectivityManager.NetworkCallback networkCallback = null;
    public static boolean onLostWifi = false;
    public static boolean picoActDestoryed = false;
    public static Typeface picomoon = null;
    /* access modifiers changed from: private */
    public static SavePreferences pref = null;
    public static String toConnPwd = null;
    public static String toConnSid = null;
    public static boolean triedonce = false;
    public String FirebaseToken;
    public String TAG = "ERRORE";
    BaseActivity activity;
    boolean asckedGpsOn = false;
    boolean asckedWifiOn = false;
    boolean asckedpermissionGps = false;
    public boolean background;
    public Thread cercacentralina;
    public boolean changepinempty = true;
    boolean connected;
    ConnectivityManager connectivityManager;
    public ControlUnit cu;
    String date_time = "";
    public boolean errorcollegamento;
    boolean finishedscan;
    public boolean firtCalltoGetState;
    public Thread getState;
    boolean gettingstate;
    public boolean gotobooloader = false;
    Handler handler;
    public boolean hideloading = true;
    public int indexZona;
    boolean interrupt;
    TextView lblTitle;
    public TextView lblhome;
    public TextView lblman;
    List<String> list;
    int mDay;
    int mHour;
    int mMinute;
    public boolean manualconn;
    BundleMenuList menuArgs;
    public MenuFragment myDialogFragment = null;
    int networkId = -1;
    int onlost = 0;
    public boolean pausecercacentralina;
    public boolean paused;
    boolean redhome;
    public Runnable runnable;
    public boolean sendingstate;
    Spinner spinnerTimezone;
    int temp;
    public ControlUnit tempcu;
    int thread_sleep = 5000;
    View toolbar;
    TextView txtDateTime;
    public EditText txtPin;
    public EditText txtPin2;
    public int typeActStyle = 0;
    String urlgetupd;
    WifiManager wifiManager;
    private WifiChangeBroadcastReceiver wifiStateChangeReceiver;

    public abstract BaseActivity getActivity();

    public abstract List<ConstraintLayout> getMenu(List<ConstraintLayout> list2);

    public abstract View getToolBar();

    public abstract String setToolbarTitle();

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        avenir = Typeface.createFromAsset(getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
        icomoon = Typeface.createFromAsset(getAssets(), "fonts/icomoon.ttf");
        fontawesome = Typeface.createFromAsset(getAssets(), "fonts/fontawesome.ttf");
        avenirbold = Typeface.createFromAsset(getAssets(), "fonts/AvenirNextCondensed_Bold.ttf");
        picomoon = Typeface.createFromAsset(getAssets(), "fonts/PICOMoon.ttf");
        this.toolbar = getToolBar();
        this.activity = getActivity();
        this.paused = false;
        setToolBar();
        this.firtCalltoGetState = true;
        this.manualconn = false;
        Functions.context = this;
        int i = this.typeActStyle;
        if (i == 0) {
            setTheme(R.style.AppTheme);
        } else if (i == 1) {
            setTheme(R.style.AppThemeTS);
        } else if (i == 3) {
            setTheme(R.style.AppThemeVMC);
        } else {
            setTheme(R.style.AppThemePICO);
        }
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            public void onComplete(Task<String> task) {
                if (!task.isSuccessful()) {
                    Log.w(BaseActivity.this.TAG, "Fetching FCM registration token failed", task.getException());
                    return;
                }
                BaseActivity.this.FirebaseToken = task.getResult();
            }
        });
        SavePreferences savePreferences = new SavePreferences(this, getString(R.string.PrefsName));
        pref = savePreferences;
        PICO_12H = savePreferences.getBoolean("PICO_12H");
        Constants.SEI_X_TEMP_UM = pref.getInt(Constants.SEI_X_TEMP_UM_PREF, 0);
    }

    private void setToolBar() {
        ConstraintLayout constraintLayout = (ConstraintLayout) this.toolbar.findViewById(R.id.ly_toolbar);
        final Button button = (Button) constraintLayout.findViewById(R.id.btn_optionmenu);
        Button button2 = (Button) constraintLayout.findViewById(R.id.btn_indietro);
        TextView textView = (TextView) constraintLayout.findViewById(R.id.lbltoolbarhome);
        this.lblhome = textView;
        textView.setTypeface(fontawesome);
        if (this.typeActStyle == 2) {
            TextView textView2 = (TextView) constraintLayout.findViewById(R.id.lbltoolbarman);
            this.lblman = textView2;
            textView2.setTypeface(fontawesome);
        }
        if (this.activity instanceof ControlUnitActivity) {
            if (this.cu.getIcontype() == -1) {
                this.lblhome.setVisibility(8);
            } else {
                this.lblhome.setVisibility(0);
                this.lblhome.setText(Constants.ICON_TYPE[this.cu.getIcontype()]);
            }
        }
        button.setTypeface(icomoon);
        MenuList menuList = new MenuList();
        List<ConstraintLayout> menu = getMenu(new ArrayList());
        menuList.setLayouts(menu);
        final Bundle bundle = new Bundle();
        this.myDialogFragment = new MenuFragment(menuList);
        BundleMenuList bundleMenuList = new BundleMenuList();
        this.menuArgs = bundleMenuList;
        bundleMenuList.ml = menuList;
        if (menu.size() > 0) {
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    int y = ((int) button.getY()) + button.getHeight();
                    bundle.putInt(Constants.BUNDLE_POSX, (int) button.getX());
                    bundle.putInt(Constants.BUNDLE_POSY, y);
                    bundle.putInt(Constants.BUNDLE_MENU, Constants.AS_MENU);
                    BaseActivity.this.menuArgs.bundle = bundle;
                    BaseActivity baseActivity = BaseActivity.this;
                    baseActivity.openDialogFragment(baseActivity.menuArgs);
                }
            });
        }
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                BaseActivity.this.onBackPressed();
            }
        });
        button2.setTypeface(fontawesome);
        setSupportActionBar((Toolbar) this.toolbar);
        String toolbarTitle = setToolbarTitle();
        this.lblTitle = (TextView) constraintLayout.findViewById(R.id.lblcmTitle);
        if (toolbarTitle == null || toolbarTitle.isEmpty()) {
            this.lblTitle.setVisibility(8);
            loading_position(false);
            return;
        }
        this.lblTitle.setText(toolbarTitle);
        this.lblTitle.setTypeface(avenir);
        loading_position(true);
    }

    private void loading_position(boolean z) {
        TextView textView = (TextView) ((ConstraintLayout) findViewById(R.id.ly_progress)).findViewById(R.id.lblfaketitle);
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) textView.getLayoutParams();
        if (z) {
            textView.setVisibility(4);
        } else {
            textView.setVisibility(8);
        }
    }

    public void updatemenu() {
        MenuList menuList = new MenuList();
        menuList.setLayouts(getMenu(new ArrayList()));
        BundleMenuList bundleMenuList = this.menuArgs;
        if (bundleMenuList != null) {
            bundleMenuList.ml = menuList;
        }
    }

    public void changeTitle(String str) {
        try {
            if (this.activity instanceof ControlUnitActivity) {
                if (this.cu.getIcontype() == -1) {
                    this.cu.setIcontype(0);
                }
                this.lblhome.setVisibility(0);
                this.lblhome.setText(Constants.ICON_TYPE[this.cu.getIcontype()]);
            }
        } catch (Exception unused) {
        }
        this.lblTitle.setText(str);
    }

    public void changeIconType(String str) {
        this.lblhome.setVisibility(0);
        this.lblhome.setText(str);
    }

    public void dismissdialog() {
        try {
            ((MenuFragment) getSupportFragmentManager().findFragmentByTag("dialog_fragment")).dismiss();
        } catch (Exception unused) {
        }
    }

    public void openDialogFragment(BundleMenuList bundleMenuList) {
        openDialogFragment(bundleMenuList.bundle, bundleMenuList.ml);
    }

    public void openDialogFragment(Bundle bundle, MenuList menuList) {
        try {
            if (this.myDialogFragment.isAdded()) {
                dismissdialog();
            }
        } catch (Exception unused) {
        }
        MenuFragment menuFragment = new MenuFragment(menuList);
        this.myDialogFragment = menuFragment;
        menuFragment.setArguments(bundle);
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    BaseActivity.this.myDialogFragment.show(BaseActivity.this.getSupportFragmentManager(), "dialog_fragment");
                } catch (Exception unused) {
                }
            }
        });
    }

    public void hideBackButton() {
        ((Button) ((ConstraintLayout) this.toolbar.findViewById(R.id.ly_toolbar)).findViewById(R.id.btn_indietro)).setVisibility(8);
    }

    public void hideMenuButton() {
        ((Button) ((ConstraintLayout) this.toolbar.findViewById(R.id.ly_toolbar)).findViewById(R.id.btn_optionmenu)).setVisibility(8);
    }

    public void showMenuButton() {
        ((Button) ((ConstraintLayout) this.toolbar.findViewById(R.id.ly_toolbar)).findViewById(R.id.btn_optionmenu)).setVisibility(0);
    }

    public ConstraintLayout createMenuItem(boolean z, String str, String str2, String str3, final Runnable runnable2, boolean z2, boolean z3) {
        View view;
        if (z3) {
            view = LayoutInflater.from(this.activity).inflate(R.layout.riga_popup, (ViewGroup) null);
        } else {
            view = LayoutInflater.from(this.activity).inflate(R.layout.riga_menu, (ViewGroup) null);
        }
        ConstraintLayout constraintLayout = (ConstraintLayout) view.findViewById(R.id.ly_rigamenu);
        TextView textView = (TextView) view.findViewById(R.id.mi_Text);
        TextView textView2 = (TextView) view.findViewById(R.id.mi_Icon1);
        TextView textView3 = (TextView) view.findViewById(R.id.mi_Icon2);
        TextView textView4 = (TextView) view.findViewById(R.id.mi_Spunta);
        if (z) {
            view.findViewById(R.id.mi_separator).setVisibility(8);
        }
        textView.setText(str);
        textView.setTypeface(avenir);
        if (str2.isEmpty()) {
            textView2.setVisibility(8);
        } else {
            textView2.setText(str2);
        }
        if (str3 == null) {
            textView3.setVisibility(8);
        } else if (!str3.isEmpty()) {
            textView3.setText(str3);
        } else {
            textView3.setVisibility(8);
        }
        if (!z2) {
            textView2.setTextColor(getResources().getColor(R.color.colormenuitem));
            textView3.setTextColor(getResources().getColor(R.color.colormenuitem));
            textView.setTextColor(getResources().getColor(R.color.colormenuitem));
            textView4.setVisibility(8);
        } else {
            textView2.setTextColor(getResources().getColor(R.color.colorPrimary));
            textView3.setTextColor(getResources().getColor(R.color.colorPrimary));
            textView.setTextColor(getResources().getColor(R.color.colorPrimary));
            textView4.setVisibility(0);
        }
        if (str2 != null && !str2.isEmpty()) {
            textView2.setTypeface(icomoon);
            textView3.setTypeface(icomoon);
        }
        textView4.setTypeface(icomoon);
        textView4.setText(getResources().getString(R.string.icon_im_spunta));
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                runnable2.run();
            }
        });
        return constraintLayout;
    }

    public ConstraintLayout createMenuItemImage(boolean z, String str, int i, final Runnable runnable2, boolean z2, boolean z3) {
        View view;
        if (z3) {
            view = LayoutInflater.from(this.activity).inflate(R.layout.riga_popup_img, (ViewGroup) null);
        } else {
            view = LayoutInflater.from(this.activity).inflate(R.layout.riga_menu_img, (ViewGroup) null);
        }
        ConstraintLayout constraintLayout = (ConstraintLayout) view.findViewById(R.id.ly_rigamenu);
        TextView textView = (TextView) view.findViewById(R.id.mi_Text);
        ImageView imageView = (ImageView) view.findViewById(R.id.mi_Icon1);
        TextView textView2 = (TextView) view.findViewById(R.id.mi_Spunta);
        if (z) {
            view.findViewById(R.id.mi_separator).setVisibility(8);
        }
        textView.setText(str);
        textView.setTypeface(avenir);
        if (i < 0) {
            imageView.setVisibility(8);
        } else {
            imageView.setImageDrawable(getResources().getDrawable(i));
        }
        if (!z2) {
            imageView.setColorFilter(ViewCompat.MEASURED_STATE_MASK);
            textView.setTextColor(getResources().getColor(R.color.colormenuitem));
            textView2.setVisibility(8);
        } else {
            imageView.setColorFilter(getResources().getColor(R.color.colorPrimary));
            textView.setTextColor(getResources().getColor(R.color.colorPrimary));
            textView2.setVisibility(0);
        }
        textView2.setTypeface(icomoon);
        textView2.setText(getResources().getString(R.string.icon_im_spunta));
        constraintLayout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                runnable2.run();
            }
        });
        return constraintLayout;
    }

    public ConstraintLayout createDataOraTimezoneItem(String str, String str2) {
        View inflate = LayoutInflater.from(this.activity).inflate(R.layout.pupup_timezone_dataora, (ViewGroup) null);
        ConstraintLayout constraintLayout = (ConstraintLayout) inflate.findViewById(R.id.lyTimezoneDataOra);
        TextView textView = (TextView) inflate.findViewById(R.id.txtDataOra);
        this.txtDateTime = textView;
        textView.setText(str);
        this.txtDateTime.setTypeface(avenir);
        Button button = (Button) inflate.findViewById(R.id.btnSalva);
        int i = -1;
        button.setTextColor(-1);
        button.setTypeface(avenir);
        ((TextView) inflate.findViewById(R.id.lblFreccia)).setTypeface(icomoon);
        ((TextView) inflate.findViewById(R.id.lbl24Ore)).setTypeface(avenir);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (BaseActivity.this.activity instanceof PicoActivity) {
                    BaseActivity.this.dismissdialog();
                    if (PicoActivity.pico.getOffline().booleanValue()) {
                        ((PicoActivity) BaseActivity.this.activity).impostaDataOraTimeZone(BaseActivity.this.mDay, BaseActivity.this.mHour, BaseActivity.this.mMinute, 0);
                    } else {
                        ((PicoActivity) BaseActivity.this.activity).impostaDataOraTimeZone(BaseActivity.this.mDay, BaseActivity.this.mHour, BaseActivity.this.mMinute, BaseActivity.this.spinnerTimezone.getSelectedItemPosition());
                    }
                } else if (!(BaseActivity.this.activity instanceof VMCActivity)) {
                } else {
                    if (VMCActivity.vmc.getOffline().booleanValue()) {
                        ((VMCActivity) BaseActivity.this.activity).impostaDataOraTimeZone(BaseActivity.this.mDay, BaseActivity.this.mHour, BaseActivity.this.mMinute, 0);
                    } else {
                        ((VMCActivity) BaseActivity.this.activity).impostaDataOraTimeZone(BaseActivity.this.mDay, BaseActivity.this.mHour, BaseActivity.this.mMinute, BaseActivity.this.spinnerTimezone.getSelectedItemPosition());
                    }
                }
            }
        });
        final SwitchCompat switchCompat = (SwitchCompat) inflate.findViewById(R.id.sw_24ore);
        switchCompat.setChecked(PICO_12H);
        ArrayList arrayList = new ArrayList();
        this.list = arrayList;
        arrayList.add(getResources().getString(R.string.f2cr_Luned));
        this.list.add(getResources().getString(R.string.f3cr_Marted));
        this.list.add(getResources().getString(R.string.f4cr_Mercoled));
        this.list.add(getResources().getString(R.string.f1cr_Gioved));
        this.list.add(getResources().getString(R.string.f5cr_Venerd));
        this.list.add(getResources().getString(R.string.cr_Sabato));
        this.list.add(getResources().getString(R.string.cr_Domenica));
        switch (Calendar.getInstance().get(7)) {
            case 1:
                this.mDay = 6;
                break;
            case 2:
                this.mDay = 0;
                break;
            case 3:
                this.mDay = 1;
                break;
            case 4:
                this.mDay = 2;
                break;
            case 5:
                this.mDay = 3;
                break;
            case 6:
                this.mDay = 4;
                break;
            case 7:
                this.mDay = 5;
                break;
        }
        this.mHour = Calendar.getInstance().get(11);
        this.mMinute = Calendar.getInstance().get(12);
        change_timetxt_pico(switchCompat.isChecked());
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                BaseActivity.PICO_12H = z;
                BaseActivity.pref.save("PICO_12H", BaseActivity.PICO_12H);
                BaseActivity.this.change_timetxt_pico(z);
            }
        });
        this.txtDateTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                BaseActivity.this.timePicker(switchCompat.isChecked(), true);
            }
        });
        ((TextView) inflate.findViewById(R.id.lblFreccia_tz)).setTypeface(fontawesome);
        this.spinnerTimezone = (Spinner) inflate.findViewById(R.id.spn_timezones);
        boolean z = this.activity instanceof PicoActivity ? !PicoActivity.pico.getOffline().booleanValue() : true;
        if (this.activity instanceof VMCActivity) {
            button.setBackground(getResources().getDrawable(R.drawable.btn_selector_vmc));
            ColorStateList colorStateList = new ColorStateList(new int[][]{new int[]{16842912}, new int[]{-16842912}}, new int[]{getResources().getColor(R.color.vmc_main), -3355444});
            switchCompat.setThumbTintList(colorStateList);
            switchCompat.setTrackTintList(colorStateList);
            z = !VMCActivity.vmc.getOffline().booleanValue();
        }
        if (!z) {
            this.spinnerTimezone.setVisibility(4);
        } else {
            this.spinnerTimezone.setVisibility(0);
            ArrayList arrayList2 = new ArrayList();
            for (int i2 = 0; i2 < Constants.TIMEZONES.size(); i2++) {
                arrayList2.add(Constants.TIMEZONES.get(i2).getDisplayName());
                if (str2 == null || str2.isEmpty()) {
                    if (Constants.TIMEZONES.get(i2).getIsYours() != 1) {
                    }
                } else if (!Constants.TIMEZONES.get(i2).getIdTimeZone().equalsIgnoreCase(str2)) {
                }
                i = i2;
            }
            this.spinnerTimezone.setAdapter(new SpinnerAdapter(this, R.layout.spinner_item_alto_alto, arrayList2, false));
            if (i >= 0) {
                this.spinnerTimezone.setSelection(i);
            }
        }
        return constraintLayout;
    }

    public ConstraintLayout createDataOraItem(String str, final Runnable runnable2) {
        View inflate = LayoutInflater.from(this.activity).inflate(R.layout.popup_dataora, (ViewGroup) null);
        ConstraintLayout constraintLayout = (ConstraintLayout) inflate.findViewById(R.id.lyDataOra);
        TextView textView = (TextView) inflate.findViewById(R.id.txtDataOra);
        this.txtDateTime = textView;
        textView.setText(str);
        this.txtDateTime.setTypeface(avenir);
        Button button = (Button) inflate.findViewById(R.id.btnSalva);
        button.setTextColor(-1);
        button.setBackground(getResources().getDrawable(R.drawable.btn_selector));
        button.setTypeface(avenir);
        ((TextView) inflate.findViewById(R.id.lblFreccia)).setTypeface(icomoon);
        ((TextView) inflate.findViewById(R.id.lbl24Ore)).setTypeface(avenir);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                runnable2.run();
            }
        });
        final Switch switchR = (Switch) inflate.findViewById(R.id.sw_24ore);
        if (this.cu.getH24() == 0) {
            switchR.setChecked(true);
        } else {
            switchR.setChecked(false);
        }
        switchR.isChecked();
        int i = Calendar.getInstance().get(7);
        ArrayList arrayList = new ArrayList();
        this.list = arrayList;
        arrayList.add(getResources().getString(R.string.f2cr_Luned));
        this.list.add(getResources().getString(R.string.f3cr_Marted));
        this.list.add(getResources().getString(R.string.f4cr_Mercoled));
        this.list.add(getResources().getString(R.string.f1cr_Gioved));
        this.list.add(getResources().getString(R.string.f5cr_Venerd));
        this.list.add(getResources().getString(R.string.cr_Sabato));
        this.list.add(getResources().getString(R.string.cr_Domenica));
        switch (i) {
            case 1:
                this.mDay = 6;
                break;
            case 2:
                this.mDay = 0;
                break;
            case 3:
                this.mDay = 1;
                break;
            case 4:
                this.mDay = 2;
                break;
            case 5:
                this.mDay = 3;
                break;
            case 6:
                this.mDay = 4;
                break;
            case 7:
                this.mDay = 5;
                break;
        }
        this.mHour = Calendar.getInstance().get(11);
        this.mMinute = Calendar.getInstance().get(12);
        change_timetxt(switchR.isChecked());
        switchR.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
                BaseActivity.this.change_timetxt(z);
            }
        });
        this.txtDateTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                BaseActivity.this.timePicker(switchR.isChecked(), false);
            }
        });
        return constraintLayout;
    }

    public void change_timetxt_pico(boolean z) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("hh:mm a");
        String str = String.format("%02d", new Object[]{Integer.valueOf(this.mHour)}) + ":" + String.format("%02d", new Object[]{Integer.valueOf(this.mMinute)});
        try {
            Date parse = simpleDateFormat.parse(str);
            if (!z) {
                str = simpleDateFormat2.format(parse);
            }
            this.txtDateTime.setText(this.list.get(this.mDay) + " " + str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void change_timetxt(boolean z) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("hh:mm a");
        String str = String.format("%02d", new Object[]{Integer.valueOf(this.mHour)}) + ":" + String.format("%02d", new Object[]{Integer.valueOf(this.mMinute)});
        this.cu.setOre(this.mHour);
        this.cu.setMinuti(this.mMinute);
        this.cu.setDay(this.mDay);
        this.cu.setH24(z ^ true ? 1 : 0);
        try {
            Date parse = simpleDateFormat.parse(str);
            if (!z) {
                str = simpleDateFormat2.format(parse);
            }
            this.txtDateTime.setText(this.list.get(this.mDay) + " " + str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: private */
    public void timePicker(boolean z, boolean z2) {
        View inflate = View.inflate(this.activity, R.layout.datetimepicker, (ViewGroup) null);
        AlertDialog create = new AlertDialog.Builder(this.activity).create();
        final TimePicker timePicker = (TimePicker) inflate.findViewById(R.id.time_picker);
        TextView textView = (TextView) inflate.findViewById(R.id.lblorafreccia);
        Button button = (Button) inflate.findViewById(R.id.date_time_set);
        button.setTypeface(avenir);
        button.setTextColor(-1);
        if (this.activity instanceof VMCActivity) {
            button.setBackground(getResources().getDrawable(R.drawable.btn_selector_vmc));
            ((ConstraintLayout) inflate.findViewById(R.id.lyDataORa)).setBackgroundColor(getResources().getColor(R.color.vmc_background));
        } else if (z2) {
            button.setBackground(getResources().getDrawable(R.drawable.btn_selector_pico));
        } else {
            button.setBackground(getResources().getDrawable(R.drawable.btn_selector));
        }
        textView.setTypeface(icomoon);
        final Spinner spinner = (Spinner) inflate.findViewById(R.id.spinnerora);
        spinner.setAdapter(new SpinnerAdapter(this, R.layout.spinner_item, this.list, false));
        spinner.setSelection(this.mDay);
        timePicker.setIs24HourView(Boolean.valueOf(z));
        if (Build.VERSION.SDK_INT >= 23) {
            timePicker.setHour(this.mHour);
            timePicker.setMinute(this.mMinute);
        } else {
            timePicker.setCurrentHour(Integer.valueOf(this.mHour));
            timePicker.setCurrentMinute(Integer.valueOf(this.mMinute));
        }
        final boolean z3 = z2;
        final boolean z4 = z;
        final AlertDialog alertDialog = create;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                BaseActivity.this.mHour = timePicker.getCurrentHour().intValue();
                BaseActivity.this.mMinute = timePicker.getCurrentMinute().intValue();
                BaseActivity.this.mDay = spinner.getSelectedItemPosition();
                if (z3) {
                    BaseActivity.this.change_timetxt_pico(z4);
                } else {
                    BaseActivity.this.change_timetxt(z4);
                }
                alertDialog.dismiss();
            }
        });
        create.setView(inflate);
        create.show();
    }

    public ConstraintLayout createEditTextMenuItem(String str, String str2, TextView.OnEditorActionListener onEditorActionListener, int i) {
        View inflate = LayoutInflater.from(this.activity).inflate(R.layout.menu_item_edittext, (ViewGroup) null);
        ConstraintLayout constraintLayout = (ConstraintLayout) inflate.findViewById(R.id.ly_rigamenuedittext);
        EditText editText = (EditText) inflate.findViewById(R.id.menuitem_txt);
        if (str.isEmpty()) {
            editText.setHint(str2);
        } else {
            editText.setText(str);
        }
        if (i > 0) {
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(i)});
        }
        editText.setTypeface(avenir);
        editText.setOnEditorActionListener(onEditorActionListener);
        return constraintLayout;
    }

    public ConstraintLayout createChangeNumberItem(int i, String str, final int i2, int i3, Runnable runnable2, TextView textView) {
        int i4 = i;
        String str2 = str;
        View inflate = LayoutInflater.from(this.activity).inflate(R.layout.popup_changenumber, (ViewGroup) null);
        ConstraintLayout constraintLayout = (ConstraintLayout) inflate.findViewById(R.id.ly_popupchangeNumber);
        Button button = (Button) inflate.findViewById(R.id.btnCngNumberMinus);
        Button button2 = (Button) inflate.findViewById(R.id.btnCngNumberPlus);
        Button button3 = (Button) inflate.findViewById(R.id.btnCngNumberSave);
        final TextView textView2 = (TextView) inflate.findViewById(R.id.lblChangeNumber);
        Typeface.createFromAsset(getAssets(), "fonts/fontawesome.ttf");
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "fonts/AvenirNextCondensedUltraLight.ttf");
        Typeface createFromAsset2 = Typeface.createFromAsset(getAssets(), "fonts/icomoon.ttf");
        textView2.setTypeface(createFromAsset);
        button.setTypeface(createFromAsset2);
        button2.setTypeface(createFromAsset2);
        button3.setTypeface(avenir);
        button3.setTextColor(-1);
        button3.setBackground(getResources().getDrawable(R.drawable.btn_selector));
        if (this.typeActStyle == 2) {
            button3.setBackground(getResources().getDrawable(R.drawable.btn_selector_pico));
        }
        if (this.typeActStyle == 3) {
            button3.setBackground(getResources().getDrawable(R.drawable.btn_selector_vmc));
        }
        textView2.setText(str);
        if (i4 == 0) {
            String replace = str.replace("°", "");
            textView2.setText(replace + "°");
            this.temp = (int) Double.parseDouble(replace);
            int i5 = i2;
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (BaseActivity.this.temp > i2) {
                        BaseActivity baseActivity = BaseActivity.this;
                        baseActivity.temp--;
                    }
                    textView2.setText(String.valueOf(BaseActivity.this.temp) + "°");
                }
            });
            final int i6 = i3;
            button2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (BaseActivity.this.temp < i6) {
                        BaseActivity.this.temp++;
                    }
                    textView2.setText(String.valueOf(BaseActivity.this.temp) + "°");
                }
            });
        } else if (i4 == 1) {
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    String charSequence = textView2.getText().toString();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                    try {
                        Date parse = simpleDateFormat.parse(charSequence);
                        Calendar instance = Calendar.getInstance();
                        instance.setTime(parse);
                        instance.add(12, -15);
                        textView2.setText(simpleDateFormat.format(instance.getTime()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
            button2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    String charSequence = textView2.getText().toString();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
                    try {
                        Date parse = simpleDateFormat.parse(charSequence);
                        Calendar instance = Calendar.getInstance();
                        instance.setTime(parse);
                        instance.add(12, 15);
                        textView2.setText(simpleDateFormat.format(instance.getTime()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else if (i4 != 2) {
            return null;
        } else {
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    try {
                        Date parse = new SimpleDateFormat("hh:mm a").parse(textView2.getText().toString());
                        Calendar instance = Calendar.getInstance();
                        instance.setTime(parse);
                        instance.add(12, -15);
                        textView2.setText(instance.getTime().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
            button2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    try {
                        Date parse = new SimpleDateFormat("hh:mm a").parse(textView2.getText().toString());
                        Calendar instance = Calendar.getInstance();
                        instance.setTime(parse);
                        instance.add(12, 15);
                        textView2.setText(instance.getTime().toString());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
        final TextView textView3 = textView;
        final int i7 = i;
        final TextView textView4 = textView2;
        final Runnable runnable3 = runnable2;
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                TextView textView = textView3;
                if (textView == null) {
                    String replace = textView4.getText().toString().replace("°", "");
                    if (BaseActivity.this.cu.getUnitOfMesure() == 1) {
                        replace = Functions.fromFtoCInt(replace);
                    }
                    BaseActivity.this.tempcu.setT_can(Integer.valueOf(replace).intValue());
                } else if (i7 == 0) {
                    textView.setText(textView4.getText().toString());
                } else {
                    textView.setText(textView4.getText().toString());
                }
                runnable3.run();
            }
        });
        return constraintLayout;
    }

    public ConstraintLayout createActionItem(String str, String str2, final Runnable runnable2) {
        View inflate = LayoutInflater.from(this.activity).inflate(R.layout.popup_action, (ViewGroup) null);
        ConstraintLayout constraintLayout = (ConstraintLayout) inflate.findViewById(R.id.ly_popupAction);
        Button button = (Button) inflate.findViewById(R.id.btnAction);
        TextView textView = (TextView) inflate.findViewById(R.id.lblTxtToShow);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "fonts/AvenirNextCondensedUltraLight.ttf");
        Typeface.createFromAsset(getAssets(), "fonts/icomoon.ttf");
        textView.setTypeface(createFromAsset);
        button.setTypeface(avenir);
        button.setTextColor(-1);
        button.setText(str2);
        button.setBackground(getResources().getDrawable(R.drawable.btn_selector));
        if (this.typeActStyle == 2) {
            button.setBackground(getResources().getDrawable(R.drawable.btn_selector_pico));
        }
        if (this.typeActStyle == 3) {
            button.setBackground(getResources().getDrawable(R.drawable.btn_selector_vmc));
        }
        textView.setText(str);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                runnable2.run();
            }
        });
        return constraintLayout;
    }

    public ConstraintLayout createInfoItem(String str, String str2) {
        View inflate = LayoutInflater.from(this.activity).inflate(R.layout.popup_info, (ViewGroup) null);
        ConstraintLayout constraintLayout = (ConstraintLayout) inflate.findViewById(R.id.ly_popupInfo);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "fonts/AvenirNextCondensedUltraLight.ttf");
        TextView textView = (TextView) inflate.findViewById(R.id.lblMain);
        TextView textView2 = (TextView) inflate.findViewById(R.id.lblSec);
        textView.setTypeface(createFromAsset);
        textView2.setTypeface(avenir);
        textView.setText(str);
        textView2.setText(str2);
        return constraintLayout;
    }

    public ConstraintLayout createYesNoCancelItem(String str, String str2, String str3, String str4, final Runnable runnable2, final Runnable runnable3, final Runnable runnable4) {
        View inflate = LayoutInflater.from(this.activity).inflate(R.layout.popupyesnocancel, (ViewGroup) null);
        ConstraintLayout constraintLayout = (ConstraintLayout) inflate.findViewById(R.id.ly_yesnocancel);
        TextView textView = (TextView) inflate.findViewById(R.id.lblyesno);
        Button button = (Button) inflate.findViewById(R.id.btnyes);
        Button button2 = (Button) inflate.findViewById(R.id.btnno);
        Button button3 = (Button) inflate.findViewById(R.id.btncancel);
        textView.setText(str);
        textView.setTypeface(avenir);
        button2.setTypeface(avenir);
        button.setTypeface(avenir);
        button3.setTypeface(avenir);
        button.setText(str3);
        button2.setText(str2);
        button3.setText(str4);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                runnable2.run();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                runnable3.run();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                runnable4.run();
            }
        });
        if (str2 == null) {
            str2 = "";
        }
        if (str2.isEmpty()) {
            button2.setVisibility(8);
        }
        return constraintLayout;
    }

    public ConstraintLayout createYesNOItem(String str, String str2, String str3, final Runnable runnable2, final Runnable runnable3) {
        View inflate = LayoutInflater.from(this.activity).inflate(R.layout.popupalert, (ViewGroup) null);
        ConstraintLayout constraintLayout = (ConstraintLayout) inflate.findViewById(R.id.ly_yesno);
        TextView textView = (TextView) inflate.findViewById(R.id.lblyesno);
        Button button = (Button) inflate.findViewById(R.id.btnyes);
        Button button2 = (Button) inflate.findViewById(R.id.btnno);
        textView.setText(str);
        textView.setTypeface(avenir);
        button2.setTypeface(avenir);
        button.setTypeface(avenir);
        button.setText(str3);
        button2.setText(str2);
        if (runnable2 != null) {
            button2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    runnable2.run();
                }
            });
        } else {
            button2.setVisibility(8);
        }
        if (runnable3 != null) {
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    runnable3.run();
                }
            });
        } else {
            button.setVisibility(8);
        }
        if (str2 == null) {
            str2 = "";
        }
        if (str2.isEmpty()) {
            button2.setVisibility(8);
        }
        return constraintLayout;
    }

    public BundleMenuList createChangeNumberPopUp(String str, int i, String str2, int i2, int i3, Runnable runnable2, TextView textView) {
        MenuList menuList = new MenuList();
        ArrayList arrayList = new ArrayList();
        arrayList.add(createChangeNumberItem(i, str2, i2, i3, runnable2, textView));
        menuList.setLayouts(arrayList);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BUNDLE_MENU, Constants.AS_POPUP);
        String str3 = str;
        bundle.putString(Constants.BUNDLE_TITLE, str);
        return new BundleMenuList(bundle, menuList);
    }

    public BundleMenuList createActionPopUp(String str, String str2, String str3, Runnable runnable2) {
        MenuList menuList = new MenuList();
        ArrayList arrayList = new ArrayList();
        arrayList.add(createActionItem(str2, str3, runnable2));
        menuList.setLayouts(arrayList);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BUNDLE_MENU, Constants.AS_POPUP);
        bundle.putString(Constants.BUNDLE_TITLE, str);
        return new BundleMenuList(bundle, menuList);
    }

    public BundleMenuList createInfoPopUp(String str, String str2, String str3) {
        MenuList menuList = new MenuList();
        ArrayList arrayList = new ArrayList();
        arrayList.add(createInfoItem(str2, str3));
        menuList.setLayouts(arrayList);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BUNDLE_MENU, Constants.AS_POPUP);
        bundle.putString(Constants.BUNDLE_TITLE, str);
        return new BundleMenuList(bundle, menuList);
    }

    public BundleMenuList createPopUp(boolean z, String str, List<String> list2, List<String> list3, List<String> list4, List<Runnable> list5, int i, boolean z2) {
        int i2;
        List<String> list6 = list2;
        List<String> list7 = list3;
        List<String> list8 = list4;
        List<Runnable> list9 = list5;
        MenuList menuList = new MenuList();
        ArrayList arrayList = new ArrayList();
        boolean z3 = z;
        int i3 = 0;
        while (i3 < list2.size()) {
            String str2 = "";
            if (i3 == i) {
                String str3 = list6.get(i3);
                String str4 = list7.get(i3);
                if (list8 != null) {
                    str2 = list8.get(i3);
                }
                i2 = i3;
                arrayList.add(createMenuItem(z3, str3, str4, str2, list9.get(i3), true, z2));
            } else {
                i2 = i3;
                String str5 = list6.get(i2);
                String str6 = list7.get(i2);
                if (list8 != null) {
                    str2 = list8.get(i2);
                }
                arrayList.add(createMenuItem(z3, str5, str6, str2, list9.get(i2), false, z2));
            }
            if (z3) {
                z3 = false;
            }
            i3 = i2 + 1;
        }
        menuList.setLayouts(arrayList);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BUNDLE_MENU, Constants.AS_POPUP);
        bundle.putString(Constants.BUNDLE_TITLE, str);
        return new BundleMenuList(bundle, menuList);
    }

    public BundleMenuList createPopUpImg(boolean z, String str, List<String> list2, List<Integer> list3, List<Runnable> list4, int i, boolean z2) {
        List<String> list5 = list2;
        List<Integer> list6 = list3;
        List<Runnable> list7 = list4;
        MenuList menuList = new MenuList();
        ArrayList arrayList = new ArrayList();
        boolean z3 = z;
        for (int i2 = 0; i2 < list2.size(); i2++) {
            if (i2 == i) {
                arrayList.add(createMenuItemImage(z3, list5.get(i2), list6.get(i2).intValue(), list7.get(i2), true, z2));
            } else {
                arrayList.add(createMenuItemImage(z3, list5.get(i2), list6.get(i2).intValue(), list7.get(i2), false, z2));
            }
            if (z3) {
                z3 = false;
            }
        }
        menuList.setLayouts(arrayList);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BUNDLE_MENU, Constants.AS_POPUP);
        bundle.putString(Constants.BUNDLE_TITLE, str);
        return new BundleMenuList(bundle, menuList);
    }

    public BundleMenuList createTxtPopUp(String str, List<String> list2, String str2, List<TextView.OnEditorActionListener> list3) {
        return createTxtPopUp(str, list2, str2, list3, -1);
    }

    public BundleMenuList createTxtPopUp(String str, List<String> list2, String str2, List<TextView.OnEditorActionListener> list3, int i) {
        MenuList menuList = new MenuList();
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < list2.size(); i2++) {
            arrayList.add(createEditTextMenuItem(list2.get(i2), str2, list3.get(i2), i));
        }
        menuList.setLayouts(arrayList);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BUNDLE_MENU, Constants.AS_POPUP);
        bundle.putString(Constants.BUNDLE_TITLE, str);
        return new BundleMenuList(bundle, menuList);
    }

    public BundleMenuList createDataOraTimezonePopUp(String str, String str2, String str3) {
        MenuList menuList = new MenuList();
        ArrayList arrayList = new ArrayList();
        arrayList.add(createDataOraTimezoneItem(str2, str3));
        menuList.setLayouts(arrayList);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BUNDLE_MENU, Constants.AS_POPUP);
        bundle.putString(Constants.BUNDLE_TITLE, str);
        return new BundleMenuList(bundle, menuList);
    }

    public BundleMenuList createDataOraPopUp(String str, String str2, Runnable runnable2) {
        MenuList menuList = new MenuList();
        ArrayList arrayList = new ArrayList();
        arrayList.add(createDataOraItem(str2, runnable2));
        menuList.setLayouts(arrayList);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BUNDLE_MENU, Constants.AS_POPUP);
        bundle.putString(Constants.BUNDLE_TITLE, str);
        return new BundleMenuList(bundle, menuList);
    }

    public BundleMenuList createYesNoPopUp(String str, String str2, String str3, String str4, Runnable runnable2, Runnable runnable3) {
        MenuList menuList = new MenuList();
        ArrayList arrayList = new ArrayList();
        arrayList.add(createYesNOItem(str2, str3, str4, runnable2, runnable3));
        menuList.setLayouts(arrayList);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BUNDLE_MENU, Constants.AS_POPUP);
        bundle.putString(Constants.BUNDLE_TITLE, str);
        return new BundleMenuList(bundle, menuList);
    }

    public BundleMenuList createYesNoCancelPopUp(String str, String str2, String str3, String str4, String str5, Runnable runnable2, Runnable runnable3, Runnable runnable4) {
        MenuList menuList = new MenuList();
        ArrayList arrayList = new ArrayList();
        arrayList.add(createYesNoCancelItem(str2, str3, str4, str5, runnable2, runnable3, runnable4));
        menuList.setLayouts(arrayList);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BUNDLE_MENU, Constants.AS_POPUP);
        String str6 = str;
        bundle.putString(Constants.BUNDLE_TITLE, str);
        return new BundleMenuList(bundle, menuList);
    }

    public BundleMenuList createSetPin(final Runnable runnable2) {
        View inflate = LayoutInflater.from(this.activity).inflate(R.layout.popup_changepwd, (ViewGroup) null);
        inflate.findViewById(R.id.lblInfoPin).setVisibility(8);
        ((TextView) inflate.findViewById(R.id.lblcmTitle)).setTypeface(avenir);
        EditText editText = (EditText) inflate.findViewById(R.id.txtcm);
        this.txtPin = editText;
        editText.setTypeface(avenir);
        this.txtPin.setHint(getResources().getString(R.string.cu_pinHint));
        Button button = (Button) inflate.findViewById(R.id.btncmyes);
        Button button2 = (Button) inflate.findViewById(R.id.btncmno);
        button.setText(getResources().getString(R.string.general_OK));
        button2.setText(getResources().getString(R.string.ba_cancel));
        button.setTypeface(avenir);
        button2.setTypeface(avenir);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                BaseActivity.this.dismissdialog();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                runnable2.run();
            }
        });
        MenuList menuList = new MenuList();
        ArrayList arrayList = new ArrayList();
        arrayList.add((ConstraintLayout) inflate.findViewById(R.id.ly_change_pwd));
        menuList.setLayouts(arrayList);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BUNDLE_MENU, Constants.AS_POPUP);
        bundle.putString(Constants.BUNDLE_TITLE, getResources().getString(R.string.cu_pinRequest));
        return new BundleMenuList(bundle, menuList);
    }

    public BundleMenuList createGenarlPin(final Runnable runnable2, final Runnable runnable3) {
        View inflate = LayoutInflater.from(this.activity).inflate(R.layout.popup_changepwd, (ViewGroup) null);
        ((TextView) inflate.findViewById(R.id.lblcmTitle)).setTypeface(avenir);
        EditText editText = (EditText) inflate.findViewById(R.id.txtcm);
        this.txtPin2 = editText;
        editText.setHint(getResources().getString(R.string.bootloader_PINHint));
        this.txtPin2.setInputType(129);
        this.txtPin2.setTypeface(avenir);
        Button button = (Button) inflate.findViewById(R.id.btncmyes);
        Button button2 = (Button) inflate.findViewById(R.id.btncmno);
        TextView textView = (TextView) inflate.findViewById(R.id.lblInfoPin);
        textView.setVisibility(0);
        button.setText(getResources().getString(R.string.general_OK));
        button2.setText(getResources().getString(R.string.ba_cancel));
        button.setTypeface(avenir);
        button2.setTypeface(avenir);
        textView.setTypeface(avenir);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                runnable3.run();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                runnable2.run();
            }
        });
        MenuList menuList = new MenuList();
        ArrayList arrayList = new ArrayList();
        arrayList.add((ConstraintLayout) inflate.findViewById(R.id.ly_change_pwd));
        menuList.setLayouts(arrayList);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BUNDLE_MENU, Constants.AS_POPUP);
        bundle.putString(Constants.BUNDLE_TITLE, getResources().getString(R.string.bootloader_PINTitle));
        return new BundleMenuList(bundle, menuList);
    }

    public BundleMenuList createGenarlPin(final Runnable runnable2, final Runnable runnable3, String str, String str2, String str3) {
        View inflate = LayoutInflater.from(this.activity).inflate(R.layout.popup_changepwd, (ViewGroup) null);
        ((TextView) inflate.findViewById(R.id.lblcmTitle)).setTypeface(avenir);
        EditText editText = (EditText) inflate.findViewById(R.id.txtcm);
        this.txtPin2 = editText;
        editText.setHint(str2);
        this.txtPin2.setInputType(129);
        this.txtPin2.setTypeface(avenir);
        Button button = (Button) inflate.findViewById(R.id.btncmyes);
        Button button2 = (Button) inflate.findViewById(R.id.btncmno);
        TextView textView = (TextView) inflate.findViewById(R.id.lblInfoPin);
        textView.setVisibility(0);
        button.setText(getResources().getString(R.string.general_OK));
        button2.setText(getResources().getString(R.string.ba_cancel));
        button.setTypeface(avenir);
        button2.setTypeface(avenir);
        textView.setTypeface(avenir);
        textView.setText(str3);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                runnable3.run();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                runnable2.run();
            }
        });
        MenuList menuList = new MenuList();
        ArrayList arrayList = new ArrayList();
        arrayList.add((ConstraintLayout) inflate.findViewById(R.id.ly_change_pwd));
        menuList.setLayouts(arrayList);
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.BUNDLE_MENU, Constants.AS_POPUP);
        bundle.putString(Constants.BUNDLE_TITLE, str);
        return new BundleMenuList(bundle, menuList);
    }

    public void restartGetState(boolean z) {
        inizializeGetState(z);
        this.getState.start();
    }

    public void stopgetState() {
        try {
            Thread thread = this.getState;
            if (thread != null && thread.isAlive()) {
                this.interrupt = true;
                this.getState.interrupt();
            }
            Handler handler2 = this.handler;
            if (handler2 != null) {
                handler2.removeCallbacksAndMessages((Object) null);
            }
        } catch (Exception unused) {
        }
    }

    public void inizializeGetState(final boolean z) {
        if (this.getState != null) {
            stopgetState();
        }
        this.interrupt = false;
        this.gettingstate = false;
        this.handler = new Handler();
        this.getState = new Thread(new Runnable() {
            /* JADX WARNING: Can't wrap try/catch for region: R(5:13|14|15|16|17) */
            /* JADX WARNING: Can't wrap try/catch for region: R(5:20|21|22|23|24) */
            /* JADX WARNING: Missing exception handler attribute for start block: B:16:0x0068 */
            /* JADX WARNING: Missing exception handler attribute for start block: B:23:0x00b7 */
            /* JADX WARNING: Unknown top exception splitter block from list: {B:23:0x00b7=Splitter:B:23:0x00b7, B:16:0x0068=Splitter:B:16:0x0068} */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r6 = this;
                    it.tecnosystemi.TS.Activity.BaseActivity r0 = it.tecnosystemi.TS.Activity.BaseActivity.this
                    boolean r0 = r0.gettingstate
                    if (r0 != 0) goto L_0x00cf
                    it.tecnosystemi.TS.Activity.BaseActivity r0 = it.tecnosystemi.TS.Activity.BaseActivity.this
                    boolean r0 = r0.interrupt
                    if (r0 != 0) goto L_0x00cf
                    it.tecnosystemi.TS.Activity.BaseActivity r0 = it.tecnosystemi.TS.Activity.BaseActivity.this
                    boolean r0 = r0.sendingstate
                    if (r0 != 0) goto L_0x00cf
                    r0 = 0
                    boolean r1 = r3     // Catch:{ Exception -> 0x00c1 }
                    if (r1 != 0) goto L_0x0026
                    java.lang.Thread r1 = new java.lang.Thread     // Catch:{ Exception -> 0x00c1 }
                    it.tecnosystemi.TS.Activity.BaseActivity$33$1 r2 = new it.tecnosystemi.TS.Activity.BaseActivity$33$1     // Catch:{ Exception -> 0x00c1 }
                    r2.<init>()     // Catch:{ Exception -> 0x00c1 }
                    r1.<init>(r2)     // Catch:{ Exception -> 0x00c1 }
                    r1.start()     // Catch:{ Exception -> 0x00c1 }
                    goto L_0x00cf
                L_0x0026:
                    it.tecnosystemi.TS.Activity.BaseActivity r1 = it.tecnosystemi.TS.Activity.BaseActivity.this     // Catch:{ Exception -> 0x00c1 }
                    it.tecnosystemi.TS.Activity.BaseActivity r1 = r1.activity     // Catch:{ Exception -> 0x00c1 }
                    it.tecnosystemi.TS.Activity.BaseActivity r2 = it.tecnosystemi.TS.Activity.BaseActivity.this     // Catch:{ Exception -> 0x00c1 }
                    it.tecnosystemi.TS.Activity.BaseActivity r2 = r2.activity     // Catch:{ Exception -> 0x00c1 }
                    it.tecnosystemi.TS.Commands.MySocket.initInstance(r1, r2, r0)     // Catch:{ Exception -> 0x00c1 }
                    it.tecnosystemi.TS.Activity.BaseActivity r1 = it.tecnosystemi.TS.Activity.BaseActivity.this     // Catch:{ Exception -> 0x00c1 }
                    r2 = 1
                    r1.gettingstate = r2     // Catch:{ Exception -> 0x00c1 }
                    org.json.JSONObject r1 = new org.json.JSONObject     // Catch:{ Exception -> 0x00c1 }
                    java.lang.String r2 = "{}"
                    r1.<init>(r2)     // Catch:{ Exception -> 0x00c1 }
                    it.tecnosystemi.TS.Activity.BaseActivity r2 = it.tecnosystemi.TS.Activity.BaseActivity.this     // Catch:{ Exception -> 0x00c1 }
                    it.tecnosystemi.TS.Activity.BaseActivity r2 = r2.activity     // Catch:{ Exception -> 0x00c1 }
                    boolean r2 = r2 instanceof it.tecnosystemi.TS.Activity.ControlUnitActivity     // Catch:{ Exception -> 0x00c1 }
                    r3 = 0
                    if (r2 == 0) goto L_0x0072
                    java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_C     // Catch:{ Exception -> 0x00c1 }
                    java.lang.String r4 = "stato_r"
                    r1.put(r2, r4)     // Catch:{ Exception -> 0x00c1 }
                    java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_PIN     // Catch:{ Exception -> 0x00c1 }
                    it.tecnosystemi.TS.Activity.BaseActivity r4 = it.tecnosystemi.TS.Activity.BaseActivity.this     // Catch:{ Exception -> 0x00c1 }
                    it.tecnosystemi.TS.Model.ControlUnit r4 = r4.cu     // Catch:{ Exception -> 0x00c1 }
                    java.lang.String r4 = r4.getPinOffline()     // Catch:{ Exception -> 0x00c1 }
                    r1.put(r2, r4)     // Catch:{ Exception -> 0x00c1 }
                    java.lang.Thread r2 = new java.lang.Thread     // Catch:{ Exception -> 0x0068 }
                    it.tecnosystemi.TS.Activity.BaseActivity$33$2 r4 = new it.tecnosystemi.TS.Activity.BaseActivity$33$2     // Catch:{ Exception -> 0x0068 }
                    r4.<init>(r1)     // Catch:{ Exception -> 0x0068 }
                    r2.<init>(r4)     // Catch:{ Exception -> 0x0068 }
                    r2.start()     // Catch:{ Exception -> 0x0068 }
                    goto L_0x00cf
                L_0x0068:
                    it.tecnosystemi.TS.Activity.BaseActivity r1 = it.tecnosystemi.TS.Activity.BaseActivity.this     // Catch:{ Exception -> 0x00c1 }
                    r1.offlineResCU(r3)     // Catch:{ Exception -> 0x00c1 }
                    it.tecnosystemi.TS.Activity.BaseActivity r1 = it.tecnosystemi.TS.Activity.BaseActivity.this     // Catch:{ Exception -> 0x00c1 }
                    r1.gettingstate = r0     // Catch:{ Exception -> 0x00c1 }
                    goto L_0x00cf
                L_0x0072:
                    it.tecnosystemi.TS.Activity.BaseActivity r2 = it.tecnosystemi.TS.Activity.BaseActivity.this     // Catch:{ Exception -> 0x00c1 }
                    it.tecnosystemi.TS.Activity.BaseActivity r2 = r2.activity     // Catch:{ Exception -> 0x00c1 }
                    boolean r2 = r2 instanceof it.tecnosystemi.TS.Activity.ZoneActivity     // Catch:{ Exception -> 0x00c1 }
                    if (r2 == 0) goto L_0x00cf
                    java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_C     // Catch:{ Exception -> 0x00c1 }
                    java.lang.String r4 = "stato_zona"
                    r1.put(r2, r4)     // Catch:{ Exception -> 0x00c1 }
                    java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_PIN     // Catch:{ Exception -> 0x00c1 }
                    it.tecnosystemi.TS.Activity.BaseActivity r4 = it.tecnosystemi.TS.Activity.BaseActivity.this     // Catch:{ Exception -> 0x00c1 }
                    it.tecnosystemi.TS.Model.ControlUnit r4 = r4.cu     // Catch:{ Exception -> 0x00c1 }
                    java.lang.String r4 = r4.getPinOffline()     // Catch:{ Exception -> 0x00c1 }
                    r1.put(r2, r4)     // Catch:{ Exception -> 0x00c1 }
                    java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.JSON_OFFLINE_COMMAND_IDZONA     // Catch:{ Exception -> 0x00c1 }
                    it.tecnosystemi.TS.Activity.BaseActivity r4 = it.tecnosystemi.TS.Activity.BaseActivity.this     // Catch:{ Exception -> 0x00c1 }
                    it.tecnosystemi.TS.Model.ControlUnit r4 = r4.cu     // Catch:{ Exception -> 0x00c1 }
                    java.util.List r4 = r4.getZone()     // Catch:{ Exception -> 0x00c1 }
                    it.tecnosystemi.TS.Activity.BaseActivity r5 = it.tecnosystemi.TS.Activity.BaseActivity.this     // Catch:{ Exception -> 0x00c1 }
                    int r5 = r5.indexZona     // Catch:{ Exception -> 0x00c1 }
                    java.lang.Object r4 = r4.get(r5)     // Catch:{ Exception -> 0x00c1 }
                    it.tecnosystemi.TS.Model.Zona r4 = (it.tecnosystemi.TS.Model.Zona) r4     // Catch:{ Exception -> 0x00c1 }
                    int r4 = r4.getZoneId()     // Catch:{ Exception -> 0x00c1 }
                    r1.put(r2, r4)     // Catch:{ Exception -> 0x00c1 }
                    java.lang.Thread r2 = new java.lang.Thread     // Catch:{ Exception -> 0x00b7 }
                    it.tecnosystemi.TS.Activity.BaseActivity$33$3 r4 = new it.tecnosystemi.TS.Activity.BaseActivity$33$3     // Catch:{ Exception -> 0x00b7 }
                    r4.<init>(r1)     // Catch:{ Exception -> 0x00b7 }
                    r2.<init>(r4)     // Catch:{ Exception -> 0x00b7 }
                    r2.start()     // Catch:{ Exception -> 0x00b7 }
                    goto L_0x00cf
                L_0x00b7:
                    it.tecnosystemi.TS.Activity.BaseActivity r1 = it.tecnosystemi.TS.Activity.BaseActivity.this     // Catch:{ Exception -> 0x00c1 }
                    r1.offlineResZona(r3)     // Catch:{ Exception -> 0x00c1 }
                    it.tecnosystemi.TS.Activity.BaseActivity r1 = it.tecnosystemi.TS.Activity.BaseActivity.this     // Catch:{ Exception -> 0x00c1 }
                    r1.gettingstate = r0     // Catch:{ Exception -> 0x00c1 }
                    goto L_0x00cf
                L_0x00c1:
                    r1 = move-exception
                    java.lang.String r2 = "Error"
                    java.lang.String r1 = r1.toString()
                    android.util.Log.d(r2, r1)
                    it.tecnosystemi.TS.Activity.BaseActivity r1 = it.tecnosystemi.TS.Activity.BaseActivity.this
                    r1.gettingstate = r0
                L_0x00cf:
                    it.tecnosystemi.TS.Activity.BaseActivity r0 = it.tecnosystemi.TS.Activity.BaseActivity.this
                    boolean r0 = r0.interrupt
                    if (r0 != 0) goto L_0x00e1
                    it.tecnosystemi.TS.Activity.BaseActivity r0 = it.tecnosystemi.TS.Activity.BaseActivity.this
                    android.os.Handler r0 = r0.handler
                    it.tecnosystemi.TS.Activity.BaseActivity r1 = it.tecnosystemi.TS.Activity.BaseActivity.this
                    int r1 = r1.thread_sleep
                    long r1 = (long) r1
                    r0.postDelayed(r6, r1)
                L_0x00e1:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.BaseActivity.AnonymousClass33.run():void");
            }
        });
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0019 A[Catch:{ Exception -> 0x004e }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void offlineResCURistretto(java.lang.String r6) {
        /*
            r5 = this;
            r0 = 0
            r1 = 0
            if (r6 == 0) goto L_0x0015
            java.lang.String r2 = ""
            int r2 = r6.compareTo(r2)     // Catch:{ Exception -> 0x004e }
            if (r2 != 0) goto L_0x000f
            r5.gettingstate = r0     // Catch:{ Exception -> 0x004e }
            goto L_0x0015
        L_0x000f:
            org.json.JSONObject r2 = new org.json.JSONObject     // Catch:{ Exception -> 0x004e }
            r2.<init>(r6)     // Catch:{ Exception -> 0x004e }
            goto L_0x0016
        L_0x0015:
            r2 = r1
        L_0x0016:
            r6 = 1
            if (r2 == 0) goto L_0x0044
            r5.hideProgress()     // Catch:{ Exception -> 0x004e }
            java.lang.String r3 = it.tecnosystemi.TS.Utils.Constants.JSON_RES     // Catch:{ Exception -> 0x004e }
            boolean r3 = r2.has(r3)     // Catch:{ Exception -> 0x004e }
            if (r3 == 0) goto L_0x0045
            java.lang.String r3 = it.tecnosystemi.TS.Utils.Constants.JSON_RES     // Catch:{ Exception -> 0x004e }
            int r3 = r2.getInt(r3)     // Catch:{ Exception -> 0x004e }
            r4 = 4
            if (r3 != r4) goto L_0x003b
            java.lang.Thread r6 = new java.lang.Thread     // Catch:{ Exception -> 0x004e }
            it.tecnosystemi.TS.Activity.BaseActivity$34 r0 = new it.tecnosystemi.TS.Activity.BaseActivity$34     // Catch:{ Exception -> 0x004e }
            r0.<init>()     // Catch:{ Exception -> 0x004e }
            r6.<init>(r0)     // Catch:{ Exception -> 0x004e }
            r6.start()     // Catch:{ Exception -> 0x004e }
            return
        L_0x003b:
            java.lang.String r3 = it.tecnosystemi.TS.Utils.Constants.JSON_RES     // Catch:{ Exception -> 0x004e }
            int r3 = r2.getInt(r3)     // Catch:{ Exception -> 0x004e }
            if (r3 == r6) goto L_0x0044
            goto L_0x0045
        L_0x0044:
            r1 = r2
        L_0x0045:
            it.tecnosystemi.TS.Activity.BaseActivity r2 = r5.activity     // Catch:{ Exception -> 0x004e }
            it.tecnosystemi.TS.Activity.ControlUnitActivity r2 = (it.tecnosystemi.TS.Activity.ControlUnitActivity) r2     // Catch:{ Exception -> 0x004e }
            r2.getCuState(r1, r6)     // Catch:{ Exception -> 0x004e }
            r5.gettingstate = r0     // Catch:{ Exception -> 0x004e }
        L_0x004e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.BaseActivity.offlineResCURistretto(java.lang.String):void");
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0019 A[Catch:{ Exception -> 0x0037 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void offlineResCU(java.lang.String r5) {
        /*
            r4 = this;
            r0 = 1
            r1 = 0
            r2 = 0
            if (r5 == 0) goto L_0x0016
            java.lang.String r3 = ""
            int r3 = r5.compareTo(r3)     // Catch:{ Exception -> 0x0037 }
            if (r3 != 0) goto L_0x0010
            r4.gettingstate = r1     // Catch:{ Exception -> 0x0037 }
            goto L_0x0016
        L_0x0010:
            org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ Exception -> 0x0037 }
            r3.<init>(r5)     // Catch:{ Exception -> 0x0037 }
            goto L_0x0017
        L_0x0016:
            r3 = r2
        L_0x0017:
            if (r3 == 0) goto L_0x002d
            r4.hideProgress()     // Catch:{ Exception -> 0x0037 }
            java.lang.String r5 = it.tecnosystemi.TS.Utils.Constants.JSON_RES     // Catch:{ Exception -> 0x0037 }
            boolean r5 = r3.has(r5)     // Catch:{ Exception -> 0x0037 }
            if (r5 == 0) goto L_0x002c
            java.lang.String r5 = it.tecnosystemi.TS.Utils.Constants.JSON_RES     // Catch:{ Exception -> 0x0037 }
            int r5 = r3.getInt(r5)     // Catch:{ Exception -> 0x0037 }
            if (r5 == r0) goto L_0x002d
        L_0x002c:
            r3 = r2
        L_0x002d:
            it.tecnosystemi.TS.Activity.BaseActivity r5 = r4.activity     // Catch:{ Exception -> 0x0037 }
            it.tecnosystemi.TS.Activity.ControlUnitActivity r5 = (it.tecnosystemi.TS.Activity.ControlUnitActivity) r5     // Catch:{ Exception -> 0x0037 }
            r5.getCuState(r3, r0)     // Catch:{ Exception -> 0x0037 }
            r4.gettingstate = r1     // Catch:{ Exception -> 0x0037 }
            goto L_0x0040
        L_0x0037:
            r4.gettingstate = r1
            it.tecnosystemi.TS.Activity.BaseActivity r5 = r4.activity
            it.tecnosystemi.TS.Activity.ControlUnitActivity r5 = (it.tecnosystemi.TS.Activity.ControlUnitActivity) r5
            r5.getCuState(r2, r0)
        L_0x0040:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.BaseActivity.offlineResCU(java.lang.String):void");
    }

    public void offlineResZona(String str) {
        JSONObject jSONObject;
        if (str != null) {
            try {
                if (str.compareTo("") == 0) {
                    this.gettingstate = false;
                } else {
                    jSONObject = new JSONObject(str);
                    if (jSONObject != null && (!jSONObject.has(Constants.JSON_RES) || jSONObject.getInt(Constants.JSON_RES) != 1)) {
                        jSONObject = null;
                    }
                    ((ZoneActivity) this.activity).getZoneState(jSONObject);
                    this.gettingstate = false;
                }
            } catch (Exception unused) {
                this.gettingstate = false;
                ((ZoneActivity) this.activity).getZoneState((JSONObject) null);
                return;
            }
        }
        jSONObject = null;
        jSONObject = null;
        ((ZoneActivity) this.activity).getZoneState(jSONObject);
        this.gettingstate = false;
    }

    public void pinerror() {
        if (this.changepinempty) {
            if (this.cu.isOffline()) {
                this.cu.setPinOffline("");
            } else {
                this.cu.setPin("");
            }
            ControlUnit.saveCuInPref(this.cu, this);
            return;
        }
        this.changepinempty = true;
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i == 12345) {
            connectToWifi(this.runnable, this.redhome, false);
            hideProgress();
        }
    }

    public String getCurretWifiName() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) getApplicationContext().getSystemService("connectivity")).getActiveNetworkInfo();
        if (Build.VERSION.SDK_INT > 27) {
            try {
                WifiInfo connectionInfo = ((WifiManager) this.activity.getApplicationContext().getSystemService(Constants.INTENT_WIFI)).getConnectionInfo();
                connectionInfo.getSSID();
                return connectionInfo.getSSID();
            } catch (Exception unused) {
            }
        } else {
            if (activeNetworkInfo != null) {
                return activeNetworkInfo.getExtraInfo();
            }
            return "";
        }
    }

    private boolean hasGPSDevice(Context context) {
        List<String> allProviders;
        LocationManager locationManager = (LocationManager) context.getSystemService(FirebaseAnalytics.Param.LOCATION);
        if (locationManager == null || (allProviders = locationManager.getAllProviders()) == null) {
            return false;
        }
        return allProviders.contains("gps");
    }

    private void enableLocation() {
        LocationRequest create = LocationRequest.create();
        create.setPriority(100);
        LocationServices.getSettingsClient((Activity) this).checkLocationSettings(new LocationSettingsRequest.Builder().addLocationRequest(create).build()).addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse result = task.getResult(ApiException.class);
                    BaseActivity baseActivity = BaseActivity.this;
                    baseActivity.connectToWifi(baseActivity.runnable, BaseActivity.this.redhome, true);
                } catch (ApiException e) {
                    if (e.getStatusCode() == 6) {
                        try {
                            ((ResolvableApiException) e).startResolutionForResult(BaseActivity.this.activity, 100);
                        } catch (IntentSender.SendIntentException | ClassCastException unused) {
                        }
                    }
                }
            }
        });
    }

    public void connectToWifi(Runnable runnable2, boolean z, boolean z2) {
        connectToWifi(runnable2, (Runnable) null, z, z2);
    }

    public void connectToWifi(Runnable runnable2, Runnable runnable3, boolean z, boolean z2) {
        final Runnable runnable4 = runnable2;
        final Runnable runnable5 = runnable3;
        final boolean z3 = z;
        final boolean z4 = z2;
        new Thread(new Runnable() {
            public void run() {
                BaseActivity.this.connectToWifi_(runnable4, runnable5, z3, z4);
            }
        }).start();
    }

    public Network getCurrentWifiNetwork() {
        ConnectivityManager connectivityManager2 = (ConnectivityManager) getSystemService("connectivity");
        int i = 0;
        if (Build.VERSION.SDK_INT >= 23) {
            Network[] m = connectivityManager2.getAllNetworks();
            int length = m.length;
            while (i < length) {
                Network network = m[i];
                NetworkCapabilities m2 = Functions$$ExternalSyntheticApiModelOutline0.m(connectivityManager2, network);
                if (m2 != null && m2.hasTransport(1)) {
                    return network;
                }
                i++;
            }
            return null;
        }
        NetworkInfo activeNetworkInfo = connectivityManager2.getActiveNetworkInfo();
        if (activeNetworkInfo == null || !activeNetworkInfo.isConnected() || activeNetworkInfo.getType() != 1) {
            return null;
        }
        Network[] m3 = connectivityManager2.getAllNetworks();
        int length2 = m3.length;
        while (i < length2) {
            Network network2 = m3[i];
            NetworkInfo m4 = Functions$$ExternalSyntheticApiModelOutline0.m(connectivityManager2, network2);
            if (m4 != null && m4.getType() == 1 && m4.isConnected()) {
                return network2;
            }
            i++;
        }
        return null;
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(13:17|18|19|(1:21)|22|23|(1:25)|26|(1:32)|34|(1:36)|37|38) */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:117:0x0220 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:22:0x0068 */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x006c A[Catch:{ Exception -> 0x009a }] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x00a4  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void connectToWifi_(java.lang.Runnable r9, final java.lang.Runnable r10, boolean r11, boolean r12) {
        /*
            r8 = this;
            java.lang.String r0 = "CONN_PICO"
            java.lang.String r1 = "Connesione A Wifi"
            android.util.Log.d(r0, r1)
            r0 = 0
            onLostWifi = r0
            r8.showProgress()
            r8.runnable = r9
            r8.redhome = r11
            boolean r9 = r8.manualconn
            if (r9 == 0) goto L_0x0024
            r8.hideProgress()     // Catch:{ Exception -> 0x0019 }
            goto L_0x001a
        L_0x0019:
        L_0x001a:
            java.lang.Runnable r9 = r8.runnable
            if (r9 == 0) goto L_0x0021
            r9.run()
        L_0x0021:
            r8.manualconn = r0
            return
        L_0x0024:
            java.lang.String r9 = "location"
            java.lang.Object r9 = r8.getSystemService(r9)
            android.location.LocationManager r9 = (android.location.LocationManager) r9
            java.lang.String r11 = "gps"
            boolean r9 = r9.isProviderEnabled(r11)
            if (r9 != 0) goto L_0x003e
            if (r12 != 0) goto L_0x003e
            java.lang.Runnable r9 = r8.runnable
            r8.runnable = r9
            r8.enableLocation()
            return
        L_0x003e:
            java.lang.String r9 = r8.getCurretWifiName()
            java.lang.String r11 = "\""
            java.lang.String r12 = ""
            java.lang.String r9 = r9.replace(r11, r12)
            java.lang.String r11 = toConnSid
            boolean r9 = r9.equals(r11)
            java.lang.String r11 = "connectivity"
            java.lang.String r1 = "POLARIS_UPDATE"
            r2 = 1
            if (r9 == 0) goto L_0x00aa
            java.lang.String r9 = "WIFI"
            java.lang.String r10 = "GIà CONNESSO"
            android.util.Log.d(r9, r10)
            java.lang.String r9 = toConnSid     // Catch:{ Exception -> 0x0068 }
            boolean r9 = r9.equals(r1)     // Catch:{ Exception -> 0x0068 }
            if (r9 == 0) goto L_0x0068
            it.tecnosystemi.TS.Activity.BootloaderActivity.MANUALCONN = r2     // Catch:{ Exception -> 0x0068 }
        L_0x0068:
            android.net.ConnectivityManager r9 = r8.connectivityManager     // Catch:{ Exception -> 0x009a }
            if (r9 != 0) goto L_0x0078
            android.content.Context r9 = r8.getApplicationContext()     // Catch:{ Exception -> 0x009a }
            java.lang.Object r9 = r9.getSystemService(r11)     // Catch:{ Exception -> 0x009a }
            android.net.ConnectivityManager r9 = (android.net.ConnectivityManager) r9     // Catch:{ Exception -> 0x009a }
            r8.connectivityManager = r9     // Catch:{ Exception -> 0x009a }
        L_0x0078:
            int r9 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x009a }
            r10 = 23
            if (r9 < r10) goto L_0x009b
            it.tecnosystemi.TS.Activity.BaseActivity r9 = ShowingActivity     // Catch:{ Exception -> 0x009a }
            boolean r9 = r9 instanceof it.tecnosystemi.TS.Activity.BootloaderActivity     // Catch:{ Exception -> 0x009a }
            if (r9 == 0) goto L_0x008a
            int r9 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x009a }
            r10 = 34
            if (r9 >= r10) goto L_0x009b
        L_0x008a:
            android.net.Network r9 = r8.getCurrentWifiNetwork()     // Catch:{ Exception -> 0x009a }
            android.net.ConnectivityManager r10 = r8.connectivityManager     // Catch:{ Exception -> 0x009a }
            it.tecnosystemi.TS.Utils.Functions$$ExternalSyntheticApiModelOutline0.m((android.net.ConnectivityManager) r10, (android.net.Network) r9)     // Catch:{ Exception -> 0x009a }
            it.tecnosystemi.TS.Commands.MySocket.tobindnet = r9     // Catch:{ Exception -> 0x009a }
            it.tecnosystemi.TS.Commands.MySocketBootLoader.tobindnet = r9     // Catch:{ Exception -> 0x009a }
            it.tecnosystemi.TS.Commands.PicoSocketBootloader.tobindnet = r9     // Catch:{ Exception -> 0x009a }
            goto L_0x009b
        L_0x009a:
        L_0x009b:
            r8.hideProgress()
            r8.errorcollegamento = r0
            java.lang.Runnable r9 = r8.runnable
            if (r9 == 0) goto L_0x00a7
            r9.run()
        L_0x00a7:
            r8.manualconn = r0
            return
        L_0x00aa:
            boolean r9 = triedonce
            r3 = 500(0x1f4, double:2.47E-321)
            if (r9 == 0) goto L_0x011d
            boolean r9 = r8 instanceof it.tecnosystemi.TS.Activity.BootloaderActivity
            if (r9 != 0) goto L_0x011d
            boolean r9 = r8 instanceof it.tecnosystemi.TS.Activity.IstrBootloaderActivity
            if (r9 != 0) goto L_0x011d
            boolean r9 = r8 instanceof it.tecnosystemi.TS.Activity.PICO.Config.CheckLedPICOActivity
            if (r9 != 0) goto L_0x011d
            int r9 = r8.typeActStyle
            if (r9 == 0) goto L_0x011d
            boolean r9 = r8 instanceof it.tecnosystemi.TS.Activity.PICO.Config.SetNameAndPinPICOActivity
            if (r9 != 0) goto L_0x011d
            boolean r9 = r8 instanceof it.tecnosystemi.TS.Activity.PICO.PicoActivity
            if (r9 != 0) goto L_0x011d
            boolean r9 = r8 instanceof it.tecnosystemi.TS.Activity.PICO.Config.EnableWifiVCActivityPICO
            if (r9 != 0) goto L_0x011d
            boolean r9 = r8 instanceof it.tecnosystemi.TS.Activity.VMC.VMCActivity
            if (r9 != 0) goto L_0x011d
            boolean r9 = r8 instanceof it.tecnosystemi.TS.Activity.PICO.Config.ConfigPICOActivity
            if (r9 != 0) goto L_0x011d
            java.lang.Class r9 = r8.getClass()
            java.lang.String r9 = r9.getName()
            java.lang.String r5 = "it.tecnosystemi.TS.Activity.VMC"
            boolean r9 = r9.startsWith(r5)
            if (r9 != 0) goto L_0x011d
            java.lang.Class r9 = r8.getClass()
            java.lang.String r9 = r9.getName()
            java.lang.String r5 = "it.tecnosystemi.TS.Activity.SEIX"
            boolean r9 = r9.startsWith(r5)
            if (r9 != 0) goto L_0x011d
            java.lang.Thread.sleep(r3)     // Catch:{ Exception -> 0x011c }
            it.tecnosystemi.TS.Activity.BaseActivity r9 = r8.activity     // Catch:{ Exception -> 0x011c }
            it.tecnosystemi.TS.Commands.MySocket.initInstance(r9, r9, r0)     // Catch:{ Exception -> 0x011c }
            java.lang.String r9 = it.tecnosystemi.TS.Utils.Constants.ip     // Catch:{ Exception -> 0x011c }
            int r5 = it.tecnosystemi.TS.Utils.Constants.port     // Catch:{ Exception -> 0x011c }
            java.lang.String r9 = it.tecnosystemi.TS.Commands.MySocket.checkPinCmd(r12, r9, r5)     // Catch:{ Exception -> 0x011c }
            if (r9 == 0) goto L_0x0119
            boolean r9 = r9.isEmpty()     // Catch:{ Exception -> 0x011c }
            if (r9 != 0) goto L_0x0119
            r8.connected = r2     // Catch:{ Exception -> 0x011c }
            r8.hideProgress()     // Catch:{ Exception -> 0x011c }
            java.lang.Runnable r9 = r8.runnable     // Catch:{ Exception -> 0x011c }
            if (r9 == 0) goto L_0x0118
            r9.run()     // Catch:{ Exception -> 0x011c }
        L_0x0118:
            return
        L_0x0119:
            r8.connected = r0     // Catch:{ Exception -> 0x011c }
            goto L_0x011d
        L_0x011c:
        L_0x011d:
            boolean r9 = r8.asckedpermissionGps
            if (r9 != 0) goto L_0x0137
            java.lang.String r9 = "android.permission.ACCESS_FINE_LOCATION"
            int r12 = androidx.core.app.ActivityCompat.checkSelfPermission(r8, r9)
            if (r12 == 0) goto L_0x0137
            r8.asckedpermissionGps = r2
            r8.manualconn = r0
            java.lang.String[] r9 = new java.lang.String[]{r9}
            r10 = 12345(0x3039, float:1.7299E-41)
            androidx.core.app.ActivityCompat.requestPermissions(r8, r9, r10)
            return
        L_0x0137:
            java.lang.String r9 = toConnSid
            toConnSid = r9
            java.lang.String r12 = toConnPwd
            toConnPwd = r12
            boolean r9 = r9.equals(r1)
            if (r9 == 0) goto L_0x0147
            it.tecnosystemi.TS.Activity.BootloaderActivity.MANUALCONN = r0
        L_0x0147:
            r8.connected = r0
            it.tecnosystemi.TS.Activity.BaseActivity r9 = r8.getActivity()
            android.content.Context r9 = r9.getApplicationContext()
            java.lang.String r12 = "wifi"
            java.lang.Object r9 = r9.getSystemService(r12)
            android.net.wifi.WifiManager r9 = (android.net.wifi.WifiManager) r9
            r8.wifiManager = r9
            int r9 = android.os.Build.VERSION.SDK_INT
            r12 = 29
            if (r9 < r12) goto L_0x0238
            android.net.wifi.WifiManager r9 = r8.wifiManager
            boolean r9 = r9.isWifiEnabled()
            if (r9 != 0) goto L_0x0179
            r8.manualconn = r0
            android.content.Intent r9 = new android.content.Intent
            java.lang.String r10 = "android.settings.panel.action.WIFI"
            r9.<init>(r10)
            r8.startActivity(r9)
            r8.hideProgress()
            return
        L_0x0179:
            it.tecnosystemi.TS.Activity.BaseActivity$37 r9 = new it.tecnosystemi.TS.Activity.BaseActivity$37
            r9.<init>()
            android.content.IntentFilter r12 = new android.content.IntentFilter
            r12.<init>()
            java.lang.String r1 = "android.net.wifi.SCAN_RESULTS"
            r12.addAction(r1)
            it.tecnosystemi.TS.Activity.BaseActivity r1 = r8.activity
            r1.registerReceiver(r9, r12)
            r8.finishedscan = r0
            r12 = 0
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x01c1 }
            r5 = 31
            if (r1 < r5) goto L_0x01bb
            android.net.wifi.WifiManager r1 = r8.wifiManager     // Catch:{ Exception -> 0x01c1 }
            java.util.List r1 = r1.getScanResults()     // Catch:{ Exception -> 0x01c1 }
            java.util.Iterator r1 = r1.iterator()     // Catch:{ Exception -> 0x01c1 }
        L_0x01a0:
            boolean r5 = r1.hasNext()     // Catch:{ Exception -> 0x01c1 }
            if (r5 == 0) goto L_0x01b8
            java.lang.Object r5 = r1.next()     // Catch:{ Exception -> 0x01c1 }
            android.net.wifi.ScanResult r5 = (android.net.wifi.ScanResult) r5     // Catch:{ Exception -> 0x01c1 }
            java.lang.String r6 = r5.SSID     // Catch:{ Exception -> 0x01c1 }
            java.lang.String r7 = toConnSid     // Catch:{ Exception -> 0x01c1 }
            boolean r6 = r6.equals(r7)     // Catch:{ Exception -> 0x01c1 }
            if (r6 == 0) goto L_0x01a0
            java.lang.String r12 = r5.BSSID     // Catch:{ Exception -> 0x01c1 }
        L_0x01b8:
            r8.finishedscan = r2     // Catch:{ Exception -> 0x01c1 }
            goto L_0x01c3
        L_0x01bb:
            android.net.wifi.WifiManager r1 = r8.wifiManager     // Catch:{ Exception -> 0x01c1 }
            r1.startScan()     // Catch:{ Exception -> 0x01c1 }
            goto L_0x01c3
        L_0x01c1:
            r8.finishedscan = r2
        L_0x01c3:
            boolean r1 = r8.finishedscan     // Catch:{ Exception -> 0x01d2 }
            if (r1 != 0) goto L_0x01d3
            int r0 = r0 + r2
            r5 = 1000(0x3e8, double:4.94E-321)
            java.lang.Thread.sleep(r5)     // Catch:{ Exception -> 0x01d2 }
            r1 = 15
            if (r0 <= r1) goto L_0x01c3
            goto L_0x01d3
        L_0x01d2:
        L_0x01d3:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r8.activity
            r0.unregisterReceiver(r9)
            android.net.wifi.WifiNetworkSpecifier$Builder r9 = new android.net.wifi.WifiNetworkSpecifier$Builder
            r9.<init>()
            java.lang.String r0 = toConnSid
            android.net.wifi.WifiNetworkSpecifier.Builder unused = r9.setSsid(r0)
            if (r12 == 0) goto L_0x01eb
            android.net.MacAddress r12 = android.net.MacAddress.fromString(r12)
            android.net.wifi.WifiNetworkSpecifier.Builder unused = r9.setBssid(r12)
        L_0x01eb:
            java.lang.String r12 = toConnPwd
            android.net.wifi.WifiNetworkSpecifier.Builder unused = r9.setWpa2Passphrase(r12)
            android.net.wifi.WifiNetworkSpecifier r9 = r9.build()
            android.net.NetworkRequest$Builder r12 = new android.net.NetworkRequest$Builder
            r12.<init>()
            r0 = 12
            android.net.NetworkRequest.Builder unused = r12.removeCapability(r0)
            android.net.NetworkRequest.Builder unused = r12.addTransportType(r2)
            android.net.NetworkRequest.Builder unused = r12.setNetworkSpecifier(r9)
            android.net.NetworkRequest r9 = r12.build()
            java.lang.Object r11 = r8.getSystemService(r11)
            android.net.ConnectivityManager r11 = (android.net.ConnectivityManager) r11
            android.os.Handler r12 = mHandler
            if (r12 == 0) goto L_0x0220
            java.lang.String r12 = "NETWORK"
            java.lang.String r0 = "cancello handler"
            android.util.Log.d(r12, r0)     // Catch:{ Exception -> 0x0220 }
            android.os.Handler r12 = mHandler     // Catch:{ Exception -> 0x0220 }
            java.lang.Runnable r0 = mDelayedRunnable     // Catch:{ Exception -> 0x0220 }
            r12.removeCallbacks(r0)     // Catch:{ Exception -> 0x0220 }
        L_0x0220:
            android.net.ConnectivityManager$NetworkCallback r12 = networkCallback     // Catch:{ Exception -> 0x0228 }
            r11.unregisterNetworkCallback(r12)     // Catch:{ Exception -> 0x0228 }
            java.lang.Thread.sleep(r3)     // Catch:{ Exception -> 0x0228 }
        L_0x0228:
            triedonce = r2
            it.tecnosystemi.TS.Activity.BaseActivity$38 r12 = new it.tecnosystemi.TS.Activity.BaseActivity$38
            r12.<init>(r10)
            networkCallback = r12
            r10 = 120000(0x1d4c0, float:1.68156E-40)
            r11.requestNetwork(r9, r12, r10)
            goto L_0x029a
        L_0x0238:
            r8.manualconn = r2     // Catch:{ Exception -> 0x029a }
            java.lang.Runnable r9 = r8.runnable     // Catch:{ Exception -> 0x029a }
            r8.runnable = r9     // Catch:{ Exception -> 0x029a }
            it.tecnosystemi.TS.Utils.WifiChangeBroadcastReceiver r9 = new it.tecnosystemi.TS.Utils.WifiChangeBroadcastReceiver     // Catch:{ Exception -> 0x029a }
            r9.<init>(r8)     // Catch:{ Exception -> 0x029a }
            r8.wifiStateChangeReceiver = r9     // Catch:{ Exception -> 0x029a }
            android.content.IntentFilter r11 = new android.content.IntentFilter     // Catch:{ Exception -> 0x029a }
            java.lang.String r12 = "android.net.wifi.STATE_CHANGE"
            r11.<init>(r12)     // Catch:{ Exception -> 0x029a }
            r8.registerReceiver(r9, r11)     // Catch:{ Exception -> 0x029a }
            android.net.wifi.WifiManager r9 = r8.wifiManager     // Catch:{ Exception -> 0x029a }
            boolean r9 = r9.isWifiEnabled()     // Catch:{ Exception -> 0x029a }
            if (r9 != 0) goto L_0x025c
            android.net.wifi.WifiManager r9 = r8.wifiManager     // Catch:{ Exception -> 0x029a }
            r9.setWifiEnabled(r2)     // Catch:{ Exception -> 0x029a }
        L_0x025c:
            android.net.wifi.WifiConfiguration r9 = r8.buildWifiConfig()     // Catch:{ Exception -> 0x029a }
            r11 = 2
            r9.status = r11     // Catch:{ Exception -> 0x029a }
            java.lang.String r11 = toConnSid     // Catch:{ Exception -> 0x029a }
            int r11 = r8.getIdForConfiguredNetwork(r11)     // Catch:{ Exception -> 0x029a }
            r8.networkId = r11     // Catch:{ Exception -> 0x029a }
            r12 = -1
            if (r11 != r12) goto L_0x0276
            android.net.wifi.WifiManager r11 = r8.wifiManager     // Catch:{ Exception -> 0x029a }
            int r9 = r11.addNetwork(r9)     // Catch:{ Exception -> 0x029a }
            r8.networkId = r9     // Catch:{ Exception -> 0x029a }
        L_0x0276:
            int r9 = r8.networkId     // Catch:{ Exception -> 0x029a }
            STATIC_networkId = r9     // Catch:{ Exception -> 0x029a }
            android.net.wifi.WifiManager r9 = r8.wifiManager     // Catch:{ Exception -> 0x029a }
            r9.disconnect()     // Catch:{ Exception -> 0x029a }
            android.net.wifi.WifiManager r9 = r8.wifiManager     // Catch:{ Exception -> 0x029a }
            int r11 = r8.networkId     // Catch:{ Exception -> 0x029a }
            r9.enableNetwork(r11, r2)     // Catch:{ Exception -> 0x029a }
            android.net.wifi.WifiManager r9 = r8.wifiManager     // Catch:{ Exception -> 0x029a }
            r9.reconnect()     // Catch:{ Exception -> 0x029a }
            java.util.Timer r9 = new java.util.Timer     // Catch:{ Exception -> 0x029a }
            r9.<init>()     // Catch:{ Exception -> 0x029a }
            it.tecnosystemi.TS.Activity.BaseActivity$39 r11 = new it.tecnosystemi.TS.Activity.BaseActivity$39     // Catch:{ Exception -> 0x029a }
            r11.<init>(r10)     // Catch:{ Exception -> 0x029a }
            r0 = 30000(0x7530, double:1.4822E-319)
            r9.schedule(r11, r0)     // Catch:{ Exception -> 0x029a }
        L_0x029a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.BaseActivity.connectToWifi_(java.lang.Runnable, java.lang.Runnable, boolean, boolean):void");
    }

    /* access modifiers changed from: private */
    public void AvaiableNetwork(Network network) {
        Log.d("NETWORK", "AvaiableNetwork");
        this.manualconn = false;
        this.onlost = 0;
        Functions$$ExternalSyntheticApiModelOutline0.m((ConnectivityManager) getSystemService("connectivity"), network);
        MySocket.tobindnet = network;
        MySocketBootLoader.tobindnet = network;
        PicoSocketBootloader.tobindnet = network;
        this.connected = true;
        continueTrie = false;
        if (MySocketBootLoader.lastFWPK <= 0 || bootloaderActivity == null) {
            Runnable runnable2 = this.runnable;
            if (runnable2 != null) {
                runnable2.run();
                this.runnable = null;
                return;
            }
            hideProgress();
        } else if (Build.VERSION.SDK_INT < 34) {
            bootloaderActivity.runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        BaseActivity.bootloaderActivity.enableView();
                    } catch (Exception unused) {
                    }
                }
            });
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        try {
            unregisterReceiver(this.wifiStateChangeReceiver);
        } catch (Exception unused) {
        }
    }

    public void disconnectFromWIfi() {
        if (Build.VERSION.SDK_INT < 29) {
            try {
                this.wifiManager.removeNetwork(STATIC_networkId);
                this.wifiManager.saveConfiguration();
                this.wifiManager.disconnect();
                Thread.sleep(500);
                this.wifiManager.reconnect();
            } catch (Exception unused) {
            }
        } else {
            ConnectivityManager connectivityManager2 = (ConnectivityManager) getSystemService("connectivity");
            ConnectivityManager.NetworkCallback networkCallback2 = networkCallback;
            if (networkCallback2 != null) {
                connectivityManager2.unregisterNetworkCallback(networkCallback2);
            }
            Functions$$ExternalSyntheticApiModelOutline0.m(connectivityManager2, (Network) null);
            networkCallback = null;
        }
    }

    public WifiConfiguration buildWifiConfig() {
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = "\"" + toConnSid + "\"";
        wifiConfiguration.preSharedKey = "\"" + toConnPwd + "\"";
        wifiConfiguration.priority = 999999;
        return wifiConfiguration;
    }

    public void onWifiChangeBroadcastReceived(Context context, Intent intent) {
        WifiConfiguration buildWifiConfig = buildWifiConfig();
        WifiInfo wifiInfo = (WifiInfo) intent.getParcelableExtra("wifiInfo");
        if (wifiInfo != null && wifiInfo.getSSID() != null && wifiInfo.getSSID().equals(buildWifiConfig.SSID)) {
            bindProcessToNetwork();
            this.connected = true;
            Runnable runnable2 = this.runnable;
            if (runnable2 != null) {
                runnable2.run();
            }
        }
    }

    public int getIdForConfiguredNetwork(String str) {
        if (ActivityCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != 0) {
            return -1;
        }
        for (WifiConfiguration next : this.wifiManager.getConfiguredNetworks()) {
            if (next.SSID.equals(str)) {
                return next.networkId;
            }
        }
        return -1;
    }

    public void bindProcessToNetwork() {
        this.connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService("connectivity");
        if (Build.VERSION.SDK_INT >= 21) {
            Network networkObjectForCurrentWifiConnection = getNetworkObjectForCurrentWifiConnection();
            if (Build.VERSION.SDK_INT >= 23) {
                MySocket.tobindnet = networkObjectForCurrentWifiConnection;
            } else {
                boolean unused = ConnectivityManager.setProcessDefaultNetwork(networkObjectForCurrentWifiConnection);
            }
        }
    }

    /* JADX WARNING: Can't wrap try/catch for region: R(6:0|1|2|3|4|(4:6|(1:8)(1:9)|10|12)(1:14)) */
    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        return;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0005 */
    /* JADX WARNING: Removed duplicated region for block: B:14:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:6:0x000b A[Catch:{ Exception -> 0x002f }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void unbidNetwork() {
        /*
            r3 = this;
            r0 = 0
            it.tecnosystemi.TS.Commands.MySocket.tobindnet = r0     // Catch:{ Exception -> 0x0005 }
            it.tecnosystemi.TS.Commands.MySocketBootLoader.tobindnet = r0     // Catch:{ Exception -> 0x0005 }
        L_0x0005:
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x002f }
            r2 = 21
            if (r1 < r2) goto L_0x002f
            android.content.Context r1 = r3.getApplicationContext()     // Catch:{ Exception -> 0x002f }
            java.lang.String r2 = "connectivity"
            java.lang.Object r1 = r1.getSystemService(r2)     // Catch:{ Exception -> 0x002f }
            android.net.ConnectivityManager r1 = (android.net.ConnectivityManager) r1     // Catch:{ Exception -> 0x002f }
            r3.connectivityManager = r1     // Catch:{ Exception -> 0x002f }
            int r1 = android.os.Build.VERSION.SDK_INT     // Catch:{ Exception -> 0x002f }
            r2 = 23
            if (r1 < r2) goto L_0x0025
            android.net.ConnectivityManager r1 = r3.connectivityManager     // Catch:{ Exception -> 0x002f }
            it.tecnosystemi.TS.Utils.Functions$$ExternalSyntheticApiModelOutline0.m((android.net.ConnectivityManager) r1, (android.net.Network) r0)     // Catch:{ Exception -> 0x002f }
            goto L_0x0028
        L_0x0025:
            boolean unused = android.net.ConnectivityManager.setProcessDefaultNetwork(r0)     // Catch:{ Exception -> 0x002f }
        L_0x0028:
            android.net.ConnectivityManager r0 = r3.connectivityManager     // Catch:{ Exception -> 0x002f }
            android.net.ConnectivityManager$NetworkCallback r1 = networkCallback     // Catch:{ Exception -> 0x002f }
            r0.unregisterNetworkCallback(r1)     // Catch:{ Exception -> 0x002f }
        L_0x002f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.BaseActivity.unbidNetwork():void");
    }

    public Network getNetworkObjectForCurrentWifiConnection() {
        for (Object m : Arrays.asList(this.connectivityManager.getAllNetworks())) {
            Network m2 = Functions$$ExternalSyntheticApiModelOutline0.m(m);
            if (Functions$$ExternalSyntheticApiModelOutline0.m(this.connectivityManager, m2).hasTransport(1)) {
                return m2;
            }
        }
        return null;
    }

    public void errorcollWifi() {
        hideProgress();
        this.activity.runOnUiThread(new Runnable() {
            public void run() {
                Functions.makeErrorToast(BaseActivity.this.activity, BaseActivity.this.getResources().getString(R.string.ha_apAssente));
            }
        });
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (this.redhome) {
            try {
                this.activity.startActivity(new Intent("android.settings.WIFI_SETTINGS"));
            } catch (Exception unused) {
            }
        } else {
            this.activity.startActivity(new Intent("android.settings.WIFI_SETTINGS"));
        }
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        super.onDestroy();
        if (this.networkId >= 0) {
            if (this.wifiManager == null) {
                this.wifiManager = (WifiManager) this.activity.getApplicationContext().getSystemService(Constants.INTENT_WIFI);
            }
            this.wifiManager.removeNetwork(this.networkId);
            this.wifiManager.saveConfiguration();
        }
        picoActDestoryed = this instanceof PicoActivity;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0075, code lost:
        if (r0 >= r2.getTime()) goto L_0x00aa;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onResume() {
        /*
            r7 = this;
            super.onResume()
            ShowingActivity = r7
            r0 = 0
            r7.background = r0
            boolean r0 = r7.hideloading
            if (r0 == 0) goto L_0x0010
            r7.hideProgress()
            goto L_0x0013
        L_0x0010:
            r0 = 1
            r7.hideloading = r0
        L_0x0013:
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity
            boolean r0 = r0 instanceof it.tecnosystemi.TS.Activity.BootloaderActivity
            if (r0 == 0) goto L_0x001a
            return
        L_0x001a:
            boolean r0 = it.tecnosystemi.TS.Utils.Constants.ISDEMO
            if (r0 != 0) goto L_0x00aa
            it.tecnosystemi.TS.Activity.BaseActivity r0 = r7.activity
            boolean r1 = r0 instanceof it.tecnosystemi.TS.Activity.SignUpActivity
            if (r1 != 0) goto L_0x00aa
            boolean r1 = r0 instanceof it.tecnosystemi.TS.Activity.ChangePwdActivity
            if (r1 != 0) goto L_0x00aa
            boolean r1 = r0 instanceof it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity
            if (r1 != 0) goto L_0x00aa
            boolean r0 = r0 instanceof it.tecnosystemi.TS.Activity.GDPRActivity
            if (r0 != 0) goto L_0x00aa
            it.tecnosystemi.TS.Utils.SavePreferences r0 = new it.tecnosystemi.TS.Utils.SavePreferences     // Catch:{ Exception -> 0x00a9 }
            int r1 = it.tecnosystemi.TS.R.string.PrefsName     // Catch:{ Exception -> 0x00a9 }
            java.lang.String r1 = r7.getString(r1)     // Catch:{ Exception -> 0x00a9 }
            r0.<init>(r7, r1)     // Catch:{ Exception -> 0x00a9 }
            pref = r0     // Catch:{ Exception -> 0x00a9 }
            java.lang.String r1 = "tokendate"
            java.lang.String r0 = r0.getString(r1)     // Catch:{ Exception -> 0x00a9 }
            boolean r1 = r0.isEmpty()     // Catch:{ Exception -> 0x00a9 }
            if (r1 != 0) goto L_0x00aa
            java.lang.Long r0 = java.lang.Long.valueOf(r0)     // Catch:{ Exception -> 0x00a9 }
            long r0 = r0.longValue()     // Catch:{ Exception -> 0x00a9 }
            java.util.Date r2 = new java.util.Date     // Catch:{ Exception -> 0x00a9 }
            r2.<init>()     // Catch:{ Exception -> 0x00a9 }
            long r3 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x00a9 }
            r5 = 3600000(0x36ee80, double:1.7786363E-317)
            long r3 = r3 - r5
            r2.setTime(r3)     // Catch:{ Exception -> 0x00a9 }
            java.lang.String r3 = it.tecnosystemi.TS.Utils.Constants.token     // Catch:{ Exception -> 0x00a9 }
            java.lang.String r4 = ""
            if (r3 == 0) goto L_0x0077
            java.lang.String r3 = it.tecnosystemi.TS.Utils.Constants.token     // Catch:{ Exception -> 0x00a9 }
            boolean r3 = r3.equals(r4)     // Catch:{ Exception -> 0x00a9 }
            if (r3 != 0) goto L_0x0077
            long r2 = r2.getTime()     // Catch:{ Exception -> 0x00a9 }
            int r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r5 >= 0) goto L_0x00aa
        L_0x0077:
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.token     // Catch:{ Exception -> 0x00a9 }
            if (r0 == 0) goto L_0x0090
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.token     // Catch:{ Exception -> 0x00a9 }
            boolean r0 = r0.equals(r4)     // Catch:{ Exception -> 0x00a9 }
            if (r0 != 0) goto L_0x0090
            android.content.res.Resources r0 = r7.getResources()     // Catch:{ Exception -> 0x00a9 }
            int r1 = it.tecnosystemi.TS.R.string.sessioneScaduta     // Catch:{ Exception -> 0x00a9 }
            java.lang.String r0 = r0.getString(r1)     // Catch:{ Exception -> 0x00a9 }
            it.tecnosystemi.TS.Utils.Functions.makeNormalToast(r7, r0)     // Catch:{ Exception -> 0x00a9 }
        L_0x0090:
            it.tecnosystemi.TS.Utils.Constants.token = r4     // Catch:{ Exception -> 0x00a9 }
            boolean r0 = r7 instanceof it.tecnosystemi.TS.Activity.LoginActivity     // Catch:{ Exception -> 0x00a9 }
            if (r0 != 0) goto L_0x00aa
            android.content.Intent r0 = new android.content.Intent     // Catch:{ Exception -> 0x00a9 }
            java.lang.Class<it.tecnosystemi.TS.Activity.LoginActivity> r1 = it.tecnosystemi.TS.Activity.LoginActivity.class
            r0.<init>(r7, r1)     // Catch:{ Exception -> 0x00a9 }
            r1 = 67108864(0x4000000, float:1.5046328E-36)
            r0.addFlags(r1)     // Catch:{ Exception -> 0x00a9 }
            r7.startActivity(r0)     // Catch:{ Exception -> 0x00a9 }
            r7.finish()     // Catch:{ Exception -> 0x00a9 }
            return
        L_0x00a9:
        L_0x00aa:
            boolean r0 = r7 instanceof it.tecnosystemi.TS.Activity.ControlUnitActivity
            if (r0 != 0) goto L_0x00ba
            boolean r0 = r7 instanceof it.tecnosystemi.TS.Activity.ZoneActivity
            if (r0 != 0) goto L_0x00ba
            boolean r0 = r7 instanceof it.tecnosystemi.TS.Activity.CronoSetActivity
            if (r0 != 0) goto L_0x00ba
            boolean r0 = r7 instanceof it.tecnosystemi.TS.Activity.CronoSummaryActivity
            if (r0 == 0) goto L_0x0106
        L_0x00ba:
            boolean r0 = it.tecnosystemi.TS.Utils.Constants.ISDEMO
            if (r0 != 0) goto L_0x0106
            it.tecnosystemi.TS.Model.ControlUnit r0 = r7.cu
            if (r0 == 0) goto L_0x0106
            boolean r0 = r0.isOffline()
            if (r0 == 0) goto L_0x0106
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            java.lang.String r1 = "POLARIS_"
            r0.<init>(r1)
            it.tecnosystemi.TS.Model.ControlUnit r1 = r7.cu
            java.lang.String r1 = r1.getSerial()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            java.lang.String r2 = "TS_"
            r1.<init>(r2)
            it.tecnosystemi.TS.Model.ControlUnit r2 = r7.cu
            java.lang.String r2 = r2.getSerial()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            it.tecnosystemi.TS.Activity.BaseActivity$42 r2 = new it.tecnosystemi.TS.Activity.BaseActivity$42
            r2.<init>()
            toConnPwd = r1
            toConnSid = r0
            java.lang.Thread r0 = new java.lang.Thread
            it.tecnosystemi.TS.Activity.BaseActivity$43 r1 = new it.tecnosystemi.TS.Activity.BaseActivity$43
            r1.<init>(r2)
            r0.<init>(r1)
            r0.start()
        L_0x0106:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.BaseActivity.onResume():void");
    }

    public void onBackPressed() {
        super.onBackPressed();
    }

    /* access modifiers changed from: protected */
    public void onPause() {
        super.onPause();
        this.background = true;
        try {
            this.pausecercacentralina = true;
        } catch (Exception unused) {
        }
        MenuFragment menuFragment = this.myDialogFragment;
        if (menuFragment != null && menuFragment.isAdded()) {
            dismissdialog();
        }
    }

    public void showProgress() {
        try {
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    ((ConstraintLayout) BaseActivity.ShowingActivity.findViewById(R.id.ly_progress)).setVisibility(0);
                }
            });
        } catch (Exception unused) {
        }
    }

    public void hideProgress() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 3) {
            StackTraceElement stackTraceElement = stackTrace[3];
            Log.d("hideProgress", "Metodo chiamato da: " + stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName());
        }
        try {
            this.activity.runOnUiThread(new Runnable() {
                /* JADX WARNING: Failed to process nested try/catch */
                /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x000f */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void run() {
                    /*
                        r3 = this;
                        r0 = 8
                        it.tecnosystemi.TS.Activity.BaseActivity r1 = it.tecnosystemi.TS.Activity.BaseActivity.this     // Catch:{ Exception -> 0x000f }
                        int r2 = it.tecnosystemi.TS.R.id.ly_progress     // Catch:{ Exception -> 0x000f }
                        android.view.View r1 = r1.findViewById(r2)     // Catch:{ Exception -> 0x000f }
                        androidx.constraintlayout.widget.ConstraintLayout r1 = (androidx.constraintlayout.widget.ConstraintLayout) r1     // Catch:{ Exception -> 0x000f }
                        r1.setVisibility(r0)     // Catch:{ Exception -> 0x000f }
                    L_0x000f:
                        it.tecnosystemi.TS.Activity.BaseActivity r1 = it.tecnosystemi.TS.Activity.BaseActivity.ShowingActivity     // Catch:{ Exception -> 0x001c }
                        int r2 = it.tecnosystemi.TS.R.id.ly_progress     // Catch:{ Exception -> 0x001c }
                        android.view.View r1 = r1.findViewById(r2)     // Catch:{ Exception -> 0x001c }
                        androidx.constraintlayout.widget.ConstraintLayout r1 = (androidx.constraintlayout.widget.ConstraintLayout) r1     // Catch:{ Exception -> 0x001c }
                        r1.setVisibility(r0)     // Catch:{ Exception -> 0x001c }
                    L_0x001c:
                        return
                    */
                    throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.BaseActivity.AnonymousClass45.run():void");
                }
            });
        } catch (Exception unused) {
        }
    }

    public class BundleMenuList {
        public Bundle bundle;
        public MenuList ml;

        public BundleMenuList(Bundle bundle2, MenuList menuList) {
            this.bundle = bundle2;
            this.ml = menuList;
        }

        public BundleMenuList() {
        }
    }

    public Response makeApiCall(String str, String str2, int i, int i2, String str3, boolean z) {
        return makeApiCall(str, str2, i, i2, str3, z, 5);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0048, code lost:
        return r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized it.tecnosystemi.TS.Model.Response makeApiCall(java.lang.String r10, java.lang.String r11, int r12, int r13, java.lang.String r14, boolean r15, int r16) {
        /*
            r9 = this;
            monitor-enter(r9)
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.token     // Catch:{ all -> 0x0049 }
            if (r0 == 0) goto L_0x0018
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Constants.token     // Catch:{ all -> 0x0049 }
            java.lang.String r1 = ""
            if (r0 == r1) goto L_0x0018
            if (r15 != 0) goto L_0x0018
            it.tecnosystemi.TS.Utils.SavePreferences r0 = pref     // Catch:{ all -> 0x0049 }
            android.content.res.Resources r1 = r9.getResources()     // Catch:{ all -> 0x0049 }
            java.lang.String r0 = it.tecnosystemi.TS.Utils.Functions.calcNewToken(r0, r1)     // Catch:{ all -> 0x0049 }
            goto L_0x001a
        L_0x0018:
            java.lang.String r0 = "Ga5mM61KCm5Bk18lhD5J999jC2Mu0Vaf"
        L_0x001a:
            r3 = r0
            r1 = r10
            r2 = r11
            r4 = r14
            r5 = r12
            r6 = r13
            it.tecnosystemi.TS.Model.Response r0 = it.tecnosystemi.TS.Threads.WebClientDevWrapper.getNewHttpClient(r1, r2, r3, r4, r5, r6)     // Catch:{ all -> 0x0049 }
            if (r0 == 0) goto L_0x0047
            int r1 = r0.getHttpResponceCode()     // Catch:{ all -> 0x0049 }
            r2 = 401(0x191, float:5.62E-43)
            if (r1 != r2) goto L_0x0047
            if (r16 <= 0) goto L_0x0044
            int r8 = r16 + -1
            r0 = 50
            java.lang.Thread.sleep(r0)     // Catch:{ Exception -> 0x0037 }
        L_0x0037:
            r1 = r9
            r2 = r10
            r3 = r11
            r4 = r12
            r5 = r13
            r6 = r14
            r7 = r15
            it.tecnosystemi.TS.Model.Response r0 = r1.makeApiCall(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ all -> 0x0049 }
            monitor-exit(r9)
            return r0
        L_0x0044:
            r9.unauth()     // Catch:{ all -> 0x0049 }
        L_0x0047:
            monitor-exit(r9)
            return r0
        L_0x0049:
            r0 = move-exception
            monitor-exit(r9)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.BaseActivity.makeApiCall(java.lang.String, java.lang.String, int, int, java.lang.String, boolean, int):it.tecnosystemi.TS.Model.Response");
    }

    private void unauth() {
        if (!(this instanceof LoginActivity)) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Functions.makeNormalToast(BaseActivity.this.activity, BaseActivity.this.activity.getResources().getString(R.string.sessioneScaduta));
                    Intent intent = new Intent(BaseActivity.this.activity, LoginActivity.class);
                    intent.addFlags(67108864);
                    BaseActivity.this.activity.startActivity(intent);
                }
            });
        }
    }
}
