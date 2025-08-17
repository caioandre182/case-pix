package com.casepix.pixkeys.infra.error;

import com.casepix.pixkeys.domain.exception.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.List;


@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ErrorDetail(String field, String message) {}
    public record ErrorResponse(Instant timestamp, String code, String message, List<ErrorDetail> details) {}

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(MethodArgumentNotValidException ex) {
        var details = ex.getBindingResult().getFieldErrors().stream()
            .map(err -> new ErrorDetail(err.getField(), err.getDefaultMessage()))
            .toList();
        return new ErrorResponse(Instant.now(), "BAD_REQUEST", "Payload inválido", details);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleGeneric(Exception ex) {
        return new ErrorResponse(Instant.now(), "BAD_REQUEST", ex.getMessage(), List.of());
    }
}
