package cn.yusys.hive;

import java.util.HashMap;

import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * @author Tangzhi mail:tangzhi8023@gmail.com
 * @description:hive�Զ��庯��
 * @date 2018��10��21��
 */
public class ToUpper extends UDF{
	public static HashMap<String, String> proviceMap = new HashMap<String, String>();
	static {
		proviceMap.put("136", "hunan");
		proviceMap.put("137", "beijing");
		proviceMap.put("138", "guangzhou");
	}
     /**
      * ʵ��hive����Сдת��д
      */
	public String evaluate(String s){
		String result = s.toUpperCase();
		return result;
	}
	/**
	 * ʵ���ֻ���������ص���ϵ
	 */
	public String evaluat(int phonenbr) {
		    String phone = String.valueOf(phonenbr);
	        return proviceMap.get(phone.substring(0, 3)) == null?"chawucidi":proviceMap.get(phone.substring(0,3));
	}
}
