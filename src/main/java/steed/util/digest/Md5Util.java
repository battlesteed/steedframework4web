package steed.util.digest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import steed.util.base.StringUtil;

public class Md5Util {
	public static String Md5Digest(String data){
		return DigestUtil.byte2hex(Md5Digest(StringUtil.getSystemCharacterSetBytes(data)));
	}
	
	public static byte[] Md5Digest(byte[] data){
		   MessageDigest sha256;
		try {
			sha256 = MessageDigest.getInstance("MD5");
			byte[] passHash = sha256.digest(data);
			return passHash;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			throw new RuntimeException("获取MD5加密实例失败!!!");
		}        
	}
}