package me.nithanim.cultures.lsp.processor.lines;

import javax.annotation.Nullable;
import lombok.Value;
import me.nithanim.cultures.lsp.processor.util.Origin;

@Value
public class CulturesIniCategory implements CulturesIniLine {
  Type lineType = Type.CATEGORY;
  @Nullable CulturesIniCategoryType categoryType;
  Origin originAll;

  @Override
  public String printLine() {
    if (categoryType == null) {
      return "";
    } else {
      return "[" + categoryType.getRealname() + "]";
    }
  }
}
