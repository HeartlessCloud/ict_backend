package com.laojiahuo.ictproject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class SocketServer {
    public static void main(String[] args) {
        // 用于存储已连接客户端的映射，key 为客户端 IP 和端口，value 为对应的 Socket 对象
        Map<String, Socket> clientMap = new HashMap<>();

        try (ServerSocket serverSocket = new ServerSocket(1024)) {
            System.err.println("服务器启动，等待客户端连接....");

            // 进入无限循环，等待客户端连接
            while (true) {
                // 等待客户端连接
                Socket socket = serverSocket.accept();
                // 获取客户端的 IP 地址和端口号
                String ip = socket.getInetAddress().getHostAddress();
                int port = socket.getPort();
                System.err.println("有客户端连接, ip:" + ip + " 端口:" + port);

                // 将客户端的 IP 和端口作为 key 存储到 clientMap 中
                clientMap.put(ip + port, socket);

                // 启动新线程处理该客户端的输入
                new Thread(() -> {
                    try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                        String readData;

                        // 不断读取客户端发送的数据
                        while ((readData = bufferedReader.readLine()) != null) {
                            System.out.println("客户端" + port + ":" + readData);

                            // 对每个已连接的客户端发送接收到的消息
                            String finalReadData = readData; // 为了在 lambda 表达式中使用
                            clientMap.forEach((k, v) -> {
                                try {
                                    // 获取当前客户端的输出流
                                    OutputStream outputStream = v.getOutputStream();
                                    // 创建 PrintWriter 以便发送数据
                                    PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream));
                                    // 发送格式化的消息
                                    printWriter.println("用户" + port + ":" + finalReadData);
                                    printWriter.flush(); // 刷新输出流，以确保数据被发送
                                } catch (IOException e) {
                                    throw new RuntimeException(e); // 抛出运行时异常以进行处理
                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace(); // 捕获并打印输入输出异常
                    }
                }).start(); // 启动新线程
            }
        } catch (IOException e) {
            e.printStackTrace(); // 捕获并打印服务器启动异常
        }
    }
}
