package cn.com.easyerp.report;

public class ReportPageConfigModel {
    public class DetailConfig {
        private int lines1;
        private int lines2;

        public int getLines1() {
            return this.lines1;
        }

        public void setLines1(int lines1) {
            this.lines1 = lines1;
        }

        public int getLines2() {
            return this.lines2;
        }

        public void setLines2(int lines2) {
            this.lines2 = lines2;
        }
    }

    private DetailConfig detail;

    public DetailConfig getDetail() {
        return this.detail;
    }

    public void setDetail(DetailConfig detail) {
        this.detail = detail;
    }
}