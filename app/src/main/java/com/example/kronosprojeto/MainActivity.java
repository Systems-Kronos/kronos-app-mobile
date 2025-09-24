package com.example.kronosprojeto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.kronosprojeto.config.RetrofitClientSQL;
import com.example.kronosprojeto.databinding.ActivityMainBinding;
import com.example.kronosprojeto.dto.UserResponseDto;
import com.example.kronosprojeto.service.UserService;
import com.example.kronosprojeto.ui.Login.LoginActivity;
import com.example.kronosprojeto.utils.NotificationHelper;
import com.example.kronosprojeto.viewmodel.UserViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    private UserViewModel userViewModel;

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


        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
        String token = prefs.getString("jwt", null);
        String cpf = prefs.getString("cpf", null);

        if (token != null && cpf != null) {
            UserService userService = RetrofitClientSQL.createService(UserService.class);
            Call<UserResponseDto> call = userService.getUserByCPF("Bearer " + token, cpf);

            call.enqueue(new Callback<UserResponseDto>() {

                public void onResponse(Call<UserResponseDto> call, Response<UserResponseDto> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        userViewModel.setUser(response.body());
                        prefs
                                .edit()
                                .putString("id", String.valueOf( response.body().getId()))
                                .apply();
                    }
                }

                @Override
                public void onFailure(Call<UserResponseDto> call, Throwable t) {
                }
            });
        }


    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}
