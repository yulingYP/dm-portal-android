package com.definesys.dmportal.main.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.base.BaseActivity;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.bean.MyMessage;
import com.definesys.dmportal.appstore.customViews.CustomTitleIndicator;
import com.definesys.dmportal.appstore.customViews.NoScrollViewPager;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.config.MyCongfig;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.presenter.UserInfoPresent;
import com.definesys.dmportal.main.ui.fragment.ContactFragment;
import com.definesys.dmportal.appstore.ui.fragment.HomeAppFragment;
import com.definesys.dmportal.main.ui.fragment.MyFragment;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding2.view.RxView;
import com.jpeng.jptabbar.JPTabBar;
import com.jpeng.jptabbar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
@Route(path = ARouterConstants.MainActivity)
public class MainActivity extends BaseActivity<MainPresenter> {


    @BindView(R.id.mTitlebar)
    CustomTitleBar mTitlebar ;

    @BindView(R.id.mTabbar)
    JPTabBar mTabbar ;

    @BindView(R.id.mViewPager)
    NoScrollViewPager mViewPager ;

    CustomTitleIndicator titleIndicator;
    private int currentPosition=1;

    private ContactFragment contactFragment ;
    private HomeAppFragment homeAppFragment ;
    private MyFragment myFragment ;
    public static int screenWith;//手机屏幕的宽度
    public static int screenHeight;//手机屏幕的高度
    public static boolean XG_isBind = false;//是否绑定信鸽
    private boolean isFirst = true;//第一次点击消息页
//    private NotificationManager notiManager;//通知栏管理
//    private int notifyID=0;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case Constants.XG_CODE://检测信鸽是否绑定
                    if(!XG_isBind){
                        Log.d("myXG","reBind");
//                        XGPushManager.bindAccount(MainActivity.this,String.valueOf(SharedPreferencesUtil.getInstance().getUserId()));
                        handler.sendEmptyMessageDelayed(Constants.XG_CODE,Constants.sendDelayTime*5);
                    }
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        RxBus.get().register(this);
//        JPushInterface.init(getApplicationContext());

        //提醒模式
        MyCongfig.remindMode = SharedPreferencesUtil.getInstance().getUserSetting();
        //信鸽是否绑定
//        handler.sendEmptyMessage(Constants.XG_CODE);
        //获取用户信息
        (new UserInfoPresent(this)).getUserInfo(SharedPreferencesUtil.getInstance().getUserId(),SharedPreferencesUtil.getInstance().getUserType());
        //获取手机宽高
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWith = dm.widthPixels;
        Log.d("myWidth", "" + screenWith + "  " + screenHeight);

        initView();

    }

    private void initView() {
        mTitlebar.setTitle(R.string.tab3);
        mTitlebar.setBackgroundDividerEnabled(false);
        mTitlebar.showTitleView(true);
        mTabbar.setTitles( R.string.tab2, R.string.tab3, R.string.tab4)
                .setNormalIcons(R.mipmap.tab3_normal, R.mipmap.tab1_normal ,R.mipmap.tab4_normal)
                .setSelectedIcons( R.mipmap.tab3_selected, R.mipmap.tab1_selected ,R.mipmap.tab4_selected)
                .generate();
        mTabbar.setSelectTab(currentPosition);
        mTabbar.setSelectedColor(Color.parseColor("#3f475a"));
        mTabbar.setTabListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                currentPosition = position;
                mViewPager.setCurrentItem(position, false);

                if((position==0&& mTabbar.getTabAtPosition(position).isBadgeShow())||isFirst){
                    contactFragment.freshMsgFragment();
                    isFirst = false;
                }
            }

            @Override
            public boolean onInterruptSelect(int index) {
                return false;
            }


        });

        myFragment = MyFragment.newInstance("","");
        contactFragment = ContactFragment.newInstance("","");
       // groupFragment= GroupFragment.newInstance("","");
        homeAppFragment = HomeAppFragment.newInstance();
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(contactFragment);
        fragmentList.add(homeAppFragment);
        //fragmentList.add(groupFragment);
        fragmentList.add(myFragment);

        mViewPager.setScroll(false);
        mViewPager.setAdapter(new MainFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setCurrentItem(currentPosition, true);
        if(mViewPager.getAdapter()!=null)mViewPager.getAdapter().notifyDataSetChanged();

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
                if (position == 0) {
                    mTitlebar.setTitle("");
                    mTitlebar.setCenterView(titleIndicator);
                    titleIndicator.setVisibility(View.VISIBLE);
                    titleIndicator.setTitle1Text(getString(R.string.message));
                    titleIndicator.setTitle2Text(getString(R.string.trends));
                    titleIndicator.setFocus(titleIndicator.getSelectItem());
                } else {
                    titleIndicator.setVisibility(View.GONE);
                    mTitlebar.showTitleView(true);
                    if(position==1)mTitlebar.setTitle(R.string.tab3);
                    else if(position==2)mTitlebar.setTitle(R.string.tab4);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        RxView.clicks(mTitlebar)
                .subscribe(obj->myFragment.refreshUserImage());
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
    /**
     * 获取网络头像成功
     * @param str
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_USER_INFO)
    }, thread = EventThread.MAIN_THREAD)
    public void getUserUrl(String str) {
       if(myFragment!=null) {
           SharedPreferencesUtil.getInstance().setUserLocal("");
           myFragment.refreshUserImage();
           myFragment.updateShowInfo();
       }
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
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);// 信鸽必须要调用这句
        //点击通知进入主页进行的操作

        singleLogout(intent!=null&&intent.getBooleanExtra(getString(R.string.exit_en), false));
    }
    /**
     * 退出登录
     */
    @Subscribe(tags = {
            @Tag("exitActivity")
    }, thread = EventThread.MAIN_THREAD)
    public void singleLogout(Boolean isExit) {
        if (isExit) {
            //信鸽解绑
            Log.d("myXG","unbind");
//            XGPushManager.delAccount(this,String.valueOf(SharedPreferencesUtil.getInstance().getUserId()));
//            XG_isBind = false;
            SharedPreferencesUtil.getInstance().clearUser();
            ARouter.getInstance().build(ARouterConstants.LoginAcitvity).navigation(this, new NavCallback() {
                @Override
                public void onArrival(Postcard postcard) {
                    MainActivity.this.finish();
                }
            });
        }
    }
    /**
     * 重新绑定信鸽
     */
    @Subscribe(tags = {
            @Tag("reBind")
    }, thread = EventThread.MAIN_THREAD)
    public void reBind(Object o){
        handler.sendEmptyMessage(Constants.XG_CODE);
    }
    /**
     * 添加消息
     */
    @Subscribe(tags = {
            @Tag("addMessage")
    }, thread = EventThread.MAIN_THREAD)
    public void addMessage(MyMessage message) {
        if(currentPosition==0&&contactFragment.getCurrentitem()==0){//在消息页面
            contactFragment.getMsgFragment().addMsg(message);
        } else //显示红点
            mTabbar.getTabAtPosition(0).showCirclePointBadge();
    }

    //消息页红点
    @Subscribe(
            tags = {@Tag("setRed")},
            thread = EventThread.MAIN_THREAD
    )
    public void setRed(Boolean isRed){
        if(isRed) {
            mTabbar.getTabAtPosition(0).showCirclePointBadge();
        }
        else {
            mTabbar.getTabAtPosition(0).hiddenBadge();
//            deleteNofi();
        }
    }
    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);
                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
//                    if (!ExampleUtil.isEmpty(extras)) {
//                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
//                    }
//                    setCostomMsg(showMsg.toString());
                }
            } catch (Exception e){
            }
        }
    }

//    private void setCostomMsg(String msg){
//        if (null != msgText) {
//            msgText.setText(msg);
//            msgText.setVisibility(android.view.View.VISIBLE);
//        }
//    }

}
