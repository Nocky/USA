package com.linkloving.rtring_c_watch.logic.setup;

import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.linkloving.utils._Utils;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.HandUp;
import com.rtring.buiness.logic.dto.JobDispatchConst;
import com.rtring.buiness.logic.dto.UserEntity;

public class HandUpActivity extends DataLoadableActivity
{
	private LinearLayout timeLL = null;
	private LinearLayout startTimeLL = null;
	private LinearLayout endTimeLL = null;
	// private Button startTimeBtn = null;
	// private Button endTimeBtn = null;

	private TextView startTime;
	private TextView endTime;
	private CheckBox handupSwitch = null;
	// private Button saveBtn = null;
	private ChangeHandUpTimeWindow timeWindow = null;

	private static boolean isStartTime;
	private HandUp currenthundup = null;
	private int startHour;
	private int startMinute;
	private int endHour;
	private int endMinute;

	private BLEProvider provider;
	private BLEProviderObserverAdapter bleProviderObserver = new BLEHandler.BLEProviderObserverAdapter()
	{
		@Override
		public void updateFor_notifyForDeviceAloneSyncSucess_D()
		{
			// Toast.makeText(LongSitActivity.this, "久坐提醒设置成功！",
			// Toast.LENGTH_SHORT).show();
			// WidgetUtils.showToast(LongSitActivity.this,
			// "DEBUG(设备反馈): 久坐提醒设置成功！", ToastType.OK);
		}

		@Override
		protected Activity getActivity()
		{
			return HandUpActivity.this;
		}
	};

	/**
	 * 提交至服务端的对象
	 */
	private HandUp dataHandUp = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		if (android.os.Build.MANUFACTURER.equalsIgnoreCase("meizu"))
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
		provider.setBleProviderObserver(bleProviderObserver);
	}

	protected void onDestroy()
	{
		super.onDestroy();

		if (provider.getBleProviderObserver() == bleProviderObserver)
			provider.setBleProviderObserver(null);
	}

	private void saveSettingAuto()
	{
		if(handupSwitch.isChecked()){
//			if (startHour >= endHour || (startHour == endHour && startMinute > endMinute))
//			{
				dataHandUp = new HandUp();
				dataHandUp.setUser_id(MyApplication.getInstance(HandUpActivity.this).getLocalUserInfoProvider().getUser_id());
				dataHandUp.setHand_up_enable(handupSwitch.isChecked() ? 1 : 0);// 负的间隔时间即表示关闭
				dataHandUp.setStartHour(startHour);
				dataHandUp.setStartMinute(startMinute);
				dataHandUp.setEndHour(endHour);
				dataHandUp.setEndMinute(endMinute);
				new DataAsyncTask().execute();
//			}
//			else
//			{
//				new com.eva.android.widgetx.AlertDialog.Builder(HandUpActivity.this).setTitle(R.string.long_sit_time_error_title)
//						.setMessage(R.string.long_sit_time_error_message).setPositiveButton(R.string.general_ok, null).show();
//			}	
		}else{
			dataHandUp = new HandUp();
			dataHandUp.setUser_id(MyApplication.getInstance(HandUpActivity.this).getLocalUserInfoProvider().getUser_id());
			dataHandUp.setHand_up_enable(handupSwitch.isChecked() ? 1 : 0);// 负的间隔时间即表示关闭
			dataHandUp.setStartHour(startHour);
			dataHandUp.setStartMinute(startMinute);
			dataHandUp.setEndHour(endHour);
			dataHandUp.setEndMinute(endMinute);
			new DataAsyncTask().execute();
		}
		

	}

	@Override
	protected void initViews()
	{
		customeTitleBarResId = R.id.handup_titleBar;
		// 首先设置contentview
		setContentView(R.layout.handup_activity);
		this.setTitle(R.string.handup_setting);

		timeLL = (LinearLayout) findViewById(R.id.handup_time_ll);
		// startTimeBtn = (Button) findViewById(R.id.longsit_start_time_btn);
		// endTimeBtn = (Button) findViewById(R.id.longsit_end_time_btn);
		handupSwitch = (CheckBox) findViewById(R.id.handup_switch_checkbox);
		startTimeLL = (LinearLayout) findViewById(R.id.handup_start_time_linear);
		endTimeLL = (LinearLayout) findViewById(R.id.handup_end_time_linear);
		startTime = (TextView) findViewById(R.id.handup_start_time);
		endTime = (TextView) findViewById(R.id.handup_end_time);
		timeLL.setVisibility(handupSwitch.isChecked() ? View.VISIBLE : View.GONE);
		// saveBtn = (Button) findViewById(R.id.longsit_save_btn);
	}

	@Override
	protected void initListeners()
	{
		startTimeLL.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				isStartTime = true;
				showHandUpTimeChange();
			}
		});

		endTimeLL.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				isStartTime = false;
				showHandUpTimeChange();
			}
		});

		handupSwitch.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				timeLL.setVisibility(handupSwitch.isChecked() ? View.VISIBLE : View.GONE);
				saveSettingAuto();
			}
		});

	}

	@Override
	protected DataFromServer queryData(String... arg0)
	{
		// 判断有无网络
		if (ToolKits.isNetworkConnected(this))
		{
			return HttpServiceFactory4AJASONImpl
					.getInstance()
					.getDefaultService()
					.sendObjToServer(
							DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_USERSETTING).setJobDispatchId(JobDispatchConst.USER_SETTING_HAND_UP)
									.setActionId(SysActionConst.ACTION_APPEND3)
									.setNewData(MyApplication.getInstance(this).getLocalUserInfoProvider().getUser_id()));
		}
		else
		{
			DataFromServer dfs = new DataFromServer();
			dfs.setSuccess(true);

			LocalSetting setting = LocalUserSettingsToolkits.getLocalSettingInfo(this, MyApplication.getInstance(this).getLocalUserInfoProvider()
					.getUser_mail());

			HandUp handUp = new HandUp();
			if (setting == null || CommonUtils.isStringEmpty(setting.getHandup_time()))
			{
				String hand_up_time = MyApplication.getInstance(this).getLocalUserInfoProvider().getHand_up_time();
				int startSeconds = 0;
				int endSeconds = 0;

				if (!CommonUtils.isStringEmpty(hand_up_time))
				{
					String[] times = hand_up_time.split("-");
					startSeconds = Integer.parseInt(times[0]);
					endSeconds = Integer.parseInt(times[1]);
				}
				UserEntity u = MyApplication.getInstance(this).getLocalUserInfoProvider();
				// 这里 修改
				handUp.setUser_id(u.getUser_id());
				handUp.setHand_up_update(CommonUtils.isStringEmpty(u.getHand_up_enable()) ? 0 : Long.parseLong(u.getHand_up_update()));
				handUp.setHand_up_enable(Integer.parseInt(u.getHand_up_enable()));

				handUp.setStartHour(_Utils.getHourBySeconds(startSeconds));
				handUp.setStartMinute(_Utils.getMinuteBySeconds(startSeconds));
				handUp.setEndHour(_Utils.getHourBySeconds(endSeconds));
				handUp.setEndMinute(_Utils.getMinuteBySeconds(endSeconds));
			}
			else
			{

				String hand_up_time = setting.getHandup_time();
				int startSeconds = 0;
				int endSeconds = 0;

				if (!CommonUtils.isStringEmpty(hand_up_time))
				{
					String[] times = hand_up_time.split("-");
					startSeconds = Integer.parseInt(times[0]);
					endSeconds = Integer.parseInt(times[1]);
				}

				handUp.setUser_id(MyApplication.getInstance(this).getLocalUserInfoProvider().getUser_id());
				handUp.setHand_up_update(setting.getHandup_update());
				handUp.setHand_up_enable(setting.getHandup());

				handUp.setStartHour(_Utils.getHourBySeconds(startSeconds));
				handUp.setStartMinute(_Utils.getMinuteBySeconds(startSeconds));
				handUp.setEndHour(_Utils.getHourBySeconds(endSeconds));
				handUp.setEndMinute(_Utils.getMinuteBySeconds(endSeconds));

			}
			dfs.setReturnValue(JSON.toJSONString(handUp));
			return dfs;
		}
	}

	@Override
	protected void refreshToView(Object result)
	{
		if (result != null && !result.equals("null"))
		{
			HandUp hundup = JSON.parseObject((String) result, HandUp.class);
			currenthundup = hundup;
		}
		else
		{
			currenthundup = new HandUp();
			currenthundup.setHand_up_enable(0);
			currenthundup.setStartHour(0);
			currenthundup.setEndMinute(0);
			currenthundup.setStartMinute(0);
			currenthundup.setEndHour(0);
		}

		startHour = currenthundup.getStartHour();
		startMinute = currenthundup.getStartMinute();
		endHour = currenthundup.getEndHour();
		endMinute = currenthundup.getEndMinute();
		
		handupSwitch.setChecked(currenthundup.getHand_up_enable() > 0);
		timeLL.setVisibility(handupSwitch.isChecked() ? View.VISIBLE : View.GONE);
		startTime.setText(ToolKits.int2String(currenthundup.getStartHour()) + ":" + ToolKits.int2String(currenthundup.getStartMinute()));
		endTime.setText(ToolKits.int2String(currenthundup.getEndHour()) + ":" + ToolKits.int2String(currenthundup.getEndMinute()));

	}

	/**
	 * 提交数据请求和处理的异步执行线程实现类.
	 */
	protected class DataAsyncTask extends DataLoadingAsyncTask<String, Integer, DataFromServer>
	{
		public DataAsyncTask()
		{
			super(HandUpActivity.this, $$(R.string.general_submitting));
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
			// 这里存到本地需要修改
			LocalSetting localSetting = new LocalSetting();
			long update_time = ToolKits.getDayFromDate(new Date(), 0).getTime();

			localSetting.setUser_mail(MyApplication.getInstance(HandUpActivity.this).getLocalUserInfoProvider().getUser_mail());
			localSetting.setHandup(dataHandUp.getHand_up_enable());
			String sit_time = (dataHandUp.getStartHour() * 3600 + dataHandUp.getStartMinute() * 60) + "-"
					+ (dataHandUp.getEndHour() * 3600 + dataHandUp.getEndMinute() * 60);
			localSetting.setHandup_time(sit_time);
			localSetting.setHandup_update(update_time);
			LocalUserSettingsToolkits.setLocalSettingHandUpInfo(context, localSetting);

			// 判断有无网络
			if (ToolKits.isNetworkConnected(HandUpActivity.this))
			{
				return HttpServiceFactory4AJASONImpl
						.getInstance()
						.getDefaultService()
						.sendObjToServer(
								DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_USERSETTING)
										.setJobDispatchId(JobDispatchConst.USER_SETTING_HAND_UP).setActionId(SysActionConst.ACTION_APPEND4)
										.setNewData(new Gson().toJson(dataHandUp)));
			}
			else
			{
				DataFromServer dfs = new DataFromServer();
				dfs.setSuccess(true);
				dfs.setReturnValue(JSON.toJSONString(dataHandUp));
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

				ToolKits.showCommonTosat(HandUpActivity.this, true, $$(R.string.setting_success), Toast.LENGTH_LONG);
				refreshToView(result);
				UserEntity userEntity = MyApplication.getInstance(HandUpActivity.this).getLocalUserInfoProvider();
				
				{
					StringBuffer sb = new StringBuffer();
					sb.append(dataHandUp.getStartHour() * 60 * 60 + dataHandUp.getStartMinute() * 60).append("-")
							.append(dataHandUp.getEndHour() * 60 * 60 + dataHandUp.getEndMinute() * 60);

					// 更新本地用户的抬手显示数据
					userEntity.setHand_up_enable(String.valueOf(dataHandUp.getHand_up_enable()));
					userEntity.setHand_up_time(sb.toString());
					userEntity.setHand_up_update(dataHandUp.getHand_up_update() + "");

					// 判断有无网络
					if (ToolKits.isNetworkConnected(HandUpActivity.this))
						// 删除内存
						LocalUserSettingsToolkits.removeLocalSettingHandUpInfo(context, MyApplication.getInstance(HandUpActivity.this)
								.getLocalUserInfoProvider().getUser_mail());
				}

				try
				{
					if(provider.isConnectedAndDiscovered()){
						provider.SetHandUp(HandUpActivity.this, DeviceInfoHelper.fromUserEntity(userEntity));
					}
					
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

	public void showHandUpTimeChange()
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
		timeWindow = new ChangeHandUpTimeWindow(HandUpActivity.this, itemsOnClick);
		// 显示窗口
		timeWindow.showAtLocation(findViewById(R.id.handup_activity), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
	}

	private class ChangeHandUpTimeWindow extends ChoiceItemPopWindow
	{
		private Button btn_save, btn_cancel;
		private TimePicker timePicker;
		private TextView titleView;

		public ChangeHandUpTimeWindow(Activity context, OnClickListener mItemsOnClick)
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
			timePicker.setIs24HourView(true);
			timePicker.setCurrentHour(isStartTime ? startHour : endHour);
			timePicker.setCurrentMinute(isStartTime ? startMinute : endMinute);

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
						startHour = timePicker.getCurrentHour();
						startMinute = timePicker.getCurrentMinute();
						startTime.setText(ToolKits.int2String(startHour) + ":" + ToolKits.int2String(startMinute));
					}
					else
					{
						endHour = timePicker.getCurrentHour();
						endMinute = timePicker.getCurrentMinute();
						endTime.setText(ToolKits.int2String(endHour) + ":" + ToolKits.int2String(endMinute));
					}
					saveSettingAuto();
					dismiss();
				}
			});
		}
	}

}
