package com.linkloving.rtring_c_watch.logic.setup;

import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.eva.android.platf.std.AutoUpdateDaemon;
import com.eva.android.platf.std.DataLoadableActivity;
import com.eva.android.widget.ChoiceItemPopWindow;
import com.eva.android.widget.DataLoadingAsyncTask;
import com.eva.epc.common.util.CommonUtils;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.eva.epc.core.dto.SysActionConst;
import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEHandler.BLEProviderObserverAdapter;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.google.gson.Gson;
import com.linkloving.rtring_c_watch.LocalUserSettingsToolkits;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.linkloving.rtring_c_watch.logic.model.LocalSetting;
import com.linkloving.rtring_c_watch.utils.DeviceInfoHelper;
import com.linkloving.rtring_c_watch.utils.SkinSettingManager;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.linkloving.utils._Utils;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.JobDispatchConst;
import com.rtring.buiness.logic.dto.LongSit;
import com.rtring.buiness.logic.dto.UserEntity;

public class LongSitActivity extends DataLoadableActivity
{
	private LinearLayout timeLL = null;
	private LinearLayout startTimeOneLL = null;
	private LinearLayout endTimeOneLL = null;
	private LinearLayout startTimeTwoLL = null;
	private LinearLayout endTimeTwoLL = null;

	private TextView startTime1;
	private TextView endTime1;
	private TextView startTime2;
	private TextView endTime2;
	
	private TextView longSitStep;
	
	private CheckBox longsitSwitch = null;
	// private Button saveBtn = null;
	private ChangeLongSitTimeWindow timeWindow = null;

	private static boolean isStartTime, isFirstTime;
	
	private LongSit currentLongsit = null;
	
	private int startHour1;
	private int startMinute1;
	private int endHour1;
	private int endMinute1;
	private int startHour2;
	private int startMinute2;
	private int endHour2;
	private int endMinute2;
	
	private int step;

	private Button saveBtn;
	private SkinSettingManager mSettingManager;
	
	private BLEProvider provider;
	private BLEProviderObserverAdapter bleProviderObserver = new BLEHandler.BLEProviderObserverAdapter()
	{
		@Override
		public void updateFor_notifyForDeviceAloneSyncSucess_D()
		{
		}

		@Override
		protected Activity getActivity()
		{
			return LongSitActivity.this;
		}

		@Override
		public void updateFor_handleSetTime() {
			// TODO Auto-generated method stub
			
		}

	};


	/**
	 * 提交至服务端的对象
	 */
	private LongSit dataLongsit = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		if(android.os.Build.MANUFACTURER.equalsIgnoreCase("meizu"))
			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
		
		super.onCreate(savedInstanceState); 
		provider = MyApplication.getInstance(this).getCurrentHandlerProvider();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		// provider =
		// MyApplication.getInstance(this).getProvider(setAllDeviceInfoHandler);
		mSettingManager=new SkinSettingManager(this);
		mSettingManager.initSkins();
		provider.setBleProviderObserver(bleProviderObserver);
		
	}

	protected void onDestroy()
	{
		super.onDestroy();

		if (provider.getBleProviderObserver() == bleProviderObserver)
			provider.setBleProviderObserver(null);
	}
	
	private boolean checkSettingTimeValid()
	{
		if (
				//时间段1中起始时间小于结束时间
				(startHour1 < endHour1 || (startHour1 == endHour1 && startMinute1 < endMinute1))
				&&
				//时间段2中起始时间小于结束时间
				(startHour2 < endHour2 || (startHour2 == endHour2 && startMinute2 < endMinute2))
				&&
				//时间段2中的起始时间小于时间段1中的结束时间
				(endHour1 < startHour2 || (endHour1 == startHour2 && startMinute1 < startMinute2))
			)
			return true;
		
		return false;
	}

	private void saveSettingAuto()
	{
		if (checkSettingTimeValid())
		{
			dataLongsit = new LongSit();
			dataLongsit.setUser_id(MyApplication.getInstance(LongSitActivity.this).getLocalUserInfoProvider().getUser_id());
			
			dataLongsit.setStartHour1(startHour1);
			dataLongsit.setStartMinute1(startMinute1);
			dataLongsit.setEndHour1(endHour1);
			dataLongsit.setEndMinute1(endMinute1);
			
			dataLongsit.setStartHour2(startHour2);
			dataLongsit.setStartMinute2(startMinute2);
			dataLongsit.setEndHour2(endHour2);
			dataLongsit.setEndMinute2(endMinute2);
			
			
			step = CommonUtils.getIntValue(longSitStep.getText().toString(), 60);
			dataLongsit.setLong_sit_step(step);
			
			// 3600秒为默认的时间间隔，界面上没有显示与设置该字段的功能
			dataLongsit.setInterval(longsitSwitch.isChecked() ? 3600 : -3600);// 负的间隔时间即表示关闭

			new DataAsyncTask().execute();
			
		}
		else
		{
			new com.eva.android.widgetx.AlertDialog.Builder(LongSitActivity.this).setTitle(R.string.long_sit_time_error_title)
					.setMessage(R.string.long_sit_time_error_message).setPositiveButton(R.string.general_ok, null).show();
		}

	}

	@Override
	protected void initViews()
	{
		customeTitleBarResId = R.id.longsit_titleBar;
		// 首先设置contentview
		setContentView(R.layout.longsit_activity);

		this.setTitle(R.string.long_sit_title);

		timeLL = (LinearLayout) findViewById(R.id.longsit_time_ll);
		// startTimeBtn = (Button) findViewById(R.id.longsit_start_time_btn);
		// endTimeBtn = (Button) findViewById(R.id.longsit_end_time_btn);
		longsitSwitch = (CheckBox) findViewById(R.id.longsit_switch_checkbox);
		
		startTimeOneLL = (LinearLayout) findViewById(R.id.long_sit_start_time_one_linear);
		endTimeOneLL = (LinearLayout) findViewById(R.id.long_sit_end_time_one_linear);
		
		startTimeTwoLL = (LinearLayout) findViewById(R.id.long_sit_start_time_two_linear);
		endTimeTwoLL = (LinearLayout) findViewById(R.id.long_sit_end_time_two_linear);
		
		startTime1 = (TextView) findViewById(R.id.long_sit_start_time_one);
		endTime1 = (TextView) findViewById(R.id.long_sit_end_time_one);
		
		startTime2 = (TextView) findViewById(R.id.long_sit_start_time_two);
		endTime2 = (TextView) findViewById(R.id.long_sit_end_time_two);
		
		longSitStep = (EditText) findViewById(R.id.long_sit_step_edit);
		
		saveBtn = (Button) findViewById(R.id.long_sit_setting_saveBtn);
	}

	@Override
	protected void initListeners()
	{
		startTimeOneLL.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				isStartTime = true;
				isFirstTime = true;
				showLongSitTimeChange();
			}
		});

		endTimeOneLL.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				isStartTime = false;
				isFirstTime = true;
				showLongSitTimeChange();
			}
		});
		
		
		startTimeTwoLL.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				isStartTime = true;
				isFirstTime = false;
				showLongSitTimeChange();
			}
		});

		endTimeTwoLL.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				isStartTime = false;
				isFirstTime = false;
				showLongSitTimeChange();
			}
		});

		longsitSwitch.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				timeLL.setVisibility(longsitSwitch.isChecked() ? View.VISIBLE : View.GONE);
			}
		});
		
		
		longSitStep.addTextChangedListener(new TextWatcher()
		{

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
				
			}

			@Override
			public void afterTextChanged(Editable s)
			{

			}
		});
		
		saveBtn.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				saveSettingAuto();
			}
		});

	}


	@Override
	protected DataFromServer queryData(String... arg0)
	{
			DataFromServer dfs = new DataFromServer();
			dfs.setSuccess(true);
			
			LocalSetting setting = LocalUserSettingsToolkits.getLocalSettingInfo(this, MyApplication.getInstance(this).getLocalUserInfoProvider().getUser_mail());
			
			LongSit longsit = new LongSit();
			if(setting == null || CommonUtils.isStringEmpty(setting.getLong_sit_time()))
			{
				String long_sit_time = MyApplication.getInstance(this).getLocalUserInfoProvider().getLong_sit_time();
				
				//根据long_sit_time设置特定时间属性
				_Utils.setPropertyWithLongSitTimeString(longsit, long_sit_time);
				
				UserEntity u = MyApplication.getInstance(this).getLocalUserInfoProvider();
				longsit.setUser_id(u.getUser_id());
				longsit.setLong_sit_update(CommonUtils.isStringEmpty(u.getLong_sit_update()) ? 0 : Long.parseLong(u.getLong_sit_update()));
				longsit.setInterval(Integer.parseInt(u.getLong_sit()));
				longsit.setLong_sit_step(Integer.parseInt(u.getLong_sit_step()));
				
			}
			else
			{
				
				String long_sit_time = setting.getLong_sit_time();
				
				//根据long_sit_time设置特定时间属性
				_Utils.setPropertyWithLongSitTimeString(longsit, long_sit_time);
				
				longsit.setUser_id(MyApplication.getInstance(this).getLocalUserInfoProvider().getUser_id());
				longsit.setLong_sit_update(setting.getLong_sit_update());
				longsit.setInterval(setting.getLong_sit());
				longsit.setLong_sit_step(setting.getLong_sit_step());
				
			}
			dfs.setReturnValue(JSON.toJSONString(longsit));
			return dfs;
	}

	@Override
	protected void refreshToView(Object result)
	{
		
		
		if (result != null)
		{
			LongSit longsit = JSON.parseObject((String) result, LongSit.class);
			currentLongsit = longsit;
		}
		else
		{
			currentLongsit = new LongSit();
			currentLongsit.setInterval(3600);
			currentLongsit.setStartHour1(0);
			currentLongsit.setEndMinute1(0);
			currentLongsit.setStartMinute1(0);
			currentLongsit.setEndHour1(0);
			currentLongsit.setStartHour2(0);
			currentLongsit.setEndMinute2(0);
			currentLongsit.setStartMinute2(0);
			currentLongsit.setEndHour2(0);
			currentLongsit.setLong_sit_step(60);
		}
		
		startHour1 = currentLongsit.getStartHour1();
		startMinute1 = currentLongsit.getStartMinute1();
		startHour2 = currentLongsit.getStartHour2();
		startMinute2 = currentLongsit.getStartMinute2();
		
		endHour1 = currentLongsit.getEndHour1();
		endMinute1 = currentLongsit.getEndMinute1();
		endHour2 = currentLongsit.getEndHour2();
		endMinute2 = currentLongsit.getEndMinute2();
		
		step = currentLongsit.getLong_sit_step();
		
		longsitSwitch.setChecked(currentLongsit.getInterval() > 0);
		
		changeviewtime(startHour1,startMinute1,startTime1);
		changeviewtime(startHour2,startMinute2,startTime2);
		changeviewtime(endHour1,endMinute1,endTime1);
		changeviewtime(endHour2,endMinute2,endTime2);
		
		longSitStep.setText(step + "");
		
		timeLL.setVisibility(longsitSwitch.isChecked() ? View.VISIBLE : View.GONE);
		
	}
	
	private void changeviewtime(int hour, int minute, TextView tv){
		if(hour>12){
			tv.setText(ToolKits.int2String(hour-12) + ":" + ToolKits.int2String(minute)+" PM");
		}else{
			if(hour==0)
				tv.setText("12:" + ToolKits.int2String(minute)+" AM");
			else
			tv.setText(ToolKits.int2String(hour) + ":" + ToolKits.int2String(minute)+" AM");
		}
	}

	/**
	 * 提交数据请求和处理的异步执行线程实现类.
	 */
	protected class DataAsyncTask extends DataLoadingAsyncTask<String, Integer, DataFromServer>
	{
		public DataAsyncTask()
		{
			super(LongSitActivity.this, $$(R.string.general_submitting));
		}

		/**
		 * 在后台执行 {@link doLogin()}实现登陆信息的提交和处于结果的读取 .
		 * 
		 * @param parems
		 *            外界传进来的参数
		 * @return 查询结果，将传递给onPostExecute(..)方法
		 */
		@Override
		protected DataFromServer doInBackground(String... params)
		{
			
			LocalSetting localSetting = new LocalSetting();
			long update_time = ToolKits.getDayFromDate(new Date(), 0).getTime();
			
			localSetting.setUser_mail(MyApplication.getInstance(LongSitActivity.this).getLocalUserInfoProvider().getUser_mail());
			localSetting.setLong_sit(dataLongsit.getInterval());
			localSetting.setLong_sit_step(dataLongsit.getLong_sit_step());
			//根据各个时间属性。生成sit_time
			String sit_time = _Utils.createLongSitTimeStringByProperty(dataLongsit);
			localSetting.setLong_sit_time(sit_time);
			localSetting.setLong_sit_update(update_time);
			LocalUserSettingsToolkits.setLocalSettingLongSitInfo(context, localSetting);
			
			//判断有无网络
			if(ToolKits.isNetworkConnected(LongSitActivity.this))
			{
				return HttpServiceFactory4AJASONImpl
						.getInstance()
						.getDefaultService()
						.sendObjToServer(
								DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_USERSETTING).setJobDispatchId(JobDispatchConst.USER_SETTINGS_LONGSIT)
										.setActionId(SysActionConst.ACTION_APPEND4).setNewData(new Gson().toJson(dataLongsit)));
			}
			else
			{
				DataFromServer dfs = new DataFromServer();
				dfs.setSuccess(true);
				dfs.setReturnValue(JSON.toJSONString(dataLongsit));
				return dfs;
			}
		}

		/**
		 * 处理服务端返回的登陆结果信息.
		 * 
		 * @see AutoUpdateDaemon
		 * @see #needSaveDefaultLoginName()
		 * @see #afterLoginSucess()
		 */
		protected void onPostExecuteImpl(Object result)
		{
			if (result != null)
			{
				
				ToolKits.showCommonTosat(LongSitActivity.this, true, $$(R.string.setting_success), Toast.LENGTH_LONG);
				refreshToView(result);
				UserEntity userEntity = MyApplication.getInstance(LongSitActivity.this).getLocalUserInfoProvider();
				// if(currentLongsit.getInterval() < 0)
				// {
				// userEntity.setLong_sit_time("0-0");
				// }
				// else
				{
					
					String long_sit_time = _Utils.createLongSitTimeStringByProperty(currentLongsit);

					// 更新本地用户的久坐提醒数据
					userEntity.setLong_sit(String.valueOf(currentLongsit.getInterval()));
					userEntity.setLong_sit_time(long_sit_time);
					userEntity.setLong_sit_update(currentLongsit.getLong_sit_update() + "");
					userEntity.setLong_sit_step(currentLongsit.getLong_sit_step() + "");
					//判断有无网络
					if(ToolKits.isNetworkConnected(LongSitActivity.this))
						//删除内存
						LocalUserSettingsToolkits.removeLocalSettingLongSitInfo(context, MyApplication.getInstance(LongSitActivity.this).getLocalUserInfoProvider().getUser_mail());
				}
				try
				{
					provider.SetLongSit(context, DeviceInfoHelper.fromUserEntity(userEntity));
					finish();
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
				catch (ParseException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void showLongSitTimeChange()
	{
		// 为弹出窗口实现监听类
		final OnClickListener itemsOnClick = new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

			}
		};
		// 实例化SelectPicPopupWindow
		timeWindow = new ChangeLongSitTimeWindow(LongSitActivity.this, itemsOnClick);
		// 显示窗口
		timeWindow.showAtLocation(findViewById(R.id.longsit_activity), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
	}

	private class ChangeLongSitTimeWindow extends ChoiceItemPopWindow
	{
		private Button btn_save, btn_cancel;
		private TimePicker timePicker;
		private TextView titleView;
		int tpHour = 0;
		int tpMinute = 0;

		public ChangeLongSitTimeWindow(Activity context, OnClickListener mItemsOnClick)
		{
			super(context, mItemsOnClick, R.layout.change_long_sit_time_dialog, R.id.long_sit_time_dialog);
		}

		protected void initContentViewComponents(View mMenuView)
		{
			btn_save = (Button) mMenuView.findViewById(R.id.long_sit_time_dialog_savebtn);
			btn_cancel = (Button) mMenuView.findViewById(R.id.long_sit_time_dialog_cancelbtn);
			titleView = (TextView) mMenuView.findViewById(R.id.long_sit_time_text);

			titleView.setText(isStartTime ? R.string.long_sit_start_time_text : R.string.long_sit_end_time_text);

			timePicker = (TimePicker) mMenuView.findViewById(R.id.long_sit_time_dialog_timepicker);
			timePicker.setIs24HourView(false);
			
			if(isStartTime)
			{
				tpHour = isFirstTime ? startHour1 : startHour2;
				tpMinute = isFirstTime ? startMinute1 : startMinute2;
			}
			else
			{
				tpHour = isFirstTime ? endHour1 : endHour2;
				tpMinute = isFirstTime ? endMinute1 : endMinute2;
			}
			
			timePicker.setCurrentHour(tpHour);
			timePicker.setCurrentMinute(tpMinute);

			timePicker.setOnTimeChangedListener(new OnTimeChangedListener()
			{
				@Override
				public void onTimeChanged(TimePicker view, int hourOfDay, int minute)
				{
					view.setCurrentHour(hourOfDay);
					view.setCurrentMinute(minute);
				}
			});

			// 取消按钮
			btn_cancel.setOnClickListener(createCancelClickListener());
			btn_save.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					timePicker.clearFocus();
					if (isStartTime)
					{
						if(isFirstTime)
						{
							startHour1 = timePicker.getCurrentHour();
							startMinute1 = timePicker.getCurrentMinute();
							changeviewtime(startHour1, startMinute1, startTime1);
						}
						else
						{
							startHour2 = timePicker.getCurrentHour();
							startMinute2 = timePicker.getCurrentMinute();
							changeviewtime(startHour2, startMinute2, startTime2);
						}
					}
					else
					{
						if(isFirstTime)
						{
							endHour1 = timePicker.getCurrentHour();
							endMinute1 = timePicker.getCurrentMinute();
							changeviewtime(endHour1, endMinute1, endTime1);
						}
						else
						{
							endHour2 = timePicker.getCurrentHour();
							endMinute2 = timePicker.getCurrentMinute();
							changeviewtime(endHour2, endMinute2, endTime2);
						}
						
					}
					dismiss();
				}
			});
		}
	}

}
