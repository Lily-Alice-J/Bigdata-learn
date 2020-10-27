package cn.yusys.mapreduce.weather;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

/**
 * 
 * @author Tangzhi mail:tangzhi8023@gmail.com
 * @description:Reducer�׶εķ��������㷨��������д�����Ĭ�ϻ����Map�׶ε������㷨
 * @date 2019��9��1��
 */
public class WeatherGroupingComparator extends WritableComparator{
          public WeatherGroupingComparator() {
        	  super(WeatherBean.class,true);
		}
          @Override
        public int compare(WritableComparable a, WritableComparable b) {
        	  WeatherBean w1 = (WeatherBean) a;
              WeatherBean w2 = (WeatherBean) b;
              // �����ꡢ�±Ƚ�
              int res1 = Integer.compare(w1.getYear(), w2.getYear());
              if (res1 == 0) {
				 return Integer.compare(w1.getMonth(), w2.getMonth());
			}
              return res1;
        }
}
