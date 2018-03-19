package com.yocmoon.evaluation.activity;

import java.util.HashMap;

import org.ksoap2.serialization.SoapObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

import com.yocmoon.evaluation.MainApplication;
import com.yocmoon.evaluation.R;
import com.yocmoon.evaluation.interfaces.OnWebServiceProgressListener;
import com.yocmoon.evaluation.receiver.ActivityBroadcastReceiver;
import com.yocmoon.evaluation.task.WSAsyncTask;
import com.yocmoon.evaluation.utils.CustomXMLUtil;
import com.yocmoon.evaluation.utils.StringUtil;

public class ConfirmActivity extends BaseActivity implements OnClickListener {
	private String TAG = ConfirmActivity.class.getSimpleName();

	private WebView confirmInfoWebView;
	private Button confirmYButton;
	private Button confirmNButton;

	// 获取确认信息
	private String confirmHtml;
	// 提交确认结果
	private String banJianGuid;
	private String confirmCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.confirm);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			banJianGuid = extras.getString(ActivityBroadcastReceiver.PARAM_BANJIANGUID);
		}
		// TODO test
		// banJianGuid = "8b620387-2fbb-4fae-9af7-1484d158bcd2";
		// banJianGuid = "20c30e1b-f0ae-40ce-a8f4-5289a491e927";

		confirmInfoWebView = (WebView) findViewById(R.id.confirmInfo);
		confirmInfoWebView.setScrollBarStyle((View.SCROLLBARS_INSIDE_OVERLAY));
		// confirmInfoWebView.getSettings().setBuiltInZoomControls(true);
		confirmYButton = (Button) findViewById(R.id.confirmY);
		confirmYButton.setOnClickListener(this);
		confirmNButton = (Button) findViewById(R.id.confirmN);
		confirmNButton.setOnClickListener(this);

		// 初始化界面
		enableButton(false);

		// 异步执行调用WebService的任务
		executeGetConfirmInfoWSAsyncTask();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.confirmY:
			confirmCode = "Y";
			break;
		case R.id.confirmN:
			confirmCode = "N";
			break;
		default:
			break;
		}

		// 异步执行调用WebService的任务
		executeConfirmWSAsyncTask();
		// TODO 暂时没有处理返回结果
		mHandler.postDelayed(exitRunnable, 3 * 1000);
	}

	private void executeGetConfirmInfoWSAsyncTask() {
		if (StringUtil.isNullOrEmpty(banJianGuid)) {
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
							// confirmHtml =
							// "<div><table align=\"center\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: 宋体;font-size: 9pt; color: #000000; border-collapse: collapse;\" border=\"0\" width=\"696\"><tr height=\"36px\"><td style=\"border-left: #000000 1px solid; border-right: #000000 1px solid; border-top: #000000 1px solid;border-bottom: #000000 1px solid; font-family: 仿宋; font-size: 14pt; text-align: center\">事项名称</td><td style=\"border-right: #000000 1px solid; border-top: #000000 1px solid; border-bottom: #000000 1px solid;font-family: 仿宋; font-size: 14pt; text-align: left\" colspan=\"3\">&nbsp;九龙新寓13号门禁对讲 物业维修资金使用的审核(商品房维修首付款办理)</td></tr><tr height=\"34px\"><td style=\"border-left: #000000 1px solid; border-right: #000000 1px solid; border-top: #000000 1px solid;border-bottom: #000000 1px solid; font-family: 仿宋; font-size: 14pt; text-align: center\">办件序号</td><td style=\"border-right: #000000 1px solid; border-top: #000000 1px solid; border-bottom: #000000 1px solid;font-family: 仿宋; font-size: 14pt; text-align: left\">&nbsp;JS13SP09J01338</td><td style=\"border-right: #000000 1px solid; border-top: #000000 1px solid; border-bottom: #000000 1px solid;font-family: 仿宋; font-size: 14pt; text-align: center\">受理时间</td><td style=\"border-right: #000000 1px solid; border-top: #000000 1px solid; border-bottom: #000000 1px solid;font-family: 仿宋; font-size: 14pt; text-align: left\">&nbsp;2014-12-18</td></tr><tr height=\"34px\"><td style=\"border-left: #000000 1px solid; border-right: #000000 1px solid; border-top: #000000 1px solid;border-bottom: #000000 1px solid; font-family: 仿宋; font-size: 14pt; text-align: center\">办件类型</td><td style=\"border-top: #000000 1px solid; border-bottom: #000000 1px solid; border-right: #000000 1px solid;font-family: 仿宋; font-size: 14pt; text-align: left\">&nbsp;承诺件</td><td style=\"border-left: #000000 1px solid; border-right: #000000 1px solid; border-top: #000000 1px solid;border-bottom: #000000 1px solid; font-family: 仿宋; font-size: 14pt; text-align: center\">是否加急</td><td style=\"border-right: #000000 1px solid; border-top: #000000 1px solid; border-bottom: #000000 1px solid;font-family: 仿宋; font-size: 14pt; text-align: left\">&nbsp;</td></tr><tr height=\"34px\"><td style=\"border-left: #000000 1px solid; border-right: #000000 1px solid; border-top: #000000 1px solid;border-bottom: #000000 1px solid; font-family: 仿宋; font-size: 14pt; text-align: center\">承诺时限</td><td style=\"border-right: #000000 1px solid; border-top: #000000 1px solid; border-bottom: #000000 1px solid;font-family: 仿宋; font-size: 14pt; text-align: left\">&nbsp;5(工作日)</td><td style=\"border-right: #000000 1px solid; border-top: #000000 1px solid; border-bottom: #000000 1px solid;font-family: 仿宋; font-size: 14pt; text-align: center\">法定时限</td><td style=\"border-right: #000000 1px solid; border-top: #000000 1px solid; border-bottom: #000000 1px solid;font-family: 仿宋; font-size: 14pt; text-align: left\">&nbsp;10(工作日)</td></tr><tr height=\"34px\"><td style=\"border-left: #000000 1px solid; border-right: #000000 1px solid; border-top: #000000 1px solid;border-bottom: #000000 1px solid; font-family: 仿宋; font-size: 14pt; text-align: center\">申请人</td><td style=\"border-right: #000000 1px solid; border-top: #000000 1px solid; border-bottom: #000000 1px solid;font-family: 仿宋; font-size: 14pt; text-align: left\">&nbsp;郭元康</td><td style=\"border-right: #000000 1px solid; border-top: #000000 1px solid; border-bottom: #000000 1px solid;font-family: 仿宋; font-size: 14pt; text-align: center\">身份证号</td><td style=\"border-right: #000000 1px solid; border-top: #000000 1px solid; border-bottom: #000000 1px solid;font-family: 仿宋; font-size: 14pt; text-align: left\">&nbsp;321083196903163737</td></tr><tr height=\"34px\"><td style=\"border-left: #000000 1px solid; border-right: #000000 1px solid; border-top: #000000 1px solid;border-bottom: #000000 1px solid; font-family: 仿宋; font-size: 14pt; text-align: center\">联系电话</td><td style=\"border-right: #000000 1px solid; border-top: #000000 1px solid; border-bottom: #000000 1px solid;font-family: 仿宋; font-size: 14pt; text-align: left\">&nbsp;15895891443</td><td style=\"border-right: #000000 1px solid; border-top: #000000 1px solid; border-bottom: #000000 1px solid;font-family: 仿宋; font-size: 14pt; text-align: center\">固定电话</td><td style=\"border-right: #000000 1px solid; border-top: #000000 1px solid; border-bottom: #000000 1px solid;font-family: 仿宋; font-size: 14pt; text-align: left\">&nbsp;</td></tr><tr height=\"34px\"><td style=\"border-left: #000000 1px solid; border-right: #000000 1px solid; border-top: #000000 1px solid;border-bottom: #000000 1px solid; font-family: 仿宋; font-size: 14pt; text-align: center\">联系地址</td><td style=\"border-right: #000000 1px solid; border-top: #000000 1px solid; border-bottom: #000000 1px solid;text-align: left; font-family: 仿宋; font-size: 14pt;\" colspan=\"3\">&nbsp;大明路210号</td></tr><tr height=\"34px\"><td style=\"border-left: #000000 1px solid; border-right: #000000 1px solid; border-top: #000000 1px solid; border-bottom: #000000 1px solid; font-family: 仿宋; font-size: 14pt; text-align: center\">申报单位</td><td style=\"border-right: #000000 1px solid; border-top: #000000 1px solid; border-bottom: #000000 1px solid;text-align: left; font-family: 仿宋; font-size: 14pt;\" colspan=\"3\">&nbsp;南京市九龙雅苑住宅小区业主委员会</td></tr><tr height=\"211px\"><td style=\"border-left: #000000 1px solid; border-right: #000000 1px solid; border-top: #000000 1px solid; border-bottom: #000000 1px solid; font-family: 仿宋; font-size: 14pt; text-align: center\">申报材料</td><td style=\"border-right: #000000 1px solid; border-left: #000000 1px solid; border-top: #000000 1px solid;border-bottom: #000000 1px solid; font-family: 仿宋; font-size: 14pt; text-align: left\"colspan=\"3\" valign=\"top\"></td></tr></table></div>";
							confirmHtml = progress
									.getPropertyAsString("getInfoResult");
						}
						mHandler.post(showConfirmInfoRunnable);
					}
				});

		requestWSAsyncTask.setNameSpace(MainApplication.nameSpace);
		requestWSAsyncTask.setMethodName(MainApplication.methodName);
		requestWSAsyncTask.setServiceUrl(MainApplication.serviceUrl);
		HashMap<String, String> propsMap = new HashMap<String, String>();
		propsMap.put("CZLBID", "30001");
		HashMap<String, String> xmlPropsMap = new HashMap<String, String>();
		xmlPropsMap.put("BanJianGuid", banJianGuid);
		propsMap.put("XmlInfo", (new CustomXMLUtil()).createXML(xmlPropsMap));
		requestWSAsyncTask.setPropsMap(propsMap);
		requestWSAsyncTask.setContext(this);
		requestWSAsyncTask.execute();
	}

	private void executeConfirmWSAsyncTask() {
		if (StringUtil.isNullOrEmpty(banJianGuid)) {
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
							// "<?xml version=\"1.0\" encoding=\"utf-8\"?><EpointNewDataSetRES><responsecode>00</responsecode><responseinfo></responseinfo><czlb>30002</czlb></EpointNewDataSetRES>";
							String strXML = progress
									.getPropertyAsString("getInfoResult");
							HashMap<String, String> propsMap = CustomXMLUtil
									.parserXML(strXML);
							String responsecode = propsMap.get("responsecode");
							String responseinfo = propsMap.get("responseinfo");
							String czlb = propsMap.get("czlb");
						}
//						// TODO 暂时没有处理返回结果
//						mHandler.postDelayed(exitRunnable, 3 * 1000);
					}
				});

		requestWSAsyncTask.setNameSpace(MainApplication.nameSpace);
		requestWSAsyncTask.setMethodName(MainApplication.methodName);
		requestWSAsyncTask.setServiceUrl(MainApplication.serviceUrl);
		HashMap<String, String> propsMap = new HashMap<String, String>();
		propsMap.put("CZLBID", "30002");
		HashMap<String, String> xmlPropsMap = new HashMap<String, String>();
		xmlPropsMap.put("BanJianGuid", banJianGuid);
		xmlPropsMap.put("ComfirmResult", confirmCode);
		propsMap.put("XmlInfo", (new CustomXMLUtil()).createXML(xmlPropsMap));
		requestWSAsyncTask.setPropsMap(propsMap);
		requestWSAsyncTask.setContext(this);
		requestWSAsyncTask.execute();
	}

	private Runnable exitRunnable = new Runnable() {
		public void run() {
			finish();
		}
	};

	private Runnable showConfirmInfoRunnable = new Runnable() {
		public void run() {
			showConfirmInfo();
		}
	};

	private void showConfirmInfo() {
		// confirmInfoWebView.setText(Html.fromHtml(confirmHtml));
		confirmInfoWebView.loadDataWithBaseURL("", confirmHtml, "text/html",
				"utf-8", null);
		enableButton(true);
	}

	private void enableButton(boolean enable) {
//		confirmYButton.setEnabled(enable);
//		confirmNButton.setEnabled(enable);
		// TODO 默认显示确认按钮
		confirmYButton.setEnabled(true);
		confirmNButton.setEnabled(true);
	}
}
