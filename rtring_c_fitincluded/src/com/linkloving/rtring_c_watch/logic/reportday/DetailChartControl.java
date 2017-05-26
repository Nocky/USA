package com.linkloving.rtring_c_watch.logic.reportday;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.eva.android.RHolder;
import com.eva.android.x.AsyncTaskManger;
import com.linkloving.band.ui.BRDetailData;
import com.linkloving.band.ui.DetailChartCountData;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.PreferencesToolkits;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.logic.reportday.util.Constant;
import com.linkloving.rtring_c_watch.logic.reportday.util.TimeUtil;
import com.linkloving.rtring_c_watch.utils.ToolKits;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 滑动图表
 * 
 * @author Administrator
 */
public class DetailChartControl extends RelativeLayout
{

	protected static final String TAG = "DetailChartControl";

	/** 图表滑动到时日期内的偏移时间片数 */
	private int ticksFromDayBegin;

	/** 图表日期 */
	private int chartDayIndex;

	/** 图表保留总页数 */
	private final static int PAGE_COUNT = 3;

	private AsyncTaskManger asyncTaskManger = null;

	public enum AddType
	{
		RIGHT, LEFT
	}

	/** 日期格式 */
	protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	LinearLayout lLayout;
	HorizontalScrollView hScrollView;
	ImageView symbolImageView;
	TextView timeTextview;
	TextView stateTextview;
	TextView infoTextview;

	Context context;

	// /int userId;
	int dayIndex;
	int curState = -1;
	// int firstDataBeginDay;
	int userCreatedDay;

	/** 图片生成器 */
	DetailBitmapCreator detailBitmapCreator;

	/** 图表滑动回调 */
	IDetailTimeChangeCallback detailTimeChangeCallback;
	/** 图表统计数据返回回调 */
	IDetailDataCountCallback detailDataCountCallback;

	int lastScrollX;
	/** 图表指针所在位置 */
	int symbolLine;
	/** 图表加载新页面临界区间 */
	int scrollRange;
	int imageWidth;
	int imageHeight;

	/** 每个时间片对应像素宽度（px/片) */
	float xScale;
	float yScale;

	boolean sleepBegin;
	boolean syncing;
	boolean isLeftEnd;
	boolean isRightEnd;

	Random rand;

	// !!!!!!!!!!!!
	private List<BRDetailData> detailDatas = new ArrayList<BRDetailData>();

	public DetailChartControl(Context context)
	{
		super(context);

		InitDetailChartControl(context);
	}

	@SuppressLint("NewApi")
	public DetailChartControl(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		InitDetailChartControl(context);
	}

	public DetailChartControl(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		InitDetailChartControl(context);
	}

	public void finishAllAsyncTask()
	{
		asyncTaskManger.finishAllAsyncTask();
	}

	public List<BRDetailData> getDetailDatas()
	{
		return detailDatas;
	}

	public void setDetailDatas(List<BRDetailData> detailDatas)
	{
		this.detailDatas = detailDatas;
	}

	public void SetDetailTimeChangeCallback(IDetailTimeChangeCallback detailTimeChangeCallback)
	{
		this.detailTimeChangeCallback = detailTimeChangeCallback;
	}

	public void setDetailDataCountChangeCallback(IDetailDataCountCallback detailDataCountCallback)
	{
		this.detailDataCountCallback = detailDataCountCallback;
	}

	// public void initMediator(CloudSyncMediator cloudSyncMediator,
	// BraceletDetailMediator braceletDetailMediator, SportMediator
	// sportMediator) {
	// this.cloudSyncMediator = cloudSyncMediator;
	// this.braceletDetailMediator = braceletDetailMediator;
	// this.sportMediator = sportMediator;
	// }

	public void initDayIndex(int dayIndex, int userCreatedDay, boolean sleepBegin)
	{
		lastScrollX = -1;
		// 图表时间
		this.dayIndex = dayIndex;
		// 用户创建时间
		this.userCreatedDay = userCreatedDay;
		// 是否是是睡眠
		this.sleepBegin = sleepBegin;
		// 最早的日期
		// firstDataBeginDay =
		// TimeUtil.getDayIndexFrom1970(sportMediator.getEarliestOriginalDataDate(userId).getTime());
		for (int i = 0; i < PAGE_COUNT; i++)
		{
			AsyncAddDetailChart(AddType.LEFT);
		}
	}

	public void initSymbolLine()
	{
		int[] location = new int[2];
		symbolImageView.getLocationInWindow(location);
		symbolLine = location[0] + symbolImageView.getWidth() / 2;

		updateShowTime();
	}

	public void slide2DayIndex(int dayIndex)
	{
		// 图表时间
		this.dayIndex = dayIndex;
		for (int i = 0; i < PAGE_COUNT; i++)
		{
			AsyncAddDetailChart(AddType.LEFT);
		}
		initSymbolLine();
	}

	public void SetChartTime(Context context, BRDetailData data)
	{
		int totalMinute = (data.getDayIndex() - dayIndex - 1) * Constant.TICKSPERDAY + data.getBegin();
		Log.e(TAG, "图表totalMinute:"+totalMinute);
		int scrollX = (int) (totalMinute * xScale) - symbolLine;
		hScrollView.smoothScrollTo(scrollX, 0);
		timeTextview.setText(TimeUtil.formatTimeFromMinuteCount(data.getBegin() / 2));
		SetStateAndInfo(context, data);
	}

	/**
	 * 设置图表指针的状态和显示信息
	 * 
	 * @param context
	 * @param data
	 */
	public void SetStateAndInfo(Context context, BRDetailData data)
	{
		if (data != null)
		{
			Log.d(TAG, "SetStateAndInfo======================================================== " + data.getBegin());
			stateTextview.setText(GetStateString(context, data.getState()));
			StringBuilder infoStr = new StringBuilder();
			if (data.getState() == BRDetailData.STATE_WALKING || data.getState() == BRDetailData.STATE_RUNNING)
			{
				infoStr.append(String.valueOf(data.getSteps()));
				infoStr.append(context.getString(R.string.unit_step));
				infoStr.append(" - ");
				if(MyApplication.getInstance(context).getUNIT_TYPE().equals("Imperial")){
					
					infoStr.append(ToolKits.MChangetoMIRate(Integer.valueOf(data.getDistance())));
					infoStr.append(context.getString(R.string.unit_miles));
					
				}else{
					
					infoStr.append(Integer.valueOf(data.getDistance()));
					infoStr.append(context.getString(R.string.unit_m));
				}
				
				

			}
			else if (data.getState() == BRDetailData.STATE_SLEEP_LIGHT || data.getState() == BRDetailData.STATE_SLEEP_DEEP)
			{
				infoStr.append(GetDurationString(context, data.getDuration()));
			}
			Log.d(TAG, "infoStr:" + infoStr);
			infoTextview.setText(infoStr);

			if (curState != data.getState())
			{
				curState = data.getState();
				if (data.getState() == BRDetailData.STATE_IDLE)
				{
					symbolImageView.setImageResource(R.drawable.detail_site_symbol);
				}
				else if (data.getState() == BRDetailData.STATE_WALKING)
				{
					symbolImageView.setImageResource(R.drawable.detail_walk_symbol);
				}
				else if (data.getState() == BRDetailData.STATE_RUNNING)
				{
					symbolImageView.setImageResource(R.drawable.detail_run_symbol);
				}
				else
				{
					symbolImageView.setImageResource(R.drawable.detail_sleep_symbol);
				}
			}

		}
		else
		{
			if (this.sleepBegin)
			{
				if (curState != BRDetailData.STATE_SLEEP_ACTIVE)
				{
					curState = BRDetailData.STATE_SLEEP_ACTIVE;
					symbolImageView.setImageResource(R.drawable.detail_sleep_symbol);
				}
			}
			else
			{
				if (curState != BRDetailData.STATE_IDLE)
				{
					curState = BRDetailData.STATE_IDLE;
					symbolImageView.setImageResource(R.drawable.detail_site_symbol);
				}
			}

			stateTextview.setText("");
			infoTextview.setText("");
		}
	}

	/**
	 * 初始化图表
	 * 
	 * @param context
	 */
	private void InitDetailChartControl(Context context)
	{
		asyncTaskManger = new AsyncTaskManger();

		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.detail_chart_view, this);
		InitView();
		initValues();
	}

	private void InitView()
	{
		this.lLayout = (LinearLayout) findViewById(R.id.lLayout);
		this.hScrollView = (HorizontalScrollView) findViewById(R.id.hScrollView);
		this.symbolImageView = (ImageView) findViewById(R.id.symbolImageView);
		this.stateTextview = (TextView) findViewById(R.id.stateTextview);
		this.infoTextview = (TextView) findViewById(R.id.infoTextview);
		this.timeTextview = (TextView) findViewById(R.id.timeTextview);

		hScrollView.setOnTouchListener(new TouchListenerImpl());
	}

	private void initValues()
	{
		initXScale();
		detailBitmapCreator = new DetailBitmapCreator(context);
		detailBitmapCreator.initChartParameter(new ChartParameter(xScale, yScale, imageWidth, imageHeight, ToolKits.dip2px(context, 24)));
		// userId = MainApplication.Instance().userId;
		rand = new Random();
		syncing = false;
	}

	/**
	 * 更新图表的时间显示
	 */
	private void updateShowTime()
	{
		// 当前时间时间片
		int totalTicks = Constant.TICKSPERDAY * (PAGE_COUNT - 1) + TimeUtil.getTicksOfDay();
		// if (sleepBegin) {
		// BRDailyData brDailyData = BRDailyData.getByDayIndex(dayIndex + 2);
		// if (brDailyData != null && brDailyData.wakeUpTime != null) {
		// totalTicks = Constant.TICKSPERDAY +
		// brDailyData.wakeUpTime.getMinutes() * 2 - 2;
		// }
		// } else {
		// totalTicks = Constant.TICKSPERDAY +
		// BraceletOriginalDataDao.Instance().getLastSportTimeInday(dayIndex +
		// 2, userId);
		//
		// }
		int scrollX = (int) (totalTicks * xScale) - symbolLine;
		hScrollView.smoothScrollTo(scrollX, 0);
		OnScrollchanged(scrollX);
	}

	/**
	 * 更新显示图表指针状态和信息
	 * 
	 * @param detailData
	 * @param begin
	 */
	private void updateStateAndInfo(List<BRDetailData> detailData, int begin)
	{
		int listIndex = GetDetailIndex(detailData, begin);
		if (listIndex > -1)
		{
			BRDetailData data = detailData.get(listIndex);
			Log.d(TAG, "get Detail Data:" + data.toString());
			SetStateAndInfo(context, data);
		}
		else
		{
			SetStateAndInfo(context, null);
		}
	}

	/**
	 * 获取图表指针所时间点对应的运动数据的条目索引
	 * 
	 * @param detailData
	 * @param ticksFromDayBegin
	 * @return
	 */
	private int GetDetailIndex(List<BRDetailData> detailData, int ticksFromDayBegin)
	{
		int index = -1;
		if (detailData != null && detailData.size() > 0)
		{
			int i = 0;
			for (BRDetailData data : detailData)
			{
				if (data.getState() < BRDetailData.STATE_WALKING || data.getState() > BRDetailData.STATE_SLEEP_DEEP || data.getDuration() == 0)
				{
					i++;
					continue;
				}

				if ((chartDayIndex == data.getDayIndex()) && (data.getBegin() <= ticksFromDayBegin)
						&& ((data.getBegin() + data.getDuration()) >= ticksFromDayBegin))
				{
					index = i;
					break;
				}
				else if (chartDayIndex == data.getDayIndex() && (data.getBegin() <= ticksFromDayBegin)
						&& ((data.getBegin() + data.getDuration()) >= ticksFromDayBegin - 1 / xScale))
				{
					index = i;
					break;
				}
				i++;
			}
		}
		// Log.d(TAG, "getDetailIndex:"+index);
		return index;
	}

	private void OnScrollchanged(int scrollX)
	{
		// 滑动的总时间片数
		int totalMinute = (int) ((scrollX + symbolLine) / xScale);
		// 图表滑动到时日期内的偏移时间片数
		ticksFromDayBegin = totalMinute % Constant.TICKSPERDAY;
		// 图表当前日期对应的时间片
		chartDayIndex = dayIndex + totalMinute / Constant.TICKSPERDAY +1;

		if (detailTimeChangeCallback != null)
		{
					detailTimeChangeCallback.OnDetailTimeChange(chartDayIndex, ticksFromDayBegin);
		}

		List<BRDetailData> tmpDetailData = new ArrayList<BRDetailData>();
		
		for (int i = 0; i < lLayout.getChildCount(); i++)
		{
			tmpDetailData.addAll((List<BRDetailData>) lLayout.getChildAt(i).getTag());
		}
		updateStateAndInfo(tmpDetailData, ticksFromDayBegin);
		// dateTextview.setText(TimeUtil.formatDateMD(TimeUtil.getDateByDay(chartDayIndex)));
		// Log.e(TAG, "ticksFromDayBegin:" + ticksFromDayBegin);
		timeTextview.setText(TimeUtil.formatTimeFromMinuteCount(TimeUtil.parseSecFromTicks(ticksFromDayBegin)));
		Log.i(TAG, "能显示的最后时间是："+TimeUtil.formatTimeFromMinuteCount(TimeUtil.parseSecFromTicks(ticksFromDayBegin)));
		// timeTextview.setText(String.valueOf(chartResultList.get(0).getStartTime()
		// * 30000));
		/*
		 * if (chartDayIndex >
		 * TimeUtil.getDayCountFrom1970(System.currentTimeMillis())) {
		 * MCToast.show("请耐心等待明天的到来", context); } else if (chartDayIndex <
		 * this.firstDataBeginDay) { MCToast.show("没有更早的数据", context); }
		 */
	}

	private class TouchListenerImpl implements OnTouchListener
	{
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent)
		{
			switch (motionEvent.getAction())
			{
			
			case MotionEvent.ACTION_DOWN:
				break;
				
			case MotionEvent.ACTION_MOVE:
				int scrollX = view.getScrollX();
				int width = view.getWidth();
				int scrollViewMeasuredWidth = hScrollView.getChildAt(0).getMeasuredWidth();

				Log.i("MotionEvent_scrollX", String.valueOf(scrollX));

				OnScrollchanged(view.getScrollX());
				if (lastScrollX != scrollX)
				{
					if (scrollX < scrollRange)
					{
						if (AsyncAddDetailChart(AddType.LEFT))
						{
							view.scrollTo(imageWidth + scrollRange, 0);
						}
						else if (scrollX == 0)
						{
							// TODO: 提示没有更多数据
							// MCToast.show(context.getString(R.string.home_data_nomore),
							// context);
						}
					}
					else if (scrollX + width > scrollViewMeasuredWidth - scrollRange)
					{
						if (AsyncAddDetailChart(AddType.RIGHT))
						{
							view.scrollTo(scrollX - imageWidth, 0);
						}
						else if (scrollX + width == scrollViewMeasuredWidth)
						{
							// TODO:提示等待明天到来
							// MCToast.show(context.getString(R.string.home_data_tomorrow),
							// context);
						}

					}
					lastScrollX = scrollX;
				}
				break;
			case MotionEvent.ACTION_UP:
				break;
			default:
				break;
			}
			return false;
		}
	};

	// protected abstract List<BRDetailData> getBRDetailDatas();
	/**
	 * 添加图表新条目（分为 左边添加 和 右边添加 两种）
	 * 
	 * @param addType
	 * @return
	 */
	private boolean AsyncAddDetailChart(AddType addType)
	{
		// 准备图表上要显示的数据
		final List<BRDetailData> detailData = new ArrayList<BRDetailData>();
		// final ImageView imageview = new ImageView(context);
		final DetailChartItemView item = new DetailChartItemView(context);
		item.setLayoutParams(new LayoutParams(imageWidth, imageHeight));
		final int chartDayIndex;
		if (addType == AddType.LEFT)
		{
			chartDayIndex = dayIndex;
			Log.e(TAG, "AddingLeft chartDayIndex:" + TimeUtil.formatDateByYYYYMMDD(TimeUtil.parseDateFromDayIndex(chartDayIndex)));
		}
		else
		{
			chartDayIndex = dayIndex + lLayout.getChildCount() + 1;
			Log.e(TAG, "AddingRight chartDayIndex:" + TimeUtil.formatDateByYYYYMMDD(TimeUtil.parseDateFromDayIndex(chartDayIndex)));
		}
		int curDayIndex = TimeUtil.getDayIndexFrom1970(System.currentTimeMillis());
//		
		if (chartDayIndex <= curDayIndex)
		// && chartDayIndex >= userCreatedDay - 1
		// || curDayIndex < userCreatedDay)
		{
			// if (chartDayIndex == firstDataBeginDay - 1) {
			// if (addType == AddType.LEFT) {
			// //从云端获取更早数据
			// // OnNeedEarlyData(firstDataBeginDay);
			// }
			// return false;
			// }
			new DetailChartDataCreator()
			{
				@Override
				public void onDataResult(List<BRDetailData> result, DetailChartCountData count)  //标记
				{
					if (item.isRecycled())
					{
						return;
					}

					if (result == null || count == null)
					{
						Log.e(TAG, "详细数据获取失败！！！！！！！！！！！！！");
						return;
					}
					
					/***************判断是否是夏令时****************/
//					Calendar c = Calendar.getInstance(TimeZone.getDefault());
//					int zone =  c.get(Calendar.DST_OFFSET)/ (60 * 60 * 1000); // 时区偏移值
//					Log.e(TAG, "时区偏移值！！！！！！！！！！！！"+zone);
//					if(zone>0){
//						for(int i =0;i<result.size();i++){
//							
//							result.get(i).setBegin(result.get(i).getBegin()+120);
//						}
//					}
					
					Bitmap bitmapChart = detailBitmapCreator.drawDetailChart(result, chartDayIndex);
					item.setImageBitmap(bitmapChart);
					// imageview.setImageBitmap(bitmapChart);
					item.setTag(result);

					Log.d(TAG, "Add success chartDayIndex:" + TimeUtil.formatDateByYYYYMMDD(TimeUtil.parseDateFromDayIndex(chartDayIndex)));
					if (result.size() > 0)
						Log.d(TAG, "result dayIndex:" + TimeUtil.formatDateByYYYYMMDD(TimeUtil.parseDateFromDayIndex(result.get(0).getDayIndex())));
					Log.i(TAG, "count soft_sleep_duration:" +count.soft_sleep_duration + "-----count deep_sleep_duration:" +count.deep_sleep_duration);
					
					Log.i(TAG, "按天存储运动数据统计数据");
					
					PreferencesToolkits.updateDetailChartCountDate(context, count, chartDayIndex);
					
					if (detailDataCountCallback != null)
						detailDataCountCallback.OnDetailDataCountChange();

					List<BRDetailData> tmpDetailData = new ArrayList<BRDetailData>();
					for (int i = 0; i < lLayout.getChildCount(); i++)
					{
						tmpDetailData.addAll((List<BRDetailData>) lLayout.getChildAt(i).getTag());
					}
					updateStateAndInfo(tmpDetailData, ticksFromDayBegin);
				}

				@Override
				protected void addAsyncTask(AsyncTask at)
				{
					asyncTaskManger.addAsyncTask(at);
				}

				@Override
				protected void removeAsyncTask(AsyncTask at)
				{
					asyncTaskManger.removeAsyncTask(at);
				}
			}.getDetailChartData(context, chartDayIndex);

			// Bitmap bitmapChart =
			// detailBitmapCreator.drawDetailChart(detailData, chartDayIndex);
			// imageview.setImageBitmap(bitmapChart);
			// imageview.setScaleType(ImageView.ScaleType.FIT_XY);
			// imageview.setTag(detailData);
			item.setTag(detailData);
			if (addType == AddType.LEFT)
			{
				// lLayout.addView(imageview, 0);
				lLayout.addView(item, 0);
				dayIndex--;
			}
			else
			{
				// lLayout.addView(imageview);
				lLayout.addView(item);
			}
			Log.d(TAG, "Layout child count:" + lLayout.getChildCount());
			if (lLayout.getChildCount() > PAGE_COUNT)
			{
				DetailChartItemView removed;
				if (addType == AddType.LEFT)
				{
					int lastIndex = lLayout.getChildCount() - 1;
					removed = (DetailChartItemView) lLayout.getChildAt(lastIndex);
					lLayout.removeViewAt(lastIndex);
					Log.d(TAG, "recycle Left!!!!!!!!!!!!!!!!!!!!!!!!!");
				}
				else
				{
					removed = (DetailChartItemView) lLayout.getChildAt(0);
					lLayout.removeViewAt(0);
					dayIndex++;
					Log.d(TAG, "recycle Right!!!!!!!!!!!!!!!!!!!!!!!!!");
				}
				// recycleImageView(removed);
				removed.recycle();
			}
			return true;
		}
		Log.d(TAG, "Add failed chartDayIndex:" + TimeUtil.formatDateByYYYYMMDD(TimeUtil.parseDateFromDayIndex(chartDayIndex)));
		return false;
	}

	// private boolean addDetailChart(AddType addType) {
	// // List<BRDetailData> detailData = InitDayData(dayIndex);
	// int chartDayIndex;
	// if (addType == AddType.LEFT) {
	// chartDayIndex = dayIndex;
	// } else {
	// chartDayIndex = dayIndex + lLayout.getChildCount() + 1;
	// }
	// int curDayIndex = TimeUtil.getDayIndexFrom1970(System
	// .currentTimeMillis() + 1);
	// if (chartDayIndex <= curDayIndex && chartDayIndex >= userCreatedDay - 1
	// || curDayIndex < userCreatedDay) {
	//
	// // if (chartDayIndex == firstDataBeginDay - 1) {
	// // if (addType == AddType.LEFT) {
	// // //从云端获取更早数据
	// // // OnNeedEarlyData(firstDataBeginDay);
	// // }
	// // return false;
	// // }
	// long time = TimeUtil.getTimeByDayIndex(chartDayIndex);
	// long end = TimeUtil.getTimeByDayIndex(chartDayIndex + 1);
	// DataFromServer obj = HttpHelper.submitQuerySportRecordsToServer_l(
	// context, sdf.format(time), sdf.format(end), false, true);
	//
	// if (obj == null || obj.getReturnValue() == null || !obj.isSuccess()) {
	// Log.d(TAG, "load detail data is  null!!!!!!!!!!!!!!!!!!!");
	// return false;
	// }
	//
	// // List<BRDetailData> detailData =
	// // HttpHelper.submitQuerySportRecordsToServer_l(context,
	// // sdf.format(chartDayIndex), sdf.format(chartDayIndex), online,
	// // true);
	// // 原始运动数据
	// List<SportRecord> originalSportDatas = HttpHelper
	// .parseQuerySportRecordsFromServer(context,
	// (String) (obj.getReturnValue()), false);
	// // 计算睡眠
	// List<DLPSportData> srs = SleepDataHelper
	// .querySleepDatas2(originalSportDatas);
	// // 将睡眠算法计算完成的睡眠状态回填（那么这样的话，在组织成日汇总数据时也就能合计出睡眠时间了）
	// ToolKits.putSleepStateFromSleepResult(originalSportDatas, srs);
	// // 准备图表上要显示的数据
	// List<BRDetailData> detailData = new ArrayList<BRDetailData>();
	// // for (SportRecord row : srs)
	// for (DLPSportData row : srs)
	// {
	// detailData.add(new BRDetailData(row));
	// }
	//
	// Bitmap bitmapChart = detailBitmapCreator.drawDetailChart(
	// detailData, chartDayIndex);
	// ImageView imageview = new ImageView(context);
	// imageview.setImageBitmap(bitmapChart);
	// imageview.setScaleType(ImageView.ScaleType.FIT_XY);
	// if (addType == AddType.LEFT) {
	// lLayout.addView(imageview, 0);
	// dayIndex--;
	// } else {
	// lLayout.addView(imageview);
	// }
	//
	// if (lLayout.getChildCount() > 2) {
	// ImageView removed;
	// if (addType == AddType.LEFT) {
	// int lastIndex = lLayout.getChildCount() - 1;
	// removed = (ImageView) lLayout.getChildAt(lastIndex);
	// lLayout.removeViewAt(lastIndex);
	// } else {
	// removed = (ImageView) lLayout.getChildAt(0);
	// lLayout.removeViewAt(0);
	// dayIndex++;
	// }
	// recycleImageView(removed);
	// }
	// return true;
	// } else {
	// return false;
	// }
	//
	// }

	// For test Data
	// private List<BRDetailData> InitDayData(int dayIndex) {
	// List<BRDetailData> dayData = new ArrayList<BRDetailData>();
	//
	// int sleepStart = 0;
	// for (int i = sleepStart; i < sleepStart + 28; i++) {
	// BRDetailData data = new BRDetailData();
	// data.setSleep(true);
	// data.setDayIndex(dayIndex);
	// data.setBegin(i * Constant.TICKSPERHOUR / 4);
	// data.setDuration(Constant.TICKSPERHOUR / 4);
	// data.setState(rand.nextInt(8) / 3 + 3);
	// dayData.add(data);
	//
	// }
	//
	// int sportStart = 28;
	// for (int i = sportStart; i < sportStart + 64; i++) {
	// BRDetailData data = new BRDetailData();
	// data.setSleep(false);
	// data.setDayIndex(dayIndex);
	// data.setBegin(i * Constant.TICKSPERHOUR / 4);
	// data.setDuration(Constant.TICKSPERHOUR / 4);
	// data.setSteps(rand.nextInt(20) * Constant.TICKSPERHOUR / 4);
	// dayData.add(data);
	//
	// }
	//
	// sleepStart = 92;
	// for (int i = sleepStart; i < sleepStart + 4; i++) {
	// BRDetailData data = new BRDetailData();
	// data.setSleep(true);
	// data.setDayIndex(dayIndex);
	// data.setBegin(i * Constant.TICKSPERHOUR / 4);
	// data.setDuration(Constant.TICKSPERHOUR / 4);
	// data.setState(rand.nextInt(8) / 3 + 3);
	// dayData.add(data);
	// }
	//
	// return dayData;
	// }

	// public void OnNeedEarlyData(int dayIndex) {
	// Log.i(TAG, "OnNeedEarlyData");
	//
	// syncing = true;
	// LPSportData lpSportData =
	// BraceletOriginalDataDao.Instance().getEarliestOriginalData(userId);
	// if (lpSportData != null) {
	// int day = lpSportData.getDayIndex();
	// int begin = lpSportData.getBegin();
	// long to = TimeUtil.parseTimeFromDayIndexAndSeconds30Count(day, begin);
	// getOneWeekOriginalDataFromCloud(to);
	// } else {
	// long to = TimeUtil.parseTimeFromDayIndexAndSeconds30Count(dayIndex, 0);
	// getOneWeekOriginalDataFromCloud(to);
	// }
	// }

	// private void getOneWeekOriginalDataFromCloud(final long to) {
	// new AsyncTask<Object, String, Boolean>() {
	// @Override
	// protected void onPreExecute() {
	// MCProgress.show(context, null,
	// context.getString(R.string.common_loading), true, false);
	// }
	//
	// @Override
	// protected Boolean doInBackground(Object... params) {
	// try {
	// long end = to;
	// for (int i = 0; i < 2; i++) {
	// end -= i * Constant.ONE_DAY_MILLIS;
	// cloudSyncMediator.getOriginalData(end - Constant.ONE_DAY_MILLIS, end);
	// firstDataBeginDay--;
	// }
	// return true;
	// } catch (MCException e) {
	// e.printStackTrace();
	// return false;
	// }
	// }
	//
	// @Override
	// protected void onPostExecute(Boolean result) {
	// if (result) {
	// if (addDetailChart(AddType.LEFT)) {
	// hScrollView.scrollTo(imageWidth + scrollRange, 0);
	// }
	// }
	// MCProgress.dismiss();
	// syncing = false;
	// };
	// }.execute();
	// }

	private void initXScale()
	{
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		// imageWidth = width * 2;
		imageWidth = width;
		imageHeight = width / 2;
		// xScale = (float) width / (Constant.TICKSPERHOUR * 12f); // 12 个小时一屏
		xScale = (float) width / (Constant.TICKSPERHOUR * 24f);// 24小时一屏
		yScale = imageHeight / 80;
		// scrollRange = width / 4;
		scrollRange = width / 8 ;
		Log.i(TAG, "scrollRange:"+scrollRange);
	}

	// private void recycleImageView(ImageView imageView) {
	// BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView
	// .getDrawable();
	// if (!bitmapDrawable.getBitmap().isRecycled()) {
	// bitmapDrawable.getBitmap().recycle();
	// }
	// }

	public String GetStateString(Context context, int state)
	{
		String stateStr = "";
		switch (state)
		{
		case 0:
			// "静坐"
			stateStr = context.getString(RHolder.getInstance().getEva$android$R().string("home_state_sit"));
			break;
		case 1:
			// "走路"
			stateStr = context.getString(RHolder.getInstance().getEva$android$R().string("home_state_walk"));
			break;
		case 2:
			// "跑步"
			stateStr = context.getString(RHolder.getInstance().getEva$android$R().string("home_state_run"));
			break;
		case 3:
			// "活动"
			stateStr = context.getString(RHolder.getInstance().getEva$android$R().string("home_state_active"));
			break;
		case 4:
			// "浅睡"
			stateStr = context.getString(RHolder.getInstance().getEva$android$R().string("common_sleep_light"));
			break;
		case 5:
			// "深睡"
			stateStr = context.getString(RHolder.getInstance().getEva$android$R().string("common_sleep_sound"));
		default:
			break;
		}

		return stateStr;
	}
	
//	public static String getDayTime(int dayIndex, int begin)
//	  {
//		
//	    Date d = new Date((dayIndex * 2880 * 30 + 3600 + begin * 30) * 1000L);
//	    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	    sdf1.setTimeZone(TimeZone.getTimeZone("GMT"));
//	    return sdf1.format(new Date(d.getTime()));
//	    
//	  }

	public String GetDurationString(Context context, int duration)
	{
		String durationStr = "";
		String unitStr = "";
		if (duration < 2)
		{
			unitStr = context.getResources().getString(RHolder.getInstance().getEva$android$R().string("unit_second"));
			durationStr = String.valueOf(duration * 30) + unitStr;
		}
//		else if (duration >= 120)
//		{
//			unitStr = context.getResources().getString(RHolder.getInstance().getEva$android$R().string("unit_hour"));
//			durationStr = String.valueOf(duration / 120) + unitStr;
//		}
		else
		{
			unitStr = context.getResources().getString(RHolder.getInstance().getEva$android$R().string("unit_minute"));
			durationStr = String.valueOf(duration / 2) + unitStr;
		}
		return durationStr;
	}
}
