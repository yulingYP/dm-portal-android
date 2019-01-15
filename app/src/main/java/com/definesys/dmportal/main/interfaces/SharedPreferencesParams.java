package com.definesys.dmportal.main.interfaces;

public interface SharedPreferencesParams {
    //sp 存储user对象的文件名
    String spFileName = "userConfig";
    //sp文件中用户姓名的对应字段
    String spUserName = "u_name";
    //sp文件中用户手机的对应字段
    String spUserPhone = "u_phone";
    //sp文件中用户性别的对应字段
    String spUserSex = "u_sex";
    //sp文件中用户头像的对应字段
    String spUserImage = "u_image";

    //sp文件中token所用的对应的字段
    String spToken = "sp_token";

    //sp文件中url所用的对应的字段
    String spUserUrl = "u_url";
    //sp文件中url所用的对应的字段
    String spUserId = "u_id";

    //sp文件中localimg所用的对应的字段
    String spUserLocalimg = "u_localimg";

    //sp文件中院系id
    String spFaculty = "u_faculty";
}
