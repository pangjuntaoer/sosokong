package cn.edu.swust.processor;

import org.apache.commons.collections.functors.WhileClosure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.edu.swust.uri.CandidateURI;

@Component
public class ProcessorChain {
@Autowired
private Processor fetchProcessor;
@Autowired
private Processor extractProcessor;
@Autowired
private Processor writerProcessor;
@Autowired
private Processor postLinksProcessor;

public void beginProcess(CandidateURI uri){
	this.fetchProcessor.process(uri);
	this.extractProcessor.process(uri);
	this.writerProcessor.process(uri);
	this.postLinksProcessor.process(uri);
}
}
