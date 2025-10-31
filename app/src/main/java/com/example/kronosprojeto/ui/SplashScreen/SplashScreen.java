package com.example.kronosprojeto.ui.SplashScreen;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.LinearInterpolator;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kronosprojeto.MainActivity;
import com.example.kronosprojeto.R;
import com.example.kronosprojeto.config.RetrofitClientSQL;
import com.example.kronosprojeto.dto.UserResponseDto;
import com.example.kronosprojeto.service.UserService;
import com.example.kronosprojeto.ui.Login.LoginActivity;
import com.example.kronosprojeto.utils.ToastHelper;

import java.net.SocketTimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashScreen extends AppCompatActivity {
    private ImageView clockImage;
    private TextView finalText;
    private ObjectAnimator rotation;

    private final int SPLASH_MIN_DURATION = 2000; // tempo mínimo em ms
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        clockImage = findViewById(R.id.clockImage);
        finalText = findViewById(R.id.finalText);

        rotation = ObjectAnimator.ofFloat(clockImage, "rotation", 0f, 360f);
        rotation.setDuration(2000);
        rotation.setRepeatCount(ValueAnimator.INFINITE);
        rotation.setInterpolator(new LinearInterpolator());
        rotation.start();

        startTime = System.currentTimeMillis();

        SharedPreferences prefs = getSharedPreferences("app", MODE_PRIVATE);
        String token = prefs.getString("jwt", null);
        String cpf = prefs.getString("cpf", null);

        if (token != null && cpf != null) {
            validarSessao(token, cpf);
        } else {
            new Handler().postDelayed(() -> abrirLogin(), SPLASH_MIN_DURATION);
        }
    }

    private void validarSessao(String token, String cpf) {
        UserService userService = RetrofitClientSQL.createService(UserService.class);
        Call<UserResponseDto> call = userService.getUserByCPF("Bearer " + token, cpf);

        call.enqueue(new Callback<UserResponseDto>() {
            @Override
            public void onResponse(Call<UserResponseDto> call, Response<UserResponseDto> response) {
                long elapsed = System.currentTimeMillis() - startTime;
                long remaining = Math.max(0, SPLASH_MIN_DURATION - elapsed);

                new Handler().postDelayed(() -> {
                    rotation.cancel();
                    if (response.isSuccessful() && response.body() != null) {
                        animarSaida();
                        abrirMain();
                    } else {
                        animarSaida();
                        abrirLogin();
                    }
                }, remaining);
            }

            @Override
            public void onFailure(Call<UserResponseDto> call, Throwable t) {
                long elapsed = System.currentTimeMillis() - startTime;
                long remaining = Math.max(0, SPLASH_MIN_DURATION - elapsed);

                new Handler().postDelayed(() -> {
                    rotation.cancel();
                    if (t instanceof SocketTimeoutException) {
                        ToastHelper.showFeedbackToast(getApplicationContext(), "error", "Erro de conexão", "Tempo de resposta excedido");
                    } else {
                        ToastHelper.showFeedbackToast(getApplicationContext(), "error", "Erro", "Falha ao conectar ao servidor");
                    }
                    animarSaida();
                    abrirLogin();
                }, remaining);
            }
        });
    }

    private void animarSaida() {
        AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
        fadeOut.setDuration(800);
        fadeOut.setFillAfter(true);
        clockImage.startAnimation(fadeOut);

        finalText.setVisibility(View.VISIBLE);
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(800);
        finalText.startAnimation(fadeIn);
    }

    private void abrirMain() {
        startActivity(new Intent(SplashScreen.this, MainActivity.class));
        finish();
    }

    private void abrirLogin() {
        startActivity(new Intent(SplashScreen.this, LoginActivity.class));
        finish();
    }
}
