package com.linkloving.rtring_c_watch.logic.more;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.eva.android.widget.ActivityRoot;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.logic.launch.LoginActivity;
import com.linkloving.rtring_c_watch.utils.IntentFactory;
import com.linkloving.rtring_c_watch.utils.LanguageHelper;
import com.linkloving.rtring_c_watch.utils.SkinSettingManager;

public class AboutActivity extends ActivityRoot
{

	// 条款
	private Button termsBtn = null;
	// 隐私
	private Button privacyBtn = null;

	// officalwebsite
	private Button websiteBtn = null;
	// mail
	private Button mailBtn = null;

	// ----------------------------------- SNS2
	// weibo
	private Button weiboBtn = null;
	// douban
	private Button doubanBtn = null;

	private TextView versionView = null;
	
	private SkinSettingManager mSettingManager;
	@Override
	protected void onResume() {
		super.onResume();
		mSettingManager=new SkinSettingManager(this);
		mSettingManager.initSkins();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		initViews();
		initListeners();
		initDatas();
	}

	private void initViews()
	{
		// 设定自定义标题栏（设定此值即意味着开启自定义标题栏的使用，必须要在setContentView前设定）
		customeTitleBarResId = R.id.main_about_titleBar;
		// 养成良好习惯：首先设置主layout，确保后绪的操作中使用到的组件都可以被find到
		setContentView(R.layout.main_about);

		termsBtn = (Button) findViewById(R.id.main_about_terms_conditions);
		privacyBtn = (Button) findViewById(R.id.main_about_privacy_policy);
		websiteBtn = (Button) findViewById(R.id.main_about_official_website);
		mailBtn = (Button) findViewById(R.id.main_about_mail);

		weiboBtn = (Button) findViewById(R.id.main_about_weibo);
		doubanBtn = (Button) findViewById(R.id.main_about_douban);

		versionView = (TextView) findViewById(R.id.main_about_versionView);
		versionView.setText(LoginActivity.getAPKVersionName(this) + "(" + LoginActivity.getAPKVersionCode(this) + ")");

		// 根据语言决定该显示什么样的SNS功能
		ViewGroup cnSnsLL = (ViewGroup) findViewById(R.id.main_about_sns_cn_ll);
		// if(LaguageHelper.isChinese_SimplifiedChinese())
		// {
		// enSnsLL.setVisibility(View.GONE);
		// cnSnsLL.setVisibility(View.VISIBLE);
		// }
		// else
		{
		}

		// 设置标题（自定义标题栏后的title文本设置是不同的哦，见CustomeTitleBar中的说明）
		this.setTitle(R.string.main_about_FitIncluded);
	}

	private void initListeners()
	{

		mailBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startActivity(new Intent(AboutActivity.this, SendMailActivity.class));
			}
		});

		termsBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startWebAcvitity(AboutActivity.this, MyApplication.REGISTER_AGREEMENT_CN_URL);
			}
		});

		privacyBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				startActivity(IntentFactory.createCommonWebActivityIntent(AboutActivity.this, LanguageHelper.isChinese_SimplifiedChinese()? MyApplication.PRIVACY_CN_URL : MyApplication.PRIVACY_EN_URL));
			}
		});

		websiteBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
//				 ToolKits.shareContent(AboutActivity.this, "连爱手环", "消息标题1",
//				 "我在玩连爱手环", "/sdcard/test1.png");
				 startWebAcvitity(AboutActivity.this, MyApplication.LONKLOVING_OFFICAL_WEBSITE);
			}
		});

		weiboBtn.setOnClickListener(shareOnClickListener);
		doubanBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				// startActivity(IntentFactory.createCommonWebActivityIntent(
				// AboutActivity.this, (String)((TextView)
				// findViewById(R.id.main_about_douban_urlView)).getText()));
			}
		});
	}

	OnClickListener shareOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Intent it = new Intent(Intent.ACTION_SEND);
			it.putExtra(Intent.EXTRA_TEXT, "The email subject text");
			it.setType("text/plain");
			startActivity(Intent.createChooser(it, "Choose Share Client"));
		}
	};
	
	private void initDatas()
	{

	}

	public static void startWebAcvitity(Activity activity, String url)
	{
		activity.startActivity(IntentFactory.createCommonWebActivityIntent(activity, url));
	}

}
