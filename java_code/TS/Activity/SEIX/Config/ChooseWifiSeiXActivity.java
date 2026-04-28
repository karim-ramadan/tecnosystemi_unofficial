package it.tecnosystemi.TS.Activity.SEIX.Config;

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
import it.tecnosystemi.TS.Commands.CmdPICO;
import it.tecnosystemi.TS.Commands.MySocket;
import it.tecnosystemi.TS.Commands.Protocols;
import it.tecnosystemi.TS.Commands.UDPSocket;
import it.tecnosystemi.TS.Model.WiFi;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

public class ChooseWifiSeiXActivity extends BaseActivity {
    BaseActivity activity;
    Button btnAggiorna;
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
        setContentView(R.layout.activity_choose_wifi_seix_activity);
        this.activity = this;
        super.onCreate(bundle);
        hideMenuButton();
        Intent intent = getIntent();
        this.mode = intent.getIntExtra(Constants.INTENT_SETUPMODE, -1);
        if (!Constants.ISDEMO) {
            this.pin = intent.getStringExtra(Constants.INTENT_PIN);
            this.name = intent.getStringExtra("name");
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
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
        this.btnAggiorna.setTypeface(createFromAsset);
        TextView textView = (TextView) findViewById(R.id.lblIstr);
        textView.setTypeface(createFromAsset);
        if (this.mode == 5) {
            textView.setText(R.string.c4_lblInstructionPicoSlave);
        }
    }

    public void aggiornaWifi() {
        this.wifis = new ArrayList();
        if (Constants.ISDEMO) {
            if (this.mode == 5) {
                WiFi wiFi = new WiFi();
                wiFi.setSid(getResources().getString(R.string.PICOMASTER1));
                wiFi.setCrip(0);
                this.wifis.add(wiFi);
                WiFi wiFi2 = new WiFi();
                wiFi2.setSid(getResources().getString(R.string.PICOMASTER2));
                wiFi2.setCrip(0);
                this.wifis.add(wiFi2);
            } else {
                WiFi wiFi3 = new WiFi();
                wiFi3.setSid(getResources().getString(R.string.WIFI1));
                wiFi3.setCrip(0);
                this.wifis.add(wiFi3);
                WiFi wiFi4 = new WiFi();
                wiFi4.setSid(getResources().getString(R.string.WIFI2));
                wiFi4.setCrip(0);
                this.wifis.add(wiFi4);
                WiFi wiFi5 = new WiFi();
                wiFi5.setSid(getResources().getString(R.string.WIFI3));
                wiFi5.setCrip(0);
                this.wifis.add(wiFi5);
                WiFi wiFi6 = new WiFi();
                wiFi6.setSid(getResources().getString(R.string.WIFI4));
                wiFi6.setCrip(0);
                this.wifis.add(wiFi6);
            }
            this.lvWifi.setAdapter(new WifiAdapter(this, this.wifis, Constants.DEVICE_TYPE_6X));
            return;
        }
        try {
            connectToWifi(new Runnable() {
                public void run() {
                    new Thread(new Runnable() {
                        public void run() {
                            new Runnable() {
                                public void run() {
                                }
                            };
                            MySocket.initInstance(ChooseWifiSeiXActivity.this.activity, ChooseWifiSeiXActivity.this.activity, false);
                            ChooseWifiSeiXActivity.this.showProgress();
                            CmdPICO cmdPICO = new CmdPICO();
                            cmdPICO.setPin(ChooseWifiSeiXActivity.this.pin);
                            cmdPICO.setCmd(Protocols.CMD_GET_WIFI);
                            UDPSocket.startListening();
                            final String sendCMD = UDPSocket.sendCMD(cmdPICO, 10000, 1500);
                            ChooseWifiSeiXActivity.this.hideProgress();
                            ChooseWifiSeiXActivity.this.activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    ChooseWifiSeiXActivity.this.addwifi(sendCMD);
                                }
                            });
                        }
                    }).start();
                }
            }, false, true);
        } catch (Exception e) {
            Log.d(com.google.firebase.messaging.Constants.IPC_BUNDLE_KEY_SEND_ERROR, e.toString());
        }
    }

    public void addwifi(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            JSONArray jSONArray = jSONObject.getJSONArray(Constants.JSON_WIFI);
            this.wifis = new ArrayList();
            for (int i = 0; i < jSONArray.length(); i++) {
                WiFi wiFi = new WiFi(jSONArray.getJSONObject(i));
                int i2 = 0;
                while (true) {
                    if (i2 >= this.wifis.size()) {
                        this.wifis.add(wiFi);
                        break;
                    } else if (this.wifis.get(i2).getSid().equals(wiFi.getSid())) {
                        break;
                    } else {
                        i2++;
                    }
                }
            }
            Log.d("addwifi", jSONObject.toString());
            this.lvWifi.setAdapter(new WifiAdapter(this, this.wifis, Constants.DEVICE_TYPE_6X));
        } catch (Exception e) {
            e.printStackTrace();
        }
        hideProgress();
    }

    public void protectedwifi(final WiFi wiFi, boolean z) {
        if (Constants.ISDEMO) {
            Intent intent = new Intent(this.activity, ConfigSeiXActivity.class);
            intent.putExtra(Constants.INTENT_WIFI, wiFi);
            intent.putExtra("name", this.name);
            intent.putExtra(Constants.INTENT_SETUPMODE, this.mode);
            intent.putExtra(Constants.INTENT_PIN, this.pin);
            this.activity.startActivity(intent);
        } else if (!z) {
            Intent intent2 = new Intent(this.activity, ConfigSeiXActivity.class);
            intent2.putExtra(Constants.INTENT_WIFI, wiFi);
            intent2.putExtra("name", this.name);
            intent2.putExtra(Constants.INTENT_SETUPMODE, this.mode);
            intent2.putExtra(Constants.INTENT_PIN, this.pin);
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
                    Intent intent = new Intent(ChooseWifiSeiXActivity.this.activity, ConfigSeiXActivity.class);
                    wiFi.setPwd(textView.getText().toString());
                    intent.putExtra(Constants.INTENT_WIFI, wiFi);
                    intent.putExtra("name", ChooseWifiSeiXActivity.this.name);
                    intent.putExtra(Constants.INTENT_SETUPMODE, ChooseWifiSeiXActivity.this.mode);
                    intent.putExtra(Constants.INTENT_PIN, ChooseWifiSeiXActivity.this.pin);
                    ChooseWifiSeiXActivity.this.activity.startActivity(intent);
                    ChooseWifiSeiXActivity.this.dismissdialog();
                    return false;
                }
            });
            openDialogFragment(createTxtPopUp(getResources().getString(R.string.c4_ErrorEmptyPwd), arrayList, getResources().getString(R.string.c4_PwdHint), arrayList2));
        }
    }

    public void btnAggiorna(View view) {
        aggiornaWifi();
    }

    public View getToolBar() {
        return findViewById(R.id.cwa_toolbar);
    }

    public String setToolbarTitle() {
        return getResources().getString(R.string.c4_title);
    }
}
