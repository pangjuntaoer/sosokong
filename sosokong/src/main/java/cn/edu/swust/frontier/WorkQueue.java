package cn.edu.swust.frontier;

import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cn.edu.swust.uri.CandidateURI;
import cn.edu.swust.uri.SeedTask;

/**
 * 外链队列 一个种子对应一个队列， 所有种子构成一个种子队列 key1 key2 key3 ....横向种子队列 url11 url21 url31
 * url12 url22 url32 .. .. ... 纵向外链队列 存取外链的时候，先计算属于那个纵向队列key(整数)
 * 有多少个种子（任务，一个种子算一个任务）就会初始化几个队列
 * 
 * @author pery 2014年10月08日19:07:59
 */
public class WorkQueue {
	/**
	 * 外链队列(不提供对外访问接口)
	 */
	private List<BlockingQueue> outLinksQueue;
	/**
	 * (不提供对外访问接口) 种子队列索引表，key是种子url的唯一标示 这里采用LinkedhashMap,保证存和遍历顺序一致
	 */
	private Map<String, SeedIndex> seedIndexMap;
	/**
	 * 上次遍历取外链成功的索引
	 */
	private int lastGetIndex = 0;
	/**
	 * 显示锁提供线程安全
	 */
	private Lock lock = new ReentrantLock(false);

	/**
	 * 取出一个外链
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	public CandidateURI nextCandidateURI() throws InterruptedException {
		Preconditions.checkArgument(lastGetIndex >= 0
				&& lastGetIndex < seedIndexMap.size(), "上次取外链的索引参数异常");
		CandidateURI uri = null;
		boolean success = false;
		int nowIndex = -1;
		SeedIndex seedIndex = null;
		lock.lock();
		try {
			while (!success) {
				nowIndex = (lastGetIndex + 1) % seedIndexMap.size();
				seedIndex = seedIndexMap.get(nowIndex);
				if (!seedIndex.isSkip) {
					success = true;
					break;
				} else {
					lastGetIndex++;
				}
			}
			if (success && nowIndex > -1) {
				BlockingQueue<CandidateURI> linksQueue = outLinksQueue
						.get(nowIndex);
				uri = linksQueue.poll(timeout, TimeUnit.MILLISECONDS);
				seedIndex.outLinksSize = linksQueue.size();
			}
		} finally {
			lock.unlock();
		}
		return uri;
	}

	/**
	 * 添加一个外链
	 * 
	 * @param uri
	 * @throws InterruptedException
	 */
	public void addCandidateURI(CandidateURI uri) throws InterruptedException {
		SeedTask seedTask = uri.getSeedTask();
		SeedIndex seedIndex = null;
		if (seedTask != null) {
			seedIndex = seedIndexMap.get(seedTask.getSeedFingerprint());
		}
		if (seedIndex != null) {
			lock.lock();
			BlockingQueue<CandidateURI> linksQueue = outLinksQueue
					.get(seedIndex.index);
			linksQueue.offer(uri, timeout, TimeUnit.MILLISECONDS);
			seedIndex.outLinksSize = linksQueue.size();
			lock.unlock();
		}
	}

	/**
	 * 批量加入外链，推荐队列大小无限制使用，且要求是同一个任务下的外链
	 * 
	 * @param uris
	 */
	public void addAllCandidateURI(List<CandidateURI> uris) {
		if (uris == null || uris.size() <= 0)
			return;
		SeedTask seedTask = uris.get(0).getSeedTask();
		SeedIndex seedIndex = null;
		if (seedTask != null) {
			seedIndex = seedIndexMap.get(seedTask.getSeedFingerprint());
		}
		if (seedIndex != null) {
			lock.lock();
			try {
				BlockingQueue<CandidateURI> linksQueue = outLinksQueue
						.get(seedIndex.index);
				linksQueue.addAll(uris);
				seedIndex.outLinksSize = linksQueue.size();
			} finally {
				lock.unlock();
			}
		}
	}

	/**
	 * 初始化队列外链队列
	 * @throws InterruptedException 
	 */
	public void initialWorkQueue(List<SeedTask> seedTasks) throws InterruptedException {
		lock.lock();
		try {
			outLinksQueue = Lists.newArrayList();
			seedIndexMap = Maps.newLinkedHashMap();
			int i = 0;
			for (SeedTask seed : seedTasks) {
				seedIndexMap.put(seed.getSeedFingerprint(), new SeedIndex(i));
				BlockingQueue<CandidateURI> queue = this.getQueueInstance();
				queue.put(new CandidateURI(seed));
				outLinksQueue.add(queue);
				i++;
			}
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 新增一个种子外链队列（顺序）
	 * 
	 * @param task
	 */
	public void addTaskSeed(SeedTask task) {
		Preconditions.checkArgument(outLinksQueue != null, "workQueue还没有初始化");
		lock.lock();
		try {
			this.seedIndexMap.put(task.getSeedFingerprint(), new SeedIndex(
					this.seedIndexMap.size()));
			outLinksQueue.add(this.getQueueInstance());
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 删除一个种子的外链队列
	 * 
	 * @param task
	 */
	public void removeTask(SeedTask task) {
		Preconditions.checkArgument(outLinksQueue != null, "workQueue还没有初始化");
		lock.lock();
		try {
			int index = seedIndexMap.get(task.getSeedFingerprint()).index;
			Iterator<String> it = seedIndexMap.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				boolean hasFind = false;
				// 找到待删除任务的索引
				if (!hasFind&&key.equals(task.getSeedFingerprint())) {
					hasFind = true;
					key = it.next();
				}
				if (hasFind && key != null) {// 后面的任务索引迁移操作
					seedIndexMap.get(key).index += 1;
				}
			}
			outLinksQueue.remove(index);
			// index后的索引都需要改变索引值（前移操作）
			seedIndexMap.remove(task.getSeedFingerprint());
		} finally {
			lock.unlock();
		}
	}

	/**
	 * 根据配置的队列类型和大小，实例化一个可用的链接队列
	 * 
	 * @return
	 */
	private BlockingQueue getQueueInstance() {
		if (this.queueType.getValue().equals("array")) {
			return new ArrayBlockingQueue<CandidateURI>(this.queueSize);
		} else if (this.queueType.getValue().equals("linked")) {
			return new LinkedBlockingQueue<CandidateURI>(this.queueSize);
		} else {
			return new PriorityBlockingQueue<CandidateURI>(this.queueSize);
		}
	}

	// //////////实例化构造////
	private QueueType queueType;
	private int queueSize = Integer.MAX_VALUE;
	private long timeout = 1000;// 默认队列阻塞时长，，ms

	/**
	 * 外链队列支持的队列的类型
	 * 
	 * @author pery
	 * 
	 */
	public enum QueueType {
		array("数组阻塞队列") {
		},
		linked("链表阻塞队列") {
		},
		priority("优先阻塞队列") {
		};

		private String value;

		private QueueType(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}
	}

	public QueueType getQueueType() {
		return queueType;
	}

	public void setQueueType(QueueType queueType) {
		this.queueType = queueType;
	}

	public int getQueueSize() {
		return queueSize;
	}

	public void setQueueSize(int queueSize) {
		this.queueSize = queueSize;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

}

/**
 * 种子队列快查表内容
 * 
 * @author pery
 * 
 */
class SeedIndex {
	public SeedIndex(int index) {
		this.index = index;
	}

	// 其外链队列索引
	int index;
	// 剩余外链大小
	int outLinksSize;
	// 上次取出外链时间
	Calendar lastTakeTime;
	// 是否跳过扫描其下队列的外链
	boolean isSkip = false;
}