package cn.edu.swust.core;

import java.util.TimerTask;
/**
 * 备份控制器
 * 2014年10月21日12:16:14
 * @author pery
 */
public class BackupController extends TimerTask implements Runnable{
	private BackupHandle backupHandle;
	private Object [] objects;
	public BackupController (Object [] objects,BackupHandle backupHandle){
		this.backupHandle = backupHandle;
		this.objects = objects;
	}
	public void run() {
	 this.backupHandle.backUpData(objects);
	}
}
