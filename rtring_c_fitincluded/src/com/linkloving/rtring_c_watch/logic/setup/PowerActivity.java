package com.linkloving.rtring_c_watch.logic.setup;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;

import com.eva.android.platf.std.DataLoadableActivity;
import com.eva.epc.core.dto.DataFromServer;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.PreferencesToolkits;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.logic.model.LocalInfoVO;

public class PowerActivity extends DataLoadableActivity{
	private static String TAG = PowerActivity.class.getSimpleName();
	
	private BLEProvider provider;
	private LocalInfoVO vo;
	private LPDeviceInfo lpDeviceInfo;
	private CheckBox PM1Switch = null;
	private CheckBox PM2Switch = null;
	private FrameLayout PM0_intro = null;
	private FrameLayout PM1_intro = null;
	private FrameLayout PM2_intro = null;
	private FrameLayout PM3_intro = null;
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		if (android.os.Build.MANUFACTURER.equalsIgnoreCase("meizu"))
			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏

		super.onCreate(savedInstanceState);
		provider = MyApplication.getInstance(PowerActivity.this).getCurrentHandlerProvider();
//		lpDeviceInfo = MyApplication.getInstance(PowerActivity.this).lpDeviceInfo;
		if(MyApplication.getInstance(PowerActivity.this).lpDeviceInfo==null){
			lpDeviceInfo = new LPDeviceInfo();
			lpDeviceInfo.deviceStatus=0;
		}else{
			lpDeviceInfo = MyApplication.getInstance(PowerActivity.this).lpDeviceInfo;
		}
		
			
	}
	
	
	@Override
	protected void initViews() {
		customeTitleBarResId = R.id.power_titleBar;
		// 首先设置contentview
		setContentView(R.layout.power_activity);
		this.setTitle(R.string.power_setting);
		
		PM1Switch = (CheckBox) findViewById(R.id.PM1_switch_checkbox);
		PM2Switch = (CheckBox) findViewById(R.id.PM2_switch_checkbox);
		PM0_intro = (FrameLayout) findViewById(R.id.PM0_intro);
		PM1_intro = (FrameLayout) findViewById(R.id.PM1_intro);
		PM2_intro = (FrameLayout) findViewById(R.id.PM2_intro);
		PM3_intro = (FrameLayout) findViewById(R.id.PM3_intro);
		refreshToView(null);
	}




	@Override
	protected void initListeners() {
		PM1Switch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PM2Switch.setChecked(false);
				setPower();
//				LPDeviceInfo deviceInfo = new LPDeviceInfo();
//				if(PM1Switch.isChecked())
//					deviceInfo.deviceStatus=1;
//				else
//					deviceInfo.deviceStatus=0;
				
//				provider.SetPower(PowerActivity.this, deviceInfo);
			}

		});
		
	PM2Switch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				PM1Switch.setChecked(false);
				setPower();
			}

		});
	}

	private void setPower() {
		
		if(PM1Switch.isChecked()){
			lpDeviceInfo.deviceStatus=1;
		}
		else if(PM2Switch.isChecked()){
			lpDeviceInfo.deviceStatus=2;
			
		}else if(!PM1Switch.isChecked() && !PM2Switch.isChecked()){
			lpDeviceInfo.deviceStatus=0;
		}
		
		PreferencesToolkits.updateLocalDeviceInfo(PowerActivity.this, lpDeviceInfo);
		updataView();
		
		provider.SetPower(PowerActivity.this, lpDeviceInfo);
	}
	
	private void updataView() {
		vo = PreferencesToolkits.getLocalDeviceInfo(PowerActivity.this);
		Log.i(TAG, "vo.deviceStatus:"+vo.deviceStatus);
		PM1Switch.setChecked(vo.deviceStatus==1);
		PM2Switch.setChecked(vo.deviceStatus==2);
		
		if(vo.deviceStatus==0 || vo.deviceStatus==-1){
			PM0_intro.setVisibility(View.VISIBLE);
			PM3_intro.setVisibility(View.VISIBLE);
			PM1_intro.setVisibility(View.GONE);
			PM2_intro.setVisibility(View.GONE);
		}else if(vo.deviceStatus==1){
			PM0_intro.setVisibility(View.GONE);
			PM1_intro.setVisibility(View.VISIBLE);
			PM2_intro.setVisibility(View.GONE);
			PM3_intro.setVisibility(View.VISIBLE);
		}else if(vo.deviceStatus==2){
			PM0_intro.setVisibility(View.GONE);
			PM1_intro.setVisibility(View.GONE);
			PM2_intro.setVisibility(View.VISIBLE);
			PM3_intro.setVisibility(View.VISIBLE);
		}
	}


	@Override
	protected DataFromServer queryData(String... arg0) {
//		DataFromServer dfs = new DataFromServer();
//		dfs.setSuccess(true);
		
		return null;
	}


	@Override
	protected void refreshToView(Object arg0) {
		updataView();
	}

}
