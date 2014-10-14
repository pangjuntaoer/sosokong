package cn.edu.swust.uri;

import org.springframework.scheduling.config.Task;

import com.google.common.base.Preconditions;

public class CandidateURI implements Comparable<CandidateURI>{
	/**
	 * 该外链所属的任务种子；
	 * 必须字段，此字段是
	 */
	private SeedTask seedTask;
	private boolean isSeed=false;
	private String candidateURI;
	/**
	 * 种子转换为CandidateURI
	 * @param seedTask
	 */
	public CandidateURI(SeedTask seedTask){
		this.isSeed=true;
		this.seedTask=seedTask;
		this.candidateURI=seedTask.getSeedUrl();
	}
	public SeedTask getSeedTask() {
	Preconditions.checkArgument(this.seedTask!=null,
				"错误：该CandidateURI所属种子任务为空了！");
		return seedTask;
	}
	/**
	 * 默认种子排序
	 */
	public int compareTo(CandidateURI o) {
		return 0;
	}
	public void setSeedTask(SeedTask seedTask) {
		this.seedTask = seedTask;
	}

	public boolean isSeed() {
		return isSeed;
	}

	public void setSeed(boolean isSeed) {
		this.isSeed = isSeed;
	}
	public String getCandidateURI() {
		return candidateURI;
	}
	public void setCandidateURI(String outLinks) {
		this.candidateURI = outLinks;
	}
	
}
