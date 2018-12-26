package com.definesys.dmportal.main.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.definesys.dmportal.MainApplication;
import com.definesys.dmportal.R;
import com.definesys.dmportal.main.bean.User;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyFragment} interface
 * to handle interaction events.
 * Use the {@link MyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static final int SETTING_CODE = 2101;

    @BindView(R.id.main_fragment_my)
    View top;

    private Unbinder unbinder;
    private ImageView userImage;
    private TextView userName;
    private TextView welcomeText;
    private MainApplication application;
    RequestOptions option = new RequestOptions().centerCrop().placeholder(R.drawable.my).error(R.drawable.my);

    public MyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyFragment newInstance(String param1, String param2) {
        MyFragment fragment = new MyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        initView();
        refreshUserImage();
        refreshUserInformation();
    }


    //初始化界面
    private void initView() {
        application = (MainApplication) getActivity().getApplication();

        userImage = top.findViewById(R.id.head_item_uc);

        userName = top.findViewById(R.id.name_item_uc);


        welcomeText = top.findViewById(R.id.hello_item_uc);


        top.setOnClickListener((view) ->
                ARouter.getInstance().build("/dmportal/usercenter/UserInformationActivity").navigation());


    }

    /**
     * 更新完成刷新显示用户头像
     */
    public void refreshUserImage() {
        String str = SharedPreferencesUtil.getInstance().getUserLocal();
        if("".equals(str)){
            str = SharedPreferencesUtil.getInstance().getUser().getUrl();
        }
        Glide.with(this).load(str).apply(option).into(userImage);
    }

    /**
     * 更新完成刷新显示用户其他信息（姓名、称呼）
     */
    public void refreshUserInformation() {
//        userName.setText(SharedPreferencesUtil.getInstance().getUserName());
//        // 判断性别设置欢迎语
//        String sex_temp = SharedPreferencesUtil.getInstance().getUserSex();
//        if (!sex_temp.equals("")) {
//            if (sex_temp.equals(getString(R.string.sex_male))) {
//                welcomeText.setText(getString(R.string.uc_hello, getString(R.string.sex_male_call)));
//            } else {
//                welcomeText.setText(getString(R.string.uc_hello, getString(R.string.sex_female_call)));
//            }
//        } else {
//            welcomeText.setText(getString(R.string.uc_hello, ""));
//        }

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
        unbinder.unbind();
        super.onDestroyView();
    }
}
