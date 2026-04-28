package it.tecnosystemi.TS.Adapters;

import android.widget.ArrayAdapter;
import it.tecnosystemi.TS.Activity.ControlUnitActivity;
import it.tecnosystemi.TS.Model.ControlUnit;
import it.tecnosystemi.TS.Model.Zona;
import it.tecnosystemi.TS.R;
import java.util.List;

public class ZoneAdapter extends ArrayAdapter<Zona> {
    ControlUnitActivity context;
    ControlUnit cu;
    List<Zona> zone;

    public ZoneAdapter(ControlUnitActivity controlUnitActivity, ControlUnit controlUnit) {
        super(controlUnitActivity, R.layout.riga_zone_cu, controlUnit.getZone());
        this.zone = controlUnit.getZone();
        this.context = controlUnitActivity;
        this.cu = controlUnit;
    }

    public Zona getItem(int i) {
        try {
            return this.zone.get(i);
        } catch (Exception unused) {
            return null;
        }
    }

    public int getPosition(Zona zona) {
        return this.zone.indexOf(zona);
    }

    /* JADX WARNING: Removed duplicated region for block: B:15:0x0085 A[SYNTHETIC, Splitter:B:15:0x0085] */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x009a  */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0108  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x01f6  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x021e  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0254  */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x0277  */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0285  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0295  */
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
            it.tecnosystemi.TS.Model.Zona r2 = r22.getItem((int) r23)
            if (r2 != 0) goto L_0x001d
            return r1
        L_0x001d:
            int r3 = r0.getPosition((it.tecnosystemi.TS.Model.Zona) r2)
            int r4 = it.tecnosystemi.TS.R.id.btnZone
            android.view.View r4 = r1.findViewById(r4)
            android.widget.Button r4 = (android.widget.Button) r4
            int r5 = it.tecnosystemi.TS.R.id.lblZoneName
            android.view.View r5 = r1.findViewById(r5)
            android.widget.TextView r5 = (android.widget.TextView) r5
            int r6 = it.tecnosystemi.TS.R.id.lblZoneTemp1
            android.view.View r6 = r1.findViewById(r6)
            android.widget.TextView r6 = (android.widget.TextView) r6
            int r7 = it.tecnosystemi.TS.R.id.lblZoneTemp2
            android.view.View r7 = r1.findViewById(r7)
            android.widget.TextView r7 = (android.widget.TextView) r7
            int r8 = it.tecnosystemi.TS.R.id.lblZoneError
            android.view.View r8 = r1.findViewById(r8)
            android.widget.TextView r8 = (android.widget.TextView) r8
            int r9 = it.tecnosystemi.TS.R.id.lblZoneCasetta
            android.view.View r9 = r1.findViewById(r9)
            android.widget.TextView r9 = (android.widget.TextView) r9
            int r10 = it.tecnosystemi.TS.R.id.lblZoneCwin
            android.view.View r10 = r1.findViewById(r10)
            android.widget.TextView r10 = (android.widget.TextView) r10
            int r11 = it.tecnosystemi.TS.R.id.lblZonecbadge
            android.view.View r11 = r1.findViewById(r11)
            android.widget.TextView r11 = (android.widget.TextView) r11
            r12 = 8
            r10.setVisibility(r12)
            java.lang.Object r13 = r2.getCWin()
            r14 = -4616189618054758400(0xbff0000000000000, double:-1.0)
            if (r13 == 0) goto L_0x007a
            java.lang.Object r13 = r2.getCWin()     // Catch:{ Exception -> 0x0079 }
            java.lang.Double r13 = (java.lang.Double) r13     // Catch:{ Exception -> 0x0079 }
            double r16 = r13.doubleValue()     // Catch:{ Exception -> 0x0079 }
            goto L_0x007c
        L_0x0079:
        L_0x007a:
            r16 = r14
        L_0x007c:
            r11.setVisibility(r12)
            java.lang.Object r13 = r2.getCBadge()
            if (r13 == 0) goto L_0x0091
            java.lang.Object r13 = r2.getCBadge()     // Catch:{ Exception -> 0x0090 }
            java.lang.Double r13 = (java.lang.Double) r13     // Catch:{ Exception -> 0x0090 }
            double r14 = r13.doubleValue()     // Catch:{ Exception -> 0x0090 }
            goto L_0x0091
        L_0x0090:
        L_0x0091:
            it.tecnosystemi.TS.Model.ControlUnit r13 = r0.cu
            boolean r13 = r13.getIsOff()
            r12 = 0
            if (r13 == 0) goto L_0x0108
            r4.setEnabled(r12)
            it.tecnosystemi.TS.Activity.ControlUnitActivity r13 = r0.context
            android.content.res.Resources r13 = r13.getResources()
            int r14 = it.tecnosystemi.TS.R.color.colordisable
            int r13 = r13.getColor(r14)
            r4.setTextColor(r13)
            it.tecnosystemi.TS.Activity.ControlUnitActivity r13 = r0.context
            android.content.res.Resources r13 = r13.getResources()
            int r14 = it.tecnosystemi.TS.R.color.colordisable
            int r13 = r13.getColor(r14)
            r5.setTextColor(r13)
            it.tecnosystemi.TS.Activity.ControlUnitActivity r13 = r0.context
            android.content.res.Resources r13 = r13.getResources()
            int r14 = it.tecnosystemi.TS.R.color.colordisable
            int r13 = r13.getColor(r14)
            r6.setTextColor(r13)
            it.tecnosystemi.TS.Activity.ControlUnitActivity r13 = r0.context
            android.content.res.Resources r13 = r13.getResources()
            int r14 = it.tecnosystemi.TS.R.color.colordisable
            int r13 = r13.getColor(r14)
            r7.setTextColor(r13)
            it.tecnosystemi.TS.Activity.ControlUnitActivity r13 = r0.context
            android.content.res.Resources r13 = r13.getResources()
            int r14 = it.tecnosystemi.TS.R.color.colordisable
            int r13 = r13.getColor(r14)
            r9.setTextColor(r13)
            it.tecnosystemi.TS.Activity.ControlUnitActivity r13 = r0.context
            android.content.res.Resources r13 = r13.getResources()
            int r14 = it.tecnosystemi.TS.R.color.colordisable
            int r13 = r13.getColor(r14)
            r11.setTextColor(r13)
            it.tecnosystemi.TS.Activity.ControlUnitActivity r13 = r0.context
            android.content.res.Resources r13 = r13.getResources()
            int r14 = it.tecnosystemi.TS.R.color.colordisable
            int r13 = r13.getColor(r14)
            r10.setTextColor(r13)
            goto L_0x01d1
        L_0x0108:
            r13 = 1
            r4.setEnabled(r13)
            boolean r13 = r2.isOff()
            if (r13 == 0) goto L_0x017c
            it.tecnosystemi.TS.Activity.ControlUnitActivity r13 = r0.context
            android.content.res.Resources r13 = r13.getResources()
            int r14 = it.tecnosystemi.TS.R.color.textdisableselector
            android.content.res.ColorStateList r13 = r13.getColorStateList(r14)
            r4.setTextColor(r13)
            it.tecnosystemi.TS.Activity.ControlUnitActivity r13 = r0.context
            android.content.res.Resources r13 = r13.getResources()
            int r14 = it.tecnosystemi.TS.R.color.colordisable
            int r13 = r13.getColor(r14)
            r5.setTextColor(r13)
            it.tecnosystemi.TS.Activity.ControlUnitActivity r13 = r0.context
            android.content.res.Resources r13 = r13.getResources()
            int r14 = it.tecnosystemi.TS.R.color.colordisable
            int r13 = r13.getColor(r14)
            r6.setTextColor(r13)
            it.tecnosystemi.TS.Activity.ControlUnitActivity r13 = r0.context
            android.content.res.Resources r13 = r13.getResources()
            int r14 = it.tecnosystemi.TS.R.color.colordisable
            int r13 = r13.getColor(r14)
            r7.setTextColor(r13)
            it.tecnosystemi.TS.Activity.ControlUnitActivity r13 = r0.context
            android.content.res.Resources r13 = r13.getResources()
            int r14 = it.tecnosystemi.TS.R.color.colordisable
            int r13 = r13.getColor(r14)
            r9.setTextColor(r13)
            it.tecnosystemi.TS.Activity.ControlUnitActivity r13 = r0.context
            android.content.res.Resources r13 = r13.getResources()
            int r14 = it.tecnosystemi.TS.R.color.colordisable
            int r13 = r13.getColor(r14)
            r11.setTextColor(r13)
            it.tecnosystemi.TS.Activity.ControlUnitActivity r13 = r0.context
            android.content.res.Resources r13 = r13.getResources()
            int r14 = it.tecnosystemi.TS.R.color.colordisable
            int r13 = r13.getColor(r14)
            r10.setTextColor(r13)
            goto L_0x01d1
        L_0x017c:
            it.tecnosystemi.TS.Activity.ControlUnitActivity r13 = r0.context
            android.content.res.Resources r13 = r13.getResources()
            int r12 = it.tecnosystemi.TS.R.color.textprimaryselector
            android.content.res.ColorStateList r12 = r13.getColorStateList(r12)
            r4.setTextColor(r12)
            r12 = -1
            r5.setTextColor(r12)
            r6.setTextColor(r12)
            r7.setTextColor(r12)
            r9.setTextColor(r12)
            r18 = 0
            int r13 = (r16 > r18 ? 1 : (r16 == r18 ? 0 : -1))
            if (r13 != 0) goto L_0x01a1
            r10.setTextColor(r12)
        L_0x01a1:
            r20 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            int r13 = (r16 > r20 ? 1 : (r16 == r20 ? 0 : -1))
            if (r13 != 0) goto L_0x01b6
            it.tecnosystemi.TS.Activity.ControlUnitActivity r13 = r0.context
            android.content.res.Resources r13 = r13.getResources()
            int r12 = it.tecnosystemi.TS.R.color.colorAccent
            int r12 = r13.getColor(r12)
            r10.setTextColor(r12)
        L_0x01b6:
            int r12 = (r14 > r18 ? 1 : (r14 == r18 ? 0 : -1))
            if (r12 != 0) goto L_0x01be
            r12 = -1
            r11.setTextColor(r12)
        L_0x01be:
            int r12 = (r14 > r20 ? 1 : (r14 == r20 ? 0 : -1))
            if (r12 != 0) goto L_0x01d1
            it.tecnosystemi.TS.Activity.ControlUnitActivity r12 = r0.context
            android.content.res.Resources r12 = r12.getResources()
            int r13 = it.tecnosystemi.TS.R.color.colorAccent
            int r12 = r12.getColor(r13)
            r11.setTextColor(r12)
        L_0x01d1:
            java.lang.String r12 = r2.getName()
            r5.setText(r12)
            java.lang.String r12 = r2.getSetTemp()
            double r12 = java.lang.Double.parseDouble(r12)
            java.lang.String r14 = r2.getTemp()
            double r14 = java.lang.Double.parseDouble(r14)
            r25 = r1
            it.tecnosystemi.TS.Model.ControlUnit r1 = r0.cu
            int r1 = r1.getUnitOfMesure()
            r16 = r3
            java.lang.String r3 = "°"
            if (r1 != 0) goto L_0x021e
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            int r12 = (int) r12
            java.lang.String r12 = java.lang.String.valueOf(r12)
            r1.append(r12)
            r1.append(r3)
            java.lang.String r1 = r1.toString()
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = java.lang.String.valueOf(r14)
            r12.append(r13)
            r12.append(r3)
            java.lang.String r3 = r12.toString()
            goto L_0x024e
        L_0x021e:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            double r12 = it.tecnosystemi.TS.Utils.Functions.fromCtoF((double) r12)
            int r12 = (int) r12
            java.lang.String r12 = java.lang.String.valueOf(r12)
            r1.append(r12)
            r1.append(r3)
            java.lang.String r1 = r1.toString()
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            double r13 = it.tecnosystemi.TS.Utils.Functions.fromCtoF((double) r14)
            int r13 = (int) r13
            java.lang.String r13 = java.lang.String.valueOf(r13)
            r12.append(r13)
            r12.append(r3)
            java.lang.String r3 = r12.toString()
        L_0x024e:
            int r12 = r2.getNumError()
            if (r12 <= 0) goto L_0x0277
            it.tecnosystemi.TS.Activity.ControlUnitActivity r12 = r0.context
            android.content.res.Resources r12 = r12.getResources()
            int r13 = it.tecnosystemi.TS.R.color.colorerror
            int r12 = r12.getColor(r13)
            r8.setTextColor(r12)
            it.tecnosystemi.TS.Activity.ControlUnitActivity r12 = r0.context
            android.content.res.Resources r12 = r12.getResources()
            int r13 = it.tecnosystemi.TS.R.color.colorerror
            int r12 = r12.getColor(r13)
            r5.setTextColor(r12)
            r12 = 0
            r8.setVisibility(r12)
            goto L_0x027c
        L_0x0277:
            r12 = 8
            r8.setVisibility(r12)
        L_0x027c:
            r6.setText(r3)
            boolean r2 = r2.isCoff()
            if (r2 == 0) goto L_0x0295
            it.tecnosystemi.TS.Activity.ControlUnitActivity r1 = r0.context
            android.content.res.Resources r1 = r1.getResources()
            int r2 = it.tecnosystemi.TS.R.string.gen_empty
            java.lang.String r1 = r1.getString(r2)
            r7.setText(r1)
            goto L_0x0298
        L_0x0295:
            r7.setText(r1)
        L_0x0298:
            it.tecnosystemi.TS.Activity.ControlUnitActivity r1 = r0.context
            android.content.res.AssetManager r1 = r1.getAssets()
            java.lang.String r2 = "fonts/fontawesome.ttf"
            android.graphics.Typeface r1 = android.graphics.Typeface.createFromAsset(r1, r2)
            it.tecnosystemi.TS.Activity.ControlUnitActivity r2 = r0.context
            android.content.res.AssetManager r2 = r2.getAssets()
            java.lang.String r3 = "fonts/AvenirNextCondensed_Regular.ttf"
            android.graphics.Typeface r2 = android.graphics.Typeface.createFromAsset(r2, r3)
            it.tecnosystemi.TS.Activity.ControlUnitActivity r3 = r0.context
            android.content.res.AssetManager r3 = r3.getAssets()
            java.lang.String r12 = "fonts/icomoon.ttf"
            android.graphics.Typeface r3 = android.graphics.Typeface.createFromAsset(r3, r12)
            r4.setTypeface(r3)
            r8.setTypeface(r1)
            r9.setTypeface(r3)
            r5.setTypeface(r2)
            r6.setTypeface(r2)
            r7.setTypeface(r2)
            r10.setTypeface(r3)
            r11.setTypeface(r3)
            it.tecnosystemi.TS.Adapters.ZoneAdapter$1 r1 = new it.tecnosystemi.TS.Adapters.ZoneAdapter$1
            r2 = r16
            r1.<init>(r2)
            r4.setOnClickListener(r1)
            it.tecnosystemi.TS.Adapters.ZoneAdapter$2 r1 = new it.tecnosystemi.TS.Adapters.ZoneAdapter$2
            r1.<init>(r2)
            r2 = r25
            r2.setOnClickListener(r1)
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Adapters.ZoneAdapter.getView(int, android.view.View, android.view.ViewGroup):android.view.View");
    }

    public void updateData(ControlUnit controlUnit) {
        this.zone = controlUnit.getZone();
    }
}
