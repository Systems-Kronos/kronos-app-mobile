package com.example.kronosprojeto.ui.Home;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kronosprojeto.R;

import java.util.Date;

public class TarefaViewHolder extends RecyclerView.ViewHolder {

    TextView tituloView, diaView, prioridadeView, setorView;

    public TarefaViewHolder(@NonNull View tarefa_view){
        super(tarefa_view);
        tituloView = itemView.findViewById(R.id.titulo);
        diaView = itemView.findViewById(R.id.dia);
        prioridadeView = itemView.findViewById(R.id.prioridade);
        setorView = itemView.findViewById(R.id.setor);


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

}
