package com.linkloving.rtring_c_watch.logic.main;
//package com.linkloving.rtring_c.logic.main;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.os.Bundle;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//
//import com.eva.android.widget.util.WidgetUtils;
//import com.eva.android.widget.util.WidgetUtils.ToastType;
//import com.google.zxing.client.android.CaptureActivity;
//import com.linkloving.rtring_c.R;
//
////！！本类有时间再重构！！
////！！本类有时间再重构！！
////！！本类有时间再重构！！
////！！本类有时间再重构！！
////！！本类有时间再重构！！
//public class QRScanActivity extends CaptureActivity
//{
//	Button ok;
//	Button cancel;
//	public static final int RETURN_ID = 1;
//	
//	private String scanedCode = null;
//	
//	@Override
//	public void onCreate(Bundle icicle)
//	{
//		// setContentView(R.layout.capture);
//		super.onCreate(icicle);
//		ok = (Button) findViewById(R.id.camera_button_ok);
//		cancel = (Button) findViewById(R.id.camera_button_cancel);
//		ok.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v)
//			{
//				doIt();
//			}
//		});
//		cancel.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v)
//			{
//				finish();
//			}
//		});
//	}
//	
//	private void doIt()
//	{
//		Intent intent = new Intent();
//		intent.putExtra("__camera_data__", scanedCode);
//		
//		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.... cpSet="+scanedCode);
//		setResult(RETURN_ID, intent);
//		finish();
//	}
//	
//	@Override
//	public void onConfigurationChanged(Configuration newConfig) {
//		super.onConfigurationChanged(newConfig);
//	}
//
//	public void doAfterDecode(String code)
//	{
////		WidgetUtils.showToast(this, "11111111code=="+code, ToastType.OK);//
////		System.out.println("！！！！>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>.... scanedCode="+scanedCode);
//		scanedCode = code;
//		doIt();
//		
//		
//	}
//}