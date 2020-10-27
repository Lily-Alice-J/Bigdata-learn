package cn.yusys.mapreduce.sharedfridens;

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
 * ��ͬ����(�罻��˿Ⱥ�����ݷ���)<��һ��>
 * @author Administrator
 *
 */
public class SharedFriends {
	 /**
	  * A:B,C,D,F,E,O
		B:A,C,E,K
		C:F,A,D,I
		D:A,E,F,L
		E:B,C,D,M,L
		F:A,B,C,D,E,O,M
		G:A,C,D,E,F
		H:A,C,D,E,O
		I:A,O
		J:B,O
		K:A,C,D
		L:D,E,F
		M:E,F,G
		O:A,H,I,J
	  * ���:�ı��ļ� д����reduce:<>,<>....
	  * @author Administrator
	  *
	  */
	 static class SharedFriendsMapper extends Mapper<LongWritable, Text, Text, Text>{
		 Text k = new Text();
		 Text v = new Text();
		 @Override
		protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			 // A:B,C,D,F,E,O
			  String line = value.toString();
			  String[] persion_fridens = line.split(":");
			  String persion = persion_fridens[0];
			  String friends = persion_fridens[1];
			  v.set(persion);
			  for (String friend : friends.split(",")) {
				  k.set(friend);
				  context.write(k,v);
			}
		}
	 }
	 static class SharedFriendsReduce extends Reducer<Text, Text, Text, Text>{
		 Text k = new Text();
		 Text v = new Text();
		 // ����Ϊ ����k-v��<����,��>.....
		 @Override
		protected void reduce(Text friend, Iterable<Text> persions, Reducer<Text, Text, Text, Text>.Context context)
				throws IOException, InterruptedException {
			 StringBuilder sb = new StringBuilder();
			  k.set(friend);
			  for (Text persion : persions) {
				  sb.append(persion).append(",");
				  v.set(sb.toString());
			}
			  context.write(k,v);
		}
	 }
     public static void main(String[] args) throws Exception {
    	 Configuration conf = new Configuration();
			Job wcjob = Job.getInstance(conf);
			// ָ�������job���ڵ�jar��
	        // wcjob.setJar("/home/hadoop/wordcount.jar");
			wcjob.setJarByClass(SharedFriends.class);
			wcjob.setMapperClass(SharedFriendsMapper.class);
			wcjob.setReducerClass(SharedFriendsReduce.class);
			// �����Զ������㷨
			// �������ǵ�ҵ���߼�Mapper������key��value����������
			wcjob.setMapOutputKeyClass(Text.class);
			wcjob.setMapOutputValueClass(Text.class);
			// �������ǵ�ҵ���߼�Reducer������key��value����������
			wcjob.setOutputKeyClass(Text.class);
			wcjob.setOutputValueClass(Text.class);
			// ָ��Ҫ������������ڵ�λ��
			FileInputFormat.setInputPaths(wcjob, new Path(args[0]));
			// ָ���������֮��Ľ���������λ��
			FileOutputFormat.setOutputPath(wcjob, new Path(args[1]));
			// ��yarn��Ⱥ�ύ���job
			boolean res = wcjob.waitForCompletion(true);
			System.exit(res?0:1);
	}
}
