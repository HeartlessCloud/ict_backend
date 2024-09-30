/*
package com.laojiahuo.ictproject;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.laojiahuo.ictproject.PO.UserElasticPO;
import com.laojiahuo.ictproject.PO.UserPO;
import com.laojiahuo.ictproject.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.checkerframework.checker.units.qual.A;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
@Slf4j
@SpringBootTest
public class ElasticDocTest {
    @Autowired
    private UserService userService;
    private RestHighLevelClient client;
    @BeforeEach
    void setup(){
        client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://192.168.245.100:9200")
        ));
    }

    @AfterEach
    void tearDown() throws IOException {
        if(client!=null){
            client.close();
        }
    }

    */
/**
     * 数据库转换到ElasticSearch
     * 插入文档
     * @throws IOException
     *//*

    @Test
    void testIndexDoc() throws IOException {
        UserPO userPO = userService.getById(20);
        System.out.println("MYSQL查询结果:"+userPO);
        UserElasticPO userElasticPO = new UserElasticPO();
        BeanUtils.copyProperties(userPO,userElasticPO);
        IndexRequest request = new IndexRequest("user").id(String.valueOf(userElasticPO.getId()));
        request.source(JSONUtil.toJsonStr(userElasticPO), XContentType.JSON);
        client.index(request, RequestOptions.DEFAULT);
    }

    */
/**
     * 删除文档
     * @throws IOException
     *//*

    @Test
    void testDeleteDoc() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("user","20");
        client.delete(deleteRequest,RequestOptions.DEFAULT);
    }

    */
/**
     * 获得文档
     * @throws IOException
     *//*

    @Test
    void testGetDoc() throws IOException {
        GetRequest getRequest = new GetRequest("user","20");
        GetResponse documentFields = client.get(getRequest, RequestOptions.DEFAULT);
        String json = documentFields.getSourceAsString();
        System.out.println("查询结果为:"+json);
        UserElasticPO userElasticPO = JSONUtil.toBean(json, UserElasticPO.class);
        System.out.println("转换结果"+userElasticPO);
    }

    */
/**
     * 部分修改
     * @throws IOException
     *//*

    @Test
    void testUpdateDocument() throws IOException {
        // 1.准备Request
        UpdateRequest request = new UpdateRequest("user", "20");
        // 2.准备请求参数
        request.doc(
                "school", "北大",
                "username", "楼金辉"
        );
        // 3.发送请求
        client.update(request, RequestOptions.DEFAULT);
    }

    */
/**
     * 批量操作
     *//*

    @Test
    void testBulk() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        // 1.创建Request
        BulkRequest request = new BulkRequest();
        // 2.准备请求参数
        request.add(new IndexRequest("items").id("1").source("json doc1", XContentType.JSON));
        request.add(new IndexRequest("items").id("2").source("json doc2", XContentType.JSON));
        // 3.发送请求
        client.bulk(request, RequestOptions.DEFAULT);
    }

    */
/**
     * 实现分页并且导入到ElasticSearch
     *//*

    @Test
    void testLoadUserDocs() throws IOException {
        int pageNo = 1;
        int size = 3;
        while (true) {
            Page<UserPO> page = userService.lambdaQuery().page(new Page<UserPO>(pageNo, size));
            List<UserPO> userPOS = page.getRecords();

            // 如果当前页没有数据，退出循环
            if (userPOS == null || userPOS.isEmpty()) {
                log.info("没有更多数据，退出循环");
                return;
            }

            log.info("加载第{}页数据，共{}条", pageNo, userPOS.size());
            BulkRequest bulkRequest = new BulkRequest("user");
            for (UserPO userPO : userPOS) {
                log.info("用户信息{}", userPO);
                UserElasticPO userElasticPO = new UserElasticPO();
                BeanUtils.copyProperties(userPO, userElasticPO);
                bulkRequest.add(new IndexRequest("user")
                        .id(String.valueOf(userElasticPO.getId()))
                        .source(JSONUtil.toJsonStr(userElasticPO),
                                XContentType.JSON));
            }
            client.bulk(bulkRequest, RequestOptions.DEFAULT);

            pageNo++;
        }
    }

}
*/
