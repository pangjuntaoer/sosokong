package cn.edu.swust.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;
/**
 * 对象管理池
 * @author pery
 *
 * @param <E>
 */
public class ManagePool <E>{
	private final static int DEFAULT_SIZE=5;
	private final static int DEFAULT_INTERVAL=500;
	private final static int HASUSERING=1;
	private final static int NOTUSER=0;
	//对象列表
	private List<E> list;
	//对象当前状态
	private int objectNowStatus[];
	//对象使用次数
	private int objectUsedCount[];
	//取值方式
	private String getObjMethod=ManagePoolMethod.ORDERING;
	//上次取到值的索引
	private int lastGetIndex;
	/**
	 * 当取obj为次数限制时候，表示最大使用次数，
	 * 为时间间隔限制时候表示相隔ms
	 */
	private int maxInterval;
	/**
	 * 上次取某个对象的时间
	 * (按时间取值，暂时未实现)
	 */
	private Calendar[] lastGetTime;
	/**
	 * 设置取obj限制条件
	 * @param interval
	 */
	public void setLimite(int interval){
		this.maxInterval = interval;
	}
	public void setNewObjMethod(String newMethod){
		this.getObjMethod = newMethod;
	}
	
	public ManagePool(String getObjMethod){
		this(DEFAULT_SIZE,getObjMethod);
	}
	
	public ManagePool(int initialCapacity, String getObjMethod){
		list = new ArrayList<E>(initialCapacity);
		this.getObjMethod = getObjMethod;
		objectNowStatus = new int[initialCapacity];
	}
	public ManagePool(List<E> objList,String getObjMethod){
		list = new ArrayList<E>(objList);
		this.getObjMethod = getObjMethod;
		objectNowStatus = new int[objList.size()];
	}
	
	public E nextObj(){
		if(this.getObjMethod.equals(ManagePoolMethod.ORDERING)){
			return nextObjByOrdering();
		}else if(this.getObjMethod.equals(
				ManagePoolMethod.SHARELIMIE)){
			return nextObjByShareLimite();
		}else{//其他类型暂时没有实现
			return null;
		}
	}
	
	private E nextObjByOrdering(){
		if(list==null||list.size()<=0){
			return null;
		}
		while(true){
			int nowIndex=(lastGetIndex+1)%list.size();
			if(objectNowStatus[nowIndex]==NOTUSER){
				objectNowStatus[nowIndex] = HASUSERING;
				objectUsedCount[nowIndex]+=1;
				return list.get(nowIndex);
			}else{
				lastGetIndex=nowIndex;
			}
		}
	}
	private E nextObjByShareLimite(){
		return null;
	}
	public static void main(String[] args) {
		int s[]=new int[4];
		for (int i = 0; i < s.length; i++) {
			System.out.println(s[i]);
		}
	}
}
