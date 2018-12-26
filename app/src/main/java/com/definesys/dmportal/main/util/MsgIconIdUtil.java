package com.definesys.dmportal.main.util;

import com.bumptech.glide.request.RequestOptions;
import com.definesys.dmportal.R;

public class MsgIconIdUtil {
    public static RequestOptions userImageOption = new RequestOptions().placeholder(R.drawable.ic_news_image_loading).error(R.drawable.ic_news_image_loading);
    /**
     * 根据 消息状态返回消息对应的图标
     */
    public int getMsgStatusIcon(String msgStatus){
        int iconId = R.drawable.ic_msg_status_await;
        switch (msgStatus){
            case "已审批":iconId = R.drawable.ic_msg_status_accept;break;
            case "待审批":iconId = R.drawable.ic_msg_status_await;break;
            case "已拒绝":iconId = R.drawable.ic_msg_status_refuse;break;
        }
        return iconId;
    }
}