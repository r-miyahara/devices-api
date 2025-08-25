package dev.roberto.devices.web;

import dev.roberto.devices.domain.model.Device;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

final class EtagUtil {
  private EtagUtil() {}

  static String etagFor(Device d) {
    try {
      var raw = (d.id() + "|" + d.name() + "|" + d.brand() + "|" + d.state() + "|" + d.creationTime())
        .getBytes(StandardCharsets.UTF_8);
      var digest = MessageDigest.getInstance("SHA-256").digest(raw);
      return "\"" + Base64.getUrlEncoder().withoutPadding().encodeToString(digest) + "\"";
    } catch (Exception e) {
      // fallback (não ideal, mas evita quebrar a resposta)
      return "\"" + d.id() + "\"";
    }
  }
}
