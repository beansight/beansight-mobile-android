package com.beansight.android.models;

import java.util.ArrayList;
import java.util.List;

public class UserProfile {

	private String userName;
	private String description;
	private String avatarSmall;
	private String avatarMedium;
	private String avatarLarge;

	private String uiLanguage;
	private String writtingLanguage;
	private String secondWrittingLanguage;
	
	private int successfulPredictionsCount;
	private List<String[]> scores = new ArrayList<String[]>();
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAvatarSmall() {
		return avatarSmall;
	}
	public void setAvatarSmall(String avatarSmall) {
		this.avatarSmall = avatarSmall;
	}
	public String getAvatarMedium() {
		return avatarMedium;
	}
	public void setAvatarMedium(String avatarMedium) {
		this.avatarMedium = avatarMedium;
	}
	public String getAvatarLarge() {
		return avatarLarge;
	}
	public void setAvatarLarge(String avatarLarge) {
		this.avatarLarge = avatarLarge;
	}
	public String getUiLanguage() {
		return uiLanguage;
	}
	public void setUiLanguage(String uiLanguage) {
		this.uiLanguage = uiLanguage;
	}
	public String getWrittingLanguage() {
		return writtingLanguage;
	}
	public void setWrittingLanguage(String writtingLanguage) {
		this.writtingLanguage = writtingLanguage;
	}
	public String getSecondWrittingLanguage() {
		return secondWrittingLanguage;
	}
	public void setSecondWrittingLanguage(String secondWrittingLanguage) {
		this.secondWrittingLanguage = secondWrittingLanguage;
	}
	public int getSuccessfulPredictionsCount() {
		return successfulPredictionsCount;
	}
	public void setSuccessfulPredictionsCount(int successfulPredictionsCount) {
		this.successfulPredictionsCount = successfulPredictionsCount;
	}
	public List<String[]> getScores() {
		return scores;
	}
	public void setScores(List<String[]> scores) {
		this.scores = scores;
	}

}
