package cn.edu.swust.seed.invoke;

import java.util.List;

import cn.edu.swust.uri.SeedTask;

/**
 * 种子注入方式
 * @author pery
 *2014年10月08日20:14:33
 */
public abstract class SeedInject {
/**
 * 载入种子的方式，自己实现，从数据库还是配置文件
 * @return
 */
public abstract List<SeedTask> loadSeedTasks();

}
