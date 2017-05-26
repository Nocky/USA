package com.linkloving.rtring_c_watch.logic.sns.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.linkloving.rtring_c_watch.utils.ToolKits;
import com.rtring.buiness.logic.dto.UserSignature;

/**
 * @author Administrator
 *
 */
public class WhatsUpItem implements Serializable
{
	private String nickName;
	private long stemp;
	private String sginTime;
	private String content;
	private int comments;
	private  String sign_id;
	private String user_id;
	private String user_avatar_file_name;
    private String comment_list;
	
	public String getDay()
	{
		return new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD).format(stemp);
	}
	
	public String getComment_list() {
		return comment_list;
	}

	public void setComment_list(String comment_list) {
		this.comment_list = comment_list;
	}
	public String getSginTime() {
		return sginTime;
	}
	public long getStemp() {
		return stemp;
	}
	public void setStemp(long stemp) {
		this.stemp = stemp;
	}
	public void setSginTime(String sginTime) {
		this.sginTime = sginTime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getComments() {
		return comments;
	}
	public void setComments(int comments) {
		this.comments = comments;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	public String getSign_id() {
		return sign_id;
	}

	public void setSign_id(String sign_id) {
		this.sign_id = sign_id;
	}
	
	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getUser_avatar_file_name() {
		return user_avatar_file_name;
	}

	public void setUser_avatar_file_name(String user_avatar_file_name) {
		this.user_avatar_file_name = user_avatar_file_name;
	}

	public WhatsUpItem(UserSignature userSignature) throws ParseException
	{
		this.nickName = userSignature.getNickname();
		this.comments = Integer.parseInt(userSignature.getComment_count());
		this.content = userSignature.getSign_content();
		this.stemp = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).parse(userSignature.getSign_time_utc()).getTime() +TimeZone.getDefault().getRawOffset();
		this.sginTime =  new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).format(stemp);
		this.sign_id = userSignature.getSign_id();
		this.user_avatar_file_name = userSignature.getUser_avatar_file_name();
		this.user_id = userSignature.getUser_id();
		this.comment_list = userSignature.getComment_list();
	}
	
	public WhatsUpItem( String nickName, long stemp,String content,String sign_id,String user_id,String user_avatar_file_name)
	{
		this.sign_id = sign_id;
		this.nickName = nickName;
		this.stemp = stemp;
		this.sginTime = new SimpleDateFormat(ToolKits.DATE_FORMAT_YYYY_MM_DD_HH_MM_SS).format(stemp);
		this.content = content;
		this.user_id = user_id;
		this.user_avatar_file_name = user_avatar_file_name;
	}
	
	

}
