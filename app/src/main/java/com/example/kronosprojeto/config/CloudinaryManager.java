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
            config.put("api_key", "iNS0QhgekJZuQqlSBG4MFo3hAac");
            config.put("secure", true);
            config.put("unsigned", true);

            MediaManager.init(context, config);
            initialized = true;
        }
    }
}
