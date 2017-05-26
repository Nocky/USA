package com.linkloving.rtring_c_watch.logic.main;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.eva.android.x.BaseActivity;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.utils.ToolKits;

public class BoundStep1Activity extends BaseActivity
{
	private Button nextBtn; //下一步按钮
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
		setContentView(R.layout.activity_bound_step1);
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
//		helpLinLayout.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0)
//			{
//				startActivity(IntentFactory.createHelpActivityIntent(BoundActivity.this
//						, HelpActivity.FININSH_VIEWPAGE_FINISHACTIVITY, true));
//			}
//		});
		nextBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v)
			{
				//跳转到下一页：
				startActivityForResult(new Intent(BoundStep1Activity.this, BoundStep2Activity.class),BoundActivity.REQUEST_CODE_BLE_LIST);
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
	

	public void toBoundStep2Page() {
		// TODO Auto-generated method stub
		Intent nxtPageIntent = new Intent(BoundStep1Activity.this, BoundStep2Activity.class);
		startActivity(nxtPageIntent);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		 if (requestCode == REQUEST_CODE_BLE_LIST)
		{
		     if(resultCode == Activity.RESULT_OK)
		     {
		    	setResult(Activity.RESULT_OK);
			    finish();
		     }else if(resultCode == BLEListActivity.RESULT_FAIL){
		    	ToolKits.showCommonTosat(BoundStep1Activity.this, false, ToolKits.getStringbyId(BoundStep1Activity.this, R.string.portal_main_bound_failed), Toast.LENGTH_LONG);
		    	MyApplication.getInstance(BoundStep1Activity.this).releaseBLE();
				MyApplication.getInstance(BoundStep1Activity.this).getCurrentHandlerProvider().setCurrentDeviceMac(null);
		     }else if(resultCode == BLEListActivity.RESULT_NOCHARGE){
		    	ToolKits.showCommonTosat(BoundStep1Activity.this, false, ToolKits.getStringbyId(BoundStep1Activity.this, R.string.portal_main_bound_failed_nocharge), Toast.LENGTH_LONG);
			    MyApplication.getInstance(BoundStep1Activity.this).getCurrentHandlerProvider().setCurrentDeviceMac(null);
			    MyApplication.getInstance(BoundStep1Activity.this).releaseBLE();
			  }else if(resultCode == BLEListActivity.RESULT_BACK){
		    	MyApplication.getInstance(BoundStep1Activity.this).releaseBLE();
		    	MyApplication.getInstance(BoundStep1Activity.this).getCurrentHandlerProvider().setCurrentDeviceMac(null);
			  }else if(resultCode == BLEListActivity.RESULT_DISCONNECT){
				ToolKits.showCommonTosat(BoundStep1Activity.this, false, ToolKits.getStringbyId(BoundStep1Activity.this, R.string.portal_main_bound_failed_ble_dis), Toast.LENGTH_LONG);
			    MyApplication.getInstance(BoundStep1Activity.this).releaseBLE();
			    MyApplication.getInstance(BoundStep1Activity.this).getCurrentHandlerProvider().setCurrentDeviceMac(null);
			  }else if(resultCode == BLEListActivity.RESULT_OTHER){
				MyApplication.getInstance(BoundStep1Activity.this).releaseBLE();
				MyApplication.getInstance(BoundStep1Activity.this).getCurrentHandlerProvider().setCurrentDeviceMac(null);
			  }
		     
		}
		
	}
}
