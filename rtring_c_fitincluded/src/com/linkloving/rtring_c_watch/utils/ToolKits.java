package com.linkloving.rtring_c_watch.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ParseException;
import android.net.Uri;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eva.android.widget.AsyncBitmapLoader;
import com.eva.android.widget.AsyncBitmapLoader.ImageCallBack;
import com.eva.epc.common.util.CommonUtils;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.utils.TimeZoneHelper;
//import com.linkloving.rtring_c.logic.reportday.util.TimeUtil;
//import com.linkloving.rtring_c.utils.sleep.BRDetailData;
//import com.linkloving.rtring_c.utils.sleep.DetailChartCountData;
//import com.linkloving.rtring_c.utils.sleep.SleepAlgorithmHelper;
//import com.rtring.buiness.logic.ends.sleep2.SleepAnalyzer;

public class ToolKits
{
private final static String TAG = ToolKits.class.getSimpleName();
	
	public final static String DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
	public final static String DATE_FORMAT_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
	public final static String DATE_FORMAT_YYYY_MM_DD = "yyyy-MM-dd";
	public final static String DATE_FORMAT_MM_DD = "MM-dd";
	
    public final static String DATE_FORMAT_HH_MM = "HH:mm";
    
    
    
    public final static double UNIT_METER_TO_MILES = 0.0006214;
    
    public final static double UNIT_LBS_TO_KG =  0.4535924;
    
   
    public final static double UNIT_INCHES_TO_CM = 2.54;
	
	public static String convertTimeWithPartten(long time, String partten)
	{
		long utctime = time - TimeZoneHelper.getTimeZoneOffsetMills();
		Date date = new Date(utctime);
		SimpleDateFormat sdf = new SimpleDateFormat("HH");
		if(Integer.parseInt(sdf.format(date))>12){
			SimpleDateFormat sdf_after = new SimpleDateFormat("hh:mm");
			return sdf_after.format(date)+" PM";
		}else{ 
			// 早晨  
			SimpleDateFormat sdf_morning = new SimpleDateFormat("hh:mm");
			return sdf_morning.format(date)+" AM";
		}
	}
	
	
//	public static double calcMeter2Miles(int meter)
//	{
//		return CommonUtils.getScaledDoubleValue(meter * UNIT_METER_TO_MILES, 4);
//	}
//	
	public static int calcLBS2KG(int lbs)
	{
		return (int)Math.rint(lbs * UNIT_LBS_TO_KG);
	}
//	
//	
	public static int calcInches2CM(int inches)
	{
		return (int)Math.rint(inches * UNIT_INCHES_TO_CM);
	}
	
	public static void main(String[] args)
	{
		System.out.println(CommonUtils.getScaledDoubleValue(0.0006214, 5));
	}
	
//	public final static int ONE_DAY = 24*3600;
//	public final static int ONE_HOUR = 3600;
	
	//--------------------------------------------------------------- 业务相关实用方法 START
	//--------------------------------------------------------------- 业务相关实用方法 START
//	/**
//	 * 将一个集合中的所有数据的duration合计起来.
//	 * 
//	 * @param datas
//	 * @return 返回合计结果（单位：秒）
//	 */
//	public static long cascatedSportDataDuration(List<SportRecord> datas)
//	{
//		long duraionSum = 0;
//		
//		if(datas != null && datas.size() > 0)
//		{
//			for(SportRecord sr : datas)
//				duraionSum += CommonUtils.getIntValue(sr.getDuration());
//		}
//		return duraionSum;
//	}
//	
//	/**
//	 * 因计算睡眠时，是将睡眠原始数据转成DLPSportData对象后进行的，
//	 * 所以无法直接在计算完成后返回的就是修改state后的SportRecord数据。
//	 * 本方法的作用就是将计算完成后的DLPSportData状态回填到SportRecord中。
//	 * 
//	 * @return
//	 */
//	public static List<SportRecord> putSleepStateFromSleepResult(List<SportRecord> datasNoSleepState
//			, List<DLPSportData> datasHasSleepState)
//	{
////		System.out.println("【回填睡眠state DEBUG!!】datasNoSleepState.size()="+datasNoSleepState.size()
////				+" @ datasHasSleepState.size()="+datasHasSleepState.size()+"");
//		if(datasNoSleepState != null && datasHasSleepState != null)
//		{
//			if(datasNoSleepState.size() != datasHasSleepState.size())
//			{
//				System.out.println("【回填睡眠state出错了】datasNoSleepState.size()="+datasNoSleepState.size()
//						+" [!=] datasHasSleepState.size()="+datasHasSleepState.size()+"！");
//			}
//			else
//			{
//				for(int i = 0; i< datasNoSleepState.size();i++)
//				{
//					SportRecord sr = datasNoSleepState.get(i);
//					DLPSportData sd = datasHasSleepState.get(i);
////					System.out.println("=================sr.getstate="+sr.getState()+", sd.getState()="+sd.getState());
//					if(sr != null && sd != null)
//					{
//						// 回填睡眠状态
//						sr.setState(String.valueOf(sd.getState()));
////						datasNoSleepState.set(i, sr);
//					}
//				}
//			}
//		}
//		else
//		{
//			System.out.println("【回填睡眠state出错了】datasNoSleepState="+datasNoSleepState
//					+",datasHasSleepState="+datasHasSleepState+"！");
//		}
//		
//		return datasNoSleepState;
//	}
//	
//	/**
//	 * 将日明细数据自动累加组装成对应日的合计形式.
//	 * 
//	 * @param originalSportDatas 一个start_time从小到大排序的原始运动数据集合.
//	 * @return
//	 */
//	public static List<DaySynopic> convertSportDatasToSynopics(List<SportRecord> originalSportDatas)
//	{
//		// 准备界面下方的汇总数据显示
//		List<DaySynopic> dsFromSportDatas = new ArrayList<DaySynopic>();
//		if(originalSportDatas != null && originalSportDatas.size() > 0)
//		{
//			String theDate = null;
//			int sleepSecond = 0,deepSleepSencond = 0;
//			int run_duration = 0, run_step = 0, run_distance = 0, work_duration = 0, work_step = 0, work_distance = 0;
//			for( int i = 0; i < originalSportDatas.size(); i++)
//			{
//				// 一行原始数据
//				SportRecord dayOriginal = originalSportDatas.get(i);
//				// 原始数据的状态
//				String state = dayOriginal.getState();
//				if(theDate == null)
//					// new day
//					theDate = dayOriginal.getLocalDate();
//				else
//				{
//					// 同一天
//					if(theDate.equals(dayOriginal.getLocalDate()))
//						;
//					// 下一天
//					else
//					{
//						DaySynopic ds = new DaySynopic();
//						ds.setData_date(theDate);
//						ds.setData_date2(theDate+" 00:00:00.000");
//						ds.setRun_duration(String.valueOf(run_duration));
//						ds.setRun_step(String.valueOf(run_step));
//						ds.setRun_distance(String.valueOf(run_distance));
//						ds.setWork_duration(String.valueOf(work_duration));
//						ds.setWork_step(String.valueOf(work_step));
//						ds.setWork_distance(String.valueOf(work_distance));
//						// 将睡眠时间转成分钟
//						ds.setSleepMinute(String.valueOf(CommonUtils.getScaledDoubleValue(sleepSecond/60.0, 0)));
//						ds.setDeepSleepMiute(String.valueOf(CommonUtils.getScaledDoubleValue(deepSleepSencond/60.0, 0)));
//
//						// 该天的统计已经完成放到集合中
//						dsFromSportDatas.add(ds);
//
//						// reset
//						sleepSecond = 0;
//						deepSleepSencond = 0;
//						run_duration = 0;
//						run_step = 0;
//						run_distance = 0;
//						work_duration = 0;
//						work_step = 0;
//						work_distance = 0;
//						
//						// new day
//						theDate = dayOriginal.getLocalDate();
//					}
//				}
//				
////				System.out.println("AAAAAAAA>theDate="+theDate+", localDate="+dayOriginal.getLocalDate());
//
////				System.out.println(">>【日转月计算时state===】"+state);
//				try
//				{
//					// 走路
//					if(String.valueOf(SleepAnalyzer.WALKING).equals(state))
//					{
//						work_duration = work_duration + CommonUtils.getIntValue(dayOriginal.getDuration());
//						work_step = work_step + CommonUtils.getIntValue(dayOriginal.getStep());
//						work_distance = work_distance + CommonUtils.getIntValue(dayOriginal.getDistance());
//					}
//					// 跑步
//					else if(String.valueOf(SleepAnalyzer.RUNNING).equals(state))
//					{
//						run_duration = run_duration + CommonUtils.getIntValue(dayOriginal.getDuration());
//						run_step = run_step + CommonUtils.getIntValue(dayOriginal.getStep());
//						run_distance = run_distance + CommonUtils.getIntValue(dayOriginal.getDistance());
//					}
//					else if(String.valueOf(SleepAnalyzer.LIGHT_SLEEP).equals(state))
//						sleepSecond = sleepSecond + CommonUtils.getIntValue(dayOriginal.getDuration());
//					else if(String.valueOf(SleepAnalyzer.DEEP_SLEEP).equals(state))
//						deepSleepSencond = deepSleepSencond + CommonUtils.getIntValue(dayOriginal.getDuration());
//				}
//				catch (Exception e)
//				{
//					System.out.println(">>【日转月计算时异常】"+e.getMessage());
//				}
//
//				// 到了最后一行，也就没有下一天了，直接加进去（否则就漏掉了）
//				if(i == originalSportDatas.size() - 1)
//				{
//					DaySynopic ds = new DaySynopic();
//					ds.setData_date(theDate);
//					ds.setData_date2(theDate+" 00:00:00.000");
//					ds.setRun_duration(String.valueOf(run_duration));
//					ds.setRun_step(String.valueOf(run_step));
//					ds.setRun_distance(String.valueOf(run_distance));
//					ds.setWork_duration(String.valueOf(work_duration));
//					ds.setWork_step(String.valueOf(work_step));
//					ds.setWork_distance(String.valueOf(work_distance));
//					// 将睡眠时间转成分钟
//					ds.setSleepMinute(String.valueOf(sleepSecond/60));
//					ds.setDeepSleepMiute(String.valueOf(deepSleepSencond/60));
//
//					// 该天的统计已经完成放到集合中
//					dsFromSportDatas.add(ds);
//
//					// reset
//					sleepSecond = 0;
//					deepSleepSencond = 0;
//					run_duration = 0;
//					run_step = 0;
//					run_distance = 0;
//					work_duration = 0;
//					work_step = 0;
//					work_distance = 0;
//					
////					// new day
////					theDate = dayOriginal.getLocalDate();
//				}
//			}
//		}
//
//		System.out.println(">>【日转月计算结果】共有"+dsFromSportDatas.size()+"日：");
//		for(DaySynopic ss : dsFromSportDatas)
//		{
//			System.out.println(">>【日转月计算结果】"+ss.toString());
//		}
//		
//		return dsFromSportDatas;
//	}
//	
//	/**
//	 * 从设备中读取汇总数据时，据设备中存放的utc时间戳和带时区的日时间来推定汇总数据所属时区，
//	 * 并计算出此带时区的汇总数据时间.总之，为了在跨时区时保证汇总数据的正确性，最大可能保证使用
//	 * 标准UTC时间推定出的日期。
//	 * 
//	 * @param deviceTimeStamp
//	 * @param deviceDayTime
//	 * @return 推定出的设备中带时区时间戳（单位：秒）
//	 */
//	public static long calcDeviceToday(long deviceTimeStamp, long deviceDayTime)
//	{
//		long today = deviceTimeStamp;
//		long time = today % ONE_DAY;
//
//		long Delta_T = deviceDayTime - time;
//		if (Delta_T > 12 * ONE_HOUR)
//			Delta_T -= 24 * ONE_HOUR;
//		else if (Delta_T < -12 * ONE_HOUR)
//			Delta_T += 24 * ONE_HOUR;
//
//		today = today + Delta_T;
//		long T1 = (today / ONE_DAY);
//		today = (T1 * ONE_DAY) + 1;
//		return today;
//	}
	
	/**
	 * 网络是否可传输数据.
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkConnected(Context context)
	{   
		return MyApplication.getInstance(context).isLocalDeviceNetworkOk();
//        ConnectivityManager cm = (ConnectivityManager) context   
//                .getSystemService(Context.CONNECTIVITY_SERVICE);   
//        if (cm == null) 
//        {   
//        } 
//        else 
//        {
//        	//如果仅仅是用来判断网络连接
//        	//则可以使用 cm.getActiveNetworkInfo().isAvailable();  
//        	return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();  
////            NetworkInfo[] info = cm.getAllNetworkInfo();   
////            if (info != null)
////            {   
////                for (int i = 0; i < info.length; i++) 
////                {   
////                    if (info[i].getState() == NetworkInfo.State.CONNECTED) 
////                        return true;   
////                }   
////            }   
//        }   
//        return false;   
    }
	//--------------------------------------------------------------- 业务相关实用方法 END
	//--------------------------------------------------------------- 业务相关实用方法 END

//	/**
//	 * 指定时间戳是否是今天（只需到年月日即可）.
//	 * 
//	 * @param theTimestamp
//	 * @return
//	 */
//	public static boolean isToday(long theTimestamp)
//	{
////		int currentDay = (int) (new Date().getTime() / 1000 / 60 / 60 / 24);
////		return ((int) (theTimestamp / 1000 / 60 / 60 / 24) == currentDay);
//		
//		GregorianCalendar gcCurrent = new GregorianCalendar();
//		gcCurrent.setTime(new Date());
//		
//		GregorianCalendar gcTheTime = new GregorianCalendar();
//		gcTheTime.setTimeInMillis(theTimestamp);
//		
//		return gcCurrent.get(GregorianCalendar.YEAR) == gcTheTime.get(GregorianCalendar.YEAR)
//				&& gcCurrent.get(GregorianCalendar.MONTH) == gcTheTime.get(GregorianCalendar.MONTH)
//				&& gcCurrent.get(GregorianCalendar.DAY_OF_MONTH) == gcTheTime.get(GregorianCalendar.DAY_OF_MONTH);
//	}

	public static boolean isJSONNullObj(String nullContent)
	{
		return "null".equals(nullContent);
	}

//	/**
//	 * 邮箱格式检查.
//	 * 
//	 * @param email
//	 * @return
//	 */
//	public static boolean isEmail(String email)
//	{
//		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
//		Pattern p = Pattern.compile(str);
//		Matcher m = p.matcher(email);
//
//		return m.matches();
//	}

	/**
	 * 将dip转换为像素.
	 */
	public static int dip2px(Context context, float dipValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	/**
	 * 将像素转换成dip.
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dip(Context context, float pxValue)
	{
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

//	/**
//	 * 通过年龄计算出生年，月日默认01-01
//	 * 
//	 * @param age
//	 * @return
//	 */
//	public static String getBirthdateByAge(int age)
//	{
//		Calendar mycalendar = Calendar.getInstance();// 获取现在时间
//		int year = mycalendar.get(Calendar.YEAR);// 获取年份
//		return (year - age) + "-01-01";
//	}
//
//	public static String getAgeByBirthdate(String birthdate, String dateFormatString)
//	{
//		Calendar mycalendar = Calendar.getInstance();
//		DateFormat dd = new SimpleDateFormat(dateFormatString);
//		Date date = null;
//		try
//		{
//			date = dd.parse(birthdate);
//		}
//		catch (ParseException e)
//		{
//		}
//		int year = mycalendar.get(Calendar.YEAR);
//		mycalendar.setTime(date);
//		return (year - mycalendar.get(Calendar.YEAR)) + "";
//	}

	public static String int2String(int temp)
	{
		return (temp > 9 || temp < 0) ? "" + temp : "0" + temp;
	}

//	/**
//	 * 判断是否是数字
//	 */
//	public static boolean isNumeric(String str)
//	{
//		Pattern pattern = Pattern.compile("[0-9]*");
//		return pattern.matcher(str).matches();
//	}

	public static void showCommonTosat(Context context, boolean showRightOrWrong, String showString, int toastLength)
	{
		// 获取LayoutInflater对象，该对象能把XML文件转换为与之一直的View对象
		LayoutInflater inflater = LayoutInflater.from(context);
		// 根据指定的布局文件创建一个具有层级关系的View对象
		// 第二个参数为View对象的根节点，即LinearLayout的ID
		View layout = inflater.inflate(R.layout.common_toast, null);

		// 查找ImageView控件
		// 注意是在layout中查找
		TextView text = (TextView) layout.findViewById(R.id.common_toast_text);
		text.setText(showString);

		Drawable sexImg;
		int resId = showRightOrWrong ? R.drawable.common_toast_image_right : R.drawable.common_toast_image_wrong;
		sexImg = context.getResources().getDrawable(resId);
		sexImg.setBounds(0, 0, sexImg.getMinimumWidth(), sexImg.getMinimumHeight());
		text.setCompoundDrawables(null, sexImg, null, null);

		Toast toast = new Toast(context);
		// 设置Toast的位置
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setDuration(toastLength);
		// 让Toast显示为我们自定义的样子
		toast.setView(layout);
		toast.show();

	}

	/**
	 * 获取画图后显示的文字的长度
	 * 
	 * @param pFont
	 * @param text
	 * @return
	 */
	public static float getTextLength(TextPaint pFont, String text)
	{
		float textLength = pFont.measureText(text);
		return textLength;
	}

	public static Bitmap getBlueboothPowerLevel(float level, Activity activity)
	{
		return getBlueboothPowerLevel(level, activity, BitmapFactory.decodeResource(activity.getResources(), R.drawable.fullscreen_bt),
				BitmapFactory.decodeResource(activity.getResources(), R.drawable.fullscreen_bt_bg));
	}

	/**
	 * 此方法是丁维写的关于合成蓝牙电量的图标实用方法.
	 * 
	 * @param level
	 *            0~1.00f的浮点值表示蓝牙电量
	 * @param activity
	 * @return
	 */
	public static Bitmap getBlueboothPowerLevel(float level, Activity activity, Bitmap full, Bitmap fullbg)
	{
		if (full == null || fullbg == null || activity == null)
			return null;
		// Bitmap full = BitmapFactory.decodeResource(activity.getResources(),
		// R.drawable.fullscreen_bt);
		// Bitmap fullbg = BitmapFactory.decodeResource(activity.getResources(),
		// R.drawable.fullscreen_bt_bg);
		Bitmap output = Bitmap.createBitmap(full.getWidth(), full.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final Rect rect = new Rect(0, 0, (int) (full.getWidth() * level), full.getHeight());
		final RectF rectF = new RectF(rect);
		// final Rect rectbg = new Rect(0, 0, fullbg.getWidth(),
		// (int)((fullbg.getHeight())*(1.0f-level)));
//		Log.d("level", "level..................................................................." + level);

		final Rect rectbg = new Rect(0, 0, fullbg.getWidth(), fullbg.getHeight());
		final RectF rectFbg = new RectF(rectbg);
		canvas.drawBitmap(fullbg, rectbg, rectFbg, null);
		canvas.drawBitmap(full, rect, rectF, null);

		// 及时回 收内存
		if (full != null && !full.isRecycled())
			full.recycle();
		if (fullbg != null && !fullbg.isRecycled())
			fullbg.recycle();

		return output;
	}

//	/**
//	 * 将对象转换成int型数据。
//	 * 
//	 * @param ob
//	 * @return
//	 */
//	public static int getIntValue(Object ob)
//	{
//		try
//		{
//			return getIntValue(ob, 0);
//		}
//		catch (Exception e)
//		{
//			System.err.println(e.getMessage());
//			return 0;
//		}
//	}
//
//	public static int getIntValue(Object obj, int defaultVal) throws Exception
//	{
//		String value = String.valueOf(obj);
//		return (obj == null || CommonUtils.isStringEmpty(value, true) ? defaultVal : Integer.parseInt(value));
//	}

//	/**
//	 * 数据类型转换.
//	 * 
//	 * @param s
//	 * @return
//	 */
//	public static double getDoubleValue(String s)
//	{
//		try
//		{
//			return getDoubleValue(s, 0.0);
//		}
//		catch (Exception e)
//		{
//			System.err.println(e.getMessage());
//			return 0.0;
//		}
//	}
//
//	public static double getDoubleValue(Object obj, double defaultVal) throws Exception
//	{
//		String value = String.valueOf(obj);
//		return (obj == null || CommonUtils.isStringEmpty(value, true)) ? defaultVal : Double.parseDouble(value);
//	}

//	/**
//	 * 数据类型转换.
//	 * 
//	 * @param s
//	 * @return
//	 */
//	public static float getFloatValue(String s)
//	{
//		try
//		{
//			return getFloatValue(s, 0.0f);
//		}
//		catch (Exception e)
//		{
//			System.err.println(e.getMessage());
//			return 0.0f;
//		}
//	}
//
//	public static float getFloatValue(Object obj, float defaultVal) throws Exception
//	{
//		String value = String.valueOf(obj);
//		return (obj == null || CommonUtils.isStringEmpty(value, true)) ? defaultVal : Float.parseFloat(value);
//	}

	/**
	 * 计算BMI指数
	 */
	public static double getBMI(float weight, int height)
	{
//		DecimalFormat r = new DecimalFormat();
//		r.applyPattern("#0.0");// 保留小数位数，不足会补零
		//西班牙语闪退
		BigDecimal   b   =   new   BigDecimal((weight * UNIT_LBS_TO_KG) / ((height * UNIT_INCHES_TO_CM / 100.0) * (height * UNIT_INCHES_TO_CM / 100.0)));  
		return b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
//		return new Double(r.format((weight * UNIT_LBS_TO_KG) / ((height * UNIT_INCHES_TO_CM / 100.0) * (height * UNIT_INCHES_TO_CM / 100.0)))).doubleValue();
	}

	/**
	 * 计算BMI指数描述
	 */
	public static String getBMIDesc(Context context,double bmi)
	{
		if (bmi < 18.5)
			return getStringbyId(context, R.string.body_info_slim);
		if (bmi >= 18.5 && bmi <= 24.9)
			return getStringbyId(context, R.string.body_info_normal);
		if (bmi > 25 && bmi <= 29.9 )
			return getStringbyId(context, R.string.body_info_overweight);
		if (bmi >= 30.0 && bmi<=34.9)
			return getStringbyId(context, R.string.body_info_mildly_obese);
		if (bmi >= 35 && bmi<=39.9)
			return getStringbyId(context, R.string.body_info_moderately_obese);
		if (bmi >= 40.0)
			return getStringbyId(context, R.string.body_info_severe_obesity);
		return getStringbyId(context, R.string.body_info_normal);
	}
	
	public static double CMChangetoINRate(int cm)
	{
//		DecimalFormat r = new DecimalFormat();
//		r.applyPattern("#0.0");// 保留小数位数，不足会补零
//		return new Double(r.format(cm * 0.3937008)).doubleValue();
		BigDecimal   b   =   new   BigDecimal(cm * 0.3937008);  
		return b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
		public static Integer INRateChangetoCM(int cm)
		{
			return (int) (cm / 0.3937008);
		}
	
	public static Integer LBRateChangetoKG(int cm)
	{
		return (int) (cm / 2.2046226);
	}
	/**
	 * @param 米
	 * @return 公制/英制
	 */
	public static double MChangetoMIRate (int m)
	{
//		DecimalFormat r = new DecimalFormat();
//		r.applyPattern("#0.0");// 保留小数位数，不足会补零
//		Log.e(TAG, r.format(m * 0.0006214)+"......................");
//		Log.e(TAG, new Double(r.format(m * 0.0006214)).doubleValue()+"......................");
		BigDecimal   b   =   new   BigDecimal(m * 0.0006214);  
//		return new Double(r.format(m * 0.0006214)).doubleValue();
		return b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	
	/**
	 * 加载企业用户文件
	 */
	public static void loadEntFileImage(Activity context, ImageView view, String user_id, String fileName, int width, int height)
	{
		AsyncBitmapLoader asyncLoader = new AsyncBitmapLoader(EntHelper.getEntFileSavedDir(context) + "/");
		Bitmap bitmap = null;
		if(!CommonUtils.isStringEmpty(fileName, true))
		{
			if(bitmap != null && !bitmap.isRecycled()){
				bitmap.recycle();
			}
			//根据图片URL去查找内存缓存有没有对应的Bitmap对象，并传递回调方法，如果没有，则等下载完毕回调  
			bitmap = asyncLoader.loadBitmap(view   
					// 注意：此下载地址在服务端的逻加上是不管客户有无缓存都会传过来头像文件数据的，但在此处却不需要顾及它，
					// 因为此处使用的缓存逻辑是独立的：它先判断本地用无该文件，如果没有的时候才会用此URL取头像数据，所以此
					// URL要一定能取的到头像数据就对了
					, EntHelper.getEntDownloadURL(context, user_id, fileName) 
					, fileName//, rowData.getUserAvatarFileName()
					, new ImageCallBack()  
					{  
						@Override  
						public void imageLoad(ImageView imageView, Bitmap bitmap)  
						{  
//						Log.w(GoodDetailActivity.class.getSimpleName(), "【MALL】See here!!!!!!!!!!!!!!!!!!!!!"+bitmap.getWidth());
							if(imageView != null){
								imageView.setImageBitmap(bitmap);  
							}
								
							// ## 非常奇怪的一个问题：当网络下载的图片完成时会回调至此，但图片数据明
							// ## 明有了却不能刷新显示之，目前为了它能显示就低效地notifyDataSetChanged
							// ## 一下吧，以后看看什么方法可以单独刷新（否则每一次都得刷新所有可见区），
							// ## 有可能是android的listview机制性问题
						}  
					}
					// 指定生成的Bitmap对象所描述的图片的大小(更小的尺寸将成倍地减小内存消耗)
					, width, height// 此头像用于好友列表中的好友头像，大小参考：@drawable/head_bg_hole.png
			);  

			if(bitmap != null && view != null){
				view.setImageBitmap(bitmap);
			}
				
		}
		else
		{
			if(bitmap != null && !bitmap.isRecycled()){
				bitmap.recycle();
			}
			if(view!=null){
				Resources res = context.getResources();  
				bitmap = BitmapFactory.decodeResource(res, R.drawable.main_fragment_little_logo);  
			    view.setImageBitmap(bitmap);
			}
				
		}
	}
	
	/**
	 * 分享功能
	 */
	public static void shareContent(Context context, String activityTitle, String msgTitle, String text, String imgPath)
	{
		Intent intent = new Intent(Intent.ACTION_SEND);
		if (CommonUtils.isStringEmpty(imgPath))
		{
			intent.setType("text/plain"); // 纯文本
		}
		else
		{
			File f = new File(imgPath);
			if (f != null && f.exists() && f.isFile())
			{
				intent.setType("image/*");
				Uri u = Uri.fromFile(f);
				intent.putExtra(Intent.EXTRA_STREAM, u);
			}
		}
		intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
		intent.putExtra(Intent.EXTRA_TEXT, text);
		context.startActivity(Intent.createChooser(intent, activityTitle));

	}
	
	
	public static String getStringbyId(Context context,int id)
	{
		if(context==null){
			return "";
		}
		return context.getResources().getString(id);
	}

	public static void getScreenHot(View v, String filePath)
	{
		View view = v.getRootView();
		view.setDrawingCacheEnabled(true);
		try
		{
			view.buildDrawingCache();
			Bitmap bitmap = view.getDrawingCache();
			if (bitmap != null)
			{
			
					FileOutputStream out = new FileOutputStream(filePath);
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			
			}
			else
			{
				System.out.println("bitmap is NULL!");
			}
		}
		catch (Exception e)
		{
//			e.printStackTrace();
			Log.w(TAG, e.getMessage(), e);
		}
	}
	
	/**
	 * 获取根据当前时间与index天的日期
	 * index 为负数时则为过去的天数，例：-1.则表示 date时间的以前一天，反之为后一天，0则返回date
	 * @param date
	 * @param index
	 * @return
	 */
	public static Date getDayFromDate(Date date, int index)
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, index);
		date = calendar.getTime();
		return date;
	}
	
	public static Drawable getRepetDrawable(Context context,int res)
	{
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),res);
    	BitmapDrawable drawable = new BitmapDrawable(bitmap);
    	drawable.setTileModeXY(TileMode.REPEAT , TileMode.REPEAT );
    	drawable.setDither(true);
    	return drawable;
	}
	
	//隐藏虚拟键盘
    public static void HideKeyboard(View v)
    {
        InputMethodManager imm = ( InputMethodManager ) v.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );     
        if ( imm.isActive( ) )
        {     
            imm.hideSoftInputFromWindow( v.getApplicationWindowToken( ) , 0 );   
        }    
    }
    
  //显示虚拟键盘
    public static void ShowKeyboard(View v)
    {
          InputMethodManager imm = ( InputMethodManager ) v.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );     
         imm.showSoftInput(v,InputMethodManager.SHOW_FORCED);    
    }
	
    
	public static final void showResultDialog(Context context, String msg, String title)
	{
		if (msg == null)
			return;
		String rmsg = msg.replace(",", "\n");
		Log.d("Util", rmsg);
		new AlertDialog.Builder(context).setTitle(title).setMessage(rmsg).setNegativeButton("知道了", null).create().show();
	}
	
//	/**
//	 * 统计一段时期内运动数据
//	 * @param dlpList
//	 * @param begin
//	 * @param end
//	 * @return
//	 */
//	public static DetailChartCountData countSportData(List<DLPSportData> dlpList,String begin,String end) throws ParseException
//	{
//		Date startDate = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).parse(begin);
//		Date endDate = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).parse(end);
//		
//		return countSportData(dlpList,(int)(startDate.getTime()/1000),(int)(endDate.getTime()/1000));
//	}
//	
//	public static DetailChartCountData countSportData(List<DLPSportData> dlpList,String begin) throws ParseException
//	{
//		Date startDate = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).parse(begin);
//		Date endDate = TimeUtil.afterDate(startDate, 1);
//		
//		return countSportData(dlpList,(int)(startDate.getTime()/1000),(int)(endDate.getTime()/1000));
//	}
//	
//	/**
//	 * 统计一段时期内运动数据
//	 * @param dlpList
//	 * @param begin
//	 * @param end
//	 * @return
//	 */
//	public static DetailChartCountData countSportData(List<DLPSportData> dlpList,int begin,int end)
//	{
//		List<BRDetailData> src = new ArrayList<BRDetailData>();
//		DetailChartCountData count = new DetailChartCountData();
//		TimeZone t = TimeZone.getDefault();
//		int offset = t.getRawOffset();
//		int beg = (int)((begin + offset/1000) / 30L);
//		int en = (int)((end + offset/1000) / 30L);
//        int  dayIndex =  beg / 2880;
//        int endIndex = en/2880;
//        
//        for(DLPSportData data:dlpList)
//        {
//        	src.add(new BRDetailData(data));
//        }
//        for(int i = dayIndex;i < endIndex;i++)
//        {
//        	SleepAlgorithmHelper.countSportData(dayIndex, src,count);
//        }
//        return count;
//	}
	
	/**
	 * 获取未度数字符串，大于99时显示为99+
	 * @param num
	 * @return
	 */
    public static String getUnreadString(int num)
    {
    	String tmp;
		if(num > 99)
		{
			tmp = "99+";
		}
		else
		{
			tmp = num + "";
		}
		return tmp;
    }
    
    
	// string类型转换为long类型
 	// strTime要转换的String类型的时间
 	// formatType时间格式
 	// strTime的时间格式和formatType的时间格式必须相同
 	public static long stringToLong(String strTime, String formatType)
 			throws ParseException {
 		Date date = stringToDate(strTime, formatType); // String类型转成date类型
 		if (date == null) {
 			return 0;
 		} else {
 			long currentTime = date.getTime(); // date类型转成long类型
 			return currentTime;
 		}
 	}
 	// string类型转换为date类型
  	// strTime要转换的string类型的时间，formatType要转换的格式yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日
  	// HH时mm分ss秒，
  	// strTime的时间格式必须要与formatType的时间格式相同
  	private static Date stringToDate(String strTime, String formatType) throws ParseException {
  		SimpleDateFormat formatter = new SimpleDateFormat(formatType);
  		Date date = null;
  		try {
			date = formatter.parse(strTime);
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
  		return date;
  	}
}
