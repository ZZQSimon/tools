package com.simon.game;
/**
	* Title: ReSort.java <br>
	* Description:[] <br>
	* Copyright (c)  2018<br>
	* Company: <br>
	* @Date 2019.09.11 <br>
	* 
	* @author Simon Zhang
	* @version V1.0
	*/

/**
 * @ClassName: ReSort <br>
 * @Description: [] <br>
 * @date 2019.09.11 <br>
 * 
 * @author Simon Zhang
 */

public class ReSort {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {

        String source = "A= {1,3,5},B= {2,4,6},R=1";
        sort(source);
    }

    public static void sort(String source) {
        // System.out.println(source);
        source = source.replace(" ", "");
        // System.out.println(source);
        int aStart = source.indexOf("A=");
        int bStart = source.indexOf("B=");
        int rStart = source.indexOf("R=");
        // System.out.println(aStart);
        // System.out.println(bStart);
        // System.out.println(rStart);

        String a = source.substring(aStart + 3, bStart - 2);
        String b = source.substring(bStart + 3, rStart - 2);
        String r = source.substring(rStart + 2);

        // System.out.println(a);
        // System.out.println(b);
        // System.out.println(r);

        String as[] = a.split(",");
        String bs[] = b.split(",");
        int A[] = new int[as.length];
        int B[] = new int[bs.length];
        for (int i = 0; i < A.length; i++) {
            A[i] = Integer.parseInt(as[i]);
        }
        for (int i = 0; i < B.length; i++) {
            B[i] = Integer.parseInt(bs[i]);
        }

        int R = Integer.parseInt(r);

        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < B.length; j++) {
                int c = B[j] - A[i];
                if (c <= R && c >= 0) {
                    System.out.print(String.format("(%d, %d)", A[i], B[j]));
                    continue;
                }
            }
        }
    }

}
