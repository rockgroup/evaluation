package com.yocmoon.evaluation.task;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.os.AsyncTask;

import com.yocmoon.evaluation.activity.BaseActivity;
import com.yocmoon.evaluation.interfaces.OnWebServiceProgressListener;
import com.yocmoon.evaluation.utils.LogUtil;
import com.yocmoon.evaluation.utils.StringUtil;

public class WSAsyncTask extends AsyncTask {
	private String TAG = WSAsyncTask.class.getSimpleName();

	private String nameSpace;
	private String methodName;
	private String serviceUrl;
	private HashMap<String, String> propsMap;
	private BaseActivity activity;
	private final String WEBSERVICE_EXCEPTION = "WebService Exception!";
	private final String NO_RESPONSE = "No Response!";

	/**
	 * 更新进度的回调接口
	 */
	private OnWebServiceProgressListener mOnWebServiceProgressListener;

	/**
	 * 注册回调接口的方法，供外部调用
	 * 
	 * @param mOnSocketProgressListener
	 */
	public void setOnWebServiceProgressListener(
			OnWebServiceProgressListener mOnWebServiceProgressListener) {
		this.mOnWebServiceProgressListener = mOnWebServiceProgressListener;
	}

	@Override
	protected Object doInBackground(Object... params) {
		LogUtil.logd(TAG, "doInBackground");
		String result = callWebService();
		if (WEBSERVICE_EXCEPTION.equals(result)) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			result = callWebService();
		}
		return null;
	}

	private String callWebService() {
		String result;
		try {
			// 实例化SoapObject 对象，指定WebService的命名空间和调用的方法名
			SoapObject request = new SoapObject(nameSpace, methodName);
			// 设置调用WebService接口需要传入的参数
			if (propsMap != null && !propsMap.isEmpty()) {
				Iterator<Entry<String, String>> it = propsMap.entrySet()
						.iterator();
				while (it.hasNext()) {
					Entry<String, String> entry = (Entry<String, String>) it
							.next();
					String key = entry.getKey();
					String value = entry.getValue();
					if (!StringUtil.isNullOrEmpty(key)
							&& !StringUtil.isNullOrEmpty(value)) {
						request.addProperty(key, value);
					}
				}
			}
			LogUtil.logd(TAG, "doInBackground.getRequest=" + request.toString());

			activity.toastMsg = request.toString();
			activity.mHandler.post(activity.showToastRunnable);
			
			// 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
			SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
					SoapEnvelope.VER12);
			// 跨平台非常重要，设置.net web service
			envelope.dotNet = true;
			envelope.bodyOut = request;
			// envelope.setOutputSoapObject(request);
			// // 注册Envelope(没有必要)
			// (new MarshalBase64()).register(envelope);
			// 构建传输对象，并指明WSDL文档URL
			HttpTransportSE ht = new HttpTransportSE(serviceUrl);
			ht.debug = true;
			// 调用WebService(其中参数为1：soapAction=命名空间+方法名称，2：Envelope对象)
			// ht.call(null, envelope);
			ht.call(nameSpace + methodName, envelope);
			// 解析返回数据
			if (envelope.getResponse() != null) {
				// 获取返回的数据
				SoapObject soapObject = (SoapObject) envelope.bodyIn;
				// 数据待处理
				mOnWebServiceProgressListener.onProgress(soapObject);
				result = soapObject.toString();
			} else {
				result = NO_RESPONSE;
			}
		} catch (Exception e) {
			result = WEBSERVICE_EXCEPTION;
		}
		LogUtil.logd(TAG, "doInBackground.getResponse=" + result);

		activity.toastMsg = result;
		activity.mHandler.post(activity.showToastRunnable);
		
		return result;
	}

	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public void setPropsMap(HashMap<String, String> propsMap) {
		this.propsMap = propsMap;
	}

	public void setContext(BaseActivity activity) {
		this.activity = activity;
	}
}