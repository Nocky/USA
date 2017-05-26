package com.linkloving.rtring_c_watch.logic.reportday;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.util.Log;

import com.linkloving.band.ui.BRDetailData;


/**
 * 图表图片生成类
 * @author Administrator
 *
 */
public class DetailBitmapCreator {
	private final String TAG = DetailBitmapCreator.class.getSimpleName();
	
	private ChartParameter chartParameter;
	private int beginTime;
	
	public DetailBitmapCreator(Context context){
		
	}
	
	public void initChartParameter(ChartParameter chartParameter){
		this.chartParameter = chartParameter;
	}
	
	public Bitmap drawDetailChart(List<BRDetailData> detailData, int dayInex)
	{		
		Bitmap chartBitmap = Bitmap.createBitmap(chartParameter.getWidth(),chartParameter.getHeight(),Bitmap.Config.ARGB_4444);
//	    Log.d(TAG, "chartBitmap size:"+chartBitmap.getByteCount()/1024+"kb");
//	    Log.d(TAG,"Bitmap width:"+chartParameter.getWidth()+"   Bitmap height:"+ chartParameter.getHeight());
		Canvas canvas = new Canvas(chartBitmap);
		
		/*
		Paint paint = new Paint();
		paint.setColor(Color.RED);	
		paint.setStrokeWidth(3);
		canvas.drawLine(0, 0, 0, chartParameter.getHeight(), paint);
		
		Paint textpaint = new Paint();
		textpaint.setTextSize(35);//设置字体大小		
		textpaint.setColor(Color.RED);
		canvas.drawText(TimeUtil.formatDateMD(TimeUtil.getDateByDay(dayInex)), 5, 30, textpaint);
		*/
		
		Paint rectPaint = new Paint();
		rectPaint.setColor(Color.rgb(0x00, 0x00, 0x00)); //#FF9F18 #5CC8EF
		rectPaint.setStyle(Style.FILL);
		canvas.drawRect(0, chartParameter.getChartHeight(),
				chartParameter.getWidth(), chartParameter.getHeight(), rectPaint);
		
		if(detailData != null && detailData.size() > 0){
			List<AnylyzeResult> anylyzeResult = anylyzeDetailData(detailData);		
			for(AnylyzeResult result : anylyzeResult){
				if(result.isSleep()){
					drawSleepChart(detailData,canvas, result.getBeginIndex(), result.getEndIndex());
				}else{
					drawSportChartRect(detailData,canvas, result.getBeginIndex(), result.getEndIndex());
				}
			}			
		}
		return chartBitmap;
	}
	
	private List<AnylyzeResult> anylyzeDetailData(List<BRDetailData> detailData){	
		List<AnylyzeResult> anylyzeResult = new ArrayList<AnylyzeResult>();
		AnylyzeResult result = new AnylyzeResult();
		result.setBeginIndex(0);
		result.setisSleep(detailData.get(0).isSleep());
		int i =0;
		while(i < detailData.size() -1)
		{
			if(detailData.get(i+1).isSleep()  != detailData.get(i).isSleep()){
				result.setEndIndex(i);
				anylyzeResult.add(result);
				result = new AnylyzeResult();
				result.setisSleep(detailData.get(i+1).isSleep());
				result.setBeginIndex(i+1);
			}
			i++;
		}		
		result.setEndIndex(i);
		anylyzeResult.add(result);
		return anylyzeResult;
	}
	
	
	private void drawSportChartLine(List<BRDetailData> detailData,Canvas canvas, int beginIndex, int endIndex){		
		Log.e(TAG, "beginIndex:"+beginIndex+"和 endIndex："+endIndex);
		Paint paint = new Paint();
		paint.setColor(Color.YELLOW);
		paint.setStyle(Paint.Style.FILL); 
		Path path = new Path();
		path.moveTo(getBeginX(detailData.get(beginIndex)),chartParameter.getChartHeight());		 
	
		for(int i = beginIndex; i<= endIndex; i ++){
			BRDetailData data = detailData.get(i);
			 path.lineTo(getEndX(data),getSportY(data));
		}     
		path.lineTo(getEndX(detailData.get(endIndex)),chartParameter.getChartHeight());
		path.lineTo(getBeginX(detailData.get(beginIndex)),chartParameter.getChartHeight());
		
        path.close(); 

        canvas.drawPath(path, paint);     
	}
	
	private void drawSportChartRect(List<BRDetailData> detailData,Canvas canvas, int beginIndex, int endIndex){	
		
		for(int i = beginIndex; i<= endIndex; i ++){
			BRDetailData data = detailData.get(i);
			Paint paint = new Paint();
			paint.setColor(GetSportColor(data));
			paint.setStyle(Paint.Style.FILL); 
			Path path = new Path();
			path.moveTo(getBeginX(data),chartParameter.getChartHeight());
			
			path.lineTo(getBeginX(data),getSportY(data));		   
			path.lineTo((getEndX(data)-getBeginX(data)) < 1?(getBeginX(data)+1):getEndX(data),getSportY(data));
			path.lineTo((getEndX(data)-getBeginX(data)) < 1?(getBeginX(data)+1):getEndX(data),chartParameter.getChartHeight());
			path.moveTo(getBeginX(data),chartParameter.getChartHeight());
	        path.close(); 
	        Log.d(TAG, "drawSportChartRect :"+detailData.get(i).toString());
	        canvas.drawPath(path, paint);
		}    	
	}
	
	private void drawSleepChart(List<BRDetailData> detailData,Canvas canvas, int beginIndex, int endIndex){		
		
		Paint rectPaint = new Paint();
		rectPaint.setColor(Color.rgb(0x0f, 0xb4, 0xc9));	//#0FB4C9
		rectPaint.setStyle(Style.FILL);
		canvas.drawRect(getBeginX(detailData.get(beginIndex)), chartParameter.getChartHeight(),
				getEndX(detailData.get(endIndex)), chartParameter.getHeight(), rectPaint);
		
		for(int i = beginIndex; i<= endIndex; i ++){
			BRDetailData data = detailData.get(i);
			Paint paint = new Paint();
			paint.setColor(GetSleepColor(data.getState()));
			paint.setStyle(Paint.Style.FILL);  
			
			Path path = new Path();
			path.moveTo(getBeginX(data),chartParameter.getChartHeight());
			path.lineTo(getBeginX(data),GetSleepY(data.getState()));		   
			path.lineTo(getEndX(data)  ,GetSleepY(data.getState()));
			path.lineTo(getEndX(data)  ,chartParameter.getChartHeight());
			path.moveTo(getBeginX(data),chartParameter.getChartHeight());
	        path.close(); 

	        canvas.drawPath(path, paint);
		}    	
	}	
	
	private float getBeginX(BRDetailData  data)
	{
		float width =chartParameter.getXScale()  * (data.getBegin() - beginTime);
		return width;
	}
	
	private float getEndX(BRDetailData  data)
	{
		float width =  chartParameter.getXScale()  * (data.getBegin() + data.getDuration() - beginTime);
		return width;
	}
	
	private float getSportY(BRDetailData  data){
		if(data.getDuration()  > 0 &&( data.getState() == BRDetailData.STATE_WALKING || data.getState() == BRDetailData.STATE_RUNNING)){
			float height =  data.getSteps()/data.getDuration()* chartParameter.getYScale();
//			Log.d(TAG, "height:"+ height);
			return  (chartParameter.getChartHeight() - height);
		}else{
			return chartParameter.getChartHeight();
		}
	}
	
	private float GetSleepY(int state){
		if(state == BRDetailData.STATE_SLEEP_ACTIVE){
			return chartParameter.getChartHeight() - 18 * chartParameter.getYScale();
		}else if (state == BRDetailData.STATE_SLEEP_LIGHT){
			return chartParameter.getChartHeight() - 36 * chartParameter.getYScale();
		}else{
			return chartParameter.getChartHeight() - 60 * chartParameter.getYScale();
		}
	}
	
	private int GetSportColor(BRDetailData  data){	
		int average = 0;
		if(data.getDuration() > 0)
		{
			average = data.getSteps()/data.getDuration();
		}
		
		if(average > 40){
			return Color.rgb(0xF3, 0x2D, 0x0C); //#F32D0C
		}else if(average > 20){
			return Color.rgb(0xF5, 0xB2, 0x27); //#F5B227
		}else{
			return Color.rgb(0xF7, 0xCA, 0x3F); //#F7CA3F
		}
		
	}
	
	private int GetSleepColor(int state){
		if(state == BRDetailData.STATE_SLEEP_ACTIVE){
			return Color.rgb(0xFF, 0xB6, 0x30); //#FFB630
		}else if (state == BRDetailData.STATE_SLEEP_LIGHT){
			return Color.rgb(0x30, 0xC3, 0xF9); //#30C3F9
		}else{
			return Color.rgb(0x08, 0x7B, 0xC4); //#087BC4
		}
	}
}
