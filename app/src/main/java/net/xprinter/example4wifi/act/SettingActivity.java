package net.xprinter.example4wifi.act;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import net.xprinter.example4wifi.ButtonListener;
import net.xprinter.example4wifi.R;
import net.xprinter.example4wifi.SharedPrefLib;

public class SettingActivity extends BasicActivity {
	private EditText mTextIp=null;
	private Button buttonSubmit=null;
	private EditText mprintfLog=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);

		mTextIp=(EditText)findViewById(R.id.printerIp);
		buttonSubmit = (Button) findViewById(R.id.btnSubmit);


		SharedPrefLib settingSharePref = new SharedPrefLib(this.getApplicationContext(),mTextIp);
		ButtonListener buttonListener = new ButtonListener(settingSharePref);

		buttonSubmit.setOnClickListener(buttonListener);

		//preferences = PreferenceManager.getDefaultSharedPreferences(this);
		String name = settingSharePref.getStringData("savedIP");
		if(!name.equals("false")){
			mTextIp.setText(name);
		}




		/*buttonCon=(Button)findViewById(R.id.conTest);
		buttonPf=(Button)findViewById(R.id.printf);
		buttonCash=(Button)findViewById(R.id.buttonCash);
		buttonCut=(Button)findViewById(R.id.buttonCut);

		mprintfData=(EditText)findViewById(R.id.printfData);
		mprintfLog=(EditText)findViewById(R.id.printfLog);
		ButtonListener buttonListener=new ButtonListener();
		buttonCon.setOnClickListener(buttonListener);
		buttonPf.setOnClickListener(buttonListener);
		buttonCash.setOnClickListener(buttonListener);
		buttonCut.setOnClickListener(buttonListener);
		mSockManager=new Socketmanager(SettingActivity.this);
		*/
		/*Uri uri = getIntent().getData();
		if(uri != null) {
			//判斷傳送來的uri指令
			String cmdUri = uri.getQueryParameter("cmdUri");
			String test2 = uri.getQueryParameter("arg1");
			mprintfData.setText(cmdUri);
			if (conTest(mTextIp.getText().toString())) {
				PrintfLog("連接成功...");
				buttonCon.setText("已連接...");
				buttonPf.setEnabled(true);
				buttonCash.setEnabled(true);
				buttonCut.setEnabled(true);

				if(cmdUri.equals("cash")) {
					//若傳送開錢櫃指令
					byte SendCash[] = {0x1b, 0x70, 0x00, 0x1e, (byte) 0xff, 0x00};
					if (PrintfData(SendCash)) {
						PrintfLog("打開成功...");
					} else {
						PrintfLog("打開失敗...");
					}
				}

			}
			else {
				PrintfLog("連接失敗...");
				buttonCon.setText("未連接...");
				buttonPf.setEnabled(false);
				buttonCash.setEnabled(false);
				buttonCut.setEnabled(false);
			}
		}*/
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	//選取設定事件
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_settings) {
			Intent intent1 = new Intent(this,MainActivity.class);

			this.startActivity(intent1);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}




}
