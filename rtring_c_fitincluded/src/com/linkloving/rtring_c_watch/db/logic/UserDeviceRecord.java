package com.linkloving.rtring_c_watch.db.logic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Observer;
import java.util.TimeZone;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.linkloving.band.dto.SportRecord;
import com.linkloving.band.sleep.SleepDataHelper;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.db.TableRoot;
import com.linkloving.rtring_c_watch.utils.HttpHelper;
import com.linkloving.utils.TimeZoneHelper;
import com.linkloving.utils._Utils;

/**
 * 设备日明细数据表历史记录的辅助操作实现类.
 * 
 * @author Jack Jiang, 2014-03-21
 * @since 2.5
 */
public class UserDeviceRecord extends TableRoot
{
	private final static String TAG = UserDeviceRecord.class.getSimpleName();
	
	/** 存放于sqlLite数据库中的表格字段名：自增id（主键）（默认ident列，无需插入数据）*/
	private static final String COLUMN_KEY_ID="_record_id";
	private static final String COLUMN_KEY_ACOUNT$UID="_user_id";
	
	public static final String COLUMN_DEVICE_ID="device_id";
	public static final String COLUMN_START_TIME="start_time";
	public static final String COLUMN_STATE="state";
	public static final String COLUMN_STEP="step";
	public static final String COLUMN_DURATION="duration";
	public static final String COLUMN_DISTANCE="distance";
	
	public static final String COLUMN_SYNC_TO_SERVER="sync_to_server";// 0 表示此离线数据是由设备中读出且尚未成功同步到服务端，1 表示已同步到服务端的数据
	
	public static final String COLUMN_CREATE_TIME="create_time";
	public static final String COLUMN_KEY_UPDATE$TIME="_update_time";
	
	/** 存放于sqlLite数据库中的表名 */
	public static final String TABLE_NAME="user_device_record";
	
	/** 建表语句 */
	public static final String DB_CREATE="CREATE TABLE "
										+TABLE_NAME+" ( "	
										+COLUMN_KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
										+COLUMN_KEY_ACOUNT$UID+" INTEGER,"
										+COLUMN_DEVICE_ID+" INTEGER,"
										+COLUMN_START_TIME+" TEXT,"
										+COLUMN_STATE+" INTEGER,"
										+COLUMN_STEP+" INTEGER,"
										+COLUMN_DURATION+" INTEGER,"
										+COLUMN_DISTANCE+" INTEGER,"
										+COLUMN_SYNC_TO_SERVER+" INTEGER," 
										+COLUMN_CREATE_TIME+" TIMESTAMP default (datetime('now', 'localtime')),"
//										--, _update_date TEXT default datetime(CURRENT_TIMESTAMP)
										// date('now')或CURRENT_TIMESTAMP 时间是以格林尼治标准时间为基准的，因此在中国使用的话会正好早8个小时
										+COLUMN_KEY_UPDATE$TIME+" TIMESTAMP default (datetime('now', 'localtime'))"
										+")";
	//singleton
	private static UserDeviceRecord instance;
	
	private UserDeviceRecord(Context context)
	{
		super(context);
	}
	
	public static UserDeviceRecord getInstance(Context context)
	{
		if(instance==null)
			// FIX BUG: 以下context不能直接传Activity及其子类的句柄，否则将发生内存泄漏！
			// Application为全局唯一，所以不存在不释放的问题！
			instance=new UserDeviceRecord(MyApplication.getInstance(context));
		return instance;		
	}
	
	/**
	 * 从本地sqlLite的产品信息表中查询所需数据.
	 * 
	 * @param acountUidOfOwner 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @param condition 查询条件
	 * @return 游标结果集
	 */
	private Cursor queryHistoryImpl(String acountUidOfOwner, String condition)
	{
		String where ="";
		if(condition.equals("sync_to_server=0 or sync_to_server=1")){
			//谷歌只查70条
			where = COLUMN_KEY_ACOUNT$UID+"='"+acountUidOfOwner+"'"+(condition == null?"":" and "+condition)+" order by "+COLUMN_START_TIME+" desc limit 0,500";
		}else{
			where = COLUMN_KEY_ACOUNT$UID+"='"+acountUidOfOwner+"'"+(condition == null?"":" and "+condition)+" order by "+COLUMN_START_TIME+" asc";
		}
		String clientTimezoneOffsetInMinute = String.valueOf(TimeZoneHelper.getTimeZoneOffsetMinute());
//		Log.d(TAG, "正在查询明细数据：条件> where="+where+", 时区偏移："+clientTimezoneOffsetInMinute);
		Log.d(TAG, "正在查询明细数据：条件> where="+where+COLUMN_KEY_ID+COLUMN_DEVICE_ID+COLUMN_START_TIME
				+ COLUMN_STATE+COLUMN_STEP+COLUMN_DURATION+COLUMN_DISTANCE+" strftime('%Y-%m-%d',datetime("+COLUMN_START_TIME+",'"+clientTimezoneOffsetInMinute+" minute'))");
//		time()
		return query(new String[]{
				COLUMN_KEY_ID
				,COLUMN_DEVICE_ID
				,COLUMN_START_TIME
				,COLUMN_STATE
				,COLUMN_STEP
				,COLUMN_DURATION
				,COLUMN_DISTANCE
				// 据本地时区计算出此条明细数据属于哪1天（带时间的天），方便app中使用（db中的函数处理日期肯定是性能和资源占用要好的多）
				,"strftime('%Y-%m-%d',datetime("+COLUMN_START_TIME+",'"+clientTimezoneOffsetInMinute+" minute'))"
//				,"strftime('%Y-%m-%d %H:%M:%S',"+COLUMN_START_TIME+",'"+clientTimezoneOffsetInMinute+" minute')"
				}, where);
	}
	/**
	 * 返回ArrayList<DaySynopic>记录.
	 * 
	 * @param acountUidOfOwner 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @return
	 */
	public synchronized ArrayList<SportRecord> findHistory(String acountUidOfOwner, String condition)
	{
		ArrayList<SportRecord> cpList= new ArrayList<SportRecord>();
		
		Cursor mCursor = null;
		try
		{
			//条件是
			mCursor = queryHistoryImpl(acountUidOfOwner, condition);//" 1=1 order by _update_time asc");// 取出的结果顺序的哦）
			mCursor.moveToFirst();
			while(!mCursor.isAfterLast())
			{
				try
				{
					SportRecord cp = new SportRecord();
					int j = 0;
					cp.setRecord_id(mCursor.getString(j++)); // 此record_id是本地sqlite生成的，用于update时
					cp.setDevice_id(mCursor.getString(j++));
					cp.setStart_time(mCursor.getString(j++));
					cp.setState(mCursor.getString(j++));
					cp.setStep(mCursor.getString(j++));
					cp.setDuration(mCursor.getString(j++));
					cp.setDistance(mCursor.getString(j++));
					cp.setLocalDate(mCursor.getString(j++));
					if(mCursor.isFirst()){
						Log.e(TAG, "第一次时间:"+cp.getStart_time());
						Log.e(TAG, "第一次步数:"+cp.getStep());
					}
//					if(mCursor.isLast()){
//						Log.e(TAG, "最后一次时间:"+cp.getStart_time());
//						Log.e(TAG, "最后一次步数:"+cp.getStep());
//					}
//					if(Integer.parseInt(cp.getState())==1 || Integer.parseInt(cp.getState())==2){
//						sum+= Integer.parseInt(cp.getStep());
//					}
					// ** 2015-01-15后，因新版睡眠算法里，时间重叠问题由算法自行解决，所以此处读取数据时
					// ** 就不需要额外判断了，从而节省性能，据测：12000条左右的数据去掉本判断后耗时由原40秒降到了4秒
					// 保证从本地数据库查出来的数据集合里没有同一个时间存在（同一个时间存在
					// 可能是插入数据或其它无法控制的情况下出现的），应用层是绝不允许出现时间重
					// 复的，所以本方法的目的就是为了控制不应用重复时间存在！（否则服务端将不会插入成功！）
					if(!_Utils.timeElementsExists(cpList, cp.getStart_time()))	
//					if(!timeElementsExists(cpList, cp.getStart_time()))	
						cpList.add(cp);
				}
				catch (OutOfMemoryError e)
				{
//					WidgetUtils.showToast(context, "* 您的操作过于频繁，数据载入失败，请重试！", ToastType.WARN);
					Log.e(TAG, "【首页findHistory(内)】频繁切换日期时导致内存溢出！", e);
				}
				catch (Exception e)
				{
					Log.e(TAG, e.getMessage(), e);
				}
				mCursor.moveToNext();
			}
			
			mCursor.close();
		}
		catch (OutOfMemoryError e){
//			WidgetUtils.showToast(context, "** 您的操作过于频繁，数据载入失败，请重试！", ToastType.WARN);
			Log.e(TAG, "【首页findHistory(外)】频繁切换日期时导致内存溢出！", e);
		}
		catch (Exception e)
		{
			Log.e(TAG, e.getMessage(), e);
		}
		finally
		{
			if(mCursor != null && !mCursor.isClosed())
			{
				mCursor.close();
			}
		}

		return cpList;
	}
	
	/**
	 * 返回指定日期（或时间）范围内的记录.
	 * <p>
	 * （1）如果startDate=2014-06-17 21:00:00.000， endDate=2014-06-18 09:00:00.00，则查询
	 * 的是2014-06-17 21:00:00.000到2014-06-18 09:00:00.00间的数据；<br>
	 * （2）如果startDate=2014-06-17， endDate=2014-06-18，则查询
	 * 的是2014-06-17 00:00:00.000到2014-06-17 23:59:59.999间的数据，相当于SQL的between and语句（即取的数据是>=起始，<结尾时间）；<br>
	 * 
	 * @param acountUidOfOwner 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @param startDateTimeLocal 要查询的本地时间范围起点
	 * @param endDateTimeLocal 要查询的本地时间范围结束点
	 * @param paramsIsUTC true表示传过来的时间参数已经是使用了utc时间的，就不需要再额外转utc了，否则需要本方法中来转哦
	 * @return
	 */
	public ArrayList<SportRecord> findHistoryRange_l(String acountUidOfOwner, String startDateTimeLocal, String endDateTimeLocal)
	{
		return findHistoryRange_l(acountUidOfOwner, startDateTimeLocal, endDateTimeLocal, true);
	}
	/**
	 * 返回指定日期（或时间）范围内的记录.
	 * <p>
	 * （1）如果startDate=2014-06-17 21:00:00.000， endDate=2014-06-18 09:00:00.00，则查询
	 * 的是2014-06-17 21:00:00.000到2014-06-18 09:00:00.00间的数据；<br>
	 * （2）如果startDate=2014-06-17， endDate=2014-06-18，则查询
	 * 的是2014-06-17 00:00:00.000到2014-06-17 23:59:59.999间的数据，相当于SQL的between and语句（即取的数据是>=起始，<结尾时间）；<br>
	 * 
	 * @param acountUidOfOwner 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @param startDateTimeLocal 要查询的本地时间范围起点
	 * @param endDateTimeLocal 要查询的本地时间范围结束点
	 * @param paramsIsUTC true表示传过来的时间参数已经是使用了utc时间的，就不需要再额外转utc了，否则需要本方法中来转哦
	 * @return
	 */
	public ArrayList<SportRecord> findHistoryRange_l(String acountUidOfOwner, String startDateTimeLocal, String endDateTimeLocal
			, boolean paramsIsUTC)
	{
//		ArrayList<SportRecord> srs = new ArrayList<SportRecord>();
//		Log.d(TAG, "startDateTimeLocal = " + startDateTimeLocal + ", endDateTimeLocal = " + endDateTimeLocal + ".");
		String startDateTimeUTC = "";
		String endDateTimeUTC = "";
		if(paramsIsUTC)
		{
			startDateTimeUTC = startDateTimeLocal;
			endDateTimeUTC   = endDateTimeLocal;
		}
		else
		{
			// 将本地时间转成UTC时间后再执行查询   startTimeUTC = TimeZoneHelper.__getUTC0FromLocalTime(
//			ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS_SSS, startDate+" 00:00:00.000");
			startDateTimeUTC= HttpHelper.getstartDateTimeUTC(startDateTimeLocal,false);
			endDateTimeUTC  = HttpHelper.getendDateTimeUTC(endDateTimeLocal,false);
//			startDateTimeUTC = TimeZoneHelper.__getUTC0FromLocalTime(
//					ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS, startDateTimeLocal+" 00:00:00.000");
//			endDateTimeUTC = TimeZoneHelper.__getUTC0FromLocalTime(
//					ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS, endDateTimeLocal+" 00:00:00.000");
		}
		
		// 时间范围内的数据
		String where = COLUMN_START_TIME+">='"+startDateTimeUTC+"' and "+COLUMN_START_TIME+"<'"+endDateTimeUTC+"'";
		System.out.println("kkkkkkkkkkk----->"+where);
		// 查找此期间内的运动原始数据
		return findHistory(acountUidOfOwner, where);
	}
	
	/**
	 * 返回尚未上传到服务端ArrayList<DaySynopic>记录.
	 * 
	 * @param acountUidOfOwner 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @return
	 */
	public ArrayList<SportRecord> findHistoryWitchNoSync(String acountUidOfOwner)
	{
		return findHistory(acountUidOfOwner, "sync_to_server=0");
	}
	
	/**
	 * 返回尚未上传到Googlefit的ArrayList<DaySynopic>记录.
	 * 
	 * @param acountUidOfOwner 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @return
	 */
	public ArrayList<SportRecord> findHistoryWitchNoSyncGoogle(String acountUidOfOwner)
	{
		return findHistory(acountUidOfOwner, "sync_to_server=0 or sync_to_server=1");
	}
	
	/**
	 * 返回指定日期(1日)内的需要进行睡眠计算的数据记录.
	 * 
	 * @param acountUidOfOwner 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @param date 查询日期（本地时间），格式形如：yyyy-MM-dd
	 * @see 本方法完全参考自服务端方法ReportProcessor.getDaySportDatasInMxTable_forSleep(..)，后绪改动请保持2者的同步修改！
	 * @return
	 */
	public ArrayList<SportRecord> findHistoryForSleepCalculate_l(String acountUidOfOwner, String dateLocal)
	{
		ArrayList<SportRecord> srs = new ArrayList<SportRecord>();
		try
		{
			// 自动计算1日的睡眠数据的截止日期（就是明天的日期）
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(SleepDataHelper.DATE_PATTERN_sdfYYMMDD.parse(dateLocal));
			gc.add(GregorianCalendar.DAY_OF_MONTH, 1); 
			String endDate = SleepDataHelper.DATE_PATTERN_sdfYYMMDD.format(gc.getTime());
			
//			// 查找此期间内的运动原始数据
			srs = findHistoryForSleepCalculate_l(acountUidOfOwner, dateLocal, endDate);
		}
		catch (Exception e)
		{
			Log.w(TAG, "findHistoryForSleepCalculate时日期格式转换出错，", e);
		}
		return srs;
	}
	/**
	 * 返回指定日期范围内的需要进行睡眠计算的数据记录.
	 * 
	 * @param acountUidOfOwner 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @param startDate 查询日期起（本地日期，如果查询的是2014-06-01当日1日数据，则本参数为”2014-06-01“）
	 * @param endDate 查询日期止（本地日期，如果查询的是2014-06-01当日1日数据，则本参数为”2014-06-02“）
	 * @see 本方法完全参考自服务端方法ReportProcessor.getDaySportDatasInMxTable_forSleep(..)，后绪改动请保持2者的同步修改！
	 * @return
	 */
	public ArrayList<SportRecord> findHistoryForSleepCalculate_l(String acountUidOfOwner, String startDateLocal, String endDateLocal)
	{ 
		ArrayList<SportRecord> srs = new ArrayList<SportRecord>(); 
		try
		{
			
			// 【关于时区的说明】以下代码是以系统默认时区parese，同时也以系统时区format，所以日期字符串的结果实际是不会变的，不用但心时区会变哦
			// 据Andy的意思：今天的睡眠数据实际上是昨天12：00至今天12：00，而不是今天12：00至明天12：00
//			GregorianCalendar gc = new GregorianCalendar();
//			gc.setTime(SleepDataHelper.DATE_PATTERN_sdfYYMMDD.parse(startDateLocal));
//			System.out.println("|||SleepDataHelper.DATE_PATTERN_sdfYYMMDD.parse(startDateLocal)---->"+SleepDataHelper.DATE_PATTERN_sdfYYMMDD.parse(startDateLocal));
//			gc.add(GregorianCalendar.DAY_OF_MONTH, -1); // 日期切换组件的今天-1即是andy认为的睡眠时间的“今天”
//			startDateLocal = SleepDataHelper.DATE_PATTERN_sdfYYMMDD.format(gc.getTime());
//			System.out.println("new startDateLocal--->"+startDateLocal);

//			gc.setTime(SleepDataHelper.DATE_PATTERN_sdfYYMMDD.parse(endDateLocal));
//			gc.add(GregorianCalendar.DAY_OF_MONTH, 1);// 结束日期也相应减1以便配合服务端的SQL查询
//			endDateLocal = SleepDataHelper.DATE_PATTERN_sdfYYMMDD.format(gc.getTime());
			
			// 时间范围内的数据 // TODO 新算法里，可能就不需要这个固定的时间点了，否则不利于跨时区（新睡眠算法尚未实现！）
//			String where = COLUMN_START_TIME+">='"+startDate+" 21:00:00.000' and "+COLUMN_START_TIME+"<='"+endDate+" 09:00:00.00'";
//			// 查找此期间内的运动原始数据
//			srs = findHistory(acountUidOfOwner, where);
//			srs = findHistoryRange_l(acountUidOfOwner, startDateLocal+" 00:00:00.000"
			srs = findHistoryRange_l(acountUidOfOwner, startDateLocal
//					, endDateLocal+" 09:00:00.00"
					, endDateLocal//+" 00:00:00.000" // 2014-08-07:按andy要求改成了统计到当天的24点
					);
			System.out.println("startDateLocal--->"+startDateLocal+"|||endDateLocal---->"+endDateLocal);
		}
		catch (Exception e)
		{
			Log.w(TAG, "findHistoryForSleepCalculate_l时日期格式转换出错，", e);
		}
		return srs;
	}
	
	//---------------------------------------------------------------------------------------------------
	public int getID()
	{
		return DBDataRefreshHelper.UPDATE_TABLE_KH;
	}
	public String getTableDesc()
	{
		return "首页”消息“缓存";
	}
	public String getTableName()
	{
		return TABLE_NAME;
	}
	
	/**
	 * 插入一行临时聊天的首页消息数据.
	 * 
	 * @param acountUidOfOwner 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @param srcUid 临时消息的发送者uid
	 * @param amd
	 * @return
	 */
	public long insertDaySynopic(String user_id, boolean hasSycToServer, SportRecord amd)
	{
		if(amd != null)
		{
			return insertDaySynopic(hasSycToServer
						,user_id
						,amd.getDevice_id()
						,amd.getStart_time()
						,amd.getState()
						,amd.getStep()
						,amd.getDuration()
						,amd.getDistance()
					);
		}
		return -1;
	}
	/**
	 * 插入一行数据到表中.
	 * 
	 * @param acountUidOfOwner 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @return the row ID of the newly inserted row, or -1 if an error occurred
	 */
	private long insertDaySynopic(boolean hasSycToServer
				, String user_id
				, String column_device_id
				, String column_start_time
				, String column_state
				, String column_step
				, String column_duration
				, String column_distance
			)
	{
		ContentValues initialValues=new ContentValues();
		initialValues.put(COLUMN_KEY_ACOUNT$UID, user_id);
		initialValues.put(COLUMN_DEVICE_ID, column_device_id);
		initialValues.put(COLUMN_START_TIME, column_start_time);
		initialValues.put(COLUMN_STATE, column_state);
		initialValues.put(COLUMN_STEP, column_step);
		initialValues.put(COLUMN_DURATION, column_duration);
		initialValues.put(COLUMN_DISTANCE, column_distance);
		initialValues.put(COLUMN_SYNC_TO_SERVER, hasSycToServer?1:0);
		
		return super.insert(TABLE_NAME, null, initialValues);//
	}
	
	/**
	 * <p>
	 * 将指定2维护vector表示的多行数据插入到本表中.<br><br>
	 * 
	 * 本方法中在插入开始前开始事务，在数据插入后提交数据以提高性能，否则性
	 * 能将相差N个数量级。要取消事务方式提交只需要把insertRow(..)前后有有关事务操作语句删除即可.<br><br>
	 * 
	 * 另：本方法也同时更新字段 {@link #updateTime}1次，以便刷新最近更新时间，用于其它地方显示之用.
	 * </p>
	 * 
	 * @param vector
	 * @see #insertRow(Vector)
	 */
	//*** 2011-11-17　BY　JS，生产环境下，1600条左右的产品信息，在读取完后插入到SQLLite的过程中如果不用事务
	//*** ，则从登陆开始耗时在1分30秒以上(测试表明1600行左右的产品的插入耗时就整整要1分钟30秒左右)，
	//*** 用了事务以后，从登陆开始耗时只有8秒左右(测试表明1600行左右的产品的插入耗时只需要1~2秒，太棒了！！！)
	public void insertDaySynopics(List<SportRecord> dss, String userId, boolean hasSycToServer)
	{
		SQLiteDatabase dbImpl = db.getDb(true);
		//* 手动设置开始事务[事务操作语句 1/3]
		dbImpl.beginTransaction();        
		
		// 更新最近刷新时间
		updateTime = new Date();
		
		//* 先尝试删除本地已经存储的日汇总数据（防重复，且用最新的数据插入，保证最新）
		if(dss.size() > 0)
		{
			//因为涉及到排序的问题，有逆序和升序。所以在此进行时间的判断
			//因为涉及到排序的问题，有逆序和升序。所以在此进行时间的判断
			//因为涉及到排序的问题，有逆序和升序。所以在此进行时间的判断
			String startDate = dss.get(0).getStart_time();
			String endDate = dss.get(dss.size() - 1).getStart_time();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try
			{
				Date a = sdf.parse(dss.get(0).getStart_time());
				Date b = sdf.parse(dss.get(dss.size() - 1).getStart_time());
				if(a.getTime() > b.getTime())
				{
					startDate = dss.get(dss.size() - 1).getStart_time();
					endDate = dss.get(0).getStart_time();
				}
			}
			catch (ParseException e)
			{
				Log.e("===", e.getMessage());
			}
			
			long delS = this.deleteDaySynopicWithRange(userId, startDate, endDate, false);
			System.out.println("在插入新数据新先[明细]：删除影响的行数="+delS+", userId="+userId+", startDate="+startDate+", endDate="+endDate);
		}
		
		//* 再插入数据
		for(SportRecord ds : dss)
			insertDaySynopic(userId, hasSycToServer, ds);
		
		//* 设置事务处理成功，不设置会自动回滚不提交[事务操作语句 2/3]
		dbImpl.setTransactionSuccessful(); 
		//* 事务结束，处理完成 [事务操作语句 3/3]
		dbImpl.endTransaction();        
	}
	
	/**
	 * 将指定时间范围内的数据标识为“已上传”。
	 * 
	 * @param acountUidOfOwner
	 * @param startTime 开始时间（>=）
	 * @param endTime 结束时间（<=）
	 * @return
	 */
	private long updateForSynced(String acountUidOfOwner
			, String startTime
			, String endTime)
	{
		ContentValues updateValues=new ContentValues();
		updateValues.put(COLUMN_SYNC_TO_SERVER, 1);
		
		// 时间范围内的数据全部标识为“已同步”
		String where = COLUMN_KEY_ACOUNT$UID+"='"+acountUidOfOwner+"' and "
				+COLUMN_START_TIME+">='"+startTime+"' and "+COLUMN_START_TIME+"<='"+endTime+"'";
		
		return super.update(TABLE_NAME, updateValues, where);
	}
	
	/**
	 * 将指定时间范围内的数据标识为“已上传”。google
	 * 
	 * @param acountUidOfOwner
	 * @param startTime 开始时间（>=）
	 * @param endTime 结束时间（<=）
	 * @return
	 */
	private long updateForSyncedGoogle(String acountUidOfOwner
			, String startTime
			, String endTime)
	{
		ContentValues updateValues=new ContentValues();
		updateValues.put(COLUMN_SYNC_TO_SERVER, 2);
		
		// 时间范围内的数据全部标识为“已同步”
		String where = COLUMN_KEY_ACOUNT$UID+"='"+acountUidOfOwner+"' and "
				+COLUMN_START_TIME+">='"+startTime+"' and "+COLUMN_START_TIME+"<='"+endTime+"'";
		
		return super.update(TABLE_NAME, updateValues, where);
	}
	
	/**
	 * 删除数据.
	 * 
	 * @param acountUidOfOwner 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @param srcUid 消息发送者的uid
	 * @param startDateTime >=开始时间
	 * @param endDateTime <=结束时间
	 * @param alsoDeleteNoSync true表示即使该数据未上传到服务端也被删除？
	 * @return
	 */
	public long deleteDaySynopicWithRange(String acountUidOfOwner, String startDateTime, String endDateTime
			, boolean alsoDeleteNoSync)
	{
		return super.delete(TABLE_NAME
				, COLUMN_KEY_ACOUNT$UID+"="+acountUidOfOwner+" and "
						+COLUMN_START_TIME+">='"+startDateTime+"' and "+COLUMN_START_TIME+"<='"
						+endDateTime+"' and "+COLUMN_SYNC_TO_SERVER+" in ("+(alsoDeleteNoSync?"0,1":"1")+")");
	}
//	/**
//	 * 删除临时聊天的首页”消息“.
//	 * 
//	 * @param acountUidOfOwner 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
//	 * @param srcUid 消息发送者的uid
//	 * @return
//	 */
//	public long deleteHistoryForTempChat(String acountUidOfOwner, String srcUid)
//	{
//		return super.delete(TABLE_NAME
//				// 指定消息发送者的本地记录
//				, COLUMN_KEY_ACOUNT$UID+"='"+acountUidOfOwner
//						+"' and "
//						+COLUMN_KEY_MESSAGE$TYPE+"='"+AlarmMessageType.tempChatMessage
//						+"' and "+COLUMN_KEY_KEY1+"='"+srcUid+"'");
//	}
	
	//---------------------------------------------------------------------------------------------------
	@Override
	public void refreshDataFromServer() throws Exception
	{
		// 默认本方法什么也不做
	}

	//--------------------------------------------------------------------------------------- 实用方法
	/**
	 * 返回指定日期（或时间）范围内的运动数据记录（通用方法，不是特定用于查询睡眠数据的哦）.
	 * <p>
	 * 如果startDate=2014-06-17， endDate=2014-06-18，则查询
	 * 的是2014-06-17 00:00:00.000到2014-06-17 23:59:59.999间的数据.<br>
	 * 
	 * @param acountUidOfOwner 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @param startDate 查询日期起（本地时区，如果查询的是2014-06-01当日1日数据，则本参数为”2014-06-01“）
	 * @param endDate 查询日期止（本地时区，如果查询的是2014-06-01当日1日数据，则本参数为”2014-06-02“）
	 * @param paramsIsUTC true表示传过来的时间参数已经是使用了utc时间的，就不需要再额外转utc了，否则需要本方法中来转哦
	 * @see 本方法完全参考自服务端方法ReportProcessor.getDaySportDatasInMxTable_forSleep(..)，后绪改动请保持2者的同步修改！
	 * @return
	 */
	public static ArrayList<SportRecord> findHistoryForCommon_l(Context context, String acountUidOfOwner
			, String startDate, String endDate, boolean dateNoTime, boolean paramsIsUTC)
	{
		// 如果查询的是年月日，那么就强行加上时间点，以便最内层方法findHistoryRange_l(..)把此时间点转UTC后再查询
		// ，也就是说加了时区处理后，所有的时间查询都应到时间点，只不过如果是1天的话，那就是24小时跨度的时间点（以便于转UTC时间）
		if(dateNoTime)
		{
			startDate = startDate;
			endDate = endDate;
		}
		ArrayList<SportRecord> ret = new ArrayList<SportRecord>();
		UserDeviceRecord chatMessageTable = null;
		try
		{
			// 初始化表操作对象
			chatMessageTable = UserDeviceRecord.getInstance(context);
			chatMessageTable.open();
			System.out.println("startDate---------->"+startDate+"........"+endDate);
			// 保存消息到本地数据库
			ret = chatMessageTable.findHistoryRange_l(acountUidOfOwner, startDate, endDate, paramsIsUTC);
//			int sum = 0;
//			for(int i = 0 ; i<ret.size();i++){
//				SportRecord cc = ret.get(i);
//				if(Integer.parseInt(cc.getState())==1 || Integer.parseInt(cc.getState())==2){
//					sum+= Integer.parseInt(cc.getStep());
//				}
//				//Log.i(TAG, "日汇总数据"+i+"：" + originalSportDatas.get(i).toString());
//			}
//			Log.e(TAG, "日汇总数据总部数：" + sum);
		}
		catch (Exception e)
		{
			Log.w(TAG, e);
		}
		finally
		{
			try{
				if(chatMessageTable != null)
					chatMessageTable.close();
			}
			catch (Exception e){
			}
		}
		
		return ret;
	}
	
	/**
	 * 返回指定日期(1日)内的需要进行睡眠计算的数据记录.
	 * 
	 * @param acountUidOfOwner 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @param dateLocal 查询日期（本地时区）
	 * @see 本方法完全参考自服务端方法ReportProcessor.getDaySportDatasInMxTable_forSleep(..)，后绪改动请保持2者的同步修改！
	 * @return
	 */
	public static ArrayList<SportRecord> findHistoryForSleepCalculate_l(Context context, String acountUidOfOwner, String dateLocal)
	{
		ArrayList<SportRecord> ret = new ArrayList<SportRecord>();
		UserDeviceRecord chatMessageTable = null;
		try
		{
			// 初始化表操作对象
			chatMessageTable = UserDeviceRecord.getInstance(context);
			chatMessageTable.open();
			
			// 保存消息到本地数据库
			ret = chatMessageTable.findHistoryForSleepCalculate_l(acountUidOfOwner, dateLocal);
		}
		catch (Exception e)
		{
			Log.w(TAG, e);
		}
		finally
		{
			try{
				if(chatMessageTable != null)
					chatMessageTable.close();
			}
			catch (Exception e){
			}
		}
		
		return ret;
	}
	
	/**
	 * 返回指定日期范围内的需要进行睡眠计算的数据记录.
	 * 
	 * @param acountUidOfOwner 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @param startDate 查询日期起（本地时区，如果查询的是2014-06-01当日1日数据，则本参数为”2014-06-01“）
	 * @param endDate 查询日期止（本地时区，如果查询的是2014-06-01当日1日数据，则本参数为”2014-06-02“）
	 * @see 本方法完全参考自服务端方法ReportProcessor.getDaySportDatasInMxTable_forSleep(..)，后绪改动请保持2者的同步修改！
	 * @return
	 */
	public static ArrayList<SportRecord> findHistoryForSleepCalculate_l(Context context, String acountUidOfOwner
			, String startDateLocal, String endDateLocal)
	{
		ArrayList<SportRecord> ret = new ArrayList<SportRecord>();
		
		UserDeviceRecord chatMessageTable = null;
		
		try
		{
			// 初始化表操作对象
			chatMessageTable = UserDeviceRecord.getInstance(context);
			chatMessageTable.open();
			
			// 保存消息到本地数据库
			ret = chatMessageTable.findHistoryRange_l(acountUidOfOwner, startDateLocal, endDateLocal);
//			System.out.println("/startDateLocal"+startDateLocal+".."+endDateLocal);
		}
		catch (Exception e)
		{
			Log.w(TAG, e);
		}
		finally
		{
			try{
				if(chatMessageTable != null)
					chatMessageTable.close();
			}
			catch (Exception e){
			}
		}
		
		return ret;
	}
	
	/**
	 * 返回尚未上传到服务端ArrayList<DaySynopic>记录的实用方法.
	 * 
	 * @param acountUidOfOwner 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @return
	 */
	// 同步此方法的目的是防止在短时间间内的不必要查询
	public synchronized static ArrayList<SportRecord> findHistoryWitchNoSync(Context context, String acountUidOfOwner)
	{
		ArrayList<SportRecord> ret = new ArrayList<SportRecord>();
		UserDeviceRecord chatMessageTable = null;
		try
		{
			// 初始化表操作对象
			chatMessageTable = UserDeviceRecord.getInstance(context);
			chatMessageTable.open();
			
			// 保存消息到本地数据库
			ret = chatMessageTable.findHistoryWitchNoSync(acountUidOfOwner);
		}
		catch (Exception e)
		{
			Log.w(TAG, e);
		}
		finally
		{
			try{
				if(chatMessageTable != null)
					chatMessageTable.close();
			}
			catch (Exception e){
			}
		}
		
		return ret;
	}
	
	/**
	 * 返回尚未上传到Google ArrayList<DaySynopic>记录的实用方法.
	 * 
	 * @param acountUidOfOwner 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @return
	 */
	// 同步此方法的目的是防止在短时间间内的不必要查询
	public synchronized static ArrayList<SportRecord> findHistoryWitchNoSyncGoogle(Context context, String acountUidOfOwner)
	{
		ArrayList<SportRecord> ret = new ArrayList<SportRecord>();
		UserDeviceRecord chatMessageTable = null;
		try
		{
			// 初始化表操作对象
			chatMessageTable = UserDeviceRecord.getInstance(context);
			chatMessageTable.open();
			
			// 保存消息到本地数据库
			ret = chatMessageTable.findHistoryWitchNoSyncGoogle(acountUidOfOwner);
		}
		catch (Exception e)
		{
			Log.w(TAG, e);
		}
		finally
		{
			try{
				if(chatMessageTable != null)
					chatMessageTable.close();
			}
			catch (Exception e){
			}
		}
		
		return ret;
	}
	
	/**
	 * 将指定时间范围内的数据标识为“已上传”的实用方法。
	 * 
	 * @param acountUidOfOwner
	 * @param startTime 开始时间（>=）
	 * @param endTime 结束时间（<=）
	 * @return
	 */
	public static long updateForSynced(Context context, String acountUidOfOwner, String startTime, String endTime)
	{
		UserDeviceRecord chatMessageTable = null;
		try
		{
			// 初始化表操作对象
			chatMessageTable = UserDeviceRecord.getInstance(context);
			chatMessageTable.open();
			
			// 保存消息到本地数据库
			return chatMessageTable.updateForSynced(acountUidOfOwner, startTime, endTime);
		}
		catch (Exception e)
		{
			Log.w(TAG, e);
		}
		finally
		{
			try{
				if(chatMessageTable != null)
					chatMessageTable.close();
			}
			catch (Exception e){
			}
		}
		
		return -1;
	}
	
	/**
	 * 将指定时间范围内的数据标识为“已上传”的实用方法。 google
	 * 
	 * @param acountUidOfOwner
	 * @param startTime 开始时间（>=）
	 * @param endTime 结束时间（<=）
	 * @return
	 */
	public static long updateForSyncedGoogle(Context context, String acountUidOfOwner, String startTime, String endTime)
	{
		UserDeviceRecord chatMessageTable = null;
		try
		{
			// 初始化表操作对象
			chatMessageTable = UserDeviceRecord.getInstance(context);
			chatMessageTable.open();
			
			// 保存消息到本地数据库
			return chatMessageTable.updateForSyncedGoogle(acountUidOfOwner, startTime, endTime);
		}
		catch (Exception e)
		{
			Log.w(TAG, e);
		}
		finally
		{
			try{
				if(chatMessageTable != null)
					chatMessageTable.close();
			}
			catch (Exception e){
			}
		}
		
		return -1;
	}
	
	/**
	 * 将消息保存到本地数据库中作为历史聊天消息保存下来.
	 * 
	 * @param context
	 * @param uid
	 * @param me
	 * @see #putMessage(Context, String, ChatMsgEntity)
	 * @return true表示可存成功，否则保存失败
	 */
	public static boolean saveToSqlite(Context context, List<SportRecord> dss, String userId
			, boolean hasSycToServer)
	{
		UserDeviceRecord chatMessageTable = null;
		try
		{
			// 初始化表操作对象
			chatMessageTable = UserDeviceRecord.getInstance(context);
			chatMessageTable.open();
			
			// 保存消息到本地数据库
			chatMessageTable.insertDaySynopics(dss, userId, hasSycToServer);
			return true;
		}
		catch (Exception e)
		{
			Log.w(TAG, e);
		}
		finally
		{
			try{
				if(chatMessageTable != null)
					chatMessageTable.close();
			}
			catch (Exception e){
			}
		}
		
		return false;
	}
	
	/**
	 * 将消息保存到本地数据库中作为历史聊天消息保存下来.
	 * 
	 * @param context
	 * @param uid
	 * @param me
	 * @see #putMessage(Context, String, ChatMsgEntity)
	 */
	public static void saveToSqliteAsync(final Context context, final List<SportRecord> dss ,final  String userId, final boolean hasSycToServer, final Observer obsForSucess)
	{
		new AsyncTask<Object, Object, Boolean>(){
			@Override
			protected Boolean doInBackground(Object... params)
			{
				return UserDeviceRecord.saveToSqlite(context, dss, userId, hasSycToServer);
			}
			@Override
			protected void onPostExecute(Boolean result)
			{
				if(result.booleanValue() && obsForSucess != null)
				{
					obsForSucess.update(null, null);
				}
			}
		}.execute();
	}
	
	
}
