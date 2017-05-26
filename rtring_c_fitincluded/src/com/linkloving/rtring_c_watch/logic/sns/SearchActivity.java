package com.linkloving.rtring_c_watch.logic.sns;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eva.android.HttpFileDownloadHelper;
import com.eva.android.platf.std.AutoUpdateDaemon;
import com.eva.android.widget.AsyncBitmapLoader;
import com.eva.android.widget.AsyncBitmapLoader.ImageCallBack;
import com.eva.android.widget.DataLoadingAsyncTask;
import com.eva.android.widget.util.WidgetUtils;
import com.eva.android.widget.util.WidgetUtils.ToastType;
import com.eva.android.widgetx.AlertDialog;
import com.eva.epc.common.util.CommonUtils;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.eva.epc.core.dto.SysActionConst;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.linkloving.rtring_c_watch.logic.DataLoadableMultipleAcitvity;
import com.linkloving.rtring_c_watch.logic.main.slidemenu.CommonAdapter;
import com.linkloving.rtring_c_watch.logic.more.avatar.AvatarHelper;
import com.linkloving.rtring_c_watch.utils.EntHelper;
import com.linkloving.rtring_c_watch.utils.HttpSnsHelper;
import com.linkloving.rtring_c_watch.utils.IntentFactory;
import com.linkloving.rtring_c_watch.utils.SkinSettingManager;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.linkloving.rtring_c_watch.utils.SkinSettingManager.BackgroundAsyncTask;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.Group;
import com.rtring.buiness.logic.dto.JobDispatchConst;
import com.rtring.buiness.logic.dto.UserEntity;
import com.rtring.buiness.logic.dto.UserSelected;


public class SearchActivity extends  DataLoadableMultipleAcitvity 
{
	public static final int USE_TO_UNKONW = 0;
	public static final int USE_TO_SEARCH_USER = 1;
	public static final int USE_TO_SEARCH_GROUP = 2;
	
	private PullToRefreshListView mListView;
	private UserEntity user;
	
	private static final String REQ_SEARCH = "req_search";
	
	private int pageIndex = 1;
	
	private EditText content;
	private Button search;
	
	private List<UserSelected> userList = new ArrayList<UserSelected>();
	private List<Group> groupList = new ArrayList<Group>();
	
	private String mCondition;
	private AsyncBitmapLoader asyncLoader = null; 
	private SearchUserAdapter userAdapter = null;
	private SearchGroupAdapter groupAdapter = null;
	
	int use_for = 0;
	
	
	@Override
	protected void initDataFromIntent()
	{
		use_for = IntentFactory.parseSearchActivity(getIntent());
	}

	@Override
	protected void initViews()
	{
		super.initViews();
		customeTitleBarResId = R.id.search_titleBar;
		setContentView(R.layout.activity_search);


		content = (EditText) findViewById(R.id.search_content);
		search = (Button) findViewById(R.id.search_btn);

		user = MyApplication.getInstance(this).getLocalUserInfoProvider();
		mListView = (PullToRefreshListView) findViewById(R.id.search_result_list);
		mListView.getRefreshableView().setDivider(ToolKits.getRepetDrawable(this, R.drawable.list_view_deliver));
		mListView.getRefreshableView().setDividerHeight(1);
		mListView.setMode(Mode.DISABLED);
		
		
		switch (use_for)
		{
		case USE_TO_SEARCH_USER:
		{
			content.setHint(R.string.relationship_search_hint);
			asyncLoader = new AsyncBitmapLoader(AvatarHelper.getUserAvatarSavedDir(this) + "/");
			userAdapter = new SearchUserAdapter(this, userList);
			setTitle(R.string.relationship_search_user);
			mListView.setAdapter(userAdapter);
		}
			break;
		case USE_TO_SEARCH_GROUP:
		{
			content.setHint(R.string.relationship_search_hint_group);
			asyncLoader = new AsyncBitmapLoader(AvatarHelper.getUserAvatarSavedDir(this) + "/");
			groupAdapter = new SearchGroupAdapter(this, groupList);
			setTitle(R.string.relationship_search_group);
			mListView.setAdapter(groupAdapter);
		}
			break;
		case USE_TO_UNKONW:

			break;
		}
		
		
	}
	
	@Override
	protected void initListeners() 
	{
		search.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				String condition = content.getEditableText().toString();
				ToolKits.HideKeyboard(content);
				switch (use_for)
				{
				case USE_TO_SEARCH_USER:
				{
					if (condition == null || condition.isEmpty())
					{
						WidgetUtils.showToast(SearchActivity.this, $$(R.string.relationship_search_condition_empty), ToastType.INFO);
						return;
					}
					pageIndex = 1;
					userList.clear();
					userAdapter.notifyDataSetChanged();
					mCondition = condition;
					loadData(true, HttpSnsHelper.GenerateSearchListParams(condition, user.getUser_id(), pageIndex, USE_TO_SEARCH_USER), REQ_SEARCH);
				}
					break;
				case USE_TO_SEARCH_GROUP:
				{
					pageIndex = 1;
					groupList.clear();
					groupAdapter.notifyDataSetChanged();
					mCondition = condition;
					loadData(true, HttpSnsHelper.GenerateSearchListParams(condition, user.getUser_id(), pageIndex, USE_TO_SEARCH_GROUP), REQ_SEARCH);
				}
					break;
				case USE_TO_UNKONW:

					break;
				}
			}
		});

		mListView.setOnRefreshListener(new OnRefreshListener<ListView>()
		{

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView)
			{
				
				switch (use_for)
				{
				case USE_TO_SEARCH_USER:
				{
					loadData(false, HttpSnsHelper.GenerateSearchListParams(mCondition, user.getUser_id(), pageIndex, USE_TO_SEARCH_USER), REQ_SEARCH);
				}
					break;
				case USE_TO_SEARCH_GROUP:
				{
					loadData(false, HttpSnsHelper.GenerateSearchListParams(mCondition, user.getUser_id(), pageIndex, USE_TO_SEARCH_GROUP), REQ_SEARCH);
				}
					break;
				case USE_TO_UNKONW:

					break;
				}
				
			}
		});
		
		mListView.setOnItemClickListener(new OnItemClickListener()
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3)
			{
				
				switch (use_for)
				{
				case USE_TO_SEARCH_USER:
				{
					String user_id = userList.get(index - 1 < 0 ? 0 : index - 1).getUser_id();
					Intent intent = IntentFactory.createUserDetialActivityIntent(SearchActivity.this, user_id);
					startActivity(intent);
				}
					break;
				case USE_TO_SEARCH_GROUP:
				{
					Group ent = groupList.get(index - 1 < 0 ? 0 : index - 1);
					final String ent_id = ent.getEnt_url();
					String group_name = ent.getNickname();
					String my_ent_id = MyApplication.getInstance(SearchActivity.this).getLocalUserInfoProvider().getEid();
					final String pasword = ent.getJoin_psw();
				
					if(CommonUtils.isStringEmpty(my_ent_id) || my_ent_id.equals("9999999999"))  //从未加入过群组
					{
						new AlertDialog.Builder(SearchActivity.this)
						.setTitle(getString(R.string.general_tip))
						.setMessage(MessageFormat.format(getString(R.string.relationship_search_join_group_message), group_name))
						.setPositiveButton(getString(R.string.general_yes), new DialogInterface.OnClickListener() 
						{
							@Override
							public void onClick(DialogInterface dialog,int which) 
							{
								if(pasword.equals(""))
									//密码是空 直接进入
									new BoundGroupAsyncTask().execute(new String[]{MyApplication.getInstance(SearchActivity.this).getLocalUserInfoProvider().getUser_id(), ent_id});
								
								else
								{
									//else 输入密码再进入
									LayoutInflater inflater = getLayoutInflater();
									final View layout = inflater.inflate(R.layout.user_info_update_user_nickname, (LinearLayout) findViewById(R.id.user_info_update_user_nickname_LL));
									final EditText nicknameView = (EditText) layout.findViewById(R.id.user_info_update_user_nicknameView);
									new com.eva.android.widgetx.AlertDialog.Builder(SearchActivity.this)
									.setTitle($$(R.string.relationship_search_group_inputpsd))
									.setMessage($$(R.string.relationship_search_group_msg))
									.setView(layout)
									.setPositiveButton($$(R.string.general_ok),  new DialogInterface.OnClickListener(){
										@Override
										public void onClick(DialogInterface dialog,int which)
										{
											
											if(!CommonUtils.isStringEmpty(nicknameView.getText().toString())){
												
												if(pasword.equals(nicknameView.getText().toString().trim())){
													//密码正确
													new BoundGroupAsyncTask().execute(new String[]{MyApplication.getInstance(SearchActivity.this).getLocalUserInfoProvider().getUser_id(), ent_id});
												
												}else{
													//密码错误
													new AlertDialog.Builder(SearchActivity.this)  // 
													.setTitle(getString(R.string.relationship_search_group_psderror))
													.setMessage(getString(R.string.relationship_search_group_errpasword))
													.setPositiveButton(getString(R.string.general_yes), null)
													.show();
												}
											}
											else
											{
												Toast.makeText(SearchActivity.this, R.string.relationship_search_group_nopasword, Toast.LENGTH_LONG).show();
											}
										}
									}) 
									.setNegativeButton($$(R.string.general_cancel), null)
									.show();
								}
								
							}
						})
						.setNegativeButton(getString(R.string.general_cancel), null)
						.show();
					}
					else
					{
						String my_ent_name = MyApplication.getInstance(SearchActivity.this).getLocalUserInfoProvider().getEname();
						if(ent_id.equals(my_ent_id))   //已经加入过
						{
							
							new AlertDialog.Builder(SearchActivity.this)
							.setTitle(getString(R.string.general_tip))
							.setMessage(MessageFormat.format(getString(R.string.relationship_search_already_group_message), my_ent_name))
							.setPositiveButton(getString(R.string.general_yes), null)
							.setNegativeButton(null, null)
							.show();
							
						}
						else
						{
							new AlertDialog.Builder(SearchActivity.this)  // 
							.setTitle(getString(R.string.general_tip))
							.setMessage(MessageFormat.format(getString(R.string.relationship_search_already_group_message_other), my_ent_name))
							.setPositiveButton(getString(R.string.general_yes), null)
							.setNegativeButton(null, null)
							.show();
						}
					}
				}
					break;
				case USE_TO_UNKONW:

					break;
				}
				
				
			}
		});
	}

	@Override
	protected void refreshToView(String taskName, Object taskObj, Object paramObject)
	{
		if (paramObject == null || ((String) paramObject).isEmpty())
		{
			mListView.onRefreshComplete();
			mListView.setMode(Mode.DISABLED);
			if (pageIndex < 2)
				WidgetUtils.showToast(SearchActivity.this, $$(R.string.relationship_search_empty), ToastType.INFO);
			return;
		}

		if (taskName.equals(REQ_SEARCH))
		{
			
			switch (use_for)
			{
			case USE_TO_SEARCH_USER:
			{
				ArrayList<UserSelected> list = (ArrayList<UserSelected>) JSON.parseArray((String) paramObject, UserSelected.class);
				if (list.size() < 1)
				{
					mListView.onRefreshComplete();
					mListView.setMode(Mode.DISABLED);
					if (pageIndex < 2)
						WidgetUtils.showToast(SearchActivity.this, $$(R.string.relationship_search_empty), ToastType.INFO);
					return;
				}
				mListView.setMode(Mode.PULL_FROM_END);
				pageIndex++;
				userList.addAll(list);
				userAdapter.notifyDataSetChanged();
			}
				break;
			case USE_TO_SEARCH_GROUP:
			{
				Log.e("(String) paramObject", (String) paramObject);
					ArrayList<Group> list = (ArrayList<Group>) JSON.parseArray((String) paramObject, Group.class); 
					if (list.size() < 1)
					{
						mListView.onRefreshComplete();
						mListView.setMode(Mode.DISABLED);
						if (pageIndex < 2)
							WidgetUtils.showToast(SearchActivity.this, $$(R.string.relationship_search_empty), ToastType.INFO);
						return;
					}
					mListView.setMode(Mode.PULL_FROM_END);
					pageIndex++;
					groupList.addAll(list);
					groupAdapter.notifyDataSetChanged();
			}
				break;
			case USE_TO_UNKONW:

				break;
			}
			mListView.onRefreshComplete();
			
		}
	}
	
	
	private class SearchUserAdapter extends CommonAdapter<UserSelected>
	{

		public class ViewHolder
		{
			public ImageView head;
			public TextView nickName;
			public TextView label;
		}

		private ViewHolder holder;

		public SearchUserAdapter(Context context, List<UserSelected> list)
		{
			super(context, list);
		}

		@Override
		protected View noConvertView(int position, View convertView, ViewGroup parent)
		{
			convertView = inflater.inflate(R.layout.list_item_search_user, parent, false);
			holder = new ViewHolder();
			holder.head = (ImageView) convertView.findViewById(R.id.head);
			holder.label = (TextView) convertView.findViewById(R.id.search_label);
			holder.nickName = (TextView) convertView.findViewById(R.id.search_nickName);
			convertView.setTag(holder);
			return convertView;
		}

		@Override
		protected View hasConvertView(int position, View convertView, ViewGroup parent)
		{
			holder = (ViewHolder) convertView.getTag();
			return convertView;
		}

		@Override
		protected View initConvertView(int position, View convertView, ViewGroup parent)
		{
			// 根据图片URL去查找内存缓存有没有对应的Bitmap对象，并传递回调方法，如果没有，则等下载完毕回调
			Bitmap bitmap = asyncLoader.loadBitmap(holder.head
			// 注意：此下载地址在服务端的逻加上是不管客户有无缓存都会传过来头像文件数据的，但在此处却不需要顾及它，
			// 因为此处使用的缓存逻辑是独立的：它先判断本地用无该文件，如果没有的时候才会用此URL取头像数据，所以此
			// URL要一定能取的到头像数据就对了
					, AvatarHelper.getUserAvatarDownloadURL(mContext, list.get(position).getUser_id()), list.get(position).getUserAvatar() // ,
																																			// rowData.getUserAvatarFileName()
					, new ImageCallBack()
					{
						@Override
						public void imageLoad(ImageView imageView, Bitmap bitmap)
						{
							// Log.w(GoodDetailActivity.class.getSimpleName(),
							// "【MALL】See here!!!!!!!!!!!!!!!!!!!!!"+bitmap.getWidth());
							imageView.setImageBitmap(bitmap);
							notifyDataSetChanged();
						}
					}
					// 指定生成的Bitmap对象所描述的图片的大小(更小的尺寸将成倍地减小内存消耗)
					, 120, 120 // 此头像用于好友列表中的好友头像，大小参考：@drawable/head_bg_hole.png
					);

			if (bitmap == null)
				holder.head.setImageResource(R.drawable.mini_avatar_shadow_rec);
			else
				holder.head.setImageBitmap(bitmap);

			holder.label.setText(list.get(position).getWhat_s_up());
			holder.nickName.setText(list.get(position).getNickname());
			return convertView;
		}

	}
	
	private class SearchGroupAdapter extends CommonAdapter<Group>
	{

		public class ViewHolder
		{
			public TextView name;
			public TextView count;
		}

		private ViewHolder holder;

		public SearchGroupAdapter(Context context, List<Group> list)
		{
			super(context, list);
		}

		@Override
		protected View noConvertView(int position, View convertView, ViewGroup parent)
		{
			convertView = inflater.inflate(R.layout.list_item_search_group, parent, false);
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.search_group_name);
			holder.count = (TextView) convertView.findViewById(R.id.search_group_people_count);
			convertView.setTag(holder);
			return convertView;
		}

		@Override
		protected View hasConvertView(int position, View convertView, ViewGroup parent)
		{
			holder = (ViewHolder) convertView.getTag();
			return convertView;
		}

		@Override
		protected View initConvertView(int position, View convertView, ViewGroup parent)
		{

			holder.name.setText(list.get(position).getNickname());
			holder.count.setText(MessageFormat.format(getString(R.string.relationship_search_people_count), list.get(position).getCount()));
			return convertView;
		}

	}

	protected class BoundGroupAsyncTask extends DataLoadingAsyncTask<String, Integer, DataFromServer>
	{
		public BoundGroupAsyncTask()
		{
			super(SearchActivity.this, $$(R.string.general_submitting));
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
			obj.put("user_id", params[0]);
			obj.put("ent_id", params[1]);
			
			return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(
					DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_SNS_LOGIC)
	                .setJobDispatchId(JobDispatchConst.SNS_BASE)
	                .setActionId(SysActionConst.ACTION_CANCEL_VERIFY)
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
				ToolKits.showCommonTosat(SearchActivity.this, true, getString(R.string.relationship_search_join_succeed), Toast.LENGTH_SHORT);
				UserEntity user = JSON.parseObject((String)result, UserEntity.class);
				MyApplication.getInstance(SearchActivity.this).setLocalUserInfoProvider(user);
				
				new AlertDialog.Builder(SearchActivity.this)
				.setTitle(getString(R.string.relationship_search_group_success))
				.setMessage(getString(R.string.relationship_search_group_success1))
				.setPositiveButton(getString(R.string.relationship_search_group_continue),new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
//						String url = EntHelper.getEntDownloadURL(SearchActivity.this,MyApplication.getInstance(SearchActivity.this).getLocalUserInfoProvider().getUser_id(),MyApplication.getInstance(SearchActivity.this).getLocalUserInfoProvider().getEbackground_file_name());
//						new BackgroundAsyncTask().execute(url);
						
						new AlertDialog.Builder(SearchActivity.this)
						.setTitle(getString(R.string.relationship_search_group_features))
						.setMessage(getString(R.string.relationship_search_group_success2))
						.setPositiveButton(getString(R.string.relationship_search_group_continue), new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
								broadcastUpdate(MyApplication.BLE_STATE_SUCCESS, SearchActivity.this);
								startActivity(IntentFactory.createPortalActivityIntent(SearchActivity.this));
								finish();
							}
						})
						.show();
					}
				})
				.show();
			}
			else
				ToolKits.showCommonTosat(SearchActivity.this, true, getString(R.string.relationship_search_join_failure), Toast.LENGTH_SHORT);
		}
	}
	
	private void broadcastUpdate(final String action, Context context)
	{
		final Intent intent = new Intent(action);
		context.sendBroadcast(intent);
	}
	
	public class BackgroundAsyncTask extends DataLoadingAsyncTask<String, Integer, Integer>
	{

		public BackgroundAsyncTask()
		{
			super(SearchActivity.this, null);
		}

		@Override
		protected Integer doInBackground(String... params)
		{
			return downLoadFile(params[0], MyApplication.getInstance(SearchActivity.this).getLocalUserInfoProvider().getEbackground_file_name());
		}

		@SuppressWarnings("resource")
		@Override
		protected void onPostExecuteImpl(Object arg0)
		{
			//该函数返回整形 -1：代表下载文件出错。 0：代表下载文件成功 1：代表下载文件经存在
			if (CommonUtils.getIntValue(arg0) != -1)
			{
				Bitmap bitmap = getDiskBitmap(EntHelper.getBGFileSavedDir(SearchActivity.this) + "/" + MyApplication.getInstance(SearchActivity.this).getLocalUserInfoProvider().getEbackground_file_name());
//				Bitmap bitmap = getDiskBitmap(EntHelper.getBGFileSavedDir(SearchActivity.this) + "/" + "005.jpg");
				Drawable drawable =new BitmapDrawable(bitmap);
				SearchActivity.this.getWindow().setBackgroundDrawable(drawable);
			}
			else
			{
				WidgetUtils.showToast(SearchActivity.this, "File download error！！！", ToastType.ERROR);
			}
			
		}
			
			/*
			 * 该函数返回整形 -1：代表下载文件出错。 0：代表下载文件成功 1：代表下载文件经存在 path =
			 * EntHelper.getEntFileSavedDir(this)+ fileName
			 */
			public int downLoadFile(String urlStr, String fileName)
			{
				File file = new File(EntHelper.getBGFileSavedDir(SearchActivity.this) + "/" + MyApplication.getInstance(SearchActivity.this).getLocalUserInfoProvider().getEbackground_file_name());
				if (file.exists()){
					Log.e("SkinSettingManager","文件已经存在---------无需下载----！"); 
					return 1;
				}else{
					Object[] ret;
					try {
						ret = HttpFileDownloadHelper.downloadFileEx(urlStr
										// 如果服务端判定需要更新头像到本地缓存时的保存目录
										, EntHelper.getBGFileSavedDir(SearchActivity.this), 0, null, true);
						
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
	
}
