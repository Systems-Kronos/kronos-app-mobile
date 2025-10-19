package com.example.kronosprojeto.ui.Login;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        } else {
            String cpf = cpfInput.getText().toString().trim();

            if (cpf.isEmpty() || cpf.length() != 14) {
                Toast.makeText(this, "Digite um CPF válido", Toast.LENGTH_SHORT).show();
                return;
            }

            String token = getSharedPreferences("app", MODE_PRIVATE).getString("jwt", null);
            if (token == null) {
                Toast.makeText(this, "Token não encontrado. Faça login novamente.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                // Encode para evitar problemas com "." e "-"
                String cpfEncoded = URLEncoder.encode(cpf, StandardCharsets.UTF_8.toString());

                UserService userService = RetrofitClientSQL.createService(UserService.class);
                Call<UserResponseDto> call = userService.getUserByCPF("Bearer " + token, cpfEncoded);

                call.enqueue(new Callback<UserResponseDto>() {
                    @Override
                    public void onResponse(Call<UserResponseDto> call, Response<UserResponseDto> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String telefone = response.body().getTelefone();

                            if (telefone == null || telefone.isEmpty()) {
                                Toast.makeText(PhoneRecoveryActivity.this, "Telefone não cadastrado para este usuário", Toast.LENGTH_SHORT).show();
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
                                e.printStackTrace();
                            }

                            Toast.makeText(PhoneRecoveryActivity.this,
                                    "Erro API (" + response.code() + "): " + errorMsg,
                                    Toast.LENGTH_LONG).show();
                            android.util.Log.e("API_ERROR", "Response code: " + response.code() + " | Body: " + errorMsg);
                        }
                    }

                    @Override
                    public void onFailure(Call<UserResponseDto> call, Throwable t) {
                        android.util.Log.e("API_ERROR", "Falha total: " + t.getMessage(), t);
                        Toast.makeText(PhoneRecoveryActivity.this,
                                "Erro de conexão com o servidor: " + t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e) {
                android.util.Log.e("API_ERROR", "Erro ao codificar CPF", e);
                Toast.makeText(this, "Erro ao processar CPF: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void enviarSmsParaTelefone(String phoneNumber) {
        try {
            if (!phoneNumber.startsWith("+")) {
                phoneNumber = "+55" + phoneNumber.replaceAll("[^\\d]", "");
            }

            Intent telaAtual = new Intent(this, PhoneRecoveryActivity.class);
            PendingIntent pi = PendingIntent.getActivity(this, 0, telaAtual, PendingIntent.FLAG_IMMUTABLE);

            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, "Código de verificação: 123456", pi, null);

            Toast.makeText(this, "SMS enviado para " + phoneNumber, Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(PhoneRecoveryActivity.this, CodeRecoveryActivity.class);
            startActivity(intent);

        } catch (Exception e) {
            android.util.Log.e("API_ERROR", "Erro enviando SMS", e);
            Toast.makeText(this, "Falha ao enviar SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
