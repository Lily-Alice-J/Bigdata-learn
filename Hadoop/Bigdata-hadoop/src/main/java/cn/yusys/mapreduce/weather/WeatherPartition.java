package cn.yusys.mapreduce.weather;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Partitioner;
/**
 * 
 * @author Tangzhi mail:tangzhi8023@gmail.com
 * @description: map�׶η���
 * @date 2019��9��1��
 */
public class WeatherPartition extends Partitioner<WeatherBean,IntWritable>{
	@Override
	public int getPartition(WeatherBean key, IntWritable value, int numPartitioners) {
		return key.getYear() % numPartitioners;
	}

}
