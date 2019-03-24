package com.newage.letstalk.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.newage.letstalk.Groupchat;
import com.newage.letstalk.R;
import com.newage.letstalk.adapter.GroupListAdapter;
import com.newage.letstalk.model.Group;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by Newage_android on 5/3/2018.
 */

public class ContactTabFragment extends Fragment implements GroupListAdapter.GroupClickListener {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Group> groups = new ArrayList<>();
        groups.add(new Group(R.drawable.group, "Religion Group"));
        groups.add(new Group(R.drawable.group, "Family Group"));
        groups.add(new Group(R.drawable.group, "Friend Group"));
        groups.add(new Group(R.drawable.group, "Business Group"));

        final GroupListAdapter mAdapter = new GroupListAdapter(this);
        final RecyclerView recyclerView = view.findViewById(R.id.groups);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        mAdapter.swapItems(groups);
    }

    @Override
    public void onGroupClick(Group group) {
        //Intent mIntent = new Intent(getContext(), Groupchat.class);
        //mIntent.putExtra("group", group);
        //Objects.requireNonNull(getActivity()).startActivity(mIntent);
    }

}