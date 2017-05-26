//package com.linkloving.rtring_c_watch.logic;
//
//import java.util.Calendar;
//import java.util.Locale;
//import java.util.TimeZone;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.AdapterView;
//import android.widget.Button;
//
//import com.eva.android.platf.std.DataLoadableActivity;
//import com.eva.epc.core.dto.DataFromServer;
//import com.linkloving.rtring_c_watch.R;
//import com.tyczj.extendedcalendarview.Day;
//import com.tyczj.extendedcalendarview.ExtendedCalendarView;
//import com.tyczj.extendedcalendarview.ExtendedCalendarView.OnDayClickListener;
//
//public class DatePickerActivity extends DataLoadableActivity
//{
//	private ExtendedCalendarView calendarView = null;
//
//	public static String KEY_RESULT_DATE = "date";
//	public static String KEY_CURRENT_TIME = "current_time";
//
//	private long time;
//
//	private Button back;
//
//	@Override
//	protected void initViews()
//	{
//		customeTitleBarResId = R.id.activity_date_picker_title_bar;
//		setContentView(R.layout.activity_date_picker);
//		calendarView = (ExtendedCalendarView) findViewById(R.id.calendar);
//		back = (Button) findViewById(R.id.back_today);
//		calendarView.setGesture(ExtendedCalendarView.LEFT_RIGHT_GESTURE);
//		time = getIntent().getExtras().getLong(KEY_CURRENT_TIME);
//		if (time > 0)
//		{
//			calendarView.go2Day(time);
//		}
//
//		Calendar d = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
//		Calendar cal = Calendar.getInstance();
//		cal.setTimeInMillis(time);
//		if (d.get(Calendar.YEAR) == cal.get(Calendar.YEAR) && d.get(Calendar.MONTH) == cal.get(Calendar.MONTH)
//				&& d.get(Calendar.DAY_OF_MONTH) == cal.get(Calendar.DAY_OF_MONTH))
//		{
//			back.setVisibility(View.INVISIBLE);
//		}
//		else
//		{
//			back.setVisibility(View.VISIBLE);
//		}
//
//		back.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View arg0)
//			{
//				calendarView.back2Today();
//				Calendar calendar = Calendar.getInstance();
//				calendar.setTimeInMillis(System.currentTimeMillis());
//				Intent intent = new Intent();
//				intent.putExtra(KEY_RESULT_DATE, calendar.getTime());
//				setResult(Activity.RESULT_OK, intent);
//				finish();
//			}
//		});
//
//		calendarView.setOnDayClickListener(new OnDayClickListener()
//		{
//			@Override
//			public void onDayClicked(AdapterView<?> adapter, View view, int position, long id, Day day)
//			{
//				Calendar calendar = Calendar.getInstance();
//				calendar.set(day.getYear(), day.getMonth(), day.getDay());
//				Intent intent = new Intent();
//				intent.putExtra(KEY_RESULT_DATE, calendar.getTime());
//				setResult(Activity.RESULT_OK, intent);
//				finish();
//			}
//		});
//
//		// 如果是今天，则回到今天的按钮就不需要出来
//		// if(com.linkloving.rtring_c.utils.ToolKits.isToday(System.currentTimeMillis()))
//		// back.setVisibility(View.GONE);
//		// else
//		// back.setVisibility(View.VISIBLE);
//
//		this.setLoadDataOnCreate(false);
//		setTitle(R.string.date_picker_activity_title);
//	}
//
//	@Override
//	protected DataFromServer queryData(String... arg0)
//	{
//		return null;
//	}
//
//	@Override
//	protected void refreshToView(Object arg0)
//	{
//
//	}
//}
