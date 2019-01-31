package net.xprinter.example4wifi;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.EditText;

public class SharedPrefLib{
    private EditText mText;
    private Context context;
    private SharedPreferences preferences;

    //construct,此處用多型
    public SharedPrefLib(Context argContext,EditText argText){
        context = argContext;
        mText = argText;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }
    public SharedPrefLib(Context argContext){
        context = argContext;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean saveTextData(String key){
        String renameIP = mText.getText().toString();

        //已有建構此儲存物件
        if(preferences != null){
            //---儲存設定的IP資料
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key,renameIP);
            if(editor.commit()){
                return false;
            }else{
                return true;
            }
        }
        return false;
    }
    public String getStringData(String key){
        if(preferences != null){
            String name = preferences.getString(key, "");
            return name;
        }else{
            return "false";
        }
    }
}
