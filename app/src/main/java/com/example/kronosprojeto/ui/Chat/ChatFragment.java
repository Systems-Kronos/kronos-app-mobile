package com.example.kronosprojeto.ui.Chat;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.example.kronosprojeto.MainActivity;
import com.example.kronosprojeto.adapter.ChatAdapter;
import com.example.kronosprojeto.config.RetrofitClientChatBot;
import com.example.kronosprojeto.config.RetrofitClientSQL;
import com.example.kronosprojeto.databinding.FragmentChatBinding;
import com.example.kronosprojeto.dto.ChatBotResponseDto;
import com.example.kronosprojeto.dto.LoginRequestDto;
import com.example.kronosprojeto.model.ChatBotSession;
import com.example.kronosprojeto.model.Task;
import com.example.kronosprojeto.model.Token;
import com.example.kronosprojeto.service.AuthService;
import com.example.kronosprojeto.service.ChatBotService;
import com.example.kronosprojeto.service.TaskService;
import com.example.kronosprojeto.ui.Chat.ChatMessage;
import com.example.kronosprojeto.ui.Login.LoginActivity;
import com.example.kronosprojeto.ui.Login.PhoneRecoveryActivity;
import com.example.kronosprojeto.utils.ToastHelper;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
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
    FrameLayout loadingOverlay;
    private ChatMessage loadingMessage = null;
    private List<ChatMessage> messages;
    private String sessionId;
    private boolean sessionReady = false;
    private ChatBotService chatBotService;

    private AuthService authService;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        loadingOverlay = binding.loadingOverlay;
        messages = new ArrayList<>();
        adapter = new ChatAdapter(requireContext(), messages);
        binding.recyclerViewChat.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewChat.setAdapter(adapter);
        authService = RetrofitClientSQL.createService(AuthService.class);

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
        callSqlForDau();

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


        SharedPreferences prefs = requireContext().getSharedPreferences("app", MODE_PRIVATE);
        String idString = prefs.getString("id", null);

        Call<ChatBotSession> call = chatBotService.createNewSession(idString);
        loadingOverlay.setVisibility(View.VISIBLE);

        call.enqueue(new Callback<ChatBotSession>() {
            @Override
            public void onResponse(Call<ChatBotSession> call, Response<ChatBotSession> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    ChatBotSession session = response.body();
                    sessionId = session.getSession_id();
                    loadingOverlay.setVisibility(View.GONE);

                    if (getContext() != null) {
                        getContext().getSharedPreferences("app", MODE_PRIVATE)
                                .edit()
                                .putString("sessionId", sessionId)
                                .apply();
                    }

                    sessionReady = true;
                    binding.btnSend.setEnabled(true);
                    String welcomeMessage = "Olá, para tirar dúvidas sobre o aplicativo estou à sua disposição! 😉";
                    adapter.addMessage(new ChatMessage(welcomeMessage, false));
                    binding.recyclerViewChat.scrollToPosition(adapter.getItemCount() - 1);

                } else {
                    sessionReady = false;
                }
            }

            @Override
            public void onFailure(Call<ChatBotSession> call, Throwable t) {
                t.printStackTrace();
                Log.d("ERRO AQUI", "teste");
                sessionReady = false;
            }
        });
    }



    private void sendMessage(String userMessage) {
        adapter.addMessage(new ChatMessage(userMessage, true));
        binding.recyclerViewChat.scrollToPosition(adapter.getItemCount() - 1);

        loadingMessage = new ChatMessage("Pensando na sua resposta", false);
        adapter.addMessage(loadingMessage);
        binding.recyclerViewChat.scrollToPosition(adapter.getItemCount() - 1);

        Handler handler = new Handler();
        String baseText = "Pensando na sua resposta";
        Runnable runnable = new Runnable() {
            int dotCount = 0;
            @Override
            public void run() {
                if (loadingMessage != null) {
                    dotCount = (dotCount + 1) % 4;
                    String dots = new String(new char[dotCount]).replace("\0", ".");
                    loadingMessage.setText(baseText + dots);
                    adapter.notifyItemChanged(adapter.getItemCount() - 1);
                    handler.postDelayed(this, 500);
                }
            }
        };
        handler.post(runnable);

        binding.btnSend.setEnabled(false);

        Call<ChatBotResponseDto> call = chatBotService.sendMessage(userMessage, sessionId);
        call.enqueue(new Callback<ChatBotResponseDto>() {
            @Override
            public void onResponse(Call<ChatBotResponseDto> call, Response<ChatBotResponseDto> response) {
                if (!isAdded() || binding == null) return;

                handler.removeCallbacks(runnable);
                adapter.removeMessage(loadingMessage);
                loadingMessage = null;

                binding.btnSend.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    String botMessage = response.body().getResponse();
                    adapter.addMessage(new ChatMessage(botMessage, false));
                } else {
                    adapter.addMessage(new ChatMessage("Erro: resposta inválida do servidor.", false));
                }
                binding.recyclerViewChat.scrollToPosition(adapter.getItemCount() - 1);
            }

            @Override
            public void onFailure(Call<ChatBotResponseDto> call, Throwable t) {
                t.printStackTrace();
                if (!isAdded()) return;

                handler.removeCallbacks(runnable);
                adapter.removeMessage(loadingMessage);
                loadingMessage = null;

                adapter.addMessage(new ChatMessage("Erro ao se comunicar com o servidor.", false));
            }
        });
    }


    private void callSqlForDau() {

        LoginRequestDto loginRequest = new LoginRequestDto("1", "2");
        Call<Token> call = authService.login(loginRequest);

        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {

            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
            }
        });
    }


}
