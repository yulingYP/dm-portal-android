package com.definesys.dmportal.main.presenter;

public interface HttpConst {
    //主机地址 端口号等
    String url = "http://192.168.43.248:9001/last_design/";

    //短信验证的接口地址
    String eamilUrl ="http://api02.monyun.cn:7901/sms/v2/std/single_send";

    //获取用户信息
    String getUserInfo = "getUserInfo";

    //获取用户姓名
    String getUserName = "getUserName";

    //获取课表
    String getTable = "listCurArg";

    //获取成绩
    String listCursorScore = "listCursorScore";

    //提交请假请求
    String submitLeaveRequest = "submitLeaveRequest";

    //上传图片
    String uploadLeaveImg = "uploadLeavePicture";

    //根据Id分页获取请假记录
    String getLeaveInfoById = "getLeaveInfoById";

    String getLeaveSearchList = "getLeaveSearchList";

    //根据Id分页获取该用户未审批的请假信息
    String getDealApprovalListById = "getDealApprovalListById";

    //根据Id分页获取该用户历史审批记录
    String getApprovalHistoryListById = "getApprovalHistoryListById";

    //根据请假Id获取审批记录
    String getApprovalRecordById = "getApprovalRecordById";

    //根据请假Id和审批时间获取审批记录
    String getApprovalRecordByDate = "getApprovalRecordByDate";

    //根据请假Id获取请假信息
    String getLeaveInfoByLeaveId = "getLeaveInfoByLeaveId";

    //根据请假Id更新审批状态，并插入审批记录
    String updateApprovalStatusById = "updateApprovalStatusById";

    //获取当前请假状态
    String getCurrentApprovalStatus = "getCurrentApprovalStatusById";


    //获取最近一次请假状态
    String getCurrentLeaveInfoById = "getCurrentLeaveInfoById";

    //登陆
     String userLogin = "userLogin";

    //退出
    String userLogout = "userLogout";

    //注册
     String userInsert = "userRegister";

    //获取验证码
     String getEmailCode = "getEmailCode";

     //修改密码
     String changePassword = "changePassword";

     //绑定或解绑手机
     String bindPhone = "bindPhone";

     //获取消息列表
     String getStaticMessage = "getStaticMessage";

    //获取新闻
     String getNews = "getNews";

     //获取用户推送失败的消息
    String getPushErrorMessage = "getPushErrorMessage";

    //更新消息状态
    String updateMsgStatus = "updateMsgStatus";

    String uploadUserHeadPicture="uploadUserHeadPicture";
}
