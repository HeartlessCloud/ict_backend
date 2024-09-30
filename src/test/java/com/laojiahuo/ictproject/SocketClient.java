package com.laojiahuo.ictproject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class SocketClient {
    public static void main(String[] args) {
        Socket socket = null; // 声明 Socket 对象
        try {
            // 连接到本地的 1024 端口
            socket = new Socket("127.0.0.1", 1024);
            // 获取输出流，用于发送消息
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream); // 创建 PrintWriter 以发送数据

            System.out.println("请输入内容:");

            // 创建新线程用于处理用户输入
            new Thread(() -> {
                while (true) {
                    Scanner scanner = new Scanner(System.in); // 创建 Scanner 对象用于读取输入
                    String input = scanner.nextLine(); // 读取用户输入
                    try {
                        printWriter.println(input); // 将输入写入输出流
                        printWriter.flush(); // 刷新输出流，确保数据被发送
                    } catch (Exception e) {
                        break; // 如果发生异常，退出循环
                    }
                }
            }).start(); // 启动线程

            // 创建 BufferedReader 以读取服务器发送的消息
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 创建新线程用于接收服务器消息
            new Thread(() -> {
                while (true) {
                    try {
                        String readData = bufferedReader.readLine(); // 读取服务器发送的数据
                        System.out.println(readData); // 输出接收到的消息
                    } catch (Exception e) {
                        e.printStackTrace(); // 打印异常信息
                        break; // 如果发生异常，退出循环
                    }
                }
            }).start(); // 启动线程
        } catch (Exception e) {
            e.printStackTrace(); // 捕获并打印异常信息
        }
    }
}
