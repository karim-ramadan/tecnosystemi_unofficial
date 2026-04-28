package it.tecnosystemi.TS.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import java.util.List;

public class SpinnerCUIconsAdapterTS extends ArrayAdapter<String> {
    Typeface avenir = Typeface.createFromAsset(getContext().getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
    Context context;
    Typeface fontawesome = Typeface.createFromAsset(getContext().getAssets(), "fonts/fontawesome.ttf");
    boolean isIcons;

    public SpinnerCUIconsAdapterTS(Context context2, List<String> list) {
        super(context2, R.layout.riga_cuicon_ts, list);
        this.context = context2;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.riga_cuicon_ts, (ViewGroup) null);
        }
        TextView textView = (TextView) view.findViewById(R.id.txtCuIcon);
        TextView textView2 = (TextView) view.findViewById(R.id.txtIconName);
        textView.setText(Constants.ICON_TYPE[i]);
        textView2.setText(this.context.getResources().getStringArray(R.array.cu_Icons)[i]);
        textView.setTypeface(this.fontawesome);
        textView2.setTypeface(this.avenir);
        return view;
    }

    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.riga_cuicon_ts, (ViewGroup) null);
        }
        TextView textView = (TextView) view.findViewById(R.id.txtCuIcon);
        TextView textView2 = (TextView) view.findViewById(R.id.txtIconName);
        textView.setText(Constants.ICON_TYPE[i]);
        textView2.setText(this.context.getResources().getStringArray(R.array.cu_Icons)[i]);
        textView.setTypeface(this.fontawesome);
        textView2.setTypeface(this.avenir);
        return view;
    }
}
