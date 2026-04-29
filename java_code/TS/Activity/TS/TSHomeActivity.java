package it.tecnosystemi.TS.Activity.TS;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Activity.GDPRActivity;
import it.tecnosystemi.TS.Activity.PICO.PicoBootloaderActivity;
import it.tecnosystemi.TS.Activity.SEIX.SeiXBootloaderActivity;
import it.tecnosystemi.TS.Activity.SignUpActivity;
import it.tecnosystemi.TS.Activity.VMC.VMCBootloaderActivity;
import it.tecnosystemi.TS.Adapters.HomeLVAdapterTS;
import it.tecnosystemi.TS.Commands.MySocketBootLoader;
import it.tecnosystemi.TS.Model.Plant;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Threads.ThreadDowloadFirmWare;
import it.tecnosystemi.TS.Threads.ThreadWebService;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import it.tecnosystemi.TS.Utils.SavePreferences;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TSHomeActivity extends BaseActivity {
    TSHomeActivity activity;
    boolean background;
    Button btnAddCu;
    HomeLVAdapterTS cuAdapter;
    boolean errorcollegamento;
    boolean first_getHome;
    ListView homeListView;
    JSONArray infofws;
    TextView lblSelect;
    SavePreferences pref;
    SharedPreferences preferences;

    public BaseActivity getActivity() {
        return this;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        try {
            setContentView(R.layout.activity_tshome);
            MySocketBootLoader.lastFWPK = 0;
            this.activity = this;
            this.typeActStyle = 1;
            super.onCreate(bundle);
            this.first_getHome = true;
            if (getIntent().getBooleanExtra("ForceUPD", false)) {
                this.first_getHome = false;
            }
            if (Constants.listaImpianti == null) {
                this.first_getHome = false;
            } else if (Constants.listaImpianti.size() == 0) {
                this.first_getHome = false;
            }
            this.homeListView = (ListView) findViewById(R.id.ha_listView);
            setUpGui();
            if (Constants.ISDEMO) {
                hideMenuButton();
            }
            this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
            Functions.netUnbinding(this);
        } catch (Exception unused) {
            finish();
        }
    }

    public void getInfoFw() {
        try {
            if (this.preferences == null) {
                this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
            }
            this.infofws = new JSONArray(this.preferences.getString(Constants.PREF_INFOFWS, "[]"));
            int i = 0;
            while (i < this.infofws.length()) {
                try {
                    if (!this.infofws.getJSONObject(i).getString(Constants.JSON_VERSION).equals(Constants.LastFWVr)) {
                        i++;
                    } else {
                        return;
                    }
                } catch (Exception unused) {
                }
            }
        } catch (Exception unused2) {
        }
    }

    private void setUpGui() {
        this.btnAddCu = (Button) findViewById(R.id.ha_btnAggCentralina);
        this.lblSelect = (TextView) findViewById(R.id.ha_txtSeleziona);
        Typeface createFromAsset = Typeface.createFromAsset(getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
        this.btnAddCu.setTypeface(createFromAsset);
        this.lblSelect.setTypeface(createFromAsset);
        HomeLVAdapterTS homeLVAdapterTS = new HomeLVAdapterTS(this.activity, new ArrayList(Constants.listaImpianti), false);
        this.cuAdapter = homeLVAdapterTS;
        this.homeListView.setAdapter(homeLVAdapterTS);
    }

    public void createDeleteUserPopUp() {
        AnonymousClass1 r5 = new Runnable() {
            public void run() {
                TSHomeActivity.this.dismissdialog();
            }
        };
        AnonymousClass2 r6 = new Runnable() {
            public void run() {
                TSHomeActivity.this.dismissdialog();
                TSHomeActivity.this.showProgress();
                TSHomeActivity tSHomeActivity = TSHomeActivity.this.activity;
                new ThreadWebService(tSHomeActivity, 2, 20, TSHomeActivity.this.getResources().getString(R.string.uriWebService) + TSHomeActivity.this.getResources().getString(R.string.uri_DeleteUser), (String) null, (String[]) null).start();
            }
        };
        String string = getResources().getString(R.string.cu_deleteAccountAlert_text);
        openDialogFragment(createYesNoPopUp(getResources().getString(R.string.cu_deleteAccountAlert_title), string, getResources().getString(R.string.ba_cancel), getResources().getString(R.string.general_OK), r5, r6));
    }

    public void cancellaplant(final Plant plant) {
        refreshlist(false);
        AnonymousClass3 r7 = new Runnable() {
            public void run() {
                TSHomeActivity.this.dismissdialog();
                String str = TSHomeActivity.this.getResources().getString(R.string.uriWebService) + TSHomeActivity.this.getResources().getString(R.string.uri_DeletePlant);
                try {
                    JSONObject jSONObject = new JSONObject();
                    jSONObject.put("plantID", plant.getLVPL_Id());
                    jSONObject.put(Constants.PREF_TOKEN, TSHomeActivity.this.activity.FirebaseToken);
                    jSONObject.put("platform", Constants.NOTIFIC_PLAT);
                    new ThreadWebService(TSHomeActivity.this.activity, 2, 10, str, jSONObject.toString(), (String[]) null).start();
                } catch (Exception unused) {
                }
            }
        };
        openDialogFragment(createYesNoPopUp(getResources().getString(R.string.ha_deleteImpiantoAlert_title), getResources().getString(R.string.ha_deleteImpiantoAlert_text), getResources().getString(R.string.no), getResources().getString(R.string.yes), new Runnable() {
            public void run() {
                TSHomeActivity.this.dismissdialog();
            }
        }, r7));
    }

    public void connectplant(Plant plant, int i) {
        Intent intent = new Intent(this, TSDeviceListActivity.class);
        TSDeviceListActivity.idSelected = plant.getLVPL_Id();
        intent.putExtra(Constants.INP_INDEX, i);
        startActivity(intent);
    }

    public void btnConf(View view) {
        startActivity(new Intent(this, AddUpdPlantActivity.class));
    }

    public void refreshlist(final boolean z) {
        runOnUiThread(new Runnable() {
            public void run() {
                TSHomeActivity.this.cuAdapter.changeCancella(z);
                TSHomeActivity.this.cuAdapter.changeDataSet(new ArrayList(Constants.listaImpianti));
                TSHomeActivity.this.updatemenu();
            }
        });
    }

    public void loadHome() {
        showProgress();
        new ThreadWebService(this.activity, 0, 23, getResources().getString(R.string.uriWebService) + getResources().getString(R.string.uri_GetPlants), "", (String[]) null).start();
    }

    public void onResume() {
        super.onResume();
        if (Constants.ISDEMO) {
            refreshlist(false);
            return;
        }
        if (this.first_getHome) {
            this.first_getHome = false;
            refreshlist(false);
        } else {
            loadHome();
        }
        this.background = false;
        getInfoFw();
        try {
            if (!Constants.CHECKED_TS_VER) {
                Constants.CHECKED_TS_VER = true;
                String str = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                if (Constants.LastAppVrTxt != null && !Constants.LastAppVrTxt.isEmpty() && Functions.compareVerString(str, Constants.LastAppVr) < 0) {
                    shownewappinfo();
                }
            }
        } catch (Exception unused) {
        }
    }

    public void shownewappinfo() {
        AnonymousClass6 r6 = new Runnable() {
            public void run() {
                TSHomeActivity.this.dismissdialog();
            }
        };
        openDialogFragment(createYesNoPopUp(getResources().getString(R.string.la_new_app), Constants.LastAppVrTxt, "", getResources().getString(R.string.ba_OK), r6, r6));
    }

    public View getToolBar() {
        return findViewById(R.id.ha_toolbar);
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        JSONArray jSONArray;
        getInfoFw();
        if (!Constants.ISDEMO) {
            list.add(createMenuItem(true, getResources().getString(R.string.ha_menuVerificaFW), "", "", new Runnable() {
                public void run() {
                    try {
                        TSHomeActivity.this.dismissdialog();
                    } catch (Exception unused) {
                    }
                    TSHomeActivity.this.gotobooloader = false;
                    TSHomeActivity tSHomeActivity = TSHomeActivity.this.activity;
                    new ThreadDowloadFirmWare(tSHomeActivity, TSHomeActivity.this.getResources().getString(R.string.uriWebService) + TSHomeActivity.this.getResources().getString(R.string.uri_infoFrameWork)).start();
                }
            }, false, false));
            AnonymousClass8 r7 = new Runnable() {
                public void run() {
                    try {
                        TSHomeActivity.this.dismissdialog();
                    } catch (Exception unused) {
                    }
                    TSHomeActivity.this.gotobooloader = true;
                    PicoBootloaderActivity.CLASSTOCALL = TSHomeActivity.class;
                    VMCBootloaderActivity.CLASSTOCALL = TSHomeActivity.class;
                    SeiXBootloaderActivity.CLASSTOCALL = TSHomeActivity.class;
                    TSHomeActivity tSHomeActivity = TSHomeActivity.this.activity;
                    new ThreadDowloadFirmWare(tSHomeActivity, TSHomeActivity.this.getResources().getString(R.string.uriWebService) + TSHomeActivity.this.getResources().getString(R.string.uri_infoFrameWork)).start();
                }
            };
            try {
                jSONArray = new JSONArray(PreferenceManager.getDefaultSharedPreferences(this).getString(Constants.PREF_INFOFWS, "[]"));
            } catch (JSONException e) {
                try {
                    jSONArray = new JSONArray("[]");
                } catch (JSONException e2) {
                    e2.printStackTrace();
                    jSONArray = null;
                }
                e.printStackTrace();
            }
            if (jSONArray.length() > 0) {
                list.add(createMenuItem(false, getResources().getString(R.string.ha_menuUpdateFW), "", "", r7, false, false));
            }
            AnonymousClass9 r6 = new Runnable() {
                public void run() {
                    if (TSHomeActivity.this.myDialogFragment != null) {
                        TSHomeActivity.this.dismissdialog();
                    }
                    TSHomeActivity.this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Functions.makeNormalToast(TSHomeActivity.this.activity, TSHomeActivity.this.getResources().getString(R.string.ha_cancellaCentralinaToast));
                            TSHomeActivity.this.refreshlist(true);
                        }
                    });
                    TSHomeActivity.this.updatemenu();
                }
            };
            if (Constants.listaImpianti != null && Constants.listaImpianti.size() > 0) {
                list.add(createMenuItem(false, getResources().getString(R.string.ha_cancellaCentralina), "", "", r6, false, false));
            }
            list.add(createMenuItem(false, getResources().getString(R.string.ha_cancellaAccount), "", "", new Runnable() {
                public void run() {
                    if (TSHomeActivity.this.myDialogFragment != null) {
                        TSHomeActivity.this.dismissdialog();
                    }
                    TSHomeActivity.this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            TSHomeActivity.this.createDeleteUserPopUp();
                        }
                    });
                }
            }, false, false));
            list.add(createMenuItem(false, getResources().getString(R.string.GDPR_Menu), "", "", new Runnable() {
                public void run() {
                    TSHomeActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            SignUpActivity.PRIVACY = true;
                            SignUpActivity.TOU = true;
                            Intent intent = new Intent(TSHomeActivity.this.activity, GDPRActivity.class);
                            intent.putExtra(Constants.GDPRFROMLOGIN, true);
                            intent.putExtra(Constants.GDPRFROMHOME, true);
                            intent.putExtra(Constants.GDprUSERNAME, Constants.user);
                            TSHomeActivity.this.startActivity(intent);
                        }
                    });
                }
            }, false, false));
        }
        return list;
    }

    public String setToolbarTitle() {
        return "";
    }
}
