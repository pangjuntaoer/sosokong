package cn.edu.swust.processor.fetch;

import org.apache.http.HttpStatus;

import cn.edu.swust.processor.ProcessResult;
import cn.edu.swust.processor.fetch.utils.QHttpClient;
import cn.edu.swust.uri.CandidateURI;
import cn.edu.swust.uri.CrawlURI;
import cn.edu.swust.uri.SeedTask;

public class DefaultFetchtor extends AbstractFetchtorProcessor {

	@Override
	public ProcessResult process(CrawlURI crawlURI) throws Exception {
		QHttpClient httpClient = this.getOneHttpClient();
		String content = httpClient.httpGet(crawlURI.getCandidateURI());
		boolean isRedirect = this.isRedirectHost(crawlURI, httpClient.getHttpContext());
		if(!isRedirect&&httpClient.getHttpResponseCode()>=HttpStatus.SC_OK&&
				httpClient.getHttpResponseCode()<HttpStatus.SC_BAD_REQUEST){
			crawlURI.setContent(content);
		}
		this.afterProcess(crawlURI);
		return null;
	}

	@Override
	public void afterProcess(CrawlURI crawlURI) {

	}

}
