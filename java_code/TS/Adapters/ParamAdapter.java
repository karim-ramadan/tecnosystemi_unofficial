package it.tecnosystemi.TS.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import it.tecnosystemi.TS.Model.ModBusRecipe;
import it.tecnosystemi.TS.R;
import java.util.List;

public class ParamAdapter extends ArrayAdapter<ModBusRecipe.Param> {
    Typeface avenir;
    Context context;
    Typeface icon;
    List<ModBusRecipe.Param> listmenu;

    public ParamAdapter(Context context2, List<ModBusRecipe.Param> list) {
        super(context2, R.layout.adapter_param, list);
        this.listmenu = list;
        this.context = context2;
        this.icon = Typeface.createFromAsset(context2.getAssets(), "fonts/icomoon.ttf");
        this.avenir = Typeface.createFromAsset(context2.getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
    }

    public ModBusRecipe.Param getItem(int i) {
        return this.listmenu.get(i);
    }

    public int getCount() {
        List<ModBusRecipe.Param> list = this.listmenu;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    static class ViewHolder {
        TextView lblDescr;
        TextView lblFreccia;
        TextView lblName;
        TextView lblUM;
        TextView lblValore;
        ConstraintLayout llyMenu;

        ViewHolder(View view) {
            this.lblName = (TextView) view.findViewById(R.id.lblName);
            this.lblValore = (TextView) view.findViewById(R.id.lblVal);
            this.lblFreccia = (TextView) view.findViewById(R.id.lblFreccia);
            this.lblDescr = (TextView) view.findViewById(R.id.lblDescr);
            this.lblUM = (TextView) view.findViewById(R.id.lblUM);
            this.llyMenu = (ConstraintLayout) view.findViewById(R.id.llyMenu);
            view.setTag(this);
        }
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View view2;
        ViewHolder viewHolder;
        if (view == null) {
            view2 = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.adapter_param, viewGroup, false);
            viewHolder = new ViewHolder(view2);
        } else {
            view2 = view;
            viewHolder = (ViewHolder) view.getTag();
        }
        ModBusRecipe.Param item = getItem(i);
        viewHolder.lblName.setText(item.getName());
        viewHolder.lblDescr.setText(item.getSdesc());
        viewHolder.lblFreccia.setTypeface(this.icon);
        viewHolder.lblName.setTypeface(this.avenir);
        viewHolder.lblDescr.setTypeface(this.avenir);
        if (item.getParsedValue() != -999.0d) {
            viewHolder.lblValore.setText(item.getValToShow(false));
        } else {
            viewHolder.lblValore.setText("---");
        }
        viewHolder.lblValore.setTypeface(this.avenir);
        viewHolder.lblFreccia.setVisibility(item.isPRPP_Editable() ? 0 : 4);
        if (item.getPRPA_PRPT_Id() == 1 || item.getPRPA_PRPT_Id() == 4 || item.getPRPA_PRPT_Id() == 5) {
            viewHolder.lblUM.setVisibility(0);
            viewHolder.lblUM.setText(item.getPRPA_UM());
        } else {
            viewHolder.lblUM.setVisibility(8);
        }
        viewHolder.lblUM.setTypeface(this.avenir);
        if (item.getIndicepercolore() % 2 == 1) {
            viewHolder.llyMenu.setBackground(this.context.getResources().getDrawable(R.drawable.crono_summary_selector_trasparent_vmc));
        } else {
            viewHolder.llyMenu.setBackground(this.context.getResources().getDrawable(R.drawable.crono_summary_selector_vmc));
        }
        return view2;
    }
}
