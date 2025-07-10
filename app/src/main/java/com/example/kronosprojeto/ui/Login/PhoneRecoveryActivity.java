package com.example.kronosprojeto.ui.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kronosprojeto.R;

public class PhoneRecoveryActivity extends AppCompatActivity {

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
        AppCompatButton verifyButton = findViewById(R.id.verifyButton);
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PhoneRecoveryActivity.this, CodeRecoveryActivity.class);
                startActivity(intent);
            }
        });

        EditText phoneInput = findViewById(R.id.phoneInput);
        phoneInput.addTextChangedListener(new TextWatcher() {
            boolean isUpdating;
            String oldText = "";

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

    }
}