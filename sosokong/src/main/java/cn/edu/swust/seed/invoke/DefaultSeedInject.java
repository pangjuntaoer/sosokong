package cn.edu.swust.seed.invoke;

import java.util.ArrayList;
import java.util.List;

import cn.edu.swust.uri.SeedTask;

/**
 * 默认种子注入（通过Spring配置文件自动注入）
 * @author pery
 *
 */
public class DefaultSeedInject extends SeedInject{
private ArrayList<SeedTask> seedTasks;
	@Override
	public ArrayList<SeedTask> loadSeedTasks() {
		return seedTasks;
	}
	public ArrayList<SeedTask> getSeedTasks() {
		return seedTasks;
	}
	public void setSeedTasks(ArrayList<SeedTask> seedTasks) {
		this.seedTasks = seedTasks;
	}

}
