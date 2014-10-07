package cn.edu.swust.processor;

import cn.edu.swust.uri.CandidateURI;

public abstract class Processor {

	public abstract ProcessResult process(CandidateURI candidateURI);
	
	public abstract void afterProcess(CandidateURI candidateURI);
}
