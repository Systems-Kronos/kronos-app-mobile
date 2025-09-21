package com.example.kronosprojeto.ui.Chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.kronosprojeto.databinding.FragmentChatBinding;

import java.util.ArrayList;
import java.util.List;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ChatViewModel notificationsViewModel =
                new ViewModelProvider(this).get(ChatViewModel.class);

        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        List<String> mensagens = new ArrayList<>();
        mensagens.add("Oi chat, tudo bom?");
        mensagens.add("Tudo sim, o que deseja?");
        mensagens.add("Como eu bato ponto?");
        mensagens.add("Aqui não bate ponto 😡");
        mensagens.add("Ta bom");

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}