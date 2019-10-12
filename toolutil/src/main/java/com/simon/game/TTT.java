package com.simon.game;
/**
	* Title: TTT.java <br>
	* Description:[] <br>
	* Copyright (c)  2018<br>
	* Company: <br>
	* @Date 2019.09.11 <br>
	* 
	* @author Simon Zhang
	* @version V1.0
	*/

/**
 * @ClassName: TTT <br>
 * @Description: [] <br>
 * @date 2019.09.11 <br>
 * 
 * @author Simon Zhang
 */

import java.util.Scanner;

/**
 * @ClassName: TTT <br>
 * @Description: [吕阿欢面试题] <br>
 * @date 2019.10.12 <br>
 * 
 * @author Simon Zhang
 */
public class TTT {
    // A={1,3,5},B={2,4,6},R=1
    public static void main(String[] args) {
        System.out.println("请输入格式如：A={1,3,5},B={2,4,6},R=1的字符串...");
        Scanner sc = new Scanner(System.in);
        String line1 = sc.nextLine();
        String[] array = line1.split(",");
        int length = array.length;
        int[] A = new int[(length - 1) / 2];
        int[] B = new int[(length - 1) / 2];
        for (int i = 0; i < length - 1; i++) {
            if (i == 0)
                A[i] = ((array[0].charAt(3) - '0'));
            else if (i < (length - 1) / 2 - 1)
                A[i] = array[i].charAt(0) - '0';
            else if (i == (length - 1) / 2 - 1)
                A[i] = Integer.valueOf(array[i].charAt(0) - '0');
            else if (i == (length - 1) / 2)
                B[0] = Integer.valueOf(array[i].charAt(3) - '0');
            else if (i > (length - 1) / 2 && i < length - 2)
                B[i - (length - 1) / 2] = Integer.valueOf(array[i].charAt(0) - '0');
            else
                B[i - (length - 1) / 2] = Integer.valueOf(Integer.valueOf(array[i].charAt(0) - '0'));
        }
        int r = array[length - 1].charAt(2) - '0';
        int count = 0;
        int index = -1;
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < B.length; j++) {
                if (B[j] >= A[i]) {
                    if (index == -1)
                        index = j;
                    if (B[j] - A[i] <= r) {
                        System.out.print("(");
                        System.out.print(A[i]);
                        System.out.print(",");
                        System.out.print(B[j]);
                        System.out.print(")");
                        count++;
                    }
                }
                if (count == 0 && index != -1) {
                    System.out.print("(");
                    System.out.print(A[i]);
                    System.out.print(",");
                    System.out.print(B[index]);
                    System.out.print(")");
                }
                index = -1;
            }
        }
        sc.close();
    }

}
