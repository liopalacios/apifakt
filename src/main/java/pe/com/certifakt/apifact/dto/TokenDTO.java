package pe.com.certifakt.apifact.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import pe.com.certifakt.apifact.config.UnixTimestampDateSerializer;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TokenDTO implements Serializable{

	private String token;
	@JsonSerialize(using = UnixTimestampDateSerializer.class)
	private Date expira;

}
