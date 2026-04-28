package it.tecnosystemi.TS.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<String> {
    Typeface avenir = Typeface.createFromAsset(getContext().getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
    Typeface fontawesome = Typeface.createFromAsset(getContext().getAssets(), "fonts/fontawesome.ttf");
    boolean isIcons;

    public SpinnerAdapter(Context context, int i, List<String> list, boolean z) {
        super(context, i, list);
        this.isIcons = z;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView textView = (TextView) super.getView(i, view, viewGroup);
        if (this.isIcons) {
            textView.setTypeface(this.fontawesome);
        } else {
            textView.setTypeface(this.avenir);
        }
        return textView;
    }

    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        TextView textView = (TextView) super.getDropDownView(i, view, viewGroup);
        if (this.isIcons) {
            textView.setTypeface(this.fontawesome);
        } else {
            textView.setTypeface(this.avenir);
        }
        return textView;
    }
}
