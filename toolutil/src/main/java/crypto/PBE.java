package crypto;


import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.Random;


/**
 * <p>封装同PBE（基于口令的加密）算法有关的方法，可用使用PBE算法加密和解密</p>
 * @Copyright:WDSsoft
 * @ad:WDSsoft “企业多级数字签名系统”- 最佳的企业电子文档多级数字签名方案
 * @URL:www.wdssoft.com
 * @作者 常蕴秋  mdss@wdssoft.com bluesunday@sohu.com
 */


public class PBE
{

  public PBE() {  }
  /**
   *测试
   */
  public static void main(String[] args)
  {
    String tt=new String("java");
        char[] password=tt.toCharArray();
    byte[] pp;
        PBE pbe=new PBE();
        try{

            pp=pbe.PBEunwrap(pbe.PBEwrap(tt.getBytes("UTF8"),password),password);
            System.out.print(new String(pp));

    }
        catch(Exception e){}
  }
  /**
 * 使用PBE加密byte[]，随即撒盐，和unwrap方法配套使用
 * @param byte[] target：PBE加密对象
 * @param char[] password: PBE加密口令
 * @return Object[] object[0]1:byte[]:密文; object[1]:byte[]:盐
 */
   @SuppressWarnings("static-access")
public  Object[] PBEwrap(byte[] target,char[] password){
   try{

          PBEKeySpec keySpec=new PBEKeySpec(password);
          SecretKeyFactory keyFactory=SecretKeyFactory.getInstance("PBEWithMD5AndDES");
          SecretKey key=keyFactory.generateSecret(keySpec);
          Cipher cipher=Cipher.getInstance("PBEWithMD5AndDES");
          byte[] salt=new byte[8];
          Random random=new Random();
          random.nextBytes(salt);
          PBEParameterSpec paramSpec=new PBEParameterSpec(salt,100);
          cipher.init(cipher.ENCRYPT_MODE,key,paramSpec);
          Object[] encryptedByte=new Object[2];
          encryptedByte[0]=cipher.doFinal(target);
          encryptedByte[1]=salt;
          return encryptedByte;

       }catch(Exception e){e.printStackTrace();
        return null;
        }
    }
    /**
   * 使用PBE加密byte[]，撒固定的盐，和unwrape_2配套使用
   * @param byte[] target：PBE加密对象
   * @param char[] password: PBE加密口令
   * @return byte[] :加密后的密文
 */
  @SuppressWarnings("static-access")
public  byte[] PBEwrap_2(byte[] target,char[] password){
   try{
          PBEKeySpec keySpec=new PBEKeySpec(password);
          SecretKeyFactory keyFactory=SecretKeyFactory.getInstance("PBEWithMD5AndDES");
          SecretKey key=keyFactory.generateSecret(keySpec);
          Cipher cipher=Cipher.getInstance("PBEWithMD5AndDES");
          //固定的盐，实际试用时可自行更改盐，但要和unwrape_2一致
          byte[] salt=new byte[8];
          salt[2]=salt[4]=salt[7]=11;
          salt[1]=salt[3]=salt[5]=36;
          salt[0]=10;
          PBEParameterSpec paramSpec=new PBEParameterSpec(salt,100);
          cipher.init(cipher.ENCRYPT_MODE,key,paramSpec);
          return cipher.doFinal(target);


       }catch(Exception e){e.printStackTrace();
        return null;
        }
    }



/**
 * PBE解密，和wrap配套使用
 * @param Object[] encrypted: object[0] byte[]:密文 object[1]:byte[]:盐
 * @param char[] password:口令
 * @return byte[] 明文
 */
  @SuppressWarnings("static-access")
public  byte[] PBEunwrap(Object[] encrypted,char[] password){
      try{

          byte[] wraped=(byte[])encrypted[0];
          byte[] salt=(byte[])encrypted[1];
          PBEKeySpec keySpec=new PBEKeySpec(password);
          SecretKeyFactory keyFactory=SecretKeyFactory.getInstance("PBEWithMD5AndDES");
          SecretKey key=keyFactory.generateSecret(keySpec);
          Cipher cipher=Cipher.getInstance("PBEWithMD5AndDES");
          PBEParameterSpec paramSpec=new PBEParameterSpec(salt,100);
          cipher.init(cipher.DECRYPT_MODE,key,paramSpec);
          return cipher.doFinal(wraped);


    }catch(Exception e){

      return null;}
  }

  /**
 * PBE解密，和wrap_2配套使用
 * @param byte[] encrypted:密文
 * @param char[] password:口令
 * @return byte[] 明文
 */

  @SuppressWarnings("static-access")
public  byte[] PBEunwrap_2(byte[] encrypted,char[] password){
      try{

          byte[] wraped=encrypted;

          //固定的盐，实际试用时可自行更改盐，但要和unwrap_2一致
           byte[] salt=new byte[8];
          salt[2]=salt[4]=salt[7]=11;
          salt[1]=salt[3]=salt[5]=36;
          salt[0]=10;
          PBEKeySpec keySpec=new PBEKeySpec(password);
          SecretKeyFactory keyFactory=SecretKeyFactory.getInstance("PBEWithMD5AndDES");
          SecretKey key=keyFactory.generateSecret(keySpec);
          Cipher cipher=Cipher.getInstance("PBEWithMD5AndDES");
          PBEParameterSpec paramSpec=new PBEParameterSpec(salt,100);
          cipher.init(cipher.DECRYPT_MODE,key,paramSpec);
          return cipher.doFinal(wraped);


    }catch(Exception e){

      return null;}
  }
}