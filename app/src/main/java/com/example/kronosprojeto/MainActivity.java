package com.example.kronosprojeto;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.kronosprojeto.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        View customView = getLayoutInflater().inflate(R.layout.toolbar_custom, toolbar, false);
        toolbar.addView(customView);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.HomeFragment,
                R.id.CalendarioFragment,
                R.id.PerfilFragment,
                R.id.ChatFragment,
                R.id.NotificationsFragment,
                R.id.assignmentHistoryFragment
        ).build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        BottomNavigationView navView = binding.navView;
        NavigationUI.setupWithNavController(navView, navController);

        ImageView notificationIcon = customView.findViewById(R.id.notification_logo);
        notificationIcon.setOnClickListener(v ->
                navController.navigate(R.id.NotificationsFragment, null,
                        new androidx.navigation.NavOptions.Builder()
                                .setLaunchSingleTop(true)
                                .setPopUpTo(R.id.mobile_navigation, false) // Não limpa o grafo inteiro
                                .build())
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
