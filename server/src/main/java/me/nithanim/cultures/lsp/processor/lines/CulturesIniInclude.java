package me.nithanim.cultures.lsp.processor.lines;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import me.nithanim.cultures.lsp.processor.util.Origin;

@Value
@RequiredArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
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
