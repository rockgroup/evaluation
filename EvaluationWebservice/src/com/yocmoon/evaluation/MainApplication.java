package com.yocmoon.evaluation;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import android.app.Application;

import com.yocmoon.evaluation.utils.FileUtil;
import com.yocmoon.evaluation.utils.LogUtil;

public class MainApplication extends Application {
	private String TAG = MainApplication.class.getSimpleName();

	public static String nameSpace;
	public static String methodName;
	public static String serviceUrl;
	// FIXME add by jx on 20170408 start
	public static String timerMethodName;
	public static String timerServiceUrl;
	// add by jx on 20170408 end
	
	public static String websiteUrl;
	
	// TODO debug
//	public static boolean isDebug = true;
	public static boolean showDebugLog = true;

	@Override
	public void onCreate() {
		super.onCreate();

		// 异步执行加载WebserviceProps的任务
		new Thread(loadWebservicePropsRunnable).start();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	private static final String WEBSERVICE_PROPS = "webservice.properties";
	private Runnable loadWebservicePropsRunnable = new Runnable() {
		public void run() {
			try {
				String sdPath = FileUtil.getSDPath();
				String sdFilePath = sdPath + "/" + WEBSERVICE_PROPS;
				boolean sdFileIsExist = (sdPath != null)
						&& FileUtil.fileIsExists(sdFilePath);

				Properties props = new Properties();
				InputStream in = null;
				if (sdFileIsExist) {
					in = new FileInputStream(sdFilePath);
					props.load(in);
				} else {
					try {
						props.load(MainApplication.this.getAssets().open(
								WEBSERVICE_PROPS));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				LogUtil.logd(TAG, "loadWebserviceProps:"+props.toString());
				nameSpace = props.getProperty("nameSpace");
				methodName = props.getProperty("methodName");
				serviceUrl = props.getProperty("serviceUrl");
				// FIXME add by jx on 20170408 start
				timerMethodName = props.getProperty("timerMethodName");
				timerServiceUrl = props.getProperty("timerServiceUrl");
				// add by jx on 20170408 end
				
				websiteUrl = props.getProperty("websiteUrl");

				if (sdFileIsExist) {
					in.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
}
