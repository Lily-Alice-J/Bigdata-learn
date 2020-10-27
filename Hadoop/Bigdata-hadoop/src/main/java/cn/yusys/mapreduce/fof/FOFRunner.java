package cn.yusys.mapreduce.fof;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * �����Ƽ���������TopN
 * @author Tangzhi mail:tangzhi8023@gmail.com
 * @description:
 * @date 2019��9��7��
 */
public class FOFRunner {
    public static void main(String[] args) throws IOException, Exception, InterruptedException {
		Configuration conf = new Configuration(true);
		Job job = Job.getInstance(conf);
		job.setJarByClass(FOFRunner.class);
		//conf:
		//map:
		job.setMapperClass(FOFMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(IntWritable.class); //<k,v> --> <k,v,p> ���������λ��������������Ĭ��
        //reduce:
		job.setCombinerClass(FOFReducer.class);
		FileInputFormat.setInputPaths(job, new Path("data/fof/input/"));
		FileOutputFormat.setOutputPath(job, new Path("/data/fof/output"));
		job.waitForCompletion(true);
	}
}
