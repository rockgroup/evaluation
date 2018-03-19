package com.yocmoon.evaluation.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ActivityBroadcastReceiver extends BroadcastReceiver {
	public static final String ACTION = "com.yocmoon.action.START_ACTIVITY";
	public static final String PARAM_ACTIVITY = "activity";
	public static final String PARAM_BANJIANGUID = "banJianGuid";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ACTION)) {
			try {
				String activity = intent.getStringExtra(PARAM_ACTIVITY);
				String banJianGuid = intent.getStringExtra(PARAM_BANJIANGUID);
				
				Class activityClass = Class.forName(activity);
				Intent ootStartIntent = new Intent(context, activityClass);
				ootStartIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				ootStartIntent.putExtra(PARAM_BANJIANGUID, banJianGuid);
				context.startActivity(ootStartIntent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}