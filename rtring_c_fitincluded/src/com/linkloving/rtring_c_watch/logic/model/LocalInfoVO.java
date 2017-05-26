package com.linkloving.rtring_c_watch.logic.model;

import android.content.Context;
import android.util.Log;

import com.eva.epc.common.util.CommonUtils;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.linkloving.band.dto.SleepData;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.linkloving.utils._Utils;

public class LocalInfoVO 
{
	 public String userId;
	 
	 public String version;
	 public byte[] version_byte=new byte[2];
	
	 public  long syncTime;
	 public  int timestamp;
	 
     public  int battery;
     public  String deviceId;
     public  String modelName;
     
     public  int steps;
     public  int calory;
     public  int distance;
     public  int totalsteps;
     
     public  float sleepDeepTime;
     public  float sleepTime;
     
     /**
 	 * 激活状态
 	 */
 	public int recoderStatus = -1;
 	/**
	 * 设备状态（省电模式）
	 */
	public int deviceStatus = -1;
 	
     public void updateByDeviceInfo(Context context,LPDeviceInfo deviceInfo)
     {
    	 version = deviceInfo.version;
    	 version_byte = deviceInfo.version_byte;
    	 recoderStatus = deviceInfo.recoderStatus;
    	 int level = deviceInfo.charge;
    	 timestamp = deviceInfo.timeStamp;
    	 modelName = deviceInfo.modelName;
		 Log.i("LocalInfoVO","设备电量是："+level );
    	 battery = level > 10 ? 100: ( level< 0 ? 0 : level*10 );
    	 steps = deviceInfo.dayWalkSteps + deviceInfo.dayRunSteps;
    	 totalsteps = deviceInfo.stepDayTotals;
    	 int userWeight = CommonUtils.getIntValue(MyApplication.getInstance(context)
					.getLocalUserInfoProvider().getUser_weight());
    	 calory = _Utils.calculateCalories(deviceInfo.dayRunDistance/(deviceInfo.dayRunTime*30.0d), deviceInfo.dayRunTime*30, (int) (userWeight*ToolKits.UNIT_LBS_TO_KG))
					+ _Utils.calculateCalories(deviceInfo.dayWalkDistance/(deviceInfo.dayWalkTime*30.0d), deviceInfo.dayWalkTime*30, (int) (userWeight*ToolKits.UNIT_LBS_TO_KG));
    	 distance = deviceInfo.dayRunDistance + deviceInfo.dayWalkDistance;
    	 
    	 deviceStatus = deviceInfo.deviceStatus;
     }
     
     public void updateBySleepInfo(SleepData sleepInfo)
     {
    	    sleepDeepTime = (float)sleepInfo.getDeepSleep(); 
    	    sleepTime = (float)sleepInfo.getSleep();
     }
     
}
