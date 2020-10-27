package cn.yusys.mapreduce.workcount;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * mapreduce����
 * @author Administrator
 *
 */
public class WordCountRunner {

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		Job wcjob = Job.getInstance(conf);
		wcjob.setJobName("WordCount");
		//ָ��job���ڵ�jar��
//		wcjob.setJar("/home/hadoop/wordcount.jar");
		wcjob.setJarByClass(WordCountRunner.class);
		
		wcjob.setMapperClass(WordcountMapper.class);
		wcjob.setReducerClass(WordcountReducer.class);
		//�������ǵ�ҵ���߼�Mapper������key��value����������
		wcjob.setMapOutputKeyClass(Text.class);
		wcjob.setMapOutputValueClass(IntWritable.class);
		//�������ǵ�ҵ���߼�Reducer������key��value����������
		wcjob.setOutputKeyClass(Text.class);
		wcjob.setOutputValueClass(IntWritable.class);
		// ָ��Combiner(Ӧ����ҵ���߼�ʹ��)
		wcjob.setCombinerClass(WordcountReducer.class);
		//ָ��Ҫ������������ڵ�λ��
		FileInputFormat.setInputPaths(wcjob, new Path(args[0]));
		//ָ���������֮��Ľ���������λ��
		FileOutputFormat.setOutputPath(wcjob, new Path(args[1]));
		//��yarn��Ⱥ�ύ���job
		boolean res = wcjob.waitForCompletion(true);
		System.exit(res?0:1);
	}
}
