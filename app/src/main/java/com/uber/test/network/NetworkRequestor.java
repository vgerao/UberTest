package com.uber.test.network;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.uber.test.util.CommonUtility;
import com.uber.test.util.UpdateUIHandler;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by dell on 7/21/2018.
 */
public class NetworkRequestor extends AsyncTask<String, String, String>{

	String response;
	private Context context;
	private static final int CONNECTION_TIMEOUT = 15000;
	private static final int DATARETRIEVAL_TIMEOUT = 60000;
	private ProgressDialogFragment progressDialogFragment;
	private UpdateUIHandler uiHandler;

	public NetworkRequestor(Context context, UpdateUIHandler uiHandler){
		this.context = context;
		this.uiHandler = uiHandler;
	}

	@Override
	protected String doInBackground(String... params) {
		HttpURLConnection con = null;
		try {
			URL obj = new URL(params[0]);
			con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod(params[1]);
			StringBuffer sb = new StringBuffer();
			int responseCode = con.getResponseCode();
			Log.i("Sending:", params[0]);
			Log.i("Response Code : ", ""+responseCode);

			if(responseCode != HttpURLConnection.HTTP_OK){
				response = null;
			}else {
				InputStream in = new BufferedInputStream(con.getInputStream());
				response = CommonUtility.convertInputStreamToString(in);
				in.close();
				Log.i("Response", response.toString());
			}
		}catch (Exception e){
			response = e.getMessage();
		}finally {
			if(null != con) {
				con.disconnect();
			}
		}

		return response;
	}

	@Override
	protected void onPostExecute(String result) {
		uiHandler.updateUI(result);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
	}
}