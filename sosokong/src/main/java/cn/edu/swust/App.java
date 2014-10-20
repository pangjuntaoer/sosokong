package cn.edu.swust;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.edu.swust.core.ExecuteService;

/**
 * Hello world!
 * 
 */
public class App {
	public static void main(String[] args) {
		 String xmlPath[]=new String[]{"ApplicationContext.xml"};
		 ApplicationContext ctx = new ClassPathXmlApplicationContext(xmlPath);
		 System.out.println("配置文件载入完成OK"); ExecuteService exe =
		 ctx.getBean(ExecuteService.class); exe.runAPP();
		//test1();
	}
	static void test(){
		String regex="(http://www.dianping.com/search/category/8/10/.*)|(http://www.dianping.com/shop/\\d+/review_all.*)";
		Pattern pt = Pattern.compile(regex);
		Matcher mt = pt.matcher("http://www.dianping.com/shop/530643/review_all?pageno=2");
		System.out.println("=:"+mt.find());
	}
	static void test1() {
		String hrefValue = "http://www.dianping.com/search/category/8/10/g117q666#breadCrumb?";
		String regex = "#\\w*;?";
		Pattern pt = Pattern.compile(regex);
		Matcher mt = pt.matcher(regex);
	/*	while(mt.find()){
			System.out.println("="+mt.group(1));
		}*/
		System.out.println(hrefValue.replaceAll(regex, ""));
		
	}

	static void test2() {
		String rateRegex = "^[0-9]\\.[0-9]$.*?";
		Pattern pt = Pattern.compile(rateRegex);
		String source = "口味7.8   环境8.5   服务7.9";
		Matcher mt = pt.matcher(source);
		while (mt.find()) {
			String s = mt.group();
			System.out.println(Float.parseFloat(s));
		}
		/*
		 * String str []= source.split("\\s"); String tastStr =
		 * MyStringUtils.replaceBlank(str[0].substring(2)); String eveStr =
		 * MyStringUtils.replaceBlank(str[1].substring(2)); String serStr =
		 * MyStringUtils.replaceBlank(str[2].substring(2));
		 * System.out.println(Float.parseFloat(tastStr));
		 */
	}
}
