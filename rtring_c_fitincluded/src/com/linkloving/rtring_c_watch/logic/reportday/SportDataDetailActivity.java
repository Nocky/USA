package com.linkloving.rtring_c_watch.logic.reportday;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import net.simonvt.datepicker.DatePickDialog;
import net.simonvt.datepicker.DatePickDialog.IgetDate;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.alibaba.fastjson.JSONObject;
import com.eva.android.platf.std.DataLoadableActivity;
import com.eva.android.widget.util.WidgetUtils;
import com.eva.android.widget.util.WidgetUtils.ToastType;
import com.eva.epc.common.util.CalendarHelper;
import com.eva.epc.common.util.CommonUtils;
import com.eva.epc.core.dto.DataFromServer;
import com.linkloving.band.ui.BRDetailData;
import com.linkloving.band.ui.DetailChartCountData;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.PreferencesToolkits;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.logic.reportday.util.TimeUtil;
import com.linkloving.rtring_c_watch.utils.DateSwitcher;
import com.linkloving.rtring_c_watch.utils.DateSwitcher.PeriodSwitchType;
import com.linkloving.rtring_c_watch.utils.HttpHelper;
import com.linkloving.rtring_c_watch.utils.IntentFactory;
import com.linkloving.rtring_c_watch.utils.ShareContentCustomize;
import com.linkloving.rtring_c_watch.utils.SkinSettingManager;
import com.linkloving.rtring_c_watch.utils.ToolKits;
/**
 * 详细运动数据界面
 * @author Administrator
 *
 */
public class SportDataDetailActivity extends DataLoadableActivity
{
	private final static String TAG = SportDataDetailActivity.class.getSimpleName();
	
	private long datetimeForInit = 0;  //从外面传入的时间（本地时间ms）
	
	private static final int REQ_PICK_DATE = 0;
	/**日期范围切换UI包装实现类*/
	private TimeFilterUIWrapper timeFilterUIWrapper = null;  
	/**运动的详细图表数据*/
	private TopDayDetailChartUIWrapper topDayDetailChartUIWrapper = null;
	
	private ContentUIWrapper contentUIWrapper = null;
	
	private ProgressDialog loadingDialog = null;
	
	private DateSwitcher dateSwitcher = null;
	private SkinSettingManager mSettingManager;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initDataFromIntent()
	{
		//解析从intent中传过来的数据  1430300379852
		datetimeForInit = IntentFactory.parseSportDataDetailActivity(getIntent());
	}
	
	
	@Override
	protected void initViews() 
	{
		customeTitleBarResId = R.id.reportday_titleBar;
		setContentView(R.layout.activity_sport_data_detail2);
		timeFilterUIWrapper =  new TimeFilterUIWrapper(this) {
			@Override
			protected void onFilterChaged()
			{
				Log.d(TAG, "===============================on day change==========================================");
				long ms = timeFilterUIWrapper.getDateSwitcher().getStartDate().getTime();
				int dayIndex = TimeUtil.getDayIndexFrom1970(ms);
				System.out.println("到onFilterChaged()方法中getTime()时间是："+dayIndex);
				topDayDetailChartUIWrapper.switch2DayIndex(dayIndex);
			}

			@Override
			protected void onViewTimeClick()
			{
				DatePickDialog datePickDialog = new DatePickDialog(SportDataDetailActivity.this,getDateSwitcher().getStartDate().getTime(),new IgetDate(){
                    @Override
            		public void getDate(int year, int month, int day,long mills) {
            			if(mills > System.currentTimeMillis())
            			{
            				WidgetUtils.showToast(SportDataDetailActivity.this, getString(R.string.date_picker_out_time), ToastType.INFO);
            			}
            			else 
            			{
            				timeFilterUIWrapper.getDateSwitcher().setBaseTime(new Date(mills));
            				timeFilterUIWrapper.switchedOver();
            			}
            		}
            	},getString(R.string.date_picker_activity_title), getString(R.string.general_ok), getString(R.string.general_cancel));
            	datePickDialog.show();	
			}
		};
		System.out.println("test：：："+timeFilterUIWrapper.getDateSwitcher().getStartDate());
//		if(datetimeForInit != -1)
//		{
//			timeFilterUIWrapper.getDateSwitcher().setBaseTime(new Date(datetimeForInit));
//		}
		/**
		 * 当进去设置具体运动信息界面时 再将timeFilterUIWrapper的时间改为传进来的时间
		 */
		topDayDetailChartUIWrapper = new TopDayDetailChartUIWrapper(this, datetimeForInit){
			@Override
			protected void notifyDateChangedShow(Date date)
			{
				Log.e(TAG, "_____________date:"+date);
				timeFilterUIWrapper.getDateSwitcher().setBaseTime(date);
				timeFilterUIWrapper.refreshShowText();
			}
		};
		contentUIWrapper = new ContentUIWrapper(this); 
		
		this.getCustomeTitleBar().getRightGeneralButton().setBackgroundResource(R.drawable.selector_btn_share);
		this.getCustomeTitleBar().getRightGeneralButton().setVisibility(View.VISIBLE);
		
		this.setLoadDataOnCreate(false);
		
//		// 默认载入数据
//		timeFilterUIWrapper.switchedOver();
		
		setTitle($$(R.string.detail_sport_data_title));
		
//		dateSwitcher = timeFilterUIWrapper.getDateSwitcher();
	}
	
	@Override
	protected void initListeners()
	{
		this.getCustomeTitleBar().getRightGeneralButton().setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				loadingDialog = new ProgressDialog(SportDataDetailActivity.this);
				loadingDialog.setProgressStyle(0);
				loadingDialog.setMessage($$(R.string.detail_sport_data_image_loading));
				loadingDialog.show();
				
				new ScreenHotAsyncTask().execute(v);
			}
		});
	}
	//计算完的详细数据
	public void updateCascadedDatas(int curDayIndex)
	{
		DetailChartCountData count = PreferencesToolkits.getDetailChartCountDate(this, curDayIndex);
		if(count != null)
		    contentUIWrapper.showCascadedDatas(HttpHelper.parseDaySynopicSumForPreview(this, count));
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		mSettingManager=new SkinSettingManager(this);
		mSettingManager.initSkins();
		topDayDetailChartUIWrapper.onParentResume();
	}
	
	@Override
	public void onDestroy()
	{
		//如果有未执行完成的AsyncTask则强制退出之，否则线程执行时会空指针异常哦！！！
		topDayDetailChartUIWrapper.onParentDestroy();
		super.onDestroy();
	}
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);
		topDayDetailChartUIWrapper.onParentWindowFocusChanged(hasFocus);
	}
	
//	/**
//	 */
//	private void switchData()
//	{
//		String sss = $$(R.string.detail_sport_data_refresh_range) + timeFilterUIWrapper.getDateSwitcher().getSQLBetweenAnd();
//		Log.d(TAG, sss);
//		
//		String startDateStr = timeFilterUIWrapper.getDateSwitcher().getStartDateStr();
//		String endDateStr = timeFilterUIWrapper.getDateSwitcher().getEndDateStr();
//		
////		topDayDetailChartUIWrapper.showActiveForWorkAsync(startDateStr, endDateStr, new Observer(){
////			@Override
////			public void update(Observable observable, Object data)
////			{
////				// 图表上的明细数据处理完成后刷新下方合计数据的显示
////				contentUIWrapper.showCascadedDatas((String)data);
////////				// 图表上的明细数据处理完成后刷新下方的睡眠数据显示
//////				contentUIWrapper.showDaySleepData(timeFilterUIWrapper.getDateSwitcher().getStartDate()
//////						, timeFilterUIWrapper.getDateSwitcher().getEndDate());
////			}
////		});
//	}

	@Override
	protected DataFromServer queryData(String... arg0)
	{
		return null;
	}

	@Override
	protected void refreshToView(Object arg0) 
	{

	}

	/**
	 * 日期范围切换UI包装实现类.
	 * 
	 * @author Jack Jiang, 2014-06-15
	 * @version 1.0
	 */
	public static abstract class TimeFilterUIWrapper
	{
		private Activity parentActivity = null;
		
		private Button btnLeft = null;
		private Button btnRight = null;
		private TextView viewTime = null;
		private DateSwitcher daySwitcher = null;

		public TimeFilterUIWrapper(Activity parentActivity)
		{
			this.parentActivity = parentActivity;
			this.initViews();
			this.initListeners();
		}

		private void initViews()
		{
			btnLeft  = (Button)parentActivity.findViewById(R.id.activity_sport_data_detail_dataswitch_leftBtn);
			btnRight = (Button)parentActivity.findViewById(R.id.activity_sport_data_detail_dataswitch_rightBtn);
			viewTime = (TextView)parentActivity.findViewById(R.id.activity_sport_data_detail_dataswitch_dateView);
			daySwitcher = new DateSwitcher(PeriodSwitchType.day){
				@Override
				protected void init()
				{
					switch(this.type)
					{
					case PeriodSwitchType.day:
						// 日类型时，默认时间为当天
						base = new GregorianCalendar();
//						base.add(GregorianCalendar.DAY_OF_MONTH, -1);
						break;
					default:
						Log.e(TAG, "当前日期切换组件只支持到了\"年/月/日\"的切换！");
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
						WidgetUtils.showToast(parentActivity,ToolKits .getStringbyId(parentActivity, R.string.detail_sport_data_waiting_tomorrow) , ToastType.INFO);
						return false;
					}
					return true;
				}
			};
		}

		private void initListeners()
		{
			//点击到上一天的按钮
			btnLeft.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v)
				{
					if(getDateSwitcher().previous())
						switchedOver();
				}
			});

			btnRight.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v)
				{
					if(getDateSwitcher().next())
						switchedOver();
				}
			});
			
			viewTime.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					onViewTimeClick();
				}
			});
			
		}

		public void refreshShowText()
		{
			String dateStr = new SimpleDateFormat("yyyy/MM/dd").format(getDateSwitcher().getStartDate());
			if(CalendarHelper.isToday(getDateSwitcher().getStartDate().getTime()))
				viewTime.setText(ToolKits.getStringbyId(parentActivity, R.string.detail_sport_data_today)+" "+dateStr);
			else
				viewTime.setText(dateStr);
		}

		public void switchedOver()
		{
			onFilterChaged();
		}

		public DateSwitcher getDateSwitcher() 
		{
			return daySwitcher;
		}
		
		protected abstract  void onViewTimeClick();

		protected abstract void onFilterChaged();
	}
	
	private static class ContentUIWrapper
	{
		private Activity parentActivity = null;
	   //** 运动相关ui组件
		/** 运动了 */
		private TextCompositionView viewCompositeYdl = null;
		/** 完成目标 */
		//private TextCompositionView viewCompositeWcmb = null;
		/** 活动耗时 */
		private TextCompositionView viewCompositeYdhs = null;
		/** 平均速度 */
		private TextCompositionView viewCompositePjsd = null;
		/** 运动距离 */
		private TextCompositionView viewCompositeYdjl = null;
		/** 能量消耗 */
		private TextCompositionView viewCompositeNlxh = null;
		/** N圈跑道 */
	//	private TextView viewPdNum = null;
		/** N瓶可乐 */
		//private TextView viewKlNum = null;	
		
		//** 跑步相关ui组件
		/** 跑了多少步 */
		private TextCompositionView viewPl = null;
		/** 跑步耗时 */
		private TextCompositionView viewPbhs = null;	
		/** 跑步平均速度 */
		private TextCompositionView viewCompositePbpjsd = null;
		/** 跑步运动距离 */
		private TextCompositionView viewCompositePbydjl = null;
		/** 跑步能量消耗 */
		private TextCompositionView viewCompositePbnlxh = null;
		
		//** 睡眠相关ui组件
		/** 睡眠总量 */
		private TextCompositionView viewSleepSum = null;	
		/** 深睡时间 */
		private TextCompositionView viewCompositeDeepsleep = null;
		/** 浅睡时间 */
		private TextCompositionView viewCompositeSleep = null;
		
		/**入睡时间*/
		private TextCompositionView viewCompositeRs = null;
		/** 起床时间*/
		private TextCompositionView viewCompositeQc = null;
		
		public ContentUIWrapper(Activity parentActivity)
		{
			this.parentActivity = parentActivity;
			
			this.initViews();
		}
		
		private void initViews()
		{
			int commonGrayColor = Color.rgb(0x66, 0x66, 0x66);
			
			int commonDarkOrangeColor = Color.rgb(255, 145, 66);
			int commonOrangeColor = Color.rgb(255, 182, 54);
			int commonGreenColor = Color.rgb(3, 220, 177);
			int commonLightGreenColor = Color.rgb(175, 222, 2);
			
			viewCompositeYdl = (TextCompositionView)parentActivity
					.findViewById(R.id.activity_sport_data_detail_ydlCompositeView);
			viewCompositeYdl.setText(ToolKits.getStringbyId(parentActivity, R.string.unit_step)
					, "0", "",commonDarkOrangeColor, Color.rgb(0xFF, 0x91, 0x41));
//			viewCompositeWcmb = (TextCompositionView)parentActivity
//					.findViewById(R.id.activity_sport_data_detail_wcmbCompositeView);
//			viewCompositeWcmb.setText(ToolKits.getStringbyId(parentActivity, R.string.detail_sport_data_wcmb), "0", "%", commonGrayColor, commonGrayColor);
			viewCompositeYdhs = (TextCompositionView)parentActivity
					.findViewById(R.id.activity_sport_data_detail_ydsjCompositeView);
			viewCompositeYdhs.setText(ToolKits.getStringbyId(parentActivity, R.string.unit_minute)
					, "0", "", commonDarkOrangeColor, commonGrayColor);
			viewCompositePjsd = (TextCompositionView)parentActivity
					.findViewById(R.id.activity_sport_data_detail_ydsdCompositeView);
				viewCompositePjsd.setText(ToolKits.getStringbyId(parentActivity, R.string.unit_km_per_hour_metric)
						, "0", "", commonDarkOrangeColor, commonGrayColor);
			
			
			
			viewCompositeYdjl = (TextCompositionView)parentActivity
					.findViewById(R.id.activity_sport_data_detail_ydjlCompositeView);
			
			if(MyApplication.getInstance(parentActivity).getUNIT_TYPE().equals("Imperial")){
				
				viewCompositeYdjl.setText(ToolKits.getStringbyId(parentActivity, R.string.unit_miles), "0",  "", commonDarkOrangeColor, commonGrayColor);
				
			}else{
				
				viewCompositeYdjl.setText(ToolKits.getStringbyId(parentActivity, R.string.unit_m_s), "0",  "", commonDarkOrangeColor, commonGrayColor);
			}
			
			
			viewCompositeNlxh = (TextCompositionView)parentActivity
					.findViewById(R.id.activity_sport_data_detail_ydxhCompositeView);
			viewCompositeNlxh.setText(ToolKits.getStringbyId(parentActivity, R.string.unit_cal_big)
					, "0", "", commonDarkOrangeColor, commonGrayColor);
//			viewPdNum = (TextView)parentActivity
//					.findViewById(R.id.activity_sport_data_detail_pdNumView);
//			viewKlNum = (TextView)parentActivity
//					.findViewById(R.id.activity_sport_data_detail_klNumView);
			
			viewPl = (TextCompositionView)parentActivity
					.findViewById(R.id.activity_sport_data_detail_pblCompositeView);
			viewPl.setText(ToolKits.getStringbyId(parentActivity, R.string.detail_sport_data_ydl)
					, "0", "", commonOrangeColor, commonGrayColor);
			viewPbhs = (TextCompositionView)parentActivity
					.findViewById(R.id.activity_sport_data_detail_pbsjCompositeView);
			viewPbhs.setText(ToolKits.getStringbyId(parentActivity, R.string.detail_sport_data_hdsj)
					, "0", "", commonOrangeColor, commonGrayColor);
			viewCompositePbpjsd = (TextCompositionView)parentActivity
					.findViewById(R.id.activity_sport_data_detail_pbsdCompositeView);
			
			viewCompositePbydjl = (TextCompositionView)parentActivity
					.findViewById(R.id.activity_sport_data_detail_pbjlCompositeView);
			viewCompositePbydjl.setText(ToolKits.getStringbyId(parentActivity, R.string.detail_sport_data_pbjl)
					, "0", "", commonOrangeColor, commonGrayColor);
			viewCompositePbnlxh = (TextCompositionView)parentActivity
					.findViewById(R.id.activity_sport_data_detail_pbxhCompositeView);
			viewCompositePbnlxh.setText(ToolKits.getStringbyId(parentActivity, R.string.detail_sport_data_nlxh)
					, "0", "", commonOrangeColor, commonGrayColor);
			
			viewSleepSum = (TextCompositionView)parentActivity
					.findViewById(R.id.activity_sport_data_detail_smCompositeView);
			viewSleepSum.setText(ToolKits.getStringbyId(parentActivity, R.string.detail_sport_data_sm)
					, "0",ToolKits.getStringbyId(parentActivity, R.string.unit_hour), commonGreenColor, commonGrayColor);
			viewCompositeDeepsleep = (TextCompositionView)parentActivity.findViewById(R.id.activity_sport_data_detail_ssCompositeView);
			viewCompositeDeepsleep.setText(ToolKits.getStringbyId(parentActivity, R.string.detail_sport_data_sssj), "0",ToolKits.getStringbyId(parentActivity, R.string.unit_hour), commonLightGreenColor, commonGrayColor);
			
			
			viewCompositeSleep = (TextCompositionView)parentActivity.findViewById(R.id.activity_sport_data_detail_qsCompositeView);
			viewCompositeSleep.setText(ToolKits.getStringbyId(parentActivity, R.string.detail_sport_data_qssj), "0",ToolKits.getStringbyId(parentActivity, R.string.unit_hour), commonLightGreenColor, commonGrayColor);
			
			viewCompositeRs = (TextCompositionView) parentActivity.findViewById(R.id.activity_sport_data_detail_rsCompositeView);
			viewCompositeRs.getViewUnit().setVisibility(View.GONE);
			viewCompositeRs.setText(ToolKits.getStringbyId(parentActivity, R.string.detail_sport_data_rs)
					, "","", commonLightGreenColor, commonGrayColor);
			viewCompositeQc = (TextCompositionView) parentActivity.findViewById(R.id.activity_sport_data_detail_qcCompositeView);
			viewCompositeQc.getViewUnit().setVisibility(View.GONE);
			viewCompositeQc.setText(ToolKits.getStringbyId(parentActivity, R.string.detail_sport_data_qc), "","", commonLightGreenColor, commonGrayColor);
		}
		
		public void showCascadedDatas(String resultJSONStr)
		{
			// 运动总步数
			String ydzbs = "0";
			String ydwcmb = "0";
			// 运动时间（秒）
			String ydsj = "0";
			// 运动平均运动速度
			String speed = "0";
			// 消耗卡路里
			String ca = "0";
			// 运动总距离（米）
			String ydzjl = "0";
			
			// 走路总步数
			String zlzbs = "0";
			// 走路时间（秒）
			String zlsj = "0";
			// 走路平均运动速度
			String zlspeed = "0";
			// 走路消耗卡路里
			String zlca = "0";
			// 走路总距离（米）
			String zlzjl = "0";
			
			// 跑步总步数
			String pbzbs = "0";
			// 跑步总时间
			String pbzsj = "0";
			// 跑步平均速度
			String pbpjsd = "0";
			// 跑步总距离
			String pbzjl = "0";
			// 跑步卡路里
			String pbca = "0";
			
			// 深睡时间（小时）
			String deepsleep = "0";
			// 浅睡时间（小时）
			String sleep = "0";
			
			int getuptime = 0;
			int gotobedtime = 0;

			if(resultJSONStr != null)
			{
				JSONObject nwObj = JSONObject.parseObject((String)resultJSONStr);
				
				ydzbs = nwObj.getString("ydzbs");// 注意与服务端的JSON key要一一对应！
				ydsj = nwObj.getString("ydzsj");// 注意与服务端的JSON key要一一对应！
				speed = nwObj.getString("speed");// 注意与服务端的JSON key要一一对应！
				ca = nwObj.getString("ca");// 注意与服务端的JSON key要一一对应！
				ydzjl = nwObj.getString("ydzjl");// 注意与服务端的JSON key要一一对应！
				
				zlzbs = nwObj.getString("zlzbs");// 注意与服务端的JSON key要一一对应！
				zlsj = nwObj.getString("zlzsj");// 注意与服务端的JSON key要一一对应！
				zlspeed = nwObj.getString("zlspeed");// 注意与服务端的JSON key要一一对应！
				zlca = nwObj.getString("zlca");// 注意与服务端的JSON key要一一对应！
				zlzjl = nwObj.getString("zlzjl");// 注意与服务端的JSON key要一一对应！

				pbzbs = nwObj.getString("pbzbs");// 注意与服务端的JSON key要一一对应！
				pbzsj = nwObj.getString("pbzsj");// 注意与服务端的JSON key要一一对应！
				pbpjsd = nwObj.getString("pbspeed");// 注意与服务端的JSON key要一一对应！
				pbca = nwObj.getString("pbca");// 注意与服务端的JSON key要一一对应！
				pbzjl = nwObj.getString("pbzjl");// 注意与服务端的JSON key要一一对应！
				
				deepsleep = nwObj.getString("ssm");// 注意与服务端的JSON key要一一对应！
				sleep = nwObj.getString("qsm");// 注意与服务端的JSON key要一一对应！
				
				
				getuptime = nwObj.getIntValue("getuptime");
				gotobedtime = nwObj.getIntValue("gotobedtime");
			}

			showActiveForSleep(
					zlzbs, zlsj, zlspeed, zlca, zlzjl
					, pbzbs, pbzsj, pbpjsd, pbca, pbzjl
					, deepsleep, sleep, getuptime, gotobedtime);
		}
		
		/**
		 * 显示“睡眠”相关的活动数据.
		 */
		private void showActiveForSleep(
				String zlzbs, String zlsj, String zlspeed, String zlca, String zlzjl, String pbzbs, String pbzsj, String pbpjsd, String pbca, String pbzjl, String deepsleep, String sleep, int getuptime, int gotobedtime)
		{
			viewCompositeYdl.setText(zlzbs);
			viewCompositeYdhs.setText(CommonUtils.getScaledValue(CommonUtils.getIntValue(zlsj)/60.0, 1)+"");
			if(MyApplication.getInstance(parentActivity).getUNIT_TYPE().equals("Imperial")){
				Log.e(TAG, "平均速度:"+CommonUtils.getDoubleValue(zlspeed));
				viewCompositePjsd.setText(CommonUtils.getScaledDoubleValue(CommonUtils.getDoubleValue(zlspeed) * 0.0006214 * 3600.0 , 1) + "");
				viewCompositePbpjsd.setText(CommonUtils.getScaledDoubleValue(CommonUtils.getDoubleValue(pbpjsd)* 0.0006214 * 3600.0 , 1) + "");
				viewCompositeYdjl.setText(ToolKits.MChangetoMIRate(CommonUtils.getIntValue(zlzjl)) + "");
				viewCompositeYdjl.setUnit(parentActivity.getString(R.string.unit_miles));
			}else{
				viewCompositePjsd.setText(CommonUtils.getScaledDoubleValue(CommonUtils.getDoubleValue(zlspeed) * 3600.0 / 1000.0, 1) + "");// Km/小时
				viewCompositePbpjsd.setText(CommonUtils.getScaledDoubleValue(CommonUtils.getDoubleValue(pbpjsd)* 3600.0 / 1000.0, 1)+"");  // Km/小时
				viewCompositeYdjl.setText( CommonUtils.getIntValue(zlzjl) + "");
				viewCompositeYdjl.setUnit(parentActivity.getString(R.string.unit_m_s));
			}
			
			viewCompositeNlxh.setText(zlca);
		//	viewPdNum.setText(CommonUtils.getScaledValue(CommonUtils.getIntValue(ydzjl)/400.0, 1)+"");// 标准跑道长度400m
		//	viewKlNum.setText(CommonUtils.getScaledValue(CommonUtils.getIntValue(ca)/100.0, 1)+"");// 一瓶250ml可乐热量：100大卡，100毫升=180焦=43 ->4.5*43=193大卡
			
			viewPl.setText(pbzbs);
			viewPbhs.setText(CommonUtils.getScaledValue(CommonUtils.getIntValue(pbzsj)/60.0, 1)+"");
			
			
			viewCompositePbydjl.setText(ToolKits.MChangetoMIRate(Integer.parseInt(pbzjl))+"");
			viewCompositePbnlxh.setText(pbca);
			
			// 此睡眠数据是从明细中计算出来的，是当天的0~24小时时间数据，所以不是准确睡眠数据（准确睡眠数据是前天晚9点到今天早9点）
            refreshSleepShow(sleep, deepsleep, getuptime, gotobedtime);
	//		refreshSleepShow("0", "0");
		}
		
		public void refreshSleepShow(String seepInHour, String deepSleepInHour, int getuptime, int gotobedtime)
		{
			double sum = CommonUtils.getDoubleValue(seepInHour) + CommonUtils.getDoubleValue(deepSleepInHour);
			viewSleepSum.setText(CommonUtils.getScaledDoubleValue(sum,1)+"");
			viewCompositeDeepsleep.setText(deepSleepInHour);
			viewCompositeSleep.setText(seepInHour);
			
			if(getuptime > 0)
			{
				String getuptimestr = ToolKits.convertTimeWithPartten(getuptime * 1000l, ToolKits.DATE_FORMAT_HH_MM);
				Log.e(TAG, "起床时间:"+getuptimestr);
				viewCompositeQc.setText(TimeUtil.format12TimeFromString(getuptimestr));
			}
			else{
				viewCompositeQc.setText("--:--");
			}
			
			if(gotobedtime > 0)
			{
				String gotobedtimestr = ToolKits.convertTimeWithPartten(gotobedtime * 1000l, ToolKits.DATE_FORMAT_HH_MM);
				Log.e(TAG, "入睡时间:"+gotobedtimestr);
				viewCompositeRs.setText(TimeUtil.format12TimeFromString(gotobedtimestr));
			}
			else{
				viewCompositeRs.setText("--:--");
			}
		}
	}

	/**
	 * 运动详细数据图表UI包装实现类.
	 */
	private static class TopDayDetailChartUIWrapper implements IDetailTimeChangeCallback,IDetailDataCountCallback
	{
		private Activity parentActivity = null;
		
		/** 日期格式 */
		protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		//private DetailChartView detailChartView;
		private DetailChartControl detailChartControl;
		boolean needSymbolLine;
		
		List<BRDetailData> listDetailData;
//		int listSelectIndex;
		int curListSelIndex;
		
		long datetimeForInit;
		
		boolean isSleepTime;
		
		int curDayIndex = 0;
		
		public TopDayDetailChartUIWrapper(Activity parentActivity,long datetimeForInit)
		{
			this.parentActivity = parentActivity;
			this.datetimeForInit =datetimeForInit;
			needSymbolLine = true;
			initViews();
		}
		
		private void initViews() 
		{
//			setContentView(R.layout.activity_sport_data_detail);
			detailChartControl = (DetailChartControl) parentActivity.findViewById(R.id.activity_sport_data_detail_detailChartView1);
			detailChartControl.SetDetailTimeChangeCallback(this);
			detailChartControl.setDetailDataCountChangeCallback(this);
			System.out.println("本地时间至本地时间1970.1.1的ms"+datetimeForInit);
			detailChartControl.initDayIndex(TimeUtil.getDayIndexFrom1970(datetimeForInit), TimeUtil.getDayIndexFrom1970(datetimeForInit)-365, false);
		} 
		
		public void switch2DayIndex(int dayIndex)
		{
			System.out.println("detailChartControl= " + dayIndex);
			detailChartControl.slide2DayIndex(dayIndex);
		}
		
		public void onParentResume()
		{
			detailChartControl.initSymbolLine();
		}
		
		public void onParentDestroy()
		{
			detailChartControl.finishAllAsyncTask();
		}
		
		public void onParentWindowFocusChanged(boolean hasFocus)
		{
			if (hasFocus && needSymbolLine) 
			{
				needSymbolLine = false;
				detailChartControl.initSymbolLine();
			}
		}
		
//		public void showActiveForWorkAsync(final String startDate, final String endDate, final Observer obsForSumDataShow)
//		{
//			new DataLoadingAsyncTask<Object, Integer, DataFromServer>(parentActivity, false){
//				private boolean online = false;
//				
//				@Override
//				protected DataFromServer doInBackground(Object... params)
//				{
//					online = ToolKits.isNetworkConnected(context);
//					return HttpHelper.submitQuerySportRecordsToServer_l(parentActivity, startDate, endDate, online, true);
//				}
//				
//				@Override
//				protected void onPostExecuteImpl(Object arg0)
//				{
//					if(arg0 != null)
//					{
//						try 
//						{
//							// 原始运动数据
//							List<SportRecord> originalSportDatas = HttpHelper.parseQuerySportRecordsFromServer(parentActivity, (String)arg0, online);
//							// 计算睡眠
//							final List<DLPSportData> srs = SleepDataHelper.querySleepDatas2(originalSportDatas);
//							// 将睡眠算法计算完成的睡眠状态回填（那么这样的话，在组织成日汇总数据时也就能合计出睡眠时间了）
//							ToolKits.putSleepStateFromSleepResult(originalSportDatas, srs);
//							
//							// 准备界面下方的汇总数据显示
//							if(obsForSumDataShow != null)
//							{
//								// 先将运动明细数据组装成日汇总数据
//								List<DaySynopic> daySynopicFronSportDatas = ToolKits.convertSportDatasToSynopics(originalSportDatas);
//								// 将此汇总数据加工后传给汇总数据显示UI
//								obsForSumDataShow.update(null, HttpHelper.parseDaySynopicSumForPreview(parentActivity, daySynopicFronSportDatas));
//							}
//						} 
//						catch (Exception e)
//						{
//		
//						}
//
//					}
//				}
//			}.execute();
//		}
		
//		DataLoadingAsyncTask dat;
		
		@Override
		public void OnDetailTimeChange(int dayIndex, final int begin) 
		{
			Log.i(TAG, "curDayIndex = " + curDayIndex + ", dayIndex = " + dayIndex);
			if(curDayIndex != dayIndex)
			{
				//WidgetUtils.showToast(parentActivity, TimeUtil.formatDateByYYYYMMDD(TimeUtil.parseDateFromDayIndex(dayIndex)),ToastType.INFO);
//				timeFilterUIWrapper.getDateSwitcher().setBaseTime(TimeUtil.parseDateFromDayIndex(dayIndex));
//				timeFilterUIWrapper.();
				notifyDateChangedShow(TimeUtil.parseDateFromDayIndex(dayIndex));
			}
			curDayIndex = dayIndex;
			((SportDataDetailActivity)parentActivity).updateCascadedDatas(curDayIndex);
		}

		@Override
		public void OnDetailDataCountChange()
		{
			((SportDataDetailActivity)parentActivity).updateCascadedDatas(curDayIndex);
		}
		
		protected void notifyDateChangedShow(Date date)
		{
			// default do nothing
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
		// oks.setCallback(new OneKeyShareCallback());
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
			String filePath = "/sdcard/sport_data_v.png";

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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		super.onActivityResult(requestCode, resultCode, data);
		
//		if(requestCode == REQ_PICK_DATE && resultCode == Activity.RESULT_OK)
//		{
//			Date date =  IntentFactory.parseDatePickerActivityIntent(data);
//			System.out.println("__-----");
//			if(date.getTime() > System.currentTimeMillis())
//			{
//				WidgetUtils.showToast(SportDataDetailActivity.this, getString(R.string.date_picker_out_time), ToastType.INFO);
//			}
//			else 
//			{
//				  timeFilterUIWrapper.getDateSwitcher().setBaseTime(date);
//			      timeFilterUIWrapper.switchedOver();
//			}
//	      
//		}
	}
}
