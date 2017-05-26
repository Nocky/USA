package com.linkloving.rtring_c_watch.logic.sns;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.eva.android.widget.AsyncBitmapLoader;
import com.eva.android.widget.AsyncBitmapLoader.ImageCallBack;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.logic.DataLoadableMultipleAcitvity;
import com.linkloving.rtring_c_watch.logic.main.slidemenu.CommonAdapter;
import com.linkloving.rtring_c_watch.logic.more.avatar.AvatarHelper;
import com.linkloving.rtring_c_watch.utils.HttpSnsHelper;
import com.linkloving.rtring_c_watch.utils.IntentFactory;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.rtring.buiness.logic.dto.UserEntity;
/**
 * 评论我的
 * @author Administrator
 *
 */
public class CommentsActivity extends DataLoadableMultipleAcitvity 
{
	private final static String REQ_COMMENTS = "req_comments";
	private static final String REQ_MARK_UNREAD = "req_mark_unread";
	
	private PullToRefreshListView mListView;
	private LinearLayout nullDataLinear;
	private List<CommentsDTO> list = new ArrayList<CommentsDTO>();
	
	private SimpleDateFormat sdf = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD);
	private CommentsAdapter mAdapter;
	
	private AsyncBitmapLoader asyncLoader = null; 
	
	private UserEntity u;
	
	private int unread;
	
	

	@Override
	protected void initViews() 
	{
		super.initViews();
		setContentView(R.layout.activity_comments);
		mListView = (PullToRefreshListView) findViewById(R.id.comments_list);
		mListView.getRefreshableView().setDivider(ToolKits.getRepetDrawable(this,R.drawable.list_view_deliver));
		nullDataLinear = (LinearLayout) findViewById(R.id.comments_activity_null_data_LL);
        mListView.getRefreshableView().setDividerHeight(1);
    	mAdapter = new CommentsAdapter(this, list);
    	mListView.setAdapter(mAdapter);
    	u = MyApplication.getInstance(this).getLocalUserInfoProvider();
  	    
  	  	asyncLoader = new AsyncBitmapLoader(AvatarHelper.getUserAvatarSavedDir(this)+"/");  
	}
	
	@Override
	protected void initListeners()
	{
		super.initListeners();
		mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) 
			{
				if(u != null)
					 loadData(false, HttpSnsHelper.GenerateCommentRoughListParams(u.getUser_id()),REQ_COMMENTS);
			}
		});
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3)
			{
			    String count =  list.get(index-1<0?0:index-1).unread_count;
			    if(count != null && !count.isEmpty())
			    {
			    	unread = Integer.parseInt(count);
			    	if(unread > 0)
			    	{
			    		loadData(false, HttpSnsHelper.GenerateMarkUnreadParams( u.getUser_id(), list.get(index-1<0?0:index-1).user_time), REQ_MARK_UNREAD);
			    	}
			    }
				Intent intent = IntentFactory.createUserDetialActivityIntent(CommentsActivity.this, u.getUser_id(),list.get(index-1<0?0:index-1).user_time);
				startActivity(intent);
			}
		});
	}
	
	
	
	@Override
	protected void refreshToView(String taskName,Object taskObj, Object paramObject)
	{
		   if(taskName.equals(REQ_COMMENTS))
           {
        	   if(paramObject == null || ((String)paramObject).isEmpty())
  			   { 
        		   nullDataLinear.setVisibility(View.VISIBLE);
  	   		       mListView.setVisibility(View.GONE);
  				   mListView.onRefreshComplete();
  				    return;
  			   }
        	   
        	   list.clear();
        	   List<CommentsDTO> temp = new Gson().fromJson((String)paramObject, new TypeToken<List<CommentsDTO>>(){}.getType());
        	   if(temp.isEmpty())
        	   {
        		   mListView.setVisibility(View.GONE);
        		   nullDataLinear.setVisibility(View.VISIBLE);
        	   }
        	   else
        	   {
        		   mListView.setVisibility(View.VISIBLE);
        		   nullDataLinear.setVisibility(View.GONE);
			   }
        	   list.addAll(temp);
        	   mAdapter.notifyDataSetChanged();
        	   mListView.onRefreshComplete();
           }
			else if (taskName.equals(REQ_MARK_UNREAD))
			{
				if("true".equals((String)paramObject))
				{
					 int temp = MyApplication.getInstance(CommentsActivity.this).getCommentNum();
					 temp -= unread;
					 MyApplication.getInstance(CommentsActivity.this).setCommentNum(temp);
				}
			}
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		   if(u != null)
	    	      loadData(REQ_COMMENTS, HttpSnsHelper.GenerateCommentRoughListParams(u.getUser_id()));
		   updateUnreadCount();
	}
	
	
	  private class  CommentsAdapter extends CommonAdapter<CommentsDTO>
	     {
	    	 
	    	 public class ViewHolder
	    	 {
	    		 public TextView time;
	    		 public TextView comment;
	    		 public TextView nickName;
	    		 public TextView count;
	    		 public TextView commentTime;
	    		 public ImageView head;
	    		 public TextView unRead;
	    	 }
	    	 
	    	 private ViewHolder holder;

			public CommentsAdapter(Context context, List<CommentsDTO> list) 
			{
				super(context, list);
			}

			@Override
			protected View noConvertView(int position, View convertView,
					ViewGroup parent) 
			{
				convertView = inflater.inflate(R.layout.list_item_comments, parent, false);
				holder = new ViewHolder();
				holder.time = (TextView) convertView.findViewById(R.id.comments_time);
				holder.count = (TextView) convertView.findViewById(R.id.comments_number);
				holder.comment = (TextView) convertView.findViewById(R.id.comment);
				holder.commentTime = (TextView) convertView.findViewById(R.id.time);
				holder.head = (ImageView) convertView.findViewById(R.id.head);
				holder.nickName = (TextView) convertView.findViewById(R.id.nickName);
				holder.unRead = (TextView) convertView.findViewById(R.id.comments_item_unread_text);
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
			protected View initConvertView(int position, View convertView,
					ViewGroup parent) {
				holder.count.setText(list.get(position).comment_count);
				holder.time.setText(list.get(position).user_time);
				//根据图片URL去查找内存缓存有没有对应的Bitmap对象，并传递回调方法，如果没有，则等下载完毕回调  
				Bitmap bitmap = asyncLoader.loadBitmap(holder.head   
						// 注意：此下载地址在服务端的逻加上是不管客户有无缓存都会传过来头像文件数据的，但在此处却不需要顾及它，
						// 因为此处使用的缓存逻辑是独立的：它先判断本地用无该文件，如果没有的时候才会用此URL取头像数据，所以此
						// URL要一定能取的到头像数据就对了
						, AvatarHelper.getUserAvatarDownloadURL(mContext, list.get(position).comment_user_id) 
						, list.get(position).user_avatar_file_name//, rowData.getUserAvatarFileName()
						, new ImageCallBack()  
						{  
							@Override  
							public void imageLoad(ImageView imageView, Bitmap bitmap)  
							{  
//							Log.w(GoodDetailActivity.class.getSimpleName(), "【MALL】See here!!!!!!!!!!!!!!!!!!!!!"+bitmap.getWidth());
								imageView.setImageBitmap(bitmap);  
								
								// ## 非常奇怪的一个问题：当网络下载的图片完成时会回调至此，但图片数据明
								// ## 明有了却不能刷新显示之，目前为了它能显示就低效地notifyDataSetChanged
								// ## 一下吧，以后看看什么方法可以单独刷新（否则每一次都得刷新所有可见区），
								// ## 有可能是android的listview机制性问题
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
				
				String unRead = list.get(position).unread_count;
				if(unRead != null && !unRead.isEmpty())
				{
					if(Integer.parseInt(unRead) > 0)
					{
						holder.unRead.setVisibility(View.VISIBLE);
						holder.unRead.setText(unRead);
					}
					else
					{
						holder.unRead.setVisibility(View.GONE);
					}
				
				}
				else
				{
					holder.unRead.setVisibility(View.GONE);
				}
				
				holder.comment.setText(list.get(position).comment_content);
				holder.commentTime.setText(list.get(position).comment_time);
				holder.nickName.setText(list.get(position).nickname);
				return convertView;
			}
	    	 
	     }
	  
	  private class CommentsDTO
	  {
		  public String user_id;
		  public String comment_user_id;
		  public String user_time;
		  public String comment_content;
		  public String  comment_time;
		  public String  nickname;
		  public String user_avatar_file_name;
		  public String  comment_count;
		  public String unread_count;
	  }
	  
	  public void updateUnreadCount()
	  {
		  mAdapter.notifyDataSetChanged();
	  }
}
