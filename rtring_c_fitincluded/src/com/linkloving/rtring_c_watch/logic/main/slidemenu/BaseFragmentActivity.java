package com.linkloving.rtring_c_watch.logic.main.slidemenu;



import com.eva.android.x.AppManager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public abstract class BaseFragmentActivity extends FragmentActivity {
	// Handler handler = new Handler();
	
	public BaseFragmentActivity()
	{
		super();
	}
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppManager.getAppManager().removeActivity(this);
	}



	@Override
	public void onCreate(Bundle savedInstanceState) {
		try
		{
			super.onCreate(savedInstanceState);
			AppManager.getAppManager().addActivity(this);
			init();
			setContentView(getLayoutResID());
			initView();
			setAdapter();
			bindListener();
		}
		catch (Exception e)
		{
			Log.w(BaseFragmentActivity.class.getSimpleName(), e.getMessage(), e);
		}
	}

	protected void init() {

	}

	public void closeInputMethod(View view) {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}

	protected abstract int getLayoutResID();

	protected abstract void initView();

	protected abstract void setAdapter();

	protected abstract void bindListener();
	
}
