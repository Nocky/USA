package com.linkloving.rtring_c_watch.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class IncomingSMSReceiver extends BroadcastReceiver {
	public static final String TAG = "IncomingSMSReceiver";
	
	private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(SMS_RECEIVED)){
			Log.d(TAG, "我接受到短信了！" );  
			 StringBuilder sb=new StringBuilder();
			 Bundle bundle=intent.getExtras();
			 if(bundle!=null)
			 {
				 
			   //pdus为android内置短信参数idetifier，通过bundle.get("")返回pdus的对象
			   Object[] myOBJpdus=(Object[]) bundle.get("pdus");
			   SmsMessage[] msg=new SmsMessage[myOBJpdus.length];
			   
			   for(int i=0;i<msg.length;i++)
			   {
			      msg[i]=SmsMessage.createFromPdu((byte[]) myOBJpdus[i]);
			   }
			   
			   for(SmsMessage currentMessage:msg)
			   {
			   sb.append("接受电话来自：n");
			   sb.append(currentMessage.getDisplayOriginatingAddress());
			   sb.append("n--------传来的短信内容---------n");
			   sb.append(currentMessage.getDisplayMessageBody());
			   }
			   
			}
			 Toast.makeText(context, sb.toString(), Toast.LENGTH_LONG).show();
		}
	}

}
