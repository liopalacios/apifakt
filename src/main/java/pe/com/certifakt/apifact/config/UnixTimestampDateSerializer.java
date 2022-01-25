package pe.com.certifakt.apifact.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Date;

@Slf4j
public class UnixTimestampDateSerializer extends StdSerializer<Date> {

    public UnixTimestampDateSerializer() {
        this(null);
    }

    public UnixTimestampDateSerializer(Class t) {
        super(t);
    }

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider arg2)
            throws IOException, JsonProcessingException {
        long ut = value.getTime();
        gen.writeNumber(ut);
    }
}
