package cn.edu.swust.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;

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
	private int workThreadCount = 10;
	private FrontierScheduler frontier;
	private ProcessorChain processorChain;

	public void runAPP() {
		frontier.initialFrontier();//初始化边界控制器
		executorService = Executors.newFixedThreadPool(workThreadCount);
		for (int i = 0; i < workThreadCount; i++) {
			executorService
					.submit(new CrawlController(frontier, processorChain));
		}
	}

	public int getWorkThreadCount() {
		return workThreadCount;
	}


	public void setWorkThreadCount(int workThreadCount) {
		this.workThreadCount = workThreadCount;
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
