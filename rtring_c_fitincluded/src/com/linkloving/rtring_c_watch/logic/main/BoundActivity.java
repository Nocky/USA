package com.linkloving.rtring_c_watch.logic.main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.eva.android.widget.util.WidgetUtils;
import com.eva.android.widget.util.WidgetUtils.ToastType;
import com.eva.android.widgetx.AlertDialog;
import com.eva.android.x.BaseActivity;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.utils.ToolKits;

public class BoundActivity extends BaseActivity
{
	private Button scanBtn;
//	private Button listBtn;
	private Button skipBtn;
	
	private BLEProvider provider;
	
	//请求码：前往相机扫描
    public static final int REQUEST_CODE_CAMERA = 4;
    public static final int REQUEST_CODE_BLE_LIST = 5;
    
//    public LinearLayout helpLinLayout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bound);
		provider = MyApplication.getInstance(this).getCurrentHandlerProvider();
		initView();
		bindListener();
	}
	
	private void initView()
	{
		scanBtn = (Button) findViewById(R.id.button2);
//		listBtn = (Button) findViewById(R.id.button3);
		skipBtn = (Button) findViewById(R.id.button1);
//		helpLinLayout = (LinearLayout) findViewById(R.id.help_linear);
	}
	
	private void bindListener()
	{
//		helpLinLayout.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0)
//			{
//				startActivity(IntentFactory.createHelpActivityIntent(BoundActivity.this
//						, HelpActivity.FININSH_VIEWPAGE_FINISHACTIVITY, true));
//			}
//		});
         scanBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				// 网络畅通的情况下才能绑定（否则无法完成从服务端拿到utc时间等问题）
				if(ToolKits.isNetworkConnected(BoundActivity.this))
				{
					startActivityForResult(new Intent(BoundActivity.this, BLEListActivity.class),REQUEST_CODE_BLE_LIST);
				}
				else
				{
					AlertDialog dialog = new AlertDialog.Builder(BoundActivity.this)
					.setTitle(ToolKits.getStringbyId(BoundActivity.this, R.string.bound_failed))
					.setMessage(ToolKits.getStringbyId(BoundActivity.this, R.string.bound_failed_msg))
					.setPositiveButton(ToolKits.getStringbyId(BoundActivity.this, R.string.general_ok),new DialogInterface.OnClickListener() {
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
         
//         listBtn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v)
//			{
//				startActivityForResult(new Intent(BoundActivity.this, BLEListActivity.class),REQUEST_CODE_BLE_LIST);
//			}
//		});
//         
         skipBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
//				startActivity(IntentFactory.createPortalActivityIntent(BoundActivity.this));
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
		if(requestCode == REQUEST_CODE_CAMERA)
		{
//		
		}
		else if (requestCode == REQUEST_CODE_BLE_LIST)
		{
		     if(resultCode == Activity.RESULT_OK)
		     {
		    	setResult(Activity.RESULT_OK);
			    finish();
		     }else if(resultCode == BLEListActivity.RESULT_FAIL){
		    	MyApplication.getInstance(BoundActivity.this).releaseBLE();
		    	ToolKits.showCommonTosat(BoundActivity.this, false, ToolKits.getStringbyId(BoundActivity.this, R.string.portal_main_bound_failed), Toast.LENGTH_LONG);
				MyApplication.getInstance(BoundActivity.this).getCurrentHandlerProvider().setCurrentDeviceMac(null);
		     }else if(resultCode == BLEListActivity.RESULT_BACK){
		    	MyApplication.getInstance(BoundActivity.this).releaseBLE();
		    	MyApplication.getInstance(BoundActivity.this).getCurrentHandlerProvider().setCurrentDeviceMac(null);
		     }
		}
		
	}
}
