package cn.edu.swust.processor.writer;

import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.edu.swust.processor.ProcessResult;
import cn.edu.swust.processor.Processor;
import cn.edu.swust.uri.CrawlURI;
import cn.edu.swust.uri.SeedTask;
/**
 * 内容抽取存储处理器，注意，此类的实现是单例</br>
 * 所以一定要保证实现类中的线程安全性</br>
 * time：2014年10月14日15:28:27
 * @author pery
 * @version 1.0
 * @
 */
public abstract class AbstractWriterProcessor extends Processor{
	@Override
	public ProcessResult process(CrawlURI crawlURI) throws Exception {
		if(!this.isWriteURI(crawlURI)){
			return ProcessResult.FINISH;
		}
		if(crawlURI.getSeedTask().isByJsoup()){
			Document doc = Jsoup.parse(crawlURI.getContent(), crawlURI.hostStr());
			this.extractAndWriteByJsoup(crawlURI,doc);
		}else{
			this.extractAndWriteByRegext(crawlURI);
		}
		this.afterProcess(crawlURI);
		return ProcessResult.PROCEED;
	}
	/**
	 * 判断当前url是否符合内容抽取规则
	 * @param crawlURI
	 * @return
	 */
	public boolean isWriteURI(CrawlURI crawlURI){
		SeedTask seedTask = crawlURI.getSeedTask();
		Pattern pt = Pattern.compile(seedTask.getContentUrlRegex());
		if(pt.matcher(crawlURI.getCandidateURI()).find()){
			return true;
		}
		return false;
	}
	@Override
	public void afterProcess(CrawlURI crawlURI) throws Exception {
		crawlURI.setContent(null);//helpGC
	}
	
	public abstract void extractAndWriteByJsoup(CrawlURI crawlURI, Document doc);
	public abstract void extractAndWriteByRegext(CrawlURI crawlURI);
}
