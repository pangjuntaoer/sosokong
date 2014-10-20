package cn.edu.swust.processor.postlinks;

import cn.edu.swust.processor.ProcessResult;
import cn.edu.swust.uri.CrawlURI;
import cn.edu.swust.uri.SeedTask;

public class DefaultPostLinksProcessor extends AbstractPostLinksProcessor{

	@Override
	public ProcessResult process(CrawlURI crawlURI) throws Exception {
		if(!crawlURI.isRetry()){
			this.postOutLinks2FrontinerByAll(crawlURI);
		}else{
			this.retry2FrontinerByOne(crawlURI);
		}
		this.afterProcess(crawlURI);
		return null;
	}
	@Override
	public void afterProcess(CrawlURI crawlURI) throws Exception {
		this.frontier.WriteURIFetchFinishInfo(crawlURI);//完成
		//////////下面进行判断本轮抓取完成处理
		/*	SeedTask seedTask = crawlURI.getSeedTask();
		boolean isFinish  = this.frontier.hasFinished(seedTask);
		if(isFinish){
			//
		}*/
		
		crawlURI = null;//help Gc
	}

}
