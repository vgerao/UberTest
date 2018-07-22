package com.uber.test.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.uber.test.R;

/**
 * Created by dell on 7/21/2018.
 */
public class DemoSpalshActivity extends AppCompatActivity {


	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		Window window = getWindow();
		window.setFormat(PixelFormat.RGBA_8888);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_spalsh);
	}


	@Override
	protected void onResume() {
		if(!isConnectingToInternet()){
			showAlertDialog(this, "", "Please check your internet connection");
		}else {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					Intent i = new Intent(DemoSpalshActivity.this,
							MainActivity.class);
					startActivity(i);
					finish();
				}
			}, 4 * 1000);
		}
		super.onResume();
	}

	public boolean isConnectingToInternet(){
		ConnectivityManager connectivity = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null)
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED)
					{
						return true;
					}

		}
		return false;
	}

	public void showAlertDialog(Context context, String title, String message) {
		final AlertDialog alertDialog = new AlertDialog.Builder(context).create();
		alertDialog.setTitle(title);
		alertDialog.setMessage(message);
		// Setting OK Button
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				alertDialog.dismiss();
				finish();
			}
		});

		alertDialog.show();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		return;
	}
}
