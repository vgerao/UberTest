package com.uber.test.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.uber.test.R;

/**
 * Created by dell on 7/21/2018.
 */
public class ProgressDialogFragment extends DialogFragment {

	private Dialog mDialog;

	public ProgressDialogFragment() {
	}

	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		mDialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar){
			@Override
			public void onBackPressed() {
				if(isCancelable()){
					dismissAllowingStateLoss();
				}
			}
		};
		mDialog.setContentView(R.layout.layout_progress);
	    return mDialog;
	}

	public boolean isShowing(){
		return mDialog.isShowing();
	}


	public boolean isDialogVisible(){
		return mDialog != null && mDialog.isShowing();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {

	}

	@Override
	public void show(FragmentManager manager, String tag) {
		FragmentTransaction lFragmentTransaction = manager.beginTransaction();
		lFragmentTransaction.add(this, tag);
		lFragmentTransaction.commitAllowingStateLoss();
	}

}