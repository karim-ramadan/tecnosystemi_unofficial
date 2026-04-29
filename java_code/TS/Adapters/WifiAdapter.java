package it.tecnosystemi.TS.Adapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Activity.Config.ChooseWifiActivity;
import it.tecnosystemi.TS.Activity.PICO.Config.ChooseWifiPICOActivity;
import it.tecnosystemi.TS.Activity.VMC.Config.ChooseWifiVMCActivity;
import it.tecnosystemi.TS.Model.ControlUnit;
import it.tecnosystemi.TS.Model.WiFi;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import java.util.List;

public class WifiAdapter extends ArrayAdapter<WiFi> {
    BaseActivity context;
    int type;
    List<WiFi> wifiList;

    public WifiAdapter(BaseActivity baseActivity, List<WiFi> list, int i) {
        super(baseActivity, R.layout.riga_wifi, list);
        this.wifiList = list;
        this.context = baseActivity;
        this.type = i;
    }

    public WiFi getItem(int i) {
        return this.wifiList.get(i);
    }

    public int getPosition(ControlUnit controlUnit) {
        return this.wifiList.indexOf(controlUnit);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.riga_wifi, (ViewGroup) null);
        }
        if (this.type == Constants.DEVICE_TYPE_PICO) {
            ((ConstraintLayout) view.findViewById(R.id.ly_riga_wifi)).setBackground(this.context.getResources().getDrawable(R.drawable.lw_item_selector_pico));
        }
        if (this.type == Constants.DEVICE_TYPE_VMC) {
            ((ConstraintLayout) view.findViewById(R.id.ly_riga_wifi)).setBackground(this.context.getResources().getDrawable(R.drawable.lw_item_selector_vmc));
        }
        final WiFi item = getItem(i);
        Typeface createFromAsset = Typeface.createFromAsset(this.context.getAssets(), "fonts/fontawesome.ttf");
        Typeface createFromAsset2 = Typeface.createFromAsset(this.context.getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
        Typeface createFromAsset3 = Typeface.createFromAsset(this.context.getAssets(), "fonts/icomoon.ttf");
        TextView textView = (TextView) view.findViewById(R.id.lblNomeWifi);
        textView.setText(item.getSid());
        ((TextView) view.findViewById(R.id.lblWifiLogo)).setTypeface(createFromAsset);
        textView.setTypeface(createFromAsset2);
        ((TextView) view.findViewById(R.id.lblIconFrecia)).setTypeface(createFromAsset3);
        ((TextView) view.findViewById(R.id.lblMacAddress)).setText(item.getMac());
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                boolean z = false;
                if (WifiAdapter.this.context instanceof ChooseWifiActivity) {
                    ChooseWifiActivity chooseWifiActivity = (ChooseWifiActivity) WifiAdapter.this.context;
                    WiFi wiFi = item;
                    if (wiFi.isCrip() == 1) {
                        z = true;
                    }
                    chooseWifiActivity.protectedwifi(wiFi, z);
                } else if (WifiAdapter.this.context instanceof ChooseWifiPICOActivity) {
                    ChooseWifiPICOActivity chooseWifiPICOActivity = (ChooseWifiPICOActivity) WifiAdapter.this.context;
                    WiFi wiFi2 = item;
                    if (wiFi2.isCrip() == 1) {
                        z = true;
                    }
                    chooseWifiPICOActivity.protectedwifi(wiFi2, z);
                } else if (WifiAdapter.this.context instanceof ChooseWifiVMCActivity) {
                    ChooseWifiVMCActivity chooseWifiVMCActivity = (ChooseWifiVMCActivity) WifiAdapter.this.context;
                    WiFi wiFi3 = item;
                    if (wiFi3.isCrip() == 1) {
                        z = true;
                    }
                    chooseWifiVMCActivity.protectedwifi(wiFi3, z);
                }
            }
        });
        return view;
    }
}
