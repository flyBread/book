package com.netty.old;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
  public void run() {
    Socket socket = null;
    try {
      /**
       * server没有启动的情况下：Errorjava.net.ConnectException: Connection refused
       * */
      socket = new Socket("127.0.0.1", 4700);
      PrintWriter clientWrite = new PrintWriter(socket.getOutputStream());
      BufferedReader clientReceive = new BufferedReader(new InputStreamReader(socket
          .getInputStream()));
      int flag = 0;
      while (flag < 100) {
        clientWrite.println("talkClient send : "+flag+" :" + System.currentTimeMillis());
        clientWrite.flush(); 
        String line = clientReceive.readLine();
        System.out.println("talkClient receive:" + line);
        flag += 2;
      } 

      clientReceive.close();
      clientWrite.close();
      socket.close();
    }
    catch (Exception e) {
      System.out.println("Error" + e);  
    }
  }

  public static void main(String args[]) {
    Client client = new Client();
    client.run();
  }
}

/**
 * server没有启动的时候，client连接的直接拒绝，这个也符合设定。
 * 连接以后，在读取server的数据的时候，如果server没有写数据，那么也是会阻塞在读取数据的地方：
 * clientReceive.readLine();
 * 
 * 完成任务后，Client关闭了，但是server仍然的存在（废话了）
 * **/
