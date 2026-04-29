package it.tecnosystemi.TS.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.preference.PreferenceManager;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import it.tecnosystemi.TS.Activity.Config.ChooseTypeVCActivity;
import it.tecnosystemi.TS.Adapters.HomeLVAdapter;
import it.tecnosystemi.TS.Commands.MySocketBootLoader;
import it.tecnosystemi.TS.Model.CUOperationInfo;
import it.tecnosystemi.TS.Model.ControlUnit;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Threads.ThreadDowloadFirmWare;
import it.tecnosystemi.TS.Threads.ThreadWebService;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.DataClass;
import it.tecnosystemi.TS.Utils.Functions;
import it.tecnosystemi.TS.Utils.SavePreferences;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;

public class HomeActivity extends BaseActivity {
    HomeActivity activity;
    boolean background;
    Button btnAddCu;
    boolean checkedver = false;
    boolean connectingoffline = false;
    boolean errorcollegamento;
    FirebaseInstanceId fbi;
    ListView homeListView;
    JSONArray infofws;
    TextView lblSelect;
    List<ControlUnit> listaCU;
    List<ControlUnit> listaCUServer;
    SavePreferences pref;
    SharedPreferences preferences;

    public BaseActivity getActivity() {
        return this;
    }

    public void startCUActivity(ControlUnit controlUnit, int i) {
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_home);
        MySocketBootLoader.lastFWPK = 0;
        this.activity = this;
        super.onCreate(bundle);
        this.homeListView = (ListView) findViewById(R.id.ha_listView);
        setUpGui();
        if (Constants.ISDEMO) {
            hideMenuButton();
        }
        this.preferences = PreferenceManager.getDefaultSharedPreferences(this);
        Functions.netUnbinding(this);
    }

    public void shownewappinfo() {
        AnonymousClass1 r6 = new Runnable() {
            public void run() {
                HomeActivity.this.dismissdialog();
            }
        };
        openDialogFragment(createYesNoPopUp(getResources().getString(R.string.la_new_app), Constants.LastAppVrTxt, "", getResources().getString(R.string.ba_OK), r6, r6));
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
    }

    public void setListView() {
        if (Constants.ISDEMO) {
            this.listaCU = DataClass.getInstance(this).controlunit_list;
            orderDemo();
            this.homeListView.setAdapter(new HomeLVAdapter(this, this.listaCU, false));
            return;
        }
        this.listaCU = new ArrayList();
        loadHome();
    }

    public void orderDemo() {
        Collections.sort(this.listaCU, new Comparator<ControlUnit>() {
            public int compare(ControlUnit controlUnit, ControlUnit controlUnit2) {
                try {
                    return controlUnit.getName().compareTo(controlUnit2.getName());
                } catch (Exception unused) {
                    return 0;
                }
            }
        });
    }

    public void btnConf(View view) {
        startActivity(new Intent(this, ChooseTypeVCActivity.class));
    }

    public void getHomeFromPref() {
        String string = this.pref.getString(Constants.PREF_UC_HOME);
        if (string != null) {
            try {
                JSONArray jSONArray = new JSONArray(string);
                for (int i = 0; i < jSONArray.length(); i++) {
                    ControlUnit cULocal = ControlUnit.setCULocal(jSONArray.getJSONObject(i));
                    if (!this.listaCU.contains(cULocal)) {
                        this.listaCU.add(cULocal);
                    }
                }
                refreshlist(false);
            } catch (Exception unused) {
            }
        }
    }

    public void getHomeFromServer(String str) {
        try {
            JSONArray jSONArray = new JSONArray(str);
            this.listaCUServer = new ArrayList();
            for (int i = 0; i < jSONArray.length(); i++) {
                this.listaCUServer.add(ControlUnit.setCUServer(jSONArray.getJSONObject(i)));
            }
            for (ControlUnit next : this.listaCUServer) {
                int indexOf = this.listaCU.indexOf(next);
                if (indexOf != -1) {
                    List<ControlUnit> list = this.listaCU;
                    list.set(indexOf, ControlUnit.mergeServerPref(next, list.get(indexOf)));
                } else {
                    this.listaCU.add(next);
                }
            }
            Collections.sort(this.listaCU, new Comparator<ControlUnit>() {
                public int compare(ControlUnit controlUnit, ControlUnit controlUnit2) {
                    try {
                        return controlUnit.getName().compareTo(controlUnit2.getName());
                    } catch (Exception unused) {
                        return 0;
                    }
                }
            });
            refreshlist(false);
            new Thread(new Runnable() {
                public void run() {
                    new JSONArray();
                    for (ControlUnit saveCuInPref : HomeActivity.this.listaCU) {
                        ControlUnit.saveCuInPref(saveCuInPref, HomeActivity.this.activity);
                    }
                }
            }).start();
        } catch (Exception unused) {
        }
    }

    public void loadHome() {
        this.listaCU = new ArrayList();
        HomeActivity homeActivity = this.activity;
        this.pref = new SavePreferences(homeActivity, homeActivity.getString(R.string.PrefsName));
        getHomeFromPref();
        new ThreadWebService(this, 0, 9, getResources().getString(R.string.uriWebService) + getResources().getString(R.string.uri_GetHome), "", (String[]) null).start();
        updatemenu();
    }

    public void cancellacentralina(final ControlUnit controlUnit) {
        this.activity.getSharedPreferences(Constants.PREF_REGID_NAME, 0);
        if (Functions.getNotificationPermision(this.activity)) {
            FirebaseInstanceId.getInstance().getToken();
        }
        refreshlist(false);
        AnonymousClass5 r9 = new Runnable() {
            public void run() {
                HomeActivity.this.dismissdialog();
                if (controlUnit.isOffline()) {
                    ControlUnit.deleteCufromPref(controlUnit.getSerial(), HomeActivity.this.activity);
                    HomeActivity.this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            HomeActivity.this.loadHome();
                        }
                    });
                    return;
                }
                CUOperationInfo cUOperationInfo = new CUOperationInfo();
                cUOperationInfo.setUsrName(Constants.user);
                cUOperationInfo.setCuSerial(controlUnit.getSerial());
                cUOperationInfo.setToken(HomeActivity.this.activity.FirebaseToken);
                cUOperationInfo.setPlatform(Constants.NOTIFIC_PLAT);
                new ThreadWebService(HomeActivity.this.activity, 2, 10, HomeActivity.this.getResources().getString(R.string.uriWebService) + HomeActivity.this.getResources().getString(R.string.uri_DeleteUserFromCu), new Gson().toJson((Object) cUOperationInfo), new String[]{controlUnit.getSerial()}).start();
            }
        };
        openDialogFragment(createYesNoPopUp(getResources().getString(R.string.cu_deleteCUAlert_title), getResources().getString(R.string.cu_deleteCUAlert_text), getResources().getString(R.string.no), getResources().getString(R.string.yes), new Runnable() {
            public void run() {
                HomeActivity.this.dismissdialog();
            }
        }, r9));
    }

    public void connectcentralina(ControlUnit controlUnit, int i) {
        startCUActivity(controlUnit, i);
    }

    public void refreshlist(boolean z) {
        this.homeListView.setAdapter(new HomeLVAdapter(this, this.listaCU, z));
        updatemenu();
    }

    public void onResume() {
        super.onResume();
        try {
            if (!this.connectingoffline) {
                unbidNetwork();
            } else {
                this.connectingoffline = false;
            }
        } catch (Exception unused) {
        }
        setListView();
        this.background = false;
        if (this.errorcollegamento) {
            this.errorcollegamento = false;
            Functions.makeErrorToast(this, getResources().getString(R.string.ba_apAssente));
        }
        if (!this.checkedver) {
            this.checkedver = true;
            try {
                String str = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                if (Constants.LastAppVrTxt != null && !Constants.LastAppVrTxt.isEmpty() && Functions.compareVerString(str, Constants.LastAppVr) < 0) {
                    shownewappinfo();
                }
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        getInfoFw();
    }

    public void onPause() {
        super.onPause();
        this.background = true;
    }

    /* access modifiers changed from: protected */
    public void onDestroy() {
        triedonce = false;
        if (this.cercacentralina != null) {
            this.cercacentralina.interrupt();
        }
        super.onDestroy();
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
                        HomeActivity.this.dismissdialog();
                    } catch (Exception unused) {
                    }
                    HomeActivity.this.gotobooloader = false;
                    HomeActivity homeActivity = HomeActivity.this.activity;
                    new ThreadDowloadFirmWare(homeActivity, HomeActivity.this.getResources().getString(R.string.uriWebService) + HomeActivity.this.getResources().getString(R.string.uri_infoFrameWork)).start();
                }
            }, false, false));
            AnonymousClass8 r7 = new Runnable() {
                public void run() {
                    try {
                        HomeActivity.this.dismissdialog();
                    } catch (Exception unused) {
                    }
                    HomeActivity.this.gotobooloader = true;
                    HomeActivity homeActivity = HomeActivity.this.activity;
                    new ThreadDowloadFirmWare(homeActivity, HomeActivity.this.getResources().getString(R.string.uriWebService) + HomeActivity.this.getResources().getString(R.string.uri_infoFrameWork)).start();
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
            list.add(createMenuItem(false, getResources().getString(R.string.ha_cancellaAccount), "", "", new Runnable() {
                public void run() {
                    if (HomeActivity.this.myDialogFragment != null) {
                        HomeActivity.this.dismissdialog();
                    }
                    HomeActivity.this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            HomeActivity.this.createDeleteUserPopUp();
                        }
                    });
                }
            }, false, false));
            AnonymousClass10 r6 = new Runnable() {
                public void run() {
                    if (HomeActivity.this.myDialogFragment != null) {
                        HomeActivity.this.dismissdialog();
                    }
                    HomeActivity.this.activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Functions.makeNormalToast(HomeActivity.this.activity, HomeActivity.this.getResources().getString(R.string.ha_cancellaCentralinaToast));
                            HomeActivity.this.refreshlist(true);
                        }
                    });
                    HomeActivity.this.updatemenu();
                }
            };
            List<ControlUnit> list2 = this.listaCU;
            if (list2 != null && list2.size() > 0) {
                list.add(createMenuItem(false, getResources().getString(R.string.ha_cancellaCentralina), "", "", r6, false, false));
            }
        }
        return list;
    }

    public void createDeleteUserPopUp() {
        AnonymousClass11 r5 = new Runnable() {
            public void run() {
                HomeActivity.this.dismissdialog();
            }
        };
        AnonymousClass12 r6 = new Runnable() {
            public void run() {
                HomeActivity.this.dismissdialog();
                HomeActivity.this.showProgress();
                HomeActivity homeActivity = HomeActivity.this.activity;
                new ThreadWebService(homeActivity, 2, 20, HomeActivity.this.getResources().getString(R.string.uriWebService) + HomeActivity.this.getResources().getString(R.string.uri_DeleteUser), (String) null, (String[]) null).start();
            }
        };
        String string = getResources().getString(R.string.cu_deleteAccountAlert_text);
        openDialogFragment(createYesNoPopUp(getResources().getString(R.string.cu_deleteAccountAlert_title), string, getResources().getString(R.string.ba_cancel), getResources().getString(R.string.general_OK), r5, r6));
    }

    public String setToolbarTitle() {
        return "";
    }
}
