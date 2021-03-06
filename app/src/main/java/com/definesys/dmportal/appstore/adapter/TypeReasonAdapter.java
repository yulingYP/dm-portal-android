package com.definesys.dmportal.appstore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.definesys.dmportal.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 羽翎 on 2018/11/21.
 */
//请假类型
public class TypeReasonAdapter extends BaseAdapter {
    List <String> list;
    Context mContext;


    public TypeReasonAdapter(List<String> list, Context mContext) {
        this.list = list;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        if(convertView == null)
        {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_type_reason,null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        }else
        {
            holder = (ViewHolder)convertView.getTag();
        }


        holder.tv_type.setText(list.get(position));


        return convertView;
    }

    public class ViewHolder{
        @BindView(R.id.type__item_text)
        TextView tv_type;

        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }
    }
}
