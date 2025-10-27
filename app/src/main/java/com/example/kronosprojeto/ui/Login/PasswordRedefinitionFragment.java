package com.example.kronosprojeto.ui.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.kronosprojeto.R;
import com.example.kronosprojeto.config.RetrofitClientSQL;
import com.example.kronosprojeto.dto.LoginRequestDto;
import com.example.kronosprojeto.dto.SenhaDto;
import com.example.kronosprojeto.model.Token;
import com.example.kronosprojeto.service.AuthService;
import com.example.kronosprojeto.service.UserService;
import com.example.kronosprojeto.utils.ToastHelper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PasswordRedefinitionFragment extends Fragment {

    private EditText passwordInput, passwordInputRepeat;
    private TextView criterioMaiuscula, criterioMinuscula, criterioNumero, criterioEspecial, criterioTamanho;
    private Button verifyButton;

    public PasswordRedefinitionFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("PasswordRedefFragment", "onCreateView iniciado");
        View view = inflater.inflate(R.layout.fragment_user_password_redefinition, container, false);

        try {
            passwordInput = view.findViewById(R.id.passwordInput);
            passwordInputRepeat = view.findViewById(R.id.passwordInputRepeat);

            criterioMaiuscula = view.findViewById(R.id.txtCapital);
            criterioMinuscula = view.findViewById(R.id.txtOneLower);
            criterioNumero = view.findViewById(R.id.txtOneNumber);
            criterioEspecial = view.findViewById(R.id.txtEspecialCaracter);
            criterioTamanho = view.findViewById(R.id.txtLen);

            verifyButton = view.findViewById(R.id.loginButton);
        } catch (Exception e) {
            Log.e("PasswordRedefFragment", "Erro ao encontrar views: ", e);
        }

        Log.d("PasswordRedefFragment", "Views inicializadas: " +
                "\npasswordInput=" + (passwordInput != null) +
                "\npasswordInputRepeat=" + (passwordInputRepeat != null) +
                "\ncriterioMaiuscula=" + (criterioMaiuscula != null) +
                "\ncriterioMinuscula=" + (criterioMinuscula != null) +
                "\ncriterioNumero=" + (criterioNumero != null) +
                "\ncriterioEspecial=" + (criterioEspecial != null) +
                "\ncriterioTamanho=" + (criterioTamanho != null) +
                "\nverifyButton=" + (verifyButton != null));

        verifyButton.setEnabled(false);

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                Log.d("PasswordRedefFragment", "afterTextChanged chamado, senha atual: " + passwordInput.getText().toString());
                validarSenha(passwordInput.getText().toString());
            }
        };

        if (passwordInput != null) passwordInput.addTextChangedListener(watcher);
        if (passwordInputRepeat != null) passwordInputRepeat.addTextChangedListener(watcher);

        if (verifyButton != null) verifyButton.setOnClickListener(v -> {
            Log.d("PasswordRedefFragment", "verifyButton clicado");
            atualizarSenhaEVoltarLogin();
        });

        return view;
    }


    private void validarSenha(String senha) {
        boolean temMaiuscula = senha.matches(".*[A-Z].*");
        boolean temMinuscula = senha.matches(".*[a-z].*");
        boolean temNumero = senha.matches(".*[0-9].*");
        boolean temEspecial = senha.matches(".*[!@#$%^&*()_+=<>?].*");
        boolean temTamanho = senha.length() >= 6;

        criterioMaiuscula.setVisibility(temMaiuscula ? View.GONE : View.VISIBLE);
        criterioMinuscula.setVisibility(temMinuscula ? View.GONE : View.VISIBLE);
        criterioNumero.setVisibility(temNumero ? View.GONE : View.VISIBLE);
        criterioEspecial.setVisibility(temEspecial ? View.GONE : View.VISIBLE);
        criterioTamanho.setVisibility(temTamanho ? View.GONE : View.VISIBLE);

        boolean senhasIguais = senha.equals(passwordInputRepeat.getText().toString());
        verifyButton.setEnabled(temMaiuscula && temMinuscula && temNumero && temEspecial && temTamanho && senhasIguais);
    }


    private void atualizarSenhaEVoltarLogin() {
        String senha = passwordInput.getText().toString();
        String repetir = passwordInputRepeat.getText().toString();

        if (!senha.equals(repetir)) {
            ToastHelper.showFeedbackToast(getContext(), "error", "SENHAS DIFERENTES", "As senhas devem ser iguais");
            return;
        }

        long userId = requireActivity().getSharedPreferences("app", getContext().MODE_PRIVATE)
                .getLong("user_id", -1);

        if (userId == -1) {
            ToastHelper.showFeedbackToast(getContext(), "error", "Erro", "Usuário não identificado. Faça o processo novamente.");
            return;
        }

        SenhaDto senhaDto = new SenhaDto(senha);
        UserService userService = RetrofitClientSQL.createService(UserService.class);

        Call<Void> callUpdate = userService.updatePassword(String.valueOf(userId), senhaDto);
        callUpdate.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    ToastHelper.showFeedbackToast(getContext(), "success", "Senha atualizada", "Senha redefinida com sucesso!");

                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    ToastHelper.showFeedbackToast(getContext(), "error", "Erro", "Não foi possível atualizar a senha.");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                ToastHelper.showFeedbackToast(getContext(), "error", "Falha", "Erro de conexão: " + t.getMessage());
            }
        });
    }




}
