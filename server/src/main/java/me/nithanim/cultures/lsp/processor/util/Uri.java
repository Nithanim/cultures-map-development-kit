package me.nithanim.cultures.lsp.processor.util;

import edu.umd.cs.findbugs.annotations.ReturnValuesAreNonnullByDefault;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.ParametersAreNonnullByDefault;
import lombok.SneakyThrows;
import lombok.Value;

@Value
@ReturnValuesAreNonnullByDefault
@ParametersAreNonnullByDefault
public class Uri {
  Path path;

  public static Uri of(Path p) {
    return new Uri(p);
  }

  public static Uri of(URI uri) {
    return new Uri(Paths.get(uri));
  }

  @SneakyThrows
  public static Uri of(String uri) {
    return new Uri(Paths.get(new URI(uri)));
  }

  public URI toOldUri() {
    return path.toUri();
  }

  @Override
  public String toString() {
    return toOldUri().toString();
  }
}
