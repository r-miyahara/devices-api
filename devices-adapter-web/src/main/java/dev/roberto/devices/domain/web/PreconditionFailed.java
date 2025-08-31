package dev.roberto.devices.domain.web;

public class PreconditionFailed extends RuntimeException {
  public PreconditionFailed(String message) { super(message); }
}
