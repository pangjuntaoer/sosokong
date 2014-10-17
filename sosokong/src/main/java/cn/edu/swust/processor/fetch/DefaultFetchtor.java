package cn.edu.swust.processor.fetch;

import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.springframework.util.StringUtils;

import cn.edu.swust.processor.ProcessResult;
import cn.edu.swust.processor.fetch.utils.QHttpClient;
import cn.edu.swust.uri.CandidateURI;
import cn.edu.swust.uri.CrawlURI;
import cn.edu.swust.uri.SeedTask;

public class DefaultFetchtor extends AbstractFetchtorProcessor {
	@Override
	public ProcessResult process(CrawlURI crawlURI) throws Exception {
		QHttpClient httpClient = this.getOneHttpClient();
		CookieStore cookie = crawlURI.getSeedTask().getOneCookieStore();
		
		String content = httpClient.httpGet(crawlURI.getCandidateURI(),cookie);
		
		boolean isRedirect = this.isRedirectHost(crawlURI, httpClient.getHttpContext());
		if(!isRedirect&&httpClient.getHttpResponseCode()>=HttpStatus.SC_OK&&
				httpClient.getHttpResponseCode()<HttpStatus.SC_BAD_REQUEST){
			crawlURI.setContent(content);
		}
		if(!this.isContinueProcess(crawlURI)){
			return ProcessResult.FINISH;
		}
		this.afterProcess(crawlURI);
		return ProcessResult.PROCEED;
	}
	@Override
	public void afterProcess(CrawlURI crawlURI) {
	}

}
