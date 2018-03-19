package com.yocmoon.evaluation.activity;

import java.util.HashMap;

import org.ksoap2.serialization.SoapObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yocmoon.evaluation.MainApplication;
import com.yocmoon.evaluation.R;
import com.yocmoon.evaluation.interfaces.OnWebServiceProgressListener;
import com.yocmoon.evaluation.receiver.ActivityBroadcastReceiver;
import com.yocmoon.evaluation.task.WSAsyncTask;
import com.yocmoon.evaluation.utils.CustomXMLUtil;
import com.yocmoon.evaluation.utils.StringUtil;

public class EvaluateActivity extends BaseActivity implements OnClickListener {
	private String TAG = EvaluateActivity.class.getSimpleName();

	private LinearLayout evlLinearLayout;
	private TextView notifyTextView;
	private Button evl1Button;
	private Button evl2Button;
	private Button evl3Button;
	private Button evl4Button;
	private Button evl5Button;
	private TextView evlDescTextView;
	private Button evlCfmButton;

	private String banJianGuid;
	private String evaluationCode;
	private String evaluationDesc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.evaluate);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			banJianGuid = extras.getString(ActivityBroadcastReceiver.PARAM_BANJIANGUID);
		}
		// TODO test
		// banJianGuid = "8b620387-2fbb-4fae-9af7-1484d158bcd2";
		// banJianGuid = "20c30e1b-f0ae-40ce-a8f4-5289a491e927";

		evlLinearLayout = (LinearLayout) findViewById(R.id.evl);
		notifyTextView = (TextView) findViewById(R.id.notify);
		evl1Button = (Button) findViewById(R.id.evl01);
		evl1Button.setOnClickListener(this);
		evl2Button = (Button) findViewById(R.id.evl02);
		evl2Button.setOnClickListener(this);
		evl3Button = (Button) findViewById(R.id.evl03);
		evl3Button.setOnClickListener(this);
		evl4Button = (Button) findViewById(R.id.evl04);
		evl4Button.setOnClickListener(this);
		evl5Button = (Button) findViewById(R.id.evl05);
		evl5Button.setOnClickListener(this);
		evlDescTextView = (TextView) findViewById(R.id.evaluation_tv);
		evlCfmButton = (Button) findViewById(R.id.evaluation_bt);
		evlCfmButton.setOnClickListener(this);

		// 初始化界面
		showEvaluateUI();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.evl01:
			evaluationCode = "5";
			evaluationDesc = evl1Button.getText().toString().trim();
			evlDescTextView.setText(evaluationDesc);
			evlCfmButton.setVisibility(View.VISIBLE);
			break;
		case R.id.evl02:
			evaluationCode = "4";
			evaluationDesc = evl2Button.getText().toString().trim();
			evlDescTextView.setText(evaluationDesc);
			evlCfmButton.setVisibility(View.VISIBLE);
			break;
		case R.id.evl03:
			evaluationCode = "3";
			evaluationDesc = evl3Button.getText().toString().trim();
			evlDescTextView.setText(evaluationDesc);
			evlCfmButton.setVisibility(View.VISIBLE);
			break;
		case R.id.evl04:
			evaluationCode = "2";
			evaluationDesc = evl4Button.getText().toString().trim();
			evlDescTextView.setText(evaluationDesc);
			evlCfmButton.setVisibility(View.VISIBLE);
			break;
		case R.id.evl05:
			evaluationCode = "1";
			evaluationDesc = evl5Button.getText().toString().trim();
			evlDescTextView.setText(evaluationDesc);
			evlCfmButton.setVisibility(View.VISIBLE);
			break;
		case R.id.evaluation_bt:
			// 异步执行调用WebService的任务
			executeWSAsyncTask();
			// TODO 暂时没有处理返回结果
			mHandler.post(showNotifyUIRunnable);
			mHandler.postDelayed(exitRunnable, 3 * 1000);
			break;
		default:
			break;
		}
	}

	private void executeWSAsyncTask() {
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
//						mHandler.post(showNotifyUIRunnable);
//						mHandler.postDelayed(exitRunnable, 3 * 1000);
					}
				});

		requestWSAsyncTask.setNameSpace(MainApplication.nameSpace);
		requestWSAsyncTask.setMethodName(MainApplication.methodName);
		requestWSAsyncTask.setServiceUrl(MainApplication.serviceUrl);
		HashMap<String, String> propsMap = new HashMap<String, String>();
		propsMap.put("CZLBID", "20001");
		HashMap<String, String> xmlPropsMap = new HashMap<String, String>();
		xmlPropsMap.put("BanJianGuid", banJianGuid);
		xmlPropsMap.put("Score", evaluationCode);
		xmlPropsMap.put("Suggestion", evaluationDesc);
		propsMap.put("XmlInfo", (new CustomXMLUtil()).createXML(xmlPropsMap));
		requestWSAsyncTask.setPropsMap(propsMap);
		requestWSAsyncTask.setContext(this);
		requestWSAsyncTask.execute();
	}

	private Runnable showNotifyUIRunnable = new Runnable() {
		public void run() {
			evlLinearLayout.setVisibility(View.GONE);
			notifyTextView.setVisibility(View.VISIBLE);
		}
	};

	private Runnable exitRunnable = new Runnable() {
		public void run() {
			// evlLinearLayout.setVisibility(View.GONE);
			// notifyTextView.setVisibility(View.GONE);
			finish();
		}
	};

	private void showEvaluateUI() {
		evlLinearLayout.setVisibility(View.VISIBLE);
		notifyTextView.setVisibility(View.GONE);
	}
}
