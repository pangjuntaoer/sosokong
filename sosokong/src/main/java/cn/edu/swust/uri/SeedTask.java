package cn.edu.swust.uri;

import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.client.CookieStore;

import cn.edu.swust.utils.CookieUtil;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

/**
 * 种子任务类(抽象)
 * @author pery
 *2014年10月08日20:02:19
 */
public abstract class SeedTask {
	/**
	 * 种子内容
	 */
	private String seedUrl;
	/**
	 * 是否模拟登陆
	 */
	private boolean analogLogin=false;
	/**
	 * 种子指纹（字符串唯一标示）
	 */
	private String SeedFingerprint;
	private int seedFingerprintIntValue;
	/**
	 * 外链抽取url正则表达式
	 * 注意该正则要匹配（种子，外链，内容等类型的连接）
	 */
	private String outLinksRegex;
	/**
	 * 正文内容（需要提取的正文）url正则表达式
	 */
	private String contentUrlRegex;
	/**
	 * 重定向地址正则，（一般针对，访问请求异常，如验证码页面）；
	 */
	private String redirectRegex;
	/**
	 * 抓取深度
	 */
	private int fetchHeigh;
	/**
	 * 抽取是否通过Jsoup抽取
	 */
	private boolean byJsoup=false;
	/**
	 * cookie，字符串
	 */
	private List<String> cookieStr;
	private List<CookieStore> cookieStores;
	/**
	 * 抓取间隔，以小时为单位，默认24*5，默认5天抓取抓取一次
	 */
	private int crawlInterval=120;
	
	public String getSeedUrl() {
		return seedUrl;
	}
	
	/**
	 * 生成种子任务指纹
	 * 不允许覆盖
	 * @param seedUrl
	 */
	public final void setSeedUrl(String seedUrl) {
		this.seedUrl = seedUrl;
		//生成md5指纹
		this.SeedFingerprint=DigestUtils.md5Hex(seedUrl);
		this.seedFingerprintIntValue=Objects.hashCode(this.SeedFingerprint);
	}
	public String getOutLinksRegex() {
		return outLinksRegex;
	}
	public void setOutLinksRegex(String outLinksRegex) {
		this.outLinksRegex = outLinksRegex;
	}
	public String getContentUrlRegex() {
		return contentUrlRegex;
	}
	public void setContentUrlRegex(String contentUrlRegex) {
		this.contentUrlRegex = contentUrlRegex;
	}
	/**
	 * 获得该种子的唯一标示
	 * @return
	 */
	public String getSeedFingerprint() {
		return SeedFingerprint;
	}
	public void setSeedFingerprint(String seedFingerprint) {
		SeedFingerprint = seedFingerprint;
	}
	public int getFetchHeigh() {
		return fetchHeigh;
	}
	public void setFetchHeigh(int fetchHeigh) {
		this.fetchHeigh = fetchHeigh;
	}
	public int getSeedFingerprintIntValue() {
		return seedFingerprintIntValue;
	}
	public void setSeedFingerprintIntValue(int seedFingerprintIntValue) {
		this.seedFingerprintIntValue = seedFingerprintIntValue;
	}
	public String getRedirectRegex() {
		return redirectRegex;
	}
	public void setRedirectRegex(String redirectRegex) {
		this.redirectRegex = redirectRegex;
	}

	public boolean isAnalogLogin() {
		return analogLogin;
	}

	public void setAnalogLogin(boolean analogLogin) {
		this.analogLogin = analogLogin;
	}

	public boolean isByJsoup() {
		return byJsoup;
	}

	public void setByJsoup(boolean byJsoup) {
		this.byJsoup = byJsoup;
	}

	public List<String> getCookieStr() {
		return cookieStr;
	}

	public void setCookieStr(List<String> cookieStr) {
		this.cookieStr = cookieStr;
	}
	public int getCrawlInterval() {
		return crawlInterval;
	}

	public void setCrawlInterval(int crawlInterval) {
		this.crawlInterval = crawlInterval;
	}

	///以后需要重写，cookie策略
	public CookieStore getOneCookieStore() throws Exception{
		if(cookieStores==null){
			cookieStores = Lists.newArrayList();
			for (int i = 0; i < cookieStr.size(); i++) {
				cookieStores.add(CookieUtil.stringToCookie(cookieStr.get(i)));
			}
		}
			return cookieStores.get(0);
	}
}
