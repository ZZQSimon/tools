package cn.com.easyerp.core.widget.message;

import java.util.Date;

public class MessageDescribe {
    private String receive_id;
    private String send_id;
    private String title;
    private String content;
    private Date send_date;
    private String receiver;
    private String sender;
    private String status;
    private String type;

    public MessageDescribe(String title, String content, String receiver, String type) {
        this.receive_id = getUUID();
        this.send_id = getUUID();
        this.title = title;
        this.content = content;
        this.receiver = receiver;
        this.type = type;
    }

    public MessageDescribe() {
        this.send_id = getUUID();
    }

    public String getReceive_id() {
        return this.receive_id;
    }

    public void setReceive_id(String receive_id) {
        this.receive_id = receive_id;
    }

    public String getSend_id() {
        return this.send_id;
    }

    public void setSend_id(String send_id) {
        this.send_id = send_id;
    }

    public Date getSend_date() {
        return this.send_date;
    }

    public void setSend_date(Date send_date) {
        this.send_date = send_date;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReceiver() {
        return this.receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSender() {
        return this.sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUUID() {
        return Long.toHexString(System.currentTimeMillis());
    }
}
