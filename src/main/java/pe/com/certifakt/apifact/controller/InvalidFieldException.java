package pe.com.certifakt.apifact.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class InvalidFieldException extends RuntimeException {
    private String message;

}
