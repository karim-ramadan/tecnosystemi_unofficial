package it.tecnosystemi.TS.Adapters;

import android.widget.ArrayAdapter;
import it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity;
import it.tecnosystemi.TS.Model.SeiX;
import it.tecnosystemi.TS.R;
import java.util.List;

public class ZoneAdapterSeix extends ArrayAdapter<SeiX.Zona> {
    SeiXMainActivity context;
    SeiX cu;
    List<SeiX.Zona> zone;

    public ZoneAdapterSeix(SeiXMainActivity seiXMainActivity, SeiX seiX) {
        super(seiXMainActivity, R.layout.riga_zone_cu, seiX.getStato().getZ());
        this.zone = seiX.getStato().getZ();
        this.context = seiXMainActivity;
        this.cu = seiX;
    }

    public SeiX.Zona getItem(int i) {
        try {
            return this.zone.get(i);
        } catch (Exception unused) {
            return null;
        }
    }

    public int getPosition(SeiX.Zona zona) {
        return this.zone.indexOf(zona);
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x007d A[SYNTHETIC, Splitter:B:16:0x007d] */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0097  */
    /* JADX WARNING: Removed duplicated region for block: B:25:0x0108  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x01de  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x020f  */
    /* JADX WARNING: Removed duplicated region for block: B:52:0x0266  */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x026a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.View getView(int r23, android.view.View r24, android.view.ViewGroup r25) {
        /*
            r22 = this;
            r0 = r22
            if (r24 != 0) goto L_0x0014
            android.content.Context r1 = r22.getContext()
            android.view.LayoutInflater r1 = android.view.LayoutInflater.from(r1)
            int r2 = it.tecnosystemi.TS.R.layout.riga_zone_cu
            r3 = 0
            android.view.View r1 = r1.inflate(r2, r3)
            goto L_0x0016
        L_0x0014:
            r1 = r24
        L_0x0016:
            it.tecnosystemi.TS.Model.SeiX$Zona r2 = r22.getItem((int) r23)
            if (r2 != 0) goto L_0x001d
            return r1
        L_0x001d:
            r0.getPosition((it.tecnosystemi.TS.Model.SeiX.Zona) r2)
            int r3 = it.tecnosystemi.TS.R.id.btnZone
            android.view.View r3 = r1.findViewById(r3)
            android.widget.Button r3 = (android.widget.Button) r3
            int r4 = it.tecnosystemi.TS.R.id.lblZoneName
            android.view.View r4 = r1.findViewById(r4)
            android.widget.TextView r4 = (android.widget.TextView) r4
            int r5 = it.tecnosystemi.TS.R.id.lblZoneTemp1
            android.view.View r5 = r1.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            int r6 = it.tecnosystemi.TS.R.id.lblZoneTemp2
            android.view.View r6 = r1.findViewById(r6)
            android.widget.TextView r6 = (android.widget.TextView) r6
            int r7 = it.tecnosystemi.TS.R.id.lblZoneError
            android.view.View r7 = r1.findViewById(r7)
            android.widget.TextView r7 = (android.widget.TextView) r7
            int r8 = it.tecnosystemi.TS.R.id.lblZoneCasetta
            android.view.View r8 = r1.findViewById(r8)
            android.widget.TextView r8 = (android.widget.TextView) r8
            int r9 = it.tecnosystemi.TS.R.id.lblZoneCwin
            android.view.View r9 = r1.findViewById(r9)
            android.widget.TextView r9 = (android.widget.TextView) r9
            int r10 = it.tecnosystemi.TS.R.id.lblZonecbadge
            android.view.View r10 = r1.findViewById(r10)
            android.widget.TextView r10 = (android.widget.TextView) r10
            r11 = 8
            r9.setVisibility(r11)
            int r12 = r2.getC_w()
            if (r12 == 0) goto L_0x0072
            int r12 = r2.getC_w()     // Catch:{ Exception -> 0x0071 }
            double r13 = (double) r12
            goto L_0x0074
        L_0x0071:
        L_0x0072:
            r13 = -4616189618054758400(0xbff0000000000000, double:-1.0)
        L_0x0074:
            r10.setVisibility(r11)
            int r12 = r2.getC_b()
            if (r12 == 0) goto L_0x0084
            int r12 = r2.getC_b()     // Catch:{ Exception -> 0x0083 }
            double r11 = (double) r12
            goto L_0x0086
        L_0x0083:
        L_0x0084:
            r11 = -4616189618054758400(0xbff0000000000000, double:-1.0)
        L_0x0086:
            it.tecnosystemi.TS.Model.SeiX r15 = r0.cu
            it.tecnosystemi.TS.Model.SeiX$Stato r15 = r15.getStato()
            int r15 = r15.getIs_off()
            r16 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            r23 = r11
            r11 = 1
            if (r15 != r11) goto L_0x0108
            r12 = 0
            r3.setEnabled(r12)
            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r12 = r0.context
            android.content.res.Resources r12 = r12.getResources()
            int r15 = it.tecnosystemi.TS.R.color.colordisable
            int r12 = r12.getColor(r15)
            r3.setTextColor(r12)
            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r12 = r0.context
            android.content.res.Resources r12 = r12.getResources()
            int r15 = it.tecnosystemi.TS.R.color.colordisable
            int r12 = r12.getColor(r15)
            r4.setTextColor(r12)
            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r12 = r0.context
            android.content.res.Resources r12 = r12.getResources()
            int r15 = it.tecnosystemi.TS.R.color.colordisable
            int r12 = r12.getColor(r15)
            r5.setTextColor(r12)
            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r12 = r0.context
            android.content.res.Resources r12 = r12.getResources()
            int r15 = it.tecnosystemi.TS.R.color.colordisable
            int r12 = r12.getColor(r15)
            r6.setTextColor(r12)
            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r12 = r0.context
            android.content.res.Resources r12 = r12.getResources()
            int r15 = it.tecnosystemi.TS.R.color.colordisable
            int r12 = r12.getColor(r15)
            r8.setTextColor(r12)
            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r12 = r0.context
            android.content.res.Resources r12 = r12.getResources()
            int r15 = it.tecnosystemi.TS.R.color.colordisable
            int r12 = r12.getColor(r15)
            r10.setTextColor(r12)
            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r12 = r0.context
            android.content.res.Resources r12 = r12.getResources()
            int r15 = it.tecnosystemi.TS.R.color.colordisable
            int r12 = r12.getColor(r15)
            r9.setTextColor(r12)
        L_0x0104:
            r20 = r23
            goto L_0x01d1
        L_0x0108:
            r3.setEnabled(r11)
            int r12 = r2.getIs_off()
            if (r12 != r11) goto L_0x017b
            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r12 = r0.context
            android.content.res.Resources r12 = r12.getResources()
            int r15 = it.tecnosystemi.TS.R.color.textdisableselector
            android.content.res.ColorStateList r12 = r12.getColorStateList(r15)
            r3.setTextColor(r12)
            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r12 = r0.context
            android.content.res.Resources r12 = r12.getResources()
            int r15 = it.tecnosystemi.TS.R.color.colordisable
            int r12 = r12.getColor(r15)
            r4.setTextColor(r12)
            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r12 = r0.context
            android.content.res.Resources r12 = r12.getResources()
            int r15 = it.tecnosystemi.TS.R.color.colordisable
            int r12 = r12.getColor(r15)
            r5.setTextColor(r12)
            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r12 = r0.context
            android.content.res.Resources r12 = r12.getResources()
            int r15 = it.tecnosystemi.TS.R.color.colordisable
            int r12 = r12.getColor(r15)
            r6.setTextColor(r12)
            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r12 = r0.context
            android.content.res.Resources r12 = r12.getResources()
            int r15 = it.tecnosystemi.TS.R.color.colordisable
            int r12 = r12.getColor(r15)
            r8.setTextColor(r12)
            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r12 = r0.context
            android.content.res.Resources r12 = r12.getResources()
            int r15 = it.tecnosystemi.TS.R.color.colordisable
            int r12 = r12.getColor(r15)
            r10.setTextColor(r12)
            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r12 = r0.context
            android.content.res.Resources r12 = r12.getResources()
            int r15 = it.tecnosystemi.TS.R.color.colordisable
            int r12 = r12.getColor(r15)
            r9.setTextColor(r12)
            goto L_0x0104
        L_0x017b:
            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r12 = r0.context
            android.content.res.Resources r12 = r12.getResources()
            int r15 = it.tecnosystemi.TS.R.color.textprimaryselector
            android.content.res.ColorStateList r12 = r12.getColorStateList(r15)
            r3.setTextColor(r12)
            r12 = -1
            r4.setTextColor(r12)
            r5.setTextColor(r12)
            r6.setTextColor(r12)
            r8.setTextColor(r12)
            r18 = 0
            int r15 = (r13 > r18 ? 1 : (r13 == r18 ? 0 : -1))
            if (r15 != 0) goto L_0x01a0
            r9.setTextColor(r12)
        L_0x01a0:
            int r15 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1))
            if (r15 != 0) goto L_0x01b3
            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r15 = r0.context
            android.content.res.Resources r15 = r15.getResources()
            int r11 = it.tecnosystemi.TS.R.color.colorAccent
            int r11 = r15.getColor(r11)
            r9.setTextColor(r11)
        L_0x01b3:
            r20 = r23
            r18 = 0
            int r11 = (r20 > r18 ? 1 : (r20 == r18 ? 0 : -1))
            if (r11 != 0) goto L_0x01be
            r10.setTextColor(r12)
        L_0x01be:
            int r11 = (r20 > r16 ? 1 : (r20 == r16 ? 0 : -1))
            if (r11 != 0) goto L_0x01d1
            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r11 = r0.context
            android.content.res.Resources r11 = r11.getResources()
            int r12 = it.tecnosystemi.TS.R.color.colorAccent
            int r11 = r11.getColor(r12)
            r10.setTextColor(r11)
        L_0x01d1:
            java.lang.String r11 = r2.getName()
            r4.setText(r11)
            int r11 = it.tecnosystemi.TS.Utils.Constants.SEI_X_TEMP_UM
            java.lang.String r12 = "°"
            if (r11 != 0) goto L_0x020f
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r23 = r9
            r24 = r10
            r9 = 0
            int r15 = (int) r9
            java.lang.String r15 = java.lang.String.valueOf(r15)
            r11.append(r15)
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            java.lang.String r9 = java.lang.String.valueOf(r9)
            r15.append(r9)
            r15.append(r12)
            java.lang.String r9 = r15.toString()
            r15 = r3
            r18 = r4
            goto L_0x0248
        L_0x020f:
            r23 = r9
            r24 = r10
            r9 = 0
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r15 = r3
            r18 = r4
            double r3 = it.tecnosystemi.TS.Utils.Functions.fromCtoF((double) r9)
            int r3 = (int) r3
            java.lang.String r3 = java.lang.String.valueOf(r3)
            r11.append(r3)
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            double r9 = it.tecnosystemi.TS.Utils.Functions.fromCtoF((double) r9)
            int r4 = (int) r9
            java.lang.String r4 = java.lang.String.valueOf(r4)
            r3.append(r4)
            r3.append(r12)
            java.lang.String r9 = r3.toString()
        L_0x0248:
            r3 = 8
            r7.setVisibility(r3)
            r5.setText(r9)
            int r3 = (r20 > r16 ? 1 : (r20 == r16 ? 0 : -1))
            if (r3 == 0) goto L_0x026a
            int r3 = (r13 > r16 ? 1 : (r13 == r16 ? 0 : -1))
            if (r3 == 0) goto L_0x026a
            int r3 = r2.getM_crono()
            r4 = 1
            if (r3 != r4) goto L_0x0266
            int r2 = r2.getTw_active()
            if (r2 == r4) goto L_0x0266
            goto L_0x026a
        L_0x0266:
            r6.setText(r11)
            goto L_0x0279
        L_0x026a:
            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r2 = r0.context
            android.content.res.Resources r2 = r2.getResources()
            int r3 = it.tecnosystemi.TS.R.string.gen_empty
            java.lang.String r2 = r2.getString(r3)
            r6.setText(r2)
        L_0x0279:
            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r2 = r0.context
            android.content.res.AssetManager r2 = r2.getAssets()
            java.lang.String r3 = "fonts/fontawesome.ttf"
            android.graphics.Typeface r2 = android.graphics.Typeface.createFromAsset(r2, r3)
            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r3 = r0.context
            android.content.res.AssetManager r3 = r3.getAssets()
            java.lang.String r4 = "fonts/AvenirNextCondensed_Regular.ttf"
            android.graphics.Typeface r3 = android.graphics.Typeface.createFromAsset(r3, r4)
            it.tecnosystemi.TS.Activity.SEIX.SeiXMainActivity r4 = r0.context
            android.content.res.AssetManager r4 = r4.getAssets()
            java.lang.String r9 = "fonts/icomoon.ttf"
            android.graphics.Typeface r4 = android.graphics.Typeface.createFromAsset(r4, r9)
            r9 = r15
            r9.setTypeface(r4)
            r7.setTypeface(r2)
            r8.setTypeface(r4)
            r2 = r18
            r2.setTypeface(r3)
            r5.setTypeface(r3)
            r6.setTypeface(r3)
            r2 = r23
            r2.setTypeface(r4)
            r10 = r24
            r10.setTypeface(r4)
            it.tecnosystemi.TS.Adapters.ZoneAdapterSeix$1 r2 = new it.tecnosystemi.TS.Adapters.ZoneAdapterSeix$1
            r2.<init>()
            r9.setOnClickListener(r2)
            it.tecnosystemi.TS.Adapters.ZoneAdapterSeix$2 r2 = new it.tecnosystemi.TS.Adapters.ZoneAdapterSeix$2
            r2.<init>()
            r1.setOnClickListener(r2)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Adapters.ZoneAdapterSeix.getView(int, android.view.View, android.view.ViewGroup):android.view.View");
    }

    public void updateData(SeiX seiX) {
        this.zone = seiX.getStato().getZ();
    }
}
