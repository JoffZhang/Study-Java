package com.joffzhang.java.io;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author zy
 * @date 2020/8/24 15:40
 * 字节操作IO InputStream、OutputStream
 */
public class ByteIO {

	public static void inputstream(){
		try(
				InputStream inputStream = new FileInputStream("D:\\ideaWorkSpace\\microservice-springcloud\\Study-Java\\src\\main\\java\\com\\joffzhang\\java\\demo.txt");
		) {
			byte[] bytes = new byte[inputStream.available()];
			inputStream.read(bytes);
			String str = new String(bytes);
			System.out.println(str);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void outputstream(){
		try (
				OutputStream outputStream = new FileOutputStream("D:\\ideaWorkSpace\\microservice-springcloud\\Study-Java\\src\\main\\java\\com\\joffzhang\\java\\demo.txt",true);
		){
			outputStream.write("你好，老王".getBytes());
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args){

		outputstream();

		inputstream();

	}


}
