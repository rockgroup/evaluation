package com.yocmoon.evaluation.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.yocmoon.evaluation.interfaces.OnSocketProgressListener;
import com.yocmoon.evaluation.utils.LogUtil;

public class ClientRunnable implements Runnable {
	private String TAG = ClientRunnable.class.getSimpleName();

	private OnSocketProgressListener mOnSocketProgressListener;
	private List<Socket> mSocketList;
	private Socket mSocket;
	private BufferedReader in = null;
	private BufferedWriter out = null;
	private String msg = "";
	private static final String SOCKET_EXIT = "exit";

	public ClientRunnable(OnSocketProgressListener mOnSocketProgressListener,
			List<Socket> mSocketList, Socket mSocket) {
		this.mOnSocketProgressListener = mOnSocketProgressListener;
		this.mSocketList = mSocketList;
		this.mSocket = mSocket;
	}

	@Override
	public void run() {
		try {
			mSocketList.add(mSocket);
			
			in = new BufferedReader(new InputStreamReader(
					mSocket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(
					mSocket.getOutputStream()));

			LogUtil.logd(TAG, "Start listening...");
			while (true) {
				if ((msg = in.readLine()) != null) {
					LogUtil.logd(TAG, "ReadLine:" + msg);
					if (SOCKET_EXIT.equals(msg)) {
						break;
					} else {
						mOnSocketProgressListener.onProgress(msg);
					}
				} else {
					break;
				}
			}
			LogUtil.logd(TAG, "End listening...");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			exit();
		}
	}

	public void exit() {
		try {
			if (mSocketList != null) {
				mSocketList.remove(mSocket);
			}
			if (in != null) {
				in.close();
				in = null;
			}
			if (out != null) {
				out.close();
				out = null;
			}
			if (mSocket != null) {
				mSocket.close();
				mSocket = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
