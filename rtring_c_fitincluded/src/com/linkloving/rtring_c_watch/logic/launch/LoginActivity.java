package com.linkloving.rtring_c_watch.logic.launch;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.eva.android.platf.std.AutoUpdateDaemon;
import com.eva.android.widget.Action;
import com.eva.android.widget.ActivityRoot;
import com.eva.android.widget.DataLoadingAsyncTask;
import com.eva.android.x.AppManager;
import com.eva.android.x.AsyncTaskManger;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.eva.epc.core.dto.LoginInfo;
import com.eva.epc.core.dto.LoginInfo2;
import com.eva.epc.core.dto.SysActionConst;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.PreferencesToolkits;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.linkloving.rtring_c_watch.utils.BusinessIntelligence;
import com.linkloving.rtring_c_watch.utils.HttpHelper;
import com.linkloving.rtring_c_watch.utils.IntentFactory;
import com.linkloving.rtring_c_watch.utils.NotificationPromptHelper;
import com.linkloving.rtring_c_watch.utils.SkinSettingManager;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.linkloving.rtring_c_watch.utils.logUtils.LogcatHelper;
import com.linkloving.utils._Utils;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.JobDispatchConst;
import com.rtring.buiness.logic.dto.UserEntity;
import com.rtring.buiness.logic.dto.UserRegisterDTO;
import com.rtring.buiness.util.Des3;

/**
 * 登陆Activity实现类.
 * 
 * @author Jack Jiang, 2013-07-03
 * @version 1.3
 */
public class LoginActivity extends ActivityRoot
{
	private ServiceConnection serviceConnection;
	// 请求码：前往注册页面
	public final static int REQUEST_CODE_FOR_REGISTER = 3;
	
	public final static int QQ_LOGIN_REGISTER = 993;
	
	public final static String TAG = LoginActivity.class.getSimpleName();
	
	/** 
	 * 客户端版本码：读取自程序apk文件的AndroidManifest.xml中的同名属性.
	 * 本字段值将在 {@link #initViews()}前由方法 {@link #initProgrammVersion()}进行初始化.  */
	private static int versionCode = -1;  
	/** 
	 * 客户端版本名：读取自程序apk文件的AndroidManifest.xml中的同名属性.
	 * 本字段值将在 {@link #initViews()}前由方法 {@link #initProgrammVersion()}进行初始化.  */
	private static String versionName = ""; 
	
	/** 文本编辑组件：登陆用户名/email */
	private EditText txtUid = null;
	/** 按钮：登录输入框的记录 */
	private ImageButton mDropBtn = null;
	/** 文本编辑组件：登陆密码 */
	private EditText txtLoginPsw = null;
	/** 选择组件：登陆成功后是否保存最近登陆用户名 */
	private CheckBox cbSaveDefaultLoginName = null;
	/** 按钮：登陆 */
	private Button btnSubmit = null;
	/** 按钮：退出 */
	//private Button btnExit = null;
	/** 按钮：注册 */
	private Button btnRegister = null;
	/** 按钮：忘记了密码？ */
	private Button btnForgot = null;
	
	
//	public static QQAuth mQQAuth;
	private Button btnQQ = null;
	private UserRegisterDTO u = null;
	
	private PopupWindow popView;
	private MyAdapter dropDownAdapter;
	private ListView nameListView = null; 
	/**
	 * 处理回调.
	 * {@inheritDoc}
	 */
	private SkinSettingManager mSettingManager;
	
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch (requestCode)
		{
			case REQUEST_CODE_FOR_REGISTER:
				// 将注册成功后的uid和密码默认填到界面上，方便用户登陆
				if (resultCode == Activity.RESULT_OK)
				{
					//解析从intent中传过来的数据
					//目前用于注册成功后，自动把登陆名和密码传入登陆界面从而方便登陆的场景.
					ArrayList intentDatas = IntentFactory.parseLoginFormIntent(data);
					txtUid.setText((String)intentDatas.get(0));
					try {
						txtLoginPsw.setText(Des3.decode((String)intentDatas.get(1)));
					} catch (Exception e) {
						Log.i(TAG, "解密失败:"+(String)intentDatas.get(1));
					}
				}
				break;
				
			case QQ_LOGIN_REGISTER:
			    if (resultCode == RESULT_OK)
			    {
					String userAge = data.getStringExtra("user_age");
					String userHeight = data.getStringExtra("user_height");
					String userSex = data.getStringExtra("user_sex");
					String userWeight = data.getStringExtra("user_weight");
					
					u.setUser_sex(userSex);
					u.setUser_weight(userWeight);
					u.setUser_height(userHeight);
					u.setBirthdate(_Utils.getBirthdateByAge(Integer.parseInt(userAge)));
					u.setUser_type("1");
					
			    	new RegisterSubmitAsyncTask().execute();
			    }
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mSettingManager=new SkinSettingManager(this);
		mSettingManager.initSkins();
	}

	/** 
	 * Called when the activity is first created.
	 * 本构造方法将额外调用 {@link #init()}完成本类的所有初始化工作. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		//核心初始化方法
		init();
	}
	
	//android2.0前需要重写onKeyDown方法才能实现，2.0及以后直接重写onBackPressed即可哦
	/** 
	 * 捕获back键，实现调用 {@link #doExit(Context)}方法.
	 */
	@Override
	public void onBackPressed()
	{
		doExit(LoginActivity.this
//				, MyApplication.getInstance(this)
				);
	}
	
	/**
	 * <p>
	 * 本类的核心初始化方法.<br>
	 * 本方法一般无需被子类重写，有自定义的定制需求可重写本方法
	 * 调用的几个子方法即可.<br><br>
	 * 
	 * 本类中默认是按照顺序执行几个方法来实现各个初始化任务的.<br>
	 * 执行这些方法的顺序是：<br>
	 * 1) 调用 {@link #initDefaultServices()}实现可能的Intent中传过来的数据的解析和处理、<br>
	 * 2) 调用 {@link #initProgrammVersion()}实现所有UI部分的初始化、<br>
	 * 3) 调用 {@link #initViews()}实现所有UI部分的初始化、<br>
	 * 4) 调用 {@link #initListeners()}实现UI组事件处理监听器的初始化、<br>
	 * </p>
	 * @see #initDefaultServices()
	 * @see #initProgrammVersion()
	 * @see #initViews()
	 * @see #initListeners()
	 */
	protected void init()
	{
//		//初始化一个默认的http服务提 供者地址（即MVC框架中的控制器servlet连接url）
//		initDefaultServices();
//		//初始化程序的版本信息（信息来自apk文件）
//		initProgrammVersion();
		initQQTools();
		//初始化ui组件
		initViews();
		//为ui组件增加事件监听
		initListeners();
	}
	
	protected void initQQTools()
	{
//		mQQAuth = QQAuth.createInstance(MyApplication.QQ_OPEN_APP_ID, LoginActivity.this.getApplicationContext());
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void initViews()
	{
		//设定自定义标题栏（设定此值即意味着开启自定义标题栏的使用，必须要在setContentView前设定）
		customeTitleBarResId = R.id.login_form_titleBar;
		//养成良好习惯：首先设置主layout，确保后绪的操作中使用到的组件都可以被find到
		setContentView(R.layout.login_form);
		//对自定义标题栏中的组件进行设定
		this.getCustomeTitleBar().getLeftBackButton().setVisibility(View.GONE);
		
		txtUid = (EditText)this.findViewById(R.id.login_form_uidEdit);
		txtLoginPsw = (EditText)this.findViewById(R.id.login_form_passwordEdit);
		cbSaveDefaultLoginName =(CheckBox)this.findViewById(R.id.login_form_rememberPswCb); 
		btnSubmit = (Button)this.findViewById(R.id.login_form_submigBtn);
		btnRegister = (Button)this.findViewById(R.id.login_form_registerBtn);
		btnForgot = (Button)this.findViewById(R.id.login_form_forgotPswBtn);
		mDropBtn = (ImageButton)this.findViewById(R.id.login_form_dropdown_button);
		btnQQ = (Button)this.findViewById(R.id.login_form_QQBtn);
		btnQQ.setVisibility(View.GONE);
		//btnExit = this.getCustomeTitleBar().getLeftBackButton();
//		txtUid.setText("435676558@qq.com");
//		txtLoginPsw.setText("123456");
		
		// 在程序里设置文本是为了显示下划线
		btnForgot.setText(Html.fromHtml("<u>"+this.getString(R.string.login_form_btn_forgetpsw)+"</u>"));
		
		//当前版本文本显示
		((TextView)this.findViewById(R.id.login_form_versionView))
			.setText(MessageFormat.format($$(R.string.login_form_version_info), getAPKVersionName(this), getAPKVersionCode(this)));
		//初始化默认登陆用户名，之前登陆成功时会自动把最新登陆用户名记下来的，所以此处可以还原最近用户名
		if(txtUid.getText().length()<=0)
			txtUid.setText(getDefaultLoginName());
		//开启自定义标题栏后，在AndroidManifest.xml设定android:label标题栏上
		//将不会起效，必须在代码中显示调用setTitle哦，具体见ActivityRoot中的说明
		this.setTitle(getText(R.string.login_form_title));
		
		initLoginUserName();
	}
	
	/**
	 * {@inheritDoc}
	 */
	protected void initListeners()
	{
		//登陆按钮事件处理
		btnSubmit.setOnClickListener(new OnClickListener(){			
			@Override
			public void onClick(View v){
				beginLogin();
			}
		});
		btnRegister.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				startActivityForResult(IntentFactory.createRegisterIntent(LoginActivity.this)
						, REQUEST_CODE_FOR_REGISTER);
			}
		});
		btnForgot.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
//				// 功能待实现
//				WidgetUtils.showToast(LoginActivity.this, "找回密码功能暂未实现！", ToastType.WARN);
				startActivity(IntentFactory.createForgetPassWordIntent(LoginActivity.this));
			}
		});
		//退出按钮事件处理
//		btnExit.setOnClickListener(new OnClickListener(){
//		@Override
//		public void onClick(View v)
//		{
//				doExit(LoginActivity.this, MyApplication.getInstance(LoginActivity.this));
//			}
//		});
		
		mDropBtn.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				if (popView != null)
				{
					if (!popView.isShowing())
					{
						popView.showAsDropDown(txtUid);
					}
					else
					{
						popView.dismiss();
					}
				}
				else
				{
					String[] usernames = PreferencesToolkits.getLoginNames(LoginActivity.this);
					// 如果有已经登录过账号
					if (usernames != null && usernames.length > 0)
					{
						//String[]
						initPopView(usernames);
						if (!popView.isShowing())
						{
							popView.showAsDropDown(txtUid);
						}
						else
						{
							popView.dismiss();
						}
					}
					else
					{
						Toast.makeText(LoginActivity.this, R.string.general_nodata, Toast.LENGTH_LONG).show();
					}
				}
			}
		});

	}

	private void initLoginUserName()
	{
		String[] usernames = PreferencesToolkits.getLoginNames(LoginActivity.this);
		if (usernames != null && usernames.length > 0)
		{
			String tempName = usernames[usernames.length - 1];
			txtUid.setText(tempName);
			txtUid.setSelection(tempName.length());
			String tempPwd = PreferencesToolkits.getLoginPswByLoginName(LoginActivity.this, tempName);
			txtLoginPsw.setText(tempPwd);
		}
		txtUid.addTextChangedListener(new TextWatcher()
		{

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				txtLoginPsw.setText("");
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{

			}

			@Override
			public void afterTextChanged(Editable s)
			{

			}
		});
	}
	
	
	class MyAdapter extends SimpleAdapter
	{

		private List<HashMap<String, Object>> data;

		public MyAdapter(Context context, List<HashMap<String, Object>> data, int resource, String[] from, int[] to)
		{
			super(context, data, resource, from, to);
			this.data = data;
		}

		@Override
		public int getCount()
		{
			return data.size();
		}

		
		public void setData(List<HashMap<String, Object>> data)
		{
			this.data = data;
		}
		
		@Override
		public Object getItem(int position)
		{
			return position;
		}
		
		@Override
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			System.out.println(position);
			ViewHolder holder;
			if (convertView == null)
			{
				holder = new ViewHolder();
				convertView = LayoutInflater.from(LoginActivity.this).inflate(R.layout.dropdown_item, null);
				holder.btn = (ImageButton) convertView.findViewById(R.id.delete);
				holder.tv = (TextView) convertView.findViewById(R.id.textview);
				convertView.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			
			holder.tv.setText(data.get(position).get("name").toString());
			holder.tv.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					String[] usernames = PreferencesToolkits.getLoginNames(LoginActivity.this);
					txtUid.setText(usernames == null ? "" : usernames[position]);
					txtLoginPsw.setText(PreferencesToolkits.getLoginPswByLoginName(LoginActivity.this, usernames[position]));
					popView.dismiss();
				}
			});
			
			holder.btn.setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
					String[] usernames = PreferencesToolkits.getLoginNames(LoginActivity.this);
					if (usernames != null && usernames.length > 0)
					{
						PreferencesToolkits.removeLoginInfo(LoginActivity.this, usernames[position]);
					}
					String[] newusernames = PreferencesToolkits.getLoginNames(LoginActivity.this);
					if (usernames != null && newusernames.length > 0)
					{
						updatePopView(newusernames);
						if(!popView.isShowing())
							popView.showAsDropDown(txtUid);
					}
					else
					{
						popView.dismiss();
						popView = null;
					}
				}
			});
			return convertView;
		}
	}
	
	class ViewHolder
	{
		private TextView tv;
		private ImageButton btn;
	}
	
	private void initPopView(String[] usernames)
	{
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < usernames.length; i++)
		{
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("name", usernames[i]);
			map.put("drawable", R.drawable.xicon);
			list.add(map);
		}
		dropDownAdapter = new MyAdapter(this, list, R.layout.dropdown_item, new String[] { "name", "drawable" }, new int[] { R.id.textview, R.id.delete });
		nameListView = new ListView(this);
		nameListView.setAdapter(dropDownAdapter);

		popView = new PopupWindow(nameListView, txtUid.getWidth(), ViewGroup.LayoutParams.WRAP_CONTENT, true);
		popView.setFocusable(true);
		popView.setOutsideTouchable(true);
		popView.setBackgroundDrawable(getResources().getDrawable(R.drawable.white));
		// popView.showAsDropDown(txtUid);
	}
	
	private void updatePopView(String[] usernames)
	{
		List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < usernames.length; i++)
		{
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("name", usernames[i]);
			map.put("drawable", R.drawable.xicon);
			list.add(map);
		}
		dropDownAdapter.setData(list);
		dropDownAdapter.notifyDataSetChanged();
	}
	/**
	 * <p>
	 * 实施登陆的方法.<br>
	 * 
	 * 本方法中将判断登陆用名和密码的字面合法性（比如不能为空等）.
	 * 然后执行LoginImplAsyncTask实现异步登陆处理.
	 * </p>
	 */
	private void beginLogin()
	{
		if(String.valueOf(txtUid.getText()).trim().length()<=0){
			txtUid.setError($$(R.string.login_form_validate_login_name_empty));
			return;
		}
		if(txtLoginPsw.getText().length()<=0){
			txtLoginPsw.setError($$(R.string.login_form_validate_login_psw_length_less_six));
			return;
		}
		
		// 首先启动客户端版本检查和更新线程（目前这种方式体验不是
		// 很好，但受限于AnyChat现时只能这样，以后再优化吧）
		new UpdateClientAsyncTask().execute(constructLoginInfo());
	}
	
	/**
	 * <p>
	 * 返回最近陆的用户名.
	 * 它是使用SharedPreferences机制进行存放的.
	 * </p>
	 * @return
	 * @see SharedPreferences#getString(String, String)
	 */
	protected String getDefaultLoginName()
	{
//		//取出最近登陆过的用户名
//		SharedPreferences nameSetting = getPreferences(MODE_PRIVATE);
//		return nameSetting.getString(PreferencesToolkits.SHARED_PREFERENCES_KEY_LOGIN$NAME,"");
		List<LoginInfo2> ai = PreferencesToolkits.getLoginInfo(this);
		return (ai != null && ai.size() != 0) ? ai.get(0).getLoginName() : null;
	}
	/**
	 * 调用本方法实现对用户名的保存(以备下次登陆时无需再次输入).
	 * 
	 * @param loginName
	 * @see SharedPreferences.Editor#putString(String, String)
	 */
	protected void saveDefaultLoginName(String loginName, String psw)
	{
//		SharedPreferences nameSetting=getPreferences(MODE_PRIVATE);
//		SharedPreferences.Editor namePref=nameSetting.edit();
//		namePref.putString(PreferencesToolkits.SHARED_PREFERENCES_KEY_LOGIN$NAME, loginName);
//		namePref.commit();
		LoginInfo2 ai = new LoginInfo2();
		ai.setLoginName(loginName);
		ai.setLoginPsw(psw);
		PreferencesToolkits.addLoginInfo(this, ai);
	}
	/**
	 * 调用本方法实现删除之前保存过的最近登陆用户名.
	 * 
	 * @see SharedPreferences.Editor#remove(String)
	 */
	protected void removeDefaultLoginName(String loginName)
	{
//		SharedPreferences nameSetting=getPreferences(MODE_PRIVATE);
//		SharedPreferences.Editor namePref=nameSetting.edit();
//		namePref.remove(PreferencesToolkits.SHARED_PREFERENCES_KEY_LOGIN$NAME);
//		namePref.commit();
		PreferencesToolkits.removeLoginInfo(this, loginName);
	}
	
	private LoginInfo constructLoginInfo()
	{
		String password = null;

		try
		{
			password = Des3.encode(String.valueOf(this.txtLoginPsw.getText()).trim());
//			password = String.valueOf(this.txtLoginPsw.getText()).trim();
		}
		catch (Exception e)
		{
			Log.e("[LZ]=====================", e.getMessage());
			Toast.makeText(this, "密码加密失败. 原文密码：" + String.valueOf(this.txtLoginPsw.getText()).trim(), Toast.LENGTH_SHORT).show();
		}
		
		BusinessIntelligence bi = new BusinessIntelligence(LoginActivity.this);
		String biObj = JSON.toJSONString(bi, true);
		final String uidOrMail = String.valueOf(this.txtUid.getText()).trim().toLowerCase();
		LoginInfo ai = new LoginInfo()
		.setLoginName(uidOrMail)
		.setLoginPsw(password)
		.setClientVersion(""+getAPKVersionCode(this))//本字段时存放的就是客户端APK的真正版本码
		.setDeviceInfo(biObj);
		return ai;
	}
	/**
	 * <p>
	 * 典型情况下子类在本方法里实现点击登陆按钮的事件响应.<br>
	 * 移动通里是由登陆处理异步类LoginImplAsyncTask来调用的.
	 * 
	 * 本方法是调用{@link #loginImpl(LoginInfo)}实现用户登陆的
	 * ，另：子类可重写此方法实现是否成功登陆等判断的实现.
	 * </p>
	 * 
	 * @return 返提交到服务端处理完登陆信息后返回的数据对象，子类中可以据此对象判断是否成功登陆等
	 * @see LoginImplAsyncTask
	 */
	protected void doLogin()
	{
//		LoginInfo ai = constructLoginInfo(); 
		
//		// 保存起来，以备后绪的自动重连中使用
//		MyApplication.getInstance(this).setUidOrMailForLogin(ai.getLoginName());
		// 登陆成功后额外要做的事
		afterLoginSucess();
	}
	
	/**
	 * 是否需要保存用户最近登陆的用户名.
	 * true表示需要，否则不需要.
	 * 
	 * @return
	 */
	protected boolean needSaveDefaultLoginName()
	{
		return cbSaveDefaultLoginName.isChecked();
	}

	/**
	 * 用户成功登陆后将立即调用本方法.
	 * 默认情况下本方法什么也不做，子类可自行实现之.
	 * 
	 * @param uid 登陆成功后得到的服务端端返回的用户完整信息对象
	 * @see LoginImplAsyncTask
	 */
	protected void afterLoginSucess()
	{
		final String uidOrMail = String.valueOf(this.txtUid.getText()).trim();
		
		//需要保存最近登陆的用户名
		if(needSaveDefaultLoginName())//checkBox.isChecked())
			saveDefaultLoginName(uidOrMail, String.valueOf(this.txtLoginPsw.getText()));
//		//不需要保存（并删除之前存存储的用户名）
//		else
//			removeDefaultLoginName();
		
//		Intent intent = new Intent(LoginActivity.this, PushService.class);
//		startService(intent);
//		serviceConnection = new ServiceConnection()
//		{
//			@Override
//			public void onServiceDisconnected(ComponentName name)
//			{
//			}
//
//			@Override
//			public void onServiceConnected(ComponentName name, IBinder service)
//			{
//				// try
//				// {
//				// IRemoteService remoteService =
//				// IRemoteService.Stub.asInterface(service);
//				// remoteService.login();
//				// finish();
//				// }
//				// catch (RemoteException e)
//				// {
//				// Log.e(TAG, e.getMessage());
//				// }
//			}
//		};
//		// 登陆实时推送
//		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
		
		// TODO 实现前往主页面的功能！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
//		// 前往门户主页
//		startActivity(IntentFactory.createPortalIntent(LoginActivity.this));
//		startActivity(new Intent(this, MainPageActivity.class));
		// !!!!
//		startActivity(new Intent(this, ReportActivity.class));
//		startActivity(new Intent(this, MoreActivity.class));
	    startActivity(IntentFactory.createPortalActivityIntent(this));	
//		WidgetUtils.showToast(this, "登陆成功！！！", ToastType.OK);
		//
		finish();
	}
	
	/**
	 * <p>
	 * 退出登陆实用方法.
	 */
	public static void doLogout(Context context, final Observer obsForSucess)
	{
		MyApplication.getInstance(context).releaseAll();
		// * 处理完成后通知观察者做其它事哦
		if(obsForSucess != null)
			obsForSucess.update(null, null);
	}
	
	/**
	 * <p>
	 * 退出程序.
	 * </p>
	 * @see #doExit(Context, Action)
	 */
	public static void doExit(final Context context)
	{
		doExit(context, null);
	}
	/**
	 * <p>
	 * 退出程序.<br><br>
	 * 
	 * 本方法回弹出一个选择对话框，选是才真正实施退出，否则将调用参数
	 * cancelAction指明的要执行的动作.子类如在退出时有其它需要关闭的
	 * 事情可重写本方法.<br><br>
	 * 
	 * 实现退出的过程是：先调用方法 {@link #doLogout(Context)}、再调
	 * 用 {@link System#exit(int)}实现的.
	 * </p>
	 * 
	 * @param context
	 * @param cancelAction 点击确认对话框“取消”时要额外完成的动作，本参数可
	 * 为null（意味着不需要处理额外动作）
	 * @see #doLogout(Context)
	 * @see System#exit(int)
	 */
	public static void doExit(final Context context, final Action cancelAction)
	{
		new com.eva.android.widgetx.AlertDialog.Builder(context)
			.setTitle(context.getResources().getString(R.string.general_prompt))  
			.setMessage(context.getResources().getString(R.string.login_form_exit_app_tip))
			.setPositiveButton(context.getResources().getString(R.string.general_ok),  new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog,int which)
				{
					doExitNoConfirm(context);
				}
			}) 
		.setNegativeButton(context.getResources().getString(R.string.general_cancel), null)
		.show(); 
	}
	public static void doExitNoConfirm(final Context context)
	{
		//注销回话
		doLogout(context, new Observer(){// 为何要使用观察者？因退出登陆数据发送是在异步线程中实现的，
										 // 使用观察者的目的是确保退出JVM等是在数据已发送完成后再行的哦！！！！
			@Override
			public void update(Observable observable, Object data)
			{
				systemExit(context);
				//这个判断是 用户是否已经登录的情况下 退出app
			}
		});
	}
	
	public static void systemExit(Context context)
	{
//		// 2012-08-15 by js： add this line? see Doc of System.exit(..)
//		System.runFinalizersOnExit(true);
//		//退出进程
//		System.exit(0);
		
		// 清除APP产生的所有Notification
		NotificationPromptHelper.cancalAllNotification(context);
		// 退出
		AppManager.getAppManager().AppExit(context);
//		MyApplication.getInstance(context).releaseBLE();
		Log.i(TAG, "哈哈哈哈哈哈！老子闪退了");
	}
	
	
	/**
	 * 返回本程序对应APK文件的versionCode.
	 * 
	 * @return
	 */
	public static int getAPKVersionCode(Context context)
	{
		initProgrammVersion(context);
		return versionCode;
	}
	/**
	 * 返回本程序对应APK文件的versionName.
	 * 
	 * @return
	 */
	public static String getAPKVersionName(Context context)
	{
		initProgrammVersion(context);
		return versionName;
	}
	/**
	 * <p>
	 * 初始化版本信息.<br>
	 * 
	 * 读取程序apk文件的AndroidManifest.xml中的versionCode和versionName属性
	 * 并存储在本类同名字段中以备后用.
	 * </p>
	 */
	private static void initProgrammVersion(Context context)
	{
		if(versionCode == -1)
		{
			PackageInfo info;
			try
			{
				info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
				versionCode = info.versionCode;   
				versionName = info.versionName;   
			}
			catch (NameNotFoundException e)
			{
//				WidgetUtils.showToast(this, "Update version error.", ToastType.WARN);
				Log.d(TAG, "读程序版本信息时出错,"+e.getMessage(),e);
			}
		}
	}
	
	/**
	 * 重新登陆。
	 */
	public static void relogin(Activity context)
	{
		// 尝试关闭正在运行中的蓝牙（否则还会在后台运行！）
		MyApplication.getInstance(context).releaseAll();
//		// 及时清除此Observer，以便在重新登陆时，正在运行中的蓝牙处理不会因此activity的回收而使回调产生空指针等异常
//		// 为何要在此处显示处理呢？因为PortalMainActivity的finish不会即诉调用onDestroy方法，为了保证做到这一点，所以
//		// 显式来处理之！
//		BLEProvider provider = MyApplication.getInstance(context).getCurrentHandlerProvider();
//		if(provider != null)
//		{
//			provider.setCurrentDeviceMac(null);
//			provider.setBleProviderObserver(null);
//			// 重置状态（防止disconnect完成的异步通知还没有收到就close而导致provider中的state不能重置的问题）
//			provider.resetDefaultState();
//		}
		
		//设置评论内容
		MyApplication.getInstance(context).setCommentNum(0);
				
		// 尝试结束掉正在运行中的异步线程（否则将因Activity的finish而产生空指针），准确地说是PortalMainActivity中的AsyncTask
		AsyncTaskManger.getAsyncTaskManger().finishAllAsyncTask();
		
		//** 【关于重新登陆实现的说明】重新登陆的目的是注销原会话，重新登陆，代码可参考LoginActivity类中的doExitNoConfirm(..)
		//** 方法，但也不能完全一样，至少不能退出程序、不能调用MyApplication.releaseAll等。注意：此处的重登实际上Application
		//** 还是完整的，一定要保证该重置的重置哦。最好的方式是如果能像微信一样重新启动应用则是最好的，但没找到好方法来实现！！！
		// 重置自动上传检查开关
		MyApplication.getInstance(context).setPreparedForOfflineSyncToServer(false);
		// 清除APP产生的所有Notification
		NotificationPromptHelper.cancalAllNotification(context);
		
		// TODO 退出时要关闭与服务端的会话，与LoginActivity.doExitNoConfirm(..)保持一致即可

		//** 重新开启登陆界面
		MyApplication.getInstance(context).setLocalUserInfoProvider(null); // 把用信息置空（它会自动把本地存储的也清空！）
		Intent intent = new Intent(context, LoginActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 注意此行！
		context.startActivity(intent);
		context.finish();
	}
	
	/**
	 * 提交用户登陆请求到服务端并接收处理结果的异常线程实现类.
	 * <p>
	 * 本类相对于另一包中的同名类：{@link com.linkloving.rtring_c_watch.utils.UpdateClientAsyncTask}，
	 * 区别是本类仅用于登陆界面中，点击“登陆”按钮后进行的基本用户身份认证，表示新登陆。
	 * 而{@link com.linkloving.rtring_c_watch.utils.UpdateClientAsyncTask}用于免登陆时进入程序后的
	 * 身份验证（防止免登陆期间在其它手机上修改过密码）、版本检查等。
	 * <p>
	 * 实际上，为了简化认证处理和免登陆的验证，新登陆（账号首次登陆或切换用户重新登陆）时也会在进入主界面
	 * 时调用{@link com.linkloving.rtring_c_watch.utils.UpdateClientAsyncTask}，虽有重复，但闹钟设置等
	 * 的离线同步机制是在此时实现的，所以也是必要的。而且此类的简单调用能极大简化免登陆场景下的认证处理等。
	 * 
	 * @see com.linkloving.rtring_c_watch.utils.UpdateClientAsyncTask
	 */
	protected class UpdateClientAsyncTask extends DataLoadingAsyncTask<Object, Integer, DataFromServer>
	{
		public UpdateClientAsyncTask()
		{
			super(LoginActivity.this, $$(R.string.login_form_loading_login));
		}
		
		/**
		 * 在后台执行 {@link doLogin()}实现登陆信息的提交和处于结果的读取 .
		 * 
		 * @param parems 外界传进来的参数
		 * @return 查询结果，将传递给onPostExecute(..)方法 
		 */
		@Override
		protected DataFromServer doInBackground(Object... parems) 
		{
			LoginInfo ai = (LoginInfo)parems[0];
			return HttpHelper.submitLoginToServer(ai);
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
		
			if(result != null && result instanceof String && !ToolKits.isJSONNullObj((String)result))// JSON文本字符串（目前服务端返回JSON文本即表示是RosterEntity对象的JSON，即表示正常登陆了）
			{
				Log.e(TAG, "收到服务端的返回，result.class="+result.getClass()+", result="+result);
				UserEntity userAuthedInfo = HttpHelper.parseLoginFromServer((String)result);
				if(userAuthedInfo != null)
				{
				}
				// 没有强制更新需求，无条件登陆之
				//** 【注意】当正常登陆时，以下代码一定要与免登陆时成功后的代码保持一致！！！！！！！！！
				//** 【注意】当正常登陆时，以下代码一定要与免常登陆时成功后的代码保持一致！！！！！！！！！
				//** 【注意】当正常登陆时，以下代码一定要与免常登陆时成功后的代码保持一致！！！！！！！！！
				//---------------------------------------------------------------------------- [1]处代码与[2]是一样的 S
				// 把本地用户信息保存到全局变量备用哦
				MyApplication.getInstance(LoginActivity.this).setLocalUserInfoProvider(userAuthedInfo);
//				// 2014-06-13日把完整的UserEntity对象保存起来备用（用于接下来的免登陆功能）
//				PreferencesToolkits.setLocalUserInfo(LoginActivity.this, userAuthedInfo);
				
				// 更新检查（处理）完成后开始进入连接聊天服务器的过程
				// （TODO 以后将改进为登陆验证和登陆聊天服务器在1次提交中完成！！现在是测试时先用AnyChat的模式用着！！）
//				new LoginImplAsyncTask().execute();
				doLogin();
				//---------------------------------------------------------------------------- [1]处代码与[2]是一样的 E
			}
			// 登陆认证失败
			else// 目前服务端如果认证失败（比如密码、用户不正确等，查出来的对象是null返回给客户端后的JSON也会被解析成null对象！
			{
				// FIXBUG: google统计到较大量IllegalArgumentException (@LoginActivity$UpdateClientAsyncTask:onPostExecuteImpl:1075) {main}
				//         错误，此if语句是为了保证延迟线程里不会因Activity已被关闭而此处却要非法地执行show的情况（此判断可趁为安全的show方法哦！）
				if (LoginActivity.this != null && !LoginActivity.this.isFinishing())
				{
					new com.eva.android.widgetx.AlertDialog.Builder(LoginActivity.this)
					.setTitle(R.string.login_form_error_psw_tip)  
					.setMessage(R.string.login_form_error_psw_message)
					.setPositiveButton(R.string.general_ok,  null)  
					.show(); 
				}
			}
		}
	}
	
	
	/**
	 * 提交注册数据的异步多线程封装类.
	 */
	protected class RegisterSubmitAsyncTask extends DataLoadingAsyncTask<String, Integer, DataFromServer>
	{
		public RegisterSubmitAsyncTask()
		{
			super(LoginActivity.this, false);
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
			return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService()
					.sendObjToServer(DataFromClient.n()
							.setProcessorId(MyProcessorConst.PROCESSOR_THIRD_PARTY_LOGIC)
							.setJobDispatchId(JobDispatchConst.THIRD_PARTY_LOGIC_BASE)
							.setActionId(SysActionConst.ACTION_APPEND2)
							.setNewData(JSON.toJSONString(u)));
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
			if(result != null)
			{
				UserEntity ru = JSON.parseObject((String)result, UserEntity.class);
				
				//切换账号
				BLEProvider  provider = MyApplication.getInstance(LoginActivity.this).getCurrentHandlerProvider();
				provider.disConnect();
				MyApplication.getInstance(LoginActivity.this).setLocalUserInfoProvider(ru, false);
				PreferencesToolkits.setLocalUserInfo(LoginActivity.this, ru);
				provider.setCurrentDeviceMac(ru.getLast_sync_device_id().toUpperCase());
				LoginActivity.this.startActivity(IntentFactory.createPortalActivityIntent(LoginActivity.this));	
				LoginActivity.this.finish();
				
				
			}
//			else
//			{
//				new com.eva.android.widgetx.AlertDialog.Builder(LoginActivity.this)  
//					.setTitle(R.string.general_error)
//					.setMessage("QQ登录异常.")
//					.setPositiveButton(R.string.general_ok,   null)
//					.setNegativeButton(R.string.general_cancel, null).show();
//				return;
//			}
		}
	}

}
