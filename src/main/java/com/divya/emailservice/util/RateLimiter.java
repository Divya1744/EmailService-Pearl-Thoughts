package com.divya.emailservice.util;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class RateLimiter {

    //Limit: 6 emails per user per 60 seconds
    private final int limit = 6;
    private final long windowMs = 60000;

    private final Map<String, List<Long>> rateMap = new HashMap<>();

    public synchronized boolean isAllowed(String recipient) {

        long now = System.currentTimeMillis();
        List<Long> timestamps = rateMap.getOrDefault(recipient, new ArrayList<>());

        // Remove timestamps older than the window
        timestamps.removeIf(t -> now - t > windowMs);

        System.out.println("Checking rate limit for: " + recipient + ", attempts: " + timestamps.size());

        // Allow only if the number of recent timestamps is below limit
        if (timestamps.size() >= limit) {
            return false;
        }
        timestamps.add(now);  //Record the new attempt
        rateMap.put(recipient, timestamps);
        return true;
    }
}
