package com.edutech.service;

import com.edutech.model.Progreso;
import com.edutech.repository.ProgresoRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import utils.JwtUtil;

import java.util.*;

@Service
public class ProgresoService {
    @Autowired
    private ProgresoRepository progresoRepository;
    @Autowired
    private RestTemplate restTemplate;

    /*
    * tengo el idcontenido
    * extraigo toda la informacion de ese contenido para poder acceder al idCurso
    * traigo todoel contenido que corresponde a ese idCurso y lo agrego a la lista idContenidoCompleto
    * verifico si el idContenido estaba en la lista idContenidoVisualizado y si no esta entonces se agrega
    * se calcula el size total de ambas listas y se genera el porcentaje de progreso
    *
    * */

    public void registrarProgreso(Integer idContenido, HttpServletRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + request.getHeader(HttpHeaders.AUTHORIZATION));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        //traer un contenido por su propio id
        ResponseEntity<Map> response = restTemplate.exchange(
                "http://localhost:8080/api/contenido/traer-contenido/" + idContenido,
                HttpMethod.GET,
                entity,
                Map.class // ← ♥
        );

        Map<String, Object> contenido = response.getBody();// ← ♥
        /* → ♥ lo ideal seria que el restTemplate.exchange devolviera una instancia de
        * contenido, pero esa clase no existe en el microservicio de progreso. Una solucion
        * podria ser crear una clase DTO que simule contenido y asi extraer el idCurso con
        * un gettet o trabajar con un Map para trabajar la respuesta como un json
        * ejemplo de respuesta con Map:
        *      {
        *       "idContenido": 12,
        *       titulo": "Variables en Java",
        *       "idCurso": 3,
        *       "tipo": "text/plain"
        *       }
        *       → asi se le puede asi un get() a la key del json/map ("idCurso")
        */
        Integer idCurso =  (Integer)contenido.get("idCurso");

        //traer los contenidos que corresponden al ese idCurso↑
        ResponseEntity<List> respContenidos = restTemplate.exchange(
                "http://localhost:8080/api/contenido/curso/" + idCurso,
                HttpMethod.GET,
                entity,
                List.class //no me interesa el tipo que hay dentro de la lista, por defecto se interpreta asi: List<Map<String, Object>>
        );
        List<Map<String, Object>> contenidos = respContenidos.getBody();
        List<Integer> idContenidoCompleto = new ArrayList<>();
        for (int i = 0; i < contenidos.size(); i++) {
            Integer id = (Integer) contenidos.get(i).get("idContenido");
            idContenidoCompleto.add(id);
        }

        Integer idEstudiante = JwtUtil.obtenerId(JwtUtil.obtenerToken(request));

        Optional<Progreso> p = progresoRepository.findByIdEstudianteAndIdCurso(idEstudiante, idCurso);
        Progreso progreso;
        if (p.isPresent()) {
            progreso = p.get();
        } else {
            progreso = new Progreso();
            progreso.setIdEstudiante(idEstudiante);
            progreso.setIdCurso(idCurso);
            progreso.setIdContenidoVisualizado(new ArrayList<>());
            //algo para utilizar p
        }

        progreso.setIdContenidoCompleto(idContenidoCompleto);

        //en caso de que en el contenido visualizado no este el id del contenido
        if(!progreso.getIdContenidoVisualizado().contains(idContenido)){
            progreso.getIdContenidoVisualizado().add(idContenido);
        }

        int totalContenido = idContenidoCompleto.size();
        int totalContenidoVisualizado = progreso.getIdContenidoVisualizado().size();
        double porcentaje = 0;
        if (totalContenido > 0){
            porcentaje = (totalContenidoVisualizado * 100.0) /  totalContenido;
        }
        progreso.setPorcentaje(porcentaje);

        progresoRepository.save(progreso);
    }

    public Progreso obtenerProgresoPorCurso(Integer idEstudiante, Integer idCurso) {
        return progresoRepository.findByIdEstudianteAndIdCurso(idEstudiante, idCurso)
                .orElseThrow(() -> new NoSuchElementException("Progreso no encontrado"));
    }

    public List<Progreso> obtenerTodosLosProgresos(Integer idEstudiante) {
        return progresoRepository.findByIdEstudiante(idEstudiante);
    }

}