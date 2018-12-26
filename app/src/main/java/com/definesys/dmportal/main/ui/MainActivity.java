package com.definesys.dmportal.main.ui;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.definesys.base.BaseActivity;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.customViews.CustomTitleIndicator;
import com.definesys.dmportal.appstore.customViews.NoScrollViewPager;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.ui.fragment.ContactFragment;
import com.definesys.dmportal.appstore.ui.fragment.HomeAppFragment;
import com.definesys.dmportal.main.ui.fragment.GroupFragment;
import com.definesys.dmportal.main.ui.fragment.MsgFragment;
import com.definesys.dmportal.main.ui.fragment.MyFragment;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.SmecRxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jpeng.jptabbar.JPTabBar;
import com.jpeng.jptabbar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity<MainPresenter> {


    @BindView(R.id.mTitlebar)
    CustomTitleBar mTitlebar ;

    @BindView(R.id.mTabbar)
    JPTabBar mTabbar ;

    @BindView(R.id.mViewPager)
    NoScrollViewPager mViewPager ;

    @BindView(R.id.search_box)
    LinearLayout lg_search;

    //搜索框
    @BindView(R.id.et_search)
    EditText ed_search;

    @BindView(R.id.cancel_button)
    TextView tv_cancel;

    CustomTitleIndicator titleIndicator;
    int currentPosition=0;

    ContactFragment contactFragment ;
    HomeAppFragment homeAppFragment ;
    GroupFragment groupFragment ;
    MyFragment myFragment ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        RxBus.get().register(this);
        initView();

    }

    private void initView() {
        mTitlebar.setTitle("请假");
        mTabbar.setTitles(R.string.tab1, R.string.tab2, R.string.tab3, R.string.tab4)
                .setNormalIcons(R.mipmap.tab1_normal, R.mipmap.tab3_normal, R.mipmap.tab2_normal ,R.mipmap.tab4_normal)
                .setSelectedIcons(R.mipmap.tab1_selected, R.mipmap.tab3_selected, R.mipmap.tab2_selected ,R.mipmap.tab4_selected)
                .generate();
        mTabbar.setSelectTab(currentPosition);
        setGone();
        mTabbar.setSelectedColor(Color.parseColor("#3f475a"));
        mTabbar.setTabListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                currentPosition = position;
                mViewPager.setCurrentItem(position, false);
                mTabbar.getTabAtPosition(position).hiddenBadge();   //隐藏红点
            }

            @Override
            public boolean onInterruptSelect(int index) {
                return false;
            }


        });

        myFragment = MyFragment.newInstance("","");
        contactFragment = ContactFragment.newInstance("","");
        groupFragment= GroupFragment.newInstance("","");
        homeAppFragment = HomeAppFragment.newInstance();

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(homeAppFragment);
        fragmentList.add(contactFragment);
        fragmentList.add(groupFragment);
        fragmentList.add(myFragment);

        mViewPager.setScroll(false);
        mViewPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setCurrentItem(currentPosition, true);
        mViewPager.getAdapter().notifyDataSetChanged();

        titleIndicator = new CustomTitleIndicator(this, null);
        titleIndicator.setOnTitleClickListener(new CustomTitleIndicator.OnTitleClickListener() {
            @Override
            public void onClick(int position) {
                titleIndicator.setFocus(position);
                contactFragment.getmViewpager().setCurrentItem(position);
            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                if (position == 1) {
                    mTitlebar.setTitle("");
                    mTitlebar.setCenterView(titleIndicator);
                    titleIndicator.setVisibility(View.VISIBLE);
                    titleIndicator.setTitle1Text(getString(R.string.message));
                    titleIndicator.setTitle2Text(getString(R.string.trends));
                    titleIndicator.setFocus(titleIndicator.getSelectItem());
                } else {
                    titleIndicator.setVisibility(View.GONE);
                    mTitlebar.showTitleView(true);
                    if(position==0)mTitlebar.setTitle(R.string.tab1);
                    if(position==2)mTitlebar.setTitle("");
                    else if(position==3)mTitlebar.setTitle(R.string.tab4);
                }
                if(position!=2)
                    setGone();
                else
                   setVisible();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public MainPresenter getPersenter() {
        return new MainPresenter(this);
    }


    @Subscribe(tags = {
            @Tag("scrolling")
    }, thread = EventThread.MAIN_THREAD)
    public void slidingResponse(String msg) {
        String[] m = msg.split(",");
        int position = Integer.parseInt(m[0]);
        float positionOffset = Float.parseFloat(m[1]);
        titleIndicator.setIndicatorFeatures(position, positionOffset);
    }

    @Subscribe(tags = {
            @Tag("selected")
    }, thread = EventThread.MAIN_THREAD)
    public void selectResponse(String position) {
        titleIndicator.setSelectItem(Integer.valueOf(position));
        titleIndicator.setFocus(Integer.valueOf(position));
    }

    class MainFragmentPagerAdapter extends FragmentPagerAdapter {

        //存储所有的fragment
        private List<Fragment> fragmentList;
        public MainFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
            super(fm);
            this.fragmentList=list;
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
    //隐藏搜索框
    public void setGone(){
        tv_cancel.setVisibility(View.GONE);
        ed_search.setVisibility(View.GONE);
        lg_search.setVisibility(View.GONE);
    }
    //显示搜索框
    public void setVisible(){
        tv_cancel.setVisibility(View.VISIBLE);
        ed_search.setVisibility(View.VISIBLE);
        lg_search.setVisibility(View.VISIBLE);
    }
}
