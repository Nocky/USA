package com.linkloving.rtring_c_watch.db.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.eva.android.widget.ActivityRoot;
import com.eva.android.widget.util.WidgetUtils;

//本类中之所以不把进度提示等功能封装进来，是因为这些涉及到UI方面，而在如此深度的代码中涉及到UI
//则可能会出现很多复杂的与UI方面的问题，因为Ui方面要与对应的activity绑定，而在使用中时这个activity
//的状况是很难把握的，具体细节现已忘记，但之前xzj已证明不可行，所以关于进度方面的Ui代码还是交给本类的调
//用者自行实现吧
/**
 * 用于集中管理本系统的sqlLite数据库各表的数据刷新操作.
 * 
 * @author Jack Jiang, 2013-03-18
 * @since 2.5
 */
public class DBDataRefreshHelper 
{
	//对应于产品信息的sqlLite表操作对象
	private UserDeviceRecord cpTable;
//	//对应于业务员所属普通客户信息的sqlLite表操作对象
	private DaySynopicTable khTable;
//	//对应于业务员所属商超客户信息的sqlLite表操作对象
//	private SckhTable sckhTable;
	
	private Context context;
	
	//用示标识：刷新所有sqlLite的表数据
	public final static int UPDATE_TABLE_ALL = 0;
	//用示标识：刷新产品信息的表数据
	public final static int UPDATE_TABLE_CP = 1;
	//用示标识：刷新业务员所属普通客户信息的表数据
	public final static int UPDATE_TABLE_KH = 2;
//	//用示标识：刷新业务员所属商超客户信息的表数据
//	public final static int UPDATE_TABLE_SCKH = 3;
	
	//singleton
	private static DBDataRefreshHelper instance;
	
	private DBDataRefreshHelper(Context context)
	{
		this.context=context;
		
		cpTable = UserDeviceRecord.getInstance(context);
		khTable = DaySynopicTable.getInstance(context);
//		sckhTable = SckhTable.getInstance(context);
	}	
	
	/**
	 * 获得唯一实例引用.
	 * 
	 * @param context
	 * @return
	 */
	public static DBDataRefreshHelper getInstance(Context context)
	{
		if(instance==null)
			instance=new DBDataRefreshHelper(context);
		return instance;
	}
	
	/**
	 * 获得当前本系统中sqlLite数据库表的基本信息列表.<br>
	 * 本方法目前仅用于类 DBDataListViewActivity 中, 用于管理各表的基本信息.
	 * 
	 * @return
	 * @see DBDataListViewActivity
	 */
	public List<Map<String ,String >> getDatabaseNames()
	{
		List<Map<String,String>> databaseData=new ArrayList<Map<String,String>>();
		
		databaseData.add(cpTable.getTableInfo());
		databaseData.add(khTable.getTableInfo());
//		databaseData.add(sckhTable.getTableInfo());
		
		return databaseData;
	}

	/**
	 * <p>
	 * 从服务器取数据刷新指定表的的本地SqlLite表的数据.<br><br>
	 * 
	 * 注：本方法里不能涉及UI方面的任何操作——因为本方法很可能为提高用户体验而放置于一独立线程中运行，比如
	 * 不应该使用AToolKits.queryData方法应该使用AToolKits.queryDataRoot，因为后
	 * 者涉及到在出错时调用toast.<br><br>
	 * 
	 * </p>
	 * @exception Exception 过程中出现的任何异常表示数据刷新不成功，否则表示更新成功
	 */
	public void refreshTableDataFromServer(int tableId) throws Exception
	{
		switch(tableId)
		{
			case UPDATE_TABLE_ALL:
				//初始化各表操作对象
				cpTable.open();
				khTable.open();
//				sckhTable.open();
				//刷新数据（从服务端）
				cpTable.refreshDataFromServer();
				khTable.refreshDataFromServer();
//				sckhTable.refreshDataFromServer();
				//释放各表操作对象所占用的系统资源
				cpTable.close();
				khTable.close();
//				sckhTable.close();
				break;
				
			case UPDATE_TABLE_KH:
				//初始化表操作对象
				cpTable.open();
				//刷新数据（从服务端）
				cpTable.refreshDataFromServer();
				//释放表操作对象所占用的系统资源
				cpTable.close();
				break;
				
			case UPDATE_TABLE_CP:
				//初始化表操作对象
				khTable.open();
				//刷新数据（从服务端）
				khTable.refreshDataFromServer();
				//释放表操作对象所占用的系统资源
				khTable.close();
				break;
				
//			case UPDATE_TABLE_SCKH:
//				//初始化表操作对象
//				sckhTable.open();
//				//刷新数据（从服务端）
//				sckhTable.refreshDataFromServer();
//				//释放表操作对象所占用的系统资源
//				sckhTable.close();
//				break;
		}
	}
	
	//----------------------------------------------------------------------------------------------------------------
	/**
	 * <p>
	 * 更新缓存的实现方法.
	 * 本方法实际是调用 {@link #refreshTableDataFromServer(int)}实现的，但本方法进行进一步的
	 * 易用性封装：增加了异常 处理、处理完后把message发给handler等.<br><br>
	 * 
	 * 注：本方法推荐与 {@link #handlerUpdateDataResult(Activity, Message)}成对使用.<br>
	 * </p>
	 * 
	 * @param tableID 要更新的目标表格id，参见方法： {@link #refreshTableDataFromServer(int)}
	 * @see DBDataRefreshHelper#refreshTableDataFromServer(int)
	 * @see Handler#sendMessage(Message)
	 */
	public static Message updateData(int tableID, Context context)
	{
		DBDataRefreshHelper dataBaseHelper= DBDataRefreshHelper.getInstance(context);//getApplicationContext());
		//加一层异常处理增强代码健壮性的同时如果出错则能给出较明显的提示
		try
		{
			//实施缓存更新
//			dataBaseHelper.open();
			dataBaseHelper.refreshTableDataFromServer(tableID);
//			dataBaseHelper.close();
			
			//发送结果消息集中处理
			Message msg = Message.obtain();
			msg.what = 1;//what = 1，表示缓存更新成功！
			return msg;
		}
		catch (Exception e)
		{
			//发生异常时通知handle
			Message msg = new Message();
			msg.what = 2;//what = 2，表示缓存更新过程中出现错误！
			Bundle b = new Bundle();
			b.putSerializable(ActivityRoot.EX1, e);//异常对象！
			msg.setData(b);
			
			//发送结果消息集中处理
			return msg;
		}
	}
	
	/**
	 * <p>
	 * 对缓存更新完成后的message结查进行统一处理.<br
	 * 
	 * 注：本方法推荐与 {@link #updateData(int, DBDataRefreshHelper, Handler)}成对使用.<br>
	 * </p>
	 * 
	 * @param activity 调用者activity
	 * @param msg 缓存更新完成后的结果message
	 * @return 缓存更新完成后message中的what值，目前：1 表示更新成功，2 表示更新中出现了错误
	 */
	public static int handlerUpdateDataResult(Context activity, Message msg)
	{
		switch(msg.what)
		{
			case 1:
				WidgetUtils.showToast(activity, "更新缓存成功！", WidgetUtils.ToastType.OK);
				break;
			case 2:
				Exception e = (Exception)msg.getData().getSerializable(ActivityRoot.EX1);
				e.printStackTrace();
				WidgetUtils.showToast(activity, "更新缓存出错，"+e.getMessage(), WidgetUtils.ToastType.WARN);
				break;
			default:
				WidgetUtils.showToast(activity, "UpdateTableHandler中出现未知的what="+msg.what, WidgetUtils.ToastType.ERROR);
				break;
		}
		return msg.what;
	}
}
