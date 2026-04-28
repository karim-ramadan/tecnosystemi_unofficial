package it.tecnosystemi.TS.Fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import it.tecnosystemi.TS.Model.MenuList;
import it.tecnosystemi.TS.R;
import it.tecnosystemi.TS.Utils.Constants;

public class MenuFragment extends DialogFragment {
    boolean asmenu;
    TextView lblTitle;
    LinearLayout linearLayout;
    Bundle mArgs;
    MenuList menuList;

    public MenuFragment(MenuList menuList2) {
        this.menuList = menuList2;
    }

    public void changemenu(MenuList menuList2) {
        this.menuList = menuList2;
    }

    public Dialog onCreateDialog(Bundle bundle) {
        return super.onCreateDialog(bundle);
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.fragment_menu, viewGroup);
        getDialog().setCanceledOnTouchOutside(true);
        Bundle arguments = getArguments();
        this.mArgs = arguments;
        int i = arguments.getInt(Constants.BUNDLE_MENU);
        this.asmenu = false;
        this.linearLayout = (LinearLayout) inflate.findViewById(R.id.ly_menu);
        if (i == Constants.AS_MENU) {
            this.asmenu = true;
        }
        Window window = getDialog().getWindow();
        TextView textView = (TextView) inflate.findViewById(R.id.lblTitlePopUp);
        this.lblTitle = textView;
        if (this.asmenu) {
            textView.setVisibility(8);
            window.setGravity(8388661);
            getDialog().getWindow().getAttributes().windowAnimations = R.style.MyAnimation_Window;
            WindowManager.LayoutParams attributes = window.getAttributes();
            this.mArgs.getInt(Constants.BUNDLE_POSX);
            int i2 = this.mArgs.getInt(Constants.BUNDLE_POSY);
            attributes.x = getActivity().getResources().getDimensionPixelOffset(R.dimen.margin_menu);
            attributes.y = i2;
            attributes.flags &= -3;
            window.setAttributes(attributes);
        } else {
            window.setGravity(17);
            this.lblTitle.setText(this.mArgs.getString(Constants.BUNDLE_TITLE));
            this.lblTitle.setTextColor(getActivity().getResources().getColor(R.color.colorPrimaryDark));
            this.lblTitle.setTextSize(15.0f);
            this.lblTitle.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/AvenirNextCondensed_Bold.ttf"));
        }
        for (ConstraintLayout next : this.menuList.getLayouts()) {
            if (next.getParent() == null) {
                this.linearLayout.addView(next);
            }
        }
        if (!(getDialog() == null || getDialog().getWindow() == null)) {
            getDialog().getWindow().setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.popup_round));
            getDialog().getWindow().requestFeature(1);
        }
        return inflate;
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    public void onDismiss(DialogInterface dialogInterface) {
        for (ConstraintLayout removeView : this.menuList.getLayouts()) {
            this.linearLayout.removeView(removeView);
        }
    }

    public void onResume() {
        getDialog().getWindow();
        super.onResume();
    }
}
