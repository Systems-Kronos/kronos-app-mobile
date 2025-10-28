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
    private TextView criterioCapitalLetter, criterioLowercase, criterioNumber, criterioEspecialCaracter, criterioLen, criterioEquals;
    private Button verifyButton;

    public PasswordRedefinitionFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("PasswordRedefFragment", "onCreateView iniciado");
        View view = inflater.inflate(R.layout.fragment_user_password_redefinition, container, false);

        try {
            passwordInput = view.findViewById(R.id.passwordInput);
            passwordInputRepeat = view.findViewById(R.id.passwordInputRepeat);
            criterioCapitalLetter = view.findViewById(R.id.txtCapital);
            criterioLowercase = view.findViewById(R.id.txtOneLower);
            criterioNumber = view.findViewById(R.id.txtOneNumber);
            criterioEspecialCaracter = view.findViewById(R.id.txtEspecialCaracter);
            criterioLen = view.findViewById(R.id.txtLen);
            criterioEquals = view.findViewById(R.id.txtPasswordEquals);

            verifyButton = view.findViewById(R.id.loginButton);


        } catch (Exception e) {
            Log.e("PasswordRedefFragment", "Erro ao encontrar views: ", e);
        }

        Log.d("PasswordRedefFragment", "Views inicializadas: " +
                "\npasswordInput=" + (passwordInput != null) +
                "\npasswordInputRepeat=" + (passwordInputRepeat != null) +
                "\ncriterioMaiuscula=" + (criterioCapitalLetter != null) +
                "\ncriterioMinuscula=" + (criterioLowercase != null) +
                "\ncriterioNumero=" + (criterioNumber != null) +
                "\ncriterioEspecial=" + (criterioEspecialCaracter != null) +
                "\ncriterioTamanho=" + (criterioLen != null) +
                "\nverifyButton=" + (verifyButton != null));

        verifyButton.setEnabled(false);

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                Log.d("PasswordRedefFragment", "afterTextChanged chamado, senha atual: " + passwordInput.getText().toString());
                validarSenha(passwordInput.getText().toString(), passwordInputRepeat.getText().toString());
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


    private void validarSenha(String password, String passwordInputRepeat) {
        boolean temMaiuscula = password.matches(".*[A-Z].*");
        boolean temMinuscula = password.matches(".*[a-z].*");
        boolean temNumero = password.matches(".*[0-9].*");
        boolean temEspecial = password.matches(".*[!@#$%^&*()_+=<>?.].*");
        boolean temTamanho = password.length() >= 6;
        boolean passwordIsEquals = password.equals(passwordInputRepeat);

        criterioCapitalLetter.setVisibility(temMaiuscula ? View.GONE : View.VISIBLE);
        criterioLowercase.setVisibility(temMinuscula ? View.GONE : View.VISIBLE);
        criterioNumber.setVisibility(temNumero ? View.GONE : View.VISIBLE);
        criterioEspecialCaracter.setVisibility(temEspecial ? View.GONE : View.VISIBLE);
        criterioLen.setVisibility(temTamanho ? View.GONE : View.VISIBLE);
        criterioEquals.setVisibility(passwordIsEquals ? View.GONE : View.VISIBLE);
        verifyButton.setEnabled(temMaiuscula && temMinuscula && temNumero && temEspecial && temTamanho && passwordIsEquals);
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
