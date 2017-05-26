package com.linkloving.rtring_c_watch.http;

import com.eva.android.platf.corex.HttpServiceFactory4A;
import com.eva.epc.core.endc.HttpServiceRoot;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;

/**
 * 本类是实现了以跨平台（Android、iOS等）的JSON文本为HTTP通信内容的实现类.
 * <p>
 * 本类的使用，不能与http收发java序列化对象等同视之，所有的newData和oldData数据
 * 内容必须是JSON字符串文本，否则到服务端后将被反射或处理失败！
 * <p>
 * 另，从服务端收到的数据即returnValue字段中，也是JSON文本，收到后需要自行从JSON
 * 文本进行转换后才能使用！
 * 
 * @author Jack Jiang, 2014-05-07
 * @version 1.0
 */
// 本类的独立使用，是为了解决在程序崩溃时因android的全局变量机制（Exception时全局变量会被清除掉）
// 而导致的每调用getDefaultSertvice就会nullPointException从而导致连锁崩溃的问题！！！！！！！
// TODO 通过本类是否已经彻底解决这个问题还有待观察！！！！！！！！！！！！！！！！！！！！！！！！
public class HttpServiceFactory4AJASONImpl extends HttpServiceFactory4A
{
	private static HttpServiceFactory4AJASONImpl instance = null;
	public static boolean isOAD;

	public static HttpServiceFactory4AJASONImpl getInstance()
	{
		if(instance == null)
		{
			HttpServiceFactory4A.defaultTipMsgIfFail = MyApplication.getInstance(null)
					.getString(R.string.general_network_faild);
			instance = new HttpServiceFactory4AJASONImpl();
		}
		return instance;
	}
//
//	@Override
//	public HttpServiceRoot getService(String serviceName) {
//		HttpServiceRoot serviceInstance = getServiceInstances().get(serviceName);
//		if(serviceInstance == null)
//		{
//			if(DEFAULT_SERVICE_NAME.equals(serviceName))
//			{
//				serviceInstance = new HttpServiceJASONImpl(serviceName,MyApplication.SERVER_CONTROLLER_URL_ROOT, "MyControllerJSON" );
//				addServices(MyApplication.SERVER_CONTROLLER_URL_ROOT, serviceInstance);
//			}
//		}
//		return serviceInstance;
//	}

	// TODO ！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
	@Override
	public HttpServiceRoot getService(String serviceName)
	{
		HttpServiceRoot serviceInstance = getServiceInstances().get(serviceName);
		if(serviceInstance == null)
		{
			if(DEFAULT_SERVICE_NAME.equals(serviceName))
			{
				if(isOAD){
					serviceInstance = new HttpServiceJASONImpl(serviceName
							,"http://linkloving.com/linkloving_server-watch/"
							, "MyControllerJSON" 
							);
					addServices(
							"http://linkloving.com/linkloving_server-watch/"
							, serviceInstance);
				}else{  //正常情况下
					serviceInstance = new HttpServiceJASONImpl(serviceName,MyApplication.SERVER_CONTROLLER_URL_ROOT, "MyControllerJSON" );
					addServices(MyApplication.SERVER_CONTROLLER_URL_ROOT, serviceInstance);
				}
				
			}
		}
		return serviceInstance;
	}
}