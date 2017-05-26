package com.linkloving.rtring_c_watch.logic.main;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eva.android.platf.std.AutoUpdateDaemon;
import com.eva.android.widget.ActivityRoot;
import com.eva.android.widget.DataLoadingAsyncTask;
import com.eva.android.widgetx.AlertDialog;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.eva.epc.core.dto.SysActionConst;
import com.example.android.bluetoothlegatt.BLEHandler.BLEProviderObserverAdapter;
import com.example.android.bluetoothlegatt.BLEListHandler;
import com.example.android.bluetoothlegatt.BLEListProvider;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.example.android.bluetoothlegatt.wapper.BLEWapper;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.linkloving.rtring_c_watch.logic.main.slidemenu.CommonAdapter;
import com.linkloving.rtring_c_watch.utils.HttpHelper;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.JobDispatchConst;
import com.rtring.buiness.logic.dto.UserEntity;

public class BLEListActivity extends ActivityRoot 
{
	public static final int REFRESH = 0x123;
	
	private BLEListProvider listProvider;
	private BLEProvider provider;
	private BLEListHandler handler;
	
	
	
	private int sendcount=0;
	private int button_txt_count = 40;
	private Object[]  button_txt={button_txt_count};
	private  Timer timer;
	
	private ListView mListView;
	private List<DeviceVO> macList = new ArrayList<DeviceVO>();
	
	private macListAdapter mAdapter ;
	
	private AlertDialog dialog_connect;
	private AlertDialog dialog_bound;
	
	private Button backBtn;
	private Button refresh;
	
	private Button btn_cancle;
	
	public static final int RESULT_OTHER = 1000;
	public static final int RESULT_BACK = 999;
	public static final int RESULT_FAIL = 998;
	public static final int RESULT_NOCHARGE = 997;
	public static final int RESULT_DISCONNECT = 996;
	
	private BLEProviderObserverAdapter observerAdapter=null;
			
     @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_ble_list);
    	
    	observerAdapter=new BLEProviderObserver();
    	provider = MyApplication.getInstance(this).getCurrentHandlerProvider();
    	handler = new BLEListHandler(BLEListActivity.this) 
    	{
			
			@Override
			protected void handleData(BluetoothDevice device) 
			{
				
				for(DeviceVO v:macList)
				{
					if(v.mac.equals(device.getAddress()))
					     return;
				}
				DeviceVO vo = new DeviceVO();
				vo.mac = device.getAddress();
				vo.name = device.getName();
				vo.bledevice=device;
				macList.add(vo);
				mAdapter.notifyDataSetChanged();
			}
		};
		
    	
    	listProvider = new BLEListProvider(this,BLEWapper.getInstence() , handler);
    	mAdapter = new macListAdapter(this, macList);
    	initView();
    	listProvider.scanDeviceList();
    	
    }
     
     private void initView()
     {
    	 mListView = (ListView) findViewById(R.id.ble_list);
    	 mListView.setAdapter(mAdapter);
    	 mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) 
			{
				provider.setCurrentDeviceMac(macList.get(index).mac);
				provider.setmBluetoothDevice(macList.get(index).bledevice);
				
				LayoutInflater inflater = getLayoutInflater();
				final View layout = inflater.inflate(R.layout.activity_bound_step3, (LinearLayout) findViewById(R.id.boundxiangxi));
				
				btn_cancle = (Button) layout.findViewById(R.id.btncancle);
				dialog_bound = new AlertDialog.Builder(BLEListActivity.this)
//				.setTitle(ToolKits.getStringbyId(BLEListActivity.this, R.string.bound_title))
				.setTitle("Bounding...")
				.setView(layout)
				.setOnKeyListener(new OnKeyListener() {
					
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
						 if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0)
				            {
							 	return true;
				            }
				            else
				            {
				            	return false;
				            }
					}
				})
				.setCancelable(false).create();
				button_txt[0] = button_txt_count;
				
				btn_cancle.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog_bound.dismiss();
						provider.clearProess();
						MyApplication.getInstance(BLEListActivity.this).releaseBLE();

				    	setResult(RESULT_BACK);
				    	finish();
					}
				});
				
				provider.connect();
				Log.i("BLEListActivity", "start to Bound...");
				dialog_connect = new AlertDialog.Builder(BLEListActivity.this).setTitle(ToolKits.getStringbyId(BLEListActivity.this, R.string.portal_main_bound_title))
						.setMessage(ToolKits.getStringbyId(BLEListActivity.this, R.string.portal_main_bounding_wait))
						.setOnKeyListener(new OnKeyListener() {
									@Override
									public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
										 if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0)
								            {
											 	return true;
								            }
								            else
								            {
								            	return false;
								            }
									}
								})
						.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog_, int which) {
								provider.clearProess();
								MyApplication.getInstance(BLEListActivity.this).releaseBLE();
								dialog_connect.dismiss();
								setResult(RESULT_BACK);
								finish();
							}
						}).setCancelable(false)
						.create();
					dialog_connect.show();
			}
		});
    	 
    	 backBtn = (Button) findViewById(R.id.button1);
    	 backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setResult(RESULT_BACK);
				provider.clearProess();
				MyApplication.getInstance(BLEListActivity.this).releaseBLE();
				finish();
			}
		});
    	 
    	 refresh = (Button) findViewById(R.id.refresh);
    	 refresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				macList.clear();
				mAdapter.notifyDataSetChanged();
				listProvider.scanDeviceList();
			}
		});
     }
     
     class DeviceVO
     {
    	 public String mac;
    	 public String name;
    	 public BluetoothDevice bledevice;
     }
     
     class macListAdapter extends CommonAdapter<DeviceVO>
     {
    	 public class ViewHolder
    	 {
    		 public TextView mac;
    		 public TextView name;
    	 }
    	 ViewHolder holder;

		public macListAdapter(Context context, List<DeviceVO> list) {
			super(context, list);
		}

		@Override
		protected View noConvertView(int position, View convertView,
				ViewGroup parent) {
			convertView = inflater.inflate(R.layout.list_item_ble_list, parent,false);
		   holder = new ViewHolder();
			holder.mac = (TextView) convertView.findViewById(R.id.activity_sport_data_detail_sleepSumView);
			holder.name = (TextView) convertView.findViewById(R.id.textView2);
			convertView.setTag(holder);
			return convertView;
		}

		@Override
		protected View hasConvertView(int position, View convertView,ViewGroup parent) {
			holder = (ViewHolder) convertView.getTag();
			return convertView;
		}

		@Override
		protected View initConvertView(int position, View convertView,
				ViewGroup parent) {
			holder.mac.setText("MAC address：     "+list.get(position).mac.substring(list.get(position).mac.length()-5, list.get(position).mac.length()));
			holder.name.setText(list.get(position).name);
			return convertView;
		}
    	 
     }

 	Runnable butttonRunnable = new Runnable()
 	{
 		@Override
 		public void run()
 		{
 			Message msg = new Message();
 			msg.what = REFRESH;
 			boundhandler.sendMessage(msg);
 		};
 	};
 	
 	
 	Runnable boundRunnable = new Runnable()
 	{
 		@Override
 		public void run()
 		{
 			Message msg = new Message();
 			msg.what = 0x333;
 			boundhandler.sendMessage(msg);
 		};
 	};
    
 	Handler boundhandler = new Handler()
 	{
 		public void handleMessage(Message msg)
 		{
 			switch (msg.what)
 			{
 			case 0x333:
 				provider.requestbound_recy(BLEListActivity.this);
 				break;
 			case REFRESH:
 				button_txt[0] = button_txt_count;
 			    String second_txt = MessageFormat.format($$(R.string.bound_scan_sqr), button_txt);
 			    btn_cancle.setText(second_txt);
 				if( button_txt_count ==0 ){
 					if (dialog_bound != null && dialog_bound.isShowing()){
 						if(timer!=null)
 							timer.cancel();
 						dialog_bound.dismiss();
 					}
 					setResult(RESULT_FAIL);
 					finish();
 				}
 				break;
 			}
 		};
 	};

     
     private class BLEProviderObserver extends BLEProviderObserverAdapter{
		
		@Override
		public void updateFor_handleSendDataError() {
			super.updateFor_handleSendDataError();
			provider.clearProess();
			setResult(RESULT_DISCONNECT);
			finish();
		}

		@Override
		public void updateFor_handleConnectLostMsg() {
			Log.i("BLEListActivity", "updateFor_handleConnectLostMsg");
			provider.clearProess();
			sendcount = 0;
			if( dialog_connect!=null && dialog_connect.isShowing())
				dialog_connect.dismiss();
			
			if( dialog_bound!=null && dialog_bound.isShowing()){
				if(timer!=null)
					timer.cancel();
				dialog_bound.dismiss();
			}
			setResult(RESULT_DISCONNECT);
			finish();
		}

		@Override
		public void updateFor_handleConnectSuccessMsg() {
			Log.i("BLEListActivity", "updateFor_handleConnectSuccessMsg");
			sendcount = 0;
			try {
				new Thread().sleep(150);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			provider.requestbound_fit(BLEListActivity.this);
		}
		
		@Override
		public void updateFor_BoundContinue() {
			super.updateFor_BoundContinue();
			Log.i("BLEListActivity", "updateFor_BoundContinue");
			
			if( dialog_connect!=null && dialog_connect.isShowing())
				dialog_connect.dismiss();
			
			if(!dialog_bound.isShowing()){
				dialog_bound.show();
				if(dialog_bound!=null &&  dialog_bound.isShowing()){
					timer = new Timer(); // 每分钟更新一下蓝牙状态
					timer.schedule(new TimerTask()
					{
						@Override
						public void run()
						{
							boundhandler.post(butttonRunnable);
							button_txt_count-- ;
							if(button_txt_count < 0){
								timer.cancel();
							}
						}
					}, 0, 1000);
				}
			}
			
			if(sendcount<15){
				boundhandler.postDelayed(boundRunnable , 2000);
				sendcount++;
			}else{
				Log.e("BLEListActivity", "releaseBLE()--sendcount:"+sendcount);
				MyApplication.getInstance(BLEListActivity.this).releaseBLE();
				setResult(RESULT_FAIL);
				finish();
			}
		}
		@Override
		public void updateFor_BoundSucess() {
			if( dialog_bound!=null && dialog_bound.isShowing()){
				if(timer!=null)
					timer.cancel();
				dialog_bound.dismiss();
			}
			
			dialog_connect.setMessage(ToolKits.getStringbyId(BLEListActivity.this,R.string.portal_main_server));
			dialog_connect.show();
			startBound();
		}
		
		@Override
		public void updateFor_BoundFail() {
			Log.e("BLEListActivity", "updateFor_BoundFail");
			if( dialog_connect!=null && dialog_connect.isShowing())
				dialog_connect.dismiss();
			
			if( dialog_bound!=null && dialog_bound.isShowing()){
				if(timer!=null)
					timer.cancel();
				dialog_bound.dismiss();
			}
			MyApplication.getInstance(BLEListActivity.this).releaseBLE();
			setResult(RESULT_FAIL);
			finish();
		}

		@Override
		public void updateFor_BoundNoCharge() {
			super.updateFor_BoundNoCharge();
			Log.e("BLEListActivity", "updateFor_BoundNoCharge");
			if( dialog_connect!=null && dialog_connect.isShowing())
				dialog_connect.dismiss();
			
			if( dialog_bound!=null && dialog_bound.isShowing()){
				if(timer!=null)
					timer.cancel();
				dialog_bound.dismiss();
			}
			setResult(RESULT_NOCHARGE);
			finish();
		}

		@Override
		public void updateFor_boundInfoSyncToServerFinish(Object resultFromServer) {
			if (resultFromServer != null) {
				if (((String) resultFromServer).equals("1")) {
					//绑定成功！
					// 此处为空即表示是首次绑定并收到反馈数据
					if( dialog_bound!=null && dialog_bound.isShowing())
						dialog_bound.dismiss();
					if( dialog_connect!=null && dialog_connect.isShowing())
						dialog_connect.dismiss();
					if(provider != null) provider.setBleProviderObserver(null);
					Log.e("bangd", "success");
					setResult(Activity.RESULT_OK);
					finish();
				} else if (((String) resultFromServer).equals("2")) {
					//绑定失败
					Toast.makeText(BLEListActivity.this,ToolKits.getStringbyId(BLEListActivity.this,R.string.portal_main_bound_failed) + "！！！！",Toast.LENGTH_LONG).show();
				}
				// FIXMEE BUG:此种情况下,意味着该用户已在其它设备上重复登陆并成功绑定了设备.
				// 此时本机绑定过程已近完成(绑定数据已写入到刚才的这台设备),
				// 那么我们应该通知设备回滚刚才的绑定(即把刚才写入的绑定信息摸掉),
				// 但理论上讲此指令无法保证百分非执行成功,那么失败的情况下
				// 此设备将永远无法再绑定新用户了
				else if (((String) resultFromServer).equals("0")) {
					if( dialog_connect!=null && dialog_connect.isShowing())
						dialog_connect.dismiss();
					// 尽最大努力回滚绑定
					provider.unBoundDevice(BLEListActivity.this);
					new com.eva.android.widgetx.AlertDialog.Builder(BLEListActivity.this)
							.setTitle(BLEListActivity.this.getResources().getString(R.string.general_prompt))
							.setMessage(ToolKits.getStringbyId(BLEListActivity.this,R.string.portal_main_has_bound))
							.setPositiveButton(BLEListActivity.this.getResources().getString(R.string.general_ok),new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog1, int which) {
									dialog1.dismiss();
									setResult(RESULT_OTHER);
									finish();
								}
							}).show();
				}
				// 最后一种情况，即返回结果是该设备已被绑定的账号的邮箱（即登陆账号）
				else {
					if( dialog_connect!=null && dialog_connect.isShowing())
						dialog_connect.dismiss();
					new com.eva.android.widgetx.AlertDialog.Builder(BLEListActivity.this)
							.setTitle(BLEListActivity.this.getResources().getString(R.string.general_prompt))
							.setMessage(MessageFormat.format(ToolKits.getStringbyId(BLEListActivity.this,R.string.portal_main_has_bound_other),(String) resultFromServer))
							.setPositiveButton(BLEListActivity.this.getResources().getString(R.string.general_ok),new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog1, int which) {
									dialog1.dismiss();
									setResult(RESULT_OTHER);
									finish();
								}
							}).show();
					
				}
			} else {
				Log.e("BLEListActivity", "boundAsyncTask result is null!!!!!!!!!!!!!!!!!");
			}
		}

		@Override
		protected Activity getActivity() {
			return BLEListActivity.this;
		}
		
		@Override
		public void updateFor_handleNotEnableMsg() {
			super.updateFor_handleNotEnableMsg();
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			BLEListActivity.this.startActivityForResult(enableBtIntent, MyApplication.REQUEST_ENABLE_BT);
		}
		
    	 
     }
     
     
     
     
     @Override
 	protected void onResume() {
 		super.onResume();
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
     
     
     
 	private void startBound() {
		// 绑定设备时必须保证首先从服务端取来标准UTC时间，以便给设备校时(要看看网络是否连接)
		if (ToolKits.isNetworkConnected(BLEListActivity.this)) {

			new DataLoadingAsyncTask<String, Integer, DataFromServer>(BLEListActivity.this, ToolKits.getStringbyId(BLEListActivity.this, R.string.portal_main_timing)) {
				/**
				 * 在后台执行 {@link doLogin()}实现登陆信息的提交和处于结果的读取 .
				 * 
				 * @param parems
				 *            外界传进来的参数
				 * @return 查询结果，将传递给onPostExecute(..)方法
				 */
				@Override
				protected DataFromServer doInBackground(String... params) {
					UserEntity ue = MyApplication.getInstance(context).getLocalUserInfoProvider();
					String user_id = ue.getUser_id();
					if (ue != null && provider != null && provider.getCurrentDeviceMac() != null) {
						// 将用户id 和 MAC地址交到服务端进行匹配
						return HttpHelper.submitGetServerUTCAndBoundedToServer(user_id,provider.getCurrentDeviceMac());
					} else {
						DataFromServer dfs = new DataFromServer();
						dfs.setSuccess(false);
						dfs.setReturnValue(ToolKits.getStringbyId(BLEListActivity.this,R.string.portal_main_timing_failed));
						setResult(RESULT_OTHER);
						finish();
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
				protected void onPostExecuteImpl(Object result) {
					// 真正的绑定前，会从服务端拿到2个东西：1是该mac是否已被其它用户绑定、2是服务端的UTC
					Object[] rets = HttpHelper.pareseServerUTCAndBounded(result);
					if (rets != null && rets.length == 2) {
						long serverUTC = (Long) rets[0];
						String boundedMail = (String) rets[1];
						// 如果该手环已被其它账号绑定，则本次绑定过程不能继续
						if (boundedMail != null) {
							new com.eva.android.widgetx.AlertDialog.Builder(context)
									.setTitle(context.getResources().getString(R.string.general_faild))
									.setMessage(MessageFormat.format(context.getResources().getString(R.string.portal_main_be_bounded_hint),boundedMail))// context.getResources().getString(R.string.login_form_exit_app_tip))
									.setPositiveButton(context.getResources().getString(R.string.general_ok),new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog1, int which) {
											dialog1.dismiss();
											setResult(RESULT_OTHER);
											finish();
										}
									})
									.show();
						} else {
							// 发送MAC地址到服务端，服务端绑定设备
							final String macString = provider.getCurrentDeviceMac();
							Log.d("BleActivity","current Device Mac address........"+ macString);
							// 提交绑定数据到服务端（放在全局唯一的handler中实现是为了保证不因Observer的切换而受影响，从而保证可靠性
							// ，否则在用户的角度看来，就是为何在切换到其它界面时（实现上过程可能要一段时间，而在段时间因切走了）绑定
							// 却莫名其妙地无法成功 ）
							new DataLoadingAsyncTask<String, Integer, DataFromServer>(BLEListActivity.this, false) {
								/**
								 * 在后台执行 {@link doLogin()}实现登陆信息的提交和处于结果的读取
								 * .
								 * 
								 * @param parems
								 *            外界传进来的参数
								 * @return 查询结果，将传递给onPostExecute(..)方法
								 */
								@Override
								protected DataFromServer doInBackground(String... params) {

									String code = params[0];
									JSONObject obj = new JSONObject();
									obj.put("user_id", MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id());
									obj.put("bounded_device_id", code);
									return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(
													DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_LOGIC)
															.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER)
															.setActionId(SysActionConst.ACTION_APPEND6)
															.setNewData(obj.toJSONString()));
								}

								/**
								 * 处理服务端返回的登陆结果信息.
								 * @see AutoUpdateDaemon
								 * @see #needSaveDefaultLoginName()
								 * @see #afterLoginSucess()
								 */
								protected void onPostExecuteImpl(Object result) {
									Log.e("绑定成功","bound result!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+result);
									MyApplication.getInstance(context).getLocalUserInfoProvider().setLast_sync_device_id(macString);
									if (observerAdapter != null)
										observerAdapter.updateFor_boundInfoSyncToServerFinish(result);
								}
							}.execute(macString);

							// 如果该手环没有被绑定过则看看服务端返回的UTC时间是否合法
							// 取不到合法的服务端uTC时间则也不允许继续绑定，这也是为什么绑定必须联网的原因（必须要保证取到utc时间，
							// 接下来将设到设备中作为运动数据的时间）！
							if (serverUTC > 0) {
								provider.getmLepaoProtocalImpl().setUtcFromServerBound(serverUTC);
								MyApplication.getInstance(context).getLocalUserInfoProvider().setLast_sync_device_id(macString);
								provider.setCurrentDeviceMac(macString);
							} else {

								new com.eva.android.widgetx.AlertDialog.Builder(context)
										.setTitle(context.getResources().getString(R.string.general_faild))
										.setMessage(context.getResources().getString(R.string.portal_main_timing_failed))
										.setPositiveButton(context.getResources().getString(R.string.general_ok),new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog1, int which) {
												dialog1.dismiss();
												setResult(RESULT_OTHER);
												finish();
											}
										})
										.show();
							}
						}
					}
				}
			}.execute();

		} else {
			MyApplication.getInstance(BLEListActivity.this).releaseBLE(); // 没有网络去绑定设备 // 就断开连接
		}
	}
 	
 	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == MyApplication.REQUEST_ENABLE_BT) {
			switch (resultCode) {
			case Activity.RESULT_CANCELED: //用户取消打开蓝牙
				Log.e("BLEListActivity", "用户取消打开蓝牙");
				
				break;
			case Activity.RESULT_OK:       //用户打开蓝牙
				listProvider.scanDeviceList();
				/**
				 * 全局的变量 防止在这里关闭蓝牙导致连接上后 后台循环扫描的定时器不走
				 */
				Log.e("BLEListActivity", "/用户打开蓝牙");

				break;

			default:
				break;
			}
			return;
		}

	}
     
     
}
