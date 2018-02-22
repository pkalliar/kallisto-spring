package com.pankal.utilities;

import java.security.MessageDigest;

public class Utilities {

	public static String digest(MessageDigest messageDigest, String toHash){
		StringBuffer sb = new StringBuffer();
		messageDigest.update(toHash.getBytes());
		byte[] mdbytes = messageDigest.digest();
		//convert the byte to hex format method 1
		for (int i = 0; i < mdbytes.length; i++) {
			sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}
}
