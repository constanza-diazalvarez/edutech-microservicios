package com.edutech.service;

import com.edutech.model.Contenido;
import com.edutech.repository.ContenidoRepository;
import jakarta.transaction.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class ContenidoService {

    private final ContenidoRepository contenidoRepository;

    // Inyección por constructor (recomendado)
    public ContenidoService(ContenidoRepository contenidoRepository) {
        this.contenidoRepository = contenidoRepository;
    }

    @Transactional
    public Contenido guardarContenido(Integer cursoId, MultipartFile contenido) throws IOException {
        Contenido nuevoContenido = new Contenido();
        nuevoContenido.setIdCurso(cursoId);
        nuevoContenido.setNombre(contenido.getOriginalFilename());
        nuevoContenido.setTipoContenido(contenido.getContentType());
        nuevoContenido.setDatosContenido(contenido.getBytes());

        return contenidoRepository.save(nuevoContenido);
    }

    @Transactional
    public Contenido actualizarContenido(Integer id, Integer idCurso, MultipartFile archivo) throws IOException {
        // Buscar el contenido existente
        Contenido contenidoExistente = contenidoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Contenido no encontrado con id: " + id));

        // Actualizar campos
        contenidoExistente.setIdCurso(idCurso);

        if (archivo != null && !archivo.isEmpty()) {
            contenidoExistente.setNombre(archivo.getOriginalFilename());
            contenidoExistente.setTipoContenido(archivo.getContentType());
            contenidoExistente.setDatosContenido(archivo.getBytes());
        }

        return contenidoRepository.save(contenidoExistente);
    }

    public List<Contenido> obtenerTodoContenido() {
        return contenidoRepository.findAll();
    }

    public List<Contenido> obtenerPorIdCurso(Integer idCurso) {
        return contenidoRepository.findByIdCurso(idCurso);
    }

    public ResponseEntity<byte[]> visualizarContenido(Integer idContenido) {
        Optional<Contenido> c = contenidoRepository.findById(idContenido);
        if (c.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contenido no encontrado");
        }
        Contenido contenido = c.get();

        MediaType tipo = obtenerTipo(contenido.getTipoContenido());

        HttpHeaders headers = new HttpHeaders(); //objeto para definir encabezados http
        headers.setContentType(tipo);//aqui se define si el archivo es tipo application/pdf, text/plain, image/jpeg, etc.
        headers.setContentDisposition(ContentDisposition.inline() //el contenido debe mostrarse dentro del navegador
                .filename(contenido.getNombre())//establece el nombre sugerido para el archivo en caso de descarga
                .build());//finaliza la construcción del objeto

        return new ResponseEntity<>(contenido.getDatosContenido(), headers, HttpStatus.OK);
        /*↑Crea y retorna una respuesta HTTP completa con:
        *   cuerpo de la respuesta: contenido.getDatosContenido()
        *   encabezados: El objeto headers que configuramos
        *   codigo de estado: HttpStatus.OK (200)*/
    }

    private MediaType obtenerTipo(String tipoContenido) {
        switch (tipoContenido.toLowerCase()) {
            case "pdf":
                return MediaType.APPLICATION_PDF;
            case "txt":
                return MediaType.TEXT_PLAIN;
            case "jpeg":
            case "jpg":
                return MediaType.IMAGE_JPEG;
            case "png":
                return MediaType.IMAGE_PNG;
            default:
                return MediaType.APPLICATION_OCTET_STREAM; //tipo MIME generico
                /*Como no sabe que tipo es generalmente no lo muestra y directamente lo descarga
                * Es util para evitar errores en caso de que la extencion no este en la lista del switch
                *  MIME types son etiquetas estandar que indican el formato del archivo*/
        }
    }
}