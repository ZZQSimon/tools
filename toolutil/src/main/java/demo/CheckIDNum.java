package demo;

/**
 * 本程序实现的是现代居民身份证号的输入验证
 * 
 * 现代居民身份证号的特点：
 * 1）18位，前六位为地区码，全数字
 * 2）中间八位为生日日期，后四位为随机数字
 * 3）其中最后一位可以是数字，也可以是字母x
 * 
 * 本程序主要完成功能：
 * 1）输入长度检验
 * 2）输入字符的检验
 * 3）首位非零的检验
 * 4）身份证号中生日日期的检验
 * 
 * 本程序遗留的问题：
 * 1）由于作者本人不清楚前六位地区码的命名规则，故不能完成地区码的检测
 * 2）后四位随机字符串的产生规则不清楚，也无法检验
 * 3）由于现在是2011年，该检验只完成2011年（包含2011年）之前的日期检验，后期使用可以自主添加修改
 * 制作人：Jackeen Zhang
 * 2011.06.22
 */
import java.util.Scanner;

public class CheckIDNum {
    private static String sID;
    private static char[] c;

    // ---------------------------------------------------------------------------------------------------------
    // 创建检验方法
    static void Check(String s) {
        // -----------------------------------------------------------------------------------------------------
        int a = s.length(); // 获取字符串长度
        if (a != 18) { // 检验输入字符串长度，不是18就报错退出
            System.out.println("不是正确的身份证号，输入位数错误");
            return;
        } else {
            c = new char[18]; // 将字符串转化为字符数组
            for (int i = 0; i < 18; i++) {
                c[i] = s.charAt(i);
            }
            // ------------------------------------------------------------------------------------------------------
            // 检验输入号码的格式
            for (int i = 0; i < 17; i++) { // 前十七位必须是数字
                if (c[i] > '9') {
                    System.out.println("不是正确的身份证号，第" + (i + 1) + "个字符出现非数字错误");
                }
            }
            if (c[0] == '0') { // 身份证号开头不能是0
                System.out.println("不是正确的身份证号，第一个数字不能为0");
                return;
            }
            if (c[17] != 'x' && c[17] > '9') { // 最后一位必须是数字或者字母“x”
                System.out.println("不是正确的身份证号，最后一个字符错误");
                return;
            }
            // ------------------------------------------------------------------------------------------------------
            // 检验年份
            if (c[6] != '1' && c[6] != '2') { // 由于身份证是近代出现的，年份上只能是1900至今，所以千位只能是1或2
                System.out.println("不是正确的身份证号，日期有误，第7个字符错误");
                return;
            }
            if (c[6] == '1' && c[7] != '9') { // 年份首位是1，次位不是9，报错退出
                System.out.println("不是正确的身份证号，日期年份有误，第8个字符错误");
                return;
            }
            if (c[6] == '2' && c[7] != '0') { // 年份首位是2，次位不是0，报错退出
                System.out.println("不是正确的身份证号，日期年份有误，第8个字符错误");
                return;
            }
            if (c[6] == '2' && c[7] == '0') {
                if (c[8] != '0' && c[8] != '1') { // 年份首位2次位0，但第三位不是0或1，年份越界错误
                    System.out.println("不是正确的身份证号，日期年份有误，现在不到时间，第9个字符错误");
                    return;
                }
                if (c[8] == '1') {
                    if (c[9] != '0' && c[9] != '1') { // 2000年以后，年份不是2010或者2011，年份越界错误
                        System.out.println("不是正确的身份证号，日期年份有误，现在不到时间，第10个字符错误");
                        return;
                    }
                }
            }
            // ------------------------------------------------------------------------------------------------------
            // 检验月份
            if (c[10] != '1' && c[10] != '0') { // 月份首位出现非0非1的数字，报错退出
                System.out.println("不是正确的身份证号，日期月份有误，第11个字符错误");
                return;
            }
            if (c[10] == '0') { // 月份出现00，报错退出
                if (c[11] == '0') {
                    System.out.println("不是正确的身份证号，日期月份有误，第12个字符错误");
                    return;
                }
            }
            if (c[10] == '1') { // 月份出现非10、11、12，报错退出
                if (c[11] != '0' && c[11] != '1' && c[11] != '2') {
                    System.out.println("不是正确的身份证号，日期月份有误，第12个字符错误");
                    return;
                }
            }
            // ------------------------------------------------------------------------------------------------------
            // 检验日期格式
            if (c[12] != '0' && c[12] != '1' && c[12] != '2' && c[12] != '3') { // 日子首位出现非0、1、2、3，报错退出
                System.out.println("不是正确的身份证号，日期有误，第13个字符错误");
                return;
            }
            if (c[12] == '0') { // 日子出现00，报错退出
                if (c[13] == '0') {
                    System.out.println("不是正确的身份证号，日期有误，第14个字符错误");
                    return;
                }
            }
            // ------------------------------------------------------------------------------------------------------
            // 检验日期
            // --------------------------------------------------------------------------------------------------
            // 月份为2的时候，需要判断是否为闰年，从而判断2月29号的正确性
            if (c[12] == '2' && c[10] == '0' && c[11] == '2' && c[8] == '0' && c[9] == '0') { // 年份后两位是00的闰年判断
                int q, b, t, g, n; // 将年份整形化，以便于计算判断闰年
                q = (int) c[6] - 48;
                b = (int) c[7] - 48;
                t = (int) c[8] - 48;
                g = (int) c[9] - 48;
                n = 1000 * q + 100 * b + 10 * t + g;
                if (n % 400 != 0 && c[13] == '9') { // 年份后两位00，能否被400整除
                    System.out.println("不是正确的身份证号，日期有误，第14个字符错误");
                    return;
                }
            }
            if (c[12] == '2' && c[10] == '0' && c[11] == '2') { // 其他年份的闰年判断
                int q, b, t, g, n;
                q = (int) c[6] - 48;
                b = (int) c[7] - 48;
                t = (int) c[8] - 48;
                g = (int) c[9] - 48;
                n = 1000 * q + 100 * b + 10 * t + g;

                if (n % 4 != 0 && c[13] == '9') { // 其他形式年份，能否被4整除
                    System.out.println("不是正确的身份证号，日期有误，第14个字符错误");
                    return;
                }
            }
            // ----------------------------------------------------------------------------------------------
            // 在4、6、9、11月份出现31号的报错
            if (c[12] == '3' && c[10] == '0') {
                if ((c[11] == '4' || c[11] == '6' || c[11] == '9') && c[13] == '1') { // 4、6、9月份的31号报错
                    System.out.println("不是正确的身份证号，日期有误，第14个字符错误");
                    return;
                }
            }
            if (c[12] == '3' && c[10] == '1' && c[11] == '1' && c[13] == '1') { // 11月份的31号报错
                System.out.println("不是正确的身份证号，日期有误，第14个字符错误");
                return;
            }
            // -----------------------------------------------------------------------------------------------------
            // 检验通过，没有错误，输出正确的身份证号
            System.out.println("恭喜你，输入正确的身份证号，号码为：\n" + s);
        }
    }

    // ---------------------------------------------------------------------------------------------------------
    // 主方法，由控制台输入身份证号，调用检验方法，进行检测
    public static void main(String[] args) {
        System.out.println("请输入身份证号码：");
        Scanner sk = new Scanner(System.in);
        sID = sk.next();
        Check(sID);
        sk.close();
    }
}
