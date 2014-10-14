package cn.edu.swust.processor.extract;

import cn.edu.swust.processor.ProcessResult;
import cn.edu.swust.processor.fetch.AbstractFetchtorProcessor;
import cn.edu.swust.uri.CrawlURI;

public class DefaultExtractProcessor extends AbstractExtractorProcessor {

	@Override
	public ProcessResult process(CrawlURI crawlURI) throws Exception {
		this.innerExtractOutLink(crawlURI);
		this.afterProcess(crawlURI);
		return null;
	}

	@Override
	public void afterProcess(CrawlURI crawlURI) throws Exception {

	}

}
