package com.example.kronosprojeto.ui.RestrictArea;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.example.kronosprojeto.R;
import com.example.kronosprojeto.config.RetrofitClientSQL;
import com.example.kronosprojeto.dto.LoginAdmRequestDTO;
import com.example.kronosprojeto.dto.LoginAdmResponseDTO;
import com.example.kronosprojeto.service.AdministratorService;
import com.example.kronosprojeto.utils.ToastHelper;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestrictLoginFragment extends Fragment {

    private AppCompatButton loginButton;
    private EditText txtEmail;
    private EditText passwordInput;
    private AdministratorService admService;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restrict_login, container, false);

        txtEmail = view.findViewById(R.id.txtEmail);
        passwordInput = view.findViewById(R.id.passwordInput);
        loginButton = view.findViewById(R.id.loginButton);

        admService = RetrofitClientSQL.createService(AdministratorService.class);

        loginButton.setOnClickListener(v -> login(v));

        ImageView imgBack = view.findViewById(R.id.imgBack);

        imgBack.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.HomeFragment);
        });

        return view;
    }

    private void login(View view) {
        String email = txtEmail.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            ToastHelper.showFeedbackToast(requireContext(), "warning",
                    "Campos obrigatórios", "Preencha todos os campos antes de continuar.");
            return;
        }



        LoginAdmRequestDTO loginAdmRequest = new LoginAdmRequestDTO(email, password);
        Call<LoginAdmResponseDTO> call = admService.login(loginAdmRequest);

        call.enqueue(new Callback<LoginAdmResponseDTO>() {
            @Override
            public void onResponse(Call<LoginAdmResponseDTO> call, Response<LoginAdmResponseDTO> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginAdmResponseDTO tokenResponse = response.body();

                    requireContext().getSharedPreferences("app", Context.MODE_PRIVATE)
                            .edit()
                            .putString("jwt", tokenResponse.getToken())
                            .apply();

                    ToastHelper.showFeedbackToast(requireContext(), "success",
                            "Login realizado", "Token salvo com sucesso.");

                    NavController navController = Navigation.findNavController(requireView());
                    navController.navigate(R.id.action_RestrictLogin_to_BI);

                } else {
                    ToastHelper.showFeedbackToast(requireContext(), "info",
                            "Credenciais inválidas", "E-mail ou senha incorretos.");
                }
            }

            @Override
            public void onFailure(Call<LoginAdmResponseDTO> call, Throwable t) {
                ToastHelper.showFeedbackToast(requireContext(), "error",
                        "Erro de conexão", "Não foi possível se conectar ao servidor.");
            }
        });
    }
}
