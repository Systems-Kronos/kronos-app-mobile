package com.example.kronosprojeto.ui.Home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kronosprojeto.databinding.FragmentHomeBinding;
import com.example.kronosprojeto.ui.Adapter.TarefaAdapter;
import com.example.kronosprojeto.ui.Model.Tarefa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        List<Tarefa> tarefas = new ArrayList<>();
        tarefas.add(new Tarefa("Matar boi", new Date(), 3, "Matadouro", "boi"));
        tarefas.add(new Tarefa("Matar boi", new Date(), 3, "Matadouro", "boi"));
        tarefas.add(new Tarefa("Matar boi", new Date(), 3, "Matadouro", "boi"));

        RecyclerView recyclerView = binding.recyclerview;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new TarefaAdapter(getContext(), tarefas));


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
