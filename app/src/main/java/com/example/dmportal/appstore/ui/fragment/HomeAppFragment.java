package com.example.dmportal.appstore.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.base.BaseFragment;
import com.example.dmportal.R;
import com.example.dmportal.appstore.adapter.MainIconAdapter;
import com.example.dmportal.appstore.bean.MainIcon;
import com.example.dmportal.appstore.presenter.HomeAppPresenter;
import com.example.dmportal.appstore.utils.ARouterConstants;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeAppFragment} interface
 * to handle interaction events.
 * Use the {@link HomeAppFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeAppFragment extends BaseFragment<HomeAppPresenter> {

    @BindView(R.id.recycle_view)
    RecyclerView recyclerView;

    private Unbinder unbinder;


    public HomeAppFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment HomeAppFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeAppFragment newInstance() {
        HomeAppFragment fragment = new HomeAppFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected HomeAppPresenter getPersenter() {
        return new HomeAppPresenter(this.getActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_page, container, false);
        unbinder = ButterKnife.bind(this, view);
        initList();
        return view;

    }

    private void initList() {
        List<MainIcon> mainIconList = new ArrayList<>();
        mainIconList.add(new MainIcon(getString(R.string.leave_off),R.drawable.leave_icon,ARouterConstants.LeaveMainActivity));
        mainIconList.add(new MainIcon(getString(R.string.subject_table),R.drawable.table_icon,ARouterConstants.SubjectTableActivity));
//        mainIconList.add(new MainIcon(getString(R.string.seat),R.drawable.group_icon,ARouterConstants.NoPath ));
        MainIconAdapter mainIconAdapter = new MainIconAdapter(getContext(), mainIconList, true, R.layout.item_main_icon);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(mainIconAdapter);

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
