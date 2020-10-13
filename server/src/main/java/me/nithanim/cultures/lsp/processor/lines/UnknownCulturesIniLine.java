package me.nithanim.cultures.lsp.processor.lines;

import java.util.ArrayList;
import java.util.List;
import lombok.Value;
import me.nithanim.cultures.lsp.processor.util.Origin;

@Value
public class UnknownCulturesIniLine implements CulturesIniLine {
  String name;
  Origin originAll;
  Origin originBaseCommand;
  List<CulturesIniCommand.Parameter> parameters;

  @Override
  public Type getLineType() {
    return Type.UNKNOWN;
  }

  @Override
  public String printLine() {
    List<String> parts = new ArrayList<>();
    parts.add(name);
    for (CulturesIniCommand.Parameter p : parameters) {
      if (p.getType() == CulturesIniCommand.Parameter.Type.STRING
          || p.getType() == CulturesIniCommand.Parameter.Type.TYPE) {
        parts.add("\"" + p.getValue() + "\"");
      } else {
        parts.add(p.getValue());
      }
    }
    return String.join(" ", parts);
  }
}
