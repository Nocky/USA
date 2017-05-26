package com.linkloving.rtring_c_watch.logic.main.mainfragmentimpl;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.eva.android.widget.util.WidgetUtils;
import com.eva.android.widget.util.WidgetUtils.ToastType;
import com.eva.epc.common.util.CalendarHelper;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.utils.DateSwitcher;
import com.linkloving.rtring_c_watch.utils.DateSwitcher.PeriodSwitchType;
import com.linkloving.rtring_c_watch.utils.ToolKits;

/**
 * 日期范围切换UI包装实现类.
 * 
 * @author Jack Jiang, 2014-05-14
 * @version 1.0
 */
public abstract class TimeFilterUIWrapper
{
	private final static String TAG = TimeFilterUIWrapper.class.getSimpleName();
	
	private Activity parentActivity = null;
	private View parentView = null;
	private Button btnLeft = null;
	private Button btnRight = null;
	private TextView viewTime = null;
	private DateSwitcher daySwitcher = null;
	private ViewGroup layoutOfTime = null;

	public TimeFilterUIWrapper(Activity parentActivity, View parentView)
	{
		this.parentActivity = parentActivity;
		this.parentView = parentView;

		this.initViews();
		this.initListeners();
	}

	private void initViews()
	{
		btnLeft = (Button)parentView.findViewById(R.id.report_page_activity_circleviews_dataswitch_leftBtn);
		btnRight = (Button)parentView.findViewById(R.id.report_page_activity_circleviews_dataswitch_rightBtn);
		viewTime = (TextView)parentView.findViewById(R.id.report_page_activity_circleviews_dataswitch_dateView);
		layoutOfTime = (ViewGroup)parentView.findViewById(R.id.report_page_activity_circleviews_dataswitchLL);
        //给dateswitcher绑定一个日的类型
		daySwitcher = new DateSwitcher(PeriodSwitchType.day){
			@Override
			protected void init()
			{
				switch(this.type)
				{
				case PeriodSwitchType.day:
					// 日类型时，默认时间为当天
					base = new GregorianCalendar();
					// base.add(GregorianCalendar.DAY_OF_MONTH, -1);
					break;
				default:
					Log.e(TAG, "当前日期切换组件只支持到了年月日的切换！");
					break;
				}
			}

			/**
			 * 日志切换检查. 不能切换超过今天
			 * 
			 * @return true表示检查通过，允许切换到新日期，否则不允许切换
			 */
			@Override
			protected boolean switchToNextCheck()
			{
				if(CalendarHelper.isToday(base.getTimeInMillis()))
				{
					WidgetUtils.showToast(parentActivity, ToolKits.getStringbyId(parentActivity, R.string.portal_main_waiting_tomorrow), ToastType.INFO);
					return false;
				}
				return true;
			}
		};
	}

	private void initListeners()
	{
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

		layoutOfTime.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) 
			{
				onViewTimeClick();
			}
		});
	}

	protected void refreshShowText()
	{
		String dateStr = new SimpleDateFormat("yyyy/MM/dd").format(getDateSwitcher().getStartDate());
		if(CalendarHelper.isToday(getDateSwitcher().getStartDate().getTime()))
			viewTime.setText(ToolKits.getStringbyId(parentActivity, R.string.portal_main_today)+" "+dateStr);
		else
			viewTime.setText(dateStr);
	}

	public void switchedOver()
	{
		refreshShowText();
		onFilterChaged();
	}

	public DateSwitcher getDateSwitcher()
	{
		return daySwitcher;
	}

	protected abstract void onViewTimeClick();
	/**
	 * 当内部时间切换后   外部调用并 改变自己是数据
	 */
	protected abstract void onFilterChaged();
	
	protected abstract int getPeriodSwitchType();
}