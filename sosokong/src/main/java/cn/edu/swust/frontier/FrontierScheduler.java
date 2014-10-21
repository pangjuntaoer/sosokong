package cn.edu.swust.frontier;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;

import cn.edu.swust.berkeley.BerkelyDBFilter;
import cn.edu.swust.berkeley.CrawledURIFilter;
import cn.edu.swust.core.BackupController;
import cn.edu.swust.core.BackupHandle;
import cn.edu.swust.seed.invoke.SeedInject;
import cn.edu.swust.uri.CandidateURI;
import cn.edu.swust.uri.CrawlURI;
import cn.edu.swust.uri.SeedTask;

/**
 * 链接边界调度器，负责控制外链的管理和控制 要求线程安全，每次仅仅单线程读写（可考虑读写分离锁来控制）
 * 
 * @author pery 2014年10月08日21:31:39
 */
public class FrontierScheduler {
	/**
	 * 
	 * 种子任务队列(需要随时备份)
	 */
	private List<SeedTask> seedTasks;
	/**
	 * 种子注入策略
	 */
	@Autowired
	private SeedInject seedInject;
	/**
	 * 外链存储队列(需要随时备份)
	 */
	@Autowired
	private WorkQueue workQueue;
	/**
	 * 已经抓取url过滤数据集
	 */
	@Autowired
	private BerkelyDBFilter berkelyDataSource;
	/**
	 * 显示锁来保证线程安全
	 */
	private Lock lock = new ReentrantLock(false);

	/**
	 * 初始化边界器 这里可以实现抓取备份恢复（暂时未实现）
	 * @param backupHandle 备份恢复操作
	 */
	public void initialFrontier(BackupHandle backupHandle) {
		if(this.recoverFromBackup(backupHandle)){
			return;
		}
		seedTasks = seedInject.loadSeedTasks();
		try {
			workQueue.initialWorkQueue(seedTasks);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 从备份文件中恢复数据
	 * @param backHandle
	 * @return
	 */
	private boolean recoverFromBackup(BackupHandle backupHandle){
		Object [] objects = backupHandle.recoverBackUp();
		if(objects==null||objects.length==0){
			return false;
		}else {
			boolean w =false;
			boolean s = false;
			for (int i = 0; i < objects.length; i++) {
				Object o = objects[i];
				if(o.getClass().isInstance(workQueue)){
					workQueue.recoverWorkQueue((WorkQueue)o);
					w = true;
				}else if(o.getClass().isInstance(seedTasks)){
					seedTasks = (List)o;
					s=true;
				}
			}
			return w&s;
		}
	}
	/**
	 * 获得需要备份对象
	 * @return
	 */
	public Object[] backupObjects() {
		Object [] objects= new Object [2];
		objects[0] = this.seedTasks;
		objects[1] = this.workQueue;
		return objects;
	}
	/**
	 * 获取下一个待抓取的外链 循环轮询，直到条件溢出
	 * 
	 * @return
	 */
	public CandidateURI next() {
		CandidateURI url = null;
		try {
			while (true) {
				url = this.workQueue.nextCandidateURI();
				if (url != null) {
					break;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return url;
	}

	/**
	 * 写入当前抓取链接完成的标识
	 * 
	 * @param uri
	 */
	public void WriteURIFetchFinishInfo(CrawlURI uri) {
		lock.lock();
		try {
			String key = DigestUtils.md5Hex(uri.getCandidateURI());
			CrawledURIFilter value = new CrawledURIFilter(uri.getContentMd5(),
					Calendar.getInstance());
			//berkelyDataSource.openDatabase();
			berkelyDataSource.writeToDatabase(key, value, true);
			//berkelyDataSource.closeDatabase();
		} catch (Exception e) {
			lock.unlock();
		}
	}

	/**
	 * 写入当前链接为等待抓取状态
	 * 
	 * @param uri
	 */
	public void WriteURIFetchWaitInfo(CandidateURI uri) {
		lock.lock();
		try {
		String key = DigestUtils.md5Hex(uri.getCandidateURI());
		CrawledURIFilter value = new CrawledURIFilter();
		//berkelyDataSource.openDatabase();
		berkelyDataSource.writeToDatabase(key, value, true);
		//berkelyDataSource.closeDatabase();
		} catch (Exception e) {
			lock.unlock();
		}
	}
	/**
	 * 写入当前链接为等待抓取状态
	 * 
	 * @param uri
	 */
	public void WriteURIFetchWaitInfoByBatch(List<CandidateURI> uris) {
		lock.lock();
		try {
		//berkelyDataSource.openDatabase();
		for (int i = 0; i < uris.size(); i++) {
			CandidateURI uri = uris.get(i);
			String key = DigestUtils.md5Hex(uri.getCandidateURI());
			CrawledURIFilter value = new CrawledURIFilter();
			berkelyDataSource.writeToDatabase(key, value, true);
		}
		//berkelyDataSource.closeDatabase();
		} catch (Exception e) {
			lock.unlock();
		}
	}

	
	/**
	 * 读取某个链接的抓取信息info 不需保证线程安全
	 * 
	 * @param candidateURI
	 * @return
	 */
	public CrawledURIFilter readURIFetchInfo(String candidateURI) {
		CrawledURIFilter info = null;
		String key = DigestUtils.md5Hex(candidateURI);
		//berkelyDataSource.openDatabase();
		info = berkelyDataSource.readFromDatabase(key);
		//berkelyDataSource.closeDatabase();
		return info;
	}

	/**
	 * 添加一个Candidate
	 * 
	 * @param outLink
	 */
	public void put(CandidateURI outLink) {
		try {
			this.workQueue.addCandidateURI(outLink);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 批量添加外链
	 * 
	 * @param outLinks
	 */
	public void putAll(List<CandidateURI> outLinks) {
		try {
			for (int i = 0; i < outLinks.size(); i++) {
				CandidateURI candidateURI = outLinks.get(i);
				this.WriteURIFetchWaitInfo(candidateURI);
				this.workQueue.addCandidateURI(candidateURI);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// /////////////////////////////////////////以下内容待完善////////////////
	/**
	 * 判断当前这个任务本轮是否完成抓取 有bug...待完善
	 * 
	 * @param seedTask
	 * @return
	 */
	public boolean hasFinished(SeedTask seedTask) {
		int remainSize = this.workQueue.seetTaskRemainSize(seedTask);
		if (remainSize <= 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 添加一个新的种子任务 暂时不对外不能使用(private),因为workQueue.addTaskSeed(task);未完善
	 * 
	 * @param task
	 */
	private void addOneSeedTask(SeedTask task) {
		lock.lock();
		try {
			this.seedTasks.add(task);
			this.workQueue.addTaskSeed(task);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 删除一个已经存在的任务 暂时不对外不能使用(private),因为workQueue.removeTask(task);未完善
	 * 
	 * @param task
	 */
	private void removeSeedTask(SeedTask task) {
		lock.lock();
		try {
			this.workQueue.removeTask(task);
			for (int i = 0; i < seedTasks.size(); i++) {
				if (task.getSeedFingerprint().equals(
						seedTasks.get(i).getSeedFingerprint())) {
					seedTasks.remove(i);
					break;
				}
			}
		} finally {
			lock.unlock();
		}
	}
}
