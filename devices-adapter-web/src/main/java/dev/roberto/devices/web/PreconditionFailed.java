package dev.roberto.devices.web;

public class PreconditionFailed extends RuntimeException {
  public PreconditionFailed(String message) { super(message); }
}
