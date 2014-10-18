package cn.edu.swust.berkeley;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Berkeley中存储的待抓取信息，以供过滤；
 * 
 * @author pery
 */
public class CrawledURIFilter implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static int WAITING = 0;
	public final static int HADCRAWL = 1;
	/**
	 * 抓取内容指纹
	 */
	private String contentMD5;
	/**
	 * 上次抓取时间
	 */
	private Calendar crawlTime;
	/**
	 * 状态，0表示等待抓取中，1表示已经抓取过
	 */
	private int crawlFlag;

	public CrawledURIFilter() {
		crawlFlag = CrawledURIFilter.WAITING;
		;
	}

	public CrawledURIFilter(String md5, Calendar time) {
		crawlFlag = CrawledURIFilter.HADCRAWL;
		this.contentMD5 = md5;
		this.crawlTime = time;
	}

	public String getContentMD5() {
		return contentMD5;
	}

	public void setContentMD5(String contentMD5) {
		this.contentMD5 = contentMD5;
	}

	public Calendar getCrawlTime() {
		return crawlTime;
	}

	public void setCrawlTime(Calendar crawlTime) {
		this.crawlTime = crawlTime;
	}

	public int getCrawlFlag() {
		return crawlFlag;
	}

	public void setCrawlFlag(int crawlFlag) {
		this.crawlFlag = crawlFlag;
	}

}
