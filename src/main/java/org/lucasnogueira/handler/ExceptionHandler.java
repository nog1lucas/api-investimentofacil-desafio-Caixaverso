package org.lucasnogueira.handler;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.lucasnogueira.exceptions.SmartInvestApiException;
import org.lucasnogueira.model.dto.ErroDetailDto;

import java.util.Date;

@Provider
public class ExceptionHandler implements ExceptionMapper<Exception> {

    @Override
    public Response toResponse(Exception e) {
        Throwable cause = getCause(e);

        // Trata exceções customizadas da aplicação
        if (cause instanceof SmartInvestApiException exception) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(ErroDetailDto.builder()
                            .message(exception.getMessage())
                            .status(Response.Status.BAD_REQUEST.getStatusCode())
                            .timestamp(new Date())
                            .build())
                    .build();
        }

        // Trata outras exceções não previstas
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ErroDetailDto.builder()
                        .message(e.getMessage())
                        .status(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                        .timestamp(new Date())
                        .build())
                .build();
    }

    private Throwable getCause(Throwable throwable) {
        Throwable cause;
        while ((cause = throwable.getCause()) != null) {
            throwable = cause;
        }
        return throwable;
    }
}