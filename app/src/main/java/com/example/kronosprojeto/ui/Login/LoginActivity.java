package com.example.kronosprojeto.ui.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    TextView phoneRecoveryEntrypoint;
    AppCompatButton loginButton;
    EditText cpfInput;
    EditText passwordInput;
    private AuthService authService;

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
    }

    private void login() {
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


                    Toast.makeText(LoginActivity.this, "Login OK", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Credenciais inválidas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}