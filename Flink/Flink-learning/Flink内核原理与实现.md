### Flink内核原理与实现

#### 1.Flink入门
```text
1.批流一体（有界数据+无界数据），但现在生产上还不成熟，众多还是kappa结构与lambda架构相结合
2.依赖检查点进行容错
3.支持有状态计算：其实就是可以结合历史数据进行计算，例如反欺诈行为的识别，要根据用户近几分钟之内的行为数据进行判断。一旦出现异常就需要重新执行流任务，但处理所有的历史数据是不实际的，在Flink使用State能够使流计算恢复到近期的一个检查点。
4.SQL <-- Table <-- DataStream/DataSet <-- Process Function
5.运行时架构：JM: 负责集群和作业管理等；TM: 负责任务的执行等
```
#### 2.Flink应用
```text
数据流上的类型和操作
```
![image](https://github.com/Tandoy/Bigdata-learn/blob/master/Flink/image/%E6%B5%81%E8%BD%AC%E6%8D%A2.png)

[数据流上的类型和操作](http://wuchong.me/blog/2016/05/20/flink-internals-streams-and-operations-on-streams/)

#### 3.核心抽象
```text
一、环境
    Environment：运行时环境
    StreamExecutionEnvironment：执行环境
    RuntimeContext：运行时上下文

二、StreamElement（数据流元素）
    Watermark：水位线
    StreamStatus：状态标记
    StreamRecord：数据记录
    LatencyMarker：延迟标记

三、数据转换（Transformation）
    3.1 Transformation主要分为两类：PhysicalTransformation和虚拟Transformation；在运行时DataSteam调用的api都会转换成Transformation，然后从Transformation转换成底层算子。
        PhysicalTransformation会转换成算子，但虚拟Transformation不会形成实体的算子例如： Reblance、Union、Split、Select等

四、算子（单流输入算子+双流输入算子+数据源算子+异步算子）
    4.1 生命周期管理
    4.2 状态与容错管理
    4.3 数据处理

五、函数体系
    5.1 SourceFunction、一般Function（双流输入、单流输入）、SinkFunction
    5.2 函数层级：
        5.2.1 无状态函数：UDF接口 如MapFunction等
        5.2.2 RichFunction：状态管理+生命周期+UDF接口 如RichMapFunction等
        5.2.3 ProcessFunction：状态管理+生命周期+UDF接口+触发器 如JoinFunction等

六、数据分区
    6.1 用户自定义分区器
    6.2 ForwardPartitioner：在同一个操作链中直接将数据传递至下游
    6.3 SbuftlePartitioner：随机将元素进行分区，确保下游task均匀接收数据
    6.4 ReblancePartitioner：也轮询的方式进行分区分配
    6.5 RescalingPartitioner：根据下游task并行度进行分区，且不会向未分配给自己的分区写入数据
    6.6 BroadcastPartitioner：将记录广播之所有分区，即复制N份
    6.7 KeyGroupSteamPartitioner：根据KeyGroup索引进行分区，该分区不提供给用户使用

七、分布式IP
    Flink的作业管理、资源管理、TM等都需要分布式ID。
```
#### 4.时间与窗口
```text
一、时间类型
    1.1 事件时间
    1.2 处理时间
    1.4 摄取时间

二、窗口类型
    2.1 计数窗口
      2.1.1 滚动计数窗口
      2.1.2 滑动计数窗口
    2.2 时间窗口
      2.2.1 滚动时间窗口（在Flink 1.13之前统计跨天数据时区需要设置偏移量Time.hours(-8)，1.13之后版本已修复）
      2.2.2 滑动时间窗口  
    2.3 会话窗口

三、窗口函数类型
    3.1 增量计算函数：保留窗口部分中间结果集，每流入元素，元素与中间结果集进行计算生成新的中间结果集，然后保存到窗口状态中，如ReduceFunction、AggregateFunction、FoldFunction。
    3.2 全量计算函数：先缓存窗口中全所有元素，等触发计算条件再一起进行计算。Flink内置的ProcessWindowFunction就是全量计算函数，这种计算模式效率比增量计算较低，并且要占更多内存。
    3.3 注意在Flink中窗口函数都是根据用户自定义的开窗时间间隔触发计算，如果需要每条事件record触发计算可采用Over Aggregation Function。

四、WaterMark
    4.1 水位线：
        4.1.1 WaterMark主要用来解决事件时间语义下的数据乱序问题
        4.1.2 WaterMark本身也是数据元素随着record在拓扑中流动（时间戳）
        4.1.3 WaterMark可以在拓扑中任意位置进行定义
        4.1.4 WaterMark在多流多分区情况取最小值，这里常见现象是Kafka多分区出现某一分区空闲输入导致WaterMark不会变化不触发窗口计算。解决方式：Flink 1.11之后：WatermarkStrategy.withIdleness(Duration.ofMinutes(1)); Flink 1.11之前：https://mp.weixin.qq.com/s/108o9iwEZaHyMoRBGUKX8g
        4.1.5 WaterMark触发窗口计算前闭后开，不包括结束时间。例：watermark = eventTime 5s滚动窗口 [0,5),[5,10),[10,15) 进行时间窗口触发计算
    4.2 WaterMark产生方式：
        4.2.1 Punctuated 数据流中每一个递增的EventTime都会产生一个Watermark。在实际的生产中Punctuated方式在TPS很高的场景下会产生大量的Watermark在一定程度上对下游算子造成压力，所以只有在实时性要求非常高的场景才会选择Punctuated的方式进行Watermark的生成。
        4.2.2 Periodic 周期性的（一定时间间隔或者达到一定的记录条数）产生一个Watermark。在实际的生产中Periodic的方式必须结合时间和积累条数两个维度继续周期性产生Watermark，否则在极端情况下会有很大的延时。
```