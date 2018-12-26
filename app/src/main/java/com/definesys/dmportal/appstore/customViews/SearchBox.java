package com.definesys.dmportal.appstore.customViews;

/**
 * Created by 羽翎 on 2018/8/24.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.utils.Constants;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.functions.Consumer;

public class SearchBox extends LinearLayout{
    @BindView(R.id.et_search) EditText editText;
    @BindView(R.id.clear_search) ImageView clear;
    @BindView(R.id.search_search) ImageView search;

    public SearchBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.search_box,this);
        ButterKnife.bind(this);
        RxView.clicks(search)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        editText.requestFocus();
                        editText.requestFocusFromTouch();
                    }
                });


        RxTextView.textChanges(editText).subscribe(new Consumer<CharSequence>() {
            @Override
            public void accept(CharSequence charSequence) throws Exception {
                if(editText.getText().toString().equals(""))
                    clear.setVisibility(View.GONE);
                else
                    clear.setVisibility(View.VISIBLE);
            }
        });

        RxView.clicks(clear)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        editText.setText("");
                    }
                });
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener editorActionListener){
        editText.setOnEditorActionListener(editorActionListener);
    }
}

