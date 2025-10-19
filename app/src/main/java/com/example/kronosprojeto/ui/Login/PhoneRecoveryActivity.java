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
import android.widget.TextView;
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
import com.example.kronosprojeto.utils.ToastHelper;

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

        String initialCpf = getIntent().getStringExtra("cpf_first_access");
        if(initialCpf != null && !initialCpf.isEmpty()) {
            cpfInput.setText(initialCpf);
            sendSMS();
        }
    }

    private void sendSMS() {
        Log.d(TAG, "Botão pressionado — iniciando verificação de CPF...");

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        } else {
            String cpf = cpfInput.getText().toString().trim();

            if (cpf.isEmpty() || cpf.length() != 14) {
                ToastHelper.showFeedbackToast(getApplicationContext(),"info","CREDENCIAIS INVÁLIDAS","Digite um CPF válido");
                return;
            }

            String token = getSharedPreferences("app", MODE_PRIVATE).getString("jwt", null);

            if (token == null) {
                return;
            }

            try {
                String cpfEncoded = URLEncoder.encode(cpf, StandardCharsets.UTF_8.toString());

                UserService userService = RetrofitClientSQL.createService(UserService.class);
                Call<String> call = userService.getTelefoneByCpf(cpfEncoded);

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String telefone = response.body();

                            if (telefone == null || telefone.isEmpty()) {
                                ToastHelper.showFeedbackToast(getApplicationContext(),
                                        "info", "CREDENCIAIS INVÁLIDAS", "Telefone não encontrado");
                                return;
                            }

                            int codigo = (int) (Math.random() * 9000) + 1000;
                            SendSMS.enviarSMS(PhoneRecoveryActivity.this, telefone, codigo);
                            Intent intent = new Intent(PhoneRecoveryActivity.this, CodeRecoveryActivity.class);
                            intent.putExtra("telefone", telefone);
                            startActivity(intent);
                        } else {
                            ToastHelper.showFeedbackToast(getApplicationContext(),
                                    "error", "ERRO", "Erro ao buscar telefone (" + response.code() + ")");
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        ToastHelper.showFeedbackToast(getApplicationContext(),
                                "error", "ERRO DE CONEXÃO", t.getMessage());
                    }
                });

            } catch (Exception e) {
                ToastHelper.showFeedbackToast(getApplicationContext(),"error","Erro ao processar CPF","Erro de conexão com o servidor: " +  e.getMessage() );
            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult chamado — código: " + requestCode);
    }
}
