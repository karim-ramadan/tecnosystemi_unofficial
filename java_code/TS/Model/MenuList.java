package it.tecnosystemi.TS.Model;

import androidx.constraintlayout.widget.ConstraintLayout;
import java.lang.reflect.Method;
import java.util.List;

public class MenuList {
    List<Method> ad;
    List<ConstraintLayout> layouts;
    List<String> name;

    public List<Method> getAd() {
        return this.ad;
    }

    public void setAd(List<Method> list) {
        this.ad = list;
    }

    public List<String> getName() {
        return this.name;
    }

    public void setName(List<String> list) {
        this.name = list;
    }

    public List<ConstraintLayout> getLayouts() {
        return this.layouts;
    }

    public void setLayouts(List<ConstraintLayout> list) {
        this.layouts = list;
    }
}
