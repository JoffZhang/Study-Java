package com.joffzhang.java;

import com.joffzhang.java.base.BaseObj;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.ByteOrder;



/**
 * @author zy
 * @date 2020/8/18 16:27
 */
public class Test {

	public static void main(String[] args) throws ClassNotFoundException {
		Integer i = 0;
		System.out.println("0".equals(i));
		//bigOrLittleEndian();

	}

	public static void bigOrLittleEndian(){
		Unsafe unsafe = getUnsafe();
		long a = unsafe.allocateMemory(8);
		unsafe.putLong(a,0x0102030405060708L);
		//存放此long类型数据，实际存放占8个字节，01,02,03，04,05,06,07,08
		byte b = unsafe.getByte(a);
		//通过getByte方法获取刚才存放的long，取第一个字节
		//如果是大端，long类型顺序存放—》01,02,03,04,05,06,07,08  ，取第一位便是0x01
		//如果是小端，long类型顺序存放—》08,07,06,05,04,03,02,01  ，取第一位便是0x08
		ByteOrder byteOrder;
		switch (b){
			case 0x01:
				byteOrder = ByteOrder.BIG_ENDIAN;
				break;
			case 0x08:
				byteOrder = ByteOrder.LITTLE_ENDIAN;
				break;
			default:
				assert false;
				byteOrder = null;
		}
		System.out.println(byteOrder.toString());
		System.out.println(ByteOrder.nativeOrder());
	}

	private static Unsafe getUnsafe(){
		try{
			//获取Unsafe内部的私有实例化单例对象
			Field filed = Unsafe.class.getDeclaredField("theUnsafe");
			//无视权限
			filed.setAccessible(true);
			return (Unsafe) filed.get(null);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void testBit(){
		byte i = 127;
		i <<= 1;
		i+=1;
		System.out.println(0x1);
		i <<= 1;
		i+=1;
		System.out.println(i);
	}
	public static String byteToBit(byte b){
		return ""+(byte)((b >> 7) & 0x1);
	}
}
