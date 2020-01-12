package com.longyb.mylive.server.rtmp;

import java.util.Random;

/**
@author longyubo
2019年12月10日 下午8:49:19
**/
public class Tools {
	private static Random random = new Random();;
	public static byte[] generateRandomData(int size) {
		byte[] bytes = new byte[size];
		random.nextBytes(bytes);
		return bytes;
	}
}

