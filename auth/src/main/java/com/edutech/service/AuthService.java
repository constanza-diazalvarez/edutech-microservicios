package com.edutech.service;

import com.edutech.dto.LoginRequest;
import com.edutech.dto.LoginResponse;
import com.edutech.dto.UsuarioDTO;
import com.edutech.model.Rol;
import com.edutech.model.Usuario;
import com.edutech.repository.RolRepository;
import com.edutech.repository.UsuarioRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import utils.JwtUtil;

import java.util.Map;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AuthService {
    private final RestTemplate restTemplate;

    @Autowired
    public UsuarioRepository usuarioRepository;

    @Autowired
    public RolRepository rolRepository;


    public LoginResponse login(LoginRequest loginRequest) {
        Optional<Usuario> u = usuarioRepository.findByCorreo(loginRequest.getCorreo());
        if (u.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        Usuario usuario = u.get();

        if(usuario.getPassword().equals(loginRequest.getPassword())) {
            String token = JwtUtil.generarToken(usuario.getCorreo(), usuario.getRol().getRol(), usuario.getId());
            return new LoginResponse(token, usuario.getRol().getRol(), usuario.getId());
        }
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");

    }

    public ResponseEntity<?> registrar(UsuarioDTO usuarioDTO) {
        Usuario usuario = new Usuario();
        usuario.setCorreo(usuarioDTO.getCorreo());
        usuario.setPassword(usuarioDTO.getPassword());
        usuario.setRol(findRolById(4)); //rol de ESTUDIANTE

        Usuario usuarioGuardado =  saveUsuario(usuario);

        usuarioDTO.setId(usuarioGuardado.getId());
        usuarioDTO.setEstado("ACTIVO");

        try {
            String url = "http://localhost:8080/api/usuarios/registrar";
            restTemplate.postForEntity(
                    /*en postForEntity el segundo parametro es el body, en este caso el usuario va en el cuerpo y
                    postForEntity lo transforma automaticamente en json*/
                    url,
                    usuarioDTO, //body
                    Void.class); //la clase del objeto esperando en la respuesta, si no recibe ninguno entonces Void.class
            /*Comparacion distintos metodos:
             *   postForEntity	    Envía datos con POST y devuelve toda la respuesta (ResponseEntity).
             *   postForObject       Envía datos con POST y devuelve directamente el objeto de la respuesta.
             *   exchange	        Más flexible: puedes usar cualquier verbo HTTP, headers, etc.
             */
        } catch (RestClientException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar en gestión de usuarios.");
        }

        return ResponseEntity.ok(usuarioDTO);

    }

    public ResponseEntity<?> modificarUsuario(
            @RequestBody Map<String, Object> usuarioMap,
            HttpServletRequest request) {
        String token = JwtUtil.obtenerToken(request);
        Integer id = JwtUtil.obtenerId(token);

        Usuario usuario = findUsuarioById(id);

        for (String key : usuarioMap.keySet()) {
            if (key != null){
                switch (key) {
                    case "correo" -> usuario.setCorreo(usuarioMap.get(key).toString());
                    case "password" -> usuario.setPassword(usuarioMap.get(key).toString());
                    default -> {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Campo incorrecto - " + key);
                    }
                }
            }
        }
        return ResponseEntity.ok(saveUsuario(usuario));
    }

    public Usuario findUsuarioById(Integer id) {
        Optional<Usuario> u =  usuarioRepository.findById(id);
        if (u.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado");
        }
        return u.get();
    }

    public Rol findRolById(Integer id) {
        Optional<Rol> r =  rolRepository.findById(id);
        if (r.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado");
        }
        return r.get();
    }

    public Usuario saveUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public boolean existeUsuario(Integer id) {
        return usuarioRepository.existsById(id);
    }

}
