package com.linkloving.rtring_c_watch.logic.more;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.eva.android.x.BaseActivity;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.logic.main.impl.ViewPagerAdapter;
import com.linkloving.rtring_c_watch.utils.IntentFactory;
import com.linkloving.rtring_c_watch.utils.LanguageHelper;
import com.linkloving.rtring_c_watch.utils.ToolKits;

public class HelpActivity extends BaseActivity implements OnPageChangeListener, OnTouchListener, OnGestureListener
{
	public final static int FININSH_VIEWPAGE_FINISHACTIVITY = 0;
	public final static int FININSH_VIEWPAGE_GO_TAB_HOST = 1;
	public final static String BUNDLE_LISTER = "finishLinster";
	
	private GestureDetector mGestureDetector;
	private ViewPager vp;
	// [FIX BUG]：[bug]5、【重要】解决了帮助页面的内存泄漏问题（内存泄漏高达15M）。
	// 		内存泄漏的原因是：该Activity中错误地adaptery设置成了static类型，从而导致该adapter引
	// 		用的activity(context)无法被释放，进而该Activity所占用的15M内存将直致程序退出才能释放的严重泄漏问题。
	private ViewPagerAdapter vpAdapter;
	private List<View> views;
	//引导页合集
	private int[] pageImages = new int[] { R.drawable.help_first_en};// R.drawable.help_three};
	//帮助页面合集
	private int[] pageImages_help = new int[] { R.drawable.help_first_en};
	private ImageView[] dots;
	private int currentIndex;
	private int finishaction = FININSH_VIEWPAGE_FINISHACTIVITY;
	private boolean isJiaocheng = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide);
		ArrayList datas = IntentFactory.parseHelpActivityIntent(getIntent());
//		if (getIntent().getExtras().containsKey("isJiaocheng"))
//			isJiaocheng = getIntent().getExtras().getBoolean("isJiaocheng");
//		finishaction = getIntent().getExtras().getInt("finish_action");
		finishaction = (Integer)datas.get(0);
		isJiaocheng = (Boolean)datas.get(1);
		
		if(!LanguageHelper.isChinese_SimplifiedChinese())
		{
			pageImages = new int[] { R.drawable.help_first_en};
			pageImages_help = new int[] { R.drawable.help_first_en};
		}
		
		initViews();
		initDots();
	}

	private void initViews()
	{
		mGestureDetector = new GestureDetector(this);   

		LayoutInflater inflater = LayoutInflater.from(this);

		views = new ArrayList<View>();

		vpAdapter = new ViewPagerAdapter(views, this);
		vpAdapter.finishAction = finishaction;
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		for (int i : (isJiaocheng ? pageImages_help : pageImages))
		{

			LinearLayout linearLayout = new LinearLayout(this);
			LinearLayout.LayoutParams mLayoutParams_2 = new LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
					android.widget.LinearLayout.LayoutParams.MATCH_PARENT);
			linearLayout.setLayoutParams(mLayoutParams_2);
			LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
					android.widget.LinearLayout.LayoutParams.MATCH_PARENT);
			linearLayout.setOrientation(LinearLayout.HORIZONTAL);
			ImageView imageView = new ImageView(this);
			imageView.setLayoutParams(mLayoutParams);
			imageView.setImageResource(i);
			imageView.setScaleType(ScaleType.FIT_XY);
			linearLayout.addView(imageView);
			views.add(linearLayout);
		}
		views.add(inflater.inflate(R.layout.guide_lastloading, null));
//		if (isJiaocheng)
//		{
//			ImageView v = (ImageView) views.get(views.size() - 1).findViewById(R.id.img_bg);
////			v.setImageResource(R.drawable.help_four);
//			v.setImageResource(LanguageHelper.isChinese_SimplifiedChinese() ? R.drawable.help_three : R.drawable.help_three_en);
//		}
		vp = (ViewPager) findViewById(R.id.viewpager);
		vp.setAdapter(vpAdapter);

		vp.setOnPageChangeListener(this);
		vp.setOnTouchListener(this);
	}

	public int getScreenWidth()
	{
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidthDip = dm.widthPixels;
		return screenWidthDip;
	}

	private void initDots()
	{
		LinearLayout ll = (LinearLayout) findViewById(R.id.ll);

		dots = new ImageView[views.size()];

		for (int i = 0; i < views.size(); i++)
		{
			ImageView img = new ImageView(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			params.gravity = Gravity.CENTER;
			img.setPadding(ToolKits.dip2px(this, 10), ToolKits.dip2px(this, 2), ToolKits.dip2px(this, 10), ToolKits.dip2px(this, 2));
			img.setClickable(true);
			img.setImageResource(i == 0 ? R.drawable.widget_pagger_dot_black : R.drawable.widget_pagger_dot_white);
			img.setLayoutParams(params);
			ll.addView(img);
			dots[i] = img;
			dots[i].setEnabled(true);
		}

		currentIndex = 0;
		dots[currentIndex].setEnabled(false);
	}

	private void setCurrentDot(int position)
	{
		if (position < 0 || position > views.size() - 1 || currentIndex == position)
		{
			return;
		}

		dots[position].setImageResource(R.drawable.widget_pagger_dot_black);
		dots[currentIndex].setImageResource(R.drawable.widget_pagger_dot_white);

		currentIndex = position;
	}

	@Override
	public void onPageScrollStateChanged(int position)
	{
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}

	@Override
	public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
	{
		if(position == views.size())
		{
			if(!isJiaocheng)
			{
				ViewPagerAdapter.setGuided(HelpActivity.this);				
				startActivity(IntentFactory.createLoginIntent(this));
			}
			finish();
		}
	}

	@Override
	public void onPageSelected(int arg0)
	{
		setCurrentDot(arg0);
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onDown(android.view.MotionEvent)
	 */
	@Override
	public boolean onDown(MotionEvent e)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onFling(android.view.MotionEvent, android.view.MotionEvent, float, float)
	 */
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
	{
		// 参数解释：
		// e1：第1个ACTION_DOWN MotionEvent
		// e2：最后一个ACTION_MOVE MotionEvent
		// velocityX：X轴上的移动速度，像素/秒
		// velocityY：Y轴上的移动速度，像素/秒
		// 触发条件 ：
		// X轴的坐标位移大于FLING_MIN_DISTANCE，且移动速度大于FLING_MIN_VELOCITY个像素/秒

		final int FLING_MIN_DISTANCE = 50, FLING_MIN_VELOCITY = 0;

		//向左的手势
		if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY)
		{
			
			if(currentIndex == views.size() - 1)
			{
				if(!isJiaocheng)
				{
					ViewPagerAdapter.setGuided(HelpActivity.this);				
					startActivity(IntentFactory.createLoginIntent(this));
				}
				finish();
			}
		}
		//向右的手势
		else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY)
		{
			
		}
		return false;

	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onLongPress(android.view.MotionEvent)
	 */
	@Override
	public void onLongPress(MotionEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onScroll(android.view.MotionEvent, android.view.MotionEvent, float, float)
	 */
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
	{
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onShowPress(android.view.MotionEvent)
	 */
	@Override
	public void onShowPress(MotionEvent e)
	{
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see android.view.GestureDetector.OnGestureListener#onSingleTapUp(android.view.MotionEvent)
	 */
	@Override
	public boolean onSingleTapUp(MotionEvent e)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		return mGestureDetector.onTouchEvent(event);    
	}

}
