package com.beansight.android.models;

import java.util.Date;


public class InsightListItem {
	
	private String id;
	private String content;
	// these dates should be Date objects, but it's hard to deserialize from long with Gson
	private long creationDate;
	private long endDate;
	private String creator;	
	private int category;
	private int agreeCount;
	private int disagreeCount;
	private int commentCount;
	private String lastCurrentUserVote;
	
	public String getId() {
		return id;
	}
	public void setId(String uniqueId) {
		this.id = uniqueId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Date getCreationDate() {
		return new Date(creationDate);
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate.getTime();
	}
	public Date getEndDate() {
		return new Date(endDate);
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate.getTime();
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public int getCategory() {
		return category;
	}
	public void setCategory(int category) {
		this.category = category;
	}
	public int getAgreeCount() {
		return agreeCount;
	}
	public void setAgreeCount(int agreeCount) {
		this.agreeCount = agreeCount;
	}
	public int getDisagreeCount() {
		return disagreeCount;
	}
	public void setDisagreeCount(int disagreeCount) {
		this.disagreeCount = disagreeCount;
	}
	public int getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}
	public String getLastCurrentUserVote() {
		return lastCurrentUserVote;
	}
	public void setLastCurrentUserVote(String lastCurrentUserVote) {
		this.lastCurrentUserVote = lastCurrentUserVote;
	}
	/** Return the date + content */
	public String getInsightText() {
		return "On " + getEndDate().toLocaleString() + ", " + getContent(); 
	}
	
}
