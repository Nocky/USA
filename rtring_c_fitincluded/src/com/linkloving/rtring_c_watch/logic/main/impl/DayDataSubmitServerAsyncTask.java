package com.linkloving.rtring_c_watch.logic.main.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observer;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eva.android.platf.std.AutoUpdateDaemon;
import com.eva.android.widget.DataLoadingAsyncTask;
import com.eva.epc.common.file.FileHelper;
import com.eva.epc.common.util.CommonUtils;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.eva.epc.core.dto.SysActionConst;
import com.example.android.bluetoothlegatt.proltrol.dto.LPSportRecorder;
import com.example.android.bluetoothlegatt.utils.LogX;
import com.linkloving.band.dto.DaySynopic;
import com.linkloving.band.dto.SportRecord;
import com.linkloving.band.sleep.SleepDataHelper;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.db.logic.DaySynopicTable;
import com.linkloving.rtring_c_watch.db.logic.UserDeviceRecord;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.JobDispatchConst;

/**
 * 提交数据请求和处理的异步执行线程实现类.(submit Server)
 */
public class DayDataSubmitServerAsyncTask extends DataLoadingAsyncTask<List<LPSportRecorder>, Integer, DataFromServer>
{
	private static String TAG = DayDataSubmitServerAsyncTask.class.getSimpleName();
	
	private int syncCount = 0;
	private List<DaySynopic> upList = null;
	// 数据提交成功后要通知的观察者
	private Observer obsForSubmitSucess = null;
	
	public DayDataSubmitServerAsyncTask(Context context, Observer obsForSubmitSucess, boolean showProgress)
	{
		super(context, 
//				showProgress  强行取消dialog
				false 
				);
		this.obsForSubmitSucess = obsForSubmitSucess;
	}
	
	public DayDataSubmitServerAsyncTask(Context context, Observer obsForSubmitSucess)
	{
		super(context,
//				context.getString(R.string.general_submitting   取消dialog
				false
						);
		this.obsForSubmitSucess = obsForSubmitSucess;
	}

	/**
	 * 在后台执行 {@link doLogin()}实现登陆信息的提交和处于结果的读取 .
	 * 
	 * @param parems
	 *            外界传进来的参数
	 * @return 查询结果，将传递给onPostExecute(..)方法
	 */
	@Override
	protected DataFromServer doInBackground(List<LPSportRecorder>... params)
	{
		String user_id = MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id();
		// 看看数据库中有多少未同步（到服务端的数据）
		upList = DaySynopicTable.findHistoryWitchNoSync(context, user_id);
		syncCount = upList.size();

		//** 有未同步的数据就同步到服务端先
		if(upList.size() > 0)
		{
			Log.d(TAG, "【离线数据处理】已查到"+syncCount+"条未同步的汇总数据，马上开始上传。。。");//，马上开始计算睡眠情况...");

			//* 自2014-07-04日起，睡眠数据不用计到汇总数据里了（汇总数据没有多大作用了），以下代码停用了 (15 0825再次启用 )
//			//				String user_id = MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getUser_id();
//			//** 在提交到服务端前先计算睡眠数据并回填到汇总数据里，以便提交到服务端后能用在月这样的报表里（便于日后提升性能）
//			for(DaySynopic ds : upList)
//			{
//				long t = System.currentTimeMillis();
//				// 日期
//				String date = ds.getData_date();
//
//				//------------------------------------------- 计算睡眠
//				// 浅睡眠单位：小时
//				double qsm = 0;
//				// 深睡眠单位：小时
//				double ssm = 0;
//
//				// 取出该日的运动明细数据
//				ArrayList<SportRecord> theDaySportDatas = UserDeviceRecord.findHistoryForSleepCalculate_l(
//						context, user_id, date);
//				if(theDaySportDatas.size() > 0)
//				{
//					// 计算睡眠数据
//					double[] ret2 = SleepDataHelper.getSleepSum(
//							//							SleepDataHelper.querySleepDatas(db, startDate, endDate, userId)
//							SleepDataHelper.querySleepDatas2(theDaySportDatas))
//							;
//					if(ret2 == null)
//						Log.d(TAG, "没有睡眠数据！");
//					else
//					{
//						qsm = SleepDataHelper.getMinuteWithSecond(ret2[0]);
//						ssm = SleepDataHelper.getMinuteWithSecond(ret2[1]);
//						
////						qsm = ret2[0];
////						ssm = ret2[1];
//
//						// 把计算出来的睡眠数据回填
//						ds.setSleepMinute(String.valueOf(qsm));
//						ds.setDeepSleepMiute(String.valueOf(ssm));
//
//						// 同时也更新本地数据的睡眠时间
//						long defect = DaySynopicTable.updateSleepTime(context, user_id
//								, date, Double.valueOf(qsm).longValue(), Double.valueOf(ssm).longValue());
//						if(defect > 0)
//							Log.d(TAG, "更新"+date+"日的睡眠时间到本地离线数据里成功了.");
//						else
//							Log.d(TAG, "更新"+date+"日的睡眠时间没有影响任何行！");
//					}
//				}
//
//				Log.d(TAG, "计算睡眠时间-> date="+date+"浅睡时间："+qsm+"深睡时间："
//						+ssm+".[完整计算耗时:"+(System.currentTimeMillis()-t)+"]");
//			}

			JSONObject obj = new JSONObject();
			obj.put("user_id", user_id);
			obj.put("list", JSON.toJSONString(upList));
			return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(
					DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_REPORT)
					.setJobDispatchId(JobDispatchConst.REPORT_DAY_SYNOPIC)
					.setActionId(SysActionConst.ACTION_APPEND2)
					.setNewData(obj.toJSONString()));
		}
		else
		{
			Log.d(TAG, "【离线数据同步】已查到"+syncCount+"条未同步的汇总数据，无需继续处理了！");
			DataFromServer dfs = new DataFromServer();
			dfs.setSuccess(true);
			return dfs;
		}
	}

	/**
	 * 处理服务端返回的登陆结果信息.
	 * 
	 * @see AutoUpdateDaemon
	 * @see #needSaveDefaultLoginName()
	 * @see #afterLoginSucess()
	 */
	protected void onPostExecuteImpl(Object result)
	{
		if(upList != null && upList.size() > 0)
		{
			Log.d(TAG, "汇总数据上传成功！！！！");

			String[] dates = new String[upList.size()];
			for(int i = 0; i<upList.size(); i++)
			{
				DaySynopic ds = upList.get(i);
				if(ds != null)
					dates[i] = ds.getData_date();
			}

			// 离线数据同步完成后：标识数据库的记录行的“已同步”为1
			if(MyApplication.getInstance(context).getLocalUserInfoProvider() != null)
			{
				DaySynopicTable.updateForSynced(context , MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id()
						, dates);
				Log.d(TAG, "本次共有"+dates.length+"条汇总数据已被标识为\"已同步\"！["+Arrays.toString(dates)+"]");
			}
			else
			{
			     return;	
			}
		}
		else
		{
//				showDebugInfo("自动同步:汇总数据已全部同步过，本次无需提交服务端.");
		}

		// 同步完成，通知观察者及时刷新界面
		if(obsForSubmitSucess != null)
			obsForSubmitSucess.update(null, syncCount);
		
		//
		uploadUserLog();
	}
	
	/**
	 * 读取用户的蓝牙Log，并发到服务端端以供调试之用。
	 */
	private void uploadUserLog()
	{
	    /**
	     * 上传LOG
	     */
	    //是否允许上传log
	    if(MyApplication.getInstance(context).getLocalUserInfoProvider().getEnable_sync_log().equals("1") && 
	    		FileHelper.isFileExist(LogX.getInstance().getLogPath() + LogX.FILENAME))
        {
		    try
			{
				String content = new String(FileHelper.readFileWithBytes(new File(LogX.getInstance().getLogPath() + LogX.FILENAME)), "utf-8");
				if(!CommonUtils.isStringEmpty(content))
				{
					new DataLoadingAsyncTask<String, Integer, DataFromServer>(context, false)
					{
						@Override
						protected DataFromServer doInBackground(String... params)
						{
							
							String log = params[0];
							JSONObject obj = new JSONObject();
							obj.put("user_id", MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id());
							obj.put("log_content", log);
							return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(
									DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_LOGIC)
									.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER)
									.setActionId(SysActionConst.ACTION_APPEND12)
									.setNewData(obj.toJSONString()));
						}
	
						/**
						 * 处理服务端返回的登陆结果信息.
						 * 
						 * @see AutoUpdateDaemon
						 * @see #needSaveDefaultLoginName()
						 * @see #afterLoginSucess()
						 */
						protected void onPostExecuteImpl(Object result)
						{
							if(result.equals("true"))
							{
								// 清空手机内LOG
								try
								{
									FileOutputStream testfile = new FileOutputStream(LogX.getInstance().getLogPath() + LogX.FILENAME);
									testfile.write(new String("").getBytes());
									testfile.flush();
									testfile.close();
								}
								catch (IOException e)
								{
//									e.printStackTrace();
									Log.w(TAG, e.getMessage(), e);
								}
							}
						}
					}.execute(content);
				}
			}
			catch (UnsupportedEncodingException e1)
			{
//				e1.printStackTrace();
				Log.w(TAG, e1.getMessage(), e1);
			}
			catch (Exception e1)
			{
//				e1.printStackTrace();
				Log.w(TAG, e1.getMessage(), e1);
			}
        }
	}
}