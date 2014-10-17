package cn.edu.swust.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import cn.edu.swust.frontier.FrontierScheduler;
import cn.edu.swust.processor.ProcessorChain;
/**
 * 爬虫启动中心2014年10月17日15:03:11
 * @author pery
 *
 */
public class ExecuteService {
	/**
	 * 线程池
	 */
	private ExecutorService executorService;
	/**
	 * 工作线程数
	 */
	private int threadCount = 10;
	private FrontierScheduler frontier;
	private ProcessorChain processorChain;

	public void runAPP() {
		executorService = Executors.newFixedThreadPool(threadCount);
		for (int i = 0; i < threadCount; i++) {
			executorService
					.submit(new CrawlController(frontier, processorChain));
		}
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public FrontierScheduler getFrontier() {
		return frontier;
	}

	public void setFrontier(FrontierScheduler frontier) {
		this.frontier = frontier;
	}

	public ProcessorChain getProcessorChain() {
		return processorChain;
	}

	public void setProcessorChain(ProcessorChain processorChain) {
		this.processorChain = processorChain;
	}

}
