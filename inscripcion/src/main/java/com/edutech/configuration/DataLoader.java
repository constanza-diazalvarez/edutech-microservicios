package com.edutech.configuration;

import com.edutech.modelo.Inscripcion;
import com.edutech.repository.InscripcionRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private InscripcionRepository inscripcionRepository;

    private static final Faker faker = new Faker();

    @Override
    public void run(String... args) {
        if (inscripcionRepository.count() == 0) {
            List<Inscripcion> inscripciones = generarInscripciones(30);
            inscripcionRepository.saveAll(inscripciones);
            System.out.println("Inscripciones de prueba generadas");
        }
    }

    private List<Inscripcion> generarInscripciones(int cantidad) {
        List<Inscripcion> lista = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            lista.add(crearInscripcion());
        }
        return lista;
    }

    private Inscripcion crearInscripcion() {
        Inscripcion inscripcion = new Inscripcion();

        int idUsuario = faker.number().numberBetween(1, 20);
        int idCurso = faker.number().numberBetween(1, 20);
        int idPago = faker.number().numberBetween(1, 20);

        //↓fecha aleatoria dentro de los últimos 6 meses
        LocalDateTime fecha = faker.date()
                .past(180, java.util.concurrent.TimeUnit.DAYS)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();

        inscripcion.setIdUsuario(idUsuario);
        inscripcion.setIdCurso(idCurso);
        inscripcion.setIdPago(idPago);
        inscripcion.setFechaInscripcion(fecha);

        return inscripcion;
    }
}

