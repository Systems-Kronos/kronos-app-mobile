package com.example.kronosprojeto.workers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.kronosprojeto.config.RetrofitCalenderNoSQL;
import com.example.kronosprojeto.model.Notification;
import com.example.kronosprojeto.service.NotificationService;
import com.example.kronosprojeto.utils.NotificationProcessor;
import com.example.kronosprojeto.utils.NotificationUtils;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class NotificationWorker extends Worker {

    public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        NotificationService service = RetrofitCalenderNoSQL.createService(NotificationService.class);
        Context context = getApplicationContext();

        Log.e("Worker", "ATIVOU");
        SharedPreferences prefs = context.getSharedPreferences("app", Context.MODE_PRIVATE);
        String usuarioIdStr = prefs.getString("id", "0");
        long idUsuario = Long.parseLong(usuarioIdStr);

        Call<List<Notification>> call = service.getNotificationsByUserID(idUsuario);
        try {
            Response<List<Notification>> resp = call.execute(); // síncrono, pois já estamos em background

            if (resp.isSuccessful() && resp.body() != null) {
                List<Notification> notificacoes = resp.body();

                if (!notificacoes.isEmpty()) {
                    NotificationProcessor.processarNotificacoes(context,notificacoes);
                }

                return Result.success();
            } else {
                Log.e("NotificationWorker", "Erro na resposta: " + resp.code());
                return Result.retry();
            }
        } catch (IOException e) {
            Log.e("NotificationWorker", "Erro na chamada", e);
            return Result.retry();
        }
    }
}
