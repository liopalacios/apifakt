package pe.com.certifakt.apifact.bean;

import lombok.*;

import java.io.Serializable;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria  implements Serializable {
    private String key;
    private String operation;
    private Object value;
}
