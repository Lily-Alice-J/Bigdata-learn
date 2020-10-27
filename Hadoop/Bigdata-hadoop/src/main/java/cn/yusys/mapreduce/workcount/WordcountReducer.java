package cn.yusys.mapreduce.workcount;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * hadoop ����mapreduce�ֲ�ʽ��������ͳ�ƶ���ļ���ÿ�����ʳ��ֵĴ���
 * @author Administrator
 *
 */
/**
 * KEYIN VALUEIN : ��Ӧmap��context�����Text IntWritable
 * KEYOUT, VALUEOUT : ���� �Լ����ִ��� Text IntWritable
 * @author Administrator
 *
 */
public class WordcountReducer extends Reducer<Text, IntWritable, Text, IntWritable>{
	@Override
	protected void reduce(Text key, Iterable<IntWritable> values,
			Context context) throws IOException, InterruptedException {
		/**
		 * key ��һ����ͬ����  values ����������� <kobe,1> <kobe,1> <kobe,1>.....
		 */
		// ͳ��һ�鵥�ʵĳ��ִ���
		int count = 0;
		Iterator<IntWritable> iterator = values.iterator();
		while(iterator.hasNext()){
			count += iterator.next().get();
		}
		/*for (IntWritable value : values) {
			count += value.get();
		}*/
		// mr���д��
		context.write(key, new IntWritable(count));
	}
	
}
