package cn.edu.swust.processor.extract;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

import cn.edu.swust.processor.Processor;
import cn.edu.swust.uri.CrawlURI;
import cn.edu.swust.uri.SeedTask;

public abstract class AbstractExtractorProcessor extends Processor{
	
	final static String REGEX="<[aA]\\s.*?href=[\"\'\\s]*([^\"\'\\s]+)[\"\'\\s]*[^>]*>";
	static Pattern pt = Pattern.compile(REGEX);
	
	public void innerExtractOutLink(CrawlURI crawlURI) throws MalformedURLException{
    	Matcher mt = pt.matcher(crawlURI.getContent());
    	SeedTask seed = crawlURI.getSeedTask();
    	Pattern remainUriRegex =  Pattern.compile(seed.getOutLinksRegex());
    	//绝对路径
    	URL absoluteUrl = new URL(crawlURI.getCandidateURI());
    	while(mt.find()){
    		String hrefValue = mt.group(1);
    		if(!hrefValue.startsWith("http")&&hrefValue.contains("/")){
    			try {
    				hrefValue = new URL(absoluteUrl ,hrefValue).toString();
				} catch (Exception e) {
					e.printStackTrace();
					log.error("outLink extract Excetprion[1]"+hrefValue);
				}
    		}
    		Matcher outLinkMt= remainUriRegex.matcher(hrefValue);
    		if(outLinkMt.find()){
    			crawlURI.addOneOutLink(hrefValue);
    		}
    	}
    };
    
}
