package com.example.kronosprojeto.ui.RestrictArea;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import com.example.kronosprojeto.R;

public class RestrictAreaActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restrict_area);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.restrictNavHost);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
        }
    }

    public NavController getNavController() {
        return navController;
    }
}
