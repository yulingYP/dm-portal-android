package com.definesys.dmportal.main.interfaces;

public interface SharedPreferencesParams {
    //sp文件中用户姓名的对应字段
    String isFirstOpen = "u_open";
    //sp 存储user对象的文件名
    String spFileName = "userConfig";
    //sp文件中用户姓名的对应字段
    String spUserName = "u_name";
    //sp文件中用户手机的对应字段
    String spUserPhone = "u_phone";
    //sp文件中用户性别的对应字段
    String spUserSex = "u_sex";

    //sp文件中token所用的对应的字段
    String spToken = "sp_token";

    //sp文件中url所用的对应的字段
    String spUserUrl = "u_url";

    //sp文件中url所用的对应的字段
    String spUserId = "u_id";

    //sp文件中url所用的对应的字段
    String spUserType = "u_type";
    //sp文件中url所用的对应的字段
    String spUserAuthority= "u_authority";

    //sp文件中localimg所用的对应的字段
    String spUserLocalimg = "u_localimg";

    //sp文件中院系id
    String spFaculty = "u_faculty";

    //sp文件中院系name
    String spFacultyName = "u_faculty_name";

    //sp文件中班级id
    String spClass = "u_class";

    //sp文件中各类历史的tag 格式 xxx,xxx,xxx
    String spSearchHistory = "u_history";


    //sp文件中用户设置的tag
    String spUserSetting = "u_setting";

    //sp文件中请假的历史记录 格式 xxx,xxx,xxx
    String spLeaveSearchHistory0 = "u_history0";

    //sp文件中待审批记录 格式 xxx,xxx,xxx
    String spLeaveSearchHistory1 = "u_history1";

    //sp文件中审批的历史记录 格式 xxx,xxx,xxx
    String spLeaveSearchHistory2 = "u_history2";
}
