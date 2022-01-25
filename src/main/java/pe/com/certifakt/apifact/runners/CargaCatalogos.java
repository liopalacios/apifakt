package pe.com.certifakt.apifact.runners;

import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import pe.com.certifakt.apifact.model.CatalogSunatEntity;
import pe.com.certifakt.apifact.repository.CatalogSunatRepository;

import java.util.Arrays;
import java.util.List;

@Component
@AllArgsConstructor
public class CargaCatalogos implements CommandLineRunner {

    private final CatalogSunatRepository catalogSunatRepository;


    @Override
    public void run(String... strings) throws Exception {

        //CARGANDO CATALOGO 06
        List<CatalogSunatEntity> catalogSunat06 = catalogSunatRepository.findAllByNumero("06");
        if (catalogSunat06.isEmpty()) {
            catalogSunatRepository.saveAll(Arrays.asList(
                    new CatalogSunatEntity("06", "0", "DOC.TRIB.NO.DOM.SIN.RUC", null, 1),
                    new CatalogSunatEntity("06", "1", "DNI", null, 2),
                    new CatalogSunatEntity("06", "4", "CARNET DE EXTRANJERIA", null, 3),
                    new CatalogSunatEntity("06", "6", "RUC", null, 4),
                    new CatalogSunatEntity("06", "7", "PASAPORTE", null, 5),
                    new CatalogSunatEntity("06", "A", "CED. DIPLOMATICA DE IDENTIDAD", null, 6)
            ));
        }

        //CARGANDO CATALOGO 07
        List<CatalogSunatEntity> catalogSunat07 = catalogSunatRepository.findAllByNumero("07");
        if (catalogSunat07.isEmpty()) {
            catalogSunatRepository.saveAll(Arrays.asList(
                    new CatalogSunatEntity("07", "10", "Gravado - Operación Onerosa", "1001", 1),
                    new CatalogSunatEntity("07", "11", "Gravado – Retiro por premio", "1004", 2),
                    new CatalogSunatEntity("07", "12", "Gravado – Retiro por donación", "1004", 3),
                    new CatalogSunatEntity("07", "13", "Gravado – Retiro", "1004", 4),
                    new CatalogSunatEntity("07", "14", "Gravado – Retiro por publicidad", "1004", 5),
                    new CatalogSunatEntity("07", "15", "Gravado – Bonificaciones", "1004", 6),
                    new CatalogSunatEntity("07", "16", "Gravado – Retiro por entrega a trabajadores", "1004", 7),
                    new CatalogSunatEntity("07", "17", "Gravado – IVAP", "1004", 8),
                    new CatalogSunatEntity("07", "20", "Exonerado - Operación Onerosa", "1003", 9),
                    new CatalogSunatEntity("07", "21", "Exonerado – Transferencia Gratuita", "1003", 10),
                    new CatalogSunatEntity("07", "30", "Inafecto - Operación Onerosa", "1002", 11),
                    new CatalogSunatEntity("07", "31", "Inafecto – Retiro por Bonificación", "1002", 12),
                    new CatalogSunatEntity("07", "32", "Inafecto – Retiro", "1002", 13),
                    new CatalogSunatEntity("07", "33", "Inafecto – Retiro por Muestras Médicas", "1002", 14),
                    new CatalogSunatEntity("07", "34", "Inafecto - Retiro por Convenio Colectivo", "1002", 15),
                    new CatalogSunatEntity("07", "35", "Inafecto – Retiro por premio", "1002", 16),
                    new CatalogSunatEntity("07", "36", "Inafecto - Retiro por publicidad", "1002", 17),
                    new CatalogSunatEntity("07", "40", "Exportación", "1002", 18)
            ));
        }


        //CARGANDO CATALOGO 09
        List<CatalogSunatEntity> catalogSunat09 = catalogSunatRepository.findAllByNumero("09");
        if (catalogSunat09.isEmpty()) {
            catalogSunatRepository.saveAll(Arrays.asList(
                    new CatalogSunatEntity("09", "01", "Anulación de la operación", null, 1),
                    new CatalogSunatEntity("09", "02", "Anulación por error en el RUC", null, 2),
                    new CatalogSunatEntity("09", "03", "Corrección por error en la descripción", null, 3),
                    new CatalogSunatEntity("09", "04", "Descuento global", null, 4),
                    new CatalogSunatEntity("09", "05", "Descuento por ítem", null, 5),
                    new CatalogSunatEntity("09", "06", "Devolución total", null, 6),
                    new CatalogSunatEntity("09", "07", "Devolución por ítem", null, 7),
                    new CatalogSunatEntity("09", "08", "Bonificación", null, 8),
                    new CatalogSunatEntity("09", "09", "Disminución en el valor", null, 9),
                    new CatalogSunatEntity("09", "10", "Otros Conceptos", null, 10)
            ));
        }

        //CARGANDO CATALOGO 10
        List<CatalogSunatEntity> catalogSunat10 = catalogSunatRepository.findAllByNumero("10");
        if (catalogSunat10.isEmpty()) {
            catalogSunatRepository.saveAll(Arrays.asList(
                    new CatalogSunatEntity("10", "01", "Intereses por mora", null, 1),
                    new CatalogSunatEntity("10", "02", "Aumento en el valor", null, 2),
                    new CatalogSunatEntity("10", "03", "Penalidades/ otros conceptos", null, 3)
            ));
        }

        //CARGANDO CATALOGO 17
        List<CatalogSunatEntity> catalogSunat17 = catalogSunatRepository.findAllByNumero("17");
        if (catalogSunat17.isEmpty()) {
            catalogSunatRepository.saveAll(Arrays.asList(
                    new CatalogSunatEntity("17", "01", "Venta Interna", null, 1),
                    new CatalogSunatEntity("17", "02", "Exportación", null, 2),
                    new CatalogSunatEntity("17", "04", "Anticipo o deducción de anticipo en venta interna", null, 3)
            ));
        }

        //CARGANDO CATALOGO 18
        List<CatalogSunatEntity> catalogSunat18 = catalogSunatRepository.findAllByNumero("18");
        if (catalogSunat18.isEmpty()) {
            catalogSunatRepository.saveAll(Arrays.asList(
                    new CatalogSunatEntity("18", "01", "PUBLICO", null, 1),
                    new CatalogSunatEntity("18", "02", "PRIVADO", null, 2)
            ));
        }

        //CARGANDO CATALOGO 20
        List<CatalogSunatEntity> catalogSunat20 = catalogSunatRepository.findAllByNumero("20");
        if (catalogSunat20.isEmpty()) {
            catalogSunatRepository.saveAll(Arrays.asList(
                    new CatalogSunatEntity("20", "01", "Venta", null, 1),
                    new CatalogSunatEntity("20", "14", "Venta sujeta a confirmación del comprador", null, 2),
                    new CatalogSunatEntity("20", "02", "Compra", null, 3),
                    new CatalogSunatEntity("20", "04", "Traslado entre establecimientos de la misma empresa", null, 4),
                    new CatalogSunatEntity("20", "18", "Traslado emisor itinerante CP", null, 5),
                    new CatalogSunatEntity("20", "08", "Importación", null, 6),
                    new CatalogSunatEntity("20", "09", "Exportación", null, 7),
                    new CatalogSunatEntity("20", "19", "Traslado a zona primaria", null, 8),
                    new CatalogSunatEntity("20", "13", "Otros", null, 9)
            ));
        }

    }
}
