package com.example.kronosprojeto.adapter;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kronosprojeto.R;
import com.example.kronosprojeto.config.RetrofitClientSQL;
import com.example.kronosprojeto.dto.LogAtribuicaoTarefaDto;
import com.example.kronosprojeto.service.TaskService;
import com.example.kronosprojeto.ui.Chat.ChatMessage;

import org.w3c.dom.Text;

import java.util.List;

public class HistoryAdapter{

    private final List<LogAtribuicaoTarefaDto> logs;
    private final Context context;

    public HistoryAdapter(List<LogAtribuicaoTarefaDto> log, Context context){
        this.context = context;
        this.logs = log;
    }

//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.fragment_assignment_history, container, false);
//    }

//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        View view = inflater.inflate(R.layout.history_view, parent, false);
//        return new HistoryViewHolder(view);
//    }

//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        String history = logs.get(position).getId();
//
//        ChatMessage message = messages.get(position);
//        if (holder instanceof ChatAdapter.UserViewHolder) {
//            ((ChatAdapter.UserViewHolder) holder).txtMessage.setText(message.getText());
//        } else if (holder instanceof ChatAdapter.BotViewHolder) {
//            ((ChatAdapter.BotViewHolder) holder).txtMessage.setText(message.getText());
//        }
//
//        back.setOnClickListener(v -> {
//            NavController navController = NavHostFragment.findNavController(this);
//            navController.navigate(R.id.action_details_to_HomeFragment);
//        });
//    }
//
//    static class HistoryViewHolder extends RecyclerView.ViewHolder {
//        TextView txtTittle;
//        TextView txtDescription;
//        TextView txtUser;
//        public HistoryViewHolder(@NonNull View itemView) {
//            super(itemView);
//            txtTittle = itemView.findViewById(R.id.txtRealocation);
//            txtDescription = itemView.findViewById(R.id.txtDescription);
//            txtUser = itemView.findViewById(R.id.txtUserHistory);
//        }
//    }

}
