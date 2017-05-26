package com.linkloving.rtring_c_watch.logic.launch;

import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.eva.android.platf.std.DataLoadableActivity;
import com.eva.android.widget.DataLoadingAsyncTask;
import com.eva.epc.common.util.CommonUtils;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.eva.epc.core.dto.SysActionConst;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.JobDispatchConst;

/**
 * “忘记密码”处理Activity.
 * 
 * @author tony.ma
 */
public class ForgetPassWordActivity extends DataLoadableActivity 
{
	private EditText emailEditText = null;
	private Button nextButton = null;
	
	@Override
	protected void initViews()
	{
		customeTitleBarResId = R.id.forget_password_title_bar;
		setContentView(R.layout.forget_password);
		
		emailEditText = (EditText) findViewById(R.id.forget_password_edit_text);
		nextButton = (Button) findViewById(R.id.forget_password_next_btn);
		
		setTitle(R.string.forgot_password_title);
		super.initViews();
	}
	
	/**
	 * 为各UI功能组件增加事件临听的实现方法.
	 * {@inheritDoc}
	 */
	@Override
	protected void initListeners()
	{
		nextButton.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
				if(String.valueOf(emailEditText.getText()).trim().length()<=0)
				{
					emailEditText.setError($$(R.string.general_invild));
					return;
				}
				if(!CommonUtils.isEmail(String.valueOf(emailEditText.getText()).trim()))
				{
					emailEditText.setError($$(R.string.general_invild));
					return;
				}
				
//				WidgetUtils.showToast(ForgetPassWordActivity.this
//						, "此功能暂未实现！！！！！！！！！", ToastType.INFO);
				
				// TODO 实现此功能！！！！！！！！！！！！！！！！！！！！！！！！
				// 发送重置密码确认邮件
				new SendInviteMail().execute();
			}
		});
	}

	//--------------------------------------------------------------------------------------------
	/**
	 * 从服务端查询数据并返回.
	 * 
	 * @param params loadData中传进来的参数，本类中该参数没有被用到
	 */
	@Override protected DataFromServer queryData(String... params)
	{
		return null;
	}
	//将已构造完成的完整的明细数据放入列表中显示出来
	@Override protected void refreshToView(Object dateToView)
	{
	}

	//--------------------------------------------------------------------------------------------
	/**
	 * <p>
	 * 使用异步线程实现，提升用户体验.
	 * </p>
	 */
	private class SendInviteMail extends DataLoadingAsyncTask<String, Integer, DataFromServer>
	{
		public SendInviteMail()
		{
			super(ForgetPassWordActivity.this, $$(R.string.general_send));
		}

		/**
		 * 在后台执行查询指定产品编号的大仓库库存分布情况（指定产品在哪个大仓库里有多少数量）.
		 * 
		 * @param parems 外界传进来的参数
		 * @return 查询结果，将传递给onPostExecute(..)方法
		 */
		@Override
		protected DataFromServer doInBackground(String... parems) 
		{
			// 提交请求到服务端
			DataFromClient dataFromClient = DataFromClient.n()
					.setProcessorId(MyProcessorConst.PROCESSOR_LOGIC)		
					.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER)
					.setActionId(SysActionConst.ACTION_APPEND4)
					// 要接收邀请的邮件地址
					.setNewData(// 接收邀请的email地址
							String.valueOf(emailEditText.getText()).trim());
			// 服务端发送邮件可能会有点慢，客户端就不用等了，否则体验就会有点差
			dataFromClient.setDoInput(false);
			DataFromServer dfs= HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(dataFromClient);
			return dfs;
		}

		@Override
		protected void onPostExecuteImpl(Object result)
		{
			//* 到了本方法里即意味着与服务端的交互成功了
			// 成功后要做的事：显示库存分布情况
			new com.eva.android.widgetx.AlertDialog.Builder(ForgetPassWordActivity.this)
			.setTitle(R.string.general_tip)  
			.setMessage("  The Email has been sent,please check!")
			.setNegativeButton(R.string.general_ok, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					finish();
				}
			})  
			.show();  
		}
	}
}
