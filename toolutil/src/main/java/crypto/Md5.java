/*
 * author dwd
 */
package crypto;

import java.security.*;

public class Md5 {

	private Md5() {
		super();

	}

	private static final byte[] hexTable = {(byte) '0', (byte) '1', (byte) '2',
			(byte) '3', (byte) '4', (byte) '5', (byte) '6', (byte) '7',
			(byte) '8', (byte) '9', (byte) 'a', (byte) 'b', (byte) 'c',
			(byte) 'd', (byte) 'e', (byte) 'f'};

	private static void encode(byte[] in, int inOff, int length, byte[] out,
			int outOff) {
		for (int i = 0, j = 0; i < length; i++, j += 2) {
			out[outOff + j] = hexTable[(in[inOff] >> 4) & 0x0f];
			out[outOff + j + 1] = hexTable[in[inOff] & 0x0f];
			inOff++;
		}

	}

	// -----
	private static byte[] encode(byte[] array) {
		return encode(array, 0, array.length);
	}

	private static byte[] encode(byte[] array, int off, int length) {
		byte[] enc = new byte[length * 2];
		encode(array, off, length, enc, 0);
		return enc;
	}

	public static String getMD5Digest(String s) throws NoSuchAlgorithmException {
		byte[] returnByte = null;
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] data = s.getBytes();
		md.update(data);
		returnByte = md.digest();
		return new String(encode(returnByte));
	}

	public static void main(String[] args) throws NoSuchAlgorithmException {
		System.out.println(getMD5Digest("koala"));
	}

}
