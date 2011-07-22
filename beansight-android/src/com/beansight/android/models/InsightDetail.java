package com.beansight.android.models;

public class InsightDetail {

	private String id;
	private String content;
	private Long creationDate;
	private Long endDate;
	private String creator;
	private Long category;
	private Long agreeCount;
	private Long disagreeCount;
	private Long commentCount;
	private String lastCurrentUserVote;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Long creationDate) {
		this.creationDate = creationDate;
	}

	public Long getEndDate() {
		return endDate;
	}

	public void setEndDate(Long endDate) {
		this.endDate = endDate;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Long getCategory() {
		return category;
	}

	public void setCategory(Long category) {
		this.category = category;
	}

	public Long getAgreeCount() {
		return agreeCount;
	}

	public void setAgreeCount(Long agreeCount) {
		this.agreeCount = agreeCount;
	}

	public Long getDisagreeCount() {
		return disagreeCount;
	}

	public void setDisagreeCount(Long disagreeCount) {
		this.disagreeCount = disagreeCount;
	}

	public Long getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(Long commentCount) {
		this.commentCount = commentCount;
	}

	public String getLastCurrentUserVote() {
		return lastCurrentUserVote;
	}

	public void setLastCurrentUserVote(String lastCurrentUserVote) {
		this.lastCurrentUserVote = lastCurrentUserVote;
	}

}
