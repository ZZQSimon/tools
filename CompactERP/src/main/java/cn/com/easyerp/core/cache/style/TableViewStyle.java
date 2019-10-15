package cn.com.easyerp.core.cache.style;

import java.util.Set;

public class TableViewStyle {
    public static enum Button {
        all, add, edit, delete, view, importing, export, operation, save, next, exportAll;
    }

    private DetailTabViewStyle tab;
    private Set<Button> hideButtons;
    private String seq;
    private FilterViewStyle filter;

    public DetailTabViewStyle getTab() {
        return this.tab;
    }

    public void setTab(DetailTabViewStyle tab) {
        this.tab = tab;
    }

    public Set<Button> getHideButtons() {
        return this.hideButtons;
    }

    public void setHideButtons(Set<Button> hideButtons) {
        this.hideButtons = hideButtons;
    }

    public String getSeq() {
        return this.seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public FilterViewStyle getFilter() {
        return this.filter;
    }

    public void setFilter(FilterViewStyle filter) {
        this.filter = filter;
    }
}