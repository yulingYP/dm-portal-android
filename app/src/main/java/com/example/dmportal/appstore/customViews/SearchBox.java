package com.example.dmportal.appstore.customViews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.dmportal.R;
import com.example.dmportal.appstore.utils.Constants;
import com.jakewharton.rxbinding2.view.RxView;
import com.jakewharton.rxbinding2.widget.RxTextView;
import java.util.concurrent.TimeUnit;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *
 * Created by 羽翎 on 2018/8/24.
 */

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
                .subscribe(o -> {
                    editText.requestFocus();
                    editText.requestFocusFromTouch();
                });


        RxTextView.textChanges(editText).subscribe(charSequence -> {
            if(editText.getText().toString().equals(""))
                clear.setVisibility(View.GONE);
            else
                clear.setVisibility(View.VISIBLE);
        });

        RxView.clicks(clear)
                .throttleFirst(Constants.clickdelay,TimeUnit.MILLISECONDS)
                .subscribe(o -> editText.setText(""));
    }

    public void setOnEditorActionListener(TextView.OnEditorActionListener editorActionListener){
        editText.setOnEditorActionListener(editorActionListener);
    }
}

