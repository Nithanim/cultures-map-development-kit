package me.nithanim.cultures.lsp.processor.model;

import java.util.Map;
import lombok.Value;
import me.nithanim.cultures.lsp.processor.util.Origin;

public class StringsFile {
  private String language;
  private Map<Integer, StringDefinition> idToStringMap;

  @Value
  public static class StringDefinition {
    int id;
    String language;
    Origin origin;
  }
}
