package com.definesys.dmportal.appstore.customViews;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

import com.definesys.dmportal.R;
import com.definesys.dmportal.main.interfaces.OnItemClickListener;

import java.util.List;

import static android.view.View.GONE;

/*
    TODO 待进一步封装成Dialog
 */
public class BottomDialog extends Dialog {

    private Context context;
    private List<String> stringList;
    private OnItemClickListener onOptionClickListener;
    private View.OnClickListener onCancelClickListener;

    public BottomDialog(Context context, @NonNull List<String> stringList) {
        super(context, R.style.BottomDialog);
        this.context = context;
        this.stringList = stringList;
    }

    private void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_dialog, null);
        LinearLayout layoutOptions = view.findViewById(R.id.layout_options_bottom_dialog);
        Button cancelButton = view.findViewById(R.id.cancel_bottom_dialog);

        cancelButton.setOnClickListener(onCancelClickListener);
        layoutOptions.removeAllViews();

        //TODO 待请教这个重复问题
        for (String s : stringList) {
            int index = stringList.indexOf(s);
            View optionView = LayoutInflater.from(context).inflate(R.layout.item_btn_bottom_dialog, null);
            if (index == 0) {
                optionView.findViewById(R.id.view_line_bottom_dialog).setVisibility(GONE);
            }
            Button option = optionView.findViewById(R.id.option_bottom_dialog);
            option.setText(s);
            if (onOptionClickListener != null)
                option.setOnClickListener(view1 ->
                        onOptionClickListener.onClick(index)   //这种写法要求数组内不能有重复字段
                );
            layoutOptions.addView(optionView);
        }

        this.setContentView(view);
        //获取当前Activity所在的窗体
        if (getWindow() == null) {
            return;
        }
        //设置Dialog从窗体底部弹出
        getWindow().setGravity(Gravity.BOTTOM);
        //设置铺满屏幕
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        initView();
    }

    public BottomDialog setOnOptionClickListener(OnItemClickListener onOptionClickListener) {
        this.onOptionClickListener = onOptionClickListener;
        return this;
    }

    public BottomDialog setOnCancelButtonClickListener(View.OnClickListener onCancelClickListener) {
        this.onCancelClickListener = onCancelClickListener;
        return this;
    }
}
