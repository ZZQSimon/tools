/**
 * DES加密算法
 * 
 * @author dwd  
 * @serialData 2010-01-20  修改为兼用1.4和1.5虚拟机
 *  
 */
package crypto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;

public class DES {

	private static String Algorithm = "DES"; // 定义 加密算法,可用 DES,DESede

	/*
	 * 加密 @param keybyte为密钥byte数组 @param src为明文byte数组 返回byte数组
	 */

	private static byte[] encryptMode(byte[] src, byte[] key) {
		SecureRandom sr = new SecureRandom();
		try {

			DESKeySpec dks = new DESKeySpec(key);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(Algorithm);
			SecretKey securekey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(Algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
			return cipher.doFinal(src);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 解密 @param keybyte为密钥byte数组 @param src为密文byte数组 返回byte数组
	 */

	private static byte[] decryptMode(byte[] src, byte[] key) {
		SecureRandom sr = new SecureRandom();
		try {
			DESKeySpec dks = new DESKeySpec(key);
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(Algorithm);
			SecretKey securekey = keyFactory.generateSecret(dks);
			Cipher cipher = Cipher.getInstance(Algorithm);
			cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
			return cipher.doFinal(src);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 将byte[]转换成字符串
	 */
	private static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";

		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}

	/*
	 * 将字符串转换成byte[]
	 */
	private static byte[] hexStr2ByteArr(String strIn) {
		byte[] arrB = strIn.getBytes();
		int iLen = arrB.length;

		// 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
		byte[] arrOut = new byte[iLen / 2];
		for (int i = 0; i < iLen; i = i + 2) {
			String strTmp = new String(arrB, i, 2);
			arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
		}
		return arrOut;
	}

	/*
	 * 加密方法 @param key为密钥 @param src为明文 返回密文
	 */
	public static String encrypt(String src, String key) throws Exception {
		String str = null;

		byte[] arr = src.getBytes();
		if ((key.length() == 8) || (key.length() == 24)) {
			if (key.length() == 8) {
				Algorithm = "DES";
				byte[] keybyte = new byte[8];
				keybyte = key.getBytes();
				str = byte2hex(encryptMode(arr, keybyte));
			} else {
				Algorithm = "DESede";
				byte[] keybyte = new byte[24];
				keybyte = key.getBytes();
				str = byte2hex(encryptMode(arr, keybyte));
			}
		} else {
			throw new Exception("密钥位数不正确！");
		}
		return str;
	}

	/*
	 * 解密方法 @param key为密钥 @param src为密文 返回明文
	 */
	public static String decrypt(String src, String key) throws Exception {
		String str = null;

		byte[] arr = hexStr2ByteArr(src);
		if ((key.length() == 8) || (key.length() == 24)) {
			if (key.length() == 8) {
				Algorithm = "DES";
				byte[] keybyte = new byte[8];
				keybyte = key.getBytes();
				byte[] arrB = decryptMode(arr, keybyte);
				str = new String(arrB);
			} else {
				Algorithm = "DESede";
				byte[] keybyte = new byte[24];
				keybyte = key.getBytes();
				byte[] arrB = decryptMode(arr, keybyte);
				str = new String(arrB);
			}
		} else {
			throw new Exception("密钥位数不正确！");
		}
		return str;
	}
}