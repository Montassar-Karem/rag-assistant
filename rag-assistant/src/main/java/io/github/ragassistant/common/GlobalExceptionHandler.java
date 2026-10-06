package io.github.ragassistant.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import tools.jackson.core.JacksonException;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // The model can't be reached (Ollama stopped, network down…)
    @ExceptionHandler(ResourceAccessException.class)
    public ProblemDetail handleModelUnavailable(ResourceAccessException ex) {
        log.error("AI model unreachable: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.SERVICE_UNAVAILABLE);
        problem.setTitle("Modèle d'IA indisponible");
        problem.setDetail("Le service d'IA ne répond pas. Réessayez dans quelques instants.");
        return problem;
    }

    // The model answered, but its JSON can't be used
    @ExceptionHandler(JacksonException.class)
    public ProblemDetail handleInvalidModelOutput(JacksonException ex) {
        log.warn("Invalid AI model output: {}", ex.getMessage());
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_GATEWAY);
        problem.setTitle("Réponse du modèle invalide");
        problem.setDetail("Le modèle d'IA a renvoyé une réponse inexploitable. Réessayez.");
        return problem;
    }

    // Anything else: a real bug
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        problem.setTitle("Erreur interne");
        problem.setDetail("Une erreur inattendue s'est produite.");
        return problem;
    }
}