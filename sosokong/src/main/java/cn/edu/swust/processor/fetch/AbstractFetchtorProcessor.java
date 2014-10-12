package cn.edu.swust.processor.fetch;

import cn.edu.swust.processor.ProcessResult;
import cn.edu.swust.processor.Processor;
import cn.edu.swust.uri.CandidateURI;

public abstract class AbstractFetchtorProcessor extends Processor{

	public abstract ProcessResult process(CandidateURI candidateURI);
	
	public abstract void afterProcess(CandidateURI candidateURI);
}
