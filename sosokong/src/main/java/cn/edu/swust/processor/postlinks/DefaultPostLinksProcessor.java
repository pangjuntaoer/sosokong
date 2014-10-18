package cn.edu.swust.processor.postlinks;

import cn.edu.swust.processor.ProcessResult;
import cn.edu.swust.uri.CrawlURI;
import cn.edu.swust.uri.SeedTask;

public class DefaultPostLinksProcessor extends AbstractPostLinksProcessor{

	@Override
	public ProcessResult process(CrawlURI crawlURI) throws Exception {
		this.postOutLinks2FrontinerByAll(crawlURI);
		this.afterProcess(crawlURI);
		return null;
	}
	/**
	 * 暂时不用
	 */
	@Override
	public void afterProcess(CrawlURI crawlURI) throws Exception {
	/*	SeedTask seedTask = crawlURI.getSeedTask();
		boolean isFinish  = this.frontier.hasFinished(seedTask);
		if(isFinish){
			//
		}*/
		
		crawlURI = null;//help Gc
	}

}
