package cn.com.easyerp.core.companyCalendar;

public class PagingDesctibe {
    private int start;

    public int getStart() {
        return this.start;
    }

    private static final int PAGECOUNT = 5;
    private int pageStartCount;

    public void setStart(int start) {
        this.start = start;
        this.pageStartCount = start * 5;
    }

    public static int getPagecount() {
        return 5;
    }

    public int getPageStartCount() {
        return this.pageStartCount;
    }
}
