 package cn.com.easyerp.core.view;
 
 import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
 
 
 
 
 
 @Service
 public class TagIdGenerator
 {
   public static final char[] DICTIONARY_16 = { 
       '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
 
 
 
   
   public static final char[] DICTIONARY_32 = { 
       '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
 
 
   
   public static final char[] DICTIONARY_62 = { 
       '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };
 
 
 
   
   public static final char[] DICTIONARY_89 = { 
       '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '+', '"', '@', '*', '#', '%', '&', '/', '|', '(', ')', '=', '?', '~', '[', ']', '{', '}', '$', '-', '_', '.', ':', ',', ';', '<', '>' };
 
   
   private static BigInteger seed = BigInteger.valueOf((new Date()).getTime());
 
 
 
   
   protected char[] dictionary;
 
 
 
 
   
   public TagIdGenerator(char[] dictionary) { this.dictionary = dictionary; }
 
 
 
 
 
 
   
   public TagIdGenerator() { this(DICTIONARY_62); }
 
 
 
 
 
   
   public static void main(String[] args) {
     TagIdGenerator tig = new TagIdGenerator(DICTIONARY_62);
     BigInteger seed = new BigInteger("21111111111111111111111");
     System.out.println(tig.encode(seed));
   }
 
 
 
 
 
 
 
 
   
   public String encode(BigInteger value) { return encode(value, ""); }
 
 
 
 
 
 
 
 
 
   
   public String encode(BigInteger value, String prefix) {
     List<Character> result = new ArrayList<Character>();
     BigInteger base = new BigInteger("" + this.dictionary.length);
     int exponent = 1;
     BigInteger remaining = value;
     while (true) {
       BigInteger a = base.pow(exponent);
       BigInteger b = remaining.mod(a);
       BigInteger c = base.pow(exponent - 1);
       BigInteger d = b.divide(c);
 
 
 
       
       result.add(Character.valueOf(this.dictionary[d.intValue()]));
       remaining = remaining.subtract(b);
 
       
       if (remaining.equals(BigInteger.ZERO)) {
         break;
       }
       
       exponent++;
     } 
 
     
     StringBuilder sb = new StringBuilder(prefix);
     for (int i = result.size() - 1; i >= 0; i--) {
       sb.append(result.get(i));
     }
     return sb.toString();
   }
 
 
 
 
 
 
 
 
 
   
   public BigInteger decode(String str) {
     char[] chars = new char[str.length()];
     str.getChars(0, str.length(), chars, 0);
     
     char[] chars2 = new char[str.length()];
     int i = chars2.length - 1;
     for (char c : chars) {
       chars2[i--] = c;
     }
 
     
     Map<Character, BigInteger> dictMap = new HashMap<Character, BigInteger>();
     int j = 0;
     for (char c : this.dictionary) {
       dictMap.put(Character.valueOf(c), new BigInteger("" + j++));
     }
     
     BigInteger bi = BigInteger.ZERO;
     BigInteger base = new BigInteger("" + this.dictionary.length);
     int exponent = 0;
     for (char c : chars2) {
       BigInteger a = (BigInteger)dictMap.get(Character.valueOf(c));
       BigInteger b = base.pow(exponent).multiply(a);
       bi = bi.add(new BigInteger("" + b));
       exponent++;
     } 
     
     return bi;
   }
 
 
 
 
 
   
   private BigInteger seeding() {
     BigInteger orig = seed;
     seed = orig.add(BigInteger.ONE);
     return orig;
   }
 
 
 
 
 
 
 
   
   public String get(String prefix) { return encode(seeding(), prefix); }
 }


