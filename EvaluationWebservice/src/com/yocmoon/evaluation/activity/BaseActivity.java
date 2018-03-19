package com.yocmoon.evaluation.activity;

import java.util.HashMap;

import org.ksoap2.serialization.SoapObject;

import com.yocmoon.evaluation.MainApplication;
import com.yocmoon.evaluation.interfaces.OnWebServiceProgressListener;
import com.yocmoon.evaluation.receiver.ActivityBroadcastReceiver;
import com.yocmoon.evaluation.task.WSAsyncTask;
import com.yocmoon.evaluation.utils.CustomXMLUtil;
import com.yocmoon.evaluation.utils.StringUtil;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class BaseActivity extends Activity {

	public Handler mHandler = new Handler();
	public String toastMsg = "test";

	// FIXME add by jx on 20170408 start
	private String command;
	protected String banJianGuid;
	public static String IpAddress = "";
	public static String MacAddress = "";
	// add by jx on 20170408 end

	public Runnable showToastRunnable = new Runnable() {
		public void run() {
			if (MainApplication.showDebugLog) {
				Toast.makeText(BaseActivity.this, toastMsg, Toast.LENGTH_LONG)
						.show();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 防止休眠
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
        //设置无标题  
        requestWindowFeature(Window.FEATURE_NO_TITLE);  
//        //设置全屏  
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
//                WindowManager.LayoutParams.FLAG_FULLSCREEN); 
	}

	// FIXME add by jx on 20170408 start
	@Override
	protected void onResume() {
		super.onResume();
		
//		if (!StringUtil.isNullOrEmpty(MacAddress)) {
			// 2. 启动计时器
			timerHandler.postDelayed(timerRunnable, TIMER_VALUE);//每两秒执行一次runnable. 
//		}
	};

	@Override
	protected void onPause() {
		super.onPause();
		
//		if (!StringUtil.isNullOrEmpty(MacAddress)) {
			// 3. 停止计时器
			timerHandler.removeCallbacks(timerRunnable);
//		}
	};
	
	@Override
	protected void onDestroy() {
		// do nothing
		super.onDestroy();
	}

	private BaseActivity getCurrentActivity() {
		return this;
	}

	// 1. 定义一个Handler类
	private static Handler timerHandler = new Handler();
	private final static long TIMER_VALUE = 4000;
	private Runnable timerRunnable = new Runnable() {
	    @Override
	    public void run() {
	    	WSAsyncTask requestWSAsyncTask = new WSAsyncTask();
			requestWSAsyncTask
					.setOnWebServiceProgressListener(new OnWebServiceProgressListener() {
						@Override
						public void onProgress(SoapObject progress) {
							if (progress == null) {
								// do nothing
							} else if (progress.hasProperty("GetPJInfoResult")) {
								// TODO test
								// String strXML =
								// "<?xml version=\"1.0\" encoding=\"utf-8\"?><EpointNewDataSetRES><responsecode>00</responsecode><responseinfo></responseinfo><czlb>30002</czlb></EpointNewDataSetRES>";
								String strXML = progress
										.getPropertyAsString("GetPJInfoResult");
								HashMap<String, String> propsMap = CustomXMLUtil
										.parserXML(strXML);
								String responsecode = propsMap.get("responsecode");
								String responseinfo = propsMap.get("responseinfo");
								command = propsMap.get("COMMAND");
								banJianGuid = propsMap.get("GUID");
							} else {
								// do nothing
							}
//							// TODO 暂时没有处理返回结果
//							mHandler.postDelayed(exitRunnable, 3 * 1000);
							// add by jx on 20170408 start
							if ("SURE".equals(command)) {
								startConfirmActivity();
							} else if ("SCORE".equals(command)) {
								startEvaluateActivity();
//							} else if ("NO".equals(command)) {
//								startEvaluateActivity();
							} else {
								// "NO" or null, do nothing
							} 
							// add by jx on 20170408 end
						}
					});

			requestWSAsyncTask.setNameSpace(MainApplication.nameSpace);
			// FIXME
			requestWSAsyncTask.setMethodName(MainApplication.timerMethodName);
			requestWSAsyncTask.setServiceUrl(MainApplication.timerServiceUrl);
			HashMap<String, String> propsMap = new HashMap<String, String>();
			propsMap.put("MAC", MacAddress);
			requestWSAsyncTask.setPropsMap(propsMap);
			requestWSAsyncTask.setContext(getCurrentActivity());
			requestWSAsyncTask.execute();
	    	
	        timerHandler.postDelayed(this, TIMER_VALUE);
	    }
	};
//	// 2. 启动计时器
//	timerHandler.postDelayed(timerRunnable, 2000);//每两秒执行一次runnable. 
//	// 3. 停止计时器
//	timerHandler.removeCallbacks(timerRunnable);
	// add by jx on 20170408 end

	// FIXME add by jx on 20170408 start
	protected Runnable startConfirmActivityRunnable = new Runnable() {
		public void run() {
			startConfirmActivity();
		}
	};
	
	private void startConfirmActivity() {
//		Intent intent = new Intent(MainActivity.this, ConfirmActivity.class);
//		intent.putExtra(ActivityBroadcastReceiver.PARAM_BANJIANGUID, banJianGuid);
//		startActivity(intent);
		Intent intent = new Intent(ActivityBroadcastReceiver.ACTION);
		intent.putExtra(ActivityBroadcastReceiver.PARAM_ACTIVITY, ConfirmActivity.class.getName());
		intent.putExtra(ActivityBroadcastReceiver.PARAM_BANJIANGUID, banJianGuid);
		sendBroadcast(intent);
	}

	protected Runnable startEvaluateActivityRunnable = new Runnable() {
		public void run() {
			startEvaluateActivity();
		}
	};

	private void startEvaluateActivity() {
//		Intent intent = new Intent(MainActivity.this, EvaluateActivity.class);
//		intent.putExtra(ActivityBroadcastReceiver.PARAM_BANJIANGUID, banJianGuid);
//		startActivity(intent);
		Intent intent = new Intent(ActivityBroadcastReceiver.ACTION);
		intent.putExtra(ActivityBroadcastReceiver.PARAM_ACTIVITY, EvaluateActivity.class.getName());
		intent.putExtra(ActivityBroadcastReceiver.PARAM_BANJIANGUID, banJianGuid);
		sendBroadcast(intent);
	}
	// add by jx on 20170408 end
}
