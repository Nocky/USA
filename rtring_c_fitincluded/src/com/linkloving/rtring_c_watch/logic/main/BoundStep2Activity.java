package com.linkloving.rtring_c_watch.logic.main;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.eva.android.widgetx.AlertDialog;
import com.eva.android.x.BaseActivity;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.utils.ToolKits;

public class BoundStep2Activity extends BaseActivity
{
	private Button nextBtn;
//	private Button listBtn;
	private Button skipBtn;
	
	private BLEProvider provider;
	
    public static final int REQUEST_CODE_BLE_LIST = 5;
    
//    public LinearLayout helpLinLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bound_step2);
		provider = MyApplication.getInstance(this).getCurrentHandlerProvider();
		initView();
		bindListener();
	}
	
	private void initView()
	{
		nextBtn = (Button) findViewById(R.id.button2); 
		skipBtn = (Button) findViewById(R.id.button1);
		
		
//		helpLinLayout = (LinearLayout) findViewById(R.id.help_linear);
	}
	
	private void bindListener()
	{
		nextBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				// 网络畅通的情况下才能绑定（否则无法完成从服务端拿到utc时间等问题）
				if(ToolKits.isNetworkConnected(BoundStep2Activity.this))
				{
					startActivityForResult(new Intent(BoundStep2Activity.this, BLEListActivity.class),REQUEST_CODE_BLE_LIST);
				}
				else
				{
					AlertDialog dialog = new AlertDialog.Builder(BoundStep2Activity.this)
					.setTitle(ToolKits.getStringbyId(BoundStep2Activity.this, R.string.bound_failed))
					.setMessage(ToolKits.getStringbyId(BoundStep2Activity.this, R.string.bound_failed_msg))
					.setPositiveButton(ToolKits.getStringbyId(BoundStep2Activity.this, R.string.general_ok),new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();	
						}
					})
					.create();
					dialog.show();
				}
			}
		});	
         
         skipBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == BoundActivity.REQUEST_CODE_BLE_LIST){
			setResult(resultCode, data);
			finish();
		}
	}
}
