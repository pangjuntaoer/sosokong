package cn.edu.swust.seed.invoke;

import java.util.List;

import cn.edu.swust.uri.SeedTask;

/**
 * 默认种子注入（通过Spring配置文件自动注入）
 * @author pery
 *
 */
public class DefaultSeedInject extends SeedInject{
private List<SeedTask> seedTasks;
	@Override
	public List<SeedTask> loadSeedTasks() {
		return seedTasks;
	}
	public List<SeedTask> getSeedTasks() {
		return seedTasks;
	}
	public void setSeedTasks(List<SeedTask> seedTasks) {
		this.seedTasks = seedTasks;
	}

}
