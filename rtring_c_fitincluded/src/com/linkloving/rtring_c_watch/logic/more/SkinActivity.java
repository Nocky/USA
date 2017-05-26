package com.linkloving.rtring_c_watch.logic.more;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.eva.android.platf.std.DataLoadableActivity;
import com.eva.epc.core.dto.DataFromServer;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.utils.SkinSettingManager;

/**
 * 皮肤设置界面
 * @date 2014-9-28
 * @author xiaohua Deng
 */
public class SkinActivity extends DataLoadableActivity implements OnClickListener{
	
	private SkinSettingManager mSettingManager;
	private TextView title;
	
       @Override
	public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	customeTitleBarResId = R.id.main_more_titleBar;
        setContentView(R.layout.myskin);
        mSettingManager=new SkinSettingManager(this);
		mSettingManager.initSkins();
		findViewById(R.id.imageView1).setOnClickListener(this);
        findViewById(R.id.imageView2).setOnClickListener(this);
        findViewById(R.id.imageView3).setOnClickListener(this);
        findViewById(R.id.imageView4).setOnClickListener(this);
        findViewById(R.id.imageView5).setOnClickListener(this);
        this.setTitle($$(R.string.main_more_theme));
        
       }
    
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.imageView1:
			mSettingManager.toggleSkins(0);	
			break;
		case R.id.imageView2:
			mSettingManager.toggleSkins(1);	
			break;
		case R.id.imageView3:
			mSettingManager.toggleSkins(2);
			break;
		case R.id.imageView4:
			mSettingManager.toggleSkins(3);
			break;
		case R.id.imageView5:
			mSettingManager.toggleSkins(4);
			break;
		}
	}

	@Override
	protected DataFromServer queryData(String... arg0) {
		return null;
	}

	@Override
	protected void refreshToView(Object arg0) {
		
	}
}
