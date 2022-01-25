package pe.com.certifakt.apifact.bean;

import java.math.BigDecimal;

public class Campos {

    private String campo_nombre;
    private String campo_valor;

    public Campos(String campo_nombre, String campo_valor) {
        super();
        this.campo_nombre = campo_nombre;
        this.campo_valor = campo_valor;
    }

    public String getCampo_nombre() {
        return campo_nombre;
    }
    public String getCampo_valor() {
        return campo_valor;
    }
}
