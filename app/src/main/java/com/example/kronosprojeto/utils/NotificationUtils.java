package com.example.kronosprojeto.utils;

import android.content.Context;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.kronosprojeto.R;

public class NotificationUtils {
    public static void mostrarNotificacao(Context context, String mensagem, int id) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notificacoes_id")
                .setSmallIcon(R.drawable.kronoslogoyellowlogin) // seu ícone
                .setContentTitle(mensagem)
                .setContentText("clique para ver mais informações")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat.from(context).notify(id, builder.build());
    }
}
