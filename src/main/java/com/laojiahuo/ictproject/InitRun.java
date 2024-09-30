//package com.laojiahuo.ictproject;
//
//
//import com.laojiahuo.ictproject.config.websocket.netty.NettyWebSocketStarter;
//import com.laojiahuo.ictproject.redis.RedisComponent;
//import jakarta.annotation.Resource;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.stereotype.Component;
//
//import javax.sql.DataSource;
//
///**
// * 服务启动
// */
//@Component("initRun")
//public class InitRun implements ApplicationRunner {
//    private static final Logger logger = LoggerFactory.getLogger(InitRun.class);
//
//    @Resource
//    private DataSource dataSource;
//    @Resource
//    private NettyWebSocketStarter nettyWebSocketStarter;
//    @Resource
//    private RedisComponent redisComponent;
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        try{
////            dataSource.getConnection();
////            nettyWebSocketStarter.startNetty();
//            new Thread(nettyWebSocketStarter).start();
////            redisComponent.saveHeartBeat("aaa");
////            redisComponent.removeUserHeartBeat("aaa");
//            logger.info("一切正常，准备运行");
//        }catch (Exception e){
//            logger.error("数据库异常:",e);
//        }
//    }
//}
