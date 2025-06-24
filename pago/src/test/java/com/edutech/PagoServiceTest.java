package com.edutech.service;

import com.edutech.configuration.DataLoader;
import com.edutech.model.Descuento;
import com.edutech.model.Pago;
import com.edutech.repository.DescuentoRepository;
import com.edutech.repository.PagoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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

    //el test generarPago_ConCodigoDescuentoInvalido_DeberiaCrearPagoSinDescuento
    //daba un error porque el mock de pagoRepository se llamaba mas veces de las que se especifica(en el dataloader + el test)
    @MockBean // Esto evita que el DataLoader real se ejecute
    private DataLoader dataLoader;
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
                .idCliente(1)
                .descuento(descuentoValido)
                .build();

        // pago sin descuento
        pagoSinDescuento = Pago.builder()
                .idCliente(2)
                .descuento(null)
                .build();
    }


    @Test
    void generarPago_ConCodigoDescuentoValido_DeberiaAplicarDescuento() {
        // configuracion del mock = existe el descuento
        when(descuentoRepository.findByCodigo("DESC20")).thenReturn(Optional.of(descuentoValido));

        // configuracion del mock para que devuelva pago con descuento
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoConDescuento);

        // metodo a probar
        Pago resultado = pagoService.generarPago(1, "DESC20");

        // verificaciones:
        //pago no nulo
        assertNotNull(resultado);
        //coincide con el idcliente
        assertEquals(1, resultado.getIdCliente());
        //debe tener el descuento
        assertNotNull(resultado.getDescuento());
        //si codigo no coincide
        assertEquals("DESC20", resultado.getDescuento().getCodigo());
        //% no coincide
        assertEquals(0.2, resultado.getDescuento().getPorcentaje());

        // verificar que se llama a los metodos esperados
        verify(descuentoRepository).findByCodigo("DESC20");
        verify(pagoRepository).save(any(Pago.class));
    }

    @Test
    void generarPago_ConCodigoDescuentoInvalido_DeberiaCrearPagoSinDescuento() {
        //mock para simular que no existe el descuento
        when(descuentoRepository.findByCodigo("CODIGO_INVALIDO")).thenReturn(Optional.empty());

        //mock para devolver un pago sin descuento
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoSinDescuento);

        // Ejecutar el metodo con un código inválido
        Pago resultado = pagoService.generarPago(2, "CODIGO_INVALIDO");

        // Verificaciones
        //pago no nulo
        assertNotNull(resultado);
        //idcliente no coincide
        assertEquals(2, resultado.getIdCliente());
        //no debe tener dscto aplicado
        assertNull(resultado.getDescuento());

        // verificar con los mocks
        verify(descuentoRepository).findByCodigo("CODIGO_INVALIDO");
        verify(pagoRepository).save(any(Pago.class));
    }

    @Test
    void generarPago_SinCodigoDescuento_DeberiaCrearPagoSinDescuento() {
        // mock para findByCodigo con null
        when(descuentoRepository.findByCodigo(null)).thenReturn(Optional.empty());

        //mock para save
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoSinDescuento);

        //metodo del srvice
        Pago resultado = pagoService.generarPago(2, null);

        //verificaciones
        assertNotNull(resultado);
        assertEquals(2,resultado.getIdCliente());
        assertNull(resultado.getDescuento());

        // interaccion con mock
        verify(descuentoRepository).findByCodigo(null); // Verifica que se llama con null
        verify(pagoRepository).save(any(Pago.class));
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
        assertEquals(1, resultado.getIdCliente());
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
        assertEquals(2, resultado.getIdCliente());
        assertNull(resultado.getDescuento());

        //verificar que se llama al repositorio
        verify(pagoRepository).save(pagoSinDescuento);
    }
}