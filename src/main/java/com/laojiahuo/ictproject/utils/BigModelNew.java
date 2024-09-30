package com.laojiahuo.ictproject.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.laojiahuo.ictproject.constants.GPTConstants;
import com.laojiahuo.ictproject.PO.MessagePO;
import com.laojiahuo.ictproject.service.MessageService;
import okhttp3.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class BigModelNew extends WebSocketListener {
    public String totalAnswer=""; // 大模型的答案汇总
    public static final Gson gson = new Gson();
    ExecutorService executorService = Executors.newFixedThreadPool(10); // 根据需要设置线程池大小

    // 个性化参数
    private String userCode;
    private SseEmitter sseEmitter;
    private Boolean wsCloseFlag;
    private String NewQuestion = "";
    private List<MessagePO> historyList;// 对话历史存储集

    private MessageService messageService;
    private Date startDate;
    private Date endDate;

    // 初始化方法
    public BigModelNew (String userCode, Boolean wsCloseFlag, SseEmitter sseEmitter, String NewQuestion, List<MessagePO> historyList,MessageService messageService) {
        this.userCode = userCode;
        this.wsCloseFlag = wsCloseFlag;
        this.sseEmitter = sseEmitter;
        this.NewQuestion = NewQuestion;
        this.historyList = historyList;
        this.messageService = messageService;

    }


/*    public  boolean canAddHistory(){  // 由于历史记录最大上线1.2W左右，需要判断是能加入历史
        int history_length=0;
        for(MessagePO temp:historyList){
            history_length=history_length+temp.getContent().length();
        }
        if(history_length>12000){
            historyList.remove(0);
            historyList.remove(1);
            historyList.remove(2);
            historyList.remove(3);
            historyList.remove(4);
            return false;
        }else{
            return true;
        }
    }*/

    // 线程来发送音频与参数
    class MyThread extends Thread {
        private WebSocket webSocket;
        public MyThread(WebSocket webSocket) {
            this.webSocket = webSocket;
        }

        public void run() {
            try {
                JSONObject requestJson=new JSONObject();

                JSONObject header=new JSONObject();  // header参数
                header.put("app_id",GPTConstants.XF_XH_APPID_KEY);
                header.put("uid",userCode);

                JSONObject parameter=new JSONObject(); // parameter参数
                JSONObject chat=new JSONObject();
                chat.put("domain","generalv3.5");
                chat.put("temperature",0.5);
                chat.put("max_tokens",4096);
                parameter.put("chat",chat);

                JSONObject payload=new JSONObject(); // payload参数
                JSONObject message=new JSONObject();
                JSONArray text=new JSONArray();

                // 反转列表
                if (historyList != null && !historyList.isEmpty()) {
                    Collections.reverse(historyList);
                    for (MessagePO tempRoleContent : historyList) {
                        text.add(JSON.toJSON(tempRoleContent));
                    }
                }

                // 最新问题
                MessagePO messagePO=new MessagePO();
                messagePO.setRole("user");
                messagePO.setContent(NewQuestion);
                startDate = new Date();
                messagePO.setCreateTime(startDate);
                text.add(JSON.toJSON(messagePO));


                message.put("text",text);
                payload.put("message",message);
                requestJson.put("header",header);
                requestJson.put("parameter",parameter);
                requestJson.put("payload",payload);
                System.err.println("传参明细"+requestJson); // 可以打印看每次的传参明细
                System.out.print("大模型：");
                webSocket.send(requestJson.toString());
                // 等待服务端返回完毕后关闭
                while (true) {
                    // System.err.println(wsCloseFlag + "---");
                    Thread.sleep(200);
                    if (wsCloseFlag) {
                        break;
                    }
                }
                webSocket.close(1000, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        executorService.submit(new MyThread(webSocket)); // 使用线程池提交任务
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        // System.out.println(userCode + "用来区分那个用户的结果" + text);
        JsonParse myJsonParse = gson.fromJson(text, JsonParse.class);
        if (myJsonParse.header.code != 0) {
            System.out.println("发生错误，错误码为：" + myJsonParse.header.code);
            System.out.println("本次请求的sid为：" + myJsonParse.header.sid);
            webSocket.close(1000, "");
        }
        // 每次都会收到内容
        List<Text> textList = myJsonParse.payload.choices.text;
        for (Text temp : textList) {
            System.out.print(temp.content);
            try {
                sseEmitter.send(SseEmitter.event().name("reply").data(temp.content));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            totalAnswer=totalAnswer+temp.content;
        }
        if (myJsonParse.header.status == 2) {
            try {
                sseEmitter.send(sseEmitter.event().name("end").data("END"));
                sseEmitter.complete();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }finally {
                webSocket.close(1000, "Session complete"); // 确保连接关闭
                SseEmitterCache.removeEmitter(userCode);   // 清除缓存
                endDate = new Date();
            }
            // 回答内容放入数据库
            if(userCode!=null&&!"".equals(userCode)){
                // 用户问题保存
                MessagePO messageQuestion = new MessagePO();
                messageQuestion.setRole("user");
                messageQuestion.setUserCode(userCode);
                messageQuestion.setCreateTime(startDate);
                messageQuestion.setContent(NewQuestion);
                messageService.saveMessage(messageQuestion);
                // ai回答存盘
                MessagePO messageResult = new MessagePO();
                messageResult.setRole("assistant");
                messageResult.setUserCode(userCode);
                messageResult.setCreateTime(endDate);
                messageResult.setContent(totalAnswer);
                messageService.saveMessage(messageResult);
            }
            wsCloseFlag = true;
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        try {
            if (null != response) {
                int code = response.code();
                System.out.println("onFailure code:" + code);
                System.out.println("onFailure body:" + response.body().string());
                if (101 != code) {
                    System.out.println("connection failed");
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    // 鉴权方法
    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        // 时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        // 拼接
        String preStr = "host: " + url.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + url.getPath() + " HTTP/1.1";
        // System.err.println(preStr);
        // SHA256加密
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        // Base64加密
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        // System.err.println(sha);
        // 拼接
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        // 拼接地址
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder().//
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8))).//
                addQueryParameter("date", date).//
                addQueryParameter("host", url.getHost()).//
                build();

        // System.err.println(httpUrl.toString());
        return httpUrl.toString();
    }

    //返回的json结果拆解
    class JsonParse {
        Header header;
        Payload payload;
    }

    class Header {
        int code;
        int status;
        String sid;
    }

    class Payload {
        Choices choices;
    }

    class Choices {
        List<Text> text;
    }

    class Text {
        String role;
        String content;
    }

}