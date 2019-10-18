package cn.com.easyerp.framework.enums;

import java.nio.charset.Charset;

/**
 * @ClassName: CS <br>
 * @Description: [字符集] <br>
 * @date 2019.10.11<br>
 * 
 * @author Simon Zhang
 */
public enum CS {

    Big5("Big5"),

    Big5HKSCS("Big5-HKSCS"),

    CESU8("CESU-8"),

    EUCJP("EUC-JP"),

    EUCKR("EUC-KR"),

    GB18030("GB18030"),

    GB2312("GB2312"),

    GBK("GBK"),

    IBMThai("IBM-Thai"),

    IBM00858("IBM00858"),

    IBM01140("IBM01140"),

    IBM01141("IBM01141"),

    IBM01142("IBM01142"),

    IBM01143("IBM01143"),

    IBM01144("IBM01144"),

    IBM01145("IBM01145"),

    IBM01146("IBM01146"),

    IBM01147("IBM01147"),

    IBM01148("IBM01148"),

    IBM01149("IBM01149"),

    IBM037("IBM037"),

    IBM1026("IBM1026"),

    IBM1047("IBM1047"),

    IBM273("IBM273"),

    IBM277("IBM277"),

    IBM278("IBM278"),

    IBM280("IBM280"),

    IBM284("IBM284"),

    IBM285("IBM285"),

    IBM290("IBM290"),

    IBM297("IBM297"),

    IBM420("IBM420"),

    IBM424("IBM424"),

    IBM437("IBM437"),

    IBM500("IBM500"),

    IBM775("IBM775"),

    IBM850("IBM850"),

    IBM852("IBM852"),

    IBM855("IBM855"),

    IBM857("IBM857"),

    IBM860("IBM860"),

    IBM861("IBM861"),

    IBM862("IBM862"),

    IBM863("IBM863"),

    IBM864("IBM864"),

    IBM865("IBM865"),

    IBM866("IBM866"),

    IBM868("IBM868"),

    IBM869("IBM869"),

    IBM870("IBM870"),

    IBM871("IBM871"),

    IBM918("IBM918"),

    ISO2022CN("ISO-2022-CN"),

    ISO2022JP("ISO-2022-JP"),

    ISO2022JP2("ISO-2022-JP-2"),

    ISO2022KR("ISO-2022-KR"),

    ISO88591("ISO-8859-1"),

    ISO885913("ISO-8859-13"),

    ISO885915("ISO-8859-15"),

    ISO88592("ISO-8859-2"),

    ISO88593("ISO-8859-3"),

    ISO88594("ISO-8859-4"),

    ISO88595("ISO-8859-5"),

    ISO88596("ISO-8859-6"),

    ISO88597("ISO-8859-7"),

    ISO88598("ISO-8859-8"),

    ISO88599("ISO-8859-9"),

    JIS_X0201("JIS_X0201"),

    JIS_X02121990("JIS_X0212-1990"),

    KOI8R("KOI8-R"),

    KOI8U("KOI8-U"),

    Shift_JIS("Shift_JIS"),

    TIS620("TIS-620"),

    USASCII("US-ASCII"),

    UTF16("UTF-16"),

    UTF16BE("UTF-16BE"),

    UTF16LE("UTF-16LE"),

    UTF32("UTF-32"),

    UTF32BE("UTF-32BE"),

    UTF32LE("UTF-32LE"),

    UTF8("UTF-8"),

    windows1250("windows-1250"),

    windows1251("windows-1251"),

    windows1252("windows-1252"),

    windows1253("windows-1253"),

    windows1254("windows-1254"),

    windows1255("windows-1255"),

    windows1256("windows-1256"),

    windows1257("windows-1257"),

    windows1258("windows-1258"),

    windows31j("windows-31j"),

    xBig5HKSCS2001("x-Big5-HKSCS-2001"),

    xBig5Solaris("x-Big5-Solaris"),

    xeucjplinux("x-euc-jp-linux"),

    xEUCTW("x-EUC-TW"),

    xeucJPOpen("x-eucJP-Open"),

    xIBM1006("x-IBM1006"),

    xIBM1025("x-IBM1025"),

    xIBM1046("x-IBM1046"),

    xIBM1097("x-IBM1097"),

    xIBM1098("x-IBM1098"),

    xIBM1112("x-IBM1112"),

    xIBM1122("x-IBM1122"),

    xIBM1123("x-IBM1123"),

    xIBM1124("x-IBM1124"),

    xIBM1166("x-IBM1166"),

    xIBM1364("x-IBM1364"),

    xIBM1381("x-IBM1381"),

    xIBM1383("x-IBM1383"),

    xIBM300("x-IBM300"),

    xIBM33722("x-IBM33722"),

    xIBM737("x-IBM737"),

    xIBM833("x-IBM833"),

    xIBM834("x-IBM834"),

    xIBM856("x-IBM856"),

    xIBM874("x-IBM874"),

    xIBM875("x-IBM875"),

    xIBM921("x-IBM921"),

    xIBM922("x-IBM922"),

    xIBM930("x-IBM930"),

    xIBM933("x-IBM933"),

    xIBM935("x-IBM935"),

    xIBM937("x-IBM937"),

    xIBM939("x-IBM939"),

    xIBM942("x-IBM942"),

    xIBM942C("x-IBM942C"),

    xIBM943("x-IBM943"),

    xIBM943C("x-IBM943C"),

    xIBM948("x-IBM948"),

    xIBM949("x-IBM949"),

    xIBM949C("x-IBM949C"),

    xIBM950("x-IBM950"),

    xIBM964("x-IBM964"),

    xIBM970("x-IBM970"),

    xISCII91("x-ISCII91"),

    xISO2022CNCNS("x-ISO-2022-CN-CNS"),

    xISO2022CNGB("x-ISO-2022-CN-GB"),

    xiso885911("x-iso-8859-11"),

    xJIS0208("x-JIS0208"),

    xJISAutoDetect("x-JISAutoDetect"),

    xJohab("x-Johab"),

    xMacArabic("x-MacArabic"),

    xMacCentralEurope("x-MacCentralEurope"),

    xMacCroatian("x-MacCroatian"),

    xMacCyrillic("x-MacCyrillic"),

    xMacDingbat("x-MacDingbat"),

    xMacGreek("x-MacGreek"),

    xMacHebrew("x-MacHebrew"),

    xMacIceland("x-MacIceland"),

    xMacRoman("x-MacRoman"),

    xMacRomania("x-MacRomania"),

    xMacSymbol("x-MacSymbol"),

    xMacThai("x-MacThai"),

    xMacTurkish("x-MacTurkish"),

    xMacUkraine("x-MacUkraine"),

    xMS932_0213("x-MS932_0213"),

    xMS950HKSCS("x-MS950-HKSCS"),

    xMS950HKSCSXP("x-MS950-HKSCS-XP"),

    xmswin936("x-mswin-936"),

    xPCK("x-PCK"),

    xSJIS_0213("x-SJIS_0213"),

    xUTF16LEBOM("x-UTF-16LE-BOM"),

    XUTF32BEBOM("X-UTF-32BE-BOM"),

    XUTF32LEBOM("X-UTF-32LE-BOM"),

    xwindows50220("x-windows-50220"),

    xwindows50221("x-windows-50221"),

    xwindows874("x-windows-874"),

    xwindows949("x-windows-949"),

    xwindows950("x-windows-950"),

    xwindowsiso2022jp("x-windows-iso2022jp"),

    ;

    private final String forName;

    CS(String forName) {
        this.forName = forName;
    }

    public String getName() {
        return forName;
    }

    public Charset cahrset() {
        return Charset.forName(forName);
    }
}
