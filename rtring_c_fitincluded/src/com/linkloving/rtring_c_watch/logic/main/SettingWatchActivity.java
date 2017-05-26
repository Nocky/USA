package com.linkloving.rtring_c_watch.logic.main;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eva.android.platf.std.AutoUpdateDaemon;
import com.eva.android.platf.std.DataLoadableActivity;
import com.eva.android.widget.DataLoadingAsyncTask;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.eva.epc.core.dto.SysActionConst;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.linkloving.rtring_c_watch.logic.setup.MovingTargetActivity;
import com.linkloving.rtring_c_watch.utils.IntentFactory;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.JobDispatchConst;

public class SettingWatchActivity extends DataLoadableActivity
{
	private final static String TAG = SettingWatchActivity.class.getSimpleName();
	private final static int RESQUEST_SETTING = 10000;

	private ImageView wacthview;
	private LinearLayout activity_own_alarm;
	private LinearLayout activity_own_longsit;
	private LinearLayout activity_own_control;
	private LinearLayout activity_own_msg;
	private LinearLayout activity_own_power;
	private LinearLayout activity_own_content;
	
//	private Button unbound;

	@Override
	protected void initViews()
	{
		customeTitleBarResId = R.id.mywatch_titleBar;
		setContentView(R.layout.activity_own_watch);
		wacthview = (ImageView) findViewById(R.id.watch_head);
		activity_own_alarm = (LinearLayout) findViewById(R.id.activity_own_alarm);
		activity_own_longsit = (LinearLayout) findViewById(R.id.activity_own_longsit);
		activity_own_control = (LinearLayout) findViewById(R.id.activity_own_control);
		activity_own_msg = (LinearLayout) findViewById(R.id.activity_own_msg);
		activity_own_content = (LinearLayout) findViewById(R.id.activity_own_content);
		activity_own_power = (LinearLayout) findViewById(R.id.activity_own_power);
		this.setTitle("手表设置");
	}

	@Override
	protected void initListeners()
	{

		wacthview.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				startActivity(new Intent(SettingWatchActivity.this, OwnBraceletActivity.class));
			}
		});
		// 闹钟设置
		activity_own_alarm.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				IntentFactory.startAlarmActivityIntent(SettingWatchActivity.this);
			}
		});
		// 久坐提醒
		activity_own_longsit.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				IntentFactory.startLongSitActivityIntent(SettingWatchActivity.this);
			}
		});
		// 勿扰设置
		activity_own_control.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				IntentFactory.startHandUpActivityIntent(SettingWatchActivity.this);
			}
		});
		// 消息提醒
		activity_own_msg.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				IntentFactory.startNotifacitionActivityIntent(SettingWatchActivity.this);
			}
		});
		// 节能模式
		activity_own_power.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				IntentFactory.startPowerActivityIntent(SettingWatchActivity.this);
			}
		});
		// 运动目标设置
		activity_own_content.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				IntentFactory.startGoalActivityIntent(SettingWatchActivity.this, MyApplication.getInstance(SettingWatchActivity.this).getLocalUserInfoProvider(), MovingTargetActivity.COME_FROM_WATCH_SETTING_ACTIVITY);
			}
		});
		
//		unbound.setOnClickListener(new OnClickListener()
//		{
//
//			@Override
//			public void onClick(View v)
//			{
//				if (ToolKits.isNetworkConnected(SettingWatchActivity.this))
//				{
//					AlertDialog dialog = new AlertDialog.Builder(SettingWatchActivity.this)
//							.setTitle(ToolKits.getStringbyId(SettingWatchActivity.this, R.string.bracelet_unbound))
//							.setMessage(ToolKits.getStringbyId(SettingWatchActivity.this, R.string.bracelet_unbound_msg))
//							.setNegativeButton(ToolKits.getStringbyId(SettingWatchActivity.this, R.string.general_no), new DialogInterface.OnClickListener()
//							{
//
//								@Override
//								public void onClick(DialogInterface dialog, int which)
//								{
//									dialog.dismiss();
//								}
//							}).setPositiveButton(ToolKits.getStringbyId(SettingWatchActivity.this, R.string.general_yes), new DialogInterface.OnClickListener()
//							{
//								@Override
//								public void onClick(DialogInterface dialog, int which)
//								{
//									Log.d(TAG, "unBound click!!!!!!!!!!!!!!!!!!!");
//									String last_sync_device_id = MyApplication.getInstance(SettingWatchActivity.this).getLocalUserInfoProvider()
//											.getLast_sync_device_id();
//									Log.d(TAG, "last_sync_device_id...................." + last_sync_device_id);
//									if (!CommonUtils.isStringEmpty(last_sync_device_id))
//									{
//										new UnBoundAsyncTask().execute(); // 执行解绑异步操作
//									}
//									dialog.dismiss();
//
//									/**
//									 * 清空内存中的设备信息
//									 */
//									PreferencesToolkits.updateLocalDeviceInfo(SettingWatchActivity.this, new LPDeviceInfo());
//								}
//							}).create();
//					dialog.show();
//				}
//				else
//				{
//					new com.eva.android.widgetx.AlertDialog.Builder(SettingWatchActivity.this)
//							.setTitle(ToolKits.getStringbyId(SettingWatchActivity.this, R.string.bracelet_unbound_failed))
//							.setMessage(ToolKits.getStringbyId(SettingWatchActivity.this, R.string.bracelet_unbound_failed_msg))
//							.setPositiveButton(R.string.general_ok, null).show();
//				}
//			}
//		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode)
		{
		
		case RESQUEST_SETTING:
			
		
//			case REGISTER_BACK_FROM_TARGET:
//				// 将注册成功后的uid和密码默认填到界面上，方便用户登陆
//				if (resultCode == Activity.RESULT_OK)
//				{
////					Intent data = new Intent();
//					//					data.putExtra("target_step", target_step.getText().toString());
//					
//					Intent alldata = new Intent();
//					alldata.putExtra("target_step",data.getStringExtra("target_step")); 
//					String sex=null;
//					if(cbMan.isChecked()){
//						sex="1";
//					}else sex="0";
//					alldata.putExtra("user_sex", sex);
//					alldata.putExtra("user_height", height_info.getText().toString());
//					alldata.putExtra("user_weight", weight_info.getText().toString());
//					alldata.putExtra("user_age", age_info.getText().toString());
//					System.out.println(cbMan.isChecked() ? "1" : "0");
//					System.out.println(TAG+".user_height--->"+height_info.getText().toString());
//					System.out.println(TAG+".user_weight--->"+weight_info.getText().toString());
//					setResult(Activity.RESULT_OK, alldata);
//					finish();
//				}
//				break;
		
		}
	}

	/**
	 * 提交数据请求和处理的异步执行线程实现类.
	 */
	protected class UnBoundAsyncTask extends DataLoadingAsyncTask<Void, Integer, DataFromServer>
	{
		public UnBoundAsyncTask()
		{
			super(SettingWatchActivity.this, getString(R.string.general_submitting));
		}

		/**
		 * 在后台执行 {@link doLogin()}实现登陆信息的提交和处于结果的读取 .
		 * 
		 * @param parems
		 *            外界传进来的参数
		 * @return 查询结果，将传递给onPostExecute(..)方法
		 */
		@Override
		protected DataFromServer doInBackground(Void... params)
		{

			JSONObject obj = new JSONObject();
			obj.put("user_id", MyApplication.getInstance(SettingWatchActivity.this).getLocalUserInfoProvider().getUser_id());
			return HttpServiceFactory4AJASONImpl
					.getInstance()
					.getDefaultService()
					.sendObjToServer(
							DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_LOGIC).setJobDispatchId(JobDispatchConst.LOGIC_REGISTER)
									.setActionId(SysActionConst.ACTION_APPEND7).setNewData(obj.toJSONString()));
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
				if (((String) result).equals("true"))
				{
					Log.d(TAG, "解绑成功！！！！");

					MyApplication.getInstance(SettingWatchActivity.this).getCurrentHandlerProvider().unBoundDevice(SettingWatchActivity.this);
					MyApplication.getInstance(SettingWatchActivity.this).getLocalUserInfoProvider().setLast_sync_device_id(null);
					// / bleOptionWapper.unBoundRefreshView();

					// bleOptionWapper.showHint("设备解绑成功.");
					com.linkloving.rtring_c_watch.utils.ToolKits.showCommonTosat(SettingWatchActivity.this, true, SettingWatchActivity.this.getResources()
							.getString(R.string.unbound_success), Toast.LENGTH_LONG);
					MyApplication.getInstance(SettingWatchActivity.this).getCurrentHandlerProvider().release();
					finish();
				}
				else
				{
					Log.d(TAG, "解绑失败！！！！");
					Toast.makeText(SettingWatchActivity.this, ToolKits.getStringbyId(SettingWatchActivity.this, R.string.debug_device_unbound_failed),
							Toast.LENGTH_LONG).show();
					// bleOptionWapper.showHint("设备解绑信息同步到服务端时失败.");
				}
			}
			else
			{
				Log.e(TAG, "unBoundAsyncTask result is null!!!!!!!!!!!!!!!!!");
				// bleOptionWapper.showHint("设备解绑信息同步到服务端时发生未知错误.");
			}

		}
	}
	
	

	@Override
	protected DataFromServer queryData(String... arg0)
	{
		return null;
	}

	@Override
	protected void refreshToView(Object arg0)
	{

	}

}
