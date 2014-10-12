package cn.edu.swust.controller;

import cn.edu.swust.frontier.FrontierScheduler;
import cn.edu.swust.processor.ProcessorChain;
import cn.edu.swust.processor.fetch.utils.QHttpClient;
import cn.edu.swust.uri.CandidateURI;

public class CrawlController implements Runnable{
private FrontierScheduler frontierScheduler;
private ProcessorChain processorChain;
/**
 * 每个线程分配一个httpClient，方便管理
 */
private QHttpClient qHttpClient;
public CrawlController(FrontierScheduler frontierScheduler,
		ProcessorChain processorChain){
	this.frontierScheduler = frontierScheduler;
	this.processorChain = processorChain;
	//
	qHttpClient = new QHttpClient();
}
	public void run() {
		CandidateURI uri = this.frontierScheduler.next();
		processorChain.beginProcess(uri);
	}

}
