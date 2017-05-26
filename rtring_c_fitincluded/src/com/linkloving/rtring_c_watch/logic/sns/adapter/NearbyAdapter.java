/**
 * HistoryListAdapter.java
 * @author Jason Lu
 * @date 2013-11-4
 * @version 1.0
 */
package com.linkloving.rtring_c_watch.logic.sns.adapter;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.eva.android.widget.AListAdapter2;
import com.eva.android.widget.AsyncBitmapLoader;
import com.eva.android.widget.AsyncBitmapLoader.ImageCallBack;
import com.eva.epc.common.util.CommonUtils;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.logic.more.avatar.AvatarHelper;
import com.linkloving.rtring_c_watch.utils.IntentFactory;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.rtring.buiness.logic.dto.UserSelected;

/**
 * @author Jason
 * 
 */
public class NearbyAdapter extends AListAdapter2<UserSelected>
{

	/** 记录选中的ListView的行索引值以备后用（目前是在：单击、长按2类事件中保存了此索引值）. */
	protected int selectedListViewIndex = -1;
	
	private AsyncBitmapLoader asyncLoader = null; 
	
	private String user_time;
	
	private Activity context;
	
	
	public String getUser_time()
	{
		return user_time;
	}

	public void setUser_time(String user_time)
	{
		this.user_time = user_time;
	}

	public NearbyAdapter(Activity context)
	{
		super(context, R.layout.nearby_activity_listview_item);
		this.context = context;
		this.asyncLoader = new AsyncBitmapLoader(AvatarHelper.getUserAvatarSavedDir(context)+"/");  
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		ImageView viewAvatar = null;
		TextView viewNickname = null;
		TextView viewDesc = null;
		ViewGroup entGroup = null;
		TextView viewComeFrom = null;
		TextView viewWhatsup = null;
		

		// ----------------------------------------------------------------------------------------
		// （1）UI初始化
		// 当的item布局实例已经存在（不在存，意味着这个item刚好从不可见区移到可见区时）
		// ** 根据android的列表ui实现，为了节比资源占用，假如列表有100行而可见区显示5行，那么任何时候
		// ** 这个列表中只会有5个item的UI存在，其它的item都是当被转到可见区时自动把自
		// ** 已的数据实时地更新列UI上，以便查看，也就是说item的UI并不是与后台数据一
		// ** 一对应的，所以正如API文档里说的，convertView并不能确保它总能按你的想法保持不为null
		boolean needCreateItem = (convertView == null);
		// 正在操作的列表行的数据集
		final UserSelected rowData = listData.get(position);
		if (needCreateItem)
			// 明细item的UI实例化
			convertView = layoutInflater.inflate(itemResId, parent,false);
		viewNickname = (TextView) convertView.findViewById(R.id.nearby_activity_listview_item_nickname);
		viewDesc = (TextView) convertView.findViewById(R.id.nearby_activity_listview_item_distance);
		viewAvatar = (ImageView) convertView.findViewById(R.id.nearby_activity_listview_item_imageView);
		entGroup = (ViewGroup) convertView.findViewById(R.id.nearby_list_item_come_from_LL);
		viewComeFrom = (TextView) convertView.findViewById(R.id.nearby_list_item_come_from);
		viewWhatsup = (TextView) convertView.findViewById(R.id.nearby_list_item_what_s_up);
		

		// ----------------------------------------------------------------------------------------
		// （2）增加事件处理器
		// 各操作组件的事件监听器只需要在convertView被实例化时才需要重建（convertView需要被实例化
		// 当然就意味着它上面的所有操作组件都已经重新新建了）
		// ** 关于事件处理器的说明：事件处理器的增加其实可以不这么麻烦，直接每getView一次就给组件new一个处理器，
		// ** 这样做的好处是简单，但显然存在资源浪费（每刷新一次view就新建监听器）。而现在的实现就跟Android的列表
		// ** 实现原理一样，在切换到下一组item前，监听器永远只有一屏item的数量（刷新时只需要即时刷新对应item的数据到
		// ** 它的监听器里），这样就节省了资源开销！
		if (needCreateItem)
		{
			//
		}
		
		// ----------------------------------------------------------------------------------------
		// （3）
		// 给标签设置值，以供用户查看
		viewNickname.setText(rowData.getNickname());
		viewDesc.setText(MessageFormat.format(context.getString(R.string.nearby_list_item_distance_desc), getDistance(rowData.getDistance(), context), context.getString(getTimeAgoResId(rowData.getTime_ago()))));
		viewWhatsup.setText(rowData.getWhat_s_up());
		
		if(CommonUtils.isStringEmpty(rowData.getCome_from()))
		{
			entGroup.setVisibility(View.GONE);
		}
		else
		{
			entGroup.setVisibility(View.VISIBLE);
			viewComeFrom.setText(rowData.getCome_from());
			viewComeFrom.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
			entGroup.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					context.startActivity(IntentFactory.createCommonWebActivityIntent((Activity)context, rowData.getEnt_url()));
				}
			});
		}
		
		if(!CommonUtils.isStringEmpty(rowData.getUserAvatar(), true))
		{
			//根据图片URL去查找内存缓存有没有对应的Bitmap对象，并传递回调方法，如果没有，则等下载完毕回调  
			Bitmap bitmap = asyncLoader.loadBitmap(viewAvatar   
					// 注意：此下载地址在服务端的逻加上是不管客户有无缓存都会传过来头像文件数据的，但在此处却不需要顾及它，
					// 因为此处使用的缓存逻辑是独立的：它先判断本地用无该文件，如果没有的时候才会用此URL取头像数据，所以此
					// URL要一定能取的到头像数据就对了
					, AvatarHelper.getUserAvatarDownloadURL(context, rowData.getUser_id()) 
					, rowData.getUserAvatar() //, rowData.getUserAvatarFileName()
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
							NearbyAdapter.this.notifyDataSetChanged();
						}  
					}
					// 指定生成的Bitmap对象所描述的图片的大小(更小的尺寸将成倍地减小内存消耗)
					, 120 , 120 // 此头像用于好友列表中的好友头像，大小参考：@drawable/head_bg_hole.png
			);  

			if(bitmap == null)  
			{  
				viewAvatar.setImageResource(R.drawable.mini_avatar_shadow_rec);
			}  
			else  
				viewAvatar.setImageBitmap(bitmap);  
		}
		else
			viewAvatar.setImageResource(R.drawable.mini_avatar_shadow_rec);

		return convertView;
	}

	public int getSelectedListViewIndex()
	{
		return selectedListViewIndex;
	}

	public void setSelectedListViewIndex(int selectedListViewIndex)
	{
		this.selectedListViewIndex = selectedListViewIndex;
		// this.notifyDataSetChanged();
	}
	
	
	
	private static String getDistance(String distance, Activity context)
	{
		int d = (int) Float.parseFloat(distance);
//		Log.e("getDistance(rowData.getDistance(), context)", d+"");
//		if(d > 1000)
//			return d / 1000 + context.getString(R.string.nearby_unit_km);
//		if(d < 50)
		if(MyApplication.getInstance(context).getUNIT_TYPE().equals("Imperial")){
			return (int)(d*ToolKits.UNIT_METER_TO_MILES) + context.getString(R.string.unit_miles);
		}else{
			return (int)d + context.getString(R.string.unit_m);
		}
			
//		else
//			return (int)d + context.getString(R.string.nearby_unit_m); 
		
	}
	
	public static int getTimeAgoResId(String date)
	{
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date now = new Date();
			Date before = sdf.parse(date);
			long second =(now.getTime() - before.getTime()) / 1000;
			
			//5分钟以内
			if(second <= 5 * 60)
				return R.string.nearby_5_minute_ago;
			//10分钟以内
			if(second <= 10 * 60)
				return R.string.nearby_10_minute_ago;
			//30分钟以内
			if(second <= 30 * 60)
				return R.string.nearby_30_minute_ago;
			//1小时以内
			if(second <= 1 * 60 * 60)
				return R.string.nearby_1_hour_ago;
			//2小时以内
			if(second <= 2 * 60 * 60)
				return R.string.nearby_2_hour_ago;
			//3小时以内
			if(second <= 3 * 60 * 60)
				return R.string.nearby_3_hour_ago;
			//4小时以内
			if(second <= 4 * 60 * 60)
				return R.string.nearby_4_hour_ago;
			//5小时以内
			if(second <= 5 * 60 * 60)
				return R.string.nearby_5_hour_ago;
			//6小时以内
			if(second <= 6 * 60 * 60)
				return R.string.nearby_6_hour_ago;
			//7小时以内
			if(second <= 7 * 60 * 60)
				return R.string.nearby_7_hour_ago;
			//8小时以内
			if(second <= 8 * 60 * 60)
				return R.string.nearby_8_hour_ago;
			//9小时以内
			if(second <= 9 * 60 * 60)
				return R.string.nearby_9_hour_ago;
			//10小时以内
			if(second <= 10 * 60 * 60)
				return R.string.nearby_10_hour_ago;
			//11小时以内
			if(second <= 11 * 60 * 60)
				return R.string.nearby_11_hour_ago;
			//12小时以内
			if(second <= 12 * 60 * 60)
				return R.string.nearby_12_hour_ago;
			//13小时以内
			if(second <= 13 * 60 * 60)
				return R.string.nearby_13_hour_ago;
			//14小时以内
			if(second <= 14 * 60 * 60)
				return R.string.nearby_14_hour_ago;
			//15小时以内
			if(second <= 15 * 60 * 60)
				return R.string.nearby_15_hour_ago;
			//16小时以内
			if(second <= 16 * 60 * 60)
				return R.string.nearby_16_hour_ago;
			//17小时以内
			if(second <= 17 * 60 * 60)
				return R.string.nearby_17_hour_ago;
			//18小时以内
			if(second <= 18 * 60 * 60)
				return R.string.nearby_18_hour_ago;
			//19小时以内
			if(second <= 19 * 60 * 60)
				return R.string.nearby_19_hour_ago;
			//20小时以内
			if(second <= 20 * 60 * 60)
				return R.string.nearby_20_hour_ago;
			//21小时以内
			if(second <= 21 * 60 * 60)
				return R.string.nearby_21_hour_ago;
			//22小时以内
			if(second <= 22 * 60 * 60)
				return R.string.nearby_22_hour_ago;
			//23小时以内
			if(second <= 23 * 60 * 60)
				return R.string.nearby_23_hour_ago;
			//1天以内
			if(second <= 1 * 24 * 60 * 60)
				return R.string.nearby_1_day_ago;
			//2天以内
			if(second <= 2 * 24 * 60 * 60)
				return R.string.nearby_2_day_ago;
			//3天以内
			if(second <= 3 * 24 * 60 * 60)
				return R.string.nearby_3_day_ago;
			//4天以内
			if(second <= 4 * 24 * 60 * 60)
				return R.string.nearby_4_day_ago;
			//5天以内
			if(second <= 5 * 24 * 60 * 60)
				return R.string.nearby_5_day_ago;
			//6天以内
			if(second <= 6 * 24 * 60 * 60)
				return R.string.nearby_6_day_ago;
			//1周以内
			if(second <= 7 * 24 * 60 * 60)
				return R.string.nearby_1_week_ago;
			//2周以内
			if(second <= 2 * 7 * 24 * 60 * 60)
				return R.string.nearby_2_week_ago;
			//3周以内
			if(second <= 3 * 7 * 24 * 60 * 60)
				return R.string.nearby_3_week_ago;
			//1个月以内
			if(second <= 30 * 24 * 60 * 60)
				return R.string.nearby_1_month_ago;
		}
		catch (ParseException e)
		{
		}
		
		//未匹配到则返回很久没上
		return R.string.nearby_too_long_ago;
	}
	
}

