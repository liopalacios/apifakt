package pe.com.certifakt.apifact.dto;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class EmailSendDTO implements Serializable {

	private Long id;
	private String email;
	
}
