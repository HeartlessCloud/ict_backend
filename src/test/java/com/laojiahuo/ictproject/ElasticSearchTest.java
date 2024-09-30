/*
package com.laojiahuo.ictproject;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.laojiahuo.ictproject.PO.UserElasticPO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@SpringBootTest
public class ElasticSearchTest {
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
     * 数据聚合
     *//*

    @Test
    void testAgg() throws IOException {
        SearchRequest request = new SearchRequest("user");
        BoolQueryBuilder filter = QueryBuilders.boolQuery().filter(QueryBuilders.matchQuery("school", "电子"));
        request.source().query(filter).size(0);

        request.source().aggregation(
                AggregationBuilders.terms("email_agg").field("email").size(5)
        );
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        Aggregations aggregations = search.getAggregations();
        Terms emailAggs = aggregations.get("email_agg");
        List<? extends Terms.Bucket> buckets = emailAggs.getBuckets();
        for(Terms.Bucket bucket : buckets){
            String email = bucket.getKeyAsString();
            System.out.println("email = "+email);
            long docCount = bucket.getDocCount();
            System.out.println(";count = "+docCount);
        }
    }
    @Test
    void testMatchALl() throws IOException {
        // 1.创建Request
        SearchRequest request = new SearchRequest("user");
        // 2.组织请求参数
        request.source().query(QueryBuilders.matchAllQuery());
        // 3.发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.解析响应
        handleResponse(response);
    }
    @Test
    void testMatch() throws IOException {
        SearchRequest request = new SearchRequest("user");
        request.source().query(QueryBuilders.matchQuery("school","电子"));
        SearchResponse search = client.search(request, RequestOptions.DEFAULT);
        handleResponse(search);
    }
    @Test
    void testMultiMatch() throws IOException {
        // 1.创建Request
        SearchRequest request = new SearchRequest("user");
        // 2.组织请求参数
        request.source().query(QueryBuilders.multiMatchQuery("电子", "school", "username"));
        // 3.发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.解析响应
        handleResponse(response);
    }
    @Test
    void testRange() throws IOException {
        // 1.创建Request
        SearchRequest request = new SearchRequest("user");
        // 2.组织请求参数
        request.source().query(QueryBuilders.rangeQuery("createTime").gte(10000).lte(30000));
        // 3.发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.解析响应
        handleResponse(response);
    }
    @Test
    void testTerm() throws IOException {
        // 1.创建Request
        SearchRequest request = new SearchRequest("user");
        // 2.组织请求参数
        request.source().query(QueryBuilders.termQuery("school", "电子"));
        // 3.发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.解析响应
        handleResponse(response);
    }
    @Test
    void testBool() throws IOException {
        SearchRequest request = new SearchRequest("user");
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("school", "电子"));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("createTime").gte(1011142778000L).lt(1379812162000L));
        request.source().query(boolQueryBuilder);
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        handleResponse(response);
    }
    @Test
    void testHighlight() throws IOException {
        // 1.创建Request
        SearchRequest request = new SearchRequest("user");
        // 2.组织请求参数
        // 2.1.query条件
        request.source().query(QueryBuilders.matchQuery("school", "电子"));
        // 2.2.高亮条件
        request.source().highlighter(
                SearchSourceBuilder.highlight()
                        .field("school")
                        .preTags("<em>")
                        .postTags("</em>")
        );
        // 3.发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 4.解析响应
        handleResponseHighLight(response);
    }
    private void handleResponseHighLight(SearchResponse response) {
        SearchHits searchHits = response.getHits();
        // 1.获取总条数
        long total = searchHits.getTotalHits().value;
        System.out.println("共搜索到" + total + "条数据");
        // 2.遍历结果数组
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            // 3.得到_source，也就是原始json文档
            String source = hit.getSourceAsString();
            // 4.反序列化
            UserElasticPO userElasticPO = JSONUtil.toBean(source, UserElasticPO.class);
            // 5.获取高亮结果
            Map<String, HighlightField> hfs = hit.getHighlightFields();
            if (CollUtil.isNotEmpty(hfs)) {
                // 5.1.有高亮结果，获取name的高亮结果
                HighlightField hf = hfs.get("school");
                if (hf != null) {
                    // 5.2.获取第一个高亮结果片段，就是商品名称的高亮值
                    String hfName = hf.getFragments()[0].string();
                    userElasticPO.setSchool(hfName);
                }
            }
            System.out.println(userElasticPO);
        }
    }
    private void handleResponse(SearchResponse response) {
        SearchHits searchHits = response.getHits();
        // 1.获取总条数
        long total = searchHits.getTotalHits().value;
        System.out.println("共搜索到" + total + "条数据");
        // 2.遍历结果数组
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            // 3.得到_source，也就是原始json文档
            String source = hit.getSourceAsString();
            // 4.反序列化并打印
            UserElasticPO userElasticPO = JSONUtil.toBean(source, UserElasticPO.class);
            System.out.println(userElasticPO);
        }
    }
}
*/
