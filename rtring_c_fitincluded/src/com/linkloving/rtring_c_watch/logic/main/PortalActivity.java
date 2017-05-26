package com.linkloving.rtring_c_watch.logic.main;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eva.android.platf.std.AutoUpdateDaemon;
import com.eva.android.widget.DataLoadingAsyncTask;
import com.eva.android.x.AppManager;
import com.eva.epc.common.util.CommonUtils;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.eva.epc.core.dto.SysActionConst;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.linkloving.rtring_c_watch.logic.main.slidemenu.BaseFragmentActivity;
import com.linkloving.rtring_c_watch.logic.main.slidemenu.PortalMenuFragment;
import com.rtring.buiness.dto.MyProcessorConst;
import com.rtring.buiness.logic.dto.JobDispatchConst;
import com.rtring.buiness.logic.dto.UserEntity;
import com.slidingmenu.lib.SlidingMenu;

/**
 * 门户入口主界面.
 * 
 * @author Jack Jiang, 2014-05-11
 */
public class PortalActivity extends BaseFragmentActivity
{
		
	private String TAG = PortalActivity.class.getSimpleName();
	
	private SlidingMenu mSlidingMenu;
	
	private PortalMainFragment portalMainFragment;
	private PortalMenuFragment portalMenuFragment;
	public PortalActivity()
	{
		super();
	}
	
	/**************谷歌fit部分START**************/
	private GoogleApiClient mGoogleApiClient = null;
	private long start_time;
	/**************谷歌fit部分OVER**************/
	
	@SuppressLint("InflateParams")
	@Override
	public void onCreate(Bundle arg0)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(arg0);
		AppManager.getAppManager().addActivity(this);
		mSlidingMenu = createSlidingMenu();
//		MyApplication.getInstance(this).setmSlidingMenu(mSlidingMenu);
		portalMenuFragment = new PortalMenuFragment();
		@SuppressWarnings("deprecation")
		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
		View portalMenuView = LayoutInflater.from(this).inflate(R.layout.activity_portal_menu,null);
		View portalMainView = LayoutInflater.from(this).inflate(R.layout.activity_portal_main,null);
		portalMainView.setLayoutParams(param);
		portalMenuView.setLayoutParams(param);
		mSlidingMenu.setMenu(portalMenuView);
		mSlidingMenu.setContent(portalMainView);
		FragmentTransaction t = this.getSupportFragmentManager().beginTransaction();
//		portalMainFragment = new PortalMainFragment(){
//			@Override
//			protected void fireToggle()
//			{
//				mSlidingMenu.toggle();
//			}
//		};
		
		
		portalMainFragment = new PortalMainFragment().setmSlidingMenu(mSlidingMenu);
		 t.replace(R.id.fragment_main, portalMainFragment);
		 t.replace(R.id.fragment_menu, portalMenuFragment);
		 t.commit();
		 
		 if (!CommonUtils.isStringEmpty(MyApplication.getInstance(this).getLocalUserInfoProvider().getLast_sync_device_id())) {
				MyApplication.getInstance(this).getCurrentHandlerProvider().setCurrentDeviceMac(MyApplication.getInstance(this).getLocalUserInfoProvider().getLast_sync_device_id().toUpperCase());
				MyApplication.syncAllDeviceInfoAuto(this,false,null);
		 }
		 
		new DataAsyncTask().execute("");
		// 有网络时才去尝试载入图片
//		if(ToolKits.isNetworkConnected(PortalActivity.this))
//		 new AllDataAsyncTask().execute();
//		 buildFitnessClient();
	}
	

	private void buildFitnessClient() {
		 // Create the Google API Client
		mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks( new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.e("GoogleApiClient", "Connected!!!");
                                // Now you can make calls to the Fitness APIs.  What to do?
                                // Look at some data!!
                                 new InsertAndVerifyDataTask().execute();
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.e("GoogleApiClient", "Connection lost.  Cause: Network Lost.");
                                } else if (i == ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.e("GoogleApiClient", "Connection lost.  Reason: Service Disconnected");
                                    mGoogleApiClient.connect();
                                }
                            }
                        }
                )
                .enableAutoManage(this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.i("GoogleApiClient", "Google Play services connection failed. Cause: " + result.toString());
//                      Toast.makeText(PortalActivity.this, "GoogleApiClient:Exception while connecting to Google Play services: " +result.getErrorMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
	}
	
	
    /**
     *  Create a {@link DataSet} to insert data into the History API, and
     *  then create and execute a {@link DataReadRequest} to verify the insertion succeeded.
     *  By using an {@link AsyncTask}, we can schedule synchronous calls, so that we can query for
     *  data after confirming that our insert was successful. Using asynchronous calls and callbacks
     *  would not guarantee that the insertion had concluded before the read request was made.
     *  An example of an asynchronous call using a callback can be found in the example
     *  on deleting data below.
     */
    private class InsertAndVerifyDataTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {
            //First, create a new dataset and insertion request.
            DataSet dataSet = insertFitnessData();

            // [START insert_dataset]
            // Then, invoke the History API to insert the data and await the result, which is
            // possible here because of the {@link AsyncTask}. Always include a timeout when calling
            // await() to prevent hanging that can occur from the service being shutdown because
            // of low memory or other conditions.
            Log.i("GoogleApiClient", "Inserting the dataset in the History API");
            com.google.android.gms.common.api.Status insertStatus =
                    Fitness.HistoryApi.insertData(mGoogleApiClient, dataSet).await(1, TimeUnit.MINUTES);

            // Before querying the data, check to see if the insertion succeeded.
            if (!insertStatus.isSuccess()) {
                Log.e("GoogleApiClient", "There was a problem inserting the dataset.");
                return null;
            }

            // At this point, the data has been inserted and can be read.
            Log.i("GoogleApiClient", "Data insert was successful!");
            // [END insert_dataset]

            // Begin by creating the query.
//            DataReadRequest readRequest = queryFitnessData();

            // [START read_dataset]
            // Invoke the History API to fetch the data with the query and await the result of
            // the read request.
//            DataReadResult dataReadResult =
//                    Fitness.HistoryApi.readData(mGoogleApiClient, readRequest).await(1, TimeUnit.MINUTES);
            // [END read_dataset]

            // For the sake of the sample, we'll print the data so we can see what we just added.
            // In general, logging fitness information should be avoided for privacy reasons.
//            printData(dataReadResult);

            return null;
        }
    }
    
    /**
     * Create and return a {@link DataSet} of step count data for the History API.
     */
    private DataSet insertFitnessData() {
        Log.i(TAG, "Creating a new data insert request");

        // [START build_insert_data_request]
        // Set a start and end time for our data, using a start time of 1 hour before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        
        long endTime = cal.getTimeInMillis();   //数据结束时间
        cal.add(Calendar.HOUR_OF_DAY, -1);    //当前时间天-1
        if( start_time == 0){
        	start_time = cal.getTimeInMillis(); //数据开始时间
        }
        // Create a data source
        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(this)
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setName(TAG + " - step count")  //BasicHistoryApi
                .setType(DataSource.TYPE_RAW)
                .build();

        // Create a data set
        int stepCountDelta = 1000;
        DataSet dataSet = DataSet.create(dataSource);
        // For each data point, specify a start time, end time, and the data value -- in this case,
        // the number of new steps.
        DataPoint dataPoint = dataSet.createDataPoint().setTimeInterval(start_time, endTime , TimeUnit.MILLISECONDS);
        start_time = endTime;
        dataPoint.getValue(Field.FIELD_STEPS).setInt(stepCountDelta);
        dataSet.add(dataPoint);
        // [END build_insert_data_request]

        return dataSet;
    }
	
	


	@Override
	protected void onStart() {
		super.onStart();
		
//		mGoogleApiClient.connect();
	}
	
	


	@Override
	protected void onStop() {
		super.onStop();
//		if (!mGoogleApiClient.isConnecting()) 
//			mGoogleApiClient.disconnect();
	}
	
	


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		AppManager.getAppManager().removeActivity();
	}


	private SlidingMenu createSlidingMenu() {
		SlidingMenu slidingMenu = new SlidingMenu(PortalActivity.this);
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		slidingMenu.setShadowWidthRes(R.dimen.shadow_width);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
		// slidingMenu.setFadeDegree(0.35f);
		slidingMenu.setBehindScrollScale(0);
		// slidingMenu.setBehindCanvasTransformer(new
		// CustomZoomAnimation().getmTransformer());
		slidingMenu.attachToActivity(PortalActivity.this,SlidingMenu.SLIDING_CONTENT);
		return slidingMenu;
	}
	
	@Override
	protected void onNewIntent(Intent intent)  
	{
		/**
		 * onNewIntent单例模式下，startActivity后不会进入onCreate，而是进入onNewIntent
		 */
		
		if(intent!=null && intent.getStringExtra("OwnBraceletActivity")!=null){
			Log.d(TAG, intent.getStringExtra("OwnBraceletActivity"));
		}
		
		portalMainFragment.refresuUIAll();
		portalMenuFragment.refreshUI();
	}


	@Override
	protected int getLayoutResID() {
		return R.layout.activity_portal_main;
	}


	@Override
	protected void initView() 
	{
		
	}


	@Override
	protected void setAdapter()
	{
		
	}


	@Override
	protected void bindListener()
	{
		
	}
	
	@Override
	public void onBackPressed() 
	{
		mSlidingMenu.toggle();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
	
		BLEProvider provider = MyApplication.getInstance(PortalActivity.this).getCurrentHandlerProvider();
		provider.onActivityResultProess(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);
		
		// User chose not to enable Bluetooth.
		if (requestCode == MyApplication.REQUEST_ENABLE_BT) {
			switch (resultCode) {
			case Activity.RESULT_CANCELED: //用户取消打开蓝牙
				break;
			case Activity.RESULT_OK:       //用户打开蓝牙
				Log.e(TAG, "//用户打开蓝牙");
				provider.scanForConnnecteAndDiscovery();
				
				break;

			default:
				break;
			}
			return;
		}
	}
	/**
	 * 提交数据请求和处理的异步执行线程实现类.
	 */
	protected class DataAsyncTask extends DataLoadingAsyncTask<String, Integer, DataFromServer>
	{
		public DataAsyncTask()
		{
			super(PortalActivity.this, false);
		}
		
		/**
		 * 在后台执行 {@link doLogin()}实现登陆信息的提交和处于结果的读取 .
		 * 
		 * @param parems
		 *            外界传进来的参数
		 * @return 查询结果，将传递给onPostExecute(..)方法
		 */
		@SuppressLint("SimpleDateFormat")
		@Override
		protected DataFromServer doInBackground(String... params)
		{
			JSONObject dataObj = new JSONObject();
			UserEntity u = MyApplication.getInstance(context).getLocalUserInfoProvider();
			dataObj.put("user_id", u.getUser_id());
			dataObj.put("user_time", new SimpleDateFormat(com.linkloving.rtring_c_watch.utils.ToolKits.DATE_FORMAT_YYYY_MM_DD).format(new Date()));
			
			System.out.println(TAG+"--->"+dataObj.toJSONString());
			return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(
					DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_SNS_LOGIC)
					.setJobDispatchId(JobDispatchConst.SNS_BASE)
					.setActionId(SysActionConst.ACTION_VERIFY)
					.setNewData(dataObj.toJSONString()));
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
				MyApplication.getInstance(context).setCommentNum(Integer.parseInt((String)result));
				portalMenuFragment.updateUnReadCount();
				portalMainFragment.updateUnReadCount();
				
			}
			
		}
	}
	
	
	/**
	 * 刷新所有用户信息.
	 */
	protected class AllDataAsyncTask extends DataLoadingAsyncTask<String, Integer, DataFromServer>
	{
		public AllDataAsyncTask()
		{
			super(PortalActivity.this, null);
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
			JSONObject dataObj = new JSONObject();
			UserEntity u = MyApplication.getInstance(context).getLocalUserInfoProvider();
			dataObj.put("user_id",   u.getUser_id());
//			dataObj.put("user_time", new SimpleDateFormat(com.linkloving.rtring_c_watch.utils.ToolKits.DATE_FORMAT_YYYY_MM_DD).format(new Date()));
			
			System.out.println(TAG+"--->"+dataObj.toJSONString());
			return HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(
					DataFromClient.n().setProcessorId(MyProcessorConst.PROCESSOR_LOGIC)
					.setJobDispatchId(JobDispatchConst.LOGIC_REGISTER)
					.setActionId(SysActionConst.ACTION_VERIFY)
					.setNewData(dataObj.toJSONString()));
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
				Log.i(TAG, result.toString());
				
				UserEntity user_local = MyApplication.getInstance(context).getLocalUserInfoProvider();
				Log.i(TAG, "AllDataAsyncTask:"+user_local.getDevice_type());
				UserEntity user = JSON.parseObject((String)result, UserEntity.class);
				if(user.getDevice_type().equals(""))
					user.setDevice_type(user_local.getDevice_type());
				MyApplication.getInstance(context).setLocalUserInfoProvider(user);
				
			}
			
		}
	}
}

//class MyPortalMainFragment extends PortalMainFragment
//{
//	private SlidingMenu mSlidingMenu = null;
//	
//	public static MyPortalMainFragment newInstance() {
//		MyPortalMainFragment fragment = new MyPortalMainFragment();
//	    return fragment;
//	}
//	
//	public MyPortalMainFragment()
//	{
//		super();
//	}
//	
//	public SlidingMenu getmSlidingMenu()
//	{
//		return mSlidingMenu;
//	}
//	public MyPortalMainFragment setmSlidingMenu(SlidingMenu mSlidingMenu)
//	{
//		this.mSlidingMenu = mSlidingMenu;
//		return this;
//	}
//
//	@Override
//	protected void fireToggle()
//	{
//		if(mSlidingMenu != null)
//			mSlidingMenu.toggle();
//	}
//}
