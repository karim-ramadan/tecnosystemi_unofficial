package it.tecnosystemi.TS.Activity.VMC;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.gson.Gson;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Adapters.PICOCronoSummaryAdapter;
import it.tecnosystemi.TS.Commands.CmdPICO;
import it.tecnosystemi.TS.Commands.PICOServer;
import it.tecnosystemi.TS.Commands.Protocols;
import it.tecnosystemi.TS.Commands.UDPSocket;
import it.tecnosystemi.TS.Model.PICOCronoObj;
import it.tecnosystemi.TS.Model.Response;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.json.JSONObject;

public class VMCCronoSummaryActivity extends BaseActivity {
    public static List<PICOCronoObj> cronos;
    public static CmdPICO.PICO_Fasce.Response fasce;
    BaseActivity activity;
    PICOCronoSummaryAdapter adapter;
    ListView lv;

    public BaseActivity getActivity() {
        return this;
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        return list;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_vmccrono_summary);
        this.typeActStyle = 3;
        this.activity = this;
        super.onCreate(bundle);
        hideMenuButton();
        this.lv = (ListView) findViewById(R.id.csa_listView);
        VMCCronoSetActivity.indexZonetoCopy = -1;
        Functions.setFontsWithIcon(findViewById(R.id.main), this);
    }

    private void getTimeZones() {
        if (Constants.ISDEMO) {
            finish();
            return;
        }
        showProgress();
        new Thread(new Runnable() {
            public void run() {
                try {
                    CmdPICO cmdPICO = new CmdPICO();
                    cmdPICO.setIdp((long) VMCActivity.getIDP());
                    cmdPICO.setCmd(Protocols.CMD_GETFASCE);
                    cmdPICO.setPin(VMCActivity.vmc.getPin());
                    if (VMCActivity.vmc.getOffline().booleanValue()) {
                        VMCCronoSummaryActivity.this.parseRespGetFasce(UDPSocket.sendCMD(cmdPICO));
                        return;
                    }
                    PICOServer pICOServer = new PICOServer();
                    pICOServer.setSerial(VMCActivity.vmc.getSerial());
                    pICOServer.setPin(VMCActivity.vmc.getPin());
                    pICOServer.setName(VMCActivity.vmc.getName());
                    cmdPICO.setFrm("mqtt");
                    pICOServer.setCmd(new Gson().toJson((Object) cmdPICO));
                    Response makeApiCall = VMCCronoSummaryActivity.this.makeApiCall(VMCCronoSummaryActivity.this.getResources().getString(R.string.uriWebService) + VMCCronoSummaryActivity.this.getResources().getString(R.string.uri_SendVMCCmd), new Gson().toJson((Object) pICOServer), 1, 0, Constants.user, false);
                    if (makeApiCall != null) {
                        try {
                            JSONObject jSONObject = new JSONObject(makeApiCall.getHttpResponcePayload());
                            if (jSONObject.getInt("ResCode") == 0) {
                                VMCCronoSummaryActivity.this.parseRespGetFasce(jSONObject.getString("ResDescr"));
                            }
                        } catch (Exception unused) {
                        }
                    }
                } catch (Exception unused2) {
                    VMCCronoSummaryActivity.this.hideProgress();
                }
            }
        }).start();
    }

    private boolean checkRespSetMode(String str) {
        if (str == null) {
            return false;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (!jSONObject.has("res") || jSONObject.getInt("res") != 1) {
                return false;
            }
            return true;
        } catch (Exception unused) {
            return false;
        }
    }

    /* access modifiers changed from: private */
    public void parseRespGetFasce(String str) {
        try {
            if (checkRespSetMode(str)) {
                CmdPICO.PICO_Fasce.Response response = (CmdPICO.PICO_Fasce.Response) new Gson().fromJson(str, CmdPICO.PICO_Fasce.Response.class);
                fasce = response;
                cronos = response.getCronosVMC(this.activity);
                if (VMCActivity.vmc.getOffline().booleanValue()) {
                    CmdPICO.UPD_DateTime uPD_DateTime = new CmdPICO.UPD_DateTime();
                    Date date = new Date();
                    uPD_DateTime.setDate(new SimpleDateFormat("yyyy-MM-dd").format(date));
                    uPD_DateTime.setTime(new SimpleDateFormat("hh:mm:ss").format(date));
                    switch (Calendar.getInstance().get(7)) {
                        case 1:
                            uPD_DateTime.setWeek(6);
                            break;
                        case 2:
                            uPD_DateTime.setWeek(0);
                            break;
                        case 3:
                            uPD_DateTime.setWeek(1);
                            break;
                        case 4:
                            uPD_DateTime.setWeek(2);
                            break;
                        case 5:
                            uPD_DateTime.setWeek(3);
                            break;
                        case 6:
                            uPD_DateTime.setWeek(4);
                            break;
                        case 7:
                            uPD_DateTime.setWeek(5);
                            break;
                    }
                    UDPSocket.sendCMD(uPD_DateTime);
                }
                hideProgress();
                showfasce();
                return;
            }
        } catch (Exception e) {
            Log.d("ERR", e.toString());
        }
        Functions.makeErrorToast(this.activity, getResources().getString(R.string.msg_commandKo));
        hideProgress();
    }

    private void showfasce() {
        runOnUiThread(new Runnable() {
            public void run() {
                VMCCronoSummaryActivity.this.adapter = new PICOCronoSummaryAdapter(VMCCronoSummaryActivity.this.activity, VMCCronoSummaryActivity.cronos);
                VMCCronoSummaryActivity.this.lv.setAdapter(VMCCronoSummaryActivity.this.adapter);
            }
        });
    }

    public void onResume() {
        super.onResume();
        getTimeZones();
    }

    public View getToolBar() {
        return findViewById(R.id.vmc_toolbar);
    }

    public String setToolbarTitle() {
        return VMCActivity.vmc.getName();
    }
}
