package com.example.kronosprojeto.ui.AssignmentHistory;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.kronosprojeto.adapter.AssignmentHistoryAdapter;
import com.example.kronosprojeto.config.RetrofitClientSQL;
import com.example.kronosprojeto.databinding.FragmentAssignmentHistoryBinding;
import com.example.kronosprojeto.databinding.FragmentNotificationsBinding;


import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.kronosprojeto.R;
import com.example.kronosprojeto.adapter.NotificationAdapter;
import com.example.kronosprojeto.databinding.FragmentNotificationsBinding;
import com.example.kronosprojeto.dto.LogAtribuicaoTarefaDto;
import com.example.kronosprojeto.model.AssignmentHistory;
import com.example.kronosprojeto.model.Notification;
import com.example.kronosprojeto.service.TaskService;
import com.example.kronosprojeto.utils.ToastHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    private AssignmentHistoryAdapter adapter;
    private List<AssignmentHistory> historyList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAssignmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.assignmentHistoryItem;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new AssignmentHistoryAdapter(historyList);
        recyclerView.setAdapter(adapter);

        ImageView back = root.findViewById(R.id.imgBack);
        back.setOnClickListener(v -> NavHostFragment.findNavController(this).popBackStack());

        loadAssignmentHistory();

        return root;
    }

    private void loadAssignmentHistory() {
        SharedPreferences prefs = requireContext().getSharedPreferences("app", Context.MODE_PRIVATE);
        String token = prefs.getString("jwt", null);
        long idTarefa = prefs.getLong("selectedTask", -1);

        if (token == null || idTarefa == -1) {
            ToastHelper.showFeedbackToast(requireActivity(), "error", "Erro", "tarefa ou token não encontrad");
            return;
        }

        TaskService taskService = RetrofitClientSQL.createService(TaskService.class);
        taskService.searchLogByTask("Bearer " + token, idTarefa).enqueue(new Callback<List<LogAtribuicaoTarefaDto>>() {
            @Override
            public void onResponse(Call<List<LogAtribuicaoTarefaDto>> call, Response<List<LogAtribuicaoTarefaDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<LogAtribuicaoTarefaDto> logs = response.body();
                    List<AssignmentHistory> newHistoryList = new ArrayList<>();

                    for (LogAtribuicaoTarefaDto log : logs) {
                        String tittle = "#00" + log.getId() + " Log gerado em: " + log.getDataRealocacao();
                        String description = log.getObservacao();
                        String user = "Por: " + log.getIdUsuarioAtribuido();

                        newHistoryList.add(new AssignmentHistory(tittle, description, user));
                    }

                    adapter.updateList(newHistoryList);

                } else {
                    ToastHelper.showFeedbackToast(requireActivity(), "info", "Atenção", "Nenhum histórico encontrado.");
                }
            }

            @Override
            public void onFailure(Call<List<LogAtribuicaoTarefaDto>> call, Throwable t) {
                ToastHelper.showFeedbackToast(requireActivity(), "error", "Erro ao carregar histórico:", t.getMessage());
            }
        });
    }
}