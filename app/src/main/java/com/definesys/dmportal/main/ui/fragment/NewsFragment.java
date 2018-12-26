package com.definesys.dmportal.main.ui.fragment;

import android.content.Context;
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

import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.base.BaseFragment;
import com.definesys.dmportal.MainApplication;
import com.definesys.dmportal.R;
import com.definesys.dmportal.main.adapter.NewsRecycleViewAdapter;
import com.definesys.dmportal.main.bean.DataContent;
import com.definesys.dmportal.main.bean.News;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.presenter.NewsPresenter;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * 消息页的新闻子页面
 * 注意，是新闻页
 */
public class NewsFragment extends BaseFragment<NewsPresenter> {

    @BindView(R.id.smart_refresh_fragment_news)
    SmartRefreshLayout refreshLayout;
    @BindView(R.id.recycle_view_fragment_news)
    RecyclerView recyclerView;
    @BindView(R.id.iv_nodynamic)
    ImageView ivNodynamic;
    Unbinder unbinder1;
    @BindView(R.id.tv_nodynamic)
    TextView tvNodynamic;

    private NewsRecycleViewAdapter myAdapter;
    private List<News> newsList;
    Unbinder unbinder;
    private int requestPage;
    private boolean isLastPage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_news, container, false);
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
    public void onResume() {
        super.onResume();
        if (MainApplication.getInstances().isHasNewMessage()) {
            refreshLayout.autoRefresh();
            MainApplication.getInstances().setHasNewMessage(false);
        }
    }

    private void initView() {
//        tvNodynamic.setTextSize(COMPLEX_UNIT_PX, 28);
        newsList = new ArrayList<>();
        requestPage = 1;
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myAdapter = new NewsRecycleViewAdapter(getActivity(), newsList);
        recyclerView.setAdapter(myAdapter);
        myAdapter.setOnItemClickListener(position -> {
            ARouter.getInstance()
                    .build("/dmportal/webview/WebViewActivity")
                    .withString("url",newsList.get(position).getNewsUrl())
                    .navigation();
        });

        //下拉刷新
        refreshLayout.setOnRefreshListener(refreshLayout -> {
            requestPage = 1;
            mPersenter.getNews(requestPage,1);
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
        //上拉加载
        refreshLayout.setOnLoadMoreListener(refreshLayout -> {
            if (!isLastPage) {
                mPersenter.getNews(requestPage,2);
                Observable
                        .timer(5, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new  Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                refreshLayout.finishLoadMore(false);
                            }
                        });
            } else {
                refreshLayout.finishLoadMore();
            }
        });

       hide(1);
    }

    /**
     * 获取新闻成功
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_NEWS)
    }, thread = EventThread.MAIN_THREAD
    )
    public void successfulGetNews(DataContent<News> dataContent) {
        this.isLastPage = dataContent.isLastPage();
        //TODO 格式化时间
/*
        for (News m : dataContent.getList()) {

            m.setNewsDate(new SimpleDateFormat(getString(R.string.time_format)).format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("m.getNewsDate())")); // 格式化时间
        }
*/
        // 如果是下拉刷新，先清空列表
        if (refreshLayout.getState().isHeader) {
            this.newsList.clear();
            myAdapter.notifyDataSetChanged();
        }
        int size = this.newsList.size();
        this.newsList.addAll(dataContent.getList());
        int currentSize = this.newsList.size();
        myAdapter.notifyItemRangeChanged(currentSize, size);
        requestPage++;
        //传入true表示刷新成功
        if (refreshLayout.getState().isHeader) {
            refreshLayout.finishRefresh(true);
        } else {
            refreshLayout.finishLoadMore(true);
        }

        if (newsList.size() > 0) {
           show();
        } else {
            hide(1);
        }
    }

    /**
     * 获取新闻失败
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.ERROR_GET_NEWS)
    }, thread = EventThread.MAIN_THREAD
    )
    public void errorGetNews(Object msg) {

            refreshLayout.finishRefresh(false);

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    protected NewsPresenter getPersenter() {
        return new NewsPresenter(getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder1.unbind();
    }

    private void show(){
        tvNodynamic.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        ivNodynamic.setVisibility(View.GONE);
    }

    private void hide(int i){
        if(i == 1 )
        tvNodynamic.setText("暂无动态");
        else
        tvNodynamic.setText("暂无网络");
        tvNodynamic.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        ivNodynamic.setVisibility(View.VISIBLE);
    }
}
