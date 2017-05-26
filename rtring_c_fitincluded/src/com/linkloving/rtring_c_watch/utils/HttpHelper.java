package com.linkloving.rtring_c_watch.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eva.epc.common.util.CommonUtils;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.eva.epc.core.dto.LoginInfo;
import com.eva.epc.core.dto.SysActionConst;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.db.logic.UserDeviceRecord;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.linkloving.rtring_c_watch.logic.reportday.util.TimeUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.linkloving.band.dto.DaySynopic;
import com.linkloving.band.dto.SleepData;
import com.linkloving.band.dto.SportRecord;
import com.linkloving.band.sleep.DLPSportData;
import com.linkloving.band.sleep.SleepDataHelper;
import com.linkloving.band.ui.DatasProcessHelper;
import com.linkloving.band.ui.DetailChartCountData;
import com.linkloving.utils.TimeZoneHelper;
import com.linkloving.utils._Utils;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.JobDispatchConst;
import com.rtring.buiness.logic.dto.UserEntity;
import com.rtring.buiness.logic.dto.UserRegisterDTO;

public class HttpHelper
{
	private final static String TAG = HttpHelper.class.getSimpleName();
	
	public final static SimpleDateFormat DATE_PATTERN_sdfYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
	
	/**
	 * 将起始时间转为UTC时间
	 * @param startDateTime
	 * @return
	 */
	public static String getstartDateTimeUTC(String startDateTime ,boolean Issleep) {
//		Log.e(TAG, "转换前的时间:"+startDateTime);
		String startTimeUTC ="";
		
		GregorianCalendar gc = new GregorianCalendar();
		try {
			gc.setTime(SleepDataHelper.DATE_PATTERN_sdfYYMMDD.parse(startDateTime));
			//判断当前市区
//			Log.e(TAG, "TimeZoneHelper:"+TimeZoneHelper.getTimeZoneOffsetMinute());
			if(Issleep){
				gc.add(GregorianCalendar.DAY_OF_MONTH, -2);       // 日期切换组件的今天-1即是andy认为的睡眠时间的“今天”
			}else{
				gc.add(GregorianCalendar.DAY_OF_MONTH, -1);
			}
			String startDate = SleepDataHelper.DATE_PATTERN_sdfYYMMDD.format(gc.getTime());
//			Log.e(TAG, "转换后的时间1:"+startDate);
			 startTimeUTC = TimeZoneHelper.__getUTC0FromLocalTime(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS, startDate+" 23:59:59.999"); //将当地时间的0点 换成utc时间的去查询  比如北京时间（+8）的05-02 00：00就是utc时间的05-01 16：00 
//			 Log.e(TAG, "转换后的时间2:"+startTimeUTC);
		} catch (ParseException e) {
			Log.w(TAG, e.getMessage(), e);
		}
		
		return startTimeUTC;
		
	}
	/**
	 * 将结束时间转为UTC时间
	 * @param endDateTime
	 * @return
	 */
	public static String getendDateTimeUTC(String endDateTime,boolean Issleep) {
		String endTimeUTC ="";
		
		
		try {
			if(Issleep) {
				GregorianCalendar gc = new GregorianCalendar();
				gc.setTime(SleepDataHelper.DATE_PATTERN_sdfYYMMDD.parse(endDateTime));
				gc.add(GregorianCalendar.DAY_OF_MONTH, 1);// 结束日期也相应加1以便配合服务端的SQL查询
				endTimeUTC = SleepDataHelper.DATE_PATTERN_sdfYYMMDD.format(gc.getTime())+" 00:00:00.000";
			}
			
			else  {
				endTimeUTC = TimeZoneHelper.__getUTC0FromLocalTime(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS, endDateTime+" 00:00:00.000");
			}
			
			System.out.println("endTimeUTC---->"+endTimeUTC);
			
//		    endTimeUTC = TimeZoneHelper.__getUTC0FromLocalTime(  //tip:传入时 starttime 是 当前时间-1   5.3日  7点  endDateLocal是当前天数+1 5.6日 7点
//					ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS
////						, endDate+" 09:00:00.000"
//						, endDateTime+" 00:00:00.000" // 2014-08-07 : 按andy要求改成了统计到当天的24点
//						);
			
		} catch (ParseException e) {
			Log.w(TAG, e.getMessage(), e);
		}
		
		return endTimeUTC;
		
	}
	
	/**
	 * 返回睡眠查询日期的按日分隔集合.
	 * <p>
	 * 因为目前多日睡眠数据的查询，在本服务端是按每日来查的，所以此方法的目的是将日期范围拆分成每日.
	 * 
	 * @param startDate
	 *            查询日期起（如果查询的是2014-06-01当日1日数据，则本参数为”2014-06-01“）
	 * @param endDate
	 *            查询日期止（如果查询的是2014-06-01当日1日数据，则本参数为”2014-06-02“）
	 * @return 返回的是一个2维数据，每个数据里是当日查询的日期，如果2014-06-01日的睡眠数据，则本数据为["2014-06-01",
	 *         "2014-06-02"]
	 * @throws Exception
	 */
	public static String[][] spliteDateRangeToEveryDayForQuerySleep(String startDate, String endDate) throws Exception
	{
		String[][] ret = null;

		// SimpleDateFormat sdfYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
		Date s = DATE_PATTERN_sdfYYMMDD.parse(startDate);
		Date e = DATE_PATTERN_sdfYYMMDD.parse(endDate);
		System.out.println(TAG+"--->startDate:"+startDate+"=="+s);
		System.out.println(TAG+"--->endDate:"+endDate+"=="+e);
		GregorianCalendar gs = new GregorianCalendar();
		gs.setTime(s);
		gs.add(GregorianCalendar.DAY_OF_MONTH, -1);
		GregorianCalendar ge = new GregorianCalendar();
		ge.setTime(s);
		ge.add(GregorianCalendar.DAY_OF_MONTH, 1);

		// 所查询的日期范围共几日
		int dayDelta = (int) ((e.getTime() - s.getTime()) / 1000 / 60 / 60 / 24);
//		 System.out.println("总共相差的天数="+dayDelta);
		if (dayDelta > 0)
		{
			ret = new String[dayDelta][2];
			for (int i = 0; i < dayDelta; i++)
			{
				String startD = DATE_PATTERN_sdfYYMMDD.format(gs.getTime());
				String endD = DATE_PATTERN_sdfYYMMDD.format(ge.getTime());

				// 第i日
				ret[i][0] = startD;
				ret[i][1] = endD;

				// 下一日时间准备
				gs.add(GregorianCalendar.DAY_OF_MONTH, 1);
				ge.add(GregorianCalendar.DAY_OF_MONTH, 1);
			}
		}

		return ret;
	}

	
	/**
	 * 离线读取多日睡眠结果.
	 * <p>
	 * 注意：查询多天睡眠时，为了节约性能，是假定运动数据在曾今同步时已被计算过睡眠state的
	 * ，此处只是将已经过睡眠算法计算过state的睡眠结果进行合计而已，并不需要重新调用睡眠算法！切记！
	 * <p>
	 * 目前，数据在从设备中读取出来并被存到本地前，是会去调用睡眠算法计算好并回填state状态的，此次回填state
	 * 状态的目的就是用在历史数据里（查询多天睡眠情况的），以便省去重新调用睡眠算法计算的开销，但可能计算结果并没有
	 * 使到每天原始数据并重新调用睡眠算法计算精确（因为存在换手机等情况下，因数据不全而计算睡眠不够准确的情况）。
	 * <p>
	 * 目前使用回填的睡眠state统计睡眠结果只在历史数据里用到，其它地方如：首页里的睡眠、日详细里的睡眠，都是
	 * 通过取得当天原始数据后实时计算的，以便达到计算的绝对准确性（因只计算一天数据，所以性能损失并不大）。
	 * 
	 * @param context
	 * @param startDateLocal
	 * @param endDateLocal
	 * @return
	 */
	public static List<SleepData> offlineReadMultiDaySleepDataToServer(Context context
			, final String startDateLocal, final String endDateLocal)
	{
		List<SleepData> sleepDatasList = new ArrayList<SleepData>();
		try
		{
		// 将日期范围（跨多日）分拆成每1日来单独计算每日睡眠数据（分拆计算的目的是防止服务端一次性读取太多
		// 数据而内存不足（以及睡眠算法可以不用修改即用，因为现在的算法到每日时接口还是比较好调用的），但
		// 问题是会消耗更多的数据库查询时间（因为分成了多次查询）
		String[][] dteds = spliteDateRangeToEveryDayForQuerySleep(startDateLocal, endDateLocal);
		if(dteds != null && dteds.length > 0)
		{
//			int i = 0;
			for(String[] theDay : dteds)
			{
				SleepData sleepData = new SleepData();
				long tt = System.currentTimeMillis();
				String ss = theDay[0];
				String ee = theDay[1];
				//时间的柱状图      标志时间
				Date tmpDate = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).parse(ss);
				Date curDate = TimeUtil.afterDate(tmpDate, 1);
				String cur = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(curDate);
				sleepData.setDate(cur);
				String user_id = MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id();
				//将时间转为UTC时间
				String startDateTimeUTC  =         getstartDateTimeUTC(ss,true);
				String endDateTimeUTC    =         getendDateTimeUTC(ee,true);
				//  取出本次查询睡眠的数据（查询多天睡眠时，为了节约性能，是假定运动数据在曾今同步时已被计算过睡眠state的
				// ，此处只是将已经过睡眠算法计算过state的睡眠结果进行合计而已，并不需要重新调用睡眠算法！切记！）
				
				ArrayList<SportRecord> originalSportDatas = UserDeviceRecord.findHistoryForSleepCalculate_l(context, user_id, startDateTimeUTC, endDateTimeUTC);
				// 将已计算过state的每日睡眠数据合计出来
			    //	double[] ret = SleepDataHelper.getSleepSumWithHour(SleepDataHelper.getSleepSumWithSportRecord(srs));
				// 计算睡眠
				List<DLPSportData> srs = SleepDataHelper.querySleepDatas2(originalSportDatas);
			    DetailChartCountData count = DatasProcessHelper.countSportData(srs, cur);
			    double[] ret = new double[2];
			    ret[0] = count.soft_sleep_duration/2/60;
			    ret[1] = count.deep_sleep_duration/2/60;
				if(ret == null)
				{
					Log.e(TAG, "DEBUG【历史数据查询-睡眠】"+ss+"没有睡眠数据！");
				}
				else
				{
					sleepData.setSleep(ret[0]);    // 浅睡眠单位：小时
					sleepData.setDeepSleep(ret[1]);// 深睡眠单位：小时
				}
				
				Log.e(TAG, "DEBUG【历史数据查询-睡眠】"+ss+"日：浅睡"+sleepData.getSleep()
						+"小时、深睡"+sleepData.getDeepSleep()+"小时，计算耗时"+(System.currentTimeMillis() - tt)+"毫秒！");
				sleepDatasList.add(sleepData);
			}
		}
		}
		catch (Exception e)
		{
			Log.w(TAG, e.getMessage(), e);
		}
		
		return sleepDatasList;
	}
	
	
	


	/**
	 * 提交单日睡眠数据请求到服务端。
	 * 【目前用于首页睡眠】
	 * 
	 * @param startDateLocal 查询日期起（本地时区，如果查询的是2014-06-01当日1日数据，则本参数为”2014-06-01“）
	 * @param endDateLocal 查询日期止（本地时区，如果查询的是2014-06-01当日1日数据，则本参数为”2014-06-02“）
	 * @param online true表示本次查询是连网查询，否则表示离线查询
	 * @return SleepData
	 */// TODO [离线处理ok]本接口是首页读取和处理睡眠数据的接口
	public static DataFromServer submitReportForDaySleepDataToServer(  //tip:传入时 starttime 是 当前时间 5.4日   endDateLocal是当前天数+1 5.5日
			Context context, final String startDateLocal, final String endDateLocal, boolean online)  //时间均为手机上的时间
	{
		String user_id = MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id();
		try
		{
			String startDateTimeUTC  =         getstartDateTimeUTC(startDateLocal,true);
			String endDateTimeUTC    =         getendDateTimeUTC(endDateLocal,true);
//			// 【关于时区的说明】以下代码是以系统默认时区parese，同时也以系统时区format，所以日期字符串的结果实际是不会变的，不用但心时区会变哦
//			// 据Andy的意思：今天的睡眠数据实际上是昨天12：00至今天12：00，而不是今天12：00至明天12：00
//			GregorianCalendar gc = new GregorianCalendar();
//			gc.setTime(SleepDataHelper.DATE_PATTERN_sdfYYMMDD.parse(startDateLocal));
//			gc.add(GregorianCalendar.DAY_OF_MONTH, -1);    // 日期切换组件的今天-1即是andy认为的睡眠时间的“今天”
//			String startDate = SleepDataHelper.DATE_PATTERN_sdfYYMMDD.format(gc.getTime());
			System.out.println("aaaaaaaaaaa----->startDateTimeUTC...."+startDateTimeUTC);
			System.out.println("aaaaaaaaaaa----->endDateTimeUTC...."+endDateTimeUTC);
//
//			gc.setTime(SleepDataHelper.DATE_PATTERN_sdfYYMMDD.parse(endDateLocal));
//			gc.add(GregorianCalendar.DAY_OF_MONTH, 1);// 结束日期也相应减1以便配合服务端的SQL查询
//			String endDate = SleepDataHelper.DATE_PATTERN_sdfYYMMDD.format(gc.getTime());
//
//			// 先计算出要取的睡眠时间范围（UTC时间）
//			String startDateTimeUTC = TimeZoneHelper.__getUTC0FromLocalTime(
//					ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS, startDate+" 00:00:00.000"); //将当地时间的0点 换成utc时间的去查询  比如北京时间（+8）的05-02 00：00就是utc时间的05-01 16：00 
//			String endDateTimeUTC = TimeZoneHelper.__getUTC0FromLocalTime(  //tip:传入时 starttime 是 当前时间-1   5.3日  7点  endDateLocal是当前天数+1 5.6日 7点
//					ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS
////						, endDate+" 09:00:00.000"
//						, endDate+" 00:00:00.000" // 2014-08-07 : 按andy要求改成了统计到当天的24点
//						);
//			Log.d(TAG, "startDate:" + startDate + ",endDate:" + endDate);
//			System.out.println("startDateTimeUTC:" + startDateTimeUTC + ",endDateTimeUTC:" + endDateTimeUTC);
			ArrayList<SportRecord> srsOffline = UserDeviceRecord.findHistoryForSleepCalculate_l(context, user_id, startDateTimeUTC, endDateTimeUTC);
//			Log.d(TAG, "startDate:" + startDate + ",endDate:" + endDate);
			//ArrayList<SportRecord> srsOffline = UserDeviceRecord.findHistoryForSleepCalculate_l(context, user_id, startDateLocal + " 00:00:00.000", endDateLocal + " 00:00:00.000");
			// 网络未连接时使用离线数据
//			if(!ToolKits.isNetworkConnected(context))
//			if(!online)
			if(srsOffline.size() <= 0 && online) // 2014-08-07 应andy要求：优先用本地数据，没有且有网的时候才尝试从网络取（andy的意思是换手机的情况基本不用考虑！）
			{
					return HttpHelper2.querySportDatasFromRemote(user_id, startDateTimeUTC, endDateTimeUTC);
			}
			else
			{
//				ArrayList<SportRecord> srs = UserDeviceRecord.findHistoryForSleepCalculate_l( context, user_id, startDateLocal, endDateLocal );
				Log.d(TAG, "[网络不可用]单日睡眠数据接口submitReportForDaySleepDataToServer(..)调用时将使用离线数据, 且查出来的离线记录条数"+ srsOffline.size()+".");
				DataFromServer dfs = new DataFromServer();
				dfs.setSuccess(true);
				dfs.setReturnValue(JSON.toJSONString(srsOffline));
				return dfs;
			}
		}
		catch (Exception e)
		{
			Log.w(TAG, e.getMessage(), e);
			DataFromServer dfs = new DataFromServer();
			dfs.setSuccess(false);
			dfs.setReturnValue("日期格式错误！！");
			return dfs;
		}
	}
	public static SleepData parseReportForDaySleepDataFromServer(Context context
			, String startDateInLocal, String endDateInLocal, Object retValue, boolean online)
	{
		long t = System.currentTimeMillis();
		List<SportRecord> srs = new ArrayList<SportRecord>();
		List<DaySynopic> srs1 =new ArrayList<DaySynopic>();;
//		SleepData sleepData = new SleepData();
		// 服务端查询返回的JSON文本
		if(retValue instanceof String)
		{
			// 从服务端取的是需要计算的原始数据哦
			srs = new Gson().fromJson((String)retValue, new TypeToken<List<SportRecord>>(){}.getType());
			
			if(online)
				// 把数据本地化存储起来（以便在无网络时能正常使用）
				UserDeviceRecord.saveToSqliteAsync(context, srs, MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id()
					, true, null);
		}
//		// 此种况的返回数据是在无网络情况下本地离线数据的查询返回
//		else if(retValue instanceof ArrayList<?>)
//		{
//			srs = (ArrayList<SportRecord>)retValue;
			Log.d(TAG, "[离线或在线数据]马上将解析此离线单日睡眠数据：共"+srs.size()+"条. startDateInLocal:" + startDateInLocal);
//			for(int i = 0;i<srs.size();i++){
//				Log.e(TAG, "单条数据:"+srs.get(i).toString());
//			}
//		}
		
		//------------------------------------------- 计算睡眠
		// 从未设置过睡眠状态的运动数据中计算睡眠结果并合计出睡眠时间
			
		SleepData sleepData = DatasProcessHelper.parseReportForDaySleepDataFromServer(srs, startDateInLocal);
		
//		// 浅睡眠单位：小时
//		double qsm = 0;
//		// 深睡眠单位：小时
//		double ssm = 0;
//		
//		// 取睡眠数据
//		double[] ret2 = SleepDataHelper.getSleepSumWithHour(SleepDataHelper.getSleepSum(
////				SleepDataHelper.querySleepDatas(db, startDate, endDate, userId)
//				SleepDataHelper.querySleepDatas2(srs)
//				));
//		if(ret2 == null)
////			LoggerFactory.getLog().debug("没有睡眠数据！");
//			Log.d(TAG, "没有睡眠数据！");
//		else
//		{
//			qsm = ret2[0];
//			ssm = ret2[1];
////			LoggerFactory.getLog().debug("计算睡眠时间-> userId="+userId+"的[>="+startDate+",<"+endDate+"]浅睡时间："+ret2[0]+"深睡时间："+ret2[1]);
//		}
//		
//		sleepData.setDate(startDateInLocal);
//		sleepData.setSleep(qsm);
//		sleepData.setDeepSleep(ssm);
		
		Log.d(TAG, "睡眠计算完成耗时："+(System.currentTimeMillis()-t)+" -> [>="+startDateInLocal+",<"+endDateInLocal+"]浅睡时间："+(sleepData.getSleep())
				+"，深睡时间："+(sleepData.getDeepSleep()));
		
		return sleepData;
	}
//	/**
//	 * 从未设置过睡眠状态的运动数据中计算睡眠结果并合计出睡眠时间.
//	 * 
//	 * @param srs 需要使用睡眠算法计算睡眠state的原始运动数据
//	 * @param startDateInLocal
//	 * @return
//	 */
//	public static SleepData parseReportForDaySleepDataFromServer(List<SportRecord> srs, String startDateInLocal)
//	{
//		SleepData sleepData = new SleepData();
//		//------------------------------------------- 计算睡眠
//		// 浅睡眠单位：小时
//		double qsm = 0;
//		// 深睡眠单位：小时
//		double ssm = 0;
//
////		// 取睡眠数据
////		double[] ret2 = SleepDataHelper.getSleepSumWithHour(SleepDataHelper.getSleepSum(
//////						SleepDataHelper.querySleepDatas(db, startDate, endDate, userId)
////				SleepDataHelper.querySleepDatas2(srs) // 使用睡眠算法先设置好睡眠状态
////				));
//		
//		List<DLPSportData> dlpList = SleepDataHelper.querySleepDatas2(srs);
//		try {
//			DetailChartCountData count = DatasProcessHelper.countSportData(dlpList, startDateInLocal);
//			double[] ret2 = new double[2];
//			ret2[0] = count.soft_sleep_duration/2/60;
//			ret2[1] = count.deep_sleep_duration/2/60;
//			if(ret2 == null)
////				LoggerFactory.getLog().debug("没有睡眠数据！");
//		     Log.d(TAG, "没有睡眠数据！");
//	       else
//	       {
//				qsm = ret2[0];
//				ssm = ret2[1];
////				LoggerFactory.getLog().debug("计算睡眠时间-> userId="+userId+"的[>="+startDate+",<"+endDate+"]浅睡时间："+ret2[0]+"深睡时间："+ret2[1]);
//	       }
//
//		} catch (ParseException e) {
//			Log.e(TAG, e.getMessage());
//		}
//	
//		sleepData.setDate(startDateInLocal);
//		sleepData.setSleep(qsm);
//		sleepData.setDeepSleep(ssm);
//
//		return sleepData;
//	}
	
	/**
	 * 提交历史汇总数据请求到服务端。
	 * 【目前仅用于历史数据查看界面中：2014-08-22使用服务端数据优先读取的策略（只在无网的
	 * 情况下读取本地），因为如果优先读取本地则如果一月只有一天在本地则其它日数据就取不下来了！用户就看不到其它日了！】.
	 * <p>
	 * 如果startDate=2014-06-17， endDate=2014-06-18，则查询
	 * 的是2014-06-17 00:00:00.000到2014-06-17 23:59:59.999间的数据.
	 * 
	 * @param online true表示本次查询是连网查询，否则表示离线查询
	 * @return
	 */// TODO [离线处理ok]本接口是取历史汇总原始数据的通用接口
	public static List<DaySynopic> submitQueryDaySynopicDatasToServerNew(
			Context context, final String startDate, final String endDate, boolean online) throws Exception
	{
		List<DaySynopic> srsForReturn = new ArrayList<DaySynopic>();
		String user_id = MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id();

		// 提交查询明细数据
		DataFromServer dfs = HttpHelper.submitQuerySportRecordsToServer_l(
				context, startDate, endDate, online, true, false
//				, true
				, false// 2015-02-02为了提升加载体验，统一跟日详细等界面里的数据读取一样，优先读取本地，没数据就云同步一下！
				);

		if(dfs.isSuccess())
		{
			// 解析明细数据
			List<SportRecord> sportRecords = HttpHelper.parseQuerySportRecordsFromServer(context, 
					dfs.getReturnValue(), online);
			// 先将运动明细数据组装成日汇总数据
			List<DaySynopic> srs = DatasProcessHelper.convertSportDatasToSynopics(sportRecords);
			Log.d(TAG, "历史汇总数据接口submitQueryDaySynopicDatasToServerNew(..)调用时查出来的记录条数"+
					sportRecords.size()+"，组合完成的日汇总数据条数"+srsForReturn.size()+".");

			//--------------------------------------------------- 如果该日期没有查到数据，就用0数据来填，否则图表是就看不到该天了（0数据至少可以看到）
			Date s = DATE_PATTERN_sdfYYMMDD.parse(startDate);
			Date e = DATE_PATTERN_sdfYYMMDD.parse(endDate);
			GregorianCalendar gs = new GregorianCalendar();
			gs.setTime(s);
			
			// 所查询的日期范围共几日
			int dayDelta = (int)((e.getTime() - s.getTime())/1000 / 60 / 60 / 24);
			if(dayDelta > 0)
			{
				for(int i=0;i< dayDelta;i++)
				{
					String startD = DATE_PATTERN_sdfYYMMDD.format(gs.getTime());
					
					if(srs.size() > 0)
					{
						for(int j = 0; j<srs.size();j++)
						{
							DaySynopic ds = srs.get(j);
							if(startD.equals(ds.getData_date()))
							{
								srsForReturn.add(ds);
								break;
							}

							// 匹配到最后一个，依然没有匹配上，则放入一个空对象
							if(j == srs.size() - 1)
								srsForReturn.add(_createEmptyDaySynopic(startD, user_id));
						}
					}
					else
						srsForReturn.add(_createEmptyDaySynopic(startD, user_id));
					
					// 下一日时间准备
					gs.add(GregorianCalendar.DAY_OF_MONTH, 1);
				}
			}
		}
		else
		{
			throw new Exception((String)dfs.getReturnValue());
		}
		
		return srsForReturn;
	}
	private static DaySynopic _createEmptyDaySynopic(String date, String user_id)
	{
		DaySynopic empty = new DaySynopic();
		empty.setUser_id(user_id);
		empty.setData_date(date);
		empty.setDeepSleepMiute("0");
		empty.setSleepMinute("0");
		empty.setRun_distance("0");
		empty.setRun_duration("0");
		empty.setRun_step("0");
		empty.setWork_distance("0");
		empty.setWork_step("0");
		empty.setWork_duration("0");
		return empty;
	}
		
	/**
	 * 查询运动明细数据请求到服务端（本方法中默认时间参数不是UTC（是带时区的））.
	 * 【目前用于“首页-运动数据查看中”、“日详细页-图表明细数据中”】。
	 * <p>
	 * 如果startDate=2014-06-17， endDate=2014-06-18，则查询
	 * 的是2014-06-17 00:00:00.000到2014-06-17 23:59:59.999(或说是2014-06-18 00:00:00.000)间的数据.<br>
	 * 
	 * @param online true表示本次查询是连网查询，否则表示离线查询
	 * @param dateNoTime true表示时间范围参数已经带上时分秒，否则没有带（即表
	 * 示传过来的参数是年-月-日，方法中将自动补上时分秒“00:00:00.000”）
	 * @return
	 */// TODO [离线处理ok]本接口是取日明细原始数据的通用接口
	public static DataFromServer submitQuerySportRecordsToServer_l(Context context, final String startDateLocal, final String endDateLocal
			, boolean online, boolean dateNoTime)
	{
		return submitQuerySportRecordsToServer_l(context, startDateLocal, endDateLocal, online, dateNoTime, false);
	}
	/**
	 * 查询运动明细数据请求到服务端（默认使用本地数据优先的数据读取策略）.
	 * 【目前用于“首页-从设备读取到运动数据且不足1半小时时取前12小时明细数据时”】。
	 * <p>
	 * 如果startDate=2014-06-17， endDate=2014-06-18，则查询
	 * 的是2014-06-17 00:00:00.000到2014-06-17 23:59:59.999(或说是2014-06-18 00:00:00.000)间的数据.<br>
	 * 
	 * @param online true表示本次查询是连网查询，否则表示离线查询
	 * @param dateNoTime true表示时间范围参数已经带上时分秒，否则没有带（方法中将自动补上时分秒“00:00:00.000”）
	 * @param paramsIsUTC true表示传过来的时间参数已经是使用了utc时间的，就不需要再额外转utc了，否则需要本方法中来转哦
	 * @param fromServerPriority 数据读取策略的先决条件：true表示优先从服务端取数据（在有网络的情况下），否则按列在的逻辑是优先取本地数据（只在本地无数据时才从网络取）
	 * @return
	 */// TODO [离线处理ok]本接口是取日明细原始数据的通用接口
	public static DataFromServer submitQuerySportRecordsToServer_l(
			Context context, final String startDateLocal, final String endDateLocal, boolean online, boolean dateNoTime
			, boolean paramsIsUTC)
	{
		return submitQuerySportRecordsToServer_l(context, startDateLocal, endDateLocal, online, dateNoTime, paramsIsUTC, false);
	}
	/**
	 * 查询运动明细数据请求到服务端.
	 * 【目前用于“首页-从设备读取到运动数据且不足1半小时时取前12小时明细数据时”】。
	 * <p>
	 * 如果startDate=2014-06-17， endDate=2014-06-18，则查询
	 * 的是2014-06-17 00:00:00.000到2014-06-17 23:59:59.999(或说是2014-06-18 00:00:00.000)间的数据.<br>
	 * 
	 * @param online true表示本次查询是连网查询，否则表示离线查询
	 * @param dateNoTime true表示时间范围参数已经带上时分秒，否则没有带（方法中将自动补上时分秒“00:00:00.000”）
	 * @param paramsIsUTC true表示传过来的时间参数已经是使用了utc时间的，就不需要再额外转utc了，否则需要本方法中来转哦
	 * @param fromServerPriority 数据读取策略的先决条件：true表示优先从服务端取数据（在有网络的情况下），否则按列在的逻辑是优先取本地数据（只在本地无数据时才从网络取）
	 * @return
	 */// TODO [离线处理ok]本接口是取日明细原始数据的通用接口
	public static DataFromServer submitQuerySportRecordsToServer_l(
			Context context, final String startDateLocal, final String endDateLocal, boolean online, boolean dateNoTime
			, boolean paramsIsUTC, boolean fromServerPriority)
	{
		try
		{
			String user_id = MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id();
			// 优先读取本地数据模式  (强制从本地读取)
			if(!fromServerPriority)
			{
				ArrayList<SportRecord> srsOffline = UserDeviceRecord.findHistoryForCommon_l(context, user_id, startDateLocal, endDateLocal, dateNoTime, paramsIsUTC);
				
				Log.d(TAG, "[网络不可用]运动数据接口submitQuerySportRecordsToServer(..)调用时将使用离线数据, 且查出来的离线记录条数"+srsOffline.size()+".");
				DataFromServer dfs = new DataFromServer();
				dfs.setSuccess(true);
				dfs.setReturnValue(srsOffline);
				return dfs;
			}
				// 优先读取服务端数据模式
			else
			{
				// 网络未连接时使用离线数据
//				if(!ToolKits.isNetworkConnected(context))
				if(!online)
				{
					ArrayList<SportRecord> srsOffline = UserDeviceRecord.findHistoryForCommon_l(context, user_id, startDateLocal, endDateLocal, dateNoTime, paramsIsUTC);
					Log.d(TAG, "[网络不可用]运动数据接口submitQuerySportRecordsToServer(..)调用时将使用离线数据, 且查出来的离线记录条数"+srsOffline.size()+".");
					DataFromServer dfs = new DataFromServer();
					dfs.setSuccess(true);
					dfs.setReturnValue(JSON.toJSONString(srsOffline));
					return dfs;
				}
				else
				{
					Log.d(TAG, "[网络OK]运动数据接口submitQuerySportRecordsToServer(..)调用时将连网查数据.");
					String startDateTimeUTC = "";
					String endDateTimeUTC = "";
					if(paramsIsUTC)
					{
						startDateTimeUTC = startDateLocal+(dateNoTime?" 00:00:00.000":"");
						endDateTimeUTC = endDateLocal+(dateNoTime?" 00:00:00.000":"");
					}
					else
					{
						startDateTimeUTC = TimeZoneHelper.__getUTC0FromLocalTime(com.linkloving.rtring_c_watch.utils.ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS, startDateLocal+(dateNoTime?" 00:00:00.000":""));
						endDateTimeUTC = TimeZoneHelper.__getUTC0FromLocalTime(com.linkloving.rtring_c_watch.utils.ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS, endDateLocal+(dateNoTime?" 00:00:00.000":""));
					}
					
					return HttpHelper2.querySportDatasFromRemote(user_id, startDateTimeUTC, endDateTimeUTC);
				}
			}
		}
		catch (Exception e)
		{
			
		}
		
		DataFromServer errorD = new DataFromServer();
		errorD.setSuccess(false);
		errorD.setReturnValue("日期格式错误！");
		return errorD;
	}
	/**
	 * 将JSON字符串解析成运动明细数据集合的通用方法。
	 * 并提供是否保存到本地作为离线数据使用。
	 * （注意：本方法中当需要保存到本地数据库时使用异步方式）。
	 * 
	 * @param context
	 * @param retValue
	 * @param online true表示本次查询是连网查询，否则表示离线查询
	 * @param asyncToSaveIfNeed true表示，当处于在线状态时（即前面的数据是通过网络取下来的）保存到本地sqlite时不用异步线程而用同步，
	 * 此场景在那些希望首先完成保存后才进行的操作里（防止因异步而导致不可控的局面）
	 * @return
	 */
	public static  List<SportRecord> parseQuerySportRecordsFromServer(Context context, Object retValue, boolean online)
	{
		return parseQuerySportRecordsFromServer(context, retValue, online, true);
	}
	/**
	 * 将JSON字符串解析成运动明细数据集合的通用方法。
	 * 并提供是否保存到本地作为离线数据使用。
	 * 
	 * @param context
	 * @param retValue
	 * @param online true表示本次查询是连网查询，否则表示离线查询
	 * @param asyncToSaveIfNeed true表示，当处于在线状态时（即前面的数据是通过网络取下来的）保存到本地sqlite时不用异步线程而用同步，
	 * 此场景在那些希望首先完成保存后才进行的操作里（防止因异步而导致不可控的局面）
	 * @return
	 */
	public static List<SportRecord> parseQuerySportRecordsFromServer(Context context, Object retValue, boolean online, boolean asyncToSaveIfNeed)
	{
		List<SportRecord> srs = new ArrayList<SportRecord>();
		// 服务端查询返回的JSON文本
		if(retValue instanceof String)
		{
			srs = new Gson().fromJson((String)retValue, new TypeToken<List<SportRecord>>(){}.getType());
			if(online)
			{
				if(asyncToSaveIfNeed)
					// 把数据本地化存储起来（以便在无网络时能正常使用）
					UserDeviceRecord.saveToSqliteAsync(context, srs, MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id(), true, null);
				else
					// 把数据本地化存储起来（以便在无网络时能正常使用）
					UserDeviceRecord.saveToSqlite(context, srs, MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id(), true);
			}
		}
		// 此种况的返回数据是在无网络情况下本地离线数据的查询返回
		else if(retValue instanceof ArrayList<?>)
		{
			srs = (ArrayList<SportRecord>)retValue;
			
			Log.d(TAG, "[离线或在线数据]马上将解析此离线运动数据：共"+srs.size()+"条.");
		}
//		int sum = 0;
//		for(int i=0;i<srs.size();i++){
//			if(Integer.parseInt(srs.get(i).getState()) ==4){
//				sum+=Integer.parseInt(srs.get(i).getDuration());
//			}
//		}
//		
//		Log.e(TAG, "运动数据getDuration："+sum);
		
		return srs;
	}
	
	/**
	 * 将日汇总数据集合原始数据合计计算的解析和组装方法.
	 * 【目前用于“日详细”界面中查看合计数据时】.
	 * 
	 * @return
	 */
	public static String parseDaySynopicSumForPreview(Context context,DetailChartCountData count)
    {
			String[] datas = new String[15];
			datas[0] = (int)(count.walking_distance +count.runing_distance) + "";
			Log.d(TAG, "当日运动距离：walking_distance：" +count.walking_distance+"<-->runing_distance"+count.runing_distance );
			datas[1] = (int)(count.walking_duration +count.runing_duation)*30 + "";
			datas[2] = (int)(count.walking_steps +count.runing_steps) + "";
			Log.d(TAG, "当日运动步数：walking_steps：" +count.walking_steps+"<-->runing_steps"+count.runing_steps );
			datas[3] = (int)(count.walking_distance) + "";
			datas[4] = (int)(count.walking_duration)*30 + "";
			datas[5] = (int)(count.walking_steps) + "";
			datas[6] = (int)(count.runing_distance) + "";
			datas[7] = (int)(count.runing_duation) *30+ "";
			datas[8] = (int)(count.runing_steps) + "";
			datas[11] = (count.soft_sleep_duration)/2 + "";
			datas[12] = (count.deep_sleep_duration)/2 + "";
			Log.d(TAG, "soft_sleep_duration：" +(count.soft_sleep_duration)/2);
			Log.d(TAG, "deep_sleep_duration：" +(count.deep_sleep_duration)/2 );
			datas[13] = (count.getupTime)+ "";
			datas[14] = (count.gotoBedTime) + "";
			// 1天的数据
			return parseDaySynopicSumForPreview(context, datas, true);
     }
	
	/**
	 * 将数据组织成上层UI需要的数据构造，以方便解析和使用.
	 * 
	 * @param context
	 * @param datas 计算出的数据
	 * @param hasGotoSleep true表示datas[13]、datas[14]存在（放的是起床和入睡时间），false表示没有这两个单元。通常起床入睡时间只能“1天”
	 * 数据有意义，而多天的合计数据是无意义的，重用此方法的目的是为了实现1到N天的合计，而起床和入睡时间只应针对1天哦！
	 * @return
	 */
	private static String parseDaySynopicSumForPreview(Context context,String[] datas, boolean hasGotoSleep)
	{
		if(datas.length < 13)
		{
			return null;
		}
		
		String ydzjl = datas[0];//-- 运动总距离（米）
		String ydzsj = datas[1];//-- 运动总时间（秒）
		String ydzbs = datas[2];//-- 运动总步数

		String zlzjl = datas[3];//-- 走路总距离（米）0
		String zlzsj = datas[4];//-- 走路总时间（秒）
		String zlzbs = datas[5];//-- 走路总步数

		String pbzjl = datas[6];//-- 跑步总距离（米）
		String pbzsj = datas[7];//-- 跑步总时间（秒）
		String pbzbs = datas[8];//-- 跑步总步数

		// 2014-06-18后：深、浅睡时间用新的方式实现：直接从汇总数据中拿即可（不用算了！）
		//		String qssj = "0";// datas[9]; // 浅睡时间（小时）
		//		String sssj = "0";// datas[10];// 深睡时间（小时）
		String qssj = CommonUtils.getScaledValue(CommonUtils.getDoubleValue(datas[11])/60.0, 1)+""; // 浅睡时间（小时）
		String sssj = CommonUtils.getScaledValue(CommonUtils.getDoubleValue(datas[12])/60.0, 1)+"";// datas[10];// 深睡时间（小时）
		
		String getuptime = hasGotoSleep?datas[13]:""; //起床时间
		String gotobedtime = hasGotoSleep?datas[14]:""; //入睡时间
		
		int ipbzsj = CommonUtils.getIntValue(pbzsj); // 跑步总时间（int型）
		// 跑步平均速度
		double runSpeed = 0;
		if(ipbzsj > 0)
			runSpeed = CommonUtils.getIntValue(pbzjl) / (ipbzsj * 1.0);

		// 走路平均速度
		double workSpeed = 0;
		int izlzsj = CommonUtils.getIntValue(zlzsj); // 走路总时间（int型）
		if(izlzsj > 0)
			workSpeed = CommonUtils.getIntValue(zlzjl) / (izlzsj * 1.0);

		int iydzsj = CommonUtils.getIntValue(ydzsj); // 运动（跑步+走路）总时间（int型）
		// 运动平均速度
		double sportSpeed = 0;
		if(iydzsj > 0)
			sportSpeed = CommonUtils.getIntValue(ydzjl) / (iydzsj * 1.0);

		// 用户体重
		int userWeight = CommonUtils.getIntValue(MyApplication.getInstance(context)
				.getLocalUserInfoProvider().getUser_weight());
		
		

		if(datas != null)
		{
			int pbca = getCalory(runSpeed, ipbzsj, userWeight);
			int zlca = getCalory(workSpeed, izlzsj, userWeight);
			
			Log.e("[LZ]======打印用户体重", pbca + "," + zlca);
			// 消耗卡路里（为了精确，结果=跑步消耗卡路里+走路消耗卡路里）
			int ca = pbca + zlca;

			// 将取到的数据返回
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("ydzbs", ydzbs);// 运动总步数
			map.put("ydzsj", ydzsj);// 运动总时间（秒）
			map.put("speed", String.valueOf(sportSpeed));// 运动速度（米/秒）
			map.put("ca", String.valueOf(ca));// 消耗卡路里
			map.put("ydzjl", ydzjl);// 运动总距离（米）

			// 消耗卡路里
			map.put("pbzbs", pbzbs);// 跑步总步数 
			map.put("pbzsj", pbzsj);// 跑步总时间（秒） 
			map.put("pbspeed", String.valueOf(runSpeed));// 跑步平均速度（米/秒）
			map.put("pbca", String.valueOf(pbca));// 消耗卡路里
			map.put("pbzjl", pbzjl);// 跑步总距离

			// 活动类型过滤条件：睡眠
			map.put("qsm", qssj);
			map.put("ssm", sssj);
			
			if(hasGotoSleep)
			{
				map.put("getuptime", getuptime);
				map.put("gotobedtime", gotobedtime);
			}

			// 走路
			map.put("zlzbs", zlzbs);// 走路总步数 
			map.put("zlzsj", zlzsj);// 走路总时间（秒） 
			map.put("zlspeed", String.valueOf(workSpeed));// 走路平均速度（米/秒）
			map.put("zlca", String.valueOf(zlca));// 消耗卡路里
			map.put("zlzjl",zlzjl);// 走路总距离

			return JSON.toJSONString(map);
		}
		else
			return null;
	}
	
	/**
	 * 将日汇总数据集合原始数据合计计算的解析和组装方法.
	 * 【目前用于“历史数据”、“日详细”界面中查看合计数据时】.
	 * 
	 * @return
	 */
	public static String parseDaySynopicSumForPreview(Context context, List<DaySynopic> srs)
	{
		if(srs != null && srs.size() > 0)
		{
			//--------------------------------------------------------------------------------
			String[] datas = new String[13];
			for(DaySynopic ds : srs)
			{
				// 累加起来
				datas[0] = String.valueOf(CommonUtils.getIntValue(datas[0]) 
						+ (CommonUtils.getIntValue(ds.getRun_distance()) + CommonUtils.getIntValue(ds.getWork_distance())));
				datas[1] = String.valueOf(CommonUtils.getIntValue(datas[1]) 
						+ (CommonUtils.getIntValue(ds.getRun_duration()) + CommonUtils.getIntValue(ds.getWork_duration())));
				datas[2] = String.valueOf(CommonUtils.getIntValue(datas[2]) 
						+ (CommonUtils.getIntValue(ds.getRun_step()) + CommonUtils.getIntValue(ds.getWork_step())));

				datas[3] = String.valueOf(CommonUtils.getIntValue(datas[3]) 
						+ CommonUtils.getIntValue(ds.getWork_distance()));
				datas[4] = String.valueOf(CommonUtils.getIntValue(datas[4]) 
						+ CommonUtils.getIntValue(ds.getWork_duration()));
				datas[5] = String.valueOf(CommonUtils.getIntValue(datas[5]) 
						+ CommonUtils.getIntValue(ds.getWork_step()));

				datas[6] = String.valueOf(CommonUtils.getIntValue(datas[6]) 
						+ CommonUtils.getIntValue(ds.getRun_distance()));
				datas[7] = String.valueOf(CommonUtils.getIntValue(datas[7]) 
						+ CommonUtils.getIntValue(ds.getRun_duration()));
				datas[8] = String.valueOf(CommonUtils.getIntValue(datas[8]) 
						+ CommonUtils.getIntValue(ds.getRun_step()));

				datas[11] = String.valueOf(CommonUtils.getDoubleValue(datas[11]) 
						+ CommonUtils.getDoubleValue(ds.getSleepMinute()));
				datas[12] = String.valueOf(CommonUtils.getDoubleValue(datas[12]) 
						+ CommonUtils.getDoubleValue(ds.getDeepSleepMiute()));
			}
			
			// 多天的数据
            return parseDaySynopicSumForPreview(context, datas, false);
		}
		else
			return null;
	}
	
	/**
	 * 提交UTC时间请求到服务端.
	 * 目前用于绑定时、同步完运动数据时设置设备时间时。
	 * 
	 * @param registerData
	 * @return
	 */
	public static DataFromServer submitGetServerUTCToServer()
	{
		// 提交请求到服务端
		DataFromClient dataFromClient = DataFromClient.n()
				.setProcessorId(MyProcessorConst.PROCESSOR_LOGIC)		
				.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER)
				.setActionId(SysActionConst.ACTION_APPEND9);
		return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(dataFromClient);
	}
	public static long pareseServerUTC()
	{
		long _beforeSubmitTm = System.currentTimeMillis();
		DataFromServer dfs = HttpHelper.submitGetServerUTCToServer();
		Log.i(TAG, "服务端返回dfs:"+dfs.toString());
		if(dfs.isSuccess())
		{
			try
			{
				// 返回的服务端时间戳
				long _utcFronServer = _Utils.getUTC0FromUTCTime(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS, (String)dfs.getReturnValue());
				long localDeviceTm = System.currentTimeMillis(); // 当前时间戳
				long delta = localDeviceTm - _beforeSubmitTm;    // 本次查询耗时
				// 服务端时间戳+此次好时，就认为是此刻服务端的
				long utcFronServer = _utcFronServer + delta;
				System.out.println("[CC]TESTTEST, 【成功】当前服务端tm:"+_utcFronServer
						+",查询耗时:"+delta+"毫秒,本地tm:"+localDeviceTm+",本地比服务端快："+(localDeviceTm - utcFronServer)+"毫秒！");
				return utcFronServer;
			}
			catch (Exception e)
			{
				Log.w(TAG, e.getMessage(), e);
				return 0;
			}
		}
		else
		{
			System.out.println("[CC]TESTTEST, 【失败】从服务端获取时间失败，原因是："+dfs.getReturnValue());
		}
		return 0;
	}
	
	/**
	 * 提交UTC时间和扫描出的MAC地址是否被绑定的请求到服务端.
	 * 目前用于绑定时、同步完运动数据时设置设备时间时。
	 * 
	 * @param registerData
	 * @return
	 */
	public static DataFromServer submitGetServerUTCAndBoundedToServer(String user_id, String mac)
	{
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("user_id", user_id);
		map.put("mac", mac);
		
		// 提交请求到服务端
		DataFromClient dataFromClient = DataFromClient.n()
				.setProcessorId(MyProcessorConst.PROCESSOR_LOGIC)		
				.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER)
				.setActionId(SysActionConst.ACTION_APPEND10)
				.setNewData(JSON.toJSONString(map));// 注意：目前的通信协议是扁平JASON文本，不支持直接传输java序列化对象！
		
		return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(dataFromClient);
	}
	public static Object[] pareseServerUTCAndBounded(Object retValue)
	{
		long _beforeSubmitTm = System.currentTimeMillis();
//		DataFromServer dfs = HttpHelper.submitGetServerUTCAndBoundedToServer(user_id, mac);
//		if(dfs.isSuccess())
		{
//			if(dfs.getReturnValue() != null)
			if(retValue != null)
			{
				try
				{
					JSONObject nwObj = JSONObject.parseObject((String)retValue);
					String beBoundMail = nwObj.getString("be_bound_mail");
					
					String serverUtcTm = nwObj.getString("server_utc_tm");
					// 返回的服务端时间戳
					long _utcFronServer = _Utils.getUTC0FromUTCTime(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS, serverUtcTm);
					long localDeviceTm = System.currentTimeMillis(); // 当前时间戳
					long delta = localDeviceTm - _beforeSubmitTm;    // 本次查询耗时
					// 服务端时间戳+此次好时，就认为是此刻服务端的
					long utcFronServer = _utcFronServer + delta;
					System.out.println("[CC]TESTTEST, 【成功】当前服务端tm:"+_utcFronServer
							+",查询耗时:"+delta+"毫秒,本地tm:"+localDeviceTm+",本地比服务端快："
							+(localDeviceTm - utcFronServer)+"毫秒！boundMac="+beBoundMail);
					return new Object[]{utcFronServer, beBoundMail};
				}
				catch (Exception e)
				{
					Log.w(TAG, e.getMessage(), e);
				}
			}
			return new Object[]{0, null};
		}
//		else
//		{
//			System.out.println("[CC]TESTTEST, 【失败】从服务端获取时间失败，原因是："+dfs.getReturnValue());
//		}
//		return new Object[]{0, null};
	}
	
	/**
	 * 提交注册信息到服务端.
	 * 
	 * @param registerData
	 * @return
	 */
	public static DataFromServer submitRegisterationToServer(UserRegisterDTO registerData)
	{
		// 提交请求到服务端
		DataFromClient dataFromClient = DataFromClient.n()
				.setProcessorId(MyProcessorConst.PROCESSOR_LOGIC)		
				.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER)
				.setActionId(SysActionConst.ACTION_EDIT)
				// 要接收邀请的邮件地址
				.setNewData(JSON.toJSONString(registerData));// 注意：目前的通信协议是扁平JASON文本，不支持直接传输java序列化对象！
		return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(dataFromClient);
	}
	
	/**
	 * 提交登陆信息到服务端.
	 * 
	 * @param ai
	 * @return
	 */
	public static DataFromServer submitLoginToServer(LoginInfo ai)
	{
		// 服务端会使用User对象来进行反射，但因历史原因：User对象中的字段名如login_name不符合JavaBean的命名规范，
		// 如果强制使用User对象先转JSON的话，到服务端后就不能正常反射了，因为它将形如：{"login_name":"", "login_psw":""}
		// 所以此处干脆手动组件一个服务端能正常反射出来的JSON伪User对象
		String loginInfoWithJSON = "{\"loginName\":\""+ai.getLoginName()+"\", \"loginPsw\":\""+ai.getLoginPsw()+"\"}";
		return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(
				DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_CLIENT_UPDATE_VERSION)
							.setNewData(loginInfoWithJSON)
							);
	}
	public static UserEntity parseLoginFromServer(String jsonOfResult)
	{
		return new Gson().fromJson(jsonOfResult, UserEntity.class);
	}
	
	private static int getCalory(double speed, int seconds, int weight)
	{
		int result = _Utils.calculateCalories(speed, seconds, (int) (weight*ToolKits.UNIT_LBS_TO_KG));
		System.out.println(">>>>>> speed="+speed+", seconds="+seconds+", weight="+weight+", 计算结果="+result);
//		return (int)(speed * 100);
		return result;
	}

}
