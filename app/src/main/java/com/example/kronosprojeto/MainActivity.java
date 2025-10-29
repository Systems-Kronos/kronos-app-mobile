
package com.example.kronosprojeto;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.Manifest;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.kronosprojeto.config.RetrofitClientNoSQL;
import com.example.kronosprojeto.config.RetrofitClientSQL;
import com.example.kronosprojeto.databinding.ActivityMainBinding;
import com.example.kronosprojeto.dto.UserResponseDto;
import com.example.kronosprojeto.model.Notification;
import com.example.kronosprojeto.service.NotificationService;
import com.example.kronosprojeto.service.UserService;
import com.example.kronosprojeto.ui.SplashScreen.SplashScreen;
import com.example.kronosprojeto.utils.NotificationHelper;
import com.example.kronosprojeto.utils.NotificationProcessor;
import com.example.kronosprojeto.utils.ToastHelper;
import com.example.kronosprojeto.viewmodel.NotificationViewModel;
import com.example.kronosprojeto.viewmodel.UserViewModel;
import com.example.kronosprojeto.workers.NotificationWorker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private NavController navController;
    private UserViewModel userViewModel;
    private NotificationViewModel notificationViewModel;
    private DrawerLayout drawerLayout;
    TextView tvUsername;
    private static final String POST_NOTIF = Manifest.permission.POST_NOTIFICATIONS;

    private ActivityResultLauncher<String> requestNotificationPermission;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view_drawer);

        View headerView = navigationView.getHeaderView(0);


        TextView tvUsername = headerView.findViewById(R.id.tv_username);

        View customView = getLayoutInflater().inflate(R.layout.toolbar_custom, toolbar, false);
        toolbar.addView(customView);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.HomeFragment,
                R.id.CalendarioFragment,
                R.id.PerfilFragment,
                R.id.ChatFragment,
                R.id.NotificationsFragment,
                R.id.assignmentHistoryFragment,
                R.id.details,
                R.id.RestrictBIFragment,
                R.id.RestrictLogin
        )
                .build();

        BottomNavigationView navView = binding.navView;
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        ImageView notificationIcon = customView.findViewById(R.id.notification_logo);
        notificationIcon.setOnClickListener(v ->
                navController.navigate(R.id.NotificationsFragment, null,
                        new androidx.navigation.NavOptions.Builder()
                                .setLaunchSingleTop(true)
                                .setPopUpTo(R.id.mobile_navigation, false)
                                .build())
        );



        ImageView menuIcon = customView.findViewById(R.id.menu_logo);
        menuIcon.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            int startDestination = navController.getGraph().getStartDestinationId();

            if (id == R.id.nav_home) {
                navController.navigate(R.id.HomeFragment, null,
                        new androidx.navigation.NavOptions.Builder()
                                .setLaunchSingleTop(true)
                                .setPopUpTo(startDestination, true)
                                .build());
            } else if (id == R.id.nav_profile) {
                navController.navigate(R.id.PerfilFragment, null,
                        new androidx.navigation.NavOptions.Builder()
                                .setLaunchSingleTop(true)
                                .setPopUpTo(startDestination, true)
                                .build());
            } else if (id == R.id.nav_chat) {
                navController.navigate(R.id.ChatFragment, null,
                        new androidx.navigation.NavOptions.Builder()
                                .setLaunchSingleTop(true)
                                .setPopUpTo(startDestination, true)
                                .build());
            } else if (id == R.id.nav_calendar) {
                navController.navigate(R.id.CalendarioFragment, null,
                        new androidx.navigation.NavOptions.Builder()
                                .setLaunchSingleTop(true)
                                .setPopUpTo(startDestination, true)
                                .build());
            }else if (id == R.id.nav_restrict) {
                navController.navigate(R.id.RestrictLogin,null,
                        new androidx.navigation.NavOptions.Builder()
                                .setLaunchSingleTop(true)
                                .setPopUpTo(startDestination, true)
                                .build());

            } else if (id == R.id.nav_logout) {
                SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
                prefs.edit().clear().apply();


                Intent intent = new Intent(MainActivity.this, SplashScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);


        requestNotificationPermission = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        ToastHelper.showFeedbackToast(this, "success", "SUCESSO", "Notificações ativadas!");
                    } else {
                        ToastHelper.showFeedbackToast(this, "error", "Aviso", "Permissão negada!");
                    }
                }
        );


        verificarPermissaoNotificacao();

        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
        String token = prefs.getString("jwt", null);
        String cpf = prefs.getString("cpf", null);

        if (token != null && cpf != null) {
            UserService userService = RetrofitClientSQL.createService(UserService.class);
            Call<UserResponseDto> call = userService.getUserByCPF("Bearer " + token, cpf);

            call.enqueue(new Callback<UserResponseDto>() {
                @Override
                public void onResponse(Call<UserResponseDto> call, Response<UserResponseDto> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        userViewModel.setUser(response.body());
                        prefs.edit().putString("id", String.valueOf(response.body().getId())).apply();
                        carregarNotificacoes(response.body().getId());

                        TextView title = customView.findViewById(R.id.toolbar_title);
                        title.setText("Olá, " + response.body().getNome().split(" ")[0] + ", como você está?");
                        tvUsername.setText(("Olá, " + response.body().getNome().split(" ")[0] ));
                        toolbar.removeView(customView);
                        toolbar.addView(customView);
                    } else if (response.code() == 403) {
                        startActivity(new Intent(MainActivity.this, SplashScreen.class));
                        finish();
                    }
                }

                @Override
                public void onFailure(Call<UserResponseDto> call, Throwable t) {
                    if (t instanceof SocketTimeoutException) {
                        startActivity(new Intent(MainActivity.this, SplashScreen.class));
                        ToastHelper.showFeedbackToast(getApplicationContext(), "error", "Erro de conexão", "Tempo de resposta excedido");
                    }
                    ToastHelper.showFeedbackToast(getApplicationContext(), "error", "ERRO:", "Não foi possível carregar as tarefas");
                }
            });
        }
    }

    private void carregarNotificacoes(long idUsuario) {
        NotificationService notificationService = RetrofitClientNoSQL.createService(NotificationService.class);
        Call<List<Notification>> callNotificacoes = notificationService.getNotificationsByUserID(idUsuario);

        callNotificacoes.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Notification> lista = response.body();
                    notificationViewModel.setNotifications(lista);
                    NotificationProcessor.processarNotificacoes(MainActivity.this, lista);
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                ToastHelper.showFeedbackToast(getApplicationContext(), "error", "ERRO:", "Não foi carregar as notificações");
                startActivity(new Intent(MainActivity.this, SplashScreen.class));
            }
        });

        NotificationHelper.createNotificationChannel(this);

        PeriodicWorkRequest notificationWorkRequest =
                new PeriodicWorkRequest.Builder(NotificationWorker.class, 1, TimeUnit.MINUTES)
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "notificacoes_worker",
                ExistingPeriodicWorkPolicy.UPDATE,
                notificationWorkRequest
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    private void verificarPermissaoNotificacao() {
        Log.d("PERMISSION_DEBUG", "Status: " +
                ContextCompat.checkSelfPermission(this, POST_NOTIF));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    POST_NOTIF
            ) != PackageManager.PERMISSION_GRANTED) {

                new AlertDialog.Builder(this)
                        .setTitle("Permitir notificações")
                        .setMessage("O app usa notificações para te avisar de novas tarefas. Deseja permitir?")
                        .setPositiveButton("Permitir", (dialog, which) -> {
                            ActivityCompat.requestPermissions(
                                    this,
                                    new String[]{
                                    POST_NOTIF
                                    },
                                    100
                            );
                        })
                        .setNegativeButton("Agora não", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        }

    }
}