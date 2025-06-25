package com.edutech.service;

import com.edutech.configuration.DataLoader;
import com.edutech.model.Descuento;
import com.edutech.model.Pago;
import com.edutech.repository.DescuentoRepository;
import com.edutech.repository.PagoRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import utils.JwtUtil;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PagoServiceTest {

    // mock del repositorio de pagos
    @MockBean
    private PagoRepository pagoRepository;

    // mock del repositorio de descuentos
    @MockBean
    private DescuentoRepository descuentoRepository;

    // servicio real a probar
    @Autowired
    private PagoService pagoService;

    private Descuento descuentoValido;
    private Pago pagoConDescuento;
    private Pago pagoSinDescuento;

    //crear entidades
    @BeforeEach
    void crearEntidades() {
        // descuento válido
        descuentoValido = Descuento.builder()
                .idDescuento(1L)
                .codigo("DESC20")
                .porcentaje(0.2)
                .build();

        // pago con descuento aplicado
        pagoConDescuento = Pago.builder()
                .idUsuario(1)
                .descuento(descuentoValido)
                .build();

        // pago sin descuento
        pagoSinDescuento = Pago.builder()
                .idUsuario(2)
                .descuento(null)
                .build();
    }

    @Test
    void generarPago_ConCodigoDescuentoValido_DeberiaAplicarDescuento() {
        // mock del request
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer token_valido");

        // mock estático de JwtUtil
        try (MockedStatic<JwtUtil> mockedJwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            //los mocks estáticos
            mockedJwtUtil.when(() -> JwtUtil.obtenerToken(request)).thenReturn("token_valido");
            mockedJwtUtil.when(() -> JwtUtil.obtenerId("token_valido")).thenReturn(1);

            // mocks de repositorios
            when(descuentoRepository.findByCodigo("DESC20")).thenReturn(Optional.of(descuentoValido));
            when(pagoRepository.save(any(Pago.class))).thenReturn(pagoConDescuento);

            // Ejecutar el metodo
            Pago resultado = pagoService.generarPago(request, "DESC20");

            // Verificaciones
            assertNotNull(resultado);
            assertEquals(1, resultado.getIdUsuario());
            assertNotNull(resultado.getDescuento());
            assertEquals("DESC20", resultado.getDescuento().getCodigo());
            assertEquals(0.2, resultado.getDescuento().getPorcentaje(), 0.001);

            // Verificar interacciones
            mockedJwtUtil.verify(() -> JwtUtil.obtenerToken(request));
            mockedJwtUtil.verify(() -> JwtUtil.obtenerId("token_valido"));
            verify(descuentoRepository).findByCodigo("DESC20");
            verify(pagoRepository).save(any(Pago.class));
        }
    }

    @Test
    void generarPago_ConCodigoDescuentoInvalido_DeberiaAsignarDescuentoPorDefecto() {
        // configurar mocks estaticos para JwtUtil
        try (MockedStatic<JwtUtil> mockedJwtUtil = Mockito.mockStatic(JwtUtil.class)) {
            // simular que no existe el descuento con código inválido
            when(descuentoRepository.findByCodigo("CODIGO_INVALIDO"))
                    .thenReturn(Optional.empty());

            // crear un descuento por defecto (DSCTO0)
            Descuento descuentoPorDefecto = Descuento.builder()
                    .idDescuento(3L)
                    .codigo("DSCTO0")
                    .porcentaje(0.0)
                    .build();

            // simular que existe el descuento por defecto
            when(descuentoRepository.findByCodigo("DSCTO0"))
                    .thenReturn(Optional.of(descuentoPorDefecto));

            // mock del request y token JWT
            HttpServletRequest request = mock(HttpServletRequest.class);
            when(request.getHeader("Authorization")).thenReturn("Bearer token_valido");

            // configurar los mocks estáticos
            mockedJwtUtil.when(() -> JwtUtil.obtenerToken(request)).thenReturn("token_valido");
            mockedJwtUtil.when(() -> JwtUtil.obtenerId("token_valido")).thenReturn(2);

            // mock del pago guardado (con descuento por defecto)
            Pago pagoGuardado = Pago.builder()
                    .idUsuario(2)
                    .descuento(descuentoPorDefecto)
                    .build();
            when(pagoRepository.save(any(Pago.class))).thenReturn(pagoGuardado);

            // ejecutar el metodo
            Pago resultado = pagoService.generarPago(request, "CODIGO_INVALIDO");

            // Verificaciones
            assertNotNull(resultado);
            assertEquals(2, resultado.getIdUsuario());

            // Verificar que se asignó el descuento por defecto (no null)
            assertNotNull(resultado.getDescuento());
            assertEquals("DSCTO0", resultado.getDescuento().getCodigo());

            // verificar interacciones con el repositorio y jwt
            mockedJwtUtil.verify(() -> JwtUtil.obtenerToken(request));
            mockedJwtUtil.verify(() -> JwtUtil.obtenerId("token_valido"));
            verify(descuentoRepository).findByCodigo("CODIGO_INVALIDO");
            verify(descuentoRepository).findByCodigo("DSCTO0");
            verify(pagoRepository).save(any(Pago.class));
        }
    }

    @Test
    void findAll_DeberiaRetornarTodosLosPagos() {
        //mock para devolver una lista de pagos
        List<Pago> pagosEsperados = Arrays.asList(pagoConDescuento, pagoSinDescuento);//convierte un array en una lista
        when(pagoRepository.findAll()).thenReturn(pagosEsperados);

        // ejecutar el metodo
        List<Pago> resultado = pagoService.findAll();

        // verificaciones
        assertNotNull(resultado);
        //misma cantidad de pagos
        assertEquals(2, resultado.size());

        //se llama al repositorio
        verify(pagoRepository).findAll();
    }

    @Test
    void save_PagoConDescuento_DeberiaGuardarCorrectamente() {
        //mock para devolver el pago con descuento
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoConDescuento);

        //metodo del service
        Pago resultado = pagoService.save(pagoConDescuento);

        //verificaciones
        assertNotNull(resultado);
        assertEquals(1, resultado.getIdUsuario());
        assertNotNull(resultado.getDescuento());

        // verificar que se llama al repositorio
        verify(pagoRepository).save(pagoConDescuento);
    }

    @Test
    void save_PagoSinDescuento_DeberiaGuardarCorrectamente() {
        //mock para devolver el pago sin descuento
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoSinDescuento);

        //metodo
        Pago resultado = pagoService.save(pagoSinDescuento);

        //verificaciones
        assertNotNull(resultado);
        assertEquals(2, resultado.getIdUsuario());
        assertNull(resultado.getDescuento());

        //verificar que se llama al repositorio
        verify(pagoRepository).save(pagoSinDescuento);
    }
}