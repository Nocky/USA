package com.linkloving.rtring_c_watch.logic.reportday;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.linkloving.rtring_c_watch.R;

/**
 * 滑动图表的滑动条目
 * @author Administrator
 *
 */
public class DetailChartItemView extends LinearLayout 
{
	private LayoutInflater inflater;
	private ImageView  image;
	private FrameLayout bar;
	private ImageView error;

	public DetailChartItemView(Context context, AttributeSet attrs, int defStyle) 
	{
		super(context, attrs, defStyle);
		init(context);
	}

	public DetailChartItemView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init(context);
	}

	public DetailChartItemView(Context context)
	{
		super(context);
		init(context);
	}
	
	private void init(Context context)
	{
		inflater = LayoutInflater.from(context);
		inflater.inflate(R.layout.view_detail_chart_item, this);
		image = (ImageView) findViewById(R.id.detail_chart_item_image);
		bar = (FrameLayout) findViewById(R.id.detail_chart_item_progess_frame);
		error = (ImageView) findViewById(R.id.detail_chart_item_error);
	}
	
	/**
	 * 设置条目的图片
	 * @param bitmap
	 */
    public void setImageBitmap(Bitmap bitmap)
    {
    	bar.setVisibility(View.GONE);
    	image.setVisibility(View.VISIBLE);
    	image.setImageBitmap(bitmap);
    	image.setScaleType(ImageView.ScaleType.FIT_XY);
    }
    
    /**
     * 设置下载数据失败状态
     */
    public void setError()
    {
    	bar.setVisibility(View.GONE);
    	error.setVisibility(View.VISIBLE);
    }
    
    /**
     * 回收条目
     */
    public void recycle()
    {
    	recycleImageView(image);
    }
    
    /**
     * 判断条目是否被回收
     * @return
     */
   public boolean isRecycled()
   {
		BitmapDrawable bitmapDrawable = (BitmapDrawable) image
				.getDrawable();
		if(bitmapDrawable == null || bitmapDrawable.getBitmap() == null)
			return false;
		return bitmapDrawable.getBitmap().isRecycled();
   }
    
   /**
    * 回收图片内存
    * @param imageView
    */
    private void recycleImageView(ImageView imageView) {
		BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView
				.getDrawable();
		if(bitmapDrawable == null || bitmapDrawable.getBitmap() == null)
			return;
		if (!bitmapDrawable.getBitmap().isRecycled()) {
			bitmapDrawable.getBitmap().recycle();
		}
	}
}
