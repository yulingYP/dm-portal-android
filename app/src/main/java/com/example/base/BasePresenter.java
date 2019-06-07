package com.example.base;

import android.content.Context;

import java.io.Serializable;


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
