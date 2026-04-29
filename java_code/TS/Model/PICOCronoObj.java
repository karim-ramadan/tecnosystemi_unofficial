package it.tecnosystemi.TS.Model;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import it.tecnosystemi.TS.Activity.VMC.VMCCronoSummaryActivity;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Functions;
import java.util.ArrayList;
import java.util.List;

public class PICOCronoObj {
    private Drawable background;
    private List<String> endTimeList = new ArrayList();
    private int indexInList;
    private List<Integer> mods = new ArrayList();
    private String name;
    private List<String> startTimeList = new ArrayList();
    private List<List<Integer>> values;
    private List<Integer> values_toWrite = new ArrayList();

    public PICOCronoObj() {
        for (int i = 0; i < 7; i++) {
            this.startTimeList.add((Object) null);
            this.endTimeList.add((Object) null);
            this.mods.add((Object) null);
            this.values_toWrite.add((Object) null);
        }
    }

    public int getIndexInList() {
        return this.indexInList;
    }

    public void setIndexInList(int i) {
        this.indexInList = i;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public List<List<Integer>> getValues() {
        return this.values;
    }

    public void setValues(List<List<Integer>> list) {
        this.values = list;
    }

    public Drawable getBackground() {
        return this.background;
    }

    public void setBackground(Drawable drawable) {
        this.background = drawable;
    }

    public String getTime(int i) {
        List<List<Integer>> list = this.values;
        if (list == null || list.get(i) == null || ((Integer) this.values.get(i).get(0)).intValue() < 0) {
            return "";
        }
        return (((((String.format("%02d", new Object[]{Integer.valueOf(((Integer) this.values.get(i).get(0)).intValue() / 60)}) + ":") + String.format("%02d", new Object[]{Integer.valueOf(((Integer) this.values.get(i).get(0)).intValue() % 60)})) + "\n") + String.format("%02d", new Object[]{Integer.valueOf(((Integer) this.values.get(i).get(1)).intValue() / 60)})) + ":") + String.format("%02d", new Object[]{Integer.valueOf(((Integer) this.values.get(i).get(1)).intValue() % 60)});
    }

    public Drawable getImage1(Activity activity, int i) {
        if (activity instanceof VMCCronoSummaryActivity) {
            int intValue = ((Integer) this.values.get(i).get(2)).intValue();
            if (intValue == 0 || intValue == 21) {
                return activity.getResources().getDrawable(R.drawable.vmc_standby1);
            }
            if (intValue == 1) {
                return activity.getResources().getDrawable(R.drawable.vmc_absmode);
            }
            if (intValue == 2) {
                return activity.getResources().getDrawable(R.drawable.vmc_minimum_speed);
            }
            if (intValue == 23) {
                return activity.getResources().getDrawable(R.drawable.vmc_medium_speed);
            }
            if (intValue == 3) {
                return activity.getResources().getDrawable(R.drawable.vmc_maximum_speed);
            }
            if (intValue == 13) {
                return activity.getResources().getDrawable(R.drawable.vmc_boostmode);
            }
            if (intValue == 24) {
                return activity.getResources().getDrawable(R.drawable.vmc_auto);
            }
            return null;
        }
        List<List<Integer>> list = this.values;
        if (list == null || list.get(i) == null || ((Integer) this.values.get(i).get(0)).intValue() < 0) {
            return null;
        }
        int intValue2 = ((Integer) this.values.get(i).get(2)).intValue();
        if (intValue2 == 1) {
            return activity.getResources().getDrawable(R.drawable.recupero);
        }
        if (intValue2 == 2) {
            return activity.getResources().getDrawable(R.drawable.estrazione);
        }
        if (intValue2 == 3) {
            return activity.getResources().getDrawable(R.drawable.immissione);
        }
        if (intValue2 == 4) {
            return activity.getResources().getDrawable(R.drawable.auto1);
        }
        if (intValue2 == 5) {
            return activity.getResources().getDrawable(R.drawable.auto2);
        }
        if (intValue2 == 6) {
            return activity.getResources().getDrawable(R.drawable.auto3);
        }
        if (intValue2 == 7) {
            return activity.getResources().getDrawable(R.drawable.auto4);
        }
        if (intValue2 == 8) {
            return activity.getResources().getDrawable(R.drawable.auto5);
        }
        if (intValue2 == 9) {
            return activity.getResources().getDrawable(R.drawable.auto6);
        }
        if (intValue2 == 10) {
            return activity.getResources().getDrawable(R.drawable.auto7);
        }
        if (intValue2 == 11) {
            return activity.getResources().getDrawable(R.drawable.auto8);
        }
        if (intValue2 == 12) {
            return activity.getResources().getDrawable(R.drawable.ricambio_naturale);
        }
        return null;
    }

    public Drawable getImage2(Activity activity, int i) {
        List<List<Integer>> list;
        if (!(activity instanceof VMCCronoSummaryActivity) && (list = this.values) != null && list.get(i) != null && ((Integer) this.values.get(i).get(0)).intValue() >= 0) {
            int intValue = ((Integer) this.values.get(i).get(2)).intValue();
            if (intValue == 1 || intValue == 2 || intValue == 3 || intValue == 6 || intValue == 7) {
                if (((Integer) this.values.get(i).get(3)).intValue() == 1) {
                    return activity.getResources().getDrawable(R.drawable.vel1);
                }
                if (((Integer) this.values.get(i).get(3)).intValue() == 2) {
                    return activity.getResources().getDrawable(R.drawable.vel2);
                }
                if (((Integer) this.values.get(i).get(3)).intValue() == 3) {
                    return activity.getResources().getDrawable(R.drawable.vel3);
                }
                if (((Integer) this.values.get(i).get(3)).intValue() == 4) {
                    return activity.getResources().getDrawable(R.drawable.night);
                }
            }
            if (intValue == 4 || intValue == 5 || intValue == 10 || intValue == 11) {
                if (((Integer) this.values.get(i).get(4)).intValue() == 1) {
                    return activity.getResources().getDrawable(R.drawable.hum_40);
                }
                if (((Integer) this.values.get(i).get(4)).intValue() == 2) {
                    return activity.getResources().getDrawable(R.drawable.hum_50);
                }
                if (((Integer) this.values.get(i).get(4)).intValue() == 3) {
                    return activity.getResources().getDrawable(R.drawable.hum_60);
                }
            }
        }
        return null;
    }

    public void setTimeLists() {
        for (int i = 0; i < this.values.size(); i++) {
            if (this.values.get(i) != null && ((Integer) this.values.get(i).get(0)).intValue() >= 0) {
                this.startTimeList.set(i, (String.format("%02d", new Object[]{Integer.valueOf(((Integer) this.values.get(i).get(0)).intValue() / 60)}) + ":") + String.format("%02d", new Object[]{Integer.valueOf(((Integer) this.values.get(i).get(0)).intValue() % 60)}));
                this.endTimeList.set(i, (String.format("%02d", new Object[]{Integer.valueOf(((Integer) this.values.get(i).get(1)).intValue() / 60)}) + ":") + String.format("%02d", new Object[]{Integer.valueOf(((Integer) this.values.get(i).get(1)).intValue() % 60)}));
                Integer num = (Integer) this.values.get(i).get(2);
                int intValue = num.intValue();
                this.mods.set(i, num);
                if (intValue == 1 || intValue == 2 || intValue == 3 || intValue == 6 || intValue == 7) {
                    this.values_toWrite.set(i, (Integer) this.values.get(i).get(3));
                } else if (intValue == 4 || intValue == 5 || intValue == 10 || intValue == 11) {
                    this.values_toWrite.set(i, (Integer) this.values.get(i).get(4));
                }
            }
        }
    }

    public void setTimeListsVMC() {
        for (int i = 0; i < this.values.size(); i++) {
            if (this.values.get(i) != null && ((Integer) this.values.get(i).get(0)).intValue() >= 0) {
                this.startTimeList.set(i, (String.format("%02d", new Object[]{Integer.valueOf(((Integer) this.values.get(i).get(0)).intValue() / 60)}) + ":") + String.format("%02d", new Object[]{Integer.valueOf(((Integer) this.values.get(i).get(0)).intValue() % 60)}));
                this.endTimeList.set(i, (String.format("%02d", new Object[]{Integer.valueOf(((Integer) this.values.get(i).get(1)).intValue() / 60)}) + ":") + String.format("%02d", new Object[]{Integer.valueOf(((Integer) this.values.get(i).get(1)).intValue() % 60)}));
                Integer num = (Integer) this.values.get(i).get(2);
                num.intValue();
                this.mods.set(i, num);
            }
        }
    }

    public boolean isok() {
        int i = 0;
        while (i < 4) {
            try {
                if (this.startTimeList.get(i) == null) {
                    return true;
                }
                String str = this.endTimeList.get(i);
                if (Functions.differencetime(this.startTimeList.get(i), str) <= 0) {
                    return false;
                }
                if (i != 3) {
                    int i2 = i + 1;
                    if (this.startTimeList.get(i2) != null && Functions.differencetime(str, this.startTimeList.get(i2)) < 0) {
                        return false;
                    }
                }
                i++;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public String getStartTime(int i) {
        return this.startTimeList.get(i);
    }

    public void setStartTime(String str, int i) {
        this.startTimeList.set(i, str);
    }

    public String getEndTime(int i) {
        return this.endTimeList.get(i);
    }

    public void setEndTime(String str, int i) {
        this.endTimeList.set(i, str);
    }

    public Integer getMode(int i) {
        return this.mods.get(i);
    }

    public void setMode(Integer num, int i) {
        this.mods.set(i, num);
    }

    public void setModeVMC(Integer num, int i) {
        if (num.intValue() == -1) {
            this.mods.set(i, -1);
        }
        int intValue = num.intValue() - 1;
        Integer valueOf = Integer.valueOf(intValue);
        valueOf.getClass();
        if (intValue == 0) {
            this.mods.set(i, 0);
        }
        valueOf.getClass();
        if (intValue == 1) {
            this.mods.set(i, 1);
        }
        valueOf.getClass();
        if (intValue == 2) {
            this.mods.set(i, 2);
        }
        valueOf.getClass();
        if (intValue == 3) {
            this.mods.set(i, 23);
        }
        valueOf.getClass();
        if (intValue == 4) {
            this.mods.set(i, 3);
        }
        valueOf.getClass();
        if (intValue == 5) {
            this.mods.set(i, 13);
        }
        valueOf.getClass();
        if (intValue == 6) {
            this.mods.set(i, 24);
        }
    }

    public Integer getValueToWrite(int i) {
        return this.values_toWrite.get(i);
    }

    public void setValueToWrite(Integer num, int i) {
        this.values_toWrite.set(i, num);
    }

    public int getEndtimeAsInt15(int i) {
        try {
            if (this.endTimeList.get(i) == null) {
                return -1;
            }
            String[] split = this.endTimeList.get(i).split(":");
            return ((Integer.parseInt(split[0]) * 60) + Integer.parseInt(split[1])) / 15;
        } catch (Exception unused) {
            return -1;
        }
    }

    public int getStarttimeAsInt15(int i) {
        try {
            if (this.startTimeList.get(i) == null) {
                return -1;
            }
            String[] split = this.startTimeList.get(i).split(":");
            return ((Integer.parseInt(split[0]) * 60) + Integer.parseInt(split[1])) / 15;
        } catch (Exception unused) {
            return -1;
        }
    }

    public int getTimeAsInt(String str) {
        try {
            String[] split = str.split(":");
            return (Integer.parseInt(split[0]) * 60) + Integer.parseInt(split[1]);
        } catch (Exception unused) {
            return -1;
        }
    }

    public List<List<Integer>> getPicoFasce() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < 4; i++) {
            arrayList.add(valueForFasce(i));
        }
        return arrayList;
    }

    public List<List<Integer>> getVMCFasce() {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < 4; i++) {
            arrayList.add(valueForFasceVMC(i));
        }
        return arrayList;
    }

    public Integer getVMCModeIndex(int i) {
        int intValue = this.mods.get(i).intValue();
        if (intValue == 0 || intValue == 21) {
            return 1;
        }
        if (intValue == 1) {
            return 2;
        }
        if (intValue == 2) {
            return 3;
        }
        if (intValue == 23) {
            return 4;
        }
        if (intValue == 3) {
            return 5;
        }
        if (intValue == 13) {
            return 6;
        }
        if (intValue == 24) {
            return 7;
        }
        return null;
    }

    public List<Integer> valueForFasce(int i) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(-1);
        arrayList.add(-1);
        arrayList.add(0);
        arrayList.add(0);
        arrayList.add(0);
        if (getStartTime(i) != null) {
            arrayList.set(0, Integer.valueOf(getTimeAsInt(getStartTime(i))));
            arrayList.set(1, Integer.valueOf(getTimeAsInt(getEndTime(i))));
            Integer mode = getMode(i);
            int intValue = mode.intValue();
            arrayList.set(2, mode);
            if (intValue == 1 || intValue == 2 || intValue == 3 || intValue == 6 || intValue == 7) {
                arrayList.set(3, getValueToWrite(i));
            } else if (intValue == 4 || intValue == 5 || intValue == 10 || intValue == 11) {
                arrayList.set(4, getValueToWrite(i));
            }
        }
        return arrayList;
    }

    public List<Integer> valueForFasceVMC(int i) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(-1);
        arrayList.add(-1);
        arrayList.add(-1);
        if (getStartTime(i) != null) {
            arrayList.set(0, Integer.valueOf(getTimeAsInt(getStartTime(i))));
            arrayList.set(1, Integer.valueOf(getTimeAsInt(getEndTime(i))));
            Integer mode = getMode(i);
            mode.intValue();
            arrayList.set(2, mode);
        }
        return arrayList;
    }
}
