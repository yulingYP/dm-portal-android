package com.definesys.dmportal.main.presenter;

public interface HttpConst {
    //主机地址 端口号等 192.168.155.1 192.168.43.248 192.168.191.1
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
    String uploadPictures = "uploadPictures";

    //根据Id分页获取请假记录
    String getLeaveInfoById = "getLeaveInfoById";

    //获取请假搜索列表
    String getLeaveSearchList = "getLeaveSearchList";

    //根据Id分页获取该用户未审批的请假信息
    String getDealApprovalListById = "getDealApprovalListById";

    //根据Id分页获取该用户历史审批记录
    String getApprovalHistoryListById = "getApprovalHistoryListById";

    //根据请假Id获取审批记录
    String getApprovalRecordById = "getApprovalRecordById";

    //根据请假Id和审批时间或msgId获取审批记录
    String getApprovalRecord = "getApprovalRecord";

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

    //更新用户头像
    String uploadUserPicture="uploadUserPicture";

    //提交反馈建议
    String submitFeedBack = "submitFeedBack";

    //获取权限的详细描述
    String getAuthorityDetailInfo = "getAuthorityDetailInfo";

    //获取用户申请权限所需要的信息列表
    String getApplyListInfo = "getApplyListInfo";

    //提交权限申请
    String submitAuthoritiesApply = "submitAuthoritiesApply";

    //删除权限
    String deleteAuthorities = "deleteAuthorities";

    //获取辅导员权限中不可删除的班级id
    String getNoAbleDeleteClassId = "getNoAbleDeleteClassId";

    //根据id获取申请记录
    String getApplyInfoById = "getApplyInfoById";

    //获取搜索结果列表
    String getApplySearchList = "getApplySearchList";

    //提交审批结果
    String submitApplyResult = "submitApplyResult";

    //获取请求的申请列表或审批记录
    String getRequestApplyList = "getRequestApplyList";

}
