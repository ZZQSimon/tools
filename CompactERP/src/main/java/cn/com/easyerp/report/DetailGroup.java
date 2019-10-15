package cn.com.easyerp.report;

class DetailGroup {
    Object val;
    int col;
    int start;
    int end;

    DetailGroup(Object val, int col, int start) {
        this.val = val;
        this.col = col;
        this.start = start;
    }
}
