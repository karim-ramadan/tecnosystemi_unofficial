package it.tecnosystemi.TS.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChangePwdModel extends LoginModel {
    @SerializedName("OldPassword")
    @Expose
    private String oldPwd;

    public String getOldPwd() {
        return this.oldPwd;
    }

    public void setOldPwd(String str) {
        this.oldPwd = str;
    }
}
