package com.joffzhang.java.io.socket;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.time.LocalDate;

/**
 * @author zy
 * @date 2020/8/24 17:58
 */
public class Client {

	public static void main(String[] args) {
		new Thread(()->{

				try (
						Socket socket = new Socket(InetAddress.getLocalHost(),3333);
				){
						writeData(socket);

						readData(socket);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

		}).start();
	}

	private static void readData(Socket socket) {
		try{
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bufferedReader.lines().forEach(s -> System.out.println("客户端读取：" + s));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeData(Socket socket) throws IOException, InterruptedException {
		// Socket 客户端（接收信息并打印）
		try{
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			bufferedWriter.write(Charset.defaultCharset().encode("客户端发送").toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
