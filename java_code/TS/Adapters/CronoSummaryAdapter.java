package it.tecnosystemi.TS.Adapters;

import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Activity.CronoSetActivity;
import it.tecnosystemi.TS.Model.ControlUnit;
import it.tecnosystemi.TS.Model.Crono;
import it.tecnosystemi.TS.Model.Zona;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.util.List;

public class CronoSummaryAdapter extends ArrayAdapter<List<Crono>> {
    BaseActivity context;
    List<List<Crono>> cronos;
    ControlUnit cu;
    int unitm;
    Zona zona;

    public CronoSummaryAdapter(BaseActivity baseActivity, ControlUnit controlUnit, Zona zona2, int i) {
        super(baseActivity, R.layout.riga_cronosummary, zona2.getCrono());
        this.context = baseActivity;
        this.cu = controlUnit;
        this.zona = zona2;
        this.cronos = zona2.getCrono();
        this.unitm = i;
    }

    public List<Crono> getItem(int i) {
        return this.cronos.get(i);
    }

    public int getPosition(List<Crono> list) {
        return this.cronos.indexOf(list);
    }

    private static class ViewHolder {
        TextView lblCrono1;
        TextView lblCrono2;
        TextView lblCrono3;
        TextView lblCrono4;
        TextView lblCronoD;

        private ViewHolder() {
        }
    }

    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        String str;
        String str2;
        String str3;
        String str4;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.riga_cronosummary, (ViewGroup) null);
            viewHolder = new ViewHolder();
            viewHolder.lblCrono1 = (TextView) view.findViewById(R.id.lblTimeZFascia1);
            viewHolder.lblCrono2 = (TextView) view.findViewById(R.id.lblTimeZFascia2);
            viewHolder.lblCrono3 = (TextView) view.findViewById(R.id.lblTimeZFascia3);
            viewHolder.lblCrono4 = (TextView) view.findViewById(R.id.lblTimeZFascia4);
            viewHolder.lblCronoD = (TextView) view.findViewById(R.id.lblTimeZDay);
            Typeface createFromAsset = Typeface.createFromAsset(this.context.getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
            viewHolder.lblCrono1.setTypeface(createFromAsset);
            viewHolder.lblCrono2.setTypeface(createFromAsset);
            viewHolder.lblCrono3.setTypeface(createFromAsset);
            viewHolder.lblCrono4.setTypeface(createFromAsset);
            viewHolder.lblCronoD.setTypeface(createFromAsset);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        List item = getItem(i);
        if (isvalid((Crono) item.get(0))) {
            Crono crono = (Crono) item.get(0);
            String temperatura = crono.getTemperatura();
            while (temperatura.contains("°")) {
                temperatura = temperatura.replace("°", "");
            }
            if (this.unitm == 1) {
                int fromCtoF = (int) Functions.fromCtoF(Double.parseDouble(temperatura));
                str4 = crono.getStartTime() + "\n" + crono.getEndTime() + "\n" + String.valueOf(fromCtoF) + "°";
            } else {
                int parseDouble = (int) Double.parseDouble(temperatura);
                str4 = crono.getStartTime() + "\n" + crono.getEndTime() + "\n" + String.valueOf(parseDouble) + "°";
            }
            viewHolder.lblCrono1.setText(str4);
        } else {
            viewHolder.lblCrono1.setText("");
        }
        if (isvalid((Crono) item.get(1))) {
            Crono crono2 = (Crono) item.get(1);
            String temperatura2 = crono2.getTemperatura();
            while (temperatura2.contains("°")) {
                temperatura2 = temperatura2.replace("°", "");
            }
            if (this.unitm == 1) {
                int fromCtoF2 = (int) Functions.fromCtoF(Double.parseDouble(temperatura2));
                str3 = crono2.getStartTime() + "\n" + crono2.getEndTime() + "\n" + String.valueOf(fromCtoF2) + "°";
            } else {
                int parseDouble2 = (int) Double.parseDouble(temperatura2);
                str3 = crono2.getStartTime() + "\n" + crono2.getEndTime() + "\n" + String.valueOf(parseDouble2) + "°";
            }
            viewHolder.lblCrono2.setText(str3);
        } else {
            viewHolder.lblCrono2.setText("");
        }
        if (isvalid((Crono) item.get(2))) {
            Crono crono3 = (Crono) item.get(2);
            String temperatura3 = crono3.getTemperatura();
            while (temperatura3.contains("°")) {
                temperatura3 = temperatura3.replace("°", "");
            }
            if (this.unitm == 1) {
                str2 = crono3.getStartTime() + "\n" + crono3.getEndTime() + "\n" + String.valueOf((int) Functions.fromCtoF(Double.parseDouble(temperatura3))) + "°";
            } else {
                str2 = crono3.getStartTime() + "\n" + crono3.getEndTime() + "\n" + String.valueOf((int) Double.parseDouble(temperatura3)) + "°";
            }
            viewHolder.lblCrono3.setText(str2);
        } else {
            viewHolder.lblCrono3.setText("");
        }
        if (isvalid((Crono) item.get(3))) {
            Crono crono4 = (Crono) item.get(3);
            String temperatura4 = crono4.getTemperatura();
            while (temperatura4.contains("°")) {
                temperatura4 = temperatura4.replace("°", "");
            }
            if (this.unitm == 1) {
                int fromCtoF3 = (int) Functions.fromCtoF(Double.parseDouble(temperatura4));
                str = crono4.getStartTime() + "\n" + crono4.getEndTime() + "\n" + String.valueOf(fromCtoF3) + "°";
            } else {
                int parseDouble3 = (int) Double.parseDouble(temperatura4);
                str = crono4.getStartTime() + "\n" + crono4.getEndTime() + "\n" + String.valueOf(parseDouble3) + "°";
            }
            viewHolder.lblCrono4.setText(str);
        } else {
            viewHolder.lblCrono4.setText("");
        }
        if (i == 0) {
            viewHolder.lblCronoD.setText(this.context.getResources().getString(R.string.cs_DayL));
            view.setBackground(this.context.getResources().getDrawable(R.drawable.crono_summary_selector_green));
        } else if (i == 1) {
            viewHolder.lblCronoD.setText(this.context.getResources().getString(R.string.cs_DayMa));
            view.setBackground(this.context.getResources().getDrawable(R.drawable.crono_summary_selector_trasparent));
        } else if (i == 2) {
            viewHolder.lblCronoD.setText(this.context.getResources().getString(R.string.cs_DayMe));
            view.setBackground(this.context.getResources().getDrawable(R.drawable.crono_summary_selector_green));
        } else if (i == 3) {
            viewHolder.lblCronoD.setText(this.context.getResources().getString(R.string.cs_DayG));
            view.setBackground(this.context.getResources().getDrawable(R.drawable.crono_summary_selector_trasparent));
        } else if (i == 4) {
            viewHolder.lblCronoD.setText(this.context.getResources().getString(R.string.cs_DayV));
            view.setBackground(this.context.getResources().getDrawable(R.drawable.crono_summary_selector_green));
        } else if (i == 5) {
            viewHolder.lblCronoD.setText(this.context.getResources().getString(R.string.cs_DayS));
            view.setBackground(this.context.getResources().getDrawable(R.drawable.crono_summary_selector_trasparent));
        } else if (i == 6) {
            viewHolder.lblCronoD.setText(this.context.getResources().getString(R.string.cs_DayD));
            view.setBackground(this.context.getResources().getDrawable(R.drawable.crono_summary_selector_green));
        }
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(CronoSummaryAdapter.this.context, CronoSetActivity.class);
                intent.putExtra(Constants.INTENT_ZONA, CronoSummaryAdapter.this.zona);
                intent.putExtra(Constants.INTENT_CU, CronoSummaryAdapter.this.cu);
                intent.putExtra("index", i);
                intent.putExtra(Constants.INTENT_UNITM, CronoSummaryAdapter.this.unitm);
                CronoSummaryAdapter.this.context.startActivity(intent);
            }
        });
        return view;
    }

    public boolean isvalid(Crono crono) {
        if (crono == null || crono.getTemperatura() == null || crono.getEndTime() == null || crono.getStartTime() == null || crono.getTemperatura().isEmpty() || crono.getEndTime().isEmpty() || crono.getStartTime().isEmpty()) {
            return false;
        }
        return true;
    }
}
