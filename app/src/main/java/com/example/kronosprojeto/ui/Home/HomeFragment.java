package com.example.kronosprojeto.ui.Home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kronosprojeto.R;
import com.example.kronosprojeto.config.RetrofitClientSQL;
import com.example.kronosprojeto.databinding.FragmentHomeBinding;
import com.example.kronosprojeto.adapter.TaskAdapter;
import com.example.kronosprojeto.model.Task;
import com.example.kronosprojeto.service.TaskService;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    RecyclerView recyclerView;
    TaskAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        List<Task> tarefas = new ArrayList<>();

        recyclerView = binding.tomorrowstasks;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TaskAdapter(getContext(), new ArrayList<>()); // lista vazia inicial
        recyclerView.setAdapter(adapter);
        Log.d("DEBUG_APP", "Iniciando a MainActivity...");

        PieChart pieChart = binding.pieChart;

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(70f, ""));
        entries.add(new PieEntry(30f, ""));


        PieDataSet dataSet = new PieDataSet(entries, "");

        dataSet.setColors(Color.parseColor("#E6B648"), Color.parseColor("#FFFFFF"));
        dataSet.setDrawValues(false);
        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        pieChart.setDrawEntryLabels(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);

        pieChart.setHoleRadius(80f);
        pieChart.setTransparentCircleRadius(85f);

        pieChart.invalidate();

        Typeface boldTypeface = Typeface.defaultFromStyle(Typeface.BOLD);
        pieChart.setCenterTextTypeface(boldTypeface);

        pieChart.setCenterText("70%");
        pieChart.setCenterTextSize(35f);
        pieChart.setCenterTextColor(Color.parseColor("#370963"));
        pieChart.setDrawCenterText(true);
        pieChart.setHoleColor(Color.parseColor("#FFE9B5"));
        SharedPreferences prefs = getActivity().getSharedPreferences("app", Context.MODE_PRIVATE);





        String token = prefs.getString("jwt", null);
        String usuarioIdStr = prefs.getString("id", "0");
        Long usuarioId = Long.parseLong(usuarioIdStr);
        carregarTarefasUsuario(token,usuarioId, "1", "1");

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void carregarTarefasUsuario(String token, Long usuarioId, String tipoTarefa, String status) {
        // Logando parâmetros da requisição
        Log.d("DEBUG_TASKS", "Chamando getTasksByUserID com:");
        Log.d("DEBUG_TASKS", "Token: " + token);
        Log.d("DEBUG_TASKS", "usuarioId: " + usuarioId);
        Log.d("DEBUG_TASKS", "tipoTarefa: " + tipoTarefa);
        Log.d("DEBUG_TASKS", "status: " + status);

        TaskService service = RetrofitClientSQL.createService(TaskService.class);
        Call<List<Task>> call = service.getTasksByUserID(usuarioId,"Bearer "+ token, tipoTarefa, status);

        call.enqueue(new Callback<List<Task>>() {
            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Task> tarefas = response.body();
                    Log.d("DEBUG_TASKS", "Quantidade de tarefas recebidas: " + tarefas.size());
                    for (Task tarefa : tarefas) {
                        Log.d("DEBUG_TASKS", "Tarefa: " + tarefa.getNome()
                                + ", Gravidade: " + tarefa.getGravidade()
                                + ", Origem: " + tarefa.getOrigemTarefa()
                                + ", Data Atribuicao: " + tarefa.getDataAtribuicao()
                                + ", Status: " + tarefa.getStatus());
                    }
                    adapter.updateList(tarefas); // Atualiza RecyclerView
                } else {
                    Log.d("DEBUG_TASKS", "Resposta não foi bem sucedida. Código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Log.e("DEBUG_TASKS", "Erro ao buscar tarefas", t);
            }

        });




    }

}
