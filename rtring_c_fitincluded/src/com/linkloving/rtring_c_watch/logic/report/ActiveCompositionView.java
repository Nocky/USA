package com.linkloving.rtring_c_watch.logic.report;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linkloving.rtring_c_watch.R;

public class ActiveCompositionView extends LinearLayout
{
	/** 描述文本框 */
	private TextView viewDesc = null;
	/** 内容文本框 */
	private TextView viewContent = null;
	/** 单位文本框 */
	private TextView viewUnit = null;
	
	public ActiveCompositionView(Context context)
	{
		this(context, null);
	}
	public ActiveCompositionView(final Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		LayoutInflater.from(context).inflate(R.layout.report_page_active_composition_view,this,true);
		viewDesc = (TextView)this.findViewById(R.id.report_page_active_composition_view_descView);
		viewContent = (TextView)this.findViewById(R.id.report_page_active_composition_view_contentView);
		viewUnit = (TextView)this.findViewById(R.id.report_page_active_composition_view_unitView);
	}
	
	public void setText(String desc, String content, String unit, int descColor)
	{
		viewDesc.setText(desc);
		viewContent.setText(content);
		viewUnit.setText(unit);
//		this.getContext().getResources().getColor(id)
		viewDesc.setTextColor(this.getContext().getResources().getColor(descColor));//contentColor);
//		viewContent.setTextColor(this.getContext().getResources().getColor(contentColor));//contentColor);
	}
	
}
