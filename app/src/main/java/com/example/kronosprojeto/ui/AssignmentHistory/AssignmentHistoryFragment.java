package com.example.kronosprojeto.ui.AssignmentHistory;

import android.os.Bundle;

import com.example.kronosprojeto.adapter.AssignmentHistoryAdapter;
import com.example.kronosprojeto.databinding.FragmentAssignmentHistoryBinding;
import com.example.kronosprojeto.databinding.FragmentNotificationsBinding;


import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kronosprojeto.R;
import com.example.kronosprojeto.adapter.NotificationAdapter;
import com.example.kronosprojeto.databinding.FragmentNotificationsBinding;
import com.example.kronosprojeto.model.AssignmentHistory;
import com.example.kronosprojeto.model.Notification;

import java.util.ArrayList;
import java.util.List;

public class AssignmentHistoryFragment extends Fragment {
    private FragmentAssignmentHistoryBinding binding;

    public AssignmentHistoryFragment() {
    }

    public static AssignmentHistoryFragment newInstance(String param1, String param2) {
        AssignmentHistoryFragment fragment = new AssignmentHistoryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAssignmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        List<AssignmentHistory> item = new ArrayList<>();
        item.add(new AssignmentHistory("#002 Realocação 18/05/2025", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, :", "Por: User2"));
        item.add(new AssignmentHistory("#003 Realocação 18/05/2025", "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum has been the industry's standard dummy text ever since the 1500s, :", "Por: User2"));

        RecyclerView recyclerView = binding.assignmentHistoryItem;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new AssignmentHistoryAdapter(getContext(), item));

        return root;

    }


}