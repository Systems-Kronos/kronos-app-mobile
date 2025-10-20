package com.example.kronosprojeto.ui.Login;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.kronosprojeto.R;
import com.example.kronosprojeto.utils.ToastHelper;

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

        criterioMaiuscula.setTextColor(temMaiuscula ? Color.GREEN : Color.RED);
        criterioMinuscula.setTextColor(temMinuscula ? Color.GREEN : Color.RED);
        criterioNumero.setTextColor(temNumero ? Color.GREEN : Color.RED);
        criterioEspecial.setTextColor(temEspecial ? Color.GREEN : Color.RED);
        criterioTamanho.setTextColor(temTamanho ? Color.GREEN : Color.RED);

        boolean senhasIguais = senha.equals(passwordInputRepeat.getText().toString());

        if(!senhasIguais){
            ToastHelper.showFeedbackToast(getContext(),"error","SENHAS DIFERENTES","As senhas devem ser iguais");
            return;
        }
        boolean todosValidos = temMaiuscula && temMinuscula && temNumero && temEspecial && temTamanho && senhasIguais;
        verifyButton.setEnabled(todosValidos);

        verifyButton.setOnClickListener(v -> {
            if(!senhasIguais){
                ToastHelper.showFeedbackToast(getContext(),"error","SENHAS DIFERENTES","As senhas devem ser iguais");
                return;
            }
        });
    }
}
