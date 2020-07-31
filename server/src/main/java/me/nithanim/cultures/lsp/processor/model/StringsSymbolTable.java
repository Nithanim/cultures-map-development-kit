package me.nithanim.cultures.lsp.processor.model;

import java.util.List;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommand;

public interface StringsSymbolTable {
  void getStrings(int id);

  void getString(int id, String lang);

  void replaceLanguage(List<CulturesIniCommand> commands);
}
