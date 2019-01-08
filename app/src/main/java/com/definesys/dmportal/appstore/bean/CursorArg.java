package com.definesys.dmportal.appstore.bean;

/**
 * Created by 羽翎 on 2019/1/4.
 */

import java.io.Serializable;

/**
 * 授课列表
 */
public class CursorArg  {

    private String id;//授课id

    private String classroom;//上课教室

    private String weekDay;//一周中的周几上课 0.无课 1.有课

    private String pitch;//16进制 这天第几节上课 转化为2进制后0.无课 1.有课

    private int startWeek;//开始周

    private int endWeek;//结束周

    private String cursorName;//课程名称

    private String teacherName; //授课老师姓名

    private Long teacherId; //授课老师id

    private short credit;//学分

    private short cursorHour;//学时

    private String cursorType;//课程类型

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getWeekDay() {
        return weekDay;
    }

    public void setWeekDay(String weekDay) {
        this.weekDay = weekDay;
    }

    public String getPitch() {
        return pitch;
    }

    public void setPitch(String pitch) {
        this.pitch = pitch;
    }

    public int getStartWeek() {
        return startWeek;
    }

    public void setStartWeek(int startWeek) {
        this.startWeek = startWeek;
    }

    public int getEndWeek() {
        return endWeek;
    }

    public void setEndWeek(int endWeek) {
        this.endWeek = endWeek;
    }

    public String getCursorName() {
        return cursorName;
    }

    public void setCursorName(String cursorName) {
        this.cursorName = cursorName;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public short getCredit() {
        return credit;
    }

    public void setCredit(short credit) {
        this.credit = credit;
    }

    public short getCursorHour() {
        return cursorHour;
    }

    public void setCursorHour(short cursorHour) {
        this.cursorHour = cursorHour;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public String getCursorType() {
        return cursorType;
    }

    public void setCursorType(String cursorType) {
        this.cursorType = cursorType;
    }
}
