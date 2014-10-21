package cn.edu.swust.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
public class BackupHandle {
	private static Log logger = LogFactory.getLog(BackupHandle.class);
	private String backPath="backup.back";
	@Value("backup.back")
	public void setBackPath(String backPath) {
		this.backPath = backPath;
	}
	/**
	 * 备份一个对象数组
	 * @param objects
	 */
	public synchronized void backUpData(Object []objects){
		ObjectOutputStream os = null;
		try {
			 os = new ObjectOutputStream(new FileOutputStream(backPath));
			 os.writeObject(objects);
			 os.flush();
			logger.info("BackUpData,备份"+objects.getClass()+" 对象数据成功");
		} catch (FileNotFoundException e) {
			logger.info("BackUpData 备份文件不存在！"+e.getMessage());
		} catch (IOException e) {
			logger.info("BackUpData 备份文件写入错误！"+e.getMessage());
		}finally{
			try {
				os.close();
			} catch (IOException e) {
				logger.info("BackUpData IOE 备份时关闭备份文件错误！");
			}
		}
	}

	public synchronized Object recoverOneObject(Class classType){
		Object[] objs = this.recoverBackUp();
		Object result = null;
		try {
			Object targetObj = classType.newInstance();
			for (int i = 0; i < objs.length; i++) {
				Object o = objs[i];
				if(o.getClass().isInstance(targetObj)){
					result = o;
					break;
				}
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}finally{
			return result;
		}
	}
	/**
	 * 从序列化备份中读取对象数组
	 * @return
	 */
	public Object[] recoverBackUp(){
		ObjectInputStream oi = null;
		Object[] result=null;
		try {
			 oi = new ObjectInputStream(new FileInputStream(backPath));
			 result = (Object[]) oi.readObject();
		} catch (FileNotFoundException e) {
			logger.info("RecoverbackUp 恢复时备份文件不存在！");
		} catch (IOException e) {
			logger.info("RecoverbackUp IOE 恢复时打开备份文件，读取错误！");
		} catch (ClassNotFoundException e) {
			logger.info("RecoverbackUp 恢复时，未找到待恢复对象类：！");
		}finally{
			try {
				if(oi!=null)
				oi.close();
			} catch (IOException e) {
				logger.info("RecoverbackUp IOE 恢复时关闭备份文件错误！");
			}
			return result;
		}
	}
}
