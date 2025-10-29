package com.example.kronosprojeto.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kronosprojeto.R;
import com.example.kronosprojeto.model.Task;
import com.example.kronosprojeto.ui.Chat.ChatMessage;
import com.google.android.flexbox.FlexboxLayout;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final List<ChatMessage> messages;
    private final Context context;

    private static final int TYPE_USER = 0;
    private static final int TYPE_BOT = 1;

    public ChatAdapter(Context context, List<ChatMessage> messages){
        this.context = context;
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isFromUser() ? TYPE_USER : TYPE_BOT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == TYPE_USER) {
            View view = inflater.inflate(R.layout.message_user_view, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.message_chat_view, parent, false);
            return new BotViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).txtMessage.setText(message.getText());
        } else if (holder instanceof BotViewHolder) {
            ((BotViewHolder) holder).txtMessage.setText(message.getText());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage;
        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtMessageChat);
        }
    }

    static class BotViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage;
        public BotViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txtMessageChat);
        }
    }
    public void removeMessage(ChatMessage message) {
        int index = messages.indexOf(message);
        if (index != -1) {
            messages.remove(index);
            notifyItemRemoved(index);
        }
    }


    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }
}