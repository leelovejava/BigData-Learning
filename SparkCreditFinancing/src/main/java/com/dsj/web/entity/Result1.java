package com.dsj.web.entity;

public class Result1 {
    public String period;
    public double percent;

    public Result1(String period, double percent) {
        super();
        this.period = period;
        this.percent = percent;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

}
