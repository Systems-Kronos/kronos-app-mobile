package com.example.kronosprojeto.ui.RestrictArea;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.kronosprojeto.MainActivity;
import com.example.kronosprojeto.R;
import com.example.kronosprojeto.config.RetrofitClientSQL;
import com.example.kronosprojeto.dto.LoginAdmRequestDTO;
import com.example.kronosprojeto.dto.LoginAdmResponseDTO;
import com.example.kronosprojeto.service.AdministratorService;
import com.example.kronosprojeto.utils.ToastHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestrictLogin extends Fragment {

    private TextView phoneRecoveryEntrypoint;
    private AppCompatButton loginButton;
    private EditText txtEmail;
    private EditText passwordInput;
    private FrameLayout loadingOverlay;

    private AdministratorService admService;

    private boolean isUpdating = false;
    private final String mask = "###.###.###-##";

    public RestrictLogin() {
    }

    public static RestrictLogin newInstance(String param1, String param2) {
        RestrictLogin fragment = new RestrictLogin();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restrict_login, container, false);

        txtEmail = view.findViewById(R.id.txtEmail);
        passwordInput = view.findViewById(R.id.passwordInput);
        loginButton = view.findViewById(R.id.loginButton);
        loadingOverlay = view.findViewById(R.id.loadingOverlay);

        admService = RetrofitClientSQL.createService(AdministratorService.class);

        loginButton.setOnClickListener(v -> login());

        return view;
    }

    private void login() {
        loadingOverlay.setVisibility(View.VISIBLE);

        String email = txtEmail.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            ToastHelper.showFeedbackToast(requireContext(), "warning",
                    "Campos obrigatórios", "Preencha todos os campos antes de continuar.");
            loadingOverlay.setVisibility(View.GONE);
            return;
        }

        LoginAdmRequestDTO loginAdmRequest = new LoginAdmRequestDTO(email, password);

        Call<LoginAdmResponseDTO> call = admService.login(loginAdmRequest);

        call.enqueue(new Callback<LoginAdmResponseDTO>() {
            @Override
            public void onResponse(Call<LoginAdmResponseDTO> call, Response<LoginAdmResponseDTO> response) {
                loadingOverlay.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    LoginAdmResponseDTO tokenResponse = response.body();

                    requireContext()
                            .getSharedPreferences("app", Context.MODE_PRIVATE)
                            .edit()
                            .putString("jwt", tokenResponse.getToken())
                            .apply();

                    ToastHelper.showFeedbackToast(requireContext(), "success",
                            "Login realizado", "Token salvo com sucesso.");

                    startActivity(new Intent(requireContext(), MainActivity.class));
                    requireActivity().finish();

                } else {
                    ToastHelper.showFeedbackToast(requireContext(), "info",
                            "Credenciais inválidas", "E-mail ou senha incorretos.");
                }
            }

            @Override
            public void onFailure(Call<LoginAdmResponseDTO> call, Throwable t) {
                loadingOverlay.setVisibility(View.GONE);
                ToastHelper.showFeedbackToast(requireContext(), "error",
                        "Erro de conexão", "Não foi possível se conectar ao servidor.");
            }
        });
    }

    private String unmask(String s) {
        return s.replaceAll("[^0-9]", "");
    }

    private void setupCpfMask(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) {
                    isUpdating = false;
                    return;
                }

                String raw = unmask(s.toString());
                StringBuilder masked = new StringBuilder();

                int i = 0;
                for (char m : mask.toCharArray()) {
                    if (m != '#') {
                        if (raw.length() > i) masked.append(m);
                        else break;
                    } else {
                        if (raw.length() > i) masked.append(raw.charAt(i++));
                        else break;
                    }
                }

                isUpdating = true;
                int selection = masked.length();
                editText.setText(masked.toString());
                editText.setSelection(selection <= editText.getText().length() ? selection : editText.getText().length());
            }
        });
    }
}
