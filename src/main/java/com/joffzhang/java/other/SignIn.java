package com.joffzhang.java.other;

import java.time.LocalDate;
import java.time.Period;

/**
 * @author zy
 * @date 2020/8/19 10:32
 * 实现一个签到领奖励的算法，描述一下尽可能优化的数据结构 .规则是 用户一天签到一次，连续签到奖励越多，一旦不连续从头开始计算，连续八天后也从头开始计算！
 * 二进制 & | ~ ^
 * & :	有0则0
 * | :	有1则1
 * ~ :	0-1 0-1
 * ^ :	同0异1
 */
public class SignIn {

	public static void main(String[] args) {
		testSignIn2();
	}


	private static void testSignIn2() {
		SignInData signInData = new SignInData();
		for (int i = 0; i < 20; i++) {
			signInData.doSignIn(i);
			System.out.println(signInData.byteToBit(signInData.signIn));
		}

	}

	static class SignInData{
		byte signIn;
		int point = 8;
		//  day  0-7
		//判断是否连续  0000 0111 第4天判断连续  判断3位置是否为1
		boolean isSerial(int day){
			return ((signIn >> (day-1)) & 0x1) == 1;
		}

		void doSignIn(int day){
			day %= point;
			if(day != 0 && isSerial(day)){//如果连续
				signIn |= (1 << day);
			}else{
				signIn = 1;
			}
		}

		/**
		 * byte转二进制
		 * @param b
		 * @return
		 */
		String byteToBit(byte b){
			return new StringBuilder()
					.append((byte)((b >> 7) & 0x1))
					.append((byte)((b >> 6) & 0x1))
					.append((byte)((b >> 5) & 0x1))
					.append((byte)((b >> 4) & 0x1))
					.append((byte)((b >> 3) & 0x1))
					.append((byte)((b >> 2) & 0x1))
					.append((byte)((b >> 1) & 0x1))
					.append((byte)((b >> 0) & 0x1)).toString();
		}
	}


}
