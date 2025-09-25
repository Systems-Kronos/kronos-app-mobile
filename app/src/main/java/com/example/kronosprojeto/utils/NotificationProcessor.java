package com.example.kronosprojeto.utils;

import android.content.Context;
import android.util.Log;

import com.example.kronosprojeto.model.Notification;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class NotificationProcessor {
    public static void processarNotificacoes(Context context, List<Notification> notificacoes) {
        Set<String> cache = new HashSet<>(NotificationCache.carregar(context));

        int id = 0;
        for (Notification n : notificacoes) {
            String idUnico = n.getTitulo() + "_" + n.getDescrição();
            Log.e("AAA", idUnico);
            if (!cache.contains(idUnico)) {
                NotificationUtils.mostrarNotificacao(context, n.getTitulo(), id);
                Log.e("AAA","NOVA NOTIFICACAO");
                cache.add(idUnico);
                id++;
            }
        }

        NotificationCache.salvar(context, cache);
    }
}
