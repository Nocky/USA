package com.linkloving.rtring_c_watch.logic.launch;

import java.util.Date;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eva.android.platf.std.AutoUpdateDaemon;
import com.eva.android.platf.std.DataLoadableActivity;
import com.eva.android.widget.DataLoadingAsyncTask;
import com.eva.epc.common.util.CommonUtils;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.eva.epc.core.dto.SysActionConst;
import com.linkloving.rtring_c_watch.LocalUserSettingsToolkits;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.linkloving.rtring_c_watch.logic.model.LocalSetting;
import com.linkloving.rtring_c_watch.logic.setup.BodyActivity;
import com.linkloving.rtring_c_watch.utils.HttpHelper;
import com.linkloving.rtring_c_watch.utils.IntentFactory;
import com.linkloving.rtring_c_watch.utils.LanguageHelper;
import com.linkloving.rtring_c_watch.utils.SkinSettingManager;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.linkloving.utils._Utils;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.JobDispatchConst;
import com.rtring.buiness.logic.dto.UserRegisterDTO;
import com.rtring.buiness.util.Des3;

/**
 * 用户注册界面.
 * 
 * @author Jack Jiang, 2013-07-04
 * @version 1.0
 */
public class RegisterActivity extends DataLoadableActivity
{
	public static final int REGISTER_BACK_FROM_BODY = 99999;
	private SkinSettingManager mSettingManager;
	/** 文本编辑组件：昵称 */
	private TextView txtNickname = null;
	/** 文本编辑组件：email */
	private TextView txtEmail = null;
	/** 文本编辑组件：输入密码 */
	private TextView txtPassword = null;
	/** 文本编辑组件：确认密码 */
	private TextView txtConformPassword = null;
	/** 选择组件：是否同意条款 */
	private CheckBox cbAgreeLisence = null;
	/** 选择组件：查看条款 */
	private Button btnLookCaluse = null;
	
	private String userHeight = null;
	private String userWeight = null;
	private String userAge = null;
	private String userSex = null;
	private String target_step = null;
	private String userId = null;
	
	
	
	/** 按钮：下一页 */
	private Button btnNext = null;
	
	private boolean mailForUserChecked = false;
	private boolean mailValid = false;
	
	
	/** 
	 * <p>
	 * 初始化界面组件.
	 * 默认情况下本方法什么也不做，子类需自行实现之.
	 * </p>
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mSettingManager=new SkinSettingManager(this);
		mSettingManager.initSkins();
	}
	
	protected void initViews()
	{
//		//设置本activity在进行各数据修改时服务端的处理器id
//		this.processor_id = MyProcessorConst.PROCESSOR_LOGIC;	
//		//设置本activity在进行各数据修改时服务端处理器内的作业调度id
//		this.job_dispatch_id = JobDispatchConst.LOGIC_REGISTER;
		
		customeTitleBarResId = R.id.register_form_titleBar;
		
		//养成良好习惯：首先设置主layout，确保后绪的操作中使用到的组件都可以被find到
		setContentView(R.layout.register_form);
		
//		getCustomeTitleBar().getLeftBackButton().setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v)
//			{
//			     RegisterActivity.this.finish();
//			}
//		});
		
		setTitle(R.string.register_form_title);

		//各功能组件
		txtNickname = (TextView)this.findViewById(R.id.resigter_form_nicknameEdit);
		txtEmail = (TextView)this.findViewById(R.id.register_form_emailEdit);
		txtPassword = (TextView)this.findViewById(R.id.register_form_passwordEdit);
		txtConformPassword = (TextView)this.findViewById(R.id.register_form_conformPswEdit);
		cbAgreeLisence =(CheckBox)this.findViewById(R.id.register_form_agreeLisenseCb);
		btnLookCaluse = (Button)this.findViewById(R.id.register_form_to_clause);
		
		
		btnNext = (Button)this.findViewById(R.id.register_form_submitBtn);
	}
	
	/**
	 * 为各UI功能组件增加事件监听的实现方法.
	 * 默认情况下本方法什么也不做，子类需自行实现之.
	 */
	protected void initListeners()
	{
		
		txtEmail.setOnFocusChangeListener(new OnFocusChangeListener()
		{
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				//失去焦点时验证邮箱地址是否被注册
				if(!hasFocus)
				{
					if (String.valueOf(txtEmail.getText()).trim().length() <= 0)
					{
						txtEmail.setError($$(R.string.register_form_valid_mail));
						mailValid = false;
						return;
					}
					if (!CommonUtils.isEmail(String.valueOf(txtEmail.getText()).trim()))
					{
						txtEmail.setError($$(R.string.general_invild));
						mailValid = false;
						return;
					}
					new MailAddressAsyncTask(new Observer(){
						@Override
						public void update(Observable observable, Object data)
						{
							mailForUserChecked = true;
						}
					}).execute();
				}
				else
					mailForUserChecked = false;
			}
		});
		btnLookCaluse.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
//				WidgetUtils.showToast(RegisterActivity.this, "条款查看功能稍后实现！！！！！！！！", ToastType.INFO);
//				//跳转到条款查看activity　
				startActivity(IntentFactory.createCommonWebActivityIntent(RegisterActivity.this, LanguageHelper.isChinese_SimplifiedChinese() ? MyApplication.REGISTER_AGREEMENT_CN_URL : MyApplication.REGISTER_AGREEMENT_EN_URL));
			}
		});
		
		btnNext.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v)
			{
				if(mailForUserChecked)
				{
					if(fireSave())
						startActivityForResult(IntentFactory.createBodyActivityIntent(RegisterActivity.this, null, BodyActivity.REGISTER_ACTIVITY,""), REGISTER_BACK_FROM_BODY);
				}
				else
				{
					new MailAddressAsyncTask(new Observer(){
						@Override
						public void update(Observable observable, Object data)
						{
							if(fireSave())
								startActivityForResult(IntentFactory.createBodyActivityIntent(RegisterActivity.this, null, BodyActivity.REGISTER_ACTIVITY,""), REGISTER_BACK_FROM_BODY);
						}
					}).execute();
				}
			}
		});
		
		cbAgreeLisence.setOnCheckedChangeListener(new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if(isChecked)
					cbAgreeLisence.setError(null);
			}
		});
	}
	
	
	
	public UserRegisterDTO getFormData()
	{
		UserRegisterDTO u = new UserRegisterDTO();
		u.setNickname(String.valueOf(this.txtNickname.getText()));
		u.setUser_mail(String.valueOf(this.txtEmail.getText()).toLowerCase());
//		u.setUser_psw(String.valueOf(this.txtPassword.getText()));
		try
		{
			u.setUser_psw(Des3.encode(String.valueOf(this.txtPassword.getText())));
		}
		catch (Exception e)
		{
			Log.e("[LZ]=====================", "密码加密失败. 原文密码：" + String.valueOf(this.txtPassword.getText()));
			return null;
		}
		u.setUser_sex(userSex);
		u.setUser_weight(userWeight);
		u.setUser_height(userHeight);
		u.setBirthdate(_Utils.getBirthdateByAge(Integer.parseInt(userAge)));
		
		return u;
	}
	
	private boolean fireSave()
	{
		if (String.valueOf(txtEmail.getText()).trim().length() <= 0)
		{
			txtEmail.setError($$(R.string.register_form_valid_mail));
			return false;
		}
		if (!CommonUtils.isEmail(String.valueOf(txtEmail.getText()).trim()))
		{
			txtEmail.setError($$(R.string.general_invild));
			return false;
		}
		if(!mailValid)
		{
			txtEmail.setError($$(R.string.register_form_mail_have_exist));
			return false;
		}
		if (String.valueOf(txtNickname.getText()).trim().length() <= 0)
		{
			txtNickname.setError($$(R.string.register_form_valid_nick_name));
			return false;
		}

		if (String.valueOf(txtPassword.getText()).trim().length() < 6)
		{
			txtPassword.setError($$(R.string.register_form_valid_psw_notenouph));
			return false;
		}
		
		String password = String.valueOf(this.txtPassword.getText());
		if (password != null && !password.equals(String.valueOf(this.txtConformPassword.getText())))
		{
			txtConformPassword.setError($$(R.string.register_form_valid_psw_not_same));
			return false;
		}
		
		if (!cbAgreeLisence.isChecked())
		{
			cbAgreeLisence.setError($$(R.string.register_form_valid_agree_role));
			return false;
		}
		return true;
	}
	
	/**
	 * 验证邮箱是否被注册
	 */
	protected class MailAddressAsyncTask extends DataLoadingAsyncTask<String, Integer, DataFromServer>
	{
		private Observer obsForCheckEnd = null;
		
		public MailAddressAsyncTask(Observer obsForCheckEnd)
		{
			super(RegisterActivity.this, false);
			this.obsForCheckEnd = obsForCheckEnd;
		}
		
		/**
		 * 在后台执行 {@link doLogin()}实现登陆信息的提交和处于结果的读取 .
		 * 
		 * @param parems 外界传进来的参数
		 * @return 查询结果，将传递给onPostExecute(..)方法
		 */
		@Override
		protected DataFromServer doInBackground(String... parems) 
		{
			return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(
					DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_LOGIC)
					.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER)
					.setActionId(SysActionConst.ACTION_APPEND8)
					.setNewData(String.valueOf(txtEmail.getText()).trim()));
		}
		
		/**
		 * 处理服务端返回的登陆结果信息.
		 * 
		 * @see AutoUpdateDaemon
		 * @see #needSaveDefaultLoginName()
		 * @see #afterLoginSucess()
		 */
		protected void onPostExecuteImpl(Object result)
		{
			mailValid = !Boolean.parseBoolean((String)result);
			Drawable d = null;
			//int iconwith = com.google.zxing.client.android.utils.ToolKits.dip2px(RegisterActivity.this, 22.5f);
			//int iconheight = com.google.zxing.client.android.utils.ToolKits.dip2px(RegisterActivity.this, 21.5f);
			int iconwith = ToolKits.dip2px(RegisterActivity.this, 22.5f);
			int iconheight = ToolKits.dip2px(RegisterActivity.this, 21.5f);
			if(!mailValid)
			{
				d = RegisterActivity.this.getResources().getDrawable(R.drawable.widget_toast_icon_error);
				d.setBounds(0, 0, iconwith, iconheight);
				txtEmail.setError($$(R.string.register_form_mail_have_exist), d);
			}
			else
			{
				d = RegisterActivity.this.getResources().getDrawable(R.drawable.widget_toast_icon_ok);
				d.setBounds(0, 0, iconwith, iconheight);
				txtEmail.setError($$(R.string.register_form_mail_ok), d);
			}
			
			if(obsForCheckEnd != null)
				obsForCheckEnd.update(null, null);
		}
	}

	

	@Override
	protected DataFromServer queryData(String... arg0)
	{
		return null;
	}
	@Override
	protected void refreshToView(Object arg0)
	{
	}
	
	/**
	 * 处理回调.
	 * {@inheritDoc}
	 * 
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
			case LoginActivity.REQUEST_CODE_FOR_REGISTER:
				// 将注册成功后的uid和密码默认填到界面上，方便用户登陆
				if (resultCode == Activity.RESULT_OK)
				{
					//注册成功后，将uid和密码回传给登陆页面，方便用户立即登陆
					setResult(Activity.RESULT_OK, data);
					finish();
				}
				break;
			case REGISTER_BACK_FROM_BODY:
				if (resultCode == Activity.RESULT_OK)
				{
//					target_step=data.getStringExtra("target_step");
					userAge = data.getStringExtra("user_age");
					userHeight = data.getStringExtra("user_height");
					userSex = data.getStringExtra("user_sex");
					userWeight = data.getStringExtra("user_weight");
					System.out.println("Register.userSex--->"+userSex);
					//* 开始提交注册信息(在里面还提交了目标步数)
					new RegisterSubmitAsyncTask().execute();
					
				}
				break;
		}
	}

	
	/**
	 * 提交注册数据的异步多线程封装类.
	 */
	protected class RegisterSubmitAsyncTask extends DataLoadingAsyncTask<String, Integer, DataFromServer>
	{
		private UserRegisterDTO registerData = null;
		
		public RegisterSubmitAsyncTask()
		{
			super(RegisterActivity.this, $$(R.string.general_submitting));
		}
		
		@Override
		protected void onPreExecute()
		{
			super.onPreExecute();
			btnNext.setEnabled(false);
		}
		
		/**
		 * 在后台执行 {@link doLogin()}实现登陆信息的提交和处于结果的读取 .
		 * 
		 * @param parems 外界传进来的参数
		 * @return 查询结果，将传递给onPostExecute(..)方法
		 */
		@Override
		protected DataFromServer doInBackground(String... parems) 
		{
			//** 提交注册数据到服务端
			registerData = getFormData();
			//将数据提交服务端进行保存和处理
//			DataFromServer dfs = submitToServer(SysActionConst.ACTION_APPEND1, registerData, null);
			
//			// 提交请求到服务端
//			DataFromClient dataFromClient = DataFromClient.n()
//					.setProcessorId(MyProcessorConst.PROCESSOR_LOGIC)		
//					.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER)
//					.setActionId(SysActionConst.ACTION_EDIT)
//					// 要接收邀请的邮件地址
//					.setNewData(registerData);
//			DataFromServer dfs = HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(dataFromClient);
			return HttpHelper.submitRegisterationToServer(registerData);
		}
		
		@Override
		protected void onPostExecute(DataFromServer result) 
		{
			super.onPostExecute(result);
			btnNext.setEnabled(true);
		}
		
		/**
		 * 处理服务端返回的登陆结果信息.
		 * @see AutoUpdateDaemon
		 * @see #needSaveDefaultLoginName()
		 * @see #afterLoginSucess()
		 */
		protected void onPostExecuteImpl(Object result)
		{
			Object retVal = result;
			if(retVal instanceof String)
			{
//				WidgetUtils.showToast(RegisterActivity.this, "注册成功，你的UID是："+((String)retVal), WidgetUtils.ToastType.OK);
				if(retVal.equals("0"))
				{
					new com.eva.android.widgetx.AlertDialog.Builder(RegisterActivity.this)  
					.setTitle(R.string.general_error)
					.setMessage(R.string.register_form_error_mail_exist)
					.setPositiveButton(R.string.general_ok,   null)
					.setNegativeButton(R.string.general_cancel, null).show();
					return;
				}
				else
				{
					registerData.setUser_id((String)retVal);
					userId=(String)retVal;
//					new DataAsyncTask().execute();
					Intent intent = IntentFactory.createRegisterSuccessIntent(RegisterActivity.this, registerData);
					startActivityForResult(intent, LoginActivity.REQUEST_CODE_FOR_REGISTER);
				}
			}
			//否则该返回对象应该是包含异常信息的字符串
			else
			{
				new com.eva.android.widgetx.AlertDialog.Builder(RegisterActivity.this)  
					.setTitle(R.string.general_error)
					.setMessage(R.string.register_form_error_message)
					.setPositiveButton(R.string.general_ok,   null)
					.setNegativeButton(R.string.general_cancel, null).show();
				return;
			}
		}
	}
	/**
	 * 提交运动目标的数据请求和处理的异步执行线程实现类.
	 */
	protected class DataAsyncTask extends DataLoadingAsyncTask<String, Integer, DataFromServer>
	{
		public DataAsyncTask()
		{
			super(RegisterActivity.this, $$(R.string.general_submitting));
		}

		/**
		 * 在后台执行 {@link doLogin()}实现登陆信息的提交和处于结果的读取 .
		 * 
		 * @param parems
		 *            外界传进来的参数
		 * @return 查询结果，将传递给onPostExecute(..)方法
		 */
		@Override
		protected DataFromServer doInBackground(String... params)
		{
			LocalSetting localSetting = new LocalSetting();
			long update_time = ToolKits.getDayFromDate(new Date(), 0).getTime();
			
			localSetting.setUser_mail(String.valueOf(txtEmail.getText()).toLowerCase());
			localSetting.setGoal(target_step);
			localSetting.setGoal_update(update_time);
			LocalUserSettingsToolkits.setLocalSettingGoalInfo(context, localSetting);
			
			
			JSONObject dataObj = new JSONObject();
			dataObj.put("goal", target_step);
			dataObj.put("goal_update", update_time);
			dataObj.put("user_id", userId);
			
			
			if(ToolKits.isNetworkConnected(RegisterActivity.this))
			{
				return HttpServiceFactory4AJASONImpl
						.getInstance()
						.getDefaultService()
						.sendObjToServer(
								DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_USERSETTING).setJobDispatchId(JobDispatchConst.USER_SETTINGS_GOAL)
										.setActionId(SysActionConst.ACTION_APPEND4).setNewData(dataObj.toJSONString()));
			}
			else
			{
				DataFromServer dfs = new DataFromServer();
				dfs.setSuccess(true);
				dfs.setReturnValue(JSON.toJSONString(dataObj));
				return dfs;
			}
			
		}

		/**
		 * 处理服务端返回的登陆结果信息.
		 * 
		 * @see AutoUpdateDaemon
		 * @see #needSaveDefaultLoginName()
		 * @see #afterLoginSucess()
		 */
		protected void onPostExecuteImpl(Object result)
		{
			if (result != null)
			{
				JSONObject obj = JSON.parseObject((String) result);
				
				
				
				
			}
		}
	}
}
