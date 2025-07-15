package com.example.kronosprojeto.ui.Home;

import android.content.ClipData;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kronosprojeto.R;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TerefaAdapter extends RecyclerView.Adapter<TarefaViewHolder> {
    Context context;
    List<Tarefa> tarefas;

    public TerefaAdapter(Context context, List<Tarefa> tarefas) {
        this.context = context;
        this.tarefas = tarefas;
    }


    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public TarefaViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType){
        return new TarefaViewHolder(LayoutInflater.from(context).inflate(R.layout.tarefa_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull TarefaViewHolder holder, int position){
        SimpleDateFormat data = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dataFormatada = data.format(tarefas.get(position).getDia());

        holder.tituloView.setText(tarefas.get(position).getTitulo());
        holder.diaView.setText(dataFormatada);
        holder.setorView.setText(tarefas.get(position).getSetor());
        holder.prioridadeView.setText(String.valueOf(tarefas.get(position).getPrioridade()));

    }

    @Override
    public int getItemCount(){
        return tarefas.size();
    }


}
