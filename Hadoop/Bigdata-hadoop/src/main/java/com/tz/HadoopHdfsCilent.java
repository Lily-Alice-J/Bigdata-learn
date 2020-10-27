package com.tz;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocatedFileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * ����hadoop hdfs
 * @author Administrator
 *
 */
public class HadoopHdfsCilent {
   FileSystem fs = null;
   Configuration conf = null;
   @Before
   public void init() throws Exception{
	   //Ĭ��ͬ�������в�true�ķ���
	   Configuration conf = new Configuration(); 
	   // ���ø�������
//	   conf.set("dfs.replication", "2");
	   conf.set("fs.defaultFS", "hdfs://192.168.25.11:9000");
	// ��ȡhdfs�ͻ��˲���ʵ������
	// ��windows��JAVA APIԶ�̲���hadoop HDFS�ļ�ϵͳʱӦע���û���֤Ȩ��
	   fs = FileSystem.get(new URI("hdfs://192.168.25.11:9000"), conf, "root");
   }
   /**
    * HDFS�ļ��ϴ�
    * @throws Exception
    * @throws IOException
    */
   @Test
   public void testUpload() throws Exception, IOException{
	   fs.copyFromLocalFile(new Path("D:/gp-hdfs.txt"), new Path("/gp-hdfs.txt"));
	   fs.close();
   }
   /**
    * HDFS�ļ�����
 * @throws IOException 
 * @throws IllegalArgumentException 
    */
    @Test
    public void testdownLoad() throws IllegalArgumentException, IOException{
    	fs.copyToLocalFile(new Path("/test.txt"), new Path("D:/"));
    	// �ر���Դ
    	fs.close();
    }
    /**
     * HDFS�ļ���ɾ�Ĳ�
     * @throws IOException 
     * @throws IllegalArgumentException 
     */
    @Test
    public void testMkidr() throws IllegalArgumentException, IOException{
    	// ����Ŀ¼
    	boolean mkdirs = fs.mkdirs(new Path("/tz/test/kobe"));
    	fs.close();
    	System.out.println(mkdirs);
    }
    
	@Test
    public void testDelete() throws IllegalArgumentException, IOException{
    	// ɾ���ļ�
		// �ڶ�������Ϊ��ʱ�ļ����� ִ�еݹ�ɾ��
    	boolean delete = fs.delete(new Path("/kobe.txt"),false);
    	fs.close();
    	System.out.println(delete);
    }
	
	@Test
	public void renameFile() throws IllegalArgumentException, IOException {
	    // �޸��ļ�����
		fs.rename(new Path("/kobe2.txt"), new Path("/kobe.txt"));
		// �ر���Դ
		fs.close();
	}
	
    @Test
    public void testLs() throws FileNotFoundException, IllegalArgumentException, IOException{
    	//  ��ȡ�ļ�����(���õ������ķ�ʽ���б���)
    	RemoteIterator<LocatedFileStatus> listFiles = fs.listFiles(new Path("/"), true);
    	while(listFiles.hasNext()){
    		LocatedFileStatus fileStatus = listFiles.next();
    		// �ļ�����
    		System.out.println(fileStatus.getPath().getName());
    		// ���С
			System.out.println(fileStatus.getBlockSize());
			// Ȩ��
			System.out.println(fileStatus.getPermission());
			// ����
			System.out.println(fileStatus.getLen());
			// ӵ����
			System.out.println(fileStatus.getOwner());
    	}
    	// �ر���Դ
    	fs.close();
    }
    
    @Test
    public void testLs2() throws FileNotFoundException, IllegalArgumentException, IOException{
    	// �ж�HDFS�����ļ��л����ļ�
    	FileStatus[] listStatus = fs.listStatus(new Path("/"));
    	String flag = "d--";     
    	for (FileStatus fileStatus : listStatus) {
    		if (fileStatus.isFile())  
    			flag = "f--";
			System.out.println(flag + fileStatus.getPath().getName());
		}
    	// �ر���Դ
    	fs.close();
    }
    
    /**
     * ͨ����������д���ļ���С
     * @throws IOException 
     * @throws IllegalArgumentException 
     */
    @Test
	public void testUpload2() throws IllegalArgumentException, IOException{
    	// ��ȡ�����
    	FSDataOutputStream outputStream = fs.create(new Path("/kobe2.txt"), true);
    	// ����������
    	FileInputStream inputStream = new FileInputStream(new File("D:/kobe.txt"));
    	// ִ���ϴ�
    	IOUtils.copyBytes(inputStream, outputStream, conf);
    	// �ر���Դ
    	IOUtils.closeStream(inputStream);
        IOUtils.closeStream(outputStream);
    	fs.close();
    }
    /**
     * ͨ��IO����λ����(��һ�ַ�ʽ)
     */
    @Test
	public void testRandomAccess() throws IllegalArgumentException, IOException{
    	// ��ȡ������
		FSDataInputStream fis = fs.open(new Path("/"));
		// ��ȡ�����
		FileOutputStream fos = new FileOutputStream(new File("d:/"));
		// ���ö�ȡ��С
		byte[] buf = new byte[1024];
		for (int i = 0; i<1024*128; i++)  {
			fis.read(buf);
			fos.write(buf);
		}
		// �ر���Դ
		IOUtils.closeStream(fos);
		IOUtils.closeStream(fis);
		fs.close();
	}
    
    /**
     * ͨ��IO����λ����(�ڶ��ַ�ʽ)
     * @throws IOException 
     * @throws IllegalArgumentException 
     */
    public void testRandomAccess2() throws IllegalArgumentException, IOException {
    	// ��ȡ������
    	FSDataInputStream fis = fs.open(new Path("/"));
    	// ����ƫ�����ƶ�λ�ö�ȡ
    	fis.seek(1024*1024*128);
    	// ���������
    	FileOutputStream fos = new FileOutputStream(new File("/"));
    	// ���ĶԿ�
    	IOUtils.copyBytes(fis, fos, conf);
    	// �ر���Դ
    	IOUtils.closeStream(fos);
    	IOUtils.closeStream(fis);
    	fs.close();
    }
}

