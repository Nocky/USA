package com.linkloving.rtring_c_watch.logic.setup;

import java.text.ParseException;
import java.util.Date;

import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eva.android.platf.std.AutoUpdateDaemon;
import com.eva.android.platf.std.DataLoadableActivity;
import com.eva.android.widget.DataLoadingAsyncTask;
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
import com.linkloving.rtring_c_watch.logic.model.LocalSetting;
import com.linkloving.rtring_c_watch.utils.DeviceInfoHelper;
import com.linkloving.rtring_c_watch.utils.SkinSettingManager;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.linkloving.utils._Utils;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.JobDispatchConst;
import com.rtring.buiness.logic.dto.UserEntity;

public class GoalActivity extends DataLoadableActivity
{
	public final static String TAG = GoalActivity.class.getSimpleName();
	
	private Button saveBtn = null;
	private SeekBar seekBar = null;
	private TextView stepView = null;
	private TextView caloryView = null;
	private TextView tuijianView = null;
	
	private int tuijianProcess = 0;
	private String dataGoal;
	
	private final int step = 500;
	private SkinSettingManager mSettingManager;
	private BLEProvider provider;
	private BLEProviderObserverAdapter bleProviderObserver = new BLEHandler.BLEProviderObserverAdapter(){
		@Override
		public void updateFor_notifyForDeviceAloneSyncSucess_D()
		{
//			   // Toast.makeText(GoalActivity.this, "设置目标成功！",
			// Toast.LENGTH_SHORT).show();
//			WidgetUtils.showToast(GoalActivity.this, "DEBUG(设备反馈): 设置目标成功！", ToastType.OK);
		}
		
		@Override
		protected Activity getActivity()
		{
			return GoalActivity.this;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		provider = MyApplication.getInstance(this).getCurrentHandlerProvider();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		mSettingManager=new SkinSettingManager(this);
		mSettingManager.initSkins();
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
		customeTitleBarResId = R.id.goal_setting_titleBar;
		// 首先设置contentview
		setContentView(R.layout.goal_activity);

		this.setTitle(R.string.goal_title);

		saveBtn = (Button) findViewById(R.id.goal_setting_saveBtn);
		stepView = (TextView) findViewById(R.id.goal_setting_steps);
		caloryView = (TextView) findViewById(R.id.goal_setting_calory);
		tuijianView = (TextView) findViewById(R.id.goal_setting_tuijianView);
		seekBar = (SeekBar) findViewById(R.id.goal_setting_seekbar);
		seekBar.setMax(20000);
	}

	@Override
	protected void initListeners()
	{
		
		seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{

			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
//				if(seekBar.getProgress() == tuijianProcess)
//				{
//					tuijianView.setBackground(getResources().getDrawable(R.drawable.goal_tuijian_gaoliang));
//					tuijianView.setClickable(false);
//				}
//				else
//				{
//					tuijianView.setBackground(getResources().getDrawable(R.drawable.goal_tuijian_btn_selector));
//					tuijianView.setClickable(true);
//				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				// TODO Auto-generated method stub
				System.out.println("====>change:" + progress);
				if(progress == 0)
				{
					stepView.setText("0");
					caloryView.setText("0");
				}
				else
				{
					progress = (progress / step) * step;
					refreshSuggestUI(progress);
//					if(progress == tuijianProcess)
//					{
//						tuijianView.setBackground(getResources().getDrawable(R.drawable.goal_tuijian_gaoliang));
//						tuijianView.setClickable(false);
//					}
//					else
//					{
						tuijianView.setBackground(getResources().getDrawable(R.drawable.goal_tuijian_btn_selector));
						tuijianView.setClickable(true);
//					}
					seekBar.setProgress(progress);
					stepView.setText((seekBar.getProgress() / step) * step + "");
					caloryView.setText((int) (CommonUtils.getIntValue(stepView.getText()) / 1000.0 * 38.0) + "");
				}
			}
		});
		
		tuijianView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				seekBar.setProgress(tuijianProcess);
			}
		});
		


		saveBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (!CommonUtils.isStringEmpty(stepView.getText().toString().trim()) && CommonUtils.isNumeric(stepView.getText().toString().trim()))
				{
					dataGoal = stepView.getText().toString().trim();
					new DataAsyncTask().execute();
				}
				else
				{
					new com.eva.android.widgetx.AlertDialog.Builder(GoalActivity.this).setTitle(R.string.goal_step_error_title)
							.setMessage(R.string.goal_step_error_message).setPositiveButton(R.string.general_ok, null).show();
				}
			}
		});
	}
	
	/**
	 * 根据进度值，来决定推荐值ui的显示效果等.
	 * 
	 * @param progress
	 */
	private void refreshSuggestUI(int progress)
	{
//		if(progress == tuijianProcess)
//		{
//			tuijianView.setBackground(getResources().getDrawable(R.drawable.goal_tuijian_gaoliang));
//			tuijianView.setClickable(false);
//		}
//		else
//		{
			tuijianView.setBackground(getResources().getDrawable(R.drawable.goal_tuijian_btn_selector));
			tuijianView.setClickable(true);
//		}
	}

	@Override
	protected DataFromServer queryData(String... arg0)
	{
		//判断有无网络
		if(ToolKits.isNetworkConnected(this))
		{
			return HttpServiceFactory4AJASONImpl
				.getInstance()
				.getDefaultService()
				.sendObjToServer(
						DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_USERSETTING).setJobDispatchId(JobDispatchConst.USER_SETTINGS_GOAL)
								.setActionId(SysActionConst.ACTION_APPEND3).setNewData(MyApplication.getInstance(this).getLocalUserInfoProvider().getUser_id()));
		}
		else
		{
			DataFromServer dfs = new DataFromServer();
			dfs.setSuccess(true);
			
			JSONObject obj = new JSONObject();
			LocalSetting setting = LocalUserSettingsToolkits.getLocalSettingInfo(this, MyApplication.getInstance(this).getLocalUserInfoProvider().getUser_mail());
			
			if(setting != null && !CommonUtils.isStringEmpty(setting.getGoal()))
			{
				obj.put("goal", setting.getGoal());
				obj.put("goal_update", setting.getGoal_update());
			}
			else
			{
				obj.put("goal", MyApplication.getInstance(this).getLocalUserInfoProvider().getPlay_calory());
				obj.put("goal_update", MyApplication.getInstance(this).getLocalUserInfoProvider().getGoal_update());
			}
			
			dfs.setReturnValue(obj.toJSONString());
			return dfs;
		}
	}

	@Override
	protected void refreshToView(Object result)
	{
		int progress = 0;
		if (result != null)
		{
			JSONObject obj = JSON.parseObject((String)result);
			stepView.setText(obj.getString("goal"));
			caloryView.setText((int) (Integer.parseInt(obj.getString("goal")) / 1000.0 * 38.0) + "");
			
			progress = CommonUtils.getIntValue(obj.getString("goal"));
			seekBar.setProgress(progress);
		}
		
		tuijianProcess = _Utils.getDefultGoalByBirthdate(MyApplication.getInstance(this).getLocalUserInfoProvider().getBirthdate());
		int margin = (int) ((tuijianProcess * 1.0 / seekBar.getMax()) * seekBar.getWidth());
		tuijianView.setText($$(R.string.goal_recommend) + tuijianProcess + $$(R.string.goal_step));
		FrameLayout.LayoutParams flp = (FrameLayout.LayoutParams)tuijianView.getLayoutParams(); 
		flp.setMargins(margin - (tuijianView.getWidth() / 2) + ToolKits.dip2px(GoalActivity.this, 25), 0, 0, 0);    
		tuijianView.setLayoutParams(flp);
		
		refreshSuggestUI(progress);
	}

	/**
	 * 提交数据请求和处理的异步执行线程实现类.
	 */
	protected class DataAsyncTask extends DataLoadingAsyncTask<String, Integer, DataFromServer>
	{
		public DataAsyncTask()
		{
			super(GoalActivity.this, $$(R.string.general_submitting));
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
			
			localSetting.setUser_mail(MyApplication.getInstance(GoalActivity.this).getLocalUserInfoProvider().getUser_mail());
			localSetting.setGoal(dataGoal);
			localSetting.setGoal_update(update_time);
			LocalUserSettingsToolkits.setLocalSettingGoalInfo(context, localSetting);
			
			
			JSONObject dataObj = new JSONObject();
			dataObj.put("goal", dataGoal);
			dataObj.put("goal_update", update_time);
			dataObj.put("user_id", MyApplication.getInstance(GoalActivity.this).getLocalUserInfoProvider().getUser_id());
			
			
			if(ToolKits.isNetworkConnected(GoalActivity.this))
			{
				return HttpServiceFactory4AJASONImpl
						.getInstance()
						.getDefaultService()
						.sendObjToServer(
								DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_USERSETTING).setJobDispatchId(JobDispatchConst.USER_SETTINGS_GOAL)
										.setActionId(SysActionConst.ACTION_APPEND4).setNewData(dataObj.toJSONString()));
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
			if (result != null)
			{
				JSONObject obj = JSON.parseObject((String) result);
				MyApplication.getInstance(context).getLocalUserInfoProvider().setPlay_calory(obj.getString("goal"));
				System.out.println(TAG+"--->"+obj.getString("goal"));
				MyApplication.getInstance(context).getLocalUserInfoProvider().setGoal_update(obj.getString("goal_update"));
				
				if(ToolKits.isNetworkConnected(GoalActivity.this))
					//删除内存
					LocalUserSettingsToolkits.removeLocalSettingGoalInfo(context, MyApplication.getInstance(GoalActivity.this).getLocalUserInfoProvider().getUser_mail());
				
				ToolKits.showCommonTosat(GoalActivity.this, true, $$(R.string.setting_success), Toast.LENGTH_LONG);
				refreshToView(result);
				UserEntity userEntity = MyApplication.getInstance(GoalActivity.this).getLocalUserInfoProvider();
				try {
					provider.setTarget(GoalActivity.this, DeviceInfoHelper.fromUserEntity(userEntity));
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
