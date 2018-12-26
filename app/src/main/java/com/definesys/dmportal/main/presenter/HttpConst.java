package com.definesys.dmportal.main.presenter;

public class HttpConst {
    //主机地址 端口号等
    public static final String url = "http://wcpublic.smec-cn.com:7777/simp/simp/TEISService";

    //1.登陆
    public static final String userLogin = "userLogin";

    //2.注册
    public static final String userInsert = "userRegister";

    //3.获取验证码
    public static final String getVerificationCode = "getVerificationCode";

    //4.获取消息列表
    public static final String getStaticMessage = "getStaticMessage";

    //5.获取个人信息
    public static final String getPersonMessage = "getPersonInfo";

    //6.修改个人信息
    public static final String changeMessage = "updatePersonInfo";

    //7.获取IC卡列表
    public static final String getICCardAuth = "getICCardAuth";

    //8.获取未过期访客二维码列表（是否包括详情）
    public static final String getNotExpiredQRCode = "getNotExpiredQRCode";

    //9.提交访客二维码信息
    public static final String addVisitorInfo = "addVisitorInfo";

    //10.删除IC卡权限
    public static final String delICCardAuth = "delICCardAuth";

    //11.保存IC卡设置信息
    public static final String saveCardSetting = "saveCardSetting";

    //12.获取成员列表
    public static final String getMemberInfo = "getMemberInfo";

    //13.获取每一个成员权限详情
    public static final String getMemberAuthDetail = "getMemberAuthDetail";

    //14.删除成员权限（按人/按权限）
    public static final String delMemberAuth = "delMemberAuth";

    //15.提交成员申请
    public static final String addMember = "addMember";

    //16.获取手机号对应姓名
    public static final String getMemberNameByPhone = "getMemberNameByPhone";

    //17.获取成员申请可分配权限
    public static final String getAssignAuth = "getAssignAuth";

    //18.提交反馈建议
    public static final String feedback = "feedback";

    //19.修改密码（原密码/短信验证码修改）
    public static final String changePassword = "updatePassword";

    //20.检查更新
    public static final String updateVersion = "updateVersion";

    //21.退出登录
    public static final String logOut = "logOut";

    //22.获取新闻
    public static final String getNews = "getNews";

    //23.保存用户设置接口
    public static final String saveUserConfig = "saveUserConfig";

    //24.修改头像
    public static final String changeAttachment = "updateAvatar";

    //26.获取卡片详细信息
    public static final String getICCardDetail = "getICCardDetail";

    //27.修改IC卡背景
    public static final String saveCardBackground = "saveCardBackground";
}
