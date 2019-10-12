package caipiao;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CaiPiao {

    private static final String MSG_SNO = "-第{0}期- >>\t{1}\t  ";
    // private static final String MSG_SNO = "-SNO:{0}- >>\t{1}\t ";
    private static final String MSG_BINGO = MSG_SNO + "{2}";
    private static final String MSG_SORRY = MSG_SNO + " ----------------------------";
    private static final String MSG_DT = "[\"{0}\", {1}]\t";
    private static final String MSG_RESULT = "\nTotal bonus: {0}, cost : {1}, surplus : {2}";
    private static final String SYMBOL_BINGO = "♥";
    private static final String SYMBOL_SORRY = "♡";
    // ♚ ♛ ♝ ♞ ♜ ♟ ♔ ♕ ♗ ♘ ♖ ♟
    // ✐✎✏✑✒✍✉✁✂✃✄✆✉☎☏☑✓✔√☐☒✗✘ㄨ✕✖✖☢☠☣✈★☆✡囍㍿☯☰☲☱☴☵☶☳☷☜☞☝✍☚☛☟✌♤♧♡♢♠♣♥♦☀☁☂❄☃♨웃유❖☽☾☪✿♂♀✪✯☭➳卍卐√×■◆●○◐◑✙☺☻❀⚘♔♕♖♗♘♙♚♛♜♝♞♟♧♡♂♀♠♣♥❤☜☞☎☏⊙◎☺☻☼▧▨♨◐◑↔↕▪▒◊◦▣▤▥▦▩◘◈◇♬♪♩♭♪の★☆→あぃ￡Ю〓§♤♥▶¤✲❈✿✲❈➹☀☂☁【】┱┲❣✚✪✣✤✥✦❉❥❦❧❃❂❁❀✄☪☣☢☠☭ღ▶▷◀◁☀☁☂☃☄★☆☇☈⊙☊☋☌☍ⓛⓞⓥⓔ╬『』∴☀♫♬♩♭♪☆∷﹌の★◎▶☺☻►◄▧▨♨◐◑↔↕↘▀▄█▌◦☼♪の☆→♧ぃ￡❤▒▬♦◊◦♠♣▣۰•❤•۰►◄▧▨♨◐◑↔↕▪▫☼♦⊙●○①⊕◎Θ⊙¤㊣★☆♀◆◇◣◢◥▲▼△▽⊿◤◥✐✌✍✡✓✔✕✖♂♀♥♡☜☞☎☏⊙◎☺☻►◄▧▨♨◐◑↔↕♥♡▪▫☼♦▀▄█▌▐░▒▬♦◊◘◙◦☼♠♣▣▤▥▦▩◘◙◈♫♬♪♩♭♪✄☪☣☢☠♯♩♪♫♬♭♮☎☏☪♈ºº₪¤큐«»™♂✿♥
    // ◕‿-｡ ｡◕‿◕｡
    private static final String B_1 = "10000000";
    private static final String B_2 = "1000000";
    private static final String B_3 = "10000";
    private static final String B_4 = "3000";
    private static final String B_5 = "300";
    private static final String B_6 = "200";
    private static final String B_7 = "100";
    private static final String B_8 = "15";
    private static final String B_9 = "5";

    private static final int INT0 = 0;
    private static final int INT5 = 5;
    private static final int INT6 = 6;

    private static final int SINGLE_COST = 2;

    private static int bonus = 0;
    private static int cost = 0;
    private static Map<String, String> GoalMap = new HashMap<>();
    private static Map<String, String[]> SoldMap = new LinkedHashMap<>();
    private static List<String[]> BoughtList = new LinkedList<>();

    static {
        GoalMap.put("5+2", B_1);
        GoalMap.put("5+1", B_2);
        GoalMap.put("5+0", B_3);
        GoalMap.put("4+2", B_4);
        GoalMap.put("4+1", B_5);
        GoalMap.put("3+2", B_6);
        GoalMap.put("4+0", B_7);
        GoalMap.put("3+1", B_8);
        GoalMap.put("2+2", B_8);
        GoalMap.put("3+0", B_9);
        GoalMap.put("1+2", B_9);
        GoalMap.put("2+1", B_9);
        GoalMap.put("0+2", B_9);

        BoughtList.add(genGoal("03,09,17,21,25,01,12"));
        BoughtList.add(genGoal("03,12,22,27,28,04,06"));
        BoughtList.add(genGoal("03,13,22,27,28,03,09"));
        BoughtList.add(genGoal("03,17,21,23,30,02,08"));
        BoughtList.add(genGoal("03,17,21,25,30,11,12"));

        addSold("19100,05,08,12,19,21,06,11");
        addSold("19099,04,06,18,27,33,07,09");

        addSold("19098,13,15,17,19,22,03,04");
        addSold("19097,10,17,20,30,35,10,12");
        addSold("19096,02,12,22,23,27,02,06");
        addSold("19095,03,12,17,19,31,02,06");
        addSold("19094,01,12,14,26,27,07,09");

        addSold("19093,16,17,26,29,35,01,07");
        addSold("19092,02,11,15,27,30,02,05");
        addSold("19091,04,13,20,26,28,03,12");
        addSold("19090,13,19,28,30,33,02,12");
        addSold("19089,09,14,18,33,34,03,12");

        addSold("19088,01,05,18,27,33,06,08");
        addSold("19087,05,06,18,19,27,07,12");
        addSold("19086,05,15,16,28,31,02,07");
        addSold("19085,04,05,06,15,26,08,11");
        addSold("19084,12,23,25,34,35,01,07");
        //
        // addSold("19083,17,24,26,28,32,07,09");
        //
        // addSold("19082,06,18,20,21,31,03,04");
        // addSold("19081,06,24,26,30,33,02,07");
        // addSold("19080,05,13,18,19,24,02,07");
        // addSold("19079,10,13,16,28,35,04,05");
        // addSold("19078,05,10,21,26,30,01,07");
        //
        // addSold("19077,01,15,17,21,33,07,10");
        // addSold("19076,01,06,11,23,29,01,11");
        // addSold("19075,05,06,07,11,16,02,11");
        // addSold("19074,08,09,13,14,34,05,07");
        // addSold("19073,04,11,19,20,24,08,11");
        //
        // addSold("19072,02,04,06,09,18,07,10");
        // addSold("19071,07,13,14,15,22,07,11");
        // addSold("19070,02,13,15,22,34,02,12");
        // addSold("19069,08,22,23,33,34,04,06");
        // addSold("19068,04,05,08,17,27,06,11");
        //
        // addSold("19067,03,08,11,15,16,02,10");
        // addSold("19066,04,10,11,18,31,05,08");
        //
        // addSold("19065,03,08,10,23,27,02,12");
        // addSold("19064,07,08,13,19,29,11,12");
        // addSold("19063,05,08,15,18,21,04,08");
        // addSold("19062,05,16,23,25,28,01,12");
        // addSold("19061,01,02,13,14,26,05,11");
        //
        // addSold("19060,01,11,12,18,21,08,10");
        // addSold("19059,07,08,15,27,32,08,12");
        // addSold("19058,03,10,13,14,32,07,11");
        // addSold("19057,08,14,16,34,35,06,12");
        // addSold("19056,01,07,10,12,23,06,08");
        //
        // addSold("19055,02,04,06,19,22,02,06");
        // addSold("19054,02,18,23,27,30,05,10");
        // addSold("19053,01,11,19,26,35,11,12");
        // addSold("19052,04,10,15,20,24,07,11");
        // addSold("19051,11,15,16,20,29,04,08");
        //
        // addSold("19050,04,15,16,20,35,03,12");
        // addSold("19049,10,16,22,26,27,07,11");
        // addSold("19048,06,10,16,29,33,05,11");
        // addSold("19047,03,04,10,16,32,04,09");
        // addSold("19046,02,08,14,15,35,10,12");
        //
        // addSold("19045,01,04,16,19,33,03,12");
        // addSold("19044,03,06,10,12,31,01,02");
        // addSold("19043,02,06,07,12,15,06,12");
        // addSold("19042,04,10,13,28,33,11,12");
        // addSold("19041,12,19,20,22,28,02,06");
        //
        // addSold("19040,01,03,31,32,34,01,03");
        // addSold("19039,01,12,18,26,35,06,12");
        // addSold("19038,05,11,16,18,27,07,12");
        // addSold("19037,06,16,26,33,35,04,08");
        // addSold("19036,03,09,21,28,30,01,12");
        //
        // addSold("19035,01,03,05,07,18,08,09");
        // addSold("19034,08,12,15,27,30,01,02");
        // addSold("19033,01,02,04,16,24,07,09");
        // addSold("19032,01,04,07,11,30,07,08");
        // addSold("19031,03,08,21,26,33,04,05");
        // addSold("19030,07,09,13,14,33,02,04");
        // addSold("19029,01,04,18,24,28,02,03");
        // addSold("19028,06,22,28,29,33,02,07");
        // addSold("19027,16,18,24,25,27,02,07");
        // addSold("19026,10,12,15,17,19,02,03");
        // addSold("19025,06,13,16,19,29,03,07");
        // addSold("19024,11,17,19,29,33,08,09");
        // addSold("19023,01,03,14,26,30,06,11");
        // addSold("19022,01,04,14,15,21,01,05");
        // addSold("19021,07,13,17,24,33,01,10");
        // addSold("19020,01,08,16,27,34,03,12");
    }

    public static void check() {
        for (Entry<String, String[]> entry : SoldMap.entrySet()) {
            StringBuffer details = new StringBuffer();
            StringBuffer msg = new StringBuffer();
            boolean bingo = false;
            for (String[] strArr : BoughtList) {
                cost += SINGLE_COST;
                String rest = check(entry.getValue(), strArr);
                if (GoalMap.containsKey(rest)) {
                    bingo = true;
                    details.append(SYMBOL_BINGO);

                    String bonusString = GoalMap.get(rest);
                    msg.append(MessageFormat.format(MSG_DT, rest, bonusString));
                    try {
                        bonus += Integer.parseInt(bonusString);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    details.append(SYMBOL_SORRY);
                }
            }
            String resultMsg = MessageFormat.format(MSG_SORRY, entry.getKey(), details);
            if (bingo) {
                resultMsg = MessageFormat.format(MSG_BINGO, entry.getKey(), details, msg);
            }
            System.out.println(resultMsg);
        }

        System.out.println(MessageFormat.format(MSG_RESULT, bonus, cost, bonus - cost));
    }

    private static String check(String[] sold, String[] bought) {
        if (null == sold || null == bought) {
            System.err.println("存在NULL数据");
            return null;
        }
        if (sold.length != 7) {
            System.err.println("开奖号码异常");
            return null;
        }
        if (bought.length != 7) {
            System.err.println("购买号码异常");
            return null;
        }
        String[] sold5 = new String[] { sold[0], sold[1], sold[2], sold[3], sold[4] };
        String[] sold67 = new String[] { sold[5], sold[6] };
        int pre = 0;
        int suf = 0;
        for (int i = 0; i < 5; i++) {
            if (isInSold(bought[i], sold5)) {
                pre++;
            }
        }
        for (int i = 0; i < 2; i++) {
            if (isInSold(bought[5 + i], sold67)) {
                suf++;
            }
        }
        return MessageFormat.format("{0}+{1}", pre, suf);
    }

    private static boolean isInSold(String buyOne, String[] sold) {
        for (int i = 0; i < sold.length; i++) {
            if (equals(sold[i], buyOne)) {
                return true;
            }
        }
        return false;
    }

    private static boolean equals(String sold, String bought) {
        return sold.equals(bought);
    }

    private static void addSold(String strings) {
        strings = strings.replaceAll(" ", "");
        String date = strings.substring(INT0, INT5);
        String goalStr = strings.substring(INT6);
        SoldMap.put(date, genGoal(goalStr));
    }

    private static String[] genGoal(String strings) {
        return strings.replaceAll(" ", "").split(",");
    }

    public static void main(String[] args) {
        check();
    }
}
