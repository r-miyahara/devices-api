package dev.roberto.devices.web;

import dev.roberto.devices.usecase.exception.DomainRuleViolationException;
import dev.roberto.devices.usecase.exception.NotFoundException;
import dev.roberto.devices.web.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class RestExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> notFound(NotFoundException ex, HttpServletRequest req) {
    return build(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage(), req);
  }

  @ExceptionHandler(DomainRuleViolationException.class)
  public ResponseEntity<ErrorResponse> domainRule(DomainRuleViolationException ex, HttpServletRequest req) {

    return build(HttpStatus.UNPROCESSABLE_ENTITY, "Domain Rule Violation", ex.getMessage(), req);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> validation(MethodArgumentNotValidException ex, HttpServletRequest req) {
    String msg = ex.getBindingResult().getFieldErrors().stream()
      .map(RestExceptionHandler::formatFieldError)
      .collect(Collectors.joining("; "));
    return build(HttpStatus.BAD_REQUEST, "Validation Error", msg, req);
  }

  @ExceptionHandler({ IllegalArgumentException.class, HttpMessageNotReadableException.class })
  public ResponseEntity<ErrorResponse> badRequest(RuntimeException ex, HttpServletRequest req) {
    return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), req);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> generic(Exception ex, HttpServletRequest req) {
    return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "Unexpected error", req);
  }

  private static ResponseEntity<ErrorResponse> build(HttpStatus status, String error, String message, HttpServletRequest req) {
    var body = new ErrorResponse(Instant.now(), status.value(), error, message, req.getRequestURI());
    return ResponseEntity.status(status).body(body);
  }

  private static String formatFieldError(FieldError fe) {
    return "%s: %s".formatted(fe.getField(), fe.getDefaultMessage());
  }

  @ExceptionHandler(PreconditionFailed.class)
  public ResponseEntity<ErrorResponse> precondition(PreconditionFailed ex, HttpServletRequest req) {
    return build(HttpStatus.PRECONDITION_FAILED, "Precondition Failed", ex.getMessage(), req);
  }

  class PreconditionFailed extends RuntimeException { PreconditionFailed(String m){ super(m);} }

}
