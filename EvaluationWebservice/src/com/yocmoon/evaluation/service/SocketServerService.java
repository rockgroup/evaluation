package com.yocmoon.evaluation.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.yocmoon.evaluation.activity.MainActivity;
import com.yocmoon.evaluation.interfaces.OnSocketProgressListener;
import com.yocmoon.evaluation.utils.LogUtil;
import com.yocmoon.evaluation.utils.StringUtil;

public class SocketServerService extends Service {
	private String TAG = SocketServerService.class.getSimpleName();

	private List<Socket> mSocketList = null;
	private ServerSocket server = null;
	private ExecutorService mExecutorService = null;
	public boolean acceptFlag = true;

	/**
	 * 更新进度的回调接口
	 */
	private OnSocketProgressListener mOnSocketProgressListener;

	/**
	 * 注册回调接口的方法，供外部调用
	 * 
	 * @param mOnSocketProgressListener
	 */
	public void setOnSocketProgressListener(
			OnSocketProgressListener mOnSocketProgressListener) {
		this.mOnSocketProgressListener = mOnSocketProgressListener;
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
		public SocketServerService getService() {
			return SocketServerService.this;
		}
	}

	public void startServer() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					int port = StringUtil.toInteger(MainActivity.socketPort,
							7890);
					server = new ServerSocket(port);
					mSocketList = new ArrayList<Socket>();
					mExecutorService = Executors.newCachedThreadPool();
					LogUtil.logd(TAG, "Start server...");
					while (acceptFlag) {
						Socket clientSocket = server.accept();
						LogUtil.logd(TAG, "Accept a new clientSocket.");
						mExecutorService.execute(new ClientRunnable(
								mOnSocketProgressListener, mSocketList,
								clientSocket));
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (mSocketList != null && !mSocketList.isEmpty()) {
						int size = mSocketList.size();
						for (int i = 0; i < size; i++) {
							try {
								Socket mSocket = mSocketList.get(i);
								if (mSocket != null) {
									mSocket.close();
									mSocket = null;
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						mSocketList.clear();
					}

					if (server != null) {
						try {
							server.close();
							server = null;
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					LogUtil.logd(TAG, "End server...");
				}
			}
		}).start();
	}
}
