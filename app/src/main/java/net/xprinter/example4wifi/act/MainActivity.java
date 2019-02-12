package net.xprinter.example4wifi.act;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
	private ProgressDialog pd;
	private String domainName = "http://erp015.ezrun.com.tw/pos/";

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
					PrintfLog("離開...");
					Intent homeIntent = new Intent(Intent.ACTION_MAIN);
					homeIntent.addCategory( Intent.CATEGORY_HOME );
					homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(homeIntent);

				}else{
					if(cmdUri.equals("print")){
						//列印單據指令
						//POS單號
						String posCode = uri.getQueryParameter("pos_code");
						new JsonTask().execute(domainName+posCode+"/printing");
					}
				}
			}else{
				PrintfLog("連接失敗...");
				buttonCon.setText("未連接...");
				buttonPf.setEnabled(false);
				buttonCash.setEnabled(false);
				buttonCut.setEnabled(false);

				//POS單號
				//String posCode = "20190122006";
				//String posCode = uri.getQueryParameter("pos_code");
				//new JsonTask().execute(domainName+posCode+"/printing");

				//列印完畢回到CHROME畫面
				/*Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				intent.setClassName("com.android.chrome","com.google.android.apps.chrome.Main");
				//開啟的網頁
				intent.setData(Uri.parse(domainName+"create"));
				startActivity(intent);*/
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private class JsonTask extends AsyncTask<String, String, String> {

		protected void onPreExecute() {
			super.onPreExecute();

			pd = new ProgressDialog(MainActivity.this);
			pd.setMessage("Please wait");
			pd.setCancelable(false);
			pd.show();
		}

		protected String doInBackground(String... params) {


			HttpURLConnection connection = null;
			BufferedReader reader = null;

			try {
				URL url = new URL(params[0]);
				connection = (HttpURLConnection) url.openConnection();
				connection.connect();


				InputStream stream = connection.getInputStream();

				reader = new BufferedReader(new InputStreamReader(stream));

				StringBuffer buffer = new StringBuffer();
				String line = "";

				while ((line = reader.readLine()) != null) {
					buffer.append(line+"\n");
					Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

				}

				return buffer.toString();


			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
				try {
					if (reader != null) {
						reader.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (pd.isShowing()){
				pd.dismiss();
			}


			try {
				//將字串轉為json陣列格式
				JSONArray jsonResults = new JSONArray(result);
				//該陣列的總數

				/*int jsonArrLength = jsonResults.length();
				for(int i = 0; i < jsonArrLength ; i++){
					//取得陣列中各個JSONObject
					JSONObject jsonResult = jsonResults.getJSONObject(i);
					Log.d("My App", jsonResult.get("name").toString());
				}*/
				//將資料庫收到的JSON資料送至列印函式
				PrintBasedData(jsonResults);

				//列印完畢回到CHROME畫面
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				intent.setClassName("com.android.chrome","com.google.android.apps.chrome.Main");
				intent.setData(Uri.parse(domainName+"create"));
				startActivity(intent);

			} catch (Throwable t) {
				Log.e("My App", "Could not parse malformed JSON: \"" + result + "\"");
			}
		}
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

	public void PrintBasedData(JSONArray jsonPrints){
		//send data to printer and print
		try {
			CommandBytes cmdBytes = new CommandBytes(jsonPrints);
			cmdBytes.addHeader();
			cmdBytes.addBody();
			//cmdBytes.addBytes((mprintfData.getText().toString()).getBytes("GBK"));
			if (PrintfData(cmdBytes.getBytes())) {
				PrintfLog("打印成功...");
			}
			else {
				PrintfLog("打印失敗...");
				buttonPf.setEnabled(false);
			}
		} catch (JSONException je){
			je.printStackTrace();
			PrintfLog("格式轉換發生錯誤...");
		} catch (Exception e) {
			e.printStackTrace();
			PrintfLog("數據發送錯誤...");
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
