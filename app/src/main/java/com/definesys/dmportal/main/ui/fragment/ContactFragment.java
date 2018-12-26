package com.definesys.dmportal.main.ui.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.definesys.dmportal.R;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.SmecRxBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ContactFragment} interface
 * to handle interaction events.
 * Use the {@link ContactFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    MsgFragment msgFragment;
    NewsFragment newsFragment;

    @BindView(R.id.viewpager_fragment_ctc)
    ViewPager mViewpager;


    int currentitem = 0;

    Unbinder unbinder;

    public ContactFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactFragment newInstance(String param1, String param2) {
        ContactFragment fragment = new ContactFragment();
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
        return inflater.inflate(R.layout.fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
        SmecRxBus.get().register(this);
        initView();
        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * 滑动时，发送消息，改变字体大小和左边距
             * @param position 当前位置
             * @param positionOffset 偏移量（0 - 1）
             * @param positionOffsetPixels 偏移像素（0px - screen width）
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if(positionOffset!=1 && positionOffset!=0)
                    RxBus.get().post("scrolling", position + "," + Math.round(positionOffset * 100) / 100.0);
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state == ViewPager.SCROLL_STATE_IDLE) {
                    currentitem = mViewpager.getCurrentItem();
                    RxBus.get().post("selected",String.valueOf(currentitem));
                }
            }
        });
    }

    private void initView() {

        newsFragment = new NewsFragment();
        msgFragment = new MsgFragment();
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(msgFragment);
        fragments.add(newsFragment);
        mViewpager.setAdapter(new MainFragmentPagerAdapter(Objects.requireNonNull(getActivity()).getSupportFragmentManager(), fragments));
        mViewpager.setCurrentItem(0, true);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
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
        SmecRxBus.get().unregister(this);
    }

    class MainFragmentPagerAdapter extends FragmentPagerAdapter {

        //存储所有的fragment
        private List<Fragment> fragmentList;

        public MainFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.fragmentList = list;
            // TODO Auto-generated constructor stub
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }
    }

    public ViewPager getmViewpager() {
        return mViewpager;
    }

    public void setmViewpager(ViewPager mViewpager) {
        this.mViewpager = mViewpager;
    }
}
