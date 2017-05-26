package com.linkloving.rtring_c_watch.logic.launch;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.eva.android.widget.ActivityRoot;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.utils.IntentFactory;
import com.linkloving.rtring_c_watch.utils.SkinSettingManager;
import com.rtring.buiness.logic.dto.UserRegisterDTO;

public class RegisterSuccessActivity extends ActivityRoot 
{
	private SkinSettingManager mSettingManager;
	private TextView mailTextView;
	private Button goButton;
	private UserRegisterDTO u;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_success);
		
		u = IntentFactory.parseRegisterSuccessIntent(getIntent());
		initViews();
		bindListener();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mSettingManager=new SkinSettingManager(this);
		mSettingManager.initSkins();
	}
	
	private void initViews()
	{
		mailTextView = (TextView) findViewById(R.id.register_sucess_email_text);
		if(u != null)
		{
			mailTextView.setText(u.getUser_mail());
		}
		goButton = (Button) findViewById(R.id.register_success_go_btn);
	}
	
	private void bindListener()
	{
		goButton.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
//				Intent intent = new Intent(RegisterSuccessActivity.this, LoginActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				startActivity(intent);
				
				// 注册成功后，将uid和密码回传给登陆页面，方便用户立即登陆
				setResult(Activity.RESULT_OK, IntentFactory.createLoginIntent(RegisterSuccessActivity.this, u.getUser_mail(), u.getUser_psw()));
				Log.i("LoginActivity", "RegisterSuccessActivity的密码:"+u.getUser_psw());
				finish();
			}
		});
	}
}
