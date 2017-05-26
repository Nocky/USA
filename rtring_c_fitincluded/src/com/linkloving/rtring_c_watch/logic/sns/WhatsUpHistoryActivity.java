package com.linkloving.rtring_c_watch.logic.sns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.eva.android.widget.AsyncBitmapLoader;
import com.eva.android.widget.AsyncBitmapLoader.ImageCallBack;
import com.eva.android.widget.util.WidgetUtils;
import com.eva.android.widget.util.WidgetUtils.ToastType;
import com.eva.android.widgetx.AlertDialog;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.logic.DataLoadableMultipleAcitvity;
import com.linkloving.rtring_c_watch.logic.main.slidemenu.CommonAdapter;
import com.linkloving.rtring_c_watch.logic.more.avatar.AvatarHelper;
import com.linkloving.rtring_c_watch.logic.sns.model.WhatsUpItem;
import com.linkloving.rtring_c_watch.utils.HttpSnsHelper;
import com.linkloving.rtring_c_watch.utils.IntentFactory;
import com.rtring.buiness.logic.dto.UserEntity;
import com.rtring.buiness.logic.dto.UserSignature;

public class WhatsUpHistoryActivity extends DataLoadableMultipleAcitvity 
{
	private static final String TAG = WhatsUpHistoryActivity.class.getSimpleName();
	
	private final String REQ_WHATSUP_HISTORY = "req_whatsup_history";
	private final String REQ_ADD_WHATSUP = "req_add_whatsup";
	private final String REQ_EDIT_WHATSUP = "req_edit_whatsup";
	private final String REQ_DELETE_WHATSUP = "req_delete_whatsup";
	
	private final int REQ_COMMENT_NUM = 1;
	
	private AsyncBitmapLoader asyncLoader = null; 
	private WhatsUpHistoryAdapter adapter;
	
	private UserEntity u;
	private int pageIndex = 1;
	
	private String fromUserID;
	
	private PullToRefreshListView mListView;
	private LinearLayout nullDataLinear;
	private List<WhatsUpItem> mList = new ArrayList<WhatsUpItem>();
	
	@Override
	protected void initDataFromIntent() 
	{
		super.initDataFromIntent();
		fromUserID = IntentFactory.parseWhatsUpHistoryActivity(getIntent());
	}
	
	@Override
	protected void initViews()
	{
		customeTitleBarResId = R.id.whats_up_history_titleBar;
		setContentView(R.layout.activity_whats_up_history);
		setTitle($$(R.string.whats_up_history_title));
		
		asyncLoader = new AsyncBitmapLoader(AvatarHelper.getUserAvatarSavedDir(this)+"/");
		mListView = (PullToRefreshListView) findViewById(R.id.whats_up_history_listview);
		mListView.setMode(Mode.DISABLED);
		nullDataLinear = (LinearLayout) findViewById(R.id.whats_up_history_activity_null_data_LL);
		
		adapter = new WhatsUpHistoryAdapter(this, mList);
		mListView.setAdapter(adapter);
		u = MyApplication.getInstance(this).getLocalUserInfoProvider();
		if(u != null && u.getUser_id().equals(fromUserID))
		{
			getCustomeTitleBar().getRightGeneralButton().setVisibility(View.VISIBLE);
			getCustomeTitleBar().getRightGeneralButton().setBackgroundResource(R.drawable.title_add);
		}
		loadData(true, HttpSnsHelper.GenerateWhatsUpListParams(fromUserID, pageIndex), REQ_WHATSUP_HISTORY);
	}
	
	
	private void updateWhatsup()
	{
		if(u != null && u.getUser_id().equals(fromUserID))
		{
			String whatsUp = "";
			if(mList == null || mList.isEmpty())
			{
				whatsUp = $$(R.string.user_info_what_s_up_enter_hint);
			}
			else
			{
				whatsUp = mList.get(0).getContent();
			}
			u.setWhat_s_up(whatsUp);
		}
	}
	
	private void hasData()
	{
		mListView.setVisibility(View.VISIBLE);
		nullDataLinear.setVisibility(View.GONE);
	}
	
	private void noData()
	{
		mListView.setVisibility(View.GONE);
		nullDataLinear.setVisibility(View.VISIBLE);
	}
	
	@Override
	protected void initListeners()
	{
		getCustomeTitleBar().getRightGeneralButton().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) 
			{
				LayoutInflater inflater = getLayoutInflater();
				final View layout = inflater.inflate(R.layout.user_info_update_whatsup
						, (LinearLayout) findViewById(R.id.user_info_update_what_s_up_LL));
				final EditText whatsupView = (EditText) layout.findViewById(R.id.user_info_update_whatsupView);
				whatsupView.setMaxLines(3500);
				TextView max = (TextView) layout.findViewById(R.id.user_info_update_whatsup_max);
				max.setText("* You can enter 3500 characters");
				new com.eva.android.widgetx.AlertDialog.Builder(WhatsUpHistoryActivity.this)
				.setTitle($$(R.string.whats_up_history_add_whats_up))
				.setView(layout)
				.setPositiveButton($$(R.string.whats_up_history_add),  new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog,int which)
					{
						String content = whatsupView.getText().toString();
						if(!content.isEmpty())
						{
							   loadData(false, HttpSnsHelper.GenerateAddWhatsUpParams(fromUserID, content), REQ_ADD_WHATSUP,content);
						}
						else
						{
								WidgetUtils.showToast(WhatsUpHistoryActivity.this,$$(R.string.whats_up_history_comments_empty), ToastType.INFO);
						}
					}
				}) 
				.setNegativeButton($$(R.string.general_cancel), null)
				.show(); 
			}
		});
		
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView)
			{
				   loadData(false, HttpSnsHelper.GenerateWhatsUpListParams(fromUserID, pageIndex), REQ_WHATSUP_HISTORY);
			}
		});
	}

	@Override
	protected void refreshToView(String taskName,Object taskObj, Object paramObject)
	{
		if(taskName.equals(REQ_WHATSUP_HISTORY))
		{
			if(paramObject == null || ((String)paramObject).isEmpty())
			{
				mListView.onRefreshComplete();
				mListView.setMode(Mode.DISABLED);
				if(pageIndex < 2)
					noData();
				updateWhatsup();
				return;
			}
			
			ArrayList<UserSignature> list = (ArrayList<UserSignature>) JSON.parseArray((String)paramObject, UserSignature.class);
			if(list.size() < 1)
			{
				if(pageIndex < 2)
					noData();
				mListView.onRefreshComplete();
				mListView.setMode(Mode.DISABLED);
				updateWhatsup();
				return;
			}
			
			mListView.setMode(Mode.PULL_FROM_END);
			pageIndex++;
			for(UserSignature item:list)
			{
				try
				{
					mList.add(new WhatsUpItem(item));
				}
				catch (ParseException e)
				{
					Log.e(TAG, e.getMessage());
				}
			}
			adapter.notifyDataSetChanged();
			mListView.onRefreshComplete();
			hasData();
			updateWhatsup();
		}
		else if (taskName.equals(REQ_ADD_WHATSUP))
		{
		     	if(!"false".equals(paramObject))
		     	{
		     		if(u != null)
		     		     mList.add(0, new WhatsUpItem(u.getNickname(),System.currentTimeMillis(), (String)taskObj,(String)paramObject,u.getUser_id(),u.getUser_avatar_file_name()));
		     		adapter.notifyDataSetChanged();
		     		hasData();
		     		updateWhatsup();
		        }
		}
		else if (taskName.equals(REQ_EDIT_WHATSUP))
		{
		    if("true".equals(paramObject))
		    {
		    	Map<String, String> map = (Map<String, String>) taskObj;
		    	String content = map.get("content");
		    	String pos = map.get("pos");
		    	mList.get(Integer.parseInt(pos)).setContent(content);
		    	adapter.notifyDataSetChanged();
		    	updateWhatsup();
		    }
		}
		else if (taskName.equals(REQ_DELETE_WHATSUP))
		{
		    if("true".equals(paramObject))
		    {
		    	
		    	if(mList.size() < 1)
		    	{
		    		 loadData(false, HttpSnsHelper.GenerateWhatsUpListParams(fromUserID, pageIndex), REQ_WHATSUP_HISTORY);
		    	}
		    	updateWhatsup();
		    }
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data)
	{
		if(requestCode == REQ_COMMENT_NUM)
		{
			int addComments = data.getExtras().getInt("comments");
			int pos = data.getExtras().getInt("pos");
			int comments = mList.get(pos).getComments();
			mList.get(pos).setComments(comments + addComments);
			adapter.notifyDataSetChanged();
		}
	};
	
	private class WhatsUpHistoryAdapter extends CommonAdapter<WhatsUpItem>
	{
		public class ViewHolder
		{
			public ImageView head;
			public LinearLayout dateLinear;
			public TextView date;
			public TextView signTime;
			public TextView content;
			public TextView comments;
			public LinearLayout editLinear;
			public LinearLayout deleteLinear;
			public LinearLayout commentsLinear;
			public TextView nickName;
			public LinearLayout whatsupLinear;
		}

		private ViewHolder holder;

		public WhatsUpHistoryAdapter(Context context, List<WhatsUpItem> list) {
			super(context, list);
		}

		@Override
		protected View noConvertView(int position, View convertView,
				ViewGroup parent)
		{
			
			convertView = inflater.inflate(R.layout.whats_up_history_listview_item, parent, false);
			holder = new ViewHolder();
			holder.comments = (TextView) convertView.findViewById(R.id.whats_up_history_list_item_comments);
			holder.content = (TextView) convertView.findViewById(R.id.whats_up_histoty_list_item_content);
			holder.date = (TextView) convertView.findViewById(R.id.whats_up_histoty_list_item_date);
			holder.dateLinear = (LinearLayout) convertView.findViewById(R.id.whats_up_history_list_item_date_linear);
			holder.head = (ImageView) convertView.findViewById(R.id.whats_up_history_list_item_portrait);
			holder.signTime = (TextView) convertView.findViewById(R.id.whats_up_history_list_item_sign_time);
			holder.editLinear = (LinearLayout) convertView.findViewById(R.id.whats_up_histoty_list_item_edit_linear);
			holder.deleteLinear = (LinearLayout) convertView.findViewById(R.id.whats_up_history_list_item_delete_linear);
			holder.commentsLinear = (LinearLayout) convertView.findViewById(R.id.whats_up_history_list_item_comments_linear);
			holder.nickName = (TextView) convertView.findViewById(R.id.whats_up_history_list_item_nickName);
			holder.whatsupLinear = (LinearLayout) convertView.findViewById(R.id.whats_up_history_list_item_content);
			convertView.setTag(holder);
			return convertView;
		}

		@Override
		protected View hasConvertView(int position, View convertView,
				ViewGroup parent) {
			holder = (ViewHolder) convertView.getTag();
			return convertView;
		}

		@Override
		protected View initConvertView(final int position, View convertView,
				ViewGroup parent)
		{
			holder.comments.setText(list.get(position).getComments()+"");
			holder.content.setText(list.get(position).getContent());
			if(position == 0 || (!list.get(position).getDay().equals(list.get(position-1).getDay())))
			{
				holder.dateLinear.setVisibility(View.VISIBLE);
				String date = new SimpleDateFormat($$(R.string.whats_up_history_date)).format(list.get(position).getStemp());
				holder.date.setText(date);
			}
			else
			{
				holder.dateLinear.setVisibility(View.GONE);
			}
			
				//根据图片URL去查找内存缓存有没有对应的Bitmap对象，并传递回调方法，如果没有，则等下载完毕回调  
				Bitmap bitmap = asyncLoader.loadBitmap(holder.head   
						// 注意：此下载地址在服务端的逻加上是不管客户有无缓存都会传过来头像文件数据的，但在此处却不需要顾及它，
						// 因为此处使用的缓存逻辑是独立的：它先判断本地用无该文件，如果没有的时候才会用此URL取头像数据，所以此
						// URL要一定能取的到头像数据就对了
						, AvatarHelper.getUserAvatarDownloadURL(mContext,list.get(position).getUser_id()) 
						,list.get(position).getUser_avatar_file_name() //, rowData.getUserAvatarFileName()
						, new ImageCallBack()  
						{  
							@Override  
							public void imageLoad(ImageView imageView, Bitmap bitmap)  
							{  
//							Log.w(GoodDetailActivity.class.getSimpleName(), "【MALL】See here!!!!!!!!!!!!!!!!!!!!!"+bitmap.getWidth());
								imageView.setImageBitmap(bitmap);  
								notifyDataSetChanged();
							}  
						}
						// 指定生成的Bitmap对象所描述的图片的大小(更小的尺寸将成倍地减小内存消耗)
						, 120 , 120 // 此头像用于好友列表中的好友头像，大小参考：@drawable/head_bg_hole.png
				);  

				if(bitmap == null)  
				{  
				       holder.head.setImageResource(R.drawable.mini_avatar_shadow_rec);
				}  
				else  
					   holder.head.setImageBitmap(bitmap);
		
			holder.nickName.setText(list.get(position).getNickName());
			holder.signTime.setText(list.get(position).getSginTime());
			
			holder.whatsupLinear.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0)
				{
					startActivityForResult(IntentFactory.createWhatsUpDetailActivity(mContext, list.get(position),position),REQ_COMMENT_NUM);
				}
			});
			
			holder.editLinear.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) 
				{
					LayoutInflater inflater = getLayoutInflater();
					final View layout = inflater.inflate(R.layout.user_info_update_whatsup
							, (LinearLayout) findViewById(R.id.user_info_update_what_s_up_LL));
					final EditText whatsupView = (EditText) layout.findViewById(R.id.user_info_update_whatsupView);
					whatsupView.setMaxLines(3500);
					TextView max = (TextView) layout.findViewById(R.id.user_info_update_whatsup_max);
					max.setText("* You can enter 3500 characters");
					
					whatsupView.setText(list.get(position).getContent());
					new com.eva.android.widgetx.AlertDialog.Builder(mContext)
					.setTitle($$(R.string.whats_up_history_edit_whats_up))
					.setView(layout)
					.setPositiveButton($$(R.string.general_change),  new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog,int which)
						{
							if(!whatsupView.getText().toString().isEmpty())
							{
								String content =  whatsupView.getText().toString();
								if(content.isEmpty())
								{
									WidgetUtils.showToast(mContext,$$(R.string.whats_up_history_comments_empty), ToastType.INFO);
									return;
								}
								
								Map<String, String> map = new HashMap<String, String>();
								map.put("content",  content);
								map.put("pos", position+"");
								loadData(false, HttpSnsHelper.GenerateModifyWhatsUpParams(list.get(position).getSign_id(), whatsupView.getText().toString()), REQ_EDIT_WHATSUP, map);
							}
						}
					}) 
					.setNegativeButton($$(R.string.general_cancel), null)
					.show(); 
					
				}
			});
			
			if(u != null && u.getUser_id().equals(fromUserID))
			{
				holder.deleteLinear.setVisibility(View.VISIBLE);
				holder.editLinear.setVisibility(View.VISIBLE);
			}
			else
			{
				holder.deleteLinear.setVisibility(View.INVISIBLE);
				holder.editLinear.setVisibility(View.INVISIBLE);
			}
			
			holder.deleteLinear.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0)
				{
					new AlertDialog.Builder(mContext).setTitle(R.string.general_delete)
					                                                            .setMessage(R.string.whats_up_history_delete_msg)
					                                                            .setPositiveButton(R.string.general_ok, new DialogInterface.OnClickListener() {
																					
																					@Override
																					public void onClick(DialogInterface arg0, int arg1)
																					{
																						   //先将数据删除，否则网络延迟有数据不一致风险！！！！！
																						   loadData(false, HttpSnsHelper.GenerateDeleteWhatsUpParams(list.get(position).getSign_id()), REQ_DELETE_WHATSUP, position);
																						   mList.remove(position);
																				    	   adapter.notifyDataSetChanged();
																					}
																				})
																				.setNegativeButton(R.string.general_cancel, null)
																				.create()
																				.show();
				}
			});
			
			holder.commentsLinear.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) 
				{
					startActivityForResult(IntentFactory.createWhatsUpDetailActivity(mContext, list.get(position),position),REQ_COMMENT_NUM);
				}
			});
			return convertView;
		}
		
	}
	
	
	
	
	
//	private class WhatsUpHistoryAdapter extends BaseExpandableListAdapter
//	{
//		private List<WhatsUPGroupItem> mList;
//		private Context mContext;
//		private LayoutInflater mInflater;
//		
//		public class GroupViewHolder
//		{
//		     public TextView textView1;
//		     public TextView textView2;
//		}
//		
//		public class ChildViewHolder
//		{
//			public TextView textView3;
//			public TextView textView4;
//		}
//		
//		private GroupViewHolder groupViewHolder;
//		private ChildViewHolder childViewHolder;
//		
//		public WhatsUpHistoryAdapter(Context context,List<WhatsUPGroupItem> list)
//		{
//			mList = list;
//			mContext = context;
//			mInflater = LayoutInflater.from(context);
//		}
//		
//		@Override
//		public Object getChild(int arg0, int arg1) 
//		{
//			return mList.get(arg0).getChildList().get(arg1);
//		}
//
//		@Override
//		public long getChildId(int arg0, int arg1) {
//			return arg1;
//		}
//
//		@Override
//		public View getChildView(int arg0, int arg1, boolean arg2, View arg3,
//				ViewGroup arg4)
//		{
//			if(arg3 == null)
//			{
//				childViewHolder = new ChildViewHolder();
//				arg3 = mInflater.inflate(R.layout.whats_up_history_listview_child_item, arg4,false);
//				childViewHolder.textView3 = (TextView) arg3.findViewById(R.id.textView1);
//				childViewHolder.textView4 = (TextView) arg3.findViewById(R.id.textView2);
//				arg3.setTag(groupViewHolder);
//			}
//			else
//			{
//				childViewHolder = (ChildViewHolder) arg3.getTag();
//			}
//			childViewHolder.textView3.setText(mList.get(arg0).getGroupName());
//			childViewHolder.textView4.setText(mList.get(arg0).getGroupName2());
//			return arg3;
//		}
//
//		@Override
//		public int getChildrenCount(int arg0)
//		{
//			return mList.get(arg0).getChildList().size();
//		}
//
//		@Override
//		public Object getGroup(int arg0) {
//			return mList.get(arg0);
//		}
//
//		@Override
//		public int getGroupCount() {
//			return mList.size();
//		}
//
//		@Override
//		public long getGroupId(int arg0) {
//			return arg0;
//		}
//
//		@Override
//		public View getGroupView(int arg0, boolean arg1, View arg2,
//				ViewGroup arg3) 
//		{
//			if(arg2 == null)
//			{
//				groupViewHolder = new GroupViewHolder();
//				arg2 = mInflater.inflate(R.layout.whats_up_history_listview_group_item, arg3,false);
//				groupViewHolder.textView1 = (TextView) arg2.findViewById(R.id.textView1);
//				groupViewHolder.textView2 = (TextView) arg2.findViewById(R.id.textView2);
//				arg2.setTag(groupViewHolder);
//			}
//			else
//			{
//				groupViewHolder = (GroupViewHolder) arg2.getTag();
//			}
//			groupViewHolder.textView1.setText(mList.get(arg0).getGroupName());
//			groupViewHolder.textView2.setText(mList.get(arg0).getGroupName2());
//			
//			return arg2;
//		}
//
//		@Override
//		public boolean hasStableIds() {
//			return false;
//		}
//
//		@Override
//		public boolean isChildSelectable(int arg0, int arg1) {
//			return false;
//		}
//	}
}
