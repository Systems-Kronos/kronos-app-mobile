package com.example.kronosprojeto.ui.Chat;

import static android.content.Context.MODE_PRIVATE;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kronosprojeto.adapter.ChatAdapter;
import com.example.kronosprojeto.config.RetrofitClientChatBot;
import com.example.kronosprojeto.databinding.FragmentChatBinding;
import com.example.kronosprojeto.dto.ChatBotResponseDto;
import com.example.kronosprojeto.model.ChatBotSession;
import com.example.kronosprojeto.service.ChatBotService;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {
    private FragmentChatBinding binding;
    private ChatAdapter adapter;
    private List<ChatMessage> messages;
    private String sessionId;
    private boolean sessionReady = false;
    private ChatBotService chatBotService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        messages = new ArrayList<>();
        adapter = new ChatAdapter(requireContext(), messages);
        binding.recyclerViewChat.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewChat.setAdapter(adapter);

        if (getContext() != null) {
            sessionId = getContext().getSharedPreferences("app", MODE_PRIVATE)
                    .getString("sessionId", null);
        }
        binding.btnSend.setEnabled(false);

        binding.btnSend.setOnClickListener(v -> {
            String userMessage = binding.editTextMessage.getText().toString().trim();
            if (!userMessage.isEmpty() && sessionReady) {
                sendMessage(userMessage);
                binding.editTextMessage.setText("");
            }
        });

        startNewSession();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void startNewSession() {
        sessionReady = false;
        binding.btnSend.setEnabled(false);

        chatBotService = RetrofitClientChatBot.createService(ChatBotService.class);
        Call<ChatBotSession> call = chatBotService.createNewSession();

        call.enqueue(new Callback<ChatBotSession>() {
            @Override
            public void onResponse(Call<ChatBotSession> call, Response<ChatBotSession> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    ChatBotSession session = response.body();
                    sessionId = session.getSession_id();

                    if (getContext() != null) {
                        getContext().getSharedPreferences("app", MODE_PRIVATE)
                                .edit()
                                .putString("sessionId", sessionId)
                                .apply();
                    }

                    sessionReady = true;
                    binding.btnSend.setEnabled(true);
                } else {
                    sessionReady = false;
                }
            }

            @Override
            public void onFailure(Call<ChatBotSession> call, Throwable t) {
                t.printStackTrace();
                sessionReady = false;
            }
        });
    }

    private void sendMessage(String userMessage) {
        adapter.addMessage(new ChatMessage(userMessage, true));

        Call<ChatBotResponseDto> call = chatBotService.sendMessage(userMessage, sessionId);
        call.enqueue(new Callback<ChatBotResponseDto>() {
            @Override
            public void onResponse(Call<ChatBotResponseDto> call, Response<ChatBotResponseDto> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    String botMessage = response.body().getResponse();
                    adapter.addMessage(new ChatMessage(botMessage, false));
                    binding.recyclerViewChat.scrollToPosition(adapter.getItemCount() - 1);
                } else {
                    adapter.addMessage(new ChatMessage("Erro: resposta inválida do servidor.", false));
                }
            }

            @Override
            public void onFailure(Call<ChatBotResponseDto> call, Throwable t) {
                t.printStackTrace();
                if (!isAdded()) return;
                adapter.addMessage(new ChatMessage("Erro ao se comunicar com o servidor.", false));
            }
        });
    }
}
