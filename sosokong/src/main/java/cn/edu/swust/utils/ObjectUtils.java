package cn.edu.swust.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ObjectUtils {
/**
 * 父类向子类拷贝属性值	
 * @param father
 * @param child
 * @throws Exception
 */
	public static void fatherObject2ChildObject(Object father,Object child) throws Exception{
		if(!(child.getClass().getSuperclass()==father.getClass())){
			throw new Exception("child不是father的子类");
		}
		Class fatherClass = father.getClass();
		Class childClass = child.getClass();
		Field ff[] = fatherClass.getDeclaredFields();
		for (int i = 0; i < ff.length; i++) {
			Field f=ff[i];
			Class type = f.getType();
			Method m = fatherClass.getMethod("get"+upperHeadChar(f.getName()));
			Object obj = m.invoke(father);
			//f.set(child, obj);
			Method m2 = childClass.getMethod("set" + upperHeadChar(f.getName()),type);
			m2.invoke(child, new Object[]{obj}) ;//设置子类值
		}
		
	}
	/** 
     * 首字母大写，in:deleteDate，out:DeleteDate 
     */  
    private static String upperHeadChar(String in){  
        String head=in.substring(0,1);  
        String out=head.toUpperCase()+in.substring(1,in.length());  
        return out;  
    } 
}
