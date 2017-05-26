package com.linkloving.rtring_c_watch.logic.reportday;
//package com.linkloving.rtring_c.logic.reportday;
//
//import java.sql.Date;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collection;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.GregorianCalendar;
//import java.util.List;
//
//import m.framework.utils.Data;
//
//import com.linkloving.rtring_c.R;
//import com.rtring.buiness.logic.ends.sleep2.DLPSportData;
//import com.rtring.buiness.logic.ends.sleep2.SleepAnalyzer;
//
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Paint.Style;
//import android.graphics.Path;
//import android.graphics.Point;
//import android.graphics.Rect;
//import android.graphics.RectF;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.View;
//
///**
// * 旧图表
// * @author Administrator
// *
// */
//public class DetailChartView extends View
//{
//	private static final String TAG = DetailChartView.class.getSimpleName();
//	
//	/** 曲线颜色*/
//	private int lineColor = Color.parseColor("#92B7BB");
//	/** 深睡矩形颜色*/
//	private int deepSleepColor = Color.parseColor("#3B01B8");
//	/** 浅睡矩形颜色*/
//	private int lightSleepColor = Color.parseColor("#BAD0EA"); 
//	
//	private int underLineColor = Color.parseColor("#92B7BB");
//	
//	/** 时间片基数*/
//	private int baseSlice = 60*60;
//	
//	/** 画布的尺寸*/
//	private int mHeight;
//	private int mWidth;
//	
//	/**曲线宽度*/
//	private int lineWidth = 10;
//	
//	/** 底部基线*/
//	private int underLineWidth = 10;
//	
//	/**矩形高度*/
//	private int deepSleepHeight = 100;
//	private int lightSleepHeight = 50;
//	
//	/** 画笔*/
//	private Paint linePaint;
//	private Paint deepSleepPaint;
//	private Paint lightSleepPaint;
//	private Paint underLinePaint;
//	
//	/** 绘图数据*/
//	private List<RectF> deepSleepColumns = new ArrayList<RectF>();
//	private List<RectF> lightSleepColumns = new ArrayList<RectF>();
//	private List<DPoint> curvePoints = new ArrayList<DPoint>();
//
//	public DetailChartView(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle);
//		 init(context,attrs);
//	}
//
//	public DetailChartView(Context context, AttributeSet attrs) {
//		super(context, attrs);
//		 init(context,attrs);
//	}
//
//	public DetailChartView(Context context) {
//		super(context);
//		 init(context,null);
//	}
//	
//	private void init(Context context,AttributeSet attrs)
//	{
//		if(attrs != null)
//		{
//			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.chart_view);  
//			lineColor = a.getColor(R.styleable.chart_view_lineColor, Color.parseColor("#92B7BB"));
//			deepSleepColor = a.getColor(R.styleable.chart_view_deepSleepColor, Color.parseColor("#3B01B8"));
//			lightSleepColor = a.getColor(R.styleable.chart_view_lightSleepColor, Color.parseColor("#BAD0EA"));
//			
//			baseSlice = a.getInteger(R.styleable.chart_view_baseSlice, 30);
//			lineWidth = a.getDimensionPixelSize(R.styleable.chart_view_lineWidth, 10);
//			deepSleepHeight = a.getDimensionPixelSize(R.styleable.chart_view_deepSleepHeight, 100);
//			lightSleepHeight = a.getDimensionPixelSize(R.styleable.chart_view_lightSleepHeight, 50);
//			a.recycle();
//		}
//		
//		linePaint = new Paint();
//		linePaint.setAntiAlias(true);
//		linePaint.setStyle(Style.STROKE);
//		linePaint.setStrokeWidth(lineWidth);
//		linePaint.setColor(lineColor);
//		
//		deepSleepPaint = new Paint();
//		deepSleepPaint.setAntiAlias(true);
//		deepSleepPaint.setColor(deepSleepColor);
//		deepSleepPaint.setStyle(Style.FILL);
//		
//		lightSleepPaint = new Paint();
//		lightSleepPaint.setAntiAlias(true);
//		lightSleepPaint.setColor(lightSleepColor);
//		lightSleepPaint.setStyle(Style.FILL);
//		
//		underLinePaint = new Paint();
//		underLinePaint.setAntiAlias(true);
//		underLinePaint.setColor(underLineColor);
//		underLinePaint.setStyle(Style.STROKE);
//		underLinePaint.setStrokeWidth(underLineWidth);
//	}
//	
//	/**
//	 * 设置数据并绘制
//	 * @param datas  原始数据
//	 */
//	public void setDetailDatas(List<DLPSportData> datas)
//	{
//		deepSleepColumns.clear();
//		lightSleepColumns.clear();
//		curvePoints.clear();
//		
//		List<DLPSportData> sportDatas = new ArrayList<DLPSportData>();
//		List<DLPSportData> mergedData;
//		
//		for(DLPSportData data:datas)
//		{
//			if(data.getState() == SleepAnalyzer.WALKING  ||  data.getState() == SleepAnalyzer.RUNNING)
//			{
//				    sportDatas.add(data);
//			}
//			else if (data.getState() == SleepAnalyzer.DEEP_SLEEP)
//			{
//				DLPSportData unSportData = new DLPSportData();
//				unSportData.begin = data.begin + data.duration;
//				 sportDatas.add(unSportData);
//			     RectF deepSleepRect = getRectFByData(data, mWidth, mHeight, deepSleepHeight);
//			     deepSleepColumns.add(deepSleepRect);
//			}
//			else if(data.getState() == SleepAnalyzer.LIGHT_SLEEP)
//			{
//				DLPSportData unSportData = new DLPSportData();
//				unSportData.begin = data.begin + data.duration;
//				 sportDatas.add(unSportData);
//				RectF lightSleepRect = getRectFByData(data, mWidth, mHeight, lightSleepHeight);
//				lightSleepColumns.add(lightSleepRect);
//			}
//		}
//		
//		mergedData = mergeSportData(sportDatas, baseSlice);
//		int max = getMaxSteps(mergedData);
//		
//		for(DLPSportData obj:mergedData)
//		{
//			DPoint point = getPointBySportData(obj, max, mWidth, mHeight, baseSlice);
//			curvePoints.add(point);
//		}
//		sortCurePoints(curvePoints);
//		invalidate();
//	}
//	
//	
//	private void sortCurePoints( List<DPoint> data)
//	{
//		Collections.sort(data,new Comparator<Point>() {
//
//			@Override
//			public int compare(Point lhs, Point rhs)
//			{
//				return lhs.x-rhs.x;
//			}
//		});
//	}
//	
//	
//	/**
//	 * 合并运动数据
//	 * @param sportDatas   运动数据
//	 * @param baseSlice   时间片基数
//	 * @return  以给定时间片基数合并后的数据
//	 */
//	private List<DLPSportData> mergeSportData(List<DLPSportData> sportDatas,int baseSlice)
//	{
//		List<DLPSportData> preData = new ArrayList<DLPSportData>();
//		List<DLPSportData> mergedData = new ArrayList<DLPSportData>();
//		
//		if(baseSlice > 30)
//		{
//			 baseSlice = baseSlice/30*30;
//		}
//		else
//		{
//			baseSlice = 30;
//		}
//		
//		int count = 24*60*60/baseSlice;
//		preData.addAll(sportDatas);
//		for(int i = 0;i < count;i++)
//		{
//			DLPSportData tmp = new DLPSportData();
//			tmp.begin = i* baseSlice;
//			preData.add(tmp);
//		}
//		
//		for(DLPSportData sportData:preData)
//		{
//			 DLPSportData mergedItem = new DLPSportData();
//			 mergedItem.begin =getDateSeconds(sportData.begin)/baseSlice;
//			 Log.d(TAG, "sport data begin>>>>>>>>>>>>>>>>>>>>>"+sportData.begin);
//			 mergedItem.steps = sportData.steps;
//			 if(mergedItem.steps == 0)
//			 {
//				 mergedData.add(mergedItem)	;
//				 continue;
//			 }
//			 
//			 int index = hasSameTimeData(mergedData, mergedItem);
//			 if(index >= 0)
//			 {
//				 mergedData.get(index).steps = mergedData.get(index).steps + mergedItem.steps;
//			 }
//			 else
//			 {
//			     mergedData.add(mergedItem)	;
//			 }
//		}
//		return mergedData;
//	}
//	
//	private int getMaxSteps(List<DLPSportData> src)
//	{
//		int max = 0;
//		for(DLPSportData obj:src)
//		{
//			if(obj.steps > max)
//				max = obj.steps;
//		}
//	    return max;
//	}
//	
//	private int hasSameTimeData(List<DLPSportData> src,DLPSportData obj)
//	{
//		for(int i =0;i < src.size();i++)
//		{
//			if(obj.begin == src.get(i).begin)
//				return i;
//		}
//		return -1;
//	}
//	
//	/**
//	 * 将运动数据转换成绘制曲线的点
//	 * @param sportData  运动数据
//	 * @param max  运动数据最大值
//	 * @param canvasWidth  画布宽度
//	 * @param canvasHeight  画布高度
//	 * @param baseSlice 时间片基数 
//	 * @return   曲线点
//	 */
//	private DPoint getPointBySportData(DLPSportData sportData,int max,int canvasWidth,int canvasHeight,int baseSlice)
//	{
//		if(baseSlice > 30)
//		{
//			 baseSlice = baseSlice/30*30;
//		}
//		else
//		{
//			baseSlice = 30;
//		}
//		float timePart = canvasWidth/(24*60*60/(float)baseSlice);
//		int x = (int) (timePart*sportData.begin);
//		int y =(int) (canvasHeight - sportData.steps*canvasHeight*(0.9f)/max)  ;
//		Log.d(TAG, "生成绘制点>>>>>>canvasWidth:"+canvasWidth+"   canvasHeight:"+canvasHeight+"    timePart:"+timePart+"    x:"+x+"    y:"+y);
//		return new DPoint(x, y);
//	}
//	
//	/**
//	 *  从时间戳获取当日时间的秒数
//	 * @param time  时间戳（单位：秒）
//	 * @return    当日时间的秒数
//	 */
//	private int getDateSeconds(long time)
//	{
//		GregorianCalendar gc = new  GregorianCalendar();
//		gc.setTimeInMillis(time*1000);
//		int hour = gc.get(GregorianCalendar.HOUR_OF_DAY);
//		int minutes = gc.get(GregorianCalendar.MINUTE);
//		int sec = gc.get(GregorianCalendar.SECOND);
//		Log.d(TAG, "date seconds>>>>>>>>>>>>hour:"+hour+">>>>minutes:"+minutes+">>>>>>>sec"+sec);
//		return hour*60*60+minutes*60+sec;
//	}
//	
//	/**
//	 *  将数据转换成要显示的矩形
//	 * @param data  数据
//	 * @param canvasWidth  画布宽度
//	 * @param canvasHeight  画布高度
//	 * @param rectHeight   矩形的高度
//	 * @return
//	 */
//	private RectF getRectFByData(DLPSportData data,int canvasWidth,int canvasHeight,int rectHeight)
//	{
//		float timePart = canvasWidth/(float)(24*60*60);
//		
//		float left = timePart*getDateSeconds(data.begin);
//		float right = timePart*(getDateSeconds(data.begin)+data.duration);
//		float top = canvasHeight - rectHeight;
//		float bottom = canvasHeight;
//		Log.d(TAG, "生成绘矩形>>>>>>left:"+left+"   right:"+right+"    top:"+top+"    bottom:"+bottom);
//		return new RectF(left, top, right, bottom);
//	}
//	
//	@Override
//	protected void onDraw(Canvas canvas)
//	{
//		mHeight = getHeight();
//		mWidth  = getWidth();
//		Log.d(TAG, "height:"+mHeight+"   width:"+mWidth);
//		super.onDraw(canvas);
//		drawCurveLine(curvePoints, canvas, linePaint);
//		drawColumns(deepSleepColumns, canvas, deepSleepPaint);
//		drawColumns(lightSleepColumns, canvas, lightSleepPaint);
//		drawUnderLine(canvas, underLinePaint, mHeight, mWidth);
//	}
//	
//	private void drawUnderLine(Canvas canvas,Paint paint,int canvasHeight,int canvasWidth)
//	{
//		canvas.drawLine(0, canvasHeight, canvasWidth, canvasHeight, paint);
//	}
//	
//	/**
//	 * 绘制矩形
//	 * @param columns   矩形
//	 * @param canvas  画布
//	 * @param paint  画笔
//	 */
//	private void drawColumns(List<RectF>columns,Canvas canvas,Paint paint)
//	{
//		if(columns == null || columns.size() <= 0)
//		{
//			Log.e(TAG, "没有要绘制的矩形!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//			return;
//		}
//			
//		Log.d(TAG, "绘制矩形个数为>>>>>>>>>>>>>>>>>>>>>>>"+columns.size());
//		for(RectF column:columns)
//		{
//			canvas.drawRect(column, paint);
//		}
//	}
//	
//	/**
//	 * 绘制曲线
//	 * @param points 曲线点
//	 * @param canvas  画布
//	 * @param paint 画笔
//	 */
//	private void drawCurveLine(List<DPoint> points, Canvas canvas, Paint paint) 
//	{
//		if(points == null  || points.size() <= 0)
//		{
//			Log.e(TAG, "没有绘制曲线的点!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//			return;
//		}
//		    
//		Log.d(TAG, "绘制曲线点数为>>>>>>>>>>>>>>>>>>>>>>>"+points.size());
//		Path path = new Path();
//		if (points.size() > 1) {
//		    Point prevPoint = null;
//		    for (int i = 0; i < points.size(); i++) {
//		        Point point = points.get(i);
//
//		        if (i == 0) {
//		            path.moveTo(point.x, point.y);
//		        } else {
//		            float midX = (prevPoint.x + point.x) / 2;
//		            float midY = (prevPoint.y + point.y) / 2;
//
//		            if (i == 1) {
//		                path.lineTo(midX, midY);
//		            } else {
//		                path.quadTo(prevPoint.x, prevPoint.y, midX, midY);
//		            }
//		        }
//		        prevPoint = point;
//		    }
//		    path.lineTo(prevPoint.x, prevPoint.y);
//		}
//
//		canvas.drawPath(path, paint);
//	}
//	
//	
//	private void drawCurveLine2(List<DPoint> points, Canvas canvas, Paint paint)
//	{
//		  Path path = new Path();
//
//		    if(points.size() > 1){
//		        for(int i = points.size() - 2; i < points.size(); i++){
//		            if(i >= 0){
//		            	DPoint point = points.get(i);
//
//		                if(i == 0){
//		                	Point next = points.get(i + 1);
//		                    point.dx = ((next.x - point.x) / 3.0f);
//		                    point.dy = ((next.y - point.y) / 3.0f);
//		                }
//		                else if(i == points.size() - 1){
//		                	Point prev = points.get(i - 1);
//		                    point.dx = ((point.x - prev.x) / 3.0f);
//		                    point.dy = ((point.y - prev.y) / 3.0f);
//		                }
//		                else{
//		                    Point next = points.get(i + 1);
//		                    Point prev = points.get(i - 1);
//		                    point.dx = ((next.x - prev.x) / 3.0f);
//		                    point.dy = ((next.y - prev.y) / 3.0f);
//		                }
//		            }
//		        }
//		    }
//
//		    boolean first = true;
//		    for(int i = 0; i < points.size(); i++){
//		    	DPoint point = points.get(i);
//		        if(first){
//		            first = false;
//		            path.moveTo(point.x, point.y);
//		        }
//		        else{
//		        	DPoint prev = points.get(i - 1);
//		            path.cubicTo(prev.x + prev.dx, prev.y + prev.dy, point.x - point.dx, point.y - point.dy, point.x, point.y);
//		        }
//		    }
//		    canvas.drawPath(path, paint);
//
//	}
//	
//	class DPoint extends Point
//	{
//		float dx,dy;
//		public DPoint(int x,int y)
//		{
//			super(x,y);
//		}
//	}
//}
