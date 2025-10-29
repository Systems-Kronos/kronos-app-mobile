package com.example.kronosprojeto.ui.Notifications;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.kronosprojeto.R;
import com.example.kronosprojeto.adapter.NotificationAdapter;
import com.example.kronosprojeto.databinding.FragmentNotificationsBinding;

import com.example.kronosprojeto.viewmodel.NotificationViewModel;
import com.google.android.flexbox.FlexboxLayout;


import java.util.ArrayList;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private NotificationAdapter adapter;
    private NotificationViewModel notificationViewModel;
    FlexboxLayout noContentFlex;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        noContentFlex = binding.noContentNotifications;

        RecyclerView recyclerView = binding.recyclerviewNotifications;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificationAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        notificationViewModel = new ViewModelProvider(requireActivity()).get(NotificationViewModel.class);

        noContentFlex.setVisibility(View.VISIBLE);

        ImageView imgBack = root.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.HomeFragment);
        });

        notificationViewModel.getNotifications().observe(getViewLifecycleOwner(), notifications -> {
            if (notifications.isEmpty()){
                Log.e("NOTIFICATIONS", "EMPTY");
            }else {
                noContentFlex.setVisibility(View.GONE);
                Log.e("NOTIFICATIONS", "content");
            }
            adapter.updateList(notifications);
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
