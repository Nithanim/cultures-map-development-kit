package me.nithanim.cultures.lsp.processor.lines;

import javax.annotation.Nullable;
import lombok.Value;
import me.nithanim.cultures.lsp.processor.util.Origin;

@Value
public class CulturesIniCategory implements CulturesIniLine {
  @Nullable CulturesIniCategoryType type;
  Origin originAll;

  @Override
  public String printLine() {
    if (type == null) {
      return "";
    } else {
      return "[" + type.getRealname() + "]";
    }
  }
}
