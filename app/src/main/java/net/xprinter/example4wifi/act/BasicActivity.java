package net.xprinter.example4wifi.act;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

import net.xprinter.example4wifi.R;

public class BasicActivity extends Activity {

    //選取設定事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            //Context nextPage = (this.getApplicationContext() == SettingActivity.class)?SettingActivity.class:MainActivity.class;
            Intent intent1 = new Intent(this,SettingActivity.class);
            //if(this == )
            this.startActivity(intent1);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
