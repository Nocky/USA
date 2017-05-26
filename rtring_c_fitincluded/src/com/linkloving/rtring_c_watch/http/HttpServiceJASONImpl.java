package com.linkloving.rtring_c_watch.http;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import android.util.Log;

import com.eva.epc.core.dto.DataFromClient;
import com.eva.epc.core.dto.DataFromServer;
import com.eva.epc.core.endc.HttpServiceRoot;
import com.google.gson.Gson;
import com.linkloving.utils.EncodeConf;

/**
 * HttpServiceRoot的JSON跨平台（Android、iOS等）数据传输方式的实现类.
 * 
 * @author Jack Jiang, 2014-05-13
 */
public class HttpServiceJASONImpl extends HttpServiceRoot
{
	private final static String TAG = HttpServiceJASONImpl.class.getSimpleName();
	
	public HttpServiceJASONImpl(String serviceName, String servletRootURL,String servletName)
	{
		super(serviceName, servletRootURL, servletName);
	}
	
	/**
	 * <p>
	 * 发送数据给服务端servlet的核心实现方法.<br>
	 * 
	 * 默认发送的是Java序列化对象，子类重写本方法可实现自已的数据发送方式，比如直接发送2进制数据等.
	 * </p>
	 * 
	 * @param _obj 要发送给服务端的数据对象
	 * @param connectionOutputStream 用于数据通信的HttpURLConnection对象所对应的输出流对象，能 过此流完成数据发送
	 * @return 返回输出流对象引用，以便外层调用者统计进行关闭
	 * @throws Exception 过程中产生的任何异常
	 * @see ObjectOutputStream#writeObject(Object)
	 */
	// 重写父类方法的目的是使用JSON文本的跨平台方式与服务端进行通信
	@Override
	protected OutputStream processSendData(DataFromClient _obj,OutputStream connectionOutputStream) throws Exception
	{
		// 发出的是JSON文本描述的DataFromClient对象
		byte[] bs = new Gson().toJson(_obj).getBytes(EncodeConf.ENCODE_TO_CLIENT);//JSON.toJSONString(_obj).getBytes(EncodeConf.ENCODE_TO_CLIENT);
		OutputStream out = connectionOutputStream;
		out.write(bs);
		out.flush();
		out.close();
		return out;
	}
	
	/**
	 * <p>
	 * 接收服务端servlet数据的核心实现方法.<br>
	 * 默认发送的是Java序列化对象，子类重写本方法可实现自已的数据接收方式，比如直接接收2进制数据等.
	 * <br>
	 * <br>
	 * <b>特别说明：</b>接收的数据将被封装成DataFromServer对象，并请务必确保将此对象放入参数dfsesHandle数组的第0索引位置，
	 * 这是保存接收数据的唯一途径，否则无法保证逻辑的正确性！
	 * </p>
	 * 
	 * @param connectionInputStream 用于数据通信的HttpURLConnection对象所对应的输入流对象，通过此流完成数据读取
	 * @param dfsesHandle
	 * @return 返回输入流对象引用，以便外层调用者统计进行关闭
	 * @throws Exception 过程中产生的任何异常
	 * @see ObjectInputStream#readObject()
	 */
	// 重写父类方法的目的是使用JSON文本的跨平台方式与服务端进行通信
	@Override
	protected InputStream processReceiveData(InputStream connectionInputStream, DataFromServer[] dfsesHandle) throws Exception
	{
		InputStream is = connectionInputStream;//req.getInputStream();
		int ch;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((ch = is.read()) != -1)
			bos.write(ch);
		is.close();
		byte[] bs = bos.toByteArray();
		bos.close();
		// 接收的数据是JSON文本描述的DataFromClient对象
		String res = new String(bs, EncodeConf.DECODE_FROM_CLIENT);
		Log.e(TAG, "收到服务端的JSON反馈:"+res);
		dfsesHandle[0] = new Gson().fromJson(res, DataFromServer.class);//JSON.parseObject(res, DataFromServer.class);
		return is;
	}
}
