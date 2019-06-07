package com.definesys.dmportal.appstore.presenter;

import android.content.Context;
import com.definesys.base.BasePresenter;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.appstore.bean.ApplyInfo;
import com.definesys.dmportal.appstore.bean.MyMessage;
import com.definesys.dmportal.appstore.tempEntity.AuthorityDetail;
import com.definesys.dmportal.appstore.utils.DensityUtil;
import com.definesys.dmportal.main.presenter.HttpConst;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.google.gson.Gson;
import com.hwangjr.rxbus.SmecRxBus;
import com.vise.xsnow.http.ViseHttp;
import com.vise.xsnow.http.callback.ACallback;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 * Created by 羽翎 on 2019/3/2.
 */

public class LeaveAuthorityPresenter extends BasePresenter {
    public LeaveAuthorityPresenter(Context context) {
        super(context);
    }
    //获取用户对应权限的详细信息
    public void getUserAuthorityDetail(Number userId, int authorityType, int authority){
        HashMap<String,Number> map = new HashMap<>();
        map.put("userId",userId);
        map.put("authority",authorityType==1?authority+10:authority); //审批老师权限的时候+10；方便以后整理textView
        map.put("authorityType",authorityType);
        ViseHttp.POST(HttpConst.getAuthorityDetailInfo)
                .tag(HttpConst.getAuthorityDetailInfo)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<List<String>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<String>> data) {
                        switch (data.getCode()){
                            case "200":
                                data.setExtendInfo(authorityType);
                                data.setMsg(""+authority);
//                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_AUTHORITY_DETAIL_INFO,  data);
                                break;
                            default:
                                SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, data.getMsg());
                                break;

                        }
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, "");
                    }
                });
    }

    /**
     *获取用户详细权限信息
     * @param userId u
     * @param authorityType 0只要学生权限 1.只要教师权限 2.获取所有权限
     * @param authorities 0.审批学生权限 1.审批教师权限 2.全部权限  审批教师的权限为（单个权限+10）
     * @param isShow  是否展示错误信息
     */
    public void getUserAuthorityDetail(Number userId, int authorityType, List<String> authorities,boolean isShow){
        HashMap<String,Object> map = new HashMap<>();
        map.put("userId",userId);
        map.put("authorities",authorities); //审批老师权限的时候+10；方便以后整理textView
        ViseHttp.POST(HttpConst.getAuthorityDetailInfo)
                .tag(HttpConst.getAuthorityDetailInfo)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<List<AuthorityDetail>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<AuthorityDetail>> data) {
                        switch (data.getCode()){
                            case "200":
                                data.setExtendInfo(authorityType);
//                                data.setMsg(""+authority);
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_AUTHORITY_DETAIL_INFO,  data);
                                break;
                            default:
                               if(isShow) SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, data.getMsg());
                                break;

                        }
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        if(isShow) SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, "");
                    }
                });
    }
    /**
     * 获取对应的数组
     * @param extendId1 type: 0/2.用户id 1.班级id *100 3/4/5/6/7/8/9/10/11/12/20/21.空
     * @param extendId2 type: 0/2.院系id 1.用户性别 3.空 4.院系名称 5.空 6.院系名称7.班级id 8.空 9.院系名称 10/11/12/20/21.空
     * @param type 0.寝室长权限根据facultyId获取班级名称
     *             1.根据班级id获取班级名单
     *             2.班长权限 根据facultyId获取班级名称
     *             3.班主任权限 获取院系列表
     *             4.获取该院系所有班级的id
     *             5.毕设老师权限 获取所有院系的名称
     *             6.毕设老师权限 获取该院系所有班级的id
     *             7.获取班级全部成员
     *             8.辅导员权限 获取院系列表
     *             9.获取所有班级id
     *             10.学院实习工作负责人权限 获取院系列表
     *             11.学生工作负责人权限 获取院系列表
     *             12.教学院长权限 获取院系列表
     *             20.部门请假负责人权限 获取所有部门的id
     *             21.部门教学院长权限 获取所有部门的id
     */
    public void getApplyList(Number extendId1, String extendId2,int type){
        if(type ==0||type==2)
            extendId1 = (extendId1.intValue()/10000)*100; //例151110401 则 1511100
        HashMap<String,Object> map = new HashMap<>();
        map.put("extendId1",extendId1);
        map.put("extendId2",extendId2);
        map.put("type",type);
        ViseHttp.POST(HttpConst.getApplyListInfo)
                .tag(HttpConst.getAuthorityDetailInfo)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<List<String>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<String>> data) {
                        switch (data.getCode()){
                            case "200":
                                data.setExtendInfo(type);
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_APPLY_LIST_INFO,  data);
                                break;
                            default:
                                SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, data.getMsg());
                                break;

                        }
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, "");
                    }
                });
    }
    //提交权限请求
    public void submitAuthoritiesApply(List<ApplyInfo> applyList, String applyReason){
        for(ApplyInfo applyInfo:applyList){
         applyInfo.setApplyReason(applyReason.trim());
         applyInfo.setApplyDetailContent(null);
         }
        ViseHttp.POST(HttpConst.submitAuthoritiesApply)
                .tag(HttpConst.getAuthorityDetailInfo)
                .setJson(new Gson().toJson(applyList))
                .request(new ACallback<BaseResponse<List<String>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<String>> data) {
                        switch (data.getCode()){
                            case "200":
                                data.setExtendInfo(100);
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_APPLY_LIST_INFO,  data);
                                for(String applyId:data.getData()){
                                    if(!"success".equals(applyId)) {
                                        //data.getData(),leaveInfo.getUserId(), (short) 2, content, (short)(isAgree?1:0) ,leaveInfo.getId(),null,new Date() )
                                        SmecRxBus.get().post("addMessage", new MyMessage(System.currentTimeMillis(), SharedPreferencesUtil.getInstance().getUserId(), (short) 4, "", (short) 4, applyId, new Date()));
                                    }
                                }
                                break;
                            default:
                                SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, data.getMsg());
                                break;

                        }
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, "");
                    }
                });
    }
    //删除权限
    public void deleteAuthorities(List<ApplyInfo> applyList){
        ViseHttp.POST(HttpConst.deleteAuthorities)
                .tag(HttpConst.getAuthorityDetailInfo)
                .setJson(new Gson().toJson(applyList))
                .request(new ACallback<BaseResponse<List<String>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<String>> data) {
                        switch (data.getCode()){
                            case "200":
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_DELETE_AUTHORITIES,  data);
                                int size = applyList.size();
                                List<String> applyIds = data.getData();
                                int applyListSize = applyIds.size()==0?1:applyIds.size();
                                for(int i = 0;i<size;i++){
                                    String id = applyIds.get(i%applyListSize);
                                    if(!"success".contains(id))
                                        applyList.get(i).setApplyId(DensityUtil.string2Long(id));
                                }
                                for(ApplyInfo applyInfo:applyList){
                                    //向消息页发送权限修改信息
                                    SmecRxBus.get().post("addMessage", new MyMessage(System.currentTimeMillis(), SharedPreferencesUtil.getInstance().getUserId(), (short) 6, applyInfo.getApplyStatus() == -100 ? "change" : "delete", applyInfo.getApplyAuthority().shortValue(), String.valueOf(applyInfo.getApplyId()), new Date()));
                                }
                                break;
                            default:
                                SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, data.getMsg());
                                break;

                        }
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                        SmecRxBus.get().post(MainPresenter.ERROR_NETWORK, "");
                    }
                });
    }
    //获取辅导员权限中不可删除的班级id列表
    public void getNoAbleDeleteClassId(Number userId){
        HashMap<String,Number> map = new HashMap<>();
        map.put("userId",userId);
        ViseHttp.POST(HttpConst.getNoAbleDeleteClassId)
                .tag(HttpConst.getAuthorityDetailInfo)
                .setJson(new Gson().toJson(map))
                .request(new ACallback<BaseResponse<List<String>>>() {
                    @Override
                    public void onSuccess(BaseResponse<List<String>> data) {
                        switch (data.getCode()){
                            case "200":
                                SmecRxBus.get().post(MainPresenter.SUCCESSFUL_GET_NOABLE_CLASS_IDS,  data);
                                break;
                        }
                    }

                    @Override
                    public void onFail(int errCode, String errMsg) {
                    }
                });
    }
    @Override
    public void unsubscribe() {
        super.unsubscribe();
        ViseHttp.cancelTag(HttpConst.getAuthorityDetailInfo);
    }
}
