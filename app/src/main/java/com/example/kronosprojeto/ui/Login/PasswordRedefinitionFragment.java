package com.example.kronosprojeto.ui.Login;

import android.graphics.Color;
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
import com.example.kronosprojeto.dto.SenhaDto;
import com.example.kronosprojeto.service.UserService;
import com.example.kronosprojeto.utils.ToastHelper;
import retrofit2.Call;

public class PasswordRedefinitionFragment extends Fragment {

    private EditText passwordInput, passwordInputRepeat;
    private TextView criterioMaiuscula, criterioMinuscula, criterioNumero, criterioEspecial, criterioTamanho;
    private Button verifyButton;

    public PasswordRedefinitionFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user_password_redefinition, container, false);

        passwordInput = view.findViewById(R.id.passwordInput);
        passwordInputRepeat = view.findViewById(R.id.passwordInputRepeat);

        criterioMaiuscula = view.findViewById(R.id.txtCapital);
        criterioMinuscula = view.findViewById(R.id.txtOneLower);
        criterioNumero = view.findViewById(R.id.txtOneNumber);
        criterioEspecial = view.findViewById(R.id.txtEspecialCaracter);
        criterioTamanho = view.findViewById(R.id.txtLen);

        verifyButton = view.findViewById(R.id.loginButton);
        verifyButton.setEnabled(false);

        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                validarSenha(passwordInput.getText().toString());
            }
        };

        passwordInput.addTextChangedListener(watcher);
        passwordInputRepeat.addTextChangedListener(watcher);

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

        criterioMaiuscula.setTextColor(Color.RED);
        criterioMinuscula.setTextColor(Color.RED);
        criterioNumero.setTextColor(Color.RED);
        criterioEspecial.setTextColor(Color.RED);
        criterioTamanho.setTextColor(Color.RED);

        boolean senhasIguais = senha.equals(passwordInputRepeat.getText().toString());

        if (!senhasIguais) {
            ToastHelper.showFeedbackToast(getContext(), "error", "SENHAS DIFERENTES", "As senhas devem ser iguais");
            return;
        }

        boolean todosValidos = temMaiuscula && temMinuscula && temNumero && temEspecial && temTamanho && senhasIguais;
        verifyButton.setEnabled(todosValidos);

        verifyButton.setOnClickListener(v -> {
            if (!senhasIguais) {
                ToastHelper.showFeedbackToast(getContext(), "error", "SENHAS DIFERENTES", "As senhas devem ser iguais");
                return;
            }

            UserService service = RetrofitClientSQL.createService(UserService.class);
            long idUsuario = requireActivity()
                    .getSharedPreferences("app", getContext().MODE_PRIVATE)
                    .getLong("user_id", -1);

            if (idUsuario == -1) {
                ToastHelper.showFeedbackToast(getContext(), "error", "Erro", "Usuário não identificado. Faça o processo novamente.");
                return;
            }

            SenhaDto senhaDto = new SenhaDto(senha);
            Log.d("PasswordRedefinition", "Atualizando usuário ID: " + idUsuario + " Senha : " + senhaDto);

            Call<Void> call = service.updatePassword(String.valueOf(idUsuario), senhaDto);
            call.enqueue(new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, retrofit2.Response<Void> response) {
                    if (response.isSuccessful()) {
                        ToastHelper.showFeedbackToast(getContext(), "success", "Senha atualizada", "Sua senha foi redefinida com sucesso!");
                    } else {
                        ToastHelper.showFeedbackToast(getContext(), "error", "Erro", "Não foi possível atualizar a senha.");
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    ToastHelper.showFeedbackToast(getContext(), "error", "Falha", "Erro de conexão: " + t.getMessage());
                }
            });
        });
    }
}
