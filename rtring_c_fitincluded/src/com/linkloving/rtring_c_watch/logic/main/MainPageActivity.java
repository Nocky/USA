package com.linkloving.rtring_c_watch.logic.main;

import com.eva.android.platf.std.DataLoadableActivity;
import com.eva.epc.core.dto.DataFromServer;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.logic.main.impl.TodayCircleView;
import com.linkloving.rtring_c_watch.logic.report.ReportCircleView;

/**
 * 主功能界面。本界面是PortalActivity的一级子页面。
 * 
 * @author Jack Jiang, 2014-05-11
 */
public class MainPageActivity extends DataLoadableActivity 
{
	@Override
	protected void initViews()
	{
//		customeTitleBarResId = R.id.forget_password_title_bar;
		setContentView(R.layout.main_page_activity);

//		emailEditText = (EditText) findViewById(R.id.forget_password_edit_text);
//		nextButton = (Button) findViewById(R.id.forget_password_next_btn);

		setTitle("主页");
		
		this.setLoadDataOnCreate(false);
		
		TodayCircleView tcv = (TodayCircleView)findViewById(R.id.main_page_activity_preview_todayCircleView);
		tcv.setPercent(0.65f);
		ReportCircleView rcv = (ReportCircleView)findViewById(R.id.main_page_activity_preview_reportCircleView);
//		rcv.setPercent(0.55f);
	}

	/**
	 * 为各UI功能组件增加事件临听的实现方法.
	 * {@inheritDoc}
	 */
	@Override
	protected void initListeners()
	{

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

}
