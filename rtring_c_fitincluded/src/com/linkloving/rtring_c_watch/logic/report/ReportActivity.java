package com.linkloving.rtring_c_watch.logic.report;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import com.alibaba.fastjson.JSONObject;
import com.eva.android.platf.std.DataLoadableActivity;
import com.eva.android.widget.DataLoadingAsyncTask;
import com.eva.epc.common.util.CommonUtils;
import com.eva.epc.core.dto.DataFromServer;
import com.linkloving.band.dto.DaySynopic;
import com.linkloving.band.dto.SleepData;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.utils.DateSwitcher;
import com.linkloving.rtring_c_watch.utils.DateSwitcher.PeriodSwitchType;
import com.linkloving.rtring_c_watch.utils.HttpHelper;
import com.linkloving.rtring_c_watch.utils.SkinSettingManager;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.linkloving.utils._Utils;
import com.lz.rtchart.ChartView2;
import com.rtring.buiness.logic.dto.ActiveSwitchType;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class ReportActivity extends DataLoadableActivity 
{
	private final static String TAG = ReportActivity.class.getSimpleName();
	
	/** 按钮：返回 */
	private Button btnBack = null;
	
	/** 数据显示图表实现类 */
	private BarChartUIWrapper barChartUIWrapper = null;
	/** 标题周期切换实现类 */
	private PeriodFilterUIWrapper periodFilterUIWrapper = null;
	/** 时间范围切换实现类 */
	private TimeFilterUIWrapper timeFilterUIWrapper = null;
	/** 活动类型切换实现类（图表上方的Tab切换） */
	private ActiveFilterUIWrapper activeFilterUIWrapper = null;
	/** 总性描述数据显示实现类（上部的步数、距离、卡路里、睡眠合计） */
	private CircleChartUIWrapper circleChartUIWrapper = null;
	
	/** 中间柱状图表的父layout */
	private ViewGroup vgBarChartLL = null;
	
	private ProgressDialog loadingDialog = null;
	
	/** 一键分享按钮 */
	private Button btnShare = null;
	
	private final String mPageName = "ReportPage";
	private SkinSettingManager mSettingManager;
	@Override
	public void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
//		MobclickAgent.onPageEnd(mPageName);
	}

	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		mSettingManager=new SkinSettingManager(this);
		mSettingManager.initSkins();
//		MobclickAgent.onPageStart(mPageName);
	}
	
	@Override
	protected void initViews()
	{
		//		customeTitleBarResId = R.id.forget_password_title_bar;
		setContentView(R.layout.report_page_activity);
		
		btnBack = (Button)this.findViewById(R.id.report_page_activity_backToFriendsBtn);
		btnShare = (Button) this.findViewById(R.id.report_page_activity_shareRtn);
		vgBarChartLL = (ViewGroup) findViewById(R.id.report_page_activity_barchartLL);
		
		barChartUIWrapper = new BarChartUIWrapper(this);
		periodFilterUIWrapper = new PeriodFilterUIWrapper(this){
			@Override
			protected void onFilterChaged(int periodSwitchType)
			{
				switchData();
				// 周期切换的同时刷新日期范围的文本显示
				timeFilterUIWrapper.refreshShowText();
			}
		};
		
		timeFilterUIWrapper = new TimeFilterUIWrapper(this)
		{
			@Override
			protected void onFilterChaged()
			{
				switchData();
			}

			@Override
			protected int getPeriodSwitchType()
			{
				return periodFilterUIWrapper.getPeriodFilterType();
			}
		};
		
		activeFilterUIWrapper = new ActiveFilterUIWrapper(this){
			@Override
			protected void onFilterChaged()
			{
				switchData();
			}
		};
		
		circleChartUIWrapper = new CircleChartUIWrapper(this);
		
		// 刷新默认日期范围的文本显示
		timeFilterUIWrapper.refreshShowText();

//		setTitle("测试日折线图");

		this.setLoadDataOnCreate(false);
		
		// 默认载入数据
		switchData();
	}

	/**
	 * 为各UI功能组件增加事件临听的实现方法.
	 * {@inheritDoc}
	 */
	@Override
	protected void initListeners()
	{
		//标题返回按钮事件处理
		btnBack.setOnClickListener(new OnClickListener(){
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
				loadingDialog = new ProgressDialog(ReportActivity.this);
				loadingDialog.setProgressStyle(0);
				loadingDialog.setMessage($$(R.string.detail_sport_data_image_loading));
				loadingDialog.show();
				
				new ScreenHotAsyncTask().execute(v);
			}
		});
	}

	//--------------------------------------------------------------------------------------------
	/**
	 * 从服务端查询数据并返回.
	 * 
	 * @param params loadData中传进来的参数，本类中该参数没有被用到
	 */
	@Override protected DataFromServer queryData(String... params)
	{
		return null;
	}
	//将已构造完成的完整的明细数据放入列表中显示出来
	@Override protected void refreshToView(Object dateToView)
	{
	}
	
	/**
	 * 
	 * @param isActiveFilterSwitch true表示数据切换来源自“活动类型”的切换（比如切换到“运动”、“跑步”、“睡眠”、“卡路里”）
	 */
	private void switchData(
//			boolean isActiveFilterSwitch
			)
	{
		String sss = "刷新：周期="+periodFilterUIWrapper.getPeriodFilterType()
				+", 活动类型="+activeFilterUIWrapper.getActiveSwitchType()
				+", 日期范围："+timeFilterUIWrapper.getDateSwitcher().getSQLBetweenAnd();
		Log.d(TAG, sss);
//		WidgetUtils.showToast(this, sss, ToastType.OK);
		
		String startDateStr = timeFilterUIWrapper.getDateSwitcher().getStartDateStr();
		String endDateStr = timeFilterUIWrapper.getDateSwitcher().getEndDateStr();
		int activeSwitchType = activeFilterUIWrapper.getActiveSwitchType();
		int periodSwitchType = periodFilterUIWrapper.getPeriodFilterType();
		
		// ** 异步刷新概览数据的显示
//		circleChartUIWrapper.showActiveForWorkAsync(startDateStr, endDateStr);//, activeSwitchType);
		// ** 异步刷新中部图表数据的显示
		barChartUIWrapper.loadDataAsync(startDateStr, endDateStr, activeSwitchType, periodSwitchType, new Observer(){
			@Override
			public void update(Observable observable, Object data)
			{
				revalidateBarChartView();
				
//				Log.e(TAG, "=======================data"+data);
				if(data != null)
				{
					// FIXME ：目前，如果在睡眠界面中切换日期时，此概览数据就没法变了，
					//		因为睡眠计算时再计算概览数据就会消耗更多的计算量，算了，谁会在意呢？？
					if(data != null)//instanceof List<?>)
					{
						List<DaySynopic> ds = (List<DaySynopic>)data;
//						Log.e(TAG, "=======================0"+ds.size());
						circleChartUIWrapper.showActiveForSleep(HttpHelper
							.parseDaySynopicSumForPreview(ReportActivity.this, ds));
					}
				}
			}
		});
	}
	
	/**
	 * 刷新柱状图的显示.
	 * <p>
	 * 为何要调用此方法呢？因为不知何故AChartEngine中的图表需要在addView前repaint才有效，
	 * 为了让他能刷新显示，目前只能先让它remove后再addView，就正常了，具体问题是什么目前还没有深究！
	 */
	private void revalidateBarChartView()
	{
		vgBarChartLL.removeAllViews();
		vgBarChartLL.addView(barChartUIWrapper.getChartView(), new LayoutParams(LayoutParams.MATCH_PARENT,
		          LayoutParams.MATCH_PARENT));
//		
//		System.out.println("到这里来了吗？？？？？？？？？？？？？？？？？？？？？？？？？？？？？");
	}
	
	public int reandom()
	{
		return Math.abs(new Random().nextInt()) % 20000;
	}

	//------------------------------------------------------------------------ inner classes
	private static class BarChartUIWrapper
	{
		private ChartView2 mChartView = null;
		private Activity parentActivity = null;
		
		private ViewGroup layoutOfLengendForSleep = null;
		private TextView viewLengendLeft = null;
		private TextView viewLengendRight = null;
		
		private final int WEEK_WIDTH_DP = 30;
		private final int WEEK_GAP_DP = 30;
		private final int MONTH_WIDTH_DP = 20;
		private final int MONTH_GAP_DP = 20;

		public BarChartUIWrapper(Activity parentActivity)
		{
			this.parentActivity = parentActivity;
			initViews();
//			refreshData();
		}

		private void initViews()
		{
			mChartView = (ChartView2)parentActivity.findViewById(R.id.report_page_activity_barchartView);
			layoutOfLengendForSleep = (ViewGroup)parentActivity
					.findViewById(R.id.report_page_activity_chartLengendLL);
			viewLengendLeft = (TextView)parentActivity
					.findViewById(R.id.report_page_activity_chartLengendLeftView);
			viewLengendRight = (TextView)parentActivity
					.findViewById(R.id.report_page_activity_chartLengendRightView);
			mChartView.setDataDate(new String[]{});
			mChartView.setDataValue(new double[]{});
			mChartView.setChatWidthdp(WEEK_WIDTH_DP);
			mChartView.setChatGapdp(WEEK_GAP_DP);
//			mChartView.setTextSize(ToolKits.dip2px(parentActivity, 12));
//			mChartView.setTextColorId(R.color.chat_bg_color);
		}
		
		public void loadDataAsync(final String startDate, final String endDate, final int activeSwitchType, final int periodSwitchType, final Observer obsAfterSucess)
		{
			new DataLoadingAsyncTask<Object, Integer, DataFromServer>( parentActivity , parentActivity.getString( R.string.report_data_reading )){
				private boolean online = false;
				
				@Override
				protected DataFromServer doInBackground(Object... params)
				{
					online = ToolKits.isNetworkConnected(context);
					
					DataFromServer dfs = new DataFromServer();
					try
					{
						if(activeSwitchType == ActiveSwitchType.sleep)
						{
							// 如果查询的是睡眠数据，则单独使用睡眠数据查询接口
							// 注意：目前汇总数据里的睡眠是无条件使用的本地数据（即使有网时也不尝试从网络下载，
							//       目的是尽可能减少因网络加截而额外带来的体验下降，反正此时的汇总只是用来看的，无所谓了！）
							List<SleepData> multiSleepDatas = HttpHelper.offlineReadMultiDaySleepDataToServer(
									parentActivity, startDate, endDate);
							dfs.setReturnValue(multiSleepDatas);
						}
						else
						{
							List<DaySynopic> dss = HttpHelper.submitQueryDaySynopicDatasToServerNew(parentActivity, startDate, endDate, online);
							dfs.setReturnValue(dss);
//							Log.e(TAG, "通用数据计算完成："+dss.size());
						}
						
						dfs.setSuccess(true);
					}
					catch (Exception e)
					{
						Log.e(TAG, e.getMessage(), e);
						dfs.setSuccess(false);
						dfs.setReturnValue(e.getMessage());
					}
					return dfs;
				}
				
				@Override
				protected void onPostExecuteImpl(Object arg0)
				{
					try
					{
//					Log.e(TAG, "【服务端成功返回：正在刷新周/月图数据】arg0="+arg0);
					if(arg0 != null && (arg0 instanceof List<?>))
					{
						final SimpleDateFormat sdfYYMMDD = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD);
						final SimpleDateFormat sdfMMDD = new SimpleDateFormat(ToolKits.DATE_FORMAT_MM_DD);

						Object dataToObserver = null;
						String[] xValues = null;
						double[] yValues = null;
						double[] y2Values = null;
						if(activeSwitchType == ActiveSwitchType.sleep)
						{
							// 解析出折线图原始数据
//							List<SleepData> srs = new Gson().fromJson((String)arg0, new TypeToken<List<SleepData>>(){}.getType());
//							List<DaySynopic> srs = HttpHelper.parseQueryDaySynopicDatasFromServer(parentActivity, (String)arg0, online);
							List<SleepData> srs = (ArrayList<SleepData>)arg0;
							xValues = new String[srs.size()];
							yValues = new double[srs.size()];
							y2Values = new double[srs.size()];
							int j = 0, k = 0, l = 0;
							for(int i = 0; i < srs.size(); i++)
							{
//								SleepData sr = srs.get(i);
								SleepData sr = srs.get(i);
								double yValue = 0;
								double y2Value = 0;
								// 睡眠=浅睡+深睡（单位：小时）
								y2Value = CommonUtils.getScaledDoubleValue(sr.getSleep(), 1);
								yValue = CommonUtils.getScaledDoubleValue(sr.getDeepSleep(), 1);
//								yValue = CommonUtils.getScaledDoubleValue(sr.getSleep()+sr.getDeepSleep(), 1);
								String xValue = sr.getDate();
								try
								{
									Date date = sdfYYMMDD.parse(sr.getDate());
									xValue = sdfMMDD.format(date);
								}
								catch (Exception e)
								{
									Log.w(TAG, e);
								}

								xValues[j++] = xValue;//sr.getData_date();
								yValues[k++] = yValue;
								y2Values[l++] = y2Value;
							}
							
							// FIXME ：目前，如果在睡眠界面中切换日期时，此概览数据就没法变了，
							//		因为睡眠计算时再计算概览数据就会消耗更多的计算量，算了，谁会在意呢？？
							dataToObserver = null;
						}
						else
						{
							// 解析出折线图原始数据
//							List<DaySynopic> srs = new Gson().fromJson((String)arg0, new TypeToken<List<DaySynopic>>(){}.getType());
//							List<DaySynopic> srs = HttpHelper.parseQueryDaySynopicDatasFromServer(parentActivity, (String)arg0, online);
							List<DaySynopic> srs = (List<DaySynopic>)arg0;
							
							xValues = new String[srs.size()];
							yValues = new double[srs.size()];
							y2Values = new double[srs.size()];
							int j = 0, k = 0, l = 0;
							for(int i = 0; i < srs.size(); i++)
							{
								DaySynopic sr = srs.get(i);
								double yValue = 0;
								double y2Value = 0;

								switch(activeSwitchType)
								{
									// 运动步数（跑步+走路）
									case ActiveSwitchType.step:
//										yValue = CommonUtils.getIntValue(sr.getRun_step()) + CommonUtils.getIntValue(sr.getWork_step());
										y2Value = CommonUtils.getIntValue(sr.getWork_step());
										yValue = CommonUtils.getIntValue(sr.getRun_step());
										break;
										// 运动里程（跑步+走路）时X轴是日期、Y轴是距离
									case ActiveSwitchType.distance:
//										yValue = CommonUtils.getIntValue(sr.getRun_distance()) + CommonUtils.getIntValue(sr.getWork_distance());
										if(MyApplication.getInstance(parentActivity).getUNIT_TYPE().equals("Imperial")){
											y2Value = ToolKits.MChangetoMIRate(CommonUtils.getIntValue(sr.getWork_distance()));
											yValue = ToolKits.MChangetoMIRate(CommonUtils.getIntValue(sr.getRun_distance()));
										}else{
											y2Value = (CommonUtils.getIntValue(sr.getWork_distance()));
											yValue = (CommonUtils.getIntValue(sr.getRun_distance()));
										}
										
										break;
//										// 睡眠=浅睡+深睡（单位：小时）
//										case ActiveSwitchType.sleep:
//										{
////										yValue = CommonUtils.getIntValue(reandom());//TODO　睡眠数据目前还未有算法解决，先用随机数据演示之！！！
//										// 睡眠=浅睡+深睡（单位：小时）
//											yValue = CommonUtils.getScaledDoubleValue(CommonUtils.getIntValue(sr.getSleepMinute())
//												+CommonUtils.getIntValue(sr.getDeepSleepMiute()), 1);
//											break;
//										}
										// 运动（跑步+走路）时X轴是日期、Y轴是日总消耗卡路里
									case ActiveSwitchType.ca:
									{
										// 跑步平均速度
										double runSpeed = 0;
										int ipbzsj = CommonUtils.getIntValue(sr.getRun_duration()); // 跑步总时间（int型）
										if(ipbzsj > 0)
											runSpeed = CommonUtils.getIntValue(sr.getRun_distance()) / (ipbzsj * 1.0);
	
										// 走路平均速度
										double workSpeed = 0;
										int izlzsj = CommonUtils.getIntValue(sr.getWork_duration()); // 走路总时间（int型）
										if(izlzsj > 0)
											workSpeed = CommonUtils.getIntValue(sr.getWork_distance()) / (izlzsj * 1.0);
	
										// 动运总消耗卡路里=跑步消耗+动动消耗
										int userWeight = CommonUtils.getIntValue(MyApplication.getInstance(context)
												.getLocalUserInfoProvider().getUser_weight());
//										yValue  = _Utils.calculateCalories(runSpeed, ipbzsj, userWeight)
//												+ _Utils.calculateCalories(workSpeed, izlzsj, userWeight);
										y2Value = _Utils.calculateCalories(workSpeed, izlzsj, (int) (userWeight*ToolKits.UNIT_LBS_TO_KG));
										yValue = _Utils.calculateCalories(runSpeed, ipbzsj, (int) (userWeight*ToolKits.UNIT_LBS_TO_KG));
										break;
									}
								}

								String xValue = sr.getData_date();
								try
								{
									Date date = sdfYYMMDD.parse(sr.getData_date());
									xValue = sdfMMDD.format(date);
								}
								catch (Exception e)
								{
									Log.w(TAG, e);
								}

								xValues[j++] = xValue;//sr.getData_date();
								yValues[k++] = (double)yValue;
								y2Values[l++] = y2Value;
							}
							
							//
							dataToObserver = srs;
						}
						
						if(xValues != null && yValues != null)
						{
							// 刷新UI数据的显示
							refreshData(xValues, yValues, y2Values, activeSwitchType, periodSwitchType);
							// 并通知观察者已成功完成数据加载和显示
							if(obsAfterSucess != null)
								obsAfterSucess.update(null, dataToObserver);
						}
					}
					else
					{
						Log.e(TAG, "agr0======"+arg0);
					}
					}
					catch (Exception e)
					{
						Log.e(TAG, e.getMessage(), e);
					}
				}
			}.execute();
		}
		
		/**
		 * 刷新折线数据.
		 * 
		 * @param values <[时间戳, 距离累加], [时间戳, 距离累加], [时间戳, 距离累加], ...>
		 */
		public void refreshData(String[] xValues, double[] yValues, double[] y2Values, final int activeSwitchType, int periodSwitchType)
		{
			if(periodSwitchType == PeriodSwitchType.week)
			{
				mChartView.setChatWidthdp(WEEK_WIDTH_DP);
				mChartView.setChatGapdp(WEEK_GAP_DP);
			}
			else
			{
				mChartView.setChatWidthdp(MONTH_WIDTH_DP);
				mChartView.setChatGapdp(MONTH_GAP_DP);
			}
			
			if(xValues != null && xValues.length > 0)
			{
				mChartView.setDataDate(xValues);
				mChartView.setDataValue(yValues);
				mChartView.setDataDValue(y2Values);
				
				layoutOfLengendForSleep.setVisibility(View.GONE);
				
				switch(activeSwitchType)
				{
					// 步数
					case ActiveSwitchType.step:
						mChartView.setTextDesc("");//ToolKits.getStringbyId(parentActivity, R.string.unit_step));
						mChartView.setYaxisInteger(true);
						layoutOfLengendForSleep.setVisibility(View.VISIBLE);
						viewLengendLeft.setText(parentActivity.getString(R.string.report_day_activte_lengend_step_work));
						viewLengendRight.setText(parentActivity.getString(R.string.report_day_activte_lengend_step_run));
						break;
						
					// 里程
					case ActiveSwitchType.distance:
						mChartView.setTextDesc("");//parentActivity.getString(R.string.unit_m));
						mChartView.setYaxisInteger(true);
						layoutOfLengendForSleep.setVisibility(View.VISIBLE);
						if(MyApplication.getInstance(parentActivity).getUNIT_TYPE().equals("Imperial")){
							viewLengendLeft.setText(parentActivity.getString(R.string.report_day_activte_lengend_distance_work));
							viewLengendRight.setText(parentActivity.getString(R.string.report_day_activte_lengend_distance_run));
						}else{
							viewLengendLeft.setText(parentActivity.getString(R.string.report_day_activte_lengend_distance_work_m));
							viewLengendRight.setText(parentActivity.getString(R.string.report_day_activte_lengend_distance_run_m));
						}
						
						break;
						
					// 睡眠
					case ActiveSwitchType.sleep:
						mChartView.setTextDesc("");//parentActivity.getString(R.string.unit_hour));
						mChartView.setYaxisInteger(false);
						layoutOfLengendForSleep.setVisibility(View.VISIBLE);
						viewLengendLeft.setText(parentActivity.getString(R.string.report_day_activte_lengend_sleep));
						viewLengendRight.setText(parentActivity.getString(R.string.report_day_activte_lengend_deepsleep));
						break;
						
					// 卡路里
					case ActiveSwitchType.ca:
						mChartView.setTextDesc("");//parentActivity.getString(R.string.unit_cal_big));
						mChartView.setYaxisInteger(true);
						layoutOfLengendForSleep.setVisibility(View.VISIBLE);
						viewLengendLeft.setText(parentActivity.getString(R.string.report_day_activte_lengend_ca_work));
						viewLengendRight.setText(parentActivity.getString(R.string.report_day_activte_lengend_ca_run));
						break;
				}
				
				mChartView.invalidate();
			}
		}
		
//		public int reandom()
//		{
//			return Math.abs(new Random().nextInt()) % 8;
//		}
		
		public ChartView2 getChartView()
		{
			return mChartView;
		}
	}
	
	/**
	 * 上部总结性数据Wrapper实现类.
	 */
	private static class CircleChartUIWrapper
	{
		private Activity parentActivity = null;
		
		private ActiveCompositionView viewLeftTop = null;
		private ActiveCompositionView viewRightTop = null;
		private ActiveCompositionView viewLeftBottom = null;
		private ActiveCompositionView viewRightBottm = null;
		
		public CircleChartUIWrapper(Activity parentActivity)
		{
			this.parentActivity = parentActivity;
			
			this.initViews();
		}
		
		private void initViews()
		{
			viewLeftTop = 	 (ActiveCompositionView)parentActivity.findViewById(R.id.report_page_activity_circleviews_leftTopActiveView);
			viewRightTop = 	 (ActiveCompositionView)parentActivity.findViewById(R.id.report_page_activity_circleviews_rightTopActiveView);
			viewLeftBottom = (ActiveCompositionView)parentActivity.findViewById(R.id.report_page_activity_circleviews_leftBottomActiveView);
			viewRightBottm = (ActiveCompositionView)parentActivity.findViewById(R.id.report_page_activity_circleviews_rightBottomActiveView);
		}

		public void showActiveForSleep(String resultJSONStr)
		{
			Log.e("ABC", "【你好】resultJSONStr="+resultJSONStr);
//			int ast = activeType;//activeFilterUIWrapper.getActiveSwitchType();
			{
				// 运动总量（米）
				String ydzjl = "0";
				// 运动总步数
				String ydzbs = "0";
				// 平均运动速度
				String speed = "0";
				// 消耗卡路里
				String ca = "0";
				if(resultJSONStr != null)
				{
					JSONObject nwObj = JSONObject.parseObject(resultJSONStr);
					ydzjl = nwObj.getString("ydzjl");// 注意与服务端的JSON key要一一对应！
					ydzbs = nwObj.getString("ydzbs");// 注意与服务端的JSON key要一一对应！
//					speed = nwObj.getString("speed");// 注意与服务端的JSON key要一一对应！
					ca = nwObj.getString("ca");// 注意与服务端的JSON key要一一对应！
				}
				// 深睡眠（小时）
				String ssm = "0";
				// 浅睡眠（小时）
				String qsm = "0";
				if(resultJSONStr != null)
				{
					JSONObject nwObj = JSONObject.parseObject(resultJSONStr);
					ssm = nwObj.getString("ssm");// 注意与服务端的JSON key要一一对应！
					qsm = nwObj.getString("qsm");// 注意与服务端的JSON key要一一对应！
				}

				showActiveForSleep(ydzjl, ydzbs, ca, ssm, qsm);
//				break;
			}
		}
		
		/**
		 * 显示“睡眠”相关的活动数据.
		 */
		public void showActiveForSleep(String ydzjl, String ydzbs, String ca, String ssm, String qsm)
		{
			viewLeftTop.setText(parentActivity.getString(R.string.report_steps), ydzbs, parentActivity.getString(R.string.unit_step), R.color.report_active_text_step_color);
			if(MyApplication.getInstance(parentActivity).getUNIT_TYPE().equals("Imperial")){
				
				viewRightTop.setText(parentActivity.getString(R.string.report_distance), ToolKits.MChangetoMIRate(Integer.parseInt(ydzjl))+"", parentActivity.getString(R.string.unit_miles), R.color.report_active_text_distance_color);
			
			}else{
				
				viewRightTop.setText(parentActivity.getString(R.string.report_distance), (Integer.parseInt(ydzjl))+"", parentActivity.getString(R.string.unit_m), R.color.report_active_text_distance_color);
			
			}
			viewLeftBottom.setText(parentActivity.getString(R.string.report_calories), ca, parentActivity.getString(R.string.unit_cal_big), R.color.report_active_text_ca_color);
			viewRightBottm.setText(parentActivity.getString(R.string.report_sleep)
					, String.valueOf(CommonUtils.getScaledDoubleValue(CommonUtils.getDoubleValue(ssm)
							+ CommonUtils.getDoubleValue(qsm), 1))
					, parentActivity.getString(R.string.unit_short_hour), R.color.report_active_text_sleep_color);
		}
	}
	
	/**
	 * 标题上的数据周期过滤UI包装实现类.
	 */
	private static abstract class PeriodFilterUIWrapper
	{
		private Activity parentActivity = null;
		private RadioButton rbWeek = null;
		private RadioButton rbMonth = null;
		private RadioGroup rgPeriod = null;
		private int lastCheckedId = -1;
		
		public PeriodFilterUIWrapper(Activity parentActivity)
		{
			this.parentActivity = parentActivity;
			
			this.initViews();
		}
		
		private void initViews()
		{
			rgPeriod = (RadioGroup)parentActivity.findViewById(R.id.report_page_activity_circleviews_periodRg);
//			rbDay = (RadioButton)parentActivity.findViewById(R.id.report_page_activity_circleviews_dayRb);
			rbWeek = (RadioButton)parentActivity.findViewById(R.id.report_page_activity_circleviews_weekRb);
			rbMonth = (RadioButton)parentActivity.findViewById(R.id.report_page_activity_circleviews_monthRb);
			
			// 默认认选中的是日
			lastCheckedId = rbWeek.getId();//rbDay.getId();
			
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
			int type = PeriodSwitchType.week;
			if(rbWeek.isChecked())
				type = PeriodSwitchType.week;
			else
				type = PeriodSwitchType.month;
			return type;
		}
		
		protected abstract void onFilterChaged(int periodSwitchType);
	}
	
	/**
	 * 日期范围切换UI包装实现类（可切换周、月的时间范围）.
	 * 
	 * @author Jack Jiang, 2014-05-14
	 * @version 1.0
	 */
	private static abstract class TimeFilterUIWrapper
	{
		private Activity parentActivity = null;
		
		private Button btnLeft = null;
		private Button btnRight = null;
		private TextView viewTime = null;
		
		private DateSwitcher weekSwitcher = null;
		private DateSwitcher monthSwitcher = null;
		
		public TimeFilterUIWrapper(Activity parentActivity)
		{
			this.parentActivity = parentActivity;
			
			this.initViews();
			this.initListeners();
		}
		
		private void initViews()
		{
			btnLeft = (Button)parentActivity.findViewById(R.id.report_page_activity_circleviews_dataswitch_leftBtn);
			btnRight = (Button)parentActivity.findViewById(R.id.report_page_activity_circleviews_dataswitch_rightBtn);
			viewTime = (TextView)parentActivity.findViewById(R.id.report_page_activity_circleviews_dataswitch_dateView);
			
			weekSwitcher = new DateSwitcher(PeriodSwitchType.week);
			monthSwitcher = new DateSwitcher(PeriodSwitchType.month);
		}
		
		private void initListeners()
		{
			viewTime.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0)
				{
					getDateSwitcher().setBaseTime(new Date());
					switchedOver();
				}
			});
			
			btnLeft.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v)
				{
					getDateSwitcher().previous();
					switchedOver();
				}
			});
			
			btnRight.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v)
				{
					getDateSwitcher().next();
					switchedOver();
				}
			});
		}
		
		public void refreshShowText()
		{
			int pst = getPeriodSwitchType();
			if(pst == PeriodSwitchType.month)
				viewTime.setText(new SimpleDateFormat("yyyy/MM").format(getDateSwitcher().getStartDate()));
			else
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
				DateSwitcher ds = getDateSwitcher();
				/**
				 * 只用于数据展示ToolKits.getDayFromDate(ds.getEndDate(), -1))
				 */
				viewTime.setText(sdf.format(ds.getStartDate())+" ~ "+sdf.format(ToolKits.getDayFromDate(ds.getEndDate(), -1)));
			}
		}
		
		private void switchedOver()
		{
			refreshShowText();
			onFilterChaged();
		}
		
		public DateSwitcher getDateSwitcher()
		{
			int pst = getPeriodSwitchType();
			if(pst == PeriodSwitchType.week)
				return weekSwitcher;
			else
				return monthSwitcher;
		}
		
		protected abstract void onFilterChaged();
		protected abstract int getPeriodSwitchType();
	}
	
	/**
	 * 活动类型切换Wrapper实现类.
	 */
	private static abstract class ActiveFilterUIWrapper
	{
		private Activity parentActivity = null;
		
		private RadioButton rbStep = null;
		private RadioButton rbDistance = null;
		private RadioButton rbSleep = null;
		private RadioButton rbCa = null;
		
		private RadioGroup rgSwitch = null;
		private int lastCheckedId = -1;
		
		public ActiveFilterUIWrapper(Activity parentActivity)
		{
			this.parentActivity = parentActivity;
			
			this.initViews();
		}
		
		private void initViews()
		{
			rgSwitch = (RadioGroup)parentActivity.findViewById(R.id.report_page_activity_circleviews_activefilter_switchRg);
			rbStep = (RadioButton)parentActivity.findViewById(R.id.report_page_activity_circleviews_activefilter_workRb);
			rbDistance = (RadioButton)parentActivity.findViewById(R.id.report_page_activity_circleviews_activefilter_runRb);
			rbSleep = (RadioButton)parentActivity.findViewById(R.id.report_page_activity_circleviews_activefilter_sleepRb);
			rbCa = (RadioButton)parentActivity.findViewById(R.id.report_page_activity_circleviews_activefilter_caRb);
			
			// 默认认选中的是步数
			lastCheckedId = rbStep.getId();
			
			rgSwitch.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId)
				{
					if(lastCheckedId != checkedId)
					{
						onFilterChaged();
						lastCheckedId = checkedId;
					}
				}
			});
		}
		
		public int getActiveSwitchType()
		{
			int type = ActiveSwitchType.step;
			if(rbStep.isChecked())
				type = ActiveSwitchType.step;
			else if(rbDistance.isChecked())
				type = ActiveSwitchType.distance;
			else if(rbSleep.isChecked())
				type = ActiveSwitchType.sleep;
			else
				type = ActiveSwitchType.ca;
			return type;
		}
		
		protected abstract void onFilterChaged();
	}
	
	//------------------------------------------------------------------ 一键分享相关代码
	private void showShare(View v, String imgPath)
	{
		
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
			String filePath = "/sdcard/report_v.png";

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
}
