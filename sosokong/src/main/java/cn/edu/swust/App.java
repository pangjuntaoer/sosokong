package cn.edu.swust;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import cn.edu.swust.core.ExecuteService;
/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	String xmlPath[]=new String[]{"ApplicationContext.xml"};
		ApplicationContext ctx = new ClassPathXmlApplicationContext(xmlPath);
		System.out.println("配置文件载入完成OK");
		ExecuteService exe = ctx.getBean(ExecuteService.class);
		exe.runAPP();
    }
}
