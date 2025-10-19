package com.example.kronosprojeto.ui.Login;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kronosprojeto.R;
import com.example.kronosprojeto.config.RetrofitClientSQL;
import com.example.kronosprojeto.dto.UserResponseDto;
import com.example.kronosprojeto.service.UserService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhoneRecoveryActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 1;
    private static final String TAG = "PhoneRecovery";
    private EditText cpfInput;
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
        setContentView(R.layout.activity_phone_recovery);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        cpfInput = findViewById(R.id.phoneInput);
        setupCpfMask(cpfInput);

        AppCompatButton verifyButton = findViewById(R.id.verifyButton);
        verifyButton.setOnClickListener(v -> sendSMS());
    }

    private void sendSMS() {
        Log.d(TAG, "Botão pressionado — iniciando verificação de CPF...");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Permissão de SMS ainda não concedida.");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        } else {
            String cpf = cpfInput.getText().toString().trim();
            Log.d(TAG, "CPF digitado (com máscara): " + cpf);

            if (cpf.isEmpty() || cpf.length() != 14) {
                Toast.makeText(this, "Digite um CPF válido", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "CPF inválido: " + cpf);
                return;
            }

            String token = getSharedPreferences("app", MODE_PRIVATE).getString("jwt", null);
            Log.d(TAG, "Token JWT recuperado: " + (token != null ? token.substring(0, Math.min(token.length(), 20)) + "..." : "NULO"));

            if (token == null) {
                Toast.makeText(this, "Token não encontrado. Faça login novamente.", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Token JWT não encontrado no SharedPreferences.");
                return;
            }

            try {
                String cpfEncoded = URLEncoder.encode(cpf, StandardCharsets.UTF_8.toString());
                Log.d(TAG, "CPF codificado para URL: " + cpfEncoded);

                UserService userService = RetrofitClientSQL.createService(UserService.class);
                Call<UserResponseDto> call = userService.getUserByCPF("Bearer " + token, cpfEncoded);

                Log.d(TAG, "Iniciando chamada Retrofit GET /api/usuario/selecionarCpf/" + cpfEncoded);

                call.enqueue(new Callback<UserResponseDto>() {
                    @Override
                    public void onResponse(Call<UserResponseDto> call, Response<UserResponseDto> response) {
                        Log.d(TAG, "onResponse chamado | Código HTTP: " + response.code());
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d(TAG, "Requisição bem-sucedida. Corpo recebido: " + response.body().toString());
                            String telefone = response.body().getTelefone();
                            Log.d(TAG, "Telefone retornado: " + telefone);

                            if (telefone == null || telefone.isEmpty()) {
                                Toast.makeText(PhoneRecoveryActivity.this, "Telefone não cadastrado para este usuário", Toast.LENGTH_SHORT).show();
                                Log.w(TAG, "Usuário encontrado, mas sem telefone cadastrado.");
                                return;
                            }

                            enviarSmsParaTelefone(telefone);
                        } else {
                            String errorMsg = "Usuário não encontrado";
                            try {
                                if (response.errorBody() != null) {
                                    errorMsg = response.errorBody().string();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "Erro ao ler corpo de erro", e);
                            }

                            Toast.makeText(PhoneRecoveryActivity.this,
                                    "Erro API (" + response.code() + "): " + errorMsg,
                                    Toast.LENGTH_LONG).show();

                            Log.e(TAG, "Erro API: Código " + response.code() + " | Body: " + errorMsg);
                            Log.e(TAG, "Headers da resposta: " + response.headers());
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponseDto> call, Throwable t) {
                        Log.e(TAG, "Falha total na chamada Retrofit: " + t.getMessage(), t);
                        Toast.makeText(PhoneRecoveryActivity.this,
                                "Erro de conexão com o servidor: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Erro ao codificar CPF ou iniciar requisição", e);
                Toast.makeText(this, "Erro ao processar CPF: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void enviarSmsParaTelefone(String phoneNumber) {
        Log.d(TAG, "Enviando SMS para: " + phoneNumber);
        try {
            if (!phoneNumber.startsWith("+")) {
                phoneNumber = "+55" + phoneNumber.replaceAll("[^\\d]", "");
            }

            int codigo = (int) (Math.random() * 9000) + 1000;
            String mensagem = "Código de verificação: " + codigo;
            Log.d(TAG, "Código gerado: " + codigo);

            Intent telaCodigo = new Intent(this, CodeRecoveryActivity.class);
            PendingIntent pi = PendingIntent.getActivity(this, 0, telaCodigo, PendingIntent.FLAG_IMMUTABLE);

            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, mensagem, pi, null);

            Toast.makeText(this, "SMS enviado para " + phoneNumber, Toast.LENGTH_SHORT).show();
            Log.d(TAG, "SMS enviado com sucesso para " + phoneNumber);

            getSharedPreferences("app", MODE_PRIVATE)
                    .edit()
                    .putInt("codigo_verificacao", codigo)
                    .apply();

            Intent intent = new Intent(PhoneRecoveryActivity.this, CodeRecoveryActivity.class);
            startActivity(intent);

        } catch (Exception e) {
            Log.e(TAG, "Erro enviando SMS", e);
            Toast.makeText(this, "Falha ao enviar SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult chamado — código: " + requestCode);
    }
}
