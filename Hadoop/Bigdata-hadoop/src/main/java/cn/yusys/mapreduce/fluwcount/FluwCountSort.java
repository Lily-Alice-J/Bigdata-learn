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
 * �����Ѿ��ֲ�ʽ������û���������������
 * @author Administrator
 *
 */
public class FluwCountSort {
	/**
	 * Text : 13502468823     7335    110349  117684
	 * FluwBean : ������
	 * Text : �ֻ���
	 * @author Administrator
	 */
    static class FluwCountSortMappper extends Mapper<LongWritable, Text, FluwBean, Text>{
    	FluwBean bean = new FluwBean();
    	Text v = new Text();
        @Override
    	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, FluwBean, Text>.Context context)
    			throws IOException, InterruptedException {
    		String line = value.toString();
    		String[] fields = line.split("\t");
    		// �ֻ���
    		String phoneNbr = fields[0];
    		// �������� ��������
    		long upFluw = Long.parseLong(fields[1]);
    		long downFluw = Long.parseLong(fields[2]);
    		bean.set(upFluw, downFluw);
    		v.set(phoneNbr);
    		// hadoop mr��ܸ���key�Զ�����(��ʵ������ӿ�)
    		context.write(bean, v);
    	}
    }
    
    /**
     * ��� : ����ʵ���� �ֻ���
     * д�� : �ֻ��� ����ʵ���� 
     * @author Administrator
     *
     */
    static class FluwCountSortReduce extends Reducer<FluwBean, Text, Text, FluwBean>{
    	  @Override
    	protected void reduce(FluwBean bean, Iterable<Text> values, Reducer<FluwBean, Text, Text, FluwBean>.Context context)
    			throws IOException, InterruptedException {
    		  // �������ֻ���Ϊkey ����beanΪvalue ���
    		 context.write(values.iterator().next(), bean);
    	}
    }
    /**
     * mr���������
     * @param args
     * @throws IOException 
     * @throws InterruptedException 
     * @throws ClassNotFoundException 
     */
	public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
		Configuration conf = new Configuration();
		Job wcjob = Job.getInstance(conf);
		//ָ�������job���ڵ�jar��
//		wcjob.setJar("/home/hadoop/wordcount.jar");
		wcjob.setJarByClass(FluwCountSort.class);
		
		wcjob.setMapperClass(FluwCountSortMappper.class);
		wcjob.setReducerClass(FluwCountSortReduce.class);
		//�������ǵ�ҵ���߼�Mapper������key��value����������
		wcjob.setMapOutputKeyClass(FluwBean.class);
		wcjob.setMapOutputValueClass(Text.class);
		//�������ǵ�ҵ���߼�Reducer������key��value����������
		wcjob.setOutputKeyClass(Text.class);
		wcjob.setOutputValueClass(FluwBean.class);
		//ָ��Ҫ������������ڵ�λ��
		FileInputFormat.setInputPaths(wcjob, new Path(args[0]));
		//ָ���������֮��Ľ���������λ��
		FileOutputFormat.setOutputPath(wcjob, new Path(args[1]));
		
		//��yarn��Ⱥ�ύ���job
		boolean res = wcjob.waitForCompletion(true);
		System.exit(res?0:1);
	}

}
