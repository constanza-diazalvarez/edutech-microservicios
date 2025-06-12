package com.edutech;

import com.edutech.model.Contenido;
import com.edutech.repository.ContenidoRepository;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Profile("test")
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ContenidoRepository contenidoRepository;

    @Override
    public void run(String... args) throws Exception {
        // Crear instancia de Faker con idioma español (método correcto)
        Faker faker = new Faker(Locale.forLanguageTag("es"));

        // Generar contenido de prueba
        for (int i = 0; i < 20; i++) {
            Contenido contenido = new Contenido();

            // Generar nombres de archivo más realistas
            String extension = faker.options().option("pdf", "docx", "pptx", "xlsx", "mp4", "jpg", "png");
            String fileName = faker.book().title().replaceAll(" ", "_").toLowerCase() + "." + extension;

            contenido.setNombre(fileName);

            // Generar tipos MIME más realistas según la extensión
            String mimeType = getMimeType(extension);
            contenido.setTipoContenido(mimeType);

            // Generar contenido ficticio
            String contenidoTexto = faker.lorem().paragraph(5);
            contenido.setDatosContenido(contenidoTexto.getBytes());

            // ID de curso aleatorio
            contenido.setIdCurso(faker.number().numberBetween(1, 10));

            contenidoRepository.save(contenido);

            System.out.println("Creado contenido: " + fileName + " para curso: " + contenido.getIdCurso());
        }

        System.out.println("datos generados yei:)");
    }

    private String getMimeType(String extension) {
        return switch (extension.toLowerCase()) {
            case "pdf" -> "application/pdf";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case "pptx" -> "application/vnd.openxmlformats-officedocument.presentationml.presentation";
            case "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "mp4" -> "video/mp4";
            case "jpg" -> "image/jpeg";
            case "png" -> "image/png";
            default -> "application/octet-stream";
        };
    }
}