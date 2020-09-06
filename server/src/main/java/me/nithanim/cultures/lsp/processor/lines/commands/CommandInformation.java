package me.nithanim.cultures.lsp.processor.lines.commands;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCategoryType;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class CommandInformation {
  String name;
  String displayName;
  CulturesIniCategoryType category;
  String documentation;
  /** TODO disables some general checking, like parameter count */
  boolean special;
  /** minimum supported parameters */
  int parametersMinimum;
  /** maximum supported parameters */
  int parametersMaximum;

  List<ParameterInformation> parameters;

  public ParameterInformation getParameter(int i) {
    return parameters.get(i);
  }

  @Value
  @AllArgsConstructor
  @NoArgsConstructor(force = true)
  public static class ParameterInformation {
    String name;
    Type type;
    String documentation;
    Range range;

    @Value
    @AllArgsConstructor
    @NoArgsConstructor(force = true)
    public static class Range {
      int min;
      int max;
    }

    public enum Type {
      NUMBER,
      TYPE
    }
  }
}
