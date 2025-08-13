package com.example.kronosprojeto.ui.Notifications;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kronosprojeto.adapter.NotificationAdapter;
import com.example.kronosprojeto.databinding.FragmentNotificationsBinding;
import com.example.kronosprojeto.model.Notification;


import java.util.ArrayList;
import java.util.List;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

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

        List<Notification> notifications = new ArrayList<>();
        notifications.add(new Notification("Uma tarefa foi realocada para você", "A tarefa “Separar embalagens por cor” de User2 foi realocada para você"));
        notifications.add(new Notification("Uma tarefa foi realocada para você", "A tarefa “Separar embalagens por cor” de User2 foi realocada para você"));
        notifications.add(new Notification("Uma tarefa foi realocada para você", "A tarefa “Separar embalagens por cor” de User2 foi realocada para você"));
        notifications.add(new Notification("Uma tarefa foi realocada para você", "A tarefa “Separar embalagens por cor” de User2 foi realocada para você"));
        notifications.add(new Notification("Uma tarefa foi realocada para você", "A tarefa “Separar embalagens por cor” de User2 foi realocada para você"));
        notifications.add(new Notification("Uma tarefa foi realocada para você", "A tarefa “Separar embalagens por cor” de User2 foi realocada para você"));
        notifications.add(new Notification("Uma tarefa foi realocada para você", "A tarefa “Separar embalagens por cor” de User2 foi realocada para você"));
        notifications.add(new Notification("Uma tarefa foi realocada para você", "A tarefa “Separar embalagens por cor” de User2 foi realocada para você"));
        notifications.add(new Notification("Uma tarefa foi realocada para você", "A tarefa “Separar embalagens por cor” de User2 foi realocada para você"));

        RecyclerView recyclerView = binding.recyclerviewNotifications;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new NotificationAdapter(getContext(), notifications));

        return root;
    }

    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}