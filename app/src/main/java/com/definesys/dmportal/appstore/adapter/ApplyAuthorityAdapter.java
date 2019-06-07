package com.definesys.dmportal.appstore.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.utils.Constants;
import com.definesys.dmportal.main.interfaces.OnItemClickListener;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 *   type
 *   0.寝室长权限根据facultyId获取班级名称
 *   1.根据班级id获取班级名单
 *   2.班长权限 根据facultyId获取班级名称
 *   3.班主任权限 获取院系列表
 *   4.获取该院系所有班级的id
 *   5.毕设老师权限 获取所有院系的名称
 *   6.毕设老师权限 获取该院系所有班级的id
 *   7.获取班级全部成员
 *   8.辅导员权限 获取院系列表
 *   9.获取所有班级id
 *   10.学院实习工作负责人权限 获取院系列表
 *   11.学生工作负责人权限 获取院系列表
 *   12.教学院长权限 获取院系列表
 *   20.部门请假负责人权限 获取所有部门的id
 *   21.部门教学院长权限 获取所有部门的id
 *   100，101.提交提示框
 * Created by 羽翎 on 2019/2/28.
 */

public class ApplyAuthorityAdapter extends RecyclerView.Adapter<ApplyAuthorityAdapter.ViewHolder>{
    private List<String> typeList;
    private Context mContext;
    private int selectPosition;//选择的样式的位置
    private ImageView iv_selected;//选择的图标
    private int type;
    private OnItemClickListener onItemClickListener;
    private List<Boolean> selectList;
    public ApplyAuthorityAdapter(List<String> typeList, Context mContext,int type) {
        this.typeList = typeList;
        this.mContext = mContext;
        this.type = type;
        selectPosition=-1;
        if(type==1||type==4||type==7||type==9){
            selectList = new ArrayList<>();
            for(int i = 0 ; i <typeList.size();i++)
                selectList.add(i,false);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_sign_type_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tv_name.setText(typeList.get(position));
        if(type==100||type==101) {
            holder.iv_select.setVisibility(View.GONE);
            holder.tv_name.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        } else
            holder.iv_select.setVisibility(View.VISIBLE);
        if(type!=1&&type!=4&&type!=7&&type!=9) {//0/2班级、10/11部门
            if (selectPosition == position) {
                holder.iv_select.setImageResource(R.drawable.right_icon);
                iv_selected = holder.iv_select;
            } else {
                holder.iv_select.setImageResource(R.drawable.no_select);
            }
        }else {
            if (selectList.get(position)) {
                holder.iv_select.setImageResource(R.drawable.right_icon);
            } else {
                holder.iv_select.setImageResource(R.drawable.no_select);
            }
        }
        //点击事件
        RxView.clicks(holder.itemView)
                .throttleFirst(Constants.clickdelay, TimeUnit.MILLISECONDS)
                .subscribe(o -> {
                    if(type!=1&&type!=4&&type!=7&&type!=9) {
                        if (iv_selected != null)
                            iv_selected.setImageResource(R.drawable.no_select);
                        selectPosition = position;
                        holder.iv_select.setImageResource(R.drawable.right_icon);
                        iv_selected = holder.iv_select;
                    }else {
                        if (selectList.get(position)) {
                            holder.iv_select.setImageResource(R.drawable.no_select);
                            selectList.set(position,false);
                        } else {
                            holder.iv_select.setImageResource(R.drawable.right_icon);
                            selectList.set(position,true);
                        }
                    }
                    if (onItemClickListener != null)
                        onItemClickListener.onClick(position);


                });
    }

    @Override
    public int getItemCount() {
        return typeList==null?0:typeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.name_text)
        TextView tv_name;
        @BindView(R.id.select_img)
        ImageView iv_select;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }


    public int getSelectPosition() {
        return selectPosition;
    }



    public void setMyOnClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public List<Boolean> getSelectList() {
        return selectList;
    }


}
