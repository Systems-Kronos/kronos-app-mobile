package com.example.kronosprojeto.ui.Login;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kronosprojeto.R;
import com.example.kronosprojeto.utils.SendSMS;
import com.example.kronosprojeto.utils.ToastHelper;

public class CodeRecoveryActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_SMS = 1;
    private static final long COUNTDOWN_MILLIS = 60_000;

    private EditText et1, et2, et3, et4;
    private TextView txtSendTo;
    private TextView txtTimeReesend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_code_recovery);

        if (findViewById(R.id.main) != null) {
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }

        txtSendTo = findViewById(R.id.txtSendTo);
        txtTimeReesend = findViewById(R.id.txtTimeReesend);
        et1 = findViewById(R.id.otp1);
        et2 = findViewById(R.id.otp2);
        et3 = findViewById(R.id.otp3);
        et4 = findViewById(R.id.otp4);

        String phone = getSharedPreferences("app", MODE_PRIVATE).getString("phone", null);
        if (phone == null) {
            return;
        }
        if (phone != null && phone.length() >= 4 && txtSendTo != null) {
            String ultimos4 = phone.substring(phone.length() - 4);
            String maskedPhone = phone.substring(0, phone.length() - 4).replaceAll("\\d", "*");
            txtSendTo.setText("SMS enviado para o telefone: " + maskedPhone + ultimos4);
        }

        if (et1 != null && et2 != null && et3 != null && et4 != null) {
            et1.addTextChangedListener(new GenericTextWatcher(et1, et2));
            et2.addTextChangedListener(new GenericTextWatcher(et2, et3));
            et3.addTextChangedListener(new GenericTextWatcher(et3, et4));
            et4.addTextChangedListener(new GenericTextWatcher(et4, null));
            et4.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 1) verificarCodigo();
                }
            });
            configurarBackspace();
        }

        iniciarContador();
    }

    private boolean hasSmsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestSmsPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.SEND_SMS},
                PERMISSION_REQUEST_SMS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissão de SMS concedida", Toast.LENGTH_SHORT).show();
            } else {
                ToastHelper.showFeedbackToast(this, "error", "PERMISSÃO NEGADA", "Não será possível enviar SMS sem permissão");
            }
        }
    }

    private void iniciarContador() {
        if (txtTimeReesend == null) return;

        txtTimeReesend.setEnabled(false);
        new CountDownTimer(COUNTDOWN_MILLIS, 1000) {
            public void onTick(long millisUntilFinished) {
                txtTimeReesend.setText("Reenviar código em " + millisUntilFinished / 1000 + " segundos");
            }

            public void onFinish() {
                txtTimeReesend.setText("Clique aqui para reenviar SMS");
                txtTimeReesend.setEnabled(true);
                txtTimeReesend.setOnClickListener(v -> {
                    String telefone = getIntent().getStringExtra("telefone");
                    if (telefone == null || telefone.isEmpty()) {
                        Log.e("SMS_DEBUG", "Telefone inválido ou nulo");
                        ToastHelper.showFeedbackToast(getApplicationContext(), "error", "ERRO", "Telefone inválido");
                        return;
                    }

                    int novoCodigo = (int) (Math.random() * 9000) + 1000;
                    Log.d("SMS_DEBUG", "Código gerado: " + novoCodigo + " para telefone: " + telefone);

                    if (!hasSmsPermission()) {
                        Log.d("SMS_DEBUG", "Permissão de SMS negada, solicitando...");
                        requestSmsPermission();
                        return;
                    } else {
                        Log.d("SMS_DEBUG", "Permissão de SMS concedida");
                    }

                    try {
                        Log.d("SMS_DEBUG", "Tentando enviar SMS...");
                        SendSMS.enviarSMS(CodeRecoveryActivity.this, telefone, novoCodigo);
                        Log.d("SMS_DEBUG", "SMS enviado via SendSMS");

                        getSharedPreferences("app", MODE_PRIVATE)
                                .edit()
                                .putInt("verification_code", novoCodigo)
                                .apply();
                        Log.d("SMS_DEBUG", "Código salvo nas SharedPreferences");

                        iniciarContador();

                    } catch (Exception e) {
                        Log.e("SMS_DEBUG", "Erro ao enviar SMS: " + e.getMessage(), e);
                        ToastHelper.showFeedbackToast(getApplicationContext(), "error", "ERRO", "Não foi possível enviar o SMS");
                    }
                });

            }
        }.start();
    }

    private void configurarBackspace() {
        if (et1 == null || et2 == null || et3 == null || et4 == null) return;
        et2.setOnKeyListener((v, keyCode, event) -> keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN && et2.getText().toString().isEmpty() ? et1.requestFocus() : false);
        et3.setOnKeyListener((v, keyCode, event) -> keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN && et3.getText().toString().isEmpty() ? et2.requestFocus() : false);
        et4.setOnKeyListener((v, keyCode, event) -> keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN && et4.getText().toString().isEmpty() ? et3.requestFocus() : false);
    }

    private void verificarCodigo() {
        int codigoSalvo = getSharedPreferences("app", MODE_PRIVATE)
                .getInt("verification_code", -1);

        if (et1 == null || et2 == null || et3 == null || et4 == null) return;

        String codigoDigitado = et1.getText().toString() +
                et2.getText().toString() +
                et3.getText().toString() +
                et4.getText().toString();

        try {
            int codigoUsuario = Integer.parseInt(codigoDigitado);

            if (codigoUsuario == codigoSalvo) {
                ToastHelper.showFeedbackToast(this,
                        "success", "SUCESSO", "Código verificado com sucesso!");

                Intent intent = new Intent(CodeRecoveryActivity.this, PasswordRedefinitionActivity.class);
                startActivity(intent);
                finish();

            }
            else {
                ToastHelper.showFeedbackToast(this,
                        "error", "CÓDIGO INCORRETO", "Código incorreto. Tente novamente");
            }
        } catch (NumberFormatException e) {
            ToastHelper.showFeedbackToast(this,
                    "error", "CÓDIGO INCORRETO", "Código incorreto. Tente novamente");
        }
    }

    public class GenericTextWatcher implements TextWatcher {
        private EditText currentView, nextView;
        public GenericTextWatcher(EditText currentView, EditText nextView) { this.currentView = currentView; this.nextView = nextView; }
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override public void afterTextChanged(Editable s) {
            if (s.length() == 1 && nextView != null) nextView.requestFocus();
        }
    }
}
