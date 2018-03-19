package com.yocmoon.evaluation.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.ksoap2.serialization.SoapObject;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.yocmoon.evaluation.MainApplication;
import com.yocmoon.evaluation.activity.MainActivity;
import com.yocmoon.evaluation.interfaces.OnWebServiceProgressListener;
import com.yocmoon.evaluation.task.WSAsyncTask;
import com.yocmoon.evaluation.utils.CustomXMLUtil;
import com.yocmoon.evaluation.utils.LogUtil;
import com.yocmoon.evaluation.utils.StringUtil;

public class TimerClientService extends Service {
	private String TAG = TimerClientService.class.getSimpleName();

	/**
	 * 更新进度的回调接口
	 */
	private OnWebServiceProgressListener mOnWebServiceProgressListener;

	Intent intent;
	
	/**
	 * 注册回调接口的方法，供外部调用
	 * 
	 * @param mOnWebServiceProgressListener
	 */
	public void setOnWebServiceProgressListener(
			OnWebServiceProgressListener mOnWebServiceProgressListener) {
		this.mOnWebServiceProgressListener = mOnWebServiceProgressListener;
	}

	// @Override
	// public void onCreate() {
	// super.onCreate();
	// }

	public int onStartCommand(Intent intent, int flags, int startId) { 
		// 手动返回START_STICKY，亲测当service因内存不足被kill，当内存又有的时候，service又被重新创建
	    flags = START_STICKY;  
	    return super.onStartCommand(intent, flags, startId);  
	}  

	@Override
	public IBinder onBind(Intent intent) {
		LogUtil.logd(TAG, "onBind");
		return new MsgBinder();
	}

	// @Override
	// public void onDestroy() {
	// super.onDestroy();
	// }

	public class MsgBinder extends Binder {
		public TimerClientService getService() {
			return TimerClientService.this;
		}
	}

	public void startServer() {
//		// 2. 启动计时器
//		handler.postDelayed(runnable, 2000);//每两秒执行一次runnable. 
	}
	
	// 1. 定义一个Handler类
	final Handler handler = new Handler();
	Runnable runnable = new Runnable() {
	    @Override
	    public void run() {
	        // TODO Auto-generated method stub
	        //要做的事情
//	    	mOnWebServiceProgressListener.onProgress(new SoapObject());
	    	
	    	WSAsyncTask requestWSAsyncTask = new WSAsyncTask();
			requestWSAsyncTask
					.setOnWebServiceProgressListener(new OnWebServiceProgressListener() {
						@Override
						public void onProgress(SoapObject progress) {
							if (progress != null
									&& progress.hasProperty("getInfoResult")) {
								// TODO test
								// String strXML =
								// "<?xml version=\"1.0\" encoding=\"utf-8\"?><EpointNewDataSetRES><responsecode>00</responsecode><responseinfo></responseinfo><czlb>30002</czlb></EpointNewDataSetRES>";
								String strXML = progress
										.getPropertyAsString("getInfoResult");
								HashMap<String, String> propsMap = CustomXMLUtil
										.parserXML(strXML);
								String responsecode = propsMap.get("responsecode");
								String responseinfo = propsMap.get("responseinfo");
								String czlb = propsMap.get("czlb");
							}
//							// TODO 暂时没有处理返回结果
//							mHandler.postDelayed(exitRunnable, 3 * 1000);
							mOnWebServiceProgressListener.onProgress(progress);
						}
					});

			requestWSAsyncTask.setNameSpace(MainApplication.nameSpace);
			requestWSAsyncTask.setMethodName(MainApplication.methodName);
			requestWSAsyncTask.setServiceUrl(MainApplication.serviceUrl);
			HashMap<String, String> propsMap = new HashMap<String, String>();
			propsMap.put("MAC", "???TODO???");
			requestWSAsyncTask.setPropsMap(propsMap);
//			requestWSAsyncTask.setContext(intent);
			requestWSAsyncTask.execute();
	    	
	        handler.postDelayed(this, 2000);
	    }
	};
//	// 2. 启动计时器
//	handler.postDelayed(runnable, 2000);//每两秒执行一次runnable. 
//	// 3. 停止计时器
//	handler.removeCallbacks(runnable);
}
