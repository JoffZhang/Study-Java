package com.joffzhang.java.io;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author zy
 * @date 2020/8/24 16:50
 * 字符IO Writer 、Reader
 */
public class CharIO {

	private static void reader() {
		try(
				Reader reader = new FileReader("D:\\ideaWorkSpace\\microservice-springcloud\\Study-Java\\src\\main\\java\\com\\joffzhang\\java\\demo.txt");
				BufferedReader bufferedReader = new BufferedReader(reader);
		){
			StringBuffer stringBuffer = new StringBuffer();
			String str;
			while((str = bufferedReader.readLine()) != null){
				stringBuffer.append(str+"\n");
			}
			System.out.println(stringBuffer.toString());
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private static void writer() {
		try (
				Writer writer = new FileWriter("D:\\ideaWorkSpace\\microservice-springcloud\\Study-Java\\src\\main\\java\\com\\joffzhang\\java\\demo.txt",true); //true追加，false，重写
		){
			writer.append("老王,你好");
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	private static void writereadJDK7(){
		try{
			//写文件
			Files.write(Paths.get("D:\\ideaWorkSpace\\microservice-springcloud\\Study-Java\\src\\main\\java\\com\\joffzhang\\java\\demo.txt"), "老王阿".getBytes(StandardCharsets.UTF_8),StandardOpenOption.APPEND);

			//读文件
			byte[] data = Files.readAllBytes(Paths.get("D:\\ideaWorkSpace\\microservice-springcloud\\Study-Java\\src\\main\\java\\com\\joffzhang\\java\\demo.txt"));
			System.out.println(new String(data,StandardCharsets.UTF_8));
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		writer();

		reader();

		writereadJDK7();
	}

}
