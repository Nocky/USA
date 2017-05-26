package com.linkloving.rtring_c_watch.logic.reportday;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linkloving.rtring_c_watch.R;

public class TextCompositionView extends LinearLayout
{
	private ImageView viewImg = null;
	/** 描述文本框 */
	private TextView viewDesc = null;
	/** 内容文本框 */
	private TextView viewContent = null;
	/** 单位文本框 */
	private TextView viewUnit = null;

	public TextCompositionView(Context context)
	{
		this(context, null);
	}

	public TextCompositionView(final Context context, AttributeSet attrs)
	{
		super(context, attrs);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.sport_item);
		int mId = a.getResourceId(R.styleable.sport_item_icon, 0);
		int mColor = a.getColor(R.styleable.sport_item_textColor, 0);
		int iconTextId = a.getResourceId(R.styleable.sport_item_iconText, 0);

		LayoutInflater.from(context).inflate(R.layout.reportday_composition_view, this, true);
		viewDesc = (TextView) this.findViewById(R.id.reportday_composition_view_descView);
		viewContent = (TextView) this.findViewById(R.id.reportday_composition_view_contentView);
		viewContent.setTextColor(mColor);
		viewUnit = (TextView) this.findViewById(R.id.reportday_composition_view_unitView);
		viewImg = (ImageView) this.findViewById(R.id.reportday_composition_view_icon);
		
		if (mId > 0)
		{
			viewImg.setVisibility(View.VISIBLE);
			viewImg.setBackgroundResource(mId);
		}

		if (iconTextId > 0)
		{
			viewDesc.setText(iconTextId);
		}
		a.recycle();
	}

	public void setText(String desc, String content, String unit, int contentColor, int unitColor)
	{
		viewContent.setText(content);
		viewUnit.setText(unit);
		viewContent.setTextColor(contentColor);
		viewUnit.setTextColor(unitColor);
	}

	// public void setText(String desc, String content, String unit, int
	// contentColor_resid, int unitColor_resid)
	// {
	// viewDesc.setText(desc);
	// viewContent.setText(content);
	// viewUnit.setText(unit);
	// // this.getContext().getResources().getColor(id)
	// viewContent.setTextColor(this.getContext().getResources().getColor(contentColor_resid));//contentColor);
	// viewUnit.setTextColor(this.getContext().getResources().getColor(unitColor_resid));//contentColor);
	// //
	// viewContent.setTextColor(this.getContext().getResources().getColor(contentColor));//contentColor);
	// }

	public void setText(String content)
	{
		viewContent.setText(content);
	}

	public TextView getViewUnit()
	{
		return viewUnit;
	}

	public void setViewUnit(TextView viewUnit)
	{
		this.viewUnit = viewUnit;
	}
	
	public void setUnit(String  unit)
	{
		viewUnit.setText(unit);
	}

}
