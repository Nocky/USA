package com.linkloving.rtring_c_watch.utils;

import java.util.Observer;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eva.android.platf.std.AutoUpdateDaemon;
import com.eva.android.platf.std.dto.AutoUpdateInfoFromServer;
import com.eva.android.widgetx.AlertDialog;
import com.eva.android.x.AsyncTaskManger;
import com.eva.epc.common.util.CommonUtils;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.google.gson.Gson;
import com.linkloving.rtring_c_watch.LocalUserSettingsToolkits;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.linkloving.rtring_c_watch.logic.model.LocalSetting;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.LoginInfoExtension;
import com.rtring.buiness.logic.dto.UserEntity;

/**
 * 提交用户登陆请求到服务端并接收处理结果的异常线程实现类.
 */
public abstract class UpdateClientAsyncTask extends AsyncTask<String, Integer, DataFromServer> 
{
	private final static String TAG = UpdateClientAsyncTask.class.getSimpleName();
	
	private Activity activity = null;
	private LoginInfoExtension ai = null;
	/** 当最新用户信息读取回来时要通知的观察者 */
	private Observer obsForUserInfoFetchSucess = null;
	
	public UpdateClientAsyncTask(Activity activity, LoginInfoExtension ai, Observer obsForUserInfoFetchSucess)
	{
		this.activity = activity;
		this.ai = ai;
		this.obsForUserInfoFetchSucess = obsForUserInfoFetchSucess;

		AsyncTaskManger.getAsyncTaskManger().addAsyncTask(this);

	}
	
	/**
	 * 在后台执行 {@link doLogin()}实现登陆信息的提交和处于结果的读取 .
	 * @param parems 外界传进来的参数
	 * @return 查询结果，将传递给onPostExecute(..)方法
	 */
	@Override
	protected DataFromServer doInBackground(String... parems) 
	{
		LocalSetting setting = LocalUserSettingsToolkits.getLocalSettingInfo(activity, ai.getLoginName());
		
		if(setting != null)
		{
			if(!CommonUtils.isStringEmpty(setting.getAlarm_list()))
			{
				ai.setAlarm(setting.getAlarm_list());
				ai.setAlarm_update(setting.getAlarm_update());
			}
			
			if(!CommonUtils.isStringEmpty(setting.getGoal()))
			{
				ai.setGoal(setting.getGoal());
				ai.setGoal_update(setting.getGoal_update());
				
			}
			
			if(!CommonUtils.isStringEmpty(setting.getLong_sit_time()))
			{
				ai.setLongsit(setting.getLong_sit() + "");
				ai.setLongsit_time(setting.getLong_sit_time());
				ai.setLongsit_update(setting.getLong_sit_update());
			}
		}
		
		// 提交请求到服务端
		DataFromClient dataFromClient = DataFromClient.n()
				.setProcessorId(MyProcessorConst.PROCESSOR_ANDROID_CLIENT_VER_CHECK2)		
				.setNewData(JSON.toJSONString(ai));// 注意：目前的通信协议是扁平JASON文本，不支持直接传输java序列化对象！
		return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(dataFromClient);
	}

	/**
	 * 处理服务端返回的登陆结果信息.
	 * 
	 * @see AutoUpdateDaemon
	 * @see #needSaveDefaultLoginName()
	 * @see #afterLoginSucess()
	 */
	protected void onPostExecute(DataFromServer result) 
	{
		if (result != null)
		{
			if(result.getReturnValue() instanceof String)
			{
				try
				{
					JSONObject nwObj = JSONObject.parseObject((String)result.getReturnValue());
					String updateInfoJson = nwObj.getString("update_info");
					String userInfoJson = nwObj.getString("authed_info");

					System.out.println("服务端返回>>updateInfoJson="+updateInfoJson+", userInfoJson="+userInfoJson);

					// 登陆验证失败
					if(userInfoJson == null)
					{
						new AlertDialog.Builder(activity)
						.setTitle(R.string.login_form_error_psw_tip)
						.setMessage(R.string.login_form_error_psw_message)
						.setCancelable(false)
						.setPositiveButton(activity.getString(R.string.login_form_relogin_text), new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog,int which) 
							{
								relogin();
							}
						})
						.show();
					}
					// 登陆验证成功
					else
					{
						final AutoUpdateInfoFromServer aui = new Gson().fromJson(updateInfoJson, AutoUpdateInfoFromServer.class);
						final UserEntity ue = new Gson().fromJson(userInfoJson, UserEntity.class);
						
						UserEntity user_local = MyApplication.getInstance(activity).getLocalUserInfoProvider();
						Log.i(TAG, TAG+":"+user_local.getDevice_type());
						if(ue.getDevice_type().equals(""))
							ue.setDevice_type(user_local.getDevice_type());
							MyApplication.getInstance(activity).setLocalUserInfoProvider(ue);
						// 先把最新用户信息保存起来
						
						if(obsForUserInfoFetchSucess != null)
							obsForUserInfoFetchSucess.update(null, ue);
						// app有更新的版本
						if (aui.isNeedUpdate()) 
						{
							Log.d(TAG, "isNeedUpdate?" + aui.isNeedUpdate()
									+ ",getLatestClientAPKVercionCode="
									+ aui.getLatestClientAPKVercionCode()
									+ ",getLatestClientAPKFileSize="
									+ aui.getLatestClientAPKFileSize()
									+ ",getLatestClientAPKURL"
									+ aui.getLatestClientAPKURL());

							new AlertDialog.Builder(activity)
							.setTitle(activity.getString(R.string.login_form_have_latest_version))
							.setMessage(activity.getString(R.string.login_form_have_latest_version_descrption))
							.setPositiveButton(activity.getString(R.string.login_form_have_latest_version_update_now), new DialogInterface.OnClickListener() 
							{
								@Override
								public void onClick(DialogInterface dialog,int which) 
								{
									fireUpdate(aui);
								}
							})
							.setNegativeButton(activity.getString(R.string.login_form_have_latest_version_ignore), null)
							.show();
						}
						else
						{
							Log.d(TAG, "客户端当前已是最新版，无需更新.");
						}
					}
				}
				catch (Exception e)
				{
					Log.w(TAG, e.getMessage(), e);
				}
			} 
			else
				Log.d(TAG, "服务端返回了无效的版本更新信息！"+ result.getReturnValue());
		} 
		else
			Log.d(TAG, "服务端返回的版本更新信息为null！");
		
		AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(this);
	}
	
	/** 登陆验证失败时会调用的方法 */
	protected abstract void relogin();

	// 版本更新实施方法
	public void fireUpdate(AutoUpdateInfoFromServer aui) 
	{
		// 进入版本更新处理类进行更新处理
		AutoUpdateDaemon up = new AutoUpdateDaemon(activity,
				aui.getLatestClientAPKVercionCode(),
				aui.getLatestClientAPKFileSize(),
				aui.getLatestClientAPKURL());
		try {
			up.doUpdate();
		} 
		catch (Exception e) {
			Toast.makeText(activity,
					"版本更新时发生错误："+ e.getMessage(),
					Toast.LENGTH_LONG).show();
			Log.d(TAG, "新版下载时遇到错误，" + e.getMessage(), e);
		}
	}
	
	

}