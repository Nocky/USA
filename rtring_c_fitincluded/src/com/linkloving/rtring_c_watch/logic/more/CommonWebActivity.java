package com.linkloving.rtring_c_watch.logic.more;

import android.content.Context;
import android.net.http.SslError;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.eva.android.platf.std.DataLoadableActivity;
import com.eva.android.widget.AProgressDialog;
import com.eva.epc.core.dto.DataFromServer;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.utils.IntentFactory;

public class CommonWebActivity extends DataLoadableActivity 
{
	protected WebView webView;
	
	protected String url;
	
	protected LayoutInflater inflater;
	
	protected View progressView;
	
	protected AProgressDialog pd = null;
	
	
	@Override
	protected void initViews()
	{
		customeTitleBarResId = R.id.common_webview_titlebar;
		setContentView(R.layout.common_web_view);
		
		inflater = LayoutInflater.from(CommonWebActivity.this);
		
		pd = new AProgressDialog(this, getString(R.string.general_loading));
		pd.show();
		
		url = IntentFactory.parseCommonWebIntent(getIntent());
		webView = (WebView) findViewById(R.id.view_web_webview);
		progressView = inflater.inflate(R.layout.common_web_view_progress, null);
		WebSettings webSettings = webView.getSettings();       
//		webSettings.setUseWideViewPort(true);
		/**
		 * 此属性设置后，将会将内容限制到屏幕宽度。如果有超出部分将会被挤压导致变形。
		 */
//		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
	    webSettings.setJavaScriptEnabled(true); 
	    webSettings.setSupportZoom(true);
	    webSettings.setDefaultZoom(ZoomDensity.FAR);
	    webSettings.setBuiltInZoomControls(true);
	    
	    webView.setWebViewClient(new mWebViewClient());
	    webView.setWebChromeClient(new WebChromeClient()
	    {  
            @Override  
            public void onProgressChanged(WebView view, int newProgress) 
            {  
                super.onProgressChanged(view, newProgress);  
                //这里将textView换成你的progress来设置进度  
//              if (newProgress == 0) 
//              {  
//                  textView.setVisibility(View.VISIBLE);  
//                   progressBar.setVisibility(View.VISIBLE);  
//              }  
                if (newProgress == 100) 
                {  
                	pd.dismiss();
                }  
            }  
	    });
	    
	    webView.loadUrl(url);
		
		setTitle("");
		super.initViews();
	}
	
	private class mWebViewClient extends WebViewClient
	{
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url)
		{
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
			super.onReceivedSslError(view, handler, error);
			//忽略证书的错误继续Load页面内容  
            handler.proceed();  
            //handler.cancel(); // Android默认的<strong>处理</strong>方式  
            //handleMessage(Message msg); // 进行其他<strong>处理</strong> 
		}
		
		
	}
	
	//android2.0前需要重写onKeyDown方法才能实现，2.0及以后直接重写onBackPressed即可哦
	/** 
	 * 捕获back键，实现调用 {@link #doExit(Context)}方法.
	 */
	@Override
	public void onBackPressed()
	{
		if (webView.canGoBack()) 
			webView.goBack(); //goBack()表示返回WebView的上一页面 
		else
			super.onBackPressed();
	}

	/* (non-Javadoc)
	 * @see com.eva.android.platf.std.DataLoadableActivity#queryData(java.lang.String[])
	 */
	@Override
	protected DataFromServer queryData(String... arg0)
	{
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.eva.android.platf.std.DataLoadableActivity#refreshToView(java.lang.Object)
	 */
	@Override
	protected void refreshToView(Object arg0)
	{
		// TODO Auto-generated method stub
		
	}
	
	
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event)
//	{
//		 if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) 
//		 { 
//	            webView.goBack(); //goBack()表示返回WebView的上一页面 
//	            return true; 
//	     } 
//	     return super.onKeyDown(keyCode, event); 
//	}
}
