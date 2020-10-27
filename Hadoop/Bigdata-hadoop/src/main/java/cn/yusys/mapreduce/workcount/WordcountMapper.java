package cn.yusys.mapreduce.workcount;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * hadoop ����mapreduce�ֲ�ʽ��������ͳ�ƶ�����ļ���ÿ�����ʳ��ֵĴ���
 * @author Administrator
 *
 */
/**
 * KEYIN:Ĭ�������:��mr��ܶ�����һ�����ݵ���ʼƫ����   Long ��hadoop ���б�Serializable ��Ϊ��������л��ӿ� LongWritable
 * VALUEIN:Ĭ������� : ��mr��ܶ�����һ���ı����� String ͬ�� Text���кŽӿ�
 * KEYOUT:Ĭ�������:��mr��ܾ����ֲ�ʽ����������key ���� String
 * VALUEOUT:Ĭ�������:��mr��ܾ����ֲ�ʽ����������value ÿ�����ʳ��ֵĴ��� Ieteger ͬ�� IntWritable
 * @author Administrator
 * ����LongWritable:�к�
 * ����Text:һ������
 * ���Text:����
 * ���IntWritable:���ʸ���
 */
public class WordcountMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
   Text k = new Text();
   IntWritable v = new IntWritable(1);
   @Override
   protected void map(LongWritable key, Text value,Context context)
		throws IOException, InterruptedException {
	    // 1.��valueת�����ַ���
	   String str = value.toString();
	   // 2.���ÿո��и�
	   String[] words = str.split(" ");
	   // 3.ѭ������Context��
	   for (String word : words) {
		   // ����:1 ��ֵ�Ե���ʽд�뵽context��
		   k.set(word);
		   context.write(k,v);
	}
 }
}
