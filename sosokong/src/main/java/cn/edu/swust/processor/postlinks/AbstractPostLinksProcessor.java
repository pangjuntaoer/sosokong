package cn.edu.swust.processor.postlinks;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Lists;

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
	for (int i = 0; i < outLinks.size(); i++) {
		candidateURIs.add(new CandidateURI(seedTask,outLinks.get(i)
				,reference));
	}
	frontier.putAll(candidateURIs);
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

public void setBerkeleyFlag(CrawlURI uri){
	
}

}
