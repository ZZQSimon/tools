package cn.com.easyerp.module.salaryCal;

public class UserHoliday {
    private String type;
    private String begin_date;
    private String end_date;

    public String getType() {
        return this.type;
    }

    private String begin_time;
    private String end_time;
    private double how_long;
    private double percent;

    public void setType(String type) {
        this.type = type;
    }

    public String getBegin_date() {
        return this.begin_date;
    }

    public void setBegin_date(String begin_date) {
        this.begin_date = begin_date;
    }

    public String getEnd_date() {
        return this.end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getBegin_time() {
        return this.begin_time;
    }

    public void setBegin_time(String begin_time) {
        this.begin_time = begin_time;
    }

    public String getEnd_time() {
        return this.end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public double getHow_long() {
        return this.how_long;
    }

    public void setHow_long(double how_long) {
        this.how_long = how_long;
    }

    public double getPercent() {
        return this.percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }
}
