package com.example.kronosprojeto.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kronosprojeto.R;

public class ToastHelper {

    public static void showFeedbackToast(Context context, String type, String title, String message) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.feedback_toast,
                null); // Aqui não precisamos do findViewById do ToastContainer, porque é usado no XML

        ImageView icon = layout.findViewById(R.id.toast_icon);
        TextView txtTitle = layout.findViewById(R.id.toast_title);
        TextView txtMessage = layout.findViewById(R.id.toast_message);
        View barra = layout.findViewById(R.id.toast_barra);

        txtTitle.setText(title);
        txtMessage.setText(message);

        switch (type) {
            case "success":
                icon.setImageResource(R.drawable.check_icon);
                barra.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                break;
            case "error":
                icon.setImageResource(R.drawable.error_icon);
                barra.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F44336")));
                break;
            case "info":
                icon.setImageResource(R.drawable.info_icon);
                barra.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFC107")));
                break;
        }

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 30);

        layout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_in_top));
        toast.show();

        // Animação de saída
        new Handler().postDelayed(() -> layout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.slide_out_top)), 2500);
    }
}
