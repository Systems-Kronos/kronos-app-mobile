package com.example.kronosprojeto.ui.Home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kronosprojeto.databinding.FragmentHomeBinding;
import com.example.kronosprojeto.ui.Home.HomeViewModel;
import com.example.kronosprojeto.ui.Home.Tarefa;
import com.example.kronosprojeto.ui.Home.TerefaAdapter;

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

        // Cria a lista de tarefas (você pode depois carregar do banco, etc.)
        List<Tarefa> tarefas = new ArrayList<>();
        tarefas.add(new Tarefa("Matar boi", new Date(), 3, "Matadouro", "boi"));
        tarefas.add(new Tarefa("Matar boi", new Date(), 3, "Matadouro", "boi"));
        tarefas.add(new Tarefa("Matar boi", new Date(), 3, "Matadouro", "boi"));
        tarefas.add(new Tarefa("Matar boi", new Date(), 3, "Matadouro", "boi"));
        tarefas.add(new Tarefa("Matar boi", new Date(), 3, "Matadouro", "boi"));
        tarefas.add(new Tarefa("Matar boi", new Date(), 3, "Matadouro", "boi"));
        tarefas.add(new Tarefa("Matar boi", new Date(), 3, "Matadouro", "boi"));
        tarefas.add(new Tarefa("Matar boi", new Date(), 3, "Matadouro", "boi"));
        tarefas.add(new Tarefa("Matar boi", new Date(), 3, "Matadouro", "boi"));

        // Aqui está o trecho que você perguntou:
        RecyclerView recyclerView = binding.recyclerview;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new TerefaAdapter(getContext(), tarefas));


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
