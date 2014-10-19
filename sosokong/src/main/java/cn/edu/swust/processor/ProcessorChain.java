package cn.edu.swust.processor;

import org.apache.commons.collections.functors.WhileClosure;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.edu.swust.uri.CandidateURI;
import cn.edu.swust.uri.CrawlURI;
import cn.edu.swust.utils.ObjectUtils;

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

public void beginProcess(CandidateURI outLink){
	try {
		CrawlURI uri = new CrawlURI(outLink);
/*		////////////////
		String outLinks="http://www.dianping.com/shop/530643/review_all?pageno=1";
		uri.setCandidateURI(outLinks);
		/////////////////////
*/		ProcessResult fr = this.fetchProcessor.process(uri);
		if(fr.equals(ProcessResult.PROCEED)){
			ProcessResult er = this.extractProcessor.process(uri);
			if(er.equals(ProcessResult.PROCEED)){
				ProcessResult wr = this.writerProcessor.process(uri);
				ProcessResult pr = this.postLinksProcessor.process(uri);
			}
		}else if(fr.equals(ProcessResult.FINISH)){
			this.postLinksProcessor.process(uri);
		}
		
	} catch (Exception e) {
		e.printStackTrace();
	}
}
}
