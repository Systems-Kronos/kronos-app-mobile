package com.example.kronosprojeto.adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kronosprojeto.R;
import com.example.kronosprojeto.model.Task;
import com.google.android.flexbox.FlexboxLayout;

import java.util.List;

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

            holder.getDayView().setText("Data Atribuição: " + atribuicao.split("T")[0]);
        } else {
            holder.getDayView().setText("Data: -");
        }

        holder.getTitleView().setText(tarefas.get(position).getNome());
        holder.getSectorView().setText(" ");
        int tempo = tarefas.get(position).getTempoEstimado();
        holder.getDayTermView().setText("Tempo estimado: " + tempo + " Horas");

        int gut = tarefas.get(position).getGravidade()  * tarefas.get(position).getUrgencia() * tarefas.get(position).getTendencia();
        double gutEscala = (gut*10 / 125.0);
        Log.e("ESCALA GUT", String.valueOf(gutEscala));
        if (gutEscala > 8){
            holder.getBorderView().setBackgroundResource(R.drawable.border_red_shape);
            holder.getGutLevelIcon().setImageResource(R.drawable.higher_priority);
        } else if (gutEscala > 6 && gutEscala <= 8) {
            holder.getBorderView().setBackgroundResource(R.drawable.border_orange_shape);
            holder.getGutLevelIcon().setImageResource(R.drawable.high_priority);
        } else if (gutEscala > 4 && gutEscala <= 6) {
            holder.getBorderView().setBackgroundResource(R.drawable.border_yellow_shape);
            holder.getGutLevelIcon().setImageResource(R.drawable.medium_priority);
        }else if (gutEscala > 2 && gutEscala <= 4) {
            holder.getBorderView().setBackgroundResource(R.drawable.border_green_shape);
            holder.getGutLevelIcon().setImageResource(R.drawable.low_priority);
        }else {
            holder.getBorderView().setBackgroundResource(R.drawable.border_blue_shape);
            holder.getGutLevelIcon().setImageResource(R.drawable.lower_priority);
        }

        holder.getPriorityView().setText(String.valueOf(gutEscala)+"/10");

        holder.getCardTask().setOnClickListener(v->{
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
        FlexboxLayout cardTask;
        TextView titleView, dayView, priorityView, sectorView, detailsView, tagView, dayTermView;
        View borderView;
        ImageView gutLevelIcon;

        public TarefaViewHolder(@NonNull View tarefa_view){
            super(tarefa_view);
            cardTask = itemView.findViewById(R.id.cardTask);
            gutLevelIcon = itemView.findViewById(R.id.gutLevel);
            titleView = itemView.findViewById(R.id.txtTitle);
            dayView = itemView.findViewById(R.id.txtDay);
            dayTermView = itemView.findViewById(R.id.dateTerm);
            priorityView = itemView.findViewById(R.id.priority);
            sectorView = itemView.findViewById(R.id.txtSector);
            detailsView = itemView.findViewById(R.id.txtInformation);
            tagView = itemView.findViewById(R.id.txtTag);
            borderView = itemView.findViewById(R.id.barra_decorativa);
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
        public ImageView getGutLevelIcon(){return gutLevelIcon;}
        public TextView getTagView() {
            return tagView;
        }
        public FlexboxLayout getCardTask(){ return  cardTask;}
        public View getBorderView(){ return borderView; }

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

