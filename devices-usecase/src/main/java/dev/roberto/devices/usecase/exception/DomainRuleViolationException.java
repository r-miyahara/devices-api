package dev.roberto.devices.usecase.exception;

public class DomainRuleViolationException extends RuntimeException {
  public DomainRuleViolationException(String message) { super(message); }
}
