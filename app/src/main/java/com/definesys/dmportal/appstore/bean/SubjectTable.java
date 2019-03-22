package com.definesys.dmportal.appstore.bean;
import java.util.Date;
import java.util.List;

/*
 *
 * Created by 羽翎 on 2019/1/4.
 */

/**
 * 课程安排返回的实体
 */
public class SubjectTable {
    private Date startDate;//学期开始时间

    private Date endDate;//学期结束时间

    private int sumWeek;//当前学期总周数

    private List<CursorArg> cursorArgList;//课程安排

    public SubjectTable(Date startDate, Date endDate, int sumWeek, List<CursorArg> cursorArgList) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.sumWeek = sumWeek;
        this.cursorArgList = cursorArgList;
    }


    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getSumWeek() {
        return sumWeek;
    }

    public void setSumWeek(int sumWeek) {
        this.sumWeek = sumWeek;
    }

    public List<CursorArg> getCursorArgList() {
        return cursorArgList;
    }

    public void setCursorArgList(List<CursorArg> cursorArgList) {
        this.cursorArgList = cursorArgList;
    }
}

