package com.example.kronosprojeto.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kronosprojeto.R;
import com.example.kronosprojeto.ui.Home.TarefaViewHolder;
import com.example.kronosprojeto.model.Tarefa;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TarefaAdapter extends RecyclerView.Adapter<TarefaViewHolder> {
    Context context;
    List<Tarefa> tarefas;

    public TarefaAdapter(Context context, List<Tarefa> tarefas) {
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
        String dataFormatada = "Data: " + data.format(tarefas.get(position).getDia());
        holder.getTituloView().setText(tarefas.get(position).getTitulo());
        holder.getDiaView().setText(dataFormatada);
        holder.getSetorView().setText("Setor: "+tarefas.get(position).getSetor());
        holder.getPrioridadeView().setText(String.valueOf(tarefas.get(position).getPrioridade()));
        
        holder.getMaisDetalhesView().setOnClickListener(v -> {
            if (context instanceof FragmentActivity) {
                NavController navController = Navigation.findNavController(
                        ((FragmentActivity) context), R.id.nav_host_fragment_activity_main
                );
                navController.navigate(R.id.action_HomeFragment_to_details);
            }
        });
        holder.getTagView().setText("Tag"); // precisa ter no model
        holder.getMaisDetalhesView().setText("Mais informações");
    }

    @Override
    public int getItemCount(){
        return tarefas.size();
    }


}
