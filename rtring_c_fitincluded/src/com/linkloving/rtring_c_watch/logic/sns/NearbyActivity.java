package com.linkloving.rtring_c_watch.logic.sns;

import java.util.ArrayList;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.eva.android.platf.std.DataLoadableActivity;
import com.eva.android.widget.AProgressDialog;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.eva.epc.core.dto.SysActionConst;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.linkloving.rtring_c_watch.logic.sns.adapter.NearbyAdapter;
import com.linkloving.rtring_c_watch.utils.IntentFactory;
import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.JobDispatchConst;
import com.rtring.buiness.logic.dto.UserSelected;

/**
 * 附近的人
 * @author Administrator
 *
 */
public class NearbyActivity extends DataLoadableActivity
{
	
	private NearbyAdapter nearbyAdapter;
	
	private PullToRefreshListView paginationView = null; 
	private ListView nearbyListView;
	private LinearLayout nullDataLinear;
	
	private int pageIndex = 1;
	
	private double longitude = 0;
	
	private double latitude = 0;
	
	private LocationClient mLocationClient;
	
	public MyLocationListener mMyLocationListener;
	
	private AProgressDialog pd;
    
	
	protected void initViews()
	{
		customeTitleBarResId = R.id.nearby_activity_titleBar;
		// 首先设置cosntentview
		setContentView(R.layout.nearby_activity);
		this.setTitle(R.string.nearby_title);
		
		/**
		 * 禁止进入就请求服务端，等待GPS定位结果后再请求
		 */
		this.setLoadDataOnCreate(false);
		
		paginationView = (PullToRefreshListView) this.findViewById(R.id.nearby_activity_list_view);
		nearbyListView = paginationView.getRefreshableView();
		nearbyListView.setDivider(ToolKits.getRepetDrawable(this,R.drawable.list_view_deliver));
		nearbyListView.setDividerHeight(1);
		paginationView.setMode(Mode.DISABLED);
		
		nullDataLinear = (LinearLayout) findViewById(R.id.nearby_activity_null_data_LL);
		
		nearbyAdapter = new NearbyAdapter(this);
		nearbyListView.setAdapter(nearbyAdapter);
		
		
		paginationView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView)
			{
			      NearbyActivity.this.loadData();
			}
		});
		
		paginationView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3)
			{
				        String userID = nearbyAdapter.getListData().get(index-1 < 0?0:index-1).getUser_id();
				        Intent intent = IntentFactory.createUserDetialActivityIntent(NearbyActivity.this, userID);
				        startActivity(intent);
			}
		});
		
		mLocationClient = new LocationClient(this.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		
		initLocation();
		mLocationClient.start();
		pd = new AProgressDialog(this,$$(R.string.portal_main_data_loading));
		pd.show();

	}
	
	private void initLocation()
	{
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("gcj02");// 返回的定位结果是百度经纬度，默认值gcj02 bd09ll bd09
		option.setScanSpan(1000);// 设置发起定位请求的间隔时间为1000ms
		option.setIsNeedAddress(true); //返回地址
		mLocationClient.setLocOption(option);
	}
	
	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener
	{

		@Override
		public void onReceiveLocation(BDLocation location)
		{
			longitude = location.getLongitude();
			latitude = location.getLatitude();
			
			NearbyActivity.this.loadData(false, "");
			mLocationClient.stop();
		}

	}
	
	@Override
	protected void initListeners()
	{
		
	}

	@Override
	protected DataFromServer queryData(String... arg0)
	{
		JSONObject obj = new JSONObject();
		obj.put("user_id", MyApplication.getInstance(this).getLocalUserInfoProvider().getUser_id());
		obj.put("longitude", longitude);
		obj.put("latitude", latitude);
		obj.put("page", pageIndex);
		
		return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(
				DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_SNS_LOGIC)
				.setJobDispatchId(JobDispatchConst.SNS_BASE)
				.setActionId(SysActionConst.ACTION_QUERY)
				.setNewData(obj.toJSONString()));
	}
	
	@Override
	protected void refreshToView(Object result)
	{
		if(result == null || ((String)result).isEmpty())
		{
			paginationView.onRefreshComplete();
			paginationView.setMode(Mode.DISABLED);
			if(pageIndex < 2)
			{
				nullDataLinear.setVisibility(View.VISIBLE);
				paginationView.setVisibility(View.GONE);
			}
			else
			{
				nullDataLinear.setVisibility(View.GONE);
				paginationView.setVisibility(View.VISIBLE);
			}
				//WidgetUtils.showToast(NearbyActivity.this, $$(R.string.nearby_empty), ToastType.INFO);
			return;
		}
		
		ArrayList<UserSelected> list = (ArrayList<UserSelected>) JSON.parseArray(result.toString(), UserSelected.class);
		
		if(list.size() < 1)
		{
			paginationView.onRefreshComplete();
			paginationView.setMode(Mode.DISABLED);
			
			if(pageIndex < 2)
			{
				nullDataLinear.setVisibility(View.VISIBLE);
				paginationView.setVisibility(View.GONE);
			}
			else
			{
				nullDataLinear.setVisibility(View.GONE);
				paginationView.setVisibility(View.VISIBLE);
			}
//			if(pageIndex < 2)
//				WidgetUtils.showToast(NearbyActivity.this, $$(R.string.nearby_empty), ToastType.INFO);
			return;
		}
		paginationView.setMode(Mode.PULL_FROM_END);
		pageIndex++;
		nearbyAdapter.getListData().addAll(list);
		nearbyAdapter.notifyDataSetChanged();
		paginationView.onRefreshComplete();
		
		if(pd.isShowing())
		     pd.dismiss();
	}
	
}
