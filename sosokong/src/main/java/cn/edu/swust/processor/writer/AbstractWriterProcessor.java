package cn.edu.swust.processor.writer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import cn.edu.swust.processor.ProcessResult;
import cn.edu.swust.processor.Processor;
import cn.edu.swust.uri.CrawlURI;
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
		if(crawlURI.getSeedTask().isByJsoup()){
			Document doc = Jsoup.parse(crawlURI.getContent(), crawlURI.hostStr());
			this.extractAndWriteByJsoup(crawlURI,doc);
		}else{
			this.extractAndWriteByRegext(crawlURI);
		}
		this.afterProcess(crawlURI);
		return null;
	}

	@Override
	public void afterProcess(CrawlURI crawlURI) throws Exception {
		crawlURI.setContent(null);//helpGC
	}
	
	public abstract void extractAndWriteByJsoup(CrawlURI crawlURI, Document doc);
	public abstract void extractAndWriteByRegext(CrawlURI crawlURI);
}
