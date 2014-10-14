package cn.edu.swust.processor;

import cn.edu.swust.uri.CandidateURI;
import cn.edu.swust.uri.CrawlURI;

public abstract class Processor {
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
