package com.edutech.configuration;

import com.edutech.model.Progreso;
import com.edutech.repository.ProgresoRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProgresoRepository progresoRepository;

    private static final Faker faker = new Faker();

    @Override
    public void run(String... args) {
        if(progresoRepository.count() == 0) {
            List<Progreso> progresos = generarProgresos(20);
            progresoRepository.saveAll(progresos);
            System.out.println("Progresos de prueba cargados correctamente");
        }
    }

    private List<Progreso> generarProgresos(int cantidadUsuarios) {
        List<Progreso> progresos = new ArrayList<>();
        for (int i = 0; i < cantidadUsuarios; i++) {
            int idEstudiante = i + 1;
            progresos.addAll(crearProgresosPorEstudiante(idEstudiante));
        }
        return progresos;
    }

    private List<Progreso> crearProgresosPorEstudiante(int idEstudiante) {
        List<Progreso> progresos = new ArrayList<>();

        ResponseEntity<List> responseInscripciones = restTemplate.exchange(
                "http://localhost:8082/inscripcion/dev/inscripciones-por-usuario/" + idEstudiante,
                HttpMethod.GET,
                null,
                List.class
        );

        List<Map<String, Object>> inscripciones = responseInscripciones.getBody();
        if (inscripciones == null) {
            return progresos;
        }

        for (Map<String, Object> inscripcion : inscripciones) {
            Integer idCurso = (Integer) inscripcion.get("idCurso");

            ResponseEntity<List> responseContenidos = restTemplate.exchange(
                    "http://localhost:8092/contenido/curso/" + idCurso,
                    HttpMethod.GET,
                    null,
                    List.class
            );

            List<Map<String, Object>> contenidos = responseContenidos.getBody();
            List<Integer> idContenidoCompleto = new ArrayList<>();
            List<Integer> idContenidoVisualizado = new ArrayList<>();

            if (contenidos != null) {
                for (Map<String, Object> contenido : contenidos) {
                    Integer idContenido = (Integer) contenido.get("idContenido");
                    idContenidoCompleto.add(idContenido);

                    if (faker.bool().bool()) { //→genera un boleano aleatorio
                        idContenidoVisualizado.add(idContenido);
                    }
                }
            }

            double porcentaje = 0;
            if (!idContenidoCompleto.isEmpty()) {
                porcentaje = (idContenidoVisualizado.size() * 100.0) / idContenidoCompleto.size();
            }

            Progreso progreso = new Progreso();
            progreso.setIdEstudiante(idEstudiante);
            progreso.setIdCurso(idCurso);
            progreso.setIdContenidoCompleto(idContenidoCompleto);
            progreso.setIdContenidoVisualizado(idContenidoVisualizado);
            progreso.setPorcentaje(Math.round(porcentaje * 100.0) / 100.0); //mantiene dos decimales ej: 12.34

            progresos.add(progreso);
        }
        return progresos;
    }
}

