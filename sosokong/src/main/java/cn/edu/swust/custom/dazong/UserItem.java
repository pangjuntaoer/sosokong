package cn.edu.swust.custom.dazong;

import java.util.Date;

public class UserItem {
private String userId;
private String ItemId;

private float rating;
private float tast;
private float environment;
private float service;
private String times;
private String review;
private String userName;
private float userRank;
private String userPic;
private String recommend="";
public String toString(){
	StringBuffer bf = new StringBuffer();
	bf.append("userId:").append(this.userId)
		.append("Itemid:").append(this.ItemId)
		.append("rating:").append(this.rating)
		.append("tast:").append(this.tast)
		.append("environment:").append(this.environment)
		.append("service:").append(this.service)
		.append("times:").append(this.times)
		.append("review:").append(this.review);
	return bf.toString();
}
public String getUserId() {
	return userId;
}
public void setUserId(String userId) {
	this.userId = userId;
}
public String getItemId() {
	return ItemId;
}
public void setItemId(String itemId) {
	ItemId = itemId;
}

public float getRating() {
	return rating;
}
public void setRating(float rating) {
	this.rating = rating;
}
public float getTast() {
	return tast;
}
public void setTast(float tast) {
	this.tast = tast;
}
public float getEnvironment() {
	return environment;
}
public void setEnvironment(float environment) {
	this.environment = environment;
}
public float getService() {
	return service;
}
public void setService(float service) {
	this.service = service;
}
public String getTimes() {
	return times;
}
public void setTimes(String times) {
	this.times = times;
}
public String getReview() {
	return review;
}
public void setReview(String review) {
	this.review = review;
}
public String getUserPic() {
	return userPic;
}
public void setUserPic(String userPic) {
	this.userPic = userPic;
}
public String getRecommend() {
	return recommend;
}
public void setRecommend(String recommend) {
	this.recommend = recommend;
}
public String getUserName() {
	return userName;
}
public void setUserName(String userName) {
	this.userName = userName;
}
public float getUserRank() {
	return userRank;
}
public void setUserRank(float userRank) {
	this.userRank = userRank;
}

}
