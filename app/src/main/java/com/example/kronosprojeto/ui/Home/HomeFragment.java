package com.example.kronosprojeto.ui.Home;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kronosprojeto.R;
import com.example.kronosprojeto.databinding.FragmentHomeBinding;
import com.example.kronosprojeto.adapter.TarefaAdapter;
import com.example.kronosprojeto.model.Tarefa;
import com.example.kronosprojeto.ui.Details;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

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
        tarefas.add(new Tarefa("Matar boi", new Date(), 3, "Frigorífico", "boi"));
        tarefas.add(new Tarefa("Matar boi", new Date(), 3, "Administração", "boi"));
        tarefas.add(new Tarefa("Matar boi", new Date(), 3, "Administração", "boi"));
        tarefas.add(new Tarefa("Matar boi", new Date(), 3, "Administração", "boi"));
        tarefas.add(new Tarefa("Matar boi", new Date(), 3, "Administração", "boi"));

        RecyclerView recyclerView = binding.tomorrowstasks;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new TarefaAdapter(getContext(), tarefas));

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

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
