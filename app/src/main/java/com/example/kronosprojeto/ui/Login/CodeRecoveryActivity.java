package com.example.kronosprojeto.ui.Login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.kronosprojeto.R;

public class CodeRecoveryActivity extends AppCompatActivity {

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
        EditText et1 = findViewById(R.id.otp1);
        EditText et2 = findViewById(R.id.otp2);
        EditText et3 = findViewById(R.id.otp3);
        EditText et4 = findViewById(R.id.otp4);

        et1.addTextChangedListener(new GenericTextWatcher(et1, et2));
        et2.addTextChangedListener(new GenericTextWatcher(et2, et3));
        et3.addTextChangedListener(new GenericTextWatcher(et3, et4));
        et4.addTextChangedListener(new GenericTextWatcher(et4, null));
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
    public class GenericTextWatcher implements TextWatcher {
        private EditText currentView;
        private EditText nextView;

        public GenericTextWatcher(EditText currentView, EditText nextView) {
            this.currentView = currentView;
            this.nextView = nextView;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.length() == 1 && nextView != null) {
                nextView.requestFocus();
            }
        }


    }

}