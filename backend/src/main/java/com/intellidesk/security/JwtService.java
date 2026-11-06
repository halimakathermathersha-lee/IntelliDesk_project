package com.intellidesk.security;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
    public class JwtService {
      @Value("${intellidesk.jwt.secret}")
      private String secret;

      @Value("${intellidesk.jwt.expiration-ms}")
      private long expiration;

       private SecretKey key(){

           return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
       }

       public String createToken(String email,String role){
          return Jwts.builder().subject(email)
                  .claim("role",role)
                  .issuedAt(new Date()).expiration(new Date(System.currentTimeMillis()+expiration))
                  .signWith(key()).compact();
       }

       public String getEmail(String token){
           return Jwts.parser()
                   .verifyWith(key())
                   .build()
                   .parseSignedClaims(token)
                   .getPayload()
                   .getSubject();
       }
   }
