/**
 * LocalUserSettingsToolkits.java
 * @author Jason Lu
 * @date 2014-9-3
 * @version 1.0
 */
package com.linkloving.rtring_c_watch;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

import com.alibaba.fastjson.JSON;
import com.eva.epc.common.util.CommonUtils;
import com.google.gson.Gson;
import com.linkloving.rtring_c_watch.logic.model.LocalSetting;

/**
 * @author Lz
 * 
 */
public class LocalUserSettingsToolkits
{
	public static void setLocalSettingGoalInfo(Context context, LocalSetting localSetting)
	{
		List<LocalSetting> settings = getLocalSettingInfoList(context);
		int index = -1;
		LocalSetting needUpdate = null;
		for (int i = 0; i < settings.size(); i++)
		{
			LocalSetting setting = settings.get(i);
			if (setting.getUser_mail().equals(localSetting.getUser_mail()))
			{
				index = i;
				needUpdate = setting;
				needUpdate.setGoal(localSetting.getGoal());
				needUpdate.setGoal_update(localSetting.getGoal_update());
				break;
			}
		}
		if (index == -1)
			settings.add(localSetting);
		else
		{
			settings.remove(index);
			settings.add(needUpdate);
		}

		commitPreferencesSetting(context, settings);

	}
	
	public static void setLocalSettingHandUpInfo(Context context, LocalSetting localSetting)
	{
		List<LocalSetting> settings = getLocalSettingInfoList(context);
		int index = -1;
		LocalSetting needUpdate = null;
		for (int i = 0; i < settings.size(); i++)
		{
			LocalSetting setting = settings.get(i);
			if (setting.getUser_mail().equals(localSetting.getUser_mail()))
			{
				index = i;
				needUpdate = setting;
				needUpdate.setHandup(localSetting.getHandup());
				needUpdate.setHandup_time(localSetting.getHandup_time());
				needUpdate.setHandup_update(localSetting.getHandup_update());
				break;
			}
		}
		if (index == -1)
			settings.add(localSetting);
		else
		{
			settings.remove(index);
			settings.add(needUpdate);
		}

		commitPreferencesSetting(context, settings);

	}
	public static void setLocalSettingLongSitInfo(Context context, LocalSetting localSetting)
	{
		List<LocalSetting> settings = getLocalSettingInfoList(context);
		int index = -1;
		LocalSetting needUpdate = null;
		for (int i = 0; i < settings.size(); i++)
		{
			LocalSetting setting = settings.get(i);
			if (setting.getUser_mail().equals(localSetting.getUser_mail()))
			{
				index = i;
				needUpdate = setting;
				needUpdate.setLong_sit(localSetting.getLong_sit());
				needUpdate.setLong_sit_time(localSetting.getLong_sit_time());
				needUpdate.setLong_sit_update(localSetting.getLong_sit_update());
				break;
			}
		}
		if (index == -1)
			settings.add(localSetting);
		else
		{
			settings.remove(index);
			settings.add(needUpdate);
		}

		commitPreferencesSetting(context, settings);

	}

	public static void setLocalSettingAlarmInfo(Context context, LocalSetting localSetting)
	{
		List<LocalSetting> settings = getLocalSettingInfoList(context);
		int index = -1;
		LocalSetting needUpdate = null;
		for (int i = 0; i < settings.size(); i++)
		{
			LocalSetting setting = settings.get(i);
			if (setting.getUser_mail().equals(localSetting.getUser_mail()))
			{
				index = i;
				needUpdate = setting;
				needUpdate.setAlarm_list(localSetting.getAlarm_list());
				needUpdate.setAlarm_update(localSetting.getAlarm_update());
				break;
			}
		}
		if (index == -1)
			settings.add(localSetting);
		else
		{
			settings.remove(index);
			settings.add(needUpdate);
		}

		commitPreferencesSetting(context, settings);

	}

	
	private static void commitPreferencesSetting(Context context, List<LocalSetting> settings)
	{
		String jString = new Gson().toJson(settings);
		SharedPreferences sharedPreferences;
		try
		{
			sharedPreferences = PreferencesToolkits.getAppDefaultSharedPreferences(context, true);
			SharedPreferences.Editor edit = sharedPreferences.edit();
			edit.putString(PreferencesToolkits.KEY_USER_SETTING_INFO, jString);
			edit.commit();
			System.out.println("commit--" + jString);
		}
		catch (Exception e)
		{
//			Log.e(e);
		}
		
	}

	public static LocalSetting getLocalSettingInfo(Context context, String user_mail)
	{
		List<LocalSetting> settings = getLocalSettingInfoList(context);
		for (LocalSetting localSetting : settings)
		{
			if (localSetting.getUser_mail().equals(user_mail))
				return localSetting;
		}
		return null;
	}

	private static List<LocalSetting> getLocalSettingInfoList(Context context)
	{
		SharedPreferences sharedPreferences;
		try
		{
			sharedPreferences = PreferencesToolkits.getAppDefaultSharedPreferences(context, false);
			String jString = sharedPreferences.getString(PreferencesToolkits.KEY_USER_SETTING_INFO, "");
			System.out.println(jString);
			return CommonUtils.isStringEmpty(jString) ? new ArrayList<LocalSetting>() : JSON.parseArray(jString, LocalSetting.class);
		}
		catch (Exception e)
		{
//			Log.e(e);
			return new ArrayList<LocalSetting>();
		}
	}

	public static void removeLocalSettingAlarmInfo(Context context, String user_mail)
	{
		List<LocalSetting> settings = getLocalSettingInfoList(context);
		int index = -1;
		LocalSetting needUpdate = null;
		for (int i = 0; i < settings.size(); i++)
		{
			LocalSetting localSetting = settings.get(i);
			if (localSetting.getUser_mail().equals(user_mail))
			{
				index = i;
				needUpdate = localSetting;
				needUpdate.setAlarm_list(null);
				break;
			}
		}

		if (index != -1)
		{
			settings.remove(index);
			settings.add(needUpdate);
		}
		commitPreferencesSetting(context, settings);
	}

	public static void removeLocalSettingGoalInfo(Context context, String user_mail)
	{
		List<LocalSetting> settings = getLocalSettingInfoList(context);
		int index = -1;
		LocalSetting needUpdate = null;
		for (int i = 0; i < settings.size(); i++)
		{
			LocalSetting localSetting = settings.get(i);
			if (localSetting.getUser_mail().equals(user_mail))
			{
				index = i;
				needUpdate = localSetting;
				needUpdate.setGoal(null);
				break;
			}
		}

		if (index != -1)
		{
			settings.remove(index);
			settings.add(needUpdate);
		}
		commitPreferencesSetting(context, settings);
	}

	public static void removeLocalSettingLongSitInfo(Context context, String user_mail)
	{
		List<LocalSetting> settings = getLocalSettingInfoList(context);
		int index = -1;
		LocalSetting needUpdate = null;
		for (int i = 0; i < settings.size(); i++)
		{
			LocalSetting localSetting = settings.get(i);
			if (localSetting.getUser_mail().equals(user_mail))
			{
				index = i;
				needUpdate = localSetting;
				needUpdate.setLong_sit(0);
				needUpdate.setLong_sit_time(null);
				break;
			}
		}

		if (index != -1)
		{
			settings.remove(index);
			settings.add(needUpdate);
		}
		commitPreferencesSetting(context, settings);
	}
	
	public static void removeLocalSettingHandUpInfo(Context context, String user_mail)
	{
		List<LocalSetting> settings = getLocalSettingInfoList(context);
		int index = -1;
		LocalSetting needUpdate = null;
		for (int i = 0; i < settings.size(); i++)
		{
			LocalSetting localSetting = settings.get(i);
			if (localSetting.getUser_mail().equals(user_mail))
			{
				index = i;
				needUpdate = localSetting;
				needUpdate.setHandup(0);
				needUpdate.setHandup_time(null);
				break;
			}
		}

		if (index != -1)
		{
			settings.remove(index);
			settings.add(needUpdate);
		}
		commitPreferencesSetting(context, settings);
	}
	
	public static void removeLocalAncsInfo(Context context, String user_mail)
	{
		List<LocalSetting> settings = getLocalSettingInfoList(context);
		int index = -1;
		LocalSetting needUpdate = null;
		for (int i = 0; i < settings.size(); i++)
		{
			LocalSetting localSetting = settings.get(i);
			if (localSetting.getUser_mail().equals(user_mail))
			{
				index = i;
				needUpdate = localSetting;
				needUpdate.setAncs(0);
				needUpdate.setLong_sit_time(null);
				break;
			}
		}

		if (index != -1)
		{
			settings.remove(index);
			settings.add(needUpdate);
		}
		commitPreferencesSetting(context, settings);
	}
	

}
