package com.linkloving.rtring_c_watch;

import java.lang.Thread.UncaughtExceptionHandler;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import cn.sharesdk.framework.ShareSDK;

import com.baidu.mapapi.SDKInitializer;
import com.eva.android.ApplicationRoot;
import com.eva.android.HelloR;
import com.eva.android.RHolder;
import com.eva.android.widget.util.WidgetUtils;
import com.eva.epc.common.util.CommonUtils;
import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.exception.BLException;
import com.example.android.bluetoothlegatt.proltrol.LepaoProtocalImpl;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.example.android.bluetoothlegatt.proltrol.dto.LPSportData;
import com.example.android.bluetoothlegatt.utils.LogX;
import com.example.android.bluetoothlegatt.wapper.BLEWapper;
import com.linkloving.band.dto.SportRecord;
import com.linkloving.rtring_c_watch.db.logic.UserDeviceRecord;
import com.linkloving.rtring_c_watch.logic.launch.LoginActivity;
import com.linkloving.rtring_c_watch.logic.model.LocalInfoVO;
import com.linkloving.rtring_c_watch.utils.BleErrSubmitServerAsyncTask;
import com.linkloving.rtring_c_watch.utils.DeviceInfoHelper;
import com.linkloving.rtring_c_watch.utils.HttpHelper;
import com.linkloving.rtring_c_watch.utils.OsUtils;
import com.linkloving.rtring_c_watch.utils.SportDataHelper;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.rtring.buiness.logic.dto.UserEntity;

/**
 * <p>
 * 根据Android程序设计最佳实践技巧，本系统中的所有全局变量应存放于本类中.
 * @author Jack Jiang, 2011-12-03
 * @version 1.0
 */
public class MyApplication extends ApplicationRoot
{
	
	private final static String TAG = MyApplication.class.getSimpleName();
	
	//**************广播**************//
	public static final String BLE_STATE_SUCCESS = "com.ble.state";
	public static final String BLE_SYN_SUCCESS = "com.ble.connectsuc";
	public static final String BLE_CONNECT_LOST = "com.ble.connectlost";
	
	//<-------test.OVER--------->
	private static MyApplication self = null;
	
	/** 用前用于记灵SNS中的未读数的全局变量 */
	private int currentTotalCommentNum;
	
	public final static String SERVICE_WATCH = "com.linkloving.rtring_c_watch";
	public final static String SERVICE_REMOTE = "com.linkloving.rtring_c_watch:remote";
	public final static String SERVICE_umengService_v1 = "com.linkloving.rtring_c_watch:umengService_v1";
	public static final int REQUEST_ENABLE_BT = 0x10;
	
	// ** APP的服务器根地址 
	public final static String APP_ROOT_URL =
			"admin.fitincluded.com:8080";
//			"192.99.103.43:8080";
//			"app.efeelink.com:9080";
//			"54.199.134.150:9080";
//			"115.29.110.195:9080";
//			"192.168.66.226:8080"; 
//			"192.168.82.104:8080"; 
	// ** 普通数据服务端的根URL 
	public final static String SERVER_CONTROLLER_URL_ROOT = 
		//真机调试用的是手机自已的3G网络所以要用外网
//		"http://"+APP_ROOT_URL+":8080/rtring_s_new_t/";    // 发布地址  // TODO
//		"http://"+APP_ROOT_URL+"/rtring_s_new_t/";   // 发布地址  // TODO
		"http://"+APP_ROOT_URL+"/rtring_s_new-watch-fitincluded/";   // 发布地址
	 
	// ** 用户头像上传Servlet地址
	public final static String AVATAR_UPLOAD_CONTROLLER_URL_ROOT =
//			"http://"+APP_ROOT_URL+":8080/rtring_s_new_t/UserAvatarUploadController";  // TODO
//			"http://"+APP_ROOT_URL+"/rtring_s_new_t/UserAvatarUploadController";  // TODO
			"http://"+APP_ROOT_URL+"/rtring_s_new-watch-fitincluded/UserAvatarUploadController";
	
	// ** 用户头像下载Servlet地址
	public final static String AVATAR_DOWNLOAD_CONTROLLER_URL_ROOT =
//			"http://"+APP_ROOT_URL+":8080/rtring_s_new_t/UserAvatarDownloadController";  // TODO
//			"http://"+APP_ROOT_URL+"/rtring_s_new_t/UserAvatarDownloadController";  // TODO
			"http://"+APP_ROOT_URL+"/rtring_s_new-watch-fitincluded/UserAvatarDownloadController";
	
	// ** 企业用户下载Servlet地址
	public final static String ENT_DOWNLOAD_CONTROLLER_URL_ROOT =
//			"http://"+APP_ROOT_URL+":8080/rtring_s_new_t/UserAvatarDownloadController";  // TODO
//			"http://"+APP_ROOT_URL+"/rtring_s_new_t/BinaryDownloadController";  // TODO
			"http://"+APP_ROOT_URL+"/rtring_s_new-watch-fitincluded/BinaryDownloadController";
	
	//** 服务条款
	public final static String REGISTER_AGREEMENT_EN_URL = 
//			"http://" + APP_ROOT_URL + ":8080/rtring_s_new_t/clause/agreement.html";  // TODO
//			"http://" + APP_ROOT_URL + "/rtring_s_new_t/clause/agreement.html";  // TODO
			"http://fitincluded.com/reflex-terms.php";
	//** 服务条款
	public final static String REGISTER_AGREEMENT_CN_URL = 
//			"http://" + APP_ROOT_URL + ":8080/rtring_s_new_t/clause/agreement_cn.html";
//			"http://" + APP_ROOT_URL + "/rtring_s_new_t/clause/agreement_cn.html";
			"http://fitincluded.com/reflex-terms.php";
	
	//** FAQ
	public final static String FAQ_EN_URL = 
//			"http://" + APP_ROOT_URL + ":8080/rtring_s_new_t/clause/agreement.html";
//			"http://" + APP_ROOT_URL + "/rtring_s_new_t/clause/qna.html";
			"http://fitincluded.com/reflex-faq.php";
	//** FAQ
	public final static String FAQ_CN_URL = 
//			"http://" + APP_ROOT_URL + ":8080/rtring_s_new_t/clause/agreement_cn.html";
//			"http://" + APP_ROOT_URL + "/rtring_s_new_t/clause/qna_cn.html";
			"http://fitincluded.com/reflex-faq.php";
	
	//** 隐私申明
	public final static String PRIVACY_EN_URL = 
//			"http://" + APP_ROOT_URL + ":8080/rtring_s_new_t/clause/privacy.html";
//			"http://" + APP_ROOT_URL + "/rtring_s_new_t/clause/privacy.html";
			"http://fitincluded.com/reflex-privacy.php";
	public final static String PRIVACY_CN_URL = 
//			"http://" + APP_ROOT_URL + ":8080/rtring_s_new_t/clause/privacy_cn.html";
//			"http://" + APP_ROOT_URL + "/rtring_s_new_t/clause/privacy_cn.html";
			"http://fitincluded.com/reflex-privacy.php";
	
	/** 官网 */
	public final static String LONKLOVING_OFFICAL_WEBSITE = 
			"http://www.fitincluded.com";
	
	/** 联系邮件 */
	public final static String LINKLOVING_OFFICAL_MAIL = "support@fitincluded.com";
	
	/** QQ开放平台APP_ID*/
	public static final String QQ_OPEN_APP_ID = "1104705744";  //正式APPID：1104705744 222222
	
	/** 
	 * 网络是否可用, true表示可用，否则表示不可用.
	 * <p>
	 * 本字段在将在网络事件通知处理中被设置.
	 * <p>
	 * 注意：本类中的网络状态变更事件，尤其在网络由断变好之后，事件收到延迟在1~2秒
	 * ，不知道有没有更好的方法能实时获得网络的变更情况，以后再说吧！
	 */
	private boolean localDeviceNetworkOk = true;
	
	/**
	 * true表示是首次进入app，否则不是。用于首页这样的界面里判断是否显示指示性帮助提示。
	 */
	private boolean firstIn = false;
	
	/**
	 * 用户登陆成功后，服务端返回的该用户的最新信息封装对象.
	 */
	private UserEntity localUserInfoProvider = null;
	
	/**
	 * 用户登陆成功后,自动获取单位的类型
	 */
	public static String UNIT_TYPE = "Imperial" ; 
	
	/**
	 * true表示已准备好在网络连接上事件发生时自动启运离线数据自动同步机制，
	 * 否则表示还没有准备好。加此标识的目的是：当程序启动时（比如刚打开闪屏界面时），
	 * 系统就会触发一个网络已连接上的事件（实际上此时程序还没有登陆好、没有准备好呢），此标识
	 * 的目的就是要跳过此启动时就发出的事件通知。
	 */
	private boolean preparedForOfflineSyncToServer = false;
	
	/** 汇总数据同步（到服务端）成后要通知的观察者对象(用于ui提示) */
    private Observer obsForDaySynopicsUploadSucess = null;
    /** 运动数据同步（到服务端）成后要通知的观察者对象(用于ui提示) */
    private Observer obsForSportDatasUploadSucess = null;
    /** Log（到服务端）成后要通知的观察者对象(用于ui提示) */
    private Observer obsForLogSucess = null;
	
	//
	public final Const _const = new Const();
	
	public long getservertime(){
		return HttpHelper.pareseServerUTC();
	}
	
	public  boolean HAS_BG_PIC = false;
	
	
	/**是否OADstep*/
	public int old_step=0; 
	
	private int retryCount = 0;
	
	
	/**正在同步*/
	private static boolean IS_SYN = false;
	public LPDeviceInfo lpDeviceInfo;
	
	public  String getUNIT_TYPE() {
		return UNIT_TYPE;
	}
	public  void setUNIT_TYPE(String uNIT_TYPE) {
		UNIT_TYPE = uNIT_TYPE;
	}
	
	public int getOld_step() {
		return old_step;
	}
	public void setOld_step(int old_step) {
		this.old_step = old_step;
	}
	// uncaught exception handler variable
	private UncaughtExceptionHandler defaultUEH;
	// handler listener
	private Thread.UncaughtExceptionHandler _unCaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {
			if(ex != null)
				Log.e("_unCaughtExceptionHandler", ex.getMessage(), ex);
			
			LoginActivity.doLogout(MyApplication.this, new Observer() {
				@Override
				public void update(Observable observable, Object data) {
					// 退出
					LoginActivity.systemExit(MyApplication.this);
					// re-throw critical exception further to the os (important)
				}
			});
			
			defaultUEH.uncaughtException(thread, ex);
		}
	};
	
	/** BLE蓝牙服务封装 */
	private BLEProvider provider;

	
	private UserEntity userEntity;
	
	static{
//		//必须要设置一个Service,否则接下来将无法与服务端通信的服务
//		AHttpServiceFactory.addServices(SERVER_CONTROLLER_URL_ROOT, "MyController", true);
		
		// EVA_Android可重用库的R文件反射设置
		HelloR eva$android$R = new HelloR();
		eva$android$R.setDefaultRClassPath(R.class.getPackage().getName());
		RHolder.getInstance().setEva$android$R(eva$android$R);
		
		// 
		WidgetUtils.toastTypeSurport = WidgetUtils.TOAST_TYPE_INFO 
										| WidgetUtils.TOAST_TYPE_OK 
										| WidgetUtils.TOAST_TYPE_WARN 
										| WidgetUtils.TOAST_TYPE_ERROR;
	}
	
	
	
	
//	@Override
//	protected void attachBaseContext(Context base) {
//		super.attachBaseContext(base);
//		MultiDex.install(this);
//	}
	
	public void onCreate() 
	{  
        super.onCreate();  
        
        String processName = OsUtils.getProcessName(this,android.os.Process.myPid());
        Log.e(TAG, "进程名称:"+processName);
        if (processName != null) {
			boolean defaultProcess = processName.equals(MyApplication.SERVICE_WATCH);
			if (defaultProcess) {
				
				//必要的初始化资源操作
		        self = this;
		        
		        // ** 百度地图初始化
//		        SDKInitializer.initialize(this);
		        ShareSDK.initSDK(this);
		        
		        // ** 网络连接事件注册
				// Register for broadcasts when network status changed
				IntentFilter intentFilter = new IntentFilter(); 
				intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); 
				this.registerReceiver(networkConnectionStatusBroadcastReceiver, intentFilter);
				Log.e(TAG, "注册成功！");
				
				// 不必为每一次HTTP请求都创建一个RequestQueue对象，推荐在application中初始化
//				NoHttp.init(MyApplication.this);
//				requestQueue = NoHttp.newRequestQueue();
				// ** 崩溃处理handler
		        defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
		        // setup handler for uncaught exception 
		        Thread.setDefaultUncaughtExceptionHandler(_unCaughtExceptionHandler);
		        
		        // ** 蓝牙 BLE 初始化相关代码
		        initBLEProvider();
		        
		        setUNIT_TYPE(PreferencesToolkits.get_unit(MyApplication.this));
		        
			}
		}
       
    }
	

 
	
	private BLEProvider initBLEProvider()
	{
		LepaoProtocalImpl lpi = new LepaoProtocalImpl(){
        	@Override
        	protected boolean isNetworkConnected()
        	{
        		return ToolKits.isNetworkConnected(MyApplication.this);
        	}
        	@Override
        	protected long getServerUTCTimestamp()
        	{
        		return HttpHelper.pareseServerUTC();
        	}
        };
        provider = new BLEProvider(this, BLEWapper.getInstence(), lpi){
        	/**
        	 * 每组运动数据读完后的同步保存方法。每次运动数据读取时，因数据量大，而硬件处理能力有限，是
        	 * 分批次发过来的，应用层每取一次返回的可能是若干条运动数据，取N次直到设备中数据全部读到为止。
        	 * <p>
        	 * 子类可重写本方法以便实现此功能。
        	 * 
        	 * @param originalSportDatas
        	 */
        	// add by Jack Jiang: 2014-08-07
			@Override
			protected void saveSportSync2DB(List<LPSportData> originalSportDatas) {
				
				if(originalSportDatas != null && originalSportDatas.size() > 0)
        		{
        			Log.d(TAG, "【NEW运动数据】收到同步保存运动数据请求：条数"+originalSportDatas.size());
        			
        			// * 将数据保存到本地数据库（以最大可能保证大数据量读取时能最大限度保证数据的可靠性（
        			// * 防止如app崩溃时数据不至于在内存中未来的急被保存而丢失）
        			//   先将原始数据先转换成应用层使用的数据模型
        			// * 每次取出10条 然后保存到数据库
        			List<SportRecord> upList = SportDataHelper.convertLPSportData(originalSportDatas);
        			
        			for(int i=0;i<upList.size();i++){
        				Log.d(TAG, "-----------》开始同步到本地数据库"+upList.get(i).toString());
        			}
        			// 保存数据（到本地数据库先）
        			long t = System.currentTimeMillis();
        			
        			UserDeviceRecord.saveToSqlite(MyApplication.this, upList, MyApplication.this.getLocalUserInfoProvider().getUser_id(), false);
        			
        			Log.d(TAG, "【NEW运动数据】同步保存运动数据完成，本次共"+upList.size()+"条数据，耗时:"+(System.currentTimeMillis() - t)+"毫秒！");
        			
        		}
        		else
        		{
        			Log.d(TAG, "【NEW运动数据】收到同步保存运动数据请求：但集合是空的，originalSportDatas="+originalSportDatas);
        		}
			}
        };
        
        
        provider.setProviderHandler(new BLEHandler(this){
        	
			@Override
			protected BLEProvider getProvider()
			{
				return provider;
			}
			
			// ** 目前APP的同步逻辑时，当在主界面时，一旦连接上蓝牙（连接的场景有2种：1）每次登陆时的
			// ** 自动同步数据），2）主页上下拉同步时）就同时同步所有信息（全流程同步包括数据）
			// 重写本方法的目的是希望把全流程同步数据放到后台无条件执行（之前是放在Observer里执行，
			// 存在的风险是当切换到其它设置界面时，因重置Observer而导致连接还未完成就被替换了，那么
			// 也就不能调用sync方法了，当用户是用于绑定设备时，则包括绑定数据的定入和提交到服务端就
			// 提交不了了，用户的感受就是没有绑定成功，这就有点恶心了）
			@Override
			protected void handleConnectSuccessMsg()
			{
				Log.e(TAG, "handleConnectSuccessMsg");
//				getLocalUserInfoProvider().setLast_sync_device_id(provider.getCurrentDeviceMac());
				// ** 先调用父类方法
				super.handleConnectSuccessMsg();
				retryCount = 0;
				// ** 再默认调用同步方法（即全流程同步）
				// 连接成功就同步数据（包括未定绑定时的绑定设备），但此方法如果是在Observer里，所以还是存
				// 在当未完成时切到其它设置界面时因重置了observer，则可能会使得此同步不会被调用，那么在绑定设备时，
				// 就可能存在不能成功把绑定信息写到设备（及服务端）。放到本方法中在全局唯一的handler中执行
				// 这样很保险，但在诸如设置时的重置如果也要调用这方法的话，就体验不好了，目前先这样吧至少保证绑定等同步
				// 的绝对可靠，只是保存设置时太恶心了（因每次都要同步所有数据，体验就有点恶心了），测试时先这样，不行
				// 的话就取消吧（还是老方法，只是同步又不保险了而已）！
				if(!CommonUtils.isStringEmpty(MyApplication.getInstance(MyApplication.this).getLocalUserInfoProvider().getLast_sync_device_id())){
					MyApplication.syncAllDeviceInfo(MyApplication.this);
				}
				
			}
			
			
//			@Override
//			protected void handleDeviceMsg(BluetoothDevice bluetoothDevice) {
//				super.handleDeviceMsg(bluetoothDevice);
//				Log.e(TAG, "DeviceMac开始保存到本地");
//				PreferencesToolkits.save_ble_device(MyApplication.this,provider.getCurrentDeviceMac());
//			}

			// 重写此方法是希望把解绑时重置设备id放到后台无条件执行（从而保证在切换到其它设置界面时
			// 也能无条件保证可靠地重置）
			@Override
			protected void notifyForDeviceUnboundSucess_D()
			{
				// 解绑成功时，无条件保证重置本地用户存储的mac地址
				getLocalUserInfoProvider().setLast_sync_device_id(null);
				//
				super.notifyForDeviceUnboundSucess_D();
			}
			
			
			
			@Override
			protected void handleConnectLostMsg() {
				super.handleConnectLostMsg();
				Log.e(TAG, "handleConnectLostMsg");
				
				IS_SYN = false;
				provider.clearProess();
				
				if(MyApplication.getInstance(MyApplication.this).getLocalUserInfoProvider()!=null){
					
					if (!CommonUtils.isStringEmpty(MyApplication.getInstance(MyApplication.this).getLocalUserInfoProvider().getLast_sync_device_id()))
					{
						broadcastUpdate(BLE_SYN_SUCCESS, MyApplication.this);
						
//						if (retryCount < 3)
//						{
//							Log.e(TAG, "BleService 正在重连！");
//							provider.scanForConnnecteAndDiscovery();
//							retryCount++;
//						}
					}	
				}
			}
			

			@Override
			protected void handleSendDataError() {
				super.handleSendDataError();
				IS_SYN=false;
				
				broadcastUpdate(BLE_SYN_SUCCESS, MyApplication.this);
				
				provider.release();
				
				retryCount = 0;
				
			}

			// 重写此方法是希望把全流程同步（主要是当未绑定时绑定的过程）放到后台无条件执
			// 行（从而保证在切换到其它设置界面时也能保证绑定流程（写到设备里后的数据返回、
			// 提交到服务端等）的可靠执行完，否则如果放到observer中实现可能会因切换到其它设置
			// 页面因重新设置Observer而可能在蓝牙处理的异步过程中会错过这部分的处理
			/**
			 * 全流程（设备信息）同步成功后会调用此方法，以便通知上层应用刷新ui等.
			 * <p>
			 * 本方法默认什么也不做.
			 * @param deviceInfo 同步成功后返回的最新设备信息对象
			 */
			@Override
			protected void notifyForDeviceFullSyncSucess_D(LPDeviceInfo deviceInfo)
			{
				if(deviceInfo != null)
				{
				   Log.i(TAG, "进来了");
					PreferencesToolkits.updateLocalDeviceInfo(MyApplication.this, deviceInfo);
					Date mDate = new Date();
					PreferencesToolkits.updateLocalDeviceInfo(MyApplication.this, mDate.getTime());
					super.notifyForDeviceFullSyncSucess_D(deviceInfo);
				}
			}
			
			/**
			 * 收到获取设备id数据反馈时会调用此方法，以便通知上层应用刷新ui等.
			 * <p>
			 * 目前APP中没有需要单独获取设备id的地方，通常认为在获得设备id通知时即是在同步全流程中，
			 * 那么我们也就可以在此方法中判定用户跟设备的绑定情况了.
			 * 
			 * @param id
			 */
			@Override
			protected void notifyForFullSyncGetDeviceIDSucess_D(final String deviceId)
			{
				super.notifyForFullSyncGetDeviceIDSucess_D(deviceId);
				
			}
			/*****从0x13返回 开始同步流程*****/
			@Override
			protected void notifyFor0x13ExecSucess_D(LPDeviceInfo latestDeviceInfo) {
				if(!IS_SYN){
					if(latestDeviceInfo != null){
						IS_SYN = true;
						//********将设备信息保存到ShareP******************//
						LocalInfoVO vo = PreferencesToolkits.getLocalDeviceInfo(MyApplication.this);
						latestDeviceInfo.deviceStatus = vo.deviceStatus;
						PreferencesToolkits.updateLocalDeviceInfo(MyApplication.this, latestDeviceInfo);
						PreferencesToolkits.updateLocalDeviceInfo(MyApplication.this, new Date().getTime());
						//**************************//
						Log.d(TAG, "当日所有步数:" + latestDeviceInfo.stepDayTotals);
						
						broadcastUpdate(BLE_SYN_SUCCESS, MyApplication.this);
						broadcastUpdate(BLE_STATE_SUCCESS, MyApplication.this);
						
						provider.setTimestemp(latestDeviceInfo.timeStamp);
						provider.setServertime(System.currentTimeMillis());
						
						lpDeviceInfo = latestDeviceInfo;
						
						
						userEntity = MyApplication.getInstance(MyApplication.this).getLocalUserInfoProvider();
						synAllInfo();
					}
				}
				super.notifyFor0x13ExecSucess_D(latestDeviceInfo);
			}
			
			
			
			private void synAllInfo() {
				try {
					provider.getModelName(MyApplication.this);
					provider.SetClock(MyApplication.this, DeviceInfoHelper.fromUserEntity(userEntity));
					
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			
			

			//闹钟设置成功
			@Override
			protected void notifyForSetClockSucess()
			{
				super.notifyForSetClockSucess();
				try
				{
					provider.SetLongSit(MyApplication.this, DeviceInfoHelper.fromUserEntity(userEntity));
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
				catch (ParseException e)
				{
					e.printStackTrace();
				}
			}
			
			//久坐提醒设置成功
			@Override
			protected void notifyForLongSitSucess()
			{
				super.notifyForLongSitSucess();
				provider.getSportDataNew(MyApplication.this);
			}
			
			// 运动数据读取完成
			@Override
			protected void handleDataEnd()
			{
				super.handleDataEnd();
				provider.SetDeviceTime(MyApplication.this);
			}
			
			
			 //时间设置成功
			@Override
			public void notifyForSetDeviceTimeSucess()
			{
				super.notifyForSetDeviceTimeSucess();
				IS_SYN=false;
				if(!CommonUtils.isStringEmpty(MyApplication.getInstance(MyApplication.this).getLocalUserInfoProvider().getLast_sync_device_id())){
					try
					{
						if(lpDeviceInfo.recoderStatus == 1 &&  
								lpDeviceInfo.userHeight == Integer.parseInt(userEntity.getUser_height()) &&  
								lpDeviceInfo.userWeight == Integer.parseInt(userEntity.getUser_weight()))
						{
							Log.d(TAG, "全部命令OK:");
							broadcastUpdate(BLE_SYN_SUCCESS, MyApplication.this);
						}else{
							provider.regiesterNew(MyApplication.this, DeviceInfoHelper.fromUserEntity(userEntity));
						}
					}
					catch (JSONException e)
					{
						e.printStackTrace();
					}
					catch (ParseException e)
					{
						e.printStackTrace();
					}
				}
			}

			
			@Override
			protected void notifyForModelName(LPDeviceInfo latestDeviceInfo) {
				lpDeviceInfo.modelName = latestDeviceInfo.modelName;
				PreferencesToolkits.updateLocalDeviceInfo(MyApplication.this, lpDeviceInfo);
				PreferencesToolkits.updateLocalDeviceInfo(MyApplication.this, new Date().getTime());
				
				super.notifyForModelName(latestDeviceInfo);
			}

			@Override
			protected void notifyForSetBodySucess() {
				super.notifyForSetBodySucess();
			
			}
			
			
			
        });
   
        
        return provider;
	}

	
	public boolean isLocalDeviceNetworkOk()
	{
		return localDeviceNetworkOk;
	}
	
	public boolean isFirstIn()
	{
		return firstIn;
	}
	public void setFirstIn(boolean firstIn)
	{
		this.firstIn = firstIn;
	}

	
	/**
	 * APP退出时需要做的释放操作.
	 * 
	 * @see #releaseBLE()
	 */
	public void releaseAll()
	{
		try
		{
			Log.e(TAG, "Myapplication  releaseAll");
			// Unregister broadcast listeners
			this.unregisterReceiver(networkConnectionStatusBroadcastReceiver);
			releaseBLE();
		}
		catch (Exception e)
		{
			Log.w(TAG, e.getMessage(), e);
		}
	}
	/**.
	 * 释放蓝牙相关的所有资源.
	 */
	public void releaseBLE()
	{
		Log.e(TAG, "Myapplication  releaseBLE");
		if(provider.isConnectedAndDiscovered())
		{
			provider.release();
			provider.setCurrentDeviceMac(null);
			provider.setmBluetoothDevice(null);
			provider.resetDefaultState();
		}
	}
	
	public UserEntity getLocalUserInfoProvider()
	{
		// 用户信息对象是null的情况，可能是被Android内存回收机制错误地进行垃圾回收了，
		// 因此对象使用范围很广，为了在发生错误内存回收后，不会导致调用方的NullPointException，
		// 此处就强制从本地读取一份（好在当app启动后，每个属性的改变都会及时保存一次到本地，所以
		// 此时读不会导致不一致的发生，是安全的！）
		if(localUserInfoProvider == null)
			localUserInfoProvider = PreferencesToolkits.getLocalUserInfo(this);
		return localUserInfoProvider;
	}
	/**
	 * 本方法只在2种情形下被调用.
	 * <p>
	 * 1)正常登陆成功时，会把返回的用户信息返回保存起来（也就调用本方法）；<br>
	 * 2)当免登陆时，会把本地存储的用户信息读取并调用本方法保存到内存中.
	 * 
	 * @param localUserInfoProvider
	 */
	public void setLocalUserInfoProvider(final UserEntity _localUserInfoProvider)
	{
		setLocalUserInfoProvider(_localUserInfoProvider, true);
	}
	/**
	 * 本方法只在2种情形下被调用.
	 * <p>
	 * 1)正常登陆成功时，会把返回的用户信息返回保存起来（也就调用本方法）；<br>
	 * 2)当免登陆时，会把本地存储的用户信息读取并调用本方法保存到内存中.
	 * 
	 * @param localUserInfoProvider
	 * @param savePreferences true表示同时会被写到本地preference，否则不保存
	 */
	public void setLocalUserInfoProvider(final UserEntity _localUserInfoProvider, boolean savePreferences)
	{
		this.localUserInfoProvider = _localUserInfoProvider;
		if(savePreferences)
			// 2014-06-13日把完整的UserEntity对象保存起来备用（用于接下来的免登陆功能）
			PreferencesToolkits.setLocalUserInfo(this, localUserInfoProvider);
		// 为UserEntity对象添加属性改变监听事件处理
		if(localUserInfoProvider != null)
		{
			localUserInfoProvider.setPropertyChangedObserver(new Observer()
			{
				// 当每个属性改变时，都即时更新到本地存储，以保证内存中与Prefrence中的内容完全一致
				@Override
				public void update(Observable observable, Object data)
				{
					System.out.println("【UserEntity属性改变了】属性名:"+data);
					// 每次属性被改变时（比如修改昵称、修改性能等）都将重新把此用户对象存储一次（以备下次免登陆时使用）
					PreferencesToolkits.setLocalUserInfo(MyApplication.this, MyApplication.this.localUserInfoProvider);
					//清除本地家庭账号存储（确保当前用户信息被更改后，下次打开家庭号列表时能从网络取一份最新的，否则就不一致了）
					SharedPreferencesUtil.saveSharedPreferences(MyApplication.this, "__ue_list__", "");
				}
			});
			
			// 根据用户的日志开启属性来决定蓝牙log是否要被抓到本地
			if("1".equals(localUserInfoProvider.getEnable_sync_log()))
				LogX.enabled = true;
			else
				LogX.enabled = false;
		}
	}

	public void setObsForDaySynopicsUploadSucess(Observer obsForDaySynopicsUploadSucess)
	{
		this.obsForDaySynopicsUploadSucess = obsForDaySynopicsUploadSucess;
	}

	public void setObsForSportDatasUploadSucess(Observer obsForSportDatasUploadSucess)
	{
		this.obsForSportDatasUploadSucess = obsForSportDatasUploadSucess;
	}
	public void setobsForLogSucess(Observer ob)
	{
		this.obsForLogSucess = ob;
	}
	
	public void setPreparedForOfflineSyncToServer(boolean preparedForOfflineSyncToServer)
	{
		this.preparedForOfflineSyncToServer = preparedForOfflineSyncToServer;
	}
	
	public int getCommentNum()
	{
		return currentTotalCommentNum;
	}
	public void setCommentNum(int commentNum) 
	{
		currentTotalCommentNum = commentNum;
	}

	//--
	public void onLowMemory ()
	{
		super.onLowMemory();
		Log.e(TAG, "???????????????????????????onLowMemory!!!!!!!!!");
	}
	
	/**
	 * 本地网络状态变更消息接收对象.
	 * <p>
	 * 接收本地网络状态变更的目的在于解决当正常的连接因本地网络改变（比如：网络断开了后，又连上了）
	 * 而无法再次正常发送数据的问题（即使网络恢复后），解决的方法是：当检测到本地网络断开后就立即关停
	 * 本地UDP Socket，这样当下次重新登陆或尝试发送数据时就会重新建立Socket从而达到重置Socket的目的，
	 * Socket重置后也就解决了这个问题。
	 */
	// The BroadcastReceiver that listens for discovered devices and
	// changes the title when discovery is finished
	private final BroadcastReceiver networkConnectionStatusBroadcastReceiver = new BroadcastReceiver() 
	{ 
		@Override
		public void onReceive(Context context, Intent intent)
		{
			ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE); 
			NetworkInfo mobNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); 
			NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
			if(!((mobNetInfo != null && mobNetInfo.isAvailable()) || (wifiNetInfo != null && wifiNetInfo.isAvailable())))
			{ 
				//					if(ClientCoreSDK.DEBUG)
				Log.e(TAG, "【本地网络通知】检测本地网络连接断开了!"); 
				//
				localDeviceNetworkOk = false;
			}
			else 
			{ 
//				if(ClientCoreSDK.DEBUG)
					// connect network 
					Log.e(TAG, "【本地网络通知】检测本地网络已连接上了!"); 
				//
				localDeviceNetworkOk = true;
				//蓝牙发送异常数据上传
				
				new BleErrSubmitServerAsyncTask(MyApplication.this).execute();
				
				if(preparedForOfflineSyncToServer)
				{
					//** 当网络连接上时，自动尝试提交未成功同步的离线数据（从而保证数据的及时提交）
					// 离线数据保存成功后，同时无条件尝试启动数据同步线程
//					new DayDataSubmitServerAsyncTask(MyApplication.this, obsForDaySynopicsUploadSucess, false).execute();
					// 看看数据库中有多少未同步（到服务端的数据）
//					List<SportRecord> upList = UserDeviceRecord.findHistoryWitchNoSync(MyApplication.this, MyApplication.getInstance(MyApplication.this).getLocalUserInfoProvider().getUser_id());
					// 离线数据保存成功后，同时尝试启动数据同步线程
					/**
					 * 
					 */
//					new SportDataSubmitServerAsyncTask(MyApplication.this, obsForSportDatasUploadSucess, upList, false).execute();
				}
			} 
		}
	};
	public static void syncAllDeviceInfoAuto(Context context,boolean isScaned, Observer obsForHint)
	{
		BLEProvider provider = MyApplication.getInstance(context).getCurrentHandlerProvider();
		MyApplication.getInstance(context).getLocalUserInfoProvider().setLast_sync_device_id(provider.getCurrentDeviceMac());
		if(provider != null)
		{
			if(provider.isConnectedAndDiscovered())
			{
				Log.d(TAG, "isConnectedAndDiscovered()==true, 指令将可直接执行。");
				syncAllDeviceInfo(context);
			}
			else
			{   
				Log.d(TAG, "isConnectedAndDiscovered()==false, 即将开始连接过程。。。");
				if(obsForHint != null)
				obsForHint.update(null, null);
				
				if(CommonUtils.isStringEmpty(MyApplication.getInstance(context).getLocalUserInfoProvider().getLast_sync_device_id())){
					Log.i(TAG, "扫描连接BLE");
					provider.scanForConnnecteAndDiscovery();  //syncAllDataHandler);
				}else{
					try{
						Log.i(TAG, "直接连接BLE");
						provider.connect_mac(MyApplication.getInstance(context).getLocalUserInfoProvider().getLast_sync_device_id());
					} catch (BLException e) {
						Log.d(TAG, "直接连接BLE失败："+e.getMessage());
					}
				}
//				provider.scanForConnnecteAndDiscovery();  //syncAllDataHandler);
			}
		}
		else{
			System.out.println("provider是空的啊");
		}
	}
	public static void syncAllDeviceInfo(Context context)
	{
		BLEProvider provider = MyApplication.getInstance(context).getCurrentHandlerProvider();
		if(provider != null)
		{
			UserEntity userEntity = MyApplication.getInstance(context).getLocalUserInfoProvider();
			try
			{
				new Thread().sleep(150);
				provider.getAllDeviceInfoNew(context);
			} catch (Exception e) {
				Log.w(TAG, e.getMessage(), e);
			}
		}
	}
	public BLEProvider getCurrentHandlerProvider() {
		return provider;
	}

	public void setProvider(BLEProvider provider) {
		this.provider = provider;
	}
	
	private void broadcastUpdate(final String action, Context context)
	{
		final Intent intent = new Intent(action);
		context.sendBroadcast(intent);
	}
	/**
	 * <p>
	 * 一个获得本application对象的方便方法.<br>
	 * 本方法就相当于在你的activity中调用：(MyApplication)this.getApplicationContext()
	 * ，本方法只是为了简化操作而已.
	 * </p>
	 * 
	 * @param context
	 * @return
	 */
	public static MyApplication getInstance(Context context)
	{
		return self;
	}

	//-------------------------------------------------------------------------------------- inner class
	public class Const
	{
		public final static String DIR_KCHAT_WORK_RELATIVE_ROOT = "/"+".rtring";
		
		/** 用户头像缓存目录 */
		public final static String DIR_KCHAT_AVATART_RELATIVE_DIR = DIR_KCHAT_WORK_RELATIVE_ROOT+"/"+"avatar";
		/** 用户OAD升级文件缓存目录 */
		public final static String DIR_KCHAT_OAD_RELATIVE_DIR = DIR_KCHAT_WORK_RELATIVE_ROOT+"/"+"OAD";
		/** 用户群组背景图片 */
		public final static String DIR_KCHAT_BG_RELATIVE_DIR = DIR_KCHAT_WORK_RELATIVE_ROOT+"/"+"background";
		
		public final static String DIR_ENT_IMAGE_RELATIVE_DIR = DIR_KCHAT_WORK_RELATIVE_ROOT+"/"+"ent";

		/** 用户上传头像时，允许的最大用户头像文件大小 */
		public final static long LOCAL_AVATAR_FILE_DATA_MAX_LENGTH = 2 * 1024 * 1024; // 2M
	}
}