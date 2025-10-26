package com.example.kronosprojeto.config;

import android.content.Context;
import com.cloudinary.android.MediaManager;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryManager {
    private static boolean initialized = false;

    public static void init(Context context) {
        if (!initialized) {
            Map<String, Object> config = new HashMap<>();
            config.put("cloud_name", "dblwo3rra");
            MediaManager.init(context, config);
            initialized = true;
        }
    }
}
