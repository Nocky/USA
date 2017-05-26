package com.linkloving.rtring_c_watch.logic.setup;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eva.android.platf.std.DataLoadableActivity;
import com.eva.epc.common.util.CommonUtils;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.eva.epc.core.dto.SysActionConst;
import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEHandler.BLEProviderObserverAdapter;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.linkloving.rtring_c_watch.LocalUserSettingsToolkits;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.linkloving.rtring_c_watch.logic.main.adapter.AlarmAdapter;
import com.linkloving.rtring_c_watch.logic.model.LocalSetting;
import com.linkloving.rtring_c_watch.utils.SkinSettingManager;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.Alarm;
import com.rtring.buiness.logic.dto.JobDispatchConst;

public class AlarmActivity extends DataLoadableActivity
{

	private AlarmAdapter alarmAdapter;
	private ListView alarmListView;
	
//	private BLEHandler setDeviceInfoHandler;
	private BLEProviderObserverAdapter bleProviderObserver = null;
	private BLEProvider provider;
	private SkinSettingManager mSettingManager;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		if(android.os.Build.MANUFACTURER.equalsIgnoreCase("meizu"))
			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
		super.onCreate(savedInstanceState);
		
		bleProviderObserver = new BLEHandler.BLEProviderObserverAdapter(){
			@Override
			public void updateFor_notifyForDeviceAloneSyncSucess_D()
			{
			}

			@Override
			protected Activity getActivity()
			{
				return AlarmActivity.this;
			}

			@Override
			public void updateFor_handleSetTime() {
				
			}
		};
	}

	@Override
	protected void initViews()
	{
		customeTitleBarResId = R.id.alarm_activity_titleBar;
		// 首先设置contentview
		setContentView(R.layout.alarm_activity);

		this.setTitle(R.string.alarm_activity_manage_title);
		
		provider = MyApplication.getInstance(this).getCurrentHandlerProvider();

		alarmListView = (ListView) this.findViewById(R.id.alarm_activity_list_view);
		alarmAdapter = new AlarmAdapter(this,provider);
		alarmListView.setAdapter(alarmAdapter);
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		mSettingManager=new SkinSettingManager(this);
		mSettingManager.initSkins();
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
	protected void initListeners()
	{
	}

	@Override
	protected DataFromServer queryData(String... arg0)
	{
		//判断有无网络
//		if(ToolKits.isNetworkConnected(this))
//		{
//			return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService()
//					.sendObjToServer(DataFromClient.n()
//					.setProcessorId(MyProcessorConst.PROCESSOR_USERSETTING)
//					.setJobDispatchId(JobDispatchConst.USER_SETTINGS_ALARM)
//					.setActionId(SysActionConst.ACTION_APPEND3)
//					.setNewData(MyApplication.getInstance(this).getLocalUserInfoProvider().getUser_id()));
//		}
//		else
//		{
			DataFromServer dfs = new DataFromServer();
			dfs.setSuccess(true);
			
			JSONObject obj = new JSONObject();
			LocalSetting setting = LocalUserSettingsToolkits.getLocalSettingInfo(this, MyApplication.getInstance(this).getLocalUserInfoProvider().getUser_mail());
			String alarm_list = "";
			if(setting == null || CommonUtils.isStringEmpty(setting.getAlarm_list()))
			{
				String alarm_list_local = MyApplication.getInstance(AlarmActivity.this).getLocalUserInfoProvider().getAlarm_list();
				if(CommonUtils.isStringEmpty(alarm_list_local))
				{
					List<Alarm> alarmList = new ArrayList<Alarm>();
					//目前界面只有三个闹钟
					for(int i = 0; i < 3; i++)
					{
						Alarm a = new Alarm();
						a.setAlarmTime(0);
						a.setRepeat(0);
						a.setValid(0);
						alarmList.add(a);
					}
					alarm_list = JSON.toJSONString(alarmList);
				}
				else
					alarm_list = alarm_list_local;
			}
			else
			{
				alarm_list = setting.getAlarm_list();
			}
			
			obj.put("alarm_list", alarm_list);
			obj.put("alarm_update", setting == null ? 0 : setting.getAlarm_update());
			
			dfs.setReturnValue(obj.toJSONString());
			return dfs;
//		}


	}

	@Override
	protected void refreshToView(Object result)
	{
		JSONObject obj = JSON.parseObject((String)result);
		long alarm_update = obj.getLongValue("alarm_update");
		String alarm_list = "";
		LocalSetting setting = LocalUserSettingsToolkits.getLocalSettingInfo(this, MyApplication.getInstance(this).getLocalUserInfoProvider().getUser_mail());
		
		if(setting != null && !CommonUtils.isStringEmpty(setting.getAlarm_list()) && setting.getAlarm_update() >= alarm_update)
			alarm_list = setting.getAlarm_list();
		else
			alarm_list = obj.getString("alarm_list");
		ArrayList<Alarm> listData = (ArrayList<Alarm>) JSON.parseArray(alarm_list, Alarm.class);
		alarmAdapter.setListData(listData);
		alarmAdapter.notifyDataSetChanged();
	}
}
