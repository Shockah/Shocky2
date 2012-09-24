package pl.shockah;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Security {
	public static String md5(BinBuffer binb) throws NoSuchAlgorithmException {
		MessageDigest alg = MessageDigest.getInstance("MD5");
		alg.reset();
		alg.update(binb.getByteBuffer());
		
		byte[] digest = alg.digest();
		StringBuffer sb = new StringBuffer();
		
		for (byte b : digest) sb.append(Integer.toHexString(0xFF & b));
		return sb.toString();
	}
	public static String md5(String text) throws NoSuchAlgorithmException {
		MessageDigest alg = MessageDigest.getInstance("MD5");
		alg.reset();
		alg.update(text.getBytes(Charset.forName("UTF-8")));
		
		byte[] digest = alg.digest();
		StringBuffer sb = new StringBuffer();
		
		for (byte b : digest) sb.append(Integer.toHexString(0xFF & b));
		return sb.toString();
	}
}