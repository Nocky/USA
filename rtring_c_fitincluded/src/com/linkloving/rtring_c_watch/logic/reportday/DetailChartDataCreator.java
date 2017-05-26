package com.linkloving.rtring_c_watch.logic.reportday;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.eva.android.widget.DataLoadingAsyncTask;
import com.eva.epc.core.dto.DataFromServer;
import com.linkloving.band.dto.SportRecord;
import com.linkloving.band.sleep.DLPSportData;
import com.linkloving.band.sleep.SleepDataHelper;
import com.linkloving.band.ui.BRDetailData;
import com.linkloving.band.ui.DatasProcessHelper;
import com.linkloving.band.ui.DetailChartCountData;
import com.linkloving.rtring_c_watch.logic.reportday.model.DetailChartData;
import com.linkloving.rtring_c_watch.logic.reportday.util.TimeUtil;
import com.linkloving.rtring_c_watch.utils.HttpHelper;
import com.linkloving.rtring_c_watch.utils.ToolKits;
/**
 * 图表运动数据的获取和合并统计处理
 * @author Administrator
 *
 */
public abstract class DetailChartDataCreator 
{
	
	private final String TAG = DetailChartDataCreator.class.getSimpleName();
	
	/** 日期格式 */
	protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	/** 返回数据*/
	public abstract void  onDataResult(List<BRDetailData> result,DetailChartCountData count);
	
	/** 获取图表运动数据*/
	public void getDetailChartData(Context context,int chartDayIndex)
	{
//		long startTime;
//		//
//		if(TimeZone.getDefault().getRawOffset()>0){
//			startTime = TimeUtil.getTimeByDayIndex(chartDayIndex - 1);
//		}else{
//			startTime = TimeUtil.getTimeByDayIndex(chartDayIndex);
//		}
		final long startTime = TimeUtil.getTimeByDayIndex(chartDayIndex-1);
		final long endTime = TimeUtil.getTimeByDayIndex(chartDayIndex+2);
		Log.i(TAG, "startTime："+(long)startTime+"******endTime:"+(long)endTime);
		String[] params = {sdf.format(startTime),sdf.format(endTime)};
		//进行时区判断
		LoadDataWorker worker = new LoadDataWorker(context, false, TimeZone.getDefault().getRawOffset() >= 0 ? chartDayIndex : (chartDayIndex - 1));
//		AsyncTaskManger.getAsyncTaskManger().addAsyncTask(worker);
		worker.execute(params);
	}

   /**
    * 获取未合并数据
    * @param src
    * @param dayIndex
    * @return
    */
	private List<BRDetailData> getTodayData(List<BRDetailData> src,int dayIndex)
	{
		   List<BRDetailData> result = new ArrayList<BRDetailData>();
	       for(int i = 0; i < src.size() ;i++)
	        {
	        	if(src.get(i).getDayIndex() == dayIndex)
	        	{
	        		result.add(src.get(i));
	        	}
	        }
	        return result;
	}
	
//	/**
//	 * 统计运动数据
//	 * @param dayIndx
//	 * @param src
//	 * @param count
//	 * @return
//	 */
//	private List<BRDetailData> countSportData(int dayIndx,List<BRDetailData> src,DetailChartCountData count)
//	{
//        boolean sleepStart = false;
//        int day_index_bak = 0;
//        
//        float tmp_soft_sleep = 0;
//        float tmp_deep_sleep = 0;
//        
//        List<BRDetailData> tmp = new ArrayList<BRDetailData>();
//
//		if (src.size() ==0)
//	    {
//	        return null;
//	    }
//	 
//    for(BRDetailData data : src)
//    {
//        if((data.getState() == BRDetailData.STATE_WALKING)
//        		||(data.getState() == BRDetailData.STATE_RUNNING)
//        		||(data.getState() == BRDetailData.STATE_SLEEP_LIGHT)
//        		||(data.getState() == BRDetailData.STATE_SLEEP_DEEP))
//        {
//       // 	add to list;
//             tmp.add(data);
//             Log.d(TAG, "统计:"+data.toString());
//        }
//        
//        if(day_index_bak == 0)
//    	{
//    		day_index_bak = data.getDayIndex();
//    	}   
//
//        switch (data.getState())
//        {
//            case BRDetailData.STATE_WALKING:
//                sleepStart =false;
//                if(data.getDayIndex() ==dayIndx )
//                {
//                	//   Log.d(TAG, "统计走路:"+data.toString());
//                	   count.walking_distance +=data.getDistance();
//                       count.walking_steps += data.getSteps();
//                       count.walking_duration += data.getDuration();
//                }
//                break;
//
//            case BRDetailData.STATE_RUNNING:
//                sleepStart =false;
//                if(data.getDayIndex() ==dayIndx )
//                {
//                    count.runing_distance +=data.getDistance();
//                    count.runing_steps += data.getSteps();
//                    count.runing_duation += data.getDuration();
//               	 //   Log.d(TAG, "统计跑步:"+data.toString());
//                }
//                break;
//            case BRDetailData.STATE_SLEEP_LIGHT:
//                sleepStart = true;
//                if(data.getDayIndex() == dayIndx )
//                {
//                	   count.soft_sleep_duration +=  data.getDuration();
//                       tmp_soft_sleep += data.getDuration();
//                }
//                break;
//            case BRDetailData.STATE_SLEEP_DEEP:
//                sleepStart = true;
//                if(data.getDayIndex() == dayIndx )
//                {
//                	   if ( data.getDuration() >= 10)
//                       {
//                       	   count.soft_sleep_duration +=10;
//                           count.deep_sleep_duration += ( data.getDuration() -10);
//                           tmp_deep_sleep  += ( data.getDuration() -10);
//                           tmp_soft_sleep += 10;
//                        }
//                       else
//                       {
//                       	   count.soft_sleep_duration +=  data.getDuration();
//                           tmp_soft_sleep +=  data.getDuration();
//                       }
//                }
//             
//                break;
//            case BRDetailData.STATE_SLEEP_ACTIVE:
//                if(data.getDayIndex() >dayIndx ) break;
//                 break;
//
//            default:
//                sleepStart = false;
//                break;
//        }
//        if(!sleepStart)
//        {
//        	tmp_deep_sleep = 0;
//        	tmp_soft_sleep = 0;
//        }
//        
//        if(day_index_bak != data.getDayIndex())
//        {
//        	if(day_index_bak == (dayIndx-1))
//        	{
//        		count.soft_sleep_duration = tmp_soft_sleep;
//        		count.deep_sleep_duration = tmp_deep_sleep;
//        	}
//        	else if(day_index_bak == dayIndx)
//        	{
//        		count.soft_sleep_duration -= tmp_soft_sleep;
//        		count.deep_sleep_duration -= tmp_deep_sleep;
//        	}
//        	else
//        	{
//        		
//        	}
//        	day_index_bak = data.getDayIndex();
//        }
//	}
//    //Log.d(TAG, "统计跑步总数："+count.toString());
//    return tmp;
//	}
	
	private List<DLPSportData> parseFromDataFromServer(Context context,DataFromServer dataFromServer )
	{
		 Object obj = dataFromServer.getReturnValue();
		 if(obj != null)
		 {
				List<SportRecord> originalSportDatas = HttpHelper.parseQuerySportRecordsFromServer(context,obj, false);
				// 计算睡眠
				List<DLPSportData> srs = SleepDataHelper.querySleepDatas2(originalSportDatas);
//				for(int i=0;i<srs.size();i++){
//					Log.e(TAG, srs.get(i).toString());
//				}
				
				// 将睡眠算法计算完成的睡眠状态回填（那么这样的话，在组织成日汇总数据时也就能合计出睡眠时间了）
			    // ToolKits.putSleepStateFromSleepResult(originalSportDatas, srs);
				return srs;
		 }
		 else
		 {
			 return null;
		 }
	
	}
	
	public int change2BRDetailDataListAndCountDate(List<DLPSportData> srs,List<BRDetailData> target)
	{
		Map<Integer, Integer> coutMap = new HashMap<Integer, Integer>();
		int sum = 0;
		if(srs != null && target != null)
		{
			for(DLPSportData row:srs)
			{
				BRDetailData item = new BRDetailData(row);
//				Log.i(TAG, "原始数据："+row.toString());
				if(row.state==4 || row.state==5){
					sum+=row.duration;
				}
				
				coutMap.put(item.getDayIndex(), item.getDayIndex());
				target.add(item);
			}
			return coutMap.size();
		}
		return -1;
	}
	
	
	private int countDateFromDataFromServer(Context context,DataFromServer dataFromServer,List<BRDetailData> target)
	{
		   if ((dataFromServer != null) &&  dataFromServer.isSuccess())
		    {
			   Log.e(TAG, "开始解析数据：》》》》》》》》》》》》》》》》》》》》》》》》》"+new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS).format(new Date()));
		        List<DLPSportData> srs = parseFromDataFromServer(context, dataFromServer);
		        Log.e(TAG, "结束解析数据：《《《《《《《《《《《《《《《《《《《《《《《《《"+new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS).format(new Date()));
		        Log.e(TAG, "开始转换数据：》》》》》》》》》》》》》》》》》》》》》》》》》"+new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS).format(new Date()));
		        int dayCount = change2BRDetailDataListAndCountDate(srs,target);
		        Log.e(TAG, "结束转换数据：《《《《《《《《《《《《《《《《《《《《《《《《《"+new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS).format(new Date()));
				return dayCount;
		    }
		   else
		   {
			   Log.e(TAG, "--失败");
		   }
		   return -1;
	}
	
	private DetailChartData parseDetailChartData(int dayIndex,List<BRDetailData> detailData)
	{
		if(detailData != null)
		{
//			int sum = 0;
//			for(int i=0;i<detailData.size();i++){
//				if(detailData.get(i).getDayIndex() == dayIndex && detailData.get(i).getState() == 4)
//				sum+=detailData.get(i).getDuration();
//			}
//			Log.e(TAG, "dayIndex"+dayIndex+"  sum--------------:"+sum);
			Log.e(TAG, "开始统计合并数据：》》》》》》》》》》》》》》》》》》》》》》》》》"+new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS).format(new Date()));
			DetailChartCountData count = new DetailChartCountData();
			List<BRDetailData> data = DatasProcessHelper.countSportData(dayIndex, detailData,count);
			detailData.clear();
			
		    DetailChartData result = new DetailChartData();
		    result.count = count;
		    Log.i(TAG, ""+count.toString());
		    
		    result.list = DatasProcessHelper.mergeSrcData(data,dayIndex);
//		    for(int i=0;i<result.list.size();i++){
//			    Log.e(TAG, "data合并数据：《《《《《《《《《《《《《《《《《《《《《《《《《"+result.list.get(0).toString());
//			}
		    
		    
		    Log.e(TAG, "结束统计合并数据：《《《《《《《《《《《《《《《《《《《《《《《《《"+new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS).format(new Date()));
		    return result;
		}
		return null;
	}

	/**
	 * 异步下载处理运动数据
	 * @author Administrator
	 *
	 */
	private class LoadDataWorker extends DataLoadingAsyncTask<String, Object, DetailChartData>
	{
		private boolean online = false;
		private List<BRDetailData> detailData = new ArrayList<BRDetailData>();
		private int dayIndex;
		
        public LoadDataWorker(Context context, boolean showProgress,int dayIndex)
        {
        	super(context, showProgress);
        	this.dayIndex = dayIndex;
        	
        	addAsyncTask(this);
        }
        
		public LoadDataWorker(Context context, boolean showProgress) {
			super(context, showProgress);
			
			addAsyncTask(this);
		}

		public LoadDataWorker(Context context, String showMessage) {
			super(context, showMessage);
			
			addAsyncTask(this);
		}

		public LoadDataWorker(Context context) {
			super(context);
			addAsyncTask(this);
		}
		
		@Override
		protected void onPostExecute(DetailChartData result)
		{
			try
			{
				if(result != null)
				{
					onDataResult(result.list, result.count);
					
				}
				else 
				{
					onDataResult(null,null);
				}
			}
			catch (Exception e)
			{
			}
			finally
			{
				removeAsyncTask(this);
			}
		}

		@Override
		protected DetailChartData doInBackground(String... arg0) {
			synchronized(DetailChartDataCreator.class)
			{
				online = ToolKits.isNetworkConnected(context);
				System.out.println("<<arg0:"+arg0[0]+"-----arg1:"+arg0[1]);
				Log.e(TAG, "开始查询数据：》》》》》》》》》》》》》》》》》》》》》》》》》"+new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS).format(new Date()));
				DataFromServer dataFromServer =  HttpHelper.submitQuerySportRecordsToServer_l(context, arg0[0], arg0[1], online,true);
				Log.e(TAG, "结束查询数据：《《《《《《《《《《《《《《《《《《《《《《《《《"+new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS).format(new Date()));
				Log.d(TAG, "<<arg0:"+arg0[0]+"-----arg1:"+arg0[1]);
				detailData.clear();
				
                int dayCount =  countDateFromDataFromServer(context,dataFromServer,detailData);
				if(dayCount >= 0)
				{
//					Date date = TimeUtil.parseDateFromDayIndex(dayIndex);
//					if(TimeUtil.isToday(date) && dayCount > 1 || dayCount > 2)
//					{
//						//do nothing
//					}
//					else 
//					{
//						detailData.clear();
//						dataFromServer =  HttpHelper.submitQuerySportRecordsToServer_l(
//								context, arg0[0], arg0[1], online,
//								true,false,true);
//						dayCount =  countDateFromDataFromServer(context,dataFromServer,detailData);
//						if(dayCount < 0)
//						{
//							 return null;
//						}
//					}
					return parseDetailChartData(dayIndex, detailData);
				}
				  return null;
			}
		
		}

		@Override
		protected void onPostExecuteImpl(Object arg0) {
		}
	}
	
	protected abstract void addAsyncTask(AsyncTask at);
	protected abstract void removeAsyncTask(AsyncTask at);
	
}
