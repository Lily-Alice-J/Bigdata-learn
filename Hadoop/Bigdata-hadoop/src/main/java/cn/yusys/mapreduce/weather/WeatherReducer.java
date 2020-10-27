package cn.yusys.mapreduce.weather;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * @author Tangzhi mail:tangzhi8023@gmail.com
 * @description:�������� Reduce�׶�
 * @date 2019��9��1��
 */
public class WeatherReducer extends Reducer<WeatherBean, IntWritable, Text, IntWritable>{
           Text rkey = new Text();  
           IntWritable rval = new IntWritable();
	       @Override
            protected void reduce(WeatherBean key, Iterable<IntWritable> values,Context context)
            		throws IOException, InterruptedException {
            	   //��ͬ��keyΪһ�飬����һ��reduce����
            	int flg = 0;
           		int day = 0;
           		for (IntWritable v : values) {
           			//�¶ȵ�һ��
           			if(flg == 0){
           				day = key.getDay();	
           				rkey.set(key.getYear()+"-"+key.getMonth()+"-"+key.getDay());
           				rval.set(key.getTemperature());
           				context.write(rkey,rval);
           				flg ++;
           				
           			}
           			//�¶ȵڶ���
           			if(flg != 0 && day != key.getDay()){
           				rkey.set(key.getYear()+"-"+key.getMonth()+"-"+key.getDay());
           				rval.set(key.getTemperature());
           				context.write(rkey,rval);
           				break;
           			}
            	   
            }
     }
}
