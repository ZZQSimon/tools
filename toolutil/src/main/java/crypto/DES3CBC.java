/**
 * 
 */
package crypto;

/**
 * @serial 2010-3-2 上午09:59:43
 * @author dwd
 * @version 1.0
 * @see
 */

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.spec.AlgorithmParameterSpec;

public class DES3CBC {
	// private static int BLOCK_SIZE = 8;

	public static void main(String[] args) {
		try {
			DES3CBC enc = new DES3CBC();
			String value = enc.Encrypt("12345678", "ABCDEFGH");
			System.err.println(value);
		} catch (Exception ex) {
			System.err.println(ex);
		}
	}

	public String Encrypt(String inkey, String data) throws Exception {
		//  start //byte[] iv = new byte[]{(byte)0x8E, 0x12, 0x39, (byte)0x9C,0x07, 0x72,
		// 0x6F,
		// 0x5A};
		byte[] iv = { 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40, 0x40 };

		@SuppressWarnings("unused")
		AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);

		// convert key to byte array and get it into a key object
		byte[] rawkey = inkey.getBytes();
		DESedeKeySpec keyspec = new DESedeKeySpec(rawkey);
		SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede");
		SecretKey key = keyfactory.generateSecret(keyspec);

		Cipher c2 = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		// ----------------start c2.init(Cipher.ENCRYPT_MODE, key, paramSpec);

		// c2.init( Cipher.ENCRYPT_MODE, key );

		@SuppressWarnings("unused")
		byte encodedParameters[] = c2.getParameters().getEncoded();
		byte[] out = c2.doFinal(data.getBytes());

		Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		// byte[] out = cipher.doFinal( padString(data).getBytes() );
		out = cipher.doFinal(data.getBytes());

		// String tst = byteArrayToHexString( out );
		return new sun.misc.BASE64Encoder().encode(out);

		// return byteArrayToHexString( out );
	}

	@SuppressWarnings("unused")
	private String byteArrayToHexString(byte in[]) {
		byte ch = 0x00;
		int i = 0;

		if (in == null || in.length <= 0)
			return null;

		String pseudo[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F" };

		StringBuffer out = new StringBuffer(in.length);

		while (i < in.length) {
			ch = (byte) (in[i] & 0xF0);
			ch = (byte) (ch >>> 4);
			ch = (byte) (ch & 0x0F);
			out.append(pseudo[(int) ch]);
			ch = (byte) (in[i] & 0x0F);

			out.append(pseudo[(int) ch]);
			i++;
		}

		String rslt = new String(out);
		return rslt;
	}

}