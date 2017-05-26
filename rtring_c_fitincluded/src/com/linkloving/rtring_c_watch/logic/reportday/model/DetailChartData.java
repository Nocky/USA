package com.linkloving.rtring_c_watch.logic.reportday.model;

import java.util.List;

import com.linkloving.band.ui.BRDetailData;
import com.linkloving.band.ui.DetailChartCountData;
/**
 * 图表数据结构
 * @author Administrator
 *
 */
public class DetailChartData 
{
	/** 统计数据*/
	public DetailChartCountData count;
	/** 图表运动数据列表*/
	public List<BRDetailData> list;
}
