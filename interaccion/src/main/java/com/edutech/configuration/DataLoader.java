package com.edutech.configuration;

import com.edutech.model.Comentario;
import com.edutech.model.ResenaCalificacion;
import com.edutech.repository.ComentarioRepository;
import com.edutech.repository.ResenaCalificacionRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Profile("dev")
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private ResenaCalificacionRepository resenaCalificacionRepository;

    private static final Faker faker = new Faker();

    @Override
    public void run(String... args) throws Exception {
        // Cargar comentarios si no existen
        if (comentarioRepository.count() == 0) {
            List<Comentario> comentarios = generarComentarios(50);
            comentarioRepository.saveAll(comentarios);
            System.out.println("Comentarios de prueba cargados exitosamente");
        }

        // Cargar reseñas y calificaciones si no existen
        if (resenaCalificacionRepository.count() == 0) {
            List<ResenaCalificacion> resenasCalificaciones = generarResenasCalificaciones(30);
            resenaCalificacionRepository.saveAll(resenasCalificaciones);
            System.out.println("Reseñas y calificaciones de prueba cargadas exitosamente");
        }
    }

    private List<Comentario> generarComentarios(int cantidad) {
        List<Comentario> comentarios = new ArrayList<>();

        for (int i = 0; i < cantidad; i++) {
            comentarios.add(crearComentario());
        }
        return comentarios;
    }

    private List<ResenaCalificacion> generarResenasCalificaciones(int cantidad) {
        List<ResenaCalificacion> resenasCalificaciones = new ArrayList<>();

        for (int i = 0; i < cantidad; i++) {
            resenasCalificaciones.add(crearResenaCalificacion());
        }
        return resenasCalificaciones;
    }

    private Comentario crearComentario() {
        Comentario comentario = new Comentario();

        comentario.setIdCurso(faker.number().numberBetween(1, 20));
        comentario.setIdUsuario(faker.number().numberBetween(1, 100));
        comentario.setComentario(generarComentarioTexto());

        return comentario;
    }

    private ResenaCalificacion crearResenaCalificacion() {
        ResenaCalificacion resenaCalificacion = new ResenaCalificacion();

        Integer idCurso = faker.number().numberBetween(1, 20);
        resenaCalificacion.setIdCurso(idCurso);
        resenaCalificacion.setNombreCurso(generarNombreCurso());
        resenaCalificacion.setResena(faker.bool().bool() ? generarResenaTexto() : null);
        resenaCalificacion.setCalificacion(faker.number().numberBetween(1, 5));

        return resenaCalificacion;
    }

    private String generarComentarioTexto() {
        String[] comentarios = {
                "Excelente curso, muy bien explicado y con ejemplos prácticos.",
                "Me gustó mucho la metodología utilizada por el instructor.",
                "El contenido está actualizado y es muy relevante para el mercado actual.",
                "Buena introducción al tema, aunque esperaba más profundidad en algunos conceptos.",
                "Los ejercicios prácticos son muy útiles para reforzar el aprendizaje.",
                "El instructor explica de manera clara y fácil de entender.",
                "Curso completo y bien estructurado, lo recomiendo totalmente.",
                "Me ayudó a entender conceptos que antes me resultaban difíciles.",
                "El material de apoyo es excelente y muy completo.",
                "Perfecto para principiantes que quieren empezar en esta área.",
                "La calidad del contenido es muy buena, vale la pena la inversión.",
                "Esperaba más contenido práctico, pero la teoría está bien explicada.",
                "El curso cumplió con mis expectativas, aprendí mucho.",
                "Muy recomendable para quienes buscan actualizar sus conocimientos.",
                "El ritmo del curso es adecuado, no es ni muy rápido ni muy lento.",
                "Los proyectos finales son desafiantes y muy formativos.",
                "Buen curso, aunque algunos temas podrían estar mejor explicados.",
                "El instructor domina el tema y se nota su experiencia.",
                "Curso actualizado con las últimas tendencias de la industria.",
                "Me sirvió para refrescar conceptos que tenía olvidados."
        };

        return faker.options().option(comentarios);
    }

    private String generarResenaTexto() {
        String[] resenas = {
                "Curso excepcional que superó mis expectativas. El contenido es muy completo y está bien organizado.",
                "Muy buen curso para principiantes. Las explicaciones son claras y los ejercicios son útiles.",
                "El instructor tiene mucha experiencia y se nota en la calidad de la enseñanza.",
                "Contenido actualizado y relevante. Me ayudó mucho en mi trabajo actual.",
                "Excelente relación calidad-precio. Lo recomiendo sin dudas.",
                "Curso bien estructurado con buenos ejemplos prácticos y casos de uso reales.",
                "Aprendí mucho y pude aplicar los conocimientos inmediatamente en mi trabajo.",
                "El material de apoyo es muy completo y útil para consultas futuras.",
                "Buen curso, aunque algunos módulos podrían tener más profundidad.",
                "Perfecto para actualizar conocimientos y aprender nuevas técnicas.",
                "El curso me dio las herramientas necesarias para avanzar en mi carrera.",
                "Muy satisfecho con el contenido y la metodología utilizada.",
                "Curso completo que abarca todos los aspectos importantes del tema.",
                "Las actividades prácticas son muy valiosas para consolidar el aprendizaje.",
                "Recomendable para profesionales que buscan especializarse en el área."
        };

        return faker.options().option(resenas);
    }

    private String generarNombreCurso() {
        String[] prefijos = {
                "Introducción a", "Fundamentos de", "Principios de", "Curso Avanzado de",
                "Taller de", "Seminario de", "Masterclass de", "Bootcamp de"
        };

        String[] asignaturas = {
                "Java", "Python", "JavaScript", "React", "Angular", "Spring Boot", "Machine Learning",
                "Inteligencia Artificial", "Desarrollo Web", "Bases de Datos", "Ciberseguridad",
                "Cloud Computing", "DevOps", "UI/UX Design", "Marketing Digital", "Análisis de Datos"
        };

        String prefijo = faker.options().option(prefijos);
        String asignatura = faker.options().option(asignaturas);

        return prefijo + " " + asignatura;
    }
}