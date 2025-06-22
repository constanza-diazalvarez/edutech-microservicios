package com.edutech.configuration;


import com.edutech.model.*;
import com.edutech.repository.DescuentoRepository;
import com.edutech.repository.PagoRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Random;


@Profile("test")
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private DescuentoRepository descuentoRepository;


    @Override
    public void run(String... args) throws Exception {
            cargarDescuentos();
            crearPagos();
        }

        private void cargarDescuentos() {
            crearDscto(25);
            crearDscto(40);
            crearDscto(70);
        }

        private void crearDscto(int porcentaje) {
            if (!descuentoRepository.findByCodigo("DSC"+String.valueOf(porcentaje)).isPresent()) {
                double porcDouble=porcentaje/100;
                Descuento descuento = Descuento.builder().codigo("DSC"+String.valueOf(porcentaje)).porcentaje(porcDouble).build();
                descuentoRepository.save(descuento);
            }
        }

        //crear pagos
        private void crearPagos() {
            Faker faker = new Faker();
            Random random = new Random();

            for (int i = 0; i < 10; i++) {
                int idCliente = faker.number().numberBetween(1, 51);
                Optional<Descuento> descuento = Optional.empty(); // Por defecto sin descuento
                // 45% de probabilidad de tener descuento
                if (random.nextDouble() < 0.45) {
                    Long idDescuento = (long) faker.number().numberBetween(1, 4);
                    descuento = descuentoRepository.findById(idDescuento);
                }
                Pago pago = Pago.builder().idCliente(idCliente).descuento(descuento.orElse(null)) // Convierte Optional a null si está vacío
                        .build();
                pagoRepository.save(pago);
            }
            System.out.println("Pagos creados");
        }

}
