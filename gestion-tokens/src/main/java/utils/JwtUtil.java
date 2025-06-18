package utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.util.Date;

public class
JwtUtil {
    private static final String SECRET_STRING = "clave1234clave1234clave1234clave1234";
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(SECRET_STRING.getBytes());

    public static String generarToken(String nombreUsuario, String rol, Integer idUsuario) {
        return Jwts.builder()
                .setSubject(nombreUsuario)
                .claim("rol", rol)
                .claim("idUsuario", idUsuario)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public static String obtenerUsername(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public static String obtenerRol(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("rol", String.class);
    }

    public static Integer obtenerId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("idUsuario", Integer.class);
    }


    public static boolean validarToken(String token, String nombreUsuario) {
        String usuario = obtenerUsername(token);
        return usuario.equals(nombreUsuario);
    }

    public static boolean estaExpirado(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration().before(new Date());
    }

    public static Claims obtenerClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static boolean validarRolToken(String token, String rolEsperado) {
        Claims claims = obtenerClaims(token);
        String rol = (String) claims.get("rol");

        return rol.equalsIgnoreCase(rolEsperado);
    }

    public static String obtenerToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        String token = header.replace("Bearer ", "");
        return token;
    }
}
