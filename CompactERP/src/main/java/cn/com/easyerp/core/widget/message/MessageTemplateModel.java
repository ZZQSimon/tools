package cn.com.easyerp.core.widget.message;

public class MessageTemplateModel {
    private String id;
    private String template_id;

    public String getId() {
        return this.id;
    }

    private String title;
    private String template;
    private String btn_text;

    public void setId(String id) {
        this.id = id;
    }

    public String getTemplate_id() {
        return this.template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTemplate() {
        return this.template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getBtn_text() {
        return this.btn_text;
    }

    public void setBtn_text(String btn_text) {
        this.btn_text = btn_text;
    }
}
