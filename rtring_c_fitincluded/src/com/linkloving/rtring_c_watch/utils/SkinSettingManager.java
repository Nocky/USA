package com.linkloving.rtring_c_watch.utils;

import java.io.File;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.LinearLayout;

import com.eva.android.HttpFileDownloadHelper;
import com.eva.android.widget.DataLoadingAsyncTask;
import com.eva.android.widget.util.WidgetUtils;
import com.eva.android.widget.util.WidgetUtils.ToastType;
import com.eva.epc.common.util.CommonUtils;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
public class SkinSettingManager {
public final static String SKIN_PREF = "skinSetting";

    public final static String NO_SKIN = "";
	public SharedPreferences skinSettingPreference;
	
	
	private int[] skinResources = { R.drawable.wp1};
	
	private Activity mActivity;
    private LinearLayout mlayout;	
    
	public SkinSettingManager(Activity activity,LinearLayout layout) {
		this.mActivity = activity;	
		this.mlayout=layout;
		skinSettingPreference = mActivity.getSharedPreferences(SKIN_PREF, 3);
	}
	public SkinSettingManager(Activity activity) {
		this.mActivity = activity;	
		skinSettingPreference = mActivity.getSharedPreferences(SKIN_PREF, 3);
	}
	
	
	/**
	 * 获取当前程序的皮肤序号
	 * 
	 * @return
	 */
	public int getSkinType() {
		String key = "skin_type";
		return skinSettingPreference.getInt(key, 0);
	}

	/**
	 * 把皮肤序号写到全局设置里去
	 * 
	 * @param j
	 */
	public void setSkinType(int j) {
		SharedPreferences.Editor editor = skinSettingPreference.edit();
		String key  = "skin_type";
		editor.putInt(key, j);
		editor.commit();
	}
	
	/**
	 * 获取当前皮肤的背景图资源id
	 * 
	 * @return
	 */
	public int getCurrentSkinRes() {
		int skinLen = skinResources.length;
		int getSkinLen = getSkinType();
		if(getSkinLen >= skinLen){
			getSkinLen = 0;
		}
		return skinResources[getSkinLen];
	}
	
	/**
	 * 用于导航栏皮肤按钮切换皮肤
	 */
	public void toggleSkins(int skinType){
		setSkinType(skinType);
		mActivity.getWindow().setBackgroundDrawable(null);
		try {
			String url = EntHelper.getEntDownloadURL(mActivity,MyApplication.getInstance(mActivity).getLocalUserInfoProvider().getUser_id(),MyApplication.getInstance(mActivity).getLocalUserInfoProvider().getEbackground_file_name());
			new BackgroundAsyncTask().execute(url);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
		
	/**
	 * 用于初始化皮肤
	 */
	public void initSkins(){	
		if(mlayout==null){
			
			Log.e("SkinSettingManager", "系统默认背景"+(MyApplication.getInstance(mActivity).getLocalUserInfoProvider()==null));
			if(MyApplication.getInstance(mActivity).getLocalUserInfoProvider()==null || MyApplication.getInstance(mActivity).getLocalUserInfoProvider().getEbackground_file_name()==null ||MyApplication.getInstance(mActivity).getLocalUserInfoProvider().getEbackground_file_name().equals(NO_SKIN)){
				Log.e("SkinSettingManager", "系统默认背景");
				mActivity.getWindow().setBackgroundDrawableResource(getCurrentSkinRes());
			}else{
				/*****************************/
				if(MyApplication.getInstance(mActivity).HAS_BG_PIC){
					Bitmap bitmap = getDiskBitmap(EntHelper.getBGFileSavedDir(mActivity) + "/" + MyApplication.getInstance(mActivity).getLocalUserInfoProvider().getEbackground_file_name());
					Drawable drawable =new BitmapDrawable(bitmap);
					mActivity.getWindow().setBackgroundDrawable(drawable);
				/****************************/
				}else{
					String url = EntHelper.getEntDownloadURL(mActivity,MyApplication.getInstance(mActivity).getLocalUserInfoProvider().getUser_id(),MyApplication.getInstance(mActivity).getLocalUserInfoProvider().getEbackground_file_name());
					new BackgroundAsyncTask().execute(url);
				}
				
				
				
			}
		}else{
		    mlayout.setBackgroundResource(getCurrentSkinRes());
		}
	}
	
	
	public class BackgroundAsyncTask extends DataLoadingAsyncTask<String, Integer, Integer>
	{

		public BackgroundAsyncTask()
		{
			super(mActivity,false);
		}

		@Override
		protected Integer doInBackground(String... params)
		{
			return downLoadFile(params[0], MyApplication.getInstance(mActivity).getLocalUserInfoProvider().getEbackground_file_name());
		}

		@SuppressWarnings("resource")
		@Override
		protected void onPostExecuteImpl(Object arg0)
		{
			//该函数返回整形 -1：代表下载文件出错。 0：代表下载文件成功 1：代表下载文件经存在
			if (CommonUtils.getIntValue(arg0) != -1)
			{
				MyApplication.getInstance(mActivity).HAS_BG_PIC = true;
				Bitmap bitmap = getDiskBitmap(EntHelper.getBGFileSavedDir(mActivity) + "/" + MyApplication.getInstance(mActivity).getLocalUserInfoProvider().getEbackground_file_name());
				Drawable drawable =new BitmapDrawable(bitmap);
				mActivity.getWindow().setBackgroundDrawable(drawable);
			}
			else
			{
				WidgetUtils.showToast(mActivity, "File download error！！！", ToastType.ERROR);
			}
			
		}
			
			/*
			 * 该函数返回整形 -1：代表下载文件出错。 0：代表下载文件成功 1：代表下载文件经存在 path =
			 * EntHelper.getEntFileSavedDir(this)+ fileName
			 */
			public int downLoadFile(String urlStr, String fileName)
			{
				File file = new File(EntHelper.getBGFileSavedDir(mActivity) + "/" + MyApplication.getInstance(mActivity).getLocalUserInfoProvider().getEbackground_file_name());
				if (file.exists()){
					return 1;
				}else{
					Object[] ret;
					try {
						ret = HttpFileDownloadHelper.downloadFileEx(urlStr
										// 如果服务端判定需要更新头像到本地缓存时的保存目录
										, EntHelper.getBGFileSavedDir(mActivity), 0, null, true);
						
						if(ret != null && ret.length >=2)
						{
							String savedPath = (String)ret[0];
							int fileLength = (Integer)ret[1];
							
//							Log.i(TAG,"================"  + savedPath + "," + fileLength); 
						}
					} catch (Exception e) {
						e.printStackTrace();
						return -1;
					}
					return 0;
				}
			}
		}

		
	private Bitmap getDiskBitmap(String pathString)  
	{  
	    Bitmap bitmap = null;  
	    try  
	    {  
	        File file = new File(pathString);  
	        if(file.exists())  
	        {  
	            bitmap = BitmapFactory.decodeFile(pathString);  
	        }  
	    } catch (Exception e)  
	    {  
	       
	    }  
	      
	      
	    return bitmap;  
	}  
	
	
	
}
