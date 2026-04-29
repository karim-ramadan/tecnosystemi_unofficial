package it.tecnosystemi.TS.Activity.TS;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.gson.Gson;
import it.tecnosystemi.TS.Activity.BaseActivity;
import it.tecnosystemi.TS.Adapters.SpinnerCUIconsAdapter;
import it.tecnosystemi.TS.Model.Plant;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Threads.ThreadWebService;
import it.tecnosystemi.TS.Utils.Constants;
import it.tecnosystemi.TS.Utils.Functions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddUpdPlantActivity extends BaseActivity {
    BaseActivity activity;
    int indexPL;
    TextView lblfreccia;
    Spinner spinnerhome;
    EditText txtNome;

    public BaseActivity getActivity() {
        return this;
    }

    public List<ConstraintLayout> getMenu(List<ConstraintLayout> list) {
        return list;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        setContentView(R.layout.activity_add_upd_plant);
        this.activity = this;
        this.typeActStyle = 1;
        this.indexPL = getIntent().getIntExtra(Constants.INP_INDEX, -1);
        super.onCreate(bundle);
        hideMenuButton();
        setUpGui();
    }

    public void btnProc(View view) {
        String str;
        if (Constants.ISDEMO) {
            Functions.makeErrorToast(this, getResources().getString(R.string.cu_DemoVersion));
        } else if (this.txtNome.getText().toString().isEmpty()) {
            this.txtNome.setError(getResources().getString(R.string.sa_errorEmpty));
        } else {
            Plant plant = new Plant();
            if (this.indexPL < 0) {
                str = getResources().getString(R.string.uriWebService) + getResources().getString(R.string.uri_InsertPlant);
            } else {
                plant.setLVPL_Id(Constants.listaImpianti.get(this.indexPL).getLVPL_Id());
                str = getResources().getString(R.string.uriWebService) + getResources().getString(R.string.uri_UpdatePlant);
            }
            plant.setLVPL_Name(this.txtNome.getText().toString().toUpperCase());
            plant.setLVPL_Icon(this.spinnerhome.getSelectedItemPosition());
            showProgress();
            new ThreadWebService(this.activity, 1, 22, str, new Gson().toJson((Object) plant), (String[]) null).start();
        }
    }

    private void setUpGui() {
        Button button = (Button) findViewById(R.id.snpa_btnProc);
        this.txtNome = (EditText) findViewById(R.id.snpa_txtNome);
        this.lblfreccia = (TextView) findViewById(R.id.snpa_lblFreccia);
        this.spinnerhome = (Spinner) findViewById(R.id.snpa_spinner);
        this.spinnerhome.setAdapter(new SpinnerCUIconsAdapter(this, new ArrayList(Arrays.asList(Constants.ICON_TYPE))));
        this.spinnerhome.setSelection(0);
        this.lblfreccia.setTypeface(fontawesome);
        button.setTypeface(avenir);
        this.txtNome.setTypeface(avenir);
        ((TextView) findViewById(R.id.lblInfo)).setTypeface(avenir);
        if (this.indexPL < 0) {
            button.setText(getResources().getString(R.string.cp_btnCreatePlant));
            return;
        }
        this.txtNome.setText(Constants.listaImpianti.get(this.indexPL).getLVPL_Name());
        if (Constants.listaImpianti.get(this.indexPL).getLVPL_Icon() >= 0) {
            this.spinnerhome.setSelection(Constants.listaImpianti.get(this.indexPL).getLVPL_Icon());
        }
        button.setText(getResources().getString(R.string.cp_btnUpdatePlant));
    }

    public View getToolBar() {
        return findViewById(R.id.snpa_toolbar);
    }

    public String setToolbarTitle() {
        if (this.indexPL < 0) {
            return getResources().getString(R.string.cp_lblTitle);
        }
        return getResources().getString(R.string.cp_lblTitleUpdate);
    }
}
