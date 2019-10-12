package cn.com.easyerp.framework.util;

import java.util.Date;

/**
 * Bean 代码生成器 <br>
 * 
 * @date 2018.08.29 <br>
 * 
 * @author Simon Zhang
 */

public class BeanGenerator {
    private static final String newLine = "\n";
    private static final String newContentLine = "<br>";

    public static void genDB(String packageName, String calssEnName, String calssCnName, String tableName,
            String[] commentArray, String[] annTypeArray, String[] annCountArray, String[] fieldArray) {
        StringBuffer sb = new StringBuffer();

        sb.append("package com.hisense.edi.").append(packageName).append(";").append(newLine).append(newLine);
        sb.append("import javax.persistence.Column;").append(newLine);
        sb.append("import javax.persistence.Entity;").append(newLine);
        sb.append("import javax.persistence.GeneratedValue;").append(newLine);
        sb.append("import javax.persistence.Id;").append(newLine);
        sb.append("import javax.persistence.Table;").append(newLine).append(newLine);
        sb.append("import org.hibernate.annotations.GenericGenerator;").append(newLine);
        sb.append("import com.hisense.edi.bic.db.AbstractPo;").append(newLine);
        sb.append("import lombok.Data;").append(newLine);
        sb.append("import lombok.EqualsAndHashCode;").append(newLine);
        sb.append("import lombok.ToString;").append(newLine).append(newLine);

        sb.append("/**").append(newLine);
        sb.append("*").append(calssCnName).append(newContentLine).append(newLine);
        sb.append("* @date ").append(DateUtil.date2String(new Date(), "yyyy.MM.dd"));
        sb.append(newContentLine).append(newLine);
        sb.append("* @author Simon Zhang */").append(newLine);

        sb.append("@Data").append(newLine);
        sb.append("@EqualsAndHashCode(callSuper = false)").append(newLine);
        sb.append("@ToString(callSuper = true)").append(newLine);
        sb.append("@Entity").append(newLine);
        sb.append("@Table(name = \"").append(tableName).append("\")").append(newLine);
        sb.append("@GenericGenerator(name = \"idGenerator\", strategy = \"org.hibernate.id.UUIDGenerator\")");
        sb.append(newLine);

        sb.append("public class ").append(calssEnName).append(" extends AbstractPo {").append(newLine);
        sb.append("private static final long serialVersionUID = -1L;").append(newLine);
        sb.append("@Id").append(newLine);
        sb.append("@GeneratedValue(generator = \"idGenerator\")").append(newLine);
        sb.append("private String id;").append(newLine);
        sb.append(genDBFields(commentArray, annTypeArray, annCountArray, fieldArray)).append(newLine);
        sb.append("}").append(newLine);

        System.out.println(sb.toString());
    }

    private static String genDBFields(String[] commentArray, String[] annTypeArray, String[] annCountArray,
            String[] fieldArray) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < fieldArray.length; i++) {
            sb.append("/** COMMENT '").append(commentArray[i]).append("' */").append(newLine);

            sb.append("@Column(name = \"").append(fieldArray[i]);
            sb.append("\", columnDefinition = \"").append(annTypeArray[i]);
            sb.append("(").append(annCountArray[i]).append(")\")").append(newLine);

            sb.append("private String ");
            sb.append(Underline2Camel.underline2Camel(fieldArray[i], true)).append(";");
            sb.append(newLine).append(newLine);
        }
        return sb.toString();
    }

    public static void genDBSetter(String beanName, String[] commentArray, String[] fieldArray, String[] beanArray,
            String title) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < fieldArray.length; i++) {
            sb.append("// 设定 [数据库").append(beanName).append("]:[");
            sb.append(commentArray[i]).append("] ,使用[").append(beanArray[i]).append("]").append(newLine);
            sb.append("bean." + Underline2Camel.underline2Camel("set_" + fieldArray[i], true));
            sb.append("(slip.get");
            sb.append(title.toUpperCase());
            sb.append("().trimValue());").append(newLine);
        }
        System.out.println(sb.toString());
    }

    public static void gen(String packageName, String calssEnName, String calssCnName, String contentLegth,
            String[] comment, String[] feildDefaultValue, int[] bitss, String[] chaset, String title, int count) {
        StringBuffer sb = new StringBuffer();
        sb.append("package com.hisense.edi.").append(packageName).append(";").append(newLine).append(newLine);
        sb.append("import com.hisense.edi.framework.base.BaseBean;").append(newLine);
        sb.append("import com.hisense.edi.framework.base.SpecifiedLengthString;").append(newLine);
        sb.append("import com.hisense.edi.framework.exception.ConstructorFieldLengthException;").append(newLine);
        sb.append("import com.hisense.edi.framework.exception.ConstructorFieldNullException;").append(newLine);
        sb.append("import com.hisense.edi.framework.util.EmptyUtil;").append(newLine);

        sb.append("/**").append(newLine);
        sb.append("*").append(calssCnName).append(newContentLine).append(newLine);
        sb.append("* @date ").append(DateUtil.date2String(new Date(), "yyyy.MM.dd"));
        sb.append(newContentLine).append(newLine);
        sb.append("* @author Simon Zhang */").append(newLine);

        sb.append("public class ").append(calssEnName).append(" extends BaseBean {").append(newLine);
        sb.append(genFields(comment, feildDefaultValue, bitss, chaset, title, count)).append(newLine);

        sb.append("public ").append(calssEnName).append("() {}").append(newLine);

        sb.append("/**").append(newLine);
        sb.append("* 使用字符串反向创建对象").append(newLine);
        sb.append("* @param content").append(newLine);
        sb.append("* @throws ConstructorFieldNullException").append(newLine);
        sb.append("* @throws ConstructorFieldLengthException */").append(newLine);

        sb.append("public ").append(calssEnName);
        sb.append("(String content) throws ConstructorFieldNullException, ConstructorFieldLengthException {");
        sb.append(newLine);
        sb.append("if (EmptyUtil.isStrBlank(content)) {");
        sb.append("    throw new ConstructorFieldNullException(\"").append(calssEnName);
        sb.append(" content is blank\");}");
        sb.append(newLine);
        sb.append("if (content.length() != L").append(contentLegth).append(") {");
        sb.append("    throw new ConstructorFieldLengthException(\"").append(calssEnName);
        sb.append(" content length is not ");
        sb.append(contentLegth).append("\");}").append(newLine);
        sb.append("int temp = 0;").append(newLine);
        sb.append(genContrustorTemp(title, count));
        sb.append(" }").append(newLine);

        sb.append(genGetter(comment, title, count)).append(newLine);
        sb.append(genSetter(comment, title, count)).append(newLine);

        sb.append("}").append(newLine);

        System.out.println(sb.toString());
    }

    public static String genFields(String[] comment, String[] feildDefaultValue, int[] bitss, String[] chaset,
            String title, int count) {
        String pattarn = "private SpecifiedLengthString %s = new SpecifiedLengthString(%s, \"%s\", %s, %s);";

        if (EmptyUtil.isEmpty(comment) || EmptyUtil.isEmpty(bitss) || EmptyUtil.isEmpty(chaset)
                || EmptyUtil.isEmpty(title)) {
            throw new NullPointerException("参数有空");
        }

        if (comment.length != count) {
            throw new NullPointerException("注释个数不匹配");
        }
        if (bitss.length != count) {
            throw new NullPointerException("大小个数不匹配");
        }
        if (chaset.length != count) {
            throw new NullPointerException("数据类型个数不匹配");
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < count; i++) {
            sb.append("/** ");
            sb.append(comment[i]);
            sb.append(":");
            sb.append(feildDefaultValue[i]);
            sb.append("*/");
            sb.append(newLine);
            String cha = chaset[i];
            boolean flag = false;
            if ("9".equals(cha)) {
                flag = true;
                cha = "Zero";
            } else if ("数字".equals(cha)) {
                flag = true;
                cha = "Zero";
            } else {
                cha = "HalfSpace";
            }
            String key = "00" + (i + 1);
            key = title + key.substring(key.length() - 2);
            String printer = String.format(pattarn, key, bitss[i], feildDefaultValue[i], cha, (flag));
            sb.append(printer).append(newLine);
        }

        return sb.toString();
    }

    private static String genContrustorTemp(String title, int count) {
        String pattarn = "%s.setValue(content.substring(temp, temp += %s.getLength()));";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < count; i++) {
            String key = "00" + (i + 1);
            key = key.substring(key.length() - 2);
            key = title + key;
            sb.append(String.format(pattarn, key, key)).append(newLine);
        }
        return sb.toString();
    }

    private static String genGetter(String[] comment, String title, int count) {
        String contentPattern = " /** ";
        String contentPattern1 = " %s:[%s]的取得 ";
        String contentPattern2 = " @return %s */";
        String getterPattern = "public SpecifiedLengthString get%s() { return %s; }";

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < count; i++) {
            String keyLower = "00" + (i + 1);
            keyLower = title + keyLower.substring(keyLower.length() - 2);
            String keyUpper = "00" + (i + 1);
            keyUpper = title.toUpperCase() + keyUpper.substring(keyUpper.length() - 2);
            sb.append(String.format(contentPattern)).append(newLine);
            sb.append(String.format(contentPattern1, keyLower, comment[i])).append(newLine);
            sb.append(String.format(contentPattern2, keyLower)).append(newLine);
            sb.append(String.format(getterPattern, keyUpper, keyLower)).append(newLine);
        }
        return sb.toString();
    }

    private static String genSetter(String[] comment, String title, int count) {
        String contentPattern = " /** ";
        String contentPattern1 = " %s:[%s]的值设定 ";
        String contentPattern2 = " @param value 值 */";
        String getterPattern = "public void set%s(String value) {  this.%s.setValue(value); }";

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < count; i++) {
            String keyLower = "00" + (i + 1);
            keyLower = title + keyLower.substring(keyLower.length() - 2);
            String keyUpper = "00" + (i + 1);
            keyUpper = title.toUpperCase() + keyUpper.substring(keyUpper.length() - 2);
            sb.append(String.format(contentPattern)).append(newLine);
            sb.append(String.format(contentPattern1, keyLower, comment[i])).append(newLine);
            sb.append(String.format(contentPattern2, keyLower)).append(newLine);
            sb.append(String.format(getterPattern, keyUpper, keyLower)).append(newLine);
        }
        return sb.toString();
    }

    protected static void printGetter(String[] comment, String title, int count) {
        System.out.println(genGetter(comment, title, count));
    }

    protected static void printContrustorTemp(String title, int count) {
        System.out.println(genContrustorTemp(title, count));
    }

    protected static void printFields(String[] comment, String[] feildDefaultValue, int[] bitss, String[] chaset,
            String title, int count) {
        System.out.println(genFields(comment, feildDefaultValue, bitss, chaset, title, count));
    }
}
