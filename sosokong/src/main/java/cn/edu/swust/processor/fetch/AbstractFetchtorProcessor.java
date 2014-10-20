package cn.edu.swust.processor.fetch;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpHost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.google.common.collect.Maps;

import cn.edu.swust.berkeley.CrawledURIFilter;
import cn.edu.swust.frontier.FrontierScheduler;
import cn.edu.swust.processor.Processor;
import cn.edu.swust.processor.fetch.utils.QHttpClient;
import cn.edu.swust.uri.CrawlURI;

public abstract class AbstractFetchtorProcessor extends Processor{
    // 日志输出
private static Log log = LogFactory.getLog(AbstractFetchtorProcessor.class);
@Autowired
protected FrontierScheduler frontier;
/**
 * httpClient实例集合，一个线程对应一个httClient实例；
 */
protected static Map<String,QHttpClient> httpClientMap;
static{
	httpClientMap=Maps.newConcurrentMap();
}
/**
 * 抓取前预处理，判断该链接是否在<strong>本轮</strong>抓取过</br>
 * 当本轮抓取过就不在抓取了
 * @param uri
 * @return
 */
public boolean preFetchProcess(CrawlURI uri){
	return false;
}
/**
 * 抓取后内容判断处理,判断是否进行后续处理
 * 只用于每一轮抓取的判断
 * @param uri
 * @return
 */
public boolean isContinueProcess(CrawlURI uri){
	CrawledURIFilter info = this.frontier.readURIFetchInfo(uri.getCandidateURI());
	if(info!=null&&StringUtils.hasText(info.getContentMD5())){
		if(uri.getContentMd5().equals(info.getContentMD5())){
			this.frontier.WriteURIFetchFinishInfo(uri);//覆盖上次抓取记录（更新抓取时间）
			return false;
		}
	}
	return true;
}
/**
 * 根据线程名称获得对应的HttpClient;
 * @return
 */
public QHttpClient getOneHttpClient(){
	String name = Thread.currentThread().getName();
	QHttpClient httpClient = httpClientMap.get(name);
	if(httpClient==null){
		httpClient = new QHttpClient();
		httpClientMap.put(name, httpClient);
		log.info("[AbstractFetchtorProcessor NewHttpClient]"+name);
	}
	return httpClient;
}
public boolean  isRedirectHost(CrawlURI uri,HttpContext httpContext){
	//请求主机地址
    HttpHost targetHost = (HttpHost)httpContext.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
    //获取实际的请求对象的URI,即重定向之后的"/blog/admin/login.jsp"
    HttpUriRequest realRequest = (HttpUriRequest)httpContext.getAttribute(ExecutionContext.HTTP_REQUEST);
    String host = targetHost.toString()+realRequest.getURI().toString();
    if(host.contains("?")){
    	host = host.substring(0, host.indexOf("?"));
    }
    if(uri.realPath().contains(host)){
    	return false;
    }
    String redirectRegex = uri.getSeedTask().getRedirectRegex();
    if(redirectRegex!=null&&host.matches(redirectRegex)){
    	log.info("[redirect]"+uri.getCandidateURI()+" -> "+host);
    	return true;
    }
    log.info("[redirect]"+uri.getCandidateURI()+" -> "+host);
    return true;
}
}
