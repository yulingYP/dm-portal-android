package com.definesys.base;

import android.content.Context;

import java.io.Serializable;

/**
 * Created by apple on 2016/10/23.
 */
public abstract class BasePresenter implements Serializable{


    protected Context mContext ;

    private BasePresenter(){
    }

    public BasePresenter(Context context) {
        this();
        mContext = context ;
    }

    public void subscribe() {}

    public void unsubscribe() {
    }



}
