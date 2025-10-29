package com.example.kronosprojeto.ui.SplashScreen;

import android.content.Intent;
import android.os.Bundle;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kronosprojeto.R;
import com.example.kronosprojeto.ui.Login.LoginActivity;

public class SplashScreen extends AppCompatActivity {
    ImageView clockImage;
    TextView finalText;
    ObjectAnimator rotation;

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

        new Handler().postDelayed(() -> {
            rotation.cancel();

            AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
            fadeOut.setDuration(1000);
            fadeOut.setFillAfter(true);
            clockImage.startAnimation(fadeOut);

            finalText.setVisibility(View.VISIBLE);
            AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
            fadeIn.setDuration(1000);
            finalText.startAnimation(fadeIn);
            new Handler().postDelayed(() -> {
                Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }, 1000);

        }, 2000);
    }
}