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

public class AssignmentHistoryAdapter extends RecyclerView.Adapter<AssignmentHistoryAdapter.ViewHolder> {

    private final List<AssignmentHistory> historyList;

    public AssignmentHistoryAdapter(List<AssignmentHistory> historyList) {
        this.historyList = historyList;
    }

    public void updateList(List<AssignmentHistory> newList) {
        historyList.clear();
        historyList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AssignmentHistory item = historyList.get(position);
        holder.title.setText(item.getTitle());
        holder.user.setText(item.getAssignedUserName());
        holder.description.setText(item.getDescription());
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, user, description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.txtRealocation);
            user = itemView.findViewById(R.id.txtUserHistory);
            description = itemView.findViewById(R.id.txtDescription);
        }
    }
}
