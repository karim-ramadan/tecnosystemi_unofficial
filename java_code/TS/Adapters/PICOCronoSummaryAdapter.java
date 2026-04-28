package it.tecnosystemi.TS.Adapters;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Activity.PICO.PicoCronoSetActivity;
import it.tecnosystemi.TS.Activity.VMC.VMCCronoSetActivity;
import it.tecnosystemi.TS.Activity.VMC.VMCCronoSummaryActivity;
import it.tecnosystemi.TS.Model.PICOCronoObj;
import it.tecnosystemi.TS.R;
import java.util.List;

public class PICOCronoSummaryAdapter extends ArrayAdapter<PICOCronoObj> {
    BaseActivity activity;
    List<PICOCronoObj> cronos;

    public PICOCronoSummaryAdapter(BaseActivity baseActivity, List<PICOCronoObj> list) {
        super(baseActivity, R.layout.riga_cronosummary_pico, list);
        this.cronos = list;
        this.activity = baseActivity;
    }

    public PICOCronoObj getItem(int i) {
        return this.cronos.get(i);
    }

    public int getPosition(PICOCronoObj pICOCronoObj) {
        return this.cronos.indexOf(pICOCronoObj);
    }

    private static class ViewHolder {
        ImageView ivCrono1Icon1;
        ImageView ivCrono1Icon2;
        ImageView ivCrono1Icon3;
        ImageView ivCrono2Icon1;
        ImageView ivCrono2Icon2;
        ImageView ivCrono2Icon3;
        ImageView ivCrono3Icon1;
        ImageView ivCrono3Icon2;
        ImageView ivCrono3Icon3;
        ImageView ivCrono4Icon1;
        ImageView ivCrono4Icon2;
        ImageView ivCrono4Icon3;
        TextView lblCrono1;
        TextView lblCrono2;
        TextView lblCrono3;
        TextView lblCrono4;
        TextView lblCronoD;

        private ViewHolder() {
        }
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.riga_cronosummary_pico, (ViewGroup) null);
            viewHolder = new ViewHolder();
            viewHolder.lblCrono1 = (TextView) view.findViewById(R.id.lblTimeZFascia1);
            viewHolder.lblCrono2 = (TextView) view.findViewById(R.id.lblTimeZFascia2);
            viewHolder.lblCrono3 = (TextView) view.findViewById(R.id.lblTimeZFascia3);
            viewHolder.lblCrono4 = (TextView) view.findViewById(R.id.lblTimeZFascia4);
            viewHolder.lblCronoD = (TextView) view.findViewById(R.id.lblTimeZDay);
            viewHolder.ivCrono1Icon1 = (ImageView) view.findViewById(R.id.ivCrono1Icon1);
            viewHolder.ivCrono1Icon2 = (ImageView) view.findViewById(R.id.ivCrono1Icon2);
            viewHolder.ivCrono1Icon3 = (ImageView) view.findViewById(R.id.ivCrono1Icon3);
            viewHolder.ivCrono2Icon1 = (ImageView) view.findViewById(R.id.ivCrono2Icon1);
            viewHolder.ivCrono2Icon2 = (ImageView) view.findViewById(R.id.ivCrono2Icon2);
            viewHolder.ivCrono2Icon3 = (ImageView) view.findViewById(R.id.ivCrono2Icon3);
            viewHolder.ivCrono3Icon1 = (ImageView) view.findViewById(R.id.ivCrono3Icon1);
            viewHolder.ivCrono3Icon2 = (ImageView) view.findViewById(R.id.ivCrono3Icon2);
            viewHolder.ivCrono3Icon3 = (ImageView) view.findViewById(R.id.ivCrono3Icon3);
            viewHolder.ivCrono4Icon1 = (ImageView) view.findViewById(R.id.ivCrono4Icon1);
            viewHolder.ivCrono4Icon2 = (ImageView) view.findViewById(R.id.ivCrono4Icon2);
            viewHolder.ivCrono4Icon3 = (ImageView) view.findViewById(R.id.ivCrono4Icon3);
            viewHolder.lblCrono1.setTypeface(BaseActivity.avenir);
            viewHolder.lblCrono2.setTypeface(BaseActivity.avenir);
            viewHolder.lblCrono3.setTypeface(BaseActivity.avenir);
            viewHolder.lblCrono4.setTypeface(BaseActivity.avenir);
            viewHolder.lblCronoD.setTypeface(BaseActivity.avenir);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        final PICOCronoObj item = getItem(i);
        view.setBackground(item.getBackground());
        viewHolder.lblCronoD.setText(item.getName());
        viewHolder.lblCrono1.setText(item.getTime(0));
        viewHolder.lblCrono2.setText(item.getTime(1));
        viewHolder.lblCrono3.setText(item.getTime(2));
        viewHolder.lblCrono4.setText(item.getTime(3));
        if (item.getImage2(this.activity, 0) == null) {
            viewHolder.ivCrono1Icon1.setImageDrawable((Drawable) null);
            viewHolder.ivCrono1Icon2.setImageDrawable((Drawable) null);
            viewHolder.ivCrono1Icon3.setImageDrawable(item.getImage1(this.activity, 0));
        } else {
            viewHolder.ivCrono1Icon1.setImageDrawable(item.getImage1(this.activity, 0));
            viewHolder.ivCrono1Icon2.setImageDrawable(item.getImage2(this.activity, 0));
            viewHolder.ivCrono1Icon3.setImageDrawable((Drawable) null);
        }
        if (item.getImage2(this.activity, 1) == null) {
            viewHolder.ivCrono2Icon1.setImageDrawable((Drawable) null);
            viewHolder.ivCrono2Icon2.setImageDrawable((Drawable) null);
            viewHolder.ivCrono2Icon3.setImageDrawable(item.getImage1(this.activity, 1));
        } else {
            viewHolder.ivCrono2Icon1.setImageDrawable(item.getImage1(this.activity, 1));
            viewHolder.ivCrono2Icon2.setImageDrawable(item.getImage2(this.activity, 1));
            viewHolder.ivCrono2Icon3.setImageDrawable((Drawable) null);
        }
        if (item.getImage2(this.activity, 2) == null) {
            viewHolder.ivCrono3Icon1.setImageDrawable((Drawable) null);
            viewHolder.ivCrono3Icon2.setImageDrawable((Drawable) null);
            viewHolder.ivCrono3Icon3.setImageDrawable(item.getImage1(this.activity, 2));
        } else {
            viewHolder.ivCrono3Icon1.setImageDrawable(item.getImage1(this.activity, 2));
            viewHolder.ivCrono3Icon2.setImageDrawable(item.getImage2(this.activity, 2));
            viewHolder.ivCrono3Icon3.setImageDrawable((Drawable) null);
        }
        if (item.getImage2(this.activity, 3) == null) {
            viewHolder.ivCrono4Icon1.setImageDrawable((Drawable) null);
            viewHolder.ivCrono4Icon2.setImageDrawable((Drawable) null);
            viewHolder.ivCrono4Icon3.setImageDrawable(item.getImage1(this.activity, 3));
        } else {
            viewHolder.ivCrono4Icon1.setImageDrawable(item.getImage1(this.activity, 3));
            viewHolder.ivCrono4Icon2.setImageDrawable(item.getImage2(this.activity, 3));
            viewHolder.ivCrono4Icon3.setImageDrawable((Drawable) null);
        }
        view.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (PICOCronoSummaryAdapter.this.activity instanceof VMCCronoSummaryActivity) {
                    Intent intent = new Intent(PICOCronoSummaryAdapter.this.activity, VMCCronoSetActivity.class);
                    VMCCronoSetActivity.index = item.getIndexInList();
                    PICOCronoSummaryAdapter.this.activity.startActivity(intent);
                    return;
                }
                Intent intent2 = new Intent(PICOCronoSummaryAdapter.this.activity, PicoCronoSetActivity.class);
                PicoCronoSetActivity.index = item.getIndexInList();
                PICOCronoSummaryAdapter.this.activity.startActivity(intent2);
            }
        });
        return view;
    }
}
