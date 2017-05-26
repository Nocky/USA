package com.linkloving.rtring_c_watch.logic.more;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.text.ParseException;

import org.json.JSONException;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eva.android.platf.std.AutoUpdateDaemon;
import com.eva.android.widget.DataLoadingAsyncTask;
import com.eva.android.widget.util.WidgetUtils;
import com.eva.android.widget.util.WidgetUtils.ToastType;
import com.eva.epc.common.util.CommonUtils;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.eva.epc.core.dto.SysActionConst;
import com.example.android.bluetoothlegatt.BLEHandler;
import com.example.android.bluetoothlegatt.BLEHandler.BLEProviderObserverAdapter;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.google.gson.Gson;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.PreferencesToolkits;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.linkloving.rtring_c_watch.logic.DataLoadableMultipleAcitvity;
import com.linkloving.rtring_c_watch.logic.more.avatar.ProfileAvatarChangeWrapper;
import com.linkloving.rtring_c_watch.logic.more.avatar.ShowUserAvatar;
import com.linkloving.rtring_c_watch.logic.setup.BodyActivity;
import com.linkloving.rtring_c_watch.utils.DeviceInfoHelper;
import com.linkloving.rtring_c_watch.utils.HttpSnsHelper;
import com.linkloving.rtring_c_watch.utils.IntentFactory;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.linkloving.utils._Utils;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.JobDispatchConst;
import com.rtring.buiness.logic.dto.UserEntity;
import com.rtring.buiness.logic.dto.UserRegisterDTO;
import com.rtring.buiness.util.Des3;

public class UserActivity extends DataLoadableMultipleAcitvity
{
	private final String REQ_WHATSUP_COUNT = "req_whatsup_count";
	
	private static final int UPDATE_BODY_INFO_FROM_USER = 995;
	
	//修改密码
	private static final String IS_CHANGE_PASSWORD = "1";
	private static final String IS_NOT_CHANGE_PASSWORD = "0";
	private static final String IS_CHANGE_WHATSUP = "2";
	
	private TextView nameTextView;
	private TextView emailTextView;
	private TextView bodyInfoTextView;
	private TextView whatsupTextView;
	private TextView whatsupCountTextView;
	private TextView whatsupCommentsTextView;
	
	private ImageView viewAvatar = null;
	private Button changePswBtn;
	private Button nickNameLinerLayout;
	private Button bodyLinerLayout;
	private Button ChildBtn = null;
	private Button whatsupLinerLayout;
	
	
	private UserEntity u;
	private UserEntity dataU; 
	
	private BLEProvider provider;
	
	private BLEProviderObserverAdapter bleProviderObserver = new BLEHandler.BLEProviderObserverAdapter(){
		@Override
		public void updateFor_notifyForDeviceAloneSyncSucess_D()
		{
//		    Toast.makeText(UserActivity.this, "", Toast.LENGTH_SHORT).show();
			WidgetUtils.showToast(UserActivity.this, $$(R.string.debug_device_set_success), ToastType.OK);
		}

		@Override
		protected Activity getActivity()
		{
			return UserActivity.this;
		}

		@Override
		public void updateFor_handleSetTime() {
			// TODO Auto-generated method stub
			
		}

	};
	
	/** 本地用户图像管理包装实现类 */
	private ProfileAvatarChangeWrapper profilePhotoWrapper = null;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		provider = MyApplication.getInstance(this).getCurrentHandlerProvider();
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
//		provider = MyApplication.getInstance(this).getProvider(setAllDeviceInfoHandler);
		provider.setBleProviderObserver(bleProviderObserver);
		refreshDatas();
	}
	
	protected void onDestroy()
	{
		super.onDestroy();
		
		if(provider.getBleProviderObserver() == bleProviderObserver)
			provider.setBleProviderObserver(null);
	}
	
	@Override
	protected void initViews()
	{
		customeTitleBarResId = R.id.user_info_title_bar;
		setContentView(R.layout.user_info);
		
		nameTextView = (TextView) findViewById(R.id.user_info_name_text);
		emailTextView = (TextView) findViewById(R.id.user_info_email_text);
		bodyInfoTextView = (TextView) findViewById(R.id.user_info_body_info_text);
		whatsupTextView = (TextView)findViewById(R.id.user_info_update_whatsup_text);
		whatsupCountTextView = (TextView) findViewById(R.id.user_info_update_whatsup_count_text);
		Object[]  args = {0};
		String msg = MessageFormat.format($$(R.string.user_info_update_whats_up_count), args);
		whatsupCountTextView.setText(msg);
		whatsupCommentsTextView = (TextView) findViewById(R.id.user_info_update_comments_count_text);
		changePswBtn = (Button)findViewById(R.id.user_info_passwordBtn);
		viewAvatar = (ImageView)findViewById(R.id.main_more_settings_avatarView);
		ChildBtn = (Button) findViewById(R.id.user_info_child_btn);
		ChildBtn.setVisibility(View.GONE);
		
		whatsupLinerLayout = (Button) findViewById(R.id.user_info_what_s_up_btn);
		nickNameLinerLayout = (Button) findViewById(R.id.user_info_nick_nameBtn);
		bodyLinerLayout = (Button)findViewById(R.id.user_info_body_btn);
		setTitle(R.string.user_info_title);
		
		u = MyApplication.getInstance(this).getLocalUserInfoProvider();
		
		if(u != null)
			loadData(false, HttpSnsHelper.GenerateWhatsUpCountParams(u.getUser_id()), REQ_WHATSUP_COUNT);
		
		refreshDatas();
		
		//
		profilePhotoWrapper = new ProfileAvatarChangeWrapper(this, findViewById(R.id.user_info_MainLL));
		
		// 现在只在onCreate时刷新本地用户头像
		if(u != null)
		{
			// 更新本地用户头像
			new ShowUserAvatar(this, u.getUser_id()
					, viewAvatar
					// 注意：打开本界面时如果存在本地缓存则就不需要尝试去服务端更新
					// 最新了（这跟MoreActivity里的逻辑不一样哦），因为前一个MoreActivity界面
					// 里已经尝试过了，本界面就没有必要了，节约服务器端性能压力吧
					, false 
					, 120 , 120 // 此头像用于界面中好友的头像显示，大小参考：user_info布局中的@+id/main_more_settings_avatarView（60dp*60dp）
			).showCahedAvatar();
		}
	}
	
	@Override
	protected void initListeners() 
	{
//		ChildBtn.setOnClickListener(new OnClickListener()
//		{
//			
//			@Override
//			public void onClick(View v)
//			{
//				startActivity(IntentFactory.createChildActivityIntent(UserActivity.this));
//			}
//		});
//		//* KChat2.3起，为提升用户体验，去掉了此处点击查看大图的功能
//		// 点击头像看大图
//		((ImageView) this.findViewById(R.id.main_more_settings_avatarView)).setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v)
//			{
//				if(u != null)
//					AvatarHelper.showAvatarImage(UserActivity.this, u.getUser_id()
//							, new Observer()
//							{
//								@Override
//								public void update(Observable observable,
//										Object data)
//								{
//									// 如果还没有上传头像则显示头像上传窗口
//									if(profilePhotoWrapper != null)
//										profilePhotoWrapper.shotAvatarChage();
//								}
//							});
//			}
//		});
		nickNameLinerLayout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				
				LayoutInflater inflater = getLayoutInflater();
				
				final View layout = inflater.inflate(R.layout.user_info_update_user_nickname, (LinearLayout) findViewById(R.id.user_info_update_user_nickname_LL));
				final EditText nicknameView = (EditText) layout.findViewById(R.id.user_info_update_user_nicknameView);
				
				nicknameView.setText(u == null?"":u.getNickname());
				new com.eva.android.widgetx.AlertDialog.Builder(UserActivity.this)
				.setTitle($$(R.string.user_info_update_nick_name_title))
				.setView(layout)
				.setPositiveButton($$(R.string.general_change),  new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog,int which)
					{
						if(!CommonUtils.isStringEmpty(nicknameView.getText().toString())){
							if(!u.getNickname().equals(nicknameView.getText().toString().trim())){
								dataU = u;
								dataU.setNickname(nicknameView.getText().toString());
								new DataAsyncTask().execute(IS_NOT_CHANGE_PASSWORD);
							}
						}
						else
						{
							Toast.makeText(UserActivity.this, R.string.user_info_update_nick_name_validate, Toast.LENGTH_LONG).show();
						}
					}
				}) 
				.setNegativeButton($$(R.string.general_cancel), null)
				.show(); 
			}
		});
		
		whatsupLinerLayout.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				UserEntity u = MyApplication.getInstance(UserActivity.this).getLocalUserInfoProvider();
				if(u != null)
				    startActivity(IntentFactory.createWhatsUpHistoryActivityIntent(UserActivity.this,u.getUser_id()));
//				LayoutInflater inflater = getLayoutInflater();
//				final View layout = inflater.inflate(R.layout.user_info_update_whatsup
//						, (LinearLayout) findViewById(R.id.user_info_update_what_s_up_LL));
//				final EditText whatsupView = (EditText) layout.findViewById(R.id.user_info_update_whatsupView);
//				
//				whatsupView.setText(u == null ? "" : (String) u.getWhat_s_up());
//				new com.eva.android.widgetx.AlertDialog.Builder(UserActivity.this)
//				.setTitle($$(R.string.user_info_what_s_up))
//				.setView(layout)
//				.setPositiveButton($$(R.string.general_change),  new DialogInterface.OnClickListener(){
//					@Override
//					public void onClick(DialogInterface dialog,int which)
//					{
////						if(!CommonUtils.isStringEmpty(whatsupView.getText().toString()))
////						{
//							if(u.getWhat_s_up() != null && !u.getWhat_s_up().equals(whatsupView.getText().toString().trim()))
//							{
//								dataU = u;
//								dataU.setWhat_s_up(whatsupView.getText().toString());
//								new DataAsyncTask().execute(IS_CHANGE_WHATSUP);
//							}
////						}
////						else
////						{
////							Toast.makeText(UserActivity.this, "Please enter something.", Toast.LENGTH_LONG).show();
////						}
//					}
//				}) 
//				.setNegativeButton($$(R.string.general_cancel), null)
//				.show(); 
			}
		});
		
		bodyLinerLayout.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
			
				                               
				startActivityForResult(IntentFactory.createBodyActivityIntent(UserActivity.this, u, BodyActivity.USER_ACTIVITY,""), UPDATE_BODY_INFO_FROM_USER);
//			    LayoutInflater inflater = getLayoutInflater();
//				
//				final View layout = inflater.inflate(R.layout.user_info_update_user_body, (LinearLayout) findViewById(R.id.user_info_update_user_body_LL));
//				final EditText heightView = (EditText) layout.findViewById(R.id.user_info_update_user_height);
//				final EditText weightView = (EditText) layout.findViewById(R.id.user_info_update_user_weight);
//				final EditText ageView = (EditText) layout.findViewById(R.id.user_info_update_user_age);
//				final RadioButton manRB = (RadioButton)layout.findViewById(R.id.user_info_update_user_manCb);
//				final RadioButton womanRB = (RadioButton)layout.findViewById(R.id.user_info_update_user_womanCb);
//				heightView.setText(u.getUser_height());
//				weightView.setText(u.getUser_weight());
//				manRB.setChecked(u.getUser_sex().equals("1"));
//				womanRB.setChecked(u.getUser_sex().equals("0"));
//				ageView.setText(ToolKits.getAgeByBirthdate(u.getBirthdate(), "yyyy-MM-dd"));
//				
//				new com.eva.android.widgetx.AlertDialog.Builder(UserActivity.this)
//				.setTitle($$(R.string.user_info_update_body_title))
//				.setView(layout)
//				.setPositiveButton($$(R.string.general_change),  new DialogInterface.OnClickListener(){
//					@Override
//					public void onClick(DialogInterface dialog, int which)
//					{
//						//验证是否满足修改密码的若干条件
//						boolean valid = updateBodyByVlidate(heightView, weightView, ageView);
//						if(valid)
//						{
//							dataU = new UserEntity();
//							dataU.setUser_id(u.getUser_id());
//							dataU.setUser_height(String.valueOf(heightView.getText()));
//							dataU.setUser_weight(String.valueOf(weightView.getText()));
//							dataU.setUser_sex(manRB.isChecked() ? "1" : "0");
//							dataU.setBirthdate(ToolKits.getBirthdateByAge(Integer.parseInt(String.valueOf(ageView.getText()))));
//							new DataAsyncTask().execute(IS_UPDATE_BODY_INFO);
//							allowCloseDialog(dialog, true);
//						}
//						else
//						{
//							//不允许关闭dialog
//							allowCloseDialog(dialog, false);
//						}
//					}
//				}) 
//				.setNegativeButton($$(R.string.general_cancel), new DialogInterface.OnClickListener(){
//					@Override
//					public void onClick(DialogInterface dialog, int which)
//					{
//						allowCloseDialog(dialog, true);
//					}
//				})
//				.show(); 
			}
		});
		
		
		changePswBtn.setOnClickListener(new View.OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				LayoutInflater inflater = getLayoutInflater();
				
				final View layout = inflater.inflate(R.layout.user_info_update_user_psw, (LinearLayout) findViewById(R.id.user_info_update_user_psw_LL));
				final EditText oldPswView = (EditText) layout.findViewById(R.id.user_info_update_user_psw_old_psw);
				final EditText newPswView = (EditText) layout.findViewById(R.id.user_info_update_user_psw_new_psw);
				final EditText reNewPswView = (EditText) layout.findViewById(R.id.user_info_update_user_psw_repeat_new_psw);
				oldPswView.setText("");
				newPswView.setText("");
				reNewPswView.setText("");
				
				new com.eva.android.widgetx.AlertDialog.Builder(UserActivity.this)
				.setTitle($$(R.string.user_info_update_psw_title))
				.setView(layout)
				.setPositiveButton($$(R.string.general_change),  new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						//验证是否满足修改密码的若干条件
						boolean valid = updatePswByVlidate(oldPswView, newPswView, reNewPswView);
						if(valid)
						{
							String newPsw = String.valueOf(newPswView.getText());
							String oldPsw = String.valueOf(oldPswView.getText());
							try
							{
								newPsw = Des3.encode(String.valueOf(newPswView.getText()));
								oldPsw = Des3.encode(String.valueOf(oldPswView.getText()));
							}
							catch (Exception e)
							{
								Log.e("[LZ]=====================", "密码加密失败. 原文密码：" + String.valueOf(newPswView.getText() + "," + String.valueOf(oldPswView.getText())));
							}
							dataU = new UserEntity();
							dataU.setUser_id(u.getUser_id());
							dataU.setUser_psw(newPsw);
							dataU.setOld_psw(oldPsw);
							new DataAsyncTask().execute(IS_CHANGE_PASSWORD);
							allowCloseDialog(dialog, true);
						}
						else
						{
							//不允许关闭dialog
							allowCloseDialog(dialog, false);
						}
					}
				}) 
				.setNegativeButton($$(R.string.general_cancel), new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						allowCloseDialog(dialog, true);
					}
				})
				.show(); 
			}
		});
		
		super.initListeners();
	}
	
	private void refreshDatas()
	{
		if(u != null)
		{
			nameTextView.setText(u.getNickname());
			emailTextView.setText(u.getUser_mail());
			if(MyApplication.getInstance(this).getUNIT_TYPE().equals("Imperial")){
				
				bodyInfoTextView.setText(MessageFormat.format($$(R.string.user_info_body_info_text), u.getUser_height(), u.getUser_weight(), _Utils.getAgeByBirthdate(u.getBirthdate(), "yyyy-MM-dd"), u.getUser_sex().equals("1") ? $$(R.string.register_form_view_man) : $$(R.string.register_form_view_woman)));

			}else{
				bodyInfoTextView.setText(MessageFormat.format($$(R.string.user_info_body_info_text_m), ToolKits.calcInches2CM(Integer.parseInt(u.getUser_height()))+"",ToolKits.calcLBS2KG(Integer.parseInt(u.getUser_weight()))+"" , _Utils.getAgeByBirthdate(u.getBirthdate(), "yyyy-MM-dd"), u.getUser_sex().equals("1") ? $$(R.string.register_form_view_man) : $$(R.string.register_form_view_woman)));
			}
			
			if(!CommonUtils.isStringEmpty((String)u.getWhat_s_up(),true))
				whatsupTextView.setText((String)u.getWhat_s_up());
			else
				whatsupTextView.setText($$(R.string.user_info_what_s_up_enter_hint));
			
//			// 在网络头像被加载头，先显示默认头像吧，否则ui上那一块就是空的，很难看
//			if(u.isMan())
//				viewAvatar.setImageResource(R.drawable.head_man_online);
//			else
//				viewAvatar.setImageResource(R.drawable.head_woman_online);
		}
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
		if (requestCode == UPDATE_BODY_INFO_FROM_USER )
		{
		    if (resultCode == RESULT_OK)
		    {
		    	u = (UserEntity) data.getSerializableExtra("__UserEntity__");
				refreshDatas();
		    }
		}
		else
			profilePhotoWrapper.onParantActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void finish()
	{
		this.setResult(RESULT_OK);
		super.finish();
	}
	
	/**
	 * 提交数据请求和处理的异步执行线程实现类.
	 */
	protected class DataAsyncTask extends DataLoadingAsyncTask<String, Integer, DataFromServer>
	{
		private String what = "";
		private int sysActionConst = 0; 
		public DataAsyncTask()
		{
			super(UserActivity.this, $$(R.string.general_submitting));
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
			what = params[0];
			if(params[0].equals(IS_CHANGE_PASSWORD))
				sysActionConst = SysActionConst.ACTION_APPEND3;
			else if(params[0].equals(IS_NOT_CHANGE_PASSWORD))
				sysActionConst = SysActionConst.ACTION_APPEND2;
			else if(params[0].equals(IS_CHANGE_WHATSUP))
				sysActionConst = SysActionConst.ACTION_MULTI_ADD;
			
			if(sysActionConst == 0)
			{
				DataFromServer dfs = new DataFromServer();
				dfs.setSuccess(false);
				return dfs;
			}
				
			return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(
					DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_LOGIC)
					.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER)
					.setActionId(sysActionConst)
					.setNewData(new Gson().toJson(dataU)));
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
			int resId;
			boolean res = false;
			JSONObject resObj = JSONObject.parseObject((String) result);
			if (resObj.getString("type").toLowerCase().equals("userentity"))
			{
				u = resObj.getObject("ret", UserEntity.class);
				refreshDatas();
				
				// 将用户信息的修改实时同步到设备中
				UserEntity userEntity = MyApplication.getInstance(context).getLocalUserInfoProvider();
				userEntity.setNickname(u.getNickname());
				try
				{
					provider.regiesterNew(context, DeviceInfoHelper.fromUserEntity(userEntity));
					// provider.setAllDeviceInfo(context,
					// DeviceInfoHelper.fromUserEntity(userEntity));
				}
				catch (JSONException e)
				{
					e.printStackTrace();
				}
				catch (ParseException e)
				{
					e.printStackTrace();
				}
				
				resId = R.string.user_info_update_success;
				res = true;
			}
			else if (resObj.getString("type").toLowerCase().equals("integer"))
			{
				if(resObj.getIntValue("ret") == UserRegisterDTO.UPDATE_PSW_ERROR_OLD_NOT_EQ_LOGIN_PSW)
					resId = R.string.user_info_update_user_psw_old_psw_false;
				else
					resId = R.string.general_error;
				res = false;
			}
			else if (resObj.getString("type").toLowerCase().equals("boolean"))
			{
				if(resObj.getBooleanValue("ret"))
				{
					resId = R.string.user_info_update_success;
					res = true;
					
					// 密码修改成功！
					if(what.equals(IS_CHANGE_PASSWORD))
						// 把新密码更新到登陆信息中，以便下次免登陆时使用
						PreferencesToolkits.updateLoginInfo(UserActivity.this, u.getUser_mail(), dataU.getUser_psw());
				}
				else 
				{
					resId = R.string.user_info_update_failure;
					res = false;
				}
			}
			else
			{
				res = false;
				resId = R.string.general_error;
			}
			ToolKits.showCommonTosat(UserActivity.this, res, $$(resId), Toast.LENGTH_SHORT);
		}
	}
	
	private void allowCloseDialog(DialogInterface dialog, boolean allow)
	{
		try
		{
			Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
			field.setAccessible(true);
			try
			{
				field.set(dialog, allow);
			}
			catch (IllegalArgumentException e)
			{
//				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
//				e.printStackTrace();
			}
		}
		catch (SecurityException e)
		{
//			e.printStackTrace();
		}
		catch (NoSuchFieldException e)
		{
//			e.printStackTrace();
		}
	}
	
	private boolean updatePswByVlidate(TextView viewOldPsw, TextView viewNewPsw, TextView viewReNewPsw)
	{
		String oldPsw = String.valueOf(viewOldPsw.getText());
		String newPsw = String.valueOf(viewNewPsw.getText());
		String reNewPsw = String.valueOf(viewReNewPsw.getText());
		
		// 当前密码是否为空
		if (CommonUtils.isStringEmpty(oldPsw))
		{
			viewOldPsw.setError($$(R.string.general_invild));
			return false;
		}
		
		// 新密码是否为空
		if (CommonUtils.isStringEmpty(newPsw))
		{
			viewNewPsw.setError($$(R.string.general_invild));
			return false;
		}
		
		if(CommonUtils.isStringEmpty(reNewPsw))
		{
			viewReNewPsw.setError($$(R.string.general_invild));
			return false;
		}
		
		// 两次输入新密码是否一致
		if (!newPsw.equals(reNewPsw))
		{
			viewNewPsw.setError($$(R.string.user_info_update_user_psw_new_psw_not_equal));
			viewReNewPsw.setError($$(R.string.user_info_update_user_psw_new_psw_not_equal));
			return false;
		}
		
		// 新密码长度是否大于6
		if (newPsw.length() < 6)
		{
			viewNewPsw.setError($$(R.string.user_info_update_user_psw_length));
			return false;
		}
		
		// 登录密码与新密码是否一致
		if (oldPsw.equals(newPsw))
		{
			viewNewPsw.setError($$(R.string.user_info_update_user_psw_old_equal_new));
			return false;
		}
//		
//		// 当前密码是否与登录密码一致
//		if (!(oldPsw.equals(u.getUser_psw())))
//		{
//			viewOldPsw.setError($$(R.string.user_info_update_user_psw_old_psw_false));
//			return false;
//		}
		
		return true;
	}
	
	private boolean updateBodyByVlidate(TextView viewHieght, TextView viewWieght, TextView viewAge)
	{
		String height = String.valueOf(viewHieght.getText());
		String weight = String.valueOf(viewWieght.getText());
		String age = String.valueOf(viewAge.getText());
		if(CommonUtils.isStringEmpty(height) || !CommonUtils.isNumeric(height))
		{
			viewHieght.setError($$(R.string.user_info_update_user_height_not_valid));
			return false;
		}
		if(CommonUtils.isStringEmpty(weight) || !CommonUtils.isNumeric(weight))
		{
			viewHieght.setError($$(R.string.user_info_update_user_weight_not_valid));
			return false;
		}
		if(CommonUtils.isStringEmpty(age) || !CommonUtils.isNumeric(age))
		{
			viewHieght.setError($$(R.string.user_info_update_user_age_not_valid));
			return false;
		}
		
		return true;
	}

	@Override
	protected void refreshToView(String taskName, Object taskObj,
			Object paramObject)
	{
		if(taskName.equals(REQ_WHATSUP_COUNT))
		{
			JSONObject obj = JSON.parseObject((String)paramObject);
			int  sign_count = obj.getIntValue("sign_count");
			int   comment_count‍ = obj.getIntValue("comment_count");
			Object[]  args = {sign_count};
			String msg = MessageFormat.format($$(R.string.user_info_update_whats_up_count), args);
			whatsupCountTextView.setText(msg);
			whatsupCommentsTextView.setText(comment_count‍ + "");
		}
	}
	//----------------------------------------------------------------------------------- inner class
	
}
