package com.example.kronosprojeto.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kronosprojeto.R;
import com.example.kronosprojeto.model.AssignmentHistory;
import com.example.kronosprojeto.model.Notification;

import java.util.List;

public class AssignmentHistoryAdapter extends RecyclerView.Adapter<AssignmentHistoryAdapter.ViewHolder>  {

    Context context;
    List<AssignmentHistory> historyList;

    public AssignmentHistoryAdapter(Context context, List<AssignmentHistory> historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.history_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentHistoryAdapter.ViewHolder holder, int position) {
        holder.getTitle().setText(historyList.get(position).getTitulo());
        holder.getUser().setText(historyList.get(position).getUsuario());
        holder.getDescription().setText(historyList.get(position).getDescricao());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
            TextView title, user, description;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.txtRealocation);
                user = itemView.findViewById(R.id.txtUserHistory);
                description = itemView.findViewById(R.id.txtDescription);
            }

            public TextView getTitle() {
                return title;
            }

            public TextView getUser() {
                return user;
            }

            public TextView getDescription(){
                return description;
            }
        }
}
