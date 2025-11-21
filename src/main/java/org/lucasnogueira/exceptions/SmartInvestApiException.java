package org.lucasnogueira.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Builder
@Getter
@Setter
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SmartInvestApiException extends RuntimeException {

    private int codigoHTTP;
    private String mensagem;

    public SmartInvestApiException(String mensagem) {
        super(mensagem);
        this.codigoHTTP = 412; // código para quando pré-condição falhar
    }

    public SmartInvestApiException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }

    public SmartInvestApiException(int codigoHTTP, String mensagem) {
        super(mensagem);
        this.codigoHTTP = codigoHTTP;
    }

}
