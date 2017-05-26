/**
 * HistoryListAdapter.java
 * @author Jason Lu
 * @date 2013-11-4
 * @version 1.0
 */
package com.linkloving.rtring_c_watch.logic.main.adapter;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eva.android.platf.std.AutoUpdateDaemon;
import com.eva.android.widget.AListAdapter2;
import com.eva.android.widget.ChoiceItemPopWindow;
import com.eva.android.widget.DataLoadingAsyncTask;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.eva.epc.core.dto.SysActionConst;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.linkloving.rtring_c_watch.LocalUserSettingsToolkits;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.linkloving.rtring_c_watch.logic.model.LocalSetting;
import com.linkloving.rtring_c_watch.utils.DeviceInfoHelper;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.linkloving.utils._Utils;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.Alarm;
import com.rtring.buiness.logic.dto.JobDispatchConst;
import com.rtring.buiness.logic.dto.UserEntity;

/**
 * @author Jason
 *
 */
public class AlarmAdapter extends AListAdapter2<Alarm>
{
	private static final String TAG = AlarmAdapter.class.getSimpleName();
	
	/** 记录选中的ListView的行索引值以备后用（目前是在：单击、长按2类事件中保存了此索引值）. */
	protected int selectedListViewIndex = -1;
	
	private Activity context;
	
	private AlarmChangeWindow menuWindow = null;
	
	private BLEProvider provider;
	
	
	

	/**
	 * 上传至服务端的对象
	 */
	private List<Alarm> data = null;


	public AlarmAdapter(final Activity context,BLEProvider provider)
	{
		super(context, R.layout.alarm_activity_listview_item);
		this.context = context;
		this.provider = provider;
		
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		//ImageView viewAvatar = null;
		TextView viewTime = null;
		TextView viewDesc = null;
		CheckBox alarmSwtich = null;
//		Button settingBtn = null;
		ViewGroup clickableLL = null;

		// ----------------------------------------------------------------------------------------
		// （1）UI初始化
		// 当的item布局实例已经存在（不在存，意味着这个item刚好从不可见区移到可见区时）
		// ** 根据android的列表ui实现，为了节比资源占用，假如列表有100行而可见区显示5行，那么任何时候
		// ** 这个列表中只会有5个item的UI存在，其它的item都是当被转到可见区时自动把自
		// ** 已的数据实时地更新列UI上，以便查看，也就是说item的UI并不是与后台数据一
		// ** 一对应的，所以正如API文档里说的，convertView并不能确保它总能按你的想法保持不为null
		boolean needCreateItem = (convertView == null);
		// 正在操作的列表行的数据集
		final Alarm rowData = listData.get(position);
		if (needCreateItem)
			// 明细item的UI实例化 itemResId
			convertView = layoutInflater.inflate(R.layout.alarm_activity_listview_item, null);
		viewTime = (TextView) convertView.findViewById(R.id.alarm_item_time);
		viewDesc = (TextView) convertView.findViewById(R.id.alarm_item_day_text);
		alarmSwtich = (CheckBox) convertView.findViewById(R.id.alarm_item_switch_checkbox);
		clickableLL = (ViewGroup) convertView.findViewById(R.id.alarm_item_setting_clickableLL);
//		settingBtn = (Button) convertView.findViewById(R.id.alarm_item_setting_btn);
		// ----------------------------------------------------------------------------------------
		// （2）增加事件处理器
		// 各操作组件的事件监听器只需要在convertView被实例化时才需要重建（convertView需要被实例化
		// 当然就意味着它上面的所有操作组件都已经重新新建了）
		// ** 关于事件处理器的说明：事件处理器的增加其实可以不这么麻烦，直接每getView一次就给组件new一个处理器，
		// ** 这样做的好处是简单，但显然存在资源浪费（每刷新一次view就新建监听器）。而现在的实现就跟Android的列表
		// ** 实现原理一样，在切换到下一组item前，监听器永远只有一屏item的数量（刷新时只需要即时刷新对应item的数据到
		// ** 它的监听器里），这样就节省了资源开销！
		if (needCreateItem)
		{
			//
		}
		convertView.setTag(rowData);
		
		clickableLL.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				selectedListViewIndex = position;
				showAlarmChange();
			}
		});
		
		alarmSwtich.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				data = AlarmAdapter.this.getListData();
				if(data.get(position).getRepeat()==0){ //1015添加
					data.get(position).setValid(0);
				}else{
					data.get(position).setValid(((CheckBox)v).isChecked() ? 1 : 0);
				}
				new DataAsyncTask().execute();
			}
		});
//		if (alarmSwtich != null && alarmSwtich instanceof Checkable) {
//			((Checkable) alarmSwtich).setChecked(false);
//			SwitchButton switchButton = (SwitchButton) alarmSwtich;
//			switchButton
//					.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
//						public void onCheckedChanged(CompoundButton buttonView,
//								boolean isChecked) {
//							Log.i(TAG, isChecked+"");
//							// TODO Auto-generated method stub
//							data = AlarmAdapter.this.getListData();
//							data.get(position).setValid(isChecked ? 1 : 0);
//							new DataAsyncTask().execute();
//						}
//					});
//			// send an event to announce the value change of the CheckBox and is
//			// done here
//			// because clicking a preference does not immediately change the
//			// checked state
//			// for example when enabling the WiFi
//		}

		// ----------------------------------------------------------------------------------------
		// （3）
		// 给标签设置值，以供用户查看
		int time = rowData.getAlarmTime();
		alarmSwtich.setChecked(rowData.getValid() == 1);
		if(_Utils.getHourBySeconds(Math.abs(time))>12){
			viewTime.setText(ToolKits.int2String(_Utils.getHourBySeconds(Math.abs(time))-12) + ":" + ToolKits.int2String(_Utils.getMinuteBySeconds(Math.abs(time)))+" PM");
		}else{
			if(_Utils.getHourBySeconds(Math.abs(time))==0)
				viewTime.setText("12:" + ToolKits.int2String(_Utils.getMinuteBySeconds(Math.abs(time)))+" AM");
			else
			viewTime.setText(ToolKits.int2String(_Utils.getHourBySeconds(Math.abs(time))) + ":" + ToolKits.int2String(_Utils.getMinuteBySeconds(Math.abs(time)))+" AM");
		}
		viewDesc.setText(initAlarmDesc(rowData.getRepeat()));
		
		return convertView;
	}

	public int getSelectedListViewIndex()
	{
		return selectedListViewIndex;
	}

	public void setSelectedListViewIndex(int selectedListViewIndex)
	{
		this.selectedListViewIndex = selectedListViewIndex;
		// this.notifyDataSetChanged();
	}
	
	private String initAlarmDesc(int repeat)
	{
		String desc = "";
		if(repeat == 0)
			return ToolKits.getStringbyId(context, R.string.alarm_desc_none);
		if(repeat == 65)
			return ToolKits.getStringbyId(context, R.string.alarm_desc_weekend);
		if(repeat == 62)
			return ToolKits.getStringbyId(context, R.string.alarm_desc_workday);
		if(repeat == 127)
			return ToolKits.getStringbyId(context, R.string.alarm_desc_everyday);
		for (int i = 0; i < 7; i++)
		{
			if((repeat & (int)Math.pow(2, i)) == (int)Math.pow(2, i))
			{
				if(desc.equals(""))
					desc = ToolKits.getStringbyId(context, R.string.alarm_week) + getAlarmDescStr(i);
				else
					desc += " " + getAlarmDescStr(i);
			}
		}
		return desc;
	}
	
	
	/**
	 * 初始化闹钟状态
	 */
	private void initCheckBoxStatus(View mMenuView, int repeat)
	{
		for (int i = 0; i < 7; i++)
		{
			CheckBox check = (CheckBox) mMenuView.findViewById(getCheckBoxIdByIndex(i));
			check.setChecked((repeat & (int)Math.pow(2, i)) == (int)Math.pow(2, i));
		}
	}
	
	
	private void showAlarmChange()
	{
		//为弹出窗口实现监听类
		final OnClickListener  itemsOnClick = new OnClickListener(){
			public void onClick(View v)
			{
				menuWindow.dismiss();
				switch (v.getId()) 
				{
				case 1:
				{
					break;
				}
				case 2:	
				{
					break;
				}
				default:
					break;
				}
			}
		};
		//实例化SelectPicPopupWindow
		menuWindow = new AlarmChangeWindow(context, itemsOnClick);
		//显示窗口
		menuWindow.showAtLocation(context.findViewById(R.id.alarm_activity), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
	}
	

	private class AlarmChangeWindow extends ChoiceItemPopWindow
	{
		private Button btn_save, btn_cancel;
		private CheckBox monday, tuesday, wednessday, thursday, friday, saturday, sunday;
		private TimePicker timePicker;

		public AlarmChangeWindow(Activity context, OnClickListener mItemsOnClick)
		{
			super(context, mItemsOnClick, R.layout.alarm_change_alarm_dialog, R.id.alarm_change_alarm_dialog);
		}

		protected void initContentViewComponents(View mMenuView)
		{
			initCheckBoxStatus(mMenuView, getListData().get(selectedListViewIndex).getRepeat());
			
			btn_save = (Button) mMenuView.findViewById(R.id.alarm_change_alarm_dialog_savebtn);
			btn_cancel = (Button) mMenuView.findViewById(R.id.alarm_change_alarm_dialog_cancelbtn);

			sunday = (CheckBox) mMenuView.findViewById(R.id.alarm_change_alarm_dialog_sunday);
			monday = (CheckBox) mMenuView.findViewById(R.id.alarm_change_alarm_dialog_monday);
			tuesday = (CheckBox) mMenuView.findViewById(R.id.alarm_change_alarm_dialog_tuesday);
			wednessday = (CheckBox) mMenuView.findViewById(R.id.alarm_change_alarm_dialog_wednessday);
			thursday = (CheckBox) mMenuView.findViewById(R.id.alarm_change_alarm_dialog_thursday);
			friday = (CheckBox) mMenuView.findViewById(R.id.alarm_change_alarm_dialog_friday);
			saturday = (CheckBox) mMenuView.findViewById(R.id.alarm_change_alarm_dialog_saturday);
			
			timePicker = (TimePicker) mMenuView.findViewById(R.id.alarm_change_alarm_dialog_timepicker);
			timePicker.setIs24HourView(false);
			timePicker.setCurrentHour(_Utils.getHourBySeconds(getListData().get(selectedListViewIndex).getAlarmTime()));
			timePicker.setCurrentMinute(_Utils.getMinuteBySeconds(getListData().get(selectedListViewIndex).getAlarmTime()));
			
			// 取消按钮
			btn_cancel.setOnClickListener(createCancelClickListener());
			
			btn_save.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					int repeat = 0;
					repeat += sunday.isChecked() ? 1 : 0;
					repeat += monday.isChecked() ? 2 : 0;
					repeat += tuesday.isChecked() ? 4 : 0;
					repeat += wednessday.isChecked() ? 8 : 0;
					repeat += thursday.isChecked() ? 16 : 0;
					repeat += friday.isChecked() ? 32 : 0;
					repeat += saturday.isChecked() ? 64 : 0;
					data = AlarmAdapter.this.getListData();
					timePicker.clearFocus();
					Log.e(TAG, "timePicker.getCurrentHour()"+timePicker.getCurrentHour());
					data.get(selectedListViewIndex).setAlarmTime(timePicker.getCurrentHour() * 3600 + timePicker.getCurrentMinute() * 60);
					data.get(selectedListViewIndex).setRepeat(repeat);
					if (repeat != 0) {
						data.get(selectedListViewIndex).setValid(1);
					}else if(repeat == 0){
						data.get(selectedListViewIndex).setValid(0);
					}
					new DataAsyncTask().execute();
					dismiss();
				}
			});
		}
	}

	private int getCheckBoxIdByIndex(int i)
	{
		switch (i)
		{
		case 0:
			return R.id.alarm_change_alarm_dialog_sunday;
		case 1:
			return R.id.alarm_change_alarm_dialog_monday;
		case 2:
			return R.id.alarm_change_alarm_dialog_tuesday;
		case 3:
			return R.id.alarm_change_alarm_dialog_wednessday;
		case 4:
			return R.id.alarm_change_alarm_dialog_thursday;
		case 5:
			return R.id.alarm_change_alarm_dialog_friday;
		case 6:
			return R.id.alarm_change_alarm_dialog_saturday;
		}
		return 0;
	}
	
	private String getAlarmDescStr(int i)
	{
		switch (i)
		{
		case 0:
			return ToolKits.getStringbyId(context, R.string.alarm_desc_sunday);
		case 1:
			return ToolKits.getStringbyId(context, R.string.alarm_desc_monday);
		case 2:
			return ToolKits.getStringbyId(context, R.string.alarm_desc_tuesday);
		case 3:
			return ToolKits.getStringbyId(context, R.string.alarm_desc_wednesday);
		case 4:
			return ToolKits.getStringbyId(context, R.string.alarm_desc_thursday);
		case 5:
			return ToolKits.getStringbyId(context, R.string.alarm_desc_friday);
		case 6:
			return ToolKits.getStringbyId(context, R.string.alarm_desc_saturday);
		}
		return "";
	}
	
	/**
	 * 提交数据请求和处理的异步执行线程实现类.
	 */
	protected class DataAsyncTask extends DataLoadingAsyncTask<String, Integer, DataFromServer>
	{
		public DataAsyncTask()
		{
			super(getContext(), getContext().getString(R.string.general_submitting));
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
			
			localSetting.setUser_mail(MyApplication.getInstance(getContext()).getLocalUserInfoProvider().getUser_mail());
			localSetting.setAlarm_list(JSON.toJSONString(data));
			localSetting.setAlarm_update(update_time);
			LocalUserSettingsToolkits.setLocalSettingAlarmInfo(context, localSetting);
			
			JSONObject dataObj = new JSONObject();
			dataObj.put("alarm_update", update_time);
			dataObj.put("data", JSON.toJSONString(data));
			dataObj.put("user_id", MyApplication.getInstance(getContext()).getLocalUserInfoProvider().getUser_id());
			
			if(ToolKits.isNetworkConnected(context))
			{
				return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(
						DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_USERSETTING)
						.setJobDispatchId(JobDispatchConst.USER_SETTINGS_ALARM)
						.setActionId(SysActionConst.ACTION_APPEND4)
						.setNewData(dataObj.toJSONString()));
			}
			else
			{
				DataFromServer dfs = new DataFromServer();
				dfs.setSuccess(true);
				dfs.setReturnValue(JSON.toJSONString(dataObj));
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
			if(result != null)
			{
				JSONObject obj = JSON.parseObject((String) result);
				MyApplication.getInstance(context).getLocalUserInfoProvider().setAlarm_list(obj.getString("data"));
				MyApplication.getInstance(context).getLocalUserInfoProvider().setAlarm_update(obj.getString("alarm_update"));
				
				//判断有无网络
				if(ToolKits.isNetworkConnected(context))
					//删除内存
					LocalUserSettingsToolkits.removeLocalSettingAlarmInfo(context, MyApplication.getInstance(getContext()).getLocalUserInfoProvider().getUser_mail());
				
				UserEntity userEntity = MyApplication.getInstance(context).getLocalUserInfoProvider();
				try
				{
					provider.SetClock(context, DeviceInfoHelper.fromUserEntity(userEntity));
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
				catch (ParseException e)
				{
					e.printStackTrace();
				}
				ArrayList<Alarm> data = (ArrayList<Alarm>) JSON.parseArray(obj.getString("data"), Alarm.class);
				AlarmAdapter.this.setListData(data);
				notifyDataSetChanged();
			}
		}
	}
}
