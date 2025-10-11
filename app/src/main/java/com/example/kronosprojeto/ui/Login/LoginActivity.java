package com.example.kronosprojeto.ui.Login;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kronosprojeto.MainActivity;
import com.example.kronosprojeto.R;
import com.example.kronosprojeto.config.RetrofitClientSQL;
import com.example.kronosprojeto.dto.LoginRequestDto;
import com.example.kronosprojeto.model.Token;
import com.example.kronosprojeto.service.AuthService;
import com.example.kronosprojeto.utils.ToastHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    TextView phoneRecoveryEntrypoint;
    AppCompatButton loginButton;
    EditText cpfInput;
    EditText passwordInput;
    private AuthService authService;
    FrameLayout loadingOverlay;

    private boolean isUpdating = false;
    private final String mask = "###.###.###-##";

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadingOverlay= findViewById(R.id.loadingOverlay);

        phoneRecoveryEntrypoint = findViewById(R.id.passwordRecoveryText);
        phoneRecoveryEntrypoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, PhoneRecoveryActivity.class);
                startActivity(intent);
            }
        }

        );
        loginButton = findViewById(R.id.loginButton);

        cpfInput = findViewById(R.id.cpfInput);
        passwordInput = findViewById(R.id.passwordInput);

        authService = RetrofitClientSQL.createService(AuthService.class);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        setupCpfMask(cpfInput);
    }

    private void login() {
        loadingOverlay.setVisibility(View.VISIBLE);

        String cpf = cpfInput.getText().toString();
        String password = passwordInput.getText().toString();

        LoginRequestDto loginRequest = new LoginRequestDto(cpf, password);

        Call<Token> call = authService.login(loginRequest);

        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Token token = response.body();

                    getSharedPreferences("app", MODE_PRIVATE)
                            .edit()
                            .putString("jwt", token.getToken())
                            .apply();
                    getSharedPreferences("app", MODE_PRIVATE)
                            .edit()
                            .putString("cpf", cpf)
                            .apply();



                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    loadingOverlay.setVisibility(View.GONE);

                    startActivity(intent);
                    finish();
                } else {
                    loadingOverlay.setVisibility(View.GONE);
                    ToastHelper.showFeedbackToast(getApplicationContext(),"info","CREDENCIAIS INVÁLIDAS","CPF e senha não condizem!");
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                loadingOverlay.setVisibility(View.GONE);
                ToastHelper.showFeedbackToast(getApplicationContext(),"error","ERRO:","Ocorreu alguma instabilidade e não foi possível concluir a operação");


            }
        });
    }



}