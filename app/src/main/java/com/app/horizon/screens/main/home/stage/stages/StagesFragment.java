package com.app.horizon.screens.main.home.stage.stages;


import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.app.horizon.R;
import com.app.horizon.core.base.BaseFragment;
import com.app.horizon.databinding.FragmentStagesBinding;
import com.app.horizon.screens.main.home.stage.questions.QuestionFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 */
public class StagesFragment extends BaseFragment<StagesViewModel>{

    public static final String TAG = StagesFragment.class.getSimpleName();

    FragmentStagesBinding binding;
    StagesFragmentAdapter adapter;
    RecyclerView recyclerView;
    List<Integer> totalPage = new ArrayList<>();
    String categoryId;
    @Inject
    ViewModelProvider.Factory factory;
    private StagesViewModel viewModel;

    public StagesFragment() {
        // Required empty public constructor
    }

    public static StagesFragment newInstance(){
        StagesFragment fragment = new StagesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public StagesViewModel getViewModel() {
        viewModel = ViewModelProviders.of(this, factory).get(StagesViewModel.class);
        return viewModel;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_stages, container,
                false);
        View view = binding.getRoot();
        binding.setClick(new MyHandler());

        //Get intent extras
        categoryId = getArguments().getString("CategoryId");

        //Clear the adapter to avoid duplicates
        totalPage.clear();

        //Initialize the recyclerview
        initRecyclerView();

        //Call the showStage method
        showStage(categoryId);
        return view;
    }

    private void initRecyclerView(){
        adapter = new StagesFragmentAdapter(getActivity(), totalPage, listener);
        recyclerView = binding.stageView;
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }


    /**
     * Fetches stages of category
     */
    public void showStage(String categoryId){
        viewModel.getStage(categoryId).observe(getViewLifecycleOwner(), response -> {
            int page = response.getPaging().getTotalPages().intValue();
            for(int i = 1; i <= page; i++){
                totalPage.add(i);
            }
            adapter.updateStages(totalPage);
        });

    }

    public View.OnClickListener listener = view -> {

        Fragment fragment = new QuestionFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        /*Bundle args = new Bundle();
        args.putString("CategoryId", value);
        fragment.setArguments(args);*/
        transaction.replace(R.id.fragment_container, fragment)
                .addToBackStack("dialog")
                .commit();
    };

    public class MyHandler{
        public void onButtonClick(View view) {
            getActivity().finish();
        }
    }


}