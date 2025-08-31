package dev.roberto.devices.domain.usecase;

import java.util.List;

public record PageResult<T>(List<T> items, long total, int page, int size) {}
