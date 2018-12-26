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

import com.definesys.base.BaseFragment;
import com.definesys.dmportal.R;
import com.definesys.dmportal.main.adapter.MsgRecycleViewAdapter;
import com.definesys.dmportal.main.bean.DataContent;
import com.definesys.dmportal.main.bean.Message;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.presenter.MessagePresenter;
import com.definesys.dmportal.main.util.MsgIconIdUtil;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

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
    private List<Message> messageList;
    private boolean isLastPage;
    private int requestPage;

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
        refreshLayout.autoRefresh();
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
        requestPage = 1;
        messageList = new ArrayList<>();
        LinearLayoutManager manager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        myAdapter = new MsgRecycleViewAdapter(getActivity(), messageList);

        recyclerView.setAdapter(myAdapter);

        refreshLayout.setOnRefreshListener(refreshLayout -> {
            requestPage = 1;
            mPersenter.getMsg(SharedPreferencesUtil.getInstance().getUserCode(), requestPage,1);
            Observable
                    .timer(5, TimeUnit.SECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            refreshLayout.finishRefresh(false);

                        }
                    });
        });
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            if (!isLastPage) {
                mPersenter.getMsg(SharedPreferencesUtil.getInstance().getUserCode(), requestPage,2);
                Observable
                        .timer(5, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                refreshLayout.finishLoadMore(false);

                            }
                        });
            } else {
                refreshLayout.finishLoadMore();
                show();
            }


        });
        hide(1);
    }

    /**
     * 传入消息内容
     * 重设当前页数、是否尾页、适配器内容
     * 通知页面刷新成功
     *
     * @param dataContent 消息内容
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_MESSAGE)
    }, thread = EventThread.MAIN_THREAD
    )
    public void successfulGetMessage(DataContent<Message> dataContent) {
        isLastPage = dataContent.isLastPage();
        //初始化工具类
        dataContent.getList().get(0).setMsgStatus("已审批");
        dataContent.getList().get(0).setMsgTitle("您的请假申请已通过");
        dataContent.getList().get(1).setMsgTitle("您的请假申请正在审批中,点击查看进度详情");
        dataContent.getList().get(2).setMsgStatus("已拒绝");
        dataContent.getList().get(2).setMsgTitle("您的请假申请已被拒绝，点击查看详细内容");
        MsgIconIdUtil msgIconIdUtil = new MsgIconIdUtil();
        for (Message message : dataContent.getList()) {

//            message.setMsgDate(new SimpleDateFormat(getString(R.string.time_format),Locale.getDefault()).format(message.getMsgDate()));// 格式化时间
            message.setMsgIcon(msgIconIdUtil.getMsgStatusIcon(message.getMsgStatus()));   //  设置图标
        }
        if (refreshLayout.getState().isHeader) {
            this.messageList.clear();
            myAdapter.notifyDataSetChanged();
        }
        requestPage++;
        int size = this.messageList.size();
        this.messageList.addAll(dataContent.getList());

        Collections.sort(messageList, new Comparator<Message>(){
            @Override
            public int compare(Message o1, Message o2) {
                return o2.getMsgDate().compareTo(o1.getMsgDate());
            }
        });

        int currentSize = this.messageList.size();
        myAdapter.notifyItemRangeChanged(currentSize, size);
        myAdapter.notifyDataSetChanged();
        //传入true表示刷新成功
        if (refreshLayout.getState().isHeader) {
            refreshLayout.finishRefresh(true);
        } else {
            refreshLayout.finishLoadMore(true);
        }

        if ( messageList != null   || messageList.size() > 0) {
            show();
        }
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
    public void errorGetMessage(Object msg) {
        refreshLayout.finishRefresh(false);
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
            tvNomessage.setText("暂无动态");
        else
            tvNomessage.setText("暂无网络");
    }
}
