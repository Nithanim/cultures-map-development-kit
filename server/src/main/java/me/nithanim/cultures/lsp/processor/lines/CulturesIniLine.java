package me.nithanim.cultures.lsp.processor.lines;

import me.nithanim.cultures.lsp.processor.util.Origin;

public interface CulturesIniLine {
  Type getLineType();

  Origin getOriginAll();

  String printLine();

  public enum Type {
    CATEGORY,
    COMMAND,
    CONSTANT,
    INCLUDE;
  }
}
