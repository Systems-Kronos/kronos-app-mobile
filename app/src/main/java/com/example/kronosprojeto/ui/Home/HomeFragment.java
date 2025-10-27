package com.example.kronosprojeto.ui.Home;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Visibility;

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
import com.google.android.flexbox.FlexboxLayout;

import java.nio.channels.FileLock;
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
    PieChart pieChart;
    ArrayList<PieEntry> entries;
    FrameLayout loadingOverlay;
    NestedScrollView nestedScrollView;
    FlexboxLayout noContentFlex;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        loadingOverlay= binding.loadingOverlay;
        nestedScrollView = binding.contentScroll;
        noContentFlex = binding.noContent;
        TextView presentTodayText = binding.presentTodayQuestion;

        String texto = "Não estará presente no trabalho hoje? clique aqui!";
        SpannableString spannable = new SpannableString(texto);

        int start = texto.indexOf(" clique aqui!");
        int end = start + " clique aqui!".length();

        presentTodayText.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(this);

            NavOptions navOptions = new NavOptions.Builder()
                    .setPopUpTo(R.id.mobile_navigation, true)
                    .setLaunchSingleTop(true)
                    .build();

            navController.navigate(R.id.CalendarioFragment, null, navOptions);

        });

        spannable.setSpan(
                new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.YellowMessage)),
                start,
                end,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        presentTodayText.setText(spannable);


        recyclerView = binding.tomorrowstasks;
        AppCompatButton buttonAll = binding.btnTarefaTodas;
        AppCompatButton buttonRealocadas = binding.btnTarefaRealocadas;


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new TaskAdapter(getContext(), new ArrayList<>(), "home");
        recyclerView.setAdapter(adapter);
        Log.d("DEBUG_APP", "Iniciando a MainActivity...");

        pieChart = binding.pieChart;

        entries = new ArrayList<>();



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

        buttonAll.setOnClickListener(v-> {
            carregarTarefasUsuario(token,usuarioId, "1", "1");
            buttonAll.setBackgroundTintList(ContextCompat.getColorStateList(getContext(),R.color.YellowMessage));
            buttonRealocadas.setBackgroundTintList(ContextCompat.getColorStateList(getContext(),R.color.PurpleLight));

        });
        buttonRealocadas.setOnClickListener(v->{
            carregarTarefasUsuario(token,usuarioId, "2", "1");
            buttonRealocadas.setBackgroundTintList(ContextCompat.getColorStateList(getContext(),R.color.YellowMessage));
            buttonAll.setBackgroundTintList(ContextCompat.getColorStateList(getContext(),R.color.PurpleLight));
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private void carregarTarefasUsuario(String token, Long usuarioId, String tipoTarefa, String status) {
        Log.d("DEBUG_TASKS", "Chamando getTasksByUserID com:");
        Log.d("DEBUG_TASKS", "Token: " + token);
        Log.d("DEBUG_TASKS", "usuarioId: " + usuarioId);
        Log.d("DEBUG_TASKS", "tipoTarefa: " + tipoTarefa);
        Log.d("DEBUG_TASKS", "status: " + status);

        TaskService service = RetrofitClientSQL.createService(TaskService.class);
        Call<List<Task>> call = service.getTasksByUserID(usuarioId, "Bearer " + token, tipoTarefa, status);
        nestedScrollView.setVisibility(View.GONE);
        loadingOverlay.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<List<Task>>() {

            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Task> tarefas = response.body();
                    loadingOverlay.setVisibility(View.GONE);
                    nestedScrollView.setVisibility(View.VISIBLE);

                    // 🔹 Ordena pela prioridade (gravidade * urgencia * tendencia)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        tarefas.sort((t1, t2) -> {
                            int prioridade1 = (t1.getGravidade() * t1.getUrgencia() * t1.getTendencia());
                            int prioridade2 = (t2.getGravidade() * t2.getUrgencia() * t2.getTendencia());
                            return Integer.compare(prioridade2, prioridade1); // maior prioridade primeiro
                        });
                    }

                    int total = tarefas.size();
                    int concluidas = 0;

                    for (Task tarefa : tarefas) {
                        Log.d("DEBUG_TASKS", "Tarefa: " + tarefa.getNome()
                                + ", Gravidade: " + tarefa.getGravidade()
                                + ", Urgência: " + tarefa.getUrgencia()
                                + ", Tendência: " + tarefa.getTendencia()
                                + ", Status: " + tarefa.getStatus());

                        if ("Concluída".equalsIgnoreCase(tarefa.getStatus())) {
                            concluidas++;
                        }
                    }

                    int porcentagemConcluidas = total > 0 ? (int) ((concluidas * 100.0f) / total) : 0;

                    pieChart.setCenterText(porcentagemConcluidas + "%");
                    entries.clear();

                    entries.add(new PieEntry(porcentagemConcluidas, ""));
                    entries.add(new PieEntry(100 - porcentagemConcluidas, ""));

                    PieDataSet dataSet = new PieDataSet(entries, "");
                    dataSet.setColors(Color.parseColor("#E6B648"), Color.parseColor("#FFFFFF"));
                    dataSet.setDrawValues(false);

                    PieData data = new PieData(dataSet);
                    pieChart.setData(data);
                    pieChart.invalidate();

                    adapter.updateList(tarefas);
                    if (tarefas.isEmpty()){
                        noContentFlex.setVisibility(View.VISIBLE);
                    }else {
                        noContentFlex.setVisibility(View.GONE);
                    }
                }
                else {
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
