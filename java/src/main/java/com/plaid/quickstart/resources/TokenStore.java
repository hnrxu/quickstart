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
}
