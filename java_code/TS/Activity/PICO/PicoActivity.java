package it.tecnosystemi.TS.Activity.PICO;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import com.google.gson.Gson;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Activity.PICO.Config.CheckLedPICOActivity;
import it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity;
import it.tecnosystemi.TS.Commands.CmdPICO;
import it.tecnosystemi.TS.Commands.PICOServer;
import it.tecnosystemi.TS.Commands.PICOServerTimezone;
import it.tecnosystemi.TS.Commands.Protocols;
import it.tecnosystemi.TS.Commands.UDPSocket;
import it.tecnosystemi.TS.Model.Device;
import it.tecnosystemi.TS.Model.Device_OP;
import it.tecnosystemi.TS.Model.Pico;
import it.tecnosystemi.TS.Model.Response;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Threads.ThreadDowloadFirmWare;
import it.tecnosystemi.TS.Threads.ThreadWebService;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.DataClass;
import it.tecnosystemi.TS.Utils.Functions;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PicoActivity extends BaseActivity {
    public static final int MODE_AUTO7 = 10;
    public static final int MODE_AUTO8 = 11;
    public static final int MODE_CO2_ESTRAZIONE = 9;
    public static final int MODE_CO2_RECUPERATORE = 8;
    public static final int MODE_COMFORT_ESTATE = 6;
    public static final int MODE_COMFORT_INVERNO = 7;
    public static final int MODE_ESTRAZIONE = 2;
    public static final int MODE_IMMISSIONE = 3;
    public static final int MODE_RECUPERATORE = 1;
    public static final int MODE_RIC_NAT = 12;
    public static final int MODE_UM1 = 4;
    public static final int MODE_UM2 = 5;
    private static int idp;
    static Date lastErrShow;
    static Date lastReceivedStato;
    public static Pico pico;
    static Timer timerStato;
    public static Pico tmpPico;
    PicoActivity activity;
    boolean asckedPin = false;
    BaseActivity.BundleMenuList bundlePopUp;
    boolean changingvalue = false;
    int colorDisable;
    int colorTextDisable;
    int cronoOn = 0;
    Device devtodel;
    boolean errcon = false;
    boolean error4001 = false;
    boolean first = true;
    boolean firstCheck = false;
    boolean gotofasce = false;
    Handler handlerAQI;
    int hasslave = 0;
    ImageView img_InOut;
    ImageView img_night;
    ImageView img_otherchoise1;
    ImageView img_otherchoise2;
    ImageView img_vent1;
    ImageView img_vent2;
    ImageView img_vent3;
    boolean isFasce = false;
    boolean isRunningAQI = false;
    ImageView iv_btnOnOff;
    Date lastHideMan = null;
    Date lastRetry;
    String lastStato;
    HashMap<ConstraintLayout, PicoBTNOBJ> layouts;
    TextView lblAquiCO2;
    TextView lblAquiHum;
    TextView lblAquiInOut;
    TextView lblStatoBello;
    TextView lblStatoDebug;
    TextView lblTitleDebug;
    TextView lbl_icon_freccia1;
    TextView lbl_icon_freccia2;
    TextView lbl_icon_freccia3;
    TextView lbl_otherchoise1;
    TextView lbl_otherchoise2;
    int ledacceso = 0;
    List<ConstraintLayout> listlayouts;
    ConstraintLayout lyDebug;
    ConstraintLayout lyHum;
    ConstraintLayout lyModes;
    ConstraintLayout lyVEntVel;
    ConstraintLayout ly_aqi_aqi;
    ConstraintLayout ly_aqi_co2;
    ConstraintLayout ly_co2;
    ConstraintLayout ly_comfort;
    ConstraintLayout ly_estrazione;
    ConstraintLayout ly_immissione;
    ConstraintLayout ly_otherchoise;
    ConstraintLayout ly_otherchoise1;
    ConstraintLayout ly_otherchoise2;
    ConstraintLayout ly_recupero;
    ConstraintLayout ly_ric_nat;
    ConstraintLayout ly_umidita1;
    ConstraintLayout ly_umidita2;
    ConstraintLayout ly_ventole;
    int oldtipeQualita = -1;
    boolean pausastato = false;
    boolean picoCMD = false;
    Runnable runnableAQI;
    SeekBar sb_speed;
    boolean showMenuFasce = false;
    boolean showingInside = true;
    int times = 0;
    String verfw = null;
    View vw_aqi_div2;
    View vw_aqi_div3;

    public BaseActivity getActivity() {
        return this;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_pico);
        this.typeActStyle = 2;
        this.activity = this;
        picoActDestoryed = false;
        idp = 0;
        pico = (Pico) getIntent().getSerializableExtra(Constants.INTENT_PICO);
        if (Constants.ISDEMO) {
            Pico pico2 = DataClass.getInstance(this).pico_list.get(Constants.DEMO_PICO_INDEX);
            pico = pico2;
            this.hasslave = pico2.getHas_slave();
            this.ledacceso = pico.getLed();
        }
        tmpPico = pico.getCopy();
        super.onCreate(bundle);
        setUpGui();
        updateView();
        UDPSocket.setStatoActivity(this);
        this.handlerAQI = new Handler();
        this.runnableAQI = new Runnable() {
            public void run() {
                if (PicoActivity.this.showingInside) {
                    PicoActivity.this.showOutsideQualita(true);
                } else {
                    PicoActivity.this.showInsideQualita(true);
                }
                if (PicoActivity.this.isRunningAQI) {
                    PicoActivity.this.handlerAQI.postDelayed(this, 5000);
                }
            }
        };
    }

    /* access modifiers changed from: private */
    public void updateStatoLabel(final String str) {
        runOnUiThread(new Runnable() {
            public void run() {
                try {
                    JSONObject jSONObject = new JSONObject(str);
                    if (!PicoActivity.pico.getOffline().booleanValue()) {
                        jSONObject = new JSONObject(jSONObject.getString("lastpk"));
                    }
                    PicoActivity.this.lblStatoDebug.setText(jSONObject.toString(2));
                    StringBuilder sb = new StringBuilder("FW: ");
                    sb.append(jSONObject.get("fw_ver").toString());
                    sb.append(", ");
                    sb.append(jSONObject.get("fw_note").toString());
                    sb.append("\nTemperatura interna: ");
                    if (jSONObject.has("AMB_tmpr")) {
                        sb.append(jSONObject.get("AMB_tmpr").toString());
                    } else {
                        sb.append("---");
                    }
                    sb.append("\nTemperatura esterna: ");
                    if (jSONObject.has("EXT_tmpr")) {
                        sb.append(jSONObject.get("EXT_tmpr").toString());
                    } else {
                        sb.append("---");
                    }
                    sb.append("\nTemperatura letta: ");
                    if (jSONObject.has("v_tmpr")) {
                        sb.append(jSONObject.get("v_tmpr").toString());
                    } else {
                        sb.append("---");
                    }
                    sb.append("\nVelocità: ");
                    if (jSONObject.has("spd_row")) {
                        sb.append(jSONObject.get("spd_row").toString());
                    } else {
                        sb.append("---");
                    }
                    PicoActivity.this.lblStatoBello.setText(sb.toString());
                } catch (Exception unused) {
                }
            }
        });
    }

    public void btnSetVelDebug(View view) {
        try {
            tmpPico.setSpeed_raw(Integer.parseInt(((EditText) findViewById(R.id.editTextNumber2)).getText().toString()));
            updatePico(5);
        } catch (Exception unused) {
        }
    }

    public void updateView() {
        if (pico.getOff() == null) {
            pico.setOff(true);
            pico.setPCOM_Id(-1L);
            tmpPico = pico.getCopy();
        }
        runOnUiThread(new Runnable() {
            public void run() {
                boolean z;
                if (PicoActivity.pico.getHas_slave() != PicoActivity.this.hasslave) {
                    PicoActivity.this.hasslave = PicoActivity.pico.getHas_slave();
                    z = true;
                } else {
                    z = false;
                }
                if (PicoActivity.pico.getLed() != PicoActivity.this.ledacceso) {
                    PicoActivity.this.ledacceso = PicoActivity.pico.getLed();
                    z = true;
                }
                if (PicoActivity.pico.getVr() == 3) {
                    if (!PicoActivity.this.showMenuFasce) {
                        PicoActivity.this.showMenuFasce = true;
                        z = true;
                    }
                    if (PicoActivity.pico.getM_crono() != PicoActivity.this.cronoOn) {
                        PicoActivity.this.cronoOn = PicoActivity.pico.getM_crono();
                        z = true;
                    }
                    if (PicoActivity.pico.getM_crono() != 1) {
                        PicoActivity.this.iv_btnOnOff.setImageDrawable(PicoActivity.this.getResources().getDrawable(R.drawable.on_off));
                    } else if (PicoActivity.pico.getTw_active() > 0) {
                        PicoActivity.this.iv_btnOnOff.setImageDrawable(PicoActivity.this.getResources().getDrawable(R.drawable.clock));
                    } else {
                        PicoActivity.this.iv_btnOnOff.setImageDrawable(PicoActivity.this.getResources().getDrawable(R.drawable.calendar));
                    }
                } else {
                    if (PicoActivity.this.showMenuFasce) {
                        PicoActivity.this.showMenuFasce = false;
                        z = true;
                    }
                    PicoActivity.this.iv_btnOnOff.setImageDrawable(PicoActivity.this.getResources().getDrawable(R.drawable.on_off));
                }
                if (z) {
                    PicoActivity.this.updatemenu();
                }
                if (PicoActivity.pico.getVr() == 1) {
                    PicoActivity.this.ly_aqi_co2.setVisibility(8);
                    PicoActivity.this.ly_aqi_aqi.setVisibility(8);
                    PicoActivity.this.vw_aqi_div2.setVisibility(8);
                    PicoActivity.this.vw_aqi_div3.setVisibility(8);
                    PicoActivity.this.ly_umidita2.setVisibility(8);
                    PicoActivity.this.ly_comfort.setVisibility(8);
                    PicoActivity.this.ly_co2.setVisibility(8);
                    PicoActivity.this.ly_ric_nat.setLayoutParams(PicoActivity.this.ly_umidita2.getLayoutParams());
                } else {
                    PicoActivity.this.ly_umidita2.setVisibility(0);
                    PicoActivity.this.ly_comfort.setVisibility(0);
                    PicoActivity.this.ly_co2.setVisibility(0);
                    PicoActivity.this.ly_aqi_co2.setVisibility(0);
                    PicoActivity.this.ly_aqi_aqi.setVisibility(0);
                    PicoActivity.this.vw_aqi_div2.setVisibility(0);
                    PicoActivity.this.vw_aqi_div3.setVisibility(0);
                }
                PicoActivity.this.lyModes.setVisibility(0);
                PicoActivity.this.setErrorsMan();
                if (PicoActivity.pico.getOff().booleanValue()) {
                    PicoActivity.this.disableAllLayout();
                    PicoActivity.this.iv_btnOnOff.setColorFilter(ContextCompat.getColor(PicoActivity.this.activity, R.color.grayPicoColor), PorterDuff.Mode.SRC_IN);
                    PicoActivity.this.enableMode(-1, -1);
                    PicoActivity.this.hideVentole();
                    return;
                }
                PicoActivity.this.iv_btnOnOff.setColorFilter(ContextCompat.getColor(PicoActivity.this.activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                PicoActivity.this.enableAllLayout();
                if (PicoActivity.pico.isNight()) {
                    PicoActivity.this.modenight();
                } else {
                    PicoActivity.this.modeNonNight();
                }
                switch ((int) PicoActivity.pico.getPCOM_Id().longValue()) {
                    case 1:
                        PicoActivity.this.enableMode(0, 1);
                        PicoActivity.this.showVentole();
                        PicoActivity.this.showQualAria(3);
                        return;
                    case 2:
                        PicoActivity.this.enableMode(1, 1);
                        PicoActivity.this.showVentole();
                        PicoActivity.this.showQualAria(1);
                        return;
                    case 3:
                        PicoActivity.this.enableMode(2, 1);
                        PicoActivity.this.showVentole();
                        PicoActivity.this.showQualAria(2);
                        return;
                    case 4:
                        PicoActivity.this.enableMode(3, 1);
                        PicoActivity.this.showHum();
                        PicoActivity.this.showQualAria(3);
                        return;
                    case 5:
                        PicoActivity.this.enableMode(3, 2);
                        PicoActivity.this.showHum();
                        PicoActivity.this.showQualAria(1);
                        return;
                    case 6:
                        PicoActivity.this.enableMode(5, 1);
                        PicoActivity.this.showVentole();
                        PicoActivity.this.showQualAria(3);
                        return;
                    case 7:
                        PicoActivity.this.enableMode(5, 2);
                        PicoActivity.this.showVentole();
                        PicoActivity.this.showQualAria(3);
                        return;
                    case 8:
                        PicoActivity.this.enableMode(6, 1);
                        PicoActivity.this.hideVentole();
                        PicoActivity.this.showQualAria(3);
                        return;
                    case 9:
                        PicoActivity.this.enableMode(6, 2);
                        PicoActivity.this.hideVentole();
                        PicoActivity.this.showQualAria(1);
                        return;
                    case 10:
                        PicoActivity.this.enableMode(4, 1);
                        PicoActivity.this.showHum();
                        PicoActivity.this.showQualAria(3);
                        return;
                    case 11:
                        PicoActivity.this.enableMode(4, 2);
                        PicoActivity.this.showHum();
                        PicoActivity.this.showQualAria(1);
                        return;
                    case 12:
                        PicoActivity.this.enableMode(7, 1);
                        PicoActivity.this.hideVentole();
                        PicoActivity.this.showQualAria(3);
                        return;
                    default:
                        PicoActivity.this.enableMode(-1, 1);
                        PicoActivity.this.hideVentole();
                        return;
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void showQualAria(int i) {
        int i2 = this.oldtipeQualita;
        if (i2 != 3 || i != 3) {
            if (i != i2) {
                this.oldtipeQualita = i;
            }
            if (i != 3) {
                stopHandlerInOut();
            }
            if (i == 1) {
                showInsideQualita(false);
            } else if (i == 2) {
                showOutsideQualita(false);
            } else {
                startHandlerInOut();
            }
        }
    }

    private void showCoseFasce() {
        if (!this.showMenuFasce) {
            this.showMenuFasce = true;
            updatemenu();
        }
    }

    private void hideCoseFasce() {
        if (this.showMenuFasce) {
            this.showMenuFasce = false;
            updatemenu();
        }
    }

    private void startHandlerInOut() {
        if (!Constants.ISDEMO && !this.isRunningAQI) {
            this.isRunningAQI = true;
            this.handlerAQI.post(this.runnableAQI);
        }
    }

    public void stopHandlerInOut() {
        this.isRunningAQI = false;
        this.handlerAQI.removeCallbacks(this.runnableAQI);
    }

    /* access modifiers changed from: private */
    public void showInsideQualita(boolean z) {
        if (pico.getPar_amb() != null && pico.getPar_amb().size() != 0) {
            this.showingInside = true;
            this.lblAquiInOut.setText(getResources().getString(R.string.pm_qa_Inside));
            this.img_InOut.setImageDrawable(getResources().getDrawable(R.drawable.inside));
            if (z) {
                this.lyHum.setVisibility(4);
            } else {
                this.lyHum.setVisibility(0);
                if (pico.getPar_amb().get(1) == null || pico.getPar_amb().get(1).intValue() == -999) {
                    this.lblAquiHum.setText(getResources().getString(R.string.icon_fa_refresh));
                    this.lblAquiHum.setTypeface(fontawesome);
                    this.lblAquiHum.setGravity(17);
                } else {
                    TextView textView = this.lblAquiHum;
                    textView.setText((pico.getPar_amb().get(1).intValue() / 100) + " " + getResources().getString(R.string.pm_qa_RH));
                    this.lblAquiHum.setTypeface(avenir);
                    this.lblAquiHum.setGravity(19);
                }
            }
            if (pico.getPar_amb().get(3) == null || pico.getPar_amb().get(3).intValue() == -999 || pico.getPar_amb().get(3).intValue() < 30000 || pico.getPar_amb().get(3).intValue() > 300000) {
                this.lblAquiCO2.setText(getResources().getString(R.string.icon_fa_refresh));
                this.lblAquiCO2.setTypeface(fontawesome);
                this.lblAquiCO2.setGravity(17);
            } else {
                TextView textView2 = this.lblAquiCO2;
                textView2.setText((pico.getPar_amb().get(3).intValue() / 100) + " " + getResources().getString(R.string.pm_qa_PPM));
                this.lblAquiCO2.setTypeface(avenir);
                this.lblAquiCO2.setGravity(19);
            }
            showFreccieQualita(pico.getPar_amb().get(2));
        }
    }

    /* access modifiers changed from: private */
    public void showOutsideQualita(boolean z) {
        if (pico.getPar_ext() != null && pico.getPar_ext().size() != 0) {
            this.showingInside = false;
            this.lblAquiInOut.setText(getResources().getString(R.string.pm_qa_Outside));
            this.img_InOut.setImageDrawable(getResources().getDrawable(R.drawable.outside));
            if (z) {
                this.lyHum.setVisibility(4);
            } else {
                this.lyHum.setVisibility(0);
                if (pico.getPar_ext().get(1) == null || pico.getPar_ext().get(1).intValue() == -999) {
                    this.lblAquiHum.setText(getResources().getString(R.string.icon_fa_refresh));
                    this.lblAquiHum.setTypeface(fontawesome);
                    this.lblAquiHum.setGravity(17);
                } else {
                    TextView textView = this.lblAquiHum;
                    textView.setText((pico.getPar_ext().get(1).intValue() / 100) + " " + getResources().getString(R.string.pm_qa_RH));
                    this.lblAquiHum.setTypeface(avenir);
                    this.lblAquiHum.setGravity(19);
                }
            }
            if (pico.getPar_ext().get(3) == null || pico.getPar_ext().get(3).intValue() == -999 || pico.getPar_ext().get(3).intValue() < 30000 || pico.getPar_ext().get(3).intValue() > 300000) {
                this.lblAquiCO2.setText(getResources().getString(R.string.icon_fa_refresh));
                this.lblAquiCO2.setTypeface(fontawesome);
                this.lblAquiCO2.setGravity(17);
            } else {
                TextView textView2 = this.lblAquiCO2;
                textView2.setText((pico.getPar_ext().get(3).intValue() / 100) + " " + getResources().getString(R.string.pm_qa_PPM));
                this.lblAquiCO2.setTypeface(avenir);
                this.lblAquiCO2.setGravity(19);
            }
            showFreccieQualita(pico.getPar_ext().get(2));
        }
    }

    private void showFreccieQualita(Integer num) {
        if (num == null || num.intValue() == -999) {
            this.lbl_icon_freccia1.setVisibility(4);
            this.lbl_icon_freccia2.setVisibility(4);
            this.lbl_icon_freccia3.setVisibility(4);
        } else if (num.intValue() > 400) {
            this.lbl_icon_freccia1.setVisibility(4);
            this.lbl_icon_freccia2.setVisibility(4);
            this.lbl_icon_freccia3.setVisibility(0);
        } else if (num.intValue() > 200) {
            this.lbl_icon_freccia1.setVisibility(4);
            this.lbl_icon_freccia2.setVisibility(0);
            this.lbl_icon_freccia3.setVisibility(4);
        } else {
            this.lbl_icon_freccia1.setVisibility(0);
            this.lbl_icon_freccia2.setVisibility(4);
            this.lbl_icon_freccia3.setVisibility(4);
        }
    }

    /* access modifiers changed from: private */
    public void modenight() {
        runOnUiThread(new Runnable() {
            public void run() {
                GradientDrawable gradientDrawable = (GradientDrawable) PicoActivity.this.getResources().getDrawable(R.drawable.pico_rouded_back);
                gradientDrawable.setColor(PicoActivity.this.getColorInt(R.color.picoBlueColor));
                PicoActivity.this.img_night.setBackground(gradientDrawable);
                PicoActivity.this.sb_speed.setEnabled(false);
                PicoActivity.this.sb_speed.setThumb(PicoActivity.this.getResources().getDrawable(R.drawable.seek_thumb_pico_disabled));
            }
        });
    }

    /* access modifiers changed from: private */
    public void modeNonNight() {
        runOnUiThread(new Runnable() {
            public void run() {
                PicoActivity.this.img_night.setBackgroundColor(0);
                PicoActivity.this.img_night.setBackground(PicoActivity.this.getResources().getDrawable(R.drawable.back_pico_vel_dis));
                PicoActivity.this.sb_speed.setEnabled(true);
                PicoActivity.this.sb_speed.setThumb(PicoActivity.this.getResources().getDrawable(R.drawable.seek_thumb_pico));
            }
        });
    }

    /* access modifiers changed from: private */
    public void setErrorsMan() {
        boolean z;
        boolean z2;
        boolean z3 = true;
        if (pico.getMan() == null || pico.getMan().length <= 0) {
            z = true;
        } else {
            z = true;
            for (int i = 0; i < pico.getMan().length && z; i++) {
                z = pico.getMan()[i] != 1;
            }
        }
        this.error4001 = false;
        if (pico.getErr() == null || pico.getErr().length <= 0) {
            z2 = true;
        } else {
            z2 = true;
            for (int i2 = 0; i2 < pico.getErr().length && z2; i2++) {
                if (pico.getErr()[i2] != null) {
                    for (int i3 = 0; i3 < pico.getErr()[i2].length && z2; i3++) {
                        z2 = pico.getErr()[i2][i3] == 0;
                        int i4 = pico.getErr()[i2][i3];
                    }
                }
            }
        }
        if (z2) {
            this.lblhome.setVisibility(8);
        } else {
            this.lblhome.setVisibility(0);
        }
        if (z) {
            this.lblman.setVisibility(8);
            return;
        }
        boolean z4 = this.lastHideMan == null;
        if (!z4) {
            if (new Date().getTime() - this.lastHideMan.getTime() <= 60000) {
                z3 = false;
            }
            z4 = z3;
        }
        if (z4) {
            this.lblman.setVisibility(0);
        }
    }

    /* access modifiers changed from: private */
    public void disableAllLayout() {
        for (int i = 0; i < this.listlayouts.size(); i++) {
        }
    }

    /* access modifiers changed from: private */
    public void enableAllLayout() {
        for (int i = 0; i < this.listlayouts.size(); i++) {
            this.listlayouts.get(i).setEnabled(true);
        }
    }

    public void setBarValue(final int i) {
        if (!this.changingvalue) {
            runOnUiThread(new Runnable() {
                public void run() {
                    PicoActivity.this.sb_speed.setProgress(i);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void selectedVentole() {
        runOnUiThread(new Runnable() {
            public void run() {
                PicoActivity.this.lyVEntVel.setVisibility(0);
                PicoActivity.this.ly_ventole.setVisibility(0);
                PicoActivity.this.img_vent1.setVisibility(4);
                PicoActivity.this.img_vent2.setVisibility(4);
                PicoActivity.this.img_vent3.setVisibility(4);
                PicoActivity.this.setBarValue(PicoActivity.pico.getSpd_rich());
            }
        });
    }

    /* access modifiers changed from: private */
    public void showVentole() {
        if (pico.getOff().booleanValue()) {
            hideVentole();
        } else {
            runOnUiThread(new Runnable() {
                public void run() {
                    PicoActivity.this.selectedVentole();
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void selectedHum() {
        runOnUiThread(new Runnable() {
            public void run() {
                PicoActivity.this.lyVEntVel.setVisibility(4);
                GradientDrawable gradientDrawable = (GradientDrawable) PicoActivity.this.getResources().getDrawable(R.drawable.pico_rouded_back);
                gradientDrawable.setColor(PicoActivity.this.getColorInt(R.color.picoBlueColor));
                PicoActivity.this.img_vent1.setImageDrawable(PicoActivity.this.getResources().getDrawable(R.drawable.hum_40));
                PicoActivity.this.img_vent2.setImageDrawable(PicoActivity.this.getResources().getDrawable(R.drawable.hum_50));
                PicoActivity.this.img_vent3.setImageDrawable(PicoActivity.this.getResources().getDrawable(R.drawable.hum_60));
                if (PicoActivity.pico.getHumvel() == 1) {
                    PicoActivity.this.img_vent1.setBackground(gradientDrawable);
                    PicoActivity.this.img_vent1.setColorFilter(PicoActivity.this.getColorInt(R.color.color_white));
                } else {
                    PicoActivity.this.img_vent1.setBackgroundColor(0);
                    PicoActivity.this.img_vent1.setBackground(PicoActivity.this.getResources().getDrawable(R.drawable.back_pico_vel_dis));
                    PicoActivity.this.img_vent1.setColorFilter(PicoActivity.this.getColorInt(R.color.grayPicoColor));
                }
                if (PicoActivity.pico.getHumvel() == 2) {
                    PicoActivity.this.img_vent2.setBackground(gradientDrawable);
                    PicoActivity.this.img_vent2.setColorFilter(PicoActivity.this.getColorInt(R.color.color_white));
                } else {
                    PicoActivity.this.img_vent2.setBackgroundColor(0);
                    PicoActivity.this.img_vent2.setBackground(PicoActivity.this.getResources().getDrawable(R.drawable.back_pico_vel_dis));
                    PicoActivity.this.img_vent2.setColorFilter(PicoActivity.this.getColorInt(R.color.grayPicoColor));
                }
                if (PicoActivity.pico.getHumvel() == 3) {
                    PicoActivity.this.img_vent3.setBackground(gradientDrawable);
                    PicoActivity.this.img_vent3.setColorFilter(PicoActivity.this.getColorInt(R.color.color_white));
                    return;
                }
                PicoActivity.this.img_vent3.setBackgroundColor(0);
                PicoActivity.this.img_vent3.setBackground(PicoActivity.this.getResources().getDrawable(R.drawable.back_pico_vel_dis));
                PicoActivity.this.img_vent3.setColorFilter(PicoActivity.this.getColorInt(R.color.grayPicoColor));
            }
        });
    }

    /* access modifiers changed from: private */
    public void showHum() {
        if (pico.getOff().booleanValue()) {
            hideVentole();
        } else {
            runOnUiThread(new Runnable() {
                public void run() {
                    PicoActivity.this.selectedHum();
                    PicoActivity.this.img_vent1.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            PicoActivity.tmpPico.setHumvel(1);
                            PicoActivity.this.updatePico(6);
                        }
                    });
                    PicoActivity.this.img_vent2.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            PicoActivity.tmpPico.setHumvel(2);
                            PicoActivity.this.updatePico(6);
                        }
                    });
                    PicoActivity.this.img_vent3.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            PicoActivity.tmpPico.setHumvel(3);
                            PicoActivity.this.updatePico(6);
                        }
                    });
                    PicoActivity.this.ly_ventole.setVisibility(0);
                    PicoActivity.this.img_vent1.setVisibility(0);
                    PicoActivity.this.img_vent2.setVisibility(0);
                    PicoActivity.this.img_vent3.setVisibility(0);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void hideVentole() {
        runOnUiThread(new Runnable() {
            public void run() {
                PicoActivity.this.ly_ventole.setVisibility(4);
            }
        });
    }

    private void setViewValue(ConstraintLayout constraintLayout, int i) {
        PicoBTNOBJ picoBTNOBJ = this.layouts.get(constraintLayout);
        picoBTNOBJ.getImageView().setImageDrawable(picoBTNOBJ.getImages()[i]);
        picoBTNOBJ.getLbl().setTextColor(picoBTNOBJ.getLbl_colors()[i]);
        if (i != 0) {
            picoBTNOBJ.getImageView().setColorFilter(picoBTNOBJ.getLbl_colors()[i], PorterDuff.Mode.SRC_IN);
            GradientDrawable gradientDrawable = (GradientDrawable) getResources().getDrawable(R.drawable.pico_rouded_back);
            if (pico.getOff().booleanValue()) {
                gradientDrawable.setColor(getResources().getColor(R.color.grayPicoColorOLD));
            } else {
                gradientDrawable.setColor(picoBTNOBJ.getColors()[i]);
            }
            constraintLayout.setBackground(gradientDrawable);
            return;
        }
        constraintLayout.setBackground(getResources().getDrawable(R.drawable.back_pico_vel_dis));
        picoBTNOBJ.getImageView().setColorFilter(picoBTNOBJ.getColorsvgUnslected(), PorterDuff.Mode.SRC_IN);
    }

    private void setUpMainLayout(ConstraintLayout constraintLayout, Integer num, Integer num2, Integer num3, Integer num4, Integer num5, Integer num6, Integer num7, Integer num8, Integer num9, Integer num10) {
        Drawable drawable = null;
        PicoBTNOBJ picoBTNOBJ = new PicoBTNOBJ();
        int colorInt = getColorInt(num.intValue());
        int colorInt2 = num2 != null ? getColorInt(num2.intValue()) : 0;
        Drawable drawable2 = getResources().getDrawable(num4.intValue());
        Drawable drawable3 = getResources().getDrawable(num5.intValue());
        if (num6 != null) {
            drawable = getResources().getDrawable(num6.intValue());
        }
        int colorInt3 = getColorInt(num8.intValue());
        int colorInt4 = getColorInt(num9.intValue());
        picoBTNOBJ.setColorsvgUnslected(getColorInt(num10.intValue()));
        picoBTNOBJ.setColors(new int[]{this.colorDisable, colorInt, colorInt2});
        picoBTNOBJ.setImageView((ImageView) findViewById(num3.intValue()));
        picoBTNOBJ.setImages(new Drawable[]{drawable2, drawable3, drawable});
        picoBTNOBJ.setLbl((TextView) findViewById(num7.intValue()));
        picoBTNOBJ.setLbl_colors(new int[]{this.colorTextDisable, colorInt3, colorInt4});
        this.layouts.put(constraintLayout, picoBTNOBJ);
        this.listlayouts.add(constraintLayout);
    }

    public void receiveStato(final String str) {
        Log.d("STATO", str);
        runOnUiThread(new Runnable() {
            public void run() {
                PicoActivity.this.updateStatoLabel(str);
            }
        });
        lastReceivedStato = new Date();
        if (this.picoCMD) {
            this.picoCMD = false;
            return;
        }
        hideProgress();
        this.lastStato = str;
        pico.receivedSync(str);
        if (pico.getFw_ver() != null && !pico.getFw_ver().equals(this.verfw)) {
            this.verfw = pico.getFw_ver();
            updatemenu();
        }
        tmpPico = pico.getCopy();
        updateView();
    }

    public void startStatoTimer() {
        this.pausastato = false;
        lastReceivedStato = null;
        try {
            timerStato.cancel();
            timerStato = null;
        } catch (Exception unused) {
        }
        Timer timer = new Timer();
        timerStato = timer;
        timer.schedule(new TimerTask() {
            public void run() {
                if (Constants.ISDEMO) {
                    return;
                }
                if (PicoActivity.this.pausastato) {
                    PicoActivity.lastReceivedStato = null;
                    return;
                }
                if (PicoActivity.lastReceivedStato == null) {
                    PicoActivity.lastReceivedStato = new Date();
                }
                if (new Date().getTime() - PicoActivity.lastReceivedStato.getTime() > 15000) {
                    boolean z = PicoActivity.lastErrShow == null;
                    if (!z) {
                        z = new Date().getTime() - PicoActivity.lastErrShow.getTime() > 15000;
                    }
                    if (z) {
                        PicoActivity.lastErrShow = new Date();
                        Functions.makeErrorToast(PicoActivity.this.activity, PicoActivity.this.getResources().getStringArray(R.array.zone_errors)[5]);
                    }
                    PicoActivity.pico.getErr();
                    int[] iArr = new int[2];
                    iArr[1] = 1;
                    iArr[0] = 1;
                    int[][] iArr2 = (int[][]) Array.newInstance(Integer.TYPE, iArr);
                    iArr2[0][0] = 10004;
                    PicoActivity.pico.setErr(iArr2);
                    PicoActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            PicoActivity.this.setErrorsMan();
                        }
                    });
                }
                if (!PicoActivity.pico.getOffline().booleanValue()) {
                    if (PicoActivity.lastReceivedStato != null) {
                        new Date().getTime();
                        PicoActivity.lastReceivedStato.getTime();
                    }
                    new ThreadWebService(PicoActivity.this.activity, 0, 26, PicoActivity.this.getResources().getString(R.string.uriWebService_PICO) + PicoActivity.this.getResources().getString(R.string.uri_GetPICOState) + "?picoSerial=" + PicoActivity.pico.getSerial() + "&PIN=" + PicoActivity.pico.getPin(), (String) null, (String[]) null, PicoActivity.this.first).start();
                    PicoActivity.this.first = false;
                } else if (!UDPSocket.isConnected()) {
                    if (PicoActivity.this.lastRetry == null) {
                        PicoActivity.this.lastRetry = new Date();
                    }
                    if (new Date().getTime() - PicoActivity.this.lastRetry.getTime() > 10000) {
                        PicoActivity.this.lastRetry = new Date();
                        PicoActivity.this.showProgress();
                        UDPSocket.stopListening();
                        PicoActivity.this.reconnect();
                    }
                } else {
                    UDPSocket.startListening();
                    try {
                        Thread.sleep(500);
                    } catch (Exception unused) {
                    }
                    UDPSocket.sendGetStato(PicoActivity.pico.getPin());
                }
            }
        }, 0, 5000);
    }

    public void parseRespSetStato(Response response) {
        hideProgress();
        try {
            if (response.getHttpResponceCode() == 200) {
                JSONObject jSONObject = new JSONObject(response.getHttpResponcePayload());
                if (jSONObject.has("ResCode") && jSONObject.getInt("ResCode") == 0) {
                    if (this.gotofasce) {
                        goToFasce();
                        return;
                    }
                    pico = tmpPico.getCopy();
                    this.picoCMD = true;
                    updateView();
                    return;
                }
            }
        } catch (Exception unused) {
        }
        Functions.makeErrorToast(this, getResources().getString(R.string.msg_commandKo));
    }

    public void parserServerStato(Response response) {
        if (response != null) {
            try {
                if (response.getHttpResponceCode() == 200) {
                    JSONObject jSONObject = new JSONObject(response.getHttpResponcePayload());
                    if (!jSONObject.has("ResCode")) {
                        return;
                    }
                    if (jSONObject.getInt("ResCode") == 0) {
                        receiveStato(jSONObject.getString("ResDescr"));
                    } else {
                        checkonlineError(jSONObject);
                    }
                } else {
                    checkonlineError(new JSONObject(response.getHttpResponcePayload()));
                }
            } catch (Exception unused) {
            }
        }
    }

    public void btnOnOff(View view) {
        tmpPico.setOff(Boolean.valueOf(!pico.getOff().booleanValue()));
        updatePico(2);
    }

    /* access modifiers changed from: private */
    public void enableMode(int i, int i2) {
        for (int i3 = 0; i3 < this.listlayouts.size(); i3++) {
            if (i == i3) {
                setViewValue(this.listlayouts.get(i3), i2);
                this.colorDisable = getColorInt(R.color.grayPicoColor);
            } else {
                setViewValue(this.listlayouts.get(i3), 0);
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:58:0x034b  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x035c  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x037b  */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x038c  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x039e  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x03b3  */
    /* JADX WARNING: Removed duplicated region for block: B:71:0x03c5  */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x03da  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showOtherChoise(int r19) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            r2 = 5
            java.lang.Long r2 = java.lang.Long.valueOf(r2)
            r3 = 4
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            r5 = 1
            r6 = 17170443(0x106000b, float:2.4611944E-38)
            if (r1 != 0) goto L_0x00da
            android.content.res.Resources r9 = r18.getResources()
            int r10 = it.tecnosystemi.TS.R.string.pm_btnAuto3
            java.lang.String r9 = r9.getString(r10)
            android.content.res.Resources r10 = r18.getResources()
            int r11 = it.tecnosystemi.TS.R.string.pm_btnAuto4
            java.lang.String r10 = r10.getString(r11)
            it.tecnosystemi.TS.Activity.PICO.PicoActivity$14 r11 = new it.tecnosystemi.TS.Activity.PICO.PicoActivity$14
            r11.<init>()
            it.tecnosystemi.TS.Activity.PICO.PicoActivity$15 r12 = new it.tecnosystemi.TS.Activity.PICO.PicoActivity$15
            r12.<init>()
            it.tecnosystemi.TS.Model.Pico r13 = pico
            java.lang.Long r13 = r13.getPCOM_Id()
            r14 = 6
            java.lang.Long r14 = java.lang.Long.valueOf(r14)
            boolean r13 = r13.equals(r14)
            if (r13 == 0) goto L_0x0071
            it.tecnosystemi.TS.Model.Pico r13 = pico
            java.lang.Boolean r13 = r13.getOff()
            boolean r13 = r13.booleanValue()
            if (r13 != 0) goto L_0x0071
            int r13 = it.tecnosystemi.TS.R.color.comfortEstatePicoColor
            int r13 = r0.getColorInt(r13)
            android.content.res.Resources r14 = r18.getResources()
            int r15 = it.tecnosystemi.TS.R.drawable.auto3
            android.graphics.drawable.Drawable r14 = r14.getDrawable(r15)
            android.content.res.Resources r15 = r18.getResources()
            int r15 = r15.getColor(r6)
            android.graphics.PorterDuff$Mode r8 = android.graphics.PorterDuff.Mode.SRC_IN
            r14.setColorFilter(r15, r8)
            r8 = 1
            goto L_0x0085
        L_0x0071:
            int r13 = r0.colorDisable
            android.content.res.Resources r8 = r18.getResources()
            int r14 = it.tecnosystemi.TS.R.drawable.auto3
            android.graphics.drawable.Drawable r14 = r8.getDrawable(r14)
            int r8 = r0.colorDisable
            android.graphics.PorterDuff$Mode r15 = android.graphics.PorterDuff.Mode.SRC_IN
            r14.setColorFilter(r8, r15)
            r8 = 0
        L_0x0085:
            it.tecnosystemi.TS.Model.Pico r15 = pico
            java.lang.Long r15 = r15.getPCOM_Id()
            r16 = 7
            java.lang.Long r4 = java.lang.Long.valueOf(r16)
            boolean r4 = r15.equals(r4)
            if (r4 == 0) goto L_0x00c4
            it.tecnosystemi.TS.Model.Pico r4 = pico
            java.lang.Boolean r4 = r4.getOff()
            boolean r4 = r4.booleanValue()
            if (r4 != 0) goto L_0x00c4
            int r4 = it.tecnosystemi.TS.R.color.comfortInvernoPicoColor
            int r4 = r0.getColorInt(r4)
            android.content.res.Resources r8 = r18.getResources()
            int r15 = it.tecnosystemi.TS.R.drawable.auto4
            android.graphics.drawable.Drawable r8 = r8.getDrawable(r15)
            android.content.res.Resources r15 = r18.getResources()
            int r15 = r15.getColor(r6)
            android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.SRC_IN
            r8.setColorFilter(r15, r7)
        L_0x00c0:
            r6 = r8
            r8 = 2
            goto L_0x0332
        L_0x00c4:
            int r4 = r0.colorDisable
            android.content.res.Resources r7 = r18.getResources()
            int r15 = it.tecnosystemi.TS.R.drawable.auto4
            android.graphics.drawable.Drawable r7 = r7.getDrawable(r15)
            int r15 = r0.colorDisable
            android.graphics.PorterDuff$Mode r6 = android.graphics.PorterDuff.Mode.SRC_IN
            r7.setColorFilter(r15, r6)
            r6 = r7
            goto L_0x0332
        L_0x00da:
            if (r1 != r5) goto L_0x01a7
            android.content.res.Resources r4 = r18.getResources()
            int r6 = it.tecnosystemi.TS.R.string.pm_btnAuto5
            java.lang.String r9 = r4.getString(r6)
            android.content.res.Resources r4 = r18.getResources()
            int r6 = it.tecnosystemi.TS.R.string.pm_btnAuto6
            java.lang.String r10 = r4.getString(r6)
            it.tecnosystemi.TS.Activity.PICO.PicoActivity$16 r11 = new it.tecnosystemi.TS.Activity.PICO.PicoActivity$16
            r11.<init>()
            it.tecnosystemi.TS.Activity.PICO.PicoActivity$17 r12 = new it.tecnosystemi.TS.Activity.PICO.PicoActivity$17
            r12.<init>()
            it.tecnosystemi.TS.Model.Pico r4 = pico
            java.lang.Long r4 = r4.getPCOM_Id()
            r6 = 8
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            boolean r4 = r4.equals(r6)
            if (r4 == 0) goto L_0x013c
            it.tecnosystemi.TS.Model.Pico r4 = pico
            java.lang.Boolean r4 = r4.getOff()
            boolean r4 = r4.booleanValue()
            if (r4 != 0) goto L_0x013c
            int r4 = it.tecnosystemi.TS.R.color.co2auto5PicoColor
            int r4 = r0.getColorInt(r4)
            android.content.res.Resources r6 = r18.getResources()
            int r7 = it.tecnosystemi.TS.R.drawable.auto5
            android.graphics.drawable.Drawable r6 = r6.getDrawable(r7)
            android.content.res.Resources r7 = r18.getResources()
            r8 = 17170443(0x106000b, float:2.4611944E-38)
            int r7 = r7.getColor(r8)
            android.graphics.PorterDuff$Mode r8 = android.graphics.PorterDuff.Mode.SRC_IN
            r6.setColorFilter(r7, r8)
            r13 = r4
            r14 = r6
            r8 = 1
            goto L_0x0152
        L_0x013c:
            int r4 = r0.colorDisable
            android.content.res.Resources r6 = r18.getResources()
            int r7 = it.tecnosystemi.TS.R.drawable.auto5
            android.graphics.drawable.Drawable r6 = r6.getDrawable(r7)
            int r7 = r0.colorDisable
            android.graphics.PorterDuff$Mode r8 = android.graphics.PorterDuff.Mode.SRC_IN
            r6.setColorFilter(r7, r8)
            r13 = r4
            r14 = r6
            r8 = 0
        L_0x0152:
            it.tecnosystemi.TS.Model.Pico r4 = pico
            java.lang.Long r4 = r4.getPCOM_Id()
            r6 = 9
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            boolean r4 = r4.equals(r6)
            if (r4 == 0) goto L_0x0192
            it.tecnosystemi.TS.Model.Pico r4 = pico
            java.lang.Boolean r4 = r4.getOff()
            boolean r4 = r4.booleanValue()
            if (r4 != 0) goto L_0x0192
            int r4 = it.tecnosystemi.TS.R.color.co2auto6PicoColor
            int r4 = r0.getColorInt(r4)
            android.content.res.Resources r6 = r18.getResources()
            int r7 = it.tecnosystemi.TS.R.drawable.auto6
            android.graphics.drawable.Drawable r8 = r6.getDrawable(r7)
            android.content.res.Resources r6 = r18.getResources()
            r7 = 17170443(0x106000b, float:2.4611944E-38)
            int r6 = r6.getColor(r7)
            android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.SRC_IN
            r8.setColorFilter(r6, r7)
            goto L_0x00c0
        L_0x0192:
            int r4 = r0.colorDisable
            android.content.res.Resources r6 = r18.getResources()
            int r7 = it.tecnosystemi.TS.R.drawable.auto6
            android.graphics.drawable.Drawable r6 = r6.getDrawable(r7)
            int r7 = r0.colorDisable
            android.graphics.PorterDuff$Mode r15 = android.graphics.PorterDuff.Mode.SRC_IN
            r6.setColorFilter(r7, r15)
            goto L_0x0332
        L_0x01a7:
            r4 = 2
            if (r1 != r4) goto L_0x0269
            android.content.res.Resources r4 = r18.getResources()
            int r6 = it.tecnosystemi.TS.R.string.pm_btnAuto1
            java.lang.String r9 = r4.getString(r6)
            android.content.res.Resources r4 = r18.getResources()
            int r6 = it.tecnosystemi.TS.R.string.pm_btnAuto2
            java.lang.String r10 = r4.getString(r6)
            it.tecnosystemi.TS.Activity.PICO.PicoActivity$18 r11 = new it.tecnosystemi.TS.Activity.PICO.PicoActivity$18
            r11.<init>()
            it.tecnosystemi.TS.Activity.PICO.PicoActivity$19 r12 = new it.tecnosystemi.TS.Activity.PICO.PicoActivity$19
            r12.<init>()
            it.tecnosystemi.TS.Model.Pico r4 = pico
            java.lang.Long r4 = r4.getPCOM_Id()
            boolean r4 = r4.equals(r3)
            if (r4 == 0) goto L_0x0204
            it.tecnosystemi.TS.Model.Pico r4 = pico
            java.lang.Boolean r4 = r4.getOff()
            boolean r4 = r4.booleanValue()
            if (r4 != 0) goto L_0x0204
            int r4 = it.tecnosystemi.TS.R.color.auto1PicoColor
            int r4 = r0.getColorInt(r4)
            android.content.res.Resources r6 = r18.getResources()
            int r7 = it.tecnosystemi.TS.R.drawable.auto1
            android.graphics.drawable.Drawable r6 = r6.getDrawable(r7)
            android.content.res.Resources r7 = r18.getResources()
            r8 = 17170444(0x106000c, float:2.4611947E-38)
            int r7 = r7.getColor(r8)
            android.graphics.PorterDuff$Mode r8 = android.graphics.PorterDuff.Mode.SRC_IN
            r6.setColorFilter(r7, r8)
            r13 = r4
            r14 = r6
            r8 = 1
            goto L_0x021a
        L_0x0204:
            int r4 = r0.colorDisable
            android.content.res.Resources r6 = r18.getResources()
            int r7 = it.tecnosystemi.TS.R.drawable.auto1
            android.graphics.drawable.Drawable r6 = r6.getDrawable(r7)
            int r7 = r0.colorDisable
            android.graphics.PorterDuff$Mode r8 = android.graphics.PorterDuff.Mode.SRC_IN
            r6.setColorFilter(r7, r8)
            r13 = r4
            r14 = r6
            r8 = 0
        L_0x021a:
            it.tecnosystemi.TS.Model.Pico r4 = pico
            java.lang.Long r4 = r4.getPCOM_Id()
            boolean r4 = r4.equals(r2)
            if (r4 == 0) goto L_0x0254
            it.tecnosystemi.TS.Model.Pico r4 = pico
            java.lang.Boolean r4 = r4.getOff()
            boolean r4 = r4.booleanValue()
            if (r4 != 0) goto L_0x0254
            int r4 = it.tecnosystemi.TS.R.color.auto2PicoColor
            int r4 = r0.getColorInt(r4)
            android.content.res.Resources r6 = r18.getResources()
            int r7 = it.tecnosystemi.TS.R.drawable.auto2
            android.graphics.drawable.Drawable r8 = r6.getDrawable(r7)
            android.content.res.Resources r6 = r18.getResources()
            r7 = 17170444(0x106000c, float:2.4611947E-38)
            int r6 = r6.getColor(r7)
            android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.SRC_IN
            r8.setColorFilter(r6, r7)
            goto L_0x00c0
        L_0x0254:
            int r4 = r0.colorDisable
            android.content.res.Resources r6 = r18.getResources()
            int r7 = it.tecnosystemi.TS.R.drawable.auto2
            android.graphics.drawable.Drawable r6 = r6.getDrawable(r7)
            int r7 = r0.colorDisable
            android.graphics.PorterDuff$Mode r15 = android.graphics.PorterDuff.Mode.SRC_IN
            r6.setColorFilter(r7, r15)
            goto L_0x0332
        L_0x0269:
            android.content.res.Resources r4 = r18.getResources()
            int r6 = it.tecnosystemi.TS.R.string.pm_btnAuto7
            java.lang.String r9 = r4.getString(r6)
            android.content.res.Resources r4 = r18.getResources()
            int r6 = it.tecnosystemi.TS.R.string.pm_btnAuto8
            java.lang.String r10 = r4.getString(r6)
            it.tecnosystemi.TS.Activity.PICO.PicoActivity$20 r11 = new it.tecnosystemi.TS.Activity.PICO.PicoActivity$20
            r11.<init>()
            it.tecnosystemi.TS.Activity.PICO.PicoActivity$21 r12 = new it.tecnosystemi.TS.Activity.PICO.PicoActivity$21
            r12.<init>()
            it.tecnosystemi.TS.Model.Pico r4 = pico
            java.lang.Long r4 = r4.getPCOM_Id()
            r6 = 10
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            boolean r4 = r4.equals(r6)
            if (r4 == 0) goto L_0x02c9
            it.tecnosystemi.TS.Model.Pico r4 = pico
            java.lang.Boolean r4 = r4.getOff()
            boolean r4 = r4.booleanValue()
            if (r4 != 0) goto L_0x02c9
            int r4 = it.tecnosystemi.TS.R.color.co2auto7PicoColor
            int r4 = r0.getColorInt(r4)
            android.content.res.Resources r6 = r18.getResources()
            int r7 = it.tecnosystemi.TS.R.drawable.auto7
            android.graphics.drawable.Drawable r6 = r6.getDrawable(r7)
            android.content.res.Resources r7 = r18.getResources()
            r8 = 17170443(0x106000b, float:2.4611944E-38)
            int r7 = r7.getColor(r8)
            android.graphics.PorterDuff$Mode r8 = android.graphics.PorterDuff.Mode.SRC_IN
            r6.setColorFilter(r7, r8)
            r13 = r4
            r14 = r6
            r8 = 1
            goto L_0x02df
        L_0x02c9:
            int r4 = r0.colorDisable
            android.content.res.Resources r6 = r18.getResources()
            int r7 = it.tecnosystemi.TS.R.drawable.auto7
            android.graphics.drawable.Drawable r6 = r6.getDrawable(r7)
            int r7 = r0.colorDisable
            android.graphics.PorterDuff$Mode r8 = android.graphics.PorterDuff.Mode.SRC_IN
            r6.setColorFilter(r7, r8)
            r13 = r4
            r14 = r6
            r8 = 0
        L_0x02df:
            it.tecnosystemi.TS.Model.Pico r4 = pico
            java.lang.Long r4 = r4.getPCOM_Id()
            r6 = 11
            java.lang.Long r6 = java.lang.Long.valueOf(r6)
            boolean r4 = r4.equals(r6)
            if (r4 == 0) goto L_0x031f
            it.tecnosystemi.TS.Model.Pico r4 = pico
            java.lang.Boolean r4 = r4.getOff()
            boolean r4 = r4.booleanValue()
            if (r4 != 0) goto L_0x031f
            int r4 = it.tecnosystemi.TS.R.color.co2auto8PicoColor
            int r4 = r0.getColorInt(r4)
            android.content.res.Resources r6 = r18.getResources()
            int r7 = it.tecnosystemi.TS.R.drawable.auto8
            android.graphics.drawable.Drawable r8 = r6.getDrawable(r7)
            android.content.res.Resources r6 = r18.getResources()
            r7 = 17170443(0x106000b, float:2.4611944E-38)
            int r6 = r6.getColor(r7)
            android.graphics.PorterDuff$Mode r7 = android.graphics.PorterDuff.Mode.SRC_IN
            r8.setColorFilter(r6, r7)
            goto L_0x00c0
        L_0x031f:
            int r4 = r0.colorDisable
            android.content.res.Resources r6 = r18.getResources()
            int r7 = it.tecnosystemi.TS.R.drawable.auto8
            android.graphics.drawable.Drawable r6 = r6.getDrawable(r7)
            int r7 = r0.colorDisable
            android.graphics.PorterDuff$Mode r15 = android.graphics.PorterDuff.Mode.SRC_IN
            r6.setColorFilter(r7, r15)
        L_0x0332:
            android.widget.TextView r7 = r0.lbl_otherchoise1
            r7.setText(r9)
            android.widget.TextView r7 = r0.lbl_otherchoise2
            r7.setText(r10)
            it.tecnosystemi.TS.Model.Pico r7 = pico
            java.lang.Long r7 = r7.getPCOM_Id()
            boolean r3 = r7.equals(r3)
            if (r3 == 0) goto L_0x035c
            r3 = 2
            if (r1 != r3) goto L_0x035c
            android.widget.TextView r3 = r0.lbl_otherchoise1
            android.content.res.Resources r7 = r18.getResources()
            r9 = 17170444(0x106000c, float:2.4611947E-38)
            int r7 = r7.getColor(r9)
            r3.setTextColor(r7)
            goto L_0x036c
        L_0x035c:
            android.widget.TextView r3 = r0.lbl_otherchoise1
            android.content.res.Resources r7 = r18.getResources()
            r9 = 17170443(0x106000b, float:2.4611944E-38)
            int r7 = r7.getColor(r9)
            r3.setTextColor(r7)
        L_0x036c:
            it.tecnosystemi.TS.Model.Pico r3 = pico
            java.lang.Long r3 = r3.getPCOM_Id()
            boolean r2 = r3.equals(r2)
            if (r2 == 0) goto L_0x038c
            r2 = 2
            if (r1 != r2) goto L_0x038c
            android.widget.TextView r1 = r0.lbl_otherchoise2
            android.content.res.Resources r2 = r18.getResources()
            r3 = 17170444(0x106000c, float:2.4611947E-38)
            int r2 = r2.getColor(r3)
            r1.setTextColor(r2)
            goto L_0x039c
        L_0x038c:
            android.widget.TextView r1 = r0.lbl_otherchoise2
            android.content.res.Resources r2 = r18.getResources()
            r3 = 17170443(0x106000b, float:2.4611944E-38)
            int r2 = r2.getColor(r3)
            r1.setTextColor(r2)
        L_0x039c:
            if (r8 != r5) goto L_0x03b3
            android.content.res.Resources r1 = r18.getResources()
            int r2 = it.tecnosystemi.TS.R.drawable.pico_rouded_back
            android.graphics.drawable.Drawable r1 = r1.getDrawable(r2)
            android.graphics.drawable.GradientDrawable r1 = (android.graphics.drawable.GradientDrawable) r1
            r1.setColor(r13)
            androidx.constraintlayout.widget.ConstraintLayout r2 = r0.ly_otherchoise1
            r2.setBackground(r1)
            goto L_0x03c2
        L_0x03b3:
            androidx.constraintlayout.widget.ConstraintLayout r1 = r0.ly_otherchoise1
            android.content.res.Resources r2 = r18.getResources()
            int r3 = it.tecnosystemi.TS.R.drawable.back_pico_vel_dis
            android.graphics.drawable.Drawable r2 = r2.getDrawable(r3)
            r1.setBackground(r2)
        L_0x03c2:
            r1 = 2
            if (r8 != r1) goto L_0x03da
            android.content.res.Resources r1 = r18.getResources()
            int r2 = it.tecnosystemi.TS.R.drawable.pico_rouded_back
            android.graphics.drawable.Drawable r1 = r1.getDrawable(r2)
            android.graphics.drawable.GradientDrawable r1 = (android.graphics.drawable.GradientDrawable) r1
            r1.setColor(r4)
            androidx.constraintlayout.widget.ConstraintLayout r2 = r0.ly_otherchoise2
            r2.setBackground(r1)
            goto L_0x03e9
        L_0x03da:
            androidx.constraintlayout.widget.ConstraintLayout r1 = r0.ly_otherchoise2
            android.content.res.Resources r2 = r18.getResources()
            int r3 = it.tecnosystemi.TS.R.drawable.back_pico_vel_dis
            android.graphics.drawable.Drawable r2 = r2.getDrawable(r3)
            r1.setBackground(r2)
        L_0x03e9:
            androidx.constraintlayout.widget.ConstraintLayout r1 = r0.ly_otherchoise1
            it.tecnosystemi.TS.Activity.PICO.PicoActivity$22 r2 = new it.tecnosystemi.TS.Activity.PICO.PicoActivity$22
            r2.<init>(r11)
            r1.setOnClickListener(r2)
            androidx.constraintlayout.widget.ConstraintLayout r1 = r0.ly_otherchoise2
            it.tecnosystemi.TS.Activity.PICO.PicoActivity$23 r2 = new it.tecnosystemi.TS.Activity.PICO.PicoActivity$23
            r2.<init>(r12)
            r1.setOnClickListener(r2)
            android.widget.ImageView r1 = r0.img_otherchoise1
            r1.setImageDrawable(r14)
            android.widget.ImageView r1 = r0.img_otherchoise2
            r1.setImageDrawable(r6)
            androidx.constraintlayout.widget.ConstraintLayout r1 = r0.ly_otherchoise
            r2 = 0
            r1.setVisibility(r2)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.PICO.PicoActivity.showOtherChoise(int):void");
    }

    public void hideHoterChoises() {
        runOnUiThread(new Runnable() {
            public void run() {
                PicoActivity.this.ly_otherchoise.setVisibility(8);
            }
        });
    }

    public void resetManutenzione() {
        AnonymousClass25 r6 = new Runnable() {
            public void run() {
                PicoActivity.this.dismissdialog();
                PicoActivity.this.updatePico(8);
            }
        };
        AnonymousClass26 r5 = new Runnable() {
            public void run() {
                PicoActivity.this.dismissdialog();
            }
        };
        openDialogFragment(createYesNoPopUp(getResources().getString(R.string.pm_dialogPICOinMaintenace), getResources().getString(R.string.pm_dialogPICOinMaintenace_AzzeraDialogText), getString(R.string.ba_attenzione_cancel), getString(R.string.pm_dialogPICOinMaintenace_Azzera), r5, r6));
    }

    private void setUpGui() {
        this.lyDebug = (ConstraintLayout) findViewById(R.id.lyDebug);
        this.lblStatoDebug = (TextView) findViewById(R.id.lblStatoDebug);
        this.lblStatoBello = (TextView) findViewById(R.id.lblStatoBello);
        this.lblStatoDebug.setMovementMethod(new ScrollingMovementMethod());
        this.lyVEntVel = (ConstraintLayout) findViewById(R.id.lyVEntVel);
        this.lyHum = (ConstraintLayout) findViewById(R.id.lyHum);
        this.lblTitleDebug = (TextView) findViewById(R.id.lblTitle);
        Functions.setFontsWithIcon(findViewById(R.id.ly_container), this);
        this.lblhome.setTypeface(fontawesome);
        this.lblman.setTypeface(fontawesome);
        SeekBar seekBar = (SeekBar) findViewById(R.id.sb_speed);
        this.sb_speed = seekBar;
        seekBar.setMax(100);
        this.sb_speed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.d("SEEKBAR", "INIZO");
                PicoActivity.this.changingvalue = true;
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d("SEEKBAR", "FINE");
                PicoActivity.tmpPico.setSpeed_raw(seekBar.getProgress());
                PicoActivity.tmpPico.setSpd_rich(PicoActivity.tmpPico.getSpeed_raw());
                PicoActivity.this.updatePico(5);
                PicoActivity.this.changingvalue = false;
            }
        });
        this.lblhome.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                StringBuilder sb = new StringBuilder();
                if (PicoActivity.pico.getErr() != null) {
                    for (int i = 0; i < PicoActivity.pico.getErr().length; i++) {
                        if (PicoActivity.pico.getErr()[i] != null && PicoActivity.pico.getErr()[i].length > 0) {
                            sb.append("\n");
                            sb.append(Functions.getStringResourceByName("PICO" + i, PicoActivity.this.activity));
                            sb.append(":");
                            boolean z = true;
                            for (int i2 = 0; i2 < PicoActivity.pico.getErr()[i].length; i2++) {
                                int i3 = PicoActivity.pico.getErr()[i][i2] / 1000;
                                int i4 = PicoActivity.pico.getErr()[i][i2] % 1000;
                                for (int i5 = 0; i5 < 8; i5++) {
                                    int pow = (int) Math.pow(2.0d, (double) i5);
                                    if ((i4 & pow) == pow) {
                                        if (z) {
                                            z = false;
                                        } else {
                                            sb.append(" - ");
                                        }
                                        sb.append(Functions.getStringResourceByName("ERRPICO" + i3 + String.format("%03d", new Object[]{Integer.valueOf(i5 + 1)}), PicoActivity.this.activity));
                                    }
                                }
                            }
                        }
                    }
                }
                AnonymousClass1 r8 = new Runnable() {
                    public void run() {
                        PicoActivity.this.dismissdialog();
                    }
                };
                PicoActivity picoActivity = PicoActivity.this;
                PicoActivity.this.openDialogFragment(picoActivity.createYesNoPopUp(picoActivity.getString(R.string.pm_dialogPICOinError), sb.toString(), "", PicoActivity.this.getString(R.string.general_OK), (Runnable) null, r8));
            }
        });
        this.lblman.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                StringBuilder sb = new StringBuilder();
                if (PicoActivity.pico.getMan() != null) {
                    for (int i = 0; i < PicoActivity.pico.getMan().length; i++) {
                        if (PicoActivity.pico.getMan()[i] == 1) {
                            sb.append(Functions.getStringResourceByName("PICO" + i, PicoActivity.this.activity));
                            sb.append("\n");
                        }
                    }
                }
                AnonymousClass1 r7 = new Runnable() {
                    public void run() {
                        PicoActivity.this.dismissdialog();
                        PicoActivity.this.resetManutenzione();
                    }
                };
                AnonymousClass2 r6 = new Runnable() {
                    public void run() {
                        PicoActivity.this.dismissdialog();
                    }
                };
                PicoActivity picoActivity = PicoActivity.this;
                PicoActivity.this.openDialogFragment(picoActivity.createYesNoPopUp(picoActivity.getResources().getString(R.string.pm_dialogPICOinMaintenace), sb.toString(), PicoActivity.this.getString(R.string.ba_attenzione_cancel), PicoActivity.this.getString(R.string.pm_dialogPICOinMaintenace_Azzera), r6, r7));
            }
        });
        this.layouts = new HashMap<>();
        this.listlayouts = new ArrayList();
        this.colorTextDisable = getColorInt(R.color.picotxt_color1);
        this.colorDisable = getColorInt(R.color.grayPicoColor);
        this.ly_otherchoise = (ConstraintLayout) findViewById(R.id.ly_otherchoise);
        this.ly_otherchoise1 = (ConstraintLayout) findViewById(R.id.ly_otherchoise1);
        this.ly_otherchoise2 = (ConstraintLayout) findViewById(R.id.ly_otherchoise2);
        this.img_otherchoise1 = (ImageView) findViewById(R.id.img_otherchoise1);
        this.img_otherchoise2 = (ImageView) findViewById(R.id.img_otherchoise2);
        this.lbl_otherchoise1 = (TextView) findViewById(R.id.lbl_otherchoise1);
        this.lbl_otherchoise2 = (TextView) findViewById(R.id.lbl_otherchoise2);
        this.iv_btnOnOff = (ImageView) findViewById(R.id.iv_btnOnOff);
        ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.ly_recupero);
        this.ly_recupero = constraintLayout;
        setUpMainLayout(constraintLayout, Integer.valueOf(R.color.recuperatorePicoColor), (Integer) null, Integer.valueOf(R.id.imgRec), Integer.valueOf(R.drawable.recupero), Integer.valueOf(R.drawable.recupero), (Integer) null, Integer.valueOf(R.id.lblRec), Integer.valueOf(R.color.picotxt_color1), Integer.valueOf(R.color.picotxt_color1), Integer.valueOf(R.color.picotxt_color1));
        ConstraintLayout constraintLayout2 = (ConstraintLayout) findViewById(R.id.ly_estrazione);
        this.ly_estrazione = constraintLayout2;
        setUpMainLayout(constraintLayout2, Integer.valueOf(R.color.estrazionePicoColor), (Integer) null, Integer.valueOf(R.id.imgEst), Integer.valueOf(R.drawable.estrazione), Integer.valueOf(R.drawable.estrazione), (Integer) null, Integer.valueOf(R.id.lblEst), Integer.valueOf(R.color.picotxt_color1), Integer.valueOf(R.color.picotxt_color1), Integer.valueOf(R.color.picotxt_color1));
        ConstraintLayout constraintLayout3 = (ConstraintLayout) findViewById(R.id.ly_immissione);
        this.ly_immissione = constraintLayout3;
        setUpMainLayout(constraintLayout3, Integer.valueOf(R.color.immissionePicoColor), (Integer) null, Integer.valueOf(R.id.imgImm), Integer.valueOf(R.drawable.immissione), Integer.valueOf(R.drawable.immissione), (Integer) null, Integer.valueOf(R.id.lblImm), Integer.valueOf(R.color.picotxt_color1), Integer.valueOf(R.color.picotxt_color1), Integer.valueOf(R.color.picotxt_color1));
        ConstraintLayout constraintLayout4 = (ConstraintLayout) findViewById(R.id.ly_umidita1);
        this.ly_umidita1 = constraintLayout4;
        setUpMainLayout(constraintLayout4, Integer.valueOf(R.color.auto1PicoColor), Integer.valueOf(R.color.auto2PicoColor), Integer.valueOf(R.id.imgUm1), Integer.valueOf(R.drawable.auto1_2), Integer.valueOf(R.drawable.auto1), Integer.valueOf(R.drawable.auto2), Integer.valueOf(R.id.lblUm1), Integer.valueOf(R.color.picotxt_color2), Integer.valueOf(R.color.picotxt_color2), Integer.valueOf(R.color.picotxt_color1));
        ConstraintLayout constraintLayout5 = (ConstraintLayout) findViewById(R.id.ly_umidita2);
        this.ly_umidita2 = constraintLayout5;
        setUpMainLayout(constraintLayout5, Integer.valueOf(R.color.co2auto7PicoColor), Integer.valueOf(R.color.co2auto8PicoColor), Integer.valueOf(R.id.imgUm2), Integer.valueOf(R.drawable.auto7_8), Integer.valueOf(R.drawable.auto7), Integer.valueOf(R.drawable.auto8), Integer.valueOf(R.id.lblUm2), Integer.valueOf(R.color.picotxt_color1), Integer.valueOf(R.color.picotxt_color1), Integer.valueOf(R.color.picotxt_color1));
        ConstraintLayout constraintLayout6 = (ConstraintLayout) findViewById(R.id.ly_comfort);
        this.ly_comfort = constraintLayout6;
        setUpMainLayout(constraintLayout6, Integer.valueOf(R.color.comfortEstatePicoColor), Integer.valueOf(R.color.comfortInvernoPicoColor), Integer.valueOf(R.id.imgComf), Integer.valueOf(R.drawable.auto3_4), Integer.valueOf(R.drawable.auto3), Integer.valueOf(R.drawable.auto4), Integer.valueOf(R.id.lblComf), Integer.valueOf(R.color.picotxt_color1), Integer.valueOf(R.color.picotxt_color1), Integer.valueOf(R.color.picotxt_color1));
        ConstraintLayout constraintLayout7 = (ConstraintLayout) findViewById(R.id.ly_co2);
        this.ly_co2 = constraintLayout7;
        setUpMainLayout(constraintLayout7, Integer.valueOf(R.color.co2auto5PicoColor), Integer.valueOf(R.color.co2auto6PicoColor), Integer.valueOf(R.id.imgCO2), Integer.valueOf(R.drawable.auto5_6), Integer.valueOf(R.drawable.auto5), Integer.valueOf(R.drawable.auto6), Integer.valueOf(R.id.lblCO2), Integer.valueOf(R.color.picotxt_color1), Integer.valueOf(R.color.picotxt_color1), Integer.valueOf(R.color.picotxt_color1));
        ConstraintLayout constraintLayout8 = (ConstraintLayout) findViewById(R.id.ly_ric_nat);
        this.ly_ric_nat = constraintLayout8;
        setUpMainLayout(constraintLayout8, Integer.valueOf(R.color.ricambioPicoColor), (Integer) null, Integer.valueOf(R.id.imgRicNat), Integer.valueOf(R.drawable.ricambio_naturale), Integer.valueOf(R.drawable.ricambio_naturale), (Integer) null, Integer.valueOf(R.id.lblRicNat), Integer.valueOf(R.color.picotxt_color1), Integer.valueOf(R.color.picotxt_color1), Integer.valueOf(R.color.picotxt_color1));
        this.ly_ric_nat.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PicoActivity.tmpPico.setPCOM_Id(12L);
                PicoActivity.tmpPico.setOff(false);
                PicoActivity.this.updatePico(3);
            }
        });
        this.ly_recupero.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PicoActivity.tmpPico.setPCOM_Id(1L);
                PicoActivity.tmpPico.setOff(false);
                PicoActivity.this.updatePico(3);
            }
        });
        this.ly_estrazione.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PicoActivity.tmpPico.setPCOM_Id(2L);
                PicoActivity.tmpPico.setOff(false);
                PicoActivity.this.updatePico(3);
            }
        });
        this.ly_immissione.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PicoActivity.tmpPico.setPCOM_Id(3L);
                PicoActivity.tmpPico.setOff(false);
                PicoActivity.this.updatePico(3);
            }
        });
        this.ly_umidita1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PicoActivity.this.showOtherChoise(2);
            }
        });
        this.ly_umidita2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PicoActivity.this.showOtherChoise(3);
            }
        });
        this.ly_comfort.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PicoActivity.this.showOtherChoise(0);
            }
        });
        this.ly_co2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PicoActivity.this.showOtherChoise(1);
            }
        });
        this.ly_ventole = (ConstraintLayout) findViewById(R.id.ly_ventole);
        this.img_vent1 = (ImageView) findViewById(R.id.img_vent1);
        this.img_vent2 = (ImageView) findViewById(R.id.img_vent2);
        this.img_vent3 = (ImageView) findViewById(R.id.img_vent3);
        ImageView imageView = (ImageView) findViewById(R.id.img_night);
        this.img_night = imageView;
        imageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                PicoActivity.tmpPico.setNight(!PicoActivity.tmpPico.isNight());
                if (PicoActivity.tmpPico.isNight()) {
                    PicoActivity.tmpPico.setSpd_rich(0);
                }
                PicoActivity.this.updatePico(4);
            }
        });
        this.lyModes = (ConstraintLayout) findViewById(R.id.lyModes);
        this.lblTitleDebug.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                CmdPICO cmdPICO = new CmdPICO();
                cmdPICO.setCmd("publish_sync_db");
                cmdPICO.setPin(PicoActivity.pico.getPin());
                PicoActivity.this.sendCmdUpdPico(cmdPICO);
            }
        });
        this.img_InOut = (ImageView) findViewById(R.id.img_InOut);
        this.lblAquiInOut = (TextView) findViewById(R.id.lblAquiInOut);
        this.lbl_icon_freccia1 = (TextView) findViewById(R.id.lbl_icon_freccia1);
        this.lbl_icon_freccia2 = (TextView) findViewById(R.id.lbl_icon_freccia2);
        this.lbl_icon_freccia3 = (TextView) findViewById(R.id.lbl_icon_freccia3);
        this.ly_aqi_co2 = (ConstraintLayout) findViewById(R.id.ly_aqi_co2);
        this.ly_aqi_aqi = (ConstraintLayout) findViewById(R.id.ly_aqi_aqi);
        this.vw_aqi_div2 = findViewById(R.id.vw_aqi_div2);
        this.vw_aqi_div3 = findViewById(R.id.vw_aqi_div3);
        this.lblAquiHum = (TextView) findViewById(R.id.lblAquiHum);
        this.lblAquiCO2 = (TextView) findViewById(R.id.lblAquiCO2);
        this.lblAquiHum.setText(getResources().getString(R.string.icon_fa_refresh));
        this.lblAquiHum.setTypeface(fontawesome);
        this.lblAquiHum.setGravity(17);
        this.lblAquiCO2.setText(getResources().getString(R.string.icon_fa_refresh));
        this.lblAquiCO2.setTypeface(fontawesome);
        this.lblAquiCO2.setGravity(17);
    }

    public void updatePico(final int i) {
        if (Constants.ISDEMO) {
            pico = tmpPico.getCopy();
            democlick();
            return;
        }
        showProgress();
        new Thread(new Runnable() {
            public void run() {
                int i = 2;
                switch (i) {
                    case 2:
                        CmdPICO.UpdPicoON updPicoON = new CmdPICO.UpdPicoON();
                        updPicoON.setPin(PicoActivity.pico.getPin());
                        if (!PicoActivity.tmpPico.getOff().booleanValue()) {
                            i = 1;
                        }
                        updPicoON.setOn_off(i);
                        PicoActivity.this.sendCmdUpdPico(updPicoON);
                        return;
                    case 3:
                        CmdPICO.UpdPicoONMode updPicoONMode = new CmdPICO.UpdPicoONMode();
                        updPicoONMode.setPin(PicoActivity.pico.getPin());
                        if (!PicoActivity.tmpPico.getOff().booleanValue()) {
                            i = 1;
                        }
                        updPicoONMode.setOn_off(i);
                        updPicoONMode.setMod((int) PicoActivity.tmpPico.getPCOM_Id().longValue());
                        PicoActivity.this.sendCmdUpdPico(updPicoONMode);
                        return;
                    case 4:
                        CmdPICO.UpdPicoNight updPicoNight = new CmdPICO.UpdPicoNight();
                        updPicoNight.setPin(PicoActivity.pico.getPin());
                        if (PicoActivity.tmpPico.isNight()) {
                            i = 1;
                        }
                        updPicoNight.setNight_mod(i);
                        PicoActivity.this.sendCmdUpdPico(updPicoNight);
                        return;
                    case 5:
                        CmdPICO.UpdPicoSpeed updPicoSpeed = new CmdPICO.UpdPicoSpeed();
                        updPicoSpeed.setPin(PicoActivity.pico.getPin());
                        updPicoSpeed.setSpd_row(PicoActivity.tmpPico.getSpeed_raw());
                        PicoActivity.this.sendCmdUpdPico(updPicoSpeed);
                        return;
                    case 6:
                        CmdPICO.UpdPicoHum updPicoHum = new CmdPICO.UpdPicoHum();
                        updPicoHum.setPin(PicoActivity.pico.getPin());
                        updPicoHum.setS_umd(PicoActivity.tmpPico.getHumvel());
                        PicoActivity.this.sendCmdUpdPico(updPicoHum);
                        return;
                    case 7:
                        CmdPICO.UpdPicoLed updPicoLed = new CmdPICO.UpdPicoLed();
                        updPicoLed.setPin(PicoActivity.pico.getPin());
                        if (PicoActivity.tmpPico.getLed() == 1) {
                            updPicoLed.setLed_on_off(1);
                        } else {
                            updPicoLed.setLed_on_off(2);
                        }
                        PicoActivity.this.sendCmdUpdPico(updPicoLed);
                        return;
                    case 8:
                        PicoActivity.this.lastHideMan = new Date();
                        CmdPICO.UpdPManReset updPManReset = new CmdPICO.UpdPManReset(PicoActivity.pico.getMan());
                        updPManReset.setPin(PicoActivity.pico.getPin());
                        PicoActivity.tmpPico.resetMan();
                        PicoActivity.this.sendCmdUpdPico(updPManReset);
                        return;
                    case 9:
                        CmdPICO.UpdPicoMCrono updPicoMCrono = new CmdPICO.UpdPicoMCrono();
                        updPicoMCrono.setPin(PicoActivity.pico.getPin());
                        updPicoMCrono.setM_crono(PicoActivity.tmpPico.getM_crono());
                        PicoActivity.this.sendCmdUpdPico(updPicoMCrono);
                        return;
                    default:
                        CmdPICO.UpdPico updPico = new CmdPICO.UpdPico();
                        updPico.setPin(PicoActivity.pico.getPin());
                        if (!PicoActivity.tmpPico.getOff().booleanValue()) {
                            i = 1;
                        }
                        updPico.setOn_off(i);
                        updPico.setMod((int) PicoActivity.tmpPico.getPCOM_Id().longValue());
                        updPico.setSpeed(PicoActivity.tmpPico.getVentvel());
                        updPico.setUmd(PicoActivity.tmpPico.getHumvel());
                        updPico.setLed_on_off(PicoActivity.tmpPico.getLed());
                        updPico.setSpeed_raw(PicoActivity.tmpPico.getSpeed_raw());
                        PicoActivity.this.sendCmdUpdPico(updPico);
                        return;
                }
            }
        }).start();
    }

    public static int getIDP() {
        if (idp > 500) {
            idp = 1;
        }
        int i = idp + 1;
        idp = i;
        return i;
    }

    private void sendCmdDataOraTImezone(CmdPICO cmdPICO, String str, String str2, boolean z) {
        this.gotofasce = z;
        if (pico.getOffline().booleanValue()) {
            this.picoCMD = true;
            String sendCMD = UDPSocket.sendCMD(cmdPICO);
            hideProgress();
            if (checkRespSetMode(sendCMD)) {
                pico = tmpPico.getCopy();
                updateView();
                if (z) {
                    goToFasce();
                    return;
                }
                return;
            }
            return;
        }
        PICOServerTimezone pICOServerTimezone = new PICOServerTimezone();
        pICOServerTimezone.setSerial(pico.getSerial());
        pICOServerTimezone.setPin(pico.getPin());
        pICOServerTimezone.setTimezone(str2);
        if (cmdPICO != null) {
            cmdPICO.setFrm("mqtt");
            cmdPICO.setIdp((long) getIDP());
            pICOServerTimezone.setCmd(new Gson().toJson((Object) cmdPICO));
        }
        new ThreadWebService(this.activity, 1, 27, str, new Gson().toJson((Object) pICOServerTimezone), (String[]) null).start();
    }

    /* access modifiers changed from: private */
    public void sendCmdUpdPico(CmdPICO cmdPICO) {
        this.gotofasce = false;
        if (pico.getOffline().booleanValue()) {
            this.picoCMD = true;
            String sendCMD = UDPSocket.sendCMD(cmdPICO);
            hideProgress();
            if (checkRespSetMode(sendCMD)) {
                pico = tmpPico.getCopy();
                updateView();
                return;
            }
            return;
        }
        PICOServer pICOServer = new PICOServer();
        pICOServer.setSerial(pico.getSerial());
        pICOServer.setPin(pico.getPin());
        pICOServer.setName(pico.getName());
        if (cmdPICO != null) {
            cmdPICO.setFrm("mqtt");
            cmdPICO.setIdp((long) getIDP());
            pICOServer.setCmd(new Gson().toJson((Object) cmdPICO));
        }
        new ThreadWebService(this.activity, 1, 27, getResources().getString(R.string.uriWebService_PICO) + getResources().getString(R.string.uri_SendPicoCmd) + "?picoSerial=" + pico.getSerial() + "&PIN=" + pico.getPin(), new Gson().toJson((Object) pICOServer), (String[]) null).start();
    }

    private void checkonlineError(JSONObject jSONObject) {
        try {
            if (jSONObject.has("ResCode") && jSONObject.getInt("ResCode") == 2) {
                stopgetState();
                if (!this.asckedPin) {
                    showGetPin();
                }
            }
        } catch (Exception unused) {
        }
    }

    private boolean checkRespSetMode(String str) {
        if (str == null) {
            return false;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (!jSONObject.has("res") || jSONObject.getInt("res") != 1) {
                return false;
            }
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void checkPin() {
        new Thread(new Runnable() {
            public void run() {
                PicoActivity.this.showProgress();
                CmdPICO cmdPICO = new CmdPICO();
                cmdPICO.setCmd(Protocols.CMD_CHECK_PIN);
                cmdPICO.setPin(PicoActivity.pico.getPin());
                UDPSocket.startListening();
                try {
                    Thread.sleep(100);
                } catch (Exception unused) {
                }
                String sendCMD = UDPSocket.sendCMD(cmdPICO);
                if (sendCMD == null) {
                    UDPSocket.stopListening();
                    try {
                        Thread.sleep(2000);
                    } catch (Exception unused2) {
                    }
                    UDPSocket.startListening();
                    try {
                        Thread.sleep(100);
                    } catch (Exception unused3) {
                    }
                    sendCMD = UDPSocket.sendCMD(cmdPICO);
                }
                if (sendCMD != null) {
                    try {
                        JSONObject jSONObject = new JSONObject(sendCMD);
                        if (jSONObject.has(Constants.JSON_RES) && jSONObject.getInt(Constants.JSON_RES) == 1) {
                            PicoActivity.this.startStatoTimer();
                            return;
                        }
                    } catch (Exception unused4) {
                    }
                } else if (!UDPSocket.isConnected()) {
                    Functions.makeErrorToast(PicoActivity.this.activity, PicoActivity.this.getResources().getString(R.string.connectToPolaris));
                    PicoActivity.this.reconnect();
                    return;
                }
                PicoActivity.this.hideProgress();
                PicoActivity.this.showGetPin();
            }
        }).start();
    }

    /* access modifiers changed from: private */
    public void showGetPin() {
        if (pico.getOffline().booleanValue() || !this.pausastato) {
            this.pausastato = true;
            runOnUiThread(new Runnable() {
                public void run() {
                    AnonymousClass1 r0 = new Runnable() {
                        public void run() {
                            PicoActivity.pico.setPin(PicoActivity.this.txtPin.getText().toString());
                            Pico.savePicoInPref(PicoActivity.pico, PicoActivity.this.activity);
                            PicoActivity.this.dismissdialog();
                            if (PicoActivity.pico.getOffline().booleanValue()) {
                                PicoActivity.this.checkPin();
                                return;
                            }
                            PicoActivity.this.first = true;
                            PicoActivity.this.startStatoTimer();
                        }
                    };
                    PicoActivity picoActivity = PicoActivity.this;
                    picoActivity.bundlePopUp = picoActivity.createSetPin(r0);
                    PicoActivity picoActivity2 = PicoActivity.this;
                    picoActivity2.openDialogFragment(picoActivity2.bundlePopUp);
                }
            });
        }
    }

    public void democlick() {
        new Thread(new Runnable() {
            public void run() {
                PicoActivity.this.showProgress();
                try {
                    Thread.sleep(500);
                } catch (Exception unused) {
                }
                PicoActivity.this.hideProgress();
                PicoActivity.this.updateView();
            }
        }).start();
    }

    /* access modifiers changed from: private */
    public int getColorInt(int i) {
        if (Build.VERSION.SDK_INT >= 23) {
            return getActivity().getResources().getColor(i, getActivity().getTheme());
        }
        return getActivity().getResources().getColor(i);
    }

    public View getToolBar() {
        return findViewById(R.id.pico_toolbar);
    }

    public void createPopUpRinominaCU() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        arrayList.add(pico.getName().toUpperCase());
        arrayList2.add(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 6) {
                    return false;
                }
                PicoActivity.this.dismissdialog();
                if (Constants.ISDEMO) {
                    PicoActivity.pico.setName(textView.getText().toString().toUpperCase());
                    Constants.listaImpianti.get(Constants.DEMO_IMP_INDEX).getListDevices().get(Constants.DEMO_DEV_INDEX).setName(textView.getText().toString().toUpperCase());
                    DataClass.getInstance(PicoActivity.this.activity).pico_list.get(Constants.DEMO_PICO_INDEX).setName(PicoActivity.pico.getName());
                    PicoActivity.this.changeTitle(PicoActivity.pico.getName());
                    return false;
                }
                PicoActivity.pico.setName(textView.getText().toString().toUpperCase());
                if (PicoActivity.pico.getOffline().booleanValue()) {
                    TSDeviceListActivity.SELECTED_DEV.setName(PicoActivity.pico.getName());
                    Device.updateDevice(TSDeviceListActivity.SELECTED_DEV, PicoActivity.this.activity);
                    Pico.savePicoInPref(PicoActivity.pico, PicoActivity.this.activity);
                } else {
                    PicoActivity.this.showProgress();
                    PicoActivity.this.sendCmdUpdPico((CmdPICO) null);
                }
                PicoActivity.this.changeTitle(PicoActivity.pico.getName());
                return false;
            }
        });
        this.bundlePopUp = createTxtPopUp(getResources().getString(R.string.cu_menuRinomina), arrayList, "", arrayList2);
    }

    /* access modifiers changed from: private */
    public void setApConfig() {
        if (pico.getOffline().booleanValue()) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Intent intent = new Intent(PicoActivity.this.activity, PicoBootloaderActivity.class);
                    intent.setFlags(67108864);
                    intent.putExtra("FROMPICOACT", true);
                    PicoActivity.this.startActivity(intent);
                }
            });
            return;
        }
        AnonymousClass46 r2 = new Runnable() {
            public void run() {
                PicoActivity.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(PicoActivity.this.activity, PicoBootloaderActivity.class);
                        intent.setFlags(67108864);
                        intent.putExtra("FROMPICOACT", true);
                        PicoActivity.this.startActivity(intent);
                    }
                });
            }
        };
        toConnPwd = "TS_" + pico.getSerial();
        toConnSid = Constants.WIFI_NAME_OFFLINE_PICO + pico.getSerial();
        connectToWifi(r2, false, false);
    }

    private void connectToWiFi() {
        showProgress();
        AnonymousClass47 r0 = new Runnable() {
            public void run() {
                PicoActivity.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        PicoActivity.this.gotoCheckLed();
                    }
                });
            }
        };
        toConnPwd = "12345678";
        toConnSid = Constants.WIFI_NAME_PICO_CONFIG;
        connectToWifi(r0, false, false);
    }

    public void gotoCheckLed() {
        runOnUiThread(new Runnable() {
            public void run() {
                Intent intent = new Intent(PicoActivity.this.activity, CheckLedPICOActivity.class);
                intent.putExtra(Constants.INTENT_BOOTLOADER, true);
                PicoActivity.this.startActivity(intent);
            }
        });
    }

    private void gotoBootIstr() {
        runOnUiThread(new Runnable() {
            public void run() {
                PicoActivity.this.startActivity(new Intent(PicoActivity.this.activity, IstrBootloaderActivityPICO.class));
            }
        });
    }

    public void parseRespPicoCmd(Response response, int i) {
        if (i == 28 && response != null) {
            try {
                if (response.getHttpResponceCode() == 200) {
                    JSONObject jSONObject = new JSONObject(response.getHttpResponcePayload());
                    if (!jSONObject.has("ResCode")) {
                        return;
                    }
                    if (jSONObject.getInt("ResCode") == 0) {
                        gotoBootIstr();
                    } else {
                        checkonlineError(jSONObject);
                    }
                } else {
                    checkonlineError(new JSONObject(response.getHttpResponcePayload()));
                }
            } catch (Exception unused) {
            }
        }
    }

    public void cancellaDevice() {
        Device fromPref = Device.getFromPref(pico.getSerial(), Constants.DEVICE_TYPE_PICO, this);
        this.devtodel = fromPref;
        if (fromPref == null) {
            Device device = new Device();
            this.devtodel = device;
            device.setSerial(pico.getSerial());
            this.devtodel.setLVDV_Type(Constants.DEVICE_TYPE_PICO);
        }
        AnonymousClass50 r8 = new Runnable() {
            public void run() {
                PicoActivity.this.dismissdialog();
                Device.deleteDevFromPref(PicoActivity.this.devtodel, PicoActivity.this.activity);
                if (PicoActivity.pico == null || !PicoActivity.pico.getOffline().booleanValue()) {
                    Pico.deletePICOfromPref(PicoActivity.this.devtodel.getSerial(), PicoActivity.this.activity);
                    Device.deleteDevFromPref(PicoActivity.this.devtodel, PicoActivity.this.activity);
                    Device_OP.DeviceOp deviceOp = new Device_OP.DeviceOp();
                    deviceOp.setDeviceID(PicoActivity.this.devtodel.getLVDV_Id());
                    deviceOp.setToken(PicoActivity.this.activity.FirebaseToken);
                    deviceOp.setPlatform(Constants.NOTIFIC_PLAT);
                    new ThreadWebService(PicoActivity.this.activity, 2, 10, PicoActivity.this.getResources().getString(R.string.uriWebService_PICO) + PicoActivity.this.getResources().getString(R.string.uri_DeleteDevice), new Gson().toJson((Object) deviceOp), new String[]{PicoActivity.this.devtodel.getSerial()}).start();
                    return;
                }
                Pico.deletePICOfromPref(PicoActivity.this.devtodel.getSerial(), PicoActivity.this.activity);
                PicoActivity.this.finish();
            }
        };
        openDialogFragment(createYesNoPopUp(getResources().getString(R.string.hd_deleteDEVAlert_title), getResources().getString(R.string.hd_deleteDEVAlert_text), getResources().getString(R.string.no), getResources().getString(R.string.yes), new Runnable() {
            public void run() {
                PicoActivity.this.dismissdialog();
            }
        }, r8));
    }

    /* access modifiers changed from: private */
    public void setDateTime() {
        this.bundlePopUp = createDataOraTimezonePopUp(getResources().getString(R.string.dialog_data_ora_title), "", pico.getTimezone());
    }

    public void impostaDataOraTimeZone(int i, int i2, int i3, int i4) {
        CmdPICO.UPD_DateTime uPD_DateTime = new CmdPICO.UPD_DateTime();
        uPD_DateTime.setDate(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        uPD_DateTime.setTime(String.format("%02d", new Object[]{Integer.valueOf(i2)}) + ":" + String.format("%02d", new Object[]{Integer.valueOf(i3)}) + ":00");
        uPD_DateTime.setWeek(i);
        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.uriWebService_PICO));
        sb.append(getResources().getString(R.string.uri_UpdPicoTimeAndTimezone));
        sendCmdDataOraTImezone(uPD_DateTime, sb.toString(), Constants.TIMEZONES.get(i4).getIdTimeZone(), this.isFasce);
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        JSONArray jSONArray;
        AnonymousClass52 r5 = new Runnable() {
            public void run() {
                PicoActivity.this.dismissdialog();
                PicoActivity.this.createPopUpRinominaCU();
                PicoActivity picoActivity = PicoActivity.this;
                picoActivity.openDialogFragment(picoActivity.bundlePopUp);
            }
        };
        list.add(createMenuItem(true, getResources().getString(R.string.pm_menuRename), "", (String) null, r5, false, false));
        AnonymousClass53 r6 = new Runnable() {
            public void run() {
                PicoActivity.this.dismissdialog();
                Functions.makeNormalToast(PicoActivity.this.activity, PicoActivity.this.getResources().getString(R.string.cu_setPinInfo));
            }
        };
        list.add(createMenuItem(false, getResources().getString(R.string.pm_menuEditPin), "", (String) null, r6, false, false));
        if (!Constants.ISDEMO) {
            if (!pico.getOffline().booleanValue()) {
                AnonymousClass54 r62 = new Runnable() {
                    public void run() {
                        try {
                            PicoActivity.this.dismissdialog();
                        } catch (Exception unused) {
                        }
                        PicoActivity.this.gotobooloader = false;
                        PicoActivity picoActivity = PicoActivity.this.activity;
                        new ThreadDowloadFirmWare(picoActivity, PicoActivity.this.getResources().getString(R.string.uriWebService_PICO) + PicoActivity.this.getResources().getString(R.string.uri_infoFrameWork)).start();
                    }
                };
                list.add(createMenuItem(false, getResources().getString(R.string.ha_menuVerificaFW), "", "", r62, false, false));
            }
            AnonymousClass55 r63 = new Runnable() {
                public void run() {
                    PicoActivity.this.dismissdialog();
                    PicoActivity.this.setApConfig();
                }
            };
            try {
                jSONArray = new JSONArray(PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.PREF_INFOFWS, "[]"));
            } catch (JSONException e) {
                e.printStackTrace();
                jSONArray = null;
            }
            if (jSONArray != null && jSONArray.length() > 0) {
                list.add(createMenuItem(false, getResources().getString(R.string.pm_menuUpdateFW), "", (String) null, r63, false, false));
            }
        }
        if (this.ledacceso == 1) {
            AnonymousClass56 r7 = new Runnable() {
                public void run() {
                    PicoActivity.tmpPico.setLed(2);
                    PicoActivity.this.updatePico(7);
                    PicoActivity.this.dismissdialog();
                }
            };
            list.add(createMenuItem(false, getResources().getString(R.string.pm_menuSpegniLed), "", (String) null, r7, false, false));
        } else {
            AnonymousClass57 r72 = new Runnable() {
                public void run() {
                    PicoActivity.tmpPico.setLed(1);
                    PicoActivity.this.updatePico(7);
                    PicoActivity.this.dismissdialog();
                }
            };
            list.add(createMenuItem(false, getResources().getString(R.string.pm_menuAccendiLed), "", (String) null, r72, false, false));
        }
        if (this.showMenuFasce) {
            AnonymousClass58 r73 = new Runnable() {
                public void run() {
                    PicoActivity.this.isFasce = false;
                    PicoActivity.this.setDateTime();
                    PicoActivity picoActivity = PicoActivity.this;
                    picoActivity.openDialogFragment(picoActivity.bundlePopUp);
                }
            };
            list.add(createMenuItem(false, getResources().getString(R.string.pm_menuUpdateDateTime), "", (String) null, r73, false, false));
            AnonymousClass59 r74 = new Runnable() {
                public void run() {
                    if (PicoActivity.pico.getOffline().booleanValue() || (PicoActivity.pico.getTimezone() != null && !PicoActivity.pico.getTimezone().isEmpty())) {
                        PicoActivity.this.goToFasce();
                        return;
                    }
                    PicoActivity.this.isFasce = true;
                    PicoActivity.this.setDateTime();
                    PicoActivity picoActivity = PicoActivity.this;
                    picoActivity.openDialogFragment(picoActivity.bundlePopUp);
                }
            };
            list.add(createMenuItem(false, getResources().getString(R.string.pm_menuFasce), "", (String) null, r74, false, false));
            if (pico.getM_crono() == 1) {
                list.add(createMenuItem(false, getResources().getString(R.string.pm_menuDisabilitaCrono), "", (String) null, new Runnable() {
                    public void run() {
                        PicoActivity.tmpPico.setM_crono(2);
                        PicoActivity.this.updatePico(9);
                        PicoActivity.this.dismissdialog();
                    }
                }, false, false));
            } else if (pico.getM_crono() == 2) {
                list.add(createMenuItem(false, getResources().getString(R.string.pm_menuAbilitaCrono), "", (String) null, new Runnable() {
                    public void run() {
                        PicoActivity.tmpPico.setM_crono(1);
                        PicoActivity.this.updatePico(9);
                        PicoActivity.this.dismissdialog();
                    }
                }, false, false));
            }
        }
        if (this.hasslave > 0) {
            AnonymousClass62 r64 = new Runnable() {
                public void run() {
                    PicoActivity.this.dismissdialog();
                    PicoActivity.this.activity.startActivity(new Intent(PicoActivity.this.activity, PicoCascataActivity.class));
                }
            };
            list.add(createMenuItem(false, getResources().getString(R.string.pm_modalitaCascata), "", (String) null, r64, false, false));
        }
        AnonymousClass63 r65 = new Runnable() {
            public void run() {
                PicoActivity.this.dismissdialog();
                if (Constants.ISDEMO) {
                    Functions.makeNormalToast(PicoActivity.this.activity, PicoActivity.this.getResources().getString(R.string.cu_DemoVersion));
                } else {
                    PicoActivity.this.cancellaDevice();
                }
            }
        };
        list.add(createMenuItem(false, getResources().getString(R.string.pm_deleteDevice), "", (String) null, r65, false, false));
        new Runnable() {
            public void run() {
                PicoActivity.this.dismissdialog();
                PicoActivity.this.lyDebug.setVisibility(0);
            }
        };
        AnonymousClass65 r66 = new Runnable() {
            public void run() {
                PicoActivity.this.dismissdialog();
            }
        };
        Pico pico2 = pico;
        if (!(pico2 == null || pico2.getSerial() == null)) {
            list.add(createMenuItem(false, getResources().getString(R.string.pm_IDDevice) + pico.getSerial(), "", (String) null, r66, false, false));
        }
        AnonymousClass66 r67 = new Runnable() {
            public void run() {
                PicoActivity.this.dismissdialog();
            }
        };
        Pico pico3 = pico;
        if (!(pico3 == null || pico3.getFw_ver() == null)) {
            list.add(createMenuItem(false, getResources().getString(R.string.c2_1_pico_InfoFWVer) + ": " + this.verfw, "", (String) null, r67, false, false));
        }
        return list;
    }

    public String setToolbarTitle() {
        return pico.getName().toUpperCase();
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0010 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDestroy() {
        /*
            r1 = this;
            super.onDestroy()
            it.tecnosystemi.TS.Commands.UDPSocket.stopListening()
            java.util.Timer r0 = timerStato     // Catch:{ Exception -> 0x0010 }
            r0.purge()     // Catch:{ Exception -> 0x0010 }
            java.util.Timer r0 = timerStato     // Catch:{ Exception -> 0x0010 }
            r0.cancel()     // Catch:{ Exception -> 0x0010 }
        L_0x0010:
            it.tecnosystemi.TS.Model.Pico r0 = pico     // Catch:{ Exception -> 0x0019 }
            java.lang.Boolean r0 = r0.getOffline()     // Catch:{ Exception -> 0x0019 }
            r0.booleanValue()     // Catch:{ Exception -> 0x0019 }
        L_0x0019:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.PICO.PicoActivity.onDestroy():void");
    }

    public void onResume() {
        super.onResume();
        this.pausastato = false;
        if (pico.getOffline() == null) {
            return;
        }
        if (!pico.getOffline().booleanValue()) {
            startStatoTimer();
        } else if (!this.firstCheck && !this.errcon) {
            reconnect();
        }
    }

    /* access modifiers changed from: private */
    public void goToFasce() {
        runOnUiThread(new Runnable() {
            public void run() {
                PicoActivity.this.activity.startActivity(new Intent(PicoActivity.this.activity, PICOCronoSummaryActivity.class));
            }
        });
    }

    public void reconnect() {
        StringBuilder sb = new StringBuilder("");
        int i = this.times;
        this.times = i + 1;
        sb.append(i);
        Log.d("TENTATIVI", sb.toString());
        AnonymousClass68 r2 = new Runnable() {
            public void run() {
                if (!PicoActivity.this.firstCheck) {
                    PicoActivity.this.firstCheck = true;
                    PicoActivity.this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            PicoActivity.this.hideProgress();
                            UDPSocket.resetIDP();
                            UDPSocket.startListening(true);
                            PicoActivity.this.checkPin();
                        }
                    });
                } else if (UDPSocket.isConnected() || PicoActivity.this.times <= 1) {
                    UDPSocket.resetIDP();
                    UDPSocket.startListening(true);
                    PicoActivity.this.startStatoTimer();
                } else {
                    PicoActivity.this.disconnectFromWIfi();
                    PicoActivity.this.times = 0;
                }
            }
        };
        AnonymousClass69 r3 = new Runnable() {
            public void run() {
                PicoActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        PicoActivity.this.errcon = true;
                        String str = ((PicoActivity.this.getResources().getString(R.string.ba_apAssente) + "\n" + PicoActivity.this.getResources().getString(R.string.connectToPolaris)) + "\nSSID: " + BaseActivity.toConnSid) + "\n" + PicoActivity.this.getResources().getString(R.string.c4_PwdHint) + ": " + BaseActivity.toConnPwd;
                        AlertDialog.Builder builder = new AlertDialog.Builder(PicoActivity.this.activity);
                        builder.setMessage(str).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                try {
                                    Intent intent = new Intent("android.settings.WIFI_SETTINGS");
                                    intent.addFlags(268435456);
                                    PicoActivity.this.activity.startActivity(intent);
                                    PicoActivity.this.errcon = false;
                                } catch (Exception unused) {
                                }
                            }
                        });
                        AlertDialog create = builder.create();
                        create.show();
                        create.getButton(-1).setTextColor(PicoActivity.this.getResources().getColor(R.color.picoBlueColor));
                    }
                });
            }
        };
        toConnPwd = "TS_" + pico.getSerial();
        toConnSid = Constants.WIFI_NAME_OFFLINE_PICO + pico.getSerial();
        connectToWifi(r2, r3, false, false);
    }

    public void onPause() {
        super.onPause();
        this.pausastato = true;
        stopHandlerInOut();
    }

    public void onBackPressed() {
        if (this.ly_otherchoise.getVisibility() == 0) {
            this.ly_otherchoise.setVisibility(8);
        } else if (this.lyDebug.getVisibility() == 0) {
            this.lyDebug.setVisibility(8);
        } else {
            super.onBackPressed();
        }
    }

    private class PicoBTNOBJ {
        private int[] colors;
        int colorsvgUnslected;
        private ImageView imageView;
        private Drawable[] images;
        private TextView lbl;
        private int[] lbl_colors;

        private PicoBTNOBJ() {
        }

        public int getColorsvgUnslected() {
            return this.colorsvgUnslected;
        }

        public void setColorsvgUnslected(int i) {
            this.colorsvgUnslected = i;
        }

        public TextView getLbl() {
            return this.lbl;
        }

        public void setLbl(TextView textView) {
            this.lbl = textView;
        }

        public int[] getLbl_colors() {
            return this.lbl_colors;
        }

        public void setLbl_colors(int[] iArr) {
            this.lbl_colors = iArr;
        }

        public ImageView getImageView() {
            return this.imageView;
        }

        public void setImageView(ImageView imageView2) {
            this.imageView = imageView2;
        }

        public int[] getColors() {
            return this.colors;
        }

        public void setColors(int[] iArr) {
            this.colors = iArr;
        }

        public Drawable[] getImages() {
            return this.images;
        }

        public void setImages(Drawable[] drawableArr) {
            this.images = drawableArr;
        }
    }
}
