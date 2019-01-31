package net.xprinter.example4wifi;

import android.view.View;

public class ButtonListener implements View.OnClickListener {
    SharedPrefLib sharePrf;

    public ButtonListener(SharedPrefLib argSharePrf){
        super();
        sharePrf = argSharePrf;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSubmit:
                sharePrf.saveTextData("savedIP");
                break;
            default:
                break;
        }
    }
}
