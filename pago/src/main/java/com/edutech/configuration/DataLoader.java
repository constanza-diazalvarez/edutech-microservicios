package com.edutech.configuration;

import com.edutech.model.Descuento;
import com.edutech.model.Pago;
import com.edutech.repository.DescuentoRepository;
import com.edutech.repository.PagoRepository;
import net.datafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
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
        // Instancia de Faker con idioma español
        Faker faker = new Faker(new Locale("es"));
        Random random = new Random();

        // Generar descuentos de prueba
        for (int i = 0; i < 10; i++) {
            Descuento descuento = new Descuento();

            // Código tipo DESC1234
            String codigo = "DESC" + faker.number().numberBetween(1000, 9999);
            descuento.setCodigo(codigo);

            // Porcentaje entre 5% y 50%
            double porcentaje = 5 + random.nextInt(46);
            descuento.setPorcentaje(porcentaje);

            descuentoRepository.save(descuento);
        }

        // Generar pagos de prueba
        for (int i = 0; i < 50; i++) {
            Pago pago = new Pago();

            pago.setIdUsuario(faker.number().numberBetween(1, 101));

            // 70% de probabilidad de tener descuento
            if (random.nextDouble() < 0.7) {
                List<Descuento> descuentos = descuentoRepository.findAll();
                if (!descuentos.isEmpty()) {
                    Descuento descuentoAleatorio = descuentos.get(random.nextInt(descuentos.size()));
                    pago.setDescuento(descuentoAleatorio);
                }
            }

            pagoRepository.save(pago);
        }

        System.out.println("Datos de pago y descuento generados correctamente");
    }
}
