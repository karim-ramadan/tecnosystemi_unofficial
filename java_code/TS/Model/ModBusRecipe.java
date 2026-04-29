package it.tecnosystemi.TS.Model;

import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import it.tecnosystemi.TS.Utils.Version;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;

public class ModBusRecipe {
    private List<FW> Fws;
    private long PRAN_Id;
    private String PRAN_ProductName;
    private List<Param> Params;

    public long getPRAN_Id() {
        return this.PRAN_Id;
    }

    public void setPRAN_Id(long j) {
        this.PRAN_Id = j;
    }

    public String getPRAN_ProductName() {
        return this.PRAN_ProductName;
    }

    public void setPRAN_ProductName(String str) {
        this.PRAN_ProductName = str;
    }

    public List<FW> getFws() {
        return this.Fws;
    }

    public void setFws(List<FW> list) {
        this.Fws = list;
    }

    public List<Param> getParams() {
        return this.Params;
    }

    public void setParams(List<Param> list) {
        this.Params = list;
    }

    public static class FW {
        private long PRFW_Id;
        private String PRFW_RecipeKey;
        private String PRFW_Version;

        public long getPRFW_Id() {
            return this.PRFW_Id;
        }

        public void setPRFW_Id(long j) {
            this.PRFW_Id = j;
        }

        public String getPRFW_RecipeKey() {
            return this.PRFW_RecipeKey;
        }

        public void setPRFW_RecipeKey(String str) {
            this.PRFW_RecipeKey = str;
        }

        public String getPRFW_Version() {
            return this.PRFW_Version;
        }

        public void setPRFW_Version(String str) {
            this.PRFW_Version = str;
        }
    }

    public static class Param {
        private long PRPA_Id;
        private long PRPA_IdParam;
        private long PRPA_PRMT_Id;
        private long PRPA_PRPT_Id;
        private String PRPA_UM;
        private String PRPP_Address;
        private int PRPP_Default;
        private boolean PRPP_Editable;
        private long PRPP_Id;
        private int PRPP_Incr;
        public Integer PRPP_LogicAddress;
        public long PRPP_MQTTState;
        private int PRPP_Max;
        private int PRPP_Min;
        public String PRPP_ModbusFun;
        private long PRPP_PRFT_Id;
        private String PRPP_PRFW_Max;
        private String PRPP_PRFW_Min;
        public Integer PRPP_Sampling;
        private int PRPP_Size;
        private List<ParamLabel> ParamLabels;
        private String Value;
        private int idScheda;
        int indicepercolore;

        public int getIndicepercolore() {
            return this.indicepercolore;
        }

        public void setIndicepercolore(int i) {
            this.indicepercolore = i;
        }

        public int getIdScheda() {
            return this.idScheda;
        }

        public void setIdScheda(int i) {
            this.idScheda = i;
        }

        public String getValue() {
            return this.Value;
        }

        public void setValue(String str) {
            this.Value = str;
        }

        public List<ParamLabel> getParamLabels() {
            return this.ParamLabels;
        }

        public void setParamLabels(List<ParamLabel> list) {
            this.ParamLabels = list;
        }

        public long getPRPP_Id() {
            return this.PRPP_Id;
        }

        public void setPRPP_Id(long j) {
            this.PRPP_Id = j;
        }

        public long getPRPA_Id() {
            return this.PRPA_Id;
        }

        public void setPRPA_Id(long j) {
            this.PRPA_Id = j;
        }

        public long getPRPA_IdParam() {
            return this.PRPA_IdParam;
        }

        public void setPRPA_IdParam(long j) {
            this.PRPA_IdParam = j;
        }

        public long getPRPA_PRMT_Id() {
            return this.PRPA_PRMT_Id;
        }

        public void setPRPA_PRMT_Id(long j) {
            this.PRPA_PRMT_Id = j;
        }

        public long getPRPA_PRPT_Id() {
            return this.PRPA_PRPT_Id;
        }

        public void setPRPA_PRPT_Id(long j) {
            this.PRPA_PRPT_Id = j;
        }

        public String getPRPA_UM() {
            return this.PRPA_UM;
        }

        public void setPRPA_UM(String str) {
            this.PRPA_UM = str;
        }

        public String getPRPP_Address() {
            return this.PRPP_Address;
        }

        public void setPRPP_Address(String str) {
            this.PRPP_Address = str;
        }

        public int getPRPP_Default() {
            return this.PRPP_Default;
        }

        public void setPRPP_Default(int i) {
            this.PRPP_Default = i;
        }

        public boolean isPRPP_Editable() {
            return this.PRPP_Editable;
        }

        public void setPRPP_Editable(boolean z) {
            this.PRPP_Editable = z;
        }

        public int getPRPP_Incr() {
            return this.PRPP_Incr;
        }

        public void setPRPP_Incr(int i) {
            this.PRPP_Incr = i;
        }

        public Integer getPRPP_LogicAddress() {
            return this.PRPP_LogicAddress;
        }

        public void setPRPP_LogicAddress(Integer num) {
            this.PRPP_LogicAddress = num;
        }

        public int getPRPP_Max() {
            return this.PRPP_Max;
        }

        public void setPRPP_Max(int i) {
            this.PRPP_Max = i;
        }

        public int getPRPP_Min() {
            return this.PRPP_Min;
        }

        public void setPRPP_Min(int i) {
            this.PRPP_Min = i;
        }

        public String getPRPP_ModbusFun() {
            return this.PRPP_ModbusFun;
        }

        public void setPRPP_ModbusFun(String str) {
            this.PRPP_ModbusFun = str;
        }

        public long getPRPP_MQTTState() {
            return this.PRPP_MQTTState;
        }

        public void setPRPP_MQTTState(long j) {
            this.PRPP_MQTTState = j;
        }

        public long getPRPP_PRFT_Id() {
            return this.PRPP_PRFT_Id;
        }

        public void setPRPP_PRFT_Id(long j) {
            this.PRPP_PRFT_Id = j;
        }

        public String getPRPP_PRFW_Min() {
            return this.PRPP_PRFW_Min;
        }

        public void setPRPP_PRFW_Min(String str) {
            this.PRPP_PRFW_Min = str;
        }

        public String getPRPP_PRFW_Max() {
            return this.PRPP_PRFW_Max;
        }

        public void setPRPP_PRFW_Max(String str) {
            this.PRPP_PRFW_Max = str;
        }

        public void setPRPP_Sampling(Integer num) {
            this.PRPP_Sampling = num;
        }

        public int getPRPP_Size() {
            return this.PRPP_Size;
        }

        public void setPRPP_Size(int i) {
            this.PRPP_Size = i;
        }

        public String getName() {
            return Functions.getTrasnslation(Constants.PARAM_NAME_STRING + this.PRPA_IdParam, "");
        }

        public String getSdesc() {
            return Functions.getTrasnslation(Constants.PARAM_SDESC_STRING + this.PRPA_IdParam, "");
        }

        public String getLdesc() {
            return Functions.getTrasnslation(Constants.PARAM_LDESC_STRING + this.PRPA_IdParam, "");
        }

        public double getCorrectMax() {
            return getCorrectValue(this, (double) this.PRPP_Max);
        }

        public double getCorrectMin() {
            return getCorrectValue(this, (double) this.PRPP_Min);
        }

        public double getCorrectIncr() {
            return getCorrectValue(this, (double) this.PRPP_Incr);
        }

        public double getCorrectDef() {
            return getCorrectValue(this, (double) this.PRPP_Default);
        }

        public double getCorrectValue() {
            return getCorrectValue(this, getParsedValue());
        }

        public void setPRPP_LogicAddress(int i) {
            this.PRPP_LogicAddress = Integer.valueOf(i);
        }

        public int getPRPP_Sampling() {
            return this.PRPP_Sampling.intValue();
        }

        public void setPRPP_Sampling(int i) {
            this.PRPP_Sampling = Integer.valueOf(i);
        }

        public String getValToShow(boolean z) {
            String str;
            long j = this.PRPA_PRPT_Id;
            if (j == 1 || j == 4) {
                DecimalFormat decimalFormat = new DecimalFormat("0.00", new DecimalFormatSymbols(Locale.US));
                DecimalFormat decimalFormat2 = new DecimalFormat("0.0", new DecimalFormatSymbols(Locale.US));
                if (getPRPP_PRFT_Id() == 2) {
                    str = decimalFormat2.format(getCorrectValue()) + "";
                } else if (getPRPP_PRFT_Id() == 4) {
                    str = decimalFormat.format(getCorrectValue()) + "";
                } else {
                    str = ((int) getCorrectValue()) + "";
                }
                if (!z) {
                    return str;
                }
                return str + " " + this.PRPA_UM;
            } else if (j == 5) {
                String valToShowUser = getValToShowUser((Double) null, (Double) null);
                if (!z) {
                    return valToShowUser;
                }
                return valToShowUser + " " + this.PRPA_UM;
            } else if (j == 2) {
                return Functions.getTrasnslation(getParsedValue() == 0.0d ? R.string.za_cronoON : R.string.za_cronoOFF);
            } else if (j != 3) {
                return String.valueOf(this.Value);
            } else {
                int i = 0;
                ParamLabel paramLabel = this.ParamLabels.get(0);
                while (true) {
                    if (i >= this.ParamLabels.size()) {
                        break;
                    } else if (((double) this.ParamLabels.get(i).getPRPL_Value()) == getParsedValue()) {
                        paramLabel = this.ParamLabels.get(i);
                        break;
                    } else {
                        i++;
                    }
                }
                return Functions.getTrasnslation(Constants.PARAM_NAME_STRING + paramLabel.getPRLA_IdLabel(), paramLabel.getPRLA_Descr());
            }
        }

        public double getParsedValue() {
            String str = this.Value;
            if (str == null || !str.startsWith("h")) {
                return -999.0d;
            }
            byte[] hexStringToByteArray = ModBusRecipe.hexStringToByteArray(this.Value.substring(1));
            if (hexStringToByteArray.length == 2) {
                return (double) ByteBuffer.wrap(hexStringToByteArray).order(ByteOrder.BIG_ENDIAN).getShort();
            }
            if (this.PRPA_PRPT_Id == 5) {
                return (double) ByteBuffer.wrap(hexStringToByteArray).order(ByteOrder.BIG_ENDIAN).getFloat();
            }
            return (double) ByteBuffer.wrap(hexStringToByteArray).order(ByteOrder.BIG_ENDIAN).getInt();
        }

        public String getValToShowUser(Double d, Double d2) {
            double parsedValue = getParsedValue();
            if (d != null && parsedValue <= d.doubleValue()) {
                return "---";
            }
            if (d2 != null && parsedValue >= d2.doubleValue()) {
                return "---";
            }
            return String.format(Locale.US, "%.1f", new Object[]{Double.valueOf(getParsedValue())});
        }

        public String getDescrValue() {
            if (this.PRPA_PRPT_Id != 3) {
                return "";
            }
            int i = 0;
            ParamLabel paramLabel = this.ParamLabels.get(0);
            while (true) {
                if (i >= this.ParamLabels.size()) {
                    break;
                } else if (((double) this.ParamLabels.get(i).getPRPL_Value()) == getParsedValue()) {
                    paramLabel = this.ParamLabels.get(i);
                    break;
                } else {
                    i++;
                }
            }
            return Functions.getTrasnslation(Constants.PARAM_NAME_STRING + paramLabel.getPRLA_IdLabel(), "") + ": " + Functions.getTrasnslation(Constants.PARAM_SDESC_STRING + paramLabel.getPRLA_IdLabel(), "");
        }

        public boolean isParErrInErr() {
            try {
                ParamLabel paramLabel = this.ParamLabels.get(0);
                int i = 0;
                while (true) {
                    if (i >= this.ParamLabels.size()) {
                        break;
                    } else if (((double) this.ParamLabels.get(i).getPRPL_Value()) == getParsedValue()) {
                        paramLabel = this.ParamLabels.get(i);
                        break;
                    } else {
                        i++;
                    }
                }
                if (paramLabel.getPRLA_IdLabel() != 10000) {
                    return true;
                }
                return false;
            } catch (Exception unused) {
                return false;
            }
        }

        public static double getCorrectValue(Param param, double d) {
            int pRPP_PRFT_Id = (int) param.getPRPP_PRFT_Id();
            if (pRPP_PRFT_Id == 1) {
                return d * 10.0d;
            }
            if (pRPP_PRFT_Id == 2) {
                return d / 10.0d;
            }
            if (pRPP_PRFT_Id != 3) {
                return pRPP_PRFT_Id != 4 ? d : d / 100.0d;
            }
            return d * 100.0d;
        }

        public static boolean IsValidForFW(Param param, String str) {
            if (param.getPRPP_PRFW_Min() == null && param.getPRPP_PRFW_Max() == null) {
                return true;
            }
            Version parseVersion = Version.parseVersion(param.getPRPP_PRFW_Min());
            Version parseVersion2 = Version.parseVersion(param.getPRPP_PRFW_Max());
            Version parseVersion3 = Version.parseVersion(str);
            if (parseVersion != null && Version.compareVersions(parseVersion3.major, parseVersion3.minor, parseVersion3.release, parseVersion.major, parseVersion.minor, parseVersion.release) < 0) {
                return false;
            }
            if (parseVersion2 == null || Version.compareVersions(parseVersion3.major, parseVersion3.minor, parseVersion3.release, parseVersion2.major, parseVersion2.minor, parseVersion2.release) <= 0) {
                return true;
            }
            return false;
        }

        public static class ParamLabel {
            private String PRLA_Descr;
            private long PRLA_IdLabel;
            private long PRPL_Id;
            private int PRPL_Value;

            public String getPRLA_Descr() {
                return this.PRLA_Descr;
            }

            public void setPRLA_Descr(String str) {
                this.PRLA_Descr = str;
            }

            public long getPRPL_Id() {
                return this.PRPL_Id;
            }

            public void setPRPL_Id(long j) {
                this.PRPL_Id = j;
            }

            public long getPRLA_IdLabel() {
                return this.PRLA_IdLabel;
            }

            public void setPRLA_IdLabel(long j) {
                this.PRLA_IdLabel = j;
            }

            public int getPRPL_Value() {
                return this.PRPL_Value;
            }

            public void setPRPL_Value(int i) {
                this.PRPL_Value = i;
            }

            public String getName() {
                return Functions.getTrasnslation(Constants.PARAM_NAME_STRING + this.PRLA_IdLabel, getPRLA_Descr());
            }
        }

        public static class ConfigParam {
            private long DVCP_PRPA_Id;
            private long DVCP_PRPP_Id;
            private int DVCP_Value;

            public long getDVCP_PRPA_Id() {
                return this.DVCP_PRPA_Id;
            }

            public void setDVCP_PRPA_Id(long j) {
                this.DVCP_PRPA_Id = j;
            }

            public long getDVCP_PRPP_Id() {
                return this.DVCP_PRPP_Id;
            }

            public void setDVCP_PRPP_Id(long j) {
                this.DVCP_PRPP_Id = j;
            }

            public int getDVCP_Value() {
                return this.DVCP_Value;
            }

            public void setDVCP_Value(int i) {
                this.DVCP_Value = i;
            }
        }
    }

    public static byte[] hexStringToByteArray(String str) {
        int length = str.length();
        byte[] bArr = new byte[(length / 2)];
        for (int i = 0; i < length; i += 2) {
            bArr[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4) + Character.digit(str.charAt(i + 1), 16));
        }
        return bArr;
    }
}
