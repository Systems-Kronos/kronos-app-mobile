package com.example.kronosprojeto.ui.Home;

import android.content.Context;
import android.content.Intent;
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

import com.example.kronosprojeto.R;
import com.example.kronosprojeto.config.RetrofitClientSQL;
import com.example.kronosprojeto.databinding.FragmentHomeBinding;
import com.example.kronosprojeto.adapter.TaskAdapter;
import com.example.kronosprojeto.model.Task;
import com.example.kronosprojeto.service.TaskService;
import com.example.kronosprojeto.ui.SplashScreen.SplashScreen;
import com.example.kronosprojeto.utils.ToastHelper;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.flexbox.FlexboxLayout;

import java.net.SocketTimeoutException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

        String CalendarText = "Não estará presente no trabalho hoje? clique aqui!";
        SpannableString spannable = new SpannableString(CalendarText);

        int start = CalendarText.indexOf(" clique aqui!");
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
        String userIdStr = prefs.getString("id", "0");
        Long userId = Long.parseLong(userIdStr);

        chargeUserTask(token,userId, "1", "4");

        buttonAll.setOnClickListener(v-> {
            chargeUserTask(token,userId, "1", "4");
            buttonAll.setBackgroundTintList(ContextCompat.getColorStateList(getContext(),R.color.YellowMessage));
            buttonRealocadas.setBackgroundTintList(ContextCompat.getColorStateList(getContext(),R.color.PurpleLight));

        });
        buttonRealocadas.setOnClickListener(v->{
            chargeUserTask(token,userId, "2", "4");
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
    private void chargeUserTask(String token, Long userId, String taskType, String status) {
        TaskService service = RetrofitClientSQL.createService(TaskService.class);
        Call<List<Task>> call = service.getTasksByUserID(userId, "Bearer " + token, taskType, status);
        nestedScrollView.setVisibility(View.GONE);
        loadingOverlay.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<List<Task>>() {

            @Override
            public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Task> tasks = response.body();
                    loadingOverlay.setVisibility(View.GONE);
                    nestedScrollView.setVisibility(View.VISIBLE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        tasks.sort((t1, t2) -> {
                            int priority1 = (t1.getGravidade() * t1.getUrgencia() * t1.getTendencia());
                            int prioriry2 = (t2.getGravidade() * t2.getUrgencia() * t2.getTendencia());
                            return Integer.compare(prioriry2, priority1);
                        });
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.MONTH, -1);
                    Date oneMonthAgo = cal.getTime();

                    List<Task> tasksLastMonth = new ArrayList<>();
                    for (Task tarefa : tasks) {
                        try {
                            Date prazo = sdf.parse(tarefa.getDataPrazo());
                            if ((prazo != null && prazo.after(oneMonthAgo)) ||"Pendente".equalsIgnoreCase(tarefa.getStatus()) || "Em Desenvolvimento".equalsIgnoreCase(tarefa.getStatus()) || "Em Andamento".equalsIgnoreCase(tarefa.getStatus()) ) {
                                tasksLastMonth.add(tarefa);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    int total = tasksLastMonth.size();
                    int concluidas = 0;

                    for (Task task : tasksLastMonth) {
                        Log.d("DEBUG_TASKS", "Tarefa: " + task.getNome()
                                + ", Gravidade: " + task.getGravidade()
                                + ", Urgência: " + task.getUrgencia()
                                + ", Tendência: " + task.getTendencia()
                                + ", Status: " + task.getStatus());

                        Log.e("CONCLUIDO", task.getStatus());
                        if ("Concluída".equalsIgnoreCase(task.getStatus()) || "Concluida".equalsIgnoreCase(task.getStatus()) || "Concluido".equalsIgnoreCase(task.getStatus()) || "Concluído".equalsIgnoreCase(task.getStatus()) || "Cancelada".equalsIgnoreCase(task.getStatus()) || "Cancelado".equalsIgnoreCase(task.getStatus())) {
                            concluidas++;
                        }
                    }

                    PieDataSet dataSet = new PieDataSet(entries, "");
                    dataSet.setColors(Color.parseColor("#E6B648"), Color.parseColor("#FFFFFF"));
                    dataSet.setDrawValues(false);

                    PieData data = new PieData(dataSet);
                    pieChart.setData(data);
                    pieChart.invalidate();

                    List<Task> pendingTasks = new ArrayList<>();
                    for (Task task : tasks) {
                        if ("Pendente".equalsIgnoreCase(task.getStatus()) || "Em Desenvolvimento".equalsIgnoreCase(task.getStatus()) || "Em Andamento".equalsIgnoreCase(task.getStatus()) ) {
                            pendingTasks.add(task);
                        }
                    }

                    Log.e("CONCLUIDO", ""+concluidas);
                    Log.e("TOTAL", ""+total);




                    int porcentagemConcluidas = total > 0 ? (int) ((concluidas * 100.0f) / total) : 0;

                    pieChart.setCenterText(porcentagemConcluidas + "%");
                    entries.clear();

                    entries.add(new PieEntry(porcentagemConcluidas, ""));
                    entries.add(new PieEntry(100 - porcentagemConcluidas, ""));

                    adapter.updateList(pendingTasks);
                    if (pendingTasks.isEmpty()){
                        noContentFlex.setVisibility(View.VISIBLE);
                    }else {
                        noContentFlex.setVisibility(View.GONE);
                    }
                }
                else {
                    Log.d("DEBUG_TASKS", "Resposta não foi bem sucedida. Código: " + response.code());
                }
                if (!isAdded()) {
                    return;
                }

                if (response.code() == 403 || response.code() == 401) {
                    Context context = getContext();
                    if (context != null) {
                        Intent intent = new Intent(context, SplashScreen.class);
                        startActivity(intent);
                        requireActivity().finish();
                    }
                }
                Log.e("CALL",response.code() + "");
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Log.e("DEBUG_TASKS", "Erro ao buscar tarefas", t);

                if (!isAdded() || getContext() == null) {
                    return;
                }

                if (t instanceof SocketTimeoutException) {
                    ToastHelper.showFeedbackToast(
                            getContext(),
                            "error",
                            "Erro de conexão",
                            "Tempo de resposta excedido"
                    );
                } else {
                    ToastHelper.showFeedbackToast(
                            getContext(),
                            "error",
                            "Erro",
                            "Falha na conexão com o servidor"
                    );
                }

                requireActivity().runOnUiThread(() -> {
                    Intent intent = new Intent(requireActivity(), SplashScreen.class);
                    startActivity(intent);
                    requireActivity().finish();
                });
            }

        });
    }

}
