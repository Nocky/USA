package com.linkloving.rtring_c_watch.logic.reportday;

/**
 * 图表参数
 * @author Administrator
 *
 */
public class ChartParameter {
	private float xScale;
	private float yScale;
	private int width;
	private int height;	
	private int bottomBar;
	private int chartHeight;
	
	public ChartParameter(float xScale,	float yScale, int width, int height, int bottomBar){
		this.xScale = xScale;
		this.yScale = yScale;
		this.width = width;
		this.height = height;
		this.bottomBar = bottomBar;
		chartHeight = height - bottomBar;
	}
	
	public float getXScale(){
		return xScale;
	}
	
	public void setXScale(float xScale){
		this.xScale = xScale;
	}
	
	public float getYScale(){
		return yScale;
	}
	
	public void setYScale(float yScale){
		this.yScale = yScale;
	}
	
	public int getWidth(){
		return width;
	}
	
	public void setWidth(int width){
		this.width = width;
	}
	
	public int getHeight(){
		return height;
	}
	
	public void setHeight(int height){
		this.height = height;
	}
	
	public int getBottomBar(){
		return bottomBar;
	}
	
	public void setBottomBar(int bottomBar){
		this.bottomBar = bottomBar;
	}
	
	public int getChartHeight(){
		return this.chartHeight;
	}
}
