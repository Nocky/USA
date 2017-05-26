package com.linkloving.rtring_c_watch.logic.main.impl;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.logic.main.impl.SportDataAdapter.SportDataItemVO;
import com.linkloving.rtring_c_watch.logic.main.slidemenu.CommonAdapter;
import com.linkloving.rtring_c_watch.utils.ToolKits;

public class SportDataAdapter extends CommonAdapter<SportDataItemVO> {

	public class ViewHolder
	{
		public TextView title;
		public TextView data;
		public TextView unit;
		public ImageView select;
	}
	
	private ViewHolder holder;
	
	private GridView mGridView;
	
	private Context mContext;
	
	public SportDataAdapter(Context context, List<SportDataItemVO> list,GridView mGridView) {
		super(context, list);
		mContext = context;
		this.mGridView = mGridView;
	}

	@Override
	protected View noConvertView(int position, View convertView,
			ViewGroup parent) {
		holder = new ViewHolder();
		convertView = inflater.inflate(R.layout.grid_item_sport_data,null);
	  
		holder.title = (TextView) convertView.findViewById(R.id.sport_data_item_title);
		holder.data = (TextView) convertView.findViewById(R.id.sport_data_item_data);
		holder.unit = (TextView) convertView.findViewById(R.id.sport_data_item_unit);
		holder.select = (ImageView) convertView.findViewById(R.id.sport_data_item_select);
		convertView.setTag(holder);
		return convertView;
	}

	@Override
	protected View hasConvertView(int position, View convertView,
			ViewGroup parent) {
		holder = (ViewHolder) convertView.getTag();
		return convertView;
	}

	@Override
	protected View initConvertView(int position, View convertView,
			ViewGroup parent) 
	{
	    int height = mGridView.getHeight();
		AbsListView.LayoutParams params = new AbsListView.LayoutParams(LayoutParams.FILL_PARENT, (height-ToolKits.dip2px(mContext,(float)3.5))/3);
		convertView.setLayoutParams(params);
		holder.title.setText(list.get(position).getSportDataTitle());
		if(list.get(position).getValueType() < 0)
		{
			holder.data.setText((int)list.get(position).getSportData()+"");
		}
		else
		{
			holder.data.setText(list.get(position).getSportData()+"");
		}
		
		holder.unit.setText(list.get(position).getSportDataUnit());
		
		if(selectEnable && isSelect(position))
		{
			holder.select.setVisibility(View.VISIBLE);
		}
		else
		{
			holder.select.setVisibility(View.GONE);
		}
		
		
		// FIXME : 不要使用汉字名称作为类型判断条件，单独加一个type字段吧！！
		if(list.get(position).getSportDataTitle().equals("跑步量"))
		{
			holder.data.setTextColor(Color.YELLOW);
			holder.title.setTextColor(Color.YELLOW);
			holder.unit.setTextColor(Color.YELLOW);
		}
		else if (list.get(position).getSportDataTitle().equals("跑步速度")) 
		{
			holder.data.setTextColor(Color.GREEN);
			holder.title.setTextColor(Color.GREEN);
			holder.unit.setTextColor(Color.GREEN);
		}
		else if (list.get(position).getSportDataTitle().equals("跑步能量消耗"))
		{
			holder.data.setTextColor(Color.RED);
			holder.title.setTextColor(Color.RED);
			holder.unit.setTextColor(Color.RED);
		}
		else if (list.get(position).getSportDataTitle().equals("深睡眠"))
		{
			holder.data.setTextColor(Color.WHITE);
			holder.title.setTextColor(Color.WHITE);
			holder.unit.setTextColor(Color.WHITE);
			holder.data.setText("--");// TODO 睡眠算法目前未实现，所以显示时就不要显示数居了！
		}
		else if (list.get(position).getSportDataTitle().equals("浅睡眠"))
		{
			holder.data.setTextColor(Color.WHITE);
			holder.title.setTextColor(Color.WHITE);
			holder.unit.setTextColor(Color.WHITE);
			holder.data.setText("--");// TODO 睡眠算法目前未实现，所以显示时就不要显示数居了！
		}
		else
		{
			holder.data.setTextColor(Color.WHITE);
			holder.title.setTextColor(Color.WHITE);
			holder.unit.setTextColor(Color.WHITE);
		}
		
		return convertView;
	}
	
	public static class SportDataItemVO 
	{
		private int valueType;
		
		private String sportDataTitle;
		
		private float sportData;
		
		private String sportDataUnit;
		
		

		public int getValueType() {
			return valueType;
		}

		public void setValueType(int valueType) {
			this.valueType = valueType;
		}

		public String getSportDataTitle() {
			return sportDataTitle;
		}

		public void setSportDataTitle(String sportDataTitle) {
			this.sportDataTitle = sportDataTitle;
		}

		public float getSportData() {
			return sportData;
		}

		public void setSportData(float sportData) {
			this.sportData = sportData;
		}

		public String getSportDataUnit() {
			return sportDataUnit;
		}

		public void setSportDataUnit(String sportDataUnit) {
			this.sportDataUnit = sportDataUnit;
		}
		
	}

}
