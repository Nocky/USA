package com.linkloving.rtring_c_watch.logic;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.eva.android.RHolder;
import com.eva.android.platf.core.AHttpServiceFactory;
import com.eva.android.widget.ActivityRoot;
import com.eva.android.widget.DataLoadingAsyncTask;
import com.eva.android.x.AsyncTaskManger;
import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.linkloving.rtring_c_watch.http.HttpServiceFactory4AJASONImpl;
import com.linkloving.rtring_c_watch.utils.SkinSettingManager;

public abstract class DataLoadableMultipleAcitvity extends ActivityRoot
{
	  
	  
	  private AsyncTaskManger asyncTaskManger = new AsyncTaskManger();
	  
	  public void onCreate(Bundle savedInstanceState)
	  {
	    super.onCreate(savedInstanceState);

	    init();

	  }
	  
	  private SkinSettingManager mSettingManager;



	@Override
	protected void onResume() {
		super.onResume();
		mSettingManager=new SkinSettingManager(this);
		mSettingManager.initSkins();
	}

	public void loadData(String taskName,DataFromClient params)
	  {
	    loadData(true, params,taskName,null);
	  }
     
	  public void loadData(boolean showProgress,DataFromClient params,String taskName)
	  {
	      loadData(showProgress, params,taskName,null);
	  }
	  
	  public void loadData(boolean showProgress,DataFromClient params,String taskName,Object taskObj)
	  {
		  QueryDataWorder queryDataWorder = new QueryDataWorder(showProgress,taskName,taskObj);
		  asyncTaskManger.addAsyncTask(queryDataWorder);
		  queryDataWorder.execute(params);
	  }

	  protected void init()
	  {
	    initDataFromIntent();
	    initViews();
	    initListeners();
	  }

	  protected void initDataFromIntent()
	  {
	  }

	  protected void initViews()
	  {
	  }

	  protected void initListeners()
	  {
	  }

	  protected abstract void refreshToView(String taskName,Object taskObj,Object paramObject);

	  public boolean onCreateOptionsMenu(Menu menu)
	  {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(getOptionsMenuRes(), menu);
	    return true;
	  }

	  public boolean onOptionsItemSelected(MenuItem item)
	  {
	    fireOptionsItemSelected(item.getItemId());
	    return true;
	  }

	  protected int getOptionsMenuRes()
	  {
	    return RHolder.getInstance().getEva$android$R().menu("common_data_loadable_activity_menu");
	  }

	  protected void fireOptionsItemSelected(int itemId)
	  {
	    if (itemId == 
	      RHolder.getInstance().getEva$android$R().id("newspaper_list_menu_back"))
	    {
	      finish();
	    }
	  }

	  private class QueryDataWorder extends DataLoadingAsyncTask<DataFromClient, Integer, DataFromServer>
	  {
		  private String taskName = "";
		  private Object taskObj = null;
		  
	    public QueryDataWorder(boolean showProgress,String taskName)
	    {
	     	super(DataLoadableMultipleAcitvity.this);
	        init(showProgress,taskName,null);
	    }
	    
	    public QueryDataWorder(boolean showProgress,String taskName,Object obj)
	    {
	    	super(DataLoadableMultipleAcitvity.this);
	        init(showProgress,taskName,obj);
		}
	    
	    private void  init(boolean showProgress,String taskName,Object obj)
	    {
	     	this.taskName = taskName;
	    	this.taskObj = obj;
	        setShowProgress(showProgress);
	    }
	    
		@Override
		protected DataFromServer doInBackground(DataFromClient... params) {
			  Log.d(DataLoadableMultipleAcitvity.class.getSimpleName(),"taskName:  "+taskName+"  上传数据："+(String)params[0].getNewData());
			  return   HttpServiceFactory4AJASONImpl.getInstance().getDefaultService().sendObjToServer(
			  	        DataFromClient.n()
				        .setProcessorId(params[0].getProcessorId())
				        .setJobDispatchId(params[0].getJobDispatchId())
				        .setActionId(params[0].getActionId())
				        .setNewData(params[0].getNewData())
				        .setOldData(params[0].getOldData()));
			
		}

	    protected void onPostExecuteImpl(Object result)
	    {
	       asyncTaskManger.removeAsyncTask(this);
	      if ((result != null) && ((result instanceof DataFromServer)))
	      {
	        if (!((DataFromServer)result).isSuccess()) {
	          DataLoadableMultipleAcitvity.this.finish();
	        }
	      }

	      refreshToView(taskName,taskObj,result);
	    }
	  }
	  
	  public void removeAllAsyncTask()
	  {
		  asyncTaskManger.finishAllAsyncTask();
	  }
	  
	  
	  @Override
	protected void onDestroy() {
		super.onDestroy();
		removeAllAsyncTask();
	}
	  

}
