package com.linkloving.rtring_c_watch.logic.main.impl;
//package com.linkloving.rtring_c.logic.main.impl;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.UnsupportedEncodingException;
//
//import android.content.Context;
//import android.util.Log;
//
//import com.alibaba.fastjson.JSONObject;
//import com.eva.android.widget.DataLoadingAsyncTask;
//import com.eva.epc.common.file.FileHelper;
//import com.eva.epc.common.util.CommonUtils;
//import com.eva.epc.core.dto.DataFromClient;
//import com.eva.epc.core.dto.DataFromServer;
//import com.eva.epc.core.dto.SysActionConst;
//import com.example.android.bluetoothlegatt.utils.LogX;
//import com.example.android.bluetoothlegatt.utils.LogY;
//import com.linkloving.rtring_c.MyApplication;
//import com.linkloving.rtring_c.http.HttpServiceFactory4AJASONImpl;
//import com.linkloving.rtring_c.logic.DataLoadableMultipleAcitvity;
//import com.rtring.buiness.dto.MyProcessorConst;
//import com.rtring.buiness.logic.dto.JobDispatchConst;
//
//public class BleErrSubmitServerAsyncTask extends
//		DataLoadingAsyncTask<Void, Void, DataFromServer>
//{
//	private final String TAG = BleErrSubmitServerAsyncTask.class.getSimpleName();
//
//	public BleErrSubmitServerAsyncTask(Context context) {
//		super(context, false);
//	}
//
//
//	@Override
//	protected void onPostExecuteImpl(Object result)
//	{
//	      if ((result != null) && ((result instanceof DataFromServer)))
//	      {
//	        if (((DataFromServer)result).isSuccess()) {
//
//				if(result.equals("true"))
//				{
//					// 清空手机内LOG
//					try
//					{
//						FileOutputStream testfile = new FileOutputStream(LogY.getInstance().getLogPath() + LogY.FILENAME);
//						testfile.write(new String("").getBytes());
//						testfile.flush();
//						testfile.close();
//					}
//					catch (IOException e)
//					{
////						e.printStackTrace();
//						Log.w(TAG, e.getMessage(), e);
//					}
//				}
//			
//	        }
//	      }
//	}
//
//	@Override
//	protected DataFromServer doInBackground(Void... arg0)
//	{
//	    if(MyApplication.getInstance(context).getLocalUserInfoProvider().getEnable_sync_log().equals("1") && 
//	    		FileHelper.isFileExist(LogY.getInstance().getLogPath() + LogY.FILENAME))
//        {
//			try {
//				String content = new String(FileHelper.readFileWithBytes(new File(LogY.getInstance().getLogPath() + LogY.FILENAME)), "utf-8");
//				if(!CommonUtils.isStringEmpty(content))
//				{
//					JSONObject obj = new JSONObject();
//					obj.put("user_id", MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id());
//					obj.put("log_content", content);
//					return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(
//							DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_LOGIC)
//							.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER)
//							.setActionId(SysActionConst.ACTION_APPEND12)
//							.setNewData(obj.toJSONString()));
//				}
//			} catch (UnsupportedEncodingException e) {
//				Log.w(TAG, e.getMessage(), e);
//			} catch (Exception e) {
//				Log.w(TAG, e.getMessage(), e);
//			}
//        }
//	    
//	    return null;
//	}
//	
//	
//	
//	
//
//}
