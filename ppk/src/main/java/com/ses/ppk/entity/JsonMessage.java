package com.ses.ppk.entity;

import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

public class JsonMessage {

    public static ResponseEntity<Map<String, String>> message(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);

        return ResponseEntity.ok(response);
    }
}

