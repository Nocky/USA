/**
 * LocalSetting.java
 * @author Jason Lu
 * @date 2014-9-3
 * @version 1.0
 */
package com.linkloving.rtring_c_watch.logic.model;

/**
 * @author Lz
 * 该类所保存 为手机离现时，设置的闹钟，久坐提醒，运动目标。
 * 在下一次登录联网时，会根据user_mail来找到离线数据并提交同步至服务端。
 */
public class LocalSetting
{

	private String user_mail;

	private String goal;
	private long goal_update;

	private String alarm_list;
	private long alarm_update;

	private int long_sit;
	private int long_sit_step;
	private String long_sit_time;
	private long long_sit_update;

	private int handup;
	private String handup_time;
	private long handup_update;
	
	private int Ancs;
	private long Ancs_update;
	
	
	public int getAncs() {
		return Ancs;
	}

	public void setAncs(int ancs) {
		Ancs = ancs;
	}

	public long getAncs_update() {
		return Ancs_update;
	}

	public void setAncs_update(long ancs_update) {
		Ancs_update = ancs_update;
	}

	public int getHandup() {
		return handup;
	}

	public void setHandup(int handup) {
		this.handup = handup;
	}

	public String getHandup_time() {
		return handup_time;
	}

	public void setHandup_time(String handup_time) {
		this.handup_time = handup_time;
	}

	public long getHandup_update() {
		return handup_update;
	}

	public void setHandup_update(long handup_update) {
		this.handup_update = handup_update;
	}

	public String getUser_mail()
	{
		return user_mail;
	}

	public void setUser_mail(String user_mail)
	{
		this.user_mail = user_mail;
	}

	public String getGoal()
	{
		return goal;
	}

	public void setGoal(String goal)
	{
		this.goal = goal;
	}

	public long getGoal_update()
	{
		return goal_update;
	}

	public void setGoal_update(long goal_update)
	{
		this.goal_update = goal_update;
	}

	public int getLong_sit()
	{
		return long_sit;
	}

	public void setLong_sit(int long_sit)
	{
		this.long_sit = long_sit;
	}

	public String getLong_sit_time()
	{
		return long_sit_time;
	}

	public void setLong_sit_time(String long_sit_time)
	{
		this.long_sit_time = long_sit_time;
	}

	public int getLong_sit_step()
	{
		return long_sit_step;
	}

	public void setLong_sit_step(int long_sit_step)
	{
		this.long_sit_step = long_sit_step;
	}

	public long getLong_sit_update()
	{
		return long_sit_update;
	}

	public void setLong_sit_update(long long_sit_update)
	{
		this.long_sit_update = long_sit_update;
	}

	public String getAlarm_list()
	{
		return alarm_list;
	}

	public void setAlarm_list(String alarm_list)
	{
		this.alarm_list = alarm_list;
	}

	public long getAlarm_update()
	{
		return alarm_update;
	}

	public void setAlarm_update(long alarm_update)
	{
		this.alarm_update = alarm_update;
	}

}
