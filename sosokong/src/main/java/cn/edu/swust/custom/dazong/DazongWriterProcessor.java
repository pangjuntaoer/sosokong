package cn.edu.swust.custom.dazong;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Preconditions;

import cn.edu.swust.processor.writer.AbstractWriterProcessor;
import cn.edu.swust.uri.CrawlURI;

public class DazongWriterProcessor extends AbstractWriterProcessor {
	private static Log log = LogFactory.getLog(DazongWriterProcessor.class);
	private static int URLCANDIDAT_QUEUE_SIZE  = 60;
	private static String ITEM_REGEX="http://www.dianping.com/search/category/8/10/.*";
	private static String USER_ITEM_REGEX="http://www.dianping.com/shop/(\\d+)/review_all\\?.*";
	private static String ITEM_COMMOMENT="口味(.*)&nbsp;&nbsp;环境(.*)&nbsp;&nbsp;服务(.*)";
	private static String BASE_ITEM_PAGE="http://www.dianping.com";
	static Pattern item_Pattern = Pattern.compile(ITEM_REGEX);
	static Pattern UserItem_Pattern = Pattern.compile(USER_ITEM_REGEX);
	static Pattern item_Comment_Pattern = Pattern.compile(ITEM_COMMOMENT);
	@Autowired
	private DbDao dbdao;
	@Override
	public void extractAndWriteByJsoup(CrawlURI crawlURI,Document doc) {
		Preconditions.checkArgument(doc!=null, "[not surpport]不支持通过Jsoup抽取，"
				+ "请先在种子配置中配置");
		Set<String> candidateUrlList= new HashSet<String>(URLCANDIDAT_QUEUE_SIZE);
		Matcher itemMt = item_Pattern.matcher(crawlURI.getCandidateURI());
		Matcher userItemMt = UserItem_Pattern.matcher(crawlURI.getCandidateURI());		
		if(itemMt.find()){//如果是item列表页面
			candidateUrlList = processItemPage(doc,candidateUrlList);
		}else if(userItemMt.find()){//item评论页面
			String itemId = userItemMt.group(1);
			candidateUrlList = processUserItemPage(doc,itemId,candidateUrlList);
		}
		crawlURI.addAllOutLinks(new ArrayList<String>(candidateUrlList));
	}
	
	private Set<String> processUserItemPage(Document doc,String itemId,Set<String> candidateUrlList) {
		Element comonDiv = doc.body().getElementsByClass("comment-mode").first();
		Element div = comonDiv.select(".comment-list").first();
		Elements comments = div.
				child(0).children();
		List<UserItem> list = new ArrayList<UserItem>(comments.size());
		for (int i = 0; i < comments.size(); i++) {
			Element e = comments.get(i);
			if(e.tagName().equals("li")){
				try {
					UserItem userItem = extractUserItemInfo(e);
					userItem.setItemId(itemId);
					list.add(userItem);
				} catch (Exception e2) {
					e2.printStackTrace();
					log.error(e.html());
				}
			}
		}
		try {
			if(list.size()>0)
			dbdao.insertIntoUserItem(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 评分页面的构造的分页抽取
		Elements pages = comonDiv.select(".PageLink");
		this.itemPageOutLinkExtract(itemId, pages, candidateUrlList);
		return candidateUrlList;
	}
	private void itemPageOutLinkExtract(String itemId,Elements pages,Set<String> candidateUrlList){
		for (int i = 0; i < pages.size(); i++) {
			Element ue = pages.get(i);
			String href = ue.attr("href");
			if(href.contains("?pageno")){
				candidateUrlList.add("http://www.dianping.com/shop/"+
						itemId+"/review_all"+ue.attr("href"));
			}
		}
	}
	private UserItem extractUserItemInfo(Element e) {
		UserItem userItem= new UserItem();
		//li->pic->a[a]
		Element pic = e.child(0);
		String userPic = pic.child(0).child(0).attr("src");
		String userName= pic.child(0).child(0).attr("title");
		String userId = pic.child(0).attr("user-id");
		userItem.setUserPic(userPic);
		userItem.setUserName(userName);
		userItem.setUserId(userId);
		//
		String rankStr = pic.child(2).child(0).attr("class");
		String rankInt = rankStr.substring(rankStr.indexOf("urr-rank")+8);
		//用户级别
		userItem.setUserRank(Float.parseFloat(rankInt)/10);
		
		Element content = e.child(1);
		Element user_info = content.child(0);
		String starStr = user_info.child(0).attr("class");
		String starInt = starStr.substring(starStr.indexOf("irr-star")+8);
		//综合评分
		userItem.setRating(Float.parseFloat(starInt)/10);
		//分项评分
		Elements commentRst=user_info.select(".comment-rst").select(".rst");
		
		String tastStr = commentRst.get(0).ownText();
		String eveStr = commentRst.get(1).ownText();
		String servStr = commentRst.get(2).ownText();
		
		
		String tastInt = tastStr.substring(tastStr.length()-1);
		String eveInt = eveStr.substring(eveStr.length()-1);
		String servInt = servStr.substring(servStr.length()-1);
		userItem.setTast(Float.parseFloat(tastInt));
		userItem.setEnvironment(Float.parseFloat(eveInt));
		userItem.setService(Float.parseFloat(servInt));
		
		Elements recommends = content.select(".comment-recommend");
		for (int i = 0; i < recommends.size(); i++) {
			Element recommend = recommends.get(i);
			String text = recommend.ownText();
			Elements childs = recommend.children();
			for (int j = 0; j < childs.size(); j++) {
				text+=","+childs.get(j).text();//分开
			}
			userItem.setRecommend(userItem.getRecommend()+"|"+text);
		}
		//common_text
		String commen_txt = content.select(".J_brief-cont").first().text();
		userItem.setReview(commen_txt);
		//time
		String time = content.select(".misc-info").first().child(0).text();
		userItem.setTimes(DazongWriterProcessor.findTime(time));
		return userItem;
	}
	//时间处理
	static int year = Calendar.getInstance().get(Calendar.YEAR);
	private static String findTime(String timeStr){
		String time = timeStr.trim();
		if(timeStr.contains("更新于")){
			String prefix = timeStr.substring(0,timeStr.indexOf("更新于")).trim();
			String postfix = timeStr.substring(timeStr.indexOf("更新于")+3).trim();
			if(postfix.length()>=14){//yy-mm-dd hh:mm
				time = postfix;
			}else if(prefix.length()==8&&postfix.length()<=11){
				time=prefix.substring(0,2)+"-"+postfix;
			}
		}
		if(time.length()==5){//yy-mm
			time = year+"-"+time;
		}else if(!time.startsWith("20")){//yy-mm-dd
			time = "20"+time;
		}
		return time;
	}
	public static void main(String[] args) {
		DazongWriterProcessor ty = new DazongWriterProcessor();
		//06-07-18  更新于08-21 22:07
		System.out.println(ty.findTime("07-18  更新于14-08-21 22:07"));
	}
	private Set<String> processItemPage(Document doc,Set<String> candidateUrlList) {
		Elements div = doc.body().select("#sortBar");
		Elements itemList = div.get(0).child(0).child(1).children(); //getElementsByClass("content");
		System.out.println("共有条目："+itemList.size());
		List<Item> list = new ArrayList<Item>(itemList.size());
		for (int i = 0; i < itemList.size(); i++) {
			Element e = itemList.get(i);
			if(e.tagName().equals("li")){
				try {
					Item item = extractItemInfo(e);
					list.add(item);
					//构造评分页码
					candidateUrlList.add("http://www.dianping.com/shop/"+item.getId()+"/review_all?pageno=1");
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		try {
			if(list.size()>0)
			dbdao.insertIntoItem(list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Element page = div.select(".page").first();
		this.userItemPageOutLinkExtract(page, candidateUrlList);
		//导航分类抽取
		Element nav = doc.body().select(".nav").first();
		Elements navUrl=nav.select("a[href]");
		for (int i = 0; i < navUrl.size(); i++) {
			Element ue = navUrl.get(i);
			if(ue.attr("href").contains("/search/category/8/10")){
				candidateUrlList.add(BASE_ITEM_PAGE+ue.attr("href"));
			}
		}
		return candidateUrlList;
	}

	public void userItemPageOutLinkExtract(Element page,Set<String> candidateUrlList){
		///分页抽取
				Elements urlsEle = page.select("a[href]");
				for (int i = 0; i < urlsEle.size(); i++) {
					Element ue = urlsEle.get(i);
					if(ue.attr("href").contains("/search/category/8/10")){
						candidateUrlList.add(BASE_ITEM_PAGE+ue.attr("href"));
					}
				}
	}
	private Item extractItemInfo(Element e) {
		Item item = new Item();
		String itemUrl = e.child(0).attr("href");//item外链
		String itemPic = e.child(0).child(0).attr("src");//item图片uri
		item.setId(itemUrl.substring(6));//获取item的id
		item.setItemPic(itemPic);
		
		Element info = e.child(1);//class=info
		Elements infoChilds = info.children();
		item.setName(infoChilds.get(0).child(0).child(0).text());
		Element remark = infoChilds.get(1);//class=remark
		String starStr = remark.child(0).attr("class");
		String star = starStr.substring(starStr.length()-2, starStr.length()-1);
		//获得item级别
		item.setStar(Float.parseFloat(star.charAt(0)+"."+star.charAt(1)));
		
		String reviewCountStr = remark.child(1).text();
		String reviewCount = reviewCountStr.trim().substring(0, reviewCountStr.indexOf("封")); 
		//获得评评价总数
		item.setReviewCount(Integer.parseInt(reviewCount));
		
		Element comment = infoChilds.get(2);//class=comment
		String price = comment.child(0).child(0).text().trim().replace("￥", "");
		//获得人均消费
		item.setCost(Float.parseFloat(price));
		
		String commenList = comment.child(1).text();
		String [] str = commenList.split("\\s");
		item.setTast(Float.parseFloat(str[0].substring(2)));
		item.setEnvironment(Float.parseFloat(str[1].substring(2)));
		item.setService(Float.parseFloat(str[2].substring(2)));
		
		if(infoChilds.size()>3){//优惠活动等等
			for (int i = 3; i < infoChilds.size(); i++) {
				Element other= infoChilds.get(0);
				item.setItemInfo(item.getItemInfo()+"|"+other.text());
			}
		}
		Element message = e.child(2);//class=message
		item.setItemKeyWord(message.text());
		return item;
	}
	@Override
	public void extractAndWriteByRegext(CrawlURI crawlURI) {
		
	}

}
