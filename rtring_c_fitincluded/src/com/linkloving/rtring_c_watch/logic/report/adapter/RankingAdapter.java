/**
 * HistoryListAdapter.java
 * @author Jason Lu
 * @date 2013-11-4
 * @version 1.0
 */
package com.linkloving.rtring_c_watch.logic.report.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.eva.android.platf.std.AutoUpdateDaemon;
import com.eva.android.widget.AListAdapter2;
import com.eva.android.widget.AsyncBitmapLoader;
import com.eva.android.widget.AsyncBitmapLoader.ImageCallBack;
import com.eva.android.widget.DataLoadingAsyncTask;
import com.eva.epc.common.util.CommonUtils;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.eva.epc.core.dto.SysActionConst;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.linkloving.rtring_c_watch.logic.more.avatar.AvatarHelper;
import com.linkloving.rtring_c_watch.utils.DateSwitcher.PeriodSwitchType;
import com.linkloving.rtring_c_watch.utils.IntentFactory;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.JobDispatchConst;
import com.rtring.buiness.logic.dto.Ranking;

/**
 * @author Jason
 * 
 */
public class RankingAdapter extends AListAdapter2<Ranking>
{

	private static final String TYPE_DING = "0";
	
	private static final String TYPE_CAI = "1";
	
	/** 记录选中的ListView的行索引值以备后用（目前是在：单击、长按2类事件中保存了此索引值）. */
	protected int selectedListViewIndex = -1;
	
	private AsyncBitmapLoader asyncLoader = null; 
	
	private boolean hasSelf = false;
	
	private int periodSwitchType = 0;
	
	private String user_time;
	
	
	public String getUser_time()
	{
		return user_time;
	}

	public void setUser_time(String user_time)
	{
		this.user_time = user_time;
	}

	public RankingAdapter(Activity context, int periodSwitchType)
	{
		super(context, R.layout.ranking_activity_listview_item);
		this.asyncLoader = new AsyncBitmapLoader(AvatarHelper.getUserAvatarSavedDir(context)+"/");  
		this.periodSwitchType = periodSwitchType;
	}
	
	public RankingAdapter(Activity context)
	{
		super(context, R.layout.ranking_activity_listview_item);
		this.asyncLoader = new AsyncBitmapLoader(AvatarHelper.getUserAvatarSavedDir(context)+"/");  
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent)
	{
		ImageView viewAvatar = null;
		TextView viewNickname = null;
		TextView viewDistance = null;
		TextView viewRank = null;
		ViewGroup entGroup = null;
		TextView viewComeFrom = null;
		TextView viewWhatsup = null;
		
		RadioButton dingCheckBox = null;
		RadioButton caiCheckBox = null;
		Button commendBtn = null;
		
		LinearLayout praiseLinear = null;
		
		TextView viewDingValue = null;
		TextView viewCaiValue = null;
		TextView viewCommendCount = null;

		// ----------------------------------------------------------------------------------------
		// （1）UI初始化
		// 当的item布局实例已经存在（不在存，意味着这个item刚好从不可见区移到可见区时）
		// ** 根据android的列表ui实现，为了节比资源占用，假如列表有100行而可见区显示5行，那么任何时候
		// ** 这个列表中只会有5个item的UI存在，其它的item都是当被转到可见区时自动把自
		// ** 已的数据实时地更新列UI上，以便查看，也就是说item的UI并不是与后台数据一
		// ** 一对应的，所以正如API文档里说的，convertView并不能确保它总能按你的想法保持不为null
		boolean needCreateItem = (convertView == null);
		// 正在操作的列表行的数据集
		final Ranking rowData = listData.get(position);
		if (needCreateItem)
			// 明细item的UI实例化
			convertView = layoutInflater.inflate(itemResId, null);
		viewNickname = (TextView) convertView.findViewById(R.id.ranking_activity_listview_item_nickname);
		viewDistance = (TextView) convertView.findViewById(R.id.ranking_activity_listview_item_distance);
		viewAvatar = (ImageView) convertView.findViewById(R.id.ranking_activity_listview_item_imageView);
		viewRank = (TextView) convertView.findViewById(R.id.ranking_activity_listview_item_rank);
		entGroup = (ViewGroup) convertView.findViewById(R.id.ranking_list_item_come_from_LL);
		viewComeFrom = (TextView) convertView.findViewById(R.id.ranking_list_item_come_from);
		viewWhatsup = (TextView) convertView.findViewById(R.id.ranking_list_item_what_s_up);
		
		dingCheckBox = (RadioButton) convertView.findViewById(R.id.ranking_list_item_dingBtn);
		caiCheckBox = (RadioButton) convertView.findViewById(R.id.ranking_list_item_caiBtn);
		commendBtn = (Button) convertView.findViewById(R.id.ranking_list_item_commendBtn);
		
		viewDingValue = (TextView) convertView.findViewById(R.id.ranking_list_item_dingValue);
		viewCaiValue = (TextView) convertView.findViewById(R.id.ranking_list_item_caiValue);
		viewCommendCount = (TextView) convertView.findViewById(R.id.ranking_list_item_commendCount);
		
		praiseLinear = (LinearLayout) convertView.findViewById(R.id.ranking_prise_linear);

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
		viewDistance.setText(rowData.getDistance());
		viewWhatsup.setText(rowData.getWhat_s_up());
		viewRank.setText(rowData.getRank());//position + 1 + "");
		
		viewCommendCount.setText(rowData.getCommend_count().equals("0") ? "" : rowData.getCommend_count());
		viewCaiValue.setText(rowData.getCai().equals("0") ? "" : rowData.getCai());
		viewDingValue.setText(rowData.getZan().equals("0") ? "" : rowData.getZan());
		
		dingCheckBox.setChecked(rowData.getYizan().equals("1"));
		caiCheckBox.setChecked(rowData.getYicai().equals("1"));
		
		commendBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				  String userID = getListData().get(position).getUser_id();
				  Intent intent = IntentFactory.createUserDetialActivityIntent((Activity)context, userID);
				  context.startActivity(intent);
			}
		});
		
		if(getListData().get(position).isVirtual())
		{
			praiseLinear.setVisibility(View.GONE);
		}
		else
		{
			praiseLinear.setVisibility(View.VISIBLE);
		}
		
		if(dingCheckBox.isChecked() || caiCheckBox.isChecked())
		{
			dingCheckBox.setClickable(false);
			caiCheckBox.setClickable(false);
		}
		else
		{
			dingCheckBox.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{ 
					if(rowData.getYizan().equals("0") && rowData.getYicai().equals("0"))
					{
						new DataAsyncTask().execute(new String[]{TYPE_DING, rowData.getUser_id()});
						rowData.setYizan("1");
						rowData.setZan((Integer.parseInt(rowData.getZan()) + 1) + "");
						notifyDataSetChanged();
					}
				}
			});
			
			caiCheckBox.setOnClickListener(new OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					if(rowData.getYizan().equals("0") && rowData.getYicai().equals("0"))
					{
						new DataAsyncTask().execute(new String[]{TYPE_CAI, rowData.getUser_id()});
						rowData.setYicai("1");
						rowData.setCai((Integer.parseInt(rowData.getCai()) + 1) + "");
						notifyDataSetChanged();
					}
				}
			});
		}
		
		if(CommonUtils.isStringEmpty(rowData.getCome_from()) || periodSwitchType == PeriodSwitchType.ent)
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

		if(position > 2)
			viewRank.setBackground(getContext().getResources().getDrawable(R.drawable.ranking_other_icon));
		else
			viewRank.setBackground(getContext().getResources().getDrawable(R.drawable.ranking_top_three_icon));
		
		
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
							RankingAdapter.this.notifyDataSetChanged();
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
	
	
	
	/**
	 * 提交数据请求和处理的异步执行线程实现类.
	 */
	protected class DataAsyncTask extends DataLoadingAsyncTask<String, Integer, DataFromServer>
	{
		public DataAsyncTask()
		{
			super(getContext(), false);
		}
		
		private String type;
		
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
			type = params[0];
			JSONObject dataObj = new JSONObject();
			dataObj.put("user_time", user_time);
			dataObj.put("type", type);
			dataObj.put("user_id", params[1]);
			dataObj.put("praise_user_id", MyApplication.getInstance(getContext()).getLocalUserInfoProvider().getUser_id());
			return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(
					DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_THIRD_PARTY_LOGIC)
					.setJobDispatchId(JobDispatchConst.THIRD_PARTY_LOGIC_BASE)
					.setActionId(SysActionConst.ACTION_APPEND3)
					.setNewData(dataObj.toJSONString()));
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
			
		}
	}
}

