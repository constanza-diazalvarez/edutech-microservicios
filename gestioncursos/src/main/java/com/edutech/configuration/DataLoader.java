package com.edutech.configuration;

import com.edutech.model.Curso;
import com.edutech.repository.CursoRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Profile("test")
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private CursoRepository cursoRepository;

    private static final Faker faker = new Faker(); // Instancia de net.datafaker.Faker

    @Override
    public void run(String... args) throws Exception {
        if (cursoRepository.count() == 0) {
            List<Curso> cursos = generarCursos(20);
            cursoRepository.saveAll(cursos);
            System.out.println("listos los cursos de prueba");
        }
    }

    private List<Curso> generarCursos(int cantidad) {
        List<Curso> cursos = new ArrayList<>();
        for (int i = 0; i < cantidad; i++) {
            cursos.add(crearCurso());
        }
        return cursos;
    }

    private Curso crearCurso() {
        Curso curso = new Curso();

        curso.setNombreCurso(generarNombre());
        curso.setIdUsuario(faker.bool().bool() ? faker.number().numberBetween(1, 100) : null);
        curso.setDescripcion(faker.bool().bool() ? generarDescripcion() : null);
        curso.setCategoria(generarCategoria());
        curso.setNivel(generarNivel());
        curso.setDuracion(faker.number().numberBetween(8, 120));

        return curso;
    }

    private String generarNombre() {
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

    private String generarDescripcion() {
        String[] descripciones = {
                "Aprende los conceptos fundamentales y desarrolla habilidades prácticas en este curso completo.",
                "Domina las herramientas y técnicas más utilizadas en la industria actual.",
                "Curso diseñado para principiantes que quieren dar sus primeros pasos en el área.",
                "Profundiza tus conocimientos con ejercicios prácticos y proyectos reales.",
                "Adquiere las competencias necesarias para destacar en tu carrera profesional.",
                "Curso intensivo con enfoque 100% práctico y orientado a resultados.",
                "Desarrolla proyectos desde cero y construye un portafolio sólido.",
                "Aprende de la mano de expertos con años de experiencia en la industria."
        };

        return faker.options().option(descripciones);
    }

    private String generarCategoria() {
        String[] categorias = {
                "Programación", "Desarrollo Web", "Ciencia de Datos", "Inteligencia Artificial",
                "Ciberseguridad", "Diseño", "Marketing", "Negocios",
                "DevOps", "Mobile Development", "Cloud Computing", "Bases de Datos"
        };

        return faker.options().option(categorias);
    }

    private String generarNivel() {
        String[] niveles = {"Principiante", "Intermedio", "Avanzado", "Experto"};
        return faker.options().option(niveles);
    }
}
