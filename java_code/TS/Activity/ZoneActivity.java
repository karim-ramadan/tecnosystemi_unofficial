package it.tecnosystemi.TS.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Commands.MySocket;
import it.tecnosystemi.TS.Model.ControlUnit;
import it.tecnosystemi.TS.Model.Zona;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Threads.ThreadWebService;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class ZoneActivity extends BaseActivity {
    Button btnElettrovalvola;
    Button btnFancoil;
    Button btnManCrono;
    Button btnOnOff;
    Button btnSerranda;
    Button btnTempMinus;
    Button btnTempPlus;
    BaseActivity.BundleMenuList bundlePopUp;
    boolean cambiato;
    boolean firstzo;
    TextView lblCBadgeIco;
    TextView lblCasetta;
    TextView lblCronoIco;
    TextView lblCronoOnOff;
    TextView lblCwinIco;
    TextView lblErrorIcon;
    TextView lblErrorText;
    TextView lblGradi;
    TextView lblNomeCU;
    TextView lblRealTemp;
    TextView lblTemp;
    TextView lblUmd;
    TextView lblUmdIco;
    TextView lblUmdIco2;
    Thread modifitemp;
    double realtemp;
    int temp;
    Zona tempZona;
    Zona zona;

    public BaseActivity getActivity() {
        return this;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_zone);
        Intent intent = getIntent();
        this.cu = (ControlUnit) intent.getSerializableExtra(Constants.INTENT_CU);
        if (!this.cu.isOffline()) {
            this.urlgetupd = getResources().getString(R.string.uriWebService_POLARIS) + getResources().getString(R.string.uri_GetState) + "?cuSerial=" + this.cu.getSerial() + "&PIN=" + this.cu.getPin();
        }
        this.indexZona = intent.getIntExtra(Constants.INTENT_INDEXZONA, 0);
        Constants.DEMO_INDEX_ZONA = this.indexZona;
        this.tempZona = (Zona) intent.getSerializableExtra(Constants.INTENT_ZONA);
        this.zona = this.cu.getZone().get(this.indexZona);
        super.onCreate(bundle);
        setupGui();
    }

    public void inizializeModifiTemp() {
        Thread thread = this.modifitemp;
        if (thread != null) {
            thread.interrupt();
        }
        this.modifitemp = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(3000);
                    ZoneActivity.this.saveData();
                } catch (Exception unused) {
                }
            }
        });
    }

    public void setupGui() {
        this.lblNomeCU = (TextView) findViewById(R.id.za_lblcuName);
        this.lblRealTemp = (TextView) findViewById(R.id.za_lblRealTmep);
        this.lblTemp = (TextView) findViewById(R.id.za_lblTemp);
        this.lblGradi = (TextView) findViewById(R.id.za_lblgradi);
        this.lblErrorIcon = (TextView) findViewById(R.id.za_lblErrorIcon);
        this.lblErrorText = (TextView) findViewById(R.id.za_lblError);
        this.lblCasetta = (TextView) findViewById(R.id.za_lblCasetta);
        this.lblCronoIco = (TextView) findViewById(R.id.za_lblCronoIco);
        this.lblCronoOnOff = (TextView) findViewById(R.id.za_lblCronoOnOff);
        this.lblUmd = (TextView) findViewById(R.id.za_lblUmd);
        this.btnOnOff = (Button) findViewById(R.id.za_btnOnOff);
        this.btnFancoil = (Button) findViewById(R.id.za_btnFanCoil);
        this.btnElettrovalvola = (Button) findViewById(R.id.za_btnElettroValvola);
        this.btnSerranda = (Button) findViewById(R.id.za_btnSerranda);
        this.btnManCrono = (Button) findViewById(R.id.za_btnFascieOrarie);
        this.btnTempMinus = (Button) findViewById(R.id.za_btnTempMinus);
        this.btnTempPlus = (Button) findViewById(R.id.za_btnTempPlus);
        this.lblUmdIco = (TextView) findViewById(R.id.za_lblUmdIco);
        this.lblCwinIco = (TextView) findViewById(R.id.za_lblcwinIcon);
        this.lblCBadgeIco = (TextView) findViewById(R.id.za_lblcbadgeIco);
        this.lblUmdIco2 = (TextView) findViewById(R.id.za_lblUmd2);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "fonts/fontawesome.ttf");
        Typeface createFromAsset2 = Typeface.createFromAsset(getAssets(), "fonts/icomoon.ttf");
        Typeface createFromAsset3 = Typeface.createFromAsset(getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
        Typeface createFromAsset4 = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext_UltraLight.ttf");
        this.lblGradi.setTypeface(createFromAsset3);
        this.lblRealTemp.setTypeface(createFromAsset3);
        this.lblTemp.setTypeface(createFromAsset4);
        this.lblNomeCU.setTypeface(createFromAsset3);
        this.lblErrorText.setTypeface(createFromAsset3);
        this.lblCronoOnOff.setTypeface(createFromAsset3);
        this.lblUmd.setTypeface(createFromAsset3);
        this.btnTempMinus.setTypeface(createFromAsset2);
        this.btnTempPlus.setTypeface(createFromAsset2);
        this.btnOnOff.setTypeface(createFromAsset2);
        this.btnFancoil.setTypeface(createFromAsset2);
        this.btnElettrovalvola.setTypeface(createFromAsset2);
        this.btnSerranda.setTypeface(createFromAsset2);
        this.btnManCrono.setTypeface(createFromAsset2);
        this.lblCasetta.setTypeface(createFromAsset2);
        this.lblCronoIco.setTypeface(createFromAsset2);
        this.lblErrorIcon.setTypeface(createFromAsset);
        this.lblUmdIco.setTypeface(createFromAsset2);
        this.lblCwinIco.setTypeface(createFromAsset2);
        this.lblCBadgeIco.setTypeface(createFromAsset2);
        this.lblUmdIco2.setTypeface(createFromAsset2);
        allViewGone();
        this.tempZona.setSetTemp(this.zona.getSetTemp());
        if (Constants.ISDEMO) {
            setValuesFromCU();
        }
    }

    private void allViewGone() {
        this.lblGradi.setVisibility(8);
        this.lblRealTemp.setVisibility(8);
        this.lblTemp.setVisibility(8);
        this.lblNomeCU.setVisibility(8);
        this.lblErrorText.setVisibility(8);
        this.lblCronoOnOff.setVisibility(8);
        this.btnTempMinus.setVisibility(8);
        this.btnTempPlus.setVisibility(8);
        this.btnOnOff.setVisibility(8);
        this.btnFancoil.setVisibility(8);
        this.btnElettrovalvola.setVisibility(8);
        this.btnSerranda.setVisibility(8);
        this.btnManCrono.setVisibility(8);
        this.lblCasetta.setVisibility(8);
        this.lblCronoIco.setVisibility(8);
        this.lblErrorIcon.setVisibility(8);
        this.lblUmdIco2.setVisibility(8);
    }

    private void viewVisible() {
        this.lblGradi.setVisibility(0);
        this.lblRealTemp.setVisibility(0);
        this.lblTemp.setVisibility(0);
        this.lblNomeCU.setVisibility(0);
        this.lblErrorText.setVisibility(0);
        this.lblCronoOnOff.setVisibility(0);
        this.btnTempMinus.setVisibility(0);
        this.btnTempPlus.setVisibility(0);
        this.btnOnOff.setVisibility(0);
        this.btnManCrono.setVisibility(0);
        this.lblCasetta.setVisibility(0);
        this.lblCronoIco.setVisibility(0);
        this.lblUmdIco2.setVisibility(0);
    }

    public void setValuesFromCU() {
        this.zona = this.cu.getZone().get(this.indexZona);
        setZoneValues();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:61:?, code lost:
        r13.lblCwinIco.setVisibility(8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:?, code lost:
        r13.lblCBadgeIco.setVisibility(8);
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:38:0x01f9 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:60:0x02ac */
    /* JADX WARNING: Missing exception handler attribute for start block: B:81:0x0359 */
    /* JADX WARNING: Removed duplicated region for block: B:143:0x054f A[Catch:{ Exception -> 0x05c5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x0555 A[Catch:{ Exception -> 0x05c5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:147:0x0592 A[Catch:{ Exception -> 0x05c5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:151:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setZoneValues() {
        /*
            r13 = this;
            r13.viewVisible()     // Catch:{ Exception -> 0x05c5 }
            boolean r0 = it.tecnosystemi.TS.Utils.Constants.ISDEMO     // Catch:{ Exception -> 0x05c5 }
            if (r0 == 0) goto L_0x008b
            it.tecnosystemi.TS.Model.Zona r0 = r13.tempZona     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r1 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            int r1 = r1.getManCrono()     // Catch:{ Exception -> 0x05c5 }
            r0.setManCrono(r1)     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r0 = r13.tempZona     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r1 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            boolean r1 = r1.isOff()     // Catch:{ Exception -> 0x05c5 }
            r0.setOff(r1)     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r0 = r13.tempZona     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r1 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            boolean[] r1 = r1.getErrors()     // Catch:{ Exception -> 0x05c5 }
            r0.setErrors(r1)     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r0 = r13.tempZona     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r1 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            int r1 = r1.getEV()     // Catch:{ Exception -> 0x05c5 }
            r0.setEV(r1)     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r0 = r13.tempZona     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r1 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            int r1 = r1.getFancoilSet()     // Catch:{ Exception -> 0x05c5 }
            r0.setFancoilSet(r1)     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r0 = r13.tempZona     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r1 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            int r1 = r1.getFancoil()     // Catch:{ Exception -> 0x05c5 }
            r0.setFancoil(r1)     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r0 = r13.tempZona     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r1 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            int r1 = r1.getSerranda()     // Catch:{ Exception -> 0x05c5 }
            r0.setSerranda(r1)     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r0 = r13.tempZona     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r1 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            int r1 = r1.getSerrandaSet()     // Catch:{ Exception -> 0x05c5 }
            r0.setSerrandaSet(r1)     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r0 = r13.tempZona     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r1 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            java.lang.Object r1 = r1.getCWin()     // Catch:{ Exception -> 0x05c5 }
            r0.setCWin(r1)     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r0 = r13.tempZona     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r1 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            java.lang.Object r1 = r1.getCBadge()     // Catch:{ Exception -> 0x05c5 }
            r0.setCBadge(r1)     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r0 = r13.tempZona     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r1 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            java.lang.String r1 = r1.getUmd()     // Catch:{ Exception -> 0x05c5 }
            r0.setUmd(r1)     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r0 = r13.tempZona     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r1 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            java.lang.String r1 = r1.getSetUmd()     // Catch:{ Exception -> 0x05c5 }
            r0.setSetUmd(r1)     // Catch:{ Exception -> 0x05c5 }
        L_0x008b:
            it.tecnosystemi.TS.Model.Zona r0 = r13.tempZona     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r1 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            java.lang.String r1 = r1.getName()     // Catch:{ Exception -> 0x05c5 }
            r0.setName(r1)     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            java.lang.String r0 = r0.getName()     // Catch:{ Exception -> 0x05c5 }
            r13.changeTitle(r0)     // Catch:{ Exception -> 0x05c5 }
            android.widget.TextView r0 = r13.lblNomeCU     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.ControlUnit r1 = r13.cu     // Catch:{ Exception -> 0x05c5 }
            java.lang.String r1 = r1.getName()     // Catch:{ Exception -> 0x05c5 }
            r0.setText(r1)     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r0 = r13.tempZona     // Catch:{ Exception -> 0x05c5 }
            java.lang.String r0 = r0.getSetTemp()     // Catch:{ Exception -> 0x05c5 }
            double r0 = java.lang.Double.parseDouble(r0)     // Catch:{ Exception -> 0x05c5 }
            java.lang.Thread r2 = r13.modifitemp     // Catch:{ Exception -> 0x05c5 }
            if (r2 == 0) goto L_0x00be
            boolean r2 = r2.isAlive()     // Catch:{ Exception -> 0x05c5 }
            if (r2 != 0) goto L_0x00c8
        L_0x00be:
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            java.lang.String r0 = r0.getSetTemp()     // Catch:{ Exception -> 0x05c5 }
            double r0 = java.lang.Double.parseDouble(r0)     // Catch:{ Exception -> 0x05c5 }
        L_0x00c8:
            it.tecnosystemi.TS.Model.ControlUnit r2 = r13.cu     // Catch:{ Exception -> 0x05c5 }
            int r2 = r2.getUnitOfMesure()     // Catch:{ Exception -> 0x05c5 }
            r3 = 1
            if (r2 != r3) goto L_0x00d9
            double r0 = it.tecnosystemi.TS.Utils.Functions.fromCtoF((double) r0)     // Catch:{ Exception -> 0x05c5 }
            int r0 = (int) r0     // Catch:{ Exception -> 0x05c5 }
            r13.temp = r0     // Catch:{ Exception -> 0x05c5 }
            goto L_0x00dc
        L_0x00d9:
            int r0 = (int) r0     // Catch:{ Exception -> 0x05c5 }
            r13.temp = r0     // Catch:{ Exception -> 0x05c5 }
        L_0x00dc:
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            java.lang.String r0 = r0.getTemp()     // Catch:{ Exception -> 0x05c5 }
            double r0 = java.lang.Double.parseDouble(r0)     // Catch:{ Exception -> 0x05c5 }
            r13.realtemp = r0     // Catch:{ Exception -> 0x05c5 }
            int r0 = r13.temp     // Catch:{ Exception -> 0x05c5 }
            java.lang.String r0 = java.lang.String.valueOf(r0)     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.ControlUnit r1 = r13.cu     // Catch:{ Exception -> 0x05c5 }
            int r1 = r1.getUnitOfMesure()     // Catch:{ Exception -> 0x05c5 }
            if (r1 != 0) goto L_0x00fd
            double r1 = r13.realtemp     // Catch:{ Exception -> 0x05c5 }
            java.lang.String r1 = java.lang.String.valueOf(r1)     // Catch:{ Exception -> 0x05c5 }
            goto L_0x0108
        L_0x00fd:
            double r1 = r13.realtemp     // Catch:{ Exception -> 0x05c5 }
            double r1 = it.tecnosystemi.TS.Utils.Functions.fromCtoF((double) r1)     // Catch:{ Exception -> 0x05c5 }
            int r1 = (int) r1     // Catch:{ Exception -> 0x05c5 }
            java.lang.String r1 = java.lang.String.valueOf(r1)     // Catch:{ Exception -> 0x05c5 }
        L_0x0108:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x05c5 }
            r2.<init>()     // Catch:{ Exception -> 0x05c5 }
            r2.append(r1)     // Catch:{ Exception -> 0x05c5 }
            java.lang.String r1 = "°"
            r2.append(r1)     // Catch:{ Exception -> 0x05c5 }
            java.lang.String r1 = r2.toString()     // Catch:{ Exception -> 0x05c5 }
            android.widget.TextView r2 = r13.lblRealTemp     // Catch:{ Exception -> 0x05c5 }
            r2.setText(r1)     // Catch:{ Exception -> 0x05c5 }
            android.widget.TextView r1 = r13.lblTemp     // Catch:{ Exception -> 0x05c5 }
            r1.setText(r0)     // Catch:{ Exception -> 0x05c5 }
            android.widget.TextView r0 = r13.lblGradi     // Catch:{ Exception -> 0x05c5 }
            r1 = 0
            r0.setVisibility(r1)     // Catch:{ Exception -> 0x05c5 }
            android.widget.Button r0 = r13.btnTempPlus     // Catch:{ Exception -> 0x05c5 }
            r0.setVisibility(r1)     // Catch:{ Exception -> 0x05c5 }
            android.widget.Button r0 = r13.btnTempMinus     // Catch:{ Exception -> 0x05c5 }
            r0.setVisibility(r1)     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            int r0 = r0.getManCrono()     // Catch:{ Exception -> 0x05c5 }
            r2 = 8
            if (r0 != r3) goto L_0x015b
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            boolean r0 = r0.isFasciaAttiva()     // Catch:{ Exception -> 0x05c5 }
            if (r0 != 0) goto L_0x015b
            android.widget.TextView r0 = r13.lblTemp     // Catch:{ Exception -> 0x05c5 }
            java.lang.String r4 = "--"
            r0.setText(r4)     // Catch:{ Exception -> 0x05c5 }
            android.widget.TextView r0 = r13.lblGradi     // Catch:{ Exception -> 0x05c5 }
            r0.setVisibility(r2)     // Catch:{ Exception -> 0x05c5 }
            android.widget.Button r0 = r13.btnTempPlus     // Catch:{ Exception -> 0x05c5 }
            r0.setVisibility(r2)     // Catch:{ Exception -> 0x05c5 }
            android.widget.Button r0 = r13.btnTempMinus     // Catch:{ Exception -> 0x05c5 }
            r0.setVisibility(r2)     // Catch:{ Exception -> 0x05c5 }
        L_0x015b:
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            java.lang.String r0 = r0.getUmd()     // Catch:{ Exception -> 0x05c5 }
            r4 = -1
            if (r0 != 0) goto L_0x0170
            android.widget.TextView r0 = r13.lblUmdIco     // Catch:{ Exception -> 0x05c5 }
            r0.setVisibility(r2)     // Catch:{ Exception -> 0x05c5 }
            android.widget.TextView r0 = r13.lblUmd     // Catch:{ Exception -> 0x05c5 }
            r0.setVisibility(r2)     // Catch:{ Exception -> 0x05c5 }
            goto L_0x01fe
        L_0x0170:
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x01f9 }
            java.lang.String r0 = r0.getUmd()     // Catch:{ Exception -> 0x01f9 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ Exception -> 0x01f9 }
            int r0 = r0 / 10
            android.widget.TextView r5 = r13.lblUmd     // Catch:{ Exception -> 0x01f9 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01f9 }
            r6.<init>()     // Catch:{ Exception -> 0x01f9 }
            r6.append(r0)     // Catch:{ Exception -> 0x01f9 }
            java.lang.String r7 = "%"
            r6.append(r7)     // Catch:{ Exception -> 0x01f9 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x01f9 }
            r5.setText(r6)     // Catch:{ Exception -> 0x01f9 }
            android.widget.TextView r5 = r13.lblUmd     // Catch:{ Exception -> 0x01f9 }
            r5.setVisibility(r1)     // Catch:{ Exception -> 0x01f9 }
            it.tecnosystemi.TS.Model.Zona r5 = r13.zona     // Catch:{ Exception -> 0x01f9 }
            boolean r5 = r5.isOff()     // Catch:{ Exception -> 0x01f9 }
            if (r5 == 0) goto L_0x01af
            android.widget.TextView r0 = r13.lblUmdIco     // Catch:{ Exception -> 0x01f9 }
            android.content.res.Resources r5 = r13.getResources()     // Catch:{ Exception -> 0x01f9 }
            int r6 = it.tecnosystemi.TS.R.drawable.btndisable     // Catch:{ Exception -> 0x01f9 }
            android.graphics.drawable.Drawable r5 = r5.getDrawable(r6)     // Catch:{ Exception -> 0x01f9 }
            r0.setBackground(r5)     // Catch:{ Exception -> 0x01f9 }
            goto L_0x01fe
        L_0x01af:
            it.tecnosystemi.TS.Model.ControlUnit r5 = r13.cu     // Catch:{ Exception -> 0x01f9 }
            int r5 = r5.getOperatingMode()     // Catch:{ Exception -> 0x01f9 }
            r6 = 2
            if (r5 != r6) goto L_0x01f3
            android.widget.TextView r5 = r13.lblUmdIco     // Catch:{ Exception -> 0x01f9 }
            r5.setVisibility(r1)     // Catch:{ Exception -> 0x01f9 }
            android.widget.TextView r5 = r13.lblUmdIco     // Catch:{ Exception -> 0x01f9 }
            r5.setTextColor(r4)     // Catch:{ Exception -> 0x01f9 }
            android.widget.TextView r5 = r13.lblUmdIco     // Catch:{ Exception -> 0x01f9 }
            android.content.res.Resources r6 = r13.getResources()     // Catch:{ Exception -> 0x01f9 }
            int r7 = it.tecnosystemi.TS.R.drawable.icon_zone_grey_back     // Catch:{ Exception -> 0x01f9 }
            android.graphics.drawable.Drawable r6 = r6.getDrawable(r7)     // Catch:{ Exception -> 0x01f9 }
            r5.setBackground(r6)     // Catch:{ Exception -> 0x01f9 }
            it.tecnosystemi.TS.Model.Zona r5 = r13.zona     // Catch:{ Exception -> 0x01fe }
            java.lang.String r5 = r5.getSetUmd()     // Catch:{ Exception -> 0x01fe }
            double r5 = java.lang.Double.parseDouble(r5)     // Catch:{ Exception -> 0x01fe }
            r7 = 4621819117588971520(0x4024000000000000, double:10.0)
            double r5 = r5 / r7
            double r7 = (double) r0     // Catch:{ Exception -> 0x01fe }
            int r0 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r0 >= 0) goto L_0x01fe
            android.widget.TextView r0 = r13.lblUmdIco     // Catch:{ Exception -> 0x01fe }
            android.content.res.Resources r5 = r13.getResources()     // Catch:{ Exception -> 0x01fe }
            int r6 = it.tecnosystemi.TS.R.drawable.icon_zone_green_back     // Catch:{ Exception -> 0x01fe }
            android.graphics.drawable.Drawable r5 = r5.getDrawable(r6)     // Catch:{ Exception -> 0x01fe }
            r0.setBackground(r5)     // Catch:{ Exception -> 0x01fe }
            goto L_0x01fe
        L_0x01f3:
            android.widget.TextView r0 = r13.lblUmdIco     // Catch:{ Exception -> 0x01f9 }
            r0.setVisibility(r2)     // Catch:{ Exception -> 0x01f9 }
            goto L_0x01fe
        L_0x01f9:
            android.widget.TextView r0 = r13.lblUmdIco     // Catch:{ Exception -> 0x05c5 }
            r0.setVisibility(r2)     // Catch:{ Exception -> 0x05c5 }
        L_0x01fe:
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            java.lang.Object r0 = r0.getCWin()     // Catch:{ Exception -> 0x05c5 }
            r5 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            r7 = 0
            r9 = -4616189618054758400(0xbff0000000000000, double:-1.0)
            if (r0 != 0) goto L_0x0213
            android.widget.TextView r0 = r13.lblCwinIco     // Catch:{ Exception -> 0x05c5 }
            r0.setVisibility(r2)     // Catch:{ Exception -> 0x05c5 }
            goto L_0x02b1
        L_0x0213:
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x02ac }
            java.lang.Object r0 = r0.getCWin()     // Catch:{ Exception -> 0x02ac }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x02ac }
            double r11 = java.lang.Double.parseDouble(r0)     // Catch:{ Exception -> 0x02ac }
            int r0 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r0 != 0) goto L_0x022c
            android.widget.TextView r0 = r13.lblCwinIco     // Catch:{ Exception -> 0x02ac }
            r0.setVisibility(r2)     // Catch:{ Exception -> 0x02ac }
            goto L_0x02b1
        L_0x022c:
            int r0 = (r11 > r7 ? 1 : (r11 == r7 ? 0 : -1))
            if (r0 != 0) goto L_0x026c
            android.widget.TextView r0 = r13.lblCwinIco     // Catch:{ Exception -> 0x02ac }
            r0.setVisibility(r1)     // Catch:{ Exception -> 0x02ac }
            android.widget.TextView r0 = r13.lblCwinIco     // Catch:{ Exception -> 0x02ac }
            android.content.res.Resources r11 = r13.getResources()     // Catch:{ Exception -> 0x02ac }
            int r12 = it.tecnosystemi.TS.R.string.icon_im_cwinClosed     // Catch:{ Exception -> 0x02ac }
            java.lang.String r11 = r11.getString(r12)     // Catch:{ Exception -> 0x02ac }
            r0.setText(r11)     // Catch:{ Exception -> 0x02ac }
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x02ac }
            boolean r0 = r0.isOff()     // Catch:{ Exception -> 0x02ac }
            if (r0 == 0) goto L_0x025c
            android.widget.TextView r0 = r13.lblCwinIco     // Catch:{ Exception -> 0x02ac }
            android.content.res.Resources r11 = r13.getResources()     // Catch:{ Exception -> 0x02ac }
            int r12 = it.tecnosystemi.TS.R.drawable.btndisable     // Catch:{ Exception -> 0x02ac }
            android.graphics.drawable.Drawable r11 = r11.getDrawable(r12)     // Catch:{ Exception -> 0x02ac }
            r0.setBackground(r11)     // Catch:{ Exception -> 0x02ac }
            goto L_0x02b1
        L_0x025c:
            android.widget.TextView r0 = r13.lblCwinIco     // Catch:{ Exception -> 0x02ac }
            android.content.res.Resources r11 = r13.getResources()     // Catch:{ Exception -> 0x02ac }
            int r12 = it.tecnosystemi.TS.R.drawable.icon_zone_green_back     // Catch:{ Exception -> 0x02ac }
            android.graphics.drawable.Drawable r11 = r11.getDrawable(r12)     // Catch:{ Exception -> 0x02ac }
            r0.setBackground(r11)     // Catch:{ Exception -> 0x02ac }
            goto L_0x02b1
        L_0x026c:
            int r0 = (r11 > r5 ? 1 : (r11 == r5 ? 0 : -1))
            if (r0 != 0) goto L_0x02b1
            android.widget.TextView r0 = r13.lblCwinIco     // Catch:{ Exception -> 0x02ac }
            r0.setVisibility(r1)     // Catch:{ Exception -> 0x02ac }
            android.widget.TextView r0 = r13.lblCwinIco     // Catch:{ Exception -> 0x02ac }
            android.content.res.Resources r11 = r13.getResources()     // Catch:{ Exception -> 0x02ac }
            int r12 = it.tecnosystemi.TS.R.string.icon_im_cwin     // Catch:{ Exception -> 0x02ac }
            java.lang.String r11 = r11.getString(r12)     // Catch:{ Exception -> 0x02ac }
            r0.setText(r11)     // Catch:{ Exception -> 0x02ac }
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x02ac }
            boolean r0 = r0.isOff()     // Catch:{ Exception -> 0x02ac }
            if (r0 == 0) goto L_0x029c
            android.widget.TextView r0 = r13.lblCwinIco     // Catch:{ Exception -> 0x02ac }
            android.content.res.Resources r11 = r13.getResources()     // Catch:{ Exception -> 0x02ac }
            int r12 = it.tecnosystemi.TS.R.drawable.btndisable     // Catch:{ Exception -> 0x02ac }
            android.graphics.drawable.Drawable r11 = r11.getDrawable(r12)     // Catch:{ Exception -> 0x02ac }
            r0.setBackground(r11)     // Catch:{ Exception -> 0x02ac }
            goto L_0x02b1
        L_0x029c:
            android.widget.TextView r0 = r13.lblCwinIco     // Catch:{ Exception -> 0x02ac }
            android.content.res.Resources r11 = r13.getResources()     // Catch:{ Exception -> 0x02ac }
            int r12 = it.tecnosystemi.TS.R.drawable.icon_zone_grey_back     // Catch:{ Exception -> 0x02ac }
            android.graphics.drawable.Drawable r11 = r11.getDrawable(r12)     // Catch:{ Exception -> 0x02ac }
            r0.setBackground(r11)     // Catch:{ Exception -> 0x02ac }
            goto L_0x02b1
        L_0x02ac:
            android.widget.TextView r0 = r13.lblCwinIco     // Catch:{ Exception -> 0x05c5 }
            r0.setVisibility(r2)     // Catch:{ Exception -> 0x05c5 }
        L_0x02b1:
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            java.lang.Object r0 = r0.getCBadge()     // Catch:{ Exception -> 0x05c5 }
            if (r0 != 0) goto L_0x02c0
            android.widget.TextView r0 = r13.lblCBadgeIco     // Catch:{ Exception -> 0x05c5 }
            r0.setVisibility(r2)     // Catch:{ Exception -> 0x05c5 }
            goto L_0x035e
        L_0x02c0:
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x0359 }
            java.lang.Object r0 = r0.getCBadge()     // Catch:{ Exception -> 0x0359 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0359 }
            double r11 = java.lang.Double.parseDouble(r0)     // Catch:{ Exception -> 0x0359 }
            int r0 = (r11 > r9 ? 1 : (r11 == r9 ? 0 : -1))
            if (r0 != 0) goto L_0x02d9
            android.widget.TextView r0 = r13.lblCBadgeIco     // Catch:{ Exception -> 0x0359 }
            r0.setVisibility(r2)     // Catch:{ Exception -> 0x0359 }
            goto L_0x035e
        L_0x02d9:
            int r0 = (r11 > r7 ? 1 : (r11 == r7 ? 0 : -1))
            if (r0 != 0) goto L_0x0319
            android.widget.TextView r0 = r13.lblCBadgeIco     // Catch:{ Exception -> 0x0359 }
            r0.setVisibility(r1)     // Catch:{ Exception -> 0x0359 }
            android.widget.TextView r0 = r13.lblCBadgeIco     // Catch:{ Exception -> 0x0359 }
            android.content.res.Resources r5 = r13.getResources()     // Catch:{ Exception -> 0x0359 }
            int r6 = it.tecnosystemi.TS.R.string.icon_im_cbadge     // Catch:{ Exception -> 0x0359 }
            java.lang.String r5 = r5.getString(r6)     // Catch:{ Exception -> 0x0359 }
            r0.setText(r5)     // Catch:{ Exception -> 0x0359 }
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x0359 }
            boolean r0 = r0.isOff()     // Catch:{ Exception -> 0x0359 }
            if (r0 == 0) goto L_0x0309
            android.widget.TextView r0 = r13.lblCBadgeIco     // Catch:{ Exception -> 0x0359 }
            android.content.res.Resources r5 = r13.getResources()     // Catch:{ Exception -> 0x0359 }
            int r6 = it.tecnosystemi.TS.R.drawable.btndisable     // Catch:{ Exception -> 0x0359 }
            android.graphics.drawable.Drawable r5 = r5.getDrawable(r6)     // Catch:{ Exception -> 0x0359 }
            r0.setBackground(r5)     // Catch:{ Exception -> 0x0359 }
            goto L_0x035e
        L_0x0309:
            android.widget.TextView r0 = r13.lblCBadgeIco     // Catch:{ Exception -> 0x0359 }
            android.content.res.Resources r5 = r13.getResources()     // Catch:{ Exception -> 0x0359 }
            int r6 = it.tecnosystemi.TS.R.drawable.icon_zone_green_back     // Catch:{ Exception -> 0x0359 }
            android.graphics.drawable.Drawable r5 = r5.getDrawable(r6)     // Catch:{ Exception -> 0x0359 }
            r0.setBackground(r5)     // Catch:{ Exception -> 0x0359 }
            goto L_0x035e
        L_0x0319:
            int r0 = (r11 > r5 ? 1 : (r11 == r5 ? 0 : -1))
            if (r0 != 0) goto L_0x035e
            android.widget.TextView r0 = r13.lblCBadgeIco     // Catch:{ Exception -> 0x0359 }
            r0.setVisibility(r1)     // Catch:{ Exception -> 0x0359 }
            android.widget.TextView r0 = r13.lblCBadgeIco     // Catch:{ Exception -> 0x0359 }
            android.content.res.Resources r5 = r13.getResources()     // Catch:{ Exception -> 0x0359 }
            int r6 = it.tecnosystemi.TS.R.string.icon_im_cbadge2     // Catch:{ Exception -> 0x0359 }
            java.lang.String r5 = r5.getString(r6)     // Catch:{ Exception -> 0x0359 }
            r0.setText(r5)     // Catch:{ Exception -> 0x0359 }
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x0359 }
            boolean r0 = r0.isOff()     // Catch:{ Exception -> 0x0359 }
            if (r0 == 0) goto L_0x0349
            android.widget.TextView r0 = r13.lblCBadgeIco     // Catch:{ Exception -> 0x0359 }
            android.content.res.Resources r5 = r13.getResources()     // Catch:{ Exception -> 0x0359 }
            int r6 = it.tecnosystemi.TS.R.drawable.btndisable     // Catch:{ Exception -> 0x0359 }
            android.graphics.drawable.Drawable r5 = r5.getDrawable(r6)     // Catch:{ Exception -> 0x0359 }
            r0.setBackground(r5)     // Catch:{ Exception -> 0x0359 }
            goto L_0x035e
        L_0x0349:
            android.widget.TextView r0 = r13.lblCBadgeIco     // Catch:{ Exception -> 0x0359 }
            android.content.res.Resources r5 = r13.getResources()     // Catch:{ Exception -> 0x0359 }
            int r6 = it.tecnosystemi.TS.R.drawable.icon_zone_grey_back     // Catch:{ Exception -> 0x0359 }
            android.graphics.drawable.Drawable r5 = r5.getDrawable(r6)     // Catch:{ Exception -> 0x0359 }
            r0.setBackground(r5)     // Catch:{ Exception -> 0x0359 }
            goto L_0x035e
        L_0x0359:
            android.widget.TextView r0 = r13.lblCBadgeIco     // Catch:{ Exception -> 0x05c5 }
            r0.setVisibility(r2)     // Catch:{ Exception -> 0x05c5 }
        L_0x035e:
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            boolean r0 = r0.isOff()     // Catch:{ Exception -> 0x05c5 }
            if (r0 == 0) goto L_0x0376
            android.widget.Button r0 = r13.btnOnOff     // Catch:{ Exception -> 0x05c5 }
            android.content.res.Resources r5 = r13.getResources()     // Catch:{ Exception -> 0x05c5 }
            int r6 = it.tecnosystemi.TS.R.color.textdisableselector     // Catch:{ Exception -> 0x05c5 }
            android.content.res.ColorStateList r5 = r5.getColorStateList(r6)     // Catch:{ Exception -> 0x05c5 }
            r0.setTextColor(r5)     // Catch:{ Exception -> 0x05c5 }
            goto L_0x0385
        L_0x0376:
            android.widget.Button r0 = r13.btnOnOff     // Catch:{ Exception -> 0x05c5 }
            android.content.res.Resources r5 = r13.getResources()     // Catch:{ Exception -> 0x05c5 }
            int r6 = it.tecnosystemi.TS.R.color.textprimaryselector     // Catch:{ Exception -> 0x05c5 }
            android.content.res.ColorStateList r5 = r5.getColorStateList(r6)     // Catch:{ Exception -> 0x05c5 }
            r0.setTextColor(r5)     // Catch:{ Exception -> 0x05c5 }
        L_0x0385:
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            int r0 = r0.getNumError()     // Catch:{ Exception -> 0x05c5 }
            if (r0 <= 0) goto L_0x03a8
            android.widget.TextView r0 = r13.lblErrorText     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r5 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            android.content.res.Resources r6 = r13.getResources()     // Catch:{ Exception -> 0x05c5 }
            int r7 = it.tecnosystemi.TS.R.array.zone_errors     // Catch:{ Exception -> 0x05c5 }
            java.lang.String[] r6 = r6.getStringArray(r7)     // Catch:{ Exception -> 0x05c5 }
            java.lang.String r5 = r5.getStringerrors(r6)     // Catch:{ Exception -> 0x05c5 }
            r0.setText(r5)     // Catch:{ Exception -> 0x05c5 }
            android.widget.TextView r0 = r13.lblErrorIcon     // Catch:{ Exception -> 0x05c5 }
            r0.setVisibility(r1)     // Catch:{ Exception -> 0x05c5 }
            goto L_0x03ad
        L_0x03a8:
            android.widget.TextView r0 = r13.lblErrorIcon     // Catch:{ Exception -> 0x05c5 }
            r0.setVisibility(r2)     // Catch:{ Exception -> 0x05c5 }
        L_0x03ad:
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            int r0 = r0.getFancoil()     // Catch:{ Exception -> 0x05c5 }
            r5 = 4
            r6 = 16
            if (r0 != r4) goto L_0x03be
            android.widget.Button r0 = r13.btnFancoil     // Catch:{ Exception -> 0x05c5 }
            r0.setVisibility(r5)     // Catch:{ Exception -> 0x05c5 }
            goto L_0x0419
        L_0x03be:
            android.widget.Button r0 = r13.btnFancoil     // Catch:{ Exception -> 0x05c5 }
            r0.setVisibility(r1)     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            int r0 = r0.getFancoil()     // Catch:{ Exception -> 0x05c5 }
            if (r0 <= r6) goto L_0x03d6
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            int r7 = r0.getFancoil()     // Catch:{ Exception -> 0x05c5 }
            int r7 = r7 + -13
            r0.setFancoil(r7)     // Catch:{ Exception -> 0x05c5 }
        L_0x03d6:
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            int r0 = r0.getFancoil()     // Catch:{ Exception -> 0x05c5 }
            if (r0 != r6) goto L_0x03e3
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            r0.setFancoil(r1)     // Catch:{ Exception -> 0x05c5 }
        L_0x03e3:
            android.widget.Button r0 = r13.btnFancoil     // Catch:{ Exception -> 0x05c5 }
            java.lang.String[] r7 = it.tecnosystemi.TS.Utils.Constants.ZONE_FANCOIL_ICON     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r8 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            int r8 = r8.getFancoil()     // Catch:{ Exception -> 0x05c5 }
            r7 = r7[r8]     // Catch:{ Exception -> 0x05c5 }
            r0.setText(r7)     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            int r0 = r0.getFancoil()     // Catch:{ Exception -> 0x05c5 }
            if (r0 != 0) goto L_0x040a
            android.widget.Button r0 = r13.btnFancoil     // Catch:{ Exception -> 0x05c5 }
            android.content.res.Resources r7 = r13.getResources()     // Catch:{ Exception -> 0x05c5 }
            int r8 = it.tecnosystemi.TS.R.drawable.btndisable     // Catch:{ Exception -> 0x05c5 }
            android.graphics.drawable.Drawable r7 = r7.getDrawable(r8)     // Catch:{ Exception -> 0x05c5 }
            r0.setBackground(r7)     // Catch:{ Exception -> 0x05c5 }
            goto L_0x0419
        L_0x040a:
            android.widget.Button r0 = r13.btnFancoil     // Catch:{ Exception -> 0x05c5 }
            android.content.res.Resources r7 = r13.getResources()     // Catch:{ Exception -> 0x05c5 }
            int r8 = it.tecnosystemi.TS.R.drawable.btn_selector     // Catch:{ Exception -> 0x05c5 }
            android.graphics.drawable.Drawable r7 = r7.getDrawable(r8)     // Catch:{ Exception -> 0x05c5 }
            r0.setBackground(r7)     // Catch:{ Exception -> 0x05c5 }
        L_0x0419:
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            int r0 = r0.getFancoilSet()     // Catch:{ Exception -> 0x05c5 }
            r7 = 7
            if (r0 < r6) goto L_0x0427
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            r0.setFancoilSet(r7)     // Catch:{ Exception -> 0x05c5 }
        L_0x0427:
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            int r0 = r0.getEV()     // Catch:{ Exception -> 0x05c5 }
            if (r0 != r4) goto L_0x0435
            android.widget.Button r0 = r13.btnElettrovalvola     // Catch:{ Exception -> 0x05c5 }
            r0.setVisibility(r5)     // Catch:{ Exception -> 0x05c5 }
            goto L_0x0461
        L_0x0435:
            android.widget.Button r0 = r13.btnElettrovalvola     // Catch:{ Exception -> 0x05c5 }
            r0.setVisibility(r1)     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            int r0 = r0.getEV()     // Catch:{ Exception -> 0x05c5 }
            if (r0 != 0) goto L_0x0452
            android.widget.Button r0 = r13.btnElettrovalvola     // Catch:{ Exception -> 0x05c5 }
            android.content.res.Resources r8 = r13.getResources()     // Catch:{ Exception -> 0x05c5 }
            int r9 = it.tecnosystemi.TS.R.drawable.btndisable     // Catch:{ Exception -> 0x05c5 }
            android.graphics.drawable.Drawable r8 = r8.getDrawable(r9)     // Catch:{ Exception -> 0x05c5 }
            r0.setBackground(r8)     // Catch:{ Exception -> 0x05c5 }
            goto L_0x0461
        L_0x0452:
            android.widget.Button r0 = r13.btnElettrovalvola     // Catch:{ Exception -> 0x05c5 }
            android.content.res.Resources r8 = r13.getResources()     // Catch:{ Exception -> 0x05c5 }
            int r9 = it.tecnosystemi.TS.R.drawable.btn_selector     // Catch:{ Exception -> 0x05c5 }
            android.graphics.drawable.Drawable r8 = r8.getDrawable(r9)     // Catch:{ Exception -> 0x05c5 }
            r0.setBackground(r8)     // Catch:{ Exception -> 0x05c5 }
        L_0x0461:
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            int r0 = r0.getSerranda()     // Catch:{ Exception -> 0x05c5 }
            if (r0 != r4) goto L_0x046f
            android.widget.Button r0 = r13.btnSerranda     // Catch:{ Exception -> 0x05c5 }
            r0.setVisibility(r5)     // Catch:{ Exception -> 0x05c5 }
            goto L_0x04ca
        L_0x046f:
            android.widget.Button r0 = r13.btnSerranda     // Catch:{ Exception -> 0x05c5 }
            r0.setVisibility(r1)     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            int r0 = r0.getSerranda()     // Catch:{ Exception -> 0x05c5 }
            if (r0 <= r6) goto L_0x0487
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            int r4 = r0.getSerranda()     // Catch:{ Exception -> 0x05c5 }
            int r4 = r4 + -13
            r0.setSerranda(r4)     // Catch:{ Exception -> 0x05c5 }
        L_0x0487:
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            int r0 = r0.getSerranda()     // Catch:{ Exception -> 0x05c5 }
            if (r0 != r6) goto L_0x0494
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            r0.setSerranda(r1)     // Catch:{ Exception -> 0x05c5 }
        L_0x0494:
            android.widget.Button r0 = r13.btnSerranda     // Catch:{ Exception -> 0x05c5 }
            java.lang.String[] r4 = it.tecnosystemi.TS.Utils.Constants.ZONE_SERRANDA_ICON     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r5 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            int r5 = r5.getSerranda()     // Catch:{ Exception -> 0x05c5 }
            r4 = r4[r5]     // Catch:{ Exception -> 0x05c5 }
            r0.setText(r4)     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            int r0 = r0.getSerranda()     // Catch:{ Exception -> 0x05c5 }
            if (r0 != 0) goto L_0x04bb
            android.widget.Button r0 = r13.btnSerranda     // Catch:{ Exception -> 0x05c5 }
            android.content.res.Resources r4 = r13.getResources()     // Catch:{ Exception -> 0x05c5 }
            int r5 = it.tecnosystemi.TS.R.drawable.btndisable     // Catch:{ Exception -> 0x05c5 }
            android.graphics.drawable.Drawable r4 = r4.getDrawable(r5)     // Catch:{ Exception -> 0x05c5 }
            r0.setBackground(r4)     // Catch:{ Exception -> 0x05c5 }
            goto L_0x04ca
        L_0x04bb:
            android.widget.Button r0 = r13.btnSerranda     // Catch:{ Exception -> 0x05c5 }
            android.content.res.Resources r4 = r13.getResources()     // Catch:{ Exception -> 0x05c5 }
            int r5 = it.tecnosystemi.TS.R.drawable.btn_selector     // Catch:{ Exception -> 0x05c5 }
            android.graphics.drawable.Drawable r4 = r4.getDrawable(r5)     // Catch:{ Exception -> 0x05c5 }
            r0.setBackground(r4)     // Catch:{ Exception -> 0x05c5 }
        L_0x04ca:
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            int r0 = r0.getSerrandaSet()     // Catch:{ Exception -> 0x05c5 }
            if (r0 < r6) goto L_0x04d7
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            r0.setSerrandaSet(r7)     // Catch:{ Exception -> 0x05c5 }
        L_0x04d7:
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            boolean r0 = r0.isCronoMode()     // Catch:{ Exception -> 0x05c5 }
            if (r0 == 0) goto L_0x04e5
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            r0.setManCrono(r3)     // Catch:{ Exception -> 0x05c5 }
            goto L_0x04ea
        L_0x04e5:
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            r0.setManCrono(r1)     // Catch:{ Exception -> 0x05c5 }
        L_0x04ea:
            android.widget.Button r0 = r13.btnManCrono     // Catch:{ Exception -> 0x05c5 }
            java.lang.String[] r4 = it.tecnosystemi.TS.Utils.Constants.ZONE_MANCRONO_ICON     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r5 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            int r5 = r5.getManCrono()     // Catch:{ Exception -> 0x05c5 }
            r4 = r4[r5]     // Catch:{ Exception -> 0x05c5 }
            r0.setText(r4)     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            boolean r0 = r0.isFasciaAttiva()     // Catch:{ Exception -> 0x05c5 }
            if (r0 == 0) goto L_0x0529
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            boolean r0 = r0.isOff()     // Catch:{ Exception -> 0x05c5 }
            if (r0 == 0) goto L_0x050a
            goto L_0x0529
        L_0x050a:
            android.widget.TextView r0 = r13.lblCronoOnOff     // Catch:{ Exception -> 0x05c5 }
            android.content.res.Resources r4 = r13.getResources()     // Catch:{ Exception -> 0x05c5 }
            int r5 = it.tecnosystemi.TS.R.string.za_cronoON     // Catch:{ Exception -> 0x05c5 }
            java.lang.String r4 = r4.getString(r5)     // Catch:{ Exception -> 0x05c5 }
            r0.setText(r4)     // Catch:{ Exception -> 0x05c5 }
            android.widget.TextView r0 = r13.lblCronoOnOff     // Catch:{ Exception -> 0x05c5 }
            android.content.res.Resources r4 = r13.getResources()     // Catch:{ Exception -> 0x05c5 }
            int r5 = it.tecnosystemi.TS.R.color.colorPrimary     // Catch:{ Exception -> 0x05c5 }
            int r4 = r4.getColor(r5)     // Catch:{ Exception -> 0x05c5 }
            r0.setTextColor(r4)     // Catch:{ Exception -> 0x05c5 }
            goto L_0x0547
        L_0x0529:
            android.widget.TextView r0 = r13.lblCronoOnOff     // Catch:{ Exception -> 0x05c5 }
            android.content.res.Resources r4 = r13.getResources()     // Catch:{ Exception -> 0x05c5 }
            int r5 = it.tecnosystemi.TS.R.string.za_cronoOFF     // Catch:{ Exception -> 0x05c5 }
            java.lang.String r4 = r4.getString(r5)     // Catch:{ Exception -> 0x05c5 }
            r0.setText(r4)     // Catch:{ Exception -> 0x05c5 }
            android.widget.TextView r0 = r13.lblCronoOnOff     // Catch:{ Exception -> 0x05c5 }
            android.content.res.Resources r4 = r13.getResources()     // Catch:{ Exception -> 0x05c5 }
            int r5 = it.tecnosystemi.TS.R.color.colordisable     // Catch:{ Exception -> 0x05c5 }
            int r4 = r4.getColor(r5)     // Catch:{ Exception -> 0x05c5 }
            r0.setTextColor(r4)     // Catch:{ Exception -> 0x05c5 }
        L_0x0547:
            it.tecnosystemi.TS.Model.Zona r0 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            int r0 = r0.getManCrono()     // Catch:{ Exception -> 0x05c5 }
            if (r0 != 0) goto L_0x0555
            android.widget.TextView r0 = r13.lblCronoOnOff     // Catch:{ Exception -> 0x05c5 }
            r0.setVisibility(r2)     // Catch:{ Exception -> 0x05c5 }
            goto L_0x055a
        L_0x0555:
            android.widget.TextView r0 = r13.lblCronoOnOff     // Catch:{ Exception -> 0x05c5 }
            r0.setVisibility(r1)     // Catch:{ Exception -> 0x05c5 }
        L_0x055a:
            android.widget.Button r0 = r13.btnManCrono     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r2 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            boolean r2 = r2.isOff()     // Catch:{ Exception -> 0x05c5 }
            r2 = r2 ^ r3
            r0.setEnabled(r2)     // Catch:{ Exception -> 0x05c5 }
            android.widget.Button r0 = r13.btnElettrovalvola     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r2 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            boolean r2 = r2.isOff()     // Catch:{ Exception -> 0x05c5 }
            r2 = r2 ^ r3
            r0.setEnabled(r2)     // Catch:{ Exception -> 0x05c5 }
            android.widget.Button r0 = r13.btnSerranda     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r2 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            boolean r2 = r2.isOff()     // Catch:{ Exception -> 0x05c5 }
            r2 = r2 ^ r3
            r0.setEnabled(r2)     // Catch:{ Exception -> 0x05c5 }
            android.widget.Button r0 = r13.btnFancoil     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.Zona r2 = r13.zona     // Catch:{ Exception -> 0x05c5 }
            boolean r2 = r2.isOff()     // Catch:{ Exception -> 0x05c5 }
            r2 = r2 ^ r3
            r0.setEnabled(r2)     // Catch:{ Exception -> 0x05c5 }
            it.tecnosystemi.TS.Model.ControlUnit r0 = r13.cu     // Catch:{ Exception -> 0x05c5 }
            boolean r0 = r0.getIsOff()     // Catch:{ Exception -> 0x05c5 }
            if (r0 == 0) goto L_0x05d7
            android.widget.Button r0 = r13.btnOnOff     // Catch:{ Exception -> 0x05c5 }
            r0.setEnabled(r1)     // Catch:{ Exception -> 0x05c5 }
            android.widget.Button r0 = r13.btnOnOff     // Catch:{ Exception -> 0x05c5 }
            android.content.res.Resources r2 = r13.getResources()     // Catch:{ Exception -> 0x05c5 }
            int r3 = it.tecnosystemi.TS.R.color.colordisable     // Catch:{ Exception -> 0x05c5 }
            int r2 = r2.getColor(r3)     // Catch:{ Exception -> 0x05c5 }
            r0.setTextColor(r2)     // Catch:{ Exception -> 0x05c5 }
            android.widget.Button r0 = r13.btnManCrono     // Catch:{ Exception -> 0x05c5 }
            r0.setEnabled(r1)     // Catch:{ Exception -> 0x05c5 }
            android.widget.Button r0 = r13.btnElettrovalvola     // Catch:{ Exception -> 0x05c5 }
            r0.setEnabled(r1)     // Catch:{ Exception -> 0x05c5 }
            android.widget.Button r0 = r13.btnSerranda     // Catch:{ Exception -> 0x05c5 }
            r0.setEnabled(r1)     // Catch:{ Exception -> 0x05c5 }
            android.widget.Button r0 = r13.btnFancoil     // Catch:{ Exception -> 0x05c5 }
            r0.setEnabled(r1)     // Catch:{ Exception -> 0x05c5 }
            android.widget.Button r0 = r13.btnTempPlus     // Catch:{ Exception -> 0x05c5 }
            r0.setEnabled(r1)     // Catch:{ Exception -> 0x05c5 }
            android.widget.Button r0 = r13.btnTempMinus     // Catch:{ Exception -> 0x05c5 }
            r0.setEnabled(r1)     // Catch:{ Exception -> 0x05c5 }
            goto L_0x05d7
        L_0x05c5:
            android.widget.TextView r0 = r13.lblErrorText
            android.content.res.Resources r1 = r13.getResources()
            int r2 = it.tecnosystemi.TS.R.array.zone_errors
            java.lang.String[] r1 = r1.getStringArray(r2)
            r2 = 5
            r1 = r1[r2]
            r0.setText(r1)
        L_0x05d7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.ZoneActivity.setZoneValues():void");
    }

    public void saveData() {
        if (Constants.ISDEMO) {
            this.zona.setName(this.tempZona.getName());
            this.zona.setManCrono(this.tempZona.getManCrono());
            this.zona.setCronoMode(this.tempZona.isCronoMode());
            this.zona.setOff(this.tempZona.isOff());
            this.zona.setErrors(this.tempZona.getErrors());
            this.zona.setEV(this.tempZona.getEV());
            this.zona.setFancoilSet(this.tempZona.getFancoilSet());
            this.zona.setFancoil(this.tempZona.getFancoilSet());
            this.zona.setSerrandaSet(this.tempZona.getSerrandaSet());
            this.zona.setSetTemp(this.tempZona.getSetTemp());
            this.zona.setSerranda(this.tempZona.getSerrandaSet());
            Constants.DEMO_CU.getZone().set(this.indexZona, this.tempZona);
            showProgress();
            new Thread(new Runnable() {
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (Exception unused) {
                    }
                    ZoneActivity.this.hideProgress();
                    ZoneActivity.this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            ZoneActivity.this.setZoneValues();
                        }
                    });
                }
            }).start();
        } else if (!somethingchanged()) {
            final JSONObject update_ZONA_Command = this.tempZona.update_ZONA_Command();
            if (this.cu.isOffline()) {
                try {
                    update_ZONA_Command.put(Constants.INTENT_PIN, this.cu.getPinOffline());
                    new Thread(new Runnable() {
                        public void run() {
                            ZoneActivity.this.sendingstate = true;
                            try {
                                String commandToCU = MySocket.commandToCU(update_ZONA_Command.toString(), Constants.ip, Constants.port, true, true, true);
                                if (commandToCU != null && !commandToCU.isEmpty()) {
                                    JSONObject jSONObject = new JSONObject(commandToCU);
                                    if (jSONObject.has(Constants.JSON_RES) && jSONObject.getInt(Constants.JSON_RES) == 1) {
                                        ZoneActivity.this.activity.runOnUiThread(new Runnable() {
                                            public void run() {
                                                Functions.makeNormalToast(ZoneActivity.this.activity, ZoneActivity.this.getResources().getString(R.string.msg_commandOk));
                                            }
                                        });
                                    }
                                }
                            } catch (Exception unused) {
                            }
                            ZoneActivity.this.sendingstate = false;
                        }
                    }).start();
                } catch (Exception unused) {
                    this.sendingstate = false;
                }
            } else {
                new Thread(new Runnable() {
                    public void run() {
                        ZoneActivity.this.sendingstate = true;
                        int i = -1;
                        if (ZoneActivity.this.cu.getIp() != null && !ZoneActivity.this.cu.getIp().isEmpty()) {
                            try {
                                MySocket.initInstance(ZoneActivity.this.activity, ZoneActivity.this.activity, true);
                                update_ZONA_Command.put(Constants.INTENT_PIN, ZoneActivity.this.cu.getPin());
                                String commandToCU = MySocket.commandToCU(update_ZONA_Command.toString(), ZoneActivity.this.cu.getIp(), Constants.port, false, true, false);
                                if (commandToCU != null && !commandToCU.isEmpty()) {
                                    JSONObject jSONObject = new JSONObject(commandToCU);
                                    if (jSONObject.has(Constants.JSON_RES)) {
                                        i = jSONObject.getInt(Constants.JSON_RES);
                                        if (jSONObject.getInt(Constants.JSON_RES) == 1) {
                                            ZoneActivity.this.sendingstate = false;
                                        } else {
                                            ZoneActivity.this.activity.runOnUiThread(new Runnable() {
                                                public void run() {
                                                    Functions.makeNormalToast(ZoneActivity.this.activity, ZoneActivity.this.getResources().getString(R.string.msg_commandKo));
                                                }
                                            });
                                            ZoneActivity.this.sendingstate = false;
                                            return;
                                        }
                                    }
                                }
                            } catch (Exception unused) {
                                ZoneActivity.this.sendingstate = false;
                            }
                        }
                        JSONObject jSONObject2 = new JSONObject();
                        try {
                            update_ZONA_Command.put(Constants.INTENT_PIN, ZoneActivity.this.cu.getPin());
                            jSONObject2.put(Constants.JSON_CU_SERIAL, ZoneActivity.this.cu.getSerial());
                            jSONObject2.put(Constants.JSON_CU_NAME, ZoneActivity.this.cu.getName());
                            jSONObject2.put(Constants.JSON_CU_PIN, ZoneActivity.this.cu.getPin());
                            jSONObject2.put(Constants.JSON_ZONE_ID, ZoneActivity.this.zona.getZoneId());
                            jSONObject2.put("Cmd", update_ZONA_Command.toString());
                            String[] strArr = new String[2];
                            String str = ZoneActivity.this.getResources().getString(R.string.uriWebService_POLARIS) + ZoneActivity.this.getResources().getString(R.string.uri_UpdZonaState);
                            if (i == 1) {
                                strArr[0] = Constants.COMMANDSENT;
                                str = str + "?create_command=false";
                            } else {
                                strArr[0] = Constants.COMMAND10MIN;
                            }
                            strArr[1] = ZoneActivity.this.cu.getFWVer();
                            new ThreadWebService(ZoneActivity.this.activity, 1, 13, str, jSONObject2.toString(), strArr).start();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            ZoneActivity.this.sendingstate = false;
                        }
                    }
                }).start();
            }
        }
    }

    public void resetTemp() {
        setValuesFromCU();
    }

    public boolean somethingchanged() {
        return this.zona.isOff() == this.tempZona.isOff() && this.zona.getFancoilSet() == this.tempZona.getFancoilSet() && this.tempZona.isCronoMode() == this.tempZona.isCronoMode() && this.zona.getSerrandaSet() == this.tempZona.getSerrandaSet() && this.zona.getSetTemp() == this.tempZona.getSetTemp() && this.zona.getName().equals(this.tempZona.getName());
    }

    public void createPopUpRinominaZona() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        arrayList.add(this.zona.getName());
        arrayList2.add(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i != 6) {
                    return false;
                }
                ZoneActivity.this.tempZona.setName(textView.getText().toString());
                ZoneActivity.this.dismissdialog();
                ZoneActivity.this.saveData();
                return false;
            }
        });
        this.bundlePopUp = createTxtPopUp(getResources().getString(R.string.za_menuRinomina), arrayList, "", arrayList2, 20);
    }

    public void createPopUpFancoil() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        arrayList.add(getResources().getString(R.string.za_menuFancoilAA));
        arrayList.add(getResources().getString(R.string.za_menuFancoilA3));
        arrayList.add(getResources().getString(R.string.za_menuFancoilA2));
        arrayList.add(getResources().getString(R.string.za_menuFancoilA1));
        arrayList2.add(Constants.ZONE_FANCOIL_ICON[7]);
        arrayList2.add(Constants.ZONE_FANCOIL_ICON[3]);
        arrayList2.add(Constants.ZONE_FANCOIL_ICON[2]);
        int i = 1;
        arrayList2.add(Constants.ZONE_FANCOIL_ICON[1]);
        arrayList3.add(new Runnable() {
            public void run() {
                ZoneActivity.this.tempZona.setFancoilSet(7);
                ZoneActivity.this.tempZona.setLastFancoil(true);
                ZoneActivity.this.dismissdialog();
                ZoneActivity.this.saveData();
            }
        });
        arrayList3.add(new Runnable() {
            public void run() {
                ZoneActivity.this.tempZona.setFancoilSet(3);
                ZoneActivity.this.tempZona.setLastFancoil(true);
                ZoneActivity.this.dismissdialog();
                ZoneActivity.this.saveData();
            }
        });
        arrayList3.add(new Runnable() {
            public void run() {
                ZoneActivity.this.tempZona.setFancoilSet(2);
                ZoneActivity.this.tempZona.setLastFancoil(true);
                ZoneActivity.this.dismissdialog();
                ZoneActivity.this.saveData();
            }
        });
        arrayList3.add(new Runnable() {
            public void run() {
                ZoneActivity.this.tempZona.setFancoilSet(1);
                ZoneActivity.this.tempZona.setLastFancoil(true);
                ZoneActivity.this.dismissdialog();
                ZoneActivity.this.saveData();
            }
        });
        int fancoilSet = this.zona.getFancoilSet();
        if (fancoilSet == 1) {
            i = 3;
        } else if (fancoilSet == 2) {
            i = 2;
        } else if (fancoilSet != 3) {
            i = fancoilSet != 7 ? -1 : 0;
        }
        this.bundlePopUp = createPopUp(true, getResources().getString(R.string.za_menuFancoilTitle), arrayList, arrayList2, (List<String>) null, arrayList3, i, true);
    }

    public void createPopUpSerranda() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        arrayList.add(getResources().getString(R.string.za_menuSerrandaAA));
        arrayList.add(getResources().getString(R.string.za_menuSerrandaA3));
        arrayList.add(getResources().getString(R.string.za_menuSerrandaA2));
        arrayList.add(getResources().getString(R.string.za_menuSerrandaA1));
        arrayList2.add(Constants.ZONE_SERRANDA_ICON[7]);
        arrayList2.add(Constants.ZONE_SERRANDA_ICON[3]);
        arrayList2.add(Constants.ZONE_SERRANDA_ICON[2]);
        int i = 1;
        arrayList2.add(Constants.ZONE_SERRANDA_ICON[1]);
        arrayList3.add(new Runnable() {
            public void run() {
                ZoneActivity.this.tempZona.setSerrandaSet(7);
                ZoneActivity.this.tempZona.setLastFancoil(false);
                ZoneActivity.this.dismissdialog();
                ZoneActivity.this.saveData();
            }
        });
        arrayList3.add(new Runnable() {
            public void run() {
                ZoneActivity.this.tempZona.setSerrandaSet(3);
                ZoneActivity.this.tempZona.setLastFancoil(false);
                ZoneActivity.this.dismissdialog();
                ZoneActivity.this.saveData();
            }
        });
        arrayList3.add(new Runnable() {
            public void run() {
                ZoneActivity.this.tempZona.setSerrandaSet(2);
                ZoneActivity.this.tempZona.setLastFancoil(false);
                ZoneActivity.this.dismissdialog();
                ZoneActivity.this.saveData();
            }
        });
        arrayList3.add(new Runnable() {
            public void run() {
                ZoneActivity.this.tempZona.setSerrandaSet(1);
                ZoneActivity.this.tempZona.setLastFancoil(false);
                ZoneActivity.this.dismissdialog();
                ZoneActivity.this.saveData();
            }
        });
        int serrandaSet = this.zona.getSerrandaSet();
        if (serrandaSet == 1) {
            i = 3;
        } else if (serrandaSet == 2) {
            i = 2;
        } else if (serrandaSet != 3) {
            i = serrandaSet != 7 ? -1 : 0;
        }
        this.bundlePopUp = createPopUp(true, getResources().getString(R.string.za_menuSerrandaTitle), arrayList, arrayList2, (List<String>) null, arrayList3, i, true);
    }

    public void createPopUpCronoManSetCrono() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        arrayList.add(getResources().getString(R.string.za_menuManuale));
        arrayList.add(getResources().getString(R.string.za_menuCrono));
        arrayList.add(getResources().getString(R.string.za_menuVaiAProgrammazione));
        arrayList2.add(Constants.ZONE_MANCRONO_ICON[0]);
        arrayList2.add(Constants.ZONE_MANCRONO_ICON[1]);
        arrayList2.add(Constants.ZONE_MANCRONO_ICON[1]);
        arrayList3.add(new Runnable() {
            public void run() {
                ZoneActivity.this.tempZona.setManCrono(0);
                ZoneActivity.this.tempZona.setCronoMode(false);
                ZoneActivity.this.dismissdialog();
                ZoneActivity.this.saveData();
            }
        });
        arrayList3.add(new Runnable() {
            public void run() {
                ZoneActivity.this.tempZona.setManCrono(1);
                ZoneActivity.this.tempZona.setCronoMode(true);
                ZoneActivity.this.dismissdialog();
                ZoneActivity.this.saveData();
            }
        });
        arrayList3.add(new Runnable() {
            public void run() {
                ZoneActivity.this.dismissdialog();
                ZoneActivity.this.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        Intent intent = new Intent(ZoneActivity.this.activity, CronoSummaryActivity.class);
                        intent.putExtra(Constants.INTENT_ZONA, ZoneActivity.this.zona);
                        intent.putExtra(Constants.INTENT_INDEXZONA, ZoneActivity.this.indexZona);
                        intent.putExtra(Constants.INTENT_CU, ZoneActivity.this.cu);
                        ZoneActivity.this.startActivity(intent);
                    }
                });
            }
        });
        this.bundlePopUp = createPopUp(true, getResources().getString(R.string.za_menuManCronoTitle), arrayList, arrayList2, (List<String>) null, arrayList3, this.tempZona.getManCrono(), true);
    }

    public void btnOnOff(View view) {
        this.tempZona.setOff(!this.zona.isOff());
        saveData();
    }

    public void btnFancoil(View view) {
        createPopUpFancoil();
        openDialogFragment(this.bundlePopUp);
    }

    public void btnEV(View view) {
        Functions.makeNormalToast(this, getResources().getString(R.string.za_EVInfo));
    }

    public void btnSerranda(View view) {
        createPopUpSerranda();
        openDialogFragment(this.bundlePopUp);
    }

    public void btnCronos(View view) {
        createPopUpCronoManSetCrono();
        openDialogFragment(this.bundlePopUp);
    }

    public void getCuState(JSONObject jSONObject) {
        this.gettingstate = false;
        if (jSONObject == null) {
            this.zona.setErrors(Functions.geterror(32));
            this.zona.setNumError(1);
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    ZoneActivity.this.setValuesFromCU();
                }
            });
            return;
        }
        this.firtCalltoGetState = false;
        try {
            if (!this.cu.isOffline()) {
                this.cu = ControlUnit.mergeFromGetState(this.cu, ControlUnit.getCuFromJSONFromServer(jSONObject, this));
            }
        } catch (Exception unused) {
        }
        this.activity.runOnUiThread(new Runnable() {
            public void run() {
                ZoneActivity.this.setValuesFromCU();
            }
        });
    }

    public void getZoneState(JSONObject jSONObject) {
        boolean z = false;
        this.gettingstate = false;
        if (jSONObject == null) {
            this.zona.setErrors(Functions.geterror(32));
            this.zona.setNumError(1);
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    ZoneActivity.this.setZoneValues();
                }
            });
            return;
        }
        if (jSONObject.has(Constants.JSON_OFFLINE_COMMAND_ISOFFCU)) {
            try {
                ControlUnit controlUnit = this.cu;
                if (jSONObject.getInt(Constants.JSON_OFFLINE_COMMAND_ISOFFCU) == 1) {
                    z = true;
                }
                controlUnit.setIsOff(z);
            } catch (Exception unused) {
            }
        }
        this.zona = Zona.getZonaFromJsonOffline(jSONObject);
        this.activity.runOnUiThread(new Runnable() {
            public void run() {
                ZoneActivity.this.setZoneValues();
            }
        });
    }

    public void btnTempPlus(View view) {
        if ((this.zona.getManCrono() != 1 || this.zona.isFasciaAttiva()) && !this.zona.isOff()) {
            this.temp++;
            if (this.cu.getUnitOfMesure() == 1) {
                if (this.temp <= Constants.tempmaxF) {
                    this.tempZona.setSetTemp(String.valueOf(Functions.fromFtoC(this.temp)));
                } else {
                    this.temp = Constants.tempmaxF;
                }
            } else if (this.temp <= Constants.tempmaxC) {
                this.tempZona.setSetTemp(String.valueOf(this.temp));
            } else {
                this.temp = Constants.tempmaxC;
            }
            this.lblTemp.setText(String.valueOf(this.temp));
            inizializeModifiTemp();
            this.modifitemp.start();
        }
    }

    public void btnTempMinus(View view) {
        if (!this.zona.isOff()) {
            if (this.zona.getManCrono() != 1 || this.zona.isFasciaAttiva()) {
                this.temp--;
                if (this.cu.getUnitOfMesure() == 1) {
                    if (this.temp >= Constants.tempminF) {
                        this.tempZona.setSetTemp(String.valueOf(Functions.fromFtoC(this.temp)));
                    } else {
                        this.temp = Constants.tempminF;
                    }
                } else if (this.temp >= Constants.tempminC) {
                    this.tempZona.setSetTemp(String.valueOf(this.temp));
                } else {
                    this.temp = Constants.tempminC;
                }
                this.lblTemp.setText(String.valueOf(this.temp));
                inizializeModifiTemp();
                this.modifitemp.start();
            }
        }
    }

    public void onResume() {
        super.onResume();
        if (!Constants.ISDEMO) {
            this.gettingstate = false;
            inizializeGetState(this.cu.isOffline());
            this.getState.start();
        }
    }

    public void onDestroy() {
        super.onDestroy();
        if (!Constants.ISDEMO) {
            stopgetState();
            Thread thread = this.modifitemp;
            if (thread != null && thread.isAlive()) {
                this.modifitemp.interrupt();
                saveData();
            }
        }
    }

    public void onPause() {
        stopgetState();
        super.onPause();
    }

    public View getToolBar() {
        return findViewById(R.id.za_toolbar);
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        AnonymousClass21 r5 = new Runnable() {
            public void run() {
                ZoneActivity.this.dismissdialog();
                ZoneActivity.this.createPopUpRinominaZona();
                ZoneActivity zoneActivity = ZoneActivity.this;
                zoneActivity.openDialogFragment(zoneActivity.bundlePopUp);
            }
        };
        list.add(createMenuItem(true, getResources().getString(R.string.za_menuRinomina), "", "", r5, false, false));
        return list;
    }

    public String setToolbarTitle() {
        return this.zona.getName();
    }
}
