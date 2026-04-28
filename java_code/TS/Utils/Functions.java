package it.tecnosystemi.TS.Utils;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.view.ViewCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Model.ControlUnit;
import it.tecnosystemi.TS.Model.Device;
import it.tecnosystemi.TS.Model.ModBusRecipe;
import it.tecnosystemi.TS.Model.Pico;
import it.tecnosystemi.TS.Model.Plant;
import it.tecnosystemi.TS.Model.VMC;
import it.tecnosystemi.TS.R;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;

public class Functions {
    public static Context context;

    public static double fromCtoF(double d) {
        return (d * 1.8d) + 32.0d;
    }

    public static String getTrasnslation(String str, String str2) {
        try {
            return context.getResources().getString(context.getResources().getIdentifier(str, "string", context.getPackageName()));
        } catch (Exception e) {
            Log.d("ERR", e.toString());
            return str2 == null ? str : str2;
        }
    }

    public static String getTrasnslation(int i) {
        return context.getResources().getString(i);
    }

    public static List<ModBusRecipe.Param> getParamsFromKey(String str, long j) {
        String str2 = null;
        for (ModBusRecipe next : Constants.MODBUSRECEPIES) {
            if (next.getPRAN_Id() == j) {
                for (int i = 0; i < next.getFws().size(); i++) {
                    if (str2 == null || str.equals(next.getFws().get(i).getPRFW_RecipeKey())) {
                        str2 = next.getFws().get(i).getPRFW_Version();
                    }
                }
            }
        }
        return getParams(str2, j);
    }

    public static List<ModBusRecipe.Param> getParams(String str, long j) {
        ArrayList arrayList = new ArrayList();
        Iterator<ModBusRecipe> it2 = Constants.MODBUSRECEPIES.iterator();
        while (true) {
            if (!it2.hasNext()) {
                break;
            }
            ModBusRecipe next = it2.next();
            if (next.getPRAN_Id() == j) {
                List<ModBusRecipe.Param> params = next.getParams();
                Collections.sort(params, new Comparator<ModBusRecipe.Param>() {
                    public int compare(ModBusRecipe.Param param, ModBusRecipe.Param param2) {
                        return Long.compare(param.getPRPA_IdParam(), param2.getPRPA_IdParam());
                    }
                });
                for (int i = 0; i < params.size(); i++) {
                    if (ModBusRecipe.Param.IsValidForFW(params.get(i), str)) {
                        params.get(i).setIdScheda(arrayList.size());
                        arrayList.add(params.get(i));
                    }
                }
            }
        }
        return arrayList;
    }

    public static int compareVersions(String str, String str2) {
        if (str == null && str2 == null) {
            return 0;
        }
        if (str == null) {
            return -1;
        }
        if (str2 == null) {
            return 1;
        }
        String[] split = str.split("\\.");
        String[] split2 = str2.split("\\.");
        int max = Math.max(split.length, split2.length);
        int i = 0;
        while (i < max) {
            int parseInt = i < split.length ? Integer.parseInt(split[i]) : 0;
            int parseInt2 = i < split2.length ? Integer.parseInt(split2[i]) : 0;
            if (parseInt < parseInt2) {
                return -1;
            }
            if (parseInt > parseInt2) {
                return 1;
            }
            i++;
        }
        return 0;
    }

    public static boolean isPicoWiFi(String str) {
        return str != null && str.startsWith(Constants.WIFI_NAME_OFFLINE_PICO) && !str.equals(Constants.WIFI_NAME_PICO_CONFIG);
    }

    public static String getStringResourceByName(String str, Activity activity) {
        try {
            return activity.getString(activity.getResources().getIdentifier(str, "string", activity.getPackageName()));
        } catch (Exception unused) {
            return "";
        }
    }

    public static int[] getIntArray(JSONArray jSONArray) {
        try {
            int[] iArr = new int[jSONArray.length()];
            for (int i = 0; i < jSONArray.length(); i++) {
                iArr[i] = jSONArray.getInt(i);
            }
            return iArr;
        } catch (Exception unused) {
            return null;
        }
    }

    public static InetAddress getIPAddress(boolean z) {
        try {
            for (T inetAddresses : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                Iterator<T> it2 = Collections.list(inetAddresses.getInetAddresses()).iterator();
                while (true) {
                    if (it2.hasNext()) {
                        InetAddress inetAddress = (InetAddress) it2.next();
                        if (!inetAddress.isLoopbackAddress()) {
                            boolean z2 = inetAddress.getHostAddress().indexOf(58) < 0;
                            if (z) {
                                if (z2) {
                                    return inetAddress;
                                }
                            } else if (!z2) {
                                return inetAddress;
                            }
                        }
                    }
                }
            }
            return null;
        } catch (Exception unused) {
            return null;
        }
    }

    public static void SyncInpianti(List<Plant> list, BaseActivity baseActivity) {
        SavePreferences savePreferences;
        SavePreferences savePreferences2;
        VMC vMCFromPref;
        List<Plant> list2 = list;
        BaseActivity baseActivity2 = baseActivity;
        SavePreferences savePreferences3 = new SavePreferences(baseActivity2, baseActivity2.getString(R.string.PrefsName));
        String string = savePreferences3.getString(Constants.PREF_DEVS_TS);
        HashMap hashMap = new HashMap();
        if (string != null && !string.isEmpty()) {
            try {
                hashMap = (HashMap) new Gson().fromJson(string, new TypeToken<HashMap<Long, List<Device>>>() {
                }.getType());
            } catch (Exception unused) {
            }
        }
        if (list2 != null && list.size() > 0) {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            ArrayList arrayList4 = new ArrayList();
            ArrayList arrayList5 = new ArrayList();
            int i = 0;
            while (i < list.size()) {
                arrayList.add(Long.valueOf(list2.get(i).getLVPL_Id()));
                if (!hashMap.containsKey(Long.valueOf(list2.get(i).getLVPL_Id())) || hashMap.get(Long.valueOf(list2.get(i).getLVPL_Id())) == null) {
                    savePreferences = savePreferences3;
                    hashMap.put(Long.valueOf(list2.get(i).getLVPL_Id()), list2.get(i).getListDevices());
                } else {
                    int i2 = 0;
                    while (i2 < list2.get(i).getListDevices().size()) {
                        int indexOf = ((List) hashMap.get(Long.valueOf(list2.get(i).getLVPL_Id()))).indexOf(list2.get(i).getListDevices().get(i2));
                        if (indexOf < 0) {
                            ((List) hashMap.get(Long.valueOf(list2.get(i).getLVPL_Id()))).add(list2.get(i).getListDevices().get(i2));
                            savePreferences2 = savePreferences3;
                        } else {
                            savePreferences2 = savePreferences3;
                            if (((Device) ((List) hashMap.get(Long.valueOf(list2.get(i).getLVPL_Id()))).get(indexOf)).getLVDV_Type() == Constants.DEVICE_TYPE_PROAIR) {
                                ControlUnit cuFromPref = ControlUnit.getCuFromPref(((Device) ((List) hashMap.get(Long.valueOf(list2.get(i).getLVPL_Id()))).get(indexOf)).getSerial(), baseActivity2);
                                if (cuFromPref != null) {
                                    arrayList4.add(cuFromPref.getSerial());
                                    if (cuFromPref.isOffline()) {
                                        if (!(list2.get(i).getListDevices().get(i2).getLastAddTimezone() == null || ((Device) ((List) hashMap.get(Long.valueOf(list2.get(i).getLVPL_Id()))).get(indexOf)).getLastAddTimezone() == null)) {
                                            try {
                                                if (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(((Device) ((List) hashMap.get(Long.valueOf(list2.get(i).getLVPL_Id()))).get(indexOf)).getLastAddTimezone()).getTime() > new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(list2.get(i).getListDevices().get(i2).getLastAddTimezone()).getTime()) {
                                                }
                                            } catch (Exception unused2) {
                                            }
                                        }
                                    }
                                }
                            } else if (((Device) ((List) hashMap.get(Long.valueOf(list2.get(i).getLVPL_Id()))).get(indexOf)).getLVDV_Type() == Constants.DEVICE_TYPE_PICO) {
                                Pico pICOFromPref = Pico.getPICOFromPref(((Device) ((List) hashMap.get(Long.valueOf(list2.get(i).getLVPL_Id()))).get(indexOf)).getSerial(), baseActivity2);
                                if (pICOFromPref != null) {
                                    arrayList3.add(pICOFromPref.getSerial());
                                    if (pICOFromPref.getOffline().booleanValue()) {
                                        if (list2.get(i).getListDevices().get(i2).getLastAddTimezone() != null) {
                                            if (pICOFromPref.getLastAddTimezone() != null) {
                                                try {
                                                    if (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(pICOFromPref.getLastAddTimezone()).getTime() > new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(list2.get(i).getListDevices().get(i2).getLastAddTimezone()).getTime()) {
                                                    }
                                                } catch (Exception unused3) {
                                                }
                                            }
                                            Pico.deletePICOfromPref(pICOFromPref.getSerial(), baseActivity2);
                                        }
                                    }
                                }
                            } else if (((Device) ((List) hashMap.get(Long.valueOf(list2.get(i).getLVPL_Id()))).get(indexOf)).getLVDV_Type() == Constants.DEVICE_TYPE_VMC && (vMCFromPref = VMC.getVMCFromPref(((Device) ((List) hashMap.get(Long.valueOf(list2.get(i).getLVPL_Id()))).get(indexOf)).getSerial(), baseActivity2)) != null) {
                                arrayList5.add(vMCFromPref.getSerial());
                                if (vMCFromPref.getOffline().booleanValue()) {
                                    if (list2.get(i).getListDevices().get(i2).getLastAddTimezone() != null) {
                                        if (vMCFromPref.getLastAddTimezone() != null) {
                                            try {
                                                if (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(vMCFromPref.getLastAddTimezone()).getTime() > new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(list2.get(i).getListDevices().get(i2).getLastAddTimezone()).getTime()) {
                                                }
                                            } catch (Exception unused4) {
                                            }
                                        }
                                        VMC.deleteVMCfromPref(vMCFromPref.getSerial(), baseActivity2);
                                    }
                                }
                            }
                            ((List) hashMap.get(Long.valueOf(list2.get(i).getLVPL_Id()))).set(indexOf, list2.get(i).getListDevices().get(i2));
                        }
                        i2++;
                        savePreferences3 = savePreferences2;
                    }
                    savePreferences = savePreferences3;
                }
                i++;
                savePreferences3 = savePreferences;
            }
            SavePreferences savePreferences4 = savePreferences3;
            for (Long l : hashMap.keySet()) {
                l.longValue();
                int indexOf2 = arrayList.indexOf(l);
                if (indexOf2 >= 0) {
                    if (hashMap.get(l) != null) {
                        ArrayList arrayList6 = new ArrayList();
                        for (int i3 = 0; i3 < ((List) hashMap.get(l)).size(); i3++) {
                            if (((Device) ((List) hashMap.get(l)).get(i3)).getLVDV_Type() == Constants.DEVICE_TYPE_PICO) {
                                Pico pICOFromPref2 = Pico.getPICOFromPref(((Device) ((List) hashMap.get(l)).get(i3)).getSerial(), baseActivity2);
                                if (pICOFromPref2 != null && pICOFromPref2.getOffline() != null && !pICOFromPref2.getOffline().booleanValue() && !arrayList3.contains(pICOFromPref2.getSerial())) {
                                    arrayList6.add(Integer.valueOf(i3));
                                    Pico.deletePICOfromPref(pICOFromPref2.getSerial(), baseActivity2);
                                }
                            } else if (((Device) ((List) hashMap.get(l)).get(i3)).getLVDV_Type() == Constants.DEVICE_TYPE_VMC) {
                                VMC vMCFromPref2 = VMC.getVMCFromPref(((Device) ((List) hashMap.get(l)).get(i3)).getSerial(), baseActivity2);
                                if (vMCFromPref2 != null && vMCFromPref2.getOffline() != null && !vMCFromPref2.getOffline().booleanValue() && !arrayList5.contains(vMCFromPref2.getSerial())) {
                                    arrayList6.add(Integer.valueOf(i3));
                                    VMC.deleteVMCfromPref(vMCFromPref2.getSerial(), baseActivity2);
                                }
                            } else {
                                ControlUnit cuFromPref2 = ControlUnit.getCuFromPref(((Device) ((List) hashMap.get(l)).get(i3)).getSerial(), baseActivity2);
                                if (cuFromPref2 != null && !cuFromPref2.isOffline() && !arrayList4.contains(cuFromPref2.getSerial())) {
                                    arrayList6.add(Integer.valueOf(i3));
                                    ControlUnit.deleteCufromPref(cuFromPref2.getSerial(), baseActivity2);
                                }
                            }
                        }
                        List list3 = (List) hashMap.get(l);
                        for (int size = arrayList6.size() - 1; size >= 0; size--) {
                            list3.remove(arrayList6.get(size));
                        }
                        hashMap.put(l, list3);
                        Collections.sort((List) hashMap.get(l), new CustomComparatorDev());
                    }
                    list2.get(indexOf2).setListDevices((List) hashMap.get(l));
                } else {
                    arrayList2.add(l);
                }
            }
            for (int i4 = 0; i4 < arrayList2.size(); i4++) {
                deleteFromPref((List) hashMap.get(arrayList2.get(i4)), baseActivity2);
                hashMap.remove(arrayList2.get(i4));
            }
            savePreferences4.save(Constants.PREF_DEVS_TS, new Gson().toJson((Object) hashMap));
        }
        Collections.sort(list2, new CustomComparatorPlant());
        Constants.listaImpianti = list2;
    }

    private static void deleteFromPref(List<Device> list, BaseActivity baseActivity) {
        if (list != null) {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).getLVDV_Type() == Constants.DEVICE_TYPE_PROAIR) {
                    arrayList.add(list.get(i).getSerial());
                } else if (list.get(i).getLVDV_Type() == Constants.DEVICE_TYPE_PICO) {
                    arrayList2.add(list.get(i).getSerial());
                }
            }
            ControlUnit.deleteListCufromPref(arrayList, baseActivity);
            Pico.deleteListPICOfromPref(arrayList2, baseActivity);
        }
    }

    private static class CustomComparatorPlant implements Comparator<Plant> {
        private CustomComparatorPlant() {
        }

        public int compare(Plant plant, Plant plant2) {
            return plant.getLVPL_Name().compareTo(plant2.getLVPL_Name());
        }
    }

    private static class CustomComparatorDev implements Comparator<Device> {
        private CustomComparatorDev() {
        }

        public int compare(Device device, Device device2) {
            if (device.getName() == null) {
                if (device2.getName() == null) {
                    return 0;
                }
                return -1;
            } else if (device2.getName() != null) {
                return device.getName().compareTo(device2.getName());
            } else {
                if (device.getName() == null) {
                    return 0;
                }
                return 1;
            }
        }
    }

    public static boolean IS4x(String str) {
        try {
            return Integer.parseInt(str.split("\\.")[0]) > 3;
        } catch (Exception unused) {
            return false;
        }
    }

    public static int compareVerString(String str, String str2) {
        int i;
        if (str2 == null || str2.length() == 0) {
            return 0;
        }
        try {
            String[] split = str.split("\\.");
            String[] split2 = str2.split("\\.");
            int i2 = 0;
            while (true) {
                if (i2 >= split.length || i2 >= split2.length) {
                    i = 0;
                } else {
                    try {
                        if (Integer.parseInt(split[i2]) < Integer.parseInt(split2[i2])) {
                            i = -1;
                            break;
                        } else if (Integer.parseInt(split[i2]) > Integer.parseInt(split2[i2])) {
                            i = 1;
                            break;
                        } else {
                            i2++;
                        }
                    } catch (Exception unused) {
                        return 0;
                    }
                }
            }
            i = 0;
            return i == 0 ? split.length - split2.length : i;
        } catch (Exception unused2) {
            return 0;
        }
    }

    public static List<View> getAllChildren(View view) {
        if (!(view instanceof ViewGroup)) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(view);
            return arrayList;
        }
        ArrayList arrayList2 = new ArrayList();
        ViewGroup viewGroup = (ViewGroup) view;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            arrayList2.addAll(getAllChildren(viewGroup.getChildAt(i)));
        }
        return arrayList2;
    }

    public static void setFonts(View view, Activity activity) {
        List<View> allChildren = getAllChildren(view);
        for (int i = 0; i < allChildren.size(); i++) {
            try {
                View view2 = allChildren.get(i);
                if (view2 instanceof TextView) {
                    TextView textView = (TextView) view2;
                    String resourceEntryName = activity.getResources().getResourceEntryName(textView.getId());
                    if (!(textView.getId() == R.id.btn_optionmenu || textView.getId() == R.id.btn_indietro)) {
                        Log.d("SETFONT", resourceEntryName);
                        if (resourceEntryName.toLowerCase().contains("bold")) {
                            ((TextView) view.findViewById(textView.getId())).setTypeface(BaseActivity.avenirbold);
                        } else {
                            ((TextView) view.findViewById(textView.getId())).setTypeface(BaseActivity.avenir);
                        }
                    }
                }
            } catch (Exception unused) {
            }
        }
    }

    public static void setFontsWithIcon(View view, Activity activity) {
        List<View> allChildren = getAllChildren(view);
        for (int i = 0; i < allChildren.size(); i++) {
            try {
                View view2 = allChildren.get(i);
                if (view2 instanceof TextView) {
                    TextView textView = (TextView) view2;
                    String resourceEntryName = activity.getResources().getResourceEntryName(textView.getId());
                    if (!(textView.getId() == R.id.btn_optionmenu || textView.getId() == R.id.btn_indietro)) {
                        if (resourceEntryName.toLowerCase().contains(Constants.INTENT_ICON)) {
                            ((TextView) view.findViewById(textView.getId())).setTypeface(BaseActivity.fontawesome);
                        } else if (resourceEntryName.toLowerCase().contains("moon")) {
                            ((TextView) view.findViewById(textView.getId())).setTypeface(BaseActivity.icomoon);
                        } else if (resourceEntryName.toLowerCase().contains("bold")) {
                            ((TextView) view.findViewById(textView.getId())).setTypeface(BaseActivity.avenirbold);
                        } else {
                            ((TextView) view.findViewById(textView.getId())).setTypeface(BaseActivity.avenir);
                        }
                    }
                }
            } catch (Exception unused) {
            }
        }
    }

    public static void makeErrorToast(final Activity activity, final String str) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = new Toast(activity.getApplicationContext());
                Typeface createFromAsset = Typeface.createFromAsset(activity.getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
                View inflate = activity.getLayoutInflater().inflate(R.layout.custom_toast_background, (ViewGroup) activity.findViewById(R.id.custom_toast_container));
                inflate.setBackground(activity.getResources().getDrawable(R.drawable.toast_error));
                TextView textView = (TextView) inflate.findViewById(R.id.lbltoast);
                textView.setText(str);
                textView.setTextColor(-1);
                textView.setTypeface(createFromAsset);
                toast.setGravity(80, 0, 65);
                toast.setDuration(1);
                toast.setView(inflate);
                toast.show();
            }
        });
    }

    public static void makeNormalToast(final Activity activity, final String str) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = new Toast(activity.getApplicationContext());
                Typeface createFromAsset = Typeface.createFromAsset(activity.getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
                View inflate = activity.getLayoutInflater().inflate(R.layout.custom_toast_background, (ViewGroup) activity.findViewById(R.id.custom_toast_container));
                inflate.setBackground(activity.getResources().getDrawable(R.drawable.toast_normal));
                TextView textView = (TextView) inflate.findViewById(R.id.lbltoast);
                textView.setText(str);
                textView.setTextColor(ViewCompat.MEASURED_STATE_MASK);
                textView.setTypeface(createFromAsset);
                toast.setGravity(80, 0, 65);
                toast.setDuration(1);
                toast.setView(inflate);
                toast.show();
            }
        });
    }

    public static String streamToString(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine == null) {
                return sb.toString();
            }
            sb.append(readLine);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0039 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x003a  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String calcNewToken(it.tecnosystemi.TS.Utils.SavePreferences r9, android.content.res.Resources r10) {
        /*
            java.lang.String r10 = ""
            r0 = 0
            r1 = 0
            it.tecnosystemi.TS.Utils.AESCrypt r2 = new it.tecnosystemi.TS.Utils.AESCrypt     // Catch:{ Exception -> 0x002b }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x002b }
            r3.<init>()     // Catch:{ Exception -> 0x002b }
            java.lang.String r4 = it.tecnosystemi.TS.Utils.Constants.android_id     // Catch:{ Exception -> 0x002b }
            r5 = 8
            java.lang.String r4 = r4.substring(r0, r5)     // Catch:{ Exception -> 0x002b }
            r3.append(r4)     // Catch:{ Exception -> 0x002b }
            java.lang.String r4 = it.tecnosystemi.TS.Utils.Constants.SALT     // Catch:{ Exception -> 0x002b }
            r3.append(r4)     // Catch:{ Exception -> 0x002b }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x002b }
            r2.<init>(r3)     // Catch:{ Exception -> 0x002b }
            java.lang.String r3 = it.tecnosystemi.TS.Utils.Constants.token     // Catch:{ Exception -> 0x0029 }
            java.lang.String r3 = r2.decrypt(r3)     // Catch:{ Exception -> 0x0029 }
            goto L_0x0037
        L_0x0029:
            r3 = move-exception
            goto L_0x002d
        L_0x002b:
            r3 = move-exception
            r2 = r1
        L_0x002d:
            java.lang.String r4 = "tecnosistemi"
            java.lang.String r3 = r3.toString()
            android.util.Log.i(r4, r3)
            r3 = r1
        L_0x0037:
            if (r3 != 0) goto L_0x003a
            return r1
        L_0x003a:
            java.lang.String r4 = "_"
            java.lang.String[] r3 = r3.split(r4)
            int r5 = r3.length
            r6 = 2
            if (r5 != r6) goto L_0x0085
            r5 = 1
            r5 = r3[r5]     // Catch:{ Exception -> 0x0085 }
            long r5 = java.lang.Long.parseLong(r5)     // Catch:{ Exception -> 0x0085 }
            r7 = 1
            long r5 = r5 + r7
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0085 }
            r7.<init>()     // Catch:{ Exception -> 0x0085 }
            r0 = r3[r0]     // Catch:{ Exception -> 0x0085 }
            r7.append(r0)     // Catch:{ Exception -> 0x0085 }
            r7.append(r4)     // Catch:{ Exception -> 0x0085 }
            r7.append(r5)     // Catch:{ Exception -> 0x0085 }
            java.lang.String r0 = r7.toString()     // Catch:{ Exception -> 0x0085 }
            java.lang.String r0 = r2.encrypt(r0)     // Catch:{ Exception -> 0x0085 }
            java.lang.String r2 = "\r"
            java.lang.String r0 = r0.replace(r2, r10)     // Catch:{ Exception -> 0x0085 }
            java.lang.String r2 = "\n"
            java.lang.String r10 = r0.replace(r2, r10)     // Catch:{ Exception -> 0x0085 }
            it.tecnosystemi.TS.Utils.Constants.token = r10     // Catch:{ Exception -> 0x0085 }
            java.lang.String r0 = "cont_token"
            java.lang.String r2 = java.lang.String.valueOf(r5)     // Catch:{ Exception -> 0x0085 }
            r9.save((java.lang.String) r0, (java.lang.String) r2)     // Catch:{ Exception -> 0x0085 }
            java.lang.String r0 = "TOKENVALUE"
            java.lang.String r2 = it.tecnosystemi.TS.Utils.Constants.token     // Catch:{ Exception -> 0x0085 }
            r9.save((java.lang.String) r0, (java.lang.String) r2)     // Catch:{ Exception -> 0x0085 }
            return r10
        L_0x0085:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Utils.Functions.calcNewToken(it.tecnosystemi.TS.Utils.SavePreferences, android.content.res.Resources):java.lang.String");
    }

    public static void ShowerrorOnView(Activity activity, EditText editText, String str) {
        Typeface createFromAsset = Typeface.createFromAsset(activity.getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
        spannableStringBuilder.setSpan(new CustomTypefaceSpan("", createFromAsset), 0, spannableStringBuilder.length(), 34);
        editText.setError(spannableStringBuilder);
    }

    public static double fromFtoC(int i) {
        return Constants.FROMFTOC.get(Integer.valueOf(i)).doubleValue();
    }

    public static String fromFtoC(String str) {
        return String.valueOf(fromFtoC(Integer.parseInt(str)));
    }

    public static String fromFtoCInt(String str) {
        return String.valueOf((int) fromFtoC(Integer.parseInt(str)));
    }

    public static String fromCtoF(String str) {
        return String.valueOf((Double.parseDouble(str) * 1.8d) + 32.0d);
    }

    public static boolean[] geterror(int i) {
        boolean[] zArr = new boolean[8];
        for (int i2 = 0; i2 < 8; i2++) {
            if (i % 2 == 0) {
                zArr[i2] = false;
            } else {
                zArr[i2] = true;
            }
            i /= 2;
        }
        return zArr;
    }

    public static class CustomTypefaceSpan extends TypefaceSpan {
        private final Typeface newType;

        public CustomTypefaceSpan(String str, Typeface typeface) {
            super(str);
            this.newType = typeface;
        }

        public void updateDrawState(TextPaint textPaint) {
            applyCustomTypeFace(textPaint, this.newType);
        }

        public void updateMeasureState(TextPaint textPaint) {
            applyCustomTypeFace(textPaint, this.newType);
        }

        private static void applyCustomTypeFace(Paint paint, Typeface typeface) {
            int i;
            Typeface typeface2 = paint.getTypeface();
            if (typeface2 == null) {
                i = 0;
            } else {
                i = typeface2.getStyle();
            }
            int style = i & (typeface.getStyle() ^ -1);
            if ((style & 1) != 0) {
                paint.setFakeBoldText(true);
            }
            if ((style & 2) != 0) {
                paint.setTextSkewX(-0.25f);
            }
            paint.setTypeface(typeface);
        }
    }

    public static boolean getNotificationPermision(Activity activity) {
        if (Build.VERSION.SDK_INT <= 19) {
            SharedPreferences sharedPreferences = activity.getSharedPreferences(Constants.PREF_REGID_NAME, 0);
            if (sharedPreferences == null || sharedPreferences.getInt(Constants.PREF_REGID_SETTING_NAME, 0) != 1) {
                return false;
            }
            return true;
        } else if (Build.VERSION.SDK_INT >= 24) {
            return NotificationManagerCompat.from(activity).areNotificationsEnabled();
        } else {
            AppOpsManager appOpsManager = (AppOpsManager) activity.getSystemService("appops");
            ApplicationInfo applicationInfo = activity.getApplicationInfo();
            String packageName = activity.getApplicationContext().getPackageName();
            int i = applicationInfo.uid;
            try {
                Class<?> cls = Class.forName(AppOpsManager.class.getName());
                Class cls2 = Integer.TYPE;
                Method method = cls.getMethod("checkOpNoThrow", new Class[]{cls2, cls2, String.class});
                Integer num = (Integer) cls.getDeclaredField("OP_POST_NOTIFICATION").get(Integer.class);
                num.intValue();
                if (((Integer) method.invoke(appOpsManager, new Object[]{num, Integer.valueOf(i), packageName})).intValue() == 0) {
                    return true;
                }
                return false;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return false;
            } catch (NoSuchMethodException e2) {
                e2.printStackTrace();
                return false;
            } catch (NoSuchFieldException e3) {
                e3.printStackTrace();
                return false;
            } catch (InvocationTargetException e4) {
                e4.printStackTrace();
                return false;
            } catch (IllegalAccessException e5) {
                e5.printStackTrace();
                return false;
            }
        }
    }

    public static long differencetime(String str, String str2) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        try {
            return simpleDateFormat.parse(str2).getTime() - simpleDateFormat.parse(str).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int getExistingNetworkId(String str, Activity activity) {
        List<WifiConfiguration> configuredNetworks = ((WifiManager) activity.getApplicationContext().getSystemService(Constants.INTENT_WIFI)).getConfiguredNetworks();
        if (configuredNetworks == null) {
            return -1;
        }
        for (WifiConfiguration next : configuredNetworks) {
            if (next.SSID.equals(str)) {
                return next.networkId;
            }
        }
        return -1;
    }

    public static void netBinding(final Context context2) {
        if (Build.VERSION.SDK_INT >= 21) {
            final ConnectivityManager connectivityManager = (ConnectivityManager) context2.getSystemService("connectivity");
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            NetworkRequest.Builder unused = builder.addTransportType(1);
            connectivityManager.requestNetwork(builder.build(), new ConnectivityManager.NetworkCallback() {
                public void onAvailable(Network network) {
                    String str;
                    if (Build.VERSION.SDK_INT < 27) {
                        str = Functions$$ExternalSyntheticApiModelOutline0.m(connectivityManager, network).getExtraInfo();
                    } else if (((ConnectivityManager) context2.getSystemService("connectivity")).getNetworkInfo(1).isConnected()) {
                        WifiInfo connectionInfo = ((WifiManager) context2.getApplicationContext().getSystemService(Constants.INTENT_WIFI)).getConnectionInfo();
                        connectionInfo.getSSID();
                        str = connectionInfo.getSSID();
                    } else {
                        str = "";
                    }
                    if (str.indexOf("POLARIS") > 0) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            Functions$$ExternalSyntheticApiModelOutline0.m(connectivityManager, network);
                        } else {
                            boolean unused = ConnectivityManager.setProcessDefaultNetwork(network);
                        }
                        connectivityManager.unregisterNetworkCallback(this);
                        return;
                    }
                    Functions.netUnbinding(context2);
                }
            });
        }
    }

    public static void netUnbinding(Context context2) {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                Functions$$ExternalSyntheticApiModelOutline0.m((ConnectivityManager) context2.getSystemService("connectivity"), (Network) null);
            } else if (Build.VERSION.SDK_INT >= 21) {
                boolean unused = ConnectivityManager.setProcessDefaultNetwork((Network) null);
            }
        } catch (Exception unused2) {
        }
    }
}
