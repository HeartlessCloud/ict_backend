/*
package com.laojiahuo.ictproject;


import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ElasticIndexTest {
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

    @Test
    void testConnection(){
        System.out.println(client);
    }

    private static final String MAPPING_TEMPLATE = "{\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"id\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"username\":{\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_smart\"\n" +
            "      },\n" +
            "      \"email\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"headshot\":{\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"school\":{\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_smart\"\n" +
            "      },\n" +
            "      \"createTime\":{\n" +
            "        \"type\": \"date\"\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}";

    */
/**
     * 创建索引库
     * @throws IOException
     *//*

    @Test
    void testCreateHotelIndex() throws IOException {
        // 1.创建Resquest对象
        CreateIndexRequest request = new CreateIndexRequest("user");
        // 2.准备请求参数
        request.source(MAPPING_TEMPLATE, XContentType.JSON);
        // 3.发生请求
        client.indices().create(request, RequestOptions.DEFAULT);
    }

    */
/**
     * 删除索引库
     * @throws IOException
     *//*

    @Test
    void testDeleteHotelIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("user");
        client.indices().delete(request,RequestOptions.DEFAULT);
    }

    */
/**
     * 获得索引
     * @throws IOException
     *//*

    @Test
    void testGetIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("user");
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println("exists = "+exists);
    }

}
*/
