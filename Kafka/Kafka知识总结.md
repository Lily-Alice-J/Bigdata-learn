1、kafka是什么

	类JMS消息队列，结合JMS中的两种模式，可以有多个消费者主动拉取数据，在JMS中只有点对点模式才有消费者主动拉取数据。
	kafka是一个生产-消费模型。
	Producer：生产者，只负责数据生产，生产者的代码可以集成到任务系统中。 
			  数据的分发策略由producer决定，默认是defaultPartition  Utils.abs(key.hashCode) % numPartitions
	Broker：当前服务器上的Kafka进程,俗称拉皮条。只管数据存储，不管是谁生产，不管是谁消费。
			在集群中每个broker都有一个唯一brokerid，不得重复。
	Topic:目标发送的目的地，这是一个逻辑上的概念，落到磁盘上是一个partition的目录。partition的目录中有多个segment组合(index,log)
			一个Topic对应多个partition[0,1,2,3]，一个partition对应多个segment组合。一个segment有默认的大小是1G。
			每个partition可以设置多个副本(replication-factor 1),会从所有的副本中选取一个leader出来。所有读写操作都是通过leader来进行的。
			特别强调，和mysql中主从有区别，mysql做主从是为了读写分离，在kafka中读写操作都是leader。
	ConsumerGroup：数据消费者组，ConsumerGroup可以有多个，每个ConsumerGroup消费的数据都是一样的。
			       可以把多个consumer线程划分为一个组，组里面所有成员共同消费一个topic的数据，组员之间不能重复消费。
				   
2、kafka生产数据时的分组策略

	默认是defaultPartition  Utils.abs(key.hashCode) % numPartitions
	上文中的key是producer在发送数据时传入的，produer.send(KeyedMessage(topic,myPartitionKey,messageContent))

3、kafka如何保证数据的完全生产

	ack机制：broker表示发来的数据已确认接收无误，表示数据已经保存到磁盘。
	0：不等待broker返回确认消息
	1：等待topic中某个partition leader保存成功的状态反馈
	-1：等待topic中某个partition 所有副本都保存成功的状态反馈
	
4、broker如何保存数据

	在理论环境下，broker按照顺序读写的机制，可以每秒保存600M的数据。主要通过pagecache机制，尽可能的利用当前物理机器上的空闲内存来做缓存。
	当前topic所属的broker，必定有一个该topic的partition，partition是一个磁盘目录。partition的目录中有多个segment组合(index,log)

5、partition如何分布在不同的broker上

	int i = 0
	list{kafka01,kafka02,kafka03}
	
	for(int i=0;i<5;i++){
		brIndex = i%broker;
		hostName = list.get(brIndex)
	}
	
6、consumerGroup的组员和partition之间如何做负载均衡

	最好是一一对应，一个partition对应一个consumer。
	如果consumer的数量过多，必然有空闲的consumer。
	算法：
		假如topic1,具有如下partitions: P0,P1,P2,P3
		加入group中,有如下consumer: C1,C2
		首先根据partition索引号对partitions排序: P0,P1,P2,P3
		根据consumer.id排序: C0,C1
		计算倍数: M = [P0,P1,P2,P3].size / [C0,C1].size,本例值M=2(向上取整)
		然后依次分配partitions: C0 = [P0,P1],C1=[P2,P3],即Ci = [P(i * M),P((i + 1) * M -1)]

7、如何保证kafka消费者消费数据是全局有序的

	伪命题
	如果要全局有序的，必须保证生产有序，存储有序，消费有序。
	由于生产可以做集群，存储可以分片，消费可以设置为一个consumerGroup，要保证全局有序，就需要保证每个环节都有序。
	只有一个可能，就是一个生产者，一个partition，一个消费者。这种场景和大数据应用场景相悖。


