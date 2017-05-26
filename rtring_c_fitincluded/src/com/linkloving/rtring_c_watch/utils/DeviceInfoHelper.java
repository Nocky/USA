package com.linkloving.rtring_c_watch.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONException;

import com.eva.epc.common.util.CommonUtils;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.linkloving.band.dto.DaySynopic;
import com.linkloving.utils._Utils;
import com.rtring.buiness.logic.dto.Alarm;
import com.rtring.buiness.logic.dto.LongSit;
import com.rtring.buiness.logic.dto.UserEntity;

import android.util.Log;


public class DeviceInfoHelper 
{
	private static final String TAG = DeviceInfoHelper.class.getSimpleName();
	
	public static DaySynopic toDaySynopic(LPDeviceInfo deviceInfo)
	{
		DaySynopic daySynopi = null;
		if(deviceInfo != null)
		{
			daySynopi = new DaySynopic();
			daySynopi.setData_date(new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(new Date()));
			daySynopi.setData_date2(new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS).format(new Date()));
			
			daySynopi.setRun_step(String.valueOf(deviceInfo.dayRunSteps));
			daySynopi.setRun_distance(String.valueOf(deviceInfo.dayRunDistance));
			daySynopi.setRun_duration(String.valueOf(deviceInfo.dayRunTime * 30));// *30才是秒，否则是时间片
			
			daySynopi.setWork_step(String.valueOf(deviceInfo.dayWalkSteps));
			daySynopi.setWork_distance(String.valueOf(deviceInfo.dayWalkDistance));
			daySynopi.setWork_duration(String.valueOf(deviceInfo.dayWalkTime * 30));// *30才是秒，否则是时间片
		}
		
		return daySynopi;
	}
	
	public static LPDeviceInfo fromDaySynopic(DaySynopic daySynopic)
	{
		LPDeviceInfo deviceInfo = new LPDeviceInfo();
		
		deviceInfo.dayRunSteps = Integer.parseInt(daySynopic.getRun_step());
		Log.i(TAG, "deviceInfo.dayRunSteps:"+daySynopic.getRun_step());
		deviceInfo.dayRunDistance = Integer.parseInt(daySynopic.getRun_distance());
		deviceInfo.dayRunTime = Integer.parseInt(daySynopic.getRun_duration()) / 30; // 需要转成时间片
		
		deviceInfo.dayWalkDistance = Integer.parseInt(daySynopic.getWork_distance());
		deviceInfo.dayWalkSteps = Integer.parseInt(daySynopic.getWork_step());
		deviceInfo.dayWalkTime = Integer.parseInt(daySynopic.getWork_duration()) / 30; // 需要转成时间片
		return deviceInfo;
	}
	
	public static final LPDeviceInfo fromUserEntity(UserEntity userEntity) throws JSONException, ParseException
	{
		
		 LPDeviceInfo deviceInfo = new LPDeviceInfo();
		 
//		 deviceInfo.recoderStatus = Integer.parseInt(userEntity.getUser_status() == null ? "1":userEntity.getUser_status());
		 deviceInfo.recoderStatus=1;
		 
		 Date dt = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).parse(userEntity.getBirthdate());
//		 System.out.println(">>>>>>dt.gettime="+dt.getTime());
		 deviceInfo.userAge = (int)((System.currentTimeMillis() - dt.getTime())/(365L * 24 * 60 * 60 * 1000));
		 deviceInfo.userGender = Integer.parseInt(userEntity.getUser_sex());
		 deviceInfo.userHeight = (int)Math.rint(Integer.parseInt(userEntity.getUser_height()) * ToolKits.UNIT_INCHES_TO_CM);
		 deviceInfo.userId = Integer.parseInt(userEntity.getUser_id());
		 deviceInfo.userNickname = userEntity.getNickname();
		 deviceInfo.userWeight = (int)Math.rint(Integer.parseInt(userEntity.getUser_weight()) * ToolKits.UNIT_LBS_TO_KG);
		 deviceInfo.dayIndex = Integer.parseInt("10");//??????????????????????????
		 
		 deviceInfo.step = Integer.parseInt(userEntity.getPlay_calory());// TODO
		 
		 // 久坐提醒间隔是负值的话，就意味着关闭，那么直接把start和end time置0就行了。
		 // 实际上此deviceInfo.timeWindow在设备上是一个固定值，小马在设置时会使用它的固定值而
		 // 忽略此处的设定值（即忽略deviceInfo.timeWindow，或userEntity.getLong_sit()）
		 deviceInfo.timeWindow = Integer.parseInt(userEntity.getLong_sit())/30;
		 //deviceInfo.level = 50;
		 if(deviceInfo.timeWindow < 0)
		 {
//			 deviceInfo.startTime = 0;
//			 deviceInfo.endTime = 0;
		 }
		 else
		 {
			 LongSit dataLongsit = new LongSit();
			 String long_sit_time = userEntity.getLong_sit_time();
			 Log.e("[LZ]=====================", long_sit_time);
			 if(long_sit_time.indexOf(":") == -1) {
				 long_sit_time = "32400:41400-50400:72000" ;
				 userEntity.setLong_sit_time(long_sit_time);
			 } 
			 _Utils.setPropertyWithLongSitTimeString(dataLongsit, long_sit_time);
			 if(CommonUtils.isStringEmpty(userEntity.getLong_sit_step())){
				 deviceInfo.longsit_step = 60 ;
			 }else{
				 deviceInfo.longsit_step =  Integer.parseInt(userEntity.getLong_sit_step());
			 }
			 
			    deviceInfo.startTime1_H = dataLongsit.getStartHour1();
				deviceInfo.startTime1_M = dataLongsit.getStartMinute1();
				deviceInfo.endTime1_H = dataLongsit.getEndHour1();
				deviceInfo.endTime1_M = dataLongsit.getEndMinute1();
				
//				Log.i(TAG, "startTime1_H："+deviceInfo.startTime1_H+" startTime1_M:"+deviceInfo.startTime1_M);
//				Log.i(TAG, "endTime1_H："+deviceInfo.endTime1_H+" endTime1_M:"+deviceInfo.endTime1_M);
				
				
				deviceInfo.startTime2_H = dataLongsit.getStartHour2();
				deviceInfo.startTime2_M = dataLongsit.getStartMinute2();
				deviceInfo.endTime2_H = dataLongsit.getEndHour2();
				deviceInfo.endTime2_M = dataLongsit.getEndMinute2();
//				Log.i(TAG, "startTime2_H："+deviceInfo.startTime2_H+" startTime2_M:"+deviceInfo.startTime2_M);
//				Log.i(TAG, "endTime2_H："+deviceInfo.endTime2_H+" endTime2_M:"+deviceInfo.endTime2_M);
		 }
		 
		 // 抬手显示间隔是负值的话，就意味着关闭，那么直接把start和end time置0就行了。
		 if(Integer.parseInt(userEntity.getHand_up_enable()) > 0)
		 {
			 String time = userEntity.getHand_up_time();
			 if(time != null && !time.equals(""))
			 {
				 String[] times = time.split("-");
				 if(times.length >= 2)
				 {
					 if(Integer.parseInt(times[0])/3600 < Integer.parseInt(times[1])/3600 || 
							 (
									 Integer.parseInt(times[0])/3600 == Integer.parseInt(times[1])/3600 
							 && (Integer.parseInt(times[0])-(deviceInfo.handup_endTime1_H *3600))/60 
							 < (Integer.parseInt(times[1])-(deviceInfo.handup_startTime1_H*3600)/60)
							 )){
						 
						 deviceInfo.handup_startTime1_H = 0;
						 deviceInfo.handup_startTime1_M = 0;
						 deviceInfo.handup_endTime1_H   = Integer.parseInt(times[0])/3600;
						 deviceInfo.handup_endTime1_M   = (Integer.parseInt(times[0])-(deviceInfo.handup_endTime1_H *3600))/60 ;
						 
						 deviceInfo.handup_startTime2_H = Integer.parseInt(times[1])/3600;
						 deviceInfo.handup_startTime2_M = (Integer.parseInt(times[1])-(deviceInfo.handup_startTime2_H*3600))/60;
						 deviceInfo.handup_endTime2_H   = 23;
						 deviceInfo.handup_endTime2_M   = 59;
						 
					 }
					 else
					 {
					 deviceInfo.handup_startTime1_H = Integer.parseInt(times[1])/3600;
					 Log.i(TAG, "deviceInfo.handup_startTime_H："+deviceInfo.handup_startTime1_H);
					 deviceInfo.handup_startTime1_M = (Integer.parseInt(times[1])-(deviceInfo.handup_startTime1_H*3600))/60 ;
					 Log.i(TAG, "deviceInfo.handup_startTime_M："+deviceInfo.handup_startTime1_M);
					 deviceInfo.handup_endTime1_H   = Integer.parseInt(times[0])/3600;
					 Log.i(TAG, "deviceInfo.handup_endTime_H："+deviceInfo.handup_endTime1_H);
					 deviceInfo.handup_endTime1_M   = (Integer.parseInt(times[0])-(deviceInfo.handup_endTime1_H *3600))/60 ;
					 Log.i(TAG, "deviceInfo.handup_endTime_M："+deviceInfo.handup_endTime1_M);
					 }
				 }
			 }
		 }
		 else
		 {
			 
			 deviceInfo.handup_startTime1_H = 0;
			 deviceInfo.handup_startTime1_M = 0 ;
			 deviceInfo.handup_endTime1_H   = 23;
			 deviceInfo.handup_endTime1_M   = 59 ;
		 }
		 

		 String clockString = userEntity.getAlarm_list();
		 
		 if(clockString != null && !clockString.equals(""))
		 {
			 List<Alarm> alarms = new Gson().fromJson(clockString, new TypeToken<List<Alarm>>(){}.getType());
			 
			 if(alarms.get(0) != null && alarms.get(0).getValid() != 0)
			 {
					 deviceInfo.alarmTime1_H = alarms.get(0).getAlarmTime()/3600;
					 deviceInfo.alarmTime1_M =(alarms.get(0).getAlarmTime()- deviceInfo.alarmTime1_H*3600)/60;
					 deviceInfo.frequency1 = alarms.get(0).getRepeat();
			 }
			 
			 if(alarms.size() > 1 && alarms.get(1).getValid() != 0)
			 {
				 deviceInfo.alarmTime2_H = alarms.get(1).getAlarmTime()/3600;
				 deviceInfo.alarmTime2_M =(alarms.get(1).getAlarmTime()- deviceInfo.alarmTime2_H*3600)/60;
				 deviceInfo.frequency2 = alarms.get(1).getRepeat();
			 }
			 if(alarms.size() > 2 && alarms.get(2).getValid() != 0)
			 {
				 deviceInfo.alarmTime3_H = alarms.get(2).getAlarmTime()/3600;
				 deviceInfo.alarmTime3_M =(alarms.get(2).getAlarmTime()- deviceInfo.alarmTime3_H*3600)/60;
				 deviceInfo.frequency3 = alarms.get(2).getRepeat();
			 }
			 
		 }
		 
		 Log.d(TAG,deviceInfo.toString());
//		 deviceInfo.startTime = Integer.parseInt(userEntity.get);
		 return deviceInfo;
	}


}
