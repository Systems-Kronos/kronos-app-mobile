package com.example.kronosprojeto.ui.Notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kronosprojeto.adapter.NotificationAdapter;
import com.example.kronosprojeto.config.RetrofitCalenderNoSQL;
import com.example.kronosprojeto.databinding.FragmentNotificationsBinding;
import com.example.kronosprojeto.model.Notification;
import com.example.kronosprojeto.service.NotificationService;


import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    private NotificationService notificationService;
    public NotificationsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        notificationService = RetrofitCalenderNoSQL.createService(NotificationService.class);

        RecyclerView recyclerView = binding.recyclerviewNotifications;
        SharedPreferences prefs = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        String usuarioIdStr = prefs.getString("id", "0");
        Long usuarioId = Long.parseLong(usuarioIdStr);

        // Faz a chamada à API
        Call<List<Notification>> call = notificationService.getNotificationsByUserID(usuarioId);
        call.enqueue(new retrofit2.Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, retrofit2.Response<List<Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Notification> notifications = response.body();
                    NotificationAdapter adapter = new NotificationAdapter(getContext(), notifications);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                t.printStackTrace();
            }
        });

        return root;
    }

    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}