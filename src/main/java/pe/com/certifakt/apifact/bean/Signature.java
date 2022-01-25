package pe.com.certifakt.apifact.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Signature  implements Serializable
{

	private String id;
	private String denominacionEmisor;
	private String rucEmisor;
	private String uri;
}
