package cn.edu.swust.uri;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.common.base.Objects;

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
	 * 抓取深度
	 */
	private int fetchHeigh;
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
	
}
