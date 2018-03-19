package com.yocmoon.evaluation.activity;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

import org.ksoap2.serialization.SoapObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yocmoon.evaluation.MainApplication;
import com.yocmoon.evaluation.R;
import com.yocmoon.evaluation.interfaces.OnSocketProgressListener;
import com.yocmoon.evaluation.interfaces.OnWebServiceProgressListener;
import com.yocmoon.evaluation.receiver.ActivityBroadcastReceiver;
import com.yocmoon.evaluation.service.SocketServerService;
import com.yocmoon.evaluation.task.WSAsyncTask;
import com.yocmoon.evaluation.utils.CustomXMLUtil;
import com.yocmoon.evaluation.utils.FileUtil;
import com.yocmoon.evaluation.utils.LogUtil;
import com.yocmoon.evaluation.utils.StringUtil;

/**
 * @author jx
 *
 */
public class MainActivity extends BaseActivity implements OnClickListener, OnTouchListener {
	private String TAG = MainActivity.class.getSimpleName();

	private LinearLayout windowidLinearLayout;
	private TextView windowidTextView;
	private TextView WindowNameTextView;
	private TextView DepartmentTextView;

	private String windowId;
	private String windowName;
	private String department;

	public static String socketPort;
	private SocketServerService msgService;
	private String lastProgress = "";
	
//	// FIXME del by jx on 20170408 start
//	private String banJianGuid;
//	public String IpAddress;
//	public String MacAddress;
//	// del by jx on 20170408 end

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		windowidLinearLayout = (LinearLayout) findViewById(R.id.windowLinearLayout);
		windowidLinearLayout.setOnTouchListener(this);
		windowidTextView = (TextView) findViewById(R.id.windowid);
		WindowNameTextView = (TextView) findViewById(R.id.WindowName);
		DepartmentTextView = (TextView) findViewById(R.id.Department);

		// 异步执行加载WifiAddressProps,并调用WebService的任务
		new Thread(loadWifiAddressPropsRunnable).start();

//		// FIXME del by jx on 20170408 start
//		// 异步执行加载SocketProps,并调用SocketServer的任务
//		new Thread(loadSocketPropsRunnable).start();
//		// del by jx on 20170408 end
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		if (conn != null) {
			unbindService(conn);
		}
		if (msgService != null) {
			msgService.acceptFlag = false;
		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.windowid:
			break;
		case R.id.WindowName:
			break;
		case R.id.Department:
			break;
		default:
			break;
		}
	}
	
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
            return true;
        }
        return false;
    }
	
	@Override
	public boolean onTouch(View v, MotionEvent arg1) {
		if (!StringUtil.isNullOrEmpty(MainApplication.websiteUrl)) {
			startWebsiteActivity();
		}
		
		return false;
	}

	private static final String WIFIADDRESS_PROPS = "wifiAddress.properties";
	private Runnable loadWifiAddressPropsRunnable = new Runnable() {
		public void run() {
			try {
				String sdPath = FileUtil.getSDPath();
				String sdFilePath = sdPath + "/" + WIFIADDRESS_PROPS;
				boolean sdFileIsExist = (sdPath != null)
						&& FileUtil.fileIsExists(sdFilePath);

				Properties props = new Properties();
				InputStream in = null;
				if (sdFileIsExist) {
					in = new FileInputStream(sdFilePath);
					props.load(in);
				} else {
					try {
						props.load(MainActivity.this.getAssets().open(
								WIFIADDRESS_PROPS));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				LogUtil.logd(TAG, "loadWifiAddressProps:"+props.toString());
				IpAddress = props.getProperty("IpAddress");
				MacAddress = props.getProperty("MacAddress");

				if (sdFileIsExist) {
					in.close();
				}

				// 获取配置文件后启动Webservice
				executeWSAsyncTask();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private void executeWSAsyncTask() {
		if (StringUtil.isNullOrEmpty(IpAddress)
				|| StringUtil.isNullOrEmpty(MacAddress)) {
			getLocalMacAddressFromWifiManager(this);
		}
		if (StringUtil.isNullOrEmpty(IpAddress)
				|| StringUtil.isNullOrEmpty(MacAddress)) {
			return;
		}

		WSAsyncTask requestWSAsyncTask = new WSAsyncTask();
		requestWSAsyncTask
				.setOnWebServiceProgressListener(new OnWebServiceProgressListener() {
					@Override
					public void onProgress(SoapObject progress) {
						if (progress != null
								&& progress.hasProperty("getInfoResult")) {
							// TODO test
							// String strXML =
							// "<?xml version=\"1.0\" encoding=\"utf-8\"?><EpointNewDataSetRES><responsecode>00</responsecode><responseinfo></responseinfo><czlb>10001</czlb><Table><windowid>A252</windowid><WindowName>A252窗口</WindowName><Department>环保</Department></Table></EpointNewDataSetRES>";
							String strXML = progress
									.getPropertyAsString("getInfoResult");
							HashMap<String, String> propsMap = CustomXMLUtil
									.parserXML(strXML);
							String responsecode = propsMap.get("responsecode");
							String responseinfo = propsMap.get("responseinfo");
							String czlb = propsMap.get("czlb");

							windowId = propsMap.get("windowid");
							windowName = propsMap.get("WindowName");
							department = propsMap.get("Department");
						} else {
							windowId = "xml error";
							windowName = "xml error";
							department = "xml error";
						}
						mHandler.post(showWindowInfoRnnable);
					}
				});
		requestWSAsyncTask.setNameSpace(MainApplication.nameSpace);
		requestWSAsyncTask.setMethodName(MainApplication.methodName);
		requestWSAsyncTask.setServiceUrl(MainApplication.serviceUrl);
		HashMap<String, String> propsMap = new HashMap<String, String>();
		propsMap.put("CZLBID", "10001");
		HashMap<String, String> xmlPropsMap = new HashMap<String, String>();
		xmlPropsMap.put("IpAddress", IpAddress);
		xmlPropsMap.put("MacAddress", MacAddress);
		propsMap.put("XmlInfo", (new CustomXMLUtil()).createXML(xmlPropsMap));
		requestWSAsyncTask.setPropsMap(propsMap);
		requestWSAsyncTask.setContext(this);
		requestWSAsyncTask.execute();
	}

	private Runnable showWindowInfoRnnable = new Runnable() {
		public void run() {
			windowidTextView.setText(windowId);
			WindowNameTextView.setText(windowName);
			DepartmentTextView.setText(department);
		}
	};

	private void getLocalMacAddressFromWifiManager(Context context) {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		int ipAddress = info.getIpAddress();
		IpAddress = intToIp(ipAddress);
		MacAddress = info.getMacAddress();
		LogUtil.logd(TAG, "getLocalMacAddressFromWifiManager:IpAddress="+IpAddress+";MacAddress="+MacAddress);
	}

	private String intToIp(int i) {
		// return ((i >> 24) & 0xFF) + "." + ((i >> 16) & 0xFF) + "."
		// + ((i >> 8) & 0xFF) + "." + (i & 0xFF);
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + ((i >> 24) & 0xFF);
	}

	// ---------------------------- 功能分隔 ----------------------------------

	private static final String SOCKET_PROPS = "socket.properties";
	private Runnable loadSocketPropsRunnable = new Runnable() {
		public void run() {
			try {
				String sdPath = FileUtil.getSDPath();
				String sdFilePath = sdPath + "/" + SOCKET_PROPS;
				boolean sdFileIsExist = (sdPath != null)
						&& FileUtil.fileIsExists(sdFilePath);

				Properties props = new Properties();
				InputStream in = null;
				if (sdFileIsExist) {
					in = new FileInputStream(sdFilePath);
					props.load(in);
				} else {
					try {
						props.load(MainActivity.this.getAssets().open(SOCKET_PROPS));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				LogUtil.logd(TAG, "loadSocketProps:"+props.toString());
				socketPort = props.getProperty("socketPort");

				if (sdFileIsExist) {
					in.close();
				}

				// 获取配置文件后启动socketServer
				bindService(new Intent(MainActivity.this,
						SocketServerService.class), conn,
						Context.BIND_AUTO_CREATE);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder serviceBinder) {
			LogUtil.logd(TAG, "onServiceConnected");
			// 返回一个MsgService对象
			msgService = ((SocketServerService.MsgBinder) serviceBinder)
					.getService();
			// 注册回调接口来接收下载进度的变化
			msgService
					.setOnSocketProgressListener(new OnSocketProgressListener() {
						@Override
						public void onProgress(String progress) {
							// To check the socket info
							LogUtil.logd(TAG, "onServiceConnected.onProgress:"
									+ progress);
							if (StringUtil.isNullOrEmpty(progress)) {
								return;
							}
							toastMsg = progress;
							mHandler.post(showToastRunnable);

							if (lastProgress.contains("@sure")) {
								// @sure\n@60b69906-487e-4a93-92dd-30048492fcfb\n@over
								if (progress.length() > 1) {
									banJianGuid = progress.substring(1);
								}

								mHandler.post(startConfirmActivityRunnable);
							} else if (lastProgress.contains("@score")) {
								// @score\n@60b69906-487e-4a93-92dd-30048492fcfb\n@over
								if (progress.length() > 1) {
									banJianGuid = progress.substring(1);
								}

								mHandler.post(startEvaluateActivityRunnable);
							} else {
								// do nothing
							}
							lastProgress = progress;
						}
					});
			// 启动服务
			msgService.startServer();
		}
	};

	private Runnable startSocketServiceRunnable = new Runnable() {
		public void run() {
			Intent intent = new Intent(MainActivity.this,
					SocketServerService.class);
			bindService(intent, conn, Context.BIND_AUTO_CREATE);
		}
	};

	private Runnable stopSocketServiceRunnable = new Runnable() {
		public void run() {
			unbindService(conn);
		}
	};

	private void startWebsiteActivity() {
//		Intent intent = new Intent(MainActivity.this, WebsiteActivity.class);
//		intent.putExtra(ActivityBroadcastReceiver.PARAM_BANJIANGUID, banJianGuid);
//		startActivity(intent);
		Intent intent = new Intent(ActivityBroadcastReceiver.ACTION);
		intent.putExtra(ActivityBroadcastReceiver.PARAM_ACTIVITY, WebsiteActivity.class.getName());
		intent.putExtra(ActivityBroadcastReceiver.PARAM_BANJIANGUID, banJianGuid);
		sendBroadcast(intent);
	}

//	// FIXME del by jx on 20170408 start
//	private Runnable startConfirmActivityRunnable = new Runnable() {
//		public void run() {
////			Intent intent = new Intent(MainActivity.this, ConfirmActivity.class);
////			intent.putExtra(ActivityBroadcastReceiver.PARAM_BANJIANGUID, banJianGuid);
////			startActivity(intent);
//			Intent intent = new Intent(ActivityBroadcastReceiver.ACTION);
//			intent.putExtra(ActivityBroadcastReceiver.PARAM_ACTIVITY, ConfirmActivity.class.getName());
//			intent.putExtra(ActivityBroadcastReceiver.PARAM_BANJIANGUID, banJianGuid);
//			sendBroadcast(intent);
//		}
//	};
//
//	private Runnable startEvaluateActivityRunnable = new Runnable() {
//		public void run() {
////			Intent intent = new Intent(MainActivity.this, EvaluateActivity.class);
////			intent.putExtra(ActivityBroadcastReceiver.PARAM_BANJIANGUID, banJianGuid);
////			startActivity(intent);
//			Intent intent = new Intent(ActivityBroadcastReceiver.ACTION);
//			intent.putExtra(ActivityBroadcastReceiver.PARAM_ACTIVITY, EvaluateActivity.class.getName());
//			intent.putExtra(ActivityBroadcastReceiver.PARAM_BANJIANGUID, banJianGuid);
//			sendBroadcast(intent);
//		}
//	};
//	// del by jx on 20170408 end
}
