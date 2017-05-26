package com.linkloving.rtring_c_watch.utils;

import java.util.HashMap;

import com.alibaba.fastjson.JSON;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.eva.epc.core.dto.SysActionConst;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.linkloving.utils.TimeZoneHelper;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.JobDispatchConst;

public class HttpHelper2
{
	private final static String TAG = HttpHelper2.class.getSimpleName();
	
	/**
	 * 无条件从服务端获取运动明细数据的通用实现方法。
	 * <p>
	 * 查询数据的范围为："[start_time(UTC) >= startDateTimeUTC and start_time(UTC) <= endDateTimeUTC]".
	 * 如：查询条件为“2014-07-01 00:00:00.000”至“2014-07-02 00:00:00.000”，则读取的数据实际
	 * 上是07/01日共1日的（全天）数据。
	 * 
	 * @param user_id
	 * @param startDateTimeUTC UTC时间戳，形如：“2014-07-01 00:00:00.000”
	 * @param endDateTimeUTC UTC时间戳，形如：“2014-07-02 00:00:00.000”
	 * @return
	 */
	public static DataFromServer querySportDatasFromRemote(String user_id
			, String startDateTimeUTC, String endDateTimeUTC)
	{
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("user_id", user_id);
		map.put("startDayIndex", startDateTimeUTC);
		map.put("endDayIndex", endDateTimeUTC);
		map.put("clientTimezoneOffsetInMinute", String.valueOf(TimeZoneHelper.getTimeZoneOffsetMinute()));
		
		// 提交请求到服务端
		DataFromClient dataFromClient = DataFromClient.n()
			.setProcessorId(MyProcessorConst.PROCESSOR_REPORT)		
			.setJobDispatchId(JobDispatchConst.REPORT_BASE)
//			.setActionId(SysActionConst.ACTION_APPEND7)
//			.setActionId(SysActionConst.ACTION_NEW)
			.setActionId(SysActionConst.ACTION_APPEND10)
			.setNewData(JSON.toJSONString(map));// 注意：目前的通信协议是扁平JASON文本，不支持直接传输java序列化对象！
		return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(dataFromClient);
	}
	
}
