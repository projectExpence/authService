package org.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Service
public class GmailOAuthService {
    @Value("${gmail.client-id}")
    private String clientId;

    @Value("${gmail.client-secret}")
    private String clientSecret;

    @Value("${gmail.refresh-token}")
    private String refreshToken;

    public String getAccessToken() throws Exception{
        String tokenEndpoint = "https://oauth2.googleapis.com/token";
        String requestBody = "client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&refresh_token=" + refreshToken +
                "&grant_type=refresh_token";

        URI uri = URI.create(tokenEndpoint);
        HttpURLConnection conn =(HttpURLConnection) uri.toURL().openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        try(OutputStream os = conn.getOutputStream()){
            os.write(requestBody.getBytes(StandardCharsets.UTF_8));
        }
        ObjectMapper mapper = new ObjectMapper();
        JsonNode response = mapper.readTree(conn.getInputStream());

        return response.get("access_token").asText();
    }
}
