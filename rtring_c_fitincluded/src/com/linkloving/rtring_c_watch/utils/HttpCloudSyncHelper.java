package com.linkloving.rtring_c_watch.utils;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.SysActionConst;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.utils.TimeZoneHelper;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.JobDispatchConst;

public class HttpCloudSyncHelper 
{
	/**
	 * 生成云同步参数
	 * @return
	 */
	public static DataFromClient GenerateCloudSyncParams(Context context,int pageIndex)
	{
		JSONObject obj = new JSONObject();
		obj.put("user_id", MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id());
		obj.put("page", pageIndex);
		obj.put("clientTimezoneOffsetInMinute", String.valueOf(TimeZoneHelper.getTimeZoneOffsetMinute()));
	
		return  DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_REPORT)
		.setJobDispatchId(JobDispatchConst.REPORT_BASE)
		.setActionId(SysActionConst.ACTION_CANCEL_VERIFY)
		.setNewData(JSON.toJSONString(obj));
	}
}
