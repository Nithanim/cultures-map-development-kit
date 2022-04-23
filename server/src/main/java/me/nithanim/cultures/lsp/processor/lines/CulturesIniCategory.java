package me.nithanim.cultures.lsp.processor.lines;

import javax.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import me.nithanim.cultures.lsp.processor.util.Origin;

@Value
@RequiredArgsConstructor
@NoArgsConstructor(force = true, access = AccessLevel.PACKAGE)
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
