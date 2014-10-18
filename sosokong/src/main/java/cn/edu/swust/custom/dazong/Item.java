package cn.edu.swust.custom.dazong;

public class Item {
private String id;
private String name;
private float star;
private float cost;
private float tast;
private float environment;
private float service;
private int reviewCount;

private String itemPic;
private String itemInfo="";
private String itemKeyWord;
public String toString(){
	StringBuffer bf = new StringBuffer();
	bf.append("id:").append(this.id)
	  .append("name:").append(this.name)
	  .append("start:").append(this.star)
	  .append("cost:").append(this.cost)
	  .append("tast:").append(this.tast)
	  .append("environment:").append(this.environment)
	  .append("service:").append(this.service)
	  .append("reviewCount:").append(this.reviewCount);
	return bf.toString();
}
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}

public String getItemPic() {
	return itemPic;
}
public void setItemPic(String itemPic) {
	this.itemPic = itemPic;
}

public String getItemInfo() {
	return itemInfo;
}
public void setItemInfo(String itemInfo) {
	this.itemInfo = itemInfo;
}
public float getStar() {
	return star;
}
public void setStar(float star) {
	this.star = star;
}
public float getCost() {
	return cost;
}
public void setCost(float cost) {
	this.cost = cost;
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
public int getReviewCount() {
	return reviewCount;
}
public void setReviewCount(int reviewCount) {
	this.reviewCount = reviewCount;
}
public String getItemKeyWord() {
	return itemKeyWord;
}
public void setItemKeyWord(String itemKeyWord) {
	this.itemKeyWord = itemKeyWord;
}


}
