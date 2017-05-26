package com.linkloving.rtring_c_watch.logic.setup;

import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eva.android.platf.std.AutoUpdateDaemon;
import com.eva.android.platf.std.DataLoadableActivity;
import com.eva.android.widget.DataLoadingAsyncTask;
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
import com.linkloving.rtring_c_watch.utils.ObservableHorizontalScrollView;
import com.linkloving.rtring_c_watch.utils.ObservableHorizontalScrollView.OnScrollStopListner;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.JobDispatchConst;
import com.rtring.buiness.logic.dto.UserEntity;

public class MovingTargetActivity extends DataLoadableActivity
{
	
	public final static int COME_FROM_REGISTER_ACTIVITY = 1111;
	public final static int COME_FROM_WATCH_SETTING_ACTIVITY = 2222;
	
	private final static int SEX_MAN = 1;
	
	
	
	/** 显示性别的图片 */
	private ImageView body_sex = null;
	/** BMI指数 */
	private TextView body_BMI = null;
	/** BMI指数对应的身体状况 */
	private TextView body_BMIDesc = null;
	/** 推荐目标运动步数 */
	private TextView recommend_target_step = null;
	/** 手动目标运动步数 */
	private TextView target_step = null;
	/** 滑动目标运动步数 */
	private ObservableHorizontalScrollView target_scrollview;
	/** 完成按钮 */
	private Button target_info_save_btn = null;

	private String sex;
	private String BMI;
	private String BMIDesc;
	private int from;
	private String target;

	private Handler handler;

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
			return MovingTargetActivity.this;
		}

		@Override
		public void updateFor_handleSetTime()
		{

		}
	};

	@Override
	protected void onResume()
	{
		super.onResume();
		if (from != COME_FROM_REGISTER_ACTIVITY)
			provider.setBleProviderObserver(bleProviderObserver);
	}

	protected void onDestroy()
	{
		super.onDestroy();
		if (from != COME_FROM_REGISTER_ACTIVITY)
			if (provider.getBleProviderObserver() == bleProviderObserver)
				provider.setBleProviderObserver(null);
	}

	@Override
	protected void initDataFromIntent()
	{
		Intent intent = getIntent();
		from = intent.getIntExtra("type", 0);
		sex = intent.getStringExtra("user_sex"); // sex =1 男
		BMI = intent.getStringExtra("user_BMI");
		BMIDesc = intent.getStringExtra("user_BMIDesc");
		target =intent.getStringExtra("user_target");
	}

	@Override
	protected void initViews()
	{
		customeTitleBarResId = R.id.moving_target_info_titleBar;
		// 首先设置contentview
		setContentView(R.layout.moving_targer_info_activity);
		body_sex = (ImageView) findViewById(R.id.movingtarget_sex);
		body_BMI = (TextView) findViewById(R.id.movingtarget_BMI);
		body_BMIDesc = (TextView) findViewById(R.id.movingtarget_BMIDesc);

		recommend_target_step = (TextView) findViewById(R.id.recommend_target_step);
		target_step = (TextView) findViewById(R.id.target_step);
		target_scrollview = (ObservableHorizontalScrollView) findViewById(R.id.target_scrollview);
		target_info_save_btn = (Button) findViewById(R.id.target_info_save_btn);
		
		if(from != COME_FROM_REGISTER_ACTIVITY){
			this.setTitle("运动目标");
		         provider = MyApplication.getInstance(this).getCurrentHandlerProvider();
		    }
			else{
				this.setTitle("检测报告");
			}
				
	}

	@Override
	protected void initListeners()
	{
		// 设置性别图片
		if (Integer.parseInt(sex) == SEX_MAN)
		{
			body_sex.setBackgroundResource(R.drawable.movingtarget_man);
		}
		else
			body_sex.setBackgroundResource(R.drawable.movingtarget_woman);

		body_BMI.setText(BMI);
		body_BMIDesc.setText(BMIDesc);

		// 判断从哪个页面进入设置目标步数的
		switch (from)
		{

		case COME_FROM_REGISTER_ACTIVITY:
			// 系统所推荐的步数
			recommend_target_step.setText("12000");
			target_step.setText("12000");
			Runnable runnable1 = new Runnable()
			{
				@Override
				public void run()
				{
					target_scrollview.scrollTo(new ObservableHorizontalScrollView(getApplicationContext()).int2int((((Integer.parseInt(target_step.getText().toString())) -2500)/100)* 7), 0);// 改变滚动条的位置
				}
			};
			handler = new Handler();
			handler.postDelayed(runnable1, 100);
			break;
		case COME_FROM_WATCH_SETTING_ACTIVITY:
			// 界面所传来的步数
			recommend_target_step.setText("12000");
			target_step.setText(target);
			Log.i("MovingTargetActivity",target);
			Runnable runnable2 = new Runnable()
			{
				@Override
				public void run()
				{
					//Integer.parseInt(recommend_target_step.getText().toString())
					target_scrollview.scrollTo(new ObservableHorizontalScrollView(MovingTargetActivity.this).int2int((((Integer.parseInt(target)) -2500)/100)* 7), 0);// 改变滚动条的位置
				}
			};
			handler = new Handler();
			handler.postDelayed(runnable2, 100);
			break;

		default:
			break;
		}
		// 运动步数的滑动
		target_scrollview.setOnTouchListener(new OnTouchListener()
		{
			public boolean onTouch(View v, MotionEvent event)
			{
				if (event.getAction() == MotionEvent.ACTION_UP)
				{
					target_scrollview.startScrollerTask();
				}
				else if (event.getAction() == MotionEvent.ACTION_MOVE)
				{
					target_scrollview.startScrollerTask();
				}
				return false;
			}
		});
		// 运动步数重的滑动停止的监听
		target_scrollview.setOnScrollStopListner(new OnScrollStopListner()
		{
			public void onScrollChange(int index)
			{
				if (index == 0)
				{
					target_step.setText("2500");
				}
				else
				{
					int value = new ObservableHorizontalScrollView(getApplicationContext()).px2dip(index);
					target_step.setText((value / 7 * 100 + 2500) + "");
				}
			}
		});
		// 完成按钮
		target_info_save_btn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				switch (from)
				{

				case COME_FROM_REGISTER_ACTIVITY:

					Intent data = new Intent();
					data.putExtra("target_step", target_step.getText().toString());
					setResult(Activity.RESULT_OK, data);
					finish();
					break;
				case COME_FROM_WATCH_SETTING_ACTIVITY:
//					Intent data = new Intent();
//					data.putExtra("target_step", target_step.getText().toString());
//					setResult(Activity.RESULT_OK, data);
//					finish();
//					break;
					new DataAsyncTask().execute();
					finish();
					break;
				default:
					break;
				}

			}
		});
	}

	/**
	 * 提交数据请求和处理的异步执行线程实现类.
	 */
	protected class DataAsyncTask extends DataLoadingAsyncTask<String, Integer, DataFromServer>
	{
		public DataAsyncTask()
		{
			super(MovingTargetActivity.this, $$(R.string.general_submitting));
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

			localSetting.setUser_mail(MyApplication.getInstance(MovingTargetActivity.this).getLocalUserInfoProvider().getUser_mail());
			localSetting.setGoal(target_step.getText().toString().trim());
			localSetting.setGoal_update(update_time);
			LocalUserSettingsToolkits.setLocalSettingGoalInfo(context, localSetting);

			JSONObject dataObj = new JSONObject();
			dataObj.put("goal", target_step.getText().toString().trim());
			dataObj.put("goal_update", update_time);
			dataObj.put("user_id", MyApplication.getInstance(MovingTargetActivity.this).getLocalUserInfoProvider().getUser_id());

			if (ToolKits.isNetworkConnected(MovingTargetActivity.this))
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
				MyApplication.getInstance(context).getLocalUserInfoProvider().setGoal_update(obj.getString("goal_update"));

				if (ToolKits.isNetworkConnected(MovingTargetActivity.this))
					// 删除内存
					LocalUserSettingsToolkits.removeLocalSettingGoalInfo(context, MyApplication.getInstance(MovingTargetActivity.this).getLocalUserInfoProvider().getUser_mail());
				ToolKits.showCommonTosat(MovingTargetActivity.this, true, $$(R.string.setting_success), Toast.LENGTH_LONG);
				refreshToView(result);
				UserEntity userEntity = MyApplication.getInstance(MovingTargetActivity.this).getLocalUserInfoProvider();
				provider = MyApplication.getInstance(MovingTargetActivity.this).getCurrentHandlerProvider();
//				provider.setTarget(MovingTargetActivity.this, DeviceInfoHelper.fromUserEntity(userEntity));
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
