package me.nithanim.cultures.lsp.processor.util;

import lombok.NonNull;
import lombok.Value;
import org.eclipse.lsp4j.Position;

@Value
public class MyPosition {
  @NonNull SourceFile sourceFile;
  Position position;
}
