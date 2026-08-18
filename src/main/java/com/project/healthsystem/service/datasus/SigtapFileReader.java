package com.project.healthsystem.service.datasus;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;

public class SigtapFileReader {
  public static <T> List<T> read(Path path, Function<String, T> parser) throws IOException {
    try (BufferedReader reader = Files.newBufferedReader(
      path,
      StandardCharsets.ISO_8859_1
    )) {
      return reader.lines()
        .map(parser)
        .toList();
    }
  }
  
}
