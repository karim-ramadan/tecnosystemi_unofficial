package it.tecnosystemi.TS.Commands;

import android.app.Activity;
import it.tecnosystemi.TS.Model.ModBusRecipe;
import it.tecnosystemi.TS.Model.PICOCronoObj;
import it.tecnosystemi.TS.Model.Pico;
import it.tecnosystemi.TS.R;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CmdPICO {
    /* access modifiers changed from: private */
    public String cmd;
    private String frm = "app";
    private long idp;
    private String pin;

    public static class ACK {
        private String frm;
        private long idp;
        private long res = 99;
    }

    public String getCmd() {
        return this.cmd;
    }

    public void setCmd(String str) {
        this.cmd = str;
    }

    public String getFrm() {
        return this.frm;
    }

    public void setFrm(String str) {
        this.frm = str;
    }

    public String getPin() {
        return this.pin;
    }

    public void setPin(String str) {
        this.pin = str;
    }

    public long getIdp() {
        return this.idp;
    }

    public void setIdp(long j) {
        this.idp = j;
    }

    public static class Upd6X extends CmdPICO {
        private int m_crono;
        private int man_reset;
        private int mod;
        private int on_off;

        public Upd6X() {
            String unused = this.cmd = "upd_P6X";
        }

        public int getM_crono() {
            return this.m_crono;
        }

        public void setM_crono(int i) {
            this.m_crono = i;
        }

        public int getMan_reset() {
            return this.man_reset;
        }

        public void setMan_reset(int i) {
            this.man_reset = i;
        }

        public int getOn_off() {
            return this.on_off;
        }

        public void setOn_off(int i) {
            this.on_off = i;
        }

        public int getMod() {
            return this.mod;
        }

        public void setMod(int i) {
            this.mod = i;
        }
    }

    public static class Upd6X_OnOff extends CmdPICO {
        private int on_off;

        public Upd6X_OnOff() {
            String unused = this.cmd = "upd_P6X";
        }

        public int getOn_off() {
            return this.on_off;
        }

        public void setOn_off(int i) {
            this.on_off = i;
        }
    }

    public static class Upd6X_OnOff_Mode extends CmdPICO {
        private int mod;
        private int on_off;

        public Upd6X_OnOff_Mode() {
            String unused = this.cmd = "upd_P6X";
        }

        public int getOn_off() {
            return this.on_off;
        }

        public void setOn_off(int i) {
            this.on_off = i;
        }

        public int getMod() {
            return this.mod;
        }

        public void setMod(int i) {
            this.mod = i;
        }
    }

    public static class UpdPicoON extends CmdPICO {
        private int on_off;

        public UpdPicoON() {
            String unused = this.cmd = "upd_pico";
        }

        public int getOn_off() {
            return this.on_off;
        }

        public void setOn_off(int i) {
            this.on_off = i;
        }
    }

    public static class UpdPManReset extends CmdPICO {
        private int[] man_reset;

        public UpdPManReset(int[] iArr) {
            String unused = this.cmd = "upd_pico";
            this.man_reset = new int[iArr.length];
            for (int i = 0; i < iArr.length; i++) {
                if (iArr[i] == 1) {
                    this.man_reset[i] = 1;
                } else {
                    this.man_reset[i] = 0;
                }
            }
        }

        public int[] getMan_reset() {
            return this.man_reset;
        }

        public void setMan_reset(int[] iArr) {
            this.man_reset = iArr;
        }
    }

    public static class UpdPicoLed extends CmdPICO {
        private int led_on_off_breve;

        public UpdPicoLed() {
            String unused = this.cmd = "upd_pico";
        }

        public int getLed_on_off() {
            return this.led_on_off_breve;
        }

        public void setLed_on_off(int i) {
            this.led_on_off_breve = i;
        }
    }

    public static class UpdPicoHum extends CmdPICO {
        private int s_umd;

        public UpdPicoHum() {
            String unused = this.cmd = "upd_pico";
        }

        public int getS_umd() {
            return this.s_umd;
        }

        public void setS_umd(int i) {
            this.s_umd = i;
        }
    }

    public static class UpdPicoSpeed extends CmdPICO {
        private int spd_row;
        private int speed = 0;

        public UpdPicoSpeed() {
            String unused = this.cmd = "upd_pico";
            this.speed = 0;
        }

        public int getSpeed() {
            return this.speed;
        }

        public void setSpeed(int i) {
            this.speed = i;
        }

        public int getSpd_row() {
            return this.spd_row;
        }

        public void setSpd_row(int i) {
            this.spd_row = i;
        }
    }

    public static class UpdPicoNight extends CmdPICO {
        private int night_mod;

        public UpdPicoNight() {
            String unused = this.cmd = "upd_pico";
        }

        public int getNight_mod() {
            return this.night_mod;
        }

        public void setNight_mod(int i) {
            this.night_mod = i;
        }
    }

    public static class UpdPicoONMode extends CmdPICO {
        private int mod;
        private int on_off;

        public UpdPicoONMode() {
            String unused = this.cmd = "upd_pico";
        }

        public int getOn_off() {
            return this.on_off;
        }

        public void setOn_off(int i) {
            this.on_off = i;
        }

        public int getMod() {
            return this.mod;
        }

        public void setMod(int i) {
            this.mod = i;
        }
    }

    public static class UpdPicoMCrono extends CmdPICO {
        int m_crono;

        public UpdPicoMCrono() {
            String unused = this.cmd = "upd_pico";
        }

        public int getM_crono() {
            return this.m_crono;
        }

        public void setM_crono(int i) {
            this.m_crono = i;
        }
    }

    public static class UpdPico extends CmdPICO {
        private int led_on_off_breve;
        private int mod;
        private int on_off;
        private int s_umd;
        private int spd_row;
        private int speed;

        public UpdPico() {
            String unused = this.cmd = "upd_pico";
        }

        public int getOn_off() {
            return this.on_off;
        }

        public void setOn_off(int i) {
            this.on_off = i;
        }

        public int getMod() {
            return this.mod;
        }

        public void setMod(int i) {
            this.mod = i;
        }

        public int getSpeed() {
            return this.speed;
        }

        public void setSpeed(int i) {
            this.speed = i;
        }

        public int getUmd() {
            return this.s_umd;
        }

        public void setUmd(int i) {
            this.s_umd = i;
        }

        public int getLed_on_off() {
            return this.led_on_off_breve;
        }

        public void setLed_on_off(int i) {
            this.led_on_off_breve = i;
        }

        public int getSpeed_raw() {
            return this.spd_row;
        }

        public void setSpeed_raw(int i) {
            this.spd_row = i;
        }
    }

    public static class CheckLed extends CmdPICO {
        int led_color = 2;

        public CheckLed() {
            String unused = this.cmd = "check_led";
        }

        public int getLed_color() {
            return this.led_color;
        }

        public void setLed_color(int i) {
            this.led_color = i;
        }
    }

    public static class PicoAP extends CmdPICO {
        int ap_m;

        public PicoAP() {
            String unused = this.cmd = "pico_ap";
            CmdPICO.super.setPin("-1");
        }

        public int getAp_m() {
            return this.ap_m;
        }

        public void setAp_m(int i) {
            this.ap_m = i;
        }
    }

    public static class VmcAP extends PicoAP {
        public VmcAP() {
            super.setCmd("vmc_ap");
        }
    }

    public static class ConfigMasterOffline extends CmdPICO {
        int config_mod;
        long id_slave = 0;
        String name;
        int verso = 1;

        public ConfigMasterOffline() {
            String unused = this.cmd = Protocols.CMD_CONFIG;
        }

        public int getConfig_mod() {
            return this.config_mod;
        }

        public void setConfig_mod(int i) {
            this.config_mod = i;
        }

        public String getName() {
            return this.name;
        }

        public void setName(String str) {
            this.name = str;
        }

        public long getIdSlave() {
            return this.id_slave;
        }

        public void setIdSlave(long j) {
            this.id_slave = j;
        }

        public int getVerso() {
            return this.verso;
        }

        public void setVerso(int i) {
            this.verso = i;
        }
    }

    public static class ConfigMasterOnline extends ConfigMasterOffline {
        String api_token;
        String api_user;
        String wifi_mac;
        String wifi_pwd;
        int wifi_sec;
        String wifi_ssid;

        public int getWifi_sec() {
            return this.wifi_sec;
        }

        public void setWifi_sec(int i) {
            this.wifi_sec = i;
        }

        public String getWifi_ssid() {
            return this.wifi_ssid;
        }

        public void setWifi_ssid(String str) {
            this.wifi_ssid = str;
        }

        public String getWifi_mac() {
            return this.wifi_mac;
        }

        public void setWifi_mac(String str) {
            this.wifi_mac = str;
        }

        public String getWifi_pwd() {
            return this.wifi_pwd;
        }

        public void setWifi_pwd(String str) {
            this.wifi_pwd = str;
        }

        public String getApi_token() {
            return this.api_token;
        }

        public void setApi_token(String str) {
            this.api_token = str;
        }

        public String getApi_user() {
            return this.api_user;
        }

        public void setApi_user(String str) {
            this.api_user = str;
        }
    }

    public static class SetSlave extends CmdPICO {
        List<Pico.Slave.ForSet> list_slave = new ArrayList();

        public SetSlave(List<Pico.Slave> list, boolean z) {
            String unused = this.cmd = "set_slave";
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) != null && (list.get(i).getSet_stato() == 1 || z)) {
                    this.list_slave.add(list.get(i).getSlaveForSet());
                }
            }
        }

        public List<Pico.Slave.ForSet> getList_slave() {
            return this.list_slave;
        }

        public void setList_slave(List<Pico.Slave.ForSet> list) {
            this.list_slave = list;
        }
    }

    public static class OTA_Start extends CmdPICO {
        String sha256;
        int size;

        public OTA_Start() {
            String unused = this.cmd = "ota_start";
        }

        public String getSha() {
            return this.sha256;
        }

        public void setSha(String str) {
            this.sha256 = str;
        }

        public int getSize() {
            return this.size;
        }

        public void setSize(int i) {
            this.size = i;
        }
    }

    public static class UPD_DateTime extends CmdPICO {
        String date;
        String time;
        int week;

        public UPD_DateTime() {
            String unused = this.cmd = "upd_datetime";
        }

        public String getDate() {
            return this.date;
        }

        public void setDate(String str) {
            this.date = str;
        }

        public String getTime() {
            return this.time;
        }

        public void setTime(String str) {
            this.time = str;
        }

        public int getWeek() {
            return this.week;
        }

        public void setWeek(int i) {
            this.week = i;
        }

        public void createDate() {
            this.date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        }
    }

    public static class PICO_Fasce extends CmdPICO {
        private List<List<Integer>> D0;
        private List<List<Integer>> D1;
        private List<List<Integer>> D2;
        private List<List<Integer>> D3;
        private List<List<Integer>> D4;
        private List<List<Integer>> D5;
        private List<List<Integer>> D6;
        private int m_crono;

        public PICO_Fasce() {
            String unused = this.cmd = "set_fasce";
        }

        public int getM_crono() {
            return this.m_crono;
        }

        public void setM_crono(int i) {
            this.m_crono = i;
        }

        public List<List<Integer>> getD0() {
            return this.D0;
        }

        public void setD0(List<List<Integer>> list) {
            this.D0 = list;
        }

        public List<List<Integer>> getD1() {
            return this.D1;
        }

        public void setD1(List<List<Integer>> list) {
            this.D1 = list;
        }

        public List<List<Integer>> getD2() {
            return this.D2;
        }

        public void setD2(List<List<Integer>> list) {
            this.D2 = list;
        }

        public List<List<Integer>> getD3() {
            return this.D3;
        }

        public void setD3(List<List<Integer>> list) {
            this.D3 = list;
        }

        public List<List<Integer>> getD4() {
            return this.D4;
        }

        public void setD4(List<List<Integer>> list) {
            this.D4 = list;
        }

        public List<List<Integer>> getD5() {
            return this.D5;
        }

        public void setD5(List<List<Integer>> list) {
            this.D5 = list;
        }

        public List<List<Integer>> getD6() {
            return this.D6;
        }

        public void setD6(List<List<Integer>> list) {
            this.D6 = list;
        }

        public List<PICOCronoObj> getCronos(Activity activity) {
            ArrayList arrayList = new ArrayList();
            PICOCronoObj pICOCronoObj = new PICOCronoObj();
            pICOCronoObj.setName(activity.getResources().getString(R.string.cs_DayL));
            pICOCronoObj.setValues(this.D0);
            pICOCronoObj.setBackground(activity.getResources().getDrawable(R.drawable.crono_summary_selector_blue));
            pICOCronoObj.setTimeLists();
            pICOCronoObj.setIndexInList(arrayList.size());
            arrayList.add(pICOCronoObj);
            PICOCronoObj pICOCronoObj2 = new PICOCronoObj();
            pICOCronoObj2.setName(activity.getResources().getString(R.string.cs_DayMa));
            pICOCronoObj2.setValues(this.D1);
            pICOCronoObj2.setBackground(activity.getResources().getDrawable(R.drawable.crono_summary_selector_trasparent_pico));
            pICOCronoObj2.setTimeLists();
            pICOCronoObj2.setIndexInList(arrayList.size());
            arrayList.add(pICOCronoObj2);
            PICOCronoObj pICOCronoObj3 = new PICOCronoObj();
            pICOCronoObj3.setName(activity.getResources().getString(R.string.cs_DayMe));
            pICOCronoObj3.setValues(this.D2);
            pICOCronoObj3.setBackground(activity.getResources().getDrawable(R.drawable.crono_summary_selector_blue));
            pICOCronoObj3.setTimeLists();
            pICOCronoObj3.setIndexInList(arrayList.size());
            arrayList.add(pICOCronoObj3);
            PICOCronoObj pICOCronoObj4 = new PICOCronoObj();
            pICOCronoObj4.setName(activity.getResources().getString(R.string.cs_DayG));
            pICOCronoObj4.setValues(this.D3);
            pICOCronoObj4.setBackground(activity.getResources().getDrawable(R.drawable.crono_summary_selector_trasparent_pico));
            pICOCronoObj4.setTimeLists();
            pICOCronoObj4.setIndexInList(arrayList.size());
            arrayList.add(pICOCronoObj4);
            PICOCronoObj pICOCronoObj5 = new PICOCronoObj();
            pICOCronoObj5.setName(activity.getResources().getString(R.string.cs_DayV));
            pICOCronoObj5.setValues(this.D4);
            pICOCronoObj5.setBackground(activity.getResources().getDrawable(R.drawable.crono_summary_selector_blue));
            pICOCronoObj5.setTimeLists();
            pICOCronoObj5.setIndexInList(arrayList.size());
            arrayList.add(pICOCronoObj5);
            PICOCronoObj pICOCronoObj6 = new PICOCronoObj();
            pICOCronoObj6.setName(activity.getResources().getString(R.string.cs_DayS));
            pICOCronoObj6.setValues(this.D5);
            pICOCronoObj6.setBackground(activity.getResources().getDrawable(R.drawable.crono_summary_selector_trasparent_pico));
            pICOCronoObj6.setTimeLists();
            pICOCronoObj6.setIndexInList(arrayList.size());
            arrayList.add(pICOCronoObj6);
            PICOCronoObj pICOCronoObj7 = new PICOCronoObj();
            pICOCronoObj7.setName(activity.getResources().getString(R.string.cs_DayD));
            pICOCronoObj7.setValues(this.D6);
            pICOCronoObj7.setBackground(activity.getResources().getDrawable(R.drawable.crono_summary_selector_blue));
            pICOCronoObj7.setTimeLists();
            pICOCronoObj7.setIndexInList(arrayList.size());
            arrayList.add(pICOCronoObj7);
            return arrayList;
        }

        public List<PICOCronoObj> getCronosVMC(Activity activity) {
            ArrayList arrayList = new ArrayList();
            PICOCronoObj pICOCronoObj = new PICOCronoObj();
            pICOCronoObj.setName(activity.getResources().getString(R.string.cs_DayL));
            pICOCronoObj.setValues(this.D0);
            pICOCronoObj.setBackground(activity.getResources().getDrawable(R.drawable.crono_summary_selector_vmc));
            pICOCronoObj.setTimeListsVMC();
            pICOCronoObj.setIndexInList(arrayList.size());
            arrayList.add(pICOCronoObj);
            PICOCronoObj pICOCronoObj2 = new PICOCronoObj();
            pICOCronoObj2.setName(activity.getResources().getString(R.string.cs_DayMa));
            pICOCronoObj2.setValues(this.D1);
            pICOCronoObj2.setBackground(activity.getResources().getDrawable(R.drawable.crono_summary_selector_trasparent_vmc));
            pICOCronoObj2.setTimeListsVMC();
            pICOCronoObj2.setIndexInList(arrayList.size());
            arrayList.add(pICOCronoObj2);
            PICOCronoObj pICOCronoObj3 = new PICOCronoObj();
            pICOCronoObj3.setName(activity.getResources().getString(R.string.cs_DayMe));
            pICOCronoObj3.setValues(this.D2);
            pICOCronoObj3.setBackground(activity.getResources().getDrawable(R.drawable.crono_summary_selector_vmc));
            pICOCronoObj3.setTimeListsVMC();
            pICOCronoObj3.setIndexInList(arrayList.size());
            arrayList.add(pICOCronoObj3);
            PICOCronoObj pICOCronoObj4 = new PICOCronoObj();
            pICOCronoObj4.setName(activity.getResources().getString(R.string.cs_DayG));
            pICOCronoObj4.setValues(this.D3);
            pICOCronoObj4.setBackground(activity.getResources().getDrawable(R.drawable.crono_summary_selector_trasparent_vmc));
            pICOCronoObj4.setTimeListsVMC();
            pICOCronoObj4.setIndexInList(arrayList.size());
            arrayList.add(pICOCronoObj4);
            PICOCronoObj pICOCronoObj5 = new PICOCronoObj();
            pICOCronoObj5.setName(activity.getResources().getString(R.string.cs_DayV));
            pICOCronoObj5.setValues(this.D4);
            pICOCronoObj5.setBackground(activity.getResources().getDrawable(R.drawable.crono_summary_selector_vmc));
            pICOCronoObj5.setTimeListsVMC();
            pICOCronoObj5.setIndexInList(arrayList.size());
            arrayList.add(pICOCronoObj5);
            PICOCronoObj pICOCronoObj6 = new PICOCronoObj();
            pICOCronoObj6.setName(activity.getResources().getString(R.string.cs_DayS));
            pICOCronoObj6.setValues(this.D5);
            pICOCronoObj6.setBackground(activity.getResources().getDrawable(R.drawable.crono_summary_selector_trasparent_vmc));
            pICOCronoObj6.setTimeListsVMC();
            pICOCronoObj6.setIndexInList(arrayList.size());
            arrayList.add(pICOCronoObj6);
            PICOCronoObj pICOCronoObj7 = new PICOCronoObj();
            pICOCronoObj7.setName(activity.getResources().getString(R.string.cs_DayD));
            pICOCronoObj7.setValues(this.D6);
            pICOCronoObj7.setBackground(activity.getResources().getDrawable(R.drawable.crono_summary_selector_vmc));
            pICOCronoObj7.setTimeListsVMC();
            pICOCronoObj7.setIndexInList(arrayList.size());
            arrayList.add(pICOCronoObj7);
            return arrayList;
        }

        public static class Response extends PICO_Fasce {
            private int res;
            private int tw_active;

            public int getRes() {
                return this.res;
            }

            public void setRes(int i) {
                this.res = i;
            }

            public int getTw_active() {
                return this.tw_active;
            }

            public void setTw_active(int i) {
                this.tw_active = i;
            }
        }
    }

    public static class Init_End_Recipe extends CmdPICO {
        private String key_recipe;
        private int numblock;
        private int prmtot;
        private String uartcfg = "115200|n|8|1";

        public String getKey_recipe() {
            return this.key_recipe;
        }

        public void setKey_recipe(String str) {
            this.key_recipe = str;
        }

        public String getUartcfg() {
            return this.uartcfg;
        }

        public void setUartcfg(String str) {
            this.uartcfg = str;
        }

        public int getNumblock() {
            return this.numblock;
        }

        public void setNumblock(int i) {
            this.numblock = i;
        }

        public int getPrmtot() {
            return this.prmtot;
        }

        public void setPrmtot(int i) {
            this.prmtot = i;
        }
    }

    public static class Send_Recipe extends CmdPICO {
        int block;
        private String key_recipe;
        List<Parameter_JS> lstprm;
        int prmprz;
        int prmtot;

        public Send_Recipe() {
            setCmd("send_recipe");
        }

        public int getPrmtot() {
            return this.prmtot;
        }

        public void setPrmtot(int i) {
            this.prmtot = i;
        }

        public String getKey_recipe() {
            return this.key_recipe;
        }

        public void setKey_recipe(String str) {
            this.key_recipe = str;
        }

        public int getPrmprz() {
            return this.prmprz;
        }

        public void setPrmprz(int i) {
            this.prmprz = i;
        }

        public int getBlock() {
            return this.block;
        }

        public void setBlock(int i) {
            this.block = i;
        }

        public List<Parameter_JS> getLstprm() {
            return this.lstprm;
        }

        public void setLstprm(List<ModBusRecipe.Param> list) {
            this.lstprm = new ArrayList();
            for (int i = 0; i < list.size(); i++) {
                this.lstprm.add(new Parameter_JS(list.get(i)));
            }
        }

        private static class Parameter_JS {
            long dad;
            int irg;
            long kid;
            String knm;
            int mbf;
            int nrg;
            List<Integer> opt;
            String prm;
            int sdt;
            int tyd;
            String um;

            public Parameter_JS(ModBusRecipe.Param param) {
                this.kid = (long) param.getIdScheda();
                this.knm = "param_name_" + param.getPRPA_IdParam();
                this.um = param.getPRPA_UM();
                this.dad = (long) param.getPRPP_LogicAddress().intValue();
                this.mbf = Integer.parseInt(param.getPRPP_ModbusFun());
                this.irg = Integer.parseInt(param.getPRPP_Address());
                int pRPP_Size = param.getPRPP_Size();
                this.nrg = pRPP_Size;
                if (pRPP_Size == 1) {
                    this.tyd = 1;
                } else if (pRPP_Size == 2) {
                    if (param.getPRPA_PRPT_Id() == 5) {
                        this.tyd = 3;
                    } else {
                        this.tyd = 2;
                    }
                }
                this.sdt = param.getPRPP_Size() * 2;
                ArrayList arrayList = new ArrayList();
                this.opt = arrayList;
                arrayList.add(Integer.valueOf(param.getPRPP_Min()));
                this.opt.add(Integer.valueOf(param.getPRPP_Max()));
                this.opt.add(Integer.valueOf(param.getPRPP_Default()));
                this.opt.add(Integer.valueOf(param.getPRPP_Sampling()));
                this.opt.add(Integer.valueOf((int) param.getPRPP_MQTTState()));
                this.opt.add(0);
                this.prm = "";
                if (param.isPRPP_Editable()) {
                    this.prm += "w";
                }
                this.prm += "r";
            }
        }
    }

    public static class Rd_param extends CmdPICO {
        int dad;
        List<Integer> ids;
        private String key_recipe;

        public Rd_param() {
            setCmd("rd_param");
        }

        public String getKey_recipe() {
            return this.key_recipe;
        }

        public void setKey_recipe(String str) {
            this.key_recipe = str;
        }

        public List<Integer> getIds() {
            return this.ids;
        }

        public void setIds(List<Integer> list) {
            this.ids = list;
        }

        public int getDad() {
            return this.dad;
        }

        public void setDad(int i) {
            this.dad = i;
        }

        public static class Resp extends Rd_param {
            List<String> val;

            public List<String> getVal() {
                return this.val;
            }

            public void setVal(List<String> list) {
                this.val = list;
            }
        }
    }

    public static class Wr_param extends CmdPICO {
        private int dad;
        private String key_recipe;
        private int kid;
        private int nrg;
        private int tyd;
        private String wrv;

        public Wr_param() {
            setCmd("wr_param");
        }

        public String getKey_recipe() {
            return this.key_recipe;
        }

        public void setKey_recipe(String str) {
            this.key_recipe = str;
        }

        public int getDad() {
            return this.dad;
        }

        public void setDad(int i) {
            this.dad = i;
        }

        public int getKid() {
            return this.kid;
        }

        public void setKid(int i) {
            this.kid = i;
        }

        public String getWrv() {
            return this.wrv;
        }

        public void setWrv(String str) {
            this.wrv = str;
        }

        public int getNrg() {
            return this.nrg;
        }

        public void setNrg(int i) {
            this.nrg = i;
        }

        public int getTyd() {
            return this.tyd;
        }

        public void setTyd(int i) {
            this.tyd = i;
        }

        public static Wr_param fromParam(ModBusRecipe.Param param, String str, float f) {
            byte[] bArr;
            Wr_param wr_param = new Wr_param();
            wr_param.setKey_recipe(str);
            wr_param.setDad(param.getPRPP_LogicAddress().intValue());
            wr_param.setKid(param.getIdScheda());
            wr_param.setNrg(param.getPRPP_Size());
            if (param.getPRPP_Size() == 1) {
                wr_param.setTyd(1);
                bArr = ByteBuffer.allocate(2).order(ByteOrder.BIG_ENDIAN).putShort((short) ((int) f)).array();
            } else if (param.getPRPA_PRPT_Id() == 5) {
                bArr = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putFloat(f).array();
                wr_param.setTyd(3);
            } else {
                bArr = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt((int) f).array();
                wr_param.setTyd(2);
            }
            wr_param.setWrv("h" + bytesToHex(bArr));
            return wr_param;
        }

        private static String bytesToHex(byte[] bArr) {
            StringBuilder sb = new StringBuilder();
            int length = bArr.length;
            for (int i = 0; i < length; i++) {
                sb.append(String.format("%02X", new Object[]{Byte.valueOf(bArr[i])}));
            }
            return sb.toString();
        }
    }
}
