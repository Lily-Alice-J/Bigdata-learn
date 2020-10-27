package cn.yusys.mapreduce.fluwcount;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * ͳ���û��������� ���������ֲ�ʽ����
 * @author Administrator
 */
public class FluwCount {
	/**
	 * Text: ��ǰ�û��ֻ���
	 * FlueBean: ��װ���������� �������� ʵ����(ʵ��hadoop���л��ӿ�)
	 * @author Administrator
	 */
	static class FluwCountMapper extends Mapper<LongWritable, Text, Text, FluwBean>{
		Text k = new Text();
		FluwBean fluwBean = new FluwBean();
		@Override
		protected void map(LongWritable key, Text value, Context context)
				throws IOException, InterruptedException {
			// ��HDFS�н�ȡ��ǰ�û��ֻ��� �������� ��������
			 String line = value.toString();
			 String[] fields = line.split("\t");
			 // �ֻ���
			 String phoneNbr = fields[1];
	         // �������� ��������
			 long upFluw = Long.parseLong(fields[fields.length - 3]);
			 long downFluw = Long.parseLong(fields[fields.length - 2]); 
			 /**
			  * д������reduce����
			  * key : �û��ֻ���
			  * value : �������� �������� ʵ����
			  */
			 k.set(phoneNbr);
			 fluwBean.set(upFluw, downFluw);
			 context.write(k,fluwBean);
		}
	}
	/**
	 * @author Administrator
	 *  ��� : �û��ֻ���   �������� �������� ʵ����
	 *  ��� : �û��ֻ���    ��������
	 */
   static class FluwCountReduce extends Reducer<Text, FluwBean, Text, FluwBean>{
	   FluwBean resultFluwBean = new FluwBean();
	   @Override
	   protected void reduce(Text key, Iterable<FluwBean> values,
			Context context) throws IOException, InterruptedException {
		   long sum_upFluw = 0;
		   long sum_downFluw = 0;
		   //  ͬһ�绰���� ����reduce���� ���������������������
		   for (FluwBean fluwBean : values) {
			 sum_upFluw += fluwBean.getUpFluw();
			 sum_downFluw = fluwBean.getDownFluw();
		}
		   resultFluwBean.set(sum_upFluw, sum_downFluw);
		   context.write(key, resultFluwBean);
	 }
   }
   
   public static void main(String[] args) throws Exception {
	   Configuration conf = new Configuration();
		Job wcjob = Job.getInstance(conf);
		// ָ�������job���ڵ�jar��
        // wcjob.setJar("/home/hadoop/wordcount.jar");
		wcjob.setJarByClass(FluwCount.class);
		wcjob.setMapperClass(FluwCountMapper.class);
		wcjob.setReducerClass(FluwCountReduce.class);
		// �����Զ������㷨(�Զ����߼�������̳�Partitioner)
		wcjob.setPartitionerClass(ProvincePartitioner.class);
//		// ���÷ֲ�ʽ��������� ��ʱ�ͻ�����������ͳ���ļ�part-0000  00001  00002......
//		wcjob.setNumReduceTasks(5);
		// �������ǵ�ҵ���߼�Mapper������key��value����������
		wcjob.setMapOutputKeyClass(Text.class);
		wcjob.setMapOutputValueClass(FluwBean.class);
		// �������ǵ�ҵ���߼�Reducer������key��value����������
		wcjob.setOutputKeyClass(Text.class);
		wcjob.setOutputValueClass(FluwBean.class);
		// ָ��Ҫ������������ڵ�λ��
		FileInputFormat.setInputPaths(wcjob, new Path(args[0]));
		// ָ���������֮��Ľ���������λ��
		FileOutputFormat.setOutputPath(wcjob, new Path(args[1]));
		// ��yarn��Ⱥ�ύ���job
		boolean res = wcjob.waitForCompletion(true);
		System.exit(res?0:1);
  }
}
