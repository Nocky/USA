package com.linkloving.rtring_c_watch.logic.sns;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import com.rtring.buiness.logic.dto.AttentionUser;
import com.rtring.buiness.logic.dto.UserEntity;
/**
 * 我关注的
 * @author Administrator
 *
 */
public class ConcernAboutActivity extends DataLoadableMultipleAcitvity
{
	private final static String REQ_CONCERN_ABOUT = "req_concern_about";
	
	private PullToRefreshListView mListView;
	private LinearLayout nullDataLinear;
	private List<AttentionUser> list = new ArrayList<AttentionUser>();
	
	private SimpleDateFormat sdf = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS);
	private ConcernAboutAdapter mAdapter;
	
	private UserEntity u;
	private AsyncBitmapLoader asyncLoader = null; 
	
     @Override
    protected void initViews() 
    {
    	super.initViews();
    	setContentView(R.layout.activity_concern_about);
    	 asyncLoader = new AsyncBitmapLoader(AvatarHelper.getUserAvatarSavedDir(this)+"/");  
    	
    	mListView = (PullToRefreshListView) findViewById(R.id.concern_about_list);
    	mListView.getRefreshableView().setDivider(ToolKits.getRepetDrawable(this,R.drawable.list_view_deliver));
        mListView.getRefreshableView().setDividerHeight(1);
        nullDataLinear = (LinearLayout) findViewById(R.id.concern_about_activity_null_data_LL);
        
    	mAdapter = new ConcernAboutAdapter(this, list);
    	mListView.setAdapter(mAdapter);
    	u = MyApplication.getInstance(this).getLocalUserInfoProvider();
  	   
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
		    	      loadData(false, HttpSnsHelper.GenerateConcernAboutListParams(u.getUser_id(),sdf.format(new Date())),REQ_CONCERN_ABOUT);
			}
		});
    	
    	mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				      String userID =  list.get(index-1< 0?0:index-1).getAttention_user_id();
				      Intent intent = IntentFactory.createUserDetialActivityIntent(ConcernAboutActivity.this, userID);
				      startActivity(intent);
			}
		});
    }
     
     @Override
    protected void onResume()
    {
    	super.onResume();
    	 if(u != null)
   	        loadData(REQ_CONCERN_ABOUT, HttpSnsHelper.GenerateConcernAboutListParams(u.getUser_id(),sdf.format(new Date())));
    }
     
     
     
     @Override
    protected void refreshToView(String taskName,Object taskObj, Object paramObject)
    {
           if(taskName.equals(REQ_CONCERN_ABOUT))
           {
        	   if(paramObject == null || ((String)paramObject).isEmpty())
  			   { 
  				   mListView.onRefreshComplete();
  				   nullDataLinear.setVisibility(View.VISIBLE);
	   		       mListView.setVisibility(View.GONE);
  				    return;
  			   }
        	   
        	   list.clear();
        	   List<AttentionUser> temp = new Gson().fromJson((String)paramObject, new TypeToken<List<AttentionUser>>(){}.getType());
        	   if(temp.isEmpty())
        	   {
        		   nullDataLinear.setVisibility(View.VISIBLE);
        		   mListView.setVisibility(View.GONE);
        	   }
        	   else 
        	   {
        		   nullDataLinear.setVisibility(View.GONE);
        		   mListView.setVisibility(View.VISIBLE);
			   }
        	   list.addAll(temp);
        	   mAdapter.notifyDataSetChanged();
        	   mListView.onRefreshComplete();
           }
    }
     
     
     private class ConcernAboutAdapter extends CommonAdapter<AttentionUser>
     {
    	 
    	 public class ViewHolder
    	 {
    		 public ImageView head;
    		 public TextView nickName;
    		 public TextView label;
    		 public TextView steps;
    		 public TextView unitStep;
    	 }
    	 
    	 private ViewHolder holder;

		public ConcernAboutAdapter(Context context, List<AttentionUser> list) 
		{
			super(context, list);
		}

		@Override
		protected View noConvertView(int position, View convertView,
				ViewGroup parent) 
		{
			convertView = inflater.inflate(R.layout.list_item_concern_about, parent, false);
			holder = new ViewHolder();
			holder.head = (ImageView) convertView.findViewById(R.id.head);
			holder.label = (TextView) convertView.findViewById(R.id.concern_label);
			holder.nickName = (TextView) convertView.findViewById(R.id.concern_nickname);
			holder.steps = (TextView) convertView.findViewById(R.id.concern_steps);
			holder.unitStep = (TextView) convertView.findViewById(R.id.concern_unit_step);
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
			//根据图片URL去查找内存缓存有没有对应的Bitmap对象，并传递回调方法，如果没有，则等下载完毕回调  
			Bitmap bitmap = asyncLoader.loadBitmap(holder.head   
					// 注意：此下载地址在服务端的逻加上是不管客户有无缓存都会传过来头像文件数据的，但在此处却不需要顾及它，
					// 因为此处使用的缓存逻辑是独立的：它先判断本地用无该文件，如果没有的时候才会用此URL取头像数据，所以此
					// URL要一定能取的到头像数据就对了
					, AvatarHelper.getUserAvatarDownloadURL(mContext, list.get(position).getAttention_user_id()) 
					, list.get(position).getAttention_user_id() //, rowData.getUserAvatarFileName()
					, new ImageCallBack()  
					{  
						@Override  
						public void imageLoad(ImageView imageView, Bitmap bitmap)  
						{  
//						Log.w(GoodDetailActivity.class.getSimpleName(), "【MALL】See here!!!!!!!!!!!!!!!!!!!!!"+bitmap.getWidth());
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
			
			holder.label.setText( list.get(position).getWhat_s_up());
			holder.nickName.setText(list.get(position).getNickname());
			if(list.get(position).getDistance() == null || list.get(position).getDistance().isEmpty() || list.get(position).getDistance().equals("0"))
			{
				//holder.steps.setText("0");
				holder.unitStep.setVisibility(View.INVISIBLE);
				holder.steps.setVisibility(View.INVISIBLE);
			}
			else
			{
				holder.unitStep.setVisibility(View.VISIBLE);
				holder.steps.setVisibility(View.VISIBLE);
				holder.steps.setText(list.get(position).getDistance());
			}
			return convertView;
		}
    	 
     }
     
     
}
