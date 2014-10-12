package cn.edu.swust.frontier;

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.beans.factory.annotation.Autowired;

import cn.edu.swust.seed.invoke.SeedInject;
import cn.edu.swust.uri.CandidateURI;
import cn.edu.swust.uri.SeedTask;

/**
 * 链接边界调度器，负责控制外链的管理和控制
 * 要求线程安全，每次仅仅单线程读写（可考虑读写分离锁来控制）
 * @author pery
 * 2014年10月08日21:31:39
 */
public class FrontierScheduler{
	/**
	 * 
	 * 种子任务队列
	 */
	private List<SeedTask> seedTasks;
	/**
	 * 种子注入策略
	 */
	@Autowired
	private SeedInject seedInject;
	/**
	 * 外链策略
	 */
	@Autowired
	private WorkQueue workQueue;
	/**
	 * 已经抓取url过滤数据集
	 */
	@Autowired
	private BerkelyDBFilter BerkelyDataSource;
	/**
	 * 显示锁来保证线程安全
	 */
	private Lock lock = new ReentrantLock(false);
	/**
	 * 初始化边界器 这里可以实现抓取备份恢复（暂时未实现）
	 */
	public void initialFrontier() {
		seedTasks = seedInject.loadSeedTasks();
		try {
			workQueue.initialWorkQueue(seedTasks);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 获取下一个待抓取的外链
	 * 循环轮询，直到条件溢出
	 * @return
	 */
	public CandidateURI next() {
		lock.lock();
		CandidateURI url= null;
		try {
			while(true){
			 url = this.workQueue.nextCandidateURI();
			 if(url!=null){
				 break;
			 }
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			lock.unlock();
		}
		return url;
	}
	/**
	 * 添加一个Candidate
	 * @param outLink
	 */
	public void put(CandidateURI outLink) {
		lock.lock();
		try {
			this.workQueue.addCandidateURI(outLink);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally{
			lock.unlock();
		}
	}
	/**
	 * 批量添加
	 * @param outLinks
	 */
	public void putAll(List<CandidateURI> outLinks){
		lock.lock();
		try {
			this.workQueue.addAllCandidateURI(outLinks);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			lock.unlock();
		}
	} 
	/**
	 * 添加一个新的种子任务
	 * @param task
	 */
	private void addOneSeedTask(SeedTask task){
		this.seedTasks.add(task);
		this.workQueue.addTaskSeed(task);
	}
	/**
	 * 删除一个已经存在的任务
	 * @param task
	 */
	private void removeSeedTask(SeedTask task){
		
	}
	
}