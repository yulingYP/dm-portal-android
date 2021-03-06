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

    //sp文件中用户头像url所用的对应的字段
    String spUserHead = "u_head_url";

    //sp文件中用户签名url所用的对应的字段
    String spUserSign = "u_sign_url";

    //sp文件中url所用的对应的字段
    String spUserId = "u_id";

    //sp文件中url所用的对应的字段
    String spUserType = "u_type";

    //sp文件中审批学生请假权限对应的字段
    String spApprovalStuAut= "u_approval_student_authority";

    //sp文件中审批教师请假权限对应的字段
    String spApprovalTeaAut= "u_apprpval_teacher_authority";

    //sp文件中localimg所用的对应的字段
    String spUserLocalimg = "u_localimg";

    //sp文件中院系id
    String spFacultyId = "u_faculty_id";

    //sp文件中教师请假部门id
    String spBranchId = "u_branch_id ";

    //sp文件中教师请假部门名称
    String spBranchName = "u_branch_name ";

    //sp文件中院系name
    String spFacultyName = "u_faculty_name";

    //sp文件中班级id
    String spClassId = "u_class_id";

    //sp文件中班级名称
    String spClassName = "u_class_name";

    //sp文件中各类历史的tag 格式 xxx,xxx,xxx
    String spSearchHistory = "u_history";

    //sp文件中用户设置的tag
    String spUserSetting = "u_setting";

    //sp文件中上次请假的时间
    String spLeaveTime= "u_leave_time";

    //sp文件中上次权限申请的时间
    String spApplyTime= "u_apply_time";

}
