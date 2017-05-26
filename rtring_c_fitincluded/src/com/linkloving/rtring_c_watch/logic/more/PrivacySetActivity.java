package com.linkloving.rtring_c_watch.logic.more;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.eva.android.widget.DataLoadingAsyncTask;
import com.eva.android.widget.util.WidgetUtils;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.eva.epc.core.dto.SysActionConst;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.PreferencesToolkits;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.linkloving.rtring_c_watch.logic.DataLoadableMultipleAcitvity;
import com.linkloving.rtring_c_watch.logic.sns.SearchActivity;
import com.linkloving.rtring_c_watch.utils.IntentFactory;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.JobDispatchConst;
import com.rtring.buiness.logic.dto.UserEntity;

public class PrivacySetActivity extends DataLoadableMultipleAcitvity{

	private Button unit;
	private TextView unit_state;
	private Button privacy;
	private TextView privacy_state;
	private Button 	googlefit_btn;
	private TextView settings_googlefit;
	
	public static final int OPEN_PRIVACY = -1;
	public static final int CLOSE_PRIVACY = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//设定自定义标题栏（设定此值即意味着开启自定义标题栏的使用，必须要在setContentView前设定）
		customeTitleBarResId = R.id.privacy_titleBar;
		setContentView(R.layout.activity_privacy);
		this.setTitle(R.string.main_more_privacy_title);
		initView();
		initListeners_();
	}
	private void initView() {
		unit = (Button) findViewById(R.id.unit_conditions);
		unit_state =  (TextView) findViewById(R.id.settings_unitstate);
		privacy = (Button) findViewById(R.id.unit_privacy_policy);
		privacy_state =  (TextView) findViewById(R.id.settings_privacystate);
		googlefit_btn = (Button) findViewById(R.id.googlefit_btn);
		settings_googlefit =  (TextView) findViewById(R.id.settings_googlefit);
	}

	private void initListeners_() {
		
		privacy.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				final UserEntity user = MyApplication.getInstance(PrivacySetActivity.this).getLocalUserInfoProvider();
				if(privacy_state.getText().equals("ON")){
					privacy_state.setText("OFF");
					user.setUser_status(CLOSE_PRIVACY+"");
					
				}
				else if(privacy_state.getText().equals("OFF"))
				{
					privacy_state.setText("ON");
					user.setUser_status(OPEN_PRIVACY+"");
					
				}
				MyApplication.getInstance(PrivacySetActivity.this).setLocalUserInfoProvider(user);
				
				if(ToolKits.isNetworkConnected(PrivacySetActivity.this))
				{
					new DataLoadingAsyncTask<String, Integer, DataFromServer>(PrivacySetActivity.this, getString(R.string.pay_loading)){
						@Override
									protected DataFromServer doInBackground(String... params) {
										JSONObject obj = new JSONObject();
										obj.put("user_id", user.getUser_id());
										obj.put("user_status", user.getUser_status());
										return HttpServiceFactory4AJASONImpl.getInstance()
												.getDefaultService()
												.sendObjToServer(DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_SNS_LOGIC)
																.setJobDispatchId(JobDispatchConst.SNS_BASE)
																.setActionId(SysActionConst.ACTION_MULTI_DEL)
																.setNewData(obj.toJSONString()));
									}
									
									@Override
									protected void onPostExecuteImpl(Object result) {
										Log.e("DataLoadingAsyncTask", "result:"+result);
										if(Boolean.parseBoolean((String) result)){
											if(user.getUser_status().equals(CLOSE_PRIVACY+""))
												new com.eva.android.widgetx.AlertDialog.Builder(PrivacySetActivity.this)
												.setTitle(R.string.main_about_privacy_conditions)  
												.setMessage(R.string.main_more_privacy_messiage_off)
												.setPositiveButton(R.string.general_ok,  null)  
												.show();
											else if(user.getUser_status().equals(OPEN_PRIVACY+"")){
												new com.eva.android.widgetx.AlertDialog.Builder(PrivacySetActivity.this)
												.setTitle(R.string.main_about_privacy_conditions)  
												.setMessage(R.string.main_more_privacy_messiage_on)
												.setPositiveButton(R.string.general_ok,  null)  
												.show();
											}
										}
									}
					
				
					}.execute();
				}else{  //general_network_faild
					new com.eva.android.widgetx.AlertDialog.Builder(PrivacySetActivity.this)
					.setTitle(R.string.main_about_privacy_conditions)  
					.setMessage(R.string.general_network_faild)
					.setPositiveButton(R.string.general_ok,  null)  
					.show();
				}
			}
		});
		
		
		unit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				new com.eva.android.widgetx.AlertDialog.Builder(PrivacySetActivity.this)
				.setTitle(R.string.unit_setting)  
				.setMessage(R.string.main_more_privacy_switch_unit)
				.setPositiveButton(R.string.general_ok,  null)  
				.show();
				
				if(unit_state.getText().toString().equals("Imperial"))
				{ //英制
					unit_state.setText("Metric");
					
				}
				else if(unit_state.getText().toString().equals("Metric"))
				{
					unit_state.setText("Imperial");
				}
				PreferencesToolkits.save_unit(PrivacySetActivity.this, unit_state.getText().toString());
				
			}
		});
		
		googlefit_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
//				new com.eva.android.widgetx.AlertDialog.Builder(PrivacySetActivity.this)
//				.setTitle(R.string.general_tip)  
//				.setMessage(R.string.main_more_privacy_switch_google)
//				.setPositiveButton(R.string.general_ok,  null)  
//				.show();
				boolean can_up = false;
				if(settings_googlefit.getText().toString().equals("ON"))
				{ 
					Toast.makeText(PrivacySetActivity.this, R.string.main_more_privacy_switch_google_off, Toast.LENGTH_SHORT).show();
					settings_googlefit.setText("OFF");
					can_up = false;
					
				}
				else if(settings_googlefit.getText().toString().equals("OFF"))
				{
					Toast.makeText(PrivacySetActivity.this, R.string.main_more_privacy_switch_google_on, Toast.LENGTH_LONG).show();
					settings_googlefit.setText("ON");
					can_up = true;
					startActivity(IntentFactory.createPortalActivityIntent(PrivacySetActivity.this));
				}
				PreferencesToolkits.save_googlefit(PrivacySetActivity.this, can_up);
				
			}
		});
		
	}
	@Override
	protected void onResume() {
		super.onResume();
		
		this.refreshView();
	}
	
	private void refreshView(){
		
		UserEntity entity = MyApplication.getInstance(PrivacySetActivity.this).getLocalUserInfoProvider();
		
		switch (Integer.parseInt(entity.getUser_status())) {
		case OPEN_PRIVACY:
			privacy_state.setText("ON");
			break;
		case CLOSE_PRIVACY:
			privacy_state.setText("OFF");
			break;

		default:
			break;
		}
		
		String unit_type = PreferencesToolkits.get_unit(PrivacySetActivity.this);
		//Metric 公制  /Imperial  英制
		if(unit_type.equals("Imperial")){ //英制
			unit_state.setText("Imperial");
		}else if(unit_type.equals("Metric")){
			unit_state.setText("Metric");
		}
		
		boolean google = PreferencesToolkits.get_googlefit(PrivacySetActivity.this);
		
		if(google){ //英制
			settings_googlefit.setText("ON");
		}else{
			settings_googlefit.setText("OFF");
		}
	}
	
	@Override
	protected void refreshToView(String taskName, Object taskObj,Object paramObject) {
		
	}
}
