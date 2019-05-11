package com.definesys.dmportal.main;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.StrictMode;
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
import com.definesys.dmportal.BuildConfig;
import com.definesys.dmportal.MyActivityManager;
import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.AppLyListActivity;
import com.definesys.dmportal.appstore.ApplyInfoActivity;
import com.definesys.dmportal.appstore.ApprovalApplyInfoActivity;
import com.definesys.dmportal.appstore.ApprovalLeaveInfoActivity;
import com.definesys.dmportal.appstore.LeaveInfoDetailActivity;
import com.definesys.dmportal.appstore.LeaveListActivity;
import com.definesys.dmportal.appstore.bean.MyMessage;
import com.definesys.dmportal.appstore.customViews.CustomTitleIndicator;
import com.definesys.dmportal.appstore.customViews.NoScrollViewPager;
import com.definesys.dmportal.appstore.leaveSettingUI.AuthoritySettingActivity;
import com.definesys.dmportal.appstore.utils.ARouterConstants;
import com.definesys.dmportal.appstore.utils.DensityUtil;
import com.definesys.dmportal.commontitlebar.CustomTitleBar;
import com.definesys.dmportal.config.MyCongfig;
import com.definesys.dmportal.main.presenter.MainPresenter;
import com.definesys.dmportal.main.presenter.UserInfoPresent;
import com.definesys.dmportal.main.ui.fragment.ContactFragment;
import com.definesys.dmportal.appstore.ui.fragment.HomeAppFragment;
import com.definesys.dmportal.main.ui.fragment.MyFragment;
import com.definesys.dmportal.main.util.SharedPreferencesUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import cn.jpush.android.api.JPushInterface;

@Route(path = ARouterConstants.MainActivity)
public class MainActivity extends BaseActivity<UserInfoPresent> {

    @BindView(R.id.mTitlebar)
    CustomTitleBar mTitlebar ;

    @BindView(R.id.mTabbar)
    JPTabBar mTabbar ;

    @BindView(R.id.mViewPager)
    NoScrollViewPager mViewPager ;

    @Autowired(name = "message")
    String splashMessage;//从跳转页得到的消息，可能是json化的Message实例

    private CustomTitleIndicator titleIndicator;
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
        ARouter.getInstance().inject(this);
        initApp();
        //设置推送别名
        JPushInterface.setAlias(this,++notifyID,String.valueOf(SharedPreferencesUtil.getInstance().getUserId().intValue()));
        //设置静默时间段
        JPushInterface.setSilenceTime(getApplicationContext(),1,1,23,59);

        //提醒模式
        MyCongfig.remindMode = SharedPreferencesUtil.getInstance().getUserSetting();
        //通知管理
        notiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //获取用户信息
        mPersenter.getUserInfo(SharedPreferencesUtil.getInstance().getUserId());
        //获取推送失败和未读的消息
        mPersenter.getPushErrorReadMsg(SharedPreferencesUtil.getInstance().getUserId());

        //获取手机宽高
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWith = dm.widthPixels;
        Log.d("myWidth", "" + screenWith + "  " + screenHeight);
        needSplash(splashMessage);
        initView();
    }

    private void initView() {
        mTitlebar.setTitle(R.string.tab2);
        mTitlebar.setBackgroundDividerEnabled(false);
        mTitlebar.setBackground(getResources().getDrawable(R.drawable.title_bg));

        mTitlebar.showTitleView(true);
        mTabbar.setTitles( R.string.tab1, R.string.tab2, R.string.tab3)
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
                if(position==0&& (mTabbar.getTabAtPosition(position).isBadgeShow()||isFirst)){
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
        titleIndicator.setOnTitleClickListener(position -> {
            titleIndicator.setFocus(position);
            contactFragment.getmViewpager().setCurrentItem(position);
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
                    if(position==1)mTitlebar.setTitle(R.string.tab2);
                    else if(position==2)mTitlebar.setTitle(R.string.tab3);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        RxView.clicks(mTitlebar)
                .subscribe(obj-> mPersenter.getUserInfo(SharedPreferencesUtil.getInstance().getUserId()));
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
     * @param str s
     */
    @Subscribe(tags = {
            @Tag(MainPresenter.SUCCESSFUL_GET_USER_INFO)
    }, thread = EventThread.MAIN_THREAD)
    public void getUserUrl(String str) {
        JPushInterface.setAlias(this,++notifyID,String.valueOf(SharedPreferencesUtil.getInstance().getUserId().intValue()));
       if( MyActivityManager.getInstance().getCurrentActivity()==this&&myFragment!=null) {
//           SharedPreferencesUtil.getInstance().setUserLocal("");
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
            if((myMessage.getPushResult()==2)||
                    (myMessage.getPushResult()==0&&(myMessage.getMessageType()==1||myMessage.getMessageType()==4)
                    &&(myMessage.getMessageExtend2()==0||myMessage.getMessageExtend2()==1))){//推送失败和未读的请假、权限申请结果消息
                if(myMessage.getMessageType()==2&&myMessage.getMessageExtend2()==4){//新的请假请求
                    myMessage.setMessageType((short)10);
                }else if(myMessage.getMessageType()==5&&myMessage.getMessageExtend2()==4){
                    myMessage.setMessageType((short)11);
                }
                hasNotify(myMessage);
            }
            else if(myMessage.getPushResult()==0&&
                    ((myMessage.getMessageType()==2||myMessage.getMessageType()==5)&&myMessage.getMessageExtend2()==4)//未读的审批请求消息
                    ||myMessage.getMessageType()==6){//未读的权限修改信息
                setRed(true);
            }

        }
    }
    class MainFragmentPagerAdapter extends FragmentPagerAdapter {

        //存储所有的fragment
        private List<Fragment> fragmentList;
        private MainFragmentPagerAdapter(FragmentManager fm, List<Fragment> list) {
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
            if(contactFragment!=null){
                contactFragment.freshMsgFragment();
            }
        }
        else if(message.contains("{")){
            myMessage= new Gson().fromJson(message, new TypeToken<MyMessage>() {
            }.getType());
        }
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
            notiManager.cancelAll();
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
        }
        else {//显示红点
            if(message!=null&&(//有消息
              (message.getMessageType()==1&&message.getMessageExtend2()<=1)||//请假申请被批准或者被拒绝的消息
              (message.getMessageType()==2&&message.getMessageExtend2()==4)|| //请假审批人收到审批消息
              (message.getMessageType()==4&&message.getMessageExtend2()<=1)||//权限申请结果
              (message.getMessageType()==5&&message.getMessageExtend2()==4)||//新的权限申请请求
               message.getMessageType()==6||//权限发生变化（部分权限被删除）
               message.getMessageType()==10||message.getMessageType()==11//新的权限申请请求
            ))
            setRed(true);
        }
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
            if(currentPosition==0&&contactFragment.getCurrentitem()==0)//在消息页面
                mTabbar.getTabAtPosition(0).hiddenBadge();
        }
    }

    //当前时间
    private long currentTime=0;
    private int msgType = 0;
    //显示通知
    @Subscribe(tags = {
            @Tag("hasNotify")
    }, thread = EventThread.MAIN_THREAD)
    public void hasNotify(MyMessage myMessage) {
        if(System.currentTimeMillis()-currentTime>=1000*5||(myMessage!=null&&myMessage.getMessageType()!=msgType)) {//信息特别多时 在5秒内获得的相同类型的信息只提示1次
            MyCongfig.checkMode(this);
            currentTime = System.currentTimeMillis();
            msgType=myMessage.getMessageType();
        }
        addMessage(myMessage);//添加消息

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

        //获取跳转的intent
        Intent intent = setResultIntent(myMessage);
        //点击通知的pengdingIntent
        PendingIntent pendingIntentClick =  PendingIntent.getActivity(this, ++notifyID,  intent, PendingIntent.FLAG_UPDATE_CURRENT) ;

        if(myMessage!=null){
            if(myMessage.getMessageType()==1) {//请假人审批结果
                title = getString(R.string.approval_result);
                if(myMessage.getMessageExtend2()==0)content = getString(R.string.approval_result_tip_3);//拒绝
                else if(myMessage.getMessageExtend2()==1)content = getString(R.string.approval_result_tip_2);//同意
                else if(myMessage.getMessageExtend2()==2)content = getString(R.string.approval_result_tip_1);//未审批
                else if(myMessage.getMessageExtend2()==3){//销假
                    title = getString(R.string.approval_result_2);
                    content = getString(R.string.approval_result_tip_4);
                }
            }else if(myMessage.getMessageType()==2) {//新的请假请求，跳转到详细页面
                title = getString(R.string.leave_request);
                content = getString(R.string.leave_request_tip_1);
            }else if(myMessage.getMessageType()==4) {//新的请假请求，跳转到详细页面
                title = getString(R.string.approval_result_3);
                if(myMessage.getMessageExtend2()==0)content = getString(R.string.approval_result_tip_5);//拒绝
                else if(myMessage.getMessageExtend2()==1)content = getString(R.string.approval_result_tip_6);//同意
            }else if(myMessage.getMessageType()==5) {//新的请假请求，跳转到详细页面
                title = getString(R.string.approval_result_4);
                content = getString(R.string.approval_result_tip_7);
            }else if(myMessage.getMessageType()==6){
                title = getString(R.string.approval_result_5);
                content = "delete".equals(myMessage.getMessageContent().toLowerCase())?getString(R.string.approval_result_tip_8, DensityUtil.getAuthorityName(this,myMessage.getMessageExtend2())):getString(R.string.approval_result_tip_9, DensityUtil.getAuthorityName(this,myMessage.getMessageExtend2()));
            }
            else if(myMessage.getMessageType()==10){//新的请假请求，跳转到列表页面
                title = getString(R.string.leave_request);
                content = getString(R.string.leave_request_tip_2);
            }else if(myMessage.getMessageType()==11){//新的权限申请请求，跳转到列表页面
                title = getString(R.string.approval_result_4);
                content = getString(R.string.leave_request_tip_3);
            }
//            title = myMessage.getMessageId();
        }
        Notification notification = mBuilder
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.mipmap.app_icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.app_icon))
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentIntent(pendingIntentClick)
                .setDefaults(NotificationCompat.FLAG_ONLY_ALERT_ONCE)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setSound(null)
                .setLights(Color.GREEN, 1000, 1000)
                .build();

        notiManager.notify(notifyID,notification );
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
            intent.putExtra("leaveId",Long.parseLong(myMessage.getMessageExtend()));
        }else if(myMessage.getMessageType()==2){//审批人新的审批任务，跳转到详情页
            String infoId = myMessage.getMessageExtend();
            infoId = infoId.length()>9?infoId.substring(9):infoId;
            intent = new Intent(this, ApprovalLeaveInfoActivity.class);
            intent.putExtra("leaveId", Long.parseLong(infoId));
            intent.putExtra("msgId",myMessage.getMessageId());
            intent.putExtra("type", myMessage.getMessageExtend2().intValue());
        }else if(myMessage.getMessageType()==4){//申请人申请结果
            intent = new Intent(this, ApplyInfoActivity.class);
            intent.putExtra("applyId", myMessage.getMessageExtend());
            if(myMessage.getMessageExtend2()==1)//权限申请通过，重新获取权限信息
                mPersenter.getUserInfo(SharedPreferencesUtil.getInstance().getUserId());
        }else if(myMessage.getMessageType()==5){//审批人新的审批任务，跳转到详情页
            intent = new Intent(this, ApprovalApplyInfoActivity.class);
            intent.putExtra("applyId", myMessage.getMessageExtend());
            intent.putExtra("type", myMessage.getMessageExtend2().intValue());
        }else if(myMessage.getMessageType()==6){////权限发生变化
            intent = new Intent(this, "".equals(myMessage.getMessageExtend())?AuthoritySettingActivity.class:ApplyInfoActivity.class);
            intent.putExtra("applyId",myMessage.getMessageExtend());
            intent.putExtra("isMsg",true);
            if("delete".equals(myMessage.getMessageContent().toLowerCase()))//权限已被删除，重新获取权限
                mPersenter.getUserInfo(SharedPreferencesUtil.getInstance().getUserId());
            else if("change".equals(myMessage.getMessageContent().toLowerCase())){//权限已被修改，但用户没有修改后的权限
                short applyAut  = myMessage.getMessageExtend2();
                String aut = applyAut<10?""+SharedPreferencesUtil.getInstance().getApprpvalStudentAuthority():""+SharedPreferencesUtil.getInstance().getApprpvalTeacherAuthority();
                if(!aut.contains(""+applyAut%10))
                    mPersenter.getUserInfo(SharedPreferencesUtil.getInstance().getUserId());
            }
        } else if(myMessage.getMessageType()==10){//推送失败时收到的请假请求消息 跳转到审批列表页
            intent = new Intent(this, LeaveListActivity.class);
            intent.putExtra("userId",(int) SharedPreferencesUtil.getInstance().getUserId());
            intent.putExtra("type",1);
            intent.putExtra("isAll",true);
            intent.putExtra("isSearch",false);
            intent.putExtra("ARouterPath",ARouterConstants.ApprovalLeaveInfoActivity);
        }else if(myMessage.getMessageType()==11){//推送失败时收到的权限申请请求 跳转到权限审批列表页
            intent = new Intent(this, AppLyListActivity.class);
            intent.putExtra("ARouterPath",ARouterConstants.ApprovalApplyInfoActivity);
            intent.putExtra("isAll",true);
            intent.putExtra("isSearch",false);
            intent.putExtra("type",0);
        }
        if(intent == null) {//跳转到消息页
            intent = new Intent(this, MainActivity.class);
            intent.putExtra("message","showMessage");
        }
        return intent;
    }

    //app功能相关的初始化
    private void initApp() {
        JPushInterface.setDebugMode(true);
        if (BuildConfig.DEBUG) { //如果在debug模式下
            // 打印日志,默认关闭
            ARouter.openLog();
            // 开启调试模式，默认关闭(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
            ARouter.openDebug();
            // 打印日志的时候打印线程堆栈
            ARouter.printStackTrace();
        }
        //消除相机url异常
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }


}
