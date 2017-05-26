package com.linkloving.rtring_c_watch.utils;

import android.app.NotificationManager;
import android.content.Context;

public class NotificationPromptHelper
{
//	// 【重要说明】每添加一种Notification则务必去方法cancalAllNotification()中加入清除处理！！！！
//	// 【重要说明】每添加一种Notification则务必去方法cancalAllNotification()中加入清除处理！！！！
//	// 【重要说明】每添加一种Notification则务必去方法cancalAllNotification()中加入清除处理！！！！
//	// 【重要说明】每添加一种Notification则务必去方法cancalAllNotification()中加入清除处理！！！！
//	// 【重要说明】每添加一种Notification则务必去方法cancalAllNotification()中加入清除处理！！！！
//	// 【重要说明】每添加一种Notification则务必去方法cancalAllNotification()中加入清除处理！！！！
//	// 【重要说明】每添加一种Notification则务必去方法cancalAllNotification()中加入清除处理！！！！
//	public final static int NOTIFICATION_UNIQE_IDENT_ID_ADD$FRIEND$REQUEST = 1;
//	public final static int NOTIFICATION_UNIQE_IDENT_ID_ADD$FRIEND$REQUEST_RESPONSE$FOR$ERROR_SERVER$TO$A = 2;
//	public final static int NOTIFICATION_UNIQE_IDENT_ID_NEW$FRIEND$ADD$SUCESS = 3;
//	/** 加好友被拒绝时的提示 */
//	public final static int NOTIFICATION_UNIQE_IDENT_ID_ADD$FRIEND$BE$REJECT = 4;
//	/** 相关处理界面处于后台时接收到音视频聊天请求时的提示 */
//	public final static int NOTIFICATION_UNIQE_IDENT_ID_VOICE$VIDEO$CHAT$REQUEST = 5;
//	/** 相关处理界面处于后台时接收到好友发过来的角色指令时的提示 */
//	public final static int NOTIFICATION_UNIQE_IDENT_ID_RECIEVED$FRIEND$SCENSE$CMD = 6;
//	/** 相关处理界面处于后台时接收到好友发过来的聊天消息时的提示 */
//	public final static int NOTIFICATION_UNIQE_IDENT_ID_RECIEVED$FRIEND$MESSAGE = 7;
//	/** 相关处理界面处于后台时接收到实时语音聊天请求时的提示 */
//	public final static int NOTIFICATION_UNIQE_IDENT_ID_REAL$TIME$VOICE$CHAT$REQUEST = 9;
//	
//	/** 相关处理界面处于后台时接收到好友发过来的临时聊天消息时的提示 */
//	public final static int NOTIFICATION_UNIQE_IDENT_ID_RECIEVED$TEMP$MESSAGE = 10;
//	/** 相关处理界面处于后台时接收到好友发过来的BBS聊天消息时的提示 */
//	public final static int NOTIFICATION_UNIQE_IDENT_ID_RECIEVED$BBS$MESSAGE = 11;
//	
//	public static void showAddFriendRequestNotivication(Context context
//			, RosterElementEntity srcUserInfo)
//	{
//		NotificationHelperEx.addNotificaction(
//				NOTIFICATION_UNIQE_IDENT_ID_ADD$FRIEND$REQUEST, context
////				, new Intent()//IntentFactory.createPortalIntent(context)
//				, IntentFactory.createFriendReqProcessIntent(context, srcUserInfo)
//				, R.drawable.main_alarms_sns_addfriendreject_message_icon
//				//, "Received a request to be lover from "+srcUserInfo.getNickname()+"..."
//				, MessageFormat.format(context.getResources().getString(R.string.notification_sns_add_friend_reauest_ticker_text), srcUserInfo.getNickname())
//				, context.getResources().getString(R.string.notification_sns_add_friend_reauest_info_title)
//				, MessageFormat.format(context.getResources().getString(R.string.notification_sns_add_friend_reauest_info_text), srcUserInfo.getNickname())
//				, true, true);
//	}
//	
//	public static void showAddFriendRequest_RESPONSE$FOR$ERROR_SERVER$TO$ANotivication(Context context
//			, String errorMsg)
//	{
//		NotificationHelperEx.addNotificaction(
//				NOTIFICATION_UNIQE_IDENT_ID_ADD$FRIEND$REQUEST_RESPONSE$FOR$ERROR_SERVER$TO$A, context
//				, new Intent()//IntentFactory.createFriendInfoIntent(context, friendInfo, false)
//				, R.drawable.main_alarms_sns_addfrienderror_message_icon
//				, context.getResources().getString(R.string.notification_sns_add_friend_response_from_server_error_ticker_text)
//				, context.getResources().getString(R.string.notification_sns_add_friend_response_from_server_error_info_title)
//				, errorMsg, true, true);
//	}
//	
//	/**
//	 * 新添加的好友成列加入到好友列表了.
//	 * 
//	 * @param context
//	 * @param errorMsg
//	 */
//	public static void showNewFriendAddSucessNotivication(Context context
//			, String newFriendNickName, String friendUID)
//	{
//		NotificationHelperEx.addNotificaction(
//				NOTIFICATION_UNIQE_IDENT_ID_NEW$FRIEND$ADD$SUCESS, context
//				, IntentFactory.createChatIntent(context, friendUID)
//				, R.drawable.main_alarms_sns_addfriendok_message_icon
////				, "Successfully completed to add a new lover "+newFriendNickName
//				, MessageFormat.format(context.getResources().getString(R.string.notification_sns_add_friend_success_ticker_text), newFriendNickName)
//				, context.getResources().getString(R.string.notification_sns_add_friend_success_info_title)
//				, MessageFormat.format(context.getResources().getString(R.string.notification_sns_add_friend_success_info_text), newFriendNickName)
//				, true, true);
//	}
//	
//	/**
//	 * 加好友被拒绝时的提示（由服务端提示加好友发起人A）.
//	 * 
//	 * @param context
//	 * @param rejectSrcNickName 拒绝者(B)的昵称
//	 */
//	public static void showAddFriendBeRejectNotivication(Context context
//			, RosterElementEntity userInfoFromServer)
//	{
//		NotificationHelperEx.addNotificaction(
//				NOTIFICATION_UNIQE_IDENT_ID_ADD$FRIEND$BE$REJECT, context
//				, IntentFactory.createFriendInfoIntent(context, userInfoFromServer)//, false)
//				, R.drawable.main_alarms_sns_addfrienderror_message_icon
////				, userInfoFromServer.getNickname()+" refused your lover request"
//				, MessageFormat.format(context.getResources().getString(R.string.notification_sns_add_friend_reject_ticker_text), userInfoFromServer.getNickname())
//				, context.getResources().getString(R.string.notification_sns_add_friend_reject_info_title)
//				, MessageFormat.format(context.getResources().getString(R.string.notification_sns_add_friend_reject_info_text), userInfoFromServer.getNickname())
//				, true, true);
//	}
//	
//	//############################################################################ 2013-11-16因不稳定先去掉了新音视频框架！ S
////	/**
////	 * 相关处理界面处于后台时接收到音视频聊天请求时的提示（来自发起人A）.
////	 * 
////	 * @param context
////	 * @param friendNickName 昵称
////	 */
////	public static void showVoiceAndVideoRequestNotivication(Context context
////			, String friendUID, String friendNickName)
////	{
////		NotificationHelper.addNotificaction(
////				NOTIFICATION_UNIQE_IDENT_ID_VOICE$VIDEO$CHAT$REQUEST, context
////				, IntentFactory.createVideoCallComeIntent(context, friendUID)
////				, R.drawable.sns_friend_list_form_item_defult_portrait
////				, friendNickName+"请求你进行音视频聊天"
////				, "请求音视频聊天"
////				, "注意，"+friendNickName+"正在请求你进行音视频聊天，请打开界面处理之.", true, true);
////	}
//	//############################################################################ 2013-11-16因不稳定先去掉了新音视频框架！ E
//	
//	/**
//	 * 相关处理界面处于后台时接收到音视频聊天请求时的提示（来自发起人A）. -- AnyChat
//	 * 
//	 * @param context
//	 * @param friendNickName 昵称
//	 */
//	public static void showVoiceAndVideoRequestNotivication(Context context
//			, String friendUID, String friendNickName)
//	{
//		NotificationHelperEx.addNotificaction(
//				NOTIFICATION_UNIQE_IDENT_ID_VOICE$VIDEO$CHAT$REQUEST, context
//				, IntentFactory.createChatIntent(context, friendUID)//new Intent() //IntentFactory.createVideoCallComeIntent_anychat(context, friendUID)
//				, R.drawable.main_alarms_chat_opr_realtime_video_icon
////				, friendNickName+" requests you to audio and video chat"
//				, MessageFormat.format(context.getResources().getString(R.string.notification_video_and_voice_chat_request_ticker_text), friendNickName)
//				, context.getResources().getString(R.string.notification_video_and_voice_chat_request_info_title)
//				, MessageFormat.format(context.getResources().getString(R.string.notification_video_and_voice_chat_request_info_text), friendNickName)
//				, true, true);
//	}
//	
//	/**
//	 * 相关处理界面处于后台时接收到好友发过来的角色指令时的提示（来自发起人A）.
//	 * 
//	 * @param context
//	 * @param friendNickName 昵称
//	 */
//	public static void showRecievedFriendScenseCmdNotivication(Context context
//			, String friendUID, String friendNickName, SenceCmdDTO senceCmd)
//	{
//		String cmdStr = "";
//		switch(senceCmd.getCmdType())
//		{
//			case SenceCmdDTO.CMD_SWITCH_BACKGROUND:
//				cmdStr = "背景";
//			break;
//			case SenceCmdDTO.CMD_PLAY_REPLY:
//				cmdStr = "快捷回复";
//			break;
//			case SenceCmdDTO.CMD_PLAY_FACE:
//				cmdStr = "表情";
//			break;
//			case SenceCmdDTO.CMD_PLAY_SOUND:
//				cmdStr = "声音";
//			break;
//		}
//		
//		NotificationHelperEx.addNotificaction(
//				NOTIFICATION_UNIQE_IDENT_ID_RECIEVED$FRIEND$SCENSE$CMD, context
//				, new Intent()
//				, R.drawable.main_alarms_system_message_icon
//				, friendNickName+"Andy has sent an ["+cmdStr+"] role order"
//				, "Received your friend's role order"
//				, "Tip："+friendNickName+"sent an ["+cmdStr+"] role order, receive by opening his/her chat interface.", true, true);
//	}
//	
//	/**
//	 * 相关处理界面处于后台时接收到好友发过来的聊天消息时的提示（来自发起人A）.
//	 * 
//	 * @param context
//	 * @param friendNickName 昵称
//	 */
//	public static void showRecievedFriendMessageNotivication(Context context
//			, String friendUID, String friendNickName, String message)
//	{
//		NotificationHelperEx.addNotificaction(
//				NOTIFICATION_UNIQE_IDENT_ID_RECIEVED$FRIEND$MESSAGE, context
//				, IntentFactory.createChatIntent(context, friendUID)//new Intent()
//				, R.drawable.main_alarms_chat_message_icon
////				, friendNickName+" said: "+message
//				, MessageFormat.format(context.getResources().getString(R.string.notification_text_chatting_message_ticker_text), friendNickName, message)
//				, MessageFormat.format(context.getResources().getString(R.string.notification_text_chatting_message_info_title), friendNickName)
//				, message, true, false);
//	}
//	
////	/**
////	 * 相关处理界面处于后台时接收到好友的蓝牙交互控制请求时的提示（来自发起人A）.
////	 * 
////	 * @param context
////	 * @param friendNickName 昵称
////	 */
////	public static void showRecievedFriendBluetoothInteractControlPermissionNotivication(Context context
////			, String friendUID, String friendNickName, String messageContent)
////	{
////		NotificationHelper.addNotificaction(
////				NOTIFICATION_UNIQE_IDENT_ID_RECIEVED$FRIEND$BLUETOOTH$INTERACT$CONTROL$PERMISSION, context
////				, new Intent()
////				, R.drawable.sns_friend_list_form_item_defult_portrait
////				, "收到"+friendNickName+"蓝牙交互控制消息"
////				, "蓝牙交互控制消息"
////				, messageContent, true, true);//"提示："+friendNickName+"正在请求你进行蓝牙交互控制，请打开界面处理之.", true);
////	}
//	
//	/**
//	 * 收到一个临时聊天消息哦.
//	 * <p>目前此Notivication里只处理了“普通文本消息”哦！！！！！！！！
//	 * 
//	 * @param context
//	 * @param tcmd 临时聊天消息数据封装对象（自kchat2.2(20140212)后，msg_content存放的不再是
//	 * 简单文本消息内容而是TextMessage对象的JSON文本，如果读取聊天文本消息则需要进行JSON解析哦）
//	 * @param messageContentForShow 
//	 */
//	public static void showATempChatMsgNotivication(Context context
//			, TempChatMsgDTO tcmd)
//	{
//		// 自kchat2.2(20140212)后，此字段将用于消息内容的显示
//		String messageContentForShow = ChatHelper.parseMessageForShow(context, tcmd.getMsg_content());
//		
//		NotificationHelperEx.addNotificaction(
//				NOTIFICATION_UNIQE_IDENT_ID_RECIEVED$TEMP$MESSAGE, context
//				, IntentFactory.createTempChatIntent(context
//						, tcmd.getUser_uid(), tcmd.getNickName(), tcmd.getUser_mail())//new Intent()//IntentFactory.createPortalIntent(context)
//				, R.drawable.main_alarms_tenpchat_message_icon
//				, context.getResources().getString(R.string.notification_temp_text_chatting_message_ticker_text)
//				, context.getResources().getString(R.string.notification_temp_text_chatting_message_info_title)
////				, tcmd.getNickName()+" said:"+tcmd.getMsg_content()
//				, MessageFormat.format(context.getResources().getString(R.string.notification_temp_text_chatting_message_info_text)
//						, tcmd.getNickName()
//						, messageContentForShow)//tcmd.getMsg_content())
//				, true, true);
//	}
//	
//	/**
//	 * 收到一个BBS聊天消息哦.
//	 * <p>目前此Notivication里只处理了“普通文本消息”哦！！！！！！！！
//	 * 
//	 * @param context
//	 * @param tcmd BBS聊天消息数据封装对象（自kchat2.2(20140212)后，msg_content存放的不再是
//	 * 简单文本消息内容而是TextMessage对象的JSON文本，如果读取聊天文本消息则需要进行JSON解析哦）
//	 * @param messageContentForShow 
//	 */
//	public static void showABBSChatMsgNotivication(Context context
//			, TempChatMsgDTO tcmd)
//	{
//		// 自kchat2.2(20140212)后，此字段将用于消息内容的显示
//		String messageContentForShow = ChatHelper.parseMessageForShow(context, tcmd.getMsg_content());
//		
//		NotificationHelperEx.addNotificaction(
//				NOTIFICATION_UNIQE_IDENT_ID_RECIEVED$BBS$MESSAGE, context
//				, new Intent(context, BBSChatActivity.class)
//				, R.drawable.main_alarms_bbschat_message_icon
//				, context.getResources().getString(R.string.notification_bbs_text_chatting_message_ticker_text)
//				, context.getResources().getString(R.string.notification_bbs_text_chatting_message_info_title)
////				, tcmd.getNickName()+" said:"+tcmd.getMsg_content()
//				, MessageFormat.format(context.getResources().getString(R.string.notification_bbs_text_chatting_message_info_text)
//						, tcmd.getNickName()
//						, messageContentForShow)//tcmd.getMsg_content())
//				, true, true);
//	}
//	
//	/**
//	 * 【收到实时语音请求处理方式3】相关处理界面处于后台时接收实时语音聊天请求时的提示（来自发起人A）.
//	 * 
//	 * @param context
//	 * @param friendNickName 昵称
//	 */
//	public static void showRealTimeVoiceRequestNotivication(Context context
//			, String friendUID, String friendNickName)
//	{
//		NotificationHelperEx.addNotificaction(
//				NOTIFICATION_UNIQE_IDENT_ID_REAL$TIME$VOICE$CHAT$REQUEST, context
//				, IntentFactory.createChatIntent(context, friendUID, true, System.currentTimeMillis())
//				, R.drawable.main_alarms_chat_opr_realtime_voice_icon
//				, MessageFormat.format(context.getResources().getString(R.string.notification_real_time_chat_request_ticker_text), friendNickName)
//				, context.getResources().getString(R.string.notification_real_time_chat_request_info_title)
//				, MessageFormat.format(context.getResources().getString(R.string.notification_real_time_chat_request_info_text), friendNickName)
//				, true, true);
//	}
	
	
	//---------------------------------------------------------------------------------------------
	/**
	 * 尝试清除本除程序产生的所有Notification.这通常需要在APP正常退或者崩溃时调用之.
	 * <p>
	 * 为何要清除？因为当APP退出后，如果还遗留这些东西，则再点击它时必然会产生崩溃，因为
	 * APp都退出了，所有的数据等都不存在了！
	 */
	public static void cancalAllNotification(Context context)
	{
		NotificationManager notiManager =  (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE); 
		if(notiManager != null)
		{
////			notiManager.cancel(NOTIFICATION_UNIQE_IDENT_ID_APP);
//			notiManager.cancel(NOTIFICATION_UNIQE_IDENT_ID_ADD$FRIEND$REQUEST);
//			notiManager.cancel(NOTIFICATION_UNIQE_IDENT_ID_ADD$FRIEND$REQUEST_RESPONSE$FOR$ERROR_SERVER$TO$A);
//			notiManager.cancel(NOTIFICATION_UNIQE_IDENT_ID_NEW$FRIEND$ADD$SUCESS);
//			notiManager.cancel(NOTIFICATION_UNIQE_IDENT_ID_ADD$FRIEND$BE$REJECT);
//			notiManager.cancel(NOTIFICATION_UNIQE_IDENT_ID_VOICE$VIDEO$CHAT$REQUEST);
//			notiManager.cancel(NOTIFICATION_UNIQE_IDENT_ID_RECIEVED$FRIEND$SCENSE$CMD);
//			notiManager.cancel(NOTIFICATION_UNIQE_IDENT_ID_RECIEVED$FRIEND$MESSAGE);
//			notiManager.cancel(NOTIFICATION_UNIQE_IDENT_ID_REAL$TIME$VOICE$CHAT$REQUEST);
//			notiManager.cancel(NOTIFICATION_UNIQE_IDENT_ID_RECIEVED$TEMP$MESSAGE);
//			notiManager.cancel(NOTIFICATION_UNIQE_IDENT_ID_RECIEVED$BBS$MESSAGE);
//			
////			notiManager.cancel(R.string.ident_genius_services_started);//
		}
	}
}
