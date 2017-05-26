package com.linkloving.rtring_c_watch.logic.reportday.util;

import android.os.Environment;

public class Constant {

	// 所有Action定义在这里，请按规则统一命名
	/** 定位广播 */
	public static final String ACTION_LOCATION = "com.hoolai.magic.action.LOCATION";
	/** 单次运动定时器 */
	public static final String ACTION_TIMER = "com.hoolai.magic.action.TIMER";
	/** 单次运动目标达成 */
	public static final String ACTION_TARGET_REACHED = "com.hoolai.magic.action.TARGET_REACHED";
	/** 健身计划创建成功 */
	public static final String ACTION_SCHEDULE_CREATED = "com.hoolai.magic.action.SCHEDULE_CREATED";
	/** 从通知栏点击进入 */
	public static final String ACTION_SHOW_ALARM = "com.hoolai.magic.action.FROM_NOTIFICATION";
	/** 接收到推送消息 */
	public static final String ACTION_PUSH_MESSAGE = "com.hoolai.magic.action.PUSH_MESSAGE";
	/** 删除通知后发广播 */
	public static final String ACTION_DELETE_NOTIFICATION = "com.hoolai.magic.action.DELETE_NOTIFICATION";
	/** 用户头像和昵称更新广播 */
	// public static final String ACTION_AVATAR_AND_NICKNAME_UPDATED =
	// "com.hoolai.magic.action.AVATAR_AND_NICKNAME_UPDATED";
	/** 手环激活结果广播 */
	public static final String ACTION_BRACELET_ACTIVATE_RESULT = "com.hoolai.magic.action.BRACELET_ACTIVATE_RESULT";

	public static final double SPORT_PATTERN_DISTANCE_START = 0.5;
	public static final double SPORT_PATTERN_DISTANCE_END = 50;
	public static final double SPORT_PATTERN_DISTANCE_STEP = 0.5;

	public static final int SPORT_PATTERN_TIME_START = 5;
	public static final int SPORT_PATTERN_TIME_END = 300;
	public static final int SPORT_PATTERN_TIME_STEP = 5;

	public static final String UPDATE_SAVENAME = "updateapkmagic.apk";

	/** 一天有的时间片个数， 时间片长度为30秒 */
	public static int TICKSPERDAY = 2880;
	/** 一小时有的时间片个数， 时间片长度为30秒 */
	public static int TICKSPERHOUR = 120;

	/** 一个自然日的毫秒数 */
	public static final long ONE_DAY_MILLIS = 24 * 60 * 60 * 1000;
	/** 推迟一次的毫秒数 */
	public static final long DELAY_OFFSET_MILLIS = 2 * ONE_DAY_MILLIS;

	public static final int DAYS_PER_PERIOD = 7;

	public static final int SOURCE_PERSONAL_SPORT = 3;

	// 运动类型
	public static final int SPORT_TYPE_WAIKING = 1;
	public static final int SPORT_TYPE_RUNNING = 2;
	public static final int SPORT_TYPE_CYCLING = 3;
	public static final int SPORT_TYPE_SKIING = 4;
	public static final int SPORT_TYPE_SKATING = 5;
	public static final int SPORT_TYPE_CLIMBING = 6;
	public static final int SPORT_TYPE_BADMINTON = 7;

	// 运动模式
	public static final int SPORT_MODE_NORMAL = 1;
	public static final int SPORT_MODE_DISTANCE = 2;
	public static final int SPORT_MODE_TIMING = 3;

	/** 3天未运动提醒ID */
	public static final int PENDING_NOTIFICATION_ID = 3;

	/** 反馈类型:私教反馈 */
	public static final int FEEDBACK_TYPE_PTPLAN = 0;
	/** 反馈类型:应用反馈 */
	public static final int FEEDBACK_TYPE_APP = 1;

	/** 私教计划延期一天最多3次 */
	public static final int SCHEDULE_DELAY_1DAY_MAX_TIMES = 3;
	/** 私教计划延期三十分钟最多1次 */
	public static final int SCHEDULE_DELAY_30MINS_TIMES = 1;

	/** 位于SD卡上的应用数据文件目录，用于存储应用用到的各种数据以及资源文件 */
	public static final String DATA_DIR = "/hoolai_data";
	/** 应用在SD卡上的数据文件存储目录绝对路径 */
	public static final String BASE_DATA_DIR = Environment.getExternalStorageDirectory().toString() + DATA_DIR;

	public static final String FILENAME_PROGRAMME_LIST = "programmeList.data";
	public static final String FILENAME_HOBBY_LIST = "hobbyList.data";
	public static final String FILENAME_NATIONAL_STANDARD_PHONE_LIST = "nationalStandardPhoneList.data";
	public static final String FILENAME_OFFLINE_PACKAGE_LIST = "offlinePackageList.data";
	public static final String PATH_PROGRAMMELIST = BASE_DATA_DIR + "/" + FILENAME_PROGRAMME_LIST;
	public static final String PATH_HOBBYLIST = BASE_DATA_DIR + "/" + FILENAME_HOBBY_LIST;
	// 图片文件夹
	public static final String PATH_IMAGE_DIR = BASE_DATA_DIR + "/magic/images";
	// 分享截图路径
	public static final String PATH_IMAGE_HEADIMAGE = PATH_IMAGE_DIR + "/headimage.png";
	// 头像路径
	public static final String PATH_IMAGE_SCREENSHOT = PATH_IMAGE_DIR + "/screenshot.png";

	// 体测中心+每日运动目标
	// 卡路里常量
	public static final int SCALE_COUNT = 7;
	public static final int[][] CALORIE_SCALE = { //
	{ 50, 100, 150, 200, 250, 300, 350 },// 0-12,60+
			{ 150, 200, 250, 300, 350, 400, 450 },// 13-19,41-59
			{ 250, 300, 350, 400, 450, 500, 550 } };// 20-40
	public static final int[][] STEP_SCALE = { //
	{ 2000, 4000, 6000, 8000, 10000, 12000, 14000 },// 0-12,60+
			{ 4000, 6000, 8000, 10000, 12000, 14000, 16000 },// 13-19,41-59
			{ 6000, 8000, 10000, 12000, 14000, 16000, 18000 } };// 20-40

	/** 久坐提醒LEVEL固定值 */
	public static final int BRACELET_REMIND_LEVEL = 100;

	/** 本地白名单 */
	/** 测试按钮测试出的白名单 */
	public static final int LOCAL_WHITE_LEVEL_TESTER = 3;
	/** 自动识别出的白名单 */
	public static final int LOCAL_WHITE_LEVEL_AUTO = 1;
	/** 无本地白名单 */
	public static final int LOCAL_WHITE_LEVEL_NONE = -1;

	/** 排行榜最大数据量 */
	public static final int RANKING_MAX_COUNT = 1000;
}
