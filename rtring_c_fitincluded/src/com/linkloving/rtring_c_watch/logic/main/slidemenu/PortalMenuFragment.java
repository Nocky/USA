package com.linkloving.rtring_c_watch.logic.main.slidemenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eva.epc.common.util.CommonUtils;
import com.example.android.bluetoothlegatt.BLEProvider;
import com.linkloving.rtring_c_watch.MyApplication;
import com.linkloving.rtring_c_watch.R;
import com.linkloving.rtring_c_watch.logic.launch.LoginActivity;
import com.linkloving.rtring_c_watch.logic.main.OwnBraceletActivity;
import com.linkloving.rtring_c_watch.logic.main.slidemenu.CommonAdapter.MenuVO;
import com.linkloving.rtring_c_watch.logic.more.MoreActivity;
import com.linkloving.rtring_c_watch.logic.more.UserActivity;
import com.linkloving.rtring_c_watch.logic.more.avatar.ShowUserAvatar;
import com.linkloving.rtring_c_watch.utils.IntentFactory;
import com.rtring.buiness.logic.dto.UserEntity;

public class PortalMenuFragment extends Fragment
{
	public static final int REQUEST_CODE_FOR_GO_TO_MORE$ACTIVITY = 999;
	
	private ListView mList;
	private ImageView head;
	private TextView name;
	private Button exitBtn;
	
	// 当此值为true时表示本Activity中已尝试过从服务端更新头像，否由表示未尝试过
	// 此值的目的是控制头像从服务端的更新仅在本Activity的生命周期中执行1次以结省服务端性能和压力，仅此而已
	private boolean tryGetAvatarFromServer = false;
	private ShowUserAvatar showUserAvatarWrapper= null;
	
	private MenuAdapter mAdapter;
	
	/** 蓝牙管理类 */
	private BLEProvider provider;
	
	private int send_index = 0;
	private boolean CAN_RING = true; //防止重复点击
	
	public enum MenuIndex
	{
		SPORT,RANKING,CLOCK,SEDENTARY,GOAL,RELATIONSHIP,RING,MORE
	}
	
	int[] menuIcon = { 
			R.drawable.menu_icon_sport_data, 
			R.drawable.menu_icon_ranking,
			R.drawable.menu_icon_clock,
			R.drawable.menu_icon_sedentary, 
			R.drawable.menu_icon_goal, 
			R.drawable.menu_icon_friends,
			R.drawable.menu_icon_ring,
			R.drawable.menu_icon_more
			};
	
	int[] menuText = { 
			R.string.menu_sport_data,
			R.string.menu_ranking, 
			R.string.menu_clock, 
			R.string.menu_sedentary,
			R.string.menu_goal,
			R.string.relationship,
			R.string.menu_ring,
			R.string.menu_more 
			};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		return inflater.inflate(R.layout.fragment_portal_menu, container,false);
	}
	
	
	public void refreshUI()
	{
		// 现在只在onCreate时刷新本地用户头像：可能更新不太及时，但总比要onResume里对服务端的性能压力要小吧
		UserEntity u = MyApplication.getInstance(getActivity()).getLocalUserInfoProvider();
		if(u != null)
		{
			// 更新本地用户头像
			showUserAvatarWrapper = new ShowUserAvatar(getActivity(), u.getUser_id(), head, true
					, 120 , 120 // 此头像用于界面中好友的头像显示，大小参考：main_more布局中的@+id/main_more_settings_avatarView（60dp*60dp）
					){
				@Override
				protected void avatarUpdateForDownload(Bitmap cachedAvatar)
				{
					super.avatarUpdateForDownload(cachedAvatar);
					tryGetAvatarFromServer = true;
				}
			};
			name.setText(u.getNickname());
		}
		refreshDatas();
	}
	

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
		provider = MyApplication.getInstance(getActivity()).getCurrentHandlerProvider();
		initView(view);
		setAdapter();
		bindListener();

		// 现在只在onCreate时刷新本地用户头像：可能更新不太及时，但总比要onResume里对服务端的性能压力要小吧
		UserEntity u = MyApplication.getInstance(getActivity()).getLocalUserInfoProvider();
		if(u != null)
		{
			// 更新本地用户头像
			showUserAvatarWrapper = new ShowUserAvatar(getActivity(), u.getUser_id(), head, true
					, 120 , 120 // 此头像用于界面中好友的头像显示，大小参考：main_more布局中的@+id/main_more_settings_avatarView（60dp*60dp）
					){
				@Override
				protected void avatarUpdateForDownload(Bitmap cachedAvatar)
				{
					super.avatarUpdateForDownload(cachedAvatar);
					tryGetAvatarFromServer = true;
				}
			};
			
			name.setText(u.getNickname());
		}
		refreshDatas();
		
	}
	
	private void initView(View view)
	{
		mList = (ListView) view.findViewById(R.id.fragment_menu_list);
		head = (ImageView) view.findViewById(R.id.fragment_head);
		name = (TextView) view.findViewById(R.id.fragment_name_text);
		
		exitBtn = (Button) view.findViewById(R.id.fragment_menu_exit);
	}
	
	private void setAdapter()
	{
		List<MenuVO> list = new ArrayList<MenuVO>();
		for(int i = 0;i < menuIcon.length ;i++)
		{
			MenuVO vo = new MenuVO();
			vo.setImgID(menuIcon[i]);
			vo.setTextID(menuText[i]);
			list.add(vo);
		}
		
	    mAdapter = new MenuAdapter(getActivity(), list);
		mList.setAdapter(mAdapter);
	}
	
	private void bindListener()
	{
		mList.setOnItemClickListener(new OnItemClickListener()
		{
			// IntentFactory.startAlarmActivityIntent(SettingWatchActivity.this);
			//IntentFactory.startLongSitActivityIntent(SettingWatchActivity.this,RESQUEST_SETTING);
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,
					long arg3) {
				
				if (MenuIndex.MORE.ordinal() == index)
				{
					// 更多
					Intent intent = new Intent(PortalMenuFragment.this.getActivity(), MoreActivity.class);
					startActivityForResult(intent, REQUEST_CODE_FOR_GO_TO_MORE$ACTIVITY);
					
				}
				else if (MenuIndex.RING.ordinal() == index)
				{
					final int count = 10;
					//已经绑定
					if(!CommonUtils.isStringEmpty(MyApplication.getInstance(getActivity()).getLocalUserInfoProvider().getLast_sync_device_id())){
						
						if(!provider.isConnectedAndDiscovered()){
							Toast.makeText(getActivity(), "Bluetooth not connected", Toast.LENGTH_SHORT).show();
						}else if(!CAN_RING){
//							Toast.makeText(getActivity(), "Fin", Toast.LENGTH_SHORT).show();
						}
						// 震动
						else 
						{
								CAN_RING = false ;
								final Timer timer = new Timer(); // 每分钟更新一下蓝牙状态
								timer.schedule(new TimerTask()
								{
									@Override
									public void run()
									{
										MyApplication.getInstance(getActivity()).getCurrentHandlerProvider().SetBandRing(getActivity());
										send_index++;
										if(send_index == count ){
											send_index = 0;
											timer.cancel();
											CAN_RING = true ;
										}
									}
								}, 0, 1000);
								
						}
						
					}else{
						
						Toast.makeText(getActivity(), getString(R.string.portal_main_unbound_msg), Toast.LENGTH_SHORT).show();
					}
					
					
				}
				else if (MenuIndex.RANKING.ordinal() == index)
				{
					// 排行榜
					IntentFactory.startRankingActivityIntent(PortalMenuFragment.this.getActivity());
				}
				else if (MenuIndex.SPORT.ordinal() == index)
				{
					// 运动趋势
					getActivity().startActivity(IntentFactory.createReportActivityIntent(getActivity()));
				}
				else if (MenuIndex.GOAL.ordinal() == index)
				{
					// 运动目标
					IntentFactory.startGoalActivityIntent(PortalMenuFragment.this.getActivity());
				}
				else if (MenuIndex.RELATIONSHIP.ordinal() == index) 
				{
					//关系圈
					getActivity().startActivity(IntentFactory.createRelationshipGroupActivityIntent(getActivity()));
				}
				else if (MenuIndex.CLOCK.ordinal() == index) 
				{
					//闹钟
					IntentFactory.startAlarmActivityIntent(getActivity());
				}
				else if (MenuIndex.SEDENTARY.ordinal() == index) 
				{
					//久坐
					IntentFactory.startLongSitActivityIntent(getActivity());
				}
			}
		});
		
		head.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(PortalMenuFragment.this.getActivity(), UserActivity.class);
				startActivityForResult(intent, MoreActivity.REQUEST_CODE_FOR_GO_TO_USER$ACTIVITY);
			}
		});
		
		exitBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0)
			{
//				new com.eva.android.widgetx.AlertDialog.Builder(PortalMenuFragment.this.getActivity())
//				.setTitle(null)
//				.setItems(new String[] {"重新登陆","退出程序"}
//					, null
////					new DialogInterface.OnClickListener() {
////				         @Override
////				         public void onClick(DialogInterface dialog, int which) 
////				         {
////				        	 System.out.println("》》》》》》》》which="+which);
////				         }
////			        }
//					)
//				.setNegativeButton("取消", null)
//				.show();
				
				new com.eva.android.widgetx.AlertDialog.Builder(PortalMenuFragment.this.getActivity())
				.setTitle(getActivity().getString(R.string.menu_exit_mode))//PortalMenuFragment.this.getActivity().getResources().getString(R.string.general_prompt))  
				.setPositiveButton(getActivity().getString(R.string.menu_exit_app)
						,  new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog,int which)
						{
							LoginActivity.doExitNoConfirm(PortalMenuFragment.this.getActivity());
						}

						
					}) 
				.setNegativeButton(getActivity().getString(R.string.login_form_relogin_text)
						, new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog,int which)
						{
							LoginActivity.relogin(PortalMenuFragment.this.getActivity());
//							//** 【关于重新登陆实现的说明】重新登陆的目的是注销原会话，重新登陆，代码可参考LoginActivity类中的doExitNoConfirm(..)
//							//** 方法，但也不能完全一样，至少不能退出程序、不能调用MyApplication.releaseAll等。注意：此处的重登实际上Application
//							//** 还是完整的，一定要保证该重置的重置哦。最好的方式是如果能像微信一样重新启动应用则是最好的，但没找到好方法来实现！！！
//							// 重置自动上传检查开关
//							MyApplication.getInstance(PortalMenuFragment.this.getActivity()).setPreparedForOfflineSyncToServer(false);
//							// 清除APP产生的所有Notification
//							NotificationPromptHelper.cancalAllNotification(PortalMenuFragment.this.getActivity());
//							
//							// TODO 退出时要关闭与服务端的会话，与LoginActivity.doExitNoConfirm(..)保持一致即可
//
//							//** 重新开启登陆界面
//							MyApplication.getInstance(getActivity()).setLocalUserInfoProvider(null); // 把用信息置空（它会自动把本地存储的也清空！）
//							Intent intent = new Intent(getActivity(), LoginActivity.class);
//							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);// 注意此行！
//							startActivity(intent);
//							getActivity().finish();
						}
					})
				.show(); 
			}
		});
	}
	
		
	private void refreshDatas()
	{
		//把用户名设置到”当前登陆用户“组件上，以便查看
		UserEntity u = MyApplication.getInstance(getActivity()).getLocalUserInfoProvider();
		if(u != null)
		{
			name.setText(u.getNickname());
		}
		
		// 有onResume方法中刷新用户头像的目的是保持本地头像的及时刷新
		if(showUserAvatarWrapper != null)
		{
			if(!tryGetAvatarFromServer)
				showUserAvatarWrapper.setNeedTryGerAvatarFromServer(true);
			else
				showUserAvatarWrapper.setNeedTryGerAvatarFromServer(false);
			showUserAvatarWrapper.showCahedAvatar();
		}
	}



	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// 从个人信息界面返回的，那么就尝试刷新他的头像吧（因为在这个界面里可能修改了用户的头像哦）
		if(requestCode == REQUEST_CODE_FOR_GO_TO_MORE$ACTIVITY || requestCode == MoreActivity.REQUEST_CODE_FOR_GO_TO_USER$ACTIVITY)
		{
			UserEntity u = MyApplication.getInstance(PortalMenuFragment.this.getActivity()).getLocalUserInfoProvider();
			if(u != null)
			{
				// 更新本地用户头像
				new ShowUserAvatar(PortalMenuFragment.this.getActivity(), u.getUser_id(), head
						// 注意：此时就没有必要尝试从服务端更新头像了，因为在用户个人信
						// 息界面里如果要更新那么就已经存了一个新缓存到本地了
						, false 
						, 120 , 120 // 此头像用于界面中好友的头像显示，大小参考：main_more布局中的@+id/main_more_settings_avatarView（60dp*60dp）
				).showCahedAvatar();
			}
			
			name.setText(u.getNickname());
		}
	}
	
	public void updateUnReadCount()
	{
		mAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		updateUnReadCount();
	}

}
