package cn.edu.swust.core;
/**
 * 对象管理池的取值方式
 * @author pery
 */
public interface ManagePoolMethod {
/**
 * 依次获得对象使用（独占）	
 */
public static String ORDERING="order"; 
/**
 * 共享使用(无限制)
 */
public static String SHARE="share";
/**
 * 共享使用，但是使用次数有限制
 */
public static String SHARELIMIE="sharelimit";
/**
 * 按时间取obj
 */
public static String TIMELIMITE="timelimite";
}
