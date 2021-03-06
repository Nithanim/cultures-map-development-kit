package me.nithanim.cultures.lsp.processor.lines;

import lombok.Value;
import me.nithanim.cultures.lsp.processor.util.Origin;

@Value
public class CulturesIniInclude implements CulturesIniLine {
  Type lineType = Type.INCLUDE;
  Origin originAll;
  Origin originPath;
  String path;

  @Override
  public String printLine() {
    return "#include \"" + path + "\"";
  }
}
