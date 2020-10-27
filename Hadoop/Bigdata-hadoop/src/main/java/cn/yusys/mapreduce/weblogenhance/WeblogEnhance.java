package cn.yusys.mapreduce.weblogenhance;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;



/**
 * �����û����ʵ�ַ����  ��url�Ѵ��ڹ��������ǿ��ǰ�û���־ ��û�������ʧ�ܴ��ڹ������
 * @author Administrator
 *
 */
public class WeblogEnhance {
    static class WeblogEnhanceMapper extends Mapper<LongWritable, Text, Text, NullWritable>{
    	Map<String,String> ruleMap = new HashMap<String, String>();
    	Text k = new Text();
    	NullWritable v = NullWritable.get();
    	// ��ʼ�������(sql��ѯ)
    	@Override
    	protected void setup(Mapper<LongWritable, Text, Text, NullWritable>.Context context)
    			throws IOException, InterruptedException {
    		    try {
					DBLoader.dbLoader(ruleMap);
				} catch (Exception e) {
					e.printStackTrace();
				}
    	}
    	
    	@Override
    	protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, NullWritable>.Context context)
    			throws IOException, InterruptedException {
    		  String line = value.toString();
    		  String[] fields = line.split("\t");
    		  try {
    			  String url = fields[26];
        		  String content_tag = ruleMap.get(url);
        		  if(content_tag == null){
        			  // �����û�д�url��¼ д������ȡ�嵥
        			  k.set(url + "\t" + "tocrowl" +"\n"); 
        			  context.write(k, v);
        		  }else {
        			  // ��������д�url��¼ ������־��ǿ
        			  k.set(line + "\t" + content_tag + "\n");
        			  context.write(k, v);
        		  }
			} catch (Exception e) {
				  e.printStackTrace();
			}
    	}
    }
	public static void main(String[] args) throws Exception {
		 Configuration conf = new Configuration();
			Job wcjob = Job.getInstance(conf);
			// ָ�������job���ڵ�jar��
	        // wcjob.setJar("/home/hadoop/wordcount.jar");
			wcjob.setJarByClass(WeblogEnhance.class);
			wcjob.setMapperClass(WeblogEnhanceMapper.class);
			// �����Զ������㷨
			// �������ǵ�ҵ���߼�Mapper������key��value����������
			wcjob.setMapOutputKeyClass(Text.class);
			wcjob.setMapOutputValueClass(NullWritable.class);
			// �Զ���OutPutFarmat
			wcjob.setOutputFormatClass(WelogEnhanceOutputFarmat.class);
			// �������ǵ�ҵ���߼�Reducer������key��value����������
			// ָ��Ҫ������������ڵ�λ��
			FileInputFormat.setInputPaths(wcjob, new Path(args[0]));
			// ָ���������֮��Ľ���������λ��
		    FileOutputFormat.setOutputPath(wcjob, new Path(args[1]));
			// ��yarn��Ⱥ�ύ���job
			boolean res = wcjob.waitForCompletion(true);
			System.exit(res?0:1);
	}

}
