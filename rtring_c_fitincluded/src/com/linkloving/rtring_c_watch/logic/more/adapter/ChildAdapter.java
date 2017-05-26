///**
// * HistoryListAdapter.java
// * @author Jason Lu
// * @date 2013-11-4
// * @version 1.0
// */
//package com.linkloving.rtring_c_watch.logic.more.adapter;
//
//import java.text.MessageFormat;
//
//import android.app.Activity;
//import android.content.DialogInterface;
//import android.graphics.Bitmap;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.eva.android.platf.std.AutoUpdateDaemon;
//import com.eva.android.widget.AListAdapter2;
//import com.eva.android.widget.AsyncBitmapLoader;
//import com.eva.android.widget.AsyncBitmapLoader.ImageCallBack;
//import com.eva.android.widget.DataLoadingAsyncTask;
//import com.eva.epc.common.util.CommonUtils;
//import com.eva.epc.core.dto.DataFromClient;
//import com.eva.epc.core.dto.DataFromServer;
//import com.eva.epc.core.dto.SysActionConst;
//import com.example.android.bluetoothlegatt.BLEProvider;
//import com.linkloving.rtring_c_watch.MyApplication;
//import com.linkloving.rtring_c_watch.R;
//import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
//import com.linkloving.rtring_c_watch.logic.more.avatar.AvatarHelper;
//import com.linkloving.rtring_c_watch.utils.IntentFactory;
//import com.linkloving.rtring_c_watch.utils.ToolKits;
//import com.linkloving.rtring_c_watch.widget.SlideView;
//import com.linkloving.rtring_c_watch.widget.SlideView.OnSlideClickListener;
//import com.linkloving.rtring_c_watch.widget.SlideView.OnSlideListener;
//import com.rtring.buiness.dto.MyProcessorConst;
//import com.rtring.buiness.logic.dto.JobDispatchConst;
//import com.rtring.buiness.logic.dto.UserEntity;
//import com.salelife.store.service.util.SharedPreferencesUtil;
//
///**
// * @author Jason
// * 
// */
//public class ChildAdapter extends AListAdapter2<UserEntity>
//{
//
//	/** 记录选中的ListView的行索引值以备后用（目前是在：单击、长按2类事件中保存了此索引值）. */
//	protected int selectedListViewIndex = -1;
//	
//	private AsyncBitmapLoader asyncLoader = null; 
//
//	private Activity context;
//
//	private BLEProvider provider;
//	
//	private SlideView slideView;
//	private SlideView mLastSlideViewWithStatusOn;
//	
//	public ChildAdapter(Activity context, BLEProvider provider)
//	{
//		super(context, R.layout.child_acount_activity_listview_item);
//		this.asyncLoader = new AsyncBitmapLoader(AvatarHelper.getUserAvatarSavedDir(context)+"/");  
//		this.context = context;
//		this.provider = provider;
//	}
//
//	@Override
//	public View getView(final int position, View convertView, ViewGroup parent)
//	{
//		ImageView viewAvatar = null;
//		TextView nicknameView = null;
//		TextView descView = null;
//		//Button deleteBtn = null;
//		TextView switchBtn = null;
//		
//		// ----------------------------------------------------------------------------------------
//		// （1）UI初始化
//		// 当的item布局实例已经存在（不在存，意味着这个item刚好从不可见区移到可见区时）
//		// ** 根据android的列表ui实现，为了节比资源占用，假如列表有100行而可见区显示5行，那么任何时候
//		// ** 这个列表中只会有5个item的UI存在，其它的item都是当被转到可见区时自动把自
//		// ** 已的数据实时地更新列UI上，以便查看，也就是说item的UI并不是与后台数据一
//		// ** 一对应的，所以正如API文档里说的，convertView并不能确保它总能按你的想法保持不为null
//		boolean needCreateItem = (convertView == null);
//		// 正在操作的列表行的数据集
//		final UserEntity rowData = listData.get(position);
//		if (needCreateItem)
//		{
//			// 明细item的UI实例化
//			 slideView = new SlideView(context);
//            slideView.setContentView(layoutInflater.inflate(itemResId, null));
//			convertView = slideView;
//		}
//		else
//		{
//		   slideView = (SlideView) convertView;	
//		}
//		
//		nicknameView = (TextView) convertView.findViewById(R.id.child_account_item_nickname);
//		descView = (TextView) convertView.findViewById(R.id.child_account_item_desc);
//		viewAvatar = (ImageView) convertView.findViewById(R.id.child_account_item_imageView);
//		switchBtn = (TextView) convertView.findViewById(R.id.child_account_item_switchBtn);
//		
//		// ----------------------------------------------------------------------------------------
//		// （2）增加事件处理器
//		// 各操作组件的事件监听器只需要在convertView被实例化时才需要重建（convertView需要被实例化
//		// 当然就意味着它上面的所有操作组件都已经重新新建了）
//		// ** 关于事件处理器的说明：事件处理器的增加其实可以不这么麻烦，直接每getView一次就给组件new一个处理器，
//		// ** 这样做的好处是简单，但显然存在资源浪费（每刷新一次view就新建监听器）。而现在的实现就跟Android的列表
//		// ** 实现原理一样，在切换到下一组item前，监听器永远只有一屏item的数量（刷新时只需要即时刷新对应item的数据到
//		// ** 它的监听器里），这样就节省了资源开销！
//		if (needCreateItem)
//		{
//			//
//		}
//		convertView.setTag(rowData);
//
//		nicknameView.setText(rowData.getNickname());
//		
//		
//		//当前登录账号 不显示删除与切换按钮
//		if(rowData.getUser_id().equals(MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id()))
//		{
//			switchBtn.setVisibility(View.GONE);
//			descView.setBackgroundResource(R.drawable.child_account_select_bg);
//		}
//		else
//		{
//			switchBtn.setVisibility(View.VISIBLE);
//			descView.setBackgroundResource(R.drawable.child_account_normal_bg);
//		}
//		
//		//主账号不可删除
//		if(CommonUtils.isStringEmpty(rowData.getParent_id()))
//		{
//			descView.setText(R.string.user_info_main_account_desc);
//		}
//		else
//		{
//			descView.setText(R.string.user_info_child_account_desc);
//		}
//		
//		slideView.setOnSlideListener(new OnSlideListener() {
//			
//			@Override
//			public void onSlide(View view, int status) 
//			{
//			     if (mLastSlideViewWithStatusOn != null && mLastSlideViewWithStatusOn != view) {
//			            mLastSlideViewWithStatusOn.shrink();
//			        }
//
//			        if (status == SLIDE_STATUS_ON) {
//			            mLastSlideViewWithStatusOn = (SlideView) view;
//			        }
//			}
//		});
//		
//		slideView.setOnHolderClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//
//				if(rowData.getUser_id().equals(MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id()))
//				{
//					  //当前使用账号无法删除
//					new com.eva.android.widgetx.AlertDialog.Builder(context)
//					.setTitle(context.getString(R.string.general_not_delete))
//					.setMessage(MessageFormat.format(context.getString(R.string.user_info_child_item_not_delete_msg), rowData.getNickname()))
//					.setPositiveButton(context.getString(R.string.general_yes), new DialogInterface.OnClickListener()
//					{
//						@Override
//						public void onClick(DialogInterface dialog, int which)
//						{
//						}
//					}).show();
//				}
//				else
//				{
//					//删除账号
//					new com.eva.android.widgetx.AlertDialog.Builder(context)
//					.setTitle(context.getString(R.string.general_delete))
//					.setMessage(MessageFormat.format(context.getString(R.string.user_info_child_item_delete_msg), rowData.getNickname()))
//					.setPositiveButton(context.getString(R.string.general_yes), new DialogInterface.OnClickListener()
//					{
//						@Override
//						public void onClick(DialogInterface dialog, int which)
//						{
//							selectedListViewIndex = position;
//						
//							new DataAsyncTask().execute(rowData.getUser_id());
//						}
//					}).setNegativeButton(context.getString(R.string.general_cancel), new DialogInterface.OnClickListener()
//					{
//						@Override
//						public void onClick(DialogInterface dialog, int which)
//						{
//							
//						}
//					}).show();
//				}
//			}
//		});
//		
//		slideView.setOnSlideClickListener(new OnSlideClickListener() {
//			
//			@Override
//			public void onSlideClick(View view)
//			{
//				if(rowData.getUser_id().equals(MyApplication.getInstance(context).getLocalUserInfoProvider().getUser_id()))
//				{
//					new com.eva.android.widgetx.AlertDialog.Builder(context)
//					.setTitle(context.getString(R.string.user_info_child_item_not_change))
//					.setMessage(MessageFormat.format(context.getString(R.string.user_info_child_item_not_change_msg), rowData.getNickname()))
//					.setPositiveButton(context.getString(R.string.general_yes), new DialogInterface.OnClickListener()
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
//					//切换账号
//					provider.disConnect();
//					MyApplication.getInstance(context).setLocalUserInfoProvider(rowData, false);
//					provider.setCurrentDeviceMac(rowData.getLast_sync_device_id().toUpperCase());
//					context.startActivity(IntentFactory.createPortalActivityIntent(context));	
//					context.finish();
//				}
//			}
//		});
//		
//		if(!CommonUtils.isStringEmpty(rowData.getUser_avatar_file_name(), true))
//		{
//			//根据图片URL去查找内存缓存有没有对应的Bitmap对象，并传递回调方法，如果没有，则等下载完毕回调  
//			Bitmap bitmap = asyncLoader.loadBitmap(viewAvatar   
//					// 注意：此下载地址在服务端的逻加上是不管客户有无缓存都会传过来头像文件数据的，但在此处却不需要顾及它，
//					// 因为此处使用的缓存逻辑是独立的：它先判断本地用无该文件，如果没有的时候才会用此URL取头像数据，所以此
//					// URL要一定能取的到头像数据就对了
//					, AvatarHelper.getUserAvatarDownloadURL(context, rowData.getUser_id()) 
//					, rowData.getUser_avatar_file_name() //, rowData.getUserAvatarFileName()
//					, new ImageCallBack()  
//					{  
//						@Override  
//						public void imageLoad(ImageView imageView, Bitmap bitmap)  
//						{  
////						Log.w(GoodDetailActivity.class.getSimpleName(), "【MALL】See here!!!!!!!!!!!!!!!!!!!!!"+bitmap.getWidth());
//							imageView.setImageBitmap(bitmap);  
//							
//							// ## 非常奇怪的一个问题：当网络下载的图片完成时会回调至此，但图片数据明
//							// ## 明有了却不能刷新显示之，目前为了它能显示就低效地notifyDataSetChanged
//							// ## 一下吧，以后看看什么方法可以单独刷新（否则每一次都得刷新所有可见区），
//							// ## 有可能是android的listview机制性问题
//							ChildAdapter.this.notifyDataSetChanged();
//						}  
//					}
//					// 指定生成的Bitmap对象所描述的图片的大小(更小的尺寸将成倍地减小内存消耗)
//					, 120 , 120 // 此头像用于好友列表中的好友头像，大小参考：@drawable/head_bg_hole.png
//			);  
//
//			if(bitmap == null)  
//			{  
//				viewAvatar.setImageResource(R.drawable.mini_avatar_shadow_rec);
//			}  
//			else  
//				viewAvatar.setImageBitmap(bitmap);  
//		}
//		else
//			viewAvatar.setImageResource(R.drawable.mini_avatar_shadow_rec);
//		
//		
//		
//		return convertView;
//	}
//
//	public int getSelectedListViewIndex()
//	{
//		return selectedListViewIndex;
//	}
//
//	public void setSelectedListViewIndex(int selectedListViewIndex)
//	{
//		this.selectedListViewIndex = selectedListViewIndex;
//		// this.notifyDataSetChanged();
//	}
//
//	                            /**
//	 * 提交数据请求和处理的异步执行线程实现类.
//	 */
//	protected class DataAsyncTask extends DataLoadingAsyncTask<String, Integer, DataFromServer>
//	{
//		public DataAsyncTask()
//		{
//			super(getContext(), getContext().getString(R.string.general_submitting));
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
//			return HttpServiceFactory4AJASONImpl
//					.getInstance()
//					.getDefaultService()
//					.sendObjToServer(DataFromClient.n()
//					.setProcessorId(MyProcessorConst.PROCESSOR_LOGIC)
//					.setJobDispatchId(JobDispatchConst.LOGIC_BASE)
//					.setActionId(SysActionConst.ACTION_REMOVE).setNewData( params[0]));
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
//			if (result != null && result.equals("true"))
//			{
//				ChildAdapter.this.getListData().remove(selectedListViewIndex);
//				//跟新列表保存到本地
//			    SharedPreferencesUtil.saveSharedPreferences(context, "__ue_list__",  "");
//				notifyDataSetChanged();
//			}
//			else
//			{
//				ToolKits.showCommonTosat(context, false, context.getString(R.string.general_faild), Toast.LENGTH_LONG);
//			}
//		}
//	}
//}
