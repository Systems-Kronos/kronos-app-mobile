package com.example.kronosprojeto.adapter;

import android.content.Context;
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
import com.example.kronosprojeto.model.Tarefa;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class TarefaAdapter extends RecyclerView.Adapter<TarefaAdapter.TarefaViewHolder> {
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


    public class TarefaViewHolder extends RecyclerView.ViewHolder {

        TextView tituloView, diaView, prioridadeView, setorView, maisDetalhesView, tagView;

        public TarefaViewHolder(@NonNull View tarefa_view){
            super(tarefa_view);
            tituloView = itemView.findViewById(R.id.titulo);
            diaView = itemView.findViewById(R.id.dia);
            prioridadeView = itemView.findViewById(R.id.prioridade);
            setorView = itemView.findViewById(R.id.setor);
            maisDetalhesView = itemView.findViewById(R.id.txtInformation);
            tagView = itemView.findViewById(R.id.tag);

        }



        public TextView getTituloView() {
            return tituloView;
        }
        public TextView getDiaView() {
            return diaView;
        }
        public TextView getPrioridadeView() {
            return prioridadeView;
        }
        public TextView getSetorView() {
            return setorView;
        }

        public TextView getMaisDetalhesView() {
            return maisDetalhesView;
        }

        public TextView getTagView() {
            return tagView;
        }
    }


    @Override
    public int getItemCount(){
        return tarefas.size();
    }


}

