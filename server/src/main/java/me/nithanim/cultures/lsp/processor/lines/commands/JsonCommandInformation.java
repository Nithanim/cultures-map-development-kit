package me.nithanim.cultures.lsp.processor.lines.commands;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class JsonCommandInformation {
  String name;
  String displayName;
  String category;
  String documentation;
  boolean variableLengthParameters;
  List<JsonParameterInformation> parameters;

  @Value
  @AllArgsConstructor
  @NoArgsConstructor(force = true)
  public static class JsonParameterInformation {
    String name;
    String documentation;
    JsonType type;
    JsonRange numberRange;
    List<Integer> numberHints;

    @Value
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class JsonRange {
      int min;
      int max;
    }

    public enum JsonType {
      NUMBER,
      TYPE
    }
  }
}
