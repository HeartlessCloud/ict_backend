package com.laojiahuo.ictproject.listener;

import cn.hutool.json.JSONUtil;
import com.laojiahuo.ictproject.PO.UserElasticPO;
import com.laojiahuo.ictproject.PO.UserPO;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class UserListener {

    /**
     * es客户端
     */
    @Autowired
    private RestHighLevelClient client;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(name = "direct.queue"),
                    exchange = @Exchange(name = "user.direct",type = ExchangeTypes.DIRECT),
                    key = {"user.register","user.update"}
            )
    )
    public void userInfoChangeQueue(UserPO userPO) throws IOException {
        log.info("UserListener userInfoChangeQueue userPO{}",userPO);
        UserElasticPO userElasticPO = new UserElasticPO();
        BeanUtils.copyProperties(userPO,userElasticPO);
        IndexRequest request = new IndexRequest("user").id(String.valueOf(userElasticPO.getId()));
        request.source(JSONUtil.toJsonStr(userElasticPO), XContentType.JSON);
        client.index(request, RequestOptions.DEFAULT);
    }
}
