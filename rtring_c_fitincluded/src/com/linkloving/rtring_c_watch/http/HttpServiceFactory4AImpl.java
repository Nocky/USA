package com.linkloving.rtring_c_watch.http;

import com.eva.android.platf.corex.HttpServiceFactory4A;
import com.eva.epc.core.endc.HttpServiceRoot;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;

/**
 * 本类是传统的Java序列化对象作为数据传输方式的实现类.
 * 
 * @author Jack Jiang, 2014-05-07
 * @version 1.0
 */
// 本类的独立使用，是为了解决在程序崩溃时因android的全局变量机制（Exception时全局变量会被清除掉）
// 而导致的每调用getDefaultSertvice就会nullPointException从而导致连锁崩溃的问题！！！！！！！
// TODO 通过本类是否已经彻底解决这个问题还有待观察！！！！！！！！！！！！！！！！！！！！！！！！
public class HttpServiceFactory4AImpl extends HttpServiceFactory4A
{
	private static HttpServiceFactory4AImpl instance = null;

	public static HttpServiceFactory4AImpl getInstance()
	{
		if(instance == null)
		{
			HttpServiceFactory4A.defaultTipMsgIfFail = MyApplication.getInstance(null)
					.getString(R.string.general_network_faild);
			instance = new HttpServiceFactory4AImpl();
		}
		return instance;
	}

	// TODO ！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
	@Override
	public HttpServiceRoot getService(String serviceName)
	{
		HttpServiceRoot serviceInstance = getServiceInstances().get(serviceName);
		if(serviceInstance == null)
		{
			if(DEFAULT_SERVICE_NAME.equals(serviceName))
			{
				serviceInstance = new HttpServiceRoot(serviceName
						,MyApplication.SERVER_CONTROLLER_URL_ROOT
//						, "http://192.168.82.138:8080/rtring_s_new/"
						, "MyController" //,"MyController"
						);
				addServices(
						MyApplication.SERVER_CONTROLLER_URL_ROOT
//						"http://192.168.82.138:8080/rtring_s_new/"
						, serviceInstance);
			}
		}
		return serviceInstance;
	}
}