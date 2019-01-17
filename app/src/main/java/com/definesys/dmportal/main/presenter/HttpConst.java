package com.definesys.dmportal.main.presenter;

public interface HttpConst {
    //主机地址 端口号等
    String url = "http://192.168.43.248:9001/last_design/";

    //获取课表
    String getTable = "listCurArg";

    //提交请假请求
    String submitLeaveRequest="submitLeaveRequest";

    //上传图片
    String uploadLeaveImg="uploadLeavePicture";

    //根据Id获取请假记录
    String getLeaveInfoById="getLeaveInfoById";

    //1.登陆
     String userLogin = "userLogin";

    //2.注册
     String userInsert = "userRegister";

    //3.获取验证码
     String getVerificationCode = "getVerificationCode";

    //4.获取消息列表
     String getStaticMessage = "getStaticMessage";


    //22.获取新闻
     String getNews = "getNews";

}
