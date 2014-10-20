package cn.edu.swust.processor.postlinks;

import java.util.Calendar;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

import cn.edu.swust.berkeley.CrawledURIFilter;
import cn.edu.swust.frontier.FrontierScheduler;
import cn.edu.swust.processor.Processor;
import cn.edu.swust.uri.CandidateURI;
import cn.edu.swust.uri.CrawlURI;
import cn.edu.swust.uri.SeedTask;
/**
 * 后续链接处理
 * @author pery
 *
 */
public abstract class AbstractPostLinksProcessor extends Processor {
@Autowired	
protected FrontierScheduler frontier;
/**
 * 批量添加外链
 * 2014年10月15日10:25:01
 * @param uri
 * @param outLinks
 */
protected void postOutLinks2FrontinerByAll(CrawlURI uri){
	SeedTask seedTask = uri.getSeedTask();
	String reference = uri.getCandidateURI();
	List<String> outLinks = uri.outLinkIList();
	List<CandidateURI> candidateURIs = Lists.newArrayList();
	Calendar now = Calendar.getInstance();
	for (int i = 0; i < outLinks.size(); i++) {
		String candidateURI = outLinks.get(i);
		CrawledURIFilter crawlURIinfo = frontier.readURIFetchInfo(candidateURI);
		if(crawlURIinfo==null){
			candidateURIs.add(new CandidateURI(seedTask,candidateURI,reference));
		}else if(crawlURIinfo.getCrawlFlag()==CrawledURIFilter.HADCRAWL){
			//如果已经抓取过了，判断时间节点是否满足新的一轮抓取
			Calendar old = crawlURIinfo.getCrawlTime();
			if(now.get(Calendar.HOUR)-old.get(Calendar.HOUR)>=
			seedTask.getCrawlInterval()){
				candidateURIs.add(new CandidateURI(seedTask,candidateURI,reference));
			}
		}
	}
	frontier.putAll(candidateURIs);
}
/**
 * 重试
 * @param crawlURI
 */
protected void retry2FrontinerByOne(CrawlURI crawlURI){
	frontier.put(crawlURI);
}
/**
 * 一个一个加入外链
 * @param uri
 * @param outLinks
 */
protected void postOutLinks2FrontinerByOne(CrawlURI uri,List<String> outLinks){
	SeedTask seedTask = uri.getSeedTask();
	String reference = uri.getCandidateURI();
	for (int i = 0; i < outLinks.size(); i++) {
		frontier.put(new CandidateURI(seedTask,outLinks.get(i)
				,reference));
	}
}
}
