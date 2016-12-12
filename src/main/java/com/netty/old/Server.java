package com.netty.old;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

  @SuppressWarnings("resource")
  public static void main(String args[]) throws Exception {
    ServerSocket server = new ServerSocket(4700);
    while (true) {
      /**
       * 没有客户端连接的时候，直接在此阻塞
       * */
      Socket connection = server.accept();
      handleRequest(connection);
      connection.close();
    }
  }

  private static void handleRequest(Socket socket) throws IOException {
    BufferedReader is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    PrintWriter os = new PrintWriter(socket.getOutputStream());
    int flag = 0;
    while (flag < 100) {
      String line = is.readLine();
      if (line != null) {
        System.out.println("server receive: " + line);
        os.println("server send: " +flag+"  :" + Math.random());
        os.flush();
      }
    }
    os.close(); 
    is.close(); 
  }
}

/**
 * server 起来的时候，等待Client端的连接，阻塞在：server.accept();
 * client连接以后，在读取数据的时候，如果客户端没有写入数据，这个时候也是会阻塞的：is.readLine()
 * **/
