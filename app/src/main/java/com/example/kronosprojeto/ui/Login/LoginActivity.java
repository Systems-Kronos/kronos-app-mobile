package com.example.kronosprojeto.ui.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.widget.AppCompatButton;

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

    private TextView phoneRecoveryEntrypoint;
    private AppCompatButton loginButton;
    private EditText cpfInput;
    private EditText passwordInput;
    private FrameLayout loadingOverlay;
    private AuthService authService;

    private boolean isUpdating = false;
    private final String mask = "###.###.###-##";

    private String unmask(String s) {
        return s.replaceAll("[^0-9]", "");
    }

    private void setupCpfMask(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

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

        // ⚠️ Essencial: define o layout antes de usar findViewById
        setContentView(R.layout.activity_login);

        // EdgeToEdge
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Views
        loadingOverlay = findViewById(R.id.loadingOverlay);
        cpfInput = findViewById(R.id.cpfInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        phoneRecoveryEntrypoint = findViewById(R.id.passwordRecoveryText);

        authService = RetrofitClientSQL.createService(AuthService.class);

        setupCpfMask(cpfInput);

        phoneRecoveryEntrypoint.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, PhoneRecoveryActivity.class);
            startActivity(intent);
        });

        String openFragment = getIntent().getStringExtra("open_fragment");
        if ("password_redefinition".equals(openFragment)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, new PasswordRedefinitionFragment())
                    .commit();
        }

        loginButton.setOnClickListener(v -> login());
    }

    private void login() {
        loadingOverlay.setVisibility(View.VISIBLE);

        String cpf = cpfInput.getText().toString();
        String password = passwordInput.getText().toString();

        if (cpf.isEmpty() || password.isEmpty()) {
            ToastHelper.showFeedbackToast(getApplicationContext(),
                    "info",
                    "Campos vazios",
                    "Preencha CPF e senha!");
            loadingOverlay.setVisibility(View.GONE);
            return;
        }

        LoginRequestDto loginRequest = new LoginRequestDto(cpf, password);
        Call<Token> call = authService.login(loginRequest);

        call.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                loadingOverlay.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    Token token = response.body();

                    getSharedPreferences("app", MODE_PRIVATE)
                            .edit()
                            .putString("jwt", token.getToken())
                            .putString("cpf", cpf)
                            .apply();

                    if (password.equals("senha123")) {
                        Intent intent = new Intent(LoginActivity.this, PasswordRedefinitionActivity.class);
                        startActivity(intent);
                        return;
                    }

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    ToastHelper.showFeedbackToast(getApplicationContext(),
                            "info",
                            "CREDENCIAIS INVÁLIDAS",
                            "CPF e senha não condizem!");
                }
            }

            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                loadingOverlay.setVisibility(View.GONE);
                ToastHelper.showFeedbackToast(getApplicationContext(),
                        "error",
                        "ERRO:",
                        "Ocorreu alguma instabilidade e não foi possível concluir a operação");
            }
        });
    }

}
