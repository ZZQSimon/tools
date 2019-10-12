package demo;

import java.util.Scanner;

public class CaiQuan {
    Scanner input = new Scanner(System.in);
    String p1chuquan;
    String p2chuquan;
    int mod, p1, p2;
    int chuquanp1;
    int chuquanp2;
    int win = 0, lose = 0, he = 0, winx = 0;
    String p1name, p2name;
    int score;

    public void top() {
        System.out.print("-----------------------------欢 迎 进 入 游 戏 世 界-----------------------------\n\n");
        System.out.print("                             *******************\n");
        System.out.print("                             ** 猜拳 V 1.0开始  **\n");
        System.out.print("                             *******************\n\n\n");
        System.out.print("-----------------------------版权所有       违者必究-----------------------------\n\n");
        System.out.print("请选择您喜爱的游戏模式：\n");
        boolean t5 = true;
        while (t5) {
            System.out.print("1.我要虐待电脑\t2.双人鏖战\n\n");
            mod = input.nextInt();
            switch (mod) {
            case 1:
                System.out.print("出拳规则：1.剪刀 2.石头 3.布\n");
                pve();
                t5 = false;
                break;
            case 2:
                System.out.print("出拳规则：1.剪刀 2.石头 3.布\n");
                pvp();
                t5 = false;
                break;
            default:
                System.out.print("您的输入有误，请重新选择！\n");
            }
        }
    }

    public void pvp() {
        boolean t6 = true;
        while (t6) {
            System.out.print("请第一位玩家选择角色(1：刘备 2：孙权 3：曹操)：\n");
            p1 = input.nextInt();
            switch (p1) {
            case 1:
                p1name = "刘备";
                t6 = false;
                break;
            case 2:
                p1name = "孙权";
                t6 = false;
                break;
            case 3:
                p1name = "曹操";
                t6 = false;
                break;
            default:
                System.out.print("输入错误！请重新选择！\n");
                continue;
            }
        }
        boolean t7 = true;
        while (t7) {
            System.out.print("请第二位玩家选择角色(1：刘备 2：孙权 3：曹操)：\n");
            p2 = input.nextInt();
            switch (p2) {
            case 1:
                p2name = "刘备";
                t7 = false;
                break;
            case 2:
                p2name = "孙权";
                t7 = false;
                break;
            case 3:
                p2name = "曹操";
                t7 = false;
                break;
            default:
                System.out.print("输入错误！请重新选择！\n");
                continue;
            }
        }
        if (p1name.equals(p2name)) {
            p1name = p1name + 1;
            p2name = p2name + 2;
        }
        System.out.println("玩家一选择<<<" + p1name + ">>>开始了征程！ ");
        System.out.println("玩家二选择<<<" + p2name + ">>>与您一决高下！\n");
    }

    public void pve() {
        boolean t1 = true;
        while (t1) {
            System.out.print("请选择角色(1：刘备 2：孙权 3：曹操)：\n");
            p1 = input.nextInt();
            p2 = (int) (Math.random() * 2);
            switch (p1) {
            case 1:
                p1name = "刘备";
                if (p2 == 0) {
                    p2name = "孙权";
                } else {
                    p2name = "曹操";
                }
                t1 = false;
                break;
            case 2:
                p1name = "孙权";
                if (p2 == 0) {
                    p2name = "刘备";
                } else {
                    p2name = "曹操";
                }
                t1 = false;
                break;
            case 3:
                p1name = "曹操";
                if (p2 == 0) {
                    p2name = "刘备";
                } else {
                    p2name = "孙权";
                }
                t1 = false;
                break;
            default:
                System.out.print("输入错误！请重新选择！\n");
            }
        }
        System.out.println("您选择<<<" + p1name + ">>>开始了征程！ ");
        System.out.println("电脑决定用<<<" + p2name + ">>>与您一决高下！\n");
    }

    public void chuquan() {
        boolean t2 = true;
        while (t2) {
            System.out.print("\n请玩家一出拳：1.剪刀 2.石头 3.布 (输入相应的数字)：");
            chuquanp1 = input.nextInt();
            switch (chuquanp1) {
            case 1:
                p1chuquan = "剪刀";
                t2 = false;
                break;
            case 2:
                p1chuquan = "石头";
                t2 = false;
                break;
            case 3:
                p1chuquan = "布";
                t2 = false;
                break;
            default:
                System.out.print("输入错误！请重新选择！\n");
                continue;
            }
        }
        switch (mod) {
        case 1:
            chuquanp2 = (int) (Math.random() * 3);
            switch (chuquanp2) {
            case 0:
                p2chuquan = "剪刀";
                break;
            case 1:
                p2chuquan = "石头";
                break;
            case 2:
                p2chuquan = "布";
                break;
            }
            break;
        case 2:
            boolean t8 = true;
            while (t8) {
                System.out.print("\n请玩家二出拳：1.剪刀 2.石头 3.布 (输入相应的数字)：");
                chuquanp2 = input.nextInt();
                switch (chuquanp2) {
                case 1:
                    p2chuquan = "剪刀";
                    t8 = false;
                    break;
                case 2:
                    p2chuquan = "石头";
                    t8 = false;
                    break;
                case 3:
                    p2chuquan = "布";
                    t8 = false;
                    break;
                default:
                    System.out.print("输入错误！请重新选择！\n");
                    continue;
                }
            }
            break;
        }
        System.out.println(p1name + "出拳：" + p1chuquan);
        System.out.println(p2name + "出拳：" + p2chuquan);
    }

    public void shengfu() {
        if (p1chuquan.equals(p2chuquan)) {
            he++;
            winx = 1;
            System.out.println("平局！");
        } else if (p1chuquan == "剪刀" && p2chuquan == "布") {
            win++;
            winx = 2;
            System.out.println(p1name + "获胜！");
        } else if (p1chuquan == "布" && p2chuquan == "石头") {
            win++;
            winx = 2;
            System.out.println(p1name + "获胜！");
        } else if (p1chuquan == "石头" && p2chuquan == "剪刀") {
            win++;
            winx = 2;
            System.out.println(p1name + "获胜！");
        } else {
            lose++;
            winx = 3;
            System.out.println(p2name + "获胜！");
        }
        yuyan();
    }

    public void yuyan() {
        if (p1name.equals("刘备") && p2name.equals("孙权")) {
            if (winx == 1) {
                System.out.println(p1name + "：蜀将何在？");
                System.out.println(p2name + "：吴将何在？");
            } else if (winx == 2) {
                System.out.println(p1name + "：惟贤惟德，能服以人!");
                System.out.println(p2name + "：容我三思...");
            } else if (winx == 3) {
                System.out.println(p1name + "：这就是桃园吗？");
                System.out.println(p2name + "：智者千虑必有一失，愚者千虑必有一得！");
            }
        } else if (p1name.equals("刘备") && p2name.equals("曹操")) {
            if (winx == 1) {
                System.out.println(p1name + "：蜀将何在？");
                System.out.println(p2name + "：魏将何在？");
            } else if (winx == 2) {
                System.out.println(p1name + "：惟贤惟德，能服以人!");
                System.out.println(p2name + "：来人，护驾!");
            } else if (winx == 3) {
                System.out.println(p1name + "：这就是桃园吗？");
                System.out.println(p2name + "：宁教我负天下人，休叫天下人负我！");
            }
        } else if (p1name.equals("孙权") && p2name.equals("刘备")) {
            if (winx == 1) {
                System.out.println(p1name + "：吴将何在？");
                System.out.println(p2name + "：蜀将何在？");
            } else if (winx == 2) {
                System.out.println(p1name + "：智者千虑必有一失，愚者千虑必有一得！");
                System.out.println(p2name + "：这就是桃园吗？");
            } else if (winx == 3) {
                System.out.println(p1name + "：容我三思...");
                System.out.println(p2name + "：惟贤惟德，能服以人!");
            }
        } else if (p1name.equals("孙权") && p2name.equals("曹操")) {
            if (winx == 1) {
                System.out.println(p1name + "：吴将何在？");
                System.out.println(p2name + "：魏将何在？");
            } else if (winx == 2) {
                System.out.println(p1name + "：智者千虑必有一失，愚者千虑必有一得！");
                System.out.println(p2name + "：来人，护驾!");
            } else if (winx == 3) {
                System.out.println(p1name + "：容我三思...");
                System.out.println(p2name + "：宁教我负天下人，休叫天下人负我！");
            }
        } else if (p1name.equals("曹操") && p2name.equals("刘备")) {
            if (winx == 1) {
                System.out.println(p1name + "：魏将何在？");
                System.out.println(p2name + "：蜀将何在？");
            } else if (winx == 2) {
                System.out.println(p1name + "：宁教我负天下人，休叫天下人负我！");
                System.out.println(p2name + "：这就是桃园吗？");
            } else if (winx == 3) {
                System.out.println(p1name + "：来人，护驾!");
                System.out.println(p2name + "：惟贤惟德，能服以人!");
            }
        } else if (p1name.equals("曹操") && p2name.equals("孙权")) {
            if (winx == 1) {
                System.out.println(p1name + "：魏将何在？");
                System.out.println(p2name + "：吴将何在？");
            } else if (winx == 2) {
                System.out.println(p1name + "：宁教我负天下人，休叫天下人负我！");
                System.out.println(p2name + "：容我三思...");
            } else if (winx == 3) {
                System.out.println(p1name + "：来人，护驾!");
                System.out.println(p2name + "：智者千虑必有一失，愚者千虑必有一得！");
            }
        } else {
            if (winx == 1) {
                System.out.println(p1name + "：难兄难弟？");
                System.out.println(p2name + "：以和为贵！");
            } else if (winx == 2) {
                System.out.println(p1name + "：你是假的，我是真的！");
                System.out.println(p2name + "：被看穿了吗？");
            } else if (winx == 3) {
                System.out.println(p1name + "：苍天！~~~");
                System.out.println(p2name + "：以假乱真!");
            }
        }
    }

    public void stop() {
        score = win - lose;
        System.out.print("-----------------------------------------------------------------------------\n");
        System.out.print(p1name + " vs " + p2name + "\n");
        System.out.print("对战次数：" + (win + lose + he) + "\n");
        System.out.print("结果：" + score + "分\n");
        System.out.print("赢：" + win + "\n");
        System.out.print("输：" + lose + "\n");
        System.out.print("和：" + he + "\n");
        if (score > 0) {
            System.out.println("天下三分，终归于晋，望汝得之于民，惠之于民！");
        } else if (score == 0) {
            System.out.println("今日天色已晚，且自收兵，来日再战！");
        } else {
            System.out.println("我军行仁义，得天道，汝安得不败？");
        }
        System.out.print("-----------------------------------------------------------------------------\n");
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        CaiQuan a = new CaiQuan();
        a.top();
        boolean t3 = true;
        while (t3) {
            a.chuquan();
            a.shengfu();
            boolean t4 = true;
            while (t4) {
                System.out.print("\n是否开始下一轮(y/n)：");
                String panduan1 = input.next();
                if (panduan1.equals("y")) {
                    break;
                } else if (panduan1.equals("n")) {
                    t3 = false;
                    break;
                } else {
                    System.out.print("输入错误！请重新选择！\n");
                }
            }
        }
        a.stop();
        input.close();
    }
}
