package pe.com.certifakt.apifact.util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import pe.com.certifakt.apifact.model.ProductEntity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CsvUtils {

    public static <T> List<T> read(Class<T> clazz, InputStream stream) throws IOException {

        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(clazz).withHeader().withColumnReordering(true);
        ObjectReader reader = mapper.readerFor(clazz).with(schema);
        return reader.<T>readValues(stream).readAll();
    }

    public static List<ProductEntity> processInputFile(InputStream stream) {
        List<ProductEntity> inputList = new ArrayList<ProductEntity>();
        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            // skip the header of the csv
            inputList = br.lines().skip(1).map(mapToItem).collect(Collectors.toList());
            br.close();
        } catch (IOException e) {
            System.out.println("Error al procesar input file");
        }
        return inputList;
    }

    private static Function<String, ProductEntity> mapToItem = (line) -> {
        String COMMA = ",";
        String[] p = line.split(COMMA);// a CSV has comma separated lines
        ProductEntity item = new ProductEntity();

        item.setCodigo(p[0]);//<-- this is the first column in the csv file
        item.setDescripcion(p[1]);
        item.setMoneda(p[2]);
        item.setUnidadMedida(p[3]);
        item.setTipoAfectacion(p[4]);
        if(p.length > 5){
            item.setValorVentaSinIgv(new BigDecimal(p[5]));
            item.setValorVentaConIgv(new BigDecimal(p[6]));
        }
        //more initialization goes here
        return item;
    };
}
