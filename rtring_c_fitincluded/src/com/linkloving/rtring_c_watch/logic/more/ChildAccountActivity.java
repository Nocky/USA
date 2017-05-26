//package com.linkloving.rtring_c_watch.logic.more;
//
//import java.text.MessageFormat;
//import java.util.ArrayList;
//import java.util.List;
//
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.graphics.Bitmap;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.Window;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.alibaba.fastjson.JSON;
//import com.eva.android.platf.std.AutoUpdateDaemon;
//import com.eva.android.platf.std.DataLoadableActivity;
//import com.eva.android.widget.AsyncBitmapLoader;
//import com.eva.android.widget.AsyncBitmapLoader.ImageCallBack;
//import com.eva.android.widget.DataLoadingAsyncTask;
//import com.eva.android.widgetx.AlertDialog;
//import com.eva.epc.common.util.CommonUtils;
//import com.eva.epc.core.dto.DataFromClient;
//import com.eva.epc.core.dto.DataFromServer;
//import com.eva.epc.core.dto.SysActionConst;
//import com.example.android.bluetoothlegatt.BLEProvider;
//import com.google.gson.Gson;
//import com.linkloving.rtring_c_watch.MyApplication;
//import com.linkloving.rtring_c_watch.R;
//import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
//import com.linkloving.rtring_c_watch.logic.main.impl.CircularImage;
//import com.linkloving.rtring_c_watch.logic.more.adapter.ChildAdapter;
//import com.linkloving.rtring_c_watch.logic.more.avatar.AvatarHelper;
//import com.linkloving.rtring_c_watch.logic.setup.BodyActivity;
//import com.linkloving.rtring_c_watch.utils.IntentFactory;
//import com.rtring.buiness.dto.MyProcessorConst;
//import com.rtring.buiness.logic.dto.JobDispatchConst;
//import com.rtring.buiness.logic.dto.UserEntity;
//import com.salelife.store.service.util.SharedPreferencesUtil;
//
//public class ChildAccountActivity extends DataLoadableActivity
//{
//	private static int ADD_CHILD_ACCOUNT = 994;
//	
//	private ChildAdapter childAdapter;
//	private ListView childListView;
//	private CircularImage property;
//	private TextView nickName;
//	private TextView category;
//	private TextView changeItem;
//	private LinearLayout mainAccountLinear;
//	
//	private AsyncBitmapLoader asyncLoader = null; 
//	
////	private BLEHandler setDeviceInfoHandler;
////	private BLEProviderObserverAdapter bleProviderObserver = null;
//	private BLEProvider provider;
//	
//	@Override
//	public void onCreate(Bundle savedInstanceState)
//	{
//		setLoadDataOnCreate(false);
//		requestWindowFeature(Window.FEATURE_NO_TITLE);
//		super.onCreate(savedInstanceState);
//		this.asyncLoader = new AsyncBitmapLoader(AvatarHelper.getUserAvatarSavedDir(this)+"/");  
//		
//		
//		AutoGetUserEntityList(this,"__ue_list__");
////		bleProviderObserver = new BLEHandler.BLEProviderObserverAdapter(){
////			@Override
////			public void updateFor_notifyForDeviceAloneSyncSucess_D()
////			{
//////				   Toast.makeText(AlarmActivity.this, "设置闹钟成功！", Toast.LENGTH_SHORT).show();	
//////					WidgetUtils.showToast(AlarmActivity.this, "DEBUG(设备反馈): 设置闹钟成功！", ToastType.OK);
////			}
////
////			@Override
////			protected Activity getActivity()
////			{
////				return ChildAccountActivity.this;
////			}
////		};
//	}
//
//	@Override
//	protected void initViews()
//	{
//		customeTitleBarResId = R.id.child_account_activity_titleBar;
//		// 首先设置contentview
//		setContentView(R.layout.child_account_activity);
//
//		this.setTitle(R.string.user_info_child_title);
//		
//		this.getCustomeTitleBar().getRightGeneralButton().setVisibility(View.VISIBLE);
//		this.getCustomeTitleBar().getRightGeneralButton().setText("添加");
//		this.getCustomeTitleBar().getRightGeneralButton().setBackgroundResource(R.color.transparent);
//		this.getCustomeTitleBar().getRightGeneralButton().setTextColor(Color.WHITE);
//		
//		
//		provider = MyApplication.getInstance(this).getCurrentHandlerProvider();
//
//		childListView = (ListView) this.findViewById(R.id.child_account_list_view);
//		childAdapter = new ChildAdapter(this, provider);
//		childListView.setAdapter(childAdapter);
//		
//		property = (CircularImage) findViewById(R.id.main_account_property);
//		nickName = (TextView) findViewById(R.id.textView1);
//		category = (TextView) findViewById(R.id.textView2);
//		changeItem = (TextView) findViewById(R.id.change_item);
//		mainAccountLinear = (LinearLayout) findViewById(R.id.main_portal_linear);
//		mainAccountLinear.setVisibility(View.GONE);
//	}
//	
//	private  void AutoGetUserEntityList(Context context,String key)
//	{
//		String jstring = getUserEntityFromLocal(context, key);
//		if(jstring == null || jstring.equals(""))
//		{
//			loadData("");
//		}
//		else
//		{
//			refreshToView(jstring);
//			loadData(false, "");
//		}
//	}
//	
//	private void saveUserEntityList2Local(Context context,String key,List<UserEntity> list)
//	{
//	     String content = new Gson().toJson(list);
//	     SharedPreferencesUtil.saveSharedPreferences(context, key, content);
//	}
//	
//	private String getUserEntityFromLocal(Context context,String key)
//	{
//		String content = SharedPreferencesUtil.getSharedPreferences(context, key, "");
//		return content;
//	}
//	
////	@Override
////	protected void onResume()
////	{
////		super.onResume();
////		
//////		provider.setBleProviderObserver(bleProviderObserver);
////	}
////	
////	protected void onDestroy()
////	{
////		super.onDestroy();
////		if(provider.getBleProviderObserver() == bleProviderObserver)
////			provider.setBleProviderObserver(null);
////	}
//
//	@Override
//	protected void initListeners()
//	{
//		
//		this.getCustomeTitleBar().getRightGeneralButton().setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				LayoutInflater inflater = getLayoutInflater();
//				final View layout = inflater.inflate(R.layout.user_info_child_account_nickname, (LinearLayout) findViewById(R.id.user_info_child_account_nickname_LL));
//				final EditText nicknameView = (EditText) layout.findViewById(R.id.user_info_child_account_nicknameView);
//				new AlertDialog.Builder(ChildAccountActivity.this).setTitle($$(R.string.user_info_child_account))
//				                               .setView(layout)
//				                              	.setPositiveButton($$(R.string.general_next),  new DialogInterface.OnClickListener(){
//					@Override
//					public void onClick(DialogInterface dialog,int which)
//					{
//						if(!CommonUtils.isStringEmpty(nicknameView.getText().toString()))
//						{
//							startActivityForResult(IntentFactory.createBodyActivityIntent(ChildAccountActivity.this, MyApplication.getInstance(ChildAccountActivity.this).getLocalUserInfoProvider(), BodyActivity.CHILD_ACTIVITY,nicknameView.getText().toString()), ADD_CHILD_ACCOUNT);
//						}
//						else
//						{
//							Toast.makeText(ChildAccountActivity.this, R.string.user_info_update_nick_name_validate, Toast.LENGTH_LONG).show();
//						}
//					}
//				}) 
//				.setNegativeButton($$(R.string.general_cancel), null)
//				.show(); 
//				
//			}
//		});
//		mainAccountLinear.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0)
//			{
//				final UserEntity rowData = (UserEntity) mainAccountLinear.getTag();
//				if(rowData == null)
//					return;
//				
//				if(rowData.getUser_id().equals(MyApplication.getInstance(ChildAccountActivity.this).getLocalUserInfoProvider().getUser_id()))
//				{
//					new com.eva.android.widgetx.AlertDialog.Builder(ChildAccountActivity.this)
//					.setTitle(getString(R.string.user_info_child_item_not_change))
//					.setMessage(MessageFormat.format(getString(R.string.user_info_child_item_not_change_msg), rowData.getNickname()))
//					.setPositiveButton(getString(R.string.general_yes), new DialogInterface.OnClickListener()
//					{
//						@Override
//						public void onClick(DialogInterface dialog, int which)
//						{
//							
//						}
//					}).show();
//				}
//				else
//				{
//					provider.disConnect();
//					MyApplication.getInstance(ChildAccountActivity.this).setLocalUserInfoProvider(rowData, false);
//					provider.setCurrentDeviceMac(rowData.getLast_sync_device_id().toUpperCase());
//					startActivity(IntentFactory.createPortalActivityIntent(ChildAccountActivity.this));	
//					finish();
//				}
//			}
//		});
//		
////		childListView.setOnItemClickListener(new OnItemClickListener()
////		{
////
////			@Override
////			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
////			{
////				
////				Toast.makeText(ChildAccountActivity.this, "asdasd", Toast.LENGTH_LONG).show();
////			}
////		});
//	}
//	
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
//	{
//		if (requestCode == ADD_CHILD_ACCOUNT)
//		{
//		    if (resultCode == RESULT_OK)
//		    {
//		    	new DataAsyncTask().execute();
//		    }
//		}
//	}
//	
//
//	@Override
//	protected DataFromServer queryData(String... arg0)
//	{
//		return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService()
//				.sendObjToServer(DataFromClient.n()
//				.setProcessorId(MyProcessorConst.PROCESSOR_LOGIC)
//				.setJobDispatchId(JobDispatchConst.LOGIC_BASE)
//				.setActionId(SysActionConst.ACTION_APPEND1)
//				.setNewData(MyApplication.getInstance(this).getLocalUserInfoProvider().getUser_id()));
//	}
//
//	@Override
//	protected void refreshToView(Object result)
//	{
//		mainAccountLinear.setVisibility(View.VISIBLE);
//		ArrayList<UserEntity> listData = (ArrayList<UserEntity>) JSON.parseArray(result.toString(), UserEntity.class);
//		saveUserEntityList2Local(ChildAccountActivity.this,"__ue_list__",listData);
//		for(UserEntity u:listData)
//		{
//			if(CommonUtils.isStringEmpty(u.getParent_id()))
//			{
//				mainAccountLinear.setTag(u);
//				listData.remove(u);
//				break;
//			}
//		}
//		
//		if(!CommonUtils.isStringEmpty(((UserEntity)mainAccountLinear.getTag()).getUser_avatar_file_name(), true))
//		{
//			//根据图片URL去查找内存缓存有没有对应的Bitmap对象，并传递回调方法，如果没有，则等下载完毕回调  
//			Bitmap bitmap = asyncLoader.loadBitmap(property   
//					// 注意：此下载地址在服务端的逻加上是不管客户有无缓存都会传过来头像文件数据的，但在此处却不需要顾及它，
//					// 因为此处使用的缓存逻辑是独立的：它先判断本地用无该文件，如果没有的时候才会用此URL取头像数据，所以此
//					// URL要一定能取的到头像数据就对了
//					, AvatarHelper.getUserAvatarDownloadURL(ChildAccountActivity.this, ((UserEntity)mainAccountLinear.getTag()).getUser_id()) 
//					, ((UserEntity)mainAccountLinear.getTag()).getUser_avatar_file_name() //, rowData.getUserAvatarFileName()
//					, new ImageCallBack()  
//					{  
//						@Override  
//						public void imageLoad(ImageView imageView, Bitmap bitmap)  
//						{  
//							imageView.setImageBitmap(bitmap);  
//						}  
//					}
//					// 指定生成的Bitmap对象所描述的图片的大小(更小的尺寸将成倍地减小内存消耗)
//					, 120 , 120 // 此头像用于好友列表中的好友头像，大小参考：@drawable/head_bg_hole.png
//			);  
//
//			if(bitmap == null)  
//			{  
//				property.setImageResource(R.drawable.mini_avatar_shadow_rec);
//			}  
//			else  
//				property.setImageBitmap(bitmap);  
//		}
//		else
//			property.setImageResource(R.drawable.mini_avatar_shadow_rec);
//		
//		nickName.setText(((UserEntity)mainAccountLinear.getTag()).getNickname());
//		category.setText(R.string.user_info_main_account_desc);
//		//当前登录账号 不显示删除与切换按钮
//		if(((UserEntity)mainAccountLinear.getTag()).getUser_id().equals(MyApplication.getInstance(ChildAccountActivity.this).getLocalUserInfoProvider().getUser_id()))
//		{
//			category.setBackgroundResource(R.drawable.child_main_account_bg);
//			changeItem.setVisibility(View.GONE);
//		}
//		else
//		{
//			category.setBackgroundResource(R.drawable.child_main_account_bg_off);
//			changeItem.setVisibility(View.VISIBLE);
//		}
//				
//		childAdapter.setListData(listData);
//		childAdapter.notifyDataSetChanged();
//	}
//	
//	protected class DataAsyncTask extends DataLoadingAsyncTask<String, Integer, DataFromServer>
//	{
//		public DataAsyncTask()
//		{
//			super(ChildAccountActivity.this, $$(R.string.general_submitting));
//		}
//		
//		/**
//		 * 在后台执行 {@link doLogin()}实现登陆信息的提交和处于结果的读取 .
//		 * 
//		 * @param parems
//		 *            外界传进来的参数
//		 * @return 查询结果，将传递给onPostExecute(..)方法
//		 */
//		@Override
//		protected DataFromServer doInBackground(String... params)
//		{
//			return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService()
//					.sendObjToServer(DataFromClient.n()
//					.setProcessorId(MyProcessorConst.PROCESSOR_LOGIC)
//					.setJobDispatchId(JobDispatchConst.LOGIC_BASE)
//					.setActionId(SysActionConst.ACTION_APPEND1)
//					.setNewData(MyApplication.getInstance(ChildAccountActivity.this).getLocalUserInfoProvider().getUser_id()));
//		}
//
//		/**
//		 * 处理服务端返回的登陆结果信息.
//		 * 
//		 * @see AutoUpdateDaemon
//		 * @see #needSaveDefaultLoginName()
//		 * @see #afterLoginSucess()
//		 */
//		protected void onPostExecuteImpl(Object result)
//		{
//			refreshToView(result);
//		}
//	}
//}
//	
