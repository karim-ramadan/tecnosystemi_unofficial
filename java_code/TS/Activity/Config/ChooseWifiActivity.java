package it.tecnosystemi.TS.Activity.Config;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Adapters.WifiAdapter;
import it.tecnosystemi.TS.Commands.MySocket;
import it.tecnosystemi.TS.Model.WiFi;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChooseWifiActivity extends BaseActivity {
    BaseActivity activity;
    Button btnAggiorna;
    int icon;
    ListView lvWifi;
    int mode;
    String name;
    String pin;
    List<WiFi> wifis;

    public BaseActivity getActivity() {
        return this;
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        return list;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_choose_wifi);
        super.onCreate(bundle);
        hideMenuButton();
        if (!Constants.ISDEMO) {
            Intent intent = getIntent();
            this.pin = intent.getStringExtra(Constants.INTENT_PIN);
            this.name = intent.getStringExtra("name");
            this.mode = intent.getIntExtra(Constants.INTENT_SETUPMODE, -1);
            this.icon = intent.getIntExtra(Constants.INTENT_ICON, 0);
            if (this.mode == -1) {
                finish();
                return;
            }
        }
        this.activity = this;
        setUpGui();
        aggiornaWifi();
    }

    private void setUpGui() {
        this.btnAggiorna = (Button) findViewById(R.id.cwa_btnAggiorna);
        this.lvWifi = (ListView) findViewById(R.id.cwa_lvWifi);
        this.btnAggiorna.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/AvenirNextCondensed_Regular.ttf"));
    }

    public void aggiornaWifi() {
        aggiornaWifi(0);
    }

    public void aggiornaWifi(final int i) {
        this.wifis = new ArrayList();
        if (Constants.ISDEMO) {
            WiFi wiFi = new WiFi();
            wiFi.setSid(getResources().getString(R.string.WIFI1));
            wiFi.setCrip(0);
            this.wifis.add(wiFi);
            WiFi wiFi2 = new WiFi();
            wiFi2.setSid(getResources().getString(R.string.WIFI2));
            wiFi2.setCrip(0);
            this.wifis.add(wiFi2);
            WiFi wiFi3 = new WiFi();
            wiFi3.setSid(getResources().getString(R.string.WIFI3));
            wiFi3.setCrip(0);
            this.wifis.add(wiFi3);
            WiFi wiFi4 = new WiFi();
            wiFi4.setSid(getResources().getString(R.string.WIFI4));
            wiFi4.setCrip(0);
            this.wifis.add(wiFi4);
            this.lvWifi.setAdapter(new WifiAdapter(this, this.wifis, Constants.DEVICE_TYPE_PROAIR));
            return;
        }
        try {
            connectToWifi(new Runnable() {
                public void run() {
                    new Thread(new Runnable() {
                        public void run() {
                            MySocket.initInstance(ChooseWifiActivity.this.activity, ChooseWifiActivity.this.activity, false);
                            ChooseWifiActivity.this.showProgress();
                            final String wifi = MySocket.getWifi(Constants.ip, Constants.port);
                            ChooseWifiActivity.this.hideProgress();
                            ChooseWifiActivity.this.activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    try {
                                        Thread.sleep(2000);
                                    } catch (Exception unused) {
                                    }
                                    ChooseWifiActivity.this.addwifi(wifi, i);
                                }
                            });
                            Log.d(Constants.INTENT_WIFI, wifi);
                        }
                    }).start();
                }
            }, false, true);
        } catch (Exception e) {
            Log.d(com.google.firebase.messaging.Constants.IPC_BUNDLE_KEY_SEND_ERROR, e.toString());
        }
    }

    public void addwifi(String str, int i) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            JSONArray jSONArray = jSONObject.getJSONArray(Constants.JSON_WIFI);
            this.wifis = new ArrayList();
            for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                WiFi wiFi = new WiFi(jSONArray.getJSONObject(i2));
                int i3 = 0;
                while (true) {
                    if (i3 >= this.wifis.size()) {
                        this.wifis.add(wiFi);
                        break;
                    } else if (this.wifis.get(i3).getSid().equals(wiFi.getSid())) {
                        break;
                    } else {
                        i3++;
                    }
                }
            }
            Log.d("addwifi", jSONObject.toString());
            this.lvWifi.setAdapter(new WifiAdapter(this, this.wifis, Constants.DEVICE_TYPE_PROAIR));
        } catch (JSONException e) {
            e.printStackTrace();
            if (i < 5) {
                aggiornaWifi(i + 1);
            }
        }
        hideProgress();
    }

    public void btnAggiorna(View view) {
        aggiornaWifi();
    }

    public void protectedwifi(final WiFi wiFi, boolean z) {
        if (Constants.ISDEMO) {
            Intent intent = new Intent(this.activity, ConfigActivity.class);
            intent.putExtra(Constants.INTENT_WIFI, wiFi);
            intent.putExtra("name", this.name);
            intent.putExtra(Constants.INTENT_SETUPMODE, this.mode);
            intent.putExtra(Constants.INTENT_PIN, this.pin);
            intent.putExtra(Constants.INTENT_ICON, this.icon);
            this.activity.startActivity(intent);
        } else if (!z) {
            Intent intent2 = new Intent(this.activity, ConfigActivity.class);
            intent2.putExtra(Constants.INTENT_WIFI, wiFi);
            intent2.putExtra("name", this.name);
            intent2.putExtra(Constants.INTENT_SETUPMODE, this.mode);
            intent2.putExtra(Constants.INTENT_PIN, this.pin);
            intent2.putExtra(Constants.INTENT_ICON, this.icon);
            this.activity.startActivity(intent2);
        } else {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            arrayList.add("");
            arrayList2.add(new TextView.OnEditorActionListener() {
                public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    if (i != 6) {
                        return false;
                    }
                    Intent intent = new Intent(ChooseWifiActivity.this.activity, ConfigActivity.class);
                    wiFi.setPwd(textView.getText().toString());
                    intent.putExtra(Constants.INTENT_WIFI, wiFi);
                    intent.putExtra("name", ChooseWifiActivity.this.name);
                    intent.putExtra(Constants.INTENT_SETUPMODE, ChooseWifiActivity.this.mode);
                    intent.putExtra(Constants.INTENT_PIN, ChooseWifiActivity.this.pin);
                    intent.putExtra(Constants.INTENT_ICON, ChooseWifiActivity.this.icon);
                    ChooseWifiActivity.this.activity.startActivity(intent);
                    ChooseWifiActivity.this.dismissdialog();
                    return false;
                }
            });
            openDialogFragment(createTxtPopUp(getResources().getString(R.string.c4_ErrorEmptyPwd), arrayList, getResources().getString(R.string.c4_PwdHint), arrayList2));
        }
    }

    public View getToolBar() {
        return findViewById(R.id.cwa_toolbar);
    }

    public String setToolbarTitle() {
        return getResources().getString(R.string.c4_title);
    }
}
