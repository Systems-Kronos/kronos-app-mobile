package com.example.kronosprojeto.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class NotificationCache {
    private static final String PREFS_NAME = "notification_cache";
    private static final String KEY_IDS = "shown_notifications";

    public static void salvar(Context context, Set<String> ids) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putStringSet(KEY_IDS, ids).apply();
    }

    public static Set<String> carregar(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getStringSet(KEY_IDS, new HashSet<>());
    }
}
