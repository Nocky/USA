package com.linkloving.rtring_c_watch.logic.main.impl;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.logic.more.HelpActivity;
import com.linkloving.rtring_c_watch.utils.IntentFactory;

public class ViewPagerAdapter extends PagerAdapter
{
	public int finishAction;
	// 界面列表
	private List<View> views;
	public Activity activity;

	private static final String SHAREDPREFERENCES_NAME = "first_pref";

	public ViewPagerAdapter(List<View> views, Activity activity)
	{
		this.views = views;
		this.activity = activity;
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2)
	{
		((ViewPager) arg0).removeView(views.get(arg1));
	}

	@Override
	public void finishUpdate(View arg0)
	{
	}

	// 获得当前界面
	@Override
	public int getCount()
	{
		if (views != null)
		{
			return views.size();
		}
		return 0;
	}

	// 初始化arg1位置的界
	@Override
	public Object instantiateItem(View arg0, int arg1)
	{
		((ViewPager) arg0).addView(views.get(arg1), 0);
		if (arg1 == views.size() - 1)
		{
			Button mStartWeiboImageButton = (Button) arg0.findViewById(R.id.iv_start_weibo);
			mStartWeiboImageButton.setOnClickListener(new OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					if (finishAction == HelpActivity.FININSH_VIEWPAGE_GO_TAB_HOST)
					{
						// 设置已经引导
						setGuided(activity);
						goHome();
					}
					else if (finishAction == HelpActivity.FININSH_VIEWPAGE_FINISHACTIVITY)
					{
						activity.finish();
					}
				}
			});
		}
		return views.get(arg1);
	}

	private void goHome()
	{
		activity.startActivity(IntentFactory.createLoginIntent(activity));
		activity.finish();
	}

	/**
	 * 
	 * method desc：设置已经引导过了，下次启动不用再次引导
	 */
	public static void setGuided(Context context)
	{
		SharedPreferences preferences = context.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		// 存入数据
		editor.putBoolean("isFirstIn", false);
		// 提交修改
		editor.commit();
	}

	// 判断是否由对象生成界�?
	@Override
	public boolean isViewFromObject(View arg0, Object arg1)
	{
		return (arg0 == arg1);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1)
	{
	}

	@Override
	public Parcelable saveState()
	{
		return null;
	}

	@Override
	public void startUpdate(View arg0)
	{
	}

}
