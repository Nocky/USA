package com.linkloving.rtring_c_watch.logic.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eva.android.platf.std.AutoUpdateDaemon;
import com.eva.android.platf.std.DataLoadableActivity;
import com.eva.android.widget.AsyncBitmapLoader;
import com.eva.android.widget.AsyncBitmapLoader.ImageCallBack;
import com.eva.android.widget.DataLoadingAsyncTask;
import com.eva.android.widget.util.WidgetUtils;
import com.eva.android.widget.util.WidgetUtils.ToastType;
import com.eva.epc.common.util.CalendarHelper;
import com.eva.epc.common.util.CommonUtils;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.eva.epc.core.dto.SysActionConst;
import com.linkloving.band.sleep.SleepDataHelper;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.linkloving.rtring_c_watch.logic.more.avatar.AvatarHelper;
import com.linkloving.rtring_c_watch.logic.report.adapter.RankingAdapter;
import com.linkloving.rtring_c_watch.utils.DateSwitcher;
import com.linkloving.rtring_c_watch.utils.DateSwitcher.PeriodSwitchType;
import com.linkloving.rtring_c_watch.utils.IntentFactory;
import com.linkloving.rtring_c_watch.utils.ShareContentCustomize;
import com.linkloving.rtring_c_watch.utils.SkinSettingManager;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.linkloving.rtring_c_watch.utils.logUtils.MyLog;
import com.linkloving.rtring_c_watch.widget.PaginationView;
import com.linkloving.rtring_c_watch.widget.PaginationView.OnPullDownListener;
import com.linkloving.utils.TimeZoneHelper;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.JobDispatchConst;
import com.rtring.buiness.logic.dto.Ranking;
import com.rtring.buiness.logic.dto.UserEntity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import net.simonvt.datepicker.DatePickDialog;
import net.simonvt.datepicker.DatePickDialog.IgetDate;

public class RankingActivity extends DataLoadableActivity
{
	private final static String TAG = RankingActivity.class.getSimpleName();
	
	private RankingAdapter rankingAdapter;
	
	private int mIndex;
	
	private PaginationView paginationView = null; 
	private ListView rankingListView;
	
	private static final int REQ_COMMENTS = 1;
	private static final int REQ_PICK_DATE = 0;
	
	private Button btnLeft = null;
	private Button btnRight = null;
	private TextView viewTime = null;
	
	private LinearLayout nullDataLL = null;
	private DateSwitcher daySwitcher = null;
	
	private ImageView entRankView = null;
	private TextView entRankNumView = null;
	
	private UserEntity currUser = null;
	
	private View titleBar = null;
	private View titleGroup = null;
	
	private Button backBtn = null;
	private Button btnShare = null;
	private PeriodFilterUIWrapper periodFilterUIWrapper = null;
	
	private SelfUIWrapper selfUIWrapper = null;
	
	private OnPullDownListener onPullDownListener = null;
	
	private ProgressDialog loadingDialog = null;
	
	private int pageIndex = 1;
	
	private String my_steps;
	
	private boolean needRefresh = true;
	
	private SimpleDateFormat sdf = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD);
	
	
	private final String mPageName = "RankingPage";
	private SkinSettingManager mSettingManager;
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//SDK已经禁用了基于Activity 的页面统计，所以需要再次重新统计页面
//		MobclickAgent.onPageEnd(mPageName);
//		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mSettingManager=new SkinSettingManager(this);
		mSettingManager.initSkins();
		//SDK已经禁用了基于Activity 的页面统计，所以需要再次重新统计页面
//		MobclickAgent.onPageStart(mPageName);
//		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void initViews()
	{
		customeTitleBarResId = R.id.ranking_activity_titleBar;
		// 首先设置cosntentview
		setContentView(R.layout.ranking_activity);
		this.setTitle(R.string.ranking_title);
		
		this.getCustomeTitleBar().getRightGeneralButton().setVisibility(View.VISIBLE);
		this.getCustomeTitleBar().getRightGeneralButton().setBackgroundResource(R.drawable.selector_btn_share);

		titleBar = findViewById(R.id.ranking_activity_titleBar);
		titleGroup = findViewById(R.id.ranking_activity_radioGroup);
		currUser = MyApplication.getInstance(this).getLocalUserInfoProvider();
		if(CommonUtils.isStringEmpty(currUser.getEname()))
		{
			titleGroup.setVisibility(View.GONE);
			titleBar.setVisibility(View.VISIBLE);
		}
		else
		{
			titleBar.setVisibility(View.GONE);
			titleGroup.setVisibility(View.VISIBLE);
			
			backBtn = (Button) findViewById(R.id.ranking_activity_backBtn);
			btnShare =  (Button) findViewById(R.id.ranking_activity_shareBtn);
			//标题返回按钮事件处理
			backBtn.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					finish();
				}
			});
			
			btnShare.setOnClickListener(new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					loadingDialog = new ProgressDialog(RankingActivity.this);
					loadingDialog.setProgressStyle(0);
					loadingDialog.setMessage($$(R.string.detail_sport_data_image_loading));
					loadingDialog.show();
					
					new ScreenHotAsyncTask().execute(v);
				}
			});
			
			periodFilterUIWrapper = new PeriodFilterUIWrapper(this){
				@Override
				protected void onFilterChaged(int periodSwitchType)
				{
					switchedOver();
				}
			};
			
		}
		
		btnLeft = (Button)findViewById(R.id.ranking_activity_leftBtn);
		btnRight = (Button)findViewById(R.id.ranking_activity_rightBtn);
		viewTime = (TextView)findViewById(R.id.ranking_activity_dateView);
		nullDataLL = (LinearLayout) findViewById(R.id.ranking_activity_null_data_LL);
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
					WidgetUtils.showToast(RankingActivity.this, $$(R.string.ranking_wait_tomorrow), ToastType.INFO);
					return false;
				}
				return true;
			}
		};
		refreshShowText();
		
		paginationView = (PaginationView) this.findViewById(R.id.ranking_activity_list_view);
		rankingListView = paginationView.getListView();
		
		if(periodFilterUIWrapper != null)
			rankingAdapter = new RankingAdapter(this, periodFilterUIWrapper.getPeriodFilterType());
		else
			rankingAdapter = new RankingAdapter(this);
		
		rankingListView.setAdapter(rankingAdapter);
		
		View view = LayoutInflater.from(this).inflate(R.layout.ranking_activity_list_head_view, rankingListView,false);
		entRankView = (ImageView) view.findViewById(R.id.ent_ranking_image);
		entRankNumView = (TextView) view.findViewById(R.id.ent_ranking_numView);
		rankingListView.addHeaderView(view);
		
		rankingListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3)
			{
				
				String userID = "";
				//选中的是自己的数据
				if(position == 1)
					userID = MyApplication.getInstance(RankingActivity.this).getLocalUserInfoProvider().getUser_id();
				else
					userID= rankingAdapter.getListData().get(position - 2).getUser_id();
				Intent intent = IntentFactory.createUserDetialActivityIntent(RankingActivity.this, userID, sdf.format(daySwitcher.getStartDate()));
				mIndex = position - 2;
				startActivityForResult(intent, REQ_COMMENTS);
			}
		});
		
		onPullDownListener = new OnPullDownListener()
		{
			@Override
			public void onRefresh()
			{
			}
			
			/** 刷新事件接口 这里要注意的是获取更多完 要关闭 更多的进度条 notifyDidMore() **/
			@Override
			public void onMore()
			{
				needRefresh = false;
				new DataAsyncTask().execute();
			}
		};
		
		// 设置可以自动获取更多 滑到最后一个自动获取 改成false将禁用自动获取更多
		paginationView.enableAutoFetchMore(false, 1);
		// 显示并启用自动获取更多
		paginationView.setShowFooter();
		// 隐藏并且禁用头部刷新
		paginationView.setHideHeader();
		
		paginationView.setOnPullDownListener(onPullDownListener);
		
		selfUIWrapper = new SelfUIWrapper(this, currUser, periodFilterUIWrapper == null ? PeriodSwitchType.ERROR : periodFilterUIWrapper.getPeriodFilterType());

	}

	@Override
	protected void initListeners()
	{
		btnLeft.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				if(daySwitcher.previous())
					switchedOver();
			}
		});
		
		btnRight.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				if(daySwitcher.next())
					switchedOver();
			}
		});
		
		viewTime.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) 
			{
				DatePickDialog datePickDialog = new DatePickDialog(RankingActivity.this,daySwitcher.getStartDate().getTime(),new IgetDate(){
                    @Override
            		public void getDate(int year, int month, int day,long mills) {
                    	if(mills> System.currentTimeMillis())
        				{
        					WidgetUtils.showToast(RankingActivity.this, getString(R.string.date_picker_out_time), ToastType.INFO);
        				}
        				else 
        				{
        					  daySwitcher.setBaseTime(new Date(mills));
        				      switchedOver();
        				}
            		}
            	}, getString(R.string.date_picker_activity_title), getString(R.string.general_ok), getString(R.string.general_cancel));
            	datePickDialog.show();	
			}
		});
		
//		rankingListView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
//					long arg3) {
//			
//			}
//		});
		
		this.getCustomeTitleBar().getRightGeneralButton().setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				loadingDialog = new ProgressDialog(RankingActivity.this);
				loadingDialog.setProgressStyle(0);
				loadingDialog.setMessage($$(R.string.detail_sport_data_image_loading));
				loadingDialog.show();
				
				new ScreenHotAsyncTask().execute(v);
			}
		});
	}
	
	public void refreshShowText()
	{
		viewTime.setText(new SimpleDateFormat("yyyy/MM/dd").format(daySwitcher.getStartDate()));
	}
	
	private void switchedOver()
	{
		needRefresh = true;
		refreshShowText();
		this.loadData();
	}
	

	@Override
	protected DataFromServer queryData(String... arg0)
	{
		JSONObject dataObj = new JSONObject();
		String date = daySwitcher.getStartDateStr();
		rankingAdapter.setUser_time(daySwitcher.getStartDateStr());
		dataObj.put("date_str", date);
		dataObj.put("user_id", currUser.getUser_id());
		dataObj.put("page", pageIndex = 1);
		
		// 2014-07-28日 by JS：因目前汇总数据存在与明细数据不能完全对应的情况，自已查看排行榜时，每次自动
		// 汇总明细数据并回填，这样至少自已看到的排行数据能与运动数据一一对应了
		try
		{
			// 为了保持APP界面其它地方取数据范围的一致性（其它地方都是用>=今天0时和<=明天0时转UTC时间的方式取值的）
			// ，此处就不用>= XXXX 00:00:00.000 <=23:59:59.999，否则理论上真跨了这个毫秒点的话，就有点误差了（不
			// 过1毫秒的时间倒不至于，主要是强迫症）
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(SleepDataHelper.DATE_PATTERN_sdfYYMMDD.parse(date));
			gc.add(GregorianCalendar.DAY_OF_MONTH, 1); 
			String endDate = SleepDataHelper.DATE_PATTERN_sdfYYMMDD.format(gc.getTime());
			
			// 将本地时间转成UTC时间后再执行查询
			String startDateTimeUTC = TimeZoneHelper.__getUTC0FromLocalTime(
					ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS, date+" 00:00:00");
			String endDateTimeUTC = TimeZoneHelper.__getUTC0FromLocalTime(
					ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS, endDate+" 00:00:00");
			
			dataObj.put("start_datetime_utc", startDateTimeUTC);
			dataObj.put("end_datetime_utc", endDateTimeUTC);
		}
		catch (Exception e)
		{
			Log.e(TAG, e.getMessage(), e);
		}
		
		if(periodFilterUIWrapper!= null &&
				!( CommonUtils.isStringEmpty(currUser.getEid()) && currUser.getEid().equals("9999999999") ) 
				&& periodFilterUIWrapper.getPeriodFilterType() == PeriodSwitchType.ent)
			dataObj.put("ent_id", currUser.getEid());
		return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(
				DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_REPORT)
				.setJobDispatchId(JobDispatchConst.REPORT_BASE)
				.setActionId(SysActionConst.ACTION_APPEND11)
				.setNewData(dataObj.toJSONString()));
	}

	@Override
	protected void refreshToView(Object result)
	{
		if(result != null)
		{
			ArrayList<Ranking> listData = (ArrayList<Ranking>) JSON.parseArray(result.toString(), Ranking.class);
			
			if(listData.size() == 0 )
			{
				if(pageIndex > 1)
				{
					pageIndex--;
					paginationView.setNoMoreFooter();
				}
				
				if(needRefresh)
				{
					rankingAdapter.setListData(listData);
					new RankAsyncTask().execute();
				}
			}
			else
			{
				paginationView.setMoreFooter();
				
				if(needRefresh)
				{
					rankingAdapter.setListData(listData);
					/**
					 * 切换日期需要加载自己的名次
					 */
					new RankAsyncTask().execute();
				}
				else
				{
					rankingAdapter.getListData().addAll(listData);
					/**
					 * 当天首次需要加载自己的名次
					 */
					if(pageIndex == 1)
						new RankAsyncTask().execute();

				}
					
				rankingAdapter.notifyDataSetChanged();
			}
			
			if(rankingAdapter.getListData().size() == 0 & ("-1").equals(my_steps))
			{
				nullDataLL.setVisibility(View.VISIBLE);
				rankingListView.setVisibility(View.GONE);
			}
			else
			{ 	
				nullDataLL.setVisibility(View.GONE);
				rankingListView.setVisibility(View.VISIBLE);
			}

			paginationView.notifyDidMore();
		}
		else
		{
			if(pageIndex > 1)
				pageIndex--;
		}
	}
	
	
	/**
	 * 提交数据请求和处理的异步执行线程实现类.
	 */
	protected class DataAsyncTask extends DataLoadingAsyncTask<String, Integer, DataFromServer>
	{
		public DataAsyncTask()
		{
			super(RankingActivity.this, $$(R.string.general_loading));
		}
		
		/**
//		 * 在后台执行 {@link 实现登陆信息的提交和处于结果的读取 .
//		 *
//		 * @param parems
		 *            外界传进来的参数
		 * @return 查询结果，将传递给onPostExecute(..)方法
		 */
		@Override
		protected DataFromServer doInBackground(String... params)
		{
			JSONObject dataObj = new JSONObject();
			dataObj.put("date_str", daySwitcher.getStartDateStr());
			dataObj.put("user_id", currUser.getUser_id());
			if(needRefresh)
				dataObj.put("page", pageIndex = 1);
			else
				dataObj.put("page", ++pageIndex);
			if(!(CommonUtils.isStringEmpty(currUser.getEid()) && currUser.getEid().equals("9999999999")) && periodFilterUIWrapper != null && periodFilterUIWrapper.getPeriodFilterType() == PeriodSwitchType.ent)
				dataObj.put("ent_id", currUser.getEid());
			return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(
					DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_REPORT)
					.setJobDispatchId(JobDispatchConst.REPORT_BASE)
					.setActionId(SysActionConst.ACTION_APPEND11)
					.setNewData(dataObj.toJSONString()));
		}

		/**
		 * 处理服务端返回的登陆结果信息.
		 * 
		 * @see AutoUpdateDaemon
//		 * @see #needSaveDefaultLoginName()
//		 * @see #afterLoginSucess()
		 */
		protected void onPostExecuteImpl(Object result)
		{
			refreshToView(result);
		}
	}
	
	/**
	 * 提交数据请求和处理的异步执行线程实现类.
	 */
	protected class RankAsyncTask extends DataLoadingAsyncTask<String, Integer, DataFromServer>
	{
		public RankAsyncTask()
		{
			super(RankingActivity.this, false);
		}
		
		/**
		 *
		 *            外界传进来的参数
		 * @return 查询结果，将传递给onPostExecute(..)方法
		 */
		@Override
		protected DataFromServer doInBackground(String... params)
		{
			JSONObject dataObj = new JSONObject();
			dataObj.put("date_str", daySwitcher.getStartDateStr());
			dataObj.put("user_id", currUser.getUser_id());
			if(!(CommonUtils.isStringEmpty(currUser.getEid()) && currUser.getEid().equals("9999999999")) && periodFilterUIWrapper!= null && periodFilterUIWrapper.getPeriodFilterType() == PeriodSwitchType.ent)
				dataObj.put("ent_id", currUser.getEid());
			
			return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(
					DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_REPORT)
					.setJobDispatchId(JobDispatchConst.REPORT_BASE)
					.setActionId(SysActionConst.ACTION_APPEND9)
					.setNewData(dataObj.toJSONString()));
		}

		/**
		 * 处理服务端返回的登陆结果信息.
		 * 
		 * @see AutoUpdateDaemon
//		 * @see #needSaveDefaultLoginName()
//		 * @see #afterLoginSucess()
		 */
		protected void onPostExecuteImpl(Object result)
		{
			Log.i(TAG, result.toString());
			JSONObject obj = JSONObject.parseObject((String)result);
			my_steps = obj.getString("my_steps");
			String my_ranking = obj.getString("my_ranking");
			String my_ding = obj.getString("zan");
			String my_cai = obj.getString("cai");
			String my_commend = obj.getString("commend");
			
			// 我今天没有成绩
			if("-1".equals(my_steps))
			{
				entRankNumView.setVisibility(View.GONE);
				entRankView.setImageResource(R.drawable.ranking_logo_default_4);
				selfUIWrapper.viewDistanceUnit.setVisibility(View.GONE);
				selfUIWrapper.viewDistance.setText(R.string.ranking_no_data);
				selfUIWrapper.viewRank.setBackground(getResources().getDrawable(R.drawable.ranking_top_three_icon));
				selfUIWrapper.viewRank.setText("");
				selfUIWrapper.viewDing.setText(my_ding);
				selfUIWrapper.viewCai.setText(my_cai);
				selfUIWrapper.viewComments.setText(my_commend);
			}
			else
			{
				String fileName = "";
				
				// 默认名次图标
				entRankNumView.setVisibility(View.GONE);
				selfUIWrapper.viewRank.setBackground(getResources().getDrawable(R.drawable.ranking_top_three_icon));
				if ("1".equals(my_ranking))
					entRankView.setImageResource(R.drawable.ranking_logo_default_1);
				else if ("2".equals(my_ranking))
					entRankView.setImageResource(R.drawable.ranking_logo_default_2);
				else if ("3".equals(my_ranking))
					entRankView.setImageResource(R.drawable.ranking_logo_default_3);
				else
				{
					selfUIWrapper.viewRank.setBackground(getResources().getDrawable(R.drawable.ranking_top_three_icon));
					entRankView.setImageResource(R.drawable.ranking_logo_default_4);
					entRankNumView.setVisibility(View.VISIBLE);
					entRankNumView.setText(my_ranking);
				}
				selfUIWrapper.viewDistanceUnit.setVisibility(View.VISIBLE);
				selfUIWrapper.viewRank.setText(my_ranking);
				selfUIWrapper.viewDistance.setText(my_steps);
				selfUIWrapper.viewDing.setText(my_ding);
				selfUIWrapper.viewCai.setText(my_cai);
				selfUIWrapper.viewComments.setText(my_commend);

				// 如果是企业定制用户则还需要额外尝试加载定制名次图
				if(currUser != null && !(CommonUtils.isStringEmpty(currUser.getEid()) && currUser.getEid().equals("9999999999")))
				{
					if ("1".equals(my_ranking))
						fileName = currUser.getEranking_1_file_name();
					else if ("2".equals(my_ranking))
						fileName = currUser.getEranking_2_file_name();
					else if ("3".equals(my_ranking))
						fileName = currUser.getEranking_3_file_name();
					else
						fileName = currUser.getEranking_4_file_name();

					// 异步加载图片
					if(!CommonUtils.isStringEmpty(fileName))
					ToolKits.loadEntFileImage(RankingActivity.this, entRankView, currUser.getUser_id(), fileName, 332, 248);
				}
			}
		}
	}
	
	/**
	 * 数据周期过滤UI包装实现类.
	 */
	private static abstract class PeriodFilterUIWrapper
	{
		private Activity parentActivity = null;
		private RadioButton rbAll = null;
		private RadioButton rbEnt = null;
		private RadioGroup rgPeriod = null;
		private int lastCheckedId = -1;
		
		public PeriodFilterUIWrapper(Activity parentActivity)
		{
			this.parentActivity = parentActivity;
			
			this.initViews();
		}
		
		private void initViews()
		{
			rgPeriod = (RadioGroup)parentActivity.findViewById(R.id.ranking_activity_periodRg);
//			rbDay = (RadioButton)parentActivity.findViewById(R.id.report_page_activity_circleviews_dayRb);
			rbAll = (RadioButton)parentActivity.findViewById(R.id.ranking_activity_allRb);
			rbEnt = (RadioButton)parentActivity.findViewById(R.id.ranking_activity_entRb);
			
			rbEnt.setText(MyApplication.getInstance(parentActivity).getLocalUserInfoProvider().getEname());
			// 默认认选中的是全网
			lastCheckedId = rbAll.getId();//rbDay.getId();
			
			rgPeriod.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId)
				{
					if(lastCheckedId != checkedId)
					{
						onFilterChaged(getPeriodFilterType());
						lastCheckedId = checkedId;
					}
				}
			});
		}
		
		/**
		 * 返回周期过滤类型.
		 * 
		 * @return
		 */
		public int getPeriodFilterType()
		{
			int type = PeriodSwitchType.all;
			if(rbAll.isChecked())
				type = PeriodSwitchType.all;
			else
				type = PeriodSwitchType.ent;
			return type;
		}
		
		protected abstract void onFilterChaged(int periodSwitchType);
	}
	
	/**
	 * 自己信息
	 */
	private static class SelfUIWrapper
	{
		private Activity parentActivity = null;
		private ImageView viewAvatar = null;
		private TextView viewNickname = null;
		private TextView viewDistance = null;
		private TextView viewRank = null;
		private TextView viewDing;
		private TextView viewCai;
		private TextView viewComments;
		private ViewGroup entGroup = null;
		private TextView viewComeFrom = null;
		private TextView viewDistanceUnit = null; 
		private int periodSwitchType;
		private UserEntity u = null;
		
		private AsyncBitmapLoader asyncLoader = null; 
		
		public SelfUIWrapper(Activity parentActivity, UserEntity u, int periodSwitchType)
		{
			this.periodSwitchType = periodSwitchType;
			this.parentActivity = parentActivity;
			this.asyncLoader = new AsyncBitmapLoader(AvatarHelper.getUserAvatarSavedDir(parentActivity)+"/");  
			this.u = u;
			this.initViews();
		}
		
		private void initViews()
		{
			viewNickname = (TextView) parentActivity.findViewById(R.id.ranking_activity_listview_item_nickname);
			viewDistance = (TextView) parentActivity.findViewById(R.id.ranking_activity_listview_item_distance);
			viewAvatar = (ImageView) parentActivity.findViewById(R.id.ranking_activity_listview_item_imageView);
			viewRank = (TextView) parentActivity.findViewById(R.id.ranking_activity_listview_item_rank);
			viewDing = (TextView) parentActivity.findViewById(R.id.ranking_list_head_dingValue);
			viewCai = (TextView) parentActivity.findViewById(R.id.ranking_list_head_caiValue);
			viewComments = (TextView) parentActivity.findViewById(R.id.ranking_list_head_commendCount);
			entGroup = (ViewGroup) parentActivity.findViewById(R.id.ranking_list_item_come_from_LL);
			viewComeFrom = (TextView) parentActivity.findViewById(R.id.ranking_list_item_come_from);
			viewDistanceUnit = (TextView) parentActivity.findViewById(R.id.ranking_activity_listview_item_distance_unit);
			
			viewNickname.setText(u.getNickname());
			if(CommonUtils.isStringEmpty(u.getEname()) || periodSwitchType == PeriodSwitchType.ent)
			{
				entGroup.setVisibility(View.GONE);
			}
			else
			{
				entGroup.setVisibility(View.VISIBLE);
				viewComeFrom.setText(u.getEname());
				viewComeFrom.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
				entGroup.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						parentActivity.startActivity(IntentFactory.createCommonWebActivityIntent(parentActivity, u.getEportal_url()));
					}
				});
			}
			
			if(!CommonUtils.isStringEmpty(u.getUser_avatar_file_name(), true))
			{
				//根据图片URL去查找内存缓存有没有对应的Bitmap对象，并传递回调方法，如果没有，则等下载完毕回调  
				Bitmap bitmap = asyncLoader.loadBitmap(viewAvatar   
						// 注意：此下载地址在服务端的逻加上是不管客户有无缓存都会传过来头像文件数据的，但在此处却不需要顾及它，
						// 因为此处使用的缓存逻辑是独立的：它先判断本地用无该文件，如果没有的时候才会用此URL取头像数据，所以此
						// URL要一定能取的到头像数据就对了
						, AvatarHelper.getUserAvatarDownloadURL(parentActivity, u.getUser_id()) 
						, u.getUser_avatar_file_name() //, rowData.getUserAvatarFileName()
						, new ImageCallBack()  
						{  
							@Override  
							public void imageLoad(ImageView imageView, Bitmap bitmap)  
							{  
//							Log.w(GoodDetailActivity.class.getSimpleName(), "【MALL】See here!!!!!!!!!!!!!!!!!!!!!"+bitmap.getWidth());
								imageView.setImageBitmap(bitmap);  
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

		}
			
	}
	
	private void showShare(View v, String imgPath)
	{
		ShareSDK.initSDK(this);

		OnekeyShare oks = new OnekeyShare();
		
//			oks.setNotification(R.drawable.ic_appicon, this.getString(R.string.app_name));
		
		oks.setTitle(this.getString(R.string.evenote_title));
//			oks.setTitleUrl("http://linkloving.com");
		oks.setText(this.getString(R.string.share_content));
		
		
//			oks.setUrl("http://linkloving.com");

		oks.setFilePath(imgPath);

		oks.setImagePath(imgPath);
		
		oks.setComment(this.getString(R.string.share)); 
		
		oks.setSilent(false);
		
		// 令编辑页面显示为Dialog模式
		oks.setDialogMode();
		oks.disableSSOWhenAuthorize(); 

		// 去除注释，则快捷分享的操作结果将通过OneKeyShareCallback回调
		 oks.setCallback(new PlatformActionListener() {
			 @Override
			 public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
				 MyLog.e(TAG+"share",platform.getName().toString());
			 }

			 @Override
			 public void onError(Platform platform, int i, Throwable throwable) {
				 MyLog.e(TAG+"share",throwable.toString());
			 }

			 @Override
			 public void onCancel(Platform platform, int i) {
				 MyLog.e(TAG+"share","cancel");
			 }
		 });
//		oks.setShareContentCustomizeCallback(new ShareContentCustomize());


		oks.show(this);
	}
	
	
	private class ScreenHotAsyncTask extends AsyncTask<View, String, String>
	{

		View v = null;
		@Override
		protected String doInBackground(View... params)
		{
			v = params[0];
			String filePath = "/sdcard/ranking_v.png";

			// 截屏
			ToolKits.getScreenHot(v, filePath);

			if (loadingDialog != null)
				loadingDialog.dismiss();

			return filePath;
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			showShare(v.getRootView(), result);
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQ_COMMENTS)
		{
			if (mIndex != -1)
			{
				List<Ranking> list = rankingAdapter.getListData();
				String count = list.get(mIndex).getCommend_count();
				int from = data.getIntExtra("count", 0);
				if (count.isEmpty())
				{
					list.get(mIndex).setCommend_count(from + "");
				}
				else
				{
					int temp = Integer.parseInt(count);
					temp += from;
					list.get(mIndex).setCommend_count(temp + "");
				}
				rankingAdapter.notifyDataSetChanged();
			}
		}
		
	}
	
}
