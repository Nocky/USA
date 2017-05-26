package com.linkloving.rtring_c_watch.logic.sns;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.eva.android.widget.AsyncBitmapLoader;
import com.eva.android.widget.AsyncBitmapLoader.ImageCallBack;
import com.eva.android.widget.util.WidgetUtils;
import com.eva.android.widget.util.WidgetUtils.ToastType;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.logic.DataLoadableMultipleAcitvity;
import com.linkloving.rtring_c_watch.logic.main.slidemenu.CommonAdapter;
import com.linkloving.rtring_c_watch.logic.more.avatar.AvatarHelper;
import com.linkloving.rtring_c_watch.logic.sns.model.WhatsUpDetailItem;
import com.linkloving.rtring_c_watch.logic.sns.model.WhatsUpItem;
import com.linkloving.rtring_c_watch.utils.HttpSnsHelper;
import com.linkloving.rtring_c_watch.utils.IntentFactory;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.rtring.buiness.logic.dto.UserEntity;
import com.rtring.buiness.logic.dto.UserSignature;
import com.rtring.buiness.logic.dto.UserSignatureComment;

public class WhatsUpDetailActivity extends DataLoadableMultipleAcitvity
{
	private final String REQ_DETAIL_LIST = "req_detail_list";
	private final String REQ_ADD_COMMENT = "req_add_comment";
	private final String REQ_REPLY_COMMENT = "req_reply_comment";
	
	private ImageView head;
	private TextView nickName;
	private TextView signTime;
	private TextView content;
	
	private Button commentBtn;
	private Button replyBtn;
	private EditText comments;

	private ListView mListView;
	private LinearLayout nullDataLinear;
	
	private WhatsUpItem item;
	private UserEntity u;
	
	private int commentNum;
	private int fromPos;
	
	private WhatsUpDetailAdapter adapter;
	private AsyncBitmapLoader asyncLoader = null; 
	
	private List<WhatsUpDetailItem> mList = new ArrayList<WhatsUpDetailItem>();
	
	private WhatsUpDetailItem mComment;
	private String commentStr;
	
	@Override
	protected void initDataFromIntent()
	{
		super.initDataFromIntent();
		Object[] objs = IntentFactory.parseWhatsUpDetailActivity(getIntent());
	    item = (WhatsUpItem) objs[0];
	    fromPos = (Integer) objs[1];
	}
	
	private void noData()
	{
		mListView.setVisibility(View.GONE);
		nullDataLinear.setVisibility(View.VISIBLE);
	}
	
	private void hasData()
	{
		mListView.setVisibility(View.VISIBLE);
		nullDataLinear.setVisibility(View.GONE);
	}


	@Override
	protected void initViews() 
	{
		customeTitleBarResId = R.id.whats_up_detail_titleBar;
		setContentView(R.layout.activity_whats_up_detail);
		head = (ImageView) findViewById(R.id.whats_up_detail_list_item_portrait);
		nickName = (TextView) findViewById(R.id.whats_up_detail_list_item_nickName);
		signTime = (TextView) findViewById(R.id.whats_up_detail_list_item_sign_time);
		content = (TextView) findViewById(R.id.whats_up_detail_list_item_content);
		commentBtn = (Button) findViewById(R.id.whats_up_detail_comments_btn);
		replyBtn = (Button) findViewById(R.id.whats_up_detail_reply_btn);
		comments = (EditText) findViewById(R.id.whats_up_detail_comments_editText);
		nullDataLinear = (LinearLayout) findViewById(R.id.whats_up_detail_activity_null_data_LL);
		//ToolKits.HideKeyboard(comments);
		
		setTitle($$(R.string.whats_up_detail_title));
		
		u = MyApplication.getInstance(this).getLocalUserInfoProvider();
		
		asyncLoader = new AsyncBitmapLoader(AvatarHelper.getUserAvatarSavedDir(this)+"/");
		
		if(item != null )
		{
			nickName.setText(item.getNickName());
			signTime.setText(item.getSginTime());
			content.setText(item.getContent());
			//根据图片URL去查找内存缓存有没有对应的Bitmap对象，并传递回调方法，如果没有，则等下载完毕回调  
			Bitmap bitmap = asyncLoader.loadBitmap(head   
					// 注意：此下载地址在服务端的逻加上是不管客户有无缓存都会传过来头像文件数据的，但在此处却不需要顾及它，
					// 因为此处使用的缓存逻辑是独立的：它先判断本地用无该文件，如果没有的时候才会用此URL取头像数据，所以此
					// URL要一定能取的到头像数据就对了
					, AvatarHelper.getUserAvatarDownloadURL(this,item.getUser_id()) 
					,item.getUser_avatar_file_name() //, rowData.getUserAvatarFileName()
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
			
			loadData(false, HttpSnsHelper.GenerateWhatsUpDetailList(item.getSign_id()), REQ_DETAIL_LIST);
		}
		
		mListView = (ListView) findViewById(R.id.whats_up_detail_listview);
		
		adapter = new WhatsUpDetailAdapter(this, mList);
		mListView.setAdapter(adapter);
	}
	
	@Override
	protected void initListeners()
	{
		super.initListeners();
		
		getCustomeTitleBar().getLeftBackButton().setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0)
			{
				Intent intent = new Intent();
				intent.putExtra("comments", commentNum);
				intent.putExtra("pos", fromPos);
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
		
		commentBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) 
			{
				
				commentStr = comments.getText().toString();
				if(!commentStr.isEmpty())
				{
					UserSignatureComment comment = new UserSignatureComment();
					comment.setComment_user_id(u.getUser_id());
					comment.setSign_id(item.getSign_id());
					comment.setComment_content(commentStr);
				     loadData(false, HttpSnsHelper.GenerateWhatsUpAddCommentsParams(comment),REQ_ADD_COMMENT);	
				 	ToolKits.HideKeyboard(comments);
				}
				else 
				{
					  WidgetUtils.showToast(WhatsUpDetailActivity.this, getString(R.string.relationship_comment_empty), ToastType.INFO);
				}
			}
		});
		
		
		replyBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0)
			{
				commentStr  = comments.getText().toString();
				if(!commentStr .isEmpty())
				{
					UserSignatureComment comment = new UserSignatureComment();
					comment.setComment_user_id(u.getUser_id());
					comment.setSign_id(item.getSign_id());
					comment.setReply_user_id(mComment.getComment_user_id());
					comment.setComment_content(commentStr);
				     loadData(false, HttpSnsHelper.GenerateWhatsUpAddCommentsParams(comment),REQ_REPLY_COMMENT);	
				 	ToolKits.HideKeyboard(comments);
				}
				else 
				{
					  WidgetUtils.showToast(WhatsUpDetailActivity.this, getString(R.string.relationship_comment_empty), ToastType.INFO);
				}  
			}
		});
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) 
			{

				if(mList.get(index).getComment_user_id().equals(u.getUser_id()))
				{
					WidgetUtils.showToast(WhatsUpDetailActivity.this, getString(R.string.relationship_not_reply_self), ToastType.INFO);
					return;
				}
				
				mComment = mList.get(index);
				replyBtn.setVisibility(View.VISIBLE);
				commentBtn.setVisibility(View.GONE);
				ToolKits.ShowKeyboard(comments);
				comments.setHint($$(R.string.relationship_reply)+mComment.getComNickName()+":");
			}
		});
		
	}
	
	@Override
	protected void refreshToView(String taskName,Object taskObj, Object paramObject)
	{
		if(taskName.equals(REQ_DETAIL_LIST))
		{
			if(paramObject == null)
			{
				noData();
				return;
			}
			
			UserSignature  signature = new Gson().fromJson((String) paramObject, UserSignature.class);
			
			if(signature.getComment_list() == null || signature.getComment_list().isEmpty())
			{
				noData();
				return;
			}
				
			
			List<UserSignatureComment> list = new Gson().fromJson(signature.getComment_list(), new TypeToken<List<UserSignatureComment>>(){}.getType());
			for(UserSignatureComment item:list)
			{
				mList.add(new WhatsUpDetailItem(item));
			}
			adapter.notifyDataSetChanged();
			hasData();
		}
		else if (taskName.equals(REQ_ADD_COMMENT)) 
		{
			if(!"false".equals((String)paramObject))
			{
				if(u != null)
				{
					WhatsUpDetailItem item = new WhatsUpDetailItem(u.getNickname(),null, commentStr,u.getUser_id());
					mList.add(item);
					comments.setText("");
					comments.setHint("");
					adapter.notifyDataSetChanged();
					commentNum++;
					hasData();
				}
			}
			else 
			{
				//评论失败
				WidgetUtils.showToast(this, getString(R.string.relationship_comment_failed), ToastType.INFO);
			}
		}
		else if (taskName.equals(REQ_REPLY_COMMENT))
		{
			if(!"false".equals((String)paramObject))
			{
				if(u != null)
				{
					WhatsUpDetailItem item = new WhatsUpDetailItem(u.getNickname(), mComment.getComNickName(), commentStr, mComment.getComment_user_id());
					mList.add(item);
					adapter.notifyDataSetChanged();
					comments.setText("");
					comments.setHint("");
					commentNum++;
					replyBtn.setVisibility(View.GONE);
					commentBtn.setVisibility(View.VISIBLE);
					hasData();
				}
			}
			else 
			{
				//回复失败
				WidgetUtils.showToast(this, getString(R.string.relationship_reply_failed), ToastType.INFO);
			}
		}
	}
	
	private class WhatsUpDetailAdapter extends CommonAdapter<WhatsUpDetailItem>
	{
		public class ViewHolder
		{
			TextView nickName;
			TextView replyNickName;
			TextView comments;
			LinearLayout replyLinear;
		}
		
		private ViewHolder holder;

		public WhatsUpDetailAdapter(Context context,
				List<WhatsUpDetailItem> list)
		{
			super(context, list);
		}

		@Override
		protected View noConvertView(int position, View convertView,
				ViewGroup parent) 
		{
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.whats_up_detail_listview_item, parent, false);
			holder.comments = (TextView) convertView.findViewById(R.id.whats_up_detail_comments);
			holder.nickName = (TextView) convertView.findViewById(R.id.whats_up_detail_nickName);
			holder.replyNickName = (TextView) convertView.findViewById(R.id.whats_up_detail_reply_nickName);
			holder.replyLinear = (LinearLayout) convertView.findViewById(R.id.whats_up_detail_reply_linear);
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
		protected View initConvertView(int position, View convertView,
				ViewGroup parent)
		{
			holder.comments.setText(list.get(position).getComments());
			holder.nickName.setText(list.get(position).getComNickName());
			if(list.get(position).getReplyNickName() == null || list.get(position).getReplyNickName().isEmpty())
			{
				holder.replyLinear.setVisibility(View.GONE);
			}
			else
			{
				holder.replyLinear.setVisibility(View.VISIBLE);
				holder.replyNickName.setText(list.get(position).getReplyNickName());
			}
			return convertView;
		}
		
	}
	
	@Override
	protected void onDestroy() 
	{
		super.onDestroy();
		ToolKits.HideKeyboard(comments);
	}
	
	
	@Override
	public void onBackPressed()
	{
		Intent intent = new Intent();
		intent.putExtra("comments", commentNum);
		setResult(Activity.RESULT_OK, intent);
		intent.putExtra("pos", fromPos);
		finish();
	}

}
