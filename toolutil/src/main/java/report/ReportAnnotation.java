package report;
import java.lang.annotation.ElementType;   
import java.lang.annotation.Retention;   
import java.lang.annotation.RetentionPolicy;   
import java.lang.annotation.Target;   
  

/**
 * 报表注释类
 * @createDate 2011-8-22
 * @author Simon Zhang
 * @version 1.0.0
 * @modifyAuthor 
 * @modifyDate
 */
@Retention(RetentionPolicy.RUNTIME)   
@Target(ElementType.FIELD)   
public @interface ReportAnnotation {
	/**
	 * 列名称
	 */
    public String columnName();
    /**
     * 父列名称
     */
    public String columnParent() default "";
    /**
     * 列宽度   针对网页元素
     */
    public int width() default 100 ;
    /**
     * 时间format 当变量为Date类型时，才起作用
     */
    public String dateFormat() default "yyyy-MM-dd";
    
    /**
     * 默认解析时间格式  当变量为Date类型时，才起作用
     */
    public String dateParserFormat() default "null";
    /**
     * 是否排序
     * @return
     */
    public String sortable() default "false";
	/**
	 * 默认居中
	 * @return
	 */
    public String align() default "center";
	/**
	 * 是否隐藏
	 * @return
	 */
    public String hidden() default "false";
	/**
	 * 计算公式目前仅支持加减
	 * @return
	 */
    public String calculation() default "null";
	/**
	 * decode(1,男,2,女,不男不女)
	 * decode语句
	 */
    public String decode() default "null";
	
}  
