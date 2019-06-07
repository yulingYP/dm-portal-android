package com.example.base;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.hwangjr.rxbus.SmecRxBus;

/**
 *
 * Created by apple on 2018/12/4.
 */

public abstract class BaseFragment<T extends BasePresenter> extends Fragment {

    protected T mPersenter ;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPersenter = getPersenter();
        SmecRxBus.get().register(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        mPersenter.subscribe();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);}

    @Override
    public void onStop() {
        super.onStop();
        mPersenter.unsubscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SmecRxBus.get().unregister(this);
    }

    protected abstract T getPersenter();
}
