package com.joffzhang.java.io.socket.bio;

import com.joffzhang.java.io.socket.Calculator;

import javax.script.ScriptException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author zy
 * @date 2020/8/26 11:33
 * 客户端消息处理线程ServerHandler
 * 用于处理一个客户端的Socket链路
 */
public class ServerHandler implements Runnable{

	private Socket socket;
	public ServerHandler(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try (
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
		){
			String expression;
			String result;
			while(true){
				//通过BufferedReader读取一行
				//如果已经读到输入流尾部，返回null,退出循环
				//如果得到非空值，就尝试计算结果并返回
				if((expression = in.readLine()) == null) {break;}
				System.out.println("服务器收到消息：" + expression);
				try {
					result = Calculator.cal(expression).toString();
				} catch (ScriptException e) {
					result = "计算错误：" + e.getMessage();
				}
				out.println(result);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(socket != null){
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				socket = null;
			}

		}
	}
}
