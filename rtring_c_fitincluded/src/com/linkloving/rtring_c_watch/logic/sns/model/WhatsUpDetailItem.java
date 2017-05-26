package com.linkloving.rtring_c_watch.logic.sns.model;

import com.rtring.buiness.logic.dto.UserSignatureComment;

public class WhatsUpDetailItem 
{
	private String comNickName;
	private String replyNickName;
	private String comments;
	
	private String comment_id;
	private String comment_user_id;
	private String reply_user_id;

	
	public String getComNickName() {
		return comNickName;
	}
	public void setComNickName(String comNickName) {
		this.comNickName = comNickName;
	}
	public String getReplyNickName() {
		return replyNickName;
	}
	public void setReplyNickName(String replyNickName) {
		this.replyNickName = replyNickName;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	
	
	
	public String getComment_id() {
		return comment_id;
	}
	public void setComment_id(String comment_id) {
		this.comment_id = comment_id;
	}
	public String getComment_user_id() {
		return comment_user_id;
	}
	public void setComment_user_id(String comment_user_id) {
		this.comment_user_id = comment_user_id;
	}
	public String getReply_user_id() {
		return reply_user_id;
	}
	public void setReply_user_id(String reply_user_id) {
		this.reply_user_id = reply_user_id;
	}
	public WhatsUpDetailItem(UserSignatureComment comment)
	{
		this.comments = comment.getComment_content();
		this.comNickName = comment.getComment_nickname();
		this.replyNickName = comment.getReply_nickname();
		this.comment_id = comment.getComment_id();
	    this.comment_user_id = comment.getComment_user_id();
		this.reply_user_id = comment.getReply_user_id();
	}
	
	public WhatsUpDetailItem(String comNickName,String replyNickName,String comments,String comment_user_id)
	{
		this.comNickName = comNickName;
		this.replyNickName = replyNickName;
		this.comments = comments;
		this.comment_user_id = comment_user_id;
	}
}
