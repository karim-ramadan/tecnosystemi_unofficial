package it.tecnosystemi.TS.Activity.PICO;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.gson.Gson;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Adapters.PICOCronoSummaryAdapter;
import it.tecnosystemi.TS.Commands.CmdPICO;
import it.tecnosystemi.TS.Commands.PICOServerTimezone;
import it.tecnosystemi.TS.Commands.Protocols;
import it.tecnosystemi.TS.Commands.UDPSocket;
import it.tecnosystemi.TS.Model.PICOCronoObj;
import it.tecnosystemi.TS.Model.Response;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Threads.ThreadWebService;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.json.JSONObject;

public class PICOCronoSummaryActivity extends BaseActivity {
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
        setContentView(R.layout.activity_pico_crono_summary);
        this.typeActStyle = 2;
        this.activity = this;
        super.onCreate(bundle);
        this.lv = (ListView) findViewById(R.id.csa_listView);
        PicoCronoSetActivity.indexZonetoCopy = -1;
        hideMenuButton();
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
                    cmdPICO.setIdp((long) PicoActivity.getIDP());
                    cmdPICO.setCmd(Protocols.CMD_GETFASCE);
                    cmdPICO.setPin(PicoActivity.pico.getPin());
                    if (PicoActivity.pico.getOffline().booleanValue()) {
                        PICOCronoSummaryActivity.this.parseRespGetFasce(UDPSocket.sendCMD(cmdPICO));
                        return;
                    }
                    PICOServerTimezone pICOServerTimezone = new PICOServerTimezone();
                    pICOServerTimezone.setSerial(PicoActivity.pico.getSerial());
                    pICOServerTimezone.setPin(PicoActivity.pico.getPin());
                    cmdPICO.setFrm("mqtt");
                    pICOServerTimezone.setCmd(new Gson().toJson((Object) cmdPICO));
                    new ThreadWebService(PICOCronoSummaryActivity.this.activity, 1, 33, PICOCronoSummaryActivity.this.getResources().getString(R.string.uriWebService_PICO) + PICOCronoSummaryActivity.this.getResources().getString(R.string.uri_SendPicoCmd), new Gson().toJson((Object) pICOServerTimezone), (String[]) null).start();
                } catch (Exception unused) {
                    PICOCronoSummaryActivity.this.hideProgress();
                }
            }
        }).start();
    }

    public void parseRespGetFasceServer(Response response, int i) {
        if (response != null) {
            try {
                if (response.getHttpResponceCode() == 200) {
                    JSONObject jSONObject = new JSONObject(response.getHttpResponcePayload());
                    if (jSONObject.has("ResCode") && jSONObject.getInt("ResCode") == 0) {
                        parseRespGetFasce(jSONObject.getString("ResDescr"));
                        hideProgress();
                        return;
                    }
                }
            } catch (Exception unused) {
            }
        }
        Functions.makeErrorToast(this.activity, getResources().getString(R.string.msg_commandKo));
        hideProgress();
    }

    /* access modifiers changed from: private */
    public void parseRespGetFasce(String str) {
        try {
            if (checkRespSetMode(str)) {
                CmdPICO.PICO_Fasce.Response response = (CmdPICO.PICO_Fasce.Response) new Gson().fromJson(str, CmdPICO.PICO_Fasce.Response.class);
                fasce = response;
                cronos = response.getCronos(this.activity);
                if (PicoActivity.pico.getOffline().booleanValue()) {
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
        } catch (Exception unused) {
        }
        Functions.makeErrorToast(this.activity, getResources().getString(R.string.msg_commandKo));
        hideProgress();
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

    private void showfasce() {
        runOnUiThread(new Runnable() {
            public void run() {
                PICOCronoSummaryActivity.this.adapter = new PICOCronoSummaryAdapter(PICOCronoSummaryActivity.this.activity, PICOCronoSummaryActivity.cronos);
                PICOCronoSummaryActivity.this.lv.setAdapter(PICOCronoSummaryActivity.this.adapter);
            }
        });
    }

    public void onResume() {
        super.onResume();
        getTimeZones();
    }

    public View getToolBar() {
        return findViewById(R.id.ewa_toolbar);
    }

    public String setToolbarTitle() {
        return PicoActivity.pico.getName();
    }
}
