package com.definesys.dmportal.appstore.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.base.BaseFragment;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.presenter.HomeAppPresenter;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.webview.ui.WebViewActivity;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeAppFragment} interface
 * to handle interaction events.
 * Use the {@link HomeAppFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeAppFragment extends BaseFragment<HomeAppPresenter> {

    @BindView(R.id.leave_layout)
    LinearLayout lg_leave;

    @BindView(R.id.confirm_layout)
    LinearLayout lg_confirm;

    private Unbinder unbinder;


    public HomeAppFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeAppFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeAppFragment newInstance() {
        HomeAppFragment fragment = new HomeAppFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected HomeAppPresenter getPersenter() {
        return new HomeAppPresenter(this.getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_app, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView(view);
        return view;

    }

    private void initView(View view) {
        RxView.clicks(lg_leave)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        ARouter.getInstance()
                                .build(ARouterConstants.LeaveActivity)
                                .navigation();
                    }
                });
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
