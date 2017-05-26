package com.linkloving.rtring_c_watch.logic.main.impl;

import java.util.List;
import java.util.Observer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eva.android.platf.std.AutoUpdateDaemon;
import com.eva.android.widget.DataLoadingAsyncTask;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.eva.epc.core.dto.SysActionConst;
import com.linkloving.band.dto.SportRecord;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.db.logic.UserDeviceRecord;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.JobDispatchConst;

import android.content.Context;
import android.util.Log;

/**
 * 提交本地存储的未同步（到服务端）的运动数据异步执行线程实现类.
 */
public class SportDataSubmitServerAsyncTask extends DataLoadingAsyncTask<Object, Integer, DataFromServer>
{
	private static String TAG = SportDataSubmitServerAsyncTask.class.getSimpleName();
	private int syncCount = 0;
	private List<SportRecord> upList = null;

	// 数据提交成功后要通知的观察者
	private Observer obsForSubmitSucess = null;
	
	public SportDataSubmitServerAsyncTask(Context context, Observer obsForSubmitSucess, List<SportRecord> upList, boolean showProgress)
	{
		super(context,
//				showProgress
				false
				);
		this.obsForSubmitSucess = obsForSubmitSucess;
		this.upList = upList;
	}

	public SportDataSubmitServerAsyncTask(Context context, Observer obsForSubmitSucess, List<SportRecord> upList)
	{
		super(context,
//				context.getString(R.string.general_submitting)
				false
				);
		this.obsForSubmitSucess = obsForSubmitSucess;
		this.upList = upList;
	}
	

	/**
	 * 在后台执行 {@link doLogin()}实现登陆信息的提交和处于结果的读取 .
	 * 
	 * @param parems
	 *            外界传进来的参数
	 * @return 查询结果，将传递给onPostExecute(..)方法
	 */
	@Override
	protected DataFromServer doInBackground(Object... params)//List<LPSportData>... params)
	{
//		// 看看数据库中有多少未同步（到服务端的数据）
//		upList = UserDeviceRecord.findHistoryWitchNoSync(context
//				, MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id());
		
		if(upList != null && upList.size() > 0)
		{
			syncCount = upList.size();

			// 有未同步的数据就同步到服务端先
			if(upList.size() > 0)
			{
				Log.d(TAG, "【NEW未同步数据】已查到"+syncCount+"条未同步的运动数据！");
//				for(SportRecord list:upList){
//					Log.d(TAG, "【NEW未同步数据】已查到"+JSON.toJSONString(upList));
//				}
				JSONObject obj = new JSONObject();
				obj.put("device_id", 1);
				obj.put("utc_time", 1);
				obj.put("user_id", MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id());
				obj.put("list", JSON.toJSONString(upList));
//				Log.d(TAG, "【NEW未同步数据】obj.toJSONString():"+obj.toJSONString());
				return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(
						DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_REPORT)
						.setJobDispatchId(JobDispatchConst.REPORT_BASE)
						.setActionId(SysActionConst.ACTION_EDIT)
						.setNewData(obj.toJSONString()));
			}
			else
			{
				Log.d(TAG, "【NEW离线数据同步】已查到"+syncCount+"条未同步的运动数据，无需继续处理了！");
				DataFromServer dfs = new DataFromServer();
				dfs.setSuccess(true);
				dfs.setReturnValue("no_data_async");
				return dfs;
			}
		}
		else
		{
//			Log.d(TAG, "【NEW离线数据同步】传进来要同步的数据集是空的？！upList="+upList);
			Log.d(TAG, "【NEW离线数据同步】没有需要同步的运动数据，无需继续处理了！");
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
			Log.d(TAG, "运动数据上传成功！！！！");

			// 离线数据同步完成后：标识数据库的记录行的“已同步”为1
			String startTime = upList.get(0).getStart_time();
			String endTime = upList.get(upList.size()-1).getStart_time();
			long sychedNum = UserDeviceRecord.updateForSynced(context, MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id(), startTime, endTime);
			Log.d(TAG, "【NEW离线数据同步】本次共有"+sychedNum+"条运动数据已被标识为\"已同步\"！["+startTime+"~"+endTime+"]");
		}
		else
		{
//				showDebugInfo("自动同步:运动数据已全部同步过，本次无需提交服务端.");
		}

		// 同步完成，通知观察者及时刷新界面
		if(obsForSubmitSucess != null)
			obsForSubmitSucess.update(null, syncCount);
	}
}