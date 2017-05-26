package com.linkloving.rtring_c_watch.logic.more.avatar;

import java.util.HashMap;

import android.util.Log;

import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.eva.epc.core.dto.SysActionConst;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AImpl;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.JobDispatchConst;

/**
 * 2014-01-12日启用的用户头像上传新实用类.
 * <p>
 * 原 {@link HttpUploadHelper2}因Http上传时有一定几率莫名上传失败，因而
 * 改用本类中的方法实现，本类中的方法是将文件数据一次性读取到内存中，此方法
 * 经Jack Jiang多年使用经验证是可靠的，但它的缺点是：要1次性将数据读到
 * 内存里使得APp的内存使用峰值上升、另外它也受限于HTTP一次上传数据过大的限制
 * （只能上传16M以内的数据）哦.
 * 
 * @author Jack Jiang, 2014-01-12
 * @since 2.1
 */
public class AvatarDataUploadHttpHelper
{
	private static String TAG = AvatarDataUploadHttpHelper.class.getSimpleName();
	
	/**
	 * 更新用户头像实现方法.
	 * <p>
	 * 本方法自KChat2.1启用.
	 * 
	 * @param filePath
	 * @param fileName
	 * @param requestProperties
	 * @since 2.1
	 */
	public static boolean uploadAvatarFile(String fileName
			, String localUserUid, byte[] fileData)
	{
		boolean sucess = false;
		try
		{
			HashMap requestProperties = new HashMap();
			
			// 此参数名注意要与服务端保持一致哦
			requestProperties.put("user_uid", localUserUid);
			requestProperties.put("file_name", fileName);   // 因为服务端是支持多文件上传的API，所以此处单独把文件名带过去，方便使用！
			requestProperties.put("file_data", fileData);
			
			DataFromServer dfs = HttpServiceFactory4AImpl.getInstance().getDefaultService().sendObjToServer(
					DataFromClient.n()
					.setProcessorId(MyProcessorConst.PROCESSOR_AVATAR_UPLOAD)
					.setJobDispatchId(JobDispatchConst.AVATAR_UPLOAD)
					.setActionId(SysActionConst.ACTION_APPEND1)
					.setNewData(requestProperties));
			if(dfs != null)
			{
				if(dfs.isSuccess())
					sucess = true;
				else
				{
					if(dfs.getReturnValue() instanceof Exception)
					{
						Exception eFromServer = (Exception)dfs.getReturnValue();
						Log.e(TAG, "上传头像时服务端报错了："+eFromServer.getMessage(), eFromServer);
					}
					else
						Log.e(TAG, "上传头像时服务端报错了："+dfs.getReturnValue());
				}
			}
		}
		catch (Exception e)
		{
			Log.e(TAG, "上传头像到服务端时失败了："+e.getMessage(), e);
		}
		
		return sucess;
	}

}
