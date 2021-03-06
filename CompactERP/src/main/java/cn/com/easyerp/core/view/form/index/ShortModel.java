package cn.com.easyerp.core.view.form.index;

import cn.com.easyerp.core.widget.WidgetModelBase;

public class ShortModel extends WidgetModelBase {
    private String id;
    private String name;
    private String page_id;
    private int end_col;
    private int start_col;
    private int end_row;
    private int start_row;
    private String itemType;
    private String background_color;
    private int height;
    private int width;

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public ShortModel() {
        super("");
    }

    public String idPrefix() {
        return "m";
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPage_id() {
        return this.page_id;
    }

    public void setPage_id(String page_id) {
        this.page_id = page_id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getEnd_col() {
        return this.end_col;
    }

    public void setEnd_col(int end_col) {
        this.end_col = end_col;
    }

    public int getStart_col() {
        return this.start_col;
    }

    public void setStart_col(int start_col) {
        this.start_col = start_col;
    }

    public int getEnd_row() {
        return this.end_row;
    }

    public void setEnd_row(int end_row) {
        this.end_row = end_row;
    }

    public int getStart_row() {
        return this.start_row;
    }

    public void setStart_row(int start_row) {
        this.start_row = start_row;
    }

    public String getItemType() {
        return this.itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public String getBackground_color() {
        return this.background_color;
    }

    public void setBackground_color(String background_color) {
        this.background_color = background_color;
    }
}
