package com.example.kronosprojeto.ui.Login;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.kronosprojeto.R;

public class PasswordRedefinitionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_redefinition);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new PasswordRedefinitionFragment())
                    .commit();
        }
    }
}
