package com.definesys.base;

/**
 * Created by 羽翎 on 2018/10/11.
 */

public class BaseResponse<T> {
    private String code ;
    private String msg ;
    private T data ;
    private int extendInfo;//附加字段

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg==null?"":msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getExtendInfo() {
        return extendInfo;
    }

    public void setExtendInfo(int extendInfo) {
        this.extendInfo = extendInfo;
    }

    @Override
    public String toString() {
        return "BaseResponse{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data.toString() +
                '}';
    }
}
