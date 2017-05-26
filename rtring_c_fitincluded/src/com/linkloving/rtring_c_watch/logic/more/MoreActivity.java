package com.linkloving.rtring_c_watch.logic.more;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eva.android.platf.std.AutoUpdateDaemon;
import com.eva.android.platf.std.dto.AutoUpdateInfoFromServer;
import com.eva.android.widget.DataLoadingAsyncTask;
import com.eva.android.widget.util.WidgetUtils;
import com.eva.android.widget.util.WidgetUtils.ToastType;
import com.eva.android.widgetx.AlertDialog;
import com.eva.epc.common.util.CommonUtils;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.eva.epc.core.dto.SysActionConst;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.linkloving.band.dto.SportRecord;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.PreferencesToolkits;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.db.logic.UserDeviceRecord;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.linkloving.rtring_c_watch.logic.DataLoadableMultipleAcitvity;
import com.linkloving.rtring_c_watch.logic.launch.LoginActivity;
import com.linkloving.rtring_c_watch.logic.more.avatar.AvatarHelper;
import com.linkloving.rtring_c_watch.logic.more.avatar.ShowUserAvatar;
import com.linkloving.rtring_c_watch.logic.sns.SearchActivity;
import com.linkloving.rtring_c_watch.utils.HttpCloudSyncHelper;
import com.linkloving.rtring_c_watch.utils.IntentFactory;
import com.linkloving.rtring_c_watch.utils.LanguageHelper;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.JobDispatchConst;
import com.rtring.buiness.logic.dto.UserEntity;

/**
 *  导航“更多”对应的activity实现页面.
 * 
 * @author js, 2012-08-09
 * @version 1.0
 * @since 2.1
 */
public class MoreActivity extends DataLoadableMultipleAcitvity
{
	public static final int REQUEST_CODE_FOR_GO_TO_USER$ACTIVITY = 1; 
	
	public static final String REQ_CLOULD_SYNC = "req_clould_sync";
	
	public static final int OPEN_PRIVACY = -1;
	public static final int CLOSE_PRIVACY = 1;
	
	private ViewGroup currentUserBtn;
	private Button aboutUsBtn = null;
//	private Button rateUsBtn = null;
	private Button exitBtn = null;
	private Button helpBtn = null;
	private Button versionBtn = null;
	private Button entCustomerBtn = null;
	private Button faqBtn = null;
	private LinearLayout syncBtn = null;
	private Button main_more_theme = null;
	private Button main_more_privacy = null;
	
	private LinearLayout entGroupView = null;
	private Button aboutGroupBtn = null;
	private Button exitGroupBtn = null;
	
	private TextView syncTxt = null;
	
	private ViewGroup entCustomerLL = null;
	
	private ImageView genderView = null;
	
	private ImageView viewlocalUserAvatar = null;
	
	// 当此值为true时表示本Activity中已尝试过从服务端更新头像，否由表示未尝试过
	// 此值的目的是控制头像从服务端的更新仅在本Activity的生命周期中执行1次以结省服务端性能和压力，仅此而已
	private boolean tryGetAvatarFromServer = false;
	private ShowUserAvatar showUserAvatarWrapper= null;
	
	
//	private Dialog progressDialog;
	PopupWindow popup;
	ProgessWidget progessWidget;
	
	private int pageIndex = 1;
	private int count;
	
	private UserEntity user = null;
	
//	public static final int REQUEST_CODE_ACTIVITY_ID_VIDEOCONFIG = 1;		// 视频配置界面标识
	//android2.0前需要重写onKeyDown方法才能实现，2.0及以后直接重写onBackPressed即可哦
	@Override
	public void onBackPressed()
	{
		this.finish();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//		initListeners();
//		refreshDatas();
		
		// 现在只在onCreate时刷新本地用户头像：可能更新不太及时，但总比要onResume里对服务端的性能压力要小吧
		user = MyApplication.getInstance(this).getLocalUserInfoProvider();
		if(user != null)
		{
			// 更新本地用户头像
			showUserAvatarWrapper = new ShowUserAvatar(this, user.getUser_id(), viewlocalUserAvatar, true
					, 120 , 120 // 此头像用于界面中好友的头像显示，大小参考：main_more布局中的@+id/main_more_settings_avatarView（60dp*60dp）
					){
				@Override
				protected void avatarUpdateForDownload(Bitmap cachedAvatar)
				{
					super.avatarUpdateForDownload(cachedAvatar);
					tryGetAvatarFromServer = true;
				}
			};
		}
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		
		//
		this.refreshDatas();
		this.refreshView();
	}
	
	protected void initViews()
	{
		//开启按back键相当于按Home键的效果，目的是使得在首页这样的地方
		//按back键程序回到回台运行，而不是默认的finish掉进而关闭整个进程
		//，提高用户体验，省的总是要重新登陆
		this.goHomeOnBackPressed = true;
		//设定自定义标题栏（设定此值即意味着开启自定义标题栏的使用，必须要在setContentView前设定）
		customeTitleBarResId = R.id.main_more_titleBar;
		//养成良好习惯：首先设置主layout，确保后绪的操作中使用到的组件都可以被find到
		setContentView(R.layout.main_more);
		//对自定义标题栏中的组件进行设定
//		this.getCustomeTitleBar().getLeftBackButton().setVisibility(View.GONE);
		
		//设置标题（自定义标题栏后的title文本设置是不同的哦，见CustomeTitleBar中的说明）
		this.setTitle($$(R.string.portal_activity_more));
		
		entGroupView = (LinearLayout) findViewById(R.id.main_more_settings_entCustomerLL);
		aboutGroupBtn = (Button)this.findViewById(R.id.main_more_settings_aboutEntCustomerBtn);
		exitGroupBtn = (Button)this.findViewById(R.id.main_more_settings_exitEntCustomerBtn);
		main_more_privacy = (Button)this.findViewById(R.id.main_more_settings_privacy);
//		main_more_privacystate = (TextView) this.findViewById(R.id.main_more_settings_privacystate);
		
		refreshView();
		
		syncBtn = (LinearLayout) findViewById(R.id.main_more_settings_cloud_sync_linear);
		syncTxt = (TextView) findViewById(R.id.main_more_settings_cloud_sync_text);
		String time = PreferencesToolkits.getLastCloudSyncTime(this);
		if(time!= null && !time.isEmpty())
		{
			syncTxt.setVisibility(View.VISIBLE);
			syncTxt.setText(time);
		}
		currentUserBtn = (ViewGroup)findViewById(R.id.main_more_settings_currentUserBtn);
		exitBtn = (Button)this.findViewById(R.id.main_more_settings_exitSystemBtn);
		aboutUsBtn = (Button)this.findViewById(R.id.main_more_settings_aboutusBtn);
		helpBtn = (Button)this.findViewById(R.id.main_more_settings_helpBtn);
		viewlocalUserAvatar = (ImageView)this.findViewById(R.id.main_more_settings_avatarView);
		versionBtn = (Button)this.findViewById(R.id.main_more_settings_currentVersionBtn);
		genderView = (ImageView) findViewById(R.id.main_more_settings_gender);
		faqBtn = (Button)this.findViewById(R.id.main_more_settings_faqBtn);
//		entCustomerBtn = (Button)this.findViewById(R.id.main_more_settings_entCustomerBtn);
//		entCustomerLL = (ViewGroup)findViewById(R.id.main_more_settings_entCustomerLL);
		popup = createPopupWindow(MoreActivity.this,R.layout.popup_progess_view,ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT, true);
		
		progessWidget = new ProgessWidget(popup,findViewById(R.id.main_more_linear)); 
	//	progressDialog = createProgessDialog(this);
		
		main_more_theme = (Button) findViewById(R.id.main_more_theme);
		main_more_theme.setVisibility(View.GONE);
	}
	
	private void refreshView()
	{
		user = MyApplication.getInstance(this).getLocalUserInfoProvider();
		
		
		if(CommonUtils.isStringEmpty(user.getEid()) || user.getEid().equals("9999999999"))
			entGroupView.setVisibility(View.GONE);
		else
		{
			entGroupView.setVisibility(View.VISIBLE);
			aboutGroupBtn.setText(MessageFormat.format(getString(R.string.main_more_about_ent), user.getEname()));
			exitGroupBtn.setText(MessageFormat.format(getString(R.string.main_more_exit_ent), user.getEname()));
		}
	}
	
	protected void initListeners()
	{
		
		
		//TEST:OAD按钮点击
		main_more_theme.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(IntentFactory.createSkinActivityIntent(MoreActivity.this));
//				Toast.makeText(MoreActivity.this, "wait……", Toast.LENGTH_SHORT).show();
			}
		});
		
		// 点击头像看大图
		viewlocalUserAvatar.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				// 本地用户信息
				UserEntity u = MyApplication.getInstance(MoreActivity.this).getLocalUserInfoProvider();
				if(u != null)
					AvatarHelper.showAvatarImage(MoreActivity.this, u.getUser_id(), null);
			}
		});
//		((Button)this.findViewById(R.id.main_more_settings_cacheBtn))
//				.setOnClickListener(new OnClickListener(){
//					@Override
//					public void onClick(View v)
//					{
////						startActivity(net.dmkx.mobi1.db2.util.IntentFactory
////								.createGoToUpDateTableIntent(MoreActivity.this));
//					}
//		});
		currentUserBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				Intent intent = new Intent(MoreActivity.this, UserActivity.class);
				startActivityForResult(intent, REQUEST_CODE_FOR_GO_TO_USER$ACTIVITY);
			}
		});
		
		aboutGroupBtn.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				if(!CommonUtils.isStringEmpty(user.getEportal_url()))
					startActivity(IntentFactory.createCommonWebActivityIntent(MoreActivity.this, user.getEportal_url()));
			}
		});
		
		exitGroupBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				
				new AlertDialog.Builder(MoreActivity.this)
				.setTitle(getString(R.string.general_tip))
				.setMessage(MessageFormat.format(getString(R.string.main_more_exit_ent_message), user.getEname()))
				.setPositiveButton(getString(R.string.general_yes), new DialogInterface.OnClickListener() 
				{
					@Override
					public void onClick(DialogInterface dialog,int which) 
					{
						new ExitGroupAsyncTask().execute();
					}
				})
				.setNegativeButton(getString(R.string.general_cancel), null)
				.show();
			}
		});
		
		exitBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				LoginActivity.doExit(MoreActivity.this);
			}
		});
		
		aboutUsBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				startActivity(new Intent(MoreActivity.this, AboutActivity.class));
			}
		});
		
		faqBtn.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				startActivity(IntentFactory.createCommonWebActivityIntent(MoreActivity.this, LanguageHelper.isChinese_SimplifiedChinese()? MyApplication.FAQ_CN_URL : MyApplication.FAQ_EN_URL));
			}
		});
		
		
		
		helpBtn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
//				Intent intent = new Intent(MoreActivity.this, HelpActivity.class);
//				intent.putExtra("finish_action", HelpActivity.FininshViewPage_finishActivity);
//				intent.putExtra("isJiaocheng", true);
//				startActivity(intent);
				startActivity(IntentFactory.createHelpActivityIntent(MoreActivity.this
						, HelpActivity.FININSH_VIEWPAGE_FINISHACTIVITY, true));
			}
		});
		
		
		
		syncBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				new com.eva.android.widgetx.AlertDialog.Builder(MoreActivity.this)
				.setTitle($$(R.string.main_more_sycn_title))
				.setMessage($$(R.string.main_more_sycn_message))
				.setPositiveButton($$(R.string.general_yes),  new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog,int which)
					{
						progessWidget.show();
						progessWidget.startSyncing();
						loadData(false,HttpCloudSyncHelper.GenerateCloudSyncParams(MoreActivity.this,pageIndex),REQ_CLOULD_SYNC);
						//progressDialog.show();
					}
				}) 
				.setNegativeButton($$(R.string.general_no), null)
				.show(); 
				
			}
		});
		
		main_more_privacy.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				startActivity(new Intent(MoreActivity.this,PrivacySetActivity.class));
//				user = MyApplication.getInstance(MoreActivity.this).getLocalUserInfoProvider();
//				if(main_more_privacystate.getText().equals("ON")){
//					main_more_privacystate.setText("OFF");
//					user.setUser_status(CLOSE_PRIVACY+"");
//					
//				}
//				else if(main_more_privacystate.getText().equals("OFF"))
//				{
//					main_more_privacystate.setText("ON");
//					user.setUser_status(OPEN_PRIVACY+"");
//					
//				}
//				MyApplication.getInstance(MoreActivity.this).setLocalUserInfoProvider(user);
//				
//				if(ToolKits.isNetworkConnected(MoreActivity.this))
//				{
//					new DataLoadingAsyncTask<String, Integer, DataFromServer>(MoreActivity.this, null){
//						@Override
//									protected DataFromServer doInBackground(String... params) {
//										JSONObject obj = new JSONObject();
//										obj.put("user_id", user.getUser_id());
//										obj.put("user_status", user.getUser_status());
//										return HttpServiceFactory4AJASONImpl.getInstance()
//												.getDefaultService()
//												.sendObjToServer(DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_SNS_LOGIC)
//																.setJobDispatchId(JobDispatchConst.SNS_BASE)
//																.setActionId(SysActionConst.ACTION_MULTI_DEL)
//																.setNewData(obj.toJSONString()));
//									}
//									
//									@Override
//									protected void onPostExecuteImpl(Object result) {
//										Log.e("DataLoadingAsyncTask", "result:"+result);
//										if(Boolean.parseBoolean((String) result)){
//											if(user.getUser_status().equals(CLOSE_PRIVACY+""))
//												new com.eva.android.widgetx.AlertDialog.Builder(MoreActivity.this)
//												.setTitle(R.string.general_reminder)  
//												.setMessage(R.string.main_more_privacy_messiage_off)
//												.setPositiveButton(R.string.general_ok,  null)  
//												.show();
//											else if(user.getUser_status().equals(OPEN_PRIVACY+"")){
//												new com.eva.android.widgetx.AlertDialog.Builder(MoreActivity.this)
//												.setTitle(R.string.general_reminder)  
//												.setMessage(R.string.main_more_privacy_messiage_on)
//												.setPositiveButton(R.string.general_ok,  null)  
//												.show();
//											}
//										}
//									}
//					
//				
//					}.execute();
//				}else{  //general_network_faild
//					new com.eva.android.widgetx.AlertDialog.Builder(MoreActivity.this)
//					.setTitle(R.string.general_reminder)  
//					.setMessage(R.string.general_network_faild)
//					.setPositiveButton(R.string.general_ok,  null)  
//					.show();
//				}
				
			
			}
		});
		
//		// 版本检查
//		versionBtn.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v)
//			{
//				new VersionCheckAsyncTask().execute();
//			}
//		});
	}
	
	private void refreshDatas()
	{
		//把用户名设置到”当前登陆用户“组件上，以便查看
		final UserEntity u = MyApplication.getInstance(this).getLocalUserInfoProvider();
		if(u != null)
		{
			((TextView)this.findViewById(R.id.main_more_settings_currentUserInfoView))
				.setText(u.getNickname()//+"("+u.getUser_id()+")"
						);
			// 如果是QQ登陆就不显示邮件地址了（也没有邮件地址）
			if("1".equals(u.getUser_type()))
				((TextView)this.findViewById(R.id.main_more_settings_mailView))
					.setText($$(R.string.main_more_from_qq_login));
			else
				((TextView)this.findViewById(R.id.main_more_settings_mailView))
					.setText(u.getUser_mail());
			
			genderView.setBackground(u.getUser_sex().equals("1") ? getResources().getDrawable(R.drawable.gender_man) : getResources().getDrawable(R.drawable.gender_woman));
		}
		
		((TextView)this.findViewById(R.id.main_more_settings_currentVersionInfoView)).setText(
				LoginActivity.getAPKVersionName(this)+"("+LoginActivity.getAPKVersionCode(this)+")");
		
		// 有onResume方法中刷新用户头像的目的是保持本地头像的及时刷新
		if(showUserAvatarWrapper != null)
		{
			if(!tryGetAvatarFromServer)
				showUserAvatarWrapper.setNeedTryGerAvatarFromServer(true);
			else
				showUserAvatarWrapper.setNeedTryGerAvatarFromServer(false);
			showUserAvatarWrapper.showCahedAvatar();
		}
		
//		if(u != null && !CommonUtils.isStringEmpty(u.getEname()))
//		{
//			entCustomerLL.setVisibility(View.VISIBLE);
//			entCustomerBtn.setText($$(R.string.general_about)+u.getEname());
//			entCustomerBtn.setOnClickListener(new OnClickListener()
//			{
//				@Override
//				public void onClick(View v)
//				{
//					if(!CommonUtils.isStringEmpty(u.getEportal_url()))
//						AboutActivity.startWebAcvitity(MoreActivity.this, u.getEportal_url());
//				}
//			});
//		}
//		else
//		{
//			entCustomerLL.setVisibility(View.GONE);
//		}
	}
	
	//----------------------------------------------------------------------------------------------------------
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if(resultCode != Activity.RESULT_OK)
		{
			//result is not correct
			return;
		}
		
		// 从个人信息界面返回的，那么就尝试刷新他的头像吧（因为在这个界面里可能修改了用户的头像哦）
		if(requestCode == REQUEST_CODE_FOR_GO_TO_USER$ACTIVITY)
		{
			UserEntity u = MyApplication.getInstance(this).getLocalUserInfoProvider();
			if(u != null)
			{
				// 更新本地用户头像
				new ShowUserAvatar(this, u.getUser_id(), viewlocalUserAvatar
						// 注意：此时就没有必要尝试从服务端更新头像了，因为在用户个人信
						// 息界面里如果要更新那么就已经存了一个新缓存到本地了
						, false 
						, 120 , 120 // 此头像用于界面中好友的头像显示，大小参考：main_more布局中的@+id/main_more_settings_avatarView（60dp*60dp）
				).showCahedAvatar();
			}
		}
		
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	//----------------------------------
	/**
	 * 版本检查线程实现类.
	 */
	protected class VersionCheckAsyncTask extends DataLoadingAsyncTask<Object, Integer, DataFromServer>
	{
		public VersionCheckAsyncTask()
		{
			super(MoreActivity.this, $$(R.string.general_loading));
		}
		
		/**
		 * 在后台执行 {@link doLogin()}实现登陆信息的提交和处于结果的读取 .
		 * 
		 * @param parems 外界传进来的参数
		 * @return 查询结果，将传递给onPostExecute(..)方法 
		 */
		@Override
		protected DataFromServer doInBackground(Object... parems) 
		{
			return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(
					DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_CLIENT_UPDATE_VERSION)
								.setNewData(""+LoginActivity.getAPKVersionCode(MoreActivity.this))
								);
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
			// 返回的客户端版本更新信息：表示需要首先更新系统
			if(result instanceof AutoUpdateInfoFromServer)
			{
				final AutoUpdateInfoFromServer aui = (AutoUpdateInfoFromServer)result;
				if(aui.isNeedUpdate())
				{
					Log.d("MoreActivity", "isNeedUpdate?"+aui.isNeedUpdate()
							+",getLatestClientAPKVercionCode="+aui.getLatestClientAPKVercionCode()
							+",getLatestClientAPKFileSize="+aui.getLatestClientAPKFileSize()
							+",getLatestClientAPKURL"+aui.getLatestClientAPKURL());
					
					new com.eva.android.widgetx.AlertDialog.Builder(MoreActivity.this)
						.setTitle($$(R.string.login_form_have_latest_version))  
						.setMessage($$(R.string.login_form_have_latest_version_descrption))
						.setPositiveButton($$(R.string.login_form_have_latest_version_update_now),  new DialogInterface.OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								//进入版本更新处理类进行更新处理
								AutoUpdateDaemon up=new AutoUpdateDaemon(MoreActivity.this
										, aui.getLatestClientAPKVercionCode()
										, aui.getLatestClientAPKFileSize()
										, aui.getLatestClientAPKURL());
								try{
									up.doUpdate();
								}
								catch (Exception e){
									WidgetUtils.showToast(MoreActivity.this, $$(R.string.login_form_version_error), ToastType.ERROR);
									Log.d("MoreActivity", "新版检查和下载时遇到错误，"+e.getMessage(), e);
								}
							}
						})  
						.setNegativeButton($$(R.string.login_form_have_latest_version_ignore),  null)
						.show(); 

//					WidgetUtils.showToast(LoginActivity.this, $$(R.string.login_form_begin_update_version));
				}
				// 无需更新
				else
				{
//					// 更新检查（处理）完成后开始进入真正的登陆线程
//					new LoginImplAsyncTask().execute();
					new com.eva.android.widgetx.AlertDialog.Builder(MoreActivity.this)
						.setTitle(R.string.general_prompt)  
						.setMessage(R.string.main_more_version_check_is_latest)
						.setPositiveButton(R.string.general_ok,  null)  
					.show(); 
				}

			}
		}
	}
	
	@Override
	protected void refreshToView(String taskName,Object taskObj, Object paramObject)
	{
		if(taskName.equals(REQ_CLOULD_SYNC))
		{
			if(paramObject != null)
			{
				Map<String, String> result = new Gson().fromJson((String)paramObject, new TypeToken<HashMap<String, String>>(){}.getType());
				String datas = result.get("datas");
				count = Integer.parseInt(result.get("datas_total_count").isEmpty()?"0":result.get("datas_total_count"));
				if(datas != null)
				{
					List<SportRecord> srs = JSON.parseArray((String)datas, SportRecord.class);
//					List<SportRecord> srs = DecodeData.decode(datas);
					if(srs.size() > 0)
					{
						UserDeviceRecord.saveToSqliteAsync(MoreActivity.this, srs, MyApplication.getInstance(MoreActivity.this).getLocalUserInfoProvider().getUser_id(), true, null);
						updateProgess(srs.size(),count,progessWidget);
					    pageIndex++;
					    loadData(false, HttpCloudSyncHelper.GenerateCloudSyncParams(MoreActivity.this, pageIndex), taskName);
					}
					else
					{
						updateProgess(srs.size(),count,progessWidget);
						progessWidget.syncFinish();
						
						//ToolKits.showCommonTosat(MoreActivity.this, true, $$(R.string.general_clouding_success), Toast.LENGTH_SHORT);
						//pageIndex = 1;
					}
				}
			
			}
			else
			{
			    progessWidget.syncFinish();
				//pageIndex = 1;
			}
		}
		
	}
	
	private int progess;
	private void updateProgess(int size,int max,ProgessWidget progessWidget)
	{
		if(max <= 0)
		{
			progessWidget.getProgressBar().setMax(1);
			progessWidget.getProgressBar().setProgress(1);
			progessWidget.setMessage(progess, max);
			return;
		}
		progess += size;
		progessWidget.getProgressBar().setMax(max);
		progessWidget.getProgressBar().setProgress(progess);
		progessWidget.setMessage(progess, max);
	}
	
	private PopupWindow createPopupWindow(Context context,int layoutID,int width,int height,boolean focus)
	{
		View contentView = View.inflate(context, layoutID, null);
		PopupWindow pop = new PopupWindow(contentView, width, height,focus);
		pop.setBackgroundDrawable(new ColorDrawable(0x55000000));
		pop.setOutsideTouchable(false);
		pop.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() 
			{
				removeAllAsyncTask();
			}
		});
		return pop;
	}
	
	public class ProgessWidget
	{
		private View contentView;
		private ImageView syncImage;
		private ImageView syncEndImage;
		private TextView title;
		private ProgressBar bar;
		private TextView msg;
		private Button btn;
		private PopupWindow popWindow;
		
		private View rootView;
		
		public ProgessWidget(final PopupWindow pop,View parent) 
		{
			 rootView = parent;
			 contentView = pop.getContentView();
			 popWindow = pop;
			 syncImage = (ImageView) contentView.findViewById(R.id.syncing_image);
			 syncEndImage = (ImageView) contentView.findViewById(R.id.sync_success_image);
			 title = (TextView) contentView.findViewById(R.id.title_textView);
			 bar = (ProgressBar) contentView.findViewById(R.id.progressBar1);
			 msg = (TextView) contentView.findViewById(R.id.textView1);
			 Object[] args = {"-","-","-"}; 
			 String format = MessageFormat.format(getString(R.string.general_dialog_sync_text), args);
			 msg.setText(format);
			 btn = (Button) contentView.findViewById(R.id.button1);
			 btn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0)
				{
					//清除断点状态
					progess = 0;
					clearMessage();
					pageIndex = 1;
					bar.setProgress(0);
				    pop.dismiss();	
				}
			});
		}
		
		public void setTitle(int resid)
		{
			title.setText(resid);
		}
		
		public ProgressBar getProgressBar()
		{
			return bar;
		}
		
		public void startSyncing()
		{
			syncImage.setVisibility(View.VISIBLE);
			syncEndImage.setVisibility(View.GONE);
			setTitle(R.string.general_sycing);
			btn.setVisibility(View.GONE);
		}
		
		public void syncFinish()
		{
			syncImage.setVisibility(View.GONE);
			syncEndImage.setVisibility(View.VISIBLE);
			setTitle(R.string.general_sync_end);
			btn.setVisibility(View.VISIBLE);
			Object[] args = {new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).format(new Date()),count+""};
			String format = MessageFormat.format(getString(R.string.general_sync_time), args);
			PreferencesToolkits.saveLastCloudSyncTime(MoreActivity.this, format);
			syncTxt.setVisibility(View.VISIBLE);
			syncTxt.setText(format);
		}
		
		public void clearMessage()
		{
			Object[] args = {"-","-","-"};
			String format = MessageFormat.format(getString(R.string.general_dialog_sync_text), args);
			msg.setText(format);
		}
		
		public void setMessage(int progess,int max)
		{
			if(max <= 0)
			{
				int percent = 100;
				Object[] args = {percent,max,progess};
				String format = MessageFormat.format(getString(R.string.general_dialog_sync_text), args);
				msg.setText(format);
				return;
			}
			
			int percent = progess*100/max > 100 ? 100:progess*100/max;
			Object[] args = {percent,max,progess};
			String format = MessageFormat.format(getString(R.string.general_dialog_sync_text), args);
			msg.setText(format);
		}
		
		public void show()
		{
			popWindow.showAtLocation(rootView, Gravity.CENTER,0, 0);
		}
		
		public void dismiss()
		{
			popWindow.dismiss();
		}
		
	}
	
	protected class ExitGroupAsyncTask extends DataLoadingAsyncTask<String, Integer, DataFromServer>
	{
		public ExitGroupAsyncTask()
		{
			super(MoreActivity.this, $$(R.string.general_submitting));
		}
		
		/**
		 * 在后台执行 {@link doLogin()}实现登陆信息的提交和处于结果的读取 .
		 * 
		 * @param parems
		 *            外界传进来的参数
		 * @return 查询结果，将传递给onPostExecute(..)方法
		 */
		@Override
		protected DataFromServer doInBackground(String... params)
		{
			JSONObject obj = new JSONObject();
			obj.put("user_id", user.getUser_id());
			
			return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(
					DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_SNS_LOGIC)
	                .setJobDispatchId(JobDispatchConst.SNS_BASE)
	                .setActionId(SysActionConst.ACTION_MULTI_ADD)
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
			if(result != null)
			{   
				MyApplication.getInstance(MoreActivity.this).HAS_BG_PIC = false;
				ToolKits.showCommonTosat(MoreActivity.this, true, getString(R.string.main_more_exit_ent_success), Toast.LENGTH_SHORT);
				UserEntity user = JSON.parseObject((String)result, UserEntity.class);
				MyApplication.getInstance(MoreActivity.this).setLocalUserInfoProvider(user);
				
				broadcastUpdate(MyApplication.BLE_STATE_SUCCESS, MoreActivity.this);
			}
			else
				ToolKits.showCommonTosat(MoreActivity.this, true, getString(R.string.main_more_exit_ent_faliure), Toast.LENGTH_SHORT);
			
			refreshView();
		}
	}
	
	private void broadcastUpdate(final String action, Context context)
	{
		final Intent intent = new Intent(action);
		context.sendBroadcast(intent);
	}

}
