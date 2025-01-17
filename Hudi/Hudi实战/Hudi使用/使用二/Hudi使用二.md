## Hudi使用二

### 一、使用Hudi-Clustering
```text
Clustering对数据按照数据特征进行聚簇，以便优化文件大小和数据布局。可重新组织数据以提高查询性能，也不会影响摄取速度。现在Hudi支持同步和异步的Clustering模式。
```
```shell script
同步模式：
import org.apache.hudi.QuickstartUtils._
import scala.collection.JavaConversions._
import org.apache.spark.sql.SaveMode._
import org.apache.hudi.DataSourceReadOptions._
import org.apache.hudi.DataSourceWriteOptions._
import org.apache.hudi.config.HoodieWriteConfig._
val df =  //generate data frame
df.write.format("org.apache.hudi").
        options(getQuickstartWriteConfigs).
        option(PRECOMBINE_FIELD_OPT_KEY, "ts").
        option(RECORDKEY_FIELD_OPT_KEY, "uid").
        option(PARTITIONPATH_FIELD_OPT_KEY, "ts").
        option(TABLE_NAME, "hudi_test_kafka").
        option("hoodie.parquet.small.file.limit", "0").
        option("hoodie.clustering.inline", "true").
        option("hoodie.clustering.inline.max.commits", "4").
        option("hoodie.clustering.plan.strategy.target.file.max.bytes", "1073741824").
        option("hoodie.clustering.plan.strategy.small.file.limit", "629145600").
        option("hoodie.clustering.plan.strategy.sort.columns", "uid"). //optional, if sorting is needed as part of rewriting data
        mode(Append).
        save("hdfs://dxbigdata101:8020/user/hudi/test/data/hudi_test_kafka");
```
```shell script
异步模式：分为两步(1.doSchedule,生成.replacecommit.requested; 2.doCluster,拿到doCluster相关信息进行真正的Clustering)
spark-submit \
spark2-submit \
--master yarn \
--deploy-mode client \
--conf spark.task.cpus=1 \
--conf spark.executor.cores=1 \
--class org.apache.hudi.utilities.HoodieClusteringJob `ls /home/appuser/tangzhi/hudi-spark/hudi-sparkSql/packaging/hudi-utilities-bundle/target/hudi-utilities-bundle_2.11-0.9.0-SNAPSHOT.jar` \
--schedule \
--base-path hdfs://dxbigdata101:8020/user/hudi/test/data/hudi_on_flink \
--table-name hudi_on_flink \
--spark-memory 1G

spark2-submit \
--master yarn \
--deploy-mode client \
--conf spark.task.cpus=1 \
--conf spark.executor.cores=1 \
--class org.apache.hudi.utilities.HoodieClusteringJob `ls /home/appuser/tangzhi/hudi-spark/hudi-sparkSql/packaging/hudi-utilities-bundle/target/hudi-utilities-bundle_2.11-0.9.0-SNAPSHOT.jar` \
--instant-time 20210706101201 \
--base-path hdfs://dxbigdata101:8020/user/hudi/test/data/hudi_on_flink \
--table-name hudi_on_flink \
--spark-memory 1G
```
### 二、Debug HoodieClusteringJob
```shell script
spark2-submit \
--master spark://dxbigdata101:7077 \
--driver-java-options \
"-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8023" \
--conf spark.task.cpus=1 \
--conf spark.executor.cores=1 \
--class org.apache.hudi.utilities.HoodieClusteringJob `ls /home/appuser/tangzhi/hudi-spark/hudi-sparkSql/packaging/hudi-utilities-bundle/target/hudi-utilities-bundle_2.11-0.9.0-SNAPSHOT.jar` \
--schedule \
--base-path hdfs://dxbigdata101:8020/user/hudi/test/data/hudi_on_flink \
--table-name hudi_on_flink \
--spark-memory 1G
```

### 三、使用Hudi-OCC
```text
从Hudi-0.8.0版本开始，支持单表乐观锁并发写特性。Hudi支持文件级OCC，即对于发生在同一个表上的任何2个提交（或写入者），如果它们没有写入正在更改的重叠文件，则允许两个写入者成功。此功能目前处于实验阶段，需要Zookeeper或HiveMetastore来获取锁。
```
```shell script
##start hudi
spark-shell \
--packages org.apache.spark:spark-avro_2.11:2.4.4 \
--conf 'spark.serializer=org.apache.spark.serializer.KryoSerializer' \
--jars /home/appuser/tangzhi/hudi-0.8/hudi-release-0.8.0/packaging/hudi-spark-bundle/target/hudi-spark-bundle_2.11-0.8.0.jar
```
```shell script
##Datasource Writer
import org.apache.hudi.QuickstartUtils._
import scala.collection.JavaConversions._
import org.apache.spark.sql.SaveMode._
import org.apache.hudi.DataSourceReadOptions._
import org.apache.hudi.DataSourceWriteOptions._
import org.apache.hudi.config.HoodieWriteConfig._
val tableName = "hudi_test_occ"
val basePath = "hdfs://dxbigdata101:8020/user/hudi/test/data/hudi_test_occ"
val inserts = Seq("""{"area":"hunan","uid":"1","itemid":"11","npgid":"43","evid":"addComment","os":"andriod","pgid":"30","appid":"gmall2019","mid":"mid_117","type":"event","ts":"2021-08-14 12:23:34"}""")
import spark.implicits._
val ds = spark.createDataset(inserts)
val df = spark.read.json(ds)
df.write.format("hudi")
.options(getQuickstartWriteConfigs)
.option("hoodie.datasource.write.commitmeta.key.prefix","deltastreamer.checkpoint.key")
.option("hoodie.cleaner.policy.failed.writes", "LAZY")
.option("hoodie.write.concurrency.mode", "optimistic_concurrency_control")
.option("hoodie.write.lock.zookeeper.url", "dxbigdata103")
.option("hoodie.write.lock.zookeeper.port", "2181")
.option("hoodie.write.lock.zookeeper.lock_key", "occ")
.option("hoodie.write.lock.zookeeper.base_path", "/hudi/occ")
.option("hoodie.datasource.write.keygenerator.class", "org.apache.hudi.keygen.TimestampBasedKeyGenerator")
.option("hoodie.deltastreamer.keygen.timebased.timestamp.type", "DATE_STRING")
.option("hoodie.deltastreamer.keygen.timebased.input.dateformat", "yyyy-MM-dd HH:mm:ss")
.option("hoodie.deltastreamer.keygen.timebased.output.dateformat", "yyyy/MM/dd")
.option("hoodie.deltastreamer.keygen.timebased.timezone", "GMT+8:00")
.option(PRECOMBINE_FIELD_OPT_KEY, "ts")
.option(RECORDKEY_FIELD_OPT_KEY, "uid")
.option(PARTITIONPATH_FIELD_OPT_KEY, "ts")
.option(TABLE_NAME, tableName)
.mode(Overwrite)
.save(basePath)
```
```shell script
##DeltaStreamer
spark-submit \
--master yarn \
--driver-memory 1G \
--num-executors 2 \
--executor-memory 1G \
--executor-cores 4 \
--deploy-mode cluster \
--conf spark.yarn.executor.memoryOverhead=512 \
--conf spark.yarn.driver.memoryOverhead=512 \
--class org.apache.hudi.utilities.deltastreamer.HoodieDeltaStreamer `ls /home/appuser/tangzhi/hudi-0.8/hudi-release-0.8.0/packaging/hudi-utilities-bundle/target/hudi-utilities-bundle_2.11-0.8.0.jar` \
--props file:///opt/apps/hudi/hudi-utilities/src/test/resources/delta-streamer-config/kafka.properties \
--schemaprovider-class org.apache.hudi.utilities.schema.FilebasedSchemaProvider \
--source-class org.apache.hudi.utilities.sources.JsonKafkaSource \
--target-base-path hdfs://dxbigdata101:8020/user/hudi/test/data/hudi_test_occ \
--op UPSERT \
--continuous \
--target-table hudi_test_occ \
--table-type COPY_ON_WRITE \
--source-ordering-field uid \
--source-limit 5000000
```
```shell script
##read
import org.apache.hudi.QuickstartUtils._
import scala.collection.JavaConversions._
import org.apache.spark.sql.SaveMode._
import org.apache.hudi.DataSourceReadOptions._
import org.apache.hudi.DataSourceWriteOptions._
import org.apache.hudi.config.HoodieWriteConfig._
val basePath = "hdfs://dxbigdata101:8020/user/hudi/test/data/hudi_test_occ"
val roViewDF = spark.read.
format("org.apache.hudi").
load(basePath + "/*/*/*/*")
roViewDF.registerTempTable("hudi_test_occ")
spark.sql("select count(1) from  hudi_test_occ").show()
```
```shell script
1.设置正确的本机锁提供程序客户端重试
    hoodie.write.lock.wait_time_ms
    hoodie.write.lock.num_retries
2.为Zookeeper和HiveMetastore设置正确的hudi客户端重试
    hoodie.write.lock.client.wait_time_ms
    hoodie.write.lock.client.num_retries
3.禁用多重写入
    hoodie.write.concurrency.mode=single_writer
    hoodie.cleaner.policy.failed.writes=EAGER
```