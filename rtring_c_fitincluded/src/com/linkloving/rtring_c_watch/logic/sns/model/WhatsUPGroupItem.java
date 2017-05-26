package com.linkloving.rtring_c_watch.logic.sns.model;

import java.util.List;

public class WhatsUPGroupItem 
{
	private String groupName;
	private String groupName2;
	
	private List<WhatsUpChildItem> childList;
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public List<WhatsUpChildItem> getChildList() {
		return childList;
	}
	public void setChildList(List<WhatsUpChildItem> childList) {
		this.childList = childList;
	}
	public String getGroupName2() {
		return groupName2;
	}
	public void setGroupName2(String groupName2) {
		this.groupName2 = groupName2;
	}
}
