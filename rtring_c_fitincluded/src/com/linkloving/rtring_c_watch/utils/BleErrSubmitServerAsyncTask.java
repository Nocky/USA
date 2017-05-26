package com.linkloving.rtring_c_watch.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import com.alibaba.fastjson.JSONObject;
import com.eva.android.widget.DataLoadingAsyncTask;
import com.eva.epc.common.file.FileHelper;
import com.eva.epc.common.util.CommonUtils;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.eva.epc.core.dto.SysActionConst;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.linkloving.rtring_c_watch.utils.logUtils.LogcatHelper;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.JobDispatchConst;

import android.content.Context;
import android.util.Log;

public class BleErrSubmitServerAsyncTask extends
		DataLoadingAsyncTask<Void, Void, DataFromServer>
{
	private final static String TAG = BleErrSubmitServerAsyncTask.class.getSimpleName();

	public BleErrSubmitServerAsyncTask(Context context) {
		super(context, false);
	}


	@Override
	protected void onPostExecuteImpl(Object result)
	{
		  Log.e(TAG, "result:"+result);
	      if ((result != null) && ((result instanceof DataFromServer)))
	      {
	        if (((DataFromServer)result).isSuccess()) {
	        	Log.e(TAG, "result:"+result);
				if(result.equals("true"))
				{
					// 清空手机内LOG
					try
					{
						FileOutputStream testfile = new FileOutputStream("/sdcard/FitincludLog/LOG.txt");
						testfile.write(new String("").getBytes());
						testfile.flush();
						testfile.close();
					}
					catch (IOException e)
					{
//						e.printStackTrace();
						Log.w(TAG, e.getMessage(), e);
					}
				}
	        }
	      }
	      LogcatHelper.getInstance(context).start();
	}

	@Override
	protected DataFromServer doInBackground(Void... arg0)
	{
	    if(FileHelper.isFileExist("/sdcard/FitincludLog/LOG.txt"))
        {
			try {
				String content = new String(FileHelper.readFileWithBytes(new File("/sdcard/FitincludLog/LOG.txt")), "utf-8");
//				Log.e(TAG, "content的内容是:"+content);
				if(!CommonUtils.isStringEmpty(content))
				{
					JSONObject obj_ = new JSONObject();
					obj_.put("user_id", MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id());
					obj_.put("log_content", content);
			        
//					JSONObject obj = new JSONObject();
//					obj.put("processorId", MyProcessorConst.PROCESSOR_LOGIC);
//					obj.put("jobDispatchId", JobDispatchConst.LOGIC_REGISTER);
//					obj.put("actionId", SysActionConst.ACTION_APPEND12);
//					obj.put("newData", obj_.toString());
//					String back = HttpUtils.doPost(MyApplication.SERVER_CONTROLLER_URL_ROOT, obj.toString());
//					Log.e(TAG, "back:"+back);
					
					return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(
							DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_LOGIC)
							.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER)
							.setActionId(SysActionConst.ACTION_APPEND12)
							.setNewData(obj_.toJSONString()));
				}
			} catch (Exception e) {
				Log.w(TAG, e.getMessage(), e);
			}
        }
	    return null;
	}
	
	 public static String txt2String(File file){
		      String result = "";
		          try{
		             BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
		              String s = null;
		              while((s = br.readLine())!=null){//使用readLine方法，一次读一行
		                  result = result + "\n" +s;
		                  Log.e(TAG, "result的内容是:"+result.length());
		              }
		              br.close();    
		          }catch(Exception e){
		              e.printStackTrace();
		          }
		          return result;
		      }
	
	
	
	

}
