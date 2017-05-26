package com.linkloving.rtring_c_watch.logic.setup;

import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eva.android.platf.std.AutoUpdateDaemon;
import com.eva.android.platf.std.DataLoadableActivity;
import com.eva.android.widget.DataLoadingAsyncTask;
import com.eva.epc.common.util.CommonUtils;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.eva.epc.core.dto.SysActionConst;
import com.google.gson.Gson;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.linkloving.rtring_c_watch.utils.DeviceInfoHelper;
import com.linkloving.rtring_c_watch.utils.IntentFactory;
import com.linkloving.rtring_c_watch.utils.SkinSettingManager;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.linkloving.utils._Utils;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.JobDispatchConst;
import com.rtring.buiness.logic.dto.UserEntity;
import com.rtring.buiness.logic.dto.UserRegisterDTO;

public class BodyActivity extends DataLoadableActivity
{
	private SkinSettingManager mSettingManager;
	
	public final static int REGISTER_ACTIVITY = 1;
	public final static int USER_ACTIVITY = 2;
	public final static int CHILD_ACTIVITY = 3;
	public final static int LOGIN_ACTIVITY = 4;
	
	private UserEntity user = null;
	private int from = 0;
	private String nickName;

	private RadioButton cbMan = null;
	private RadioButton cbWomen = null;

	private View reduceHeight = null;
	private View plusHeight = null;
	private EditText editHeight = null;

	private View reduceWeight = null;
	private View plusWeight = null;
	private EditText editWeight = null;

	private View reduceAge = null;
	private View plusAge = null;
	private EditText editAge = null;

	private TextView bmiView = null;
	private TextView bmiDescView = null;
	
	private TextView unit_height = null;
	private TextView unit_weight = null;
	
	private Button saveBtn = null;

	@Override
	protected void initDataFromIntent()
	{
		ArrayList data = IntentFactory.parseBodyActivityIntent(getIntent());
		user = (UserEntity)data.get(0);
		from = (Integer)data.get(1);
		nickName = (String) data.get(2);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mSettingManager=new SkinSettingManager(this);
		mSettingManager.initSkins();
	}

	@Override
	protected void initViews()
	{
		customeTitleBarResId = R.id.body_info_titleBar;
		// 首先设置contentview
		setContentView(R.layout.body_info_activity);


		reduceHeight = findViewById(R.id.body_info_reduceHieght);
		plusHeight = findViewById(R.id.body_info_plusHeight);
		editHeight = (EditText) findViewById(R.id.body_info_heightView);

		reduceWeight = findViewById(R.id.body_info_reduceWieght);
		plusWeight = findViewById(R.id.body_info_plusWeight);
		editWeight = (EditText) findViewById(R.id.body_info_weightView);

		reduceAge = findViewById(R.id.body_info_reduceAge);
		plusAge = findViewById(R.id.body_info_plusAge);
		editAge = (EditText) findViewById(R.id.body_info_ageView);

		cbMan = (RadioButton) findViewById(R.id.body_info_manCb);
		cbWomen = (RadioButton) findViewById(R.id.body_info_womanCb);

		bmiView = (TextView) findViewById(R.id.body_info_bmi);
		bmiDescView = (TextView) findViewById(R.id.body_info_bmi_desc);
		
		unit_height = (TextView) findViewById(R.id.unit_height);
		unit_weight = (TextView) findViewById(R.id.unit_weight);
		
		if(MyApplication.getInstance(this).getUNIT_TYPE().equals("Imperial")){
			
			unit_height.setText(getString(R.string.unit_cm));
			unit_weight.setText(getString(R.string.unit_kg));
			
		}else{
			unit_height.setText(getString(R.string.unit_cm_m));
			unit_weight.setText(getString(R.string.unit_kg_m));
		}
		
		

		saveBtn = (Button) findViewById(R.id.body_info_save_btn);

		
		String title = "";
		switch (from)
		{
		case REGISTER_ACTIVITY:
			title = $$(R.string.body_info_register);
			editHeight.setText("67");
			editWeight.setText("132");
			editAge.setText("35");
			cbMan.setChecked(true);
			refreshBMI();
			break;
		case USER_ACTIVITY:
			title = $$(R.string.body_info_modify);
			if(MyApplication.getInstance(this).getUNIT_TYPE().equals("Imperial")){
				
				editHeight.setText(user.getUser_height());
				editWeight.setText(user.getUser_weight());
				
			}else{
				
				editHeight.setText(ToolKits.calcInches2CM(Integer.parseInt(user.getUser_height()))+"");
				editWeight.setText(ToolKits.calcLBS2KG(Integer.parseInt(user.getUser_weight()))+"");
			}
			editAge.setText(_Utils.getAgeByBirthdate(user.getBirthdate(), "yyyy-MM-dd"));
			cbMan.setChecked(user.getUser_sex().equals("1"));
			cbWomen.setChecked(user.getUser_sex().equals("0"));
			refreshBMI();
			break;
		case CHILD_ACTIVITY:
			editHeight.setText("67");
			editWeight.setText("132");
			editAge.setText("35");
			cbMan.setChecked(true);
			title = $$(R.string.user_info_child_add_title);
			break;
		case LOGIN_ACTIVITY:
			editHeight.setText("67");
			editWeight.setText("132");
			editAge.setText("35");
			cbMan.setChecked(true);
			title = $$(R.string.user_info_child_add_title);
			break;

		default:
			break;
		}
		this.setTitle(title);
	}

	@Override
	protected void initListeners()
	{
		reduceHeight.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int height = CommonUtils.getIntValue(editHeight.getText().toString());
				editHeight.setText(height <= 0 ? "0" : (height - 1) + "");
				refreshBMI();
				checkBodyInfo();
			}
		});

		plusHeight.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int height = CommonUtils.getIntValue(editHeight.getText().toString());
				editHeight.setText((height + 1) + "");
				refreshBMI();
				checkBodyInfo();
			}
		});

		editHeight.addTextChangedListener(new TextWatcher()
		{

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s)
			{
				refreshBMI();
				checkBodyInfo();
			}
		});

		reduceWeight.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int weight = CommonUtils.getIntValue(editWeight.getText().toString());
				editWeight.setText(weight <= 0 ? "0" : (weight - 1) + "");
				refreshBMI();
				checkBodyInfo();
			}
		});

		plusWeight.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int weight = CommonUtils.getIntValue(editWeight.getText().toString());
				editWeight.setText((weight + 1) + "");
				refreshBMI();
				checkBodyInfo();
			}
		});

		editWeight.addTextChangedListener(new TextWatcher()
		{

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s)
			{
				refreshBMI();
				checkBodyInfo();
			}
		});

		reduceAge.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int age = CommonUtils.getIntValue(editAge.getText().toString());
				editAge.setText(age <= 0 ? "0" : (age - 1) + "");
				checkBodyInfo();
			}
		});

		plusAge.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int age = CommonUtils.getIntValue(editAge.getText().toString());
				editAge.setText((age + 1) + "");
				refreshBMI();
				checkBodyInfo();
			}
		});

		editAge.addTextChangedListener(new TextWatcher()
		{

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s)
			{
				refreshBMI();
				checkBodyInfo();
			}
		});

		saveBtn.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (checkBodyInfo())
				{
					switch (from)
					{
					case LOGIN_ACTIVITY:
					{
						Intent data = new Intent();
						data.putExtra("user_sex", cbMan.isChecked() ? "1" : "0");
						data.putExtra("user_height", editHeight.getText().toString());
						data.putExtra("user_weight", editWeight.getText().toString());
						data.putExtra("user_age", editAge.getText().toString());

						setResult(Activity.RESULT_OK, data);
						finish();
						break;
					}
					case REGISTER_ACTIVITY:
					{
						Intent data = new Intent();
						data.putExtra("user_sex", cbMan.isChecked() ? "1" : "0");
						data.putExtra("user_height", editHeight.getText().toString());
						data.putExtra("user_weight", editWeight.getText().toString());
						data.putExtra("user_age", editAge.getText().toString());
						setResult(Activity.RESULT_OK, data);
						finish();
						break;
					}
					case USER_ACTIVITY:
						new DataAsyncTask().execute();
						break;
					case CHILD_ACTIVITY:
//						new AddChildAsyncTask().execute();
						break;
					default:
						break;
					}
				}
			}
		});
	}

	private void refreshBMI()
	{
		try
		{
			bmiView.setText(ToolKits.getBMI(CommonUtils.getFloatValue(editWeight.getText().toString()), CommonUtils.getIntValue(editHeight.getText().toString())) + "");
			bmiDescView.setText(ToolKits.getBMIDesc(BodyActivity.this,CommonUtils.getDoubleValue(bmiView.getText().toString())));
		}
		catch (Exception e)
		{
			bmiView.setText($$(R.string.body_info_unknow));
			bmiDescView.setText($$(R.string.body_info_unknow));
			Log.e(BodyActivity.class.getSimpleName(), e.getMessage(), e);
		}
	}

	private boolean checkBodyInfo()
	{
		/**
		 * 验证身高
		 */
		if (CommonUtils.isStringEmpty(String.valueOf(editHeight.getText()), true))
		{
			editHeight.setError($$(R.string.body_info_height_is_necessary));
			return false;
		}
		else
		{
			int height = CommonUtils.getIntValue(editHeight.getText().toString());
			
			if(MyApplication.getInstance(BodyActivity.this).getUNIT_TYPE().equals("Imperial")){
				if (height < 24 || height > 107)
				{
					editHeight.setError($$(R.string.body_info_height_recommend));
					return false;
				}else{
					editHeight.setError(null);
				}
			}else{
				if (height < 60 || height > 272)
				{
					editHeight.setError($$(R.string.body_info_height_recommend_m));
					return false;
				}else{
					editHeight.setError(null);
				}
			}
			
		}

		/**
		 * 验证体重
		 */
		if (CommonUtils.isStringEmpty(String.valueOf(editWeight.getText()), true))
		{
			editWeight.setError($$(R.string.body_info_weight_is_necessary));
			return false;
		}
		else
		{
			int weight = CommonUtils.getIntValue(editWeight.getText().toString());
			if(MyApplication.getInstance(BodyActivity.this).getUNIT_TYPE().equals("Imperial")){
				if (weight < 44 || weight > 998)
				{
					editWeight.setError($$(R.string.body_info_weight_recommend));
					return false;
				}
				else{
					editWeight.setError(null);
				}
			}else{
				if (weight < 20 || weight > 453)
				{
					editWeight.setError($$(R.string.body_info_weight_recommend_m));
					return false;
				}
				else{
					editWeight.setError(null);
				}
			}
			
		}

		/**
		 * 验证年龄
		 */
		if (CommonUtils.isStringEmpty(String.valueOf(editAge.getText()), true))
		{
			editAge.setError($$(R.string.body_info_age_is_necessary));
			return false;
		}
		else
		{
			int age = CommonUtils.getIntValue(editAge.getText().toString());
			if (age < 12 || age > 114)
			{
				editAge.setError($$(R.string.body_info_age_recommend));
				return false;
			}
			else{
				editAge.setError(null);
			}
		}
		

		return true;
	}

	/**
	 * 提交数据请求和处理的异步执行线程实现类.
	 */
	protected class DataAsyncTask extends DataLoadingAsyncTask<String, Integer, DataFromServer>
	{
		public DataAsyncTask()
		{
			super(BodyActivity.this, $$(R.string.general_submitting));
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
			UserEntity dataU = new UserEntity();
			dataU.setUser_id(user.getUser_id());
			dataU.setUser_height(editHeight.getText().toString());
			dataU.setUser_weight(editWeight.getText().toString());
			dataU.setBirthdate(_Utils.getBirthdateByAge(Integer.parseInt(editAge.getText().toString())));
			dataU.setUser_sex(cbMan.isChecked() ? "1" : "0");

			return HttpServiceFactory4AJASONImpl
					.getInstance()
					.getDefaultService()
					.sendObjToServer(
							DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_LOGIC).setJobDispatchId(JobDispatchConst.LOGIC_REGISTER)
									.setActionId(SysActionConst.ACTION_APPEND5).setNewData(new Gson().toJson(dataU)));
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
			int resultCode = 0;
			Intent data = null;
			JSONObject resObj = JSONObject.parseObject((String) result);
			if (resObj.getString("type").toLowerCase().equals("userentity"))
			{
				UserEntity u = resObj.getObject("ret", UserEntity.class);
				// 将用户信息的修改实时同步到设备中
				UserEntity userEntity = MyApplication.getInstance(context).getLocalUserInfoProvider();
				userEntity.setUser_sex(u.getUser_sex());
				userEntity.setUser_height(u.getUser_height());
				userEntity.setUser_weight(u.getUser_weight());
				userEntity.setBirthdate(u.getBirthdate());
				
				// 将用户信息的修改实时同步到设备中
				try
				{
					MyApplication.getInstance(context).getCurrentHandlerProvider().regiesterNew(context, DeviceInfoHelper.fromUserEntity(userEntity));
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
				
				data = new Intent();
				data.putExtra("__UserEntity__", u);
				resultCode = Activity.RESULT_OK;
				resId = R.string.user_info_update_success;
				res = true;
			}
			else if (resObj.getString("type").toLowerCase().equals("integer"))
			{
				if (resObj.getIntValue("ret") == UserRegisterDTO.UPDATE_PSW_ERROR_OLD_NOT_EQ_LOGIN_PSW)
					resId = R.string.user_info_update_user_psw_old_psw_false;
				else
					resId = R.string.general_error;
				res = false;
			}
			else if (resObj.getString("type").toLowerCase().equals("boolean"))
			{
				if (resObj.getBooleanValue("ret"))
				{
					resId = R.string.user_info_update_success;
					res = true;
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

			BodyActivity.this.setResult(resultCode, data);
			ToolKits.showCommonTosat(BodyActivity.this, res, $$(resId), Toast.LENGTH_SHORT);
			BodyActivity.this.finish();
		}
	}
	
	

	@Override 
	protected DataFromServer queryData(String... arg0)
	{
		return null;
	}

	@Override
	protected void refreshToView(Object result)
	{
	}
}
