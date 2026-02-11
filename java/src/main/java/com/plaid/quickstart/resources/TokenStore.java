package com.plaid.quickstart.resources;


import java.net.URI;

import redis.clients.jedis.Jedis;


import org.json.JSONObject;

public class TokenStore {
    

    public static void saveToken(String accessToken, String itemId) {
        try (Jedis jedis = new Jedis(URI.create(System.getenv("REDIS_URL")))) {
            if (itemId != null) {
                jedis.set("itemId", itemId);
            }
            if (accessToken != null) {
                jedis.set("accessToken", accessToken);
            }
        } 
      
        
    }

    public static JSONObject loadToken() {
        try (Jedis jedis = new Jedis(URI.create(System.getenv("REDIS_URL")))) {
            String itemId = jedis.get("itemId");
            String accessToken = jedis.get("accessToken");   
            JSONObject userInfo = new JSONObject();
            userInfo.put("itemId", itemId); 
            userInfo.put("accessToken", accessToken); 
            return userInfo; 
        }
        
        
    }

    public static void deleteToken() {
    String redisUrl = System.getenv("REDIS_URL");
    System.out.println("Deleting from Redis URL: " + redisUrl); // Debug log
    
    try (Jedis jedis = new Jedis(URI.create(redisUrl))) {
        // Test connection
        String pong = jedis.ping();
        System.out.println("Redis ping: " + pong); // Should print "PONG"
        
        Long deleted1 = jedis.del("itemId");
        Long deleted2 = jedis.del("accessToken");
        
        System.out.println("Deleted itemId: " + deleted1); // Should be 1 if existed, 0 if not
        System.out.println("Deleted accessToken: " + deleted2); // Should be 1 if existed, 0 if not
        
    } catch (Exception e) {
        System.err.println("Error deleting from Redis: " + e.getMessage());
        e.printStackTrace();
    }
}
}
