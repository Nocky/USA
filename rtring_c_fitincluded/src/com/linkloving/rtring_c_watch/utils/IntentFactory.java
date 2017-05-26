package com.linkloving.rtring_c_watch.utils;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.eva.epc.common.util.CommonUtils;
import com.linkloving.rtring_c_watch.logic.launch.ForgetPassWordActivity;
import com.linkloving.rtring_c_watch.logic.launch.LoginActivity;
import com.linkloving.rtring_c_watch.logic.launch.RegisterActivity;
import com.linkloving.rtring_c_watch.logic.launch.RegisterSuccessActivity;
import com.linkloving.rtring_c_watch.logic.main.BLEListActivity;
import com.linkloving.rtring_c_watch.logic.main.BoundStep1Activity;
import com.linkloving.rtring_c_watch.logic.main.BundTypeActivity;
import com.linkloving.rtring_c_watch.logic.main.MainPageActivity;
import com.linkloving.rtring_c_watch.logic.main.OwnBraceletActivity;
import com.linkloving.rtring_c_watch.logic.main.PortalActivity;
import com.linkloving.rtring_c_watch.logic.main.SettingWatchActivity;
import com.linkloving.rtring_c_watch.logic.more.CommonWebActivity;
import com.linkloving.rtring_c_watch.logic.more.HelpActivity;
import com.linkloving.rtring_c_watch.logic.more.SkinActivity;
import com.linkloving.rtring_c_watch.logic.report.RankingActivity;
import com.linkloving.rtring_c_watch.logic.report.ReportActivity;
import com.linkloving.rtring_c_watch.logic.reportday.SportDataDetailActivity;
import com.linkloving.rtring_c_watch.logic.setup.AlarmActivity;
import com.linkloving.rtring_c_watch.logic.setup.BodyActivity;
import com.linkloving.rtring_c_watch.logic.setup.GoalActivity;
import com.linkloving.rtring_c_watch.logic.setup.HandUpActivity;
import com.linkloving.rtring_c_watch.logic.setup.LongSitActivity;
import com.linkloving.rtring_c_watch.logic.setup.MovingTargetActivity;
import com.linkloving.rtring_c_watch.logic.setup.NotifacitionActivity;
import com.linkloving.rtring_c_watch.logic.setup.PowerActivity;
import com.linkloving.rtring_c_watch.logic.sns.RelationshipGroupActivity;
import com.linkloving.rtring_c_watch.logic.sns.SearchActivity;
import com.linkloving.rtring_c_watch.logic.sns.UserDetialActivity;
import com.linkloving.rtring_c_watch.logic.sns.WhatsUpDetailActivity;
import com.linkloving.rtring_c_watch.logic.sns.WhatsUpHistoryActivity;
import com.linkloving.rtring_c_watch.logic.sns.model.WhatsUpItem;
import com.rtring.buiness.logic.dto.UserEntity;
import com.rtring.buiness.logic.dto.UserRegisterDTO;

public class IntentFactory
{
	/**
	 * 通用WebActivity
	 * @param thisActivity
	 * @param url
	 * @return
	 */
	public static Intent createSportDataDetailActivityIntent(Activity thisActivity, long datetime)
	{
		Intent intent = new Intent(thisActivity, SportDataDetailActivity.class);
		intent.putExtra("__datetime__", datetime);
		return intent;
	}
	public static long parseSportDataDetailActivity(Intent intent)
	{
		return intent.getLongExtra("__datetime__", -1); //若没取到值 则取-1
	}

	/**
	 * 通用WebActivity
	 * @param thisActivity
	 * @param url
	 * @return
	 */
	public static Intent createCommonWebActivityIntent(Activity thisActivity, String url)
	{
		Intent intent = new Intent(thisActivity, CommonWebActivity.class);
		intent.putExtra("__url__", url);
		return intent;
	}
	public static String parseCommonWebIntent(Intent intent)
	{
		return intent.getStringExtra("__url__");
	}
	private final static int REQUEST_CODE_BOUND = 1;
	
    public static void startBundTypeActivity(Activity activity) {
        Intent intent = new Intent(activity, BundTypeActivity.class);
        activity.startActivityForResult(intent,REQUEST_CODE_BOUND);
    }
	
	/**
	 * 打开ReportActivity的Intent构造方法.
	 * 
	 * @return
	 */
	public static Intent createReportActivityIntent(Context thisActivity)
	{
		Intent intent = new Intent(thisActivity, ReportActivity.class);
		return intent;
	}
	
	public static Intent createSkinActivityIntent(Activity thisActivity)
	{
		Intent intent = new Intent(thisActivity, SkinActivity.class);
		return intent;
	}
	
	/**
	 * 打开PortalActivity的Intent构造方法.
	 * 
	 * @return
	 */
	public static Intent createPortalActivityIntent(Context thisActivity)
	{
		Intent intent = new Intent(thisActivity, PortalActivity.class);
		return intent;
	}
	
	public static Intent createBoundActivity(Context thisActivity)
	{
		Intent intent = new Intent(thisActivity, BoundStep1Activity.class);
		return intent;
	}
	
	public static Intent createBleListActivity(Context thisActivity)
	{
		Intent intent = new Intent(thisActivity, BLEListActivity.class);
		return intent;
	}
	/**
	 * 打开MainPageActivity的Intent构造方法.
	 * 
	 * @param thisActivity
	 * @param loginUidOrEmail
	 * @param loginPassword
	 * @return
	 */
	public static Intent createMainPageActivityIntent(Context thisActivity)
	{
		Intent intent = new Intent(thisActivity, MainPageActivity.class);
		return intent;
	}
	
	/**
	 * 打开ForgetPassWordActivity的Intent构造方法.
	 * 
	 * @param thisActivity
	 * @param loginUidOrEmail
	 * @param loginPassword
	 * @return
	 */
	public static Intent createForgetPassWordIntent(Context thisActivity)
	{
		Intent intent = new Intent(thisActivity, ForgetPassWordActivity.class);
		return intent;
	}
	
	/**
	 * 打开VideoCallComeActivity的Intent构造方法.
	 * 
	 * @param thisActivity
	 * @param friendUIDForInit
	 * @return
	 */
	public static Intent createHelpActivityIntent(Context thisActivity
			, int finishAction, boolean isJiaocheng)
	{
		Intent intent = new Intent(thisActivity, HelpActivity.class);
		intent.putExtra("finish_action", finishAction);
		intent.putExtra("isJiaocheng", isJiaocheng);
//		thisActivity.startActivity(intent);

		return intent;
	}
	/**
	 * 解析intent传过来给VideoCallComeActivity的数据.
	 * 
	 * @param intent
	 * @return
	 */
	public static ArrayList parseHelpActivityIntent(Intent intent)
	{
		ArrayList datas = new ArrayList();
		datas.add(intent.getIntExtra("finish_action", -1));
		datas.add(intent.getBooleanExtra("isJiaocheng", false));
		return datas;
	}
	
	/**
	 * 打开LoginActivity的Intent构造方法. 此方法通常用于无法普通地打开登陆界面的场景.
	 * 
	 * @param thisActivity
	 * @return
	 */
	public static Intent createLoginIntent(Activity thisActivity)
	{
		Intent intent = new Intent(thisActivity, LoginActivity.class);
		return intent;
	}
	/**
	 * 打开LoginActivity的Intent构造方法. 此方法目前用于注册成功后，自动把登陆名和密码传入登陆界面从而方便登陆的场景.
	 * 
	 * @param thisActivity
	 * @param loginUidOrEmail
	 * @param loginPassword
	 * @return
	 */
	public static Intent createLoginIntent(Activity thisActivity, String loginUidOrEmail, String loginPassword)
	{
		Intent intent = new Intent(thisActivity, LoginActivity.class);
		intent.putExtra("__loginUidOrEmail__", loginUidOrEmail);
		intent.putExtra("__loginPassword__", loginPassword);

		return intent;
	}
	/**
	 * 解析intent传过来给LoginActivity的数据.
	 * 
	 * @param intent
	 * @return
	 */
	public static ArrayList parseLoginFormIntent(Intent intent)
	{
		ArrayList datas = new ArrayList();
		datas.add(intent.getSerializableExtra("__loginUidOrEmail__"));
		datas.add(intent.getSerializableExtra("__loginPassword__"));
		return datas;
	}
	
	/**
	 * 打开RegisterActivity的Intent构造方法. 此方法通常用于无法普通地打开登陆界面的场景.
	 * 
	 * @param thisActivity
	 * @return
	 */
	public static Intent createRegisterIntent(Activity thisActivity)
	{
		Intent intent = new Intent(thisActivity, RegisterActivity.class);
		return intent;
	}
	/**
	 * 打开RegisterSuccess的Intent构造方法
	 * 
	 * @param thisActivity
	 * @param uid
	 * @return
	 */
	public static Intent createRegisterSuccessIntent(Activity thisActivity, UserRegisterDTO u)
	{
		Intent intent = new Intent(thisActivity, RegisterSuccessActivity.class);
		intent.putExtra("__UserRegisterDTO__", u);
		return intent;
	}
	/**
	 * 解析intent传过来的RegisterActivity数据
	 * 
	 * @param intent
	 * @return
	 */
	public static UserRegisterDTO parseRegisterSuccessIntent(Intent intent)
	{
		return (UserRegisterDTO) intent.getSerializableExtra("__UserRegisterDTO__");
	}
	
	/**
	 * 打开GoalActivity的Intent构造方法
	 */
	public static void startGoalActivityIntent(Activity thisActivity, UserEntity user, int from)
	{
		Intent intent = new Intent(thisActivity, MovingTargetActivity.class);
		
		double bmi = ToolKits.getBMI(CommonUtils.getFloatValue(user.getUser_weight()), CommonUtils.getIntValue(user.getUser_height()));
		
		intent.putExtra("user_sex", user.getUser_sex());
		intent.putExtra("user_BMI", bmi + "");
		intent.putExtra("user_BMIDesc", ToolKits.getBMIDesc(thisActivity, bmi));
		intent.putExtra("user_target", user.getPlay_calory());
		intent.putExtra("type", from);
		
		thisActivity.startActivity(intent);
	}
	
	
	/**
	 * 打开RankingActivity的Intent构造方法
	 */
	public static void startRankingActivityIntent(Activity thisActivity)
	{
		Intent intent = new Intent(thisActivity, RankingActivity.class);
		thisActivity.startActivity(intent);
	}
	/**
	 * 打开LongSitActivity的Intent构造方法
	 */
	public static void startLongSitActivityIntent(Activity thisActivity)
	{
		Intent intent = new Intent(thisActivity, LongSitActivity.class);
		thisActivity.startActivity(intent);
	}
	/**
	 * 打开GoalActivity的Intent构造方法
	 */
	public static void startGoalActivityIntent(Activity thisActivity)
	{
		Intent intent = new Intent(thisActivity, GoalActivity.class);
		thisActivity.startActivity(intent);
	}
	/**
	 * 打开HandUpActivity的Intent构造方法
	 */
	public static void startHandUpActivityIntent(Activity thisActivity)
	{
		Intent intent = new Intent(thisActivity, HandUpActivity.class);
		thisActivity.startActivity(intent);
	}
	/**
	 * 打开startPowerActivityIntent的Intent构造方法
	 */
	public static void startPowerActivityIntent(Activity thisActivity)
	{
		Intent intent = new Intent(thisActivity, PowerActivity.class);
		thisActivity.startActivity(intent);
	}
	/**
	 * 打开NotifacitionActivity的Intent构造方法
	 */
	public static void startNotifacitionActivityIntent(Activity thisActivity)
	{
		Intent intent = new Intent(thisActivity, NotifacitionActivity.class);
		thisActivity.startActivity(intent);
	}
	/**
	 * 打开AlarmActivity的Intent构造方法
	 */
	public static void startAlarmActivityIntent(Activity thisActivity)
	{
		Intent intent = new Intent(thisActivity, AlarmActivity.class);
		thisActivity.startActivity(intent);
	}

	
	/**
	 * 打开BodyActivity的Intent构造方法
	 * 
	 * @param thisActivity
	 * @param uid
	 * @return
	 */
	public static Intent createBodyActivityIntent(Activity thisActivity, UserEntity u, int type, String nickName)
	{
		Intent intent = new Intent(thisActivity, BodyActivity.class);
		intent.putExtra("__UserEntity__", u);
		intent.putExtra("__type__", type);
		intent.putExtra("__nickName__", nickName);
		return intent;
	}
	
	/**
	 * 
	 * @param intent
	 * @return
	 */
	public static ArrayList parseBodyActivityIntent(Intent intent)
	{
		ArrayList datas = new ArrayList();
		datas.add(intent.getSerializableExtra("__UserEntity__"));
		datas.add(intent.getIntExtra("__type__", 0));
		datas.add(intent.getStringExtra("__nickName__"));
		return datas;
	}
	
	public static Intent createSearchActivity(Context thisActivity, int function)
	{
		Intent intent = new Intent(thisActivity, SearchActivity.class);
		intent.putExtra("__function__", function);
		return intent;
	}
	
	
	public static Intent createRelationshipGroupActivityIntent(Activity thisActivity)
	{
		Intent intent  = new Intent(thisActivity, RelationshipGroupActivity.class);
		return intent;
	}
	public static Intent createSettingWatchActivityIntent(Activity thisActivity)
	{
		Intent intent  = new Intent(thisActivity, SettingWatchActivity.class);
		return intent;
	}
	
	public static Intent createUserDetialActivityIntent(Activity thisActivity,String userID)
	{
		Intent intent = new Intent(thisActivity, UserDetialActivity.class);
		intent.putExtra("__user_id__", userID);
		return intent;
	}
	
	public static Intent createUserDetialActivityIntent(Activity thisActivity,String userID,String time)
	{
		Intent intent = new Intent(thisActivity, UserDetialActivity.class);
		intent.putExtra("__user_id__", userID);
		intent.putExtra("__time__", time);
		return intent;
	}
	
	public static String[] parseUserDetialActivityIntent(Intent intent)
	{
		String[] tmp = new String[2];
		tmp[0] =  intent.getExtras().getString("__user_id__", "");
		tmp[1] =  intent.getExtras().getString("__time__", "");
		return tmp;
	}
	
	public static Intent createWhatsUpHistoryActivityIntent(Activity thisActivity,String userID)
	{
		Intent intent = new Intent(thisActivity, WhatsUpHistoryActivity.class);
		intent.putExtra("__user_id__", userID);
		return intent;
	}
	
	public static String parseWhatsUpHistoryActivity(Intent intent)
	{
		return intent.getExtras().getString("__user_id__");
	}
	
	public static Intent createWhatsUpDetailActivity(Context thisActivity,WhatsUpItem item,int pos)
	{
		Intent intent = new Intent(thisActivity, WhatsUpDetailActivity.class);
		intent.putExtra("__whats_up__", item);
		intent.putExtra("pos", pos);
		return intent;
	}
	
	public static Object[] parseWhatsUpDetailActivity(Intent intent)
	{
		Object[] objs = new Object[2];
		objs[0] =  (WhatsUpItem) intent.getExtras().getSerializable("__whats_up__");
		objs[1] = intent.getExtras().getInt("pos");
		return objs;
	}
	
	public static int parseSearchActivity(Intent intent)
	{
		return intent.getIntExtra("__function__", SearchActivity.USE_TO_UNKONW);
	}
	
	/**
	 * 打开OwnBraceletActivity的Intent构造方法
	 * 
	 * @return
	 */
	public static Intent createOwnBraceletActivityIntent(Activity thisActivity)
	{
		Intent intent = new Intent(thisActivity, OwnBraceletActivity.class);
		intent.putExtra("__type__", 111);
		return intent;
	}
	
	public static int getOwnBraceletActivityIntent(Intent intent)
	{
		return intent.getIntExtra("__type__",1);
	}
}
