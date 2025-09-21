package com.example.kronosprojeto.ui.Profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.kronosprojeto.R;
import com.example.kronosprojeto.adapter.TaskAdapter;
import com.example.kronosprojeto.databinding.FragmentProfileBinding;
import com.example.kronosprojeto.model.Task;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;

    public ProfileFragment() {
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageView profileImg = binding.profileImg;

        Glide.with(this)
                .load(R.drawable.profile_mock)
                .circleCrop()
                .into(profileImg);


        List<Task> tarefas = new ArrayList<>();
        tarefas.add(new Task("Matar boi", new Date(), 3, "Matadouro", "boi", new Date()));
        tarefas.add(new Task("Matar boi", new Date(), 3, "Frigorífico", "boi", new Date()));
        tarefas.add(new Task("Matar boi", new Date(), 3, "Administração", "boi", new Date()));
        tarefas.add(new Task("Matar boi", new Date(), 3, "Administração", "boi", new Date()));
        tarefas.add(new Task("Matar boi", new Date(), 3, "Administração", "boi", new Date()));
        tarefas.add(new Task("Matar boi", new Date(), 3, "Administração", "boi", new Date()));

        RecyclerView recyclerView = binding.userTasks;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new TaskAdapter(getContext(), tarefas, "profile"));


        return root;
    }
}