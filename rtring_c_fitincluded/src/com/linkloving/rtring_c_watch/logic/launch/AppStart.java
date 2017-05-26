package com.linkloving.rtring_c_watch.logic.launch;

import java.io.File;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.eva.android.BitmapHelper;
import com.eva.android.x.BaseActivity;
import com.eva.epc.common.util.CommonUtils;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.PreferencesToolkits;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.logic.more.HelpActivity;
import com.linkloving.rtring_c_watch.utils.EntHelper;
import com.linkloving.rtring_c_watch.utils.IntentFactory;
import com.rtring.buiness.logic.dto.UserEntity;

/**
 * 应用程序启动类：显示欢迎界面并跳转到主界面.
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class AppStart extends BaseActivity
{
	private final static String TAG = AppStart.class.getSimpleName();
	public static String SHAREDPREFERENCES_NAME = "first_pref";

	
	private ServiceConnection serviceConnection;
	
	private RelativeLayout startLL = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		// FIX: 以下代码是为了解决Android自level 1以来的[安装完成点击“Open”后导致的应用被重复启动]的Bug
		// @see https://code.google.com/p/android/issues/detail?id=52247
		// @see https://code.google.com/p/android/issues/detail?id=2373
		// @see https://code.google.com/p/android/issues/detail?id=26658
		// @see https://github.com/cleverua/android_startup_activity
		// @see http://stackoverflow.com/questions/4341600/how-to-prevent-multiple-instances-of-an-activity-when-it-is-launched-with-differ/
		// @see http://stackoverflow.com/questions/12111943/duplicate-activities-on-the-back-stack-after-initial-installation-of-apk
		// 加了以下代码还得确保Manifast里加上权限申请：“android.permission.GET_TASKS”
		checkBle();
		if (!isTaskRoot()) 
		{// FIX START
		    final Intent intent = getIntent();
		    final String intentAction = intent.getAction();
		    if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) &&
		            intentAction != null && intentAction.equals(Intent.ACTION_MAIN)) {
		        finish();
		    }
		}// FIX END
		
		super.onCreate(savedInstanceState);
		final View view = View.inflate(this, R.layout.start, null);
		setContentView(view);
		startLL = (RelativeLayout) findViewById(R.id.start_LL);
		
		UserEntity user = PreferencesToolkits.getLocalUserInfo(this);
		if(user != null && !CommonUtils.isStringEmpty(user.getEsplash_screen_file_name()))
		{
			//MyApplication.getInstance(this)._const.DIR_ENT_IMAGE_RELATIVE_DIR + "/" + user.getEsplash_screen_file_name()
			File file = new File(EntHelper.getEntFileSavedDir(this)+"/"+ user.getEsplash_screen_file_name());
			if(file.exists())
			{
				try
				{
					startLL.setBackground(BitmapHelper.loadDrawble(file.getAbsolutePath()));
				}
				catch (Exception e)
				{
					Log.w(TAG, e.getMessage(), e);
				}
			}
		}

		// 渐变展示启动屏
		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(2000);
		view.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener()
		{
			@Override
			public void onAnimationEnd(Animation arg0)
			{
				redirectTo();
			}

			@Override
			public void onAnimationRepeat(Animation animation)
			{
			}

			@Override
			public void onAnimationStart(Animation animation)
			{
			}
		});
	}
	
	   private void checkBle() {
	        //判断是否有权限
	        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
	            //请求权限
	            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
	            //判断是否需要 向用户解释，为什么要申请该权限
	            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
	                    Manifest.permission.READ_CONTACTS)) {
	                Toast.makeText(this, "shouldShowRequestPermissionRationale", Toast.LENGTH_SHORT).show();
	            }
	        }
	    }

	/**
	 * 跳转到...
	 */
	private void redirectTo()
	{
		SharedPreferences preferences = this.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
		boolean isFrist = (!preferences.contains("isFirstIn")) || (preferences.getBoolean("isFirstIn", true));
		if (isFrist)
		{
			MyApplication.getInstance(this).setFirstIn(true);
//			Intent intent = new Intent(AppStart.this, HelpActivity.class);
//			intent.putExtra("finish_action", HelpActivity.FininshViewPage_go_tab_host);
			startActivity(IntentFactory.createHelpActivityIntent(AppStart.this
					, HelpActivity.FININSH_VIEWPAGE_GO_TAB_HOST, false));
			finish();
		}
		else
		{
			MyApplication.getInstance(this).setFirstIn(false);
			UserEntity userAuthedInfo = PreferencesToolkits.getLocalUserInfoForLaunch(this);
			// 免登陆
			if(userAuthedInfo != null 
					// 2014-06-24新版本更新前，此值肯定是null，此条件就是强制要求老版本用户登陆一次（之后免登陆自然就ok了）
					// user_type为1表示为第三方登录，是不需要记录用户名的
					&& ((!CommonUtils.isStringEmpty(userAuthedInfo.getUser_type()) && !userAuthedInfo.getUser_type().equals("0")) || PreferencesToolkits.getLoginInfo(this) != null) )
			{
				//** 【注意】当免登陆时，以下代码一定要与正常登陆时成功后的代码保持一致！！！！！！！！！
				//** 【注意】当免登陆时，以下代码一定要与正常登陆时成功后的代码保持一致！！！！！！！！！
				//** 【注意】当免登陆时，以下代码一定要与正常登陆时成功后的代码保持一致！！！！！！！！！
				// 把本地用户信息保存到全局变量备用哦
				MyApplication.getInstance(this).setLocalUserInfoProvider(userAuthedInfo);
				
				
//				Intent intent = new Intent(AppStart.this, PushService.class);
//			    startService(intent);
//			    serviceConnection = new ServiceConnection() {
//					
//					@Override
//					public void onServiceDisconnected(ComponentName name)
//					{
//						
//					}
//					
//					@Override
//					public void onServiceConnected(ComponentName name, IBinder service) 
//					{
//					      try 
//					      {
//					    	  IRemoteService remoteService = IRemoteService.Stub.asInterface(service);
//							  remoteService.login();
//							  finish();
//						  } 
//					      catch (RemoteException e) 
//					      {
//							  Log.e(TAG, e.getMessage());
//						   }
//					}
//				};
//			    //登陆实时推送
//			    bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
				
				startActivity(IntentFactory.createPortalActivityIntent(this));	
			}
			else
			{
				startActivity(IntentFactory.createLoginIntent(this));
				finish();
			}
		}
	}
	
//	protected void onDestroy()
//	{
//		super.onDestroy();
//		this.setContentView(null);
//	}
}