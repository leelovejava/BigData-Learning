package com.atguigu.spark.streaming;

import java.io.*;
import java.net.Socket;

/**
 *  向Socket端口发送数据
 */
public class ProduceSocketDataToPort {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("192.168.179.17", 9999);
        // 向服务端程序发送数据
        OutputStream ops = socket.getOutputStream();
        OutputStreamWriter opsw = new OutputStreamWriter(ops);
        BufferedWriter bw = new BufferedWriter(opsw);
        while(true){
            System.out.println("等待输入。。。");
            bw.write(new BufferedReader(new InputStreamReader(System.in)).readLine()+"\n");
            bw.flush();
            System.out.println("写入完成");
        }
        // 从服务端程序接收数据
//        InputStream ips = socket.getInputStream();
//        InputStreamReader ipsr = new InputStreamReader(ips);
//        BufferedReader br = new BufferedReader(ipsr);
//        String s = "";
//        while((s = br.readLine()) != null)
//            System.out.println(s);
//        socket.close();
    }
}