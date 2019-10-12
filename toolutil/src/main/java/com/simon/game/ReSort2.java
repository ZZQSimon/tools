package com.simon.game;
import java.util.Scanner;

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

public class ReSort2 {

    /**
     * 
     * @param args
     */
    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);
        String source = in.nextLine();
        // String source = "A= {1,3,5},B= {2,4,6},R=1";
        // String source = "A= {1,3,5,7,8,12,13,15},B= {2,4,12,300},R=1";
        sort(source);
        in.close();
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
            int aTemp = A[i];
            boolean flag = false;
            for (int j = 0; j < B.length; j++) {
                int bTemp = B[j];
                if (aTemp > bTemp) {
                    continue;
                } else {
                    int c = bTemp - aTemp;
                    // System.out.println(aTemp + "---" + bTemp + "---" + flag);

                    if (flag) {
                        if (c > 0) {
                            System.out.print(String.format("(%d, %d)", aTemp, bTemp));
                            flag = false;
                            break;
                        }
                    } else {
                        if (c <= R) {
                            System.out.print(String.format("(%d, %d)", aTemp, bTemp));
                            break;
                        } else {
                            j = j - 1;
                            flag = true;
                            continue;
                        }
                    }

                }
            }
        }
    }

}
