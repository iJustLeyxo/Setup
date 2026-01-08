package com.cavetale.setup.data;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

/** Parent for all data classes. */
public interface Data {
  /** Turns caps snake case to camel case. */
  static @NotNull String capsToCamel(@NotNull String string) {
    return Arrays.stream(string.split("_")).map(s -> s.isEmpty() ? "" : s.charAt(0) + s.substring(1).toLowerCase()).collect(Collectors.joining());
  }
}
