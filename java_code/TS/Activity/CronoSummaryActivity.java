package it.tecnosystemi.TS.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.gson.Gson;
import it.tecnosystemi.TS.Adapters.CronoSummaryAdapter;
import it.tecnosystemi.TS.Commands.MySocket;
import it.tecnosystemi.TS.Commands.Protocols;
import it.tecnosystemi.TS.Model.ControlUnit;
import it.tecnosystemi.TS.Model.Crono;
import it.tecnosystemi.TS.Model.Zona;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Threads.ThreadWebService;
import it.tecnosystemi.TS.Utils.Constants;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class CronoSummaryActivity extends BaseActivity {
    CronoSummaryAdapter adapter;
    int index;
    ListView lv;
    int unitm;
    Zona zona;
    Zona zonafill;

    public BaseActivity getActivity() {
        return this;
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        return list;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_crono_summary);
        Intent intent = getIntent();
        this.zona = (Zona) intent.getSerializableExtra(Constants.INTENT_ZONA);
        this.cu = (ControlUnit) intent.getSerializableExtra(Constants.INTENT_CU);
        this.index = intent.getIntExtra(Constants.INTENT_INDEXZONA, -1);
        this.unitm = this.cu.getUnitOfMesure();
        super.onCreate(bundle);
        this.lv = (ListView) findViewById(R.id.csa_listView);
        hideMenuButton();
        setUpGui();
        this.zonafill = new Zona();
        CronoSummaryAdapter cronoSummaryAdapter = new CronoSummaryAdapter(this, this.cu, this.zonafill, this.unitm);
        this.adapter = cronoSummaryAdapter;
        this.lv.setAdapter(cronoSummaryAdapter);
    }

    private void setUpGui() {
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
        ((TextView) findViewById(R.id.csa_lblF1)).setTypeface(createFromAsset);
        ((TextView) findViewById(R.id.csa_lblF2)).setTypeface(createFromAsset);
        ((TextView) findViewById(R.id.csa_lblF3)).setTypeface(createFromAsset);
        ((TextView) findViewById(R.id.csa_lblF4)).setTypeface(createFromAsset);
    }

    public void onResume() {
        super.onResume();
        getzone();
    }

    public void getzone() {
        if (Constants.ISDEMO) {
            Zona zona2 = Constants.DEMO_CU.getZone().get(Constants.DEMO_INDEX_ZONA);
            this.zona = zona2;
            if (zona2.getCrono() != null) {
                CronoSummaryAdapter cronoSummaryAdapter = new CronoSummaryAdapter(this, this.cu, this.zona, this.unitm);
                this.adapter = cronoSummaryAdapter;
                this.lv.setAdapter(cronoSummaryAdapter);
            }
        } else if (this.cu.isOffline()) {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        JSONObject jSONObject = new JSONObject("{}");
                        jSONObject.put(Constants.JSON_OFFLINE_COMMAND_C, Protocols.CMD_GETFASCE);
                        jSONObject.put(Constants.JSON_OFFLINE_COMMAND_PIN, CronoSummaryActivity.this.cu.getPinOffline());
                        jSONObject.put(Constants.JSON_OFFLINE_COMMAND_IDZONA, CronoSummaryActivity.this.zona.getZoneId());
                        final JSONObject jSONObject2 = new JSONObject(MySocket.commandToCU(jSONObject.toString(), Constants.ip, Constants.port, true, true, true));
                        CronoSummaryActivity.this.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                CronoSummaryActivity.this.getTWOffline(jSONObject2);
                            }
                        });
                    } catch (Exception unused) {
                    }
                }
            }).start();
        } else {
            new Thread(new Runnable() {
                public void run() {
                    try {
                        new ThreadWebService(CronoSummaryActivity.this.activity, 0, 14, (CronoSummaryActivity.this.getResources().getString(R.string.uriWebService_POLARIS) + CronoSummaryActivity.this.getResources().getString(R.string.uri_GetTW)) + "?cuSerial=" + CronoSummaryActivity.this.cu.getSerial() + "&zoneID=" + CronoSummaryActivity.this.zona.getZoneId() + "&PIN=" + CronoSummaryActivity.this.cu.getPin(), "", (String[]) null).start();
                    } catch (Exception unused) {
                    }
                }
            }).start();
        }
    }

    /* access modifiers changed from: private */
    public void getTWOffline(JSONObject jSONObject) {
        if (jSONObject != null) {
            new Gson();
            this.zona.azzeracrono();
            int i = 0;
            while (i < 7) {
                try {
                    if (jSONObject.has(String.valueOf(i))) {
                        JSONArray jSONArray = jSONObject.getJSONArray(String.valueOf(i));
                        for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                            this.zona.getCrono().get(i).set(i2, Crono.JsonOffline(jSONArray.getJSONObject(i2)));
                            ((Crono) this.zona.getCrono().get(i).get(i2)).normalizzaOrario();
                        }
                    }
                    i++;
                } catch (Exception e) {
                    Log.d("CronoSummary", e.toString());
                }
            }
            this.adapter = new CronoSummaryAdapter(this, this.cu, this.zona, this.unitm);
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    CronoSummaryActivity.this.lv.setAdapter(CronoSummaryActivity.this.adapter);
                }
            });
            Log.e("prova", jSONObject.toString());
        }
    }

    public void getTW(JSONObject jSONObject) {
        if (jSONObject != null) {
            Gson gson = new Gson();
            this.zona.azzeracrono();
            int i = 0;
            while (i < 7) {
                try {
                    if (jSONObject.has(Constants.JSON_GIORNI[i])) {
                        JSONArray jSONArray = jSONObject.getJSONArray(Constants.JSON_GIORNI[i]);
                        for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                            int i3 = jSONArray.getJSONObject(i2).getInt("Type");
                            this.zona.getCrono().get(i).set(i3, (Crono) gson.fromJson(jSONArray.getJSONObject(i2).toString(), Crono.class));
                            ((Crono) this.zona.getCrono().get(i).get(i3)).normalizzaOrario();
                        }
                    }
                    i++;
                } catch (Exception e) {
                    Log.d("CronoSummary", e.toString());
                }
            }
            this.adapter = new CronoSummaryAdapter(this, this.cu, this.zona, this.unitm);
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    CronoSummaryActivity.this.lv.setAdapter(CronoSummaryActivity.this.adapter);
                }
            });
            Log.e("prova", jSONObject.toString());
        }
    }

    public View getToolBar() {
        return findViewById(R.id.csa_toolbar);
    }

    public String setToolbarTitle() {
        return this.zona.getName();
    }
}
