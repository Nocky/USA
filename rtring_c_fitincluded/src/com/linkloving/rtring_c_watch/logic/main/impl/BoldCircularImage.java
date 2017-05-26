package com.linkloving.rtring_c_watch.logic.main.impl;

import com.linkloving.rtring_c_watch.utils.ToolKits;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

public class BoldCircularImage extends CircularImage
{
	
	private int borderWidth;
	private int borderColor = Color.rgb(250,204,202);

	public BoldCircularImage(Context paramContext,
			AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		init (paramContext);
	}

	public BoldCircularImage(Context paramContext,
			AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init (paramContext);
	}

	public BoldCircularImage(Context paramContext) {
		super(paramContext);
		 init (paramContext);
	}
	
	private void init (Context context)
	{
		borderWidth = ToolKits.dip2px(context, 2);
		invalidate();
	}
	
	public void setBorderWidth(int width)
	{
		borderWidth = width;
		invalidate();
	}
	
	public void setBorderColor(int color)
	{
		borderColor = color;
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas paramCanvas) 
	{
		super.onDraw(paramCanvas);
		drawBorder(paramCanvas, getWidth(), getHeight(), borderWidth, borderColor);
	}
	
	
	 private void drawBorder(Canvas canvas, final int width, final int height,int borderWidth,int borderColor) {  
	        if (borderWidth == 0) {  
	            return;  
	        }  
	        final Paint mBorderPaint = new Paint();  
	        mBorderPaint.setStyle(Paint.Style.STROKE);  
	        mBorderPaint.setAntiAlias(true);  
	        mBorderPaint.setColor(borderColor);  
	        mBorderPaint.setStrokeWidth(borderWidth);  
	          
	        canvas.drawCircle(width >> 1, height >> 1, (width - borderWidth+2) >> 1, mBorderPaint);  
	    }  
}
