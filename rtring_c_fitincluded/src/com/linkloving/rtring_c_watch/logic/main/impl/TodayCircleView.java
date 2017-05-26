package com.linkloving.rtring_c_watch.logic.main.impl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.utils.ToolKits;

/**
 * 首页 - 运动数据圆环图View实现类.
 * 
 * @author Jack Jiang, 2014-05
 */
public class TodayCircleView extends View
{
	private final static String TAG = TodayCircleView.class.getSimpleName();
	
	private Paint mPaint;
	private Paint circleGreenPaint;
	
	protected Bitmap _mbmCircleGray = null;
	protected Bitmap _mbmCircleGreen = null;
	protected Bitmap _mbmRightArrow = null;
	protected Bitmap _mbmCircleTop = null;
	
	/** 绿色圆环占的百分比:0.0f~1.0f的百分比值 */
	private float percent = 0.0f;
	
	public TodayCircleView(Context context)
	{
		this(context, null);
	}

	public TodayCircleView(Context context, AttributeSet attrs) 
	{
		this(context, attrs, 0);
	}

	public TodayCircleView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circleGreenPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		circleGreenPaint.setColor(getPercentColor());
		circleGreenPaint.setStyle(Style.FILL);
	}
	
	protected int getPercentColor()
	{
		return Color.rgb(21, 77, 100);
	}
 
    @Override
    protected void onDraw(Canvas canvas) 
    {
        super.onDraw(canvas);

        Log.d(TAG, "width="+this.getWidth()+" height="+this.getHeight()+", mesureWidth="+this.getMeasuredWidth()+" mesureHeight="+this.getMeasuredHeight());
        int width = getWidth();
        int height = getHeight();
        
        Bitmap bitmapCircleGray = getBmCircleGray();
   //     Bitmap bitmapCircleGreen = getBmCircleGreen();
        Bitmap bitmapCircleTop = getBmCircleTop();
        Bitmap bitmapRightArrow = getBmRightArrow();
        
        // 【第一步】绘制底层灰色圆环
        // 已解决问题：“NullPointerException Canvas.throwIfRecycled”
        // 参见http://www.nowherenearithaca.com/2011/06/solved-bizarre-null-pointer-thrown-in.html
//        canvas.drawBitmap(bitmapCircleGray, 0, 0, mPaint);// 此实现会导致“NullPointerException Canvas.throwIfRecycled”问题
        canvas.drawBitmap(bitmapCircleGray, null, new RectF(0, 0, width, height), mPaint);
        
        // 【第二步】绘制上层绿色圆环
        float startDegree = 0.0f;
        float endDegree = percent >= 1? 360.0f : (360.0f * percent); // 当大于100%时，只需要绘制一圈就行了，否则扇形裁切算法好像有问题
        
        Log.e(TAG, "正在绘制：percent="+percent);
//        canvas.save();
//        // 整备好cavas的扇形裁切区
//        getSectorClip(canvas, width/2, height/2, (int)Math.sqrt(width * width + width * width)/2
//        		, startDegree, endDegree);
////        getSector(canvas, (float)Math.sqrt(width * width + width * width)/2
////        		, (double)startDegree, (double)endDegree, Region.Op.REVERSE_DIFFERENCE);
////      canvas.drawBitmap(bitmapCircleGreen, matrix, mPaint);
//        // 在扇形中绘制图片
//        canvas.drawBitmap(bitmapCircleGreen, null, new RectF(0, 0, width, height), mPaint);
////      canvas.drawBitmap(bitmapCircleGreen, 0, 0, mPaint);
//        canvas.restore();// 扇形裁切完成后重置绘制区
        //rrrrrr
        float padding = 5;
        RectF circleGreenRect = new RectF(padding, padding, width-padding, height-padding);
        float circleGreenStartDegree =  startDegree + 270;
     //   Log.d(TAG, "正在绘制circleGreen[circleGreenStartDegree:"+circleGreenStartDegree+"  circleGreenEndDegree:"+circleGreenEndDegree+"]");
        canvas.drawArc(circleGreenRect,circleGreenStartDegree,endDegree, true, circleGreenPaint);
        
        // 【第三步】绘制顶层
        canvas.drawBitmap(bitmapCircleTop, null, new RectF(0, 0, width, height), mPaint);
        
        // 【第四步】绘制白色小箭头
//        drawRightArrow(canvas, width, bitmapRightArrow);
    }
    
    protected void drawRightArrow(Canvas canvas, int width, Bitmap bitmapRightArrow)
    {
        int arrowIconWidth = ToolKits.dip2px(this.getContext(), 6);
        int arrowIconHeight = ToolKits.dip2px(this.getContext(), 7);
        int yDrawDelta = ToolKits.dip2px(this.getContext(), 12);
        float arrowStartX = width/2 + ToolKits.dip2px(this.getContext(), 2);
        float arrowStartY = 0 + yDrawDelta;
        float arrowEndX = arrowStartX + arrowIconWidth;
        float arrowEndY = yDrawDelta + arrowIconHeight;
        canvas.drawBitmap(bitmapRightArrow, null, new RectF(arrowStartX, arrowStartY, arrowEndX, arrowEndY), mPaint);
    }
    
    /** 
     * 返回一个扇形的剪裁区。
     * 
     * @param canvas 画笔 
     * @param center_X 圆心X坐标 
     * @param center_Y 圆心Y坐标 
     * @param r 半径 
     * @param startAngle 起始角度 
     * @param sweepAngle 终点角度 
     *  
     */  
    public static void getSectorClip(Canvas canvas,float center_X
    		,float center_Y,float r,float startAngle,float sweepAngle)  
    {  
    	// -90度的目的是将扇形起始边从水平逆时针改为垂直，以便达到UI设计要求
    	startAngle = -90 + startAngle;
    	// -90度的目的是将扇形结束边逆时针旋转90度，以便达到UI设计要求
    	sweepAngle = -90 + sweepAngle;

    	Path path = new Path();  
    	//下面是获得一个三角形的剪裁区  
    	path.moveTo(center_X, center_Y);  //圆心  
    	path.lineTo((float)(center_X+r*Math.cos(startAngle* Math.PI / 180)),   //起始点角度在圆上对应的横坐标  
    			(float)(center_Y+r*Math.sin(startAngle* Math.PI / 180)));    //起始点角度在圆上对应的纵坐标  
    	path.lineTo((float)(center_X+r*Math.cos(sweepAngle* Math.PI / 180)),  //终点角度在圆上对应的横坐标  
    			(float)(center_Y+r*Math.sin(sweepAngle* Math.PI / 180)));   //终点点角度在圆上对应的纵坐标  
    	path.close();  
    	//     //设置一个正方形，内切圆  
    	RectF rectF = new RectF(center_X-r,center_Y-r,center_X+r,center_Y+r);  
    	//下面是获得弧形剪裁区的方法      
    	path.addArc(rectF, startAngle, sweepAngle - startAngle);   

    	Log.e(TAG, "==========rectF="+rectF+", startAngle="+startAngle+"|sweepAngle="+sweepAngle);

    	canvas.clipPath(path);  
    }  

    protected Bitmap getBmCircleGray()
    {
    	if(_mbmCircleGray == null)
    		_mbmCircleGray = BitmapFactory.decodeResource(this.getContext().getResources()
    				, R.drawable.main_circle_view_bgcircle1);
    	return _mbmCircleGray;
    }
//    protected Bitmap getBmCircleGreen()
//    {
//    	if(_mbmCircleGreen == null)
//    		_mbmCircleGreen = BitmapFactory.decodeResource(this.getContext().getResources()
//    				, R.drawable.main_circle_view_bgcircle2);
//    	return _mbmCircleGreen;
//    }
    protected Bitmap getBmCircleTop()
    {
    	if(_mbmCircleTop == null)
    		_mbmCircleTop = BitmapFactory.decodeResource(this.getContext().getResources()
    				, R.drawable.main_circle_view_sleep_bgcircle3);
    	return _mbmCircleTop;
    }
    protected Bitmap getBmRightArrow()
    {
    	if(_mbmRightArrow == null)
    		_mbmRightArrow = BitmapFactory.decodeResource(this.getContext().getResources()
    				, R.drawable.main_circle_view_rightarrow);
    	return _mbmRightArrow;
    }

    public float getPercent()
    {
    	return percent;
    }
    public void setPercent(float percent)
    {
    	this.percent = percent;
    	this.invalidate();
    }

//    public void repaint()
//    {
//    	setPercent(this.percent);
//    }

    public void recycleBitmap()
    {
    	if(_mbmCircleGray != null && !_mbmCircleGray.isRecycled())
    		_mbmCircleGray.recycle();
    	if(_mbmCircleGreen != null && !_mbmCircleGreen.isRecycled())
    		_mbmCircleGreen.recycle();
    	if(_mbmRightArrow != null && !_mbmRightArrow.isRecycled())
    		_mbmRightArrow.recycle();
    	if(_mbmCircleTop != null && !_mbmCircleTop.isRecycled())
    		_mbmCircleTop.recycle();
    }
}
