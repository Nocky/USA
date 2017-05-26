package com.linkloving.rtring_c_watch.logic.main;

import java.io.File;

import com.eva.android.platf.std.DataLoadableActivity;
import com.eva.epc.core.dto.DataFromServer;
import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEHandler.BLEProviderObserverAdapter;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.linkloving.rtring_c_watch.utils.ZipUtil;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class DeviceInfoActivity extends DataLoadableActivity 
{
	private BLEProvider provider;
//	private BLEHandler deviceInfoHandler;
	
	private BLEProviderObserverAdapter bleProviderObserver = null;
	
	private TextView boundDeviceMac;
	private TextView boundDeviceName;
	private TextView boundDeviceInfo;
	
	private Button btn_share;
	
	@Override
	protected void onResume()
	{
		super.onResume();
		
//		provider = MyApplication.getInstance(this).getProvider(setDeviceInfoHandler);
		provider.setBleProviderObserver(bleProviderObserver);
	}
	
	protected void onDestroy()
	{
		super.onDestroy();
		
		if(provider.getBleProviderObserver() == bleProviderObserver)
			provider.setBleProviderObserver(null);
	}
	
	@Override
	protected void initViews()
	{
		customeTitleBarResId = R.id.activity_device_info_titleBar;
		setContentView(R.layout.activity_device_info);
		
		boundDeviceMac = (TextView) findViewById(R.id.textView2);
		boundDeviceName = (TextView) findViewById(R.id.textView4);
		boundDeviceInfo = (TextView) findViewById(R.id.textView6);
		btn_share = (Button) findViewById(R.id.btn_share);
		provider = MyApplication.getInstance(this).getCurrentHandlerProvider();
		bleProviderObserver = new BLEHandler.BLEProviderObserverAdapter(){
			@Override
			public void updateFor_notifyFor0x13ExecSucess_D(LPDeviceInfo latestDeviceInfo)
			{
				if(latestDeviceInfo != null)
					boundDeviceInfo.setText(latestDeviceInfo.toString());
			}
			
			@Override
			protected Activity getActivity()
			{
				return DeviceInfoActivity.this;
			}

		};
		provider.getAllDeviceInfoNew(this);
		boundDeviceMac.setText(provider.getCurrentDeviceMac());
		if(provider.getmBluetoothDevice() != null)
		    boundDeviceName.setText(provider.getmBluetoothDevice().getName());
		
		this.setTitle(ToolKits.getStringbyId(DeviceInfoActivity.this, R.string.device_info_title));
		this.setLoadDataOnCreate(false);
	}
	
	
	
	

	@Override
	protected void initListeners() {
		super.initListeners();
		btn_share.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try {
					ZipUtil.zipFolder("/sdcard/FitincludLog","/sdcard/FitincludedZip/FitincludLog.zip");
					showShare();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void showShare() {
		File file = new File("/sdcard/FitincludedZip/FitincludLog.zip"); //附件文件地址
		 Intent intent = new Intent(Intent.ACTION_SEND);
		 intent.putExtra("subject", file.getName());   //
		 intent.putExtra("body", "FactoryTest.zip - email sender"); //正文
		 intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file)); //添加附件，附件为file对象
		            if (file.getName().endsWith(".gz")) {
		                intent.setType("application/x-gzip"); //如果是gz使用gzip的mime
		            } else if (file.getName().endsWith(".txt")) {
		                intent.setType("text/plain"); //纯文本则用text/plain的mime
		            } else {
		                intent.setType("application/octet-stream"); //其他的均使用流当做二进制数据来发送
		            }
		  startActivity(intent); //调用系统的mail客户端进行发送
	}

	@Override
	protected DataFromServer queryData(String... arg0)
	{
		super.onResume();
		return null;
	}

	@Override
	protected void refreshToView(Object arg0)
	{
		
	}

}
