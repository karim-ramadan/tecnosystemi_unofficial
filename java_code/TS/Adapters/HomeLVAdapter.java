package it.tecnosystemi.TS.Adapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import it.tecnosystemi.TS.Activity.HomeActivity;
import it.tecnosystemi.TS.Model.ControlUnit;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import java.util.List;

public class HomeLVAdapter extends ArrayAdapter<ControlUnit> {
    boolean cancella;
    HomeActivity context;
    List<ControlUnit> cuList;

    public HomeLVAdapter(HomeActivity homeActivity, List<ControlUnit> list, boolean z) {
        super(homeActivity, R.layout.riga_lwhome, list);
        this.cuList = list;
        this.context = homeActivity;
        this.cancella = z;
    }

    public ControlUnit getItem(int i) {
        return this.cuList.get(i);
    }

    public int getPosition(ControlUnit controlUnit) {
        return this.cuList.indexOf(controlUnit);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.riga_lwhome, (ViewGroup) null);
        }
        final ControlUnit item = getItem(i);
        Button button = (Button) view.findViewById(R.id.lwHome_button);
        TextView textView = (TextView) view.findViewById(R.id.lblError);
        TextView textView2 = (TextView) view.findViewById(R.id.lblrigaicontype);
        Typeface createFromAsset = Typeface.createFromAsset(this.context.getAssets(), "fonts/fontawesome.ttf");
        Typeface createFromAsset2 = Typeface.createFromAsset(this.context.getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
        textView2.setTypeface(createFromAsset);
        textView.setTypeface(createFromAsset);
        if (item.getIcontype() == -1) {
            textView2.setVisibility(0);
            textView2.setText(Constants.ICON_TYPE[0]);
        } else {
            textView2.setVisibility(0);
            textView2.setText(Constants.ICON_TYPE[item.getIcontype()]);
        }
        button.setTextColor(-1);
        if (item.getNumError() == 0) {
            textView.setVisibility(8);
        } else {
            textView.setVisibility(0);
            if (!this.cancella) {
                button.setTextColor(this.context.getResources().getColor(R.color.colorerror));
            }
        }
        button.setText(item.getName().toUpperCase());
        if (this.cancella) {
            button.setBackground(this.context.getResources().getDrawable(R.drawable.btnred));
        }
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (HomeLVAdapter.this.cancella) {
                    HomeLVAdapter.this.context.cancellacentralina(item);
                    return;
                }
                HomeActivity homeActivity = HomeLVAdapter.this.context;
                ControlUnit controlUnit = item;
                homeActivity.connectcentralina(controlUnit, HomeLVAdapter.this.getPosition(controlUnit));
            }
        });
        button.setTypeface(createFromAsset2);
        return view;
    }
}
