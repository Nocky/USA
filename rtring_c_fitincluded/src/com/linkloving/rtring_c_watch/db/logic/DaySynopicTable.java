package com.linkloving.rtring_c_watch.db.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.linkloving.band.dto.DaySynopic;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.db.TableRoot;

/**
 * 日数据概览表历史记录的辅助操作实现类.
 * 
 * @author Jack Jiang, 2014-03-21
 * @since 2.5
 */
public class DaySynopicTable extends TableRoot
{
	private final static String TAG = DaySynopicTable.class.getSimpleName();
	
	/** 存放于sqlLite数据库中的表格字段名：自增id（主键）（默认ident列，无需插入数据）*/
	private static final String COLUMN_KEY_ID="_record_id";
	private static final String COLUMN_KEY_ACOUNT$UID="_user_id";
	
	public static final String COLUMN_DATA_DATE="data_date";
	public static final String COLUMN_DATA_DATE2="data_date2";
	public static final String COLUMN_RUN_DURATION="run_duration";
	public static final String COLUMN_RUN_STEP="run_step";
	public static final String COLUMN_RUN_DISTANCE="run_distance";
	public static final String COLUMN_WORK_DURATION="work_duration";
	public static final String COLUMN_WORK_STEP="work_step";
	public static final String COLUMN_WORK_DISTANCE="work_distance";
	
	public static final String COLUMN_SLEEP_MINUTE="sleep_minute";
	public static final String COLUMN_DEEP_SLEEP_MIUTE="deep_sleep_miute";
	
	public static final String COLUMN_SYNC_TO_SERVER="sync_to_server";// 0 表示此离线数据是由设备中读出且尚未成功同步到服务端，1 表示已同步到服务端的数据
	
	public static final String COLUMN_CREATE_TIME="create_time";
	public static final String COLUMN_KEY_UPDATE$TIME="_update_time";
	
	/** 存放于sqlLite数据库中的表名 */
	public static final String TABLE_NAME="rt_day_synopic";
	
	/** 建表语句 */
	public static final String DB_CREATE="CREATE TABLE "
										+TABLE_NAME+" ( "	
										+COLUMN_KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"
										+COLUMN_KEY_ACOUNT$UID+" INTEGER,"
										+COLUMN_DATA_DATE+" TEXT,"
										+COLUMN_DATA_DATE2+" TEXT,"
										+COLUMN_RUN_DURATION+" INTEGER,"
										+COLUMN_RUN_STEP+" INTEGER,"
										+COLUMN_RUN_DISTANCE+" INTEGER,"
										+COLUMN_WORK_DURATION+" INTEGER,"
										+COLUMN_WORK_STEP+" INTEGER," 
										+COLUMN_WORK_DISTANCE+" INTEGER," 
										+COLUMN_SLEEP_MINUTE+" INTEGER," 
										+COLUMN_DEEP_SLEEP_MIUTE+" INTEGER," 
										+COLUMN_SYNC_TO_SERVER+" INTEGER," 
										+COLUMN_CREATE_TIME+" TIMESTAMP default (datetime('now', 'localtime')),"
//										--, _update_date TEXT default datetime(CURRENT_TIMESTAMP)
										// date('now')或CURRENT_TIMESTAMP 时间是以格林尼治标准时间为基准的，因此在中国使用的话会正好早8个小时
										+COLUMN_KEY_UPDATE$TIME+" TIMESTAMP default (datetime('now', 'localtime'))"
										+")";
	//singleton
	private static DaySynopicTable instance;
	
	private DaySynopicTable(Context context)
	{
		super(context);
	}
	
	public static DaySynopicTable getInstance(Context context)
	{
		if(instance==null)
			// FIX BUG: 以下context不能直接传Activity及其子类的句柄，否则将发生内存泄漏！
			// Application为全局唯一，所以不存在不释放的问题！
			instance=new DaySynopicTable(MyApplication.getInstance(context));
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
		return query(new String[]{
				 COLUMN_KEY_ID
				,COLUMN_DATA_DATE
				,COLUMN_DATA_DATE2
				,COLUMN_RUN_DURATION
				,COLUMN_RUN_STEP
				,COLUMN_RUN_DISTANCE
				,COLUMN_WORK_DURATION
				,COLUMN_WORK_STEP
				,COLUMN_WORK_DISTANCE
				,COLUMN_SLEEP_MINUTE
				,COLUMN_DEEP_SLEEP_MIUTE
				} , COLUMN_KEY_ACOUNT$UID+"='"+acountUidOfOwner+"'"+(condition == null?"":" and "+condition)+" order by "+COLUMN_DATA_DATE+" asc");
	}
	/**
	 * 返回ArrayList<DaySynopic>记录.
	 * 
	 * @param acountUidOfOwner 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @return
	 */
	public ArrayList<DaySynopic> findHistory(String acountUidOfOwner, String condition)
	{
		ArrayList<DaySynopic> cpList= new ArrayList<DaySynopic>();
		//条件是
		Cursor mCursor = queryHistoryImpl(acountUidOfOwner, condition);//" 1=1 order by _update_time asc");// 取出的结果顺序的哦）
		mCursor.moveToFirst();
		while(!mCursor.isAfterLast())
		{
			try
			{
				DaySynopic cp = new DaySynopic();
				int j = 0;
				cp.setRecord_id(mCursor.getString(j++)); // 此record_id是本地sqlite生成的，用于update时
				cp.setData_date(mCursor.getString(j++));
				cp.setData_date2(mCursor.getString(j++));
				cp.setRun_duration(mCursor.getString(j++));
				cp.setRun_step(mCursor.getString(j++));
				cp.setRun_distance(mCursor.getString(j++));
				cp.setWork_duration(mCursor.getString(j++));
				cp.setWork_step(mCursor.getString(j++));
				cp.setWork_distance(mCursor.getString(j++));
				cp.setSleepMinute(mCursor.getString(j++));
				cp.setDeepSleepMiute(mCursor.getString(j++));
				cpList.add(cp);
			}
			catch (Exception e)
			{
				Log.e(TAG, e.getMessage(), e);
			}
			
			mCursor.moveToNext();
		}
		mCursor.close();
		return cpList;
	}
	
	/**
	 * 返回指定日期范围内的记录.
	 * <p>
	 * 如果startDate=2014-06-17， endDate=2014-06-18，则查询
	 * 的是2014-06-17 00:00:00.000到2014-06-17 23:59:59.999间的数据.<br>
	 * 
	 * @param acountUidOfOwner 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public ArrayList<DaySynopic> findHistoryRange(String acountUidOfOwner, String startDate, String endDate)
	{
//		ArrayList<DaySynopic> srs = new ArrayList<DaySynopic>();
		// 时间范围内的数据
//		String where = COLUMN_DATA_DATE+">='"+startDate+"' and "+COLUMN_DATA_DATE+"<='"+endDate+"'";
		String where = "("+COLUMN_DATA_DATE+" between '"+startDate+"' and '"+endDate+"')";
		
		// 查找此期间内的运动原始数据
		return findHistory(acountUidOfOwner, where);
	}
	
	/**
	 * 返回尚未上传到服务端ArrayList<DaySynopic>记录.
	 * 
	 * @param acountUidOfOwner 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @return
	 */
	public ArrayList<DaySynopic> findHistoryWitchNoSync(String acountUidOfOwner)
	{
		return findHistory(acountUidOfOwner, "sync_to_server=0");
	}
	
	
	//---------------------------------------------------------------------------------------------------
	public int getID()
	{
		return DBDataRefreshHelper.UPDATE_TABLE_CP;
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
	public long insertDaySynopic(String user_id, boolean hasSycToServer, DaySynopic amd)
	{
		if(amd != null)
		{
			return insertDaySynopic(hasSycToServer
						,user_id
						,amd.getData_date()
						,amd.getData_date2()
						,amd.getRun_duration()
						,amd.getRun_step()
						,amd.getRun_distance()
						,amd.getWork_duration()
						,amd.getWork_step()
						,amd.getWork_distance()
						,amd.getSleepMinute()
						,amd.getDeepSleepMiute()
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
				, String data_date
				, String data_date2
				, String run_duaration
				, String run_step
				, String run_distance
				, String work_duaration
				, String work_step
				, String work_distance
				, String sleep_minute
				, String deep_sleep_miute
			)
	{
		ContentValues initialValues=new ContentValues();
		initialValues.put(COLUMN_KEY_ACOUNT$UID, user_id);
		initialValues.put(COLUMN_DATA_DATE, data_date);
		initialValues.put(COLUMN_DATA_DATE2, data_date2);
		initialValues.put(COLUMN_RUN_DURATION, run_duaration);
		initialValues.put(COLUMN_RUN_STEP, run_step);
		initialValues.put(COLUMN_RUN_DISTANCE, run_distance);
		initialValues.put(COLUMN_WORK_DURATION, work_duaration);
		initialValues.put(COLUMN_WORK_STEP, work_step);
		initialValues.put(COLUMN_WORK_DISTANCE, work_distance);
		initialValues.put(COLUMN_SLEEP_MINUTE, sleep_minute);
		initialValues.put(COLUMN_DEEP_SLEEP_MIUTE, deep_sleep_miute);
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
	public void insertDaySynopics(List<DaySynopic> dss, String userId, boolean hasSycToServer)
	{
		SQLiteDatabase dbImpl = db.getDb(true);
		//* 手动设置开始事务[事务操作语句 1/3]
		dbImpl.beginTransaction();        
		
		// 更新最近刷新时间
		updateTime = new Date();
		
		//* 先尝试删除本地已经存储的日汇总数据（防重复，且用最新的数据插入，保证最新）
		if(dss.size() > 0)
		{
			String startDate = dss.get(0).getData_date();
			String endDate = dss.get(dss.size()-1).getData_date();
			// 
			long delS = this.deleteDaySynopicWithRange(userId, startDate, endDate);
			System.out.println("在插入新数据新先[汇总]：删除影响的行数="+delS+", userId="+userId+", startDate="+startDate+", endDate="+endDate);
		}
		
		//* 再插入数据
		for(DaySynopic ds : dss)
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
	 * @param dates 要处理的日期数组
	 * @return
	 */
	//*** 使用事务，提升性能
	public void updateForSynceds(String acountUidOfOwner, String[] dates)
	{
		if(dates != null && dates.length > 0)
		{
			SQLiteDatabase dbImpl = db.getDb(true);
			//* 手动设置开始事务[事务操作语句 1/3]
			dbImpl.beginTransaction();        

			// 更新最近刷新时间
			updateTime = new Date();

			//* 更新数据
			for(String date : dates)
				updateForSynced(acountUidOfOwner, date);

			//* 设置事务处理成功，不设置会自动回滚不提交[事务操作语句 2/3]
			dbImpl.setTransactionSuccessful(); 
			//* 事务结束，处理完成 [事务操作语句 3/3]
			dbImpl.endTransaction();      
		}
	}
	/**
	 * 将指定日期内的数据标识为“已上传”。
	 * 
	 * @param acountUidOfOwner
	 * @param dates 要处理的日期数组
	 * @return
	 */
	private long updateForSynced(String acountUidOfOwner, String date)
	{
		ContentValues updateValues=new ContentValues();
		updateValues.put(COLUMN_SYNC_TO_SERVER, 1);
		
		// 时间范围内的数据全部标识为“已同步”
		String where = COLUMN_KEY_ACOUNT$UID+"='"+acountUidOfOwner+"' and "
				+COLUMN_DATA_DATE+"='"+date+"'";
		
		return super.update(TABLE_NAME, updateValues, where);
	}
	
	/**
	 * 更新指定日期汇总数据的浅睡眠时间（单位：分钟）、深睡眠时间（单位：分钟）。
	 * 
	 * @param acountUidOfOwner
	 * @param dates 要处理的日期数组
	 * @return
	 */
	private long updateSleepTime(String acountUidOfOwner, String date, long sleepMinite, long deepSleepMinute)
	{
		ContentValues updateValues=new ContentValues();
		updateValues.put(COLUMN_SLEEP_MINUTE, sleepMinite);
		updateValues.put(COLUMN_DEEP_SLEEP_MIUTE, deepSleepMinute);
		
		// 时间范围内的数据全部标识为“已同步”
		String where = COLUMN_KEY_ACOUNT$UID+"='"+acountUidOfOwner+"' and "
				+COLUMN_DATA_DATE+"='"+date+"'";
		
		return super.update(TABLE_NAME, updateValues, where);
	}
	
	/**
	 * 删除数据.
	 * 
	 * @param acountUidOfOwner 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @param srcUid 消息发送者的uid
	 * @param startDate >=开始时期
	 * @param endDate <=结束日期
	 * @return
	 */
	public long deleteDaySynopicWithRange(String acountUidOfOwner, String startDate, String endDate)
	{
		return super.delete(TABLE_NAME
				, COLUMN_KEY_ACOUNT$UID+"="+acountUidOfOwner+" and "
						+COLUMN_DATA_DATE+">='"+startDate+"' and "+COLUMN_DATA_DATE+"<='"+endDate+"'");
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
	 * 返回指定日期范围内的记录.
	 * <p>
	 * 如果startDate=2014-06-17， endDate=2014-06-18，则查询
	 * 的是2014-06-17 00:00:00.000到2014-06-17 23:59:59.999间的数据.<br>
	 * 
	 * @param acountUidOfOwner 本地数据的所有者账号，本条件是读取本地数据的先决条件，否则就窜数据了！
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public static ArrayList<DaySynopic> findHistoryRange(Context context, String acountUidOfOwner, String startDate, String endDate)
	{
		ArrayList<DaySynopic> ret = new ArrayList<DaySynopic>();
		DaySynopicTable chatMessageTable = null;
		try
		{
			// 初始化表操作对象
			chatMessageTable = DaySynopicTable.getInstance(context);
			chatMessageTable.open();
			
			// 保存消息到本地数据库
			ret = chatMessageTable.findHistoryRange(acountUidOfOwner, startDate, endDate);
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
	public synchronized static ArrayList<DaySynopic> findHistoryWitchNoSync(Context context, String acountUidOfOwner)
	{
		ArrayList<DaySynopic> ret = new ArrayList<DaySynopic>();
		DaySynopicTable chatMessageTable = null;
		try
		{
			// 初始化表操作对象
			chatMessageTable = DaySynopicTable.getInstance(context);
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
	 * 将指定时间范围内的数据标识为“已上传”的实用方法。
	 * 
	 * @param acountUidOfOwner
	 * @param startTime 开始时间（>=）
	 * @param endTime 结束时间（<=）
	 * @return
	 */
	public static void updateForSynced(Context context, String acountUidOfOwner, String[] dates)
	{
		DaySynopicTable chatMessageTable = null;
		try
		{
			// 初始化表操作对象
			chatMessageTable = DaySynopicTable.getInstance(context);
			chatMessageTable.open();
			
			// 保存消息到本地数据库
			chatMessageTable.updateForSynceds(acountUidOfOwner, dates);
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
	}
	
	/**
	 * 更新指定日期汇总数据的浅睡眠时间（单位：分钟）、深睡眠时间（单位：分钟）。
	 * 
	 * @param acountUidOfOwner
	 * @param dates 要处理的日期数组
	 * @return
	 */
	public static long updateSleepTime(Context context, String acountUidOfOwner
			, String date, long sleepMinite, long deepSleepMinute)
	{
		DaySynopicTable chatMessageTable = null;
		try
		{
			// 初始化表操作对象
			chatMessageTable = DaySynopicTable.getInstance(context);
			chatMessageTable.open();
			
			// 保存消息到本地数据库
			return chatMessageTable.updateSleepTime(acountUidOfOwner, date, sleepMinite, deepSleepMinute);
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
	public static boolean saveToSqlite(Context context, List<DaySynopic> dss, String userId
			, boolean hasSycToServer)
	{
		DaySynopicTable chatMessageTable = null;
		try
		{
			// 初始化表操作对象
			chatMessageTable = DaySynopicTable.getInstance(context);
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
	public static void saveToSqliteAsync(final Context context, final List<DaySynopic> dss
			,final  String userId, final boolean hasSycToServer)
	{
		new AsyncTask<Object, Object, Object>(){
			@Override
			protected Object doInBackground(Object... params)
			{
				DaySynopicTable.saveToSqlite(context, dss, userId, hasSycToServer);
				return null;
			}
		}.execute();
	}
	
	
}
