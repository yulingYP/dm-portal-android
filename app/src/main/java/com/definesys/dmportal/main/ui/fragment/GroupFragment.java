package com.definesys.dmportal.main.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.definesys.dmportal.R;
import com.definesys.dmportal.appstore.adapter.TypeListAdapter;
import com.definesys.dmportal.main.adapter.GruopInfoRecycleViewAdapter;
import com.definesys.dmportal.main.bean.GroupInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GroupFragment} interface
 * to handle interaction events.
 * Use the {@link GroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GroupFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Unbinder unbinder;


    //种类列表
    @BindView(R.id.type_view)
    RecyclerView tyepListView;
    private List<String> typeList;
    private TypeListAdapter typeListAdapter;

    //种类列表
    @BindView(R.id.group_view)
    RecyclerView groupListView;
    private List<GroupInfo> groupList;
    private GruopInfoRecycleViewAdapter gruopInfoRecycleViewAdapter;

    public GroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GroupFragment newInstance(String param1, String param2) {
        GroupFragment fragment = new GroupFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        View view=inflater.inflate(R.layout.fragment_group, container, false);
        unbinder = ButterKnife.bind(this, view);
        initTpyeList();
        initGroupList();
        return view;
    }

    private void initGroupList() {
        groupList = new ArrayList<>();
        groupList.add(new GroupInfo("篮球爱好者协会","运动","描述.....",String.valueOf(R.drawable.x11)));
        groupList.add(new GroupInfo("影像协会","摄影","描述....",String.valueOf(R.drawable.x11)));
        groupList.add(new GroupInfo("威软实验室","实验室、编程","描述....",String.valueOf(R.drawable.x11)));
        groupList.add(new GroupInfo("冰峰实验室","实验室、编程、Android\\IOS","描述....",String.valueOf(R.drawable.x11)));
        gruopInfoRecycleViewAdapter = new GruopInfoRecycleViewAdapter(groupList,getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        groupListView.setLayoutManager(layoutManager);
        groupListView.setAdapter(gruopInfoRecycleViewAdapter);
    }

    private void initTpyeList() {
            typeList = new ArrayList<>();
            typeList.add("全部");
            typeList.add("音乐");
            typeList.add("摄影");
            typeList.add("舞蹈");
            typeList.add("运动");
            typeList.add("动漫");
            typeList.add("实验室");
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
            typeListAdapter = new TypeListAdapter(typeList,R.layout.item_type_list);
            tyepListView.setLayoutManager(staggeredGridLayoutManager);
            tyepListView.setAdapter(typeListAdapter);
            ((SimpleItemAnimator) tyepListView.getItemAnimator()).setSupportsChangeAnimations(false);
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
