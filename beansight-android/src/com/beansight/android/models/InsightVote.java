package com.beansight.android.models;

public class InsightVote {
	private String id;
	private Long updatedAgreeCount;
	private Long updatedDisagreeCount;
	private String voteState;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Long getUpdatedAgreeCount() {
		return updatedAgreeCount;
	}
	public void setUpdatedAgreeCount(Long updatedAgreeCount) {
		this.updatedAgreeCount = updatedAgreeCount;
	}
	public Long getUpdatedDisagreeCount() {
		return updatedDisagreeCount;
	}
	public void setUpdatedDisagreeCount(Long updatedDisagreeCount) {
		this.updatedDisagreeCount = updatedDisagreeCount;
	}
	public String getVoteState() {
		return voteState;
	}
	public void setVoteState(String voteState) {
		this.voteState = voteState;
	}

}
