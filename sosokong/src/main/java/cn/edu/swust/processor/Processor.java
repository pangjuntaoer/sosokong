package cn.edu.swust.processor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cn.edu.swust.processor.fetch.utils.QHttpClient;
import cn.edu.swust.uri.CrawlURI;

public abstract class Processor {
	// 日志输出
	protected static Log log = LogFactory.getLog(Processor.class);
	/**
	 * 处理过程
	 * @param crawlURI
	 * @return
	 * @throws Exception
	 */
	public abstract ProcessResult process(CrawlURI crawlURI) throws Exception;
	/**
	 * 处理完成后的一些工作，比如清理工作，状态改变
	 * @param crawlURI
	 * @throws Exception
	 */
	public abstract void afterProcess(CrawlURI crawlURI)throws Exception;
}
