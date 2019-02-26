package com.definesys.dmportal.main.ui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.callback.NavCallback;
import com.alibaba.android.arouter.launcher.ARouter;
import com.definesys.base.BaseActivity;
import com.definesys.dmportal.MainApplication;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.ApprovalLeaveInfoActivity;
import com.definesys.dmportal.appstore.LeaveInfoDetailActivity;
import com.definesys.dmportal.appstore.LeaveListActivity;
import com.definesys.dmportal.appstore.bean.ApprovalRecord;
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
import com.example.jpushdemo.ExampleUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hwangjr.rxbus.RxBus;
import com.hwangjr.rxbus.annotation.Subscribe;
import com.hwangjr.rxbus.annotation.Tag;
import com.hwangjr.rxbus.thread.EventThread;
import com.jakewharton.rxbinding2.view.RxView;
import com.jpeng.jptabbar.JPTabBar;
import com.jpeng.jptabbar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.data.JPushLocalNotification;

@Route(path = ARouterConstants.MainActivity)
public class MainActivity extends BaseActivity<UserInfoPresent> {

    @BindView(R.id.mTitlebar)
    CustomTitleBar mTitlebar ;

    @BindView(R.id.mTabbar)
    JPTabBar mTabbar ;

    @BindView(R.id.mViewPager)
    NoScrollViewPager mViewPager ;

    @Autowired(name = "isLogin")
    boolean isLogin;//是否通过登陆进入主页

    @Autowired(name = "message")
    String splashMessage;//从跳转页得到的消息，可能是json化的Message实例

    CustomTitleIndicator titleIndicator;
    private int currentPosition=1;

    private ContactFragment contactFragment ;
    private HomeAppFragment homeAppFragment ;
    private MyFragment myFragment ;
    public static int screenWith;//手机屏幕的宽度
    public static int screenHeight;//手机屏幕的高度
    private boolean isFirst = true;//第一次点击消息页

    public static NotificationManager notiManager;//通知栏管理
    private int notifyID=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        RxBus.get().register(this);
        ARouter.getInstance().inject(this);

        //设置推送别名
        JPushInterface.setAlias(this,++notifyID,String.valueOf(SharedPreferencesUtil.getInstance().getUserId().intValue()));
        //设置静默时间段
        JPushInterface.setSilenceTime(getApplicationContext(),1,1,23,59);

        //提醒模式
        MyCongfig.remindMode = SharedPreferencesUtil.getInstance().getUserSetting();
        //通知管理
        notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //获取用户信息
        mPersenter.getUserInfo(SharedPreferencesUtil.getInstance().getUserId(),SharedPreferencesUtil.getInstance().getUserType());
//        if(isLogin){//通过登陆页面进入主页面时，获取登出时发送失败的推送
            mPersenter.getPushErrorReadMsg(SharedPreferencesUtil.getInstance().getUserId());
//        }
        //获取手机宽高
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWith = dm.widthPixels;
        Log.d("myWidth", "" + screenWith + "  " + screenHeight);
        needSplash(splashMessage);
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
                .subscribe(obj-> mPersenter.getPushErrorReadMsg(SharedPreferencesUtil.getInstance().getUserId()));
    }

    @Override
    public UserInfoPresent getPersenter() {
        return new UserInfoPresent(this);
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
    /**
     * 获取推送失败和未读的消息列表成功
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_PUSH_ERROR_MSG)
    }, thread = EventThread.MAIN_THREAD)
    public void getPushErrorMsg(ArrayList<MyMessage> data) {
        for(MyMessage myMessage:data){
            if((myMessage.getPushResult()==2)||(myMessage.getPushResult()==0&&myMessage.getMessageType()==1
                    &&(myMessage.getMessageExtend2()==0||myMessage.getMessageExtend2()==1))){//推送失败和未读的请假结果消息
                if(myMessage.getMessageType()==2){//新的请假请求
                    myMessage.setMessageType((short)10);
                }
                hasNotify(myMessage);
            }else if(myMessage.getPushResult()==0){//未读消息
                setRed(true);
            }

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
        if(intent!=null) {
            //点击通知进入主页进行的操作
            needSplash(intent.getStringExtra("message"));
            singleLogout( intent.getBooleanExtra(getString(R.string.exit_en), false));
        }
    }

    //是否需要跳转页页面
    private void needSplash(String message) {
        if(message==null)
            return;
        MyMessage myMessage = null;
        if(message.equals("showMessage")){//跳转到消息也
            isFirst = false;
             mTabbar.setSelectTab(0);
            contactFragment.freshMsgFragment();
        }
        else if(message.contains("{")){
            myMessage= new Gson().fromJson(message, new TypeToken<MyMessage>() {
            }.getType());
        }else
            startActivity(setResultIntent(myMessage));
    }

    /**
     * 退出登录
     */
    @Subscribe(tags = {
            @Tag("exitActivity")
    }, thread = EventThread.MAIN_THREAD)
    public void singleLogout(Boolean isExit) {
        if (isExit) {

            SharedPreferencesUtil.getInstance().clearUser();
            //解绑当前用户
            JPushInterface.deleteAlias(this,++notifyID);

            ARouter.getInstance().build(ARouterConstants.LoginAcitvity).navigation(this, new NavCallback() {
                @Override
                public void onArrival(Postcard postcard) {
                    MainActivity.this.finish();
                }
            });
        }
    }

    /**
     * 添加消息
     */
    @Subscribe(tags = {
            @Tag("addMessage")
    }, thread = EventThread.MAIN_THREAD)
    public void addMessage(MyMessage message) {
        if(currentPosition==0&&contactFragment.getCurrentitem()==0){//在消息页面
            if(message!=null) {
                contactFragment.getMsgFragment().addMsg(message);

            }
        } else {//显示红点
            MainApplication.getInstances().setHasNewMessage(true);
            mTabbar.getTabAtPosition(0).showCirclePointBadge();
        }
    }

    //消息页红点
    @Subscribe(
            tags = {@Tag("setRed")},
            thread = EventThread.MAIN_THREAD
    )
    public void setRed(Boolean isRed){
        MainApplication.getInstances().setHasNewMessage(isRed);
        if(isRed) {
            mTabbar.getTabAtPosition(0).showCirclePointBadge();
        }
        else {
//                notiManager.cancelAll();
                mTabbar.getTabAtPosition(0).hiddenBadge();
        }
    }

    //显示通知
    @Subscribe(tags = {
            @Tag("hasNotify")
    }, thread = EventThread.MAIN_THREAD)
    public void hasNotify(MyMessage myMessage) {
        MyCongfig.checkMode(this);
        addMessage(myMessage);
        NotificationCompat.Builder mBuilder;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            NotificationChannel channel;
            channel = new NotificationChannel("com.last.design.dmportal", "jpChannel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setSound(null,null);
            //channel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
            notiManager.createNotificationChannel(channel);
            mBuilder = new NotificationCompat.Builder(this,"com.last.design.dmportal");
        }else {
            mBuilder=new NotificationCompat.Builder(this);
        }

        String title="aa";//标题
        String content="bb";//内容
        Intent intent = setResultIntent(myMessage);
        PendingIntent pendingIntent =  PendingIntent.getActivity(this, 0,  intent, PendingIntent.FLAG_UPDATE_CURRENT) ;
        if(myMessage!=null){
            if(myMessage.getMessageType()==1) {//请假人审批结果
                title = getString(R.string.approval_result);
                content = getString(R.string.approval_result_tip_1);
            }else if(myMessage.getMessageType()==2) {//新的请假请求，跳转到详细页面
                title = getString(R.string.leave_request);
                content = getString(R.string.leave_request_tip_1);
            }else if(myMessage.getMessageType()==10){//新的请假请求，跳转到列表页面
                title = getString(R.string.leave_request);
                content = getString(R.string.leave_request_tip_2);
            }

        }
        Notification notification = mBuilder
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.app_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.app_icon))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(null)
                .setLights(Color.GREEN, 1000, 1000)
                .build();

        notiManager.notify(++notifyID,notification );
    }

    //设置跳转的intent
    public Intent setResultIntent(MyMessage myMessage) {
        Intent intent = null;
        if(myMessage==null) {//跳转到消息页
            intent=new Intent(this, MainActivity.class);
            intent.putExtra("message","showMessage");
            return intent;
        }

        if(myMessage.getMessageType()==1){//请假人请假结果
            intent = new Intent(this, LeaveInfoDetailActivity.class);
            intent.putExtra("leaveId",myMessage.getMessageExtend());
        }else if(myMessage.getMessageType()==2){//审批人新的审批任务，跳转到详情页
            MyMessage temp = contactFragment.getMsgAdapter().getMessage(myMessage);
            intent = new Intent(this, ApprovalLeaveInfoActivity.class);
            if(temp!=null) {
                intent.putExtra("leaveId", temp.getMessageExtend());
                intent.putExtra("type", temp.getMessageExtend2());
                intent.putExtra("approvalDate", temp.getSendTime());
                intent.putExtra("approvalContent", temp.getMessageContent());
            }else {
                intent.putExtra("leaveId", myMessage.getMessageExtend());
                intent.putExtra("type", 4);
            }
        }else if(myMessage.getMessageType()==10){//推送失败时收到的请假请求消息 跳转到审批列表页
            intent = new Intent(this, LeaveListActivity.class);
            intent.putExtra("userId",(int) SharedPreferencesUtil.getInstance().getUserId());
            intent.putExtra("type",1);
            intent.putExtra("isAll",true);
            intent.putExtra("isSearch",false);
            intent.putExtra("ARouterPath",ARouterConstants.ApprovalLeaveInfoActivity);

        }
        if(intent == null) {//跳转到消息页
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("message","showMessage");
        }
        return intent;
    }


}
