package com.definesys.dmportal.appstore.bean;

import java.util.List;

/**
 * Created by 羽翎 on 2019/3/3.
 */

public class ApplyAuthority {
    private List<String> list;//选取的学号或班级id
    private String content;//展示的内容
    private int autType;//权限类型 0.审批学生 1.审批老师
    private int authority;//权限
}
