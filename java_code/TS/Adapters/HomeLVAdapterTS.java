package it.tecnosystemi.TS.Adapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Activity.TS.TSHomeActivity;
import it.tecnosystemi.TS.Model.ControlUnit;
import it.tecnosystemi.TS.Model.Plant;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import java.util.ArrayList;
import java.util.List;

public class HomeLVAdapterTS extends ArrayAdapter<Plant> {
    boolean cancella;
    BaseActivity context;
    List<Plant> plList;

    public HomeLVAdapterTS(BaseActivity baseActivity, List<Plant> list, boolean z) {
        super(baseActivity, R.layout.riga_lwhome, list);
        this.plList = new ArrayList(list);
        this.context = baseActivity;
        this.cancella = z;
    }

    public Plant getItem(int i) {
        return this.plList.get(i);
    }

    public int getPosition(ControlUnit controlUnit) {
        return this.plList.indexOf(controlUnit);
    }

    public void changeCancella(boolean z) {
        this.cancella = z;
    }

    public void changeDataSet(List<Plant> list) {
        this.plList = new ArrayList(list);
        notifyDataSetChanged();
    }

    public int getCount() {
        return this.plList.size();
    }

    public int getPosition(Plant plant) {
        return this.plList.indexOf(plant);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.riga_lwhome, (ViewGroup) null);
        }
        final Plant item = getItem(i);
        Button button = (Button) view.findViewById(R.id.lwHome_button);
        TextView textView = (TextView) view.findViewById(R.id.lblError);
        TextView textView2 = (TextView) view.findViewById(R.id.lblrigaicontype);
        Typeface createFromAsset = Typeface.createFromAsset(this.context.getAssets(), "fonts/fontawesome.ttf");
        Typeface createFromAsset2 = Typeface.createFromAsset(this.context.getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
        textView2.setTypeface(createFromAsset);
        textView.setTypeface(createFromAsset);
        if (item.getLVPL_Icon() == -1) {
            textView2.setVisibility(0);
            textView2.setText(Constants.ICON_TYPE[0]);
        } else {
            textView2.setVisibility(0);
            textView2.setText(Constants.ICON_TYPE[item.getLVPL_Icon()]);
        }
        button.setTextColor(-1);
        if (item.getNumError() == 0) {
            textView.setVisibility(8);
        } else {
            textView.setVisibility(0);
            if (!this.cancella) {
                button.setTextColor(this.context.getResources().getColor(R.color.colorerror_ts));
                textView.setTextColor(this.context.getResources().getColor(R.color.colorerror_ts));
            }
        }
        button.setText(item.getLVPL_Name().toUpperCase());
        if (this.cancella) {
            button.setBackground(this.context.getResources().getDrawable(R.drawable.btn_selector_darker_ts));
        } else {
            button.setBackground(this.context.getResources().getDrawable(R.drawable.btn_selector_ts));
        }
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (HomeLVAdapterTS.this.cancella) {
                    ((TSHomeActivity) HomeLVAdapterTS.this.context).cancellaplant(item);
                    return;
                }
                Plant plant = item;
                ((TSHomeActivity) HomeLVAdapterTS.this.context).connectplant(plant, HomeLVAdapterTS.this.getPosition(plant));
            }
        });
        button.setTypeface(createFromAsset2);
        return view;
    }
}
