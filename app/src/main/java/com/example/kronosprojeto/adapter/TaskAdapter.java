package com.example.kronosprojeto.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kronosprojeto.R;
import com.example.kronosprojeto.model.Task;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TarefaViewHolder> {
    Context context;
    List<Task> tarefas;
    String comeFrom;

    public TaskAdapter(Context context, List<Task> tarefas, String comeFrom) {
        this.context = context;
        this.tarefas = tarefas;
        this.comeFrom = comeFrom;
    }

    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public TarefaViewHolder onCreateViewHolder(@NonNull @org.jetbrains.annotations.NotNull ViewGroup parent, int viewType){
        return new TarefaViewHolder(LayoutInflater.from(context).inflate(R.layout.tarefa_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull @org.jetbrains.annotations.NotNull TarefaViewHolder holder, int position){


        String atribuicao = tarefas.get(position).getDataAtribuicao();
        if (atribuicao != null && !atribuicao.isEmpty()) {
            holder.getDayView().setText("Data: " + atribuicao);
        } else {
            holder.getDayView().setText("Data: -");
        }

// AQUIII
        holder.getTitleView().setText(tarefas.get(position).getNome());
        holder.getSectorView().setText(" ");
        int tempo = tarefas.get(position).getTempoEstimado();
        holder.getDayTermView().setText("Tempo estimado: " + tempo + " Horas");

        int gut = tarefas.get(position).getGravidade()  * tarefas.get(position).getGravidade() *  tarefas.get(position).getUrgencia() * tarefas.get(position).getTendencia();
        double gutEscala = (gut / 125.0) * 5;
        holder.getPriorityView().setText(String.valueOf(gutEscala));
        
        holder.getDetailsView().setOnClickListener(v -> {
            if (context instanceof FragmentActivity) {
                NavController navController = Navigation.findNavController(
                        ((FragmentActivity) context), R.id.nav_host_fragment_activity_main
                );
                Bundle bundle = new Bundle();
                bundle.putLong("idTarefa", tarefas.get(position).getId());

                if(comeFrom.equals("home")){
                    navController.navigate(R.id.action_HomeFragment_to_details,bundle);
                }else{
                    navController.navigate(R.id.action_PerfilFragment_to_details,bundle);

                }
            }
        });
        holder.getTagView().setText(tarefas.get(position).getOrigemTarefa()); // precisa ter no model
        holder.getDetailsView().setText("Mais informações");
    }


    public class TarefaViewHolder extends RecyclerView.ViewHolder {

        TextView titleView, dayView, priorityView, sectorView, detailsView, tagView, dayTermView;

        public TarefaViewHolder(@NonNull View tarefa_view){
            super(tarefa_view);
            titleView = itemView.findViewById(R.id.txtTitle);
            dayView = itemView.findViewById(R.id.txtDay);
            dayTermView = itemView.findViewById(R.id.dateTerm);
            priorityView = itemView.findViewById(R.id.priority);
            sectorView = itemView.findViewById(R.id.txtSector);
            detailsView = itemView.findViewById(R.id.txtInformation);
            tagView = itemView.findViewById(R.id.txtTag);

        }

        public TextView getDayTermView() {
            return dayTermView;
        }

        public TextView getTitleView() {
            return titleView;
        }
        public TextView getDayView() {
            return dayView;
        }
        public TextView getPriorityView() {
            return priorityView;
        }
        public TextView getSectorView() {
            return sectorView;
        }

        public TextView getDetailsView() {
            return detailsView;
        }

        public TextView getTagView() {
            return tagView;
        }
    }


    @Override
    public int getItemCount(){
        return tarefas.size();
    }
    public void updateList(List<Task> novasTarefas) {
        this.tarefas.clear();
        this.tarefas.addAll(novasTarefas);
        notifyDataSetChanged();
    }


}

