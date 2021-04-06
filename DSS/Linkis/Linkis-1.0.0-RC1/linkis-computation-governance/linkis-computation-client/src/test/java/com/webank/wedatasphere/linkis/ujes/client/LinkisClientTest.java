package com.webank.wedatasphere.linkis.ujes.client;

import com.webank.wedatasphere.linkis.common.utils.Utils;
import com.webank.wedatasphere.linkis.httpclient.dws.authentication.StaticAuthenticationStrategy;
import com.webank.wedatasphere.linkis.httpclient.dws.authentication.TokenAuthenticationStrategy;
import com.webank.wedatasphere.linkis.httpclient.dws.config.DWSClientConfig;
import com.webank.wedatasphere.linkis.httpclient.dws.config.DWSClientConfigBuilder;
import com.webank.wedatasphere.linkis.ujes.client.UJESClient;
import com.webank.wedatasphere.linkis.ujes.client.UJESClientImpl;
import com.webank.wedatasphere.linkis.ujes.client.request.JobExecuteAction;
import com.webank.wedatasphere.linkis.ujes.client.request.ResultSetAction;
import com.webank.wedatasphere.linkis.ujes.client.response.JobExecuteResult;
import com.webank.wedatasphere.linkis.ujes.client.response.JobInfoResult;
import com.webank.wedatasphere.linkis.ujes.client.response.JobProgressResult;
import org.apache.commons.io.IOUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Use the official example LinkisClientTest test linkis-1.0-rc1 without label
 */
public class LinkisClientTest {

    public static void main(String[] args){

        String user = "appuser";
        String executeCode = "show databases;";

        // 1. 配置DWSClientBuilder，通过DWSClientBuilder获取一个DWSClientConfig
        DWSClientConfig clientConfig = ((DWSClientConfigBuilder) (DWSClientConfigBuilder.newBuilder()
                .addServerUrl("http://dxbigdata103:9001")  //指定ServerUrl，linkis服务器端网关的地址,如http://{ip}:{port}
                .connectionTimeout(30000)   //connectionTimeOut 客户端连接超时时间
                .discoveryEnabled(false).discoveryFrequency(1, TimeUnit.MINUTES)  //是否启用注册发现，如果启用，会自动发现新启动的Gateway
                .loadbalancerEnabled(true)  // 是否启用负载均衡，如果不启用注册发现，则负载均衡没有意义
                .maxConnectionSize(5)   //指定最大连接数，即最大并发数
                .retryEnabled(false).readTimeout(30000)   //执行失败，是否允许重试
                .setAuthenticationStrategy(new StaticAuthenticationStrategy())   //AuthenticationStrategy Linkis认证方式
                .setAuthTokenKey("appuser").setAuthTokenValue("appuser")))  //认证key，一般为用户名;  认证value，一般为用户名对应的密码
                .setDWSVersion("v1").build();  //linkis后台协议的版本，当前版本为v1

        // 2. 通过DWSClientConfig获取一个UJESClient
        UJESClient client = new UJESClientImpl(clientConfig);

        try {
            // 3. 开始执行代码
            System.out.println("user : " + user + ", code : [" + executeCode + "]");
            Map<String, Object> startupMap = new HashMap<String, Object>();
            startupMap.put("wds.linkis.yarnqueue", "default"); // 在startupMap可以存放多种启动参数，参见linkis管理台配置
            JobExecuteResult jobExecuteResult = client.execute(JobExecuteAction.builder()
                    .setCreator("linkisClient-Test")  //creator，请求linkis的客户端的系统名，用于做系统级隔离
                    .addExecuteCode(executeCode)   //ExecutionCode 请求执行的代码
                    .setEngineType((JobExecuteAction.EngineType) JobExecuteAction.EngineType$.MODULE$.HIVE()) // 希望请求的linkis的执行引擎类型，如Spark hive等
                    .setUser(user)   //User，请求用户；用于做用户级多租户隔离
                    .setStartupParams(startupMap)
                    .build());
            System.out.println("execId: " + jobExecuteResult.getExecID() + ", taskId: " + jobExecuteResult.taskID());

            // 4. 获取脚本的执行状态
            JobInfoResult jobInfoResult = client.getJobInfo(jobExecuteResult);
            int sleepTimeMills = 1000;
            while(!jobInfoResult.isCompleted()) {
                // 5. 获取脚本的执行进度
                JobProgressResult progress = client.progress(jobExecuteResult);
                Utils.sleepQuietly(sleepTimeMills);
                jobInfoResult = client.getJobInfo(jobExecuteResult);
            }

            // 6. 获取脚本的Job信息
            JobInfoResult jobInfo = client.getJobInfo(jobExecuteResult);
            // 7. 获取结果集列表（如果用户一次提交多个SQL，会产生多个结果集）
            String resultSet = jobInfo.getResultSetList(client)[0];
            // 8. 通过一个结果集信息，获取具体的结果集
            Object fileContents = client.resultSet(ResultSetAction.builder().setPath(resultSet).setUser(jobExecuteResult.getUser()).build()).getFileContent();
            System.out.println("fileContents: " + fileContents);

        } catch (Exception e) {
            e.printStackTrace();
            IOUtils.closeQuietly(client);
        }
        IOUtils.closeQuietly(client);
    }
}