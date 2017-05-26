package com.eva.android.x;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

public class BaseActivity extends Activity {
	private boolean allowDestroy = true;
	private View view;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AppManager.getAppManager().addActivity(this);
	}

	protected void onStart() {
		super.onStart();

	}

	protected void onStop() {
		super.onStop();

	}

	protected void onDestroy() {
		super.onDestroy();

		AppManager.getAppManager().removeActivity(this);
	}

	public void setAllowDestroy(boolean allowDestroy) {
		this.allowDestroy = allowDestroy;
	}

	public void setAllowDestroy(boolean allowDestroy, View view) {
		this.allowDestroy = allowDestroy;
		this.view = view;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == 4) && (this.view != null)) {
			this.view.onKeyDown(keyCode, event);
			if (!this.allowDestroy) {
				return false;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
}