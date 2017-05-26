package com.linkloving.rtring_c_watch.logic.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
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

import com.alibaba.fastjson.JSONObject;
import com.eva.android.HttpFileDownloadHelper;
import com.eva.android.platf.std.AutoUpdateDaemon;
import com.eva.android.widget.DataLoadingAsyncTask;
import com.eva.android.widget.util.WidgetUtils;
import com.eva.android.widget.util.WidgetUtils.ToastType;
import com.eva.android.widgetx.AlertDialog;
import com.eva.android.x.AsyncTaskManger;
import com.eva.android.x.BaseActivity;
import com.eva.epc.common.util.CommonUtils;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.eva.epc.core.dto.SysActionConst;
import com.example.android.bluetoothlegatt.BLEHandler.BLEProviderObserverAdapter;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.proltrol.LepaoProtocalImpl;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.PreferencesToolkits;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.linkloving.rtring_c_watch.logic.model.LocalInfoVO;
import com.linkloving.rtring_c_watch.utils.EntHelper;
import com.linkloving.rtring_c_watch.utils.IntentFactory;
import com.linkloving.rtring_c_watch.utils.SkinSettingManager;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.JobDispatchConst;
import com.rtring.buiness.logic.dto.UserEntity;

public class OwnBraceletActivity extends BaseActivity
{
	private final static String TAG = OwnBraceletActivity.class.getSimpleName();
	public final static int Device_Type_Watch = 1;
	public final static int DEVICE_VERSION_TYPE = Device_Type_Watch+1;

	private String file_name_OAD;

	private Button backButton;
	private Button OAD;
	private TextView mac;
	private TextView version;
	private TextView battery;
	private ImageView batteryImg;
	private TextView syncTime;
	private TextView viewNickName;
	private Button unbound;
	private Button ring;
	
	
	AlertDialog dialog_unbond;
	AlertDialog dialog_oad;
	
	PopupWindow popup;
	ProgessWidget progessWidget;

	private LocalInfoVO vo;
	private BLEProvider provider;
//	private MyHandler handler;  //用于接受和更新下载进度的Handler
	
	private byte[] data;
	
	private boolean canoad=false;
	/**控制dialog*/ 
	private boolean click_oad=false;//以前出现过切换到此页面也出现dialog的情况
	
	public static int makeShort(byte b1, byte b2) {
		return (int) (((b1 & 0xFF) << 8) | (b2 & 0xFF));
	}

	// private BLEProvider provider;
	// private BLEHandler unBoundHandler;

	private LinearLayout braceletLinear;
	private LinearLayout helpLinear;
	
	BLEProviderObserverAdapter observerAdapter=null;
	private AsyncTaskManger asyncTaskManger = new AsyncTaskManger();
	private LepaoProtocalImpl mLepaoProtocalImpl=new LepaoProtocalImpl();
	private SkinSettingManager mSettingManager;
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_own_bracelet);
		observerAdapter=new BLEProviderObserver();
		vo = PreferencesToolkits.getLocalDeviceInfo(OwnBraceletActivity.this);
		initView();
		bindListener();
		//用来区别是否直接更新
		Intent intent = this.getIntent();
		
		int type = IntentFactory.getOwnBraceletActivityIntent(intent);
		if(type == 111){
			//重复oad点击事件
			if (ToolKits.isNetworkConnected(OwnBraceletActivity.this))
			{
				if(vo.battery<3){
					new com.eva.android.widgetx.AlertDialog.Builder(OwnBraceletActivity.this)
					.setTitle(ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.bracelet_oad_failed))
					.setMessage(ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.bracelet_oad_failed_battery)) //no eng
					.setPositiveButton(R.string.general_ok, null).show();
				}else{
					click_oad = true;
					MyApplication.getInstance(OwnBraceletActivity.this).syncAllDeviceInfoAuto(OwnBraceletActivity.this,false,null); 
				}
			}
			else 
			{
				new com.eva.android.widgetx.AlertDialog.Builder(OwnBraceletActivity.this)
						.setTitle(ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.bracelet_oad_failed))
						.setMessage(ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.bracelet_oad_failed_msg))
						.setPositiveButton(R.string.general_ok, null).show();
			}
		}
		
	}
	

	@Override
	protected void onResume() {
		super.onResume();
		mSettingManager=new SkinSettingManager(this);
		mSettingManager.initSkins();
		provider = MyApplication.getInstance(this).getCurrentHandlerProvider();
		provider.setBleProviderObserver(observerAdapter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 及时清除此Observer，以便在重新登陆时，正在运行中的蓝牙处理不会因此activity的回收而使回调产生空指针等异常
				provider = MyApplication.getInstance(this).getCurrentHandlerProvider();
				if(provider != null)
					provider.setBleProviderObserver(null);
	}



	@SuppressLint("SimpleDateFormat")
	private void initView()
	{
		backButton = (Button) findViewById(R.id.button1);
		OAD = (Button) findViewById(R.id.oad);

		braceletLinear = (LinearLayout) findViewById(R.id.bracelet_info_linear);

		mac = (TextView) findViewById(R.id.textView2);
		battery = (TextView) findViewById(R.id.textView7);
		String b = ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.bracelet_battery);
		battery.setText(MessageFormat.format(b, "--"));
		batteryImg = (ImageView) findViewById(R.id.imageView1);
		syncTime = (TextView) findViewById(R.id.textView5);
		syncTime.setText(new SimpleDateFormat(ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.bracelet_sync_format)).format(new Date(0)) + "");
		viewNickName = (TextView) findViewById(R.id.activity_own_bracelet_viewNickName);
		version = (TextView) findViewById(R.id.textView4);
		String v = ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.bracelet_version);
		version.setText(MessageFormat.format(v, "--"));
		unbound = (Button) findViewById(R.id.unbound);
		ring = (Button) findViewById(R.id.ring);
		
		popup = createPopupWindow(OwnBraceletActivity.this,R.layout.popup_progess_view,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT, true);
		progessWidget = new ProgessWidget(popup,findViewById(R.id.own_bracelet)); 

		String m = ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.bracelet_mac_address);
		mac.setText(MessageFormat.format(m, MyApplication.getInstance(OwnBraceletActivity.this).getCurrentHandlerProvider().getCurrentDeviceMac()));

		if (vo != null)
		{
			updateView(vo);
		}

		// 显示昵称
		if (MyApplication.getInstance(this).getLocalUserInfoProvider() != null)
			viewNickName.setText(MyApplication.getInstance(this).getLocalUserInfoProvider().getNickname());
	}

	@SuppressLint("SimpleDateFormat")
	private void updateView(LocalInfoVO vo)
	{
		String v = ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.bracelet_version);
		if (!CommonUtils.isStringEmpty(vo.version))
			version.setText(getString(R.string.brace_version)+vo.version);
//			version.setText(MessageFormat.format(v, vo.version));
		else
			version.setText(MessageFormat.format(v, "Unknow"));
		String b = ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.bracelet_battery);
		battery.setText(MessageFormat.format(b, vo.battery));
		Bitmap bitmap = com.linkloving.rtring_c_watch.utils.ToolKits.getBlueboothPowerLevel(vo.battery / 100.0f, OwnBraceletActivity.this);
		batteryImg.setImageBitmap(bitmap);
		if (vo.syncTime > 0)
		{
			syncTime.setText(new SimpleDateFormat(ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.bracelet_sync_format)).format(new Date(vo.syncTime)) + "");
		}
		else
		{
			syncTime.setText(ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.bracelet_sync_none));
		}
	}

	private void bindListener()
	{
//		helpLinear.setOnClickListener(new OnClickListener()
//		{
//
//			@Override
//			public void onClick(View arg0)
//			{
//				startActivity(IntentFactory.createHelpActivityIntent(OwnBraceletActivity.this, HelpActivity.FININSH_VIEWPAGE_FINISHACTIVITY, true));
//			}
//		});

		braceletLinear.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				startActivity(new Intent(OwnBraceletActivity.this, DeviceInfoActivity.class));
			}
		});

		backButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				finish();
			}
		});
		
		OAD.setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("static-access")
			@Override
			public void onClick(View v) {
				if (ToolKits.isNetworkConnected(OwnBraceletActivity.this))
				{
					if(vo.battery<3){
						new com.eva.android.widgetx.AlertDialog.Builder(OwnBraceletActivity.this)
						.setTitle(ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.bracelet_oad_failed))
						.setMessage(ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.bracelet_oad_failed_battery)) //no eng
						.setPositiveButton(R.string.general_ok, null).show();
					}else{
						click_oad = true;
						MyApplication.getInstance(OwnBraceletActivity.this).syncAllDeviceInfoAuto(OwnBraceletActivity.this,false,null); 
					}
				}
				else 
				{
					new com.eva.android.widgetx.AlertDialog.Builder(OwnBraceletActivity.this)
							.setTitle(ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.bracelet_oad_failed))
							.setMessage(ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.bracelet_oad_failed_msg))
							.setPositiveButton(R.string.general_ok, null).show();
				}
				
			}
		});
		ring.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(provider.isConnectedAndDiscovered()){
					MyApplication.getInstance(OwnBraceletActivity.this).getCurrentHandlerProvider().SetBandRing(OwnBraceletActivity.this);
				}else{
					Toast.makeText(OwnBraceletActivity.this, "Bluetooth not connected！", Toast.LENGTH_SHORT).show();
//					provider.scanForConnnecteAndDiscovery();
				}
				
			}
		});
		
		unbound.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				
//				if(provider.isConnectedAndDiscovered()){
					if (ToolKits.isNetworkConnected(OwnBraceletActivity.this))
					{
						dialog_unbond = new AlertDialog.Builder(OwnBraceletActivity.this)
						.setTitle(ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.bracelet_unbound))
						.setMessage(ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.bracelet_unbound_msg))
						.setNegativeButton(ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.general_no), new DialogInterface.OnClickListener()
						{
							
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								dialog_unbond.dismiss();
							}
						}).setPositiveButton(ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.general_yes), new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								Log.d(TAG, "unBound click!!!!!!!!!!!!!!!!!!!");
								String last_sync_device_id = MyApplication.getInstance(OwnBraceletActivity.this).getLocalUserInfoProvider()
										.getLast_sync_device_id();
								Log.d(TAG, "last_sync_device_id...................." + last_sync_device_id);
								if (!CommonUtils.isStringEmpty(last_sync_device_id))
								{
									new UnBoundAsyncTask().execute(); // 执行解绑异步操作
								}
								dialog_unbond.dismiss();
								/**
								 * 清空内存中的设备信息
								 */
								PreferencesToolkits.updateLocalDeviceInfo(OwnBraceletActivity.this, new LPDeviceInfo());
							}
						}).create();
						dialog_unbond.show();
					}
					else
					{
						new com.eva.android.widgetx.AlertDialog.Builder(OwnBraceletActivity.this)
						.setTitle(ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.bracelet_unbound_failed))
						.setMessage(ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.bracelet_unbound_failed_msg))
						.setPositiveButton(R.string.general_ok, null).show();
					}
					
//				}else{
//					Toast.makeText(OwnBraceletActivity.this, "请连接蓝牙！", Toast.LENGTH_SHORT).show();
//				}
			}
		});
		
		
		
	}

	/**
	 * 获取网络OAD文件的版本号 和 当前硬件的版本号进行对比 判断是否要更新
	 * 
	 * @author cherry
	 * 
	 */
	protected class UntreatedAsyncTask extends DataLoadingAsyncTask<Void, Integer, DataFromServer>
	{

		public UntreatedAsyncTask()
		{
			super(OwnBraceletActivity.this, getString(R.string.general_submitting));
		}

		@Override
		protected DataFromServer doInBackground(Void... params)
		{ //http://115.29.110.195:6080/linkloving_server-watch/MyControllerJSON
			LocalInfoVO vo =  PreferencesToolkits.getLocalDeviceInfo(context);
			JSONObject obj = new JSONObject();
			obj.put("device_type", Device_Type_Watch);
			obj.put("firmware_type", DEVICE_VERSION_TYPE);
			obj.put("model_name", vo.modelName); 
			int version_int = makeShort(vo.version_byte[1],vo.version_byte[0]);
			obj.put("version_int", version_int+"");  
			Log.i(TAG, "device_type:" + Device_Type_Watch+"...firmware_type:"+DEVICE_VERSION_TYPE+"...version_int:"+version_int);
			HttpServiceFactory4AJASONImpl.isOAD = true;
			return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(
							DataFromClient.n()
							.setProcessorId(MyProcessorConst.PROCESSOR_FIRMWARE)
							.setJobDispatchId(JobDispatchConst.FIRMWARE_BASE)
							.setActionId(SysActionConst.ACTION_NEW)
							.setNewData(obj.toJSONString()));     // 本地固件的版本
		}
		
		@Override
		protected void onPostExecuteImpl(Object result) {
			HttpServiceFactory4AJASONImpl.isOAD = false;
			if(result==null){
				Toast.makeText(OwnBraceletActivity.this, R.string.bracelet_oad_version_top, Toast.LENGTH_SHORT).show();
			}else if(("").equals(result.toString())){
				Toast.makeText(OwnBraceletActivity.this, R.string.bracelet_oad_version_top, Toast.LENGTH_SHORT).show();
			}
			else{
				String json=result.toString();
				Log.i(TAG, "json:"+json);
				JSONObject jsonObject = JSONObject.parseObject(json);
				String version_code=jsonObject.getString("max_version_code");
				if(Integer.parseInt(version_code, 16)<=Integer.parseInt(vo.version, 16)){  
					Toast.makeText(OwnBraceletActivity.this, R.string.bracelet_oad_version_top, Toast.LENGTH_SHORT).show();
				}else{
//					MyApplication.getInstance(OwnBraceletActivity.this).setIsoad(true);
					file_name_OAD=jsonObject.getString("file_name");
					showdialog();
				}
			}
			
//			file_name_OAD = firmwares.get(0).getFile_name();
//			file_name_OAD ="0205A.bin";
//			file_name_OAD ="ASC12";
//			file_name_OAD ="HZK12"; 
//			file_name_OAD ="u2gtest.bin";
		}
	}
	/**
	 * 提交数据请求和处理的异步执行线程实现类.
	 */
	protected class UnBoundAsyncTask extends DataLoadingAsyncTask<Void, Integer, DataFromServer>
	{
		public UnBoundAsyncTask()
		{
			super(OwnBraceletActivity.this, getString(R.string.general_submitting));
//			super(OwnBraceletActivity.this, false);
		}

		/**
		 * 在后台执行 {@link doLogin()}实现登陆信息的提交和处于结果的读取 .
		 * 
		 * @param parems
		 *            外界传进来的参数
		 * @return 查询结果，将传递给onPostExecute(..)方法
		 */
		@Override
		protected DataFromServer doInBackground(Void... params)
		{
			JSONObject obj = new JSONObject();
			obj.put("user_id", MyApplication.getInstance(OwnBraceletActivity.this).getLocalUserInfoProvider().getUser_id());
			return HttpServiceFactory4AJASONImpl
					.getInstance()
					.getDefaultService()
					.sendObjToServer(DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_LOGIC)
													   .setJobDispatchId(JobDispatchConst.LOGIC_REGISTER)
													   .setActionId(SysActionConst.ACTION_APPEND7).setNewData(obj.toJSONString()));
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
			if (result != null)
			{
				if (((String) result).equals("true"))
				{
					
					if(provider.isConnectedAndDiscovered()){//连接才去下这个指令
						
						MyApplication.getInstance(OwnBraceletActivity.this).getCurrentHandlerProvider().unBoundDevice(OwnBraceletActivity.this);
					}   
					else
					{
						UserEntity userEntity = MyApplication.getInstance(OwnBraceletActivity.this).getLocalUserInfoProvider();
						userEntity.setLast_sync_device_id(null);
						//*******模拟断开   不管有没有连接 先执行这个再说
						MyApplication.getInstance(OwnBraceletActivity.this).getCurrentHandlerProvider().release();
						MyApplication.getInstance(OwnBraceletActivity.this).getCurrentHandlerProvider().setCurrentDeviceMac(null);
						MyApplication.getInstance(OwnBraceletActivity.this).getCurrentHandlerProvider().setmBluetoothDevice(null);
						
						com.linkloving.rtring_c_watch.utils.ToolKits.showCommonTosat(OwnBraceletActivity.this, true, OwnBraceletActivity.this.getResources().getString(R.string.unbound_success), Toast.LENGTH_LONG);
						OwnBraceletActivity.this.finish();
					}
				}
				else
				{
					Log.d(TAG, "解绑失败！！！！");
					Toast.makeText(OwnBraceletActivity.this, ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.debug_device_unbound_failed),Toast.LENGTH_LONG).show();
					// bleOptionWapper.showHint("设备解绑信息同步到服务端时失败.");
				}
			}
			else
			{
				Log.e(TAG, "unBoundAsyncTask result is null!!!!!!!!!!!!!!!!!");
			}

		}
	}
	/**
	 * 下载文件 并且 将文件写入设备 （应用层和蓝牙层交互）
	 * 
	 * @author cherry
	 * 
	 */
	protected class StartOADAsyncTask extends DataLoadingAsyncTask<String, Integer, Integer>
	{

		public StartOADAsyncTask()
		{
			super(OwnBraceletActivity.this, getString(R.string.general_uploading));
		}

		@Override
		protected Integer doInBackground(String... params)
		{
			Log.i(TAG, "地址是："+EntHelper.getOADFileSavedDir(OwnBraceletActivity.this));
			return downLoadFile(params[0], file_name_OAD);
		}

		@SuppressWarnings("resource")
		@Override
		protected void onPostExecuteImpl(Object arg0)
		{
			//该函数返回整形 -1：代表下载文件出错。 0：代表下载文件成功 1：代表下载文件经存在
			if (CommonUtils.getIntValue(arg0) != -1)
			{
				try {
					
//					BufferedInputStream in = new BufferedInputStream(new FileInputStream(EntHelper.getOADFileSavedDir(OwnBraceletActivity.this) + "/" + file_name_OAD));
//					 ByteArrayOutputStream out = new ByteArrayOutputStream(1024);   
//				        System.out.println("Available bytes:" + in.available());   
//				        byte[] temp = new byte[1024];   
//				        int size = 0;   
//				        while ((size = in.read(temp)) != -1) {   
//				            out.write(temp, 0, size);   
//				        }   
//				        in.close();   
//				        byte[] content = out.toByteArray();   
//				        Log.i(TAG, "Readed bytes count:" + content.length);
					File file = new File(EntHelper.getOADFileSavedDir(OwnBraceletActivity.this) + "/" + file_name_OAD);  
			        long fileSize = file.length();  
			        Log.i(TAG, "file size..."+fileSize);
			        if (fileSize > Integer.MAX_VALUE) {
			        	Log.i(TAG, "file too big...");
			        }  
			  
			        FileInputStream fi = new FileInputStream(file);  
			  
			        byte[] buffer = new byte[(int) fileSize];  
			  
			        int offset = 0;  
			  
			        int numRead = 0;  
			  
			        while (offset < buffer.length  
			  
			        && (numRead = fi.read(buffer, offset, buffer.length - offset)) >= 0) {  
			            offset += numRead;  
			        }  
			        
			        //确保所有数据均被读取  
			        if (offset != buffer.length) {
			            throw new IOException("Could not completely read file "+ file.getName());  
			        }  
			       fi.close();
				        if (provider.isConnectedAndDiscovered())
						{
				        	Log.i(TAG, "buffer size..."+buffer.length);
				        	data = buffer;
				        	canoad=true;
							provider.OADDeviceHead(context, data);          // 第一次传文件头过去
							handler.post(r);
							
						}
						else
						{
							Toast.makeText(getApplicationContext(), "蓝牙断开", Toast.LENGTH_SHORT).show();
						}
				} catch (FileNotFoundException e1) {
					e1.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}   
			}
			else
			{
				WidgetUtils.showToast(OwnBraceletActivity.this, "文件下载错误！~", ToastType.OK);
			}

			// 文件下载完成后 开始执行 OAD
			// provider.set

			// OAD 确认完成后 还得向服务端发送 信息 确定升级完毕
			// 执行下载文件的过程 ----------需要重写
			// JSONObject obj = new JSONObject();
			// obj.put("id", id);
			// obj.put("max_version_code", version_code);
			// obj.put("file_name", file_name_OAD);
			// return
			// HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(
			// DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_FIRMWARE)
			// //1018
			// .setJobDispatchId(JobDispatchConst.FIRMWARE_BASE) //6
			// .setActionId(SysActionConst.ACTION_REMOVE) //2
			// .setNewData(obj.toJSONString()));
		}

		/*
		 * 该函数返回整形 -1：代表下载文件出错。 0：代表下载文件成功 1：代表下载文件经存在 path =
		 * EntHelper.getEntFileSavedDir(this)+ fileName
		 */
		public int downLoadFile(String urlStr, String fileName)
		{
			Object[] ret;
			try {
				ret = HttpFileDownloadHelper.downloadFileEx(urlStr
								// 如果服务端判定需要更新头像到本地缓存时的保存目录
								, EntHelper.getOADFileSavedDir(OwnBraceletActivity.this), 0, null, true);
				
				if(ret != null && ret.length >=2)
				{
					String savedPath = (String)ret[0];
					int fileLength = (Integer)ret[1];
					
					Log.i(TAG,"================"  + savedPath + "," + fileLength); 
				}
			} catch (Exception e) {
				e.printStackTrace();
				return -1;
			}
			
			return 0;
		}

	}

	/**
	 * 比对发现新版本后 向用户确认是否更新
	 */
	private void showdialog()
	{
		dialog_oad = new AlertDialog.Builder(OwnBraceletActivity.this)
				.setTitle(ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.bracelet_oad))
				.setMessage(ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.bracelet_oad_msg))
				.setNegativeButton(ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.general_no), new DialogInterface.OnClickListener()
				{
							@Override
							public void onClick(DialogInterface dialog,int which) {
								dialog_oad.dismiss();
							}
							
				}).setPositiveButton(ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.general_yes), new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						// 执行OAD代码
						Log.d(TAG, "OAD click!!!!!!!!!!!!!!!!!!!");
						provider = MyApplication.getInstance(OwnBraceletActivity.this).getCurrentHandlerProvider();
						if (provider.isConnectedAndDiscovered())
//						if(true)
						{
							String url = "http://linkloving.com:6080/linkloving_server-watch/BLEFirmwareDownloadController?action=ade&name="+file_name_OAD;
							new StartOADAsyncTask().execute(url);
						}
						else
						{
							Toast.makeText(getApplicationContext(), ToolKits.getStringbyId(OwnBraceletActivity.this, R.string.bracelet_bluetooth_closed), Toast.LENGTH_SHORT).show();
						}
						dialog_oad.dismiss();
						progessWidget.show();
						progessWidget.startSyncing();
						
					}
				}).create();
		dialog_oad.show();
	}
	@SuppressLint("HandlerLeak")
	Handler handler = new Handler() {  
        public void handleMessage(Message msg) {  
            switch (msg.what) {  
            
            case 0x333:  
            	if(canoad && msg.arg1 <(data.length-17)/16  )
				{
            		updateProgess(msg.arg1,(data.length-17)/16,progessWidget);  
                    postDelayed(r, 2000);  
				}else{
//					progessWidget.syncFinish(canoad);
//            	    canoad=false;
				}
                break;  
            }  
        };  
    }; 
    Runnable r = new Runnable() {  
        int i = 0;  
        @SuppressWarnings("static-access")
		@Override  
        public void run() {  
            Message msg = new Message();  
            msg.what = 0x333;  
            i = mLepaoProtocalImpl.getOAD_percent();  
            msg.arg1 = i;  
//            Log.d(TAG, "OAD  进度："+i);
            handler.sendMessage(msg);  
        };  
    }; 
    
    


	private class BLEProviderObserver extends BLEProviderObserverAdapter
	{

		@Override
		protected Activity getActivity()
		{
			return OwnBraceletActivity.this;
		}

		
		
		
		@Override
		public void updateFor_notifyForDeviceUnboundSucess_D() {
			super.updateFor_notifyForDeviceUnboundSucess_D();
			Log.e(TAG, "解绑成功！");
			MyApplication.getInstance(OwnBraceletActivity.this).getLocalUserInfoProvider().setLast_sync_device_id(null);
			MyApplication.getInstance(OwnBraceletActivity.this).releaseBLE();
			com.linkloving.rtring_c_watch.utils.ToolKits.showCommonTosat(OwnBraceletActivity.this, true, OwnBraceletActivity.this.getResources().getString(R.string.unbound_success), Toast.LENGTH_LONG);
			mac.setText("");
			version.setText("");
			OwnBraceletActivity.this.finish();
		}



		@Override
		public void updateFor_notifyCanOAD_D()
		{
			if(provider.isConnectedAndDiscovered()){
				canoad = true;
				LocalInfoVO vo_new =  PreferencesToolkits.getLocalDeviceInfo(OwnBraceletActivity.this);
				MyApplication.getInstance(OwnBraceletActivity.this).setOld_step(vo_new.totalsteps);
				provider.OADDevice(getActivity(), data);
			}
		}

		@Override
		public void updateFor_notifyNOTCanOAD_D() {
//			Toast.makeText(OwnBraceletActivity.this, "OAD 升级失败！", Toast.LENGTH_SHORT).show();
			progessWidget.msg.setText( "Firmware Upgrade Failed!");
			progessWidget.syncFinish(false);
			canoad=false;
		}

		@SuppressWarnings("static-access")
		@Override
		public void updateFor_notifyOADSuccess_D()
		{
			mLepaoProtocalImpl.OAD_percent=(data.length-17)/16;
			progessWidget.syncFinish(true);
			canoad=false;
			WidgetUtils.showToast(OwnBraceletActivity.this, "Firmware Upgrade Success!", ToastType.OK);
			provider.release();
//			Intent serviceintent = new Intent();
//			MyApplication.getInstance(OwnBraceletActivity.this).setAPP_ACTIVE(true);
//			serviceintent.setAction("com.linkloving.watch.SERVICE");
//			serviceintent.setPackage(getPackageName());
//			serviceintent.putExtra("mac", MyApplication.getInstance(OwnBraceletActivity.this).getCurrentHandlerProvider().getCurrentDeviceMac());
//			serviceintent.putExtra("old_step", vo.totalsteps);
//			Log.d(TAG, "OwnBraceletActivity---》old_step:"+vo.totalsteps);
//			startService(serviceintent);
			
		}

		@Override
		public void updateFor_FlashHeadSucess() {
			provider.flashbody(getActivity(), data);
		}
		
		

		@Override
		public void updateFor_handleSetTime() {
			super.updateFor_handleSetTime();
			if(click_oad){
				new UntreatedAsyncTask().execute();
				click_oad = false;
			}
		}




		@Override
		public void updateFor_handleConnectLostMsg() {
			super.updateFor_handleConnectLostMsg();
			Log.e(TAG, "handleConnectLostMsg()!"+canoad);
			if(canoad){
				provider.clearProess();
				Toast.makeText(OwnBraceletActivity.this, R.string.bracelet_oad_reason, Toast.LENGTH_SHORT).show();
				progessWidget.msg.setText( "Firmware Upgrade Failed!");
				progessWidget.syncFinish(canoad);
				canoad=false;
			}
		}




		@Override
		public void updateFor_handleConnectSuccessMsg() {
			super.updateFor_handleConnectSuccessMsg();
			MyApplication.getInstance(OwnBraceletActivity.this).getCurrentHandlerProvider().SetBandRing(OwnBraceletActivity.this);
		}
		
		

		// @Override
		// public void updateFor_notifyCanOAD_D() {
		// oad_continue=true; //可以继续OAD
		//
		// }
		//
		// @Override
		// public void updateFor_notifyOADSuccess_D() {
		// oad_success=true; //OAD完成
		//
		// }

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
		
		progess = size;
		progessWidget.getProgressBar().setMax(max);
		progessWidget.getProgressBar().setProgress(progess);
		progessWidget.setMessage(progess, max);
	}
	
	private PopupWindow createPopupWindow(Context context,int layoutID,int width,int height,boolean focus)
	{
		View contentView = View.inflate(context, layoutID, null);
		PopupWindow pop = new PopupWindow(contentView, width, height,focus);
		
		contentView.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK){  
					Log.i(TAG, "在PopupWindow的里面");
		            	return false;
		            }
				return false;
			}
		});
//		pop.setBackgroundDrawable(new ColorDrawable(0x55000000));
		pop.setOutsideTouchable(false);
		pop.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() 
			{
				if(!canoad){
					removeAllAsyncTask();
					Intent intent = new Intent();
					intent.setClass(OwnBraceletActivity.this,PortalActivity.class);
					intent.putExtra("OwnBraceletActivity", "restart");
					startActivity(intent);
					Log.i(TAG, "OAD后来这里了onDismiss");
					finish();
				}else{
					byte[] null_data = {0};
					provider.runIndexProess(OwnBraceletActivity.this, 0, null_data);
				}
				
			}
		});
		return pop;
	}
	
	 public void removeAllAsyncTask()
	  {
		  asyncTaskManger.finishAllAsyncTask();
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
			setTitle(R.string.general_oading);
			btn.setVisibility(View.GONE);
		}
		
		public void syncFinish(boolean iscanoad)
		{
			syncImage.setVisibility(View.GONE);
			
			if(iscanoad){
				setTitle(R.string.general_oad_end);
				syncEndImage.setVisibility(View.VISIBLE);
				Object[] args = {100,100,progess};
				String format = MessageFormat.format(getString(R.string.general_dialog_oad), args);
				msg.setText(format);
			}
			else{
				setTitle(R.string.general_oad_fail);
			}
			btn.setVisibility(View.VISIBLE);
//			Object[] args = {new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).format(new Date()),100+""};
//			String format = MessageFormat.format(getString(R.string.general_sync_time), args);
//			PreferencesToolkits.saveLastCloudSyncTime(OwnBraceletActivity.this, format);
		}
		
		public void clearMessage()
		{
			Object[] args = {"-","-","-"};
			String format = MessageFormat.format(getString(R.string.general_dialog_oad), args);
//			StringBuffer sb = new StringBuffer();
//			sb.append(format+"\n\n请耐心等待,升级过程中请勿离开此页面或者手机远离手表");
//			msg.append(sb.toString());
			msg.setText(format);
		}
		
		public void setMessage(int progess,int max)
		{
			if(max <= 0)
			{
				int percent = 100;
				Object[] args = {percent,max,progess};
				String format = MessageFormat.format(getString(R.string.general_dialog_oad), args);
//				StringBuffer sb = new StringBuffer();
//				sb.append(format+"\n\n请耐心等待,升级过程中请勿离开此页面或者手机远离手表");
//				msg.append(sb.toString());
				msg.setText(format);

				return;
			}
			
			int percent = progess*100/max > 100 ? 100:progess*100/max;
			Object[] args = {percent,max,progess};
			String format = MessageFormat.format(getString(R.string.general_dialog_oad), args);
//			StringBuffer sb1 = new StringBuffer();
//			sb1.append(format+"\n\n请耐心等待,升级过程中请勿离开此页面或者手机远离手表");
//			msg.append(sb1.toString());
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

}
