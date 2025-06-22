package com.edutech.controller;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import utils.JwtUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@AllArgsConstructor
public class ApiGatewayController {
    private final RestTemplate restTemplate; //RestTemplate es un cliente http que se usa para enviar solicitudes a cada microservicio

    //aqui se almacenan los microservicios con sus url
    private final Map<String, String> microservicios = new HashMap<>();

    @PostConstruct //se ejecuta automaticamente al iniciar la aplicacion
    public void init() {
        microservicios.put("auth", "http://localhost:8085/auth");
        microservicios.put("usuarios", "http://localhost:8081/usuarios");
        microservicios.put("contenido", "http://localhost:8092/contenido");
        microservicios.put("cursos", "http://localhost:8091/cursos");
        microservicios.put("inscripcion", "http://localhost:8082/inscripcion");
        microservicios.put("pago", "http://localhost:8084/pago");
        microservicios.put("progreso", "http://localhost:8083/progreso");
        microservicios.put("interaccion", "http://localhost:8094/interaccion");
    }

    @RequestMapping("/api/{servicio}/**") // → '/**' significa que puede venir cualquier ruta adicional despues
    public ResponseEntity<?> redirigir(
            /*↑redirigir captura cualquier peticion (GET, POST, etc) que empiece con /api/{servicio}/**
            *ResponseEntity: clase generica que representa una respuesta http completa
            *   contiene:
            *       cuerpo(body) → return ResponseEntity.ok("Hola mundo!")
            *       estado http (status) → return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontró");
            *       headers →
            *           HttpHeaders headers = new HttpHeaders();
            *           headers.set("Mi-Encabezado", "valor");
            *           return ResponseEntity.ok().headers(headers).body("Hola");
            */
            @PathVariable("servicio") String servicio, //rescata el valor de "servicio" en la ruta
            HttpServletRequest request,
            /*↑clase de java servlet usada por springboot para manejar peticiones http. Es un objeto con todos los datos de la peticion (ruta, metodo, etc)
            * Cuando un cliente hace una peticion http el servidor crea un objeto httpservletrequest con toda la informacion de la peticion
            * Informacion que contiene:
            *   •Metodo http: GET, POST, PUT, etc
            *       request.getMethod();
            *   •URI: ruta solicitada
            *       request.getRequestURI();
            *   •protocolo: HTTP
            *       request.getProtocol();
            *   •direccion ip del clienten
            *       request.getRemoteAddr();
            *   •header
            *       request.getHeader("Authorization");
            *       todos los headers:
            *           Enumeration<String> nombresHeaders = request.getHeaderNames();
            *           while (nombresHeaders.hasMoreElements()) {
            *               String nombre = nombresHeaders.nextElement();
            *               String valor = request.getHeader(nombre);
            *               // hacer algo con el header
            *           }
            */
            @RequestBody(required = false) String body //el body pordria no existir como en un GET
            //@RequestHeader HttpHeaders headers
    ) {
        //verifica si el servicio existe en el hashmap
        if(!microservicios.containsKey(servicio)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("El servicio " + servicio + " no existe"); //esto es lo que se ve en el body
        }

        //validar token para todos los ms, menos auth
        if(!servicio.equalsIgnoreCase("auth")){
            String token = JwtUtil.obtenerToken(request);
            try {
                if(JwtUtil.estaExpirado(token)){
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body("El token esta expirado");
                }

                String usuario = JwtUtil.obtenerUsername(token); //que coincida con el uduario
                if (!JwtUtil.validarToken(token, usuario)) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body("Token inválido.");
                }

                // (Opcional) validar roles aquí según el endpoint

            } catch (Exception e) { //en caso de que cualquiera de las validaciones falle devuelve error 401 UNAUTHORIZED
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Error al validar el token: " + e.getMessage());
            }
        }

        //construir la ruta al ms
        String base = microservicios.get(servicio);
        String path = request.getRequestURI().replace("/api/" + servicio, "");
        String url = base + path;
        /*peticion del cliente: POST /api/auth/login
         *servicio: "auth"
        * request.getRequestURI() = /api/auth/login
        * quitamos /api/{servicio}: String path = request.getRequestURI().replace("/api/" + servicio, "");
        * resultado: path = /login
        * nueva url: String url = "http://localhost:8085" + path;
         *
         *
         * →getRequestURI(): devuelve la ruta completa sin incluir domunio, ip o puerto
         *      http://localhost:8080/api/auth/login
         *          Dominio / host: localhost
         *          Puerto: 8080
         *          Ruta (URI): /api/auth/login
         */

        HttpMethod metodo = HttpMethod.valueOf(request.getMethod()); //obtiene el metodo http (ej: GET, POST, PUT, DELETE, etc). Si yo tengo el metodo lo puedo reenviar a la microservicio de destino

        //headers.setContentType(MediaType.APPLICATION_JSON);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", request.getHeader("Authorization"));
        headers.setContentType(MediaType.APPLICATION_JSON);

        /*↑uno de los headers es Autorization por donde pasa el token, pero otro es Content-Type
        * por eso se le indica al header que el cuerpo(body) es de tipo json
        * Content-Type: application/json
         * →SE PORDRIAN AGREGAR LOS TOKENS AQUI←*/

        HttpEntity<String> entity = new HttpEntity<>(body, headers);
        /*HttpEntity: objeto que representa la peticion HTTP completa. Contiene el body y los headers
        *   →esto es lo que el RestTemplate envia al microservicio en cuestion*/


        //↓aqui es donde se hace la llamada HTTP al microservicio
        ResponseEntity<String> respuesta = restTemplate.exchange(
                /*exchance es la manera de llamar a otro servicio http
                * exchange hace una petición HTTP con el metod o, headers y body que le indicas, y devuelve
                * la respuesta completa (código de estado, headers y body).
                * Es la forma más flexible de RestTemplate para interactuar con servicios HTTP*/
                url,
                metodo,
                entity,
                String.class
        );

        return ResponseEntity
                .status(respuesta.getStatusCode())
                .headers(respuesta.getHeaders())
                .body(respuesta.getBody());
    }

    @PostMapping("/api/inscripcion/{idCurso}")
    public ResponseEntity<?> incribirPagarCurso(
            @PathVariable("idCurso") Integer idCurso,
            @RequestBody(required = false) String codigoDescuento,
            //@RequestParam(value = "codigoDescuento", required = false) String codigoDescuento,
            @RequestHeader HttpHeaders headers,
            HttpServletRequest request
    ){
        String token = JwtUtil.obtenerToken(request);
        try {
            if(JwtUtil.estaExpirado(token)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("El token esta expirado");
            }

            String usuario = JwtUtil.obtenerUsername(token);
            if (!JwtUtil.validarToken(token, usuario)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Token inválido.");
            }
            // (Opcional) validar roles aquí según el endpoint

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Error al validar el token: " + e.getMessage());
        }

        String pagoUrl = microservicios.get("pago") + "/procesar-pago";

        if (codigoDescuento != null && !codigoDescuento.isEmpty()) {
            pagoUrl += "?codigoDescuento=" + codigoDescuento;
        }

        try {
            ResponseEntity<String> respuestaPago = restTemplate.exchange(
                    pagoUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(codigoDescuento, headers),
                    String.class
            );

            if (!respuestaPago.getStatusCode().equals(HttpStatus.OK)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Error al validar el procesar pago");
            }

        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }

        String inscripcionUrl = microservicios.get("inscripcion") + "/"  + idCurso;

        HttpHeaders nuevosHeaders = new HttpHeaders();
        nuevosHeaders.setContentType(MediaType.APPLICATION_JSON);
        nuevosHeaders.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(nuevosHeaders);

        ResponseEntity<String> respuestaInscripcion = restTemplate.exchange(
                inscripcionUrl,
                HttpMethod.POST,
                entity,
                String.class
        );

        return ResponseEntity
                .status(respuestaInscripcion.getStatusCode())
                .body(respuestaInscripcion.getBody());
    }

    @PatchMapping("/api/usuarios/perfil/editar")
    public ResponseEntity<?> modificarUsuario(@RequestHeader("Authorization") String authHeadear,
                                              @RequestBody Map<String, Object> body,
                                              HttpServletRequest request){
        String token = JwtUtil.obtenerToken(request);
        if (!JwtUtil.validarRolToken(token, "ESTUDIANTE")){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        Map<String, Object> bodyAuth = new HashMap<>();
        Map<String, Object> bodyUsuario = new HashMap<>();

        if (body.containsKey("correo")){
            bodyAuth.put("correo", body.get("correo"));
        }
        if (body.containsKey("password")){
            bodyAuth.put("password", body.get("password"));
        }
        if (body.containsKey("nombre")){
            bodyUsuario.put("nombre", body.get("nombre"));
        }
        HttpHeaders nuevosHeaders = new HttpHeaders();
        nuevosHeaders.setContentType(MediaType.APPLICATION_JSON);
        nuevosHeaders.set("Authorization", "Bearer " + token);

        if (!bodyAuth.isEmpty()) {
            HttpEntity <Map<String, Object>> entidadAuth = new HttpEntity<>(bodyAuth, nuevosHeaders);
            /*httpentity representa una peticion http y recibe dos parametros, body y header
            * <Map<String, Object>> indica que el body es un JSON con formato de clave-valor*/
            restTemplate.exchange(
                    //→VALIDAR URL←
                    microservicios.get("auth") + "/perfil/editar",
                    HttpMethod.PATCH,
                    entidadAuth,
                    Void.class);

        }
        if (!bodyUsuario.isEmpty()) {
            HttpEntity <Map<String, Object>> entidadUsuario = new HttpEntity<>(bodyUsuario, nuevosHeaders);
            restTemplate.exchange(
                    //→VALIDAR URL←
                    microservicios.get("usuarios") + "/perfil/editar",
                    HttpMethod.PATCH,
                    entidadUsuario,
                    Void.class);
        }
        return  ResponseEntity.ok().build(); //codigo 200
    }

    @GetMapping("/api/contenido/visualizar/{idContenido}")
    public ResponseEntity<?> visualizarYRegistrarProgreso(
            @PathVariable Integer idContenido,
            HttpServletRequest request
    ) {
        String token = JwtUtil.obtenerToken(request);
        try {
            if (JwtUtil.estaExpirado(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("El token está expirado");
            }
            String usuario = JwtUtil.obtenerUsername(token);
            if (!JwtUtil.validarToken(token, usuario)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error al validar token: " + e.getMessage());
        }

        String contenidoUrl = microservicios.get("contenido") + "/visualizar/contenido/" + idContenido;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> respuestaContenido;
        try {
            respuestaContenido = restTemplate.exchange(
                    contenidoUrl,
                    HttpMethod.GET,
                    entity,
                    byte[].class
            );
        } catch (HttpClientErrorException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }

        // llamar a progreso para registrar visualización
        String progresoUrl = microservicios.get("progreso") + "/registrar-progreso/contenido/" + idContenido;

        try {
            restTemplate.exchange(
                    progresoUrl,
                    HttpMethod.POST,
                    entity, // mismo header con el token
                    Void.class
            );
        } catch (HttpClientErrorException e) {
            // Solo logueamos el error si falla progreso, no rompemos la visualización
            System.out.println("Error al registrar progreso: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        }

        return ResponseEntity
                .status(respuestaContenido.getStatusCode())
                .headers(respuestaContenido.getHeaders())
                .body(respuestaContenido.getBody());
    }

}
