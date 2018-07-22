package com.uber.test.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.uber.test.R;

/**
 * Created by dell on 7/21/2018.
 */
public class MainActivity extends FragmentActivity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro_screen_fragments);
	}
}