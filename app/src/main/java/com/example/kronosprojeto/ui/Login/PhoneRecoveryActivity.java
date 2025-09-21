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

public class PhoneRecoveryActivity extends AppCompatActivity {

    private static final int SMS_PERMISSION_CODE = 1;
    private EditText phoneInput;

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

        phoneInput = findViewById(R.id.phoneInput);

        phoneInput.addTextChangedListener(new TextWatcher() {
            boolean isUpdating;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) {
                    isUpdating = false;
                    return;
                }

                String str = s.toString().replaceAll("[^\\d]", "");
                String formatted = "";

                if (str.length() > 11)
                    str = str.substring(0, 11);

                int len = str.length();

                if (len > 0)
                    formatted += "(";

                if (len >= 1)
                    formatted += str.substring(0, Math.min(2, len));

                if (len >= 3)
                    formatted += ") ";

                if (len >= 3 && len <= 6)
                    formatted += str.substring(2, len);
                else if (len > 6)
                    formatted += str.substring(2, 7) + "-" + str.substring(7, len);

                isUpdating = true;
                phoneInput.setText(formatted);
                phoneInput.setSelection(formatted.length());
            }
        });

        AppCompatButton verifyButton = findViewById(R.id.verifyButton);
        verifyButton.setOnClickListener(v -> {
            sendSMS();
            Intent intent = new Intent(PhoneRecoveryActivity.this, CodeRecoveryActivity.class);
            startActivity(intent);
        });
    }

    private void sendSMS() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS}, SMS_PERMISSION_CODE);
        } else {
            try {
                String phoneNumber = phoneInput.getText().toString().replaceAll("[^\\d]", "");
                if (phoneNumber.isEmpty()) {
                    Toast.makeText(this, "Digite um número válido", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent telaAtual = new Intent(this, PhoneRecoveryActivity.class);
                PendingIntent pi = PendingIntent.getActivity(this, 0, telaAtual, PendingIntent.FLAG_IMMUTABLE);
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage(phoneNumber, null, "123 321 tudo sobre 2", pi, null);

                Toast.makeText(this, "SMS enviado com sucesso!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Falha ao enviar SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
