package com.linkloving.rtring_c_watch.logic.main;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.eva.android.DelayedHandler;
import com.eva.android.widget.DataLoadingAsyncTask;
import com.eva.android.widget.util.WidgetUtils;
import com.eva.android.widget.util.WidgetUtils.ToastType;
import com.eva.android.widgetx.AlertDialog;
import com.eva.android.x.AsyncTaskManger;
import com.eva.epc.common.util.CommonUtils;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.eva.epc.core.dto.SysActionConst;
import com.example.android.bluetoothlegatt.BLEHandler.BLEProviderObserverAdapter;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.proltrol.dto.LPDeviceInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.linkloving.band.dto.DaySynopic;
import com.linkloving.band.dto.SleepData;
import com.linkloving.band.dto.SportRecord;
import com.linkloving.band.ui.DatasProcessHelper;
import com.linkloving.band.ui.DetailChartCountData;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.PreferencesToolkits;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.db.logic.UserDeviceRecord;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.linkloving.rtring_c_watch.logic.launch.LoginActivity;
import com.linkloving.rtring_c_watch.logic.main.impl.SportDataSubmitServerAsyncTask;
import com.linkloving.rtring_c_watch.logic.main.impl.TodayCircleView;
import com.linkloving.rtring_c_watch.logic.main.mainfragmentimpl.TimeFilterUIWrapper;
import com.linkloving.rtring_c_watch.logic.model.LocalInfoVO;
import com.linkloving.rtring_c_watch.logic.more.avatar.ShowUserAvatar;
import com.linkloving.rtring_c_watch.utils.DateSwitcher;
import com.linkloving.rtring_c_watch.utils.DateSwitcher.PeriodSwitchType;
import com.linkloving.rtring_c_watch.utils.DeviceInfoHelper;
import com.linkloving.rtring_c_watch.utils.GooglefitDate;
import com.linkloving.rtring_c_watch.utils.HttpHelper;
import com.linkloving.rtring_c_watch.utils.IntentFactory;
import com.linkloving.rtring_c_watch.utils.SkinSettingManager;
import com.linkloving.rtring_c_watch.utils.SportDataHelper;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.linkloving.rtring_c_watch.utils.UpdateClientAsyncTask;
import com.linkloving.utils.TimeZoneHelper;
import com.linkloving.utils._Utils;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.JobDispatchConst;
import com.rtring.buiness.logic.dto.LoginInfoExtension;
import com.rtring.buiness.logic.dto.UserEntity;
import com.slidingmenu.lib.SlidingMenu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import net.simonvt.datepicker.DatePickDialog;

public class PortalMainFragment extends Fragment {
	private static String TAG = PortalMainFragment.class.getSimpleName();
	//皮肤管理
	private SkinSettingManager mSettingManager;
	
	private static int REQ_PICK_DATE = 0;
	//存放图标要显示的睡眠数据的集合
	private Map<String, SleepData> sleepmap = new HashMap<>();
	// private Handler mhandler = null;

	private BLEProvider provider;
	private TextView syncTime;
	private LinearLayout toggleBtn;
	/** 设备信息 */
	private LinearLayout deviceInfoLinear;

	private TextView batteryText;

	private ImageView batteryImg;
	/** 蓝牙连接状态背景 */
	private ImageView viewBleState;
	/** 蓝牙连接状态描述 */
	private TextView viewBleDesc;
	
	private TextView debugInfo;
	private TextView debugState;
	private StringBuilder debugInfos = new StringBuilder();

	private PullToRefreshScrollView mScrollView;

	/** 下拉同步ui的超时复位延迟执行handler （防止意外情况下，一直处于“同步中”的状态） */
	private DelayedHandler mScrollViewRefreshingHandler = new DelayedHandler(
			10 * 1000) {
		@Override
		protected void fireRun() {
			if (mScrollView != null) {
				mScrollView.onRefreshComplete();
			}
		}
	};

	private final int REQUEST_CODE_BOUND = 1;

	private BLEProviderObserverAdapter bleProviderObserver = null;

	/** 当前正在运行中的数据加载异步线程(放全局的目的是尽量控制当前只有一个在运行，防止用户恶意切换导致OOM) */
	@SuppressWarnings("rawtypes")
	private AsyncTask currentRunningSportDataAsync = null;
	
	/** 当前正在运行中的数据加载异步线程(放全局的目的是尽量控制当前只有一个在运行，防止用户恶意切换导致OOM) */
	@SuppressWarnings("rawtypes")
	private AsyncTask currentRunningSleepDataAsync = null;

	private SportDataWrapper sportDataWrapper = null;
	private SleepDataWrapper sleepDataWrapper = null;
	private TimeFilterUIWrapper timeFilterUIWrapper = null;
	
	/** 运动和睡眠圆环图上的可点击布局 （点击可进入历史数据查看界面） */
	private ViewGroup layoutOfSportAndSleepTopClickable = null;

	private DateSwitcher dateSwitcher = null;

	private RadioButton sportBtn;
	private RadioButton sleepBtn;
	/**提醒用户去升级固件的dialog*/
	public AlertDialog dialog_oad;
	/** 低电量通知对话框 */
	public AlertDialog batteryDialog;

	/** 提示下拉刷新的布局 */
	private ViewGroup layoutOfSyncTip = null;
	/** 提示下拉刷新的关闭按钮 */
	private ImageView viewCloseSyncTip = null;

	private ImageView entLogo = null;
	/** 通知的消息 */
	private TextView entNotice = null;
	/** 通知消息的布局 */
	private ViewGroup entNoticeGroup = null;

	private ViewGroup firstInGroup = null;

	private ImageView accountHead = null;
	private LinearLayout headGroup = null;

  //private boolean tryGetAvatarFromServer = false;
	private ShowUserAvatar showUserAvatarWrapper = null;

	private TextView unRead;
	/**
	 * 蓝牙处理中状态，此状态仅用于UI显示（andy希望能显示“连接中”字样，实际上andy希望的是一个概括性
	 * 状态描述（而非真正对应到蓝牙的连接中状态），仅为了给用户更好地体验而已）。
	 */
	private boolean bleProcessing = false;

	/** 连接中的观察者对象(用于ui提示) */
	private Observer obsForBerforeConnect = new Observer() {
		@Override
		public void update(Observable observable, Object data) {
			
			bleProcessing = true;
			_refreshUIWithBLEState();
			
		}
	};

	/** 汇总数据同步（到服务端）成后要通知的观察者对象(用于ui提示) */
	private Observer obsForDaySynopicsUploadSucess = new Observer() {
		@Override
		public void update(Observable observable, Object data) {
			if (data != null) {
				int sycDataCount = (Integer) data;
				if (sycDataCount > 0)
					
					showDebugInfo(MessageFormat.format(ToolKits.getStringbyId(getActivity(),R.string.portal_main_debug_sync_total_data_to_server_success),sycDataCount));
				
				else
					
					showDebugInfo(ToolKits.getStringbyId(getActivity(),R.string.portal_main_debug_sync_total_data_to_server_end));
			}
		}
	};
	/** 运动数据同步（到服务端）成后要通知的观察者对象(用于ui提示) */
	private Observer obsForSportDatasUploadSucess = new Observer() {
		@Override
		public void update(Observable observable, Object data) {
			if (data != null) {
				int sycDataCount = (Integer) data;
				if (sycDataCount > 0){
					showDebugInfo(MessageFormat.format(ToolKits.getStringbyId(getActivity(),R.string.portal_main_debug_sync_sport_data_to_server_success),sycDataCount));
				}
				else
					showDebugInfo("自动同步:运动数据已全部同步过，本次无需提交服务端.");
				
				//上传到服务器成功   提交到google
				if(PreferencesToolkits.get_googlefit(getActivity()) && mGoogleApiClient!=null && mGoogleApiClient.isConnected()){
					List<SportRecord> upList = UserDeviceRecord.findHistoryWitchNoSyncGoogle(getActivity(),MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getUser_id());
					Log.i("GoogleApiClient", "查出了【"+(upList.size())+"】 条  Data !");
					for(SportRecord googlefitDate:upList){
						Log.i("GoogleApiClient", "SportRecord!"+googlefitDate.toString());
					}
					//上传到google-fit
					List<GooglefitDate> google_list = SportDataHelper.SportData2Google(upList);
					Log.i("GoogleApiClient", "解析了【"+(google_list.size())+"】 条  Data !");
					for(GooglefitDate googlefitDate:google_list){
						Log.i("GoogleApiClient", "googlefitDate!"+googlefitDate.toString());
					}
					if(google_list.size() > 0){
						new GoogleAsyncTask().execute(google_list);
					}
						
				}
			}
		}
	};
	// ----------------------------------------------------------------------------------
	/**************谷歌fit部分START**************/
	private GoogleApiClient mGoogleApiClient = null;
	// ----------------------------------------------------------------------------------
	// START
	private SlidingMenu mSlidingMenu = null;

	// ** 地理位置
	private LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;

	public SlidingMenu getmSlidingMenu() {
		return mSlidingMenu;
	}

	public PortalMainFragment setmSlidingMenu(SlidingMenu mSlidingMenu) {
		this.mSlidingMenu = mSlidingMenu;
		return this;
	}

	// * 本类中不能使用abstract方法在父类中使用，否则会报这样恶心的问题：
	protected void fireToggle() {
		if (mSlidingMenu != null)
			mSlidingMenu.toggle();
	}

	private Blereciver blereciver;

	// ----------------------------------------------------------------------------------
	// END

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		if (!CommonUtils.isStringEmpty(MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getLast_sync_device_id())) {
//			MyApplication.getInstance(getActivity()).getCurrentHandlerProvider().setCurrentDeviceMac(MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getLast_sync_device_id().toUpperCase());
//			MyApplication.syncAllDeviceInfoAuto(getActivity(),false,null);
//		}
		
		blereciver = new Blereciver();
		
		if (getActivity() == null) {
			Log.e(TAG, "【你好中国】getActivity()=null，直接retrune了！！！！"+ getActivity());
			return;
		}
		//lowbattery
		batteryDialog = new AlertDialog.Builder(getActivity())
		        .setTitle(ToolKits.getStringbyId(getActivity(),R.string.portal_main_battery_low))
				.setMessage(ToolKits.getStringbyId(getActivity(),R.string.portal_main_battery_low_msg))
				.setPositiveButton(ToolKits.getStringbyId(getActivity(),R.string.general_ok),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								batteryDialog.dismiss();
							}
						}).create();
		
		dialog_oad = new AlertDialog.Builder(getActivity())
    	        .setMessage(ToolKits.getStringbyId(getActivity(), R.string.bracelet_oad_Portal))
				.setTitle(ToolKits.getStringbyId(getActivity(), R.string.bracelet_update))
				.setNegativeButton(ToolKits.getStringbyId(getActivity(), R.string.general_no), new DialogInterface.OnClickListener()
				{
							@Override
							public void onClick(DialogInterface dialog,int which) {
								dialog_oad.dismiss();
							}
							
				}).setPositiveButton(ToolKits.getStringbyId(getActivity(), R.string.general_yes), new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						Log.e(TAG, "点击了");
						startActivity(IntentFactory.createOwnBraceletActivityIntent(getActivity()));
					}
				}).create();

		mLocationClient = new LocationClient(this.getActivity().getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		initLocation();
		//
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_portal_main, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		super.onViewCreated(view, savedInstanceState);

		provider = MyApplication.getInstance(getActivity()).getCurrentHandlerProvider();

		Log.i(TAG, "onViewCreated  provider是空！" + (provider == null));

		bleProviderObserver = new BLEProviderObserverAdapterImpl();
		provider.setBleProviderObserver(bleProviderObserver);

		sportDataWrapper = new SportDataWrapper(view);
		sleepDataWrapper = new SleepDataWrapper(view);

		//
		initView(view);
		//
		initListeners();

		/**
		 * 启动定位
		 */
		mLocationClient.start();

		// 系统到本界面中，应该已经完成准备好了，开启在网络连上事件时自动启动同步线程的开关吧
		MyApplication.getInstance(getActivity()).setPreparedForOfflineSyncToServer(true);

		if (MyApplication.getInstance(getActivity()).isFirstIn()) {  
			firstInGroup.setVisibility(View.VISIBLE);
		}else{
			layoutOfSyncTip.setVisibility(View.VISIBLE);
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(MyApplication.BLE_SYN_SUCCESS);
		filter.addAction(MyApplication.BLE_STATE_SUCCESS);
		getActivity().registerReceiver(blereciver, filter);
	}

	private class Blereciver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(MyApplication.BLE_SYN_SUCCESS)) {
				
				timeFilterUIWrapper.switchedOver();
				mScrollView.onRefreshComplete();
				refreshTimeAndBattery(null);
				bleProcessOver();
				
			}
			else if(intent.getAction().equals(MyApplication.BLE_STATE_SUCCESS))
			{
				
				timeFilterUIWrapper.switchedOver();
				bleProcessOver();
				
				//更换企业logo
				UserEntity currUser = MyApplication.getInstance(getActivity()).getLocalUserInfoProvider();
				// 有网络时才去尝试载入图片
				refreshEntImgShow(currUser);
			}
		}
	}

	private void initLocation() {
		
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("gcj02");// 返回的定位结果是百度经纬度，默认值gcj02 bd09ll bd09
		option.setScanSpan(1000);// 设置发起定位请求的间隔时间为1000ms
		option.setIsNeedAddress(true); // 返回地址
		mLocationClient.setLocOption(option);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (PreferencesToolkits.get_googlefit(getActivity()) && mGoogleApiClient!=null && !mGoogleApiClient.isConnecting()) 
			mGoogleApiClient.disconnect();
	}

	private void buildFitnessClient() {
		 // 
			Log.e("GoogleApiClient", "Create the Google API Client");
			mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
	                .addApi(Fitness.HISTORY_API)
	                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
	                .addConnectionCallbacks( new GoogleApiClient.ConnectionCallbacks() {
	                            @Override
	                            public void onConnected(Bundle bundle) {
	                                Log.e("GoogleApiClient", "Connected!!!");
	                            }

	                            @Override
	                            public void onConnectionSuspended(int i) {
	                                // If your connection to the sensor gets lost at some point,
	                                // you'll be able to determine the reason and react to it here.
	                                if (i == ConnectionCallbacks.CAUSE_NETWORK_LOST) {
	                                    Log.e("GoogleApiClient", "Connection lost.  Cause: Network Lost.");
	                                } else if (i == ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
	                                    Log.e("GoogleApiClient", "Connection lost.  Reason: Service Disconnected");
	                                    mGoogleApiClient.connect();
	                                }
	                            }
	                        }
	                )
	                .enableAutoManage(getActivity(), 0, new GoogleApiClient.OnConnectionFailedListener() {
	                    @Override
	                    public void onConnectionFailed(ConnectionResult result) {
	                        Log.i("GoogleApiClient", "Google Play services connection failed. Cause: " + result.toString());
//	                      	Toast.makeText(PortalActivity.this, "GoogleApiClient:Exception while connecting to Google Play services: " +result.getErrorMessage(), Toast.LENGTH_SHORT).show();
	                    }
	                })
	                .build();
	}
    

	private void initListeners() {
		
		headGroup.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// startActivity(IntentFactory.createChildActivityIntent(getActivity()));
			}
		});

		// 点击进入历史数据查看界面
		layoutOfSportAndSleepTopClickable
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						getActivity().startActivity(IntentFactory.createSportDataDetailActivityIntent(getActivity(),timeFilterUIWrapper.getDateSwitcher().getStartDate().getTime()));
					}
				});

		firstInGroup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MyApplication.getInstance(getActivity()).setFirstIn(false);
				firstInGroup.setVisibility(View.GONE);
			}
		});
		
		layoutOfSyncTip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PreferencesToolkits.setNeedShowFragmentTip(getActivity(), false);
				// 刷新下接同步提示的相关组件的显示
				refreshSyncTipShow();
			}
		});

		// 点击关闭下拉同步数据的提示
		viewCloseSyncTip.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PreferencesToolkits.setNeedShowFragmentTip(getActivity(), false);
				// 刷新下接同步提示的相关组件的显示
				refreshSyncTipShow();
			}
		});

		// 点击打开调试信息查看对话框
		OnClickListener debugInfosLis = new OnClickListener() {
			@Override
			public void onClick(View v) {
				AlertDialog dialog = new AlertDialog.Builder(getActivity())
						.setTitle("DEBUG INFO")
						.setMessage(debugInfos.toString())
						.setPositiveButton(ToolKits.getStringbyId(getActivity(),R.string.general_ok), null).create();
				dialog.show();
			}
		};
		debugInfo.setOnClickListener(debugInfosLis);
		debugState.setOnClickListener(debugInfosLis);
		//
		/**
		 * 点击电量标志
		 */
		deviceInfoLinear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String mac = MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getLast_sync_device_id();
				Log.i(TAG, "点击电量标志后mac：" + mac);
				if (!CommonUtils.isStringEmpty(mac)) {
					
					startActivity(new Intent(getActivity(),OwnBraceletActivity.class));
//					startActivity(IntentFactory.createOwnBraceletActivityIntent(getActivity()));
					
				} else {
					showBundDialog();
//					startActivityForResult(IntentFactory.createBoundActivity(getActivity()),REQUEST_CODE_BOUND);
				}
			}
		});

		toggleBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// MyApplication.getInstance(getActivity()).getmSlidingMenu().toggle();
				fireToggle();
			}
		});

		sportBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				refreshViews(dateSwitcher.getStartDate(),dateSwitcher.getEndDate(), false); // false
															// 智能判断网络是否可用来决定去哪取数据
			}
		});

		sleepBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				refreshViews(dateSwitcher.getStartDate(),dateSwitcher.getEndDate(), false);
				Log.e(TAG,"sleepBtn------>dateSwitcher.getStartDate()====="+ dateSwitcher.getStartDate()+ "\\ dateSwitcher.getEndDate()==="+ dateSwitcher.getEndDate());
			}
		});
		// 延迟自动hide debug信息显示相关ui
		// 下拉刷新同步
		mScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				String s = MyApplication.getInstance(getActivity())
						.getLocalUserInfoProvider().getLast_sync_device_id();
				if (CommonUtils.isStringEmpty(s))
				{
					// 您还未绑定 请您绑定一个设备
					AlertDialog dialog = new AlertDialog.Builder(getActivity())
							.setTitle(ToolKits.getStringbyId(getActivity(),R.string.portal_main_unbound))
							.setMessage(ToolKits.getStringbyId(getActivity(),R.string.portal_main_unbound_msg))
							.setPositiveButton(ToolKits.getStringbyId(getActivity(),R.string.general_ok),
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,int which) {
											startActivityForResult(IntentFactory.createBoundActivity(getActivity()),REQUEST_CODE_BOUND);
										}
									}).create();
					dialog.show();
					mScrollView.onRefreshComplete();
				} else 
				{
					// 启动超时处理handler
					mScrollViewRefreshingHandler.start();
					// 进入扫描和连接处理过程
					MyApplication.syncAllDeviceInfoAuto(getActivity(), false, obsForBerforeConnect);
					timeFilterUIWrapper.switchedOver();
				}
			}
		});
	}

	

    /**
     * 提示绑定的弹出框
     */
    private void showBundDialog() {

        // 您还未绑定 请您绑定一个设备
//        LayoutInflater inflater = getLayoutInflater();
//        View layoutbund = inflater.inflate(R.layout.modify_sex_dialog, (ViewGroup) findViewById(R.id.linear_modify_sex));
//        final RadioButton band = (RadioButton) layoutbund.findViewById(R.id.rb_left);
//        band.setText(getString(R.string.bound_link_band));
//        final RadioButton watch = (RadioButton) layoutbund.findViewById(R.id.rb_right);
//        watch.setText(getString(R.string.bound_link_watch));
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(ToolKits.getStringbyId(getActivity(), R.string.portal_main_unbound))
                .setMessage(ToolKits.getStringbyId(getActivity(), R.string.portal_main_unbound_msg))
                .setPositiveButton(ToolKits.getStringbyId(getActivity(), R.string.general_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                IntentFactory.startBundTypeActivity(getActivity());
                            }
                        })
                .setNegativeButton(ToolKits.getStringbyId(getActivity(), R.string.general_cancel), null)
                .create();
        dialog.show();
    }
	
	
	
	/**
	 * @param view
	 */
	private void initView(View view) {
		unRead = (TextView) view.findViewById(R.id.main_fragment_unread_text);
		updateUnReadCount();
		headGroup = (LinearLayout) view.findViewById(R.id.account_head_group);
		accountHead = (ImageView) view
				.findViewById(R.id.account_head_imageView);
		// 现在只在onCreate时刷新本地用户头像：可能更新不太及时，但总比要onResume里对服务端的性能压力要小吧
		this.refreshUserAvatarImpl();

		layoutOfSyncTip = (ViewGroup) view
				.findViewById(R.id.fragment_syncTipLL);
		viewCloseSyncTip = (ImageView) view
				.findViewById(R.id.fragment_tip_closeView);

		batteryText = (TextView) view
				.findViewById(R.id.fragment_ble_battery_percent);
		batteryImg = (ImageView) view.findViewById(R.id.fragment_ble_battery);
		
		viewBleState = (ImageView) view.findViewById(R.id.fragment_ble_bgView);
		viewBleDesc = (TextView) view.findViewById(R.id.fragment_ble_descView);
		
		_refreshUIWithBLEState_noConnect(ToolKits.getStringbyId(getActivity(), R.string.portal_main_state_connecting),new Color().rgb(255, 255, 255));

		debugInfo = (TextView) view.findViewById(R.id.test_info);
		debugState = (TextView) view.findViewById(R.id.ble_state);

		deviceInfoLinear = (LinearLayout) view
				.findViewById(R.id.fragment_ble_battery_linear);
		syncTime = (TextView) view.findViewById(R.id.fragment_ble_sync_time);
		toggleBtn = (LinearLayout) view
				.findViewById(R.id.fragment_main_menu_linear);

		sportBtn = (RadioButton) view.findViewById(R.id.fragment_ble_sport_btn);
		sleepBtn = (RadioButton) view.findViewById(R.id.fragment_ble_sleep_btn);
		layoutOfSportAndSleepTopClickable = (ViewGroup) view
				.findViewById(R.id.main_cirrcle_view_clickableLL);

		mScrollView = (PullToRefreshScrollView) view
				.findViewById(R.id.fragment_ble_scrollView);

		entLogo = (ImageView) view.findViewById(R.id.main_fragment_little_logo);
		entNotice = (TextView) view.findViewById(R.id.main_ent_notice);
		entNoticeGroup = (ViewGroup) view.findViewById(R.id.main_ent_notice_LL);

		firstInGroup = (ViewGroup) view.findViewById(R.id.first_in_dialog_LL);

		sportBtn.setChecked(true);

		timeFilterUIWrapper = new TimeFilterUIWrapper(getActivity(), view) { // 此时已经设置好basetime
			@Override
			protected void onFilterChaged() {
				Log.d(TAG,
						"===============================on day change==========================================");
				refreshViews(getDateSwitcher().getStartDate(),
						getDateSwitcher().getEndDate(), false);
				System.out.println("切换时间后:getStartDate="
						+ getDateSwitcher().getStartDate() + "|||getEndDate"
						+ getDateSwitcher().getEndDate());
			}

			@Override
			protected int getPeriodSwitchType() {
				return PeriodSwitchType.day;
			}

			@Override
			protected void onViewTimeClick() {
				DatePickDialog datePickDialog = new DatePickDialog(
						getActivity(), getDateSwitcher().getStartDate()
								.getTime(), new DatePickDialog.IgetDate() {
							@Override
							public void getDate(int year, int month, int day,
									long mills) {
								if (mills > System.currentTimeMillis()) {
									WidgetUtils
											.showToast(
													getActivity(),
													getString(R.string.date_picker_out_time),
													ToastType.INFO);
								} else {
									timeFilterUIWrapper.getDateSwitcher()
											.setBaseTime(new Date(mills));
									timeFilterUIWrapper.switchedOver();
								}
							}

						}, getString(R.string.date_picker_activity_title), getString(R.string.general_ok), getString(R.string.general_cancel));
				datePickDialog.show();
			}
		};

		dateSwitcher = timeFilterUIWrapper.getDateSwitcher();
		

		// 刷新界面数据的总调用方法
		refresuUIAll();
	}

	/**
	 * 刷新界面数据的总调用方法。
	 */
	@SuppressLint("DefaultLocale")
	public void refresuUIAll() {

		// UserEntity u =
		// MyApplication.getInstance(getActivity()).getLocalUserInfoProvider();
		// 刷新用户头像显示
		refreshUserAvatarImpl();
		
		refreshViews(dateSwitcher.getStartDate(),dateSwitcher.getEndDate(), true);

		// 主页面初始化完成，无条件尝试刷新界面数据的显示
		// 调用本方法将会同时刷新日期组件上的显示，否则refreshViews只刷新数据显示而不改变日期组件的显示
		timeFilterUIWrapper.switchedOver();
		

		// 刷新下接同步提示的相关组件的显示
		refreshSyncTipShow();

		// 更换企业定制相关显示
		UserEntity currUser = MyApplication.getInstance(getActivity()).getLocalUserInfoProvider();
		refreshEntNoticeShow(currUser);
		// 有网络时才去尝试载入图片
		refreshEntImgShow(currUser);

		String last_sync_device_id = MyApplication.getInstance(getActivity())
				.getLocalUserInfoProvider().getLast_sync_device_id();
		// Log.i(TAG, "last_sync_device_id");
		//
		// // 如果用户尚未绑定设备
		if (CommonUtils.isStringEmpty(last_sync_device_id)) {
			startActivityForResult(IntentFactory.createBoundActivity(getActivity()),REQUEST_CODE_BOUND);
			bleProcessOver();
		}
		// // 如果已经绑定过设备
		else {
//			provider.setCurrentDeviceMac(last_sync_device_id.toUpperCase());
//			new Thread(){
//					@Override
//					public void run() {
//						super.run();
//						MyApplication.syncAllDeviceInfoAuto(getActivity(), false,obsForBerforeConnect);
//					}
//				
//			}.start();
			
				
		}
	}

	/**
	 * 刷新用户头像显示。
	 */
	private void refreshUserAvatarImpl() {
		UserEntity u = MyApplication.getInstance(getActivity())
				.getLocalUserInfoProvider();
		if (u != null) {
			// 更新本地用户头像
			showUserAvatarWrapper = new ShowUserAvatar(getActivity(),
					u.getUser_id(), accountHead, true, 120, 120 // 此头像用于界面中好友的头像显示，大小参考：main_more布局中的@+id/main_more_settings_avatarView（60dp*60dp）
			) {
				@Override
				protected void avatarUpdateForDownload(Bitmap cachedAvatar) {
					super.avatarUpdateForDownload(cachedAvatar);
					// tryGetAvatarFromServer = true;
				}
			};
		}
		if (showUserAvatarWrapper != null) {
			// if(!tryGetAvatarFromServer)
			// showUserAvatarWrapper.setNeedTryGerAvatarFromServer(true);
			// else
			// showUserAvatarWrapper.setNeedTryGerAvatarFromServer(false);
			showUserAvatarWrapper.showCahedAvatar();
		}
	}

	/**
	 * 刷新企业通知的显示.
	 * 
	 * @param currUser
	 */
	private void refreshEntNoticeShow(UserEntity currUser) {
		if (!CommonUtils.isStringEmpty(currUser.getElatest_notice_title())) {
			entNoticeGroup.setVisibility(View.VISIBLE);
			final String url = currUser.getElatest_notice_content_url();
			entNotice.setText(currUser.getElatest_notice_title());
				entNotice.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
						if(url.startsWith("http")){
							startActivity(IntentFactory.createCommonWebActivityIntent(getActivity(), url));
						}else{
							//设置无边框 全屏样式
							final Dialog  dialog = new Dialog(getActivity(), R.style.Dialog_Fullscreen); 
							dialog.getWindow().setWindowAnimations(R.style.PopupAnimation); //anim
							dialog.getWindow().setContentView(R.layout.ent_view);           //layout
							TextView  ent_msg = (TextView) dialog.getWindow().findViewById(R.id.ent_msg);
							LinearLayout ent_view = (LinearLayout) dialog.getWindow().findViewById(R.id.ent_view);
							
							ent_view.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									dialog.dismiss();
								}
							});
							
							ent_msg.setText(url);
							dialog.show();
						}
					}
				});
				
		} 
		else 
		{
			entNoticeGroup.setVisibility(View.GONE);
		}
	}
	/**
	 * 刷新企业定制相关图片的显示.
	 * 
	 * @param currUser
	 */
	private void refreshEntImgShow(UserEntity currUser) {
		if (getActivity() != null) {
			// 有网络时才去尝试载入图片
			if (ToolKits.isNetworkConnected(getActivity())) {
				// 载入企业定制的logo图、闪屏图
				ToolKits.loadEntFileImage(getActivity(), entLogo,
						currUser.getUser_id(),
						currUser.getEportal_logo_file_name(),
						ToolKits.dip2px(getActivity(), 134.5f),
						ToolKits.dip2px(getActivity(), 27.5f));
				ToolKits.loadEntFileImage(getActivity(), null,
						currUser.getUser_id(),
						currUser.getEsplash_screen_file_name(), 720, 1280);
			}
		} else
			Log.w(TAG, "getActivity() == null!!!!!!!!!!!!!!");
	}

	/**
	 * 刷新上次同步时存储的电量、最近更新时间.
	 */
	public void refreshTimeAndBattery(String msg) {
		LocalInfoVO vo = PreferencesToolkits.getLocalDeviceInfo(getActivity());
		if (vo != null && vo.syncTime > 0) {
			if(msg!=null){
				
			}
			else
			{
				// 电量低于10%时给于用户提示
				if (provider.isConnectedAndDiscovered() && vo.battery < 10) {
					if (!batteryDialog.isShowing())
						batteryDialog.show();
				}	
			}
			batteryText.setText(MessageFormat.format(ToolKits.getStringbyId(getActivity(), R.string.bracelet_battery), vo.battery));
			Log.i(TAG, "------》电量：" + vo.battery);
			Bitmap bitmap = com.linkloving.rtring_c_watch.utils.ToolKits.getBlueboothPowerLevel(vo.battery / 100.0f, getActivity());
			batteryImg.setImageBitmap(bitmap);
			syncTime.setText(new SimpleDateFormat(ToolKits.getStringbyId(getActivity(), R.string.bracelet_sync_format)).format(new Date(vo.syncTime))+ "");
			
		} else {
			
			batteryText.setText(MessageFormat.format(ToolKits.getStringbyId(getActivity(), R.string.bracelet_battery), vo.battery));
			Bitmap bitmap = com.linkloving.rtring_c_watch.utils.ToolKits.getBlueboothPowerLevel(0f, getActivity());
			batteryImg.setImageBitmap(bitmap);
			syncTime.setText(ToolKits.getStringbyId(getActivity(),R.string.bracelet_sync_none));
		}
	}

	private void showDebugInfo(String s) {
	}

	private void showDebugState(String s, int color) {
	}

	public void bleProcessOver() {
		this.bleProcessing = false;
		_refreshUIWithBLEState();
	}

	/**
	 * 用于本界面在onResume时能及时刷新蓝牙的状态.
	 * <p>
	 * 有时因本界面处于未激活，或者处于其它设置界面时因Observer被替换
	 * 而要能错过了蓝牙状态ui的刷新，所以本方法的目的就是在onResume时及时纠正ui的显示.
	 */
	private void _refreshUIWithBLEState() {
		UserEntity ue = MyApplication.getInstance(getActivity()).getLocalUserInfoProvider();
		if (ue != null) {
			if (this.bleProcessing) {
				
				_refreshUIWithBLEState_noConnect(ToolKits.getStringbyId(getActivity(), R.string.portal_main_state_connecting),new Color().rgb(255, 255, 255));
				
			} else {
				
				Log.i(TAG, "--->设备绑定：" + ue.isBLEBounded());
				Log.i(TAG,"--->设备是否正在连接："+ MyApplication.getInstance(getActivity()).getCurrentHandlerProvider().isConnecting());
				if (CommonUtils.isStringEmpty(ue.getLast_sync_device_id()))// 这 设备未绑定
				{
					// 刷新蓝牙状态UI
					viewBleState.setImageResource(R.drawable.main_fragment_ble_state_icon_undound);
					viewBleDesc.setVisibility(View.VISIBLE);
					viewBleDesc.setText(ToolKits.getStringbyId(getActivity(),R.string.portal_main_state_unbound));
					viewBleDesc.setTextColor(new Color().rgb(222, 0, 0)); // 红色
					batteryImg.setVisibility(View.GONE);
				} else {
					// 设备已连接
					if (MyApplication.getInstance(getActivity()).getCurrentHandlerProvider().isConnectedAndDiscovered()) {
						// 刷新蓝牙状态UI
						viewBleState.setImageResource(R.drawable.main_fragment_ble_state_icon_connected);
						viewBleDesc.setVisibility(View.GONE);
						viewBleDesc.setText(ToolKits.getStringbyId(getActivity(),R.string.portal_main_state_connected));
						batteryImg.setVisibility(View.VISIBLE);   ///////
					}
					// 正在连接
					else if (MyApplication.getInstance(getActivity()).getCurrentHandlerProvider().isConnecting()) {
						_refreshUIWithBLEState_noConnect(ToolKits.getStringbyId(getActivity(), R.string.portal_main_state_connecting),new Color().rgb(255, 255, 255));
					}
					// 设备未连接
					else 
					{
						Log.e(TAG, "--->刷新蓝牙状态UI:未连接！");
						// 刷新蓝牙状态UI
						_refreshUIWithBLEState_noConnect(ToolKits.getStringbyId(getActivity(),R.string.portal_main_state_unconnect),new Color().rgb(222, 195, 17));
					}
				}
			}
		}
	}

	private void _refreshUIWithBLEState_noConnect(String txt, int color) {
		viewBleState.setImageResource(R.drawable.main_fragment_ble_state_icon_unconnect);
		viewBleDesc.setVisibility(View.VISIBLE);
		viewBleDesc.setText(txt);
		viewBleDesc.setTextColor(color);
		batteryImg.setVisibility(View.GONE);
	}

	/**
	 * 刷新界面“运动”、“睡眠”数据.
	 * 
	 * @param date
	 * @param eDate
	 */
	private void refreshViews(Date date, Date eDate,boolean enforceUseOfflineData) {
		if (sportBtn.isChecked()) {
			sportDataWrapper.loadDataAndUpdateView(date, eDate,enforceUseOfflineData);
			sleepDataWrapper.setVisible(false);
			sportDataWrapper.setVisible(true);
		} else {
			sleepDataWrapper.loadAndUpdateView(date, eDate,enforceUseOfflineData);
			sleepDataWrapper.setVisible(true);
			sportDataWrapper.setVisible(false);
		}
	}

	/**
	 * 刷新下接同步提示的相关组件的显示.
	 */
	private void refreshSyncTipShow() {
		if (PreferencesToolkits.isNeedShowFragmentTip(getActivity()))
			layoutOfSyncTip.setVisibility(View.VISIBLE);
		else
			layoutOfSyncTip.setVisibility(View.GONE);
	}

	@Override
	public void onResume() {
		super.onResume();
		if(PreferencesToolkits.get_googlefit(getActivity()) && mGoogleApiClient!=null && !mGoogleApiClient.isConnected()){
			mGoogleApiClient.connect();
		}
		//从设置页面回来 可以重新打开谷歌健康
		if(mGoogleApiClient==null && PreferencesToolkits.get_googlefit(getActivity()) ){
        	buildFitnessClient();
        }
		
		mSettingManager=new SkinSettingManager(getActivity());
		mSettingManager.initSkins();
		
		provider = MyApplication.getInstance(getActivity()).getCurrentHandlerProvider();
		Log.i(TAG, "onResume  provider是空！" + (provider == null));
		provider.setBleProviderObserver(bleProviderObserver);
		// 刷新运动目标的显示，以便当用户从运动目标界面修改完成回来后能及时刷新首页上的目标显示
		this.sportDataWrapper.refreshCircleViewPercent();
		// 用于本界面在onResume时能及时刷新蓝牙的状态. 有时因本界面处于未激活,或者处于其它设置界面时因Observer被替换
		// 而要能错过了蓝牙状态ui的刷新，所以本方法的目的就是在onResume时及时纠正ui的显示
		String last_sync_device_id = MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getLast_sync_device_id();
		
		if (CommonUtils.isStringEmpty(last_sync_device_id)) {
			bleProcessOver();
		} 
		MyApplication.getInstance(this.getActivity()).setObsForDaySynopicsUploadSucess(obsForDaySynopicsUploadSucess);
		MyApplication.getInstance(this.getActivity()).setObsForSportDatasUploadSucess(obsForSportDatasUploadSucess);

		// 及时刷新SNS里的未读数显示
		updateUnReadCount();
		//刷新界面数据
		refreshViews(dateSwitcher.getStartDate(),dateSwitcher.getEndDate(), true);
		//刷新企业通知
		if((System.currentTimeMillis()/1000 - PreferencesToolkits.gettimeEnt(getActivity()) >43200) && ToolKits.isNetworkConnected(getActivity()))
//		if((System.currentTimeMillis()/1000 - PreferencesToolkits.gettime(getActivity()) >30) && ToolKits.isNetworkConnected(getActivity()))
		{
			new UpdateMsgAsyncTask().execute();
		}
		else{
			Log.i(TAG, "onResume  小于30s");
		}
	}

	@Override
	public void onDestroy() {
		// 及时清除此Observer，以便在重新登陆时，正在运行中的蓝牙处理不会因此activity的回收而使回调产生空指针等异常
		provider = MyApplication.getInstance(getActivity()).getCurrentHandlerProvider();
		if (provider != null)
			provider.setBleProviderObserver(null);
		getActivity().unregisterReceiver(blereciver);
		MyApplication.getInstance(this.getActivity()).setObsForDaySynopicsUploadSucess(null);
		MyApplication.getInstance(this.getActivity()).setObsForSportDatasUploadSucess(null);
		// MyApplication.getInstance(this.getActivity()).releaseBLE();
		// 如果有未执行完成的AsyncTask则强制退出之，否则线程执行时会空指针异常哦！！！
		AsyncTaskManger.getAsyncTaskManger().finishAllAsyncTask();

		super.onDestroy();
	}
	/**
	 * 在日期切换后
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQ_PICK_DATE && resultCode == Activity.RESULT_OK) {
//			Date date = IntentFactory.parseDatePickerActivityIntent(data);
//			if (date.getTime() > System.currentTimeMillis()) {
//				WidgetUtils.showToast(getActivity(),
//						getString(R.string.date_picker_out_time),
//						ToastType.INFO);
//			} else {
//				timeFilterUIWrapper.getDateSwitcher().setBaseTime(date);
//				timeFilterUIWrapper.switchedOver();
//			}
		}

		// if(requestCode == REQUEST_CODE_BOUND && resultCode ==
		// Activity.RESULT_CANCELED){
		// MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().setLast_sync_device_id(null);
		// }

		// 绑定设备的回调
		if (requestCode == REQUEST_CODE_BOUND && resultCode == Activity.RESULT_OK) {
			ToolKits.showCommonTosat(getActivity(), true, ToolKits.getStringbyId(getActivity(), R.string.portal_main_bound_success), Toast.LENGTH_LONG);
			_refreshUIWithBLEState();
			MyApplication.syncAllDeviceInfoAuto(getActivity(), false,obsForBerforeConnect);

			provider.onActivityResultProess(requestCode, resultCode, data);
		}
	}

	/**
	 * 刷新SNS中未读数的Ui显示.
	 */
	public void updateUnReadCount() {
		int num = MyApplication.getInstance(getActivity()).getCommentNum();
		if (num > 0) {
			unRead.setVisibility(View.VISIBLE);
			unRead.setText(ToolKits.getUnreadString(num));
		} else {
			unRead.setVisibility(View.GONE);
		}
	}
	/**
	 * 运动数据UI包装实现类.
	 */
	private class SportDataWrapper {
		private TextView steps;
		private TextView calory;
		private TextView distance;
		private TodayCircleView dayTcv;
		private View dayCircleView;
		private TextView goal;
		private TextView stepPercent;
		private LinearLayout dayLinear;

		public SportDataWrapper(View view) {
			initView(view);
			bindListener();
		}

		private void initView(View view) {
			dayCircleView = view.findViewById(R.id.circle_bar);
			steps = (TextView) view.findViewById(R.id.fragment_ble_steps);
			calory = (TextView) view.findViewById(R.id.fragment_ble_calory);
			distance = (TextView) view.findViewById(R.id.fragment_ble_distance);
			if(MyApplication.getInstance(getActivity()).getUNIT_TYPE().equals("Imperial")){
				distance.setText("0.0 "+ ToolKits.getStringbyId(getActivity(), R.string.unit_miles));	
			}else{
				distance.setText("0.0 "+ ToolKits.getStringbyId(getActivity(), R.string.unit_m));	
			}
			dayTcv = (TodayCircleView) dayCircleView
					.findViewById(R.id.fragment_todayCircleView);
			goal = (TextView) dayCircleView
					.findViewById(R.id.activity_sport_data_detail_sleepSumView);
			stepPercent = (TextView) dayCircleView.findViewById(R.id.textView3);
			dayLinear = (LinearLayout) view.findViewById(R.id.fragment_ble_day_linear);
		}

		public void refreshCircleViewPercent() {
			UserEntity userEntity = MyApplication.getInstance(getActivity())
					.getLocalUserInfoProvider();
			if (userEntity != null) {
				goal.setText(MessageFormat.format(ToolKits.getStringbyId(getActivity(), R.string.portal_main_goal), userEntity.getPlay_calory()));
				int goal = Integer.parseInt(userEntity.getPlay_calory());
				float percent = CommonUtils.getIntValue(steps.getText())// vo.steps
						/ ((goal <= 0 ? 1 : goal) * 1.0f);
				dayTcv.setPercent(percent);
				stepPercent.setText(CommonUtils
						.getScaledValue(percent * 100, 0) + "%");
			}
		}
		private void upDateView(LPDeviceInfo deviceInfo, DetailChartCountData count) {
			
			if((int)count.walking_steps!=0 || (int)count.runing_steps!=0)
			{
				if(((int)count.walking_steps+(int)count.runing_steps)==(deviceInfo.dayWalkSteps + deviceInfo.dayRunSteps)){
					
					steps.setText(String.valueOf((deviceInfo.dayWalkSteps + deviceInfo.dayRunSteps)));// + "步");
					if(MyApplication.getInstance(getActivity()).getUNIT_TYPE().equals("Imperial")){
						distance.setText(ToolKits.MChangetoMIRate((deviceInfo.dayRunDistance + deviceInfo.dayWalkDistance))+" "+ ToolKits.getStringbyId(getActivity(), R.string.unit_miles));
					}else{
						distance.setText((deviceInfo.dayRunDistance + deviceInfo.dayWalkDistance)+" "+ ToolKits.getStringbyId(getActivity(), R.string.unit_m));
					}
					
				}else{
					
					steps.setText(String.valueOf((int)count.walking_steps+(int)count.runing_steps));// +
					if(MyApplication.getInstance(getActivity()).getUNIT_TYPE().equals("Imperial")){
						distance.setText(ToolKits.MChangetoMIRate((int)count.walking_distance+(int)count.runing_distance)+" "+ ToolKits.getStringbyId(getActivity(), R.string.unit_miles));	
					}else{
						distance.setText(((int)count.walking_distance+(int)count.runing_distance)+" "+ ToolKits.getStringbyId(getActivity(), R.string.unit_m));	
					}
					
				}
			}
			else{
				steps.setText(String.valueOf((int)count.walking_steps+(int)count.runing_steps));// +
				if(MyApplication.getInstance(getActivity()).getUNIT_TYPE().equals("Imperial")){
					Log.e(TAG, count.walking_distance+"步数........................");
					distance.setText(ToolKits.MChangetoMIRate( (int)count.walking_distance )+" "+ ToolKits.getStringbyId(getActivity(), R.string.unit_miles));	
				}else{
					distance.setText((int)count.walking_distance+" "+ ToolKits.getStringbyId(getActivity(), R.string.unit_m));	
				}
			}
			// 用户体重
			int userWeight = CommonUtils.getIntValue(MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getUser_weight());
			int yValue = _Utils.calculateCalories(deviceInfo.dayRunDistance/ (deviceInfo.dayRunTime * 30.0d),deviceInfo.dayRunTime * 30, userWeight)+ _Utils.calculateCalories(deviceInfo.dayWalkDistance/ (deviceInfo.dayWalkTime * 30.0d),deviceInfo.dayWalkTime * 30, userWeight);
			calory.setText(yValue+" "+ ToolKits.getStringbyId(getActivity(),R.string.unit_cal_big));
			
			UserEntity userEntity = MyApplication.getInstance(getActivity()).getLocalUserInfoProvider();
			if (userEntity != null) {
				goal.setText(MessageFormat.format(ToolKits.getStringbyId(getActivity(), R.string.portal_main_goal), userEntity.getPlay_calory()));
				int goal = Integer.parseInt(userEntity.getPlay_calory());
                if(TimeZoneHelper.getTimeZoneOffsetMinute()==-420){
                	float percent = ((int)count.walking_steps+(int)count.runing_steps)/ ((goal <= 0 ? 1 : goal) * 1.0f);
                	dayTcv.setPercent(percent);
    				stepPercent.setText(CommonUtils.getScaledValue(percent * 100, 0) + "%");
                }else{
                	float percent = (deviceInfo.dayWalkSteps + deviceInfo.dayRunSteps)/( (goal <= 0?1:goal)*1.0f);
                	dayTcv.setPercent(percent);
    				stepPercent.setText(CommonUtils.getScaledValue(percent * 100, 0) + "%");
                }
				
				
			}

			// mScrollView.onRefreshComplete();
		}
		
		private void upDateView(LPDeviceInfo deviceInfo)
		{
			steps.setText(String.valueOf((deviceInfo.dayWalkSteps + deviceInfo.dayRunSteps)));// + "步");
			// 用户体重
			int userWeight = CommonUtils.getIntValue(MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getUser_weight());
			int yValue = _Utils.calculateCalories((int)(deviceInfo.dayRunDistance/ (deviceInfo.dayRunTime * 30.0d)),deviceInfo.dayRunTime * 30, (int) (userWeight*ToolKits.UNIT_LBS_TO_KG))+ _Utils.calculateCalories((int)deviceInfo.dayWalkDistance/ (deviceInfo.dayWalkTime * 30.0d),deviceInfo.dayWalkTime * 30, (int) (userWeight*ToolKits.UNIT_LBS_TO_KG));

			calory.setText(yValue +" "+ ToolKits.getStringbyId(getActivity(), R.string.unit_cal_big));
			
			if(MyApplication.getInstance(getActivity()).getUNIT_TYPE().equals("Imperial")){
			
				distance.setText(ToolKits.MChangetoMIRate((deviceInfo.dayRunDistance + deviceInfo.dayWalkDistance))+" "+ ToolKits.getStringbyId(getActivity(), R.string.unit_miles));
			
			}else{
				distance.setText((deviceInfo.dayRunDistance + deviceInfo.dayWalkDistance)+" "+ ToolKits.getStringbyId(getActivity(), R.string.unit_m));
			}
			
			UserEntity userEntity = MyApplication.getInstance(getActivity()).getLocalUserInfoProvider();
			if(userEntity != null)
			{
				goal.setText(MessageFormat.format(ToolKits.getStringbyId(getActivity(), R.string.portal_main_goal), userEntity.getPlay_calory()));
				int goal = Integer.parseInt(userEntity.getPlay_calory());
				
				float percent = (deviceInfo.dayWalkSteps + deviceInfo.dayRunSteps)/( (goal <= 0?1:goal)*1.0f);
				dayTcv.setPercent(percent);
				stepPercent.setText(CommonUtils.getScaledValue(percent * 100, 0) + "%");
			}
			
		}

		public void setVisible(boolean visible) {
			if (visible) {
				dayCircleView.setVisibility(View.VISIBLE);
				dayLinear.setVisibility(View.VISIBLE);
			} else {
				dayCircleView.setVisibility(View.GONE);
				dayLinear.setVisibility(View.GONE);
			}
		}

		/**
		 * 载入数据.运动
		 * @param date
		 * @param eDate
		 * @param enforceUseOfflineData
		 *            true表示无条件使用本地数据（即使当前网络可用的情况下），否则将智能判断网络是否可用来决定去哪取数据
		 */
		public void loadDataAndUpdateView(final Date date, final Date eDate,final boolean enforceUseOfflineData) {
			
			DataLoadingAsyncTask dat = new DataLoadingAsyncTask<Object, Object, DataFromServer>(getActivity(), false) {  //运动
				
				private boolean online = false;
				private String startDateString = "";
				private String endDateString = "";

				@Override
				protected DataFromServer doInBackground(Object... params1) {
					startDateString = new SimpleDateFormat(com.linkloving.rtring_c_watch.utils.ToolKits.DATE_FORMAT_YYYY_MM_DD).format(date);
					endDateString = new SimpleDateFormat(com.linkloving.rtring_c_watch.utils.ToolKits.DATE_FORMAT_YYYY_MM_DD).format(eDate);
					online = ToolKits.isNetworkConnected(context);
					Log.d(TAG, "查询的开始时间:" + date.toString()+ ",查询的结束时间:" + eDate.toString());
					//返回   SportRecord  
					return HttpHelper.submitQuerySportRecordsToServer_l(getActivity(), startDateString, endDateString,enforceUseOfflineData ? false : online, true);
				}

				@Override
				protected void onPostExecuteImpl(Object arg0) {  // SportRecord  
					
					Log.d(TAG,"=================on circle bar data result===========================");
					
					if (arg0 != null) {
						
						// 原始运动数据  
						List<SportRecord> originalSportDatas = HttpHelper.parseQuerySportRecordsFromServer(getActivity(), arg0, online);
						
						Log.i(TAG, "原始运动数据条数：" + originalSportDatas.size());
						
						// 先将运动明细数据组装成日汇总数据       数据库放入的是SportRecord 但是这里算出来的是DaySynopic
						
						List<DaySynopic> srs = DatasProcessHelper.convertSportDatasToSynopics(originalSportDatas);
						for(DaySynopic daySynopic :srs){
							Log.i(TAG, "daySynopic：" + daySynopic.toString());
							//daySynopic：[data_date=2016-03-01,data_date2=2016-03-01 00:00:00.000,record_id=null,user_id=null,run_duration=120,run_step=225,run_distance=339,
							// create_time=null,work_duration=630,work_step=524,work_distance=439,sleepMinute=45,deepSleepMiute=0]
						}
						
						if (!srs.isEmpty()) {
							
								Log.i(TAG, "TimeZoneHelper.getTimeZoneOffsetMinute()：" + TimeZoneHelper.getTimeZoneOffsetMinute());
								
								DaySynopic theDaySynopic = null;
								for (DaySynopic daySynopic : srs) {
									if (startDateString.equals(daySynopic.getData_date())) {
										
										theDaySynopic = daySynopic;
										break;
										
									} else {
										
										theDaySynopic = null;
									}
								}
								if (theDaySynopic != null)
									
									Log.i(TAG,"theDaySynopic:"+ theDaySynopic.toString());
								
								else
									
									Log.i(TAG, "theDaySynopic是null");
								
								Log.i(TAG,"srs.get(srs.size() - 1):"+ srs.get(srs.size() - 1).toString());
								
								LPDeviceInfo deviceInfo = DeviceInfoHelper.fromDaySynopic(theDaySynopic == null ? srs.get(srs.size() - 1): theDaySynopic);
								if (sportBtn.isChecked())
								upDateView(deviceInfo);
						}
						else {
							upDateView(new LPDeviceInfo(),new DetailChartCountData());
						}
					}
					//
					AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(this);
				}
			};
			// 确保当前只有一个AsyncTask在运行，否则用户恶心切换会OOM
			if (currentRunningSportDataAsync != null)
				AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(currentRunningSportDataAsync, true);
			
			AsyncTaskManger.getAsyncTaskManger().addAsyncTask(currentRunningSportDataAsync = dat);
			dat.execute();
		}

		private void bindListener() {
		}
	}

	/**
	 * 睡眠数据UI包装实现类.
	 */
	private class SleepDataWrapper {
		
		private TodayCircleView nightTcv;
		private View nightCircleView;
		private TextView sleepTime;
		private LinearLayout nightLinear;
		private TextView sleepDeep;
		private TextView sleep;

		public SleepDataWrapper(View view) {
			initView(view);
		}

		private void initView(View view) {
			sleepDeep = (TextView) view.findViewById(R.id.fragment_ble_sleep_deep);
			sleep = (TextView) view.findViewById(R.id.fragment_ble_sleep);
			nightCircleView = view.findViewById(R.id.circle_bar_night);

			nightTcv = (TodayCircleView) nightCircleView.findViewById(R.id.fragment_todayCircleView);
			sleepTime = (TextView) nightCircleView.findViewById(R.id.textView3);
			nightLinear = (LinearLayout) view.findViewById(R.id.fragment_ble_night_linear);
		}

		private void updateView(SleepData sleepInfo) {
			nightCircleView.setVisibility(View.VISIBLE);
			nightLinear.setVisibility(View.VISIBLE);
			double deepSleep = CommonUtils.getScaledDoubleValue(sleepInfo.getDeepSleep(), 1);
			double sleepLight = CommonUtils.getScaledDoubleValue(sleepInfo.getSleep(), 1);
			sleepDeep.setText(deepSleep + "");
			sleep.setText(sleepLight + "");
			double sum = deepSleep + sleepLight;
			nightTcv.setPercent(sleepInfo.getDeepSleep() <= 0 ? 0.0f: ((float) CommonUtils.getScaledDoubleValue(sleepInfo.getDeepSleep() / sum, 1)));
			sleepTime.setText(CommonUtils.getScaledDoubleValue(sum, 1) + "");
			mScrollView.onRefreshComplete();
		}

		public void setVisible(boolean visible) {
			if (visible) {
				nightCircleView.setVisibility(View.VISIBLE);
				nightLinear.setVisibility(View.VISIBLE);
			} else {
				nightCircleView.setVisibility(View.GONE);
				nightLinear.setVisibility(View.GONE);
			}
		}

		/**
		 * 载入数据.睡眠
		 * 
		 * @param date
		 * @param eDate
		 * @param enforceUseOfflineData true表示无条件使用本地数据（即使当前网络可用的情况下），否则将智能判断网络是否可用来决定去哪取数据
		 */
		public void loadAndUpdateView(final Date date,final Date eDate, final boolean enforceUseOfflineData)
		{
			DataLoadingAsyncTask dat = new DataLoadingAsyncTask<Object,Object, DataFromServer>(getActivity(), false)
			{
				private String startDate = null, endDate = null;
				private boolean online = false;
				
				@Override
				protected DataFromServer doInBackground(Object... params1) 
				{
					try
					{
						startDate = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(date); // yyyy-MM-dd
						endDate = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(eDate); //yyyy-MM-dd
						
						online = ToolKits.isNetworkConnected(context);
						//从数据库查出时间
						return HttpHelper.submitReportForDaySleepDataToServer(getActivity(), startDate, endDate, enforceUseOfflineData ? false : online);
					}
					catch (Exception e)
					{
						Log.w(TAG, e.getMessage(), e);
					}
					
					DataFromServer errorD = new DataFromServer();
					errorD.setSuccess(false);
					errorD.setReturnValue(ToolKits.getStringbyId(getActivity(), R.string.portal_main_date_format_error));
					return errorD;
				}

				@Override
				protected void onPostExecuteImpl(final Object arg0) 
				{
					if(arg0 != null)
					{
						DataLoadingAsyncTask a1 = new DataLoadingAsyncTask<Object,Object, DataFromServer>(getActivity(), false)//, "数据计算中...")
						{
							@Override
							protected DataFromServer doInBackground(Object... params11) {
								//经过睡眠算法过后的睡眠数据
								// 先将运动明细数据组装成日汇总数   //主要时间消耗在这里
								SleepData  srs;
								String today = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(new Date())+""; //yyyy-MM-dd
								Log.i(TAG, "date:"+startDate.toString());
								Log.i(TAG, "eDate:"+endDate.toString());
								Log.i(TAG, "today:"+today.toString());
								if(sleepmap.containsKey(startDate)){
									
									srs = sleepmap.get(startDate);
									
								}else{
									srs = HttpHelper.parseReportForDaySleepDataFromServer(getActivity(), startDate, endDate, arg0, online);
									Log.e(TAG, "睡眠数据:getSleep():"+srs.getSleep());              //1.8333333730697632
									Log.e(TAG, "睡眠数据:getDeepSleep():"+srs.getDeepSleep());      //2.5833332538604736
									Log.e(TAG, "睡眠数据:getDate():"+srs.getDate());                // 2016-02-29
									Log.e(TAG, "睡眠数据:getGetupTime():"+srs.getGetupTime());      //时间戳 1456772070
									Log.e(TAG, "睡眠数据:getGotoBedTime():"+srs.getGotoBedTime());  //时间戳 1456756080
									if(today.equals(startDate)){
										Log.e(TAG, "这是今天...");
									}else{
										double zero = 0.0;
										if((srs.getDeepSleep()==zero) && (srs.getSleep()==zero)){
											Log.e(TAG, "睡眠时间都是0");
										}else{
											sleepmap.put(startDate, srs);
										}
									}
										
									
								}
								  
								
								
								DataFromServer dd = new DataFromServer();
								dd.setSuccess(true);
								dd.setReturnValue(srs);
								return dd;
							}

							@Override
							protected void onPostExecuteImpl(Object ddd)
							{
								SleepData  srs = (SleepData)ddd;
								if(srs != null)
								{
									if (sleepBtn.isChecked())
									updateView(srs);//info);
								}
								AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(this);
							}
						};
						AsyncTaskManger.getAsyncTaskManger().addAsyncTask(a1);
						a1.execute();
					}
				}
			};

			// 确保当前只有一个AsyncTask在运行，否则用户恶心切换会OOM
			if(currentRunningSleepDataAsync != null)
				AsyncTaskManger.getAsyncTaskManger().removeAsyncTask(currentRunningSleepDataAsync, true);
			AsyncTaskManger.getAsyncTaskManger().addAsyncTask(currentRunningSleepDataAsync = dat);
			dat.execute();
		}
	}

	/**
	 * 蓝牙观察者实现类.
	 */
	private class BLEProviderObserverAdapterImpl extends
			BLEProviderObserverAdapter {
		protected Activity getActivity() {
			return PortalMainFragment.this.getActivity();
		}
		@Override
		public void updateFor_handleNotEnableMsg() {
			// 一定要调用父类的方法一下，否则蓝牙在关闭时将无法提示用户开启之
			super.updateFor_handleNotEnableMsg();
			Log.i(TAG, "updateFor_handleNotEnableMsg");
			
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    getActivity().startActivityForResult(enableBtIntent, MyApplication.REQUEST_ENABLE_BT);
		}
		
		@Override
		public void updateFor_handleConnectFailedMsg() {
		}

		@Override
		public void updateFor_handleScanTimeOutMsg() {
			Log.e(TAG, "updateFor_handleScanTimeOutMsg");
		}

		@Override
		public void updateFor_handleUserErrorMsg(int id) {
		}

		@Override
		public void updateFor_handleConnectSuccessMsg() {
			LocalInfoVO vo = PreferencesToolkits.getLocalDeviceInfo(getActivity());
			vo.battery=0;
			String msg="ConnectSuccess";
			refreshTimeAndBattery(msg);
		}

		@Override
		public void updateFor_handleConnecting() {
			Log.i(TAG, "updateFor_handleConnecting");
			bleProcessOver();
		}

		@Override
		public void updateFor_handleConnectLostMsg() {
			Log.i(TAG, "updateFor_handleConnectLostMsg");
			bleProcessOver();
		}

		@Override
		public void updateFor_notifyForDeviceUnboundSucess_D() {
			AlertDialog dialog = new AlertDialog.Builder(getActivity())
					.setTitle(ToolKits.getStringbyId(getActivity(),R.string.bracelet_unbound))
					.setNegativeButton(ToolKits.getStringbyId(getActivity(),R.string.general_yes),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,int which) {
									dialog.dismiss();
								}
							}).create();
			dialog.show();
			bleProcessOver();
		}

		@Override
		public void updateFor_notifyForSetBodySucess() {
			super.updateFor_notifyForSetBodySucess();
		}
		
		

		@Override
		public void updateFor_notifyForModelName(LPDeviceInfo latestDeviceInfo) {
			
			//检查固件更新
			if(!CommonUtils.isStringEmpty(MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getLast_sync_device_id())){
				Log.e(TAG, "检查是否更新......");
				if((System.currentTimeMillis()/1000)-PreferencesToolkits.gettime(getActivity())>86400){
					if(provider.isConnectedAndDiscovered())
						new UntreatedAsyncTask().execute();
				}
				
			} 
		}
		// ------------------------------------------------------------------------------------------
		/** 通知：设备绑定信息写到设备固件中完成 */
		@Override
		public void updateFor_boundInfoSetToDeviceOK() {
			showDebugInfo(ToolKits.getStringbyId(getActivity(),
					R.string.portal_main_debug_bound_success));
		}

		/** 通知：设备绑定信息同步到服务端完成 */
		@Override
		public void updateFor_boundInfoSyncToServerFinish(
				Object resultFromServer) {
			if (resultFromServer != null) {
				if (((String) resultFromServer).equals("1")) {
					showDebugInfo(ToolKits.getStringbyId(getActivity(),R.string.portal_main_debug_bound_to_server_success));
				} else if (((String) resultFromServer).equals("2")) {
					showDebugInfo(ToolKits.getStringbyId(getActivity(),R.string.portal_main_debug_bound_to_server_failed));
					Toast.makeText(getActivity(),ToolKits.getStringbyId(getActivity(),R.string.portal_main_bound_failed) + "！！！！",Toast.LENGTH_LONG).show();
				}
				// FIXMEE BUG:此种情况下,意味着该用户已在其它设备上重复登陆并成功绑定了设备.
				// 此时本机绑定过程已近完成(绑定数据已写入到刚才的这台设备),
				// 那么我们应该通知设备回滚刚才的绑定(即把刚才写入的绑定信息摸掉),
				// 但理论上讲此指令无法保证百分非执行成功,那么失败的情况下
				// 此设备将永远无法再绑定新用户了
				else if (((String) resultFromServer).equals("0")) {
					showDebugInfo(ToolKits.getStringbyId(getActivity(),
							R.string.portal_main_debug_bound_to_server_failed));
					// 尽最大努力回滚绑定
					provider.unBoundDevice(getActivity());

					new com.eva.android.widgetx.AlertDialog.Builder(
							getActivity())
							.setTitle(getActivity().getResources().getString(R.string.general_prompt))
							.setMessage(ToolKits.getStringbyId(getActivity(),R.string.portal_main_has_bound))
							.setPositiveButton(getActivity().getResources().getString(R.string.general_ok), null).show();
				}
				// 最后一种情况，即返回结果是该设备已被绑定的账号的邮箱（即登陆账号）
				else {
					new com.eva.android.widgetx.AlertDialog.Builder(
							getActivity())
							.setTitle(getActivity().getResources().getString(R.string.general_prompt))
							.setMessage(MessageFormat.format(ToolKits.getStringbyId(getActivity(),R.string.portal_main_has_bound_other),(String) resultFromServer))
							.setPositiveButton(getActivity().getResources().getString(R.string.general_ok), null).show();
				}
			} else {
				showDebugInfo(ToolKits.getStringbyId(getActivity(),R.string.portal_main_debug_bound_to_server_failed_unknow));
				Log.e(TAG, "boundAsyncTask result is null!!!!!!!!!!!!!!!!!");
			}

			//
			_refreshUIWithBLEState();
		}

		/**
		 * 本轮运动数据完全读取完成的消息（此消息不带任何数据，仅用于通知）。
		 */
		public void updateFor_handleDataEnd() {
			Log.d(TAG, "【NEW运动数据】收到读取完成通知：运动数据全部读取完成并保存到本地成功！");
			// 本次运动数据全部读取完成后要读取未上传的数据并尝试回填睡眠状态
			new AsyncTask<Object, Integer, List<SportRecord>>() {
				@Override
				protected List<SportRecord> doInBackground(Object... params) {
					// 看看数据库中有多少未同步（到服务端的数据）
					List<SportRecord> upList = UserDeviceRecord.findHistoryWitchNoSync(getActivity(),MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getUser_id());
					Log.e(TAG, "【NEW运动数据】收到读取完成通知：已找到本地数据库中有" + upList.size()+ "条未同步（到服务端数据）.");
					for(SportRecord record:upList)
						Log.e(TAG, "【NEW运动数据】具体明细数据：" + record.toString()+ "");
					return upList;
				}

				@Override
				protected void onPostExecute(List<SportRecord> result) {
					if (result != null && result.size() > 0) {
						// 先刷新界面显示
						showDebugInfo(MessageFormat.format(ToolKits.getStringbyId(getActivity(),R.string.portal_main_debug_save_sport_data_success),result.size()));

						// 同步完成后即时刷新数据（注意：此时是用离线数据刷新即可，省得体验不好，老是loading。。。）
						refreshViews(dateSwitcher.getStartDate(),dateSwitcher.getEndDate(), true);

						if (ToolKits.isNetworkConnected(getActivity()))  //将本地数据上传到网络
							// 离线数据保存成功后，同时尝试启动数据同步线程
							new SportDataSubmitServerAsyncTask(getActivity(),obsForSportDatasUploadSucess, result).execute();
						else
							Log.d(TAG,"【NEW运动数据】收到读取完成通知：[网络不可用]此情况下离线运动数据不需要尝试同步到服务端.");
						
					}
				}
			}.execute();
		}


		@Override
		public void updateFor_handleSetTime() {
			bleProcessOver();
		}
		
		@Override
		public void updateFor_notifyFor0x13ExecSucess_D(LPDeviceInfo latestDeviceInfo) {
			 Log.i(TAG,"PortalMainFragment里面deviceInfo--->"+latestDeviceInfo.toString());
			timeFilterUIWrapper.switchedOver();
			super.updateFor_notifyFor0x13ExecSucess_D(latestDeviceInfo);
		}
	}
	/**
	 * 实现GPS定位回调监听。
	 */
	private class MyLocationListener implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			if (getActivity() != null) {
				// 启动免登陆验证和版本检查
				// TODO 以后再优化只在免登陆时需要验证用户（否则当然不需要了），现在因版本检查的存在重复验证也没什么问题，先这样吧！
				LoginInfoExtension ai = new LoginInfoExtension();

				Log.e(TAG,
						"【你好中国】MyApplication。getintance="
								+ MyApplication.getInstance(null)
								+ ", hasecode="
								+ MyApplication.getInstance(null).hashCode());
				Log.e(TAG, "【你好中国】getActivity()=" + getActivity());

				String login_name = PreferencesToolkits.getLocalUserInfo(MyApplication.getInstance(null)).getUser_mail();
				ai.setLoginName(login_name);
				ai.setLoginPsw(PreferencesToolkits.getLoginPswByLoginName(
						MyApplication.getInstance(null), login_name));
				if (location.getLongitude() != 0 && location.getLatitude() != 0) {
					ai.setLongitude(location.getLongitude());
					ai.setLatitude(location.getLatitude());
				}
				// 只在网络可用时
				if (ai != null&& MyApplication.getInstance(getActivity()).isLocalDeviceNetworkOk()) {
					ai.setClientVersion(String.valueOf(LoginActivity.getAPKVersionCode(MyApplication.getInstance(null))));
					// 当最新用户信息读取回来时要通知的观察者
					Observer obsForUserInfoFetchSucess = new Observer() {
						@Override
						public void update(Observable observable, Object data) {
							if (data != null) {
								UserEntity latestUE = (UserEntity) data;
								// 刷新企业通知的显示
								refreshEntNoticeShow(latestUE);
								// 刷新企业相关定制图片的显示
								refreshEntImgShow(latestUE);
							}
						}
					};

					// 后台启动客户端版本检查和更新线程
					new UpdateClientAsyncTask(getActivity(), ai,obsForUserInfoFetchSucess) {
						@Override
						protected void relogin() {
							LoginActivity.relogin(PortalMainFragment.this.getActivity());
						}
					}.execute();

				}

				mLocationClient.stop();
				// // Receive Location
				
			} else {
				Log.w(TAG,
						"MyLocationListener的onReceiveLocation()里，getActivity()==null!!!!!!!!!!");
				mLocationClient.stop();
			}
		}
	}
	
    
    /**
     * Create and return a {@link DataSet} of step count data for the History API.
     */
    //efeelink@sz
    private void insertFitnessData(GooglefitDate google_date,DataSet dataSet) {
        Log.i(TAG, "Creating a new data insert request");
//        DataSet dataSet = null;
        
//        for(int position = 0;position<google_date.size();position++){

            // Create a data set
            int stepCountDelta = google_date.getStep(); 
            long start_time = google_date.getStart_time(); 
            long end_time   = google_date.getEnd_time(); 
            Log.e("GoogleApiClient","start_time:"+start_time+"---end_time:"+end_time+"----stepCountDelta:"+stepCountDelta);
            // For each data point, specify a start time, end time, and the data value -- in this case,
            // the number of new steps.                                       //此次数据开始时间                               //此次数据结束时间       
            DataPoint dataPoint = dataSet.createDataPoint().setTimeInterval(start_time, end_time , TimeUnit.MILLISECONDS);
            dataPoint.getValue(Field.FIELD_STEPS).setInt(stepCountDelta);
            dataSet.add(dataPoint);
//        }
//      [END build_insert_data_request]
    }
	
	/**
	 * 上传数据到google-fit的异步线程
	 * 
	 * @author cherry
	 * efeelink@sz
	 */
	protected class GoogleAsyncTask extends AsyncTask<List<GooglefitDate>, Integer, Boolean>
	{
		@Override
		protected Boolean doInBackground(List<GooglefitDate>... params) {
			 //First, create a new dataset and insertion request.
			List<GooglefitDate> googlefitDates = params[0];
			DataSet dataSet=null; //SportRecord!SportData [state=6, begin=2016-02-24 01:23:58, duration=7200, steps=0, distance=0, localDate=2016-02-24]
			 // Create a data source
            DataSource dataSource_step = new DataSource.Builder()
                    .setAppPackageName(getActivity())
                    .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                    .setName("Fitincluded"+ " - step count") 
                    .setType(DataSource.TYPE_RAW)  //一个数据源
                    .build();
            dataSet = DataSet.create(dataSource_step);
            
            for(int i = 0;i<googlefitDates.size();i++){
            	 insertFitnessData(googlefitDates.get(i),dataSet);
            }
            Log.i("GoogleApiClient", "start Inserting the 【"+googlefitDates.size()+"】 条 dataset in the History API");
            com.google.android.gms.common.api.Status insertStatus = Fitness.HistoryApi.insertData(mGoogleApiClient, dataSet).await(1, TimeUnit.MINUTES);
            // Before querying the data, check to see if the insertion succeeded.
            if (!insertStatus.isSuccess()) {
                 Log.e("GoogleApiClient", "There was a problem inserting the dataset.");
                 return false;
            }
            // At this point, the data has been inserted and can be read.
            Log.i("GoogleApiClient", "【"+googlefitDates.size()+"】 条  Data insert was successful!");
//             Log.i("GoogleApiClient", "【"+(i+1)+"】 条  为:"+googlefitDates.get(i).toString());
//          }
			return true;
		}

//		@Override
//		protected void onPostExecute(Boolean result) {
//			super.onPostExecute(result);
//			if((boolean)result){
//				List<SportRecord> upList = UserDeviceRecord.findHistoryWitchNoSyncGoogle(getActivity(),MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getUser_id());
//				if(upList != null && upList.size() > 0)
//				{
//					Log.d(TAG, "运动数据上传google成功！！！！");
//					// 离线数据同步完成后：标识数据库的记录行的“已同步”为1
//					String startTime = upList.get(0).getStart_time();
//					String endTime = upList.get(upList.size()-1).getStart_time();
//					long sychedNum = UserDeviceRecord.updateForSyncedGoogle(getActivity(), MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getUser_id(), startTime, endTime);
//					Log.d(TAG, "【NEW离线数据同步】本次共有"+sychedNum+"条运动数据已被标识为\"已同步\"！["+startTime+"~"+endTime+"]");
//				}
//			}
//		}
		
	}
	/**
	 * 更新企业通知
	 * @author cherry
	 * 
	 */
	protected class UpdateMsgAsyncTask extends DataLoadingAsyncTask<Void, Integer, DataFromServer>
	{

		public UpdateMsgAsyncTask()
		{
			super(getActivity(),false);
		}

		@Override
		protected DataFromServer doInBackground(Void... params)
		{
			JSONObject obj = new JSONObject();
			obj.put("user_id", MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getUser_id());
			return HttpServiceFactory4AJASONImpl.getInstance()
					.getDefaultService()
					.sendObjToServer(DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_LOGIC)
									.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER)
									.setActionId(SysActionConst.ACTION_VERIFY)
									.setNewData(obj.toJSONString()));
		}
		@Override
		protected void onPostExecuteImpl(Object result) {
			if(result!=null){
				UserEntity userEntity = JSON.parseObject((String) result, UserEntity.class);
				MyApplication.getInstance(context).setLocalUserInfoProvider(userEntity);
				// 刷新企业通知的显示
				refreshEntNoticeShow(userEntity);
				PreferencesToolkits.savetimeEnt(getActivity(), System.currentTimeMillis()/1000);
			}
		}
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
			super(getActivity(), getString(R.string.general_submitting));
		}

		@Override
		protected DataFromServer doInBackground(Void... params)
		{
			LocalInfoVO vo =  PreferencesToolkits.getLocalDeviceInfo(context);
			JSONObject obj = new JSONObject();
			obj.put("device_type", OwnBraceletActivity.Device_Type_Watch);
			obj.put("firmware_type", OwnBraceletActivity.DEVICE_VERSION_TYPE);
			int version_int = OwnBraceletActivity.makeShort(vo.version_byte[1],vo.version_byte[0]);
			obj.put("version_int", version_int+"");  
			obj.put("model_name", vo.modelName); 
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
			PreferencesToolkits.savetime(getActivity(), (System.currentTimeMillis()/1000));
			if(result!=null){
			   if(!("").equals(result.toString())){
				   	String json=result.toString();
					Log.i(TAG, "json:"+json);
					JSONObject jsonObject = JSONObject.parseObject(json);
					String version_code=jsonObject.getString("max_version_code");
					LocalInfoVO vo = PreferencesToolkits.getLocalDeviceInfo(getActivity());
					if(!(Integer.parseInt(version_code, 16)<=Integer.parseInt(vo.version, 16))){  
						//弹框
						showdialog();
						//保存时间
					} 
			   }
			}
		}
	}

    private void showdialog()
	{
    	if(!dialog_oad.isShowing())
    		dialog_oad.show();
	}
}
