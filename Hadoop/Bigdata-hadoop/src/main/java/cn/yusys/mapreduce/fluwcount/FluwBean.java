package cn.yusys.mapreduce.fluwcount;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.WritableComparable;

/**
 * �û��������� �������� ʵ����(ʵ��hadoop���л��ӿڡ�������������С����)
 * @author Administrator
 *
 */
public class FluwBean implements WritableComparable<FluwBean>{
    long upFluw; // ��������
    long downFluw; // ��������
    long sumFluw; // ������
  //�����л�ʱ����Ҫ������ÿղι��캯��������Ҫ��ʾ����һ��
  	public FluwBean(){}
    public FluwBean(long upFluw, long downFluw) {
		super();
		this.upFluw = upFluw;
		this.downFluw = downFluw;
		this.sumFluw = upFluw + downFluw;
	}
    public void set(long upFluw,long downFluw){
    	this.upFluw = upFluw;
    	this.downFluw = downFluw;
    	this.sumFluw = upFluw +downFluw;
    }
	public long getUpFluw() {
		return upFluw;
	}
	public void setUpFluw(long upFluw) {
		this.upFluw = upFluw;
	}
	public long getDownFluw() {
		return downFluw;
	}
	public void setDownFluw(long downFluw) {
		this.downFluw = downFluw;
	}	
	public long getSumFluw() {
		return sumFluw;
	}
	public void setSumFluw(long sumFluw) {
		this.sumFluw = sumFluw;
	}
	/**
	 * �Զ������л�����
	 * ���л�
	 */
	public void write(DataOutput out) throws IOException {
		    out.writeLong(upFluw);
		    out.writeLong(downFluw);
		    out.writeLong(sumFluw);
	}
	/**
	 * �����л�
	 * ע��:hadoop���л��뷴���л�˳��Ӧһ��
	 */
	public void readFields(DataInput in) throws IOException {
		    upFluw = in.readLong();
		    downFluw = in.readLong();
		    sumFluw = in.readLong();
	}
	/**
	 * ��дtoString����
	 */
	@Override
	public String toString() {
		return  upFluw + "\t" + downFluw + "\t" + sumFluw;
	}
	// �Զ�������ӿ�
	public int compareTo(FluwBean o) {
		return this.getSumFluw() > o.getSumFluw()?-1:1;
	}
}
