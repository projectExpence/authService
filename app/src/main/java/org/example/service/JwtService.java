package org.example.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    public final SecretKey secretKey;

    public JwtService(
            @Value("${jwt.secret.key}") String secretString){ // Inject the secret key from application.properties
        byte[]keyByte = Decoders.BASE64.decode(secretString); // Decode the secret key
        this.secretKey= Keys.hmacShaKeyFor(keyByte); // Create a secret key for JWT
    }


    public <T> T extractClaim(String token, Function<Claims,T>ClaimResolver){
        final Claims claims=extractAllClaims(token);
        return ClaimResolver.apply(claims);
    }

    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token){
        return extractClaim(token,Claims::getExpiration);
    }

    private Boolean isTokenExpire(String token){
        return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails){
        final String username=extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpire(token));
    }
    public String GenerateToken(String email){
        Map<String,Object> claims = new HashMap<>();
        return createToken(claims,email);

    }
    private String createToken(Map<String,Object> claims,String email){ // Create a JWT token
        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+1000*60*30))
                .signWith(secretKey)
                .compact(); // this gives the jtw token
    }
    private Claims extractAllClaims(String token){ // Extract all claims from the token
        return Jwts // Parse the token
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String generateResetToken(String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "password_reset");
        return Jwts.builder()
                .claims(claims)
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000)) // 15 min
                .signWith(secretKey)
                .compact();
    }


    public Boolean validateResetToken(String token, CustomUserDetails customUserDetails) {
        try {
            Claims claims = extractAllClaims(token);

            String type = claims.get("type", String.class);
            if (type == null || !type.equals("password_reset")) {
                return false;
            }

            return !isTokenExpire(token);
        } catch (Exception e) {
            return false;
        }
    }


}
