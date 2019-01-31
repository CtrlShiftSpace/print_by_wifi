package net.xprinter.example4wifi.act;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import net.xprinter.example4wifi.CommandBytes;
import net.xprinter.example4wifi.R;
import net.xprinter.example4wifi.SharedPrefLib;
import net.xprinter.example4wifi.Socketmanager;

public class MainActivity extends BasicActivity {
	private Button buttonCon=null;
	private Button buttonPf=null;
	private Button buttonCash=null;
	private Button buttonCut=null;
	private Button buttonExit=null;
	private EditText mTextIp=null;
	private EditText mprintfData=null;
	private EditText mprintfLog=null;
	private Socketmanager mSockManager;
	private String ipAddress = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		buttonCon=(Button)findViewById(R.id.conTest);
		buttonPf=(Button)findViewById(R.id.printf);
		buttonCash=(Button)findViewById(R.id.buttonCash);
		buttonCut=(Button)findViewById(R.id.buttonCut);
		buttonExit=(Button)findViewById(R.id.buttonExit);
		//mTextIp=(EditText)findViewById(R.id.printerIp);
		mprintfData=(EditText)findViewById(R.id.printfData);
		mprintfLog=(EditText)findViewById(R.id.printfLog);
		ButtonListener buttonListener=new ButtonListener();
		buttonCon.setOnClickListener(buttonListener);
		buttonPf.setOnClickListener(buttonListener);
		buttonCash.setOnClickListener(buttonListener);
		buttonCut.setOnClickListener(buttonListener);
        buttonExit.setOnClickListener(buttonListener);
		mSockManager=new Socketmanager(MainActivity.this);
		SharedPrefLib settingSharePref = new SharedPrefLib(this.getApplicationContext());
		ipAddress = settingSharePref.getStringData("savedIP");


		Uri uri = getIntent().getData();
		if(uri != null) {
			//判斷傳送來的uri指令
			String cmdUri = uri.getQueryParameter("cmdUri");
			//String test2 = uri.getQueryParameter("arg1");
			//mprintfData.setText(cmdUri);
			if (conTest(ipAddress)) {
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
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	class ButtonListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			//Toast.makeText(this,"11",Toast.LENGTH_SHORT)
			switch (v.getId()) {
			case R.id.conTest:
				//Connect to printer
				if (conTest(ipAddress)) {
					PrintfLog("連接成功...");
					buttonCon.setText("已連接...");
					buttonPf.setEnabled(true);
					buttonCash.setEnabled(true);
					buttonCut.setEnabled(true);
				}
				else {
					PrintfLog("連接失敗...");
					buttonCon.setText("未連接...");
					buttonPf.setEnabled(false);
					buttonCash.setEnabled(false);
					buttonCut.setEnabled(false);
				}
				break;
			case R.id.printf:
				//send data to printer and print
				try {
					//
					CommandBytes cmdBytes = new CommandBytes();
					cmdBytes.addTitle();
					cmdBytes.addBody();
					//cmdBytes.addBytes((mprintfData.getText().toString()).getBytes("GBK"));
					if (PrintfData(cmdBytes.getBytes())) {
						PrintfLog("打印成功...");
					}
					else {
						PrintfLog("打印失敗...");
						buttonPf.setEnabled(false);
					}					
				} catch (Exception e) {
					e.printStackTrace();
					PrintfLog("數據發送錯誤...");
				}
				break;
			case R.id.buttonCash:
				//send the command to open the cash
				byte SendCash[]={0x1b,0x70,0x00,0x1e,(byte)0xff,0x00};
				if (PrintfData(SendCash)) {
					PrintfLog("打開成功...");
				}
				else {
					PrintfLog("打開失敗...");
				}
				break;
			case R.id.buttonCut:
				//cut the paper
				byte SendCut[]={0x0a,0x0a,0x1d,0x56,0x01};	
				if (PrintfData(SendCut)) {
					PrintfLog("切紙成功...");
				}
				else {
					PrintfLog("切紙失敗...");
				}
				break;
			case R.id.buttonExit:
				PrintfLog("離開...");
				Intent homeIntent = new Intent(Intent.ACTION_MAIN);
				homeIntent.addCategory( Intent.CATEGORY_HOME );
				homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(homeIntent);

				break;
			default:
				break;
			}
		}
	}

	/**
	 *
	 * @param printerIp Printer的IP
	 * @return true|false
	 */
	public boolean conTest(String printerIp) {
		mSockManager.mPort=9100;
		mSockManager.mstrIp=printerIp;
		mSockManager.threadconnect();
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (mSockManager.getIstate()) {
			return true;
		}
		else {
			return false;
		}				
	}

	/**
	 * 螢幕上的 Log 顯示
	 * @param logString 顯示的字串資料
	 */
	public void PrintfLog(String logString) {
		mprintfLog.setText(logString);
	}

	/**
	 *
	 * @param data 列印的資料
	 * @return
	 */
	public boolean PrintfData(byte[]data) {
		mSockManager.threadconnectwrite(data);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (mSockManager.getIstate()) {
			return true;
		}
		else {
			return false;
		}
	}

}
