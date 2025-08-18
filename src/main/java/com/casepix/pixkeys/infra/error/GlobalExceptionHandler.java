package com.casepix.pixkeys.infra.error;

import com.casepix.pixkeys.domain.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;


@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ErrorDetail(String field, String message) {}
    public record ErrorResponse(Instant timestamp, String code, String message, List<ErrorDetail> details) {}

    private static final Pattern PUT_OR_POST_PATH = Pattern.compile("^/chave-pix/[^/]+$");

    private boolean isChavePix(HttpServletRequest req) {
        return ("PUT".equalsIgnoreCase(req.getMethod()) || "POST".equalsIgnoreCase(req.getMethod()))
            && PUT_OR_POST_PATH.matcher(req.getRequestURI()).matches();
    }

    private ErrorResponse body(String code, String message, List<ErrorDetail> details) {
        return new ErrorResponse(Instant.now(), code, message, details == null ? List.of() : details);
    }

    @ExceptionHandler(ValidacaoException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleValidacao(ValidacaoException ex) {
        return new ErrorResponse(Instant.now(), "VALIDATION_ERROR", ex.getMessage(), List.of());
    }

    @ExceptionHandler(LimiteExcedidoException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleLimite(LimiteExcedidoException ex) {
        return new ErrorResponse(Instant.now(), "LIMIT_EXCEEDED", ex.getMessage(), List.of());
    }

    @ExceptionHandler({ChavePixJaExisteException.class, DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicate(RuntimeException ex) {
        return new ErrorResponse(Instant.now(), "DUPLICATE_KEY", ex.getMessage(), List.of());
    }

    @ExceptionHandler(ContaNaoEncontradaException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(ContaNaoEncontradaException ex) {
        return new ErrorResponse(Instant.now(), "ACCOUNT_NOT_FOUND", ex.getMessage(), List.of());
    }

    @ExceptionHandler(ChavePixNaoEncontradaException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleKeyNotFound(ChavePixNaoEncontradaException ex) {
        return new ErrorResponse(Instant.now(), "KEY_NOT_FOUND", ex.getMessage(), List.of());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleGeneric(Exception ex) {
        return new ErrorResponse(Instant.now(), "BAD_REQUEST", ex.getMessage(), List.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(MethodArgumentNotValidException ex,
                                                          HttpServletRequest req) {
        var details = ex.getBindingResult().getFieldErrors().stream()
            .map(err -> new ErrorDetail(err.getField(), err.getDefaultMessage()))
            .toList();

        boolean putOrPostPix = isChavePix(req);
        var resp = body(putOrPostPix ? "VALIDATION_ERROR" : "BAD_REQUEST", "Payload inválido", details);
        return ResponseEntity.status(putOrPostPix ? HttpStatus.UNPROCESSABLE_ENTITY : HttpStatus.BAD_REQUEST).body(resp);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleNotReadable(HttpMessageNotReadableException ex,
                                                           HttpServletRequest req) {
        List<ErrorDetail> details = new ArrayList<>();
        if (ex.getCause() instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException ife) {
            for (var ref : ife.getPath()) {
                String field = ref.getFieldName() == null ? "(desconhecido)" : ref.getFieldName();
                details.add(new ErrorDetail(field, "valor inválido: " + String.valueOf(ife.getValue())));
            }
        }
        boolean putOrPostPix = isChavePix(req);
        var resp = body(putOrPostPix ? "VALIDATION_ERROR" : "BAD_REQUEST", "Payload inválido", details);
        return ResponseEntity.status(putOrPostPix ? HttpStatus.UNPROCESSABLE_ENTITY : HttpStatus.BAD_REQUEST).body(resp);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex,
                                                                   HttpServletRequest req) {
        var details = ex.getConstraintViolations().stream()
            .map(v -> new ErrorDetail(String.valueOf(v.getPropertyPath()), v.getMessage()))
            .toList();

        boolean putOrPostPix = isChavePix(req);
        var resp = body(putOrPostPix ? "VALIDATION_ERROR" : "BAD_REQUEST", "Payload inválido", details);
        return ResponseEntity.status(putOrPostPix ? HttpStatus.UNPROCESSABLE_ENTITY : HttpStatus.BAD_REQUEST).body(resp);
    }
}
