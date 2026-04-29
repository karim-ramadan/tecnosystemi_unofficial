package it.tecnosystemi.TS.Fragment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.DialogFragment;
import it.tecnosystemi.TS.Activity.LoginActivity;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;
import java.io.InputStream;

public class InstructionDialogFragment extends DialogFragment {
    static Context mcontext;
    private static int typeCall;
    private static String url;
    Button btnexit;
    Button btnok;
    Typeface custom_font;
    String ip = null;
    LinearLayout lyConnect;
    View mview;
    Resources res;
    TextView title;
    WebView wv;

    public static InstructionDialogFragment newInstance(String str, Context context, String str2, int i) {
        InstructionDialogFragment instructionDialogFragment = new InstructionDialogFragment();
        mcontext = context;
        url = mcontext.getResources().getString(R.string.uriWebService) + str2;
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_TITLE, str);
        instructionDialogFragment.setArguments(bundle);
        typeCall = i;
        return instructionDialogFragment;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_instructions, viewGroup);
        getDialog().getWindow().requestFeature(1);
        WindowManager.LayoutParams attributes = getDialog().getWindow().getAttributes();
        attributes.gravity = 17;
        attributes.horizontalMargin = 0.0f;
        attributes.width = -1;
        attributes.height = -1;
        getDialog().getWindow().setAttributes(attributes);
        this.mview = inflate;
        this.btnexit = (Button) inflate.findViewById(R.id.if_btnX);
        this.btnok = (Button) inflate.findViewById(R.id.if_btnOK);
        Typeface createFromAsset = Typeface.createFromAsset(getActivity().getAssets(), "fonts/AvenirNextCondensed_Regular.ttf");
        Typeface createFromAsset2 = Typeface.createFromAsset(getActivity().getAssets(), "fonts/fontawesome.ttf");
        TextView textView = (TextView) inflate.findViewById(R.id.if_lblTitle);
        this.title = textView;
        textView.setText(getArguments().getString(Constants.BUNDLE_TITLE));
        this.title.setTypeface(createFromAsset);
        this.res = mcontext.getResources();
        WebView webView = (WebView) inflate.findViewById(R.id.if_WebView);
        this.wv = webView;
        webView.getSettings().setJavaScriptEnabled(true);
        this.wv.getSettings().setDomStorageEnabled(true);
        this.wv.getSettings().setCacheMode(2);
        this.wv.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView webView, String str) {
                InstructionDialogFragment.this.injectCSS();
                super.onPageFinished(webView, str);
            }
        });
        this.wv.loadUrl(url);
        this.btnexit.setTypeface(createFromAsset2);
        this.btnexit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                this.dismiss();
            }
        });
        this.btnok.setTypeface(createFromAsset);
        if (typeCall != 1) {
            this.btnok.setText(getResources().getString(R.string.sa_OkTermsOfUse));
            this.btnok.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    this.dismiss();
                }
            });
        } else {
            this.btnok.setText(getResources().getString(R.string.sa_accpetTermsOfUSe));
            this.btnok.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    this.dismiss();
                    if (InstructionDialogFragment.mcontext instanceof LoginActivity) {
                        ((LoginActivity) InstructionDialogFragment.mcontext).login(InstructionDialogFragment.this.res.getString(R.string.uri_TermOfUse));
                    }
                }
            });
        }
        return inflate;
    }

    /* access modifiers changed from: private */
    public void injectCSS() {
        try {
            InputStream open = getActivity().getAssets().open("css/style.css");
            byte[] bArr = new byte[open.available()];
            open.read(bArr);
            open.close();
            String encodeToString = Base64.encodeToString(bArr, 2);
            WebView webView = this.wv;
            webView.loadUrl("javascript:(function() {var parent = document.getElementsByTagName('head').item(0);var style = document.createElement('style');style.type = 'text/css';style.innerHTML = window.atob('" + encodeToString + "');parent.appendChild(style)})()");
            this.wv.setVisibility(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        window.setLayout(-1, -1);
        window.setGravity(17);
    }
}
