package com.linkloving.rtring_c_watch.db.logic;

import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.eva.android.widget.ActivityRoot;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.db.TableRoot;

// TODO ！！本类有时间再重构！！
// TODO ！！本类有时间再重构！！
// TODO ！！本类有时间再重构！！
// TODO ！！本类有时间再重构！！
/**
 * 本activity用于查看本系统的sqlLite数据库各表的基本信息并提供手动刷新数据等功能.
 * 
 * @author xzj
 */
public class DBDataListViewActivity extends ActivityRoot 
{
	private List<Map<String,String>> mData;
	private UpdateListAdapter simpleAdapter;
	
	@Override protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		//设定自定义标题栏（设定此值即意味着开启自定义标题栏的使用，必须要在setContentView前设定）
		customeTitleBarResId = R.id.sqllite_cache_total_list_titleBar;
		//养成良好习惯：首先设置主layout，确保后绪的操作中使用到的组件都可以被find到
		this.setContentView(R.layout.sqllite_cache_view_list);
		
		simpleAdapter=new UpdateListAdapter();
		((ListView)this.findViewById(R.id.sqllite_cache_total_list_listView)).setAdapter(simpleAdapter);
		
		this.setTitle("缓存查看及刷新");
	}
	
	class UpdateListAdapter extends BaseAdapter
	{
		public UpdateListAdapter()
		{
			mData= DBDataRefreshHelper.getInstance(getApplicationContext()).getDatabaseNames();			
		}
		private LayoutInflater mInflater;
		@Override public int getCount() {
			return mData.size();
		}

		@Override public Object getItem(int position) {
			return null;
		}

		@Override public long getItemId(int position) {
			return 0;
		}

		@Override public View getView(int position, View convertView, ViewGroup parent)
		{
			Map<String, String> rowData=mData.get(position);
		
			this.mInflater = LayoutInflater.from(DBDataListViewActivity.this);
			if (convertView == null)
			{
				convertView = mInflater.inflate(R.layout.sqllite_cache_view_list_item, null);
				Button but=(Button)convertView.findViewById(R.id.update_list_item_imageButton);
				ImageClickListener imageClickListener=new ImageClickListener();
				but.setOnClickListener(imageClickListener );
				but.setTag(imageClickListener);
				convertView.setTag(rowData);
			} 
			else
			{
				convertView.setTag(rowData);
				//rowData=(Map<String, AdapterMode>)convertView.getTag();
			}
			
			((TextView)convertView.findViewById(R.id.update_list_item_tableView))
				.setText(rowData.get(TableRoot.IDENT_TABLE_SHOWNAME));
			((TextView)convertView.findViewById(R.id.data_countView))
				.setText(rowData.get(TableRoot.IDENT_TABLE_DATA_COUNT));
			((TextView)convertView.findViewById(R.id.update_timeView))
				.setText(rowData.get(TableRoot.IDENT_TABLE_UPDATE_TIME));
			((ImageClickListener)((Button)convertView.findViewById(
					R.id.update_list_item_imageButton)).getTag()).setPosition(position);
			return convertView;
		}
	}

	class ImageClickListener implements View.OnClickListener
	{
		int position=0;
		public void setPosition(int position)
		{
			this.position=position;
		}
		@Override public void onClick(View v) 
		{
			Map table=mData.get(position);
			int tableId=Integer.parseInt(table.get(TableRoot.IDENT_TABLE_ID).toString());
//			new CachesRefreshAsyncTask().execute(tableId);
		}		
	}
	
//	/**
//	 * <p>
//	 * 更新指定类型的本地缓存为最新服务端数据.
//	 * </p>
//	 */
//	private class CachesRefreshAsyncTask extends DataLoadingAsyncTask<Integer, Integer, Message>
//	{
//		public CachesRefreshAsyncTask()
//		{
//			super(DBDataListViewActivity.this, "正在更新缓存，请稍候。。。");
//		}
//		
//		/**
//		 * 在后台执行....
//		 * 
//		 * @param parems 外界传进来的参数
//		 * @return 查询结果，将传递给onPostExecute(..)方法
//		 */
//		@Override
//		protected Message doInBackground(Integer... params) 
//		{
//			//第0个元素就是要更新的缓存类型
//			return DBDataRefreshHelper.updateData(params[0], context);
//		}
//		
//		protected void onPostExecuteImpl(Object result)
//		{
//			//刷新listAdapter的数据
//			mData = DBDataRefreshHelper.getInstance(getApplicationContext()).getDatabaseNames();
//			simpleAdapter.notifyDataSetChanged();
//			
//			//对缓存更新完成后的message进行处理，返回值what具体意义参见方法注释
//			int what = DBDataRefreshHelper.handlerUpdateDataResult(context, (Message)result);
//		}
//	}
}
