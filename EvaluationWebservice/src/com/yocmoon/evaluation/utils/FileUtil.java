package com.yocmoon.evaluation.utils;

import java.io.File;

import android.os.Environment;

public class FileUtil {
	/**
	 * <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"
	 * />
	 * 
	 * @return
	 */
	public static String getSDPath() {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (sdCardExist) {
			return Environment.getExternalStorageDirectory().toString();
		} else {
			return null;
		}
	}

	public static boolean fileIsExists(String filePath) {
		try {
//			filePath = "/storage/sdcard0/Manual/test.pdf";
			File f = new File(filePath);
			if (!f.exists()) {
				return false;
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
