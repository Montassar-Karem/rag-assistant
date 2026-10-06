package io.github.ragassistant.chat;

import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;


@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResourceAccessException.class)
    public ProblemDetail handleModelUnavailable(ResourceAccessException ex) {
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
        problem.setTitle("Modèle d'IA indisponible");
        problem.setDetail("Le service d'IA ne répond pas. Réessayez dans quelques instants.");
        return problem;
    }

}
