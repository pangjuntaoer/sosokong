package cn.edu.swust.uri;

import java.io.Serializable;

import com.google.common.base.Preconditions;

public class CandidateURI implements Comparable<CandidateURI>,Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 该外链所属的任务种子；
	 * 必须字段，此字段是
	 */
	private SeedTask seedTask;
	private boolean isSeed=false;
	private String candidateURI;
	private String reference;
	protected CandidateURI(){}
	/**
	 * 种子转换为CandidateURI
	 * @param seedTask
	 */
	public CandidateURI(SeedTask seedTask){
		this.isSeed=true;
		this.seedTask=seedTask;
		this.candidateURI=seedTask.getSeedUrl();
	}
	public CandidateURI(SeedTask seedTask,String url,String reference){
		this.seedTask = seedTask;
		this.candidateURI = url;
		this.reference = reference;
	}
	/**
	 * 拷贝对象值
	 * @param candidateURI2
	 */
	public CandidateURI(CandidateURI candidateURI) {
		this.seedTask = candidateURI.getSeedTask();
		this.isSeed = candidateURI.getIsSeed();
		this.candidateURI = candidateURI.getCandidateURI();
		this.reference = candidateURI.getReference();
	}
	/**
	 * 获得该候选URI所属的种子任务
	 * @return SeedTask 种子任务
	 */
	public SeedTask getSeedTask() {
	Preconditions.checkArgument(this.seedTask!=null,
				"错误：该CandidateURI所属种子任务为空了！");
		return seedTask;
	}
	public void setSeedTask(SeedTask seedTask) {
		this.seedTask = seedTask;
	}

	public boolean getIsSeed() {
		return isSeed;
	}

	public void setIsSeed(boolean isSeed) {
		this.isSeed = isSeed;
	}
	public String getCandidateURI() {
		return candidateURI;
	}
	public void setCandidateURI(String outLinks) {
		this.candidateURI = outLinks;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	////////////////////////////////////////////////////////
	
	///////////////////////////////////////////////////////
	/**
	 * 外链的在队列中排序规则
	 */
	public int compareTo(CandidateURI o) {
		if(this.getCandidateURI().contains("search")){
			return -1;
		}
		return 0;
	}
}
