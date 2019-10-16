package cn.com.easyerp.core.widget;

public class InputEntry {
    private String label;
    private String value;

    public InputEntry() {
    }

    public InputEntry(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
