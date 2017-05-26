package com.linkloving.rtring_c_watch.logic.setup;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.FrameLayout;
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
import com.example.android.bluetoothlegatt.BLEProvider;
import com.linkloving.rtring_c_watch.LocalUserSettingsToolkits;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.linkloving.rtring_c_watch.logic.model.LocalSetting;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.JobDispatchConst;
import com.rtring.buiness.logic.dto.UserEntity;
/**
 * 
 * @author Administrator
 *cmdID_ANCS_Switch = 0x12,  
   enum {
         ANCS_APPNameID_LINKLOVING=0,
         ANCS_APPNameID_Phone=1,
         ANCS_APPNameID_SMS=2,
         ANCS_APPNameID_WEIXIN=3,
         ANCS_APPNameID_QQ=4,
         ANCS_APPNameID_UNKNOW = 0xFF,
 */
public class NotifacitionActivity extends DataLoadableActivity
{

	private static String TAG = NotifacitionActivity.class.getSimpleName();
	private FrameLayout notification_phone = null;
	private CheckBox call_switch_checkbox = null;
	private CheckBox msg_switch_checkbox = null;
	private CheckBox QQ_switch_checkbox = null;
	private CheckBox weixin_switch_checkbox = null;
	private CheckBox linkloving_switch_checkbox = null;

	private BLEProvider provider = null;

	public static byte[] intto2byte(int a)
	{
		byte[] m = new byte[2];
		m[0] = (byte) ((0xff & a));
		m[1] = (byte) (0xff & (a >> 8));
		return m;
	}

	private byte[] send_data;
	private int notif_data;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		if (android.os.Build.MANUFACTURER.equalsIgnoreCase("meizu"))
			this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// 去掉信息栏
        Log.e(TAG, "设备参数:"+android.os.Build.MODEL);
		super.onCreate(savedInstanceState);
		provider = MyApplication.getInstance(this).getCurrentHandlerProvider();
	}

	@Override
	protected void initViews()
	{
		customeTitleBarResId = R.id.notification_titleBar;
		setContentView(R.layout.notifications_activity);
		notification_phone = (FrameLayout) findViewById(R.id.notification_phone);
		call_switch_checkbox = (CheckBox) findViewById(R.id.call_switch_checkbox);
		msg_switch_checkbox = (CheckBox) findViewById(R.id.msg_switch_checkbox);
		QQ_switch_checkbox = (CheckBox) findViewById(R.id.QQ_switch_checkbox);
		weixin_switch_checkbox = (CheckBox) findViewById(R.id.weixin_switch_checkbox);
		linkloving_switch_checkbox = (CheckBox) findViewById(R.id.linkloving_switch_checkbox);
		this.setTitle("消息提醒");
	}

	@Override
	protected void initListeners()
	{
//		if (android.os.Build.MODEL.equalsIgnoreCase("X600")){
//			call_switch_checkbox.setChecked(false);
//			notification_phone.setVisibility(View.GONE);
//		}
			
		
		call_switch_checkbox.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				setNotificition();
			}

		});
		msg_switch_checkbox.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				setNotificition();
			}

		});
		QQ_switch_checkbox.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				setNotificition();
			}

		});
		weixin_switch_checkbox.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				setNotificition();
			}

		});
		linkloving_switch_checkbox.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				setNotificition();
			}
		});
	}

	private void setNotificition()
	{
		

		String notif_ = "" +(QQ_switch_checkbox.isChecked() ? 1 : 0)+ (weixin_switch_checkbox.isChecked() ? 1 : 0)+ (msg_switch_checkbox.isChecked() ? 1 : 0) + (call_switch_checkbox.isChecked() ? 1 : 0)+ (linkloving_switch_checkbox.isChecked() ? 1 : 0);
		notif_data = Integer.parseInt(notif_, 2);
		send_data = intto2byte(notif_data);
		new NotificationAsyncTask().execute();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	/**
	 * 提交数据请求和处理的异步执行线程实现类.
	 */
	protected class NotificationAsyncTask extends DataLoadingAsyncTask<String, Integer, DataFromServer>
	{
		public NotificationAsyncTask()
		{
			super(NotifacitionActivity.this, $$(R.string.general_submitting));
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
			long update_time = ToolKits.getDayFromDate(new Date(), 0).getTime();
			JSONObject datanotify = new JSONObject();
			datanotify.put("ancs", notif_data);
			datanotify.put("ancs_update", update_time);
			datanotify.put("user_id", MyApplication.getInstance(NotifacitionActivity.this).getLocalUserInfoProvider().getUser_id());
			// 判断有无网络
			if (ToolKits.isNetworkConnected(NotifacitionActivity.this))
			{
				return HttpServiceFactory4AJASONImpl
						.getInstance()
						.getDefaultService()
						.sendObjToServer(
								DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_USERSETTING)
										.setJobDispatchId(JobDispatchConst.USER_SETTING_REMIND).setActionId(SysActionConst.ACTION_APPEND4)
										.setNewData(datanotify.toJSONString()));
			}
			else
			{
				DataFromServer dfs = new DataFromServer();
				dfs.setSuccess(true);
				dfs.setReturnValue(JSON.toJSONString(datanotify));
				return dfs;
			}
		}
		/**
		 * 处理服务端返回的信息.
		 * 
		 * @see AutoUpdateDaemon
		 * @see #needSaveDefaultLoginName()
		 * @see #afterLoginSucess()
		 */
		protected void onPostExecuteImpl(Object result)
		{
			if (result != null)
			{

				ToolKits.showCommonTosat(NotifacitionActivity.this, true, $$(R.string.setting_success), Toast.LENGTH_LONG);
				refreshToView(result);
				UserEntity userEntity = MyApplication.getInstance(NotifacitionActivity.this).getLocalUserInfoProvider();
				// 更新本地用户数据
				userEntity.setAncs(notif_data + "");
				userEntity.setAncs_update(ToolKits.getDayFromDate(new Date(), 0).getTime() + "");
				// 判断有无网络
				if (ToolKits.isNetworkConnected(NotifacitionActivity.this))
					// 删除内存
					LocalUserSettingsToolkits.removeLocalAncsInfo(context, MyApplication.getInstance(NotifacitionActivity.this).getLocalUserInfoProvider()
							.getUser_mail());
				 provider.setNotification(NotifacitionActivity.this,send_data);
			}
		}
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
							DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_USERSETTING).setJobDispatchId(JobDispatchConst.USER_SETTING_REMIND)
									.setActionId(SysActionConst.ACTION_APPEND3)
									.setNewData(MyApplication.getInstance(this).getLocalUserInfoProvider().getUser_id()));
		}
		else
		{
			DataFromServer dfs = new DataFromServer();
			dfs.setSuccess(true);

			LocalSetting setting = LocalUserSettingsToolkits.getLocalSettingInfo(this, MyApplication.getInstance(this).getLocalUserInfoProvider()
					.getUser_mail());

			if (setting == null || CommonUtils.isStringEmpty(setting.getAncs() + ""))
			{
				String Ansc = MyApplication.getInstance(this).getLocalUserInfoProvider().getAncs();
				String Ansc_str = Integer.toBinaryString(Integer.parseInt(Ansc));
				char[] array = { 0, 0, 0, 0, 0 };
				char[] charr = Ansc_str.toCharArray(); // 将字符串转换为字符数组
				System.arraycopy(charr, 0, array, 5 - charr.length, charr.length);

				linkloving_switch_checkbox.setChecked(array[4] == '1');
				call_switch_checkbox.setChecked(array[3] == '1');
				msg_switch_checkbox.setChecked(array[2] == '1');
				weixin_switch_checkbox.setChecked(array[1] == '1');
				QQ_switch_checkbox.setChecked(array[0] == '1');
			}
			else
			{

				int Ansc = setting.getAncs();
				String Ansc_str = Integer.toBinaryString(Ansc);
				char[] array = { 0, 0, 0, 0, 0 };
				char[] charr = Ansc_str.toCharArray();
				System.arraycopy(charr, 0, array, 5 - charr.length, charr.length); // 将字符串转换为字符数组
				linkloving_switch_checkbox.setChecked(array[4] == '1');
				call_switch_checkbox.setChecked(array[3] == '1');
				msg_switch_checkbox.setChecked(array[2] == '1');
				weixin_switch_checkbox.setChecked(array[1] == '1');
				QQ_switch_checkbox.setChecked(array[0] == '1');
			}
			Map<String, String> map = new HashMap<String, String>();
			map.put("Ansc", setting.getAncs() + "");
			map.put("Ansc_update", setting.getAncs_update() + "");
			dfs.setReturnValue(JSON.toJSONString(map));
			return dfs;
		}
	}

	@Override
	protected void refreshToView(Object result)
	{

		//
		if (result != null)
		{
			Log.i("NotifacitionActivity", result.toString());
			String json = result.toString();
			JSONObject jsonObject = JSONObject.parseObject(json);
			if(jsonObject.getIntValue("ancs")!=0){
				String Ansc_str = Integer.toBinaryString(jsonObject.getIntValue("ancs"));
				char[] array = { 0, 0, 0, 0, 0 };
				char[] charr = Ansc_str.toCharArray(); // 将字符串转换为字符数组
				System.arraycopy(charr, 0, array, 5 - charr.length, charr.length);
				StringBuffer sb1 = new StringBuffer();
				sb1.append("array:" + "[");
				for (int i = 0; i < array.length; i++)
				{
					sb1.append(array[i] + " ");
				}
				sb1.append("]");
				Log.i(TAG, sb1.toString());
				linkloving_switch_checkbox.setChecked(array[4] == '1');
				call_switch_checkbox.setChecked(array[3] == '1');
				msg_switch_checkbox.setChecked(array[2] == '1');
				weixin_switch_checkbox.setChecked(array[1] == '1');
				QQ_switch_checkbox.setChecked(array[0] == '1');
//				if (android.os.Build.MODEL.equalsIgnoreCase("X600")){
//					call_switch_checkbox.setChecked(false);
//					notification_phone.setVisibility(View.GONE);
//				}  test
			}else
			{
				linkloving_switch_checkbox.setChecked(false);
				call_switch_checkbox.setChecked(false);
				msg_switch_checkbox.setChecked(false);
				weixin_switch_checkbox.setChecked(false);
				QQ_switch_checkbox.setChecked(false);
			}
			
		}

	}

}
