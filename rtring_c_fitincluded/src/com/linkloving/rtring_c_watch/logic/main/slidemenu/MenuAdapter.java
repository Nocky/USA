package com.linkloving.rtring_c_watch.logic.main.slidemenu;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.logic.main.slidemenu.CommonAdapter.MenuVO;
import com.linkloving.rtring_c_watch.utils.ToolKits;

public class MenuAdapter extends CommonAdapter<MenuVO> 
{
	public class ViewHolder
	{
		public TextView itemName;
		public ImageView itemImg;
		public TextView unread;
	}
	
	private ViewHolder holder;
	
	private Context mContext;
	
	public MenuAdapter(Context context, List<MenuVO> list) 
	{
		super(context, list);
		mContext = context;
	}

	@Override
	protected View noConvertView(int position, View convertView,
			ViewGroup parent)
	{
		convertView = inflater.inflate(R.layout.list_item_menu, parent,false);
		holder = new ViewHolder();
		holder.itemImg = (ImageView) convertView.findViewById(R.id.fragment_item_img);
		holder.itemName = (TextView) convertView.findViewById(R.id.fragment_item_text);
		holder.unread = (TextView) convertView.findViewById(R.id.fragment_item_unread_text);
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
		holder.itemImg.setBackgroundResource(list.get(position).getImgID());
		holder.itemName.setText(list.get(position).getTextID());
//		if(position == PortalMenuFragment.MenuIndex.SPORT.ordinal())
//		{
//			holder.itemName.setTextColor(mContext.getResources().getColor(R.color.menu_sport_data_text_bg));
//		}
//		else
//		{
			holder.itemName.setTextColor(Color.WHITE);
//		}
		
		if(position == PortalMenuFragment.MenuIndex.RELATIONSHIP.ordinal())
		{
			   int num =  MyApplication.getInstance(mContext).getCommentNum();
			   if(num > 0)
			   {
					holder.unread.setVisibility(View.VISIBLE);
					holder.unread.setText(ToolKits.getUnreadString(num));
			   }
			   else 
			   {
					holder.unread.setVisibility(View.GONE);
			   }
		}
		else
		{
			
		}
		return convertView;
	}
	

}
