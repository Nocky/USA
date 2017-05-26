package com.linkloving.rtring_c_watch.logic.sns;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TabHost;
import android.widget.TextView;

import com.eva.android.widget.CustomeTitleBar;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.utils.IntentFactory;
import com.linkloving.rtring_c_watch.utils.SkinSettingManager;
import com.linkloving.rtring_c_watch.utils.ToolKits;

/**
 * 朋友圈主界面
 * @author Administrator
 *
 */
public class RelationshipGroupActivity extends TabActivity 
{
	 
    private TabHost mTabHost; 
    private CustomeTitleBar titleBar;
    
    private Button addButton;
    private RadioButton concernAboutBtn;
    private RadioButton concernMeBtn;
    private RadioButton commentsBtn;
    
    private TextView unRead;
    
    private PopupWindow pop;
    
    public enum TabIndex
	{
		CONCERNABOUT,CONCERNME,COMMENTS
	}
    
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_relationship_group); 
	    mTabHost = getTabHost(); 
		pop = createPopupWindow(RelationshipGroupActivity.this,
				R.layout.popup_relationship_view,
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, false);
	    
	    concernAboutBtn = (RadioButton) findViewById(R.id.concern_about_btn);
	    concernMeBtn = (RadioButton) findViewById(R.id.concern_me_btn);
	    commentsBtn = (RadioButton) findViewById(R.id.comments_btn);
	    concernAboutBtn.setChecked(true);
	    
	    titleBar = (CustomeTitleBar) findViewById(R.id.relationship_group_titleBar);
	    
	    unRead = (TextView) findViewById(R.id.relationship_unread_text);
	    updateUnreadCount();
	    
	    titleBar.getTitleView().setText(R.string.relationship);
	    addButton = titleBar.getRightGeneralButton();
	    addButton.setVisibility(View.VISIBLE);
	    addButton.setBackgroundResource(R.drawable.title_add);
	    addButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0)
			{
				pop.showAtLocation(mTabHost, Gravity.TOP|Gravity.RIGHT,ToolKits.dip2px(RelationshipGroupActivity.this, 10), ToolKits.dip2px(RelationshipGroupActivity.this, 75));
				View view = pop.getContentView();
				LinearLayout nearby = (LinearLayout) view.findViewById(R.id.nearby_linear);
				LinearLayout search_user = (LinearLayout) view.findViewById(R.id.search_user_linear);
				LinearLayout search_group = (LinearLayout) view.findViewById(R.id.search_group_linear);
				
				nearby.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) 
					{
						pop.dismiss();
						startActivity(new Intent(RelationshipGroupActivity.this, NearbyActivity.class));
					}
				});
				
				search_user.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0)
					{
						pop.dismiss();
						startActivity(IntentFactory.createSearchActivity(RelationshipGroupActivity.this, SearchActivity.USE_TO_SEARCH_USER));
					}
				});
				
				search_group.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0)
					{
						pop.dismiss();
						startActivity(IntentFactory.createSearchActivity(RelationshipGroupActivity.this, SearchActivity.USE_TO_SEARCH_GROUP));
					}
				});
			}
		});
	    
	    mTabHost.addTab(mTabHost.newTabSpec("tab1")   
                .setIndicator("Followed")  
                .setContent(new Intent(this, ConcernAboutActivity.class)));  
	    mTabHost.addTab(mTabHost.newTabSpec("tab2")   
                .setIndicator("Followers")   
                .setContent(new Intent(this, ConcernMeActivity.class)));   
	    mTabHost.addTab(mTabHost.newTabSpec("tab3")   
                .setIndicator("Comment")   
                .setContent(new Intent(this, CommentsActivity.class)));  
	    
      concernAboutBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) 
			{
			     mTabHost.setCurrentTab(TabIndex.CONCERNABOUT.ordinal());	
                 addButton.setVisibility(View.VISIBLE);
			}
		});
      
      concernMeBtn.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0)
		{
			 addButton.setVisibility(View.GONE);
		     mTabHost.setCurrentTab(TabIndex.CONCERNME.ordinal());	
		}
	});
      
      commentsBtn.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0)
		{
			addButton.setVisibility(View.GONE);
		    mTabHost.setCurrentTab(TabIndex.COMMENTS.ordinal());	
		}
	});
	}
	
	private PopupWindow createPopupWindow(Context context,int layoutID,int width,int height,boolean focus)
	{
		View contentView = View.inflate(context, layoutID, null);
		PopupWindow pop = new PopupWindow(contentView, width, height,focus);
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setOutsideTouchable(true);
		return pop;
	}
	
	public void updateUnreadCount()
	{
		int num =  MyApplication.getInstance(RelationshipGroupActivity.this).getCommentNum();
		if(num > 0)
		{
			unRead.setVisibility(View.VISIBLE);
			unRead.setText(ToolKits.getUnreadString(num));
		}
		else
		{
			unRead.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected void onResume() 
	{
		super.onResume();
		updateUnreadCount();
	}
	
}
