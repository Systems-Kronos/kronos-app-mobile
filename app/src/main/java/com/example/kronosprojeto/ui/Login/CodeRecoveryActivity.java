package com.example.kronosprojeto.ui.Login;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kronosprojeto.R;
import com.example.kronosprojeto.utils.ToastHelper;

public class CodeRecoveryActivity extends AppCompatActivity {

    private EditText et1, et2, et3, et4;
    private TextView txtSendTo;
    private TextView txtTimeReesend;

    private static final long COUNTDOWN_MILLIS = 60_000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_code_recovery);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtSendTo = findViewById(R.id.txtSendTo);
        txtTimeReesend = findViewById(R.id.txtTimeReesend);
        et1 = findViewById(R.id.otp1);
        et2 = findViewById(R.id.otp2);
        et3 = findViewById(R.id.otp3);
        et4 = findViewById(R.id.otp4);

        String telefone = getIntent().getStringExtra("telefone");
        if (telefone != null && telefone.length() >= 4) {
            String ultimos4 = telefone.substring(telefone.length() - 4);
            String maskedPhone = telefone.substring(0, telefone.length() - 4).replaceAll("\\d", "*");
            txtSendTo.setText("SMS enviado para o telefone: " + maskedPhone + ultimos4);
        }

        et1.addTextChangedListener(new GenericTextWatcher(et1, et2));
        et2.addTextChangedListener(new GenericTextWatcher(et2, et3));
        et3.addTextChangedListener(new GenericTextWatcher(et3, et4));
        et4.addTextChangedListener(new GenericTextWatcher(et4, null));

        et4.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    verificarCodigo();
                }
            }
        });

        configurarBackspace();

        iniciarContador();
    }

    private void configurarBackspace() {
        et2.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == android.view.KeyEvent.KEYCODE_DEL &&
                    event.getAction() == android.view.KeyEvent.ACTION_DOWN &&
                    et2.getText().toString().isEmpty()) {
                et1.requestFocus();
                return true;
            }
            return false;
        });

        et3.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == android.view.KeyEvent.KEYCODE_DEL &&
                    event.getAction() == android.view.KeyEvent.ACTION_DOWN &&
                    et3.getText().toString().isEmpty()) {
                et2.requestFocus();
                return true;
            }
            return false;
        });

        et4.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == android.view.KeyEvent.KEYCODE_DEL &&
                    event.getAction() == android.view.KeyEvent.ACTION_DOWN &&
                    et4.getText().toString().isEmpty()) {
                et3.requestFocus();
                return true;
            }
            return false;
        });
    }

    private void verificarCodigo() {
        int codigoSalvo = getSharedPreferences("app", MODE_PRIVATE)
                .getInt("verification_code", -1);

        String codigoDigitado = et1.getText().toString() +
                et2.getText().toString() +
                et3.getText().toString() +
                et4.getText().toString();

        try {
            int codigoUsuario = Integer.parseInt(codigoDigitado);

            if (codigoUsuario == codigoSalvo) {
                ToastHelper.showFeedbackToast(getApplicationContext(),"success","SUCESSO","Código verificado com sucesso!");

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main, new PasswordRedefinitionFragment())
                        .addToBackStack(null)
                        .commit();

            } else {
                ToastHelper.showFeedbackToast(getApplicationContext(),"error","CÓDIGO INCORRETO","Código incorreto. Tente novamente");
            }
        } catch (NumberFormatException e) {
            ToastHelper.showFeedbackToast(getApplicationContext(),"error","CÓDIGO INCORRETO","Código incorreto. Tente novamente");
        }
    }

    private void iniciarContador() {
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
                    int novoCodigo = (int) (Math.random() * 9000) + 1000;

                    if (telefone != null) {
                        SendSMS.enviarSMS(CodeRecoveryActivity.this, telefone, novoCodigo);
                        getSharedPreferences("app", MODE_PRIVATE)
                                .edit()
                                .putInt("verification_code", novoCodigo)
                                .apply();
                        iniciarContador();
                    }
                });
            }
        }.start();
    }

    public class GenericTextWatcher implements TextWatcher {
        private EditText currentView;
        private EditText nextView;

        public GenericTextWatcher(EditText currentView, EditText nextView) {
            this.currentView = currentView;
            this.nextView = nextView;
        }

        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 1 && nextView != null) {
                nextView.requestFocus();
            }
        }
    }
}
