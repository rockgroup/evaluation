package com.yocmoon.evaluation.interfaces;

import org.ksoap2.serialization.SoapObject;

public interface OnWebServiceProgressListener {

	void onProgress(SoapObject progress);

}
