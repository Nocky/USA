package com.linkloving.rtring_c_watch;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.eva.epc.common.util.CommonUtils;
import com.eva.epc.core.dto.LoginInfo2;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.linkloving.band.dto.SleepData;
import com.linkloving.band.ui.DetailChartCountData;
import com.linkloving.rtring_c_watch.logic.model.LocalInfoVO;
import com.rtring.buiness.logic.dto.UserEntity;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

/**
 * Preferences辅助类.
 * 
 * @author Jack Jiang, 2014-04-01
 * @since 2.5
 */
public class PreferencesToolkits
{
	private final static String TAG = PreferencesToolkits.class.getSimpleName();
	
	/** 存储用户最近登陆用户名的key标识常量（本Shared Preferences目前只在LoginActivity内有效果哦） */
	public final static String SHARED_PREFERENCES_KEY_LOGIN$NAME = "name";
	/** 
	 * 存储常量的Shared Preferences标识常量（根据Android的Shared Preferences原理，如果不指名名字，则
	 * 用的是对应Activity的包名加类名作为名称，那么其它activity就很难取到罗）  */
	public final static String SHARED_PREFERENCES_SHAREDPREFERENCES = "__sharedpreferences__";
	
	public final static String KEY_DEVICE_INFO = "__deviceinfo__";
	/** 
	 * 存储用户登陆成功后服务端端返回的完整个人信息的Shared Preferences标识常量（根据Android的
	 * Shared Preferences原理，如果不指名名字，则用的是对应Activity的包名加类名作为名称，那么其它activity就很难取到罗）  */
	public final static String KEY_LOGIN_USER_INFO = "__userinfo__";
	/** 
	 * 存储用户登陆登陆信息（包括登陆名、密码）的Shared Preferences标识常量（根据Android的
	 * Shared Preferences原理，如果不指名名字，则用的是对应Activity的包名加类名作为名称，那么其它activity就很难取到罗）  */
//	public final static String KEY_LOGIN_LOGIN_INFO2 = "__logininfo__";
	
	public final static String KEY_LOGIN_LOGIN_INFO2 = "__logininfo5__";
	
	/**
	 * 是否上传到google健康
	 */
	public final static String KEY_GOOGLE_FIT = "__googlefit__";
	/**
	 * 用户设置信息（久坐提醒，闹钟，运动目标）
	 */
	public final static String KEY_USER_SETTING_INFO = "__usersetting__";
	
	
	/** 
	 * 存储“是否在主界面上显示可下拉刷新的提示”的key标识常量（对应的Shared Preferences标识常量
	 * 是 {@link #SHARED_PREFERENCES_SHAREDPREFERENCES} ） */
	public final static String KEY_NEED_SHOW_FRAGMENT_TIP = "__need_show_fragment_tip__";
	
	/** 
	 * 存储“是否在主界面上显示升级OAD的提示”的key标识常量（对应的Shared Preferences标识常量
	 * 是 {@link #SHARED_PREFERENCES_SHAREDPREFERENCES} ） */
	public final static String KEY_NEED_SHOW_OAD = "__need_show_oad__";
	
	
	/** 
	 * 存储“是否在主界面上显示升级ENT的提示”的key标识常量（对应的Shared Preferences标识常量
	 * 是 {@link #SHARED_PREFERENCES_SHAREDPREFERENCES} ） */
	public final static String KEY_NEED_SHOW_ENT = "__need_show_ent__";
	
	/** 
	 * 存储“单位”的key标识常量（对应的Shared Preferences标识常量
	 * 是 {@link #SHARED_PREFERENCES_SHAREDPREFERENCES} ） */
	public final static String KEY_UNIT_TYPE = "__unit_type__";
	
	public static SharedPreferences getAppDefaultSharedPreferences(Context context, boolean canWrite) throws Exception 
	{
		if(context == null)
		{
			throw new Exception("context 为空");
		}
		return context.getSharedPreferences(
				SHARED_PREFERENCES_SHAREDPREFERENCES, canWrite?Context.MODE_WORLD_WRITEABLE:Context.MODE_WORLD_READABLE);
	}
	
	
	public static LocalInfoVO getLocalDeviceInfo(Context context)
	{
		try
		{
			String id = MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id();
			SharedPreferences sharedPreferences = getAppDefaultSharedPreferences(context, false);
			String jString = sharedPreferences.getString(KEY_DEVICE_INFO, "");
//			Log.e(TAG, "localInfoVO的信息:"+jString);
			if (CommonUtils.isStringEmpty(jString))
			{
				return new LocalInfoVO();
			}
			else
			{
				LocalInfoVO vo = new Gson().fromJson(jString, LocalInfoVO.class);
				if(vo.userId != null && vo.userId.equals(id))
				{
//					Log.e(TAG, "localInfoVO的信息 vo.deviceStatus:"+vo.deviceStatus);

					return  vo;
				}
				return new LocalInfoVO();
			}
		} 
		catch (Exception e)
		{
			Log.e(PreferencesToolkits.class.getSimpleName(), e.getMessage());
			return new LocalInfoVO();
		}
	
	}

	private static void setLocalDeviceInfo(Context context, LocalInfoVO localInfoVO)
	{
		String id = MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id();
		localInfoVO.userId = id;
		String jString = new Gson().toJson(localInfoVO);
		SharedPreferences sharedPreferences;
		try 
		{
			sharedPreferences = getAppDefaultSharedPreferences(context, true);
			SharedPreferences.Editor edit = sharedPreferences.edit();
			edit.putString(KEY_DEVICE_INFO, jString);
			edit.commit();
		} 
		catch (Exception e) 
		{
			Log.e(TAG, e.getMessage());
		}
		
	}
     
     
	public static UserEntity getLocalUserInfo(Context context)
	{
		SharedPreferences sharedPreferences;
		try
		{
			sharedPreferences = getAppDefaultSharedPreferences(context, false);
			String jString = sharedPreferences.getString(KEY_LOGIN_USER_INFO, "");
			if (CommonUtils.isStringEmpty(jString))
				return new UserEntity();
			else
				return new Gson().fromJson(jString, UserEntity.class);
		} 
		catch (Exception e)
		{
			Log.e(TAG, e.getMessage());
			return new UserEntity();
		}
	
	}
	/**
	 * 用于免登陆时的用户信息读取方法.
	 * <p>
	 * 与 {@link getLocalUserInfo(Context)}唯一的不同在于当Preference中不存在时返回null而非空
	 * UserEntity对象.
	 * 
	 * @param context
	 * @return
	 */
	public static UserEntity getLocalUserInfoForLaunch(Context context)
	{
		SharedPreferences sharedPreferences;
		try 
		{
			sharedPreferences = getAppDefaultSharedPreferences(context, false);
			String jString = sharedPreferences.getString(KEY_LOGIN_USER_INFO, "");
			if (CommonUtils.isStringEmpty(jString))
				return null;
			else
				return new Gson().fromJson(jString, UserEntity.class);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
	
	}

	public static void setLocalUserInfo(Context context, UserEntity userInfo)
	{
		String jString = new Gson().toJson(userInfo);
		SharedPreferences sharedPreferences;
		try 
		{
			sharedPreferences = getAppDefaultSharedPreferences(context, true);
			SharedPreferences.Editor edit = sharedPreferences.edit();
			edit.putString(KEY_LOGIN_USER_INFO, jString);
			edit.commit();
		}
		catch (Exception e)
		{
			Log.e(TAG, e.getMessage());
		}
	
	}
	
     
     public static void updateLocalDeviceInfo(Context context,LPDeviceInfo deviceInfo)
     {
    	 LocalInfoVO localInfoVO = getLocalDeviceInfo(context);
    	 if(localInfoVO == null)
    	 {
    	      LocalInfoVO temp =  new LocalInfoVO();
    	      temp.updateByDeviceInfo(context, deviceInfo);
    	      setLocalDeviceInfo(context, temp);
    	 }
    	 else 
    	 {
		        localInfoVO	.updateByDeviceInfo(context, deviceInfo);
		        setLocalDeviceInfo(context, localInfoVO);
		 }
     }
     
     public static void updateLocalDeviceInfo(Context context,SleepData sleepInfo)
     {
    	 LocalInfoVO localInfoVO = getLocalDeviceInfo(context);
    	 if(localInfoVO == null)
    	 {
    	      LocalInfoVO temp =  new LocalInfoVO();
    	      temp.updateBySleepInfo(sleepInfo);
    	      setLocalDeviceInfo(context, temp);
    	 }
    	 else 
    	 {
		        localInfoVO	.updateBySleepInfo(sleepInfo);
		        setLocalDeviceInfo(context, localInfoVO);
		 }
     }
     
     public static void updateLocalDeviceInfo(Context context,long syncTime)
     {
    	 LocalInfoVO localInfoVO = getLocalDeviceInfo(context);
    	 if(localInfoVO == null)
    	 {
    	      LocalInfoVO temp =  new LocalInfoVO();
    	      temp.syncTime = syncTime;
    	      setLocalDeviceInfo(context, temp);
    	 }
    	 else 
    	 {
		        localInfoVO	.syncTime = syncTime;
		        setLocalDeviceInfo(context, localInfoVO);
		 }
     }
     
	
	/**
	 * 获取登陆信息.
	 * 
	 * @param activity
	 * @return
	 */
	public static List<LoginInfo2> getLoginInfo(Context activity)
	{
		//取出最近使用过的角色名
		SharedPreferences nameSetting;
		try
		{
			nameSetting = getAppDefaultSharedPreferences(activity, false);
			String jstr = nameSetting.getString(KEY_LOGIN_LOGIN_INFO2, null);
			return CommonUtils.isStringEmpty(jstr) ? null : JSON.parseArray(jstr, LoginInfo2.class);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
	
	}
	
	public static String[] getLoginNames(Context activity)
	{
		List<LoginInfo2> infos = getLoginInfo(activity);
		String[] names = null;
		if(infos != null)
		{
			names = new String[infos.size()];
			for(int i = 0; i < infos.size(); i++)
			{
				names[i] = infos.get(i).getLoginName();
			}
		}
		return names;
	}
	
	public static String getLoginPswByLoginName(Context activity, String loginName)
	{
		List<LoginInfo2> infos = getLoginInfo(activity);
		String psw = "";
		if(infos != null)
		{
			for (LoginInfo2 info : infos)
			{
				if(info.getLoginName().equalsIgnoreCase(loginName))
					return (String) info.getLoginPsw();
			}
		}
		return psw;
	}
	/**
	 * 存储登陆信息.
	 * 
	 * @param activity
	 * @return
	 */
	public static void addLoginInfo(Context activity, String loginName, String loginPsw)
	{
		List<LoginInfo2> history =  getLoginInfo(activity);
		
		LoginInfo2 loginInfo = new LoginInfo2();
		loginInfo.setLoginName(loginName);
		loginInfo.setLoginPsw(loginPsw);
		if(CommonUtils.isStringEmpty(getLoginPswByLoginName(activity, loginName)))
		{
			if(history == null)
				history = new ArrayList<LoginInfo2>();
			history.add(loginInfo);
			SharedPreferences nameSetting;
			try 
			{
				nameSetting = getAppDefaultSharedPreferences(activity, true);
				SharedPreferences.Editor namePref=nameSetting.edit();
				namePref.putString(KEY_LOGIN_LOGIN_INFO2, new Gson().toJson(history));
				namePref.commit();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		
		}
	}
	
	public static void updateLoginInfo(Context activity, String loginName, String loginPsw)
	{
		removeLoginInfo(activity, loginName);
		addLoginInfo(activity, loginName, loginPsw);
	}
	
	public static void updateLoginInfo(Context activity, LoginInfo2 loginInfo)
	{
		removeLoginInfo(activity, loginInfo.getLoginName());
		addLoginInfo(activity, loginInfo.getLoginName(), (String)loginInfo.getLoginPsw());
	}
	
	
	public static void removeLoginInfo(Context activity, String loginName)
	{
		List<LoginInfo2> history = getLoginInfo(activity);
		if(history != null)
		{
			history = new ArrayList<LoginInfo2>();
			for (LoginInfo2 loginInfo2 : history)
			{
				if(loginInfo2.getLoginName().equalsIgnoreCase(loginName))
				{
					history.remove(loginInfo2);
					break;
				}
			}
			SharedPreferences nameSetting;
			try 
			{
				nameSetting = getAppDefaultSharedPreferences(activity, true);
				SharedPreferences.Editor namePref=nameSetting.edit();
				namePref.putString(KEY_LOGIN_LOGIN_INFO2, new Gson().toJson(history));
				namePref.commit();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		
		}
	}
	/**
	 * 存储登陆信息.
	 * 
	 * @param activity
	 * @return
	 */
	public static void addLoginInfo(Context activity, LoginInfo2 loginInfo)
	{
		List<LoginInfo2> history = getLoginInfo(activity);
		if(CommonUtils.isStringEmpty(getLoginPswByLoginName(activity, (String)loginInfo.getLoginName())))
		{
			if(history == null)
				history = new ArrayList<LoginInfo2>();
			history.add(loginInfo);
	//		this.currentMySenceName = currentMySenceName;
			SharedPreferences nameSetting;
			try
			{
				nameSetting = getAppDefaultSharedPreferences(activity, true);
				SharedPreferences.Editor namePref=nameSetting.edit();
				namePref.putString(KEY_LOGIN_LOGIN_INFO2, new Gson().toJson(history));
				namePref.commit();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		
		}
		else if(!getLoginPswByLoginName(activity, (String)loginInfo.getLoginName()).equals(loginInfo.getLoginPsw()))
		{
			updateLoginInfo(activity, loginInfo);
		}
	}
//	
//	public static boolean isMsgToneOpen(Context context)
//	{
//		SharedPreferences nameSetting = getAppDefaultSharedPreferences(context, false);
//		return nameSetting.getBoolean(SHARED_PREFERENCES_KEY_MSG_TONE, true);
//	}
//	public static void setMsgToneOpen(Context activity,boolean msgToneOpen)
//	{
//		SharedPreferences nameSetting = getAppDefaultSharedPreferences(activity, true);
//		SharedPreferences.Editor namePref = nameSetting.edit();
//		namePref.putBoolean(SHARED_PREFERENCES_KEY_MSG_TONE, msgToneOpen);
//		namePref.commit();
//	}
//	
	/**
	 * “是否在主界面上显示可下拉刷新的提示”.
	 * 
	 * @param activity
	 * @return true表示打开，否则关闭
	 */
	public static boolean isNeedShowFragmentTip(Context context)
	{
//		SharedPreferences nameSetting = getAppDefaultSharedPreferences(context, false);
//		return nameSetting.getBoolean(KEY_NEED_SHOW_FRAGMENT_TIP, true);
		return false;
	}
	/**
	 * 设置“是否在主界面上显示可下拉刷新的提示”开关量.
	 * 
	 * @param activity
	 * @param msgToneOpen true表示打开，否则关闭
	 */
	public static void setNeedShowFragmentTip(Context activity,boolean show)
	{
		SharedPreferences nameSetting;
		try {
			nameSetting = getAppDefaultSharedPreferences(activity, true);
			SharedPreferences.Editor namePref = nameSetting.edit();
			namePref.putBoolean(KEY_NEED_SHOW_FRAGMENT_TIP, show);
			namePref.commit();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	
	}
	
	/**
	 * 按天存储运动数据统计数据
	 * @param context
	 * @param count
	 * @param dayIndex
	 */
	public static void updateDetailChartCountDate(Context context,DetailChartCountData count,int dayIndex)
	{
		Map<Integer, String> data = null;
		data = getDetailChartCountDate(context);
		if(data == null)
		{
			data = new HashMap<Integer, String>();
		}
		data.put(dayIndex, new Gson().toJson(count));
	    String jString = new Gson().toJson(data);
	    
		SharedPreferences sharedPreferences;
		try {
			sharedPreferences = getAppDefaultSharedPreferences(context, true);
			SharedPreferences.Editor edit = sharedPreferences.edit();
			edit.putString("detail_chart_cout", jString);
			edit.commit();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	
	}
	
	/**
	 * 获取指定某天的运动统计数据
	 * @param activity
	 * @param dayIndex
	 * @return
	 */
	public static DetailChartCountData getDetailChartCountDate(Context activity,int dayIndex)
	{
		Map<Integer, String> data = getDetailChartCountDate(activity);
		if(data != null)
		{
			String jString  = data.get(dayIndex);
			Log.i(TAG, "获取指定某天的运动统计数据"+jString);
			if(jString != null && !jString.isEmpty())
			{
				return new Gson().fromJson(jString, DetailChartCountData.class);
			}
		}
		return null;
	}
//	
	
	private static Map<Integer, String> getDetailChartCountDate(Context activity)
	{
		SharedPreferences preferences;
		try {
			preferences = getAppDefaultSharedPreferences(activity, true);
			String jString =  preferences.getString("detail_chart_cout", "");
			Log.i(TAG, jString);
			if(jString.isEmpty())
				return null;
			return new Gson().fromJson(jString,   new TypeToken<Map<Integer, String>>() { }.getType());
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
	}
	
	/**
	 * 保存上次云同步时间和条目
	 */
	public static void saveLastCloudSyncTime(Context activity,String time)
	{
		try {
			SharedPreferences preferences = getAppDefaultSharedPreferences(activity, true);
			SharedPreferences.Editor edit = preferences.edit();
			edit.putString("last_cloud_time", time);
			edit.commit();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}
	}
	
	public static String getLastCloudSyncTime(Context activity)
	{
		try 
		{
			SharedPreferences preferences = getAppDefaultSharedPreferences(activity, true);
			return preferences.getString("last_cloud_time", "");
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
	}
	
	/**
	 * 存储时间（消息 dialog）.
	 * 
	 * @param activity
	 * @return
	 */
	public static void savetimeEnt(Context activity, long time)
	{
		
			SharedPreferences savetime;
			try 
			{
				savetime = getAppDefaultSharedPreferences(activity, true);
				SharedPreferences.Editor namePref=savetime.edit();
				namePref.putString(KEY_NEED_SHOW_ENT, time+"");
				namePref.commit();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		
	}
	
	public static long  gettimeEnt(Context activity)
	{
			SharedPreferences nameSetting;
			try
			{
				nameSetting = getAppDefaultSharedPreferences(activity, false);
				String jstr = nameSetting.getString(KEY_NEED_SHOW_ENT, null);
				return CommonUtils.isStringEmpty(jstr) ? 0 : Long.parseLong(jstr);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				return System.currentTimeMillis()/1000;
			}
		
		
	}
	
	/**
	 * 存储时间（OAD dialog）.
	 * 
	 * @param activity
	 * @return
	 */
	public static void savetime(Context activity, long time)
	{
		
			SharedPreferences savetime;
			try 
			{
				savetime = getAppDefaultSharedPreferences(activity, true);
				SharedPreferences.Editor namePref=savetime.edit();
				namePref.putString(KEY_NEED_SHOW_OAD, time+"");
				namePref.commit();
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		
	}
	
	public static long  gettime(Context activity)
	{
			SharedPreferences nameSetting;
			try
			{
				nameSetting = getAppDefaultSharedPreferences(activity, false);
				String jstr = nameSetting.getString(KEY_NEED_SHOW_OAD, null);
				return CommonUtils.isStringEmpty(jstr) ? 0 : Long.parseLong(jstr);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				return System.currentTimeMillis()/1000;
			}
		
		
	}
	
	/**
	 * 存储单位制度
	 * 
	 * @param activity
	 * @return
	 */
	public static void save_unit(Context activity, String type)
	{
		
			SharedPreferences savetime;
			try 
			{
				savetime = getAppDefaultSharedPreferences(activity, true);
				SharedPreferences.Editor namePref=savetime.edit();
				namePref.putString(KEY_UNIT_TYPE, type);
				namePref.commit();
				MyApplication.getInstance(activity).setUNIT_TYPE(type);
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
			}
		
	}
	
	public static String  get_unit(Context activity)
	{
			SharedPreferences Setting;
			try
			{
				Setting = getAppDefaultSharedPreferences(activity, false);
				String jstr = Setting.getString(KEY_UNIT_TYPE, "Imperial");
				return CommonUtils.isStringEmpty(jstr) ? "Imperial" : jstr;
			} catch (Exception e) {
				Log.e(TAG, e.getMessage());
				return "Imperial";
			}
		
		
	}
	
	/**
	 * @param activity
	 * @return
	 */
	public static void save_googlefit(Context activity,boolean up2google)
	{
			SharedPreferences savedevice;
			try 
			{
				savedevice = getAppDefaultSharedPreferences(activity, true);
				SharedPreferences.Editor namePref=savedevice.edit();
				
				namePref.putBoolean(KEY_GOOGLE_FIT, up2google);
				namePref.commit();
			} catch (Exception e) {
				Log.e(TAG, "KEY_GOOGLE_FIT保存到本地失败");
			}
		
	}
	
	public static boolean get_googlefit(Context context){
		 SharedPreferences Setting;
			try
			{
				Setting = getAppDefaultSharedPreferences(context, false);
				boolean jstr = Setting.getBoolean(KEY_GOOGLE_FIT,false);
	            return jstr;
			} catch (Exception e) {
				e.printStackTrace();
			}
		    //所有异常返回null
		    return false;
		    
		  }
	
	
	
	
}
