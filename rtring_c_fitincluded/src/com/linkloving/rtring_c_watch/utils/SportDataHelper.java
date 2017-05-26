package com.linkloving.rtring_c_watch.utils;

import java.util.ArrayList;
import java.util.List;

import com.example.android.bluetoothlegatt.proltrol.dto.LPSportData;
import com.linkloving.band.dto.SportRecord;
import com.linkloving.utils.TimeZoneHelper;

import android.util.Log;

public class SportDataHelper
{
	private final static String TAG = SportDataHelper.class.getSimpleName();
	
	/**
	 * 将设备中读取出来的原始运动数据转成应用层使用的运动数据.
	 * 
	 * @param params
	 * @return
	 */
	public static List<SportRecord> convertLPSportData(List<LPSportData> original)
	{
		List<SportRecord> upList = new ArrayList<SportRecord>();
		
		for(LPSportData sportData:original)
		{
//			String _dt = null;
//			Date dt = null;
			try
			{
				SportRecord sportRecord = new SportRecord();
				sportRecord.setDevice_id("1");
				sportRecord.setDistance(sportData.getDistance()+"");
				sportRecord.setDuration(sportData.getDuration() + "");
//				_dt = new SimpleDateFormat(com.linkloving.rtring_c.utils.ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS)
//					.format(((long)sportData.getTimeStemp())*1000+((long)sportData.getRefTime())*30*1000);
//				 Calendar c = Calendar.getInstance(Locale.CHINA);
//				 int zoneOffset = c.get(java.util.Calendar.ZONE_OFFSET);
//				 int zone=zoneOffset/60/60/1000; //时区偏移值
				long utcTimestamp = ((long)sportData.getTimeStemp())*1000+((long)sportData.getRefTime())*1000;
//				dt = new Date(utcTimestamp);
				// 运动时间用UTC时间（保证跨时区能正常使用）
				sportRecord.setStart_time(TimeZoneHelper.__getUTC0FromLocalTime(com.linkloving.rtring_c_watch.utils.ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS, utcTimestamp));
				sportRecord.setStep(""+sportData.getSteps());
				sportRecord.setState(""+sportData.getState());
				upList.add(sportRecord);
			}
			catch (Exception e)
			{
				Log.w(TAG, "运动数据时间转换成UTC时间时出错，_dt=", e);
			}
		}
		
		return upList;
	}
	
	/**
	 * 将设备中读取出来的原始运动数据转成google-fit需要的数据.
	 * 
	 * @param params
	 * @return
	 */
	public static List<GooglefitDate> SportData2Google(List<SportRecord> original)
	{
		List<GooglefitDate> upList = new ArrayList<GooglefitDate>();
//		start_time:1456771200000---end_time:1456771290000----stepCountDelta:107
//		GooglefitDate googledate_1 = new GooglefitDate();
//		googledate_1.setStart_time(1456771305000L);
//		googledate_1.setEnd_time(1456771310000L);
//		googledate_1.setStep(20);
//		upList.add(googledate_1);
//		
//		GooglefitDate googledate_2 = new GooglefitDate();
//		googledate_2.setStart_time(1456771310000L);
//		googledate_2.setEnd_time(1456771315000L);
//		googledate_2.setStep(31);
//		upList.add(googledate_2);
//		
//		GooglefitDate googledate_3 = new GooglefitDate();
//		googledate_3.setStart_time(1456771315000L);
//		googledate_3.setEnd_time(1456771320000L);
//		googledate_3.setStep(22);
//		upList.add(googledate_3);
//		
//		GooglefitDate googledate_4 = new GooglefitDate();
//		googledate_4.setStart_time(1456771320000L);
//		googledate_4.setEnd_time(1456771321000L);
//		googledate_4.setStep(23);
//		upList.add(googledate_4);
//		
//		GooglefitDate googledate_5 = new GooglefitDate();
//		googledate_5.setStart_time(1456771322000L);
//		googledate_5.setEnd_time(1456771323000L);
//		googledate_5.setStep(23);
//		upList.add(googledate_5);
//		
//		GooglefitDate googledate_6 = new GooglefitDate();
//		googledate_6.setStart_time(1456771324000L);
//		googledate_6.setEnd_time(1456771325000L);
//		googledate_6.setStep(23);
//		upList.add(googledate_6);
//		
//		GooglefitDate googledate_7 = new GooglefitDate();
//		googledate_7.setStart_time(1456771326000L);
//		googledate_7.setEnd_time(1456771327000L);
//		googledate_7.setStep(23);
//		upList.add(googledate_7);
//		
//		GooglefitDate googledate_8 = new GooglefitDate();
//		googledate_8.setStart_time(1456771328000L);
//		googledate_8.setEnd_time(1456771329000L);
//		googledate_8.setStep(23);
//		upList.add(googledate_8);
		
		for(SportRecord sportData:original)
		{
			try
			{
				GooglefitDate googledate = new GooglefitDate();
				if(Integer.parseInt(sportData.getState())==1 || Integer.parseInt(sportData.getState())==2){
					long start_time = com.linkloving.rtring_c_watch.utils.ToolKits.stringToLong(sportData.getStart_time(), com.linkloving.rtring_c_watch.utils.ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);
					googledate.setStart_time(start_time);
					googledate.setEnd_time(Long.parseLong(sportData.getDuration())*1000+start_time);
					googledate.setStep(Integer.parseInt(sportData.getStep()));
					upList.add(googledate);
				}
			}
			catch (Exception e)
			{
				Log.w(TAG, "运动数据时间转换成google-fit出错，_dt=", e);
			}
		}
		
		
		if(upList.size()>1000){
			upList.subList(upList.size()-1001, upList.size());
			return upList;
		}
		
		return upList;
	}
	
	// **** 现在的版本中，无需回填睡眠状态，2015-01-21 by Jack Jiang
//	/**
//	 * 计算睡眠情况并回填到原始数据集合中（以便存放到本地和服务端）。
//	 * 
//	 * @param context
//	 * @param upList
//	 */
//	public static void backStauffSleepState(Context context, List<SportRecord> upList)
//	{
//		if(upList != null && upList.size() > 0)
//		{
//			//********************************************************* 【2】每次同步完数据后计算睡眠情况
//			// 计算睡眠是额外要做的事，try catch的目的是保证无论在何种错误下都不应影响数据的保存（切记！）
//			try
//			{
//				long durationSum = DatasProcessHelper.cascatedSportDataDuration(upList);
//				final int NEED_DURATION = 90 * 60;
//				// 当数据量小于1个半小时时（需要获取额外的数据进行睡眠计算）
//				if(durationSum < NEED_DURATION)
//				{
//					Log.w(TAG, "【读取完运动数据后计算睡眠：不足1时半】当前总数提成时长"+durationSum+"秒，不足"+NEED_DURATION+"秒，需要从网上或本地取前推12小时的数据哦！");
//					// 取出读取到的数据的首行
//					SportRecord firstRow = upList.get(0);
//					// 首行的时间
//					String firstRowStartTime = firstRow.getStart_time();
//
//					// 计算要前推的数据的起始时间
//					SimpleDateFormat DATE_PATTERN_sdfYYMMDD = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);
//					GregorianCalendar gc = new GregorianCalendar();
//					gc.setTime(DATE_PATTERN_sdfYYMMDD.parse(firstRowStartTime));
//					gc.add(GregorianCalendar.SECOND, -1); // 首行时间-1秒（防重复，因为接下来要用到的查询数据条件是>=start和<=end）
//					String dateTimeEnd_willFetch = DATE_PATTERN_sdfYYMMDD.format(gc.getTime());
//					gc.add(GregorianCalendar.HOUR_OF_DAY, -12);// 前推12小时的时间
//					String dateTimeStart_willFetch = DATE_PATTERN_sdfYYMMDD.format(gc.getTime());
//
//					Log.w(TAG, "【读取完运动数据后计算睡眠：不足1时半】数据中首行数据时间"+firstRowStartTime+", 接下来要取的数据为["
//							+dateTimeStart_willFetch+","+dateTimeEnd_willFetch+"]时间范围内的数据！");
//
//					final boolean online = ToolKits.isNetworkConnected(context);
//					// 查询（优先从服务端，无网的情况下从本地）前推（12小时）的数据
//					DataFromServer dfsForFetchData = HttpHelper.submitQuerySportRecordsToServer_l(context
//							, dateTimeStart_willFetch, dateTimeEnd_willFetch, online, false
//							, true// 注意：此处查询时的时间范围已经是UTC时间了（因为用的是设备同步上来的数据的时间）
//							);
//					if(dfsForFetchData.isSuccess())
//					{
//						// 查询数据成功，解析之
//						List<SportRecord> last12HourDatas =
//								HttpHelper.parseQuerySportRecordsFromServer(context, dfsForFetchData.getReturnValue(), online
//										, false // 注意：联网时使用同步方式保存到本地，否则会发生本次数据先于此数据放入（那么就可能被误删除了，因为插入先会先删除）
//										);
//						int last12HourDatasCount = last12HourDatas.size();
//						Log.w(TAG, "【读取完运动数据后计算睡眠：不足1时半】前12小时数据读取出来了，记录数："+last12HourDatas.size());
//
//						last12HourDatas.addAll(upList); // 将原数据加到前面12小时数据的后面
//						Log.w(TAG, "【读取完运动数据后计算睡眠：不足1时半】将前12小时数据与现次数据合并了，记录数共："+last12HourDatas.size());
//
//						// 计算睡眠后的结果
//						List<DLPSportData> dataAfterCalcuSleep = SleepDataHelper.querySleepDatas2(last12HourDatas);
//
//						Log.w(TAG, "【读取完运动数据后计算睡眠：不足1时半】计算完睡眠结果后，记录数共："+dataAfterCalcuSleep.size());
//						// 将合并计算后的睡眠数据结果的前12小时数据裁剪掉（以便与原始数据一一对应）
//						// 返射ArrayList中的protect方法removeRange以便在性能有保障的情况下裁剪数据
//						ReflectHelper.invokeMethod(ArrayList.class
//								, dataAfterCalcuSleep, "removeRange"
//								, new Class[]{int.class, int.class}
//						// 移除列表中索引在 0（包括）和 willToTrimCount（不包括）之间的所有元素
//						// 如要移除的条数是5（即willToTrimCount=5），则本次移除的索引会是：0、1、2、3、4
//						, new Object[]{0, last12HourDatasCount}, true);
//						Log.w(TAG, "【读取完运动数据后计算睡眠：不足1时半】计算完睡眠结果后裁剪掉12小时前数据，记录数还余："
//								+dataAfterCalcuSleep.size());
//
//						// 清除内存
//						last12HourDatas = null;
//
//						// 将睡眠算法计算完成的睡眠状态回填（那么这样的话，在组织成日汇总数据时也就能合计出睡眠时间了）
//						DatasProcessHelper.putSleepStateFromSleepResult(upList, dataAfterCalcuSleep);
//					}
//					else
//					{
//						Log.w(TAG, "【读取完运动数据后计算睡眠：不足1时半】前推数据读取失败，原因是："+dfsForFetchData.getReturnValue());
//					}
//				}
//				// 大于1个半小时的数据，直接计算睡眠
//				else
//				{
//					// 计算睡眠后的结果
//					List<DLPSportData> dataAfterCalcuSleep = SleepDataHelper.querySleepDatas2(upList);
//					// 将睡眠算法计算完成的睡眠状态回填（那么这样的话，在组织成日汇总数据时也就能合计出睡眠时间了）
//					DatasProcessHelper.putSleepStateFromSleepResult(upList, dataAfterCalcuSleep);
//				}
//			}
//			catch (Exception e)
//			{
//				Log.w(TAG, e.getMessage(), e);
//			}
//		}
//		else
//		{
//			Log.w(TAG, "【读取完运动数据后计算睡眠：数据集合是空？】upList="+upList);
//		}
//	}
}
