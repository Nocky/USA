package com.linkloving.rtring_c_watch.logic.more;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.eva.android.widget.ActivityRoot;
import com.eva.epc.common.util.CommonUtils;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.utils.SkinSettingManager;

public class SendMailActivity extends ActivityRoot
{

	private TextView mailView = null;

	private Button sendBtn = null;
	
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
	}
	

	private void initViews()
	{
		// 设定自定义标题栏（设定此值即意味着开启自定义标题栏的使用，必须要在setContentView前设定）
		customeTitleBarResId = R.id.main_about_send_mail_titleBar;
		// 养成良好习惯：首先设置主layout，确保后绪的操作中使用到的组件都可以被find到
		setContentView(R.layout.main_about_send_mail);

		mailView = (TextView) findViewById(R.id.main_about_send_mail_content);
		sendBtn = (Button) findViewById(R.id.main_about_send_mail_sendBtn);

		// 设置标题（自定义标题栏后的title文本设置是不同的哦，见CustomeTitleBar中的说明）
		this.setTitle(R.string.main_about_send_mail_title);
	}

	private void initListeners()
	{
		sendBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String content = String.valueOf(mailView.getText()).trim();
				if (CommonUtils.isStringEmpty(content))
				{
					mailView.setError(getString(R.string.main_about_send_mail_content));
					return;
				}

				sendMail(content);
			}
		});
	}

	private void sendMail(String content)
	{
		Intent email = new Intent(android.content.Intent.ACTION_SEND);
		email.setType("plain/text");
		String[] emailReciver = new String[] { MyApplication.LINKLOVING_OFFICAL_MAIL };
		String emailSubject = "You have a letter feedback";
		String emailBody = MyApplication.getInstance(this).getLocalUserInfoProvider().getUser_id() + "("
				+ MyApplication.getInstance(this).getLocalUserInfoProvider().getUser_mail() + ")" + " said: \n\t" + content;

		// 设置邮件默认地址
		email.putExtra(android.content.Intent.EXTRA_EMAIL, emailReciver);
		// 设置邮件默认标题
		email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailSubject);
		// 设置要默认发送的内容
		email.putExtra(android.content.Intent.EXTRA_TEXT, emailBody);
		// 调用系统的邮件系统
		startActivity(Intent.createChooser(email, "Please select sending mail software"));

		SendMailActivity.this.finish();
	}
}
