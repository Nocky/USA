package com.linkloving.rtring_c_watch.utils;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.linkloving.rtring_c_watch.logic.launch.LoginActivity;

/**
 * @author Jason
 * 
 */
public class BusinessIntelligence
{

	public BusinessIntelligence()
	{

	}

	public BusinessIntelligence(Context context)
	{
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		appVersionCode = LoginActivity.getAPKVersionCode(context);
		this.model = Build.MODEL;
		this.device = Build.DEVICE;
		this.product = Build.PRODUCT;
		this.manufacturer = Build.MANUFACTURER;
		this.display = Build.DISPLAY;
		this.sdk = Build.VERSION.SDK;
		this.frimware = Build.VERSION.RELEASE;
		this.callState = tm.getCallState();
		// this.cellLocation = tm.getCellLocation();
		this.deviceId = tm.getDeviceId();
		this.deviceSoftwareVersion = tm.getDeviceSoftwareVersion();
		this.iccCard = tm.hasIccCard();
		// this.line1Number = tm.getLine1Number();
		// this.neighboringCellInfo = tm.getNeighboringCellInfo();
		this.networkCountryIso = tm.getNetworkCountryIso();
		this.networkOperator = tm.getNetworkOperator();
		this.networkOperatorName = tm.getNetworkOperatorName();
		this.networkRoaming = tm.isNetworkRoaming();
		this.networkType = tm.getNetworkType();
		this.phoneType = tm.getPhoneType();
		this.simCountryIso = tm.getSimCountryIso();
		this.simOperator = tm.getSimOperator();
		this.simOperatorName = tm.getSimOperatorName();
		this.simSerialNumber = tm.getSimSerialNumber();
		this.simState = tm.getSimState();
		this.subscriberId = tm.getSubscriberId();
		// this.voiceMailAlphaTag = tm.getVoiceMailAlphaTag();
		// this.voiceMailNumber = tm.getVoiceMailNumber();
	}

	/*
	 * 版本号
	 */
	private int appVersionCode;

	/*
	 * 手机型号
	 */
	private String model;

	/*
	 * 
	 * SDK版本
	 */
	private String sdk;

	/*
	 * 
	 * frimware版本号(系统版本号)
	 */
	private String frimware;
	/*
	 * 电话状态： 1.tm.CALL_STATE_IDLE=0 无活动 2.tm.CALL_STATE_RINGING=1 响铃
	 * 3.tm.CALL_STATE_OFFHOOK=2 摘机
	 */
	private int callState;

	// /*
	// * 电话方位：
	// */
	// private CellLocation cellLocation;

	/*
	 * 唯一的设备ID： GSM手机的 IMEI 和 CDMA手机的 MEID. Return null if device ID is not
	 * available.
	 */

	private String deviceId;

	/*
	 * 设备的软件版本号： 例如：the IMEI/SV(software version) for GSM phones. Return null if
	 * the software version is not available.
	 */
	private String deviceSoftwareVersion;

	/*
	 * 手机号： GSM手机的 MSISDN. Return null if it is unavailable.
	 */
	private String line1Number;

	// /*
	// * 附近的电话的信息: 类型：List<NeighboringCellInfo>
	// * 需要权限：android.Manifest.permission#ACCESS_COARSE_UPDATES
	// */
	// private List<NeighboringCellInfo> neighboringCellInfo;

	/*
	 * 获取ISO标准的国家码，即国际长途区号。 注意：仅当用户已在网络注册后有效。 在CDMA网络中结果也许不可靠。
	 */
	private String networkCountryIso;

	/*
	 * MCC+MNC(mobile country code + mobile network code) 注意：仅当用户已在网络注册时有效。
	 * 在CDMA网络中结果也许不可靠。
	 */
	private String networkOperator;

	/*
	 * 按照字母次序的current registered operator(当前已注册的用户)的名字 注意：仅当用户已在网络注册时有效。
	 * 在CDMA网络中结果也许不可靠。
	 */
	private String networkOperatorName;

	/*
	 * 当前使用的网络类型： 例如： NETWORK_TYPE_UNKNOWN 网络类型未知 0 NETWORK_TYPE_GPRS GPRS网络 1
	 * NETWORK_TYPE_EDGE EDGE网络 2 NETWORK_TYPE_UMTS UMTS网络 3 NETWORK_TYPE_HSDPA
	 * HSDPA网络 8 NETWORK_TYPE_HSUPA HSUPA网络 9 NETWORK_TYPE_HSPA HSPA网络 10
	 * NETWORK_TYPE_CDMA CDMA网络,IS95A 或 IS95B. 4 NETWORK_TYPE_EVDO_0 EVDO网络,
	 * revision 0. 5 NETWORK_TYPE_EVDO_A EVDO网络, revision A. 6
	 * NETWORK_TYPE_1xRTT 1xRTT网络 7
	 */
	private int networkType;

	/*
	 * 手机类型： 例如： PHONE_TYPE_NONE 无信号 PHONE_TYPE_GSM GSM信号 PHONE_TYPE_CDMA CDMA信号
	 */
	private int phoneType;

	/*
	 * Returns the ISO country code equivalent for the SIM provider's country
	 * code. 获取ISO国家码，相当于提供SIM卡的国家码。
	 */
	private String simCountryIso;

	/*
	 * Returns the MCC+MNC (mobile country code + mobile network code) of the
	 * provider of the SIM. 5 or 6 decimal digits.
	 * 获取SIM卡提供的移动国家码和移动网络码.5或6位的十进制数字. SIM卡的状态必须是
	 * SIM_STATE_READY(使用getSimState()判断).
	 */
	private String simOperator;

	/*
	 * 服务商名称： 例如：中国移动、联通 SIM卡的状态必须是 SIM_STATE_READY(使用getSimState()判断).
	 */
	private String simOperatorName;

	/*
	 * SIM卡的序列号： 需要权限：READ_PHONE_STATE
	 */
	private String simSerialNumber;

	/*
	 * SIM的状态信息： SIM_STATE_UNKNOWN 未知状态 0 SIM_STATE_ABSENT 没插卡 1
	 * SIM_STATE_PIN_REQUIRED 锁定状态，需要用户的PIN码解锁 2 SIM_STATE_PUK_REQUIRED
	 * 锁定状态，需要用户的PUK码解锁 3 SIM_STATE_NETWORK_LOCKED 锁定状态，需要网络的PIN码解锁 4
	 * SIM_STATE_READY 就绪状态 5
	 */
	private int simState;

	/*
	 * 唯一的用户ID： 例如：IMSI(国际移动用户识别码) for a GSM phone. 需要权限：READ_PHONE_STATE
	 */
	private String subscriberId;

	/*
	 * 取得和语音邮件相关的标签，即为识别符 需要权限：READ_PHONE_STATE
	 */
	private String voiceMailAlphaTag;

	/*
	 * 获取语音邮件号码： 需要权限：READ_PHONE_STATE
	 */
	private String voiceMailNumber;

	/*
	 * ICC卡是否存在
	 */
	private boolean iccCard;

	/*
	 * 是否漫游: (在GSM用途下)
	 */
	private boolean networkRoaming;

	//
	private String device;
	private String product;
	private String manufacturer;
	private String display;

	public int getAppVersionCode()
	{
		return appVersionCode;
	}

	public void setAppVersionCode(int appVersionCode)
	{
		this.appVersionCode = appVersionCode;
	}

	public String getModel()
	{
		return model;
	}

	public void setModel(String model)
	{
		this.model = model;
	}

	public String getSdk()
	{
		return sdk;
	}

	public void setSdk(String sdk)
	{
		this.sdk = sdk;
	}

	public String getFrimware()
	{
		return frimware;
	}

	public void setFrimware(String frimware)
	{
		this.frimware = frimware;
	}

	public int getCallState()
	{
		return callState;
	}

	public void setCallState(int callState)
	{
		this.callState = callState;
	}

	// public CellLocation getCellLocation()
	// {
	// return cellLocation;
	// }
	//
	// public void setCellLocation(CellLocation cellLocation)
	// {
	// this.cellLocation = cellLocation;
	// }

	public String getDeviceId()
	{
		return deviceId;
	}

	public void setDeviceId(String deviceId)
	{
		this.deviceId = deviceId;
	}

	public String getDeviceSoftwareVersion()
	{
		return deviceSoftwareVersion;
	}

	public void setDeviceSoftwareVersion(String deviceSoftwareVersion)
	{
		this.deviceSoftwareVersion = deviceSoftwareVersion;
	}

	public String getLine1Number()
	{
		return line1Number;
	}

	public void setLine1Number(String line1Number)
	{
		this.line1Number = line1Number;
	}

	// public List<NeighboringCellInfo> getNeighboringCellInfo()
	// {
	// return neighboringCellInfo;
	// }
	//
	// public void setNeighboringCellInfo(List<NeighboringCellInfo>
	// neighboringCellInfo)
	// {
	// this.neighboringCellInfo = neighboringCellInfo;
	// }

	public String getNetworkCountryIso()
	{
		return networkCountryIso;
	}

	public void setNetworkCountryIso(String networkCountryIso)
	{
		this.networkCountryIso = networkCountryIso;
	}

	public String getNetworkOperator()
	{
		return networkOperator;
	}

	public void setNetworkOperator(String networkOperator)
	{
		this.networkOperator = networkOperator;
	}

	public String getNetworkOperatorName()
	{
		return networkOperatorName;
	}

	public void setNetworkOperatorName(String networkOperatorName)
	{
		this.networkOperatorName = networkOperatorName;
	}

	public int getNetworkType()
	{
		return networkType;
	}

	public void setNetworkType(int networkType)
	{
		this.networkType = networkType;
	}

	public int getPhoneType()
	{
		return phoneType;
	}

	public void setPhoneType(int phoneType)
	{
		this.phoneType = phoneType;
	}

	public String getSimCountryIso()
	{
		return simCountryIso;
	}

	public void setSimCountryIso(String simCountryIso)
	{
		this.simCountryIso = simCountryIso;
	}

	public String getSimOperator()
	{
		return simOperator;
	}

	public void setSimOperator(String simOperator)
	{
		this.simOperator = simOperator;
	}

	public String getSimOperatorName()
	{
		return simOperatorName;
	}

	public void setSimOperatorName(String simOperatorName)
	{
		this.simOperatorName = simOperatorName;
	}

	public String getSimSerialNumber()
	{
		return simSerialNumber;
	}

	public void setSimSerialNumber(String simSerialNumber)
	{
		this.simSerialNumber = simSerialNumber;
	}

	public int getSimState()
	{
		return simState;
	}

	public void setSimState(int simState)
	{
		this.simState = simState;
	}

	public String getSubscriberId()
	{
		return subscriberId;
	}

	public void setSubscriberId(String subscriberId)
	{
		this.subscriberId = subscriberId;
	}

	public String getVoiceMailAlphaTag()
	{
		return voiceMailAlphaTag;
	}

	public void setVoiceMailAlphaTag(String voiceMailAlphaTag)
	{
		this.voiceMailAlphaTag = voiceMailAlphaTag;
	}

	public String getVoiceMailNumber()
	{
		return voiceMailNumber;
	}

	public void setVoiceMailNumber(String voiceMailNumber)
	{
		this.voiceMailNumber = voiceMailNumber;
	}

	public boolean hasIccCard()
	{
		return iccCard;
	}

	public void setIccCard(boolean iccCard)
	{
		this.iccCard = iccCard;
	}

	public boolean isNetworkRoaming()
	{
		return networkRoaming;
	}

	public void setNetworkRoaming(boolean networkRoaming)
	{
		this.networkRoaming = networkRoaming;
	}

	public String getDevice()
	{
		return device;
	}

	public void setDevice(String device)
	{
		this.device = device;
	}

	public String getProduct()
	{
		return product;
	}

	public void setProduct(String product)
	{
		this.product = product;
	}

	public String getManufacturer()
	{
		return manufacturer;
	}

	public void setManufacturer(String manufacturer)
	{
		this.manufacturer = manufacturer;
	}

	public String getDisplay()
	{
		return display;
	}

	public void setDisplay(String display)
	{
		this.display = display;
	}

}
