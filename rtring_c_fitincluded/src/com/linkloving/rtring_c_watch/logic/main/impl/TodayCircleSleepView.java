package com.linkloving.rtring_c_watch.logic.main.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;

import com.linkloving.rtring_c_watch.R;

/**
 * 首页 - 睡眠数据圆环图View实现类.
 * 
 * @author Jack Jiang, 2014-05
 * @version 1.0
 */
public class TodayCircleSleepView extends TodayCircleView
{
	
	public TodayCircleSleepView(Context context)
	{
		super(context, null);
	}

	public TodayCircleSleepView(Context context, AttributeSet attrs) 
	{
		super(context, attrs, 0);
	}

	public TodayCircleSleepView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}
	
	@Override
	protected int getPercentColor()
	{
		return Color.rgb(21, 77, 100);
	}
	
	// 睡眠里没有百分比的概念，它相当天一个饼图，所以不需要箭头
	@Override
	protected void drawRightArrow(Canvas canvas, int width, Bitmap bitmapRightArrow)
    {
		// do nothing
    }
	
	protected Bitmap getBmCircleGray()
    {
    	if(_mbmCircleGray == null)
    		_mbmCircleGray = BitmapFactory.decodeResource(this.getContext().getResources()
    				, R.drawable.main_circle_view_bgcircle1);
    	return _mbmCircleGray;
    }
    protected Bitmap getBmCircleGreen()
    {
    	if(_mbmCircleGreen == null)
    		_mbmCircleGreen = BitmapFactory.decodeResource(this.getContext().getResources()
    				, R.drawable.main_circle_view_sleep_bgcircle2);
    	return _mbmCircleGreen;
    }
    protected Bitmap getBmCircleTop()
    {
    	if(_mbmCircleTop == null)
    		_mbmCircleTop = BitmapFactory.decodeResource(this.getContext().getResources()
    				, R.drawable.main_circle_view_sleep_bgcircle3);
    	return _mbmCircleTop;
    }

}
