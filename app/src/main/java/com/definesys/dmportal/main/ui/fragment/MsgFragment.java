package com.definesys.dmportal.main.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.definesys.base.BaseFragment;
import com.definesys.base.BaseResponse;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.bean.MyMessage;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.appstore.utils.PermissionsUtil;
import com.definesys.dmportal.main.adapter.MsgRecycleViewAdapter;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.presenter.MessagePresenter;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.SmecRxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 消息页的 消息 子页面
 * 注意，是消息页
 */
public class MsgFragment extends BaseFragment<MessagePresenter> {

    @BindView(R.id.smart_refresh_fragment_msg)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycle_view_fragment_msg)
    RecyclerView recyclerView;

    Unbinder unbinder;
    @BindView(R.id.iv_nomessage)
    ImageView ivNomessage;
    Unbinder unbinder1;
    @BindView(R.id.tv_nomessage)
    TextView tvNomessage;
    private MsgRecycleViewAdapter myAdapter;
    private List<MyMessage> messageList;
    private int requestPage;//请求页码

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_msg, container, false);
        unbinder1 = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        initView();
//        refreshLayout.autoRefresh();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected MessagePresenter getPersenter() {
        return new MessagePresenter(getActivity());
    }

    private void initView() {
//        tvNomessage.setTextSize(DensityUtil.px2sp(getActivity(),28));
        messageList = new ArrayList<>();

        //下拉刷新
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            requestPage = 1;
            messageList.clear();
            if(!PermissionsUtil.isNetworkConnected(getContext())){
                hide(2);
                return;
            }
            if(myAdapter!=null)
                myAdapter.notifyDataSetChanged();
            mPersenter.getMsg(SharedPreferencesUtil.getInstance().getUserId(), requestPage);
        });

        //上拉刷新
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            ++requestPage;
            mPersenter.getMsg(SharedPreferencesUtil.getInstance().getUserId(), requestPage);

        });
        initList();
        hide(1);
    }

    private void initList() {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        myAdapter = new MsgRecycleViewAdapter(getActivity(), messageList);

        recyclerView.setAdapter(myAdapter);
    }

    /**
     * 传入消息内容
     * 重设当前页数、是否尾页、适配器内容
     * 通知页面刷新成功
     *
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_MESSAGE)
    }, thread = EventThread.MAIN_THREAD
    )
    public void successfulGetMessage(BaseResponse<List<MyMessage>> data) {
        if(requestPage==1) {//下拉刷新
            refreshLayout.finishRefresh(true);
            refreshLayout.finishLoadMore(true);
        }
        else//加载更多
            refreshLayout.finishLoadMore(true);
        if((data.getData()==null||data.getData().size()==0)&&messageList.size()==0)//没有数据
            hide(1);
        else if(data.getData()==null||data.getData().size()==0){//已经到最后一页
            Toast.makeText(getContext(),data.getMsg(),Toast.LENGTH_SHORT).show();
            --requestPage;
            refreshLayout.finishLoadMoreWithNoMoreData();
        }
        else {//有数据
            int currentSize = messageList.size();
            messageList.addAll(data.getData());
            if(myAdapter==null)
                initList();
            else {
                myAdapter.notifyItemRangeChanged(currentSize, data.getData().size());
            }
            if(data.getData().size()< Constants.requestSize)
                refreshLayout.setNoMoreData(true);
             show();
             mPersenter.updateMsgStatus(SharedPreferencesUtil.getInstance().getUserId(),null);
        }
        SmecRxBus.get().post("setRed",false);
    }

    /**
     * 传入消息
     * Toast通知用户消息内容
     * 设置刷新失败
     *
     * @param msg 失败消息
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.ERROR_GET_MESSAGE)
    }, thread = EventThread.MAIN_THREAD
    )
    public void errorGetMessage(String msg) {
        if(msg!=null&&!"".equals(msg))
            Toast.makeText(getContext(),msg,Toast.LENGTH_SHORT).show();
        refreshLayout.finishRefresh(false);
        refreshLayout.finishLoadMore(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder1.unbind();
    }

    private void show(){
        tvNomessage.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        ivNomessage.setVisibility(View.GONE);
    }

    private void hide(int i){
        tvNomessage.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        ivNomessage.setVisibility(View.VISIBLE);
        if(i == 1 )
            tvNomessage.setText(R.string.no_msg_tip);
        else
            tvNomessage.setText(R.string.no_net_tip);
    }

    public void reFresh(){
        requestPage=1;
        refreshLayout.autoRefresh();
    }
    //添加消息
    public void addMsg(MyMessage myMessage){
     messageList.add(0,myMessage);
     //改为已读状态
     mPersenter.updateMsgStatus(myMessage.getUserId(), myMessage.getMessageId());
     if(myAdapter==null)
         initList();
     else {
//         myAdapter.notifyDataSetChanged();
         myAdapter.notifyItemInserted(0);//通知演示插入动画
         myAdapter.notifyItemRangeChanged(0,messageList.size());//通知数据与界面重新绑定
     }
    }


}
