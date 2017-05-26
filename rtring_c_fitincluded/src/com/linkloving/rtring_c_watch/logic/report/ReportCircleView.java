package com.linkloving.rtring_c_watch.logic.report;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.logic.main.impl.TodayCircleView;
import com.linkloving.rtring_c_watch.utils.ToolKits;

/**
 * 报表页中要用到的饼状图.
 * <p>
 * 为了追求极致的视觉效果，本饼图使用的是图片裁切来实现饼图的绘制，而非传统2D直接绘制图形元素。
 * <p>
 * <b>重要说明：</b>为了及时释放内存，请在退出时显示调用 {@link #recycleBitmap()}方法。
 * 
 * @author Jack Jiang, 2014-05-12
 * @version 1.0
 */
public class ReportCircleView extends View
{
	private final static String TAG = ReportCircleView.class.getSimpleName();

	private Paint mPaint;
	
	private Bitmap _mbmRunBg = null;
	private Bitmap _mbmRunActiveBg = null;
	private Bitmap _mbmWorkBg = null;
	private Bitmap _mbmWorkActiveBg = null;
	private Bitmap _mbmSleepBg = null;
	private Bitmap _mbmSleepActiveBg = null;
	private Bitmap _mbmOtherBg = null;
	private Bitmap _mbmOtherActiveBg = null;

	/** 环占的百分比:0.0f~1.0f的百分比值 */
	private float percentRun = 0.0f;
	private float percentWork = 0.0f;
	private float percentSleep = 0.0f;
	private float percentOther = 1.0f;
	
	private boolean runActive = false;
	private boolean workActive = false;
	private boolean sleepActive = true;
	private boolean otherActive = false;

	public ReportCircleView(Context context)
	{
		this(context, null);
	}
	public ReportCircleView(Context context, AttributeSet attrs) 
	{
		this(context, attrs, 0);
	}
	public ReportCircleView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	@Override
	protected void onDraw(Canvas canvas) 
	{
		super.onDraw(canvas);

		Log.d(TAG, "width="+this.getWidth()+" height="+this.getHeight()
				+", mesureWidth="+this.getMeasuredWidth()+" mesureHeight="+this.getMeasuredHeight());

		Bitmap bitmapRunBg = getBmRunBg(runActive);
		Bitmap bitmapWorkBg = getBmWorkBg(workActive);
		Bitmap bitmapSleepBg = getBmSleepBg(sleepActive);
		Bitmap bitmapOtherBg = getBmOtherBg(otherActive);

		// 176dp是布局中的宽度（参见main_page_activity.xml中@+id/main_page_activity_preview_circleView
		// ），此处dip2px目的是进行据分辨率进行自动适配大小
		float width = ToolKits.dip2px(this.getContext(), 124.5f);
		// 176dp是布局中的宽度（参见main_page_activity.xml中@+id/main_page_activity_preview_circleView
		// ），此处dip2px目的是进行据分辨率进行自动适配大小
		float height = ToolKits.dip2px(this.getContext(), 126.0f);

		// 【第一步】绘制跑步圆环
		float startDegree = 0.0f;
		float endDegree = (360.0f * percentRun);
		canvas.save();
		// 已解决问题：“NullPointerException Canvas.throwIfRecycled”
		// 参见http://www.nowherenearithaca.com/2011/06/solved-bizarre-null-pointer-thrown-in.html
		//canvas.drawBitmap(bitmapCircleGray, 0, 0, mPaint);// 此实现会导致“NullPointerException Canvas.throwIfRecycled”问题
//		canvas.drawBitmap(bitmapRunBg, null, new RectF(0, 0, width, height), mPaint);
		// 整备好cavas的扇形裁切区
		TodayCircleView.getSectorClip(canvas, width/2, height/2
				, (int)Math.sqrt(width * width + width * width)/2
				, startDegree, endDegree);
		// 在扇形中绘制图片
		canvas.drawBitmap(bitmapRunBg, null, new RectF(0, 0, width, height), mPaint);
		// 扇形裁切完成后重置绘制区
		canvas.restore();

		// 【第二步】绘制走路圆环
		startDegree = endDegree;
		endDegree = endDegree + (360.0f * percentWork);
		canvas.save();
		// 整备好cavas的扇形裁切区
		TodayCircleView.getSectorClip(canvas, width/2, height/2
				, (int)Math.sqrt(width * width + width * width)/2
				, startDegree, endDegree);
		// 在扇形中绘制图片
		canvas.drawBitmap(bitmapWorkBg, null, new RectF(0, 0, width, height), mPaint);
		// 扇形裁切完成后重置绘制区
		canvas.restore();

		// 【第三步】绘制睡眠圆环
		startDegree = endDegree;
		endDegree = endDegree + (360.0f * percentSleep);
		canvas.save();
		// 整备好cavas的扇形裁切区
		TodayCircleView.getSectorClip(canvas, width/2, height/2
				, (int)Math.sqrt(width * width + width * width)/2
				, startDegree, endDegree);
		// 在扇形中绘制图片
		canvas.drawBitmap(bitmapSleepBg, null, new RectF(0, 0, width, height), mPaint);
		// 扇形裁切完成后重置绘制区
		canvas.restore();

		// 【第四步】绘制其它圆环
		startDegree = endDegree;
		endDegree = endDegree + (360.0f * percentOther);
		canvas.save();
		// 整备好cavas的扇形裁切区
		TodayCircleView.getSectorClip(canvas, width/2, height/2
				, (int)Math.sqrt(width * width + width * width)/2
				, startDegree, endDegree);
		// 在扇形中绘制图片
		canvas.drawBitmap(bitmapOtherBg, null, new RectF(0, 0, width, height), mPaint);
		// 扇形裁切完成后重置绘制区
		canvas.restore();
		
	}

	private Bitmap getBmRunBg(boolean active)
	{
		if(active)
		{
			if(_mbmRunActiveBg  == null)
				_mbmRunActiveBg  = BitmapFactory.decodeResource(this.getContext().getResources()
						, R.drawable.main_report_circle_view_runactive_bg_img);
		}
		else
		{
			if(_mbmRunBg  == null)
				_mbmRunBg  = BitmapFactory.decodeResource(this.getContext().getResources()
						, R.drawable.main_report_circle_view_run_bg_img);
		}
		
		return active?_mbmRunActiveBg:_mbmRunBg ;
	}
	private Bitmap getBmWorkBg(boolean active)
	{
		if(active)
		{
			if(_mbmWorkActiveBg  == null)
				_mbmWorkActiveBg  = BitmapFactory.decodeResource(this.getContext().getResources()
						, R.drawable.main_report_circle_view_workactive_bg_img);
		}
		else
		{
			if(_mbmWorkBg   == null)
				_mbmWorkBg   = BitmapFactory.decodeResource(this.getContext().getResources()
						, R.drawable.main_report_circle_view_work_bg_img);
		}
		
		return active?_mbmWorkActiveBg :_mbmWorkBg  ;
	}
	private Bitmap getBmSleepBg(boolean active)
	{
		if(active)
		{
			if(_mbmSleepActiveBg  == null)
				_mbmSleepActiveBg  = BitmapFactory.decodeResource(this.getContext().getResources()
						, R.drawable.main_report_circle_view_sleepactive_bg_img);
		}
		else
		{
			if(_mbmSleepBg   == null)
				_mbmSleepBg   = BitmapFactory.decodeResource(this.getContext().getResources()
						, R.drawable.main_report_circle_view_sleep_bg_img);
		}
		
		return active?_mbmSleepActiveBg :_mbmSleepBg  ;
	}
	private Bitmap getBmOtherBg(boolean active)
	{
		if(active)
		{
			if(_mbmOtherActiveBg  == null)
				_mbmOtherActiveBg  = BitmapFactory.decodeResource(this.getContext().getResources()
						, R.drawable.main_report_circle_view_otheractive_bg_img);
		}
		else
		{
			if(_mbmOtherBg   == null)
				_mbmOtherBg   = BitmapFactory.decodeResource(this.getContext().getResources()
						, R.drawable.main_report_circle_view_other_bg_img);
		}
		
		return active?_mbmOtherActiveBg :_mbmOtherBg  ;
	}

	public ReportCircleView recycleBitmap()
	{
		if(_mbmRunBg != null && !_mbmRunBg.isRecycled())
			_mbmRunBg.recycle();
		if(_mbmRunActiveBg != null && !_mbmRunActiveBg.isRecycled())
			_mbmRunActiveBg .recycle();
		if(_mbmWorkBg != null && !_mbmWorkBg.isRecycled())
			_mbmWorkBg.recycle();
		if(_mbmWorkActiveBg != null && !_mbmWorkActiveBg.isRecycled())
			_mbmWorkActiveBg.recycle();
		if(_mbmSleepBg != null && !_mbmSleepBg.isRecycled())
			_mbmSleepBg.recycle();
		if(_mbmSleepActiveBg != null && !_mbmSleepActiveBg.isRecycled())
			_mbmSleepActiveBg.recycle();
		if(_mbmOtherBg != null && !_mbmOtherBg.isRecycled())
			_mbmOtherBg.recycle();
		if(_mbmOtherActiveBg != null && !_mbmOtherActiveBg.isRecycled())
			_mbmOtherActiveBg.recycle();
		return this;
	}
	
	public ReportCircleView clearActive()
	{
		runActive = false;
		workActive = false;
		sleepActive = false;
		otherActive = false;
		
		// 通知ui进行重绘
		this.invalidate();
		return this;
	}

	public boolean isRunActive()
	{
		return runActive;
	}
	public ReportCircleView setRunActive(boolean runActive)
	{
		this.runActive = runActive;
		
		// 通知ui进行重绘
		this.invalidate();
		return this;
	}

	public boolean isWorkActive()
	{
		return workActive;
	}
	public ReportCircleView setWorkActive(boolean workActive)
	{
		this.workActive = workActive;
		
		// 通知ui进行重绘
		this.invalidate();
		return this;
	}

	public boolean isSleepActive()
	{
		return sleepActive;
	}
	public ReportCircleView setSleepActive(boolean sleepActive)
	{
		this.sleepActive = sleepActive;
		
		// 通知ui进行重绘
		this.invalidate();
		return this;
	}

	public boolean isOtherActive()
	{
		return otherActive;
	}
	public ReportCircleView setOtherActive(boolean otherActive)
	{
		this.otherActive = otherActive;
		
		// 通知ui进行重绘
		this.invalidate();
		return this;
	}

	/**
	 * 设置饼图各数据的百分比.
	 * <p>
	 * <u>请调用者保证所有参数之和必须是1.</u>
	 * <p>
	 * 本方法的调用会自动刷新ui，您无需再手动刷新之.
	 * 
	 * @param runPercentIn1 跑步占比（0~1.0f的值）
	 * @param workPercentIn1 走路占比（0~1.0f的值）
	 * @param sleepPercentIn1 睡眠占比（0~1.0f的值）
	 * @param otherPercentIn1 其它占比（0~1.0f的值）
	 * @see #invalidate()
	 */
	public ReportCircleView setPercent(float runPercentIn1
			, float workPercentIn1, float sleepPercentIn1, float otherPercentIn1)
	{
		percentRun = runPercentIn1;
		percentWork = workPercentIn1;
		percentSleep = sleepPercentIn1;
		percentOther = otherPercentIn1;
		
		// 通知ui进行重绘
		this.invalidate();
		return this;
	}
}
