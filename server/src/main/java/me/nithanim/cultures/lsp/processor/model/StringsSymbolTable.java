package me.nithanim.cultures.lsp.processor.model;


import me.nithanim.cultures.lsp.processor.lines.CulturesIniCommand;

import java.util.List;

public interface StringsSymbolTable {
    void getStrings(int id);

    void getString(int id, String lang);

    void replaceLanguage(List<CulturesIniCommand> commands);
}
