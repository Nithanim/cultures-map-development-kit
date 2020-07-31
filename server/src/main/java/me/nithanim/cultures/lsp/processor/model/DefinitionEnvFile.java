package me.nithanim.cultures.lsp.processor.model;

import java.util.ArrayList;
import java.util.List;
import me.nithanim.cultures.lsp.processor.lines.CulturesIniLine;

public class DefinitionEnvFile {
  List<? extends CulturesIniLine> lines = new ArrayList<>();

  public void update(List<? extends CulturesIniLine> lines) {
    this.lines = lines;
  }
}
