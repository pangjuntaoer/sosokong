package cn.edu.swust.uri;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.impl.client.BasicCookieStore;

import com.google.common.collect.Lists;

public class CrawlURI extends CandidateURI {
	public CrawlURI(SeedTask seedTask) {
		super(seedTask);
	}
	public CrawlURI(CandidateURI candidateURI) {
		super(candidateURI);
	}
	private String content;
	private String contentMd5;
	private List<String> outLinks=Lists.newArrayList();
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
		this.contentMd5=DigestUtils.md5Hex(content);
	}
	
	public String getContentMd5() {
		return contentMd5;
	}
	public void setContentMd5(String contentMd5) {
		this.contentMd5 = contentMd5;
	}
	/**
	 * 获取当前外链的host
	 * @return
	 */
	public String hostStr(){
		String str = this.getCandidateURI();
		String preffix = "http://";
		if(str.startsWith("http://")){
			str.replace("http://", "");
		}else if(str.startsWith("https")){
			str.replace("https://", "");
			preffix="https://";
		}
		String host = str;
		if(str.matches(".+/.*")){
			host = str.substring(0,str.indexOf("/"));
		}
		return preffix+host;
	}
	/**
	 * 获取请求路径
	 * @return
	 */
	public String realPath(){
		String str = this.getCandidateURI();
		if(str.contains("?")){
			str = str.substring(0, str.indexOf("?"));
		}
		return str;
	}
	/**
	 * 清空当前CrawlURI已经抽取到的外链
	 * @return
	 */
	public CrawlURI clearOutLinks(){
		this.outLinks.clear();
		return this;
	}
	/**
	 * 向CrawlURI添加一个外链
	 * @param uri
	 * @return
	 */
	public CrawlURI addOneOutLink(String uri){
		this.outLinks.add(uri);
		return this;
	}
	/**
	 * 向CrawlURI添加多个外链
	 * @param uri
	 * @return
	 */
	public CrawlURI addAllOutLinks(List<String> uris){
		this.outLinks.addAll(uris);
		return this;
	}
	/**
	 * 获取外链的迭代器
	 * @return
	 */
	public Iterator<String> outLinkIterator(){
		return this.outLinks.iterator();
	}
	/**
	 * 获取外链的list数组
	 * @return
	 */
	public List<String> outLinkIList(){
		return this.outLinks;
	}
}
