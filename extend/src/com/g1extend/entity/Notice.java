package com.g1extend.entity;

/**
 * @ClassName: Notice <br>
 * @Description: [应用于短信邮件通知的数据库映射类] <br>
 * @date 2019.09.26 <br>
 * 
 * @author Simon Zhang
 */
public class Notice {

    /** 主键 32位UUID */
    private String id;
    /** 接口传递的参数 */
    private String link;
    /** 接口传递的参数 */
    private String param;
    /** 接口传递的参数 */
    private String name;
    /** 接口传递的参数 邮件地址或者电话号码 */
    private String notice_to;
    /**
     * 通知类型 <br>
     * 邮件: 01 <br>
     * 短信: 02 <br>
     * 其他: 00 <br>
     */
    private String notice_type;
    /** 接口传递的参数 */
    private String base_url;
    /** 1：已发送 0：未发送 */
    private int is_sent;
    /** 发送成功时间 */
    private String sent_time;
    /** 尝试发送次数 */
    private int sent_counts;
    /** 数据创建时间 */
    private String create_time;
    /** 发送失败原因 */
    private String failed_reason;

    public Notice() {
    }

    public Notice(String link, String param, String name, String notice_to, String base_url, String failed_reason) {
        super();
        this.link = link;
        this.param = param;
        this.name = name;
        this.notice_to = notice_to;
        this.base_url = base_url;
        this.failed_reason = failed_reason;
    }

    public Notice(String id, String link, String param, String name, String noticeTo, String notice_type,
            String base_url, int is_sent, String sent_time, int sent_counts, String create_time, String failed_reason) {
        super();
        this.id = id;
        this.link = link;
        this.param = param;
        this.name = name;
        this.notice_to = noticeTo;
        this.notice_type = notice_type;
        this.base_url = base_url;
        this.is_sent = is_sent;
        this.sent_time = null == sent_time ? "" : sent_time;
        this.sent_counts = sent_counts;
        this.create_time = null == create_time ? "" : create_time;
        this.failed_reason = failed_reason;
    }

    /**
     * id:[]的取得
     * 
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * link:[]的取得
     * 
     * @return link
     */
    public String getLink() {
        return link;
    }

    /**
     * param:[]的取得
     * 
     * @return param
     */
    public String getParam() {
        return param;
    }

    /**
     * name:[]的取得
     * 
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * notice_to:[]的取得
     * 
     * @return notice_to
     */
    public String getNotice_to() {
        return notice_to;
    }

    /**
     * notice_type:[]的取得
     * 
     * @return notice_type
     */
    public String getNotice_type() {
        return notice_type;
    }

    /**
     * base_url:[]的取得
     * 
     * @return base_url
     */
    public String getBase_url() {
        return base_url;
    }

    /**
     * is_sent:[]的取得
     * 
     * @return is_sent
     */
    public int getIs_sent() {
        return is_sent;
    }

    /**
     * sent_time:[]的取得
     * 
     * @return sent_time
     */
    public String getSent_time() {
        return sent_time;
    }

    /**
     * sent_counts:[]的取得
     * 
     * @return sent_counts
     */
    public int getSent_counts() {
        return sent_counts;
    }

    /**
     * create_time:[]的取得
     * 
     * @return create_time
     */
    public String getCreate_time() {
        return create_time;
    }

    /**
     * failed_reason:[]的取得
     * 
     * @return failed_reason
     */
    public String getFailed_reason() {
        return failed_reason;
    }

    /**
     * id:[]的设定
     * 
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * link:[]的设定
     * 
     * @param link
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * param:[]的设定
     * 
     * @param param
     */
    public void setParam(String param) {
        this.param = param;
    }

    /**
     * name:[]的设定
     * 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * notice_to:[]的设定
     * 
     * @param notice_to
     */
    public void setNotice_to(String notice_to) {
        this.notice_to = notice_to;
    }

    /**
     * notice_type:[]的设定
     * 
     * @param notice_type
     */
    public void setNotice_type(String notice_type) {
        this.notice_type = notice_type;
    }

    /**
     * base_url:[]的设定
     * 
     * @param base_url
     */
    public void setBase_url(String base_url) {
        this.base_url = base_url;
    }

    /**
     * is_sent:[]的设定
     * 
     * @param is_sent
     */
    public void setIs_sent(int is_sent) {
        this.is_sent = is_sent;
    }

    /**
     * sent_time:[]的设定
     * 
     * @param sent_time
     */
    public void setSent_time(String sent_time) {
        this.sent_time = sent_time;
    }

    /**
     * sent_counts:[]的设定
     * 
     * @param sent_counts
     */
    public void setSent_counts(int sent_counts) {
        this.sent_counts = sent_counts;
    }

    /**
     * create_time:[]的设定
     * 
     * @param create_time
     */
    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    /**
     * failed_reason:[]的设定
     * 
     * @param failed_reason
     */
    public void setFailed_reason(String failed_reason) {
        this.failed_reason = failed_reason;
    }

    /**
     * @see java.lang.Object#toString()
     * 
     * 
     * @return
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Notice [id=");
        builder.append(id);
        builder.append(", link=");
        builder.append(link);
        builder.append(", param=");
        builder.append(param);
        builder.append(", name=");
        builder.append(name);
        builder.append(", noticeTo=");
        builder.append(notice_to);
        builder.append(", notice_type=");
        builder.append(notice_type);
        builder.append(", base_url=");
        builder.append(base_url);
        builder.append(", is_sent=");
        builder.append(is_sent);
        builder.append(", sent_time=");
        builder.append(sent_time);
        builder.append(", sent_counts=");
        builder.append(sent_counts);
        builder.append(", create_time=");
        builder.append(create_time);
        builder.append(", failed_reason=");
        builder.append(failed_reason);
        builder.append("]");
        return builder.toString();
    }

}
