package com.example.back_end.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;


@Service
public class JwtService {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long expirationTime;


    public String generateToken(UserDetails userDetails){
        return create(Map.of("role",
                userDetails.getAuthorities().stream()
                        .map(Object::toString)
                        .toList()), userDetails);
    }
    private String create(Map<String, Object> extra, UserDetails userDetails){
        return buildToken(extra, userDetails, expirationTime);
    }

    public String buildToken(Map<String, Object> extraInfo, UserDetails userDetails, long expirationTime){
        return Jwts
                .builder()
                .setClaims(extraInfo)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigninKey(){
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    public String extractEmail(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver){
        final Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    public Claims extractAllClaims(String token){
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigninKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isValidToken(String token, UserDetails userDetails){
        final String email = extractEmail(token);
        return email.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }
    public Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

//    public String generateRefreshToken(User user, Collection<SimpleGrantedAuthority> authorities){
//        Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
//        return JWT.create()
//                .withSubject(user.getEmail())
//                .withExpiresAt(new Date(System.currentTimeMillis() + 50*60*1000))
//                .sign(algorithm);
//    }

    public String generatePasswordResetToken(String email){
        long expiration = System.currentTimeMillis() + 1000 * 60 * 60;
        Date expirationDate = new Date(expiration);
        return Jwts.builder()
                .setExpiration(expirationDate)
                .setSubject(email)
                .signWith(getSigninKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isValidPasswordResetToken(String token){
        return !isTokenExpired(token);
    }
}
