
package com.example.kronosprojeto;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.example.kronosprojeto.ui.Login.LoginActivity;
import com.example.kronosprojeto.ui.SplashScreen.SplashScreen;
import com.example.kronosprojeto.utils.NotificationHelper;
import com.example.kronosprojeto.utils.NotificationProcessor;
import com.example.kronosprojeto.utils.ToastHelper;
import com.example.kronosprojeto.viewmodel.NotificationViewModel;
import com.example.kronosprojeto.viewmodel.UserViewModel;
import com.example.kronosprojeto.workers.NotificationWorker;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
                R.id.assignmentHistoryFragment,
                R.id.details
        ).build();






        BottomNavigationView navView = binding.navView;
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
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
        notificationViewModel = new ViewModelProvider(this).get(NotificationViewModel.class);

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
                        carregarNotificacoes(response.body().getId());
                        TextView title = customView.findViewById(R.id.toolbar_title);
                        title.setText("Olá, "+ response.body().getNome().split(" ")[0]+ ", como você está? ");
                        toolbar.removeView(customView);
                        toolbar.addView(customView);



                    }else{
                        if (response.code() == 403){
                            Intent intent = new Intent(MainActivity.this, SplashScreen.class);
                            startActivity(intent);

                        }
                    }
                }

                @Override
                public void onFailure(Call<UserResponseDto> call, Throwable t) {
                    ToastHelper.showFeedbackToast(getApplicationContext(),"error","ERRO:","Não foi possível carregar as tarefas");

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
                    Log.e("s", "ID: "+idUsuario);
                    notificationViewModel.setNotifications(response.body());
                    List<Notification> lista = response.body();
                    if (!lista.isEmpty()){
                        for (Notification notification : lista){
                            Log.e("dd",notification.getTitulo());
                        }
                    }

                    notificationViewModel.setNotifications(lista);

                    NotificationProcessor.processarNotificacoes(MainActivity.this, lista);
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                ToastHelper.showFeedbackToast(getApplicationContext(),"error","ERRO:","Não foi carregar as notificações");

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
}