package cn.yusys.mapreduce.inverindex;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
/**
 * ��������ʵ��
 * @author Administrator
 *
 */
public class Inverindex {
    static class InverindexMapper extends Mapper<LongWritable, Text, Text, IntWritable>{
    	Text k = new Text();
    	IntWritable v = new IntWritable(1);
    	@Override
    	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, IntWritable>.Context context)
    			throws IOException, InterruptedException {
    		  // �ı���ȡ
    		String line = value.toString();
    		String[] worlds = line.split(" ");
    		 // �õ��ļ���
    		 FileSplit inputSplit = (FileSplit) context.getInputSplit();
    		 String name = inputSplit.getPath().getName();
    		 // д����reduce����
    		for (String world : worlds) {
				 k.set(world + "--" + name);
				 context.write(k, v);
			}
    	}
    }
    static class InverindexReduce extends Reducer<Text, IntWritable, Text, IntWritable>{
    	  @Override
    	protected void reduce(Text key, Iterable<IntWritable> values,
    			Reducer<Text, IntWritable, Text, IntWritable>.Context context) throws IOException, InterruptedException {
    		  // ���зֲ�ʽ����
    		  int count = 0;
    		  for (IntWritable value : values) {
				  count += value.get();
			}
    		  context.write(key, new IntWritable(count));
    	}
    }
	public static void main(String[] args) throws Exception {
		   Configuration conf = new Configuration();
			Job wcjob = Job.getInstance(conf);
			// ָ�������job���ڵ�jar��
	        // wcjob.setJar("/home/hadoop/wordcount.jar");
			wcjob.setJarByClass(Inverindex.class);
			wcjob.setMapperClass(InverindexMapper.class);
			wcjob.setReducerClass(InverindexReduce.class);
			// �����Զ������㷨
			// �������ǵ�ҵ���߼�Mapper������key��value����������
			wcjob.setMapOutputKeyClass(Text.class);
			wcjob.setMapOutputValueClass(IntWritable.class);
			// �������ǵ�ҵ���߼�Reducer������key��value����������
			wcjob.setOutputKeyClass(Text.class);
			wcjob.setOutputValueClass(IntWritable.class);
			// ָ��Ҫ������������ڵ�λ��
			FileInputFormat.setInputPaths(wcjob, new Path(args[0]));
			// ָ���������֮��Ľ���������λ��
			FileOutputFormat.setOutputPath(wcjob, new Path(args[1]));
			// ��yarn��Ⱥ�ύ���job
			boolean res = wcjob.waitForCompletion(true);
			System.exit(res?0:1);
	}

}
