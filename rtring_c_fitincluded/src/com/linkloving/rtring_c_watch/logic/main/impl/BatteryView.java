package com.linkloving.rtring_c_watch.logic.main.impl;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class BatteryView extends View
{
	private Context mContext;
	
	public BatteryView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		mContext = context;
	}

	public BatteryView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		mContext = context;
	}

	public BatteryView(Context context)
	{
		super(context);
		mContext = context;
	}
	
	
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		/*super.onDraw(canvas);
		Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), )
		canvas.drawbi*/
	}

}
