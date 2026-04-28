package it.tecnosystemi.TS.Adapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Activity.TS.TSDeviceListActivity;
import it.tecnosystemi.TS.Model.Device;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import java.util.ArrayList;
import java.util.List;

public class DeviceLVAdapterTS extends ArrayAdapter<Device> {
    boolean cancella;
    BaseActivity context;
    List<Device> dvList;

    public DeviceLVAdapterTS(BaseActivity baseActivity, List<Device> list, boolean z) {
        super(baseActivity, R.layout.riga_devicets, list);
        this.dvList = new ArrayList(list);
        this.context = baseActivity;
        this.cancella = z;
    }

    public Device getItem(int i) {
        return this.dvList.get(i);
    }

    public int getPosition(Device device) {
        return this.dvList.indexOf(device);
    }

    public void changeCancella(boolean z) {
        this.cancella = z;
    }

    public void changeDataSet(List<Device> list) {
        this.dvList = new ArrayList(list);
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.dvList.size();
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.riga_devicets, (ViewGroup) null);
        }
        final Device item = getItem(i);
        Button button = (Button) view.findViewById(R.id.lwHome_button);
        TextView textView = (TextView) view.findViewById(R.id.lblError);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView2);
        Typeface createFromAsset = Typeface.createFromAsset(this.context.getAssets(), "fonts/fontawesome.ttf");
        Typeface createFromAsset2 = Typeface.createFromAsset(this.context.getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
        textView.setTypeface(createFromAsset);
        button.setTextColor(-1);
        if (item.getNUM_ERROR() == 0) {
            textView.setVisibility(8);
        } else {
            textView.setVisibility(0);
            if (!this.cancella) {
                button.setTextColor(this.context.getResources().getColor(R.color.colorerror_ts));
                textView.setTextColor(this.context.getResources().getColor(R.color.colorerror_ts));
            }
        }
        if (item.getLVDV_Type() == Constants.DEVICE_TYPE_PROAIR) {
            imageView.setImageDrawable(this.context.getResources().getDrawable(R.drawable.logo_proair));
        } else if (item.getLVDV_Type() == Constants.DEVICE_TYPE_PICO) {
            imageView.setImageDrawable(this.context.getResources().getDrawable(R.drawable.logo_pico_new_btn));
        } else if (item.getLVDV_Type() == Constants.DEVICE_TYPE_VMC) {
            imageView.setImageDrawable(this.context.getResources().getDrawable(R.drawable.logo_vmc));
        }
        button.setText(item.getName().toUpperCase());
        if (this.cancella) {
            button.setBackground(this.context.getResources().getDrawable(R.drawable.btn_selector_darker_ts));
        } else {
            button.setBackground(this.context.getResources().getDrawable(R.drawable.btn_selector_ts));
        }
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (DeviceLVAdapterTS.this.cancella) {
                    ((TSDeviceListActivity) DeviceLVAdapterTS.this.context).cancellaDevice(item);
                } else {
                    ((TSDeviceListActivity) DeviceLVAdapterTS.this.context).connectDevice(item);
                }
            }
        });
        button.setTypeface(createFromAsset2);
        return view;
    }
}
