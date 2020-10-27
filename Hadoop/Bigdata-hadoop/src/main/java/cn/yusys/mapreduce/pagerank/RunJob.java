package cn.yusys.mapreduce.pagerank;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * ������ҳȨ��MR
 * @author Tangzhi mail:tangzhi8023@gmail.com
 * @description:
 * @date 2019��9��22��
 */
public class RunJob {
      public static enum MyCounter {
    	  //�Զ��������
    	  my
      }
      public static void main(String[] args) {
		Configuration conf = new Configuration(true);
		conf.set("mapreduce.app-submission.corss-paltform", "true");
		//����ֲ�ʽ����,�����jar��
		//��,client�ڼ�Ⱥ���hadoop jar ���ַ�ʽ����,client�б�������jar��λ��
		conf.set("mapreduce.framework.name", "local");
		//�������,ֻ����,�л��ֲ�ʽ�����ص�����ģ�����е�����
		//���ַ�ʽ���Ƿֲ�ʽ,���Բ��ô�jar��
		
		double d = 0.1;
		int i = 0;//MR��������ͳ��
		while (true) {
			i++;
			try {
				conf.setInt("runCount", i); //��MR����������Ϊ��������ֲ�ʽ���������
				FileSystem fs = FileSystem.get(conf);
				Job job = Job.getInstance(conf);
				job.setJarByClass(RunJob.class);
				job.setJobName("pr" + i);
				job.setMapperClass(PageRankMapper.class);
				job.setReducerClass(PageRankReducer.class);
				job.setMapOutputKeyClass(Text.class);
				job.setMapOutputValueClass(Text.class);
				//��ʼ�������ʽ���� �˸�ʽ���ཫ�������ݸ����Ʊ�������и�"\t"
				job.setInputFormatClass(KeyValueTextInputFormat.class);
				Path inputPath = new Path("/data/pagerank/input/");
				if (i > 1) {
					 inputPath = new Path("/data/pagerank/output/pr" + (i -1));
				}
				//������������·��
				FileInputFormat.addInputPath(job, inputPath );
				Path outputPath = new Path("/data/pagerank/output/pr" + i);			
				//�����������·��
				FileOutputFormat.setOutputPath(job, outputPath);
				if (fs.exists(outputPath)) {
					fs.delete(outputPath, true);
				}
				//�ύ��ҵ
				boolean completion = job.waitForCompletion(true);
				if (completion) {
					System.out.println("success...");
					//�Ӽ��������ó�����ҳ��Ĳ�ֵ�ܺ�
					long sum = job.getCounters().findCounter(MyCounter.my).getValue();
					System.out.println(sum);
					double avgd = sum / 4000.0;
					if (avgd < d) {
						//�����MR����Ʊ��ֵС��Ŀ��ֵʱֹͣ����
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
      
     static class PageRankMapper extends Mapper<Text, Text, Text, Text> {
    	   @Override
    	protected void map(Text key, Text value,Context context)
    			throws IOException, InterruptedException {
    		 //1.�õ�MR��������
    		   int runCount = context.getConfiguration().getInt("runCount", 1);
    		 //A	   B D
   			 //K:A
   			 //V:B D
    		 //A	0.3 B D 
   			 //K:A
   			 //V:0.3 B D
    		 //2.�����ʽ�����и���key
    		 String page = key.toString();
    		 Node node  = null;
    		 if (runCount == 1) {
    			//3.1 ��Ϊ��һ�ε�������ʼ��key��Ʊ��ֵ
				node = Node.fromMR("1.0", value.toString());
			}else {
				//3.2 ����Ϊ��һ�ε�����ֱ�����ҳ���ϵ����
			}
    		// A:1.0 B D  �����ϵ�prֵ�Ͷ�Ӧ��ҳ���ϵ
    		 context.write(new Text(page), new Text(node.toString()));
    		//4.�������Ӧҳ�����õ���Ʊ��ֵ
    		 if (node.containsAdjacentNodes()) {
				//4.1 ����Ʊ��ֵ  ��PRֵ / ������ҳ�����
    			 double outvalue = node.getPageRank() / node.getAdjacentNodeNames().length;
    			//4.2 �Դ�key����ͶƱ��ҳ����������ֵ
    			 for (int i = 0; i<node.getAdjacentNodeNames().length; i++) {
    				 String outPage = node.getAdjacentNodeNames()[i];
    				 // B:0.5 ҳ��AͶ��˭��˭��Ϊkey��val��Ʊ��ֵ
    				 context.write(new Text(outPage), new Text(outvalue + ""));
    			 }
			}
    	}
     }
     
     static class PageRankReducer extends Reducer<Text, Text, Text, Text> {
    	  @Override
    	protected void reduce(Text key, Iterable<Text> iterable, Context context)
    			throws IOException, InterruptedException {
    		//��ͬ��KeyΪһ�飬����һ��Reduce����
    		/**
    		 * key��ҳ�����Ʊ���B
    		 * ������������
    		 * B:1.0 C  ҳ���Ӧ��ϵ���ϵ�prֵ
    		 * B:0.5          ͶƱֵ
    		 */
    		double sum = 0.0;
  			
  			Node sourceNode = null;
  			for (Text i : iterable) {
  				Node node = Node.fromMR(i.toString());
  				if (node.containsAdjacentNodes()) {
  				  //������ʽΪ��B:1.0 C
  					sourceNode = node;
  				} else {
  				 //������ʽΪ��B:0.5
  					sum = sum + node.getPageRank();
  				}
  			}

  			// google��pagerank��ʽ ����4Ϊҳ������
  			double newPR = (0.15 / 4.0) + (0.85 * sum);
  			System.out.println("*********** new pageRank value is " + newPR);
  			// ���µ�prֵ�ͼ���֮ǰ��pr�Ƚ�
  			double d = newPR - sourceNode.getPageRank();
  			int j = (int) (d * 1000.0);
  			j = Math.abs(j);
  			System.out.println(j + "___________");
  			context.getCounter(MyCounter.my).increment(j);
  			sourceNode.setPageRank(newPR);
  			context.write(key, new Text(sourceNode.toString()));   
    	}
     }
}
