package com.edutech.service;

import com.edutech.model.Descuento;
import com.edutech.model.Pago;
import com.edutech.repository.DescuentoRepository;
import com.edutech.repository.PagoRepository;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private DescuentoRepository descuentoRepository;

    @InjectMocks
    private PagoService pagoService;

    private Faker faker;

    @BeforeEach
    void setUp() {
        faker = new Faker(new Locale("es"));
    }

    @Test
    void generarPago_ConDescuento_ShouldApplyDiscount() {
        // Datos de prueba
        Integer idUsuario = faker.number().numberBetween(1, 100);
        String codigoDescuento = "DESC" + faker.number().digits(4);
        Double porcentajeDescuento = 10.0 + faker.number().randomDouble(1, 0, 30);

        // Mock del descuento
        Descuento descuento = new Descuento();
        descuento.setIdDescuento(faker.number().randomNumber());
        descuento.setCodigo(codigoDescuento);
        descuento.setPorcentaje(porcentajeDescuento);

        // Mock del pago esperado
        Pago pagoEsperado = new Pago();
        pagoEsperado.setIdCliente(idUsuario);
        pagoEsperado.setDescuento(descuento);

        when(descuentoRepository.findByCodigo(codigoDescuento))
                .thenReturn(Optional.of(descuento));
        when(pagoRepository.save(any(Pago.class)))
                .thenReturn(pagoEsperado);

        // Ejecutar el metodo
        Pago resultado = pagoService.generarPago(idUsuario, codigoDescuento);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(idUsuario, resultado.getIdCliente());
        assertNotNull(resultado.getDescuento());
        assertEquals(codigoDescuento, resultado.getDescuento().getCodigo());
        assertEquals(porcentajeDescuento, resultado.getDescuento().getPorcentaje());

        verify(descuentoRepository, times(1)).findByCodigo(codigoDescuento);
        verify(pagoRepository, times(1)).save(any(Pago.class));
    }

    @Test
    void generarPago_SinDescuento_ShouldCreatePaymentWithoutDiscount() {
        // Datos de prueba
        Integer idUsuario = faker.number().numberBetween(1, 100);

        // Mock del pago esperado
        Pago pagoEsperado = new Pago();
        pagoEsperado.setIdCliente(idUsuario);
        pagoEsperado.setDescuento(null);

        when(pagoRepository.save(any(Pago.class)))
                .thenReturn(pagoEsperado);

        // Ejecutar el metodo sin código de descuento
        Pago resultado = pagoService.generarPago(idUsuario, null);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(idUsuario, resultado.getIdCliente());
        assertNull(resultado.getDescuento());

        verify(descuentoRepository, never()).findByCodigo(any());
        verify(pagoRepository, times(1)).save(any(Pago.class));
    }

    @Test
    void generarPago_DescuentoNoExiste_ShouldCreatePaymentWithoutDiscount() {
        // Datos de prueba
        Integer idUsuario = faker.number().numberBetween(1, 100);
        String codigoDescuento = "DESC" + faker.number().digits(4);

        // Mock del pago esperado
        Pago pagoEsperado = new Pago();
        pagoEsperado.setIdCliente(idUsuario);
        pagoEsperado.setDescuento(null);

        when(descuentoRepository.findByCodigo(codigoDescuento))
                .thenReturn(Optional.empty());
        when(pagoRepository.save(any(Pago.class)))
                .thenReturn(pagoEsperado);

        // Ejecutar el metodo
        Pago resultado = pagoService.generarPago(idUsuario, codigoDescuento);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(idUsuario, resultado.getIdCliente());
        assertNull(resultado.getDescuento());

        verify(descuentoRepository, times(1)).findByCodigo(codigoDescuento);
        verify(pagoRepository, times(1)).save(any(Pago.class));
    }

    @Test
    void findAll_ShouldReturnAllPayments() {
        // Generar datos de prueba con Faker
        List<Pago> pagos = List.of(
                crearPagoFake(),
                crearPagoFake(),
                crearPagoFake()
        );

        when(pagoRepository.findAll()).thenReturn(pagos);

        // Ejecutar el metodo
        List<Pago> resultado = pagoService.findAll();

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(3, resultado.size());

        verify(pagoRepository, times(1)).findAll();
    }

    @Test
    void save_ShouldSavePayment() {
        // Generar datos de prueba con Faker
        Pago pago = crearPagoFake();

        when(pagoRepository.save(any(Pago.class))).thenReturn(pago);

        // Ejecutar el metodo
        Pago resultado = pagoService.save(pago);

        // Verificaciones
        assertNotNull(resultado);
        assertEquals(pago.getIdCliente(), resultado.getIdCliente());
        if (pago.getDescuento() != null) {
            assertEquals(pago.getDescuento().getCodigo(), resultado.getDescuento().getCodigo());
        }

        verify(pagoRepository, times(1)).save(pago);
    }

    private Pago crearPagoFake() {
        Pago pago = new Pago();
        pago.setIdCliente(faker.number().numberBetween(1, 100));

        // 50% de probabilidad de tener descuento
        if (faker.bool().bool()) {
            Descuento descuento = new Descuento();
            descuento.setIdDescuento(faker.number().randomNumber());
            descuento.setCodigo("DESC" + faker.number().digits(4));
            descuento.setPorcentaje(5.0 + faker.number().randomDouble(1, 0, 45));
            pago.setDescuento(descuento);
        }

        return pago;
    }
}