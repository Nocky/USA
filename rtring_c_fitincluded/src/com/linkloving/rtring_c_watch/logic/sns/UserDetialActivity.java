package com.linkloving.rtring_c_watch.logic.sns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
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

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult.AddressComponent;
import com.eva.android.widget.AsyncBitmapLoader;
import com.eva.android.widget.AsyncBitmapLoader.ImageCallBack;
import com.eva.android.widget.util.WidgetUtils;
import com.eva.android.widget.util.WidgetUtils.ToastType;
import com.eva.epc.common.util.CalendarHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.logic.DataLoadableMultipleAcitvity;
import com.linkloving.rtring_c_watch.logic.main.impl.BoldCircularImage;
import com.linkloving.rtring_c_watch.logic.main.slidemenu.CommonAdapter;
import com.linkloving.rtring_c_watch.logic.more.avatar.AvatarHelper;
import com.linkloving.rtring_c_watch.utils.DateSwitcher;
import com.linkloving.rtring_c_watch.utils.DateSwitcher.PeriodSwitchType;
import com.linkloving.rtring_c_watch.utils.HttpSnsHelper;
import com.linkloving.rtring_c_watch.utils.IntentFactory;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.rtring.buiness.logic.dto.CommentReply;
import com.rtring.buiness.logic.dto.UserEntity;
import com.rtring.buiness.logic.dto.UserProfile;

/**
 * 个人信息
 * @author Administrator
 *
 */
public class UserDetialActivity extends DataLoadableMultipleAcitvity 
{
	private static final String TAG  = UserDetialActivity.class.getSimpleName();
	
	private static final String  REQ_USER_DETAIL = "req_user_detail";
	private static final String REQ_COMMENTS_LIST = "req_comments_list";
	private static final String REQ_ADD_COMMENT = "req_add_comment";
	private static final String REQ_REPLY = "req_reply";
	private static final String REQ_ATTENTION = "req_attention";
	private static final String REQ_CANCEL_ATTENTION = "req_cancel_attention";

	
	private int resultCount;
	
	private SimpleDateFormat sdf = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD);
	private SimpleDateFormat sdf2 = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);
	
	private CommentsUIWrapper commentsUIWrapper = null;
	private UserInfoUIWrapper userInfoUIWrapper = null;
	
	private DateSwitcher daySwitcher = null;
	private Button btnLeft = null;
	private Button btnRight = null;
	private TextView viewTime = null;
	
	private LinearLayout userDetailLabelLinear;
	
	private Button titleRightBtn;
	
	private AsyncBitmapLoader asyncLoader = null; 
	
	private UserEntity u;
	
	private String fromUserID;
	private String fromTime;
	
	private boolean mAttention = false;
	
	public interface OnCommentsClickListener
	{
		public void onCommentsClick(CommentReply comment);
	}

		
	@Override
	protected void initViews() 
	{
		super.initViews();
		customeTitleBarResId = R.id.user_detial_titleBar;
	//	GeoCodeingHelper.setAK("v44paIjq2oGqpLFqEvxRBwvF");
		setContentView(R.layout.activity_user_detial);
		
		setTitle(R.string.relationship_user_info);
		
		String[] intentTemp = IntentFactory.parseUserDetialActivityIntent(getIntent());
		fromUserID = intentTemp[0];
		fromTime = intentTemp[1];
		
		u = MyApplication.getInstance(this).getLocalUserInfoProvider();
		
		userDetailLabelLinear = (LinearLayout) findViewById(R.id.user_detail_label_linear);
		
		btnLeft = (Button) findViewById(R.id.user_detial_activity_leftBtn);
		btnRight = (Button) findViewById(R.id.user_detial_activity_rightBtn);
		viewTime = (TextView) findViewById(R.id.user_detial_activity_dateView);
		
		titleRightBtn =  getCustomeTitleBar().getRightGeneralButton();
		titleRightBtn.setTextColor(Color.WHITE);
		titleRightBtn.setVisibility(View.GONE);
		
		asyncLoader = new AsyncBitmapLoader(AvatarHelper.getUserAvatarSavedDir(this)+"/");
		
		daySwitcher = new DateSwitcher(PeriodSwitchType.day){
			@Override
			protected void init()
			{
				switch(this.type)
				{
				case PeriodSwitchType.day:
					// 日类型时，默认时间为当天
					base = new GregorianCalendar();
//					base.add(GregorianCalendar.DAY_OF_MONTH, -1);
					break;
				default:
					Log.e(TAG, "当前日期切换组件只支持到了年月日的切换！");
					break;
				}
			}
			
			/**
			 * 日志切换检查.
			 * 
			 * @return true表示检查通过，允许切换到新日期，否则不允许切换
			 */
			@Override
			protected boolean switchToNextCheck()
			{
				if(CalendarHelper.isToday(base.getTimeInMillis()))
				{
					WidgetUtils.showToast(UserDetialActivity.this, $$(R.string.ranking_wait_tomorrow), ToastType.INFO);
					return false;
				}
				return true;
			}
		};
		
		if(!fromTime.isEmpty())
		{
			try 
			{
				daySwitcher.setBaseTime(sdf.parse(fromTime));
			}
			catch (ParseException e)
			{
				Log.e(TAG, e.getMessage());
			}
		}
		
		commentsUIWrapper =   new CommentsUIWrapper(this);
		userInfoUIWrapper =  new UserInfoUIWrapper(this);
		
		switchedOver();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		userInfoUIWrapper.updateLabel();
	}
	
	public void refreshShowText()
	{
		viewTime.setText(new SimpleDateFormat("yyyy/MM/dd").format(daySwitcher.getStartDate()));
	}
	
	private void switchedOver()
	{
		refreshShowText();
		String user_time = sdf.format(daySwitcher.getStartDate());
		if(u != null)
		{
			loadData(false, HttpSnsHelper.GenerateCommentListParams(fromUserID, user_time), REQ_COMMENTS_LIST);
			loadData(false, HttpSnsHelper.GenerateUserDetailParams(u.getUser_id(), fromUserID, user_time), REQ_USER_DETAIL);
		}
	}
	
	@Override
	protected void initListeners()
	{
		super.initListeners();
		
		userDetailLabelLinear.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) 
			{
			     	startActivity(IntentFactory.createWhatsUpHistoryActivityIntent(UserDetialActivity.this, fromUserID));
			}
		});
		
	    titleRightBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) 
			{
				 if(mAttention)
				 {
					 //取消关注
					 loadData(true, HttpSnsHelper.GenerateCancelConcernParams(u.getUser_id(), fromUserID), REQ_CANCEL_ATTENTION);
				 }
				 else
				 {
					//关注
					 loadData(true, HttpSnsHelper.GenerateConcernParams(u.getUser_id(), fromUserID), REQ_ATTENTION);
				 }
			}
		});
	    
	    getCustomeTitleBar().getLeftBackButton().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.putExtra("count", resultCount);
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
		
		btnLeft.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0)
			{
				if(daySwitcher.previous())
					switchedOver();	
				commentsUIWrapper.clearReplyState();
			}
		});
		
		btnRight.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0)
			{
				if(daySwitcher.next())
					switchedOver();
				commentsUIWrapper.clearReplyState();
			}
		});
	    
	}
	
	
	@Override
	protected void refreshToView(String taskName,Object taskObj, Object paramObject) 
	{
		Log.d(UserDetialActivity.class.getSimpleName(), "taskName:"+taskName+"  result:"+paramObject);
		if(paramObject == null || ((String)paramObject).isEmpty())
			  return;
		
		if(taskName.equals(REQ_COMMENTS_LIST))
		{
			 List<CommentReply> list = new Gson().fromJson((String)paramObject, new  TypeToken<List<CommentReply>>(){}.getType());
			 commentsUIWrapper.update(list);
		}
		else if (taskName.equals(REQ_USER_DETAIL))
		{
			   UserProfile profile = new Gson().fromJson((String)paramObject, UserProfile.class);
			   userInfoUIWrapper.update(profile);
		}
		else if (taskName.equals(REQ_REPLY))
		{
			if("true".equals((String)paramObject))
			{
			
				commentsUIWrapper.replySuccess();
			}
			else 
			{
				//回复失败
				WidgetUtils.showToast(this, getString(R.string.relationship_reply_failed), ToastType.INFO);
			}
		}
		else if(taskName.equals(REQ_ADD_COMMENT))
		{
			if("true".equals((String)paramObject))
			{
				commentsUIWrapper.commentSuccess();
			}
			else 
			{
				//评论失败
				WidgetUtils.showToast(this, getString(R.string.relationship_comment_failed), ToastType.INFO);
			}
		}
		else if (taskName.equals(REQ_ATTENTION))
		{
			  //关注
			if("true".equals((String)paramObject))
			{
				mAttention = true;
				titleRightBtn.setText($$(R.string.relationship_cancel_attention));
				titleRightBtn.setBackgroundResource(R.color.transparent);
				WidgetUtils.showToast(this, getString(R.string.relationship_attention_success), ToastType.INFO);
			}
			else 
			{
				WidgetUtils.showToast(this, getString(R.string.relationship_attention_failed), ToastType.INFO);
			}
		}
		else if (taskName.equals(REQ_CANCEL_ATTENTION))
		{
			//取消关注
			if("true".equals((String)paramObject))
			{
				mAttention = false;
				titleRightBtn.setBackgroundResource(R.drawable.title_add);
				titleRightBtn.setText("");
				WidgetUtils.showToast(this, getString(R.string.relationship_cancel_attention_success), ToastType.INFO);
			}
			else 
			{
				//评论失败
				WidgetUtils.showToast(this, getString(R.string.relationship_cancel_attention_failed), ToastType.INFO);
			}
		}
	
	}
	
	
	private class CommentsAdapter extends CommonAdapter<CommentReply>
	{
		private OnCommentsClickListener listener;
	
		public void setOnCommentsClickListener(OnCommentsClickListener listener)
		{
			this.listener = listener;
		}
		public class ViewHolder
		{
			public ImageView head;
			public TextView nickName;
			public TextView replyName;
			public TextView commentMsg;
			public TextView commentTime;
			public LinearLayout replyLinear;
			public LinearLayout msgLinear;
		}
		
		private ViewHolder holder;

		public CommentsAdapter(Context context, List<CommentReply> list)
		{
			super(context, list);
		}

		@Override
		protected View noConvertView(int position, View convertView,
				ViewGroup parent)
		{
			convertView = inflater.inflate(R.layout.list_item_detail_comments, parent, false);
			holder = new ViewHolder();
			holder.head = (ImageView) convertView.findViewById(R.id.detail_comments_head);
			holder.nickName = (TextView) convertView.findViewById(R.id.detail_comments_nickName);
			holder.replyName = (TextView) convertView.findViewById(R.id.detail_comments_replyName);
			holder.commentMsg = (TextView) convertView.findViewById(R.id.detail_comments_msg);
			holder.commentTime = (TextView) convertView.findViewById(R.id.detail_comments_time);
			holder.msgLinear = (LinearLayout) convertView.findViewById(R.id.detail_comments_msg_linear);
			holder.replyLinear = (LinearLayout) convertView.findViewById(R.id.detail_comments_reply_linear);
			convertView.setTag(holder);
			return convertView;
		}

		@Override
		protected View hasConvertView(int position, View convertView,
				ViewGroup parent) 
		{
			holder = (ViewHolder) convertView.getTag();
			return convertView;
		}

		@Override
		protected View initConvertView(final int position, View convertView,
				ViewGroup parent)
		{
			
			//根据图片URL去查找内存缓存有没有对应的Bitmap对象，并传递回调方法，如果没有，则等下载完毕回调  
			Bitmap bitmap = asyncLoader.loadBitmap(holder.head   
					// 注意：此下载地址在服务端的逻加上是不管客户有无缓存都会传过来头像文件数据的，但在此处却不需要顾及它，
					// 因为此处使用的缓存逻辑是独立的：它先判断本地用无该文件，如果没有的时候才会用此URL取头像数据，所以此
					// URL要一定能取的到头像数据就对了
					, AvatarHelper.getUserAvatarDownloadURL(mContext,list.get(position).getComment_id()) 
					,list.get(position).getComment_user_avatar() //, rowData.getUserAvatarFileName()
					, new ImageCallBack()  
					{  
						@Override  
						public void imageLoad(ImageView imageView, Bitmap bitmap)  
						{  
//						Log.w(GoodDetailActivity.class.getSimpleName(), "【MALL】See here!!!!!!!!!!!!!!!!!!!!!"+bitmap.getWidth());
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
			
			holder.nickName.setText(list.get(position).getComment_nickname());
			holder.commentMsg.setText(list.get(position).getComment_content());
			holder.commentTime.setText(list.get(position).getComment_time());
			
//			holder.msgLinear.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View arg0)
//				{
//				     //点击评论内容	
//					if(listener != null)
//						listener.onCommentsClick(list.get(position));
//				}
//			});
			
			if(list.get(position).getReply_user_id() != null && !list.get(position).getReply_user_id().isEmpty())
			{
				holder.replyLinear.setVisibility(View.VISIBLE);
				holder.replyName.setText(list.get(position).getReply_nickname());
			}
			else
			{
				holder.replyLinear.setVisibility(View.GONE);
			}
			
			return convertView;
		}
		
	}
	
	private class UserInfoUIWrapper
	{
		private Activity parentActivity;
		private BoldCircularImage head;
		private TextView nickName;
		private TextView label;
		private TextView bestRecord;
		private TextView currentSteps;
		private TextView address;
		private ImageView addressIcon;
		private ImageView sex;
	
		
		public UserInfoUIWrapper(Activity parentActivity) 
		{
			this.parentActivity = parentActivity;
			initView();
		}
		
		private void initView()
		{
			head = (BoldCircularImage) parentActivity.findViewById(R.id.user_detial_head);
			head.setBorderWidth(ToolKits.dip2px(parentActivity, 3));
			nickName = (TextView) parentActivity.findViewById(R.id.user_detial_nickName);
			label = (TextView) parentActivity.findViewById(R.id.user_detial_label);
			bestRecord = (TextView) parentActivity.findViewById(R.id.user_detial_best_record);
			currentSteps = (TextView) parentActivity.findViewById(R.id.user_detial_current_steps);
			address = (TextView) parentActivity.findViewById(R.id.user_detial_address);
			addressIcon = (ImageView) parentActivity.findViewById(R.id.user_detial_address_icon);
			sex = (ImageView) parentActivity.findViewById(R.id.user_detial_sex);
		}
		
		public void updateAddress(String address)
		{
			this.address.setText(address);
		}
		
	    public void updateLabel()
	    {
	    	if(u != null && u.getUser_id().equals(fromUserID))
	    	{
	    		label.setText(u.getWhat_s_up());
	    	}
	    }
		
		public void update(UserProfile profile)
		{
			//根据图片URL去查找内存缓存有没有对应的Bitmap对象，并传递回调方法，如果没有，则等下载完毕回调  
			Bitmap bitmap = asyncLoader.loadBitmap(head   
					// 注意：此下载地址在服务端的逻加上是不管客户有无缓存都会传过来头像文件数据的，但在此处却不需要顾及它，
					// 因为此处使用的缓存逻辑是独立的：它先判断本地用无该文件，如果没有的时候才会用此URL取头像数据，所以此
					// URL要一定能取的到头像数据就对了
					, AvatarHelper.getUserAvatarDownloadURL(parentActivity, profile.getUser_id()) 
					,profile.getUserAvatar() //, rowData.getUserAvatarFileName()
					, new ImageCallBack()  
					{  
						@Override  
						public void imageLoad(ImageView imageView, Bitmap bitmap)  
						{  
//						Log.w(GoodDetailActivity.class.getSimpleName(), "【MALL】See here!!!!!!!!!!!!!!!!!!!!!"+bitmap.getWidth());
							imageView.setImageBitmap(bitmap);  
						}  
					}
					// 指定生成的Bitmap对象所描述的图片的大小(更小的尺寸将成倍地减小内存消耗)
					, 120 , 120 // 此头像用于好友列表中的好友头像，大小参考：@drawable/head_bg_hole.png
			);  

			if(bitmap == null)  
			{  
			       head.setImageResource(R.drawable.mini_avatar_shadow_rec);
			}  
			else  
				   head.setImageBitmap(bitmap);
			
			nickName.setText(profile.getNickname());
				label.setText(profile.getWhat_s_up());
			
			if(!profile.getLatitude().isEmpty() && !profile.getLongitude().isEmpty())
			{
			/*	ReverseGeoCodeOption option = new ReverseGeoCodeOption();
				LatLng latLng = new LatLng(Float.parseFloat(profile.getLatitude()), Float.parseFloat(profile.getLongitude()));
				option.location(latLng);
				GeoCoder  geocoder = GeoCoder.newInstance();
				geocoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
					
					@Override
					public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) 
					{
						
						AddressComponent component = result.getAddressDetail();
						if(!component.city.isEmpty())
						{
							addressIcon.setVisibility(View.VISIBLE);
							userInfoUIWrapper.updateAddress(component.city);
						}
						GeoCoder.newInstance().destroy();
					}
					
					@Override
					public void onGetGeoCodeResult(GeoCodeResult arg0) {
						
					}
				});
			    
			    if(geocoder.reverseGeoCode(option))
			    {
			    	Log.d(TAG, "反地理编码成功");
			    }
			    else
			    {
			    	Log.d(TAG, "反地理编码失败");
				}*/
			}
			
			if("0".equals(profile.getUser_sex()))
			{
				sex.setBackgroundResource(R.drawable.sex_women);
			}
			else
			{
				sex.setBackgroundResource(R.drawable.sex_man);
			}
			
			if(profile.getMax_distance() == null || profile.getMax_distance().isEmpty())
			{
				bestRecord.setText("0");
			}
			else
			{
				bestRecord.setText(profile.getMax_distance());
			}
			
			if(profile.getDistance() == null || profile.getDistance().isEmpty())
			{
				currentSteps.setText("0");
			}
			else
			{
				currentSteps.setText(profile.getDistance());
			}
			
			if(!u.getUser_id().equals(fromUserID))
			{
				titleRightBtn.setVisibility(View.VISIBLE);
				if(profile.getAttention().equals("0"))
				{
				     titleRightBtn.setText("");
				     titleRightBtn.setBackgroundResource(R.drawable.title_add);
				     mAttention = false;
				}
				else 
				{
					titleRightBtn.setText($$(R.string.relationship_cancel_attention));
					titleRightBtn.setBackgroundResource(R.color.transparent);
					mAttention = true;
				}
			}
			else
			{
				titleRightBtn.setVisibility(View.GONE);
			}
			
			if(profile.isVirtual())
			{
				titleRightBtn.setVisibility(View.GONE);
				commentsUIWrapper.isVirtual();
			}
		}
	}
	
	private class CommentsUIWrapper
	{
		private Activity parentActivity;
		private ListView mListView;
		private LinearLayout nullDataLinear;
		private Button commentBtn;
		private Button replyBtn;
		private EditText content;
		private String comment_content; 
		
		private CommentsAdapter adapter;
		
		private List<CommentReply> commentList = new ArrayList<CommentReply>();
		
//		private String commentsUserID;
//		private String commentsNickName; 
		
		private CommentReply mComment;
		
		public CommentsUIWrapper(Activity parentActivity)
		{
			this.parentActivity = parentActivity;
			initView();
		}
		
		private void initView()
		{
			mListView = (ListView) parentActivity.findViewById(R.id.user_detial_comments_list);
			commentBtn = (Button) parentActivity.findViewById(R.id.user_detial_comment_btn);
			replyBtn = (Button) parentActivity.findViewById(R.id.user_detial_reply_btn);
			content = (EditText) parentActivity.findViewById(R.id.user_detial_comments_edittext);
			nullDataLinear = (LinearLayout) parentActivity.findViewById(R.id.user_detail_activity_null_data_LL);
			
			adapter = new CommentsAdapter(parentActivity, commentList);
			mListView.setAdapter(adapter);
			
			mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int index, long arg3) 
				{
					if(commentList.get(index).getComment_user_id().equals(u.getUser_id()))
					{
						WidgetUtils.showToast(parentActivity, getString(R.string.relationship_not_reply_self), ToastType.INFO);
						return;
					}
					
					mComment = commentList.get(index);
					replyBtn.setVisibility(View.VISIBLE);
					commentBtn.setVisibility(View.GONE);
					ToolKits.ShowKeyboard(content);
					content.setHint($$(R.string.relationship_reply)+mComment.getComment_nickname()+":");
				}
			});
			
//			adapter.setOnCommentsClickListener(new OnCommentsClickListener() {
//				
//				@Override
//				public void onCommentsClick(CommentReply  comment)
//				{
//					mComment = comment;
//					replyBtn.setVisibility(View.VISIBLE);
//					commentBtn.setVisibility(View.GONE);
//					ToolKits.ShowKeyboard(content);
//					content.setHint($$(R.string.relationship_reply)+comment.getComment_nickname()+":");
//				}
//			});
			
			replyBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) 
				{
					comment_content = content.getEditableText().toString();
					if(comment_content.isEmpty())
					{
					    WidgetUtils.showToast(parentActivity, getString(R.string.relationship_reply_empty), ToastType.INFO);
						return;
					}
					
					ToolKits.HideKeyboard(content);
				
					
					 CommentReply reply = new CommentReply();
					 reply.setComment_content(comment_content);
					 reply.setUser_id(fromUserID);
					 reply.setUser_time(sdf.format(daySwitcher.getStartDate()));
					 reply.setComment_user_id(u.getUser_id());
					 reply.setReply_user_id(mComment.getComment_user_id());
					loadData(false, HttpSnsHelper.GenerateAddCommentParams(reply), REQ_REPLY);
				}
			});
			
			commentBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0)
				{
					comment_content = content.getEditableText().toString();
					if(comment_content.isEmpty())
					{
					    WidgetUtils.showToast(parentActivity, getString(R.string.relationship_comment_empty), ToastType.INFO);
						return;
					}
					ToolKits.HideKeyboard(content);
					 CommentReply reply = new CommentReply();
					 reply.setComment_content(comment_content);
					 reply.setUser_id(fromUserID);
					 reply.setUser_time(sdf.format(daySwitcher.getStartDate()));
					 reply.setComment_user_id(u.getUser_id());
				     loadData(false,HttpSnsHelper.GenerateAddCommentParams(reply),REQ_ADD_COMMENT);
				}
			});
		}
		
		
		public void isVirtual()
		{
			content.setVisibility(View.INVISIBLE);
			replyBtn.setVisibility(View.INVISIBLE);
			commentBtn.setVisibility(View.INVISIBLE);
		}
		
		public void update( List<CommentReply> list)
		{
			if(list.isEmpty())
			{
				mListView.setVisibility(View.GONE);
				nullDataLinear.setVisibility(View.VISIBLE);
			}
			else
			{
				mListView.setVisibility(View.VISIBLE);
				nullDataLinear.setVisibility(View.GONE);
			}
			commentList.clear();
			commentList.addAll(list);
			adapter.notifyDataSetChanged();
		}
		
		public void clearReplyState()
		{
			replyBtn.setVisibility(View.GONE);
			commentBtn.setVisibility(View.VISIBLE);
			ToolKits.HideKeyboard(content);
			content.setHint("");
			content.setText("");
		}
		
		public void replySuccess()
		{
			replyBtn.setVisibility(View.GONE);
			commentBtn.setVisibility(View.VISIBLE);
			CommentReply reply = new CommentReply();
			reply.setComment_content(comment_content);
			reply.setComment_id(u.getUser_id());
			reply.setReply_user_id(mComment.getComment_id());
			reply.setReply_nickname(mComment.getComment_nickname());
			reply.setComment_nickname(u.getNickname());
			reply.setComment_time(sdf2.format(new Date()));
			reply.setComment_user_id(u.getUser_id());
			commentList.add(0,reply);
			adapter.notifyDataSetChanged();
			content.setText("");
			content.setHint("");
			mListView.setVisibility(View.VISIBLE);
			nullDataLinear.setVisibility(View.GONE);
			
			if(fromTime.equals(daySwitcher.getStartDateStr()))
			{
				 resultCount++;
			}
//			
//			commentList.add(arg0)
//		    dddddd
		//	loadData(false, HttpSnsHelper.GenerateCommentListParams(fromUserID,sdf.format(new Date())), REQ_COMMENTS_LIST);
		}
		
		public void commentSuccess()
		{
			CommentReply reply = new CommentReply();
			reply.setComment_content(comment_content);
			reply.setUser_id(u.getUser_id());
			reply.setComment_id(u.getUser_id());
			reply.setComment_user_avatar(u.getUser_avatar_file_name());
			reply.setComment_nickname(u.getNickname());
			reply.setComment_time(sdf2.format(new Date()));
			reply.setComment_user_id(u.getUser_id());
			commentList.add(0,reply);
			adapter.notifyDataSetChanged();
			content.setText("");
			mListView.setVisibility(View.VISIBLE);
			nullDataLinear.setVisibility(View.GONE);
			
			if(fromTime.equals(daySwitcher.getStartDateStr()))
			{
				 resultCount++;
			}
			
			//loadData(false, HttpSnsHelper.GenerateCommentListParams(fromUserID,sdf.format(new Date())), REQ_COMMENTS_LIST);
		}
	}
	
	
	@Override
	public void onBackPressed() 
	{
		Intent intent = new Intent();
		intent.putExtra("count", resultCount);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

}
