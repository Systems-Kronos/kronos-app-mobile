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
import com.example.kronosprojeto.adapter.TarefaAdapter;
import com.example.kronosprojeto.databinding.FragmentNotificationsBinding;
import com.example.kronosprojeto.databinding.FragmentProfileBinding;
import com.example.kronosprojeto.model.Tarefa;

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
        // Inflate the layout for this fragment

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ImageView profileImg = binding.profileImg;

        Glide.with(this)
                .load(R.drawable.profile_mock)
                .circleCrop()
                .into(profileImg);


        List<Tarefa> tarefas = new ArrayList<>();
        tarefas.add(new Tarefa("Matar boi", new Date(), 3, "Matadouro", "boi"));
        tarefas.add(new Tarefa("Matar boi", new Date(), 3, "Frigorífico", "boi"));
        tarefas.add(new Tarefa("Matar boi", new Date(), 3, "Administração", "boi"));
        tarefas.add(new Tarefa("Matar boi", new Date(), 3, "Administração", "boi"));
        tarefas.add(new Tarefa("Matar boi", new Date(), 3, "Administração", "boi"));
        tarefas.add(new Tarefa("Matar boi", new Date(), 3, "Administração", "boi"));

        RecyclerView recyclerView = binding.userTasks;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new TarefaAdapter(getContext(), tarefas));

        return root;
    }
}