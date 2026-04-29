package it.tecnosystemi.TS.Activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.internal.view.SupportMenu;
import androidx.recyclerview.widget.ItemTouchHelper;
import it.tecnosystemi.TS.Adapters.SpinnerAdapter;
import it.tecnosystemi.TS.Commands.MySocketBootLoader;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.DataClass;
import it.tecnosystemi.TS.Utils.Functions;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONException;

public class BootloaderActivity extends BaseActivity {
    public static boolean FROMCONFIG = false;
    public static boolean MANUALCONN = false;
    public static BootloaderActivity activity;
    boolean background;
    int barprogress;
    byte[] bbootver;
    Button btnAgg;
    boolean disable;
    boolean errorcollegamento;
    boolean firsttime = true;
    String fwver;
    JSONArray infofwToShow;
    JSONArray infofwsAll;
    boolean interrupted;
    boolean lasteraseflash;
    TextView lblChangeLog;
    TextView lblDescr;
    TextView lblselectFw;
    int major4x = 1;
    int minor4x = 0;
    int mode;
    String musterasemax;
    String musterasemin;
    int nore = 0;
    int polaristype;
    SharedPreferences preferences;
    ProgressBar progressBar;
    int release4x = 7;
    int retriesKO = 0;
    int retriesconnect = 0;
    Spinner spn_fw;
    int times = 0;
    int tries = 0;
    int trieshw = 0;
    PowerManager.WakeLock wl;

    public BaseActivity getActivity() {
        return this;
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        return list;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Can't wrap try/catch for region: R(12:0|1|2|(1:4)|5|6|7|8|11|(2:13|(1:15))|16|17) */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x0028 */
    /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x003f */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x00cd  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(android.os.Bundle r6) {
        /*
            r5 = this;
            java.lang.String r0 = "[]"
            int r1 = it.tecnosystemi.TS.R.layout.activity_bootloader
            r5.setContentView((int) r1)
            super.onCreate(r6)
            bootloaderActivity = r5
            r6 = 0
            r5.errorcollegamento = r6
            it.tecnosystemi.TS.Commands.MySocketBootLoader.lastFWPK = r6
            android.content.SharedPreferences r1 = androidx.preference.PreferenceManager.getDefaultSharedPreferences(r5)
            r5.preferences = r1
            r1 = -1
            boolean r2 = FROMCONFIG     // Catch:{ Exception -> 0x0028 }
            if (r2 == 0) goto L_0x0028
            android.content.Intent r2 = r5.getIntent()     // Catch:{ Exception -> 0x0028 }
            java.lang.String r3 = "setupmode"
            int r2 = r2.getIntExtra(r3, r1)     // Catch:{ Exception -> 0x0028 }
            r5.mode = r2     // Catch:{ Exception -> 0x0028 }
        L_0x0028:
            org.json.JSONArray r2 = new org.json.JSONArray     // Catch:{ Exception -> 0x003f }
            r2.<init>()     // Catch:{ Exception -> 0x003f }
            r5.infofwToShow = r2     // Catch:{ Exception -> 0x003f }
            org.json.JSONArray r2 = new org.json.JSONArray     // Catch:{ Exception -> 0x003f }
            android.content.SharedPreferences r3 = r5.preferences     // Catch:{ Exception -> 0x003f }
            java.lang.String r4 = "infofw"
            java.lang.String r3 = r3.getString(r4, r0)     // Catch:{ Exception -> 0x003f }
            r2.<init>(r3)     // Catch:{ Exception -> 0x003f }
            r5.infofwsAll = r2     // Catch:{ Exception -> 0x003f }
            goto L_0x004b
        L_0x003f:
            org.json.JSONArray r2 = new org.json.JSONArray     // Catch:{ JSONException -> 0x0047 }
            r2.<init>(r0)     // Catch:{ JSONException -> 0x0047 }
            r5.infofwsAll = r2     // Catch:{ JSONException -> 0x0047 }
            goto L_0x004b
        L_0x0047:
            r0 = move-exception
            r0.printStackTrace()
        L_0x004b:
            r5.interrupted = r6
            android.content.res.AssetManager r0 = r5.getAssets()
            java.lang.String r2 = "fonts/AvenirNextCondensed_Regular.ttf"
            android.graphics.Typeface r0 = android.graphics.Typeface.createFromAsset(r0, r2)
            android.content.res.AssetManager r2 = r5.getAssets()
            java.lang.String r3 = "fonts/fontawesome.ttf"
            android.graphics.Typeface r2 = android.graphics.Typeface.createFromAsset(r2, r3)
            int r3 = it.tecnosystemi.TS.R.id.ba_spn_fw
            android.view.View r3 = r5.findViewById(r3)
            android.widget.Spinner r3 = (android.widget.Spinner) r3
            r5.spn_fw = r3
            activity = r5
            int r3 = it.tecnosystemi.TS.R.id.ba_prgFW
            android.view.View r3 = r5.findViewById(r3)
            android.widget.ProgressBar r3 = (android.widget.ProgressBar) r3
            r5.progressBar = r3
            r4 = 8
            r3.setVisibility(r4)
            android.widget.ProgressBar r3 = r5.progressBar
            r3.setProgress(r1)
            android.widget.ProgressBar r1 = r5.progressBar
            r1.getProgress()
            int r1 = it.tecnosystemi.TS.R.id.ba_lblDescrizione
            android.view.View r1 = r5.findViewById(r1)
            android.widget.TextView r1 = (android.widget.TextView) r1
            r5.lblDescr = r1
            r1.setVisibility(r4)
            android.widget.TextView r1 = r5.lblDescr
            r1.setTypeface(r0)
            int r1 = it.tecnosystemi.TS.R.id.ba_lblFreccia
            android.view.View r1 = r5.findViewById(r1)
            android.widget.TextView r1 = (android.widget.TextView) r1
            r1.setTypeface(r2)
            int r1 = it.tecnosystemi.TS.R.id.ba_lblSelectFW
            android.view.View r1 = r5.findViewById(r1)
            android.widget.TextView r1 = (android.widget.TextView) r1
            r5.lblselectFw = r1
            r1.setTypeface(r0)
            int r1 = it.tecnosystemi.TS.R.id.ba_lblChangeLog
            android.view.View r1 = r5.findViewById(r1)
            android.widget.TextView r1 = (android.widget.TextView) r1
            r5.lblChangeLog = r1
            int r1 = it.tecnosystemi.TS.R.id.ba_btnAggiorna
            android.view.View r1 = r5.findViewById(r1)
            android.widget.Button r1 = (android.widget.Button) r1
            r5.btnAgg = r1
            r1.setTypeface(r0)
            r5.disable = r6
            boolean r0 = it.tecnosystemi.TS.Utils.Constants.modesviluppatoreBootloader
            if (r0 == 0) goto L_0x00e8
            int r0 = it.tecnosystemi.TS.R.id.ba_swtCancellaFlash
            android.view.View r0 = r5.findViewById(r0)
            r0.setVisibility(r6)
            java.lang.String r6 = "android.permission.READ_EXTERNAL_STORAGE"
            int r0 = androidx.core.app.ActivityCompat.checkSelfPermission(r5, r6)
            if (r0 != 0) goto L_0x00df
            goto L_0x00e8
        L_0x00df:
            java.lang.String[] r6 = new java.lang.String[]{r6}
            r0 = 200(0xc8, float:2.8E-43)
            androidx.core.app.ActivityCompat.requestPermissions(r5, r6, r0)
        L_0x00e8:
            r5.hideMenuButton()
            java.lang.String r6 = ""
            r5.musterasemin = r6
            java.lang.String r6 = "power"
            java.lang.Object r6 = r5.getSystemService(r6)
            android.os.PowerManager r6 = (android.os.PowerManager) r6
            r0 = 6
            java.lang.String r1 = "proAir:wakeloktag"
            android.os.PowerManager$WakeLock r6 = r6.newWakeLock(r0, r1)
            r5.wl = r6
            r6.acquire()
            r5.getHW()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.BootloaderActivity.onCreate(android.os.Bundle):void");
    }

    private void getHW() {
        new Thread(new Runnable() {
            public void run() {
                BootloaderActivity.this.showProgress();
                try {
                    Thread.sleep(5000);
                } catch (Exception unused) {
                }
                byte[] hwVer = MySocketBootLoader.getHwVer(BootloaderActivity.activity);
                if (hwVer != null && hwVer.length > 0 && hwVer[0] == 65) {
                    try {
                        Thread.sleep(5000);
                    } catch (Exception unused2) {
                    }
                    hwVer = MySocketBootLoader.getHwVer(BootloaderActivity.activity);
                }
                if (hwVer == null) {
                    BootloaderActivity.this.gotoWIfiAndEnd();
                } else if (hwVer.length < 2) {
                    BootloaderActivity.this.gotoWIfiAndEnd();
                } else {
                    BootloaderActivity.this.hideProgress();
                    BootloaderActivity.this.trieshw = 0;
                    if (hwVer.length >= 6) {
                        BootloaderActivity.this.polaristype = -1;
                        BootloaderActivity.this.polaristype = ((hwVer[4] & 255) << 8) | (hwVer[3] & 255);
                        final byte b = ((hwVer[6] & 255) << 8) | (hwVer[5] & 255);
                        BootloaderActivity.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                BootloaderActivity.this.setSpinner(b);
                            }
                        });
                    } else if (hwVer[0] == 42 && hwVer[1] == 5) {
                        BootloaderActivity.this.polaristype = -1;
                        BootloaderActivity.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                BootloaderActivity.this.setSpinner(-1);
                            }
                        });
                    } else {
                        BootloaderActivity.this.gotoWIfiAndEnd();
                    }
                }
            }
        }).start();
    }

    public void gotoWIfiAndEnd() {
        if (this.trieshw >= 5) {
            runOnUiThread(new Runnable() {
                public void run() {
                    String str = ((BootloaderActivity.this.getResources().getString(R.string.ba_apAssente) + "\n" + BootloaderActivity.this.getResources().getString(R.string.connectToPolaris)) + "\nSSID: POLARIS_UPDATE") + "\n" + BootloaderActivity.this.getResources().getString(R.string.c4_PwdHint) + ": TS123456";
                    AlertDialog.Builder builder = new AlertDialog.Builder(BootloaderActivity.activity);
                    builder.setMessage(str).setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            try {
                                Intent intent = new Intent("android.settings.WIFI_SETTINGS");
                                intent.addFlags(268435456);
                                BootloaderActivity.activity.startActivity(intent);
                            } catch (Exception unused) {
                            }
                            BootloaderActivity.this.finish();
                        }
                    });
                    AlertDialog create = builder.create();
                    create.show();
                    create.getButton(-1).setTextColor(BootloaderActivity.this.getResources().getColor(R.color.colorPrimary));
                }
            });
        } else if (this.firsttime) {
            getHW();
        } else {
            this.firsttime = false;
            connectToWifi((Runnable) null, (Runnable) null, false, true);
            try {
                Thread.sleep(1000);
            } catch (Exception unused) {
            }
            getHW();
        }
    }

    public void prestart() {
        enableView();
        this.lblDescr.setVisibility(8);
        this.progressBar.setVisibility(8);
    }

    public void setSpinner(int i) {
        ArrayList arrayList = new ArrayList();
        if (Constants.modesviluppatoreBootloader) {
            arrayList.add("Download/polaris.s19");
        } else {
            int i2 = 0;
            while (i2 < this.infofwsAll.length()) {
                try {
                    if (!this.infofwsAll.getJSONObject(i2).has(Constants.JSON_LVDV_Type) || this.infofwsAll.getJSONObject(i2).getInt(Constants.JSON_LVDV_Type) == 0) {
                        if ((this.infofwsAll.getJSONObject(i2).getLong(Constants.JSON_MICROTYPE) == ((long) i) || (i < 0 && (this.infofwsAll.getJSONObject(i2).getLong(Constants.JSON_MICROTYPE) == 0 || this.infofwsAll.getJSONObject(i2).getLong(Constants.JSON_MICROTYPE) == 1090))) && (this.polaristype < 0 || this.infofwsAll.getJSONObject(i2).getLong(Constants.JSON_POLARISTYPE) == ((long) this.polaristype))) {
                            this.infofwToShow.put(this.infofwsAll.getJSONObject(i2));
                            arrayList.add(this.infofwsAll.getJSONObject(i2).getString(Constants.JSON_VERSION));
                        }
                        i2++;
                    } else {
                        i2++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        this.spn_fw.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onNothingSelected(AdapterView<?> adapterView) {
            }

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long j) {
                try {
                    String string = BootloaderActivity.this.infofwToShow.getJSONObject(i).getString("ChangeLogIt");
                    String string2 = BootloaderActivity.this.infofwToShow.getJSONObject(i).getString("ChangeLogEn");
                    if (string2.toUpperCase().equals("NULL") && string.toUpperCase().equals("NULL")) {
                        BootloaderActivity.this.lblChangeLog.setText("");
                    } else if (string2.toUpperCase().equals("NULL")) {
                        BootloaderActivity.this.lblChangeLog.setText(string);
                    } else if (string.toUpperCase().equals("NULL")) {
                        BootloaderActivity.this.lblChangeLog.setText(string2);
                    } else if (Locale.getDefault().getLanguage().toLowerCase().equals("it")) {
                        BootloaderActivity.this.lblChangeLog.setText(string);
                    } else {
                        BootloaderActivity.this.lblChangeLog.setText(string2);
                    }
                } catch (Exception unused) {
                }
            }
        });
        this.spn_fw.setAdapter(new SpinnerAdapter(this, R.layout.spinner_item, arrayList, false));
    }

    public void startControlloVersione() {
        try {
            this.progressBar.setIndeterminate(false);
        } catch (Exception unused) {
        }
    }

    public void setProgressBar(final int i) {
        try {
            bootloaderActivity.runOnUiThread(new Runnable() {
                public void run() {
                    BootloaderActivity.this.progressBar.setProgress(i);
                    TextView textView = BootloaderActivity.this.lblDescr;
                    textView.setText(BootloaderActivity.activity.getResources().getString(R.string.ba_aggiornamento) + ": " + i + "%");
                }
            });
            Log.d("TAG", "" + i);
        } catch (Exception unused) {
        }
    }

    public void startWrite() {
        try {
            this.lblDescr.setText(activity.getResources().getString(R.string.ba_aggiornamento));
            this.progressBar.setIndeterminate(false);
            this.barprogress = -1;
            this.progressBar.setMax(100);
        } catch (Exception unused) {
        }
    }

    public void startEraseFlash() {
        try {
            this.progressBar.setIndeterminate(false);
        } catch (Exception unused) {
        }
    }

    public void startConnesione() {
        try {
            this.lblDescr.setText(getResources().getString(R.string.ba_TentativoConnUC));
            this.progressBar.setIndeterminate(false);
            this.progressBar.setProgress(0);
        } catch (Exception unused) {
        }
    }

    public void startCrc() {
        try {
            this.lblDescr.setText(activity.getResources().getString(R.string.ba_Crc));
            this.progressBar.setIndeterminate(false);
        } catch (Exception unused) {
        }
    }

    public void btnAggiorna(View view) {
        new Runnable() {
            public void run() {
                BootloaderActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        BootloaderActivity.this.lblDescr.setVisibility(0);
                        BootloaderActivity.this.lblDescr.setTextColor(-1);
                        BootloaderActivity.this.progressBar.setVisibility(0);
                        BootloaderActivity.this.disableView();
                        if (Constants.modesviluppatoreBootloader) {
                            if (ActivityCompat.checkSelfPermission(BootloaderActivity.activity, "android.permission.READ_EXTERNAL_STORAGE") == 0) {
                                BootloaderActivity.this.startActivityForResult(Intent.createChooser(new Intent().setType("*/*").setAction("android.intent.action.GET_CONTENT"), "Select a file"), 123);
                                return;
                            }
                            ActivityCompat.requestPermissions(BootloaderActivity.activity, new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, ItemTouchHelper.Callback.DEFAULT_DRAG_ANIMATION_DURATION);
                        } else if (MySocketBootLoader.lastFWPK > 0) {
                            BootloaderActivity.this.lblDescr.setTextColor(-1);
                            BootloaderActivity.this.lblDescr.setText(BootloaderActivity.activity.getResources().getString(R.string.ba_Connessione));
                            BootloaderActivity.this.lblDescr.setVisibility(0);
                            BootloaderActivity.this.progressBar.setVisibility(0);
                            BootloaderActivity.this.progressBar.setIndeterminate(false);
                            BootloaderActivity.this.showProgress();
                            BootloaderActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    BootloaderActivity.this.btnAgg.setText(R.string.riprova);
                                }
                            });
                            BootloaderActivity.this.enableView();
                            new Runnable() {
                                public void run() {
                                }
                            };
                            new Runnable() {
                                public void run() {
                                    if (BootloaderActivity.this.retriesconnect < 5) {
                                        try {
                                            BootloaderActivity.this.retriesconnect++;
                                            BootloaderActivity.this.enableView();
                                        } catch (Exception unused) {
                                        }
                                    } else {
                                        BootloaderActivity.this.hideProgress();
                                        BootloaderActivity.this.enableView();
                                    }
                                }
                            };
                            BaseActivity.toConnSid = Constants.WIFI_NAME_BOOT;
                            BaseActivity.toConnPwd = Constants.WIFI_PWD_BOOT;
                            BootloaderActivity.this.retriesconnect = 0;
                            BootloaderActivity.this.disableView();
                            BootloaderActivity.this.hideProgress();
                            BootloaderActivity.this.tries = 0;
                            new Thread(new Runnable() {
                                public void run() {
                                    if (!MySocketBootLoader.readCRCForRetry(BootloaderActivity.activity, BootloaderActivity.this.lasteraseflash)) {
                                        new Thread(new Runnable() {
                                            public void run() {
                                                try {
                                                    Thread.sleep(1000);
                                                } catch (Exception unused) {
                                                }
                                                BootloaderActivity.this.connectToWifi((Runnable) null, (Runnable) null, false, true);
                                                if (MySocketBootLoader.lastFWPK > 0) {
                                                    BootloaderActivity.this.tries++;
                                                    BootloaderActivity.this.runOnUiThread(new Runnable() {
                                                        public void run() {
                                                            BootloaderActivity.this.btnAggiorna((View) null);
                                                        }
                                                    });
                                                    return;
                                                }
                                                BootloaderActivity.this.error();
                                            }
                                        }).start();
                                    } else {
                                        BootloaderActivity.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                BootloaderActivity.this.lblDescr.setText(BootloaderActivity.activity.getResources().getString(R.string.ba_aggiornamento_ok));
                                                BootloaderActivity.this.progressBar.setVisibility(8);
                                                BootloaderActivity.this.btnAgg.setText(BootloaderActivity.this.getResources().getString(R.string.ba_btnAggiorna2));
                                                BootloaderActivity.this.btnAgg.setOnClickListener(new View.OnClickListener() {
                                                    public void onClick(View view) {
                                                        BootloaderActivity.this.setResult(-1);
                                                        BootloaderActivity.this.finish();
                                                    }
                                                });
                                                BootloaderActivity.this.enableView();
                                            }
                                        });
                                    }
                                }
                            }).start();
                        } else {
                            BootloaderActivity.this.getVersione();
                        }
                    }
                });
            }
        }.run();
    }

    public void getVersione() {
        new Thread(new Runnable() {
            public void run() {
                byte[] fwVer = MySocketBootLoader.getFwVer(BootloaderActivity.activity);
                byte[] bootVer = MySocketBootLoader.getBootVer(BootloaderActivity.activity);
                if (fwVer == null) {
                    BootloaderActivity.this.connectToWifi((Runnable) null, false, true);
                    BootloaderActivity.this.btnAggiorna((View) null);
                } else if (bootVer == null) {
                    BootloaderActivity.this.connectToWifi((Runnable) null, false, true);
                    BootloaderActivity.this.btnAggiorna((View) null);
                } else if (fwVer.length < 6) {
                    BootloaderActivity.this.error();
                } else {
                    BootloaderActivity.this.bbootver = Arrays.copyOfRange(bootVer, 3, 6);
                    int length = fwVer.length;
                    try {
                        BootloaderActivity bootloaderActivity = BootloaderActivity.this;
                        bootloaderActivity.fwver = String.format(String.format("%02d", new Object[]{Integer.valueOf(fwVer[3] & 255)}) + "." + String.format("%02d", new Object[]{Integer.valueOf(fwVer[4] & 255)}) + "." + String.format("%02d", new Object[]{Integer.valueOf(fwVer[5] & 255)}), new Object[0]);
                        BootloaderActivity.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                BootloaderActivity.this.controlloVersione(true);
                            }
                        });
                    } catch (Exception unused) {
                        BootloaderActivity.this.error();
                    }
                }
            }
        }).start();
    }

    public void controlloVersione(boolean z) {
        AnonymousClass9 r6;
        String str;
        startControlloVersione();
        try {
            this.musterasemin = this.infofwToShow.getJSONObject(this.spn_fw.getSelectedItemPosition()).getString(Constants.JSON_MINCLEANFLASHVR);
            this.musterasemax = this.infofwToShow.getJSONObject(this.spn_fw.getSelectedItemPosition()).getString(Constants.JSON_MAXCLEANFLASHVR);
            if (this.fwver == null) {
                error();
            } else if (this.polaristype < 0 && z && !checkVr3x4x()) {
                msg1314();
                enableView();
            } else if (Functions.compareVerString(this.fwver, this.musterasemin) < 0 || this.fwver.compareTo("255.255.255") == 0 || this.fwver.compareTo("00.00.00") == 0 || (this.musterasemax.toUpperCase().compareTo("NULL") != 0 && Functions.compareVerString(this.musterasemax, this.fwver) < 0)) {
                enableView();
                AnonymousClass7 r7 = new Runnable() {
                    public void run() {
                        BootloaderActivity.this.dismissdialog();
                        BootloaderActivity.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                BootloaderActivity.this.checkResPin();
                            }
                        });
                    }
                };
                if (this.fwver.compareTo("255.255.255") == 0 || this.fwver.compareTo("00.00.00") == 0) {
                    str = getResources().getString(R.string.ba_attenzione_noreset);
                    r6 = new Runnable() {
                        public void run() {
                            BootloaderActivity.this.dismissdialog();
                            BootloaderActivity.activity.runOnUiThread(new Runnable() {
                                public void run() {
                                    BootloaderActivity.this.disableView();
                                    BootloaderActivity.this.aggiorna(false);
                                }
                            });
                        }
                    };
                } else {
                    r6 = new Runnable() {
                        public void run() {
                        }
                    };
                    str = "";
                }
                AnonymousClass10 r8 = new Runnable() {
                    public void run() {
                        BootloaderActivity.this.dismissdialog();
                        BootloaderActivity.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                BootloaderActivity.this.progressBar.setVisibility(8);
                                BootloaderActivity.this.lblDescr.setVisibility(8);
                                BootloaderActivity.this.enableView();
                            }
                        });
                    }
                };
                openDialogFragment(createYesNoCancelPopUp(getResources().getString(R.string.ba_attenzione_title), getResources().getString(R.string.ba_attenzione), str, getResources().getString(R.string.ba_attenzione_reset), getResources().getString(R.string.ba_attenzione_cancel), r6, r7, r8));
            } else {
                aggiorna(false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            error();
        }
    }

    public void checkResPin() {
        openDialogFragment(createGenarlPin(new Runnable() {
            public void run() {
                BootloaderActivity.this.dismissdialog();
                if (BootloaderActivity.this.txtPin2.getText().toString().equals(Constants.RESET_PIN)) {
                    BootloaderActivity.this.aggiorna(true);
                    BootloaderActivity.this.disableView();
                    return;
                }
                Functions.makeErrorToast(BootloaderActivity.activity, BootloaderActivity.this.getResources().getString(R.string.bootloader_invalidPINForReset));
            }
        }, new Runnable() {
            public void run() {
                BootloaderActivity.this.dismissdialog();
            }
        }));
    }

    private boolean checkVr3x4x() {
        try {
            if (Integer.parseInt(this.infofwToShow.getJSONObject(this.spn_fw.getSelectedItemPosition()).getString(Constants.JSON_VERSION).split("\\.")[0]) > 3) {
                byte[] bArr = this.bbootver;
                byte b = bArr[0];
                byte b2 = b & 255;
                int i = this.major4x;
                if (b2 == i) {
                    byte b3 = bArr[1];
                    byte b4 = b3 & 255;
                    int i2 = this.minor4x;
                    if (b4 == i2) {
                        if ((bArr[2] & 255) >= this.release4x) {
                            return true;
                        }
                        return false;
                    } else if ((b3 & 255) > i2) {
                        return true;
                    } else {
                        return false;
                    }
                } else if ((b & 255) > i) {
                    return true;
                } else {
                    return false;
                }
            } else {
                byte[] bArr2 = this.bbootver;
                byte b5 = bArr2[0];
                byte b6 = b5 & 255;
                int i3 = this.major4x;
                if (b6 == i3) {
                    byte b7 = bArr2[1];
                    byte b8 = b7 & 255;
                    int i4 = this.minor4x;
                    if (b8 == i4) {
                        if ((bArr2[2] & 255) < this.release4x) {
                            return true;
                        }
                        return false;
                    } else if ((b7 & 255) < i4) {
                        return true;
                    } else {
                        return false;
                    }
                } else if ((b5 & 255) < i3) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception unused) {
            return false;
        }
    }

    public void msg1314() {
        AnonymousClass13 r6 = new Runnable() {
            public void run() {
                BootloaderActivity.this.dismissdialog();
                BootloaderActivity.activity.runOnUiThread(new Runnable() {
                    public void run() {
                        BootloaderActivity.this.enableView();
                    }
                });
            }
        };
        openDialogFragment(createYesNoPopUp(getString(R.string.ba_verfw_err_title), getString(R.string.ba_verfw_err_text), "", getString(R.string.general_OK), (Runnable) null, r6));
    }

    public void aggiorna(final boolean z) {
        File file;
        try {
            this.times++;
            this.lasteraseflash = z;
            if (Constants.modesviluppatoreBootloader) {
                file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "polaris.s19");
            } else {
                String string = this.infofwToShow.getJSONObject(this.spn_fw.getSelectedItemPosition()).getString("Path");
                File file2 = new File(activity.getBaseContext().getFileStreamPath(Constants.FW_DIRECTORY_NAME), string);
                Log.d("filename", string);
                file = file2;
            }
            disableView();
            try {
                if (file.exists()) {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
                    DataClass instance = DataClass.getInstance(this);
                    instance.firmware_chunk_list = new ArrayList<>();
                    String readLine = bufferedReader.readLine();
                    do {
                        if (readLine.startsWith("S3") && readLine.length() > 6) {
                            instance.firmware_chunk_list.add(MySocketBootLoader.hexStringToByteArray(readLine.substring(4, readLine.length() - 2)));
                        }
                        readLine = bufferedReader.readLine();
                    } while (readLine != null);
                    bufferedReader.close();
                    fileInputStream.close();
                    instance.firmware_chunk_list.size();
                }
                new Thread(new Runnable() {
                    /* JADX WARNING: Removed duplicated region for block: B:14:0x0036  */
                    /* JADX WARNING: Removed duplicated region for block: B:16:0x004a A[ADDED_TO_REGION] */
                    /* Code decompiled incorrectly, please refer to instructions dump. */
                    public void run() {
                        /*
                            r7 = this;
                            it.tecnosystemi.TS.Activity.BootloaderActivity r0 = it.tecnosystemi.TS.Activity.BootloaderActivity.activity
                            it.tecnosystemi.TS.Activity.BootloaderActivity$14$1 r1 = new it.tecnosystemi.TS.Activity.BootloaderActivity$14$1
                            r1.<init>()
                            r0.runOnUiThread(r1)
                            r0 = 0
                            it.tecnosystemi.TS.Activity.BootloaderActivity r1 = it.tecnosystemi.TS.Activity.BootloaderActivity.activity     // Catch:{ Exception -> 0x001f }
                            boolean r2 = r7     // Catch:{ Exception -> 0x001f }
                            boolean r1 = it.tecnosystemi.TS.Commands.MySocketBootLoader.cleanFlash(r1, r2)     // Catch:{ Exception -> 0x001f }
                            java.lang.String r2 = ""
                            it.tecnosystemi.TS.Activity.BootloaderActivity r3 = it.tecnosystemi.TS.Activity.BootloaderActivity.this     // Catch:{ Exception -> 0x001d }
                            java.lang.String r3 = r3.fwver     // Catch:{ Exception -> 0x001d }
                            android.util.Log.d(r2, r3)     // Catch:{ Exception -> 0x001d }
                            goto L_0x002a
                        L_0x001d:
                            r2 = move-exception
                            goto L_0x0021
                        L_0x001f:
                            r2 = move-exception
                            r1 = 0
                        L_0x0021:
                            java.lang.String r3 = "Socket"
                            java.lang.String r2 = r2.toString()
                            android.util.Log.d(r3, r2)
                        L_0x002a:
                            r2 = 1
                            java.lang.String r3 = "TS123456"
                            java.lang.String r4 = "POLARIS_UPDATE"
                            r5 = 0
                            if (r1 != 0) goto L_0x004a
                            int r6 = it.tecnosystemi.TS.Commands.MySocketBootLoader.lastFWPK
                            if (r6 <= 0) goto L_0x004a
                            it.tecnosystemi.TS.Activity.BaseActivity.toConnSid = r4
                            it.tecnosystemi.TS.Activity.BaseActivity.toConnPwd = r3
                            it.tecnosystemi.TS.Activity.BootloaderActivity r1 = it.tecnosystemi.TS.Activity.BootloaderActivity.this
                            r1.connectToWifi(r5, r5, r0, r2)
                            it.tecnosystemi.TS.Activity.BootloaderActivity r0 = it.tecnosystemi.TS.Activity.BootloaderActivity.this
                            it.tecnosystemi.TS.Activity.BootloaderActivity$14$2 r1 = new it.tecnosystemi.TS.Activity.BootloaderActivity$14$2
                            r1.<init>()
                            r0.runOnUiThread(r1)
                            return
                        L_0x004a:
                            if (r1 != 0) goto L_0x0075
                            int r6 = it.tecnosystemi.TS.Commands.MySocketBootLoader.lastFWPK
                            if (r6 != 0) goto L_0x0075
                            it.tecnosystemi.TS.Activity.BootloaderActivity r6 = it.tecnosystemi.TS.Activity.BootloaderActivity.this     // Catch:{ Exception -> 0x006b }
                            r6.showProgress()     // Catch:{ Exception -> 0x006b }
                            it.tecnosystemi.TS.Activity.BaseActivity.toConnSid = r4     // Catch:{ Exception -> 0x006b }
                            it.tecnosystemi.TS.Activity.BaseActivity.toConnPwd = r3     // Catch:{ Exception -> 0x006b }
                            it.tecnosystemi.TS.Activity.BootloaderActivity r3 = it.tecnosystemi.TS.Activity.BootloaderActivity.this     // Catch:{ Exception -> 0x006b }
                            r3.connectToWifi(r5, r5, r0, r2)     // Catch:{ Exception -> 0x006b }
                            r2 = 1000(0x3e8, double:4.94E-321)
                            java.lang.Thread.sleep(r2)     // Catch:{ Exception -> 0x006b }
                            it.tecnosystemi.TS.Activity.BootloaderActivity r0 = it.tecnosystemi.TS.Activity.BootloaderActivity.this     // Catch:{ Exception -> 0x006b }
                            boolean r2 = r7     // Catch:{ Exception -> 0x006b }
                            r0.aggiorna(r2)     // Catch:{ Exception -> 0x006b }
                            return
                        L_0x006b:
                            r0 = move-exception
                            java.lang.String r2 = "ERROE"
                            java.lang.String r0 = r0.toString()
                            android.util.Log.d(r2, r0)
                        L_0x0075:
                            if (r1 == 0) goto L_0x0082
                            it.tecnosystemi.TS.Activity.BootloaderActivity r0 = it.tecnosystemi.TS.Activity.BootloaderActivity.this
                            it.tecnosystemi.TS.Activity.BootloaderActivity$14$3 r1 = new it.tecnosystemi.TS.Activity.BootloaderActivity$14$3
                            r1.<init>()
                            r0.runOnUiThread(r1)
                            goto L_0x0087
                        L_0x0082:
                            it.tecnosystemi.TS.Activity.BootloaderActivity r0 = it.tecnosystemi.TS.Activity.BootloaderActivity.this
                            r0.error()
                        L_0x0087:
                            return
                        */
                        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.BootloaderActivity.AnonymousClass14.run():void");
                    }
                }).start();
            } catch (Exception unused) {
                error();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i == 123 && i2 == -1) {
            sendFWMGDEBUG(intent.getData());
        }
    }

    public void sendFWMGDEBUG(Uri uri) {
        try {
            disableView();
            try {
                InputStream openInputStream = getContentResolver().openInputStream(uri);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openInputStream));
                DataClass instance = DataClass.getInstance(this);
                instance.firmware_chunk_list = new ArrayList<>();
                String readLine = bufferedReader.readLine();
                do {
                    if (readLine.startsWith("S3") && readLine.length() > 6) {
                        instance.firmware_chunk_list.add(MySocketBootLoader.hexStringToByteArray(readLine.substring(4, readLine.length() - 2)));
                    }
                    readLine = bufferedReader.readLine();
                } while (readLine != null);
                bufferedReader.close();
                openInputStream.close();
                instance.firmware_chunk_list.size();
                new Thread(new Runnable() {
                    public void run() {
                        BootloaderActivity.activity.runOnUiThread(new Runnable() {
                            public void run() {
                                BootloaderActivity.this.lblDescr.setTextColor(-1);
                                BootloaderActivity.this.lblDescr.setText(BootloaderActivity.activity.getResources().getString(R.string.ba_Connessione));
                                BootloaderActivity.this.lblDescr.setVisibility(0);
                                BootloaderActivity.this.progressBar.setVisibility(0);
                                BootloaderActivity.this.progressBar.setIndeterminate(false);
                            }
                        });
                        boolean z = false;
                        try {
                            z = MySocketBootLoader.cleanFlash(BootloaderActivity.activity, ((Switch) BootloaderActivity.this.findViewById(R.id.ba_swtCancellaFlash)).isChecked());
                            Log.d("", BootloaderActivity.this.fwver);
                        } catch (Exception e) {
                            Log.d("Socket", e.toString());
                        }
                        if (z) {
                            BootloaderActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    BootloaderActivity.this.lblDescr.setText(BootloaderActivity.activity.getResources().getString(R.string.ba_aggiornamento_ok));
                                    BootloaderActivity.this.progressBar.setVisibility(8);
                                    BootloaderActivity.this.btnAgg.setText(BootloaderActivity.this.getResources().getString(R.string.ba_btnAggiorna2));
                                    BootloaderActivity.this.btnAgg.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View view) {
                                            BootloaderActivity.this.setResult(-1);
                                            BootloaderActivity.this.finish();
                                        }
                                    });
                                    BootloaderActivity.this.enableView();
                                }
                            });
                        } else {
                            BootloaderActivity.this.error();
                        }
                    }
                }).start();
            } catch (Exception unused) {
                error();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void error() {
        runOnUiThread(new Runnable() {
            public void run() {
                BootloaderActivity.this.lblDescr.setVisibility(0);
                BootloaderActivity.this.lblDescr.setText(BootloaderActivity.activity.getResources().getString(R.string.ba_aggiornamento_ko));
                BootloaderActivity.this.progressBar.setVisibility(8);
                BootloaderActivity.this.lblDescr.setTextColor(SupportMenu.CATEGORY_MASK);
                Functions.makeErrorToast(BootloaderActivity.activity, BootloaderActivity.activity.getResources().getString(R.string.ba_aggiornamento_ko));
                BootloaderActivity.this.enableView();
            }
        });
    }

    public void disableView() {
        runOnUiThread(new Runnable() {
            public void run() {
                BootloaderActivity.this.disable = true;
                BootloaderActivity.this.btnAgg.setEnabled(false);
            }
        });
    }

    public void enableView() {
        runOnUiThread(new Runnable() {
            public void run() {
                BootloaderActivity.this.disable = false;
                BootloaderActivity.this.btnAgg.setEnabled(true);
            }
        });
    }

    public void onBackPressed() {
        if (this.disable) {
            Functions.makeNormalToast(this, getResources().getString(R.string.ba_attendere));
            return;
        }
        if (this.cercacentralina != null) {
            this.cercacentralina.interrupt();
        }
        this.interrupted = true;
        super.onBackPressed();
    }

    public void onResume() {
        super.onResume();
        this.background = false;
        if (this.errorcollegamento) {
            this.errorcollegamento = false;
            Functions.makeErrorToast(this, getResources().getString(R.string.ba_apAssente));
        }
    }

    public void onPause() {
        super.onPause();
        this.background = true;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Can't wrap try/catch for region: R(11:0|1|2|3|4|6|(1:8)|9|(1:11)|12|13) */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x000a */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x001f  */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0013  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onDestroy() {
        /*
            r1 = this;
            r0 = 10
            r1.trieshw = r0
            r0 = 0
            it.tecnosystemi.TS.Commands.MySocketBootLoader.lastFWPK = r0
            it.tecnosystemi.TS.Commands.MySocketBootLoader.disconnectWithSocket()     // Catch:{ Exception -> 0x000a }
        L_0x000a:
            r1.unbidNetwork()     // Catch:{ Exception -> 0x000e }
            goto L_0x000f
        L_0x000e:
        L_0x000f:
            java.lang.Thread r0 = r1.cercacentralina
            if (r0 == 0) goto L_0x0018
            java.lang.Thread r0 = r1.cercacentralina
            r0.interrupt()
        L_0x0018:
            r0 = 1
            r1.pausecercacentralina = r0
            android.os.PowerManager$WakeLock r0 = r1.wl
            if (r0 == 0) goto L_0x0022
            r0.release()
        L_0x0022:
            r0 = 0
            bootloaderActivity = r0
            super.onDestroy()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: it.tecnosystemi.TS.Activity.BootloaderActivity.onDestroy():void");
    }

    public View getToolBar() {
        return findViewById(R.id.ba_toolbar);
    }

    public String setToolbarTitle() {
        return getResources().getString(R.string.bi_lblTitle);
    }
}
